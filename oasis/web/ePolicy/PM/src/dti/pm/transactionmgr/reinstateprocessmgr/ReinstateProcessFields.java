package dti.pm.transactionmgr.reinstateprocessmgr;

import dti.oasis.recordset.Record;


/**
 * Constants for reinstate process.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 18, 2007
 *
 * @author Jerry
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ReinstateProcessFields {

    public static final String REINSTATE_LEVEL = "reinstateLevel";
    public static final String BASE_ID = "baseId";
    public static final String STATUS_TYPE = "statusType";
    public static final String ITEM_EFF_DATE = "itemEffDate";
    public static final String ITEM_TO_DATE = "itemToDate";
    public static final String COMMENTS = "comments";
    public static final String ACCT_DT = "acctDt";

    public static String getReinstateLevel(Record record) {
        return record.getStringValue(REINSTATE_LEVEL);
    }

    public static void setReinstateLevel(Record record, String reinstateLevel) {
        record.setFieldValue(REINSTATE_LEVEL, reinstateLevel);
    }

    public static String getBaseID(Record record) {
        return record.getStringValue(BASE_ID);
    }

    public static void setBaseID(Record record, String baseId) {
        record.setFieldValue(BASE_ID, baseId);
    }

    public static String getStatusType(Record record) {
        return record.getStringValue(STATUS_TYPE);
    }

    public static void setStatusType(Record record, String status) {
        record.setFieldValue(STATUS_TYPE, status);
    }

    public static String getItemEffDate(Record record) {
        return record.getStringValue(ITEM_EFF_DATE);
    }

    public static void setItemEffDate(Record record, String itemEffDate) {
        record.setFieldValue(ITEM_EFF_DATE, itemEffDate);
    }

    public static String getItemtoDate(Record record) {
        return record.getStringValue(ITEM_TO_DATE);
    }

    public static void setItemtoDate(Record record, String itemtoDate) {
        record.setFieldValue(ITEM_TO_DATE, itemtoDate);
    }

    public static String getComments(Record record) {
        return record.getStringValue(COMMENTS);
    }

    public static void setComments(Record record, String comments) {
        record.setFieldValue(COMMENTS, comments);
    }

    public static String getAcctdt(Record record) {
        return record.getStringValue(ACCT_DT);
    }

    public static void setAcctdt(Record record, String acctdt) {
        record.setFieldValue(ACCT_DT, acctdt);
    }


}