package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeaderFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for copy risk addresses and phone numbers
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 14, 2008
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class PerformRiskAddressPhoneCopyAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping info
     * @param form     action form
     * @param request  request that contains input param
     * @param response response
     * @return ActionForward
     * @throws Exception when something error
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllAddressPhone(mapping, form, request, response);
    }

    /**
     * This method load all policy for one entity.
     * <p/>
     *
     * @param mapping  mapping info
     * @param form     action form
     * @param request  request that contains input param
     * @param response response
     * @return ActionForward
     * @throws Exception when something error
     */
    public ActionForward loadAllAddressPhone(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAddressPhone", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // reset SELECT_IND_GH fields
            resetSelectIndicator(request);

            Record inputRecord = getInputRecord(request);

            // Load the Risk addresses and phone numbers
            RecordSet rs = getRiskManager().loadAllAddressPhone(inputRecord);

            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // if no data found, add warning message and throw validation exception directly.
            if (rs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.copyAddrPhone.nodata.error");
                throw new ValidationException("No address/phone data found error.");
            }

            RecordSet riskNameRs = getRiskManager().loadAllRiskForCopyAddressPhone(inputRecord);
            setDataBean(request, riskNameRs, RISK_NAME_GRID_ID);

            loadGridHeader(request);

            // Set currentGridId to riskNameListGrid before load risk name gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, RISK_NAME_GRID_ID);
            // Load risk name grid header
            loadGridHeader(request, null, RISK_NAME_GRID_ID, RISK_NAME_GRID_LAYER_ID);

            //add js messages to messagemanager for the current request
            addJsMessages();

            request.setAttribute("policyId", PolicyHeaderFields.getPolicyId(inputRecord));
        }
        catch (ValidationException ve) {
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to load the source risk's address and phone.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAddressPhone", af);
        return af;
    }

    /**
     * To handle copy risk addresses and phone numbers
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward copyAllAddressPhone(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyAllAddressPhone", new Object[]{mapping, form, request, response});
        }

        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);
            Record inputRecord = getInputRecord(request);

            // copy all risks
            Record resultRec = getRiskManager().copyAllAddressPhone(inputRecord);

            writeAjaxXmlResponse(response, resultRec, true);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (AppException ae) {
            handleErrorForAjax(ae.getMessageKey(), "copy risk addresses and phone numbers failed", ae, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "copy risk addresses and phone numbers failed", e, response);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyAllAddressPhone", null);
        }
        return null;
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
            if (currentGridId.equals(RISK_NAME_GRID_ID)) {
                anchorName = getRiskNameAnchorColumnName();
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
        MessageManager.getInstance().addJsMessage("pm.copyAddrPhone.addrPhone.noselection.error");
        MessageManager.getInstance().addJsMessage("pm.copyAddrPhone.copyToRisk.noselection.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getRiskNameAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'getRiskNameAnchorColumnName' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
    }

    public String getRiskNameAnchorColumnName() {
        return m_riskNameAnchorColumnName;
    }

    public void setRiskNameAnchorColumnName(String riskNameAnchorColumnName) {
        m_riskNameAnchorColumnName = riskNameAnchorColumnName;
    }

    public PerformRiskAddressPhoneCopyAction() {
    }

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String RISK_NAME_GRID_ID = "riskNameListGrid";
    protected static final String RISK_NAME_GRID_LAYER_ID = "PM_RISK_LIST_GH";

    private String m_riskNameAnchorColumnName;
}
