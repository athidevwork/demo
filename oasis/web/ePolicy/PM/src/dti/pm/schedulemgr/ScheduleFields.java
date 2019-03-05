package dti.pm.schedulemgr;

import dti.oasis.recordset.Record;
import dti.pm.busobjs.ScreenModeCode;

/**
 * Constants for Scheudle.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 4, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/08/2016       xnie        178302 - Added entityName and get/set methods.
 * 07/31/2017       lzhang      182769 - Add contigRiskEffectiveDate, contigRiskExpireDate,
 *                                       contigCoverageEffectiveDate, contigCoverageExpireDate,
 *                                       transEffectiveFromDate and get methods.
 * ---------------------------------------------------
 */

public class ScheduleFields {
    public static final String AFTER_IMAGE_RECORDB = "afterImageRecordB";
    public static final String ENTITY_ID = "entityId";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String POLICY_SCHEDULE_ID = "policyScheduleId";
    public static final String ENDORSEMENT_QUOTE_ID = "endorsementQuoteId";
    public static final String SOURCE_RECORD_ID = "sourceRecordId";
    public static final String SCHEDULE_TEXT = "scheduleText";
    public static final String COVERAGE_LIMIT_CODE = "coverageLimitCode";
    public static final String TRANSACTION_LOG_ID = "transactionLogId";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";
    public static final String SOURCE_TABLE_NAME = "sourceTableName";
    public static final String SCREEN_MODE_CODE = "screenModeCode";
    public static final String ENTITY_NAME = "entityName";
    public static final String CONTIG_RISK_EFFECTIVE_DATE = "contigRiskEffectiveDate";
    public static final String CONTIG_RISK_EXPIRE_DATE = "contigRiskExpireDate";
    public static final String CONTIG_COVERAGE_EFFECTIVE_DATE = "contigCoverageEffectiveDate";
    public static final String CONTIG_COVERAGE_EXPIRE_DATE = "contigCoverageExpireDate";
    public static final String TRANS_EFFECTIVE_FROM_DATE = "transEffectiveFromDate";

    public static String getAfterImageRecordB(Record record) {
      return record.getStringValue(AFTER_IMAGE_RECORDB);
    }

    public static void setAfterImageRecordB(Record record, String afterImageRecordB) {
      record.setFieldValue(AFTER_IMAGE_RECORDB, afterImageRecordB);
    }
    public static String getEntityId(Record record) {
      return record.getStringValue(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
      record.setFieldValue(ENTITY_ID, entityId);
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
    public static String getPolicyScheduleId(Record record) {
      return record.getStringValue(POLICY_SCHEDULE_ID);
    }

    public static void setPolicyScheduleId(Record record, String policyScheduleId) {
      record.setFieldValue(POLICY_SCHEDULE_ID, policyScheduleId);
    }
    public static String getEndorsementQuoteId(Record record) {
      return record.getStringValue(ENDORSEMENT_QUOTE_ID);
    }

    public static void setEndorsementQuoteId(Record record, String endorsementQuoteId) {
      record.setFieldValue(ENDORSEMENT_QUOTE_ID, endorsementQuoteId);
    }
    public static String getSourceRecordId(Record record) {
      return record.getStringValue(SOURCE_RECORD_ID);
    }

    public static void setSourceRecordId(Record record, String sourceRecordId) {
      record.setFieldValue(SOURCE_RECORD_ID, sourceRecordId);
    }
    public static String getScheduleText(Record record) {
      return record.getStringValue(SCHEDULE_TEXT);
    }

    public static void setScheduleText(Record record, String scheduleText) {
      record.setFieldValue(SCHEDULE_TEXT, scheduleText);
    }
    public static String getCoverageLimitCode(Record record) {
      return record.getStringValue(COVERAGE_LIMIT_CODE);
    }

    public static void setCoverageLimitCode(Record record, String coverageLimitCode) {
      record.setFieldValue(COVERAGE_LIMIT_CODE, coverageLimitCode);
    }
    public static String getTransactionLogId(Record record) {
      return record.getStringValue(TRANSACTION_LOG_ID);
    }

    public static void setTransactionLogId(Record record, String transactionLogId) {
      record.setFieldValue(TRANSACTION_LOG_ID, transactionLogId);
    }
    public static String getClosingTransLogId(Record record) {
      return record.getStringValue(CLOSING_TRANS_LOG_ID);
    }

    public static void setEntityName(Record record, String entityName) {
        record.setFieldValue(ENTITY_NAME, entityName);
    }
    public static String getEntityName(Record record) {
        return record.getStringValue(ENTITY_NAME);
    }

    public static void setClosingTransLogId(Record record, String closingTransLogId) {
      record.setFieldValue(CLOSING_TRANS_LOG_ID, closingTransLogId);
    }

    public static String getSourceTableName(Record record) {
      return record.getStringValue(SOURCE_TABLE_NAME);
    }

    public static void setSourceTableName(Record record, String sourceTableName) {
      record.setFieldValue(SOURCE_TABLE_NAME, sourceTableName);
    }

    public static ScreenModeCode getScreenModeCode(Record record) {
        Object value = record.getFieldValue(SCREEN_MODE_CODE);
        ScreenModeCode result = null;
        if (value == null || value instanceof ScreenModeCode) {
            result = (ScreenModeCode) value;
        }
        else {
            result = ScreenModeCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setScreenModeCode(Record record, ScreenModeCode screenModeCode) {
      record.setFieldValue(SCREEN_MODE_CODE, screenModeCode);
    }
    public static String getTransEffectiveFromDate(Record record) {
        return record.getStringValue(TRANS_EFFECTIVE_FROM_DATE);
    }

    public static String getContigRiskEffectiveDate(Record record) {
        return record.getStringValue(CONTIG_RISK_EFFECTIVE_DATE);
    }

    public static String getContigRiskExpireDate(Record record) {
        return record.getStringValue(CONTIG_RISK_EXPIRE_DATE);
    }

    public static String getContigCoverageEffectiveDate(Record record) {
        return record.getStringValue(CONTIG_COVERAGE_EFFECTIVE_DATE);
    }

    public static String getContigCoverageExpireDate(Record record) {
        return record.getStringValue(CONTIG_COVERAGE_EXPIRE_DATE);
    }
}
