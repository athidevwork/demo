package dti.pm.transactionmgr.batchrenewalprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalProcessManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for display/save print device for renewal batch event.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 11, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/13/2018       wrong       192557 - Modified saveSubmitPrintingJob() to call hasValidSaveToken() to be used for
 *                                       CSRFInterceptor
 * ---------------------------------------------------
 */
public class CaptureRenewalBatchPrinterAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return display(mapping, form, request, response);
    }

    /**
     * Method to load As of Date.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward display(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "display", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, getInputRecord(request));

            // Populate messages for javascirpt
            addJsMessages();

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the renewal batch print selection page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "display", af);
        return af;
    }

    /**
     * To submit printing job.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveSubmitPrintingJob(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveSubmitPrintingJob", new Object[]{mapping, form, request, response});

        try {
            if (hasValidSaveToken(request)) {
                // If the request has valid save token, then proceed with save; if not forward to load page.
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map As of Date field to RecordSet for input
                Record inputRecord = getInputRecord(request);

                // Save the changes
                getBatchRenewalProcessManager().saveSubmitPrintingJob(inputRecord);

                // Send back xml data
                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException ve) {
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to submit printing job.", e, response);
        }

        // Return the forward
        l.exiting(getClass().getName(), "saveSubmitPrintingJob", null);
        return null;
    }

    /**
     * To validate As of Date.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward validateForCaptureRenewalBatchPrinter(ActionMapping mapping,
                                                               ActionForm form,
                                                               HttpServletRequest request,
                                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateForCaptureRenewalBatchPrinter",
            new Object[]{mapping, form, request, response});

        try {
            // If the request has valid save token, then proceed with save; if not forward to load page.
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);

            Record inputRecord = getInputRecord(request);

            getBatchRenewalProcessManager().validateForCaptureRenewalBatchPrinter(inputRecord);

            // Send back xml data
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException ve) {
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate renwal batch printer.", e, response);
        }

        // Return the forward
        l.exiting(getClass().getName(), "validateForCaptureRenewalBatchPrinter", null);
        return null;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.batchRenewalProcess.print.noSelection.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getBatchRenewalProcessManager() == null)
            throw new ConfigurationException("The required property 'batchRenewalProcessManager' is missing.");
    }

    public CaptureRenewalBatchPrinterAction() {
    }

    public BatchRenewalProcessManager getBatchRenewalProcessManager() {
        return m_batchRenewalProcessManager;
    }

    public void setBatchRenewalProcessManager(BatchRenewalProcessManager batchRenewalProcessManager) {
        m_batchRenewalProcessManager = batchRenewalProcessManager;
    }

    private BatchRenewalProcessManager m_batchRenewalProcessManager;
}
