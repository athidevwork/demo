package dti.oasis.error;

import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/23/2018
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class UserDefinedDBException extends ExpectedException {
    private final Logger l = LogUtils.getLogger(getClass());

    public UserDefinedDBException(String debugMessage) {
        super(debugMessage);
    }

    public UserDefinedDBException(String debugMessage, Object[] messageParameters) {
        super(debugMessage, messageParameters);
    }

    public UserDefinedDBException(String messageKey, String debugMessage) {
        super(messageKey, debugMessage);
    }

    public UserDefinedDBException(String messageKey, String debugMessage, Object[] messageParameters) {
        super(messageKey, debugMessage, messageParameters);
    }

    public UserDefinedDBException(String debugMessage, Throwable cause) {
        super(debugMessage, cause);
    }

    public UserDefinedDBException(String debugMessage, Object[] messageParameters, Throwable cause) {
        super(debugMessage, messageParameters, cause);
    }

    public UserDefinedDBException(String messageKey, String debugMessage, Throwable cause) {
        super(messageKey, debugMessage, cause);
    }

    public UserDefinedDBException(String messageKey, String debugMessage, Object[] messageParameters, Throwable cause) {
        super(messageKey, debugMessage, messageParameters, cause);
    }
}
