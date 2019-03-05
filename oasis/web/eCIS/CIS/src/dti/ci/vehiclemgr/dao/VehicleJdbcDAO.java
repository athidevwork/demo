package dti.ci.vehiclemgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.*;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * DAO for Vehicle
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date: Jun 26, 2006
 *
 * @author gjli
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * 10/13/2010       tzhao       Issue109875:Removed money format in the getVehicleListSQL method. The money will be formatted in the OasisGrid tag.
 * ---------------------------------------------------
*/
public class VehicleJdbcDAO extends BaseDAO implements VehicleDAO{
    /**
     * Load all vehicles of an entity.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    @Override
    public RecordSet loadAllVehicle(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllVehicle", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Vehicle.Sel_All_Vehicle");

        try {
            RecordSet rs = spDao.execute(inputRecord, loadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllVehicle", rs);
            }

            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load vehicle list.", e);
            l.throwing(getClass().getName(), "loadAllVehicle", ae);
            throw ae;
        }
    }

    /**
     * Save all vehicles of an entity.
     *
     * @param inputRecordSet
     * @return
     */
    @Override
    public int saveAllVehicle(RecordSet inputRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllVehicle", new Object[]{inputRecordSet});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Vehicle.Save_Vehicle");

        try {
            int count = spDao.executeBatch(inputRecordSet);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveAllVehicle", Integer.valueOf(count));
            }

            return count;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save vehicle.", e);
            l.throwing(getClass().getName(), "saveAllVehicle", ae);
            throw ae;
        }
     }
}
