package dti.oasis.error;

import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a controller class for error handling. All the error handlers are registered via Spring configuration. This
 * error handler controller loops through the list of registered error handlers sequentially in order to fix the reported
 * error.
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
public class ErrorHandlerController {

    /**
     * This invokes the error handers to fix the reported exception. The error handlers are configured via Spring and
     * are processed sequentially in the order of configuration. If an error handler from the list is successfully
     * able to fix the reported exception, then all the remaining error handlers will be by-passed.
     *
     * @param e, an exception that needs to be fixed.
     * @return boolean, true, if the exception is fixed successfully; otherwise, false.
     */
    public boolean invokeErrorHandlers(Throwable e) {
        Logger l = LogUtils.enterLog(getClass(), "invokeErrorHandlers", new Object[]{e});
        boolean isRecovered=false;

        verifyErrorHandlers();
        if(getErrorHandlers()!=null) {
            Iterator itr = getErrorHandlers().iterator();
            while(itr.hasNext() && !isRecovered) {
                ErrorHandler errorHandler = (ErrorHandler) itr.next();
                try {
                    isRecovered = errorHandler.handleError(e);
                } catch (Exception ex) {
                    l.logp(Level.WARNING, getClass().getName(), "invoke", "Failed invoking the error handler: "+errorHandler + "; Continuing with remaining error handlers", ex);
                }
            }
        }
        l.exiting(getClass().getName(), "invokeErrorHandlers", String.valueOf(isRecovered));
        return isRecovered;
    }

    /**
     * Method that verifies whether the configured error handlers are of dti.oasis.error.ErrorHandler type. It throws an
     * exception, if any one of the error handler is not of the type dti.oasis.error.ErrorHandler.
     */
    private void verifyErrorHandlers() {
        if(getErrorHandlers()!=null) {
            Iterator itr = getErrorHandlers().iterator();
            while (itr.hasNext()) {
                Object o = itr.next();
                if(!(o instanceof ErrorHandler)) {
                    throw new ConfigurationException("The following error handler is of not required type 'dti.oasis.error.ErrorHandler': " + o);
                }
            }
        }
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public ErrorHandlerController() {
        super();
    }

    public void verifyConfig() {

    }

    public List getErrorHandlers() {
        return m_errorHandlers;
    }

    public void setErrorHandlers(List errorHandlers) {
        m_errorHandlers = errorHandlers;
    }

    private java.util.List m_errorHandlers;
}
