package dti.pm.coveragemgr.underlyingmgr.dao;

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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC dao for underlying coverage
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 12, 2018
 *
 * @author wrong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------------------------------------------------------------------------
 * 08/24/2018       wrong      188391 - Initial version.
 * ---------------------------------------------------------------------------------------------------------------------
 */
public class UnderlyingCoverageJdbcDAO extends BaseDAO implements UnderlyingCoverageDAO {
    /**
     * load all underlying coverage
     *
     * @param inputRecord         input record cotains all required parameters
     * @param recordLoadProcessor record load processor
     * @return result recordset
     */
    public RecordSet loadAllUnderlyingCoverage(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllUnderlyingCoverage", new Object[]{inputRecord, recordLoadProcessor});
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Underlying_Coverage.Sel_Coverage_Underlying");
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load underlying policy information", e);
            l.throwing(getClass().getName(), "loadAllUnderlyingCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllUnderlyingCoverage", rs);
        }
        return rs;
    }

    /**
     * get initial values for underlying coverage
     *
     * @param inputRecord input record
     * @return record contains all initial values
     */
    public Record getInitialValuesForUnderlyingCoverage(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForUnderlyingCoverage", new Object[]{inputRecord});
        }
        Record resultRecord;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("policyId", "policyUnderPolId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("underCovgBaseId", "policyUnderCovgId"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Underlying_Coverage.Sel_Last_Underlying_Covg_Info", mapping);
            resultRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get initial values for underlying coverage", e);
            l.throwing(getClass().getName(), "getInitialValuesForUnderlyingCoverage", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForUnderlyingCoverage", resultRecord);
        }

        return resultRecord;
    }


    /**
     * insert or update underlying policy coverage
     *
     * @param inputRecords recordset contains inserted/modified records
     * @return processed record count
     */
    public int saveAllUnderlyingCoverage(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllUnderlyingCoverage", new Object[]{inputRecords});
        }

        int processCount;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Underlying_Coverage.Save_Underlying_Coverage", mapping);
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save all underlying coverage", e);
            l.throwing(getClass().getName(), "saveAllUnderlyingCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllUnderlyingCoverage", String.valueOf(processCount));
        }
        return processCount;

    }


    /**
     * delete underlying coverage infos
     *
     * @param inputRecords recordset contains deleted records
     * @return processed record count
     */
    public int deleteAllUnderlyingCoverage(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllUnderlyingCoverage", new Object[]{inputRecords});
        }

        int processCount;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Underlying_Coverage.Del_Underlying_Coverage");
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to delete all underlying coverage", e);
            l.throwing(getClass().getName(), "deleteAllUnderlyingCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllUnderlyingCoverage", String.valueOf(processCount));
        }
        return processCount;
    }

    /**
     * Update underlying coverage infos
     *
     * @param inputRecords recordset contains deleted records
     * @return processed record count
     */
    public int updateAllUnderlyingCoverage(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAllUnderlyingCoverage", new Object[]{inputRecords});
        }

        int processCount;

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Underlying_Coverage.Update_Underlying_Coverage", mapping);
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to delete all underlying coverage", e);
            l.throwing(getClass().getName(), "updateAllUnderlyingCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllUnderlyingCoverage", String.valueOf(processCount));
        }
        return processCount;
    }


    /**
     * load all active policy for select
     *
     * @param inputRecord input record
     * @return recordset of active policies
     */
    public RecordSet loadAllActivePolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllActivePolicy", new Object[]{inputRecord});
        }
        RecordSet rs;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("policyCycle", "policyCycleCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Active_Policy_Risk", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load all active policy", e);
            l.throwing(getClass().getName(), "loadAllActivePolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllActivePolicy", rs);
        }

        return rs;
    }

    /**
     * Overlap validation.
     * @param inputRecord input record
     * @return validate msg
     */
    public String validateUnderlyingOverlap(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        String statusCode;
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateUnderlyingCoverageOverlap", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Underlying_Coverage.Validate_Underlying_Overlap");
            Record output = spDao.executeUpdate(inputRecord);
            statusCode = output.getStringValue("statusCode");
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to validate underlying", e);
            l.throwing(getClass().getName(), "validateUnderlyingCoverageOverlap", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateUnderlyingOverlap", statusCode);
        }
        return statusCode;
    }

    /**
     * load all active related coverage for select
     *
     * @param inputRecord input record
     * @return recordset of active related coverages
     */
    public RecordSet loadAvailableRelatedCoverage(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAvailableRelatedCoverage", new Object[]{inputRecord});
        }
        RecordSet rs;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("policyId", "policyUnderPolId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Underlying_Coverage.Sel_Underlying_Related_Covg", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load all active related coverage", e);
            l.throwing(getClass().getName(), "loadAvailableRelatedCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAvailableRelatedCoverage", rs);
        }

        return rs;
    }

    /**
     * Check if any underlying coverage version which is from same official record and in same time period exists.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    public String validateSameOffVersionExists(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateSameOffVersionExists", inputRecord);
        String sameOffVersionExists;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Underlying_Coverage.Same_Official_Version_Exist");
        try {
            sameOffVersionExists = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate underlying coverage if same official record exists.", e);
            l.throwing(getClass().getName(), "validateSameOffVersionExists", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateSameOffVersionExists");

        return sameOffVersionExists;
    }
}
