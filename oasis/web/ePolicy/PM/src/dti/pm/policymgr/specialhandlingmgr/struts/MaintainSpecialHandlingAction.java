package dti.pm.policymgr.specialhandlingmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.specialhandlingmgr.SpecialHandlingManager;
import dti.pm.policymgr.PolicyHeader;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.http.RequestIds;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for maintain Special Handling
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 15, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/02/2013       jyang       149171 - roll back 141758's change to load LOV label fields value in
 *                              getInitialValuesForSpecialHandling method.
 * ---------------------------------------------------
 */

public class MaintainSpecialHandlingAction extends PMBaseAction {

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
        return loadAllSpecialHandlings(mapping, form, request, response);
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
    public ActionForward loadAllSpecialHandlings(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSpecialHandlings", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadShResult";
        try {

            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Gets all special handlings
            RecordSet rs = (RecordSet)request.getAttribute(RequestIds.GRID_RECORD_SET) ;
            if (rs== null) {
                rs = getSpecialhandlingManager().loadAllSpecialHandlings(policyHeader);
            }

            // Sets data bean
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Loads list of values
            loadListOfValues(request, form);            

            // Add Js messages
            addJsMessages();

            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllSpecialHandlings page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllSpecialHandlings", af);
        return af;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainSpecialHandling.startDateOutsideTermDates.error");
        MessageManager.getInstance().addJsMessage("pm.maintainSpecialHandling.endDateOutsideTermDates.error");
    }

    public ActionForward getInitialValuesForSpecialHandling(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForSpecialHandling",
            new Object[]{mapping, form, request, response});


        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get the initial values
            Record initialValuesRec = getSpecialhandlingManager().getInitialValuesForSpecialHandling(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for special handling.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForSpecialHandling", af);
        return af;
    }

    /**
     * Method to save all special handlings.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllSpecialHandlings(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSpecialHandlings", new Object[]{mapping, form, request, response});
        ActionForward af;
        String forwardString = "saveShResult";
        RecordSet inputRecords = null;
        try {
            // If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form, false);

                // Parse the Grid XML as a RecordSet, and the request parameters as the Summary Record
                inputRecords = getInputRecordSet(request);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllSpecialHandlings",
                        "Saving the Special Handling input records: " + inputRecords);
                }

                // Pull the policy header from request
                PolicyHeader policyHeader = getPolicyHeader(request);

                // Call the business component to implement the validate/save logic
                getSpecialhandlingManager().saveAllSpecialHandlings(policyHeader, inputRecords);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Special Handling page.", e, request, mapping);
        }

        af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllSpecialHandlings", af);
        return af;
    }

    public SpecialHandlingManager getSpecialhandlingManager() {
        return m_specialhandlingManager;
    }

    public void setSpecialhandlingManager(SpecialHandlingManager specialhandlingManager) {
        m_specialhandlingManager = specialhandlingManager;
    }

    private SpecialHandlingManager m_specialhandlingManager;
}
