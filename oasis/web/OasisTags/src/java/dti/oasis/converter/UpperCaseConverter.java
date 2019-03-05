package dti.oasis.converter;

import dti.oasis.struts.ActionHelper;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   11/17/2017
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/17/2017      dpang       Issue 188740: Add UpperCaseConverter to enable case-insensitive grid sort.
 * ---------------------------------------------------
 */
public class UpperCaseConverter extends BaseConverter {

    public UpperCaseConverter() {
        super();
    }

    @Override
    public Object convert(Class targetType, Object inputValue, Object nullValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{targetType, inputValue, nullValue});
        }

        Object convertedValue = null;
        if (inputValue == null) {
            convertedValue = nullValue;
        } else {
            if (ActionHelper.isBase64(inputValue)) {
                inputValue = ActionHelper.decodeField(inputValue);
            }
            convertedValue = inputValue.toString().toUpperCase();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "convert", convertedValue);
        }
        return convertedValue;
    }

    @Override
    public Class getDefaultTargetType() {
        return String.class;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
