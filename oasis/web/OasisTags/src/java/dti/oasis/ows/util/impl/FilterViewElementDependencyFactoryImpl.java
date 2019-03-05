package dti.oasis.ows.util.impl;

import dti.oasis.ows.util.FilterViewElementDependency;
import dti.oasis.ows.util.FilterViewElementDependencyFactory;
import dti.oasis.util.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/20/2017
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
public class FilterViewElementDependencyFactoryImpl extends FilterViewElementDependencyFactory {
    private final Logger l = LogUtils.getLogger(getClass());
    private static final Logger c_l = LogUtils.getLogger(FilterViewElementDependencyFactoryImpl.class);

    private static Map<String, FilterViewElementDependency> c_filterViewDependency;

    static {
        c_filterViewDependency = new HashMap<String, FilterViewElementDependency>();

        // Party Filter View Dependency.
        c_filterViewDependency.put("PartyFilterView",
                FilterViewElementDependencyBuilder.newInstance("PartyFilterView")
                        .add("addressFilter", "AddressType", "partyAdditionalInfoFilter", "AddressAdditionalInfoType")
                        .add("addressFilter", "AddressType", "partyAdditionalXmlInfoFilter", "AddressAdditionalXmlDataType")
                        .build());
    }

    public FilterViewElementDependency getFilterViewDependency(String category) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFilterViewDependency", new Object[]{category});
        }

        FilterViewElementDependency filterViewElementDependency = c_filterViewDependency.get(category);

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(FilterViewElementDependency.class.getName(), "getFilterViewDependency", filterViewElementDependency);
        }
        return filterViewElementDependency;
    }
}
