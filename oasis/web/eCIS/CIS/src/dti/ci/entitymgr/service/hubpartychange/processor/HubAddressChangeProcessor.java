package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.AddressType;
import com.delphi_tech.ows.party.BasicAddressType;
import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.cs.partynotificationmgr.mgr.HubPartyManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.ows.validation.Validator;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/24/2016
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
public class HubAddressChangeProcessor extends BaseHubPartyChangeElementProcessor<BasicAddressType> {

    @Override
    public void processFromCisResult(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<BasicAddressType> cisResultElements,
                                     String entityId, List<BasicAddressType> changedElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFromCisResult",
                    new Object[]{partyChangeRequest, cisResultElements, entityId, changedElements});
        }

        RecordSet recordSet = new RecordSet();
        for (BasicAddressType cisResultBasicAddress : cisResultElements) {
            boolean foundChangedElement = false;
            AddressType cisResultAddress = null;
            for (BasicAddressType changedBasicAddress : changedElements) {
                if (changedBasicAddress.getAddressReference().equals(cisResultBasicAddress.getAddressReference())) {
                    foundChangedElement = true;
                    break;
                }
            }

            if (foundChangedElement) {
                for (AddressType address : partyChangeResult.getAddress()) {
                    if (address.getKey().equals(cisResultBasicAddress.getAddressReference())) {
                        cisResultAddress = address;
                        break;
                    }
                }
                Record inputRecord = getBasicAddrAndAddrRecord(cisResultBasicAddress, cisResultAddress, entityId);
                setCommonFieldsToRecord(inputRecord, partyChangeRequest, CISB_Y);
                recordSet.addRecord(inputRecord);
            }

        }
        if (recordSet.getSize() > 0) {
            getHubPartyManager().saveHubPartyInBatch(recordSet);
        }
        l.exiting(getClass().getName(), "processFromCisResult");
    }

    private Record getBasicAddrAndAddrRecord(BasicAddressType basicAddress, AddressType address, String entityId) {
        Record record = new Record();
        record.setFieldValue("sourceRecordId", entityId);
        record.setFieldValue("sourceTableName", "ENTITY");
        record.setFieldValue("addressId", address.getAddressNumberId());
        mapObjectToRecord(getAddressFieldElementMaps(), address, record);
        mapObjectToRecord(getBasicAddressFieldElementMaps(), basicAddress, record);

        String usaAddressB = "Y";
        if (address.getCountryCode() != null && address.getCountryCode().getValue() != null) {
            if (!"USA".equals(address.getCountryCode().getValue())) {
                usaAddressB = "N";
                mapObjectToRecord(getNonUsaAddressFieldElementMaps(), address, record);
            } else {
                mapObjectToRecord(getUsaAddressFieldElementMaps(), address, record);
            }
        }

        record.setFieldValue("usaAddressB", usaAddressB);
        return record;
    }

    @Override
    public void processForHub(PartyChangeRequestType partyChangeRequest, String entityType, String entityId,
                              List<BasicAddressType> changedElements, List<BasicAddressType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processForHub",
                    new Object[]{partyChangeRequest, entityType, entityId,
                            changedElements, originalElements});
        }

        // Get the address info from db.
        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId, partyChangeRequest);
        List<BasicAddressType> basicAddressListInDb = getBasicAddressList(partyInfoInDb, entityType, entityId);
        List<AddressType> addressListInDb = null;
        if (partyInfoInDb != null) {
            addressListInDb = partyInfoInDb.getAddress();
        }

        // Get changed address info.
        for (BasicAddressType changedBasicAddress : changedElements) {
            AddressInfoRecords addressInfoRecords = getAddressInfoRecords(partyChangeRequest, entityKey, entityType, entityId, originalElements, basicAddressListInDb, addressListInDb, changedBasicAddress);

            if (isAddressChanged(addressInfoRecords)) {
                setCommonFieldsToRecord(addressInfoRecords.getChangedAddressRecord(), partyChangeRequest, CISB_N);
                Record result = getHubPartyManager().saveHubParty(addressInfoRecords.getChangedAddressRecord());

                if (!StringUtils.isBlank(result.getStringValue("newAddressId", ""))) {
                    addressInfoRecords.getChangedAddress().setAddressNumberId(result.getStringValue("newAddressId", ""));
                }
            }
        }

        l.exiting(getClass().getName(), "processForHub");
    }

    private AddressInfoRecords getAddressInfoRecords(PartyChangeRequestType partyChangeRequest,
                                                     String entityKey,
                                                     String entityType,
                                                     String entityId,
                                                     List<BasicAddressType> originalBasicAddressList,
                                                     List<BasicAddressType> basicAddressListInDb,
                                                     List<AddressType> addressListInDb,
                                                     BasicAddressType changedBasicAddress) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddressInfoRecords",
                    new Object[]{partyChangeRequest, entityKey, originalBasicAddressList,
                            basicAddressListInDb, addressListInDb, changedBasicAddress});
        }

        AddressInfoRecords addressInfoRecords = new AddressInfoRecords();
        addressInfoRecords.setChangedBasicAddress(changedBasicAddress);

        Validator.validateFieldRequired(changedBasicAddress.getKey(),
                "ci.partyChangeService.field.required.error",
                "Basic Address Key");

        Validator.validateFieldRequired(changedBasicAddress.getAddressReference(),
                "ci.partyChangeService.field.required.error",
                "Address Reference of Basic Address");

        // Get the AddressType for the BasicAddressType
        AddressType changedAddress = getAddressByKey(partyChangeRequest.getAddress(), changedBasicAddress.getAddressReference());
        if (changedAddress == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"Cannot find Referenced Address with the address key(" + changedBasicAddress.getAddressReference() + ") in Party Change Request."});
            throw new AppException("Cannot find the referenced address.");
        }

        addressInfoRecords.setChangedAddress(changedAddress);

        // Get original address
        BasicAddressType originalBasicAddress = getBasicAddressByKey(originalBasicAddressList, changedBasicAddress.getKey());
        AddressType originalAddress = getOriginalAddress(partyChangeRequest, changedBasicAddress.getAddressReference());

        if (originalBasicAddress != null || originalAddress != null) {
            Validator.validateFieldRequired(changedAddress.getAddressNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "The Address Number Id of an existing address");
        }

        if (!StringUtils.isBlank(changedAddress.getAddressNumberId())) {
            if (originalBasicAddress == null) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"{Party Key: " + entityKey + ", Basic Address Key: " + changedBasicAddress.getKey() + "}" +
                                " The Address Number Id of the referenced address is not null, but the original Basic Address is not found in Previous Data Value Description."});
                throw new AppException("Cannot find Basic Address in previous data value description.");
            }

            if (originalAddress == null) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"{Address Key: " + changedAddress.getKey() + "} " +
                                "The Address Number Id is not null, but the original Address is not found in Previous Data Value Description."});
                throw new AppException("Cannot find Address in previous data value description with address number ID.");
            }
        }

        // Get basic address and address in db.
        AddressType addressInDb = null;
        BasicAddressType basicAddressInDb = null;

        if (!StringUtils.isBlank(changedAddress.getAddressNumberId())) {
            addressInDb = getAddressById(addressListInDb, changedAddress.getAddressNumberId());
            if (addressInDb != null) {
                basicAddressInDb = getBasicAddressByKey(basicAddressListInDb, addressInDb.getKey());
            }

            if (addressInDb == null || basicAddressInDb == null) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"{Entity Key: " + entityKey + ", Address Key: " + changedAddress.getAddressNumberId() + "} " +
                                "Cannot find the address in DB."});
                throw new AppException("Cannot find the address in DB.");
            }
        }

        Record changedAddressRecord = new Record();

        changedAddressRecord.setFieldValue("key", changedAddress.getKey());
        changedAddressRecord.setFieldValue("pk", changedAddress.getAddressNumberId());
        changedAddressRecord.setFieldValue("sourceTableName", "ENTITY");
        changedAddressRecord.setFieldValue("sourceRecordId", entityId);

        mapObjectToRecord(getBasicAddressFieldElementMaps(), changedBasicAddress, changedAddressRecord);
        mapObjectToRecord(getAddressFieldElementMaps(), changedAddress, changedAddressRecord);

        Record originalAddressRecord = null;
        if (originalBasicAddress != null && originalAddress != null) {
            originalAddressRecord = new Record();

            originalAddressRecord.setFieldValue("pk", originalAddress.getAddressNumberId());
            originalAddressRecord.setFieldValue("sourceTableName", "ENTITY");
            originalAddressRecord.setFieldValue("sourceRecordId", entityId);

            mapObjectToRecord(getBasicAddressFieldElementMaps(), originalBasicAddress, originalAddressRecord);
            mapObjectToRecord(getAddressFieldElementMaps(), originalAddress, originalAddressRecord);
        }

        Record dbAddressRecord = null;
        if (basicAddressInDb != null && addressInDb != null) {
            dbAddressRecord = new Record();

            dbAddressRecord.setFieldValue("pk", addressInDb.getAddressNumberId());
            dbAddressRecord.setFieldValue("sourceTableName", "ENTITY");
            dbAddressRecord.setFieldValue("sourceRecordId", entityId);

            mapObjectToRecord(getBasicAddressFieldElementMaps(), basicAddressInDb, dbAddressRecord);
            mapObjectToRecord(getAddressFieldElementMaps(), addressInDb, dbAddressRecord);
        }

        // Process usa/non-usa address fields.
        if (dbAddressRecord != null) {
            String usaAddressB = "Y";
            if (addressInDb.getCountryCode() != null &&
                    addressInDb.getCountryCode().getValue() != null &&
                    !"USA".equals(addressInDb.getCountryCode().getValue())) {
                usaAddressB = "N";
            }
            dbAddressRecord.setFieldValue("usaAddressB", usaAddressB);

            if (usaAddressB.equals("Y")) {
                mapObjectToRecord(getUsaAddressFieldElementMaps(), addressInDb, dbAddressRecord);

                // Need to overwrite county name with county code.
                Record inputRecord = new Record();
                inputRecord.setFieldValue("pk", addressInDb.getAddressNumberId());
//                Record addressDetail = getAddressManager().loadAddressDetailInfo(inputRecord);
//                dbAddressRecord.setFieldValue("countyCode", addressDetail.getStringValue("countyCode", ""));
            } else {
                mapObjectToRecord(getNonUsaAddressFieldElementMaps(), addressInDb, dbAddressRecord);
            }
        }

        if (originalAddressRecord != null) {
            String usaAddressB = "Y";
            if (originalAddress.getCountryCode() != null &&
                    originalAddress.getCountryCode().getValue() != null) {
                if (!"USA".equals(originalAddress.getCountryCode().getValue())) {
                    usaAddressB = "N";
                }
            } else if (dbAddressRecord != null) {
                usaAddressB = dbAddressRecord.getStringValue("usaAddressB", "N");
            }

            originalAddressRecord.setFieldValue("usaAddressB", usaAddressB);

            if (usaAddressB.equals("Y")) {
                mapObjectToRecord(getUsaAddressFieldElementMaps(), originalAddress, dbAddressRecord);
            } else {
                mapObjectToRecord(getNonUsaAddressFieldElementMaps(), originalAddress, dbAddressRecord);
            }
        }

        String usaAddressB = "Y";
        if (changedAddress.getCountryCode() != null &&
                changedAddress.getCountryCode().getValue() != null) {
            if (!"USA".equals(changedAddress.getCountryCode().getValue())) {
                usaAddressB = "N";
            }
        } else if (originalAddressRecord != null) {
            usaAddressB = originalAddressRecord.getStringValue("usaAddressB", "N");
        }

        changedAddressRecord.setFieldValue("usaAddressB", usaAddressB);

        if (usaAddressB.equals("Y")) {
            mapObjectToRecord(getUsaAddressFieldElementMaps(), changedAddress, changedAddressRecord);
        } else {
            mapObjectToRecord(getNonUsaAddressFieldElementMaps(), changedAddress, changedAddressRecord);
        }

        if (originalAddressRecord != null && dbAddressRecord != null) {
            originalAddressRecord.setFields(dbAddressRecord, false);
        }

        if (changedAddressRecord != null && originalAddressRecord != null) {
            changedAddressRecord.setFields(originalAddressRecord, false);
        }

        processUsaNonUsaAddressFields(changedAddressRecord);
        processUsaNonUsaAddressFields(originalAddressRecord);

        addressInfoRecords.setChangedAddressRecord(changedAddressRecord);
        addressInfoRecords.setOriginalAddressRecord(originalAddressRecord);
        addressInfoRecords.setDbAddressRecord(dbAddressRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddressInfoRecords", addressInfoRecords);
        }
        return addressInfoRecords;
    }

    private List<BasicAddressType> getBasicAddressList(
            PartyInquiryResultType partyInfo, String entityType, String entityId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getBasicAddressList", new Object[]{partyInfo, entityType, entityId});
        }

        List<BasicAddressType> basicAddressList = null;

        if (partyInfo != null) {
            if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_PERSON)) {
                for (PersonType person : partyInfo.getPerson()) {
                    if (entityId.equals(person.getPersonNumberId())) {
                        basicAddressList = person.getBasicAddress();
                        break;
                    }
                }
            } else if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_ORGANIZATION)) {
                for (OrganizationType org : partyInfo.getOrganization()) {
                    if (entityId.equals(org.getOrganizationNumberId())) {
                        basicAddressList = org.getBasicAddress();
                        break;
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getBasicAddressList", basicAddressList);
        }
        return basicAddressList;
    }

    private void processUsaNonUsaAddressFields(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processUsaNonUsaAddressFields", new Object[]{record});
        }

        if (record != null) {
            if (record.getStringValue("usaAddressB", "N").equals("Y")) {
                for (FieldElementMap fieldElementMap : getNonUsaAddressFieldElementMaps()) {
                    record.remove(fieldElementMap.getFieldName());
                }
            } else {
                for (FieldElementMap fieldElementMap : getUsaAddressFieldElementMaps()) {
                    record.remove(fieldElementMap.getFieldName());
                }
            }
        }

        l.exiting(getClass().getName(), "processUsaNonUsaAddressFields");
    }

    private boolean isAddressChanged(AddressInfoRecords addressInfoRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddressChanged", new Object[]{addressInfoRecords});
        }

        boolean changed = false;

        if (addressInfoRecords == null) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isAddressChanged", Boolean.FALSE);
            }
            return false;
        }

        if (addressInfoRecords.getOriginalAddressRecord() == null) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isAddressChanged", Boolean.TRUE);
            }
            return true;
        }

        Iterator fieldNames = addressInfoRecords.getChangedAddressRecord().getFieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = (String) fieldNames.next();
            String value = addressInfoRecords.getChangedAddressRecord().getStringValue(fieldName, "");

            if (addressInfoRecords.getOriginalAddressRecord().hasField(fieldName)) {
                if (!value.equals(addressInfoRecords.getOriginalAddressRecord().getStringValue(fieldName, ""))) {
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isAddressChanged", Boolean.TRUE);
                    }
                    return true;
                }
            } else if (addressInfoRecords.getDbAddressRecord().hasField(fieldName)) {
                if (!value.equals(addressInfoRecords.getDbAddressRecord().getStringValue(fieldName, ""))) {
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isAddressChanged", Boolean.TRUE);
                    }
                    return true;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddressChanged", changed);
        }
        return changed;
    }

    private AddressType getAddressByKey(List<AddressType> addressList, String addressKey) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddressByKey", new Object[]{addressList, addressKey});
        }

        AddressType address = null;
        if (!StringUtils.isBlank(addressKey)) {
            for (AddressType tempAddress : addressList) {
                if (addressKey.equals(tempAddress.getKey())) {
                    address = tempAddress;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddressByKey", address);
        }
        return address;
    }

    private AddressType getAddressById(List<AddressType> addressList, String addressId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddressById", new Object[]{addressList, addressId});
        }

        AddressType address = null;

        if (!StringUtils.isBlank(addressId)) {
            for (AddressType tempAddress : addressList) {
                if (addressId.equals(tempAddress.getAddressNumberId())) {
                    address = tempAddress;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddressById", address);
        }
        return address;
    }

    private BasicAddressType getBasicAddressByKey(List<BasicAddressType> basicAddressList, String basicAddressKey) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getBasicAddressByKey", new Object[]{basicAddressList, basicAddressKey});
        }

        BasicAddressType basicAddress = null;

        if (!StringUtils.isBlank(basicAddressKey) && basicAddressList != null) {
            for (BasicAddressType tempBasicAddress : basicAddressList) {
                if (basicAddressKey.equals(tempBasicAddress.getKey())) {
                    basicAddress = tempBasicAddress;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getBasicAddressByKey", basicAddress);
        }
        return basicAddress;
    }

    private AddressType getOriginalAddress(PartyChangeRequestType partyChangeRequest, String addressKey) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalAddress", new Object[]{partyChangeRequest, addressKey});
        }

        AddressType originalAddress = null;

        if (!StringUtils.isBlank(addressKey) &&
                partyChangeRequest.getDataModificationInformation() != null &&
                partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription() != null) {
            for (AddressType tempAddress : partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getAddress()) {
                if (addressKey.equals(tempAddress.getKey())) {
                    originalAddress = tempAddress;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalAddress", originalAddress);
        }
        return originalAddress;
    }

    public void verifyConfig() {
        if (getBasicAddressFieldElementMaps() == null)
            throw new ConfigurationException("The required property 'basicAddressFieldElementMaps' is missing.");
        if (getAddressFieldElementMaps() == null)
            throw new ConfigurationException("The required property 'addressFieldElementMaps' is missing.");
    }

    public List<FieldElementMap> getBasicAddressFieldElementMaps() {
        return m_basicAddressFieldElementMaps;
    }

    public void setBasicAddressFieldElementMaps(List<FieldElementMap> basicAddressFieldElementMaps) {
        m_basicAddressFieldElementMaps = basicAddressFieldElementMaps;
    }

    public List<FieldElementMap> getAddressFieldElementMaps() {
        return m_addressFieldElementMaps;
    }

    public void setAddressFieldElementMaps(List<FieldElementMap> addressFieldElementMaps) {
        m_addressFieldElementMaps = addressFieldElementMaps;
    }

    public List<FieldElementMap> getUsaAddressFieldElementMaps() {
        return m_usaAddressFieldElementMaps;
    }

    public void setUsaAddressFieldElementMaps(List<FieldElementMap> usaAddressFieldElementMaps) {
        m_usaAddressFieldElementMaps = usaAddressFieldElementMaps;
    }

    public List<FieldElementMap> getNonUsaAddressFieldElementMaps() {
        return m_nonUsaAddressFieldElementMaps;
    }

    public void setNonUsaAddressFieldElementMaps(List<FieldElementMap> nonUsaAddressFieldElementMaps) {
        m_nonUsaAddressFieldElementMaps = nonUsaAddressFieldElementMaps;
    }

    public HubPartyManager getHubPartyManager() {
        return m_hubPartyManager;
    }

    public void setHubPartyManager(HubPartyManager hubPartyManager) {
        m_hubPartyManager = hubPartyManager;
    }

    private HubPartyManager m_hubPartyManager;
    private List<FieldElementMap> m_basicAddressFieldElementMaps;
    private List<FieldElementMap> m_addressFieldElementMaps;
    private List<FieldElementMap> m_usaAddressFieldElementMaps;
    private List<FieldElementMap> m_nonUsaAddressFieldElementMaps;

    private static class AddressInfoRecords {
        private BasicAddressType changedBasicAddress;
        private AddressType changedAddress;
        private Record changedAddressRecord;
        private Record originalAddressRecord;
        private Record dbAddressRecord;

        public BasicAddressType getChangedBasicAddress() {
            return changedBasicAddress;
        }

        public void setChangedBasicAddress(BasicAddressType changedBasicAddress) {
            this.changedBasicAddress = changedBasicAddress;
        }

        public AddressType getChangedAddress() {
            return changedAddress;
        }

        public void setChangedAddress(AddressType changedAddress) {
            this.changedAddress = changedAddress;
        }

        public Record getChangedAddressRecord() {
            return changedAddressRecord;
        }

        public void setChangedAddressRecord(Record changedAddressRecord) {
            this.changedAddressRecord = changedAddressRecord;
        }

        public Record getOriginalAddressRecord() {
            return originalAddressRecord;
        }

        public void setOriginalAddressRecord(Record originalAddressRecord) {
            this.originalAddressRecord = originalAddressRecord;
        }

        public Record getDbAddressRecord() {
            return dbAddressRecord;
        }

        public void setDbAddressRecord(Record dbAddressRecord) {
            this.dbAddressRecord = dbAddressRecord;
        }
    }
}
