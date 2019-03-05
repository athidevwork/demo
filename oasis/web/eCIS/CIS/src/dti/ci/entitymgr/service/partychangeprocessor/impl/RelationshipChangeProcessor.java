package dti.ci.entitymgr.service.partychangeprocessor.impl;


import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.party.RelationshipType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.ci.relationshipmgr.RelationshipManager;
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
 * Date:   6/19/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/11/2018       Elvin       Issue 195835: rename entityParentFK/entityChildFK to entityParentId/entityChildId
 *                                              remove save logic of entity relation address/phone data, there are no such nodes in xsd
 * ---------------------------------------------------
 */
public class RelationshipChangeProcessor extends BasePartyChangeElementProcessor<RelationshipType> {
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
                        List<RelationshipType> changedElements, List<RelationshipType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, partyChangeResult});
        }

        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId);

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
                    // Get address info for an existing relationship record.
                    Record relationshipDataRecord = getRelationshipManager().loadRelationship(changedRelationshipRecord);
                    changedRelationshipRecord.setFields(relationshipDataRecord, false);
                }

                mergeRecordValues(changedRelationshipRecord, dbRelationshipRecord);

                if (changedRelationshipRecord.getStringValue("reverseRelationIndicator", "").equals("REVERSE RELATION")) {
                    changedRelationshipRecord.setFieldValue("pk",
                            changedRelationshipRecord.getStringValue("referredEntityId", ""));

                    changedRelationshipRecord.setFieldValue("entityParentId",
                            changedRelationshipRecord.getStringValue("referredEntityId", ""));

                    changedRelationshipRecord.setFieldValue("entityChildId",
                            changedRelationshipRecord.getStringValue("entityId", ""));

                    changedRelationshipRecord.setFieldValue("entityId",
                            changedRelationshipRecord.getStringValue("pk", ""));
                } else {
                    changedRelationshipRecord.setFieldValue("pk",
                            changedRelationshipRecord.getStringValue("entityId", ""));

                    changedRelationshipRecord.setFieldValue("entityParentId",
                            changedRelationshipRecord.getStringValue("entityId", ""));

                    changedRelationshipRecord.setFieldValue("entityChildId",
                            changedRelationshipRecord.getStringValue("referredEntityId", ""));
                }
                validateRelationship(changedRelationshipRecord);

                Record changedValues = getChangedValues(changedRelationshipRecord, originalRelationshipRecord, dbRelationshipRecord,
                        new String[]{"pk", "entityId", "entityParentId", "entityChildId", "entityRelationId", "relationTypeCode"});
                changedValues.setFieldValue(ROW_STATUS, rowStatus);
                saveRelationship(changedRelationship, changedValues);
            }
        }

        l.exiting(getClass().getName(), "process");
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

            if (!StringUtils.isBlank(relationship.getRelationshipNumberId())) {
                record.setFieldValue("entityRelationId", relationship.getRelationshipNumberId());
            } else {
                record.setFieldValue("entityRelationId", "");
            }

            mapObjectToRecord(getFieldElementMaps(), relationship, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRelationshipRecord", record);
        }
        return record;
    }

    private void validateRelationship(Record relationshipRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRelationship",
                    new Object[]{relationshipRecord});
        }

        String startDate = relationshipRecord.getStringValue("effectiveFromDate", "");
        String endDate = relationshipRecord.getStringValue("effectiveToDate", "");

        if (!StringUtils.isBlank(startDate) && !StringUtils.isBlank(endDate)) {
            Validator.validateDate2EqualOrAfterDate1(startDate, endDate,
                    "ci.partyChangeService.relationship.effectiveDates.error", null);
        }

        l.exiting(getClass().getName(), "validateRelationship");
    }

    private void saveRelationship(RelationshipType changedRelationship,
                                  Record changedRelationshipRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveRelationship",
                    new Object[]{changedRelationship, changedRelationshipRecord});
        }

        Record result = getRelationshipManager().saveRelationshipWs(changedRelationshipRecord);

        if (ROW_STATUS_NEW.equals(changedRelationshipRecord.getStringValue(ROW_STATUS, ""))) {
            changedRelationship.setRelationshipNumberId(result.getStringValue("entityRelationId"));
        }

        l.exiting(getClass().getName(), "saveRelationship");
    }

    public RelationshipManager getRelationshipManager() {
        return m_relationshipManager;
    }

    public void setRelationshipManager(RelationshipManager relationshipManager) {
        m_relationshipManager = relationshipManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private RelationshipManager m_relationshipManager;
    private List<FieldElementMap> m_fieldElementMaps;
}
