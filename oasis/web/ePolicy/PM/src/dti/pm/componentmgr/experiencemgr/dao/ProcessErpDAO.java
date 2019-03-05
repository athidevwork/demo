package dti.pm.componentmgr.experiencemgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for experience rating programs.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 1, 2011
 *
 * @author ryzhao
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface ProcessErpDAO {

    /**
     * Load all ERP data
     *
     * @param inputRecord         with user entered search criteria
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet
     */
    public RecordSet loadAllErp(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * If the process ERP page is accessed from policy/risk page, call this method to process ERP.
     *
     * @param inputRecord
     * @return Record
     */
    public Record processErp(Record inputRecord);

    /**
     * If the process ERP page is accessed from main menu, call this method to process ERP.
     *
     * @param inputRecord
     * @return Record
     */
    public Record processErpBatch(Record inputRecord);

    /**
     * Save all updated ERP
     *
     * @param inputRecords
     * @return the number of rows updated
     */
    public int saveAllErp(RecordSet inputRecords);

    /**
     * Delete a ERP batch
     *
     * @param inputRecord
     * @return Record
     */
    public Record deleteErpBatch(Record inputRecord);

    /**
     * Display the policies that have errors when deleting an ERP batch.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllErrorPolicy(Record inputRecord);
}
