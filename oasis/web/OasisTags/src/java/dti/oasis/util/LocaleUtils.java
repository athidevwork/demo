package dti.oasis.util;

import dti.oasis.app.ApplicationContext;

import java.util.Currency;
import java.util.Locale;

/**
 * Locale Utility Class
 *
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 30, 2010
 *
 * @author wfu
 */
/*
* Revision Date    Revised By  Description
* -----------------------------------------------------------------------------
* 08/30/2010       wfu         Issue 109875. Changed logic to use system locale definition
*                              and added function getOasisLocale to support multiple currency.
* 09/20/2011       mxg         Issue #100716: Added Display Type FORMATTEDNUMBER
* -----------------------------------------------------------------------------
*/

public class LocaleUtils {

    /**
     * Get oasis default locale. If it is not defined in the property file,
     * then returns the system default.
     *
     * @return Locale
     */
    public static Locale getOasisLocale() {
        String localeName = null;
        Locale locale = null;
        if (ApplicationContext.getInstance().hasProperty("oasis.locale")) {
            localeName = ApplicationContext.getInstance().getProperty("oasis.locale");
        }
        if (!StringUtils.isBlank(localeName)&&localeName.indexOf("-")>=0) {
            locale = new Locale(localeName.split("-")[0].toLowerCase(), localeName.split("-")[1].toUpperCase());
        }
        return locale==null?Locale.getDefault():locale;
    }

    /**
     * Get oasis locale used by NumberFormatter jQuery Plugin.
     * The Plugin uses Country not Language for it's locales.
     *
     * @return String
     */
    public static String getJsNbrFormatterLocale() {
        return getOasisLocale().getCountry().toLowerCase();
    }

    /**
     * Get currency symbol of oasis default locale. If the locale is not defined
     * in the property file, then returns the system default currency symbol.
     *
     * @return String
     */
    public static String getOasisCurrencySymbol() {
        String symbol = Currency.getInstance(getOasisLocale()).getSymbol(getOasisLocale());
        return symbol;
    }

}