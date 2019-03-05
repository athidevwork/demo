package dti.pm.agentmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.agentmgr.AgentManager;
import dti.pm.core.struts.PMBaseAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;


/**
 * Action class for View Agent History
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 01, 2013
 *
 * @author skommi

 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Feb 01, 2013     skommi       Issue# 111565 Created
 * Mar 25, 2013     erj          Issue# 111565 - remove addJsMessages
 * ---------------------------------------------------
 */
public class ViewAgentHistoryAction extends PMBaseAction {
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
        return loadAllAgentHist(mapping, form, request, response);
    }

    /**
     * This method is called to display Producer agent Entry page
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllAgentHist(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAgentHist", new Object[]{mapping, form, request, response});
        String forwardString = "loadAllAgentHist";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);

            // load agents and set the dataBean to request
            RecordSet agentRs = getAgentManager().loadAllAgentHistory(inputRecord);

            // Set loaded data into request.
            setDataBean(request, agentRs);

            // Make the Summary Record available for output.
            Record output = agentRs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, output);
            // Set currentGridId to every gridID on page before load gird header then load grid header for each grid.
            loadGridHeader(request);
            // Load the list of values after loading the data.
            loadListOfValues(request, form);

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the agent History.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAgentHist", af);
        return af;
    }

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");

        if (getAgentManager() == null)
            throw new ConfigurationException("The required property 'agentManager' is missing.");
    }

    public AgentManager getAgentManager() {
        return m_agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        m_agentManager = agentManager;
    }

    private AgentManager m_agentManager;
}
