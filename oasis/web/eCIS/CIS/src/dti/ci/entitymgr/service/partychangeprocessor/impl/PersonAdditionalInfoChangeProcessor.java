package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.PersonAdditionalInfoType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import dti.ci.entitymgr.service.partyadditionalinfomgr.PartyAdditionalInfoManger;
import dti.oasis.app.ConfigurationException;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/12/2017
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
public class PersonAdditionalInfoChangeProcessor extends BasePartyChangeElementProcessor<PersonAdditionalInfoType> {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                        String entityType, String entityId,
                        PersonAdditionalInfoType changedPersonAdditionalInfo, PersonAdditionalInfoType originalPersonAdditionalInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, partyChangeResult,
                    entityType, entityId, changedPersonAdditionalInfo, originalPersonAdditionalInfo});
        }

        Record changedPersonAdditionalInfoRecord = getPersonAdditionalInfoRecord(entityId, changedPersonAdditionalInfo);
        Record originalPersonAdditionalInfoRecord = getPersonAdditionalInfoRecord(entityId, originalPersonAdditionalInfo);
        Record personAdditionalInfoRecordInDb = getPersonAdditionalInfoRecordFromDB(entityId);

        String rowStatus = getRowStatus(changedPersonAdditionalInfoRecord, originalPersonAdditionalInfoRecord, personAdditionalInfoRecordInDb);

        if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
            mergeRecordValues(changedPersonAdditionalInfoRecord, personAdditionalInfoRecordInDb);

            Record changedValues = getChangedValues(changedPersonAdditionalInfoRecord, originalPersonAdditionalInfoRecord, personAdditionalInfoRecordInDb, null);
            changedValues.setFieldValue("entityId", entityId);

            getPartyAdditionalInfoManger().savePersonAdditionalInfo(changedValues);
        }

        l.exiting(getClass().getName(), "process");
    }

    private Record getPersonAdditionalInfoRecord(String entityId, PersonAdditionalInfoType personAdditionalInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPersonAdditionalInfoRecord", new Object[]{entityId, personAdditionalInfo});
        }

        Record record = null;

        if (personAdditionalInfo != null) {
            record = new Record();
            record.setFieldValue("entityId", entityId);

            mapObjectToRecord(getFieldElementMaps(), personAdditionalInfo, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPersonAdditionalInfoRecord", record);
        }
        return record;
    }

    private Record getPersonAdditionalInfoRecordFromDB(String entityId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPersonAdditionalInfoRecordFromDB", new Object[]{entityId});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("entityId", entityId);

        Record record = getPartyAdditionalInfoManger().loadPersonAdditionalInfo(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPersonAdditionalInfoRecordFromDB", record);
        }
        return record;
    }

    public void verifyConfig() {
        if (getFieldElementMaps() == null)
            throw new ConfigurationException("The required property 'fieldElementMaps' is missing.");
        if (getPartyAdditionalInfoManger() == null)
            throw new ConfigurationException("The required property 'partyAdditionalInfoManger' is missing.");
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    public PartyAdditionalInfoManger getPartyAdditionalInfoManger() {
        return m_partyAdditionalInfoManger;
    }

    public void setPartyAdditionalInfoManger(PartyAdditionalInfoManger partyAdditionalInfoManger) {
        m_partyAdditionalInfoManger = partyAdditionalInfoManger;
    }

    private PartyAdditionalInfoManger m_partyAdditionalInfoManger;
    private List<FieldElementMap> m_fieldElementMaps;
}
