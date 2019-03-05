package dti.oasis.recordset;

import dti.oasis.converter.DateConverter;
import dti.oasis.converter.Converter;

import java.util.Comparator;

/**
 * Compares two fields for order.  Returns a negative integer,
 * zero, or a positive integer as the first field is less than, equal
 * to, or greater than the second.
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
public class FieldComparator implements Comparator {

    /**
     * Construct a FieldComparator.
     * By default, null values compare as less than other values.
     * By default, sort order is ASC
     * By default, no converter
     * @param fieldName
     */
    public FieldComparator(String fieldName) {
        this(fieldName, true, SortOrder.ASC, null);
    }

    /**
     * Construct a FieldComparator specifying the sort order for the field, ASC or DESC
     * By default, null values compare as less than other values.
     * By default, no converter
     * @param fieldName
     * @param sortOrder
     */
    public FieldComparator(String fieldName, SortOrder sortOrder) {
        this(fieldName, true, sortOrder, null);
    }

    /**
     * Construct a FieldComparator specifying a converter to be applied before comparisson
     * By default, null values compare as less than other values.
     * By default, sorted in ascending order
     * @param fieldName
     * @param converter
     */
    public FieldComparator(String fieldName, Converter converter) {
        this(fieldName, true, SortOrder.ASC, converter);
    }

    /**
     * Construct a FieldComparator specifying the sort order for the field, ASC or DESC
     * Define a converter to apply to the field value before comparisson
     * By default, null values compare as less than other values.
     * @param fieldName
     * @param sortOrder
     * @param converter
     */
    public FieldComparator(String fieldName, SortOrder sortOrder, Converter converter) {
        this(fieldName, true, sortOrder, converter);
    }

    /**
     * Construct a FieldComparator specifying if null values should compare as less than or greater than other values.
     * Specifying the sort order for the field, ASC or DESC
     * Specifying a converter to apply to the field value before comparisson
     * @param fieldName
     * @param nullsAreGreater
     * @param sortOrder
     * @param converter
     */
    public FieldComparator(String fieldName, boolean nullsAreGreater, SortOrder sortOrder, Converter converter) {
        m_fieldName = fieldName;
        m_nullsAreGreater = nullsAreGreater;
        m_sortOrder = sortOrder;
        m_converter = converter;
    }

    public String getFieldName() {
        return m_fieldName;
    }

    public boolean hasConverter() {
        return m_converter != null;
    }

    /**
     * Compares two fields for order.  Returns a negative integer,
     * zero, or a positive integer as the first field is less than, equal
     * to, or greater than the second.<p>
     *
     * @param fieldA the first field to be compared.
     * @param fieldB the second field to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first field is less than, equal to, or greater than the
     *         second.
     * @throws ClassCastException if the arguments' types are not <code>Field</code>,
     *                            of if the field values are not <code>Comparable</code>.
     */
    public int compare(Object fieldA, Object fieldB) {
        int compareResult = 0;

        if (fieldA == fieldB) return 0;
        if (null == fieldA) return (m_nullsAreGreater ? 1 : -1) * m_sortOrder.intValue();
        if (null == fieldB) return (m_nullsAreGreater ? -1 : 1) * m_sortOrder.intValue();

        // Get the contained value objects as Comparable, optionally applying a converter
        Comparable valA, valB;

        if(hasConverter()) {
            valA = (Comparable) m_converter.convert(((Field) fieldA).getValue());
            valB = (Comparable) m_converter.convert(((Field) fieldB).getValue());
        } else {
            valA = (Comparable) ((Field) fieldA).getValue();
            valB = (Comparable) ((Field) fieldB).getValue();
        }

        if (valA == valB) return 0;
        if (null == valA) {
            compareResult = m_nullsAreGreater ? 1 : -1;
        } else if (null == valB) {
            compareResult = m_nullsAreGreater ? -1 : 1;
        } else {
            compareResult = valA.compareTo(valB);
        }

        // Multiply by SortOrder value to potentially flip order ASC or DESC
        return compareResult * m_sortOrder.intValue();
    }

    private boolean m_nullsAreGreater = false;
    private String m_fieldName;
    private SortOrder m_sortOrder;
    private Converter m_converter = null;
}
