package dti.ci.entitymgr.service.hubpartychange;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/19/2016
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public abstract class HubPartyChangeProcessorFactory {
    public static final String BEAN_NAME = "HubPartyChangeProcessorFactory";

    public synchronized static final HubPartyChangeProcessorFactory getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(HubPartyChangeProcessorFactory.BEAN_NAME)) {
                c_instance = (HubPartyChangeProcessorFactory) ApplicationContext.getInstance().getBean(HubPartyChangeProcessorFactory.BEAN_NAME);
            } else {
                throw new ConfigurationException("The HubEntityProcessorFactory is not configured properly.");
            }
        }
        return c_instance;
    }

    /**
     * Get the processor for given entity type.
     *
     * @param entityType
     * @return
     */
    public abstract HubPartyChangeProcessor getProcessor(String entityType);

    private static HubPartyChangeProcessorFactory c_instance;


}
