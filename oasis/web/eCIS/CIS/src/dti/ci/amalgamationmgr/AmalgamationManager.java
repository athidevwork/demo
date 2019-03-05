package dti.ci.amalgamationmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Implementation of AmalgamationManager.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface AmalgamationManager {
    /**
     * Method to load all amalgamation
     *
     * @param inputRecord a record containing input information
     * @return RecordSet resultset containing amalgamation information
     */
    public RecordSet loadAllAmalgamation(Record inputRecord);

    /**
     * Method to save all amalgamation information
     *
     * @param inputRecords
     * @return int
     */
    public int saveAllAmalgamation(RecordSet inputRecords);

    /**
     * Get initial values for adding amalgamation.
     *
     * @return Record
     */
    public Record getInitialValuesForAmalgamation();
}
