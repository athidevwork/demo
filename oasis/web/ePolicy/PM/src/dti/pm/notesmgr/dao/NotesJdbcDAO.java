package dti.pm.notesmgr.dao;

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
 * This class implements the NotesDAO.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 24, 2008
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/24/2008       Bhong       Initial version.
 * 03/28/2014       xnie        Modified loadAllPartTimeNotes to remove mapping for occupant.
 * ---------------------------------------------------
 */
public class NotesJdbcDAO extends BaseDAO implements NotesDAO {
    /**
     * Load all part time notes
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPartTimeNotes(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPartTimeNotes", new Object[]{inputRecord, recordLoadProcessor});
        }

        RecordSet rs;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyNo", "policyNumber"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_web_Notes.Sel_Part_Time_Notes", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load part time notes.", e);
            l.throwing(getClass().getName(), "loadAllPartTimeNotes", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPartTimeNotes", rs);
        }
        return rs;
    }

    /**
     * Save all changes in part time notes
     *
     * @param inputRecords
     * @return int
     */
    public int saveAllPartTimeNotes(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPartTimeNotes", new Object[]{inputRecords});
        }

        int updateCount;
        // Insert the records in batch mode
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Notes.Save_Part_Time_Notes");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save part time notes.", e);
            l.throwing(getClass().getName(), "saveAllPartTimeNotes", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPartTimeNotes", new Integer(updateCount));
        }
        return updateCount;
    }
}
