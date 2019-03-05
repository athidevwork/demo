package dti.oasis.app.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.JstlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.Properties;

/**
 * This class loads the Application Context using the Spring Framework.
 * This class should only be constructed and accessed from a concrete implementation ApplicationLifecycleAdvisor.
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
 * 02/07/2007       wer         Added Support for multiple Message Resource Files
 * 04/09/2008       wer         Added getPropertyNames method
 * 03/07/2009       Fred        Remove the blank spaces at the end of property value
 * ---------------------------------------------------
 */
public class SpringApplicationContext extends ApplicationContext {

    /**
     * Default constructor.
     * This class should only be constructed and accessed from a concrete implementation ApplicationLifecycleAdvisor.
     */
    public SpringApplicationContext(String applicationName) {
        super(applicationName);
    }

    /**
     * Returns true if the specified bean exists; otherwise, false.
     */
    public boolean hasBean(String beanName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasBean", new Object[]{beanName});
        }

        boolean beanExists = _hasBean(beanName);

        l.exiting(getClass().getName(), "hasBean", String.valueOf(beanExists));
        return beanExists;
    }

    /**
     * Returns true if the specified bean exists; otherwise, false.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public boolean _hasBean(String beanName) {
        boolean beanExists = m_appCtx.containsBean(beanName);

        return beanExists;
    }

    /**
     * Get a bean from the Application Context given the unique bean name.
     *
     * @param beanName the name of the bean to retrieve
     * @return the desired bean
     * @throws dti.oasis.app.ConfigurationException if no bean exists by the given name.
     */
    public Object getBean(String beanName) throws ConfigurationException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getBean", new Object[]{beanName});
        }

        Object bean = null;
        try {
            bean = _getBean(beanName);
        } catch (ConfigurationException e) {
            l.throwing(getClass().getName(), "getBean", e);
            throw e;
        }

        l.exiting(getClass().getName(), "getBean", bean);
        return bean;
    }

    @Override
    public <T> T getBean(Class<T> type) throws ConfigurationException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getBean", new Object[]{type});
        }

        T bean = null;
        try {
            bean = _getBean(type);
        } catch (ConfigurationException e) {
            l.throwing(getClass().getName(), "getBean", e);
            throw e;
        }

        l.exiting(getClass().getName(), "getBean", bean);
        return bean;
    }

    /**
     * Get a bean from the Application Context given the unique bean name.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public Object _getBean(String beanName) throws ConfigurationException {

        Object bean = null;
        try {
            bean = m_appCtx.getBean(beanName);
        } catch (BeansException e) {
            ConfigurationException ex = new ConfigurationException("Failed to locate the bean named '" + beanName + "'", e);
            throw ex;
        }

        return bean;
    }

    @Override
    public <T> T _getBean(Class<T> type) throws ConfigurationException {
        T bean = null;

        try {
            bean = m_appCtx.getBean(type);
        } catch (BeansException e) {
            ConfigurationException ex = new ConfigurationException("Failed to locate the bean for class: " + type.getName() + "'", e);
            throw ex;
        }

        return bean;
    }

    /**
     * Return true if the specified property exists; otherwise, false.
     */
    public boolean hasProperty(String key) throws ConfigurationException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasProperty", new Object[]{key});
        }

        boolean hasProperty = false;

        // Check if the property is found
        try {
            String value = getProperty(key);
            hasProperty = !StringUtils.isBlank(value);
        } catch (Exception e) {
            hasProperty = false;
        }

        l.exiting(getClass().getName(), "hasProperty", String.valueOf(hasProperty));
        return hasProperty;
    }

    /**
     * Return the value of the desired property.
     *
     * @param key the key for the desired property.
     * @throws ConfigurationException if no property exists by the given key.
     */
    public String getProperty(String key) throws ConfigurationException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProperty", new Object[]{key});
        }

        // Locate the PropertyPlaceholderConfigurer
        PropertyPlaceholderConfigurer propConfigurer = null;
        try {
            propConfigurer = (PropertyPlaceholderConfigurer) m_appCtx.getBean(PROPERTY_PLACEHOLDER_CONFIGURER);
        } catch (BeansException e) {
            ConfigurationException ex = new ConfigurationException("Failed to locate the PropertyPlaceholderConfigurer with id="+PROPERTY_PLACEHOLDER_CONFIGURER, e);
            l.throwing(getClass().getName(), "getBean", ex);
            throw ex;
        } catch (ClassCastException e) {
            ConfigurationException ex = new ConfigurationException("The PropertyPlaceholderConfigurer with id="+PROPERTY_PLACEHOLDER_CONFIGURER + " must be an instance or extension of dti.oasis.app.impl.PropertyPlaceholderConfigurer in order for getProperty() to work.", e);
            l.throwing(getClass().getName(), "getBean", ex);
            throw ex;
        }

        // Get the value
        String value = null;
        try {
            value = StringUtils.trimTail(propConfigurer.getProperty(key));
        } catch (Exception e) {
            ConfigurationException ex = new ConfigurationException("Failed to locate the property named '" + key + "' from the configured properties.", e);
            l.throwing(getClass().getName(), "getBean", ex);
            throw ex;
        }

        // Throw a ConfigurationException if the property value is not found.
        if (value == null) {
            ConfigurationException ex = new ConfigurationException("Failed to locate the property named '" + key + "' from the configured properties.");
            l.throwing(getClass().getName(), "getBean", ex);
            throw ex;
        }

        l.exiting(getClass().getName(), "getProperty", value);
        return value;
    }

    /**
     * Return the value of the desired property.
     *
     * @throws ConfigurationException if no property exists by the given key.
     */
    public Properties getProperties() throws ConfigurationException {
        l.entering(getClass().getName(), "getProperties");

        // Locate the PropertyPlaceholderConfigurer
        PropertyPlaceholderConfigurer propConfigurer = null;
        try {
            propConfigurer = (PropertyPlaceholderConfigurer) m_appCtx.getBean(PROPERTY_PLACEHOLDER_CONFIGURER);
        } catch (BeansException e) {
            ConfigurationException ex = new ConfigurationException("Failed to locate the PropertyPlaceholderConfigurer with id="+PROPERTY_PLACEHOLDER_CONFIGURER, e);
            l.throwing(getClass().getName(), "getBean", ex);
            throw ex;
        } catch (ClassCastException e) {
            ConfigurationException ex = new ConfigurationException("The PropertyPlaceholderConfigurer with id="+PROPERTY_PLACEHOLDER_CONFIGURER + " must be an instance or extension of dti.oasis.app.impl.PropertyPlaceholderConfigurer in order for getProperty() to work.", e);
            l.throwing(getClass().getName(), "getBean", ex);
            throw ex;
        }

        Properties props = propConfigurer.getProperties();

        l.exiting(getClass().getName(), "getProperties", props);
        return props;
    }

    /**
     * Return an Iterator or property String names.
     *
     * @return
     */
    public Iterator getPropertyNames() {
        l.entering(getClass().getName(), "getPropertyNames");

        // Locate the PropertyPlaceholderConfigurer
        PropertyPlaceholderConfigurer propConfigurer = null;
        try {
            propConfigurer = (PropertyPlaceholderConfigurer) m_appCtx.getBean(PROPERTY_PLACEHOLDER_CONFIGURER);
        } catch (BeansException e) {
            ConfigurationException ex = new ConfigurationException("Failed to locate the PropertyPlaceholderConfigurer with id="+PROPERTY_PLACEHOLDER_CONFIGURER, e);
            l.throwing(getClass().getName(), "getBean", ex);
            throw ex;
        } catch (ClassCastException e) {
            ConfigurationException ex = new ConfigurationException("The PropertyPlaceholderConfigurer with id="+PROPERTY_PLACEHOLDER_CONFIGURER + " must be an instance or extension of dti.oasis.app.impl.PropertyPlaceholderConfigurer in order for getProperty() to work.", e);
            l.throwing(getClass().getName(), "getBean", ex);
            throw ex;
        }

        l.exiting(getClass().getName(), "getPropertyNames");
        return propConfigurer.getPropertyNames();
    }

    /**
     * If there is a message source defined in the ApplicationContext, exposed it for JSTL for this request.
     */
    public void exposeMessageSourceForJstl(ServletContext servletContext, HttpServletRequest request) {
        if (ApplicationContext.getInstance().hasBean(MESSAGE_SOURCE_BEAN_NAME)) {
            MessageSource messageSource =
                (MessageSource) ApplicationContext.getInstance().getBean(MESSAGE_SOURCE_BEAN_NAME);
            JstlUtils.exposeLocalizationContext(request,
                JstlUtils.getJstlAwareMessageSource(servletContext, messageSource));
        }
    }

    /**
     * Load the Application Context from the configuration file.
     * If  System Property "application.context.config.file" is specified, it is used to locate the configuration file.
     *
     * @throws ConfigurationException
     */
    protected void load(String configurationFilename) throws ConfigurationException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "load", new Object[]{configurationFilename});
        }

        m_appCtx.load(configurationFilename);

        l.exiting(getClass().getName(), "load");
    }

    /**
     * Close the Application Context, releasing all held resources and locks.
     */
    protected void close() {
        l.entering(getClass().getName(), "close");

        m_appCtx.close();

        l.exiting(getClass().getName(), "close");
    }

    public XmlApplicationContext getXmlApplicationContext() {
        return m_appCtx;
    }


    private XmlApplicationContext m_appCtx = new XmlApplicationContext();
    private final Logger l = LogUtils.getLogger(getClass());
}
