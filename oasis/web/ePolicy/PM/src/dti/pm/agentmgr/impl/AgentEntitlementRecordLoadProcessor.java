package dti.pm.agentmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.agentmgr.AgentFields;
import dti.pm.agentmgr.dao.AgentDAO;
import dti.pm.agentmgr.dao.AgentJdbcDAO;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.busobjs.TransactionCode;
import dti.pm.transactionmgr.TransactionFields;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class extends the default record load processor .
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 1, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Apr 17, 2008     James       Issue#75265 Modify code according to code review
 * 10/21/2011       clm         issue 122654 Modify indicator IS_OUTPUT_AVAILABLE
 * 11/19/2015       eyin        167171 - Modified getInitialEntitlementValuesForAgent(), Add synchronized lock on this function.
 * ---------------------------------------------------
 */
public class AgentEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

   /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
       Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

       // Per disucssion with Kiran, she wanted to remvoe the account date logic from fmn_sel_policy_agent_info in db
       // so I am adding it here.
       //  v_sql := v_sql|| '          ( PA.ACCOUNTING_FROM_DATE <= TRUNC(SYSDATE) ) and ';
       //  v_sql := v_sql|| '          ( PA.ACCOUNTING_TO_DATE > SYSDATE ) and ';
       Date today = new Date();
       if (record.getDateValue("accountingFromDate").after(today) ||
           record.getDateValue("accountingToDate").before(today)){
       return false;
       }
       // For records loaded from the database,
       // all fields by default will not be editable. Set the edit_ind = N

       // set at the row level to N
       record.setEditIndicator(YesNoFlag.N);
       record.setFieldValue(AgentFields.IS_PRODUCER_AGENT_LIC_ID_AVAILABLE, YesNoFlag.N);

       // for delete action item: set initially not to display
       record.setFieldValue(AgentFields.IS_ROW_ELIGIBLE_FOR_DELETE, "N");

       record.setFieldValue(AgentFields.IS_OUTPUT_AVAILABLE, YesNoFlag.Y);

       l.exiting(getClass().getName(), "postProcessRecord");
       return true;
   }

    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        PolicyHeader policyHeader = getPolicyHeader();

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add(AgentFields.IS_PRODUCER_AGENT_LIC_ID_AVAILABLE);
            pageEntitlementFields.add(AgentFields.IS_ROW_ELIGIBLE_FOR_DELETE);
            pageEntitlementFields.add(AgentFields.IS_CHANGE_AVAILABLE);
            pageEntitlementFields.add(AgentFields.IS_OUTPUT_AVAILABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        // set IS_AGENT_UPDATEABLE for 2 fields (agentNote and specialConditionCode)per current UC requirements
        Record summaryRecord = recordSet.getSummaryRecord();
        summaryRecord.setFieldValue(IS_AGENT_UPDATEABLE,YesNoFlag.Y);

        boolean isCommPayCodeAvailable = getAgentDAO().isCommPayCodeAvailable(policyHeader.toRecord());
        summaryRecord.setFieldValue(AgentFields.IS_COMM_PAY_CODE_AVAILABLE, Boolean.valueOf(isCommPayCodeAvailable));

        // for Add button, we can not use the setReadOnly, because the UC has it own unique requirements for ADD
        summaryRecord.setFieldValue(AgentFields.IS_ADD_AVAILABLE,YesNoFlag.getInstance(isAddAvailable(policyHeader)));
        summaryRecord.setFieldValue(AgentFields.IS_AUTHORIZATION_CODE_AVAILABLE, YesNoFlag.N);

        // Disable Change Agent button if its a term creation transaction inprogress
        TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();
        if ((policyHeader.getLastTransactionInfo().getTransactionStatusCode().isInProgress()) &&
            (transactionCode.isNewBus() ||
                transactionCode.isConvRenew() ||
                transactionCode.isConvReissue() ||
                transactionCode.isReissue() ||
                transactionCode.isManualRenewal() ||
                transactionCode.isAutoRenewal() ||
                transactionCode.isQuote())) {
            summaryRecord.setFieldValue(AgentFields.IS_CHANGE_AVAILABLE, YesNoFlag.N);
        }
        else {
            summaryRecord.setFieldValue(AgentFields.IS_CHANGE_AVAILABLE, YesNoFlag.Y);
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Return a Record of initial entitlement values for a new Agent record.
     */
    public synchronized static Record getInitialEntitlementValuesForAgent(PolicyHeader policyHeader) {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();

            EntitlementFields.setIsRowEligibleForDelete(c_initialEntitlementValues, YesNoFlag.Y);

            // add pageEntitlement related information:
            c_initialEntitlementValues.setEditIndicator(YesNoFlag.N);

            // allow only field producerAgentLicId..editable
            c_initialEntitlementValues.setFieldValue(AgentFields.IS_PRODUCER_AGENT_LIC_ID_AVAILABLE,YesNoFlag.Y);


            c_initialEntitlementValues.setFieldValue(AgentFields.IS_OUTPUT_AVAILABLE, YesNoFlag.N);
        }

        return c_initialEntitlementValues;
    }

    private boolean isAddAvailable(PolicyHeader policyHeader){
        Logger l = LogUtils.enterLog(getClass(),"isAddAvailable", new Object[]{policyHeader});

        Record record = policyHeader.toRecord();
        TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();
        TransactionFields.setTransactionCode(record, transactionCode);
        boolean isHideConfigured = true;
        if (policyHeader.getLastTransactionInfo().getTransactionStatusCode().isComplete()) {
           isHideConfigured = getAgentDAO().hideAddOptionForCompleteTransaction(record);
        } else {
           isHideConfigured = getAgentDAO().hideAddOptionForNonCompleteTransaction(record);
        }

        l.exiting(getClass().toString(),"isAddAvailable" , Boolean.valueOf(!isHideConfigured));
        return !isHideConfigured;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public AgentEntitlementRecordLoadProcessor(){}
    public AgentEntitlementRecordLoadProcessor(PolicyHeader policyHeader){
        setPolicyHeader(policyHeader);
    }

    public void verifyConfig() {
    }

    private AgentDAO getAgentDAO() {
        return m_agentDAO;
    }

    private void setPolicyHeader(PolicyHeader policyHeader) {
      m_policyHeader = policyHeader;
    }

    private PolicyHeader getPolicyHeader(){
        return m_policyHeader;
    }

    private static final String IS_AGENT_UPDATEABLE = "isAgentUpdatable";
   // private static final String IS_ADD_AVAILABLE = "isAddAvailable";

    private static Record c_initialEntitlementValues;

    // seemed to remember that Bill asked not to make it in spring configuration for RecordLoadProcessor
    private AgentDAO m_agentDAO = new AgentJdbcDAO();
    private PolicyHeader m_policyHeader;
}
