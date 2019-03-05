package dti.oasis.test.junit5.provider.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.util.LogUtils;

import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.List;
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
public class ParameterResolverFactory {
    private final Logger l = LogUtils.getLogger(getClass());

    private static ParameterResolverFactory instacne;

    private ParameterResolverFactory() {}

    public static ParameterResolverFactory getInstacne() {
        if (instacne == null) {
            synchronized (ParameterResolverFactory.class) {
                if (instacne == null) {
                    instacne = new ParameterResolverFactory();
                }
            }
        }
        return instacne;
    }

    public ParameterValueResolver getResolver(Parameter parameter) {
        if (parameter.getAnnotation(OasisTestParameter.class) == null) {
            throw new ConfigurationException("Parameter must be annotated with OasisTestParameter");
        }

        Class parameterType = parameter.getType();

        if (parameterType.equals(String.class)) {
            return BaseParameterValueResolver.stringParameterValueResolver();

        } else if (parameterType.equals(Integer.class) || parameterType.equals(int.class)) {
            return BaseParameterValueResolver.integerParameterValueResolver();

        } else if (parameterType.equals(Long.class) || parameterType.equals(long.class)) {
            return BaseParameterValueResolver.longParameterValueResolver();

        } else if (parameterType.equals(Float.class) || parameterType.equals(float.class)) {
            return BaseParameterValueResolver.floatParameterValueResolver();

        } else if (parameterType.equals(Double.class) || parameterType.equals(double.class)) {
            return BaseParameterValueResolver.doubleParameterValueResolver();

        } else if (parameterType.equals(Boolean.class) || parameterType.equals(boolean.class)) {
            return BaseParameterValueResolver.booleanParameterValueResolver();

        } else if (parameterType.equals(Date.class)) {
            return BaseParameterValueResolver.dateParameterValueResolver();

        } else if (parameterType.equals(Record.class)) {
            return new RecordParameterValueResolver();

        } else if (parameterType.equals(RecordSet.class)) {
            return new RecordSetParameterValueResolver();

        } else if (parameterType.equals(List.class)) {
            return new ListParameterValueResolver();

        } else {
            throw new ConfigurationException("Unsupported type for parameterized test: " + parameterType.getName());
        }
    }
}
