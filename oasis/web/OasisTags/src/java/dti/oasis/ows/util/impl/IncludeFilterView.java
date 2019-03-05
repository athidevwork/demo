package dti.oasis.ows.util.impl;

import dti.oasis.util.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The filter view to describes the elements should be displayed in results.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/16/2017
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
public class IncludeFilterView extends BaseFilterView {
    private final Logger l = LogUtils.getLogger(getClass());

    public IncludeFilterView() {
    }

    @Override
    public Map<String, String> getFilterStringMap() {
        return new HashMap<String, String>();
    }
}
