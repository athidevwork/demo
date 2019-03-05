package dti.pm.transactionmgr.struts;

import dti.oasis.messagemgr.MessageManager;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.transactionmgr.TransactionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 20, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/18/2011       ryzhao      113559 - Add viewRelatedPolicyErrorWithNoPolicyHeader().
 * 10/23/2012       awu         Issue 137764 - Modified viewRelatedPolicyError() to get out the continued official flag
 *                                             and set it into input record.
 * 12/06/2013       fcb         148037 - Changes for performance tuning.
 * ---------------------------------------------------
 */

public class ViewRelatedPolicyErrorAction extends PMBaseAction {

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
        request.setAttribute(RequestIds.PROCESS,"viewRelatedPolicyError");
        return viewRelatedPolicyError(mapping, form, request, response);
    }

    /**
    * This method is called when there the process parameter "viewRelatedPolicyError"
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
    public ActionForward viewRelatedPolicyError(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "viewRelatedPolicyError", new Object[]{mapping, form, request, response});
        String forwardString = "loadRelatedPolicyError";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = policyHeader.toRecord();

            // Get the related policy errors
            RecordSet rs = getTransactionManager().loadRelatedPolicySaveError(inputRecord);

            // Set data bean
            setDataBean(request, rs);

            // Load the grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to perform view related policy save error functionality.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "viewRelatedPolicyError", af);
        return af;
    }

    /**
     * This method is called when there is no policy header.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward viewRelatedPolicyErrorWithNoPolicyHeader(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "viewRelatedPolicyErrorWithNoPolicyHeader", new Object[]{mapping, form, request, response});
        String forwardString = "loadRelatedPolicyErrorWithNoPolicyHeader";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            Record inputRecord = null;

            // Get the related policy errors
            RecordSet rs = getTransactionManager().loadRelatedPolicySaveError(inputRecord);

            // Change page title
            MessageManager.getInstance().addJsMessage("pm.processErp.error.policy.page.title");

            // Set data bean
            setDataBean(request, rs);

            // Load the grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to perform view related policy save error functionality.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "viewRelatedPolicyErrorWithNoPolicyHeader", af);
        return af;
    }

    /**
    * This method is called when there the process parameter "closeRelatedPolicyError"
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
    public ActionForward closeRelatedPolicyError(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "closeRelatedPolicyError", new Object[]{mapping, form, request, response});
        String forwardString = "closePage";

        try {
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Check if workflow exists, otherwise do nothing
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                // Get the next state
               forwardString = wa.getNextState(policyHeader.getPolicyNo());
               setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to perform view related policy error close.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "closeRelatedPolicyError", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public ViewRelatedPolicyErrorAction() {}

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;
}





