package dti.pm.workflowmgr.jobqueuemgr.impl;

import dti.oasis.util.LogUtils;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.request.RequestSession;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.MessageManagerAdmin;
import dti.pm.workflowmgr.jobqueuemgr.JobRequest;
import dti.pm.workflowmgr.jobqueuemgr.JobQueueManager;

import javax.management.timer.Timer;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.reflect.Method;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 7, 2008
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/13/2008       fcb         Refactoring based on Bill's code review.
 * 04/27/2009       fcb         FINEST logging messages added.
 * 09/04/2009       fcb         handleNotification: set messages from message manager
 *                              in the job.
 * 05/20/2010       fcb         FINEST level chaged to FINE, plus other cosmetic changes.
 * 08/03/2010       fcb         Call to finishedProcessing added.
 * 05/04/2012       fcb         133291: added logic to stop timer threads that finished their job.
 * ---------------------------------------------------
 */
public class JobProcessor implements javax.management.NotificationListener {
    /**
      * Indicator used to avoid rescheduling the the method call in the interceptor.
      */
    public static final String REQUEST_ID_PROCESS_JOB = "processJob";

    /**
     * Public constructor.
     * @param jobQueueManager
     */
    public JobProcessor (JobQueueManager jobQueueManager) {
        m_jobQueueManager = jobQueueManager;
    }

    /**
     * Method to handle a job notification.
     * @param notification
     * @param handback
     */
    public void handleNotification(javax.management.Notification notification, Object handback) {
        Logger l = LogUtils.enterLog(getClass(), "handleNotification", new Object[]{notification});

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(),"handleNotification","LRT - JobProcessor: received notification to process a new job. ");
        }

        JobRequest job = (JobRequest)notification.getUserData();
        job.setJobProcessing();
        RequestStorageManager rsm = RequestStorageManager.getInstance();
        Class classObject = null;
        RequestState requestState = null;

        RequestLifecycleAdvisor rla = RequestLifecycleAdvisor.getInstance();
        try {
            // Ensure the bean is defined so the Job can create an instance of it to use
            Object bean = ApplicationContext.getInstance().getBean(job.getBeanName());

            // Use reflection to invoke the method.
            classObject = bean.getClass();

            RequestSession rs = job.getRequestSession();
            rla.initializeFromRequestState(rs);

            // This flag is used in order to avoid re-scheduling the method call in the interceptor.
            rsm.set(REQUEST_ID_PROCESS_JOB, job.getId());

            Method method = classObject.getMethod(job.getMethod(),job.getArgumentTypes());
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(),"handleNotification","LRT - JobProcessor: invoking method " +
                    job.getMethod() + " for Job " + job.getId() + ".");
            }

            Object returnObject = method.invoke(bean,job.getArguments());
            job.setReturnObject(returnObject);
            job.setMessages(((MessageManagerAdmin)MessageManager.getInstance()).getAllMessagesList());
            requestState = RequestState.COMPLETED;
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error invoking method " + job.getMethod() +
                " in " + classObject.getClass(), e);
            job.setMessages(((MessageManagerAdmin)MessageManager.getInstance()).getAllMessagesList());
            job.setException(ae);
            requestState = RequestState.FAILED;
        }
        finally {
            rsm.remove(REQUEST_ID_PROCESS_JOB);
            m_jobQueueManager.finishedProcessing(job, requestState);
            m_jobQueueManager.notifyQueueMonitor();
            l.logp(Level.INFO, getClass().getName(),"processJob","LRT - JobProcessor: Policy=" + job.getPolicyNo() +
                ": finished method " + job.getMethod() + " for Job" + job.getId() + ". The return object is " +
                job.getReturnObject() + ". The job state is " + job.getState() + ". The elapsed time(sec) is " +
                job.getElapsedExecutionTime()/1000F);
            rla.terminate();
        }

        // Stop the timer
        m_invokerTimer.stop();
        m_invokerTimer.removeAllNotifications();
        l.exiting(getClass().getName(), "handleNotification");
    }

    public Timer getInvokerTimer() {
        return m_invokerTimer;
    }

    public void setInvokerTimer(Timer invokerTimer) {
        m_invokerTimer = invokerTimer;
    }

    private JobQueueManager m_jobQueueManager;
    private Timer m_invokerTimer;
}
