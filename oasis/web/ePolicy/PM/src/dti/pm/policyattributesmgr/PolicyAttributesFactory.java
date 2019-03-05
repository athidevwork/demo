package dti.pm.policyattributesmgr;

import dti.oasis.app.ApplicationContext;

/**
 * Factory class for PM Attribute.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  July 1, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/01/16 wdang   167534 - Initial version.
 * ---------------------------------------------------
 */
public class PolicyAttributesFactory {
    /**
     * Returns a synchronized static instance of PolicyAttributesManager.
     * @return PolicyAttributesManager, an instance of Pm Attribute Manager with cache.
     */
    public synchronized static final PolicyAttributesManager getInstance() {
        if (c_instance == null) {
            c_instance = (PolicyAttributesManager) ApplicationContext.getInstance().getBean(PolicyAttributesManager.BEAN_NAME);
        }
        return c_instance;
    }

    private static PolicyAttributesManager c_instance = null;
}
