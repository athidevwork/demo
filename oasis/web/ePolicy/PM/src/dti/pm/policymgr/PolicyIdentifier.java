package dti.pm.policymgr;

import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PolicyViewMode;

/**
 * This class provides the policy identifier information, that will be used both by web and web service
 * 
 * <p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/18/2007       fcb         m_lockedByOtherSession added.
 * 01/02/2008       fcb         m_lockedByOtherSession removed, wipB only will be used.
 * ---------------------------------------------------
 */
public class PolicyIdentifier {

    public String getPolicyNo() {
        return m_policyNo;
    }

    public void setPolicyNo(String policyNo) {
        m_policyNo = policyNo;
    }

    public String getPolicyId() {
        return m_policyId;
    }

    public void setPolicyId(String policyId) {
        m_policyId = policyId;
    }

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

    public String getPolicyWipNumber() {
        return m_policyWipNumber;
    }

    public void setPolicyWipNumber(String policyWipNumber) {
        m_policyWipNumber = policyWipNumber;
    }

    public String getPolicyOffNumber() {
        return m_policyOffNumber;
    }

    public void setPolicyOffNumber(String policyOffNumber) {
        m_policyOffNumber = policyOffNumber;
    }

    public String getPolicyLockId() {
        return m_policyLockId;
    }

    public void setPolicyLockId(String policyLockId) {
        m_policyLockId = policyLockId;
    }

    public boolean ownLock() {
        return !StringUtils.isBlank(getPolicyLockId());
    }

    public String getPolicyLockMessage() {
        return m_policyLockMessage;
    }

    public void setPolicyLockMessage(String policyLockMessage) {
        m_policyLockMessage = policyLockMessage;
    }

    public PolicyViewMode getPolicyViewMode() {
        return m_policyViewMode;
    }

    public void setPolicyViewMode(PolicyViewMode policyViewMode) {
        m_policyViewMode = policyViewMode;
    }

    public String getEndorsementQuoteId() {
        return m_endorsementQuoteId;
    }

    public void setEndorsementQuoteId(String endorsementQuoteId) {
        m_endorsementQuoteId = endorsementQuoteId;
    }

    public String toString() {
        return "PolicyIdentifier{" +
            "m_policyNo='" + m_policyNo + '\'' +
            ", m_policyId='" + m_policyId + '\'' +
            ", m_policyTermHistoryId='" + m_policyTermHistoryId + '\'' +
            ", m_termBaseRecordId='" + m_termBaseRecordId + '\'' +
            ", m_policyWIPNumber='" + m_policyWipNumber + '\'' +
            ", m_policyOFFNumber='" + m_policyOffNumber + '\'' +
            ", m_policyLockId='" + m_policyLockId + '\'' +
            ", m_policyLockMessage='" + m_policyLockMessage + '\'' +
            ", m_policyViewMode='" + m_policyViewMode + '\'' +
            ", m_endorsementQuoteId='" + m_endorsementQuoteId + '\'' +
            '}';
    }

    private String m_policyNo;
    private String m_policyId;
    private String m_policyTermHistoryId;
    private String m_termBaseRecordId;
    private String m_policyWipNumber;
    private String m_policyOffNumber;
    private String m_policyLockId;
    private String m_policyLockMessage;
    private String m_endorsementQuoteId;
    private PolicyViewMode m_policyViewMode;
}
