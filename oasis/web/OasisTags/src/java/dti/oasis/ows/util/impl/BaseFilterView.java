package dti.oasis.ows.util.impl;

import dti.oasis.ows.util.FilterView;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The base class for FilterView.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/14/2017
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
public abstract class BaseFilterView implements FilterView {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public Map<String, String> getFilterStringMap() {
        l.entering(getClass().getName(), "getFilterStringMap");

        Map<String, String> filterStringMap = new HashMap<String, String>();

        Map<String, Set<String>> filterElementsMap = getFilterElementsMap();

        for (String filterType : filterElementsMap.keySet()) {
            if (StringUtils.isBlank(filterType)) {
                continue;
            }

            String filterString = "";
            Set<String> elements = filterElementsMap.get(filterType);

            if (elements != null) {
                for (String element : elements) {
                    if (!StringUtils.isBlank(element)) {
                        filterString += "|" + element;
                    }
                }
            }

            if (!StringUtils.isBlank(filterString)) {
                filterString = "'" + filterString + "|'";
            }

            filterStringMap.put(filterType, filterString);
        }

        return filterStringMap;
    }

    @Override
    public String getCategory() {
        return m_category;
    }

    public void setCategory(String category) {
        m_category = category;
    }

    @Override
    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    @Override
    public Map<String, Set<String>> getFilterElementsMap() {
        if (m_filterElementsMap == null) {
            m_filterElementsMap = new HashMap<String, Set<String>>();
        }
        return m_filterElementsMap;
    }

    public void setFilterElementsMap(Map<String, Set<String>> filterElementsMap) {
        m_filterElementsMap = filterElementsMap;
    }

    private String m_category;
    private String m_name;
    private Map<String, Set<String>> m_filterElementsMap;
}
