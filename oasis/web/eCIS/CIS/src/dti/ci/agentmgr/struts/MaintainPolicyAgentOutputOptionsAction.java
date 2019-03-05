package dti.ci.agentmgr.struts;

import dti.ci.agentmgr.AgentFields;
import dti.ci.agentmgr.AgentManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for maintainance for agent Output Options
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 7, 2011
 *
 * @author yjmiao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class MaintainPolicyAgentOutputOptionsAction extends CIBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadAllAgentOutputOption(mapping, form, request, response);
    }

    /**
     * To load all agent output options
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward loadAllAgentOutputOption(ActionMapping mapping,
                                                  ActionForm form,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAgentOutputOption", new Object[]{mapping, form, request, response});
        String forwardString = "success";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);

            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getAgentManager().loadAllAgentOutputOption(inputRecord);
            }
            // publish output record
            Record sumRecord = rs.getSummaryRecord();
            publishOutputRecord(request, sumRecord);

            setDataBean(request, rs);

            if (inputRecord.hasStringValue(AgentFields.POLICY_ID)) {
                // Set Page Title
                PageBean pageBean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
                pageBean.setTitle(POLICY_AGENT_OUTPUT_OPTION_PAGE_TITLE);
            }

            // Load grid header bean
            loadGridHeader(request);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the agent output options.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAgentOutputOption", af);
        return af;
    }

    /**
     * Ajax method to get initial values for add output option
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForAddOutputOption(ActionMapping mapping,
                                                  ActionForm form,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
         Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddOutputOption",
                new Object[]{mapping, form, request, response});

        try {
            securePage(request, form);

            // Get inital values
            Record record = getAgentManager().getInitialValuesForAddOutputOption(getInputRecord(request));

            // Publish Output Record
            publishOutputRecord(request, record);

            loadListOfValues(request, form);

            getLovLabelsForInitialValues(request, record);

            // Send back xml data
            writeAjaxXmlResponse(response, record); 

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get output initial values.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddOutputOption", af);
        return af;
    }

    /**
     * To save all agent output Options
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllAgentOutputOption(ActionMapping mapping,
                                                  ActionForm form,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAgentOutputOption",
                new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet polTermRs;
        RecordSet outputRs = null;

        try {
            //If the request has valid save token,then process
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);

                Record inputRecord = getInputRecord(request);

                outputRs = getInputRecordSet(request);

                getAgentManager().saveAllAgentOutputOption(inputRecord, outputRs);
            }
        }
        catch (ValidationException e) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, outputRs);
            handleValidationException(e, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save agent output options.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllAgentOutputOption", af);
        return af;
    }

    public AgentManager getAgentManager() {
        return m_agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        m_agentManager = agentManager;
    }

    /**
     * add js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("cs.records.delete.confirm");
    }

    /**
     * verify config.
     */
    public void verifyConfig() {
        if (getAgentManager() == null)
            throw new ConfigurationException("The required property 'agentManager' is missing.");
    }

    public MaintainPolicyAgentOutputOptionsAction() {
    }

    private AgentManager m_agentManager;
    private static final String POLICY_AGENT_OUTPUT_OPTION_PAGE_TITLE = "Policy Agent Output Options"; 
}
