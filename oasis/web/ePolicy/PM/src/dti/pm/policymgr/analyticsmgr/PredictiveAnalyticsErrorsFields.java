package dti.pm.policymgr.analyticsmgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 9, 2011
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
public class PredictiveAnalyticsErrorsFields {
    public static final String SEARCH_CRITERIA_OPA_SCORE_REQ_ID = "searchCriteria_scoreReqId";
    public static final String SEARCH_CRITERIA_SCORE_REQ_START_DATE = "searchCriteria_scoreReqStartDate";
    public static final String SEARCH_CRITERIA_SCORE_REQ_END_DATE = "searchCriteria_scoreReqEndDate";
    public static final String SEARCH_CRITERIA_SCORE_REQ_TYPE_CODE = "searchCriteria_scoreReqTypeCode";
    public static final String MAX_ROW = "maxRow";

    public static String getSearchCriteriaOpaScoreReqId(Record record) {
        return record.getStringValue(SEARCH_CRITERIA_OPA_SCORE_REQ_ID, "");
    }

    public static void setSearchCriteriaOpaScoreReqId(Record record, String opaScoreReqId) {
        record.setFieldValue(SEARCH_CRITERIA_OPA_SCORE_REQ_ID, opaScoreReqId);
    }

    public static String getSearchCriteriaScoreReqStartDate(Record record) {
        return record.getStringValue(SEARCH_CRITERIA_SCORE_REQ_START_DATE, "");
    }

    public static void setSearchCriteriaScoreReqStartDate(Record record, String scoreReqStartDate) {
        record.setFieldValue(SEARCH_CRITERIA_SCORE_REQ_START_DATE, scoreReqStartDate);
    }

    public static String getSearchCriteriaScoreReqEndDate(Record record) {
        return record.getStringValue(SEARCH_CRITERIA_SCORE_REQ_END_DATE, "");
    }

    public static void setSearchCriteriaScoreReqEndDate(Record record, String scoreReqEndDate) {
        record.setFieldValue(SEARCH_CRITERIA_SCORE_REQ_END_DATE, scoreReqEndDate);
    }

    public static String getSearchCriteriaScoreReqTypeCode(Record record) {
        return record.getStringValue(SEARCH_CRITERIA_SCORE_REQ_TYPE_CODE, "");
    }

    public static void setSearchCriteriaScoreReqTypeCode(Record record, String scoreReqTypeCode) {
        record.setFieldValue(SEARCH_CRITERIA_SCORE_REQ_TYPE_CODE, scoreReqTypeCode);
    }

    public static String getMaxRow(Record record) {
        return record.getStringValue(MAX_ROW, "");
    }

    public static void setMaxRow(Record record, String maxRow) {
        record.setFieldValue(MAX_ROW, maxRow);
    }

    public class MaxRowValue {
        public static final String DEFAULT_MAX_ROW = "200";
    }
}
