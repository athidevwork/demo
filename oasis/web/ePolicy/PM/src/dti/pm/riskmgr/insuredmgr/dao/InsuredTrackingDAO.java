package dti.pm.riskmgr.insuredmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2015 Delphi Technology, inc. (dti)</p>
 * Date:   April 08, 2015
 *
 * @author wdang
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 04/08/2015    wdang      157211 - Initial version, Maintain Insured Tracking Information.
 * ---------------------------------------------------
 */
public interface InsuredTrackingDAO {
    
    /**
     * Returns a RecordSet loaded with list of available insured tracking for the provided risk.
     *
     * @param inputRecord   input records that contains key information
     * @param loadProcessor record load processor
     * @return              RecordSet a RecordSet loaded with list of available insured tracking.
     */
    RecordSet loadAllInsuredTracking(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Insert data in insured tracking page
     *
     * @param inputRecords a record set with data to be inserted.
     */
    void insertAllInsuredTracking(RecordSet inputRecords);
    
    /**
     * Update data in insured tracking page
     *
     * @param inputRecords a record set with data to be updated.
     * 
     */
    void updateAllInsuredTracking(RecordSet inputRecords);
    
    /**
     * Delete data in insured tracking page
     *
     * @param inputRecords a record set with data to be deleted.
     */
    void deleteAllInsuredTracking(RecordSet inputRecords);

    /**
     * Validate insured tracking records, return the result.
     *
     * @param inputRecord   input records that contains key information
     * @return                Out parameters record.
     */
    public Record validateAllInsuredTracking(Record inputRecord);
}
