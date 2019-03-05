package dti.pm.policymgr.struts;

import dti.oasis.recordset.Record;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * This class holds a list of PolicyListElement objects containing key information about a list of policies.
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
public class PolicyList {

    /**
     * Add a PolicyListElement to this PolicyList with the policyNo and policyTermHistoryId values
     * from the given Record.
     *
     * @param policyInfo a Record containing the policyNo and policyTermHistoryId values.
     */
    public void add(Record policyInfo) {
        PolicyListElement policyListElement = new PolicyListElement();
        policyListElement.setPolicyNo(policyInfo.getStringValue("policyNo"));
        policyListElement.setPolicyTermHistoryId(policyInfo.getStringValue("policyTermHistoryId"));
        add(policyListElement);
    }

    /**
     * Add the given PolicyListElement to this PolicyList.
     *
     * @param element the PolicyListElement to add to this PolicyList
     */
    public void add(PolicyListElement element) {
        if (!m_policyList.containsKey(element.getPolicyTermHistoryId())) {
            m_policyList.put(element.getPolicyTermHistoryId(), element);
            m_policyTermHistoryIds.add(element.getPolicyTermHistoryId());
            element.setListIndex(m_policyList.size() - 1);
        }
    }

    /**
     * Determine if this PolicyList has the PolicyListElement with the given policyTermHistoryId.
     *
     * @param policyTermHistoryId the policyTermHistoryId of the desired PolicyListElement
     * @return true if the PolicyListElement exists with the given policyTermHistoryId. Otherwise, false.
     * @throws IllegalArgumentException if a matching PolicyListElement is not found.
     */
    public boolean has(String policyTermHistoryId) {
        PolicyListElement policyListElement = (PolicyListElement) m_policyList.get(policyTermHistoryId);
        return policyListElement != null;
    }


    /**
     * Get the PolicyListElement from this PolicyList with the given policyTermHistoryId.
     *
     * @param policyTermHistoryId the policyTermHistoryId of the desired PolicyListElement
     * @return the matching PolicyListElement
     * @throws IllegalArgumentException if a matching PolicyListElement is not found.
     */
    public PolicyListElement get(String policyTermHistoryId) {
        PolicyListElement policyListElement = (PolicyListElement) m_policyList.get(policyTermHistoryId);
        if (policyListElement == null) {
            throw new IllegalArgumentException("The PolicyListElement for policyTermHistoryId '" + policyTermHistoryId + "' is not found in the PolicyList.");
        }
        return policyListElement;
    }

    /**
     * Get the PolicyListElement from this PolicyList at the given index.
     *
     * @param index the index of the desired PolicyListElement
     * @return the PolicyListElement at the given index.
     */
    public PolicyListElement get(int index) {
        return get((String) m_policyTermHistoryIds.get(index));
    }

    /**
     * Return the number of PolicyListElements contained in this PolicyList.
     *
     * @return the number of PolicyListElements contained in this PolicyList.
     */
    public int getSize() {
        return m_policyTermHistoryIds.size();
    }


    private Map m_policyList = new HashMap();
    private List m_policyTermHistoryIds = new ArrayList();
}
