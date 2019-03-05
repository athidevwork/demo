package dti.oasis.workflowmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.struts.BaseAction;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.oasis.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 31, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/02/2008       Joe         Refactor into dti.oasis.workflowmgr.struts pacakge.
 * 04/09/2013       mlm         142697 - Changes to support refreshing of the Workflow Screen without moving
 *                                       to the next step.
 * ---------------------------------------------------
 */

public class WorkflowAction extends BaseAction {
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
        request.setAttribute(RequestIds.PROCESS, "processWorkflow");
        return processWorkflow(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "processWorkflow"
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
    public ActionForward processWorkflow(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processWorkflow", new Object[]{mapping, form, request, response});

        String forwardString;

        try {
            // Secure access to the page
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            // Get an instance of the workflow agent
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            String workflowInstanceIdName = wa.getWorkflowInstanceIdName();
            String workflowInstanceId = inputRecord.getStringValue(workflowInstanceIdName);

            String workflowStateFromInput = "";
            String isPageRefreshed = "N";
            if (inputRecord.hasField("workflowState")) {
                workflowStateFromInput = inputRecord.getStringValue("workflowState");
            }
            if (inputRecord.hasField("isPageRefreshed")) {
                isPageRefreshed = inputRecord.getStringValue("isPageRefreshed");
            }

            l.info("Core processWorkflow: isPageRefreshed:" + isPageRefreshed +
                "/workflowInstanceId:" + workflowInstanceId +
                "/currentWorkflowState:" + wa.getWorkflowCurrentState(workflowInstanceId) +
                "/workflowStateFromInput:" + workflowStateFromInput);

            if (wa.getWorkflowCurrentState(workflowInstanceId).equalsIgnoreCase(workflowStateFromInput) && isPageRefreshed.equalsIgnoreCase("N")) {
                // Get the next state returning the forward string
                forwardString = wa.getNextState(workflowInstanceId);
            } else {
                //User manually refreshed the page. Get the next state returning the forward string
                forwardString = wa.getWorkflowCurrentState(workflowInstanceId);
            }
            l.info("Core processWorkflow: forwardString:" + forwardString);

            setForwardParameter(request, RequestIds.PROCESS, "unspecified");
            setForwardParametersForWorkflow(request, forwardString, inputRecord.getStringValue(workflowInstanceIdName),
                wa.getWorkflowInstanceIdName());
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to process workflow.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processWorkflow", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public WorkflowAction() {
    }
}

