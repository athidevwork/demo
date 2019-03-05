package dti.oasis.pageentitlementmgr;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type to define an indicator field location that is either Page or Row.
 * The getInstance method is a convenience method for parsing a string to determine
 * if it represents Page or Row.
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
public class IndicatorFieldLocation extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public static final int PAGE_VALUE = getNextIntValue();
    public static final IndicatorFieldLocation PAGE = new IndicatorFieldLocation(PAGE_VALUE, "PAGE");

    public static final int ROW_VALUE = getNextIntValue();
    public static final IndicatorFieldLocation ROW = new IndicatorFieldLocation(ROW_VALUE, "ROW");

    /**
     * Return an instance of the IndicatorFieldLocation for the given action.
     * The following strings are interpreted as valid action:
     * <ul>
     * <li>Page</li>
     * <li>GridRow</li>
     * <li>Row</li>
     * <p/>
     * An appException is raised, if the given action does not match any of the above.
     */
    public static IndicatorFieldLocation getInstance(String indicatorFieldLocation) {
        IndicatorFieldLocation result = (IndicatorFieldLocation) c_validTypes.get(indicatorFieldLocation.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("Invalid indicatorFieldLocation value '" + indicatorFieldLocation + "'; must either be Page or Row.");
        }
        return result;
    }

    public boolean isPage() {
        return intValue() == PAGE_VALUE ;
    }

    public boolean isRow() {
        return intValue() == ROW_VALUE;
    }

    private IndicatorFieldLocation(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public IndicatorFieldLocation() {
        super();
    }
}

