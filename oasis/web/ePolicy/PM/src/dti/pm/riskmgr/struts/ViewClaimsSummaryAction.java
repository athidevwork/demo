package dti.pm.riskmgr.struts;

import dti.ci.helpers.ICIConstants;
import dti.ci.summarymgr.SummaryManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for policy action Claims Summary
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 20, 2011
 *
 * @author Witti Fu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ViewClaimsSummaryAction extends PMBaseAction {


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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadAllClaims(mapping, form, request, response);
    }

    /**
     * Method to load all claims info for requested risk.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllClaims(ActionMapping mapping,
                                       ActionForm form,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllClaims", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue(ICIConstants.PK_PROPERTY, RiskFields.getEntityId(inputRecord));
            RecordSet rs = getSummaryManager().loadAllClaimsByEntity(inputRecord);
            setDataBean(request, rs);

            // Message handled here because eCIS SummaryManager is called
            if (rs.getSize() <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.viewClaimsSummary.noDataFound.error");
            } 

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, rs.getSummaryRecord());

            loadGridHeader(request);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the claims detail page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllClaims", af);
        return af;
    }

    /**
     * Method to load primary risk entity info for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getPrimaryRisk(ActionMapping mapping,
                                        ActionForm form,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPrimaryRisk", new Object[]{mapping, form, request, response});
        }

        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            Record inputRecord = getInputRecord(request);

            String riskId = inputRecord.getStringValue(RiskFields.RISK_ID, "");
            String entityId = inputRecord.getStringValue(RiskFields.ENTITY_ID, "");

            // Gets Policy Header
            PolicyHeader policyHeader = null;
            // If not invoked from risk page, we need to get entityId from risk header
            if (StringUtils.isBlank(entityId) && (StringUtils.isBlank(riskId) || Long.parseLong(riskId) > 0)) {
                try {
                    // If invoked from policy page, we need reload policy header with primary risk
                    if (StringUtils.isBlank(riskId)) {
                        request.setAttribute(RequestIds.POLICY_HEADER, null);
                    }
                    // If riskId is not null, it will return policy header with given risk
                    // if riskId is null, it will return policy header with primary risk;
                    // if no primary risk found, system will throw exception.
                    policyHeader = getPolicyHeader(request, true);
                } catch (AppException ae) {
                    // No primary risk exist
                    policyHeader = null;
                }
            }

            // If invoke from coverage/coverage class page, get entityId from risk header
            // if invoke from policy page, get primary riskId and entityId from risk header
            // if invoke from risk page, entityId and riskId are no need to get.
            if (StringUtils.isBlank(entityId) && policyHeader != null && policyHeader.hasRiskHeader()) {
                entityId = policyHeader.getRiskHeader().getRiskEntityId();
                riskId = policyHeader.getRiskHeader().getRiskId();
            }

            Record record = new Record();
            RiskFields.setRiskId(record, riskId);
            RiskFields.setEntityId(record, entityId);
            writeAjaxXmlResponse(response, record, true);

        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get the primary risk.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getPrimaryRisk", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getSummaryManager() == null)
            throw new ConfigurationException("The required property 'summaryManager' is missing.");
    }

    public SummaryManager getSummaryManager() {
        return m_SummaryManager;
    }

    public void setSummaryManager(SummaryManager summaryManager) {
        m_SummaryManager = summaryManager;
    }

    private SummaryManager m_SummaryManager;

}