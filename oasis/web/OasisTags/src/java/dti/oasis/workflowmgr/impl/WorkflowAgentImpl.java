package dti.oasis.workflowmgr.impl;

import dti.oasis.workflowmgr.WorkflowAgent;
import dti.oasis.workflowmgr.WorkflowProcess;
import dti.oasis.workflowmgr.WorkflowState;
import dti.oasis.workflowmgr.WorkflowTransition;
import dti.oasis.util.LogUtils;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.AppException;
import dti.oasis.session.UserSessionManager;
import dti.oasis.session.UserSession;
import dti.oasis.messagemgr.MessageManager;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;

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
 * 04/02/2008       Joe         1. Refactor into dti.oasis.workflowmgr.impl pacakge.
 *                              2. Replace parameter "policyNo" to "workflowInstanceId" for all methods.
 *                              3. Add two methods: getMessageParameters() and getWorkflowInstanceIdName().
 * 07/13/2010       kshen       Added method isTransitionParameterValid.
 * 10/12/2012       jshen       Added method getInstance(beanName). In ePolicy, there is a implementation for interface
 *                              WorkflowAgent already, which is highly policy related. It will always use policyNo as
 *                              the workflowInstanceId. But the flow may not always runs per one policy. We need the
 *                              ability to use the oasis implementation of WorkflowAgent to control the flow.
 * 02/22/2017       tzeng       168385 - Added getWorkflowProcessId().
 * ---------------------------------------------------
 */
public class WorkflowAgentImpl implements WorkflowAgent {

    private static final Logger l = LogUtils.getLogger(WorkflowAgentImpl.class);

    public static final String BEAN_NAME = "WorkflowAgentImpl";

    /**
     * Return an instance of a ready-to-use WorkflowAgent
     *
     * @return WorkflowAgentImpl
     */
    public static WorkflowAgent getInstance() {
        l.entering(WorkflowAgentImpl.class.getName(), "getInstance");

        WorkflowAgent instance;
        instance = (WorkflowAgent) ApplicationContext.getInstance().getBean(BEAN_NAME);

        l.exiting(WorkflowAgentImpl.class.getName(), "getInstance", instance);
        return instance;
    }

    /**
     * Return an instance of a ready-to-use WorkflowAgent
     *
     * @param beanName
     * @return WorkflowAgentImpl
     */
    public static WorkflowAgent getInstance(String beanName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(WorkflowAgentImpl.class.getName(), "getInstance", new Object[]{beanName});
        }

        WorkflowAgent instance;
        instance = (WorkflowAgent) ApplicationContext.getInstance().getBean(beanName);

        l.exiting(WorkflowAgentImpl.class.getName(), "getInstance", instance);
        return instance;
    }
    
    /**
     * Initialize the User Session with the process and initial state of the initiated
     * workflow process.  Process is uniquely identified by workflowInstanceId.
     * for this session.
     *
     * @param workflowInstanceId The unique workflowInstanceId affected by the workflow
     * @param workflowProcessId  workflow unique identifier
     * @param initialState       initial state of the workflow process
     */
    public void initializeWorkflow(String workflowInstanceId, String workflowProcessId, String initialState) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initializeWorkflowBean", new Object[]{workflowInstanceId, workflowProcessId, initialState});
        }

        // If found check for existence of any existing workflow processes for
        // the current policy.
        if (hasWorkflow(workflowInstanceId)) {

            // Remove anything found as we only permit one at a time
            clearWorkflow(workflowInstanceId);
        }

        // Get an instance of the configured bean definition from Spring
        WorkflowProcess instance;
        instance = (WorkflowProcess) ApplicationContext.getInstance().getBean(workflowProcessId);

        // Set the initial state
        instance.setCurrentWorkflowState(initialState);

        // Now get the Map and add our initialized values
        getWorkflowMap().put(workflowInstanceId, instance);

        l.exiting(getClass().getName(), "initializeWorkflow");
    }

    /**
     * Determine if the UserSession has any workflow process currently
     * in progress for the passed workflowInstanceId.
     *
     * @param workflowInstanceId The unique workflowInstanceId to check for workflow processes
     * @return boolean
     */
    public boolean hasWorkflow(String workflowInstanceId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasWorkflow", new Object[]{workflowInstanceId});
        }

        boolean workflowExists = false;

        Map workflowMap = getWorkflowMap();

        if (workflowMap.containsKey(workflowInstanceId)) {
            workflowExists = true;
        }

        l.exiting(getClass().getName(), "hasWorkflow", Boolean.valueOf(workflowExists));
        return workflowExists;
    }

    /**
     * Determine if the UserSession has any workflow process currently
     * in progress for the passed workflowProcessId.
     *
     * @param workflowInstanceId The unique workflowInstanceId to check for workflow processes
     * @param workflowProcessId  workflow unique identifier
     * @return boolean
     */
    public boolean hasWorkflow(String workflowInstanceId, String workflowProcessId) {
        boolean workflowExists = false;
        if (hasWorkflow(workflowInstanceId)) {
            WorkflowProcess workflowProcess = getWorkflow(workflowInstanceId);
            if (workflowProcess.getWorkflowProcessId().equals(workflowProcessId)) {
                workflowExists = true;
            }
        }
        return workflowExists;
    }

    /**
     * Returns true if the Workflow Attribute is set for the Workflow Process identified by the given workflowInstanceId.
     * Otherwise, returns false.
     *
     * @param workflowInstanceId The unique workflowInstanceId used to identify the Workflow Process
     * @param key                The key of the workflow attribute
     * @return true if the workflow attribute is set. Otherwise, false.
     */
    public boolean hasWorkflowAttribute(String workflowInstanceId, String key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasWorkflowAttribute", new Object[]{workflowInstanceId, key});
        }

        boolean hasWorkflowAttribute = getWorkflow(workflowInstanceId).hasWorkflowAttribute(key);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasWorkflowAttribute", Boolean.valueOf(hasWorkflowAttribute));
        }
        return hasWorkflowAttribute;
    }

    /**
     * Get the Workflow Attribute value for the Workflow Process identified by the given workflowInstanceId.
     *
     * @param workflowInstanceId The unique workflowInstanceId used to identify the Workflow Process
     * @param key                The key of the workflow attribute
     * @return the value of the workflow attribute
     * @throws IllegalArgumentException if the workflow attribute does not exist.
     */
    public Object getWorkflowAttribute(String workflowInstanceId, String key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWorkflowAttribute", new Object[]{workflowInstanceId, key});
        }

        Object value = getWorkflow(workflowInstanceId).getWorkflowAttribute(key, null);
        if (value == null) {
            IllegalArgumentException e = new IllegalArgumentException("The workflow attribute <" + key + "> does not exist for the Workflow Process <"+getWorkflow(workflowInstanceId).getWorkflowProcessId() + "> executing for workflowInstanceId<"+workflowInstanceId+">.");
            l.throwing(getClass().getName(), "getWorkflowAttribute", e);
            throw e;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getWorkflowAttribute", value);
        }
        return value;
    }

    /**
     * Get the Workflow Attribute value for the Workflow Process identified by the given workflowInstanceId.
     *
     * @param workflowInstanceId The unique workflowInstanceId used to identify the Workflow Process
     * @param key                The key of the workflow attribute
     * @param nullValue          the value to return if the workflow attribute is not set.
     * @return the value of the workflow attribute
     */
    public Object getWorkflowAttribute(String workflowInstanceId, String key, Object nullValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWorkflowAttribute", new Object[]{workflowInstanceId, key, nullValue});
        }

        Object value = getWorkflow(workflowInstanceId).getWorkflowAttribute(key, null);
        if (value == null) {
            value = nullValue;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getWorkflowAttribute", value);
        }
        return value;
    }

    /**
     * Set the Workflow Attribute value for the Workflow Process identified by the given workflowInstanceId.
     *
     * @param workflowInstanceId The unique workflowInstanceId used to identify the Workflow Process
     * @param key                The key of the workflow attribute
     * @param value              the value of the workflow attribute to set.
     */
    public void setWorkflowAttribute(String workflowInstanceId, String key, Object value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setWorkflowAttribute", new Object[]{workflowInstanceId, key, value});
        }

        getWorkflow(workflowInstanceId).setWorkflowAttribute(key, value);

        l.exiting(getClass().getName(), "setWorkflowAttribute");
    }

    /**
     * Determine if the state requested by the current page of the workflow matches with
     * the user's session information.  This prevents potential bookmarking problems.
     *
     * @param workflowInstanceId current unique workflowInstanceId being processed
     * @param workflowState      character value of the current workflow state
     * @return boolean
     */
    public boolean isValidWorkflowState(String workflowInstanceId, String workflowState) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isValidWorkflowState", new Object[]{workflowInstanceId, workflowState});
        }

        boolean returnValue = false;

        Map workflowMap = getWorkflowMap();

        if (workflowMap.containsKey(workflowInstanceId)) {
            WorkflowProcess sessionWorkflowProcess = (WorkflowProcess) workflowMap.get(workflowInstanceId);
            if (sessionWorkflowProcess.getCurrentWorkflowState().equalsIgnoreCase(workflowState)) {
                returnValue = true;
            } else {
                throw new AppException("Invalid workflow state for policy: " + workflowInstanceId);
            }
        }

        l.exiting(getClass().getName(), "isValidWorkflowState", Boolean.valueOf(returnValue));
        return returnValue;
    }

    /**
     * Determine the next step in the workflow adding any messages or forward parameters
     * as necessary.
     *
     * @param workflowInstanceId Current unique workflowInstanceId being processed
     * @return String
     */
    public String getNextState(String workflowInstanceId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNextState", new Object[]{workflowInstanceId});
        }

        String forwardString = "error";

        // Get the current workflow
        WorkflowProcess w = getWorkflow(workflowInstanceId);

        // Get the current transition
        WorkflowTransition t = w.getCurrentWorkflowTransition();

        // Retrieve the transition parameter if it exists, otherwise set to DEFAULT
        String transitionParameter = t.hasTransitionParameter() ? t.getTransitionParameter() : "DEFAULT";

        // Get the next workflow state based upon the transition parameter
        WorkflowState s = (WorkflowState) t.getTransitionState().get(transitionParameter);

        if (s == null) {
            clearWorkflow(workflowInstanceId);
            throw new AppException("The Transition Parameter '" + transitionParameter + "' does not exist for the Transition State '"
                + w.getCurrentWorkflowState() + "' within the Workflow Process '" + w.getWorkflowProcessId() + "'");
        }

        // Get the next workflow state as the forward string and set it as current
        forwardString = s.getWorkflowStateId();
        w.setCurrentWorkflowState(forwardString);

        // If a workflow state message key has been defined, set it into the message manager
        if (s.hasWorkflowStateMessageKey()) {
            MessageManager.getInstance().addInfoMessage(s.getWorkflowStateMessageKey(), getMessageParameters());
        }

        // Finally if this is the exiting workflow state, clean the workflow map
        if (s.getWorkflowExitState()) {
            clearWorkflow(workflowInstanceId);
        }

        l.exiting(getClass().getName(), "getNextState", forwardString);
        return forwardString;
    }

    /**
     * Helper method to set the transition parameter for the current workflow state
     *
     * @param workflowInstanceId current unique workflowInstanceId being processed
     * @param transitionValue    value for the transtion parameter to be set
     */
    public void setWorkflowTransitionParameter(String workflowInstanceId, String transitionValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setWorkflowTransitionParameter", new Object[]{workflowInstanceId, transitionValue});
        }

        // First get the workflow
        WorkflowProcess w = getWorkflow(workflowInstanceId);

        // Second get the transition for the current state
        WorkflowTransition t = w.getCurrentWorkflowTransition();

        // Get the next workflow state based upon the transition parameter
        WorkflowState s = (WorkflowState) t.getTransitionState().get(transitionValue);
        if (s == null) {
            clearWorkflow(workflowInstanceId);
            throw new AppException("The Transition Parameter '" + transitionValue + "' does not exist for the Transition State '" + w.getCurrentWorkflowState() + "' within the Workflow Process '" + w.getWorkflowProcessId() + "'");
        }

        // Finally set the parameter
        t.setTransitionParameter(transitionValue);

        l.exiting(getClass().getName(), "setWorkflowTransitionParameter");
    }

    /**
     * Check if a transaction parameter valid.
     * @param workflowInstanceId the workflow instance id.
     * @param transitionState the transaction state.
     * @param transitionValue the transaction parameter to be check if it is valid.
     * @return return true if it is valid. Otherwise, return false.
     */
    public boolean isTransitionParameterValid(String workflowInstanceId, String transitionState, String transitionValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isTransitionParameterValid", new Object[]{workflowInstanceId, transitionState, transitionValue});
        }

        // First get the workflow
        WorkflowProcess w = getWorkflow(workflowInstanceId);

        // Second get the transition for the transitionState
        WorkflowTransition t = (WorkflowTransition) w.getWorkflowTransitions().get(transitionState);
        if (t == null) {
            return false;
        }

        // Get the next workflow state based upon the transition parameter
        WorkflowState s = (WorkflowState) t.getTransitionState().get(transitionValue);
        if (s == null) {
            return false;
        }

        return true;
    }

    /**
     * Helper method to get the transition parameter for the current workflow state
     *
     * @param workflowInstanceId current unique workflowInstanceId being processed
     * @return string parameter value
     */
    public String getWorkflowTransitionParameter(String workflowInstanceId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWorkflowTransitionParameter", new Object[]{workflowInstanceId});
        }

        // First get the workflow
        WorkflowProcess w = getWorkflow(workflowInstanceId);

        // Second get the transition for the current state
        WorkflowTransition t = w.getCurrentWorkflowTransition();

        // Next get the parameter Map and pull out the value
        String parmValue = t.getTransitionParameter();

        l.exiting(getClass().getName(), "getWorkflowTransitionParameter", parmValue);
        return parmValue;
    }

    /**
     * Helper method to get the current state of the inprogress workflow
     *
     * @param workflowInstanceId current unique workflowInstanceId being processed
     * @return string current workflow state string value
     */
    public String getWorkflowCurrentState(String workflowInstanceId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWorkflowCurrentState", new Object[]{workflowInstanceId});
        }

        String currentState;

        // First get the workflow
        WorkflowProcess w = getWorkflow(workflowInstanceId);

        // Second get the transition for the current state
        currentState = w.getCurrentWorkflowState();

        l.exiting(getClass().getName(), "getWorkflowCurrentState", currentState);
        return currentState;
    }

    /**
     * Determine if the UserSession has any workflow process currently
     * in progress for the passed policy number.
     *
     * @param workflowInstanceId The unique workflowInstanceId to check for workflow processes
     */
    public void clearWorkflow(String workflowInstanceId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "clearWorkflow", new Object[]{workflowInstanceId});
        }

        Map workflowMap = getWorkflowMap();
        workflowMap.remove(workflowInstanceId);

        l.exiting(getClass().getName(), "clearWorkflow");
    }

    /**
     * To get message parameters for next state.
     *
     * @return an array of Object with message parameters
     */
    public Object[] getMessageParameters() {
        return null;
    }

    /**
     * To get the specific workflow unique Id name, for epolicy it is "policyNo". The default value is "workflowInstanceId".
     *
     * @return the workflow Instance Id Name
     */
    public String getWorkflowInstanceIdName() {
        return "workflowInstanceId";
    }

    /**
     * Get the WorkflowProcess bean from the User Session Manager for the in progress
     * workflow process
     *
     * @param workflowInstanceId The unique workflowInstanceId to check for workflow processes
     * @return WorkflowProcess
     * @throws IllegalStateException if the workflow process is not initialized for the given workflowInstanceId
     */
    private WorkflowProcess getWorkflow(String workflowInstanceId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWorkflow", new Object[]{workflowInstanceId});
        }

        Map workflowMap = getWorkflowMap();
        WorkflowProcess workflowProcess = (WorkflowProcess) workflowMap.get(workflowInstanceId);

        // Throw IllegalStateException if the workflow process is not initialized for the given workflowInstanceId
        if (workflowProcess == null) {
            IllegalStateException e = new IllegalStateException("There is no Workflow Process for the workflowInstanceId <" + workflowInstanceId + ">");
            l.throwing(getClass().getName(), "getWorkflow", e);
            throw e;
        }

        l.exiting(getClass().getName(), "getWorkflow", workflowProcess);
        return workflowProcess;
    }

    /**
     * Get the Workflow Map from the User Session Manager for the in progress
     * workflow processes across all policies for this session
     *
     * @return Map
     */
    private Map getWorkflowMap() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWorkflowMap");
        }

        Map workflowMap;

        UserSession userSession = UserSessionManager.getInstance().getUserSession();
        if (userSession.has(WORKFLOW_PROCESS_MAP)) {
            workflowMap = (Map) userSession.get(WORKFLOW_PROCESS_MAP);
        } else {
            // If no map exists add it to the User Session Manager
            workflowMap = new HashMap();
            UserSessionManager.getInstance().getUserSession().set(WORKFLOW_PROCESS_MAP, workflowMap);
        }

        l.exiting(getClass().getName(), "getWorkflowMap", workflowMap);
        return workflowMap;
    }

    @Override
    public String getWorkflowProcessId(String workflowInstanceId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWorkflowProcessId", workflowInstanceId);
        }

        // First get the workflow
        WorkflowProcess w = getWorkflow(workflowInstanceId);

        // Second get the transition for the current state
        String workflowProcessId = w.getWorkflowProcessId();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getWorkflowProcessId", workflowProcessId);
        }
        return workflowProcessId;
    }

    private static final String WORKFLOW_PROCESS_MAP = "workflowProcessMap";
}
