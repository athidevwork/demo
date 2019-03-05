package dti.ci.entitymgr.service.partychangeprocessor;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/19/14
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
public abstract class PartyChangeElementProcessorFactory {
    public static final String BEAN_NAME = "partyChangeElementProcessorFactory";

    /**
     * Returns an instance of message manager.
     *
     * @return MessageManager, an instance of message manager with implemenation information.
     */
    public synchronized static final PartyChangeElementProcessorFactory getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(PartyChangeElementProcessorFactory.BEAN_NAME)) {
                c_instance = (PartyChangeElementProcessorFactory) ApplicationContext.getInstance().getBean(PartyChangeElementProcessorFactory.BEAN_NAME);
            } else {
                throw new ConfigurationException("The EntityElementProcessorFactory is not configured properly.");
            }
        }
        return c_instance;
    }

    /**
     * Get the processor class.
     * @param type The class of entity child element.
     * @param <T>
     * @return
     */
    public abstract <T> PartyChangeElementProcessor<T> getProcessor(Class<T> type);

    private static PartyChangeElementProcessorFactory c_instance;
}
