package dti.pm.transactionmgr.batchrenewalprocessmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class is an interface for batch renewal dao
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Sep 25, 2007          zlzhu     Created
 * 06/20/08         yhchen        apply p_enforce_max_count for procedure PM_SEL_RENEWAL_EVENT_DETAIL
 * 11/11/2010       syang         Issue 112665 - Modified saveIssueRenewalBatches to correct the input paraemter.
 * 02/14/2011       sxm           Issue 117097 - pass agent ID list to pm_batch_renewal.Issue_Renewal_Batches().
 * 09/28/2011       dzhang        Issue 123437 - Added deleteRenewalWipBatches and rerateRenewalPolicyBatches.
 * 10/13/2011       wfu           125309 - Merge getDbmsJobCount and getRenewalEventCount to getEventCount.
 * 06/11/2013       fcb           145501 - Added support for common anniversary batch renewals.
 * 04/08/2014       awu           156019 - Added releaseOutput.
 * 06/23/2016       tzeng         167531 - 1) Added hasBatchIncludePolicy, getLatestBatchForPolicy, addPolicyToBatch,
 *                                            excludePolicyFromBatch.
 *                                         2) Modified createBatchRenewalProcess to add switch as system parameter
 *                                            PM_AUTOREN_BAT_SCHED.
 *                                            If it turn on, Pm_Batch_Renewal.Create_Renewal_Batches_Sched will instead
 *                                            of Pm_Batch_Renewal.Create_Renewal_Batches.
 * ---------------------------------------------------
 */

public class BatchRenewalProcessJdbcDAO extends BaseDAO implements BatchRenewalProcessDAO {

    /**
     * getDefaultDates data
     * <p/>
     *
     * @param inputRecord         input record
     * @return the result which met the condition
     */
    public Record getDefaultDates(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultDates", new Object[]{inputRecord});
        }
        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Sel_Default_Dates");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load data.", e);
            l.throwing(getClass().getName(), "getDefaultDates", ae);
            throw ae;
        }
        Record record = rs.getRecord(0);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultDates", new Object[]{record});
        }
        return record;
    }

    /**
     * save/update the batch renewal data
     * <p/>
     *
     * @param inputRecord batch renewal record set
     * @return number of the updated rows
     */
    public int createBatchRenewalProcess(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "createBatchRenewalProcess", new Object[]{inputRecord});
        }
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "policyExpFrom"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "policyExpTo"));
        mapping.addFieldMapping(new DataRecordFieldMapping("agentId", "agent"));
        mapping.addFieldMapping(new DataRecordFieldMapping("underwriterId", "underwriter"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyCount", "maxPolicies"));

        StoredProcedureDAO spDao = null;
        if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_AUTOREN_BAT_SCHED)).booleanValue()) {
            spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Create_Renewal_Batches_Sched", mapping);
        } else {
            spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Create_Renewal_Batches", mapping);
        }
        RecordSet rs = null;
        int renewNum = -1;
        try {
            rs = spDao.execute(inputRecord);
            Record rec = rs.getSummaryRecord();
            renewNum = rec.getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save data.", e);
            l.throwing(getClass().getName(), "createBatchRenewalProcess", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "createBatchRenewalProcess",new Integer(renewNum));
        }
        return renewNum;
    }
    /**
     * judge if progress is in progress
     * @param record records to judge
     * @return returns error code,if return 0 then has no error
     * 1 for "Renewal process still in progress"
     * 2 for "Another Renewal process is in progress"
     * 3 for "Another Renewal process is in progress"
     */
    public int isRenewalInProgress(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRenewalInProgress", new Object[]{record});
        }
        record.setFieldValue("errorCode","0");
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("lsPolicyType", "policyType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "policyExpFrom"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "policyExpTo"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Is_Renewal_In_Progress", mapping);
        RecordSet rs = null;
        try {
            rs = spDao.execute(record);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save data.", e);
            l.throwing(getClass().getName(), "isRenewalInProgress", ae);
            throw ae;
        }
        int errorCode = Integer.parseInt(rs.getSummaryRecord().getStringValue("errorCode"));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isRenewalInProgress", new Integer(errorCode));
        }
        return errorCode;
    }

    /**
     * judge if record is valid for batch renewal
     * @param record record to judge
     * @return record with error code and error message if any
     * @throws ValidationException if records are invalid
     */
    public Record validateRenewal(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRenewal", new Object[]{record});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "policyExpFrom"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "policyExpTo"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Renewal", mapping);
        RecordSet rs;
        try {
            rs = spDao.execute(record);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate renewal data.", e);
            l.throwing(getClass().getName(), "validateRenewal", ae);
            throw ae;
        }

        Record returnRecord = rs.getSummaryRecord();
        l.exiting(getClass().getName(), "validateRenewal", returnRecord);
        return returnRecord;
    }

    /**
     * To load all renewal event data.
     *
     * @param inputRecord         input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return
     */
    public RecordSet loadAllRenewalEvent(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRenewalEvent", new Object[]{inputRecord, recordLoadProcessor});
        }
        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "startSearchDateFilter"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "endSearchDateFilter"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termTypeCode", "termTypeFilter"));
        mapping.addFieldMapping(new DataRecordFieldMapping("submittedBy", "submittedByFilter"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Sel_Renewal_Event", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load renewal event data.", e);
            l.throwing(getClass().getName(), "loadAllRenewalEvent", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRenewalEvent", new Object[]{rs});
        }
        return rs;
    }

    /**
     * To load all renewal event detail data.
     *
     * @param inputRecord         input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return
     */
    public RecordSet loadAllRenewalDetail(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRenewalDetail", new Object[]{inputRecord, recordLoadProcessor});
        }
        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("renewalId", "renewalEventId"));         
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Renewal_Event_Detail", mapping);
        //constant value
        inputRecord.setFieldValue("enforceMaxCount","Y");
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load renewal detail data.", e);
            l.throwing(getClass().getName(), "loadAllRenewalDetail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRenewalDetail", new Object[]{rs});
        }
        return rs;
    }

    /**
     * To save all exclude policies.
     *
     * @param inputRecords a set of excluded policy code Records for saving.
     * @return the number of rows updated.
     */
    public int saveAllExcludePolicy(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllExcludePolicy", new Object[]{inputRecords});

        int updateCount;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Save_Renewal_Exclusion");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save exclusions.", e);
            l.throwing(getClass().getName(), "saveAllExcludePolicy", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAllExcludePolicy", new Integer(updateCount));
        return updateCount;
    }

    /**
     * To Issue the pre-renewal batches.
     *
     * @param inputRecord input record
     * @return the number of rows updated.
     */
    public void saveIssueRenewalBatches(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveIssueRenewalBatches", new Object[]{inputRecord});

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("renewEventId", "renewalEventId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueState", "issueStateListDesc"));
        mapping.addFieldMapping(new DataRecordFieldMapping("practiceState", "practiceStateListDesc"));
        mapping.addFieldMapping(new DataRecordFieldMapping("agentId", "agentIdList"));
        mapping.addFieldMapping(new DataRecordFieldMapping("underwriterId", "underwriterIdList"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Issue_Renewal_Batches", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to issue renewal batches.", e);
            l.throwing(getClass().getName(), "saveIssueRenewalBatches", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveIssueRenewalBatches");
    }

    /**
     * To get renewal event and running jobs count for issue.
     *
     * @param inputRecord   input record with renewal event PK and type (issue or print).
     * @return the event count.
     */
    public int getEventCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEventCount", new Object[]{inputRecord});
        }

        int returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Get_Event_Count");
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get event count.", e);
            l.throwing(getClass().getName(), "getEventCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEventCount");
        }
        return returnValue;
    }

    /**
     * To merge the renewal batches.
     *
     * @param inputRecord input record with selected renewal event PK and numbers of them.
     * @return the number of rows updated.
     */
    public void saveMergeRenewalEvents(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveMergeRenewalEvents", new Object[]{inputRecord});

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("renewEventId", "renewalEventIds"));
        mapping.addFieldMapping(new DataRecordFieldMapping("numEvents", "eventNumbers"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Web_Merge_Renewal_Events", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to merge renewal events.", e);
            l.throwing(getClass().getName(), "saveMergeRenewalEvents", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveMergeRenewalEvents");
    }

    /**
     * To retrieve the common anniversary term dates.
     *
     * @param inputRecord input record with renewal event's policy type code, effective date.
     * @return a record with the anniversary term dates.
     */
    public Record getCommonAnniversaryTermDates(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCommonAnniversaryTermDates", new Object[]{inputRecord});
        }

        Record record;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "effectiveFromDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Get_Common_Anni_Term_Dates", mapping);
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get Common Anniversary Term Dates.", e);
            l.throwing(getClass().getName(), "getCommonAnniversaryTermDates", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCommonAnniversaryTermDates");
        }
        return record;
    }

    /**
     * To retrieve the common anniversary term dates y term year.
     *
     * @param inputRecord input record with renewal event's policy type code, effective date.
     * @return a record set with the anniversary term dates.
     */
    public RecordSet getCommonAnniversaryYearTermDates(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCommonAnniversaryYearTermDates", new Object[]{inputRecord});
        }

        RecordSet rs;
        DataRecordMapping mapping = new DataRecordMapping();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Get_Common_Anni_Year_Term_Dt", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get Common Anniversary Year Term Dates.", e);
            l.throwing(getClass().getName(), "getCommonAnniversaryTermYearTermDates", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCommonAnniversaryYearTermDates", rs);
        }
        return rs;
    }

    /**
     * To submit forms for printing.
     *
     * @param inputRecord input record with renewal event PK and type.
     * @return record with error code and message.
     */
    public Record saveSubmitPrintingJob(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveSubmitPrintingJob", new Object[]{inputRecord});

        RecordSet rs;
        DataRecordMapping mapping = new DataRecordMapping();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Web_Submit_Output", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to submit printing job.", e);
            l.throwing(getClass().getName(), "saveSubmitPrintingJob", ae);
            throw ae;
        }

        Record returnRecord = rs.getSummaryRecord();
        l.exiting(getClass().getName(), "saveSubmitPrintingJob", returnRecord);
        return returnRecord;
    }

    /**
     * To save print device.
     *
     * @param inputRecord input record with renewal event PK and device code.
     */
    public void savePrintDevice(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "savePrintDevice", new Object[]{inputRecord});

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("deviceCode", "printDevice"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Save_Print_Device", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save print device.", e);
            l.throwing(getClass().getName(), "savePrintDevice", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "savePrintDevice");
    }

    /**
     * To get existing printing job count.
     *
     * @param inputRecord input record with renewal event PK.
     * @return the existing printing job count.
     */
    public int getPrintJobCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPrintJobCount", new Object[]{inputRecord});
        }

        int returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Get_Print_Job_Count");
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get print job count.", e);
            l.throwing(getClass().getName(), "getPrintJobCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPrintJobCount");
        }
        return returnValue;
    }

    /**
     * To delete all related renewal WIPs .
     *
     * @param inputRecords a set of renewal event for processing.
     */
    public void deleteRenewalWipBatches(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteRenewalWipBatches", new Object[]{inputRecords});

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Delete_Renewal_Wip_Batches");
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete renewal WIPs.", e);
            l.throwing(getClass().getName(), "deleteRenewalWipBatches", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "deleteRenewalWipBatches");
    }

    /**
     * To ReRate all renewal event related policies.
     *
     * @param inputRecords a set of renewal event records for processing.
     */
    public void rerateRenewalPolicyBatches(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "rerateRenewalPolicyBatches", new Object[]{inputRecords});

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Rerate_Renewal_Policy_Batches");
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to reRate policies.", e);
            l.throwing(getClass().getName(), "rerateRenewalPolicyBatches", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "rerateRenewalPolicyBatches");
    }

    /**
     * getCommonAnniversaryPolicyType data
     * <p/>
     *
     * @param inputRecord         input record
     * @return the list of available common policy types
     */
    public RecordSet getCommonAnniversaryPolicyType(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCommonAnniversaryPolicyType", new Object[]{inputRecord});
        }
        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Get_Common_Anni_Pol_Type");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load data.", e);
            l.throwing(getClass().getName(), "getCommonAnniversaryPolicyType", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCommonAnniversaryPolicyType", new Object[]{rs});
        }
        return rs;
    }

    /**
     * To release forms for a renewal event
     *
     * @param inputRecord input record
     */
    public void releaseOutput(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "releaseOutput", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Release_Output");
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to release renewal event.", e);
            l.throwing(getClass().getName(), "releaseOutput", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "releaseOutput");
        }
    }

    /**
     * Checks if there is a batch that includes the current policy/term.
     *
     * @param inputRecord
     * @return
     */
    public boolean hasBatchIncludePolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasBatchIncludePolicy", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Has_Batch_Include_Policy");
        boolean result = false;

        try {
            result = spDao.executeUpdate(inputRecord).getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check if any batch include the policy.", e);
            l.throwing(getClass().getName(), "hasBatchIncludePolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasBatchIncludePolicy", result);
        }
        return result;
    }

    /**
     * Get the latest qualified batch which meet the new policy criteria and timing schedule.
     * @param inputRecord
     * @return
     */
    public Integer getLatestBatchForPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLatestBatchForPolicy", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Get_Latest_Batch_For_Policy");
        Integer result = null;

        try {
            result = spDao.executeUpdate(inputRecord).getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get latest batch.", e);
            l.throwing(getClass().getName(), "getLatestBatchForPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLatestBatchForPolicy", result);
        }
        return result;
    }

    /**
     * Add policy to existent batch renewal event.
     * @param inputRecord
     */
    public void addPolicyToBatch(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addPolicyToBatch", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Add_Policy_To_Batch");

        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to add policy to batch event", e);
            l.throwing(getClass().getName(), "addPolicyToBatch", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addPolicyToBatch");
        }
    }

    /**
     * Exclude policy from batch renewal event.
     * @param inputRecord
     */
    public void excludePolicyFromBatch(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "excludePolicyFromBatch", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Exclude_Policy_From_Batch");

        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to exclude policy from batch event", e);
            l.throwing(getClass().getName(), "excludePolicyFromBatch", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "excludePolicyFromBatch");
        }
    }
}
