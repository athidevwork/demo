package dti.oasis.recordset;

import dti.oasis.filter.Filter;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/20/2017
 *
 * @author wli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/20/2017       wli         183962 - initial the class to filter Non flat records
 * ---------------------------------------------------
 */
public class NonFlatRecordFilter implements Filter{
    private final Logger l = LogUtils.getLogger(getClass());

    public NonFlatRecordFilter(String effDate, String effTodate) {
        m_effectiveFromDate = effDate;
        m_effectiveToDate = effTodate;
    }

    @Override
    public boolean accept(Object obj) {
        if (!(obj instanceof Record))
            throw new IllegalArgumentException("The NonFlatRecordFilter only accepts objects of type Record.");

        return !((Record) obj).getDateValue(m_effectiveFromDate).equals(((Record) obj).getDateValue(m_effectiveToDate));
    }

    @Override
    public boolean hasDataToCompare() {
        return false;
    }

    private String m_effectiveFromDate;
    private String m_effectiveToDate;
}
