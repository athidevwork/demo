package dti.oasis.ows.validation;

import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.DateUtils;
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
public class Date2EqualOrAfterDate1Validator extends Validator {
    public Date2EqualOrAfterDate1Validator() {
    }

    public Date2EqualOrAfterDate1Validator(String date1, String date2) {
        m_date1 = date1;
        m_date2 = date2;
    }

    /**
     * Do validation.
     */
    @Override
    public void validate() {
        Logger l = LogUtils.enterLog(getClass(), "validate");

        if (DateUtils.isDate2AfterDate1(getDate2(), getDate1()) == 'Y') {
            MessageManager.getInstance().addErrorMessage(getMessageKey(), getMessageLabels());
            throw new ValidationException("Failed to pass Date2EqualOrAfterDate1Validator");
        }

        l.exiting(getClass().getName(), "validate");
    }

    public String getDate1() {
        return m_date1;
    }

    public void setDate1(String date1) {
        m_date1 = date1;
    }

    public String getDate2() {
        return m_date2;
    }

    public void setDate2(String date2) {
        m_date2 = date2;
    }

    private String m_date1;
    private String m_date2;
}
