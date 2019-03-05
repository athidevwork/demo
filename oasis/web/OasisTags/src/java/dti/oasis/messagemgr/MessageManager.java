package dti.oasis.messagemgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.messagemgr.impl.MessageManagerImpl;
import org.springframework.context.MessageSource;

import java.util.Iterator;
import java.util.ResourceBundle;
import java.io.Writer;

/**
 * This class manages the messages and confirmation prompts within the scope of a single request.
 * <p/>
 * All the add methods resolves the messageKey into formatted message using the java.text.MessageFormat class
 * and the configured ResourceBundle. If this Message Manager was configured with a MessageSource,
 * it is adapted into a ResourceBundle. If an object array of messageParameters are provided to the add method,
 * the values are used for formatting the final message. These messages are stored for the current request.
 * <p/>
 * The formatMessage methods can be used to format a message associated with the given message key,
 * using the java.text.MessageFormat class and the configured ResourceBundle.
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
 * 06/29/2007       sxm         Removed "implmenets MessageManagerAdmin"
 * 07/12/2007       sxm         Added handling of confirmedAsYRequired
 * 08/29/2007       sxm         Added handling of m_messageFieldId and m_messageRowId for error messages.
 * 11/14/2007       wer         Added getMessagesAsString method
 * 07/22/2009       James       Added getResourceBundle method
 * 09/11/2011       kshen       Added handling message grid id.
 * 06/28/2014       Parker      Issue#154172 Add a validation for field data type and display type.
 * 11/17/2015       Elvin       Issue 167139: add parameter messageRowId when addVerbatimMessage
 * 02/06/2017       lzhang      Issue 190834: add hasInfoNoMatchResultMessages/addInfoNoMatchResultMessage
 * ---------------------------------------------------
 */
public abstract class MessageManager {

    public static final String BEAN_NAME = "MessageManager";

    /**
     * Returns an instance of message manager.
     *
     * @return MessageManager, an instance of message manager with implemenation information.
     */
    public synchronized static final MessageManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(MessageManager.BEAN_NAME)) {
                c_instance = (MessageManager) ApplicationContext.getInstance().getBean(MessageManager.BEAN_NAME);
            }
            else {
                c_instance = new MessageManagerImpl();
            }
        }
        return c_instance;
    }

    /**
     * Returns true if the message associated with the given message key has been added
     * to the Message Manager for this request. Otherwise, it returns false.
     *
     * @param messageKey, key to the message.
     */
    public abstract boolean hasMessage(String messageKey);

    /**
     * Returns true any error messages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public abstract boolean hasErrorMessages();

    /**
     * Returns true any warning messages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public abstract boolean hasWarningMessages();

    /**
     * Returns true any info messages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public abstract boolean hasInfoMessages();

    /**
     * Returns true any messages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public abstract boolean hasMessages();

    /**
     * Returns true any info messages with NoMatchResult were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public abstract boolean hasInfoNoMatchResultMessages();
    /**
     * Formats the message associated with the given message key as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     */
    public abstract void addErrorMessage(String messageKey);

    /**
     * Formats the message associated with the given message key and related field ID as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     * @param messageFieldId, ID of the field that caused the error.
     */
    public abstract void addErrorMessage(String messageKey, String messageFieldId);

    /**
     * Formats the message associated with the given message key and related field/row IDs as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId, ID of the row that caused the error..
     */
    public abstract void addErrorMessage(String messageKey, String messageFieldId, String messageRowId);

    /**
     * Formats the message associated with the given message key and related field/row IDs as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId, ID of the row that caused the error..
     */
    public abstract void addErrorMessage(String messageKey, String messageFieldId, String messageRowId, String messageGridId);

    /**
     * Formats the message associated with the given message key and message parameters as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public abstract void addErrorMessage(String messageKey, Object[] messageParameters);

    /**
     * Formats the message associated with the given message key, message parameters and related field ID as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId, ID of the field that caused the error.
     */
    public abstract void addErrorMessage(String messageKey, Object[] messageParameters, String messageFieldId);

    /**
     * Formats the message associated with the given message key, message parameters and related field/row IDs as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId, ID of the row that caused the error.
     */
    public abstract void addErrorMessage(String messageKey, Object[] messageParameters, String messageFieldId, String messageRowId);

    /**
     * Formats the message associated with the given message key, message parameters and related field/row IDs as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId, ID of the row that caused the error.
     * @param messageGridId Id of the grid that caused the error.
     */
    public abstract void addErrorMessage(String messageKey, Object[] messageParameters, String messageFieldId, String messageRowId, String messageGridId);

    /**
     * Formats the message associated with the given message key as a Warning Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     */
    public abstract void addWarningMessage(String messageKey);
    /**
     * Formats the message associated with the given message key and message parameters as a Warning Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public abstract void addWarningMessage(String messageKey, Object[] messageParameters);

    /**
     * Formats the message associated with the given message key, message parameters and related field ID as an Warning Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId, ID of the field that caused the error.
     */
    public abstract void addWarningMessage(String messageKey, Object[] messageParameters, String messageFieldId);

    /**
     * Formats the message associated with the given message key, message parameters and related field/row IDs as an Warning Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId, ID of the row that caused the error.
     */
    public abstract void addWarningMessage(String messageKey, Object[] messageParameters, String messageFieldId, String messageRowId);

    /**
     * Formats the message associated with the given message key, message parameters and related field/row IDs as an Warning Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId, ID of the row that caused the error.
     * @param messageGridId Id of the grid that caused the error.
     */
    public abstract void addWarningMessage(String messageKey, Object[] messageParameters, String messageFieldId, String messageRowId, String messageGridId);

    /**
     * Formats the message associated with the given message key as a JavaScript Message,
     * and stores it in the Message Manager for the scope of this request.
     * This method should only be called in the UI Layer (ex. in a Struts Action class)*
     * @param messageKey, key to the message.
     */
    public abstract void addJsMessage(String messageKey);

    /**
     * Formats the message associated with the given message key and message parameters as a JavaScript Message,
     * and stores it in the Message Manager for the scope of this request.
     * This method should only be called in the UI Layer (ex. in a Struts Action class)*
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public abstract void addJsMessage(String messageKey, Object[] messageParameters);

    /**
     * Formats the message associated with the given message key as an Info Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey Key to the message.
     */
    public abstract void addInfoMessage(String messageKey);

    /**
     * Formats the message associated with the given message key and message parameters as an Info Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public abstract void addInfoMessage(String messageKey, Object[] messageParameters);

    /**
     * Formats the message associated with the given message key as an Info Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey Key to the message.
     */
    public abstract void addInfoNoMatchResultMessage(String messageKey);

    /**
     * Formats the message associated with the given message key and message parameters as an Info Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public abstract void addInfoNoMatchResultMessage(String messageKey, Object[] messageParameters);

    /**
     * Returns an Iterator of Message objects representing all Error messages reported within the scope of this request.
     */
    public abstract Iterator getErrorMessages();

    /**
     * Returns a number of Error messages reported within the scope of this request.
     */
    public abstract int getErrorMessageCount();

    /**
     * Returns an Iterator of Message objects representing all Warning messages reported within the scope of this request.
     */
    public abstract Iterator getWarningMessages();

    /**
     * Returns a number of Warning messages reported within the scope of this request.
     */
    public abstract int getWarningMessageCount();

    /**
     * Returns an Iterator of Message objects representing all Info messages reported within the scope of this request.
     */
    public abstract Iterator getInfoMessages();

    /**
     * Returns a number of Info messages reported within the scope of this request.
     */
    public abstract int getInfoMessageCount();

    /**
     * Returns an Iterator of Message objects representing all JavaScript messages reported within the scope of this request.
     * This method should only be called in the UI Layer (ex. in a JSP page)
     */
    public abstract Iterator getJsMessages();

    /**
     * Returns an Iterator of Message objects representing all messages reported within the scope of this request.
     */
    public abstract Iterator getMessages();

    /**
     * Returns an Iterator of Message objects representing all messages and confirmation prompts
     * reported within the scope of this request.
     */
    public abstract Iterator getMessagesAndConfirmationPrompts();

    /**
     * Returns true any Confirmation Prompts were added to the Message Manager for this request.
     * Otherwise, it returns false.
     */
    public abstract boolean hasConfirmationPrompts();

    /**
     * Returns true any JavaScript Messages were added to the Message Manager for this request.
     * Otherwise, it returns false.
     * This method should only be called in the UI Layer (ex. in a JSP page)"
     *
     */
    public abstract boolean hasJsMessages();

    /**
     * Formats the message associated with the given message key as a Confirmation Prompt,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey, key to the message.
     */
    public abstract void addConfirmationPrompt(String messageKey);

    /**
     * Formats the message associated with the given message key and message parameters as a Confirmation Prompt,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     */
    public abstract void addConfirmationPrompt(String messageKey, Object[] messageParameters);

    /**
     * Formats the message associated with the given message key as a Confirmation Prompt,
     * and stores it along with the message response indicator in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param confirmedAsYRequired,  flag to indicate whether the response must be Y in order to continue
     */
    public abstract void addConfirmationPrompt(String messageKey, boolean confirmedAsYRequired);

    /**
     * Formats the message associated with the given message key and message parameters as a Confirmation Prompt,
     * and stores it along with the message response indicator in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param confirmedAsYRequired,  flag to indicate whether the response must be Y in order to continue
     */
    public abstract void addConfirmationPrompt(String messageKey, Object[] messageParameters, boolean confirmedAsYRequired);

    /**
     * Formats the message associated with the given message key, message parameters and related field/row IDs as an Error Message,
     * and stores it in the Message Manager for the scope of this request.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @param confirmedAsYRequired,  flag to indicate whether the response must be Y in order to continue
     * @param messageFieldId, ID of the field that caused the error.
     * @param messageRowId, ID of the row that caused the error.
     */
    public abstract void addConfirmationPrompt(String messageKey, Object[] messageParameters, boolean confirmedAsYRequired, String messageFieldId, String messageRowId);

    /**
     * Returns an Iterator of Message objects representing all Confirmation Prompts reported within the scope of this request.
     */
    public abstract Iterator getConfirmationPrompts();

    /**
     * Format a message associated with the given message key, using the java.text.MessageFormat class.
     *
     * @param messageKey, key to the message.
     * @return String, representing the message.
     */
    public abstract String formatMessage(String messageKey);

    /**
     * Format a message associated with the given message key, using the java.text.MessageFormat class
     * and the configured ResourceBundle.
     *
     * @param messageKey,        key to the message.
     * @param messageParameters, an object array of value parameter used for formatting the message
     * @return String, representing the formatted message.
     */
    public abstract String formatMessage(String messageKey, Object[] messageParameters);

    /**
     * Get the messages as a String using the provided message separator.
     *
     * @param separator message separator
     * @return the messages as a String
     */
    public abstract String getMessagesAsString(String separator);

    /**
     * Configure the MessageManager with a ResourceBundle named by the fiven resource bundle name.
     *
     * @param resourceBundleBaseName, base name that indicates the resource bundle.
     */
    public abstract void setResourceBundleBaseName(String resourceBundleBaseName);

    /**
     * Configure the MessageManager with the given ResourceBundle.
     *
     * @param resourceBundle, the resource bundle to use for resolving messages.
     */
    public abstract void setResourceBundle(ResourceBundle resourceBundle);

    /**
     * get ResourceBundle.
     */
    public abstract ResourceBundle getResourceBundle();

    /**
     * Configure the MessageManager with a ResourceBundle mapped to the given MessageSource.
     *
     * @param messageSource, the MessageSource to use for resolving messages.
     */
    public abstract void setMessageSource(MessageSource messageSource);

    /**
     * Checks if the MessageManager is configured with a ResourceBundle
     */
    public abstract boolean isConfiguredWithResourceBundle();

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    protected MessageManager() {
    }
    //Method will be used to create message from supplied text
    public abstract void addVerbatimMessage (String messageText, MessageCategory messageCategory);

    public abstract void addVerbatimMessage (String messageText, String messageRowId, MessageCategory messageCategory);

    private static MessageManager c_instance;
}
