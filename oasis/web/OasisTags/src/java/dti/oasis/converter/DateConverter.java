package dti.oasis.converter;

import dti.oasis.struts.ActionHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;


import java.util.Base64;
import java.util.Date;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Converts the given value from it's type to a Date.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/04/2017       cesar       #188804 - Modified convert() to call ActionHelper.isBase64 to decode field if is masked.
  * ---------------------------------------------------
 */
public class DateConverter extends BaseConverter {

    public DateConverter() {
        super();
    }

    /**
     * Converts the given value from it's type to a Date.
     * <p/>
     * If a nullValue is provided, it is used as the target value if the input value is null;
     * <p/>
     *
     * @throws NumberFormatException    if the inputValue is not a Number, and its string value is not a valid Number.
     * @throws IllegalArgumentException if neither the inputValue nor the targetType is of type Date.
     */
    public Object convert(Class targetType, Object inputValue, Object nullValue) throws NumberFormatException, IllegalArgumentException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        Object convertedValue = null;
        if (Date.class.isAssignableFrom(targetType)) {
            if (inputValue instanceof Date) {
                convertedValue = inputValue;
            } else if (inputValue instanceof Calendar) {
                convertedValue = ((Calendar) inputValue).getTime();
            } else if (inputValue instanceof Number) {
                convertedValue = new Date(((Number) inputValue).longValue());
            } else if (inputValue == null || inputValue.toString().equalsIgnoreCase("")) {
                convertedValue = nullValue;
            } else {
                if (ActionHelper.isBase64(inputValue)) {
                    inputValue = ActionHelper.decodeField(inputValue);
                }
                convertedValue = DateUtils.parseDateTime(inputValue.toString());
            }
        } else {
            throw new IllegalArgumentException("The DateConverter requires the targetType to be a java.lang.Date or have a String value that is parseable by java.util.Date.parse(). Received targetType<" + targetType.getName() + ">");
        }

        l.exiting(getClass().getName(), "convert", convertedValue);
        return convertedValue;
    }

    /**
     * Returns the default target type handled by this Converter.
     */
    public Class getDefaultTargetType() {
        return Date.class;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
