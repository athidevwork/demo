package dti.pm.policymgr.dividendmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Constant field class for process distribution
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 10, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/13/2012       wfu         128705 - Added required fields to handle new dividend process.
 * 12/26/2013       awu         148187 - Added TRANSACTION_ID, TRANSFER_B, RISK_TO, TRANSFER_RISKS, TERM_ID,
 *                                             SHOW_ALL_OR_SHOW_TERM, ERROR_MSG.
 * ---------------------------------------------------
 */
public class DividendFields {
    public static final String DIVIDEND_RULE_ID = "dividendRuleId";
    public static final String DIVIDEND_EVENT_ID = "dividendEventId";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String ISSUE_COMPANY_ENTITY_ID = "issueCompanyEntityId";
    public static final String ISSUE_STATE_CODE = "issueStateCode";
    public static final String POLICY_TYPE_CODE = "policyTypeCode";
    public static final String POLICY_TERM_TYPE_CODE = "policyTermTypeCode";
    public static final String POLICY_TYPE = "policyType";
    public static final String POLICY_ID = "policyId";
    public static final String ACCOUNTING_DATE = "accountingDate";
    public static final String PERCENTAGE = "percentage";
    public static final String CANCELB = "cancelB";
    public static final String ROWNUM = "rowNum";
    public static final String IS_PRINT_AVAILABLE = "isPrintAvailable";
    public static final String IS_POST_AVAILABLE = "isPostAvailable";
    public static final String IS_DIVIDEND_EDITABLE = "isDividendEditable";
    public static final String IS_DELETE_AVAILABLE = "isDeleteAvailable";
    public static final String IS_SELECT_AVAILABLE = "isSelectAvailable";
    public static final String IS_PROCESS_AVAILABLE = "isProcessAvailable";
    public static final String IS_FIRST_LOADED = "isFirstLoaded";

    public static final String TYPE_CODE_NON_COMMON = "NON_COMMON";
    public static final String TYPE_CODE_COMMON = "COMMON";

    public static final String RET_ID = "retId";
    public static final String RET_CODE = "retCode";
    public static final String RET_MSG = "retMsg";

    public static final String DIVIDEND_AMOUNT = "dividendAmount";
    public static final String STATUS = "status";
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_PROCESSED = "Processed";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String TRANSFER_B = "transferB";
    public static final String RISK_TO = "riskTo";
    public static final String TRANSFER_DIVIDENDS = "transferAuditDividendList";
    public static final String TERM_ID = "termId";
    public static final String SHOW_ALL_OR_SHOW_TERM = "showTermOrAll";
    public static final String ERROR_MSG = "errorMsg";

    public static void setDividendEventId(Record record, String dividendEventId) {
        record.setFieldValue(DIVIDEND_EVENT_ID, dividendEventId);
    }

    public static void setDividendRuleId(Record record, String dividendRuleId) {
        record.setFieldValue(DIVIDEND_RULE_ID, dividendRuleId);
    }

    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, effectiveFromDate);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE, effectiveToDate);
    }

    public static void setIssueCompanyEntityId(Record record, String issueCompanyEntityId) {
        record.setFieldValue(ISSUE_COMPANY_ENTITY_ID, issueCompanyEntityId);
    }

    public static void setIssueStateCode(Record record, String issueStateCode) {
        record.setFieldValue(ISSUE_STATE_CODE, issueStateCode);
    }

    public static void setPolicyTypeCode(Record record, String policyTypeCode) {
        record.setFieldValue(POLICY_TYPE_CODE, policyTypeCode);
    }

    public static void setPolicyTermTypeCode(Record record, String policyTermTypeCode) {
        record.setFieldValue(POLICY_TERM_TYPE_CODE, policyTermTypeCode);
    }

    public static void setPolicyType(Record record, String policyType) {
        record.setFieldValue(POLICY_TYPE, policyType);
    }

    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID, policyId);
    }

    public static void setAccountingDate(Record record, String accountingDate) {
        record.setFieldValue(ACCOUNTING_DATE, accountingDate);
    }

    public static void setPercentage(Record record, String percentage) {
        record.setFieldValue(PERCENTAGE, percentage);
    }

    public static void setCancelB(Record record, String cancelB) {
        record.setFieldValue(CANCELB, cancelB);
    }

    public static void setIsPrintAvailable(Record record, YesNoFlag isPrintAvailable) {
        record.setFieldValue(IS_PRINT_AVAILABLE, isPrintAvailable);
    }

    public static void setIsPostAvailable(Record record, YesNoFlag isPostAvailable) {
        record.setFieldValue(IS_POST_AVAILABLE, isPostAvailable);
    }

    public static void setIsDividendEditable(Record record, YesNoFlag isDividendEditable) {
        record.setFieldValue(IS_DIVIDEND_EDITABLE, isDividendEditable);
    }

    public static void setIsDeleteAvailable(Record record, YesNoFlag isDeleteAvailable) {
        record.setFieldValue(IS_DELETE_AVAILABLE, isDeleteAvailable);
    }

    public static void setIsSelectAvailable(Record record, YesNoFlag isSelectAvailable) {
        record.setFieldValue(IS_SELECT_AVAILABLE, isSelectAvailable);
    }

    public static void setIsProcessAvailable(Record record, YesNoFlag isProcessAvailable) {
        record.setFieldValue(IS_PROCESS_AVAILABLE, isProcessAvailable);
    }

    public static void setIsFirstLoaded(Record record, YesNoFlag isFirstLoaded) {
        record.setFieldValue(IS_FIRST_LOADED, isFirstLoaded);
    }

    public static void setRetId(Record record, Long retId) {
        record.setFieldValue(RET_ID, retId);
    }

    public static void setRetCode(Record record, Long retCode) {
        record.setFieldValue(RET_CODE, retCode);
    }

    public static void setRetMsg(Record record, String retMsg) {
        record.setFieldValue(RET_MSG, retMsg);
    }

    public static void setStartDate(Record record, String startDate) {
        record.setFieldValue(START_DATE, startDate);
    }

    public static void setEndDate(Record record, String endDate) {
        record.setFieldValue(END_DATE, endDate);
    }

    public static void setTransactionId(Record record, String transactionId) {
        record.setFieldValue(TRANSACTION_ID, transactionId);
    }

    public static void setTransferB(Record record, String transferB) {
        record.setFieldValue(TRANSFER_B, transferB);
    }

    public static void setRiskTo(Record record, String riskTo) {
        record.setFieldValue(RISK_TO, riskTo);
    }

    public static void setTransferDividends(Record record, String transferDividends) {
        record.setFieldValue(TRANSFER_DIVIDENDS, transferDividends);
    }

    public static void setTermId(Record record, String termId) {
        record.setFieldValue(TERM_ID, termId);
    }

    public static void setShowAllOrShowTerm(Record record, String showAllOrTerm) {
        record.setFieldValue(SHOW_ALL_OR_SHOW_TERM, showAllOrTerm);
    }

    public static void setErrorMsg(Record record, String errorMsg) {
        record.setFieldValue(ERROR_MSG, errorMsg);
    }

    public static String getDividendRuleId(Record record) {
        return record.getStringValue(DIVIDEND_RULE_ID);
    }

    public static String getDividendEventId(Record record) {
        return record.getStringValue(DIVIDEND_EVENT_ID);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static String getIssueCompanyEntityId(Record record) {
        return record.getStringValue(ISSUE_COMPANY_ENTITY_ID);
    }

    public static String getIssueStateCode(Record record) {
        return record.getStringValue(ISSUE_STATE_CODE);
    }

    public static String getPolicyTypeCode(Record record) {
        return record.getStringValue(POLICY_TYPE_CODE);
    }

    public static String getPolicyTermTypeCode(Record record) {
        return record.getStringValue(POLICY_TERM_TYPE_CODE);
    }

    public static String getPolicyType(Record record) {
        return record.getStringValue(POLICY_TYPE);
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static String getAccountingDate(Record record) {
        return record.getStringValue(ACCOUNTING_DATE);
    }

    public static String getPercentage(Record record) {
        return record.getStringValue(PERCENTAGE);
    }

    public static String getCancelB(Record record) {
        return record.getStringValue(CANCELB);
    }

    public static YesNoFlag getIsPrintAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_PRINT_AVAILABLE));
    }

    public static YesNoFlag getIsPostAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_POST_AVAILABLE));
    }

    public static YesNoFlag getIsDividendEditable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_DIVIDEND_EDITABLE));
    }

    public static YesNoFlag getIsDeleteAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_DELETE_AVAILABLE));
    }

    public static YesNoFlag getIsSelectAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_SELECT_AVAILABLE));
    }

    public static YesNoFlag getIsProcessAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_PROCESS_AVAILABLE));
    }

    public static YesNoFlag getIsFirstLoaded(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_FIRST_LOADED));
    }

    public static Long getRetId(Record record) {
        return record.getLongValue(RET_ID);
    }

    public static Long getRetCode(Record record) {
        return record.getLongValue(RET_CODE);
    }

    public static String getRetMsg(Record record) {
        return record.getStringValue(RET_MSG);
    }

    public static String getDividendAmount(Record record) {
        return record.getStringValue(DIVIDEND_AMOUNT);
    }

    public static String getStatus(Record record) {
        return record.getStringValue(STATUS);
    }

    public static String getStartDate(Record record) {
        return record.getStringValue(START_DATE);
    }

    public static String getEndDate(Record record) {
        return record.getStringValue(END_DATE);
    }

    public static String getTransactionId(Record record) {
        return record.getStringValue(TRANSACTION_ID);
    }

    public static String getTransferB(Record record) {
        return record.getStringValue(TRANSFER_B);
    }

    public static String getRiskTo(Record record) {
        return record.getStringValue(RISK_TO);
    }

    public static String getTransferDividends(Record record) {
        return record.getStringValue(TRANSFER_DIVIDENDS);
    }

    public static String getTermId(Record record) {
        return record.getStringValue(TERM_ID);
    }

    public static String getShowAllOrShowTerm(Record record) {
        return record.getStringValue(SHOW_ALL_OR_SHOW_TERM);
    }

    public static String getErrorMsg(Record record) {
        return record.getStringValue(ERROR_MSG);
    }

    public class DividendCodeValues {
        public static final String ALL = "A";
        public static final String TERM = "T";
    }

}
