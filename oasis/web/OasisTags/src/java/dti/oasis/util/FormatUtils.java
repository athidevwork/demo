package dti.oasis.util;

import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.ActionHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Formatting Utility Class
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 20, 2004
 *
 * @author jbe
 */
/*
* Revision Date    Revised By  Description
* -----------------------------------------------------------------------------
* 3/19/2004        jbe         Switch to SimpleDateFormat
* 7/22/2004        jbe         Add isDouble
* 10/7/2004        jbe         new Currency Formatting w/ decimal places
* 04/05/2005       gcc         Added isShort, isInt, stringToBool, boolToString.
* 11/01/2006       gcc         Added new methods unformatCurrency and
*                              unformatNumber.
* 01/04/2007       gcc         Changed formatCurrency to return empty string
*                              instead of ".".
* 01/23/2007       wer         Changed usage of new Boolean(x) in logging to String.valueOf(x);
* 03/24/2008       Fred        Added method formatPercentage().
* 04/13/2009       mxg         Added methods isDateFormatUS() and formatCustomDate()
*                              used for Date Format Internationalization
* 09/23/2009       Fred        Issue 96884. Extend Internationalization to Date / Time fields
* 10/08/2009       mgitelm     Issue 98246. Ability to ignore case sensitivity when processing calendar.dateformat
* 10/09/2009       fcb         Issue# 96764: added logic for masked fields.
* 10/13/2009       mlm         Issue 99260: added parseBigDecimal()
* 10/14/2009       clm         Issue 99641: add properties scale and roundingMode for decimal number
* 11/17/2009       kenney      enh to support phone format
* 30/08/2010       wfu         Issue 109875. Changed logic to use system locale definition
*                              and used function getOasisLocale to support multiple currency.
* 10/13/2010       tzhao       Issue 109875. Add decimalFormatUnify method to unify the negative prefix and suffix.
* 10/15/2010       wfu         109875 - Change function getDateFormatForDisplayString to public using.
* 10/22/2010       tzhao       109875 - Add member variable pattern which has the default date pattern of the locale.
* 10/25/2010       tzhao       109875 - Revert the latest change and add comment to getDateFormatForDisplayString() method.
* 01/17/2011       clm         add new method formatDate and isDateTime.
* 02/18/2011       ldong       112568 - Enchanced to handle the new date format DD/MON/YYYY
* 09/20/2011       mxg         Issue #100716: Added Display Type FORMATTEDNUMBER
* 07/25/2013       mxg         152763 - dateFormat, dtmFormat
*                              and calendar converted to ThreadLocal to ensure that they are thread safe.
*                              This is due to 2 different type of errors that were received when using DateUtils:
*                               - java.lang.NumberFormatException: multiple points
*                               - java.lang.NumberFormatException: For input string: ""
*                              Please also refer to issue 147014
* 07/20/2015       kshen       Issue 164600. Corrected formatPhoneNumberForDisplay for JDK 1.8
* 09/17/2015       Parker      Issue#165637 - Use ThreadLocal to make SimpleDateFormat thread safe.
* 11/17/2015       Elvin       Issue 167139: add method getCurrentDateFullYear
* 06/27/2016       Parker      Issue#177786 Remove final String in local method
* 09/21/2017       kshen       Grid replacement. Removed method getJqxDateDisplayFormat and getJqxDateTimeDisplayFormat
* 10/20/2017       cesar       #186297 - Modified formatPhoneNumberForDisplay() to check if input parameter has been encoded.
* 01/05/2018       ylu         Issue 190396.
*                              1) modify formatCurrency() to return original input when fail to format it,
*                                 as same as formatPercentage()
* 08/03/2018       dpang       194836 - Add formatPercentage method to enable setting isGroupingUsed.
* -----------------------------------------------------------------------------
*/

public class FormatUtils {

    public static final String DISPLAY_FIELD_EXTENTION = "_DISP_ONLY";
    protected static final String clsName = FormatUtils.class.getName();
    private static DecimalFormat dftCurrFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(LocaleUtils.getOasisLocale());
    private static HashMap currFormats = new HashMap(3);

    private static final ThreadLocal<DateFormat> dateFormat
            = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("MM/dd/yyyy");
        }
    };

    private static final ThreadLocal<DateFormat> dtmFormat
            = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        }
    };
    private static String c_dateFormatForDisplayString;
    private static String c_dateTimeFormatForDisplayString;
    private static String c_dateTimeNoSecFormatForDisplayString;
    private static Boolean c_isDateFormatUS;
    private static final String YES_VALUE = "Y";
    private static final String NO_VALUE = "N";
    private static final String DOLLAR_SIGN_VALUE = "$";
    private static final String COMMA_VALUE = ",";
    private static final String OPEN_PAREN_VALUE = "(";
    private static final String CLOSE_PAREN_VALUE = ")";
    private static final String MINUS_SIGN_VALUE = "-";
    private static final String EMPTY_STRING = "";
    private static final String timeFomatStr = " hh:mm a";
    private static final String FIELD_MASK = "********";
    private static String c_fieldMask;
    private static int c_decimalScale = 2;
    private static int c_decimalRoundingMode = BigDecimal.ROUND_HALF_UP;
    private static RoundingMode c_formatNumberRoundingMode = RoundingMode.DOWN;
    private static String c_phoneNumberFormat;
    private static final String DEFAULT_PHONENUMBER_FORMAT = "###-####"; //108235: use # to represent numeric chars

    /**
     * Get the instance of the dateFormat
     *
     * @return Format
     */
    protected static DateFormat getDateFormat() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getDateFormat");
        }
        DateFormat df = dateFormat.get();
        df.setLenient(false);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getDateFormat", df);
        }
        return df;
    }

    /**
     * Get the instance of the dtmFormat
     *
     * @return Format
     */
    protected static DateFormat getDtmFormat() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getDtmFormat");
        }
        DateFormat df = dtmFormat.get();
        df.setLenient(false);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getDtmFormat", df);
        }
        return df;
    }

    /**
     * Get the right CurrencyFormat for the # of decimals
     *
     * @param numDecimals
     * @return DecimalFormat
     */
    private static DecimalFormat getCurrFormat(int numDecimals) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getCurrFormat", new Object[]{numDecimals});
        }
        String s = String.valueOf(numDecimals);
        DecimalFormat cF = (DecimalFormat) currFormats.get(s);
        if (cF == null) {
            cF = (DecimalFormat) NumberFormat.getCurrencyInstance(LocaleUtils.getOasisLocale());
            cF.setMaximumFractionDigits(numDecimals);
            currFormats.put(s, cF);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getCurrFormat", cF);
        }
        return cF;
    }

    /**
     * Formats a value based on the current locale's currency using a
     * specific number of decimal places.
     *
     * @param value       amount as String
     * @param numDecimals
     * @return String
     */
    public static String formatCurrency(String value, int numDecimals) {
        return formatCurrency(value, getCurrFormat(numDecimals));
    }

    /**
     * Formats a value based on the current locale's currency using a
     * specific number of decimal places.
     *
     * @param number      amount as long
     * @param numDecimals
     * @return String
     */
    public static String formatCurrency(long number, int numDecimals) {
        return formatCurrency(number, getCurrFormat(numDecimals));
    }

    /**
     * Formats a value based on the current locale's currency using a
     * specific number of decimal places.
     *
     * @param number      as double
     * @param numDecimals
     * @return String
     */
    public static String formatCurrency(double number, int numDecimals) {
        return formatCurrency(number, getCurrFormat(numDecimals));
    }

    /**
     * Formats a string value as percentage. The fraction digits is 2.
     * Return blank value if the pct is invalid or blank.
     *
     * @param pct String
     * @return
     */
    public static String formatPercentage(String pct) {
        return formatPercentage(pct, 2);
    }

    /**
     * @param pct            String
     * @param fractionDigits int
     * @return
     */
    public static String formatPercentage(String pct, int fractionDigits) {
        return formatPercentage(pct, fractionDigits, false);
    }

    /**
     * Formats a string value as percentage. If isGroupingUsed is true, a comma separator will be generated if formatted value is greater than or equal to 1000.00%.
     * For example, 12.34 might be formatted as 1,234.00% if true, otherwise 1234.00%.
     *
     * @param pct             String
     * @param fractionDigits  int
     * @param isGroupingUsed  boolean
     * @return
     */
    public static String formatPercentage(String pct, int fractionDigits, boolean isGroupingUsed) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatPercentage", new Object[]{pct, fractionDigits});
        }
        String percentageResult = EMPTY_STRING;
        if (!StringUtils.isBlank(pct)) {
            if (FormatUtils.isDouble(pct)) {
                NumberFormat nf = NumberFormat.getPercentInstance();
                nf.setMaximumFractionDigits(fractionDigits);
                nf.setMinimumFractionDigits(fractionDigits);
                nf.setGroupingUsed(isGroupingUsed);
                percentageResult = nf.format(Double.valueOf(pct));
            } else {
                percentageResult = pct;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatPercentage", percentageResult);
        }
        return percentageResult;
    }

    /**
     * Formats a value based on the current locale's currency
     *
     * @param value  amount as String
     * @param format DecimalFormat to use
     * @return String
     */
    private static String formatCurrency(String value, DecimalFormat format) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatCurrency", new Object[]{value, format});
        }
        String curr = EMPTY_STRING;
        decimalFormatUnify(format);
        if (!StringUtils.isBlank(value)) {
            try {
                curr = format.format(Double.parseDouble(value));
            } catch (NumberFormatException fe) {
                try {
                    value = unformatCurrency(value);
                    curr = format.format(Double.parseDouble(value));
                } catch (NumberFormatException fe1) {
                    curr = value;
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatCurrency", curr);
        }
        return curr;
    }

    /**
     * Formats a value based on the current locale's currency
     *
     * @param number amount as double
     * @param format DecimalFormat to use
     * @return String
     */
    private static String formatCurrency(double number, DecimalFormat format) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatCurrency", new Object[]{number, format});
        }
        decimalFormatUnify(format);
        String curr = format.format(number);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatCurrency", curr);
        }
        return curr;
    }

    /**
     * Formats a value based on the current locale's currency
     *
     * @param number amount as long
     * @param format DecimalFormat to use
     * @return String
     */
    private static String formatCurrency(long number, DecimalFormat format) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatCurrency", new Object[]{number, format});
        }
        decimalFormatUnify(format);
        String curr = format.format(number);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatCurrency", curr);
        }
        return curr;
    }

    /**
     * Unify decimal format negtive prefix and negative suffix
     *
     * @param format DecimalFormat to be unified
     */
    private static void decimalFormatUnify(DecimalFormat format) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "decimalFormatUnify", new Object[]{format});
        }
        //For US $ the negative prefix is ($ and the negative suffix is )
        //For Chinese RMB symbol the negative prefix is -yang and the negative suffix is empty string
        //Set the negative prefix and suffix unified to (symbol ) here        
        format.setNegativePrefix("(".concat(LocaleUtils.getOasisCurrencySymbol()));
        format.setNegativeSuffix(")");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "decimalFormatUnify");
        }
    }

    /**
     * Formats a value based on the current locale's currency
     *
     * @param value amount as String
     * @return String
     */
    public static String formatCurrency(String value) {
        return formatCurrency(value, dftCurrFormat);
    }

    /**
     * Formats a value based on the current locale's currency
     *
     * @param number amount as double
     * @return String
     */
    public static String formatCurrency(double number) {
        return formatCurrency(number, dftCurrFormat);
    }

    /**
     * Formats a value based on the current locale's currency
     *
     * @param number amount as long
     * @return String
     */
    public static String formatCurrency(long number) {
        return formatCurrency(number, dftCurrFormat);
    }

    /**
     * Formats a date object according to the current locale's Short date format
     *
     * @param date
     * @return String
     */
    public static String formatDate(Date date) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatDate", new Object[]{date});
        }
        String rc = (date == null) ? "" : getDateFormat().format(date);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatDate", rc);
        }
        return rc;
    }

    /**
     * Formats a date object by given valid format
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatDate", new Object[]{date, format});
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String rc = (date == null) ? "" : formatter.format(date);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatDate", rc);
        }
        return rc;
    }

    /**
     * Formats a date object according to the custom Short date format
     *
     * @param date
     * @return String
     */
    public static String formatDateForDisplay(Date date) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatDateForDisplay", new Object[]{date});
        }
        String strDateFormatForDisplay = getDateFormatForDisplayString();
        if (strDateFormatForDisplay.indexOf("mon") > 0) {
            strDateFormatForDisplay = strDateFormatForDisplay.replaceAll("mon", "MMM");
        }
        SimpleDateFormat df = new SimpleDateFormat(strDateFormatForDisplay);
        String rc = (date == null) ? "" : df.format(date);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatDateForDisplay", rc);
        }
        return rc;
    }

    /**
     * Formats a date object according to the custom Short date format
     *
     * @param dateString
     * @return String
     */
    public static String formatDateForDisplay(String dateString) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatDateForDisplay", new Object[]{dateString});
        }
        String rc = (dateString == null || dateString.equals("")) ? "" : formatDateForDisplay(new Date(dateString));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatDateForDisplay(String)", rc);
        }
        return rc;
    }

    /**
     * Format a date object according to the custom full date format
     *
     * @param date
     * @return
     */
    public static String formatDateTimeForDisplay(Date date) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatDateTimeForDisplay", new Object[]{date});
        }
        String strDateFormatForDisplay = getDateFormatForDisplayString();
        if (strDateFormatForDisplay.indexOf("mon") > 0) {
            strDateFormatForDisplay = strDateFormatForDisplay.replaceAll("mon", "MMM");
        }
        SimpleDateFormat df = new SimpleDateFormat(strDateFormatForDisplay + timeFomatStr);
        String rc = (date == null) ? "" : df.format(date);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatDateTimeForDisplay", rc);
        }
        return rc;
    }

    /**
     * Format string according to the custom full date format
     *
     * @param dateString
     * @return String
     * @throws ParseException
     */
    public static String formatDateTimeForDisplay(String dateString) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatDateTimeForDisplay", new Object[]{dateString});
        }

        String rc = null;
        try {
            rc = (dateString == null || dateString.equals("")) ? "" : formatDateTimeForDisplay(getDtmFormat().parse(dateString));
        } catch (ParseException e) {
            l.throwing(Class.class.getName(), "formatDateTimeForDisplay", e);
            rc = dateString;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatDateTimeForDisplay(String)", rc);
        }
        return rc;
    }

    /**
     * Determines if System Parameter "CALENDAR_DATE_FORMAT" is set to US Date Format
     *
     * @return boolean
     */
    public static boolean isDateFormatUS() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "isDateFormatUS");
        }

        if (c_isDateFormatUS == null) {
            l.logp(Level.FINE, clsName, "isDateFormatUS", "Date Format Unknown: c_isDateFormatUS=" + c_isDateFormatUS);
            determineDateFormat();
        }
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, clsName, "isDateFormatUS", "c_isDateFormatUS = " + c_isDateFormatUS);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "determineDateFormat", c_isDateFormatUS.booleanValue());
        }
        return c_isDateFormatUS.booleanValue();
    }

    private static void determineDateFormat() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "determineDateFormat");
        }
        if (!UserSessionManager.isConfigured()) {
            c_isDateFormatUS = new Boolean(true);
        } else {
            if (((SimpleDateFormat) getDateFormat()).toPattern().toLowerCase().equals(getDateFormatForDisplayString().toLowerCase())) {
                l.logp(Level.FINE, clsName, "determineDateFormat", "true");
                c_isDateFormatUS = new Boolean(true);
            } else {
                l.logp(Level.FINE, clsName, "determineDateFormat", "false");
                c_isDateFormatUS = new Boolean(false);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "determineDateFormat");
        }
    }

    /**
     * Get the string value of date format pattern.
     * Now we only support date format patterns defined with MM dd yyyy , and we only use '/' as the delimiter.
     * If the calendar.dateformat property is defined in the applicationConfig-core.properties file or the calendar.dateformat property is overrided in the
     * customApplicationconfig-XXX.properties file, the method returns the value what is set to the property.
     * Otherwise the method returns the default value "MM/dd/yyyy".
     *
     * @return date format pattern string value.
     */
    public static String getDateFormatForDisplayString() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getDateFormatForDisplayString");
        }
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, clsName, "getDateFormatForDisplayString", "Entering: c_dateFormatForDisplayString = " + c_dateFormatForDisplayString);
        }
        if (!SysParmProvider.getInstance().isAvailable()) {
            return "MM/dd/yyyy";
        } else if (c_dateFormatForDisplayString == null) {
            c_dateFormatForDisplayString = SysParmProvider.getInstance().getSysParm("CALENDAR_DATE_FORMAT", "MM/dd/yyyy");
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty("calendar.dateformat")) {
                c_dateFormatForDisplayString = ApplicationContext.getInstance().getProperty("calendar.dateformat", "MM/dd/yyyy");
                l.logp(Level.FINE, clsName, "getDateFormatForDisplayString", "Overwriting Date Format String from configuration files: c_dateFormatForDisplayString=" + c_dateFormatForDisplayString);
            } else {
                l.logp(Level.FINE, clsName, "getDateFormatForDisplayString", "NO Date Format String in configuration files: c_dateFormatForDisplayString=" + c_dateFormatForDisplayString);
            }
            if (c_dateFormatForDisplayString != null) {
                //We are supporting only MM dd yyyy format with different delimeters
                c_dateFormatForDisplayString = c_dateFormatForDisplayString.toLowerCase();
                if (c_dateFormatForDisplayString.contains("mm"))
                    c_dateFormatForDisplayString = c_dateFormatForDisplayString.replaceAll("m", "M");
                if (!c_dateFormatForDisplayString.contains("MM") || !c_dateFormatForDisplayString.contains("dd")
                        || !c_dateFormatForDisplayString.contains("yyyy"))
                    l.logp(Level.SEVERE, clsName, "getDateFormatForDisplayString", "DATE FORMAT SETUP IS INCORRECT: " + c_dateFormatForDisplayString);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getDateFormatForDisplayString", c_dateFormatForDisplayString);
        }

        return c_dateFormatForDisplayString;
    }

    public static String getDateTimeFormatForDisplayString() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getDateTimeFormatForDisplayString");
        }

        if (c_dateTimeFormatForDisplayString == null) {
            c_dateTimeFormatForDisplayString = getDateFormatForDisplayString() + " HH:mm:ss";
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getDateTimeFormatForDisplayString", c_dateTimeFormatForDisplayString);
        }
        return c_dateTimeFormatForDisplayString;
    }

    public static String getDateTimeNoSecFormatForDisplayString() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getDateTimeFormatForDisplayString");
        }

        if (c_dateTimeNoSecFormatForDisplayString == null) {
            c_dateTimeNoSecFormatForDisplayString = getDateFormatForDisplayString() + " HH:mm";
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getDateTimeFormatForDisplayString", c_dateTimeNoSecFormatForDisplayString);
        }
        return c_dateTimeNoSecFormatForDisplayString;
    }

    private static int getDecimalScale() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getDecimalScale");
        }

        if (ApplicationContext.getInstance().hasProperty("decimal.scale")) {
            c_decimalScale = Integer.parseInt(ApplicationContext.getInstance().getProperty("decimal.scale", "2"));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getDecimalScale", c_decimalScale);
        }
        return c_decimalScale;
    }

    private static int getDecimalRoundingMode() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getDecimalRoundingMode");
        }

        if (ApplicationContext.getInstance().hasProperty("decimal.roundingMode")) {
            c_decimalRoundingMode = Integer.parseInt(ApplicationContext.getInstance().getProperty("decimal.roundingMode", String.valueOf(BigDecimal.ROUND_HALF_UP)));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getDecimalRoundingMode", c_decimalRoundingMode);
        }
        return c_decimalRoundingMode;
    }

    private static RoundingMode getFormatNumberRoundingMode() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getFormatNumberRoundingMode");
        }

        if (ApplicationContext.getInstance().hasProperty("number.format.round")) {
            boolean isFormatNumberRound = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("number.format.round", "false")).booleanValue();
            if (isFormatNumberRound) {
                c_formatNumberRoundingMode = RoundingMode.HALF_EVEN;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getFormatNumberRoundingMode", c_formatNumberRoundingMode);
        }
        return c_formatNumberRoundingMode;
    }


    /**
     * Formats a date object according to the current locale's Short datetime format
     *
     * @param date
     * @return String
     */
    public static String formatDateTime(Date date) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatDateTime", new Object[]{date});
        }
        String rc = (date == null) ? "" : getDtmFormat().format(date);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatDateTime", rc);
        }
        return rc;
    }

    /**
     * Converts a Date String to a Date object then formats it by calling formatDate
     *
     * @param value
     * @return Stirng
     */
    public static String formatDate(String value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatDate", new Object[]{value});
        }
        String rc = null;
        try {
            rc = formatDate(getDateFormat().parse(value));
        } catch (ParseException pe) {
            rc = "";
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatDate", rc);
        }
        return rc;
    }

    /**
     * Format a date to XML date format.
     * <p>The method will use {@link dti.oasis.util.DateUtils#XML_DATE_FORMAT_PATTERN} as date format.<p/>
     *
     * @param date
     * @return
     */
    public static String formatXmlDate(Date date) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatXmlDate", new Object[]{date});
        }

        String rc = (date == null) ? "" : formatDate(date, DateUtils.XML_DATE_FORMAT_PATTERN);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatXmlDate", rc);
        }
        return rc;
    }

    /**
     * Converts a DateTime String to a Date object then formats it by calling formatDate
     *
     * @param value
     * @return String
     */
    public static String formatDateTime(String value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatDateTime", new Object[]{value});
        }
        String rc = null;
        try {
            rc = formatDateTime(getDtmFormat().parse(value));
        } catch (ParseException pe) {
            rc = "";
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatDateTime", rc);
        }
        return rc;

    }

    /**
     * Converts a date String to a Date object and returns it
     *
     * @param date date in current locale's SHORT date format
     * @return Date
     * @throws ParseException
     */
    public static Date getDate(String date) throws ParseException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getDate", new Object[]{date});
        }
        if (l.isLoggable(Level.FINE)) {
            l.log(Level.FINER, "PARSING DATE STRING " + date + " Format " + ((SimpleDateFormat) getDateFormat()).toPattern());
        }
        Date dt = getDateFormat().parse(date);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getDate", dt);
        }
        return dt;
    }

    /**
     * Converts a numerical value to BigDecimal based on default scale and rounding mode
     *
     * @param input
     * @return BigDecimal
     * @throws ParseException
     */
    public static BigDecimal parseBigDecimal(double input) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "parseBigDecimal", new Object[]{input});
        }

        BigDecimal returnValue = parseBigDecimal(input, getDecimalScale());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "parseBigDecimal", returnValue);
        }
        return returnValue;
    }

    /**
     * Converts a numerical value to BigDecimal based on scale and default rounding mode
     *
     * @param input
     * @param scale
     * @return BigDecimal
     * @throws ParseException
     */
    public static BigDecimal parseBigDecimal(double input, int scale) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "parseBigDecimal", new Object[]{input, scale});
        }

        BigDecimal returnValue = parseBigDecimal(input, scale, getDecimalRoundingMode());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "parseBigDecimal", returnValue);
        }
        return returnValue;
    }

    /**
     * Converts a numerical value to BigDecimal based on scale and rounding mode
     *
     * @param input
     * @param scale
     * @param roundingMode
     * @return BigDecimal
     * @throws ParseException
     */
    public static BigDecimal parseBigDecimal(double input, int scale, int roundingMode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "parseBigDecimal", new Object[]{input, scale, roundingMode});
        }

        BigDecimal returnValue = BigDecimal.valueOf(input).setScale(scale, roundingMode);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "parseBigDecimal", returnValue);
        }
        return returnValue;
    }

    /**
     * Converts a string value to BigDecimal based on default scale and rounding mode
     *
     * @param input
     * @return BigDecimal
     * @throws ParseException
     */
    public static BigDecimal parseBigDecimal(String input) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "parseBigDecimal", new Object[]{input});
        }

        BigDecimal returnValue = parseBigDecimal(Double.valueOf(input).doubleValue(), getDecimalScale());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "parseBigDecimal", returnValue);
        }
        return returnValue;
    }

    /**
     * Converts a string value to BigDecimal based on scale and default rounding mode
     *
     * @param input
     * @param scale
     * @return BigDecimal
     * @throws ParseException
     */
    public static BigDecimal parseBigDecimal(String input, int scale) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "parseBigDecimal", new Object[]{input, scale});
        }

        BigDecimal returnValue = parseBigDecimal(Double.valueOf(input).doubleValue(), scale, getDecimalRoundingMode());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "parseBigDecimal", returnValue);
        }
        return returnValue;
    }

    /**
     * Converts a string value to BigDecimal based on scale and rounding mode
     *
     * @param input
     * @param scale
     * @param roundingMode
     * @return BigDecimal
     * @throws ParseException
     */
    public static BigDecimal parseBigDecimal(String input, int scale, int roundingMode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "parseBigDecimal", new Object[]{input, scale, roundingMode});
        }

        BigDecimal returnValue = BigDecimal.valueOf(Double.valueOf(input).doubleValue()).setScale(scale, roundingMode);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "parseBigDecimal", returnValue);
        }
        return returnValue;
    }

    /**
     * Determines if a string represents a date in localized format.
     *
     * @param input Input string.
     * @return boolean
     */
    public static boolean isDate(String input) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "isDate", new Object[]{input});
        }

        boolean rc = true;
        try {
            ParsePosition parsePos = new ParsePosition(0);
            getDateFormat().parse(input, parsePos);
            if (!(parsePos.getIndex() == input.length() && parsePos.getIndex() > 0)) {
                throw new ParseException("Unparseable date: \"" + input + "\"", parsePos.getErrorIndex());
            }
        } catch (Exception eDateTest) {
            rc = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "isDate", String.valueOf(rc));
        }
        return rc;
    }

    /**
     * Determines if a string represents a date-time in localized format.
     *
     * @param input
     * @return
     */
    public static boolean isDateTime(String input) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "isDateTime", new Object[]{input});
        }

        boolean rc = true;
        try {
            getDtmFormat().parse(input);
        } catch (ParseException e) {
            rc = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "isDateTime");
        }
        return rc;
    }

    /**
     * Determines if a string represents a date-time by given valid format
     *
     * @param input
     * @return
     */
    public static boolean isDateTime(String format, String input) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "isDateTime", new Object[]{format, input});
        }

        boolean rc = true;
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setLenient(false);
        try {
            formatter.parse(input);
        } catch (ParseException e) {
            rc = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "isDateTime");
        }
        return rc;
    }

    /**
     * Determines if a string can be converted into a long.
     *
     * @param input Input string.
     * @return boolean
     */
    public static boolean isLong(String input) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "isLong", new Object[]{input});
        }

        boolean rc = true;
        try {
            Long.parseLong(input);
        } catch (Exception eLongTest) {
            rc = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "isLong", String.valueOf(rc));
        }
        return rc;
    }

    /**
     * Determines if a string can be converted into a short.
     *
     * @param input Input string.
     * @return boolean
     */
    public static boolean isShort(String input) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "isShort", new Object[]{input});
        }

        boolean rc = true;
        try {
            Short.parseShort(input);
        } catch (Exception eShortTest) {
            rc = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "isShort", String.valueOf(rc));
        }
        return rc;
    }

    /**
     * Determines if a string can be converted into an int.
     *
     * @param input Input string.
     * @return boolean
     */
    public static boolean isInt(String input) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "isInt", new Object[]{input});
        }

        boolean rc = true;
        try {
            Integer.parseInt(input);
        } catch (Exception eIntTest) {
            rc = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "isInt", String.valueOf(rc));
        }
        return rc;
    }

    /**
     * Determines if a string can be converted into a float.
     *
     * @param input Input string.
     * @return boolean
     */
    public static boolean isFloat(String input) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "isFloat", new Object[]{input});
        }

        boolean rc = true;
        try {
            Float.parseFloat(input);
        } catch (Exception eFloatTest) {
            rc = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "isFloat", String.valueOf(rc));
        }
        return rc;
    }

    /**
     * Determines if a string can be converted into a double.
     *
     * @param input Input string.
     * @return boolean
     */
    public static boolean isDouble(String input) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "isDouble", new Object[]{input});
        }

        boolean rc = true;
        try {
            Double.parseDouble(input);
        } catch (Exception eDoubleTest) {
            rc = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "isDouble", String.valueOf(rc));
        }
        return rc;
    }

    /**
     * Returns true if input string is equal to "Y"; otherwise returns false.
     *
     * @param input Input string
     * @return boolean
     */
    public static boolean stringToBool(String input) {
        if (!StringUtils.isBlank(input) && input.equals(YES_VALUE)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns "Y" if input boolean is true; otherwise returns "N".
     *
     * @param input Input boolean
     * @return String
     */
    public static String boolToString(boolean input) {
        if (input) {
            return YES_VALUE;
        } else {
            return NO_VALUE;
        }
    }

    /**
     * Unformats a currency value string by removing "$" and then passing
     * value to method unformatNumber (i.e., converts "$9,999,99" to
     * "9999.99" and "$(9,999.99)" to "-9999.99").
     * Works only for strings formatted as U.S. currency.
     *
     * @param currValue
     * @return String
     */
    public static String unformatCurrency(String currValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "unformatCurrency", new Object[]{currValue});
        }
        String currencyValue = currValue;

        if (!StringUtils.isBlank(currValue)) {
            currValue = StringUtils.replace(currValue, dftCurrFormat.getCurrency().getSymbol(LocaleUtils.getOasisLocale()), EMPTY_STRING);
            currencyValue = unformatNumber(currValue);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "unformatCurrency", currencyValue);
        }
        return currencyValue;
    }

    /**
     * Unformats a numeric value string by removing "," and then changing
     * parentheses for negative number to minus sign "-" (i.e., converts
     * "1,234.56" to "1234.56" and "(1,234.56)" to "-1234.56").
     * Works only on for number strings in formats
     * 9,999.99 and (9,999.99).
     *
     * @param numValue
     * @return String
     */
    public static String unformatNumber(String numValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "unformatNumber", new Object[]{numValue});
        }

        if (!StringUtils.isBlank(numValue)) {
            numValue = StringUtils.replace(numValue, COMMA_VALUE, EMPTY_STRING);
            if (!StringUtils.isBlank(numValue)) {
                if (numValue.length() >= 2) {
                    if (numValue.charAt(0) == '(' && numValue.charAt(numValue.length() - 1) == ')') {
                        numValue = StringUtils.replace(numValue, OPEN_PAREN_VALUE, EMPTY_STRING);
                        numValue = StringUtils.replace(numValue, CLOSE_PAREN_VALUE, EMPTY_STRING);
                        numValue = new StringBuffer().append(MINUS_SIGN_VALUE).
                                append(numValue).toString();
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "unformatNumber", numValue);
        }
        return numValue;
    }

    /**
     * Mask of a field.
     *
     * @return
     */
    public static String getFieldMask() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getFieldMask");
        }

        if (ApplicationContext.getInstance().hasProperty("field.mask.format"))
            c_fieldMask = ApplicationContext.getInstance().getProperty("field.mask.format");
        else
            c_fieldMask = FIELD_MASK;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getFieldMask", c_fieldMask);
        }
        return c_fieldMask;
    }

    /**
     * Formats a string according to the custom phone format,
     * replace each # in the format with numeric chars found from a string, and return it.
     * if the phone number does not have enough numeric chars, the remaining format will be kept and return.
     * for example:
     *
     * with the phone format configured as ###-####
     *
     * to-be-formatted Phone   Result
     * 123                     123-####
     * 1234567899              123-4567
     * 12AB345sxx##qa678       123-4567
     * null                    ###-####
     *
     * @param strPhoneNumber: a string with or without format
     * @return String
     */
    public static String formatPhoneNumberForDisplay(String strPhoneNumber) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatPhoneNumberForDisplay", new Object[]{strPhoneNumber});
        }

        if (ActionHelper.isBase64(strPhoneNumber)) {
            strPhoneNumber = (String)ActionHelper.decodeField(strPhoneNumber);
        }

        String formattedNumber = getLocalPhoneNumberFormat();
        if (!StringUtils.isBlank(strPhoneNumber)) {
            String[] phoneNumbersArray = strPhoneNumber.replaceAll("\\D", "").split("");  // get rid of all non-numeric chars

            for (String str : phoneNumbersArray) {
                if (!StringUtils.isBlank(str)) {
                    if (formattedNumber.contains("#")) {
                        formattedNumber = formattedNumber.replaceFirst("#", str);
                    } else {
                        break;
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatPhoneNumberForDisplay(String)", formattedNumber);
        }
        return formattedNumber;
    }

    /**
     * Get configured phone number format
     * 108235: use # to represent numeric chars
     *
     * @return
     */
    public static String getLocalPhoneNumberFormat() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getLocalPhoneNumberFormat");
        }

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, clsName, "getLocalPhoneNumberFormat", "Entering: c_phoneNumberFormat = " + c_phoneNumberFormat);
        }
        if (c_phoneNumberFormat == null) {
            // get configured property first if possible
            if (ApplicationContext.getInstance().hasProperty("local.phonenumber.format")) {
                c_phoneNumberFormat = ApplicationContext.getInstance().getProperty("local.phonenumber.format");
                l.logp(Level.FINE, clsName, "getLocalPhoneNumberFormat", "local.phonenumber.format configured:" + c_phoneNumberFormat);
            }
            // try system parameter if have to
            if (StringUtils.isBlank(c_phoneNumberFormat)) {
                c_phoneNumberFormat = SysParmProvider.getInstance().getSysParm("LOCAL_PHONE_FORMAT", DEFAULT_PHONENUMBER_FORMAT);
                l.logp(Level.FINE, clsName, "getLocalPhoneNumberFormat", "local.phonenumber.format not configured, using system parameter LOCAL_PHONE_FORMAT" + c_phoneNumberFormat);
            }

            //get the max phone number characters config
            if (!DEFAULT_PHONENUMBER_FORMAT.equals(c_phoneNumberFormat)) {
                //We support only 7 characters now according to the phone field definition at contact and phone_number table
                //If the definition is changed, we need to change this parameter MAX_LOCAL_PHONE_LEN also
                int intMaxPhoneLength = 7;
                String strMaxPhoneLength = SysParmProvider.getInstance().getSysParm("MAX_LOCAL_PHONE_LEN", "7");
                if (isInt(strMaxPhoneLength)) {
                    //get the max phone number length
                    intMaxPhoneLength = Integer.parseInt(strMaxPhoneLength);
                }
                int intTotalCharacters = 0;
                StringBuffer strFinalPhoneNumberFormat = new StringBuffer();
                for (int i = 0; i < c_phoneNumberFormat.length(); i++) {
                    if (c_phoneNumberFormat.charAt(i) == '#') {
                        intTotalCharacters += 1;
                    }
                    if (intMaxPhoneLength >= intTotalCharacters || c_phoneNumberFormat.charAt(i) != '#')
                        strFinalPhoneNumberFormat.append(c_phoneNumberFormat.charAt(i));
                    else
                        break;
                }
                c_phoneNumberFormat = strFinalPhoneNumberFormat.toString();
                l.logp(Level.FINE, clsName, "getLocalPhoneNumberFormat", "Phone Format String used in eOASIS System: c_phoneNumberFormat=" + c_phoneNumberFormat);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getLocalPhoneNumberFormat", c_phoneNumberFormat);
        }
        return c_phoneNumberFormat;
    }

    public static String formatNumber(String numberStr, String pattern) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatNumber", new Object[]{numberStr, pattern});
        }

        Locale locale = LocaleUtils.getOasisLocale();
        //Number number = parseNumber(numberStr, locale);
        if (!StringUtils.isBlank(numberStr)) {
            if (numberStr.indexOf("$") > -1) {
                numberStr = numberStr.replaceAll("\\$", "");
            }
            numberStr = numberStr.replaceAll(",", "");
            BigDecimal number = new BigDecimal(numberStr);

            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, clsName, "formatNumber", "number = " + number);
            }
            numberStr = formatNumber(number, pattern, locale);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatNumber", numberStr);
        }
        return numberStr;
    }

    public static String formatNumber(Number number, String pattern, Locale locale) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "formatNumber", new Object[]{number, pattern, locale});
        }

        String formattedNumber = null;
        DecimalFormat df = null;
        pattern = normalizePattern(pattern);
        try {
            df = (DecimalFormat)
                    NumberFormat.getInstance(locale);
            df.applyPattern(pattern);
            df.setRoundingMode(getFormatNumberRoundingMode());

            formattedNumber = df.format(number);
        } catch (ClassCastException e) {
            formattedNumber = NumberFormat.getInstance(locale).format(number);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "formatNumber", formattedNumber);
        }
        return formattedNumber;
    }

    private static String normalizePattern(String javaPattern) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "normalizePattern", new Object[]{javaPattern});
        }

        String colorFlag = "[Red]";
        if (javaPattern.contains(colorFlag)) {
            javaPattern = javaPattern.replace(colorFlag, "");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "normalizePattern", javaPattern);
        }
        return javaPattern;
    }

    public static String getJsNbrFormatterPattern(String javaPattern) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "getJsNbrFormatterPattern", new Object[]{javaPattern});
        }

        String subPatternSeparator = ";";
        javaPattern = normalizePattern(javaPattern);
        if (javaPattern.contains(subPatternSeparator)) {
            javaPattern = javaPattern.split(subPatternSeparator)[0];
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "getJsNbrFormatterPattern", javaPattern);
        }
        return javaPattern;
    }

    public static boolean nbrFormatPatternHasParentheses(String javaPattern) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "nbrFormatPatternHasParentheses", new Object[]{javaPattern});
        }

        String subPatternSeparator = ";";
        javaPattern = normalizePattern(javaPattern);
        boolean result = false;
        if (javaPattern.contains(subPatternSeparator)) {
            javaPattern = javaPattern.split(subPatternSeparator)[1];
            if (javaPattern.indexOf("(") == 0 && (javaPattern.lastIndexOf(")") + 1) == javaPattern.length()) {
                result = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "nbrFormatPatternHasParentheses", result);
        }
        return result;
    }

    public static boolean nbrFormatPatternHasColor(String javaPattern) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(clsName, "nbrFormatPatternHasColor", new Object[]{javaPattern});
        }

        String colorFlag = "[Red]";
        boolean result = false;
        if (javaPattern.contains(colorFlag)) {
            result = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(clsName, "nbrFormatPatternHasColor", result);
        }
        return result;
    }

    /**
     * Create an inner class to initial the parameter for the refresh logic in SysParmProvider
     *
     * @return
     */
    public static class ClearCachedConstants implements SysParmProviderRefreshListener {
        @Override
        public void refresh() {
            if (l.isLoggable(Level.FINER)) {
                l.entering(clsName, "refresh", new Object[]{c_dateFormatForDisplayString,
                        c_dateTimeFormatForDisplayString, c_dateTimeNoSecFormatForDisplayString, c_phoneNumberFormat});
            }
            c_dateFormatForDisplayString = null;
            c_dateTimeFormatForDisplayString = null;
            c_dateTimeNoSecFormatForDisplayString = null;
            c_phoneNumberFormat = null;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(clsName, "refresh", new Object[]{c_dateFormatForDisplayString,
                        c_dateTimeFormatForDisplayString, c_dateTimeNoSecFormatForDisplayString, c_phoneNumberFormat});
            }
        }
    }

    static {
        //Register the listener to system parameter provider.
        SysParmProvider.getInstance().registerRefreshListener(new FormatUtils.ClearCachedConstants());
    }

    public static int getCurrentDateFullYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private static final Logger l = LogUtils.getLogger(FormatUtils.class);

}
