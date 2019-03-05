package dti.oasis.busobjs;

import dti.oasis.util.StringUtils;

/**
 * Enumerated type to define a field that is Yes or No.
 * The getInstance method is a convenience method for parsing a string to determine
 * if it represents Yes or No.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2006
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
public class YesNoFlag extends EnumType {

    public static final int Y_VALUE = 1;
    public static final YesNoFlag Y = new YesNoFlag(Y_VALUE, "Y");

    public static final int N_VALUE = 2;
    public static final YesNoFlag N = new YesNoFlag(N_VALUE, "N");

    /**
     * Return an instance of the YesNoFlag for the given flag.
     * The following strings (case-insensitive) are interpreted as Y:
     * <ul>
     * <li>Y</li>
     * <li>YES</li>
     * <li>ON</li>
     * <li>TRUE</li>
     * <li>CHECKED</li>
     * <li>SELECTED</li>
     * </ul>
     * <p/>
     * If the given flag does not match any of the above, it is interpreted as N.
     */
    public static YesNoFlag getInstance(String flag) {
        if (StringUtils.isBlank(flag))
            return YesNoFlag.N;
        else if ("Y".equalsIgnoreCase(flag) ||
            "YES".equalsIgnoreCase(flag) ||
            "ON".equalsIgnoreCase(flag) ||
            "TRUE".equalsIgnoreCase(flag) ||
            "CHECKED".equalsIgnoreCase(flag) ||
            "SELECTED".equalsIgnoreCase(flag)) {
            return YesNoFlag.Y;
        } else
            return YesNoFlag.N;
    }

    /**
     * Return an instance of the YesNoFlag matching the given boolean,
     * where true returns Y and false returns N.
     */
    public static YesNoFlag getInstance(boolean flag) {
        return flag == true ? Y : N;
    }

    /**
     * Return a boolean representation of this YesNoFlag,
     * where Y returns true and N returns false.
     */
    public boolean booleanValue() {
        return intValue() == Y_VALUE;
    }

    private YesNoFlag(int value, String name) {
        super(value, name);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public YesNoFlag() {
        super();
    }
}