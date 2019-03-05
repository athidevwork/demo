package dti.pm.workflowmgr.jobqueuemgr;

import dti.oasis.busobjs.EnumType;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 22, 2008
 *
 * @author fcbibire
 */
/*
 * Enumerated type that represents different job categories.
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class JobCategory extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public static final int SHORT_VALUE = getNextIntValue();
    public static final JobCategory SHORT = new JobCategory(SHORT_VALUE, "SHORT");

    public static final int MEDIUM_VALUE = getNextIntValue();
    public static final JobCategory MEDIUM = new JobCategory(MEDIUM_VALUE, "MEDIUM");

    public static final int LONG_VALUE = getNextIntValue();
    public static final JobCategory LONG = new JobCategory(LONG_VALUE, "LONG");

    public static JobCategory getInstance(String jobCategory) {
        JobCategory result = (JobCategory) c_validTypes.get(jobCategory.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The job category '" + jobCategory + "' is not a valid jobCategory.");
        }
        return result;
    }

    public boolean isShort() {
        return intValue() == SHORT_VALUE;
    }

    public boolean isMedium() {
        return intValue() == MEDIUM_VALUE;
    }

    public boolean isLong() {
        return intValue() == LONG_VALUE;
    }

    private JobCategory(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public JobCategory() {
    }
}
