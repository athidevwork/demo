package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.AddressType;
import com.delphi_tech.ows.party.BasicPhoneNumberType;
import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.ci.phonemgr.PhoneListManager;
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
 * Date:   9/19/14
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
public class PhoneNumberChangeProcessor extends BasePartyChangeElementProcessor<BasicPhoneNumberType> {
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
                        List<BasicPhoneNumberType> changedElements, List<BasicPhoneNumberType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, partyChangeResult});
        }

        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId);

        for (BasicPhoneNumberType changedPhoneNumber : changedElements) {
            // Basic Phone Number Key is required.
            Validator.validateFieldRequired(changedPhoneNumber.getKey(),
                    "ci.partyChangeService.field.required.error",
                    "Basic Phone Number Key");

            BasicPhoneNumberType originalPhoneNumber = getOriginalPhoneNumber(originalElements, changedPhoneNumber);
            BasicPhoneNumberType dbPhoneNumber = getPhoneNumberInDb(partyInfoInDb, entityType, entityId, changedPhoneNumber);

            Record changedPhoneNumberRecord = getPhoneNumberRecord(partyChangeRequest, entityType, entityId, changedPhoneNumber);
            Record originalPhoneNumberRecord = getPhoneNumberRecord(partyChangeRequest, entityType, entityId, originalPhoneNumber);
            Record dbPhoneNumberRecord = getPhoneNumberRecord(partyChangeRequest, entityType, entityId, dbPhoneNumber);

            String rowStatus = getRowStatus(changedPhoneNumberRecord, originalPhoneNumberRecord, dbPhoneNumberRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedPhoneNumberRecord, dbPhoneNumberRecord);
                validatePhoneNumber(changedPhoneNumberRecord);

                Record changedValues = getChangedValues(changedPhoneNumberRecord, originalPhoneNumberRecord,
                        dbPhoneNumberRecord, new String[]{"phoneNumberId", "sourceTableName", "sourceRecordId"});
                changedValues.setFieldValue(ROW_STATUS, rowStatus);

                savePhoneNumber(changedPhoneNumber, changedValues);
            }
        }

        l.exiting(getClass().getName(), "process");
    }

    private BasicPhoneNumberType getOriginalPhoneNumber(List<BasicPhoneNumberType> originalPhoneNumbers,
                                                        BasicPhoneNumberType changedPhoneNumber) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalPhoneNumber", new Object[]{originalPhoneNumbers, changedPhoneNumber});
        }

        BasicPhoneNumberType phoneNumber = null;

        if (originalPhoneNumbers != null) {
            for (BasicPhoneNumberType tempPhoneNumber : originalPhoneNumbers) {
                if (changedPhoneNumber.getKey().equals(tempPhoneNumber.getKey())) {
                    phoneNumber = tempPhoneNumber;
                    break;
                }
            }
        }

        // The phone number id of an existing phone number cannot be empty.
        if (phoneNumber == null) {
            if (!StringUtils.isBlank(changedPhoneNumber.getPhoneNumberId())) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"Cannot find original phone number info in Previous Value Data Description" +
                                " with phone number ID:" + changedPhoneNumber.getPhoneNumberId() + "."});
                throw new AppException("Cannot find original phone number info in Previous Value Data Description.");
            }
        } else {
            Validator.validateFieldRequired(changedPhoneNumber.getPhoneNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "Phone Number ID of an existing Phone Number");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalPhoneNumber", phoneNumber);
        }
        return phoneNumber;
    }

    private BasicPhoneNumberType getPhoneNumberInDb(PartyInquiryResultType partyInfoInDb,
                                                    String entityType, String entityId, BasicPhoneNumberType changedPhoneNumber) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPhoneNumberInDb", new Object[]{entityType, entityId, changedPhoneNumber});
        }

        BasicPhoneNumberType basicPhoneNumber = null;
        List<BasicPhoneNumberType> phoneNumberList = null;

        if (!StringUtils.isBlank(changedPhoneNumber.getPhoneNumberId()) &&
                partyInfoInDb != null) {
            if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_PERSON)) {
                for (PersonType person : partyInfoInDb.getPerson()) {
                    if (person.getPersonNumberId().equals(entityId)) {
                        phoneNumberList = person.getBasicPhoneNumber();
                        break;
                    }
                }
            } else if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_ORGANIZATION)) {
                for (OrganizationType organization : partyInfoInDb.getOrganization()) {
                    if (organization.getOrganizationNumberId().equals(entityId)) {
                        phoneNumberList = organization.getBasicPhoneNumber();
                        break;
                    }
                }
            }
        }

        if (phoneNumberList != null) {
            for (BasicPhoneNumberType tempPhoneNumber : phoneNumberList) {
                if (tempPhoneNumber.getPhoneNumberId().equals(changedPhoneNumber.getPhoneNumberId())) {
                    basicPhoneNumber = tempPhoneNumber;
                    break;
                }
            }
        }

        if (!StringUtils.isBlank(changedPhoneNumber.getPhoneNumberId()) && basicPhoneNumber == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"Cannot find phone number in DB with phone number ID:" + changedPhoneNumber.getPhoneNumberId() + "."});
            throw new AppException("Cannot find phone number in DB.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPhoneNumberInDb", basicPhoneNumber);
        }
        return basicPhoneNumber;
    }
    
    private Record getPhoneNumberRecord(PartyChangeRequestType partyChangeRequest,
                                        String entityType,
                                        String entityId,
                                        BasicPhoneNumberType phoneNumber) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPhoneNumberRecord",
                    new Object[]{partyChangeRequest, entityType, entityId, phoneNumber});
        }

        Record record = null;

        if (phoneNumber != null) {
            record = new Record();

            if (StringUtils.isBlank(phoneNumber.getPhoneNumberId())) {
                if (StringUtils.isBlank(phoneNumber.getAddressReference())){
                    record.setFieldValue("sourceTableName", "ENTITY");
                    record.setFieldValue("sourceRecordId", entityId);
                } else {
                    record.setFieldValue("sourceTableName", "ADDRESS");
                    String addressId = null;

                    for (AddressType address : partyChangeRequest.getAddress()) {
                        if (phoneNumber.getAddressReference().equals(address.getKey())) {
                            addressId = address.getAddressNumberId();
                            break;
                        }
                    }

                    if (StringUtils.isBlank(addressId)) {
                        MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                                new Object[]{"{Address Key: " + phoneNumber.getAddressReference() + ", Phone Number Key: " + phoneNumber.getKey() + "} " +
                                        "Cannot find the referenced address of the phone number in party change request."});
                        throw new AppException("Phone Number is required.");
                    }

                    record.setFieldValue("sourceRecordId", addressId);
                }
            } else {
                record.setFieldValue("phoneNumberId", phoneNumber.getPhoneNumberId());
            }

            mapObjectToRecord(getFieldElementMaps(), phoneNumber, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPhoneNumberRecord", record);
        }
        return record;
    }

    private void validatePhoneNumber(Record phoneNumberRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePhoneNumber", new Object[]{phoneNumberRecord});
        }

        Validator.validateFieldRequired(phoneNumberRecord.getStringValue("phoneNumberTypeCode", ""),
                "ci.partyChangeService.field.required.error",
                "Phone Number Type Code");

        Validator.validateFieldRequired(phoneNumberRecord.getStringValue("phoneNumber", ""),
                "ci.partyChangeService.field.required.error",
                "Phone Number");

        l.exiting(getClass().getName(), "validatePhoneNumber");
    }

    private void savePhoneNumber(BasicPhoneNumberType changedPhoneNumber,
                                 Record changedPhoneNumberRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePhoneNumber",
                    new Object[]{changedPhoneNumber, changedPhoneNumberRecord});
        }

        Record recUpdateResult = getPhoneListManager().savePhoneNumberWs(changedPhoneNumberRecord);

        if (ROW_STATUS_NEW.equals(changedPhoneNumberRecord.getStringValue(ROW_STATUS, ""))) {
            changedPhoneNumber.setPhoneNumberId(recUpdateResult.getStringValue("newPhoneNumberId"));
        }

        l.exiting(getClass().getName(), "savePhoneNumber");
    }

    public PhoneListManager getPhoneListManager() {
        return m_phoneListManager;
    }

    public void setPhoneListManager(PhoneListManager phoneListManager) {
        this.m_phoneListManager = phoneListManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private PhoneListManager m_phoneListManager;
    private List<FieldElementMap> m_fieldElementMaps;
}
