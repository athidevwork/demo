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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/15/2018
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
public class ListParameterValueResolver implements ParameterValueResolver<List> {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public List resolve(List<URL> resources, Parameter parameter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolve", new Object[]{resources, parameter});
        }

        List result = null;

        String parameterPath = parameter.getAnnotation(OasisTestParameter.class).value();

        for (URL resource : resources) {
            try {
                result = JsonPath.parse(resource).read("$." + parameterPath, List.class);

            } catch (PathNotFoundException pe) {
                // It's OK to not find the element.

            } catch (IOException e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to resolve parameter: " + parameterPath, e);
                l.throwing(getClass().getName(), "resolve", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolve", result);
        }
        return result;
    }

    @Override
    public List resolve(Map parentNode, Parameter parameter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolve", new Object[]{parentNode, parameter});
        }

        String parameterPath = parameter.getAnnotation(OasisTestParameter.class).value();
        List value = (List) parentNode.get(parameterPath);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolve", value);
        }
        return value;
    }
}
