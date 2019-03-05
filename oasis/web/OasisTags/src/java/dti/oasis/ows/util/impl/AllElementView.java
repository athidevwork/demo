package dti.oasis.ows.util.impl;

import dti.oasis.ows.util.FilterViewFactory;
import dti.oasis.util.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The filter view to retrieve all elements.
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
public class AllElementView extends BaseFilterView {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public String getName() {
        return FilterViewFactory.ALL_ELEMENT_VIEW_NAME;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("The name property of AllElementView can not be changed.");
    }

    @Override
    public Map<String, Set<String>> getFilterElementsMap() {
        return new HashMap<String, Set<String>>();
    }

    @Override
    public void setFilterElementsMap(Map<String, Set<String>> filterElementsMap) {
        super.setFilterElementsMap(filterElementsMap);
    }
}
