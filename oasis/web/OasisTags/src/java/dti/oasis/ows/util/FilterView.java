package dti.oasis.ows.util;

import java.util.Map;
import java.util.Set;

/**
 * The interface for filter view.
 *
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/12/2017
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
public interface FilterView {
    /**
     * The category of the filter view.
     * We should define different category for different web service/context.
     * The category for the party inquiry service is "PartyFilterView".
     * @return
     */
    String getCategory();

    /**
     * Get the name of the view.
     * @return
     */
    String getName();

    /**
     * Get the filter elements map of filter types.
     * @return
     */
    Map<String, Set<String>> getFilterElementsMap();

    /**
     * Get the filter string of filter types.
     * @return
     */
    Map<String, String> getFilterStringMap();
}
