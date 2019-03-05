package dti.pm.tailmgr;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents the record mode code of a Policy for the Tail Use Cases.
 * The getInstance method is a convenience method for parsing a string into record mode.
 * <p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 7, 2006
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class TailRecordMode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return TailRecordMode.c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int TEMP_VALUE = getNextIntValue();
    public final static TailRecordMode TEMP = new TailRecordMode(TailRecordMode.TEMP_VALUE, "TEMP");

    public final static int OFFICIAL_VALUE = getNextIntValue();
    public final static TailRecordMode OFFICIAL = new TailRecordMode(TailRecordMode.OFFICIAL_VALUE, "OFFICIAL");

    public final static int REQUEST_VALUE = getNextIntValue();
    public final static TailRecordMode REQUEST = new TailRecordMode(TailRecordMode.REQUEST_VALUE, "REQUEST");

    public final static int WIP_VALUE = getNextIntValue();
    public final static TailRecordMode WIP = new TailRecordMode(TailRecordMode.WIP_VALUE, "WIP");

    public final static int ENDQUOTE_VALUE = getNextIntValue();
    public final static TailRecordMode ENDQUOTE = new TailRecordMode(TailRecordMode.ENDQUOTE_VALUE, "ENDQUOTE");

    public static TailRecordMode getInstance(String tailRecordMode) {
        TailRecordMode result = (TailRecordMode) TailRecordMode.c_validTypes.get(tailRecordMode.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The tailRecordMode '" + tailRecordMode + "' is not a valid TailRecordMode.");
        }
        return result;
    }

    public boolean isTemp() {
        return intValue() == TailRecordMode.TEMP_VALUE;
    }

    public boolean isOfficial() {
        return intValue() == TailRecordMode.OFFICIAL_VALUE;
    }

    public boolean isRequest() {
        return intValue() == TailRecordMode.REQUEST_VALUE;
    }

    public boolean isWIP() {
        return intValue() == TailRecordMode.WIP_VALUE;
    }

    public boolean isEndquote() {
        return intValue() == TailRecordMode.ENDQUOTE_VALUE;
    }

    public TailRecordMode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public TailRecordMode() {
    }
}
