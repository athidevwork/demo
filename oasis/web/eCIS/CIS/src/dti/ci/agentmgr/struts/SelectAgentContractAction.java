package dti.ci.agentmgr.struts;

import dti.ci.agentmgr.AgentManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 28, 2008
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Apr 17, 2008     James       Issue#75265 Modify code according to code review
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * ---------------------------------------------------
 */
public class SelectAgentContractAction extends CIBaseAction {

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
        return loadAllAgentContract(mapping, form, request, response);
    }

    /**
     * This method is called to display agent Entry page
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
    public ActionForward loadAllAgentContract(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAgentContract", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);

            // load agent contract and set the dataBean to request
            RecordSet rs = getAgentManager().loadAllAvailableAgentContract(inputRecord);
            setDataBean(request, rs);
            Record outputRecord = rs.getSummaryRecord();
            publishOutputRecord(request, outputRecord);

            // load list of values
            loadListOfValues(request, form);
            //load grid header
            loadGridHeader(request);
            //add js message
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                    "Failed to load the agent contract information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAgentContract", af);
        return af;
    }

    /**
     * add js message
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.agentmgr.selectcontract.searchByRequired");
        MessageManager.getInstance().addJsMessage("ci.agentmgr.selectcontract.searchStringRequired");
        MessageManager.getInstance().addJsMessage("ci.agentmgr.selectcontract.searchStringNotFound");

        // add js messages for csCommon.js
        MessageManager.getInstance().addJsMessage("cs.changes.lost.confirm");
        MessageManager.getInstance().addJsMessage("cs.save.process.notCompleted");
        MessageManager.getInstance().addJsMessage("cs.term.select.error.noSelect");
        MessageManager.getInstance().addJsMessage("cs.entity.miniPopup.error.noEntityId");
        MessageManager.getInstance().addJsMessage("cs.function.error.notExist");
        MessageManager.getInstance().addJsMessage("cs.records.delete.confirm");
        MessageManager.getInstance().addJsMessage("cs.entity.information.error.notRecorded");
        MessageManager.getInstance().addJsMessage("cs.rowSelected.error.exception");
        MessageManager.getInstance().addJsMessage("cs.run.error.grid.value");
    }


    /**
     * verify config
     */
    public void verifyConfig() {
        super.verifyConfig();
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
