package dti.oasis.messagemgr;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * This class extends EnumType abstract class to provide enum capability for message category used by message bean.
 * The enumerated type represents ERROR, WARNING or INFORMATION for message category. The getInstance methods is a
 * convenience method for parsing string into enumerated message category.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 6, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/06/2017       lzhang      Issue 190834: add INFORMATION_NO_MATCH_RESULT
 * ---------------------------------------------------
 */
public class MessageCategory extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int ERROR_VALUE = getNextIntValue();
    public final static String ERROR_MESSAGE_STRING = "ERROR_MESSAGE";
    public final static MessageCategory ERROR = new MessageCategory(ERROR_VALUE, "ERROR_MESSAGE");

    public final static int WARNING_VALUE = getNextIntValue();
    public final static String WARNING_MESSAGE_STRING = "WARNING_MESSAGE";
    public final static MessageCategory WARNING = new MessageCategory(WARNING_VALUE, "WARNING_MESSAGE");

    public final static int INFORMATION_VALUE = getNextIntValue();
    public final static String INFORMATION_MESSAGE_STRING = "INFORMATION_MESSAGE";
    public final static MessageCategory INFORMATION = new MessageCategory(INFORMATION_VALUE, "INFORMATION_MESSAGE");

    public final static int CONFIRMATION_PROMPT_VALUE = getNextIntValue();
    public final static String CONFIRMATION_PROMPT_STRING = "CONFIRMATION_PROMPT";
    public final static MessageCategory CONFIRMATION_PROMPT = new MessageCategory(CONFIRMATION_PROMPT_VALUE, "CONFIRMATION_PROMPT");

    public final static int JS_MESSAGE_VALUE = getNextIntValue();
    public final static String JS_MESSAGE_STRING = "JS_MESSAGE";
    public final static MessageCategory JS_MESSAGE = new MessageCategory(JS_MESSAGE_VALUE, "JS_MESSAGE");

    public final static int INFORMATION_NO_MATCH_RESULT_VALUE = getNextIntValue();
    public final static String INFORMATION_NO_MATCH_RESULT_MESSAGE_STRING = "INFORMATION_NO_MATCH_RESULT_MESSAGE";
    public final static MessageCategory INFORMATION_NO_MATCH_RESULT = new MessageCategory(INFORMATION_NO_MATCH_RESULT_VALUE, "INFORMATION_NO_MATCH_RESULT_MESSAGE");
    /**
     * Returns an instance of MessageCategory for the provided message category string.
     * @param messageCategory, a string that represents the message category
     * @return MessageCategory, a new instance of message category.
     */
    public static MessageCategory getInstance(String messageCategory) {
        MessageCategory result = (MessageCategory) c_validTypes.get(messageCategory.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The messageCategory '" + messageCategory + "' is not a valid MessageCategory.");
        }
        return result;
    }

    public boolean isError() {
        return intValue() == ERROR_VALUE;
    }

    public boolean isWarning() {
        return intValue() == WARNING_VALUE;
    }

    public boolean isInformation() {
        return intValue() == INFORMATION_VALUE;
    }

    public boolean isConfirmationPrompt() {
        return intValue() == CONFIRMATION_PROMPT_VALUE;
    }

    public boolean isJsMessage() {
        return intValue() == JS_MESSAGE_VALUE;
    }


    /**
     * Method that sets up the message category enum
     * @param value
     * @param name
     */
    public MessageCategory(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
        this.category = name;
        if(category.equals(MessageCategory.CONFIRMATION_PROMPT_STRING))
            confirmationPrompt = true;
        if(category.equals(MessageCategory.ERROR_MESSAGE_STRING))
            error = true;
        if(category.equals(MessageCategory.INFORMATION_MESSAGE_STRING))
            information = true;
        if(category.equals(MessageCategory.WARNING_MESSAGE_STRING))
            warning = true;
        if(category.equals(MessageCategory.JS_MESSAGE_STRING))
            jsMessage = true;
        if(category.equals(MessageCategory.INFORMATION_NO_MATCH_RESULT))
            informationNoMatchResult = true;
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public MessageCategory() {
    }

    private String category;
    private boolean error = false;
    private boolean warning = false;
    private boolean information = false;
    private boolean confirmationPrompt = false;
    private boolean jsMessage = false;
    private boolean informationNoMatchResult = false;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public void setInformation(boolean information) {
        this.information = information;
    }

    public void setConfirmationPrompt(boolean confirmationPrompt) {
        this.confirmationPrompt = confirmationPrompt;
    }

    public void setJsMessage(boolean jsMessage) {
        this.jsMessage = jsMessage;
    }

    public void setInformationNoMatchResult(boolean informationNoMatchResult) {
        this.informationNoMatchResult = informationNoMatchResult;
    }
}
