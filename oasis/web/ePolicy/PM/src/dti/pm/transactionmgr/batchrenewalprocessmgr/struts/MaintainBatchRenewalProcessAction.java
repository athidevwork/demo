package dti.pm.transactionmgr.batchrenewalprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.core.http.RequestIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalProcessManager;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalFields;
import dti.pm.busobjs.SysParmIds;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for maintain batch renewal process
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
 * 04/08/2014       awu          Added method releaseOutput.
 * 06/23/2016       tzeng        Added addPolicyToBatch, excludePolicyFromBatch.
 * 06/13/2018       wrong       192557 - Modified addPolicyToBatch() to call hasValidSaveToken() to be used for
 *                                       CSRFInterceptor.
 * ---------------------------------------------------
 */
public class MaintainBatchRenewalProcessAction extends PMBaseAction {
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
        return maintainRenewalEvent(mapping, form, request, response);
    }

    /**
     * Method to load batch renewal event page.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward maintainRenewalEvent(ActionMapping mapping,
                                              ActionForm form,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "maintainRenewalEvent", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            RecordSet rs = new RecordSet();
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // get term type
            String termType = SysParmProvider.getInstance().getSysParm(
                SysParmIds.PM_REN_EVT_TERM_DFLT, BatchRenewalFields.PolicyTermTypeCodeValues.COMMON);
            termType = termType.toUpperCase();
            BatchRenewalFields.setTermType(output, termType);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the Batch Renewal Event page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "maintainRenewalEvent", af);
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
    public ActionForward loadAllRenewalEvent(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRenewalEvent", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            loadGridHeader(request);

            // Load the Manuscript
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
                rs = getBatchRenewalProcessManager().loadAllRenewalEvent(getInputRecord(request), selectIndProcessor);
            }
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Populate messages for javascirpt
            addJsMessages();

        }
        catch (ValidationException ve) {
            setDataBean(request, new RecordSet());
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the Batch Renewal Event page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRenewalEvent", af);
        return af;
    }

    /**
     * To issue batch renewal event data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveIssueRenewalBatches(ActionMapping mapping,
                                                 ActionForm form,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveIssueRenewalBatches", new Object[]{mapping, form, request, response});
        }

        RecordSet records = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form);

                records = getInputRecordSet(request);
                RecordSet changedRecords = records.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
                if (changedRecords.getSize() > 0) {
                    Record changedRecord = changedRecords.getRecord(0);
                    getBatchRenewalProcessManager().saveIssueRenewalBatches(changedRecord);
                }

            }
        }
        catch (ValidationException ve) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, records);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to issue batch renewal event data.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveIssueRenewalBatches", af);
        }
        return af;
    }

    /**
     * To issue batch renewal event data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward mergeIssueRenewalBatches(ActionMapping mapping,
                                                  ActionForm form,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mergeIssueRenewalBatches", new Object[]{mapping, form, request, response});
        }

        RecordSet records = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form);

                records = getInputRecordSet(request);
                getBatchRenewalProcessManager().saveMergeRenewalEvents(records);

            }
        }
        catch (ValidationException ve) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, records);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to merge batch renewal events.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "mergeIssueRenewalBatches", af);
        }
        return af;
    }

    /**
     * To delete batch renewal event data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteRenewalWipBatches(ActionMapping mapping,
                                                      ActionForm form,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteRenewalWipBatches", new Object[]{mapping, form, request, response});
        }

        RecordSet records = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                records = getInputRecordSet(request);
                RecordSet changedRecords = records.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
                getBatchRenewalProcessManager().deleteRenewalWipBatches(changedRecords);
                MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.delete.success.info");
            }
        }
        catch (ValidationException ve) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, records);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError("pm.batchRenewalProcess.delete.failed.error",
                "Failed to delete batch renewal event data.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteRenewalWipBatches", af);
        }
        return af;
    }

    /**
     * To delete batch renewal event data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward rerateRenewalPolicyBatches(ActionMapping mapping,
                                                    ActionForm form,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "rerateRenewalPolicyBatches", new Object[]{mapping, form, request, response});
        }

        RecordSet records = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                records = getInputRecordSet(request);
                RecordSet changedRecords = records.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
                getBatchRenewalProcessManager().rerateRenewalPolicyBatches(changedRecords);
                MessageManager.getInstance().addInfoMessage("pm.batchRenewalProcess.rerate.success.info");
            }
        }
        catch (ValidationException ve) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, records);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError("pm.batchRenewalProcess.rerate.failed.error",
                "Failed to rerate renewal policies.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "rerateRenewalPolicyBatches", af);
        }
        return af;
    }

    /**
     * To release forms for a renewal event
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward releaseOutput(ActionMapping mapping,
                                           ActionForm form,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "releaseOutput", new Object[]{mapping, form, request, response});
        }

        RecordSet records = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                records = getInputRecordSet(request);
                RecordSet changedRecords = records.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
                if (changedRecords.getSize() > 0) {
                    Record changedRecord = changedRecords.getRecord(0);
                    getBatchRenewalProcessManager().releaseOutput(changedRecord);
                }
            }
        }
        catch (ValidationException ve) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, records);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError("pm.batchRenewalProcess.release.failed.error",
                "Failed to release renewal policies.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "releaseOutput", af);
        }       
        return af;
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addPolicyToBatch(ActionMapping mapping,
                                                ActionForm form,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "addPolicyToBatch", new Object[]{mapping, form, request, response});

        try {
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form, false);

                // Get policy batch renewal id from user session
                UserSession userSession = UserSessionManager.getInstance().getUserSession();

                // Pull the policy header from request
                PolicyHeader policyHeader = getPolicyHeader(request);

                // Get input record from policy header
                Record inputRecord = policyHeader.toRecord();

                if (userSession != null && userSession.has(UserSessionIds.POLICY_BATCH_RENEWAL_ID)) {
                    // Get batch renewal id from user session
                    String policyBatchRenewalId = Integer.toString((Integer) userSession.get(UserSessionIds.POLICY_BATCH_RENEWAL_ID));

                    // Put parameters to input record
                    BatchRenewalFields.setRenewalEventId(inputRecord, policyBatchRenewalId);
                }

                getBatchRenewalProcessManager().addPolicyToBatch(inputRecord);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to add policy to batch.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addPolicyToBatch", af);
        }
        return af;
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward excludePolicyFromBatch(ActionMapping mapping,
                                                ActionForm form,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "excludePolicyFromBatch", new Object[]{mapping, form, request, response});

        try {
            // Secure page
            securePage(request, form, false);

            // Get policy batch renewal id from user session
            UserSession userSession = UserSessionManager.getInstance().getUserSession();

            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get input record from policy header
            Record inputRecord = policyHeader.toRecord();

            PolicyHeaderFields.setLastOffTermEffToDate(inputRecord, policyHeader.getTermEffectiveFromDate());

            // Exclude policy from batch event
            getBatchRenewalProcessManager().excludePolicyFromBatch(inputRecord);

            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to exclude policy from batch.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "excludePolicyFromBatch", af);
        }
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.batchRenewalProcess.unsaved.data.error");
        MessageManager.getInstance().addJsMessage("pm.batchRenewalProcess.noSelection.error");
        MessageManager.getInstance().addJsMessage("pm.batchRenewalProcess.merge.selection.error");
        MessageManager.getInstance().addJsMessage("pm.batchRenewalProcess.release.no.policy.info");
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

    public MaintainBatchRenewalProcessAction() {
    }

    public BatchRenewalProcessManager getBatchRenewalProcessManager() {
        return m_batchRenewalProcessManager;
    }

    public void setBatchRenewalProcessManager(BatchRenewalProcessManager batchRenewalProcessManager) {
        m_batchRenewalProcessManager = batchRenewalProcessManager;
    }

    private BatchRenewalProcessManager m_batchRenewalProcessManager;
}
