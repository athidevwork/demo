package dti.oasis.converter;

import dti.oasis.util.LogUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts the given value from it's type to the target type
 * if the target type has a public constructor that takes a String parameter,
 * or the target type has a public getInstance factory method that takes a String parameter and returns the target type.
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
public class DefaultConverter extends BaseConverter {

    public DefaultConverter() {
        super();
    }

    /**
     * Converts the given value from it's type to the target type
     * if the target type has a public constructor that takes a String parameter,
     * or the target type has a public getInstance factory method that takes a String parameter and returns the target type.
     * <p/>
     * If a nullValue is provided, it is used as the target value if the input value is null;
     * <p/>
     *
     * @throws IllegalArgumentException if either the target type has no public constructor that takes a String parameter,
     *                                  and the target type has no public getInstance factory method that takes a String parameter and returns the target type.
     */
    public Object convert(Class targetType, Object inputValue, Object nullValue) throws IllegalArgumentException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        boolean foundConvertedValue = false;
        Object convertedValue = null;
        if (inputValue == null) {
            convertedValue = nullValue;
            foundConvertedValue = true;
        }
        else {
            try {
                // Look for a public constructor that takes a String parameter
                Constructor constructor = targetType.getDeclaredConstructor(new Class[]{String.class});
                String valueString = (String) getStringConverter().convert(inputValue, null);
                convertedValue = constructor.newInstance(new Object[]{valueString});
                foundConvertedValue = true;
            }
            catch (Exception e) {
                // Did not find a constructor that takes a String parameter.
            }

            if (!foundConvertedValue) {
                try {
                    // Look for a public getInstance factory method that takes a String parameter and returns the target type
                    Method getInstanceMethod = targetType.getDeclaredMethod("getInstance", new Class[]{String.class});
                    if (Modifier.isStatic(getInstanceMethod.getModifiers())) {
                        String valueString = (String) getStringConverter().convert(inputValue, null);
                        convertedValue = getInstanceMethod.invoke(targetType, new Object[]{valueString});
                        foundConvertedValue = true;
                    }
                }
                catch (Exception e) {
                    // Did not find a static getInstance(String) method in this class.
                }
            }
        }

        if (!foundConvertedValue) {
            throw new IllegalArgumentException("The DefaultConverter requires the targetType to have a public constructor that takes a String parameter," +
                " or have a public getInstance factory method that takes a String parameter and returns the target type<" + targetType.getName() + ">");
        }

        l.exiting(getClass().getName(), "convert", convertedValue);
        return convertedValue;
    }

    /**
     * Returns the default target type handled by this Converter.
     */
    public Class getDefaultTargetType() {
        return Object.class;
    }

    protected Converter getStringConverter() {
        if (m_stringConverter == null) {
            m_stringConverter = ConverterFactory.getInstance().getConverter(String.class);
        }
        return m_stringConverter;
    }

    private Converter m_stringConverter;
    private final Logger l = LogUtils.getLogger(getClass());
}
