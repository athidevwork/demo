package dti.oasis.test.junit5.provider.impl;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.RecordSet;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.util.LogUtils;
import net.minidev.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.net.URL;
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
public class RecordSetParameterValueResolver implements ParameterValueResolver<RecordSet> {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public RecordSet resolve(List<URL> resources, Parameter parameter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolve", new Object[]{resources, parameter});
        }

        RecordSet rs = new RecordSet();

        String parameterPath = parameter.getAnnotation(OasisTestParameter.class).value();

        if (resources.size() > 0) {
            for (int i = resources.size() - 1; i >= 0; i--) {
                URL resource = resources.get(i);

                try {
                    JSONArray dataArray = JsonPath.parse(resource).read("$." + parameterPath);

                    for (Object data: dataArray) {
                        Map map = (Map) data;

                        rs.addRecord(RecordHelper.mapToRecord(map));
                    }

                    // Found record set obj.
                    break;
                } catch (PathNotFoundException pe) {
                    // It's OK to not find the element.

                } catch (IOException e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to resolve parameter: " + parameterPath, e);
                    l.throwing(getClass().getName(), "resolve", ae);
                    throw ae;
                }

            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolve", rs);
        }
        return rs;
    }

    @Override
    public RecordSet resolve(Map parentNode, Parameter parameter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolve", new Object[]{parentNode, parameter});
        }

        RecordSet rs = new RecordSet();

        String parameterPath = parameter.getAnnotation(OasisTestParameter.class).value();
        List dataArray = (List) parentNode.get(parameterPath);

        for (Object data: dataArray) {
            Map map = (Map) data;

            rs.addRecord(RecordHelper.mapToRecord(map));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolve", rs);
        }
        return rs;
    }
}
