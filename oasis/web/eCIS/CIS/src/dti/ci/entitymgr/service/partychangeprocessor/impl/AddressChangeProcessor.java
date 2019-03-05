package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.*;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.ci.addressmgr.AddressFields;
import dti.ci.addressmgr.AddressManager;
import dti.ci.addressmgr.addresslistmgr.AddressListManager;
import dti.ci.entitymgr.service.partyadditionalinfomgr.PartyAdditionalInfoManger;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.ows.validation.Validator;
import dti.oasis.recordset.Record;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.xml.DOMUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/24/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/26/2016        dpang      issue 179865 - If system parameter CS_CHK4LOCKED_POL is Y,
 *                                             not allow adding or updating address when policy held by the entity is locked.
 * 10/09/2017        dpang      issue 188813 - Split postalCode to 'zip code' and 'zip plus four' when system parameter CI_SVS_SPLIT_ZIP4 is Y.
 * ---------------------------------------------------
 */
public class AddressChangeProcessor extends BasePartyChangeElementProcessor<BasicAddressType> {
    private final Logger l = LogUtils.getLogger(getClass());
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
                        List<BasicAddressType> changedElements, List<BasicAddressType> originalElements) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process",
                    new Object[]{partyChangeRequest, partyChangeResult, entityType, entityId,
                            changedElements, originalElements});
        }

        // Get the address info from db.
        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        PartyInquiryResultType partyInfoInDb = getPartyInfo(entityId);
        List<BasicAddressType> basicAddressListInDb = getBasicAddressList(partyInfoInDb, entityType, entityId);
        List<AddressType> addressListInDb = null;
        if (partyInfoInDb != null) {
            addressListInDb = partyInfoInDb.getAddress();
        }

        // Get changed address info.
        List<AddressInfo> addressInfoList = new ArrayList<AddressInfo>();

        AddressInfo primaryAddressInfo = null;
        AddressInfo originalPrimaryAddressInfo = null;
        AddressInfo futurePrimaryAddressInfo = null;
        AddressInfo originalFuturePrimaryAddressInfo = null;
        List<AddressInfo> changedNonPrimaryAddressInfoList = new ArrayList<AddressInfo>();

        List<String> processedAddressKeys = new ArrayList<String>();

        for (BasicAddressType changedBasicAddress : changedElements) {
            AddressInfo addressInfo = getAddressInfoRecords(partyChangeRequest, entityKey, entityType, entityId, originalElements, basicAddressListInDb, addressListInDb, changedBasicAddress);
            addressInfoList.add(addressInfo);

            if (isAddressChanged(addressInfo)) {
                boolean primaryAddressB = isPrimaryAddress(addressInfo);

                if (primaryAddressB) {
                    if (primaryAddressInfo == null) {
                        // If we didn't find a primary address previously, set the current address as primary address.
                        primaryAddressInfo = addressInfo;

                    } else {
                        if (addressInfo.getChangedAddressRecord().getStringValue("primaryAddressB", "").equals("Y")) {
                            if (primaryAddressInfo.getChangedAddressRecord().getStringValue("primaryAddressB", "").equals("Y")) {
                                // If the primary address indicator of the current address and the previous found primary address is true,
                                // give error message.
                                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                                        new Object[]{"{Party Key: " + entityKey + "} " +
                                                "An entity cannot have more than one Primary Address."});
                                throw new AppException("An entity cannot have more than one Primary Address.");

                            } else {
                                // If the primary indicator of the previous found primary is not found in root,
                                // and the primary indicator of the current address is found in root,
                                // the current address is the new primary address.
                                originalPrimaryAddressInfo = primaryAddressInfo;
                                primaryAddressInfo = addressInfo;

                            }
                        } else {
                            // If we found a primary address previously, and the primary indicator of the current address is not found in root,
                            // the current address will be changed to be a non-primary address.
                            originalPrimaryAddressInfo = addressInfo;
                        }
                    }
                } else if (isOriginalPrimaryAddress(addressInfo)) {
                    // Check if an address is changed from primary address to non-primary address.
                    originalPrimaryAddressInfo = addressInfo;
                }

                boolean futurePrimaryAddressB = isFuturePrimaryAddress(addressInfo);
                if (futurePrimaryAddressB) {
                    if (futurePrimaryAddressInfo != null) {
                        MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                                new Object[]{"{Party Key: " + entityKey + "} " +
                                        "An entity cannot have more than one Future Primary Address."});
                        throw new AppException("An entity cannot have more than one Future Primary Address.");
                    }
                    futurePrimaryAddressInfo = addressInfo;
                } else if (isOriginalFuturePrimaryAddress(addressInfo)) {
                    futurePrimaryAddressB = true;
                    originalFuturePrimaryAddressInfo = addressInfo;
                }

                if (!primaryAddressB && !futurePrimaryAddressB) {
                    changedNonPrimaryAddressInfoList.add(addressInfo);
                }
            } else {
                boolean futurePrimaryAddressB = isFuturePrimaryAddress(addressInfo);
                if (futurePrimaryAddressB) {
                    if (futurePrimaryAddressInfo != null) {
                        MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                                new Object[]{"{Party Key: " + entityKey + "} " +
                                        "An entity cannot have more than one future Primary Address."});
                        throw new AppException("An entity cannot have more than one Future Primary Address.");
                    }
                    futurePrimaryAddressInfo = addressInfo;
                }
            }
        }

        // Validate primary addresses.
        validatePrimaryAddress(entityType, entityKey,
                primaryAddressInfo, originalPrimaryAddressInfo,
                futurePrimaryAddressInfo, originalFuturePrimaryAddressInfo,
                basicAddressListInDb);

        validateAddress(entityType, entityKey, changedNonPrimaryAddressInfoList);

        processPrimaryAddress(entityType, entityId,
                primaryAddressInfo, originalPrimaryAddressInfo,
                futurePrimaryAddressInfo, originalFuturePrimaryAddressInfo, processedAddressKeys);

        processNonPrimaryAddressList(entityType, entityId, changedNonPrimaryAddressInfoList, processedAddressKeys);

        processAdditionalInfo(addressInfoList, processedAddressKeys);

        validateEntityLockStatus(entityId);

        l.exiting(getClass().getName(), "process");
    }

    private AddressInfo getAddressInfoRecords(PartyChangeRequestType partyChangeRequest,
                                                     String entityKey,
                                                     String entityType,
                                                     String entityId,
                                                     List<BasicAddressType> originalBasicAddressList,
                                                     List<BasicAddressType> basicAddressListInDb,
                                                     List<AddressType> addressListInDb,
                                                     BasicAddressType changedBasicAddress) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddressInfoRecords",
                    new Object[]{partyChangeRequest, entityKey, originalBasicAddressList, 
                            basicAddressListInDb, addressListInDb, changedBasicAddress});
        }

        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setPartyChangeRequest(partyChangeRequest);
        addressInfo.setChangedBasicAddress(changedBasicAddress);

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

        addressInfo.setChangedAddress(changedAddress);

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
                mapObjectToRecordForUsaAddress(getUsaAddressFieldElementMaps(), addressInDb, dbAddressRecord);

                // Need to overwrite county name with county code.
                Record inputRecord = new Record();
                inputRecord.setFieldValue("pk", addressInDb.getAddressNumberId());
                Record addressDetail = getAddressManager().loadAddressDetailInfo(inputRecord);
                dbAddressRecord.setFieldValue("countyCode", addressDetail.getStringValue("countyCode", ""));
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
                mapObjectToRecordForUsaAddress(getUsaAddressFieldElementMaps(), originalAddress, dbAddressRecord);
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
            mapObjectToRecordForUsaAddress(getUsaAddressFieldElementMaps(), changedAddress, changedAddressRecord);
        } else {
            mapObjectToRecord(getNonUsaAddressFieldElementMaps(), changedAddress, changedAddressRecord);
        }

        if (originalAddressRecord != null && dbAddressRecord != null) {
            originalAddressRecord.setFields(dbAddressRecord ,false);
        }

        if (changedAddressRecord != null && originalAddressRecord != null) {
            changedAddressRecord.setFields(originalAddressRecord, false);
        }

        processUsaNonUsaAddressFields(changedAddressRecord);
        processUsaNonUsaAddressFields(originalAddressRecord);

        addressInfo.setChangedAddressRecord(changedAddressRecord);
        addressInfo.setOriginalAddressRecord(originalAddressRecord);
        addressInfo.setDbAddressRecord(dbAddressRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddressInfoRecords", addressInfo);
        }
        return addressInfo;
    }

    private void processUsaNonUsaAddressFields(Record record) {
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

    private boolean isAddressChanged(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddressChanged", new Object[]{addressInfo});
        }

        boolean changed = false;

        if (addressInfo == null) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isAddressChanged", Boolean.FALSE);
            }
            return false;
        }

        if (addressInfo.getOriginalAddressRecord() == null) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isAddressChanged", Boolean.TRUE);
            }
            return true;
        }

        Iterator fieldNames = addressInfo.getChangedAddressRecord().getFieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = (String) fieldNames.next();
            String value = addressInfo.getChangedAddressRecord().getStringValue(fieldName, "");
            
            if (addressInfo.getOriginalAddressRecord().hasField(fieldName)) {
                if (!value.equals(addressInfo.getOriginalAddressRecord().getStringValue(fieldName, ""))) {
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isAddressChanged", Boolean.TRUE);
                    }
                    return true;
                }
            } else if (addressInfo.getDbAddressRecord().hasField(fieldName)) {
                if (!value.equals(addressInfo.getDbAddressRecord().getStringValue(fieldName, ""))) {
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
    
    private boolean isPrimaryAddress(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPrimaryAddress", new Object[]{addressInfo});
        }
        
        boolean primaryB = false;
        
        if (addressInfo.getChangedAddressRecord().hasField("primaryAddressB")) {
            if (YesNoFlag.getInstance(addressInfo.getChangedAddressRecord().getStringValue("primaryAddressB")).booleanValue()) {
                primaryB = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPrimaryAddress", primaryB);
        }
        return primaryB;
    }

    private boolean isOriginalPrimaryAddress(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOriginalPrimaryAddress", new Object[]{addressInfo});
        }

        boolean isOriginalPrimaryAddress = false;
        
        if (addressInfo.getChangedAddressRecord().hasField("primaryAddressB") &&
                addressInfo.getChangedAddressRecord().getStringValue("primaryAddressB", "N").equals("N")) {

            if (addressInfo.getOriginalAddressRecord() != null &&
                    addressInfo.getOriginalAddressRecord().hasField("primaryAddressB")) {

                if (addressInfo.getOriginalAddressRecord().getStringValue("primaryAddressB", "N").equals("Y")) {
                    isOriginalPrimaryAddress = true;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOriginalPrimaryAddress", isOriginalPrimaryAddress);
        }
        return isOriginalPrimaryAddress;
    }
    
    private boolean isFuturePrimaryAddress(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isFuturePrimaryAddress", new Object[]{addressInfo});
        }

        boolean futurePrimaryB = false;

        if (addressInfo.getChangedAddressRecord().hasField("futurePrimaryAddressB")) {
            if (YesNoFlag.getInstance(addressInfo.getChangedAddressRecord().getStringValue("futurePrimaryAddressB")).booleanValue()) {
                futurePrimaryB = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isFuturePrimaryAddress", futurePrimaryB);
        }
        return futurePrimaryB;
    }

    private boolean isOriginalFuturePrimaryAddress(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOriginalFuturePrimaryAddress", new Object[]{addressInfo});
        }

        boolean isOriginalFuturePrimaryAddress = false;

        if (addressInfo.getChangedAddressRecord().hasField("futurePrimaryAddressB") &&
                addressInfo.getChangedAddressRecord().getStringValue("futurePrimaryAddressB", "N").equals("N")) {

            if (addressInfo.getOriginalAddressRecord() != null &&
                    addressInfo.getOriginalAddressRecord().hasField("futurePrimaryAddressB")) {

                if (addressInfo.getOriginalAddressRecord().getStringValue("futurePrimaryAddressB", "N").equals("Y")) {
                    isOriginalFuturePrimaryAddress = true;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOriginalFuturePrimaryAddress", isOriginalFuturePrimaryAddress);
        }
        return isOriginalFuturePrimaryAddress;
    }

    private void validatePrimaryAddress(String entityType, String entityKey,
                                        AddressInfo primaryAddressInfo,
                                        AddressInfo originalPrimaryAddressInfo,
                                        AddressInfo futurePrimaryAddressInfo,
                                        AddressInfo originalFuturePrimaryAddressInfo,
                                        List<BasicAddressType> basicAddressListInDb) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePrimaryAddress",
                    new Object[]{primaryAddressInfo, futurePrimaryAddressInfo});
        }

        // Check if the entity has primary address.
        if (originalPrimaryAddressInfo != null && primaryAddressInfo == null) {

            // Check if the original primary address is changed to be a future address.
            if (!YesNoFlag.getInstance(originalPrimaryAddressInfo.getChangedAddressRecord().getStringValue("futurePrimaryAddressB", "N")).booleanValue()) {

                // If it's not a future primary address, give error message.
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"{Party Key: " + entityKey + "} " +
                                "A Primary Address cannot be changed to be a non-primary address directly. " +
                                "You must add a new primary address or change another address to be primary address."});
                throw new AppException("Cannot change a primary address to non-primary address directly.");
            }
        }

        // An address cannot be both primary and future primary address.
        if (primaryAddressInfo != null && futurePrimaryAddressInfo != null) {
            if (primaryAddressInfo.getChangedAddress().getKey().equals(
                    futurePrimaryAddressInfo.getChangedAddress().getKey())) {

                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"{Party Key: " + entityKey + ", Address Key: " + primaryAddressInfo.getChangedAddressRecord().getStringValue("key") + "} " +
                                "An address cannot be both Primary Address and Future Primary Address."});
                throw new AppException("An address cannot be both Primary Address and Future Primary Address.");
            }
        }

        String today = DateUtils.formatDate(new Date());
        String closeDate = "01/01/3000";

        String futurePrimaryAddressStartDate = null;
        String futurePrimaryAddressEndDate = null;
        if (futurePrimaryAddressInfo != null) {
            // Check if the previous future primary is not changed to be a non-primary address.
            if (originalFuturePrimaryAddressInfo == null) {
                for (BasicAddressType basicAddressInDb : basicAddressListInDb) {
                    if (basicAddressInDb.getFuturePrimaryIndicator() != null &&
                            YesNoFlag.getInstance(basicAddressInDb.getFuturePrimaryIndicator().getValue()).booleanValue()) {
                        if (!basicAddressInDb.getAddressReference().equals(
                                futurePrimaryAddressInfo.getChangedAddress().getAddressNumberId())) {
                            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                                    new Object[]{"{Party Key: " + entityKey + "} " +
                                            "There is already a Future Primary address in DB. " +
                                            "You must change the original future primary address to be a non-future primary address first."});
                            throw new AppException("There is already a Future Primary address in DB.");
                        }
                    }
                }
            }

            if (futurePrimaryAddressInfo.getChangedAddressRecord().hasField("effectiveFromDate")) {
                futurePrimaryAddressStartDate = futurePrimaryAddressInfo.getChangedAddressRecord().getStringValue("effectiveFromDate", "");
            }

            Validator.validateFieldRequired(futurePrimaryAddressStartDate,
                    "ci.partyChangeService.field.required.error",
                    "The Start Date of a future primary address");

            Validator.validateDate2AfterDate1(today, futurePrimaryAddressStartDate,
                    "ci.partyChangeService.futurePrimaryAddress.startDateCannotBeforeToday.error", null);

            if (futurePrimaryAddressInfo.getChangedAddressRecord().hasField("effectiveToDate")) {
                futurePrimaryAddressEndDate = futurePrimaryAddressInfo.getChangedAddressRecord().getStringValue("effectiveToDate", "");
            }

            Validator.validateStringInArray(new String[]{null, "", closeDate}, futurePrimaryAddressEndDate,
                    "ci.partyChangeService.futurePrimaryAddress.invalidEndDate.error", null);
        }

        String primaryAddressStartDate = null;
        String primaryAddressEndDate = null;
        if (primaryAddressInfo != null) {
            if (primaryAddressInfo.getChangedAddressRecord().hasField("effectiveFromDate")) {
                primaryAddressStartDate = primaryAddressInfo.getChangedAddressRecord().getStringValue("effectiveFromDate", "");
            }

            if (!StringUtils.isBlank(primaryAddressStartDate)) {
                Validator.validateDate2EqualOrAfterDate1(primaryAddressStartDate, today,
                        "ci.partyChangeService.primaryAddress.invalidStartDate.error", null);
            }


            if (futurePrimaryAddressInfo == null) {
                if (primaryAddressInfo.getChangedAddressRecord().hasField("effectiveToDate")) {
                    primaryAddressEndDate = primaryAddressInfo.getChangedAddressRecord().getStringValue("effectiveToDate", "");
                }

                Validator.validateStringInArray(new String[]{null, "", closeDate}, primaryAddressEndDate,
                        "ci.partyChangeService.primaryAddress.invalidEndDate.error", null);
            }
        }

        if (!StringUtils.isBlank(futurePrimaryAddressStartDate) && !StringUtils.isBlank(primaryAddressEndDate)) {
            Validator.validateStringEqual(futurePrimaryAddressStartDate, primaryAddressEndDate,
                    "ci.partyChangeService.futurePrimaryAddress.startDateNotEqualPrimaryAddressEndDate.error", null);
        }

        if (primaryAddressInfo != null) {
            validateAddress(entityType, entityKey, primaryAddressInfo);
        }
        
        if (futurePrimaryAddressInfo != null) {
            validateAddress(entityType, entityKey, futurePrimaryAddressInfo);
        }

        if (originalPrimaryAddressInfo != null) {
            validateAddress(entityType, entityKey, originalPrimaryAddressInfo);
        }

        if (originalFuturePrimaryAddressInfo != null) {
            validateAddress(entityType, entityKey, originalFuturePrimaryAddressInfo);
        }

        l.exiting(getClass().getName(), "validatePrimaryAddress");
    }

    private void validateAddress(String entityType, String entityKey, List<AddressInfo> addressInfoList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAddress", new Object[]{entityType, entityKey, addressInfoList});
        }

        if (addressInfoList != null) {
            for (AddressInfo addressInfo : addressInfoList) {
                validateAddress(entityType, entityKey, addressInfo);
            }
        }

        l.exiting(getClass().getName(), "validateAddress");
    }

    private void validateAddress(String entityType, String entityKey, AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAddress", new Object[]{entityType, entityKey, addressInfo});
        }


        // Address Type Code
        String addressTypeCode = addressInfo.getChangedAddressRecord().getStringValue("addressTypeCode", "");

        Validator.validateFieldRequired(addressTypeCode,
                "ci.partyChangeService.field.required.error", "Address Type Code");

        // Address Period
        String today = DateUtils.formatDate(new Date());
        String closeDate = "01/01/3000";

        // Check if original address is expired.
        if (addressInfo.getOriginalAddressRecord() != null) {
            String originalAddressEndDate = addressInfo.getOriginalAddressRecord().getStringValue("effectiveToDate", "");

            if (!StringUtils.isBlank(originalAddressEndDate)) {
                Validator.validateDate2AfterDate1(today, originalAddressEndDate,
                        "ci.partyChangeService.address.expiredAddressCannotBeChanged.error", null);
            }
        }

        String addressStartDate = addressInfo.getChangedAddressRecord().getStringValue("effectiveFromDate", "");
        String addressEndDate = addressInfo.getChangedAddressRecord().getStringValue("effectiveToDate", "");
        
        if (StringUtils.isBlank(addressStartDate)) {
            addressStartDate = today;
        }

        if (StringUtils.isBlank(addressEndDate)) {
            addressEndDate = closeDate;
        }

        if (addressInfo.getOriginalAddressRecord() != null) {
            String originalStartDate = addressInfo.getOriginalAddressRecord().getStringValue("effectiveFromDate", "");
            if (!StringUtils.isBlank(originalStartDate)) {
                Validator.validateDate2EqualOrAfterDate1(originalStartDate, addressStartDate,
                        "ci.partyChangeService.address.newStartDateMustBeAfterOriginalStartDate.error", null);
            }
        }

        Validator.validateDate2EqualOrAfterDate1(addressStartDate, addressEndDate,
                "ci.partyChangeService.address.startDateCannotBeAfterEndDate.error", null);

        // Address Line 1
        String lineOne = addressInfo.getChangedAddressRecord().getStringValue("addressLine1", "");

        Validator.validateFieldRequired(lineOne,
                "ci.partyChangeService.field.required.error", "Address Line One");

        // City
        String city = addressInfo.getChangedAddressRecord().getStringValue("city", "");

        Validator.validateFieldRequired(city,
                "ci.partyChangeService.field.required.error", "Address City Name");

        // State Or Province Code
        String stateOrProvinceCode = null;
        if (addressInfo.getChangedAddressRecord().getStringValue("usaAddressB", "N").equals("Y")) {
            stateOrProvinceCode = addressInfo.getChangedAddressRecord().getStringValue("stateCode", "");
        } else {
            stateOrProvinceCode = addressInfo.getChangedAddressRecord().getStringValue("province", "");
        }

        Validator.validateFieldRequired(stateOrProvinceCode,
                "ci.partyChangeService.field.required.error", "Address State Or Province Code");

        l.exiting(getClass().getName(), "validateAddress");
    }

    private void validateEntityLockStatus(String entityId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateEntityLockStatus", new Object[]{entityId});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("pk", entityId);
        Record lockFlagRecord = getAddressListManager().getEntityLockFlag(inputRecord);
        if ("Y".equalsIgnoreCase(AddressFields.getEntityLockFlag(lockFlagRecord))) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"{Entity key: " + entityId + "} " +
                            "Address cannot be added/modified while a policy held by the client is locked."});
            throw new AppException("Address cannot be added/modified while a policy held by the client is locked.");
        }

        l.exiting(getClass().getName(), "validateEntityLockStatus");
    }

    private List<BasicAddressType> getBasicAddressList(
            PartyInquiryResultType partyInfo, String entityType, String entityId) {
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

    private AddressType getAddressByKey(List<AddressType> addressList, String addressKey) {
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

    private boolean isAddressPeriodChanged(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddressPeriodChanged", new Object[]{addressInfo});
        }

        boolean changed = isAddressPeriodChanged(addressInfo.getChangedAddressRecord(), addressInfo.getOriginalAddressRecord());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddressPeriodChanged", changed);
        }
        return changed;
    }

    private boolean isAddressPeriodChanged(Record changedAddressRecord, Record originalAddressRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddressPeriodChanged",
                    new Object[]{changedAddressRecord, originalAddressRecord});
        }

        if (isAddressStartDateChanged(changedAddressRecord, originalAddressRecord)) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isAddressPeriodChanged", Boolean.TRUE);
            }
            return true;
        }

        if (isAddressEndDateChanged(changedAddressRecord, originalAddressRecord)) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isAddressPeriodChanged", Boolean.TRUE);
            }
            return true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddressPeriodChanged", Boolean.FALSE);
        }
        return false;
    }

    private boolean isAddressStartDateChanged(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddressStartDateChanged", new Object[]{addressInfo});
        }

        boolean changed = isAddressStartDateChanged(addressInfo.getChangedAddressRecord(), addressInfo.getOriginalAddressRecord());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddressStartDateChanged", changed);
        }
        return changed;
    }

    private boolean isAddressStartDateChanged(Record changedAddressRecord, Record originalAddressRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddressStartDateChanged",
                    new Object[]{changedAddressRecord, originalAddressRecord});
        }

        if (!changedAddressRecord.getStringValue("effectiveFromDate", "").equals(
                originalAddressRecord.getStringValue("effectiveFromDate", ""))) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isAddressStartDateChanged", Boolean.TRUE);
            }
            return true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddressStartDateChanged", Boolean.FALSE);
        }
        return false;
    }

    private boolean isAddressEndDateChanged(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddressEndDateChanged", new Object[]{addressInfo});
        }

        boolean changed = isAddressEndDateChanged(addressInfo.getChangedAddressRecord(), addressInfo.getOriginalAddressRecord());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddressEndDateChanged", changed);
        }
        return changed;
    }

    private boolean isAddressEndDateChanged(Record changedAddressRecord, Record originalAddressRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddressEndDateChanged",
                    new Object[]{changedAddressRecord, originalAddressRecord});
        }

        if (!changedAddressRecord.getStringValue("effectiveToDate", "").equals(
                originalAddressRecord.getStringValue("effectiveToDate", ""))) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isAddressEndDateChanged", Boolean.TRUE);
            }
            return true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddressEndDateChanged", Boolean.FALSE);
        }
        return false;
    }

    private boolean isAddressDetailChanged(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddressDetailChanged", new Object[]{addressInfo});
        }

        boolean changed = isAddressDetailChanged(addressInfo.getChangedAddressRecord(), addressInfo.getOriginalAddressRecord());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddressDetailChanged", changed);
        }
        return changed;
    }


    private boolean isAddressDetailChanged(Record changedAddressRecord, Record originalAddressRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddressDetailChanged",
                    new Object[]{changedAddressRecord, originalAddressRecord});
        }

        if (originalAddressRecord == null) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isAddressChanged", Boolean.TRUE);
            }
            return true;
        }

        Iterator fieldNames = changedAddressRecord.getFieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = (String) fieldNames.next();
            
            if (!fieldName.equals("primaryAddressB") &&
                    !fieldName.equals("futurePrimaryAddressB") &&
                    !fieldName.equals("effectiveFromDate") &&
                    !fieldName.equals("effectiveToDate") &&
                    !fieldName.equals("pk") &&
                    !fieldName.equals("key")) {
                if (!changedAddressRecord.getStringValue(fieldName, "").equals(
                        originalAddressRecord.getStringValue(fieldName, ""))) {
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isAddressChanged", Boolean.TRUE);
                    }
                    return true;
                }
            }
            
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddressDetailChanged", Boolean.FALSE);
        }
        return false;
    }

    private boolean isPrimaryAddress(BasicAddressType basicAddress, BasicAddressType basicAddressInDb) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPrimaryAddress",
                    new Object[]{basicAddress, basicAddressInDb});
        }

        if (basicAddress.getPrimaryIndicator() != null &&
                basicAddress.getPrimaryIndicator().getValue() != null) {
            // If the primary flag in the changed basic address is not null, use the changed primary flag.
            if (YesNoFlag.getInstance(basicAddress.getPrimaryIndicator().getValue()).booleanValue()) {
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isPrimaryAddress", Boolean.TRUE);
                }
                return true;
            }
        } else if (basicAddressInDb != null) {
            // If the primary flag in the changed basic address is null, use the primary flag in DB.
            if (basicAddressInDb.getPrimaryIndicator() != null) {
                if (YesNoFlag.getInstance(basicAddressInDb.getPrimaryIndicator().getValue()).booleanValue()) {
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isPrimaryAddress", Boolean.TRUE);
                    }
                    return true;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPrimaryAddress", Boolean.FALSE);
        }
        return false;
    }

    private boolean isFuturePrimaryAddress(BasicAddressType basicAddress, BasicAddressType basicAddressInDb) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isFuturePrimaryAddress", new Object[]{basicAddress, basicAddressInDb});
        }

        if (basicAddress != null &&
                basicAddress.getFuturePrimaryIndicator() != null &&
                basicAddress.getFuturePrimaryIndicator().getValue() != null) {
            // If the primary flag in the changed basic address is not null, use the changed primary flag.
            if (YesNoFlag.getInstance(basicAddress.getFuturePrimaryIndicator().getValue()).booleanValue()) {
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isPrimaryAddress", Boolean.TRUE);
                }
                return true;
            }
        } else if (basicAddressInDb != null) {
            // If the primary flag in the changed basic address is null, use the primary flag in DB.
            if (basicAddressInDb.getFuturePrimaryIndicator() != null) {
                if (YesNoFlag.getInstance(basicAddressInDb.getFuturePrimaryIndicator().getValue()).booleanValue()) {
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isPrimaryAddress", Boolean.TRUE);
                    }
                    return true;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isFuturePrimaryAddress", Boolean.FALSE);
        }
        return false;
    }

    private void processPrimaryAddress(String entityType,
                                       String entityId,
                                       AddressInfo changedPrimaryAddressInfo,
                                       AddressInfo originalPrimaryAddressIfo,
                                       AddressInfo futurePrimaryAddressInfo,
                                       AddressInfo originalFuturePrimaryAddressInfo,
                                       List<String> processedAddressKeys) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processPrimaryAddress",
                    new Object[]{changedPrimaryAddressInfo, originalPrimaryAddressIfo,
                            futurePrimaryAddressInfo, originalFuturePrimaryAddressInfo, processedAddressKeys});
        }

        // 1. Process primary address.
        if (changedPrimaryAddressInfo != null) {
            changedPrimaryAddressInfo.getChangedAddressRecord().setFieldValue("primaryAddressB", "Y");
            if (changedPrimaryAddressInfo.getOriginalAddressRecord() == null) {
                // If the original address is null, add the new primary address.
                addAddress(changedPrimaryAddressInfo, processedAddressKeys);
            } else {
                if (isAddressDetailChanged(changedPrimaryAddressInfo) ||
                        isAddressPeriodChanged(changedPrimaryAddressInfo)) {
                    // If address detail is changed, expire the previous address and add a new primary address.
                    changedPrimaryAddressInfo.getChangedAddressRecord().setFieldValue("expiredAddressFK", changedPrimaryAddressInfo.getChangedAddress().getAddressNumberId());
                    changedPrimaryAddressInfo.getChangedAddressRecord().setFieldValue("pk", "-1");
                }

                // The effective to date of a primary address is always 01/01/3000.
                changedPrimaryAddressInfo.getChangedAddressRecord().setFieldValue("effectiveToDate", "");
                updateAddress(changedPrimaryAddressInfo, processedAddressKeys);
            }
        }

        // 2. Change the original future primary address to be a non future primary address.
        if (originalFuturePrimaryAddressInfo != null) {
            if (changedPrimaryAddressInfo == null ||
                    isAddressDetailChanged(originalFuturePrimaryAddressInfo) ||
                    isAddressPeriodChanged(originalFuturePrimaryAddressInfo)) {
                originalFuturePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("primaryAddressB", "N");
                // To processing primary address correct, set the following field value.
                originalFuturePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("pk", "-1");
                originalFuturePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("expiredAddressFK", originalFuturePrimaryAddressInfo.getChangedAddress().getAddressNumberId());
                originalFuturePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("isFuturePrimaryAddressB", "Y");

                updateAddress(originalFuturePrimaryAddressInfo, processedAddressKeys);
            }
        }

        // 3. Change future primary address.
        if (futurePrimaryAddressInfo != null && isAddressChanged(futurePrimaryAddressInfo)) {
            if (futurePrimaryAddressInfo.getOriginalAddressRecord() == null) {
                futurePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("primaryAddressB", "Y");
                addAddress(futurePrimaryAddressInfo, processedAddressKeys);
            } else {
//                if (YesNoFlag.getInstance(futurePrimaryAddressInfo.getChangedAddressRecord().getStringValue("primaryAddressB", "N")).booleanValue() ||
//                        isAddressStartDateChanged(futurePrimaryAddressInfo)) {
//                    // If a primary address is changed to a future primary address,
//                    // expire the current primary address with the start date of the future primary address, and add a future primary address.
//                    futurePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("expiredAddressFK", futurePrimaryAddressInfo.getChangedAddress().getAddressNumberId());
//                    futurePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("pk", "-1");
//                }

                // To processing primary address correct, set the following field value.
                futurePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("pk", "-1");
                futurePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("expiredAddressFK", futurePrimaryAddressInfo.getChangedAddress().getAddressNumberId());
                futurePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("isFuturePrimaryAddressB", "Y");

                futurePrimaryAddressInfo.getChangedAddressRecord().setFieldValue("primaryAddressB", "Y");
                updateAddress(futurePrimaryAddressInfo, processedAddressKeys);
            }
        }

        // 4. Process original primary address
        if (originalPrimaryAddressIfo != null) {
            // Check if the address is changed to be a future primary address.
            if (originalPrimaryAddressIfo.getChangedBasicAddress().getFuturePrimaryIndicator() == null ||
                    !YesNoFlag.getInstance(originalPrimaryAddressIfo.getChangedBasicAddress().getFuturePrimaryIndicator().getValue()).booleanValue())
                // Check if the address info is changed.
                if (isAddressDetailChanged(originalPrimaryAddressIfo) ||
                        isAddressPeriodChanged(originalPrimaryAddressIfo)) {
                    // Change the address.
                    originalPrimaryAddressIfo.getChangedAddressRecord().setFieldValue("primaryAddressB", "N");
                    processNonPrimaryAddress(entityType, entityId, originalPrimaryAddressIfo, processedAddressKeys);
                }
        }

        l.exiting(getClass().getName(), "processPrimaryAddress");
    }

    private void processNonPrimaryAddress(String entityType,
                                          String entityId,
                                          AddressInfo addressInfo,
                                          List<String> processedAddressKeys) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processNonPrimaryAddress", new Object[]{entityType, entityId, addressInfo});
        }

        addressInfo.getChangedAddressRecord().setFieldValue("primaryAddressB", "N");
        if (addressInfo.getOriginalAddressRecord() == null) {
            addAddress(addressInfo, processedAddressKeys);
        } else {
            if (!isAddressStartDateChanged(addressInfo) &&
                    isAddressEndDateChanged(addressInfo) &&
                    !isAddressDetailChanged(addressInfo)) {
                try {
                    Record inputRecord = new Record();
                    inputRecord.setFieldValue("addressId", addressInfo.getChangedAddressRecord().getStringValue("pk"));
                    inputRecord.setFieldValue("effectiveToDate", addressInfo.getChangedAddressRecord().getStringValue("effectiveToDate", ""));
                    getAddressManager().expireNonPrimaryAddressForWS(inputRecord);
                } catch (Exception e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to expire a non primary address", e);
                    l.throwing(getClass().getName(), "processNonPrimaryAddress", ae);
                    throw ae;
                }
            } else {
                if (isAddressStartDateChanged(addressInfo)) {
                    // If the address start date of the address is changed, expire and add a new address.
                    // Otherwise, change the address directly.
                    addressInfo.getChangedAddressRecord().setFieldValue("expiredAddressFK", addressInfo.getChangedAddress().getAddressNumberId());
                    addressInfo.getChangedAddressRecord().setFieldValue("pk", "-1");
                }
                updateAddress(addressInfo, processedAddressKeys);
            }
        }

        l.exiting(getClass().getName(), "processNonPrimaryAddress");
    }

    private void processNonPrimaryAddressList(String entityType,
                                              String entityId,
                                              List<AddressInfo> otherAddressInfoList,
                                              List<String> processedAddressKeys) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOtherAddress", new Object[]{entityType, entityId, otherAddressInfoList, processedAddressKeys});
        }

        for (AddressInfo addressInfo : otherAddressInfoList) {
            processNonPrimaryAddress(entityType, entityId, addressInfo, processedAddressKeys);
        }

        l.exiting(getClass().getName(), "processOtherAddress");
    }

    private void processAdditionalInfo(List<AddressInfo> addressInfoRecordList, List<String> processedAddressKeys) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processAdditionalInfo", new Object[]{addressInfoRecordList, processedAddressKeys});
        }

        for (AddressInfo addressInfo : addressInfoRecordList) {
            String addressKey = addressInfo.getAddressKey();

            if (!processedAddressKeys.contains(addressKey)) {
                processAddressAdditionalInfo(addressInfo);

                processAddressAdditionalXmlData(addressInfo);
            }
        }

        l.exiting(getClass().getName(), "processAdditionalInfo");
    }

    private void addAddress(AddressInfo addressInfo, List<String> processedAddressKeys) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addAddress", new Object[]{addressInfo});
        }

        // 1. Add address info.
        Record record = new Record();
        // Get address base info.
        record.setFields(addressInfo.getChangedAddressRecord());

        // Get address additional info.
        Record changedAddressAdditionalInfoRecord = getChangedAddressAdditionalInfoRecord(addressInfo);
        if (changedAddressAdditionalInfoRecord != null) {
            record.setFields(getAddressAdditionalInfoRecord(addressInfo));
        }

        // Save address.
        Record result = getAddressManager().updateAddressDetailInfoForWS(record);

        // Update address number ID.
        String newAddressId = result.getStringValue("newAddressId");
        addressInfo.getChangedAddress().setAddressNumberId(newAddressId);

        processAddressAdditionalXmlData(addressInfo);

        processedAddressKeys.add(addressInfo.getAddressKey());

        l.exiting(getClass().getName(), "addAddress");
    }

    /**
     * Return the new address id.
     * @param addressInfo
     * @return
     */
    private void updateAddress(AddressInfo addressInfo, List<String> processedAddressKeys) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddress", new Object[]{addressInfo});
        }

        // 1. Save address info.
        Record changedValues = getChangedValues(addressInfo);

        // Get address additional info.
        Record addressAdditionalInfoRecord = getAddressAdditionalInfoRecord(addressInfo, false);
        if (addressAdditionalInfoRecord != null) {
            addressAdditionalInfoRecord.remove("addressId");
            changedValues.setFields(addressAdditionalInfoRecord);
        }

        // Save address.
        Record result = getAddressManager().updateAddressDetailInfoForWS(changedValues);

        // Set new address id to addressInfo.
        String newAddressId = result.getStringValue("newAddressId", "");
        addressInfo.setNewAddressId(newAddressId);

        processAddressAdditionalXmlData(addressInfo);

        processedAddressKeys.add(addressInfo.getAddressKey());

        l.exiting(getClass().getName(), "updateAddress");
    }

    private void processAddressAdditionalInfo(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processAddressAdditionalInfo", new Object[]{addressInfo});
        }

        Record addressAdditionalInfoRecord = getAddressAdditionalInfoRecord(addressInfo, true);

        if (addressAdditionalInfoRecord != null) {
            getPartyAdditionalInfoManger().saveAddressAdditionalInfo(addressAdditionalInfoRecord);
        }

        l.exiting(getClass().getName(), "processAddressAdditionalInfo");
    }

    private void processAddressAdditionalXmlData(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processAddressAdditionalXmlData", new Object[]{addressInfo});
        }

        AddressAdditionalXmlDataType addressAdditionalXmlData = addressInfo.getAddressAdditionalXmlData();

        if (addressAdditionalXmlData != null) {
            Record record = new Record();
            record.setFieldValue("sourceRecordId", StringUtils.isBlank(addressInfo.getNewAddressId()) ? addressInfo.getAddressId() : addressInfo.getNewAddressId());
            record.setFieldValue("sourceTableName", "ADDRESS");
            record.setFieldValue("xmlData", (addressAdditionalXmlData.getAny() == null ? "" : DOMUtils.nodeToString((Node) addressAdditionalXmlData.getAny())));

            getPartyAdditionalInfoManger().saveAdditionalXmlData(record);
        }

        l.exiting(getClass().getName(), "processAddressAdditionalXmlData");
    }

    protected Record getChangedValues(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedValues", new Object[]{addressInfo});
        }

        Record record = new Record();

        if (addressInfo.getOriginalAddressRecord() != null &&
                addressInfo.getDbAddressRecord() != null) {

            String[] commonFields = new String[]{"key", "pk", "expiredAddressFK", "sourceTableName", "sourceRecordId",
                    "primaryAddressB", "isFuturePrimaryAddressB", "effectiveFromDate", "effectiveToDate", "usaAddressB", "countryCode"};
            String[] usaNonUsaFields = new String[]{"countyCode", "stateCode", "zipcode", "province", "zipCodeForeign"};


            Iterator fieldNames = addressInfo.getChangedAddressRecord().getFieldNames();
            while (fieldNames.hasNext()) {
                boolean commonField = false;
                String fieldName = (String) fieldNames.next();

                // Process common fields.
                for (String commonFiled : commonFields) {
                    if (commonFiled.equals(fieldName)) {
                        commonField = true;
                        break;
                    }
                }

                if (commonField) {
                    record.setFieldValue(fieldName, addressInfo.getChangedAddressRecord().getStringValue(fieldName, ""));
                    continue;
                }

                boolean usaNonUsaFieldB = false;
                for (String usaNonUsaField : usaNonUsaFields) {
                    if (usaNonUsaField.equals(fieldName)) {
                        usaNonUsaFieldB = true;
                        break;
                    }
                }

                // Process usa and non-usa address fields.
                if (!usaNonUsaFieldB) {
                    if (addressInfo.getChangedAddressRecord().getFieldValue(fieldName, "").equals(
                            addressInfo.getOriginalAddressRecord().getStringValue(fieldName, ""))) {
                        record.setFieldValue(fieldName, addressInfo.getDbAddressRecord().getStringValue(fieldName, ""));
                    } else {
                        record.setFieldValue(fieldName, addressInfo.getChangedAddressRecord().getStringValue(fieldName, ""));
                    }
                }
            }

            // Process usa and non-usa address fields.
            String changedUsaAddressB = addressInfo.getChangedAddressRecord().getStringValue("usaAddressB", "");
            String originalUsaAddressB = addressInfo.getOriginalAddressRecord().getStringValue("usaAddressB", "");
            String dbUsaAddressB = addressInfo.getDbAddressRecord().getStringValue("usaAddressB", "");

            // If an address is not changed between usa and non-usa address, only get the changed values.
            if (changedUsaAddressB.equals(originalUsaAddressB) && changedUsaAddressB.equals(dbUsaAddressB)) {
                for (String fieldName : usaNonUsaFields) {
                    if (addressInfo.getChangedAddressRecord().hasField(fieldName)) {
                        if (addressInfo.getChangedAddressRecord().getStringValue(fieldName, "").equals(
                                addressInfo.getOriginalAddressRecord().getStringValue(fieldName, ""))) {
                            record.setFieldValue(fieldName, addressInfo.getDbAddressRecord().getStringValue(fieldName, ""));
                        } else {
                            record.setFieldValue(fieldName, addressInfo.getChangedAddressRecord().getStringValue(fieldName, ""));
                        }
                    }
                }
            } else {
                // If an address in changed between usa and non-usa address, always get the filed value for changed address.
                for (String fieldName : usaNonUsaFields) {
                    if (addressInfo.getChangedAddressRecord().hasField(fieldName)) {
                        record.setFieldValue(fieldName, addressInfo.getChangedAddressRecord().getStringValue(fieldName, ""));
                    }
                }
            }
        } else {
            record.setFields(addressInfo.getChangedAddressRecord());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedValues", record);
        }

        return record;
    }

    private Record getChangedAddressAdditionalInfoRecord(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedAddressAdditionalInfoRecord", new Object[]{addressInfo});
        }

        Record record = null;

        AddressAdditionalInfoType addressAdditionalInfo = addressInfo.getChangedAddressAdditionalInfo();
        if (addressAdditionalInfo != null) {
            record = new Record();

            mapObjectToRecord(getAddressAdditionalInfoFieldElementMaps(), addressAdditionalInfo, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedAddressAdditionalInfoRecord", record);
        }
        return record;
    }

    private Record getOriginalAddressAdditionalInfoRecord(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalAddressAdditionalInfoRecord", new Object[]{addressInfo});
        }

        Record record = null;

        AddressAdditionalInfoType originalAddressAdditionalInfo = addressInfo.getOriginalAddressAdditionalInfo();

        if (originalAddressAdditionalInfo != null) {
            record = new Record();

            mapObjectToRecord(getAddressAdditionalInfoFieldElementMaps(), originalAddressAdditionalInfo, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalAddressAdditionalInfoRecord", record);
        }
        return record;
    }

    private Record getAddressAdditionalInfoRecordFromDB(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddressAdditionalInfoRecordFromDB", new Object[]{addressInfo});
        }

        Record record = null;
        String addressId = null;

        if (!StringUtils.isBlank(addressInfo.getNewAddressId())) {
            addressId = addressInfo.getNewAddressId();
        } else if (!StringUtils.isBlank(addressInfo.getAddressId())) {
            addressId = addressInfo.getAddressId();
        }

        if (!StringUtils.isBlank(addressId)) {
            record = getAddressAdditionalInfoRecordFromDB(addressId);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddressAdditionalInfoRecordFromDB", record);
        }
        return record;
    }

    private Record getAddressAdditionalInfoRecord(AddressInfo addressInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddressAdditionalInfoRecord", new Object[]{addressInfo});
        }

        Record record = null;

        if (addressInfo.getPartyChangeRequest().getPartyAdditionalInfo() != null) {
            for (AddressAdditionalInfoType addressAdditionalInfo : addressInfo.getPartyChangeRequest().getPartyAdditionalInfo().getAddressAdditionalInfo()) {
                if (addressInfo.getAddressKey().equals(addressAdditionalInfo.getAddressReference())) {
                    record = new Record();

                    mapObjectToRecord(getAddressAdditionalInfoFieldElementMaps(), addressAdditionalInfo, record);
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddressAdditionalInfoRecord", record);
        }
        return record;
    }

    private Record getAddressAdditionalInfoRecordFromDB(String addressId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddressAdditionalInfoRecordFromDB", new Object[]{addressId});
        }

        Record input = new Record();
        input.setFieldValue("addressId", addressId);

        Record record = getPartyAdditionalInfoManger().loadAddressAdditionalInfo(input);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddressAdditionalInfoRecordFromDB", record);
        }
        return record;
    }

    private Record getAddressAdditionalInfoRecord(AddressInfo addressInfo, boolean changedRecordOnly) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddressAdditionalInfoChangedValues", new Object[]{addressInfo});
        }

        Record record = null;

        Record changedAddressAdditionalInfoRecord = getChangedAddressAdditionalInfoRecord(addressInfo);

        if (changedAddressAdditionalInfoRecord == null) {
            if (!changedRecordOnly) {
                // If changed address additional info is empty, and changedRecordOnly is false, get record from db.
                record = getAddressAdditionalInfoRecordFromDB(addressInfo);
            }
        } else {
            Record originalAddressAdditionalInfoRecord = getOriginalAddressAdditionalInfoRecord(addressInfo);
            Record addressAdditionalInfoFromDb = getAddressAdditionalInfoRecordFromDB(addressInfo);

            // Get changed values.
            record = getChangedValues(changedAddressAdditionalInfoRecord, originalAddressAdditionalInfoRecord, addressAdditionalInfoFromDb);

            if (addressAdditionalInfoFromDb != null) {
                if (record.getFieldCount() == 0) {
                    if (changedRecordOnly) {
                        // If there are no changed values, and changedRecordOnly is true, return null.
                        record = null;
                    } else {
                        // If there are no changed values, and changedRecordOnly is false, get record from db.
                        record = addressAdditionalInfoFromDb;
                    }
                } else {
                    // If there are changed values, also get not changed values.
                    record.setFields(addressAdditionalInfoFromDb, false);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddressAdditionalInfoChangedValues", record);
        }
        return record;
    }

    private void mapObjectToRecordForUsaAddress(List<FieldElementMap> fieldMapList, Object obj, Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mapObjectToRecordForUsaAddress", new Object[]{fieldMapList, obj, record});
        }
        mapObjectToRecord(fieldMapList, obj, record, true);

        String zipCode = "zipCode";
        if (record.hasField(zipCode) && YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("CI_SVS_SPLIT_ZIP4", "N")).booleanValue()) {
            String separater = "-";
            String zipCodeWithPlus4 = record.getStringValue(zipCode);

            if (zipCodeWithPlus4.contains(separater)) {
                String[] zipCodeArr = zipCodeWithPlus4.split(separater);

                record.setFieldValue(zipCode, zipCodeArr[0]);
                record.setFieldValue("zipPlusFour", zipCodeArr[1]);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "mapObjectToRecordForUsaAddress", record);
        }
    }

    public void verifyConfig() {
        if (getAddressManager() == null)
            throw new ConfigurationException("The required property 'addressManager' is missing.");
        if (getPartyAdditionalInfoManger() == null)
            throw new ConfigurationException("The required property 'partyAdditionalInfoManger' is missing.");
        if (getBasicAddressFieldElementMaps() == null)
            throw new ConfigurationException("The required property 'basicAddressFieldElementMaps' is missing.");
        if (getAddressFieldElementMaps() == null)
            throw new ConfigurationException("The required property 'addressFieldElementMaps' is missing.");
        if (getAddressAdditionalInfoFieldElementMaps() == null)
            throw new ConfigurationException("The required property 'addressAdditionalInfoFieldElementMaps' is missing.");
    }

    public AddressManager getAddressManager() {
        return m_addressManager;
    }

    public void setAddressManager(AddressManager addressManager) {
        this.m_addressManager = addressManager;
    }

    public AddressListManager getAddressListManager() {
        return m_addressListManager;
    }

    public void setAddressListManager(AddressListManager addressListManager) {
        this.m_addressListManager = addressListManager;
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

    public List<FieldElementMap> getAddressAdditionalInfoFieldElementMaps() {
        return m_addressAdditionalInfoFieldElementMaps;
    }

    public void setAddressAdditionalInfoFieldElementMaps(List<FieldElementMap> addressAdditionalInfoFieldElementMaps) {
        m_addressAdditionalInfoFieldElementMaps = addressAdditionalInfoFieldElementMaps;
    }

    public PartyAdditionalInfoManger getPartyAdditionalInfoManger() {
        return m_partyAdditionalInfoManger;
    }

    public void setPartyAdditionalInfoManger(PartyAdditionalInfoManger partyAdditionalInfoManger) {
        m_partyAdditionalInfoManger = partyAdditionalInfoManger;
    }

    private AddressManager m_addressManager;
    private AddressListManager m_addressListManager;
    private PartyAdditionalInfoManger m_partyAdditionalInfoManger;
    private List<FieldElementMap> m_basicAddressFieldElementMaps;
    private List<FieldElementMap> m_addressFieldElementMaps;
    private List<FieldElementMap> m_usaAddressFieldElementMaps;
    private List<FieldElementMap> m_nonUsaAddressFieldElementMaps;
    private List<FieldElementMap> m_addressAdditionalInfoFieldElementMaps;
}
