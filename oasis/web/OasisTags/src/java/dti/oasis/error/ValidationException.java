package dti.oasis.error;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 20, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/22/2007       gjlong     added logic for m_validFields record
 * 
 * ---------------------------------------------------
 */
public class ValidationException extends ExpectedException {
    /**
     * The default Message Key if none is supplied.
     */
    public static final String VALIDATION_ERROR = "appException.validation.error";

    /**
     * Construct this ValidationException with an empty debug message.
     * VALIDATION_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     */
    public ValidationException() {
        super(VALIDATION_ERROR, "");
    }
    /**
     * Construct this ValidationException with the given debug message.
     * VALIDATION_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param debugMessage a debug message
     */
    public ValidationException(String debugMessage) {
        super(VALIDATION_ERROR, debugMessage);
    }

    /**
     * Construct this ValidationException with the given debug message.
     * @param messageKey
     * @param debugMessage
     * @param parameters
     */
    public ValidationException(String messageKey, String debugMessage, String[] parameters) {
        super(messageKey,debugMessage,parameters);
    }

    /**
     *
     *  @return validFields record that is contained within this validationException object
     */
    public Record getValidFields() {
        return m_validFields;
    }

    /**
     * This method sets the given validFields for this ValidationException into the contained ValidFields Record.
     *
     * @param validFields: a record containing valid fields to be set for this validationException object
     * @return void
     */
    public void setValidFields(Record validFields) {
        m_validFields.setFields(validFields);
    }

    /**
     * Sets the given valid field value for this ValidationException into the contained ValidFields Record
     *
     * @param fieldName the string fieldName to be added
     * @param value     the Object to be added
     */
    public void setValidFieldValue(String fieldName, Object value) {
        m_validFields.setFieldValue(fieldName, value);
    }

    /**
     * Returns true if the row where the Validation Exception occurred was set. Otherwise, false. 
     */
    public boolean didValidationExceptionOccurOnARow() {
        return m_rowWhereValidationExceptionOccurred != null;
    }
    
    /**
     * Returns the row where the Validation Exception occurred.
     * @throws IllegalStateException if there is no row set for this ValidationException.
     */
    public int getRowWhereValidationExceptionOccurred() {
        if (m_rowWhereValidationExceptionOccurred == null) {
            throw new IllegalStateException("There is not a row set for this ValidationException.");
        }
        return m_rowWhereValidationExceptionOccurred.intValue();
    }

    /**
     * Set the row where the ValidationException occurred
     */
    public void setRowWhereValidationExceptionOccurred(int rowWhereValidationExceptionOccurred) {
        m_rowWhereValidationExceptionOccurred = new Integer(rowWhereValidationExceptionOccurred);
    }

    /**
     * A validationException can contain a record (fieldName-fieldValue pair) that later is retrieved
     * for whatever reason. an example of this usage is: set the value in a business component, and
     * retrieve it in a Action class when validationException is caught. and forward the message to
     * the web page callers
     *
     */
    private Record m_validFields = new Record();
    private Integer m_rowWhereValidationExceptionOccurred;
}
