package dti.pm.policymgr.specialhandlingmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for special handling.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 16, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface SpecialHandlingDAO {

    /**
     * Retrieves all special handling information.
     *
     * @param record input record
     * @return recordSet
     */
//    RecordSet loadAllSpecialHandlings(Record record);
    
    /**
     * Retrieves all special handling information.
     *
     * @param record input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */
    RecordSet loadAllSpecialHandlings(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Save all special handlings' information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    int saveAllSpecialHandlings(RecordSet inputRecords);

    /**
         * Find if configuration is there in pm_attribute to make it editable.
         *
         * @param inputRecord intput record
         * @return String a String indicating whether editable based on configuration.
         */
    String getEditableConfiguration(Record inputRecord);

}
