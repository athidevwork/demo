package dti.pm.workflowmgr.jobqueuemgr.impl;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 7, 2009
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
public class RequestState extends EnumType{

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int INITIALIZED_VALUE = getNextIntValue();
    public final static RequestState INITIALIZED = new RequestState(INITIALIZED_VALUE, "INITIALIZED");

    public final static int PROCESSING_VALUE = getNextIntValue();
    public final static RequestState PROCESSING = new RequestState(PROCESSING_VALUE, "PROCESSING");

    public final static int COMPLETED_VALUE = getNextIntValue();
    public final static RequestState COMPLETED = new RequestState(COMPLETED_VALUE, "COMPLETED");

    public final static int FAILED_VALUE = getNextIntValue();
    public final static RequestState FAILED = new RequestState(FAILED_VALUE, "FAILED");

    public static RequestState getInstance(String state) {
        RequestState result = (RequestState) c_validTypes.get(state.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The state '" + state + "' is not a valid RequestState.");
        }
        return result;
    }

    public boolean isInitialized() {
        return intValue() == INITIALIZED_VALUE;
    }

    public boolean isProcessing() {
        return intValue() == PROCESSING_VALUE;
    }

    public boolean isCompleted() {
        return intValue() == COMPLETED_VALUE;
    }

    public boolean isFailed() {
        return intValue() == FAILED_VALUE;
    }

    private RequestState(int value, String state) {
        super(value, state);
        c_validTypes.put(state, this);
    }

    public RequestState() {
    }

}

