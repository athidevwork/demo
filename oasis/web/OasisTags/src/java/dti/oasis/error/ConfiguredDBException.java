package dti.oasis.error;

import dti.oasis.app.AppException;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   04, 20, 2016
 *
 * @author huixu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/21/2016       huixu       Issue#169769 Fix WebLogicSecurity.getAuthenticators to work in WebLogic 12.2.1
 *
 * ---------------------------------------------------
 */
public class ConfiguredDBException extends AppException {

    /**
     * Construct this ConfiguredDBException with an empty debug message.
     * VALIDATION_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     */
    public ConfiguredDBException() {
        super("");
    }

    /**
     * Construct this ConfiguredDBException with the given debug message.
     * VALIDATION_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param debugMessage a debug message
     */
    public ConfiguredDBException(String debugMessage) {
        super(debugMessage);
    }

    /**
     * Construct this ConfiguredDBException with the given debug message and the throws.
     * VALIDATION_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param debugMessage a debug message
     * @param e a Throwable
     */
    public ConfiguredDBException(String debugMessage,Throwable e) {
        super(debugMessage, e);
    }
}
