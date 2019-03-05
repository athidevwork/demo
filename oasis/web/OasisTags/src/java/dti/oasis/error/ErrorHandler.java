package dti.oasis.error;

/**
 * An interface to address and fix the reported exception.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 29, 2007
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface ErrorHandler {
    /**
     * Method that handles the reported exception in order to fix it.
     *
     * @param error, an exception that needs to be fixed.
     * @return boolean true, if the exception is fixed successfully; otherwise, false.
     */
    boolean handleError(Throwable error);
}
