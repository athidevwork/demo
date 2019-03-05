package dti.pm.policymgr.regionalmgr.dao;

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
 * This class implements the RegionalTeamDAO interface.
 * This is consumed by any business logic objects that requires information about regional teams.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 19, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/13/2014        awu        148783 - Modified loadAllRegionalTeam to add record load processor.
 * ---------------------------------------------------
 */
public class RegionalTeamJdbcDAO extends BaseDAO implements RegionalTeamDAO {

    /**
     * Returns a RecordSet loaded with list of regional team.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRegionalTeam(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRegionalTeam", inputRecord);
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Regional.Sel_Regional_Team");
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load regional teams.", e);
            l.throwing(getClass().getName(), "loadAllRegionalTeam", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRegionalTeam", rs);
        }
        return rs;
    }

    /**
     * Save the regional team.
     * If the indicator rowStatus is 'NEW', system saves this record, 'MODIFIED' for update and 'DELETED' for delete.
     *
     * @param inputRecords
     * @return the number of rows saved.
     */
    public int saveAllRegionalTeam(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveRegionalTeam", inputRecords);
        }

        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Regional.Save_Regional_Team");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save regional team.", e);
            l.throwing(getClass().getName(), "saveRegionalTeam", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveRegionalTeam", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Returns a RecordSet loaded with list of regional team underwriters.
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllTeamUnderwriter(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTeamUnderwriter", new Object[]{inputRecord, recordLoadProcessor});
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Regional.Sel_Team_Underwriter");
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load team underwriter.", e);
            l.throwing(getClass().getName(), "loadAllTeamUnderwriter", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTeamUnderwriter", rs);
        }
        return rs;
    }

    /**
     * Save regional team underwriter.
     * If the indicator rowStatus is 'NEW', system saves this record, 'MODIFIED' for update and 'DELETED' for delete.
     *
     * @param inputRecords
     * @return the number of rows saved.
     */
    public int saveAllTeamUnderwriter(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveTeamUnderwriter", inputRecords);
        }

        int updateCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("underwriterSqId", "underwriterSequenceId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Regional.Save_Team_Underwriter", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save team underwriter.", e);
            l.throwing(getClass().getName(), "saveTeamUnderwriter", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveTeamUnderwriter", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Get underwriter Id when the administrator selects the team member name.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet getUnderwriterId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUnderwriterId", inputRecord);
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Regional.Sel_Underwriter_Id");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to select underwriter id.", e);
            l.throwing(getClass().getName(), "getUnderwriterId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getUnderwriterId", rs);
        }
        return rs;
    }
}
