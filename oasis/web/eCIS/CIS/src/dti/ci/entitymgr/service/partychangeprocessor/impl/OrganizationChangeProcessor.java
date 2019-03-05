package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.*;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeElementProcessorFactory;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * 05/24/2016       Elvin       Issue 176524: add external_data_id/external_reference_id logic
 * 04/10/2018       jdingle     Issue 192176: fix update for external_reference_id
 * ---------------------------------------------------
 */
public class OrganizationChangeProcessor extends BasePartyChangeProcessor {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Process org
     *
     * @param partyChangeRequest
     * @param partyChangeResult
     */
    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult) {
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
            // firstly, key is always required
            String orgKey = changedOrganization.getKey();
            if (StringUtils.isBlank(orgKey)) {
                throw new AppException("ci.webservice.error", "", new String[]{MessageManager.getInstance().formatMessage("ci.partyChangeService.structureError.keyRequired")});
            }

            OrganizationType originalOrganization = null;

            if (originalOrganizationList == null || originalOrganizationList.size() == 0) {
                // no previous data section, it is an insert to this organization
                // there must not have OrganizationNumberId in this organization
                if (!StringUtils.isBlank(changedOrganization.getOrganizationNumberId())) {
                    throw new AppException("ci.webservice.error", "", new String[]{MessageManager.getInstance().formatMessage("ci.partyChangeService.structureError.idNotAllowed", new String[]{changedOrganization.getOrganizationNumberId()})});
                } else {
                    // we have externalReferenceId or externalDataId passed in, try to find an organization with these external information
                    // if we found any, reject the request
                    if (!StringUtils.isBlank(changedOrganization.getExternalReferenceId()) || !StringUtils.isBlank(changedOrganization.getExternalDataId())) {
                        RecordSet rs = searchEntity(changedOrganization);
                        if (rs.getSize() > 0) {
                            String errorMsg = MessageManager.getInstance().formatMessage("ci.partyChangeService.insertError.alreadyExist", new String[]{rs.getFirstRecord().getStringValue("entityId"), changedOrganization.getExternalReferenceId(), changedOrganization.getExternalDataId()});
                            MessageManager.getInstance().addErrorMessage("ci.webservice.error", new String[]{errorMsg});
                            throw new AppException("ci.webservice.error", "", new String[]{errorMsg});
                        }
                    }
                }
            } else {
                // we have previous data section, but still we need to check it is an insert or update to this organization
                // if we have OrganizationNumberId, then key and OrganizationNumberId should both match previous data
                if (!StringUtils.isBlank(changedOrganization.getOrganizationNumberId())) {
                    for (OrganizationType tempOrg : originalOrganizationList) {
                        if (changedOrganization.getKey().equals(tempOrg.getKey()) && changedOrganization.getOrganizationNumberId().equals(tempOrg.getOrganizationNumberId())) {
                            originalOrganization = tempOrg;
                            break;
                        }
                    }

                    // we have OrganizationNumberId but we cannot find previous data of this organization, it is not allowed
                    if (originalOrganization == null) {
                        throw new AppException("ci.webservice.error", "", new String[]{MessageManager.getInstance().formatMessage("ci.partyChangeService.structureError.noPrevious", new String[]{changedOrganization.getOrganizationNumberId()})});
                    }
                } else {
                    // if no OrganizationNumberId, then we only check key, if key has a match, update; if no match, insert
                    for (OrganizationType tempOrg : originalOrganizationList) {
                        if (changedOrganization.getKey().equals(tempOrg.getKey())) {
                            originalOrganization = tempOrg;
                            break;
                        }
                    }

                    if (originalOrganization == null) {
                        // if originalOrganization == null, it is an insert to this organization
                        // we will query entity table with current external information, if we found any, reject the request
                        if (!StringUtils.isBlank(changedOrganization.getExternalReferenceId()) || !StringUtils.isBlank(changedOrganization.getExternalDataId())) {
                            RecordSet rs = searchEntity(changedOrganization);
                            if (rs.getSize() > 0) {
                                String errorMsg = MessageManager.getInstance().formatMessage("ci.partyChangeService.insertError.alreadyExist", new String[]{rs.getFirstRecord().getStringValue("entityId"), changedOrganization.getExternalReferenceId(), changedOrganization.getExternalDataId()});
                                MessageManager.getInstance().addErrorMessage("ci.webservice.error", new String[]{errorMsg});
                                throw new AppException("ci.webservice.error", "", new String[]{errorMsg});
                            }
                        }
                    } else {
                        // if originalOrganization is not null, it ia a update to this organization
                        // we will query entity table with previous external information
                        // since the external information may be changed in current data part
                        if (!StringUtils.isBlank(originalOrganization.getExternalReferenceId()) || !StringUtils.isBlank(originalOrganization.getExternalDataId())) {
                            RecordSet rs = searchEntity(originalOrganization);
                            if (rs.getSize() == 0) {
                                // we cannot find any entity with pass in external information
                                String errorMsg = MessageManager.getInstance().formatMessage("ci.partyChangeService.updateError.noDataFound", new String[]{originalOrganization.getExternalReferenceId(), originalOrganization.getExternalDataId()});
                                MessageManager.getInstance().addErrorMessage("ci.webservice.error", new String[]{errorMsg});
                                throw new AppException("ci.webservice.error", "", new String[]{errorMsg});
                            } else if (rs.getSize() == 1) {
                                // we found an org which is exactly user wants to update, set entity_pk to the request
                                changedOrganization.setOrganizationNumberId(rs.getFirstRecord().getStringValue("entityId"));
                                originalOrganization.setOrganizationNumberId(rs.getFirstRecord().getStringValue("entityId"));
                            } else if (rs.getSize() > 1) {
                                // more than one entity found will be rejected
                                String errorMsg = MessageManager.getInstance().formatMessage("ci.partyChangeService.updateError.tooManyFound", new String[]{originalOrganization.getExternalReferenceId(), originalOrganization.getExternalDataId()});
                                MessageManager.getInstance().addErrorMessage("ci.webservice.error", new String[]{errorMsg});
                                throw new AppException("ci.webservice.error", "", new String[]{errorMsg});
                            }
                        } else {
                            // here for a update request, we have no organization number id, no external information in original data section, it is not allowed
                            throw new AppException("ci.webservice.error", "", new String[]{MessageManager.getInstance().formatMessage("ci.partyChangeService.structureError.noEnoughData")});
                        }
                    }
                }
            }

            processOrganization(partyChangeRequest, partyChangeResult, changedOrganization, originalOrganization);
        }

        l.exiting(getClass().getName(), "process");
    }

    private void processOrganization(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                                     OrganizationType changedOrganization, OrganizationType originalOrganization) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOrganization",
                    new Object[]{partyChangeRequest, partyChangeResult, changedOrganization, originalOrganization});
        }

        performOrganizationChanges(partyChangeRequest, partyChangeResult, changedOrganization, originalOrganization);

        boolean isOriginalOrgNull = originalOrganization == null;
        List<BasicAddressType> originalBasicAddress = isOriginalOrgNull ? null : originalOrganization.getBasicAddress();
        if (isProcessNeeded(changedOrganization.getBasicAddress(), originalBasicAddress)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(BasicAddressType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getBasicAddress(), originalBasicAddress);
        }

        List<BasicPhoneNumberType> originalBasicPhoneNumber = isOriginalOrgNull ? null : originalOrganization.getBasicPhoneNumber();
        if (isProcessNeeded(changedOrganization.getBasicPhoneNumber(), originalBasicPhoneNumber)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(BasicPhoneNumberType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getBasicPhoneNumber(), originalBasicPhoneNumber);
        }

        List<OrganizationLicenseType> originalOrganizationLicense = isOriginalOrgNull ? null : originalOrganization.getOrganizationLicense();
        if (isProcessNeeded(changedOrganization.getOrganizationLicense(), originalOrganizationLicense)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(OrganizationLicenseType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getOrganizationLicense(), originalOrganizationLicense);
        }

        List<CertificationType> originalCertification = isOriginalOrgNull ? null : originalOrganization.getCertification();
        if (isProcessNeeded(changedOrganization.getCertification(), originalCertification)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(CertificationType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getCertification(), originalCertification);
        }

        List<PartyNoteType> originalPartyNote = isOriginalOrgNull ? null : originalOrganization.getPartyNote();
        if (isProcessNeeded(changedOrganization.getPartyNote(), originalPartyNote)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(PartyNoteType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getPartyNote(), originalPartyNote);
        }

        List<RelationshipType> originalRelationship = isOriginalOrgNull ? null : originalOrganization.getRelationship();
        if (isProcessNeeded(changedOrganization.getRelationship(), originalRelationship)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(RelationshipType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getRelationship(), originalRelationship);
        }

        List<PartyClassificationType> originalPartyClassification = isOriginalOrgNull ? null : originalOrganization.getPartyClassification();
        if (isProcessNeeded(changedOrganization.getPartyClassification(), originalPartyClassification)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(PartyClassificationType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedOrganization.getOrganizationNumberId(),
                    changedOrganization.getPartyClassification(), originalPartyClassification);
        }

        processOrganizationAdditionalInfo(partyChangeRequest, partyChangeResult, changedOrganization);

        processOrganizationAdditionalXmlData(partyChangeRequest, partyChangeResult, changedOrganization);

        handleChangeOrganizationResult(partyChangeRequest, partyChangeResult, changedOrganization) ;

        l.exiting(getClass().getName(), "processOrganization");
    }

    private void performOrganizationChanges(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                                            OrganizationType changedOrganization, OrganizationType originalOrganization) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performOrganizationChanges",
                    new Object[]{partyChangeRequest, partyChangeResult, changedOrganization, originalOrganization});
        }

        Record changedOrganizationRecord = getOrganizationRecord(changedOrganization);
        // ExternalReferenceId
        if (!StringUtils.isBlank(changedOrganization.getExternalReferenceId())) {
            changedOrganizationRecord.setFieldValue("legacyDataId", changedOrganization.getExternalReferenceId());
        }
        Record originalOrganizationRecord = getOrganizationRecord(originalOrganization);

        if (originalOrganization == null) {
            // Adding a new org.
            Record recUpdateResult = getEntityManager().AddEntity(changedOrganizationRecord);

            // Set new org number id.
            String newEntityId = recUpdateResult.getStringValue("newEntityId");
            changedOrganization.setOrganizationNumberId(newEntityId);
        } else {
            OrganizationType organizationInDb = getOrganizationInDb(originalOrganization.getOrganizationNumberId());
            if (organizationInDb == null) {
                throw new AppException("ci.webservice.error", "",
                        new String[]{"Cannot find organization with organization number ID: " + originalOrganization.getOrganizationNumberId() + "."});
            }

            Record dbOrganizationRecord = getOrganizationRecord(organizationInDb);

            String rowStatus = getRowStatus(changedOrganizationRecord, originalOrganizationRecord, dbOrganizationRecord);

            if (rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedOrganizationRecord, originalOrganizationRecord, dbOrganizationRecord);
                validateOrganization(changedOrganizationRecord);

                Record changedValuesRecord = getChangedValues(changedOrganizationRecord, originalOrganizationRecord, dbOrganizationRecord);

                getEntityManager().saveEntityForService(changedValuesRecord);
            }
        }

        l.exiting(getClass().getName(), "performOrganizationChanges");
    }

    private Record getOrganizationRecord(OrganizationType organization) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOrganizationRecord", new Object[]{organization});
        }

        Record record = null;

        if (organization != null) {
            record = new Record();

            record.setFieldValue("entityType", "O");

            // ExternalReferenceId
            if (!StringUtils.isBlank(organization.getExternalReferenceId())) {
                record.setFieldValue("legacyDataId", organization.getExternalReferenceId());
            }

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

    private OrganizationType getOrganizationInDb(String entityId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOrganizationInDb", new Object[]{entityId});
        }

        OrganizationType organization = null;
        PartyInquiryResultType partyInquiryResult = getPartyInfo(entityId);

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
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleChangeOrganizationResult",
                    new Object[]{partyChangeRequest, partyChangeResult, changedOrganization});
        }

        String entityId = changedOrganization.getOrganizationNumberId();
        PartyInquiryResultType updatedPartyInfo = getPartyInfo(entityId);

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
                    // Update Org Key.
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

                    // Record inputRecord = new Record();
                    // inputRecord.setFieldValue("entityId", entityId);
                    // RecordSet certificationRs = getCertificationManager().loadCertification(inputRecord);

                    // Update Certification key
                    for (CertificationType certification : updatedOrganization.getCertification()) {
                        for (CertificationType changedCertification : changedOrganization.getCertification()) {
                            if (certification.getCertificationNumberId().equals(changedCertification.getCertificationNumberId())) {
                                certification.setKey(changedCertification.getKey());
                                break;
                            }
                        }

                        // for (Record certificationRecord : certificationRs.getRecordList()) {
                        //     if (certification.getCertificationNumberId().equals(certificationRecord.getStringValue("riskClassProfileId"))) {
                        //         certification.setCertificationBoard(certificationRecord.getStringValue("entityBoardName", ""));
                        //         break;
                        //     }
                        // }
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

            if (partyChangeRequest.getPartyAdditionalInfo() != null && updatedPartyInfo.getPartyAdditionalInfo() != null) {
                if (partyChangeResult.getPartyAdditionalInfo() == null) {
                    partyChangeResult.setPartyAdditionalInfo(new PartyAdditionalInfoType());
                }

                if (partyChangeRequest.getPartyAdditionalInfo().getOrganizationAdditionalInfo().size() > 0) {
                    // Organization additional info.
                    for (OrganizationAdditionalInfoType organizationAdditionalInfo : updatedPartyInfo.getPartyAdditionalInfo().getOrganizationAdditionalInfo()) {
                        if (entityId.equals(organizationAdditionalInfo.getOrganizationReference())) {
                            organizationAdditionalInfo.setOrganizationReference(changedOrganization.getKey());

                            partyChangeResult.getPartyAdditionalInfo().getOrganizationAdditionalInfo().add(organizationAdditionalInfo);
                            break;
                        }
                    }
                }

                if (partyChangeRequest.getPartyAdditionalInfo().getAddressAdditionalInfo().size() > 0) {
                    // Address additional info.
                    for (AddressAdditionalInfoType addressAdditionalInfo : updatedPartyInfo.getPartyAdditionalInfo().getAddressAdditionalInfo()) {
                        // Change address additional info key.
                        if (addressKeyIdMap.containsKey(addressAdditionalInfo.getAddressReference())) {
                            addressAdditionalInfo.setAddressReference(addressKeyIdMap.get(addressAdditionalInfo.getAddressReference()));
                        }

                        partyChangeResult.getPartyAdditionalInfo().getAddressAdditionalInfo().add(addressAdditionalInfo);
                    }
                }
            }

            if (partyChangeRequest.getPartyAdditionalXmlInfo() != null && updatedPartyInfo.getPartyAdditionalXmlInfo() != null) {
                if (partyChangeResult.getPartyAdditionalXmlInfo() == null) {
                    partyChangeResult.setPartyAdditionalXmlInfo(new PartyAdditionalXmlInfoType());
                }

                if (partyChangeRequest.getPartyAdditionalXmlInfo().getEntityAdditionalXmlData().size() > 0) {
                    for (EntityAdditionalXmlDataType entityAdditionalXmlData : updatedPartyInfo.getPartyAdditionalXmlInfo().getEntityAdditionalXmlData()) {
                        if (entityId.equals(entityAdditionalXmlData.getOrganizationReference())) {
                            entityAdditionalXmlData.setOrganizationReference(changedOrganization.getKey());

                            partyChangeResult.getPartyAdditionalXmlInfo().getEntityAdditionalXmlData().add(entityAdditionalXmlData);
                            break;
                        }
                    }
                }

                if (partyChangeRequest.getPartyAdditionalXmlInfo().getAddressAdditionalXmlData().size() > 0) {
                    for (AddressAdditionalXmlDataType addressAdditionalXmlData : updatedPartyInfo.getPartyAdditionalXmlInfo().getAddressAdditionalXmlData()) {
                        // Change address additional info key.
                        if (addressKeyIdMap.containsKey(addressAdditionalXmlData.getAddressReference())) {
                            addressAdditionalXmlData.setAddressReference(addressKeyIdMap.get(addressAdditionalXmlData.getAddressReference()));
                        }

                        partyChangeResult.getPartyAdditionalXmlInfo().getAddressAdditionalXmlData().add(addressAdditionalXmlData);
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "handleChangeOrganizationResult");
    }

    private void validateOrganization(Record changedOrganizationRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateOrganization", new Object[]{changedOrganizationRecord});
        }

        l.exiting(getClass().getName(), "validateOrganization");
    }

    private RecordSet searchEntity(OrganizationType organization) {
        Record queryRecord = new Record();
        queryRecord.setFieldValue("externalReferenceId", StringUtils.isBlank(organization.getExternalReferenceId()) ? "" : organization.getExternalReferenceId());
        queryRecord.setFieldValue("externalDataId", StringUtils.isBlank(organization.getExternalDataId()) ? "" : organization.getExternalDataId());
        return getEntityManager().searchEntityForWS(queryRecord);
    }

    private void processOrganizationAdditionalInfo(PartyChangeRequestType partyChangeRequest,
                                                   PartyChangeResultType partyChangeResult,
                                                   OrganizationType organization) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOrganizationAdditionalInfo",
                    new Object[]{partyChangeRequest, partyChangeResult, organization});
        }

        OrganizationAdditionalInfoType changedOrganizationAdditionalInfo = getChangedOrganizationAdditionalInfo(partyChangeRequest, organization.getKey());

        if (changedOrganizationAdditionalInfo != null) {
            OrganizationAdditionalInfoType originalOrganizationAdditionalInfo = getOriginalOrganizationAdditionalInfo(partyChangeRequest, organization.getKey());

            PartyChangeElementProcessorFactory.getInstance().getProcessor(OrganizationAdditionalInfoType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), organization.getOrganizationNumberId(),
                    changedOrganizationAdditionalInfo, originalOrganizationAdditionalInfo);
        }

        l.exiting(getClass().getName(), "processOrganizationAdditionalInfo");
    }

    private OrganizationAdditionalInfoType getChangedOrganizationAdditionalInfo(PartyChangeRequestType partyChangeRequest, String organizationKey) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedOrganizationAdditionalInfo", new Object[]{partyChangeRequest, organizationKey});
        }

        OrganizationAdditionalInfoType organizationAdditionalInfo = null;

        if (partyChangeRequest.getPartyAdditionalInfo() != null) {
            for (OrganizationAdditionalInfoType tempOrganizationAdditionalInfo :
                    partyChangeRequest.getPartyAdditionalInfo().getOrganizationAdditionalInfo()) {

                if (organizationKey.equals(tempOrganizationAdditionalInfo.getOrganizationReference())) {
                    organizationAdditionalInfo = tempOrganizationAdditionalInfo;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedOrganizationAdditionalInfo", organizationAdditionalInfo);
        }
        return organizationAdditionalInfo;
    }

    private OrganizationAdditionalInfoType getOriginalOrganizationAdditionalInfo(PartyChangeRequestType partyChangeRequest, String organizationKey) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalOrganizationAdditionalInfo", new Object[]{partyChangeRequest, organizationKey});
        }

        OrganizationAdditionalInfoType organizationAdditionalInfo = null;

        if (partyChangeRequest.getDataModificationInformation() != null &&
                partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription() != null &&
                partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getPartyAdditionalInfo() != null) {

            for (OrganizationAdditionalInfoType tempOrganizationAdditionalInfo :
                    partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getPartyAdditionalInfo().getOrganizationAdditionalInfo()) {

                if (organizationKey.equals(tempOrganizationAdditionalInfo.getOrganizationReference())) {
                    organizationAdditionalInfo = tempOrganizationAdditionalInfo;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalOrganizationAdditionalInfo", organizationAdditionalInfo);
        }
        return organizationAdditionalInfo;
    }

    private void processOrganizationAdditionalXmlData(PartyChangeRequestType partyChangeRequest,
                                                PartyChangeResultType partyChangeResult,
                                                OrganizationType organization) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOrganizationAdditionalXmlData",
                    new Object[]{partyChangeRequest, partyChangeResult, organization});
        }

        if (partyChangeRequest.getPartyAdditionalXmlInfo() != null && partyChangeResult != null && !StringUtils.isBlank(organization.getKey())) {
            for (EntityAdditionalXmlDataType entityAdditionalXmlData :
                    partyChangeRequest.getPartyAdditionalXmlInfo().getEntityAdditionalXmlData()) {
                if (organization.getKey().equals(entityAdditionalXmlData.getOrganizationReference())) {

                    PartyChangeElementProcessorFactory.getInstance().getProcessor(EntityAdditionalXmlDataType.class).process(
                            partyChangeRequest, partyChangeResult,
                            getEntityType(), organization.getOrganizationNumberId(),
                            entityAdditionalXmlData, null);
                    break;
                }
            }
        }

        l.exiting(getClass().getName(), "processOrganizationAdditionalXmlData");
    }

    public String getEntityType() {
        return ENTITY_TYPE_ORGANIZATION;
    }
}
