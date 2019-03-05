package dti.pm.riskmgr.empphysmgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.PMStatusCode;

/**
 * constants fields for Employed Physician
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/23/2010       syang       102365 - Add new columns.
 * 02/25/2011       sxm         111017 - Change risk_pk to risk_base_reocrd_fk
 * 10/23/2012       xnie        137735 - Added new fields.
 * 11/25/2014       kxiang      158853 - Added fields RISK_NAME_GH and RISK_NAME_HREF to get href value in WebWB.
 * ---------------------------------------------------
 */
public class EmployedPhysicianFields {

    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";
    public static final String RISK_CLOSING_TRANS_LOG_ID = "riskClosingTransLogId";
    public static final String FTE_VALUE = "fteValue";
    public static final String EMPLOYMENT_STATUS = "employmentStatus";
    public static final String ACTUAL_HOURS = "actualHours";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String POLICY_FTE_RELATION_ID = "policyFteRelationId";
    public static final String RISK_OFFICIAL_RECORD_ID = "riskOfficialRecordId";
    public static final String RISK_RECORD_MODE_CODE = "riskRecordModeCode";
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String FTE_TOTAL = "fteTotal";
    public static final String ENTITY_ID = "entityId";
    public static final String RISK_PARENT_ID = "riskParentId";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String CURR_STATUS_CODE = "currStatusCode";
    public static final String RISK_CHILD_ID = "riskChildId";
    public static final String ADD_FTE_BY_ENTITY = "addFteByEntity";
    public static final String RENEW_B = "renewB";
    public static final String FTE_VAL_EXPDATE = "fteValExpdate";
    public static final String ORIG_EFFECTIVE_TO_DATE = "origEffectiveToDate";
    public static final String IS_IN_UPDATE_MODE = "isInUpdateMode";
    public static final String HOSP_TERM_EFFECTIVE = "hospTermEffective";
    public static final String HOSP_TERM_EXPIRATION = "hospTermExpiration";
    public static final String DEGREE_CODE = "degreeCode";
    public static final String FTE_STATUS_CODE = "fteStatusCode";
    public static final String FTE_EFFECTIVE_TO_DATE = "fteEffectiveToDate";
    public static final String FTE_STATUS_CODE_VISIBLE = "fteStatusCodeVisible";
    public static final String RISK_NAME_GH = "riskName_GH";
    public static final String RISK_NAME_HREF = "riskNameHref";

    public static YesNoFlag getIsInUpdateMode(Record record) {
        return YesNoFlag.getInstance(record.getBooleanValue(IS_IN_UPDATE_MODE,false).booleanValue());
    }

    public static void setIsInUpdateMode(Record record, YesNoFlag isInUpdateMode) {
        record.setFieldValue(IS_IN_UPDATE_MODE, isInUpdateMode);
    }

    public static String getOrigEffectiveToDate(Record record) {
        return record.getStringValue(ORIG_EFFECTIVE_TO_DATE);
    }

    public static void setOrigEffectiveToDate(Record record, String origEffectiveToDate) {
        record.setFieldValue(ORIG_EFFECTIVE_TO_DATE, origEffectiveToDate);
    }

    public static YesNoFlag getFteValExpdate(Record record) {
        return YesNoFlag.getInstance(record.getBooleanValue(FTE_VAL_EXPDATE, false).booleanValue());
    }

    public static void setFteValExpdate(Record record, YesNoFlag fteValExpdate) {
        record.setFieldValue(FTE_VAL_EXPDATE, fteValExpdate);
    }

    public static YesNoFlag getRenewB(Record record) {
        return YesNoFlag.getInstance(record.getBooleanValue(RENEW_B, false).booleanValue());
    }

    public static void setRenewB(Record record, YesNoFlag renewB) {
        record.setFieldValue(RENEW_B, renewB);
    }

    public static YesNoFlag getAddFteByEntity(Record record) {
        return YesNoFlag.getInstance(record.getBooleanValue(ADD_FTE_BY_ENTITY, false).booleanValue());
    }

    public static void setAddFteByEntity(Record record, YesNoFlag addFteByEntity) {
        record.setFieldValue(ADD_FTE_BY_ENTITY, addFteByEntity);
    }

    public static String getRiskChildId(Record record) {
        return record.getStringValue(RISK_CHILD_ID);
    }

    public static void setRiskChildId(Record record, String riskChildId) {
        record.setFieldValue(RISK_CHILD_ID, riskChildId);
    }

    public static PMStatusCode getCurrStatusCode(Record record) {
        return PMStatusCode.getInstance(record.getStringValue(CURR_STATUS_CODE));
    }

    public static void setCurrStatusCode(Record record, PMStatusCode currStatusCode) {
        record.setFieldValue(CURR_STATUS_CODE, currStatusCode);
    }

    public static void setAfterImageRecordB(Record record, YesNoFlag afterImageRecordB) {
        record.setFieldValue(AFTER_IMAGE_RECORD_B, afterImageRecordB);
    }

    public static YesNoFlag getAfterImageRecordB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(AFTER_IMAGE_RECORD_B));
    }

    public static String getRiskParentId(Record record) {
        return record.getStringValue(RISK_PARENT_ID);
    }

    public static void setRiskParentId(Record record, String riskParentId) {
        record.setFieldValue(RISK_PARENT_ID, riskParentId);
    }

    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static String getFteTotal(Record record) {
        return record.getStringValue(FTE_TOTAL);
    }

    public static void setFteTotal(Record record, String totalFteValue) {
        record.setFieldValue(FTE_TOTAL, totalFteValue);
    }


    public static String getRiskBaseRecordId(Record record) {
        return record.getStringValue(RISK_BASE_RECORD_ID);
    }

    public static void setRiskBaseRecordId(Record record, String riskBaseRecordId) {
        record.setFieldValue(RISK_BASE_RECORD_ID, riskBaseRecordId);
    }

    public static RecordMode getRiskRecordModeCode(Record record) {
        return RecordMode.getInstance(record.getStringValue(RISK_RECORD_MODE_CODE));
    }

    public static void setRiskRecordModeCode(Record record, RecordMode riskRecordModeCode) {
        record.setFieldValue(RISK_RECORD_MODE_CODE, riskRecordModeCode);
    }

    public static String getRiskOfficialRecordId(Record record) {
        return record.getStringValue(RISK_OFFICIAL_RECORD_ID);
    }

    public static void setRiskOfficialRecordId(Record record, String riskOfficialRecordId) {
        record.setFieldValue(RISK_OFFICIAL_RECORD_ID, riskOfficialRecordId);
    }

    public static String getPolicyFteRelationId(Record record) {
        return record.getStringValue(POLICY_FTE_RELATION_ID);
    }

    public static void setPolicyFteRelationId(Record record, String policyFteRelationId) {
        record.setFieldValue(POLICY_FTE_RELATION_ID, policyFteRelationId);
    }

    public static String getActualHours(Record record) {
        return record.getStringValue(ACTUAL_HOURS);
    }

    public static void setActualHours(Record record, String actualHours) {
        record.setFieldValue(ACTUAL_HOURS, actualHours);
    }

    public static EmploymentStatusCode getEmploymentStatus(Record record) {
        return EmploymentStatusCode.getInstance(record.getStringValue(EMPLOYMENT_STATUS));
    }

    public static void setEmploymentStatus(Record record, EmploymentStatusCode employmentStatus) {
        record.setFieldValue(EMPLOYMENT_STATUS, employmentStatus);
    }

    public static String getFteValue(Record record) {
        return record.getStringValue(FTE_VALUE);
    }

    public static void setFteValue(Record record, String fteValue) {
        record.setFieldValue(FTE_VALUE, fteValue);
    }

    public static String getClosingTransLogId(Record record) {
        return record.getStringValue(CLOSING_TRANS_LOG_ID);
    }

    public static void setClosingTransLogId(Record record, String closingTransLogId) {
        record.setFieldValue(CLOSING_TRANS_LOG_ID, closingTransLogId);
    }

    public static String getRiskClosingTransLogId(Record record) {
        return record.getStringValue(RISK_CLOSING_TRANS_LOG_ID);
    }

    public static void setRiskClosingTransLogId(Record record, String riskClosingTransLogId) {
        record.setFieldValue(RISK_CLOSING_TRANS_LOG_ID, riskClosingTransLogId);
    }


    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, effectiveFromDate);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE, effectiveToDate);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static String getHospTermEffective(Record record) {
        return record.getStringValue(HOSP_TERM_EFFECTIVE);
    }

    public static void setHospTermEffective(Record record, String termEffectiveDate) {
        record.setFieldValue(HOSP_TERM_EFFECTIVE, termEffectiveDate);
    }

     public static String getHospTermExpiration(Record record) {
        return record.getStringValue(HOSP_TERM_EXPIRATION);
    }

    public static void setHospTermExpiration(Record record, String termExpirationDate) {
        record.setFieldValue(HOSP_TERM_EXPIRATION, termExpirationDate);
    }
    public static String getDegreeCode(Record record) {
        return record.getStringValue(DEGREE_CODE);
    }

    public static void setDegreeCode(Record record, String degreeCode) {
        record.setFieldValue(DEGREE_CODE, degreeCode);
    }

    public static String getFteStatusCode(Record record) {
        return record.getStringValue(FTE_STATUS_CODE);
    }

    public static void setFteStatusCode(Record record, String fteStatusCode) {
        record.setFieldValue(FTE_STATUS_CODE, fteStatusCode);
    }

    public static String getFteEffectiveToDate(Record record) {
        return record.getStringValue(FTE_EFFECTIVE_TO_DATE);
    }

    public static void setFteEffectiveToDate(Record record, String fteEffectiveToDate) {
        record.setFieldValue(FTE_EFFECTIVE_TO_DATE, fteEffectiveToDate);
    }

    public static YesNoFlag getFteStatusCodeVisible(Record record) {
        return YesNoFlag.getInstance(record.getBooleanValue(FTE_STATUS_CODE_VISIBLE,false).booleanValue());
    }

    public static void setFteStatusCodeVisible(Record record, YesNoFlag fteStatusCodeVisible) {
        record.setFieldValue(FTE_STATUS_CODE_VISIBLE, fteStatusCodeVisible);
    }

    public static void setRiskNameHref(Record record, String riskNameHref) {
        record.setFieldValue(RISK_NAME_HREF, riskNameHref);
    }

    public static String getRiskNameHref(Record record) {
        return record.getStringValue(RISK_NAME_HREF);
    }
}
