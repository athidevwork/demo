package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PartyClassificationType;
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
 * Date:   4/20/2016
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
public class HubPartyClassificationChangeProcessor extends BaseHubPartyChangeElementProcessor<PartyClassificationType> {

    @Override
    public void processFromCisResult(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<PartyClassificationType> cisResultElements,
                                     String entityId, List<PartyClassificationType> changedElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFromCisResult",
                    new Object[]{partyChangeRequest, cisResultElements, entityId, changedElements});
        }

        RecordSet recordSet = new RecordSet();
        for (PartyClassificationType cisResultPartyClassification : cisResultElements) {
            boolean foundChangedElement = false;
            for (PartyClassificationType changedPartyClassification : changedElements) {
                if (changedPartyClassification.getClassificationNumberId().equals(cisResultPartyClassification.getClassificationNumberId())) {
                    foundChangedElement = true;
                    break;
                }
            }

            if (foundChangedElement) {
                Record inputRecord = getPartyClassRecord(entityId, cisResultPartyClassification);
                setCommonFieldsToRecord(inputRecord, partyChangeRequest, CISB_Y);
                inputRecord.setFieldValue("entityClassId", cisResultPartyClassification.getClassificationNumberId());
                recordSet.addRecord(inputRecord);
            }
        }

        if (recordSet.getSize() > 0) {
            getHubPartyManager().saveHubPartyInBatch(recordSet);
        }

        l.exiting(getClass().getName(), "processFromCisResult");
    }

    @Override
    public void processForHub(PartyChangeRequestType partyChangeRequest,
                              String entityType, String entityId, List<PartyClassificationType> changedElements, List<PartyClassificationType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processForHub",
                    new Object[]{partyChangeRequest, entityType, entityId, changedElements, originalElements});
        }

        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId,  partyChangeRequest);

        for (PartyClassificationType changedPartyClassification : changedElements) {
            Validator.validateFieldRequired(changedPartyClassification.getKey(), "ci.partyChangeService.field.required.error", "Party Classification Key");

            PartyClassificationType originalPartyClassification = getOriginalPartyClassification(originalElements, changedPartyClassification);
            PartyClassificationType dbPartyClassification = getPartyClassificationInDb(partyInfoInDb, entityType, entityId, changedPartyClassification);

            Record changedPartyClassRecord = getPartyClassRecord(entityId, changedPartyClassification);
            Record originalPartyClassRecord = getPartyClassRecord(entityId, originalPartyClassification);
            Record dbPartyClassRecord = getPartyClassRecord(entityId, dbPartyClassification);

            String rowStatus = getRowStatus(changedPartyClassRecord, originalPartyClassRecord, dbPartyClassRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedPartyClassRecord, dbPartyClassRecord);
                setCommonFieldsToRecord(changedPartyClassRecord, partyChangeRequest, CISB_N);
                Record result = getHubPartyManager().saveHubParty(changedPartyClassRecord);

                if (rowStatus.equals(ROW_STATUS_NEW)) {
                    changedPartyClassification.setClassificationNumberId(result.getStringValue("newEntityClassId"));
                }
            }
        }

        l.exiting(getClass().getName(), "processForHub");
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
