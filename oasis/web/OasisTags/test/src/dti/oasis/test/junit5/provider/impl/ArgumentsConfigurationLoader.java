package dti.oasis.test.junit5.provider.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.test.app.ApplicationContextHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/8/2018
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
public class ArgumentsConfigurationLoader {
    private final Logger l = LogUtils.getLogger(getClass());

    private static ArgumentsConfigurationLoader instance;

    private ArgumentsConfigurationLoader() {}

    public static ArgumentsConfigurationLoader getInstance() {
        if (instance == null) {
            synchronized (ArgumentsConfigurationLoader.class) {
                if (instance == null) {
                    instance = new ArgumentsConfigurationLoader();
                }
            }
        }

        return instance;
    }

    /**
     * Load the config files of test parameters.
     * The config file of the current environment will be loaded first.
     *
     * @param testContext
     * @return
     */
    public List<URL> load(ExtensionContext testContext) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "load", new Object[]{testContext});
        }

        String environmentName = ApplicationContextHelper.getInstance().getEnvironmentName();

        List<URL> resources = new ArrayList<>();

        ExtensionContext currentContext = testContext;
        while (currentContext != null) {
            if (currentContext.getTestClass().isPresent()) {
                Class testClass = currentContext.getTestClass().get();

                // Load environment config.
                if (!StringUtils.isBlank(environmentName)) {
                    URL resource = testClass.getResource(testClass.getSimpleName() + "-" + environmentName + ".json");

                    if (resource != null) {
                        resources.add(resource);
                    }
                }

                // Load base config.
                URL resource = testClass.getResource(testClass.getSimpleName() + ".json");

                if (resource != null) {
                    resources.add(resource);
                }
            }

            if (resources.size() > 0) {
                // Config file found.
                break;
            }

            if (currentContext.getParent().isPresent()) {
                // If config files can not be found, the current class probably is nested test class. Try to get the config file for parent test context.
                currentContext = currentContext.getParent().get();
            } else {
                break;
            }
        }

        if (resources.size() == 0) {
            throw new ConfigurationException("Can't not load test parameter configuration file for: " + testContext.getTestClass().get().getName());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "load", resources);
        }
        return resources;
    }
}
