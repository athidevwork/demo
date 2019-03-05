package dti.oasis.app.impl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.apache.commons.collections.iterators.EnumerationIterator;

import java.util.Properties;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This class exposes a getProperty method to retrieve the property's value from the configured property file(s).
 * This class extends the org.springframework.beans.factory.config.PropertyPlaceholderConfigurer so that
 * all properties who's value is another property placeholder will be resolved.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 9, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/09/2008       wer         Added getPropertyNames method
 * ---------------------------------------------------
 */
public class PropertyPlaceholderConfigurer extends org.springframework.beans.factory.config.PropertyPlaceholderConfigurer {

    public String getProperty(String key) {
        // Use the parseStringValue method to resolve all property placeholders and return the resolved value.
        return parseStringValue(DEFAULT_PLACEHOLDER_PREFIX + key + DEFAULT_PLACEHOLDER_SUFFIX, m_properties, new HashSet());
    }

    public Iterator getPropertyNames() {
        return new EnumerationIterator(m_properties.propertyNames());
    }

    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);

        // Maintain a reference to the properties so we can find property values later.
        m_properties = props;
    }

    public Properties getProperties(){
        return m_properties;    
    }

    private Properties m_properties;
}
