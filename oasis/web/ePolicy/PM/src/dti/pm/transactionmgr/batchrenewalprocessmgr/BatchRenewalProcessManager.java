package dti.pm.transactionmgr.batchrenewalprocessmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LoadProcessor;

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
 * Sep 25, 2007      zlzhu         Created
 * Sep 28, 2011      dzhang        123437 - Added  deleteRenewalWipBatches and rerateRenewalPolicyBatches.
 * Jun 11, 2013      fcb           145501 - Added support for common anniversary batch renewals.
 * 08/04/2014            awu       156019 - Added  releaseOutput.
 * Aug 13, 2014      kxiang        156446 - Modified loadAllRenewalDetail to add parameter LoadProcessor.
 * 06/23/2016        tzeng         167531 - Added hasBatchIncludePolicy, getLatestBatchForPolicy, addPolicyToBatch,
 *                                          excludePolicyFromBatch.
 * ---------------------------------------------------
 */

public interface BatchRenewalProcessManager {
    /**
     * save all the batch renewal data
     * <p/>
     * @param record batch renewal data that needed to save
     */
    public int createBatchRenewalProcess(Record record);

    /**
     * load all batch renewal related data
     * <p/>
     * @return the result met the condition
     */
    public Record getInitialValuesForBatchRenew();

    /**
     * load all common anniversary batch renewal related data
     * <p/>
     * @return the result met the condition
     */
    public Record getInitialValuesForCommonAnniversaryBatchRenew();

    /**
     * To load all batch renewal event data.
     *
     * @param inputRecord   input record with search criteria.
     * @param loadProcessor record load processor
     * @return a record set met the condition.
     */
    public RecordSet loadAllRenewalEvent(Record inputRecord, LoadProcessor loadProcessor);

    /**
     * To load all batch renewal detail data.
     *
     * @param inputRecord   input record with renewal event PK.
     * @param loadProcessor record load processor
     * @return a record set met the condition.
     */
    public RecordSet loadAllRenewalDetail(Record inputRecord, LoadProcessor loadProcessor);

    /**
     * To save all excluded policy
     *
     * @param records RecordSet with excluded policy info
     * @return the number updated
     */
    public int saveAllExcludePolicy(RecordSet records);

    /**
     * To issue a pre-renewal batch event.
     *
     * @param inputRecord   input record with pre-renewal event info
     */
    public void saveIssueRenewalBatches(Record inputRecord);

    /**
     * To validate for capturing renewal event printer
     * 
     * @param inputRecord   input record with renewal batch print info
     */
    public void validateForCaptureRenewalBatchPrinter(Record inputRecord);

    /**
     * To submit the print job.
     *
     * @param inputRecord    input record with print device code and renewal event id.
     */
    public void saveSubmitPrintingJob(Record inputRecord);

    /**
     * To merge renewal events.
     *
     * @param inputRecords renewal events records for merge
     */
    public void saveMergeRenewalEvents(RecordSet inputRecords);

    /**
     * To delete renewal WIPs.
     *
     * @param inputRecords renewal events records
     */
    public void deleteRenewalWipBatches(RecordSet inputRecords);

    /**
     * To Rerate renewal policies.
     *
     * @param inputRecords renewal events records
     */
    public void rerateRenewalPolicyBatches(RecordSet inputRecords);

    /**
     * Save all common anniversary batch renewal data
     * <p/>
     *
     * @param inputRecord the batch renewal needed to save
     */
    public int createCommonAnniversaryBatchRenewalProcess(Record inputRecord);

     /**
     * To release forms for a renewal event.
     *
     * @param inputRecord renewal events records
     */
    public void releaseOutput(Record inputRecord);

    /**
     * Checks if there is a batch that includes the current policy/term.
     *
     * @param inputRecord
     * @return
     */
    public boolean hasBatchIncludePolicy(Record inputRecord);

    /**
     * Get the latest qualified batch which meet the new policy criteria and timing schedule.
     * @param inputRecord
     * @return
     */
    public Integer getLatestBatchForPolicy(Record inputRecord);

    /**
     * Add policy to batch renewal event.
     * @param inputRecord
     */
    public void addPolicyToBatch(Record inputRecord);

    /**
     * Exclude policy from batch renewal event.
     * @param inputRecord
     */
    public void excludePolicyFromBatch(Record inputRecord);
}
