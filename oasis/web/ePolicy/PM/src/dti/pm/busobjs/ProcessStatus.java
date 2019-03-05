package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents different policy process status's for a policy.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 29, 2007
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ProcessStatus extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    private static String NULL_STRING = "NULL";
    private static String CANCELONLY_STRING = "CANCELONLY";

    public final static int CANCELONLY_VALUE = getNextIntValue();
    public final static ProcessStatus CANCELONLY = new ProcessStatus(CANCELONLY_VALUE, CANCELONLY_STRING);

    public final static int NULL_VALUE = getNextIntValue();
    public final static ProcessStatus NULL = new ProcessStatus(NULL_VALUE, NULL_STRING);

    public static ProcessStatus getInstance(String processStatus) {
        processStatus = (processStatus == null)?NULL_STRING:processStatus;
        ProcessStatus result = (ProcessStatus) c_validTypes.get(processStatus.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The processStatus '" + processStatus + "' is not a valid ProcessStatus.");
        }
        return result;
    }

    public boolean isProcessStatusCancelOnly() {
        return intValue() == CANCELONLY_VALUE;
    }

     public boolean isProcessStatusNull() {
        return intValue() == NULL_VALUE;
    }

    private ProcessStatus(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public ProcessStatus() {
    }
}
