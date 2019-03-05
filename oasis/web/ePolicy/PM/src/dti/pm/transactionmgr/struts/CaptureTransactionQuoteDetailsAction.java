package dti.pm.transactionmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 28, 2008
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CaptureTransactionQuoteDetailsAction extends PMBaseAction {

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return display(mapping, form, request, response);
    }

    /**
     * Method to load the initial values for display.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward display(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "display", new Object[]{mapping, form, request, response});
        String forwardString = "success";

        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);

            // add all request parameters into a record
            Record inputRecord = getInputRecord(request);

            Record outputRecord = getTransactionManager().getInitialValuesForCaptureTransactionDetails(policyHeader, inputRecord);

            // publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);

            // Load the list of values after loading the data
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to capture transaction details information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "display", af);
        return af;
    }

      /**
     * Method to fullfill AJAX call to get the default values, invoked by javascript
     * when system parameter PM_SKIP_COMMENT_WIND is N
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */

    public ActionForward getInitialValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValues", new Object[]{mapping, form, request, response});

        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);

            // add all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // add the actionclassName for TransactionManager to get the default wb values
            Record defaultRecord = getTransactionManager().getInitialValuesForCaptureTransactionDetails(policyHeader, inputRecord);

            // create a new Record contains newXXXX fields, the prefix "new" are expected by the subsequent callers
            // we could not do in TransactionManager().getInitialValuesForCaptureTransactionDetails directly,
            // becaues the resulting record is also consumed by the non-ajax call:display

            Record ajaxRecord = new Record();
            ajaxRecord.setFieldValue("newAccountingDate",defaultRecord.getStringValue("accountingDate"));
            ajaxRecord.setFieldValue("newTransactionComment",""); // Comment is entered by user's input, not set by ajax
            if (defaultRecord.hasFieldValue("endorsementCode")) {
                ajaxRecord.setFieldValue("newEndorsementCode",defaultRecord.getStringValue("endorsementCode"));
            }
            ajaxRecord.setFieldValue("newDeclineReasonCode",""); // DeclineReasonCode is entered by user's input, not set by ajax

            writeAjaxXmlResponse(response, ajaxRecord, true);
        }

        catch (Exception e) {
            l.exiting(getClass().getName(), "getInitialValues", e);
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,"Problem to get initial values", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValues", af);
        return af;
    }

    /**
     * Method to fullfill AJAX call to validate accountingDate .
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */

    public ActionForward validateTransactionDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateTransactionDetails", new Object[]{mapping, form, request, response});

        String forwardString = null;
        try {
            // add all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Validation date (accountingDate change)
            getTransactionManager().validateTransactionDetails(inputRecord);
            writeEmptyAjaxXMLResponse(response);
        }

        catch (ValidationException ve) {
            //request.setAttribute(RequestIds.INITIAL_VALUES, inputRecord);
            Record validFields = ve.getValidFields();
            writeAjaxXmlResponse(response, validFields, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get default expiration date for capture Tran page.", e, response);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "validateTransactionDetails", af);
        return af;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    private TransactionManager m_transactionManager;

}
