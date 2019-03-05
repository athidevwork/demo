package dti.ci.demographic.clientmgr.mntduplicate.bo;

import dti.ci.demographic.clientmgr.mntduplicate.data.EntityMntDuplicateDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle Maintain Entity Duplicate page information.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 19, 2008
 *
 * @author ldong
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 * 11/26/2015       dpang       Issue 164029. Add saveEntityMntDuplicateWs for web service PartyChangeService
 * ---------------------------------------------------
*/

public class EntityMntDuplicateManagerImpl implements EntityMntDuplicateManager{

    /**
     * Merge Duplicate Entity
     *
     * @param inputRecords input records
     * @return a string representing the error Message if any, or null
     */
    public String saveEntityMntDuplicate(Record inputRecords){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityMntDuplicate", new Object[]{inputRecords});
        }

        /* call DAO to merge entities */
        String rslt = getEntityMntDuplicateDAO().saveEntityMntDuplicate(inputRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEntityMntDuplicate", rslt);
        }
        return rslt;
    }

    /**
     * Merge Duplicate Entity for web service PartyChangeService.
     *
     * @param inputRecords input records
     * @return a string representing the error Message if any, or null
     */
    @Override
    public String saveEntityMntDuplicateWs(Record inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityMntDuplicateWs", new Object[]{inputRecords});
        }

        /* call DAO to merge entities */
        String rslt = getEntityMntDuplicateDAO().saveEntityMntDuplicateWs(inputRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEntityMntDuplicateWs", rslt);
        }
        return rslt;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getEntityMntDuplicateDAO() == null) {
            throw new ConfigurationException("The required property 'EntityMntDuplicateDAO' is missing.");
        }

    }

    public EntityMntDuplicateDAO getEntityMntDuplicateDAO() {
        return m_entityMntDuplicateDAO;
    }

    public void setEntityMntDuplicateDAO(EntityMntDuplicateDAO entityMntDuplicateDAO) {
        m_entityMntDuplicateDAO = entityMntDuplicateDAO;
    }

    private EntityMntDuplicateDAO m_entityMntDuplicateDAO;
}
