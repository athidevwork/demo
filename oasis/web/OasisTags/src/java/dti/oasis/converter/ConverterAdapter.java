package dti.oasis.converter;

import dti.oasis.util.LogUtils;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This Class allows a Jakarta Commons Beanutils type of Converter to be used as a dti.oasis.converter.Converter.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ConverterAdapter implements Converter {

    public ConverterAdapter(org.apache.commons.beanutils.Converter converter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "ConverterAdapter", new Object[]{converter});
        }

        m_converter = converter;

        l.exiting(getClass().getName(), "ConverterAdapter");
    }

    /**
     * Converts the given value from it's type to an object of the specified target type.
     */
    public Object convert(Class targetType, Object inputValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue});
        }

        Object convertedValue = m_converter.convert(targetType, inputValue);

        l.exiting(getClass().getName(), "convert", convertedValue);
        return convertedValue;
    }

    /**
     * Converts the given value from it's type to an object of the specified target type.
     * <p/>
     * The provided nullValue is used as the target value if the input value is null.
     */
    public Object convert(Class targetType, Object inputValue, Object nullValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        Object retVal = convert(targetType, inputValue);
        retVal = (retVal == null ? nullValue : retVal);

        l.exiting(getClass().getName(), "convert", retVal);
        return retVal;
    }

    /**
     * Converts the given value from it's type to an object of the default target type.
     * <p/>
     * The provided nullValue is used as the target value if the input value is null.
     */
    public Object convert(Object inputValue, Object nullValue) {
        return convert(getDefaultTargetType(), inputValue, nullValue);
    }

    /**
     * Converts the given value from it's type to an object of the default target type.
     * <p/>
     * If the input value is null, the returned value is null.
     */
    public Object convert(Object inputValue) {
        return convert(getDefaultTargetType(), inputValue);
    }

    /**
     * Returns the default target type handled by this Converter.
     */
    public Class getDefaultTargetType() {
        return String.class;
    }

    private org.apache.commons.beanutils.Converter m_converter;
    private final Logger l = LogUtils.getLogger(getClass());
}
