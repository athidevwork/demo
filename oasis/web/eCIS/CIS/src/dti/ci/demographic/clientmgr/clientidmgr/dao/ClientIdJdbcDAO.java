package dti.ci.demographic.clientmgr.clientidmgr.dao;

import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.util.LogUtils;
import dti.ci.core.dao.BaseDAO;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/24/2008       kshen       Removed class prefix "CI".
 * ---------------------------------------------------
 */
public class ClientIdJdbcDAO extends BaseDAO implements ClientIdDAO {
    public RecordSet loadAllClientIds(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllClientIds", new Object[]{record, recordLoadProcessor});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_client_id.sel_client_id");
            RecordSet rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllClientIds", rs);
            }
            return rs;

        } catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load client id information", e);
            l.throwing(getClass().getName(), "loadAllClientIds", ae);
            throw ae;
        }
    }

    public int addAllClientIds(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllClientIds", new Object[]{inputRecords});

        int updateCount = 0;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_client_id.save_client_id");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save inserted client ids.", e);
            l.throwing(getClass().getName(), "addAllClientIds", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllClientIds", new Integer(updateCount));
        }
        return updateCount;
    }

    public int updateAllClientIds(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllClientIds", new Object[]{inputRecords});

        int updateCount = 0;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_client_id.save_client_id");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save updated client ids.", e);
            l.throwing(getClass().getName(), "updateAllClientIds", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllClientIds", new Integer(updateCount));
        }
        return updateCount;
    }

    public int deleteAllClientIds(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllClientIds", new Object[]{inputRecords});

        int updateCount = 0;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_client_id.delete_client_id");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete client ids.", e);
            l.throwing(getClass().getName(), "deleteAllClientIds", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllClientIds", new Integer(updateCount));
        }
        return updateCount;
    }

    public ClientIdJdbcDAO() {
    }
}
