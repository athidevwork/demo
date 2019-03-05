package dti.oasis.test.app;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.impl.ApplicationLifecycleAdvisorImpl;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/27/2018
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public abstract class ApplicationContextHelper {
    private static ApplicationContextHelper instance = null;

    public static ApplicationContextHelper getInstance() {
        if (instance == null) {
            synchronized (ApplicationContextHelper.class) {
                if (instance == null) {
                    instance = new DefaultApplicationContextHelper();
                    instance.init();
                }
            }
        }

        return instance;
    }

    public abstract void init();

    public abstract ApplicationContext getApplicationContext();

    public abstract String getEnvironmentName();
}

class DefaultApplicationContextHelper extends ApplicationContextHelper {
    private static final String APPLICATION_NAME = "application.name";
    private static final String APPLICATION_DEFAULT_NAME = "ApplicationConfigTest";
    private static final String APPLICATION_CONFIG_FILE_NAME = "dti/oasisTagsTestConfig.xml";
    private static ApplicationLifecycleAdvisorImpl advisor;

    @Override
    public void init() {
        if (advisor == null) {
            String appName = System.getProperty(APPLICATION_NAME);
            if (appName == null || appName.length() <= 0) {
                appName = APPLICATION_DEFAULT_NAME;
            }

            System.setProperty(ApplicationContext.APPLICATION_CONTEXT_CONFIG_FILE_SYSTEM_PROPERTY, APPLICATION_CONFIG_FILE_NAME);

            advisor = new ApplicationLifecycleAdvisorImpl();
            advisor.initialize(appName);
        }
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return ApplicationContext.getInstance();
    }

    @Override
    public String getEnvironmentName() {
        return getApplicationContext().getProperty("test.environment.name", "");
    }
}
