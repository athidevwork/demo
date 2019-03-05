package dti.oasis.recordset;

import dti.oasis.converter.Converter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.util.StringUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/** This class represents the specialized field: a field with
 *  multiple values. It is commonly implemented in OASIS as, but
 *  not limited to a muli-select or a multi-checkbox field). With
 *  the implentation of MultiValueField, when a field has more
 *  than 1 value, it can behaviors differently from a regular
 *  field such as returning the comma-separated string values.
 *  (that is in addition to the behaviors it might have as a
 *  regular field object).
 *
 *  <p>
 *  To get the string value from a MultiValueField, use the
 *  method getStringValue() which returns all values separated
 *  by comma, or return one of the values if all values are same
 * <p>
 *  To get a specific value from the list of the value, use the
 *  method getStringValue(int)
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 13, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MultiValueField extends Field {

    public MultiValueField() {
        super();
    }

    public MultiValueField(String name) {
        super();
        m_name = name;
    }

    /**
     * Returns the value of this field.
     * If the value is a String, returns the result of calling getStringValue().
     */
    public Object getValue() {
        Object value = super.getValue();
        if (value instanceof String) {
            value = getStringValue();
        }
        return value;
    }

    /**
     * Returns the value in the specified index as an Object.
     *
     * @param index index of the desired value
     * @return value in the specified index.
     */
    public Object getValue(int index) {
        return m_valueList.get(index);
    }

    /**
     * Get the string value at the specified position from the list
     * that the MultiValueField object contains.
     *
     * @param  index  the index of the value from the list that the  MultiValueField contains
     * @return the string value at the specified position from the list.
     */

    public String getStringValue(int index) {
        if (c_stringConverter == null) {
            c_stringConverter = ConverterFactory.getInstance().getConverter(String.class);
        }

        return getStringValue(c_stringConverter, index);
    }

    /**
     * Get the string value at the specified position from the list
     * that the MultiValueField object contains.
     *
     * @param  converter that converts the given value from it's type to an object
     *         of the specified target type
     * @param  index  the index of the value from the list that the  MultiValueField contains
     * @return the string value at the specified position from the list.
     */

    public String getStringValue(Converter converter, int index) {
        return (String) converter.convert(String.class, m_valueList.get(index));
    }

    /**
     * Get the string value from the MultiValueField object.
     * It returns null if the MultiValueField is empty or null;
     * It returns one of the values if all the values are same;
     * Otherwise it returns a comma-separated string from the list
     * that it contains.
     *
     * @param  converter that converts the given value from it's type to an object
     *         of the specified target type
     * @return the string value of the MultiValueField.
     */

    public String getStringValue(Converter converter) {
        String stringValue = null;

        if (!isEmpty()) {
            StringBuffer values = new StringBuffer();

            if (m_allValueEquivalent) {
                // if they are same, just get the first one.
                values.append(converter.convert(String.class, m_valueList.get(0)));
            }
            else {
                Iterator iter = m_valueList.iterator();
                String delimiter = "";

                while (iter.hasNext()) {
                    String val = (String) converter.convert(String.class, iter.next());
                    // Skip blank values
                    if (!StringUtils.isBlank(val)) {
                        values.append(delimiter).append(val);
                        delimiter = ",";
                    }
                }
            }
            stringValue = values.toString();
        }

        return stringValue;
    }

    /**
     * set the value for a MultiValueField object.
     * In addition to it, it also sets the value for the Field object
     * So other behaviors as a regular Field object can still exist.
     *
     * @param  o Object to be set into the MultiValueField object
     */

    public void setValue(Object o) {
        super.setValue(o);

        if (m_valueList.size() > 0) {
            m_valueList.set(0, o);
        } else {
            m_valueList.add(o);
        }
    }

    /**
      * add a string value to a MultiValueField object.
      * In addition to it, if the Field object is empty,
      * it adds the value for the Field object,
      * So other behaviors as a regular Field object can still exist
      *
      * @param  value a string value to be added into the MultiValueField object
      */

    public void addValue(String value) {

        if (isEmpty() ||
            StringUtils.isBlank((String) m_valueList.get(0)) && !StringUtils.isBlank(value)) {
            // If we are empty OR
            // If the first value is blank, and the new value is not,
            // Then set the value for the super class (Field) to support the various getXXX methods
            super.setValue(value);
        }
        else {
            // re-exam the equivalency when adding a new value
            if (m_allValueEquivalent ) {
                m_allValueEquivalent = m_valueList.get(0).equals(value);
            }
        }

        // Always add the value to the end of the list to
        // make sure the number and order of values is in sync with the request.
        m_valueList.add(value);
    }

    public int size() {
        return m_valueList.size();
    }

    public boolean isEmpty() {
        return (m_valueList.size() == 0);
    }
        
    private static Converter c_stringConverter;
    private List m_valueList = new ArrayList();
    private boolean m_allValueEquivalent = true;

}
