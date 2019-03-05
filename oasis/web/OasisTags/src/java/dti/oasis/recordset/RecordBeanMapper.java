package dti.oasis.recordset;

import dti.oasis.app.AppException;
import dti.oasis.converter.Converter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.util.LogUtils;
import dti.oasis.util.Mapper;
import dti.oasis.util.StringUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Map properties of any type between a Record and a Java Bean, bidirectionally.
 * You can optionally provide a list of property names to exclude while mapping.
 * If there are multiple set methods for a property,
 * the first one found where there is a valid converter availble will be used; a property will not be set more than once.
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
public class RecordBeanMapper implements Mapper {

    public RecordBeanMapper() {
        addPropertyExclusion("class");
    }

    /**
     * Construct this RecordBeanMapper with a list of property names to excluding while mapping.
     */
    public RecordBeanMapper(String[] propertyExclusions) {
        this();
        for (int i = 0; i < propertyExclusions.length; i++) {
            String propertyExclusion = propertyExclusions[i];
            addPropertyExclusion(propertyExclusion);
        }
    }

    /**
     * Add a property name to exclude while mapping.
     */
    public void addPropertyExclusion(String propertyName) {
        m_propertyExclusions.put(propertyName.toUpperCase(), propertyName);
    }

    /**
     * Returns boolean indicating if the property will be excluded while mapping.
     */
    public boolean isPropertyExcluded(String propertyName) {
        return m_propertyExclusions.containsKey(propertyName.toUpperCase());
    }

    /**
     * Map properties of any type between a Record and a Java Bean, bidirectionally.
     * <p/>
     *
     * @param source
     * @param target
     */
    public void map(Object source, Object target) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "map", new Object[]{source, target});
        }

        ConverterFactory converterFactory = ConverterFactory.getInstance();
        Converter stringConverter = converterFactory.getConverter(String.class);

        // Clear the stored mapped properties.
        clearMappedProperties();

        // Map from a Record to a Java Beam
        if (source instanceof Record) {
            Record sourceRecord = (Record) source;

            // Iterate through the get/set accessor methods on the Java Bean
            PropertyDescriptor[] targetPropertyDescriptors = PropertyUtils.getPropertyDescriptors(target);
            for (int i = 0; i < targetPropertyDescriptors.length; i++) {
                PropertyDescriptor targetPropertyDescriptor = targetPropertyDescriptors[i];
                String propName = targetPropertyDescriptor.getName();

                // If the Property has not already been mapped, and the property is not excluded from mapping,
                // and the Java Bean exposes a setProperty method, and the Source Record has the Field matching the Property Name,
                // and a Converter is registered with the ConverterFactory for the type of the target Java Bean,
                // then map the property.
                if (!hasPropertyBeenMapped(propName) && !isPropertyExcluded(propName) && sourceRecord.hasField(propName)) {
                    try {
                        // Get the set accessor method if one exists.
                        Method writeMethod = PropertyUtils.getWriteMethod(targetPropertyDescriptor);
                        if (writeMethod != null) {

                            Object value = sourceRecord.getFieldValue(propName);
                            boolean haveWriteableValue = false;

                            if (value == null || targetPropertyDescriptor.getPropertyType().isAssignableFrom(value.getClass())){
                                haveWriteableValue = true;
                            }
                            else {
                                // Use the registered Converter to perform default type conversions.
                                Converter converter = converterFactory.getConverter(targetPropertyDescriptor.getPropertyType());
                                value = converter.convert(targetPropertyDescriptor.getPropertyType(), value);

                                haveWriteableValue = true;
                            }

                            if (haveWriteableValue) {
                                // Map the property from the Record to the Java Bean
                                writeMethod.invoke(target, new Object[]{value});

                                // Remember that we have mapped this property
                                addMappedProperty(propName);
                            }
                            else {
                                l.logp(Level.FINE, getClass().getName(), "map", "There is no converter registered for the property '" + propName + "' with target type '" + targetPropertyDescriptor.getPropertyType() + "'");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new AppException("RecordBeanMapper failed to invoke the set" + StringUtils.capitalizeFirstLetter(propName) + " method on the target object of type <" + target.getClass().getName() + ">");
                    }
                }
            }
        }
        // Map from a Java Bean to a Record
        else if (target instanceof Record) {
            Record targetRecord = (Record) target;

            // Iterate through the get/set accessor methods on the Java Bean
            PropertyDescriptor[] sourcePropertyDescriptors = PropertyUtils.getPropertyDescriptors(source);
            for (int i = 0; i < sourcePropertyDescriptors.length; i++) {
                PropertyDescriptor sourcePropertyDescriptor = sourcePropertyDescriptors[i];
                String propName = sourcePropertyDescriptor.getName();

                try {
                    // If the Property has not already been mapped, and the property is not excluded from mapping,
                    // and the Java Bean exposes a getProperty method,
                    // then map the property.
                    if (!hasPropertyBeenMapped(propName) && !isPropertyExcluded(propName)) {

                        // Get the get accessor method if one exists.
                        Method readMethod = PropertyUtils.getReadMethod(sourcePropertyDescriptor);
                        if (readMethod != null) {
                            Object propValue = readMethod.invoke(source, new Object[0]);
                            targetRecord.setFieldValue(propName, propValue);

                            addMappedProperty(propName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new AppException("RecordBeanMapper failed to invoke the set" + StringUtils.capitalizeFirstLetter(propName) + " method on the source object of type <" + source.getClass().getName() + ">");
                }
            }
        } else {
            throw new AppException("The RecordBeanMapper expects either the source or target object to be a Record.");
        }

        l.exiting(getClass().getName(), "map");
    }

    /**
     * Return an Iterator of the properties that were mapped.
     */
    public Iterator getMappedProperties() {
        return m_mappedProperties.keySet().iterator();
    }

    protected void clearMappedProperties() {
        m_mappedProperties.clear();
    }

    protected void addMappedProperty(String propName) {
        m_mappedProperties.put(propName, propName);
    }

    protected boolean hasPropertyBeenMapped(String propName) {
        return m_mappedProperties.containsKey(propName);
    }

    private Map m_mappedProperties = new HashMap();
    private Map m_propertyExclusions = new HashMap();
    private final Logger l = LogUtils.getLogger(getClass());
}
