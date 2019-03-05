package dti.oasis.ows.util;

import dti.oasis.app.ApplicationContext;
import dti.oasis.util.LogUtils;

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
public abstract class FilterViewElementDependencyFactory {
    private final Logger l = LogUtils.getLogger(getClass());
    private static final String BEAN_NAME = "filterViewElementDependencyFactory";
    private static FilterViewElementDependencyFactory c_instance;

    public static FilterViewElementDependencyFactory getInstance() {
        if (c_instance == null) {
            synchronized (FilterViewFactory.class) {
                c_instance = (FilterViewElementDependencyFactory) ApplicationContext.getInstance().getBean(BEAN_NAME);
            }
        }
        return c_instance;
    }

    public abstract FilterViewElementDependency getFilterViewDependency(String category);
}
