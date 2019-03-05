package dti.oasis.app;

import dti.oasis.util.LogUtils;

import java.util.Set;
import java.util.logging.Logger;

/**
 * The ApplicationLifecycleAdvisor coordinates all initialization and termination of application components.
 * Register as an ApplicationLifecycleListener to receive notification of application initialization and termination.
 * This class can be configured with a Set of High-Priority ApplicationLifecycleListeners,
 * that will be initialized first (in the provided order),
 * and terminated last (in the reverse order they were initialized).
 * <p/>
 * In any of the registered listeners fail to initialize, an AppException will be thrown representing the exception.
 * If any of the registered listeners fail to terminate, the exception will be logged, and termination will continue.
 * <p/>
 * Extend this class for the purpose of triggering the Iniatialize and Terminate events.
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
public abstract class ApplicationLifecycleAdvisor {

    /**
     * Bean name of a java.util.Set of High-Priority ApplicationLifecycleListeners.
     */
    public static final String HIGH_PRIORITY_LISTENERS = "HighPriorityApplicationLifecycleListeners";

    /**
     * Get an instance of the ApplicationLifecycleAdvisor.
     * A concrete extension of this ApplicationLifecycleAdvisor must be explicitly created prior to calling
     * this method.
     *
     * @throws ConfigurationException if a concrete extension of this ApplicationLifecycleAdvisor has not been explicitly created.
     */
    public static ApplicationLifecycleAdvisor getInstance() {
        Logger l = LogUtils.enterLog(ApplicationLifecycleAdvisor.class, "getInstance");
        if (c_instance == null) {
            throw new ConfigurationException("A concrete implementation of ApplicationLifecycleAdvisor has not been configured.");
        }
        l.exiting(ApplicationLifecycleAdvisor.class.getName(), "getInstance", c_instance);
        return c_instance;
    }

    /**
     * Setup this class with a set of High-Priority Listeners,
     * that will be initialized first (in the provided order),
     * and terminated last (in the reverse order they were initialized).
     *
     * @param highPriorityListeners
     */
    public abstract void setHighPriorityApplicationLifecycleListeners(Set highPriorityListeners);

    /**
     * Register an ApplicationLifecycleListener to receive notification of application initialization and termination.
     *
     * @param listener an ApplicationLifecycleListener
     */
    public abstract void registerApplicationLifecycleListener(ApplicationLifecycleListener listener);

    /**
     * Cleanup all class variables
     */
    public void terminate() {
        Logger l = LogUtils.enterLog(getClass(), "terminate");

        c_instance = null;

        l.exiting(getClass().getName(), "terminate");
    }

    /**
     * Constructor. Store a reference to the concrete implementation of this class.
     */
    protected ApplicationLifecycleAdvisor() {
        Logger l = LogUtils.enterLog(getClass(), "ApplicationLifecycleAdvisor");

        c_instance = this;

        l.exiting(getClass().getName(), "ApplicationLifecycleAdvisor");
    }

    private static ApplicationLifecycleAdvisor c_instance;
}