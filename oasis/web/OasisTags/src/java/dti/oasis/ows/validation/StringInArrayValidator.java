package dti.oasis.ows.validation;

import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   10/28/14
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
public class StringInArrayValidator extends Validator {
    public StringInArrayValidator() {
    }

    public StringInArrayValidator(String[] stringArray, String value) {
        m_stringArray = stringArray;
        m_value = value;
    }

    /**
     * Do validation.
     */
    @Override
    public void validate() {
        Logger l = LogUtils.enterLog(getClass(), "validate");

        boolean found = false;
        
        if (getStringArray() != null) {
            for (String tempValue : getStringArray()) {
                if (getValue() == null) {
                    if (tempValue == null) {
                        found = true;
                        break;
                    }
                } else if (getValue().equals(tempValue)) {
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            MessageManager.getInstance().addErrorMessage(getMessageKey(), getMessageLabels());
            throw new ValidationException("Failed to pass the StringInArrayValidator.");
        }

        l.exiting(getClass().getName(), "validate");
    }

    public String[] getStringArray() {
        return m_stringArray;
    }

    public void setStringArray(String[] stringArray) {
        m_stringArray = stringArray;
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(String value) {
        m_value = value;
    }

    private String[] m_stringArray;
    private String m_value;
}
