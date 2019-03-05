package dti.pm.riskmgr.empphysmgr;

import dti.oasis.busobjs.EnumType;
import dti.oasis.util.StringUtils;

import java.util.Map;
import java.util.HashMap;

/**
 * Employment status code
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2007
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
public class EmploymentStatusCode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return EmploymentStatusCode.c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int FULLTIME_VALUE = getNextIntValue();
    public final static EmploymentStatusCode FULLTIME = new EmploymentStatusCode(EmploymentStatusCode.FULLTIME_VALUE, "FULLTIME");

    public final static int PARTTIME_VALUE = getNextIntValue();
    public final static EmploymentStatusCode PARTTIME = new EmploymentStatusCode(EmploymentStatusCode.PARTTIME_VALUE, "PARTTIME");

    public final static int PREDIEM_VALUE = getNextIntValue();
    public final static EmploymentStatusCode PREDIEM = new EmploymentStatusCode(EmploymentStatusCode.PREDIEM_VALUE, "PERDIEM");

    public final static int UNDEFINED_VALUE = getNextIntValue();
     public final static EmploymentStatusCode UNDEFINED = new EmploymentStatusCode(EmploymentStatusCode.UNDEFINED_VALUE, "UNDEFINED");

    public static EmploymentStatusCode getInstance(String employmentStatus) {

        EmploymentStatusCode result = !StringUtils.isBlank(employmentStatus)?
            (EmploymentStatusCode) EmploymentStatusCode.c_validTypes.get(employmentStatus.toUpperCase()):EmploymentStatusCode.UNDEFINED;
        if (result == null) {
            result = EmploymentStatusCode.UNDEFINED; 
//            throw new IllegalArgumentException("The employmentStatusCode '" + employmentStatus + "' is not a valid employmentStatus.");
        }
        return result;
    }

    public boolean isFulltime() {
        return intValue() == EmploymentStatusCode.FULLTIME_VALUE;
    }

    public boolean isParttime() {
        return intValue() == EmploymentStatusCode.PARTTIME_VALUE;
    }

    public boolean isPrediem() {
        return intValue() == EmploymentStatusCode.PREDIEM_VALUE;
    }

    public boolean isUndefined() {
        return intValue() == EmploymentStatusCode.UNDEFINED_VALUE;
    }



    public EmploymentStatusCode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public EmploymentStatusCode() {
    }
}