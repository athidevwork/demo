package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;
import dti.oasis.util.StringUtils;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents WIP or OFFICIAL mode of a policy.
 * The getInstance method is a convenience method for parsing a string into policy mode.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 7, 2006
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
public class PolicyViewMode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int WIP_VALUE = getNextIntValue();
    public final static PolicyViewMode WIP = new PolicyViewMode(WIP_VALUE, "WIP");

    public final static int OFFICIAL_VALUE = getNextIntValue();
    public final static PolicyViewMode OFFICIAL = new PolicyViewMode(OFFICIAL_VALUE, "OFFICIAL");

    public final static int ENDQUOTE_VALUE = getNextIntValue();
    public final static PolicyViewMode ENDQUOTE = new PolicyViewMode(ENDQUOTE_VALUE, "ENDQUOTE");

    public static PolicyViewMode getInstance(String policyViewMode) {
        PolicyViewMode result = (PolicyViewMode) c_validTypes.get(policyViewMode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The policyViewMode '" + policyViewMode + "' is not a valid PolicyViewMode.");
        }
        return result;
    }

    public boolean isWIP() {
        return intValue() == WIP_VALUE;
    }

    public boolean isOfficial() {
        return intValue() == OFFICIAL_VALUE;
    }

    public boolean isEndquote() {
        return intValue() == ENDQUOTE_VALUE;
    }

    public PolicyViewMode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public PolicyViewMode() {
    }

}
