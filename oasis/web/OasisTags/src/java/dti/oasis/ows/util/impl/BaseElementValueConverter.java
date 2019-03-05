package dti.oasis.ows.util.impl;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.ows.util.ElementValueConverter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.NestedNullException;

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
public abstract class BaseElementValueConverter implements ElementValueConverter {
    /**
     * Get object property value.
     * @param obj
     * @param property
     * @return
     */
    protected String getProperty(Object obj, String property) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProperty", new Object[]{obj, property});
        }

        String value = null;

        if (obj != null && !StringUtils.isBlank(property)) {
            try {
                value = BeanUtils.getProperty(obj, property);
            } catch (NestedNullException nne) {
                l.logp(Level.FINE, this.getClass().getName(), "mapObjectToRecord", "Cannot find property: " + property);
            } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException(
                        "Failed to get property: " + property + ". " +
                                "Please check the fieldElementMaps config for PropertyChangeProcessor.", e);
                l.throwing(this.getClass().getName(), "setMappedFieldValues", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProperty", value);
        }
        return value;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
