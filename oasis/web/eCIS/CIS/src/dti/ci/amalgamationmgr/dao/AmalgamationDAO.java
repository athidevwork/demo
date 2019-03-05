package dti.ci.amalgamationmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for Amalgamation.
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
public interface AmalgamationDAO {

    /**
     * Method to load all amalgamation
     *
     * @param inputRecord         a record containing entity information
     * @param recordLoadProcessor
     * @return RecordSet resultset containing amalgamation information
     */
    public RecordSet loadAllAmalgamation(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method to save all amalgamation information
     *
     * @param inputRecords 
     * @return int
     */
    public int saveAllAmalgamation(RecordSet inputRecords);

}
