package dti.oasis.recordset;

import dti.oasis.converter.Converter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Iterator;

/**
 * Compares two records for order.  Returns a negative integer,
 * zero, or a positive integer as the first record is less than, equal
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
public class RecordComparator implements Comparator {

    /**
     * Construct a RecordComparator.
     * By default, null field values compare as less than other values.
     * By default, sorting is Ascending
     * @param fieldName
     */
    public RecordComparator(String fieldName) {
        this(fieldName, true, SortOrder.ASC, null);
    }

    /**
     * Construct a RecordComparator specifying the sort order of ASC or DESC
     * By default, null field values compare as less than other values.
     * @param fieldName
     * @param sortOrder
     */
    public RecordComparator(String fieldName, SortOrder sortOrder) {
        this(fieldName, true, sortOrder, null);
    }

    /**
     * Construct a RecordComparator specifying a converter to be applied before comparisson
     * By default, null field values compare as less than other values.
     * By default, sorting will be ascending
     * @param fieldName
     * @param converter
     */
    public RecordComparator(String fieldName, Converter converter) {
        this(fieldName, true, SortOrder.ASC, converter);
    }

    /**
     * Construct a RecordComparator specifying the sort order of ASC or DESC and
     * specifying a converter to apply to the field value before comparisson
     * By default, null field values compare as less than other values.
     * @param fieldName
     * @param sortOrder
     * @param converter
     */
    public RecordComparator(String fieldName, SortOrder sortOrder, Converter converter) {
        this(fieldName, true, sortOrder, converter);
    }

    /**
     * Construct a RecordComparator
     * Specifying if null field values should compare as less than or greater than other values.
     * Specifiying a sort order of ASC/DESC
     * Specifying a convreter to apply to the field value before comparisson
     * @param fieldName
     * @param nullsAreGreater
     * @param sortOrder
     * @param converter
     */
    public RecordComparator(String fieldName, boolean nullsAreGreater, SortOrder sortOrder, Converter converter) {
        m_fieldComparator = new ArrayList();
        m_fieldComparator.add(new FieldComparator(fieldName, nullsAreGreater, sortOrder, converter));
    }

    /**
     * Add an additional Field comparator to the RecordComparator.
     * By default, null field values compare as less than other values.
     * By default, sorting is Ascending
     * @param fieldName
     */
    public void addFieldComparator(String fieldName) {
        addFieldComparator(fieldName, true, SortOrder.ASC, null);
    }

    /**
     * Add an additional Field comparator to the RecordComparator specifying sort order.
     * By default, null field values compare as less than other values.
     * @param fieldName
     * @param sortOrder
     */
    public void addFieldComparator(String fieldName, SortOrder sortOrder) {
        addFieldComparator(fieldName, true, sortOrder, null);
    }

    /**
     * Add an additional Field comparator to the RecordComparator specifying sort order.
     * By default, null field values compare as less than other values.
     * @param fieldName
     * @param converter
     */
    public void addFieldComparator(String fieldName, Converter converter) {
        addFieldComparator(fieldName, true, SortOrder.ASC, converter);
    }

    /**
     * Add an additional Field comparator to the RecordComparator specifying sort order
     * and specifying a converter to apply to the field value before comparisson.
     * By default, null field values compare as less than other values.
     * @param fieldName
     * @param sortOrder
     * @param converter
     */
    public void addFieldComparator(String fieldName, SortOrder sortOrder, Converter converter) {
        addFieldComparator(fieldName, true, sortOrder, converter);
    }

    /**
     * Add an additional Field comparator to the RecordComparator specifying if null values should coompare as less than
     * or greater than other values.
     * @param fieldName
     * @param nullsAreGreater
     * @param sortOrder
     */
    public void addFieldComparator(String fieldName, boolean nullsAreGreater, SortOrder sortOrder, Converter converter) {
        m_fieldComparator.add(new FieldComparator(fieldName, nullsAreGreater, sortOrder, converter));
    }

    /**
     * Compares two records for order.  Returns a negative integer,
     * zero, or a positive integer as the first record is less than, equal
     * to, or greater than the second.<p>
     *
     * @param recordA the first record to be compared.
     * @param recordB the second record to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first record is less than, equal to, or greater than the
     *         second.
     * @throws ClassCastException if the arguments' types are not <code>Record</code>,
     *                            or if the corresponding field values are not <code>Comparable</code>.
     */
    public int compare(Object recordA, Object recordB) {
        int compareResult = 0;
        Record recA = (Record) recordA;
        Record recB = (Record) recordB;

        Iterator iter = m_fieldComparator.iterator();
        while(iter.hasNext()) {
            FieldComparator fldComparator = (FieldComparator) iter.next();
            compareResult = fldComparator.compare(recA.getField(fldComparator.getFieldName()),
                                                  recB.getField(fldComparator.getFieldName()));
            if(compareResult != 0) {
                break;
            }
        }
        return compareResult;
    }

    private List m_fieldComparator;
}
