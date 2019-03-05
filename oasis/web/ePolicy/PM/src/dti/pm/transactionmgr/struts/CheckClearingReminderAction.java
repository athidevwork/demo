package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.MessageManagerAdmin;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Created by AWU
 * Date: 12/24/12
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/13/2018       wrong       192557 - Modified saveCheckClearingReminder() to call hasValidSaveToken() to be used
 *                                       for CSRFInterceptor.
 * ---------------------------------------------------
 */
public class CheckClearingReminderAction extends PMBaseAction {

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
        return saveCheckClearingReminder(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "invokeCheckClearingReminder"
     * sent in along the requested url. It used to check whether to invoke clearing reminder page or not.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward checkClearingReminder(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "checkClearingReminder", new Object[]{mapping, form, request, response});

        String forwardString = "loadPage";
        try {
            securePage(request, form, false);
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Boolean checkClearReminder = getTransactionManager().checkClearingReminder(policyHeader);
            if (!checkClearReminder) {
                // Check if workflow exists, otherwise throw application exception
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                    // Set the flag so that the workflow can continue to process other flows.
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "NO_CHECK_CLEARING_REMINDER");

                    // Get the next state
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
                else {
                    throw new AppException(AppException.UNEXPECTED_ERROR,
                        "Failed to determine workflow for Check Clearing Reminder.");
                }
            }
            else {
                ((MessageManagerAdmin) MessageManager.getInstance()).defineMessageForRequest(
                    MessageCategory.getInstance("CONFIRMATION_PROMPT"),
                    policyHeader.getPolicyNo(), "Check Clearing Reminder?",
                    "Y");
            }
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to process check clearing reminder.", e, request, mapping);
        }

        // Done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "checkClearingReminder", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "invokeClearingReminder"
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
    public ActionForward saveCheckClearingReminder(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveCheckClearingReminder", new Object[]{mapping, form, request, response});
        String forwardString = "";
        try {
            if (hasValidSaveToken(request)) {
                securePage(request, form, false);
                // Get PolicyHeader
                PolicyHeader policyHeader = getPolicyHeader(request);
                Record inputRecord = getInputRecord(request);
                getTransactionManager().processSaveCheckClearingReminder(policyHeader, inputRecord);

                // Check if workflow exists, otherwise throw application exception
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    // Get the next state
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
                else {
                    throw new AppException(AppException.UNEXPECTED_ERROR,
                        "Failed to determine workflow for Save Clearing Reminder.");
                }
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to process save clearing reminder.", e, request, mapping);
        }

        // Done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveCheckClearingReminder", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public CheckClearingReminderAction() {
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;
}
