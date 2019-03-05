package dti.ci.disabilitymgr.impl;

import dti.ci.disabilitymgr.DisabilityManager;
import dti.ci.disabilitymgr.dao.DisabilityDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Business Object for Disability
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   May 12, 2006
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

public class DisabilityManagerImpl implements DisabilityManager {
    /**
     * Get Disability List of an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet loadDisabilityList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadDisabilityList", new Object[]{inputRecord});
        }

        RecordSet rs = getDisabilityDAO().getDisabilityList(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadDisabilityList", rs);
        }
        return rs;
    }

    /**
    * Method to save Disability information
    *
    * @param inputRecords
    * @return int
    */
    public int saveDisabilityData(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveDisabilityData", new Object[]{inputRecords});
        }
        // Get the changes
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);
        int updateCount = 0;
        if (changedRecords.getSize() > 0) {
            updateCount = getDisabilityDAO().saveDisabilityData(changedRecords); 
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveDisabilityData", new Integer(updateCount));
        }
        return updateCount;
    }

    public void verifyConfig() {
        if (getDisabilityDAO() == null) {
            throw new ConfigurationException("The required property 'getDisabilityDAO' is missing.");
        }
    }

    public DisabilityDAO getDisabilityDAO() {
        return m_disabilityDAO;
    }

    public void setDisabilityDAO(DisabilityDAO m_disabilityDAO) {
        this.m_disabilityDAO = m_disabilityDAO;
    }

    private DisabilityDAO m_disabilityDAO;


}
