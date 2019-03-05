package dti.pm.schedulemgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for schedule information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface ScheduleDAO {
  /**
     * Returns a RecordSet loaded with list of available schedules for the provided
     * policy/risk information.
     * <p/>
     * @param inputRecord record with risk/coverage fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available sechedules for risk/coverage.
     */
    RecordSet loadAllSchedules(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

     /**
     * insert all schedules' information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    int addAllSchedules(RecordSet inputRecords);

    /**
     * Update all schedules' information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    int updateAllSchedules(RecordSet inputRecords);

     /**
     * delete all schedules' information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    int deleteAllSchedules(RecordSet inputRecords);

    /**
     * Method to copy schedule to target risk 
     * @param inputRecord
     * @return
     */
    void copyAllSchedule(Record inputRecord);
}

