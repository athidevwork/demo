package dti.pm.policymgr.struts;

import dti.cs.policynotificationmgr.NotificationTransactionTimeEnum;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.OasisUser;
import dti.oasis.util.StringUtils;
import dti.oasis.workflowmgr.WorkflowFields;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyManager;
import dti.oasis.workflowmgr.impl.WorkflowAgentImpl;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   September 28, 2012
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/27/2012       xnie        133766 - Initial version.
 * 11/16/2012       xnie        138948 - Moved load rerate result detail logic to new action.
 * 12/12/2012       xnie        139838 - Modified reRateBatch() to pass record to ajax xml.
 * 02/22/2017       tzeng       168385 - Modified reRateBatch() and performReRateOnDemand() to set requested transaction time.
 * ---------------------------------------------------
 */
public class MassReRateAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return openReRateResult(mapping, form, request, response);
    }

    /**
     * Method to load mass rerate result
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward openReRateResult(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "openReRateResult", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            RecordSet rs = new RecordSet();
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record outputRecord = new Record();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load mass rerate result page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "openReRateResult", af);
        return af;
    }

    /**
     * Method to load mass rerate result
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllReRateResult(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllReRateResult", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);
            WorkflowAgent wa = WorkflowAgentImpl.getInstance(PolicyManager.OASIS_WORKFLOW_BEAN_NAME);
            String workflowInstanceId;
            String requestId = "";
            if (inputRecord.hasStringValue(WorkflowFields.WORKFLOW_INSTANCE_ID)) {
                workflowInstanceId = WorkflowFields.getWorkflowInstanceId(inputRecord);
                if (wa.hasWorkflow(workflowInstanceId)) {
                    requestId = (String) wa.getWorkflowAttribute(workflowInstanceId, PolicyFields.REQUEST_ID);
                    if (!StringUtils.isBlank(requestId)) {
                        PolicyFields.setRequestId(inputRecord, requestId);
                    }
                }
            }
            else if (inputRecord.hasStringValue(PolicyFields.REQUEST_ID)) {
                requestId = PolicyFields.getRequestId(inputRecord);
            }

            // Load mass rerate result
            RecordSet rs = getPolicyManager().loadAllReRateResult(inputRecord);
            Record outputRecord = rs.getSummaryRecord();

            // Set all data beans to request
            setDataBean(request, rs);

            PolicyFields.setRequestId(outputRecord, requestId);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);

            // Load grid header
            loadGridHeader(request);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // add js messages to messagemanager for the current request
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load mass rerate result page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllReRateResult", af);
        return af;
    }

    /**
     * Batch rerate.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward reRateBatch(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "reRateBatch", new Object[]{mapping, form, request, response});

        String forwardString = null;
        Record inputRecord = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Set notification transaction time to oasis user
                OasisUser oasisUser = UserSessionManager.getInstance().getUserSession().getOasisUser();
                oasisUser.setRequestedTransactionTime(NotificationTransactionTimeEnum.OFFICIAL.getCode());

                // Map request to record for input
                inputRecord = getInputRecord(request);

                // Save the changes
                Record retRec = getPolicyManager().reRateBatch(inputRecord);
                
                writeAjaxXmlResponse(response, retRec);
            }
        }
        catch (ValidationException ve) {
            // Handle the validation exception
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to Batch rerate.", e, response);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "reRateBatch", af);
        return af;
    }

    /**
     * On-demand rerate policies.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward reRateOnDemand(ActionMapping mapping,
                                        ActionForm form,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "reRateOnDemand", new Object[]{mapping, form, request, response});
        }

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page
                securePage(request, form, false);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                Record retRec = getPolicyManager().reRateOnDemand(inputRecord);
                
                writeAjaxXmlResponse(response, retRec);
            }
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to On-Demand rerate policies.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "reRateOnDemand", af);
        }
        return af;
    }

    /**
     * On-demand rerate.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performReRateOnDemand(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performReRateOnDemand", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                // Rerate On-demand
                WorkflowAgent wa = WorkflowAgentImpl.getInstance(PolicyManager.OASIS_WORKFLOW_BEAN_NAME);
                String workflowInstanceId = WorkflowFields.getWorkflowInstanceId(inputRecord);
                if (!wa.hasWorkflow(workflowInstanceId)) {
                    throw new AppException(AppException.UNEXPECTED_ERROR,
                        "Failed to determine workflow for rerate on demand.");
                }
                String termList = (String) wa.getWorkflowAttribute(workflowInstanceId, PolicyFields.TERM_LIST);
                PolicyFields.setTermList(inputRecord, termList);

                // Set notification transaction time to oasis user
                OasisUser oasisUser = UserSessionManager.getInstance().getUserSession().getOasisUser();
                oasisUser.setRequestedTransactionTime(NotificationTransactionTimeEnum.OFFICIAL.getCode());

                String requestId = getPolicyManager().performReRateOnDemand(inputRecord);
                wa.setWorkflowAttribute(workflowInstanceId, PolicyFields.REQUEST_ID, requestId);

                forwardString = wa.getNextState(workflowInstanceId);
                setForwardParametersForWorkflow(request, forwardString, workflowInstanceId, wa.getWorkflowInstanceIdName());
            }
        }
        catch (ValidationException ve) {
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to On-Demand rerate.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "performReRateOnDemand", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.reRatePolicy.reRateResult.nodata.found.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public MassReRateAction(){}

}
