package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.ContactType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.cs.partynotificationmgr.mgr.HubPartyManager;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.ows.validation.Validator;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/26/2016
 *
 * @author eouyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class HubContactChangeProcessor extends BaseHubPartyChangeElementProcessor<ContactType> {
    @Override
    public void processFromCisResult(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<ContactType> cisResultElements, String entityId, List<ContactType> changedElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFromCisResult",
                    new Object[]{partyChangeRequest, cisResultElements, entityId, changedElements});
        }

        RecordSet inputRs = new RecordSet();
        for (ContactType contactInResult : cisResultElements) {
            boolean foundChangedElement = false;
            for (ContactType changedContact : changedElements) {
                if (changedContact.getContactNumberId().equals(contactInResult.getContactNumberId())) {
                    foundChangedElement = true;
                    break;
                }
            }

            if (foundChangedElement) {
                Record inputRecord = getContactRecord(entityId, contactInResult);
                setCommonFieldsToRecord(inputRecord, partyChangeRequest, CISB_Y);
                inputRecord.setFieldValue("contactNumberId", contactInResult.getContactNumberId());
                inputRs.addRecord(inputRecord);
            }
        }

        if (inputRs.getSize() > 0) {
            getHubPartyManager().saveHubPartyInBatch(inputRs);
        }

        l.exiting(getClass().getName(), "processFromCisResult");
    }

    @Override
    public void processForHub(PartyChangeRequestType partyChangeRequest, String entityType, String entityId, List<ContactType> changedElements, List<ContactType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processForHub", new Object[]{partyChangeRequest, entityType, entityId, changedElements, originalElements});
        }

        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId, partyChangeRequest);
        for (ContactType changedContact : changedElements) {
            Validator.validateFieldRequired(changedContact.getKey(),
                    "ci.partyChangeService.field.required.error", "Party Contact Key");

            Record changedRecord = getContactRecord(entityId, changedContact);
            Record dbRecrod = getContactRecord(entityId, getContactInDb(partyInfoInDb, entityType, entityId, changedContact));

            mergeRecordValues(changedRecord, dbRecrod);
            setCommonFieldsToRecord(changedRecord, partyChangeRequest, CISB_N);
            Record result = getHubPartyManager().saveHubParty(changedRecord);

            if (!StringUtils.isBlank(result.getStringValue("newContactId", ""))) {
                changedContact.setContactNumberId(result.getStringValue("newContactId"));
            }
        }

        l.exiting(getClass().getName(), "processForHub");
    }

    private Record getContactRecord(String entityId, ContactType contact) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getContactRecord", new Object[]{entityId, contact});
        }

        Record record = null;

        if (contact != null) {
            record = new Record();
            record.setFieldValue("entityId", entityId);

            if (!StringUtils.isBlank(contact.getContactNumberId())) {
                record.setFieldValue("contactId", contact.getContactNumberId());
            }
            mapObjectToRecord(getFieldElementMaps(), contact, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getContactRecord", record);
        }
        return record;
    }

    private ContactType getContactInDb(PartyInquiryResultType partyInfoInDb,
                                       String entityType, String entityId, ContactType changedContact) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getContactInDb", new Object[]{partyInfoInDb, entityType, entityId, changedContact});
        }

        ContactType contact = null;
        List<ContactType> contactList = null;

        if (!StringUtils.isBlank(changedContact.getContactNumberId()) &&
                partyInfoInDb != null) {
            if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_PERSON)) {
                for (PersonType person : partyInfoInDb.getPerson()) {
                    if (person.getPersonNumberId().equals(entityId)) {
                        contactList = person.getContact();
                        break;
                    }
                }
            }
        }

        if (contactList != null) {
            for (ContactType tempContact : contactList) {
                if (tempContact.getContactNumberId().equals(changedContact.getContactNumberId())) {
                    contact = tempContact;
                    break;
                }
            }
        }

        if (!StringUtils.isBlank(changedContact.getContactNumberId()) && contact == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"Cannot find Contact in DB with contact number ID:" + changedContact.getContactNumberId() + "."});
            throw new AppException("Cannot find Contact in DB.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getContactInDb", contact);
        }
        return contact;
    }

    public HubPartyManager getHubPartyManager() {
        return m_hubPartyManager;
    }

    public void setHubPartyManager(HubPartyManager hubPartyManager) {
        m_hubPartyManager = hubPartyManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private HubPartyManager m_hubPartyManager;
    private List<FieldElementMap> m_fieldElementMaps;
}
