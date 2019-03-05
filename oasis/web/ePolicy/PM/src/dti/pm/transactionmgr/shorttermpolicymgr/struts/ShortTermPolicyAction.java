package dti.pm.transactionmgr.shorttermpolicymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.shorttermpolicymgr.ShortTermPolicyManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Short Term Policy.
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
 * 04/26/2016       eyin        171030 - Modified createAcceptPolicyTransaction(), unlockWIPReinitialize() is called
 *                                       to unlock policy once any exception is caught.
 * ---------------------------------------------------
 */

public class ShortTermPolicyAction extends PMBaseAction {

    /**
     * Call this method to create accept short term policy transaction.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward createAcceptPolicyTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "createAcceptPolicyTransaction", new Object[]{mapping, form, request, response});
        PolicyHeader policyHeader = null;
        Record inputRecord = null;

        try {
            // Secures access to the page
            securePage(request, form, false);
            // Get input
            inputRecord = getInputRecord(request);
            // Get PolicyHeader
            policyHeader = getPolicyHeader(request);
            getShortTermPolicyManager().createAcceptPolicyTransaction(policyHeader, inputRecord);
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            // System should unlock the policy if it is locked by itself.
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (ownLock) {
                getLockManager().unlockWIPReinitialize(policyHeader, YesNoFlag.N, "Unlock from ValidationException during Create Accept Policy Transaction.");
            }
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            // System should unlock the policy if it is locked by itself.
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (ownLock) {
                getLockManager().unlockWIPReinitialize(policyHeader, YesNoFlag.N, "Unlock from Exception during Create Accept Policy Transaction.");
            }
            handleErrorForAjax("pm.shortTermPolicy.createAcceptPolicyTransaction.error", "Create transaction failed.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "createAcceptPolicyTransaction", af);
        return af;
    }

    /**
     * Call this method to accept short term policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performAcceptPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performAcceptPolicy", new Object[]{mapping, form, request, response});
        PolicyHeader policyHeader = null;
        Record inputRecord = null;
        String forwardString = "closePage";

        try {
            // Secures access to the page
            securePage(request, form, false);
            policyHeader = getPolicyHeader(request);
            String policyNumber = policyHeader.getPolicyNo();
            // Get input record
            inputRecord = getInputRecord(request);

            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyNumber)) {
                String acceptStatus = getShortTermPolicyManager().performAcceptPolicy(policyHeader, inputRecord);
                wa.setWorkflowTransitionParameter(policyNumber, acceptStatus);
                // Get the next state
                forwardString = wa.getNextState(policyNumber);
                setForwardParametersForWorkflow(request, forwardString, policyNumber, wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR, "Failed to determine workflow for accept policy.");
            }
        }
        catch (ValidationException ve) {
            // Delete WIP transaction
            getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
            // Unlock the policy if it is locked by itself
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (ownLock) {
                getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from ValidationException during Perform Accept Policy." );
            }
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            // Delete WIP transaction
            getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
            // Unlock the policy if it is locked by itself
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (ownLock) {
                getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from Exception during Perform Accept Policy");
            }
            handleErrorPopup("pm.shortTermPolicy.acceptPolicy.error", "Failed to accept policy.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performAcceptPolicy", af);
        }
        return af;
    }

    /**
     * Call this method to decline short term policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performDeclinePolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performDeclinePolicy", new Object[]{mapping, form, request, response});
        PolicyHeader policyHeader = null;
        Record inputRecord = null;

        try {
            // Secures access to the page                               
            securePage(request, form, false);
            policyHeader = getPolicyHeader(request);
            inputRecord = getInputRecord(request);
            getShortTermPolicyManager().performDeclinePolicy(policyHeader, inputRecord);
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.shortTermPolicy.declinePolicy.error", "Failed to decline policy.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "performDeclinePolicy", af);
        return af;
    }

    /**
     * Verify ShortTermPolicyManager
     */
    public void verifyConfig() {
        if (getShortTermPolicyManager() == null)
            throw new ConfigurationException("The required property 'shortTermPolicyManager' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public ShortTermPolicyManager getShortTermPolicyManager() {
        return m_shortTermPolicyManager;
    }

    public void setShortTermPolicyManager(ShortTermPolicyManager shortTermPolicyManager) {
        m_shortTermPolicyManager = shortTermPolicyManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;
    private ShortTermPolicyManager m_shortTermPolicyManager;
}
