package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.EducationInformationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.educationmgr.EducationManager;
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
 * Date:   9/23/14
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
public class EducationChangeProcessor extends BasePartyChangeElementProcessor<EducationInformationType> {
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
                        List<EducationInformationType> changedElements, List<EducationInformationType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", 
                    new Object[]{partyChangeRequest, partyChangeResult, entityType, entityId, changedElements, originalElements});
        }

        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId);

        for (EducationInformationType changedEducationInfo : changedElements) {
            Validator.validateFieldRequired(changedEducationInfo.getKey(),
                    "ci.partyChangeService.field.required.error", "Education Key");

            EducationInformationType originalEducationInfo = getOriginalEducationInfo(originalElements, changedEducationInfo);
            EducationInformationType dbEducationInfo = getEducationInfoInDb(partyInfoInDb, entityType, entityId, changedEducationInfo);

            Record changedEducationInfoRecord = getEducationRecord(entityId, changedEducationInfo);
            Record originalEducationInfoRecord = getEducationRecord(entityId, originalEducationInfo);
            Record dbEducationInfoRecord = getEducationRecord(entityId, dbEducationInfo);

            String rowStatus = getRowStatus(changedEducationInfoRecord, originalEducationInfoRecord, dbEducationInfoRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedEducationInfoRecord, dbEducationInfoRecord);
                validateEducationInfo(changedEducationInfoRecord);

                Record changedValues = getChangedValues(changedEducationInfoRecord, originalEducationInfoRecord,
                        dbEducationInfoRecord, new String[]{"educationProfileId", "entityAttendeeId"});
                changedValues.setFieldValue(ROW_STATUS, rowStatus);
                saveEducationInfo(changedEducationInfo, changedValues);
            }
        }

        l.exiting(getClass().getName(), "process");
    }

    private EducationInformationType getOriginalEducationInfo(List<EducationInformationType> originalEducationList,
                                                              EducationInformationType changedEducationInformation) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalEducationInfo",
                    new Object[]{originalEducationList, changedEducationInformation});
        }

        EducationInformationType educationInfo = null;

        if (originalEducationList != null) {
            for (EducationInformationType tempEducationInfo : originalEducationList) {
                if (changedEducationInformation.getKey().equals(tempEducationInfo.getKey())) {
                    educationInfo = tempEducationInfo;
                    break;
                }
            }
        }

        if (educationInfo == null) {
            if (!StringUtils.isBlank(changedEducationInformation.getEducationInformationNumberId())) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"Cannot find original Education Information in Previous Value Data Description" +
                                " with education info number ID:" + changedEducationInformation.getEducationInformationNumberId() + "."});
                throw new AppException("Cannot find original Education Information in Previous Value Data Description.");
            }
        } else {
            Validator.validateFieldRequired(changedEducationInformation.getEducationInformationNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "Education Information Number ID of an existing Education Information");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalEducationInfo", educationInfo);
        }
        return educationInfo;
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

    private void validateEducationInfo(Record educationRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateEducationInfo", new Object[]{educationRecord});
        }

        String educationTypeCode = educationRecord.getStringValue("trainingTypeCode", "");
        Validator.validateStringEqual(educationTypeCode, "MEDSCHOOL",
                "ci.partyChangeService.education.educationTypeCode.error", null);

        String institutionName = educationRecord.getStringValue("institutionName", "");
        Validator.validateFieldRequired(institutionName,
                "ci.partyChangeService.field.required.error", "Education Institution Name");

        l.exiting(getClass().getName(), "validateEducationInfo");
    }

    private void saveEducationInfo(EducationInformationType changedEducationInfo,
                                   Record changedEducationRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEducationInfo",
                    new Object[]{changedEducationInfo, changedEducationRecord});
        }

        Record recUpdateResult = getEducationManager().saveEducation(changedEducationRecord);

        if (ROW_STATUS_NEW.equals(changedEducationRecord.getStringValue(ROW_STATUS, ""))) {
            changedEducationInfo.setEducationInformationNumberId(recUpdateResult.getStringValue("newEducationProfileId"));
        }

        l.exiting(getClass().getName(), "saveEducationInfo");
    }

    public EducationManager getEducationManager() {
        return m_educationManager;
    }

    public void setEducationManager(EducationManager educationManager) {
        this.m_educationManager = educationManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private EducationManager m_educationManager;
    private List<FieldElementMap> m_fieldElementMaps;
}
