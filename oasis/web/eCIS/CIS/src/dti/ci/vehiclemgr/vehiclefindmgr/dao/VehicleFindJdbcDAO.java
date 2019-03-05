package dti.ci.vehiclemgr.vehiclefindmgr.dao;

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
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Jan 12, 2011
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class VehicleFindJdbcDAO implements VehicleFindDAO {
    /**
     * Load Entity Vehicle List.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public RecordSet loadEntityVehicleList(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "loadEntityVehicleList";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{loadProcessor});
        }
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Vehicle.Sel_Vehicle_List");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all Entity Vehicle List.", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
        }
}
