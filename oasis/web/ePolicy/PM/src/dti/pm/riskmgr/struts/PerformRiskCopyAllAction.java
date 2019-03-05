package dti.pm.riskmgr.struts;

import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.WebLayer;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.http.RequestIds;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.session.UserSessionManager;
import dti.oasis.error.ValidationException;
import dti.pm.riskmgr.RiskCopyFields;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class handle risk copy all process
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 13, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/07/2010       dzhang      #108261 - Modified method loadAllCopyRisk().
 * 10/21/2010       syang       Issue 113025 - Published the invisible field ids for risk/coverage/component/coverage class.
 * 10/28/2010       syang       Issue 113025 - Refactor the getFieldIdsForLayer() to handle all fields in layer.
 * 06/13/2018       wrong       192557 - Modified deleteAllCopiedRisk() to call hasValidSaveToken() to be used for
 *                                       CSRFInterceptor.
 * ---------------------------------------------------
 */
public class PerformRiskCopyAllAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS, "loadAllRiskRelation");
        return loadAllCopyRisk(mapping, form, request, response);
    }

    /**
     * Method to load all data of coverage/component/coverage class/target risk
     * for copy all risk.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllCopyRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCopyRisk", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            //reset select indicator in fields
            resetSelectIndicator(request);
            //First,load policy header with risk header
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            Record inputRecord = getInputRecord(request);

            // Load the coverages from request
            RecordSet covgRs = (RecordSet) request.getAttribute(COVG_GRID_RECORD_SET);
            // Load the components from request
            RecordSet compRs = (RecordSet) request.getAttribute(COMP_GRID_RECORD_SET);
            // Load the coverage class from request
            RecordSet covgClassRs = (RecordSet) request.getAttribute(COVG_CLASS_GRID_RECORD_SET);
            // Load the target risks from request
            RecordSet riskRs = (RecordSet) request.getAttribute(RISK_GRID_RECORD_SET);

            //load all the config fields
            HashMap configFieldsMap = (HashMap) getRiskManager().getAllFieldForCopyAll();

            if (covgRs == null) {
                RecordLoadProcessor covgAddSelIndLP = AddSelectIndLoadProcessor.getInstance();
                covgRs = getCoverageManager().loadAllSourceCoverage(policyHeader, covgAddSelIndLP, (String)configFieldsMap.get(RiskCopyFields.CopyLevelValues.COVERAGE_LEVEL));
            }
            if (compRs == null) {
                // Prepare component owner
                ComponentOwner owner = ComponentOwner.COVERAGE;
                RecordLoadProcessor compAddSelIndLP = AddSelectIndLoadProcessor.getInstance();
                compRs = getComponentManager().loadAllSourceComponent(policyHeader, inputRecord, owner, covgRs, compAddSelIndLP, (String)configFieldsMap.get(RiskCopyFields.CopyLevelValues.COMPONENT_LEVEL));
            }
            if (covgClassRs == null) {
                RecordLoadProcessor covgClassAddSelIndLP = AddSelectIndLoadProcessor.getInstance();
                covgClassRs = getCoverageClassManager().loadAllSourceCoverageClass(policyHeader, covgRs, covgClassAddSelIndLP, (String)configFieldsMap.get(RiskCopyFields.CopyLevelValues.COVERAGE_CLASS_LEVEL));
            }
            if (riskRs == null) {
                RecordLoadProcessor riskAddSelIndLP = AddSelectIndLoadProcessor.getInstance();
                riskRs = getRiskManager().loadAllTargetRisk(policyHeader, inputRecord, riskAddSelIndLP, (String)configFieldsMap.get(RiskCopyFields.CopyLevelValues.RISK_LEVEL));
            }

            // Set loaded data into request
            setDataBean(request, covgRs, COVERAGE_GRID_ID);
            setDataBean(request, compRs, COMPONENT_GRID_ID);
            setDataBean(request, covgClassRs, COVERAGE_CLASS_GRID_ID);
            setDataBean(request, riskRs, RISK_GRID_ID);

            // Make the Summary Record available for output
            Record output = covgRs.getSummaryRecord();
            output.setFields(compRs.getSummaryRecord(), false);
            output.setFields(covgClassRs.getSummaryRecord(), false);
            output.setFields(riskRs.getSummaryRecord(), false);
            output.setFields(inputRecord, false);
            // Set invisible field id(s).
            OasisFields fieldsMap = (OasisFields) request.getAttribute("fieldsMap");
            Record fieldIdsRecord = getInvisibleFieldIds(fieldsMap , output);
            output.setFields(fieldIdsRecord, false);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Set currentGridId to every gridID on page before load gird header
            // then load grid header for each grid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);
            loadGridHeader(request, null, COVERAGE_GRID_ID, COVERAGE_GRID_LAYER_ID);
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);
            loadGridHeader(request, null, COMPONENT_GRID_ID, COMPONENT_GRID_LAYER_ID);
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_CLASS_GRID_ID);
            loadGridHeader(request, null, COVERAGE_CLASS_GRID_ID, COVG_CLASS_GRID_LAYER_ID);
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, RISK_GRID_ID);
            loadGridHeader(request, null, RISK_GRID_ID, RISK_GRID_LAYER_ID);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Populate messages for javascirpt
            addJsMessages();

            // Set current policyTermHistoryId, riskId and coverageId in the usersession
            getPolicyManager().setCurrentIdsInSession(policyHeader.getTermBaseRecordId(),
                request.getParameter(RequestIds.RISK_ID), request.getParameter(RequestIds.COVERAGE_ID),
                request.getParameter(RequestIds.COVERAGE_CLASS_ID), UserSessionManager.getInstance().getUserSession());

            //get operation parameter from request and set back to request
            request.setAttribute("operation", inputRecord.getStringValue("operation"));
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the coverage page.", e, request, mapping);
        }


        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCopyRisk", af);
        }

        return af;
    }

    /**
     * handle risk copy all
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward copyAllRisk(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performRiskCopyAll", new Object[]{mapping, form, request, response});
        }

        RecordSet covgRs = null;
        RecordSet compRs = null;
        RecordSet covgClassRs = null;
        RecordSet coiRs = null;
        RecordSet affiRs = null;
        RecordSet scheduleRs = null; 
        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);
            //get policy header
            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);

            // Map coverage textXML to RecordSet for input
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);
            covgRs = getInputRecordSet(request, COVERAGE_GRID_ID)
                .getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);
            compRs = getInputRecordSet(request, COMPONENT_GRID_ID)
                .getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_CLASS_GRID_ID);
            covgClassRs = getInputRecordSet(request, COVERAGE_CLASS_GRID_ID)
                .getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));

            String textXMLName = COI_GRID_ID + dti.oasis.http.RequestIds.TEXT_XML;
            if (!StringUtils.isBlank(request.getParameter(textXMLName))) {
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COI_GRID_ID);
                coiRs = getInputRecordSet(request, COI_GRID_ID)
                    .getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            }
            textXMLName = AFFILIATION_GRID_ID + dti.oasis.http.RequestIds.TEXT_XML;
            if (!StringUtils.isBlank(request.getParameter(textXMLName))) {
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, AFFILIATION_GRID_ID);
                affiRs = getInputRecordSet(request, AFFILIATION_GRID_ID)
                    .getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            }
            textXMLName = SCHEDULE_GRID_ID + dti.oasis.http.RequestIds.TEXT_XML;
            if (!StringUtils.isBlank(request.getParameter(textXMLName))) {
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SCHEDULE_GRID_ID);
                scheduleRs = getInputRecordSet(request, SCHEDULE_GRID_ID)
                    .getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            }

            // copy all risks
            Record valResultRec = getRiskManager().copyAllRisk(policyHeader, covgRs, compRs, covgClassRs, coiRs, affiRs, scheduleRs, inputRecord);

            writeAjaxXmlResponse(response, valResultRec, true);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "copy risk failed", e, response);
        }
        return null;
    }

    /**
     * validate source risk in Ajax mode
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward validateRiskCopySource(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateRiskCopySource",
            new Object[]{mapping, form, request, response});


        try {
            // Secure page
            securePage(request, form, false);

            // Get the policy header from the request, and load risk header
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // validate source risk
            String validateResult = getRiskManager().validateRiskCopySource(policyHeader, inputRecord);
            Record outputRecord = new Record();
            outputRecord.setFieldValue("validateResult", validateResult);
            writeAjaxXmlResponse(response, outputRecord, true);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate source risk", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateRiskCopySource", af);
        }
        return af;
    }


    /**
     * handle risk delete all
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteAllCopiedRisk(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllCopiedRisk", new Object[]{mapping, form, request, response});
        }

        RecordSet covgRs = null;
        RecordSet compRs = null;
        RecordSet covgClassRs = null;
        try {
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                //get policy header
                PolicyHeader policyHeader = getPolicyHeader(request, true);
                Record inputRecord = getInputRecord(request);

                // Map coverage textXML to RecordSet for input
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);
                covgRs = getInputRecordSet(request, COVERAGE_GRID_ID)
                    .getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);
                compRs = getInputRecordSet(request, COMPONENT_GRID_ID)
                    .getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_CLASS_GRID_ID);
                covgClassRs = getInputRecordSet(request, COVERAGE_CLASS_GRID_ID)
                    .getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));

                // delete risk all
                Record outputRecord =
                    getRiskManager().deleteAllCopiedRisk(policyHeader, covgRs, compRs, covgClassRs, inputRecord);

                writeAjaxXmlResponse(response, outputRecord, true);
            }
        }
        catch (ValidationException ve) {
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "copy risk failed", e, response);
        }

        // Return the forward
        ActionForward af = null;
        l.exiting(getClass().getName(), "deleteAllCopiedRisk", af);
        return af;
    }

    /**
     * Get the invisible field id(s) for risk/coverage/component/coverage class, they will be published out.
     *
     * @param oasisFields
     * @param inputRecord
     * @return Record
     */
    private Record getInvisibleFieldIds(OasisFields oasisFields, Record inputRecord) {
        Record outputRecord = new Record();
        // Set invisible field id(s) for risk form.
        String invisibleRiskFormFields = getFieldIdsForLayer(oasisFields, "PM_SRC_RISK_FM");
        outputRecord.setFieldValue("invisibleRiskFormFields", invisibleRiskFormFields);

        // Set invisible field id(s) for coverage grid.
        String invisibleCovgGridFields = getFieldIdsForLayer(oasisFields, "PM_SRC_COVG_FM");
        outputRecord.setFieldValue("invisibleCovgGridFields", invisibleCovgGridFields);

        // Set invisible field id(s) for component grid.
        String invisibleCompGridFields = getFieldIdsForLayer(oasisFields, "PM_SRC_COMP_FM");
        outputRecord.setFieldValue("invisibleCompGridFields", invisibleCompGridFields);

        // Set invisible field id(s) for coverage class grid.
        String invisibleCovgClassGridFields = getFieldIdsForLayer(oasisFields, "PM_SRC_SUB_COVG_FM");
        outputRecord.setFieldValue("invisibleCovgClassGridFields", invisibleCovgClassGridFields);
        return outputRecord;
    }

    /**
     * Get invisible id(s) for specified layer.
     *
     * @param oasisFields
     * @param layerId
     * @return String
     */
    private String getFieldIdsForLayer(OasisFields oasisFields, String layerId) {
        // The invisible ids should be ",aa,bb,cc,".
        StringBuffer sb = new StringBuffer(",");
        WebLayer webLayer = oasisFields.getLayerFieldsMap(layerId);
        if (webLayer != null) {
            Iterator its = webLayer.entrySet().iterator();
            while (its.hasNext()) {
                Map.Entry fieldEntry = (Map.Entry) its.next();
                String fieldId = (String) fieldEntry.getKey();
                OasisFormField field = (OasisFormField) fieldEntry.getValue();
                // We suppose the field is invisible if the layer is hidden.
                if (webLayer.isHidden() || !field.getIsVisible()) {
                    sb.append(fieldId).append(",");
                }
            }
        }
        return sb.toString();
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.processCopy.confirm");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.sourceNotSelected.error");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.targetNotSelected.error");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.deselectState.error");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.failure.error");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.copySucess.msg");

        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.deleteAll.sourceNotSelected.error");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.deleteAll.targetNotSelected.error");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.deleteAll.processDelete.confirm");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.deleteAll.deleteSource.confirm");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.deleteAll.deleteTargetFail.error");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.deleteAll.deleteSourceFail.error");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.deleteSucess.msg");
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
            if (currentGridId.equals(COMPONENT_GRID_ID)) {
                anchorName = getComponentAnchorColumnName();
            }
            else if (currentGridId.equals(COVERAGE_CLASS_GRID_ID)) {
                anchorName = getCovgClassAnchorColumnName();
            }
            else if (currentGridId.equals(RISK_GRID_ID)) {
                anchorName = getRiskAnchorColumnName();
            }
            else if (currentGridId.equals(COI_GRID_ID)) {
                anchorName = getCoiAnchorColumnName();
            }
            else if (currentGridId.equals(AFFILIATION_GRID_ID)) {
                anchorName = getAffiAnchorColumnName();
            }
            else if (currentGridId.equals(SCHEDULE_GRID_ID)) {
                anchorName = getScheduleAnchorColumnName();
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


    public String getCovgClassAnchorColumnName() {
        return m_covgClassAnchorColumnName;
    }

    public void setCovgClassAnchorColumnName(String covgClassAnchorColumnName) {
        m_covgClassAnchorColumnName = covgClassAnchorColumnName;
    }

    public String getCoverageAnchorColumnName() {
        return m_coverageAnchorColumnName;
    }

    public void setCoverageAnchorColumnName(String coverageAnchorColumnName) {
        m_coverageAnchorColumnName = coverageAnchorColumnName;
    }

    public String getComponentAnchorColumnName() {
        return m_componentAnchorColumnName;
    }

    public void setComponentAnchorColumnName(String componentAnchorColumnName) {
        m_componentAnchorColumnName = componentAnchorColumnName;
    }

    public String getRiskAnchorColumnName() {
        return m_riskAnchorColumnName;
    }

    public void setRiskAnchorColumnName(String riskAnchorColumnName) {
        m_riskAnchorColumnName = riskAnchorColumnName;
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }


    public CoverageClassManager getCoverageClassManager() {
        return m_coverageClassManager;
    }

    public void setCoverageClassManager(CoverageClassManager coverageClassManager) {
        m_coverageClassManager = coverageClassManager;
    }


    public String getCoiAnchorColumnName() {
        return m_coiAnchorColumnName;
    }

    public void setCoiAnchorColumnName(String coiAnchorColumnName) {
        m_coiAnchorColumnName = coiAnchorColumnName;
    }

    public String getAffiAnchorColumnName() {
        return m_affiAnchorColumnName;
    }

    public void setAffiAnchorColumnName(String affiAnchorColumnName) {
        m_affiAnchorColumnName = affiAnchorColumnName;
    }

    public String getScheduleAnchorColumnName() {
        return m_scheduleAnchorColumnName;
    }

    public void setScheduleAnchorColumnName(String scheduleAnchorColumnName) {
        m_scheduleAnchorColumnName = scheduleAnchorColumnName;
    }

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String COVERAGE_GRID_ID = "coverageListGrid";
    protected static final String COMPONENT_GRID_ID = "componentListGrid";
    protected static final String COVERAGE_CLASS_GRID_ID = "coverageClassListGrid";
    protected static final String RISK_GRID_ID = "riskListGrid";
    protected static final String AFFILIATION_GRID_ID = "affiliationListGrid";
    protected static final String COI_GRID_ID = "coiListGrid";
    protected static final String SCHEDULE_GRID_ID = "scheduleListGrid";
    protected static final String COVERAGE_GRID_LAYER_ID = "PM_SRC_COVG_GH";
    protected static final String COMPONENT_GRID_LAYER_ID = "PM_SRC_COMP_GH";
    protected static final String COVG_CLASS_GRID_LAYER_ID = "PM_SRC_SUBCOVG_GH";
    protected static final String RISK_GRID_LAYER_ID = "PM_TGT_RISK_GH";
    protected static final String COVG_GRID_RECORD_SET = "covgGridRecordSet";
    protected static final String COMP_GRID_RECORD_SET = "compGridRecordSet";
    protected static final String COVG_CLASS_GRID_RECORD_SET = "covgClassGridRecordSet";
    protected static final String RISK_GRID_RECORD_SET = "riskGridRecordSet";

    private String m_covgClassAnchorColumnName;
    private String m_coverageAnchorColumnName;
    private String m_componentAnchorColumnName;
    private String m_coiAnchorColumnName;
    private String m_affiAnchorColumnName;
    private String m_scheduleAnchorColumnName;
    private String m_riskAnchorColumnName;
    private ComponentManager m_componentManager;
    private CoverageClassManager m_coverageClassManager;

}
