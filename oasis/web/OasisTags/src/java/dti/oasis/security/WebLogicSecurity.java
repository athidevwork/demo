package dti.oasis.security;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionIds;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.util.StringUtils;
import org.apache.batik.dom.util.HashTable;
import weblogic.management.mbeanservers.runtime.RuntimeServiceMBean;
import weblogic.management.runtime.ServerRuntimeMBean;
import weblogic.servlet.security.ServletAuthentication;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles security using the WebLogic LDAP Security Realm
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 23, 2003
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  2/6/2004        jbe     Added Logging
 *  3/17/2004       jbe     Cleanup, Added getGroups & getUsers
 *                          addMemberToGroup, getMemberGroups
 *                          removeMemberFromGroup, removeUser,
 *                          resetPassword
 *  7/7/2004        jbe     Added logout
 *  8/4/2004        jbe     throw UserAlreadyExistsException instead
 *                          of WebLogic AlreadyExistsException
 * 09/11/2006       sxm     Added createUser(HttpServletRequest, String, String, String, ArrayList)
 * 01/23/2007       wer     Changed usage of new Boolean(x) in logging to String.valueOf(x);
 * 04/09/2008       wer     Changed thrown exceptions to AppExcetion, and removed them from the method declarations.
 *                          Also enhanced getMemberGroups() to work if the logged in user is not an administrator.
 * 08/28/2008       Larry   Add methods getADUserNames() and isADUser() to check Active Directory Users
 * 05/13/2010       mxg     Added ADUser Attribute to the User Session to avoid checking Active Directory on every request
 * 02/21/2011       kshen   Fix the problem that url can not contain "\r\n".
 * 04/21/2016       huixu   Issue#169769 Fix WebLogicSecurity.getAuthenticators to work in WebLogic 12.2.1
 * 01/30/2017       cv      182615 - Modified getAuthenticationProviders() to get the ServerRuntime Listening protocol and port.
 * 04/25/2017       cv      184692 - Added public methods getRuntimeURL and setRuntimeURL to get local runtime url.
 * 04/12/2018       cv      192560 - check if the userSession is not null.
 * ---------------------------------------------------
 */

public class WebLogicSecurity implements IJ2EESecurity {

    public static final String DEFAULT_AUTHENTICATOR_CLASS = "weblogic.security.providers.authentication.DefaultAuthenticatorMBeanImpl";
    public static final String ACTIVE_DIRECTORY_AUTHENTICATOR_CLASS = "weblogic.security.providers.authentication.ActiveDirectoryAuthenticatorMBeanImpl";


    private static final WebLogicSecurity INSTANCE = new WebLogicSecurity();

    // Private constructor supresses
    // default public constructor
    private WebLogicSecurity() {
    }

    public static WebLogicSecurity getInstance() {
        return INSTANCE;
    }

    private String makeErrorURL(HttpServletResponse response,
                                String message, String origurl) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "makeErrorURL", new Object[]{response, message, origurl});
        }
        message = message.replaceAll("\r", "<BR>").replaceAll("\n", "");
        String url = response.encodeRedirectURL("changepassword.jsp?errormsg=" + message + "&origurl=" + origurl);

        l.exiting(getClass().getName(), "makeErrorURL", url);
        return url;
    }

    /**
     * get domain Mbean server
     * @return
     */
    protected MBeanServer getMBeanServer() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMBeanServer");
        }
        Context ctx = null;
        MBeanServer server = null;
        try {
            ctx = new InitialContext();
            server = (MBeanServer) ctx.lookup("java:comp/env/jmx/runtime");
        } catch (NamingException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get the runtime MBeanServer", e);
            l.throwing(getClass().getName(), "getAuthenticators", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getMBeanServer", server);
        return server;
    }

    /**
     * get AuthenticationProviders
     * @return
     */
    protected List<ObjectName> getAuthenticationProviders(){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAuthenticationProviders");
        }
        List<ObjectName> authenticationProviderList = null;
        MBeanServer server = getMBeanServer();
        try {
            ObjectName runtimeService = new ObjectName("com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean");


            ObjectName domainConfiguration = (ObjectName) server.getAttribute(runtimeService, "DomainConfiguration");
            ObjectName securityConfiguration = (ObjectName) server.getAttribute(domainConfiguration, "SecurityConfiguration");
            ObjectName[] realms = (ObjectName[]) server.getAttribute(securityConfiguration, "Realms");
            authenticationProviderList = new ArrayList<>();
            for (ObjectName realm : realms) {
                ObjectName[] authenticationProviders = (ObjectName[]) server.getAttribute(realm, "AuthenticationProviders");
                for (ObjectName authenticationProvider : authenticationProviders) {
                    authenticationProviderList.add(authenticationProvider);
                }
            }

            //set the runtime url
            setRuntimeURL();

        } catch (Exception e) {
            l.logp(Level.SEVERE, this.getClass().getName(), "getAuthenticationProviders", "Failed to AuthenticationProviders.", e);
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get the runtime MBeanServer", e);
            l.throwing(getClass().getName(), "getAuthenticators", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getAuthenticationProviders", server);
        return authenticationProviderList;
    }

    /**
     * Check if the user is AD user or not
     *
     * @param request
     * @param userId
     * @return boolean
     */
    public boolean isADUser(String userId, HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isADUser", new Object[]{userId, request});
        }

        HttpSession session = request.getSession();

        boolean result = false;
        if (session.getAttribute(IOasisAction.KEY_AD_USER) != null) {
            result = ((Boolean) session.getAttribute(IOasisAction.KEY_AD_USER)).booleanValue();
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "isADUser", "ADUser is in Session: " + result);
            }
        } else if (StringUtils.isBlank(userId) || "anonymous".equalsIgnoreCase(userId)) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "isADUser", "Anonymous user; isADUser = " + result);
            }
        } else {
            l.logp(Level.FINE, getClass().getName(), "isADUser", "ADUser is NOT in Session");
            List<ObjectName> authenticationProviderList = getAuthenticationProviders();

            MBeanServer server = getMBeanServer();
            for (ObjectName authenticationProvider : authenticationProviderList) {
                MBeanInfo mBeanInfo = null;
                try {
                    mBeanInfo = server.getMBeanInfo(authenticationProvider);
                } catch (Exception e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to get MBeanInfo."
                            + authenticationProvider.getCanonicalName(), e);
                    throw ae;
                }
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "isADUser", "Authentication Provider name " + authenticationProvider.getCanonicalName());
                }
                if (ACTIVE_DIRECTORY_AUTHENTICATOR_CLASS.equals(mBeanInfo.getClassName())) {
                    try {
                        result = (Boolean) server.invoke(authenticationProvider, "userExists", new Object[]{userId},
                                new String[]{"java.lang.String"});
                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "isADUser", "Is in AD: " + result);
                        }
                        if (result){
                            break;
                        }
                    } catch (Exception e) {
                        l.logp(Level.SEVERE, this.getClass().getName(), "isADUser", "Failed to invoke userExists on ActiveDirectoryAuthenticator "
                                + authenticationProvider.getCanonicalName(), e);
                        if (e.getMessage().indexOf("[Security") >= 0) {
                            String msg = e.getMessage().substring(e.getMessage().indexOf("]") + 1);
                            throw new IllegalArgumentException(msg);
                        } else {
                            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get the list of users.", e);
                            throw ae;
                        }
                    }
                }
            }
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "isADUser", "Checked isADUser = " + result);
            }
            session.setAttribute(IOasisAction.KEY_AD_USER, new Boolean(result));
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isADUser", new Boolean(result));
        }
        return result;
    }

    /**
     * Resets a user password (admin privleges required)
     *
     * @param request
     * @param userId
     * @param password
     */
    public void resetPassword(HttpServletRequest request, String userId, String password) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resetPassword", new Object[]{request, userId, password});
        }

        // check if user is AD User, the password could be reset for only Non AD user. Larry
        if (isADUser(userId, request)) {
            // put error message in request that indicate user only AD exists, so password could not be reset.
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to reset the user password.", new Exception("User " + userId + " is AD User, password could not be reset."));
            l.throwing(getClass().getName(), "resetPassword", ae);
            throw ae;

        }
        boolean resetPassword = false;
        List<ObjectName> authenticationProviderList = getAuthenticationProviders();

        MBeanServer server = getMBeanServer();
        for (ObjectName authenticationProvider : authenticationProviderList) {
            try {
                MBeanInfo mBeanInfo = null;
                try {
                    mBeanInfo = server.getMBeanInfo(authenticationProvider);
                } catch (Exception e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to get MBeanInfo."
                            + authenticationProvider.getCanonicalName(), e);
                    throw ae;
                }
                if (DEFAULT_AUTHENTICATOR_CLASS.equals(mBeanInfo.getClassName())) {
                    boolean isUserExists = (Boolean) server.invoke(authenticationProvider, "userExists", new Object[]{userId}, new String[]{"java.lang.String"});
                    if (isUserExists) {
                        //Reset password
                        server.invoke(authenticationProvider, "resetUserPassword", new Object[]{userId, password},
                                new String[]{"java.lang.String", "java.lang.String"});
                        l.logp(Level.INFO, getClass().getName(), "resetPassword", "Reset user password success");
                        resetPassword = true;
                        break;
                    } else {
                        l.logp(Level.FINE, getClass().getName(), "changePassword", "User doesn't exist in:" + authenticationProvider.getCanonicalName());
                    }
                }
            } catch (Exception e) {
                resetPassword = false;
                if (e.getMessage().indexOf("[Security") >= 0) {
                    String msg = e.getMessage().substring(e.getMessage().indexOf("]") + 1);
                    l.logp(Level.SEVERE, this.getClass().getName(), "resetPassword", "Failed to execute the invoke: " + msg, e);
                    throw new IllegalArgumentException(msg);
                } else {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to locate the User Information.", e);
                    l.logp(Level.SEVERE, this.getClass().getName(), "resetPassword", "Failed to locate the User Information", e);
                    throw ae;
                }
            }
        }

        if (!resetPassword) {
            AppException ae = new AppException("Failed to Reset the password for the user: " + userId);
            l.throwing(getClass().getName(), "resetPassword", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "resetPassword");
    }

    /**
     * Logs a user out when single signon is enabled
     *
     * @param request
     */
    public void logout(HttpServletRequest request) {
//        ServletAuthentication.logout( request );
        ServletAuthentication.invalidateAll(request);
    }

    /**
     * Determines if a user already exists in the WebLogic LDAP Security REALM
     *
     * @param request
     * @param userId  userid
     * @return true/false
     */
    public boolean userExists(HttpServletRequest request, String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "userExists", new Object[]{request, userId});
        }

        boolean foundDefaultAuthenticator = false;
        boolean exists = false;
        List<ObjectName> authenticationProviderList = getAuthenticationProviders();

        MBeanServer server = getMBeanServer();
        for (ObjectName authenticationProvider : authenticationProviderList) {
            try {
                MBeanInfo mBeanInfo = null;
                try {
                    mBeanInfo = server.getMBeanInfo(authenticationProvider);
                } catch (Exception e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to get MBeanInfo."
                            + authenticationProvider.getCanonicalName(), e);
                    throw ae;
                }
                if (DEFAULT_AUTHENTICATOR_CLASS.equals(mBeanInfo.getClassName())) {
                    foundDefaultAuthenticator = true;
                    exists = (Boolean) server.invoke(authenticationProvider, "userExists", new Object[]{userId}, new String[]{"java.lang.String"});
                    if (exists) {
                        l.logp(Level.FINE, getClass().getName(), "userExists", "Find user in " + authenticationProvider.getCanonicalName());
                        break;
                    }else{
                        l.logp(Level.FINE, getClass().getName(), "userExists", "User doesn't exist in " + authenticationProvider.getCanonicalName());
                    }
                }
            } catch (Exception e) {
                l.logp(Level.SEVERE, this.getClass().getName(), "userExists", "Failed to determine if the user exists.", e);
                if (e.getMessage().indexOf("[Security") >= 0) {
                    String msg = e.getMessage().substring(e.getMessage().indexOf("]") + 1);
                    throw new IllegalArgumentException(msg);
                } else {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if the user exists.", e);
                    l.throwing(getClass().getName(), "userExists", ae);
                    throw ae;
                }
            }
        }

        if (!foundDefaultAuthenticator) {
            AppException ae = new AppException("Failed to locate the DefaultAuthenticator.");
            l.throwing(getClass().getName(), "userExists", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getUsers", String.valueOf(exists));
        return exists;

    }

    /**
     * Handles changing passwords in the WebLogic LDAP Security Realm
     *
     * @param request
     * @param response
     */
    public boolean changePassword(HttpServletRequest request,
                                  HttpServletResponse response) {
        l.entering(getClass().getName(), "changePassword");
        // Note that even though we are running as a privileged user,
        // response.getRemoteUser() still returns the user who authenticated.
        // weblogic.security.Security.getCurrentUserId() will return the
        // run-as user.
        String username = request.getRemoteUser();
        // Get the arguments
        String currentpassword = request.getParameter("currentpassword");
        String newpassword = request.getParameter("newpassword");
        String confirmpassword = request.getParameter("confirmpassword");
        String origurl = request.getParameter(IOasisAction.PARM_URL);

        try {
            // Validate the arguments
            if (currentpassword == null || currentpassword.length() == 0) {
                response.sendRedirect(makeErrorURL(response, "Please enter a value for \"Current Password\".", origurl));
                l.exiting(getClass().getName(), "changePassword", String.valueOf(false));
                return false;
            }
            if (newpassword == null || newpassword.length() == 0) {
                response.sendRedirect(makeErrorURL(response, "Please enter a value for \"Password\".", origurl));
                l.exiting(getClass().getName(), "changePassword", String.valueOf(false));
                return false;
            }
            if (confirmpassword == null || confirmpassword.length() == 0) {
                response.sendRedirect(makeErrorURL(response, "Please enter a value for \"Confirm Password\".", origurl));
                l.exiting(getClass().getName(), "changePassword", String.valueOf(false));
                return false;
            }
            if (!newpassword.equals(confirmpassword)) {
                response.sendRedirect(makeErrorURL(response, "New passwords did not match.", origurl));
                l.exiting(getClass().getName(), "changePassword", String.valueOf(false));
                return false;
            }
            if (username == null || username.length() == 0) {
                response.sendRedirect(makeErrorURL(response, "UserId must not be null.", origurl));
                l.exiting(getClass().getName(), "changePassword", String.valueOf(false));
                return false;
            }
            if (newpassword.equals(currentpassword)) {
                response.sendRedirect(makeErrorURL(response, "Old and new passwords must not match.", origurl));
                l.exiting(getClass().getName(), "changePassword", String.valueOf(false));
                return false;
            }

            // check if user is AD User,the password could be updated for only Non AD user. Larry
            if (isADUser(username, request)) {
                // put error message in request that indicate user only AD exists, so password could not be updated.
                // request.setAttribute("msg", "user is AD User , so password could not be updated.");
                response.sendRedirect(makeErrorURL(response, "User " + username + " is AD User, its password could not be updated.", origurl));
                l.exiting(getClass().getName(), "changePassword", String.valueOf(false));
                return false;

            }

            boolean changed = false;
            List<ObjectName> authenticationProviderList = getAuthenticationProviders();

            MBeanServer server = getMBeanServer();
            for (ObjectName authenticationProvider : authenticationProviderList) {
                try {
                    MBeanInfo mBeanInfo = null;
                    try {
                        mBeanInfo = server.getMBeanInfo(authenticationProvider);
                    } catch (Exception e) {
                        AppException ae = ExceptionHelper.getInstance().handleException("Failed to get MBeanInfo."
                                + authenticationProvider.getCanonicalName(), e);
                        throw ae;
                    }
                    if (DEFAULT_AUTHENTICATOR_CLASS.equals(mBeanInfo.getClassName())) {
                        boolean isUserExists = (Boolean) server.invoke(authenticationProvider, "userExists", new Object[]{username}, new String[]{"java.lang.String"});
                        if (isUserExists) {
                            //Change password
                            server.invoke(authenticationProvider, "changeUserPassword", new Object[]{username, currentpassword, newpassword},
                                    new String[]{"java.lang.String", "java.lang.String", "java.lang.String"});
                            l.logp(Level.INFO, getClass().getName(), "changePassword", "User password changed success");
                            changed = true;
                            break;
                        } else {
                            l.logp(Level.FINE, getClass().getName(), "changePassword", "User doesn't exist in:" + authenticationProvider.getCanonicalName());
                        }
                    }
                } catch (Exception e) {
                    changed = false;
                    if (e.getMessage().indexOf("[Security") >= 0) {
                        String msg = e.getMessage().substring(e.getMessage().indexOf("]") + 1);
                        l.logp(Level.SEVERE, this.getClass().getName(), "changePassword", "Failed to execute the invoke: " + msg, e);
                        throw new IllegalArgumentException(msg);
                    } else {
                        AppException ae = ExceptionHelper.getInstance().handleException("Failed to locate the User Information.", e);
                        l.logp(Level.SEVERE, this.getClass().getName(), "changePassword", "Failed to locate the User Information", e);
                        throw ae;
                    }
                }
            }
            if (!changed) {
                // This happens when the current user is not known to any providers
                // that implement UserPasswordEditorMBean
                response.sendRedirect(makeErrorURL(response,
                        "No password editors know about user " + username + '.', origurl));
                l.exiting(getClass().getName(), "changePassword", String.valueOf(false));
                return false;
            }
        } catch (IOException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to send a redirect through the HttpServletResponse", e);
            l.throwing(getClass().getName(), "changePassword", ae);
            throw ae;
        }

        HttpSession session = request.getSession();
        Authenticator.updatePasswordChanged((OasisUser) session.getAttribute(IOasisAction.KEY_OASISUSER), ActionHelper.getDbPoolId(request));
        l.exiting(getClass().getName(), "changePassword", String.valueOf(true));
        return true;
    }

    /**
     * get Weblogic runtime url
     *  @return
     */
   public String getRuntimeURL(){
       if (l.isLoggable(Level.FINER)) {
           l.entering(getClass().getName(), "getRuntimeURL");
       }

       String httpURL = "";
       UserSession userSession = null;
       try {
           userSession = UserSessionManager.getInstance().getUserSession();
       } catch (Exception ex) {
           //continue
       }

       if (userSession == null) {
           httpURL = getCurrentURL();
       } else {
           httpURL = (String)UserSessionManager.getInstance().getUserSession().get(UserSessionIds.SERVER_RUNTIME_HTTP_URL);
           if (StringUtils.isBlank(httpURL)) {
               setRuntimeURL();
               httpURL = (String)UserSessionManager.getInstance().getUserSession().get(UserSessionIds.SERVER_RUNTIME_HTTP_URL);
           }
       }

       l.exiting(getClass().getName(), "getRuntimeURL", httpURL);

       return httpURL;
   }

    /**
     * Obtain the weblogic runtime url and then storage it into UserSessionManager().
     *  @return
     */
   protected void setRuntimeURL(){
       if (l.isLoggable(Level.FINER)) {
           l.entering(getClass().getName(), "setRuntimeURL");
       }
       String httpURL = "";
       UserSession userSession = null;

       try {
           userSession = UserSessionManager.getInstance().getUserSession();
           if(userSession != null) {
               httpURL = getCurrentURL();
               UserSessionManager.getInstance().getUserSession().set(UserSessionIds.SERVER_RUNTIME_HTTP_URL, httpURL);
               if (l.isLoggable(Level.FINER)) {
                   l.exiting(getClass().getName(), "WebLogicSecurity.setRuntimeURL HTTP URL:", httpURL);
               }
           }
       } catch (Exception e) {
           //continue
       }
       l.exiting(getClass().getName(), "WebLogicSecurity.setRuntimeURL", httpURL);

   }
   public String getCurrentURL() {
       String httpURL = "";
       if (l.isLoggable(Level.FINER)) {
           l.entering(getClass().getName(), "getCurrentURL");
       }

       MBeanServer server = getMBeanServer();

       try {

           ObjectName runtimeService = new ObjectName("com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean");
           //get the runtime url
           ObjectName rt = (ObjectName) server.getAttribute(runtimeService, "ServerRuntime");
           Integer port = (Integer)server.getAttribute(rt, "ListenPort");
           String listenAddress = (String) server.getAttribute(rt, "ListenAddress");

           int index = listenAddress.indexOf("/");
           String hostName = listenAddress.substring(0,index);

           httpURL = "http://" + hostName + ":" + port;

       } catch (Exception e) {
           l.logp(Level.SEVERE, this.getClass().getName(), "WebLogicSecurity.getCurrentURL", "Failed to get protocol and port.", e);
           AppException ae = ExceptionHelper.getInstance().handleException("Failed to get the runtime MBeanServer", e);
           l.throwing(getClass().getName(), "WebLogicSecurity.getCurrentURL", ae);
           throw ae;
       }
       l.exiting(getClass().getName(), "WebLogicSecurity.getCurrentURL", httpURL);
       return httpURL;
   }
    private final Logger l = LogUtils.getLogger(getClass());
}
