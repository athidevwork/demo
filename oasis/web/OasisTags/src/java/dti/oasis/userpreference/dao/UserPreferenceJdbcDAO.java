package dti.oasis.userpreference.dao;

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
 * This class provides the implementation details for UserPreferenceDAO.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:  Apr 10, 2008
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

public class UserPreferenceJdbcDAO implements UserPreferenceDAO{

     /**
     * Get user preference based on given preference code
     *
     * @param inputRecord Record contains input values
     * @return String containing the user preference
     */
    public String getUserPreference(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUserPreference", new Object[]{inputRecord});
        }
        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Oasis_Utility.User_Pref");
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute Oasis_Utility.User_Pref.", e);
            l.throwing(getClass().getName(), "getUserPreference", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getUserPreference", returnValue);
        return returnValue;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
