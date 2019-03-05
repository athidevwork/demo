package dti.pm.policymgr.mailingmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.mailingmgr.MailingAttributeFields;
import dti.pm.policymgr.mailingmgr.MailingEventFields;
import dti.pm.policymgr.mailingmgr.MailingRecipientFields;
import dti.pm.policymgr.mailingmgr.PolicyMailingManager;
import dti.pm.policymgr.mailingmgr.dao.PolicyMailingDAO;

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of PolicyMailingManager Interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 17, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/02/2008       sxm         Issue 86930 - add loadAllMailingGenerationError,
 *                              and modified mailing generation error handling
 * 10/06/2008       sxm         Issue 86930 - merge generateMailingEvent and loadAllMailingGenerationError
 *                              since the generation errors are stored in global temp table
 * 01/24/2013       adeng       Issue 140829 - Add a method to extract the part of reuse code.
 * ---------------------------------------------------
 */


public class PolicyMailingManagerImpl implements PolicyMailingManager {

    /**
     * load all mailing event info by policy info
     *
     * @param inputRecord
     * @return RecordSet a recordSet of mailing event
     */

    public RecordSet loadAllMailingEvent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMailingEvent", new Object[]{inputRecord});
        }
        RecordSet rs;

        rs = getPolicyMailingDAO().loadAllMailingEvent(inputRecord, new MailingEventEntitlementRecordLoadProcessor());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMailingEvent", rs);
        }
        return rs;
    }

    /**
     * load all policy mailing attribute
     *
     * @param inputRecord
     * @return RecordSet a recordSet of mailing attribute
     */
    public RecordSet loadAllMailingAttribute(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMailingAttribute", new Object[]{inputRecord});
        }
        Record record = new Record();
        if (inputRecord.hasStringValue("startSearchDate")) {
            record.setFieldValue("startSearchDate", inputRecord.getFieldValue("startSearchDate"));
        }
        else {
            record.setFieldValue("startSearchDate", "01/01/1900");
        }
        if (inputRecord.hasStringValue("endSearchDate")) {
            record.setFieldValue("endSearchDate", inputRecord.getFieldValue("endSearchDate"));
        }
        else {
            record.setFieldValue("endSearchDate", "01/01/3000");
        }
        if (inputRecord.hasStringValue("queryId")) {
            record.setFieldValue("productMailingId", inputRecord.getFieldValue("queryId"));
        }
        else {
            record.setFieldValue("productMailingId", "");
        }
        if (inputRecord.hasStringValue("policyNum")) {
            record.setFieldValue("policyNum", inputRecord.getFieldValue("policyNum"));
        }
        else {
            record.setFieldValue("policyNum", "");
        }
        RecordSet rs;
        rs = getPolicyMailingDAO().loadAllMailingAttribute(record, new MailingAttributeEntitlementRecordLoadProcessor());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMailingAttribute", rs);
        }
        return rs;
    }

    /**
     * load all mailing recipient info by mailing event
     *
     * @param inputRecord (policyMailingId)
     * @return RecordSet   a record set loaded with list of mailing recipient
     */
    public RecordSet loadAllMailingRecipient(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMailingRecipient", new Object[]{inputRecord});
        }
        RecordSet rs;
        rs = getPolicyMailingDAO().loadAllMailingRecipient(inputRecord, new MailingRecipientEntitlementRecordLoadProcessor());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMailingRecipient", rs);
        }
        return rs;
    }

    /**
     * load all past mailing
     *
     * @param inputRecord (policyMailingId)
     * @return RecordSet   a record set loaded with list of past mailing
     */
    public RecordSet loadAllPastMailing(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPastMailing", new Object[]{inputRecord});
        }
        RecordSet rs;
        RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
        rs = getPolicyMailingDAO().loadAllPastMailing(inputRecord, selectIndProcessor);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPastMailing", rs);
        }
        return rs;
    }

    /**
     * save all policy mailing info(event, attribute, recipient)
     *
     * @param mailingEventRecords     mailing event records
     * @param mailingAttributeRecords mailing attribute records
     * @param mailingRecipientRecords mailing recipient records
     * @return the number of rows updated.
     */
    public int saveAllPolicyMailing(RecordSet mailingEventRecords, RecordSet mailingAttributeRecords, RecordSet mailingRecipientRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllPolicyMailing", new Object[]{mailingEventRecords, mailingEventRecords});
        //do update and save and delelte
        int updateCount = 0;

        //delete mailing attribute
        RecordSet deletedMailingAttributeRecords = mailingAttributeRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED}));
        updateCount += getPolicyMailingDAO().deleteAllMailingAttribute(deletedMailingAttributeRecords);

        //delete mailing recipient
        RecordSet deletedMailingRecipientRecords = mailingRecipientRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED}));
        updateCount += getPolicyMailingDAO().deleteAllMailingRecipient(deletedMailingRecipientRecords);

        //delete mailing event
        RecordSet deletedMailingEventRecords = mailingEventRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED}));
        updateCount += getPolicyMailingDAO().deleteAllMailingEvent(deletedMailingEventRecords);

        // saving new/modified mailing event
        RecordSet updatedMailingEventRecords = mailingEventRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        RecordSet modifedMailingEventRecords = updatedMailingEventRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        modifedMailingEventRecords.setFieldValueOnAll("rowStatus", "MODIFIED");
        RecordSet newMailingEventRecords = updatedMailingEventRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        newMailingEventRecords.setFieldValueOnAll("rowStatus", "NEW");
        modifedMailingEventRecords.addRecords(newMailingEventRecords);
        updateCount += getPolicyMailingDAO().saveAllMailingEvent(modifedMailingEventRecords);

        // saving new/modified mailing attribute
        RecordSet updatedMailingAttributeRecords = mailingAttributeRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        RecordSet modifedMailingAttributeRecords = updatedMailingAttributeRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        modifedMailingAttributeRecords.setFieldValueOnAll("rowStatus", "MODIFIED");
        RecordSet newMailingAttributeRecords = updatedMailingAttributeRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        newMailingAttributeRecords.setFieldValueOnAll("rowStatus", "NEW");
        modifedMailingAttributeRecords.addRecords(newMailingAttributeRecords);
        updateCount += getPolicyMailingDAO().saveAllMailingAttribute(modifedMailingAttributeRecords);

        //saving new/modified mailing recipient
        RecordSet updatedMailingRecipientRecords = mailingRecipientRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        RecordSet modifedMailingRecipientRecords = updatedMailingRecipientRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        modifedMailingRecipientRecords.setFieldValueOnAll("rowStatus", "MODIFIED");
        RecordSet newMailingRecipientRecords = updatedMailingRecipientRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        newMailingRecipientRecords.setFieldValueOnAll("rowStatus", "NEW");
        modifedMailingRecipientRecords.addRecords(newMailingRecipientRecords);
        updateCount += getPolicyMailingDAO().saveAllMailingRecipient(modifedMailingRecipientRecords);
        l.exiting(getClass().getName(), "saveAllPolicyMailing", new Long(updateCount));
        return updateCount;
    }

    /**
     * create policy mailing info from selected policy
     *
     * @param inputRecord (selectedPolicyIds, productMailingId)
     * @return the number of rows updated.
     */
    public String createPolicyMailingFromPolicy(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "createPolicyMailingFromPolicy", new Object[]{inputRecord});

        //save mailing event
        Record mailingEventRecord = getMailingEventRecord(inputRecord);
        String policyMailingId = MailingEventFields.getPolicyMailingId(mailingEventRecord);
        mailingEventRecord.setFieldValue("rowStatus", "NEW");
        RecordSet mailingEventRecords = new RecordSet();
        mailingEventRecords.addRecord(mailingEventRecord);
        getPolicyMailingDAO().saveAllMailingEvent(mailingEventRecords);
        //save mailing recipient
        RecordSet mailingRecipientRecords = getMailingRecipientRecords(inputRecord, mailingEventRecord.getStringValue("policyMailingId"));
        mailingRecipientRecords.setFieldValueOnAll("rowStatus", "NEW");
        getPolicyMailingDAO().saveAllMailingRecipient(mailingRecipientRecords);
        return policyMailingId;
    }

    /**
     * Get inital values for mailing event
     *
     * @return Record
     */
    public Record getInitialValuesForMailingEvent() {

        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForMailingEvent");

        //get default record from workbench
        Record output = new Record();
        MailingEventFields.setPolicyMailingId(output, getDbUtilityManager().getNextSequenceNo().toString());
        MailingEventFields.setGenerateDate(output, "");
        l.exiting(getClass().getName(), "getInitialValuesForMailingEvent");
        return output;
    }

    /**
     * Get inital values for mailing attribute
     *
     * @return Record
     */
    public Record getInitialValuesForMailingAttribute(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForMailingAttribute");
        Record output = new Record();
        MailingAttributeFields.setPolicyMailingId(output, inputRecord.getStringValue("policyMailingId"));
        MailingAttributeFields.setPolicyMailingResendId(output, getDbUtilityManager().getNextSequenceNo().toString());
        MailingAttributeFields.setProductMailingResendId(output,"");
        MailingAttributeFields.setResendType(output, "");
        MailingAttributeFields.setResendDays(output, "0");
        MailingAttributeFields.setResendDate(output,"");
        new MailingAttributeEntitlementRecordLoadProcessor().setInitialEntitlementValuesForMailingAttribute(output);
        l.exiting(getClass().getName(), "getInitialValuesForMailingAttribute");
        return output;
    }

    /**
     * Get inital values for mailing recipient
     *
     * @return Record
     */
    public Record getInitialValuesForMailingRecipient(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForMailingRecipient");
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForMailingRecipient");
        }
        Record output = new Record();
        MailingRecipientFields.setPolicyMailingId(output, inputRecord.getStringValue("policyMailingId"));
        MailingRecipientFields.setPolicyMailingDtlId(output, getDbUtilityManager().getNextSequenceNo().toString());
        MailingRecipientFields.setRiskBaseRecordId(output, "");
        MailingRecipientFields.setGenerateDate(output, "");
        MailingRecipientFields.setReceivedDate(output, "");
        MailingRecipientFields.setReceivedB(output, "N");
        new MailingRecipientEntitlementRecordLoadProcessor().setInitialEntitlementValuesForMailingRecipient(output);
        l.exiting(getClass().getName(), "getInitialValuesForMailingRecipient");
        return output;
    }

    /**
     * validate mailing recipient
     *
     * @param inputRecord
     * @return recordSet
     */
    private RecordSet validateMailingRecipientRecordSet(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateMailingRecipientRecordSet", new Object[]{inputRecord});
        RecordSet recordSet = getPolicyMailingDAO().validateMailingRecipient(inputRecord);
        l.exiting(getClass().getName(), "validateMailingRecipientRecordSet");
        return recordSet;
    }

    /**
     * validate mailing recipient
     *
     * @param inputRecord
     * @return record(errorFlag,policyId,name,policyNo)
     */
    public Record validateMailingRecipient(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateMailingRecipient", new Object[]{inputRecord});
        Record output = new Record();
        String policyNo = inputRecord.getStringValue("policyNo");
        RecordSet recordSet = validateMailingRecipientRecordSet(inputRecord);
        if (recordSet.getSize() == 0) {
            output.setFieldValue("errorFlag", "Y");
            output.setFieldValue("name", "");
            output.setFieldValue("policyId", "");
            output.setFieldValue("policyNo", policyNo);
        }
        else {
            output.setFieldValue("errorFlag", "N");
            output.setFieldValue("name", recordSet.getRecord(0).getStringValue("policyholderName"));
            output.setFieldValue("policyId", recordSet.getRecord(0).getStringValue("policyId"));
            output.setFieldValue("policyNo", policyNo);
        }
        l.exiting(getClass().getName(), "validateMailingRecipient");
        return output;
    }

    /**
     * get resend days by selected resend
     *
     * @param inputRecord (productMailingResendId)
     * @return record(resendDays)
     */
    public Record getResendDaysBySelectedResend(Record inputRecord) {
        Record output = new Record();
        String resendDays = getPolicyMailingDAO().getResendDaysBySelectedResend(inputRecord);
        output.setFieldValue("resendDays", resendDays);
        return output;
    }

    /**
     * check past mailing exist or not
     *
     * @param inputRecord
     * @return count(ifcountvalue>0,exist)
     */
    public int checkPastMailing(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "checkPastMailing", new Object[]{inputRecord});
        int count = getPolicyMailingDAO().checkPastMailing(inputRecord);
        l.exiting(getClass().getName(), "checkPastMailing");
        return count;
    }

    /**
     * generate mailing event by policy mailng id
     *
     * @param inputRecord (policyMailingId)
     * @return RecordSet   a record set loaded with list of mailing generation errors
     */
    public RecordSet generateMailingEvent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateMailingEvent", new Object[]{inputRecord});
        }

        RecordSet rs = getPolicyMailingDAO().generateMailingEvent(inputRecord);

        // get return code and message from the dummry record
        Record r = rs.getSubSet(new RecordFilter("dummyRec", YesNoFlag.Y)).getRecord(0);
        int returnCode = Integer.parseInt(r.getStringValue("policyNo"));
        String retrunMsg = r.getStringValue("errorMessage");
        if (returnCode == 0)
            MessageManager.getInstance().addInfoMessage("pm.maintainPolicyMailing.generateSuccess.info",
                new String[]{"", retrunMsg});
        else
            MessageManager.getInstance().addErrorMessage("pm.maintainPolicyMailing.generateFail.error",
                new String[]{"", retrunMsg});

        // remove dummy record from the result set before return it
        RecordSet rs2 = rs.getSubSet( new RecordFilter("dummyRec", YesNoFlag.N));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateMailingEvent", rs2);
        }
        return rs2;
    }

    /**
     * reprint mailing event by policy mailng id
     *
     * @param inputRecord
     * @return count(ifcount=0success,elsefail)
     */
    public int reprintMailingEvent(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "reprintMailingEvent", new Object[]{inputRecord});
        Record record = getPolicyMailingDAO().reprintMailingEvent(inputRecord);
        l.exiting(getClass().getName(), "reprintMailingEvent");
        return Integer.parseInt(record.getStringValue("rc"));
    }

    /**
     * delete exluded policy mailing detail
     *
     * @param mailingDtls
     * @return
     */
    public int deleteExludedPolicies(String mailingDtls) {

        Logger l = LogUtils.enterLog(getClass(), "deleteExludedPolicies", new Object[]{mailingDtls});
        int result=0;
        RecordSet inputRecordSet=getInputRecordSetForExludedPolicies(mailingDtls);
        result= getPolicyMailingDAO().deleteAllMailingRecipient(inputRecordSet);
        l.exiting(getClass().getName(), "deleteExludedPolicies");
        return result;
    }

//-------------------------------------------------
// Configuration constructor and accessor methods
//-------------------------------------------------

    public void verifyConfig() {
        if (getPolicyMailingDAO() == null)
            throw new ConfigurationException("The required property 'policyMailingDAO' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public PolicyMailingManagerImpl() {
    }

    public PolicyMailingDAO getPolicyMailingDAO() {
        return this.policyMailingDAO;
    }

    public void setPolicyMailingDAO(PolicyMailingDAO policyMailingDAO) {
        this.policyMailingDAO = policyMailingDAO;
    }


    private PolicyMailingDAO policyMailingDAO;

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return this.workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        this.workbenchConfiguration = workbenchConfiguration;
    }

    public DBUtilityManager getDbUtilityManager() {
        return this.dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        this.dbUtilityManager = dbUtilityManager;
    }


    //construct mailing event record from select policy
    private Record getMailingEventRecord(Record inputRecord) {
        String productMailingId = inputRecord.getStringValue("productMailingId");
        Record mailingEventRecord = new Record();
        MailingEventFields.setPolicyMailingId(mailingEventRecord, getDbUtilityManager().getNextSequenceNo().toString());
        MailingEventFields.setProductMailingId(mailingEventRecord, productMailingId);
        return mailingEventRecord;
    }

    //construct mailing recipient record from selected policies and set initial values
    private RecordSet getMailingRecipientRecords(Record inputRecord, String policyMailingId) {
        RecordSet resultRecordSet = new RecordSet();
        String selectedPolicyNos = inputRecord.getStringValue("selectedPolicyNos");
        String inValidPolicyNos = "";
        //remove redundent policyNos by putting them in a Set.
        String[] policyNos = selectedPolicyNos.split(",");
        HashSet policySet = new HashSet();
        for (int i = 0; i < policyNos.length; i++) {
            policySet.add(policyNos[i]);
        }

        for (Iterator itor = policySet.iterator(); itor.hasNext();) {
            String policyNo = (String) itor.next();
            Record policyNoRecord = new Record();
            policyNoRecord.setFieldValue("policyNo", policyNo);
            RecordSet validateResultRecordSet = validateMailingRecipientRecordSet(policyNoRecord);
            if (validateResultRecordSet.getSize() == 0) {
                inValidPolicyNos = inValidPolicyNos + policyNo + " ";
            }
            else {
                Record record = new Record();
                MailingRecipientFields.setPolicyId(record, validateResultRecordSet.getRecord(0).getStringValue("policyId"));
                MailingRecipientFields.setPolicyMailingId(record, policyMailingId);
                MailingRecipientFields.setPolicyMailingDtlId(record, getDbUtilityManager().getNextSequenceNo().toString());
                resultRecordSet.addRecord(record);
            }
        }
        if (!StringUtils.isBlank(inValidPolicyNos)) {
            MessageManager.getInstance().addErrorMessage("pm.policyMailing.validateRecipient.error", new Object[]{inValidPolicyNos});

        }
        return resultRecordSet;
    }

    // transfer mailing dtls to recordset for input
    private RecordSet getInputRecordSetForExludedPolicies(String mailingDtls) {
        RecordSet result = new RecordSet();
        String[] mailingDtlIds = mailingDtls.split(",");
        for (int i = 0; i < mailingDtlIds.length; i++) {
            Record record = new Record();
            record.setFieldValue("policyMailingDtlId", mailingDtlIds[i]);
            result.addRecord(record);
        }
        return result;
    }

    private DBUtilityManager dbUtilityManager;
    private WorkbenchConfiguration workbenchConfiguration;
    protected static final String MAINTAIN_POLICY_MAILING_ACTION_CLASS_NAME = "dti.pm.policymgr.mailingmgr.struts.MaintainPolicyMailingAction";
}
