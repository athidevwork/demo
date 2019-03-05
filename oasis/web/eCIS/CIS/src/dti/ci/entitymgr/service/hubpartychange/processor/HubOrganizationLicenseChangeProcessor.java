package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.OrganizationLicenseType;
import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PartyClassificationType;
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
 * Date:   4/23/2016
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
public class HubOrganizationLicenseChangeProcessor extends BaseHubPartyChangeElementProcessor<OrganizationLicenseType> {

    @Override
    public void processFromCisResult(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<OrganizationLicenseType> cisResultElements,
                                     String entityId, List<OrganizationLicenseType> changedElements) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFromCisResult",
                    new Object[]{partyChangeRequest, cisResultElements, entityId, changedElements});
        }
        RecordSet recordSet = new RecordSet();
        for (OrganizationLicenseType cisResultOrgLicense : cisResultElements) {
            boolean foundChangedElement = false;
            for (OrganizationLicenseType changedOrgLicense : changedElements) {
                if (changedOrgLicense.getLicenseNumberId().equals(cisResultOrgLicense.getLicenseNumberId())) {
                    foundChangedElement = true;
                    break;
                }
            }

            if (foundChangedElement) {
                Record inputRecord = getOrganizationLicenseRecord(entityId, cisResultOrgLicense);
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
                              List<OrganizationLicenseType> changedElements, List<OrganizationLicenseType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processForHub", new Object[]{partyChangeRequest, entityType, entityId, changedElements, originalElements});
        }

        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId, partyChangeRequest);

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
                setCommonFieldsToRecord(changedOrganizationLicenseRecord, partyChangeRequest, CISB_N);
                Record result = getHubPartyManager().saveHubParty(changedOrganizationLicenseRecord);

                if (rowStatus.equals(ROW_STATUS_NEW)) {
                    changedOrganizationLicense.setLicenseNumberId(result.getStringValue("newLicenseId"));
                }
            }
        }

        l.exiting(getClass().getName(), "processForHub");
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
