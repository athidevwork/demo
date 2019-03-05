package dti.oasis.app;

import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.Name;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * This class provides access to the Beans and Properties associated configured for this Application.
 * All beans that are configured through the ApplicationContext should expose a verifyConfig method,
 * that is invoked after configuring all beans to verify that all required properties are defined.
 * If any required properties are missing, a ConfigurationException must be thrown
 * that tells what required property is missing. For example,
 * <p/><code>public void verifyConfig() {
 * <p/>&nbsp;&nbsp;&nbsp;&nbsp;if (getMaxRows() == null)
 * <p/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new ConfigurationException("The required property 'maxRows' is missing.");
 * <p/>}</code>
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
 * 04/08/2008       James       Add last started time
 * 03/07/2009       Fred        Remove the blank spaces at the end of the property value
 * 06/22/2018       ylu         according code change for Form Letter refactor.
 * 06/28/2018       dpang       Added instance variable m_buildNumberParam.
 * ---------------------------------------------------
 */
public abstract class ApplicationContext {

    /**
     * The default name of the application context configuration file.
     */
    public static String DEFAULT_APPLICATION_CONTEXT_CONFIG_FILE = "dti/applicationConfig.xml";
    /**
     * The name of the System Property used to override the default application context configuration file name.
     */
    public static String APPLICATION_CONTEXT_CONFIG_FILE_SYSTEM_PROPERTY = "application.context.config.file";
    /**
     * The name of the dti.oasis.app.impl.PropertyPlaceholderConfigurer defined to handle property placeholders.
     */
    public String PROPERTY_PLACEHOLDER_CONFIGURER = "PropertyPlaceholderConfigurer";

    /**
     * The name of the message source bean defined in the Application Context.
     */
    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";

    /**
     * Determines if the ApplicationContext class has been initialized yet.
     *
     * @return true if it has been initialized. Otherwise, false.
     */
    public static boolean isInitialized() {
        return c_instance != null;
    }

    /**
     * Get an instance of the ApplicationContext.
     * If a concrete extension of the ApplicationContext is not specifically created, the DefaultApplicationContext is used.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public static ApplicationContext getInstance() {
        if (c_instance == null) {
            c_instance = new DefaultApplicationContext();
        }
        return c_instance;
    }

    /**
     * Return the name of this application.
     */
    public String getApplicationName() {
        return m_applicationName;
    }


    /**
     * Return the server name.
     */
    public String getServerName() {
        return m_serverName;
    }

    /**
     * use this method to get build number, when the "buildNumberParam" cannot be shared in csCommon.jsp
     * @return
     */
    public String getBuildNumberParameter() {
        if (m_buildNumberParam == null) {
            synchronized (this) {
                if (m_buildNumberParam == null) {
                    m_buildNumberParam = "buildNumber=" + getProperty(IOasisAction.KEY_BUILD_NUMBER);
                }
            }
        }
        return m_buildNumberParam;
    }

    /**
     * get the last refresh time
     * If the application is not refreshed, return the last start/redeploy time.
     *
     * Note: ApplicationContext properties and beans are not refreshed, only the Sys Parms, LOVs and Navigation
     * @return
     */
    public Date getLastRefreshTime() {
        return m_lastRefreshTime;
    }

    /**
     * Set the last refresh time
     *
     * Note: ApplicationContext properties and beans are not refreshed, only the Sys Parms, LOVs and Navigation
     * @param lastRefreshTime
     */
    public void setLastRefreshTime(Date lastRefreshTime) {
        m_lastRefreshTime = lastRefreshTime;
    }

    /**
     * Returns true if the specified bean exists; otherwise, false.
     */
    public abstract boolean hasBean(String beanName);

    /**
     * Returns true if the specified bean exists; otherwise, false.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public abstract boolean _hasBean(String beanName);

    /**
     * Get a bean from the Application Context given the unique bean name.
     *
     * @param beanName the name of the bean to retrieve
     * @return the desired bean
     * @throws dti.oasis.app.ConfigurationException if no bean exists by the given name.
     */
    public abstract Object getBean(String beanName) throws ConfigurationException;

    /**
     * Get a bean from the Application Context give the bean type
     * @param type
     * @param <T>
     * @return
     * @throws ConfigurationException
     */
    public abstract <T> T getBean(Class<T> type) throws ConfigurationException;

    /**
     * Get a bean from the Application Context given the unique bean name.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public abstract Object _getBean(String beanName) throws ConfigurationException;

    /**
     * Get a bean from the Application Context given the bean type.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    public abstract <T> T _getBean(Class<T> type) throws ConfigurationException;

    /**
     * Return true if the specified property exists; otherwise, false.
     */
    public abstract boolean hasProperty(String key) throws ConfigurationException;

    /**
     * Return the value of the desired property.
     *
     * @param key the key for the desired property.
     * @throws ConfigurationException if no property exists by the given key.
     */
    public abstract String getProperty(String key) throws ConfigurationException;


    /**
     * Return all configuration properties.
     *
     * @throws ConfigurationException if no property exists by the given key.
     */
    public abstract Properties getProperties() throws ConfigurationException;

    /**
     * Return an Iterator or property String names.
     * @return
     */
    public abstract Iterator getPropertyNames();

    /**
     * Return the value of the desired property.
     *
     * @param key the key for the desired property.
     * @param defaultValue the default value if the key is not found, or is bound to null.
     */
    public String getProperty(String key, String defaultValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProperty", new Object[]{key, defaultValue});
        }

        String value = null;
        try {
            value = getProperty(key);
        } catch (ConfigurationException e) {
        }

        value = StringUtils.isBlank(value) ? defaultValue : value;

        l.exiting(getClass().getName(), "getProperty", value);
        return value;
    }

    /**
     * If there is a message source defined in the ApplicationContext, exposed it for JSTL for this request.
     */
    public abstract void exposeMessageSourceForJstl(ServletContext servletContext, HttpServletRequest request);

    /**
     * Load the Application Context from the configuration file.
     * If  System Property "application.context.config.file" is specified, it is used to locate the configuration file.
     *
     * @throws ConfigurationException
     */
    public void load() throws ConfigurationException {
        l.entering(getClass().getName(), "load");

        String configFilename = System.getProperty(APPLICATION_CONTEXT_CONFIG_FILE_SYSTEM_PROPERTY);
        if (configFilename == null) {
            configFilename = DEFAULT_APPLICATION_CONTEXT_CONFIG_FILE;
        }
        load(configFilename);

        l.exiting(getClass().getName(), "load");
    }

    /**
     * Load the Application Context from the named configuration file.
     *
     * @param configurationFilename
     * @throws ConfigurationException
     */
    protected abstract void load(String configurationFilename) throws ConfigurationException;

    /**
     * Constructor. Store a reference to the concrete implementation of this class.
     *
     * This method does not log any messages so it can be used by the Logger
     */
    protected ApplicationContext(String applicationName) {
        m_applicationName = applicationName;
        c_instance = this;
        try {
            m_serverName = System.getProperty("weblogic.Name");
        } catch (Exception e) {
            m_serverName = "";
        }

        System.out.println("ApplicationContext is now running as'"+this.getClass().getName()+"'");
    }

    private String m_buildNumberParam;
    private String m_applicationName;
    private String m_serverName;
    /**
     * The last time the SysParmProvider was refreshed by the refreshparms.jsp page.
     *  If it was not refreshed, the last start/deployed time
     */
    private Date m_lastRefreshTime;

    private static ApplicationContext c_instance;
    private final Logger l = LogUtils.getLogger(getClass());
}

class DefaultApplicationContext extends ApplicationContext {

    public static final String DEFAULT_APPLICATION_NAME = "OASIS";

    protected DefaultApplicationContext() {
        super(DEFAULT_APPLICATION_NAME);
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
        boolean beanExists = false;
        try {
            beanExists = getEnvContext().lookup(beanName) != null;
        } catch (NamingException e) {
            beanExists = false;
        }

        return beanExists;
    }

    /**
     * Get a bean from the Application Context given the unique bean name.
     *
     * @param beanName the name of the bean to retrieve
     * @return the desired bean
     * @throws ConfigurationException
     *          if no bean exists by the given name.
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

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getBean", bean);
        }
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
            bean = getEnvContext().lookup(beanName);
        } catch (NamingException e) {
            ConfigurationException ex = new ConfigurationException("Failed to locate the bean '"+beanName+"' in  the 'java:comp/env' InitialContext", e);
            throw ex;
        }

        return bean;
    }

    @Override
    public <T> T _getBean(Class<T> clazz) throws ConfigurationException {
        T bean = null;
        try {
            bean = (T) getEnvContext().lookup(clazz.getName());
        } catch (NamingException e) {
            ConfigurationException ex = new ConfigurationException("Failed to locate the bean '"+clazz.getName()+"' in  the 'java:comp/env' InitialContext", e);
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
     * @throws ConfigurationException
     *          if no property exists by the given key.
     */
    public String getProperty(String key) throws ConfigurationException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProperty", new Object[]{key});
        }

        String value = StringUtils.trimTail((String) getBean(key));

        l.exiting(getClass().getName(), "getProperty", value);
        return value;
    }

    /**
     * Return the value of the desired property.
     *
     * @throws ConfigurationException
     *          if no property exists by the given key.
     */
    public Properties getProperties() throws ConfigurationException {
        l.entering(getClass().getName(), "getProperties");

        Properties props = new Properties();
        Iterator it = this.getPropertyNames();
        while(it.hasNext()){
            String propName = (String) it.next();
            String property = this.getProperty(propName);
            props.setProperty(propName, property);
        }

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

        List propertyNames = new ArrayList();
        try {
            NamingEnumeration namingEnum = new InitialContext().list("java:comp/env");
            while (namingEnum.hasMoreElements()) {
                Name name = (Name) namingEnum.nextElement();
                propertyNames.add(name.toString());
            }
        } catch (NamingException e) {
            ConfigurationException ex = new ConfigurationException("Failed to locate the property names in  the 'java:comp/env' InitialContext", e);
            l.throwing(getClass().getName(), "getInitialContext", ex);
            throw ex;
        }


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPropertyNames", propertyNames);
        }
        return propertyNames.iterator();
    }

    /**
     * If there is a message source defined in the ApplicationContext, exposed it for JSTL for this request.
     */
    public void exposeMessageSourceForJstl(ServletContext servletContext, HttpServletRequest request) {
        // Do nothing by default because the MessageSource
    }

    /**
     * Load the Application Context from the named configuration file.
     *
     * @param configurationFilename
     * @throws ConfigurationException
     *
     */
    protected void load(String configurationFilename) throws ConfigurationException {
        // Do Nothing.
    }

    /**
     * This method does not log any messages so it can be used by the Logger
     */
    protected Context getEnvContext() throws NamingException {
        Context env = (Context) new InitialContext().lookup("java:comp/env");
        return env;
    }
    private final Logger l = LogUtils.getLogger(getClass());
}
