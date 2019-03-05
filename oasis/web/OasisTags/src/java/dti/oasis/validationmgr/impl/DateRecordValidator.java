package dti.oasis.validationmgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.validationmgr.RecordValidator;

import java.util.logging.Logger;

/**
 * This class implements validation of date.
 * <p/>
 * <p/>
 * Rule -
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 21, 2008
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class DateRecordValidator implements RecordValidator {

    /**
     * Validate the date field in given record set.
     *
     * @param inputRecord a data Record
     * @return true if the RecordSet is valid; otherwise false.
     */
    public boolean validate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validate", new Object[]{inputRecord});
        boolean isValid = true;
        for (int i = 0; i < getDateFieldNames().length; i++) {
            // get current values
            String dateFieldName = getDateFieldNames()[i];
            if (inputRecord.hasFieldValue(dateFieldName)) {
                String dateString = inputRecord.getStringValue(dateFieldName);
                if (!isValidDate(dateString)) {
                    addErrorMessage(inputRecord, dateFieldName);
                    isValid = false;
                }
            }
        }
        l.exiting(getClass().getName(), "validate", Boolean.valueOf(isValid));
        return isValid;
    }

    /**
     * get message parameters
     *
     * @param inputRecord
     * @param dateFieldName
     * @return
     */
    protected Object[] getParmameters(Record inputRecord, String dateFieldName) {
        Logger l = LogUtils.enterLog(getClass(), "getParmameters", new Object[]{inputRecord, dateFieldName});
        // set row numbers as message parameters first
        int numParms = getParmFieldNames().length;
        Object[] parms = new Object[numParms + 1];
        parms[0] = inputRecord.getFieldValue(dateFieldName);
        // set additional message parameters
        for (int i = 0; i < numParms; i++) {
            String parmFieldName = getParmFieldNames()[i];
            if (inputRecord.hasStringValue(parmFieldName + "LOVLABEL"))
                parms[i + 1] = inputRecord.getFieldValue(parmFieldName + "LOVLABEL");
            else
                parms[i + 1] = inputRecord.getFieldValue(parmFieldName);
        }
        l.exiting(getClass().getName(), "getParmameters");
        return parms;
    }

    /**
     * Add error message
     *
     * @param inputRecord
     * @param dateFieldName
     */
    protected void addErrorMessage(Record inputRecord, String dateFieldName) {
        Logger l = LogUtils.enterLog(getClass(), "addErrorMessage", new Object[]{inputRecord, dateFieldName});
        if (getIdFieldName() == null) {
            MessageManager.getInstance().addErrorMessage(getMessageKey(), getParmameters(inputRecord, dateFieldName),
                dateFieldName);
        }
        else {
            MessageManager.getInstance().addErrorMessage(getMessageKey(), getParmameters(inputRecord, dateFieldName),
                dateFieldName, inputRecord.getStringValue(getIdFieldName()));
        }

        l.exiting(getClass().getName(), "addErrorMessage");
    }

    /**
     * check whether date is valid
     *
     * @param date
     * @return returns true if arguments year, month, and day form a valid date.
     */
    public boolean isValidDate(String date) {
        Logger l = LogUtils.enterLog(getClass(), "isValidDate", new Object[]{date});
        String[] tokens = date.split("/");
        String month = tokens[0];
        String day = tokens[1];
        String year = tokens[2];
        boolean isValidDate = isValidDate(year, month, day);
        l.exiting(getClass().getName(), "isValidDate", new Boolean(isValidDate));
        return isValidDate;
    }

    /**
     * check whether date is valid
     *
     * @param year
     * @param month
     * @param day
     * @return returns true if arguments year, month, and day form a valid date.
     */
    public boolean isValidDate(String year, String month, String day) {
        Logger l = LogUtils.enterLog(getClass(), "isValidDate", new Object[]{year});
        boolean isValidDate = false;
        // catch invalid years (not 2- or 4-digit) and invalid months and days.
        if (!(isYear(year) && isMonth(month) && isDay(day))) {
            isValidDate = false;
        }
        else {
            int intYear = Integer.parseInt(year);
            int intMonth = Integer.parseInt(month);
            int intDay = Integer.parseInt(day);

            // catch invalid days, except for February
            if (intDay > daysInMonth[intMonth - 1]) {
                isValidDate = false;
            }
            else {
                // catch invalid days for February
                if ((intMonth == 2) && (intDay > daysInFebruary(intYear))) {
                    isValidDate = false;
                }
                else {
                    isValidDate = true;
                }
            }
        }
        l.exiting(getClass().getName(), "isValidDate", new Boolean(isValidDate));
        return isValidDate;
    }

    /**
     * check whether the given Year is valid
     *
     * @param year
     * @return returns true if string s is a valid Year number.  Must be 2 or 4 digits only.
     */
    public boolean isYear(String year) {
        Logger l = LogUtils.enterLog(getClass(), "isYear", new Object[]{year});
        boolean isYear = false;
        if (!StringUtils.isBlank(year)) {
            if (FormatUtils.isLong(year) && Long.parseLong(year) > 0) {
                isYear = ((year.length() == 2) || (year.length() == 4));
            }
            else {
                isYear = false;
            }
        }
        l.exiting(getClass().getName(), "isYear", new Boolean(isYear));
        return isYear;
    }

    /**
     * check whether the given month is valid
     *
     * @param month
     * @return returns true if string s is a valid month number between 1 and 12.
     */
    public boolean isMonth(String month) {
        Logger l = LogUtils.enterLog(getClass(), "isMonth", new Object[]{month});
        boolean isMonth = false;
        if (!StringUtils.isBlank(month)) {
            isMonth = isIntegerInRange(month, 1, 12);
        }
        l.exiting(getClass().getName(), "isMonth", new Boolean(isMonth));
        return isMonth;
    }

    /**
     * check whether the given day is valid
     *
     * @param day
     * @return returns true if string s is a valid day number between 1 and 31.
     */
    public boolean isDay(String day) {
        Logger l = LogUtils.enterLog(getClass(), "isDay", new Object[]{day});
        boolean isDay = false;
        if (!StringUtils.isBlank(day)) {
            isDay = isIntegerInRange(day, 1, 31);
        }
        l.exiting(getClass().getName(), "isDay", new Boolean(isDay));
        return isDay;
    }

    /**
     * check whether the given string is in range
     *
     * @param s
     * @param min
     * @param max
     * @return
     */
    public boolean isIntegerInRange(String s, int min, int max) {
        Logger l = LogUtils.enterLog(getClass(), "isIntegerInRange",
            new Object[]{s, new Integer(min), new Integer(max)});
        boolean isInRange = false;
        if (!StringUtils.isBlank(s)) {
            if (!FormatUtils.isLong(s)) return false;
            int num = Integer.parseInt(s);
            isInRange = ((num >= min) && (num <= max));
        }
        l.exiting(getClass().getName(), "isIntegerInRange", new Boolean(isInRange));
        return isInRange;
    }

    /**
     * get days in February
     * <p/>
     * Rule:
     * February has 29 days in any year evenly divisible by four,
     * EXCEPT for centurial years which are not also divisible by 400.
     *
     * @param year
     * @return number of days in February of that year.
     */
    public int daysInFebruary(int year) {
        Logger l = LogUtils.enterLog(getClass(), "daysInFebruary",
            new Object[]{new Integer(year)});
        int days = (((year % 4 == 0) && ((!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28);
        l.exiting(getClass().getName(), "daysInFebruary", new Integer(days));
        return days;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public DateRecordValidator() {
    }

    public DateRecordValidator(String[] dateFieldNames, String messageKey) {
        Logger l = LogUtils.enterLog(getClass(), "DateRecordValidator",
            new Object[]{dateFieldNames, messageKey});
        setDateFieldNames(dateFieldNames);
        setMessageKey(messageKey);
        setParmFieldNames(new String[0]);
        l.exiting(getClass().getName(), "DateRecordValidator");
    }

    public DateRecordValidator(String[] dateFieldNames, String messageKey, String idFieldName) {
        Logger l = LogUtils.enterLog(getClass(), "DateRecordValidator",
            new Object[]{dateFieldNames, idFieldName, messageKey});
        setDateFieldNames(dateFieldNames);
        setIdFieldName(idFieldName);
        setMessageKey(messageKey);
        setParmFieldNames(new String[0]);
        l.exiting(getClass().getName(), "DateRecordValidator");
    }


    public String[] getDateFieldNames() {
        return m_dateFieldNames;
    }

    public void setDateFieldNames(String[] dateFieldNames) {
        this.m_dateFieldNames = dateFieldNames;
    }

    public String getIdFieldName() {
        return m_idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        m_idFieldName = idFieldName;
    }

    public String getMessageKey() {
        return m_messageKey;
    }

    public void setMessageKey(String messageKey) {
        m_messageKey = messageKey;
    }

    public String[] getParmFieldNames() {
        return m_parmFieldNames;
    }

    public void setParmFieldNames(String[] parmFieldNames) {
        m_parmFieldNames = parmFieldNames;
    }

    private String m_idFieldName;
    private String m_messageKey;
    private String[] m_dateFieldNames;
    private String[] m_parmFieldNames;

    // must programmatically check February
    private static int[] daysInMonth = new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

}