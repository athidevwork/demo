package dti.oasis.converter;

import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts the given value from it's Date or String in DateTime format to a String in Date format.
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
 * 09/17/2015       Parker      Issue#165637 - Use ThreadLocal to make SimpleDateFormat thread safe. *
 *
 * ---------------------------------------------------
 */
public class DateTimeToDateStringConverter extends BaseConverter {

    public DateTimeToDateStringConverter() {
        super();
    }

    /**
     * Converts the given value from it's type to a String.
     * <p/>
     * If a nullValue is provided, it is used as the target value if the input value is null;
     * <p/>
     *
     * @throws IllegalArgumentException if neither the inputValue nor the targetType is of type String.
     */
    public Object convert(Class targetType, Object inputValue, Object nullValue) throws NumberFormatException, IllegalArgumentException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        Object convertedValue = null;
        if (String.class.isAssignableFrom(targetType)) {
            if (inputValue == null) {
                convertedValue = nullValue;
            } else if (inputValue instanceof String) {
                try {
                    convertedValue = getDateFormat().format(getDatetimeFormat().parseObject(inputValue.toString()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException("The DateTimeToDateStringConverter requires the inputValue to be either a java.util.Date or a java.lang.String in the format \""+getDatetimeFormatPattern()+"\". Received inputValue<" + inputValue + ">");
                }
            } else if (inputValue instanceof Date) {
                convertedValue = getDateFormat().format(inputValue);
            } else {
                throw new IllegalArgumentException("The DateTimeToDateStringConverter requires the inputValue to be either a java.util.Date or a java.lang.String in the format \""+getDatetimeFormatPattern()+"\". Received inputValue<" + inputValue + ">");
            }
        } else {
            throw new IllegalArgumentException("The DateTimeToDateStringConverter requires the targetType to be a java.lang.String. Received targetType<" + targetType + ">");
        }
        l.exiting(getClass().getName(), "convert", convertedValue);
        return convertedValue;
    }

    /**
     * Returns the default target type handled by this Converter.
     */
    public Class getDefaultTargetType() {
        return String.class;
    }

    public String getDatetimeFormatPattern() {
        return m_datetimeFormatPattern;
    }

    public void setDatetimeFormatPattern(String datetimeFormatPattern) {
        m_datetimeFormatPattern = datetimeFormatPattern;
        setDatetimeFormat(datetimeFormatPattern);
    }

    protected Format getDatetimeFormat() {
        return m_datetimeFormat.get();
    }

    protected void setDatetimeFormat(final String datetimeFormatPattern) {
        m_datetimeFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(datetimeFormatPattern);
            }
        };
    }

    public String getDateFormatPattern() {
        return m_dateFormatPattern;
    }

    public void setDateFormatPattern(String dateFormatPattern) {
        m_dateFormatPattern = dateFormatPattern;
        setDateFormat(dateFormatPattern);
    }

    protected Format getDateFormat() {
        return m_dateFormat.get();
    }

    protected void setDateFormat(final String dateFormatPattern) {
        m_dateFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(dateFormatPattern);
            }
        };
    }

    private String m_datetimeFormatPattern = DateUtils.DATETIME_FORMAT_PATTERN;
    private ThreadLocal<DateFormat> m_datetimeFormat = DateUtils.c_dateTimeFormat;
    private String m_dateFormatPattern = DateUtils.DATE_FORMAT_PATTERN;
    private ThreadLocal<DateFormat> m_dateFormat = DateUtils.c_dateFormat;
    private final Logger l = LogUtils.getLogger(getClass());
}
