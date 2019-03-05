package dti.oasis.app.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ApplicationLifecycleAdvisor;
import dti.oasis.app.ApplicationLifecycleListener;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LocaleUtils;
import dti.oasis.util.LogUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Concrete implementation that exposes the initialize and terminate methods of the ApplicationLifecycleAdvisor.
 * This class should only be constructed and accessed by a class that adapts the application lifecycle events from the
 * application server container to the ApplicationLifecycleAdvisor.
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
 * 04/08/2008       James       Add last started time
 * ---------------------------------------------------
 */
public class ApplicationLifecycleAdvisorImpl extends ApplicationLifecycleAdvisor {

    public static synchronized ApplicationLifecycleAdvisorImpl getInstance() {
        if (c_instance == null) {
            c_instance = new ApplicationLifecycleAdvisorImpl();
        }
        return c_instance;
    }

    /**
     * Default constructor.
     * This class should only be constructed and accessed by a class that adapts the application lifecycle events from the
     * application server container to the ApplicationLifecycleAdvisor.
     */
    public ApplicationLifecycleAdvisorImpl() {
        super();
    }

    /**
     * Setup this class with a set of High-Priority Listeners,
     * that will be initialized first (in the provided order),
     * and terminated last (in the reverse order they were initialized).
     * If any of the listeners is already registered as normal-priority listeners,
     * is is switched to a high-priority listener.
     *
     * @param highPriorityListeners
     */
    public synchronized void setHighPriorityApplicationLifecycleListeners(Set highPriorityListeners) {
        Logger l = LogUtils.enterLog(getClass(), "setHighPriorityApplicationLifecycleListeners");

        if (highPriorityListeners != null) {
            m_highPriorityListeners = highPriorityListeners;

            // Iterate through the given list, and add them if they are not already registered.
            m_reverseOrderHighPriorityListeners = new LinkedList();
            Iterator iter = m_highPriorityListeners.iterator();
            while (iter.hasNext()) {
                ApplicationLifecycleListener listener = (ApplicationLifecycleListener) iter.next();
                l.logp(Level.FINE, getClass().getName(), "setHighPriorityApplicationLifecycleListeners", "Registering the following listener as high-priority: " + listener);
                m_reverseOrderHighPriorityListeners.add(0, listener);

                // Remove the listener from the normal-priority list if it exists.
                if (containsApplicationLifecycleListener(listener)) {
                    removeApplicationLifecycleListener(listener);
                }
            }
        }
        else {
            l.logp(Level.WARNING, getClass().getName(), "setHighPriorityApplicationLifecycleListeners", "The provided set of high-priority listeners is null. Ignoring this call.");
        }
        l.exiting(getClass().getName(), "setHighPriorityApplicationLifecycleListeners");
    }

    /**
     * Register an ApplicationLifecycleListener to receive notification of application initialization and termination.
     * If the given listener is already registered as a high-priority listener, it is ignored.
     *
     * @param listener an ApplicationLifecycleListener
     */
    public synchronized void registerApplicationLifecycleListener(ApplicationLifecycleListener listener) {
        Logger l = LogUtils.enterLog(getClass(), "registerApplicationLifecycleListener", new Object[]{listener});

        if (!containsHighPriorityApplicationLifecycleListener(listener)) {
            l.logp(Level.FINE, getClass().getName(), "registerApplicationLifecycleListener", "Registering the following listener as normal-priority: " + listener);
            m_listeners.add(listener);
        }

        l.exiting(getClass().getName(), "registerApplicationLifecycleListener");
    }

    /**
     * Execute the initialization logic.
     */
    public synchronized void  initialize(String applicationName) {
        Logger l = LogUtils.enterLog(getClass(), "initialize");

        if (!m_initialized) {
            m_applicationName = applicationName;

            // Run the Garbage Collector to ensure there is as much memory available as possible before initializing the application
            runGC("initialize", 2, 1000);

            // Load the ApplicationContext
            ApplicationContext appCtx = loadApplicationContext();
            if (appCtx != ApplicationContext.getInstance()) {
                ConfigurationException e = new ConfigurationException("The ApplicationContext was not properly initialized for application <" + m_applicationName + ">");
                l.throwing(getClass().getName(), "initialize", e);
                throw e;
            }
            // set last started/deployed time
            appCtx.setLastRefreshTime(new Date());

            // Setup the High-Priority listeners if they are defined in the application context.
            if (appCtx.hasBean(HIGH_PRIORITY_LISTENERS)) {
                setHighPriorityApplicationLifecycleListeners((Set) appCtx.getBean(HIGH_PRIORITY_LISTENERS));
            }

            // Initialize the High-Priority listeners
            triggerInitializeEvent("High-Priority", getHighPriorityListeners());

            // Initialize the Normal-Priority listeners
            triggerInitializeEvent("Normal-Priority", getNormalPriorityListeners());

            // Run the Garbage Collector in a background thread to ensure there is as much memory available as possible after initializing the application
            // Delay 10 seconds before starting
            // Run GC 8 times, pausing 30 seconds between iterations
            triggerRunGC(10000, 8, 30000);

            m_initialized = true;
        }

        l.exiting(getClass().getName(), "initialize");
    }

    /**
     * Execute the termination logic.
     * Upon completion of this method, this class will no longer be useable,
     * as all member and class variables will be cleared.
     */
    public void terminate() {
        Logger l = LogUtils.enterLog(getClass(), "terminate");

        // Initialize the Normal-Priority listeners
        triggerTerminateEvent("Normal-Priority", getNormalPriorityListeners());

        // Initialize the High-Priority listeners
        triggerTerminateEvent("High-Priority", getHighPriorityListenersInReverseOrder());

        // Close the ApplicationContext
        closeApplicationContext();

        // Run the Garbage Collector to ensure there is as much memory available as possible for the next deployment and other running applications.
        runGC("terminate", 2, 1000);

        // Clear all member and class variables.
        m_highPriorityListeners = null;
        m_reverseOrderHighPriorityListeners = null;
        m_listeners = null;
        super.terminate();

        l.exiting(getClass().getName(), "terminate");
    }

    private void runGC(String methodName, int numIterations, long pauseDuration) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "runGC", new Object[]{methodName, Integer.valueOf(numIterations), Long.valueOf(pauseDuration)});
        }

        DecimalFormat df = (DecimalFormat)NumberFormat.getNumberInstance(LocaleUtils.getOasisLocale());
        df.applyPattern("#,##0");

        for (int i = 1; i <= numIterations; i++) {
            if (i > 1) {
                try {
                    Thread.sleep(pauseDuration);
                } catch (InterruptedException e) { }
            }
            l.logp(Level.INFO, getClass().getName(), methodName + ".runGC", "(Iteration " + i + " of " + numIterations + " for <" + m_applicationName + "> Free memory before running Runtime.getRuntime().gc(): " + df.format(Runtime.getRuntime().freeMemory()));
            Runtime.getRuntime().gc();
            l.logp(Level.INFO, getClass().getName(), methodName + ".runGC", "(Iteration " + i + " of " + numIterations + " for <" + m_applicationName + "> Free memory after running Runtime.getRuntime().gc(): " + df.format(Runtime.getRuntime().freeMemory()));
        }

        l.exiting(getClass().getName(), "runGC");
    }

    private void triggerRunGC(long startDelay, int numIterations, long pauseDuration) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "triggerRunGC", new Object[]{Long.valueOf(startDelay), Long.valueOf(pauseDuration)});
        }

        PostInitGC postInitGC = new PostInitGC(startDelay, numIterations, pauseDuration);
        new Thread(postInitGC).start();
        
        l.exiting(getClass().getName(), "triggerRunGC");
    }

    /**
     * Load the concrete implementation of ApplicationContext.
     */
    protected ApplicationContext loadApplicationContext() {
        Logger l = LogUtils.enterLog(getClass(), "loadApplicationContext");

        ApplicationContext appCtx = new SpringApplicationContext(getApplicationName());
        appCtx.load();
        m_appCtx = ((SpringApplicationContext)appCtx).getXmlApplicationContext();
        l.exiting(getClass().getName(), "loadApplicationContext");

        return appCtx;
    }

    public org.springframework.context.ApplicationContext getAppCtx() {
        return m_appCtx;
    }


    /**
     * Release the Application Context resources.
     */
    protected void closeApplicationContext() {
        Logger l = LogUtils.enterLog(getClass(), "closeApplicationContext");

        SpringApplicationContext appCtx = (SpringApplicationContext) ApplicationContext.getInstance();
        appCtx.close();

        l.exiting(getClass().getName(), "closeApplicationContext");
    }

    /**
     * Check if the given listener is registered as a high-priority ApplicationLifecycleListener.
     */
    protected boolean containsHighPriorityApplicationLifecycleListener(ApplicationLifecycleListener listener) {
        Logger l = LogUtils.enterLog(getClass(), "containsHighPriorityApplicationLifecycleListener", new Object[]{listener});

        boolean containsListener = false;
        Iterator iter = m_highPriorityListeners.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o == listener) {
                containsListener = true;
                break;
            }
        }

        l.exiting(getClass().getName(), "containsHighPriorityApplicationLifecycleListener", String.valueOf(containsListener));
        return containsListener;
    }

    /**
     * Check if the given listener is registered as a normal-priority ApplicationLifecycleListener.
     */
    protected boolean containsApplicationLifecycleListener(ApplicationLifecycleListener listener) {
        Logger l = LogUtils.enterLog(getClass(), "containsApplicationLifecycleListener", new Object[]{listener});

        boolean containsListener = false;
        Iterator iter = m_listeners.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o == listener) {
                containsListener = true;
                break;
            }
        }

        l.exiting(getClass().getName(), "containsApplicationLifecycleListener", String.valueOf(containsListener));
        return containsListener;
    }

    /**
     * Remove the given listener from this set if it exists.
     */
    protected void removeApplicationLifecycleListener(ApplicationLifecycleListener listener) {
        Logger l = LogUtils.enterLog(getClass(), "removeApplicationLifecycleListener", new Object[]{listener});

        Iterator iter = m_listeners.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o == listener) {
                iter.remove();
            }
        }
        l.exiting(getClass().getName(), "removeApplicationLifecycleListener");
    }

    /**
     * Trigger the initialize event on the given list of listeners
     *
     * @param priorityLevel a description of the priority level for the given listeners
     * @param iter          an Iterator of listeners.
     */
    private void triggerInitializeEvent(String priorityLevel, Iterator iter) {
        Logger l = LogUtils.enterLog(getClass(), "triggerInitializeEvent", new Object[]{priorityLevel});
        while (iter.hasNext()) {
            ApplicationLifecycleListener listener = (ApplicationLifecycleListener) iter.next();
            try {
                l.logp(Level.FINE, getClass().getName(), "triggerInitializeEvent", "Invoking initialize() on the ApplicationLifecycleListener: '" + listener.getClass().getName() + "'");
                listener.initialize();
            } catch (AppException e) {
                String msg = "Failed while invoking initialize on the " + priorityLevel + " listener: '" + listener + "'.";
                l.logp(Level.SEVERE, getClass().getName(), "initialize", msg);
                throw e;
            } catch (Throwable e) {
                String msg = "Failed while invoking initialize on the " + priorityLevel + " listener: '" + listener + "'.";
                l.logp(Level.SEVERE, getClass().getName(), "initialize", msg);
                AppException ae = new AppException(msg, e);
                l.throwing(getClass().getName(), "initialize", ae);
                throw ae;
            }
        }
        l.exiting(getClass().getName(), "initialize");
    }

    /**
     * Trigger the terminate event on the given list of listeners
     *
     * @param priorityLevel a description of the priority level for the given listeners
     * @param iter          an Iterator of listeners.
     */
    private void triggerTerminateEvent(String priorityLevel, Iterator iter) {
        Logger l = LogUtils.enterLog(getClass(), "triggerTerminateEvent", new Object[]{priorityLevel});
        while (iter.hasNext()) {
            ApplicationLifecycleListener listener = (ApplicationLifecycleListener) iter.next();
            try {
                l.logp(Level.FINE, getClass().getName(), "triggerTerminateEvent", "Invoking terminate() on the ApplicationLifecycleListener: '" + listener.getClass().getName() + "'");
                listener.terminate();
            } catch (Throwable e) {
                String msg = "Failed while invoking Terminate on the " + priorityLevel + " listener: '" + listener + "'.";
                l.logp(Level.SEVERE, getClass().getName(), "terminate", msg);
            }
        }
        l.exiting(getClass().getName(), "terminate");
    }

    class PostInitGC implements Runnable {
        private long m_delayStart;
        private int m_numIterations;
        private long m_pauseDuration;
        PostInitGC(long delayStart, int numIterations, long pauseDuration) {
            m_delayStart = delayStart;
            m_numIterations = numIterations;
            m_pauseDuration = pauseDuration;
        }
        
        public void run() {
            try {
                Thread.sleep(m_delayStart);
            } catch (InterruptedException e) { }

            runGC("PostInitGC", m_numIterations, m_pauseDuration);
        }
    }

    //-------------------------------------------------
    // Accessor methods
    //-------------------------------------------------
    protected synchronized Iterator getHighPriorityListeners() {
        return m_highPriorityListeners.iterator();
    }

    protected synchronized Iterator getHighPriorityListenersInReverseOrder() {
        return m_reverseOrderHighPriorityListeners.iterator();
    }

    protected synchronized Iterator getNormalPriorityListeners() {
        return m_listeners.iterator();
    }

    protected synchronized String getApplicationName() {
        return m_applicationName;
    }

    private Set m_highPriorityListeners = new LinkedHashSet();
    private List m_reverseOrderHighPriorityListeners = new LinkedList();
    private Set m_listeners = new LinkedHashSet();
    private String m_applicationName;
    private boolean m_initialized = false;
    private org.springframework.context.ApplicationContext m_appCtx;

    private static ApplicationLifecycleAdvisorImpl c_instance = null;
}
