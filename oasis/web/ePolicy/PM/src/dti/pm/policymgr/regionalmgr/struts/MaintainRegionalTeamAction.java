package dti.pm.policymgr.regionalmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.regionalmgr.RegionalTeamFields;
import dti.pm.policymgr.regionalmgr.RegionalTeamManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for maintain regional team and underwriter.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 19, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/02/2013       jyang       149171 - Roll back 141758's change to load LOV label fields' value in
 *                                       getInitialValuesForXXX method.
 * 11/25/2014       kxiang      158853 - Set href value to nameHref in method getInitialValuesForRegionalTeamMember
 * ---------------------------------------------------
 */
public class MaintainRegionalTeamAction extends PMBaseAction {
    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllRegionalTeam(mapping, form, request, response);
    }

    /**
     * Method to load all regional team.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllRegionalTeam(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRegionalTeam", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            // Load the regional team recordSet from request.
            RecordSet teamRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            // Load the team member recordSet from request.
            RecordSet memberRs = (RecordSet) request.getAttribute(TEAM_MEMBER_RECORD_SET);

            if (teamRs == null) {
                teamRs = getRegionalTeamManager().loadAllRegionalTeam(inputRecord);
            }
            if (memberRs == null) {
                memberRs = getRegionalTeamManager().loadAllTeamUnderwriter(inputRecord);
            }
            // Set loaded data into request.
            setDataBean(request, teamRs);
            setDataBean(request, memberRs, TEAM_MEMBER_GRID_ID);

            // Make the Summary Record available for output.
            Record output = teamRs.getSummaryRecord();
            output.setFields(memberRs.getSummaryRecord(), false);
            output.setFields(inputRecord, false);

            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, output);

            // Set currentGridId to every gridID on page before load gird header
            // then load grid header for each grid.
            loadGridHeader(request);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, TEAM_MEMBER_GRID_ID);
            loadGridHeader(request, null, TEAM_MEMBER_GRID_ID, TEAM_MEMBER_GRID_LAYER_ID);

            // Load the list of values after loading the data.
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load maintain regional team page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRegionalTeam", af);
        return af;
    }

    /**
     * Save all regional team and team member.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllRegionalTeamAndUnderwriter(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRegionalTeamAndUnderwriter", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet teamInputRecords = null;
        RecordSet memberInputRecords = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields.
                securePage(request, form, false);
                // Map regiona team textXML to RecordSet for input.
                teamInputRecords = getInputRecordSet(request, REGIONAL_TEAM_GRID_ID);
                // set currentGridId to regionalTeamMemberListGrid before get input recordSet for team member grid.
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, TEAM_MEMBER_GRID_ID);
                // Map team member textXML to RecordSet for input.
                memberInputRecords = getInputRecordSet(request, TEAM_MEMBER_GRID_ID);
                // Save all the regional teams and team members changes.
                getRegionalTeamManager().saveAllRegionalTeamAndUnderwriter(teamInputRecords, memberInputRecords);
                // Set back to regionalTeamListGrid.
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, REGIONAL_TEAM_GRID_ID);
            }
        }
        catch (ValidationException v) {
            // Save the input records into request.
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, REGIONAL_TEAM_GRID_ID);
            request.setAttribute(RequestIds.GRID_RECORD_SET, teamInputRecords);
            request.setAttribute(TEAM_MEMBER_RECORD_SET, memberInputRecords);
            // Handle the validation exception.
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save all regional team and underwriter.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllRegionalTeamAndUnderwriter", af);
        return af;
    }

    /**
     * Get initial values for the regional team when adds a team.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForRegionalTeam(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForRegionalTeam", new Object[]{mapping, form, request, response});
        try {
            // Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            // Get the initial values.
            Record record = getRegionalTeamManager().getInitialValuesForRegionalTeam();
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for regional team .", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForRegionalTeam", af);
        return af;
    }

    /**
     * Get initial values for the regional team member when adds a member.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForRegionalTeamMember(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForRegionalTeamMember", new Object[]{mapping, form, request, response});

        try {
            // Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            // Get initial values for the regional team member.
            Record record = getRegionalTeamManager().getInitialValuesForRegionalTeamMember(inputRecord);

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField entityIdField = fields.getField(RegionalTeamFields.ENTITY_ID_GH);
            RegionalTeamFields.setEntityIdHref(record, entityIdField.getHref());

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            // Set back to regionalTeamListGrid.
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, TEAM_MEMBER_GRID_ID);
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for regional team member.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForRegionalTeamMember", af);
        return af;
    }

    /**
     * Get the underwriter Id.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getUnderwriterId(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getUnderwriterId", new Object[]{mapping, form, request, response});

        try {
            // Secure page
            securePage(request, form, false);
            Record inputRecord = getInputRecord(request);
            // Get the underwriter Id.
            Record record = getRegionalTeamManager().getUnderwriterId(inputRecord);
            writeAjaxXmlResponse(response, record, false);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for get underwriter Id.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getUnderwriterId", af);
        return af;
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
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(TEAM_MEMBER_GRID_ID)) {
                anchorName = getMemberAnchorColumnName();
            }
            else {
                anchorName = super.getAnchorColumnName();
            }
        }
        else {
            anchorName = super.getAnchorColumnName();
        }

        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getMemberAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'memberAnchorColumnName' is missing.");
        if (getRegionalTeamManager() == null)
            throw new ConfigurationException("The required property 'regionalTeamManager' is missing.");
    }

    public RegionalTeamManager getRegionalTeamManager() {
        return m_regionalTeamManager;
    }

    public void setRegionalTeamManager(RegionalTeamManager regionalTeamManager) {
        m_regionalTeamManager = regionalTeamManager;
    }

    public String getMemberAnchorColumnName() {
        return m_memberAnchorColumnName;
    }

    public void setMemberAnchorColumnName(String memberAnchorColumnName) {
        m_memberAnchorColumnName = memberAnchorColumnName;
    }

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String REGIONAL_TEAM_GRID_ID = "regionalTeamListGrid";
    protected static final String TEAM_MEMBER_GRID_ID = "regionalTeamMemberListGrid";
    protected static final String REGIONAL_TEAM_GRID_LAYER_ID = "PM_REGIONAL_TEAM_GH";
    protected static final String TEAM_MEMBER_GRID_LAYER_ID = "PM_TEAM_MEMBER_GH";
    protected static final String REGIONAL_TEAM_RECORD_SET = "regionalTeamRecordSet";
    protected static final String TEAM_MEMBER_RECORD_SET = "regionalTeamMemberRecordSet";

    private RegionalTeamManager m_regionalTeamManager;
    private String m_memberAnchorColumnName;
}
