package dti.pm.renewalquestionnairemgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * Constants for Renewal Questionnaire Mailing Event.
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   June 16, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/12/2011       ryzhao     Issue 116543 - POLICY_TYPE added.
 * 09/13/2012       tcheng     Issue 137095 - FILTER_POLICY_NO and FILTER_NAME added.
 * 11/06/2012       xnie       Issue 138374 - Added field and get method.
 * ---------------------------------------------------
 */
public class QuestionnaireMailingEventFields {
    public static final String START_SEARCH_DATE = "startSearchDate";
    public static final String END_SEARCH_DATE = "endSearchDate";
    public static final String QUESTIONNAIRE_TYPE = "questionnaireType";
    public static final String SEARCH_MAILING_EVENT = "searchMailingEvent";
    public static final String FILTER_MAILING_EVENT = "filterMailingEvent";
    public static final String POLICY_TYPE_CODE = "policyTypeCode";
    public static final String POL_REN_FRM_MASTER_ID = "polRenfrmMasterId";
    public static final String POLICY_HOLDER_NAME = "policyHolderName";
    public static final String POLICY_HOLDER_NAME_UPCASE = "policyHolderNameUpcase";
    public static final String SAVE_QUESTION_INFO = "saveQuestionInfo";
    public static final String QUESTION_ERROR_MESSAGE = "questionErrorMessage";
    public static final String COMMENT_CHANGED = "commentChanged";
    public static final String COMMENTS = "comments";
    public static final String NOTE = "note";
    public static final String RISK_ID = "riskId";
    public static final String MESSAGE = "message";
    public static final String POLICY_NO = "policyNo";
    public static final String POLICY_NO_CRITERIA = "policyNoCriteria";
    public static final String POLICY_ID = "policyId";
    public static final String SEND_DATE = "sendDate";
    public static final String ADD_QUESTIONNAIRE = "addQuestionnaire";
    public static final String PRINT_OPTIONS = "printOptions";
    public static final String FILTER_SUCCESS = "filterSuccess";
    public static final String POLICY_TYPE = "policyType";
    public static final String FILTER_POLICY_NO = "filterPolicyNo";
    public static final String FILTER_NAME = "filterName";
    public static final String PERFORM_B = "performB";

    public static String getStartSearchDate(Record record) {
        return record.getStringValue(START_SEARCH_DATE);
    }

    public static String getEndSearchDate(Record record) {
        return record.getStringValue(END_SEARCH_DATE);
    }

    public static String getQuestionnaireType(Record record) {
        return record.getStringValue(QUESTIONNAIRE_TYPE);
    }

    public static YesNoFlag getSearchMailingEvent(Record record) {

        if (record.hasField(SEARCH_MAILING_EVENT)) {
            return YesNoFlag.getInstance(record.getStringValue(SEARCH_MAILING_EVENT));
        }
        else {
            return YesNoFlag.N;
        }
    }

    public static YesNoFlag getFilterMailingEvent(Record record) {
        if (record.hasField(FILTER_MAILING_EVENT)) {
            return YesNoFlag.getInstance(record.getStringValue(FILTER_MAILING_EVENT));
        }
        else {
            return YesNoFlag.N;
        }
    }

    public static String getPolicyTypeCode(Record record) {
        return record.getStringValue(POLICY_TYPE_CODE);
    }

    public static String getPolRenfrmMasterId(Record record) {
        return record.getStringValue(POL_REN_FRM_MASTER_ID);
    }

    public static String getPolicyHolderName(Record record) {
        return record.getStringValue(POLICY_HOLDER_NAME);
    }

    public static String getPolicyHolderNameUpcase(Record record) {
        return record.getStringValue(POLICY_HOLDER_NAME_UPCASE);
    }

    public static String getSaveQuestionInfo(Record record) {
        return record.getStringValue(SAVE_QUESTION_INFO);
    }

    public static String getQuestionErrorMessage(Record record) {
        return record.getStringValue(QUESTION_ERROR_MESSAGE);
    }

    public static YesNoFlag getCommentChanged(Record record) {
        if (record.hasField(COMMENT_CHANGED)) {
            return YesNoFlag.getInstance(record.getStringValue(COMMENT_CHANGED));
        }
        else {
            return YesNoFlag.N;
        }
    }

    public static String getComments(Record record) {
        return record.getStringValue(COMMENTS);
    }

    public static String getNote(Record record) {
        return record.getStringValue(NOTE);
    }

    public static String getRiskId(Record record) {
        return record.getStringValue(RISK_ID);
    }

    public static String getMessage(Record record) {
        return record.getStringValue(MESSAGE);
    }

    public static String getPolicyNo(Record record) {
        return record.getStringValue(POLICY_NO);
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static String getSendDate(Record record) {
        return record.getStringValue(SEND_DATE);
    }

    public static String getAddQuestionnaire(Record record) {
        return record.getStringValue(ADD_QUESTIONNAIRE);
    }

    public static String getPrintOptions(Record record) {
        return record.getStringValue(PRINT_OPTIONS);
    }

    public static String getFilterSuccess(Record record) {
        return record.getStringValue(FILTER_SUCCESS);
    }

    public static String getPolicyType(Record record) {
        return record.getStringValue(POLICY_TYPE);
    }

    public static String getFilterPolicyNo(Record record) {
        return record.getStringValue(FILTER_POLICY_NO);
    }

    public static String getFilterName(Record record) {
        return record.getStringValue(FILTER_NAME);
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

    public static void setSearchMailingEvent(Record record, String searchMailingEvent) {
        record.setFieldValue(SEARCH_MAILING_EVENT, searchMailingEvent);
    }

    public static void setFilterMailingEvent(Record record, String filterMailingEvent) {
        record.setFieldValue(FILTER_MAILING_EVENT, filterMailingEvent);
    }

    public static void setPolicyTypeCode(Record record, String policyTypeCode) {
        record.setFieldValue(POLICY_TYPE_CODE, policyTypeCode);
    }

    public static void setPolRenfrmMasterId(Record record, String polRenfrmMasterId) {
        record.setFieldValue(POL_REN_FRM_MASTER_ID, polRenfrmMasterId);
    }

    public static void setPolicyHolderName(Record record, String policyHolderName) {
        record.setFieldValue(POLICY_HOLDER_NAME, policyHolderName);
    }

    public static void setPolicyHolderNameUpcase(Record record, String policyHolderNameUpcase) {
        record.setFieldValue(POLICY_HOLDER_NAME_UPCASE, policyHolderNameUpcase);
    }

    public static void setSaveQuestionInfo(Record record, String saveQuestionInfo) {
        record.setFieldValue(SAVE_QUESTION_INFO, saveQuestionInfo);
    }

    public static void setQuestionErrorMessage(Record record, String questionErrorMessage) {
        record.setFieldValue(QUESTION_ERROR_MESSAGE, questionErrorMessage);
    }

    public static void setCommentChanged(Record record, String commentChanged) {
        record.setFieldValue(COMMENT_CHANGED, commentChanged);
    }

    public static void setComments(Record record, String comments) {
        record.setFieldValue(COMMENTS, comments);
    }

    public static void setNote(Record record, String note) {
        record.setFieldValue(NOTE, note);
    }

    public static void setRiskId(Record record, String riskId) {
        record.setFieldValue(RISK_ID, riskId);
    }

    public static void setMessage(Record record, String message) {
        record.setFieldValue(MESSAGE, message);
    }

    public static void setPolicyNo(Record record, String policyNo) {
        record.setFieldValue(POLICY_NO, policyNo);
    }

    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID, policyId);
    }

    public static void setSendDate(Record record, String sendDate) {
        record.setFieldValue(SEND_DATE, sendDate);
    }

    public static void setAddQuestionnaire(Record record, String addQuestionnaire) {
        record.setFieldValue(ADD_QUESTIONNAIRE, addQuestionnaire);
    }

    public static void setPrintOptions(Record record, String printOptions) {
        record.setFieldValue(PRINT_OPTIONS, printOptions);
    }

    public static void setFilterSuccess(Record record, String filterSuccess) {
        record.setFieldValue(FILTER_SUCCESS, filterSuccess);
    }

    public static void setPolicyType(Record record, String policyType) {
        record.setFieldValue(POLICY_TYPE, policyType);
    }

    public static void setFilterPolicyNo(Record record, String filterPolicyNo) {
        record.setFieldValue(FILTER_POLICY_NO, filterPolicyNo);
    }

    public static void setFilterName(Record record, String filterName) {
        record.setFieldValue(FILTER_NAME, filterName);
    }

    public static YesNoFlag getPerformB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(PERFORM_B));
    }
}
