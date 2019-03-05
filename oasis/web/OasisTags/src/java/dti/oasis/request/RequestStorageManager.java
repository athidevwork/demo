package dti.oasis.request;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ApplicationLifecycleListener;
import dti.oasis.session.UserSession;
import dti.oasis.session.impl.HttpUserSession;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.OasisUser;
import org.apache.commons.collections.map.HashedMap;
import org.apache.struts.Globals;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Maintains access to key/value pairs, within the scope of the current request. The current request is determined
 * based on the Thread. This class assumes that all execution for a given request occurs in a single thread.
 * <p/>
 * If a new Thread is created, or a MDB is used to handle a request on a new Thread,
 * call the getStorageMap() method to get the current map of key/value pairs,
 * and call setStorageMap() in the new Thread to setup the new Thread with the key/value pairs from the calling Thread.
 * <p/>
 * The specific implementation of this class can be overriden by specifying an extension in the Application Context
 * using the bean name specified by the BEAN_NAME constant defined for this class.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author wreeder
 * @see #BEAN_NAME
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/28/2008       fcb         setupForRequest() and getCopy() added.
 * ---------------------------------------------------
 */
public class RequestStorageManager implements ApplicationLifecycleListener {

    /** The bean name of a RequestStorageManager extension if it is configured in the ApplicationContext. */
    public static final String BEAN_NAME = "RequestStorageManager";

    /**
     * Return an instance of the RequestStorageManager.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public synchronized static final RequestStorageManager getInstance() {
        c_l.entering(RequestStorageManager.class.getName(), "getInstance");
        if (c_l.isLoggable(Level.FINEST))
            c_l.logp(Level.FINEST,RequestStorageManager.class.getName(), "getInstance", "Getting RequestStorageManager instance. ThreadId[" + Thread.currentThread().getId() + "]");
        if (c_instance == null) {

            if (ApplicationContext.getInstance()._hasBean(BEAN_NAME)) {
                if (c_l.isLoggable(Level.FINEST)) {
                    c_l.logp(Level.FINEST,RequestStorageManager.class.getName(), "getInstance", "Getting RequestStorageManager from ApplicationContext");
                }
                c_instance = (RequestStorageManager) ApplicationContext.getInstance()._getBean(BEAN_NAME);
                if(c_instance.getId()==null) {
                    if (c_l.isLoggable(Level.FINEST))
                        c_l.logp(Level.FINEST,RequestStorageManager.class.getName(), "getInstance", "Setting ID................");
                    c_instance.setId(System.identityHashCode(c_instance));
                }
                if (c_l.isLoggable(Level.FINEST))
                    c_l.logp(Level.FINEST,RequestStorageManager.class.getName(), "getInstance", "GOT RequestStorageManager from ApplicationContext. RequestStorageManager ID: "+c_instance.getId());
            }
            else{
                if (c_l.isLoggable(Level.FINEST)) {
                    c_l.logp(Level.FINEST, RequestStorageManager.class.getName(), "getInstance", "Creating new RequestStorageManager instance");
                }
                c_instance = new RequestStorageManager();
            }
        }

        if (c_l.isLoggable(Level.FINEST)) {
            c_l.logp(Level.FINEST, RequestStorageManager.class.getName(), "getInstance", "GOT RequestStorageManager instance ThreadId[" + Thread.currentThread().getId() + "] RequestStorageManager ID: " + c_instance.getId());
        }

        if (c_l.isLoggable(Level.FINER))
            c_l.exiting(RequestLifecycleAdvisor.class.getName(), "getInstance", c_instance);
        return c_instance;
    }

    /**
     * Returns true if the specified value exists, within the scope of the current request.
     *
     * @param key
     */
    public boolean has(String key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "has", new Object[]{key});
        }

        boolean valueExists = _has(key);

        l.exiting(getClass().getName(), "has", String.valueOf(valueExists));

        return valueExists;
    }

    /**
     * Returns true if the specified value exists, within the scope of the current request.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public boolean _has(String key) {
        Object value = getRequestStorageMap().get(key);
        boolean valueExists = value != null;
        return valueExists;
    }

    /**
     * Returns the value specified by the given key, within the scope of the current request.
     *
     * @param key
     * @throws IllegalArgumentException if the key is not found
     */
    public Object get(String key) {
        return get(key, null);
    }

    /**
     * Returns the value specified by the given key, within the scope of the current request.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public Object _get(String key) {
        return _get(key, null);
    }

    /**
     * Returns the value specified by the given key, within the scope of the current request.
     *
     * @param key
     * @param defaultValue The default value to return if the key is not found.
     */
    public Object get(String key, Object defaultValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "get", new Object[]{key, defaultValue});
        }

        Map rsm = getRequestStorageMap();
        Object value = rsm.get(key);

        if (value == null && defaultValue != null) {
            value = defaultValue;
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "get", "The value for key <" + key + "> was not found; returning the defaultValue <" + defaultValue + ">");
            }
        }
        
        if (value == null) {
            IllegalArgumentException e = new IllegalArgumentException("The value for key '" + key + "' was not found in the RequestStorageManager.");
            l.throwing(getClass().getName(), "remove", e);
            throw e;
        }
        if (l.isLoggable(Level.FINER))
            l.exiting(getClass().getName(), "get", value);

        return value;
    }

    /**
     * Returns the value specified by the given key, within the scope of the current request.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public Object _get(String key, Object defaultValue) {

        Map rsm = getRequestStorageMap();
        Object value = rsm.get(key);

        if (value == null && defaultValue != null) {
            value = defaultValue;
        }

        if (value == null) {
            IllegalArgumentException e = new IllegalArgumentException("The value for key '" + key + "' was not found in the RequestStorageManager.");
            throw e;
        }

        return value;
    }

    /**
     * Sets the key/value pair, within the scope of the current request.
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        set(key, value, false);
    }

    /**
     * Sets the key/value pair, within the scope of the current request.
     *
     * @param key
     * @param skipWhenCopy skip this value when copying the RequestStorage using the getCopy method
     * @param value
     * @see RequestStorageManager#getCopy
     */
    public void set(String key, Object value, boolean skipWhenCopy) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "set", new Object[]{key, value, skipWhenCopy});
        }

        getRequestStorageMap().put(key, value);

        if (skipWhenCopy) {
            m_skipEntries.put(key, value);
        }

        l.exiting(getClass().getName(), "set");
    }

    /**
     * Remove the value for the specified key, within the scope of the current request.
     *
     * @param key
     * @return the value that was removed.
     * @throws IllegalArgumentException if the value was not found.
     */
    public Object remove(String key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "remove", new Object[]{key});
        }

        Map rsm = getRequestStorageMap();
        Object value = rsm.remove(key);
        if (value == null) {
            IllegalArgumentException e = new IllegalArgumentException("The value for key '" + key + "' was not found in the RequestStorageManager.");
            l.throwing(getClass().getName(), "remove", e);
            throw e;
        }

        if (l.isLoggable(Level.FINER))
            l.exiting(getClass().getName(), "remove", value);

        return value;
    }

    /**
     * Clear all key/value pairs for the current request scope.
     */
    public void clear() {
        l.entering(getClass().getName(), "clear");
        if (l.isLoggable(Level.FINE))
            l.logp(Level.FINE, getClass().getName(), "clearNoArgs", "threadId[" + Thread.currentThread().getId() + "]. Calling clear()");
        // Remove the reference to the Map for this Thread so it can be garbage collected.
        // Don't clear the map anymore because when the JobQueueManager needs to cleanup a Job,
        //      it needs to use the request session map from the JobRequest that was already terminated by the JobProcessor
        Map map = (Map) m_storagePerThreadMap.get(Thread.currentThread());
        if (map != null) {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "clearNoArgs", "threadId[" + Thread.currentThread().getId() + "]. Found storagePerThreadMap");
            Object o = m_storagePerThreadMap.remove(Thread.currentThread());
            if(o != null) {
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "clearNoArgs", "threadId[" + Thread.currentThread().getId() + "]. Cleared storagePerThreadMap");
            } else {
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "clearNoArgs", "threadId[" + Thread.currentThread().getId() + "]. Failed to clear storagePerThreadMap");
            }
        } else {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "clearNoArgs", "threadId[" + Thread.currentThread().getId()+"]. Did not find storagePerThreadMap");
        }
        if (l.isLoggable(Level.FINER))
            l.exiting(getClass().getName(), "clear");
    }

    /**
     * Clear all key/value pairs for a specific Thread.
     *  @param thread
     */
    private void clear(Thread thread) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "clear", new Object[]{thread});
        }
        if (l.isLoggable(Level.FINE))
            l.logp(Level.FINE, getClass().getName(), "clear", "Thread [ID: "+thread.getId()+"]. Calling clear() for Thread: " + thread.getId());

        Map map = (Map) m_storagePerThreadMap.get(thread);
        if (map != null) {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "clear", "Thread [ID: "+thread.getId()+"]. Found storagePerThreadMap for Thread: " + thread.getId());
            Object o = m_storagePerThreadMap.remove(thread);
            if(o != null) {
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "clear", "Thread [ID: " + thread.getId() + "]. Cleared storagePerThreadMap for Thread: " + thread.getId());
            } else {
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "clear", "Thread [ID: " + thread.getId() + "]. Failed to clear storagePerThreadMap for Thread: " + thread.getId());
            }
        } else {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "clear", "Thread [ID: "+thread.getId()+"]. Did not find storagePerThreadMap for Thread: " + thread.getId());
        }

        l.exiting(getClass().getName(), "clear");
    }

    public Boolean isHttpSessionValid() {
        l.entering(getClass().getName(), "isHttpSessionValid");

        Boolean isValid = true;
        if (l.isLoggable(Level.FINE))
            l.logp(Level.FINE, getClass().getName(), "isHttpSessionValid", "threadId[" + Thread.currentThread().getId() + "]");

        UserSession userSession = null;
        if (has(USER_SESSION)) {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "isHttpSessionValid", "threadId[" + Thread.currentThread().getId() + "] has UserSession");
            userSession = (UserSession) get(USER_SESSION);
            if(userSession!=null) {
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "isHttpSessionValid", "threadId[" + Thread.currentThread().getId() + "]. UserSession IS NOT NULL");
                try {
                    if (userSession instanceof HttpUserSession) {
                        if (l.isLoggable(Level.FINE))
                            l.logp(Level.FINE, getClass().getName(), "isHttpSessionValid", "threadId[" + Thread.currentThread().getId() + "] UserSession is HttpUserSession");
                        HttpUserSession httpUserSession = (HttpUserSession) userSession;
                        if (l.isLoggable(Level.FINE))
                            l.logp(Level.FINE, getClass().getName(), "isHttpSessionValid", "threadId[" + Thread.currentThread().getId() + "] HttpUserSession: " + httpUserSession);
                        if (httpUserSession.has(Globals.TRANSACTION_TOKEN_KEY)) {
                            if (l.isLoggable(Level.FINE))
                                l.logp(Level.FINE, getClass().getName(), "isHttpSessionValid", "threadId[" + Thread.currentThread().getId() + "] httpUserSession.has: " + httpUserSession.get(Globals.TRANSACTION_TOKEN_KEY));
                        }

                    } else {
                        if (l.isLoggable(Level.FINE))
                            l.logp(Level.FINE, getClass().getName(), "isHttpSessionValid", "threadId[" + Thread.currentThread().getId() + "] UserSession is NOT HttpUserSession");
                    }
                } catch (IllegalStateException ex) {
                    l.logp(Level.WARNING, getClass().getName(), "isHttpSessionValid", "threadId[" + Thread.currentThread().getId() + "] Caught IllegalStateException: UserSession " + userSession.getSessionId() + " Contains HttpSession that IS Invalid: " + userSession);
                    isValid = false;
                    ex.printStackTrace();
                }
            } else {
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "isHttpSessionValid", "threadId[" + Thread.currentThread().getId() + "]. UserSession IS NULL");
            }
        } else {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "isHttpSessionValid", "threadId[" + Thread.currentThread().getId()+"]. Did not find storagePerThreadMap");
        }

        if (l.isLoggable(Level.FINER))
            l.exiting(getClass().getName(), "isHttpSessionValid");

        return isValid;
    }

//    @Deprecated
    public Boolean isUserMatch(String remoteUser){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isUserMatch", new Object[]{remoteUser});
        }

        Boolean result = true;
        if (l.isLoggable(Level.FINE))
            l.logp(Level.FINE, getClass().getName(), "isUserMatch", "threadId[" + Thread.currentThread().getId() + "]");

        UserSession userSession = null;
        if (has(USER_SESSION)) {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "isUserMatch", "threadId[" + Thread.currentThread().getId() + "] has UserSession");
            userSession = (UserSession) get(USER_SESSION);
            if (userSession != null) {
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "isUserMatch", "threadId[" + Thread.currentThread().getId() + "]. UserSession IS NOT NULL");
                String userId = userSession.getUserId();
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "isUserMatch", "threadId[" + Thread.currentThread().getId() + "]. userId: "+userId);
                OasisUser oasisUser = userSession.getOasisUser();
                if(oasisUser!=null){
                    String oasisUserId = oasisUser.getUserId();
                    if (l.isLoggable(Level.FINE))
                        l.logp(Level.FINE, getClass().getName(), "isUserMatch", "threadId[" + Thread.currentThread().getId() + "]. oasisUserId: "+oasisUserId);
                } else {
                    if (l.isLoggable(Level.FINE))
                        l.logp(Level.FINE, getClass().getName(), "isUserMatch", "threadId[" + Thread.currentThread().getId() + "]. OasisUser IS NULL");
                }

                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "isUserMatch", "threadId[" + Thread.currentThread().getId() + "]. remoteUser: "+remoteUser);
            } else {
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "isUserMatch", "threadId[" + Thread.currentThread().getId() + "]. UserSession IS NULL");
            }
        } else {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "isUserMatch", "threadId[" + Thread.currentThread().getId()+"]. Did not find "+USER_SESSION+" key in RSM: "+this);
        }

        if (l.isLoggable(Level.FINER))
            l.exiting(getClass().getName(), "isUserMatch");

        return result;
    }

    /**
     * Determine if the RequestStorageManager has been properly setup for the current request.
     * @return true if the RequestStorageManager is setup for the current request; otherwise, false.
     */
    public boolean isSetupForRequest() {
        l.entering(getClass().getName(), "isSetupForRequest");

        boolean setupForRequest = false;
        if (has(SETUP_FOR_REQUEST_INDICATOR)) {
            setupForRequest = ((Boolean)get(SETUP_FOR_REQUEST_INDICATOR)).booleanValue();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSetupForRequest", Boolean.valueOf(setupForRequest));
        }
        return setupForRequest;
    }

    /**
     * Setup the RequestStorageManager for a new request.
     */
    public void setupForRequest() {
        l.entering(getClass().getName(), "setupForRequest");

        clear();
        set(SETUP_FOR_REQUEST_INDICATOR, Boolean.TRUE);

        l.exiting(getClass().getName(), "setupForRequest");
    }

    /**
     * Setup the RequestStorageManager for a new request.
     */
    public void setupForRequest(Map map) {
        l.entering(getClass().getName(), "setupForRequest");
        clear();
        m_storagePerThreadMap.put(Thread.currentThread(), map);
        set(SETUP_FOR_REQUEST_INDICATOR, Boolean.TRUE);

        l.exiting(getClass().getName(), "setupForRequest");
    }

    /**
     * Cleanup the RequestStorageManager for this request.
     */
    public void cleanupFromRequest() {
        l.entering(getClass().getName(), "cleanupFromRequest");

        clear();
        set(SETUP_FOR_REQUEST_INDICATOR, Boolean.FALSE);

        l.exiting(getClass().getName(), "cleanupFromRequest");
    }

    /**
     * Return the Map for the current request scope.
     */
    protected Map getRequestStorageMap() {
        Map map = (Map) m_storagePerThreadMap.get(Thread.currentThread());
        if (map == null) {
            map = new HashMap();
            m_storagePerThreadMap.put(Thread.currentThread(), map);
        }
        return map;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    /**
     * This method does not log any messages so it can be used by the Logger
     */
    public RequestStorageManager() {
        l.entering(getClass().getName(), "RequestStorageManager");
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, RequestStorageManager.class.getName(), "RequestStorageManager", "*******************************************");
            l.logp(Level.FINE, RequestStorageManager.class.getName(), "RequestStorageManager", "DEFAULT RequestStorageManager() CONSTRUCTOR...ThreadId[" + Thread.currentThread().getId() + "]");
            l.logp(Level.FINE, RequestStorageManager.class.getName(), "RequestStorageManager", "*******************************************");
        }
        this.setId(System.identityHashCode(this));
        if (l.isLoggable(Level.FINE))
            l.logp(Level.FINE,RequestStorageManager.class.getName(), "RequestStorageManager", "DEFAULT RequestStorageManager() CONSTRUCTOR - SET RequestStorageManager ID: "+this.getId());
        if (l.isLoggable(Level.FINE))
            l.logp(Level.FINE,RequestStorageManager.class.getName(), "RequestStorageManager", "DEFAULT RequestStorageManager() CONSTRUCTOR. ThreadId[" + Thread.currentThread().getId() + "] RequestStorageManager ID: "+this.getId());
    }

    //-------------------------------------------------
    // Initialization / Termination logic
    //-------------------------------------------------

    /**
     * Initialize the bean.
     */
    public void initialize() {
    }

    /**
     * Cleanup open resources and sizeable caches.
     */
    public void terminate() {
        m_storagePerThreadMap.clear();
    }

    /**
     * Returns a copy of the request storage
     */
    public Map getCopy() {
        HashMap requestStorageMap = (HashMap) m_storagePerThreadMap.get(Thread.currentThread());
        Map clonedMap = (Map)requestStorageMap.clone();
        if (m_skipEntries.size() > 0) {
            Iterator iter = m_skipEntries.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                clonedMap.remove(key);
            }
        }
        return clonedMap;
    }

    @Override
    public String toString() {
        HashMap requestStorageMap = (HashMap) m_storagePerThreadMap.get(Thread.currentThread());
        Map clonedMap = new HashedMap();
        if(requestStorageMap!=null)
        clonedMap = (Map)requestStorageMap.clone();
//        Map skipEntriesClone = (Map)m_skipEntries.clone();
        return "RequestStorageManager{" +
                "RequestStorageManager ID: "+id+"\n"+
//                "m_skipEntries=" + printMap(skipEntriesClone) +
                ", m_storagePerThreadMap=["+Thread.currentThread()+"]: \n" + printMap(clonedMap) +
                '}';
    }

    private String printMap(Map map){
        String mapStr = "STARTING MAP:"+ "\n";
        if (map.size() > 0) {
            Iterator iter = map.keySet().iterator();
            while (iter.hasNext()) {
                Object key = iter.next();
                Object value = map.get(key);
                if(value instanceof Map)
                    printMap((Map)value);
                else
                    mapStr += "key: "+key+" value: "+value+"\n";
            }
        }

        return mapStr;
    }

    /**
     *  Displays Expired UserSession objects in ALL Threads
     */
    public void displayAllUserSessions(){
        l.entering(getClass().getName(), "displayAllUserSessions");
        Set<Map.Entry<Thread, HashMap>> entrySet= m_storagePerThreadMap.entrySet();
        Iterator<Map.Entry<Thread, HashMap>> entrySetIterator = entrySet.iterator();
        while (entrySetIterator.hasNext()) {
            Map.Entry<Thread, HashMap> entry = entrySetIterator.next();
            Thread thread = entry.getKey();
            HashMap requestStorageMap = entry.getValue();
            if(requestStorageMap!=null && requestStorageMap.size()>0) {
                if (requestStorageMap != null) {
                    if (requestStorageMap.containsKey("userSession")) {
                        UserSession userSession = (UserSession)requestStorageMap.get("userSession");
                        if (l.isLoggable(Level.FINE))
                            l.logp(Level.FINE, getClass().getName(), "displayAllUserSessions", "UserSession for requestStorageMap for Thread [ID: "+thread.getId()+"] [Name: "+thread.getName()+"]\n "+userSession);

                        try {
                            if (userSession instanceof HttpUserSession) {
                                if (l.isLoggable(Level.FINE))
                                    l.logp(Level.FINE, getClass().getName(), "displayAllUserSessions", "Thread [ID: "+thread.getId()+"] UserSession is HttpUserSession");
                                HttpUserSession httpUserSession = (HttpUserSession) userSession;
                                if (l.isLoggable(Level.FINE))
                                    l.logp(Level.FINE, getClass().getName(), "displayAllUserSessions", "Thread [ID: "+thread.getId()+"] HttpUserSession: " + httpUserSession);
                                if (httpUserSession.has(Globals.TRANSACTION_TOKEN_KEY)) {
                                    if (l.isLoggable(Level.FINE))
                                        l.logp(Level.FINE, getClass().getName(), "displayAllUserSessions", "Thread [ID: "+thread.getId()+"] httpUserSession.has: " + httpUserSession.get(Globals.TRANSACTION_TOKEN_KEY));
                                }

                            } else {
                                if (l.isLoggable(Level.FINE))
                                    l.logp(Level.FINE, getClass().getName(), "displayAllUserSessions", "Thread [ID: "+thread.getId()+"] UserSession is NOT HttpUserSession");
                            }
                        } catch (IllegalStateException ex) {
                            l.logp(Level.WARNING, getClass().getName(), "displayAllUserSessions", "Thread [ID: "+thread.getId()+"] Caught IllegalStateException: UserSession " + userSession.getSessionId() + " Contains HttpSession that IS Invalid: " + userSession);
                            ex.printStackTrace();
                        }
                    } else {
                        if (l.isLoggable(Level.FINE))
                            l.logp(Level.FINE, getClass().getName(), "displayAllUserSessions", "requestStorageMap doesn't have userSession for Thread [ID: "+thread.getId()+"] [Name: "+thread.getName()+"]");
                    }
                } else {
                    if (l.isLoggable(Level.FINE))
                        l.logp(Level.FINE, getClass().getName(), "displayAllUserSessions", "requestStorageMap IS NULL for Thread [ID: "+thread.getId()+"] [Name: "+thread.getName()+"]");
                }
            } else {
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "displayAllUserSessions", "No requestStorageMap for Thread [ID: "+thread.getId()+"] [Name: "+thread.getName()+"]");
            }
        }

        l.exiting(getClass().getName(), "displayAllUserSessions");
    }

    /**
     *  Clears Expired UserSession objects in ALL Threads
     */
    //Using a special JSP Page
    public Boolean clearExpiredUsersSessions(){
        l.entering(getClass().getName(), "clearExpiredUsersSessions");

        Boolean result = false;
        Set<Thread> keySet = m_storagePerThreadMap.keySet();
        Iterator<Thread> keySetIterator = keySet.iterator();
        while (keySetIterator.hasNext()) {
            Thread thread = (Thread)keySetIterator.next();
            HashMap requestStorageMap = (HashMap) m_storagePerThreadMap.get(thread);
            if(requestStorageMap!=null && requestStorageMap.size()>0) {
                Map clonedMap = new HashedMap();
                if (requestStorageMap != null) {
                    clonedMap = (Map) requestStorageMap.clone();
                    if (clonedMap.containsKey("userSession")) {
                        UserSession userSession = (UserSession)clonedMap.get("userSession");
                        if (l.isLoggable(Level.FINE))
                            l.logp(Level.FINE, getClass().getName(), "clearExpiredUsersSessions", "UserSession for requestStorageMap for Thread [ID: "+thread.getId()+"] [Name: "+thread.getName()+"]\n "+userSession);

                        try {
                            if (userSession instanceof HttpUserSession) {
                                if (l.isLoggable(Level.FINE))
                                    l.logp(Level.FINE, getClass().getName(), "clearExpiredUsersSessions", "Thread [ID: "+thread.getId()+"] UserSession is HttpUserSession");
                                HttpUserSession httpUserSession = (HttpUserSession) userSession;
                                if (l.isLoggable(Level.FINE))
                                    l.logp(Level.FINE, getClass().getName(), "clearExpiredUsersSessions", "Thread [ID: "+thread.getId()+"] HttpUserSession: " + httpUserSession);
                                if (httpUserSession.has(Globals.TRANSACTION_TOKEN_KEY)) {
                                    if (l.isLoggable(Level.FINE))
                                        l.logp(Level.FINE, getClass().getName(), "clearExpiredUsersSessions", "Thread [ID: "+thread.getId()+"] httpUserSession.has: " + httpUserSession.get(Globals.TRANSACTION_TOKEN_KEY));
                                }
                            } else {
                                if (l.isLoggable(Level.FINE))
                                    l.logp(Level.FINE, getClass().getName(), "clearExpiredUsersSessions", "Thread [ID: "+thread.getId()+"] UserSession is NOT HttpUserSession");
                            }
                        } catch (IllegalStateException ex) {
                            l.logp(Level.WARNING, getClass().getName(), "clearExpiredUsersSessions", "Thread [ID: "+thread.getId()+"] Caught IllegalStateException: UserSession " + userSession.getSessionId() + " Contains HttpSession that IS Invalid: " + userSession);
                            ex.printStackTrace();
                            l.logp(Level.INFO, getClass().getName(), "clearExpiredUsersSessions", "Prepared to clear Expired User Session for Thread [ID: "+thread.getId()+"] [Name: "+thread.getName()+"]");
                            this.clear(thread);
                            result = true;
                        }
                    } else {
                        if (l.isLoggable(Level.FINE))
                            l.logp(Level.FINE, getClass().getName(), "clearExpiredUsersSessions", "requestStorageMap doesn't have userSession for Thread [ID: "+thread.getId()+"] [Name: "+thread.getName()+"]");
                    }
                } else {
                    if (l.isLoggable(Level.FINE))
                        l.logp(Level.FINE, getClass().getName(), "clearExpiredUsersSessions", "requestStorageMap IS NULL for Thread [ID: "+thread.getId()+"] [Name: "+thread.getName()+"]");
                }
            } else {
                if (l.isLoggable(Level.FINE))
                    l.logp(Level.FINE, getClass().getName(), "clearExpiredUsersSessions", "No requestStorageMap for Thread [ID: "+thread.getId()+"] [Name: "+thread.getName()+"]");
            }
        }

        return result;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    private Map m_storagePerThreadMap = new ConcurrentHashMap();
    private Map m_skipEntries = new Hashtable();
    private Integer id;

    private static final String USER_SESSION = "userSession";
    private static final String SETUP_FOR_REQUEST_INDICATOR = "rsm.setup.for.request";
    private static RequestStorageManager c_instance;
    private final Logger l = LogUtils.getLogger(getClass());
    private static final Logger c_l = LogUtils.getLogger(RequestStorageManager.class);
}