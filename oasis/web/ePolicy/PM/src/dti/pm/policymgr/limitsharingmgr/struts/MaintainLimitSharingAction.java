package dti.pm.policymgr.limitsharingmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.limitsharingmgr.LimitSharingManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Limit Sharing.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 12, 2007
 *
 * @author rlli
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/16/2010       syang       110383 - Modified saveAllLimitSharing() to set attribute limitShareType and selectedSharedGroupId.
 * 11/15/2010       dzhang      114336 - Modified getAnchorColumnName() to add anchor column for separateLimitListGrid.
 * 08/14/2012       adeng       135972 - Modified loadAllLimitSharing() to address no risk problem, set false into attribute of request.
 * 08/24/2012       adeng       135972 - Reverted the previous change and using new solution to modify getInitialValuesForSharedGroup()
 *                                       to check if has risk and set it into inputRecord, LimitSharingManager.getInitialValuesForSharedGroup()
 *                                       will do further processing.
 * 08/31/2012       adeng       135972 - Roll backed changes,using new solution of hiding Limit Sharing button.
 * 12/02/2013       jyang       149171 - roll back 141758's change to load LOV label fields value in
 *                                       getInitialValuesForSharedGroup and getInitialValuesForSharedDetail methods.
 * 08/24/2015       eyin        165581 - Modified saveAllLimitSharing(), to move the codes where set attribute
 *                                       'limitShareType' in request before save AllLimitSharing data.
 * ---------------------------------------------------
 */

public class MaintainLimitSharingAction extends PMBaseAction {
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
        return loadAllLimitSharing(mapping, form, request, response);
    }

    /**
     * Method to load list of available risk for requested policy.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllLimitSharing(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllLimitSharing", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            securePage(request, form);
            // Get the policy header from the request
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Load the coverages
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getLimitSharingManager().loadAllSharedGroup(policyHeader);
            }
            // Set loaded sharedGroup data into request
            setDataBean(request, rs);
            // Load the group detail
            RecordSet sharedDetailRs = (RecordSet) request.getAttribute(SHARED_DETAIL_RECORD_SET);
            if (sharedDetailRs == null) {
                sharedDetailRs = getLimitSharingManager().loadAllSharedDetail(policyHeader);
            }
            // Set loaded shared detail data into request
            setDataBean(request, sharedDetailRs, SHARED_DETAIL_GRID_ID);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            output.setFields(sharedDetailRs.getSummaryRecord(), false);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            // Load limitSharing grid header
            loadGridHeader(request);
            // Set currentGridId to sharedDetailListGrid before load shared detail gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SHARED_DETAIL_GRID_ID);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            // Load shared detail grid header
            loadGridHeader(request, null, SHARED_DETAIL_GRID_ID, SHARED_DETAIL_GRID_LAYER_ID);
            // Populate messages for javascirpt
            addJsMessages();
            //set share sir column and share sir field visible or not
            boolean visibleFlag = getLimitSharingManager().validateSirVisibility(policyHeader);
            OasisFields fieldsMap = (OasisFields) request.getAttribute("fieldsMap");
            HashMap shareGroupGridFieldsMap = fieldsMap.getLayerFieldsMap("PM_SHARED_GROUP_GH");
            OasisFormField field = (OasisFormField) (shareGroupGridFieldsMap.get("SHARESIRB_GH"));
            field.setIsVisible(visibleFlag);
            HashMap shareGroupFormFieldsMap = fieldsMap.getLayerFieldsMap("PM_SHARED_GROUP_FORM");
            field = (OasisFormField) (shareGroupFormFieldsMap.get("SHARESIRB"));
            field.setIsVisible(visibleFlag);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the limit sharing page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllLimitSharing", af);
        return af;
    }

    /**
     * load all the separate limit data(for bottom grid)
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllSeparateLimit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSeparateLimit", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadSeparateLimit";
        try {
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Load the change detail
            RecordSet rs = getLimitSharingManager().loadAllSeparateLimit(policyHeader);
            // Set loaded change detail data into request
            setDataBean(request, rs, SEPARATE_LIMITS_GRID_ID);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SEPARATE_LIMITS_GRID_ID);
            loadGridHeader(request, null, SEPARATE_LIMITS_GRID_ID, SEPARATE_LIMITS_GRID_LAYER_ID);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the separate limit.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSeparateLimit", af);
        }
        return af;
    }

    /**
     * Save all updated limit sharing records.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllLimitSharing(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllLimitSharing", new Object[]{mapping, form, request, response});

        RecordSet inputRecords = null;
        RecordSet sharedDetailInputRecords = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                // First save shared group records
                // Map shared group textXML to RecordSet for input
                inputRecords = getInputRecordSet(request, SHARED_GROUP_GRID_ID);
                PolicyHeader policyHeader = getPolicyHeader(request);
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SHARED_DETAIL_GRID_ID);
                // Issue 110383, set attribute limitShareType/selectedSharedGroupId if the current process is called before adding group/detail.
                Record inputRecord = getInputRecord(request);
                if(inputRecord.hasStringValue("addForType") && !StringUtils.isBlank(inputRecord.getStringValue("addForType"))){
                    request.setAttribute("limitShareType", inputRecord.getStringValue("addForType"));
                }
                if(inputRecord.hasStringValue("selectedSharedGroupId") && !StringUtils.isBlank(inputRecord.getStringValue("selectedSharedGroupId"))){
                    request.setAttribute("selectedSharedGroupId", inputRecord.getStringValue("selectedSharedGroupId"));
                }
                // Map sharedDetail textXML to RecordSet for input
                sharedDetailInputRecords = getInputRecordSet(request, SHARED_DETAIL_GRID_ID);
                // Save all changes
                getLimitSharingManager().saveAllLimitSharing(policyHeader, inputRecords, sharedDetailInputRecords);
                // Set back to sharedGroupListGrid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SHARED_GROUP_GRID_ID);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SHARED_GROUP_GRID_ID);
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            request.setAttribute(SHARED_DETAIL_RECORD_SET, sharedDetailInputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save the limit Sharing page.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllLimitSharing", af);
        return af;
    }


    /**
     * Get Inital Values for new added shared group
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForSharedGroup(ActionMapping mapping,
                                                        ActionForm form,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForSharedGroup",
            new Object[]{mapping, form, request, response});

        try {
			//Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get inital values
            Record record = getLimitSharingManager().getInitialValuesForSharedGroup(policyHeader, getInputRecord(request));
            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            // Send back xml data
            writeAjaxXmlResponse(response, record);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for shared group.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForSharedGroup", af);
        return af;

    }

    /**
     * Get Inital Values for new added shared detail
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForSharedDetail(ActionMapping mapping,
                                                         ActionForm form,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForSharedDetail",
            new Object[]{mapping, form, request, response});

        try {
			//Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get inital values
            Record record = getLimitSharingManager().getInitialValuesForSharedDetail(policyHeader, getInputRecord(request));
            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SHARED_DETAIL_GRID_ID);
            // Send back xml data
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for shared detail.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForSharedDetail", af);
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
            if (currentGridId.equals(SHARED_DETAIL_GRID_ID)) {
                anchorName = getSharedDetailAnchorColumnName();
            }
            else if (currentGridId.equals(SEPARATE_LIMITS_GRID_ID)) {
                anchorName = getSeparateLimitAnchorColumnName();
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
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainLimitSharing.deleteSharedGroup.error");
        MessageManager.getInstance().addJsMessage("pm.maintainLimitSharing.modifyDetailExpDate.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainLimitSharing.deleteDetailsAndGroup.warning");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getLimitSharingManager() == null)
            throw new ConfigurationException("The required property 'limitSharingManager' is missing.");
        if (getSharedDetailAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'sharedDetailAnchorColumnName' is missing.");
        if (getSeparateLimitAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'separateLimitAnchorColumnName' is missing.");
        if (super.getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public String getSharedDetailAnchorColumnName() {
        return sharedDetailAnchorColumnName;
    }

    public void setSharedDetailAnchorColumnName(String sharedDetailAnchorColumnName) {
        this.sharedDetailAnchorColumnName = sharedDetailAnchorColumnName;
    }

    public String getSeparateLimitAnchorColumnName() {
        return separateLimitAnchorColumnName;
    }

    public void setSeparateLimitAnchorColumnName(String separateLimitAnchorColumnName) {
        this.separateLimitAnchorColumnName = separateLimitAnchorColumnName;
    }


    public LimitSharingManager getLimitSharingManager() {
        return limitSharingManager;
    }

    public void setLimitSharingManager(LimitSharingManager limitSharingManager) {
        this.limitSharingManager = limitSharingManager;
    }


    public MaintainLimitSharingAction() {
    }

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String SHARED_GROUP_GRID_ID = "sharedGroupListGrid";
    protected static final String SHARED_DETAIL_GRID_ID = "sharedDetailListGrid";
    protected static final String SEPARATE_LIMITS_GRID_ID = "separateLimitListGrid";
    protected static final String SHARED_DETAIL_GRID_LAYER_ID = "PM_SHARED_DETAIL_GH";
    protected static final String SEPARATE_LIMITS_GRID_LAYER_ID = "PM_SEPARATE_LIMIT_GH";
    protected static final String SHARED_DETAIL_RECORD_SET = "sharedDetailGridRecordSet";

    private LimitSharingManager limitSharingManager;
    private String sharedDetailAnchorColumnName;
    private String separateLimitAnchorColumnName;
}
