package dti.pm.transactionmgr.batchrenewalprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalProcessManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends PMBaseAction for Common Anniversary processing.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 20, 2007
 *
 * @author fcb
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */

public class CreateCommonAnniversaryBatchRenewalProcessAction extends PMBaseAction {

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
        return captureCommonAnniversaryBatchRenewalDetail(mapping, form, request, response);
    }

    /**
     * load all the batch renewal data
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward captureCommonAnniversaryBatchRenewalDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "captureCommonAnniversaryBatchRenewalDetail", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            securePage(request, form);
            Record record = (Record)request.getAttribute(SAVED_INPUT_RECORD);
            if(record==null){
                record = getBatchRenewalProcessManager().getInitialValuesForCommonAnniversaryBatchRenew();
            }
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the commonAnniversaryBatchRenewalProcess.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "captureCommonAnniversaryBatchRenewalDetail", af);
        }
        return af;
    }

    /**
     * save batch renewal data
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward createCommonAnniversaryBatchRenewalProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                                    HttpServletResponse response) throws Exception {
        Logger l=LogUtils.getLogger(getClass());
        if(l.isLoggable(Level.FINER)){
            l.entering(getClass().getName(),"createCommonAnniversaryBatchRenewalProcess",new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        Record inputRecord = getInputRecord(request);
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form);
                getBatchRenewalProcessManager().createCommonAnniversaryBatchRenewalProcess(inputRecord);
                addJsMessages();
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(SAVED_INPUT_RECORD, inputRecord);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the commonAnniversaryBatchRenewalProcess page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        if(l.isLoggable(Level.FINER)){
            l.exiting(getClass().getName(),"createCommonAnniversaryBatchRenewalProcess",af);
        }
        return af;
    }

    /**
     * add js messages to message manager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.batchRenewalProcess.save.error");
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    /**
     * verify config
     */
    public void verifyConfig() {
        if (getBatchRenewalProcessManager() == null)
            throw new ConfigurationException("The required property 'batchRenewalProcessManager' is missing.");
    }

    /**
     *
     */
    public CreateCommonAnniversaryBatchRenewalProcessAction() {
    }
    /**
     * get batchRenewalProcessManager
     *
     * @return batchRenewalProcessManager
     */
    public BatchRenewalProcessManager getBatchRenewalProcessManager() {
        return m_batchRenewalProcessManager;
    }
    /**
     * set batchRenewalProcessManager
     * @param batchRenewalProcessManager batchRenewalProcess manager
     */
    public void setBatchRenewalProcessManager(BatchRenewalProcessManager batchRenewalProcessManager) {
        m_batchRenewalProcessManager = batchRenewalProcessManager;
    }


    protected static final String SAVED_INPUT_RECORD = "savedInputRecord";
    private BatchRenewalProcessManager m_batchRenewalProcessManager;
}
