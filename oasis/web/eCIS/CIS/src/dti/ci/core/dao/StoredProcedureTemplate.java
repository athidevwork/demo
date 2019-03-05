package dti.ci.core.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.ci.core.error.PersistenceException;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * The template classes to excute sotred procedure.
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 18, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/02/2008       kshen       Added method doUpdate,
 *                              Updated method doBatchUpdate.
 * ---------------------------------------------------
 */
public class StoredProcedureTemplate {
    private static final Class CLAZZ = StoredProcedureTemplate.class;

    private StoredProcedureTemplate() {
    }

    /**
     * Do batch update.
     *
     * @param storedProcedure
     * @param inputRecords
     * @return
     */
    public static int doBatchUpdate(String storedProcedure, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(CLAZZ);

        if (l.isLoggable(Level.FINER)) {
            l.entering(CLAZZ.getName(), "doBatchUpdate", new Object[]{storedProcedure, inputRecords});
        }

        int updateCount = 0;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(storedProcedure);
            updateCount = spDao.executeBatch(inputRecords);
        } catch (SQLException e) {
            handleSQLException(e);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(CLAZZ.getName(), "doBatchUpdate");
        }
        return updateCount;
    }

    public static void doUpdate(String storedProcedure, Record inputRecord) {
        Logger l = LogUtils.getLogger(CLAZZ);

        if (l.isLoggable(Level.FINER)) {
            l.entering(CLAZZ.getName(), "doUpdate", new Object[]{storedProcedure, inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(storedProcedure);
            spDao.executeUpdate(inputRecord);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(CLAZZ.getName(), "doBatchUpdate");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    protected static void handleSQLException(SQLException e) {
        Logger l = LogUtils.getLogger(CLAZZ);
        String msg = null;
        // Check sql error.
        try {
            // Check exception for Oracle specific errors.
            msg = checkException(e);
        } catch (Exception e1) {
            // It's an unexpected error.
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to do batch update.", e1);
            l.throwing(CLAZZ.getName(), "doBatchUpdate", ae);
            throw ae;
        }
        PersistenceException pe = new PersistenceException(
                "cm.persistence.error",
                "Unable to do batch update",
                new Object[]{msg},
                e);
        throw pe;
    }

    /**
     * Checks exception for Oracle specific things & returns more friendly message.
     *
     * @param e Exception object.
     * @param e Exception object.
     * @return String - More friendly error msg if an ORA-20xxx error.
     * @throws Exception
     */
    public static String checkException(Exception e) throws Exception {
        return checkException(e, true);
    }

    /**
     * Checks exception for Oracle specific things & returns more friendly message.
     *
     * @param e      Exception object.
     * @param logMsg Write the message to the log?
     * @return String - More friendly error msg if an ORA-20xxx error.
     * @throws Exception
     */
    public static String checkException(Exception e, boolean logMsg) throws Exception {
        Logger l = LogUtils.enterLog(CLAZZ, "checkException",
                new Object[]{e, new Boolean(logMsg)});
        if (e instanceof AppException) {
            if (e.getCause() instanceof SQLException) {
                e = (SQLException) e.getCause();
            } else {
                throw e;
            }
        }

        if (e instanceof SQLException) {
            int pos = e.getMessage().lastIndexOf("ORA-20");
            if (pos > -1) {
                int pos1 = e.getMessage().indexOf("ORA-", pos + 1);
                String msg = (pos1 < 0) ? e.getMessage().substring(pos + 11) :
                        e.getMessage().substring(pos + 11, pos1 - 1);
                if (pos1 > -1) {
                    if (logMsg) {
                        l.warning(new StringBuffer("***Reporting error to user:\n").
                                append(msg).append("\nFull Exception Details:\n").
                                append(e.getMessage()).toString());
                    }
                }

                l.exiting(CLAZZ.getName(), "checkException", msg);
                return msg.replaceAll(",", "&#44;");
            }
        }
        throw e;
    }
}
