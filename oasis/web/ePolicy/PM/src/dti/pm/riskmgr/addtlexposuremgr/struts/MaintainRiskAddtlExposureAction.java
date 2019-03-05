package dti.pm.riskmgr.addtlexposuremgr.struts;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.request.RequestStorageManager;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.addtlexposuremgr.RiskAddtlExposureManager;
import dti.pm.riskmgr.addtlexposuremgr.impl.RiskAddtlExposureRowStyleRecordLoadprocessor;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.addtlexposuremgr.RiskAddtlExposureFields;

/**
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * Date:   May 23, 2017
 *
 * @author eyin
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 09/21/2017    eyin       169483, Initial version.
 * ---------------------------------------------------
 */
public class MaintainRiskAddtlExposureAction extends PMBaseAction {

    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping       mapping
     * @param form          form
     * @param request       request
     * @param response      response
     * @return              action forward
     * @throws Exception
     */
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.loadAllRiskAddtlExposure(mapping, form, request, response);
    }
    
    /**
     * load all Additional Exposure information.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllRiskAddtlExposure(ActionMapping mapping, ActionForm form,
                                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAdditionalExposure", new Object[] {mapping, form, request,response  });
        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            Record inputRecord = getInputRecord(request);

            // Load the Primary Practice List
            RecordSet primaryPracticeRs = (RecordSet) request.getAttribute(dti.oasis.http.RequestIds.GRID_RECORD_SET);
            if (primaryPracticeRs == null) {
                primaryPracticeRs = getRiskAddtlExposureManager().loadPrimaryPractice(policyHeader, inputRecord);
            }

            // Sets data bean
            setDataBean(request, primaryPracticeRs);

            // Load the Additional Exposure List
            RecordSet rs = (RecordSet) request.getAttribute(RISK_ADDTL_EXPOSURE_GRID_RECORD_SET);
            if (rs == null) {
                RecordLoadProcessor rowStyleLp = new RiskAddtlExposureRowStyleRecordLoadprocessor();
                rs = getRiskAddtlExposureManager().loadAllRiskAddtlExposure(policyHeader, inputRecord, rowStyleLp, true);
            }
            // Sets data bean
            setDataBean(request, rs, RISK_ADDTL_EXPOSURE_GRID_ID);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load risk additional exposure grid header
            loadGridHeader(request);

            // Set currentGridId to riskAddtlExposureListGrid before load additional exposure gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PRIMARY_EXPOSURE_GRID_ID);
            // Load primary practice grid header
            loadGridHeader(request, null, PRIMARY_EXPOSURE_GRID_ID, PRIMARY_EXPOSURE_GRID_LAYER_ID);

            // Set currentGridId to riskAddtlExposureListGrid before load additional exposure gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, RISK_ADDTL_EXPOSURE_GRID_ID);
            // Load primary practice grid header
            loadGridHeader(request, null, RISK_ADDTL_EXPOSURE_GRID_ID, ADDTL_EXPOSURE_GRID_LAYER_ID);

            // Loads list of values
            loadListOfValues(request, form);

            //add js messages to messagemanager for the current request
            addJsMessages();

            request.setAttribute("riskEffectiveFromDate", RiskFields.getRiskEffectiveFromDate(output));
            request.setAttribute("riskName", RiskFields.getRiskName(output));
            request.setAttribute(ADDTL_PRACTICE_SIZE, rs.getSize());

        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Additional Exposure.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAdditionalExposure", af);
        return af;
    }
    
    /**
     * Save all Additional Exposure information.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllAdditionalExposure(ActionMapping mapping, ActionForm form,
                                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAdditionalExposure", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        RecordSet primaryPracticeInputRecords = null;
        RecordSet rs = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (isTokenValid(request, true)) {
                // Secure page
                securePage(request, form, false);

                //get policy header
                PolicyHeader policyHeader = getPolicyHeader(request, true);

                // get input
                Record inputRecord = getInputRecord(request);

                // Map primary practice textXML to RecordSet for input
                primaryPracticeInputRecords = getInputRecordSet(request, PRIMARY_EXPOSURE_GRID_ID);

                // Set currentGridId to riskAddtlExposureListGrid before get input recordSet for risk additional exposure grid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, RISK_ADDTL_EXPOSURE_GRID_ID);
                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request, RISK_ADDTL_EXPOSURE_GRID_ID);

                //load risk before modified
                RecordLoadProcessor rowStyleLp = new RiskAddtlExposureRowStyleRecordLoadprocessor();
                rs = getRiskAddtlExposureManager().loadAllRiskAddtlExposure(policyHeader, inputRecord, rowStyleLp, false);
                //merge modified risk records and old risk records
                rs.merge(inputRecords, RiskAddtlExposureFields.RISK_ADDTL_EXPOSURE_ID);

                // Call the business component to implement the validate/save logic
                int updateCount = getRiskAddtlExposureManager().saveAllRiskAddtlExposure(policyHeader, inputRecords);

                // If data changed, send a flag to page. page will refresh the risk/risk summary page depends on this flag.
                if (updateCount > 0) {
                    request.setAttribute(DATA_SAVED_B, "Y");
                }
            }
        }
        catch (ValidationException v) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, primaryPracticeInputRecords);
            request.setAttribute(RISK_ADDTL_EXPOSURE_GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save Additional Exposure.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAdditionalExposure", af);
        }
        return af;
    }

    /**
     * Get initial values for add Additional Exposure
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForAddRiskAddtlExposure(ActionMapping mapping,
                                                                 ActionForm form,
                                                                 HttpServletRequest request,
                                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAdditionalExposure",
            new Object[]{mapping, form, request, response});
        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get the initial values
            Record initialValuesRec = getRiskAddtlExposureManager().getInitialValuesForAddRiskAddtlExposure(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for Additional Exposure.", e, response);
        }
        l.exiting(getClass().getName(), "getInitialValuesForAdditionalExposure");
        return null;
    }

    /**
     * Check if Change option is available
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateForOoseRiskAddtlExposure(ActionMapping mapping,
                                                          ActionForm form,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateForOoseRiskAddtlExposure",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            // do validation
            getRiskAddtlExposureManager().validateForOoseRiskAddtlExposure(policyHeader, inputRecord);

            // Send back xml data
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate OOSE risk additional exposure.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateForOoseRiskAddtlExposure", af);
        return af;
    }

    /**
     * Get initial values for OOSE risk additional exposure
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForOoseRiskAddtlExposure(ActionMapping mapping,
                                                                  ActionForm form,
                                                                  HttpServletRequest request,
                                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForOoseRiskAddtlExposure",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);

            // get initial values
            Record record = getRiskAddtlExposureManager().getInitialValuesForOoseRiskAddtlExposure(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // prepare return values
            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for OOSE risk additional exposure.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForOoseRiskAddtlExposure", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.oose.modified.record.exist.error2");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskAddtlExposure.ooseRiskAddtlExposure.changeOption.error");
    }

    public RiskAddtlExposureManager getRiskAddtlExposureManager() {
        return m_riskAddtlExposureManager;
    }

    public void setRiskAddtlExposureManager(RiskAddtlExposureManager riskAddtlExposureManager) {
        m_riskAddtlExposureManager = riskAddtlExposureManager;
    }

    private RiskAddtlExposureManager m_riskAddtlExposureManager;

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String RISK_ADDTL_EXPOSURE_GRID_ID = "riskAddtlExposureListGrid";
    protected static final String RISK_ADDTL_EXPOSURE_GRID_RECORD_SET = "riskAddtlExposureGridRecordSet";
    protected static final String PRIMARY_EXPOSURE_GRID_ID = "primaryExposureListGrid";
    protected static final String ADDTL_EXPOSURE_GRID_LAYER_ID = "PM_RISK_ADDTL_EXPOSURE_GH";
    protected static final String PRIMARY_EXPOSURE_GRID_LAYER_ID = "PM_PRIMARY_EXPOSURE_GH";

    private static final String DATA_SAVED_B = "dataSavedB";
    private static final String ADDTL_PRACTICE_SIZE = "addtlPracticeSize";
}
