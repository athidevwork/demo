package dti.ci.agentmgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.ci.agentmgr.AgentFields;

import java.util.logging.Logger;

/**
 * This class implements validation of duplicates.
 * <p/>
 * <p/>
 * Rule - The given combination of field values cannot have overlapping time periods.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 4, 2008
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Apr 17, 2008     James       Issue#75265 Modify code according to code review
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * 10/24/2017       htwang      Issue 188776 - add message field name that will be used in
 *                              MessageManager.getInstance().addErrorMessage() later
 * ---------------------------------------------------
 */
public class ContractCommissionRecordSetValidator extends ContinuityRecordSetValidator {

    /**
     * Override this method to add rowid for parent grid (contract grid)
     *
     * @param currentRecord
     * @param parms
     */
    protected void addErrorMessage(Record currentRecord, Object[] parms) {
        Logger l = LogUtils.enterLog(getClass(), "addErrorMessage", new Object[]{currentRecord, parms});
        String rowId = currentRecord.getStringValue(getIdFieldName());

        String agentLicenseId = AgentFields.getAgentLicenseId(currentRecord);

        //get contract rowid
        for (int i = 0; i < getParentResultSet().getSize(); i++) {
            Record contractRecord = getParentResultSet().getRecord(i);
            String contractRowId = AgentFields.getAgentLicenseId(contractRecord);
            if (contractRowId.equals(agentLicenseId)) {
                rowId = contractRowId + "," + rowId;
                break;
            }
        }

        MessageManager.getInstance().addErrorMessage(getMessageKey(), parms,
            getMessageFieldName() == null ? "" : getMessageFieldName(), rowId);

        l.exiting(getClass().getName(), "addErrorMessage");
    }

    //-------------------------------------------------
    // constructor
    //-------------------------------------------------
    public ContractCommissionRecordSetValidator() {
        super();
    }

    public ContractCommissionRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, messageFieldName);
    }

    public ContractCommissionRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String rowNumberFieldName, String messageKey, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, rowNumberFieldName, messageKey);
    }

    public ContractCommissionRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, boolean validateGap, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, validateGap, messageFieldName);
    }

    public ContractCommissionRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, keyFieldNames, parmFieldNames, messageFieldName);
    }

    public ContractCommissionRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames, boolean validateGap, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, keyFieldNames, parmFieldNames, validateGap, messageFieldName);
    }

    public ContractCommissionRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames, String rowNumberFieldName, boolean validateGap, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, keyFieldNames, parmFieldNames, rowNumberFieldName, validateGap, messageFieldName);
    }


    /**
     * get parent resultset
     * @return
     */
    public RecordSet getParentResultSet() {
        return m_parentResultSet;
    }

    /**
     * set parent resultset
     * @param parentResultSet
     */
    public void setParentResultSet(RecordSet parentResultSet) {
        m_parentResultSet = parentResultSet;
    }

    /**
     * parent result set
     */
    private RecordSet m_parentResultSet;

}
