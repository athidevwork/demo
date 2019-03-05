package dti.ci.riskmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * Jdbc Inplementation of RiskDAO.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 18, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/22/2015       bzhu        Issue 156487 - retrieve accumulated discount point.
 * ---------------------------------------------------
 */

public class RiskJdbcDAO extends BaseDAO implements RiskDAO {
    /**
     * Get the current risk management discount for an entity.
     *
     * @param inputRecord the information of an entity.
     * @return The current risk management discount information of the entity.
     */
    public Record getCurrentRiskManagementDiscount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCurrentRiskManagementDiscount", new Object[]{inputRecord});
        }

        Record record = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Wb_Ci_Risk.Sel_Current_Rm_Discount_Desrc");
            RecordSet rs = spDao.execute(inputRecord);
            if (rs != null && rs.getSize() == 1) {
                record = rs.getRecord(0);
            }
        } catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get current risk management discount", e);
            l.throwing(getClass().getName(), "getCurrentRiskManagementDiscount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCurrentRiskManagementDiscount", record);
        }
        return record;
    }

    /**
     * Get the current mandate window period information for an entity.
     *
     * @param inputRecord The information of an entity.
     * @return The current mandate window period informantion of the entity.
     */
    public Record getCurrentMandateWindowPeriod(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCurrentMandateWindowPeriod", new Object[]{inputRecord});
        }

        Record record = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Wb_Ci_Risk.Sel_Window_Period_Desrc");
            RecordSet rs = spDao.execute(inputRecord);
            if (rs != null && rs.getSize() == 1) {
                record = rs.getRecord(0);
            }
        } catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to Get current Manadate Window Period", e);
            l.throwing(getClass().getName(), "getCurrentMandateWindowPeriod", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCurrentMandateWindowPeriod", record);
        }

        return record;
    }

    /**
     * Get the program history information for an entity.
     *
     * @param inputRecord The entity information.
     * @return The program history information of the entity.
     */
    public RecordSet getProgramHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProgramHistory", new Object[]{inputRecord});
        }

        RecordSet rs = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Wb_Ci_Risk.Sel_Program_History");
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load program history records.", e);
            l.throwing(getClass().getName(), "getProgramHistory", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProgramHistory", rs);
        }

        return rs;
    }

    /**
     * Get the window period history information for an entity.
     *
     * @param inputRecord The entity information.
     * @return The window period history information of the entity.
     */
    public RecordSet getWindowPeriodHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWindowPeriodHistory", new Object[]{inputRecord});
        }

        RecordSet rs = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Wb_Ci_Risk.Sel_Window_period_History");
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load window period history records.", e);
            l.throwing(getClass().getName(), "getWindowPeriodHistory", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getWindowPeriodHistory", rs);
        }

        return rs;
    }

    /**
     * Get the Additional Risk Management Discount for an entity.
     *
     * @param inputRecord The entity.
     * @return The Additional Risk Manangement Discount information of the entity.
     */
    public RecordSet getAdditionalRiskManagementDiscount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAdditionalRiskManagementDiscount", new Object[]{inputRecord});
        }
        RecordSet rs = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Wb_Ci_Risk.Sel_Additional_Rm_Discount");
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load additional rm discount records.", e);
            l.throwing(getClass().getName(), "getAdditionalRiskManagementDiscount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAdditionalRiskManagementDiscount", rs);
        }

        return rs;
    }

    /**
     * Get ERS Point History for an entity.
     *
     * @param inputRecord The entity information.
     * @return The ERS Point History information of the entity.
     */
    public RecordSet getErsPointHistory(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getErsPointHistory", new Object[]{inputRecord});
        }
        RecordSet rs = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Wb_Ci_Risk.Sel_Ers_Point_History");
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        } catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to ERS Point History records.", e);
            l.throwing(getClass().getName(), "getErsPointHistory", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getErsPointHistory", rs);
        }

        return rs;
    }

    /**
     * Get Accumulated Discount Point for an entity.
     *
     * @param inputRecord The entity.
     * @return The accumulated discount point information of the entity.
     */
    public Record getAccumulatedDiscountPoint(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAccumulatedDiscountPoint", new Object[]{inputRecord});
        }

        Record record = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Wb_Ci_Risk.Sel_Accumulated_Discount_Point");
            RecordSet rs = spDao.execute(inputRecord);
            if (rs != null && rs.getSize() == 1) {
                record = rs.getRecord(0);
            }
        } catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to accumulated discount point record.", e);
            l.throwing(getClass().getName(), "getAccumulatedDiscountPoint", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAccumulatedDiscountPoint", record);
        }

        return record;
    }

    public RiskJdbcDAO() {
    }
}
