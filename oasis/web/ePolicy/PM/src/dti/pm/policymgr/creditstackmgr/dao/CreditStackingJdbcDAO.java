package dti.pm.policymgr.creditstackmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details of all DAO operations that are performed against the CreditStackingManager.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 26, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CreditStackingJdbcDAO extends BaseDAO implements CreditStackingDAO {

    /**
     * Retrieve header information.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllHeaderInformation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllHeaderInformation");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Custom.CRST_Hdr_Rules");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get header information.", e);
            l.throwing(getClass().getName(), "loadAllHeaderInformation", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllHeaderInformation", rs);
        }
        return rs;
    }

    /**
     * Retrieve applied information.
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllAppliedInformation(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAppliedInformation");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Custom.CRST_Dtl_Comps");
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get applied information.", e);
            l.throwing(getClass().getName(), "loadAllAppliedInformation", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAppliedInformation", rs);
        }
        return rs;
    }

}
