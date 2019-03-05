package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.EntityAdditionalXmlDataType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import dti.ci.entitymgr.service.partyadditionalinfomgr.PartyAdditionalInfoManger;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.xml.DOMUtils;
import org.w3c.dom.Node;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/26/2017
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
public class EntityAdditionalXmlChangeProcessor extends BasePartyChangeElementProcessor<EntityAdditionalXmlDataType> {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                        String entityType, String entityId,
                        EntityAdditionalXmlDataType changedEntityAdditionalXmlData, EntityAdditionalXmlDataType originalEntityAdditionalXml) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process", new Object[]{partyChangeRequest, partyChangeResult,
                    entityType, entityId, changedEntityAdditionalXmlData, originalEntityAdditionalXml});
        }

        Record changedEntityAdditionalXmlDataRecord = getEntityAdditionalXmlDataRecord(entityId, changedEntityAdditionalXmlData);

        if (changedEntityAdditionalXmlDataRecord != null) {
            getPartyAdditionalInfoManger().saveAdditionalXmlData(changedEntityAdditionalXmlDataRecord);
        }

        l.exiting(getClass().getName(), "process");
    }

    private Record getEntityAdditionalXmlDataRecord(String entityId, EntityAdditionalXmlDataType entityAdditionalXmlData) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityAdditionalXmlDataRecord", new Object[]{entityId, entityAdditionalXmlData});
        }

        Record record = null;

        if (entityAdditionalXmlData != null) {
            record = new Record();
            record.setFieldValue("sourceRecordId", entityId);
            record.setFieldValue("sourceTableName", "ENTITY");
            record.setFieldValue("xmlData", (entityAdditionalXmlData.getAny() == null ? "" : DOMUtils.nodeToString((Node)entityAdditionalXmlData.getAny())));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityAdditionalXmlDataRecord", record);
        }
        return record;
    }

    public void verifyConfig() {
        if (getPartyAdditionalInfoManger() == null)
            throw new ConfigurationException("The required property 'partyAdditionalInfoManger' is missing.");
    }

    public PartyAdditionalInfoManger getPartyAdditionalInfoManger() {
        return m_partyAdditionalInfoManger;
    }

    public void setPartyAdditionalInfoManger(PartyAdditionalInfoManger partyAdditionalInfoManger) {
        m_partyAdditionalInfoManger = partyAdditionalInfoManger;
    }

    private PartyAdditionalInfoManger m_partyAdditionalInfoManger;
}
