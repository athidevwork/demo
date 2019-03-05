package dti.oasis.accesstrailmgr.dao;

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
 * Date:   Aug 22, 2014
 *
 * @author Parker Xu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 *
 * ---------------------------------------------------
 */
public class OwsAccessTrailJdbcDAO implements OwsAccessTrailDAO {

    /**
     * add the ows logger information to the database.
     *
     * @param inputRecord
     */
    public Record addOwsAccessTrail(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addOwsAccessTrail", new Object[]{inputRecord});
        }

        Record result = new Record();
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(PROC_ADD_OWS_ACCESS_TRAIL);
            spDao.executeUpdate(inputRecord);

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save access trail into " + PROC_ADD_OWS_ACCESS_TRAIL, e);
            l.throwing(getClass().getName(), "addOwsAccessTrail", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "addOwsAccessTrail", result);
        return result;
    }

    /**
     * update the response xml and the status code to ows logger.
     *
     * @param inputRecord
     */
    public void updateOwsAccessTrail(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateOwsAccessTrail", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(PROC_UPDATE_OWS_ACCESS_TRAIL);
            spDao.executeUpdate(inputRecord);

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update access trail into " + PROC_UPDATE_OWS_ACCESS_TRAIL, e);
            l.throwing(getClass().getName(), "updateOwsAccessTrail", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "updateOwsAccessTrail");
    }

    /**
     * get the logger enable indicator for the request name.
     *
     * @param inputRecord
     * @return boolean
     */
    @Override
    public String checkTheConfigForRequestName(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkTheConfigForRequestName", new Object[]{inputRecord});
        }
        // get the return value
        String dateStr;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(FUNC_GET_INDICATOR_FOR_REQUESTNAME);
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            dateStr = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute " + FUNC_GET_INDICATOR_FOR_REQUESTNAME + ".", e);
            l.throwing(getClass().getName(), "checkTheConfigForRequestName", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "checkTheConfigForRequestName", dateStr);
        return dateStr;
    }
    private final Logger l = LogUtils.getLogger(getClass());

    private static String PROC_ADD_OWS_ACCESS_TRAIL = "OWS_ACCESS.Add_Access_Trail";
    private static String PROC_UPDATE_OWS_ACCESS_TRAIL = "OWS_ACCESS.Update_Access_Trail";
    private static String FUNC_GET_INDICATOR_FOR_REQUESTNAME = "OWS_ACCESS.Get_Indicator_For_RequestName";
}
