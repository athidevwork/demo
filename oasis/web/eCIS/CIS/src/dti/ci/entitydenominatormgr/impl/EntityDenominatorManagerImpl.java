package dti.ci.entitydenominatormgr.impl;

import dti.ci.entitydenominatormgr.EntityDenominatorManager;
import dti.ci.entitydenominatormgr.dao.EntityDenominatorDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object for Denominator
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   May 30, 2006
 *
 * @author bhong
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
*/

public class EntityDenominatorManagerImpl implements EntityDenominatorManager {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Load all denominator of an entity.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllEntityDenominator(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEntityDenominator", new Object[]{inputRecord});
        }

        RecordSet rs = getEntityDenominatorDAO().loadAllEntityDenominator(inputRecord, AddSelectIndLoadProcessor.getInstance());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEntityDenominator", rs);
        }
        return rs;
    }

    /**
     * Save all entity denominator.
     *
     * @param rs
     */
    @Override
    public void saveAllEntityDenominator(RecordSet rs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllEntityDenominator", new Object[]{rs});
        }

        RecordSet changedRs = OasisRecordSetHelper.setRowStatusOnModifiedRecords(rs);

        getEntityDenominatorDAO().saveAllEntityDenominator(changedRs);

        l.exiting(getClass().getName(), "saveAllEntityDenominator");
    }

    public void verifyConfig() {
        if (getEntityDenominatorDAO() == null)
            throw new ConfigurationException("The required property 'entityDenominatorDAO' is missing.");
    }

    public EntityDenominatorDAO getEntityDenominatorDAO() {
        return m_entityDenominatorDAO;
    }

    public void setEntityDenominatorDAO(EntityDenominatorDAO entityDenominatorDAO) {
        m_entityDenominatorDAO = entityDenominatorDAO;
    }

    private EntityDenominatorDAO m_entityDenominatorDAO;
}
