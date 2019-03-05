package dti.pm.riskmgr.insuredmgr.dao;

import java.sql.SQLException;
import java.util.logging.Logger;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;
import dti.pm.riskmgr.RiskHeader;

/**
 * <p>(C) 2015 Delphi Technology, inc. (dti)</p>
 * Date:   April 08, 2015
 *
 * @author wdang
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 04/08/2015    wdang      157211 - Initial version, Maintain Insured Tracking Information.
 * ---------------------------------------------------
 */
public class InsuredTrackingJdbcDAO extends BaseDAO implements InsuredTrackingDAO {

    @Override
    public RecordSet loadAllInsuredTracking(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllInsuredTracking", new Object[]{inputRecord});

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Insured_Tracking.Select_Insured_Tracking");
            rs = spDao.execute(inputRecord, loadProcessor);

            l.exiting(getClass().getName(), "loadAllInsuredTracking", rs);
            return rs;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load insured tracking", e);
            l.throwing(getClass().getName(), "loadAllTaxHeader", ae);
            throw ae;
        }
    }

    @Override
    public void insertAllInsuredTracking(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "insertAllInsuredTracking", new Object[]{inputRecords});

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Insured_Tracking.Insert_Insured_Tracking");
            spDao.executeBatch(inputRecords);
            
            l.exiting(getClass().getName(), "insertAllInsuredTracking");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to insert insured tracking", e);
            l.throwing(getClass().getName(), "insertAllInsuredTracking", ae);
            throw ae;
        }
    }
    
    @Override
    public void updateAllInsuredTracking(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllInsuredTracking", new Object[]{inputRecords});

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Insured_Tracking.Update_Insured_Tracking");
            spDao.executeBatch(inputRecords);
            
            l.exiting(getClass().getName(), "updateAllInsuredTracking");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to update insured tracking", e);
            l.throwing(getClass().getName(), "updateAllInsuredTracking", ae);
            throw ae;
        }
    }
    
    @Override
    public void deleteAllInsuredTracking(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllInsuredTracking", new Object[]{inputRecords});

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Insured_Tracking.Delete_Insured_Tracking");
            spDao.executeBatch(inputRecords);
            
            l.exiting(getClass().getName(), "deleteAllInsuredTracking");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete insured tracking", e);
            l.throwing(getClass().getName(), "deleteAllInsuredTracking", ae);
            throw ae;
        }
    }

    @Override
    public Record validateAllInsuredTracking(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateAllInsuredTracking", new Object[]{inputRecord});

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Insured_Tracking.Validate_Insured_Tracking");
            Record output = spDao.execute(inputRecord).getSummaryRecord();

            l.exiting(getClass().getName(), "validateAllInsuredTracking");
            return output;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate insured tracking", e);
            l.throwing(getClass().getName(), "validateAllInsuredTracking", ae);
            throw ae;
        }
    }
}
