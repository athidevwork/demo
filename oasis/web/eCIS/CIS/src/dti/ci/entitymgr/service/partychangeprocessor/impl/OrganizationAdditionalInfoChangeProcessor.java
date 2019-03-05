package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.OrganizationAdditionalInfoType;
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
 * Date:   4/14/2017
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
public class OrganizationAdditionalInfoChangeProcessor extends BasePartyChangeElementProcessor<OrganizationAdditionalInfoType> {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                        String entityType, String entityId,
                        OrganizationAdditionalInfoType changedOrgAdditionalInfo, OrganizationAdditionalInfoType originalOrgAdditionalInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, partyChangeResult,
                    entityType, entityId, changedOrgAdditionalInfo, originalOrgAdditionalInfo});
        }

        Record changedOrgAdditionalInfoRecord = getOrgAdditionalInfoRecord(entityId, changedOrgAdditionalInfo);
        Record originalOrgAdditionalInfoRecord = getOrgAdditionalInfoRecord(entityId, originalOrgAdditionalInfo);
        Record orgAdditionalInfoRecordInDb = getOrgAdditionalInfoRecordFromDB(entityId);

        String rowStatus = getRowStatus(changedOrgAdditionalInfoRecord, originalOrgAdditionalInfoRecord, orgAdditionalInfoRecordInDb);

        if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
            mergeRecordValues(changedOrgAdditionalInfoRecord, orgAdditionalInfoRecordInDb);

            Record changedValues = getChangedValues(changedOrgAdditionalInfoRecord, originalOrgAdditionalInfoRecord, orgAdditionalInfoRecordInDb, null);
            changedValues.setFieldValue("entityId", entityId);

            getPartyAdditionalInfoManger().saveOrganizationAdditionalInfo(changedValues);
        }

        l.exiting(getClass().getName(), "process");
    }

    private Record getOrgAdditionalInfoRecord(String entityId, OrganizationAdditionalInfoType orgAdditionalInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOrgAdditionalInfoRecord", new Object[]{entityId, orgAdditionalInfo});
        }

        Record record = null;

        if (orgAdditionalInfo != null) {
            record = new Record();
            record.setFieldValue("entityId", entityId);

            mapObjectToRecord(getFieldElementMaps(), orgAdditionalInfo, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOrgAdditionalInfoRecord", record);
        }
        return record;
    }

    private Record getOrgAdditionalInfoRecordFromDB(String entityId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOrgAdditionalInfoRecordFromDB", new Object[]{entityId});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("entityId", entityId);

        Record record = getPartyAdditionalInfoManger().loadOrganizationAdditionalInfo(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOrgAdditionalInfoRecordFromDB", record);
        }
        return record;
    }

    public void verifyConfig() {
        if (getPartyAdditionalInfoManger() == null)
            throw new ConfigurationException("The required property 'partyAdditionalInfoManger' is missing.");
        if (getFieldElementMaps() == null)
            throw new ConfigurationException("The required property 'fieldElementMaps' is missing.");
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
