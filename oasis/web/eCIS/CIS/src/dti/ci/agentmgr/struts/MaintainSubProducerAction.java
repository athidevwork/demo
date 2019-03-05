package dti.ci.agentmgr.struts;

import dti.ci.struts.action.CIBaseAction;
import dti.ci.agentmgr.AgentManager;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The action class for maintain Sub Producer.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 3, 2010
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * ---------------------------------------------------
 */

public class MaintainSubProducerAction extends CIBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllSubProducer(mapping, form, request, response);
    }

    /**
     * This method is to load sub producer.
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
    public ActionForward loadAllSubProducer(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSubProducer", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllSubProducerResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);
            RecordSet rs = getAgentManager().loadAllSubProducer(inputRecord);

            setDataBean(request, rs);

            loadGridHeader(request);

            Record outputRecord = new Record();
            outputRecord.setFields(inputRecord);
            outputRecord.setFields(rs.getSummaryRecord(), false);
            publishOutputRecord(request, outputRecord);

            // load list of values
            loadListOfValues(request, form);

            // set js messages
            addJsMessages();

            saveToken(request);

        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to load the sub producer page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSubProducer", af);
        }

        return af;
    }

    //add js message
    private void addJsMessages() {
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


    public MaintainSubProducerAction() {
    }

    /**
     * verify config.
     */
    public void verifyConfig() {
        if (getAgentManager() == null) {
            throw new ConfigurationException("The required property 'agentManager' is missing.");
        }
    }

    public AgentManager getAgentManager() {
        return m_agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        m_agentManager = agentManager;
    }

    private AgentManager m_agentManager;
}
