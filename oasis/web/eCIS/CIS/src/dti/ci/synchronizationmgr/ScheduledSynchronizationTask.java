package dti.ci.synchronizationmgr;

import dti.cs.cishubmgr.CisHubDataManager;
import dti.cs.cishubmgr.CisHubFeatures;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManagerAdmin;
import dti.oasis.util.LogUtils;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by eouyang on 3/24/2016.
 */
public class ScheduledSynchronizationTask {

    public void synchronizationEntityData() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "synchronizationEntityData");
            l.logp(Level.FINE, getClass().getName(), "synchronizationEntityData", "Task start time: " + Calendar.getInstance().getTime());
        }

        try {
            UserSession userSession = m_userSessionManagerAdmin.getCopy();
            RequestStorageManager.getInstance().set("userSession", userSession);
            RequestStorageManager.getInstance().set("webSessionId", userSession.getSessionId());

            boolean noError = true;
            RecordSet rs = getCisHubDataManager().synchronizeCisHub();
            if (rs.getSize() > 0) {
                noError = false;
            }

            if (!noError) {
                // if synchronization fails, we need to create features base on system parameter setting
                getCisHubFeatures().processFeatures(rs);
            } else {
                // if synchronization succeeds, we need to delete previous created features if there has any
                getCisHubFeatures().deleteFeatures();
            }
        } catch (Exception e) {
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINE, getClass().getName(), "synchronizationEntityData", "Error when synchronizing cis hub data: " + e.getMessage());
            }
        } finally {
            RequestStorageManager.getInstance().cleanupFromRequest();
        }

        if (l.isLoggable(Level.FINER)) {
            l.logp(Level.FINE, getClass().getName(), "synchronizationEntityData", "Task end time: " + Calendar.getInstance().getTime());
        }
    }

    public UserSessionManagerAdmin getUserSessionManagerAdmin() {
        return m_userSessionManagerAdmin;
    }

    public void setUserSessionManagerAdmin(UserSessionManagerAdmin userSessionManagerAdmin) {
        this.m_userSessionManagerAdmin = userSessionManagerAdmin;
    }

    public CisHubDataManager getCisHubDataManager() {
        return m_cisHubDataManager;
    }

    public void setCisHubDataManager(CisHubDataManager cisHubDataManager) {
        this.m_cisHubDataManager = cisHubDataManager;
    }

    public CisHubFeatures getCisHubFeatures() {
        return m_cisHubFeatures;
    }

    public void setCisHubFeatures(CisHubFeatures cisHubFeatures) {
        this.m_cisHubFeatures = cisHubFeatures;
    }

    private CisHubFeatures m_cisHubFeatures;
    private CisHubDataManager m_cisHubDataManager;
    private UserSessionManagerAdmin m_userSessionManagerAdmin;
}
