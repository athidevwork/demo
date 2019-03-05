package dti.pm.transactionmgr.batchrenewalprocessmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.DateUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.StringUtils;
import dti.oasis.util.LoadProcessor;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.securitymgr.SecurityManager;
import dti.pm.core.http.RequestIds;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalProcessManager;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalFields;
import dti.pm.transactionmgr.batchrenewalprocessmgr.dao.BatchRenewalProcessDAO;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * this class is an interface for batch renewal manager
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
 * Sep 28, 2011          dzhang    123437 - Added  deleteRenewalWipBatches and rerateRenewalPolicyBatches.
 * Oct 18, 2011          wfu       125309 - Modified validateInProgressEvent to use merged getEventCount.
 * Oct 18, 2011          dzhang    126283 - Added validateInProgressEvent logic for Delete WIP and Rerate.
 * Sep 14, 2012          tcheng    137096 - Modified loadAllRenewalDetail to replace ' with '' in policyNoFilter field
 * Jun 11, 2013          fcb       145501 - Added support for common anniversary batch renewals.
 * Mar 19, 2014          adeng     149313 - Modified validateForMergeRenewalEvents() to correct messages
 *                                          for duplicated error.
 * May 19, 2014          awu       149590 - Modified getInitialValuesForBatchRenew to set default value for primary risk type.
 * 08/04/2014            awu       156019 - Added  releaseOutput.
 * Aug 13, 2014          kxiang    156446 - Modified loadAllRenewalDetail to add parameter LoadProcessor.
 * Aug 27, 2014          kxiang    156446 - Modified loadAllRenewalDetail, if record excludeB is 'Y', set selectInd to
 *                                          '-1', else set to '0'.
 *                                          Modified saveAllExcludePolicy, if record selectInd is 'Y', set excludeB to
 *                                          'Y', else set to 'N'.
 * 06/23/2016            tzeng     167531 - 1) Added hasBatchIncludePolicy, getLatestBatchForPolicy, addPolicyToBatch,
 *                                             excludePolicyFromBatch.
 *                                          2) Modified createBatchRenewalProcess to display another message when any
 *                                             policy submitted successfully.
 * ---------------------------------------------------
 */

public class BatchRenewalProcessManagerImpl implements BatchRenewalProcessManager {
    /**
     * save all batch renewal data
     * <p/>
     *
     * @param inputRecord the batch renewal needed to save
     */
    public int createBatchRenewalProcess(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "createBatchRenewalProcess", new Object[]{inputRecord});
        }
        inputRecord.setFieldValue("termTypeCode", BatchRenewalFields.PolicyTermTypeCodeValues.NON_COMMON);
        validateForCreateBatchRenewal(inputRecord);
        int renewNum = getBatchRenewalProcessDAO().createBatchRenewalProcess(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "createBatchRenewalProcess", new Integer(renewNum));
        }
        if(renewNum>0){
            if(YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_AUTOREN_BAT_SCHED)).booleanValue()){
                MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.create.twoSteps.info",new Object[]{new Integer(renewNum)});
            }else{
                MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.create.info",new Object[]{new Integer(renewNum)});
            }
        }else{
            MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.nodata.error");
        }
        return renewNum;
    }

    /**
     * do validation before create batch renewal
     *
     * @param record input record for batch renewal
     */
    protected void validateForCreateBatchRenewal(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForCreateBatchRenewal", new Object[]{record});
        }
        //if renewal is already in progress
        int error = getBatchRenewalProcessDAO().isRenewalInProgress(record);
        if (error == 1) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.inprogress.error");
        }
        else if (error == 2) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.inprogress.another.error",
                new Object[]{record.getStringValue("policyType")});
        }
        else if (error == 3) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.inprogress.another.noarg.error");
        }

        //if data range is valid
        BatchRenewalFields.setProcessCode(record, BatchRenewalFields.ProcessCodeValues.PRERENEWAL);
        String policyExpFrom = BatchRenewalFields.getPolicyExpFrom(record);
        String policyExpTo = BatchRenewalFields.getPolicyExpTo(record);
        //it means policyExpFrom - policyExpTo
        long days = DateUtils.dateDiff(DateUtils.DD_DAYS, DateUtils.parseDate(policyExpTo),
            DateUtils.parseDate(policyExpFrom));
        if (days >= 0) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.invalid.data.range.error");
        }
        //if more than one years
        if (days <= -366) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.date.range.error");
        }
        SysParmProvider sysParm = SysParmProvider.getInstance();
        int maxDays = Integer.parseInt(sysParm.getSysParm(SysParmIds.PM_AUTO_REN_MAX_DAYS, SysParmIds.AutoRenewMaxDayValues.DEFAULT));
        Date currentDate = new Date(System.currentTimeMillis());
        long daysToExp = DateUtils.dateDiff(DateUtils.DD_DAYS, currentDate, DateUtils.parseDate(policyExpTo));
        //if system define a max day(max!=0),then compare it to current day
        if (maxDays > 0 && daysToExp - maxDays > 0) {
            MessageManager.getInstance().addErrorMessage(
                "pm.batchRenewalProcess.dates.toofar.error", new Object[]{new Integer(maxDays)});
        }

        //do other validations
        validateRenewal(record);
        //validate security
        //to compose security pattern
        /*
        ^POLICY_TYPE_CODE^' + comma delimited policy type code + ^ISSUE_STATE_CODE^ +
        comma delimited issue state code
         */
        String policyType = record.getStringValue("policyType");
        String issueState = record.getStringValue("issueState");
        String securityPattern = "^POLICY_TYPE_CODE^" + policyType + "^ISSUE_STATE_CODE^" + issueState + "^";
        boolean isSecurity = getSecurityManager().isDataSecured("PMS", "NON_COMN_RENEW_RENEWAL_BUTTON", securityPattern);
        if (isSecurity) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.security.error");
        }


        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid batch renewal data.");
        }
    }

    /**
     * load the batch renewal related data
     * <p/>
     *
     * @return the result
     */
    public Record getInitialValuesForBatchRenew() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultDates");
        }
        Record input = new Record();
        SysParmProvider sysParm = SysParmProvider.getInstance();
        Record result = getBatchRenewalProcessDAO().getDefaultDates(input);
        //if this param = Y,set all selects default value to all
        String ifSetToAll = sysParm.getSysParm(SysParmIds.PM_RENEW_DFLT_ALL, SysParmIds.RenewDefaultAllValues.DEFAULT);
        if (YesNoFlag.getInstance(ifSetToAll).booleanValue()) {
            result.setFieldValue("issueState", "ALL");
            result.setFieldValue("practiceState", "ALL");
            result.setFieldValue("underwriter", "ALL");
            result.setFieldValue("agent", "ALL");
            result.setFieldValue("policyType", "ALL");
            result.setFieldValue("primaryRiskType", "ALL");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultDates", new Object[]{result});
        }
        return result;
    }

    /**
     * Save all common anniversary batch renewal data
     * <p/>
     *
     * @param inputRecord the common anniversary batch renewal needed to save
     */
    public int createCommonAnniversaryBatchRenewalProcess(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "createCommonAnniversaryBatchRenewalProcess", new Object[]{inputRecord});
        }

        inputRecord.setFieldValue("termTypeCode", BatchRenewalFields.PolicyTermTypeCodeValues.COMMON);
        validateForCreateCommonAnniversaryBatchRenewal(inputRecord);

        int renewNum = 0;
        try {
            renewNum = getBatchRenewalProcessDAO().createBatchRenewalProcess(inputRecord);
        }
        catch (AppException ae) {
            String errorMsg = "The Pre-renewal process did not complete.";
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.commonAnniversary.noCompletion.error");
            throw new ValidationException(errorMsg);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "createCommonAnniversaryBatchRenewalProcess", new Integer(renewNum));
        }
        if(renewNum>0){
            MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.create.info",new Object[]{new Integer(renewNum)});
        }else{
            MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.nodata.error");
        }
        return renewNum;
    }

    /**
     * Perform validation before create common anniversary batch renewal
     *
     * @param record input record for batch renewal
     */
    protected void validateForCreateCommonAnniversaryBatchRenewal(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForCreateCommonAnniversaryBatchRenewal", new Object[]{record});
        }

        RecordSet rs = getBatchRenewalProcessDAO().getCommonAnniversaryYearTermDates(record);
        if (rs.getSize() != 1) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.commonAnniversary.noTermDates.error",
                new Object[]{record.getStringValue("policyYear")});
            return;
        }

        Record termDates = rs.getFirstRecord();
        record.setFieldValue("policyExpFrom", termDates.getStringValue("effectiveFromDate"));
        record.setFieldValue("policyExpTo", termDates.getStringValue("effectiveToDate"));

        int error = getBatchRenewalProcessDAO().isRenewalInProgress(record);
        if (error == 1) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.inprogress.error");
        }
        else if (error == 2) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.inprogress.another.error",
                new Object[]{record.getStringValue("policyType")});
        }
        else if (error == 3) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.inprogress.another.noarg.error");
        }

        record.setFieldValue("processCode", "PRERENEWAL");
        validateRenewal(record);

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid common anniversary batch renewal data.");
        }
    }

    /**
     * Load the common anniversary batch renewal related data
     * <p/>
     *
     * @return the result
     */
    public Record getInitialValuesForCommonAnniversaryBatchRenew() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForCommonAnniversaryBatchRenew");
        }
        Record record = new Record();
        RecordSet polType = getBatchRenewalProcessDAO().getCommonAnniversaryPolicyType(record);
        if (polType.getSize() == 0) {
            MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.commonAnniversary.noPolicyType.info");
        }

        Record result = new Record();
        result.setFieldValue("policyYear", Calendar.getInstance().get(Calendar.YEAR));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForCommonAnniversaryBatchRenew", new Object[]{result});
        }
        return result;
    }

    /**
     * To load all batch renewal event data.
     *
     * @param inputRecord   input record with search criteria.
     * @param loadProcessor record load processor
     * @return a record set met the condition.
     */
    public RecordSet loadAllRenewalEvent(Record inputRecord, LoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRenewalEvent", new Object[]{inputRecord});
        }

        // validate the search criteria
        validateSearchCriteria(inputRecord);

        // Load batch renewal event data
        RecordLoadProcessor processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            new BatchRenewalEntitlementRecordLoadProcessor(), (RecordLoadProcessor) loadProcessor);
        RecordSet rs = getBatchRenewalProcessDAO().loadAllRenewalEvent(inputRecord, processor);

        // if no data found, add warning message.
        if (rs.getSize() == 0) {
            MessageManager.getInstance().addWarningMessage("pm.batchRenewalProcess.nodata.found.error");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRenewalEvent", rs);
        }
        return rs;
    }

    /**
     * To load all batch renewal detail data.
     *
     * @param inputRecord input record with renewal event PK.
     * @param loadProcessor record load processor
     * @return a record set met the condition.
     */
    public RecordSet loadAllRenewalDetail(Record inputRecord, LoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRenewalDetail", new Object[]{inputRecord});
        }
        // Issue 137096
        if (inputRecord.hasStringValue(BatchRenewalFields.POLICY_NO_FILTER)) {
            BatchRenewalFields.setPolicyNoFilter(inputRecord,BatchRenewalFields.getPolicyNoFilter(inputRecord).replaceAll("'", "''"));
        }
        // Load batch renewal event data
        RecordLoadProcessor processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            new BatchRenewalDetailEntitlementRecordLoadProcessor(BatchRenewalFields.getProcessCode(inputRecord)),
            (RecordLoadProcessor) loadProcessor);
        RecordSet rs = getBatchRenewalProcessDAO().loadAllRenewalDetail(inputRecord, processor);

        // Set select indicator based on exclude indicator
        Iterator iter = rs.getRecords();
        while (iter.hasNext()) {
            Record record = (Record) iter.next();
            BatchRenewalFields.setSelectInd(record, BatchRenewalFields.getExcludeB(record).equals("Y") ? "-1" : "0");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRenewalDetail", rs);
        }
        return rs;
    }

    /**
     * To save all excluded policy
     *
     * @param records RecordSet with excluded policy info
     * @return the number updated
     */
    public int saveAllExcludePolicy(RecordSet records) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllExcludePolicy", new Object[]{records});
        }

        Iterator iter = records.getRecords();
        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            if (BatchRenewalFields.getSelectInd(rec).booleanValue()) {
                BatchRenewalFields.setExcludeB(rec, "Y");
            }
            else {
                BatchRenewalFields.setExcludeB(rec, "N");
            }
        }
        int updateCount = getBatchRenewalProcessDAO().saveAllExcludePolicy(records);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllExcludePolicy", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * To issue a pre-renewal batch event.
     *
     * @param inputRecord input record with pre-renewal event info
     */
    public void saveIssueRenewalBatches(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveIssueRenewalBatches", new Object[]{inputRecord});
        }

        // mapping fields to match 
        BatchRenewalFields.setPolicyType(inputRecord, BatchRenewalFields.getPolicyTypeCode(inputRecord));
        BatchRenewalFields.setPolicyExpFromDate(inputRecord, BatchRenewalFields.getEffectiveFromDate(inputRecord));
        BatchRenewalFields.setPolicyExpToDate(inputRecord, BatchRenewalFields.getEffectiveToDate(inputRecord));
        // do the validation
        validateForIssueRenewalBatches(inputRecord);

        try {
            getBatchRenewalProcessDAO().saveIssueRenewalBatches(inputRecord);
        }
        catch (AppException ae) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.issue.failed.error");
            throw ae;
        }
        MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.issue.success.info");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveIssueRenewalBatches");
        }
    }

    /**
     * To validate for capturing renewal event printer
     *
     * @param inputRecord
     */
    public void validateForCaptureRenewalBatchPrinter(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForCaptureRenewalBatchPrinter", new Object[]{inputRecord});
        }

        // Validation #1: there must be no other batch print process in progress
        validateInProgressEvent(inputRecord, "PRINT");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForCaptureRenewalBatchPrinter");
        }
    }

    /**
     * To submit the print job.
     *
     * @param inputRecord input record with print device code and renewal event id.
     */
    public void saveSubmitPrintingJob(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveSubmitPrintingJob", new Object[]{inputRecord});
        }

        // If print device is empty, do not save it.
        if (inputRecord.hasStringValue(BatchRenewalFields.PRINT_DEVICE)) {
            // If print job does not exist, save print device.
            int existPrintJobCount = getBatchRenewalProcessDAO().getPrintJobCount(inputRecord);
            if (existPrintJobCount <= 0) {
                // Save print device
                getBatchRenewalProcessDAO().savePrintDevice(inputRecord);
            }
        }

        // Submit print job
        BatchRenewalFields.setType(inputRecord, "SUBMIT");
        Record returnRecord;
        try {
            returnRecord = getBatchRenewalProcessDAO().saveSubmitPrintingJob(inputRecord);
        }
        catch (AppException ae) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.print.failed.error");
            throw ae;
        }

        int errorCode = Integer.parseInt(returnRecord.getStringValue("retCode"));
        if (errorCode < 0) {
            String errorMsg = returnRecord.getStringValue("retMsg");
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.save.error", new String[]{errorMsg});
            throw new ValidationException(errorMsg);
        }
        else {
            MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.print.success.info");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveSubmitPrintingJob");
        }
    }

    /**
     * To merge renewal events.
     *
     * @param inputRecords renewal events records for merge
     */
    public void saveMergeRenewalEvents(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveMergeRenewalEvents", new Object[]{inputRecords});
        }

        // do the validation
        validateForMergeRenewalEvents(inputRecords);

        // create a comma delimited string of renewal event pk
        RecordSet changedRecords = inputRecords.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
        int size = changedRecords.getSize();
        String renewalEventIds = "";
        for (int i = 0; i < size; i++) {
            Record rec = changedRecords.getRecord(i);
            renewalEventIds += BatchRenewalFields.getRenewalEventId(rec) + ",";
        }
        renewalEventIds = renewalEventIds.substring(0, renewalEventIds.length() - 1);

        Record record = new Record();
        BatchRenewalFields.setRenewalEventIds(record, renewalEventIds);
        BatchRenewalFields.setEventNumbers(record, String.valueOf(size));
        getBatchRenewalProcessDAO().saveMergeRenewalEvents(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveMergeRenewalEvents");
        }
    }

    /**
     * To delete renewal WIPs.
     *
     * @param inputRecords input record with pre-renewal event info
     */
    public void deleteRenewalWipBatches(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteRenewalWipBatches", new Object[]{inputRecords});
        }
        // do the validation
        validateForInProgressEvents(inputRecords, BatchRenewalFields.BatchRenewalTypeValues.DELETE_WIP);

        getBatchRenewalProcessDAO().deleteRenewalWipBatches(inputRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteRenewalWipBatches");
        }
    }

    /**
     * To Rerate renewal policies.
     *
     * @param inputRecords renewal events records
     */
    public void rerateRenewalPolicyBatches(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "rerateRenewalPolicyBatches", new Object[]{inputRecords});
        }

        // do the validation
        validateForInProgressEvents(inputRecords, BatchRenewalFields.BatchRenewalTypeValues.RERATE);

        getBatchRenewalProcessDAO().rerateRenewalPolicyBatches(inputRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "rerateRenewalPolicyBatches");
        }
    }    

    /**
     * A protected method to do the validation of batch renewal event data.
     * It calls BatchRenewalProcessDAO.validateRenewal() method.
     *
     * @param inputRecord
     */
    protected void validateRenewal(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRenewal", new Object[]{inputRecord});
        }

        Record returnRecord;
        try {
            returnRecord = getBatchRenewalProcessDAO().validateRenewal(inputRecord);
        }
        catch (AppException ae) {
            String errorMsg = "Validate renewal data error.";
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.save.error", new String[]{errorMsg});
            throw new ValidationException(errorMsg);
        }

        int errorCode = Integer.parseInt(returnRecord.getStringValue("returnCode"));
        if (errorCode < 0) {
            String errorMsg = returnRecord.getStringValue("returnMsg");
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.save.error", new String[]{errorMsg});
            throw new ValidationException(errorMsg);
        }

        l.exiting(getClass().getName(), "validateRenewal");
    }

    /**
     * To validate for saving issue renewal batches
     *
     * @param inputRecord
     */
    protected void validateForIssueRenewalBatches(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForIssueRenewalBatches", new Object[]{inputRecord});
        }

        // First, validate renewal
        validateRenewal(inputRecord);

        // Second, determine non other events are in progress for issuance
        validateInProgressEvent(inputRecord, "ISSUE");

        l.exiting(getClass().getName(), "validateForIssueRenewalBatches");
    }

    /**
     * validate search criteria when loading batch renewal event data
     *
     * @param record record with search criteria
     */
    protected void validateSearchCriteria(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSearchCriteria", new Object[]{record});
        }

        // Validation #1: Invalid Search Criteria - NULL Term Type
        if (StringUtils.isBlank(BatchRenewalFields.getTermType(record))) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.termType.null.error");
        }

        // Validation #2: Invalid Search Criteria - NULL Start Search or End Search
        String startSearchDateStr = BatchRenewalFields.getStartSearchDate(record);
        String endSearchDateStr = BatchRenewalFields.getEndSearchDate(record);

        if (StringUtils.isBlank(startSearchDateStr) || StringUtils.isBlank(endSearchDateStr)) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.dates.null.error");
        }
        else {
            // Validation #3: Invalid Search Criteria - End Date Prior to Start Date
            Date startSearchDate = DateUtils.parseDate(startSearchDateStr);
            Date endSearchDate = DateUtils.parseDate(endSearchDateStr);
            if (!startSearchDate.before(endSearchDate)) {
                MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.endDate.prior.startDate.error");
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Batch Renewal Search Criteria.");
        }

        l.exiting(getClass().getName(), "validateSearchCriteria");
    }

    /**
     * To validate in progress event for Issue and Print.
     *
     * @param inputRecord
     * @param type        ISSUE or PRINT
     */
    private void validateInProgressEvent(Record inputRecord, String type) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateInProgressEvent", new Object[]{inputRecord, type});
        }

        String errType;
        if (type.equals(BatchRenewalFields.BatchRenewalTypeValues.ISSUE)) {
            errType = BatchRenewalFields.BatchRenewalErrorTypeValues.ISSUE_ERR_TYPE;
        }
        else if (type.equals(BatchRenewalFields.BatchRenewalTypeValues.DELETE_WIP)) {
            errType = BatchRenewalFields.BatchRenewalErrorTypeValues.DELETE_WIP_ERR_TYPE;
        }
        else if (type.equals(BatchRenewalFields.BatchRenewalTypeValues.RERATE)) {
            errType = BatchRenewalFields.BatchRenewalErrorTypeValues.RERATE_ERR_TYPE;
        }
        else {
            errType = BatchRenewalFields.BatchRenewalErrorTypeValues.BATCH_PRINT_ERR_TYPE;
        }
        
        BatchRenewalFields.setType(inputRecord, type);
        int eventCount = getBatchRenewalProcessDAO().getEventCount(inputRecord);
        if (eventCount > 0) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.eventInProgress.error",
                new String[]{errType});
            throw new ValidationException("In progress event found.");
        }

        l.exiting(getClass().getName(), "validateInProgressEvent");
    }

    /**
     * To validate for saving issue renewal batches
     *
     * @param inputRecords
     */
    protected void validateForMergeRenewalEvents(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForMergeRenewalEvents", new Object[]{inputRecords});
        }

        // Validation #1: Only one event selected or No event selected
        RecordSet changedRecords = inputRecords.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
        int size = changedRecords.getSize();
        if (size == 0 || size == 1) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.merge.selection.error");
            throw new ValidationException("Renewal events selection error.");
        }

        // prepare data
        Record refRecord = changedRecords.getRecord(0);
        String policyTypeCode = BatchRenewalFields.getPolicyTypeCode(refRecord);
        String eventFromDateStr = BatchRenewalFields.getEffectiveFromDate(refRecord);
        String eventToDateStr = BatchRenewalFields.getEffectiveToDate(refRecord);
        Date refEventFromDate = DateUtils.parseDate(eventFromDateStr);
        Date refEventToDate = DateUtils.parseDate(eventToDateStr);
        String policyTermTypeCode = BatchRenewalFields.getPolicyTermTypeCode(refRecord);

        for (int i = 0; i < size; i++) {
            Record rec = changedRecords.getRecord(i);
            String rowNum = String.valueOf(rec.getRecordNumber() + 1);
            String rowId = BatchRenewalFields.getRenewalEventId(rec);

            // Validation #3: Non Pre-Renewal Selected
            String processCode = BatchRenewalFields.getProcessCode(rec);
            if (!processCode.equals(BatchRenewalFields.ProcessCodeValues.PRERENEWAL)) {
                MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.merge.nonPrerenewal.error",
                    new String[]{rowNum}, "", rowId);
            }

            if (i > 0) {
                // Validation #2: Policy Type Mismatch
                if (!BatchRenewalFields.getPolicyTypeCode(rec).equals(policyTypeCode)) {
                    MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.merge.policyType.mismatch.error",
                        new String[]{rowNum}, "", rowId);
                }

                // Validation #4: Non Common Anniversary Date Mismatch
                String fromDateStr = BatchRenewalFields.getEffectiveFromDate(rec);
                String toDateStr = BatchRenewalFields.getEffectiveToDate(rec);
                Date fromDate = DateUtils.parseDate(fromDateStr);
                Date toDate = DateUtils.parseDate(toDateStr);
                if (policyTermTypeCode.equals(BatchRenewalFields.PolicyTermTypeCodeValues.NON_COMMON)) {
                    if (!refEventFromDate.equals(fromDate) || !refEventToDate.equals(toDate)) {
                        MessageManager.getInstance().addErrorMessage(
                            "pm.batchRenewalProcess.merge.nonCommon.anniversaryDate.mismatch.error", new String[]{rowNum}, "", rowId);
                    }
                }

                // Validation #5: Common Anniversary Date Mismatch
                if (policyTermTypeCode.equals(BatchRenewalFields.PolicyTermTypeCodeValues.COMMON)) {
                    // get common anniversary term dates
                    Record anniTermDatesRec = getBatchRenewalProcessDAO().getCommonAnniversaryTermDates(rec);
                    String anniEffFromDateStr = BatchRenewalFields.getEffectiveFromDate(anniTermDatesRec);
                    String anniEffToDateStr = BatchRenewalFields.getEffectiveToDate(anniTermDatesRec);
                    Date anniEffFromDate = DateUtils.parseDate(anniEffFromDateStr);
                    Date anniEffToDate = DateUtils.parseDate(anniEffToDateStr);

                    if (fromDate.before(anniEffFromDate) || toDate.after(anniEffToDate)) {
                        MessageManager.getInstance().addErrorMessage(
                            "pm.batchRenewalProcess.merge.common.anniversaryDate.mismatch.error", new String[]{rowNum}, "", rowId);
                    }
                }
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Merge Batch Renewal Events errors.");
        }

        l.exiting(getClass().getName(), "validateForMergeRenewalEvents");
    }

    /**
     * To validate for in progress events
     *
     * @param inputRecords
     */
    protected void validateForInProgressEvents(RecordSet inputRecords, String type) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForInProgressEvents", new Object[]{inputRecords, type});
        }

        RecordSet changedRecords = inputRecords.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));

        int size = changedRecords.getSize();
        for (int i = 0; i < size; i++) {
            Record rec = changedRecords.getRecord(i);
            Record inputRecord = new Record();
            BatchRenewalFields.setRenewalEventId(inputRecord, BatchRenewalFields.getRenewalEventId(rec));
            // Determine non other events are in progress for the given type
            validateInProgressEvent(inputRecord, type);
        }

        l.exiting(getClass().getName(), "validateForInProgressEvents");
    }

    /**
     * To release forms for a renewal event
     * @param inputRecord renewal events records
     */
    public void releaseOutput(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "releaseOutput", new Object[]{inputRecord});
        }

        try {
            getBatchRenewalProcessDAO().releaseOutput(inputRecord);
        }
        catch (AppException ae) {
            MessageManager.getInstance().addErrorMessage("pm.batchRenewalProcess.release.failed.error");
            throw ae;
        }
        MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.release.success.info");

        l.exiting(getClass().getName(), "releaseOutput");
    }

    /**
     * Checks if there is a batch that includes the current policy/term.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public boolean hasBatchIncludePolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasBatchIncludePolicy", new Object[]{inputRecord});
        }

        boolean result = false;
        try {
            result = getBatchRenewalProcessDAO().hasBatchIncludePolicy(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to call hasBatchIncludePolicy from DAO", e);
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

        Integer result = null;
        try {
            result = getBatchRenewalProcessDAO().getLatestBatchForPolicy(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to call getLatestBatchForPolicy from DAO", e);
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

        try {
            getBatchRenewalProcessDAO().addPolicyToBatch(inputRecord);
        }
        catch (Exception e) {
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

        try {
            getBatchRenewalProcessDAO().excludePolicyFromBatch(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to exclude policy from batch event", e);
            l.throwing(getClass().getName(), "excludePolicyFromBatch", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "excludePolicyFromBatch");
        }
    }

    /**
     * get current DAO
     * <p/>
     *
     * @return current DAO
     */
    public BatchRenewalProcessDAO getBatchRenewalProcessDAO() {
        return m_batchRenewalProcessDAO;
    }

    /**
     * set current DAO
     * <p/>
     *
     * @param batchRenewalProcessDAO batch renewal DAO
     */
    public void setBatchRenewalProcessDAO(BatchRenewalProcessDAO batchRenewalProcessDAO) {
        m_batchRenewalProcessDAO = batchRenewalProcessDAO;
    }

    /**
     * get security manager
     *
     * @return SecurityManager
     */
    public SecurityManager getSecurityManager() {
        return m_securityManager;
    }

    /**
     * set security manager
     *
     * @param securityManager security manager
     */
    public void setSecurityManager(SecurityManager securityManager) {
        m_securityManager = securityManager;
    }

    /**
     * verify config
     */
    public void verifyConfig() {
        if (getBatchRenewalProcessDAO() == null)
            throw new ConfigurationException("The required property 'batchRenewalProcessDAO' is missing.");
    }

    public BatchRenewalProcessManagerImpl() {
    }

    private BatchRenewalProcessDAO m_batchRenewalProcessDAO;
    private SecurityManager m_securityManager;
}
