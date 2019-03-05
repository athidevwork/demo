package dti.ci.entityminipopupmgr.impl;

import dti.ci.contactmgr.dao.ContactDAO;
import dti.ci.contactmgr.impl.ContactListLoadProcessor;
import dti.ci.core.recordset.RecordHelper;
import dti.ci.entitymgr.dao.EntityDAO;
import dti.ci.entityminipopupmgr.EntityMiniPopupFields;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.app.ConfigurationException;
import dti.ci.entityminipopupmgr.EntityMiniPopupManager;
import dti.ci.entityminipopupmgr.dao.EntityMiniPopupDAO;
import dti.oasis.util.StringUtils;

import java.util.logging.Logger;
import java.util.logging.Level;

import static dti.ci.core.CIFields.ENTITY_TYPE_ORG_CHAR;
import static dti.ci.core.CIFields.ENTITY_TYPE_PERSON_CHAR;

/**
 * Implementation class to handle logics of Entity Mini Popup Manager
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 28, 2010
 *
 * @author bchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/17/2018       dzhang      Issue 192649: entity mini popup refactor
 * ---------------------------------------------------
 */
public class EntityMiniPopupManagerImpl implements EntityMiniPopupManager {

    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public Record loadEntity(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntity", new Object[]{inputRecord});
        }

        Record record = getEntityDAO().loadEntityData(inputRecord);

        // add the prefix as of "entity_XXX"
        record = RecordHelper.addRecordPrefix(record, EntityMiniPopupFields.ENTITY_PREFIX);

        String entityType = EntityMiniPopupFields.getEntityType(record);
        if (!StringUtils.isBlank(entityType)) {
            if (entityType.charAt(0) == ENTITY_TYPE_PERSON_CHAR) {
                record.setFieldValue(EntityMiniPopupFields.ENTITY_TYPE_B, ENTITY_TYPE_PERSON_CHAR);
            } else if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                record.setFieldValue(EntityMiniPopupFields.ENTITY_TYPE_B, ENTITY_TYPE_ORG_CHAR);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntity", record);
        }
        return record;
    }

    @Override
    public RecordSet loadEntityAddressList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityAddressList", new Object[]{inputRecord});
        }

        RecordSet addrList = getEntityMiniPopupDAO().loadEntityAddressList(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityAddressList",addrList);
        }
        return addrList;
    }

    @Override
    public RecordSet loadAddressPhoneList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressPhoneList", new Object[]{inputRecord});
        }

        RecordSet outputRS = getEntityMiniPopupDAO().loadAddressPhoneList(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressPhoneList", outputRS);
        }
        return outputRS;
    }

    /**
     * To load contact list
     *
     * @param inputRecord
     * @return
     */
    public RecordSet getContactList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getContactList", new Object[]{inputRecord});
        }

        RecordSet rs = getContactDAO().loadAllContact(inputRecord, new ContactListLoadProcessor());
        rs = RecordHelper.addPrefixToAllRecords(rs, EntityMiniPopupFields.CONTACT_PREFIX);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getContactList", rs);
        }
        return rs;
    }

    /**
     * Load entity general phone list
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadEntityGeneralPhoneList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityGeneralPhoneList", new Object[]{inputRecord});
        }

        RecordSet generalPhoneList = getEntityMiniPopupDAO().loadEntityGeneralPhoneList(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityGeneralPhoneList", generalPhoneList);
        }
        return generalPhoneList;
    }

    /**
     * To verify configuration
     *
     * @param
     * @return
     */
    public void verifyConfig() {
        if (getEntityMiniPopupDAO() == null) {
            throw new ConfigurationException("The required property 'entityMiniPopupDAO' is missing.");
        }

        if (getEntityDAO() == null) {
            throw new ConfigurationException("The required property 'entityDAO' is missing.");
        }

        if (getContactDAO() == null) {
            throw new ConfigurationException("The required property 'contactDAO' is missing.");
        }
    }

    public EntityMiniPopupDAO getEntityMiniPopupDAO() {
        return m_entityMiniPopupDAO;
    }

    public void setEntityMiniPopupDAO(EntityMiniPopupDAO entityMiniPopupDAO) {
        this.m_entityMiniPopupDAO = entityMiniPopupDAO;
    }

    public EntityDAO getEntityDAO() {
        return m_entityDAO;
    }

    public void setEntityDAO(EntityDAO entityDAO) {
        this.m_entityDAO = entityDAO;
    }

    public ContactDAO getContactDAO() {
        return m_contactDAO;
    }

    public void setContactDAO(ContactDAO contactDAO) {
        this.m_contactDAO = contactDAO;
    }

    private EntityMiniPopupDAO m_entityMiniPopupDAO;
    private EntityDAO m_entityDAO;
    private ContactDAO m_contactDAO;
}
