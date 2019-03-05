package dti.oasis.recordset;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.converter.Converter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.filter.Filter;
import dti.oasis.util.StringUtils;

/**
 * Filter to accept records that match based on the provided field name and value.
 * <p/>
 * Minimally, the fieldName must exist.
 * If the fieldName and fieldValue are provided, then the field must exist in the record,
 * and the field's value must match the provided fieldValue in order to match.
 * If the fieldValue is an Object[], then the field's value must match
 * one of the provided fieldValues in the provided Object[].
 * <p/>
 * If the fieldValue is null, then either the field doesn't exist in the record,
 * or the value of the record is null in order to match.
 * <p/>
 * If the fieldValue is a YesNoFlag, the value is passed to YesNoFlag.getInstance(),
 * and the return value is compared with with the yesNoFieldValue. If the field doesn't exist or the value is null,
 * it will match with a yesNoFieldValue of YesNoFlag.N.
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
 * 07/21/2008       yhchen      #83894 ignore the type differences and only check string values
 * 07/22/2008       yhchen      #83894 convert the filter's value (or array of values) to the type of the record's value
 * ---------------------------------------------------
 */
public class RecordFilter implements Filter {

    public RecordFilter(String fieldName, YesNoFlag yesNofieldValue) {
        this(fieldName, (Object) yesNofieldValue, null);
        m_yesNoFieldValue = yesNofieldValue;
    }

    public RecordFilter(String fieldName, Object fieldValue) {
        this(fieldName, fieldValue, null);
    }

    public RecordFilter(String fieldName, Object[] fieldValueArray) {
        this(fieldName, fieldValueArray, null);
    }

    /**
     * Matches all records with the named field value either empty or not empty, depending on the value of matchNotEmpty.
     * Empty means the value is either null or an empty string.
     *
     * @param fieldName     the name of the field to compare.
     * @param matchNotEmpty true if all non-empty fields are accepted, or false if only empty fields are accepted.
     */
    public RecordFilter(String fieldName, boolean matchNotEmpty) {
        m_fieldName = fieldName;
        m_matchNotEmptyValues = matchNotEmpty;
    }


    public RecordFilter(String fieldName, YesNoFlag yesNofieldValue, Filter nextFiter) {
        this(fieldName, (Object) yesNofieldValue, nextFiter);
        m_yesNoFieldValue = yesNofieldValue;
    }

    public RecordFilter(String fieldName, Object[] fieldValueArray, Filter nextFiter) {
        if (fieldName == null)
            throw new IllegalArgumentException("The fieldName constructor parameter can not be null.");

        m_fieldName = fieldName;
        m_fieldValue = fieldValueArray;
        m_nextFiter = nextFiter;
    }

    public RecordFilter(String fieldName, Object fieldValue, Filter nextFiter) {
        if (fieldName == null)
            throw new IllegalArgumentException("The fieldName constructor parameter can not be null.");

        m_fieldName = fieldName;
        m_fieldValue = fieldValue;
        m_nextFiter = nextFiter;
    }

    /**
     * Returns true if the given object is accepted by this filter. Otherwise, false.
     */
    public boolean accept(Object obj) {

        if (!(obj instanceof Record))
            throw new IllegalArgumentException("The RecordFilter only accepts objects of type Record.");

        if (!hasDataToCompare()) return false;

        boolean matchResult = false;  // by default, no match found

        Record record = (Record) obj;
        if (record.hasFieldValue(m_fieldName)) {
            Field field = record.getField(m_fieldName);
            Object value = field.getValue();

            if (m_fieldValue != null && value != null) {
                if (m_yesNoFieldValue != null) {
                    value = YesNoFlag.getInstance(value.toString());
                }
                if (m_fieldValue instanceof Object[]) {
                    Object[] fieldValueArray = (Object[]) m_fieldValue;
                    for (int i = 0; i < fieldValueArray.length; i++) {
                        Object fieldValue = fieldValueArray[i];
                        //convert the filter's array of values to the type of the record's value
                        if (fieldValue.getClass() != value.getClass()) {
                            fieldValue = ConverterFactory.getInstance().getConverter(value.getClass()).convert(fieldValue);
                            fieldValueArray[i] = fieldValue;
                        }
                        if (fieldValue.equals(value)) {
                            matchResult = true;
                            break;
                        }
                    }
                }
                else {
                    //convert the filter's value to the type of the record's value
                    if (m_fieldValue.getClass() != value.getClass()) {
                        m_fieldValue = ConverterFactory.getInstance().getConverter(value.getClass()).convert(m_fieldValue);
                    }
                    if (m_fieldValue.equals(value)) {
                        // The field value was provided, and it equals the record's field value; match found
                        matchResult = true;
                    }
                }
            }
            else if ((m_matchNotEmptyValues && !StringUtils.isBlank(value.toString())) ||
                (!m_matchNotEmptyValues && StringUtils.isBlank(value.toString()))) {
                matchResult = true;
            }
        }
        else if ((m_fieldValue == null) ||
            (m_yesNoFieldValue != null && !m_yesNoFieldValue.booleanValue())) {
            // The field value is null or is YesNoFlag.N, and the record's field does not exist; match found
            if (!m_matchNotEmptyValues) {
                matchResult = true;
            }
        }

        if (matchResult && m_nextFiter != null) {
            // The object matches and there is another filter; pass on to the next filter to further determine if it's a match.
            matchResult = m_nextFiter.accept(obj);
        }

        return matchResult;
    }

    public boolean hasDataToCompare() {
        return m_fieldName != null;
    }

    private String m_fieldName;
    private Object m_fieldValue;
    private YesNoFlag m_yesNoFieldValue;
    private Filter m_nextFiter;
    private boolean m_matchNotEmptyValues;
}
