package dti.pm.transactionmgr.shorttermpolicymgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import dti.pm.transactionmgr.shorttermpolicymgr.ShortTermPolicyManager;
import dti.pm.transactionmgr.shorttermpolicymgr.dao.ShortTermPolicyDAO;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of ShortTermPolicyManager Interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 1, 2011
 *
 * @author ryzhao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/22/2012       xnie        130643 - Modified performDeclinePolicy() to replace pm.purgePolicy.createTransaction.error
 *                                       to pm.specialTrans.createTransaction.error.  
 * 12/06/2013       fcb         148037 - changes for performance tuning.
 * 06/19/2014       awu         155198 - Modified createAcceptPolicyTransaction to initial workflow starting point to invokeProductNotifyMsg
 * 09/18/2014       jyang2      157645 - Modified createAcceptPolicyTransaction to pass the policy termEffectiveToDate to
 *                                       createTransaction method.
 * ---------------------------------------------------
 */

public class ShortTermPolicyManagerImpl implements ShortTermPolicyManager {

    /**
     * Create accept short term policy transaction
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void createAcceptPolicyTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "createAcceptPolicyTransaction", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord());
        // Create transaction
        getTransactionManager().createTransaction(policyHeader, inputRecord, policyHeader.getTermEffectiveToDate(), TransactionCode.ACCEPTPOL);
        // Initialize the accept short term policy workflow
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        // If system parameter PM_CHECK_NOTICE is set and its value is "Y" then we will start the work flow with "invokeProductNotify".
        // Otherwise we will start the workflow with "invokeAcceptShortTermPolicyMsg".
        String pmCheckNotice = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_CHECK_NOTICE, "N");
        if (YesNoFlag.getInstance(pmCheckNotice).booleanValue()) {
            wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                "AcceptShortTermPolicyWorkflow",
                "invokeProductNotifyMsg");
        }
        else {
            wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                "AcceptShortTermPolicyWorkflow",
                "invokeAcceptShortTermPolicyMsg");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "createAcceptPolicyTransaction");
        }
    }

    /**
     * Accept short term policy
     *
     * @param policyHeader
     * @param inputRecord
     * @return accept status - SUCCESS/FAILED
     */
    public String performAcceptPolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performAcceptPolicy", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord());
        // Accept policy
        Record outputRecord = getShortTermPolicyDAO().acceptPolicy(inputRecord);
        String acceptStatus = null;
        if (outputRecord != null && outputRecord.hasStringValue("return")) {
            acceptStatus = outputRecord.getStringValue("return");
        }
        else {
            acceptStatus = "FAILED";
        }

        // If accept policy successfully unlock the policy and complete the transaction
        if ("SUCCESS".equalsIgnoreCase(acceptStatus)) {
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (ownLock) {
                getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from perform Accept Policy.");
            }
            getTransactionManager().UpdateTransactionStatusNoLock(policyHeader.getLastTransactionInfo(), TransactionStatus.COMPLETE);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performAcceptPolicy", acceptStatus);
        }
        return acceptStatus;
    }

    /**
     * Decline short term policy
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void performDeclinePolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performDeclinePolicy", new Object[]{policyHeader, inputRecord});
        }

        Transaction trans = null;
        // Create transaction
        String transactionEffectiveDate = policyHeader.getTermEffectiveFromDate();
        try {
            trans = getTransactionManager().createTransaction(policyHeader, inputRecord, transactionEffectiveDate, TransactionCode.DECLINEPOL);
            String transactionLogId = trans.getTransactionLogId();
            TransactionFields.setTransactionLogId(inputRecord, transactionLogId);
        }
        catch (Exception e) {
            MessageManager.getInstance().addErrorMessage("pm.specialTrans.createTransaction.error");
            throw new ValidationException("Unable to create transaction.");
        }

        // Decline policy
        CancelProcessFields.setCancellationType(inputRecord, CANCEL_TYPE);
        CancelProcessFields.setCancellationReason(inputRecord, CANCEL_REASON);
        CancelProcessFields.setCancellationMethod(inputRecord, METHOD_CODE);
        CancelProcessFields.setTailCreateB(inputRecord, YesNoFlag.N);
        CancelProcessFields.setNumAgeOvrdRisks(inputRecord, "0");
        try {
            getShortTermPolicyDAO().declinePolicy(inputRecord);
        }
        catch (Exception e) {
            // Set new created transaction to policy header for deleting WIP transaction.
            policyHeader.setLastTransactionInfo(trans);
            // Delete WIP transaction
            getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
            // Unlock the policy if it is locked by itself
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (ownLock) {
                getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from Exception during Decline Policy.");
            }
            MessageManager.getInstance().addErrorMessage("pm.shortTermPolicy.declinePolicy.error");
            throw new ValidationException("Unable to decline the policy.");
        }

        // Initialize the save official workflow
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
            "SaveOfficialWorkflow",
            "invokeSaveOfficial");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performDeclinePolicy");
        }
    }

    /**
     * Verify config
     */
    public void verifyConfig() {
        if (getShortTermPolicyDAO() == null)
            throw new ConfigurationException("The required property 'shortTermPolicyDAO' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
        if (getTransactionManager() == null) {
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        }
    }

    public ShortTermPolicyDAO getShortTermPolicyDAO() {
        return m_shortTermPolicyDAO;
    }

    public void setShortTermPolicyDAO(ShortTermPolicyDAO shortTermPolicyDAO) {
        m_shortTermPolicyDAO = shortTermPolicyDAO;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private ShortTermPolicyDAO m_shortTermPolicyDAO;
    private LockManager m_lockManager;
    private TransactionManager m_transactionManager;

    private static final String CANCEL_TYPE = "CANCUNDWR";
    private static final String CANCEL_REASON = "SHORTTERM";
    private static final String METHOD_CODE = "SHORTRATE";
}
