package dti.pm.coveragemgr.underlyingmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for underlying coverage information.
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 12, 2008
 *
 * @author wrong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/24/2018       wrong      188391 - Initial version.
 * ---------------------------------------------------
 */
public interface UnderlyingCoverageDAO {
    /**
     * load all underlying coverage
     * @param inputRecord input record cotains all required parameters
     * @param  recordLoadProcessor record load processor
     * @return result recordset
     */
    RecordSet loadAllUnderlyingCoverage(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * get initial values for underlying coverage
     * @param inputRecord input record
     * @return record contains all initial values
     */
    Record getInitialValuesForUnderlyingCoverage(Record inputRecord);

    /**
     * insert or update underlying policy coverage
     * @param inputRecords recordset contains inserted/modified records
     * @return processed record count
     */
    int saveAllUnderlyingCoverage(RecordSet inputRecords);

    /**
     * delete underlying policy coverage
     * @param inputRecords recordset contains deleted records
     * @return processed record count
     */
    int deleteAllUnderlyingCoverage(RecordSet inputRecords);

    /**
     * update underlying policy coverage
     * @param inputRecords recordset contains updated records
     * @return processed record count
     */
    int updateAllUnderlyingCoverage(RecordSet inputRecords);

    /**
     * load all active policy for select
     * @param inputRecord input record
     * @return recordset of active policies
     */
    RecordSet loadAllActivePolicy(Record inputRecord);

    /**
     * Overlap validation.
     *
     * @param inputRecord input record
     */
    String validateUnderlyingOverlap(Record inputRecord);

    /**
     * load all active related coverage for select
     * @param inputRecord input record
     * @return recordset of active related coverages
     */
    RecordSet loadAvailableRelatedCoverage(Record inputRecord);

    /**
     * Check if any underlying coverage version which is from same official record and in same time period exists.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    String validateSameOffVersionExists(Record inputRecord);
}
