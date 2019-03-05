package dti.oasis.util;

import dti.oasis.session.UserSessionManager;
import dti.oasis.session.UserSessionIds;
import dti.oasis.session.UserSession;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.struts.ActionHelper;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton Container for SysParm objects.  The objects
 * are stored in a HashMap, one entry per
 * Connection Pool Id
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 4, 2003
 * @author jbe
 */
 /*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 2/7/2004         jbe         Added Logging
 * 1/5/2005         jbe         Added getSubsytemInfo
 * 02/22/2007       sxm         Added get(String) and get(Request, String)
 * 09/25/2008       Larry       Issue 86826 DB connection leakage change
 * 04/08/2010       kshen       Added method isAvailable.
 * 01/21/2016       Parker      Issue#168627 Optimize the system parameter logic.
 * ---------------------------------------------------
 */

public class SysParmProvider implements IParmProvider, Serializable {
    private static final SysParmProvider INSTANCE = new SysParmProvider();
    private static final ArrayList<SysParmProviderRefreshListener> refreshListenerList = new ArrayList();
    private HashMap parms = new HashMap();
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Private Constructor
     */
    private SysParmProvider() {
    }

    /**
     * Register the listener to system provider. prepare the data for refresh.
     * @parm SysParmProviderRefreshListener
     * @return void
     */
    public void registerRefreshListener(SysParmProviderRefreshListener listener){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "registerRefreshListener", new Object[]{listener});
        }
        refreshListenerList.add(listener);
        l.exiting(getClass().getName(), "registerRefreshListener", listener);
    }

    /**
     * Singleton Instance getter
     * @return the instance
     */
    public static SysParmProvider getInstance() {
        return INSTANCE;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * get the SysParm from parms map. create a new one when don't contain
     * @parm dbPoolId
     * @return the SysParm
     */
    private SysParm getParmObj(String dbPoolId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getParmObj", new Object[]{dbPoolId});
        }
        SysParm parm = null;
        if (!parms.containsKey(dbPoolId)) {
            parm = new SysParm(dbPoolId);
            parms.put(dbPoolId, parm);
        } else
            parm = (SysParm) parms.get(dbPoolId);
        l.exiting(getClass().getName(), "getParmObj", parm);
        return parm;
    }

    /**
     *
     * @param subsystem
     * @return
     * @throws SQLException
     * @throws NamingException
     */
    public SubsystemInfo getSubsytemInfo(String subsystem) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSubsytemInfo", new Object[]{subsystem});
        }

        String dbPoolId = (String) UserSessionManager.getInstance().getUserSession().get(UserSessionIds.DB_POOL_ID);

        if (dbPoolId == null) {
            AppException ae = new AppException("Failed to get system parameter. The DB Pool is not set for user session.");
            l.throwing(getClass().getName(), "getSubsytemInfo", ae);
            throw ae;
        }

        SubsystemInfo info = null;
        try {
            info = getSubsytemInfo(dbPoolId, subsystem);
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get subsystem info: " + subsystem, e);
            l.throwing(getClass().getName(), "getSubsytemInfo", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSubsytemInfo", info);
        }
        return info;
    }

    /**
     * Gets information about an OASIS subsystem.
     *
     * @param subsystem e.g. CM, PM, FM, RM
     * @return A SubsystemInfo class
     * @throws SQLException
     */
    public SubsystemInfo getSubsytemInfo(String dbPoolId, String subsystem)
            throws SQLException, NamingException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSubsytemInfo", new Object[]{dbPoolId, subsystem});
        }

        SysParm parm = getParmObj(dbPoolId);
        SubsystemInfo info = parm.getSubSystemInfo(subsystem);
        l.exiting(getClass().getName(), "get", info);
        return info;
    }
    /**
     * Look up a single system parameter. Returns null if the parm is not found.
     * @param dbPoolId Database Pool Id
     * @param key parm name
     * @return parm value
     * @throws SQLException DB Problem
     * @throws NamingException Database Pool Id not found
     */
    public String get(String dbPoolId, String key) throws SQLException, NamingException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "get", new Object[]{dbPoolId, key});
        }
        SysParm parm = getParmObj(dbPoolId);
        String rc = parm.get(key);
        l.exiting(getClass().getName(), "get", rc);
        return rc;
    }

    /**
     * Looks up a set of system parameters.  A HashMap is returned containing the
     * System Parm values for each System Parm code.
     * If one of the parameters is not found, the entry will be null.
     * @param dbPoolId Database Pool Id
     * @param keys Array of System Parm codes
     * @return HashMap keys are sysparm codes, entries are values
     * @throws SQLException DB Problem
     * @throws NamingException Database Pool Id not found
     */
    public HashMap get(String dbPoolId, String[] keys) throws SQLException, NamingException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "get", new Object[]{dbPoolId, keys});
        }

        SysParm parm = getParmObj(dbPoolId);

        HashMap map = parm.get(keys);
        l.exiting(getClass().getName(), "get", map);
        return map;
    }

    /**
     * Refreshes the currently loaded list of system parameters
     * @param dbPoolId Database Pool Id
     * @throws SQLException DB Problem
     * @throws NamingException Database Pool Id not found
     */
    public void refresh(String dbPoolId) throws SQLException, NamingException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "refresh", new Object[]{dbPoolId});
        }

        SysParm parm = getParmObj(dbPoolId);
        parm.refresh();
        for (SysParmProviderRefreshListener refreshListener : refreshListenerList) {
            try {
                refreshListener.refresh();
            } catch (Exception e) {
                l.logp(Level.SEVERE, getClass().getName(), "refresh", "Failed to refresh the listener <" + refreshListener.getClass().getName() + ">", e);
            }
        }
        l.exiting(getClass().getName(), "refresh");
    }

    /**
     * Convenience method to look up a single system parameter and return
     * a default value if it is not found.
     * @param dbPoolId Database Pool Id
     * @param key parm name
     * @param dftValue default value to return if parm is not found
     * @return parm value
     * @throws SQLException DB Problem
     * @throws NamingException Database Pool Id not found
     */
    public String get(String dbPoolId, String key, String dftValue) throws SQLException, NamingException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "get", new Object[]{dbPoolId, key, dftValue});
        }

        String val = get(dbPoolId, key);
        val = (val == null) ? dftValue : val;
        l.exiting(getClass().getName(), "get", val);
        return val;
    }

    /**
     * Look up a single system parameter using DB Pool Id in User Session
     * @param key parm name
     * @return parm value
     */
    public String getSysParm(String key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSysParm", new Object[]{key});
        }

        String dbPoolId = (String) UserSessionManager.getInstance().getUserSession().get(UserSessionIds.DB_POOL_ID);
        String val = null;
        if (dbPoolId == null) {
            AppException ae = new AppException("Failed to get system parameter. The DB Pool is not set for user session.");
            l.throwing(getClass().getName(), "getSysParm", ae);
            throw ae;
        }
        try {
            val = get(dbPoolId, key);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get value for system parameter: " + key, e);
            l.throwing(getClass().getName(), "getSysParm", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getSysParm", val);
        return val;
    }

    /**
     * Look up a single system parameter and convert to integer using DB Pool Id in User Session
     * @param key parm name
     * @return parm value
     */
    public String getSysParm(String key, String defaultValue)  {
       String  val = getSysParm(key);
       val = (val == null) ? defaultValue : val;
       return val;
    }

    public int getSysParmAsInt(String key, int defaultValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSysParmAsInt", new Object[]{key, defaultValue});
        }

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSysParmAsInt", new Object[]{key, defaultValue});
        }

        String sysParmValue = null;
        int intValue = defaultValue;
        try {
            sysParmValue = getSysParm(key, Integer.toString(defaultValue));
            intValue = Integer.parseInt(sysParmValue);
        } catch (NumberFormatException e) {
            l.logp(Level.SEVERE, getClass().getName(), "getSysParmAsInt", "Failed to parse the system parameter "+key+" value <"+sysParmValue+"> as an Integer. Using default value <"+defaultValue+">.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSysParmAsInt", intValue);
        }
        return intValue;
    }

    /**
     * Look up a single system parameter using DB Pool Id in User Session
     * @param request HttpServletRequest
     * @param key parm name
     * @return parm value
     */
    public String getSysParm(HttpServletRequest request, String key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSysParm", new Object[]{request, key});
        }

        String dbPoolId = null;
        String val = null;

        UserSession us = UserSessionManager.getInstance().getUserSession(request);
        try {
            if (us.has(UserSessionIds.DB_POOL_ID)) {
               dbPoolId = (String) UserSessionManager.getInstance().getUserSession(request).get(UserSessionIds.DB_POOL_ID);
            }
            else {
              dbPoolId  = ActionHelper.getDbPoolId(request);
            }

        if (dbPoolId == null) {
            AppException ae = new AppException("Failed to get system parameter. The DB Pool is not set for user session.");
            l.throwing(getClass().getName(), "getSysParm", ae);
            throw ae;
        }
            val = get(dbPoolId, key);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get value for system parameter: " + key, e);
            l.throwing(getClass().getName(), "getSysParm", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getSysParm", val);
        return val;
    }

    public String getSysParm(HttpServletRequest request, String key, String defaultValue) {
        String val = getSysParm(request, key);
        val = (val == null) ? defaultValue : val;
        return val;
    }

    /**
     * Check if SysParmProvider is available.
     * If a user is not loged in to the system, the SysParmProvider would not be
     * available for the user.
     * @return
     */
    public boolean isAvailable() {
        return UserSessionManager.getInstance().isConfigured();
    }
}
