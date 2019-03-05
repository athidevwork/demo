package dti.pm.policymgr;

import dti.oasis.util.FormatUtils;

import java.util.Date;

/**
 * This class represents a Term for a Policy.
 *
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 9, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class Term {

    public String getPolicyTermHistoryId() {
        return m_policyTermHistoryId;
    }

    public void setPolicyTermHistoryId(String policyTermHistoryId) {
        m_policyTermHistoryId = policyTermHistoryId;
    }

    public String getTermBaseRecordId() {
        return m_termBaseRecordId;
    }

    public void setTermBaseRecordId(String termBaseRecordId) {
        m_termBaseRecordId = termBaseRecordId;
    }

    public String getEffectiveFromDate() {
        return m_effectiveFromDate;
    }

    public void setEffectiveFromDate(String effectiveFromDate) {
        m_effectiveFromDate = effectiveFromDate;
    }

    public String getEffectiveToDate() {
        return m_effectiveToDate;
    }

    public void setEffectiveToDate(String effectiveToDate) {
        m_effectiveToDate = effectiveToDate;
    }


    public boolean isEndorsementQuoteExists() {
        return m_endorsementQuoteExists;
    }

    public void setEndorsementQuoteExists(boolean endorsementQuoteExists) {
        m_endorsementQuoteExists = endorsementQuoteExists;
    }

    public boolean isRenewalQuoteExists() {
        return m_renewalQuoteExists;
    }

    public void setRenewalQuoteExists(boolean renewalQuoteExists) {
        m_renewalQuoteExists = renewalQuoteExists;
    }

    public boolean isOfficialExists() {
        return m_officialExists;
    }

    public void setOfficialExists(boolean officialExists) {
        m_officialExists = officialExists;
    }

    public boolean isWipExists() {
        return m_wipExists;
    }

    public void setWipExists(boolean wipExists) {
        m_wipExists = wipExists;
    }

    public String toString() {
        return "Term{" +
            "m_policyTermHistoryId='" + m_policyTermHistoryId + '\'' +
            ", m_termBaseRecordId='" + m_termBaseRecordId + '\'' +
            ", m_effectiveFromDate='" + m_effectiveFromDate + '\'' +
            ", m_effectiveToDate='" + m_effectiveToDate  +'\'' +
            ", m_endorsementQuoteExists='"+m_endorsementQuoteExists +'\'' +
            ", m_renewalQuoteExists='"+m_renewalQuoteExists  +
            '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Term term = (Term) o;

        if (!m_policyTermHistoryId.equals(term.m_policyTermHistoryId)) return false;

        return true;
    }

    public int hashCode() {
        return m_policyTermHistoryId.hashCode();
    }

    private String m_policyTermHistoryId;
    private String m_termBaseRecordId;
    private String m_effectiveFromDate;
    private String m_effectiveToDate;
    private boolean m_endorsementQuoteExists;
    private boolean m_renewalQuoteExists;
    private boolean m_officialExists;
    private boolean m_wipExists;

}
