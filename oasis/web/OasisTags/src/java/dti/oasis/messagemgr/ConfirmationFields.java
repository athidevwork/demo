package dti.oasis.messagemgr;

import dti.oasis.recordset.Record;

/**
 * This class helps interogate a Record for confirmation fields.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 29, 2007
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
public class ConfirmationFields {
    /**
     * Returns true if a field exists representing the user's answer to a confirmation prompt, and the answer is yes.
     * Otherwise, returns false.
     * The confirmation prompt is keyed by the given messageKey.
     */
    public static boolean isConfirmed(String messageKey, Record record) {
        String messageKeyConfirmed = messageKey + ".confirmed";
        boolean isConfirmed = record.getBooleanValue(messageKeyConfirmed, false).booleanValue();
        return isConfirmed;
    }
}
