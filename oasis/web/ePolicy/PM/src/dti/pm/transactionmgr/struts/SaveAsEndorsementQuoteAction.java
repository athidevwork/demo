package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.http.RequestIds;
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
 * Action class for save as endorsement quote
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 25, 2008
 *
 * @author sli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/09/2010       syang       115296 - Added exitSaveAsEndorsementQuote() and exitMonitorInvokeSaveEndorsementQuote()
 *                              to exit workflow and forward to saveEndorsementQuoteSuccess.jsp, system will set correct view mode in this jsp.
 * 02/20/2012       syang       130467 - Modified exitSaveAsEndorsementQuote to display the message based on the current transaction.
 * 01/03/2013       adeng       138680 - 1)Modified processSaveAsEndorsementQuote() to always to go to next flow step;
 *                                       2)Changed the next flows to Product Notify for monitorInvokeSaveEndorsementQuote
 *                                       in workflow, so removed useless method exitMonitorInvokeSaveEndorsementQuote().
 * 08/21/2015       wdang       165535 - Modified exitSaveAsEndorsementQuote() to retrieve endorsementQuoteId from workflow attribute
 *                                       "endQuoteId" instead of "endorsementQuoteId".
 * ---------------------------------------------------
 */

public class SaveAsEndorsementQuoteAction extends PMBaseAction {

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
        request.setAttribute(RequestIds.PROCESS, "processSaveAsEndorsementQuote");
        return processSaveAsEndorsementQuote(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "processSaveAsEndorsementQuote"
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
    public ActionForward processSaveAsEndorsementQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processSaveAsEndorsementQuote", new Object[]{mapping, form, request, response});
        String forwardString="success";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Call the transactionmgr business component to perform the save logic
            String endorsementQuoteId=getTransactionManager().processSaveTransactionAsEndorsementQuote(policyHeader);
            request.setAttribute("endorsementQuoteId",endorsementQuoteId);
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                request.setAttribute("workFlowProcessId", "saveEndorsementQuote");
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Save As Endorsement Quote.");
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to perform save as endorsement quote functionality.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);

        l.exiting(getClass().getName(), "processSaveAsEndorsementQuote", af);
        return af;
    }

    /**
     * Exit save as endorsement quote workflow and then forward to "success" page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward exitSaveAsEndorsementQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "exitSaveAsEndorsementQuote", new Object[]{mapping, form, request, response});
        String forwardString = "success";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Set endorsementQuoteId, clear workflow and then forward to success page.
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                String endorsementQuoteId = (String) wa.getWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.END_QUOTE_ID);
                request.setAttribute("endorsementQuoteId", endorsementQuoteId);
                // System displays the message based on the current transaction.
                String quoteType = "endorsement";
                if (policyHeader.getLastTransactionInfo().getTransactionTypeCode().isRenewal()) {
                    quoteType = "renewal";
                }
                MessageManager.getInstance().addInfoMessage("pm.workflowmgr.save.endQuote.saveAsEndorsementQuote.info", new Object[]{quoteType});
                wa.clearWorkflow(policyHeader.getPolicyNo());
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to exist save as endorsement quote functionality.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "exitSaveAsEndorsementQuote", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public SaveAsEndorsementQuoteAction() {
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;
    private static final String MONITOR_LONG_RUNNING_TRANSACTION = "MONITOR_LONG_RUNNING_TRANSACTION";
}
