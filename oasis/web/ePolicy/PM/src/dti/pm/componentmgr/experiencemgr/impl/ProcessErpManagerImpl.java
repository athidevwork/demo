package dti.pm.componentmgr.experiencemgr.impl;

import dti.ci.core.struts.AddRowNoLoadProcessor;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.componentmgr.experiencemgr.ExperienceComponentFields;
import dti.pm.componentmgr.experiencemgr.ProcessErpManager;
import dti.pm.componentmgr.experiencemgr.dao.ProcessErpDAO;
import dti.pm.policymgr.PolicyHeader;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of Experience Rating Programs Manager Interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 1, 2011
 *
 * @author ryzhao
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */

public class ProcessErpManagerImpl implements ProcessErpManager {

    /**
     * Get search criteria default values
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getDefaultValuesForSearchCriteria(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getDefaultValuesForSearchCriteria",
            new Object[]{inputRecord});

        Record outputRecord = new Record();
        outputRecord.setFields(inputRecord);

        // Renewal Date: Default to current system date by default.
        String renewalDate = DateUtils.formatDate(new Date());
        // Renewal Date: If user accessed this page from Policy/Risk page, default to the policy's term effective date for the term being displayed.
        if (inputRecord.hasStringValue(ExperienceComponentFields.POLICY_ID)) {
            renewalDate = ExperienceComponentFields.getTermEff(inputRecord);
        }
        // Renewal Year: Default to the year portion of the value in Renewal Date field.
        String renewalYear = StringUtils.isBlank(renewalDate) ?
            null : String.valueOf(DateUtils.getYear(DateUtils.parseDate(renewalDate)));

        ExperienceComponentFields.setRenewalDate(outputRecord, renewalDate);
        ExperienceComponentFields.setRenewalYear(outputRecord, renewalYear);
        ExperienceComponentFields.setShowAll(outputRecord, YesNoFlag.N);

        //Set page entitlement
        ProcessErpEntitlementRecordLoadProcessor.setEntitlementValuesForErp(policyHeader, outputRecord);

        l.exiting(getClass().getName(), "getDefaultValuesForSearchCriteria", outputRecord);
        return outputRecord;
    }

    /**
     * Load all ERP data
     *
     * @param policyHeader
     * @param inputRecord  with user entered search criteria
     * @return RecordSet
     */
    public RecordSet loadAllErp(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllErp", new Object[]{inputRecord});
        }

        Record newInputRecord = new Record();
        newInputRecord.setFields(inputRecord);

        // Set called from field before calling DAO
        if (newInputRecord.hasStringValue(ExperienceComponentFields.RISK_ID)) {
            ExperienceComponentFields.setCalledFrom(newInputRecord, CALLED_FROM_RISK);
        }
        else if (newInputRecord.hasStringValue(ExperienceComponentFields.POLICY_ID)) {
            ExperienceComponentFields.setCalledFrom(newInputRecord, CALLED_FROM_POLICY);
        }
        else {
            ExperienceComponentFields.setCalledFrom(newInputRecord, CALLED_FROM_MAIN_MENU);
        }

        // If the page is opened from Policy/Risk page, or if it is invoked from the Show All option, set below fields Null
        if (newInputRecord.hasStringValue(ExperienceComponentFields.POLICY_ID) ||
            ExperienceComponentFields.getShowAll(newInputRecord).booleanValue()) {
            ExperienceComponentFields.setRenewalDate(newInputRecord, "");
            ExperienceComponentFields.setProcessDate(newInputRecord, "");
            ExperienceComponentFields.setErpIssueStateCode(newInputRecord, "");
            ExperienceComponentFields.setBatchNo(newInputRecord, "");
        }

        RecordLoadProcessor lp = new AddRowNoLoadProcessor();
        ProcessErpEntitlementRecordLoadProcessor processLP = new ProcessErpEntitlementRecordLoadProcessor(policyHeader, newInputRecord);
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(lp, processLP);

        RecordSet rs = getProcessErpDAO().loadAllErp(newInputRecord, lp);

        if (rs.getSize() < 1) {
            MessageManager.getInstance().addInfoMessage("pm.processErp.noErpDataFound.information");
            rs.getSummaryRecord().setFieldValue(TOTAL_POLICY_NUMBER_FIELD_NAME, String.valueOf(0));
        }
        else {
            calculateTotalPolicyNumber(rs);
        }

        l.exiting(getClass().getName(), "loadAllErp", rs);
        return rs;
    }

    /**
     * Calculate total number of distinct policy.
     *
     * @param rs Result set that has all the erp data with policy information in it.
     */
    private void calculateTotalPolicyNumber(RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "calculateTotalPolicyNumber", new Object[]{rs});
        }

        HashMap map = new HashMap();
        for (int i = 0; i < rs.getSize(); i++) {
            Record record = rs.getRecord(i);
            map.put(record.getStringValue("policyNo"), "");
        }
        rs.getSummaryRecord().setFieldValue(TOTAL_POLICY_NUMBER_FIELD_NAME, String.valueOf(map.size()));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "calculateTotalPolicyNumber");
        }
    }

    /**
     * Validate search criteria before process ERP
     *
     * @param inputRecord
     */
    protected void validateSearchCriteria(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSearchCriteria", new Object[]{inputRecord});
        }

        // If this page was accessed from Main Menu, process date and issue state couldn't be null.
        if (!inputRecord.hasStringValue(ExperienceComponentFields.POLICY_ID)) {
            if (!inputRecord.hasStringValue(ExperienceComponentFields.PROCESS_DATE)) {
                MessageManager.getInstance().addErrorMessage("pm.processErp.noProcessDate.error",
                    ExperienceComponentFields.PROCESS_DATE);
            }
            if (!inputRecord.hasStringValue(ExperienceComponentFields.ERP_ISSUE_STATE_CODE)) {
                MessageManager.getInstance().addErrorMessage("pm.processErp.noIssueState.error",
                    ExperienceComponentFields.ERP_ISSUE_STATE_CODE);
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid search criteria data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSearchCriteria");
        }
    }

    /**
     * Process ERP
     *
     * @param inputRecord
     * @return Record
     */
    public Record processErp(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processErp", new Object[]{inputRecord});
        }

        Record outputRecord = null;
        // If accessed from Policy/Risk page
        if (inputRecord.hasStringValue(ExperienceComponentFields.POLICY_ID)) {
            try {
                outputRecord = getProcessErpDAO().processErp(inputRecord);
            }
            catch (Exception e) {
                throw new AppException(AppException.UNEXPECTED_ERROR, "System failure when process ERP.");
            }
        }
        // If accessed from Main Menu
        else {
            validateSearchCriteria(inputRecord);
            try {
                outputRecord = getProcessErpDAO().processErpBatch(inputRecord);
            }
            catch (Exception e) {
                throw new AppException(AppException.UNEXPECTED_ERROR, "System failure when process ERP.");
            }
        }

        int rc = Integer.parseInt(outputRecord.getStringValue("rc", "0"));

        // If rc < 0, then, processing failed.
        if (rc < 0) {
            MessageManager.getInstance().addErrorMessage("pm.processErp.processErp.error");
        }
        // If rc >= 0, then deleting was successful.  The message returned by retMsg will be displayed to the user.
        else {
            String retMsg = outputRecord.getStringValue("retMsg", "Process ERP data successfully.");
            MessageManager.getInstance().addInfoMessage("pm.processErp.processErp.success", new Object[]{retMsg});
        }

        l.exiting(getClass().getName(), "processErp", outputRecord);
        return outputRecord;
    }

    /**
     * Save all updated ERP
     *
     * @param inputRecords
     * @return the number of rows updated
     */
    public int saveAllErp(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllErp", new Object[]{inputRecords});
        }
        // Get the updated records.
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(new String[]{UpdateIndicator.UPDATED}));
        int updateCount = getProcessErpDAO().saveAllErp(changedRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllErp", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Delete a ERP batch
     *
     * @param inputRecord
     * @return Record
     */
    public Record deleteErpBatch(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteErpBatch", new Object[]{inputRecord});
        }

        Record outputRecord = getProcessErpDAO().deleteErpBatch(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteErpBatch", outputRecord);
        }

        return outputRecord;
    }

    /**
     * Display the policies that have errors when deleting an ERP batch.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllErrorPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllErrorPolicy", new Object[]{inputRecord});
        }

        RecordSet rs = getProcessErpDAO().loadAllErrorPolicy(inputRecord);

        l.exiting(getClass().getName(), "loadAllErrorPolicy", rs);

        return rs;
    }

    /**
     * Verify config
     */
    public void verifyConfig() {
        if (getProcessErpDAO() == null)
            throw new ConfigurationException("The required property 'processErpDAO' is missing.");
    }

    public ProcessErpDAO getProcessErpDAO() {
        return m_processErpDAO;
    }

    public void setProcessErpDAO(ProcessErpDAO processErpDAO) {
        m_processErpDAO = processErpDAO;
    }

    private ProcessErpDAO m_processErpDAO;

    private static final String CALLED_FROM_POLICY = "POLICY_TAB";
    private static final String CALLED_FROM_RISK = "RISK_TAB";
    private static final String CALLED_FROM_MAIN_MENU = "GOTO_MENU";
    public static final String TOTAL_POLICY_NUMBER_FIELD_NAME = "totalPolicyNumber";

}
