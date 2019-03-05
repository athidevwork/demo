package dti.pm.coveragemgr.prioractmgr.struts;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.StringUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.coveragemgr.prioractmgr.PriorActManager;
import dti.pm.coveragemgr.prioractmgr.PriorActFields;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.riskmgr.RiskFields;
import dti.pm.busobjs.ComponentOwner;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Action class for Maintain Prior Acts.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 29, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/10/2011       wqfu        103799 - Added logic to handle copy prior act stats.
 * 01/22/2013       adeng       141183 - Modified loadAllPriorAct() to get note field's setting of visible in WebWB and
 *                                       set it into input record.
 * 01/25/2013       adeng       141183 - Roll backed last change.
 * 12/02/2013       jyang       149171 - Roll back 141758's change to load LOV label fields' value in
 *                                       getInitialValuesForXXX method.
 * 10/22/2014       jyang       157203 - Modified loadAllPriorAct to set parent risk's earliestContigEffectiveDate and
 *                                       effectiveToDate in current term to field 'riskStartDate' and 'riskExpirationDate'.
 * 11/19/2015       eyin        167171 - Modified getInitialValuesForPriorAct(), Add logic to process when initialValuesRec is null.
 * 08/26/2016       wdang       167534 - Disable Delete buttons when readOnly flag is ON.
 * ---------------------------------------------------
 */
public class MaintainPriorActAction extends PMBaseAction {
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
        return loadAllPriorAct(mapping, form, request, response);
    }


    /**
     * Method to load list of available prior acts data for requested coverage.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllPriorAct(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoverage", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            // Get the policy header from the request, and load the risk header and coverage header
            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            //if the request is not a redirect request, get and set common values on inputRecord
            if (!inputRecord.hasStringValue(PriorActFields.COVERAGE_RETRO_DATE)) {
                Record commonVlaues = getPriorActManager().getCommonValues(policyHeader, inputRecord);
                inputRecord.setFields(commonVlaues, true);
            }

            // Load the prior act risks
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getPriorActManager().loadAllPriorActRisk(policyHeader, inputRecord);
                if (EntitlementFields.isReadOnly(rs.getSummaryRecord())) {
                    rs.setFieldValueOnAll("isDelRiskAvailable", YesNoFlag.N);
                }
            }
            // Set loaded prior act risk data into request
            setDataBean(request, rs);

            //Load the prior act coverages
            RecordSet covgRs = (RecordSet) request.getAttribute(COVERAGE_GRID_RECORD_SET);
            if (covgRs == null) {
                covgRs = getPriorActManager().loadAllPriorActCoverage(policyHeader, inputRecord);
                if (EntitlementFields.isReadOnly(rs.getSummaryRecord())) {
                    covgRs.setFieldValueOnAll("isDelCovgAvailable", YesNoFlag.N);
                }
            }
            // Set loaded prior act coverage data into request
            setDataBean(request, covgRs, COVERAGE_GRID_ID);

            // Load the prior act components
            RecordSet compRs = (RecordSet) request.getAttribute(COMP_GRID_RECORD_SET);
            if (compRs == null) {
                compRs = getComponentManager().loadAllComponent(policyHeader, inputRecord, ComponentOwner.PRIOR_ACT, covgRs);
                if (EntitlementFields.isReadOnly(rs.getSummaryRecord())) {
                    compRs.setFieldValueOnAll("isDelCompAvailable", YesNoFlag.N);
                }
            }
            // Set loaded Component data into request
            setDataBean(request, compRs, COMPONENT_GRID_ID);

            // If current loading is after Copy Prior Acts Stats, we need to delete the pending data which has been
            // loaded to display for editing. The data will really be inserted only when user click Save button.
            if (inputRecord.hasStringValue(RequestIds.IS_COPY_ACTS_STATS) &&
                YesNoFlag.getInstance(inputRecord.getStringValue(RequestIds.IS_COPY_ACTS_STATS)).booleanValue()) {
                String riskId = "";
                String covgId = "";
                String compId = "";
                Record record = new Record();
                Iterator iter = rs.getRecords();
                while (iter.hasNext()) {
                    Record riskRec = (Record) iter.next();
                    riskId += RiskFields.getRiskId(riskRec) + ",";
                }
                iter = covgRs.getRecords();
                while (iter.hasNext()) {
                    Record covgRec = (Record) iter.next();
                    covgId += CoverageFields.getCoverageId(covgRec) + ",";
                }
                iter = compRs.getRecords();
                while (iter.hasNext()) {
                    Record compRec = (Record) iter.next();
                    compId += ComponentFields.getPolicyCovComponentId(compRec) + ",";
                }

                if (!StringUtils.isBlank(riskId) || !StringUtils.isBlank(covgId) || !StringUtils.isBlank(compId)) {
                    RiskFields.setRiskId(record, (riskId.length()>0) ? "," + riskId : null);
                    CoverageFields.setCoverageId(record, (covgId.length()>0) ? "," + covgId : null);
                    ComponentFields.setPolicyCovComponentId(record, (compId.length()>0) ? "," + compId : null);
                    // To delete pending data
                    getPriorActManager().deleteAllPendPriorActs(record);
                }

                // Set the status to request attribute
                request.setAttribute(RequestIds.IS_COPY_ACTS_STATS, YesNoFlag.Y);
            }

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            output.setFields(covgRs.getSummaryRecord(), false);
            output.setFields(compRs.getSummaryRecord(), false);
            output.setFields(inputRecord, false);
            //set parent risk effective from date to load lov for some fields
            PriorActFields.setRiskStartDate(output, policyHeader.getRiskHeader().getEarliestContigEffectiveDate());
            PriorActFields.setRiskExpirationDate(output, policyHeader.getRiskHeader().getRiskEffectiveToDate());
            //component owner
            output.setFieldValue(ComponentFields.COMPONENT_OWNER, ComponentOwner.PRIOR_ACT);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Set currentGridId to riskListGrid before load risk gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, RISK_GRID_ID);
            // Load risk grid header
            loadGridHeader(request, null, RISK_GRID_ID, RISK_GRID_LAYER_ID);
            // Set currentGridId to coverageListGrid before load coverage gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);
            // Load coverage grid header
            loadGridHeader(request, null, COVERAGE_GRID_ID, COVERAGE_GRID_LAYER_ID);
            // Set currentGridId to componentListGrid before load component gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);
            // Load component grid header
            loadGridHeader(request, null, COMPONENT_GRID_ID, COMPONENT_GRID_LAYER_ID);

            // Load the list of values after loading the data
            loadListOfValues(request, form);
            // Populate messages for javascirpt
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the prior act page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCoverage", af);
        return af;
    }


    /**
     * Save all updated prior acts data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllPriorAct(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllPriorAct", new Object[]{mapping, form, request, response});

        RecordSet riskRs = null;
        RecordSet covgRs = null;
        RecordSet compRs = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // First all Prior Acts records
                // Map coverage textXML to RecordSet for input
                riskRs = getInputRecordSet(request, RISK_GRID_ID);
                // set currentGridId to coverageListGrid before get input recordSet for coverage grid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);
                covgRs = getInputRecordSet(request, COVERAGE_GRID_ID);
                // set currentGridId to componentListGrid before get input recordSet for component grid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);
                compRs = getInputRecordSet(request, COMPONENT_GRID_ID);

                // Get the policy header from the request, and load the risk header and coverage header
                PolicyHeader policyHeader = getPolicyHeader(request, true, true);
            
                Record inputRecord = getInputRecord(request);
                inputRecord.remove(RISK_GRID_ID+ dti.oasis.http.RequestIds.TEXT_XML );
                inputRecord.remove(COVERAGE_GRID_ID+ dti.oasis.http.RequestIds.TEXT_XML );
                inputRecord.remove(COMPONENT_GRID_ID+ dti.oasis.http.RequestIds.TEXT_XML );
                
                // save the prior acts data
                getPriorActManager().saveAllPriorAct(policyHeader, inputRecord, riskRs, covgRs, compRs);

                // Set back to riskListGrid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, RISK_GRID_ID);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, RISK_GRID_ID);
            request.setAttribute(RequestIds.GRID_RECORD_SET, riskRs);
            request.setAttribute(COVERAGE_GRID_RECORD_SET, covgRs);
            request.setAttribute(COMP_GRID_RECORD_SET, compRs);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup("pm.maintainPriorActs.saveDataFailed",
                "Failed to save the prior acts page.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllPriorAct", af);
        return af;
    }


    /**
     * validate prior act coverage date in Ajax mode
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward validatePriorActCoverageDate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validatePriorActCoverageDate",
            new Object[]{mapping, form, request, response});


        try {
            // Secure page
            securePage(request, form, false);

            // Get the policy header from the request, and load the risk header and coverage header
            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // validate prior act coverage date
            getPriorActManager().validatePriorActCoverageDate(policyHeader, inputRecord);

            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate prior acts coverage date fields", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePriorActCoverageDate", af);
        }
        return af;
    }

    /**
     * validate prior act compoent date in Ajax mode
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward validatePriorActComponentDate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validatePriorActComponentDate",
            new Object[]{mapping, form, request, response});


        try {
            // Secure page
            securePage(request, form, false);

            // Get the policy header from the request, and load the risk header and coverage header
            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // validate prior act component date
            getPriorActManager().validatePriorActComponentDate(policyHeader, inputRecord);

            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate prior acts component date fields", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePriorActComponentDate", af);
        }
        return af;
    }

    /**
     * validate for delete prior act risk
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward validateForDelete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateForDelete",
            new Object[]{mapping, form, request, response});


        try {
            // Secure page
            securePage(request, form, false);

            // Get the policy header from the request, and load the risk header and coverage header
            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // validate prior act component date
            getPriorActManager().validateForDelete(policyHeader, inputRecord);

            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate for delete prior act risk", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForDelete", af);
        }
        return af;
    }

    /**
     * for newly add prior act risk/coverage/compoment, provide default values
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward getInitialValuesForPriorAct(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForPriorAct",
            new Object[]{mapping, form, request, response});


        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // Get the policy header from the request, and load the risk header and coverage header
            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get the initial values for risk
            Record initialValuesRec=null;

            if (inputRecord.getStringValue("initialLevel").equals("RISK")) {
                initialValuesRec = getPriorActManager().getInitialValuesForPriorActRisk(
                    policyHeader, inputRecord);
            }
            else if (inputRecord.getStringValue("initialLevel").equals("COVERAGE")) {
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);
                RecordFilter rf = new RecordFilter(
                    RiskFields.RISK_BASE_RECORD_ID, policyHeader.getRiskHeader().getRiskBaseRecordId());
                RecordSet covgRs = getInputRecordSet(request, COVERAGE_GRID_ID).getSubSet(rf);
                initialValuesRec = getPriorActManager().getInitialValuesForPriorActCoverage(policyHeader, inputRecord, covgRs);
            }
            // In practice, system shall not move into this else block.
            else {
                initialValuesRec = new Record();
            }
            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for prior act.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForPriorAct", af);
        }
        return af;
    }

    /**
     * validate prior act compoent date in Ajax mode
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward copyPriorActsStats(ActionMapping mapping,
                                            ActionForm form,
                                            HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "copyPriorActsStats", new Object[]{mapping, form, request, response});

        try {
            // Secure page
            securePage(request, form, false);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get selected policy and risk list
            if (inputRecord.hasStringValue(PriorActFields.CF_POLICY_ID) &&
                inputRecord.hasStringValue(PriorActFields.CF_RISK_BASE_ID) ) {
                // generate to copy stats for all selected policy and risk
                getPriorActManager().copyPriorActsStats(inputRecord);
            }

            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to copy prior acts stats", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyPriorActsStats", af);
        }
        return af;
    }


    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        // Add and Maintain Prior Acts messages
        MessageManager.getInstance().addJsMessage("pm.maintainPriorActs.saveCoverageFirst.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainPriorActs.confirm.deleteExistCovgComp");

        // Component messages
        MessageManager.getInstance().addJsMessage("pm.maintainComponent.effectiveToDate.rule1.error");
        MessageManager.getInstance().addJsMessage("pm.addComponent.noCoverage.error");
        MessageManager.getInstance().addJsMessage("pm.maintainReinstate.confirm.continue");
        MessageManager.getInstance().addJsMessage("pm.addComponent.duplicated.error");

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
            if (currentGridId.equals(COVERAGE_GRID_ID)) {
                anchorName = getCoverageAnchorColumnName();
            }
            else if (currentGridId.equals(COMPONENT_GRID_ID)) {
                anchorName = getComponentAnchorColumnName();
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

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        super.verifyConfig();
        if (getCoverageAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'coverageAnchorColumnName' is missing.");
        if (getComponentAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'componentAnchorColumnName' is missing.");
        if (getPriorActManager() == null)
            throw new ConfigurationException("The required property 'priorActManager' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
    }

    public PriorActManager getPriorActManager() {
        return m_priorActManager;
    }

    public void setPriorActManager(PriorActManager priorActManager) {
        m_priorActManager = priorActManager;
    }


    public String getComponentAnchorColumnName() {
        return m_componentAnchorColumnName;
    }

    public void setComponentAnchorColumnName(String componentAnchorColumnName) {
        m_componentAnchorColumnName = componentAnchorColumnName;
    }

    public String getCoverageAnchorColumnName() {
        return m_coverageAnchorColumnName;
    }

    public void setCoverageAnchorColumnName(String coverageAnchorColumnName) {
        m_coverageAnchorColumnName = coverageAnchorColumnName;
    }


    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }


    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String RISK_GRID_ID = "riskListGrid";
    protected static final String RISK_GRID_RECORD_SET = "riskGridRecordSet";
    protected static final String RISK_GRID_LAYER_ID = "PM_PA_RISK_GH";
    protected static final String COVERAGE_GRID_ID = "coverageListGrid";
    protected static final String COVERAGE_GRID_RECORD_SET = "coverageGridRecordSet";
    protected static final String COVERAGE_GRID_LAYER_ID = "PM_PA_COVG_GH";
    protected static final String COMPONENT_GRID_ID = "componentListGrid";
    protected static final String COMP_GRID_RECORD_SET = "compGridRecordSet";
    protected static final String COMPONENT_GRID_LAYER_ID = "PM_PA_COMP_GH";
    private PriorActManager m_priorActManager;
    private ComponentManager m_componentManager;
    private String m_componentAnchorColumnName;
    private String m_coverageAnchorColumnName;

}
