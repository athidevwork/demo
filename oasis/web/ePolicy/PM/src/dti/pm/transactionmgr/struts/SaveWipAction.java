package dti.pm.transactionmgr.struts;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.transactionmgr.impl.TransactionManagerImpl;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.busobjs.PolicyHeaderReloadCode;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.impl.TransactionSaveProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 11, 2008
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/06/2013       fcb         142697 - Changed for View Premium in the Workflow.
 * 02/28/2017       mlm         183387 - Refactored to handle preview for long running transactions.
 * 04/26/2017       mlm         184827 - Refactored to initialize previewRequest in policy header correctly.
 * ---------------------------------------------------
 */

public class SaveWipAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS, "processSaveWip");
        return processSaveWip(mapping, form, request, response);
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
    public ActionForward processSaveWip(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processSaveWip", new Object[]{mapping, form, request, response});

        String forwardString;
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            /*
             For long-running transaction, the parent page gets refreshed immediately and the workflow starts in a popup page.
             For such cases, the previewRequest indicator needs to be set to false so that the preview popup wont appear immediately,
             but rather wait until the workflow is completed.

             After the workflow is completed, policy header may be loaded from session or RSM cache, which will have the previewRequest as false.
             So, check if workflow exists (for long running transactions) in order to reset the preview indicator,
             only for the case when the workflow is started as a result of preview request.
            */
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // If this request is part of a workflow, reset the preview request indicator because it could have been loaded from cached session.
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "processSaveWip", "The request is part of a workflow instance [id=" + policyHeader.getPolicyNo() + ". Checking whether this is a preview request...");
                }
                if (wa.hasWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.IS_PREVIEW_REQUEST)) {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "processSaveWip", "[" + policyHeader.getPolicyNo() + "] The preview indicator is :" + ((YesNoFlag) wa.getWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.IS_PREVIEW_REQUEST)).booleanValue());
                    }
                    policyHeader.setPreviewRequest(((YesNoFlag) wa.getWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.IS_PREVIEW_REQUEST)).booleanValue());
                } else {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "processSaveWip", "[" + policyHeader.getPolicyNo() + "] This workflow is not part of preview request.");
                    }
                }
            }
            Record inputRecord = policyHeader.toRecord();
            inputRecord.setFieldValue("parms", "MISC_TYPE^WIP_SAVE^");

            TransactionSaveProcessor saveProcessor = (TransactionSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
            saveProcessor.saveTransactionAsWip(inputRecord);

            // Check if workflow exists, otherwise throw application exception
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

               forwardString = wa.getNextState(policyHeader.getPolicyNo());

               policyHeader.setReloadCode(PolicyHeaderReloadCode.CURRENT_TERM);
               setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Save Wip.");
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to save wip.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processSaveWip", af);
        return af;
    }

    /**
     * This method is called when the process parameter "exitFromInvokeSaveWipProcess" is
     * sent along in the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward exitFromInvokeSaveWipProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "exitFromInvokeSaveWipProcess", new Object[]{mapping, form, request, response});

        String forwardString;
        try {
            PolicyHeader policyHeader = getPolicyHeader(request);

            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                
                YesNoFlag showViewPremium = YesNoFlag.N;
                if (wa.hasWorkflowAttribute(policyHeader.getPolicyNo(), "showViewPremium"))  {
                    showViewPremium = (YesNoFlag)wa.getWorkflowAttribute(policyHeader.getPolicyNo(), "showViewPremium");
                }
                YesNoFlag isPreviewRequest = YesNoFlag.N;
                if (wa.hasWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.IS_PREVIEW_REQUEST))  {
                    isPreviewRequest = (YesNoFlag)wa.getWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.IS_PREVIEW_REQUEST);
                }
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                if (!showViewPremium.booleanValue()) {
                    // Skip the next step in the Workflow (View Premium).
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                }
                policyHeader.setReloadCode(PolicyHeaderReloadCode.CURRENT_TERM);
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                if (isPreviewRequest.booleanValue()) {
                    //Setup preview request information, if the workflow was initiated for preview request.
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "exitFromInvokeSaveWipProcess", "Setting isPreviewRequest value to " + (isPreviewRequest.booleanValue() ? "TRUE" : "FALSE") + " in policy header.");
                    }
                    policyHeader.setPreviewRequest(isPreviewRequest.booleanValue());
                    UserSessionManager.getInstance().getUserSession().set(RequestIds.IS_PREVIEW_REQUEST, isPreviewRequest);
                    UserSessionManager.getInstance().getUserSession().set(TransactionManagerImpl.IS_SAVE_REQUEST_COMPLETED_SUCCESSFULLY, YesNoFlag.Y);
                }
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Exit from Save Wip.");
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to Exit from save wip.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "exitFromInvokeSaveWipProcess", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public SaveWipAction() {}

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    protected static final String SAVE_PROCESSOR = "TransactionManager";
    
    private TransactionManager m_transactionManager;
}
