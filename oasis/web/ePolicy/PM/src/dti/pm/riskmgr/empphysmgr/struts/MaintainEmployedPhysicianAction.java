package dti.pm.riskmgr.empphysmgr.struts;

import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.empphysmgr.EmployedPhysicianFields;
import dti.pm.riskmgr.empphysmgr.EmployedPhysicianManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
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
 * Maintain employed physician data
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/20/2011       wqfu        121424 - Modified saveAllEmployedPhysician to load risk header for validation
 * 12/02/2013       jyang       149171 - roll back 141758's change to load LOV label fields value in
 *                                       getInitialValuesForSharedGroup and getInitialValuesForSharedDetail methods.
 * 11/25/2014       kxiang      158853 - Set href value to nameHref in method getInitialValuesForEmployedPhysician.
 * ---------------------------------------------------
 */
public class MaintainEmployedPhysicianAction extends PMBaseAction {
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
        return loadAllEmployedPhysician(mapping, form, request, response);
    }


    /**
     * load page to show employed physician data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllEmployedPhysician(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllEmployedPhysician", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            //get parameters from request
            Record inputRecord = getInputRecord(request);

            // Get the policy header from the request
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Attempt to get the gridRecordSet out of the request.  This will be populated
            // on a validation error to provide data to reload the page.
            RecordSet empPhyRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            // Load the employed physician data
            if (empPhyRs == null) {
                empPhyRs = getEmployedPhysicianManager().loadAllEmployedPhysician(policyHeader, inputRecord);
            }
            setDataBean(request, empPhyRs);

            if (empPhyRs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.noData.error");
            }

            Record output = policyHeader.toRecord();
            output.setFields(inputRecord, false);
            output.setFields(empPhyRs.getSummaryRecord(), true);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load tail grid header
            loadGridHeader(request);

            //add js validation messages
            addJsMessages();

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the emp phy page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllEmployedPhysician", af);
        return af;
    }


    /**
     * save employed physician data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllEmployedPhysician(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllEmployedPhysician", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                PolicyHeader policyHeader = getPolicyHeader(request, true);
                Record inputRecord = getInputRecord(request);

                // First save employed physician records
                inputRecords = getInputRecordSet(request);

                //save all employed physician data
                getEmployedPhysicianManager().saveAllEmployedPhysician(policyHeader, inputRecords, inputRecord);

            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(dti.pm.core.http.RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to save or validate the employed physician page.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllEmployedPhysician", af);
        return af;
    }

    /**
     * for newly add additional insured, provide default values
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward getInitialValuesForEmployedPhysician(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAdditionalInsured",
            new Object[]{mapping, form, request, response});


        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get the initial values
            Record initialValuesRec = getEmployedPhysicianManager().getInitialValuesForEmployedPhysician(
                policyHeader, inputRecord);

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField agentField = fields.getField(EmployedPhysicianFields.RISK_NAME_GH);
            EmployedPhysicianFields.setRiskNameHref(initialValuesRec, agentField.getHref());

            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for employed physician.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAdditionalInsured", af);
        }
        return af;
    }

    /**
     * calcuate fte value when fields are changed on page by AJAX request
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward getValuesForChangedRecord(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getValuesForChangedRecord",
            new Object[]{mapping, form, request, response});


        try {
            // Secure page
            securePage(request, form, false);

            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get the initial values
            Record fteRec = getEmployedPhysicianManager().getChangedValuesForEmployedPhysician(policyHeader, inputRecord);

            writeAjaxXmlResponse(response, fteRec);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get Values For Changed Record.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getValuesForChangedRecord", af);
        }
        return af;
    }


    /**
     * calcuate total fte value when calculate button is clicked on page by AJAX request
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward calculateTotalFte(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "calculateTotalFte",
            new Object[]{mapping, form, request, response});


        try {
            // Secure page
            securePage(request, form, false);

            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);
            // get fte input recordset
            RecordSet inputRecords = getInputRecordSet(request);

            // Get the total fte value
            Record totalFteRec = getEmployedPhysicianManager().calculateTotalFte(policyHeader, inputRecords);

            writeAjaxXmlResponse(response, totalFteRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to calculate total fte value.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "calculateTotalFte", af);
        }
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainEmployedPhysician.endDate.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getEmployedPhysicianManager() == null)
            throw new ConfigurationException("The required property 'employedPhysicianManager' is missing.");
    }

    public EmployedPhysicianManager getEmployedPhysicianManager() {
        return m_employedPhysicianManager;
    }

    public void setEmployedPhysicianManager(EmployedPhysicianManager employedPhysicianManager) {
        m_employedPhysicianManager = employedPhysicianManager;
    }

    private EmployedPhysicianManager m_employedPhysicianManager;

}
