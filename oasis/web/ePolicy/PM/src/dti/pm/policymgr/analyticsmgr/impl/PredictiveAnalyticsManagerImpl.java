package dti.pm.policymgr.analyticsmgr.impl;

import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.DateUtils;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.analyticsmgr.PredictiveAnalyticsFields;
import dti.pm.policymgr.analyticsmgr.PredictiveAnalyticsManager;
import dti.pm.policymgr.analyticsmgr.PredictiveAnalyticsErrorsFields;
import dti.pm.policymgr.analyticsmgr.dao.PredictiveAnalyticsDAO;
import dti.pm.transactionmgr.TransactionFields;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 17, 2009
 *
 * @author gchitta
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/14/2011       kshen       Added methods for Opa Error page.
 * ---------------------------------------------------
 */
public class PredictiveAnalyticsManagerImpl implements PredictiveAnalyticsManager {

    /**
     * Retrieve request.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRequest(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRequest", new Object[]{inputRecord});
        }
        RecordSet rs = getPredictiveAnalyticsDAO().loadAllRequest(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRequest", rs);
        }
        return rs;
    }

    /**
     * Retrieve result.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllResult(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllResult", new Object[]{inputRecord});
        }
        RecordSet rs = getPredictiveAnalyticsDAO().loadAllResult(inputRecord);
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
            l.entering(getClass().getName(), "loadAllReason", new Object[]{inputRecord});
        }
        RecordSet rs = getPredictiveAnalyticsDAO().loadAllReason(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllReason", rs);
        }
        return rs;
    }

    /**
     * Get initial values for opa.
     *
     * @param policyHeader
     * @return
     */
    public Record getInitialValueForOpa(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValueForOpa", new Object[]{policyHeader});
        }
        Record outputRecord = new Record();
        // Set policyId.
        outputRecord.setFields(policyHeader.toRecord());
        // Set model type.
        String modelType = getPredictiveAnalyticsDAO().getModelType(policyHeader.toRecord());
        PredictiveAnalyticsFields.setModelType(outputRecord, modelType);
        // Set socre request type.
        TransactionTypeCode transTypeCode = policyHeader.getLastTransactionInfo().getTransactionTypeCode();
        if (transTypeCode.isNewBus()) {
            PredictiveAnalyticsFields.setScoreRequestType(outputRecord, "N");
        }
        else if (transTypeCode.isRenewal()) {
            PredictiveAnalyticsFields.setScoreRequestType(outputRecord, "D");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValueForOpa", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Get initial values for opa error page.
     *
     * @return
     */
    public Record getInitialValueForOpaError() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValueForOpaError");
        }

        Record outputRecord = new Record();
        PredictiveAnalyticsErrorsFields.setSearchCriteriaScoreReqTypeCode(outputRecord, "R");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValueForOpa", outputRecord);
        }
        return outputRecord;
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

        RecordSet rs = getPredictiveAnalyticsDAO().loadAllScoringError(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllScoringError", rs);
        }
        return rs;
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

        RecordSet rs = getPredictiveAnalyticsDAO().loadAllScoringErrorDetail(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllScoringErrorDetail", rs);
        }

        return rs;
    }

    /**
     * Search Scoring Error Records.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet searchScoringError(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "searchScoringError", new Object[]{inputRecord});
        }

        validateSearchScoringErrorCriteria(inputRecord);

        RecordSet rs = getPredictiveAnalyticsDAO().loadAllScoringError(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "searchScoringError", rs);
        }

        return rs;
    }

    /**
     * Validate the search criteria of searching scoring error.
     * @param inputRecord
     */
    protected void validateSearchScoringErrorCriteria(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSearchScoringErrorCriteria", new Object[]{inputRecord});
        }

        String opaScoreReqId = PredictiveAnalyticsErrorsFields.getSearchCriteriaOpaScoreReqId(inputRecord);
        String scoreReqStartDateStr = PredictiveAnalyticsErrorsFields.getSearchCriteriaScoreReqStartDate(inputRecord);
        String scoreReqEndDateStr = PredictiveAnalyticsErrorsFields.getSearchCriteriaScoreReqEndDate(inputRecord);
        String scoreReqTypeCode = PredictiveAnalyticsErrorsFields.getSearchCriteriaScoreReqTypeCode(inputRecord);

        if (StringUtils.isBlank(opaScoreReqId) && StringUtils.isBlank(scoreReqStartDateStr)
            && StringUtils.isBlank(scoreReqEndDateStr) && StringUtils.isBlank(scoreReqTypeCode)) {
            MessageManager.getInstance().addErrorMessage("pm.predictiveAnalyticsErrors.atLeastOneSearchCriteria.error");
            throw new ValidationException("At least one search criteria must be entered.");
        }

        if (!StringUtils.isBlank(scoreReqStartDateStr) && !StringUtils.isBlank(scoreReqEndDateStr)) {
            Date scoreReqStartDate = DateUtils.parseDate(scoreReqStartDateStr);
            Date scoreReqEndDate = DateUtils.parseDate(scoreReqEndDateStr);
            if (scoreReqStartDate.after(scoreReqEndDate)) {
                MessageManager.getInstance().addErrorMessage(
                    "pm.predictiveAnalyticsErrors.searchCriteria.reqStartDateAfterEndDate.error",
                    PredictiveAnalyticsErrorsFields.SEARCH_CRITERIA_SCORE_REQ_END_DATE);
                throw new ValidationException("Score Req End Date must be after Score Req Start Date.");
            }
        }

        l.exiting(getClass().getName(), "validateSearchScoringErrorCriteria");
    }

    /**
     * Process opa.
     *
     * @param inputRecord
     */
    public void processOpa(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOpa", new Object[]{inputRecord});
        }
        validateProcessOpa(inputRecord);

        //Set transactionLogId to null if not in a transaction
        if(TransactionFields.getTransactionStatusCode(inputRecord).isComplete()) {
            TransactionFields.setTransactionLogId(inputRecord, "");
        }

        Record record = getPredictiveAnalyticsDAO().processOpa(inputRecord);
        int rc = record.getIntegerValue("rc").intValue();
        if (rc < 0 && record.hasStringValue("rmsg")) {
            String msg = record.getStringValue("rmsg");

            if(msg.indexOf(".error") > 0) {
                MessageManager.getInstance().addErrorMessage(msg);
            } else {
                MessageManager.getInstance().addErrorMessage("pm.predictiveAnalytics.process.error", new String[]{msg});
            }
        }
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Process Opa error.");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processOpa");
        }
    }

    protected void validateProcessOpa(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateProcessOpa", new Object[]{inputRecord});
        }
        // Validate model type.
        if (!inputRecord.hasStringValue(PredictiveAnalyticsFields.MODEL_TYPE)) {
            MessageManager.getInstance().addErrorMessage("pm.predictiveAnalytics.process.modelType.required");
        }
        // Validate request type.
        if (!inputRecord.hasStringValue(PredictiveAnalyticsFields.SCORE_REQUEST_TYPE)) {
            MessageManager.getInstance().addErrorMessage("pm.predictiveAnalytics.process.requestType.required");
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Data error.");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateProcessOpa");
        }
    }

    public PredictiveAnalyticsDAO getPredictiveAnalyticsDAO() {
        return m_predictiveAnalyticsDAO;
    }

    public void setPredictiveAnalyticsDAO(PredictiveAnalyticsDAO predictiveAnalyticsDAO) {
        m_predictiveAnalyticsDAO = predictiveAnalyticsDAO;
    }

    private PredictiveAnalyticsDAO m_predictiveAnalyticsDAO;
}
