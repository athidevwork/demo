package dti.oasis.session.impl;

import dti.oasis.accesstrailmgr.AccessTrailManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.cachemgr.UserCacheManager;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.security.Authenticator;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.session.UserSessionManagerAdmin;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.util.StringUtils;
import org.apache.struts.Globals;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The UserSessionManagerImpl implements all UserSessionManager and UserSessionManagerAdmin logic.
 * <p/>
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
 * 11/14/2007       wer         Added ability to skip Load And Authenticate User if initialized by userId
 * 02/28/2008       fcb         getCopy() added.
 * 04/09/2008       wer         Enhanced to support configuring a dbPoolId for a role associated with a user or it's group. This does not work without HttpServletRequest yet.
 * 09/21/2012       fcb         136956: getUserSession - removed use of Logger. This method is called from 
 *                              Logger.processMessage, and the use of Logger will create an infinite recurssion.
 * ---------------------------------------------------
 */
public class UserSessionManagerImpl extends UserSessionManager implements UserSessionManagerAdmin {

    /**
     * The Default number of minutes a non-HTTP Session is valid for without activity.
     */
    public static long DEFAULT_NON_HTTP_USER_SESSION_TIMEOUT_IN_MINUTES = 30;

    @Override
    /**
     * Returns true if there is a UserSession associated with the current request.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public boolean hasUserSession() {
        return getRequestStorageManager()._has(USER_SESSION);
    }

    /**
     * Get the UserSession associated with the current request.
     * DO NOT USE Logger in this method.
     *
     * @throws dti.oasis.app.AppException if the UserSessionManager was not setup for the current request.
     */
    public UserSession getUserSession() {
        Logger l = LogUtils.enterLog(getClass(), "getUserSessionNoArgs");
        // Retrieve the cached UserSession from the RequestStorageManager
        UserSession userSession = (UserSession) getRequestStorageManager()._get(USER_SESSION);
        if (userSession == null) {
            AppException e = new AppException("The UserSessionManager was not setup for the current request.");
            throw e;
        } else {
            try {
                if (userSession instanceof HttpUserSession) {
                    if (l.isLoggable(Level.FINER))
                        l.logp(Level.FINER, getClass().getName(), "getUserSessionNoArgs", "UserSession is HttpUserSession");
                    HttpUserSession httpUserSession = (HttpUserSession) userSession;
                    if (l.isLoggable(Level.FINER))
                        l.logp(Level.FINER, getClass().getName(), "getUserSessionNoArgs", "HttpUserSession: " + httpUserSession);
                    if (httpUserSession.has(Globals.TRANSACTION_TOKEN_KEY)) {
                        if (l.isLoggable(Level.FINER))
                            l.logp(Level.FINER, getClass().getName(), "getUserSessionNoArgs", "httpUserSession.has: " + httpUserSession.get(Globals.TRANSACTION_TOKEN_KEY));
                    }

                } else {
                    if (l.isLoggable(Level.FINER))
                        l.logp(Level.FINER, getClass().getName(), "getUserSessionNoArgs", "UserSession is NOT HttpUserSession");
                }
            } catch (IllegalStateException ex) {
                l.logp(Level.WARNING, getClass().getName(), "getUserSessionNoArgs", "Caught IllegalStateException: UserSession " + userSession.getSessionId() + " Contains HttpSession that IS Invalid: " + userSession);
                ex.printStackTrace();
            }
        }
        if (l.isLoggable(Level.FINER))
            l.exiting(getClass().getName(), "getUserSessionNoArgs", userSession);

        return userSession;
    }

    /**
     * Get the UserSession object associated with the user specified in the HttpServletRequest.getRemoteUser().
     * If there is no user identified in the HttpServletRequest, the UserSession for the anonymous user is used.
     * If the anonymous user is not configured, an AppException is thrown
     *
     * @param request the HttpServletRequest
     * @return the UserSession object for the current request
     * @throws AppException if there was a problem determining the current user,
     *                      and no anonymous userid was not configured.
     */
    public UserSession getUserSession(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "getUserSession", new Object[]{request});

        if (l.isLoggable(Level.FINER)) {
            if(request!=null && request.getRequestURI()!=null) {
                l.logp(Level.FINER, getClass().getName(), "getUserSession", "request URI:" + request.getRequestURI());
                l.logp(Level.FINER, getClass().getName(), "getUserSession", "request query string:" + request.getQueryString());
                l.logp(Level.FINER, getClass().getName(), "getUserSession", "request getRequestedSessionId():" + request.getRequestedSessionId());
                l.logp(Level.FINER, getClass().getName(), "getUserSession", "request  isRequestedSessionIdValid:" + request.isRequestedSessionIdValid());
            } else {
                l.logp(Level.FINER, getClass().getName(), "getUserSession", "HttpServletRequest IS NULL");
            }
        }

        HttpUserSession userSession = null;
        String userSessionId = getUserSessionId(request);

        // Locate the existing UserSession
        if (m_userSessions.containsKey(userSessionId)) {
            userSession = (HttpUserSession) m_userSessions.get(userSessionId);
            userSession.updateLastAccessedTime();
            if (l.isLoggable(Level.FINER))
                l.logp(Level.FINER, getClass().getName(), "getUserSession","  XXX --- request.isRequestedSessionIdValid(): "
                    +request.isRequestedSessionIdValid()  + " UserSession key: "+userSessionId+" Session ID: "+request.getRequestedSessionId()) ;

            if (l.isLoggable(Level.FINEST))
                displayUserSessions();

            // Update the HttpSession in case the J2EE Server re-created the instance
            userSession.setHttpSession(request.getSession());

            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "getUserSession", "Found and retrieved session [" + userSession.getSessionId() + "] from m_userSessions collection with key [" + userSessionId + "]");
            }
        }
        // Create a new UserSession for this OasisUser
        else {

//            clearExpiredUsersSessionsFromRequestStorageManager();

            // Load the OasisUser information
            OasisUser oasisUser = null;
            try {
                oasisUser = ActionHelper.initializeOasisUser(request);
            } catch (Exception e) {
                AppException ae = new AppException("Failed to determine the Oasis User.", e);
                l.throwing(getClass().getName(), "getUserSession", ae);
                throw ae;
            }

            // Create a UserSession object for this User.
            userSession = new HttpUserSession(request.getSession(), oasisUser);

            // Store the UserSession, keyed by the HttpSession ID
            m_userSessions.put(userSessionId, userSession);

            if (l.isLoggable(Level.FINEST))
                displayUserSessions();

            if (l.isLoggable(Level.FINE)) {
              l.logp(Level.FINE, getClass().getName(), "getUserSession", "Created new http session [" + userSession.getSessionId() + "] and added it to m_userSessions collection with key [" + userSessionId + "]");
            }
        }


        l.exiting(getClass().getName(), "getUserSession", userSession);
        return userSession;
    }

    /**
     *   Clear expired Users' Sessions from RequestStorageManager
     */
    public Boolean clearExpiredUsersSessionsFromRequestStorageManager(){
        Boolean result = false;
        Logger l = LogUtils.enterLog(getClass(), "clearExpiredUsersSessionsFromRequestStorageManager");
        if (l.isLoggable(Level.FINER))
            l.logp(Level.FINER, getClass().getName(), "clearExpiredUsersSessionsFromRequestStorageManager","Clear Expired UsersSessions From RequestStorageManager...");
        result = getRequestStorageManager().clearExpiredUsersSessions();
        l.exiting(getClass().getName(), "clearExpiredUsersSessionsFromRequestStorageManager");
        return result;
    }


    public void displayAllUserSessionsFromRequestStorageManager(){
        Logger l = LogUtils.enterLog(getClass(), "displayAllUserSessionsFromRequestStorageManager");
        if (l.isLoggable(Level.FINER))
            l.logp(Level.FINER, getClass().getName(), "displayAllUserSessionsFromRequestStorageManager","Clear Expired UsersSessions From RequestStorageManager...");
        getRequestStorageManager().displayAllUserSessions();
        l.exiting(getClass().getName(), "displayAllUserSessionsFromRequestStorageManager");
    }

    /**
     * Get the UserSession object associated with the user specified in the HttpServletRequest.getRemoteUser(),
     * and associate it with the current request.
     * If there is no user identified in the HttpServletRequest, the UserSession for the anonymous user is used.
     * If the anonymous user is not configured, an AppException is thrown
     *
     * @param request the HttpServletRequest
     * @throws AppException if there was a problem determining the current user,
     *                      and no anonymous userid was not configured.
     */
    public void setupForRequest(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "setupForRequest", new Object[]{request});

        /*
          For each WAR within an EAR - a new http session instance will be created.
          http session object is not shared between WAR within the same EAR.
         */
        UserSession userSession = getUserSession(request);

        // Cache the UserSession in the RequestStorageManager
        getRequestStorageManager().set(USER_SESSION, userSession);

        l.logp(Level.FINER, getClass().getName(), "setupForRequest","createUserCacheForUser");

        String userSessionId = getUserSessionId(request);

        if(request.getSession().getAttribute(CACHED_USER_ID) == null){
            String userId =  userSession.getUserId();
            request.getSession().setAttribute(CACHED_USER_ID,userId);
            l.logp(Level.FINER, getClass().getName(),"setupForRequest"," Setting "+CACHED_USER_ID+" for session ID {"+request.getSession().getId()+"] "+
                "with key ["+userSessionId+"] to "+userSession.getUserId());
            addActiveUserSessions(userId, userSessionId);
        } else {
            l.logp(Level.FINER, getClass().getName(),"setupForRequest",CACHED_USER_ID+" exists for session ID {"+request.getSession().getId()+"] with key["+
                userSessionId+"]");
        }
        //Get Prior Login Timestamp
        if (request.getSession().getAttribute(IOasisAction.KEY_PRIOR_LOGIN_TS) == null){
            String webSessionId = ActionHelper.getJSessionId(request);
            if(webSessionId != null){
                if(StringUtils.isBlank(LogUtils.getPage()))
                    LogUtils.setPage("RequestURI:"+request.getRequestURI());
                String priorLoginTs = AccessTrailManager.getInstance().getPriorLoginTimestamp(userSession.getUserId(),webSessionId);
                LogUtils.setPage(null);
                if(priorLoginTs != null){
                    request.getSession().setAttribute(IOasisAction.KEY_PRIOR_LOGIN_TS, FormatUtils.formatDateTimeForDisplay(priorLoginTs));
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.logp(Level.FINER, getClass().getName(), "setupForRequest", "Session [" + userSession.getSessionId() + "]added to request storage manager for future retrieval.");
        }

        l.exiting(getClass().getName(), "setupForRequest");
    }

    /**
     * Get the UserSession object associated with the given user id, and associate it with the current request.
     * If the OasisUser can not be loaded, an AppException is thrown.
     *
     * @throws AppException if the Oasis User could not be determined.
     */
    public void setupForRequest(String userId) {
        Logger l = LogUtils.enterLog(getClass(), "setupForRequest", new Object[]{userId});

        UserSession userSession = getUserSession(userId);

        // Cache the UserSession in the RequestStorageManager
        getRequestStorageManager().set(USER_SESSION, userSession);

        l.exiting(getClass().getName(), "setupForRequest");
    }

    /**
     * Get the UserSession object associated with the given user id.
     * If the OasisUser can not be loaded, an AppException is thrown.
     *
     * @return a new UserSession object for the named userId.
     * @throws AppException if the Oasis User could not be determined.
     */
    protected UserSession getUserSession(String userId) {
        Logger l = LogUtils.enterLog(getClass(), "getUserSession", new Object[]{userId});

        UserSession userSession = null;

        // Locate the existing UserSession
        if (m_userSessions.containsKey(userId)) {
            userSession = (UserSession) m_userSessions.get(userId);

            l.logp(Level.FINER, getClass().getName(), "displayUserSessions","Old User("+userId+")");

            if (l.isLoggable(Level.FINEST))
                displayUserSessions();

            // Invalidate the session if it has timed out
            long sessionTimeoutInMillis = getNonHttpUserSessionTimeoutInMinutes() * 60000;
            if (userSession.getLastAccessedTime() < System.currentTimeMillis() - sessionTimeoutInMillis) {
                userSession = null;
                l.logp(Level.INFO, getClass().getName(), "getUserSession", "The current UserSession has timed out. Creating a new one.");
            }
            else {
                userSession.updateLastAccessedTime();
            }
        }
        // Create a new UserSession for this OasisUser if one is not found or is timed out
        if (userSession == null) {
            OasisUser oasisUser = null;
            // TODO: Determine the dbPoolId for this User's Role
            String dbPoolId = null;
            if (dbPoolId == null) {
                throw new UnsupportedOperationException("Can not yet determine the dbPoolId for a user without the HttpServletRequest.");
            }

            if (isSkipLoadAndAuthenticateUser()) {
                oasisUser = new OasisUser(userId, false);

            } else {
                // Verify that the user is valid
                boolean isUserValid = false;
                try {
                    String msg = Authenticator.isUserValid(userId, dbPoolId);
                    isUserValid = (msg == null);
                }
                catch (Exception e) {
                    l.logp(Level.SEVERE, getClass().getName(), "getUserSession", "Failed to call Authenticator.isUserValid", e);
                }
                if (!isUserValid) {
                    throw new SecurityException("The userId '" + userId + "' is not a valid user for this application.");
                }

                // Load the OasisUser information
                try {
                    oasisUser = Authenticator.getUser(userId, dbPoolId);
                }
                catch (Exception e) {
                }
                if (oasisUser == null) {
                    throw new SecurityException("The userId '" + userId + "' is not a valid user for this application.");
                }

                // Update the logged in data for this user.
                try {
                    Authenticator.updateLoggedInDate(oasisUser.getUserId(), dbPoolId);
                }
                catch (Exception e) {
                    l.logp(Level.SEVERE, getClass().getName(), "getUserSession", "Failed to update the logged in date for the user '" + userId + "'", e);
                }
            }

            // Create a UserSession object for this User.
            userSession = new DefaultUserSession(oasisUser);

            // Cache the dbPoolId and Group Name for the session if it is configured
            if (dbPoolId != null) {
                userSession.set(IOasisAction.KEY_DBPOOLID, dbPoolId);

                // TODO: Determine how to get the dbPoolId RoleName when there is no HttpServletRequest
                String dbPoolIdRoleName = null;
                userSession.set(IOasisAction.KEY_DBPOOLIDROLENAME, dbPoolIdRoleName);
                System.out.println("UserSessionManagerImpl.getUserSession: dbPoolIdRoleName = " + dbPoolIdRoleName);
            }

            l.logp(Level.FINER, getClass().getName(), "getUserSession","New User("+userId+")");

            if (l.isLoggable(Level.FINEST))
                displayUserSessions();

            // Store the UserSession, keyed by the HttpSession ID
            m_userSessions.put(userId, userSession);

        }

        l.exiting(getClass().getName(), "getUserSession", userSession);
        return userSession;
    }

    /**
     * Disassociate the UserSession from the current request, it one is setup.
     */
    public void cleanupFromRequest() {
        Logger l = LogUtils.enterLog(getClass(), "cleanupFromRequest");

        if (getRequestStorageManager().has(USER_SESSION))
            getRequestStorageManager().remove(USER_SESSION);

        if (l.isLoggable(Level.FINEST))
            displayUserSessions();

        l.exiting(getClass().getName(), "cleanupFromRequest");
    }


    private UserSession removeUserSession(String userSessionId){
        Logger l = LogUtils.enterLog(getClass(), "removeUserSession", new Object[]{userSessionId});
        UserSession result = null;
        if (m_userSessions.containsKey(userSessionId)) {
            if (l.isLoggable(Level.FINER))
                l.logp(Level.FINER, getClass().getName(), "removeUserSession"," m_userSessions.containsKey( "+userSessionId+") OK");
            HttpUserSession userSession = (HttpUserSession) m_userSessions.get(userSessionId);
            if(userSession!=null){
                String userId = userSession.getUserId();
                if (l.isLoggable(Level.FINER))
                    l.logp(Level.FINER, getClass().getName(), "removeUserSession", " Found userSession for userId "+userId+" CAN CLEAR CACHE!");
            } else {
                if (l.isLoggable(Level.FINER))
                    l.logp(Level.FINER, getClass().getName(), "removeUserSession"," Can't find userSession: Can't clearUserCacheForUser");
            }
            if (l.isLoggable(Level.FINER))
                l.logp(Level.FINER, getClass().getName(), "removeUserSession","   --- Before removeUserSession("+userSessionId+")");
            if (l.isLoggable(Level.FINEST))
                displayUserSessions();
            result = m_userSessions.remove(userSessionId);
            if (l.isLoggable(Level.FINER))
                l.logp(Level.FINER, getClass().getName(), "removeUserSession","   --- removed UserSession("+userSessionId+"): "+result);
            if (l.isLoggable(Level.FINEST))
                displayUserSessions();
        } else {
            l.logp(Level.FINER, getClass().getName(), "removeUserSession", " NO m_userSessions.containsKey( "+userSessionId+") ");

        }

        l.exiting(getClass().getName(), "removeUserSession");

        return result;
    }

    /**
     * Removes all user session information
     * @param session
     */
    public void cleanupUserSession(HttpSession session) {
        Logger l = LogUtils.enterLog(getClass(), "cleanupUserSession", new Object[]{session});

        String userSessionId = getUserSessionId(session);

        UserSession removedUserSession = removeUserSession(userSessionId);
        if (l.isLoggable(Level.FINER)) {
            if(removedUserSession != null) {
                l.logp(Level.FINER, getClass().getName(), "cleanupUserSession", " Successfully Removed UserSession: " + removedUserSession);
            }
        }

        String userCacheUserId = (String)session.getAttribute(CACHED_USER_ID);

        if (l.isLoggable(Level.FINER))
            l.logp(Level.FINER, getClass().getName(),"cleanupUserSession",CACHED_USER_ID+": " +userCacheUserId+". SESSION ID: ["+session.getId()+"]. Key:["+userSessionId+"]");

        if(!StringUtils.isBlank(userCacheUserId)){
            l.logp(Level.FINER, getClass().getName(),"cleanupUserSession",CACHED_USER_ID+": " +userCacheUserId+". SESSION ID: ["+session.getId()+"]. Key:["+userSessionId+"]. CLEAR USER CACHE....");

            removeActiveUserSessions(userCacheUserId, userSessionId);
            clearUserCache(userCacheUserId, userSessionId);

            l.logp(Level.FINER, getClass().getName(),"cleanupUserSession",CACHED_USER_ID+": " +userCacheUserId+". SESSION ID: ["+session.getId()+"]. Key:["+userSessionId+"]. CLEARED USER CACHE");
        } else {
            l.logp(Level.FINER, getClass().getName(),"cleanupUserSession",CACHED_USER_ID+" is BLANK. CAN'T CLEAR USER CACHE SESSION ID: ["+session.getId()+"]. Key:["+userSessionId+"]");
        }

//        if (l.isLoggable(Level.FINE))
//            getRequestStorageManager().displayAllUserSessions();
//        clearExpiredUsersSessionsFromRequestStorageManager();

        l.exiting(getClass().getName(), "cleanupUserSession");
    }

    public String getUserSessionId(HttpSession session){
        return session.getId() + ":" + session.getServletContext().getContextPath();
    }

    protected String getUserSessionId(HttpServletRequest request){
        return request.getSession().getId() + ":" + request.getContextPath();
    }

    /**
     * Clears UserCacheManager for userId
     * @param userId
     */
    private void clearUserCache(String userId, String userSessionId) {
        Logger l = LogUtils.enterLog(getClass(), "clearUserCache", new Object[]{userId, userSessionId});

        if(!isUserActive(userId)){
            l.logp(Level.FINER, getClass().getName(), "clearUserCache"," User is no longer active. clearUserCache for userId "+userId+" Session Id: ["+userSessionId+"]");
            UserCacheManager.getInstance().clearUserCacheForUser(userId);
        } else {
            l.logp(Level.FINER, getClass().getName(), "clearUserCache"," User "+userId+"still active. Do nothing Session Id: ["+userSessionId+"]");
        }

        l.exiting(getClass().getName(), "clearUserCache");
    }


    private void addActiveUserSessions(String userId, String userSessionId){
        Logger l = LogUtils.enterLog(getClass(), "addActiveUserSessions", new Object[]{userId, userSessionId});
        boolean found =  m_activeUsers.containsKey(userId);

        if(!found){
            l.logp(Level.FINER, getClass().getName(), "addActiveUserSessions","userId "+userId+" IS NOT found among Active Users");
            List<String> userSessions = Collections.synchronizedList(new ArrayList<String>());
            boolean result = userSessions.add(userSessionId);
            if (l.isLoggable(Level.FINER)){
                if(result)
                    l.logp(Level.FINER, getClass().getName(), "addActiveUserSessions","Added Session Id: ["+userSessionId+"] to Active User "+userId);
                else
                    l.logp(Level.FINER, getClass().getName(), "addActiveUserSessions", "Did NOT add Session Id: [" + userSessionId + "] to Active User " + userId);
            }
            m_activeUsers.put(userId, userSessions);

            l.logp(Level.FINER, getClass().getName(), "addActiveUserSessions","Added userId "+userId+" to Active Users");
        } else {
            l.logp(Level.FINER, getClass().getName(), "addActiveUserSessions","userId "+userId+" found among Active Users");
            List<String> userSessions = m_activeUsers.get(userId);
            boolean result = userSessions.add(userSessionId);

            if (l.isLoggable(Level.FINER)){
                if(result)
                    l.logp(Level.FINER, getClass().getName(), "addActiveUserSessions","Added Session Id: ["+userSessionId+"] to Active User "+userId);
                else
                    l.logp(Level.FINER, getClass().getName(), "addActiveUserSessions","Did NOT add Session Id: ["+userSessionId+"] to Active User "+userId);
            }

//            m_activeUsers.replace(userId, userSessions);
//            l.logp(Level.FINER, getClass().getName(), "addActiveUserSessions","Updated Active User: "+userId+" amongst Active Users");
        }

        l.exiting(getClass().getName(), "addActiveUserSessions");
    }

    private void removeActiveUserSessions(String userId, String userSessionId){
        Logger l = LogUtils.enterLog(getClass(), "removeActiveUserSessions", new Object[]{userId, userSessionId});
        boolean found =  m_activeUsers.containsKey(userId);

        if(found){
            if (l.isLoggable(Level.FINER))
                l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions","userId "+userId+" found among Active Users");
            List userSessions = m_activeUsers.get(userId);
            if(!userSessions.isEmpty()) {
                if (l.isLoggable(Level.FINER))
                    l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions","userSessions are NOT EMPTY for Active User "+userId);
                boolean result = userSessions.remove(userSessionId);

                if (l.isLoggable(Level.FINER)){
                    if(result)
                        l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions","Removed Session Id: ["+userSessionId+"] from Active User "+userId);
                    else
                        l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions","Did NOT remove Session Id: ["+userSessionId+"] from Active User "+userId);
                }

                if(userSessions.isEmpty()){
                    if (l.isLoggable(Level.FINER))
                        l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions","No userSessions remain for Active User "+userId+" Removing....");
                    List<String> userSessionsRemoved = m_activeUsers.remove(userId);
                    if (l.isLoggable(Level.FINER)) {
                        if (userSessionsRemoved != null)
                            l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions", "Removed UserSessions for Active User " + userId);
                        else
                            l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions", "Can't Remove UserSessions for Active User " + userId);
                    }
                } else {
                    if (l.isLoggable(Level.FINER))
                        l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions","userSessions exist for Active User "+userId);
                }

//                m_activeUsers.replace(userId, userSessions);
//                l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions","Added Session Id: ["+userSessionId+"] to Active User: "+userId);
            } else {
                if (l.isLoggable(Level.FINER))
                    l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions","userSessions are EMPTY for Active User "+userId+" Removing because userSessions are EMPTY....");
                List<String> userSessionsRemoved = m_activeUsers.remove(userId);
                if (l.isLoggable(Level.FINER)) {
                    if (userSessionsRemoved != null)
                        l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions", "Removed UserSessions for Active User " + userId);
                    else
                        l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions", "Can't Remove UserSessions for Active User " + userId);
                }
            }
        } else {
            if (l.isLoggable(Level.FINER))
                l.logp(Level.FINER, getClass().getName(), "removeActiveUserSessions","userId "+userId+" IS NOT found among Active Users");
        }

        l.exiting(getClass().getName(), "removeActiveUserSessions");
    }

    private boolean isUserActive(String userId){
        Logger l = LogUtils.enterLog(getClass(), "isUserActive", new Object[]{userId});
        boolean result =  m_activeUsers.containsKey(userId);

        if (l.isLoggable(Level.FINER)){
            if(result){
                l.logp(Level.FINER, getClass().getName(), "isUserActive","userId "+userId+" found among Active Users");
                List<String> userSessions = m_activeUsers.get(userId);
                l.logp(Level.FINER, getClass().getName(), "isUserActive","There are "+userSessions.size()+" User Sessions for Active User: "+userId);

                int counter = 0;
                for (String userSessionId : userSessions) {
                    l.logp(Level.FINER, getClass().getName(), "isUserActive", "userSessionId["+counter+"]="+userSessionId);
                    counter++;
                }
            } else {
                l.logp(Level.FINER, getClass().getName(), "isUserInActiveUsers","userId "+userId+" IS NOT found among Active Users");
            }
        }

        l.exiting(getClass().getName(), "isUserActive");

        return result;
    }

    /**
     * Override session's max inactive interval with value of "sessionTimeout"
     * in "applicationConfig.properties"
     * @param session
     */
    public void setHttpSessionMaxInactiveInterval(HttpSession session){
        Logger l = LogUtils.enterLog(getClass(), "setHttpSessionMaxInactiveInterval", new Object[]{session});

        String sTimeout = ApplicationContext.getInstance().getProperty("sessionTimeout", "");

        l.logp(Level.FINER, getClass().getName(), "setHttpSessionMaxInactiveInterval","sTimeout: "+sTimeout);

        if (!StringUtils.isBlank(sTimeout) && StringUtils.isNumeric(sTimeout)) {
            session.setMaxInactiveInterval(Integer.parseInt(sTimeout) * 60);

            l.logp(Level.FINER, getClass().getName(), "setHttpSessionMaxInactiveInterval","setMaxInactiveInterval to: "+session.getMaxInactiveInterval());

        } else {
            l.logp(Level.FINER, getClass().getName(), "setHttpSessionMaxInactiveInterval","Can't get sTimeout "+sTimeout);
        }

        l.exiting(getClass().getName(), "setHttpSessionMaxInactiveInterval");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * This method does not log any messages so it can be used by the Logger
     */
    public UserSessionManagerImpl() {
    }

    public void verifyConfig() {
        if (getRequestStorageManager() == null)
            throw new ConfigurationException("The required property 'requestStorageManager' is missing.");
    }

    public RequestStorageManager getRequestStorageManager() {
        if (m_requestStorageManager == null) {
            // Allow this class to not be configured through Spring
            m_requestStorageManager = RequestStorageManager.getInstance();
        }
        return m_requestStorageManager;
    }

    public void setRequestStorageManager(RequestStorageManager requestStorageManager) {
        m_requestStorageManager = requestStorageManager;
    }

    public long getNonHttpUserSessionTimeoutInMinutes() {
        return m_nonHttpUserSessionTimeoutInMinutes;
    }

    public void setNonHttpUserSessionTimeoutInMinutes(long nonHttpUserSessionTimeoutInMinutes) {
        m_nonHttpUserSessionTimeoutInMinutes = nonHttpUserSessionTimeoutInMinutes;
    }

    public boolean isSkipLoadAndAuthenticateUser() {
        return m_skipLoadAndAuthenticateUser;
    }

    public void setSkipLoadAndAuthenticateUser(boolean skipLoadAndAuthenticateUser) {
        m_skipLoadAndAuthenticateUser = skipLoadAndAuthenticateUser;
    }

    public void setupForRequest(UserSession userSession) {}

    public UserSession getCopy() {
        Logger l = LogUtils.enterLog(getClass(), "getCopy");

        UserSessionManager usm = UserSessionManager.getInstance();
        UserSession userSession=null;

        try {
            BaseUserSession bus = (BaseUserSession)usm.getUserSession();
            userSession = (UserSession)bus.clone();
        }
        catch (CloneNotSupportedException e) {
            l.logp(Level.SEVERE, getClass().getName(), "getCopy", "Failed to copy the user session.", e);
        }

        l.exiting(getClass().getName(), "copy", userSession);
        return userSession;

    }

    private void displayUserSessions(){
        Logger l = LogUtils.enterLog(getClass(), "displayUserSessions");
        if (l.isLoggable(Level.FINEST))
            l.logp(Level.FINEST, getClass().getName(), "displayUserSessions","   --- displayUserSessions() Begin --- Map Size : "+m_userSessions.size());
        String key = "";

        for (Map.Entry<String, UserSession> entry : m_userSessions.entrySet())
        {
            key = entry.getKey();
            UserSession userSession = entry.getValue();
            if (l.isLoggable(Level.FINEST))
                l.logp(Level.FINEST, getClass().getName(), "displayUserSessions","Key: "+key+" \n"+"Value: " + userSession);

            try {
                if(userSession instanceof HttpUserSession){
                    if (l.isLoggable(Level.FINEST))
                        l.logp(Level.FINEST, getClass().getName(), "displayUserSessions","UserSession is HttpUserSession");
                    HttpUserSession httpUserSession = (HttpUserSession)userSession;
                    if (l.isLoggable(Level.FINEST))
                        l.logp(Level.FINEST, getClass().getName(), "displayUserSessions","HttpUserSession: " + httpUserSession);
                    if(httpUserSession.has(Globals.TRANSACTION_TOKEN_KEY)) {
                        if (l.isLoggable(Level.FINEST))
                            l.logp(Level.FINEST, getClass().getName(), "displayUserSessions","httpUserSession.has: " + httpUserSession.get(Globals.TRANSACTION_TOKEN_KEY));
                    }

                } else {
                    if (l.isLoggable(Level.FINEST))
                    l.logp(Level.FINEST, getClass().getName(), "displayUserSessions","UserSession is NOT HttpUserSession");
                }
            } catch (IllegalStateException ex){
                l.logp(Level.WARNING, getClass().getName(), "displayUserSessions","Caught IllegalStateException UserSession "+key+" Contains HttpSession that IS Invalid: "+userSession);
                //ex.printStackTrace();
            }
        }
        l.exiting(getClass().getName(), "displayUserSessions");

    }

    private RequestStorageManager m_requestStorageManager;
    private Map<String, UserSession> m_userSessions = new ConcurrentHashMap<String, UserSession>();
    private ConcurrentHashMap<String, List<String>> m_activeUsers = new ConcurrentHashMap<String, List<String>>();
    private long m_nonHttpUserSessionTimeoutInMinutes = DEFAULT_NON_HTTP_USER_SESSION_TIMEOUT_IN_MINUTES;
    private boolean m_skipLoadAndAuthenticateUser = false;

    public static final String USER_SESSION = "userSession";
    protected static final String CACHED_USER_ID = "userCacheUserId";
}