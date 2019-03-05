package dti.ci.entitymgr.service.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/20/2016
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class PartyChangeServiceJdbcDAO implements PartyChangeServiceDAO {

    @Override
    public void setHubOrigin(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setHubOrigin", new Object[]{record});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ows_notification.set_h_origin");
        try {
            spDao.executeUpdate(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to set hub origin: \n" + e.getMessage(), e);
            l.throwing(getClass().getName(), "setHubOrigin", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setHubOrigin");
        }
    }
}
