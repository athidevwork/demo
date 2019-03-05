package dti.pm.tailmgr;

import dti.oasis.busobjs.EnumType;

import java.util.HashMap;
import java.util.Map;

/**
 * get tail screen mode for current policy
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 30, 2007
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

public class TailScreenMode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public static final int WIP_VALUE = getNextIntValue();
    public final static TailScreenMode WIP = new TailScreenMode(WIP_VALUE, "WIP");

    public static final int UPDATE_VALUE = getNextIntValue();
    public final static TailScreenMode UPDATE = new TailScreenMode(UPDATE_VALUE, "UPDATE");

    public static final int UPDATABLE_VALUE = getNextIntValue();
    public final static TailScreenMode UPDATABLE = new TailScreenMode(UPDATABLE_VALUE, "UPDATABLE");

    public static final int VIEW_ONLY_VALUE = getNextIntValue();
    public final static TailScreenMode VIEW_ONLY = new TailScreenMode(VIEW_ONLY_VALUE, "VIEW_ONLY");

    public static TailScreenMode getInstance(String screenModeCode) {
        TailScreenMode result = (TailScreenMode) c_validTypes.get(screenModeCode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The tailScreenMode '" + screenModeCode + "' is not a valid TailScreenMode.");
        }
        return result;
    }

    public boolean isWIP() {
        return this.intValue() == WIP_VALUE;
    }

    public boolean isUpdate() {
        return this.intValue() == UPDATE_VALUE;
    }

    public boolean isUpdatable() {
        return this.intValue() == UPDATABLE_VALUE;
    }

    public boolean isViewOnly() {
        return this.intValue() == VIEW_ONLY_VALUE;
    }

    private TailScreenMode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public TailScreenMode() {
    }
}
