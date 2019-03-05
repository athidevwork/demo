package dti.pm.policymgr.quickpaymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.quickpaymgr.QuickPayFields;
import dti.pm.policymgr.quickpaymgr.QuickPayManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * This is an action class for manage quick pay.
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   July 22, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/20/2010       dzhang      Update per Bill's comments.
 * 09/03/2010       dzhang      Move loadPolicyHeader position in loadAllQuickPay().
 * 09/09/2010       dzhang      #103800 - Modified loadAllQuickPay() to automatically load the data for the initialized
 *                              term, without having to select Search option for the first enter.
 * 04/12/2012       jshen       Issue 132152 - set field polNo and polHolderName values
 * 06/13/2018       wrong       192557 - Modified deleteQuickPayWip() to call hasValidSaveToken() to be used for
 *                                       CSRFInterceptor.
 * ---------------------------------------------------
 */

public class MaintainQuickPayAction extends PMBaseAction {
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
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);
            inputRecord.setFields(policyHeader.toRecord());
            Record output = new Record();
            //First enter the manage quick pay page, need to get the initial values for the search criteria.
            if (!inputRecord.hasStringValue("requestFromPageItself")) {
                Record initSearchRecord = getQuickPayManager().getInitialValuesForSearchCriteria(policyHeader);
                inputRecord.setFields(initSearchRecord);
                output.setFields(initSearchRecord);
            }
            // Get record for quick pay summary
            Record summaryRecord = getQuickPayManager().loadQuickPaySummary(inputRecord);
            // if no data found, add error message.
            if (summaryRecord == null) {
                MessageManager.getInstance().addErrorMessage("pm.manageQuickPay.summaryList.noDataFound.error");
            }
            else {
                output.setFields(summaryRecord, false);
            }
            // Get record set for grid - transaction history
            RecordSet rs = getQuickPayManager().loadAllTransactionHistory(inputRecord);
            // if no data found, add error message.
            if (rs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.manageQuickPay.transactionHistoryList.noDataFound.error");
            }

            output.setFields(inputRecord, false);
            output.setFields(rs.getSummaryRecord());
            QuickPayFields.setPolNo(output, PolicyHeaderFields.getPolicyNo(inputRecord));
            QuickPayFields.setPolHolderName(output, QuickPayFields.getPolicyHolderName(inputRecord));
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Set all data beans to request
            setDataBean(request, rs);

            // Load grid headers for the two grids, the grid header bean name is:
            loadGridHeader(request);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Add Js messages
            addJsMessages();

            // Set back the transaction log id that created by quick pay transaction.
            String wipQpTransLogId = getQuickPayManager().getLastWipQuickPayTransactionLogId(inputRecord);
            request.setAttribute("wipQpTransLogId", wipQpTransLogId);

        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the Manual Quick Pay page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllQuickPay", af);
        return af;
    }

    /**
     * Remove quick pay discount records.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward removeQuickPayDiscount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "removeQuickPayDiscount", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords = getInputRecordSet(request);
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);
                // Save the changes
                getQuickPayManager().removeQuickPayDiscount(inputRecords);
            }
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to remove quick pay discount page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "removeQuickPayDiscount", af);
        return af;
    }

    /**
     * Add quick pay discount by percent records.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward addQuickPayDiscount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "addQuickPayDiscount", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords = getInputRecordSet(request);
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);
                // Save the changes
                getQuickPayManager().addQuickPayDiscount(inputRecords);
            }
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to give quick pay discount by percent", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "addQuickPayDiscount", af);
        return af;
    }

    /**
     * Save all quick pay discount records.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward completeQuickPayTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "completeQuickPayTransaction", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords = getInputRecordSet(request);
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);
                // complete the quick pay transaction
                getQuickPayManager().completeQuickPayTransaction(inputRecords);
            }

        }
        catch (Exception e) {
            forwardString = handleError("pm.manageQuickPay.save.error", "Failed to complete quick pay transaction.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "completeQuickPayTransaction", af);
        return af;
    }

    /**
     * Method to check if the quick pay discount can be given to the selected record.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward validateForAddQuickPayDiscount(ActionMapping mapping, ActionForm form,
                                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateForAddQuickPayDiscount", new Object[]{mapping, form, request, response});

        try {
            // Secure page without load fields
            securePage(request, form, false);

            // Map request to record for input
            Record inputRecord = getInputRecord(request);

            // Get the additional info data  validation pass
            boolean isAddQuickPayAllowed = getQuickPayManager().isAddQuickPayAllowed(inputRecord);
            Record output = new Record();
            if (isAddQuickPayAllowed) {
                output.setFieldValue("isAddQuickPayAllowed", "Y");
            }
            else {
                output.setFieldValue("isAddQuickPayAllowed", "N");
            }

            // Send back xml data
            writeAjaxXmlResponse(response, output);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate for add quick pay discount.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateForAddQuickPayDiscount", af);
        return af;
    }

    /**
     * Method to get last quick pay transaction log id.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward getLastQuickPayTransactionLogId(ActionMapping mapping, ActionForm form,
                                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getLastQuickPayTransactionLogId", new Object[]{mapping, form, request, response});

        try {
            // Secure page without load fields
            securePage(request, form, false);

            // Map request to record for input
            Record inputRecord = getInputRecord(request);

            // Get the last quick pay transaction log id
            Record output = new Record();
            output.setFieldValue("lastQpTransLogId", getQuickPayManager().getLastQuickPayTransactionLogId(inputRecord));
            // Send back xml data
            writeAjaxXmlResponse(response, output);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get last quick pay transaction log id.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getLastQuickPayTransactionLogId", af);
        return af;
    }

    /**
     * Method to delete WIP data.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward deleteQuickPayWip(ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "deleteQuickPayWip", new Object[]{mapping, form, request, response});

        try {
            if (hasValidSaveToken(request)) {
                // Secure page without load fields
                securePage(request, form, false);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);
                getQuickPayManager().deleteQuickPayWip(inputRecord);

                // Send back xml data
                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to delete Quick Pay WIP data.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "deleteQuickPayWip", af);
        return af;
    }


    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.manageQuickPay.shortTermPolicy.error");
        MessageManager.getInstance().addJsMessage("pm.manageQuickPay.unsavedData.error");
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

    public MaintainQuickPayAction() {
    }

    public QuickPayManager getQuickPayManager() {
        return m_quickPayManager;
    }

    public void setQuickPayManager(QuickPayManager quickPayManager) {
        m_quickPayManager = quickPayManager;
    }

    private QuickPayManager m_quickPayManager;
}
