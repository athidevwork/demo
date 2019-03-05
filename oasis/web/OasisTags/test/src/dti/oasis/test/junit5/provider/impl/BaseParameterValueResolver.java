package dti.oasis.test.junit5.provider.impl;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.util.LogUtils;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
public abstract class BaseParameterValueResolver<T> implements ParameterValueResolver<T> {
    private final Logger l = LogUtils.getLogger(getClass());
    private Class<T> parameterType;

    protected BaseParameterValueResolver(Class<T> parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public T resolve(List<URL> resources, Parameter parameter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolve", new Object[]{resources, parameter});
        }

        String parameterPath = parameter.getAnnotation(OasisTestParameter.class).value();

        T value = null;

        for (URL resource : resources) {
            try {
                value = resolveValue(resource, parameterType, parameterPath);
                break;
            } catch (PathNotFoundException pe) {
                // If the path is not found, return null.
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolve", value);
        }
        return value;
    }

    protected T resolveValue(URL resource, Class<T> parameterType, String parameterPath) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolveValue", new Object[]{resource, parameterType, parameterPath});
        }

        try {
            T value = JsonPath.parse(resource).read("$." + parameterPath, parameterType);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "resolveValue", value);
            }
            return value;
        } catch (IOException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to resolve parameter: " + parameterPath, e);
            l.throwing(getClass().getName(), "resolve", ae);
            throw ae;
        }
    }

    @Override
    public T resolve(Map parentNode, Parameter parameter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolve", new Object[]{parentNode, parameter});
        }

        String parameterPath = parameter.getAnnotation(OasisTestParameter.class).value();
        T value = (T) parentNode.get(parameterPath);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolve", value);
        }
        return value;
    }

    public static ParameterValueResolver<String> stringParameterValueResolver() {
        return new StringParameterValueResolver();
    }

    public static ParameterValueResolver<Integer> integerParameterValueResolver() {
        return new IntegerParameterValueResolver();
    }

    public static ParameterValueResolver<Long> longParameterValueResolver() {
        return new LongParameterValueResolver();
    }

    public static ParameterValueResolver<Float> floatParameterValueResolver() {
        return new FloatParameterValueResolver();
    }

    public static ParameterValueResolver<Double> doubleParameterValueResolver() {
        return new DoubleParameterValueResolver();
    }

    public static ParameterValueResolver<Boolean> booleanParameterValueResolver() {
        return new BooleanParameterValueResolver();
    }

    public static ParameterValueResolver<Date> dateParameterValueResolver() {
        return new DateParameterValueResolver();
    }
}
