package dti.pm.policymgr.quickpaymgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 5, 2012
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class QuickPayFields {
    public static final String POL_NO = "polNo";
    public static final String POL_HOLDER_NAME = "polHolderName";
    public static final String POLICY_HOLDER_NAME = "policyHolderName";

    public static String getPolNo(Record record) {
        return record.getStringValue(POL_NO);
    }

    public static void setPolNo(Record record, String policyNo) {
        record.setFieldValue(POL_NO, policyNo);
    }

    public static String getPolHolderName(Record record) {
        return record.getStringValue(POL_HOLDER_NAME);
    }

    public static void setPolHolderName(Record record, String policyHolderName) {
        record.setFieldValue(POL_HOLDER_NAME, policyHolderName);
    }

    public static String getPolicyHolderName(Record record) {
        return record.getStringValue(POLICY_HOLDER_NAME);
    }
}
