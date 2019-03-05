package dti.oasis.data;

import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ErrorHandlerController;
import org.aopalliance.intercept.MethodInvocation;

/**
 * This class provides the AOP model to intercept the request's method invocation and perform customer processing such
 * as fixing errors encountered during the process of the request.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 26, 2007
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
public class DBTransactionInterceptor extends org.springframework.transaction.interceptor.TransactionInterceptor{

    /**
     * This invokes the method for the provided request's method invocation information.

     * @param methodInvocation, information about the method that needs to be invoked.
     * @return object
     * @throws Throwable
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object objResult=null;
        try {
            objResult = super.invoke(methodInvocation);
        } catch (Exception e) {
            boolean isRecovered = getErrorHandlerController().invokeErrorHandlers(e);
            if(isRecovered) {
                //Exception successfully recovered by the error handler, so try invoking the method again.
                objResult = super.invoke(methodInvocation);
            } else {
                //Unable to recover by the error handler, so raise the actual exception reported earlier.
                throw e;
            }
        }
        return objResult ;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public DBTransactionInterceptor() {
        super();
    }

    public void verifyConfig() {
        if (getErrorHandlerController() == null)
                throw new ConfigurationException("The required property 'errorHandlerController' is missing.");
    }

    public ErrorHandlerController getErrorHandlerController() {
        return m_errorHandlerController;
    }

    public void setErrorHandlerController(ErrorHandlerController errorHandlerController) {
        m_errorHandlerController = errorHandlerController;
    }

    private ErrorHandlerController m_errorHandlerController;
}
