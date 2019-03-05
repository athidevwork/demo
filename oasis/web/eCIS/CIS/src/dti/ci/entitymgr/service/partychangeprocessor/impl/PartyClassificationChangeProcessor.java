package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PartyClassificationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.entityclassmgr.EntityClassManager;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.ows.validation.Validator;
import dti.oasis.recordset.Record;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/20/14
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
public class PartyClassificationChangeProcessor extends BasePartyChangeElementProcessor<PartyClassificationType> {
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
                        List<PartyClassificationType> changedElements, List<PartyClassificationType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process",
                    new Object[]{partyChangeRequest, partyChangeResult, entityType, entityId, changedElements, originalElements});
        }

        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId);

        for (PartyClassificationType changedPartyClassification : changedElements) {
            Validator.validateFieldRequired(changedPartyClassification.getKey(),
                    "ci.partyChangeService.field.required.error", "Party Classification Key");

            PartyClassificationType originalPartyClassification = getOriginalPartyClassification(originalElements, changedPartyClassification);
            PartyClassificationType dbPartyClassification = getPartyClassificationInDb(partyInfoInDb, entityType, entityId, changedPartyClassification);

            Record changedPartyClassRecord = getPartyClassRecord(entityId, changedPartyClassification);
            Record originalPartyClassRecord = getPartyClassRecord(entityId, originalPartyClassification);
            Record dbPartyClassRecord = getPartyClassRecord(entityId, dbPartyClassification);

            String rowStatus = getRowStatus(changedPartyClassRecord, originalPartyClassRecord, dbPartyClassRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedPartyClassRecord, dbPartyClassRecord);
                validatePartyClassification(changedPartyClassRecord);

                Record changedValues = getChangedValues(changedPartyClassRecord, originalPartyClassRecord,
                        dbPartyClassRecord, new String[]{"entityClassId", "entityId"});
                changedValues.setFieldValue(ROW_STATUS, rowStatus);

                savePartyClassification(changedPartyClassification, changedValues);
            }
        }
    }

    private PartyClassificationType getOriginalPartyClassification(List<PartyClassificationType> originalPartyClassList,
                                                                   PartyClassificationType changedPartyClassification) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalPartyClassification",
                    new Object[]{originalPartyClassList, changedPartyClassification});
        }

        PartyClassificationType originalPartyClass = null;

        if (originalPartyClassList != null) {
            for (PartyClassificationType tempPartyClass : originalPartyClassList) {
                if (changedPartyClassification.getKey().equals(tempPartyClass.getKey())) {
                    originalPartyClass = tempPartyClass;
                    break;
                }
            }
        }

        if (originalPartyClass == null) {
            if (!StringUtils.isBlank(changedPartyClassification.getClassificationNumberId())) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"Cannot find original Party Classification in Previous Value Data Description" +
                                " with party classification number ID:" + changedPartyClassification.getClassificationNumberId() + "."});
                throw new AppException("Cannot find original Party Classification in Previous Value Data Description.");
            }
        } else {
            Validator.validateFieldRequired(changedPartyClassification.getClassificationNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "Party Classification Number ID of an existing Party Classification");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalPartyClassification", originalPartyClass);
        }
        return originalPartyClass;
    }


    private PartyClassificationType getPartyClassificationInDb(PartyInquiryResultType partyInfoInDb,
                                                               String entityType, String entityId,
                                                               PartyClassificationType changedPartyClass) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPartyClassificationInDb",
                    new Object[]{partyInfoInDb, entityType, entityId, changedPartyClass});
        }

        PartyClassificationType partyClassification = null;
        List<PartyClassificationType> partyClassificationList = null;

        if (!StringUtils.isBlank(changedPartyClass.getClassificationNumberId()) &&
                partyInfoInDb != null) {
            if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_PERSON)) {
                for (PersonType person : partyInfoInDb.getPerson()) {
                    if (person.getPersonNumberId().equals(entityId)) {
                        partyClassificationList = person.getPartyClassification();
                        break;
                    }
                }
            } else if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_ORGANIZATION)) {
                for (OrganizationType organization : partyInfoInDb.getOrganization()) {
                    if (organization.getOrganizationNumberId().equals(entityId)) {
                        partyClassificationList = organization.getPartyClassification();
                    }
                }
            }
        }

        if (partyClassificationList != null) {
            for (PartyClassificationType tempPartyClass : partyClassificationList) {
                if (tempPartyClass.getClassificationNumberId().equals(changedPartyClass.getClassificationNumberId())) {
                    partyClassification = tempPartyClass;
                    break;
                }
            }
        }

        if (!StringUtils.isBlank(changedPartyClass.getClassificationNumberId()) && partyClassification == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"Cannot find Party Classification in DB with party classification number ID:" +
                            changedPartyClass.getClassificationNumberId() + "."});
            throw new AppException("Cannot find Party Classification in DB.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPartyClassificationInDb", partyClassification);
        }
        return partyClassification;
    }

    private Record getPartyClassRecord(String entityId, PartyClassificationType partyClassification) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPartyClassRecord", new Object[]{entityId, partyClassification});
        }

        Record record = null;

        if (partyClassification != null) {
            record = new Record();

            record.setFieldValue("entityId", entityId);

            if (!StringUtils.isBlank(partyClassification.getClassificationNumberId())) {
                record.setFieldValue("entityClassId", partyClassification.getClassificationNumberId());
            }
            mapObjectToRecord(getFieldElementMaps(), partyClassification, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPartyClassRecord", record);
        }
        return record;
    }

    private void validatePartyClassification(Record changedPartyClassRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePartyClassification", new Object[]{changedPartyClassRecord});
        }

        String classificationCode = changedPartyClassRecord.getStringValue("entityClassCode", "");
        Validator.validateFieldRequired(classificationCode,
                "ci.partyChangeService.field.required.error", "Party Classification Code");

        String startDate = changedPartyClassRecord.getStringValue("effectiveFromDate", "");
        String endDate = changedPartyClassRecord.getStringValue("effectiveToDate", "");

        if (!StringUtils.isBlank(startDate) && !StringUtils.isBlank(endDate)) {
            Validator.validateDate2EqualOrAfterDate1(startDate, endDate,
                    "ci.partyChangeService.partyClass.effectiveDates.error", null);
        }

        l.exiting(getClass().getName(), "validatePartyClassification");
    }

    private void savePartyClassification(PartyClassificationType changedPartyClassification,
                                         Record changedPartyClassRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePartyClassification",
                    new Object[]{changedPartyClassification, changedPartyClassRecord});
        }

        boolean hasOverlapClass = getEntityClassManager().hasOverlapEntityClass(changedPartyClassRecord);
        if (hasOverlapClass) {
            String entityClassCode = changedPartyClassRecord.getStringValue("entityClassCode", "");
            MessageManager.getInstance().addErrorMessage(
                    "ci.partyChangeService.classification.overlapClass",
                    new Object[]{entityClassCode});
            throw new AppException("Classification overlaps with another record.");
        }

        Record result = getEntityClassManager().saveEntityClassWs(changedPartyClassRecord);
        if (ROW_STATUS_NEW.equals(changedPartyClassRecord.getStringValue(ROW_STATUS, ""))) {
            changedPartyClassification.setClassificationNumberId(result.getStringValue("newEntityClassId", ""));
        }

        l.exiting(getClass().getName(), "savePartyClassification");
    }



    public EntityClassManager getEntityClassManager() {
        return m_entityClassManager;
    }

    public void setEntityClassManager(EntityClassManager entityClassManager) {
        m_entityClassManager = entityClassManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private EntityClassManager m_entityClassManager;
    private List<FieldElementMap> m_fieldElementMaps;
}
