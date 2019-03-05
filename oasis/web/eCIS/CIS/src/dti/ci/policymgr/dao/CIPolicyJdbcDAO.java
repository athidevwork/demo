package dti.ci.policymgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO contains all policy associted call in eCIS.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 04, 2009
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
public class CIPolicyJdbcDAO extends BaseDAO implements CIPolicyDAO {
    /**
     * Load all locked policy
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllLockedPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllLockedPolicy", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Cs_Ci_Check_Locked_Policies");
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllLockedPolicy", rs);
            }
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllLockedPolicy", rs);
            }
            return rs;

        }

        catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load locked policy", e);
            l.throwing(getClass().getName(), "loadAllLockedPolicy", ae);
            throw ae;
        }
    }
}
