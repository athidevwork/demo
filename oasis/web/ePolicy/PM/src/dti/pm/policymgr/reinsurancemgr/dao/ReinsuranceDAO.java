package dti.pm.policymgr.reinsurancemgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.pm.policymgr.reinsurancemgr.impl.ReinsuranceEntitlementRecordLoadProcessor;

/**
 * An interface that provides DAO operation for reinsurance.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 30, 2007
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

public interface ReinsuranceDAO {

    /**
     * Retrieves all reinsurance information.
     *
     * @param record input record
     * @param recordLoadProcessor loadProcessor
     * @return recordSet
     */
    RecordSet loadAllReinsurance(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Save all reinsurance information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    int saveAllReinsurance(RecordSet inputRecords);

     /**
     * Delete all reinsurance information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    int deleteAllReinsurance(RecordSet inputRecords);
}
