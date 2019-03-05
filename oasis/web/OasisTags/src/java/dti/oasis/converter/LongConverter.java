package dti.oasis.converter;

import dti.oasis.util.LogUtils;

import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Converts the given value from it's type to a Long.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
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
public class LongConverter extends BaseConverter {

    public LongConverter() {
        super();
    }

    /**
     * Converts the given value from it's type to a Long.
     * <p/>
     * If a nullValue is provided, it is used as the target value if the input value is null;
     * <p/>
     *
     * @throws NumberFormatException    if the inputValue is not a Number, and its string value is not a valid Number.
     * @throws IllegalArgumentException if neither the inputValue nor the targetType is of type Long.
     */
    public Object convert(Class targetType, Object inputValue, Object nullValue) throws NumberFormatException, IllegalArgumentException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        Object convertedValue = null;
        if (Long.class.isAssignableFrom(targetType) || Long.TYPE.isAssignableFrom(targetType)) {
            if (inputValue instanceof Long) {
                convertedValue = inputValue;
            } else if (inputValue instanceof Number) {
                convertedValue = new Long(((Number) inputValue).longValue());
            } else if (inputValue instanceof Date) {
                convertedValue = new Long(((Date) inputValue).getTime());
            } else if (inputValue == null || inputValue.toString().equalsIgnoreCase("")) {
                convertedValue = nullValue;
            } else {
                convertedValue = Long.valueOf(inputValue.toString());
            }
        } else {
            throw new IllegalArgumentException("The LongConverter requires the targetType to be a java.lang.Long. Received targetType<" + targetType.getName() + ">");
        }

        l.exiting(getClass().getName(), "convert", convertedValue);
        return convertedValue;
    }

    /**
     * Returns the default target type handled by this Converter.
     */
    public Class getDefaultTargetType() {
        return Long.class;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
