package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.partyinquiryservice.*;
import dti.ci.entitymgr.EntityManager;
import dti.ci.entitymgr.service.PartyInquiryServiceFields;
import dti.ci.entitymgr.service.PartyInquiryServiceManager;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import org.springframework.util.CollectionUtils;

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
public abstract class BasePartyChangeProcessor implements PartyChangeProcessor {
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

        PartyInquiryResultParametersType partyInquiryResultParameters = new PartyInquiryResultParametersType();
        partyInquiryResultParameters.getViewName().add(PartyInquiryServiceFields.PARTY_ALL_ELEMENT_VIEW);
        partyInquiryRequest.setPartyInquiryResultParameters(partyInquiryResultParameters);

        PartyInquiryResultType partyInquiryResult = getPartyInquiryServiceManager().loadParty(partyInquiryRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPartyInfo", partyInquiryResult);
        }
        return partyInquiryResult;
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
            // Only merge the records in DB for validation.
            if (dbRecord != null) {
                changedRecord.setFields(dbRecord, false);
            }
        }

        l.exiting(getClass().getName(), "mergeRecordValues");
    }

    /**
     * Get changed fields
     * @param changedRecord
     * @param originalRecord
     * @param dbRecord
     * @return
     */
    protected Record getChangedValues(Record changedRecord, Record originalRecord, Record dbRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedValues", new Object[]{changedRecord, originalRecord, dbRecord});
        }

        Record record = new Record();
        if (changedRecord.hasField("entityId")) {
            record.setFieldValue("entityId", changedRecord.getStringValue("entityId", ""));
        }

        if (changedRecord != null) {
            if (originalRecord != null && dbRecord != null) {
                Iterator fieldNames = changedRecord.getFieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = (String) fieldNames.next();

                    if (originalRecord.hasField(fieldName)) {
                        if (!changedRecord.getStringValue(fieldName, "").equals(originalRecord.getStringValue(fieldName, ""))) {
                            // If the value in changed xml is different with the value in original record, the field is changed.
                            record.setFieldValue(fieldName, changedRecord.getStringValue(fieldName, ""));
                        }
                    } else {
                        // If the value is not found in original xml, we think the field is changed.
                        record.setFieldValue(fieldName, changedRecord.getStringValue(fieldName, ""));
                    }
                }
            } else {
                // Get all field values for a new record.
                record.setFields(changedRecord);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedValues", record);
        }
        return record;
    }

    /**
     * Check if needs to process the element type.
     *
     * @param changedTypeList
     * @param originalTypeList
     * @return
     */
    protected boolean isProcessNeeded(List changedTypeList, List originalTypeList) {
        return !CollectionUtils.isEmpty(changedTypeList) || !CollectionUtils.isEmpty(originalTypeList);
    }

    /**
     * Get entity type.
     * @return
     */
    public abstract String getEntityType();

    public PartyInquiryServiceManager getPartyInquiryServiceManager() {
        return m_partyInquiryServiceManager;
    }

    public void setPartyInquiryServiceManager(PartyInquiryServiceManager partyInquiryServiceManager) {
        m_partyInquiryServiceManager = partyInquiryServiceManager;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private PartyInquiryServiceManager m_partyInquiryServiceManager;
    private EntityManager m_entityManager;
    private List<FieldElementMap> m_fieldElementMaps;

    public static final String ROW_STATUS = "rowStatus";
    public static final String ROW_STATUS_UNCHANGED = "UNCHANGED";
    public static final String ROW_STATUS_NEW = "NEW";
    public static final String ROW_STATUS_MODIFIED = "MODIFIED";
    public static final String ROW_STATUS_DELETED = "DELETED";
}