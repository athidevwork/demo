package dti.pm.transactionmgr.batchrenewalprocessmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.error.ValidationException;

import java.util.Date;


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
 * Sep 26, 2007          jshen     Add more methods for manage/issue/merge/print batch renewal events.
 * Sep 28, 2011          dzhang    123437 - Added deleteRenewalWipBatches and rerateRenewalPolicyBatches.
 * Oct 13, 2011          wfu       125309 - Merge getDbmsJobCount and getRenewalEventCount to getEventCount.
 * Jun 11, 2013          fcb       145501 - Added support for common anniversary batch renewals.
 * 04/08/2014            awu       156019 - Added releaseOutput.
 * 06/23/2016            tzeng     167531 - Added hasBatchIncludePolicy, getLatestBatchForPolicy, addPolicyToBatch.
 * ---------------------------------------------------
 */

public interface BatchRenewalProcessDAO {
    /**
     *
     * @param inputRecord it should include the following field:
     * @return the result which met the condition
     */
    public Record getDefaultDates(Record inputRecord);
    /**
     * save/update the data
     *
     * @param record batch renewal record set
     * @return number of the updated rows
     */
    public int createBatchRenewalProcess(Record record);

    /**
     * judge if progress is in progress
     * @param record records to judge
     * @throws ValidationException if is in progress
     */
    int isRenewalInProgress(Record record);

    /**
     * judge if records are valid for batch renewal
     * @param record records to judge
     * @return record with error code and error message if any
     * @throws ValidationException if records are invalid
     */
    Record validateRenewal(Record record);

    /**
     * To load all renewal event data.
     *
     * @param inputRecord          input record
     * @param recordLoadProcessor  an instance of the load processor to set page entitlements
     * @return
     */
    public RecordSet loadAllRenewalEvent(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * To load all renewal event detail data.
     *
     * @param inputRecord          input record
     * @param recordLoadProcessor  an instance of the load processor to set page entitlements
     * @return
     */
    public RecordSet loadAllRenewalDetail(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * To save all exclude policies.
     *
     * @param inputRecords  a set of excluded policy code Records for saving.
     * @return the number of rows updated.
     */
    public int saveAllExcludePolicy(RecordSet inputRecords);

    /**
     * To Issue the pre-renewal batches.
     *
     * @param inputRecord   input record
     * @return the number of rows updated.
     */
    public void saveIssueRenewalBatches(Record inputRecord);

    /**
     * To get renewal event and running jobs count for issue.
     *
     * @param inputRecord   input record with renewal event PK and type (issue or print).
     * @return the event count.
     */
    public int getEventCount(Record inputRecord);

    /**
     * To merge the renewal batches.
     *
     * @param inputRecord   input record with selected renewal event PK and numbers of them.
     * @return the number of rows updated.
     */
    public void saveMergeRenewalEvents(Record inputRecord);

    /**
     * To retrieve the common anniversary term dates.
     *
     * @param inputRecord   input record with renewal event's policy type code, effective date.
     * @return a record with the anniversary term dates.
     */
    public Record getCommonAnniversaryTermDates(Record inputRecord);

    /**
     * To submit forms for printing.
     *
     * @param inputRecord   input record with renewal event PK and type.
     * @return record with error code and message.
     */
    public Record saveSubmitPrintingJob(Record inputRecord);

    /**
     * To save print device.
     *
     * @param inputRecord   input record with renewal event PK and device code.
     */
    public void savePrintDevice(Record inputRecord);

    /**
     * To get existing printing job count.
     *
     * @param inputRecord   input record with renewal event PK.
     * @return the existing printing job count.
     */
    public int getPrintJobCount(Record inputRecord);

    /**
     * To delete the pre-renewal WIPs.
     *
     * @param inputRecords input records
     */
    public void deleteRenewalWipBatches(RecordSet inputRecords);

    /**
     * To rerate the renewal policies.
     *
     * @param inputRecords input records
     */
    public void rerateRenewalPolicyBatches(RecordSet inputRecords);

    /**
     * getCommonAnniversaryPolicyType data
     * <p/>
     *
     * @param inputRecord         input record
     * @return the list of available common policy types
     */
    public RecordSet getCommonAnniversaryPolicyType(Record inputRecord);

    /**
     * To retrieve the common anniversary term dates y term year.
     *
     * @param inputRecord input record with renewal event's policy type code, effective date.
     * @return a record set with the anniversary term dates.
     */
    public RecordSet getCommonAnniversaryYearTermDates(Record inputRecord);

    /**
     * To release forms for a renewal event.
     *
     * @param inputRecord   input record
     * @return the number of rows updated.
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
