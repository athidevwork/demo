package dti.pm.componentmgr.experiencemgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Helper constants and set/get methods to access Experience Component Fields.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 29, 2009
 *
 * @author gchitta
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/03/2011       ryzhao      113559 - Added some new fields for process ERP page.
 * 11/04/2011       ryzhao      126084 - Added IS_POPUP_PAGE.
 * ---------------------------------------------------
 */
public class ExperienceComponentFields {
    public static final String START_SEARCH_DATE = "startSearchDate";
    public static final String END_SEARCH_DATE = "endSearchDate";
    public static final String PROCESS_DATE = "processDate";
    public static final String RETMSG = "retmsg";

    //Added for 113559 process ERP.
    public static final String RENEWAL_DATE = "renewalDate";
    public static final String RENEWAL_YEAR = "renewalYear";
    public static final String ERP_ISSUE_STATE_CODE = "erpIssueStateCode";
    public static final String BATCH_NO = "batchNo";
    public static final String POLICY_ID = "policyId";
    public static final String RISK_ID = "riskId";
    public static final String TRANS_LOG_ID = "transLogId";
    public static final String TERM_ID = "termId";
    public static final String TERM_EFF = "termEff";
    public static final String TERM_EXP = "termExp";
    public static final String TRANS_EFF = "transEff";
    public static final String SHOW_ALL = "showAll";
    public static final String CALLED_FROM = "calledFrom";
    public static final String IS_DELETE_ERP_BATCH_SUCCESS = "isDeleteErpBatchSuccess";
    public static final String IS_DELETE_BATCH_AVAILABLE = "isDeleteBatchAvailable";
    public static final String IS_PROCESS_ERP_AVAILABLE = "isProcessErpAvailable";
    public static final String IS_SAVE_AVAILABLE = "isSaveAvailable";
    public static final String IS_UPDATE_MODE = "isUpdateMode";
    public static final String IS_CLOSE_AVAILABLE = "isCloseAvailable";
    public static final String ELIGIBLE_CR_B = "eligibleCrB";
    public static final String ELIGIBLE_DB_B = "eligibleDbB";
    public static final String IS_POPUP_PAGE = "isPopupPage";

    public static String getStartSearchDate(Record record) {
        return record.getStringValue(START_SEARCH_DATE);
    }

    public static String getEndSearchDate(Record record) {
        return record.getStringValue(END_SEARCH_DATE);
    }

    public static String getProcessDate(Record record) {
        return record.getStringValue(PROCESS_DATE);
    }

    public static void setStartSearchDate(Record record, String startSearchDate) {
        record.setFieldValue(START_SEARCH_DATE, startSearchDate);
    }

    public static void setEndSearchDate(Record record, String endSearchDate) {
        record.setFieldValue(END_SEARCH_DATE, endSearchDate);
    }

    public static void setProcessDate(Record record, String processDate) {
        record.setFieldValue(PROCESS_DATE, processDate);
    }

    public static String getRetmsg(Record record) {
        return record.getStringValue(RETMSG);
    }

    public static void setRetmsg(Record record, String retmsg) {
        record.setFieldValue(RETMSG, retmsg);
    }

    public static String getRenewalDate(Record record) {
        return record.getStringValue(RENEWAL_DATE);
    }

    public static void setRenewalDate(Record record, String renewalDate) {
        record.setFieldValue(RENEWAL_DATE, renewalDate);
    }

    public static String getRenewalYear(Record record) {
        return record.getStringValue(RENEWAL_YEAR);
    }

    public static void setRenewalYear(Record record, String renewalYear) {
        record.setFieldValue(RENEWAL_YEAR, renewalYear);
    }

    public static String getErpIssueStateCode(Record record) {
        return record.getStringValue(ERP_ISSUE_STATE_CODE);
    }

    public static void setErpIssueStateCode(Record record, String erpIssueStateCode) {
        record.setFieldValue(ERP_ISSUE_STATE_CODE, erpIssueStateCode);
    }

    public static String getBatchNo(Record record) {
        return record.getStringValue(BATCH_NO);
    }

    public static void setBatchNo(Record record, String batchNo) {
        record.setFieldValue(BATCH_NO, batchNo);
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID, policyId);
    }

    public static String getRiskId(Record record) {
        return record.getStringValue(RISK_ID);
    }

    public static void setRiskId(Record record, String riskId) {
        record.setFieldValue(RISK_ID, riskId);
    }

    public static String getTransLogId(Record record) {
        return record.getStringValue(TRANS_LOG_ID);
    }

    public static void setTransLogId(Record record, String transLogId) {
        record.setFieldValue(TRANS_LOG_ID, transLogId);
    }

    public static String getTermId(Record record) {
        return record.getStringValue(TERM_ID);
    }

    public static void setTermId(Record record, String termId) {
        record.setFieldValue(TERM_ID, termId);
    }

    public static String getTermEff(Record record) {
        return record.getStringValue(TERM_EFF);
    }

    public static void setTermEff(Record record, String termEff) {
        record.setFieldValue(TERM_EFF, termEff);
    }

    public static String getTermExp(Record record) {
        return record.getStringValue(TERM_EXP);
    }

    public static void setTermExp(Record record, String termExp) {
        record.setFieldValue(TERM_EXP, termExp);
    }

    public static String getTransEff(Record record) {
        return record.getStringValue(TRANS_EFF);
    }

    public static void setTransEff(Record record, String transEff) {
        record.setFieldValue(TRANS_EFF, transEff);
    }

    public static YesNoFlag getShowAll(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(SHOW_ALL));
    }

    public static void setShowAll(Record record, YesNoFlag showAll) {
        record.setFieldValue(SHOW_ALL, showAll);
    }

    public static String getCalledFrom(Record record) {
        return record.getStringValue(CALLED_FROM);
    }

    public static void setCalledFrom(Record record, String calledFrom) {
        record.setFieldValue(CALLED_FROM, calledFrom);
    }

    public static YesNoFlag getIsDeleteErpBatchSuccess(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_DELETE_ERP_BATCH_SUCCESS));
    }

    public static void setIsDeleteErpBatchSuccess(Record record, YesNoFlag isDeleteErpBatchSuccess) {
        record.setFieldValue(IS_DELETE_ERP_BATCH_SUCCESS, isDeleteErpBatchSuccess);
    }

    public static YesNoFlag getIsDeleteBatchAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_DELETE_BATCH_AVAILABLE));
    }

    public static void setIsDeleteBatchAvailable(Record record, YesNoFlag isDeleteBatchAvailable) {
        record.setFieldValue(IS_DELETE_BATCH_AVAILABLE, isDeleteBatchAvailable);
    }

    public static YesNoFlag getIsProcessErpAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_PROCESS_ERP_AVAILABLE));
    }

    public static void setIsProcessErpAvailable(Record record, YesNoFlag isProcessErpAvailable) {
        record.setFieldValue(IS_PROCESS_ERP_AVAILABLE, isProcessErpAvailable);
    }

    public static YesNoFlag getIsSaveAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_SAVE_AVAILABLE));
    }

    public static void setIsSaveAvailable(Record record, YesNoFlag isSaveAvailable) {
        record.setFieldValue(IS_SAVE_AVAILABLE, isSaveAvailable);
    }

    public static YesNoFlag getIsUpdateMode(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_UPDATE_MODE));
    }

    public static void setIsUpdateMode(Record record, YesNoFlag isUpdateMode) {
        record.setFieldValue(IS_UPDATE_MODE, isUpdateMode);
    }

    public static YesNoFlag getIsCloseAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_CLOSE_AVAILABLE));
    }

    public static void setIsCloseAvailable(Record record, YesNoFlag isCloseAvailable) {
        record.setFieldValue(IS_CLOSE_AVAILABLE, isCloseAvailable);
    }

    public static YesNoFlag getEligibleCrB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(ELIGIBLE_CR_B));
    }

    public static void setEligibleCrB(Record record, YesNoFlag eligibleCrB) {
        record.setFieldValue(ELIGIBLE_CR_B, eligibleCrB);
    }

    public static YesNoFlag getEligibleDbB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(ELIGIBLE_DB_B));
    }

    public static void setEligibleDbB(Record record, YesNoFlag eligibleDbB) {
        record.setFieldValue(ELIGIBLE_DB_B, eligibleDbB);
    }

    public static YesNoFlag getIsPopupPage(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_POPUP_PAGE));
    }

    public static void setIsPopupPage(Record record, YesNoFlag isPopupPage) {
        record.setFieldValue(IS_POPUP_PAGE, isPopupPage);
    }
}