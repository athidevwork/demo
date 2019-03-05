package dti.ci.entitymgr.service.partychangeprocessor.impl;

import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessorFactory;

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
public class PartyChangeProcessorFactoryImpl extends PartyChangeProcessorFactory {
    /**
     * Get the processor for given entity type.
     *
     * @param entityType
     * @return
     */
    @Override
    public PartyChangeProcessor getProcessor(String entityType) {
        return getPartyChangeProcessorMap().get(entityType);
    }

    public Map<String, PartyChangeProcessor> getPartyChangeProcessorMap() {
        return m_partyChangeProcessorMap;
    }

    public void setPartyChangeProcessorMap(Map<String, PartyChangeProcessor> partyChangeProcessorMap) {
        this.m_partyChangeProcessorMap = partyChangeProcessorMap;
    }

    private Map<String, PartyChangeProcessor> m_partyChangeProcessorMap;
}
