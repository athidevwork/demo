package dti.pm.transactionmgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 11, 2007
 *
 * @author sma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/16/2016        lzhang      170647 Add MSG_CODE, PRERATE, POSTRATE
 *                                      and OFFICIAL field.
 * 12/09/2016        tzeng       166929 Add userResponse field, and add its set/get method.
 * ---------------------------------------------------
 */
public class NotifyFields {
    public static final String PRODUCT_NOTIFY_ID = "productNotifyId";
    public static final String STATUS = "status";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_CATEGORY = "messageCategory";
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String NOTIFY_COUNT = "notifyCount";
    public static final String MSG_CODE = "msgCode";
    public static final String PRERATE = "PRERATE";
    public static final String POSTRATE = "POSTRATE";
    public static final String OFFICIAL = "OFFICIAL";
    public static final String RESPONSE = "response";
    public static final String RESPONSE_TYPE = "responseType";

    public static String getProductNotifyId(Record record) {
      return record.getStringValue(PRODUCT_NOTIFY_ID);
    }

    public static void setProductNotifyId(Record record, String productNotifyId) {
      record.setFieldValue(PRODUCT_NOTIFY_ID, productNotifyId);
    }

    public static String getStatus(Record record) {
      return record.getStringValue(STATUS);
    }

    public static void setStatus(Record record, String status) {
      record.setFieldValue(STATUS, status);
    }

    public static String getMessage(Record record) {
      return record.getStringValue(MESSAGE);
    }

    public static void setMessage(Record record, String message) {
      record.setFieldValue(MESSAGE, message);
    }

    public static String getMessageCategory(Record record) {
      return record.getStringValue(MESSAGE_CATEGORY);
    }

    public static void setMessageCategory(Record record, String messageCategory) {
      record.setFieldValue(MESSAGE_CATEGORY, messageCategory);
    }

    public static String getDefaultValue(Record record) {
      return record.getStringValue(DEFAULT_VALUE);
    }

    public static void setDefaultValue(Record record, String defaultValue) {
      record.setFieldValue(DEFAULT_VALUE, defaultValue);
    }

    public static String getNotifyCount(Record record) {
      return record.getStringValue(NOTIFY_COUNT);
    }

    public static void setNotifyCount(Record record, String notifyCount) {
      record.setFieldValue(NOTIFY_COUNT, notifyCount);
    }

    public static String getMsgCode(Record record) {
        return record.getStringValue(MSG_CODE);
    }

    public static void setMsgCode(Record record, String message) {
        record.setFieldValue(MSG_CODE, message);
    }

    public static String getResponse(Record record) {
        return record.getStringValue(RESPONSE);
    }

    public static void setResponse(Record record, String response) {
        record.setFieldValue(RESPONSE, response);
    }

    public static String getResponseType(Record record) {
        return record.getStringValue(RESPONSE_TYPE);
    }

    public static void setResponseType(Record record, String responseType) {
        record.setFieldValue(RESPONSE_TYPE, responseType);
    }
}
