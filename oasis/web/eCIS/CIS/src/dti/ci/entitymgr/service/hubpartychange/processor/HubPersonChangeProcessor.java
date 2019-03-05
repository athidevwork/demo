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
public class HubPersonChangeProcessor extends BaseHubPartyChangeProcessor  {
    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult) {
        Logger l = LogUtils.getLogger(getClass());
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
            // The original person of the changed person.
            PersonType originalPerson = null;
            PersonType cisResultPerson = null;

            // If the person number id of a changed person is not empty, it should be an existing person.
            if (!StringUtils.isBlank(changedPerson.getPersonNumberId())) {
                if (originalPersonList != null) {
                    for (PersonType tempPerson : originalPersonList) {
                        if (changedPerson.getPersonNumberId().equals(tempPerson.getPersonNumberId())) {
                            originalPerson = tempPerson;
                            break;
                        }
                    }
                }

                for (PersonType resultPerson : partyChangeResult.getPerson()) {
                    if (changedPerson.getKey().equals(resultPerson.getKey())) {
                        cisResultPerson = resultPerson;
                        break;
                    }
                }

                if (originalPerson == null && cisResultPerson == null) {
                    MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                            new Object[]{"Cannot find the previous person in PartyChangeRequest with person number ID :"
                                    + changedPerson.getPersonNumberId() + "."});
                    throw new AppException("Cannot find the previous person in PartyChangeRequest with person number ID.");
                }
            }

            processPerson(partyChangeRequest, partyChangeResult, changedPerson, originalPerson, cisResultPerson);
        }

        l.exiting(getClass().getName(), "process");
    }

    private void processPerson(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                               PersonType changedPerson, PersonType originalPerson, PersonType cisResultPerson) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processPerson",
                    new Object[]{partyChangeRequest, partyChangeResult, changedPerson, originalPerson, cisResultPerson});
        }

        performPersonChanges(partyChangeRequest, cisResultPerson, changedPerson, originalPerson);

        boolean isOriginalPersonNull = originalPerson == null;
        boolean isCisResultPersonNull = cisResultPerson == null;

        List<BasicAddressType> originalBasicAddress = isOriginalPersonNull ? null : originalPerson.getBasicAddress();
        if (isProcessNeeded(changedPerson.getBasicAddress(), originalBasicAddress)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(BasicAddressType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultPersonNull ? null : cisResultPerson.getBasicAddress()),
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getBasicAddress(), originalBasicAddress);
        }

        List<BasicPhoneNumberType> originalBasicPhoneNumber = isOriginalPersonNull ? null : originalPerson.getBasicPhoneNumber();
        if (isProcessNeeded(changedPerson.getBasicPhoneNumber(), originalBasicPhoneNumber)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(BasicPhoneNumberType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultPersonNull ? null : cisResultPerson.getBasicPhoneNumber()),
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getBasicPhoneNumber(), originalBasicPhoneNumber);
        }

        List<EducationInformationType> originalEducationInfo = isOriginalPersonNull ? null : originalPerson.getEducationInformation();
        if (isProcessNeeded(changedPerson.getEducationInformation(), originalEducationInfo)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(EducationInformationType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultPersonNull ? null : cisResultPerson.getEducationInformation()),
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getEducationInformation(), originalEducationInfo);
        }

        List<ProfessionalLicenseType> originalProLicense = isOriginalPersonNull ? null : originalPerson.getProfessionalLicense();
        if (isProcessNeeded(changedPerson.getProfessionalLicense(), originalProLicense)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(ProfessionalLicenseType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultPersonNull ? null : cisResultPerson.getProfessionalLicense()),
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getProfessionalLicense(), originalProLicense);
        }

        List<CertificationType> originalCertification = isOriginalPersonNull ? null : originalPerson.getCertification();
        if (isProcessNeeded(changedPerson.getCertification(), originalCertification)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(CertificationType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultPersonNull ? null : cisResultPerson.getCertification()),
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getCertification(), originalCertification);
        }

        List<ContactType> originalContact = isOriginalPersonNull ? null : originalPerson.getContact();
        if (isProcessNeeded(changedPerson.getContact(), originalContact)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(ContactType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultPersonNull ? null : cisResultPerson.getContact()),
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getContact(), originalContact);
        }

        List<PartyNoteType> originalPartyNote = isOriginalPersonNull ? null : originalPerson.getPartyNote();
        if (isProcessNeeded(changedPerson.getPartyNote(), originalPartyNote)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(PartyNoteType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultPersonNull ? null : cisResultPerson.getPartyNote()),
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getPartyNote(), originalPartyNote);
        }

        List<RelationshipType> originalRelationship = isOriginalPersonNull ? null : originalPerson.getRelationship();
        if (isProcessNeeded(changedPerson.getRelationship(), originalRelationship)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(RelationshipType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultPersonNull ? null : cisResultPerson.getRelationship()),
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getRelationship(), originalRelationship);
        }

        List<PartyClassificationType> originalPartyClassification = isOriginalPersonNull ? null : originalPerson.getPartyClassification();
        if (isProcessNeeded(changedPerson.getPartyClassification(), originalPartyClassification)) {
            HubPartyChangeElementProcessorFactory.getInstance().getProcessor(PartyClassificationType.class).process(
                    partyChangeRequest, partyChangeResult, (isCisResultPersonNull ? null : cisResultPerson.getPartyClassification()),
                    getEntityType(), changedPerson.getPersonNumberId(),
                    changedPerson.getPartyClassification(), originalPartyClassification);
        }

        handleChangePersonResult(partyChangeRequest, partyChangeResult, changedPerson);

        l.exiting(getClass().getName(), "processPerson");
    }

    private void performPersonChanges(PartyChangeRequestType partyChangeRequest, PersonType cisResultPerson,
                                      PersonType changedPerson, PersonType originalPerson) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performPersonChanges",
                    new Object[]{partyChangeRequest, cisResultPerson, changedPerson, originalPerson});
        }

        String personKey = changedPerson.getKey();
        if (StringUtils.isBlank(personKey)) {
            throw new AppException("ci.webservice.error", "",
                    new String[]{"Person key cannot be empty."});
        }

        Record changedPersonRecord = getPersonRecord(changedPerson);
        Record originalPersonRecord = getPersonRecord(originalPerson);

        if (originalPerson == null) {
            //Insertion
            if (cisResultPerson != null) {
                saveHubPerson(cisResultPerson, partyChangeRequest);
            } else {
                setCommonFieldsToRecord(changedPersonRecord, partyChangeRequest, CISB_N);
                Record result = getHubPartyManager().saveHubParty(changedPersonRecord);

                String newEntityId = result.getStringValue("newEntityId");
                changedPerson.setPersonNumberId(newEntityId);
            }
        } else {
            //Update
            if (cisResultPerson != null) {
                saveHubPerson(cisResultPerson, partyChangeRequest);
            } else {
                PersonType personInDb = getPersonInDb(originalPerson.getPersonNumberId(), partyChangeRequest);
                if (personInDb == null) {
                    throw new AppException("ci.webservice.error", "",
                            new String[]{"Cannot find person with person number ID: " + originalPerson.getPersonNumberId() + "."});
                }

                Record dbPersonRecord = getPersonRecord(personInDb);
                String rowStatus = getRowStatus(changedPersonRecord, originalPersonRecord, dbPersonRecord);

                if (ROW_STATUS_MODIFIED.equals(rowStatus)) {
                    mergeRecordValues(changedPersonRecord, dbPersonRecord);
                    setCommonFieldsToRecord(changedPersonRecord, partyChangeRequest, CISB_N);
                    getHubPartyManager().saveHubParty(changedPersonRecord);
                }
            }
        }

        l.exiting(getClass().getName(), "performPersonChanges");
    }

    private void saveHubPerson(PersonType cisResultPerson, PartyChangeRequestType partyChangeRequest) {
        Record cisResultPersonRecord = getPersonRecord(cisResultPerson);
        setCommonFieldsToRecord(cisResultPersonRecord, partyChangeRequest, CISB_Y);
        getHubPartyManager().saveHubParty(cisResultPersonRecord);
    }

    private Record getPersonRecord(PersonType person) {
        Logger l = LogUtils.getLogger(getClass());
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

    private PersonType getPersonInDb(String entityId, PartyChangeRequestType partyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPersonInDb", new Object[]{entityId});
        }

        PersonType person = null;
        PartyInquiryResultType partyInquiryResult = getPartyInfo(entityId, partyChangeRequest);

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

    @Override
    public String getEntityType() {
        return ENTITY_TYPE_PERSON;
    }

    private void handleChangePersonResult(PartyChangeRequestType partyChangeRequest,
                                          PartyChangeResultType partyChangeResult,
                                          PersonType changedPerson) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleChangePersonResult",
                    new Object[]{partyChangeRequest, partyChangeResult, changedPerson});
        }

        String entityId = changedPerson.getPersonNumberId();
        PartyInquiryResultType updatedPartyInfo = getPartyInfo(entityId, partyChangeRequest);

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
        }

        l.exiting(getClass().getName(), "handleChangePersonResult");
    }

}
