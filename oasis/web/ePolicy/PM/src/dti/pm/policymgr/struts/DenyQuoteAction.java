package dti.pm.policymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.oasis.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Quote Deny.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 4, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/28/2008       fcb         82170: validateDenyQuote added.
 * 10/20/2008       yhyang      87300: Update validateDenyQuote.
 * ---------------------------------------------------
 */

public class DenyQuoteAction extends PMBaseAction {

    /**
     * This method is triggered automatically when there is no process parameter
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return captureDenyData(mapping, form, request, response);
    }

    /**
     * this method handles deny quote
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward denyQuote(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "denyQuote", new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {
                PolicyHeader policyHeader = getPolicyHeader(request);
                // Secures access to the page
                securePage(request, form, false);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                //quoteManger handle deny quote process
                getPolicyManager().denyQuote(policyHeader, inputRecord);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to deny quote.", e, response);
        }

        l.exiting(getClass().getName(), "denyQuote", null);
        return null;
    }

    /**
     * this method handles validations for deny quote
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateDenyQuote(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateDenyQuote", new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {

                // Secures access to the page
                securePage(request, form, false);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                // Initialize Workflow
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                    "DenyQuote", "invokeProductNotifyProcess");

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate deny quote.", e, response);
        }
        ActionForward af = mapping.findForward(null);
        l.exiting(getClass().getName(), "validateDenyQuote", null);
        return af;
    }

    /**
     * capture deny quote data screen
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward captureDenyData(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "captureDenyData",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadDenyData";
        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);

            // Publish the output record for use by the Oasis Tags and JSP
            Record outputRecord = getPolicyManager().getInitialValuesForDenyQuote(policyHeader);
            publishOutputRecord(request, outputRecord);

            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, outputRecord);

            //add js messages to messagemanager for the current request
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to capture Deny Data.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "captureDenyData", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainQuote.deny.effDateRange.error");
    }

    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    public PolicyManager getPolicyManager() {
        return this.policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    // private memember variables...
    private PolicyManager policyManager;
}