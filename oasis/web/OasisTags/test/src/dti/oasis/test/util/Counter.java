package dti.oasis.test.util;

import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/6/2018
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
public class Counter {
    private final Logger l = LogUtils.getLogger(getClass());

    private int count = 0;

    public void increase() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
