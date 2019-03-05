package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.EducationInformationType;
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
public class HubEducationChangeProcessor extends BaseHubPartyChangeElementProcessor<EducationInformationType> {
    @Override
    public void processFromCisResult(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<EducationInformationType> cisResultElements, String entityId, List<EducationInformationType> changedElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFromCisResult",
                    new Object[]{partyChangeRequest, cisResultElements, entityId, changedElements});
        }

        RecordSet inputRs = new RecordSet();

        for (EducationInformationType educationInResult : cisResultElements) {
            boolean foundChangedElement = false;
            for (EducationInformationType changedEducation : changedElements) {
                if (changedEducation.getEducationInformationNumberId().equals(educationInResult.getEducationInformationNumberId())) {
                    foundChangedElement = true;
                    break;
                }
            }

            if (foundChangedElement) {
                Record inputRecord = getEducationRecord(entityId, educationInResult);
                setCommonFieldsToRecord(inputRecord, partyChangeRequest, CISB_Y);
                inputRecord.setFieldValue("educationInformationNumberId", educationInResult.getEducationInformationNumberId());
                inputRs.addRecord(inputRecord);
            }
        }

        if (inputRs.getSize() > 0) {
            getHubPartyManager().saveHubPartyInBatch(inputRs);
        }

        l.exiting(getClass().getName(), "processFromCisResult");
    }

    @Override
    public void processForHub(PartyChangeRequestType partyChangeRequest, String entityType, String entityId, List<EducationInformationType> changedElements, List<EducationInformationType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processForHub", new Object[]{partyChangeRequest, entityType, entityId, changedElements, originalElements});
        }

        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId, partyChangeRequest);
        for (EducationInformationType changedEducation : changedElements) {
            Validator.validateFieldRequired(changedEducation.getEducationInformationNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "Education Information Number ID of an existing Education Information");

            Record changedRecord = getEducationRecord(entityId, changedEducation);
            Record dbRecrod = getEducationRecord(entityId, getEducationInfoInDb(partyInfoInDb, entityType, entityId, changedEducation));

            mergeRecordValues(changedRecord, dbRecrod);
            setCommonFieldsToRecord(changedRecord, partyChangeRequest, CISB_N);
            Record result = getHubPartyManager().saveHubParty(changedRecord);
            if (!StringUtils.isBlank(result.getStringValue("newEducationProfileId", ""))) {
                changedEducation.setEducationInformationNumberId(result.getStringValue("newEducationProfileId"));
            }
        }

        l.exiting(getClass().getName(), "processForHub");
    }

    private Record getEducationRecord(String entityId, EducationInformationType educationInformation) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEducationRecord", new Object[]{entityId, educationInformation});
        }

        Record record = null;

        if (educationInformation != null) {
            record = new Record();
            record.setFieldValue("entityAttendeeId", entityId);

            if (!StringUtils.isBlank(educationInformation.getEducationInformationNumberId())) {
                record.setFieldValue("educationProfileId", educationInformation.getEducationInformationNumberId());
            }
            mapObjectToRecord(getFieldElementMaps(), educationInformation, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEducationRecord", record);
        }
        return record;
    }

    private EducationInformationType getEducationInfoInDb(PartyInquiryResultType partyInfoInDb,
                                                          String entityType, String entityId, EducationInformationType changedEducationInformation) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEducationInfoInDb",
                    new Object[]{partyInfoInDb, entityType, entityId, changedEducationInformation});
        }

        EducationInformationType educationInfo = null;
        List<EducationInformationType> educationInfoList = null;

        if (!StringUtils.isBlank(changedEducationInformation.getEducationInformationNumberId()) &&
                partyInfoInDb != null) {
            // Only person has education info.
            if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_PERSON)) {
                for (PersonType person : partyInfoInDb.getPerson()) {
                    if (person.getPersonNumberId().equals(entityId)) {
                        educationInfoList = person.getEducationInformation();
                        break;
                    }
                }
            }
        }

        if (educationInfoList != null) {
            for (EducationInformationType tempEducationINfo : educationInfoList) {
                if (tempEducationINfo.getEducationInformationNumberId().equals(
                        changedEducationInformation.getEducationInformationNumberId())) {
                    educationInfo = tempEducationINfo;
                    break;
                }
            }
        }

        if (!StringUtils.isBlank(changedEducationInformation.getEducationInformationNumberId()) && educationInfo == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"Cannot find Education Information in DB with contact number ID:" +
                            changedEducationInformation.getEducationInformationNumberId() + "."});
            throw new AppException("Cannot find Education Information in DB.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEducationInfoInDb", educationInfo);
        }
        return educationInfo;
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
