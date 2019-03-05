package dti.pm.policymgr.specialhandlingmgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 12, 2007
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
public class SpecialHandlingFields {
    public static final String POL_SPECIALHANDLING_ID = "polSpecialHandlingId";
    public static final String POL_SPECHAND_ID = "polSpecHandId";
    public static final String TRANSACTION_LOG_ID = "transactionLogId";
    public static final String EFFCTIVE_TO_DATE = "effectiveToDate";
    public static final String EFFCTIVE_FROM_DATE = "effectiveFromDate";
    public static final String RENEWAL_B = "renewalB";
    public static final String SPECIALHANDLING_CODE = "specialHandlingCode";
    public static final String ORIG_EFFCTIVE_FROM_DATE = "origEffectiveFromDate";
    public static final String ORIG_EFFCTIVE_TO_DATE = "origEffectiveToDate";

    public static String getOrigEffectiveFromDate(Record record) {
        return record.getStringValue(ORIG_EFFCTIVE_FROM_DATE);
    }

    public static String getOrigEffectiveToDate(Record record) {
        return record.getStringValue(ORIG_EFFCTIVE_TO_DATE);
    }

    public static String getPolSpecialHandlingId(Record record) {
      return record.getStringValue(POL_SPECIALHANDLING_ID);
    }

    public static void setPolSpecialHandlingId(Record record, String polSpecialHandlingId) {
      record.setFieldValue(POL_SPECIALHANDLING_ID, polSpecialHandlingId);
    }
    public static String getPolSpecHandId(Record record) {
      return record.getStringValue(POL_SPECHAND_ID);
    }

    public static void setPolSpecHandId(Record record, String polSpecHandId) {
      record.setFieldValue(POL_SPECHAND_ID, polSpecHandId);
    }
    public static String getTransactionLogId(Record record) {
      return record.getStringValue(TRANSACTION_LOG_ID);
    }

    public static void setTransactionLogId(Record record, String transactionLogId) {
      record.setFieldValue(TRANSACTION_LOG_ID, transactionLogId);
    }
    public static String getEffectiveToDate(Record record) {
      return record.getStringValue(EFFCTIVE_TO_DATE);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
      record.setFieldValue(EFFCTIVE_TO_DATE, effectiveToDate);
    }
    public static String getEffectiveFromDate(Record record) {
      return record.getStringValue(EFFCTIVE_FROM_DATE);
    }

    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
      record.setFieldValue(EFFCTIVE_FROM_DATE, effectiveFromDate);
    }

    public static YesNoFlag getRenewalB(Record record) {
      return YesNoFlag.getInstance(record.getStringValue(RENEWAL_B));
    }

    public static void setRenewalB(Record record, YesNoFlag renewal) {
      record.setFieldValue(RENEWAL_B, renewal.getName());
    }

    public static String getSpecialHandlingCode(Record record) {
      return record.getStringValue(SPECIALHANDLING_CODE);
    }

    public static void setSpecialHandlingCode(Record record, String specialHandlingCode) {
      record.setFieldValue(SPECIALHANDLING_CODE, specialHandlingCode);
    }


}
