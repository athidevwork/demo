package dti.pm.transactionmgr.premiumadjustmentprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.premiumadjustmentprocessmgr.PremiumAdjustmentManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Maintain Premium Adjustment.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 12, 2008
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/13/2018       wrong       192557 - Modified saveAllPremiumAdjustment() to call hasValidSaveToken() to be used for
 *                                       CSRFInterceptor.
 * ---------------------------------------------------
 */

public class MaintainPremiumAdjustmentAction extends PMBaseAction {
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
        return loadAllPremiumAdjustment(mapping, form, request, response);
    }

    /**
     * Method to load list of available risk for requested policy.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllPremiumAdjustment(ActionMapping mapping, ActionForm form,
                                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPremiumAdjustment", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            securePage(request, form);
            // Get the policy header from the request
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Load the coverages
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getPremiumAdjustmentManager().loadAllCoverage(policyHeader);
            }
            // Set loaded coverage data into request
            setDataBean(request, rs);
            // Load premium adjustment
            RecordSet premAdjustmentRs = (RecordSet) request.getAttribute(PERMIUM_ADJUSTMENT_RECORD_SET);
            if (premAdjustmentRs == null) {
                premAdjustmentRs = getPremiumAdjustmentManager().loadAllPremiumAdjustment(policyHeader, rs);
            }

            // Set loaded shared detail data into request
            setDataBean(request, premAdjustmentRs, PREMIUM_ADJUSTMENT_GRID_ID);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            output.setFields(premAdjustmentRs.getSummaryRecord(), false);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            loadGridHeader(request);
            // Set currentGridId to premiumAdjustmentListGrid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PREMIUM_ADJUSTMENT_GRID_ID);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            // Load premium adjustment grid header
            loadGridHeader(request, null, PREMIUM_ADJUSTMENT_GRID_ID, PERMIUM_ADJUSTMENT_GRID_LAYER_ID);
            // Populate messages for javascirpt
            addJsMessages();
            //set share sir column and share sir field visible or not

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the premium adjustment page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllPremiumAdjustment", af);
        return af;
    }


    /**
     * Save all updated premium adjustment records.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllPremiumAdjustment(ActionMapping mapping, ActionForm form,
                                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllPremiumAdjustment", new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                PolicyHeader policyHeader = getPolicyHeader(request);
                // Map sharedDetail textXML to RecordSet for input
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PREMIUM_ADJUSTMENT_GRID_ID);
                RecordSet premiumAdjustmentInputRecords = getInputRecordSet(request, PREMIUM_ADJUSTMENT_GRID_ID);

                String success = "Y";
                Record record = new Record();
                // Save all changes
                try {
                    getPremiumAdjustmentManager().saveAllPremiumAdjustment(policyHeader, premiumAdjustmentInputRecords);
                }
                catch (ValidationException e) {
                    success = "N";
                }
                record.setFieldValue("success", success);
                writeAjaxXmlResponse(response, record);
            }
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to save all premium adjustment.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "saveAllPremiumAdjustment", af);
        return af;
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
            if (currentGridId.equals(PREMIUM_ADJUSTMENT_GRID_ID)) {
                anchorName = getPremiumAdjustmentAnchorColumnName();
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

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainPremiumAdjustment.save.error");
        MessageManager.getInstance().addJsMessage("pm.maintainPremiumAdjustment.save.success");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getPremiumAdjustmentManager() == null)
            throw new ConfigurationException("The required property 'premiumAdjustmentManager' is missing.");
        if (getPremiumAdjustmentAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'sharedDetailAnchorColumnName' is missing.");
        if (super.getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public String getPremiumAdjustmentAnchorColumnName() {
        return premiumAdjustmentAnchorColumnName;
    }

    public void setPremiumAdjustmentAnchorColumnName(String premiumAdjustmentAnchorColumnName) {
        this.premiumAdjustmentAnchorColumnName = premiumAdjustmentAnchorColumnName;
    }


    public PremiumAdjustmentManager getPremiumAdjustmentManager() {
        return premiumAdjustmentManager;
    }

    public void setPremiumAdjustmentManager(PremiumAdjustmentManager premiumAdjustmentManager) {
        this.premiumAdjustmentManager = premiumAdjustmentManager;
    }


    public MaintainPremiumAdjustmentAction() {
    }

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String COVERAGE_GRID_ID = "coverageListGrid";
    protected static final String PREMIUM_ADJUSTMENT_GRID_ID = "premiumAdjustmentListGrid";
    protected static final String PERMIUM_ADJUSTMENT_GRID_LAYER_ID = "PM_PREM_ADJUSTMENT_GH";
    protected static final String PERMIUM_ADJUSTMENT_RECORD_SET = "premiumAdjustmentGridRecordSet";

    private PremiumAdjustmentManager premiumAdjustmentManager;
    private String premiumAdjustmentAnchorColumnName;
}
