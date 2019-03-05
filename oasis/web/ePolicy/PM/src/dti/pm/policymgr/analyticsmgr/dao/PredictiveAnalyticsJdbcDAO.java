package dti.pm.policymgr.analyticsmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
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
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 06, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/14/2011       kshen       Added methods for Opa Error page.
 * ---------------------------------------------------
 */
public class PredictiveAnalyticsJdbcDAO extends BaseDAO implements PredictiveAnalyticsDAO {

    /**
     * Retrieve request.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRequest(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRequest");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("WB_OPA.Get_Scoring_Requests");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get request.", e);
            l.throwing(getClass().getName(), "loadAllRequest", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRequest", rs);
        }
        return rs;
    }

    /**
     * Retrieve request.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllResult(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllResult");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("WB_OPA.Get_Scoring_Results");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get result.", e);
            l.throwing(getClass().getName(), "loadAllResult", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllResult", rs);
        }
        return rs;
    }

    /**
     * Retrieve reason.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllReason(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllReason");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("WB_OPA.Get_Scoring_Reasons");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get reason.", e);
            l.throwing(getClass().getName(), "loadAllReason", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllReason", rs);
        }
        return rs;
    }

    /**
     * Process opa.
     *
     * @param inputRecord
     * @return Record
     */
    public Record processOpa(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOpa");
        }
        Record outputRecord;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("requestType", "scoreRequestType"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("WB_OPA.Process_Opa", mapping);
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to process Opa.", e);
            l.throwing(getClass().getName(), "processOpa", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processOpa", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Get model type for current policy.
     *
     * @param inputRecord
     * @return String
     */
    public String getModelType(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getModelType");
        }
        String returnValue;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("issueSt", "issueStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCoId", "issueCompanyEntityId"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Opa_Extract.Get_Model_Type", mapping);
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Get model type.", e);
            l.throwing(getClass().getName(), "getModelType", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getModelType", returnValue);
        }
        return returnValue;
    }

    /**
     * Load all Scoring Errors Records.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllScoringError(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllScoringError", new Object[]{inputRecord});
        }

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("scoreReqId", "searchCriteria_scoreReqId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("scoreReqStartDate", "searchCriteria_scoreReqStartDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("scoreReqEndDate", "searchCriteria_scoreReqEndDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("scoreReqTypeCode", "searchCriteria_scoreReqTypeCode"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Wb_Opa.Get_Scoring_Errors", mapping);
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllScoringError", rs);
            }

            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load scoring errors.", e);
            l.throwing(getClass().getName(), "loadAllScoringError", ae);
            throw ae;
        }
    }

    /**
     * Load scoring error details.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllScoringErrorDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllScoringErrorDetail", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Wb_Opa.Get_Scoring_Errors_Detail");
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllScoringErrorDetail", rs);
            }

            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load scoring error details.", e);
            l.throwing(getClass().getName(), "loadAllScoringErrorDetail", ae);
            throw ae;
        }
    }

}
