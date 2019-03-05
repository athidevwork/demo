package dti.ci.entitymgr.service.hubpartychange.processor;

import dti.ci.entitymgr.service.hubpartychange.HubPartyChangeElementProcessorFactory;
import dti.oasis.app.ConfigurationException;

import java.util.Map;

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
public class HubPartyChangeElementProcessorFactoryImpl extends HubPartyChangeElementProcessorFactory {

    /**
     * Get the processor class.
     *
     * @param type The class of entity child element.
     * @param <T>
     * @return
     */
    @Override
    public <T> HubPartyChangeElementProcessor<T> getProcessor(Class<T> type) {
        HubPartyChangeElementProcessor<T> entityElementProcessor = (HubPartyChangeElementProcessor<T>) getProcessorMap().get(type.getName());
        if (entityElementProcessor == null) {
            throw new ConfigurationException("The entity element processor for " + type.getName() + " is not configured.");
        }
        return entityElementProcessor;
    }

    public Map<String, HubPartyChangeElementProcessor<?>> getProcessorMap() {
        return processorMap;
    }

    public void setProcessorMap(Map<String, HubPartyChangeElementProcessor<?>> processorMap) {
        this.processorMap = processorMap;
    }

    private Map<String, HubPartyChangeElementProcessor<?>> processorMap;
}
