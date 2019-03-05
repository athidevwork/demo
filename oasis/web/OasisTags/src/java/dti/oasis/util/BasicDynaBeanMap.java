package dti.oasis.util;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaClass;

import java.util.Map;

/**
 * This class extends the BasisDynaBean to make it more useable by JSP pages using JSTL.
 *
 * The internal map is exposed for use by JSTL using a syntax like:
 * <pre>
 *  ${dynaBeanPropName.map.dynaBeanPropName}</pre>
 * </p>
 *
 * Also, the toString method is implemented to return the last property value set on this bean.
 * That way, if a single property is set, as is done when creating DynaBeans for each value used
 * by Struts, the value is easily accessible using syntax like:
 * <pre>
 *  ${dynaBeanPropName}</pre>
 * </p>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 22, 2006
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
public class BasicDynaBeanMap extends BasicDynaBean {
    public BasicDynaBeanMap(DynaClass dynaClass) {
        super(dynaClass);
    }

    /**
     * <p>Returns the <code>Map</code> containing the property values.  This is
     * done mostly to facilitate accessing the <code>DynaActionForm</code>
     * through JavaBeans accessors, in order to use the JavaServer Pages
     * Standard Tag Library (JSTL).</p>
     *
     * <p>For instance, the normal JSTL EL syntax for accessing an
     * <code>ActionForm</code> would be something like this:
     * <pre>
     *  ${formbean.prop}</pre>
     * The JSTL EL syntax for accessing a <code>DynaActionForm</code> looks
     * something like this (because of the presence of this
     * <code>getMap()</code> method):
     * <pre>
     *  ${dynaBeanPropName.map.dynaBeanPropName}</pre>
     * </p>
     */
    public Map getMap() {
        return (values);
    }

    public void set(String name, Object value) {
        super.set(name, value);
        m_defaultValue = value;
    }

    public void set(String name, int i, Object value) {
        super.set(name, i, value);
        m_defaultValue = value;
    }

    public void set(String name, String key, Object value) {
        super.set(name, key, value);
        m_defaultValue = value;
    }

    /**
     * Returns the last value set on this DynaBean as the default value.
     * @return a string representation of the object.
     */
    public String toString() {
        return m_defaultValue == null ? null : m_defaultValue.toString();
    }

    private Object m_defaultValue;
}
