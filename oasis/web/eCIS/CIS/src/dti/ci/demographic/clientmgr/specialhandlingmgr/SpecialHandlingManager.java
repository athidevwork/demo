package dti.ci.demographic.clientmgr.specialhandlingmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;


/**
 * An interface to handle CRUD operation on Special Handling information.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 30, 2008
 *
 * @author
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/15/2010       Kenney      Issue#106087: Load initial values when adding special handling
 * ---------------------------------------------------
 */
public interface SpecialHandlingManager {

    /**
     * Retrieves all Special Handling's information
     *
     * @param entityFK
     * @return RecordSet
     */
    RecordSet loadSpecialHandlingsByEntity(long entityFK);

    /**
     * Saves all Special Handlings' information
     *
     * @param strUserId
     * @param inputRecords input records
     * @return int
     */
    int saveAllSpecialHandlings(String strUserId, RecordSet inputRecords);

    /**
     * method to get the initial value when adding special handling
     *
     * @param inputRecord
     * @return record
     */
    public Record getInitialValuesForAddSpecialHandling(Record inputRecord);    
}