package dti.pm.policymgr.limitsharingmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of Limit Sharing Manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 10, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/12/2011       dzhang     113568 - Added loadAllSharedLimit().
 * ---------------------------------------------------
 */

public interface LimitSharingManager {

    /**
     * Returns a RecordSet loaded with list of available shared group for the provided
     * policy information.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available shared groups.
     */
    public RecordSet loadAllSharedGroup(PolicyHeader policyHeader);

    /**
     * load all shared detail info by shared group set
     *
     * @param policyHeader
     * @return RecordSet   a record set loaded with list of shared details
     */
    public RecordSet loadAllSharedDetail(PolicyHeader policyHeader);

    /**
     * load all separate limit info by policy infomation
     *
     * @param policyHeader
     * @return RecordSet   a record set loaded with list of separate limit
     */
    public RecordSet loadAllSeparateLimit(PolicyHeader policyHeader);

    /**
     * save all share group and share detail info
     *
     * @param policyHeader        the summary policy information corresponding to the provided coverages.
     * @param inputRecords        shared group records
     * @param sharedDetailRecords shared detail records
     * @return the number of rows updated.
     */
    public int saveAllLimitSharing(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet sharedDetailRecords);


    /**
     * get Initial value of shared group
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForSharedGroup(PolicyHeader policyHeader, Record inputRecord);

    /**
     * get Initial value of shared group
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForSharedDetail(PolicyHeader policyHeader, Record inputRecord);

    /**
     * load all available shared detail
     *
     * @param policyHeader
     * @param inputRecord
     * @return recordSet
     */
    public RecordSet loadAllAvailableSharedDetail(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate sir column is visible or not.
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean validateSirVisibility(PolicyHeader policyHeader);

    /**
     * load all available shared limit detail
     *
     * @param policyHeader
     * @param inputRecord
     * @return recordSet
     */
    public RecordSet loadAllSharedLimit(PolicyHeader policyHeader, Record inputRecord);
}
