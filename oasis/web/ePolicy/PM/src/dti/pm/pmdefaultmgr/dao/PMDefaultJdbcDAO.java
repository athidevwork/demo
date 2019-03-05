package dti.pm.pmdefaultmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.pm.core.dao.BaseDAO;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.dao.DataFieldNames;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 19, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/10/2007       sxm         1. Moved getDefaultState() from PolicyJdbcDAO
 *                              2. Moved getInitialDddwForRisk() from RiskJdbcDAO
 * 03/01/2010       fcb         104191: new function added: getMappedDefaultLevel. 
 * ---------------------------------------------------
 */
public class PMDefaultJdbcDAO extends BaseDAO implements PMDefaultDAO {
    /**
     * Load the PM Level Defaults, returning each default as a field in the resulting Record.
     */
    public Record getDefaultLevel(Record input) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultLevel", new Object[]{input});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EFF, PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE));
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TRANS_EFF, TransactionFields.TRANSACTION_EFFECTIVE_FROM_DATE));

        Record result;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Default.Get_Level_Default_1", mapping);
        try {
            result = spDao.execute(input).getSummaryRecord();
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get default level values", e);
            l.throwing(getClass().getName(), "getDefaultLevel", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultLevel", result);
        }
        return result;
    }

    /**
     * Load the PM Mapped Level Defaults, returning each default as a field in the resulting Record.
     */
    public Record getMappedDefaultLevel(Record input) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMappedDefaultLevel", new Object[]{input});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EFF, PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE));
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TRANS_EFF, TransactionFields.TRANSACTION_EFFECTIVE_FROM_DATE));

        Record result;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Default_Manager.Get_Mapped_Level_Default", mapping);
        try {
            result = spDao.execute(input).getSummaryRecord();
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get default level values", e);
            l.throwing(getClass().getName(), "getMappedDefaultLevel", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMappedDefaultLevel", result);
        }
        return result;
    }

    /**
     * Get default state code based on entity ID
     *
     * @param defaultLevel default level code
     * @param entityId     entity ID
     * @param asOfDate     as of date in mm/dd/yyyy format
     * @return String containing the default state code
     */
    public String getDefaultState(String defaultLevel, String entityId, String asOfDate) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultState",
                new Object[]{defaultLevel, entityId, asOfDate});
        }

        // map the values to the input record
        Record inputRecord = new Record();
        inputRecord.setFieldValue("defaultLevel", defaultLevel);
        inputRecord.setFieldValue("entityId", entityId);
        inputRecord.setFieldValue("asOfdate", asOfDate);

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Default.Get_Default_State");
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Default.Get_Default_State.", e);
            l.throwing(getClass().getName(), "getDefaultState", ae);
            throw ae;
        }

        // done
        l.exiting(getClass().getName(), "getDefaultState", returnValue);
        return returnValue;
    }

    /**
     * Get risk default DDDWs.
     *
     * @param termEffectiveFromdate  Term effective from date.
     * @param transEffectiveFromDate Transaction effective from date.
     * @param policyTypeCode         Policy type code.
     * @param riskTypeCode           Risk type code.
     * @param practiceStateCode      Risk paractice state code.
     * @return String that contains DDDW field ID list.
     */
    public String getInitialDddwForRisk(String termEffectiveFromdate, String transEffectiveFromDate,
                                        String policyTypeCode, String riskTypeCode, String practiceStateCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialDddwForRisk",
                new Object[]{termEffectiveFromdate, transEffectiveFromDate, policyTypeCode, riskTypeCode, practiceStateCode});
        }

        // Create the input data mapping
        Record inputRecord = new Record();
        inputRecord.setFieldValue("level", "RISK_DDDW");
        inputRecord.setFieldValue("termEff", termEffectiveFromdate);
        inputRecord.setFieldValue("transEff", transEffectiveFromDate);
        inputRecord.setFieldValue("code1", "POLICY_TYPE_CODE");
        inputRecord.setFieldValue("value1", policyTypeCode);
        inputRecord.setFieldValue("code2", "RISK_TYPE_CODE");
        inputRecord.setFieldValue("value2", riskTypeCode);
        inputRecord.setFieldValue("code3", "PRACTICE_STATE_CODE");
        inputRecord.setFieldValue("value3", practiceStateCode);
        inputRecord.setFieldValue("webFieldIdB", "Y");

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Default.Get_Dddw_Default");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue("columnList");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get risk default DDDWs", e);
            l.throwing(getClass().getName(), "getInitialDddwForRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialDddwForRisk", returnValue);
        }
        return returnValue;
    }

    /**
     * Get mapping code values from primary risk to others.
     *
     * @param inputRecord Record containing the mapped level as well as policy, risk, coverage details
     * @return String comma-delimited string containing all parent mapping code values
     */
    public String getMappedDefaultValues(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMappedDefaultValues",
                new Object[]{inputRecord});
        }

        // Create the input data mapping
        inputRecord.setFieldValue("transEff", inputRecord.getStringValue("transEffectiveFromDate"));

        // Get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Default_Manager.Get_Mapped_From_Codes");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get mapped from codes", e);
            l.throwing(getClass().getName(), "getMappedDefaultValues", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMappedDefaultValues", returnValue);
        }
        return returnValue;
    }

    /**
     * Get default value for relationship type or to rate.
     *
     * @param inputRecord Record containing the expected values
     * @return String of the default value
     */
    public String getDefaultValue(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultValue", new Object[]{inputRecord});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("defaultLevel", "level"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transEffDate", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "transactionLogId"));

        // Get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Default.Get_Default_Value", mapping);
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get default value", e);
            l.throwing(getClass().getName(), "getDefaultValue", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultValue", returnValue);
        }
        return returnValue;
    }
}
