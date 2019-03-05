package dti.oasis.accesstrailmgr.request;

import dti.oasis.accesstrailmgr.AccessTrailManager;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.request.RequestLifecycleListener;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.app.ConfigurationException;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class to used to record any user activities on pages.
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 12, 2010
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class OasisAccessRequestLifecycleListener implements RequestLifecycleListener {

    /**
     * Implement this method to handle the Initialization Request Lifecycle Event.
     */
    public void initialize() {
        l.entering(getClass().getName(), "initialize");
        l.logp(Level.FINE, getClass().getName(), "initialize", "Nothing to initialize.");
        l.exiting(getClass().getName(), "initialize");
    }

    /**
     * Implement this method to handle the Termination Request Lifecycle Event.
     * This method will always be invoked upon termination of the request, regardless of whether failure() was called.
     */
    public void terminate() {
        l.entering(getClass().getName(), "terminate");
        try {
            RequestStorageManager rsm = RequestStorageManager.getInstance();
            if (!rsm.has(RequestStorageIds.EXECUTING_IN_BACKGROUND_THREAD) ||
                    !YesNoFlag.getInstance((String) rsm.get(RequestStorageIds.EXECUTING_IN_BACKGROUND_THREAD)).booleanValue()) {
                HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
                getAccessTrailManager().processAccessInfo(request);
            }
            else {
                l.logp(Level.FINE, getClass().getName(), "terminate", "Skipping terminate logic since executing in a background thread.");
            }
        } catch (Exception e) {
            l.logp(Level.SEVERE, getClass().getName(), "terminate", "Fail to terminate in OasisAccessRequestLifecycleListener", e);
        }
        l.exiting(getClass().getName(), "terminate");
    }

    /**
     * Implement this method to handle the Failure Request Lifecycle Event.
     * Return true if the failure was fixed; otherwise, false.
     *
     * @param e     the Throwable Exception that triggered the failure event.
     * @param fixed the problem has already be fixed.
     * @return true if the failure was fixed; otherwise, false.
     */
    public boolean failure(Throwable e, boolean fixed) {
        return false;
    }


    /**
     * verify config.
     */
    public void verifyConfig() {
        if (getAccessTrailManager() == null)
            throw new ConfigurationException("The required property 'accessTrailManager' is missing.");
    }

    public AccessTrailManager getAccessTrailManager() {
        return m_accessTrailManager;
    }

    public void setAccessTrailManager(AccessTrailManager accessTrailManager) {
        this.m_accessTrailManager = accessTrailManager;
    }

    private AccessTrailManager m_accessTrailManager;
    private final Logger l = LogUtils.getLogger(getClass());
}