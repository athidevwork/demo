package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.AddressType;
import com.delphi_tech.ows.party.BasicPhoneNumberType;
import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
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
public class HubPhoneNumberChangeProcessor extends BaseHubPartyChangeElementProcessor<BasicPhoneNumberType> {

    @Override
    public void processFromCisResult(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<BasicPhoneNumberType> cisResultElements,
                                     String entityId, List<BasicPhoneNumberType> changedElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFromCisResult",
                    new Object[]{partyChangeRequest, cisResultElements, entityId, changedElements});
        }

        RecordSet recordSet = new RecordSet();
        for (BasicPhoneNumberType cisResultBasicPhoneNumber : cisResultElements) {
            boolean foundChangedElement = false;
            for (BasicPhoneNumberType changedBasicPhoneNumber : changedElements) {
                if (changedBasicPhoneNumber.getPhoneNumberId().equals(cisResultBasicPhoneNumber.getPhoneNumberId())) {
                    foundChangedElement = true;
                    break;
                }
            }

            if (foundChangedElement) {
                Record inputRecord = getPhoneNumberRecord(partyChangeRequest, entityId, cisResultBasicPhoneNumber);
                setCommonFieldsToRecord(inputRecord, partyChangeRequest, CISB_Y);
                inputRecord.setFieldValue("phoneNumberId", cisResultBasicPhoneNumber.getPhoneNumberId());
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
                              List<BasicPhoneNumberType> changedElements, List<BasicPhoneNumberType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processForHub", new Object[]{partyChangeRequest, entityType, entityId, changedElements, originalElements});
        }

        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId, partyChangeRequest);
        for (BasicPhoneNumberType changedPhoneNumber : changedElements) {
            // Basic Phone Number Key is required.
            Validator.validateFieldRequired(changedPhoneNumber.getKey(),
                    "ci.partyChangeService.field.required.error",
                    "Basic Phone Number Key");

            BasicPhoneNumberType originalPhoneNumber = getOriginalPhoneNumber(originalElements, changedPhoneNumber);
            BasicPhoneNumberType dbPhoneNumber = getPhoneNumberInDb(partyInfoInDb, entityType, entityId, changedPhoneNumber);

            Record changedPhoneNumberRecord = getPhoneNumberRecord(partyChangeRequest, entityId, changedPhoneNumber);
            Record originalPhoneNumberRecord = getPhoneNumberRecord(partyChangeRequest, entityId, originalPhoneNumber);
            Record dbPhoneNumberRecord = getPhoneNumberRecord(partyChangeRequest, entityId, dbPhoneNumber);

            String rowStatus = getRowStatus(changedPhoneNumberRecord, originalPhoneNumberRecord, dbPhoneNumberRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedPhoneNumberRecord, dbPhoneNumberRecord);
                setCommonFieldsToRecord(changedPhoneNumberRecord, partyChangeRequest, CISB_N);
                Record result = getHubPartyManager().saveHubParty(changedPhoneNumberRecord);
                if (rowStatus.equals(ROW_STATUS_NEW)) {
                    changedPhoneNumber.setPhoneNumberId(result.getStringValue("newPhoneNumberId"));
                }
            }
        }

        l.exiting(getClass().getName(), "processForHub");
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
                                        String entityId,
                                        BasicPhoneNumberType phoneNumber) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPhoneNumberRecord",
                    new Object[]{partyChangeRequest, entityId, phoneNumber});
        }

        Record record = null;

        if (phoneNumber != null) {
            record = new Record();

            if (StringUtils.isBlank(phoneNumber.getAddressReference())) {
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
            record.setFieldValue("phoneNumberId", phoneNumber.getPhoneNumberId());
            mapObjectToRecord(getFieldElementMaps(), phoneNumber, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPhoneNumberRecord", record);
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
