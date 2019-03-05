package dti.pm.riskmgr.insuredmgr.impl;

import java.text.ParseException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.RecordMode;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.Term;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.dao.RiskDAO;
import dti.pm.riskmgr.insuredmgr.InsuredTrackingFields;
import dti.pm.riskmgr.insuredmgr.InsuredTrackingManager;
import dti.pm.riskmgr.insuredmgr.dao.InsuredTrackingDAO;
import dti.pm.transactionmgr.TransactionFields;

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
public class InsuredTrackingManagerImpl implements InsuredTrackingManager {

    private Record calcTermEffExpDates(PolicyHeader policyHeader, String searchTermHistoryId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "calcTermEffExpDates", new Object[]{policyHeader, searchTermHistoryId});
        }
        try {
            // find term by specified term base record id
            Term searchTerm = null;
            Iterator<Term> iter = policyHeader.getPolicyTerms();
            while (iter.hasNext()) {
                Term term = iter.next();
                if (term.getTermBaseRecordId().equals(searchTermHistoryId)) {
                    searchTerm = term;
                    break;
                }
            }
            Record r = new Record();
            // no term is found by specified term base record id
            if (searchTerm == null) {
                return r;
            }
            // flat term
            else if (DateUtils.daysDiff(searchTerm.getEffectiveFromDate(), searchTerm.getEffectiveToDate()) <= 0) {
                PolicyFields.setTermEffFromDate(r, searchTerm.getEffectiveFromDate());
                PolicyFields.setTermEffToDate(r, searchTerm.getEffectiveToDate());
                return r;
            }
            // check if any non-flat term prior to specified term
            Term priorTerm = null;
            iter = policyHeader.getPolicyTerms();
            while (iter.hasNext()) {
                Term term = iter.next();
                if (!searchTerm.getTermBaseRecordId().equals(term.getTermBaseRecordId()) &&
                    DateUtils.daysDiff(term.getEffectiveFromDate(), term.getEffectiveToDate()) > 0 &&
                    DateUtils.daysDiff(term.getEffectiveToDate(), searchTerm.getEffectiveFromDate()) >= 0) {
                    if (priorTerm == null) {
                        priorTerm = term;
                    }
                    else {
                        if (DateUtils.daysDiff(priorTerm.getEffectiveToDate(), term.getEffectiveToDate()) > 0) {
                            priorTerm = term;
                        }
                    }
                }
            }
            // first term
            if (priorTerm == null) {
                PolicyFields.setTermEffToDate(r, searchTerm.getEffectiveToDate());
            }
            // there's a gap between prior term and specified term
            else if (DateUtils.daysDiff(priorTerm.getEffectiveToDate(), searchTerm.getEffectiveFromDate()) > 0){
                PolicyFields.setTermEffFromDate(r, priorTerm.getEffectiveToDate());
                PolicyFields.setTermEffToDate(r, searchTerm.getEffectiveToDate());
            }
            else {
                PolicyFields.setTermEffFromDate(r, searchTerm.getEffectiveFromDate());
                PolicyFields.setTermEffToDate(r, searchTerm.getEffectiveToDate());
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "calcTermEffExpDates", r);
            }
            return r;
        }catch (ParseException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in parsing dates.", e);
            l.throwing(getClass().getName(), "calcTermEffExpDates", ae);
            throw ae;
        }
    }

    @Override
    public RecordSet loadAllInsuredTracking(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllInsuredTracking", new Object[]{policyHeader, inputRecord});
        }

        Record input = new Record();
        RiskFields.setRiskBaseRecordId(input, policyHeader.getRiskHeader().getRiskBaseRecordId());
        InsuredTrackingFields.setRecordModeCode(input, policyHeader.getRecordMode().getName());
        TransactionFields.setEndorsementQuoteId(input, policyHeader.getLastTransactionInfo().getEndorsementQuoteId());

        if (inputRecord.hasField(InsuredTrackingFields.SEARCH_TERM_HISTORY_ID)) {
            input.setFields(calcTermEffExpDates(policyHeader, InsuredTrackingFields.getSearchTermHistoryId(inputRecord)));
        }
        if (inputRecord.hasField(InsuredTrackingFields.SEARCH_ENTITY_ID)) {
            InsuredTrackingFields.setEntityId(input, InsuredTrackingFields.getSearchEntityId(inputRecord));
        }
        if (inputRecord.hasField(InsuredTrackingFields.SEARCH_INSURED_TYPE)) {
            InsuredTrackingFields.setInsuredType(input, InsuredTrackingFields.getSearchInsuredType(inputRecord));
        }

        RecordLoadProcessor loadProcessor = new InsuredTrackingEntitlementRecordLoadProcessor(policyHeader, inputRecord);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor,
                        new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, InsuredTrackingFields.INSURED_TRACKING_ID));

        RecordSet rs = getInsuredTrackingDAO().loadAllInsuredTracking(input, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllInsuredTracking", rs);
        }
        return rs;
    }

    @Override
    public void saveAllInsuredTracking(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllInsuredTracking", new Object[]{inputRecords});
        }

        // input policyHeader parameter
        inputRecords.setFieldValueOnAll(InsuredTrackingFields.RISK_ID, policyHeader.getRiskHeader().getRiskId(), true);
        inputRecords.setFieldValueOnAll(InsuredTrackingFields.RISK_BASE_RECORD_ID, policyHeader.getRiskHeader().getRiskBaseRecordId(), true);
        inputRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId(), true);

        // validation
        validateAllInsuredTracking(policyHeader, inputRecord, inputRecords);

        // delete
        RecordSet deleteRs = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        if(deleteRs.getSize() > 0){
            getInsuredTrackingDAO().deleteAllInsuredTracking(deleteRs);
        }

        // insert
        RecordSet insertRs = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        if(insertRs.getSize() > 0){
            insertRs.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
            getInsuredTrackingDAO().insertAllInsuredTracking(insertRs);
        }

        // update
        RecordSet updateRs = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        if(updateRs.getSize() > 0){
            updateRs.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
            getInsuredTrackingDAO().updateAllInsuredTracking(updateRs);
        }

        // validate
        Record record = new Record();
        record.setFieldValue(InsuredTrackingFields.RISK_BASE_RECORD_ID, policyHeader.getRiskHeader().getRiskBaseRecordId());
        record = getInsuredTrackingDAO().validateAllInsuredTracking(record);
        Integer statusCode = record.getIntegerValue(InsuredTrackingFields.STATUS_CODE);
        if (statusCode < 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainInsuredTracking.overlapDates.error");
            throw new ValidationException("Invalid data.");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllInsuredTracking");
        }
    }

    @Override
    public Record getInitialValuesForInsuredTracking(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForInsuredTracking", new Object[]{policyHeader, inputRecord});
        }

        Record record = new Record();
        record.setFields(inputRecord);
        record.setFields(InsuredTrackingEntitlementRecordLoadProcessor.getInitialEntitlementValues());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForInsuredTracking", record);
        }
        return record;
    }

    /**
     * Get the latest term expiration date.
     *
     * @param policyHeader
     * @return
     */
    private String getLatestTermExpDate(PolicyHeader policyHeader) {
        String latestTermExpDateStr = "";
        Iterator iter = policyHeader.getPolicyTerms();
        if (iter.hasNext()) {
            Term lastTerm = (Term) iter.next();
            latestTermExpDateStr = lastTerm.getEffectiveToDate();
        }
        return latestTermExpDateStr;
    }

    private void validateAllInsuredTracking (PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllInsuredTracking", new Object[]{inputRecords});
        }

        // get risk expiration date
        Record r = new Record();
        RiskFields.setRiskBaseRecordId(r, policyHeader.getRiskHeader().getRiskBaseRecordId());
        RiskFields.setRiskEffectiveFromDate(r, policyHeader.getRiskHeader().getRiskEffectiveFromDate());
        String riskExpDate = getRiskDAO().getRiskExpDate(r);

        // Set the displayRecordNumber to all visible records.
        PMRecordSetHelper.setDisplayRecordNumberOnRecords(inputRecords);
        
        // create a recordset for insert/update validation   
        RecordSet modifiedRecords = inputRecords
                .getSubSet(new UpdateIndicatorRecordFilter(new String[] {UpdateIndicator.INSERTED, UpdateIndicator.UPDATED }));
        // loop each record to validate
        try {
            for (int i = 0; i < modifiedRecords.getSize(); i ++) {
                Record record = modifiedRecords.getRecord(i);
                String rowId = record.getRowId();
                if(DateUtils.daysDiff(InsuredTrackingFields.getStartDate(record), InsuredTrackingFields.getEndDate(record)) < 0) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainInsuredTracking.invalidDates.error",
                        InsuredTrackingFields.START_DATE, rowId);
                }

                if(record.hasStringValue(InsuredTrackingFields.RETROACTIVE_DATE) &&
                    DateUtils.daysDiff(InsuredTrackingFields.getRetroactiveDate(record), InsuredTrackingFields.getStartDate(record)) < 0) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainInsuredTracking.invalidRetroDate.error",
                        InsuredTrackingFields.RETROACTIVE_DATE, rowId);
                }

                if(DateUtils.daysDiff(InsuredTrackingFields.getStartDate(record), getLatestTermExpDate(policyHeader)) <= 0) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainInsuredTracking.invalidStartDate.error",
                            InsuredTrackingFields.START_DATE, rowId);
                }
                if(RecordMode.TEMP.getName().equals(InsuredTrackingFields.getRecordModeCode(record)) &&
                    StringUtils.isBlank(InsuredTrackingFields.getOfficialRecordId(record)) &&
                    DateUtils.daysDiff(InsuredTrackingFields.getStartDate(record), InsuredTrackingFields.getEndDate(record)) == 0) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainInsuredTracking.flatRecord.error",
                        InsuredTrackingFields.START_DATE, rowId);
                }

                if(DateUtils.daysDiff(InsuredTrackingFields.getEndDate(record), riskExpDate) < 0) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainInsuredTracking.invalidEndDate.error",
                            InsuredTrackingFields.END_DATE, rowId);
                }
            }
        }
        catch (ParseException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in parsing dates.", e);
            l.throwing(getClass().getName(), "validateAllInsuredTracking", ae);
            throw ae;
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid data.");
        }

        l.exiting(getClass().getName(), "validateAllRiskRelation");
    }

    public InsuredTrackingDAO getInsuredTrackingDAO() {
        return m_insuredTrackingDAO;
    }

    public void setInsuredTrackingDAO(InsuredTrackingDAO insuredTrackingDAO) {
        m_insuredTrackingDAO = insuredTrackingDAO;
    }
    public RiskDAO getRiskDAO() {
        return m_riskDAO;
    }

    public void setRiskDAO(RiskDAO riskDAO) {
        m_riskDAO = riskDAO;
    }

    private InsuredTrackingDAO m_insuredTrackingDAO;
    private RiskDAO m_riskDAO;
}