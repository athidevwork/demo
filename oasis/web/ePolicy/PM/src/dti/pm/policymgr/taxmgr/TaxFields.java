package dti.pm.policymgr.taxmgr;

import dti.oasis.recordset.Record;

/**
 * Maintain/View Tax Fields
 * <p/>
 * <p>
 * (C) 2014 Delphi Technology, inc. (dti)
 * </p>
 * Date: Oct 20, 2014
 *
 * @author wdang
 */
/*
 * 
 * Revision Date Revised By Description
 * --------------------------------------------------- 
 * 10/20/2014    wdang      158112 - Initial version, Maintain/View Tax Fields.
 * ---------------------------------------------------
 */
public class TaxFields {

    // risk fields
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String RISK_STATUS = "riskStatus";
    public static final String RISK_BASE_STATUS = "riskBaseStatus";
    public static final String RISK_NAME = "riskName";
    public static final String RISK_TYPE_CODE = "riskTypeCode";

    // tax fields
    public static final String PREMIUM_TAX_RECORD_ID = "premiumTaxHeaderId";
    public static final String RISK_ID = "riskId";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String ORIG_EFFECTIVE_TO_DATE = "origEffectiveToDate";
    public static final String STATE_CODE = "stateCode";
    public static final String ORIG_STATE_CODE = "origStateCode";
    public static final String COUNTY_TAX_CODE = "countyTaxCode";
    public static final String ORIG_COUNTY_TAX_CODE = "origCountyTaxCode";
    public static final String CITY_TAX_CODE = "cityTaxCode";
    public static final String ORIG_CITY_TAX_CODE = "origCityTaxCode";
    public static final String TAX_LEVEL = "taxLevel";
    public static final String ORIG_TAX_LEVEL = "origTaxLevel";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";

    // DAO fields
    public static final String TERM_EFF = "termEff";
    public static final String TERM_EXP = "termExp";
    public static final String EFF_DATE = "effDate";
    public static final String EXP_DATE = "expDate";
    public static final String REC_MODE = "recMode";
    public static final String EQT_ID = "eqtId";
    public static final String END_QUOTE_ID = "endQuoteId";
    public static final String TRANS_EFF = "transEff";
    public static final String EFFECTIVE_DATE = "effectiveDate";
    public static final String TERM_ID = "termId";

    // entitlement fields
    public static final String IS_ADD_VISIBLE = "isAddVisible";
    public static final String IS_DELETE_VISIBLE = "isDeleteVisible";
    public static final String IS_SAVE_VISIBLE = "isSaveVisible";
    public static final String IS_FORM_FIELDS_EDITABLE = "isFormFieldsEditable";

    // business fields
    public static final String TAX_ALGORITHM_M = "M";

    public static String getRiskBaseRecordId(Record record) {
        return record.getStringValue(RISK_BASE_RECORD_ID);
    }

    public static void setRiskBaseRecordId(Record record, String riskBaseRecordId) {
        record.setFieldValue(RISK_BASE_RECORD_ID, riskBaseRecordId);
    }

    public static String getRiskId(Record record) {
        return record.getStringValue(RISK_ID);
    }

    public static void setRiskId(Record record, String riskId) {
        record.setFieldValue(RISK_ID, riskId);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static void setEffectiveFromDate(Record record,
                                            String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, effectiveFromDate);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE, effectiveToDate);
    }

    public static String getStateCode(Record record) {
        return record.getStringValue(STATE_CODE);
    }

    public static void setStateCode(Record record, String stateCode) {
        record.setFieldValue(STATE_CODE, stateCode);
    }

    public static String getCountyTaxCode(Record record) {
        return record.getStringValue(COUNTY_TAX_CODE);
    }

    public static void setCountyTaxCode(Record record, String countyTaxCode) {
        record.setFieldValue(COUNTY_TAX_CODE, countyTaxCode);
    }

    public static String getCityTaxCode(Record record) {
        return record.getStringValue(CITY_TAX_CODE);
    }

    public static void setCityTaxCode(Record record, String cityTaxCode) {
        record.setFieldValue(CITY_TAX_CODE, cityTaxCode);
    }

    public static String getRiskStatus(Record record) {
        return record.getStringValue(RISK_STATUS);
    }

    public static void setRiskStatus(Record record, String riskStatus) {
        record.setFieldValue(RISK_STATUS, riskStatus);
    }

    public static String getRiskBaseStatus(Record record) {
        return record.getStringValue(RISK_BASE_STATUS);
    }

    public static void setRiskBaseStatus(Record record, String riskStatus) {
        record.setFieldValue(RISK_BASE_STATUS, riskStatus);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static String getRecordModeCode(Record record) {
        return record.getStringValue(RECORD_MODE_CODE);
    }

    public static void setRecordModeCode(Record record, String recordModeCode) {
        record.setFieldValue(RECORD_MODE_CODE, recordModeCode);
    }

    public static String getAfterImageRecordB(Record record) {
        return record.getStringValue(AFTER_IMAGE_RECORD_B);
    }

    public static void setAfterImageRecordB(Record record, String afterImageRecordB) {
        record.setFieldValue(AFTER_IMAGE_RECORD_B, afterImageRecordB);
    }

    public static String getClosingTransLogId(Record record) {
        return record.getStringValue(CLOSING_TRANS_LOG_ID);
    }

    public static void setClosingTransLogId(Record record, String closingTransLogId) {
        record.setFieldValue(CLOSING_TRANS_LOG_ID, closingTransLogId);
    }

    public static String getRiskName(Record record) {
        return record.getStringValue(RISK_NAME);
    }

    public static void setRiskName(Record record, String riskName) {
        record.setFieldValue(RISK_NAME, riskName);
    }

    public static String getRiskTypeCode(Record record) {
        return record.getStringValue(RISK_TYPE_CODE);
    }

    public static void setRiskTypeCode(Record record, String riskTypeCode) {
        record.setFieldValue(RISK_TYPE_CODE, riskTypeCode);
    }

    public static String getPremiumTaxHeaderId(Record record) {
        return record.getStringValue(PREMIUM_TAX_RECORD_ID);
    }

    public static void setPremiumTaxHeaderId(Record record, String premiumTaxHeaderId) {
        record.setFieldValue(PREMIUM_TAX_RECORD_ID, premiumTaxHeaderId);
    }

    public static String getOrigEffectiveToDate(Record record) {
        return record.getStringValue(ORIG_EFFECTIVE_TO_DATE);
    }

    public static void setOrigEffectiveToDate(Record record, String origEffectiveToDate) {
        record.setFieldValue(ORIG_EFFECTIVE_TO_DATE, origEffectiveToDate);
    }

    public static String getOrigStateCode(Record record) {
        return record.getStringValue(ORIG_STATE_CODE);
    }

    public static void setOrigStateCode(Record record, String origStateCode) {
        record.setFieldValue(ORIG_STATE_CODE, origStateCode);
    }

    public static String getOrigCountyTaxCode(Record record) {
        return record.getStringValue(ORIG_COUNTY_TAX_CODE);
    }

    public static void setOrigCountyTaxCode(Record record, String origCountyTaxCode) {
        record.setFieldValue(ORIG_COUNTY_TAX_CODE, origCountyTaxCode);
    }

    public static String getOrigCityTaxCode(Record record) {
        return record.getStringValue(ORIG_CITY_TAX_CODE);
    }

    public static void setOrigCityTaxCode(Record record, String origCityTaxCode) {
        record.setFieldValue(ORIG_CITY_TAX_CODE, origCityTaxCode);
    }

    public static String getOrigTaxLevel(Record record) {
        return record.getStringValue(ORIG_TAX_LEVEL);
    }

    public static void setOrigTaxLevel(Record record, String origTaxLevel) {
        record.setFieldValue(ORIG_TAX_LEVEL, origTaxLevel);
    }

    public static String getTaxLevel(Record record) {
        return record.getStringValue(TAX_LEVEL);
    }

    public static void setTaxLevel(Record record, String taxLevel) {
        record.setFieldValue(TAX_LEVEL, taxLevel);
    }

}
