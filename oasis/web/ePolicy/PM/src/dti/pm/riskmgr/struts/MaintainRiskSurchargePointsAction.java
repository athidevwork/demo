package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Maintain Risk Surcharge Points.
 * <p/>
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 03, 2010
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
public class MaintainRiskSurchargePointsAction extends PMBaseAction {
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
        return loadAllRiskSurchargePoint(mapping, form, request, response);
    }

    /**
     * Method to load all risk surcharge point.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllRiskSurchargePoint(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskSurchargePoint",
            new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Gets all risk surcharge points
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getRiskManager().loadAllRiskSurchargePoint(policyHeader, getInputRecord(request));
            }
            // Sets data Bean
            setDataBean(request, rs);
            // Publish out parameter
            publishOutputRecord(request, rs.getSummaryRecord());
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to load risk surcharge points page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRiskSurchargePoint", af);
        return af;
    }

    /**
     * Method to save surcharge points.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllRiskSurchargePoint(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllRiskSurchargePoint", new Object[]{mapping, form, request, response});
        ActionForward af;
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        try {
            // If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page.
                securePage(request, form, false);
                // Get input records.
                inputRecords = getInputRecordSet(request);
                // Save risk surcharge point.
                getRiskManager().saveAllRiskSurchargePoint(inputRecords);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup("pm.maintainRiskSurchargePoints.saved.date.failed",
                "Failed to save surcharge points.", e, request, mapping);
        }

        af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllRiskSurchargePoint", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    private RiskManager m_riskManager;
}