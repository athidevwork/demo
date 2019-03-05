package dti.oasis.accesstrailmgr.impl;

import dti.oasis.accesstrailmgr.OwsLogProcessor;
import dti.oasis.accesstrailmgr.OwsLogQueueManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.ApplicationLifecycleListener;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.timer.Timer;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OwsLogQueueManagerImpl is a synchronized implementation of the OwsLogQueueManager interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 22, 2014
 *
 * @author Parker Xu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 *
 * ---------------------------------------------------
 */
public class OwsLogQueueManagerImpl implements OwsLogQueueManager, ApplicationLifecycleListener {

    /**
     * Public constructor.
     */
    public OwsLogQueueManagerImpl() {
    }

    /**
     * Initialize the the listener.
     */
    public void initialize() {
    }

    /**
     * Method to schedule a owsLogRequest to be executed.
     *
     * @param owsLogRequest
     */
    public void schedule(OwsLogRequest owsLogRequest) {
        Logger l = LogUtils.enterLog(getClass(), "schedule", new Object[]{owsLogRequest});

        queueProcessingOwsLogJob(owsLogRequest);

        synchronized (m_lock) {
            if (!isTimerInitializedAndActive()) {
                m_owsLogTimer = new Timer();
                m_owsLogTimer.addNotificationListener(getOwsLogProcessor(), null, null);
                m_owsLogTimer.setSendPastNotifications(true);
                m_owsLogTimer.start();
            }
            m_owsLogTimer.addNotification(INVOKER_EVENT_TYPE, INVOKER_EVENT_MESSAGE, owsLogRequest, new Date());
        }

        l.exiting(getClass().getName(), "schedule");
    }

    /**
     * check the request job status.If not in processing queue. return true.
     * @param owsLogRequest the job request object
     * @return boolean
     */
    public boolean checkJobStatusForRequest(OwsLogRequest owsLogRequest) {
        Logger l = LogUtils.enterLog(getClass(), "checkJobStatusForRequest", owsLogRequest);

        boolean isRequestJobInProcessingQueue = false;
        if(OwsLogRequest.INDICATOR_UPDATE.equals(owsLogRequest.getIndicator())) {
            for (OwsLogRequest processingRequest : m_processingQueue) {
                if (processingRequest.getOwsAccessTrailId().equals(owsLogRequest.getOwsAccessTrailId())
                    && OwsLogRequest.INDICATOR_INSERT.equals(processingRequest.getIndicator())) {
                    isRequestJobInProcessingQueue = true;
                    break;
                }
            }
        }
        l.exiting(getClass().getName(), "checkJobStatusForRequest", isRequestJobInProcessingQueue);
        return isRequestJobInProcessingQueue;
    }

    /**
     * Method to finish a processing owsLogRequest.
     *
     * @param owsLogRequest the owsLogRequest request object
     */
    public void finishedProcessing(OwsLogRequest owsLogRequest) {
        Logger l = LogUtils.enterLog(getClass(), "finishedProcessing", new Object[]{owsLogRequest});

        synchronized (this) {
            deQueueProcessingOwsLogJob(owsLogRequest);
        }

        l.exiting(getClass().getName(), "finishedProcessing");
    }

    /**
     * check the timer is initialized and active
     *
     * @return
     */
    protected boolean isTimerInitializedAndActive() {
        Logger l = LogUtils.enterLog(getClass(), "isTimerInitializedAndActive");
        boolean isActive;
        if (m_owsLogTimer == null) {
            isActive = false;
        } else {
            if (!m_owsLogTimer.isActive()) {
                isActive = false;
            } else {
                isActive = true;
            }
        }
        l.exiting(getClass().getName(), "isTimerInitializedAndActive", isActive);

        return isActive;
    }

    /**
     * add owsLogRequest to processing queue.
     *
     * @return
     */
    protected void queueProcessingOwsLogJob(OwsLogRequest owsLogRequest) {
        Logger l = LogUtils.enterLog(getClass(), "queueProcessingOwsLogJob", new Object[]{owsLogRequest});

        synchronized (this) {
            m_processingQueue.add(owsLogRequest);
        }

        l.exiting(getClass().getName(), "queueProcessingOwsLogJob");
    }

    /**
     * remove owsLogRequest from processing queue.
     *
     * @return
     */
    protected void deQueueProcessingOwsLogJob(OwsLogRequest owsLogRequest) {
        Logger l = LogUtils.enterLog(getClass(), "deQueueProcessingOwsLogJob");

        synchronized (this) {
            m_processingQueue.remove(owsLogRequest);
        }

        l.exiting(getClass().getName(), "deQueueProcessingOwsLogJob");
    }

    /**
     * stop the timer when terminate the application.
     *
     * @return
     */
    @Override
    public void terminate() {
        Logger l = LogUtils.enterLog(getClass(), "terminateOwsLogQueue");
        while (m_processingQueue.size() > 0) {
            try {
                l.logp(Level.INFO, getClass().getName(), "terminate", "Waiting to terminate the OWS log queue. Remaining log requests:" + m_processingQueue.size());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                l.logp(Level.SEVERE, getClass().getName(), "terminate", "Failed to sleep", e);
            }
        }
        synchronized (m_lock) {
            if (isTimerInitializedAndActive()) {
                m_owsLogTimer.stop();
            }
        }

        l.exiting(getClass().getName(), "terminateOwsLogQueue");
    }


    public OwsLogProcessor getOwsLogProcessor() {
        return m_owsLogProcessor;
    }

    public void setOwsLogProcessor(OwsLogProcessor owsLogProcessor) {
        this.m_owsLogProcessor = owsLogProcessor;
    }

    public void verifyConfig() {
        if (getOwsLogProcessor() == null)
            throw new ConfigurationException("The required property 'owsLogProcessor' is missing.");
    }

    private OwsLogProcessor m_owsLogProcessor;

    private Timer m_owsLogTimer;

    private final Object m_lock = new Object();

    private final LinkedList<OwsLogRequest> m_processingQueue = new LinkedList<OwsLogRequest>();

    private static final String INVOKER_EVENT_TYPE = "OwsLogTimer";
    private static final String INVOKER_EVENT_MESSAGE = "OwsLogTimer";


}
