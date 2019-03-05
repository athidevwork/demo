package dti.oasis.accesstrailmgr;

import dti.oasis.http.RequestIds;
import dti.oasis.request.RequestSession;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionIds;
import dti.oasis.session.impl.UserSessionManagerImpl;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
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
public class OwsLogProcessor implements javax.management.NotificationListener {

    /**
     * Method to handle a owsJob notification.
     *
     * @param notification
     * @param handBack
     */
    public void handleNotification(javax.management.Notification notification, Object handBack) {
        Logger l = LogUtils.enterLog(getClass(), "handleNotification", new Object[]{notification, handBack});

        OwsLogRequest owsLogRequest = (OwsLogRequest) notification.getUserData();
        boolean isRequestJobInProcessingQueue = false;
        try {
            owsLogRequest.setStartExecutionTime(System.currentTimeMillis());
            isRequestJobInProcessingQueue = m_owsLogQueueManager.checkJobStatusForRequest(owsLogRequest);
            if (!isRequestJobInProcessingQueue) {
                owsLogRequest.setOwsLogJobProcessing();
                RequestStorageManager rsm = RequestStorageManager.getInstance();
                RequestSession rs = owsLogRequest.getRequestSession();
                rsm.set(RequestStorageIds.HTTP_SEVLET_REQUEST, rs.getRequestStorageMap().get(RequestStorageIds.HTTP_SEVLET_REQUEST));
                rsm.set(UserSessionManagerImpl.USER_SESSION, rs.getUserSession());
                rsm.set(UserSessionIds.DB_POOL_ID, rs.getUserSession().get(UserSessionIds.DB_POOL_ID));
                rsm.set(RequestIds.WEB_SESSION_ID, rs.getUserSession().getSessionId());
                getOwsAccessTrailManager().processOwsLogRequest(owsLogRequest);
                owsLogRequest.setOwsLogJobComplete();
            }
            m_owsLogQueueManager.finishedProcessing(owsLogRequest);
            if (isRequestJobInProcessingQueue) {
                m_owsLogQueueManager.schedule(owsLogRequest);
            } else {
                owsLogRequest.setFinishExecutionTime(System.currentTimeMillis());
            }
            l.logp(Level.FINE, getClass().getName(), "processOwsJob", "LRT - OwsJobProcessor" + owsLogRequest.getOwsAccessTrailId()
                    + ": Cost time is: " + (owsLogRequest.getFinishExecutionTime() - owsLogRequest.getStartExecutionTime()) + "ms.");
        } catch (Exception e) {
            m_owsLogQueueManager.finishedProcessing(owsLogRequest);
            l.logp(Level.SEVERE, getClass().getName(), "OwsLogProcessor", "Failed to Process Ows Log : " + owsLogRequest.toString(), e);
            owsLogRequest.setOwsLogJobFailed();
        }  catch (Throwable throwable) {
            l.logp(Level.SEVERE, getClass().getName(), "OwsLogProcessor", "Caught Throwable -- Failed to Process Ows Log : " + owsLogRequest.toString());
            throwable.printStackTrace();
            owsLogRequest = null;
        }
        l.exiting(getClass().getName(), "handleNotification");
    }

    public void setOwsLogQueueManager(OwsLogQueueManager owsLogQueueManager) {
        m_owsLogQueueManager = owsLogQueueManager;
    }

    public OwsAccessTrailManager getOwsAccessTrailManager() {
        return m_owsAccessTrailManager;
    }

    public void setOwsAccessTrailManager(OwsAccessTrailManager owsAccessTrailManager) {
        this.m_owsAccessTrailManager = owsAccessTrailManager;
    }

    private OwsLogQueueManager m_owsLogQueueManager;

    private OwsAccessTrailManager m_owsAccessTrailManager;
}
