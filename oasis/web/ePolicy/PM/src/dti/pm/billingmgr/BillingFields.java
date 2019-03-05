package dti.pm.billingmgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 20, 2014
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/28/2018      fhuang       193406 - add MINIMAL and EXCLUDEPARTY viewname
 * ---------------------------------------------------
 */

public class BillingFields {

    public static final String ACCOUNT_NO = "accountNo";
    public static final String ACCOUNT_TYPE = "accountType";
    public static final String BILL_LEAD_DAYS = "billLeadDays";
    public static final String ISSUE_COMPANY_ENTITY_ID = "issCompEntityId";
    public static final String SINGLE_POLICY_B = "singlePolicyB";
    public static final String ACCOUNTING_MODE = "accountingMode";
    public static final String OVERDUE_RULE = "overdueRule";
    public static final String BILLING_ACCOUNT_ID = "billingAccountId";
    public static final String BASE_BILL_MONTH_DAY = "baseBillMonthDay";
    public static final String BILLING_FREQUENCY = "billingFrequency";
    public static final String ACCT_HOLDER_IS_POL_HOLDER_B = "acctHolderIsPolHolderB";
    public static final String NEXT_BILLING_DATE = "nextBillingDate";
    public static final String PAYMENT_PLAN_ID = "paymentPlanId";
    public static final String DERIVED_POLICY_NO = "derivedpolicyno";
    public static final String MINIMAL_VIEW = "MINIMAL";
    public static final String EXCLUDE_PARTY_VIEW = "EXCLUDEPARTY";

    //Set to record.
    public static void setAccountNo(Record record, String accountNo) {
        record.setFieldValue(ACCOUNT_NO, accountNo);
    }

    public static void setAccountType(Record record, String accountType) {
        record.setFieldValue(ACCOUNT_TYPE, accountType);
    }

    public static void setBillLeadDays(Record record, String billLeadDays) {
        record.setFieldValue(BILL_LEAD_DAYS, billLeadDays);
    }

    public static void setIssueCompanyEntityId(Record record, String issueCompanyEntityId) {
        record.setFieldValue(ISSUE_COMPANY_ENTITY_ID, issueCompanyEntityId);
    }

    public static void setSinglePolicyB(Record record, String singlePolicyB) {
        record.setFieldValue(SINGLE_POLICY_B, singlePolicyB);
    }

    public static void setAccountingMode(Record record, String accountingMode) {
        record.setFieldValue(ACCOUNTING_MODE, accountingMode);
    }

    public static void setOverdueRule(Record record, String overdueRule) {
        record.setFieldValue(OVERDUE_RULE, overdueRule);
    }

    public static void setBillingAccountId(Record record, String billingAcccountId) {
        record.setFieldValue(BILLING_ACCOUNT_ID, billingAcccountId);
    }

    public static void setBaseBillMonthDay(Record record, String baseBillMonthDay) {
        record.setFieldValue(BASE_BILL_MONTH_DAY, baseBillMonthDay);
    }

    public static void setBillingFrequency(Record record, String billingFrequency) {
        record.setFieldValue(BILLING_FREQUENCY, billingFrequency);
    }

    public static void setAcctHolderIsPolHolderB(Record record, String acctHolderIsPolHolderB) {
        record.setFieldValue(ACCT_HOLDER_IS_POL_HOLDER_B, acctHolderIsPolHolderB);
    }

    public static void setNextBillingDate(Record record, String nextBillingDate) {
        record.setFieldValue(NEXT_BILLING_DATE, nextBillingDate);
    }
    
    public static void setPaymentPlanId(Record record, String paymentPlanId) {
        record.setFieldValue(PAYMENT_PLAN_ID, paymentPlanId);
    }
    
    public static void setDerivedPolicyNo(Record record, String derivedPolicyNo) {
        record.setFieldValue(DERIVED_POLICY_NO, derivedPolicyNo);
    }

    //Get from record
    public static String getAccountNo(Record record) {
        return record.getStringValue(ACCOUNT_NO);
    }

    public static String getAccountType(Record record) {
        return record.getStringValue(ACCOUNT_TYPE);
    }

    public static String getBillLeadDays(Record record) {
        return record.getStringValue(BILL_LEAD_DAYS);
    }

    public static String getIssueCompanyEntityId(Record record) {
        return record.getStringValue(ISSUE_COMPANY_ENTITY_ID);
    }

    public static String getSinglePolicyB(Record record) {
        return record.getStringValue(SINGLE_POLICY_B);
    }

    public static String getAccountingMode(Record record) {
        return record.getStringValue(ACCOUNTING_MODE);
    }

    public static String getOverdueRule(Record record) {
        return record.getStringValue(OVERDUE_RULE);
    }

    public static String getBillingAccountId(Record record) {
        return record.getStringValue(BILLING_ACCOUNT_ID);
    }

    public static String getBaseBillMonthDay(Record record) {
        return record.getStringValue(BASE_BILL_MONTH_DAY);
    }

    public static String getBillingFrequency(Record record) {
        return record.getStringValue(BILLING_FREQUENCY);
    }

    public static String getAcctHolderIsPolHolderB(Record record) {
        return record.getStringValue(ACCT_HOLDER_IS_POL_HOLDER_B);
    }

    public static String getNextBillingDate(Record record) {
        return record.getStringValue(NEXT_BILLING_DATE);
    }
    
    public static String getPaymentPlanId(Record record) {
        return record.getStringValue(PAYMENT_PLAN_ID);
    }
    
    public static String getDerivedPolicyNo(Record record) {
        return record.getStringValue(DERIVED_POLICY_NO);
    }

}
