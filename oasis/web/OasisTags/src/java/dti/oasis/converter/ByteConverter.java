package dti.oasis.converter;

import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts the given value from it's type to a Byte.
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
public class ByteConverter extends BaseConverter {

    public ByteConverter() {
        super();
    }

    /**
     * Converts the given value from it's type to a Byte.
     * <p/>
     * If a nullValue is provided, it is used as the target value if the input value is null;
     * <p/>
     *
     * @throws NumberFormatException    if the inputValue is not a Number, and its string value is not a valid Number.
     * @throws IllegalArgumentException if neither the inputValue nor the targetType is of type Byte.
     */
    public Object convert(Class targetType, Object inputValue, Object nullValue) throws NumberFormatException, IllegalArgumentException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        Object convertedValue = null;
        if (Byte.class.isAssignableFrom(targetType) || Byte.TYPE.isAssignableFrom(targetType)) {
            if (inputValue instanceof Byte) {
                convertedValue = inputValue;
            } else if (inputValue instanceof Number) {
                convertedValue = new Byte(((Number) inputValue).byteValue());
            } else if (inputValue == null || inputValue.toString().equals("")) {
                convertedValue = nullValue;
            } else {
                convertedValue = Byte.valueOf(inputValue.toString());
            }
        } else {
            throw new IllegalArgumentException("The ByteConverter requires the targetType to be a java.lang.Byte. Received targetType<" + targetType.getName() + ">");
        }

        l.exiting(getClass().getName(), "convert", convertedValue);
        return convertedValue;
    }

    /**
     * Returns the default target type handled by this Converter.
     */
    public Class getDefaultTargetType() {
        return Byte.class;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
