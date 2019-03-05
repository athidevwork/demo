package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerated type that represents different transaction type code for a given transaction.
 * The getInstance method is a convenience method for parsing a string into transaction status.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 11, 2007
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class TransactionTypeCode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public static final int CANC_REWRT_VALUE = getNextIntValue();
    public static final TransactionTypeCode CANC_REWRT = new TransactionTypeCode(CANC_REWRT_VALUE, "CANC/REWRT");

    public static final int CANCEL_VALUE = getNextIntValue();
    public static final TransactionTypeCode CANCEL = new TransactionTypeCode(CANCEL_VALUE, "CANCEL");

    public static final int CHGCLIENT_VALUE = getNextIntValue();
    public static final TransactionTypeCode CHGCLIENT = new TransactionTypeCode(CHGCLIENT_VALUE, "CHGCLIENT");

    public static final int CLICOIHOLD_VALUE = getNextIntValue();
    public static final TransactionTypeCode CLICOIHOLD = new TransactionTypeCode(CLICOIHOLD_VALUE, "CLICOIHOLD");

    public static final int ENDORSE_VALUE = getNextIntValue();
    public static final TransactionTypeCode ENDORSE = new TransactionTypeCode(ENDORSE_VALUE, "ENDORSE");

    public static final int ENDQUOTE_VALUE = getNextIntValue();
    public static final TransactionTypeCode ENDQUOTE = new TransactionTypeCode(ENDQUOTE_VALUE, "ENDQUOTE");

    public static final int EXTEND_VALUE = getNextIntValue();
    public static final TransactionTypeCode EXTEND = new TransactionTypeCode(EXTEND_VALUE, "EXTEND");

    public static final int NEWBUS_VALUE = getNextIntValue();
    public static final TransactionTypeCode NEWBUS = new TransactionTypeCode(NEWBUS_VALUE, "NEWBUS");

    public static final int QUEST_VALUE = getNextIntValue();
    public static final TransactionTypeCode QUEST = new TransactionTypeCode(QUEST_VALUE, "QUEST");

    public static final int QUICKPAY_VALUE = getNextIntValue();
    public static final TransactionTypeCode QUICKPAY = new TransactionTypeCode(QUICKPAY_VALUE, "QUICKPAY");

    public static final int QUOTE_VALUE = getNextIntValue();
    public static final TransactionTypeCode QUOTE = new TransactionTypeCode(QUOTE_VALUE, "QUOTE");

    public static final int REINSTATE_VALUE = getNextIntValue();
    public static final TransactionTypeCode REINSTATE = new TransactionTypeCode(REINSTATE_VALUE, "REINSTATE");

    public static final int REISSUE_VALUE = getNextIntValue();
    public static final TransactionTypeCode REISSUE = new TransactionTypeCode(REISSUE_VALUE, "REISSUE");

    public static final int RENEWAL_VALUE = getNextIntValue();
    public static final TransactionTypeCode RENEWAL = new TransactionTypeCode(RENEWAL_VALUE, "RENEWAL");

    public static final int TLENDORSE_VALUE = getNextIntValue();
    public static final TransactionTypeCode TLENDORSE = new TransactionTypeCode(TLENDORSE_VALUE, "TLENDORSE");

    public static final int UNDOTERM_VALUE = getNextIntValue();
    public static final TransactionTypeCode UNDOTERM = new TransactionTypeCode(UNDOTERM_VALUE, "UNDOTERM");


    public static TransactionTypeCode getInstance(String transactionTypeCode) {
        TransactionTypeCode result = (TransactionTypeCode) c_validTypes.get(transactionTypeCode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The transactionTypeCode '" + transactionTypeCode + "' is not a valid TransactionTypeCode.");
        }
        return result;
    }

    public boolean isCancRewrt() {
        return intValue() == CANC_REWRT_VALUE;
    }

    public boolean isCancel() {
        return intValue() == CANCEL_VALUE;
    }

    public boolean isChgClient() {
        return intValue() == CHGCLIENT_VALUE;
    }

    public boolean isCliCoiHold() {
        return intValue() == CLICOIHOLD_VALUE;
    }

    public boolean isEndorse() {
        return intValue() == ENDORSE_VALUE;
    }

    public boolean isEndQuote() {
        return intValue() == ENDQUOTE_VALUE;
    }

    public boolean isExtend() {
        return intValue() == EXTEND_VALUE;
    }

    public boolean isNewBus() {
        return intValue() == NEWBUS_VALUE;
    }

    public boolean isQuest() {
        return intValue() == QUEST_VALUE;
    }

    public boolean isQuickPay() {
        return intValue() == QUICKPAY_VALUE;
    }

    public boolean isQuote() {
        return intValue() == QUOTE_VALUE;
    }

    public boolean isReinstate() {
        return intValue() == REINSTATE_VALUE;
    }

    public boolean isReissue() {
        return intValue() == REISSUE_VALUE;
    }

    public boolean isRenewal() {
        return intValue() == RENEWAL_VALUE;
    }

    public boolean isTailEndorse() {
        return intValue() == TLENDORSE_VALUE;
    }

    public boolean isUndoterm() {
        return intValue() == UNDOTERM_VALUE;
    }


    private TransactionTypeCode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public TransactionTypeCode() {
    }
}
