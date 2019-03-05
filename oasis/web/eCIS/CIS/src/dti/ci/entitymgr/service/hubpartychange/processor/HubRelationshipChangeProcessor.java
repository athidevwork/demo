package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.party.RelationshipType;
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
 * Date:   4/22/2016
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
public class HubRelationshipChangeProcessor extends BaseHubPartyChangeElementProcessor<RelationshipType> {

    @Override
    public void processFromCisResult(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<RelationshipType> cisResultElements,
                                     String entityId, List<RelationshipType> changedElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFromCisResult",
                    new Object[]{partyChangeRequest, cisResultElements, entityId, changedElements});
        }
        RecordSet recordSet = new RecordSet();
        for (RelationshipType cisResultRelationship : cisResultElements) {
            boolean foundChangedElement = false;
            for (RelationshipType changedRelationship : changedElements) {
                if (changedRelationship.getRelationshipNumberId().equals(cisResultRelationship.getRelationshipNumberId())) {
                    foundChangedElement = true;
                    break;
                }
            }

            if (foundChangedElement) {
                Record inputRecord = getRelationshipRecord(entityId, cisResultRelationship);
                reverseRelationIfNeeded(inputRecord);
                setCommonFieldsToRecord(inputRecord, partyChangeRequest, CISB_Y);
                inputRecord.setFieldValue("entityRelationId", cisResultRelationship.getRelationshipNumberId());
                recordSet.addRecord(inputRecord);
            }
        }
        if (recordSet.getSize() > 0) {
            getHubPartyManager().saveHubPartyInBatch(recordSet);
        }
        l.exiting(getClass().getName(), "processFromCisResult");
    }

    @Override
    public void processForHub(PartyChangeRequestType partyChangeRequest, String entityType,
                              String entityId, List<RelationshipType> changedElements, List<RelationshipType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processForHub", new Object[]{partyChangeRequest});
        }

        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId, partyChangeRequest);
        for (RelationshipType changedRelationship : changedElements) {
            Validator.validateFieldRequired(changedRelationship.getKey(),
                    "ci.partyChangeService.field.required.error", "Relationship Key");

            RelationshipType originalRelationship = getOriginalRelationship(originalElements, changedRelationship);
            RelationshipType dbRelationship = getRelationshipInDb(partyInfoInDb, entityType, entityId, changedRelationship);

            Record changedRelationshipRecord = getRelationshipRecord(entityId, changedRelationship);
            Record originalRelationshipRecord = getRelationshipRecord(entityId, originalRelationship);
            Record dbRelationshipRecord = getRelationshipRecord(entityId, dbRelationship);

            String rowStatus = getRowStatus(changedRelationshipRecord, originalRelationshipRecord, dbRelationshipRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                if (!StringUtils.isBlank(changedRelationship.getRelationshipNumberId())) {
                    changedRelationshipRecord.setFieldValue("entityRelationId", changedRelationship.getRelationshipNumberId());

                    mergeRecordValues(changedRelationshipRecord, dbRelationshipRecord);
                    reverseRelationIfNeeded(changedRelationshipRecord);

                    setCommonFieldsToRecord(changedRelationshipRecord, partyChangeRequest, CISB_N);
                    Record result = getHubPartyManager().saveHubParty(changedRelationshipRecord);

                    if (rowStatus.equals(ROW_STATUS_NEW)) {
                        changedRelationship.setRelationshipNumberId(result.getStringValue("newEntityRelationId"));
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "processForHub");
    }

    private void reverseRelationIfNeeded(Record changedRelationshipRecord) {
        if (changedRelationshipRecord.getStringValue("reverseRelationIndicator", "").equals("REVERSE RELATION")) {
            changedRelationshipRecord.setFieldValue("entityParentId",
                    changedRelationshipRecord.getStringValue("referredEntityId", ""));

            changedRelationshipRecord.setFieldValue("entityChildId",
                    changedRelationshipRecord.getStringValue("entityId", ""));
        } else {
            changedRelationshipRecord.setFieldValue("entityParentId",
                    changedRelationshipRecord.getStringValue("entityId", ""));

            changedRelationshipRecord.setFieldValue("entityChildId",
                    changedRelationshipRecord.getStringValue("referredEntityId", ""));
        }
    }

    private RelationshipType getOriginalRelationship(List<RelationshipType> originalRelationshipList,
                                                     RelationshipType changedRelationship) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalRelationship",
                    new Object[]{originalRelationshipList, changedRelationship});
        }

        RelationshipType relationship = null;

        if (originalRelationshipList != null) {
            for (RelationshipType tempRelationship : originalRelationshipList) {
                if (changedRelationship.getKey().equals(tempRelationship.getKey())) {
                    relationship = tempRelationship;
                    break;
                }
            }
        }

        if (relationship == null) {
            if (!StringUtils.isBlank(changedRelationship.getRelationshipNumberId())) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"Cannot find original Relationship in Previous Value Data Description" +
                                " with Relationship number ID:" + changedRelationship.getRelationshipNumberId() + "."});
                throw new AppException("Cannot find original Relationship in Previous Value Data Description.");
            }
        } else {
            Validator.validateFieldRequired(changedRelationship.getRelationshipNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "Relationship Number ID of an existing Relationship");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalRelationship", relationship);
        }
        return relationship;
    }

    private RelationshipType getRelationshipInDb(PartyInquiryResultType partyInfoInDb,
                                                 String entityType, String entityId, RelationshipType changedRelationship) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRelationshipInDb",
                    new Object[]{partyInfoInDb, entityType, entityId, changedRelationship});
        }

        RelationshipType relationship = null;
        List<RelationshipType> relationshipList = null;

        if (!StringUtils.isBlank(changedRelationship.getRelationshipNumberId()) &&
                partyInfoInDb != null) {
            if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_PERSON)) {
                for (PersonType person : partyInfoInDb.getPerson()) {
                    if (person.getPersonNumberId().equals(entityId)) {
                        relationshipList = person.getRelationship();
                        break;
                    }
                }
            } else if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_ORGANIZATION)) {
                for (OrganizationType organization : partyInfoInDb.getOrganization()) {
                    if (organization.getOrganizationNumberId().equals(entityId)) {
                        relationshipList = organization.getRelationship();
                        break;
                    }
                }
            }
        }

        if (relationshipList != null) {
            for (RelationshipType tempRelationship : relationshipList) {
                if (tempRelationship.getRelationshipNumberId().equals(changedRelationship.getRelationshipNumberId())) {
                    relationship = tempRelationship;
                    break;
                }
            }
        }

        if (!StringUtils.isBlank(changedRelationship.getRelationshipNumberId()) && relationship == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"Cannot find Relationship in DB with Relationship number ID:" + changedRelationship.getRelationshipNumberId() + "."});
            throw new AppException("Cannot find Relationship in DB.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRelationshipInDb", relationship);
        }
        return relationship;
    }

    private Record getRelationshipRecord(String entityId, RelationshipType relationship) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRelationshipRecord", new Object[]{entityId, relationship});
        }

        Record record = null;
        if (relationship != null) {
            record = new Record();
            record.setFieldValue("entityId", entityId);
            mapObjectToRecord(getFieldElementMaps(), relationship, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRelationshipRecord", record);
        }
        return record;
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
