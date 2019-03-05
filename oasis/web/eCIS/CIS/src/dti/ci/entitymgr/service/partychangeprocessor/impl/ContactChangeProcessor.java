package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.ContactType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.contactmgr.ContactManager;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.ows.validation.Validator;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   9/29/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/10/2017       kshen       Issue 181805. Added new element PersonTitle to Contact element. Both the existing
 *                              element PersonTitlePrefix and the new PersonTitle are mapped to the column title.
 *                              If both of them are changed, we will use the PersonTitle element to update DB.
 * ---------------------------------------------------
 */
public class ContactChangeProcessor extends BasePartyChangeElementProcessor<ContactType> {

    /**
     * Process entity elements.
     *
     * @param partyChangeRequest
     * @param partyChangeResult
     * @param entityType
     * @param entityId
     * @param changedElements
     * @param originalElements
     */
    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                        String entityType, String entityId,
                        List<ContactType> changedElements, List<ContactType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process",
                    new Object[]{partyChangeRequest, partyChangeResult, entityType, entityId, changedElements, originalElements});
        }

        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId);

        for (ContactType changedContact : changedElements) {
            Validator.validateFieldRequired(changedContact.getKey(),
                    "ci.partyChangeService.field.required.error", "Party Contact Key");

            ContactType originalContact = getOriginalContact(originalElements, changedContact);
            ContactType dbContact = getContactInDb(partyInfoInDb, entityType, entityId, changedContact);

            Record changedContactRecord = getContactRecord(entityId, changedContact);
            Record originalContactRecord = getContactRecord(entityId, originalContact);
            Record dbContactRecord = getContactRecord(entityId, dbContact);

            String rowStatus = getRowStatus(changedContactRecord, originalContactRecord, dbContactRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedContactRecord, dbContactRecord);
                validateContact(changedContactRecord);

                Record changedValues = getChangedValues(changedContactRecord, originalContactRecord,
                        dbContactRecord, new String[]{"contactId", "entityId"});
                changedValues.setFieldValue(ROW_STATUS, rowStatus);

                saveContact(changedContact, changedValues);
            }
        }

        l.exiting(getClass().getName(), "process");
    }

    private ContactType getOriginalContact(List<ContactType> originalContacts, ContactType changedContact) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalContact", new Object[]{originalContacts, changedContact});
        }

        ContactType originalContact = null;

        if (originalContacts != null) {
            for (ContactType tempContact : originalContacts) {
                if (changedContact.getKey().equals(tempContact.getKey())) {
                    originalContact = tempContact;
                    break;
                }
            }
        }

        if (originalContact == null) {
            if (!StringUtils.isBlank(changedContact.getContactNumberId())) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"Cannot find original Contact in Previous Value Data Description" +
                                " with contact number ID:" + changedContact.getContactNumberId() + "."});
                throw new AppException("Cannot find original Contact in Previous Value Data Description.");
            }
        } else {
            Validator.validateFieldRequired(changedContact.getContactNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "Contact Number ID of an existing Contact");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalContact", originalContact);
        }
        return originalContact;
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

    private void validateContact(Record contactRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateContact", new Object[]{contactRecord});
        }

        // No validation rules for now.

        l.exiting(getClass().getName(), "validateContact");
    }

    private void saveContact(ContactType changedContact, Record changedContactRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveContact", new Object[]{changedContact, changedContactRecord});
        }

        // Added new element PersonTitle to Contact element. Both the existing
        // element PersonTitlePrefix and the new PersonTitle are mapped to the column title.
        // If both of them are changed, we will use the PersonTitle element to update DB.
        String personTitle = changedContactRecord.getStringValue("personTitle", "");
        if (!StringUtils.isBlank(personTitle)) {
            changedContactRecord.setFieldValue("title", personTitle);
        }

        Record recUpdateResult = getContactManager().saveContact(changedContactRecord);

        if (ROW_STATUS_NEW.equals(changedContactRecord.getStringValue(ROW_STATUS, ""))) {
            changedContact.setContactNumberId(recUpdateResult.getStringValue("newContactId"));
        }
        
        l.exiting(getClass().getName(), "saveContact");
    }

    public ContactManager getContactManager() {
        return m_contactManager;
    }

    public void setContactManager(ContactManager contactManager) {
        this.m_contactManager = contactManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private ContactManager m_contactManager;
    private List<FieldElementMap> m_fieldElementMaps;
}
