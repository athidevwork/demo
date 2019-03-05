package dti.pm.riskmgr.coimgr.struts;

import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.coimgr.CoiFields;
import dti.pm.riskmgr.coimgr.CoiManager;
import dti.pm.riskmgr.coimgr.impl.CoiSaveProcessor;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import dti.pm.riskmgr.RiskHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Action class for Maintain COI Holder.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 6, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/25/2008       fcb         getNoteByNoteCode added.
 * 05/13/2008       fcb         82189: saveAllCoiInWorkflow - risk header loaded.
 * 03/19/2010       fcb         102700: saveAllCoiInWorkflow - get the risk header from workflow attributes.
 * 11/02/2011       wfu         127703 - Modified getInitialValuesForCoi to get risk header information.
 * 12/02/2013       jyang       149171 - roll back 141758's change to load lov label fields' value in
 *                              getInitialValuesForCoi method.
 * 07/30/2014       kxiang      155534 - Set href value to coiNameHref in method getInitialValuesForCoi.
 * ---------------------------------------------------
 */
public class MaintainCoiAction extends PMBaseAction {
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
        return loadAllCoiHolder(mapping, form, request, response);
    }

    /**
     * Method to load list of available COI Holder for requested risk.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllCoiHolder(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoiHolder", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Load the COI Holders
            RecordSet rs = (RecordSet) request.getAttribute(dti.oasis.http.RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
                rs = getCoiManager().loadAllCoiHolder(policyHeader, selectIndProcessor);
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

            // set PM_COI_WIP_TRANS into request
            request.setAttribute("needToCaptureTransaction", output.getStringValue("needToCaptureTransaction"));
            request.setAttribute("pmCoiClaimsParam", output.getStringValue("pmCoiClaimsParam"));
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the COI Holder page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCoiHolder", af);
        return af;
    }

    /**
     * Save all updated coverage records.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllCoiHolder(ActionMapping mapping,
                                          ActionForm form,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllCoiHolder", new Object[]{mapping, form, request, response});

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
                    l.logp(Level.FINE, getClass().getName(), "saveAllCoiHolder",
                        "Saving the COI inputRecords: " + inputRecords);
                }

                // Save the changes
                getCoiManager().saveAllCoiHolder(policyHeader, inputRecords);
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
                "Failed to save COI Holder.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllCoiHolder", af);
        return af;
    }

    /**
     * Method to save all COI from Workflow.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllCoiInWorkflow(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        final String SAVE_PROCESSOR = "CoiManager";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllCoiInWorkflow", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page
                securePage(request, form, false);

                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                String policyNumber = getPolicyHeader(request).getPolicyNo();

                // Get input records from request.
                inputRecords = (RecordSet)wa.getWorkflowAttribute(policyNumber, "inputRecords");
                RiskHeader riskHeader = (RiskHeader) wa.getWorkflowAttribute(policyNumber, "riskHeader");

                // Pull the policy header from request.
                PolicyHeader policyHeader = getPolicyHeader(request);
                policyHeader.setRiskHeader(riskHeader);

                // Call the business component to implement the validate/save logic.
                CoiSaveProcessor saveProcessor = (CoiSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
                saveProcessor.performSaveAllCoiHolder(policyHeader, inputRecords);

                // Get the next state.
                forwardString = wa.getNextState(policyNumber);
                setForwardParametersForWorkflow(request, forwardString, policyNumber, wa.getWorkflowInstanceIdName());
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(dti.oasis.http.RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            handleErrorPopup("pm.maintainCoi.save.error",
                "Failed to save COI page in workflow.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllCoiInWorkflow", af);
        }

        return af;
    }

    /**
     * Get initial values for add coi holder
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForCoi(ActionMapping mapping,
                                                ActionForm form,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForCoi",
            new Object[]{mapping, form, request, response});

        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get the initial values
            Record initialValuesRec = getCoiManager().getInitialValuesForCoiHolder(policyHeader, inputRecord);

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField coiField = fields.getField(CoiFields.COI_NAME_GH);
            CoiFields.setCoiNameHref(initialValuesRec,coiField.getHref());

            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for coi holder.", e, response);
        }

        l.exiting(getClass().getName(), "getInitialValuesForCoi");
        return null;
    }

    /**
     * To generate COI.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward generateAllCoi(ActionMapping mapping,
                                        ActionForm form,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generateAllCoi", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        Record inputRecord = null;

        try {
            // If the request has valid save token, then proceed with generate all COI; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Get policy header
                PolicyHeader policyHeader = getPolicyHeader(request);

                // Map As of Date field to RecordSet for input
                inputRecord = getInputRecord(request);

                // Save the data
                getCoiManager().generateAllCoi(policyHeader, inputRecord);
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to generate all COI.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "generateAllCoi", af);
        return af;
    }

    /**
     * An ajax call to get the notes based on the note code.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getNoteByNoteCode(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getNoteByNoteCode",
            new Object[]{mapping, form, request, response});

        try {
            // Secure access to the page
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            // get notes
            getCoiManager().getNoteByNoteCode(policyHeader, inputRecord);

            // prepare return values
            writeAjaxXmlResponse(response, inputRecord, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to get the COI note information.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getNoteByNoteCode", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.generateCoi.noselection.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getCoiManager() == null)
            throw new ConfigurationException("The required property 'coiManager' is missing.");
    }

    public CoiManager getCoiManager() {
        return m_coiManager;
    }

    public void setCoiManager(CoiManager coiManager) {
        m_coiManager = coiManager;
    }

    public MaintainCoiAction(){}

    private CoiManager m_coiManager;
}
