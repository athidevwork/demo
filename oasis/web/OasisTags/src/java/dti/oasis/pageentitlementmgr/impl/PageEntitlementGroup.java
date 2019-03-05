package dti.oasis.pageentitlementmgr.impl;

import dti.oasis.pageentitlementmgr.PageEntitlement;
import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements PageEntitlementGroup by providing implementation detail about how the page entitlements
 * are stored and retrieved for a given entitlement group. The entitlement group refers either to a an empty pageURI
 * (when means, default page entitlements for all pages) or an actual pageURI.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 14, 2007
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
public class PageEntitlementGroup {

    /**
     * Method that add the provided pageEntitlement into collection of pageEntitlements.
     *
     * @param pageEntitlement
     */
    public void addPageEntitlement(PageEntitlement pageEntitlement) {
        m_pageEntitlements.add(pageEntitlement);
        m_pageEntitlementsIds.put(pageEntitlement.getId(), pageEntitlement.getId());
        m_pageEntitlementIndFields.add(pageEntitlement.getIndFieldName());
    }

    /**
     * Method that returns the number of configured page entitlement for a particular entitlement group.
     * @return an integer, representing the number of configured page entitlement for a particular entitlement group.
     */
    public int size() {
        return m_pageEntitlements.size();
    }

    /**
     * Method that clears the page entitlement collection.
     */
    public void clear() {
        m_pageEntitlements.clear();
        m_pageEntitlementsIds.clear();
        m_pageEntitlementIndFields.clear();
    }

    /**
     * Method that returns an iterator that contains a collection of page entitlements configured
     * for an entitlement group.
     *
     * @return a collection of PageEntitlements
     */
    public Iterator iterator() {
        return m_pageEntitlements.iterator();
    }

    /**
     * This method returns a boolean flag tha indicates whether the provided indicator field is configured for
     * page entitlements.
     *
     * @param indFieldName
     * @return boolean true value, if the indFieldName is configured for page entitlement; otherwise false.
     */
    public boolean isIndicatorFieldConfigured(String indFieldName) {
        Logger l = LogUtils.enterLog(getClass(), "isIndicatorFieldConfigured", new Object[]{indFieldName});
        boolean exists=m_pageEntitlementIndFields.contains(indFieldName);
        l.exiting(getClass().getName(), "isIndicatorFieldConfigured", String.valueOf(exists));
        return exists;
    }

    /**
     * This method returns a boolean flag tha indicates whether the provided tag id is configured for
     * page entitlements.
     *
     * @param id
     * @return boolean true value, if the tag id is configured for page entitlement; otherwise false.
     */
    public boolean isIdConfigured(String id) {
        Logger l = LogUtils.enterLog(getClass(), "isIdConfigured", new Object[]{id});
        boolean exists=m_pageEntitlementsIds.containsKey(id);
        l.exiting(getClass().getName(), "isIdConfigured", String.valueOf(exists));
        return exists;
    }

    /**
     * Constructor for PageEntitlementGroup
     */
    public PageEntitlementGroup() {
    }

    private List m_pageEntitlements = new ArrayList();
    private Map m_pageEntitlementsIds = new HashMap();
    private ArrayList m_pageEntitlementIndFields = new ArrayList();
}
