package dti.pm.transactionmgr.reinstateprocessmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.reinstateprocessmgr.ReinstateProcessFields;
import dti.pm.transactionmgr.reinstateprocessmgr.ReinstateProcessManager;
import dti.pm.transactionmgr.reinstateprocessmgr.dao.ReinstateProcessDAO;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskRelationFields;
import dti.pm.core.request.RequestStorageIds;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of ReinstateProcessManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 20, 2007
 *
 * @author Jerry
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/30/2007       Rewrite logics in "validateStatusAndTerm"
 * 10/19/2011       wfu         125007 - Added logic to catch policy locking or policy picture changing
 *                                       validation exception for correct error message displaying.
 * ---------------------------------------------------
 */

public class ReinstateProcessManagerImpl implements ReinstateProcessManager {

    /**
     * validate SoloOwnerReinstate,CustomReinstate,ActiveReinstate and Policy Prompt Eligible
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a set of Records, each with the updated Reinstate info.
     */
    public void validateProcessReinstate(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateProcessReinstate", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);
        String reinstatelevel = ReinstateProcessFields.getReinstateLevel(inputRecord);

        // Validate Customer Reinstate for policy/risk/coverage/coverage class
        Record custValidateResult = getReinstateProcessDAO().validateCustomReinstate(inputRecord);
        if ("INVALID".equals(custValidateResult.getStringValue("status"))) {
            MessageManager.getInstance().addErrorMessage("pm.maintainreinstate.customReinstate.error",
                new String[]{custValidateResult.getStringValue("msg")});
        }

        // validateSoloOwnerReinstate;
        if (reinstatelevel.equals("RISK")) {
            Long validateSolo = getReinstateProcessDAO().validateSoloOwnerReinstate(inputRecord);
            if (validateSolo.longValue() > 0) {
                MessageManager.getInstance().addErrorMessage("pm.maintainReinstate.soloOwnerValidation.error");
            }
        }
        // validateActiveReinstate;
        String parm = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_ACTIVE_TAIL_MSG, "N");
        if ("Y".equals(parm)) {
            String validateACtive = getReinstateProcessDAO().validateActiveReinstate(inputRecord);
            if (validateACtive.equals("YES")) {
                MessageManager.getInstance().addErrorMessage("pm.maintainReinstate.activeTailExists.error");
            }
        }
        // get Policy Prompt Eligible
        String policyEligible = isPolicyEligibleForPrompt(policyHeader, inputRecord);

        if ("Y".equals(policyEligible) && !ConfirmationFields.isConfirmed(
            "pm.maintainReinstate.confirmed", inputRecord)) {
            MessageManager.getInstance().addConfirmationPrompt("pm.maintainReinstate.policyEligible");
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts())
            throw new ValidationException();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateProcessReinstate");
        }
    }

    /**
     * Perform all Reinstate' information
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a set of Records, each with the updated Reinstate info
     * @return record containing reinstate results
     */
    public Record performReinstate(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performReinstate", new Object[]{policyHeader, inputRecord});
        }

        // Is it possible to lock down the policy? but do not lock it until we are about to createTransaction
        boolean canLockPolicy = getLockManager().canLockPolicy(policyHeader);
        if (!canLockPolicy) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error");
            throw new ValidationException();
        }

        Record outputRecord;
        // If reinstateLevel is RISK RELATION, call performRiskRelationReinstate() method
        String reinstateLevel = ReinstateProcessFields.getReinstateLevel(inputRecord);
        if (reinstateLevel.equals(REINSTATE_LEVEL_RISKREL)) {
            outputRecord = performRiskRelationReinstate(policyHeader, inputRecord);
        }
        else {
            // set field values
            String transactionId = policyHeader.getLastTransactionInfo().getTransactionLogId();
            inputRecord.setFieldValue("transId", transactionId);
            inputRecord.setFields(policyHeader.toRecord(), false);

            // process reinstate
            outputRecord = getReinstateProcessDAO().performReinstate(inputRecord);

            Long rcNumber = outputRecord.getLongValue("rc");
            if (rcNumber.longValue() < 0) {
                MessageManager.getInstance().addErrorMessage("pm.maintainReinstate.error.save");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performReinstate", outputRecord);
        }
        return outputRecord;
    }


    /**
     * Validate base record stats and term for reinstatement
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a set of Records, each with the updated Reinstate info
     */
    public void validateStatusAndTerm(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateStatusAndTerm", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);
        String status = ReinstateProcessFields.getStatusType(inputRecord).toUpperCase();
        String reinstateLevel = ReinstateProcessFields.getReinstateLevel(inputRecord).toUpperCase();
        String activeMessageKey;
        String termMessageKey = "";

        // Get message keys based on reinstate level
        if ("POLICY".equals(reinstateLevel)) {
            activeMessageKey = "pm.maintainReinstate.active.policy.error";
        }
        else if ("RISK".equals(reinstateLevel)) {
            activeMessageKey = "pm.maintainReinstate.active.risk.error";
            termMessageKey = "pm.maintainReinstate.validateTerm.risk.error";
        }
        else if ("COVERAGE".equals(reinstateLevel)) {
            activeMessageKey = "pm.maintainReinstate.active.coverage.error";
            termMessageKey = "pm.maintainReinstate.validateTerm.coverage.error";
        }
        else {
            activeMessageKey = "pm.maintainReinstate.active.coverageClass.error";
            termMessageKey = "pm.maintainReinstate.validateTerm.coverageClass.error";
        }

        // Alternate Flow: Failed Pre-Reinstate Validation
        if ("POLICY".equals(reinstateLevel)) {
            // Reinstate is in "Policy" level
            // If policy status is not CANCEL or HOLD
            if (!("CANCEL".equals(status) || "HOLD".equals(status))) {
                MessageManager.getInstance().addErrorMessage(activeMessageKey);
            }
        }
        else {
            // Reinstate is in "RISK/Coverage/Coverage Class" level
            if ("CANCEL".equals(status)) {
                // Special Requirement: Identifying Risk/Coverage/Class Cancelled Term
                String identifyTerm = getReinstateProcessDAO().identifyingTerm(inputRecord);
                if ("N".equals(identifyTerm)) {
                    MessageManager.getInstance().addErrorMessage(termMessageKey);
                }
            }
            else {
                // Selected Risk/Coverage/Coverage Class base record status is not CANCEL
                MessageManager.getInstance().addErrorMessage(activeMessageKey);
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateStatusAndTerm");
        }
    }


    /**
     * is Policy Eligible For Prompt
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a set of Records, each with the updated Reinstate info
     * @return String
     */
    public String isPolicyEligibleForPrompt(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPolicyEligibleForPrompt", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);
        String reinstCount = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_REINST_COUNT, null);
        String isPolicyPromt = null;
        if (!StringUtils.isBlank(reinstCount)) {
            isPolicyPromt = getReinstateProcessDAO().isPolicyprompt(inputRecord);
        }

        l.exiting(getClass().getName(), "isPolicyEligibleForPrompt", isPolicyPromt);
        return isPolicyPromt;
    }

    /**
     * To reinstate risk relation
     *
     * @param policyHeader
     * @param inputRecord
     * @return record with reinstate risk relation results
     */
    private Record performRiskRelationReinstate(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performRiskRelationReinstate", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);

        // process reinstate
        Record outputRecord = getReinstateProcessDAO().performRiskRelationReinstate(inputRecord);

        RiskFields.setOrigRiskEffectiveFromDate(outputRecord, RiskFields.getOrigRiskEffectiveFromDate(inputRecord));
        RiskFields.setRiskEffectiveFromDate(outputRecord, RiskFields.getRiskEffectiveFromDate(inputRecord));
        RiskFields.setRiskEffectiveToDate(outputRecord, RiskFields.getRiskEffectiveToDate(inputRecord));
        RiskRelationFields.setRiskCountyCode(outputRecord, RiskRelationFields.getRiskCountyCode(inputRecord));
        RiskRelationFields.setCurrentRiskTypeCode(outputRecord, RiskRelationFields.getCurrentRiskTypeCode(inputRecord));
        ReinstateProcessFields.setReinstateLevel(outputRecord, REINSTATE_LEVEL_RISKREL);

        Long rcNumber = outputRecord.getLongValue("rc");
        if (rcNumber.longValue() < 0) {
            String errorMsg = outputRecord.getStringValue("rmsg");
            MessageManager.getInstance().addErrorMessage("pm.reinstateRiskRelation.save.error",
                new String[]{errorMsg});
            throw new AppException("Error: " + errorMsg);
        }

        // reload the policy header to get the lock
        getTransactionManager().setPolicyHeaderReloadCode(policyHeader, TransactionCode.REINSTATE);
        RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
        PolicyHeader ph = getPolicyManager().loadPolicyHeader(policyHeader.getPolicyNo(),
            "dti.pm.transactionmgr.reinstateprocessmgr.struts.PerformReinstateAction&process=performReinstate", "ReinstateProcess: performRiskRelationReinstate");

        // unlock the policy
        getLockManager().unLockPolicy(ph, "ReinstateProcess: performRiskRelationReinstate");

        // Initialize the workflow
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        wa.initializeWorkflow(policyHeader.getPolicyNo(),
            "SaveAsOfficialDetail",
            "invokeRateNotifyAndSaveAsOfficialDetail");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performRiskRelationReinstate");
        }
        return outputRecord;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getReinstateProcessDAO() == null)
            throw new ConfigurationException("The required property 'reinstateProcessDAO' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyDAO' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
    }

    public ReinstateProcessDAO getReinstateProcessDAO() {
        return m_ReinstateProcessDAO;
    }

    public void setReinstateProcessDAO(ReinstateProcessDAO reinstateProcessDAO) {
        m_ReinstateProcessDAO = reinstateProcessDAO;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    private TransactionManager m_transactionManager;
    private PolicyManager m_policyManager;
    private LockManager m_lockManager;
    private ReinstateProcessDAO m_ReinstateProcessDAO;
    public static final String REINSTATE_LEVEL_RISKREL = "RISK RELATION";
}




