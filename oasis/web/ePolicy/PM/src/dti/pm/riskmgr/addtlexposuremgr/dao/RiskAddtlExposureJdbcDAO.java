package dti.pm.riskmgr.addtlexposuremgr.dao;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;
import dti.pm.riskmgr.addtlexposuremgr.RiskAddtlExposureFields;

/**
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * Date:   May 23, 2017
 *
 * @author eyin
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 09/21/2017    eyin       169483, Initial version.
 * 11/21/2017    lzhang     189820  Add getRiskAddtlBaseId method.
 * 12/27/2017    eyin       190491  1) Added isOoseChangeDateAllowed.
 * ---------------------------------------------------
 */
public class RiskAddtlExposureJdbcDAO extends BaseDAO implements RiskAddtlExposureDAO {

    /**
     * Returns a RecordSet loaded with list of available Additional Exposure for the provided risk.
     *
     * @param inputRecord   input records that contains key information
     * @param loadProcessor record load processor
     * @return              RecordSet a RecordSet loaded with list of available Additional Exposure.
     */
    public RecordSet loadAllRiskAddtlExposure(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskAddtlExposure", new Object[]{inputRecord});

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Risk_Addtl_Exposure.Select_Risk_Addtl_Exposure");
            rs = spDao.execute(inputRecord, loadProcessor);

            l.exiting(getClass().getName(), "loadAllRiskAddtlExposure", rs);
            return rs;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Additional Exposure", e);
            l.throwing(getClass().getName(), "loadAllRiskAddtlExposure", ae);
            throw ae;
        }
    }

    /**
     * Returns a RecordSet loaded with list of Primary Practice for the provided risk.
     *
     * @param inputRecord   input records that contains key information
     * @return              RecordSet a RecordSet loaded with list of Primary Practice
     */
    public RecordSet loadPrimaryPractice(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadPrimaryPractice", new Object[]{inputRecord});

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Risk_Addtl_Exposure.Select_Primary_Practice");
            rs = spDao.execute(inputRecord);

            l.exiting(getClass().getName(), "loadPrimaryPractice", rs);
            return rs;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Primary Practice", e);
            l.throwing(getClass().getName(), "loadPrimaryPractice", ae);
            throw ae;
        }
    }

    /**
     * Returns a RecordSet loaded with list of available Additional Exposure for the provided policy term.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllRiskAddtlExposure(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskAddtlExposure", new Object[]{inputRecord});

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Risk_Addtl_Exposure.Select_Risk_Addtl_Exposure");
            RecordSet rs = spDao.execute(inputRecord);

            l.exiting(getClass().getName(), "loadAllRiskAddtlExposure", rs);
            return rs;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Additional Exposure", e);
            l.throwing(getClass().getName(), "loadAllRiskAddtlExposure", ae);
            throw ae;
        }
    }

    /**
     * Insert data in Additional Exposure page
     *
     * @param inputRecords a record set with data to be inserted.
     */
    public int insertAllRiskAddtlExposure(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "insertAllRiskAddtlExposure", new Object[]{inputRecords});

        int updateCount = 0;

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("addtlExpBaseRecordId", "riskAddtlExpBaseRecordId"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Risk_Addtl_Exposure.Insert_Risk_Addtl_Exposure", mapping);
            updateCount += spDao.executeBatch(inputRecords);
            
            l.exiting(getClass().getName(), "insertAllRiskAddtlExposure");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to insert Additional Exposure", e);
            l.throwing(getClass().getName(), "insertAllRiskAddtlExposure",  ae);
            throw ae;
        }

        return updateCount;
    }

    /**
     * Update data in Additional Exposure page
     *
     * @param inputRecords a record set with data to be updated.
     *
     */
    public int updateAllRiskAddtlExposure(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllRiskAddtlExposure", new Object[]{inputRecords});

        int updateCount = 0;

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("addtlExpBaseRecordId", "riskAddtlExpBaseRecordId"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Risk_Addtl_Exposure.Update_Risk_Addtl_Exposure", mapping);
            updateCount += spDao.executeBatch(inputRecords);
            
            l.exiting(getClass().getName(), "updateAllRiskAddtlExposure");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to update Additional Exposure", e);
            l.throwing(getClass().getName(), "updateAllRiskAddtlExposure", ae);
            throw ae;
        }

        return updateCount;
    }

    /**
     * Delete data in Additional Exposure page
     *
     * @param inputRecords a record set with data to be deleted.
     */
    public int deleteAllRiskAddtlExposure(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllRiskAddtlExposure", new Object[]{inputRecords});

        int updateCount = 0;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Risk_Addtl_Exposure.Delete_Risk_Addtl_Exposure");
            updateCount += spDao.executeBatch(inputRecords);
            
            l.exiting(getClass().getName(), "deleteAllRiskAddtlExposure");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete Additional Exposure", e);
            l.throwing(getClass().getName(), "deleteAllRiskAddtlExposure", ae);
            throw ae;
        }

        return updateCount;
    }

    /**
     * Validate Risk Additional Practice Duplication
     * @param inputRecord
     * @return
     */
    public Record validateRiskAddtlPracticeDuplicate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateRiskAddtlPracticeDuplicate", new Object[]{inputRecord});

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Data.New_Addtl_Practice_Duplicate");
        RecordSet rs = null;
        Record outputRec = null;
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate risk additional practice duplication.", e);
            l.throwing(getClass().getName(), "validateRiskAddtlPracticeDuplicate", ae);
            throw ae;
        }
        if (rs != null) {
            outputRec = rs.getSummaryRecord();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateRiskAddtlPracticeDuplicate", outputRec);
        }
        return outputRec;
    }

    /**
     * Get risk additional base record fk
     * @param inputRecord
     * @return
     */
    public String getRiskAddtlBaseId(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskAddtlBaseId", new Object[]{inputRecord});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBase", "riskBaseRecordId"));

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Base_Fk.Get_Risk_Addtl_Exp_Base_Fk", mapping);
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue("baseId");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get Risk Addtl base id ", e);
            l.throwing(getClass().getName(), "getRiskAddtlBaseId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskAddtlBaseId", returnValue);
        }
        return returnValue;
    }

    /**
     * Check if changing exposure expiring date in OOSE is allowed
     *
     * @param inputRecord
     * @return
     */
    public String isOoseChangeDateAllowed(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOoseChangeDateAllowed", new Object[]{inputRecord});
        }
        String isOoseChangeDateAllowed;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseId", RiskAddtlExposureFields.RISK_ADDTL_EXP_BASE_RECORD_ID));
        mapping.addFieldMapping(new DataRecordFieldMapping("changedDate", RiskAddtlExposureFields.EFFECTIVE_TO_DATE));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Risk_Addtl_Exposure.Is_OOSE_Change_Date_Allowed", mapping);
        try {
            isOoseChangeDateAllowed = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get isOoseChangeDateAllowed.", e);
            l.throwing(getClass().getName(), "isOoseChangeDateAllowed", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOoseChangeDateAllowed", isOoseChangeDateAllowed);
        }
        return isOoseChangeDateAllowed;
    }
}
