package dti.pm.policymgr.quickpaymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.quickpaymgr.QuickPayManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.logging.Logger;

/**
 * This is an action class for process quick pay transaction.
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 3, 2010
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/19/2010       dzhang      Update per Bill's comments.
 * 09/01/2010       dzhang      103800 - Modified the MessageSource key.
 * ---------------------------------------------------
 */

public class ProcessQuickPayAction extends PMBaseAction {

    /**
     * do this process when no process is specified
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllQuickPay(mapping, form, request, response);
    }


    /**
     * Method to load quick pay data.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllQuickPay(ActionMapping mapping,
                                         ActionForm form,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllQuickPay", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            Record inputRecord = getInputRecord(request);

            // Get record for original transaction information
            Record output = getQuickPayManager().loadOriginalTransaction(inputRecord);

            // if no data found, add warning message.
            if (output == null) {
                output = new Record();
                MessageManager.getInstance().addErrorMessage("pm.processQuickPay.origTransInfoLayer.noDataFound.error");
            }

            // Get record set for grid - transaction history
            RecordSet rs = getQuickPayManager().loadAllRiskCoverageForOriginalTransaction(inputRecord);
            // if no data found, add warning message.
            if (rs.getSize() == 0) {
                EntitlementFields.setReadOnly(output, true);
                MessageManager.getInstance().addErrorMessage("pm.processQuickPay.riskCoverageList.noDataFound.error");
            }

            if (MessageManager.getInstance().hasErrorMessages()) {
                EntitlementFields.setReadOnly(output, true);
            }

            // Set all data beans to request
            setDataBean(request, rs);
            output.setFieldValue("qpAccountingDate", DateUtils.formatDate(new Date()));
            publishOutputRecord(request, output);
            // Load all grid headers for the gcatch (ValidationExceptionrid
            loadGridHeader(request);

            // Add Js messages
            addJsMessages();
            request.setAttribute("eligibleCount", rs.getSummaryRecord().hasStringValue("eligibleCount") ? rs.getSummaryRecord().getStringValue("eligibleCount") : "");
        }

        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the Process Quick Pay page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllQuickPay", af);
        return af;
    }



    /**
     * Save all quick pay transaction detail records.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */

    public ActionForward saveAllRiskCoverageForOriginalTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllRiskCoverageForOriginalTransaction", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords = getInputRecordSet(request);
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                Record inputRecord = getInputRecord(request);
                if(StringUtils.isBlank(inputRecord.getStringValue("hasAlreadySubmitted"))) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);
                // Save the changes
                getQuickPayManager().saveAllRiskCoverageForOriginalTransaction(inputRecords);
                }
            }
            request.setAttribute("hasAlreadySubmitted","Y");

        }
        catch (Exception e) {
            forwardString = handleError("pm.processQuickPay.save.error", "Failed to save the process quick pay.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllRiskCoverageForOriginalTransaction", af);
        return af;
    }


    /**
     * add js messages to message manager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.processQuickPay.qpAmountBlankOrZero.error");
        MessageManager.getInstance().addJsMessage("pm.processQuickPay.qpAmountGreaterThanTransAmount.error");
        MessageManager.getInstance().addJsMessage("pm.processQuickPay.indDiscountGreaterThanIndAmount.error");
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getQuickPayManager() == null)
            throw new ConfigurationException("The required property 'quickPayManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public ProcessQuickPayAction() {
    }

    public QuickPayManager getQuickPayManager() {
        return m_quickPayManager;
    }

    public void setQuickPayManager(QuickPayManager quickPayManager) {
        m_quickPayManager = quickPayManager;
    }

    private QuickPayManager m_quickPayManager;
}
