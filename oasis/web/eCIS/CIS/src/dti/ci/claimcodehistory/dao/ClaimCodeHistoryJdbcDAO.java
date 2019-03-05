package dti.ci.claimcodehistory.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;





/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 5, 2009
 *
 * @author msnadar
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ClaimCodeHistoryJdbcDAO extends BaseDAO implements ClaimCodeHistoryDAO {

    /**
     * Get Claim Code History
     *
     * @param inputRecord Source Table and FK
     * @return  The CLaim Code History
     */
    public RecordSet getClaimCodeHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getClaimCodeHistory", new Object[]{inputRecord});
        }
        RecordSet rs = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_cm_claim_code_history.get_code_history");
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get claim code history records.", e);
            l.throwing(getClass().getName(), "getClaimCodeHistory", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getClaimCodeHistory", rs);
        }

        return rs;
    }

//    public ClaimCodeHistoryJdbcDAO() {
//    }
}
