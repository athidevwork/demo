package dti.pm.policymgr.service;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   1/4/2017
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/23/2016       tzeng       166929 - Initial version.
 * ---------------------------------------------------
 */
public class EApplicationInquiryFields {
    public static final String POLICY_ID = "policyId";
    public static final String STATUS_CODE = "statusCode";
    public static final String TYPE_CODE = "typeCode";
    public static final String POLICY_NUMBER_ID = "policyNumberId";
    public static final String POLICY_TERM_NUMBER_ID = "policyTermNumberId";
    public static final String APPLICATION_ID = "applicationId";
    public static final String STATUS_CODE_REQUESTED = "REQUESTED";
    public static final String TYPE_CODE_NBAPP = "NBAPP";
    public static final String TYPE_CODE_RENAPP = "RENAPP";
    public static final String INIT_RESULT = "initResult";
    public static final String APP_INIT_SUCCESS = "SUCCESS";
    public static final String APP_INIT_EXISTED = "EXISTED";
    public static final String APP_INIT_ERROR = "ERROR";
    public static final String INIT_EXCEPTION = "initException";

    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID, policyId);
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static boolean hasPolicyId(Record record) {
        return record.hasStringValue(POLICY_ID);
    }

    public static void setStatusCode(Record record, String statusCode) {
        record.setFieldValue(STATUS_CODE, statusCode);
    }

    public static String getStatusCode(Record record) {
        return record.getStringValue(STATUS_CODE);
    }

    public static boolean hasStatusCode(Record record) {
        return record.hasStringValue(STATUS_CODE);
    }

    public static void setTypeCode(Record record, String typeCode) {
        record.setFieldValue(TYPE_CODE, typeCode);
    }

    public static String getTypeCode(Record record) {
        return record.getStringValue(TYPE_CODE);
    }

    public static boolean hasTypeCode(Record record) {
        return record.hasStringValue(TYPE_CODE);
    }

    public static void setPolicyNumberId(Record record, String policyNumberId) {
        record.setFieldValue(POLICY_NUMBER_ID, policyNumberId);
    }

    public static String getPolicyNumberId(Record record) {
        return record.getStringValue(POLICY_NUMBER_ID);
    }

    public static boolean hasPolicyNumberId(Record record) {
        return record.hasStringValue(POLICY_NUMBER_ID);
    }

    public static void setPolicyTermNumberId(Record record, String policyTermNumberId) {
        record.setFieldValue(POLICY_TERM_NUMBER_ID, policyTermNumberId);
    }

    public static String getPolicyTermNumberId(Record record) {
        return record.getStringValue(POLICY_TERM_NUMBER_ID);
    }

    public static boolean hasPolicyTermNumberId(Record record) {
        return record.hasStringValue(POLICY_TERM_NUMBER_ID);
    }

    public static void setEApplicationId(Record record, String eApplicationId) {
        record.setFieldValue(APPLICATION_ID, eApplicationId);
    }

    public static String getEApplicationId(Record record) {
        return record.getStringValue(APPLICATION_ID);
    }

    public static boolean hasEApplicationId(Record record) {
        return record.hasStringValue(APPLICATION_ID);
    }

    public static void setInitResult(Record record, String initResult) {
        record.setFieldValue(INIT_RESULT, initResult);
    }

    public static String getInitResult(Record record) {
        return record.getStringValue(INIT_RESULT);
    }

    public static boolean hasInitResult(Record record) {
        return record.hasStringValue(INIT_RESULT);
    }

    public static void setInitException(Record record, Exception initException) {
        record.setFieldValue(INIT_EXCEPTION, initException);
    }

    public static AppException getInitException(Record record) {
        Object obj = record.getFieldValue(INIT_EXCEPTION);
        AppException appException = null;
        if (null != obj) {
            if (obj instanceof AppException) {
                appException = (AppException) obj;
            }
            else if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                appException = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, e.getMessage(), e);
            }
        }
        return appException;
    }
}
