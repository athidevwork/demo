package dti.pm.quotemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.oasis.workflowmgr.WorkflowFields;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.core.session.UserSessionIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policyattributesmgr.PolicyAttributesFactory;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.quotemgr.QuoteFields;
import dti.pm.quotemgr.QuoteManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionXrefFields;
import dti.pm.transactionmgr.renewalprocessmgr.RenewalProcessManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import weblogic.wsee.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class to maintain quote transfer page
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   April 27, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/26/2016       wdang       167534 - Initial version.
 * 09/07/2016       wdang       179350 - Modified performCopy to pass Policy Header.
 * ---------------------------------------------------
 */
public class MaintainQuoteTransferAction extends PMBaseAction {

    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping       mapping
     * @param form          form
     * @param request       request
     * @param response      response
     * @return              action forward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadQuoteTransfer(mapping, form, request, response);
    }

    /**
     * Method to Copy quotes from policy/quote.
     *
     * @param mapping       mapping
     * @param form          form
     * @param request       request
     * @param response      response
     * @return              action forward
     * @throws Exception
     */
    public ActionForward performCopy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performCopy", new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);

                PolicyHeader policyHeader = getPolicyHeader(request);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                //quoteManger handle copy policy to quote process
                Record outputRecord = getQuoteManager().performCopy(policyHeader, inputRecord);

                writeAjaxXmlResponse(response, outputRecord);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to copy quote.", e, response);
        }
        l.exiting(getClass().getName(), "performCopy", null);
        return null;
    }

    /**
     * Method to load all quotes to be transferred for requested policy.
     *
     * @param mapping       mapping
     * @param form          form
     * @param request       request
     * @param response      response
     * @return              action forward
     * @throws Exception
     */
    public ActionForward loadQuoteTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadQuoteTransfer", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without
            // loading LOVs, and map the input parameters into the fields.
            securePage(request, form);

            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Gets quote information.
            Record inputRecord = getInputRecord(request);

            // Gets grid record set
            RecordSet rs = (RecordSet)request.getAttribute(RequestIds.GRID_RECORD_SET) ;

            if (policyHeader.getPolicyCycleCode().isPolicy() &&
                PolicyAttributesFactory.getInstance().isDisplayQuoteInViewModeEnable(
                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate(),
                policyHeader.getPolicyCycleCode(),
                policyHeader.getQuoteCycleCode(),
                policyHeader.getLastTransactionInfo().getTransactionTypeCode(),
                policyHeader.getRecordMode(),
                policyHeader.getPolicyStatus())) {

                if (rs == null) {
                    // Build select mode
                    Record record = policyHeader.toRecord();
                    record.setFields(inputRecord);
                    record.setFieldValue(QuoteFields.SELECT_MODE, QuoteFields.joinSelectMode(
                        QuoteFields.EXCL_SELF, QuoteFields.TERM_SENS, QuoteFields.EXCL_NB,
                        QuoteFields.EXCL_INVALID, QuoteFields.EXCL_ACCEPTED));

                    // Load quote information
                    rs = getQuoteManager().loadQuoteVersions(record);
                    rs.setFieldValueOnAll(RequestIds.SELECT_IND, 0, true);
                    rs.setFieldValueOnAll(QuoteFields.TRANSFER_STATUS, null, true);
                }
            }
            else if (rs == null) {
                rs = new RecordSet();
            }

            // Publish page field
            publishOutputRecord(request, inputRecord);

            // Sets data bean
            setDataBean(request, rs);

            // Loads list of values
                loadListOfValues(request, form);

            // Load grid header bean
            loadGridHeader(request);

            // Add js messages to messagemanager for the current request
            addJsMessages();

            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo()) && (rs == null || rs.getSize() == 0)) {
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadQuoteTransfer page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadQuoteTransfer", af);
        return af;
    }

    /**
     * Method to save all quote transfer information.
     *
     * @param mapping       mapping
     * @param form          form
     * @param request       request
     * @param response      response
     * @return              action forward
     * @throws Exception
     */
    public ActionForward performTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performTransfer", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (isTokenValid(request, true)) {
                // Secure page
                securePage(request, form, false);

                //get policy header
                PolicyHeader policyHeader = getPolicyHeader(request, true);

                // get input
                Record inputRecord = getInputRecord(request);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                // Call the business component to implement the validate/save logic
                for (Record r : inputRecords.getRecordList()) {
                    if ("-1".equals(r.getStringValue(RequestIds.SELECT_IND))) {
                        inputRecord.setFieldValue(QuoteFields.QUOTE_ID, r.getStringValue(QuoteFields.POLICY_ID));
                        inputRecord.setFieldValue(QuoteFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
                        try {
                            getQuoteManager().performTransfer(inputRecord);
                            r.setFieldValue(QuoteFields.TRANSFER_STATUS, QuoteFields.SUCCESS);
                        }
                        catch (Exception e) {
                            l.log(Level.SEVERE, "Failed to transfer changes to " + r.getStringValue(QuoteFields.POLICY_NO, ""), e);
                            r.setFieldValue(QuoteFields.TRANSFER_STATUS, QuoteFields.FAILURE);
                        }
                    }
                    else {
                        r.setFieldValue(QuoteFields.TRANSFER_STATUS, null);
                    }
                }
                request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

                // Check if workflow exists, otherwise just forward to the original
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa != null && wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    // Get the next state
                    forwardString = wa.getWorkflowCurrentState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
            }
        }
        catch (ValidationException v) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save quote transfer.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performTransfer", af);
        }
        return af;
    }

    /**
     * Method to apply renewal quote to renewal WIP.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performApply (
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performApply", new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);

                PolicyHeader policyHeader = getPolicyHeader(request);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                inputRecord.setFields(policyHeader.toRecord());
                RecordSet rs = getQuoteManager().loadQuoteVersions(inputRecord).getSubSet
                    (new RecordFilter(PolicyFields.POLICY_CYCLE_CODE, PolicyCycleCode.POLICY.getName()));
                rs = getQuoteManager().loadQuoteVersions(inputRecord);
                if (rs.getSize() == 0) {
                    throw new ValidationException("pm.maintainQuoteTransfer.noPolicy.error");
                }

                Record record = new Record();
                record.setFieldValue(QuoteFields.POLICY_ID, rs.getRecord(0).getFieldValue(QuoteFields.POLICY_ID));
                record.setFieldValue(QuoteFields.PARALLEL_POL_NO, rs.getRecord(0).getFieldValue(QuoteFields.POLICY_NO));
                record.setFieldValue(QuoteFields.QUOTE_ID, policyHeader.getPolicyId());
                //quoteManger handle apply quote to policy
                getQuoteManager().performApply(record);

                writeAjaxXmlResponse(response, record);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to apply quote.", e, response);
        }
        l.exiting(getClass().getName(), "performApply", null);
        return null;
    }

    /**
     * Method to merge Renewal WIP.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performMerge (
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performMerge", new Object[]{mapping, form, request, response});
        try {
            // Secures access to the page
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request);

            // Map request to record for input
            Record inputRecord = getInputRecord(request);

            // Renew policy
            inputRecord.setFieldValue("pm.maintainRenewal.confirm.applyPRT.confirmed", YesNoFlag.Y);
            Record outputRecord = getRenewalProcessManager().renewPolicy(policyHeader, inputRecord);

            // merge
            Record xrefRecord = new Record();
            String lastTransactionId = policyHeader.getLastTransactionId();
            xrefRecord.setFieldValue(TransactionXrefFields.XREF_TYPE, TransactionXrefFields.AUTO_PENDING_RENEWAL);
            xrefRecord.setFieldValue(TransactionXrefFields.RELATED_TRANS_ID, lastTransactionId);
            boolean mergeB = getTransactionManager().hasTransactionXref(xrefRecord);
            if (mergeB) {
                xrefRecord.setFieldValue(TransactionXrefFields.ORIGINAL_TRANS_ID, outputRecord.getStringValue("rc"));
                xrefRecord.setFieldValue(TransactionXrefFields.RELATED_TRANS_ID, lastTransactionId);
                getQuoteManager().performMerge(xrefRecord);
            }

            // Open window when loading page.
            if (mergeB) {
                Record record = new Record();
                record.setFieldValue(PolicyFields.POLICY_ID, policyHeader.getPolicyId());
                record.setFieldValue(QuoteFields.SELECT_MODE, QuoteFields.joinSelectMode(
                    QuoteFields.EXCL_SELF, QuoteFields.TERM_SENS, QuoteFields.EXCL_NB,
                    QuoteFields.EXCL_INVALID, QuoteFields.EXCL_ACCEPTED));

                RecordSet rs = getQuoteManager().loadQuoteVersions(record);
                if (rs.getSize() > 0) {
                    Message message = new Message();
                    message.setMessageCategory(MessageCategory.JS_MESSAGE);
                    message.setMessageKey("pm.maintainQuoteTransfer.autoOpenWindow");
                    UserSession userSession = UserSessionManager.getInstance().getUserSession();
                    if (!userSession.has(UserSessionIds.POLICY_SAVE_MESSAGE)) {
                        userSession.set(UserSessionIds.POLICY_SAVE_MESSAGE, new ArrayList<>());
                    }
                    List<Message> messageList = (List<Message>) userSession.get(UserSessionIds.POLICY_SAVE_MESSAGE);
                    messageList.add(message);
                }
            }

            writeAjaxXmlResponse(response, (Record)null);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to merge pending renewal.", e, response);
        }
        l.exiting(getClass().getName(), "performMerge", null);
        return null;
    }

    /**
     * This method is called when the process parameter "closePage"
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return ActionForward the forward
     * @throws Exception if there are some errors
     */
    public ActionForward closePage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "closePage", new Object[]{mapping, form, request, response});
        String forwardString = "closePage";

        try {
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Check if workflow exists, otherwise just forward to the original
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to determine validation closure method.", e, request, mapping);
        }

        // Done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "closePage", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainQuoteTransfer.noselection.error");
        MessageManager.getInstance().addJsMessage("pm.maintainQuoteTransfer.noPolicy.error");
    }

    public QuoteManager getQuoteManager() {
        return m_quoteManager;
    }

    public void setQuoteManager(QuoteManager quoteManager) {
        m_quoteManager = quoteManager;
    }

    public RenewalProcessManager getRenewalProcessManager() {
        return m_renewalProcessManager;
    }

    public void setRenewalProcessManager(RenewalProcessManager renewalProcessManager) {
        m_renewalProcessManager = renewalProcessManager;
    }

    private QuoteManager m_quoteManager;
    private RenewalProcessManager m_renewalProcessManager;
}
