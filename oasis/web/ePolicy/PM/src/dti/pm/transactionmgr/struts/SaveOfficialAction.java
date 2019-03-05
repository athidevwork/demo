package dti.pm.transactionmgr.struts;

import dti.cs.policynotificationmgr.NotificationTransactionTimeEnum;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.OasisUser;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.policyattributesmgr.PolicyAttributesManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.quotemgr.QuoteManager;
import dti.pm.transactionmgr.TransactionXrefFields;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalProcessManager;
import dti.pm.transactionmgr.renewalprocessmgr.RenewalProcessManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.busobjs.TransactionCode;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Action class for save as official
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 13, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/16/2009       yhyang      For Purge transaction, system should reload policyheader after save as official.
 * 08/10/2010       bhong       Move viewPmFmDiscrepancy out to new action class ViewPmFmDiscrepancyAction
 * 07/23/2015       Elvin       Issue 160360.
 * 06/23/2016       tzeng       167531 - Modified processSaveOfficial to judge if need add message to session after save
 *                                       official.
 * 08/26/2016       wdang       167534 - Modified processSaveOfficial to call processAutoPendingRenewal.
 * 02/22/2017       tzeng       168385 - Modified processSaveOfficial() to set requested transaction time.
 * 02/28/2017       mlm         183387 - Refactored to handle preview for long running transactions.
 * ---------------------------------------------------
 */

public class SaveOfficialAction extends PMBaseAction {

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
        request.setAttribute(RequestIds.PROCESS,"processSaveOfficial");
        return processSaveOfficial(mapping, form, request, response);
    }

    /**
    * This method is called when there the process parameter "viewRelatedPolicies"
    * sent in along the requested url.
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward processSaveOfficial(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processSaveOfficial", new Object[]{mapping, form, request, response});
        String forwardString;

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = policyHeader.toRecord();
            // Set the save type into the record
            inputRecord.setFieldValue("newSaveOption", "OFFICIAL");
            // Set notification transaction time to oasis user
            OasisUser oasisUser = UserSessionManager.getInstance().getUserSession().getOasisUser();
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            boolean hasWorkFlowB = wa.hasWorkflow(policyHeader.getPolicyNo());
            if (hasWorkFlowB) {
                String processId = wa.getWorkflowProcessId(policyHeader.getPolicyNo());
                String transactionTimeCode = NotificationTransactionTimeEnum.getTransactionTimeCodeByValue(processId);
                oasisUser.setRequestedTransactionTime(transactionTimeCode);
            }
            // Call the transactionmgr business component to perform the save logic
            getTransactionManager().processSaveTransactionAsOfficial(policyHeader, inputRecord);

            // Check if workflow exists, otherwise do nothing
            if (hasWorkFlowB) {
                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                // Because the Purge transaction flat cancels the reissued term and then save as official process removess the term,
                // system needd to display the last term prior to reissue.
                if (TransactionCode.PURGE.equals(policyHeader.getLastTransactionInfo().getTransactionCode())) {
                    getTransactionManager().setPolicyHeaderReloadCode(policyHeader, TransactionCode.PURGE);
                }

                if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_AUTOREN_BAT_SCHED)).booleanValue()){
                    PolicyHeaderFields.setLastOffTermEffToDate(inputRecord, policyHeader.getTermEffectiveToDate());
                    //Check if need to prompt after save official
                    if (getPolicyAttributesManager().isSaveOfficialPromptEnableForRenewalBatch(policyHeader) &&
                        getBatchRenewalProcessManager().hasBatchIncludePolicy(inputRecord)) {
                        Message message = new Message();
                        message.setMessageCategory(MessageCategory.JS_MESSAGE);
                        message.setMessageKey("pm.batchRenewalProcess.existInBatch.afterSaveOfficial.info");
                        UserSession userSession = UserSessionManager.getInstance().getUserSession();
                        if (!userSession.has(UserSessionIds.POLICY_SAVE_MESSAGE)) {
                            userSession.set(UserSessionIds.POLICY_SAVE_MESSAGE, new ArrayList<>());
                        }
                        List<Message> messageList = (List<Message>) userSession.get(UserSessionIds.POLICY_SAVE_MESSAGE);
                        messageList.add(message);
                    }

                    // Remove batch id from session
                    UserSession userSession = UserSessionManager.getInstance().getUserSession();
                    if (userSession.has(UserSessionIds.POLICY_BATCH_RENEWAL_ID)) {
                        userSession.remove(UserSessionIds.POLICY_BATCH_RENEWAL_ID);
                    }
                }

                getQuoteManager().processAutoPendingRenewal(policyHeader, inputRecord);

                if (SysParmProvider.getInstance().getSysParm(SysParmIds.ODS_INSTALLED, "N").equalsIgnoreCase("Y")) {
                    // ODS has been installed, no need to call the 3rd party interface from OASIS
                    UserSessionManager.getInstance().getUserSession().set(RequestIds.INVOKE_ODS, YesNoFlag.N);
                } else {
                    // ODS is not installed, need to call the 3rd party interface directly from OASIS
                    UserSessionManager.getInstance().getUserSession().set(RequestIds.INVOKE_ODS, YesNoFlag.Y);
                }
            } else {
                    throw new AppException(AppException.UNEXPECTED_ERROR,
                        "Failed to determine workflow for Save Official.");
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to perform save as official functionality.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processSaveOfficial", af);
        return af;
    }


    /**
    * This method is called when there are errors when saving tail transaction as official
    *
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward exitWorkflowWithError(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processSaveOfficial", new Object[]{mapping, form, request, response});
        String forwardString = "exitWorkflow";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = policyHeader.toRecord();

            //Delete WIP and Unlock policy
            getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to perform save as official functionality.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processSaveOfficial", af);
        return af;
    }

    /**
     * exit work flow with confirm for amalgamation
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward exitWorkflowWithAmalgamateConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "exitWorkflowWithAmalgamateConfirm", new Object[]{mapping, form, request, response});
        String forwardString = "closePageWithAmalgamateConfirm";

        try {
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get amalgamation policy No
            Record inputRecord = new Record();
            TransactionFields.setTransactionLogId(
                inputRecord, policyHeader.getLastTransactionInfo().getTransactionLogId());
            Record result = getTransactionManager().getAmalgamationLinkedPolicy(inputRecord);
            // publish amalgamation policy no
            publishOutputRecord(request, result);
            addJsMessages();
            
            // Check if workflow exists, otherwise do nothing
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to perform exitWorkflowWithAmalgamateConfirm close.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "exitWorkflowWithAmalgamateConfirm", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.amalgamation.saveAsOfficialFowardConfirm.info");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public SaveOfficialAction() {}

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public PolicyAttributesManager getPolicyAttributesManager() {
        return m_policyAttributesManager;
    }

    public void setPolicyAttributesManager(PolicyAttributesManager policyAttributesManager) {
        m_policyAttributesManager = policyAttributesManager;
    }

    public BatchRenewalProcessManager getBatchRenewalProcessManager() {
        return m_batchRenewalProcessManager;
    }

    public void setBatchRenewalProcessManager(BatchRenewalProcessManager batchRenewalProcessManager) {
        m_batchRenewalProcessManager = batchRenewalProcessManager;
    }

    private TransactionManager m_transactionManager;
    private PolicyAttributesManager m_policyAttributesManager;
    private BatchRenewalProcessManager m_batchRenewalProcessManager;

}




