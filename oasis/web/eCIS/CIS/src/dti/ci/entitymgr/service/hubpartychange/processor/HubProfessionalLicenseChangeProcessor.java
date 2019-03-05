package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.party.ProfessionalLicenseType;
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
 * Date:   5/18/2016
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
public class HubProfessionalLicenseChangeProcessor extends BaseHubPartyChangeElementProcessor<ProfessionalLicenseType> {

    @Override
    public void processFromCisResult(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                                     List<ProfessionalLicenseType> cisResultElements, String entityId, List<ProfessionalLicenseType> changedElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFromCisResult", new Object[]{partyChangeRequest, cisResultElements, entityId, changedElements});
        }

        RecordSet recordSet = new RecordSet();
        for (ProfessionalLicenseType cisResultProfessionalLicense : cisResultElements) {
            boolean foundChangedElement = false;
            for (ProfessionalLicenseType changedProfessionalLicense : changedElements) {
                if (changedProfessionalLicense.getLicenseNumberId().equals(cisResultProfessionalLicense.getLicenseNumberId())) {
                    foundChangedElement = true;
                    break;
                }
            }

            if (foundChangedElement) {
                Record inputRecord = getProfessionalLicenseRecord(entityId, cisResultProfessionalLicense);
                setCommonFieldsToRecord(inputRecord, partyChangeRequest, CISB_Y);
                recordSet.addRecord(inputRecord);
            }
        }

        if (recordSet.getSize() > 0) {
            getHubPartyManager().saveHubPartyInBatch(recordSet);
        }
        l.exiting(getClass().getName(), "processFromCisResult");
    }

    @Override
    public void processForHub(PartyChangeRequestType partyChangeRequest, String entityType, String entityId,
                              List<ProfessionalLicenseType> changedElements, List<ProfessionalLicenseType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, entityType, entityId, changedElements, originalElements});
        }

        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId, partyChangeRequest);

        for (ProfessionalLicenseType changedProfessionalLicense : changedElements) {
            Validator.validateFieldRequired(changedProfessionalLicense.getKey(),
                    "ci.partyChangeService.field.required.error", "Party Professional License Key");

            ProfessionalLicenseType originalProfessionalLicense = getOriginalProfessionalLicense(originalElements, changedProfessionalLicense);
            ProfessionalLicenseType dbProfessionalLicense = getProfessionalLicenseInDb(partyInfoInDb, entityType, entityId, changedProfessionalLicense);

            Record changedProfessionalLicenseRecord = getProfessionalLicenseRecord(entityId, changedProfessionalLicense);
            Record originalProfessionalLicenseRecord = getProfessionalLicenseRecord(entityId, originalProfessionalLicense);
            Record dbProfessionalLicenseRecord = getProfessionalLicenseRecord(entityId, dbProfessionalLicense);

            String rowStatus = getRowStatus(changedProfessionalLicenseRecord,
                    originalProfessionalLicenseRecord,
                    dbProfessionalLicenseRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedProfessionalLicenseRecord, dbProfessionalLicenseRecord);
                Record result = getHubPartyManager().saveHubParty(changedProfessionalLicenseRecord);

                if (rowStatus.equals(ROW_STATUS_NEW)) {
                    changedProfessionalLicense.setLicenseNumberId(result.getStringValue("newLicenseId"));
                }
            }
        }

        l.exiting(getClass().getName(), "process");
    }

    private ProfessionalLicenseType getOriginalProfessionalLicense(
            List<ProfessionalLicenseType> originalProfessionalLicenseList,
            ProfessionalLicenseType changedProfessionalLicense) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalProfessionalLicense",
                    new Object[]{originalProfessionalLicenseList, changedProfessionalLicense});
        }

        ProfessionalLicenseType professionalLicense = null;
        if (originalProfessionalLicenseList != null) {
            for (ProfessionalLicenseType tempProfessionalLicense : originalProfessionalLicenseList) {
                if (changedProfessionalLicense.getKey().equals(tempProfessionalLicense.getKey())) {
                    professionalLicense = tempProfessionalLicense;
                    break;
                }
            }
        }

        if (professionalLicense == null) {
            if (!StringUtils.isBlank(changedProfessionalLicense.getLicenseNumberId())) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"Cannot find original Professional License in Previous Value Data Description" +
                                " with Professional License number ID:" + changedProfessionalLicense.getLicenseNumberId() + "."});
                throw new AppException("Cannot find original Professional License in Previous Value Data Description.");
            }
        } else {
            Validator.validateFieldRequired(changedProfessionalLicense.getLicenseNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "Professional License Number ID of an existing Professional Licens");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalProfessionalLicense", professionalLicense);
        }
        return professionalLicense;
    }

    private ProfessionalLicenseType getProfessionalLicenseInDb(PartyInquiryResultType partyInfo,
                                                               String entityType, String entityId,
                                                               ProfessionalLicenseType changedProfessionalLicense) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProfessionalLicenseInDb",
                    new Object[]{partyInfo, entityType, entityId, changedProfessionalLicense});
        }

        ProfessionalLicenseType professionalLicense = null;
        List<ProfessionalLicenseType> professionalLicenseList = null;

        if (!StringUtils.isBlank(changedProfessionalLicense.getLicenseNumberId()) &&
                partyInfo != null) {
            if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_PERSON)) {
                for (PersonType person : partyInfo.getPerson()) {
                    if (person.getPersonNumberId().equals(entityId)) {
                        professionalLicenseList = person.getProfessionalLicense();
                        break;
                    }
                }
            }
        }

        if (professionalLicenseList != null) {
            for (ProfessionalLicenseType tempProfessionalLicense : professionalLicenseList) {
                if (tempProfessionalLicense.getLicenseNumberId().equals(changedProfessionalLicense.getLicenseNumberId())) {
                    professionalLicense = tempProfessionalLicense;
                    break;
                }
            }
        }

        if (!StringUtils.isBlank(changedProfessionalLicense.getLicenseNumberId()) && professionalLicense == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"Cannot find Professional License in DB with Professional License number ID:" +
                            changedProfessionalLicense.getLicenseNumberId() + "."});
            throw new AppException("Cannot find Professional License in DB.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProfessionalLicenseInDb", professionalLicense);
        }
        return professionalLicense;
    }

    private Record getProfessionalLicenseRecord(String entityId, ProfessionalLicenseType professionalLicense) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProfessionalLicenseRecord",
                    new Object[]{entityId, professionalLicense});
        }

        Record record = null;

        if (professionalLicense != null) {
            record = new Record();
            record.setFieldValue("entityId", entityId);

            if (!StringUtils.isBlank(professionalLicense.getLicenseNumberId())) {
                record.setFieldValue("licenseProfileId", professionalLicense.getLicenseNumberId());
            }

            mapObjectToRecord(getFieldElementMaps(), professionalLicense, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProfessionalLicenseRecord", record);
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
