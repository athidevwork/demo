package dti.oasis.ows.validation;

import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

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
public class StringEqualValidator extends Validator {
    public StringEqualValidator() {
    }

    public StringEqualValidator(String string1, String string2) {
        m_string1 = string1;
        m_string2 = string2;
    }

    /**
     * Do validation.
     */
    @Override
    public void validate() {
        Logger l = LogUtils.enterLog(getClass(), "validate");

        if (!StringUtils.isSame(getString1(), getString2())) {
            MessageManager.getInstance().addErrorMessage(getMessageKey(), getMessageLabels());
            throw new ValidationException("Failed to pass StringEqualValidator.");
        }
        
        l.exiting(getClass().getName(), "validate");
    }

    public String getString1() {
        return m_string1;
    }

    public void setString1(String string1) {
        m_string1 = string1;
    }

    public String getString2() {
        return m_string2;
    }

    public void setString2(String string2) {
        m_string2 = string2;
    }

    private String m_string1;
    private String m_string2;
}
