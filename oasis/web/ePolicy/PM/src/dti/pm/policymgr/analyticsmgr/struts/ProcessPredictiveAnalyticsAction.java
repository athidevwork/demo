package dti.pm.policymgr.analyticsmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.analyticsmgr.PredictiveAnalyticsManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 06, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ProcessPredictiveAnalyticsAction extends PMBaseAction {

    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
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
        return loadAll(mapping, form, request, response);
    }

    /**
     * Load all scoring data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAll(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "loadAll", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get policy header.
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = policyHeader.toRecord();

            // Get initial values for page.
            Record outputRecord = (Record) request.getAttribute(INIT_RECORD);
            if (outputRecord == null) {
                outputRecord = getPredictiveAnalyticsManager().getInitialValueForOpa(policyHeader);
            }
            // Get scoring request.
            RecordSet requestRs = getPredictiveAnalyticsManager().loadAllRequest(inputRecord);
            // Get scoring result.
            RecordSet resultRs = getPredictiveAnalyticsManager().loadAllResult(inputRecord);
            // Get scoring reason.
            RecordSet reasonRs = getPredictiveAnalyticsManager().loadAllReason(inputRecord);

            // Publish output record.
            publishOutputRecord(request, outputRecord);

            // Set scoring request data into request
            setDataBean(request, requestRs);
            // Set scoring result data into request
            setDataBean(request, resultRs, RESULT_GRID_ID);
            // Set scoring reason data into request
            setDataBean(request, reasonRs, REASON_GRID_ID);

            loadListOfValues(request, form);

            // Load scoring request grid.
            loadGridHeader(request);
            // Set currentGridId to resultListGrid before load result gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, RESULT_GRID_ID);
            // Load result grid header
            loadGridHeader(request, null, RESULT_GRID_ID, RESULT_GRID_LAYER_ID);
            // Set currentGridId to reasonListGrid before load reason gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, REASON_GRID_ID);
            // Load reason grid header
            loadGridHeader(request, null, REASON_GRID_ID, REASON_GRID_LAYER_ID);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the Predictive Analytics page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAll", null);
        return af;
    }

    /**
     * Process Opa.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward processOpa(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processOpa", new Object[]{mapping, form, request, response});
        String forwardString = "processResult";
        Record inputRecord = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                // Get policy header.
                PolicyHeader policyHeader = getPolicyHeader(request);
                // Get all request parameters.
                inputRecord = getInputRecord(request);
                Record record = new Record();
                record.setFields(inputRecord);
                record.setFields(policyHeader.toRecord());
                getPredictiveAnalyticsManager().processOpa(record);
            }
        }
        catch (ValidationException ve) {
            // Handle the validation exception
            request.setAttribute(INIT_RECORD, inputRecord);
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to process opa .", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processOpa", af);
        return af;
    }

    /**
     * Verify PredictiveAnalyticsManager in spring config.
     */
    public void verifyConfig() {
        if (getPredictiveAnalyticsManager() == null)
            throw new ConfigurationException("The required property 'predictiveAnalyticsManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getResultAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'resultAnchorColumnName' is missing.");
        if (getReasonAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'reasonAnchorColumnName' is missing.");
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     * <p/>
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(RESULT_GRID_ID)) {
                return getResultAnchorColumnName();
            }
            else if (currentGridId.equals(REASON_GRID_ID)) {
                return getReasonAnchorColumnName();
            }
            else {
                return super.getAnchorColumnName();
            }
        }
        else {
            return super.getAnchorColumnName();
        }
    }

    public PredictiveAnalyticsManager getPredictiveAnalyticsManager() {
        return m_predictiveAnalyticsManager;
    }

    public void setPredictiveAnalyticsManager(PredictiveAnalyticsManager predictiveAnalyticsManager) {
        m_predictiveAnalyticsManager = predictiveAnalyticsManager;
    }

    public String getResultAnchorColumnName() {
        return m_resultAnchorColumnName;
    }

    public void setResultAnchorColumnName(String resultAnchorColumnName) {
        m_resultAnchorColumnName = resultAnchorColumnName;
    }

    public String getReasonAnchorColumnName() {
        return m_reasonAnchorColumnName;
    }

    public void setReasonAnchorColumnName(String reasonAnchorColumnName) {
        m_reasonAnchorColumnName = reasonAnchorColumnName;
    }

    private PredictiveAnalyticsManager m_predictiveAnalyticsManager;
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String REQUEST_GRID_ID = "requestListGrid";
    protected static final String RESULT_GRID_ID = "resultListGrid";
    protected static final String REASON_GRID_ID = "reasonListGrid";
    protected static final String REQUEST_GRID_LAYER_ID = "OPA_SCORING_REQUESTS_GH";
    protected static final String RESULT_GRID_LAYER_ID = "OPA_SCORING_RESULTS_GH";
    protected static final String REASON_GRID_LAYER_ID = "OPA_SCORING_REASONS_GH";
    private String m_resultAnchorColumnName;
    private String m_reasonAnchorColumnName;
    private String INIT_RECORD = "initRecord";

}
