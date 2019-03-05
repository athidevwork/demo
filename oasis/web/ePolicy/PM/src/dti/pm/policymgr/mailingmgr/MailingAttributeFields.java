package dti.pm.policymgr.mailingmgr;

import dti.oasis.recordset.Record;

/**
 * Constant field class for mailing attribute
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 20, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MailingAttributeFields {
   public static final String POLICY_MAILING_ID = "policyMailingId";
	public static final String POLICY_MAILING_RESEND_ID = "policyMailingResendId";
	public static final String RESEND_DAYS = "resendDays";
    public static final String RESEND_TYPE = "resendType";
    public static final String PRODUCT_MAILING_RESEND_ID = "productMailingResendId";
    public static final String RESEND_DATE = "resendDate";



    public static void setPolicyMailingId(Record record, String policyMailingId) {
		record.setFieldValue(POLICY_MAILING_ID,policyMailingId);
	}
	public static String getPolicyMailingId(Record record){
		return record.getStringValue(POLICY_MAILING_ID);
	}
	public static void setPolicyMailingResendId(Record record, String policyMailingResendId) {
		record.setFieldValue(POLICY_MAILING_RESEND_ID,policyMailingResendId);
	}
	public static String getPolicyMailingResendId(Record record){
		return record.getStringValue(POLICY_MAILING_RESEND_ID);
	}
	public static void setResendDays(Record record, String resendDays) {
		record.setFieldValue(RESEND_DAYS,resendDays);
	}
	public static String getResendDays(Record record){
		return record.getStringValue(RESEND_DAYS);
	}
    public static void setResendType(Record record, String resendType) {
		record.setFieldValue(RESEND_TYPE,resendType);
	}
	public static String getResendType(Record record){
		return record.getStringValue(RESEND_TYPE);
	}
    public static void setProductMailingResendId(Record record, String productMailingResendId) {
		record.setFieldValue(PRODUCT_MAILING_RESEND_ID,productMailingResendId);
	}
	public static String getProductMailingResendId(Record record){
		return record.getStringValue(PRODUCT_MAILING_RESEND_ID);
	}
    public static void setResendDate(Record record, String resendDate) {
		record.setFieldValue(RESEND_DATE,resendDate);
	}
	public static String getResendDate(Record record){
		return record.getStringValue(RESEND_DATE);
	}
}
