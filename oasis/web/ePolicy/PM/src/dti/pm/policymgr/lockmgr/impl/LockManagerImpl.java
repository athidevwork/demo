package dti.pm.policymgr.lockmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyIdentifier;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.policymgr.lockmgr.dao.LockDAO;
import dti.cs.lockmgr.impl.BaseLockManagerImpl;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
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
 * 2/27/2009        gjl         Refactored code to use BaseLockManagerImpl: 88739
 * 05/12/2010       fcb         107461: unlockPolicy() added.
 * 11/18/2010       bhong       113819 -  Added unlockOfficialPolicy method
 * 12/17/2014       fcb         149906 - unlockPolicy removed.
 * 01/26/2015       fcb         159897 - added refreshPolicyLock
 * 04/21/2016       eyin        171030 - Added unlockWIPReinitialize().
 * ---------------------------------------------------
 */

public class LockManagerImpl extends BaseLockManagerImpl implements LockManager {

    /**
     * Method that read the application configuration to get the lock duration
     *
     * @return String           lock duration
     */
    public String getLockDuration() {
        Logger l = LogUtils.enterLog(getClass(), "getLockDuration");

        String policyLockDuration = ApplicationContext.getInstance().getProperty(PROPERTY_LOCK_DURATION, "0");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLockDuration", policyLockDuration);
        }

        return policyLockDuration;
    }


    /**
     * @param policyHeader Policy Header that contains policyIdentifier
     * @return boolean       true, if a lock is obtained successfully; otherwise, false.
     */
    public boolean lockPolicy(PolicyHeader policyHeader, String lockContext) {
        PolicyIdentifier pi = policyHeader.getPolicyIdentifier();
        if (StringUtils.isBlank(pi.getPolicyLockId())) {
            String lockId = generateLockId(policyHeader.getPolicyNo());
            pi.setPolicyLockId(lockId);
        }
        return getLockDAO().lockWIP(policyHeader.getPolicyNo(), policyHeader.getPolicyIdentifier(),
            getLockDuration(), lockContext);
    }

    /**
     * Method that returns a boolean value that indicates whether the lock held on the policy has been release successfully.
     * <p/>
     *
     * @param policyHeader Policy Header that contains policyIdentifier
     * @return true, if the lock has been release successfully;otherwise, false.
     */
    public boolean unLockPolicy(PolicyHeader policyHeader, String lockContext) {
        return unLockPolicy(policyHeader, YesNoFlag.Y, lockContext);
    }

    /**
     * Method that returns a boolean value that indicates whether the lock held on the policy has been
     * released successfully along with setting the WIP_B indicator to 'N'.
     * <p/>
     *
     * @param policyHeader Policy Header that contains policyIdentifier
     * @param wipB         String value defining what the WIP indicator shall be set to during unlock
     * @return true, if the lock has been release successfully;otherwise, false.
     */
    public boolean unLockPolicy(PolicyHeader policyHeader, YesNoFlag wipB, String lockContext) {
        return getLockDAO().unLockWIP(policyHeader.getPolicyIdentifier(), wipB, lockContext);
    }

    /**
     * Method that returns a boolean value that indicates whether the lock held on the policy has been
     * released successfully along with setting the WIP_B indicator to 'N'. This method will not update policy official number.
     * <p/>
     *
     * @param policyHeader Policy Header that contains policyIdentifier
     * @param wipB         String value defining what the WIP indicator shall be set to during unlock
     * @return true, if the lock has been release successfully;otherwise, false.
     */
    public boolean unlockWIPReinitialize(PolicyHeader policyHeader, YesNoFlag wipB, String lockContext) {
        return getLockDAO().unLockWIP(policyHeader.getPolicyIdentifier(), wipB, lockContext, YesNoFlag.Y);
    }

    /**
     * Method to return if the policy represented by policyHeader is lockable again (by current process)
     * this method does not lock the policy.
     *
     * @param policyHeader
     * @return true or false to indicate if the policy is lockable at the moment when the method is invoked
     */
    public boolean canLockPolicy(PolicyHeader policyHeader) {
        return getLockDAO().canLockPolicy(policyHeader.getPolicyIdentifier());
    }

    /**
     * Method that is used to remove a lock when changing to view another policy or quote.
     *
     * @return Boolean           True/False indicator if lock was successfully released.
     */
    public boolean unLockPreviouslyHeldLock() {
        Logger l = LogUtils.enterLog(getClass(), "unLockPreviouslyHeldLock");
        boolean isSuccessfullyUnlocked = false;
        if (getUserSessionManager().getUserSession().has(UserSessionIds.POLICY_HEADER)) {
            // There is already a Policy Header cached in the UserSession.
            l.logp(Level.FINE, getClass().getName(), "unLockPreviouslyHeldLock", "Loading Policy Header From Session.");
            PolicyHeader policyHeader = (PolicyHeader) getUserSessionManager().getUserSession().get(UserSessionIds.POLICY_HEADER);
            if (policyHeader.isWipB()) {
                if (!policyHeader.getPolicyIdentifier().ownLock()) {
                    //Let this go, since the user tried to view the wip version of a policy, which is currently locked by another user.
                    //In this scenario, we must pretend that the unlock is successfull as the user never had the lock at first place.
                    //This way, the user can navigate to another polcy.
                    isSuccessfullyUnlocked = true;
                    l.logp(Level.FINE, getClass().getName(), "unLockPreviouslyHeldLock", "Cannot unlock the requested policy. Reason: There is no lock held on the policy.");
                }
                else {
                    isSuccessfullyUnlocked = unLockPolicy(policyHeader, "unLockPreviouslyHeldLock, Policy Header has wipB=true, and owns lock");
                }
            }
            else {
                // Policy was view at official state - no lock was obtained.
                isSuccessfullyUnlocked = true;
            }
        }
        else {
            //There is nothing to unlock. eg. User Logged in and clicked on home page.
            isSuccessfullyUnlocked = true;
        }
        l.exiting(getClass().getName(), "unLockPreviouslyHeldLock", String.valueOf(isSuccessfullyUnlocked));
        return isSuccessfullyUnlocked;
    }

    /**
     * load all locked policy
     *
     * @param inputRecord
     * @param lp load processor passed from action method
     * @return list of locked policy
     */
    public RecordSet loadAllLockedPolicy(Record inputRecord, RecordLoadProcessor lp) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllLockedPolicy", new Object[]{inputRecord});
        }
        RecordSet rs = null;
        //check if need to load data from database
        if (inputRecord.hasField("noLoadData") && inputRecord.getBooleanValue("noLoadData").booleanValue()) {
            //construct empty recordset
            rs = new RecordSet();
            List nameList = new ArrayList();
            nameList.add("policyNo");
            nameList.add("policyHolder");
            nameList.add("rmsg");
            rs.addFieldNameCollection(nameList);
        }
        else {
            rs = getLockDAO().loadAllLockedPolicy(inputRecord, lp);
        }


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllLockedPolicy", rs);
        }
        return rs;
    }

    /**
     * unlock all locked policies, add unlock result and info on every record
     *
     * @param inputRecords
     */
    public void unlockAllPolicy(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "unlockAllPolicy", new Object[]{inputRecords});
        }

        RecordSet selectedRecords = inputRecords.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
        //validate unlock all policies
        validateUnlockAllPolicy(selectedRecords);

        int successCount = 0;
        int failureCount = 0;
        Iterator selRecIter = selectedRecords.getRecords();
        while (selRecIter.hasNext()) {
            Record selRec = (Record) selRecIter.next();
            Record unlockResult = getLockDAO().unlockPolicy(selRec);
            selRec.setFields(unlockResult, true);
            //reset "SELECT_IND" 
            selRec.setFieldValue(RequestIds.SELECT_IND, "0");
            //set unlock sucessfully message, if the returned code is zero
            if (unlockResult.getIntegerValue("rc").intValue() == 0) {
                successCount++;
            }
            else {
                failureCount++;
            }
            selRec.setFieldValue("rmsg",
                MessageManager.getInstance().formatMessage("pm.maitainUnlockPolicy.unlockSucess"));
        }

        //add unlock result message to MessageManager
        MessageManager.getInstance().addInfoMessage("pm.maitainUnlockPolicy.unlockResult",
            new String[]{String.valueOf(successCount), String.valueOf(failureCount)});

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "unlockAllPolicy", inputRecords);
        }
    }

    /**
     * Refreshes a policy lock.
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean refreshPolicyLock(PolicyHeader policyHeader, String lockContext) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "refreshPolicyLock", new Object[]{policyHeader});
        }
        boolean policyLockRefreshed = false;

        Record record = new Record();
        record.setFieldValue("policyId", policyHeader.getPolicyId());
        record.setFieldValue("webSessionId", policyHeader.getPolicyLockId());
        record.setFieldValue("lockDuration", getLockDuration());
        record.setFieldValue("lockContext", lockContext);

        try {
            Record refreshResult = getLockDAO().refreshPolicyLock(record);
            if (refreshResult.hasField("returnvalue") && "0".equals(refreshResult.getFieldValue("returnvalue"))) {
                policyLockRefreshed = true;
            }
        }
        catch (Exception e) {
            policyLockRefreshed = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "refreshPolicyLock", policyHeader);
        }

        return policyLockRefreshed;
    }

    /**
     * validate unlock all policies
     *
     * @param selectedRecords
     */
    protected void validateUnlockAllPolicy(RecordSet selectedRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateUnlockAllPolicy", new Object[]{selectedRecords});
        }

        if (selectedRecords.getSize() == 0) {
            MessageManager.getInstance().addErrorMessage("pm.maitainUnlockPolicy.noPolicySelected.error");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateUnlockAllPolicy",
                String.valueOf(MessageManager.getInstance().hasErrorMessages()));
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("validate unlock all policies failed");
        }
    }

    /**
     * Unlock official policy and update wip_b to N
     *
     * @param policyId
     */
    public void unlockOfficialPolicy(String policyId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "unlockOfficialPolicy", new Object[]{policyId,});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyId", policyId);
        getLockDAO().unlockOfficialPolicy(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "unlockOfficialPolicy");
        }
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getLockDAO() == null)
            throw new ConfigurationException("The required property 'lockDAO' is missing.");
        if (getUserSessionManager() == null)
            throw new ConfigurationException("The required property 'userSessionManager' is missing.");
    }

    public UserSessionManager getUserSessionManager() {
        return m_userSessionManager;
    }

    public void setUserSessionManager(UserSessionManager userSessionManager) {
        m_userSessionManager = userSessionManager;
    }

    public LockDAO getLockDAO() {
        return m_lockDAO;
    }

    public void setLockDAO(LockDAO lockDAO) {
        m_lockDAO = lockDAO;
    }

    private UserSessionManager m_userSessionManager;
    private LockDAO m_lockDAO;
    private static final String PROPERTY_LOCK_DURATION = "policymgr.lock.duration";    
}
