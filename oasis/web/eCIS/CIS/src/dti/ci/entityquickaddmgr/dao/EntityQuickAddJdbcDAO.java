package dti.ci.entityquickaddmgr.dao;

import dti.ci.core.error.ExpMsgConvertor;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DAO component of Quick Add Person.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  08/15/2016
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityQuickAddJdbcDAO implements EntityQuickAddDAO {
    /**
     * Save Entity.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveEntity(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntity", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_WEB_QUICK_ADD.ADD_ENTITY");
        Record record = null;
        try {
            record = spDAO.executeUpdate(inputRecord,false);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveEntity", record);
            }
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "saveEntity", e);
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "ci.generic.error", e);
            throw ae;
        }
        return record;
    }

    /**
     * Save Entity Class.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveClass(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntity", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_WEB_QUICK_ADD.ADD_CLASS");
        Record record = null;
        try {
            record = spDAO.executeUpdate(inputRecord,false);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveClass", record);
            }
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "saveClass", e);
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "ci.generic.error", e);
            throw ae;
        }
        return record;
    }

    /**
     * Save Entity Address.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveAddress(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAddress", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_WEB_QUICK_ADD.ADD_ADDRESS");
        Record record = null;
        try {
            record = spDAO.executeUpdate(inputRecord,false);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveAddress", record);
            }
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "saveAddress", e);
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "ci.generic.error", e);
            throw ae;
        }
        return record;
    }

    /**
     * Save Address Phone.
     *
     * @param inputRecord
     * @return Record
     */
    public Record savePhone(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePhone", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_WEB_QUICK_ADD.ADD_PHONE");
        Record record = null;
        try {
            record = spDAO.executeUpdate(inputRecord,false);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "savePhone", record);
            }
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "savePhone", e);
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "ci.generic.error", e);
            throw ae;
        }
        return record;
    }

    /**
     * Save Denominator.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveDenominator(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveDenominator", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_WEB_QUICK_ADD.ADD_DENOMINATOR");
        Record record = null;
        try {
            record = spDAO.executeUpdate(inputRecord,false);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveDenominator", record);
            }
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "saveDenominator", e);
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "ci.generic.error", e);
            throw ae;
        }
        return record;
    }
}
