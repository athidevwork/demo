package dti.pm.policymgr.mailingmgr;

import dti.oasis.recordset.Record;

/**
 * Constant field class for mailing event
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
public class MailingEventFields {
    public static final String POLICY_MAILING_ID = "policyMailingId";
    public static final String PRODUCT_MAILING_ID = "productMailingId";
    public static final String GENERATE_DATE = "generateDate";

    public static void setPolicyMailingId(Record record, String policyMailingId) {
        record.setFieldValue(POLICY_MAILING_ID, policyMailingId);
    }

    public static String getPolicyMailingId(Record record) {
        return record.getStringValue(POLICY_MAILING_ID);
    }

    public static void setProductMailingId(Record record, String productMailingId) {
        record.setFieldValue(PRODUCT_MAILING_ID, productMailingId);
    }

    public static String getProductMailingId(Record record) {
        return record.getStringValue(PRODUCT_MAILING_ID);
    }

    public static void setGenerateDate(Record record, String generateDate) {
        record.setFieldValue(GENERATE_DATE, generateDate);
    }

    public static String getGenerateDate(Record record) {
        return record.getStringValue(GENERATE_DATE);
    }
}
