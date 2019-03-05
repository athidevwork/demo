package dti.pm.policymgr.struts;

/**
 * This class contains key policy information to identify a policy term.
 * It also contains the index of this element in the containing PolicyList.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 20, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class PolicyListElement {

    /**
     * Get the policy number.
     *
     * @return the policy number.
     */
    public String getPolicyNo() {
        return m_policyNo;
    }

    /**
     * Set the policy number.
     *
     * @param policyNo the policy number.
     */
    public void setPolicyNo(String policyNo) {
        m_policyNo = policyNo;
    }

    /**
     * Get the policy term history id for this policy instance.
     *
     * @return the policy term history id
     */
    public String getPolicyTermHistoryId() {
        return m_policyTermHistoryId;
    }

    /**
     * set the policy term history id for this policy instance.
     *
     * @param policyTermHistoryId the policy term history id
     */
    public void setPolicyTermHistoryId(String policyTermHistoryId) {
        m_policyTermHistoryId = policyTermHistoryId;
    }

    /**
     * Get the 0-based index of this PolicyListElement.
     *
     * @return the 0-based index of this PolicyListElement.
     */
    public int getListIndex() {
        return m_listIndex;
    }

    /**
     * set the 0-based index of this PolicyListElement.
     *
     * @param listIndex the 0-based index of this PolicyListElement.
     */
    protected void setListIndex(int listIndex) {
        m_listIndex = listIndex;
    }

    private String m_policyNo;
    private String m_policyTermHistoryId;
    private int m_listIndex;
}
