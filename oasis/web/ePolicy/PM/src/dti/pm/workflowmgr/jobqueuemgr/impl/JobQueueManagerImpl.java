package dti.pm.workflowmgr.jobqueuemgr.impl;

import dti.oasis.util.LogUtils;
import dti.oasis.app.ApplicationLifecycleListener;
import dti.oasis.app.ConfigurationException;
import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.request.RequestSession;
import dti.oasis.recordset.Record;
import dti.pm.workflowmgr.jobqueuemgr.JobRequest;
import dti.pm.workflowmgr.jobqueuemgr.JobQueueManager;
import dti.pm.workflowmgr.jobqueuemgr.JobCategoryEvaluator;
import dti.pm.transactionmgr.transaction.dao.TransactionDAO;

import java.util.LinkedList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.management.timer.Timer;

/**
 * JobQueueManagerImpl is a synchronized implementation of the JobQueueManager interface. It can be accessed by
 * multiple threads that run long running transactions.
 * The strategy for synchronization is as follows:
 * Any attempt to add or remove a job from any of the internal queues is done by acquiring a lock on the
 * JobQueueManagerImpl object.
 * Any attempt to move an object from one queue to another is done by wrapping the statements that dequeue/enqueue the
 * job object with a synchronized block that aquired a lock on JobQueueManager object.
 * Any external object that requires information from the JobQueueManager (ex getJob) will do it through methods that
 * wrap the code to search the queues with a synchronized block that aquired a lock on JobQueueManager object.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2008
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/03/2008       fcb         finishJob renamed to cleanupJob.
 * 05/12/2008       fcb         refactoring of the code based on Bill's code review.
 * 04/27/2009       fcb         FINEST logging messages added.
 *                              New class MonitorProcessor added to monitor whether the JobProcessor
 *                              picks up the job notification.
 * 05/20/2010       fcb         FINEST level changed to FINE, plus other cosmetic changes.
 * 06/28/2010       fcb         109187: cleanProcessingJobs added.
 *                              TransactionDAO added to this class.
 * 08/03/2010       fcb         Introduced an additional result queue, changed the
 *                              mechanism for synchronization, etc.
 * 05/04/2012       fcb         133291: added logic to stop timer threads that finished their job.
 * 09/20/2012       fcb         Issue 136956: added logic for various method to log to the java log the user id
 *                              that was used to create a Job Request.
 * 02/28/2014       fcb         152186: Refactoring of the job monitor logic.
 * ---------------------------------------------------
 */
public class JobQueueManagerImpl implements JobQueueManager, ApplicationLifecycleListener {

    /**
     * Public constructor.
     */
    public JobQueueManagerImpl() {
    }

    /**
     * Initialize the the listener.
     */
    public void initialize() {}

    /**
     * Cleanup resources.
     */
    public void terminate() {
        Logger l = LogUtils.enterLog(getClass(), "terminate");
        notifyQueueMonitor();
        destroyQueueManager();
        l.exiting(getClass().getName(), "terminate");
    }

    /**
     * Method to schedule a job to be executed.
     * @param jobRequest
     */
    public void scheduleJob (JobRequest jobRequest) {
        Logger l = LogUtils.enterLog(getClass(), "scheduleJob", new Object[]{jobRequest});
        if (!isMonitorTimerInitializedAndActive()) {
            initializeMonitorTimer();
        }
        if (jobRequest.getJobCategory().isMedium()) {
            queueMediumWaitingJob(jobRequest);
        }
        else if (jobRequest.getJobCategory().isLong()) {
            queueLongWaitingJob(jobRequest);
        }
        else {
            IllegalArgumentException e = new IllegalArgumentException("Invalid job category: " + jobRequest.getJobCategory());
            l.throwing(getClass().getName(), "getJob", e);
            throw e;
        }

        notifyQueueMonitor();

        l.exiting(getClass().getName(), "scheduleJob");
    }

    /**
     * Method to clean up a processed job.
     * @param jobRequest
     */
    public void cleanupJob (JobRequest jobRequest) {
        Logger l = LogUtils.enterLog(getClass(), "cleanupJob", new Object[]{jobRequest});

        synchronized(this) {
            m_resultQueue.remove(jobRequest);
        }

        l.logp(Level.INFO, getClass().getName(), "cleanupJob", getMsgUsr(jobRequest) + "LRT - JobQueueManager: " +
                " Policy " + jobRequest.getPolicyNo() + " with Job " + (jobRequest).getId() + " removed from Result Queue.");

        notifyQueueMonitor();

        l.exiting(getClass().getName(), "cleanupJob");
    }

    /**
     * Method to terminate the Queue Manager.
     */
    public void destroyQueueManager() {
        Logger l = LogUtils.enterLog(getClass(), "destroyQueueManager");
        if (!isMonitorTimerInitializedAndActive()) {
            return;
        }

        try {
            m_monitorTimer.stop();
            m_monitorTimer.removeAllNotifications();
        } catch (Exception e) {
             if (l.isLoggable(Level.FINE)) {
                 l.logp(Level.FINE, getClass().getName(), "destroyQueueManager", "LRT - JobQueueManager: error destroying the queue manager.");
             }
        }
        l.exiting(getClass().getName(), "destroyQueueManager");
    }

    /**
     * Method to get a JobRequest based on a given id.
     * Throws IllegalArgumentException if the JobRequest is not found.
     * @param id
     * @return
     */
    public JobRequest getJob (String id) {
        Logger l = LogUtils.enterLog(getClass(), "getJob", new Object[]{id});
        JobRequest currentjob = null;
        if (id!=null) {
            synchronized(this) {
                currentjob = getJob(id, m_mediumProcessingQueue);
                if (currentjob==null) {
                    currentjob = getJob(id, m_longProcessingQueue);
                }
                if (currentjob==null) {
                    currentjob = getJob(id, m_mediumWaitingQueue);
                }
                if (currentjob==null) {
                    currentjob = getJob(id, m_longWaitingQueue);
                }
                if (currentjob==null) {
                    currentjob = getJob(id, m_resultQueue);
                }
            }
        }

        notifyQueueMonitor();

        if (currentjob==null) {
            IllegalArgumentException e = new IllegalArgumentException("Error getting the JobRequest for id=" + id);
            l.throwing(getClass().getName(), "getJob", e);
            throw e;
        }

        l.exiting(getClass().getName(), "getJob");
        return currentjob;
    }

    /**
     * Method to check if a job is still connected to a valid Web session.
     * @param identifier of the job (for ex. policy number)
     * @param policyLockId of the job
     * @return true/false
     */
    public boolean isJobSessionActive(String identifier, String policyLockId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
           l.entering(getClass().getName(), "isJobSessionActive", new Object[]{identifier, policyLockId});
        }

        Record record = new Record();
        record.setFieldValue("policyNo", identifier);
        record.setFieldValue("policyLockId", policyLockId);

        boolean isActive = getTransactionDAO().isJobSessionActive(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isJobSessionActive", Boolean.toString(isActive));
        }

        return isActive;
    }

    /**
     * Method to finish a processing job.
     * @param jobRequest the job request object
     * @param requestState the state the job is in
     */
    public void finishedProcessing(JobRequest jobRequest, RequestState requestState) {
        Logger l = LogUtils.enterLog(getClass(), "finishedProcessing", new Object[]{jobRequest, requestState});

        jobRequest.setFinishExecutionTime(System.currentTimeMillis());

        synchronized(this) {
            if (requestState.equals(RequestState.COMPLETED)) {
                jobRequest.setJobComplete();
            }
            else if (requestState.equals(RequestState.FAILED)) {
                jobRequest.setJobFailed();
            }
            else {
                IllegalArgumentException e = new IllegalArgumentException("Invalid RequestState:" + requestState);
                l.throwing(getClass().getName(), "finishedProcessing", e);
                throw e;
            }

            if (jobRequest.getJobCategory().isMedium()) {
                dequeueMediumProcessingJob(jobRequest);
            }
            else if (jobRequest.getJobCategory().isLong()) {
                dequeueLongProcessingJob(jobRequest);
            }
            else {
                IllegalArgumentException e = new IllegalArgumentException("Invalid job category: " + jobRequest.getJobCategory());
                l.throwing(getClass().getName(), "finishedProcessing", e);
                throw e;
            }

            m_resultQueue.add(jobRequest);

            l.logp(Level.INFO, getClass().getName(), "finishedProcessing", getMsgUsr(jobRequest) + "LRT - JobQueueManager: " +
                    " Policy " + jobRequest.getPolicyNo() + " with Job " + (jobRequest).getId() + " added to Result Queue.");
        }

        l.exiting(getClass().getName(), "finishedProcessing");
    }

    /**
     * Method to notify the queue monitor to check for jobs that could be processed.
     */
    public void notifyQueueMonitor() {
        Logger l = LogUtils.enterLog(getClass(), "notifyQueueMonitor");
        synchronized(m_jobMonitor) {
            m_jobMonitor.notify();
        }
        l.exiting(getClass().getName(), "notifyQueueMonitor");
    }

    protected void notifyQueueManager() {
       Logger l = LogUtils.enterLog(getClass(), "notifyQueueManager");

        try {
            // If the process is not successful for any reason, we do not want to stop
            // the notification. The cleaning is done here for convenience as we want to
            // try to clean the orphans as often as possible.
            cleanOrphanJobs();
        } catch ( Exception e) {}

        if (isOKToProcessMedium()) {
            processMediumEvent();
        }
        if (isOKToProcessLong()) {
            processLongEvent();
        }

        l.exiting(getClass().getName(), "notifyQueueManager");
    }

    protected void cleanOrphanJobs() {
        Logger l = LogUtils.enterLog(getClass(), "cleanOrphanJobs");

        if (m_resultQueue.size() > 0) {
            cleanJobsFromResultQueue(m_resultQueue);
        }

        l.exiting(getClass().getName(), "cleanOrphanJobs");
    }

    protected void cleanJobsFromResultQueue(LinkedList queue) {
        Logger l = LogUtils.enterLog(getClass(), "cleanJobsFromResultQueue");

        synchronized(this) {
            try {
                Iterator it = queue.iterator();
                RequestLifecycleAdvisor rla = RequestLifecycleAdvisor.getInstance();
                while(it.hasNext()) {
                    JobRequest jobRequest = (JobRequest)it.next();

                    RequestSession rs = jobRequest.getRequestSession();
                    rla.initializeFromRequestState(rs);
                    try {


                        String policyLockId = jobRequest.getPolicyLockId();
                        long startExecutionTime = jobRequest.getStartExecutionTime();
                        long currentTime = System.currentTimeMillis();
                        boolean canRemoveJob = currentTime - startExecutionTime > getMaxTimeInResultQueue() * 3600 * 1000;
                        boolean isActive = true;

                        if (canRemoveJob) {
                            try {
                                // We simply attempt to get the OasisUser as this relies on the session. When the
                                // session is not active anymore, this will raise an error, and it constitutes the
                                // signal that the session associated with this job is no longer in use and can be removed.
                                jobRequest.getRequestSession().getUserSession().getOasisUser();
                            } catch (Exception ex) {
                                // Set to false when the session is invalid.
                                isActive = false;
                            }
                        }

                        if (canRemoveJob && !isActive) {
                            it.remove();
                            l.logp(Level.INFO, getClass().getName(), "cleanJobsFromResultQueue",
                                    getMsgUsr(jobRequest) + "LRT - JobQueueManager: " +
                                            " Policy " + jobRequest.getPolicyNo() + " and sessionId=" + policyLockId +
                                            " - Orphan Job " + (jobRequest).getId() + " removed from Result Queue.");
                        }
                    } finally {
                        rla.terminate();
                    }
                }
            } catch (Exception e) {
                l.logp(Level.INFO, getClass().getName(), "cleanJobsFromResultQueue", "Error cleaning the Result Queue: " +
                    e.getMessage());
            }
        }

        l.exiting(getClass().getName(), "cleanJobsFromResultQueue");
    }

    protected void processMediumEvent() {
        Logger l = LogUtils.enterLog(getClass(), "processMediumEvent");

        JobRequest jobRequest;

        synchronized(this) {
            jobRequest = (JobRequest)dequeueMediumWaitingJob();
            queueMediumProcessingJob(jobRequest);
        }

        processJob(jobRequest);

        l.exiting(getClass().getName(), "processMediumEvent");
    }

    protected void processLongEvent() {
        Logger l = LogUtils.enterLog(getClass(), "processLongEvent");

        JobRequest jobRequest;

        synchronized(this) {
            jobRequest = (JobRequest)dequeueLongWaitingJob();
            queueLongProcessingJob(jobRequest);
        }
        processJob(jobRequest);

        l.exiting(getClass().getName(), "processLongEvent");
    }

    protected void processJob(JobRequest jobRequest) {
        Logger l = LogUtils.enterLog(getClass(), "processJob");

        notifyProcessor(jobRequest);

        if (isMonitorProcessorConfigured()) {
            new MonitorProcessor(jobRequest).start();
        }

        l.exiting(getClass().getName(), "processJob");
    }

    protected void notifyProcessor(JobRequest jobRequest) {
        Logger l = LogUtils.enterLog(getClass(), "notifyProcessor");

        JobProcessor invokerListener = new JobProcessor(this);
        Timer invokerTimer;
        invokerTimer = new Timer();
        invokerTimer.addNotificationListener(invokerListener, null, null);
        invokerTimer.setSendPastNotifications(true);
        invokerTimer.start();
        invokerTimer.addNotification(INVOKER_EVENT_TYPE, INVOKER_EVENT_MESSAGE, jobRequest, new Date());
        invokerListener.setInvokerTimer(invokerTimer);
        jobRequest.setStartExecutionTime(System.currentTimeMillis());

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "notifyProcessor", getMsgUsr(jobRequest) +
                "LRT - JobQueueManager: create a JobProcessor to process job " + jobRequest.getId() + ".");
        }

        l.exiting(getClass().getName(), "notifyProcessor");
    }

    protected boolean isOKToProcessMedium() {
        Logger l = LogUtils.enterLog(getClass(), "isOKToProcessMedium");
        boolean proceed = (!isMediumWaitingQueueEmpty() && getMediumTransProcessing() < getMaxMediumTransactionProcessors());
        if (!isMediumWaitingQueueEmpty()) {
            String message = proceed?"OK to process job":"not OK to process job";
            l.logp(Level.INFO, getClass().getName(),"isOKToProcessMedium", "LRT -  JobQueueManager: " + message +
                ", Medium Job Waiting=" + !isMediumWaitingQueueEmpty() + ", # of Medium Job Processing=" +
                getMediumTransProcessing() + ", # of Max Medium Processing=" + getMaxMediumTransactionProcessors()+".");
        }
        l.exiting(getClass().getName(), "isOKToProcessMedium", String.valueOf(proceed));
        return proceed;
    }

    protected boolean isOKToProcessLong() {
        Logger l = LogUtils.enterLog(getClass(), "isOKToProcessLong");
        boolean proceed = (!isLongWaitingQueueEmpty() && getLongTransProcessing() < getMaxLongTransactionProcessors());
        if (!isLongWaitingQueueEmpty()) {
            String message = proceed?"OK to process job":"not OK to process job";
            l.logp(Level.INFO, getClass().getName(),"isOKToProcessLong","LRT - JobQueueManager: " + message +
                ", Long Job Waiting=" + !isLongWaitingQueueEmpty() + ", # of Long Job Processing=" +
                getLongTransProcessing() + ", # of Max Long Processing=" + getMaxLongTransactionProcessors()+".");
        }
        l.exiting(getClass().getName(), "isOKToProcessLong", String.valueOf(proceed));
        return proceed;
    }

    protected boolean isMonitorProcessorConfigured() {
        Logger l = LogUtils.enterLog(getClass(), "isMonitorProcessorConfigured");
        int resetAttempts = getMaxProcessorResetAttempts();

        boolean isConfigured = false;
        if (resetAttempts > 0) {
            isConfigured = true;
        }

        l.exiting(getClass().getName(), "isMonitorProcessorConfigured", String.valueOf(isConfigured));

        return isConfigured;
    }

    protected int getLongTransProcessing() {
        return m_longProcessingQueue.size();
    }

    protected int getMediumTransProcessing() {
        return m_mediumProcessingQueue.size();
    }

    protected boolean isMonitorTimerInitializedAndActive() {
        Logger l = LogUtils.enterLog(getClass(), "isMonitorTimerInitializedAndActive");
        boolean isActive;
        if (m_monitorTimer == null) {
            isActive = false;
        }
        else {
            if (!m_monitorTimer.isActive()) {
                isActive = false;
            }
            else {
                isActive = true;
            }
        }
        l.exiting(getClass().getName(), "isMonitorTimerInitializedAndActive", isActive);

        return isActive;
    }

    protected void queueMediumWaitingJob(JobRequest jobRequest) {
        Logger l = LogUtils.enterLog(getClass(), "queueMediumWaitingJob", new Object[]{jobRequest});

        synchronized(this) {
            m_mediumWaitingQueue.add(jobRequest);
            jobRequest.setJobInitialized();
        }

        l.logp(Level.INFO, getClass().getName(), "queueMediumWaitingJob", getMsgUsr(jobRequest) + "LRT - JobQueueManager: " +
                " Policy " + jobRequest.getPolicyNo() + " with Job " + (jobRequest).getId() +
                " added to Medium Waiting Queue.");
        l.exiting(getClass().getName(), "queueMediumWaitingJob");
    }

    protected Object dequeueMediumWaitingJob() {
        Logger l = LogUtils.enterLog(getClass(), "dequeueMediumWaitingJob");
        Object o;

        synchronized(this) {
            o = m_mediumWaitingQueue.removeFirst();
        }

        l.logp(Level.INFO, getClass().getName(), "dequeueMediumWaitingJob", getMsgUsr((JobRequest)o) + "LRT - JobQueueManager: " +
               "Policy " + ((JobRequest)o).getPolicyNo() + " with Job " + ((JobRequest)o).getId() +
               " removed from Medium Waiting Queue.");
        l.exiting(getClass().getName(), "dequeueMediumWaitingJob");

        return o;
    }

    protected void queueLongWaitingJob(JobRequest jobRequest) {
        Logger l = LogUtils.enterLog(getClass(), "queueLongWaitingJob", new Object[]{jobRequest});

        synchronized(this) {
            m_longWaitingQueue.add(jobRequest);
            jobRequest.setJobInitialized();
        }

        l.logp(Level.INFO, getClass().getName(),"queueLongWaitingJob","LRT - JobQueueManager: " +
                " Policy " + jobRequest.getPolicyNo() + " with Job " + (jobRequest).getId() + " added to Long Waiting Queue.");
        l.exiting(getClass().getName(), "queueLongWaitingJob");
    }

    protected Object dequeueLongWaitingJob() {
        Logger l = LogUtils.enterLog(getClass(), "dequeueLongWaitingJob");
        Object o;

        synchronized(this) {
            o = m_longWaitingQueue.removeFirst();
        }

        l.logp(Level.INFO, getClass().getName(), "dequeueLongWaitingJob", getMsgUsr((JobRequest)o) + "LRT - JobQueueManager: " +
               "Policy " + ((JobRequest)o).getPolicyNo() + " with Job " + ((JobRequest)o).getId() +
               " removed from Long Waiting Queue.");
        l.exiting(getClass().getName(), "dequeueLongWaitingJob");

        return o;
    }

    protected void queueMediumProcessingJob(JobRequest jobRequest) {
        Logger l = LogUtils.enterLog(getClass(), "queueMediumProcessingJob", new Object[]{jobRequest});

        synchronized(this) {
            m_mediumProcessingQueue.add(jobRequest);
        }

        l.logp(Level.INFO, getClass().getName(), "queueMediumProcessingJob", getMsgUsr(jobRequest) + "LRT - JobQueueManager: " +
                " Policy " + jobRequest.getPolicyNo() + " with Job " + (jobRequest).getId() +
                " added to Medium Processing Queue.");
        l.exiting(getClass().getName(), "queueMediumProcessingJob");
    }

    protected void queueLongProcessingJob(JobRequest jobRequest) {
        Logger l = LogUtils.enterLog(getClass(), "queueLongProcessingJob", new Object[]{jobRequest});

        synchronized(this) {
            m_longProcessingQueue.add(jobRequest);
        }

        l.logp(Level.INFO, getClass().getName(), "queueMediumProcessingJob", getMsgUsr(jobRequest) + "LRT - JobQueueManager: " +
                " Policy " + jobRequest.getPolicyNo() + " with Job " + (jobRequest).getId() +
                " added to Long Processing Queue.");
        l.exiting(getClass().getName(), "queueLongProcessingJob");
    }

    protected void dequeueMediumProcessingJob(JobRequest jobRequest) {
        Logger l = LogUtils.enterLog(getClass(), "dequeueMediumProcessingJob");

        synchronized(this) {
            m_mediumProcessingQueue.remove(jobRequest);
        }

        l.logp(Level.INFO, getClass().getName(), "dequeueMediumProcessingJob", getMsgUsr(jobRequest) + "LRT - JobQueueManager: " +
               "Policy " + jobRequest.getPolicyNo() + " with Job " + jobRequest.getId() +
               " removed from Medium Processing Queue.");
        l.exiting(getClass().getName(), "dequeueMediumProcessingJob");
    }

    protected void dequeueLongProcessingJob(JobRequest jobRequest) {
        Logger l = LogUtils.enterLog(getClass(), "dequeueLongProcessingJob");

        synchronized(this) {
            m_longProcessingQueue.remove(jobRequest);
        }

        l.logp(Level.INFO, getClass().getName(), "dequeueLongProcessingJob", getMsgUsr(jobRequest) + "LRT - JobQueueManager: " +
               "Policy " + jobRequest.getPolicyNo() + " with Job " + jobRequest.getId() +
               " removed from Long Processing Queue.");
        l.exiting(getClass().getName(), "dequeueLongProcessingJob");
    }

    protected boolean isMediumWaitingQueueEmpty() {
        return m_mediumWaitingQueue.size()==0;
    }

    protected boolean isLongWaitingQueueEmpty() {
        return m_longWaitingQueue.size()==0;
    }

    private JobRequest getJob (String id, LinkedList queue) {
        Logger l = LogUtils.enterLog(getClass(), "getJob", new Object[]{id});
        JobRequest jobRequest;
        JobRequest currentJob = null;

        synchronized(this) {
            Iterator it = queue.iterator();
            while (it.hasNext()) {
                jobRequest = (JobRequest)it.next();
                if (id.equals(jobRequest.getId())) {
                    currentJob = jobRequest;
                }
            }
        }

        l.exiting(getClass().getName(), "getJob");

        return currentJob;
    }

    private String getMsgUsr (JobRequest jobRequest) {
        return "<" + jobRequest.getUserId() + "> ";
    }

    private synchronized void initializeMonitorTimer() {
        Logger l = LogUtils.enterLog(getClass(), "initializeMonitorTimer");

        m_jobMonitor = new JobQueueMonitor();
        m_monitorTimer = new Timer();
        m_monitorTimer.addNotificationListener(m_jobMonitor, null, null);
        m_monitorTimer.setSendPastNotifications(true);
        m_monitorTimer.start();
        m_monitorTimer.addNotification( MONITOR_EVENT_TYPE, MONITOR_EVENT_MESSAGE, this, new Date());

        l.exiting(getClass().getName(), "initializeMonitorTimer");
    }

    public void setMaxMediumTransactionProcessors(int maxMediumTransactionProcessors) {
        this.m_maxMediumTransactionProcessors = maxMediumTransactionProcessors;
    }

    public int getMaxMediumTransactionProcessors() {
        return m_maxMediumTransactionProcessors;
    }

    public void setMaxLongTransactionProcessors(int maxLongTransactionProcessors) {
        this.m_maxLongTransactionProcessors = maxLongTransactionProcessors;
    }

    public int getMaxLongTransactionProcessors() {
        return m_maxLongTransactionProcessors;
    }

    public JobCategoryEvaluator getJobCategoryEvaluator() {
        return m_jobCategoryEvaluator;
    }

    public void setJobCategoryEvaluator(JobCategoryEvaluator jobCategoryEvaluator) {
        m_jobCategoryEvaluator = jobCategoryEvaluator;
    }

    public void verifyConfig() {
        if (getJobCategoryEvaluator() == null)
            throw new ConfigurationException("The required property 'jobCategoryEvaluator' is missing.");
        if (getTransactionDAO() == null)
            throw new ConfigurationException("The required property 'transactionDAO' is missing.");
    }

    public int getMaxProcessorResetAttempts() {
        return m_maxProcessorResetAttempts;
    }

    public void setMaxProcessorResetAttempts(int maxProcessorResetAttempts) {
        m_maxProcessorResetAttempts = maxProcessorResetAttempts;
    }

    public int getProcessorMonitorRate() {
        return m_processorMonitorRate;
    }

    public void setProcessorMonitorRate(int processorMonitorRate) {
        m_processorMonitorRate = processorMonitorRate;
    }

    public int getMaxTimeInResultQueue() {
        return m_maxTimeInResultQueue;
    }

    public void setMaxTimeInResultQueue(int maxTimeInResultQueue) {
        m_maxTimeInResultQueue = maxTimeInResultQueue;
    }

    public TransactionDAO getTransactionDAO() {
        return m_transactionDAO;
    }

    public void setTransactionDAO(TransactionDAO transactionDAO) {
        m_transactionDAO = transactionDAO;
    }

    private final LinkedList<JobRequest> m_mediumWaitingQueue = new LinkedList<JobRequest>();
    private final LinkedList<JobRequest> m_longWaitingQueue = new LinkedList<JobRequest>();
    private final LinkedList<JobRequest> m_mediumProcessingQueue = new LinkedList<JobRequest>();
    private final LinkedList<JobRequest> m_longProcessingQueue = new LinkedList<JobRequest>();
    private final LinkedList<JobRequest> m_resultQueue = new LinkedList<JobRequest>();

    private Timer m_monitorTimer;
    private JobCategoryEvaluator m_jobCategoryEvaluator;
    private JobQueueMonitor m_jobMonitor;
    private TransactionDAO m_transactionDAO;

    private int m_maxMediumTransactionProcessors;
    private int m_maxLongTransactionProcessors;

    private int m_maxProcessorResetAttempts;
    private int m_processorMonitorRate;
    private int m_maxTimeInResultQueue;

    private static final String INVOKER_EVENT_TYPE = "InvokerTimer";
    private static final String INVOKER_EVENT_MESSAGE = "InvokerTimer";
    private static final String MONITOR_EVENT_TYPE = "MonitorTimer";
    private static final String MONITOR_EVENT_MESSAGE = "MonitorTimer";

    private class JobQueueMonitor implements javax.management.NotificationListener {
        /**
         * Method to implement notifications.
         * @param notification
         * @param handback
         */
        public void handleNotification(javax.management.Notification notification, Object handback) {
            Logger l = LogUtils.enterLog(getClass(), "handleNotification", new Object[]{notification});

            while ( isMonitorTimerInitializedAndActive() ) {
                try {
                    synchronized(m_jobMonitor) {
                        m_jobMonitor.wait();
                    }
                }
                catch( InterruptedException ie) {}

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "handleNotification", "LRT - JobQueueMonitor: " +
                        "Monitor notifies the Queue Manager to check for available jobs.");
                }
                notifyQueueManager();
            }

            l.exiting(getClass().getName(), "handleNotification");
        }
    }

    private class MonitorProcessor extends Thread  {
        public MonitorProcessor(JobRequest jobRequest) {
            this.jobRequest = jobRequest;
        }

        public void run() {
            Logger l = LogUtils.enterLog(getClass(), "run");
            int numberOfAttempts = 0;

            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(),"run", getMsgUsr(jobRequest) +
                    "LRT - MonitorProcessor: starting monitoring the JobProcessor for job " +
                    jobRequest.getId() + ".");
            }

            while( numberOfAttempts < getMaxProcessorResetAttempts() ) {
                numberOfAttempts++;
                if (jobRequest.isJobHandled()) {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(),"run", getMsgUsr(jobRequest) +
                            "LRT - MonitorProcessor: JobProcessor has received notification to process job " +
                            jobRequest.getId() + ".");
                    }
                    break;
                }
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(),"run", getMsgUsr(jobRequest) +
                        "LRT - MonitorProcessor: JobProcessor has not received notification to process job " +
                        jobRequest.getId() + ".");
                }

                try {
                    Thread.sleep(1000 * getProcessorMonitorRate());
                } catch (InterruptedException e) { };
            }

            if (!jobRequest.isJobHandled()) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "run", getMsgUsr(jobRequest) +
                        "LRT - MonitorProcessor: Exceeded max notification wait time, re-notifying the JobProcessor for job " +
                        jobRequest.getId() + ".");
                }
                notifyProcessor(jobRequest);
            }

            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "run", getMsgUsr(jobRequest) +
                    "LRT - MonitorProcessor: terminated monitoring the JobProcessor for job " +
                    jobRequest.getId() + ". JobProcessor is processing this job now.");
            }
        }

        JobRequest jobRequest;
    }

}
