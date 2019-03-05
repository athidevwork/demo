package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partychangeservice.PreviousDataValueDescriptionType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.cs.partynotificationmgr.mgr.HubPartyMergeManager;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.validation.Validator;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/9/2016
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
public class HubPartyMergeProcessor extends BaseHubPartyChangeProcessor {

    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, partyChangeResult});
        }
        validatePartyChangeRequest(partyChangeRequest);

        Record prevRecord = new Record();
        //Get entityId to query the consolidated entity later.
        String entityId = setInputRecordForPreviousData(partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription(), prevRecord);

        Record rootRecord = new Record();
        setInputRecordForRootContent(partyChangeRequest, rootRecord);

        validateIfElementsMatch(prevRecord, rootRecord);
        //merge client. If passes validation, set the value of entityFrom field of rootRecord to preRecord
        prevRecord.setFieldValue(ENTITY_FROM, rootRecord.getStringValue(ENTITY_FROM));
        getHubPartyMergeManager().saveHubEntityMntDuplicate(prevRecord);

        PartyInquiryResultType party = getPartyInfo(entityId, partyChangeRequest);
        partyChangeResult.getOrganization().addAll(party.getOrganization());
        partyChangeResult.getPerson().addAll(party.getPerson());
        partyChangeResult.getAddress().addAll(party.getAddress());
        partyChangeResult.getProperty().addAll(party.getProperty());
        l.exiting(getClass().getName(), "process");
    }

    private String setInputRecordForPreviousData(PreviousDataValueDescriptionType previousData, Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputRecordForPreviousData", new Object[]{previousData, record});
        }

        String entityId = null;
        if (!CollectionUtils.isEmpty(previousData.getPerson())) {
            PersonType person = previousData.getPerson().get(0);
            entityId = person.getPersonNumberId();
            record.setFieldValue(ENTITY_TO, entityId);
            setRecordFieldValueForPerson(person, record);
        } else {
            OrganizationType organization = previousData.getOrganization().get(0);
            entityId = organization.getOrganizationNumberId();
            record.setFieldValue(ENTITY_TO, entityId);
            setRecordFieldValueForOrganization(organization, record);
        }

        l.exiting(getClass().getName(), "setInputRecordForPreviousData");
        return entityId;
    }

    private void setInputRecordForRootContent(PartyChangeRequestType partyChangeRequest, Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputRecordForRootContent", new Object[]{partyChangeRequest, record});
        }

        if (!CollectionUtils.isEmpty(partyChangeRequest.getPerson())) {
            PersonType person = partyChangeRequest.getPerson().get(0);
            record.setFieldValue(ENTITY_FROM, person.getPersonNumberId());
            setRecordFieldValueForPerson(person, record);
        } else {
            OrganizationType organization = partyChangeRequest.getOrganization().get(0);
            record.setFieldValue(ENTITY_FROM, organization.getOrganizationNumberId());
            setRecordFieldValueForOrganization(organization, record);
        }
        l.exiting(getClass().getName(), "setInputRecordForRootContent");
    }

    private void setRecordFieldValueForPerson(PersonType person, Record record) {
        record.setFieldValue(ENTITY_TYPE, PERSON_TYPE);

        if (!CollectionUtils.isEmpty(person.getBasicAddress())) {
            record.setFieldValue(MOVE_ADDRESS, Y);
        }
        if (!CollectionUtils.isEmpty(person.getBasicPhoneNumber())) {
            record.setFieldValue(MOVE_PHONE, Y);
        }
        if (!CollectionUtils.isEmpty(person.getContact())) {
            record.setFieldValue(MOVE_CONTACT, Y);
        }
        if (!CollectionUtils.isEmpty(person.getEducationInformation())) {
            record.setFieldValue(MOVE_EDUCATION, Y);
        }
        if (!CollectionUtils.isEmpty(person.getPartyClassification())) {
            record.setFieldValue(MOVE_CLASSIFICATION, Y);
        }
        if (!CollectionUtils.isEmpty(person.getRelationship())) {
            record.setFieldValue(MOVE_RELATIONSHIP, Y);
        }
        if (!CollectionUtils.isEmpty(person.getProfessionalLicense())) {
            record.setFieldValue(MOVE_LICENSE, Y);
        }
        if (!CollectionUtils.isEmpty(person.getCertification())) {
            record.setFieldValue(MOVE_CERTIFICATION, Y);
        }
    }

    private void setRecordFieldValueForOrganization(OrganizationType organization, Record record) {
        record.setFieldValue(ENTITY_TYPE, ORGANIZATION_TYPE);
        if (!CollectionUtils.isEmpty(organization.getBasicAddress())) {
            record.setFieldValue(MOVE_ADDRESS, Y);
        }
        if (!CollectionUtils.isEmpty(organization.getBasicPhoneNumber())) {
            record.setFieldValue(MOVE_PHONE, Y);
        }
        if (!CollectionUtils.isEmpty(organization.getPartyClassification())) {
            record.setFieldValue(MOVE_CLASSIFICATION, Y);
        }
        if (!CollectionUtils.isEmpty(organization.getRelationship())) {
            record.setFieldValue(MOVE_RELATIONSHIP, Y);
        }
        if (!CollectionUtils.isEmpty(organization.getOrganizationLicense())) {
            record.setFieldValue(MOVE_LICENSE, Y);
        }
        if (!CollectionUtils.isEmpty(organization.getCertification())) {
            record.setFieldValue(MOVE_CERTIFICATION, Y);
        }
    }

    private void validatePartyChangeRequest(PartyChangeRequestType partyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePartyChangeRequest", new Object[]{partyChangeRequest});
        }

        PreviousDataValueDescriptionType previousData = partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription();
        MessageManager messageManager = MessageManager.getInstance();
        if (previousData == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{messageManager.formatMessage("ci.partyChangeService.merge.previousEntityRequired.error")});
            throw new ValidationException("Cannot find Original client.");
        }

        List<PersonType> duplicatePersons = partyChangeRequest.getPerson();
        List<OrganizationType> duplicateOrganizations = partyChangeRequest.getOrganization();
        List<PersonType> originalPersons = previousData.getPerson();
        List<OrganizationType> originalOrganizations = previousData.getOrganization();

        validateIfExistOriginalDuplicateClient(originalPersons, originalOrganizations, messageManager.formatMessage("ci.partyChangeService.merge.previousEntityRequired.error"));
        validateIfExistOriginalDuplicateClient(duplicatePersons, duplicateOrganizations, messageManager.formatMessage("ci.partyChangeService.merge.rootEntityRequired.error"));

        validateIfExistOnlyOneOriginalDuplicateClient(originalPersons, originalOrganizations, messageManager.formatMessage("ci.partyChangeService.merge.moreThanOnePreviousEntity.error"));
        validateIfExistOnlyOneOriginalDuplicateClient(duplicatePersons, duplicateOrganizations, messageManager.formatMessage("ci.partyChangeService.merge.moreThanOneRootEntity.error"));


        if (originalPersons.size() == 1) {
            validateIfExistsNumberId(originalPersons.get(0),
                    messageManager.formatMessage("ci.partyChangeService.merge.previousEntityNumberIdRequired.error", new Object[]{"Person"}));
        }

        if (originalOrganizations.size() == 1) {
            validateIfExistsNumberId(originalOrganizations.get(0),
                    messageManager.formatMessage("ci.partyChangeService.merge.previousEntityNumberIdRequired.error", new Object[]{"Organization"}));
        }

        if (duplicatePersons.size() == 1) {
            validateIfExistsNumberId(duplicatePersons.get(0),
                    messageManager.formatMessage("ci.partyChangeService.merge.rootEntityNumberIdRequired.error", new Object[]{"Person"}));
        }

        if (duplicateOrganizations.size() == 1) {
            validateIfExistsNumberId(duplicateOrganizations.get(0),
                    messageManager.formatMessage("ci.partyChangeService.merge.rootEntityNumberIdRequired.error", new Object[]{"Organization"}));
        }

        String originalClientId = CollectionUtils.isEmpty(originalPersons) ? originalOrganizations.get(0).getOrganizationNumberId() : originalPersons.get(0).getPersonNumberId();
        String duplicateClientId = CollectionUtils.isEmpty(duplicatePersons) ? duplicateOrganizations.get(0).getOrganizationNumberId() : duplicatePersons.get(0).getPersonNumberId();

        if (originalClientId.equals(duplicateClientId)) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{messageManager.formatMessage("ci.partyChangeService.merge.sameEntity.error")});
            throw new ValidationException("The Original and Duplicate client are the same.");
        }

        l.exiting(getClass().getName(), "validatePartyChangeRequest");
    }

    /**
     * There are four kinds merge:
     * person to person
     * person to organization
     * organization to person
     * organization to organization
     * <p/>
     * To validate if elements under party change request root content and those under previous data match,
     * we compare the field names of the records to simplify the validation.
     *
     * @param preRecord
     * @param rootRecord
     */
    private void validateIfElementsMatch(Record preRecord, Record rootRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateIfElementsMatch", new Object[]{preRecord, rootRecord});
        }

        List<String> preFieldNameList = preRecord.getFieldNameList();
        List<String> rootFieldNameList = rootRecord.getFieldNameList();

        //Find the uncommon fields of the two records
        StringBuilder errorMsgBuilder = new StringBuilder();
        validateFields(preFieldNameList, rootFieldNameList, rootRecord, errorMsgBuilder);
        validateFields(rootFieldNameList, preFieldNameList, preRecord, errorMsgBuilder);

        if (errorMsgBuilder.length() > 0) {
            errorMsgBuilder.setLength(errorMsgBuilder.length() - 2); // remove the last comma and space
            MessageManager messageManager = MessageManager.getInstance();
            String errorMsg = messageManager.formatMessage("ci.partyChangeService.merge.fieldsMismatch.error", new String[]{errorMsgBuilder.toString()});
            messageManager.addErrorMessage("ci.webservice.error", new Object[]{errorMsg});
            throw new ValidationException("Fields of the Original and Duplicate clients don't match.");
        }
        l.exiting(getClass().getName(), "validateIfElementsMatch");
    }

    private void validateFields(List<String> fieldNameList, List<String> fieldNameList2, Record record, StringBuilder errorMsgBuilder) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateFields", new Object[]{fieldNameList, fieldNameList2, record, errorMsgBuilder});
        }

        List<String> excludedFields = new ArrayList<String>();
        excludedFields.add(ENTITY_FROM);
        excludedFields.add(ENTITY_TO);
        excludedFields.add(ENTITY_TYPE);

        for (String fieldName : fieldNameList) {
            //We need to exclude contact and education if entity type is organization.
            if (ORGANIZATION_TYPE.equals(record.getFieldValue(ENTITY_TYPE))) {
                excludedFields.add(MOVE_CONTACT);
                excludedFields.add(MOVE_EDUCATION);
            }

            if (!fieldNameList2.contains(fieldName) && !excludedFields.contains(fieldName)) {
                errorMsgBuilder.append(fieldName.substring(fieldName.lastIndexOf("move") + 4) + ", ");
            }
        }
        l.exiting(getClass().getName(), "validateFields");
    }

    private void validateIfExistsNumberId(Object obj, String errorMsg) {
        if (obj instanceof PersonType) {
            PersonType person = (PersonType) obj;
            Validator.validateFieldRequired(person.getPersonNumberId(), "ci.partyChangeService.field.required.error", errorMsg);
        } else {
            OrganizationType organization = (OrganizationType) obj;
            Validator.validateFieldRequired(organization.getOrganizationNumberId(), "ci.partyChangeService.field.required.error", errorMsg);
        }
    }

    private void validateIfExistOriginalDuplicateClient(List<PersonType> persons, List<OrganizationType> organizations, String errorMsg) {
        if (CollectionUtils.isEmpty(persons) && CollectionUtils.isEmpty(organizations)) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error", new Object[]{errorMsg});
            throw new ValidationException(errorMsg);
        }
    }

    private void validateIfExistOnlyOneOriginalDuplicateClient(List<PersonType> persons, List<OrganizationType> organizations, String errorMsg) {
        if ((!CollectionUtils.isEmpty(persons) && !CollectionUtils.isEmpty(organizations) ||
                (!CollectionUtils.isEmpty(persons) && persons.size() != 1) ||
                (!CollectionUtils.isEmpty(organizations) && organizations.size() != 1))) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error", new Object[]{errorMsg});
            throw new ValidationException(errorMsg);
        }
    }

    @Override
    public String getEntityType() {
        return null;
    }

    public HubPartyMergeManager getHubPartyMergeManager() {
        return m_hubPartyMergeManager;
    }

    public void setHubPartyMergeManager(HubPartyMergeManager hubPartyMergeManager) {
        m_hubPartyMergeManager = hubPartyMergeManager;
    }

    private HubPartyMergeManager m_hubPartyMergeManager;

    private static final String Y = "Y";
    private static final String ENTITY_FROM = "fromEntityId";
    private static final String ENTITY_TO = "toEntityId";
    private static final String ENTITY_TYPE = "entityType";
    private static final String PERSON_TYPE = "P";
    private static final String ORGANIZATION_TYPE = "O";
    private static final String MOVE_ADDRESS = "moveAddress";
    private static final String MOVE_PHONE = "movePhone";
    private static final String MOVE_CONTACT = "moveContact";
    private static final String MOVE_EDUCATION = "moveEducation";
    private static final String MOVE_CLASSIFICATION = "moveClassification";
    private static final String MOVE_RELATIONSHIP = "moveRelationship";
    private static final String MOVE_LICENSE = "moveLicense";
    private static final String MOVE_CERTIFICATION = "moveCertification";

}
