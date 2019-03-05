package dti.oasis.converter;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts the given value from it's type to a Boolean.
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
public class BooleanConverter extends BaseConverter {

    public BooleanConverter() {
        super();
    }

    /**
     * Converts the given value from it's type to a Boolean.
     * <p/>
     * If a nullValue is provided, it is used as the target value if the input value is null;
     * <p/>
     *
     * @throws IllegalArgumentException if neither the inputValue nor the targetType is of type Boolean.
     */
    public Object convert(Class targetType, Object inputValue, Object nullValue) throws NumberFormatException, IllegalArgumentException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        Object convertedValue = null;
        if (Boolean.class.isAssignableFrom(targetType) || Boolean.TYPE.isAssignableFrom(targetType)) {
            if (inputValue instanceof Boolean) {
                convertedValue = inputValue;
            } else if (inputValue == null) {
                convertedValue = nullValue;
            } else {
                convertedValue = Boolean.valueOf(YesNoFlag.getInstance(inputValue.toString()).booleanValue());
            }
        } else {
            throw new IllegalArgumentException("The BooleanConverter requires the targetType to be a java.lang.Boolean. Received targetType<" + targetType + ">");
        }

        l.exiting(getClass().getName(), "convert", convertedValue);
        return convertedValue;
    }

    /**
     * Returns the default target type handled by this Converter.
     */
    public Class getDefaultTargetType() {
        return Boolean.class;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
