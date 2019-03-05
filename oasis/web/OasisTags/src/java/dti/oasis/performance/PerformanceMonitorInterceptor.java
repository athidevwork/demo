package dti.oasis.performance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.logging.Logger;
import java.util.logging.Level;

import dti.oasis.util.LogUtils;

/**
 * Logs the amount of time to execute the method as Level.INFO, using the logger for this class.
 * This class is meant for debugging purposes only, and should not be configured in a production system.
 * <p/>
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
public class PerformanceMonitorInterceptor implements MethodInterceptor {
    public Object invoke(MethodInvocation i) throws Throwable {
        Monitor monitor = new Monitor(i.getMethod().getDeclaringClass().getName() + " " + i.getMethod().getName());
        monitor.start();
        try {
            return i.proceed();
        } finally {
            monitor.stop();
            l.logp(Level.INFO, getClass().getName(), "invoke", monitor.getStatus());
        }
    }
    private final Logger l = LogUtils.getLogger(getClass());
}
