package dti.pm.riskmgr.addtlexposuremgr.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.core.data.RowAccessorRecordLoadProcessor;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.Term;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskManager;
import dti.pm.riskmgr.affiliationmgr.AffiliationFields;
import dti.pm.riskmgr.dao.RiskDAO;
import dti.pm.riskmgr.addtlexposuremgr.RiskAddtlExposureFields;
import dti.pm.riskmgr.addtlexposuremgr.RiskAddtlExposureManager;
import dti.pm.riskmgr.addtlexposuremgr.dao.RiskAddtlExposureDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.validationmgr.impl.ContinuityRecordValidator;
import dti.pm.validationmgr.impl.PreOoseChangeValidator;
import dti.pm.validationmgr.impl.StandardEffectiveToDateRecordValidator;
import java.math.BigDecimal;

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
 * 11/21/2017    lzhang     189820  Modified validateAllRiskAddtlExposure:
 *                                  get addtlExpBaseId for new-added records
 * 12/08/2017    eyin       190084  1) Modified the name of setFieldsForExpireAddtlPractice();
 *                                  2) Removed the if condition about comparing effective to date.
 *                                  3) Modified validateAllRiskAddtlExposure(), add specific validation logic about
 *                                  effective to date for OOSE transaction.
 * 12/27/2017    eyin       190491  1) Added validateExpChangeDateAllowed, to check if changing expiring date is allowed.
 * ---------------------------------------------------
 */
public class RiskAddtlExposureManagerImpl implements RiskAddtlExposureManager {

    /**
     * Returns a RecordSet loaded with list of available Additional Exposure for the provided risk.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with original selected risk information
     * @param loadProcessor an instance of data load processor
     * @param processEntitlements true/false indicator for processing entitlements
     * @return RecordSet   a RecordSet loaded with list of available Additional Exposure.
     */
    public RecordSet loadAllRiskAddtlExposure(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor, boolean processEntitlements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskAddtlExposure", new Object[]{policyHeader});
        }

        Record input = new Record();

        // Set the input record
        RecordMode recordModeCode = RecordMode.TEMP;
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (viewMode.isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        PMCommonFields.setRecordModeCode(input, recordModeCode);

        RiskAddtlExposureFields.setRiskBaseRecordId(input, policyHeader.getRiskHeader().getRiskBaseRecordId());
        PolicyHeaderFields.setPolicyId(input, policyHeader.getPolicyId());
        PolicyHeaderFields.setTermBaseRecordId(input, policyHeader.getTermBaseRecordId());
        TransactionFields.setEndorsementQuoteId(input, policyHeader.getLastTransactionInfo().getEndorsementQuoteId());
        PolicyHeaderFields.setTermEffectiveFromDate(input, policyHeader.getTermEffectiveFromDate());
        PolicyHeaderFields.setTermEffectiveToDate(input, policyHeader.getTermEffectiveToDate());

        // Calculate dates
        RiskAddtlExposureFields.setTransactionCode(input, policyHeader.getLastTransactionInfo().getTransactionCode().getName());
        RiskFields.setRiskId(input, inputRecord.getStringValue(RiskFields.RISK_ID));
        RiskAddtlExposureFields.setTransEffectiveFromDate(input, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        RiskAddtlExposureFields.setLatestTermExpDate(input, getLatestTermExpDate(policyHeader));

        // Row accessor process will set display indicator to hide records, so this process is not depended on
        // processEntitlements indicator.
        RecordLoadProcessor rowAccessorLP = new RowAccessorRecordLoadProcessor(
            RiskAddtlExposureFields.RISK_ADDTL_EXPOSURE_ID, RiskAddtlExposureFields.EFFECTIVE_FROM_DATE,
            RiskAddtlExposureFields.EFFECTIVE_TO_DATE, policyHeader, policyHeader.getScreenModeCode());
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, rowAccessorLP);

        if(processEntitlements){
            RecordLoadProcessor entitlementRLP = new RiskAddtlExposureEntitlementRecordLoadProcessor(policyHeader,
                inputRecord, this);
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, entitlementRLP);

            // Add original value for validation
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(origFieldLoadProcessor, loadProcessor);

            // filter official record for end quote.
            FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor =
                new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, RiskAddtlExposureFields.RISK_ADDTL_EXPOSURE_ID);
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, endquoteLoadProcessor);
        }

        RecordSet rs = getRiskAddtlExposureDAO().loadAllRiskAddtlExposure(input, loadProcessor);

        Record sumRec = rs.getSummaryRecord();

        // get risk name
        RiskFields.setRiskName(sumRec, policyHeader.getRiskHeader().getRiskName());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskAddtlExposure", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of Primary Practice for the provided risk.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with original selected risk information
     * @return RecordSet   a RecordSet loaded with list of Primary Practice.
     */
    public RecordSet loadPrimaryPractice(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPrimaryPractice", new Object[]{policyHeader});
        }

        Record input = new Record();

        // Set the input record
        RecordMode recordModeCode = RecordMode.TEMP;
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (viewMode.isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        PMCommonFields.setRecordModeCode(input, recordModeCode);

        RiskFields.setRiskBaseRecordId(input, policyHeader.getRiskHeader().getRiskBaseRecordId());
        PolicyHeaderFields.setPolicyId(input, policyHeader.getPolicyId());
        PolicyHeaderFields.setTermBaseRecordId(input, policyHeader.getTermBaseRecordId());
        TransactionFields.setEndorsementQuoteId(input, policyHeader.getLastTransactionInfo().getEndorsementQuoteId());
        PolicyHeaderFields.setTermEffectiveFromDate(input, policyHeader.getTermEffectiveFromDate());
        PolicyHeaderFields.setTermEffectiveToDate(input, policyHeader.getTermEffectiveToDate());
        TransactionFields.setTransactionLogId(input, policyHeader.getCurTransactionId());

        RecordSet rs = getRiskAddtlExposureDAO().loadPrimaryPractice(input);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPrimaryPractice", rs);
        }
        return rs;
    }

    /**
     * Save all data in Additional Exposure page
     *
     * @param policyHeader   policy header that contains all key policy information.
     * @param inputRecords   a record set with data to be saved.
     */
    public int saveAllRiskAddtlExposure(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRiskAddtlExposure", new Object[]{inputRecords});
        }

        int updateCount = 0;

        // input policyHeader parameter
        inputRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId(), true);

        // set specialty as empty for Org type risk.
        inputRecords.setFieldValueOnAll(RiskAddtlExposureFields.RISK_CLASS, "", false);

        // validation
        validateAllRiskAddtlExposure(policyHeader, inputRecords);

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        changedRecords.setFieldsOnAll(policyHeader.toRecord(), false);

        // Split the input records for add, update and delete
        // Get the WIP records and Official records
        RecordSet wipRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet offRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
        RecordSet ooseRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.REQUEST));

        // Get the inserted and updated WIP records
        RecordSet insertedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        insertedWipRecords.setFieldValueOnAll(ROW_STATUS, NEW);
        RecordSet updatedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        updatedWipRecords.setFieldValueOnAll(ROW_STATUS, MODIFIED);

        // Inserted or updated WIP records in batch mode
        updatedWipRecords.addRecords(insertedWipRecords);
        updateCount += getRiskAddtlExposureDAO().insertAllRiskAddtlExposure(updatedWipRecords);

        // Delete the WIP records marked for delete in batch mode
        RecordSet deleteRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        if (deleteRecords.getSize() > 0) {
            updateCount += getRiskAddtlExposureDAO().deleteAllRiskAddtlExposure(deleteRecords);
        }
        // Update the OFFICIAL records marked for update in batch mode
        RecordSet updateRecords = offRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));

        // For the official records set the effective from date equal to the transaction effective date
        // but only for non REQUEST recordModeCode items
        updateRecords.setFieldValueOnAll(RiskAddtlExposureFields.EFFECTIVE_FROM_DATE, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());

        // For the oose records (recordModeCode = 'REQUEST') only reset recordModeCode = 'TEMP'
        ooseRecords.setFieldValueOnAll(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP);

        setFieldsForExpireAddtlPractice(updateRecords);

        updateRecords.addRecords(ooseRecords);
        updateCount += getRiskAddtlExposureDAO().updateAllRiskAddtlExposure(updateRecords);

        // Validation for risk additional practice duplication.
        if (updateCount > 0) {
            Record parmRecord = setInputForDuplicateValidation(policyHeader);
            Record outputRec = getRiskAddtlExposureDAO().validateRiskAddtlPracticeDuplicate(parmRecord);
            if (outputRec != null && YesNoFlag.N.equals(outputRec.getStringValue("result"))) {
                handleDuplicateValidation(policyHeader, outputRec);
                if (MessageManager.getInstance().hasErrorMessages()) {
                    throw new ValidationException("Additional Practice is duplicated.");
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllRiskAddtlExposure");
        }

        return updateCount;
    }

    /**
     * Prepare the input parameters to do duplication validation.
     * @param policyHeader
     * @return
     */
    private Record setInputForDuplicateValidation(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "setInputForDuplicateValidation",
            new Object[]{policyHeader});

        Record parameterRecord = new Record();
        TransactionFields.setTransactionLogId(parameterRecord, policyHeader.getCurTransactionId());
        PolicyFields.setTermEffectiveFromDate(parameterRecord, policyHeader.getTermEffectiveFromDate());
        PolicyFields.setTermEffectiveToDate(parameterRecord, policyHeader.getTermEffectiveToDate());

        l.exiting(getClass().getName(), "setInputForDuplicateValidation", parameterRecord);
        return parameterRecord;
    }

    /**
     * Handle the validation error message after duplication validation is failed.
     * @param policyHeader
     * @param resultRecord
     */
    private void handleDuplicateValidation(PolicyHeader policyHeader, Record resultRecord) {
        Logger l = LogUtils.enterLog(getClass(), "handleDuplicateValidation",
            new Object[]{policyHeader, resultRecord});

        String validateMessage = resultRecord.getStringValue("validateMessage");
        String riskAddtlExposureId = RiskAddtlExposureFields.getRiskAddtlExposureId(resultRecord);
        MessageManager.getInstance().addErrorMessage("pm.common.invalid.data",
            new String[]{validateMessage}, "", riskAddtlExposureId);

        l.exiting(getClass().getName(), "handleDuplicateValidation");
    }

    /**
     * Validate some fields if an additional practice was processed.
     * @param records
     */
    private void setFieldsForExpireAddtlPractice(RecordSet records) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setFieldsForAddtlPractice", new Object[]{records});
        }

        Iterator it = records.getRecords();
        while (it.hasNext()) {
            Record record = (Record)it.next();
            if (record.getFieldValue("RECORDMODECODE").equals(OFFICIAL)) {
                String percentOfPractice = RiskAddtlExposureFields.getPercentPractice(record);
                String origPercentOfPractice = RiskAddtlExposureFields.getOrigPercentPractice(record);
                String coverageLimitCode = RiskAddtlExposureFields.getCoverageLimitCode(record);
                String origCoverageLimitCode = RiskAddtlExposureFields.getOrigCoverageLimitCode(record);
                coverageLimitCode = coverageLimitCode == null ? "" : coverageLimitCode;
                origCoverageLimitCode = origCoverageLimitCode == null ? "" : origCoverageLimitCode;

                if (percentOfPractice.equals(origPercentOfPractice) && coverageLimitCode.equals(origCoverageLimitCode)) {
                    record.setFieldValue(RiskAddtlExposureFields.PERCENT_PRACTICE, null);
                    record.setFieldValue(RiskAddtlExposureFields.COVERAGE_LIMIT_CODE, null);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setFieldsForAddtlPractice");
        }
    }

    /**
     * Get initial values for Risk Addtional Exposure
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return Record a Record loaded with initial values.
     */
    public Record getInitialValuesForAddRiskAddtlExposure(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForRiskAddtionalExposure", new Object[]{policyHeader, inputRecord});
        }
        Record outputRecord = new Record();

        Long lRiskAddtlExposureId = getDbUtilityManager().getNextSequenceNo();
        RiskAddtlExposureFields.setRiskAddtlExposureId(outputRecord, String.valueOf(lRiskAddtlExposureId.longValue()));
        RiskAddtlExposureFields.setRiskBaseRecordId(outputRecord, policyHeader.getRiskHeader().getRiskBaseRecordId());
        RiskAddtlExposureFields.setAfterImageRecordB(outputRecord, YesNoFlag.Y);
        RiskAddtlExposureFields.setBaseRecordB(outputRecord, YesNoFlag.N);
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.TEMP);
        RiskAddtlExposureFields.setOfficialRecordId(outputRecord, "");

        RiskAddtlExposureFields.setEffectiveFromDate(outputRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        RiskAddtlExposureFields.setEffectiveToDate(outputRecord, inputRecord.getStringValue("exposureRiskExpDate"));
        RiskAddtlExposureFields.setRiskTypeCode(outputRecord, policyHeader.getRiskHeader().getRiskTypeCode());
        RiskAddtlExposureFields.setRiskEntityId(outputRecord, policyHeader.getRiskHeader().getRiskEntityId());
        RiskAddtlExposureFields.setRiskStatus(outputRecord, policyHeader.getRiskHeader().getRiskStatusCode().getName());

        // Set Change option to invisible for the new outputRecord
        outputRecord.setFieldValue("isChgPracticeValueAvailable", YesNoFlag.N);
        outputRecord.setFieldValue("isChgPracticeDateAvailable", YesNoFlag.N);
        // Set Delete option to visible for the new outputRecord
        outputRecord.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
        outputRecord.setFieldValue("isSelectAddressAvailable", YesNoFlag.Y);

        outputRecord.setFieldValue("isEffectiveToDateEditable", YesNoFlag.N);
        outputRecord.setFieldValue("isPracticeStateCodeEditable", YesNoFlag.Y);
        outputRecord.setFieldValue("isRiskCountyEditable", YesNoFlag.Y);
        outputRecord.setFieldValue("isRiskClassEditable", YesNoFlag.Y);
        outputRecord.setFieldValue("isPercentPracticeEditable", YesNoFlag.Y);
        outputRecord.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.Y);

        //set premium effective date which will be used to get premium class codes
        String premClassEffDt = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_PREM_CLASS_EFF_DT);
        if (StringUtils.isBlank(premClassEffDt) || premClassEffDt.equals("RISK_EFF_DT")) {
            outputRecord.setFieldValue("premClassEffectiveDate", policyHeader.getRiskHeader().getRiskEffectiveToDate());
        }
        else if (premClassEffDt.equals("TERM_EFF_DT")) {
            outputRecord.setFieldValue("premClassEffectiveDate", policyHeader.getTermEffectiveFromDate());
        }
        else if (premClassEffDt.equals("TRANS_EFF_DT")) {
            outputRecord.setFieldValue("premClassEffectiveDate",
                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }

        // Set original values
        origFieldLoadProcessor.postProcessRecord(outputRecord, true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForRiskAddtionalExposure", outputRecord);
        }
        return outputRecord;
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

    private void validateAllRiskAddtlExposure (PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllRiskAddtionalExposure", new Object[]{inputRecords});
        }

        // Get risk addtl base record id for new-added records
        Iterator ite = inputRecords.getRecords();
        while (ite.hasNext()) {
            Record record = (Record) ite.next();
            if (UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())
                && StringUtils.isBlank(RiskAddtlExposureFields.getRiskAddtlExpBaseRecordId(record))){
                String riskAddtlBaseId = getRiskAddtlExposureDAO().getRiskAddtlBaseId(record);
                RiskAddtlExposureFields.setRiskAddtlExpBaseRecordId(record, riskAddtlBaseId);
            }
        }

        if (policyHeader.getLastTransactionInfo().getTransactionCode().isOosEndorsement()) {
            // Pre-Oose Change validations
            PreOoseChangeValidator preOoseChangeValidator = new PreOoseChangeValidator(
                null, "additional exposure", RiskAddtlExposureFields.RISK_ADDTL_EXPOSURE_ID, RiskAddtlExposureFields.RISK_ADDTL_EXP_BASE_RECORD_ID);
            preOoseChangeValidator.validate(inputRecords);
        }

        RecordSet overlapCheckRecords = inputRecords.getSubSet(
            new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP)).
            getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}));

        //reset record number for overlapCheckRecords
        Iterator iterator = overlapCheckRecords.getRecords();
        int recordNumber = 0;
        while (iterator.hasNext()) {
            Record overlapCheckRecord = (Record) iterator.next();
            overlapCheckRecord.setRecordNumber(recordNumber++);
        }

        // Get an instance of the Continuity Validator
        ContinuityRecordValidator continuityRecordValidator =
            new ContinuityRecordValidator(RiskAddtlExposureFields.EFFECTIVE_FROM_DATE, RiskAddtlExposureFields.EFFECTIVE_TO_DATE,
                RiskAddtlExposureFields.RISK_ADDTL_EXPOSURE_ID, "pm.maintainRiskAddtlExposure.riskExists.error1", overlapCheckRecords);

        // Get an instance of the Standard Effective To Date Rule Validator
        StandardEffectiveToDateRecordValidator effToDateValidator =
            new StandardEffectiveToDateRecordValidator(policyHeader,
                RiskAddtlExposureFields.EFFECTIVE_FROM_DATE, RiskAddtlExposureFields.EFFECTIVE_TO_DATE,
                RiskAddtlExposureFields.RISK_ADDTL_EXPOSURE_ID);

        // Set the displayRecordNumber to all visible records.
        inputRecords = PMRecordSetHelper.setDisplayRecordNumberOnRecords(inputRecords);

        //get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.UPDATED, UpdateIndicator.INSERTED}));

        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(r);
            String rowNum =  String.valueOf(r.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));

            String percentPracticeOrg = RiskAddtlExposureFields.getOrigPercentPractice(r);
            String effectiveToDateOrg = RiskAddtlExposureFields.getOrigEffectiveToDate(r);
            String coverageLimitCodeOrg = RiskAddtlExposureFields.getOrigCoverageLimitCode(r);
            percentPracticeOrg = percentPracticeOrg == null ? "" : percentPracticeOrg;
            coverageLimitCodeOrg = coverageLimitCodeOrg == null ? "" : coverageLimitCodeOrg;
            String percentPractice = RiskAddtlExposureFields.getPercentPractice(r);
            String effectiveToDate = RiskAddtlExposureFields.getEffectiveToDate(r);
            String coverageLimitCode = RiskAddtlExposureFields.getCoverageLimitCode(r);
            String officialRecordId = RiskAddtlExposureFields.getOfficialRecordId(r);
            percentPractice = percentPractice == null ? "" : percentPractice;
            coverageLimitCode = coverageLimitCode == null ? "" : coverageLimitCode;

            String effectiveFromDate = RiskAddtlExposureFields.getEffectiveFromDate(r);

            // if percentPracticeOrg/coverageLimitCodeCompare and effectiveToDateOrg are all null, allow change them together.(OOSE component)
            // if current record's official record id is null and record mode code is TEMP which means current
            // record is a new added one, the validation should be skipped.
            if (!(StringUtils.isBlank(officialRecordId) && recordModeCode.isTemp())) {
                String percentPracticeCompare = percentPracticeOrg;
                String effectiveToDateCompare = effectiveToDateOrg;
                String coverageLimitCodeCompare = coverageLimitCodeOrg;
                if (recordModeCode.isTemp()) {
                    Iterator offIt = inputRecords.getRecords();
                    while (offIt.hasNext()) {
                        Record offR = (Record) offIt.next();
                        if (RiskAddtlExposureFields.getRiskAddtlExposureId(offR).equals(officialRecordId)) {
                            percentPracticeCompare = RiskAddtlExposureFields.getPercentPractice(offR);
                            percentPracticeCompare = percentPracticeCompare == null ? "" : percentPracticeCompare;
                            effectiveToDateCompare = RiskAddtlExposureFields.getEffectiveToDate(offR);
                            coverageLimitCodeCompare = RiskAddtlExposureFields.getCoverageLimitCode(offR);
                            coverageLimitCodeCompare = coverageLimitCodeCompare == null ? "" : coverageLimitCodeCompare;
                            break;
                        }
                    }
                }
                // Validation #1: Only one edit for either Effective To Date or percent of practice/coverage limit code allowed.
                if (!(percentPracticeOrg.equals("") && coverageLimitCodeOrg.equals("") && effectiveToDateOrg == null) &&
                    (!percentPracticeCompare.equals(percentPractice) || !coverageLimitCodeCompare.equals(coverageLimitCode))&&
                    !effectiveToDateCompare.equals(effectiveToDate)){
                    MessageManager.getInstance().addErrorMessage("pm.maintainRiskAddtlExposure.effectiveToDate.rule1.error",
                        new String[]{rowNum}, "", RiskAddtlExposureFields.getRiskAddtlExposureId(r));
                }
            }

            // Validation #2: Standard EffectiveToDate Validator to date
            // It should not be applicable for all changed row during OOSE.
            // Same as Risk's logic.
            if ((policyHeader.getScreenModeCode().isOosWIP())) {
                if (!StringUtils.isBlank(effectiveToDate)) {
                    if (DateUtils.parseDate(effectiveToDate).before(DateUtils.parseDate(effectiveFromDate))) {
                        MessageManager.getInstance().addErrorMessage("pm.standardEffectiveToDateRecordValidator.rule1.error", new String[]{rowNum},
                            RiskAddtlExposureFields.EFFECTIVE_FROM_DATE, RiskAddtlExposureFields.getRiskAddtlExposureId(r));
                    }

                    Date transDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                    Date polExp = DateUtils.parseDate(policyHeader.getPolicyExpirationDate());
                    if (DateUtils.parseDate(effectiveToDate).before(transDate) || DateUtils.parseDate(effectiveToDate).after(polExp)) {
                        MessageManager.getInstance().addErrorMessage("pm.standardEffectiveToDateRecordValidator.rule2.error",
                            new String[]{rowNum,
                                FormatUtils.formatDateForDisplay(transDate),
                                FormatUtils.formatDateForDisplay(polExp)},
                            RiskAddtlExposureFields.EFFECTIVE_FROM_DATE, RiskAddtlExposureFields.getRiskAddtlExposureId(r));
                    }
                }
            }
            else {
                effToDateValidator.validate(r);
            }

            //Validation #3: percent of practice < 100
            //For OOSE record, if percent of practice is NULL, then skip this validation.
            if(!(RiskAddtlExposureFields.getPercentPractice(r) == null && PMCommonFields.getRecordModeCode(r).isRequest())){
                if(!isIntegerValue(RiskAddtlExposureFields.getPercentPractice(r))){
                    MessageManager.getInstance().addErrorMessage("pm.maintainRiskAddtlExposure.riskExists.error2",
                         new String[]{rowNum}, "", RiskAddtlExposureFields.getRiskAddtlExposureId(r));
                }
            }

            // If OOSE, after click Change Date, the % of practice value is empty and disabled, system
            // should skip to validate % of practice value and system should validate change date logic.
            boolean validatePracticeChangeDate = false;
            if(recordModeCode.isRequest() && r.hasStringValue("isPercentPracticeEditable") &&
                !YesNoFlag.getInstance(r.getStringValue("isPercentPracticeEditable")).booleanValue()){
                validatePracticeChangeDate = true;
            }

            // Validation #4: Check if practice expiring date can be changed in OOSE.
            if(validatePracticeChangeDate){
                boolean isOoseChangeDateAllowed = validateExpChangeDateAllowed(r, policyHeader);
                if (!isOoseChangeDateAllowed) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainRiskAddtlExposure.oose.changeDate.effectiveToDate.error",
                        new Object[]{rowNum, effectiveToDate},
                        RiskAddtlExposureFields.EFFECTIVE_TO_DATE, RiskAddtlExposureFields.getRiskAddtlExposureId(r));
                }
            }


            // Validation #5: overlaps validation
            // (inserted and updated) Additional Exposure and RecordModeCode!="REQUEST" (not oose additional exposure)
            if ((r.getUpdateIndicator().equals(UpdateIndicator.UPDATED)
                || r.getUpdateIndicator().equals(UpdateIndicator.INSERTED))
                && !PMCommonFields.getRecordModeCode(r).isRequest()) {


                String[] keyFieldNames = new String[4];
                String[] parmFieldNames = new String[0];
                keyFieldNames[0] = RiskAddtlExposureFields.RISK_BASE_RECORD_ID;
                keyFieldNames[1] = RiskAddtlExposureFields.PRACTICE_STATE_CODE;
                keyFieldNames[2] = RiskAddtlExposureFields.RISK_COUNTY;
                keyFieldNames[3] = RiskAddtlExposureFields.RISK_CLASS;
                continuityRecordValidator.setKeyFieldNames(keyFieldNames);
                continuityRecordValidator.setParmFieldNames(parmFieldNames);
                continuityRecordValidator.validate(r);
            }
        }

        //validation #6: Percent of practice for same time period cannot total over 100%.
        RecordSet activeRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.NOT_CHANGED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        RecordSet closedRecords = activeRecords.getSubSet(new RecordFilter(RiskAddtlExposureFields.CLOSING_TRANS_LOG_ID,
            policyHeader.getLastTransactionInfo().getTransactionLogId()));
        Iterator itor = closedRecords.getRecords();
        while (itor.hasNext()) {
            Record record = (Record) itor.next();
            activeRecords.removeRecord(record, true);
        }
        validateSumPercentageForAddtlPractice(activeRecords);

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid data.");
        }

        l.exiting(getClass().getName(), "validateAllRiskAddtionalExposure");
    }

    /**
     * Check if exposure expiring date can be changed in OOSE.
     *
     * @param inputRecord
     * @return boolean
     */
    protected boolean validateExpChangeDateAllowed(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateExpChangeDateAllowed", new Object[]{inputRecord,});
        }

        inputRecord.setFieldValue("ooseExpDate", policyHeader.getTermEffectiveToDate());
        boolean isOoseChangeDateAllowed = YesNoFlag.getInstance(getRiskAddtlExposureDAO().isOoseChangeDateAllowed(inputRecord)).booleanValue();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateExpChangeDateAllowed", Boolean.valueOf(isOoseChangeDateAllowed));
        }
        return isOoseChangeDateAllowed;
    }

    private boolean isIntegerValue(String str) {
        BigDecimal bd=new BigDecimal(str);
        return (bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0)
            && bd.intValue()>=1 && bd.intValue() < 100;
    }

    private void validateSumPercentageForAddtlPractice(RecordSet inputRecords) {
        //construct a sorted date set
        SortedSet dateSet = new TreeSet();
        Iterator itor = inputRecords.getRecords();
        while (itor.hasNext()) {
            Record record = (Record) itor.next();
            Date effDate = DateUtils.parseDate(RiskAddtlExposureFields.getEffectiveFromDate(record));
            Date expDate = DateUtils.parseDate(RiskAddtlExposureFields.getEffectiveToDate(record));
            dateSet.add(effDate);
            dateSet.add(expDate);
        }

        boolean hasErrorB = false;
        String timePeriodString = "";

        //dateArray is a sorted date array .
        Date[] dateArray = (Date[]) (dateSet.toArray(new Date[0]));
        List rsList = new ArrayList();

        RecordSet ooseRecords = inputRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.REQUEST));
        if(ooseRecords.getSize()>0){
            //Validation in case of OOSE
            Iterator it = ooseRecords.getRecords();
            while (it.hasNext()) {
                Record ooser = (Record) it.next();
                Date effDate = DateUtils.parseDate(RiskAddtlExposureFields.getEffectiveFromDate(ooser));
                Date expDate = DateUtils.parseDate(RiskAddtlExposureFields.getEffectiveToDate(ooser));
                for (int i = 0; i < dateArray.length - 1; i++) {
                    if ((!effDate.after(dateArray[i])) && (!expDate.before(dateArray[i + 1]))) {
                        RecordSet rs = new RecordSet();
                        Record summaryRecord = rs.getSummaryRecord();
                        summaryRecord.setFieldValue("beginDate", dateArray[i]);
                        summaryRecord.setFieldValue("endDate", dateArray[i + 1]);
                        RiskAddtlExposureFields.setOfficialRecordId(summaryRecord, RiskAddtlExposureFields.getOfficialRecordId(ooser));
                        RiskAddtlExposureFields.setPercentPractice(summaryRecord, RiskAddtlExposureFields.getPercentPractice(ooser));
                        itor = inputRecords.getRecords();
                        while (itor.hasNext()) {
                            Record record = (Record) itor.next();
                            effDate = DateUtils.parseDate(RiskAddtlExposureFields.getEffectiveFromDate(record));
                            expDate = DateUtils.parseDate(RiskAddtlExposureFields.getEffectiveToDate(record));
                            //if this record contains the time period
                            if (!effDate.after(dateArray[i]) && !expDate.before(dateArray[i + 1])
                                && !PMCommonFields.getRecordModeCode(record).isRequest()) {
                                rs.addRecord(record);
                            }
                        }
                        rsList.add(rs);
                    }
                }
            }

            //loop per time period
            itor = rsList.iterator();
            while (itor.hasNext()) {
                RecordSet rs = (RecordSet) itor.next();
                Record summary = rs.getSummaryRecord();
                if (rs.getSize() > 1) {
                    Iterator rsItor = rs.getRecords();
                    double totalPracticePercent = 0;
                    double officialRecordId = new Double(RiskAddtlExposureFields.getOfficialRecordId(summary)).doubleValue();
                    double percentOfPracticeRequest = RiskAddtlExposureFields.getPercentPractice(summary) == null ?
                        0 : new Double(RiskAddtlExposureFields.getPercentPractice(summary)).doubleValue();
                    double percentOfPracticeOfficial = 0;
                    while (rsItor.hasNext()) {
                        Record record = (Record) rsItor.next();
                        String pencentPractice = RiskAddtlExposureFields.getPercentPractice(record);
                        pencentPractice = pencentPractice == null ? "0" : pencentPractice;
                        double riskAddtlExposureId = new Double(RiskAddtlExposureFields.getRiskAddtlExposureId(record)).doubleValue();
                        totalPracticePercent += new Double(pencentPractice).doubleValue();
                        if(officialRecordId == riskAddtlExposureId){
                            percentOfPracticeOfficial = new Double(pencentPractice).doubleValue();
                        }
                    }

                    totalPracticePercent = totalPracticePercent + percentOfPracticeRequest - percentOfPracticeOfficial;

                    if (totalPracticePercent >= 100) {
                        if(hasErrorB){
                            timePeriodString += " , ";
                        }
                        Date effDate = (Date)summary.getFieldValue("beginDate");
                        Date expDate = (Date)summary.getFieldValue("endDate");
                        timePeriodString += DateUtils.formatDate(effDate) + " ~ " + DateUtils.formatDate(expDate);
                        hasErrorB = true;
                    }
                }
            }
        }else{
            for (int i = 0; i < dateArray.length - 1; i++) {
                RecordSet rs = new RecordSet();
                Record summaryRecord = rs.getSummaryRecord();
                summaryRecord.setFieldValue("beginDate", dateArray[i]);
                summaryRecord.setFieldValue("endDate", dateArray[i + 1]);
                itor = inputRecords.getRecords();
                while (itor.hasNext()) {
                    Record record = (Record) itor.next();
                    Date effDate = DateUtils.parseDate(RiskAddtlExposureFields.getEffectiveFromDate(record));
                    Date expDate = DateUtils.parseDate(RiskAddtlExposureFields.getEffectiveToDate(record));
                    //if this record contains the time period
                    if ((!effDate.after(dateArray[i])) && (!expDate.before(dateArray[i + 1]))) {
                        rs.addRecord(record);
                    }
                }
                rsList.add(rs);
            }

            //loop per time period
            itor = rsList.iterator();
            while (itor.hasNext()) {
                RecordSet rs = (RecordSet) itor.next();
                if (rs.getSize() > 1) {
                    Iterator rsItor = rs.getRecords();
                    double totalPracticePercent = 0;
                    while (rsItor.hasNext()) {
                        Record record = (Record) rsItor.next();
                        String pencentPractice = AffiliationFields.getPercentPractice(record);
                        pencentPractice = pencentPractice == null ? "0" : pencentPractice;
                        totalPracticePercent += new Double(pencentPractice).doubleValue();
                    }

                    if (totalPracticePercent >= 100) {
                        if(hasErrorB){
                            timePeriodString += " , ";
                        }
                        Record summaryRecord = rs.getSummaryRecord();
                        Date effDate = (Date)summaryRecord.getFieldValue("beginDate");
                        Date expDate = (Date)summaryRecord.getFieldValue("endDate");
                        timePeriodString += DateUtils.formatDate(effDate) + " ~ " + DateUtils.formatDate(expDate);
                        hasErrorB = true;
                    }
                }
            }
        }

        if(hasErrorB)
            MessageManager.getInstance().addErrorMessage("pm.maintainRiskAddtlExposure.overLapForTotalPractice.error",new String[]{timePeriodString});
    }

    /**
     * Check if Change option is available
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with the selected risk additional exposure record.
     */
    public void validateForOoseRiskAddtlExposure(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForOoseRiskAddtlExposure", new Object[]{policyHeader, inputRecord});
        }
        String recordModeCode = "";
        if (inputRecord.hasStringValue("recordModeCode")) {
            recordModeCode = inputRecord.getStringValue("recordModeCode");
        }
        if (policyHeader.getScreenModeCode().isOosWIP() && !"TEMP".equalsIgnoreCase(recordModeCode)) {
            // check if Change option is available or not
            YesNoFlag isChangeAvailable = YesNoFlag.N;

            String sTransEffDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
            Date transEffDate = DateUtils.parseDate(sTransEffDate);

            Record record = new Record();
            RiskFields.setRiskEffectiveFromDate(record, RiskFields.getRiskEffectiveFromDate(inputRecord));
            RiskFields.setRiskBaseRecordId(record, RiskAddtlExposureFields.getRiskBaseRecordId(inputRecord));
            boolean isOosRiskValid = YesNoFlag.getInstance(getRiskDAO().isOosRiskValid(record)).booleanValue();

            // 1. If the term displayed is the term in which the OOS transaction was initiated
            // 2. If Effective from date of the row is <= the OOS transaction effective date
            // 3. If the query from Pm_Valid_Oos_Risk returns a 'Y'
            String sRiskAddtlExposureEffDate = RiskAddtlExposureFields.getEffectiveFromDate(inputRecord);
            Date riskAddtlExposureEffDate = DateUtils.parseDate(sRiskAddtlExposureEffDate);
            if (policyHeader.isInitTermB() && !transEffDate.before(riskAddtlExposureEffDate) && isOosRiskValid) {
                isChangeAvailable = YesNoFlag.Y;
            }

            if (!isChangeAvailable.booleanValue()) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRiskAddtlExposure.ooseRiskAddtlExposure.changeOption.error");
                throw new ValidationException();
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForOoseRiskAddtlExposure");
        }
    }

    /**
     * Get initial values for OOSE risk additional exposure
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values
     */
    public Record getInitialValuesForOoseRiskAddtlExposure(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForOoseRiskAddtlExposure", new Object[]{policyHeader, inputRecord});
        }
        Record outputRecord = new Record();

        // Set Default values
        // riskAddtlExposureId
        Long lRiskAddtlExposureId = getDbUtilityManager().getNextSequenceNo();
        RiskAddtlExposureFields.setRiskAddtlExposureId(outputRecord, String.valueOf(lRiskAddtlExposureId.longValue()));
        RiskAddtlExposureFields.setRiskAddtlExpBaseRecordId(outputRecord, RiskAddtlExposureFields.getRiskAddtlExpBaseRecordId(inputRecord));
        RiskAddtlExposureFields.setRiskBaseRecordId(outputRecord, policyHeader.getRiskHeader().getRiskBaseRecordId());
        // officialRecordId
        RiskAddtlExposureFields.setOfficialRecordId(outputRecord, RiskAddtlExposureFields.getRiskAddtlExposureId(inputRecord));
        // recordModeCode
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.REQUEST);
        // afterImageRecordB
        RiskAddtlExposureFields.setAfterImageRecordB(outputRecord, YesNoFlag.Y);
        // transactionCode
        TransactionFields.setTransactionCode(outputRecord, policyHeader.getLastTransactionInfo().getTransactionCode());
        // EffectiveFromeDate
        String practiceEffFromDate = null;
        String changeType = inputRecord.getStringValue("changeType");
        if (changeType.equals("changePracticeValue")) {
            practiceEffFromDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        }
        else if (changeType.equals("changePracticeDate")) {
            practiceEffFromDate = RiskAddtlExposureFields.getEffectiveFromDate(inputRecord);
        }
        RiskAddtlExposureFields.setEffectiveFromDate(outputRecord, practiceEffFromDate);

        // EffectiveToDate
        RiskAddtlExposureFields.setEffectiveToDate(outputRecord, RiskAddtlExposureFields.getEffectiveToDate(inputRecord));

        // Set other columns same as the original outputRecord for which Change was invoked
        // set inputRecords into outputRecord
        outputRecord.setFields(inputRecord, false);

        // Set Change option to invisible for the new outputRecord
        outputRecord.setFieldValue("isChgPracticeValueAvailable", YesNoFlag.N);
        outputRecord.setFieldValue("isChgPracticeDateAvailable", YesNoFlag.N);
        // Set Delete option to visible for the new outputRecord
        outputRecord.setFieldValue("isDeleteAvailable", YesNoFlag.Y);

        // Setup intial row style
        RiskAddtlExposureRowStyleRecordLoadprocessor.setInitialEntitlementValuesForRowStyle(outputRecord);

        // Setup percent of practice/practice EffToDate/coverage limit code fields disable or enable according with changeType
        if (changeType.equals("changePracticeValue")) {
            // enable percent of practice/practice EffToDate/coverage limit code fields
            outputRecord.setFieldValue("isEffectiveToDateEditable", YesNoFlag.Y);
            outputRecord.setFieldValue("isPercentPracticeEditable", YesNoFlag.Y);
            outputRecord.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.Y);
        }
        else if (changeType.equals("changePracticeDate")) {
            // disable percent of practice/coverage limit code
            outputRecord.setFieldValue("isPercentPracticeEditable", YesNoFlag.N);
            outputRecord.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.N);
            // enable practice EffToDate field
            outputRecord.setFieldValue("isEffectiveToDateEditable", YesNoFlag.Y);
        }

        //set premium effective date which will be used to get premium class codes
        String premClassEffDt = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_PREM_CLASS_EFF_DT);
        if (StringUtils.isBlank(premClassEffDt) || premClassEffDt.equals("RISK_EFF_DT")) {
            outputRecord.setFieldValue("premClassEffectiveDate", policyHeader.getRiskHeader().getRiskEffectiveToDate());
        }
        else if (premClassEffDt.equals("TERM_EFF_DT")) {
            outputRecord.setFieldValue("premClassEffectiveDate", policyHeader.getTermEffectiveFromDate());
        }
        else if (premClassEffDt.equals("TRANS_EFF_DT")) {
            outputRecord.setFieldValue("premClassEffectiveDate",
                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }

        RiskAddtlExposureFields.setOrigCoverageLimitCode(outputRecord, null);
        RiskAddtlExposureFields.setOrigEffectiveToDate(outputRecord, null);
        RiskAddtlExposureFields.setOrigPercentPractice(outputRecord, null);
        RiskAddtlExposureFields.setPercentPractice(outputRecord, null);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForOoseRiskAddtlExposure", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Load all the policy term's Additional Exposure records, it will be called by Policy Inquiry Service.
     * @param policyHeader
     * @param filterInsured
     *
     * @return
     */
    public RecordSet loadAllRiskAddtlExposureForWS(PolicyHeader policyHeader, String filterInsured) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskAddtlExposureForWS", new Object[]{policyHeader, filterInsured});
        }

        Record inputRecord = policyHeader.toRecord();
        RecordMode recordModeCode = RecordMode.TEMP;
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (viewMode.isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }

        PMCommonFields.setRecordModeCode(inputRecord, recordModeCode);
        RiskAddtlExposureFields.setLatestTermExpDate(inputRecord, getLatestTermExpDate(policyHeader));
        if (filterInsured != null) {
            inputRecord.setFieldValue(RiskFields.RISK_BASE_RECORD_ID, filterInsured);
        }

        RecordLoadProcessor loadProcessor = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;
        RecordSet rs = getRiskAddtlExposureDAO().loadAllRiskAddtlExposure(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskAddtlExposureForWS", rs);
        }

        return rs;
    }

    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{RiskAddtlExposureFields.EFFECTIVE_TO_DATE, RiskAddtlExposureFields.PERCENT_PRACTICE,
            RiskAddtlExposureFields.COVERAGE_LIMIT_CODE});

    public RiskAddtlExposureDAO getRiskAddtlExposureDAO() {
        return m_riskAddtlExposureDAO;
    }

    public void setRiskAddtlExposureDAO(RiskAddtlExposureDAO riskAddtlExposureDAO) {
        m_riskAddtlExposureDAO = riskAddtlExposureDAO;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    public RiskDAO getRiskDAO() {
        return m_riskDAO;
    }

    public void setRiskDAO(RiskDAO riskDAO) {
        m_riskDAO = riskDAO;
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    private RiskAddtlExposureDAO m_riskAddtlExposureDAO;

    private RiskDAO m_riskDAO;

    private RiskManager m_riskManager;

    private DBUtilityManager m_dbUtilityManager;

    private static final String OPEN_DATE = "01/01/3000";

    protected static final String ROW_STATUS = "rowStatus";
    protected static final String NEW = "NEW";
    protected static final String MODIFIED = "MODIFIED";
    protected static final String OFFICIAL = "OFFICIAL";
    protected static final String NULL = "NULL";
}
