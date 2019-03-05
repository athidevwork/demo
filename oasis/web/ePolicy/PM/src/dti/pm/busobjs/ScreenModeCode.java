package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;

import java.util.HashMap;
import java.util.Map;

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

public class ScreenModeCode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public static final int MANUAL_ENTRY_VALUE = getNextIntValue();
    public final static ScreenModeCode MANUAL_ENTRY = new ScreenModeCode(MANUAL_ENTRY_VALUE, "MANUAL_ENTRY");

    public static final int VIEW_POLICY_VALUE = getNextIntValue();
    public final static ScreenModeCode VIEW_POLICY = new ScreenModeCode(VIEW_POLICY_VALUE, "VIEW_POLICY");

    public static final int RENEW_WIP_VALUE = getNextIntValue();
    public final static ScreenModeCode RENEW_WIP = new ScreenModeCode(RENEW_WIP_VALUE, "RENEWWIP");

    public static final int CANCEL_WIP_VALUE = getNextIntValue();
    public final static ScreenModeCode CANCEL_WIP = new ScreenModeCode(CANCEL_WIP_VALUE, "CANCELWIP");

    public static final int REINSTATE_WIP_VALUE = getNextIntValue();
    public final static ScreenModeCode REINSTATE_WIP = new ScreenModeCode(REINSTATE_WIP_VALUE, "REINSTATEWIP");

    public static final int OOS_WIP_VALUE = getNextIntValue();
    public final static ScreenModeCode OOS_WIP = new ScreenModeCode(OOS_WIP_VALUE, "OOSWIP");

    public static final int VIEW_ENDQUOTE_VALUE = getNextIntValue();
    public final static ScreenModeCode VIEW_ENDQUOTE = new ScreenModeCode(VIEW_ENDQUOTE_VALUE, "VIEW_ENDQUOTE");

    public static final int WIP_VALUE = getNextIntValue();
    public final static ScreenModeCode WIP = new ScreenModeCode(WIP_VALUE, "WIP");


    public static ScreenModeCode getInstance(String screenModeCode) {
        ScreenModeCode result = (ScreenModeCode) c_validTypes.get(screenModeCode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The screenModeCode '" + screenModeCode + "' is not a valid ScreenModeCode.");
        }
        return result;
    }

    public boolean isManualEntry() {
        return intValue() == MANUAL_ENTRY_VALUE;
    }

    public boolean isViewPolicy() {
        return intValue() == VIEW_POLICY_VALUE;
    }

    public boolean isRenewWIP() {
        return intValue() == RENEW_WIP_VALUE;
    }

    public boolean isCancelWIP() {
        return intValue() == CANCEL_WIP_VALUE;
    }

    public boolean isResinstateWIP() {
        return intValue() == REINSTATE_WIP_VALUE;
    }

    public boolean isOosWIP() {
        return intValue() == OOS_WIP_VALUE;
    }

    public boolean isWIP() {
        return intValue() == WIP_VALUE;
    }

    public boolean isViewEndquote() {
        return intValue() == VIEW_ENDQUOTE_VALUE;
    }


    private ScreenModeCode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public ScreenModeCode() {
    }
}
