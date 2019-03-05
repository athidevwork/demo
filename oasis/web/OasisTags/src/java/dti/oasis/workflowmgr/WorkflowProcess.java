package dti.oasis.workflowmgr;

import dti.oasis.app.ConfigurationException;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;

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
 * 04/02/2008       Joe         Refactor into dti.oasis.workflowmgr pacakge.
 * ---------------------------------------------------
 */

public class WorkflowProcess {
    
    public String getCurrentWorkflowState() {
        return m_currentWorkflowState;
    }

    public void setCurrentWorkflowState(String currentWorkflowState) {
        m_currentWorkflowState = currentWorkflowState;
    }

    public WorkflowTransition getCurrentWorkflowTransition() {
        return (WorkflowTransition) m_workflowTransitions.get(m_currentWorkflowState);
    }

    /**
     * Returns true if the Workflow Attribute is set.
     * Otherwise, returns false.
     *
     * @param key      The key of the workflow attribute
     * @return true if the workflow attribute is set. Otherwise, false.
     */
    public boolean hasWorkflowAttribute(String key) {
        return getWorkflowAttributes().get(key) != null;
    }

    /**
     * Get the Workflow Attribute value for the Workflow Process identified by the given policyNo.
     *
     * @param key      The key of the workflow attribute
     * @return the value of the workflow attribute
     * @throws IllegalArgumentException if the workflow attribute does not exist.
     */
    public Object getWorkflowAttribute(String key) {
        Object value = getWorkflowAttributes().get(key);
        if (value == null) {
            throw new IllegalArgumentException("The workflow attribute <" + key + "> does not exist for the Workflow Process <"+getWorkflowProcessId() + ">.");
        }
        return value;
    }

    /**
     * Get the Workflow Attribute value.
     * If the workflow attribute is null, the nullValue is returned.
     *
     * @param key       The key of the workflow attribute
     * @param nullValue the value to return if the workflow attribute is not set.
     * @return the value of the workflow attribute
     */
    public Object getWorkflowAttribute(String key, Object nullValue) {
        Object value = getWorkflowAttributes().get(key);
        if (value == null) {
            value = nullValue;
        }
        return value;
    }

    /**
     * Set the Workflow Attribute value.
     *
     * @param key      The key of the workflow attribute
     * @param value    the value of the workflow attribute to set.
     */
    public void setWorkflowAttribute(String key, Object value) {
        getWorkflowAttributes().put(key, value);
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getWorkflowProcessId() == null)
            throw new ConfigurationException("The required property 'workflowProcessId' is missing.");
        if (getWorkflowTransitions() == null)
            throw new ConfigurationException("The required property 'workflowTransitions' is missing.");
    }

    public WorkflowProcess() {
    }

    public String getWorkflowProcessId() {
        return m_workflowProcessId;
    }

    public void setWorkflowProcessId(String workflowProcessId) {
        m_workflowProcessId = workflowProcessId;
    }

    public Map getWorkflowTransitions() {
        return m_workflowTransitions;
    }

    public void setWorkflowTransitions(Map workflowTransitions) {
        m_workflowTransitions = workflowTransitions;
    }


    private Map getWorkflowAttributes() {
        if (m_workflowAttributes == null) {
            m_workflowAttributes = new HashMap();
        }
        return m_workflowAttributes;
    }

    String m_workflowProcessId;
    String m_currentWorkflowState;
    Map m_workflowTransitions = new Hashtable();
    Map m_workflowAttributes;
}
