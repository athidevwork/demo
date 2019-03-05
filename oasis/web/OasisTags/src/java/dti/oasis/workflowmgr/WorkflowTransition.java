package dti.oasis.workflowmgr;

import java.util.Hashtable;
import java.util.Map;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 14, 2007
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

public class WorkflowTransition {

    public boolean hasTransitionParameter() {
        return !(m_transitionParameter == null);
    }
    
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public WorkflowTransition() {
    }

    public Map getTransitionState() {
        return m_transitionState;
    }

    public void setTransitionState(Map transitionState) {
        m_transitionState = transitionState;
    }

    public String getTransitionParameter() {
        return m_transitionParameter;
    }

    public void setTransitionParameter(String transitionParameter) {
        m_transitionParameter = transitionParameter;
    }    

    Map m_transitionState = new Hashtable();
    String m_transitionParameter = null;
}
