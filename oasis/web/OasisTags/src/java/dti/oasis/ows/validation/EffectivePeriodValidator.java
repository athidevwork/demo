package dti.oasis.ows.validation;

import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;

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
public class EffectivePeriodValidator extends Validator {
    public EffectivePeriodValidator() {
    }

    public EffectivePeriodValidator(String startDate, String endDate) {
        m_startDate = startDate;
        m_endDate = endDate;
    }

    @Override
    public void validate() {
        Logger l = LogUtils.enterLog(getClass(), "validate");

        if (DateUtils.isDate2AfterDate1(m_endDate, m_startDate) == 'Y') {
            MessageManager.getInstance().addErrorMessage(getMessageKey(), getMessageLabels());
            throw new ValidationException("Failed to pass Effective Period Validator.");
        }

        l.exiting(getClass().getName(), "validate");
    }

    public String getStartDate() {
        return m_startDate;
    }

    public void setStartDate(String startDate) {
        m_startDate = startDate;
    }

    public String getEndDate() {
        return m_endDate;
    }

    public void setEndDate(String endDate) {
        m_endDate = endDate;
    }

    private String m_startDate;
    private String m_endDate;
}
