package dti.pm.policymgr.analyticsmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.analyticsmgr.PredictiveAnalyticsManager;
import dti.pm.policymgr.analyticsmgr.PredictiveAnalyticsErrorsFields;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.tags.OasisFields;
import dti.oasis.struts.IOasisAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionRedirect;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The action class for Analytics Errors page.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 14, 2011
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/05/2011       ryzhao      124622 - For pages with multiple grids:
 *                              1) Pass gridId as the third parameter to the setDataBean() method
 *                                 for all but the first grid.
 *                              2) Pass gridId/layerId as the third/fourth parameter to the loadGridHeader() method
 *                                 for all but the first grid.
 * ---------------------------------------------------
 */
public class MaintainPredictiveAnalyticsErrorsAction extends PMBaseAction {
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
        return init(mapping, form, request, response);
    }

    /**
     * Search scoring errors.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward init(ActionMapping mapping, ActionForm form,
                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "init", new Object[]{mapping, form, request, response});
        }

        String forwardString = "initResult";
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String policyId = inputRecord.getStringValue(PolicyHeaderFields.POLICY_ID, "");

            boolean isForPolicy = !StringUtils.isBlank(policyId);
            if (isForPolicy) {
                forwardString = "initPolicyOpaErrorResult";
            } else {
                inputRecord.setFields(getPredictiveAnalyticsManager().getInitialValueForOpaError());
            }

            publishOutputRecord(request, inputRecord);

            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the Analytics Errors page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "init", null);
        return af;
    }

    /**
     * Load scoring error details.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllScoringErrorDetail(ActionMapping mapping, ActionForm form,
                                                   HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllScoringErrorDetail", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllScoringErrorDetailResult";
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = getPredictiveAnalyticsManager().loadAllScoringErrorDetail(inputRecord);

            setDataBean(request, rs, OPA_SCORE_ERROR_LOG_GRID_ID);

            setCurrentGridId(OPA_SCORE_ERROR_LOG_GRID_ID);
            loadGridHeader(request, null, OPA_SCORE_ERROR_LOG_GRID_ID, OPA_SCORE_ERROR_LOG_GRID_LAYER_ID);

            publishOutputRecord(request, inputRecord);

            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the Analytics Errors Details.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllScoringErrorDetail", null);
        return af;
    }

    /**
     * Search scoring errors.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward searchScoringError(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "searchScoringError", new Object[]{mapping, form, request, response});
        }

        String forwardString = "searchScoringErrorResult";
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);

            Record inputRecord = getInputRecord(request);

            // Set score req type to readonly for policy.
            String policyId = inputRecord.getStringValue(PolicyHeaderFields.POLICY_ID, "");
            if (!StringUtils.isBlank(policyId)) {
                fields.getField(PredictiveAnalyticsErrorsFields.SEARCH_CRITERIA_SCORE_REQ_TYPE_CODE).setIsReadOnly(true);
            }

            // Get max row to return.
            String maxRow = PredictiveAnalyticsErrorsFields.getMaxRow(inputRecord);
            if (StringUtils.isBlank(maxRow)) {
                maxRow = fields.getField(PredictiveAnalyticsErrorsFields.MAX_ROW).getDefaultValue();

                if (StringUtils.isBlank(maxRow)) {
                    maxRow = PredictiveAnalyticsErrorsFields.MaxRowValue.DEFAULT_MAX_ROW;
                }

                PredictiveAnalyticsErrorsFields.setMaxRow(inputRecord, maxRow);
            }

            try {
                RecordSet rs = getPredictiveAnalyticsManager().searchScoringError(inputRecord);
                setDataBean(request, rs);
                loadGridHeader(request);
            } catch (ValidationException ve) {
                // Handle the validation exception
                handleValidationException(ve, request);
            }

            publishOutputRecord(request, inputRecord);
            loadListOfValues(request, form);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to search Analytics Errors.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "searchScoringError", null);
        return af;
    }

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
    public ActionForward clear(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "clear", new Object[]{mapping, form, request, response});
        }

        String forwardString = "clearResult";
        ActionRedirect redirect = new ActionRedirect(mapping.findForward(forwardString));

        Record inputRecord = getInputRecord(request);
        String policyId = inputRecord.getStringValue(PolicyHeaderFields.POLICY_ID, "");

        if (!StringUtils.isBlank(policyId)) {
            redirect.addParameter(PolicyHeaderFields.POLICY_ID, policyId);
            redirect.addParameter(PredictiveAnalyticsErrorsFields.SEARCH_CRITERIA_SCORE_REQ_TYPE_CODE,
                PredictiveAnalyticsErrorsFields.getSearchCriteriaScoreReqTypeCode(inputRecord));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "clear", redirect);
        }

        return redirect;
    }

    public MaintainPredictiveAnalyticsErrorsAction() {
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     * <p/>
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        if (hasCurrentGridId()) {
            String currentGridId = getCurrentGridId();
            if (currentGridId.equals(OPA_SCORE_ERROR_LOG_GRID_ID)) {
                return getOpaScoreErrorLogAnchorColumnName();
            }

            return super.getAnchorColumnName();
        }
        else {
            return super.getAnchorColumnName();
        }
    }

    /**
     * Verify PredictiveAnalyticsManager in spring config.
     */
    public void verifyConfig() {
        if (getPredictiveAnalyticsManager() == null)
            throw new ConfigurationException("The required property 'predictiveAnalyticsManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getOpaScoreReqAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'opaScoreReqAnchorColumnName' is missing.");
        if (getOpaScoreErrorLogAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'opaScoreErrorLogAnchorColumnName' is missing.");
    }

    public PredictiveAnalyticsManager getPredictiveAnalyticsManager() {
        return m_predictiveAnalyticsManager;
    }

    public void setPredictiveAnalyticsManager(PredictiveAnalyticsManager predictiveAnalyticsManager) {
        m_predictiveAnalyticsManager = predictiveAnalyticsManager;
    }

    public String getOpaScoreReqAnchorColumnName() {
        return m_opaScoreReqAnchorColumnName;
    }

    public void setOpaScoreReqAnchorColumnName(String opaScoreReqAnchorColumnName) {
        m_opaScoreReqAnchorColumnName = opaScoreReqAnchorColumnName;
    }

    public String getOpaScoreErrorLogAnchorColumnName() {
        return m_opaScoreErrorLogAnchorColumnName;
    }

    public void setOpaScoreErrorLogAnchorColumnName(String opaScoreErrorLogAnchorColumnName) {
        m_opaScoreErrorLogAnchorColumnName = opaScoreErrorLogAnchorColumnName;
    }

    private PredictiveAnalyticsManager m_predictiveAnalyticsManager;
    private String m_opaScoreReqAnchorColumnName;
    private String m_opaScoreErrorLogAnchorColumnName;
    protected static final String OPA_SCORE_ERROR_LOG_GRID_ID = "opaScoreErrorLogGrid";
    protected static final String OPA_SCORE_ERROR_LOG_GRID_LAYER_ID = "PM_OPA_ERROR_LOG_GH";
}
