package dti.pm.policymgr.lockmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.core.dao.BaseDAO;
import dti.pm.policymgr.PolicyIdentifier;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details of all DAO operations
 * that are performed against the lock manager.
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

public class LockJdbcDAO extends BaseDAO implements LockDAO {

    /**
     * Method that returns a boolean value that indicates whether a lock is obtained successfully for a policy that has
     * an in-progress transaction.
     * <p/>
     *
     * @param policyNo           the current policy #
     * @param policyIdentifier   an instance of Policy Identifier with all information loaded.
     * @param policyLockDuration the duration of the lock
     * @return boolean true, if a lock is obtained successfully; otherwise, false.
     */
    public boolean lockWIP(String policyNo, PolicyIdentifier policyIdentifier, String policyLockDuration, String lockContext) {
        Logger l = LogUtils.enterLog(getClass(), "lockWIP", new Object[]{policyNo, policyIdentifier});
        boolean isSuccessfullyLocked = false;

        if (policyIdentifier == null) {
            throw new AppException("Invalid policy information (null) passed to PolicyJdbcDAO.lockWIP method.");
        }

        try {
            // Map the PolicyIdentifyer properties to the input record
            Record input = new Record();
            RecordBeanMapper recBeanMapper = new RecordBeanMapper();
            recBeanMapper.map(policyIdentifier, input);
            input.setFieldValue("lockDuration", new Long(policyLockDuration));
            input.setFieldValue("lockContext", lockContext);

            // Create and execute the  stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Lock.Lock_WIP_2");
            policyIdentifier.setPolicyLockMessage("");
            Record output = spDao.executeUpdate(input);

            if (output.getIntegerValue(StoredProcedureDAO.UPDATE_COUNT_FIELD).intValue() != -1) {

                // Map the output record to the PolicyIdentifier
                // Update the policy WIP and Off number because the PolicyHeader is the latest if we can lock the policy.
                recBeanMapper.map(output, policyIdentifier);

                if (output.getLongValue("rc").intValue() == 0) {
                    isSuccessfullyLocked = true;
                    policyIdentifier.setPolicyLockMessage("");
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "lockWIP", "Obtained the lock for policyNo'" + policyIdentifier.getPolicyNo() + "'; policyLockId='" + policyIdentifier.getPolicyLockId() + "', officialNumber='" + policyIdentifier.getPolicyOffNumber() + "', wipNumber='" + policyIdentifier.getPolicyWipNumber() + "'");
                    }
                }
                else {
                    policyIdentifier.setPolicyLockId("");
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "lockWIP", "Failed to obtain the lock for policyNo'" + policyIdentifier.getPolicyNo() + "'; policyLockMessage='" + policyIdentifier.getPolicyLockMessage() + "'");
                    }
                }

                //RequestStorageManager.getInstance().set(RequestStorageIds.POLICY_LOCK_ID, policyIdentifier.getPolicyLockId());
            }
            else {
                throw new AppException("Lock policy process failed for policyNo:" + policyIdentifier.getPolicyNo());
            }

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to lock the policy " + policyIdentifier.getPolicyNo(), e);
            l.throwing(getClass().getName(), "lockWIP", ae);

            if (!StringUtils.isBlank(policyIdentifier.getPolicyLockId())) {
                try {
                    unLockWIP(policyIdentifier, YesNoFlag.Y, "Error - unable to lock policy with identifier = " + policyIdentifier.getPolicyLockId());
                }
                catch (Exception e1) {
                    ExceptionHelper.getInstance().handleException("Unable to unlock the policy " + policyIdentifier.getPolicyNo(), e1);
                }
            }
            throw ae;
        }

        l.exiting(getClass().getName(), "lockWIP", String.valueOf(isSuccessfullyLocked));
        return isSuccessfullyLocked;
    }

    /**
     * Method that returns a boolean value that indicates whether the lock held on the policy has been release successfully.
     * <p/>
     *
     * @param policyIdentifier an instance of Policy Identifier with all information loaded.
     * @param wipB an instance of YesNoFlag indicating the resultant wip status of the policy*
     * @param reinitializeWIPNumB an instance of YesNoFlag indicating if need to reinitialize policy WIP Num
     * @return true, if the lock has been release successfully;otherwise, false.
     */
    public boolean unLockWIP(PolicyIdentifier policyIdentifier, YesNoFlag wipB, String lockContext, YesNoFlag reinitializeWIPNumB){
        Logger l = LogUtils.enterLog(getClass(), "unLockWIP", new Object[]{policyIdentifier, wipB});
        boolean isSuccessfullyUnlocked = false;

        if (policyIdentifier == null) {
            throw new AppException("Invalid policy information (null) passed to PolicyJdbcDAO.unLockWIP method.");
        }

        try {
            if (StringUtils.isBlank(policyIdentifier.getPolicyLockId())) {
                throw new AppException("Trying to unlock the policy, before locking it.");
            }

            Record input = new Record();
            RecordBeanMapper recBeanMapper = new RecordBeanMapper();
            recBeanMapper.map(policyIdentifier, input);
            input.setFieldValue("wipB", wipB.getName());
            input.setFieldValue("lockContext", lockContext);

            // Create a DataRecordMapping for this stored procedure
            StoredProcedureDAO spDao;
            if(YesNoFlag.Y.equals(reinitializeWIPNumB)){
                spDao = StoredProcedureDAO.getInstance("PM_Lock.Unlock_WIP_Reinitialize");
            }else{
                spDao = StoredProcedureDAO.getInstance("PM_Lock.Unlock_WIP_2");
            }

            policyIdentifier.setPolicyLockMessage("");
            Record output = spDao.executeUpdate(input);

            if (output.getIntegerValue(StoredProcedureDAO.UPDATE_COUNT_FIELD).intValue() != -1) {
                // Map the output record to the PolicyIdentifier
                // Do not update the policy WIP or Off number because the PolicyHeader should be reloaded when these numbers change.
                recBeanMapper.addPropertyExclusion("policyWipNumber");
                recBeanMapper.addPropertyExclusion("policyOffNumber");
                recBeanMapper.map(output, policyIdentifier);

                if (output.getLongValue("rc").intValue() == 0) {
                    policyIdentifier.setPolicyViewMode(PolicyViewMode.OFFICIAL);
                    policyIdentifier.setPolicyLockId("");
                    isSuccessfullyUnlocked = true;
                    policyIdentifier.setPolicyLockMessage("");
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "unLockWIP", "sucessfully Unlocked policyNo '" + policyIdentifier.getPolicyNo() + "'; policyLockId='" + policyIdentifier.getPolicyLockId() + "', officialNumber='" + policyIdentifier.getPolicyOffNumber() + "', wipNumber='" + policyIdentifier.getPolicyWipNumber() + "'");
                    }
                }
                else {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "unLockWIP", "Failed to unLock policyNo '" + policyIdentifier.getPolicyNo() + "'; policyLockMessage='" + policyIdentifier.getPolicyLockMessage() + "'");
                    }
                }
            }
            else {
                throw new AppException("Unlock policy process failed for policyNo:" + policyIdentifier.getPolicyNo());
            }

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to unLock the policy " + policyIdentifier.getPolicyNo(), e);
            l.throwing(getClass().getName(), "unLockWIP", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "unLockWIP", String.valueOf(isSuccessfullyUnlocked));
        return isSuccessfullyUnlocked;
    }

    /**
     * Method that returns a boolean value that indicates whether the lock held on the policy has been release successfully.
     * <p/>
     *
     * @param policyIdentifier an instance of Policy Identifier with all information loaded.
     * @param wipB             an instance of YesNoFlag indicating the resultant wip status of the policy
     * @return true, if the lock has been release successfully;otherwise, false.
     */
    public boolean unLockWIP(PolicyIdentifier policyIdentifier, YesNoFlag wipB, String lockContext) {
        return unLockWIP(policyIdentifier, wipB, lockContext, YesNoFlag.N);
    }

    /**
     * Determine if an policy can be locked at the moment when it is called.
     *
     * @param policyIdentifier Record containing current policy, term, and transaction information
     * @return boolean: can this policy be locked by this policyIdentifier?
     */
    public boolean canLockPolicy(PolicyIdentifier policyIdentifier) {
        Logger l = LogUtils.enterLog(getClass(), "canLockPolicy", new Object[]{policyIdentifier});

        boolean canLock = false;

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("offNum", "policyOffNum"));
            mapping.addFieldMapping(new DataRecordFieldMapping("wipNum", "policyWipNum"));

            Record inputRecord = new Record();
            RecordBeanMapper recBeanMapper = new RecordBeanMapper();
            recBeanMapper.map(policyIdentifier, inputRecord);

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_lock.Can_lock_policy", mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            canLock = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to pm_lock.Can_lock_policy.", e);
            l.throwing(getClass().getName(), "canLockPolicy", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "canLockPolicy", Boolean.valueOf(canLock));

        return canLock;
    }

    /**
     * load all locked policy
     *
     * @param inputRecord
     * @param recordLoadProcessor an instance of record load processor
     * @return list of locked policy
     */
    public RecordSet loadAllLockedPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllLockedPolicy", new Object[]{inputRecord});
        }

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyNoCriteria", "policyNoFilter"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyHolderNameCriteria", "policyHolderNameFilter"));

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Locked_Policy_List", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load locked policy list.", e);
            l.throwing(getClass().getName(), "loadAllLockedPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllLockedPolicy", rs);
        }
        return rs;
    }


    /**
     * unlock policy with policyId and wipB fields
     *
     * @param inputRecord
     * @return unlock result and message returned by stored procedure
     */
    public Record unlockPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "unlockPolicy", new Object[]{inputRecord});
        }
        Record result = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Lock.Batch_Unlock_Wip");
        try {
            result = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unlock policy.", e);
            l.throwing(getClass().getName(), "unlockPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "unlockPolicy", result);
        }

        return result;
    }

    /**
     *
     * @param inputRecord
     * @return unlock result and message returned by stored procedure
     */
    public Record refreshPolicyLock(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "refreshPolicyLock", new Object[]{inputRecord});
        }
        Record result = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Lock.Refresh_Policy_Lock");
        try {
            result = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Refresh policy lock.", e);
            l.throwing(getClass().getName(), "refreshPolicyLock", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "refreshPolicyLock", result);
        }

        return result;
    }

    /**
     * unlock official policy
     *
     * @param inputRecord
     */
    public void unlockOfficialPolicy(Record inputRecord) {
       Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "unlockOfficialPolicy", new Object[]{inputRecord});
        }
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Lock.Batch_Unlock_OFF");
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unlock official policy.", e);
            l.throwing(getClass().getName(), "unlockOfficialPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "unlockOfficialPolicy");
        }
    }
}
