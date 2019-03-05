package dti.oasis.recordset;

import dti.oasis.app.AppException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
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
public class FieldNotFoundException extends AppException {
    /**
     * Construct this AppException with the given debug message.
     * UNEXPECTED_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param debugMessage a debug message
     */
    public FieldNotFoundException(String debugMessage) {
        super(debugMessage);
    }
}
