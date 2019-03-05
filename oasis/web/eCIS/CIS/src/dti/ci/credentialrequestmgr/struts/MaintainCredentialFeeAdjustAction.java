package dti.ci.credentialrequestmgr.struts;

import dti.ci.credentialrequestmgr.CredentialFeeAdjustManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class Credential Request Fee Adjustment page.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  03/04/2016
 *
 * @author jdingle
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public class MaintainCredentialFeeAdjustAction extends CIBaseAction {
    /**
     * Unspecified
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllServiceCharges(mapping, form, request, response);
    }

    /**
     * Initialize the Credential Request page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllServiceCharges(ActionMapping mapping, ActionForm form,
                                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllServiceCharges", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures page
            securePage(request, form);

            // Clear field values.
            Record inputRecord = getInputRecord(request);
            String billingAccountId = inputRecord.getStringValue("billingAccountId");

            // Load Service Charge Data.
            RecordSet rs = getCredentialFeeAdjustManager().loadAllServiceCharges(inputRecord);

            setDataBean(request, rs);
            loadGridHeader(request);

            request.setAttribute("billingAccountId", billingAccountId);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Publish the input record
            publishOutputRecord(request, output);

            loadListOfValues(request, form);

            saveToken(request);

            addJsMessages();
        } catch (Exception e) {
            forwardString = handleErrorPopup(
                    AppException.UNEXPECTED_ERROR, "Failed to initialize the Credential Fee Adjustment page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllServiceCharges", af);
        }
        return af;
    }

    /**
     * Save Credential Fee Adjustments.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllServiceCharges(ActionMapping mapping, ActionForm form,
                                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllServiceCharges", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        Record inputRecord = null;
        RecordSet feeRecords = null;
        int count = 0;

        try {
            // Secures page
            securePage(request, form);

            if (hasValidSaveToken(request)) {
                inputRecord = getInputRecord(request);
                feeRecords = getInputRecordSet(request);
                count = getCredentialFeeAdjustManager().saveAllServiceCharges(feeRecords);
                request.setAttribute("inputRecord", inputRecord);
                request.setAttribute("feeProcessed", "Y");
            }
        } catch (ValidationException ve) {
            // Save the input records into request.
            request.setAttribute("inputRecord", inputRecord);
            // Handle the validation exception
            handleValidationException(ve, request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(
                    AppException.UNEXPECTED_ERROR, "Failed to Save Credential Fee Adjustment.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllServiceCharges", af);
        }
        return af;
    }

    public void verifyConfig() {
        if (getCredentialFeeAdjustManager() == null) {
            throw new ConfigurationException("The required property 'credentialFeeAdjustManager' is missing.");
        }
        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
        if (getAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        }
    }

    /**
    * add js messages to messagemanager for the current request
    */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("js.select.row");
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public CredentialFeeAdjustManager getCredentialFeeAdjustManager() {
        return m_credentialFeeAdjustManager;
    }

    public void setCredentialFeeAdjustManager(CredentialFeeAdjustManager credentialFeeAdjustManager) {
        this.m_credentialFeeAdjustManager = credentialFeeAdjustManager;
    }

    private CredentialFeeAdjustManager m_credentialFeeAdjustManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
}
