package dti.oasis.messagemgr;

import dti.oasis.busobjs.Info;

import java.io.Serializable;

/**
 * This class implements the info interface to form a basic bean for storaging error, warning and information messages.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 6, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/28/2007       sxm         Add messageValue
 * 07/12/2007       sxm         Add m_confirmedAsYRequired
 * 08/29/2007       sxm         Add m_messageFieldId and m_messageRowId
 * 09/08/2011       kshen       Added m_messageGridId.
 * ---------------------------------------------------
 */
public class Message implements Info {
    private static final long serialVersionUID = 2183034065997026834L;

    /**
     * Return the message key for this message.
     */
    public String getMessageKey() {
        return m_messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.m_messageKey = messageKey;
    }

    /**
     * Return the message category
     */
    public MessageCategory getMessageCategory() {
        return m_messageCategory;
    }

    public void setMessageCategory(MessageCategory messageCategory) {
        this.m_messageCategory = messageCategory;
    }

    /**
     * Return the message value.
     */
    public String getDefaultConfirmationValue() {
        return m_defaultConfirmationValue;
    }

    public void setDefaultConfirmationValue(String defaultConfirmationValue) {
        this.m_defaultConfirmationValue = defaultConfirmationValue;
    }

    /**
     * Return the message indicator.
     */
    public boolean getConfirmedAsYRequired() {
        return m_confirmedAsYRequired;
    }

    public void setConfirmedAsYRequired(boolean confirmedAsYRequired) {
        this.m_confirmedAsYRequired = confirmedAsYRequired;
    }

    /**
     * Return the message.
     */
    public String getMessage() {
        return m_message;
    }

    public void setMessage(String message) {
        this.m_message = message;
    }

    //For consistency with EMS that uses htmlMessage
    public String getHtmlMessage() {
        return m_message;
    }

    public void setHtmlMessage(String htmlMessage) {
        this.m_message = htmlMessage;
    }

    public String getOnClickEvent() {
        return onClickEvent;
    }

    public void setOnClickEvent(String onClickEvent) {
        this.onClickEvent = onClickEvent;
    }

    public Boolean getAutoClose() {
        return autoClose;
    }

    public void setAutoClose(Boolean autoClose) {
        this.autoClose = autoClose;
    }

    public Long getAutoCloseDelay() {
        return autoCloseDelay;
    }

    public void setAutoCloseDelay(Long autoCloseDelay) {
        this.autoCloseDelay = autoCloseDelay;
    }

    /**
     * Return related field ID for the message.
     */
    public String getMessageFieldId() {
        return m_messageFieldId;
    }

    public void setMessageFieldId(String messageFieldId) {
        this.m_messageFieldId = messageFieldId;
    }

    /**
     * Return related row ID for the message.
     */
    public String getMessageRowId() {
        return m_messageRowId;
    }

    public void setMessageRowId(String messageRowId) {
        this.m_messageRowId = messageRowId;
    }

    /**
     * Return the grid id for message.
     * @return
     */
    public String getMessageGridId() {
        return m_messageGridId;
    }

    /**
     * Set the grid id for message.
     * @param messageGridId
     */
    public void setMessageGridId(String messageGridId) {
        m_messageGridId = messageGridId;
    }

    public Message(String m_messageKey, String m_message, MessageCategory m_messageCategory, String m_defaultConfirmationValue,
                   boolean m_confirmedAsYRequired, String m_messageFieldId, String m_messageRowId, String m_messageGridId, String onClickEvent,
                   Boolean autoClose, Long autoCloseDelay) {
        this.m_messageKey = m_messageKey;
        this.m_message = m_message;
        this.m_messageCategory = m_messageCategory;
        this.m_defaultConfirmationValue = m_defaultConfirmationValue;
        this.m_confirmedAsYRequired = m_confirmedAsYRequired;
        this.m_messageFieldId = m_messageFieldId;
        this.m_messageRowId = m_messageRowId;
        this.m_messageGridId = m_messageGridId;
        this.onClickEvent = onClickEvent;
        this.autoClose = autoClose;
        this.autoCloseDelay = autoCloseDelay;
    }

    /**
     * Constructs this Message with the given messageKey and message as the specified MessageCategory.
     *
     * @param messageCategory, an Enum value to indicate whether the message is of type error, warning or information.
     * @param message, string representing a formatted message.
     */
    public Message(MessageCategory messageCategory, String messageKey, String message, boolean confirmedAsYRequired,
                   String messageFieldId, String messageRowId) {
        m_messageKey = messageKey;
        m_message = message;
        m_messageCategory = messageCategory;
        m_confirmedAsYRequired = confirmedAsYRequired;
        m_messageFieldId = messageFieldId == null ? "" : messageFieldId;
        m_messageRowId = messageRowId == null ? "" : messageRowId;
    }

    public Message(MessageCategory messageCategory, String messageKey, String message, boolean confirmedAsYRequired,
                   String messageFieldId, String messageRowId, String messageGridId) {
        m_messageCategory = messageCategory;
        m_messageKey = messageKey;
        m_message = message;
        m_confirmedAsYRequired = confirmedAsYRequired;
        m_messageFieldId = messageFieldId == null ? "" : messageFieldId;
        m_messageRowId = messageRowId == null ? "" : messageRowId;
        m_messageGridId = messageGridId == null ? "" : messageGridId;
    }

    public Message(MessageCategory messageCategory, String messageKey, String message, String defaultConfirmationValue) {
        m_messageCategory = messageCategory;
        m_messageKey = messageKey;
        m_message = message;
        m_defaultConfirmationValue = defaultConfirmationValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (m_messageCategory != null ? !m_messageCategory.equals(message.m_messageCategory) : message.m_messageCategory != null) return false;
        if (m_defaultConfirmationValue != null ? !m_defaultConfirmationValue.equals(message.m_defaultConfirmationValue) : message.m_defaultConfirmationValue != null) return false;
        if (autoClose != null ? !autoClose.equals(message.autoClose) : message.autoClose != null) return false;
        if (autoCloseDelay != null ? !autoCloseDelay.equals(message.autoCloseDelay) : message.autoCloseDelay != null)
            return false;
        if (m_message != null ? !m_message.equals(message.m_message) : message.m_message != null) return false;
        if (m_messageFieldId != null ? !m_messageFieldId.equals(message.m_messageFieldId) : message.m_messageFieldId != null)
            return false;
        if (m_messageGridId != null ? !m_messageGridId.equals(message.m_messageGridId) : message.m_messageGridId != null)
            return false;
        if (m_messageKey != null ? !m_messageKey.equals(message.m_messageKey) : message.m_messageKey != null) return false;
        if (m_messageRowId != null ? !m_messageRowId.equals(message.m_messageRowId) : message.m_messageRowId != null)
            return false;
        if (onClickEvent != null ? !onClickEvent.equals(message.onClickEvent) : message.onClickEvent != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = m_messageKey != null ? m_messageKey.hashCode() : 0;
        result = 31 * result + (m_messageCategory != null ? m_messageCategory.hashCode() : 0);
        result = 31 * result + (m_defaultConfirmationValue != null ? m_defaultConfirmationValue.hashCode() : 0);
        result = 31 * result + (m_message != null ? m_message.hashCode() : 0);
        result = 31 * result + (onClickEvent != null ? onClickEvent.hashCode() : 0);
        result = 31 * result + (autoClose != null ? autoClose.hashCode() : 0);
        result = 31 * result + (autoCloseDelay != null ? autoCloseDelay.hashCode() : 0);
        result = 31 * result + (m_messageFieldId != null ? m_messageFieldId.hashCode() : 0);
        result = 31 * result + (m_messageRowId != null ? m_messageRowId.hashCode() : 0);
        result = 31 * result + (m_messageGridId != null ? m_messageGridId.hashCode() : 0);
        return result;
    }

    /**
     * Method that returns the string representing of message object.
     */
    @Override
    public String toString() {
        return "Message{" +
            "m_messageKey='" + m_messageKey + '\'' +
            ", m_messageCategory=" + m_messageCategory +
            ", m_defaultConfirmationValue='" + m_defaultConfirmationValue + '\'' +
            ", m_confirmedAsYRequired='" + m_confirmedAsYRequired + '\'' +
            ", m_message='" + m_message + '\'' +
            ", onClickEvent='" + onClickEvent + '\'' +
            ", autoClose=" + autoClose +
            ", autoCloseDelay=" + autoCloseDelay +
            ", m_messageFieldId='" + m_messageFieldId + '\'' +
            ", m_messageRowId='" + m_messageRowId + '\'' +
            ", m_messageGridId='" + m_messageGridId + '\'' +
            '}';
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public Message() {
    }

    private String m_messageKey;
    private String htmlMessage;
    private String m_message;
    private MessageCategory m_messageCategory;
    private String m_defaultConfirmationValue;
    private boolean m_confirmedAsYRequired;
    private String m_messageFieldId;
    private String m_messageRowId;
    private String m_messageGridId;
    private String onClickEvent;
    private Boolean autoClose;
    private Long autoCloseDelay;

    /**
     * Method that returns a boolean value to indicate whether the message is of type error.
     *
     * @return boolean, true, if the message type is error; otherwise, false.
     */
    public boolean isError() {
        return getMessageCategory().isError();
    }

    /**
     * Method that returns a boolean value to indicate whether the message is of type warning.
     *
     * @return boolean, true, if the message type is warning; otherwise, false.
     */
    public boolean isWarning() {
        return getMessageCategory().isWarning();
    }

    /**
     * Method that returns a boolean value to indicate whether the message is of type information.
     *
     * @return boolean, true, if the message type is information; otherwise, false.
     */
    public boolean isInformation() {
        return getMessageCategory().isInformation();
    }
}
