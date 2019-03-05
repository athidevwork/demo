package dti.oasis.ows.validation;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   10/16/14
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
public abstract class Validator {
    /**
     * Get a validator to check if Date2 is after Date1.
     *
     * @param date1 The Date 1.
     * @param date2 The Date 2.
     * @param messageKey The message key for the validation message.
     * @param messageLabels The labels will be used to display in validation message.
     * @return An instance of Effective Period Validator.
     */
    public static Validator getDate2AfterDate1Validator(String date1, String date2, String messageKey, String[] messageLabels) {
        Validator validator = new Date2AfterDate1Validator(date1, date2);
        validator.setMessageParams(messageKey, messageLabels);
        return validator;
    }

    /**
     * Get a validator to check if Date2 equals or is after Date1.
     * @param date1
     * @param date2
     * @param messageKey
     * @param messageLabels
     * @return
     */
    public static Validator getDate2EqualOrAfterDate1Validator(String date1, String date2, String messageKey, String[] messageLabels) {
        Validator validator = new Date2EqualOrAfterDate1Validator(date1, date2);
        validator.setMessageParams(messageKey, messageLabels);
        return validator;
    }

    /**
     * Get an instance of Effective Period Validator.
     *
     * @param startDate The start date.
     * @param endDate The end date.
     * @param messageKey The message key for the validation message.
     * @param messageLabels The labels will be used to display in validation message.
     * @return An instance of Effective Period Validator.
     */
    public static Validator getEffectivePeriodValidator(String startDate, String endDate, String messageKey, String[] messageLabels) {
        Validator validator = new EffectivePeriodValidator(startDate, endDate);
        validator.setMessageParams(messageKey, messageLabels);
        return validator;
    }

    /**
     * Get a validator to check a required field value.
     *
     * @param fieldValue The value of the field.
     * @param messageKey The message key for the validation message.
     * @param messageLabel The label will be displayed in validation message.
     * @return An instance of field required validator.
     */
    public static Validator getFieldRequiredValidator(String fieldValue, String messageKey, String messageLabel) {
        Validator validator = new FieldRequiredValidator(fieldValue);
        validator.setMessageParams(messageKey, new String[]{messageLabel});
        return validator;
    }

    /**
     * Get a validator to check if string1 equals string2.
     * @param string1
     * @param string2
     * @param messageKey
     * @param messageLabels
     * @return
     */
    public static Validator getStringEqualValidator(String string1, String string2, String messageKey, String[] messageLabels) {
        Validator validator = new StringEqualValidator(string1, string2);
        validator.setMessageParams(messageKey, messageLabels);
        return validator;
    }

    /**
     * Get validator for checking if a string is in a array.
     * @param stringArray
     * @param value
     * @param messageKey
     * @param messageLabels
     * @return
     */
    public static Validator getStringInArrayValidator(String[] stringArray, String value, String messageKey, String[] messageLabels) {
        Validator validator = new StringInArrayValidator(stringArray, value);
        validator.setMessageParams(messageKey, messageLabels);
        return validator;
    }

    /**
     * Validate if required field is empty.
     *
     * @param fieldValue The value of the field.
     * @param messageLabel The label will be displayed in validation message.
     * @param messageKey The message key for the validation message.
     */
    public static void validateFieldRequired(String fieldValue, String messageLabel, String messageKey) {
        getFieldRequiredValidator(fieldValue, messageLabel, messageKey).validate();
    }

    /**
     * Validate effective period
     *
     * @param startDate The start date.
     * @param endDate The end date.
     * @param messageLabels The labels will be used to display in validation message.
     * @param messageKey The message key for the validation message.
     */
    public static void validateEffectivePeriod(String startDate, String endDate, String messageKey, String[] messageLabels) {
        getEffectivePeriodValidator(startDate, endDate, messageKey, messageLabels).validate();
    }

    /**
     * Validate if Date2 is after Date 1.
     *
     * @param date1 The Date 1.
     * @param date2 The Date 2.
     * @param messageLabels The labels will be used to display in validation message.
     * @param messageKey The message key for the validation message.
     */
    public static void validateDate2AfterDate1(String date1, String date2, String messageKey, String[] messageLabels) {
        getDate2AfterDate1Validator(date1, date2, messageKey, messageLabels).validate();
    }

    /**
     * Validate if Date2 is after Date 1.
     *
     * @param date1 The Date 1.
     * @param date2 The Date 2.
     * @param messageLabels The labels will be used to display in validation message.
     * @param messageKey The message key for the validation message.
     */
    public static void validateDate2EqualOrAfterDate1(String date1, String date2, String messageKey, String[] messageLabels) {
        getDate2EqualOrAfterDate1Validator(date1, date2, messageKey, messageLabels).validate();
    }

    /**
     * Validate if string 1 equals string 2.
     * @param string1
     * @param string2
     * @param messageKey
     * @param messageLabels
     */
    public static void validateStringEqual(String string1, String string2, String messageKey, String[] messageLabels) {
        getStringEqualValidator(string1, string2, messageKey, messageLabels).validate();
    }
    
    /**
     * Validate if a string is in a array.
     * @param stringArray
     * @param value
     * @param messageKey
     * @param messageLabels
     */
    public static void validateStringInArray(String[] stringArray, String value, String messageKey, String[] messageLabels) {
        getStringInArrayValidator(stringArray, value, messageKey, messageLabels).validate();
    }
    
    /**
     * Do validation.
     */
    public abstract void validate();

    protected void setMessageParams(String messageKey, String[] messageLabels) {
        this.setMessageKey(messageKey);
        this.setMessageLabels(messageLabels);
    }

    protected String getMessageLabel() {
        if (messageLabels != null && messageLabels.length > 0) {
            return messageLabels[0];
        } else {
            return null;
        }
    }

    protected void setMessageLabel(String messageLabel) {
        this.messageLabels = new String[]{messageLabel};
    }


    protected String[] getMessageLabels() {
        return messageLabels;
    }

    protected void setMessageLabels(String[] messageLabels) {
        this.messageLabels = messageLabels;
    }

    protected String getMessageKey() {
        return messageKey;
    }

    protected void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    private String[] messageLabels;
    private String messageKey;
}
