package dti.pm.policymgr.processacfmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the ProcessAcfDAO interface. This is consumed by any business logic objects
 * that requires information about ACF.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ProcessAcfJdbcDAO extends BaseDAO implements ProcessAcfDAO {

    /**
     * Retrieves all product.
     *
     * @param inputRecord         input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */
    public RecordSet loadAllProduct(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllProduct", new Object[]{inputRecord, recordLoadProcessor});
        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Alloc_N_Comm.Sel_Config_Data");
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all product.", e);
            l.throwing(getClass().getName(), "loadAllProduct", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProduct", rs);
        }
        return rs;
    }

    /**
     * Retrieves all override.
     *
     * @param inputRecord         input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */
    public RecordSet loadAllOverride(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllOverride", new Object[]{inputRecord, recordLoadProcessor});
        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Alloc_N_Comm.Sel_Override_Data");
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all override.", e);
            l.throwing(getClass().getName(), "loadAllOverride", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllOverride", rs);
        }
        return rs;
    }

    /**
     * Retrieves all result.
     *
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */
    public RecordSet loadAllResult(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllResult", new Object[]{inputRecord});
        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Alloc_N_Comm.Sel_Results_Data");
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all result.", e);
            l.throwing(getClass().getName(), "loadAllResult", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllResult", rs);
        }
        return rs;
    }

    /**
     * Retrieves all fee.
     *
     * @param inputRecord         input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */

    public RecordSet loadAllFee(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllFee", new Object[]{inputRecord});
        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Alloc_N_Comm.Sel_Fees_Data");
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all fee.", e);
            l.throwing(getClass().getName(), "loadAllFee", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllFee", rs);
        }
        return rs;
    }

    /**
     * Save all override records.
     *
     * @param inputRecords input RecordSet
     * @return int the number of rows saved.
     */
    public int saveAllOverride(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllOverride", new Object[]{inputRecords});
        }
        int updateCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("id", "policyBrokerageOverrideId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("mode", "rowStatus"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "overrideTransId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "overrideTermId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Alloc_N_Comm.Save_Override_Data", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save all override.", e);
            l.throwing(getClass().getName(), "saveAllOverride", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAllOverride", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Save all fee records.
     *
     * @param inputRecords input RecordSet
     * @return int the number of rows saved.
     */
    public int saveAllFee(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllFee", new Object[]{inputRecords});
        }
        int updateCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("mode", "rowStatus"));
        mapping.addFieldMapping(new DataRecordFieldMapping("id", "policyBrokerageFeeDetailId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "feeEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "feeTransId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "feeTermId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Alloc_N_Comm.Save_Fees_Data", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save all fee.", e);
            l.throwing(getClass().getName(), "saveAllFee", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAllFee", new Integer(updateCount));
        return updateCount;
    }
}