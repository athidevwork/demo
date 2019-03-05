package dti.oasis.ows.util.impl;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.List;
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
public class BusinessEmailListElementValueConverter extends BaseElementValueConverter {
    @Override
    public String convert(Object obj, String elementPath) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{obj, elementPath});
        }

        String value = null;
        String property = null;
        String emailKey = null;

        if (elementPath.contains("#key=")) {
            property = elementPath.substring(0, elementPath.indexOf("#key="));
            emailKey = elementPath.substring(elementPath.indexOf("#key=") + 5);
        } else {
            property = elementPath;
        }

        try {
            List businessEmailList = (List) PropertyUtils.getProperty(obj, property);

            if (businessEmailList != null && businessEmailList.size() > 0) {
                Object businessEmail = null;

                if (StringUtils.isBlank(emailKey)) {
                    // Get the first email address by default.
                    businessEmail = businessEmailList.get(0);
                } else {
                    for (int i = 0; i < businessEmailList.size(); i++) {
                        Object tempBusinessEmail = businessEmailList.get(i);

                        if (tempBusinessEmail != null) {
                            String key = BeanUtils.getProperty(tempBusinessEmail, "key");

                            if (emailKey.equals(key)) {
                                businessEmail = tempBusinessEmail;
                                break;
                            }
                        }
                    }
                }

                if (businessEmail != null) {
                    value = BeanUtils.getProperty(businessEmail, "email");
                }
            }

        } catch (NestedNullException nne) {
            l.logp(Level.FINE, this.getClass().getName(), "convert", "Cannot find property: " + property);
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "Failed to get property: " + property + ". " +
                            "Please check the fieldElementMaps config for PropertyChangeProcessor.", e);
            l.throwing(this.getClass().getName(), "convert", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "convert", value);
        }
        return value;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
