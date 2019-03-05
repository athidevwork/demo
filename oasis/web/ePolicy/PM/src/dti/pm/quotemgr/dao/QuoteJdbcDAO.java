package dti.pm.quotemgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC DAO for quote
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   April 27, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/26/2016       wdang       167534 - Initial version.
 * ---------------------------------------------------
 */
public class QuoteJdbcDAO extends BaseDAO implements QuoteDAO {

    @Override
    public RecordSet loadQuoteVersions(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadQuoteVersions", new Object[]{inputRecord});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quote.Get_Quote_Versions");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get quote versions.", e);
            l.throwing(getClass().getName(), "loadQuoteVersions", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadQuoteVersions", rs);
        }
        return rs;
    }

    @Override
    public void performCopy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performCopy", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renewal_Quote.Copy");
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to copy.", e);
            l.throwing(getClass().getName(), "performCopy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performCopy");
        }
    }

    @Override
    public void performTransfer(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performTransfer", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renewal_Quote.Transfer");
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to transfer changes.", e);
            l.throwing(getClass().getName(), "performTransfer", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performTransfer");
        }
    }

    @Override
    public void performApply(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performApply", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renewal_Quote.Apply");
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to apply changes.", e);
            l.throwing(getClass().getName(), "performApply", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performApply");
        }
    }


    @Override
    public void performMerge(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performMerge", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renewal_Quote.Mrge");
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to transfer changes.", e);
            l.throwing(getClass().getName(), "performMerge", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performMerge");
        }
    }
}
