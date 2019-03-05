package dti.pm.tailmgr.impl;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 13, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/26/2010       gxc         106051 - Modified to handle Adjust Limit process code
 * ---------------------------------------------------
 */

public class TailProcessCode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int ACCEPT_VALUE = getNextIntValue();
    public static final TailProcessCode ACCEPT = new TailProcessCode(ACCEPT_VALUE,"ACCEPT");
    public final static int ACTIVATE_VALUE = getNextIntValue();
    public static final TailProcessCode ACTIVATE = new TailProcessCode(ACTIVATE_VALUE,"ACTIVATE");
    public final static int DECLINE_VALUE = getNextIntValue();
    public static final TailProcessCode DECLINE = new TailProcessCode(DECLINE_VALUE,"DECLINE");
    public final static int CANCEL_VALUE = getNextIntValue();
    public static final TailProcessCode CANCEL = new TailProcessCode(CANCEL_VALUE,"CANCEL");
    public final static int REINSTATE_VALUE = getNextIntValue();
    public static final TailProcessCode REINSTATE = new TailProcessCode(REINSTATE_VALUE,"REINSTATE");
    public final static int SAVE_VALUE = getNextIntValue();
    public static final TailProcessCode SAVE = new TailProcessCode(SAVE_VALUE,"SAVE");
    public final static int UPDATE_VALUE = getNextIntValue();
    public static final TailProcessCode UPDATE = new TailProcessCode(UPDATE_VALUE,"UPDATE");
    public final static int ADJLIMIT_VALUE = getNextIntValue();
    public static final TailProcessCode ADJ_LIMIT = new TailProcessCode(ADJLIMIT_VALUE,"ADJ_LIMIT");


    public static TailProcessCode getInstance(String pmStatusCode) {
        TailProcessCode result = (TailProcessCode) c_validTypes.get(pmStatusCode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The pmStatusCode '" + pmStatusCode + "' is not a valid PMStatusCode.");
        }
        return result;
    }

    public boolean isAccept() {
        return intValue() == ACCEPT_VALUE;
    }

    public boolean isActivate() {
        return intValue() == ACTIVATE_VALUE;
    }

    public boolean isDecline() {
        return intValue() == DECLINE_VALUE;
    }

    public boolean isCancel() {
        return intValue() ==CANCEL_VALUE;
    }

    public boolean isReinstate() {
        return intValue() == REINSTATE_VALUE;
    }

    public boolean isSave() {
        return intValue() == SAVE_VALUE;
    }

    public boolean isUpdate() {
        return intValue() == UPDATE_VALUE;
    }

    public boolean isAdjLimit() {
        return intValue() == ADJLIMIT_VALUE;
    }

    private TailProcessCode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public TailProcessCode() {
    }
}
