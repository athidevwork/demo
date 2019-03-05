package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;
import dti.oasis.util.StringUtils;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents different transaction status for a given transaction.
 * The getInstance method is a convenience method for parsing a string into transaction status.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 7, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/25/2007       Mark        Added isInvalid()
 * ---------------------------------------------------
 */
public class TransactionStatus extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public static final int INPROGRESS_VALUE = getNextIntValue();
    public static final TransactionStatus INPROGRESS = new TransactionStatus(INPROGRESS_VALUE, "INPROGRESS");

    public static final int OUTPUT_VALUE = getNextIntValue();
    public static final TransactionStatus OUTPUT = new TransactionStatus(OUTPUT_VALUE, "OUTPUT");

    public static final int COMPLETE_VALUE = getNextIntValue();
    public static final TransactionStatus COMPLETE = new TransactionStatus(COMPLETE_VALUE, "COMPLETE");

    public static final int OFFICIAL_VALUE = getNextIntValue();
    public static final TransactionStatus OFFICIAL = new TransactionStatus(OFFICIAL_VALUE, "OFFICIAL");

    public static final int ENDQUOTE_VALUE = getNextIntValue();
    public static final TransactionStatus ENDQUOTE = new TransactionStatus(ENDQUOTE_VALUE, "ENDQUOTE");

    public static final int RATE_VALUE = getNextIntValue();
    public static final TransactionStatus RATE = new TransactionStatus(OFFICIAL_VALUE, "RATE");

    public static final int INVALID_VALUE = getNextIntValue();
    public static final TransactionStatus INVALID = new TransactionStatus(INVALID_VALUE, "INVALID");

    public static TransactionStatus getInstance(String transactionStatus) {
        TransactionStatus result = (TransactionStatus) c_validTypes.get(transactionStatus.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The transactionStatus '" + transactionStatus + "' is not a valid TransactionStatus.");
        }
        return result;
    }

    public boolean isInProgress() {
        return intValue() == INPROGRESS_VALUE;
    }

    public boolean isOutput() {
        return intValue() == OUTPUT_VALUE;
    }

    public boolean isComplete() {
        return intValue() == COMPLETE_VALUE;
    }

    public boolean isOfficial() {
        return intValue() == OFFICIAL_VALUE;
    }

    public boolean isEndQuote() {
        return intValue() == ENDQUOTE_VALUE;
    }

    public boolean isRate() {
        return intValue() == RATE_VALUE;
    }

    public boolean isInvalid() {
        return intValue() == INVALID_VALUE;
    }

    private TransactionStatus(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public TransactionStatus() {
    }
}
