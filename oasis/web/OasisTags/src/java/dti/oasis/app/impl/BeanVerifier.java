package dti.oasis.app.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For all beans with a public void verifyConfig() method,
 * verify the Configuration before any initialization methods are invoked,
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
public class BeanVerifier implements BeanPostProcessor {
    /**
     * Verify the Configuration before any initialization methods are invoked.
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Logger l = LogUtils.enterLog(getClass(), "postProcessBeforeInitialization", new Object[]{bean, beanName});

        // Check for a verifyConfig method and call it if it exists.
        try {
            Method verifyConfig = bean.getClass().getMethod("verifyConfig");

            l.logp(Level.FINE, getClass().getName(), "postProcessBeforeInitialization", "invoking verifyConfig() on bean'" + beanName + "'");
            verifyConfig.invoke(bean);

        } catch (NoSuchMethodException e) {
            // Ignore this bean
        } catch (AppException e) {
            throw e;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AppException) {
                throw (AppException)cause;
            }
            else {
                ConfigurationException ce = new ConfigurationException("Failed to invoke verifyConfig() on bean'" + beanName + "'", e);
                l.throwing(getClass().getName(), "postProcessBeforeInitialization", ce);
                throw ce;
            }
        } catch (Exception e) {
            ConfigurationException ce = new ConfigurationException("Failed to invoke verifyConfig() on bean'" + beanName + "'", e);
            l.throwing(getClass().getName(), "postProcessBeforeInitialization", ce);
            throw ce;
        }

        l.exiting(getClass().getName(), "postProcessBeforeInitialization", bean);
        return bean;
    }

    /**
     * Ignore after initialization call.
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
