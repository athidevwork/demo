package dti.oasis.ows.util.impl;

import dti.oasis.ows.util.FilterViewElementDependency;
import dti.oasis.ows.util.FilterViewElement;
import dti.oasis.util.LogUtils;

import java.util.HashSet;
import java.util.Set;
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
public class FilterViewElementDependencyBuilder {
    private final Logger l = LogUtils.getLogger(getClass());

    private FilterViewElementDependencyBuilder(String category) {
        m_filterViewElementDependency = new FilterViewElementDependencyImpl(category);
    }

    public static FilterViewElementDependencyBuilder newInstance(String category) {
        return new FilterViewElementDependencyBuilder(category);
    }

    public FilterViewElementDependencyBuilder add(String filterType, String elementName, String dependencyFilterType, String dependencyElementName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "add", new Object[]{filterType, elementName, dependencyFilterType, dependencyElementName});
        }

        FilterViewElement filterViewElement = new FilterViewElement(filterType, elementName);

        Set<FilterViewElement> dependencyElements = m_filterViewElementDependency.getElementDependencyMap().get(filterViewElement);
        if (dependencyElements == null) {
            dependencyElements = new HashSet<FilterViewElement>();
            m_filterViewElementDependency.getElementDependencyMap().put(filterViewElement, dependencyElements);
        }

        dependencyElements.add(new FilterViewElement(dependencyFilterType, dependencyElementName));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "add", this);
        }
        return this;
    }

    public FilterViewElementDependency build() {
        return m_filterViewElementDependency;
    }

    private FilterViewElementDependencyImpl m_filterViewElementDependency;
}
