package dti.ci.orggroupmgr.struts;

import dti.ci.core.CIFields;
import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.entitymgr.EntityFields;
import dti.ci.orggroupmgr.OrgGroupManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 16, 2009
 *
 * @author msnadar
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/28/2009       Leo         Issue 95771
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 10/31/2014       Elvin       Issue 157727: add process = clear to response clear button
 * 06/12/2018       dpang       Issue 193846: Refactor Org/Group page.
 * 06/28/2018       ylu         Issue 194117: update for CSRF security.
 * ---------------------------------------------------
 */
public class OrgGroupViewAction extends MaintainEntityFolderBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

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
        return loadOrgGroup(mapping, form, request, response);
    }

    /**
     * This method is called to display Org/Group page
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
    public ActionForward loadOrgGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadOrgGroup", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadOrgGroup";
        try {
            // Secure access to the page, load the Oasis Fields
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            commonLoad(inputRecord, request, form);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the orgGroup information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadOrgGroup", af);
        return af;
    }

    /**
     * Clear all inputs to re-display page with default values
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
    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "clear", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadOrgGroup";
        try {
            // Secure access to the page, load the Oasis Fields
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            commonLoad(getRecordWithFieldDefaultValue(inputRecord), request, form);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the orgGroup information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "clear", af);
        return af;
    }

    /**
     * This method is called to display orgGroup print type select page.
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
    public ActionForward printOrgGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "printOrgGroup", new Object[]{mapping, form, request, response});
        }

        String forwardString = "printOrgGroup";
        try {
            // Secure access to the page without loading fields.
            securePage(request, form, false);

            // set js messages
            addJsMessages();
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load orgGroup print type select page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "printOrgGroup", af);
        return af;
    }

    /**
     * This method is called to display Org Group PDF
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
    public ActionForward generatePDFforOrgGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePDFforOrgGroup", new Object[]{mapping, form, request, response});
        }

        String forwardString = "generatePDFforOrgGroup";
        try {
            // Secure access to the page, load the Oasis Fields
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);

            ByteArrayOutputStream bos = getOrgGroupManager().generatePDFStream(getReadOnlyConnection(), inputRecord);
            response.setContentType("application/pdf");
            response.setContentLength(bos.size());
            ServletOutputStream sos = response.getOutputStream();
            bos.writeTo(sos);
            bos.close();
            sos.flush();
            return null;
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Org Group PDF.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "generatePDFforOrgGroup", af);
        return af;
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        l.entering(getClass().getName(), "getAnchorColumnName");
        String anchorName;
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(GROUP_MEMBERS_GRID_ID)) {
                anchorName = getMembersGridAnchorColumnName();
            } else if (currentGridId.equals(GROUP_MEMBERS_SUMMARY_GRID_ID)) {
                anchorName = getSummaryGridAnchorColumnName();
            } else if (currentGridId.equals(GROUP_MEMBERS_ADDRESS_GRID_ID)) {
                anchorName = getAddressGridAnchorColumnName();
            } else {
                anchorName = super.getAnchorColumnName();
            }
        } else {
            anchorName = super.getAnchorColumnName();
        }

        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }

    /**
     * verify config.
     */
    @Override
    public void verifyConfig() {
        super.verifyConfig();
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getOrgGroupManager() == null)
            throw new ConfigurationException("The required property 'orgGroupManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    @Override
    protected boolean skipRetrieveEntityInfo(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "skipRetrieveEntityInfo", new Object[]{request});
        }

        boolean skip = false;
        Record inputRecord = getInputRecord(request);
        if ("printOrgGroup".equalsIgnoreCase(inputRecord.getStringValueDefaultEmpty(RequestIds.PROCESS))) {
            skip = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "skipRetrieveEntityInfo", new Object[]{skip});
        }
        return skip;
    }

    private void commonLoad(Record inputRecord, HttpServletRequest request, ActionForm form) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "commonLoad", new Object[]{inputRecord, request, form});
        }

        inputRecord.setFieldValue(EntityFields.ENTITY_ID, inputRecord.getStringValueDefaultEmpty(CIFields.PK_PROPERTY));

        //Get Members List
        loadAndSetGridData(inputRecord, request);

        publishOutputRecord(request, inputRecord);

        // load list of values
        loadListOfValues(request, form);

        saveToken(request);

        l.exiting(getClass().getName(), "commonLoad");
    }

    private void loadAndSetGridData(Record inputRecord, HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAndSetGridData", new Object[]{inputRecord, request});
        }

        //Get Members List
        RecordSet rs = getOrgGroupManager().loadAllMember(inputRecord);
        setGridData(rs, request, GROUP_MEMBERS_GRID_ID, GROUP_MEMBERS_GRID_LAYER_ID);

        RecordSet rsSummary = getOrgGroupManager().loadSummary(inputRecord);
        setGridData(rsSummary, request, GROUP_MEMBERS_SUMMARY_GRID_ID, GROUP_MEMBERS_SUMMARY_GRID_LAYER_ID);

        RecordSet rsGroupAddress = getOrgGroupManager().loadAddress(inputRecord);
        setGridData(rsGroupAddress, request, GROUP_MEMBERS_ADDRESS_GRID_ID, GROUP_MEMBERS_ADDRESS_GRID_LAYER_ID);

        l.exiting(getClass().getName(), "loadAndSetGridData");
    }

    private void setGridData(RecordSet rs, HttpServletRequest request, String gridId, String gridLayerId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setGridData", new Object[]{rs, request, gridId, gridLayerId});
        }

        setDataBean(request, rs, gridId);
        RequestStorageManager.getInstance().set(CURRENT_GRID_ID, gridId);
        loadGridHeader(request, null, gridId, gridLayerId);

        l.exiting(getClass().getName(), "setGridData");
    }

    private Record getRecordWithFieldDefaultValue(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRecordWithFieldDefaultValue", new Object[]{inputRecord});
        }

        Record record = new Record();
        record.setFields(inputRecord);
        // set to empty first, then override if field has default value
        record.setFieldValue(AS_OF_DATE, "");
        record.setFieldValue(MEMBER_TYPE, "");
        record.setFieldValue(MEMBER_STATUS, "");

        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(getClass().getName());
        record.setFields(defaultValuesRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRecordWithFieldDefaultValue", record);
        }
        return record;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.button.unknown");
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public String getAddressGridAnchorColumnName() {
        return addressGridAnchorColumnName;
    }

    public void setAddressGridAnchorColumnName(String addressGridAnchorColumnName) {
        this.addressGridAnchorColumnName = addressGridAnchorColumnName;
    }

    public String getMembersGridAnchorColumnName() {
        return membersGridAnchorColumnName;
    }

    public void setMembersGridAnchorColumnName(String membersGridAnchorColumnName) {
        this.membersGridAnchorColumnName = membersGridAnchorColumnName;
    }

    public String getSummaryGridAnchorColumnName() {
        return summaryGridAnchorColumnName;
    }

    public void setSummaryGridAnchorColumnName(String summaryGridAnchorColumnName) {
        this.summaryGridAnchorColumnName = summaryGridAnchorColumnName;
    }

    public OrgGroupManager getOrgGroupManager() {
        return m_orgGroupManager;
    }

    public void setOrgGroupManager(OrgGroupManager orgGroupManager) {
        this.m_orgGroupManager = orgGroupManager;
    }

    private OrgGroupManager m_orgGroupManager;
    private WorkbenchConfiguration m_workbenchConfiguration;

    private static final String CURRENT_GRID_ID = "currentGridId";
    private String membersGridAnchorColumnName;
    private String summaryGridAnchorColumnName;
    private String addressGridAnchorColumnName;
    private static final String GROUP_MEMBERS_GRID_ID = "membersGrid";
    private static final String GROUP_MEMBERS_SUMMARY_GRID_ID = "summaryGrid";
    private static final String GROUP_MEMBERS_ADDRESS_GRID_ID = "addressGrid";
    private static final String GROUP_MEMBERS_GRID_LAYER_ID = "GROUP_MEMBER_LIST_LAYER";
    private static final String GROUP_MEMBERS_SUMMARY_GRID_LAYER_ID = "GROUP_MEMBER_SUMMARY_LAYER";
    private static final String GROUP_MEMBERS_ADDRESS_GRID_LAYER_ID = "GROUP_ADDRESS_LAYER";
    private static final String AS_OF_DATE = "asOfDate";
    private static final String MEMBER_TYPE = "memberType";
    private static final String MEMBER_STATUS = "memberStatus";
}

