package dti.ci.entitymgr.service.hubpartychange.impl;

import com.delphi_tech.ows.partychangeservice.DataModificationInformationType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import dti.ci.entitymgr.service.hubpartychange.HubPartyChangeProcessorFactory;
import dti.ci.entitymgr.service.hubpartychange.HubPartyChangeServiceManager;
import dti.ci.entitymgr.service.hubpartychange.processor.HubPartyMergeProcessor;
import dti.ci.entitymgr.service.hubpartychange.processor.HubPropertyChangeProcessor;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;
import dti.ows.common.MessageStatusAppException;
import dti.ows.common.MessageStatusHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

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
public class HubPartyChangeServiceManagerImpl implements HubPartyChangeServiceManager {

    @Override
    public void saveParty(PartyChangeResultType partyChangeResult, PartyChangeRequestType partyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveParty");
        }
        DataModificationInformationType dataModificationInfo = partyChangeRequest.getDataModificationInformation();
        try {
            if (dataModificationInfo != null && dataModificationInfo.getActionCode() != null &&
                    dataModificationInfo.getActionCode().size() > 0 && "MERGE".equalsIgnoreCase(partyChangeRequest.getDataModificationInformation().getActionCode().get(0))) {
                getHubPartyMergeProcessor().process(partyChangeRequest, partyChangeResult);
            } else {

                HubPartyChangeProcessorFactory.getInstance().getProcessor(PartyChangeProcessor.ENTITY_TYPE_PERSON)
                        .process(partyChangeRequest, partyChangeResult);

                HubPartyChangeProcessorFactory.getInstance().getProcessor(PartyChangeProcessor.ENTITY_TYPE_ORGANIZATION)
                        .process(partyChangeRequest, partyChangeResult);

                getHubPropertyChangeProcessor().process(partyChangeRequest, partyChangeResult);
            }
        } catch (Exception e) {
            MessageStatusAppException msae = MessageStatusHelper.getInstance().handleException("Failure invoking the HubPartyChangeServiceManagerImpl :\n " + e.getMessage(), e);
            l.logp(Level.SEVERE, getClass().getName(), "HubPartyChangeServiceManagerImpl", msae.getMessage(), msae);
            throw msae;
        }
        l.exiting(getClass().getName(), "saveParty");
    }

    public void verifyConfig() {
        if (getHubPartyMergeProcessor() == null)
            throw new ConfigurationException("The required property 'HubPartyMergeProcessor' is missing.");
        if (getHubPropertyChangeProcessor() == null)
            throw new ConfigurationException("The required property 'HubPropertyChangeProcessor' is missing.");
    }

    public HubPropertyChangeProcessor getHubPropertyChangeProcessor() {
        return m_hubPropertyChangeProcessor;
    }

    public void setHubPropertyChangeProcessor(HubPropertyChangeProcessor hubPropertyChangeProcessor) {
        this.m_hubPropertyChangeProcessor = hubPropertyChangeProcessor;
    }

    public HubPartyMergeProcessor getHubPartyMergeProcessor() {
        return m_hubPartyMergeProcessor;
    }

    public void setHubPartyMergeProcessor(HubPartyMergeProcessor hubPartyMergeProcessor) {
        m_hubPartyMergeProcessor = hubPartyMergeProcessor;
    }

    private HubPropertyChangeProcessor m_hubPropertyChangeProcessor;
    private HubPartyMergeProcessor m_hubPartyMergeProcessor;
}
