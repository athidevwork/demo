package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents TEMP or OFFICIAL mode of a policy.
 * The getInstance method is a convenience method for parsing a string into record mode.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 7, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *06/04/2007        sma         Added handling of record mode code REQUEST
 * ---------------------------------------------------
 */
public class RecordMode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int TEMP_VALUE = getNextIntValue();
    public final static RecordMode TEMP = new RecordMode(TEMP_VALUE, "TEMP");

    public final static int OFFICIAL_VALUE = getNextIntValue();
    public final static RecordMode OFFICIAL = new RecordMode(OFFICIAL_VALUE, "OFFICIAL");

    public final static int REQUEST_VALUE = getNextIntValue();
    public final static RecordMode REQUEST = new RecordMode(REQUEST_VALUE, "REQUEST");

    public final static int WIP_VALUE = getNextIntValue();
    public final static RecordMode WIP = new RecordMode(RecordMode.WIP_VALUE, "WIP");

    public final static int ENDQUOTE_VALUE = getNextIntValue();
    public final static RecordMode ENDQUOTE = new RecordMode(RecordMode.ENDQUOTE_VALUE, "ENDQUOTE");

    public static RecordMode getInstance(String recordMode) {
        RecordMode result = (RecordMode) c_validTypes.get(recordMode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The recordMode '" + recordMode + "' is not a valid RecordMode.");
        }
        return result;
    }

    public boolean isTemp() {
        return intValue() == TEMP_VALUE;
    }

    public boolean isOfficial() {
        return intValue() == OFFICIAL_VALUE;
    }

    public boolean isRequest() {
        return intValue() == REQUEST_VALUE;
    }

    public boolean isWIP() {
        return intValue() == RecordMode.WIP_VALUE;
    }

    public boolean isEndquote() {
        return intValue() == RecordMode.ENDQUOTE_VALUE;
    }

    private RecordMode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public RecordMode() {
    }
}
