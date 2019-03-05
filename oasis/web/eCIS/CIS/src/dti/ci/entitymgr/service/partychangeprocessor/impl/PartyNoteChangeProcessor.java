package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PartyNoteType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.entitymgr.EntityManager;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.ows.validation.Validator;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   10/6/14
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
public class PartyNoteChangeProcessor extends BasePartyChangeElementProcessor<PartyNoteType> {
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
                        List<PartyNoteType> changedElements, List<PartyNoteType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process",
                    new Object[]{partyChangeRequest, partyChangeResult, entityType, entityId, changedElements, originalElements});
        }

        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId);

        for (PartyNoteType changedNote : changedElements) {
            Validator.validateFieldRequired(changedNote.getKey(),
                    "ci.partyChangeService.field.required.error", "Party Note Key");

            PartyNoteType originalNote = getOriginalPartyNote(originalElements, changedNote);
            PartyNoteType dbNote = getPartyNoteInDb(partyInfoInDb, entityType, entityId, changedNote);

            Record changedNoteRecord = getPartyNoteRecord(entityId, changedNote);
            Record originalNoteRecord = getPartyNoteRecord(entityId, originalNote);
            Record dbNoteRecord = getPartyNoteRecord(entityId, dbNote);

            String rowStatus = getRowStatus(changedNoteRecord, originalNoteRecord, dbNoteRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedNoteRecord, dbNoteRecord);
                validatePartyNote(changedNoteRecord);

                Record changedValues = getChangedValues(changedNoteRecord, originalNoteRecord,
                        dbNoteRecord, new String[]{"noteId", "sourceRecordId", "sourceTableName"});
                if (rowStatus.equals(ROW_STATUS_NEW)) {
                    changedValues.setFieldValue("action", "INSERT");
                } else if (rowStatus.equals(ROW_STATUS_MODIFIED)) {
                    changedValues.setFieldValue("action", "UPDATE");
                }

                savePartyNote(changedNote, changedValues);
            }
        }

        l.exiting(getClass().getName(), "process");
    }

    private PartyNoteType getOriginalPartyNote(List<PartyNoteType> originalPartyNoteList,
                                               PartyNoteType changedPartyNote) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalPartyNote",
                    new Object[]{originalPartyNoteList, changedPartyNote});
        }

        PartyNoteType partyNote = null;
        if (originalPartyNoteList != null) {
            for (PartyNoteType tempNote : originalPartyNoteList) {
                if (changedPartyNote.getKey().equals(tempNote.getKey())) {
                    partyNote = tempNote;
                    break;
                }
            }
        }

        if (partyNote == null) {
            if (!StringUtils.isBlank(changedPartyNote.getPartyNoteNumberId())) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"Cannot find original Party Note in Previous Value Data Description" +
                                " with contact number ID:" + changedPartyNote.getPartyNoteNumberId() + "."});
                throw new AppException("Cannot find original Party Note in Previous Value Data Description.");
            }
        } else {
            Validator.validateFieldRequired(changedPartyNote.getPartyNoteNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "Party Note Number ID of an existing Party Note");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalPartyNote", partyNote);
        }
        return partyNote;
    }

    private PartyNoteType getPartyNoteInDb(PartyInquiryResultType partyInfo,
                                           String entityType, String entityId, PartyNoteType changedPartyNote) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPartyNoteInDb", new Object[]{partyInfo, entityType, entityId, changedPartyNote});
        }

        PartyNoteType partyNote = null;
        List<PartyNoteType> partyNoteList = null;

        if (!StringUtils.isBlank(changedPartyNote.getPartyNoteNumberId()) &&
                partyInfo != null) {
            if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_PERSON)) {
                for (PersonType person : partyInfo.getPerson()) {
                    if (person.getPersonNumberId().equals(entityId)) {
                        partyNoteList = person.getPartyNote();
                        break;
                    }
                }
            } else if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_ORGANIZATION)) {
                for (OrganizationType organization : partyInfo.getOrganization()) {
                    if (organization.getOrganizationNumberId().equals(entityId)) {
                        partyNoteList = organization.getPartyNote();
                        break;
                    }
                }
            }
        }

        if (partyNoteList != null) {
            for (PartyNoteType tempNote : partyNoteList) {
                if (tempNote.getPartyNoteNumberId().equals(changedPartyNote.getPartyNoteNumberId())) {
                    partyNote = tempNote;
                    break;
                }
            }
        }

        if (!StringUtils.isBlank(changedPartyNote.getPartyNoteNumberId()) && partyNote == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"Cannot find Party Note in DB with party note number ID:" + changedPartyNote.getPartyNoteNumberId() + "."});
            throw new AppException("Cannot find Party Note in DB.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPartyNoteInDb", partyNote);
        }
        return partyNote;
    }

    private Record getPartyNoteRecord(String entityId, PartyNoteType partyNote) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPartyNoteRecord", new Object[]{entityId, partyNote});
        }

        Record record = null;

        if (partyNote != null) {
            record = new Record();

            record.setFieldValue("sourceRecordId", entityId);
            record.setFieldValue("sourceTableName", "ENTITY");

            if (StringUtils.isBlank(partyNote.getPartyNoteNumberId())) {
                record.setFieldValue("noteId", "-1");
            } else {
                record.setFieldValue("noteId", partyNote.getPartyNoteNumberId());
            }

            mapObjectToRecord(getFieldElementMaps(), partyNote, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPartyNoteRecord", record);
        }
        return record;
    }


    private void validatePartyNote(Record partyNoteRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePartyNote", new Object[]{partyNoteRecord});
        }

        l.exiting(getClass().getName(), "validatePartyNote");
    }

    private void savePartyNote(PartyNoteType changedPartyNote, Record changedPartyNoteRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePartyNote",
                    new Object[]{changedPartyNote, changedPartyNoteRecord});
        }

        Record recUpdateResult = getEntityManager().savePartyNote(changedPartyNoteRecord);

        if (changedPartyNoteRecord.getStringValue("action", "").equals("INSERT")) {
            changedPartyNote.setPartyNoteNumberId(recUpdateResult.getStringValue("newNoteId"));
        }

        l.exiting(getClass().getName(), "savePartyNote");
    }

    protected Record getChangedValues(Record changedRecord, Record originalRecord, Record dbRecord, String[] additionalFieldNames) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedValues",
                    new Object[]{changedRecord, originalRecord, dbRecord, additionalFieldNames});
        }

        Record record = new Record();

        if (changedRecord != null) {
            if (originalRecord != null && dbRecord != null) {
                Iterator fieldNames = changedRecord.getFieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = (String) fieldNames.next();

                    if (originalRecord.hasField(fieldName)) {
                        if (changedRecord.getStringValue(fieldName, "").equals(originalRecord.getStringValue(fieldName, ""))) {
                            // Use the values in db for note.
                            record.setFieldValue(fieldName, dbRecord.getStringValue(fieldName, ""));
                        } else {
                            // The value in changed xml doesn't equal to the value in previous data.
                            record.setFieldValue(fieldName, changedRecord.getStringValue(fieldName, ""));
                        }
                    } else {
                        // The field is not found in original xml.
                        record.setFieldValue(fieldName, changedRecord.getStringValue(fieldName, ""));
                    }
                }
            } else {
                // This is a new record.
                record.setFields(changedRecord);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedValues", record);
        }
        return record;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private EntityManager m_entityManager;
    private List<FieldElementMap> m_fieldElementMaps;
}
