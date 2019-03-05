package dti.pm.policymgr.lockmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.cs.lockmgr.BaseLockManager;

/**
  * An interface to handle implementation of all policy related locking.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 17, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/19/2007       sxm         Removed policyIdentifier from generateLockId
 * 05/12/2010       fcb         107461: unLockPolicy made public.
 * 11/18/2010       bhong       113819 -  Added unlockOfficialPolicy method
 * 06/01/2011       ryzhao      103808 - Added BEAN_NAME
 * 01/26/2015       fcb         159897 - Added refreshPolicyLock
 * 04/21/2016       eyin        171030 - Added unlockWIPReinitialize().
 * ---------------------------------------------------
 */

public interface LockManager extends BaseLockManager {

    public static final String BEAN_NAME = "LockManager";

    /**
     *
     * @param policyHeader   Policy Header that contains policyIdentifier for calling PolicyDAO.lockWIP()
     * @return boolean true, if a lock is obtained successfully; otherwise, false.
     */
    boolean lockPolicy(PolicyHeader policyHeader, String lockContext);

    /**
     * Method that returns a boolean value that indicates whether the lock held on the policy has been release successfully.
     * <p/>
     *
     * @param policyHeader   Policy Header that contains policyIdentifier
     * @return true, if the lock has been release successfully;otherwise, false.
     */
    public boolean unLockPolicy(PolicyHeader policyHeader, String lockContext) ;

    /**
     * Method that returns a boolean value that indicates whether the lock held on the policy has been
     * released successfully along with setting the WIP_B indicator to 'N'.
     * <p/>
     *
     * @param policyHeader   Policy Header that contains policyIdentifier
     * @param wipB           String value defining what the WIP indicator shall be set to during unlock
     * @return true, if the lock has been release successfully;otherwise, false.
     */
    public boolean unLockPolicy(PolicyHeader policyHeader, YesNoFlag wipB, String lockContext);


    /**  Method to return if the policy represented by policyHeader is lockable again (by current process)
     *  this method does not lock the policy.
     * @param policyHeader
     * @return boolean true or false to indicate if the policy is lockable at the moment when the method is invoked
     */
    boolean canLockPolicy(PolicyHeader policyHeader) ;

    /**
     * Method that is used to remove a lock when changing to view another policy or quote.
     *
     * @return Boolean           True/False indicator if lock was successfully released.
     */
    boolean unLockPreviouslyHeldLock();

    /**
     * load all locked policy
     * @param inputRecord
     * @param lp load processor passed from action method
     * @return list of locked policy
     */
    public RecordSet loadAllLockedPolicy(Record inputRecord, RecordLoadProcessor lp);

    /**
     * unlock all locked policies, add unlock result and info on every record
     * @param inputRecords
     */
    public void unlockAllPolicy(RecordSet inputRecords);

    /**
     * Unlock official policy and update wip_b to N
     * @param policyId
     */
    public void unlockOfficialPolicy(String policyId);

    /**
     * @param policyHeader
     */
    public boolean refreshPolicyLock(PolicyHeader policyHeader, String lockContext);

    /**
     * Method that returns a boolean value that indicates whether the lock held on the policy has been
     * released successfully along with setting the WIP_B indicator to 'N'. This method will not update policy official number.
     * <p/>
     *
     * @param policyHeader   Policy Header that contains policyIdentifier
     * @param wipB           String value defining what the WIP indicator shall be set to during unlock
     * @return true, if the lock has been release successfully;otherwise, false.
     */
    public boolean unlockWIPReinitialize(PolicyHeader policyHeader, YesNoFlag wipB, String lockContext);
}
