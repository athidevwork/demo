package dti.pm.policymgr.underlyingpolicymgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for underlying policy information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 3, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/28/2016       ssheng      178365 - Add method validateUnderlyingOverlap.
 * 09/09/2016       xnie        178813 - Added validateSameOffVersionExists().
 * ---------------------------------------------------
 */
public interface UnderlyingPolicyDAO {
    /**
     * load all underlying policy
     * @param inputRecord input record cotains all required parameters
     * @param  recordLoadProcessor record load processor
     * @return result recordset
     */
    RecordSet loadAllUnderlyingPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * load all retro date
     * @param inputRecord input record cotains all required parameters
     * @return result recordset contains retroDate column
     */
    RecordSet loadAllRetroDate(Record inputRecord);

    /**
     * get initial values for underlying policy
     * @param inputRecord input record
     * @return record contains all initial values
     */
    Record getInitialValuesForUnderlyingPolicy(Record inputRecord);

    /**
     * insert or update underlying policy infos
     * @param inputRecords recordset contains inserted/modified records
     * @return processed record count
     */
    int saveAllUnderlyingPolicy(RecordSet inputRecords);

    /**
     * delete underlying policy infos
     * @param inputRecords recordset contains deleted records
     * @return processed record count
     */
    int deleteAllUnderlyingPolicy(RecordSet inputRecords);

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
     * Check if any underlying policy version which is from same official record and in same time period exists.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    String validateSameOffVersionExists(Record inputRecord);
}
