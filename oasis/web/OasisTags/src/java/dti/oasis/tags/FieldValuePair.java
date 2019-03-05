package dti.oasis.tags;

import java.io.Serializable;

/**
 * This class contains a pairing of field Id & value.
 * It provides a specialized equals method
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p> 
 * @author jbe
 * Date:   Dec 16, 2003
 * 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 2/7/2004     jbe     Added toString
 *
 * ---------------------------------------------------
 */

public class FieldValuePair implements Serializable {
    private String fieldId;
    private String value;

    /**
     * Constructor
     * @param fieldId
     * @param value
     */
    public FieldValuePair(String fieldId, String value) {
        this.fieldId = fieldId;
        this.value = value;
    }

    /**
     * Setter
     * @param fieldId
     */
    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * Setter
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Getter
     * @return
     */
    public String getFieldId() {
        return fieldId;
    }

    /**
     * Getter
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Determines if two FieldValuePair objects are the equal
     * @param o a FieldValuePair object
     * @return
     */
    public boolean equals(Object o) {
        if(o!=null && o instanceof FieldValuePair) {
            FieldValuePair pair = (FieldValuePair) o;
            return (pair.getFieldId().equals(fieldId) &&
                    pair.getValue().equals(value));
        }
        else
            return false;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.FieldValuePair");
        buf.append("{fieldId=").append(fieldId);
        buf.append(",value=").append(value);
        buf.append("}");
        return buf.toString();
    }

}
