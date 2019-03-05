package dti.oasis.request;

import dti.oasis.util.LogUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Register all beans that implement the RequestLifecycleListener with the RequestLifecycleAdvisor.
 * during the 'after initialization' event.
 * <p/>
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
public class RequestLifecycleRegistrar implements BeanPostProcessor {
    /**
     * Ignore the before initialization event.
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * Register all beans that implement the RequestLifecycleListener with the RequestLifecycleAdvisor.
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessAfterInitialization", new Object[]{bean, beanName});
        }

        if (bean instanceof RequestLifecycleListener) {
            l.logp(Level.INFO, getClass().getName(), "postProcessAfterInitialization", "Registering the bean '" + beanName + "' with the RequestLifecycleAdvisor as a RequestLifecycleListener.");
            RequestLifecycleAdvisor.getInstance().registerRequestLifecycleListener((RequestLifecycleListener) bean);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessAfterInitialization", bean);
        }
        return bean;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public RequestLifecycleRegistrar() {
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
