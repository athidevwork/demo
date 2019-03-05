package dti.oasis.recordset;

import dti.oasis.filter.Filter;

/**
 * Filter to accept records that match based on the provided UPDATE_IND value.
 * The value of the updateIndicator should match one of the valid values
 * specified in the UpdateIndicator Interface.
 * If the updateIndicatorValue is a String[], then the update indicator must match
 * one of the provided updateIndicator values in the provided String[].
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
public class UpdateIndicatorRecordFilter implements Filter {

    public UpdateIndicatorRecordFilter(String updateIndicatorValue) {
        this(updateIndicatorValue, null);
    }
    public UpdateIndicatorRecordFilter(String[] updateIndicatorValueArray) {
        this(updateIndicatorValueArray, null);
    }

    public UpdateIndicatorRecordFilter(String updateIndicatorValue, Filter nextFiter) {
        m_updateIndicatorValue = updateIndicatorValue;
        m_nextFiter = nextFiter;
    }

    public UpdateIndicatorRecordFilter(String[] updateIndicatorValueArray, Filter nextFiter) {
        m_updateIndicatorValue = updateIndicatorValueArray;
        m_nextFiter = nextFiter;
    }

    /**
     * Returns true if the given object is accepted by this filter. Otherwise, false.
     */
    public boolean accept(Object obj) {

        if (!(obj instanceof Record))
            throw new IllegalArgumentException("The UpdateIndicatorRecordFilter only accepts objects of type Record.");

        if (!hasDataToCompare()) return false;

        Record record = (Record) obj;
        boolean matchResult = false;
        if (m_updateIndicatorValue instanceof String[]) {
            String[] updateIndicatorValueArray = (String[]) m_updateIndicatorValue;
            for (int i = 0; i < updateIndicatorValueArray.length; i++) {
                String updateIndicatorValue = updateIndicatorValueArray[i];
                if (record.getUpdateIndicator().equals(updateIndicatorValue)) {
                    matchResult = true;
                    break;
                }
            }
        }
        else {
            matchResult = record.getUpdateIndicator().equals(m_updateIndicatorValue);
        }

        if (matchResult && m_nextFiter != null) {
            // The object matches and there is another filter; pass on to the next filter to further determine if it's a match.
            matchResult = m_nextFiter.accept(obj);
        }

        return matchResult;
    }

    public boolean hasDataToCompare() {
        return m_updateIndicatorValue != null;
    }

    private Object m_updateIndicatorValue;
    private Filter m_nextFiter;
}
