package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents different policy status's for a policy or quote.
 * The getInstance method is a convenience method for parsing a string into a policy status.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 5, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/19/2007       fcb         HOLD added.
 * ---------------------------------------------------
 */

public class PolicyStatus extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int PENDING_VALUE = getNextIntValue();
    public final static PolicyStatus PENDING = new PolicyStatus(PENDING_VALUE, "PENDING");

    public final static int ACTIVE_VALUE = getNextIntValue();
    public final static PolicyStatus ACTIVE = new PolicyStatus(ACTIVE_VALUE, "ACTIVE");

    public final static int CANCEL_VALUE = getNextIntValue();
    public final static PolicyStatus CANCEL = new PolicyStatus(CANCEL_VALUE, "CANCEL");

    public final static int ACCEPTED_VALUE = getNextIntValue();
    public final static PolicyStatus ACCEPTED = new PolicyStatus(ACCEPTED_VALUE, "ACCEPTED");

    public final static int DENIED_VALUE = getNextIntValue();
    public final static PolicyStatus DENIED = new PolicyStatus(DENIED_VALUE, "DENIED");

    public final static int INVALID_VALUE = getNextIntValue();
    public final static PolicyStatus INVALID = new PolicyStatus(INVALID_VALUE, "INVALID");

    public final static int EXPIRED_VALUE = getNextIntValue();
    public final static PolicyStatus EXPIRED = new PolicyStatus(EXPIRED_VALUE, "EXPIRED");

    public final static int HOLD_VALUE = getNextIntValue();
    public final static PolicyStatus HOLD = new PolicyStatus(HOLD_VALUE, "HOLD");    

   public static PolicyStatus getInstance(String policyStatus) {
        PolicyStatus result = (PolicyStatus) c_validTypes.get(policyStatus.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The policyStatus '" + policyStatus + "' is not a valid PolicyStatus.");
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

    public boolean isAccepted() {
        return intValue() == ACCEPTED_VALUE;
    }

    public boolean isDenied() {
        return intValue() == DENIED_VALUE;
    }

    public boolean isInvalid() {
        return intValue() == INVALID_VALUE;
    }

    public boolean isExpired() {
        return intValue() == EXPIRED_VALUE;
    }

    public boolean isHold() {
        return intValue() == HOLD_VALUE;
    }

    private PolicyStatus(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public PolicyStatus() {
    }
}