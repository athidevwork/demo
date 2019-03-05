package dti.pm.renewalquestionnairemgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Constants for Renewal Questionnaire.
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   June 11, 2007
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 * ---------------------------------------------------
 */
public class RenewalQuestionnaireFields {
    public static final String START_SEARCH_DATE = "startSearchDate";
    public static final String END_SEARCH_DATE = "endSearchDate";
    public static final String QUESTIONNAIRE_TYPE = "questionnaireType";
    public static final String SECOND_MAILING_DATE = "secondMailingDate";
    public static final String SECOND_MAILING_DATE_AVAILABLE = "secondMailingDateAvailable";
    public static final String THIRD_MAILING_DATE = "thirdMailingDate";
    public static final String THIRD_MAILING_DATE_AVAILABLE = "thirdMailingDateAvailable";
    public static final String DEADLINE_DATE = "deadlineDate";
    public static final String DEADLINE_DATE_AVAILABLE = "deadlineDateAvailable";
    public static final String SEARCH_QUESTIONNAIRE = "searchQuestionnaire";
    public static final String SEARCH_EXCEPTION = "searchException";
    public static final String GENERATE_QUESTIONNAIRE = "generateQuestionnaire";
    public static final String GENERATE_QUESTIONNAIRE_INFORMATION = "generateQuestionnaireInformation";
    public static final String QUESTIONNAIRE_RESULT = "questionnaireResult";
    public static final String POLICY_TYPE_DESC = "policyTypeDesc";
    public static final String RISK_CLASS_DESC = "riskClassDesc";
    public static final String MESSAGE = "message";
    public static final String RETMSG = "retmsg";
    public static final String RENEW_FORM_ID = "renewFormId";

    public static String getStartSearchDate(Record record) {
        return record.getStringValue(START_SEARCH_DATE);
    }

    public static String getEndSearchDate(Record record) {
        return record.getStringValue(END_SEARCH_DATE);
    }

    public static String getQuestionnaireType(Record record) {
        return record.getStringValue(QUESTIONNAIRE_TYPE);
    }

    public static String getSecondMailingDate(Record record) {
        return record.getStringValue(SECOND_MAILING_DATE);
    }

    public static String getSecondMailingDateAvailable(Record record) {
        return record.getStringValue(SECOND_MAILING_DATE_AVAILABLE);
    }

    public static String getThirdMailingDate(Record record) {
        return record.getStringValue(THIRD_MAILING_DATE);
    }

    public static String getThirdMailingDateAvailable(Record record) {
        return record.getStringValue(THIRD_MAILING_DATE_AVAILABLE);
    }

    public static String getDeadlineDate(Record record) {
        return record.getStringValue(DEADLINE_DATE);
    }

    public static String getDeadlineDateAvailable(Record record) {
        return record.getStringValue(DEADLINE_DATE_AVAILABLE);
    }

    public static String getSearchQuestionnaire(Record record) {
        if (record.hasField(SEARCH_QUESTIONNAIRE)) {
            return record.getStringValue(SEARCH_QUESTIONNAIRE);
        }
        else {
            return "N";
        }
    }

    public static String getSearchException(Record record) {
        if (record.hasField(SEARCH_EXCEPTION)) {
            return record.getStringValue(SEARCH_EXCEPTION);
        }
        else {
            return "N";
        }
    }

    public static String getGenerateQuestionnaire(Record record) {
        return record.getStringValue(GENERATE_QUESTIONNAIRE);
    }

    public static String getMessage(Record record) {
        return record.getStringValue(MESSAGE);
    }

    public static String getPolicyTypeDesc(Record record) {
        return record.getStringValue(POLICY_TYPE_DESC);
    }

    public static String getQuestionnaireResult(Record record) {
        return record.getStringValue(QUESTIONNAIRE_RESULT);
    }

    public static String getRenewFormId(Record record) {
        return record.getStringValue(RENEW_FORM_ID);
    }

    public static String getRetmsg(Record record) {
        return record.getStringValue(RETMSG);
    }

    public static String getRiskClassDesc(Record record) {
        return record.getStringValue(RISK_CLASS_DESC);
    }

    public static void setStartSearchDate(Record record, String startSearchDate) {
        record.setFieldValue(START_SEARCH_DATE, startSearchDate);
    }

    public static void setEndSearchDate(Record record, String endSearchDate) {
        record.setFieldValue(END_SEARCH_DATE, endSearchDate);
    }

    public static void setQuestionnaireType(Record record, String questionnaireType) {
        record.setFieldValue(QUESTIONNAIRE_TYPE, questionnaireType);
    }

    public static void setSecondMailingDate(Record record, String secondMailingDate) {
        record.setFieldValue(SECOND_MAILING_DATE, secondMailingDate);
    }

    public static void setSecondMailingDateAvailable(Record record, String secondMailingDateAvailable) {
        record.setFieldValue(SECOND_MAILING_DATE_AVAILABLE, secondMailingDateAvailable);
    }

    public static void setThirdMailingDate(Record record, String thirdMailingDate) {
        record.setFieldValue(THIRD_MAILING_DATE, thirdMailingDate);
    }

    public static void setThirdMailingDateAvailable(Record record, String thirdMailingDateAvailable) {
        record.setFieldValue(THIRD_MAILING_DATE_AVAILABLE, thirdMailingDateAvailable);
    }

    public static void setDeadlineDate(Record record, String deadlineDate) {
        record.setFieldValue(DEADLINE_DATE, deadlineDate);
    }

    public static void setDeadlineDateAvailable(Record record, String deadlineDateAvailable) {
        record.setFieldValue(DEADLINE_DATE_AVAILABLE, deadlineDateAvailable);
    }

    public static void setSearchQuestionnaire(Record record, YesNoFlag searchQuestionnaire) {
        record.setFieldValue(SEARCH_QUESTIONNAIRE, searchQuestionnaire);
    }

    public static void setSearchException(Record record, YesNoFlag searchException) {
        record.setFieldValue(SEARCH_EXCEPTION, searchException);
    }

    public static void setGenerateQuestionnaire(Record record, YesNoFlag generateQuestionnaire) {
        record.setFieldValue(GENERATE_QUESTIONNAIRE, generateQuestionnaire);
    }

    public static void setMessage(Record record, String message) {
        record.setFieldValue(MESSAGE, message);
    }

    public static void setPolicyTypeDesc(Record record, String policyTypeDesc) {
        record.setFieldValue(POLICY_TYPE_DESC, policyTypeDesc);
    }

    public static void setQuestionnaireResult(Record record, String questionnaireType) {
        record.setFieldValue(QUESTIONNAIRE_RESULT, questionnaireType);
    }

    public static void setRenewFormId(Record record, String renewFormId) {
        record.setFieldValue(RENEW_FORM_ID, renewFormId);
    }

    public static void setRetmsg(Record record, String retmsg) {
        record.setFieldValue(RETMSG, retmsg);
    }

    public static void setRiskClassDesc(Record record, String riskClassDesc) {
        record.setFieldValue(RISK_CLASS_DESC, riskClassDesc);
    }
}
