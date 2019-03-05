package dti.pm.workflowmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.oasis.workflowmgr.impl.WorkflowAgentImpl;
import dti.oasis.workflowmgr.struts.WorkflowAction;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Common work flow action for ePolicy
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 13, 2010
 *
 * @author bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/16/2010       dzhang      103813 - Added policyTermHistoryId to workflow's attribute.
 * 10/11/2010       dzhang      112956 - Modified processWorkflow. 
 * 10/16/2012       xnie        133766 - Added processNonPolicyWorkflow.
 * 04/09/2013       mlm         142697 - Changes to support refreshing of the Workflow Screen without moving
 *                                       to the next step.
 * ---------------------------------------------------
 */
public class PolicyWorkflowAction extends WorkflowAction {
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
               l.info("Policy processWorkflow: isPageRefreshed:" + isPageRefreshed +
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
               l.info("Policy processWorkflow: forwardString:" + forwardString);

               setForwardParameter(request, RequestIds.PROCESS, "unspecified");

               // Set forward parameters to load policy header correctly
               // Policy view mode
               if (wa.hasWorkflow(workflowInstanceId)) {
                   if (wa.hasWorkflowAttribute(workflowInstanceId, "policyViewMode")) {
                       String policyViewMode = (String) wa.getWorkflowAttribute(workflowInstanceId, "policyViewMode");
                       if (!StringUtils.isBlank(policyViewMode)) {
                           setForwardParameter(request, RequestIds.POLICY_VIEW_MODE, policyViewMode);
                       }
                   }

                   if (wa.hasWorkflowAttribute(workflowInstanceId, "endQuoteId")) {
                       String endQuoteId = (String) wa.getWorkflowAttribute(workflowInstanceId, "endQuoteId");
                       // Endosement Id
                       if (!StringUtils.isBlank(endQuoteId)) {
                           setForwardParameter(request, "endQuoteId", endQuoteId);
                       }
                   }

                   if (wa.hasWorkflowAttribute(workflowInstanceId, "policyTermHistoryId")) {
                       String policyTermHistoryId = (String) wa.getWorkflowAttribute(workflowInstanceId, "policyTermHistoryId");
                       // policy term history Id
                       if (!StringUtils.isBlank(policyTermHistoryId)) {
                           setForwardParameter(request, "policyTermHistoryId", policyTermHistoryId);
                       }
                   }
               }

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

    /**
         * This method is called when there the process parameter "processNonPolicyWorkflow"
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
        public ActionForward processNonPolicyWorkflow(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
            Logger l = LogUtils.enterLog(getClass(), "processNonPolicyWorkflow", new Object[]{mapping, form, request, response});

            String forwardString;

            try {
                // Secure access to the page
                securePage(request, form);

                Record inputRecord = getInputRecord(request);

                // Get an instance of the workflow agent
                WorkflowAgent wa = WorkflowAgentImpl.getInstance(PolicyManager.OASIS_WORKFLOW_BEAN_NAME);
                String workflowInstanceIdName = wa.getWorkflowInstanceIdName();

                // Get the next state returning the forward string
                forwardString = wa.getNextState(inputRecord.getStringValue(workflowInstanceIdName));
                setForwardParameter(request, dti.oasis.http.RequestIds.PROCESS, "unspecified");
                setForwardParametersForWorkflow(request, forwardString, inputRecord.getStringValue(workflowInstanceIdName),
                    wa.getWorkflowInstanceIdName());
            }
            catch (Exception e) {
                forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to process non-policy workflow.", e, request, mapping);
            }

            // done
            ActionForward af = mapping.findForward(forwardString);
            l.exiting(getClass().getName(), "processNonPolicyWorkflow", af);
            return af;
        }
}
