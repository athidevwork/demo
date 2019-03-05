package dti.ci.entityaddlemailmgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/22/13
 *
 * @author bzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityAddlEmailFields {
    public static final String PK = "pk";
    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String MODULE_CODE = "moduleCode";

    public static final String ENTITY_ID = "entityId";
    public static final String IS_DELETE_AVAILABLE = "isDeleteAvailable";

    public static String getPK(Record record) {
        return record.getStringValue(PK, "");
    }

    public static void setPK(Record record, String pk) {
        record.setFieldValue(PK, pk);
    }

    public static String getEmailAddress(Record record) {
        return record.getStringValue(EMAIL_ADDRESS, "");
    }

    public static void setEmailAddress(Record record, String emailAddress) {
        record.setFieldValue(EMAIL_ADDRESS, emailAddress);
    }

    public static String getModuleCode(Record record) {
        return record.getStringValue(MODULE_CODE, "");
    }

    public static void setModuleCode(Record record, String moduleCode) {
        record.setFieldValue(MODULE_CODE, moduleCode);
    }
}
