package dti.ci.emailaddressmgr;

import dti.oasis.recordset.Record;

/**
 * The constants and getter/setter method for email address fields.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 29, 2010
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

public class EmailAddressFields {
    public static final String ENTITY_ID = "entityId";
    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String EMAIL_ADDRESS1 = "emailAddress1";
    public static final String EMAIL_ADDRESS2 = "emailAddress2";
    public static final String EMAIL_ADDRESS3 = "emailAddress3";

    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID, "");
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static String getEmailAddress(Record record) {
        return record.getStringValue(EMAIL_ADDRESS, "");
    }

    public static void setEmailAddress(Record record, String emailAddress) {
        record.setFieldValue(EMAIL_ADDRESS, emailAddress);
    }

    public static String getEmailAddress1(Record record) {
        return record.getStringValue(EMAIL_ADDRESS1, "");
    }

    public static void setEmailAddress1(Record record, String emailAddress1) {
        record.setFieldValue(EMAIL_ADDRESS1, emailAddress1);
    }

    public static String getEmailAddress2(Record record) {
        return record.getStringValue(EMAIL_ADDRESS2, "");
    }

    public static void setEmailAddress2(Record record, String emailAddress2) {
        record.setFieldValue(EMAIL_ADDRESS2, emailAddress2);
    }

    public static String getEmailAddress3(Record record) {
        return record.getStringValue(EMAIL_ADDRESS3, "");
    }

    public static void setEmailAddress3 (Record record, String emailAddress3) {
        record.setFieldValue(EMAIL_ADDRESS3, emailAddress3);
    }
}
