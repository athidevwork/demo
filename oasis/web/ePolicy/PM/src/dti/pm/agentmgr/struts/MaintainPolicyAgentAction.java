package dti.pm.agentmgr.struts;

import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.pm.agentmgr.AgentFields;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.agentmgr.AgentManager;
import dti.pm.policymgr.PolicyHeader;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Action class for maintain policy agent
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 26, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Mar 21, 2008     James       Issue#75265 CreatedAdd Agent Tab to ECIS.
 *                              Same functionality, look and feel
 * 12/02/2013       jyang       149171 - Roll back 141758's change to load LOV label fields' value in
 *                                       getInitialValuesForXXX method.
 * 11/25/2014       kxiang      158853 - Set href value to nameHref in method getInitialValuesForAddAgent.
 * ---------------------------------------------------
 */
public class MaintainPolicyAgentAction extends PMBaseAction {
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
        return loadAllAgent(mapping, form, request, response);
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
    public ActionForward loadAllAgent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAgent", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);
            // get the policyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);

            // load agents and set the dataBean to request
            RecordSet rs =  (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getAgentManager().loadAllPolicyAgent(policyHeader, inputRecord);
            }
            setDataBean(request, rs);

            Record outputRecord = rs.getSummaryRecord();
            publishOutputRecord(request, outputRecord);

            // load list of values
            loadListOfValues(request, form);
            // load Grid Header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the agent information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAgent", af);
        return af;
    }

    /**
     * Validate all new and modified agent records.
     */
    public ActionForward validateAllAgent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateAllAgent", new Object[]{mapping, form, request, response});
        RecordSet inputRecords = null;
        try {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                // Save the changes
                getAgentManager().validateAllPolicyAgent(getPolicyHeader(request), inputRecords);

                // Send back xml data
                writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
             handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate the agent page.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateAllAgent", af);
        return af;
    }

    /**
     * Save all new and modified agent records.
     */
    public ActionForward saveAllAgent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAgent", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllAgent", "Saving the agent inputRecords: " + inputRecords);
                }

                // Save the changes
                getAgentManager().saveAllPolicyAgent(getPolicyHeader(request), inputRecords);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request, so loadAllAgent does not
            //  AgentDAO to get recordset again.
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save the agent page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllAgent", af);
        return af;
    }

    /**
     * This method is called by AJAX to get intitial values for a agent
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
    public ActionForward getInitialValuesForAddAgent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // return load(mapping, form, request, response);
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddAgent", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        String process = request.getParameter("process");
        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record initialRecord = getAgentManager().getInitialValuesForAddPolicyAgent(policyHeader,inputRecord);

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField agentField = fields.getField(AgentFields.PRODUCER_AGENT_NAME_GH);
            AgentFields.setProducerAgentNameHref(initialRecord, agentField.getHref());

            // get LOV labels for initial values
            publishOutputRecord(request, initialRecord);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialRecord);
            //loadGridHeader(request);

            // prepare return values
            writeAjaxXmlResponse(response, initialRecord, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial agent data.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddAgent", af);
        return af;
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

   public ActionForward getInitialValuesForAgent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
           Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAgent", new Object[]{mapping, form, request, response});
        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // get default agent data for a given agent
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record agentRecord = getAgentManager().getInitialValuesForPolicyAgent(policyHeader, getInputRecord(request));
            writeAjaxXmlResponse(response, agentRecord, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get Initial Values ForAgent.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAgent", af);
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
