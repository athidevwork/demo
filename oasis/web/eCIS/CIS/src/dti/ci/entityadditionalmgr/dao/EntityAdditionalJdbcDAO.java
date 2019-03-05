package dti.ci.entityadditionalmgr.dao;


import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
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
 * This class implements the entityAdditionalDAO interface.
 * This is consumed by any business logic objects that requires information about entityAdditional.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: February 08, 2011
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class EntityAdditionalJdbcDAO extends BaseDAO implements EntityAdditionalDAO {

    /**
     * Get all entityAdditional from DB
     *
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadAllAvailableEntityAdditionals(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableEntityAdditionals", new Object[]{record});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Additional.Sel_Entity_Additional");
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_Additional information", e);
            l.throwing(getClass().getName(), "loadAllAvailableEntityAdditionals", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableEntityAdditionals", rs);
        }
        return rs;
    }

    /**
     * Update changes of entityAdditional to DB
     *
     * @param inputRecords
     * @return
     */
    public int saveAllEntityAdditionals(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllEntityAdditionals", inputRecords);
        }

        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Additional.Save_Entity_Additional");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save  Discount Points His.", e);
            l.throwing(getClass().getName(), "saveAllEntityAdditionals", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllEntityAdditionals", new Integer(updateCount));
        }
        return updateCount;
    }

}
