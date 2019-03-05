package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 27, 2007
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

public class PolicyCycleCode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public static final int QUOTE_VALUE = getNextIntValue();
    public final static PolicyCycleCode QUOTE = new PolicyCycleCode(QUOTE_VALUE, "QUOTE");

    public static final int POLICY_VALUE = getNextIntValue();
    public final static PolicyCycleCode POLICY = new PolicyCycleCode(POLICY_VALUE, "POLICY");

    public static PolicyCycleCode getInstance(String policyCycleCode) {
        PolicyCycleCode result = (PolicyCycleCode) c_validTypes.get(policyCycleCode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The policyCycleCode '" + policyCycleCode + "' is not a valid PolicyCycleCode.");
        }
        return result;
    }

    public boolean isQuote() {
        return this.intValue() == QUOTE_VALUE;
    }

    public boolean isPolicy() {
        return this.intValue() == POLICY_VALUE;
    }

    private PolicyCycleCode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public PolicyCycleCode() {
    }
}
