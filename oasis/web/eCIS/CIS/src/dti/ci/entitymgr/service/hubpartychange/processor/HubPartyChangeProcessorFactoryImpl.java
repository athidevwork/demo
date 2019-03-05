package dti.ci.entitymgr.service.hubpartychange.processor;

import dti.ci.entitymgr.service.hubpartychange.HubPartyChangeProcessor;
import dti.ci.entitymgr.service.hubpartychange.HubPartyChangeProcessorFactory;

import java.util.Map;

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
public class HubPartyChangeProcessorFactoryImpl extends HubPartyChangeProcessorFactory {

    /**
     * Get the processor for given entity type.
     *
     * @param entityType
     * @return
     */
    @Override
    public HubPartyChangeProcessor getProcessor(String entityType) {
        return getHubPartyChangeProcessorMap().get(entityType);
    }

    public Map<String, HubPartyChangeProcessor> getHubPartyChangeProcessorMap() {
        return m_hubPartyChangeProcessorMap;
    }

    public void setHubPartyChangeProcessorMap(Map<String, HubPartyChangeProcessor> hubPartyChangeProcessorMap) {
        m_hubPartyChangeProcessorMap = hubPartyChangeProcessorMap;
    }

    private Map<String, HubPartyChangeProcessor> m_hubPartyChangeProcessorMap;
}
