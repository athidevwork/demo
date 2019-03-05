package dti.pm.acctlookupmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class provides the implementation details of DAO operations for the Account Lookup Manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
 *
 * @author sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AccountLookupJdbcDAO extends BaseDAO implements AccountLookupDAO {

    /**
     * Method that returns a list of billing accounts.
     * <p/>
     *
     * @param inputRecord Record contains input values
     * @return Record containing the billing accounts based on the input criteria
     */
    public RecordSet loadAllAccount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAccount", new Object[]{inputRecord});
        }

        // get the return value
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Account_Lookup.Sel_Billing_Account");
        RecordSet outputRecordSet;
        try {
            outputRecordSet = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Web_Account_Lookup.Sel_Billing_Account.", e);
            l.throwing(getClass().getName(), "loadAllAccount", ae);
            throw ae;
        }

        // done
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAccount", outputRecordSet);
        }
        return outputRecordSet;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public AccountLookupJdbcDAO() {
    }
}
