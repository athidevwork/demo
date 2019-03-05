package dti.ci.agentmgr.struts;

import dti.ci.agentmgr.AgentFields;
import dti.ci.agentmgr.AgentManager;
import dti.ci.agentmgr.impl.MaintainAgentEntitlementRecordLoadProcessor;
import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.entitymgr.EntityFields;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for maintain agent
 * <p/>
 * This is is totally rewritten and original Class was renamed to "MaintainPolicyAgentAction".
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 26, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Mar 21, 2008     James       CreatedAdd Agent Tab to ECIS.
 *                              Same functionality, look and feel
 * Apr 17, 2008     James       Issue#75265 Modify code according to code review
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * 08/13/2009       Guang       94091: refactored getLovLabelsForInitialValues into CIBaseAction
 * 07/01/2013       hxk         Issue 141840
 *                              Add pk to request, which CIS security needs.
 * 07/15/2016       iwang       177546: 1) Modify loadAllAgent to load agents and agent overrides information.
 *                                      2) Modify saveAllAgent to save agents and agent overrides information
 * 05/31/2018       ylu         109213: 1) refactor update for fix save error.
 *                                      2) remove some code as they are handled by CIBaseAction
 * ---------------------------------------------------
 */
public class MaintainAgentAction extends MaintainEntityFolderBaseAction {

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
    public ActionForward loadAllAgent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAgent", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);
            /* validate */
            String entityId = request.getParameter(PK_PROPERTY);

            setEntityCommonInfoToRequest(request, inputRecord);
            request.setAttribute(ENTITY_FK_PROPERTY, entityId);
            EntityFields.setEntityId(inputRecord, entityId);

            Record agentRecord = getAgentManager().loadAllAgent(inputRecord);
            //set agency id for query grid list
            if (agentRecord.hasStringValue(AgentFields.AGENT_ID)) {
                AgentFields.setAgentId(inputRecord, AgentFields.getAgentId(agentRecord));
            } else {
                AgentFields.setAgentId(inputRecord, "-1");
                // get the default values from the workbench configuration for this page
                Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(this.getClass().getName());
                agentRecord.setFields(defaultValuesRecord, false);
            }

            // Set entitlement values for agency
            MaintainAgentEntitlementRecordLoadProcessor.setEntitlementValuesForAgent(agentRecord);

            // load agency pay commisstion and set the dataBean to request
            RecordSet rs = (RecordSet) request.getAttribute(MaintainAgentAction.AGENT_PAY_COMMISSION_GRID_RECORD_SET);
            if (rs == null) {
                rs = getAgentManager().loadAllAgentPayCommission(inputRecord);
                publishOutputRecord(request, agentRecord);
            }
            setDataBean(request, rs, MaintainAgentAction.AGENT_PAY_COMMISSION_GRID_ID);
            Record outputRecord = rs.getSummaryRecord();
            publishOutputRecord(request, outputRecord);
            // Set currentGridId to componentListGrid before load component gird header
            RequestStorageManager.getInstance().set(MaintainAgentAction.CURRENT_GRID_ID, MaintainAgentAction.AGENT_PAY_COMMISSION_GRID_ID);
            // Load agency pay commission grid header
            loadGridHeader(request, null, MaintainAgentAction.AGENT_PAY_COMMISSION_GRID_ID, MaintainAgentAction.AGENT_PAY_COMMISSION_GRID_LAYER_ID);

            // load agency contract and set the dataBean to request
            rs = (RecordSet) request.getAttribute(MaintainAgentAction.AGENT_CONTRACT_GRID_RECORD_SET);
            if (rs == null) {
                rs = getAgentManager().loadAllAgentContract(inputRecord);
            }
            setDataBean(request, rs, MaintainAgentAction.AGENT_CONTRACT_GRID_ID);
            outputRecord = rs.getSummaryRecord();
            publishOutputRecord(request, outputRecord);
            // Set currentGridId to componentListGrid before load component gird header
            RequestStorageManager.getInstance().set(MaintainAgentAction.CURRENT_GRID_ID, MaintainAgentAction.AGENT_CONTRACT_GRID_ID);
            // Load agency contract grid header
            loadGridHeader(request, null, MaintainAgentAction.AGENT_CONTRACT_GRID_ID, MaintainAgentAction.AGENT_CONTRACT_GRID_LAYER_ID);

            // load agency contract commisstion and set the dataBean to request
            rs = (RecordSet) request.getAttribute(MaintainAgentAction.AGENT_CONTRACT_COMMISSION_GRID_RECORD_SET);
            if (rs == null) {
                rs = getAgentManager().loadAllAgentContractCommission(inputRecord);
            }
            setDataBean(request, rs, MaintainAgentAction.AGENT_CONTRACT_COMMISSION_GRID_ID);
            outputRecord = rs.getSummaryRecord();
            publishOutputRecord(request, outputRecord);
            // Set currentGridId to componentListGrid before load component gird header
            RequestStorageManager.getInstance().set(MaintainAgentAction.CURRENT_GRID_ID, MaintainAgentAction.AGENT_CONTRACT_COMMISSION_GRID_ID);
            // Load agency contract commission grid header
            loadGridHeader(request, null, MaintainAgentAction.AGENT_CONTRACT_COMMISSION_GRID_ID, MaintainAgentAction.AGENT_CONTRACT_COMMISSION_GRID_LAYER_ID);

            // load agent staffs and set the dataBean to request
            rs = (RecordSet) request.getAttribute(MaintainAgentAction.AGENT_STAFF_GRID_RECORD_SET);
            if (rs == null) {
                rs = getAgentManager().loadAllAgentStaff(inputRecord);
            }
            setDataBean(request, rs, MaintainAgentAction.AGENT_STAFF_GRID_ID);
            outputRecord = rs.getSummaryRecord();
            publishOutputRecord(request, outputRecord);
            // Set currentGridId to agentStaffListGrid before load component gird header
            RequestStorageManager.getInstance().set(MaintainAgentAction.CURRENT_GRID_ID, MaintainAgentAction.AGENT_STAFF_GRID_ID);
            // Load agent staff grid header
            loadGridHeader(request, null, MaintainAgentAction.AGENT_STAFF_GRID_ID, MaintainAgentAction.AGENT_STAFF_GRID_LAYER_ID);

            // load agent staff overrides and set the dataBean to request
            rs = (RecordSet) request.getAttribute(MaintainAgentAction.AGENT_STAFF_OVERRIDE_GRID_RECORD_SET);
            if (rs == null) {
                rs = getAgentManager().loadAllAgentStaffOverride(inputRecord);
            }
            setDataBean(request, rs, MaintainAgentAction.AGENT_STAFF_OVERRIDE_GRID_ID);
            outputRecord = rs.getSummaryRecord();
            publishOutputRecord(request, outputRecord);
            // Set currentGridId to agentStaffOverrideListGrid before load component gird header
            RequestStorageManager.getInstance().set(MaintainAgentAction.CURRENT_GRID_ID, MaintainAgentAction.AGENT_STAFF_OVERRIDE_GRID_ID);
            // Load agent overrides grid header
            loadGridHeader(request, null, MaintainAgentAction.AGENT_STAFF_OVERRIDE_GRID_ID, MaintainAgentAction.AGENT_STAFF_OVERRIDE_GRID_LAYER_ID);

            saveToken(request);

            // load list of values
            loadListOfValues(request, form);

            //add js message
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the agent information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAgent", af);
        return af;
    }

    /**
     * Save all new and modified agent records.
     */
    public ActionForward saveAllAgent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAgent", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet payCommissionRecords = null;
        RecordSet contractRecords = null;
        RecordSet contractCommissionRecords = null;
        RecordSet agentStaffRecords = null;
        RecordSet agentOverrideRecords = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form);

                Record inputRecord = getInputRecord(request);
                EntityFields.setEntityId(inputRecord, inputRecord.getStringValue(ENTITY_FK_PROPERTY));

                // Primary Risk Type Code visibility
                OasisFields fieldsMap = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);

                OasisFormField primaryRiskTypeCode = (OasisFormField) fieldsMap.getLayerFieldsMap(MaintainAgentAction.AGENT_CONTRACT_COMMISSION_DETAIL_LAYER_ID).get(AgentFields.CONTRACT_COMMISSION_PRIMARY_RISK_TYPE_CODE);

                if (!primaryRiskTypeCode.getIsVisible()) {
                    inputRecord.setFieldValue(AgentFields.IS_PRIMARY_RISK_TYPE_CODE_VISIBLE, YesNoFlag.N);
                } else {
                    inputRecord.setFieldValue(AgentFields.IS_PRIMARY_RISK_TYPE_CODE_VISIBLE, YesNoFlag.Y);
                }

                // Map textXML to RecordSet for input
                RequestStorageManager.getInstance().set(MaintainAgentAction.CURRENT_GRID_ID, MaintainAgentAction.AGENT_PAY_COMMISSION_GRID_ID);
                payCommissionRecords = getInputRecordSet(request, MaintainAgentAction.AGENT_PAY_COMMISSION_GRID_ID);

                RequestStorageManager.getInstance().set(MaintainAgentAction.CURRENT_GRID_ID, MaintainAgentAction.AGENT_CONTRACT_GRID_ID);
                contractRecords = getInputRecordSet(request, MaintainAgentAction.AGENT_CONTRACT_GRID_ID);

                RequestStorageManager.getInstance().set(MaintainAgentAction.CURRENT_GRID_ID, MaintainAgentAction.AGENT_CONTRACT_COMMISSION_GRID_ID);
                contractCommissionRecords = getInputRecordSet(request, MaintainAgentAction.AGENT_CONTRACT_COMMISSION_GRID_ID);

                RequestStorageManager.getInstance().set(MaintainAgentAction.CURRENT_GRID_ID, MaintainAgentAction.AGENT_STAFF_GRID_ID);
                agentStaffRecords = getInputRecordSet(request, MaintainAgentAction.AGENT_STAFF_GRID_ID);

                RequestStorageManager.getInstance().set(MaintainAgentAction.CURRENT_GRID_ID, MaintainAgentAction.AGENT_STAFF_OVERRIDE_GRID_ID);
                agentOverrideRecords = getInputRecordSet(request, MaintainAgentAction.AGENT_STAFF_OVERRIDE_GRID_ID);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllAgent", "Saving the agent inputRecords: "
                            + inputRecord + payCommissionRecords + contractRecords + contractCommissionRecords);
                }

                // Save the changes
                getAgentManager().saveAllAgent(inputRecord, payCommissionRecords, contractRecords,
                                                 contractCommissionRecords, agentStaffRecords, agentOverrideRecords);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request, so loadAllAgent does not
            //  AgentDAO to get recordset again.
            request.setAttribute(MaintainAgentAction.AGENT_PAY_COMMISSION_GRID_RECORD_SET, payCommissionRecords);
            request.setAttribute(MaintainAgentAction.AGENT_CONTRACT_GRID_RECORD_SET, contractRecords);
            request.setAttribute(MaintainAgentAction.AGENT_CONTRACT_COMMISSION_GRID_RECORD_SET, contractCommissionRecords);
            request.setAttribute(MaintainAgentAction.AGENT_STAFF_GRID_RECORD_SET, agentStaffRecords);
            request.setAttribute(MaintainAgentAction.AGENT_STAFF_OVERRIDE_GRID_RECORD_SET, agentOverrideRecords);
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
     * This method is called by AJAX to get initial values for a agent
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
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);
            Record initialRecord = getAgentManager().getInitialValuesForAddAgent(inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, initialRecord);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialRecord);

            // prepare return values
            writeAjaxXmlResponse(response, initialRecord, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial agent pay commission data.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddAgent", af);
        return af;
    }

    /**
     * verify config.
     */
    public void verifyConfig() {
        super.verifyConfig();
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getAgentManager() == null)
            throw new ConfigurationException("The required property 'agentManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }


    /**
     * add js message
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.agentmgr.maintainAgent.modifyAgentStatus");

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
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.dateOfBirth.after");
        MessageManager.getInstance().addJsMessage("ci.common.error.certifiedDate.after");
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        Logger l = LogUtils.enterLog(getClass(), "getAnchorColumnName");
        String anchorName;
        if (RequestStorageManager.getInstance().has(MaintainAgentAction.CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(MaintainAgentAction.CURRENT_GRID_ID);
            if (currentGridId.equals(MaintainAgentAction.AGENT_CONTRACT_GRID_ID)) {
                anchorName = getContractAnchorColumnName();
            } else if (currentGridId.equals(MaintainAgentAction.AGENT_CONTRACT_COMMISSION_GRID_ID)) {
                anchorName = getContractCommissionAnchorColumnName();
            } else if (currentGridId.equals(MaintainAgentAction.AGENT_STAFF_GRID_ID)) {
                anchorName = getAgentStaffAnchorColumnName();
            } else if (currentGridId.equals(MaintainAgentAction.AGENT_STAFF_OVERRIDE_GRID_ID)) {
                anchorName = getAgentStaffOverrideAnchorColumnName();
            } else {
                anchorName = super.getAnchorColumnName();
            }
        } else {
            anchorName = super.getAnchorColumnName();
        }
        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }

    public AgentManager getAgentManager() {
        return m_agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        m_agentManager = agentManager;
    }

    public String getContractAnchorColumnName() {
        return m_contractAnchorColumnName;
    }

    public void setContractAnchorColumnName(String contractAnchorColumnName) {
        m_contractAnchorColumnName = contractAnchorColumnName;
    }

    public String getContractCommissionAnchorColumnName() {
        return m_contractCommissionAnchorColumnName;
    }

    public void setContractCommissionAnchorColumnName(String contractCommissionAnchorColumnName) {
        m_contractCommissionAnchorColumnName = contractCommissionAnchorColumnName;
    }

    public String getAgentStaffAnchorColumnName() {
        return m_agentStaffAnchorColumnName;
    }

    public void setAgentStaffAnchorColumnName(String agentStaffAnchorColumnName) {
        m_agentStaffAnchorColumnName = agentStaffAnchorColumnName;
    }

    public String getAgentStaffOverrideAnchorColumnName() {
        return m_agentStaffOverrideAnchorColumnName;
    }

    public void setAgentStaffOverrideAnchorColumnName(String agentStaffOverrideAnchorColumnName) {
        m_agentStaffOverrideAnchorColumnName = agentStaffOverrideAnchorColumnName;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private AgentManager m_agentManager;

    private String m_contractAnchorColumnName;
    private String m_contractCommissionAnchorColumnName;
    private String m_agentStaffAnchorColumnName;
    private String m_agentStaffOverrideAnchorColumnName;
    private WorkbenchConfiguration m_workbenchConfiguration;

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String AGENT_PAY_COMMISSION_GRID_ID = "payListGrid";
    protected static final String AGENT_CONTRACT_GRID_ID = "contractListGrid";
    protected static final String AGENT_CONTRACT_COMMISSION_GRID_ID = "commissionListGrid";
    protected static final String AGENT_PAY_COMMISSION_GRID_LAYER_ID = "CI_AGENT_PAY_COMMISSION_GH";
    protected static final String AGENT_CONTRACT_GRID_LAYER_ID = "CI_AGENT_CONTRACT_GH";
    protected static final String AGENT_CONTRACT_COMMISSION_GRID_LAYER_ID = "CI_AGENT_CONTRACT_COMMISSION_GH";
    protected static final String AGENT_CONTRACT_COMMISSION_DETAIL_LAYER_ID = "CI_AGENT_CONTRACT_COMMISSION_DETAIL";
    protected static final String AGENT_PAY_COMMISSION_GRID_RECORD_SET = "payListGridRecordSet";
    protected static final String AGENT_CONTRACT_GRID_RECORD_SET = "contractListGridRecordSet";
    protected static final String AGENT_CONTRACT_COMMISSION_GRID_RECORD_SET = "commissionListGridRecordSet";
    protected static final String AGENT_STAFF_GRID_ID = "agentStaffListGrid";
    protected static final String AGENT_STAFF_OVERRIDE_GRID_ID = "agentStaffOverrideListGrid";
    protected static final String AGENT_STAFF_GRID_RECORD_SET = "agentStaffListGridRecordSet";
    protected static final String AGENT_STAFF_OVERRIDE_GRID_RECORD_SET = "agentStaffOverrideListGridRecordSet";
    protected static final String AGENT_STAFF_GRID_LAYER_ID = "CI_AGENT_STAFF_GH";
    protected static final String AGENT_STAFF_OVERRIDE_GRID_LAYER_ID = "CI_AGENT_STAFF_OVERRIDE_GH";
}
