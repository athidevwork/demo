package dti.oasis.log;

import java.util.logging.Level;
/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   November 14, 2017
 *
 * @author mgitelman
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class StoredProcedureLogLevel extends Level {
    public static final Level STORED_PROCEDURE = new StoredProcedureLogLevel("STORED_PROCEDURE", Level.FINEST.intValue() - 10);

    public StoredProcedureLogLevel(String name, int value) {
        super(name, value);
    }
}
