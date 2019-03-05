package dti.oasis.request;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.CachedPerRequestDataSource;
import dti.oasis.http.RequestIds;
import dti.oasis.session.UserSessionManager;
import dti.oasis.session.UserSessionManagerAdmin;
import dti.oasis.util.LogUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The RequestLifecycleAdvisor coordinates the core initialization, termination and failure logic for each request,
 * and invokes all registered RequestLifecycleListener objects with the Request Lifecycle Events
 * to indicate that the request is initializing, terminating and/or failing.
 * The Request Lifecycle will always start with initializing, and will always end with termination.
 * If a failure occurs during the request that results in an Exception,
 * the failure event will be triggered prior the termination event.
 * <p/>
 * During core initialization, first the RequestStorageManager is setup, then the UserSessionManagerAdmin is connected
 * for the user id, and finally all registered listeners are notified of the rquest initialization event.
 * <p/>
 * During core termination, first all registered listeners are notified of the rquest termination event,
 * then the UserSessionManagerAdmin is disconnected, and finally the RequestStorageManager is cleaned up.
 * <p/>
 * In any of the registered listeners fail to initialize, an AppException will be thrown representing the exception.
 * If any of the registered listeners fail to terminate, the exception will be logged, and termination will continue.
 * All registered listeners are notified of a failure, no matter if any of the listeners fix the problem.
 * <p/>
 * <b>NOTE:</b> In the context of an HTTP Request, the Request is terminated before any JSP pages are executed.
 * That is because the JSP pages may be executed as part of a redirect, in which case it is an entirely new Request.
 * Therefore, any componentents that rely upon the RequestLifecycle processing to initialize it (ex. UserSessionManager)
 * must be explicitly intialized before use in a JSP file.
 * <p/>
 * To register as a RequestLifecycleListener, implement the RequestLifecycleListener interface,
 * and either call the registerRequestLifecycleListener method on RequestLifecycleAdvisor
 * or define the RequestLifecycleListener as a bean in the Spring Configuration.
 * A Request Lifecycle Post Processor automatically registers all classes implementing the RequestLifecycleListener
 * interface with the RequestLifecycleAdvisor.
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
 * 02/28/2008       fcb         initializeFromRequestState and getRequestState() added.
 * 07/08/2008       sxm         removed m_anonymousUserId per Bill Reeder's request
 * 07/14/2008       mlm         Enhanced to publish web session id to request storage manager.
 * 09/16/2009       fcb         98370: stored request into Request Storage Manager
 * ---------------------------------------------------
 */
public class RequestLifecycleAdvisor {

    /**
     * The bean name of a RequestLifecycleAdvisor extension if this default is not used.
     */
    public static final String BEAN_NAME = "RequestLifecycleAdvisor";

    /**
     * Return an instance of the RequestLifecycleAdvisor.
     */
    public synchronized static RequestLifecycleAdvisor getInstance() {
        l.entering(RequestLifecycleAdvisor.class.getName(), "getInstance");
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (RequestLifecycleAdvisor) ApplicationContext.getInstance().getBean(BEAN_NAME);
            }
        }
        l.exiting(RequestLifecycleAdvisor.class.getName(), "getInstance", c_instance);
        return c_instance;
    }

    /**
     * Execute the initialization logic based on information from the given HttpServletRequest,
     * and then trigger the initialize event to all registered RequestLifecycleListeners.
     *
     * @param request the HttpServletRequest
     */
    public void initialize(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initialize", new Object[]{request});
        }
        if (l.isLoggable(Level.FINE))
            l.logp(Level.FINE, getClass().getName(), "initialize", "threadId[" + Thread.currentThread().getId() + "]");
        // Setup the REQUEST_URI request attribute with the request uri of this invocation so that JSPs and Tags can use it.
        // Otherwise, when a JSP/Tag calls request.getRequestURI(), it returns the URI of the JSP page.
        // Make sure to setup the REQUEST_URI each time initialize is called to make sure it reflects the current request,
        // especially if it is a forwarded request.
        request.setAttribute(RequestIds.REQUEST_URI, request.getRequestURI());

        //Check if current thread has invalid HttpSession
        //Then clear the RSM for this thread
        if(!getRequestStorageManager().isHttpSessionValid()){
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "initialize", "threadId[" + Thread.currentThread().getId() + "]. HttpSession IS INVALID. Calling clear()");
            getRequestStorageManager().clear();
        } else {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "initialize", "threadId[" + Thread.currentThread().getId() + "]. HttpSession is Valid.");
        }

        // The initialize() method may get called multiple times, so be sure to initialize the request just once.
        if (getInitializationCount() == 0) {

            // Initialize the Logger with the userId for this request
            String userId = request.getRemoteUser();
            LogUtils.setupForRequest(userId);

            getRequestStorageManager().setupForRequest();
            getRequestStorageManager().set(RequestStorageIds.HTTP_SEVLET_REQUEST, request);

            // Publish the web session id into the request storage manager for use in impl classes.
            getRequestStorageManager().set(RequestIds.WEB_SESSION_ID, request.getSession().getId());
          
            getUserSessionManagerAdmin().setupForRequest(request);
            triggerInitializeEvent();
        }

        // Keep track of how many times initialize is called for this request so we only terminate on the last call
        incrementInitializationCount();

        l.exiting(getClass().getName(), "initialize");
    }

    /**
     * Execute the initialization logic with the given user id,
     * and then trigger the initialize event to all registered RequestLifecycleListeners.
     *
     * @param userId the id of the logged-in user
     */
    public void initializeByUser(String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initializeByUser", new Object[]{userId});
        }

        // The initialize() method may get called multiple times, so be sure to initialize the request just once.
        if (getInitializationCount() == 0) {
            // Initialize the Logger with the userId for this request
            LogUtils.setupForRequest(userId);

            getRequestStorageManager().setupForRequest();
            getUserSessionManagerAdmin().setupForRequest(userId);
            triggerInitializeEvent();

          // Create a web session id (for Web Services) and publish it into the request storage manager
          // for use in impl classes.
          getRequestStorageManager().set(RequestIds.WEB_SESSION_ID, (userId + "_" + (new Random((new Date()).getTime())).toString()) );
        }

        // Keep track of how many times initialize is called for this request so we only terminate on the last call
        incrementInitializationCount();

        l.exiting(getClass().getName(), "initializeByUser");
    }

    /**
     * Execute the initialization logic from a RequestSession object.
     *
     * @param requestSession the id of the logged-in user
     */
    public void initializeFromRequestState(RequestSession requestSession) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initializeFromRequestState", new Object[]{requestSession});
        }

        // No need to check the initialization count since initialization/termination is guaranteed to only occur once in a background thread.

        // Initialize the Logger with the userId for this request
        String userId = requestSession.getUserSession().getUserId();
        LogUtils.setupForRequest(userId);

        Map map = requestSession.getRequestStorageMap();

        if (map.containsKey(CachedPerRequestDataSource.CONNECTION_KEY)) {
            map.remove(CachedPerRequestDataSource.CONNECTION_KEY);
        }

        requestSession.getRequestStorageMap().put(RequestStorageIds.EXECUTING_IN_BACKGROUND_THREAD, "Y");
        getRequestStorageManager().setupForRequest(requestSession.getRequestStorageMap());
        getUserSessionManagerAdmin().setupForRequest(requestSession.getUserSession());
        triggerInitializeEvent();

        l.exiting(getClass().getName(), "initializeFromRequestState");
    }

    public RequestSession getRequestState() {
        l.entering(getClass().getName(), "getRequestState");

        RequestSession requestSession = new RequestSession();
        // Put copies of the request state map and user session information into the requestSession
        requestSession.setRequestStorageMap(RequestStorageManager.getInstance().getCopy());
        UserSessionManagerAdmin userSessionManager = (UserSessionManagerAdmin)UserSessionManager.getInstance();
        requestSession.setUserSession(userSessionManager.getCopy());

        l.exiting(getClass().getName(), "getRequestState");

        return requestSession;
    }

    protected void triggerInitializeEvent() {
        l.entering(getClass().getName(), "triggerInitializeEvent");

        Iterator iter = getListeners();
        while (iter.hasNext()) {
            RequestLifecycleListener listener = (RequestLifecycleListener) iter.next();
            l.logp(Level.FINE, getClass().getName(), "triggerInitializeEvent", "Invoking initialize() on the RequestLifecycleListener: '"+listener.getClass().getName()+"'");
            listener.initialize();
        }

        l.exiting(getClass().getName(), "triggerInitializeEvent");
    }

    /**
     * Trigger the terminate event to all registered RequestLifecycleListeners,
     * and then execute any core termination logic.
     */
    public void terminate() {
        l.entering(getClass().getName(), "terminate");

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        if (!rsm.has(RequestStorageIds.EXECUTING_IN_BACKGROUND_THREAD) ||
                !YesNoFlag.getInstance((String) rsm.get(RequestStorageIds.EXECUTING_IN_BACKGROUND_THREAD)).booleanValue()) {

            // The initialize() method may get called multiple times, so be sure to terminate the request
            // only if we are in the scope of the first initialize request.
            int initCount = getInitializationCount();
            if (initCount == 1) {
                _terminate();
                // No need to decrement the initialization count because it is cleaned up when the RequestStorageManager is cleaned up
            }
            else {
                // Decrement the initialization count
                decrementInitializationCount();
            }
        }
        else {
            // No need to check the initialization count since initialization/termination is guaranteed to only occur once in a background thread.
            _terminate();
        }

        l.exiting(getClass().getName(), "terminate");
    }

    private void _terminate() {
        triggerTerminateEvent();
        getUserSessionManagerAdmin().cleanupFromRequest();
        getRequestStorageManager().cleanupFromRequest();

        // Cleanup the userId from the Logger for this request
        LogUtils.cleanupFromRequest();
    }

    protected void triggerTerminateEvent() {
        l.entering(getClass().getName(), "triggerTerminateEvent");

        Iterator iter = getListeners();
        while (iter.hasNext()) {
            RequestLifecycleListener listener = (RequestLifecycleListener) iter.next();
            l.logp(Level.FINE, getClass().getName(), "triggerTerminateEvent", "Invoking terminate() on the RequestLifecycleListener: '"+listener.getClass().getName()+"'");
            listener.terminate();
        }

        l.exiting(getClass().getName(), "triggerTerminateEvent");
    }

    /**
     * Trigger the failure event to all registered RequestLifecycleListeners.
     * If any listener returns true, this method returns true.
     *
     * @param e the Throwable Exception that triggered the failure event.
     * @return true if any registered RequestLifecycleListeners return true; otherwise, false.
     */
    public boolean failure(Throwable e) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "failure", new Object[]{e});
        }

        boolean fixed = false;
        Iterator iter = getListeners();
        while (iter.hasNext()) {
            RequestLifecycleListener listener = (RequestLifecycleListener) iter.next();
            if (listener.failure(e, fixed)) {
                fixed = true;
            }
        }

        l.exiting(getClass().getName(), "failure", String.valueOf(fixed));
        return fixed;
    }

    /**
     * Increment the counter of times initialize is called for this request.
     * @return the current count.
     */
    protected int incrementInitializationCount() {
        l.entering(getClass().getName(), "incrementInitializationCount");

        RequestStorageManager rsm = getRequestStorageManager();
        int initCount = 0;
        if (rsm.has(INITIALIZATION_COUNT)) {
            initCount = ((Integer) rsm.get(INITIALIZATION_COUNT)).intValue();
        }
        initCount++;
        rsm.set(INITIALIZATION_COUNT, new Integer(initCount));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "incrementInitializationCount", String.valueOf(initCount));
        }
        return initCount;
    }

    /**
     * Return the counter of times initialize is called for this request.
     * @return the current count.
     */
    protected int getInitializationCount() {
        l.entering(getClass().getName(), "getInitializationCount");

        RequestStorageManager rsm = getRequestStorageManager();
        int initCount = 0;
        if (rsm.has(INITIALIZATION_COUNT)) {
            initCount = ((Integer) rsm.get(INITIALIZATION_COUNT)).intValue();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitializationCount", String.valueOf(initCount));
        }
        return initCount;
    }
    /**
     * Decrement the counter of times initialize is called for this request.
     * @return the current count.
     */
    protected int decrementInitializationCount() {
        l.entering(getClass().getName(), "decrementInitializationCount");

        RequestStorageManager rsm = getRequestStorageManager();
        int initCount = 0;
        if (rsm.has(INITIALIZATION_COUNT)) {
            initCount = ((Integer) rsm.get(INITIALIZATION_COUNT)).intValue();
        }

        if (initCount > 0) {
            initCount--;
        }
        else {
            Throwable e = new Throwable();
            l.logp(Level.WARNING, getClass().getName(), "decrementInitializationCount", "The current request initialization count is '" + initCount + "' and should never decrimented when below 1.",e);
        }
        rsm.set(INITIALIZATION_COUNT, new Integer(initCount));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "decrementInitializationCount", String.valueOf(initCount));
        }
        return initCount;
    }

    public synchronized void registerRequestLifecycleListener(RequestLifecycleListener listener) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "registerRequestLifecycleListener", new Object[]{listener});
        }

        boolean newListener = m_listeners.add(listener);
        if (!newListener) {
            l.logp(Level.WARNING, getClass().getName(), "registerRequestLifecycleListener", "The following RequestLifecycleListener is already registered: " + newListener);
        }

        l.exiting(getClass().getName(), "registerRequestLifecycleListener");
    }

    protected synchronized Iterator getListeners() {
        return m_listeners.iterator();
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public RequestLifecycleAdvisor() {
    }

    public UserSessionManagerAdmin getUserSessionManagerAdmin() {
        if (m_userSessionManagerAdmin == null) {
            // Allow this class to not be configured through Spring
            m_userSessionManagerAdmin = (UserSessionManagerAdmin) UserSessionManager.getInstance();
        }
        return m_userSessionManagerAdmin;
    }

    public void setUserSessionManagerAdmin(UserSessionManagerAdmin userSessionManagerAdmin) {
        m_userSessionManagerAdmin = userSessionManagerAdmin;
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

    public void verifyConfig() {
        if (getUserSessionManagerAdmin() == null)
            throw new ConfigurationException("The required property 'userSessionManagerAdmin' is missing.");
        if (getRequestStorageManager() == null)
            throw new ConfigurationException("The required property 'requestStorageManager' is missing.");
    }

    private UserSessionManagerAdmin m_userSessionManagerAdmin;
    private RequestStorageManager m_requestStorageManager;
    private Set m_listeners = new LinkedHashSet();

    private static final String INITIALIZATION_COUNT = "rla.initialization.count";
    private static RequestLifecycleAdvisor c_instance;
    private static final Logger l = LogUtils.getLogger(RequestLifecycleAdvisor.class);
}
