package dti.oasis.test.junit5.provider.impl;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.util.LogUtils;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class RecordParameterValueResolver implements ParameterValueResolver<Record> {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public Record resolve(List<URL> resources, Parameter parameter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolve", new Object[]{resources, parameter});
        }

        Record record = new Record();

        String parameterPath = parameter.getAnnotation(OasisTestParameter.class).value();

        for (URL resource : resources) {
            try {
                Map fieldMap = JsonPath.parse(resource).read("$." + parameterPath);

                RecordHelper.setRecordValues(record, fieldMap);
            } catch (PathNotFoundException pe) {
                // It's OK to not find the element.

            } catch (IOException e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to resolve parameter: " + parameterPath, e);
                l.throwing(getClass().getName(), "resolve", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolve", record);
        }
        return record;
    }

    @Override
    public Record resolve(Map parentNode, Parameter parameter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolve", new Object[]{parentNode, parameter});
        }

        String parameterPath = parameter.getAnnotation(OasisTestParameter.class).value();
        Map map = (Map) parentNode.get(parameterPath);

        Record record = RecordHelper.mapToRecord(map);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolve", record);
        }
        return record;
    }
}
