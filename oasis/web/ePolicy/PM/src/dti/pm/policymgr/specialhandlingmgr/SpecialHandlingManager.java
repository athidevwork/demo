package dti.pm.policymgr.specialhandlingmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * An interface to handle CRUD operation on the Special Handling information.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 15, 2007
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

public interface SpecialHandlingManager {

    /**
     * Retrieves all Special Handlings' information for one policy
     *
     * @param policyHeader policy header
     * @return RecordSet
     */
    RecordSet loadAllSpecialHandlings(PolicyHeader policyHeader);

    /**
     * Save all Special Handlings' information
     *
     * @param policyHeader policy header
     * @param inputRecords a set of Records, each with the updated special handling info
     * @return the number of rows updated 
     */
    int saveAllSpecialHandlings(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * To get initial values for a new special handling record
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForSpecialHandling(PolicyHeader policyHeader, Record inputRecord);
}
