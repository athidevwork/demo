package dti.pm.policymgr;

import dti.oasis.recordset.Record;

/**
 * constants for deny quote action
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 23, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/17/2010       MSN         1) Added deny comments field
 * ---------------------------------------------------
 */

public class DenyQuoteFields {
    public static final String DENY_EFF_DATE = "denyEffDate";
    public static final String DENY_REASON_CODE = "denyReasonCode";
    public static final String DENY_COMMENTS = "comments";

    public static String getDenyEffDate(Record record) {
        return record.getStringValue(DENY_EFF_DATE);
    }

    public static void setDenyEffDate(Record record, String denyEffDate) {
        record.setFieldValue(DENY_EFF_DATE, denyEffDate);
    }

    public static String getDenyReasonCode(Record record) {
        return record.getStringValue(DENY_REASON_CODE);
    }

    public static void setDenyReasonCode(Record record, String denyReasonCode) {
        record.setFieldValue(DENY_REASON_CODE, denyReasonCode);
    }
  }
