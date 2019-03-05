package dti.oasis.ows.util;

import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   10/16/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * 08/24/2016        dzhang     isssue 177970: add dateTimeElementValueConverter
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class FieldElementMap {
    public String getElementValue(Object obj) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getElementValue", new Object[]{obj});
        }

        ElementValueConverter elementValueConverter = getElementValueConverter();

        if (getElementValueConverter() == null) {
            if (getDataType() == null) {
                elementValueConverter = getStringElementValueConverter();
            } else if (getDataType().equalsIgnoreCase(DATA_TYPE_STRING)) {
                elementValueConverter = getStringElementValueConverter();
            } else if (getDataType().equalsIgnoreCase(DATA_TYPE_DATE)) {
                elementValueConverter = getDateElementValueConverter();
            } else if (getDataType().equalsIgnoreCase(DATA_TYPE_YES_NO_FLAG)) {
                elementValueConverter = getYesNoFlagElementValueConverter();
            } else if (getDataType().equalsIgnoreCase(DATA_TYPE_YES_NO_EMPTY_FLAG)) {
                elementValueConverter = getYesNoEmptyFlagElementValueConverter();
            } else if (getDataType().equalsIgnoreCase(DATA_TYPE_SSN)) {
                elementValueConverter = getSsnElementValueConverter();
            } else if (getDataType().equalsIgnoreCase(DATA_TYPE_PHONE_NUMBER)) {
                elementValueConverter = getPhoneNumberElementValueConverter();
            } else if (getDataType().equalsIgnoreCase(DATA_TYPE_DATE_TIME)) {
                elementValueConverter = getDateTimeElementValueConverter();
            } else {
                elementValueConverter = getStringElementValueConverter();
            }
        }

        String value = elementValueConverter.convert(obj, getElementPath());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getElementValue", value);
        }
        return value;
    }

    public String getFieldName() {
        return m_fieldName;
    }

    public void setFieldName(String fieldName) {
        m_fieldName = fieldName;
    }

    public String getElementPath() {
        return m_elementPath;
    }

    public void setElementPath(String elementPath) {
        m_elementPath = elementPath;
    }

    public String getDataType() {
        return m_dataType;
    }

    public void setDataType(String dataType) {
        m_dataType = dataType;
    }

    public ElementValueConverter getElementValueConverter() {
        return m_elementValueConverter;
    }

    public void setElementValueConverter(ElementValueConverter elementValueConverter) {
        m_elementValueConverter = elementValueConverter;
    }

    public ElementValueConverter getStringElementValueConverter() {
        return m_stringElementValueConverter;
    }

    public void setStringElementValueConverter(ElementValueConverter stringElementValueConverter) {
        m_stringElementValueConverter = stringElementValueConverter;
    }

    public ElementValueConverter getDateElementValueConverter() {
        return m_dateElementValueConverter;
    }

    public void setDateElementValueConverter(ElementValueConverter dateElementValueConverter) {
        m_dateElementValueConverter = dateElementValueConverter;
    }

    public ElementValueConverter getYesNoFlagElementValueConverter() {
        return m_yesNoFlagElementValueConverter;
    }

    public void setYesNoFlagElementValueConverter(ElementValueConverter yesNoFlagElementValueConverter) {
        m_yesNoFlagElementValueConverter = yesNoFlagElementValueConverter;
    }

    public ElementValueConverter getYesNoEmptyFlagElementValueConverter() {
        return m_yesNoEmptyFlagElementValueConverter;
    }

    public void setYesNoEmptyFlagElementValueConverter(ElementValueConverter yesNoEmptyFlagElementValueConverter) {
        m_yesNoEmptyFlagElementValueConverter = yesNoEmptyFlagElementValueConverter;
    }

    public ElementValueConverter getPhoneNumberElementValueConverter() {
        return m_phoneNumberElementValueConverter;
    }

    public void setPhoneNumberElementValueConverter(ElementValueConverter phoneNumberElementValueConverter) {
        m_phoneNumberElementValueConverter = phoneNumberElementValueConverter;
    }

    public ElementValueConverter getSsnElementValueConverter() {
        return m_ssnElementValueConverter;
    }

    public void setSsnElementValueConverter(ElementValueConverter ssnElementValueConverter) {
        m_ssnElementValueConverter = ssnElementValueConverter;
    }

    public ElementValueConverter getBusinessEmailListElementValueConverter() {
        return m_businessEmailListElementValueConverter;
    }

    public void setBusinessEmailListElementValueConverter(ElementValueConverter businessEmailListElementValueConverter) {
        m_businessEmailListElementValueConverter = businessEmailListElementValueConverter;
    }

    public ElementValueConverter getDateTimeElementValueConverter() {
        return m_dateTimeElementValueConverter;
    }

    public void setDateTimeElementValueConverter(ElementValueConverter dateTimeElementValueConverter) {
        this.m_dateTimeElementValueConverter = dateTimeElementValueConverter;
    }

    private String m_fieldName;
    private String m_elementPath;
    private String m_dataType;
    private ElementValueConverter m_elementValueConverter;
    private ElementValueConverter m_stringElementValueConverter;
    private ElementValueConverter m_dateElementValueConverter;
    private ElementValueConverter m_yesNoFlagElementValueConverter;
    private ElementValueConverter m_yesNoEmptyFlagElementValueConverter;
    private ElementValueConverter m_phoneNumberElementValueConverter;
    private ElementValueConverter m_ssnElementValueConverter;
    private ElementValueConverter m_businessEmailListElementValueConverter;
    private ElementValueConverter m_dateTimeElementValueConverter;
    private final Logger l = LogUtils.getLogger(getClass());

    public static final String DATA_TYPE_STRING = "String";
    public static final String DATA_TYPE_DATE = "Date";
    public static final String DATA_TYPE_DATE_TIME = "DateTime";
    public static final String DATA_TYPE_YES_NO_FLAG = "YesNoFlag";
    public static final String DATA_TYPE_YES_NO_EMPTY_FLAG = "YesNoEmptyFlag";
    public static final String DATA_TYPE_PHONE_NUMBER = "PhoneNumber";
    public static final String DATA_TYPE_SSN = "SSN";
}
