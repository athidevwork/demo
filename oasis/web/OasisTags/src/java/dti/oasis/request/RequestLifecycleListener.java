package dti.oasis.request;

/**
 * This Interface represents a class that handles the Request Lifecycle Events.
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
 *
 * ---------------------------------------------------
 */
public interface RequestLifecycleListener {

    /**
     * Implement this method to handle the Initialization Request Lifecycle Event.
     */
    public void initialize();

    /**
     * Implement this method to handle the Termination Request Lifecycle Event.
     * This method will always be invoked upon termination of the request, regardless of whether failure() was called.
     */
    public void terminate();

    /**
     * Implement this method to handle the Failure Request Lifecycle Event.
     * Return true if the failure was fixed; otherwise, false.
     *
     * @param e the Throwable Exception that triggered the failure event.
     * @param fixed the problem has already be fixed.
     * @return true if the failure was fixed; otherwise, false.
     */
    public boolean failure(Throwable e, boolean fixed);
}