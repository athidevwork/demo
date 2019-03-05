package dti.oasis.messagemgr.impl;

import dti.oasis.messagemgr.*;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.app.ApplicationContext;
import dti.oasis.util.StringUtils;
import dti.oasis.util.LogUtils;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.JstlUtils;

import javax.servlet.ServletContext;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends MessageManager to provide implementation for
 * managing the messages and confirmation prompts within the scope of a single request.
 * <p/>
 * All the add methods resolves the messageKey into formatted message using the java.text.MessageFormat class
 * and the configured ResourceBundle. If this Message Manager was configured with a MessageSource,
 * it is adapted into a ResourceBundle. If an object array of messageParameters are provided to the add method,
 * the values are used for formatting the final message. These messages are stored for the current request.
 * <p/>
 * The formatMessage methods can be used to format a message associated with the given message key,
 * using the java.text.MessageFormat class and the configured ResourceBundle
 * <p/>
 * This Message Manager must be configured with either a Resource Bundle, a Resource Bundle Name, or a Message Source.
 * If using Spring configuration, the Message Source is the easiest way to configure the Message Manager.
 * If Spring configuration is not used, the DefaultRequestProcessor can be used to initialize it
 * from the JSTL init configuration parameter "javax.servlet.jsp.jstl.fmt.localizationContext"
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 1, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/07/2007       wer         Added Support for multiple Message Resource Files
 * 05/07/2007       sxm         Removed Confirmation messages from getMessages()
 * 05/09/2007       wer         Added getMessagesAndConfirmationPrompts method.
 * 05/16/2007       sxm         Replace a single quote with three quotes bafore pass to MessageFormat
 * 06/12/2007       sxm         Added defineMessageForRequest()
 * 06/28/2007       sxm         1. Inplements MessageManagerAdmin
 *                              2. Added parameter to defineMessageForRequest()
 * 07/12/2007       sxm         Added handling of confirmedAsYRequired
 * 08/29/2007       sxm         Added handling of m_messageFieldId and m_messageRowId for error messages.
 * 11/14/2007       wer         Added getMessagesAsString method
 * 04/03/2008       fcb         getAllMessagesList and setAllMessagesFromList added.
 * 04/09/2008       wer         refactord logic to initialize the resource bundle for a Servlet into initResourceBundleForServletContext() to make it reusable.
 * 07/22/2009       James       Added getResourceBundle method
 *                              Modified method setMessageSource, using our own MessageSourceResourceBundle
 *                              instead of the one in spring.
 * 05/11/2009      Blake        Modify addMessage() for issue 107296: remove all '\\n' from messages.
 * 05/26/2010       Leo         Issue 107296.
 * 09/08/2011       kshen       Added codes to handling message grid id.
 * 04/18/2012       kshen       Correct the method addErrorMessage.
 * 05/16/2012       kshen       Changed method hasMessage to also find message in the CONFIRMATION_PROMPT category.
 * 02/27/2014       Parker      Issue#149313 Problem 3 -- The duplicated error message display issue.
 * 06/28/2014       Parker      Issue#154172 Add a validation for field data type and display type.
 * 11/17/2015       Elvin       Issue 167139: add parameter messageRowId when addVerbatimMessage
 * 01/05/2016       kxiang      Issue 167306 Modified formatMessage to replace three quotes with two quotes.
 * 07/21/2016       dpang       Removed references to dwr.
 * 02/06/2017       lzhang      Issue 190834: add hasInfoNoMatchResultMessages/addInfoNoMatchResultMessage
 * ---------------------------------------------------
 */
public class MessageManagerImpl extends MessageManager implements MessageManagerAdmin {

    /**
     * Returns true if the message associated with the given message key has been added
     * to the message manager for this request. Otherwise, it returns false.
     *
     * @param messageKey, key to the message.
     */
    public boolean hasMessage(String messageKey) {

        boolean containKey = false;
        containKey = hasMessage(MessageCategory.ERROR, messageKey);
        if (!containKey) {
            containKey = hasMessage(MessageCategory.WARNING, messageKey);
        }
        if (!containKey) {
            containKey = hasMessage(MessageCategory.INFORMATION, messageKey);
        }
        if (!containKey) {
            containKey = hasMessage(MessageCategory.CONFIRMATION_PROMPT, messageKey);
        }
        return containKey;

    }

    /**
     * Returns true any error messages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public boolean hasErrorMessages() {
        return getMessages(MessageCategory.ERROR).size() > 0;
    }

    /**
     * Returns true any warning messages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public boolean hasWarningMessages() {
        return getMessages(MessageCategory.WARNING).size() > 0;
    }

    /**
     * Returns true any info messages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public boolean hasInfoMessages() {
        return getMessages(MessageCategory.INFORMATION).size() > 0;
    }

    /**
     * Returns true any info messages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public boolean hasInfoNoMatchResultMessages() {
        return getMessages(MessageCategory.INFORMATION_NO_MATCH_RESULT).size() > 0;
    }

    /**
     * Returns true any JavaScript Messages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     * This method should only be called in the UI Layer (ex. in a JSP page)
     */
    public boolean hasJsMessages() {
        return getMessages(MessageCategory.JS_MESSAGE).size() > 0;
    }

    /**
     * Returns true any essages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public boolean hasMessages() {
        return (hasErrorMessages() || hasWarningMessages() || hasInfoMessages() || hasInfoNoMatchResultMessages());
    }

    /**
     * Method to add message with given text and category.
     * @param messageText
     * @param messageCategory
     */
    public void addVerbatimMessage (String messageText, MessageCategory messageCategory){
        Map messageMap = getMessages(messageCategory);
        String messageKey = MESSAGE_KEY_PREFIX + Math.random();
        messageMap.put(messageKey, new Message(messageCategory, messageKey, messageText, false, null, null));
    }

    public void addVerbatimMessage (String messageText, String messageRowId, MessageCategory messageCategory){
        Map messageMap = getMessages(messageCategory);
        String messageKey = MESSAGE_KEY_PREFIX + Math.random();
        messageMap.put(messageKey, new Message(messageCategory, messageKey, messageText, false, null, messageRowId));
    }

    /**
     * Formats the message associated with the given message key as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     */
    public void addErrorMessage(String messageKey) {
        addErrorMessage(messageKey, (String) null, null, null);
    }

    /**
     * Formats the message associated with the given message key and related field ID as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     * @param messageFieldId, ID of the field that caused the error.
     */
    public void addErrorMessage(String messageKey, String messageFieldId) {
        addErrorMessage(messageKey, messageFieldId, null, null);
    }

    /**
     * Formats the message associated with the given message key and related field/row IDs as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId, ID of the row that caused the error..
     */
    public void addErrorMessage(String messageKey, String messageFieldId, String messageRowId) {
        addErrorMessage(messageKey, messageFieldId, messageRowId, (String) null);
    }

    /**
     * Formats the message associated with the given message key and related field/row IDs as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,     key to the message.
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId,   ID of the row that caused the error..
     */
    public void addErrorMessage(String messageKey, String messageFieldId, String messageRowId, String messageGridId) {
        addErrorMessage(messageKey, null, messageFieldId, messageRowId, messageGridId);
    }

    /**
     * Formats the message associated with the given message key and message parameters as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public void addErrorMessage(String messageKey, Object[] messageParameters) {
        addErrorMessage(messageKey, messageParameters, null);
    }

    /**
     * Formats the message associated with the given message key, message parameters and related field ID as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId, ID of the field that caused the error.
     */
    public void addErrorMessage(String messageKey, Object[] messageParameters, String messageFieldId) {
        addErrorMessage(messageKey, messageParameters, messageFieldId, null);
    }
    /**
     * Formats the message associated with the given message key, message parameters and related field/row IDs as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId, ID of the row that caused the error..
     */
    public void addErrorMessage(String messageKey, Object[] messageParameters, String messageFieldId, String messageRowId) {
        addMessage(MessageCategory.ERROR, messageKey, messageParameters, messageFieldId, messageRowId);
    }

    /**
     * Formats the message associated with the given message key, message parameters and related field/row IDs as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId,    ID of the field that caused the error.
     * @param messageRowId,      ID of the row that caused the error.
     * @param messageGridId      Id of the grid that caused the error.
     */
    public void addErrorMessage(String messageKey, Object[] messageParameters, String messageFieldId, String messageRowId, String messageGridId) {
        addMessage(MessageCategory.ERROR, messageKey, messageParameters, messageFieldId, messageRowId, messageGridId);
    }

    /**
     * Formats the message associated with the given message key as a Warning Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     */
    public void addWarningMessage(String messageKey) {
        addWarningMessage(messageKey, null);
    }

    /**
     * Formats the message associated with the given message key and message parameters as a Warning Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public void addWarningMessage(String messageKey, Object[] messageParameters) {
        addMessage(MessageCategory.WARNING, messageKey, messageParameters);
    }

    /**
     * Formats the message associated with the given message key, message parameters and related field ID as an Warning Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId, ID of the field that caused the error.
     */
    public void addWarningMessage(String messageKey, Object[] messageParameters, String messageFieldId) {
        addWarningMessage(messageKey, messageParameters, messageFieldId, null);
    }
    /**
     * Formats the message associated with the given message key, message parameters and related field/row IDs as an Warning Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId, ID of the row that caused the error..
     */
    public void addWarningMessage(String messageKey, Object[] messageParameters, String messageFieldId, String messageRowId) {
        addMessage(MessageCategory.WARNING, messageKey, messageParameters, messageFieldId, messageRowId);
    }

    /**
     * Formats the message associated with the given message key, message parameters and related field/row IDs as an Warning Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId,    ID of the field that caused the error.
     * @param messageRowId,      ID of the row that caused the error.
     * @param messageGridId      Id of the grid that caused the error.
     */
    public void addWarningMessage(String messageKey, Object[] messageParameters, String messageFieldId, String messageRowId, String messageGridId) {
        addMessage(MessageCategory.WARNING, messageKey, messageParameters, messageFieldId, messageRowId, messageGridId);
    }

    /**
     * Formats the message associated with the given message key as an Info Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey Key to the message.
     */
    public void addInfoMessage(String messageKey) {
        addInfoMessage(messageKey, null);
    }

    /**
     * Formats the message associated with the given message key and message parameters as an Info Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public void addInfoMessage(String messageKey, Object[] messageParameters) {
        addMessage(MessageCategory.INFORMATION, messageKey, messageParameters);
    }

    /**
     * Formats the message associated with the given message key as an Info Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey Key to the message.
     */
    public void addInfoNoMatchResultMessage(String messageKey) {
        addInfoNoMatchResultMessage(messageKey, null);
    }

    /**
     * Formats the message associated with the given message key and message parameters as an Info Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public void addInfoNoMatchResultMessage(String messageKey, Object[] messageParameters) {
        addMessage(MessageCategory.INFORMATION_NO_MATCH_RESULT, messageKey, messageParameters);
    }
    /**
     * Store the message associated with the given message category and message key
     * in the Message Manager for the scope of this request.
     *
     * @param messageCategory, category of the message.
     * @param messageKey, key to the message.
     * @param message,  message.
     * @param messageValue,  default value for the meesage
     */
    public void defineMessageForRequest(MessageCategory messageCategory, String messageKey, String message, String messageValue) {
        Map messageMap = getMessages(messageCategory);
        messageMap.put(messageKey, new Message(messageCategory, messageKey, message, messageValue));
    }

    /**
     * Returns an Iterator of Message objects representing all Error messages reported within the scope of this request.
     */
    public Iterator getErrorMessages() {
        return getMessages(MessageCategory.ERROR).values().iterator();
    }

    /**
     * Returns a number of Error messages reported within the scope of this request.
     */
    public int getErrorMessageCount() {
        return getMessages(MessageCategory.ERROR).values().size();
    }

    /**
     * Returns an Iterator of Message objects representing all Warning messages reported within the scope of this request.
     */
    public Iterator getWarningMessages() {
        return getMessages(MessageCategory.WARNING).values().iterator();
    }

    /**
     * Returns a number of Warning messages reported within the scope of this request.
     */
    public int getWarningMessageCount() {
        return getMessages(MessageCategory.WARNING).values().size();
    }

    /**
     * Returns an Iterator of Message objects representing all JavaScript messages reported within the scope of this request.
     * This method should only be called in the UI Layer (ex. in a JSP page)
     */
    public Iterator getJsMessages() {
        return getMessages(MessageCategory.JS_MESSAGE).values().iterator();
    }

    /**
     * Returns an Iterator of Message objects representing all Info messages reported within the scope of this request.
     */
    public Iterator getInfoMessages() {
        return getMessages(MessageCategory.INFORMATION).values().iterator();
    }

    /**
     * Returns a number of Info messages reported within the scope of this request.
     */
    public int getInfoMessageCount() {
        return getMessages(MessageCategory.INFORMATION).values().size();
    }

    /**
     * Returns an Iterator of Message objects representing all messages reported within the scope of this request.
     */
    public Iterator getMessages() {
        Map allMessages = new LinkedHashMap();
        allMessages.putAll(getMessages(MessageCategory.ERROR));
        allMessages.putAll(getMessages(MessageCategory.WARNING));
        allMessages.putAll(getMessages(MessageCategory.INFORMATION));
        allMessages.putAll(getMessages(MessageCategory.INFORMATION_NO_MATCH_RESULT));
        Iterator allMessageIterator = allMessages.values().iterator();
        return allMessageIterator;
    }

    /**
     * Returns an Iterator of Message objects representing all messages and confirmation prompts
     * reported within the scope of this request.
     */
    public Iterator getMessagesAndConfirmationPrompts() {
        Map allMessages = new LinkedHashMap();
        allMessages.putAll(getMessages(MessageCategory.ERROR));
        allMessages.putAll(getMessages(MessageCategory.WARNING));
        allMessages.putAll(getMessages(MessageCategory.INFORMATION));
        allMessages.putAll(getMessages(MessageCategory.CONFIRMATION_PROMPT));
        Iterator allMessageIterator = allMessages.values().iterator();
        return allMessageIterator;
    }

    /**
     * Returns true any Confirmation Prompts were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public boolean hasConfirmationPrompts() {
        return getMessages(MessageCategory.CONFIRMATION_PROMPT).size() > 0;
    }

    /**
     * Formats the message associated with the given message key as a Confirmation Prompt,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     */
    public void addConfirmationPrompt(String messageKey) {
        addConfirmationPrompt(messageKey, null, true);
    }

    /**
     * Formats the message associated with the given message key and message parameters as a Confirmation Prompt,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public void addConfirmationPrompt(String messageKey, Object[] messageParameters) {
        addConfirmationPrompt(messageKey, messageParameters, true);
    }

    /**
     * Formats the message associated with the given message key as a Confirmation Prompt,
     * and stores it along with the message response indicator in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param confirmedAsYRequired,  flag to indicate whether the response must be Y in order to continue
     */
    public void addConfirmationPrompt(String messageKey, boolean confirmedAsYRequired) {
        addConfirmationPrompt(messageKey, null, confirmedAsYRequired);
    }

    /**
     * Formats the message associated with the given message key and message parameters as a Confirmation Prompt,
     * and stores it along with the message response indicator in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param confirmedAsYRequired,  flag to indicate whether the response must be Y in order to continue
     */
    public void addConfirmationPrompt(String messageKey, Object[] messageParameters, boolean confirmedAsYRequired) {
        addMessage(MessageCategory.CONFIRMATION_PROMPT, messageKey, messageParameters, confirmedAsYRequired);
    }


    /**
     * Formats the message associated with the given message key, message parameters and related field/row IDs as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,           key to the message.
     * @param messageParameters,    an object array of value parameter used for formatting the message
     * @param confirmedAsYRequired, flag to indicate whether the response must be Y in order to continue
     * @param messageFieldId,       ID of the field that caused the error.
     * @param messageRowId,         ID of the row that caused the error.
     */
    public void addConfirmationPrompt(String messageKey, Object[] messageParameters, boolean confirmedAsYRequired, String messageFieldId, String messageRowId) {
        addMessage(MessageCategory.CONFIRMATION_PROMPT, messageKey, messageParameters, confirmedAsYRequired, messageFieldId, messageRowId);
    }

   

    /**
     * Formats the message associated with the given message key as a JavaScript Message,
     * and stores it in the Message Manager for the scope of this request.
     * This method should only be called in the UI Layer (ex. in a Struts Action class)
     *
     * @param messageKey, key to the message.
     */
    public void addJsMessage(String messageKey) {
        addJsMessage(messageKey,null);
    }

    /**
     * Formats the message associated with the given message key and message parameters as a JavaScript Message,
     * and stores it in the Message Manager for the scope of this request.
     * This method should only be called in the UI Layer (ex. in a Struts Action class)
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public void addJsMessage(String messageKey, Object[] messageParameters) {
       addMessage(MessageCategory.JS_MESSAGE, messageKey, messageParameters);
    }

    /**
     * Returns an Iterator of Message objects representing all Confirmation Prompts reported within the scope of this request.
     */
    public Iterator getConfirmationPrompts() {
        return getMessages(MessageCategory.CONFIRMATION_PROMPT).values().iterator();
    }

    /**
     * Format a message associated with the given message key, using the java.text.MessageFormat class.
     *
     * @param messageKey, key to the message.
     * @return String, representing the message.
     */
    public String formatMessage(String messageKey) {
        return formatMessage(messageKey, null);
    }

    /**
     * Format a message associated with the given message key, using the java.text.MessageFormat class
     * and the configured ResourceBundle.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @return String, representing the formatted message.
     */
    public String formatMessage(String messageKey, Object[] messageParameters) {
        String formattedMessage = messageKey;
        if (m_resourceBundle != null) {
            formattedMessage = m_resourceBundle.getString(messageKey);
            if (formattedMessage != null && messageParameters != null) {
                // replace a single quote with two quotes, or MessageFormat would remove it
                formattedMessage = formattedMessage.replaceAll("'", "''").replaceAll("''''''", "'''");
                formattedMessage = MessageFormat.format(formattedMessage, messageParameters);
            }
        }
        return formattedMessage;
    }


    /**
     * Get the messages as a String using the provided message separator.
     *
     * @param separator message separator
     * @return the messages as a String
     */
    public String getMessagesAsString(String separator) {
        StringBuffer buf = new StringBuffer();
        String sep = "";

        // Write Error Messages
        Iterator it = MessageManager.getInstance().getErrorMessages();
        while (it.hasNext()) {
            Message message = (Message) it.next();
            buf.append(sep).append("Error: ").append(message.getMessage());
            sep = separator;
        }

        // Write Warning Messages
        it = MessageManager.getInstance().getWarningMessages();
        while (it.hasNext()) {
            Message message = (Message) it.next();
            buf.append(sep).append("Warning: ").append(message.getMessage());
            sep = separator;
        }

        // Write Info Messages
        it = MessageManager.getInstance().getInfoMessages();
        while (it.hasNext()) {
            Message message = (Message) it.next();
            buf.append(sep).append("Info: ").append(message.getMessage());
            sep = separator;
        }
        return buf.toString();
    }

    /**
     * Configure the MessageManager with a ResourceBundle named by the fiven resource bundle name.
     *
     * @param resourceBundleBaseName, base name that indicates the resource bundle.
     */
    public void setResourceBundleBaseName(String resourceBundleBaseName) {
        m_resourceBundle = ResourceBundle.getBundle(resourceBundleBaseName, Locale.getDefault(), this.getClass().getClassLoader());
    }

    /**
     * Configure the MessageManager with the given ResourceBundle.
     *
     * @param resourceBundle, the resource bundle to use for resolving messages.
     */
    public void setResourceBundle(ResourceBundle resourceBundle) {
        m_resourceBundle = resourceBundle;
    }

    /**
     * get getResourceBundle
     * @return
     */
    public ResourceBundle getResourceBundle() {
        return m_resourceBundle;
    }

    /**
     * Configure the MessageManager with a ResourceBundle mapped to the given MessageSource.
     *
     * @param messageSource, the MessageSource to use for resolving messages.
     */
    public void setMessageSource(MessageSource messageSource) {
        m_resourceBundle = new MessageSourceResourceBundle(messageSource, Locale.getDefault());
    }

    /**
     * Checks if the MessageManager is configured with a ResourceBundle
     */
    public boolean isConfiguredWithResourceBundle() {
        return m_resourceBundle != null;
    }

    //----------------
    // private methods
    //----------------
    private boolean hasMessage(MessageCategory messageCategory, String messageKey) {
        Map messageMap = getMessages(messageCategory);
        boolean hasMessage = messageMap.containsKey(messageKey);
        if (!hasMessage) {
            Iterator iterator = messageMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if (key.startsWith(messageKey + "-")) {
                    hasMessage = true;
                    break;
                }
            }
        }
        return hasMessage;
    }

    private void addMessage(MessageCategory messageCategory, String messageKey, Object[] messageParameters) {
        addMessage(messageCategory, messageKey, messageParameters, false);
    }

    private void addMessage(MessageCategory messageCategory, String messageKey, Object[] messageParameters,
                            boolean confirmedAsYRequired) {
        addMessage(messageCategory, messageKey, messageParameters, confirmedAsYRequired, null, null);
    }

    private void addMessage(MessageCategory messageCategory, String messageKey, Object[] messageParameters,
                            String messageFieldId, String messageRowId) {
        addMessage(messageCategory, messageKey, messageParameters, false, messageFieldId, messageRowId);
    }

    private void addMessage(MessageCategory messageCategory, String messageKey, Object[] messageParameters,
                            String messageFieldId, String messageRowId, String messageGridId) {
        addMessage(messageCategory, messageKey, messageParameters, false, messageFieldId, messageRowId, messageGridId);
    }

    private void addMessage(MessageCategory messageCategory, String messageKey, Object[] messageParameters,
                            boolean confirmedAsYRequired, String messageFieldId, String messageRowId) {
        addMessage(messageCategory, messageKey, messageParameters, confirmedAsYRequired, messageFieldId, messageRowId, null);
    }

    private void addMessage(MessageCategory messageCategory, String messageKey, Object[] messageParameters,
                            boolean confirmedAsYRequired, String messageFieldId, String messageRowId, String messageGridId) {
        Map messageMap = getMessages(messageCategory);
        String formattedMessage = m_resourceBundle.getString(messageKey);
        if (messageParameters != null) {
            // replace a single quote with three quotes, or MessageFormat would remove it
            formattedMessage = formattedMessage.replaceAll("'", "'''").replaceAll("'''''''''", "'''");
            formattedMessage = MessageFormat.format(formattedMessage, messageParameters);
        }
        String mapKey = messageKey + (messageRowId == null ? "" : "-" + messageRowId);
        messageMap.put(mapKey, new Message(messageCategory, messageKey, formattedMessage, confirmedAsYRequired,
                messageFieldId, messageRowId, messageGridId));
    }

    private Map getMessages(MessageCategory messageCategory) {
        Map messageMap;
        String messageCategoryName = messageCategory.getName();
        if (RequestStorageManager.getInstance().has(messageCategoryName)) {
            messageMap = (Map) RequestStorageManager.getInstance().get(messageCategoryName);
        }
        else {
            messageMap = new LinkedHashMap();
            RequestStorageManager.getInstance().set(messageCategoryName, messageMap);
        }
        return messageMap;
    }

    /**
     * Returns a list of all messages in the Message Manager.
     * @return
     */
    public List getAllMessagesList() {
        List messagelist = new ArrayList();
        Iterator it = getErrorMessages();
        while(it.hasNext()) {
            messagelist.add(it.next());
        }
        return messagelist;
    }

    /**
     * Sets into the Message Manager messages from an input list.
     * @param messageList
     */
    public void setAllMessagesFromList(List messageList) {
        Iterator it = messageList.iterator();
        while(it.hasNext()) {
            Message message = (Message)it.next();
            addMessage(message.getMessageCategory(), message.getMessageKey(), null);
        }
    }

    /**
     * Initialize the ResourceBundel for the ServletContext.
     * @param servletContext
     */
    public void initResourceBundleForServletContext(ServletContext servletContext) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initResourceBundleForServletContext", new Object[]{servletContext});
        }

        if (!isConfiguredWithResourceBundle()) {
            if (ApplicationContext.getInstance().hasBean("messageSource")) {
                MessageSource messageSource = (MessageSource) ApplicationContext.getInstance().getBean("messageSource");
                MessageSource customerMessageSource = JstlUtils.getJstlAwareMessageSource(servletContext, messageSource);
                setResourceBundle(new MessageSourceResourceBundle(customerMessageSource, Locale.getDefault()));
                l.logp(Level.FINE, getClass().getName(), "init", "set resource bundle from the application context.");
            }
            else {
                String baseName = (String) servletContext.getInitParameter("javax.servlet.jsp.jstl.fmt.localizationContext");
                if(!StringUtils.isBlank(baseName)) {
                    l.logp(Level.FINE, getClass().getName(), "init", "baseName = " + baseName);
                    setResourceBundleBaseName(baseName);
                } else {
                    l.logp(Level.SEVERE, getClass().getName(), "init", "Improper Configuration: Unable to determine the resource bundle base name.");
                }
            }
        }
        l.exiting(getClass().getName(), "initResourceBundleForServletContext");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public MessageManagerImpl() {
    }

    private ResourceBundle m_resourceBundle;
    private final Logger l = LogUtils.getLogger(getClass());
    private static final String MESSAGE_KEY_PREFIX = "MSG_KEY_";
}
