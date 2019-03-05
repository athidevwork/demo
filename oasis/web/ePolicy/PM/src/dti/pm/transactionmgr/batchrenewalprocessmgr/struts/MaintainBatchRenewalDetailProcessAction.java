package dti.pm.transactionmgr.batchrenewalprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalFields;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalProcessManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for maintain batch renewal detail
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 28, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/21/08         yhchen     #81521 - initialize header title for search result list
 * 03/13/13         adeng      #138243 - Modified loadAllRenewalDetail to set isAllExcluded indicator into request.
 * 08/13/14         kxiang     #156446 - Added AddSelectIndLoadProcessor to involve select_ind in rs in
 *                                       loadAllRenewalDetail.
 * 11/19/2015       eyin        167171 - Modified loadAllRenewalDetail(), Add logic to process when recordSet is null.
 * ---------------------------------------------------
 */
public class MaintainBatchRenewalDetailProcessAction extends PMBaseAction {

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
        return maintainRenewalDetail(mapping, form, request, response);
    }

    /**
     * Method to load batch renewal detail page.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward maintainRenewalDetail(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "maintainRenewalDetail", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            RecordSet rs = new RecordSet();
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorIFrame(AppException.UNEXPECTED_ERROR, "Failed to load the Batch Renewal Detail page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "maintainRenewalDetail", af);
        return af;
    }

    /**
     * Method to load list of batch renewal event data.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllRenewalDetail(ActionMapping mapping,
                                              ActionForm form,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRenewalDetail", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Load the Manuscript
            Record inputRecord = getInputRecord(request);
            RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
            RecordSet rs = getBatchRenewalProcessManager().loadAllRenewalDetail(inputRecord,selectIndProcessor);
            setDataBean(request, rs);

            //initialize header title for result list
            String resultHeader = "pm.batchRenewalProcess.renewalDetail.gridHeader";
            String resultHeaderMsg = MessageManager.getInstance().formatMessage(resultHeader);
            if (rs != null && rs.getSize()>0 && rs.getFirstRecord().hasField("maxRows")){
                int intTotalRowsReturned = rs.getSize();
                int intMaxRowsConfigured = Integer.parseInt(rs.getFirstRecord().getStringValue("maxRows"));
                if(intTotalRowsReturned >= intMaxRowsConfigured){
                    resultHeader = "pm.batchRenewalProcess.renewalDetail.abortSearch.gridHeader";
                    resultHeaderMsg = MessageManager.getInstance().formatMessage(resultHeader,
                        new String[]{String.valueOf(intMaxRowsConfigured)});
                }
            }
            request.setAttribute(RENEW_LIST_HEADER, resultHeaderMsg);

            // Make the Summary Record available for output
            Record output = null;
            if (rs != null){
                output = rs.getSummaryRecord();
            }
            else {
                output = new Record();
            }

            if (output.hasField(BatchRenewalFields.IS_ALL_EXCLUDED)) {
                request.setAttribute(BatchRenewalFields.IS_ALL_EXCLUDED, output.getFieldValue(BatchRenewalFields.IS_ALL_EXCLUDED));
            }
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            loadGridHeader(request);

            // Populate messages for javascirpt
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorIFrame(AppException.UNEXPECTED_ERROR, "Failed to load the Batch Renewal Detail page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRenewalDetail", af);
        return af;
    }

    /**
     * Save all batch renewal exlusions.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllExcludePolicy(ActionMapping mapping,
                                              ActionForm form,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllExcludePolicy", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                // Save the changes
                getBatchRenewalProcessManager().saveAllExcludePolicy(inputRecords);
            }
        }
        catch (Exception e) {
            forwardString = handleErrorIFrame(AppException.UNEXPECTED_ERROR,
                "Failed to save all exclusions.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllExcludePolicy", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.batchRenewalProcess.nodata.found.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getBatchRenewalProcessManager() == null)
            throw new ConfigurationException("The required property 'batchRenewalProcessManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public MaintainBatchRenewalDetailProcessAction() {
    }

    public BatchRenewalProcessManager getBatchRenewalProcessManager() {
        return m_batchRenewalProcessManager;
    }

    public void setBatchRenewalProcessManager(BatchRenewalProcessManager batchRenewalProcessManager) {
        m_batchRenewalProcessManager = batchRenewalProcessManager;
    }
    protected static final String RENEW_LIST_HEADER = "resultHeader";
    private BatchRenewalProcessManager m_batchRenewalProcessManager;
}
