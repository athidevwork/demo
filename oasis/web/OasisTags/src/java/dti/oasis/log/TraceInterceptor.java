package dti.oasis.log;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2006
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
public class TraceInterceptor implements MethodInterceptor {
    public Object invoke(MethodInvocation i) throws Throwable {
        if (l.isLoggable(Level.FINER)) {
            l.entering(i.getThis().getClass().getName(), i.getMethod().getName(), i.getArguments());
        }

        try {
            Object retVal = i.proceed();
            l.exiting(i.getThis().getClass().getName(), i.getMethod().getName(), retVal);
            return retVal;

        } catch (Throwable t) {
            l.throwing(i.getThis().getClass().getName(), i.getMethod().getName(), t);
            throw t;
        }
    }
    private final java.util.logging.Logger l = LogUtils.getLogger(getClass());
}
