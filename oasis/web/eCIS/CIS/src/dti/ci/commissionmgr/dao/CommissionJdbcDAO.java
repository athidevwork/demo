package dti.ci.commissionmgr.dao;

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
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 24, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * ---------------------------------------------------
 */
public class CommissionJdbcDAO extends BaseDAO implements CommissionDAO {

    /**
     * method to load all commission rate Bracket for a given commRateSchedId
     *
     * @param inputRecord a record containing a commRateSchedId field
     * @return recordset
     */
    public RecordSet loadAllCommissionBracket(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCommissionBracket", new Object[]{inputRecord});

        RecordSet rs = null;

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Commission.Sel_Commission_Bracket");
        try {
            rs = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load commission bracket information", se);
            l.throwing(getClass().getName(), "loadAllCommissionBracket", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCommissionBracket", rs);
        }
        return rs;
    }
}
