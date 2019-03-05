package dti.oasis.ows.util.impl;

import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.XmlPhoneNumber;

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
public class PhoneNumberElementValueConverter extends BaseElementValueConverter {
    @Override
    public String convert(Object obj, String elementPath) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convert", new Object[]{obj, elementPath});
        }

        String property = elementPath;
        if (property.indexOf("#") > 0) {
            property = property.substring(0, property.indexOf("#"));
        }

        String value = getProperty(obj, property);
        if (!StringUtils.isBlank(value)) {

            if (!XmlPhoneNumber.isValidPhoneNumber(value)) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"Invalid Phone Number: " + value});
                throw new AppException("Invalid phone number format.");
            }

            XmlPhoneNumber xmlPhoneNumber = new XmlPhoneNumber(value);

            if (elementPath.endsWith("#AreaCode")) {
                value = xmlPhoneNumber.getAreaCode();
            } else if (elementPath.endsWith("#PhoneNumber")) {
                value = xmlPhoneNumber.getPhoneNumber();
            } else if (elementPath.endsWith("#Extension")) {
                value = xmlPhoneNumber.getExtension();
            } else if (elementPath.endsWith("#CountryCode")) {
                value = xmlPhoneNumber.getCountryCode();
            } else if (elementPath.endsWith("#XmlPhoneNumber")) {
                value = xmlPhoneNumber.getXmlPhoneNumber();
            } else {
                String phoneNumber = "";
                if (!StringUtils.isBlank(xmlPhoneNumber.getAreaCode())) {
                    phoneNumber += "(" + xmlPhoneNumber.getAreaCode() + ")";
                }

                if (!StringUtils.isBlank(xmlPhoneNumber.getPhoneNumber())) {
                    phoneNumber += xmlPhoneNumber.getPhoneNumber();
                }

                if (!StringUtils.isBlank(xmlPhoneNumber.getExtension())) {
                    phoneNumber += "x" + xmlPhoneNumber.getExtension();
                }

                value = phoneNumber;
            }

            // Set default value to empty value.
            if (value == null) {
                value = "";
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "convert", value);
        }
        return value;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
