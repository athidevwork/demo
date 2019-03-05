package dti.pm.policymgr.limitsharingmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for limit sharing.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 20, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/17/2010       fcb         97217  - validateSharedLimitGroup added.
 * 01/12/2011       dzhang      113568 - loadAllSharedLimit added.
 * ---------------------------------------------------
 */

public interface LimitSharingDAO {

    /**
     * load all shared group by policy_id, current endorsement quote id, term_effective_from_date,
     * term_effective_to_date,record mode, transactionLogId, current term base record id.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet share groups
     */
    RecordSet loadAllSharedGroup(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * oad all shared group detail by policy_id, current endorsement quote id, term_effective_from_date,
     * term_effective_to_date,record mode, transactionLogId.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet share group details
     */
    RecordSet loadAllSharedGroupDetail(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * load all separate limit by policy_id, current endorsement quote id, term_effective_from_date,
     * term_effective_to_date,record mode, transactionLogId, current term base record id.
     *
     * @param inputRecord
     * @return RecordSet separate limits
     */
    RecordSet loadAllSeparateLimit(Record inputRecord);

    /**
     * load available shared detail list
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */

    RecordSet loadAllAvailableSharedDetail(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * delete all shared detail
     *
     * @param inputRecords
     * @return updateCount
     */
    int deleteAllSharedDetail(RecordSet inputRecords);

    /**
     * save all shared detail
     *
     * @param inputRecords
     * @return updateCount
     */
    int saveAllSharedDetail(RecordSet inputRecords);

    /**
     * delete all shared group
     *
     * @param inputRecords
     * @return updateCount
     */
    int deleteAllSharedGroup(RecordSet inputRecords);

    /**
     * save all shared group
     *
     * @param inputRecords
     * @return updateCount
     */
    int saveAllSharedGroup(RecordSet inputRecords);

    /**
     * Get validation info for Deductive
     *
     * @param inputRecord
     * @return transactionId
     */
    String validateShareGroupDeduct(Record inputRecord);

    /**
     * Get validation info for SIR
     *
     * @param inputRecord
     * @return transactionId
     */
    String validateShareGroupSir(Record inputRecord);

    /**
     * validate sir is visible or not.
     *
     * @param inputRecord (policyId,currentTransactionEffFromDate)
     * @return String (Y or N)
     */
    String validateSirVisibility(Record inputRecord);

    /**
     * Generic validation for Shared Limit.
     *
     * @param inputRecord (sharedGroupId,sourceId,policyId,transactionId)
     * @return String (Y or N)
     */
    String validateSharedLimitGroup(Record inputRecord);

    /**
     * load available shared limit detail list
     *
     * @param inputRecord
     * @return recordSet
     */

    RecordSet loadAllSharedLimit(Record inputRecord);

}
