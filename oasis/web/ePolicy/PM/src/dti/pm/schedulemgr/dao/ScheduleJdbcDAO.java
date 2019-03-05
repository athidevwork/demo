package dti.pm.schedulemgr.dao;

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
import dti.pm.schedulemgr.ScheduleFields;
import dti.pm.policymgr.PolicyHeaderFields;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;

/**
 * * This class implements the ScheduleDAO interface. This is consumed by any business logic objects
 * that requires information about one or more schedules.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ScheduleJdbcDAO extends BaseDAO implements ScheduleDAO {
    /**
     * Returns a RecordSet loaded with list of available schedules for the provided
     * risk/coverage information.
     * <p/>
     *
     * @param inputRecord         record with risk/coverage fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available sechedules for risk/coverage.
     */
    public RecordSet loadAllSchedules(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllSchedules", new Object[]{recordLoadProcessor});

        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sourceId", "sourceRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sourceTable", "sourceTableName"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Policy_Schedule.Sel_Schedule_Info", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to load schedule information for policyNo:"
                    + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "loadAllSchedules", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSchedules", rs);
        }
        return rs;
    }

    /**
     * Add all schedules' information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int addAllSchedules(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSchedules");

        int updateCount;

        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("schedId", "policyScheduleId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sourceId", "sourceRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sourceTable", "sourceTableName"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("text", "scheduleText"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgLimit", "coverageLimitCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        // Insert the records in batch mode with 'Pm_Schedule.Save_Schedule'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Schedule.Save_Schedule", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            e.printStackTrace();
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to add schedules.", e);
            l.throwing(getClass().getName(), "saveAllSchedules", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllSchedules", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * delete all schedules' information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int deleteAllSchedules(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllSchedules");

        int deleteCount;

        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("schedId", "policyScheduleId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));

        // Delete the records in batch mode with 'Pm_Schedule.Delete_Schedule'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Schedule.Delete_Schedule", mapping);
        try {
            deleteCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            e.printStackTrace();
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete schedules.", e);
            l.throwing(getClass().getName(), "deleteAllSchedules", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllSchedules", new Integer(deleteCount));
        }
        return deleteCount;
    }

    /**
     * Update all schedules' information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int updateAllSchedules(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllSchedules");

        int updateCount;

        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("schedId", "policyScheduleId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("text", "scheduleText"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgLimit", "coverageLimitCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        // Update the records in batch mode with 'Pm_Schedule.Change_Schedule'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Schedule.Change_Schedule", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            e.printStackTrace();
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update schedules.", e);
            l.throwing(getClass().getName(), "updateAllSchedules", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllSchedules", new Integer(updateCount));
        }
        return updateCount;
    }
    /**
 * copy all schedule data to target risk
 *
 * @param inputRecord
 */
    public void copyAllSchedule(Record inputRecord) {
    Logger l = LogUtils.getLogger(getClass());
    if (l.isLoggable(Level.FINER)) {
        l.entering(getClass().getName(), "copyAllSchedule", new Object[]{inputRecord});
    }

    // Create the input data mapping
    DataRecordMapping mapping = new DataRecordMapping();
    mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
    mapping.addFieldMapping(new DataRecordFieldMapping("effFromForEndorse", "transEffectiveFromDate"));
    mapping.addFieldMapping(new DataRecordFieldMapping("termEffDate", "termEff"));        
    mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termExp"));
    mapping.addFieldMapping(new DataRecordFieldMapping("toRiskBaseId", "toRiskBaseRecordId"));

    //  toRiskBaseRecordId, transEffectiveFromDate, termEff, termExp, numSchedules
    StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Copy_All.Copyall_Schedule", mapping);
    try {

        spDao.execute(inputRecord);
    }
    catch (SQLException e) {
        AppException ae = ExceptionHelper.getInstance().handleException("Unable to copy all schedule", e);
        l.throwing(getClass().getName(), "copyAllSchedule", ae);
        throw ae;
    }

    if (l.isLoggable(Level.FINER)) {
        l.exiting(getClass().getName(), "copyAllSchedule");
    }
}
}


