package dti.oasis.converter;

import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.math.BigDecimal;

/**
 * Converts the given value from it's type to a BigDecimal.
 * <p/>
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
public class BigDecimalConverter extends BaseConverter {

    public BigDecimalConverter() {
        super();
    }

    /**
     * Converts the given value from it's type to a BigDecimal.
     * <p/>
     * If a nullValue is provided, it is used as the target value if the input value is null;
     * <p/>
     *
     * @throws NumberFormatException    if the inputValue is not a Number, and its string value is not a valid Number.
     * @throws IllegalArgumentException if neither the inputValue nor the targetType is of type BigDecimal.
     */
    public Object convert(Class targetType, Object inputValue, Object nullValue) throws NumberFormatException, IllegalArgumentException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        Object convertedValue = null;
        if (BigDecimal.class.isAssignableFrom(targetType)) {
            if (inputValue instanceof BigDecimal) {
                convertedValue = inputValue;
            } else if (inputValue instanceof Number) {
                convertedValue = new BigDecimal(((Number) inputValue).doubleValue());
            } else if (inputValue == null || inputValue.toString().equalsIgnoreCase("")) {
                convertedValue = nullValue;
            } else {
                String inputString = inputValue.toString();
                String noFormatString = FormatUtils.unformatCurrency(inputString);
                convertedValue = new BigDecimal(noFormatString);
            }
        } else {
            throw new IllegalArgumentException("The BigDecimalConverter requires the targetType to be a java.math.BigDecimal. Received targetType<" + targetType.getName() + ">");
        }

        l.exiting(getClass().getName(), "convert", convertedValue);
        return convertedValue;
    }

    /**
     * Returns the default target type handled by this Converter.
     */
    public Class getDefaultTargetType() {
        return BigDecimal.class;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
