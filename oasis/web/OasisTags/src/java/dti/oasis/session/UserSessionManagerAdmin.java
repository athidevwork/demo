package dti.oasis.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * The UserSessionManagerAdmin Interface describes the methods uses to associate the correct UserSession
 * with the current request. The user id is either provided directly,
 * or taken from the HttpServletRequest.getRemoteUser().
 * If the HttpServletRequest has no associated user, and an anonymous user id is configured for this application,
 * the anonymous user id is used to create a UserSession.
 * <p/>
 * The RequestLifecycleAdvisor uses the UserSessionManagerAdmin to associate the correct UserSession object
 * with the current request, and to disassociate the UserSession object from the request
 * when the request lifecycle is terminated.
 * Therefore, any Action, Bussiness Component, DAO, or related class that requires the current UserSession,
 * call the getUserSession() method.
 * <p/>
 * If any Tags or classes used by a JSP require the UserSession,
 * the UserSessionManagerAdmin should first be used to associate the correct UserSession object with the current request,
 * by calling setupForRequest(HttpServletRequest), and cleaned up using the cleanupFromRequest() method.
 * <p/>
 * If the request is not part of a HttpServletRequest, such as with a Web Service call, the UserSession is connected
 * with the specified userId.
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
 * 02/208/2008      fcb         setupForRequest() and getCopy() added.
 * ---------------------------------------------------
 */

public interface UserSessionManagerAdmin {
    /**
     * Get the UserSession object associated with the user specified in the HttpServletRequest.getRemoteUser(),
     * and associate it with the current request.
     * If there is no user identified in the HttpServletRequest, the UserSession for the anonymous user is used.
     * If the anonymous user is not configured, an AppException is thrown
     *
     * @param request the HttpServletRequest
     * @throws dti.oasis.app.AppException if there was a problem determining the current user,
     *                                    and no anonymous userid was not configured.
     */
    public void setupForRequest(HttpServletRequest request);

    /**
     * Get the UserSession object associated with the given user id, and associate it with the current request.
     * If the OasisUser can not be loaded, an AppException is thrown.
     *
     * @throws dti.oasis.app.AppException if the Oasis User could not be determined.
     */
    public void setupForRequest(String userId);

     /**
     * Sets up request from the input request.
     *
     * @throws dti.oasis.app.AppException if the Oasis User could not be determined.
     */
    public void setupForRequest(UserSession userSession);

    /**
     * Disassociate the UserSession from the current request.
     */
    public void cleanupFromRequest();

    /**
     * Returns a copy of the user session. 
     */
    public UserSession getCopy();

  /**
   * Removes all user session information
   * @param session
   */
    public void cleanupUserSession(HttpSession session);

    /**
     * Get User Session Id (for a specific context)
     * @param session
     */
    public String getUserSessionId(HttpSession session);

    /**
     * Override session's max inactive interval
     * @param session
     */
    public void setHttpSessionMaxInactiveInterval(HttpSession session);

    public Boolean clearExpiredUsersSessionsFromRequestStorageManager();

    public void displayAllUserSessionsFromRequestStorageManager();
}