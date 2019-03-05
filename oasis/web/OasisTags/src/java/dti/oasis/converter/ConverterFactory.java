package dti.oasis.converter;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigDecimal;

/**
 * This class provides access to Converters registered to handle conversion of an object to the specified type.
 * All primitive type conversions are handled by the corersponding wrapper type Converter
 * (ex. int conversions are handled by the converter registered to handle conversions to java.lang.Integer)
 * <p/>
 * Default converters are registered for the following classes:
 * <ul>
 * <li><t>java.lang.String</t></li>
 * <li><t>java.lang.Byte</t></li>
 * <li><t>java.lang.Short</t></li>
 * <li><t>java.lang.Integer</t></li>
 * <li><t>java.lang.Long</t></li>
 * <li><t>java.lang.Float</t></li>
 * <li><t>java.lang.Double</t></li>
 * </ul>
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
 * 3/15/2007        JMP         Add converter storage by name for IoC configuration of grid sorting
 * ---------------------------------------------------
 */
public class ConverterFactory {

    /**
     * The bean name of a RequestStorageManager extension if this default is not used.
     */
    public static final String BEAN_NAME = "ConverterFactory";

    /**
     * Return an instance of the ConverterFactory. If the ConverterFactory is configured in the ApplicationContext,
     * that instance is used. Otherwise, and instance of this class is used.
     */
    public synchronized static ConverterFactory getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (ConverterFactory) ApplicationContext.getInstance().getBean(BEAN_NAME);
            } else {
                c_instance = new ConverterFactory();
            }
        }
        return c_instance;
    }

    /**
     * Add the given Map of converters, keyed by the target type supported by the Converter.
     * If a Converter is associated with a Primitive Wrapper class,
     * it will be registered with both the Wrapper class and it's corresponding primitive type
     */
    public synchronized void setConverters(Map converters) {
        Logger l = LogUtils.enterLog(getClass(), "setConverters", new Object[]{converters});

        Iterator iter = converters.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry converterEntry = (Map.Entry) iter.next();
            l.logp(Level.FINE, getClass().getName(), "setConverters", "Setting the Converter for " + converterEntry.getKey() + " to " + converterEntry.getValue());
            setConverter((Class) converterEntry.getKey(), (Converter) converterEntry.getValue());
        }

        l.exiting(getClass().getName(), "setConverters");
    }

    /**
     * Set the default Converter to use if no Converter is registered for the desired Target type.
     *
     * @param defaultConverter the Default Converter.
     */
    public void setDefaultConverter(Converter defaultConverter) {
        c_defaultConverter = defaultConverter;
    }

    /**
     * Set the given converter keyed by the target type supported by the Converter.
     * If a Converter is associated with a Primitive Wrapper class,
     * it will be registered with both the Wrapper class and it's corresponding primitive type
     */
    public synchronized void setConverter(Class targetType, Converter converter) {
        Logger l = LogUtils.enterLog(getClass(), "setConverter", new Object[]{targetType, converter});

        c_typeConverters.put(targetType, converter);

        // Add the Converter for the corresponding Primitive TYPE class if it is a primitive wrapper.
        if (Byte.class.equals(targetType)) {
            c_typeConverters.put(Byte.TYPE, converter);
        } else if (Short.class.equals(targetType)) {
            c_typeConverters.put(Short.TYPE, converter);
        } else if (Integer.class.equals(targetType)) {
            c_typeConverters.put(Integer.TYPE, converter);
        } else if (Long.class.equals(targetType)) {
            c_typeConverters.put(Long.TYPE, converter);
        } else if (Float.class.equals(targetType)) {
            c_typeConverters.put(Float.TYPE, converter);
        } else if (Double.class.equals(targetType)) {
            c_typeConverters.put(Double.TYPE, converter);
        } else if (Boolean.class.equals(targetType)) {
            c_typeConverters.put(Boolean.TYPE, converter);
        }

        l.exiting(getClass().getName(), "setConverter");
    }

    /**
     * Return the Converter for the given targetType.
     * If no Converter is registered for the given targetType, the Default Converter is returned.
     */
    public Converter getConverter(Class targetType) {
        Logger l = LogUtils.enterLog(getClass(), "getConverter", new Object[]{targetType});

        Converter converter = (Converter) c_typeConverters.get(targetType);
        if (converter == null) {
            l.logp(Level.FINE, getClass().getName(), "getConverter", "There is no converter registered for the type '"+targetType.getName()+"'. Trying the DefaultConverter");
            converter = c_defaultConverter;
        }

        l.exiting(getClass().getName(), "getConverter", converter);
        return converter;
    }

    /**
     * This constructor is for IoC Initialization only.
     * Converter names are configured as needed.
     */
    public void setConvertersByName(Map convertersByName) {
        Logger l = LogUtils.enterLog(getClass(), "setConvertersByName", new Object[]{convertersByName});

        c_typeConvertersByName = convertersByName;

        l.exiting(getClass().getName(), "setConvertersByName");
    }

    /**
     * Return the Converter for the given name.
     */
    public Converter getConverterByName(String converterName) {
        Logger l = LogUtils.enterLog(getClass(), "getConverterByName", new Object[]{converterName});

        Converter converter = (Converter) c_typeConvertersByName.get(converterName);
        if (converter == null) {
            throw new ConfigurationException("There is no converter registered for the name '"+converterName+"'");
        }

        l.exiting(getClass().getName(), "getConverterByName", converter);
        return converter;
    }

    /**
     * This constructor is for IoC Initialization only.
     * Users of this class should either be configured with a reference to it, or use the getInstance static method.
     */
    public ConverterFactory() {
        initializeConverters();
    }

    protected void initializeConverters() {
        Logger l = LogUtils.enterLog(getClass(), "initializeConverters");

        setConverter(String.class, new StringConverter());
        setConverter(Byte.class, new ByteConverter());
        setConverter(Short.class, new ShortConverter());
        setConverter(Integer.class, new IntegerConverter());
        setConverter(Long.class, new LongConverter());
        setConverter(Float.class, new FloatConverter());
        setConverter(Double.class, new DoubleConverter());
        setConverter(BigDecimal.class, new BigDecimalConverter());
        setConverter(Boolean.class, new BooleanConverter());
        setConverter(Date.class, new DateConverter());

        setDefaultConverter(new DefaultConverter());

        l.exiting(getClass().getName(), "initializeConverters");
    }

    // Maintain the map of Converters statically so that if multiple ConverterFactories are mistakenly created,
    // they will all utilize the same set of Converters.
    private static Map c_typeConverters = new Hashtable();

    // Maintain the map of Converters by name for use with the grid sorting functionality
    private static Map c_typeConvertersByName = new Hashtable();

    private static Converter c_defaultConverter;

    private static ConverterFactory c_instance;
}
