package dti.oasis.ows.util.impl;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.ows.util.FilterView;
import dti.oasis.util.LogUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
public class FilterViewBuilder {
    private final Logger l = LogUtils.getLogger(getClass());

    private FilterViewBuilder(String category, String name, Class<? extends BaseFilterView> type) {
        this.m_category = category;
        this.m_name = name;
        this.m_type = type;

        this.m_filterElementsMap = new HashMap<>();
    }

    public static FilterViewBuilder newInstance(String category, String name, Class<? extends BaseFilterView> type) {
        return new FilterViewBuilder(category, name, type);
    }

    public FilterView build() {
        l.entering(getClass().getName(), "build");

        BaseFilterView filterView = null;
        try {
            filterView = (BaseFilterView) m_type.newInstance();

            filterView.setCategory(m_category);
            filterView.setName(m_name);
            filterView.setFilterElementsMap(m_filterElementsMap);
        } catch (InstantiationException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to build filter view.", e);
            l.throwing(getClass().getName(), "build", ae);
            throw ae;
        } catch (IllegalAccessException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to build filter view.", e);
            l.throwing(getClass().getName(), "build", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "build");
        return filterView;
    }

    public FilterViewBuilder addFilterElements(String filterType, String[] elements) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addFilterElements", new Object[]{filterType, elements});
        }

        Set<String> filterElements = m_filterElementsMap.get(filterType);
        if (filterElements == null) {
            filterElements = new HashSet<String>();
            m_filterElementsMap.put(filterType, filterElements);
        }

        for (String element : elements) {
            filterElements.add(element);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addFilterElements", this);
        }
        return this;
    }

    private Map<String, Set<String>> m_filterElementsMap;

    private String m_category;
    private String m_name;
    private Class<? extends BaseFilterView> m_type;
}
