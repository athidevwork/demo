package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.OrganizationLicenseType;
import com.delphi_tech.ows.party.OrganizationType;
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
 * Date:   11/14/2014
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
public class OrganizationLicenseChangeProcessor extends BasePartyChangeElementProcessor<OrganizationLicenseType> {
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
                        List<OrganizationLicenseType> changedElements, List<OrganizationLicenseType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, partyChangeResult, entityType, entityId, changedElements, originalElements});
        }

        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId);

        for (OrganizationLicenseType changedOrganizationLicense : changedElements) {
            Validator.validateFieldRequired(changedOrganizationLicense.getKey(),
                    "ci.partyChangeService.field.required.error", "Organization License Key");

            OrganizationLicenseType originalOrganizationLicense = getOriginalOrganizationLicense(originalElements, changedOrganizationLicense);
            OrganizationLicenseType dbOrganizationLicense = getOrganizationLicenseInDb(partyInfoInDb, entityType, entityId, changedOrganizationLicense);

            Record changedOrganizationLicenseRecord = getOrganizationLicenseRecord(entityId, changedOrganizationLicense);
            Record originalOrganizationLicenseRecord = getOrganizationLicenseRecord(entityId, originalOrganizationLicense);
            Record dbOrganizationLicenseRecord = getOrganizationLicenseRecord(entityId, dbOrganizationLicense);

            String rowStatus = getRowStatus(changedOrganizationLicenseRecord,
                    originalOrganizationLicenseRecord,
                    dbOrganizationLicenseRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedOrganizationLicenseRecord, dbOrganizationLicenseRecord);
                validateOrganizationLicense(changedOrganizationLicenseRecord);

                Record changedValues = getChangedValues(changedOrganizationLicenseRecord, originalOrganizationLicenseRecord,
                        dbOrganizationLicenseRecord, new String[]{"licenseProfileId", "entityId"});
                changedValues.setFieldValue(ROW_STATUS, rowStatus);

                saveOrganizationLicense(changedOrganizationLicense, changedValues);
            }
        }

        l.exiting(getClass().getName(), "process");
    }

    private OrganizationLicenseType getOriginalOrganizationLicense(
            List<OrganizationLicenseType> originalOrganizationLicenseList,
            OrganizationLicenseType changedOrganizationLicense) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalOrganizationLicense",
                    new Object[]{originalOrganizationLicenseList, changedOrganizationLicense});
        }

        OrganizationLicenseType OrganizationLicense = null;
        if (originalOrganizationLicenseList != null) {
            for (OrganizationLicenseType tempOrganizationLicense : originalOrganizationLicenseList) {
                if (changedOrganizationLicense.getKey().equals(tempOrganizationLicense.getKey())) {
                    OrganizationLicense = tempOrganizationLicense;
                    break;
                }
            }
        }

        if (OrganizationLicense == null) {
            if (!StringUtils.isBlank(changedOrganizationLicense.getLicenseNumberId())) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"Cannot find original Organization License in Previous Value Data Description" +
                                " with Organization License number ID:" + changedOrganizationLicense.getLicenseNumberId() + "."});
                throw new AppException("Cannot find original Organization License in Previous Value Data Description.");
            }
        } else {
            Validator.validateFieldRequired(changedOrganizationLicense.getLicenseNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "Organization License Number ID of an existing Organization Licens");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalOrganizationLicense", OrganizationLicense);
        }
        return OrganizationLicense;
    }

    private OrganizationLicenseType getOrganizationLicenseInDb(PartyInquiryResultType partyInfo,
                                                               String entityType, String entityId,
                                                               OrganizationLicenseType changedOrganizationLicense) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOrganizationLicenseInDb",
                    new Object[]{partyInfo, entityType, entityId, changedOrganizationLicense});
        }

        OrganizationLicenseType OrganizationLicense = null;
        List<OrganizationLicenseType> organizationLicenseList = null;

        if (!StringUtils.isBlank(changedOrganizationLicense.getLicenseNumberId()) &&
                partyInfo != null) {
            if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_ORGANIZATION)) {
                for (OrganizationType organization : partyInfo.getOrganization()) {
                    if (organization.getOrganizationNumberId().equals(entityId)) {
                        organizationLicenseList = organization.getOrganizationLicense();
                        break;
                    }
                }
            }
        }

        if (organizationLicenseList != null) {
            for (OrganizationLicenseType tempOrganizationLicense : organizationLicenseList) {
                if (tempOrganizationLicense.getLicenseNumberId().equals(changedOrganizationLicense.getLicenseNumberId())) {
                    OrganizationLicense = tempOrganizationLicense;
                    break;
                }
            }
        }

        if (!StringUtils.isBlank(changedOrganizationLicense.getLicenseNumberId()) && OrganizationLicense == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"Cannot find Organization License in DB with Organization License number ID:" +
                            changedOrganizationLicense.getLicenseNumberId() + "."});
            throw new AppException("Cannot find Organization License in DB.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOrganizationLicenseInDb", OrganizationLicense);
        }
        return OrganizationLicense;
    }

    private Record getOrganizationLicenseRecord(String entityId, OrganizationLicenseType OrganizationLicense) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOrganizationLicenseRecord",
                    new Object[]{entityId, OrganizationLicense});
        }

        Record record = null;

        if (OrganizationLicense != null) {
            record = new Record();
            record.setFieldValue("entityId", entityId);

            if (!StringUtils.isBlank(OrganizationLicense.getLicenseNumberId())) {
                record.setFieldValue("licenseProfileId", OrganizationLicense.getLicenseNumberId());
            }

            mapObjectToRecord(getFieldElementMaps(), OrganizationLicense, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOrganizationLicenseRecord", record);
        }
        return record;
    }

    private void validateOrganizationLicense(Record OrganizationLicenseRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateOrganizationLicense",
                    new Object[]{OrganizationLicenseRecord});
        }

        String licenseStartDate = OrganizationLicenseRecord.getStringValue("dateLicensed", "");
        String licenseEndDate = OrganizationLicenseRecord.getStringValue("expirationDate", "");

        if (!StringUtils.isBlank(licenseStartDate) && !StringUtils.isBlank(licenseEndDate)) {
            Validator.validateDate2EqualOrAfterDate1(licenseStartDate, licenseEndDate,
                    "ci.partyChangeService.OrganizationLicense.effectiveDates.error", null);
        }

        String suspendStartDate = OrganizationLicenseRecord.getStringValue("licenseSuspendBegDate", "");
        String suspendEndDate = OrganizationLicenseRecord.getStringValue("licenseSuspendExpDate", "");

        if (!StringUtils.isBlank(suspendStartDate) && !StringUtils.isBlank(suspendEndDate)) {
            Validator.validateDate2EqualOrAfterDate1(suspendStartDate, suspendEndDate,
                    "ci.partyChangeService.OrganizationLicense.suspendDates.error", null);
        }

        l.exiting(getClass().getName(), "validateOrganizationLicense");
    }

    private void saveOrganizationLicense(OrganizationLicenseType changedOrganizationLicense,
                                         Record changedOrganizationLicenseRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveOrganizationLicense",
                    new Object[]{changedOrganizationLicense, changedOrganizationLicenseRecord});
        }

        Record recUpdateResult = getLicenseManager().saveLicense(changedOrganizationLicenseRecord);

        if (ROW_STATUS_NEW.equals(changedOrganizationLicenseRecord.getStringValue(ROW_STATUS, ""))) {
            changedOrganizationLicense.setLicenseNumberId(recUpdateResult.getStringValue("newLicenseProfileId"));
        }

        l.exiting(getClass().getName(), "saveOrganizationLicense");
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
