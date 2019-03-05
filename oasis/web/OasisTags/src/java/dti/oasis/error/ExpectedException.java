package dti.oasis.error;

import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/24/2018
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
public class ExpectedException extends AppException {
    private final Logger l = LogUtils.getLogger(getClass());

    public ExpectedException(String debugMessage) {
        super(debugMessage);
    }

    public ExpectedException(String debugMessage, Object[] messageParameters) {
        super(debugMessage, messageParameters);
    }

    public ExpectedException(String messageKey, String debugMessage) {
        super(messageKey, debugMessage);
    }

    public ExpectedException(String messageKey, String debugMessage, Object[] messageParameters) {
        super(messageKey, debugMessage, messageParameters);
    }

    public ExpectedException(String debugMessage, Throwable cause) {
        super(debugMessage, cause);
    }

    public ExpectedException(String debugMessage, Object[] messageParameters, Throwable cause) {
        super(debugMessage, messageParameters, cause);
    }

    public ExpectedException(String messageKey, String debugMessage, Throwable cause) {
        super(messageKey, debugMessage, cause);
    }

    public ExpectedException(String messageKey, String debugMessage, Object[] messageParameters, Throwable cause) {
        super(messageKey, debugMessage, messageParameters, cause);
    }
}
