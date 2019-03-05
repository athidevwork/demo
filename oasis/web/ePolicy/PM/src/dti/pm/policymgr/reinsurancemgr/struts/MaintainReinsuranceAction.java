package dti.pm.policymgr.reinsurancemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.reinsurancemgr.ReinsuranceFields;
import dti.pm.policymgr.reinsurancemgr.ReinsuranceManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for maintain reinsurance
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 30, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/02/2011       ryzhao      123408 - Added addJsMessages() method.
 * 12/02/2013       jyang       149171 - Roll back 141758's change to load LOV label fields' value in
 *                                       getInitialValuesForXXX method.
 * 11/25/2014       kxiang      158853 - Set href value to nameHref in method getInitialValuesForReinsurance.
 * ---------------------------------------------------
 */

public class MaintainReinsuranceAction extends PMBaseAction {

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
        return loadAllReinsurance(mapping, form, request, response);
    }

    /**
     * Method to load all special handlings for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllReinsurance(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllReinsurance", new Object[]{mapping, form, request, response});
        }
        String forwardString = "loadResult";
        try {
            securePage(request, form);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Gets all special handlings
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getReinsuranceManager().loadAllReinsurance(policyHeader);
            }
            // Sets data bean
            setDataBean(request, rs);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            // Loads list of values
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
            // Add js message
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllReinsurance page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllReinsurance", af);
        return af;
    }

    /**
     * get initial values for Reinsurance
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForReinsurance(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValueForReinsurance",
            new Object[]{mapping, form, request, response});


        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            // Get the initial values
            Record initialValuesRec = getReinsuranceManager().getInitialValuesForReinsurance(policyHeader, inputRecord);

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField reinsuranceField = fields.getField(ReinsuranceFields.REINSURER_ENTITY_ID_GH);
            ReinsuranceFields.setReinsurerEntityIdHref(initialValuesRec, reinsuranceField.getHref());

            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial value for reinsurance.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValueForReinsurance", af);
        return af;
    }

    /**
     * Method to save all reinsurance.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllReinsurance(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllReinsurance", new Object[]{mapping, form, request, response});
        ActionForward af;
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        try {
            // If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form, false);
                // Parse the Grid XML as a RecordSet, and the request parameters as the Summary Record
                inputRecords = getInputRecordSet(request);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllReinsurance",
                        "Saving the reinsurance input records: " + inputRecords);
                }

                // Pull the policy header from request
                PolicyHeader policyHeader = getPolicyHeader(request);

                // Call the business component to implement the validate/save logic
                getReinsuranceManager().saveAllReinsurance(policyHeader, inputRecords);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the reinsurance page.", e, request, mapping);
        }

        af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllReinsurance", af);
        return af;
    }

    /**
     * add js messages for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainReinsurance.invalidParticipationPercent");
    }

    public ReinsuranceManager getReinsuranceManager() {
        return m_reinsuranceManager;
    }

    public void setReinsuranceManager(ReinsuranceManager reinsuranceManager) {
        m_reinsuranceManager = reinsuranceManager;
    }

    private ReinsuranceManager m_reinsuranceManager;
}
