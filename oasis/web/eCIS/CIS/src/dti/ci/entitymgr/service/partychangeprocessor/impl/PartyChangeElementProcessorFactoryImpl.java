package dti.ci.entitymgr.service.partychangeprocessor.impl;

import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeElementProcessor;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeElementProcessorFactory;
import dti.oasis.app.ConfigurationException;

import java.util.Map;

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
public class PartyChangeElementProcessorFactoryImpl extends PartyChangeElementProcessorFactory {
    /**
     * Get the processor class.
     * @param type The class of entity child element.
     * @param <T>
     * @return
     */
    @Override
    public <T> PartyChangeElementProcessor<T> getProcessor(Class<T> type) {
        @SuppressWarnings("unchecked")
        PartyChangeElementProcessor<T> entityElementProcessor = (PartyChangeElementProcessor<T>) getProcessorMap().get(type.getName());
        if (entityElementProcessor == null) {
            throw new ConfigurationException("The entity element processor for " + type.getName() + " is not configured.");
        }
        return entityElementProcessor;
    }

    public Map<String, PartyChangeElementProcessor<?>> getProcessorMap() {
        return processorMap;
    }

    public void setProcessorMap(Map<String, PartyChangeElementProcessor<?>> processorMap) {
        this.processorMap = processorMap;
    }

    private Map<String, PartyChangeElementProcessor<?>> processorMap;
}
