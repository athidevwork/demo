package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents different status codes PM entities, such as a Risk, Coverage, Coverage Class or Component.
 * The getInstance method is a convenience method for parsing a string into a policy status.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 5, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/07/2010       wtian       Added a status code "HOLD".
 * 04/25/2010       fcb         105791: Added status code "CONVERTED".
 * ---------------------------------------------------
 */

public class PMStatusCode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int PENDING_VALUE = getNextIntValue();
    public final static PMStatusCode PENDING = new PMStatusCode(PENDING_VALUE, "PENDING");

    public final static int ACTIVE_VALUE = getNextIntValue();
    public final static PMStatusCode ACTIVE = new PMStatusCode(ACTIVE_VALUE, "ACTIVE");

    public final static int CANCEL_VALUE = getNextIntValue();
    public final static PMStatusCode CANCEL = new PMStatusCode(CANCEL_VALUE, "CANCEL");

    public final static int HOLD_VALUE = getNextIntValue();
    public final static PMStatusCode HOLD = new PMStatusCode(HOLD_VALUE, "HOLD");

    public final static int CONVERTED_VALUE = getNextIntValue();
    public final static PMStatusCode CONVERTED = new PMStatusCode(CONVERTED_VALUE, "CONVERTED");

    public static PMStatusCode getInstance(String pmStatusCode) {
        PMStatusCode result = (PMStatusCode) c_validTypes.get(pmStatusCode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The pmStatusCode '" + pmStatusCode + "' is not a valid PMStatusCode.");
        }
        return result;
    }

    public boolean isPending() {
        return intValue() == PENDING_VALUE;
    }

    public boolean isActive() {
        return intValue() == ACTIVE_VALUE;
    }

    public boolean isCancelled() {
        return intValue() == CANCEL_VALUE;
    }

    public boolean isHold() {
        return intValue() == HOLD_VALUE;
    }

    public boolean isConverted() {
        return intValue() == CONVERTED_VALUE;
    }

    private PMStatusCode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public PMStatusCode() {
    }
}
