package dti.ci.entityhistoricaldatamgr.impl;

import dti.ci.entityhistoricaldatamgr.dao.EntityHistoricalDataDAO;
import dti.oasis.recordset.*;
import dti.oasis.struts.AddSelectIndLoadProcessor;
//import dti.ci.core.struts.AddRowNoLoadProcessor;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.ci.entityhistoricaldatamgr.EntityHistoricalDataManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of EntityHistoricalDataManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: August 08, 2010
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityHistoricalDataManagerImpl implements EntityHistoricalDataManager {
    /**
     * Get the Discount Points Hist recordset
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAvailableEntityHistoricalDatas(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableEntityHistoricalDatas", new Object[]{inputRecord});
        }

        RecordSet rs = getEntityHistoricalDataDAO().loadAllAvailableEntityHistoricalDatas(inputRecord,AddSelectIndLoadProcessor.getInstance());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableEntityHistoricalDatas", rs);
        }
        return rs;
    }

    
    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getEntityHistoricalDataDAO() == null)
            throw new ConfigurationException("The required property 'entityHistoricalDataDAO' is missing.");
    }

    public EntityHistoricalDataDAO getEntityHistoricalDataDAO() {
        return entityHistoricalDataDAO;
    }

    public void setEntityHistoricalDataDAO(EntityHistoricalDataDAO entityHistoricalDataDAO) {
        this.entityHistoricalDataDAO = entityHistoricalDataDAO;
    }

    private EntityHistoricalDataDAO entityHistoricalDataDAO;
      /**
     * Set a new field "rowStatus" to every modified record.
     *
     * @param inputRecords the initial inputRecords
     * @return RecordSet the modified reocrds
     */
    public static RecordSet setRowStatusOnModifiedRecords(RecordSet inputRecords) {
        /* Determine if anything has changed */
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));

        /* Create a new RecordSet to include all added, modified and deleted records */
        RecordSet allRecords = new RecordSet();

        /* Add the inserted records into allRecords for batch mode update */
        if (insertedRecords.getSize() > 0) {
            insertedRecords.setFieldValueOnAll(ROW_STATUS, NEW);
            allRecords.addRecords(insertedRecords);
        }
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));

          /* Add the inserted records into allRecords for batch mode update */
        if (updatedRecords.getSize() > 0) {
            updatedRecords.setFieldValueOnAll(ROW_STATUS, MODIFIED);
            allRecords.addRecords(updatedRecords);
        }
        return allRecords;
    }

    private static final String ROW_STATUS = "rowStatus";
    private static final String NEW = "NEW";
    private static final String MODIFIED = "MODIFIED";
    private static final String DELETED = "DELETED";
}
