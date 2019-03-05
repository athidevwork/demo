package dti.pm.policymgr.applicationmgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 17, 2009
 *
 * @author gchitta
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/30/2017       tzeng       166929 - Added EAPP_PM_INIT_PATH_OVERRIDE, EAPP_PM_INIT_PATH, EAPP_PM_INIT_TIMEOUT,
 *                                       EAPP.INITIATE.ASYNC.
 * 01/12/2018       kshen       Grid replacement. Added method isGroupFormTypeCode to check if a form type code is a
 *                              group application for now since Oasis uses GRP_APP, eApp uses GROUP, and not sure which
 *                              one is correct for now.
 * ---------------------------------------------------
 */
public class ApplicationFields {

    public static final String TERM_BASE_RECORD_ID = "termBaseRecordId";
    public static final String STATUS = "status";
    public static final String WEB_FORM_WORK_ITEM_ID = "webFormWorkItemId";
    public static final String WORK_ITEM_ID = "workItemId";
    public static final String FORM_TYPE_CODE = "formTypeCode";
    public static final String EAPP_BASE_URL = "eApp.baseUrl";
    public static final String EAPP_TOKEN_AUTH_KEY = "eApp.token.auth.key";
    public static final String EAPP_TOKEN_AUTH_INIT_VECTOR = "eApp.token.auth.init.vector";
    public static final String EAPP_BASE_URL_OVERRIDE = "EAPP.BASEURL";
    public static final String EAPP_TOKEN_AUTH_KEY_OVERRIDE = "EAPP.TKN.AUTH.KEY";
    public static final String EAPP_TOKEN_AUTH_INIT_VECTOR_OVERRIDE = "EAPP.TKN.AUTH.INIT.V";
    public static final String URL = "URL";
    public static final String DECODED_FILE_FULL_PATH = "decodedFileFullPath";
    public static final String CHANGE_TYPE_CODE = "changeTypeCode";
    public static final String CHANGE_INFO = "changeInfo";
    public static final String REVIEWER_ID = "reviewerId";
    public static final String ORIG_REVIEWER_ID = "origReviewerId";
    public static final String CHANGE_DATE = "changeDate";
    public static final String PREPARER_EMAIL_ADDR = "preparerName";
    public static final String EAPP_PM_INIT_PATH_OVERRIDE = "EAPP.PM.INIT.PATH";
    public static final String EAPP_PM_INIT_PATH = "eApp.pm.init.path";
    public static final String EAPP_PM_INIT_TIMEOUT = "EAPP.PM.INIT.TIMEOUT";
    public static final String EAPP_PM_INIT_ASYNC = "EAPP.PM.INIT.ASYNC";

    public static String getTermBaseRecordId(Record record) {
        return record.getStringValue(TERM_BASE_RECORD_ID);
    }

    public static void setTermBaseRecordId(Record record, String termBaseRecordId) {
        record.setFieldValue(TERM_BASE_RECORD_ID, termBaseRecordId);
    }

    public static String getStatus(Record record) {
        return record.getStringValue(STATUS);
    }

    public static void setStatus(Record record, String status) {
        record.setFieldValue(STATUS, status);
    }

    public static String getWebFormWorkItemId(Record record) {
        return record.getStringValue(WEB_FORM_WORK_ITEM_ID);
    }

    public static void setWebFormWorkItemId(Record record, String webFormWorkItemId) {
        record.setFieldValue(WEB_FORM_WORK_ITEM_ID, webFormWorkItemId);
    }

    public static String getFormTypeCode(Record record) {
        return record.getStringValue(FORM_TYPE_CODE);
    }

    public static void setFormTypeCode(Record record, String formTypeCode) {
        record.setFieldValue(FORM_TYPE_CODE, formTypeCode);
    }

    public static void setUrl(Record record, String url) {
        record.setFieldValue(URL, url);
    }

    public static String getDecodedFileFullPath(Record record) {
        return record.getStringValue(DECODED_FILE_FULL_PATH);
    }

    public static String getChangeTypeCode(Record record) {
        return record.getStringValue(CHANGE_TYPE_CODE);
    }

    public static void setChangeInfo(Record record, String changeInfo) {
        record.setFieldValue(CHANGE_INFO, changeInfo);
    }

    public static String getReviewerId(Record record) {
        return record.getStringValue(REVIEWER_ID);
    }

    public static String getOrigReviewerId(Record record) {
        return record.getStringValue(ORIG_REVIEWER_ID);
    }

    public static void setChangeDate(Record record, String changeDate) {
        record.setFieldValue(CHANGE_DATE, changeDate);
    }

    public static String getPreparerEmailAddress(Record record) {
        return record.getStringValue(PREPARER_EMAIL_ADDR);
    }

    public static void setPreparerEmailAddress(Record record, String preparerEmailAddress) {
        record.setFieldValue(PREPARER_EMAIL_ADDR, preparerEmailAddress);
    }

    public static class FormTypeCodeValues {
        public static final String GRP_APP = "GRP_APP";
        public static final String GROUP = "GROUP";

        // In PM, we use GRP_APP as the form type code of group application.
        // In eAPP, we use GROUP as the form type code of group application.
        // Not sure what's the correct form type code, use both them as group form type code for now.
        public static boolean isGroupFormTypeCode(String formTypeCoe) {
            return  (GRP_APP.equals(formTypeCoe) || GROUP.equals(formTypeCoe));
        }
    }

    public static class ChangeTypeCodeValues {
        public static final String REASSIGN = "REASSIGN";
        public static final String REWIP = "REWIP";
        public static final String SENDREMINDER = "SDREMINDER";
    }
}
