package dti.oasis.session;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.session.impl.UserSessionManagerImpl;
import dti.oasis.util.LogUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The UserSessionManager maintains all UserSession objects. If a User is logged in more than once,
 * associated with multiple HttpSessions, there is a separate UserSession for each HttpSession.
 * <p/>
 * The RequestLifecycleAdvisor uses the UserSessionManagerAdmin to associate the correct UserSession object
 * with the current request, and to disassociate the UserSession object from the request
 * when the request lifecycle is terminated.
 * Therefore, any Action, Bussiness Component, DAO, or related class that requires the current UserSession,
 * call the getUserSession() method.
 * <p/>
 * For all JSP pages, the RequestLifecycle has already Terminated,
 * so this UserSessionManager is no longer setup for the request.
 * Therefore, if the JSP page requires the UserSession, it can call getUserSession(HttpServletRequest).
 * However, if any Tags or classes used by the JSP require the UserSession,
 * the UserSessionManagerAdmin should first be used to associate the correct UserSession object with the current request,
 * by calling setupForRequest(HttpServletRequest), and cleaned up using the cleanupFromRequest() method.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
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
public abstract class UserSessionManager {

    /**
     * The bean name of a RequestStorageManager extension if this default is not used.
     */
    public static final String BEAN_NAME = "UserSessionManager";

    /**
     * Return an instance of the RequestStorageManager.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public synchronized static UserSessionManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance()._hasBean(BEAN_NAME)) {
                c_instance = (UserSessionManager) ApplicationContext.getInstance()._getBean(BEAN_NAME);
            }
            else{
                c_instance = new UserSessionManagerImpl();
            }
        }
        return c_instance;
    }

    /**
     * Return a boolean indicating if the UserSessionManager is configured for this application.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public static boolean isConfigured() {
        return getInstance().hasUserSession();
    }

    /**
     * Returns true if there is a UserSession associated with the current request.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public abstract boolean hasUserSession();

    /**
     * Get the UserSession associated with the current request.
     *
     * @throws dti.oasis.app.AppException if the UserSessionManager was not setup for the current request.
     */
    public abstract UserSession getUserSession();

    /**
     * Get the UserSession object associated with the user specified in the HttpServletRequest.getRemoteUser().
     * If there is no user identified in the HttpServletRequest, the UserSession for the anonymous user is used.
     * If the anonymous user is not configured, an AppException is thrown
     *
     * @param request the HttpServletRequest
     * @return the UserSession object for the current request
     * @throws dti.oasis.app.AppException if there was a problem determining the current user,
     *                                    and no anonymous userid was not configured.
     */
    public abstract UserSession getUserSession(HttpServletRequest request);


    private static UserSessionManager c_instance;
}