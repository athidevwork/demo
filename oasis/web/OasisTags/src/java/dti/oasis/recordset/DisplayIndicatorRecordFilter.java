package dti.oasis.recordset;

import dti.oasis.filter.Filter;

/**
 *  Filter to accept records that match based on the provided DISPLAY_IND value.
 * The value of the displayIndicator should match one of the valid values
 * specified in the DisplayIndicator Interface.
 * If the displayIndicatorValue is a String[], then the display indicator must match
 * one of the provided displayIndicator values in the provided String[].
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 7, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class DisplayIndicatorRecordFilter implements Filter {

    public DisplayIndicatorRecordFilter(String updateIndicatorValue) {
        this(updateIndicatorValue, null);
    }
    public DisplayIndicatorRecordFilter(String[] updateIndicatorValueArray) {
        this(updateIndicatorValueArray, null);
    }

    public DisplayIndicatorRecordFilter(String displayIndicatorValue, Filter nextFiter) {
        m_displayIndicatorValue = displayIndicatorValue;
        m_nextFiter = nextFiter;
    }

    public DisplayIndicatorRecordFilter(String[] displayIndicatorValueArray, Filter nextFiter) {
        m_displayIndicatorValue = displayIndicatorValueArray;
        m_nextFiter = nextFiter;
    }

    /**
     * Returns true if the given object is accepted by this filter. Otherwise, false.
     */
    public boolean accept(Object obj) {

        if (!(obj instanceof Record))
            throw new IllegalArgumentException("The DisplayIndicatorRecordFilter only accepts objects of type Record.");

        if (!hasDataToCompare()) return false;

        Record record = (Record) obj;
        boolean matchResult = false;
        if (m_displayIndicatorValue instanceof String[]) {
            String[] displayIndicatorValueArray = (String[]) m_displayIndicatorValue;
            for (int i = 0; i < displayIndicatorValueArray.length; i++) {
                String displayIndicatorValue = displayIndicatorValueArray[i];
                if (record.getDisplayIndicator().equals(displayIndicatorValue)) {
                    matchResult = true;
                    break;
                }
            }
        }
        else {
            matchResult = record.getDisplayIndicator().equals(m_displayIndicatorValue);
        }

        if (matchResult && m_nextFiter != null) {
            // The object matches and there is another filter; pass on to the next filter to further determine if it's a match.
            matchResult = m_nextFiter.accept(obj);
        }

        return matchResult;
    }

    public boolean hasDataToCompare() {
        return m_displayIndicatorValue != null;
    }

    private Object m_displayIndicatorValue;
    private Filter m_nextFiter;
}