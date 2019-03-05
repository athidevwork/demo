package dti.oasis.ows.validation;

import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   10/17/14
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
public class FieldRequiredValidator extends Validator {
    public FieldRequiredValidator() {
    }

    public FieldRequiredValidator(String fieldValue) {
        this.m_fieldValue = fieldValue;
    }
    
    @Override
    public void validate() {
        Logger l = LogUtils.enterLog(getClass(), "validate");

        if (StringUtils.isBlank(getFieldValue())) {
            MessageManager.getInstance().addErrorMessage(getMessageKey(), getMessageLabels());
            throw new ValidationException("Failed to pass Field Required Validator");
        }

        l.exiting(getClass().getName(), "validate");
    }

    public String getFieldValue() {
        return m_fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        m_fieldValue = fieldValue;
    }

    private String m_fieldValue;
}
