package dti.oasis.ows.util.impl;

import dti.oasis.ows.util.FilterView;
import dti.oasis.ows.util.FilterViewElementDependency;
import dti.oasis.ows.util.FilterViewElement;
import dti.oasis.ows.util.FilterViewElementDependencyFactory;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/13/2017
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
public class CompositeFilterView extends BaseFilterView {
    private final Logger l = LogUtils.getLogger(getClass());

    public CompositeFilterView(String category, List<FilterView> filterViews) {
        this.m_childFilterViews = new ArrayList<FilterView>();
        this.getChildFilterViews().addAll(filterViews);

        this.setCategory(category);
    }

    @Override
    public String getName() {
        l.entering(getClass().getName(), "getName");

        String filterViewName = "";

        for (FilterView filterView : this.getChildFilterViews()) {
            if (filterView != null && !StringUtils.isBlank(filterView.getName())) {
                if (!StringUtils.isBlank(filterViewName)) {
                    filterViewName += ", ";
                }

                filterViewName += filterView.getName();
            }
        }

        filterViewName = "[" + filterViewName + "]";

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getName", filterViewName);
        }
        return filterViewName;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("The name property of CompositeFilterView cannot be changed.");
    }

    @Override
    public Map<String, Set<String>> getFilterElementsMap() {
        l.entering(getClass().getName(), "getFilterElementsMap");

        Map<String, Set<String>> filterElementsMap = null;

        List<FilterView> includeFilterViews = new ArrayList<>();

        for (FilterView tempFilterView : getChildFilterViews()) {
            if (tempFilterView != null) {
                if (tempFilterView instanceof ExcludeFilterView) {
                    Map<String, Set<String>> tempFilterElementsMap = tempFilterView.getFilterElementsMap();

                    if (filterElementsMap == null) {
                        // If this is the first ExcludeFilterView, add all elements to the filterElements Map.
                        filterElementsMap = new HashMap<String, Set<String>>();

                        for (String filterType : tempFilterElementsMap.keySet()) {
                            // Get a copy of filter elements.
                            Set<String> filterElements = new HashSet<String>();
                            filterElements.addAll(tempFilterElementsMap.get(filterType));

                            // Add elements to filter elements map.
                            filterElementsMap.put(filterType, filterElements);
                        }
                    } else {
                        List<String> filterTypesToBeRemoved = new ArrayList<String>();

                        for (String filterType : filterElementsMap.keySet()) {
                            if (tempFilterElementsMap.containsKey(filterType)) {
                                // If the current filter type is found in temp filter elements, retain all of temp filters.
                                filterElementsMap.get(filterType).retainAll(tempFilterElementsMap.get(filterType));
                            } else {
                                // If the current filter type is not found in temp filter elements, remove it later.
                                filterTypesToBeRemoved.add(filterType);
                            }
                        }

                        for (String filterType : filterTypesToBeRemoved) {
                            // Remove elements.
                            filterElementsMap.remove(filterType);
                        }
                    }
                } else if (tempFilterView instanceof IncludeFilterView) {
                    // Add include filter views.
                    includeFilterViews.add(tempFilterView);
                }
            }
        }

        if (filterElementsMap == null) {
            filterElementsMap = new HashMap<String, Set<String>>();

        } else {
            processIncludeFilterViews(filterElementsMap, includeFilterViews);
            processDependencyElements(filterElementsMap);
        }

        return filterElementsMap;
    }

    /**
     * Process include filter views.
     * @param filterElementsMap
     * @param includeFilterViews
     */
    private void processIncludeFilterViews(Map<String, Set<String>> filterElementsMap, List<FilterView> includeFilterViews) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processIncludeFilterViews", new Object[]{filterElementsMap, includeFilterViews});
        }

        for (FilterView includeFilterView : includeFilterViews) {
            if (includeFilterView != null) {
                Map<String, Set<String>> includeFilterElementsMap = includeFilterView.getFilterElementsMap();

                for (String filterType : includeFilterElementsMap.keySet()) {
                    if (filterElementsMap.containsKey(filterType)) {
                        // Remove the include elements.
                        filterElementsMap.get(filterType).removeAll(
                                includeFilterElementsMap.get(filterType));
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "processIncludeFilterViews");
    }

    /**
     * Process dependency elements.
     * @param filterElementsMap
     */
    private void processDependencyElements(Map<String, Set<String>> filterElementsMap) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processDependencyElements", new Object[]{filterElementsMap});
        }

        if (filterElementsMap != null) {
            FilterViewElementDependency filterViewElementDependency = FilterViewElementDependencyFactory.getInstance().getFilterViewDependency(this.getCategory());

            if (filterViewElementDependency != null) {
                Set<FilterViewElement> filterViewElements = new HashSet<FilterViewElement>();

                for (String filterType : filterElementsMap.keySet()) {
                    Set<String> elements = filterElementsMap.get(filterType);

                    for (String element : elements) {
                        Set<FilterViewElement> tempFilterViewElements = filterViewElementDependency.getDependencyElements(filterType, element);

                        if (tempFilterViewElements != null) {
                            filterViewElements.addAll(tempFilterViewElements);
                        }
                    }
                }

                if (filterViewElements.size() > 0) {
                    for (FilterViewElement filterViewElement : filterViewElements) {
                        Set<String> filterElements = filterElementsMap.get(filterViewElement.getFilterType());

                        if (filterElements == null) {
                            filterElements = new HashSet<String>();
                            filterElementsMap.put(filterViewElement.getFilterType(), filterElements);
                        }

                        filterElements.add(filterViewElement.getElementName());
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "processDependencyElements");
    }

    @Override
    public void setFilterElementsMap(Map<String, Set<String>> filterElementsMap) {
        super.setFilterElementsMap(filterElementsMap);
    }

    public List<FilterView> getChildFilterViews() {
        if (m_childFilterViews == null) {
            m_childFilterViews = new ArrayList<FilterView>();
        }
        return m_childFilterViews;
    }

    public void setChildFilterViews(List<FilterView> childFilterViews) {
        m_childFilterViews = childFilterViews;
    }

    private List<FilterView> m_childFilterViews;
}
