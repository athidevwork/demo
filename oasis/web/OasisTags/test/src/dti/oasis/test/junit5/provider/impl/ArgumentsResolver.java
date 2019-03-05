package dti.oasis.test.junit5.provider.impl;

import com.jayway.jsonpath.*;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
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
public class ArgumentsResolver {
    private final Logger l = LogUtils.getLogger(getClass());

    private static ArgumentsResolver instance;

    private ArgumentsResolver() {
    }

    public static ArgumentsResolver getInstance() {
        if (instance == null) {
            synchronized (ArgumentsResolver.class) {
                if (instance == null) {
                    instance = new ArgumentsResolver();
                }
            }
        }
        return instance;
    }

    public Arguments[] resolve(ExtensionContext context) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolve", new Object[]{context});
        }

        Arguments[] argumentsArray;

        // Load Config files.
        List<URL> configResources = ArgumentsConfigurationLoader.getInstance().load(context);

        // Get test method parameters.
        Parameter[] parameters = context.getTestMethod().get().getParameters();

        // Get parent node path of test parameters.
        String parentNodePath = getParentNodePath(context);

        if (StringUtils.isBlank(parentNodePath)) {
            // Resolve arguments without parent path.
            argumentsArray = resolveArguments(context, configResources, parameters);
        } else {
            // Resolve arguments with parent path.
            argumentsArray = resolveArguments(context, configResources, parameters, parentNodePath);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolve", argumentsArray);
        }
        return argumentsArray;
    }

    protected String getParentNodePath(ExtensionContext context) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getParentNodePath", new Object[]{context});
        }

        String parentNodePath = null;

        if (context.getTestMethod().get().getAnnotation(OasisParameterizedTest.class) != null) {
            parentNodePath = context.getTestMethod().get().getAnnotation(OasisParameterizedTest.class).value();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getParentNodePath", parentNodePath);
        }
        return parentNodePath;
    }

    /**
     * Process test parameters without parent path.
     *
     * @param context
     * @param configResources
     * @param parameters
     * @return
     */
    protected Arguments[] resolveArguments(ExtensionContext context, List<URL> configResources, Parameter[] parameters) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolveArguments", new Object[]{context, configResources, parameters});
        }

        Object[] values = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            // Get parameter value resolver.
            ParameterValueResolver parameterValueResolver = ParameterResolverFactory.getInstacne().getResolver(parameter);

            // Resolver value.
            values[i] =  parameterValueResolver.resolve(configResources, parameter);
        }

        Arguments[] argumentsArray = new Arguments[]{Arguments.of(values)};

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolveArguments", argumentsArray);
        }
        return argumentsArray;
    }

    /**
     * Process test parameters with parent path.
     * @param context
     * @param configResources
     * @param parameters
     * @param parentNodePath
     * @return
     */
    protected Arguments[] resolveArguments(ExtensionContext context, List<URL> configResources, Parameter[] parameters, String parentNodePath) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolveArguments", new Object[]{context, configResources, parameters, parentNodePath});
        }

        List<Arguments> argumentsList = new ArrayList<>();

        Object parentNode = getParentNode(context, configResources, parentNodePath);

        if (parentNode instanceof List) {
            // Configured multiple sets of test parameters.
            List<Map> nodeList = (List<Map>) parentNode;

            for (Map node : nodeList) {
                // Resolve arguments.
                argumentsList.add(resolveArguments(node, parameters));
            }

        } else {
            Map node = (Map) parentNode;
            // Resolve arguments.
            argumentsList.add(resolveArguments(node, parameters));
        }

        Arguments[] argumentsArray = argumentsList.toArray(new Arguments[0]);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolveArguments", argumentsArray);
        }
        return argumentsArray;
    }

    /**
     * Get the parent node of test parameters.
     *
     * @param context
     * @param resources
     * @return
     */
    protected Object getParentNode(ExtensionContext context, List<URL> resources, String parentPath) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getParentNode", new Object[]{context, resources, parentPath});
        }

        Object parentNode = null;

        for (URL resource : resources) {
            try {
                // Get parent node.
                parentNode = JsonPath.parse(resource).read("$." + parentPath);

                if (parentNode != null) {
                    // Parent node founded.
                    break;
                }
            } catch (PathNotFoundException pe) {
                // If the path is not found, process the next resource file.
            } catch (IOException e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse test parameter config files.", e);
                l.throwing(getClass().getName(), "getParentNode", ae);
                throw ae;
            }
        }

        if (parentNode == null) {
            // Parent node not found in all config files.
            ConfigurationException ce = new ConfigurationException("Failed to parse test parameter config files.");
            l.throwing(getClass().getName(), "getParentNode", ce);
            throw ce;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getParentNode", parentNode);
        }
        return parentNode;
    }

    protected Arguments resolveArguments(Map node, Parameter[] parameters) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolveArguments", new Object[]{node, parameters});
        }

        Object[] values = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            ParameterValueResolver parameterValueResolver = ParameterResolverFactory.getInstacne().getResolver(parameter);

            values[i] = parameterValueResolver.resolve(node, parameter);
        }

        Arguments arguments = Arguments.of(values);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolveArguments", arguments);
        }
        return arguments;
    }
}
