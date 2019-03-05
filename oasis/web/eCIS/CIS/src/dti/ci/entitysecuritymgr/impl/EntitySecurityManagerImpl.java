package dti.ci.entitysecuritymgr.impl;

import dti.ci.entitysecuritymgr.EntitySecurityManager;
import dti.ci.entitysecuritymgr.dao.EntitySecurityDAO;
import dti.ci.entitysecuritymgr.dao.EntitySecurityJdbcDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.io.Serializable;


/**
 * Business Object for Security
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   Herb Koenig
 *
 * @author Herb Koenig
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public class EntitySecurityManagerImpl implements EntitySecurityManager, Serializable {
    private static final EntitySecurityManagerImpl INSTANCE = new EntitySecurityManagerImpl();
    private EntitySecurityManagerImpl(){

    }

    public static EntitySecurityManagerImpl getInstance() {
        return INSTANCE;
    }

    public boolean isEntityReadOnly(Long pk)
    {

        if (!userHasReadWriteAccessToEntity(pk)){
            return true;
        }
        return false;
    }


    private boolean userHasReadWriteAccessToEntity(Long pk) {
        
      //  verifyConfig();
        Record record = new Record();

        record.setFieldValue("entityId",pk);

        EntitySecurityDAO Dao = new EntitySecurityJdbcDAO();

        RecordSet recResult = Dao.getSecurity(record);

        String retVal = recResult.getSummaryRecord().getStringValue("returnvalue");

        // Returning a null or Y means we are not readonly
        if (retVal == null || retVal.equals("Y")){
            return true;
        }


        return false;
    }

    public void verifyConfig() {
        if (getEntitySecurityDAO() == null) {
            throw new ConfigurationException("The required security 'entitySecurityDAO' is missing.");
        }
    }

    public EntitySecurityDAO getEntitySecurityDAO() {
        return m_Entity_securityDAO;
    }

    public void setEntitySecurityDAO(EntitySecurityDAO entitySecurityDAO) {
        m_Entity_securityDAO = entitySecurityDAO;
    }

    private EntitySecurityDAO m_Entity_securityDAO;
}
