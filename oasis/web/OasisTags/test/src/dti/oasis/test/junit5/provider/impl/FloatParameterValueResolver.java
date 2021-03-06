package dti.oasis.test.junit5.provider.impl;

import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/8/2018
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
public class FloatParameterValueResolver extends BaseParameterValueResolver<Float> {
    private final Logger l = LogUtils.getLogger(getClass());

    public FloatParameterValueResolver() {
        super(Float.class);
    }
}
