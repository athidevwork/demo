package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.party.ProfessionalLicenseType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.ci.licensemgr.LicenseManager;
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
 * Date:   9/25/14
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
public class ProfessionalLicenseChangeProcessor extends BasePartyChangeElementProcessor<ProfessionalLicenseType> {
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
                        List<ProfessionalLicenseType> changedElements, List<ProfessionalLicenseType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, partyChangeResult, entityType, entityId, changedElements, originalElements});
        }

        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId);

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
                validateProfessionalLicense(changedProfessionalLicenseRecord);

                Record changedValues = getChangedValues(changedProfessionalLicenseRecord, originalProfessionalLicenseRecord,
                        dbProfessionalLicenseRecord, new String[]{"licenseProfileId", "entityId"});
                changedValues.setFieldValue(ROW_STATUS, rowStatus);
                saveProfessionalLicense(changedProfessionalLicense, changedValues);
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

    private void validateProfessionalLicense(Record professionalLicenseRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateProfessionalLicense",
                    new Object[]{professionalLicenseRecord});
        }

        String licenseStartDate = professionalLicenseRecord.getStringValue("dateLicensed", "");
        String licenseEndDate = professionalLicenseRecord.getStringValue("expirationDate", "");

        if (!StringUtils.isBlank(licenseStartDate) && !StringUtils.isBlank(licenseEndDate)) {
            Validator.validateDate2EqualOrAfterDate1(licenseStartDate, licenseEndDate,
                    "ci.partyChangeService.professionalLicense.effectiveDates.error", null);
        }

        String suspendStartDate = professionalLicenseRecord.getStringValue("licenseSuspendBegDate", "");
        String suspendEndDate = professionalLicenseRecord.getStringValue("licenseSuspendExpDate", "");

        if (!StringUtils.isBlank(suspendStartDate) && !StringUtils.isBlank(suspendEndDate)) {
            Validator.validateDate2EqualOrAfterDate1(suspendStartDate, suspendEndDate,
                    "ci.partyChangeService.professionalLicense.suspendDates.error", null);
        }

        l.exiting(getClass().getName(), "validateProfessionalLicense");
    }

    private void saveProfessionalLicense(ProfessionalLicenseType changedProfessionalLicense,
                                         Record changedProfessionalLicenseRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveProfessionalLicense",
                    new Object[]{changedProfessionalLicense, changedProfessionalLicenseRecord});
        }

        Record recUpdateResult = getLicenseManager().saveLicense(changedProfessionalLicenseRecord);

        if (ROW_STATUS_NEW.equals(changedProfessionalLicenseRecord.getStringValue(ROW_STATUS, ""))) {
            changedProfessionalLicense.setLicenseNumberId(recUpdateResult.getStringValue("newLicenseProfileId"));
        }

        l.exiting(getClass().getName(), "saveProfessionalLicense");
    }

    public LicenseManager getLicenseManager() {
        return m_licenseManager;
    }

    public void setLicenseManager(LicenseManager licenseManager) {
        this.m_licenseManager = licenseManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private LicenseManager m_licenseManager;
    private List<FieldElementMap> m_fieldElementMaps;
}
