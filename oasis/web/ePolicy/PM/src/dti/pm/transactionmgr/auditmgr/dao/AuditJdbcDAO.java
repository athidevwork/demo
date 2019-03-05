package dti.pm.transactionmgr.auditmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC dao for audit
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 24, 2007
 *
 * @author rlli
 */

public class AuditJdbcDAO extends BaseDAO implements AuditDAO {

    /**
     * Retrieves all audit records.
     *
     * @param record input record
     * @return recordSet
     */
    public RecordSet loadAllAudit(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAudit", new Object[]{record});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_TRANSACTION.Sel_Audit_Info");
            rs = spDao.execute(record);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllAudit", rs);
            }
            return rs;
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load audit information", e);
            l.throwing(getClass().getName(), "loadAllAudit", ae);
            throw ae;
        }
    }

}
