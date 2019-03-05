package dti.ci.entitymgr.service.impl;

import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.DataModificationInformationType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import dti.ci.entitymgr.service.PartyChangeServiceManager;
import dti.ci.entitymgr.service.dao.PartyChangeServiceDAO;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessorFactory;
import dti.ci.entitymgr.service.partychangeprocessor.impl.PartyMergeProcessor;
import dti.ci.entitymgr.service.partychangeprocessor.impl.PropertyChangeProcessor;
import dti.oasis.accesstrailmgr.AccessTrailRequestIds;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSession;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.XMLUtils;
import dti.ows.common.MessageStatusAppException;
import dti.ows.common.MessageStatusHelper;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/2/12
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * --------------------------------------------------------------------------------------------------------------------
 * 09/11/2012       ldong       issue 137345
 * 09/18/2012       ldong       issue 137582
 * 09/20/2012       ldong       issue 137499
 * 02/19/2013       mlm         issue 142055 - Refactored to include clientId for party change service update.
 * 01/27/2014       bzhu        issue 148789 - Refactored to include partynote and relationship for party change service update.
 * 05/15/2014       kshen       issue 154561. Add Client ID to the result of add entity.
 * 06/20/2014       kshen       Issue 154676. Changes for relationship.
 * 11/26/2015       dpang       Issue 164029. Add client merge processor
 * 04/26/2016       dpang       Issue 149588. Sync to hub for party change service
 * 06/07/2018       mproekt     Issue 193752. Add check and set value for AccessTrailRequestIds.OWS_TRAIL_ACCESS_DATE  in a session if missing
 * --------------------------------------------------------------------------------------------------------------------
 */
public class PartyChangeServiceManagerImpl implements PartyChangeServiceManager {
    private final static QName _PartyChangeRequest_QNAME = new QName("http://www.delphi-tech.com/ows/PartyChangeService", "PartyChangeRequest");
    private final static QName _PartyChangeResult_QNAME = new QName("http://www.delphi-tech.com/ows/PartyChangeService", "PartyChangeResult");

    @Override
    public PartyChangeResultType saveParty(PartyChangeRequestType partyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveParty", new Object[]{partyChangeRequest});
        }
        //Need to set access time if it was not set. the call could come from non web service calls and the time would not recorded in a session

        RequestStorageManager requestManager = RequestStorageManager.getInstance();
        HttpServletRequest request = (HttpServletRequest) requestManager.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
        if(request.getAttribute(AccessTrailRequestIds.OWS_TRAIL_ACCESS_DATE)==null){
            request.setAttribute(AccessTrailRequestIds.OWS_TRAIL_ACCESS_DATE, new Date());
        }

        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(partyChangeRequest, _PartyChangeRequest_QNAME);
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
                    partyChangeRequest.getMessageId(), partyChangeRequest.getCorrelationId(), partyChangeRequest.getUserId(), _PartyChangeRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "saveParty", xmlResult);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(partyChangeRequest, _PartyChangeRequest_QNAME,
                    partyChangeRequest.getMessageId(), partyChangeRequest.getCorrelationId(), partyChangeRequest.getUserId());
        }

        setHubOrigin(partyChangeRequest);
        PartyChangeResultType partyChangeResult = new PartyChangeResultType();

        try {
            partyChangeResult.setCorrelationId(partyChangeRequest.getCorrelationId());
            partyChangeResult.setMessageId(partyChangeRequest.getMessageId());
            DataModificationInformationType dataModificationInfo = partyChangeRequest.getDataModificationInformation();
            if (dataModificationInfo != null && dataModificationInfo.getActionCode() != null &&
                    dataModificationInfo.getActionCode().size() > 0 && "MERGE".equalsIgnoreCase(partyChangeRequest.getDataModificationInformation().getActionCode().get(0))) {
                getPartyMergeProcessor().process(partyChangeRequest, partyChangeResult);
            } else {
                PartyChangeProcessorFactory.getInstance().getProcessor(PartyChangeProcessor.ENTITY_TYPE_PERSON)
                        .process(partyChangeRequest, partyChangeResult);

                PartyChangeProcessorFactory.getInstance().getProcessor(PartyChangeProcessor.ENTITY_TYPE_ORGANIZATION)
                        .process(partyChangeRequest, partyChangeResult);

                getPropertyChangeProcessor().process(partyChangeRequest, partyChangeResult);
            }

            partyChangeResult.setMessageStatus(MessageStatusHelper.getInstance().getSuccessMessageStatus());

            owsLogRequest.setSourceTableName("ENTITY");

            List<PersonType> person = partyChangeRequest.getPerson();
            List<OrganizationType> organization = partyChangeRequest.getOrganization();
            if (person.size() > 0) {
                owsLogRequest.setSourceRecordFk(person.get(0).getPersonNumberId());
                owsLogRequest.setSourceRecordNo(person.get(0).getClientId());
            } else if (organization.size() > 0) {
                owsLogRequest.setSourceRecordFk(organization.get(0).getOrganizationNumberId());
                owsLogRequest.setSourceRecordNo(organization.get(0).getClientId());
            }
        } catch (Exception e) {
            MessageStatusAppException msae = MessageStatusHelper.getInstance().handleException("Failure invoking the PartyChangeServiceManagerImpl", e);
            l.logp(Level.SEVERE, getClass().getName(), "PartyChangeServiceManagerImpl", msae.getMessage(), msae);
            throw msae;
        }

        owsLogRequest.setMessageStatusCode(partyChangeResult.getMessageStatus().getMessageStatusCode());
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(partyChangeResult, _PartyChangeResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            l.logp(Level.FINEST, getClass().getName(), "saveParty", xmlResult);
        } else {
            owsLogRequest.setServiceResult(partyChangeResult);
            owsLogRequest.setServiceResultQName(_PartyChangeResult_QNAME);
        }
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveParty", partyChangeResult);
        }
        return partyChangeResult;
    }

    /**
     * Set hub origin to db session variable if hub function is enabled.
     *
     * @param partyChangeRequest
     */
    private void setHubOrigin(PartyChangeRequestType partyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setHubOrigin", new Object[]{partyChangeRequest});
        }

        if ("Y".equalsIgnoreCase(SysParmProvider.getInstance().getSysParm("CI_ENABLE_HUB", "N"))) {
            String origin = "OASIS";

            if (partyChangeRequest.getSendingSystemInformation() != null &&
                    !StringUtils.isBlank(partyChangeRequest.getSendingSystemInformation().getVendorProductName())) {
                origin = partyChangeRequest.getSendingSystemInformation().getVendorProductName();
            }

            Record inputRecord = new Record();
            inputRecord.setFieldValue("origin", origin);
            getPartyChangeServiceDAO().setHubOrigin(inputRecord);
        }

        l.exiting(getClass().getName(), "setHubOrigin");
    }

    public void verifyConfig() {
        if (getPropertyChangeProcessor() == null)
            throw new ConfigurationException("The required property 'propertyChangeProcessor' is missing.");
        if (getPartyMergeProcessor() == null)
            throw new ConfigurationException("The required property 'partyMergeProcessor' is missing.");
    }

    public PropertyChangeProcessor getPropertyChangeProcessor() {
        return m_propertyChangeProcessor;
    }

    public void setPropertyChangeProcessor(PropertyChangeProcessor propertyChangeProcessor) {
        m_propertyChangeProcessor = propertyChangeProcessor;
    }

    public PartyMergeProcessor getPartyMergeProcessor() {
        return m_partyMergeProcessor;
    }

    public void setPartyMergeProcessor(PartyMergeProcessor partyMergeProcessor) {
        this.m_partyMergeProcessor = partyMergeProcessor;
    }

    public PartyChangeServiceDAO getPartyChangeServiceDAO() {
        return m_partyChangeServiceDAO;
    }

    public void setPartyChangeServiceDAO(PartyChangeServiceDAO partyChangeServiceDAO) {
        m_partyChangeServiceDAO = partyChangeServiceDAO;
    }

    private PropertyChangeProcessor m_propertyChangeProcessor;
    private PartyMergeProcessor m_partyMergeProcessor;
    private PartyChangeServiceDAO m_partyChangeServiceDAO;
}
