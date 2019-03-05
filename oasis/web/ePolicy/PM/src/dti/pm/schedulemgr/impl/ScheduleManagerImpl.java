package dti.pm.schedulemgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.schedulemgr.ScheduleFields;
import dti.pm.schedulemgr.ScheduleManager;
import dti.pm.schedulemgr.dao.ScheduleDAO;
import dti.pm.schedulemgr.struts.MaintainScheduleAction;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import dti.pm.validationmgr.impl.StandardEffectiveToDateRecordValidator;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.coveragemgr.CoverageHeader;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This Class provides the implementation details of ScheduleManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/18/2009       yhyang     #91167 Remove the validation of Entity Name in validateAllSchedules().
 * 09/21/2010       syang       Issue 110819 - Added FilterOfficialRowForEndquoteRecordLoadProcessor to loadAllSchedules().
 * 08/30/2011       ryzhao      124458 - Modified validateAllSchedules to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 08/31/2011       ryzhao      124458 - Remove DateUtils.formatDate() and call FormatUtils.formatDateForDisplay() directly.
 * 12/12/2012       xnie        137972 - Modified validateAllSchedules() to remove logic which checks schedule date
 *                                       must within transaction effFromdate and term effToDate.
 * 07/08/2013       awu         145851 - Modified validateAllSchedules() to validate overlap date records only for
 *                                       WIP and displaying records.
 * 03/02/2013       awu         161383 - Modified validateAllSchedules() to do required validation for Entity Name.
 * 08/08/2016       xnie        178302 - Modified validateAllSchedules() to check entity name required when user inputted
 *                                       entity name is invalid from front end.
 * ---------------------------------------------------
 */

public class ScheduleManagerImpl implements ScheduleManager {

    public ScheduleDAO getScheduleDAO() {
        return m_scheduleDAO;
    }

    public void setScheduleDAO(ScheduleDAO scheduleDAO) {
        m_scheduleDAO = scheduleDAO;
    }

    /**
     * Load the RiskHerder Bean or CoverageHeader bean of the PolicyHeader object
     * construct a inputrecord and load schedules for risk or coverage
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available sechedules for risk/coverage.
     */
    public RecordSet loadAllSchedules(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSchedules", new Object[]{policyHeader});
        }
        // Build the input record ,offer parameters for PM_SEL_SCHEDULE_INFO procedure dao
        Record inputRecord = policyHeader.toRecord();
        // 105611, override the transactionLogId in policyHeader. 
        inputRecord.setFields(record);
        // Issue 110819, ENDQUOTE is missing.
        RecordMode recordModeCode;
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if(viewMode.isOfficial()){
           recordModeCode = RecordMode.OFFICIAL;
        }
        else if (viewMode.isEndquote()){
            recordModeCode = RecordMode.ENDQUOTE;
        }
        else {
           recordModeCode = RecordMode.TEMP;
        }

        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        String sourceTableName;
        String sourceRecordId;
        if (policyHeader.getCoverageHeader() == null) {
            sourceTableName = SOURCE_TABLE_NAME_RISK;
            sourceRecordId = policyHeader.getRiskHeader().getRiskBaseRecordId();
        }
        else {
            sourceTableName = SOURCE_TABLE_NAME_COVERAGE;
            sourceRecordId = policyHeader.getCoverageHeader().getCoverageBaseRecordId();
        }

        PMCommonFields.setRecordModeCode(inputRecord, recordModeCode);
        ScheduleFields.setSourceRecordId(inputRecord, sourceRecordId);
        ScheduleFields.setSourceTableName(inputRecord, sourceTableName);

        ScheduleDAO c = getScheduleDAO();
        /* Setup the entitlements load processor */
        ScheduleFields.setScreenModeCode(inputRecord, screenModeCode);
        inputRecord.setFieldValue("transEffectiveFromDate",
            policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());

        RecordLoadProcessor entitlementRLP = ScheduleEntitlementRecordLoadProcessor.getInstance(inputRecord);
        entitlementRLP = RecordLoadProcessorChainManager.getRecordLoadProcessor(entitlementRLP, loadProcessor);
        // Issue 110819, filter official record for end quote.                                                                                     
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor = new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "policyScheduleId");
        entitlementRLP = RecordLoadProcessorChainManager.getRecordLoadProcessor(entitlementRLP, endquoteLoadProcessor);
        RecordSet rs;
        rs = c.loadAllSchedules(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSchedules", rs);
        }

        return rs;
    }

    /**
     * Save all Schedule' information
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the updated schedule info
     * @return the number of rows updated
     */
    public int saveAllSchedules(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSchedules", new Object[]{inputRecords});

        int updateCount = 0;

        /* Set term dates on all input records */
        inputRecords.setFieldValueOnAll(PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE, policyHeader.getTermEffectiveFromDate());
        inputRecords.setFieldValueOnAll(PolicyHeaderFields.TERM_EFFECTIVE_TO_DATE, policyHeader.getTermEffectiveToDate());

        // For issue 101699. System should use the current transaction log primary key.
        inputRecords.setFieldValueOnAll(ScheduleFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());

        /* Determine if anything has changed */
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        changedRecords.setFieldsOnAll(policyHeader.toRecord(), false);

        /* If a change has occurred to special handling data - validate, create a trans and save */
        if (changedRecords.getSize() > 0) {
            //validate all schedules
            validateAllSchedules(policyHeader, inputRecords);

            // Get the WIP records
            RecordSet wipRecords = inputRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
            RecordSet offRecords = inputRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));

            // Add the WIP records in batch mode
            updateCount += addAllSchedule(policyHeader, wipRecords);

            // Delete the WIP records marked for delete in batch mode
            RecordSet deleteRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
            updateCount += getScheduleDAO().deleteAllSchedules(deleteRecords);

            // Update the OFFICIAL records marked for update in batch mode
            RecordSet updateRecords = offRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
            // For the official records set the effective from date equal to the transaction effective date
            updateCount += getScheduleDAO().updateAllSchedules(updateRecords);
        }

        l.exiting(getClass().getName(), "saveAllSchedules", new Integer(updateCount));
        return updateCount;
    }

    /**
     * validate all Schedule' information
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the updated schedule info
     */
    protected void validateAllSchedules(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllSchedules", new Object[]{policyHeader, inputRecords});
        }

        // Get an instance of the Standard Effective To Date Rule Validator
        StandardEffectiveToDateRecordValidator effToDateValidator =
            new StandardEffectiveToDateRecordValidator(policyHeader,
                ScheduleFields.EFFECTIVE_FROM_DATE, ScheduleFields.EFFECTIVE_TO_DATE, ScheduleFields.POLICY_SCHEDULE_ID);

        //get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        RecordSet wipRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}))
            .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));

        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String rowNum = String.valueOf(r.getRecordNumber() + 1);
            String rowId = ScheduleFields.getPolicyScheduleId(r);
            String entityId = ScheduleFields.getEntityId(r);
            String entityName = ScheduleFields.getEntityName(r);

            // Framework has already validated Entity Name required in JS. Here we need to check if the inputted Entity
            // name is valid or not due to user is able to edit the Entity name from front end.
            if (StringUtils.isBlank(entityId) && !StringUtils.isBlank(entityName)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainSchedule.required.error",
                    new String[]{rowNum, "Entity Name"}, ScheduleFields.ENTITY_ID, rowId);
            }

            // Validate Standard Effective To Date Rule
            // Validation #6:  End Date must be greater than or equal to Start Date
            Date effToDate = DateUtils.parseDate(ScheduleFields.getEffectiveToDate(r));
            Date effFromDate = DateUtils.parseDate(ScheduleFields.getEffectiveFromDate(r));
            if (effToDate.before(effFromDate)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainSchedule.invalidEffectiveToDate.error");
            }
        }

        // Validation #3 #4:  Validate date overlap for the same entity / or between two records
        boolean isValid = true;

        boolean chkPolschdOverlap = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.CHK_POLSCHD_OVRLP)).booleanValue();
        if (chkPolschdOverlap) {
            String [] keyFieldNames = new String[]{ScheduleFields.ENTITY_ID};
            String messageKey = "pm.maintainSchedule.dateOverlapForSameEntity.error";
            ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
                ScheduleFields.EFFECTIVE_FROM_DATE, ScheduleFields.EFFECTIVE_TO_DATE, ScheduleFields.POLICY_SCHEDULE_ID,
                messageKey, keyFieldNames, keyFieldNames);
            isValid = continuityValidator.validate(wipRecords);
        }

        if (isValid) {
            boolean chkPolschdOverlapNoEnt = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.SCHD_OVRLP_NOENT)).booleanValue();
            if (chkPolschdOverlapNoEnt) {
                String [] keyFieldNames = new String[0];
                String messageKey = "pm.maintainSchedule.dateOverlap.error";
                ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
                    ScheduleFields.EFFECTIVE_FROM_DATE, ScheduleFields.EFFECTIVE_TO_DATE, ScheduleFields.POLICY_SCHEDULE_ID,
                    messageKey, keyFieldNames, keyFieldNames);
                continuityValidator.validate(wipRecords);
            }
        }

        // throw validation exception if data is invalid
        if ( MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid schedule data");
        }


        Record inputRecord = inputRecords.getSummaryRecord();

        // Validation #1:  Validate record count
        String valLocumCntStr = getSystemParmKeyValue(SysParmIds.PM_VAL_LOCUM_CNT, policyHeader.getPolicyTypeCode());
        Integer valLocumCnt = new Integer(valLocumCntStr == null ? -1 : Integer.parseInt(valLocumCntStr));
        if (valLocumCnt.intValue() > 0
            && !ConfirmationFields.isConfirmed("pm.maintainSchedule.confirm.totalCount", inputRecord)) {
            int recordsCnt = inputRecords.getSize();
            if (recordsCnt > valLocumCnt.intValue()) {
                MessageManager.getInstance().addConfirmationPrompt("pm.maintainSchedule.confirm.totalCount");
            }
        }

        // Validation #2:  Validate duration
        String valLocumDurationStr = getSystemParmKeyValue(SysParmIds.PM_VAL_LOCUM_DURATN, policyHeader.getPolicyTypeCode());
        Integer valLocumDuration = new Integer(valLocumDurationStr == null ? -1 : Integer.parseInt(valLocumDurationStr));
        if (valLocumDuration.intValue() > 0
            && !ConfirmationFields.isConfirmed("pm.maintainSchedule.confirm.totalDuration", inputRecord)) {
            int duration = 0;

            it = inputRecords.getRecords();
            while (it.hasNext()) {
                Record rec = (Record) it.next();
                duration += DateUtils.dateDiff(DateUtils.DD_DAYS,
                    DateUtils.parseDate(ScheduleFields.getEffectiveFromDate(rec)),
                    DateUtils.parseDate(ScheduleFields.getEffectiveToDate(rec)));
            }
            if (duration > valLocumDuration.intValue()) {
                MessageManager.getInstance().addConfirmationPrompt("pm.maintainSchedule.confirm.totalDuration");
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts()) {
            throw new ValidationException("Invalid schedule data");
        }

        l.exiting(getClass().getName(), "validateAllSchedules");
    }

    /**
     * Initial values defaults for a new schedule record
     *
     * @param policyHeader contains policy term id level information     *
     * @return Record
     */
    public Record getInitialValuesForSchedule(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValues", new Object[]{policyHeader,});
        }
        //get default record from workbench
        Record output = getWorkbenchConfiguration().getDefaultValues(MaintainScheduleAction.class.getName());

        // Get the initial entitlement values
        output.setFields(ScheduleEntitlementRecordLoadProcessor.getInitialEntitlementValuesForSchedule());

        //get initial values
        String sourceTableName;
        String sourceRecordId;
        if (policyHeader.getCoverageHeader() == null) {
            sourceTableName = SOURCE_TABLE_NAME_RISK;
            sourceRecordId = policyHeader.getRiskHeader().getRiskBaseRecordId();
        }
        else {
            sourceTableName = SOURCE_TABLE_NAME_COVERAGE;
            sourceRecordId = policyHeader.getCoverageHeader().getCoverageBaseRecordId();
        }

        //set field initial values to ouput reocrd
        // For issue 101311, the effective date should default to the later of risk/coverage effective and transaction effective.
        Transaction lastTransaction = policyHeader.getLastTransactionInfo();
        String effectiveFromDate = lastTransaction.getTransEffectiveFromDate();
        // Get Risk/Coverage Header from PolicyHeader.
        RiskHeader riskHeader = policyHeader.getRiskHeader();
        CoverageHeader coverageHeader = policyHeader.getCoverageHeader();
        String laterEffectiveFromDate = null;
        if(coverageHeader != null){
           laterEffectiveFromDate = coverageHeader.getCoverageEffectiveFromDate();
        }
        else if(riskHeader != null){
            laterEffectiveFromDate = riskHeader.getEarliestContigEffectiveDate();
        }
        if(!StringUtils.isBlank(laterEffectiveFromDate)){
            Date laterEffFromDate = DateUtils.parseDate(laterEffectiveFromDate);
            Date transEffFromDate = DateUtils.parseDate(effectiveFromDate);
            if(transEffFromDate.before(laterEffFromDate)){
               effectiveFromDate = laterEffectiveFromDate;
            }
        }
        ScheduleFields.setEffectiveFromDate(output, effectiveFromDate);
        ScheduleFields.setEffectiveToDate(output, policyHeader.getTermEffectiveToDate());
        PMCommonFields.setRecordModeCode(output, RecordMode.TEMP);
        ScheduleFields.setSourceTableName(output, sourceTableName);
        ScheduleFields.setSourceRecordId(output, sourceRecordId);
        ScheduleFields.setTransactionLogId(output, policyHeader.getLastTransactionInfo().getTransactionLogId());

        l.exiting(getClass().getName(), "getInitialValues");
        return output;
    }

    /**
     * Save all WIP input records with UPDATE_IND set to 'Y' - updated, or 'I' - inserted
     *
     * @param policyHeader the summary policy information corresponding to the provided risks.
     * @param wipRecords   a set of Records in WIP mode, each with the updated Scheudle Detail info
     *                     matching the fields returned from the loadAllScheudle method.
     * @return the number of rows updated.
     */
    private int addAllSchedule(PolicyHeader policyHeader, RecordSet wipRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllSchedule", new Object[]{wipRecords});

        int updateCount = 0;
        // Add the inserted WIP records in batch mode
        RecordSet insertedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        insertedWipRecords.setFieldValueOnAll("rowStatus", "NEW");
        insertedWipRecords.setFieldValueOnAll("policyScheduleId", null);

        updateCount += getScheduleDAO().addAllSchedules(insertedWipRecords);

        // Add the updated WIP records in batch mode
        RecordSet updatedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        updatedWipRecords.setFieldValueOnAll("rowStatus", "MODIFIED");

        updateCount += getScheduleDAO().addAllSchedules(updatedWipRecords);

        l.exiting(getClass().getName(), "addAllSchedule", new Integer(updateCount));
        return updateCount;
    }

     /**
     * copy all schedule data to target risk
     *
     * @param inputRecords
     */
    public void copyAllSchedule(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyAllSchedule", new Object[]{inputRecords});
        }

        Iterator scheduleIter = inputRecords.getRecords();
        int scheduleNum = 0;
        Record scheduleRec = null;
        StringBuffer schedulePkBuff = new StringBuffer();
        while (scheduleIter.hasNext()) {
            scheduleRec = (Record) scheduleIter.next();
            String schedulePk = scheduleRec.getStringValue("policyScheduleId");

            if (schedulePkBuff.length() != 0)
                schedulePkBuff.append(",");
            schedulePkBuff.append(schedulePk);
            scheduleNum++;
        }
        Record inputRecord = new Record();
        inputRecord.setFields(scheduleRec);
        inputRecord.setFieldValue("schedulePks", schedulePkBuff.toString());
        inputRecord.setFieldValue("numSchedules",String.valueOf(scheduleNum));

        getScheduleDAO().copyAllSchedule(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyAllSchedule");
        }
    }
    /**
     * get syspara,and get value according to the key
     * the format is ^key#value^
     *
     * @param sysPara
     * @param key
     * @return value
     */
    public String getSystemParmKeyValue(String sysPara, String key) {
        String paraStr = SysParmProvider.getInstance().getSysParm(sysPara);
        if (paraStr != null) {
            int index = paraStr.indexOf(key);
            if (index >= 0) {
                return paraStr.substring(paraStr.indexOf('#', index) + 1, paraStr.indexOf('^', index));
            }
        }
        return null;
    }

    public void verifyConfig() {
        if (getScheduleDAO() == null)
            throw new ConfigurationException("The required property 'scheduleDAO' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private ScheduleDAO m_scheduleDAO;
    private WorkbenchConfiguration m_workbenchConfiguration;

    protected static final String SOURCE_TABLE_NAME_RISK = "RISK";
    protected static final String SOURCE_TABLE_NAME_COVERAGE = "COVERAGE";
    protected static final String MAINTAIN_SCHEDULE_ACTION_CLASS_NAME = "dti.pm.schedulemgr.struts.MaintainScheduleAction";
}
