package dti.pm.transactionmgr.renewalprocessmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Fields for Rnewal.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class RenewalProcessFields {

    public static final String ENDORSE_CODE = "endorseCode";
    public static final String APPLY_PRT_B = "applyPrtB";

    public static final String RENEWAL_TERM_EFF_DATE = "renewalTermEffDate";
    public static final String RENEWAL_TERM_EXP_DATE = "renewalTermExpDate";


    public static String getEndorseCode(Record record) {
        return record.getStringValue(ENDORSE_CODE);
    }

    public static void setEndorseCode(Record record, String endorseCode) {
        record.setFieldValue(ENDORSE_CODE, endorseCode);
    }

    public static YesNoFlag getApplyPrtB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(APPLY_PRT_B));
    }

    public static void setApplyPrtB(Record record, YesNoFlag applyPrtB) {
        record.setFieldValue(APPLY_PRT_B, applyPrtB);
    }

    public static String getRenewalTermEffDate(Record record) {
        return record.getStringValue(RENEWAL_TERM_EFF_DATE);
    }

    public static void setRenewalTermEffDate(Record record, String renewalTermEffDate) {
        record.setFieldValue(RENEWAL_TERM_EFF_DATE, renewalTermEffDate);
    }

    public static String getRenewalTermExpDate(Record record) {
        return record.getStringValue(RENEWAL_TERM_EXP_DATE);
    }

    public static void setRenewalTermExpDate(Record record, String renewalTermExpDate) {
        record.setFieldValue(RENEWAL_TERM_EXP_DATE, renewalTermExpDate);
    }

}
