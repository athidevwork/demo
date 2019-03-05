package dti.pm.coveragemgr.excesspremiummgr.dao;

import dti.oasis.app.AppException;
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
 * Implements DAO operation for manual access premium.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 01, 2009
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ExcessPremiumJdbcDAO extends BaseDAO implements ExcessPremiumDAO {
    /**
     * Load all manual excess premium.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllExcessPremium(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllExcessPremium", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_M_XS_PREM.Get_Dtl_Rows");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load manual excess premium", e);
            l.throwing(getClass().getName(), "loadAllExcessPremium", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllExcessPremium", rs);
        }
        return rs;
    }

    /**
     * Get column name and label for specific column.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getExcessPremiumColumn(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getExcessPremiumColumn", new Object[]{inputRecord});
        }

        String label;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_m_xs_prem.get_head");
        try {
            label = spDao.execute(inputRecord).getSummaryRecord().getStringValue(
                StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get manual excess premium column", e);
            l.throwing(getClass().getName(), "getExcessPremiumColumn", ae);
            throw ae;
        }

        // Popluate return record
        Record returnRecord = new Record();
        String columnName = "layerAmount" + inputRecord.getStringValue("colNo");
        returnRecord.setFieldValue("columnName", columnName);
        returnRecord.setFieldValue("label", label);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getExcessPremiumColumn", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Save all manual excess premium.
     *
     * @param inputRecords
     */
    public void saveAllExcessPremium(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllExcessPremium", new Object[]{inputRecords});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_M_XS_PREM.Save");
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save all manual excess premium data", e);
            l.throwing(getClass().getName(), "saveAllExcessPremium", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllExcessPremium");
        }
    }
}
