package dti.oasis.busobjs;

import dti.oasis.util.StringUtils;

/**
 * Enumerated type to define a field that is Yes, No or a null or empty string.
 * The getInstance method is a convenience method for parsing a string to determine
 * if it represents Yes, No or empty.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 19, 2012
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
public class YesNoEmptyFlag extends EnumType {

    public static final int Y_VALUE = 1;
    public static final YesNoEmptyFlag Y = new YesNoEmptyFlag(Y_VALUE, "Y");

    public static final int N_VALUE = 2;
    public static final YesNoEmptyFlag N = new YesNoEmptyFlag(N_VALUE, "N");

    public static final int EMPTY_VALUE = 3;
    public static final YesNoEmptyFlag EMPTY = new YesNoEmptyFlag(EMPTY_VALUE, "");

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
     * If the given flag is an empty string or null, it is EMPTY.
     */
    public static YesNoEmptyFlag getInstance(String flag) {
        if (StringUtils.isBlank(flag))
            return YesNoEmptyFlag.EMPTY;
        else if ("Y".equalsIgnoreCase(flag) ||
            "YES".equalsIgnoreCase(flag) ||
            "ON".equalsIgnoreCase(flag) ||
            "TRUE".equalsIgnoreCase(flag) ||
            "CHECKED".equalsIgnoreCase(flag) ||
            "SELECTED".equalsIgnoreCase(flag)) {
            return YesNoEmptyFlag.Y;
        } else
            return YesNoEmptyFlag.N;
    }

    /**
     * Return an instance of the YesNoEmptyFlag matching the given boolean,
     * where true returns Y and false returns N.
     */
    public static YesNoEmptyFlag getInstance(boolean flag) {
        return flag == true ? Y : N;
    }

    /**
     * Return a boolean representation of this YesNoEmptyFlag,
     * where Y returns true, N returns false and EMPTY returns false.
     */
    public boolean booleanValue() {
        return intValue() == Y_VALUE;
    }

    /**
     * Return "true" for boolean true,  "false" for boolean false, and "" for a null or empty string.
     */
    public String trueFalseEmptyValue() {
        String value = null;
        switch (intValue()) {
            case Y_VALUE:
                value = "true";
                break;
            case N_VALUE:
                value = "false";
                break;
            default:
                value = "";
        }
        return value;
    }

    private YesNoEmptyFlag(int value, String name) {
        super(value, name);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public YesNoEmptyFlag() {
        super();
    }
}