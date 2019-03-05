package dti.pm.policymgr.tailquotemgr;

import dti.oasis.recordset.Record;

/**
 * Constants for Tail Quotes.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 31, 2008
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
public class TailQuoteFields {

    public static String getTailQuoteDate(Record record) {
        return record.getStringValue(TAIL_QUOTE_DATE);
    }

    public static void setTailQuoteDate(Record record, String tailQuoteDate) {
        record.setFieldValue(TAIL_QUOTE_DATE, tailQuoteDate);
    }

    public static String getTransactionLogId(Record record) {
      return record.getStringValue(TRANSACTION_LOG_ID);
    }

    public static void setTransactionLogId(Record record, String transactionLogId) {
      record.setFieldValue(TRANSACTION_LOG_ID, transactionLogId);
    }


    public static final String TRANSACTION_LOG_ID = "transactionLogId";
    public static final String TAIL_QUOTE_DATE = "tailQuoteDate";
}
