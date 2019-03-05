package dti.pm.policymgr.processacfmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for ACF.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface ProcessAcfDAO {

    /**
     * Retrieves all product.
     *
     * @param inputRecord         input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */
    public RecordSet loadAllProduct(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Retrieves all override.
     *
     * @param inputRecord         input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */

    public RecordSet loadAllOverride(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Retrieves all result.
     *
     * @param inputRecord input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */

    public RecordSet loadAllResult(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Retrieves all fee.
     *
     * @param inputRecord input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */

    public RecordSet loadAllFee(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Save all override records.
     *
     * @param inputRecords input RecordSet
     * @return int the number of rows saved.
     */
    public int saveAllOverride(RecordSet inputRecords);

    /**
     * Save all fee records.
     *
     * @param inputRecords input RecordSet
     * @return int the number of rows saved.
     */
    public int saveAllFee(RecordSet inputRecords);
}