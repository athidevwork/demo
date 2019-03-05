package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.*;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.*;
import dti.ci.entitymgr.service.PartyInquiryServiceManager;
import dti.ci.propertymgr.PropertyManager;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.ows.validation.Validator;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   10/9/14
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
public class PropertyChangeProcessor {
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, partyChangeResult});
        }

        if (partyChangeRequest != null && partyChangeRequest.getProperty().size() > 0) {
            List<PropertyType> propertyListInDb = getPropertyInDb(partyChangeRequest);

            for (PropertyType changedProperty : partyChangeRequest.getProperty()) {
                String propertyKey = changedProperty.getKey();
                String propertyId = changedProperty.getPropertyNumberId();

                Validator.validateFieldRequired(propertyKey,
                        "ci.partyChangeService.field.required.error",
                        "Property Key");

                PropertyType originalProperty = getOriginalProperty(partyChangeRequest, changedProperty);

                if (StringUtils.isBlank(propertyId) && originalProperty != null) {
                    MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                            new Object[]{"The Property Number ID of an existing Property cannot be empty."});
                    throw new AppException("The Property Number ID of an existing Property be empty.");
                } else if (!StringUtils.isBlank(propertyId) && originalProperty == null) {
                    MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                            new Object[]{"Cannot find original Property Number ID in Previous Value Data Description with Property Number ID:" + propertyId + "."});
                    throw new AppException("Cannot find original Property Number ID in Previous Value Data Description.");
                }

                PropertyType propertyInDb = null;

                if (!StringUtils.isBlank(propertyId)) {
                    propertyInDb = getPropertyById(propertyListInDb, propertyId);

                    if (propertyInDb == null) {
                        MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                                new Object[]{"Cannot find Property in DB with Property Number ID: " + propertyId + "."});
                        throw new AppException("Cannot find Property in DB with Property Number ID.");
                    }
                }

                Record changedPropertyRecord = getPropertyRecord(partyChangeRequest, changedProperty);
                Record originalPropertyRecord = getPropertyRecord(partyChangeRequest, originalProperty);
                Record dbPropertyRecord = getPropertyRecord(partyChangeRequest, propertyInDb);

                String rowStatus = getRowStatus(changedPropertyRecord, originalPropertyRecord, dbPropertyRecord);
                if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                    mergeRecordValues(changedPropertyRecord, originalPropertyRecord, dbPropertyRecord);

                    changedPropertyRecord.setFieldValue(ROW_STATUS, rowStatus);
                    saveProperty(changedProperty, changedPropertyRecord);
                }

            }

            // Get updated property for processing change result
            List<PropertyType> updatedPropertyList = getPropertyInDb(partyChangeRequest);
            for (PropertyType updatedProperty : updatedPropertyList) {
                for (PropertyType changedProperty : partyChangeRequest.getProperty()) {
                    if (updatedProperty.getPropertyNumberId().equals(changedProperty.getPropertyNumberId())) {
                        updatedProperty.setKey(changedProperty.getKey());

                        String addressReference = getChildElementValue(changedProperty, ELEMENT_NAME_ADDRESS_REFERENCE);

                        if (!StringUtils.isBlank(addressReference)) {
                            updatedProperty.getBasicAddress().setAddressReference(addressReference);

                            for (BasicPhoneNumberType basicPhoneNumber : updatedProperty.getBasicPhoneNumber()) {
                                basicPhoneNumber.setAddressReference(addressReference);
                            }
                        }
                        break;
                    }
                }
            }
            partyChangeResult.getProperty().addAll(updatedPropertyList);
        }

        l.exiting(getClass().getName(), "process");
    }


    private PropertyType getOriginalProperty(PartyChangeRequestType partyChangeRequest, PropertyType changedProperty) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalProperty", new Object[]{partyChangeRequest, changedProperty});
        }

        PropertyType originalProperty = null;

        if (partyChangeRequest != null &&
                partyChangeRequest.getDataModificationInformation() != null &&
                partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription() != null &&
                partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getProperty() != null) {
            for (PropertyType tempProperty : partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getProperty()) {
                if (changedProperty.getKey().equals(tempProperty.getKey())) {
                    if (!StringUtils.isBlank(changedProperty.getPropertyNumberId()) &&
                            !changedProperty.getPropertyNumberId().equals(tempProperty.getPropertyNumberId())) {
                        continue;
                    }

                    originalProperty = tempProperty;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalProperty", originalProperty);
        }
        return originalProperty;
    }

    private List<PropertyType> getPropertyInDb(PartyChangeRequestType partyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPropertyInDb", new Object[]{partyChangeRequest});
        }

        List<PropertyType> propertyList = null;
        List<String> entityIdList = new ArrayList<String>();

        if (partyChangeRequest != null) {
            for (PersonType person : partyChangeRequest.getPerson()) {
                if (!StringUtils.isBlank(person.getPersonNumberId())) {
                    entityIdList.add(person.getPersonNumberId());
                }
            }

            for (OrganizationType organization : partyChangeRequest.getOrganization()) {
                if (!StringUtils.isBlank(organization.getOrganizationNumberId())) {
                    entityIdList.add(organization.getOrganizationNumberId());
                }
            }
        }

        if (entityIdList.size() > 0) {
            PartyInquiryRequestType partyInquiryRequest = new PartyInquiryRequestType();

            for (String entityId : entityIdList) {
                PartyInquiryRequestParametersType partyInquiryRequestParameters = new PartyInquiryRequestParametersType();
                PartyInquiryType partyInquiry = new PartyInquiryType();
                PartyType party = new PartyType();
                party.setPartyNumberId(String.valueOf(entityId));
                partyInquiry.setParty(party);
                partyInquiryRequestParameters.setPartyInquiry(partyInquiry);
                partyInquiryRequest.getPartyInquiryRequestParameters().add(partyInquiryRequestParameters);
            }

            PartyInquiryResultType partyInquiryResult = getPartyInquiryServiceManager().loadParty(partyInquiryRequest);
            propertyList = partyInquiryResult.getProperty();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPropertyInDb", propertyList);
        }
        return propertyList;
    }

    private PropertyType getPropertyById(List<PropertyType> propertyList, String propertyId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPropertyById", new Object[]{propertyList, propertyId});
        }

        PropertyType property = null;

        if (propertyList != null) {
            for (PropertyType tempProperty : propertyList) {
                if (tempProperty.getPropertyNumberId().equals(propertyId)) {
                    property = tempProperty;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPropertyById", property);
        }
        return property;
    }

    protected String getRowStatus(Record changedElementRecord, Record originalElementRecord, Record dbElementRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRowStatus",
                    new Object[]{changedElementRecord, originalElementRecord, dbElementRecord});
        }

        String rowStatus = ROW_STATUS_UNCHANGED;

        if (originalElementRecord == null && dbElementRecord == null) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getRowStatus", ROW_STATUS_NEW);
            }
            return ROW_STATUS_NEW;
        }

        Iterator fieldNames = changedElementRecord.getFieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = (String) fieldNames.next();
            String fieldValue = changedElementRecord.getStringValue(fieldName, "");

            if (originalElementRecord != null && originalElementRecord.hasField(fieldName)) {
                if (!fieldValue.equals(originalElementRecord.getFieldValue(fieldName, ""))) {
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "getRowStatus", ROW_STATUS_MODIFIED);
                    }
                    return ROW_STATUS_MODIFIED;
                }
            } else if (dbElementRecord != null && dbElementRecord.hasField(fieldName)) {
                if (!fieldValue.equals(dbElementRecord.getFieldValue(fieldName, ""))) {
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "getRowStatus", ROW_STATUS_MODIFIED);
                    }
                    return ROW_STATUS_MODIFIED;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRowStatus", rowStatus);
        }
        return rowStatus;
    }

    private void validateProperty(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateProperty",
                    new Object[]{record});
        }

        Validator.validateFieldRequired(
                record.getStringValue("propertyName", ""),
                "ci.partyChangeService.field.required.error",
                "Property Name");
        
        l.exiting(getClass().getName(), "validateProperty");
    }

    private void saveProperty(PropertyType changedProperty,
                              Record changedPropertyRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveProperty",
                    new Object[]{changedProperty, changedPropertyRecord});
        }

        validateProperty(changedPropertyRecord);
        Record recUpdateResult = getPropertyManager().saveProperty(changedPropertyRecord);

        if (changedPropertyRecord.getStringValue(ROW_STATUS, "").equals(ROW_STATUS_NEW)) {
            changedProperty.setPropertyNumberId(recUpdateResult.getStringValue("newPropertyId"));
        }

        l.exiting(getClass().getName(), "saveProperty");
    }

    private Record getPropertyRecord(PartyChangeRequestType partyChangeRequest, PropertyType property) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPropertyRecord",
                    new Object[]{partyChangeRequest, property});
        }

        Record record = null;

        if (property != null) {
            record = new Record();

            if (StringUtils.isBlank(property.getPropertyNumberId())) {
                String addressReference = getChildElementValue(property, ELEMENT_NAME_ADDRESS_REFERENCE);
                if (StringUtils.isBlank(addressReference)) {
                    MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                            new Object[]{"The Address Reference of a Property cannot be empty."});
                    throw new AppException("The Address Reference of a Property cannot be empty.");
                }

                String entityId = getEntityId(partyChangeRequest, addressReference);
                if (StringUtils.isBlank(entityId)) {
                    MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                            new Object[]{"Cannot find Party Number ID for Address Key: " + addressReference + "."});
                    throw new AppException("Cannot find Party Number ID by Address Key.");
                }
                record.setFieldValue("entityId", entityId);

                String addressId = getAddressId(partyChangeRequest, addressReference);
                if (StringUtils.isBlank(addressId)) {
                    MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                            new Object[]{"Cannot find Address Number ID for Address Key: " + addressReference + "."});
                    throw new AppException("Cannot find Address Number ID by Address Key.");
                }
                record.setFieldValue("addressId", addressId);
            } else {
                record.setFieldValue("propertyId", property.getPropertyNumberId());
            }

            mapObjectToRecord(getFieldElementMaps(), property, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPropertyRecord", record);
        }
        return record;
    }



    protected void mapObjectToRecord(List<FieldElementMap> fieldMapList, Object obj, Record record) {
        mapObjectToRecord(fieldMapList, obj, record, true);
    }

    protected void mapObjectToRecord(List<FieldElementMap> fieldMapList, Object obj, Record record,
                                     boolean overwriteFieldIfExists) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mapObjectToRecord",
                    new Object[]{fieldMapList, obj, record, overwriteFieldIfExists});
        }

        if (fieldMapList != null && obj != null && record != null) {
            for (FieldElementMap map : fieldMapList) {
                if (!overwriteFieldIfExists && record.hasField(map.getFieldName())) {
                    continue;
                }

                String value = map.getElementValue(obj);

                if (value != null) {
                    record.setFieldValue(map.getFieldName(), value);
                }
            }
        }

        l.exiting(getClass().getName(), "setMappedFieldValues");
    }

    private String getAddressId(PartyChangeRequestType partyChangeRequest, String addressKey) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddressId", new Object[]{partyChangeRequest, addressKey});
        }

        String addressId = null;

        if (partyChangeRequest != null) {
            for (AddressType address : partyChangeRequest.getAddress()) {
                if (addressKey.equals(address.getKey())) {
                    addressId = address.getAddressNumberId();
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddressId", addressId);
        }
        return addressId;
    }

    private String getEntityId(PartyChangeRequestType partyChangeRequest, String addressKey) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityId", new Object[]{partyChangeRequest, addressKey});
        }

        String entityId = null;

        if (partyChangeRequest != null) {
            for (PersonType person : partyChangeRequest.getPerson()) {
                for (BasicAddressType basicAddress : person.getBasicAddress()) {
                    if (addressKey.equals(basicAddress.getAddressReference())) {
                        entityId = person.getPersonNumberId();

                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "getEntityId", entityId);
                        }
                        return entityId;
                    }
                }
            }

            for (OrganizationType organization : partyChangeRequest.getOrganization()) {
                for (BasicAddressType basicAddress : organization.getBasicAddress()) {
                    if (addressKey.equals(basicAddress.getAddressReference())) {
                        entityId = organization.getOrganizationNumberId();

                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "getEntityId", entityId);
                        }
                        return entityId;
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityId", entityId);
        }
        return entityId;
    }

    private boolean hasChildElement(PropertyType property, String elementName) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasChildElement", new Object[]{property, elementName});
        }

        boolean exists = false;

        if (property == null) {
            exists = false;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "hasChildElement", Boolean.FALSE);
            }
            return false;
        }

        if (elementName.equals(ELEMENT_NAME_KEY)) {
            exists = (property.getKey() != null);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "hasChildElement", exists);
            }
            return exists;
        }

        if (elementName.equals(ELEMENT_NAME_PROPERTY_NAME)) {
            exists = (property.getPropertyName() != null && property.getPropertyName().getFullName() != null);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "hasChildElement", exists);
            }
            return exists;
        }

        if (elementName.equals(ELEMENT_NAME_ADDRESS_REFERENCE)) {
            exists = (property.getBasicAddress() != null && property.getBasicAddress().getAddressReference() != null);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "hasChildElement", exists);
            }
            return exists;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasChildElement", exists);
        }
        return exists;
    }

    private String getChildElementValue(PropertyType property, String elementName) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChildElementValue", new Object[]{property, elementName});
        }

        String elementValue = null;

        if (!hasChildElement(property, elementName)) {
            return null;
        }

        if (elementName.equals(ELEMENT_NAME_ADDRESS_REFERENCE)) {
            return property.getBasicAddress().getAddressReference();
        }
        if (elementName.equals(ELEMENT_NAME_PROPERTY_NAME)) {
            return property.getPropertyName().getFullName();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChildElementValue", elementValue);
        }
        return elementValue;
    }

    protected void mergeRecordValues(Record changedRecord, Record originalRecord, Record dbRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mergeRecordValues", new Object[]{changedRecord, originalRecord, dbRecord});
        }

        if (changedRecord != null) {
            if (originalRecord != null) {
                changedRecord.setFields(originalRecord, false);
            }

            if (dbRecord != null) {
                changedRecord.setFields(dbRecord, false);
            }
        }

        l.exiting(getClass().getName(), "mergeRecordValues");
    }

    public PartyInquiryServiceManager getPartyInquiryServiceManager() {
        return m_partyInquiryServiceManager;
    }

    public void setPartyInquiryServiceManager(PartyInquiryServiceManager partyInquiryServiceManager) {
        m_partyInquiryServiceManager = partyInquiryServiceManager;
    }

    public PropertyManager getPropertyManager() {
        return m_propertyManager;
    }

    public void setPropertyManager(PropertyManager propertyManager) {
        this.m_propertyManager = propertyManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private PartyInquiryServiceManager m_partyInquiryServiceManager;
    private PropertyManager m_propertyManager;
    private List<FieldElementMap> m_fieldElementMaps;

    private static final String ROW_STATUS = "rowStatus";
    private static final String ROW_STATUS_UNCHANGED = "UNCHANGED";
    private static final String ROW_STATUS_NEW = "NEW";
    private static final String ROW_STATUS_MODIFIED = "MODIFIED";
    private static final String ROW_STATUS_DELETED = "DELETED";

    private static final String ELEMENT_NAME_KEY = "Key";
    private static final String ELEMENT_NAME_PROPERTY_NAME = "PropertyName";
    private static final String ELEMENT_NAME_ADDRESS_REFERENCE = "AddressReference";
    private static final String ELEMENT_NAME_PHONE_ADDRESS_REFERENCE = "PhoneAddressReference";
}
