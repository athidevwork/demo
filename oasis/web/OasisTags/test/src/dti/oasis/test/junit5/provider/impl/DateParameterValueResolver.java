package dti.oasis.test.junit5.provider.impl;

import com.jayway.jsonpath.JsonPath;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Date;
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
public class DateParameterValueResolver extends BaseParameterValueResolver<Date> {
    private final Logger l = LogUtils.getLogger(getClass());

    public DateParameterValueResolver() {
        super(Date.class);
    }

    @Override
    protected Date resolveValue(URL resource, Class<Date> parameterType, String parameterPath) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolveValue", new Object[]{resource, parameterType, parameterPath});
        }

        try {
            Date date = null;

            String value = JsonPath.parse(resource).read("$." + parameterPath, String.class);
            if (!StringUtils.isBlank(value)) {
                date = DateUtils.parseDate(value);
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "resolveValue", date);
            }
            return date;
        } catch (IOException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to resolve parameter: " + parameterPath, e);
            l.throwing(getClass().getName(), "resolve", ae);
            throw ae;
        }
    }

    @Override
    public Date resolve(Map parentNode, Parameter parameter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolve", new Object[]{parentNode, parameter});
        }

        Date date = null;

        String value = (String) parentNode.get(parameter.getAnnotation(OasisTestParameter.class).value());

        if (!StringUtils.isBlank(value)) {
            date = DateUtils.parseDate(value);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolveValue", date);
        }
        return date;
    }
}
