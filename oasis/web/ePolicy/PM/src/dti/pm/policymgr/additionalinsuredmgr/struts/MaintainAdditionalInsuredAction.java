package dti.pm.policymgr.additionalinsuredmgr.struts;

import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.additionalinsuredmgr.AdditionalInsuredFields;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.http.RequestIds;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.error.ValidationException;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.additionalinsuredmgr.AdditionalInsuredManager;
import dti.pm.policymgr.additionalinsuredmgr.impl.AdditionalInsuredSaveProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;

/**
 * Action class for maintain Additional Insured
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 15, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/20/2008       fcb         saveAllAdditionalInsuredInWorkflow added.
 * 09/21/2010       syang       Issue 111445 - Added getAddInsCoverageData() to retrieve coverage data.
 * 02/27/2013       xnie        138026 - 1) Added generateAllAddIns() to call manager's method generateAllAddIns to
 *                                          generate Additional Insured.
 *                                       2) Modified loadAllAdditionalInsured to display select checkbox and display js
 *                                          error.
 *                                       3) Added addJsMessages() to display no selection error for Generating.
 * 12/02/2013       jyang       149171 - roll back 141758's change to load LOV label fields value in
 *                                       getInitialValuesForAdditionalInsured method.
 * 07/30/2014       kxiang      155534 - Set href value to nameHref in method getInitialValuesForAdditionalInsured.
 * ---------------------------------------------------
 */
public class MaintainAdditionalInsuredAction extends PMBaseAction {

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
        return loadAllAdditionalInsured(mapping, form, request, response);
    }


    /**
     * Method to load all Additional Insured for the given policy
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllAdditionalInsured(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAdditionalInsured", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {

            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            //get input record from request
            Record inputRecord = getInputRecord(request);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);
            //remove the riskHeader, so that the risk info will not be populated on page
            policyHeader.setRiskHeader(null);
            
            // Gets all additional insured data
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
                rs = getAdditionalInsuredManager().loadAllAdditionalInsured(policyHeader, inputRecord, selectIndProcessor);
            }

            // Sets data bean
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            output.setFields(policyHeader.toRecord(), false);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Loads list of values
            loadListOfValues(request, form);

            // Load grid header bean
            loadGridHeader(request);

            //add js messages to messagemanager for the current request
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllAdditionalInsured page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAdditionalInsured", af);
        }
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
    public ActionForward getInitialValuesForAdditionalInsured(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAdditionalInsured",
            new Object[]{mapping, form, request, response});


        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get the initial values
            Record initialValuesRec = getAdditionalInsuredManager().getInitialValuesForAdditionalInsured(policyHeader, inputRecord);

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField additionalInsuredField = fields.getField(AdditionalInsuredFields.NAME_GH);
            AdditionalInsuredFields.setNameHref(initialValuesRec,additionalInsuredField.getHref());

            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for additional insured.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAdditionalInsured", af);
        }
        return af;
    }


    /**
     * Method to save all additional insured.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllAdditionalInsured(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAdditionalInsured", new Object[]{mapping, form, request, response});
        }

        ActionForward af;
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        try {
            // Secure access to the page
            securePage(request, form, false);
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {

                //get input record from request
                Record inputRecord = getInputRecord(request);
                // Generate input records
                inputRecords = getInputRecordSet(request);

                // Pull the policy header from request
                PolicyHeader policyHeader = getPolicyHeader(request);

                // Call the business component to implement the validate/save logic
                getAdditionalInsuredManager().saveAllAdditionalInsured(policyHeader, inputRecords, inputRecord);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {            
            handleErrorPopup("pm.maintainAdditionalInsured.save.error",
                "Failed to save additionl insured page.", e, request, mapping);            
        }

        af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAdditionalInsured", af);
        }

        return af;
    }

    /**
     * Method to save all additional insured from Workflow.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllAdditionalInsuredInWorkflow(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        final String SAVE_PROCESSOR = "AdditionalInsuredManager";

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAdditionalInsuredInWorkflow", new Object[]{mapping, form, request, response});
        }

        ActionForward af;
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        try {
            // Secure access to the page
            securePage(request, form, false);
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Pull the policy header from request.
                PolicyHeader policyHeader = getPolicyHeader(request);

                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                String policyNumber = policyHeader.getPolicyNo();

                // Get input record from Workflow.
                Record inputRecord = (Record)wa.getWorkflowAttribute(policyNumber, "inputRecord");
                // Get input records from request.
                inputRecords = (RecordSet)wa.getWorkflowAttribute(policyNumber, "inputRecords");

                // Call the business component to implement the validate/save logic.
                AdditionalInsuredSaveProcessor saveProcessor = (AdditionalInsuredSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
                saveProcessor.performSaveAllAdditionalInsured(policyHeader, inputRecords, inputRecord);

                // Get the next state.
                forwardString = wa.getNextState(policyNumber);
                setForwardParametersForWorkflow(request, forwardString, policyNumber, wa.getWorkflowInstanceIdName());
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            handleErrorPopup("pm.maintainAdditionalInsured.save.error",
                "Failed to save additionl insured page in workflow.", e, request, mapping);
        }

        af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAdditionalInsuredInWorkflow", af);
        }

        return af;
    }

    /**
     * Get coverage data when Attached Risk is changed.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward getAddInsCoverageData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getAddInsCoverageData",
            new Object[]{mapping, form, request, response});
        try {
            // Secure page.
            securePage(request, form, false);
            // Pull the policy header from request.
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get all request parameters into a record.
            Record inputRecord = getInputRecord(request);
            // Retrieve coverage data.
            Record record = getAdditionalInsuredManager().getAddInsCoverageData(policyHeader, inputRecord);
            // Write response.
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get values for coverage data.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddInsCoverageData", af);
        }
        return af;
    }

    /**
     * To generate Additional Insured.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward generateAllAddIns(ActionMapping mapping,
                                           ActionForm form,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generateAllAddins", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        Record inputRecord = null;

        try {
            // If the request has valid save token, then proceed with generate all Additional Insured; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Get policy header
                PolicyHeader policyHeader = getPolicyHeader(request);

                // Map As of Date field to RecordSet for input
                inputRecord = getInputRecord(request);

                // Save the data
                getAdditionalInsuredManager().generateAllAddIns(policyHeader, inputRecord);
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to generate all Additional Insured.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "generateAllAddins", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.generateAddIns.noselection.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAdditionalInsuredManager() == null)
            throw new ConfigurationException("The required property 'additionalInsuredManager' is missing.");

    }
    
    public AdditionalInsuredManager getAdditionalInsuredManager() {
        return m_additionalInsuredManager;
    }

    public void setAdditionalInsuredManager(AdditionalInsuredManager additionalInsuredManager) {
        m_additionalInsuredManager = additionalInsuredManager;
    }

    private AdditionalInsuredManager m_additionalInsuredManager;

}
