package dti.oasis.app;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.logging.Level;
import java.util.logging.Logger;

import dti.oasis.util.LogUtils;

/**
 * Register all beans that implement the ApplicationLifecycleListener with the ApplicationLifecycleAdvisor
 * during the 'before initialization' event.
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
public class ApplicationLifecycleRegistrar implements BeanPostProcessor {
    /**
     * Register all beans that implement the ApplicationLifecycleListener with the ApplicationLifecycleAdvisor.
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Logger l = LogUtils.enterLog(getClass(), "postProcessBeforeInitialization", new Object[]{beanName});
        if (bean instanceof ApplicationLifecycleListener) {
            l.logp(Level.FINE, getClass().getName(), "postProcessAfterInitialization", "Registering the bean '" + beanName + "' with the ApplicationLifecycleAdvisor as a ApplicationLifecycleListener.");
            ApplicationLifecycleAdvisor.getInstance().
                registerApplicationLifecycleListener((ApplicationLifecycleListener) bean);
        }
        l.exiting(getClass().getName(), "postProcessBeforeInitialization");
        return bean;
    }

    /**
     * Ignore the after initialization event.
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
