package dti.pm.policymgr.limitsharingmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Constant field class for reinsurance
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dev 29, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 1        11/11/09 GCHITTA    Issue 100140 - Modified to add renewal indicator
 * 01/28/2011       dzhang      Issue 113568 - Added policyId, transLogId, effDate and expDate.
 * 11/10/2011       xnie        125517 - Added fields and get/set methods.
 * 06/05/2013       adeng       144779 - Added field SHARE_DTL_CLOS_TRANS_LOG_ID and get/set methods.
 * ---------------------------------------------------
 */
public class SharedDetailFields {
  	public static final String POLICY_SHARE_GROUP_DTL_ID = "policyShareGroupDtlId";
	public static final String SHARE_DTL_EFF_FROM_DATE = "shareDtlEffFromDate";
	public static final String SHARE_DTL_EFF_TO_DATE = "shareDtlEffToDate";
	public static final String SHARE_DTL_TRANS_LOG_ID = "shareDtlTransLogId";
	public static final String SHARE_DTL_ACCT_FROM_DATE = "shareDtlAcctFromDate";
	public static final String SHARE_DTL_SOURCE_RECORD_ID = "shareDtlSourceRecordId";
	public static final String SHARE_DTL_SOURCE_TABLE_NAME = "shareDtlSourceTableName";
	public static final String SHARE_DTL_RECORD_MODE_CODE = "shareDtlRecordModeCode";
    public static final String SHARE_DTL_GROUP_MASTER_ID = "shareDtlGroupMasterId";
	public static final String SHARE_DTL_RISK_NAME = "shareDtlRiskName";
	public static final String SHARE_DTL_COVERAGE_SHORT_DESC = "shareDtlCoverageShortDesc";
    public static final String SHARE_DTL_OWNER_B = "shareDtlOwnerB";
	public static final String SHARE_DTL_SHARED_LIMIT_B = "shareDtlSharedLimitB";
    public static final String SHARE_DTL_RENEWAL_B = "shareDtlRenewalB";
    public static final String POLICY_ID = "policyId";
    public static final String TRANS_LOG_ID = "transLogId";
    public static final String EFF_DATE = "effDate";
    public static final String EXP_DATE = "expDate";
    public static final String SELECT_IND = "SELECT_IND";
    public static final String SHARE_LIMIT_B = "isCovgSharedLimit";
    public static final String SHARE_DEDUCT_B = "isCovgSharedDeductible";
    public static final String SHARE_SIR_B = "isCovgSharedSir";
    public static final String SHARE_OWNER_B = "isCovgSharedOwner";
    public static final String SHARE_DTL_CLOS_TRANS_LOG_ID = "shareDtlClosTransLogId";

    public static void setPolicyShareGroupDtlId(Record record, String policyShareGroupDtlId) {
		record.setFieldValue(POLICY_SHARE_GROUP_DTL_ID,policyShareGroupDtlId);
	}
	public static String getPolicyShareGroupDtlId(Record record){
		return record.getStringValue(POLICY_SHARE_GROUP_DTL_ID);
	}
	public static void setShareDtlEffFromDate(Record record, String shareDtlEffFromDate) {
		record.setFieldValue(SHARE_DTL_EFF_FROM_DATE,shareDtlEffFromDate);
	}
	public static String getShareDtlEffFromDate(Record record){
		return record.getStringValue(SHARE_DTL_EFF_FROM_DATE);
	}
	public static void setShareDtlEffToDate(Record record, String shareDtlEffToDate) {
		record.setFieldValue(SHARE_DTL_EFF_TO_DATE,shareDtlEffToDate);
	}
	public static String getShareDtlEffToDate(Record record){
		return record.getStringValue(SHARE_DTL_EFF_TO_DATE);
	}
	public static void setShareDtlTransLogId(Record record, String shareDtlTransLogId) {
		record.setFieldValue(SHARE_DTL_TRANS_LOG_ID,shareDtlTransLogId);
	}
	public static String getShareDtlTransLogId(Record record){
		return record.getStringValue(SHARE_DTL_TRANS_LOG_ID);
	}
	public static void setShareDtlAcctFromDate(Record record, String shareDtlAcctFromDate) {
		record.setFieldValue(SHARE_DTL_ACCT_FROM_DATE,shareDtlAcctFromDate);
	}
	public static String getShareDtlAcctFromDate(Record record){
		return record.getStringValue(SHARE_DTL_ACCT_FROM_DATE);
	}
	public static void setShareDtlSourceRecordId(Record record, String shareDtlSourceRecordId) {
		record.setFieldValue(SHARE_DTL_SOURCE_RECORD_ID,shareDtlSourceRecordId);
	}
	public static String getShareDtlSourceRecordId(Record record){
		return record.getStringValue(SHARE_DTL_SOURCE_RECORD_ID);
	}
	public static void setShareDtlSourceTableName(Record record, String shareDtlSourceTableName) {
		record.setFieldValue(SHARE_DTL_SOURCE_TABLE_NAME,shareDtlSourceTableName);
	}
	public static String getShareDtlSourceTableName(Record record){
		return record.getStringValue(SHARE_DTL_SOURCE_TABLE_NAME);
	}
	public static void setShareDtlRecordModeCode(Record record, String shareDtlRecordModeCode) {
		record.setFieldValue(SHARE_DTL_RECORD_MODE_CODE,shareDtlRecordModeCode);
	}
	public static String getShareDtlRecordModeCode(Record record){
		return record.getStringValue(SHARE_DTL_RECORD_MODE_CODE);
	}
    	public static void setShareDtlGroupMasterId(Record record, String shareDtlGroupMasterId) {
		record.setFieldValue(SHARE_DTL_GROUP_MASTER_ID,shareDtlGroupMasterId);
	}
	public static String getShareDtlGroupMasterId(Record record){
		return record.getStringValue(SHARE_DTL_GROUP_MASTER_ID);
	}
	public static void setShareDtlRiskName(Record record, String shareDtlRiskName) {
		record.setFieldValue(SHARE_DTL_RISK_NAME,shareDtlRiskName);
	}
	public static String getShareDtlRiskName(Record record){
		return record.getStringValue(SHARE_DTL_RISK_NAME);
	}
	public static void setShareDtlCoverageShortDesc(Record record, String shareDtlCoverageShortDesc) {
		record.setFieldValue(SHARE_DTL_COVERAGE_SHORT_DESC,shareDtlCoverageShortDesc);
	}
	public static String getShareDtlCoverageShortDesc(Record record){
		return record.getStringValue(SHARE_DTL_COVERAGE_SHORT_DESC);
	}
    public static void setShareDtlOwnerB(Record record, String shareDtlOwnerB) {
		record.setFieldValue(SHARE_DTL_OWNER_B,shareDtlOwnerB);
	}
	public static String getShareDtlOwnerB(Record record){
		return record.getStringValue(SHARE_DTL_OWNER_B);
	}
	public static void setShareDtlSharedLimitB(Record record, String shareDtlSharedLimitB) {
		record.setFieldValue(SHARE_DTL_SHARED_LIMIT_B,shareDtlSharedLimitB);
	}
	public static String getShareDtlSharedLimitB(Record record){
		return record.getStringValue(SHARE_DTL_SHARED_LIMIT_B);
	}

    public static String getShareDtlRenewalB(Record record){
		return record.getStringValue(SHARE_DTL_RENEWAL_B);
	}

	public static void setShareDtlRenewalB(Record record, String shareDetailRenewalB) {
		record.setFieldValue(SHARE_DTL_RENEWAL_B,shareDetailRenewalB);
	}

    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID,policyId);
    }

    public static String getTransLogId(Record record) {
        return record.getStringValue(TRANS_LOG_ID);
    }

    public static void setTransLogId(Record record, String transLogId) {
        record.setFieldValue(TRANS_LOG_ID, transLogId);
    }

    public static String getEffDate(Record record) {
        return record.getStringValue(EFF_DATE);
    }

    public static void setEffDate(Record record, String effDate) {
        record.setFieldValue(EFF_DATE, effDate);
    }

    public static String getExpDate(Record record) {
        return record.getStringValue(EXP_DATE);
    }

    public static void setExpDate(Record record, String expDate) {
        record.setFieldValue(EXP_DATE, expDate);
    }

    public static void setSelectId(Record record, String selectId ) {
        record.setFieldValue(SELECT_IND,selectId);
    }

    public static YesNoFlag getShareLimitB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(SHARE_LIMIT_B));
    }

    public static YesNoFlag getShareDeductB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(SHARE_DEDUCT_B));
    }

    public static YesNoFlag getShareSirB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(SHARE_SIR_B));
    }
    
    public static YesNoFlag getShareOwnerB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(SHARE_OWNER_B));
    }

    public static void setShareDtlClosTransLogId(Record record, String shareDtlClosTransLogId) {
        record.setFieldValue(SHARE_DTL_CLOS_TRANS_LOG_ID,shareDtlClosTransLogId);
    }
    public static String getShareDtlClosTransLogId(Record record){
        return record.getStringValue(SHARE_DTL_CLOS_TRANS_LOG_ID);
    }
}
