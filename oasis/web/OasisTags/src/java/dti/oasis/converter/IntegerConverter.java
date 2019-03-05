package dti.oasis.converter;

import dti.oasis.util.LogUtils;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Converts the given value from it's type to a Integer.
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
public class IntegerConverter extends BaseConverter {

    public IntegerConverter() {
        super();
    }

    /**
     * Converts the given value from it's type to a Integer.
     * <p/>
     * If a nullValue is provided, it is used as the target value if the input value is null;
     * <p/>
     *
     * @throws NumberFormatException    if the inputValue is not a Number, and its string value is not a valid Number.
     * @throws IllegalArgumentException if neither the inputValue nor the targetType is of type Integer.
     */
    public Object convert(Class targetType, Object inputValue, Object nullValue) throws NumberFormatException, IllegalArgumentException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        Object convertedValue = null;
        if (Integer.class.isAssignableFrom(targetType) || Integer.TYPE.isAssignableFrom(targetType)) {
            if (inputValue instanceof Integer) {
                convertedValue = inputValue;
            } else if (inputValue instanceof Number) {
                convertedValue = new Integer(((Number) inputValue).intValue());
            } else if (inputValue == null || inputValue.toString().equalsIgnoreCase("")) {
                convertedValue = nullValue;
            } else {
                convertedValue = Integer.valueOf(inputValue.toString());
            }
        } else {
            throw new IllegalArgumentException("The IntegerConverter requires the targetType to be a java.lang.Integer. Received targetType<" + targetType.getName() + ">");
        }

        l.exiting(getClass().getName(), "convert", convertedValue);
        return convertedValue;
    }

    /**
     * Returns the default target type handled by this Converter.
     */
    public Class getDefaultTargetType() {
        return Integer.class;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
