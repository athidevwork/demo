package dti.oasis.accesstrailmgr;

import dti.oasis.busobjs.EnumType;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 22, 2014
 *
 * @author Parker Xu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 *
 * ---------------------------------------------------
 */
public class OwsLogQueueState extends EnumType {

    private static int c_nextIntValue = 1;

    private static int getNextIntValue() {
        return c_nextIntValue++;
    }

    private static Map c_validTypes = new HashMap();

    public final static int INITIALIZED_VALUE = getNextIntValue();
    public final static OwsLogQueueState INITIALIZED = new OwsLogQueueState(INITIALIZED_VALUE, "INITIALIZED");

    public final static int PROCESSING_VALUE = getNextIntValue();
    public final static OwsLogQueueState PROCESSING = new OwsLogQueueState(PROCESSING_VALUE, "PROCESSING");

    public final static int COMPLETED_VALUE = getNextIntValue();
    public final static OwsLogQueueState COMPLETED = new OwsLogQueueState(COMPLETED_VALUE, "COMPLETED");

    public final static int FAILED_VALUE = getNextIntValue();
    public final static OwsLogQueueState FAILED = new OwsLogQueueState(FAILED_VALUE, "FAILED");

    public static OwsLogQueueState getInstance(String state) {
        OwsLogQueueState result = (OwsLogQueueState) c_validTypes.get(state.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The state '" + state + "' is not a valid OwsLogQueueState.");
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

    private OwsLogQueueState(int value, String state) {
        super(value, state);
        c_validTypes.put(state, this);
    }

    public OwsLogQueueState() {
    }
}

