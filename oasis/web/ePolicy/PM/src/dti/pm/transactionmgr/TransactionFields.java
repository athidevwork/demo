package dti.pm.transactionmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.busobjs.TransactionStatus;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 22, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/08/2011       wfu         126582 - Added field transCode
 * 11/10/2011       xnie        125517 - Added transEff field and set method.
 * 10/18/2011       lmjiang     126975 - Add a hidden field 'transEffDate' for OBR rule.
 * 02/19/2013       jshen       141982 - Changed SHOW_ALL_OR_SHOW_TERM to showAllOrShowTerm. Added get/set methods for it.
 * 06/21/2013       adeng       117011 - Added transactionComment2&newTransactionComment2, and added getter/setter
 *                                       methods for them.
 * 08/26/2016       wdang       167534 - Added transId.
 * 07/05/2017       tzeng       186465 - Added transaction.
 * 06/28/2018       xnie        187070 - Added termEff.
 * 09/17/2018       ryzhao      195271 - Added CALL_FROM_SUB_TAB_B and getter function for it.
 * ---------------------------------------------------
 */
public class TransactionFields {

    public static final String TRANSACTION_CODE = "transactionCode";
    public static final String TRANSACTION_TYPE_CODE = "transactionTypeCode";
    public static final String TRANSACTION_EFFECTIVE_FROM_DATE = "transEffectiveFromDate";
    public static final String TRANSACTION_ACCOUNTING_DATE = "transactionAccountingDate";
    public static final String ENDORSEMENT_QUOTE_ID = "endorsementQuoteId";
    public static final String TRANSACTION_LOG_ID = "transactionLogId";
    public static final String TRANSACTION_COMMENT = "transactionComment";
    public static final String TRANSACTION_COMMENT2 = "transactionComment2";
    public static final String TRANSACTION_STATUS_CODE = "transactionStatusCode";
    public static final String TRANSACTION_STATUS = "transactionStatus";
    public static final String ENDORSEMENT_CODE = "endorsementCode";
    public static final String NEW_ACCOUNTING_DATE = "newAccountingDate";
    public static final String NEW_TRANSACTION_COMMENT = "newTransactionComment";
    public static final String NEW_TRANSACTION_COMMENT2 = "newTransactionComment2";
    public static final String NEW_DECLINE_REASON_CODE = "newDeclineReasonCode";
    public static final String NEW_ENDORSEMENT_CODE = "newEndorsementCode";
    public static final String LAST_TRANSACTION_ID = "lastTransactionId";
    public static final String TRANS_ID = "transId";
    public static final String TRANS_CODE = "transCode";
    public static final String TRANSACTION_EFF = "transEff";
    public static final String TRANS_EFF_DATE = "transEffDate";
    public static final String SHOW_ALL_OR_SHOW_TERM = "showAllOrShowTerm";
    public static final String TRANSACTION = "transaction";
    public static final String TERM_EFF = "termEff";
    public static final String CALL_FROM_SUB_TAB_B = "callFromSubTabB";

    public static void setEndorsementCode(Record record, String endorsementCode) {
      record.setFieldValue(ENDORSEMENT_CODE, endorsementCode);
    }

    public static String getNewEndorsementCode(Record record) {
      return record.getStringValue(NEW_ENDORSEMENT_CODE);
    }

    public static void setNewEndorsementCode(Record record, String newEndorsementCode) {
      record.setFieldValue(NEW_ENDORSEMENT_CODE, newEndorsementCode);
    }
    
    public static String getNewDeclineReasonCode(Record record) {
      return record.getStringValue(NEW_DECLINE_REASON_CODE);
    }

    public static void setNewDeclineReasonCode(Record record, String newDeclineReasonCode) {
      record.setFieldValue(NEW_DECLINE_REASON_CODE, newDeclineReasonCode);
    }

    public static boolean hasNewDeclineReasonCode(Record record) {
        return record.hasStringValue(NEW_DECLINE_REASON_CODE);
    }
    
    public static String getNewAccountingDate(Record record) {
      return record.getStringValue(NEW_ACCOUNTING_DATE);
    }

    public static void setNewAccountingDate(Record record, String newAccountingDate) {
      record.setFieldValue(NEW_ACCOUNTING_DATE, newAccountingDate);
    }



    public static String getNewTransactionComment(Record record) {
      return record.getStringValue(NEW_TRANSACTION_COMMENT);
    }

    public static void setNewTransactionComment(Record record, String newTransactionComment) {
      record.setFieldValue(NEW_TRANSACTION_COMMENT, newTransactionComment);
    }

    public static String getNewTransactionComment2(Record record) {
        return record.getStringValue(NEW_TRANSACTION_COMMENT2);
    }

    public static void setNewTransactionComment2(Record record, String newTransactionComment2) {
        record.setFieldValue(NEW_TRANSACTION_COMMENT2, newTransactionComment2);
    }


    public static String getTransactionComment(Record record) {
      return record.getStringValue(TRANSACTION_COMMENT);
    }

    public static void setTransactionComment(Record record, String transactionComment) {
      record.setFieldValue(TRANSACTION_COMMENT, transactionComment);
    }

    public static String getTransactionComment2(Record record) {
        return record.getStringValue(TRANSACTION_COMMENT2);
    }

    public static void setTransactionComment2(Record record, String transactionComment2) {
        record.setFieldValue(TRANSACTION_COMMENT2, transactionComment2);
    }


    public static boolean hasTransactionCode(Record record) {
        return record.hasStringValue(TRANSACTION_CODE);
    }


    public static TransactionCode getTransactionCode(Record record) {
        Object value = record.getFieldValue(TRANSACTION_CODE);
        TransactionCode result = null;
        if (value == null || value instanceof TransactionCode) {
            result = (TransactionCode) value;
        }
        else {
            result = TransactionCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setTransactionCode(Record record, TransactionCode transactionCode) {
        record.setFieldValue(TRANSACTION_CODE, transactionCode);
    }

    public static boolean hasTransactionTypeCode(Record record) {
        return record.hasStringValue(TRANSACTION_TYPE_CODE);
    }

    public static TransactionTypeCode getTransactionTypeCode(Record record) {
        Object value = record.getFieldValue(TRANSACTION_TYPE_CODE);
        TransactionTypeCode result = null;
        if (value == null || value instanceof TransactionTypeCode) {
            result = (TransactionTypeCode) value;
        }
        else {
            result = TransactionTypeCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setTransactionTypeCode(Record record, TransactionTypeCode transactionTypeCode) {
        record.setFieldValue(TRANSACTION_TYPE_CODE, transactionTypeCode);
    }

    public static String getTransactionEffectiveFromDate(Record record) {
        return record.getStringValue(TRANSACTION_EFFECTIVE_FROM_DATE);
    }

    public static void setTransactionEffectiveFromDate(Record record, String transEffectiveFromDate) {
        record.setFieldValue(TRANSACTION_EFFECTIVE_FROM_DATE, transEffectiveFromDate);
    }

    public static String getTransactionAccountingDate(Record record) {
        return record.getStringValue(TRANSACTION_ACCOUNTING_DATE);
    }

    public static void setTransactionAccountingDate(Record record, String transactionAccountingDate) {
        record.setFieldValue(TRANSACTION_ACCOUNTING_DATE, transactionAccountingDate);
    }

    public static String getEndorsementQuoteId(Record record) {
        return record.getStringValue(ENDORSEMENT_QUOTE_ID);
    }

    public static void setEndorsementQuoteId(Record record, String endorsementQuoteId) {
        record.setFieldValue(ENDORSEMENT_QUOTE_ID, endorsementQuoteId);
    }

    public static String getTransactionLogId(Record record) {
        return record.getStringValue(TRANSACTION_LOG_ID);
    }

    public static void setTransactionLogId(Record record, String transactionLogId) {
        record.setFieldValue(TRANSACTION_LOG_ID, transactionLogId);
    }

    public static void setTransactionEff(Record record, String transactionEff) {
        record.setFieldValue(TRANSACTION_EFF, transactionEff);
    }

    public static void setLastTransactionId(Record record, String lastTransactionId) {
        record.setFieldValue(LAST_TRANSACTION_ID, lastTransactionId);
    }
    public static boolean hasTransactionStatusCode(Record record) {
        return record.hasStringValue(TRANSACTION_STATUS_CODE);
    }
    
    public static String getTransEffDate(Record record) {
        return record.getStringValue(TRANS_EFF_DATE);
    }

    public static void setTransEffDate(Record record, String transEffDate) {
        record.setFieldValue(TRANS_EFF_DATE, transEffDate);
    }
    public static TransactionStatus getTransactionStatusCode(Record record) {
        Object value = record.getFieldValue(TRANSACTION_STATUS_CODE);
        TransactionStatus result = null;
        if (value == null || value instanceof TransactionStatus) {
            result = (TransactionStatus) value;
        }
        else {
            result = TransactionStatus.getInstance(value.toString());
        }
        return result;
    }
    
    public static TransactionStatus getTransactionStatus(Record record) {
        Object value = record.getFieldValue(TRANSACTION_STATUS);
        TransactionStatus result = null;
        if (value == null || value instanceof TransactionStatus) {
            result = (TransactionStatus) value;
        }
        else {
            result = TransactionStatus.getInstance(value.toString());
        }
        return result;
    }
    
    public static void setTransactionStatusCode(Record record, TransactionStatus transactionStatusCode) {
        record.setFieldValue(TRANSACTION_STATUS_CODE, transactionStatusCode);
    }

    public static TransactionCode getTransCode(Record record) {
        Object value = record.getFieldValue(TRANS_CODE);
        TransactionCode result = null;
        if (value == null || value instanceof TransactionCode) {
            result = (TransactionCode) value;
        }
        else {
            result = TransactionCode.getInstance(value.toString());
        }
        return result;
    }

    public static String getShowAllOrShowTerm(Record record) {
        return record.getStringValue(SHOW_ALL_OR_SHOW_TERM);
    }
    
    public static void setShowAllOrShowTerm(Record record, String showAllOrShowTerm) {
        record.setFieldValue(SHOW_ALL_OR_SHOW_TERM, showAllOrShowTerm);
    }

    public class ShowAllOrShowTermValues {
        public static final String ALL = "all";
        public static final String TERM = "term";
    }

    public static void setTermEff(Record record, String termEff) {
        record.setFieldValue(TERM_EFF, termEff);
    }
    public static boolean getTermEff(Record record) {
        return record.hasStringValue(TERM_EFF);
    }

    public static boolean getCallFromSubTabB(Record record) {
        if (record.hasStringValue(CALL_FROM_SUB_TAB_B)) {
            return YesNoFlag.getInstance(record.getStringValue(CALL_FROM_SUB_TAB_B)).booleanValue();
        }
        else {
            return YesNoFlag.N.booleanValue();
        }
    }

}
