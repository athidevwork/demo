package dti.oasis.app;

import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.logging.Logger;

/**
 * This is the general Exception class uses for all exceptions.
 * The messageKey parameter is provided to link to a displayable message in a message resource file.
 * If no messageKey is provided, the UNEXPECTED_ERROR message key is used by default.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
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
public class AppException extends RuntimeException {

    /**
     * The default Message Key if none is supplied.
     */
    public static final String UNEXPECTED_ERROR = "appException.unexpected.error";

    /**
     * The separator used between debug messages.
     */
    public static final String DEBUG_MESSAGE_SEPARATOR = " :: ";

    /**
     * Construct this AppException with the given debug message.
     * UNEXPECTED_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param debugMessage a debug message
     */
    public AppException(String debugMessage) {
        this(UNEXPECTED_ERROR, debugMessage);
    }

    /**
     * Construct this AppException with the given debug message.
     * UNEXPECTED_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     * Set the message parameters for formatting the displayable message with replaceable parameters.
     *
     * @param debugMessage      a debug message
     * @param messageParameters parameters for formatting the displayable message with replaceable parameters
     */
    public AppException(String debugMessage, Object[] messageParameters) {
        this(UNEXPECTED_ERROR, debugMessage);
        setMessageParameters(messageParameters);
    }

    /**
     * Construct this AppException with the given debug message.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param messageKey   the key of the related displayable message.
     * @param debugMessage a debug message
     */
    public AppException(String messageKey, String debugMessage) {
        super("");
        m_messageKey = messageKey;
//        m_debugMessage.append(debugMessage);
        if (!StringUtils.isBlank(debugMessage))
            pushDebugMessage(debugMessage);
    }

    /**
     * Construct this AppException with the given debug message.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     * Set the message parameters for formatting the displayable message with replaceable parameters.
     *
     * @param messageKey        the key of the related displayable message.
     * @param debugMessage      a debug message
     * @param messageParameters parameters for formatting the displayable message with replaceable parameters
     */
    public AppException(String messageKey, String debugMessage, Object[] messageParameters) {
        super("");
        m_messageKey = messageKey;
//        m_debugMessage.append(debugMessage);
        if (!StringUtils.isBlank(debugMessage))
            pushDebugMessage(debugMessage);
        setMessageParameters(messageParameters);
    }

    /**
     * Construct this AppException with the given debug debugMessage.
     * UNEXPECTED_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param debugMessage a debug debugMessage
     * @param cause        the root cause of this AppException.
     */
    public AppException(String debugMessage, Throwable cause) {
        this(UNEXPECTED_ERROR, debugMessage, cause);
    }

    /**
     * Construct this AppException with the given debug debugMessage.
     * UNEXPECTED_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     * Set the message parameters for formatting the displayable message with replaceable parameters.
     *
     * @param debugMessage      a debug debugMessage
     * @param messageParameters parameters for formatting the displayable message with replaceable parameters
     * @param cause             the root cause of this AppException.
     */
    public AppException(String debugMessage, Object[] messageParameters, Throwable cause) {
        this(UNEXPECTED_ERROR, debugMessage, cause);
        setMessageParameters(messageParameters);
    }

    /**
     * Construct this AppException with the given debug message.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param messageKey   the key of the related displayable message.
     * @param debugMessage a debug message
     * @param cause        the root cause of this AppException.
     */
    public AppException(String messageKey, String debugMessage, Throwable cause) {
        super("", cause);
        m_messageKey = messageKey;
//        m_debugMessage.append(debugMessage);
        if (!StringUtils.isBlank(debugMessage))
            pushDebugMessage(debugMessage);
    }

    /**
     * Construct this AppException with the given debug message.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     * Set the message parameters for formatting the displayable message with replaceable parameters.
     *
     * @param messageKey        the key of the related displayable message.
     * @param debugMessage      a debug message
     * @param messageParameters parameters for formatting the displayable message with replaceable parameters
     * @param cause             the root cause of this AppException.
     */
    public AppException(String messageKey, String debugMessage, Object[] messageParameters, Throwable cause) {
        super("", cause);
        m_messageKey = messageKey;
//        m_debugMessage.append("; ").append(debugMessage);
        if (!StringUtils.isBlank(debugMessage))
            pushDebugMessage(debugMessage);
        setMessageParameters(messageParameters);
    }

    /**
     * Returns the detail message string with the message key followed by the debug message(s)
     */
    public String getMessage() {
        String description;
        if (hasMessageParameters()) {
            description = MessageManager.getInstance().formatMessage(getMessageKey(), getMessageParameters());
        }
        else {
            description = MessageManager.getInstance().formatMessage(getMessageKey());
        }
        return getMessageKey() + ":" + description + getDebugMessage();
    }

    /**
     * Return the displayable message key.
     */
    public String getMessageKey() {
        return m_messageKey;
    }

    /**
     * Set the message key for this AppException.
     * Use this method to override the given message key with a more descriptive message key.
     */
    public void setMessageKey(String messageKey) {
        m_messageKey = messageKey;
    }

    /**
     * Push a debug message to the front of the Debug Message Stack for this AppException
     */
    public void pushDebugMessage(String debugMessage) {
        if (!StringUtils.isBlank(debugMessage))
            m_debugMessage.insert(0, DEBUG_MESSAGE_SEPARATOR + debugMessage);
    }

    /**
     * Returns the detail message string with the message key followed by the debug message(s)
     */
    public String getDebugMessage() {
        return m_debugMessage.toString();
    }

    /**
     * Returns the messageParameters used for formatting messages
     */
    public Object[] getMessageParameters() {
        return m_messageParameters;
    }

    /**
     * Set the messageParameters for a given AppException
     */
    public void setMessageParameters(Object[] messageParameters) {
        m_messageParameters = messageParameters;
    }

    /**
     * Return a value indicating if a given AppException has MessageParameters
     */
    public boolean hasMessageParameters() {
        return (m_messageParameters != null);
    }

    private String m_messageKey;
    private StringBuffer m_debugMessage = new StringBuffer();
    private Object[] m_messageParameters;

}
