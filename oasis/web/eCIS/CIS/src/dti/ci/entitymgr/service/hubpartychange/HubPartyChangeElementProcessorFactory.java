package dti.ci.entitymgr.service.hubpartychange;

import dti.ci.entitymgr.service.hubpartychange.processor.HubPartyChangeElementProcessor;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/20/2016
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
public abstract class HubPartyChangeElementProcessorFactory {

    public static final String BEAN_NAME = "HubPartyChangeElementProcessorFactory";

    public synchronized static final HubPartyChangeElementProcessorFactory getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(HubPartyChangeElementProcessorFactory.BEAN_NAME)) {
                c_instance = (HubPartyChangeElementProcessorFactory) ApplicationContext.getInstance().getBean(HubPartyChangeElementProcessorFactory.BEAN_NAME);
            } else {
                throw new ConfigurationException("The HubPartyChangeElementProcessorFactory is not configured properly.");
            }
        }
        return c_instance;
    }

    public abstract <T> HubPartyChangeElementProcessor<T> getProcessor(Class<T> type);

    private static HubPartyChangeElementProcessorFactory c_instance;


}
