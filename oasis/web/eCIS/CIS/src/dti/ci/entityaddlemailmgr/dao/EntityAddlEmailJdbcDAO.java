package dti.ci.entityaddlemailmgr.dao;

import dti.ci.core.dao.BaseDAO;
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
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/11/13
 *
 * @author bzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityAddlEmailJdbcDAO extends BaseDAO implements EntityAddlEmailDAO {

    /**
     * Load the entity additional email list.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadEntityAddlEmailList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityAddlEmailList", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Cs_Web_Electronic_Distribution.Load_Entity_Addlemail_List");

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadEntityAddlEmailList", rs);
            }

            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load entity additional email list.", e);
            l.throwing(getClass().getName(), "loadEntityAddlEmailList", ae);
            throw ae;
        }
    }

    /**
     * Delete the entity additional email.
     *
     * @param inputRecords
     * @return
     */
    public int deleteEntityAddlEmail(RecordSet inputRecords) {
        String methodName = "deleteEntityAddlEmail";
        Logger l = LogUtils.enterLog(getClass(), methodName, new Object[]{inputRecords});

        int deleteCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Cs_Web_Electronic_Distribution.Delete_Entity_Addlemail");

        try {
            deleteCount = spDao.executeBatch(inputRecords);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, new Integer(deleteCount));
            }

            return deleteCount;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete entity additional email.", e);
            l.throwing(getClass().getName(), "deleteEntityAddlEmail", ae);
            throw ae;
        }
    }

    /**
     * Update the entity additional email list.
     *
     * @param inputRecords
     * @return
     */
    public int updateEntityAddlEmail(RecordSet inputRecords) {
        String methodName = "updateEntityAddlEmail";
        Logger l = LogUtils.enterLog(getClass(), methodName, new Object[]{inputRecords});

        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Cs_Web_Electronic_Distribution.Update_Entity_AddlEmail");

        try {
            updateCount = spDao.executeBatch(inputRecords);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, new Integer(updateCount));
            }

            return updateCount;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update entity additional email.", e);
            l.throwing(getClass().getName(), "updateEntityAddlEmail", ae);
            throw ae;
        }
    }

    /**
     * Insert the entity additional email list.
     *
     * @param inputRecords
     * @return
     */
    public int insertEntityAddlEmail(RecordSet inputRecords) {
        String methodName = "insertEntityAddlEmail";
        Logger l = LogUtils.enterLog(getClass(), methodName, new Object[]{inputRecords});

        int insertCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Cs_Web_Electronic_Distribution.Insert_Entity_Addlemail");

        try {
            insertCount = spDao.executeBatch(inputRecords);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, new Integer(insertCount));
            }

            return insertCount;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to insert entity additional email.", e);
            l.throwing(getClass().getName(), "insertEntityAddlEmail", ae);
            throw ae;
        }
    }
}
