package dti.pm.policymgr.distributionmgr.dao;

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
 * This class implements the DistributionDAO interface.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 11, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/06/2013       xnie        142674 - Added processCatchUp() to catch up dividend.
 * ---------------------------------------------------
 */

public class DistributionJdbcDAO extends BaseDAO implements DistributionDAO {

    /**
     * load all distributions
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllDistribution(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDistribution", null);
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Sel_Dividend_Rule");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load distribution information", e);
            l.throwing(getClass().getName(), "loadAllDistribution", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDistribution", rs);
        }

        return rs;
    }

    /**
     * save/update/delete all changes of distributions
     *
     * @param inputRecords
     * @return
     */
    public void saveAllDistribution(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllDistribution", new Object[]{inputRecords});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Save_Dividend_Rule");
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save distribution.", e);
            l.throwing(getClass().getName(), "saveDistribution", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllDistribution", null);
        }
    }

    /**
     * Process the selected distribution
     *
     * @param inputRecord
     * @return
     */
    public void processDistribution(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processDistribution", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Process_Distribution");
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to process distribution.", e);
            l.throwing(getClass().getName(), "processDistribution", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processDistribution", null);
        }
    }

    /**
     * Catch up dividend
     *
     * @param inputRecord
     * @return
     */
    public void processCatchUp(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processCatchUp", new Object[]{inputRecord});
        }
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dividend_Calc.Process_Catch_Up");
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to catch up dividend.", e);
            l.throwing(getClass().getName(), "processCatchUp", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processCatchUp", null);
        }
    }
}
