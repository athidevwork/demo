package dti.ci.core.error;

import dti.oasis.app.AppException;

/**
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 18, 2008
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
public class PersistenceException extends AppException {

    /**
     * Construct this PersistenceException with the given debug message.
     * UNEXPECTED_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param debugMessage a debug message
     */
    public PersistenceException(String debugMessage) {
        super(debugMessage);
    }

    /**
     * Construct this PersistenceException with the given debug message.
     * UNEXPECTED_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     * Set the message parameters for formatting the displayable message with replaceable parameters.
     *
     * @param debugMessage      a debug message
     * @param messageParameters parameters for formatting the displayable message with replaceable parameters
     */
    public PersistenceException(String debugMessage, Object[] messageParameters) {
        super(debugMessage, messageParameters);
    }

    /**
     * Construct this PersistenceException with the given debug message.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param messageKey   the key of the related displayable message.
     * @param debugMessage a debug message
     */
    public PersistenceException(String messageKey, String debugMessage) {
        super(messageKey, debugMessage);
    }

    /**
     * Construct this PersistenceException with the given debug message.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     * Set the message parameters for formatting the displayable message with replaceable parameters.
     *
     * @param messageKey        the key of the related displayable message.
     * @param debugMessage      a debug message
     * @param messageParameters parameters for formatting the displayable message with replaceable parameters
     */
    public PersistenceException(String messageKey, String debugMessage, Object[] messageParameters) {
        super(messageKey, debugMessage, messageParameters);
    }

    /**
     * Construct this PersistenceException with the given debug debugMessage.
     * UNEXPECTED_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param debugMessage a debug debugMessage
     * @param cause        the root cause of this AppException.
     */
    public PersistenceException(String debugMessage, Throwable cause) {
        super(debugMessage, cause);
    }

    /**
     * Construct this PersistenceException with the given debug debugMessage.
     * UNEXPECTED_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     * Set the message parameters for formatting the displayable message with replaceable parameters.
     *
     * @param debugMessage      a debug debugMessage
     * @param messageParameters parameters for formatting the displayable message with replaceable parameters
     * @param cause             the root cause of this AppException.
     */
    public PersistenceException(String debugMessage, Object[] messageParameters, Throwable cause) {
        super(debugMessage, messageParameters, cause);
    }

    /**
     * Construct this PersistenceException with the given debug message.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param messageKey   the key of the related displayable message.
     * @param debugMessage a debug message
     * @param cause        the root cause of this AppException.
     */
    public PersistenceException(String messageKey, String debugMessage, Throwable cause) {
        super(messageKey, debugMessage, cause);
    }

    /**
     * Construct this PersistenceException with the given debug message.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     * Set the message parameters for formatting the displayable message with replaceable parameters.
     *
     * @param messageKey        the key of the related displayable message.
     * @param debugMessage      a debug message
     * @param messageParameters parameters for formatting the displayable message with replaceable parameters
     * @param cause             the root cause of this AppException.
     */
    public PersistenceException(String messageKey, String debugMessage, Object[] messageParameters, Throwable cause) {
        super(messageKey, debugMessage, messageParameters, cause);
    }
}
