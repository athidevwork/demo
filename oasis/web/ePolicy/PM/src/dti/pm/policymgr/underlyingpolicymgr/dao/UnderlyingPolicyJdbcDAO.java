package dti.pm.policymgr.underlyingpolicymgr.dao;

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
 * JDBC dao for underlying policy
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 3, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------------------------------------------------------------------------
 * 01/03/2014       jyang       148771 - Updated the code to use termEff/termFxp to replace the constant values for
 *                                       loadAllActivePolicy.
 * 08/28/2016       ssheng      178365 - Add method validateUnderlyingOverlap.
 * 09/09/2016       xnie        178813 - Added validateSameOffVersionExists().
 * ---------------------------------------------------------------------------------------------------------------------
 */
public class UnderlyingPolicyJdbcDAO extends BaseDAO implements UnderlyingPolicyDAO {
    /**
     * load all underlying policy
     *
     * @param inputRecord         input record cotains all required parameters
     * @param recordLoadProcessor record load processor
     * @return result recordset
     */
    public RecordSet loadAllUnderlyingPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllUnderlyingPolicy", new Object[]{inputRecord, recordLoadProcessor});
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Underlying_Policy.Sel_Underlying_Policy");
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load underlying policy information", e);
            l.throwing(getClass().getName(), "loadAllUnderwriters", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllUnderwriters", rs);
        }
        return rs;
    }


    /**
     * load all retro date
     *
     * @param inputRecord input record cotains all required parameters
     * @return result recordset contains retroDate column
     */
    public RecordSet loadAllRetroDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRetroDate", new Object[]{inputRecord});
        }
        RecordSet rs;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "effectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "effectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("underPolicyId", "policyUnderPolId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("compInsured", "companyInsuredB"));
            mapping.addFieldMapping(new DataRecordFieldMapping("covgCode", "covPartCoverageCode"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Underlying_Policy.Sel_Retro_Date_Info", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load retro dates", e);
            l.throwing(getClass().getName(), "loadAllRetroDate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRetroDate", rs);
        }
        return rs;
    }


    /**
     * get initial values for underlying policy
     *
     * @param inputRecord input record
     * @return record contains all initial values
     */
    public Record getInitialValuesForUnderlyingPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForUnderlyingPolicy", new Object[]{inputRecord});
        }
        Record resultRecord;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("policyId", "policyUnderPolId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Policy_Under_Info", mapping);
            resultRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get initial values for underlying policy", e);
            l.throwing(getClass().getName(), "getInitialValuesForUnderlyingPolicy", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForUnderlyingPolicy", resultRecord);
        }

        return resultRecord;
    }


    /**
     * insert or update underlying policy infos
     *
     * @param inputRecords recordset contains inserted/modified records
     * @return processed record count
     */
    public int saveAllUnderlyingPolicy(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllUnderlyingPolicy", new Object[]{inputRecords});
        }

        int processCount;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Underlying_Policy.Save_Underlying_Policy", mapping);
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save all underlying policy", e);
            l.throwing(getClass().getName(), "saveAllUnderlyingPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllUnderlyingPolicy", String.valueOf(processCount));
        }
        return processCount;

    }


    /**
     * delete underlying policy infos
     *
     * @param inputRecords recordset contains deleted records
     * @return processed record count
     */
    public int deleteAllUnderlyingPolicy(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllUnderlyingPolicy", new Object[]{inputRecords});
        }

        int processCount;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Underlying_Policy.Del_Underlying_Policy");
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to delete all underlying policy", e);
            l.throwing(getClass().getName(), "deleteAllUnderlyingPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllUnderlyingPolicy", String.valueOf(processCount));
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
            l.entering(getClass().getName(), "validateUnderlyingOverlap", new Object[]{inputRecord});
        }

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Underlying_Policy.Validate_Underlying_Overlap", mapping);
            Record output = spDao.executeUpdate(inputRecord);
            statusCode = output.getStringValue("statusCode");
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to validate underlying", e);
            l.throwing(getClass().getName(), "validateUnderlyingOverlap", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateUnderlyingOverlap", statusCode);
        }
        return statusCode;
    }

    /**
     * Check if any underlying policy version which is from same official record and in same time period exists.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    public String validateSameOffVersionExists(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateSameOffVersionExists", inputRecord);
        String sameOffVersionExists;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Underlying_Policy.Same_Official_Version_Exist");
        try {
            sameOffVersionExists = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate if same official record exists.", e);
            l.throwing(getClass().getName(), "validateSameOffVersionExists", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateSameOffVersionExists");

        return sameOffVersionExists;
    }
}
