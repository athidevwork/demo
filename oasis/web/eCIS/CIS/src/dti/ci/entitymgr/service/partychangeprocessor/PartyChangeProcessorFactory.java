package dti.ci.entitymgr.service.partychangeprocessor;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/18/14
 *
 * @author kshen
 */
/*
 * The factory for getting entity processor.
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public abstract class PartyChangeProcessorFactory {
    public static final String BEAN_NAME = "partyChangeProcessorFactory";

    /**
     * Returns an instance of message manager.
     *
     * @return MessageManager, an instance of message manager with implemenation information.
     */
    public synchronized static final PartyChangeProcessorFactory getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(PartyChangeProcessorFactory.BEAN_NAME)) {
                c_instance = (PartyChangeProcessorFactory) ApplicationContext.getInstance().getBean(PartyChangeProcessorFactory.BEAN_NAME);
            } else {
                throw new ConfigurationException("The EntityProcessorFactory is not configured properly.");
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
    public abstract PartyChangeProcessor getProcessor(String entityType);


    private static PartyChangeProcessorFactory c_instance;
}
