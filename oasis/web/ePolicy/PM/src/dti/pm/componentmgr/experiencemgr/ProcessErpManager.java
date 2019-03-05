package dti.pm.componentmgr.experiencemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle process Experience Rating Programs Manager
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
public interface ProcessErpManager {

    /**
     * Get search criteria default values
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getDefaultValuesForSearchCriteria(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Load all ERP data
     *
     * @param policyHeader
     * @param inputRecord  with user entered search criteria
     * @return RecordSet
     */
    public RecordSet loadAllErp(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Process ERP
     *
     * @param inputRecord
     * @return Record
     */
    public Record processErp(Record inputRecord);

    /**
     * Save all updated ERP
     *
     * @param inputRecords
     * @return the number of rows updated
     */
    public int saveAllErp(RecordSet inputRecords);

    /**
     * Delete ERP batch
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
