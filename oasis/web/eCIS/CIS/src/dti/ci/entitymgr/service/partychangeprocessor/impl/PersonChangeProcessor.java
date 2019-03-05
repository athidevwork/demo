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
public class PersonChangeProcessor extends BasePartyChangeProcessor {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Process person
     * @param partyChangeRequest
     * @param partyChangeResult
     */
    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, partyChangeResult});
        }

        List<PersonType> changedPersonList = partyChangeRequest.getPerson();
        List<PersonType> originalPersonList = null;

        if (partyChangeRequest.getDataModificationInformation() != null &&
                partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription() != null) {
            originalPersonList = partyChangeRequest.getDataModificationInformation()
                    .getPreviousDataValueDescription().getPerson();
        }

        for (PersonType changedPerson : changedPersonList) {
            // firstly, key is always required
            String personKey = changedPerson.getKey();
            if (StringUtils.isBlank(personKey)) {
                throw new AppException("ci.webservice.error", "", new String[]{MessageManager.getInstance().formatMessage("ci.partyChangeService.structureError.keyRequired")});
            }

            // The original person of the changed person.
            PersonType originalPerson = null;

            if (originalPersonList == null || originalPersonList.size() == 0) {
                // no previous data section, it is an insert to this person
                // there must not have PersonNumberId in this person
                if (!StringUtils.isBlank(changedPerson.getPersonNumberId())) {
                    throw new AppException("ci.webservice.error", "", new String[]{MessageManager.getInstance().formatMessage("ci.partyChangeService.structureError.idNotAllowed", new String[]{changedPerson.getPersonNumberId()})});
                } else {
                    // we have externalReferenceId or externalDataId passed in, try to find a person with these external information
                    // if we found any, reject the request
                    if (!StringUtils.isBlank(changedPerson.getExternalReferenceId()) || !StringUtils.isBlank(changedPerson.getExternalDataId())) {
                        RecordSet rs = searchEntity(changedPerson);
                        if (rs.getSize() > 0) {
                            String errorMsg = MessageManager.getInstance().formatMessage("ci.partyChangeService.insertError.alreadyExist", new String[]{rs.getFirstRecord().getStringValue("entityId"), changedPerson.getExternalReferenceId(), changedPerson.getExternalDataId()});
                            MessageManager.getInstance().addErrorMessage("ci.webservice.error", new String[]{errorMsg});
                            throw new AppException("ci.webservice.error", "", new String[]{errorMsg});
                        }
                    }
                }
            } else {
                // we have previous data section, but still we need to check it is an insert or update to this person
                // if we have PersonNumberId, then key and PersonNumberId should both match previous data
                if (!StringUtils.isBlank(changedPerson.getPersonNumberId())) {
                    for (PersonType tempPerson : originalPersonList) {
                        if (changedPerson.getKey().equals(tempPerson.getKey()) && changedPerson.getPersonNumberId().equals(tempPerson.getPersonNumberId())) {
                            originalPerson = tempPerson;
                            break;
                        }
                    }

                    // we have PersonNumberId but we cannot find previous data of this person, it is not allowed
                    if (originalPerson == null) {
                        throw new AppException("ci.webservice.error", "", new String[]{MessageManager.getInstance().formatMessage("ci.partyChangeService.structureError.noPrevious", new String[]{changedPerson.getPersonNumberId()})});
                    }
                } else {
                    // if no PersonNumberId, then we only check key, if key has a match, update; if no match, insert
                    for (PersonType tempPerson : originalPersonList) {
                        if (changedPerson.getKey().equals(tempPerson.getKey())) {
                            originalPerson = tempPerson;
                            break;
                        }
                    }

                    if (originalPerson == null) {
                        // if originalPerson == null, it is an insert to this person
                        // we will query entity table with current external information, if we found any, reject the request
                        if (!StringUtils.isBlank(changedPerson.getExternalReferenceId()) || !StringUtils.isBlank(changedPerson.getExternalDataId())) {
                            RecordSet rs = searchEntity(changedPerson);
                            if (rs.getSize() > 0) {
                                String errorMsg = MessageManager.getInstance().formatMessage("ci.partyChangeService.insertError.alreadyExist", new String[]{rs.getFirstRecord().getStringValue("entityId"), changedPerson.getExternalReferenceId(), changedPerson.getExternalDataId()});
                                MessageManager.getInstance().addErrorMessage("ci.webservice.error", new String[]{errorMsg});
                                throw new AppException("ci.webservice.error", "", new String[]{errorMsg});
                            }
                        }
                    } else {
                        // if originalPerson is not null, it ia a update to this person
                        // we will query entity table with previous external information
                        // since the external information may be changed in current data part
                        if (!StringUtils.isBlank(originalPerson.getExternalReferenceId()) || !StringUtils.isBlank(originalPerson.getExternalDataId())) {
                            RecordSet rs = searchEntity(originalPerson);
                            if (rs.getSize() == 0) {
                                // we cannot find any entity with pass in external information
                                String errorMsg = MessageManager.getInstance().formatMessage("ci.partyChangeService.updateError.noDataFound", new String[]{originalPerson.getExternalReferenceId(), originalPerson.getExternalDataId()});
                                MessageManager.getInstance().addErrorMessage("ci.webservice.error", new String[]{errorMsg});
                                throw new AppException("ci.webservice.error", "", new String[]{errorMsg});
                            } else if (rs.getSize() == 1) {
                                // we found a person which is exactly user wants to update, set entity_pk to the request
                                changedPerson.setPersonNumberId(rs.getFirstRecord().getStringValue("entityId"));
                                originalPerson.setPersonNumberId(rs.getFirstRecord().getStringValue("entityId"));
                            } else if (rs.getSize() > 1) {
                                // more than one entity found will be rejected
                                String errorMsg = MessageManager.getInstance().formatMessage("ci.partyChangeService.updateError.tooManyFound", new String[]{originalPerson.getExternalReferenceId(), originalPerson.getExternalDataId()});
                                MessageManager.getInstance().addErrorMessage("ci.webservice.error", new String[]{errorMsg});
                                throw new AppException("ci.webservice.error", "", new String[]{errorMsg});
                            }
                        } else {
                            // here for a update request, we have no person number id, no external information in original data section, it is not allowed
                            throw new AppException("ci.webservice.error", "", new String[]{MessageManager.getInstance().formatMessage("ci.partyChangeService.structureError.noEnoughData")});
                        }
                    }
                }
            }

            processPerson(partyChangeRequest, partyChangeResult, changedPerson, originalPerson);
        }

        l.exiting(getClass().getName(), "process");
    }

    /**
     * Process person
     * @param partyChangeRequest
     * @param partyChangeResult
     * @param changedPerson
     * @param originalPerson
     */
    private void processPerson(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                               PersonType changedPerson, PersonType originalPerson) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processPerson",
                    new Object[]{partyChangeRequest, partyChangeResult, changedPerson, originalPerson});
        }

        performPersonChanges(partyChangeRequest, partyChangeResult, changedPerson, originalPerson);

        boolean isOriginalPersonNull = originalPerson == null;

        List<BasicAddressType> originalBasicAddress = isOriginalPersonNull ? null : originalPerson.getBasicAddress();
        if (isProcessNeeded(changedPerson.getBasicAddress(), originalBasicAddress)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(BasicAddressType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getBasicAddress(), originalBasicAddress);
        }

        List<BasicPhoneNumberType> originalBasicPhoneNumber = isOriginalPersonNull ? null : originalPerson.getBasicPhoneNumber();
        if (isProcessNeeded(changedPerson.getBasicPhoneNumber(), originalBasicPhoneNumber)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(BasicPhoneNumberType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getBasicPhoneNumber(), originalBasicPhoneNumber);
        }

        List<EducationInformationType> originalEducationInfo = isOriginalPersonNull ? null : originalPerson.getEducationInformation();
        if (isProcessNeeded(changedPerson.getEducationInformation(), originalEducationInfo)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(EducationInformationType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getEducationInformation(), originalEducationInfo);
        }

        List<ProfessionalLicenseType> originalProLicense = isOriginalPersonNull ? null : originalPerson.getProfessionalLicense();
        if (isProcessNeeded(changedPerson.getProfessionalLicense(), originalProLicense)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(ProfessionalLicenseType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getProfessionalLicense(), originalProLicense);
        }

        List<CertificationType> originalCertification = isOriginalPersonNull ? null : originalPerson.getCertification();
        if (isProcessNeeded(changedPerson.getCertification(), originalCertification)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(CertificationType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getCertification(), originalCertification);
        }

        List<ContactType> originalContact = isOriginalPersonNull ? null : originalPerson.getContact();
        if (isProcessNeeded(changedPerson.getContact(), originalContact)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(ContactType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getContact(), originalContact);
        }

        List<PartyNoteType> originalPartyNote = isOriginalPersonNull ? null : originalPerson.getPartyNote();
        if (isProcessNeeded(changedPerson.getPartyNote(), originalPartyNote)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(PartyNoteType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getPartyNote(), originalPartyNote);
        }

        List<RelationshipType> originalRelationship = isOriginalPersonNull ? null : originalPerson.getRelationship();
        if (isProcessNeeded(changedPerson.getRelationship(), originalRelationship)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(RelationshipType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getRelationship(), originalRelationship);
        }

        List<PartyClassificationType> originalPartyClassification = isOriginalPersonNull ? null : originalPerson.getPartyClassification();
        if (isProcessNeeded(changedPerson.getPartyClassification(), originalPartyClassification)) {
            PartyChangeElementProcessorFactory.getInstance().getProcessor(PartyClassificationType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getPartyClassification(), originalPartyClassification);
        }

        processPersonAdditionalInfo(partyChangeRequest, partyChangeResult, changedPerson);

        processPersonAdditionalXmlData(partyChangeRequest, partyChangeResult, changedPerson);

        handleChangePersonResult(partyChangeRequest, partyChangeResult, changedPerson);

        l.exiting(getClass().getName(), "processPerson");
    }

    /**
     * Perform person changes.
     * @param partyChangeRequest
     * @param partyChangeResult
     * @param changedPerson
     * @param originalPerson
     */
    private void performPersonChanges(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                                      PersonType changedPerson, PersonType originalPerson) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performPersonChanges",
                    new Object[]{partyChangeRequest, partyChangeResult, changedPerson, originalPerson});
        }

        Record changedPersonRecord = getPersonRecord(changedPerson);
        // ExternalReferenceId
        if (!StringUtils.isBlank(changedPerson.getExternalReferenceId())) {
            changedPersonRecord.setFieldValue("legacyDataId", changedPerson.getExternalReferenceId());
        }
        Record originalPersonRecord = getPersonRecord(originalPerson);

        if (originalPerson == null) {
            // Adding a new person.
            Record recUpdateResult = getEntityManager().AddEntity(changedPersonRecord);

            // Set new person number id.
            String newEntityId = recUpdateResult.getStringValue("newEntityId");
            changedPerson.setPersonNumberId(newEntityId);
        } else {
            PersonType personInDb = getPersonInDb(originalPerson.getPersonNumberId());
            if (personInDb == null) {
                throw new AppException("ci.webservice.error", "",
                        new String[]{"Cannot find person with person number ID: " + originalPerson.getPersonNumberId() + "."});
            }

            Record dbPersonRecord = getPersonRecord(personInDb);

            String rowStatus = getRowStatus(changedPersonRecord, originalPersonRecord, dbPersonRecord);

            if (rowStatus.equals(ROW_STATUS_MODIFIED)) {
                // Merge record values for validation.
                mergeRecordValues(changedPersonRecord, originalPersonRecord, dbPersonRecord);
                validatePerson(changedPersonRecord);

                // Get changed values for updating
                Record changedValues = getChangedValues(changedPersonRecord, originalPersonRecord, dbPersonRecord);
                getEntityManager().saveEntityForService(changedValues);
            }
        }

        l.exiting(getClass().getName(), "performPersonChanges");
    }

    private Record getPersonRecord(PersonType person) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPersonRecord", new Object[]{person});
        }

        Record record =null;

        if (person != null) {
            record = new Record();
            // Entity Type
            record.setFieldValue("entityType", "P");

            // Entity Id
            if (!StringUtils.isBlank(person.getPersonNumberId())) {
                record.setFieldValue("entityId", person.getPersonNumberId());
            }

            mapObjectToRecord(getFieldElementMaps(), person, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPersonRecord", record);
        }
        return record;
    }

    private PersonType getPersonInDb(String entityId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPersonInDb", new Object[]{entityId});
        }

        PersonType person = null;
        PartyInquiryResultType partyInquiryResult = getPartyInfo(entityId);

        if (partyInquiryResult != null && partyInquiryResult.getPerson() != null) {
            for (PersonType tempPerson : partyInquiryResult.getPerson()) {
                if (entityId.equals(tempPerson.getPersonNumberId())) {
                    person = tempPerson;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPersonInDb", person);
        }
        return person;
    }

    private void handleChangePersonResult(PartyChangeRequestType partyChangeRequest,
                                          PartyChangeResultType partyChangeResult,
                                          PersonType changedPerson) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleChangePersonResult",
                    new Object[]{partyChangeRequest, partyChangeResult, changedPerson});
        }

        String entityId = changedPerson.getPersonNumberId();
        PartyInquiryResultType updatedPartyInfo = getPartyInfo(entityId);

        if (updatedPartyInfo != null) {
            Map<String, String> addressKeyIdMap = new HashMap<String, String>();
            for (AddressType address : partyChangeRequest.getAddress()) {
                addressKeyIdMap.put(address.getAddressNumberId(), address.getKey());
            }

            Map<String, String> phoneNumberKeyIdMap = new HashMap<String, String>();
            for (BasicPhoneNumberType phoneNumber : changedPerson.getBasicPhoneNumber()) {
                phoneNumberKeyIdMap.put(phoneNumber.getPhoneNumberId(), phoneNumber.getKey());
            }

            Map<String, String> basicAddressKeyReferenceMap = new HashMap<String, String>();
            for (BasicAddressType basicAddress : changedPerson.getBasicAddress()) {
                basicAddressKeyReferenceMap.put(basicAddress.getAddressReference(), basicAddress.getKey());
            }

            for (AddressType updatedAddress : updatedPartyInfo.getAddress()) {
                if (addressKeyIdMap.containsKey(updatedAddress.getAddressNumberId())) {
                    updatedAddress.setKey(addressKeyIdMap.get(updatedAddress.getAddressNumberId()));
                }
                partyChangeResult.getAddress().add(updatedAddress);
            }

            List<PersonType> updatedPersonList = updatedPartyInfo.getPerson();
            for (PersonType updatedPerson : updatedPersonList) {
                if (entityId.equals(updatedPerson.getPersonNumberId())) {
                    // Update Person Key.
                    updatedPerson.setKey(changedPerson.getKey());

                    // Update phone number key.
                    for (BasicPhoneNumberType updatedPhoneNumber : updatedPerson.getBasicPhoneNumber()) {
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
                    for (BasicAddressType updatedBasicAddress : updatedPerson.getBasicAddress()) {
                        if (addressKeyIdMap.containsKey(updatedBasicAddress.getAddressReference())) {
                            updatedBasicAddress.setAddressReference(addressKeyIdMap.get(updatedBasicAddress.getAddressReference()));
                        }

                        if (basicAddressKeyReferenceMap.containsKey(updatedBasicAddress.getAddressReference())) {
                            updatedBasicAddress.setKey(basicAddressKeyReferenceMap.get(updatedBasicAddress.getAddressReference()));
                        }
                    }

                    // Update education key
                    for (EducationInformationType educationInformation : updatedPerson.getEducationInformation()) {
                        for (EducationInformationType changedEducationInformation : changedPerson.getEducationInformation()) {
                            if (educationInformation.getEducationInformationNumberId().equals(changedEducationInformation.getEducationInformationNumberId())) {
                                educationInformation.setKey(changedEducationInformation.getKey());
                                break;
                            }
                        }
                    }

                    // Update Professional License key
                    for (ProfessionalLicenseType professionalLicense : updatedPerson.getProfessionalLicense()) {
                        for (ProfessionalLicenseType changedProfessionalLicense : changedPerson.getProfessionalLicense()) {
                            if (professionalLicense.getLicenseNumberId().equals(changedProfessionalLicense.getLicenseNumberId())) {
                                professionalLicense.setKey(changedProfessionalLicense.getKey());
                                break;
                            }
                        }
                    }

                    // Update Certification key
                    // Record inputRecord = new Record();
                    // inputRecord.setFieldValue("entityId", entityId);
                    // RecordSet certificationRs = getCertificationManager().loadCertification(inputRecord);

                    for (CertificationType certification : updatedPerson.getCertification()) {
                        for (CertificationType changedCertification : changedPerson.getCertification()) {
                            if (certification.getCertificationNumberId().equals(changedCertification.getCertificationNumberId())) {
                                certification.setKey(changedCertification.getKey());
                                break;
                            }
                        }

                        // Update board name.
                        // for (Record certificationRecord : certificationRs.getRecordList()) {
                        //     if (certification.getCertificationNumberId().equals(certificationRecord.getStringValue("riskClassProfileId"))) {
                        //         certification.setCertificationBoard(certificationRecord.getStringValue("entityBoardId", ""));
                        //         break;
                        //     }
                        // }
                    }

                    // Update Contact  key
                    for (ContactType contact : updatedPerson.getContact()) {
                        for (ContactType changedContact : changedPerson.getContact()) {
                            if (contact.getContactNumberId().equals(changedContact.getContactNumberId())) {
                                contact.setKey(changedContact.getKey());
                                break;
                            }
                        }
                    }

                    // Update Party Note Key
                    for (PartyNoteType partyNote : updatedPerson.getPartyNote()) {
                        for (PartyNoteType changedPartyNote : changedPerson.getPartyNote()) {
                            if (partyNote.getPartyNoteNumberId().equals(changedPartyNote.getPartyNoteNumberId())) {
                                partyNote.setKey(changedPartyNote.getKey());
                                break;
                            }
                        }
                    }

                    // Update Relationship Key
                    for (RelationshipType relationship : updatedPerson.getRelationship()) {
                        for (RelationshipType changedRelationship : changedPerson.getRelationship()) {
                            if (relationship.getRelationshipNumberId().equals(changedRelationship.getRelationshipNumberId())) {
                                relationship.setKey(changedRelationship.getKey());
                                break;
                            }
                        }
                    }

                    // Update Party Class Key
                    for (PartyClassificationType partyClass : updatedPerson.getPartyClassification()) {
                        for (PartyClassificationType changedPartyClass : changedPerson.getPartyClassification()) {
                            if (partyClass.getClassificationNumberId().equals(changedPartyClass.getClassificationNumberId())) {
                                partyClass.setKey(changedPartyClass.getKey());
                                break;
                            }
                        }
                    }

                    partyChangeResult.getPerson().add(updatedPerson);
                    break;
                }
            }

            if (partyChangeRequest.getPartyAdditionalInfo() != null && updatedPartyInfo.getPartyAdditionalInfo() != null) {
                if (partyChangeResult.getPartyAdditionalInfo() == null) {
                    partyChangeResult.setPartyAdditionalInfo(new PartyAdditionalInfoType());
                }

                if (partyChangeRequest.getPartyAdditionalInfo().getPersonAdditionalInfo().size() > 0) {
                    // Person additional info.
                    for (PersonAdditionalInfoType personAdditionalInfo : updatedPartyInfo.getPartyAdditionalInfo().getPersonAdditionalInfo()) {
                        if (entityId.equals(personAdditionalInfo.getPersonReference())) {
                            personAdditionalInfo.setPersonReference(changedPerson.getKey());

                            partyChangeResult.getPartyAdditionalInfo().getPersonAdditionalInfo().add(personAdditionalInfo);

                            // One person only can have one additional info.
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
                        if (entityId.equals(entityAdditionalXmlData.getPersonReference())) {
                            entityAdditionalXmlData.setPersonReference(changedPerson.getKey());

                            partyChangeResult.getPartyAdditionalXmlInfo().getEntityAdditionalXmlData().add(entityAdditionalXmlData);
                            // One person only can have one additional xml data record.
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

        l.exiting(getClass().getName(), "handleChangePersonResult");
    }

    private void validatePerson(Record personRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePerson", new Object[]{personRecord});
        }

        l.exiting(getClass().getName(), "validatePerson");
    }

    private RecordSet searchEntity(PersonType person) {
        Record queryRecord = new Record();
        queryRecord.setFieldValue("externalReferenceId", StringUtils.isBlank(person.getExternalReferenceId()) ? "" : person.getExternalReferenceId());
        queryRecord.setFieldValue("externalDataId", StringUtils.isBlank(person.getExternalDataId()) ? "" : person.getExternalDataId());
        return getEntityManager().searchEntityForWS(queryRecord);
    }

    private void processPersonAdditionalInfo(PartyChangeRequestType partyChangeRequest,
                                             PartyChangeResultType partyChangeResult,
                                             PersonType person) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processPersonAdditionalInfo",
                    new Object[]{partyChangeRequest, partyChangeResult, person});
        }

        PersonAdditionalInfoType changedPersonAdditionalInfo = getChangedPersonAdditionalInfo(partyChangeRequest, person.getKey());

        if (changedPersonAdditionalInfo != null) {
            PersonAdditionalInfoType originalPersonAdditionalInfo = getOriginalPersonAdditionalInfo(partyChangeRequest, person.getKey());

            PartyChangeElementProcessorFactory.getInstance().getProcessor(PersonAdditionalInfoType.class).process(
                    partyChangeRequest, partyChangeResult,
                    getEntityType(), person.getPersonNumberId(),
                    changedPersonAdditionalInfo, originalPersonAdditionalInfo);
        }

        l.exiting(getClass().getName(), "processPersonAdditionalInfo");
    }

    private PersonAdditionalInfoType getChangedPersonAdditionalInfo(PartyChangeRequestType partyChangeRequest, String personKey) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedPersonAdditionalInfo", new Object[]{partyChangeRequest, personKey});
        }

        PersonAdditionalInfoType personAdditionalInfo = null;

        if (partyChangeRequest.getPartyAdditionalInfo() != null) {
            for (PersonAdditionalInfoType tempPersonAdditionalInfo :
                    partyChangeRequest.getPartyAdditionalInfo().getPersonAdditionalInfo()) {

                if (personKey.equals(tempPersonAdditionalInfo.getPersonReference())) {
                    personAdditionalInfo = tempPersonAdditionalInfo;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedPersonAdditionalInfo", personAdditionalInfo);
        }
        return personAdditionalInfo;
    }

    private PersonAdditionalInfoType getOriginalPersonAdditionalInfo(PartyChangeRequestType partyChangeRequest, String personKey) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalPersonAdditionalInfo", new Object[]{partyChangeRequest, personKey});
        }

        PersonAdditionalInfoType personAdditionalInfo = null;

        if (partyChangeRequest.getDataModificationInformation() != null &&
                partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription() != null &&
                partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getPartyAdditionalInfo() != null) {

            for (PersonAdditionalInfoType tempPersonAdditionalInfo :
                    partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getPartyAdditionalInfo().getPersonAdditionalInfo()) {

                if (personKey.equals(tempPersonAdditionalInfo.getPersonReference())) {
                    personAdditionalInfo = tempPersonAdditionalInfo;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalPersonAdditionalInfo", personAdditionalInfo);
        }
        return personAdditionalInfo;
    }

    private void processPersonAdditionalXmlData(PartyChangeRequestType partyChangeRequest,
                                                PartyChangeResultType partyChangeResult,
                                                PersonType person) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processPersonAdditionalXmlData",
                    new Object[]{partyChangeRequest, partyChangeResult, person});
        }

        if (partyChangeRequest.getPartyAdditionalXmlInfo() != null && partyChangeResult != null && !StringUtils.isBlank(person.getKey())) {
            for (EntityAdditionalXmlDataType entityAdditionalXmlData :
                    partyChangeRequest.getPartyAdditionalXmlInfo().getEntityAdditionalXmlData()) {
                if (person.getKey().equals(entityAdditionalXmlData.getPersonReference())) {

                    PartyChangeElementProcessorFactory.getInstance().getProcessor(EntityAdditionalXmlDataType.class).process(
                            partyChangeRequest, partyChangeResult,
                            getEntityType(), person.getPersonNumberId(),
                            entityAdditionalXmlData, null);
                    break;
                }
            }
        }

        l.exiting(getClass().getName(), "processPersonAdditionalXmlData");
    }

    public String getEntityType() {
        return ENTITY_TYPE_PERSON;
    }
}
