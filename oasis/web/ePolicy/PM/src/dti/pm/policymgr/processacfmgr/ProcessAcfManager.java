package dti.pm.policymgr.processacfmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of ProcessAcf Manager.
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
public interface ProcessAcfManager {

    /**
     * Retrieves all product.
     *
     * @param inputRecord input record
     * @return recordSet
     */
    public RecordSet loadAllProduct(Record inputRecord);

    /**
     * Retrieves all override.
     *
     * @param inputRecord input record
     * @param policyHeader
     * @return recordSet
     */

    public RecordSet loadAllOverride(Record inputRecord, PolicyHeader policyHeader);

    /**
     * Retrieves all result.
     *
     * @param inputRecord input record
     * @return recordSet
     */

    public RecordSet loadAllResult(Record inputRecord);

    /**
     * Retrieves all fee.
     *
     * @param inputRecord input record
     * @return recordSet
     */

    public RecordSet loadAllFee(Record inputRecord, PolicyHeader policyHeader);

    /**
     * Save all override and fee records.
     *
     * @param inputRecordSets override and fee RecordSet
     * @param policyHeader 
     * @return int the number of rows saved.
     */
    public int saveAllAcf(RecordSet[] inputRecordSets, PolicyHeader policyHeader);

    /**
     * Get initial values for override.
     *
     * @param inputRecord
     * @param policyHeader
     * @return Record
     */
    public Record getInitialValuesForOverride(Record inputRecord, PolicyHeader policyHeader);

    /**
     * Get initial values for fee.
     *
     * @param inputRecord
     * @param policyHeader
     * @return Record
     */
    public Record getInitialValuesForFee(Record inputRecord, PolicyHeader policyHeader);
}