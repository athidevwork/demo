package dti.ci.amalgamationmgr.dao;

import dti.cs.data.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the AmalgamationDAO interface.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AmalgamationJdbcDAO extends BaseDAO implements AmalgamationDAO {

    /**
     * Method to load all amalgamation
     *
     * @param inputRecord         a record containing entity information
     * @param recordLoadProcessor
     * @return RecordSet resultset containing amalgamation information
     */
    public RecordSet loadAllAmalgamation(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAmalgamation", new Object[]{inputRecord, recordLoadProcessor});
        }
        RecordSet rs;
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Amalgamation.Sel_All_Amalgamation");
        try {
            rs = sp.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when load all amalgamation.", se);
            l.throwing(getClass().getName(), "loadAllAmalgamation", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAmalgamation", rs);
        }
        return rs;
    }

    /**
     * Method to save all amalgamation information
     *
     * @param inputRecords
     * @return int
     */
    public int saveAllAmalgamation(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAmalgamation", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Amalgamation.Save_All_Amalgamation");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save all amalgamation).", e);
            l.throwing(getClass().getName(), "saveAllAmalgamation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAmalgamation", new Integer(updateCount));
        }
        return updateCount;
    }

}
