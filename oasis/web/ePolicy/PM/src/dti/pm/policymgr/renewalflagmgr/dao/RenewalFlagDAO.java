package dti.pm.policymgr.renewalflagmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/16/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/24/2015       tzeng       Initial version.
 * ---------------------------------------------------
 */

public interface RenewalFlagDAO {
    /**
     * Load all renewal flags.
     * @param inputRecord input record contains all required parameters
     * @param recordLoadProcessor record load processor
     * @return recordSet a record set loaded with list of available renewal flags
     */
    RecordSet loadAllRenewalFlag(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Save inserted/modified data for renewal flag page.
     * @param inputRecords a record set with data to be inserted/updated
     * @return processed record count
     */
    int saveAllRenewalFlag(RecordSet inputRecords);

    /**
     * Delete data for renewal flag page.
     * @param inputRecords recordset contains deleted records
     * @return processed record count
     */
    int deleteAllRenewalFlag(RecordSet inputRecords);

}
