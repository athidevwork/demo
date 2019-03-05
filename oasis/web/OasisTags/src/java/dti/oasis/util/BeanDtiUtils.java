package dti.oasis.util;

import dti.oasis.tags.OasisFormField;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

import javax.servlet.jsp.JspException;
import java.util.logging.Logger;

/**
 * Bean Utility object
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 20, 2004
 * @author jbe
 *
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * 01/23/2007       wer        Added support for using the BasicDynaBeanMap.class for compatability with JSTL
 * ---------------------------------------------------
 */

public class BeanDtiUtils {
    /**
     * Dynamically creates a bean with a single property
     * The property will be have the name of the OasisFormField.fieldId
     * The value will be the given value.
     * We use Apache BeanDtiUtils for this
     * @param field The OasisFormField we are basing this on
     * @param value The value of the field
     * @return a DynaBean whose single attribute name will match the field.fieldId
     *         and whose value will match the value parameter.
     * @throws javax.servlet.jsp.JspException
     */
    public static DynaBean createValueBean(OasisFormField field, Object value)
            throws JspException {
        return createValueBean(field.getFieldId(), value);
    }

    /**
     * Dynamically creates a bean with a single property
     * The property will be have the name of the given fieldId
     * The value will be the given value.
     * We use Apache BeanDtiUtils for this
     * @param fieldId The fieldId of the field
     * @param value The value of the field
     * @return a DynaBean whose single attribute name will match the field.fieldId
     *         and whose value will match the value parameter.
     * @throws javax.servlet.jsp.JspException
     */
    public static DynaBean createValueBean(String fieldId, Object value)
            throws JspException {
        Logger l = LogUtils.enterLog(BeanDtiUtils.class, "createValueBean",
                new Object[]{fieldId, value});
        // Set up the single attribute
        DynaProperty[] props = new DynaProperty[]{
            new DynaProperty(fieldId, (value == null) ? String.class : value.getClass())
        };
        // Define a DynaClass.
        // Use the BasicDynaBeanMap so that JSTL can access the property using JSTL EL syntax:
        // ${dynabeanName.map.prop}
        BasicDynaClass dynaClass = new BasicDynaClass("bean", BasicDynaBeanMap.class, props);

        // Instantiate the bean
        DynaBean bean = null;
        try {
            bean = dynaClass.newInstance();
        } catch (InstantiationException ie) {
            throw new JspException(ie.getMessage());
        } catch (IllegalAccessException iae) {
            throw new JspException(iae.getMessage());
        }

        // set the value in the single attribute
        bean.set(fieldId, value);
        l.exiting(BeanDtiUtils.class.getName(), "createValueBean", bean);
        return bean;
    }
}
