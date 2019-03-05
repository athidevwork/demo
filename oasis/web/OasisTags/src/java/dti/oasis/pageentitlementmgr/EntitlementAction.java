package dti.oasis.pageentitlementmgr;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type to define an entitlement action that is either [ENABLED/DISABLED], [DISABLED/ENABLED], [HIDE/SHOW]
 * or [SHOW/HIDE].
 *
 * The getInstance method is a convenience method for parsing a string to determine if it represents [ENABLED/DISABLED],
 * [DISABLED/ENABLED], [HIDE/SHOW] or [SHOW/HIDE].
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 14, 2007
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
public class EntitlementAction extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public static final int ENABLED_DISABLED_VALUE = getNextIntValue();
    public static final EntitlementAction ENABLED_DISABLED = new EntitlementAction(ENABLED_DISABLED_VALUE, "Enabled/Disabled");

    public static final int DISABLED_ENABLED_VALUE = getNextIntValue();
    public static final EntitlementAction DISABLED_ENABLED = new EntitlementAction(DISABLED_ENABLED_VALUE, "Disabled/Enabled");

    public static final int HIDE_SHOW_VALUE = getNextIntValue();
    public static final EntitlementAction HIDE_SHOW = new EntitlementAction(HIDE_SHOW_VALUE, "Hide/Show");

    public static final int SHOW_HIDE_VALUE = getNextIntValue();
    public static final EntitlementAction SHOW_HIDE = new EntitlementAction(SHOW_HIDE_VALUE, "Show/Hide");
    /**
     * Return an instance of the EntitlementAction for the given action.
     * The following strings are interpreted as valid action:
     * <ul>
     * <li>Enabled</li>
     * <li>Disabled</li>
     * <li>Hide</li>
     * <li>Show</li>
     * <p/>
     * An appException is raised, if the given action does not match any of the above.
     */
    public static EntitlementAction getInstance(String action) {
        EntitlementAction result = (EntitlementAction) c_validTypes.get(action.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("Invalid input parameter value. [action] must either be [Enabled/Disabled], [Disabled/Enabled], [Hide/Show] or [Show/Hide]");
        }
        return result;
    }

    public boolean isEnabledDisabled() {
        return intValue() == ENABLED_DISABLED_VALUE ;
    }

    public boolean isDisabledEnabled() {
        return intValue() == DISABLED_ENABLED_VALUE ;
    }

    public boolean isHideShow() {
        return intValue() == HIDE_SHOW_VALUE ;
    }

    public boolean isShowHide() {
        return intValue() == SHOW_HIDE_VALUE ;
    }

    private EntitlementAction(int value, String name) {
        super(value, name);
        c_validTypes.put(name.toUpperCase(), this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public EntitlementAction() {
        super();
    }
}

