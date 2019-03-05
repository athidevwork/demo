package dti.pm.validationmgr.impl;

import dti.pm.validationmgr.dao.ValidationDAO;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.validationmgr.RecordValidator;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class implements validation of Term Duration.
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 23, 2007
 *
 * @author sma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/04/2007       sxm         Pass filed ID "termEffectiveFromDate" to addErrorMessage().
 * 09/11/2007       sxm         Allow Passing filed ID in as parameter.
 * ---------------------------------------------------
 */
public class ValidTermDurationRecordValidator implements RecordValidator {

    /**
     * Validate the given record.
     *
     * @param inputRecord    the data Record to validate
     * @return true if the record is valid; otherwise false.
     */
    public boolean validate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validate", new Object[]{inputRecord});

        boolean isValid = getValidationDAO().checkPolicyType(inputRecord).booleanValue();
        if (!isValid) {
            MessageManager.getInstance().addErrorMessage(getMessageKey(),
                new String[]{String.valueOf(inputRecord.getRecordNumber()+1)}, getFieldId());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validate", Boolean.valueOf(isValid));
        }
        return isValid;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getValidationDAO() == null)
            throw new ConfigurationException("The required property 'validationDAO' is missing.");
    }

    public ValidTermDurationRecordValidator() {
    }

    public ValidTermDurationRecordValidator(String messageKey) {
        setMessageKey(messageKey);
    }

    public ValidTermDurationRecordValidator(String messageKey, String fieldId) {
        setMessageKey(messageKey);
        setFieldId(fieldId);
    }

    public ValidationDAO getValidationDAO() {
        if (m_validationDAO == null)
            m_validationDAO = (ValidationDAO) ApplicationContext.getInstance().getBean(ValidationDAO.BEAN_NAME);
        return m_validationDAO;
    }

    public void setValidationDAO(ValidationDAO validationDAO) {
        m_validationDAO = validationDAO;
    }

    public String getMessageKey() {
        if (m_messageKey == null)
            m_messageKey = "pm.validTermDurationRecordValidator.error";
        return m_messageKey;
    }

    public void setMessageKey(String messageKey) {
        m_messageKey = messageKey;
    }

    public String getFieldId() {
        if (m_fieldId == null)
            m_fieldId = "termEffectiveFromDate";
        return m_fieldId;
    }

    public void setFieldId(String fieldId) {
        m_fieldId = fieldId;
    }

    private ValidationDAO m_validationDAO;
    private String m_messageKey;
    private String m_fieldId;
}
