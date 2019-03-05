package dti.oasis.ows.util.impl;

import dti.oasis.ows.util.FilterViewElement;
import dti.oasis.ows.util.FilterViewElementDependency;
import dti.oasis.util.LogUtils;

import java.util.*;
import java.util.logging.Level;
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
public class FilterViewElementDependencyImpl implements FilterViewElementDependency {
    private final Logger l = LogUtils.getLogger(getClass());

    public FilterViewElementDependencyImpl(String category) {
        m_category = category;
        m_elementDependencyMap = new HashMap<FilterViewElement, Set<FilterViewElement>>();
    }

    @Override public Set<FilterViewElement> getDependencyElements(String filterType, String elementName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDependencyElements", new Object[]{filterType, elementName});
        }

        FilterViewElement filterViewElement = new FilterViewElement(filterType, elementName);

        Set<FilterViewElement> filterViewElements = m_elementDependencyMap.get(filterViewElement);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDependencyElements", filterViewElements);
        }
        return filterViewElements;
    }

    @Override public String getCategory() {
        return m_category;
    }

    public void setCategory(String category) {
        m_category = category;
    }

    public Map<FilterViewElement, Set<FilterViewElement>> getElementDependencyMap() {
        return m_elementDependencyMap;
    }

    public void setElementDependencyMap(Map<FilterViewElement, Set<FilterViewElement>> elementDependencyMap) {
        m_elementDependencyMap = elementDependencyMap;
    }

    private String m_category;
    private Map<FilterViewElement, Set<FilterViewElement>> m_elementDependencyMap;
}
