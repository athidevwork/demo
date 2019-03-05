package dti.pm.riskmgr.nationalprogrammgr.dao;

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
 * This class implements the NationalProgramDAO interface. This is consumed by any business logic objects
 * that requires information about one or more National Program.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 25, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------

 * ---------------------------------------------------
 */
public class NationalProgramJdbcDAO extends BaseDAO implements NationalProgramDAO {

    /**
     * Returns a RecordSet loaded with list of available national programs for the provided risk.
     *
     * @param inputRecord   input records that contains key information
     * @param loadProcessor record load processor
     * @return RecordSet a RecordSet loaded with list of available national programs.
     */
    public RecordSet loadAllNationalProgram(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllNationalProgram", new Object[]{inputRecord});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        // Execute query
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_National_Program.Sel_National_Program", mapping);
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load national program list", e);
            l.throwing(getClass().getName(), "loadAllNationalProgram", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllNationalProgram", rs);
        }
        return rs;
    }

    /**
     * Save all data in National Program page
     *
     * @param inputRecords a record set with data to be saved
     */
    public void saveAllNationalProgram(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllNationalProgram", new Object[]{inputRecords});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_National_Program.Save_National_Program");
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save national program.", e);
            l.throwing(getClass().getName(), "saveAllNationalProgram", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllNationalProgram");
        }
    }

    /**
     * To calculate risk dates for national program.
     * <p/>
     *
     * @param inputRecord input record that contains key information
     * @return Record with risk effective from date and effective to date.
     */
    public Record calculateRiskDatesForNationalProgram(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "calculateRiskDatesForNationalProgram", new Object[]{inputRecord});

        Record record;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("allowDateChangeB", "dateChangeAllowedB"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_National_Program.Calculate_Dates", mapping);
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to calculate risk dates for national program", e);
            l.throwing(getClass().getName(), "calculateRiskDatesForNationalProgram", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "calculateRiskDatesForNationalProgram", record);
        return record;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public NationalProgramJdbcDAO() {
    }
}