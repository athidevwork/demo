package dti.pm.policymgr.lockmgr.dao;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.pm.policymgr.PolicyIdentifier;

/**
 * An interface to provide DAO operation for policy locking.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 17, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/18/2010       bhong       113819 -  Added unlockOfficialPolicy method
 * 01/26/2015       fcb         159897 -  Added refreshPolicyLock.
 * 04/21/2016       eyin        171030 -  Overloading function unLockWIP() with additional parameter reinitializeWIPNumB.
 * ---------------------------------------------------
 */

public interface LockDAO {
    /**
     * Method that returns a boolean value that indicates whether a lock is obtained successfully for a policy that has
     * an in-progress transaction.
     * <p/>
     *
     * @param policyIdentifier an instance of Policy Identifier with all information loaded.
     * @param policyLockDuration the duration of the lock
     * @return boolean true, if a lock is obtained successfully; otherwise, false.
     */
    boolean lockWIP(String policyNo, PolicyIdentifier policyIdentifier, String policyLockDuration, String lockContext);

    /**
     * Method that returns a boolean value that indicates whether the lock held on the policy has been release successfully.
     * <p/>
     *
     * @param policyIdentifier an instance of Policy Identifier with all information loaded.
     * @param wipB an instance of YesNoFlag indicating the resultant wip status of the policy*
     * @return true, if the lock has been release successfully;otherwise, false.
     */
    boolean unLockWIP(PolicyIdentifier policyIdentifier, YesNoFlag wipB, String unLockWIP);

    /**
     * Determine if an policy can be locked at the moment when it is called.
     *
     * @param policyIdentifier Record containing current policy, term, and transaction information
     * @return boolean: can this policy be locked by this policyIdentifier?
     */
    public boolean canLockPolicy(PolicyIdentifier policyIdentifier);

    /**
     * load all locked policy
     * @param inputRecord
     * @param recordLoadProcessor an instance of record load processor
     * @return list of locked policy
     */
    public RecordSet loadAllLockedPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * unlock policy with policyId and wipB fields
     * @param inputRecord
     * @return unlock result and message returned by stored procedure
     */
    public Record unlockPolicy(Record inputRecord);

    /** unlock official policy
     *
     * @param inputRecord
     */
    public void unlockOfficialPolicy(Record inputRecord);

    /**
     *
     * @param inputRecord
     * @return unlock result and message returned by stored procedure
     */
    public Record refreshPolicyLock(Record inputRecord);

    /**
     * Method that returns a boolean value that indicates whether the lock held on the policy has been release successfully.
     * <p/>
     *
     * @param policyIdentifier an instance of Policy Identifier with all information loaded.
     * @param wipB an instance of YesNoFlag indicating the resultant wip status of the policy*
     * @param reinitializeWIPNumB an instance of YesNoFlag indicating if need to reinitialize policy WIP Num
     * @return true, if the lock has been release successfully;otherwise, false.
     */
    boolean unLockWIP(PolicyIdentifier policyIdentifier, YesNoFlag wipB, String unLockWIP, YesNoFlag reinitializeWIPNumB);
}
