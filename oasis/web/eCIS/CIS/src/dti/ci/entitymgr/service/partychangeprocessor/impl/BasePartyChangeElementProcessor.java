package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.*;
import dti.ci.entitymgr.service.PartyInquiryServiceManager;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeElementProcessor;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.oasis.app.ConfigurationException;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.Iterator;
import java.util.List;
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
 *
 * ---------------------------------------------------
 */
public abstract class BasePartyChangeElementProcessor<T> implements PartyChangeElementProcessor<T> {
    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, String entityType, String entityId, List<T> changedElements, List<T> originalElements) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, String entityType, String entityId, T changedElement, T originalElement) {
        throw new UnsupportedOperationException("Unsupported");
    }

    /**
     * Get Party Info
     * @param entityId
     * @return
     */
    protected PartyInquiryResultType getPartyInfo(String entityId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPartyInfo", new Object[]{entityId});
        }

        PartyInquiryRequestType partyInquiryRequest = new PartyInquiryRequestType();
        PartyInquiryRequestParametersType partyInquiryRequestParameters = new PartyInquiryRequestParametersType();
        PartyInquiryType partyInquiry = new PartyInquiryType();
        PartyType party = new PartyType();
        party.setPartyNumberId(String.valueOf(entityId));
        partyInquiry.setParty(party);
        partyInquiryRequestParameters.setPartyInquiry(partyInquiry);
        partyInquiryRequest.getPartyInquiryRequestParameters().add(partyInquiryRequestParameters);

        PartyInquiryResultType partyInquiryResult = getPartyInquiryServiceManager().loadParty(partyInquiryRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPartyInfo", partyInquiryResult);
        }
        return partyInquiryResult;
    }

    protected String getEntityKey(PartyChangeRequestType partyChangeRequest, String entityType, String entityId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityKey", new Object[]{partyChangeRequest, entityType, entityId});
        }

        String entityKey = null;
        if (entityType.equals(PartyChangeProcessor.ENTITY_TYPE_PERSON)) {
            for (PersonType person : partyChangeRequest.getPerson()) {
                if (entityId.equals(person.getPersonNumberId())) {
                    entityKey = person.getKey();
                    break;
                }
            }
        } else {
            for (OrganizationType organization : partyChangeRequest.getOrganization()) {
                if (entityId.equals(organization.getOrganizationNumberId())) {
                    entityKey = organization.getKey();
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityKey", entityKey);
        }
        return entityKey;
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

    protected void mergeRecordValues(Record changedRecord, Record dbRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mergeRecordValues", new Object[]{changedRecord, dbRecord});
        }

        if (changedRecord != null) {
            if (dbRecord != null) {
                changedRecord.setFields(dbRecord, false);
            }
        }

        l.exiting(getClass().getName(), "mergeRecordValues");
    }

    protected Record getChangedValues(Record changedRecord, Record originalRecord, Record dbRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedValues",
                    new Object[]{changedRecord, originalRecord, dbRecord});
        }

        Record record = getChangedValues(changedRecord, originalRecord, dbRecord, null);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedValues", record);
        }
        return record;
    }

    protected Record getChangedValues(Record changedRecord, Record originalRecord, Record dbRecord, String[] additionalFieldNames) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedValues",
                    new Object[]{changedRecord, originalRecord, dbRecord, additionalFieldNames});
        }

        Record record = new Record();

        if (changedRecord != null) {
            if (originalRecord != null && dbRecord != null) {
                // Get additional field values.
                if (additionalFieldNames != null) {
                    for (String additionalFieldName : additionalFieldNames) {
                        if (changedRecord.hasField(additionalFieldName)) {
                            record.setFieldValue(additionalFieldName, changedRecord.getStringValue(additionalFieldName, ""));
                        }
                    }
                }

                Iterator fieldNames = changedRecord.getFieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = (String) fieldNames.next();

                    if (originalRecord.hasField(fieldName)) {
                        if (!changedRecord.getStringValue(fieldName, "").equals(originalRecord.getStringValue(fieldName, ""))) {
                            // The value in changed xml doesn't equal to the value in previous data.
                            record.setFieldValue(fieldName, changedRecord.getStringValue(fieldName, ""));
                        }
                    } else {
                        // The field is not found in original xml.
                        record.setFieldValue(fieldName, changedRecord.getStringValue(fieldName, ""));
                    }
                }
            } else {
                // This is a new record.
                record.setFields(changedRecord);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedValues", record);
        }
        return record;
    }

    public void verifyConfig() {
        if (getPartyInquiryServiceManager() == null)
            throw new ConfigurationException("The required property 'partyInquiryServiceManager' is missing.");
    }

    public PartyInquiryServiceManager getPartyInquiryServiceManager() {
        return m_partyInquiryServiceManager;
    }

    public void setPartyInquiryServiceManager(PartyInquiryServiceManager partyInquiryServiceManager) {
        m_partyInquiryServiceManager = partyInquiryServiceManager;
    }

    private PartyInquiryServiceManager m_partyInquiryServiceManager;

    public static final String ROW_STATUS = "rowStatus";
    public static final String ROW_STATUS_UNCHANGED = "UNCHANGED";
    public static final String ROW_STATUS_NEW = "NEW";
    public static final String ROW_STATUS_MODIFIED = "MODIFIED";
    public static final String ROW_STATUS_DELETED = "DELETED";
}
