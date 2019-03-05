package dti.pm.transactionmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.busobjs.TransactionStatus;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 22, 2007
 *
 * @author Sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * --------------------------------------------------------------------------------------------------------------------
 * 05/24/2012       xnie        132403 - Modified validateTransaction to move "INVALID" logic to "else" branch. When
 *                              current policy has no a workflow and result is "INVALID", system shows validation error
 *                              page.
 * 08/31/2012       awu         136554 - Added a method validateNotify() to display the notify confirm message for Long
 *                              or Medium policy which validation result is "VALID_FLAG"
 * --------------------------------------------------------------------------------------------------------------------
 */

public class ValidateTransactionAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "validateTransaction");
        return validateTransaction(mapping, form, request, response);
    }

    
     /**
    * This method is called when the policy is Long or Medium and the validation result from back end is "VALID_FLAG".
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward validateNotify(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateNotify", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            MessageManager.getInstance().addConfirmationPrompt(confirmAnnualRate);

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to validate transaction.", e, request, mapping);
        }
        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "validateNotify", af);
        return af;
    }

    
    /**
    * This method is called when there the process parameter "loadAllValidationError"
    * sent in along the requested url.
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward validateTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateTransaction", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record policyRecord = policyHeader.toRecord();
            String result;
            // If the transaction is not in progress, system should skip the validation and then exit the workflow.
            TransactionStatus transStatus = policyHeader.getLastTransactionInfo().getTransactionStatusCode();
            if (transStatus.isInProgress()) {
                // Validate transaction
                result = getTransactionManager().performTransactionValidation(policyRecord);
            }
            else {
                result = "OFFICIAL";
            }
            l.info("Validate transaction Result=="+result);

            // Go to the next step based on the validation result
            if (result.equals("VALID_FLAG")) {
                MessageManager.getInstance().addConfirmationPrompt(confirmAnnualRate);
            }
            else {
                // Check if workflow exists, otherwise throw application exception
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                    // Set the transition flag so that workflow knows about the validation status
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), result);

                    // Get the next state
                   forwardString = wa.getNextState(policyHeader.getPolicyNo());
                   setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
                else if (result.equals("INVALID")) {
                    forwardString = "viewValidationError";
                }
                else {
                    throw new AppException(AppException.UNEXPECTED_ERROR,
                        "Failed to determine workflow for Validatiton Transaction.");
                }
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to validate transaction.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "validateTransaction", af);
        return af;
    }

    /**
    * This method is called when there the process parameter "processConfirmation"
    * sent in along the requested url.
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward processConfirmation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processConfirmation", new Object[]{mapping, form, request, response});
        String forwardString;

        try {
            // Get confirmation
            String confirmed = getInputRecord(request).getStringValue(confirmAnnualRate+".confirmed");

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Check if workflow exists, otherwise throw application exception
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                // Set the flag so that workflow knows about the validation confirmation
                wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "VALID_FLAG" + confirmed);

                // Get the next state
               forwardString = wa.getNextState(policyHeader.getPolicyNo());
               setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Validatiton Transaction.");
            }
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to process confirmation.", e, request, mapping);
        }

        // Done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processConfirmation", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public ValidateTransactionAction() {}

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;

    private static final String confirmAnnualRate = "pm.validateAndRateTransaction.confirm.annualRate";
}
