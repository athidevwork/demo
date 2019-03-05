package dti.pm.workflowmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;

import dti.pm.policymgr.PolicyHeader;
import dti.pm.workflowmgr.jobqueuemgr.JobQueueManager;
import dti.pm.workflowmgr.jobqueuemgr.JobRequest;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.busobjs.PolicyHeaderReloadCode;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageManagerAdmin;
import dti.oasis.recordset.Record;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Iterator;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:
 *
 * @author FCB
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/03/2008       fcb         1. finishJob renamed to cleanupJob.
 *                              2. continueMonitoring: added logic to handle failed jobs.
 * 04/27/2009       fcb         FINEST logging messages added.
 * 09/04/2009       fcb         continueMonitoring: set messages in the Message Manager
 *                              for completed jobs as well.
 * 05/20/2010       fcb         FINEST level chaged to FINE, plus other cosmetic changes.  
 * 02/20/2012       syang       130467 - Modified monitorLongRunningTransaction and continueMonitoring to display
 *                              the message based on the current transaction.
 * 01/03/2013       adeng       138680 - Changed the next flows to Product Notify for monitorInvokeSaveEndorsementQuote
 *                              in workflow, so modified continueMonitoring() to remove some useless logic for exist
 *                              monitor invoke save endorsement quote.
 * ---------------------------------------------------
 */

public class LongRunningTransactionAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS, "monitorLongRunningTransaction");
        return monitorLongRunningTransaction(mapping, form, request, response);
    }

    /**
    * This method is called to initialize the monitoring of a long running transaction.
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
     public ActionForward monitorLongRunningTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "monitorLongRunningTransaction", new Object[]{mapping, form, request, response});
        String msgKey = "";
        Iterator iter = MessageManager.getInstance().getInfoMessages();
        if (iter.hasNext()) {
            Message message = (Message) iter.next();
            msgKey = message.getMessageKey();
            request.setAttribute(LONG_TRANS_PROCESS_MSG_KEY, msgKey);
        }
        PolicyHeader policyHeader = getPolicyHeader(request);
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        if (wa.hasWorkflow(policyHeader.getPolicyNo()) &&
            (wa.getWorkflowCurrentState(policyHeader.getPolicyNo()).equals("monitorInvokeSaveEndorsementQuote"))) {
            // System displays the message based on the current transaction.
            String quoteType = "endorsement";
            if (policyHeader.getLastTransactionInfo().getTransactionTypeCode().isRenewal()) {
                quoteType = "renewal";
            }
            MessageManager.getInstance().addInfoMessage("pm.workflowmgr.save.official.invokeProcessSaveEndQuoteMsg.info", new Object[]{quoteType});
        }

        request.setAttribute(RequestIds.PROCESS, "continueMonitoring");
        MessageManager.getInstance().addInfoMessage("pm.workflowmgr.longRunningTransaction.monitorTransactionMsg.info");
        String forwardString = "continueMonitoring";
        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(),"monitorLongRunningTransaction","LRT - Action: started monitoring state, moving to " +
            forwardString + ".");
        }
        l.exiting(getClass().getName(), "monitorLongRunningTransaction", af);

        return af;
    }

    /**
    * This method is called to continue monitoring a long running transaction
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward continueMonitoring(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "continueMonitoring", new Object[]{mapping, form, request, response});
        String forwardString = "continueMonitoring";
        try {
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            String result = null;
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                String jobId = (String) wa.getWorkflowAttribute(policyHeader.getPolicyNo(), JobRequest.JOB_ID);
                JobRequest jobRequest = getJobQueueManager().getJob(jobId);

                if (l.isLoggable(Level.FINE)) {
                    String jobState;
                    synchronized(jobRequest) {
                        if (jobRequest.isJobFailed()) {
                            jobState = "FAILED";
                        }
                        else if (jobRequest.isJobComplete()) {
                            jobState = "COMPLETE";
                        }
                        else {
                            jobState = "SCHEDULED/RUNNING";
                        }
                    }
                    l.logp(Level.FINE, getClass().getName(),"continueMonitoring","LRT - Action: Policy " + policyHeader.getPolicyNo() +
                        " with jobId " + jobId + " is in " + jobState + " state.");
                }

                if (jobRequest.isJobFailed()) {
                    ((MessageManagerAdmin)MessageManager.getInstance()).setAllMessagesFromList(jobRequest.getMessages());
                    AppException ae = jobRequest.getException();
                    getJobQueueManager().cleanupJob(jobRequest);
                    l.throwing(getClass().getName(), "continueMonitoring", ae);
                    throw ae;
                }
                else if (jobRequest.isJobComplete()) {
                    Object returnObject = jobRequest.getReturnObject();
                    ((MessageManagerAdmin)MessageManager.getInstance()).setAllMessagesFromList(jobRequest.getMessages());
                    if (returnObject != null && returnObject instanceof String) {
                        if (jobRequest.getReturnObject()!=null) {
                            result = jobRequest.getReturnObject().toString();
                            wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), result);
                        }
                    }

                    forwardString = wa.getNextState(policyHeader.getPolicyNo());

                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                    getJobQueueManager().cleanupJob(jobRequest);
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(),"continueMonitoring","LRT - Action: Policy " + policyHeader.getPolicyNo() +
                            " with jobId " + jobId + " sets the Workflow next state to " + forwardString +
                            ", and Transition Parameter to " + result + ".");
                    }
                }
                else {
                    Record inputRecord = getInputRecord(request);
                    if (inputRecord.hasStringValue(LONG_TRANS_PROCESS_MSG_KEY)) {
                        String msgKey = inputRecord.getStringValue(LONG_TRANS_PROCESS_MSG_KEY);
                        request.setAttribute(LONG_TRANS_PROCESS_MSG_KEY, msgKey);
                        MessageManager.getInstance().addInfoMessage(msgKey);
                    }
                    MessageManager.getInstance().addInfoMessage("pm.workflowmgr.longRunningTransaction.monitorTransactionMsg.info");
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(),"continueMonitoring","LRT - Action: Policy " + policyHeader.getPolicyNo() +
                            " with jobId " + jobId + " remains in the state " + forwardString + ".");
                    }
                }

                // As a precautionary measure we notify the Job Queue Manager's monitor every time we monitor the job status.
                getJobQueueManager().notifyQueueMonitor();
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for monitorLongRunningTransaction.");
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to process long transaction.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "continueMonitoring", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getJobQueueManager() == null)
            throw new ConfigurationException("The required property 'jobQueueManager' is missing.");
    }

    public LongRunningTransactionAction() {}

    public JobQueueManager getJobQueueManager() {
        return m_jobQueueManager;
    }

    public void setJobQueueManager(JobQueueManager jobQueueManager) {
        m_jobQueueManager = jobQueueManager;
    }

    private JobQueueManager m_jobQueueManager;
    private static final String LONG_TRANS_PROCESS_MSG_KEY = "longTransProcessingMsgKey";
}
