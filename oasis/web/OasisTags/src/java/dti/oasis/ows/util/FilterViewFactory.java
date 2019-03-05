package dti.oasis.ows.util;

import dti.oasis.app.ApplicationContext;
import dti.oasis.filter.Filter;
import dti.oasis.ows.util.impl.FilterViewFactoryImpl;
import dti.oasis.util.LogUtils;

import java.util.List;
import java.util.logging.Logger;

/**
 * The factory class for getting Filter View by filter category and filer view names.
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
public abstract class FilterViewFactory {
    private final Logger l = LogUtils.getLogger(getClass());
    public static final String ALL_ELEMENT_VIEW_NAME = "[ALL_ELEMENT]";
    public static final String DEFAULT_ELEMENT_VIEW_NAME = "[DEFAULT]";
    private static final String BEAN_NAME = "filterViewFactory";

    private static FilterViewFactory c_instance;

    public static FilterViewFactory getInstance() {
        if (c_instance == null) {
            synchronized (FilterViewFactory.class) {
                c_instance = (FilterViewFactory) ApplicationContext.getInstance().getBean(BEAN_NAME);
            }
        }
        return c_instance;
    }

    /**
     * Get filter view by filter view names.
     * @param category
     * @param filterViewNames
     * @return
     */
    public abstract FilterView getFilterView(String category, List<String> filterViewNames);

    /**
     * Get the default filter view of a category.
     * @param category
     * @return
     */
    public abstract FilterView getDefaultFilterView(String category);
}
