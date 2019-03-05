package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.*;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.entitymgr.service.hubpartychange.HubPartyChangeElementProcessorFactory;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/19/2016
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
public class HubOrganizationChangeProcessor extends BaseHubPartyChangeProcessor {

    /**
     * Process person
     *
     * @param partyChangeRequest
     * @param partyChangeResult
     */
    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process",
                    new Object[]{partyChangeRequest, partyChangeResult});
        }

        List<OrganizationType> changedOrganizationList = partyChangeRequest.getOrganization();
        List<OrganizationType> originalOrganizationList = null;

        if (partyChangeRequest.getDataModificationInformation() != null &&
                partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription() != null) {
            originalOrganizationList = partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getOrganization();
        }

        for (OrganizationType changedOrganization : changedOrganizationList) {
            OrganizationType originalOrganization = null;
            OrganizationType cisResultOrg = null;

            if (!StringUtils.isBlank(changedOrganization.getOrganizationNumberId())) {
                if (originalOrganizationList != null) {
                    for (OrganizationType tempOrg : originalOrganizationList) {
                        if (changedOrganization.getOrganizationNumberId().equals(tempOrg.getOrganizationNumberId())) {
                            originalOrganization = tempOrg;
                            break;
                        }
                    }
                }

                for (OrganizationType resultOrg : partyChangeResult.getOrganization()) {
                    if (changedOrganization.getKey().equals(resultOrg.getKey())) {
                        cisResultOrg = resultOrg;
                        break;
                    }
                }

                if (originalOrganization == null && cisResultOrg == null) {
                    MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                            new Object[]{"Cannot find the previous organization in PartyChangeRequest with organization number ID :"
                                    + changedOrganization.getOrganizationNumberId() + "."});
                    throw new AppException("Cannot find the previous organization in PartyChangeRequest with organization number ID.");
                }
            }

            processOrganization(partyChangeRequest, partyChangeResult, changedOrganization, originalOrganization, cisResultOrg);
        }

        l.exiting(getClass().getName(), "process");
    }

    private void processOrganization(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                                     OrganizationType changedOrganization, OrganizationType originalOrganization, OrganizationType cisResultOrg) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOrganization",
                    new Object[]{partyChangeRequest, partyChangeResult, changedOrganization, originalOrganization, cisResultOrg});
        }

        performOrganizationChanges(partyChangeRequest, cisResultOrg, changedOrganization, originalOrganization);

        boolean isOriginalOrgNull = originalOrganization == null;
        boolean isCisResultOrgNull = cisResultOrg == null;

        List<BasicAddressType> originalBasicAddress = isOriginalOrgNull ? null : originalOrganization.getBasicAddress();

        if (isProcessNeeded(changedOrganization.getBasicAddress(), originalBasicAddress)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(BasicAddressType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultOrgNull ? null : cisResultOrg.getBasicAddress()),
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getBasicAddress(), originalBasicAddress);
        }

        List<BasicPhoneNumberType> originalBasicPhoneNumber = isOriginalOrgNull ? null : originalOrganization.getBasicPhoneNumber();
        if (isProcessNeeded(changedOrganization.getBasicPhoneNumber(), originalBasicPhoneNumber)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(BasicPhoneNumberType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultOrgNull ? null : cisResultOrg.getBasicPhoneNumber()),
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getBasicPhoneNumber(), originalBasicPhoneNumber);
        }

        List<OrganizationLicenseType> originalOrgLicense = isOriginalOrgNull ? null : originalOrganization.getOrganizationLicense();
        if (isProcessNeeded(changedOrganization.getOrganizationLicense(), originalOrgLicense)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(OrganizationLicenseType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultOrgNull ? null : cisResultOrg.getOrganizationLicense()),
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getOrganizationLicense(), originalOrgLicense);
        }

        List<CertificationType> originalCertification = isOriginalOrgNull ? null : originalOrganization.getCertification();
        if (isProcessNeeded(changedOrganization.getCertification(), originalCertification)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(CertificationType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultOrgNull ? null : cisResultOrg.getCertification()),
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getCertification(), originalCertification);
        }

        List<PartyNoteType> originalPartyNote = isOriginalOrgNull ? null : originalOrganization.getPartyNote();
        if (isProcessNeeded(changedOrganization.getPartyNote(), originalPartyNote)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(PartyNoteType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultOrgNull ? null : cisResultOrg.getPartyNote()),
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getPartyNote(), originalPartyNote);
        }

        List<RelationshipType> originalRelationshipList = isOriginalOrgNull ? null : originalOrganization.getRelationship();
        if (isProcessNeeded(changedOrganization.getRelationship(), originalRelationshipList)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(RelationshipType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultOrgNull ? null : cisResultOrg.getRelationship()),
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getRelationship(), originalRelationshipList);
        }

        List<PartyClassificationType> originalPartyClassification = isOriginalOrgNull ? null : originalOrganization.getPartyClassification();
        if (isProcessNeeded(changedOrganization.getPartyClassification(), originalPartyClassification)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(PartyClassificationType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultOrgNull ? null : cisResultOrg.getPartyClassification()),
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getPartyClassification(), originalPartyClassification);
        }

        handleChangeOrganizationResult(partyChangeRequest, partyChangeResult, changedOrganization) ;

        l.exiting(getClass().getName(), "processOrganization");
    }


    private void performOrganizationChanges(PartyChangeRequestType partyChangeRequest, OrganizationType cisResultOrg,
                                            OrganizationType changedOrganization, OrganizationType originalOrganization) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performOrganizationChanges",
                    new Object[]{partyChangeRequest, cisResultOrg, changedOrganization, originalOrganization});
        }

        String orgKey = changedOrganization.getKey();
        if (StringUtils.isBlank(orgKey)) {
            throw new AppException("ci.webservice.error", "",
                    new String[]{"Organization key cannot be empty."});
        }

        Record changedOrganizationRecord = getOrganizationRecord(changedOrganization);
        Record originalOrganizationRecord = getOrganizationRecord(originalOrganization);

        if (originalOrganization == null) {
            if (cisResultOrg != null) {
                saveHubOrg(cisResultOrg, partyChangeRequest);
            } else{
                // Adding a new organization.
                setCommonFieldsToRecord(changedOrganizationRecord, partyChangeRequest, CISB_N);
                Record result = getHubPartyManager().saveHubParty(changedOrganizationRecord);

                String newEntityId = result.getStringValue("newEntityId");
                changedOrganization.setOrganizationNumberId(newEntityId);
            }
        } else {
            if (cisResultOrg != null) {
                saveHubOrg(cisResultOrg, partyChangeRequest);
            } else {
                OrganizationType organizationInDb = getOrganizationInDb(originalOrganization.getOrganizationNumberId(), partyChangeRequest);
                if (organizationInDb == null) {
                    throw new AppException("ci.webservice.error", "",
                            new String[]{"Cannot find organization with organization number ID: " + originalOrganization.getOrganizationNumberId() + "."});
                }

                Record dbOrganizationRecord = getOrganizationRecord(organizationInDb);

                String rowStatus = getRowStatus(changedOrganizationRecord, originalOrganizationRecord, dbOrganizationRecord);

                if (rowStatus.equals(ROW_STATUS_MODIFIED)) {
                    mergeRecordValues(changedOrganizationRecord, dbOrganizationRecord);
                    setCommonFieldsToRecord(changedOrganizationRecord, partyChangeRequest, CISB_N);
                    getHubPartyManager().saveHubParty(changedOrganizationRecord);
                }
            }
        }

        l.exiting(getClass().getName(), "performOrganizationChanges");
    }

    private void saveHubOrg(OrganizationType cisResultOrg, PartyChangeRequestType partyChangeRequest) {
        Record cisResultOrgRecord = getOrganizationRecord(cisResultOrg);
        setCommonFieldsToRecord(cisResultOrgRecord, partyChangeRequest, CISB_Y);
        getHubPartyManager().saveHubParty(cisResultOrgRecord);
    }

    private Record getOrganizationRecord(OrganizationType organization) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOrganizationRecord", new Object[]{organization});
        }

        Record record = null;

        if (organization != null) {
            record = new Record();

            record.setFieldValue("entityType", "O");

            if (organization.getOrganizationNumberId() != null) {
                record.setFieldValue("entityId", organization.getOrganizationNumberId());
            }

            mapObjectToRecord(getFieldElementMaps(), organization, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOrganizationRecord", record);
        }
        return record;
    }

    private OrganizationType getOrganizationInDb(String entityId, PartyChangeRequestType partyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOrganizationInDb", new Object[]{entityId});
        }

        OrganizationType organization = null;
        PartyInquiryResultType partyInquiryResult = getPartyInfo(entityId, partyChangeRequest);

        if (partyInquiryResult != null && partyInquiryResult.getOrganization() != null) {
            for (OrganizationType tempOrganization : partyInquiryResult.getOrganization()) {
                if (entityId.equals(tempOrganization.getOrganizationNumberId())) {
                    organization = tempOrganization;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOrganizationInDb", organization);
        }
        return organization;
    }

    private void handleChangeOrganizationResult(PartyChangeRequestType partyChangeRequest,
                                                PartyChangeResultType partyChangeResult,
                                                OrganizationType changedOrganization) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleChangeOrganizationResult",
                    new Object[]{partyChangeRequest, partyChangeResult, changedOrganization});
        }

        String entityId = changedOrganization.getOrganizationNumberId();
        PartyInquiryResultType updatedPartyInfo = getPartyInfo(entityId, partyChangeRequest);

        if (updatedPartyInfo != null) {
            Map<String, String> addressKeyIdMap = new HashMap<String, String>();
            for (AddressType address : partyChangeRequest.getAddress()) {
                addressKeyIdMap.put(address.getAddressNumberId(), address.getKey());
            }

            Map<String, String> phoneNumberKeyIdMap = new HashMap<String, String>();
            for (BasicPhoneNumberType phoneNumber : changedOrganization.getBasicPhoneNumber()) {
                phoneNumberKeyIdMap.put(phoneNumber.getPhoneNumberId(), phoneNumber.getKey());
            }

            Map<String, String> basicAddressKeyReferenceMap = new HashMap<String, String>();
            for (BasicAddressType basicAddress : changedOrganization.getBasicAddress()) {
                basicAddressKeyReferenceMap.put(basicAddress.getAddressReference(), basicAddress.getKey());
            }

            for (AddressType updatedAddress : updatedPartyInfo.getAddress()) {
                if (addressKeyIdMap.containsKey(updatedAddress.getAddressNumberId())) {
                    updatedAddress.setKey(addressKeyIdMap.get(updatedAddress.getAddressNumberId()));
                }
                partyChangeResult.getAddress().add(updatedAddress);
            }

            List<OrganizationType> updatedOrganizationList = updatedPartyInfo.getOrganization();
            for (OrganizationType updatedOrganization : updatedOrganizationList) {
                if (entityId.equals(updatedOrganization.getOrganizationNumberId())) {
                    // Update Organization Key.
                    updatedOrganization.setKey(changedOrganization.getKey());

                    // Update phone number key.
                    for (BasicPhoneNumberType updatedPhoneNumber : updatedOrganization.getBasicPhoneNumber()) {
                        if (phoneNumberKeyIdMap.containsKey(updatedPhoneNumber.getPhoneNumberId())) {
                            updatedPhoneNumber.setKey(phoneNumberKeyIdMap.get(updatedPhoneNumber.getPhoneNumberId()));

                            if (!StringUtils.isBlank(updatedPhoneNumber.getAddressReference())) {
                                if (addressKeyIdMap.containsKey(updatedPhoneNumber.getAddressReference())) {
                                    updatedPhoneNumber.setAddressReference(addressKeyIdMap.get(updatedPhoneNumber.getAddressReference()));
                                }
                            }
                        }
                    }

                    // Update address key
                    for (BasicAddressType updatedBasicAddress : updatedOrganization.getBasicAddress()) {
                        if (addressKeyIdMap.containsKey(updatedBasicAddress.getAddressReference())) {
                            updatedBasicAddress.setAddressReference(addressKeyIdMap.get(updatedBasicAddress.getAddressReference()));
                        }

                        if (basicAddressKeyReferenceMap.containsKey(updatedBasicAddress.getAddressReference())) {
                            updatedBasicAddress.setKey(basicAddressKeyReferenceMap.get(updatedBasicAddress.getAddressReference()));
                        }
                    }

                    // Update Professional License key
                    for (OrganizationLicenseType organizationLicense : updatedOrganization.getOrganizationLicense()) {
                        for (OrganizationLicenseType changedOrganizationLicense : changedOrganization.getOrganizationLicense()) {
                            if (organizationLicense.getLicenseNumberId().equals(changedOrganizationLicense.getLicenseNumberId())) {
                                organizationLicense.setKey(changedOrganizationLicense.getKey());
                                break;
                            }
                        }
                    }

                    // Update Certification key
                    for (CertificationType certification : updatedOrganization.getCertification()) {
                        for (CertificationType changedCertification : changedOrganization.getCertification()) {
                            if (certification.getCertificationNumberId().equals(changedCertification.getCertificationNumberId())) {
                                certification.setKey(changedCertification.getKey());
                                break;
                            }
                        }
                    }

                    // Update Party Note Key
                    for (PartyNoteType partyNote : updatedOrganization.getPartyNote()) {
                        for (PartyNoteType changedPartyNote : changedOrganization.getPartyNote()) {
                            if (partyNote.getPartyNoteNumberId().equals(changedPartyNote.getPartyNoteNumberId())) {
                                partyNote.setKey(changedPartyNote.getKey());
                                break;
                            }
                        }
                    }

                    // Update Relationship Key
                    for (RelationshipType relationship : updatedOrganization.getRelationship()) {
                        for (RelationshipType changedRelationship : changedOrganization.getRelationship()) {
                            if (relationship.getRelationshipNumberId().equals(changedRelationship.getRelationshipNumberId())) {
                                relationship.setKey(changedRelationship.getKey());
                                break;
                            }
                        }
                    }

                    // Update Party Class Key
                    for (PartyClassificationType partyClass : updatedOrganization.getPartyClassification()) {
                        for (PartyClassificationType changedPartyClass : changedOrganization.getPartyClassification()) {
                            if (partyClass.getClassificationNumberId().equals(changedPartyClass.getClassificationNumberId())) {
                                partyClass.setKey(changedPartyClass.getKey());
                                break;
                            }
                        }
                    }

                    partyChangeResult.getOrganization().add(updatedOrganization);
                    break;
                }
            }
        }

        l.exiting(getClass().getName(), "handleChangeOrganizationResult");
    }

    public String getEntityType() {
        return ENTITY_TYPE_ORGANIZATION;
    }
}
