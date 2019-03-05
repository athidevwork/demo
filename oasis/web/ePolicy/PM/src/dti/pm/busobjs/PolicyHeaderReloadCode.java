package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents different policy header reload codes.
 * The getInstance method is a convenience method for parsing a string into a policy header reload code.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 12, 2007
 *
 * @author sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class PolicyHeaderReloadCode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int CURRENT_TERM_VALUE = getNextIntValue();
    public final static PolicyHeaderReloadCode CURRENT_TERM = new PolicyHeaderReloadCode(CURRENT_TERM_VALUE, "CURRENT_TERM");

    public final static int LAST_TERM_VALUE = getNextIntValue();
    public final static PolicyHeaderReloadCode LAST_TERM = new PolicyHeaderReloadCode(LAST_TERM_VALUE, "LAST_TERM");

    public static PolicyHeaderReloadCode getInstance(String policyHeaderReloadCode) {
        PolicyHeaderReloadCode result = (PolicyHeaderReloadCode) c_validTypes.get(policyHeaderReloadCode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The policyHeaderReloadCode '" + policyHeaderReloadCode + "' is not a valid PolicyHeaderReloadCode.");
        }
        return result;
    }

    public boolean isCurrentTerm() {
        return intValue() == CURRENT_TERM_VALUE;
    }

    public boolean isLastTerm() {
        return intValue() == LAST_TERM_VALUE;
    }

    public PolicyHeaderReloadCode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public PolicyHeaderReloadCode() {
    }
}
