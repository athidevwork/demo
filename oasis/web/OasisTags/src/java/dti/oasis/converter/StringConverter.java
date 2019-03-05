package dti.oasis.converter;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.BasicDynaBeanMap;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import oracle.sql.TIMESTAMP;
import oracle.xdb.XMLType;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts the given value from it's type to a String.
 * If the input value is a boolean or Boolean, it is converted to 'Y' or 'N'.
 * If the input value is a Date, it is converted using the dateFormat. The default date format is 'MM/dd/yyyy'.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/08/2010       Michael     Added function to deal with the Clob data
 * 09/21/2010       fcb         111824 - added support for Oracle XMLType
 * 01/09/2013       jxgu        140424 - added support for Oracle TIMESTAMP
 * 09/17/2015       Parker      Issue#165637 - Use ThreadLocal to make SimpleDateFormat thread safe.
 * 11/12/2018       wreeder     196160 - Support converting Number and BasicDynaBeanMap instead of defaulting to using DatabaseUtils.ClobToString() for these types
 * ---------------------------------------------------
 */
public class StringConverter extends BaseConverter {

    public StringConverter() {
        super();
        l.logp(Level.INFO, getClass().getName(), "Constructor StringConverter()", "INITIALIZED StringConverter()");
    }

    /**
     * Converts the given value from it's type to a String.
     * <p/>
     * If a nullValue is provided, it is used as the target value if the input value is null;
     * <p/>
     *
     * @throws IllegalArgumentException if neither the inputValue nor the targetType is of type String.
     */
    public Object convert(Class targetType, Object inputValue, Object nullValue) throws IllegalArgumentException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        Object convertedValue = null;
        if (String.class.isAssignableFrom(targetType)) {
            if (inputValue == null) {
                convertedValue = nullValue;
            } else if (inputValue instanceof String) {
                convertedValue = inputValue;
            } else if (inputValue instanceof Number || inputValue instanceof BasicDynaBeanMap) {
                // Moved the logic here to improve performance for these types.
                // Continue to use String.valueOf() to return null as "null" to keep backward compatible
                // to when this was handled by DatabaseUtils.ClobToString().
                convertedValue = String.valueOf(inputValue.toString());
            } else if (inputValue instanceof Boolean) {
                convertedValue = YesNoFlag.getInstance(((Boolean) inputValue).booleanValue()).getName();
            } else if (inputValue instanceof Date) {
                convertedValue = getDateFormat().format(inputValue);
            } else if (inputValue instanceof TIMESTAMP) {
                try {
                    convertedValue = getDateTimeFormat().format(((TIMESTAMP) inputValue).dateValue());
                } catch (SQLException e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to convert the TIMESTAMP to a String value.", e);
                    l.throwing(getClass().getName(), "convert", ae);
                    throw ae;
                }
            } else if (inputValue instanceof XMLType) {
                try {
                    convertedValue = ((XMLType) inputValue).getStringVal();
                } catch (SQLException e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to convert the XMLType to a String value.", e);
                    l.throwing(getClass().getName(), "convert", ae);
                    throw ae;
                }
            } else if (inputValue instanceof RecordSet) {
                if (l.isLoggable(Level.FINER))
                    l.logp(Level.FINER, getClass().getName(), "convert", "Converting RecordSet...");
                if(getLevelMarker().equals(LEVEL_0_MARKER)) {
                    try {
                        setLevelMarker(LEVEL_1_MARKER);
                        convertedValue = inputValue.toString();
                        setLevelMarker(LEVEL_0_MARKER);
                    } catch (Exception e) {
                        AppException ae = ExceptionHelper.getInstance().handleException("Failed to convert the RecordSet to a String value.", e);
                        l.throwing(getClass().getName(), "convert", ae);
                        throw ae;
                    }
                } else {
                    l.logp(Level.WARNING, getClass().getName(), "convert", "We do not support recursive RecordSet conversion to String. Only the first instance of RecordSet will be converted");
                    convertedValue = "Unsupported Nested RecordSet";
                }
            } else if (inputValue instanceof Record) {
                if (l.isLoggable(Level.FINER))
                    l.logp(Level.FINER, getClass().getName(), "convert", "Converting Record...");
                try {
                    convertedValue = inputValue.toString();
                } catch (Exception e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to convert the Record to a String value.", e);
                    l.throwing(getClass().getName(), "convert", ae);
                    throw ae;
                }
            }
            else if (inputValue.getClass().getName().toUpperCase().contains("CLOB")){
                try {
                    convertedValue = DatabaseUtils.ClobToString(inputValue);
                } catch (Exception e) {
                    throw new IllegalArgumentException("The StringConverter requires the inputValue can not be convert String");
                }
            }
            else {
                // Continue to use String.valueOf() to return null as "null" to keep backward compatible
                // to when this was handled by DatabaseUtils.ClobToString().
                convertedValue = String.valueOf(inputValue.toString());
            }
        } else {
            throw new IllegalArgumentException("The StringConverter requires the targetType to be a java.lang.String. Received targetType<" + targetType + ">");
        }

        if (l.isLoggable(Level.FINER))
            l.exiting(getClass().getName(), "convert", convertedValue);
        return convertedValue;
    }

    /**
     * Returns the default target type handled by this Converter.
     */
    public Class getDefaultTargetType() {
        return String.class;
    }

    public Format getDateFormat() {
        return m_dateFormat.get();
    }

    public void setSimpleDateFormatPattern(final String formatPattern) {
        m_dateFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(formatPattern);
            }
        };
    }

    public Format getDateTimeFormat() {
        return m_dateTimeFormat.get();
    }

    public void setSimpleDateTimeFormatPattern(final String formatPattern) {
        m_dateTimeFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(formatPattern);
            }
        };
    }

    public static String getLevelMarker() {
        return levelMarker.get();
    }

    public static void setLevelMarker(String level){
        levelMarker.set(level);
    }

    private ThreadLocal<DateFormat> m_dateFormat = DateUtils.c_dateFormat;

    private ThreadLocal<DateFormat> m_dateTimeFormat = DateUtils.c_defaultDateTimeFormat;

    private static final ThreadLocal<String> levelMarker
            = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return LEVEL_0_MARKER;
        }
    };

    private static final String LEVEL_0_MARKER = "Level 0";
    private static final String LEVEL_1_MARKER = "Level 1";

    private final Logger l = LogUtils.getLogger(getClass());
}
