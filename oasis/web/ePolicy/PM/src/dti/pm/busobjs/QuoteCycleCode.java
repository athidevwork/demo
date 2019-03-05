package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerated type that represents valid quote cycle code.
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  July 1, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/26/2016       wdang       167534 - Initial Version.
 * ---------------------------------------------------
 */
public class QuoteCycleCode extends EnumType {

    private static int c_nextIntValue = 1;

    private static int getNextIntValue() {
        return c_nextIntValue++;
    }

    private static Map c_validTypes = new HashMap();

    public final static int NB_VALUE = getNextIntValue();
    public final static QuoteCycleCode NB = new QuoteCycleCode(NB_VALUE, "NB");

    public final static int RN_VALUE = getNextIntValue();
    public final static QuoteCycleCode RN = new QuoteCycleCode(RN_VALUE, "RN");

    public static QuoteCycleCode getInstance(String quoteCycleCode) {
        QuoteCycleCode result = (QuoteCycleCode) c_validTypes.get(quoteCycleCode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The quoteCycleCode '" + quoteCycleCode + "' is not a valid QuoteCycleCode.");
        }
        return result;
    }

    public boolean isNBQuote() {
        return this.intValue() == NB_VALUE;
    }

    public boolean isRNQuote() {
        return this.intValue() == RN_VALUE;
    }

    /**
     * This constructor is for use with Serialization only.
     */
    private QuoteCycleCode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }
}