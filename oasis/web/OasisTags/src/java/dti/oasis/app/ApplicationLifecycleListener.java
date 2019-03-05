package dti.oasis.app;

/**
 * This Interface represents a class that handles the Application Lifecycle Events.
 * Register an implementation of this interface with the ApplicationLifecycleAdvisor
 * to receive notification of the Application Lifecycle Events.
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
public interface ApplicationLifecycleListener {
    /**
     * Initialize the bean.
     */
    public void initialize();

    /**
     * Cleanup open resources and sizeable static variables, such as a static cache.
     */
    public void terminate();
}