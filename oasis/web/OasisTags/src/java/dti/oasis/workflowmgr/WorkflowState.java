package dti.oasis.workflowmgr;

import dti.oasis.app.ConfigurationException;

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

public class WorkflowState {

    public boolean hasWorkflowStateMessageKey() {
        return !(m_workflowStateMessageKey == null);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(
            "WorkflowState{" +
            "m_workflowStateId='" + m_workflowStateId + '\'' +
            ", m_workflowStateMessageKey='" + m_workflowStateMessageKey + '\'' +
            ", m_workflowExitState='" + m_workflowExitState);
        return sb.toString();
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getWorkflowStateId() == null)
            throw new ConfigurationException("The required property 'workflowStateId' is missing.");      
    }

    public WorkflowState() {
    }

    public String getWorkflowStateId() {
        return m_workflowStateId;
    }

    public void setWorkflowStateId(String workflowStateId) {
        m_workflowStateId = workflowStateId;
    }

    public String getWorkflowStateMessageKey() {
        return m_workflowStateMessageKey;
    }

    public void setWorkflowStateMessageKey(String workflowStateMessageKey) {
        m_workflowStateMessageKey = workflowStateMessageKey;
    }

    public boolean getWorkflowExitState() {
        return m_workflowExitState;
    }

    public void setWorkflowExitState(boolean workflowExitState) {
        m_workflowExitState = workflowExitState;
    }

    String m_workflowStateId;
    String m_workflowStateMessageKey = null;
    boolean m_workflowExitState = false;
}
