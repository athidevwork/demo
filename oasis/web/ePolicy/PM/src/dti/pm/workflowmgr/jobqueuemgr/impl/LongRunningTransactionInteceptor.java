package dti.pm.workflowmgr.jobqueuemgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.policymgr.PolicyHeader;

import dti.pm.workflowmgr.jobqueuemgr.JobCategory;
import dti.pm.workflowmgr.jobqueuemgr.JobRequest;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.core.request.RequestStorageIds;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 27, 2008
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/06/2008       fcb         Added logic to implement short and medium range transactions.
 * 05/13/2008       fcb         PROCESS_JOB renamed to REQUEST_ID_PROCESS_JOB
 *                              call to setReturnType removed.
 * 04/27/2009       fcb         FINEST logging messages added.
 * 05/20/2010       fcb         FINEST level changed to FINE, plus other cosmetic changes.
 * 06/28/2010       fcb         109187: policyLockId set into the jobRequest. 
 * 09/20/2012       fcb         Issue 136956: added logic to set the User Id in the Job Request.
 * ---------------------------------------------------
 */
public class LongRunningTransactionInteceptor implements MethodInterceptor {
    /**
     * The Workflow State that will be used while a job is either scheduled
     * or executing on a background thread.
     */
    public static final String MONITOR_LONG_RUNNING_TRANSACTION = "MONITOR_LONG_RUNNING_TRANSACTION";

    /**
     * Method to either execute a job imediate if it is a short job, or to schedule
     * it for later execution if it is medium or long. 
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
           l.entering(getClass().getName(), "invoke", new Object[]{methodInvocation});
        }

        Object objResult=null;
        RequestStorageManager rsm = RequestStorageManager.getInstance();

        JobCategory category = JobCategory.SHORT;
        if(!rsm.has(JobProcessor.REQUEST_ID_PROCESS_JOB)) {
            category = getJobQueueManager().getJobCategoryEvaluator().evaluate();

            if (!category.isShort()) {
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                PolicyHeader policyHeader = (PolicyHeader) rsm.get(RequestStorageIds.POLICY_HEADER);
                if (!wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    // Set the category to short to execute immediate, and log a warning.
                    category = JobCategory.SHORT;
                    l.logp(Level.WARNING, getClass().getName(), "LongRunningTransactionInteceptor.invoke",
                        "No workflow configured for long running transaction for policy '" +
                            policyHeader.getPolicyNo() + "'");
                }
            }
        }

        if (category.isShort()) {
            objResult = methodInvocation.proceed();
        }
        else if (category.isMedium() || category.isLong()) {
            //Verify that we are in a Workflow.
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            PolicyHeader policyHeader = (PolicyHeader) rsm.get(RequestStorageIds.POLICY_HEADER);

            JobRequest jobRequest = new JobRequest(policyHeader.getPolicyNo());
            try {
                jobRequest.setMethod(methodInvocation.getMethod().getName());
                jobRequest.setArgumentTypes(methodInvocation.getMethod().getParameterTypes());
                jobRequest.setArguments(methodInvocation.getArguments());
                
                jobRequest.setRequestState(RequestLifecycleAdvisor.getInstance().getRequestState());
                jobRequest.setBeanName(getBeanName());
                jobRequest.setJobCategory(category);
                jobRequest.setPolicyLockId(policyHeader.getPolicyLockId());
                jobRequest.setUserId(UserSessionManager.getInstance().getUserSession().getUserId());
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(),"invoke","LRT - Interceptor: Policy " + policyHeader.getPolicyNo() +
                        ", intercepted method " + jobRequest.getMethod() + ". Category is " + category +
                        ". Scheduling job " + jobRequest.getId() +".");
                }

                getJobQueueManager().scheduleJob(jobRequest);
                // After scheduling the job, the interceptor sets the transition parameter to a predefined
                // fixed value, which will force the long running transaction action class to keep monitoring
                // the status of the job. Once the job is complete, the long runnig transaction action class
                // will set the next state. MONITOR_LONG_RUNNING_TRANSACTION must be a valid entry in the
                // workflow configuration, and has to define what is the next state once the monitoring is done.
                objResult = MONITOR_LONG_RUNNING_TRANSACTION;

                wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), (String) objResult);
                wa.setWorkflowAttribute(policyHeader.getPolicyNo(), JobRequest.JOB_ID, jobRequest.getId());
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(),"invoke","LRT - Interceptor: Policy " + policyHeader.getPolicyNo() +
                        ", set Workflow Transition Parameter to " + objResult + ".");
                }
            }
            catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Error scheduling job for method "+
                    methodInvocation.getMethod().getName(), e);
                l.throwing(getClass().getName(), "invoke", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "invoke");
        }

        return objResult;
    }

    public String getBeanName() {
        if (m_beanName == null) {
            throw new ConfigurationException("The required property 'beanName' is missing.");
        }
        return m_beanName;
    }

    public void setBeanName(String beanName) {
        m_beanName = beanName;
    }

    public void verifyConfig() {
        if (getJobQueueManager() == null)
            throw new ConfigurationException("The required property 'jobQueueManager' is missing.");
    }

    public JobQueueManagerImpl getJobQueueManager() {
        return m_jobQueueManager;
    }

    public void setJobQueueManager(JobQueueManagerImpl jobQueueManager) {
        m_jobQueueManager = jobQueueManager;
    }

    private JobQueueManagerImpl m_jobQueueManager;
    private String m_beanName;
}
