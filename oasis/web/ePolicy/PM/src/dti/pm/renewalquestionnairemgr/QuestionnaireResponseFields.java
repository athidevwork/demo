package dti.pm.renewalquestionnairemgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Constants for Renewal Questionnaire Response.
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   July 08, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/03/2011       ryzhao      117394 - Added showResponseSection field.
 * ---------------------------------------------------
 */
public class QuestionnaireResponseFields {
    public static final String COME_FROM_MAILING_EVENT = "comeFromMailingEvent";
    public static final String IS_NEW_DATE = "isNewDate";
    public static final String SEARCH_DATE = "searchDate";
    public static final String SEARCH_DATE_ERROR = "searchDateError";
    public static final String SAVE_DATE_SUCCESS = "saveDateSuccess";
    public static final String DATA_CHANGED = "dataChanged";
    public static final String RESPONSE_URL = "responseURL";
    public static final String REOPEN_RESPONSE = "reopenResponse";
    public static final String SAVE_RESPONSE = "saveResponse";
    public static final String APP_STATUS = "appStatus";
    public static final String STATUS = "status";
    public static final String POLICY_NO_CRITERIA = "policyNoCriteria";
    public static final String POLICY_ID = "policyId";
    public static final String RISK_ID = "riskId";
    public static final String APP_HEADER_ID = "appHeaderId";
    public static final String APP_ID = "appId";
    public static final String WEB_TYPE = "webType";
    public static final String WEB_APP_HEADER_ID = "webAppHeaderId";
    public static final String WEB_ID = "webId";
    public static final String TYPE = "type";
    public static final String EFFECTIVE_DATE = "effectiveDate";
    public static final String POLICY_RENEW_FORM_ID = "policyRenewFormId";
    public static final String QUESTIONNAIRE = "questionnaire";
    public static final String APP_SENT = "appSentDate";
    public static final String APP_RECEIVED = "appReceivedDate";
    public static final String APP_TO_RN = "appToRm";
    public static final String APP_FROM_RM = "appReturnedFromRM";
    public static final String ORIGINAL_SENT = "date1";
    public static final String UNDERWRITER_APPROVED = "underwriterApprDate";
    public static final String UNDERWRITER_DECLINED = "underwriterDeclDate";
    public static final String CALL_COPIC_PARTICIPANT = "callCopicB";
    public static final String CHAR_1 = "char1";
    public static final String CHAR_2 = "char2";
    public static final String CHAR_3 = "char3";
    public static final String NUM_1 = "num1";
    public static final String NUM_2 = "num2";
    public static final String NUM_3 = "num3";
    public static final String DATE_2 = "date2";
    public static final String DATE_3 = "date3";
    public static final String TERM_EFFECTIVE_DATE = "termEffectiveDate";
    public static final String TERM_EXPIRATION_DATE = "termExpirationDate";
    public static final String TERM_BASE_ID = "termBaseId";
    public static final String TERM_EFF_DT = "termEffDt";
    public static final String TERM_EXP_DT = "termExpDt";
    // Hidden fields
    public static final String PM_APPLICATION_TRACKING_ID = "pmApplicationTrackingId";
    public static final String SOURCE_TABLE_NAME = "sourceTableName";
    public static final String SOURCE_RECORD_ID = "sourceRecordId";
    public static final String SHOW_RESPONSE_SECTION = "showResponseSection";

    public static YesNoFlag getComeFromMailingEvent(Record record) {
        if (record.hasStringValue(COME_FROM_MAILING_EVENT)) {
            return YesNoFlag.getInstance(record.getStringValue(COME_FROM_MAILING_EVENT));
        }
        return YesNoFlag.N;
    }

    public static YesNoFlag getIsNewDate(Record record) {
        if (record.hasStringValue(IS_NEW_DATE)) {
            return YesNoFlag.getInstance(record.getStringValue(IS_NEW_DATE));
        }
        return YesNoFlag.N;
    }

    public static YesNoFlag getSearchDate(Record record) {
        if (record.hasStringValue(SEARCH_DATE)) {
            return YesNoFlag.getInstance(record.getStringValue(SEARCH_DATE));
        }
        return YesNoFlag.N;
    }

    public static YesNoFlag getSearchDateError(Record record) {
        if (record.hasStringValue(SEARCH_DATE_ERROR)) {
            return YesNoFlag.getInstance(record.getStringValue(SEARCH_DATE_ERROR));
        }
        return YesNoFlag.N;
    }

    public static YesNoFlag getSaveDateSuccess(Record record) {
        if (record.hasStringValue(SAVE_DATE_SUCCESS)) {
            return YesNoFlag.getInstance(record.getStringValue(SAVE_DATE_SUCCESS));
        }
        return YesNoFlag.N;
    }

    public static YesNoFlag getDataChanged(Record record) {
        if (record.hasStringValue(DATA_CHANGED)) {
            return YesNoFlag.getInstance(record.getStringValue(DATA_CHANGED));
        }
        return YesNoFlag.N;
    }

    public static String getResponseURL(Record record) {
        return record.getStringValue(RESPONSE_URL);
    }

    public static YesNoFlag getReopenResponse(Record record) {
        if (record.hasStringValue(REOPEN_RESPONSE)) {
            return YesNoFlag.getInstance(record.getStringValue(REOPEN_RESPONSE));
        }
        return YesNoFlag.N;
    }

    public static YesNoFlag getSaveResponse(Record record) {
        if (record.hasStringValue(SAVE_RESPONSE)) {
            return YesNoFlag.getInstance(record.getStringValue(SAVE_RESPONSE));
        }
        return YesNoFlag.N;
    }

    public static String getAppStatus(Record record) {
        return record.getStringValue(APP_STATUS);
    }

    public static String getStatus(Record record) {
        if (record.hasStringValue(STATUS)) {
            return record.getStringValue(STATUS);
        }
        else {
            return "";
        }
    }

    public static String getPolicyNoCriteria(Record record) {
        if (record.hasStringValue(POLICY_NO_CRITERIA)) {
            return record.getStringValue(POLICY_NO_CRITERIA);
        }
        else {
            return "";
        }
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static String getRiskId(Record record) {
        if (record.hasStringValue(RISK_ID)) {
            return record.getStringValue(RISK_ID);
        }
        else {
            return "";
        }
    }

    public static String getAppHeaderId(Record record) {
        return record.getStringValue(APP_HEADER_ID);
    }

    public static String getAppId(Record record) {
        return record.getStringValue(APP_ID);
    }

    public static String getType(Record record) {
        return record.getStringValue(TYPE);
    }

    public static String getWebType(Record record) {
        return record.getStringValue(WEB_TYPE);
    }

    public static String getWebAppHeaderId(Record record) {
        return record.getStringValue(WEB_APP_HEADER_ID);
    }

    public static String getWebId(Record record) {
        return record.getStringValue(WEB_ID);
    }

    public static String getEffectiveDate(Record record) {
        return record.getStringValue(EFFECTIVE_DATE);
    }

    public static String getPolicyRenewalFormId(Record record) {
        return record.getStringValue(POLICY_RENEW_FORM_ID);
    }

    public static String getQuestionnaire(Record record) {
        if (record.hasStringValue(QUESTIONNAIRE)) {
            return record.getStringValue(QUESTIONNAIRE);
        }
        else {
            return "";
        }
    }

    public static String getAppSent(Record record) {
        return record.getStringValue(APP_SENT);
    }

    public static String getAppReceived(Record record) {
        return record.getStringValue(APP_RECEIVED);
    }

    public static String getAppToRm(Record record) {
        return record.getStringValue(APP_TO_RN);
    }

    public static String getAppFromRm(Record record) {
        return record.getStringValue(APP_FROM_RM);
    }

    public static String getOriginalSent(Record record) {
        return record.getStringValue(ORIGINAL_SENT);
    }

    public static String getUnderwriterApproved(Record record) {
        return record.getStringValue(UNDERWRITER_APPROVED);
    }

    public static String getUnderwriterDeclined(Record record) {
        return record.getStringValue(UNDERWRITER_DECLINED);
    }

    public static String getCallCopicParticipant(Record record) {
        return record.getStringValue(CALL_COPIC_PARTICIPANT);
    }

    public static String getChar1(Record record) {
        return record.getStringValue(CHAR_1);
    }

    public static String getChar2(Record record) {
        return record.getStringValue(CHAR_2);
    }

    public static String getChar3(Record record) {
        return record.getStringValue(CHAR_3);
    }

    public static String getNum1(Record record) {
        return record.getStringValue(NUM_1);
    }

    public static String getNum2(Record record) {
        return record.getStringValue(NUM_2);
    }

    public static String getNum3(Record record) {
        return record.getStringValue(NUM_3);
    }

    public static String getDate2(Record record) {
        return record.getStringValue(DATE_2);
    }

    public static String getDate3(Record record) {
        return record.getStringValue(DATE_3);
    }

    public static String getTermEffectiveDate(Record record) {
        return record.getStringValue(TERM_EFFECTIVE_DATE);
    }

    public static String getTermExpirationDate(Record record) {
        return record.getStringValue(TERM_EXPIRATION_DATE);
    }

    public static String getTermBaseId(Record record) {
        return record.getStringValue(TERM_BASE_ID);
    }

    public static String getTermEffDt(Record record) {
        return record.getStringValue(TERM_EFF_DT);
    }

    public static String getTermExpDt(Record record) {
        return record.getStringValue(TERM_EXP_DT);
    }

    public static String getPmApplicationTrackingId(Record record) {
        return record.getStringValue(PM_APPLICATION_TRACKING_ID);
    }

    public static String getSourceTableName(Record record) {
        return record.getStringValue(SOURCE_TABLE_NAME);
    }

    public static String getSourceRecordId(Record record) {
        return record.getStringValue(SOURCE_RECORD_ID);
    }

    public static YesNoFlag getShowResponseSection(Record record) {
        if (record.hasStringValue(SHOW_RESPONSE_SECTION)) {
            return YesNoFlag.getInstance(record.getStringValue(SHOW_RESPONSE_SECTION));
        }
        return YesNoFlag.N;
    }

    public static void setComeFromMailingEvent(Record record, YesNoFlag comeFromMailingEvent) {
        record.setFieldValue(COME_FROM_MAILING_EVENT, comeFromMailingEvent);
    }

    public static void setIsNewDate(Record record, YesNoFlag isNewDate) {
        record.setFieldValue(IS_NEW_DATE, isNewDate);
    }

    public static void setSearchDate(Record record, YesNoFlag searchDate) {
        record.setFieldValue(SEARCH_DATE, searchDate);
    }

    public static void setSearchDateError(Record record, YesNoFlag searchDateError) {
        record.setFieldValue(SEARCH_DATE_ERROR, searchDateError);
    }

    public static void setSaveDateSuccess(Record record, YesNoFlag saveDateSuccess) {
        record.setFieldValue(SAVE_DATE_SUCCESS, saveDateSuccess);
    }

    public static void setDataChanged(Record record, YesNoFlag dataChanged) {
        record.setFieldValue(DATA_CHANGED, dataChanged);
    }

    public static void setResponseURL(Record record, String responseURL) {
        record.setFieldValue(RESPONSE_URL, responseURL);
    }

    public static void setReopenResponse(Record record, YesNoFlag reopenResponse) {
        record.setFieldValue(REOPEN_RESPONSE, reopenResponse);
    }

    public static void setSaveResponse(Record record, YesNoFlag saveResponse) {
        record.setFieldValue(SAVE_RESPONSE, saveResponse);
    }

    public static void setAppStatus(Record record, String appStatus) {
        record.setFieldValue(APP_STATUS, appStatus);
    }

    public static void setStatus(Record record, String status) {
        record.setFieldValue(STATUS, status);
    }

    public static void setPolicyNoCriteria(Record record, String policyNoCriteria) {
        record.setFieldValue(POLICY_NO_CRITERIA, policyNoCriteria);
    }

    public static void setPolicyID(Record record, String policyId) {
        record.setFieldValue(POLICY_ID, policyId);
    }

    public static void setRiskId(Record record, String riskId) {
        record.setFieldValue(RISK_ID, riskId);
    }

    public static void setAppHeaderId(Record record, String appHeaderId) {
        record.setFieldValue(APP_HEADER_ID, appHeaderId);
    }

    public static void setAppId(Record record, String appId) {
        record.setFieldValue(APP_ID, appId);
    }

    public static void setType(Record record, String type) {
        record.setFieldValue(TYPE, type);
    }


    public static void setWebType(Record record, String webType) {
        record.setFieldValue(WEB_TYPE, webType);
    }

    public static void setWebAppHeaderId(Record record, String webAppHeaderId) {
        record.setFieldValue(WEB_APP_HEADER_ID, webAppHeaderId);
    }

    public static void setWebId(Record record, String webId) {
        record.setFieldValue(WEB_ID, webId);
    }

    public static void setEffectiveDate(Record record, String effectiveDate) {
        record.setFieldValue(EFFECTIVE_DATE, effectiveDate);
    }

    public static void setPolicyRenewalFormId(Record record, String policyRenewFormId) {
        record.setFieldValue(POLICY_RENEW_FORM_ID, policyRenewFormId);
    }

    public static void setQuestionnaire(Record record, String questionnaire) {
        record.setFieldValue(QUESTIONNAIRE, questionnaire);
    }

    public static void setAppSent(Record record, String appSent) {
        record.setFieldValue(APP_SENT, appSent);
    }

    public static void setAppReceived(Record record, String appReceived) {
        record.setFieldValue(APP_RECEIVED, appReceived);
    }

    public static void setAppToRm(Record record, String appToRm) {
        record.setFieldValue(APP_TO_RN, appToRm);
    }

    public static void setAppFromRm(Record record, String appFromRm) {
        record.setFieldValue(APP_FROM_RM, appFromRm);
    }

    public static void setOriginalSent(Record record, String originalSent) {
        record.setFieldValue(ORIGINAL_SENT, originalSent);
    }

    public static void setUnderwriterApproved(Record record, String underwriterApproved) {
        record.setFieldValue(UNDERWRITER_APPROVED, underwriterApproved);
    }

    public static void setUnderwriterDeclined(Record record, String underwriterDeclined) {
        record.setFieldValue(UNDERWRITER_DECLINED, underwriterDeclined);
    }

    public static void setCallCopicParticipant(Record record, String copicParticipant) {
        record.setFieldValue(CALL_COPIC_PARTICIPANT, copicParticipant);
    }

    public static void setChar1(Record record, String char1) {
        record.setFieldValue(CHAR_1, char1);
    }

    public static void setChar2(Record record, String char2) {
        record.setFieldValue(CHAR_2, char2);
    }

    public static void setChar3(Record record, String char3) {
        record.setFieldValue(CHAR_3, char3);
    }

    public static void setNum1(Record record, String num1) {
        record.setFieldValue(NUM_1, num1);
    }

    public static void setNum2(Record record, String num2) {
        record.setFieldValue(NUM_2, num2);
    }

    public static void setNum3(Record record, String num3) {
        record.setFieldValue(NUM_3, num3);
    }

    public static void setDate2(Record record, String date2) {
        record.setFieldValue(DATE_2, date2);
    }

    public static void setDate3(Record record, String date3) {
        record.setFieldValue(DATE_3, date3);
    }

    public static void setTermEffectiveDate(Record record, String termEffectiveDate) {
        record.setFieldValue(TERM_EFFECTIVE_DATE, termEffectiveDate);
    }

    public static void setTermExpirationDate(Record record, String termExpirationDate) {
        record.setFieldValue(TERM_EXPIRATION_DATE, termExpirationDate);
    }

    public static void setTermBaseId(Record record, String termBaseId) {
        record.setFieldValue(TERM_BASE_ID, termBaseId);
    }

    public static void setTermEffDt(Record record, String termEffDt) {
        record.setFieldValue(TERM_EFF_DT, termEffDt);
    }

    public static void setTermExpDt(Record record, String termExpDt) {
        record.setFieldValue(TERM_EXP_DT, termExpDt);
    }

    public static void setPmApplicationTrackingId(Record record, String pmApplicationTrackingId) {
        record.setFieldValue(PM_APPLICATION_TRACKING_ID, pmApplicationTrackingId);
    }

    public static void setSourceTableName(Record record, String sourceTableName) {
        record.setFieldValue(SOURCE_TABLE_NAME, sourceTableName);
    }

    public static void setSourceRecordId(Record record, String sourceRecordId) {
        record.setFieldValue(SOURCE_RECORD_ID, sourceRecordId);
    }

    public static void setShowResponseSection(Record record, YesNoFlag showResponseSection) {
        record.setFieldValue(SHOW_RESPONSE_SECTION, showResponseSection);
    }
}
