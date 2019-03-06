package dti.oasis.ows.util.impl;

import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   11/10/2014
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
public class SsnElementValueConverter extends BaseElementValueConverter {
    @Override
    public String convert(Object obj, String elementPath) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{obj, elementPath});
        }

        String value = getProperty(obj, elementPath);

        if (!StringUtils.isBlank(value)) {
            value = value.replaceAll("-", "");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "convert", value);
        }
        return value;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}