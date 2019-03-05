package dti.pm.workflowmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.app.ApplicationContext;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.oasis.workflowmgr.WorkflowProcess;
import dti.oasis.session.UserSessionManager;
import dti.pm.core.http.RequestIds;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.policymgr.PolicyHeader;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 29, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/03/2008       Joe         Refactor to extend dti.oasis.workflowmgr.impl.WorkflowAgentImpl,
 *                              override getMessageParameters() and getWorkflowInstanceIdName() methods.
 * 05/15/2009       yhyang      Override initializeWorkflow to set workflowAttribute policyTermHistoryId.
 * 01/20/2011       wfu         116136 - Changed logic to get message parameters from UserSession if not exits in RequestStorage
 * 02/28/2017       mlm         183387 - Refactored to handle preview for long running transactions.
 * 04/26/2017       mlm         184827 - Refactored to initialize previewRequest in policy header correctly.
 * ---------------------------------------------------
 */

public class WorkflowAgentImpl extends dti.oasis.workflowmgr.impl.WorkflowAgentImpl {

    /**
     * Return an instance of a ready-to-use WorkflowAgent
     *
     * @return WorkflowAgentImpl
     */
    public static WorkflowAgent getInstance() {
        Logger l = LogUtils.enterLog(WorkflowAgentImpl.class, "getInstance");

        WorkflowAgent instance;
        instance = (WorkflowAgent) ApplicationContext.getInstance().getBean(BEAN_NAME);

        l.exiting(WorkflowAgentImpl.class.getName(), "getInstance", instance);
        return instance;
    }

    /**
     * To get message parameters for next state.
     *
     * @return an array of Object with message parameters
     */
    public Object[] getMessageParameters() {
        Logger l = LogUtils.enterLog(getClass(), "getMessageParameters");

        PolicyHeader policyHeader = null;
        if (RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_HEADER)) {
            policyHeader = (PolicyHeader) RequestStorageManager.getInstance().get(RequestStorageIds.POLICY_HEADER);
        }

        if (policyHeader == null) {
            l.warning("The Policy Header cannot be found in the Request Storage Manager");
            policyHeader = (PolicyHeader) UserSessionManager.getInstance().getUserSession().get(UserSessionIds.POLICY_HEADER);
            if (policyHeader == null) {
                l.severe("The Policy Header cannot be found in the Request Storage Manager and UserSession Manager");
            } else {
                l.info("The Policy Header was found in the UserSession Manager");
            }
        }

        Object[] parm = new Object[]{""};
        if (policyHeader != null) {
            if (policyHeader.getPolicyCycleCode() != null) {
                parm = new Object[]{policyHeader.getPolicyCycleCode()};
            }
        }

        l.exiting(getClass().getName(), "getMessageParameters");
        return parm;
    }

    /**
     * To get the specific workflow unique Id name, for epolicy it is "policyNo". The default value is "workflowInstanceId".
     *
     * @return the workflow Instance Id Name
     */
    public String getWorkflowInstanceIdName() {
        return "policyNo";
    }

    /**
      * Initialize the User Session with the process and initial state of the initiated
      * workflow process.  Process is uniquely identified by workflowInstanceId.
      * for this session.
      * Set policyTermHistoryId to workflow attribute.
      *
      * @param workflowInstanceId The unique workflowInstanceId affected by the workflow
      * @param workflowProcessId  workflow unique identifier
      * @param initialState       initial state of the workflow process
      */
    public void initializeWorkflow(String workflowInstanceId, String workflowProcessId, String initialState) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initializeWorkflowBean", new Object[]{workflowInstanceId, workflowProcessId, initialState});
        }
        // Call the super initializeWorkflow
        super.initializeWorkflow(workflowInstanceId, workflowProcessId, initialState);

        // Set workflowAttribute policyTermHistoryId
        PolicyHeader policyHeader = (PolicyHeader) UserSessionManager.getInstance().getUserSession().get(UserSessionIds.POLICY_HEADER);
        if (policyHeader != null) {
            String policyTermHistoryId = policyHeader.getPolicyTermHistoryId();
            if (policyTermHistoryId != null) {
                setWorkflowAttribute(workflowInstanceId, "policyTermHistoryId", policyTermHistoryId);
            }

            //Issue 183387: This is a long running transaciton, so add preview indicator into the workflow attribute collection
            //(during initializeWorkflow) and remove it from the request so that preview popup/request doesn't get initiated immediately,
            //but rather wait until the work flow process gets completed.

            // Do not show the Preview popup until the workflow finishes.

            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "initializeWorkflowBean", "Setting isPreviewRequest value to " + (policyHeader.isPreviewRequest() ? "TRUE" : "FALSE") + " in workflow attribute collection for workflow instance id:" + workflowInstanceId + ".");
            }
            setWorkflowAttribute(workflowInstanceId, RequestIds.IS_PREVIEW_REQUEST, YesNoFlag.getInstance(policyHeader.isPreviewRequest()));

            policyHeader.setPreviewRequest(false);
        }

        l.exiting(getClass().getName(), "initializeWorkflow");
    }
}
