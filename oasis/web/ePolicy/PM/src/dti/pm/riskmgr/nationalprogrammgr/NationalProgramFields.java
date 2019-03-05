package dti.pm.riskmgr.nationalprogrammgr;

import dti.oasis.recordset.Record;

/**
 * Helper constants and set/get methods to access National Program Fields.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 25, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class NationalProgramFields {
    public static final String NATIONAL_PROGRAM_ID = "nationalProgramId";
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String ORIGINAL_EFFECTIVE_TO_DATE = "origEffectiveToDate";
    public static final String PRIMARY_B = "primaryB";
    public static final String ORIGINAL_PRIMARY_B = "origPrimaryB";
    public static final String NATIONAL_PROGRAM_CODE = "nationalProgramCode";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";
    public static final String NATIONAL_PROGRAM_GRID_ROW_STYLE = "nationalProgramGridRowStyle";

    public static String getNationalProgramId(Record record) {
        return record.getStringValue(NATIONAL_PROGRAM_ID);
    }

    public static void setNationalProgramId(Record record, String nationalProgramId) {
        record.setFieldValue(NATIONAL_PROGRAM_ID, nationalProgramId);
    }

    public static String getRiskBaseRecordId(Record record) {
        return record.getStringValue(RISK_BASE_RECORD_ID);
    }

    public static void setRiskBaseRecordId(Record record, String riskBaseRecordId) {
        record.setFieldValue(RISK_BASE_RECORD_ID, riskBaseRecordId);
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

    public static String getOriginalEffectiveToDate(Record record) {
        return record.getStringValue(ORIGINAL_EFFECTIVE_TO_DATE);
    }

    public static void setOriginalEffectiveToDate(Record record, String originalEffectiveToDate) {
        record.setFieldValue(ORIGINAL_EFFECTIVE_TO_DATE, originalEffectiveToDate);
    }

    public static String getPrimaryB(Record record) {
        return record.getStringValue(PRIMARY_B);
    }

    public static void setPrimaryB(Record record, String primaryB) {
        record.setFieldValue(PRIMARY_B, primaryB);
    }

    public static String getOriginalPrimaryB(Record record) {
        return record.getStringValue(ORIGINAL_PRIMARY_B);
    }

    public static void setOriginalPrimaryB(Record record, String originalPrimaryB) {
        record.setFieldValue(ORIGINAL_PRIMARY_B, originalPrimaryB);
    }

    public static String getNationalProgramCode(Record record) {
        return record.getStringValue(NATIONAL_PROGRAM_CODE);
    }

    public static void setNationalProgramCode(Record record, String nationalProgramCode) {
        record.setFieldValue(NATIONAL_PROGRAM_CODE, nationalProgramCode);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static String getClosingTransLogId(Record record) {
        return record.getStringValue(CLOSING_TRANS_LOG_ID);
    }

    public static void setClosingTransLogId(Record record, String closingTransLogId) {
        record.setFieldValue(CLOSING_TRANS_LOG_ID, closingTransLogId);
    }
}