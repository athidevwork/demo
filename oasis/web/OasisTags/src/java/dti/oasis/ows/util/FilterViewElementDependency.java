package dti.oasis.ows.util;

import java.util.Set;

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
public interface FilterViewElementDependency {
    Set<FilterViewElement> getDependencyElements(String filterType, String elementName);

    String getCategory();
}
