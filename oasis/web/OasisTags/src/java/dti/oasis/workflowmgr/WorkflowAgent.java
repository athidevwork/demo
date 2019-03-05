package dti.oasis.workflowmgr;

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
 * 02/22/2017       tzeng       168385 - Added getWorkflowProcessId().
 * ---------------------------------------------------
 */

public interface WorkflowAgent {

    /**
     * Initialize the User Session with the process and initial state of the initiated
     * workflow process.  Process is uniquely identified by workflowInstanceId.
     * for this session.
     *
     * @param workflowInstanceId The unique workflowInstanceId affected by the workflow
     * @param workflowProcessId workflow unique identifier
     * @param initialState initial state of the workflow process
     */
    void initializeWorkflow(String workflowInstanceId, String workflowProcessId, String initialState);
    
    /**
     * Determine if the UserSession has any workflow process currently
     * in progress for the passed workflowInstanceId.
     *
     * @param workflowInstanceId The unique workflowInstanceId to check for workflow processes
     * @return boolean
     */
    boolean hasWorkflow(String workflowInstanceId);

    /**
     * Determine if the UserSession has any workflow process currently
     * in progress for the passed workflowProcessId.
     *
     * @param workflowInstanceId The unique workflowInstanceId to check for workflow processes
     * @param workflowProcessId workflow unique identifier
     * @return boolean
     */
    boolean hasWorkflow(String workflowInstanceId, String workflowProcessId);

    /**
     * Returns true if the Workflow Attribute is set for the Workflow Process identified by the given workflowInstanceId.
     * Otherwise, returns false.
     *
     * @param workflowInstanceId The unique workflowInstanceId used to identify the Workflow Process
     * @param key The key of the workflow attribute
     * @return true if the workflow attribute is set. Otherwise, false.
     */
    boolean hasWorkflowAttribute(String workflowInstanceId, String key);

    /**
     * Get the Workflow Attribute value for the Workflow Process identified by the given workflowInstanceId.
     *
     * @param workflowInstanceId The unique workflowInstanceId used to identify the Workflow Process
     * @param key The key of the workflow attribute
     * @return the value of the workflow attribute
     * @throws IllegalArgumentException if the workflow attribute does not exist.
     */
    Object getWorkflowAttribute(String workflowInstanceId, String key);

    /**
     * Get the Workflow Attribute value for the Workflow Process identified by the given workflowInstanceId.
     *
     * @param workflowInstanceId The unique workflowInstanceId used to identify the Workflow Process
     * @param key The key of the workflow attribute
     * @param nullValue the value to return if the workflow attribute is not set.
     * @return the value of the workflow attribute
     */
    Object getWorkflowAttribute(String workflowInstanceId, String key, Object nullValue);

    /**
     * Set the Workflow Attribute value for the Workflow Process identified by the given workflowInstanceId.
     *
     * @param workflowInstanceId The unique workflowInstanceId used to identify the Workflow Process
     * @param key The key of the workflow attribute
     * @param value the value of the workflow attribute to set.
     */
    void setWorkflowAttribute(String workflowInstanceId, String key, Object value);

    /**
     * Determine if the state requested by the current page of the workflow matches with
     * the user's session information.  This prevents potential bookmarking problems.
     *
     * @param workflowInstanceId current unique workflowInstanceId being processed
     * @param workflowState character value of the current workflow state
     * @return boolean
     */
    boolean isValidWorkflowState(String workflowInstanceId, String workflowState);

    /**
     * Determine the next step in the workflow adding any messages or forward parameters
     * as necessary.
     *
     * @param workflowInstanceId Current unique workflowInstanceId being processed
     * @return String
     */
    String getNextState(String workflowInstanceId);

    /**
     * Helper method to set the transition parameter for the current workflow state
     *
     * @param workflowInstanceId current unique workflowInstanceId being processed
     * @param transitionValue value for the transtion parameter to be set
     */
    void setWorkflowTransitionParameter(String workflowInstanceId, String transitionValue);

    /**
     * Helper method to get the transition parameter for the current workflow state
     *
     * @param workflowInstanceId current unique workflowInstanceId being processed
     * @return string parameter value
     */
    String getWorkflowTransitionParameter(String workflowInstanceId);

    /**
     * Check if a transaction parameter valid.
     * @param workflowInstanceId the workflow instance id.
     * @param transitionState the transaction state.
     * @param transitionValue the transaction parameter to be check if it is valid.
     * @return return true if it is valid. Otherwise, return false.
     */
    public boolean isTransitionParameterValid(String workflowInstanceId, String transitionState, String transitionValue);

    /**
     * Helper method to get the current state of the inprogress workflow
     *
     * @param workflowInstanceId current unique workflowInstanceId being processed
     * @return string current workflow state string value
     */
    String getWorkflowCurrentState(String workflowInstanceId);

    /**
     * Determine if the UserSession has any workflow process currently
     * in progress for the passed policy number.
     *
     * @param workflowInstanceId The unique workflowInstanceId to check for workflow processes
     */
    void clearWorkflow(String workflowInstanceId);

    /**
     * To get message parameters for next state.
     *
     * @return an array of Object with message parameters
     */
    Object[] getMessageParameters();

    /**
     * To get the specific workflow unique Id name, for epolicy it is "policyNo". The default value is "workflowInstanceId".
     *
     * @return the workflow Instance Id Name
     */
    String getWorkflowInstanceIdName();

    /**
     * Helper method to get the process id of the inprogress workflow
     * @param workflowInstanceId current unique workflowInstanceId being processed
     * @return string current workflow process id
     */
    String getWorkflowProcessId(String workflowInstanceId);
}
