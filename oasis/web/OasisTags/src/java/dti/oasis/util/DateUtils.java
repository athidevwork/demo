package dti.oasis.util;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Date Utility methods
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 20, 2004
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/23/2007       wer         Added methods to parse a DateTime and a Date
 * 02/27/2007       sxm         Made c_dateTimeFormat and c_dateFormat public
 * 07/28/2008       joe         Added method minusXDays() to minc_dateFormatus days from a Date
 * 12/15/2009       joe         Added method isDate2AfterDate1() which is refactored from eClaim project
 * 02/11/2010       joe         Added methods getDayOfWeek(), getDateAdd() and countWeekend()
 * 05/14/2010       James       Added methods formatDateTime
 * 01/17/2011       clm         add new method stringToDate.
 * 12/22/2011       Michael     add new method addDay to add Day.
 * 03/07/2012       fcb         Added parseOasisDateToXMLDate and parseXMLDateToOasisDate.
 * 06/14/2012       fcb         Added additional validation for date in parseXMLDateToOasisDate.
 * 11/23/2012       Parker      enhancement issue 138228. Add a format time zone function
 * 01/08/2013       adeng       140424 - To make sure correctly format a datetime, modified parseDateTime to first match
 *                              the newly added format DATETIME_US_FORMAT_PATTERN, which include Hour in am/pm (1-12),
 *                              Minute in hour and Second in minute.
 * 05/17/2013       hxk         Issue 142978
 *                              1)  Add date overlap methods
 * 07/23/2013       fcb         parseDateTime: changed the exception type from ParseException to Exception as
 *                              parse() method can also throw a NumberFormatException and we cannot actually see the
 *                              real value in dateTime variable.
 * 07/25/2013       fcb         c_defaultDateTimeFormat, c_dateTimeFormat, c_timeZoneFormat, c_dateFormat, c_xmlDateFormat
 *                              and calendar converted to ThreadLocal to ensure that they are thread safe.
 *                              This is due to 2 different type of errors that were received when using DateUtils:
 *                               - java.lang.NumberFormatException: multiple points
 *                               - java.lang.NumberFormatException: For input string: ""
 *                              According to some Web Sites, these are typical errors with DateUtils when there is a
 *                              multithreading issue
 *                              (http://stackoverflow.com/questions/4021151/java-dateformat-is-not-threadsafe-what-does-this-leads-to)
 * 01/07/2014       kshen       Issue 148789. Changed the message key for unformat XML date.
 * 02/13/2014       htwang      Issue 150123 - Update isMMDDFormatDate method to make sure regular expressions match four numbers format Date only.
 * 05/12/2015       kshen       Added method parseISODateTime.
 * 05/27/2015       cv          162347 - Added new method getCustomWeekends. Validates if date is part of the weekend day.
 * 09/17/2015       Parker      Issue#165637 - Use ThreadLocal to make SimpleDateFormat thread safe.
 * 08/24/2016       dzhang      Issue 177970 : Add method parseXMLDateTimeToOasisDate.
 * 07/31/2017       lzhang      Issue 182769 : Add isTargetDateNotInDatesPeriod
 * ---------------------------------------------------
 */

public class DateUtils {
    protected static final String clsName = DateUtils.class.getName();
    private static final Logger l = LogUtils.getLogger(DateUtils.class);

    /**
     * The default datetime format pattern used to parse the input datatime value.
     */
    public static final String DEFAULT_DATETIME_FORMAT_PATTERN = "MM/dd/yyyy HH:mm:ss";
    public static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
    public static final String DATETIME_TIMEZONE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.S z";
    public static final String XML_DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String XML_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ISO_DATETIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATETIME_WITHOUT_SEPARATOR_FORMAT_PATTERN = "yyyyMMddHHmmss";
    public static final String DATETIME_WITH_MILLISECONDS_FORMAT_PATTERN = "MMM dd,yyyy HH:mm:ss.SSSZ";
    public static final String DATETIME_US_TIMEZONE_FORMAT_PATTERN = "MM/dd/yyyy hh:mm:ss a z";

    /**
     * The default date format pattern used to format the output date value.
     */
    public static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy";

    public static final short DD_SECS = 0;
    public static final short DD_DAYS = 1;

    public static final ThreadLocal<DateFormat> c_defaultDateTimeFormat
                = new ThreadLocal<DateFormat>(){
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(DEFAULT_DATETIME_FORMAT_PATTERN);
            }
    };

    public static final ThreadLocal<DateFormat> c_dateTimeFormat
                = new ThreadLocal<DateFormat>(){
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(DATETIME_FORMAT_PATTERN);
            }
    };

    public static final ThreadLocal<DateFormat> c_timeZoneFormat
                = new ThreadLocal<DateFormat>(){
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(DATETIME_TIMEZONE_FORMAT_PATTERN);
            }
    };

    public static final ThreadLocal<DateFormat> c_dateFormat
        = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT_PATTERN);
        }
    };

    public static final ThreadLocal<DateFormat> c_xmlDateFormat
        = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(XML_DATE_FORMAT_PATTERN);
        }
    };

    public static final ThreadLocal<DateFormat> c_dateTimeFormatWithoutSeparator
        = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATETIME_WITHOUT_SEPARATOR_FORMAT_PATTERN);
        }
    };

    public static final ThreadLocal<DateFormat> c_dateTimeFormatMilliSeconds
        = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATETIME_WITH_MILLISECONDS_FORMAT_PATTERN);
        }
    };

    public static final ThreadLocal<DateFormat> c_dateTimeUSWithTimeZone
        = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATETIME_US_TIMEZONE_FORMAT_PATTERN);
        }
    };

    public static final ThreadLocal<Calendar> calendar
            = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return new GregorianCalendar();
        }
    };

    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = 60 * ONE_SECOND;
    private static final int ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static final int EPOCH_JULIAN_DAY = 2440588; // January 1, 1970 (Gregorian)

    /**
     * To convert a string to a date by given valid format
     * @param time
     * @return
     */
    public static Date parseXMLDate(String time) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "parseXMLDate", new Object[]{time});
        }

        SimpleDateFormat formatter = new SimpleDateFormat(XML_DATE_FORMAT_PATTERN);

        ParsePosition pos = new ParsePosition(0);
        Date ctime = formatter.parse(time, pos);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "parseXMLDate", ctime);
        }

        return ctime;
    }

    /**
     * Converts a Calendar to a Julian date
     *
     * @param c Calendar
     * @return int Julian date
     */
    public static int toJulian(GregorianCalendar c) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "toJulian", new Object[]{c});
        }
        int year = c.get(Calendar.YEAR);
        int jy = (year < 0) ? 1 : year;
        int month = c.get(Calendar.MONTH) + 1;
        int jm = month;
        if (month > 2)
            jm++;
        else {
            jy--;
            jm += 13;
        }
        int day = c.get(Calendar.DAY_OF_MONTH);
        int jul = (int) (Math.floor(365.25 * jy) + Math.floor(30.6001 * jm) +
                day + 1720995.0);
        int IGREG = 15 + 31 * (10 + 12 * 1582);
        if (day + 31 * (month + 12 * year) >= IGREG) {
            // change to gregorian
            int ja = (int) (0.01 * jy);
            jul += 2 - ja + (int) (0.25 * ja);
        }
        l.exiting(clsName, "toJulian", new Integer(jul));
        return jul;
    }

    /**
     * Returns the # of days between two dates,it returns the int value of dt2 - dt1
     *
     * @param dt1 date as String (mm/dd/yyyy)
     * @param dt2 date as String (mm/dd/yyyy)
     * @return int # of days difference(dt2-dt1)
     * @throws java.text.ParseException
     */
    public static int daysDiff(String dt1, String dt2) throws ParseException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "daysDiff", new Object[]{dt1, dt2});
        }
        GregorianCalendar c1 = new GregorianCalendar();
        c1.setTime(FormatUtils.getDate(dt1));
        int g1 = toJulian(c1);
        c1.setTime(FormatUtils.getDate(dt2));
        int i = toJulian(c1) - g1;
        l.exiting(clsName, "daysDiff", new Integer(i));
        return i;
    }

    /**
     * Returns the number of either days or seconds between two Date objects
     *
     * @param type either DD_DAYS or DD_SECS
     * @param dt1
     * @param dt2
     * @return long # of incremements difference
     */
    public static long dateDiff(short type, Date dt1, Date dt2) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "dateDiff", new Object[]{type, dt1, dt2});
        }
        GregorianCalendar c1 = new GregorianCalendar();
        GregorianCalendar c2 = new GregorianCalendar();
        c1.setTime(dt1);
        c2.setTime(dt2);

        long diff = 0;
        switch (type) {
            case DD_DAYS:
                // create new calendars w/ no hours/mins/secs
                int y = c1.get(Calendar.YEAR);
                int m = c1.get(Calendar.MONTH);
                int d = c1.get(Calendar.DAY_OF_MONTH);
                c1 = new GregorianCalendar(y, m, d);
                y = c2.get(Calendar.YEAR);
                m = c2.get(Calendar.MONTH);
                d = c2.get(Calendar.DAY_OF_MONTH);
                c2 = new GregorianCalendar(y, m, d);
                diff = (c2.getTime().getTime() - c1.getTime().getTime()) / 86400000;
                break;
            case DD_SECS:
                diff = (c2.getTime().getTime() - c1.getTime().getTime()) / 1000;
                break;
        }
        l.exiting(clsName, "dateDiff", new Long(diff));
        return diff;


    }

    /**
     * Returns the number of either days or seconds between two Date objects
     *
     * @param type DD_SECS or DD_DAYS
     * @param dt1  Date as String (mm/dd/yyyy)
     * @param dt2
     * @return long # of increments difference
     * @throws ParseException
     */
    public static long dateDiff(short type, String dt1, Date dt2) throws ParseException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "dateDiff", new Object[]{type, dt1, dt2});
        }
        long lg = dateDiff(type, FormatUtils.getDate(dt1), dt2);
        l.exiting(clsName, "dateDiff", new Long(lg));
        return lg;
    }

    public static Date parseDateTime(String dateTime) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "parseDateTime", new Object[]{dateTime});
        }

        Date dt = null;
        try {
            dt = c_defaultDateTimeFormat.get().parse(dateTime);
        }
        catch (Exception pex) {
            try {
                dt = c_dateFormat.get().parse(dateTime);
            }
            catch (Exception e) {
                try {
                    dt = c_xmlDateFormat.get().parse(dateTime);
                }
                catch (Exception e1) {
                    try {
                        dt = c_dateTimeFormat.get().parse(dateTime);
                    }
                    catch (Exception e11) {
                        try {
                            dt = new Date(Date.parse(dateTime));
                        }
                        catch (Exception e2) {
                            AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the datetime '" + dateTime + "'", e2);
                            l.throwing(DateUtils.class.getName(), "parseDateTime", ae);
                            throw ae;
                        }
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "parseDateTime", dt);
        }
        return dt;
    }

    public static Date parseISODateTime(String dateTime) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "parseISODateTime", new Object[]{dateTime});
        }

        Date date = parseDate(dateTime, ISO_DATETIME_FORMAT_PATTERN);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "parseISODateTime", date);
        }
        return date;
    }

    public static Date parseDate(String date) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "parseDate", new Object[]{date});
        }

        Date dt = null;
        try {
            dt = c_dateFormat.get().parse(date);
        }
        catch (Exception e1) {
            try {
                dt = new Date(Date.parse(date));
            }
            catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the date '" + date + "'", e);
                l.throwing(DateUtils.class.getName(), "parseDate", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "parseDate", dt);
        }
        return dt;
    }

    /**
     * To convert a string to a date by given valid format
     * @param time
     * @return
     */
    public static Date parseDate(String time, String format) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "parseDate", new Object[]{time, format});
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format);

        ParsePosition pos = new ParsePosition(0);
        Date ctime = formatter.parse(time, pos);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "parseDate", ctime);
        }

        return ctime;
    }

    /**
     * Transform Date to String.
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return c_dateFormat.get().format(date);
    }



    /**
     * Transform Date to String with time.
     *
     * @param date
     * @return
     */
    public static String formatDateTime(Date date) {
        return c_defaultDateTimeFormat.get().format(date);
    }


    /**
     * Transform Date to String with time.
     *
     * @param date
     * @return
     */
    public static String formatDateTimeWithoutSeparator(Date date) {
        return c_dateTimeFormatWithoutSeparator.get().format(date);
    }

    /**
     * Transform Date to String with time.
     *
     * @param date
     * @return
     */
    public static String formatDateTimeUSWithTimeZone(Date date) {
        return c_dateTimeUSWithTimeZone.get().format(date);
    }

    /**
     * Transform Date to String with time and time zone.
     *
     * @param date
     * @return
     */
    public static String formatDateTimeAndTimeZone(Date date) {
        return c_timeZoneFormat.get().format(date);
    }

    /**
     * Get Year of the date.
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        calendar.get().setTime(date);
        return calendar.get().get(Calendar.YEAR);
    }

    /**
     * Get Month of the date.
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        calendar.get().setTime(date);
        return calendar.get().get(Calendar.MONTH) + 1;
    }

    /**
     * Get the week of month from date.
     *
     * @param date
     * @return
     */
    public static int getWeekOfMonth(Date date) {
        calendar.get().setTime(date);
        return calendar.get().get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * Get day of month from date.
     *
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        calendar.get().setTime(date);
        return calendar.get().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get day of week from date
     *
     * @param date
     * @return 1 means Sunday, 2 means Monday, 7 means Saturday
     */
    public static int getDayOfWeek(Date date) {
        calendar.get().setTime(date);
        return calendar.get().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Create a <code>Date</code> object
     *
     * @param year  The real year: do not subtract 1900
     * @param month The real month: do not subtract 1
     * @param day
     * @return
     */
    public static Date makeDate(int year, int month, int day) {
        calendar.get().clear();

        calendar.get().set(Calendar.YEAR, year);
        calendar.get().set(Calendar.MONTH, month - 1);
        calendar.get().set(Calendar.DATE, day);
        calendar.get().set(Calendar.HOUR_OF_DAY, 0);
        calendar.get().set(Calendar.MINUTE, 0);
        calendar.get().set(Calendar.SECOND, 0);
        calendar.get().set(Calendar.MILLISECOND, 0);

        return calendar.get().getTime();
    }

    /**
     * To minus xDays from the date
     *
     * @param date  a date
     * @param xDays the days you want to minus from the date
     * @return new date
     */
    public static Date minusXDays(Date date, int xDays) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        int day = toJulian(gc);
        int nn = day - xDays;
        return new Date(julianDayToMillis(nn));
    }

    public static boolean isMMDDFormatDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return true;
        }
        // As MMDD format date requires 4 numbers, January 1 format must be 0101.
        // If user enters 11 instead of 0101, it should not be matched.
        String datePattern = "(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])";
        if (Pattern.matches(datePattern, dateStr)) {
            String mStr = dateStr.substring(0, 2);
            String dStr = dateStr.substring(2, 4);
            if(mStr.charAt(0) == '0') mStr = mStr.substring(1,2);
            if(dStr.charAt(0) == '0') dStr = dStr.substring(1,2);
            int m = Integer.parseInt(mStr);
            int d = Integer.parseInt(dStr);
            int[] maxDays = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            if (d <= maxDays[m - 1])
                return true;
        }
        return false;
    }

    /**
     * Checks if date2 is after date1.
     *
     * @param dt1
     * @param dt2
     * @return 'Y' if yes, 'U' if one of the fields is not a date, 'N'
     *         if No.
     */
    public static char isDate2AfterDate1(String dt1, String dt2) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "isDate2AfterDate1", new Object[]{dt1, dt2});
        }
        char rc;
        try {
            rc = (DateUtils.daysDiff(dt1, dt2) > 0) ? 'Y' : 'N';
        }
        catch (ParseException e) {
            // added by kshen, 02/14/2007
            // log exception information
            l.logp(Level.FINER, DateUtils.class.getName(), "isDate2AfterDate1", e.getMessage(), e);
            rc = 'U';
        }
        l.exiting(DateUtils.class.getName(), "isDate2AfterDate1", new Character(rc));
        return rc;
    }

    /**
     * Get the date object for the given date plus the amount day
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date getDateAdd(Date date, int amount) {
        calendar.get().setTime(date);
        calendar.get().add(GregorianCalendar.DATE, amount);
        return calendar.get().getTime();
    }

    /**
     * Count the weekedn between startDate and endDate
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int countWeekend(Date startDate, Date endDate) {
        int result = 0;
        // Get the days between these two date
        long sumDays = Math.abs(dateDiff(DD_DAYS, startDate, endDate));
        int dayOfWeek = 0;
        for (int i = 0; i <= sumDays; i++) {
            dayOfWeek = getDayOfWeek(getDateAdd(startDate, i));
            if (dayOfWeek == 1 || dayOfWeek == 7) { // 1 means Sunday, 7 means Saturday
                result++;
            }
        }
        return result;
    }

    public static List getWeekends(Date startDate, Date endDate) {
        List weekends = new ArrayList();
        // Get the days between these two date
        long sumDays = Math.abs(dateDiff(DD_DAYS, startDate, endDate));
        int dayOfWeek = 0;
        for (int i = 0; i <= sumDays; i++) {
            Date d = getDateAdd(startDate, i);
            dayOfWeek = getDayOfWeek(d);
            if (dayOfWeek == 1 || dayOfWeek == 7) { // 1 means Sunday, 7 means Saturday
                weekends.add(d);
            }
        }
        return weekends;
    }

    public static List getCustomWeekends(Date startDate, Date endDate, String[] weekendDays) {
        List weekends = new ArrayList();
        // Get the days between these two date
        long sumDays = Math.abs(dateDiff(DD_DAYS, startDate, endDate));
        int dayOfWeek = 0;
        for (int i = 0; i <= sumDays; i++) {
            Date d = getDateAdd(startDate, i);
            dayOfWeek = getDayOfWeek(d);
            for (int x = 0; x < weekendDays.length; x++) {
                if (dayOfWeek == Integer.parseInt(weekendDays[x])) {
                    weekends.add(d);
                    break;
                }
            }
        }
        return weekends;
    }

    private static long julianDayToMillis(long julian) {
        return (julian - EPOCH_JULIAN_DAY) * ONE_DAY;
    }

    public static String parseOasisDateToUSXMLDate(String dateTime) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "parseOasisDateToUSXMLDate", new Object[]{dateTime});
        }

        String returnDate;

        if (dateTime==null || "".equalsIgnoreCase(dateTime)) {
            returnDate = dateTime;
        }
        else {
            SimpleDateFormat inputFormat = new SimpleDateFormat(DATETIME_FORMAT_PATTERN);
            SimpleDateFormat outputFormat = new SimpleDateFormat(XML_DATE_FORMAT_PATTERN);

            try {
                returnDate = outputFormat.format(inputFormat.parse(dateTime));
            }
            catch (ParseException e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the datetime '" + dateTime + "'", e);
                l.throwing(DateUtils.class.getName(), "parseOasisDateToUSXMLDate", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "parseOasisDateToUSXMLDate", returnDate);
        }

        return returnDate;
    }

    public static String parseOasisDateToXMLDate(String dateTime) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "parseOasisDateToXMLDate", new Object[]{dateTime});
        }

        String returnDate;

        if (dateTime==null || "".equalsIgnoreCase(dateTime)) {
            returnDate = dateTime;
        }
        else {
            SimpleDateFormat inputFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            SimpleDateFormat outputFormat = new SimpleDateFormat(XML_DATE_FORMAT_PATTERN);

            try {
                returnDate = outputFormat.format(inputFormat.parse(dateTime));
            }
            catch (ParseException e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the datetime '" + dateTime + "'", e);
                l.throwing(DateUtils.class.getName(), "parseOasisDateToXMLDate", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "parseOasisDateToXMLDate", returnDate);
        }

        return returnDate;
    }

    public static String parseXMLDateToOasisDate(String dateTime) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "parseXMLDateToOasisDate", new Object[]{dateTime});
        }

        String returnDate;

        if (dateTime==null || "".equalsIgnoreCase(dateTime)) {
            returnDate = dateTime;
        }
        else {
            if (!"-".equalsIgnoreCase(dateTime.substring(4,5)) || !"-".equalsIgnoreCase(dateTime.substring(7,8))) {
                AppException ae = new AppException("ows.invalid.xml.date", "", new String[]{dateTime});
                l.throwing(DateUtils.class.getName(), "parseXMLDateToOasisDate", ae);
                throw ae;
            }

            try {
                StringUtils.validateNumeric(dateTime.substring(0, 4));
                StringUtils.validateNumeric(dateTime.substring(5, 7));
                StringUtils.validateNumeric(dateTime.substring(8));
            } catch(Exception e) {
                AppException ae = new AppException("ows.invalid.xml.date", "", new String[]{dateTime});
                l.throwing(DateUtils.class.getName(), "parseXMLDateToOasisDate", ae);
                throw ae;
            }

            SimpleDateFormat inputFormat = new SimpleDateFormat(XML_DATE_FORMAT_PATTERN);
            SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

            try {
                returnDate = outputFormat.format(inputFormat.parse(dateTime));
            }
            catch (ParseException e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the datetime '" + dateTime + "'", e);
                l.throwing(DateUtils.class.getName(), "parseXMLDateToOasisDate", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "parseXMLDateToOasisDate", returnDate);
        }

        return returnDate;
    }

    /**
     * parse xml date time yyyy-mm-dd'T'hh:mm:ss to defaut mm/dd/yyyy HH:mm:ss
     *
     * @param dateTime
     * @return
     */
    public static String parseXMLDateTimeToOasisDate(String dateTime) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "parseXMLDateTimeToOasisDate", new Object[]{dateTime});
        }

        String returnDate;

        if (dateTime == null || "".equalsIgnoreCase(dateTime)) {
            returnDate = dateTime;
        } else {
            if (!"-".equalsIgnoreCase(dateTime.substring(4, 5)) || !"-".equalsIgnoreCase(dateTime.substring(7, 8))
                    || !"T".equalsIgnoreCase(dateTime.substring(10, 11)) || !":".equalsIgnoreCase(dateTime.substring(13, 14))
                    || !":".equalsIgnoreCase(dateTime.substring(16, 17))) {
                AppException ae = new AppException("ows.invalid.xml.date.time", "Error occurred when convert xml date time", new String[]{dateTime});
                l.throwing(DateUtils.class.getName(), "parseXMLDateTimeToOasisDate", ae);
                throw ae;
            }

            try {
                StringUtils.validateNumeric(dateTime.substring(0, 4));
                StringUtils.validateNumeric(dateTime.substring(5, 7));
                StringUtils.validateNumeric(dateTime.substring(8, 10));
                StringUtils.validateNumeric(dateTime.substring(11, 13));
                StringUtils.validateNumeric(dateTime.substring(14, 16));
                StringUtils.validateNumeric(dateTime.substring(17));
            } catch (Exception e) {
                AppException ae = new AppException("ows.invalid.xml.date.time", "Error occurred when convert xml date time", new String[]{dateTime});
                l.throwing(DateUtils.class.getName(), "parseXMLDateTimeToOasisDate", ae);
                throw ae;
            }

            SimpleDateFormat inputFormat = new SimpleDateFormat(XML_DATE_TIME_FORMAT_PATTERN);
            SimpleDateFormat outputFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT_PATTERN);

            try {
                returnDate = outputFormat.format(inputFormat.parse(dateTime));
            } catch (ParseException e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the datetime '" + dateTime + "'", e);
                l.throwing(DateUtils.class.getName(), "parseXMLDateTimeToOasisDate", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "parseXMLDateToOasisDate", returnDate);
        }

        return returnDate;
    }

    public static void main(String[] args) throws Exception {
        String d = "02/11/2010";
        Date date = DateUtils.parseDate(d);
        int year = DateUtils.getYear(date);
        int month = DateUtils.getMonth(date);
        int week = DateUtils.getWeekOfMonth(date);
        int dayOfMonth = DateUtils.getDayOfMonth(date);
        int dayOfWeek = DateUtils.getDayOfWeek(date);
        System.out.println("year=" + year + ", month=" + month + ", week=" + week + ", dayOfMonth=" + dayOfMonth + ", dayOfWeek=" + dayOfWeek);
        String d2 = "02/27/2010";
        Date date2 = DateUtils.parseDate(d2);
        int weekends = DateUtils.countWeekend(date, date2);
        System.out.println("weekends between "+d+" and "+d2+" are "+weekends);
        List weekendList = DateUtils.getWeekends(date, date2);
        System.out.println("weekends list between "+d+" and "+d2+" are "+weekendList);

        Date nd = DateUtils.makeDate(year + 1, month, dayOfMonth);
        System.out.println("new date=" + nd);

        System.out.println(DateUtils.minusXDays(FormatUtils.getDate("07/28/2008"), -365));

        String d3 = "02/12/2010";
        String d4 = "02/12/2010";
        System.out.println(d4+" is after "+d3+"? "+DateUtils.isDate2AfterDate1(d3, d4));
          String date1 = "2016-01-01T15:12:20";
          String a = parseXMLDateTimeToOasisDate(date1);
          System.out.println(a);
    }

    public static String addDay(String s, int n) {
        try {
            GregorianCalendar cd = new GregorianCalendar();
            cd.setTime(FormatUtils.getDate(s));
            cd.add(Calendar.DATE, n);
            return FormatUtils.formatDateForDisplay(cd.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean dateRangeOverlap(String inFromDate1, 
                                           String inToDate1,
                                           String inFromDate2,
                                           String inToDate2){
        Date   fromDate1 = new Date();
        Date   toDate1   = new Date();
        Date   fromDate2 = new Date();
        Date   toDate2   = new Date();

        //Reformat dates to facilitate comparison
        if (inFromDate1 == null ||
            "".equals(inFromDate1)) {
            fromDate1 = parseDate("01/01/1900");
        } else
        {
            fromDate1  = parseDate(inFromDate1);
        }
        if (inToDate1 == null ||
                "".equals(inToDate1)) {
            toDate1 = parseDate("01/01/3000");
        } else
        {
            toDate1 = parseDate(inToDate1);
        }

        if (inFromDate2 == null ||
                "".equals(inFromDate2)) {
            fromDate2 = parseDate("01/01/1900");
        } else
        {
            fromDate2 = parseDate(inFromDate2);
        }
        if (inToDate2 == null ||
                "".equals(inToDate2)) {
            toDate2 = parseDate("01/01/3000");
        } else
        {
            toDate2 = parseDate(inToDate2);
        }

        return dateRangeOverlap(fromDate1,toDate1,fromDate2,toDate2);

    }
    public static Boolean dateRangeOverlap(Date fromDate1,
                                           Date toDate1,
                                           Date fromDate2,
                                           Date toDate2){
        Boolean retval   = false;


        if ((fromDate1.equals(toDate1)) || (fromDate2.equals(toDate2))) {
              retval = false;
        } else
        {

            if ((fromDate1.after(fromDate2)||fromDate1.equals(fromDate2)) && (fromDate1.before( toDate2))) {
                retval = true;
            } else
            if ((fromDate2.after(fromDate1)||fromDate2.equals(fromDate1)) && (fromDate2.before( toDate1))) {
                retval = true;
            }

        }

        return retval;
    }
    public static String convertDateToDateOnlyString(Date serverDate) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "convertDateToDateOnlyString", new Object[]{serverDate});
        }

        String dateString = "";

        if(serverDate!=null) {
            dateString = c_xmlDateFormat.get().format(serverDate);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "convertDateToDateOnlyString", dateString);
        }
        return dateString;
    }

    public static Date convertDateStringToDate(String dateString) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "convertDateStringToDate", new Object[]{dateString});
        }

        Date date = null;

        if(!StringUtils.isBlank(dateString)) {
            try {
                date = parseDate(dateString);
            } catch (Exception e) {
                date = parseXMLDate(dateString);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "convertDateStringToDate", date);
        }
        return date;
    }

    public static boolean isTargetDateNotInDatesPeriod(String targetDate, String date1, String date2) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "isTargetDateNotInDatesPeriod", new Object[]{targetDate, date1, date2});
        }
        boolean retval = false;
        try {
            if (DateUtils.daysDiff(date1, targetDate) < 0 || DateUtils.daysDiff(date2,targetDate) >= 0){
                retval = true;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "isTargetDateNotInDatesPeriod", retval);
        }
        return retval;
    }
}
