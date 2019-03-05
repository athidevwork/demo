package dti.oasis.busobjs;

import java.io.Serializable;

/**
 * Base class for Enumerated Types.
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
 *
 * ---------------------------------------------------
 */
public abstract class EnumType implements Serializable {

    protected EnumType(int value, String name) {
        m_name = name;
        m_value = value;
    }

    /**
     * Returns the int value of this enumerated type.
     * This value is useful for comparing a variable against a static enum instance based on their int values.
     * This approach is more dependable than comparing object references when the enumerated types are serialized.
     */
    public int intValue() {
        return m_value;
    }

    /**
     * Returns the string name of this enumerated type.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the string name of this enumerated type.
     */
    public String toString() {
        return m_name;
    }

    public boolean equals(Object o) {
        if (null == o) return false;
        if (this == o) return true;

        if (o instanceof EnumType) {
            final EnumType enumType = (EnumType) o;

            if (m_value != enumType.m_value) return false;
            if (!m_name.equals(enumType.m_name)) return false;

        } else if (o instanceof String) {
            return m_name.equals(o);
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = m_name.hashCode();
        return result;
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public EnumType() {
    }

    private String m_name; // for debug only
    private int m_value;
}
