package dti.pm.policymgr.premiummgr;

import dti.oasis.recordset.Record;

/**
 * Constant field class for view premium accounting data.
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   June 29, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/14/2011       dzhang      113567 - Added  coverageTypeCode & EXCESS.
 * 06/15/2011       syang       111676 - Added TERM_ID, RISK_ID and EXPORT_B.
 * 10/11/2011       fcb         125838 - Added EXCESS_EXISTS_B.
 * 12/31/2014       awu         159667 - Added coverageBaseRecordId.
 * ---------------------------------------------------
 */
public class PremiumAccountingFields {
    public static final String POLICY_ID = "policyId";
    public static final String TERM_EFFECTIVE_DATE = "termEffectiveDate";
    public static final String TERM_EXPIRATION_DATE = "termExpirationDate";
    public static final String TRANS_EFF_DATE = "transEffDate";
    public static final String TRANS_ACCOUNT_DATE = "transAccountDate";
    public static final String REPORT_FROM_DATE = "reportFromDate";
    public static final String REPORT_TO_DATE = "reportToDate";
    public static final String EFFECTIVE_DATE = "effectiveDate";
    public static final String ACCOUNTING_DATE = "accountingDate";
    public static final String REPORT_ID = "reportId";
    public static final String PREMIUM_ACCOUNT_ID = "premiumAccountId";
    public static final String COVERAGE_TYPE_CODE = "coverageTypeCode";
    public static final String EXCESS = "EXCESS";
    public static final String TERM_BASE_RECORD_ID = "termBaseRecordId";
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String COVERAGE_BASE_RECORD_ID = "coverageBaseRecordId";
    public static final String EXPORT_B = "exportB";
    public static final String EXCESS_EXISTS_B = "excessExistsB";

    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID, policyId);
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static void setTermEffectiveDate(Record record, String termEffectiveDate) {
        record.setFieldValue(TERM_EFFECTIVE_DATE, termEffectiveDate);
    }

    public static String getTermEffectiveDate(Record record) {
        return record.getStringValue(TERM_EFFECTIVE_DATE);
    }

    public static void setTermExpirationDate(Record record, String termExpirationDate) {
        record.setFieldValue(TERM_EXPIRATION_DATE, termExpirationDate);
    }

    public static String getTermExpirationDate(Record record) {
        return record.getStringValue(TERM_EXPIRATION_DATE);
    }

    public static void setTransEffDate(Record record, String transEffDate) {
        record.setFieldValue(TRANS_EFF_DATE, transEffDate);
    }

    public static String getTransEffDate(Record record) {
        return record.getStringValue(TRANS_EFF_DATE);
    }

    public static void setTransAccountDate(Record record, String transAccountDate) {
        record.setFieldValue(TRANS_ACCOUNT_DATE, transAccountDate);
    }

    public static String getTransAccountDate(Record record) {
        return record.getStringValue(TRANS_ACCOUNT_DATE);
    }

    public static void setReportFromDate(Record record, String reportFromDate) {
        record.setFieldValue(REPORT_FROM_DATE, reportFromDate);
    }

    public static String getReportFromDate(Record record) {
        return record.getStringValue(REPORT_FROM_DATE);
    }

    public static void setReportToDate(Record record, String reportToDate) {
        record.setFieldValue(REPORT_TO_DATE, reportToDate);
    }

    public static String getReportToDate(Record record) {
        return record.getStringValue(REPORT_TO_DATE);
    }

    public static void setEffectiveDate(Record record, String effectiveDate) {
        record.setFieldValue(EFFECTIVE_DATE, effectiveDate);
    }

    public static String getEffectiveDate(Record record) {
        return record.getStringValue(EFFECTIVE_DATE);
    }

    public static void setAccountingDate(Record record, String accountingDate) {
        record.setFieldValue(ACCOUNTING_DATE, accountingDate);
    }

    public static String getAccountingDate(Record record) {
        return record.getStringValue(ACCOUNTING_DATE);
    }

    public static void setPremiumAccountId(Record record, String premiumAccountId) {
        record.setFieldValue(PREMIUM_ACCOUNT_ID, premiumAccountId);
    }

    public static String getPremiumAccountId(Record record) {
        return record.getStringValue(PREMIUM_ACCOUNT_ID);
    }

    public static void setReportId(Record record, String reportId) {
        record.setFieldValue(REPORT_ID, reportId);
    }

    public static String getReportId(Record record) {
        return record.getStringValue(REPORT_ID);
    }

    public static void setExcessExistsB(Record record, String excessExistsB) {
        record.setFieldValue(EXCESS_EXISTS_B, excessExistsB);
    }

    public static boolean hasExcessExistsB(Record record) {
        return record.hasStringValue(EXCESS_EXISTS_B);
    }

    public static String getExcessExistsB(Record record) {
        return record.getStringValue(EXCESS_EXISTS_B);
    }

    public static void setCoverageTypeCode(Record record, String coverageTypeCode) {
        record.setFieldValue(COVERAGE_TYPE_CODE, coverageTypeCode);
    }

    public static boolean hasCoverageTypeCode(Record record) {
        return record.hasStringValue(COVERAGE_TYPE_CODE);
    }

    public static String getCoverageTypeCode(Record record) {
        return record.getStringValue(COVERAGE_TYPE_CODE);
    }

    public static boolean isExcess(String str) {
            return EXCESS.equals(str);
    }
    
    public static String getTermBaseRecordId(Record record) {
        return record.getStringValue(TERM_BASE_RECORD_ID);
    }

    public static void setTermBaseRecordId(Record record, String termBaseRecordId) {
        record.setFieldValue(TERM_BASE_RECORD_ID, termBaseRecordId);
    }

    public static String getRiskBaseRecordId(Record record) {
        return record.getStringValue(RISK_BASE_RECORD_ID);
    }

    public static void setRiskBaseRecordId(Record record, String riskBaseRecordId) {
        record.setFieldValue(RISK_BASE_RECORD_ID, riskBaseRecordId);
    }

    public static String getExportB(Record record) {
        return record.getStringValue(EXPORT_B);
    }

    public static void setExportB(Record record, String exportB) {
        record.setFieldValue(EXPORT_B, exportB);
    }

    public static String getCoverageBaseRecordId(Record record) {
        return record.getStringValue(COVERAGE_BASE_RECORD_ID);
    }

    public static void setCoverageBaseRecordId(Record record, String coverageBaseRecordId) {
        record.setFieldValue(COVERAGE_BASE_RECORD_ID, coverageBaseRecordId);
    }
}
