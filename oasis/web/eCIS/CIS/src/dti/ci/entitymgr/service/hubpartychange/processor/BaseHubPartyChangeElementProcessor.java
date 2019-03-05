package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.partyinquiryservice.*;
import dti.ci.entitymgr.service.PartyInquiryServiceManager;
import dti.ci.entitymgr.service.partychangeprocessor.PartyChangeProcessor;
import dti.oasis.app.ConfigurationException;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/20/2016
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
public abstract class BaseHubPartyChangeElementProcessor<T> implements HubPartyChangeElementProcessor<T> {
    /**
     * Process entity elements.
     *
     * @param partyChangeRequest
     * @param cisResultElements
     * @param entityType
     * @param entityId
     * @param changedElements
     * @param originalElements
     */
    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<T> cisResultElements,
                        String entityType, String entityId, List<T> changedElements, List<T> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process",
                    new Object[]{partyChangeRequest, cisResultElements, entityType, entityId, changedElements, originalElements});
        }

        if (cisResultElements != null) {
            processFromCisResult(partyChangeRequest, partyChangeResult, cisResultElements, entityId, changedElements);
        } else {
            processForHub(partyChangeRequest, entityType, entityId, changedElements, originalElements);
        }

        l.exiting(getClass().getName(), "process");
    }

    /**
     *
     * Get Party Info
     * @param entityId
     * @param partyChangeRequest
     * @return
     */
    protected PartyInquiryResultType getPartyInfo(String entityId, PartyChangeRequestType partyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPartyInfo", new Object[]{entityId});
        }

        PartyInquiryRequestType partyInquiryRequest = new PartyInquiryRequestType();
        PartyInquiryRequestParametersType partyInquiryRequestParameters = new PartyInquiryRequestParametersType();
        PartyInquiryType partyInquiry = new PartyInquiryType();
        PartyType party = new PartyType();
        party.setPartyNumberId(String.valueOf(entityId));
        if (partyChangeRequest.getSendingSystemInformation() != null) {
            SendingSystemInformationType sendingSystemInfo = new SendingSystemInformationType();
            sendingSystemInfo.setVendorProductName(partyChangeRequest.getSendingSystemInformation().getVendorProductName());
            partyInquiryRequest.setSendingSystemInformation(sendingSystemInfo);
        }
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

    protected void setCommonFieldsToRecord(Record inputRecord, PartyChangeRequestType partyChangeRequest, String cisB) {
        if (partyChangeRequest.getSendingSystemInformation() != null && !StringUtils.isBlank(partyChangeRequest.getSendingSystemInformation().getVendorProductName())) {
            inputRecord.setFieldValue("origin", partyChangeRequest.getSendingSystemInformation().getVendorProductName());
        } else {
            //Set default value of vendorProductName to OASIS
            inputRecord.setFieldValue("origin", "OASIS");
        }
        inputRecord.setFieldValue("cisB", cisB);
    }

    protected String getSourceSystem(PartyChangeRequestType partyChangeRequest) {
        return partyChangeRequest.getSendingSystemInformation() == null ? "OASIS" : partyChangeRequest.getSendingSystemInformation().getVendorProductName();
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

    protected static final String ROW_STATUS = "rowStatus";
    protected static final String ROW_STATUS_UNCHANGED = "UNCHANGED";
    protected static final String ROW_STATUS_NEW = "NEW";
    protected static final String ROW_STATUS_MODIFIED = "MODIFIED";
    protected static final String CISB_Y = "Y";
    protected static final String CISB_N = "N";
}
