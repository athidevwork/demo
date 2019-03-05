package dti.oasis.cachemgr;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.RefreshParmsEventListener;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements an user cache.
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 30, 2013
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/16/13 fcb     150767 - refactored to use RefreshParmsEventListener.
 * ---------------------------------------------------
 */
public class UserCacheManager implements RefreshParmsEventListener {
    public static final String BEAN_NAME = "UserCacheManager";
    public static final String USER_PROFILES_CACHE_KEY = "userProfilesCacheKey";
    public static final String POLICY_OASIS_FIELDS_CACHE = "policyOasisFieldsCache";

    /**
     * Returns a synchronized static instance of User Cache Manager that contains user configurations.
     * @return UserCacheManager, an instance of User Cache Manager.
     */
    public synchronized static final UserCacheManager getInstance() {
        if (UserCacheManager.c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(UserCacheManager.BEAN_NAME)) {
                UserCacheManager.c_instance = (UserCacheManager) ApplicationContext.getInstance().getBean(UserCacheManager.BEAN_NAME);
            }
            else{
                UserCacheManager.c_instance = new UserCacheManager();
            }
        }
        return UserCacheManager.c_instance;
    }

    /**
     * Returns true if the specified value exists in the cache for the current user.
     * @param key
     * @return boolean
     */
    public synchronized boolean has(String key) {
        Logger l = LogUtils.enterLog(getClass(), "has", new Object[]{key});

        String userId = UserSessionManager.getInstance().getUserSession().getUserId();
        boolean valueExists = true;

        Map userMap =  getUserMap(userId);
        if (userMap == null || !userMap.containsKey(key)) {
            valueExists = false;
        }

        l.exiting(getClass().getName(), "has [userId=" + userId + "]", String.valueOf(valueExists));
        
        return valueExists;
    }

    /**
     * Returns the value specified by the given key for the current user.
     *
     * @param key
     * @return Object
     * @throws AppException if the key is not found
     */
    public synchronized Object get(String key) {
        Logger l = LogUtils.enterLog(getClass(), "get", new Object[]{key});

        String userId = UserSessionManager.getInstance().getUserSession().getUserId();

        Object object = getUserMap(userId).get(key);
        if (object == null) {
            AppException e = new AppException("The "+BEAN_NAME+" for userId'"+userId+"' does not contain a value for the key'"+key+"'.");
            l.throwing(getClass().getName(), "get", e);
        }

        l.exiting(getClass().getName(), "get [userId=" + userId + "]", object);

        return object;
    }

    /**
     * Returns the previous value associated with key, or null if there was no mapping for key (as in ConcurrentHashMap) for the current user.
     * @param key
     * @return Object
     */
    public synchronized Object remove(String key) {
        Logger l = LogUtils.enterLog(getClass(), "remove", new Object[]{key});

        String userId = UserSessionManager.getInstance().getUserSession().getUserId();

        Object removed = null;
        if(key!=null){
            ConcurrentHashMap userMap = getUserMap(userId);
            if(userMap.containsKey(key)) {
                removed = getUserMap(userId).remove(key);
            } else {
                if (l.isLoggable(Level.FINER)) {
                    l.logp(Level.FINER, getClass().getName(), "remove", "Key " + key + " deosn't exist in userMap in bean "+BEAN_NAME+" for userId="+userId);
                }
            }
        } else {
            l.logp(Level.FINER, getClass().getName(), "remove", "key " + key + " in bean "+BEAN_NAME+" IS NULL");
        }

        if (l.isLoggable(Level.FINER)) {
            if(removed!=null)
                l.logp(Level.FINER, getClass().getName(), "remove", "Removed Object with key " + key + " from bean "+BEAN_NAME+" for userId="+userId);
            else
                l.logp(Level.FINER, getClass().getName(), "remove", "Can't remove Object with key " + key + " from bean "+BEAN_NAME+" for userId="+userId);
        }


        l.exiting(getClass().getName(), "remove [userId=" + userId + ": key"+key+"]",removed);

        return removed;
    }

    /**
     * Returns cache for the current user, or creates one, if it doesn't exist.
     * @param userId
     * @return Object
     */
    public synchronized ConcurrentHashMap getUserMap(String userId){
        Logger l = LogUtils.enterLog(getClass(), "getUserMap", new Object[]{userId});
        ConcurrentHashMap userMap;

        if (!getUserCacheMap().containsKey(userId)) {

            l.logp(Level.FINER, getClass().getName(), "getUserMap", "UserCacheManager for "+userId+" doesn't exist. Creating...");

            userMap = getUserCacheMap().put(userId, new ConcurrentHashMap());

        }

        userMap = getUserCacheMap().get(userId);

        l.exiting(getClass().getName(), "getUserMap [userId=" + userId + "]");

        return userMap;
    }

    /**
     * Clears cache for the current user.
     * @param userId
     */
    public synchronized void clearUserCacheForUser(String userId){
        Logger l = LogUtils.enterLog(getClass(), "clearUserCacheForUser",new Object[]{userId});

        Object result = getUserCacheMap().remove(userId);

        if (l.isLoggable(Level.FINER)) {
            if(result!=null){
                l.logp(Level.FINER, getClass().getName(), "clearUserCacheForUser", " Cleared UserCacheManager for "+userId);
            } else {
                l.logp(Level.FINER, getClass().getName(), "clearUserCacheForUser", " CAN'T Clear UserCacheManager for "+userId);
            }
        }

        l.exiting(getClass().getName(), "clearUserCacheForUser [userId=" + userId + "]",result);

    }

    /**
     * Clears entire User Cache for the current user.
     */
    public synchronized List<String> clearUserCache(){
        Logger l = LogUtils.enterLog(getClass(), "clearUserCache");

        getUserCacheMap().clear();

        List<String>cacheTypes = new ArrayList<String>(m_userCacheTypes);

        getUserCacheTypes().clear();

        l.exiting(getClass().getName(), "clearUserCache",cacheTypes);

        return cacheTypes;
    }

    /**
     * Sets cache for the current user.
     *
     * @param key
     * @param value
     */
    public synchronized void set(String key, Object value) {
        Logger l = LogUtils.enterLog(getClass(), "set", new Object[]{key, value});

        String userId = UserSessionManager.getInstance().getUserSession().getUserId();

        getUserMap(userId).put(key, value);

        setCacheType(key);

        l.exiting(getClass().getName(), "set [userId=" + userId + "]");
    }

    /**
     * Sets cache type
     *
     * @param key
     */
    protected void setCacheType(String key){
        Logger l = LogUtils.enterLog(getClass(), "setCacheType", new Object[]{key});

        String value = m_userCacheTypesMap.get(key);
        l.logp(Level.FINER, getClass().getName(), "setCacheType","ADDING "+value);

        if(!StringUtils.isBlank(value))
            m_userCacheTypes.add(value);

        l.exiting(getClass().getName(), "setCacheType");
    }

    /**
     * Return the user cache map.
     */
    protected ConcurrentHashMap<String,ConcurrentHashMap> getUserCacheMap() {
        return m_userCacheMap;
    }

    protected Set<String> getUserCacheTypes() {
        return m_userCacheTypes;
    }

    /**
     * Implements the refresh parameters listener.
     * @param request
     */
    public void refreshParms(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "refreshParms");
        MessageManager messageManager = MessageManager.getInstance();
        try {
            List<String> messages = clearUserCache();

            for (String messageKey : messages) {
                messageManager.addInfoMessage(messageKey);
            }
            l.logp(Level.INFO, getClass().getName(), "refreshParms", "User Cache has been refreshed!");
        } catch (Exception e) {
            l.logp(Level.SEVERE, getClass().getName(), "refreshParms", "Failed to refresh the User Cache", e);
            messageManager.addErrorMessage("core.refresh.usercache.fail");
        }
        l.exiting(getClass().getName(), "refreshParms");
    }

    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        String lineSep = System.getProperty("line.separator");
        buf.append(lineSep+this.getClass().getName()+lineSep);
        for (Map.Entry<String, ConcurrentHashMap> entry : getUserCacheMap().entrySet()) {
            String key = entry.getKey();
            ConcurrentHashMap value = entry.getValue();
            if(value instanceof Map){
                buf.append("Child of "+key+lineSep);
                Map<String,Object> childMap = value;
                for (Map.Entry<String,Object> entry1 : childMap.entrySet()) {
                    String childKey = entry1.getKey();
                    Object childValue = entry1.getValue();
                    buf.append("Child Key: "+childKey+" Child Value: "+childValue+lineSep);
                }
                buf.append("End: Child of "+key+lineSep);
            } else {
                buf.append("Key: "+key+" Value: "+value+lineSep);
            }
        }

        buf.append("End: "+this.getClass().getName()+lineSep);
        return buf.toString();
    }

    private ConcurrentHashMap<String,ConcurrentHashMap> m_userCacheMap = new ConcurrentHashMap<String,ConcurrentHashMap>();
    private Set<String> m_userCacheTypes = new HashSet<String>();
    public static final Map<String, String> m_userCacheTypesMap = new HashMap<String, String>();
    static {
        m_userCacheTypesMap.put(POLICY_OASIS_FIELDS_CACHE,"core.refresh.usercache.policyoasisfields");
    }

    private static UserCacheManager c_instance;

}
