package dti.pm.policymgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.pm.busobjs.PolicyCycleCode;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 22, 2007
 *
 * @author Sharon Ma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/07/2007       sxm         removed setFields()
 * 01/21/2011       wfu         113566 - Added new fields to handle copying policy from risk
 *                                       according to code review result.
 * ---------------------------------------------------
 */
public class CreatePolicyFields {
    public static final String REQUEST_CONTEXT = "requestContext";
    public static final String POLICY_HOLDER_NAME_ENTITY_ID = "policyHolderNameEntityId";
    public static final String POLICY_HOLDER_ENTITY_TYPE = "policyHolderEntityType";
    public static final String ISSUE_COMPANY_ENTITY_ID = "issueCompanyEntityId";
    public static final String ISSUE_STATE_CODE = "issueStateCode";
    public static final String TERM_EFFECTIVE_FROM_DATE = "termEffectiveFromDate";
    public static final String TERM_EFFECTIVE_TO_DATE = "termEffectiveToDate";
    public static final String IS_TERM_EFFECTIVE_TO_DATE_CHANGED = "isTermEffectiveToDateChanged";
    public static final String ACCOUNTTING_DATE = "accountingDate";
    public static final String USER_TRANSACTION_CODE = "userTransactionCode";
    public static final String IS_USER_TRANSACTION_CODE_AVAILABLE = "isUserTransactionCodeAvailable";
    public static final String PRACTICE_STATE_CODE = "practiceStateCode";
    public static final String REGIONAL_OFFICE = "regionalOffice";
    public static final String SHORT_RATE_B = "shortRateB";
    public static final String POLICY_TYPE_CODE = "policyTypeCode";
    public static final String TERM_TYPE_CODE = "termTypeCode";
    public static final String POLICY_CYCLE_CODE = "policyCycleCode";
    public static final String POLICY_CYCLE = "policyCycle";
    public static final String POLICY_ID = "policyId";
    public static final String RISK_BASE_ID = "riskBaseId";
    public static final String ADDL_PARMS = "addlParms";
    public static final String POL_NO = "polNo";
    public static final String NEW_POL_NO = "newPolNo";
    public static final String EXISTING_POL_B = "existingPolB";
    public static final String FROM_CYCLE = "fromCycle";
    public static final String TO_CYCLE = "toCycle";
    public static final String QUOTE_TRANSACTION_CODE = "quoteTransactionCode";
    public static final String COVERAGE_LIST = "coverageList";
    public static final String RETROACTIVE_DATE = "retroactiveDate";
    public static final String DUMMY_STATE = "dummyState";

    public static String getRequestContext(Record record) {
        return record.getStringValue(REQUEST_CONTEXT);
    }

    public static String getPolicyHolderNameEntityId(Record record) {
        return record.getStringValue(POLICY_HOLDER_NAME_ENTITY_ID);
    }

    public static String getPolicyHolderEntityType(Record record) {
        return record.getStringValue(POLICY_HOLDER_ENTITY_TYPE);
    }

    public static String getIssueCompanyEntityId(Record record) {
        return record.getStringValue(ISSUE_COMPANY_ENTITY_ID);
    }

    public static String getIssueStateCode(Record record) {
        return record.getStringValue(ISSUE_STATE_CODE);
    }

    public static String getTermEffectiveFromDate(Record record) {
        return record.getStringValue(TERM_EFFECTIVE_FROM_DATE);
    }

    public static String getTermEffectiveToDate(Record record) {
        return record.getStringValue(TERM_EFFECTIVE_TO_DATE);
    }

    public static YesNoFlag getIsTermEffectiveToDateChanged(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_TERM_EFFECTIVE_TO_DATE_CHANGED));
    }

    public static String getAccountingDate(Record record) {
        return record.getStringValue(ACCOUNTTING_DATE);
    }

    public static String getUserTransactionCode(Record record) {
        return record.getStringValue(USER_TRANSACTION_CODE);
    }

    public static YesNoFlag getIsUserTransactionCodeAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_USER_TRANSACTION_CODE_AVAILABLE));
    }

    public static String getPracticeStateCode(Record record) {
        return record.getStringValue(PRACTICE_STATE_CODE);
    }

    public static String getRegionalOffice(Record record) {
        return record.getStringValue(REGIONAL_OFFICE);
    }

    public static YesNoFlag getShortRateB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(SHORT_RATE_B));
    }

    public static String getPolicyTypeCode(Record record) {
        return record.getStringValue(POLICY_TYPE_CODE);
    }

    public static String getTermTypeCode(Record record) {
        return record.getStringValue(TERM_TYPE_CODE);
    }

    public static PolicyCycleCode getPolicyCycleCode(Record record) {
        return (PolicyCycleCode) record.getFieldValue(POLICY_CYCLE_CODE);
    }

    public static String getPolicyCycle(Record record) {
        return record.getStringValue(POLICY_CYCLE);
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static String getRiskBaseId(Record record) {
        return record.getStringValue(RISK_BASE_ID);
    }

    public static String getAddlParms(Record record) {
        return record.getStringValue(ADDL_PARMS);
    }

    public static String getCoverageList(Record record) {
        return record.getStringValue(COVERAGE_LIST);
    }

    public static String getDummyState(Record record) {
        return record.getStringValue(DUMMY_STATE);
    }

    public static YesNoFlag getExistingPolB(Record record) {
        return (YesNoFlag) record.getFieldValue(EXISTING_POL_B);
    }

    public static PolicyCycleCode getFromCycle(Record record) {
        return (PolicyCycleCode) record.getFieldValue(FROM_CYCLE);
    }

    public static String getNewPolNo(Record record) {
        return record.getStringValue(NEW_POL_NO);
    }

    public static String getPolNo(Record record) {
        return record.getStringValue(POL_NO);
    }

    public static String getQuoteTransactionCode(Record record) {
        return record.getStringValue(QUOTE_TRANSACTION_CODE);
    }

    public static String getRetroactiveDate(Record record) {
        return record.getStringValue(RETROACTIVE_DATE);
    }

    public static PolicyCycleCode getToCycle(Record record) {
        return (PolicyCycleCode) record.getFieldValue(TO_CYCLE);
    }

    public static void setRequestContext(Record record, String requestContext) {
      record.setFieldValue(REQUEST_CONTEXT, requestContext);
    }

    public static void setPolicyHolderEntityType(Record record, String policyHolderEntityType) {
      record.setFieldValue(POLICY_HOLDER_ENTITY_TYPE, policyHolderEntityType);
    }

    public static void setPolicyHolderNameEntityId(Record record, String policyHolderNameEntityId) {
      record.setFieldValue(POLICY_HOLDER_NAME_ENTITY_ID, policyHolderNameEntityId);
    }

    public static void setIssueCompanyEntityId(Record record, String issueCompanyEntityId) {
      record.setFieldValue(ISSUE_COMPANY_ENTITY_ID, issueCompanyEntityId);
    }

    public static void setIssueStateCode(Record record, String issueStateCode) {
      record.setFieldValue(ISSUE_STATE_CODE, issueStateCode);
    }

    public static void setTermEffectiveFromDate(Record record, String termEffectiveFromDate) {
      record.setFieldValue(TERM_EFFECTIVE_FROM_DATE, termEffectiveFromDate);
    }

    public static void setTermEffectiveToDate(Record record, String termEffectiveToDate) {
      record.setFieldValue(TERM_EFFECTIVE_TO_DATE, termEffectiveToDate);
    }

    public static void setIsTermEffectiveToDateChanged(Record record, YesNoFlag isTermEffectiveToDateChanged) {
      record.setFieldValue(IS_TERM_EFFECTIVE_TO_DATE_CHANGED, isTermEffectiveToDateChanged);
    }

    public static void setAccountingDate(Record record, String accountingDate) {
      record.setFieldValue(ACCOUNTTING_DATE, accountingDate);
    }

    public static void setUserTransactionCode(Record record, String userTransactionCode) {
      record.setFieldValue(USER_TRANSACTION_CODE, userTransactionCode);
    }

    public static void setIsUserTransactionCodeAvailable(Record record, YesNoFlag isUserTransactionCodeAvailable) {
      record.setFieldValue(IS_USER_TRANSACTION_CODE_AVAILABLE, isUserTransactionCodeAvailable);
    }

    public static void setPracticeStateCode(Record record, String practiceStateCode) {
      record.setFieldValue(PRACTICE_STATE_CODE, practiceStateCode);
    }

    public static void setRegionalOffice(Record record, String regionalOffice) {
      record.setFieldValue(REGIONAL_OFFICE, regionalOffice);
    }

    public static void setShortRateB(Record record, YesNoFlag shortRateB) {
      record.setFieldValue(SHORT_RATE_B, shortRateB);
    }

    public static void setPolicyCycleCode(Record record, PolicyCycleCode policyCycleCode) {
      record.setFieldValue(POLICY_CYCLE_CODE, policyCycleCode);
    }
    
    public static void setPolicyCycle(Record record, String policyCycle) {
      record.setFieldValue(POLICY_CYCLE, policyCycle);
    }

    public static void setPolicyId(Record record, String policyId) {
      record.setFieldValue(POLICY_ID, policyId);
    }

    public static void setRiskBaseId(Record record, String riskBaseId) {
      record.setFieldValue(RISK_BASE_ID, riskBaseId);
    }

    public static void setAddlParms(Record record, String addlParms) {
      record.setFieldValue(ADDL_PARMS, addlParms);
    }

    public static void setPolNo(Record record, String polNo) {
      record.setFieldValue(POL_NO, polNo);
    }

    public static void setNewPolNo(Record record, String newPolNo) {
      record.setFieldValue(NEW_POL_NO, newPolNo);
    }

    public static void setExistingPolB(Record record, YesNoFlag existingPolB) {
      record.setFieldValue(EXISTING_POL_B, existingPolB);
    }

    public static void setFromCycle(Record record, PolicyCycleCode fromCycle) {
      record.setFieldValue(FROM_CYCLE, fromCycle);
    }

    public static void setToCycle(Record record, PolicyCycleCode toCycle) {
      record.setFieldValue(TO_CYCLE, toCycle);
    }

    public static void setQuoteTransactionCode(Record record, String quoteTransactionCode) {
      record.setFieldValue(QUOTE_TRANSACTION_CODE, quoteTransactionCode);
    }

    public static void setCoverageList(Record record, String coverageList) {
      record.setFieldValue(COVERAGE_LIST, coverageList);
    }

    public static void setRetroactiveDate(Record record, String retroactiveDate) {
      record.setFieldValue(RETROACTIVE_DATE, retroactiveDate);
    }

    public static void setDummyState(Record record, String dummyState) {
      record.setFieldValue(DUMMY_STATE, dummyState);
    }

    public class RequestContextValues {
        public static final String REQUEST_CONTEXT_PM = "PM";
    }
}
