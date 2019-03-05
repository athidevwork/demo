package dti.pm.riskmgr.insuredmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2015 Delphi Technology, inc. (dti)</p>
 * Date:   April 08, 2015
 *
 * @author wdang
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 04/08/2015    wdang      157211 - Initial version.
 * ---------------------------------------------------
 */
public class InsuredTrackingFields {

    // fields for search criteria
    public static final String SEARCH_TERM_HISTORY_ID = "searchTermHistoryId";
    public static final String SEARCH_INSURED_TYPE = "searchInsuredType";
    public static final String SEARCH_ENTITY_ID = "searchEntityId";
    
    // fields for GH/href
    public static final String INSURED_NAME_GH = "insuredName_GH";
    public static final String INSURED_NAME_HREF = "insuredNameHREF";
    
    // fields for grid/form
    public static final String INSURED_TRACKING_ID = "insuredTrackingId";
    public static final String ENTITY_ID = "entityId";
    public static final String INSURED_TYPE = "insuredType";
    public static final String INSURED_NAME = "insuredName";
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String RISK_ID = "riskId";
    public static final String RETROACTIVE_DATE = "retroactiveDate";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";

    // fields for versions
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";

    // fields for page entitlement
    public static final String IS_DELETE_VISIBLE = "isDeleteVisible";
    public static final String IS_PAGE_EDITABLE = "isPageEditable";
    public static final String IS_INSURED_TYPE_EDITABLE = "isInsuredTypeEditable";

    // fields for validation
    public static final String STATUS_CODE = "statusCode";

    public static String getSearchTermHistoryId(Record record) {
        return record.getStringValue(SEARCH_TERM_HISTORY_ID);
    }

    public static void setSearchTermHistoryId(Record record, String searchTermHistoryId) {
        record.setFieldValue(SEARCH_TERM_HISTORY_ID, searchTermHistoryId);
    }
    
    public static String getSearchInsuredType(Record record) {
        return record.getStringValue(SEARCH_INSURED_TYPE);
    }

    public static void setSearchInsuredType(Record record, String searchInsuredType) {
        record.setFieldValue(SEARCH_INSURED_TYPE, searchInsuredType);
    }
    
    public static String getSearchEntityId(Record record) {
        return record.getStringValue(SEARCH_ENTITY_ID);
    }

    public static void setSearchEntityId(Record record, String searchEntityId) {
        record.setFieldValue(SEARCH_ENTITY_ID, searchEntityId);
    }

    public static String getInsuredTrackingId(Record record) {
        return record.getStringValue(INSURED_TRACKING_ID);
    }

    public static void setInsuredTrackingId(Record record, String insuredTrackingId) {
        record.setFieldValue(INSURED_TRACKING_ID, insuredTrackingId);
    }

    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static String getInsuredType(Record record) {
        return record.getStringValue(INSURED_TYPE);
    }

    public static void setInsuredType(Record record, String insuredType) {
        record.setFieldValue(INSURED_TYPE, insuredType);
    }

    public static String getInsuredName(Record record) {
        return record.getStringValue(INSURED_NAME);
    }

    public static void setInsuredName(Record record, String insuredName) {
        record.setFieldValue(INSURED_NAME, insuredName);
    }

    public static String getRiskId(Record record) {
        return record.getStringValue(RISK_ID);
    }

    public static void setRiskId(Record record, String riskId) {
        record.setFieldValue(RISK_ID, riskId);
    }

    public static String getRetroactiveDate(Record record) {
        return record.getStringValue(RETROACTIVE_DATE);
    }

    public static void setRetroactiveDate(Record record, String retroactiveDate) {
        record.setFieldValue(RETROACTIVE_DATE, retroactiveDate);
    }

    public static String getStartDate(Record record) {
        return record.getStringValue(START_DATE);
    }

    public static void setStartDate(Record record, String startDate) {
        record.setFieldValue(START_DATE, startDate);
    }

    public static String getEndDate(Record record) {
        return record.getStringValue(END_DATE);
    }

    public static void setEndDate(Record record, String endDate) {
        record.setFieldValue(END_DATE, endDate);
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
    
    public static String getClosingTransLogId(Record record) {
        return record.getStringValue(CLOSING_TRANS_LOG_ID);
    }

    public static void setClosingTransLogId(Record record, String closingTransLogId) {
        record.setFieldValue(CLOSING_TRANS_LOG_ID, closingTransLogId);
    }

}
