package dti.oasis.pageentitlementmgr;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type to define a default entitlement action that is either [ENABLED], [DISABLED], [HIDE]or [SHOW].
 *
 * The getInstance method is a convenience method for parsing a string to determine if it represents
 * [ENABLED], [DISABLED], [HIDE]or [SHOW].
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 14, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class DefaultEntitlementAction extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public static final int ENABLED_VALUE = getNextIntValue();
    public static final DefaultEntitlementAction ENABLED = new DefaultEntitlementAction(ENABLED_VALUE, "Enabled");

    public static final int DISABLED_VALUE = getNextIntValue();
    public static final DefaultEntitlementAction DISABLED = new DefaultEntitlementAction(DISABLED_VALUE, "Disabled");

    public static final int HIDE_VALUE = getNextIntValue();
    public static final DefaultEntitlementAction HIDE = new DefaultEntitlementAction(HIDE_VALUE, "Hide");

    public static final int SHOW_VALUE = getNextIntValue();
    public static final DefaultEntitlementAction SHOW = new DefaultEntitlementAction(SHOW_VALUE, "Show");
    /**
     * Return an instance of the DefaultEntitlementAction for the given default action.
     * The following strings are interpreted as valid default actions:
     * <ul>
     * <li>Enabled</li>
     * <li>Disabled</li>
     * <li>Hide</li>
     * <li>Show</li>
     * <p/>
     * An appException is raised, if the given action does not match any of the above.
     */
    public static DefaultEntitlementAction getInstance(String defaultAction) {
        DefaultEntitlementAction result = (DefaultEntitlementAction) c_validTypes.get(defaultAction.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("Invalid input parameter value. [defaultAction] must either be [Enabled], [Disabled], [Hide]or [Show]");
        }
        return result;
    }

    public boolean isEnabled() {
        return intValue() == ENABLED_VALUE;
    }

    public boolean isDisabled() {
        return intValue() == DISABLED_VALUE;
    }

    public boolean isHide() {
        return intValue() == HIDE_VALUE;
    }

    public boolean isShow() {
        return intValue() == SHOW_VALUE;
    }

    private DefaultEntitlementAction(int value, String name) {
        super(value, name);
        c_validTypes.put(name.toUpperCase(), this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public DefaultEntitlementAction() {
        super();
    }
}
