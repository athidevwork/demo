package dti.ci.entitymgr.impl;

import dti.ci.core.EntityInfo;
import dti.ci.core.recordset.RecordHelper;
import dti.ci.entitymgr.EntityFields;
import dti.ci.entitymgr.EntityManager;
import dti.ci.entitymgr.dao.EntityDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 28, 2010
 *
 * @author ldong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/24/2016       Elvin       Issue 176524: add searchEntityForWS
 * ---------------------------------------------------
 */
public class EntityManagerImpl implements EntityManager {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Retrieves all Entity information
     * @param record Record
     * @return RecordSet
     */
    public RecordSet loadEntityList(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityList", new Object[]{record});
        }

        /* Gets Edi Extract History record set */
        RecordSet rs = getEntityDAO().loadEntityList(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityList", rs);
        }
        return rs;
    }

    /**
     * Add new Entity
     * @param record
     * @return Record
     */
    public Record AddEntity(Record record){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "AddEntity", new Object[]{record});
        }

        /* Gets Edi Extract History record set */
        Record rs = getEntityDAO().AddEntity(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "AddEntity", rs);
        }
        return rs;
    }

    /**
     * Save entity for service
     *
     * @param record
     */
    public void saveEntityForService(Record record){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityForService", new Object[]{record});
        }

        getEntityDAO().saveEntityForService(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEntityForService");
        }
    }

    /**
     * Save PartyNote
     *
     * @param inputRecord
     */
    public Record savePartyNote(Record inputRecord){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePartyNote", new Object[]{inputRecord});
        }

        Record recResult = getEntityDAO().savePartyNote(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePartyNote");
        }
        return recResult;
    }

    @Override
    public RecordSet searchEntityForWS(Record inputRecord) {
        return getEntityDAO().searchEntityForWS(inputRecord);
    }

    /**
     * Check if an entity has tax ID info.
     *
     * @param inputRecord
     * @return Returns {@code true} if either "Tax ID" or "SSN" exists. Otherwise, returns {@code false};
     */
    @Override
    public boolean hasTaxIdInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasTaxIdInfo", new Object[]{inputRecord});
        }

        boolean hasTaxIdInfo = getEntityDAO().hasTaxIdInfo(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasTaxIdInfo", hasTaxIdInfo);
        }
        return hasTaxIdInfo;
    }

    /**
     * Get entity type.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public String getEntityType(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityType", new Object[]{inputRecord});
        }

        String entityType = getEntityDAO().getEntityType(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityType", entityType);
        }
        return entityType;
    }

    /**
     * load Entity data into Per & Org modify page and mini popup page
     * @param inputRecord
     * @return
     */
    @Override
    public Record loadEntityData(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityData", new Object[]{inputRecord});
        }

        Record record = getEntityDAO().loadEntityData(inputRecord);

        // add the prefix as of "entity_XXX"
        record = RecordHelper.addRecordPrefix(record, "entity_");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityData", record);
        }
        return record;
    }

    @Override
    public String getEntityName(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityName", new Object[]{inputRecord});
        }

        String entityName = getEntityDAO().getEntityName(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityName", entityName);
        }
        return entityName;
    }

    @Override
    public EntityInfo getEntityInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityInfo", new Object[]{inputRecord});
        }

        Record entityData = getEntityDAO().loadEntityData(inputRecord);

        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setEntityId(EntityFields.getEntityId(entityData));
        entityInfo.setEntityName(EntityFields.getEntityNameComputed(entityData));
        entityInfo.setEntityType(EntityFields.getEntityType(entityData));
        entityInfo.setClientId(EntityFields.getClientId(entityData));
        entityInfo.setGender(EntityFields.getGender(entityData));
        entityInfo.setSocialSecurityNumber(EntityFields.getSocialSecurityNumber(entityData));
        entityInfo.setLegacyDataID(EntityFields.getLegacyDataID(entityData));
        entityInfo.setReferenceNumber(EntityFields.getReferenceNumber(entityData));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityInfo", entityInfo);
        }
        return entityInfo;
    }

    public void  verifyConfig() {
        if (getEntityDAO() == null) {
            throw new ConfigurationException("The required property 'entityDAO' is missing.");
        }
    }

    public EntityDAO getEntityDAO() {
        return m_entityDAO;
    }

    public void setEntityDAO(EntityDAO entityDAO) {
        this.m_entityDAO = entityDAO;
    }

    private EntityDAO m_entityDAO;
}
