package dti.oasis.ows.util;

import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/19/2017
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class FilterViewElement {
    private final Logger l = LogUtils.getLogger(getClass());

    public FilterViewElement(String filterType, String elementName) {
        m_filterType = filterType;
        m_elementName = elementName;
    }

    public String getFilterType() {
        return m_filterType;
    }

    public String getElementName() {
        return m_elementName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterViewElement that = (FilterViewElement) o;

        if (!m_elementName.equals(that.m_elementName)) return false;
        if (!m_filterType.equals(that.m_filterType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = m_filterType.hashCode();
        result = 31 * result + m_elementName.hashCode();
        return result;
    }

    private String m_filterType;
    private String m_elementName;
}
