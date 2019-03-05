package dti.oasis.request.service;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.security.WebLogicSecurity;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionIds;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.MaxSizeHashMap;
import dti.oasis.util.StringUtils;
import weblogic.management.MBeanHome;
import weblogic.management.runtime.ServerRuntimeMBean;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   1/4/15
 *
 * @author jyang2
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/15/2015       cv          163222 - buildWebServicePath was created to be used
 *                                       to compose Web service url.
 *                                     - static string were created:
 *                                       ePOLICY_PM, ePOLICY_wsPOLICY, eCLAIM, eFM_wsFM, eFM
 * 02/01/2017       cv          182615 - Modified buildWebServicePath() to get the ServerRuntime Listening address and port.
 * 04/25/2017       cv          184692 - Modified buildWebServicePath() - call WebLogicSecurity.getInstance().getRuntimeURL() if
 *                                       httpURL is empty.
 * ---------------------------------------------------
 */
public class WebServiceClientHelper {
    public static final String ePOLICY_PM = "ePolicy/PM";
    public static final String ePOLICY_wsPOLICY = "ePolicy/wsPolicy";
    public static final String eCLAIM_CM = "eClaim/CM";
    public static final String eFM_wsFM = "eFM/wsFM";
    public static final String eFM_FM = "eFM/FM";

    public static WebServiceClientHelper getInstance() {
        return INSTANCE;
    }

    private WebServiceClientHelper() {
    }

    private void setOWSUserNameAndPassword(String userId){
        Logger l = LogUtils.enterLog(this.getClass(), "setOWSUserNameAndPassword", new Object[]{userId});

        String currentUserRoleName = null;
        UserSession userSession = UserSessionManager.getInstance().getUserSession();
        if (userSession.has(IOasisAction.KEY_DBPOOLIDROLENAME))
            currentUserRoleName = userSession.get(IOasisAction.KEY_DBPOOLIDROLENAME).toString();
        if (!StringUtils.isBlank(currentUserRoleName) && ApplicationContext.getInstance()
            .hasProperty(currentUserRoleName + "." + DTI_OWS_USER_NAME) &&
            ApplicationContext.getInstance().hasProperty(currentUserRoleName + "." + DTI_OWS_USER_PASSWORD)) {
            cacheWSUsers.put(userId + "UserId", ApplicationContext.getInstance().getProperty(currentUserRoleName
                + "." + DTI_OWS_USER_NAME));
            cacheWSUsers.put(userId + "Password", ApplicationContext.getInstance().getProperty(currentUserRoleName
                + "." + DTI_OWS_USER_PASSWORD));
        }
        else if (ApplicationContext.getInstance().hasProperty(DTI_OWS_USER_NAME) &&
            ApplicationContext.getInstance().hasProperty(DTI_OWS_USER_PASSWORD)) {
            cacheWSUsers.put(userId + "UserId", ApplicationContext.getInstance().getProperty(DTI_OWS_USER_NAME));
            cacheWSUsers.put(userId + "Password", ApplicationContext.getInstance().getProperty(DTI_OWS_USER_PASSWORD));
        }
        else{
            ConfigurationException ce = new ConfigurationException("Default or role " + currentUserRoleName +
                " based ows configuration is required for userId: " + userId);
            l.throwing(getClass().getName(), "getConfiguredWSUserName", ce);
            throw ce;
        }

        l.exiting(getClass().toString(),"setOWSUserNameAndPassword");
    }

    public String getOWSUserName() {
        Logger l = LogUtils.enterLog(this.getClass(), "getOWSUserName");

        String userId = UserSessionManager.getInstance().getUserSession().getUserId();
        if (!cacheWSUsers.containsKey(userId + "UserId") || !cacheWSUsers.containsKey(userId + "Password")) {
            setOWSUserNameAndPassword(userId);
        }        
        
        l.exiting(getClass().toString(),"getOWSUserName");
        return cacheWSUsers.get(userId + "UserId");
    }

    public String getOWSPassword() {
        Logger l = LogUtils.enterLog(this.getClass(), "getOWSPassword");

        String userId = UserSessionManager.getInstance().getUserSession().getUserId();
        if (!cacheWSUsers.containsKey(userId + "UserId") || !cacheWSUsers.containsKey(userId + "Password")) {
            setOWSUserNameAndPassword(userId);
        }

        l.exiting(getClass().toString(),"getOWSUserName");
        return cacheWSUsers.get(userId + "Password");
    }

    public String buildWebServicePath(String subSystemName, String wsPath) {
        Logger l = LogUtils.enterLog(getClass(), "buildWebServicePath");

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);

        String modulPath = request.getContextPath();
        String path = modulPath.replaceAll(subSystemName, wsPath);

        String httpURL = WebLogicSecurity.getInstance().getRuntimeURL();

        String webServicePath = httpURL + path;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildWebServicePath - webServicePath: ", webServicePath);
            l.exiting(getClass().getName(), "buildWebServicePath HTTP URL:", httpURL);
        }

        l.exiting(getClass().getName(), "buildWebServicePath");
        return webServicePath;
    }


    private static final WebServiceClientHelper INSTANCE = new WebServiceClientHelper();
    private Map<String, String> cacheWSUsers = new MaxSizeHashMap(Integer.valueOf(ApplicationContext.getInstance().
        getProperty(MAX_CACHE_WS_USER_SIZE, "20")));
    public static final String MAX_CACHE_WS_USER_SIZE = "pm.cache.ows.user.size";
    public static final String DTI_OWS_USER_NAME = "dti.ows.username";
    public static final String DTI_OWS_USER_PASSWORD = "dti.ows.password";
}
