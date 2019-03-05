package dti.oasis.healthcheckmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 16, 2010
 *
 * @author fcb
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class DataSourceHealthCheckJdbcDAO implements DataSourceHealthCheckDAO{
    /**
     * Method to add check the database connectivity
     * <p/>
     *
     * @param inputRecord that represents the input data.
     * @return String that contains the return value
     */
    public String checkDatabaseConnectivity(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "checkDatabaseConnectivity", new Object[]{inputRecord});

        try {

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Get_Oasis_User");

            Record outputRecord = spDao.executeUpdate(inputRecord);
            String output = outputRecord.getStringValue(spDao.RETURN_VALUE_FIELD);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "checkDatabaseConnectivity", output);
            }

            return output;
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check database: " + inputRecord, e);
            l.throwing(getClass().getName(), "checkDatabaseConnectivity", ae);
            throw ae;
        }
    }
}
