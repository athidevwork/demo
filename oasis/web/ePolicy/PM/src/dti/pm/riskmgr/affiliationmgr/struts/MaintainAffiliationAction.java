package dti.pm.riskmgr.affiliationmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.affiliationmgr.AffiliationFields;
import dti.pm.riskmgr.affiliationmgr.AffiliationManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Affiliation.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 6, 2008
 *
 * @author Simon Li
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/04/2013       Awu         1. getInitialValuesForAffiliation, Modified to set the loading fields flag to false in securePage for Issue141758.
 *                              2. The fields should be loaded in getInitialValuesForAffiliation, or else,
 *                                 problem like issue145732 will be happened. So rollback before changes to load the fields.
 * 11/25/2014       kxiang      158853 - Set href value to nameHref in method getInitialValuesForAffiliation.
 * ---------------------------------------------------
 */
public class MaintainAffiliationAction extends PMBaseAction {
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
        return loadAllAffiliation(mapping, form, request, response);
    }

    /**
     * Method to load list of available Affiliation for requested risk.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllAffiliation(ActionMapping mapping,
                                            ActionForm form,
                                            HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAffiliation", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Load the affiliation info
            RecordSet rs = (RecordSet) request.getAttribute(dti.oasis.http.RequestIds.GRID_RECORD_SET);
            if (rs == null) {

                rs = getAffiliationManager().loadAllAffiliation(policyHeader, inputRecord);
            }
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            loadGridHeader(request);

            // add js messages to messagemanager for the current request
            addJsMessages();

            request.setAttribute("needToCaptureTransaction", output.getStringValue("needToCaptureTransaction"));

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the affiliation page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAffiliation", af);
        return af;
    }

    /**
     * Save all updated affiliation records.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllAffiliation(ActionMapping mapping,
                                            ActionForm form,
                                            HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAffiliation", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                // Get policy header
                PolicyHeader policyHeader = getPolicyHeader(request, true);
                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllAffiliation",
                        "Saving the Affiliation inputRecords: " + inputRecords);
                }
                getAffiliationManager().saveAllAffiliation(policyHeader, inputRecords);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to save affiliation.", e, request, mapping);
        }
        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllAffiliation", af);
        return af;
    }


    /**
     * Get initial values for affiliation
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForAffiliation(ActionMapping mapping,
                                                        ActionForm form,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAffiliation",
            new Object[]{mapping, form, request, response});
        try {
            // Secure page
            securePage(request, form);
            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            // Get the initial values
            Record initialValuesRec = getAffiliationManager().getInitialValuesForAffiliation(policyHeader, inputRecord);

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField organizationNameField = fields.getField(AffiliationFields.ORGANIZATION_NAME_GH);
            AffiliationFields.setOrganizationNameHref(initialValuesRec, organizationNameField.getHref());

            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for affiliation.", e, response);
        }

        l.exiting(getClass().getName(), "getInitialValuesForAffiliation");
        return null;
    }


    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainAffiliation.selectVapB.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainAffiliation.deSelectVapB.warning");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getAffiliationManager() == null)
            throw new ConfigurationException("The required property 'affiliationManager' is missing.");
    }

    public AffiliationManager getAffiliationManager() {
        return m_affiliationManager;
    }

    public void setAffiliationManager(AffiliationManager affiliationManager) {
        m_affiliationManager = affiliationManager;
    }

    public MaintainAffiliationAction() {
    }

    private AffiliationManager m_affiliationManager;
}
