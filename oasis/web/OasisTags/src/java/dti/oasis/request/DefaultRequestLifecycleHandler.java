package dti.oasis.request;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 28, 2006
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
public class DefaultRequestLifecycleHandler implements RequestLifecycleListener {
    /**
     * Implement this method to handle the Initialization Request Lifecycle Event.
     */
    public void initialize() {
        // Do nothing
    }

    /**
     * Implement this method to handle the Termination Request Lifecycle Event.
     */
    public void terminate() {
        // Do nothing
    }

    /**
     * Implement this method to handle the Failure Request Lifecycle Event.
     * Return true if the failure was fixed; otherwise, false.
     *
     * @param e     the Throwable Exception that triggered the failure event.
     * @param fixed the problem has already be fixed.
     * @return true if the failure was fixed; otherwise, false.
     */
    public boolean failure(Throwable e, boolean fixed) {
        // Do nothing
        return false;
    }
}
