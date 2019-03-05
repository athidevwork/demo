package dti.pm.policymgr.underwritermgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.underwritermgr.UnderwriterManager;
import dti.pm.policymgr.underwritermgr.UnderwritingFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Underwriter.
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 19, 2006
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/25/2011       syang       127661 - Modified loadTermUnderwriters() to set dataSavedB to request, it will be used to
 *                              refresh parent page when close underwriter page.
 * 06/05/2013       awu         138241 - 1. Add addUnderwriterTeam, addTeamMembers, getUnderwriterTeam, addJsMessages
 *                                       2. Modified saveAllUnderwriters, loadTermUnderwriters to set current record .
 * 07/30/2013       awu         147025 - Modified addJsMessages to add a new message.
 * 12/02/2013       jyang       149171 - Roll back 141758's change to load LOV label fields' value in
 *                                       getInitialValuesForXXX method.
 * 12/26/2014       kxiang      158853 - Set href value to nameHref in method loadTermUnderwriters.
 * 12/30/2014       jyang       159787 - Removed unused method getInitialValuesForUnderwriter();
 * 08/18/2015       awu                - Add back the method getInitialValuesForUnderwriter(), it was removed
 *                                       by issue 159787 by mistake. This method will be called when click
 *                                       Add Member button (This button is inactive in Base).
 * 07/08/2016       bwang       177940 - Made code changes to set a flag to indicates the Additional Info is changed.
 * 03/22/2017       eyin        180675 - Added logic to show/hide button 'Show All' && 'Show Term' in tab style.
 * 01/29/2018       wrong       191120 - Modified saveAllUnderwriters() to set value for 'isSaveAction' field.
 * ---------------------------------------------------
 */

public class MaintainUnderwriterAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter
     * sent in along the requested url.
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
        return loadTermUnderwriters(mapping, form, request, response);
    }

    /**
     * Method to load list of underwriters for requested policy.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllUnderwriters(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllUnderwriters",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadUwResult";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Gets additional policy information
            Record additionalPolicyInfo = getUnderwriterManager().loadAdditionalPolicyInfo(policyHeader);
            // Publishes additional policy information
            publishOutputRecord(request, additionalPolicyInfo);

            // Loads all underwriter Data
            RecordSet rs = getUnderwriterManager().loadAllUnderwriters(policyHeader);
            // Sets data Bean
            setDataBean(request, rs);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, rs.getSummaryRecord());

            // Load LOVs
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
            // publish other fields
            request.setAttribute("showingAll", "Y");
            String pmUIStyle = SysParmProvider.getInstance().getSysParm("PM_UI_STYLE", "T");
            if(pmUIStyle.equals("T")){
                request.setAttribute("showingAllTabStyle", "Y");
                request.setAttribute("showingTermTabStyle", "Y");
            }else{
                request.setAttribute("showingAllTabStyle", "Y");
                request.setAttribute("showingTermTabStyle", "N");
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllUnderwriter page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllUnderwriters", af);
        return af;
    }

    /**
     * Method to load list of underwriters by term for requested policy.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadTermUnderwriters(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadTermUnderwriters",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadUwResult";

        try {
            RecordSet rs;
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Attempt to get the gridRecordSet out of the request.  This will be populated
            // on a validation error to provide data to reload the page.
            rs = (RecordSet) request.getAttribute(GRID_RECORD_SET);

            if (rs == null) {
                // Gets additional policy information
                Record additionalPolicyInfo = getUnderwriterManager().loadAdditionalPolicyInfo(policyHeader);
                // Publishes additional policy information
                publishOutputRecord(request, additionalPolicyInfo);

                // Loads term underwriter
                rs = getUnderwriterManager().loadUnderwritersByTerm(policyHeader);
                // Set change flag
                request.setAttribute("addlPolicyInfoChangedB", "N");
            }
            // set entityId href
            Iterator iterator = rs.getRecords();
            while (iterator.hasNext()) {
                Record record = (Record) iterator.next();
                OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
                OasisFormField entityIdField = fields.getField(UnderwritingFields.ENTITY_ID_GH);
                UnderwritingFields.setEntityIdHref(record, entityIdField.getHref());
            }
            // Sets data Bean
            setDataBean(request, rs);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, rs.getSummaryRecord());
            // Load LOV
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
            // Add js messages to messagemanager for the current request
            addJsMessages();
            // Publish other fields
            request.setAttribute("showingAll", "N");
            String pmUIStyle = SysParmProvider.getInstance().getSysParm("PM_UI_STYLE", "T");
            if(pmUIStyle.equals("T")){
                request.setAttribute("showingAllTabStyle", "N");
                request.setAttribute("showingTermTabStyle", "N");
            }else{
                request.setAttribute("showingAllTabStyle", "Y");
                request.setAttribute("showingTermTabStyle", "N");
            }
            // The dataSavedB exists in inputRecord means it is called after saved.
            if(getInputRecord(request).hasField(DATA_SAVE_B)){
                request.setAttribute(DATA_SAVE_B, YesNoFlag.Y);
            }
            //Set the dataSavedB to N when this method is called after add underwriter.
            YesNoFlag setDataSaveB = (YesNoFlag) request.getAttribute(LOAD_DATA_AFTER_ADD_UW);
            if (setDataSaveB != null && setDataSaveB.booleanValue()) {
                request.setAttribute(DATA_SAVE_B, YesNoFlag.N);
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadTermUnderwriters page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadTermUnderwriters", af);
        return af;
    }

    /**
     * Save all underwriters
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllUnderwriters(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "save", new Object[]{mapping, form, request, response});

        String forwardString = "saveUwResult";
        PolicyHeader policyHeader;
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form, false);

                // Generate input records
                inputRecords = getInputRecordSet(request);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllUnderwriter",
                        "Saving the Underwriters inputRecords: " + inputRecords);
                }

                // Pull the policy header from the request
                policyHeader = getPolicyHeader(request);
                // Issue 191120 indicate the current action is a saving action.
                request.setAttribute(IS_SAVE_ACTION, YesNoFlag.Y);

                // Call the business component to implement the validate/save logic
                getUnderwriterManager().saveAllUnderwriters(policyHeader, inputRecords);
            }

        }
        catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute("gridRecordSet", inputRecords);

            // Handle the validation exception
            handleValidationException(v, request);
        }

        catch (Exception e) {
            handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Underwriter page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "save", af);
        return af;
    }

    /**
     * This method will be called when click 'Add Member' button.
     * 'Add Member' button is inactive in Base.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForUnderwriter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForUnderwriter", new Object[]{mapping, form, request, response});
        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            // Get the initial values
            Record initialValuesRec = getUnderwriterManager().getInitialValues(inputRecord);
            writeAjaxXmlResponse(response, initialValuesRec, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get underwriter initial values for new record.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForUnderwriter", af);
        return af;
    }

    /**
     * Get the underwriter's team info.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getUnderwriterTeam(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getUnderwriterTeam", new Object[]{mapping, form, request, response});
        PolicyHeader policyHeader;
        try {
            securePage(request, form, false);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get the initial values
            Record teamRec = getUnderwriterManager().getUnderwriterTeam(inputRecord);

            writeAjaxXmlResponse(response, teamRec, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get underwriter team code.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "getUnderwriterTeam", af);
        return af;
    }

    /**
     * Load out the underwriter's team members and return to page.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addUnderwriterTeam(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "addUnderwriterTeam", new Object[]{mapping, form, request, response});
        RecordSet inputRecords;
        String forwardString = "saveUwResult";
        try {
            securePage(request, form, false);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            PolicyHeader policyHeader = getPolicyHeader(request);
            inputRecords = getInputRecordSet(request, UNDERWRITER_GRID_ID);
            getUnderwriterManager().addUnderwriterTeam(inputRecord, inputRecords, policyHeader);
            request.setAttribute(GRID_RECORD_SET, inputRecords);
            request.setAttribute(LOAD_DATA_AFTER_ADD_UW, YesNoFlag.Y);
            request.setAttribute("addlPolicyInfoChangedB", inputRecord.getStringValue("addlPolicyInfoChangedB"));
        }
        catch (Exception e) {
            handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to add the Underwriter team members.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "addUnderwriterTeam", af);
        return af;
    }

    /**
     * Reassociate the policy with underwriter's team members.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addTeamMembers(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "addTeamMembers", new Object[]{mapping, form, request, response});
        RecordSet inputRecords;
        String forwardString = "saveUwResult";
        try {
            securePage(request, form);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            PolicyHeader policyHeader = getPolicyHeader(request);
            inputRecords = getInputRecordSet(request, UNDERWRITER_GRID_ID);
            RecordSet outputSet = getUnderwriterManager().addTeamMembers(inputRecord, inputRecords, policyHeader);
            request.setAttribute(GRID_RECORD_SET, outputSet);
            request.setAttribute(LOAD_DATA_AFTER_ADD_UW, YesNoFlag.Y);
        }
        catch (Exception e) {
            handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Underwriter members when change underwriter.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "addTeamMembers", af);
        return af;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainUnderwriter.expireOtherRoles.confirm");
        MessageManager.getInstance().addJsMessage("pm.maintainUnderwriter.addUnderwriter.indicator");
        MessageManager.getInstance().addJsMessage("pm.maintainUnderwriter.invalidSelectedEntity.error");
    }

    public MaintainUnderwriterAction() {
    }

    public UnderwriterManager getUnderwriterManager() {
        return m_underwriterManager;
    }

    public void setUnderwriterManager(UnderwriterManager underwriterManager) {
        m_underwriterManager = underwriterManager;
    }

    private UnderwriterManager m_underwriterManager;
    private static final String DATA_SAVE_B = "dataSavedB";
    private static final String IS_SAVE_ACTION = "isSaveAction";
    private static final String UNDERWRITER_GRID_ID = "underwriterListGrid";
    private static final String GRID_RECORD_SET = "gridRecordSet";
    private static final String LOAD_DATA_AFTER_ADD_UW = "loadDataAfterAddUW";
}
