package dti.pm.policymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.http.RequestIds;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for process quote status.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   April 30, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/21/2011       wfu         113063 - Change logic for trigger forms by quote status changing.
 * 08/09/2011       wfu         123690 - Remove pol header from session when reloading it to trigger multiple forms.
 * 10/24/2011       wfu         126311 - Added logic to support multiple quote versions only for official quotes.
 * 06/13/2018       wrong       192557 - Modified saveQuoteStatus() to call hasValidSaveToken() to be used for
 *                                       CSRFInterceptor.
 * ---------------------------------------------------
 */

public class ProcessQuoteStatusAction extends PMBaseAction {

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllQuoteStatus(mapping, form, request, response);
    }

    /**
     * Load all the status of a quote.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllQuoteStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllQuoteStatus", new Object[]{mapping, form, request, response});
        }
        String forwardString = "loadStatusResult";
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Gets all Quote Status
            RecordSet rs = (RecordSet)request.getAttribute(dti.oasis.http.RequestIds.GRID_RECORD_SET) ;
            if (rs== null) {
                rs = getPolicyManager().loadAllQuoteStatus(policyHeader);
            }

            // Default quote version's value when first loading or after saving
            Record inputRecord = getInputRecord(request);
            Record output = rs.getSummaryRecord();
            if (inputRecord.hasField(PolicyFields.QUOTE_VERSION + "MultiSelectText")) {
                // After saving quote status, quote version exists and sets it as the selected value.
                PolicyFields.setQuoteVersion(output, inputRecord.getStringValue(PolicyFields.QUOTE_VERSION, ""));
            } else {
                // First loading quote status page, set default value as current quote no.
                PolicyFields.setQuoteVersion(output, policyHeader.getPolicyNo());
            }

            // publish page field
            publishOutputRecord(request, output);
            // Sets data bean
            setDataBean(request, rs);
            // Loads list of values
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
            // Add Js messages
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Quote stauts page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllQuoteStatus", af);
        }
        return af;
    }

    /**
     * Save quote status
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveQuoteStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveQuoteStatus", new Object[]{mapping, form, request, response});
        String forwardString = "saveStatusResult";
        PolicyHeader policyHeader = null;
        RecordSet inputRecords = null;
        try {
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form);
                // Generate input records
                inputRecords = getInputRecordSet(request);
                Record input = inputRecords.getSummaryRecord();
                // Get policy header from the request
                policyHeader = getPolicyHeader(request);

                //Only support multiple quote versions for official quotes to trigger forms
                if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial() &&
                    input.hasStringValue(PolicyFields.QUOTE_VERSION)) {
                    String[] quoteVersion = input.getStringValue(PolicyFields.QUOTE_VERSION).split(",");
                    for (int i = 0; i < quoteVersion.length; i++) {
                        // Reload policy header using given policy no
                        request.removeAttribute(RequestIds.POLICY_HEADER);
                        RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
                        UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_HEADER);
                        policyHeader = getPolicyManager().loadPolicyHeader(quoteVersion[i], getClass().getName(), "Process Quote: saveQuoteStatus");
                        // Call the business component to save quote status
                        getPolicyManager().saveQuoteStatus(policyHeader, inputRecords);
                    }
                    // After saving complete, set policy header as original using old policy no exist in request parameter.
                    reloadPolicyHeaderForQuote(policyHeader, request);

                } else {
                    // Call the business component to implement the save/validate logic
                    getPolicyManager().saveQuoteStatus(policyHeader, inputRecords);
                }

                // Set parameter true if want to forward to capture transaction window if need to trigger forms
                if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial() &&
                    YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_QT_STATUS_CHG_FRM, "N")).booleanValue()) {
                    request.setAttribute(RequestIds.IS_TRIGGER_FORMS, YesNoFlag.Y);
                }
            }
        }
        catch (ValidationException v) {
            // If validate failed, set policy header as original using old policy no exist in request parameter.
            reloadPolicyHeaderForQuote(policyHeader, request);
            // Save the recordset into the request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            // If throw exception, set policy header as original using old policy no exist in request parameter.
            reloadPolicyHeaderForQuote(policyHeader, request);
            handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Quote stauts page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveQuoteStatus", af);
        return af;
    }

    /**
     * Method to trigger forms for requested quote versions and quote status.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward triggerForms(ActionMapping mapping,
                                      ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "triggerForms", new Object[]{mapping, form, request, response});
        }

        PolicyHeader policyHeader = null;
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String[] quoteVersion = null;
            if (inputRecord.hasStringValue(PolicyFields.QUOTE_VERSION + "MultiSelectText")) {
                quoteVersion = inputRecord.getStringValue(PolicyFields.QUOTE_VERSION + "MultiSelectText").split(",");
            } else if (inputRecord.hasStringValue(PolicyFields.QUOTE_VERSION)) {
                quoteVersion = inputRecord.getStringValue(PolicyFields.QUOTE_VERSION).split(",");
            } else {
                throw new AppException("Unable to get the quote versions.");
            }

            for (int i = 0; i < quoteVersion.length; i++) {
                // Reload policy header using given policy no
                request.removeAttribute(RequestIds.POLICY_HEADER);
                RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
                UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_HEADER);
                policyHeader = getPolicyManager().loadPolicyHeader(quoteVersion[i], getClass().getName(), "Process Quote: triggerForms");
                if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
                    getPolicyManager().triggerFormsFromQuoteStatus(policyHeader, inputRecord);
                }
            }
            // After triggering complete, set policy header as original using old policy no exist in request parameter.
            reloadPolicyHeaderForQuote(policyHeader, request);

            writeEmptyAjaxXMLResponse(response);

        } catch (Exception e) {
            // If throw exception, set policy header as original using old policy no exist in request parameter.
            reloadPolicyHeaderForQuote(policyHeader, request);
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to trigger forms from the status.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "triggerForms", af);
        return af;
    }

    /**
     * Private method to reload policy header for quote.
     *
     * @param policyHeader
     * @param request
     * @return
     * @throws Exception
     */
    private void reloadPolicyHeaderForQuote(PolicyHeader policyHeader, HttpServletRequest request) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "reloadPolicyHeaderForQuote", new Object[]{policyHeader, request});
        }

        if (policyHeader != null && !policyHeader.getPolicyNo().equals(request.getParameter(RequestIds.POLICY_NO))) {
            reloadPolicyHeader(request);
        }

        l.exiting(getClass().getName(), "reloadPolicyHeaderForQuote", null);
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.processQuoteStatus.same.status");
    }

    /**
     * Verify policyManager and anchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }
}
