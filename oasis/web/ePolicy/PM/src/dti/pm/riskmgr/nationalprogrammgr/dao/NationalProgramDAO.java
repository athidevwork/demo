package dti.pm.riskmgr.nationalprogrammgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for National Program information.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 25, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public interface NationalProgramDAO {

    /**
     * Returns a RecordSet loaded with list of available national programs for the provided risk.
     *
     * @param inputRecord   input records that contains key information
     * @param loadProcessor record load processor
     * @return RecordSet a RecordSet loaded with list of available national programs.
     */
    RecordSet loadAllNationalProgram(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Save all data in National Program page
     *
     * @param inputRecords a record set with data to be saved
     */
    void saveAllNationalProgram(RecordSet inputRecords);

    /**
     * To calculate risk dates for national program.
     * <p/>
     *
     * @param inputRecord input record that contains key information.
     * @return Record with risk effective from date and effective to date.
     */
    Record calculateRiskDatesForNationalProgram(Record inputRecord);

}