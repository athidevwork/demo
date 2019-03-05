package dti.pm.coveragemgr.prioractmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordComparator;
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
import dti.pm.busobjs.ComponentOwner;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.core.http.RequestIds;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageHeader;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.coveragemgr.prioractmgr.PriorActFields;
import dti.pm.coveragemgr.prioractmgr.PriorActManager;
import dti.pm.coveragemgr.prioractmgr.dao.PriorActDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of PriorActManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 29, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/23/2008       sxm         Issue 86798 - fix validation rule for risk effective period.
 * 06/10/2011       wqfu        103799 - Added logic to handle copy prior act stats.
 * 08/30/2011       ryzhao      124458 - Modified validatePriorActCoverageDate, validatePriorActComponentDate,
 *                                       and validateAllPriorAct to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 08/31/2011       ryzhao      124458 - Remove DateUtils.formatDate() and call FormatUtils.formatDateForDisplay() directly.
 * 01/08/2013       fcb         137981 - changes related to Pm_Dates modifications.
 * 06/10/2014       adeng       154011 - 1)Modified loadAllPriorActRisk() to filter official row for end quote.
 *                                       2)Modified validateAllPriorAct() to set the correct row number into validation
 *                                       error message.
 * 12/22/2014       fcb         157919 - getCommonValues: added additional logic for setting the retro date.
 * 01/29/2015       xnie        160614 - Modified getCommonValues to set retro date with getPriorActsRetroDate of DAO.
 * 06/26/2015       tzeng       163856 - Modified saveAllPriorActRiskAndCoverage() to make the new prior coverage to
 *                                       avoid inserting the same base record fk record as main coverage when its
 *                                       issue state created in difference.
 * 08/10/2015       tzeng       164420 - 1. Modified loadAllPriorActRisk(), loadAllPriorActCoverage() and remove
 *                                       BASE_COVERAGE_PRACTICE_STATE_CODE_MAP variable to delete relation code of
 *                                       BaseCoveragePracticeStateCodeMap.
 *                                       2. Modified saveAllPriorActRiskAndCoverage() to set main coverage base record
 *                                       id on all prior coverage.
 * 08/20/2015       ssheng      165340 - Use Common Function PolicyHeader.getRecordMode to replace calcuate recordModeCode
 *                                       via screenMode.
 * 08/26/2016       wdang       167534 - Added isEditableForRenewalQuote.
 * 07/31/2018       xnie        187493 - Modified validateAllPriorAct() to get risk start date from risk header's contig
 *                                       risk effective date instead of risk start date in inputRecord because it is by
 *                                       term.
 * ---------------------------------------------------
 */
public class PriorActManagerImpl implements PriorActManager {

    /**
     * Load all prior act risk
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPriorActRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorActRisk", new Object[]{policyHeader, inputRecord});
        }

        RecordSet rs;
        PriorActRiskRecordLoadProcessor priorActRiskRecordLoadProcessor =
            PriorActRiskRecordLoadProcessor.getInstance(inputRecord, policyHeader);
        //set record mode code
        RecordMode priorActRecordMode = getPriorActRecordMode(policyHeader);
        inputRecord.setFieldValue(PMCommonFields.RECORD_MODE_CODE, priorActRecordMode);
        //set policy header fields
        inputRecord.setFields(policyHeader.toRecord(), false);
        CoverageFields.setCoverageBaseRecordId(inputRecord, policyHeader.getCoverageHeader().getCoverageBaseRecordId());
        //set up load processor
        RecordLoadProcessor processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            priorActRiskRecordLoadProcessor,
            PriorActRiskEntitlementRecordLoadProcessor.getInstance(inputRecord, policyHeader, this));
        processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(processor, origRiskFieldLoadProcessor);

        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor = new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "riskId");
        processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(processor, endquoteLoadProcessor);

        //get prior acts risk recordset
        if (inputRecord.hasStringValue(RequestIds.IS_COPY_ACTS_STATS) &&
            YesNoFlag.getInstance(inputRecord.getStringValue(RequestIds.IS_COPY_ACTS_STATS)).booleanValue()) {
            rs = getPriorActDAO().loadAllPendPriorActRisk(inputRecord, processor);
            Iterator iter = rs.getRecords();
            while (iter.hasNext()) {
                Record record = (Record)iter.next();
                record.setUpdateIndicator(UpdateIndicator.INSERTED);
            }
        } else {
            rs = getPriorActDAO().loadAllPriorActRisk(inputRecord, processor);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPriorActRisk", rs);
        }

        return rs;

    }

    /**
     * Load all prior act coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPriorActCoverage(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorActCoverage", new Object[]{policyHeader, inputRecord});
        }

        RecordSet rs;

        //set up load processor
        RecordLoadProcessor processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            PriorActCoverageRecordLoadProcessor.getInstance(inputRecord, policyHeader),
            PriorActCoverageEntitlementRecordLoadProcessor.getInstance(inputRecord, policyHeader, this));

        processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(processor, origCoverageFieldLoadProcessor);

        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor = new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "coverageId");
        processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(processor, endquoteLoadProcessor);

        //record mode code and policyHeader fields have been set when loading prior acts risks,
        //No need to set here
        //get prior acts coverage recordset
        if (inputRecord.hasStringValue(RequestIds.IS_COPY_ACTS_STATS) &&
            YesNoFlag.getInstance(inputRecord.getStringValue(RequestIds.IS_COPY_ACTS_STATS)).booleanValue()) {
            rs = getPriorActDAO().loadAllPendPriorActCovg(inputRecord, processor);
            Iterator iter = rs.getRecords();
            while (iter.hasNext()) {
                Record record = (Record)iter.next(); 
                record.setUpdateIndicator(UpdateIndicator.INSERTED);
            }
        } else {
            rs = getPriorActDAO().loadAllPriorActCoverage(inputRecord, processor);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPriorActCoverage", rs);
        }

        return rs;
    }


    /**
     * get recordmode from the current view mode
     *
     * @param policyHeader
     * @return recordmode code
     */
    protected RecordMode getPriorActRecordMode(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPriorActRecordMode", new Object[]{policyHeader});
        }
        RecordMode recordMode = policyHeader.getRecordMode();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPriorActRecordMode", recordMode);
        }

        return recordMode;
    }

    /**
     * validate prior acts break
     *
     * @param policyHeader
     * @param inputRecord
     */
    protected void validatePriorActsBreak(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePriorActsBreak", new Object[]{policyHeader, inputRecord});
        }

        //judge if there is prior acts break exists
        inputRecord.setFields(policyHeader.toRecord(), false);
        boolean isBreak = getPriorActDAO().isPriorActsBreak(inputRecord);
        if (isBreak) {
            MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.priorActsBreak.error");
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Prior Acts breaks");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePriorActsBreak");
        }

    }


    /**
     * save all prior act risk and coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @param riskRs
     * @param covgRs
     * @return
     */
    public int saveAllPriorActRiskAndCoverage(PolicyHeader policyHeader, Record inputRecord, RecordSet riskRs, RecordSet covgRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPriorActRiskAndCoverage", new Object[]{policyHeader, inputRecord, riskRs, covgRs});
        }
        int processCount = 0;

        riskRs.setFieldsOnAll(inputRecord, false);
        covgRs.setFieldsOnAll(inputRecord, false);

        CoverageHeader coverageHeader = policyHeader.getCoverageHeader();
        covgRs.setFieldValueOnAll(CoverageFields.COVERAGE_BASE_RECORD_ID, coverageHeader.getCoverageBaseRecordId());

        //delete prior acts risk data
        RecordSet deletedRiskRs = riskRs.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        if (deletedRiskRs.getSize() > 0) {
            processCount += getPriorActDAO().deleteAllPriorActRisk(deletedRiskRs);
        }
        //save prior acts risk data
        RecordSet changedRiskRs = PMRecordSetHelper.setRowStatusOnModifiedRecords(riskRs);
        RecordSet changedTempRiskRs = changedRiskRs.getSubSet(
            new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet changedOfficialRiskRs = changedRiskRs.getSubSet(
            new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
        if (changedTempRiskRs.getSize() > 0)
            processCount += getPriorActDAO().saveAllTempPriorActRisk(changedTempRiskRs);
        if (changedOfficialRiskRs.getSize() > 0)
            processCount += getPriorActDAO().saveAllOfficialPriorActRisk(changedOfficialRiskRs);

        //delete prior acts coverage data
        RecordSet deletedCovgRs = covgRs.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        if (deletedCovgRs.getSize() > 0) {
            processCount += getPriorActDAO().deleteAllPriorActCoverage(deletedCovgRs);
        }
        //save prior acts coverage data
        RecordSet changedCovgRs = PMRecordSetHelper.setRowStatusOnModifiedRecords(covgRs);
        RecordSet changedTempCovgRs = changedCovgRs.getSubSet(
            new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet changedOfficialCovgRs = changedCovgRs.getSubSet(
            new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
        if (changedTempCovgRs.getSize() > 0){
            RecordSet newTempCovgRs = changedTempCovgRs.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
            if(newTempCovgRs.getSize()>0){
                newTempCovgRs.setFieldValueOnAll(CoverageFields.PRODUCT_COVERAGE_CODE,coverageHeader.getProductCoverageCode());
            }
            processCount += getPriorActDAO().saveAllTempPriorActCoverage(changedTempCovgRs);
        }
        if (changedOfficialCovgRs.getSize() > 0)
            processCount += getPriorActDAO().saveAllOfficialPriorActCoverage(changedOfficialCovgRs);


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPriorActRiskAndCoverage", String.valueOf(processCount));
        }
        return processCount;
    }


    /**
     * save all prior acts data
     *
     * @param policyHeader
     * @param inputRecord
     * @param riskRs
     * @param covgRs
     * @param compRs
     * @return
     */
    public int saveAllPriorAct(PolicyHeader policyHeader, Record inputRecord, RecordSet riskRs, RecordSet covgRs, RecordSet compRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPriorAct", new Object[]{policyHeader, inputRecord, riskRs, covgRs, compRs});
        }

        RecordSet valRiskRs = riskRs.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}))
            .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));
        RecordSet valCovgRs = covgRs.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}))
            .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));
        RecordSet valCompRs = compRs.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}))
            .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));

        boolean skipValidate = false;
        if (valRiskRs.getSize() == 0 && valCovgRs.getSize() == 0 && valCompRs.getSize() == 0) {
            //if delete all records, no need to validate
            skipValidate = true;
        }
        if (!skipValidate) {
            //validate coverage count
            if (valCovgRs.getSize() == 0) {
                validatePriorActCoverageCount(policyHeader, inputRecord);
            }

            //validate prior act data
            validateAllPriorAct(policyHeader, inputRecord, valRiskRs, valCovgRs, valCompRs);
        }
        // First save all inserted/updated Risk and Coverage records
        int processCount = saveAllPriorActRiskAndCoverage(policyHeader, inputRecord, riskRs, covgRs);

        // Second save component records
        // Prepare component owner
        ComponentOwner owner = ComponentOwner.PRIOR_ACT;
        // Save the component changes
        processCount += getComponentManager().saveAllComponent(policyHeader, compRs, owner, covgRs);

        if (!skipValidate) {
            //validate prior acts break
            validatePriorActsBreak(policyHeader, inputRecord);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPriorAct", String.valueOf(processCount));
        }

        return processCount;

    }

    /**
     * validate prior act coverage date
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void validatePriorActCoverageDate(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePriorActCoverageDate", new Object[]{policyHeader, inputRecord});
        }
        //to indicate if the validation is for page ajax invoke
        boolean isToValidate = inputRecord.getBooleanValue("isToValidate", false).booleanValue();

        Date covgRetroDate = DateUtils.parseDate(PriorActFields.getCoverageRetroDate(inputRecord));
        Date coverageEffectiveDate = DateUtils.parseDate(PriorActFields.getCoverageEffectiveDate(inputRecord));
        Date covgEffDate = DateUtils.parseDate(PriorActFields.getCoverageEffectiveFromDate(inputRecord));
        Date covgExpDate = DateUtils.parseDate(PriorActFields.getCoverageEffectiveToDate(inputRecord));
        String rowId = CoverageFields.getCoverageId(inputRecord);

        if (covgEffDate.before(covgRetroDate)) {
            MessageManager.getInstance().addErrorMessage(
                "pm.maintainPriorActs.effDateBeforeRetroDate.error",
                new String[]{FormatUtils.formatDateForDisplay(covgRetroDate)}, CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, rowId);
        }

        if (covgEffDate.after(covgExpDate)) {
            MessageManager.getInstance().addErrorMessage(
                "pm.maintainPriorActs.effDateAfterExpDate.error", new String[]{FormatUtils.formatDateForDisplay(covgExpDate)}
                , CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, rowId);
        }

        if (covgExpDate.after(coverageEffectiveDate)) {
            MessageManager.getInstance().addErrorMessage(
                "pm.maintainPriorActs.expDateAfterCovgEff.error", new String[]{FormatUtils.formatDateForDisplay(coverageEffectiveDate)}
                , CoverageFields.COVERAGE_EFFECTIVE_TO_DATE, rowId);
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() && isToValidate) {
            throw new ValidationException("Prior Acts Coverage date invalid");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePriorActCoverageDate");
        }
    }


    /**
     * validate prior act comopnent date
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void validatePriorActComponentDate(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePriorActComponentDate", new Object[]{policyHeader, inputRecord});
        }

        //to indicate if the validation is for page ajax invoke
        boolean isToValidate = inputRecord.getBooleanValue("isToValidate", false).booleanValue();


        Date covgEffDate = DateUtils.parseDate(CoverageFields.getCoverageEffectiveFromDate(inputRecord));
        Date covgExpDate = DateUtils.parseDate(CoverageFields.getCoverageEffectiveToDate(inputRecord));
        Date compEffDate = DateUtils.parseDate(ComponentFields.getEffectiveFromDate(inputRecord));
        Date compExpDate = DateUtils.parseDate(ComponentFields.getEffectiveToDate(inputRecord));

        if (compEffDate.before(covgEffDate)) {
            MessageManager.getInstance().addErrorMessage(
                "pm.maintainPriorActs.compEffBeforCovgEff.error", new String[]{FormatUtils.formatDateForDisplay(covgEffDate)});
        }

        if (compExpDate.after(covgExpDate)) {
            MessageManager.getInstance().addErrorMessage(
                "pm.maintainPriorActs.compExpAfterCovgExp.error", new String[]{FormatUtils.formatDateForDisplay(covgExpDate)});
        }

        if (compExpDate.before(compEffDate)) {
            MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.compEffAfterCompExp.error");
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() && isToValidate) {
            throw new ValidationException("Prior Acts Component date invalid");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePriorActComponentDate");
        }

    }

    /**
     * validate all prior acts data
     *
     * @param policyHeader
     * @param inputRecord
     * @param riskRs
     * @param covgRs
     * @param compRs
     */
    public void validateAllPriorAct(PolicyHeader policyHeader, Record inputRecord, RecordSet riskRs, RecordSet covgRs, RecordSet compRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllPriorAct", new Object[]{policyHeader, inputRecord, riskRs, covgRs, compRs});
        }

        // Set the displayRecordNumber to all visible records.
        riskRs = PMRecordSetHelper.setDisplayRecordNumberOnRecords(riskRs);
        covgRs = PMRecordSetHelper.setDisplayRecordNumberOnRecords(covgRs);

        //get common values from request
        String minNoseDate = PriorActFields.getMinimalNoseDate(inputRecord);
        String covgRetroDate = PriorActFields.getCoverageRetroDate(inputRecord);
        String termEff = policyHeader.getTermEffectiveFromDate();
        String riskStart = policyHeader.getRiskHeader().getContigRiskEffectiveDate();
        String coverageStart = PriorActFields.getCoverageStartDate(inputRecord);


        Date firstRiskEff = DateUtils.parseDate(riskStart);
        Date lastRiskExp = DateUtils.parseDate(covgRetroDate);
        //validate prior acts risk data
        Iterator riskIter = riskRs.getRecords();
        while (riskIter.hasNext()) {
            Record riskRec = (Record) riskIter.next();
            String rowId = RiskFields.getRiskId(riskRec);
            String rowNum = String.valueOf(riskRec.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));

            //validate required
            if (!riskRec.hasStringValue(PriorActFields.PRACTICE_STATE_CODE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.practiceStateRequired.error",
                    new String[]{rowNum}, PriorActFields.PRACTICE_STATE_CODE, rowId);
            }
            if (!riskRec.hasStringValue(PriorActFields.RISK_COUNTY_CODE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.countyRequired.error",
                    new String[]{rowNum}, PriorActFields.RISK_COUNTY_CODE, rowId);
            }

            if (!riskRec.hasStringValue(PriorActFields.SPECIALTY)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.specialtyRequired.error",
                    new String[]{rowNum}, PriorActFields.SPECIALTY, rowId);
            }

            //set first risk eff and last risk exp date
            String riskEff = PriorActFields.getRiskEffectiveFromDate(riskRec);
            String riskExp = PriorActFields.getRiskEffectiveToDate(riskRec);
            Date riskEffDate = DateUtils.parseDate(riskEff);
            Date riskExpDate = DateUtils.parseDate(riskExp);
            if (firstRiskEff == null || riskEffDate.before(firstRiskEff)) {
                firstRiskEff = riskEffDate;
            }
            if (lastRiskExp == null || riskExpDate.after(lastRiskExp)) {
                lastRiskExp = riskExpDate;
            }

            //validate values
            if (DateUtils.parseDate(riskEff).before(DateUtils.parseDate(covgRetroDate))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.riskEffDateBeforeRetroDate.error"
                    , new String[]{FormatUtils.formatDateForDisplay(covgRetroDate)}, RiskFields.RISK_EFFECTIVE_FROM_DATE, rowId);
            }

            if (DateUtils.parseDate(riskEff).before(DateUtils.parseDate(minNoseDate))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.riskEffDateBeforeMinDate.error"
                    , new String[]{FormatUtils.formatDateForDisplay(minNoseDate)}, RiskFields.RISK_EFFECTIVE_FROM_DATE, rowId);
            }

            if (DateUtils.parseDate(riskEff).after(DateUtils.parseDate(riskExp))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.riskEffDateAfterExpDate.error"
                    , new String[]{FormatUtils.formatDateForDisplay(riskExp)}, RiskFields.RISK_EFFECTIVE_FROM_DATE, rowId);
            }

            if (DateUtils.parseDate(riskExp).after(DateUtils.parseDate(riskStart))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.riskExpDateAfterTermExp.error"
                    , new String[]{FormatUtils.formatDateForDisplay(riskStart)}, RiskFields.RISK_EFFECTIVE_TO_DATE, rowId);
            }

            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("prior act risk data invalid");
            }

        }

        if (lastRiskExp.before(DateUtils.parseDate(riskStart))) {
            MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.riskLastPeriodMissing.error",
                new String[]{FormatUtils.formatDateForDisplay(lastRiskExp), FormatUtils.formatDateForDisplay(riskStart)});
        }
        else if (firstRiskEff.after(DateUtils.parseDate(covgRetroDate))) {
            MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.riskFirstPeriodMissing.error",
                new String[]{FormatUtils.formatDateForDisplay(covgRetroDate), FormatUtils.formatDateForDisplay(firstRiskEff)});
        }

        // exclude the flat records
        RecordSet validateRiskRs = new RecordSet();
        riskIter = riskRs.getRecords();
        while (riskIter.hasNext()) {
            Record r = (Record) riskIter.next();
            if (!RiskFields.getRiskEffectiveFromDate(r).equals(RiskFields.getRiskEffectiveToDate(r))) {
                validateRiskRs.addRecord(r);
            }
        }

        if (validateRiskRs.getSize() > 1) {
            //validate prior act risk overlap/gap
            String messageKey = "pm.maintainPriorActs.riskOverlap.error";
            ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
                RiskFields.RISK_EFFECTIVE_FROM_DATE, RiskFields.RISK_EFFECTIVE_TO_DATE,
                RiskFields.RISK_ID,
                messageKey);
            continuityValidator.validate(riskRs);

            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("prior act risk data invalid");
            }

            messageKey = "pm.maintainPriorActs.riskGap.error";
            continuityValidator = new ContinuityRecordSetValidator(
                RiskFields.RISK_EFFECTIVE_FROM_DATE, RiskFields.RISK_EFFECTIVE_TO_DATE,
                RiskFields.RISK_ID,
                messageKey, true);
            continuityValidator.validate(riskRs);
        }


        Date firstCovgEff = DateUtils.parseDate(coverageStart);
        Date lastCovgExp = DateUtils.parseDate(covgRetroDate);
        //validate prior acts coverage data
        Iterator covgIter = covgRs.getRecords();
        while (covgIter.hasNext()) {
            Record covgRec = (Record) covgIter.next();
            String rowId = CoverageFields.getCoverageId(covgRec);
            String rowNum = String.valueOf(covgRec.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));

            if (!covgRec.hasStringValue(PriorActFields.COVERAGE_LIMIT_CODE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.coverageLimitRequired.error"
                    , new String[]{rowNum}, PriorActFields.COVERAGE_LIMIT_CODE, rowId);
            }

            //set first coverage eff and last coverage exp date
            String covgEff = CoverageFields.getCoverageEffectiveFromDate(covgRec);
            String covgExp = CoverageFields.getCoverageEffectiveToDate(covgRec);
            Date covgEffDate = DateUtils.parseDate(covgEff);
            Date covgExpDate = DateUtils.parseDate(covgExp);
            if (firstCovgEff == null || covgEffDate.before(firstCovgEff)) {
                firstCovgEff = covgEffDate;
            }
            if (lastCovgExp == null || covgExpDate.after(lastCovgExp)) {
                lastCovgExp = covgExpDate;
            }

            //validate coverage date fields
            Record valRec = new Record();
            valRec.setFields(covgRec);
            valRec.setFields(inputRecord, false);
            validatePriorActCoverageDate(policyHeader, valRec);
        }

        if (lastCovgExp.before(DateUtils.parseDate(coverageStart))) {
            MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.covgLastPeriodMissing.error",
                new String[]{FormatUtils.formatDateForDisplay(lastCovgExp), FormatUtils.formatDateForDisplay(coverageStart)});
        }
        else if (firstCovgEff.after(DateUtils.parseDate(covgRetroDate)) &&
            !YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_ALLOW_RD_GAP)).booleanValue()) {
            MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.covgFirstPeriodMissing.error",
                new String[]{FormatUtils.formatDateForDisplay(covgRetroDate), FormatUtils.formatDateForDisplay(firstCovgEff)});
        }

        if (covgRs.getSize() > 0) {
            //validate prior act coverage overlap/gap
            String messageKey = "pm.maintainPriorActs.covgOverlap.error";
            ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
                CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, CoverageFields.COVERAGE_EFFECTIVE_TO_DATE,
                CoverageFields.COVERAGE_ID,
                messageKey);
            continuityValidator.validate(covgRs);

            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("prior act coverage data invalid");
            }

            messageKey = "pm.maintainPriorActs.covgGap.error";
            continuityValidator = new ContinuityRecordSetValidator(
                CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, CoverageFields.COVERAGE_EFFECTIVE_TO_DATE,
                CoverageFields.COVERAGE_ID,
                messageKey, true);
            continuityValidator.validate(covgRs);
        }


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllPriorAct");
        }
    }


    /**
     * validate prior act coverage count
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void validatePriorActCoverageCount(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePriorActCoverageCount", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);
        int covgCount = getPriorActDAO().getPriorActRiskCoverageCount(inputRecord);
        if (covgCount == 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.noCoverageInfo.error");
            throw new ValidationException("coverage infomation must be entered");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePriorActCoverageCount");
        }

    }

    /**
     * get common values for Prior Acts' retreaval
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public Record getCommonValues(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCommonValues", new Object[]{policyHeader, inputRecord});
        }

        Record commonValues = new Record();

        Date termExp = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());
        Date riskExp = DateUtils.parseDate(PriorActFields.getRiskEffectiveToDate(inputRecord));

        //set risk start date
        String riskStartDate = getPriorActDAO().getRiskStartDate(inputRecord);
        PriorActFields.setRiskStartDate(commonValues, riskStartDate);

        //set risk expiration date
        if (riskExp.before(termExp)) {
            PriorActFields.setRiskExpirationDate(commonValues, PriorActFields.getRiskEffectiveToDate(inputRecord));
        }
        else {
            PriorActFields.setRiskExpirationDate(commonValues, policyHeader.getTermEffectiveToDate());
        }

        //set coverage start date
        String covgStartDate = getPriorActDAO().getCoverageStartDate(inputRecord);
        PriorActFields.setCoverageStartDate(commonValues, covgStartDate);

        //set coverage effective date
        if (!StringUtils.isBlank(covgStartDate)) {
            PriorActFields.setCoverageEffectiveDate(commonValues, covgStartDate);
        }
        else {
            PriorActFields.setCoverageEffectiveDate(commonValues, CoverageFields.getCoverageBaseEffectiveFromDate(inputRecord));
        }

        //set coverage retro date
        String covgRetroDate = getPriorActDAO().getPriorActsRetroDate(inputRecord);
        PriorActFields.setCoverageRetroDate(commonValues, covgRetroDate);

        //set product coverage code
        PriorActFields.setCommProductCoverageCode(commonValues, CoverageFields.getProductCoverageCode(inputRecord));

        //minimal nose date
        String minNoseDate = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_MIN_NOSE_DATE);
        PriorActFields.setMinimalNoseDate(commonValues, minNoseDate);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCommonValues", commonValues);
        }

        return commonValues;
    }


    /**
     * To get initial values for a newly inserted Prior Act Risk record
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record with initial vlaues
     */
    public Record getInitialValuesForPriorActRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForPriorActRisk", new Object[]{policyHeader, inputRecord});
        }

        Record output = new Record();

        RiskHeader riskHeader = policyHeader.getRiskHeader();
        Date minimalNoseDate = DateUtils.parseDate(PriorActFields.getMinimalNoseDate(inputRecord));
        Date covgRetroDate = DateUtils.parseDate(PriorActFields.getCoverageRetroDate(inputRecord));

        if (minimalNoseDate.after(covgRetroDate)) {
            RiskFields.setRiskEffectiveFromDate(output, PriorActFields.getMinimalNoseDate(inputRecord));
        }
        else {
            RiskFields.setRiskEffectiveFromDate(output, PriorActFields.getCoverageRetroDate(inputRecord));
        }

        RiskFields.setRiskEffectiveToDate(output, PriorActFields.getCoverageEffectiveDate(inputRecord));
        TransactionFields.setTransactionLogId(output, policyHeader.getLastTransactionId());
        RiskFields.setPracticeStateCode(output, riskHeader.getPracticeStateCode());
        PriorActFields.setRiskCountyCode(output, riskHeader.getRiskCountyCode());
        RiskFields.setRiskBaseRecordId(output, riskHeader.getRiskBaseRecordId());
        RiskFields.setRiskTypeCode(output, riskHeader.getRiskTypeCode());
        RiskFields.setRiskId(output, getDbUtilityManager().getNextSequenceNo().toString());
        PMCommonFields.setRecordModeCode(output, RecordMode.TEMP);
        origRiskFieldLoadProcessor.postProcessRecord(output, false);
        output.setFieldValue("isDelRiskAvailable", YesNoFlag.Y);
        output.setFieldValue("isPracticeStateEditable", YesNoFlag.Y);
        output.setFieldValue("isPriorCarrEditable", YesNoFlag.Y);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForPriorActRisk", output);
        }

        return output;

    }


    /**
     * To get initial values for a newly inserted Prior Act Coverage record
     *
     * @param policyHeader
     * @param inputRecord
     * @param covgRecords
     * @return Record with initial vlaues
     */
    public Record getInitialValuesForPriorActCoverage(PolicyHeader policyHeader, Record inputRecord, RecordSet covgRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForPriorActCoverage", new Object[]{policyHeader, inputRecord, covgRecords});
        }

        Record output = new Record();

        RiskHeader riskHeader = policyHeader.getRiskHeader();
        CoverageHeader covgHeader = policyHeader.getCoverageHeader();
        Date covgRetroDate = DateUtils.parseDate(PriorActFields.getCoverageRetroDate(inputRecord));
        Date coverageEffectiveDate = DateUtils.parseDate(PriorActFields.getCoverageEffectiveDate(inputRecord));
        String practiceStateCode = CoverageFields.getPracticeStateCode(inputRecord);
        covgRecords = covgRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}))
            .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));

        //set covg eff and exp
        if (covgRecords.getSize() == 0) {
            CoverageFields.setCoverageEffectiveFromDate(output, PriorActFields.getCoverageRetroDate(inputRecord));
            CoverageFields.setCoverageEffectiveToDate(output, PriorActFields.getCoverageEffectiveDate(inputRecord));
        }
        else {
            RecordComparator rc = new RecordComparator("coverageEffectiveFromDate", ConverterFactory.getInstance().getConverter(Date.class));
            covgRecords = covgRecords.getSortedCopy(rc);
            Iterator covgIter = covgRecords.getRecords();
            Date covPeriodFrom = null;
            Date covPeriodTo = null;
            Date nextPeriodFrom = null;
            while (covgIter.hasNext()) {
                Record covgRec = (Record) covgIter.next();

                Date covgEffDate = DateUtils.parseDate(CoverageFields.getCoverageEffectiveFromDate(covgRec));
                Date covgExpDate = DateUtils.parseDate(CoverageFields.getCoverageEffectiveToDate(covgRec));
                if (covPeriodFrom == null) {
                    covPeriodFrom = covgEffDate;
                    covPeriodTo = covgExpDate;
                }
                else {

                    if (covPeriodFrom.after(covgRetroDate)) {
                        break;
                    }
                    else if (covgEffDate.after(covPeriodTo)) {
                        nextPeriodFrom = covgEffDate;
                        break;
                    }
                    else {
                        covPeriodTo = covgExpDate;
                    }
                }
            }

            if (covPeriodFrom.after(covgRetroDate)) {
                CoverageFields.setCoverageEffectiveFromDate(output, DateUtils.formatDate(covgRetroDate));
                CoverageFields.setCoverageEffectiveToDate(output, DateUtils.formatDate(covPeriodFrom));
            }
            else {
                CoverageFields.setCoverageEffectiveFromDate(output, DateUtils.formatDate(covPeriodTo));
                if (nextPeriodFrom != null) {
                    CoverageFields.setCoverageEffectiveToDate(output, DateUtils.formatDate(nextPeriodFrom));
                }
                else {
                    CoverageFields.setCoverageEffectiveToDate(output, PriorActFields.getCoverageEffectiveDate(inputRecord));
                }
            }
        }
        //set orignal values
        origCoverageFieldLoadProcessor.postProcessRecord(output, false);
        CoverageFields.setCoverageId(output, getDbUtilityManager().getNextSequenceNo().toString());
        CoverageFields.setCoverageBaseRecordId(output, covgHeader.getCoverageBaseRecordId());
        CoverageFields.setPracticeStateCode(output, practiceStateCode);
        String prdtCovgCode = getPriorActDAO().getProductCoverageCode(inputRecord);
        //if there is no prior acts coverage for parent coverage, throw validation exception
        if (prdtCovgCode == null) {
            MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.noPriorActCoverage.error",
                new String[]{RiskFields.getPracticeStateCode(inputRecord)});
            throw new ValidationException("no prior acts coverage for parent coverage");
        }

        CoverageFields.setProductCoverageCode(output, prdtCovgCode);
        RiskFields.setRiskBaseRecordId(output, riskHeader.getRiskBaseRecordId());
        PMCommonFields.setRecordModeCode(output, RecordMode.TEMP);
        TransactionFields.setTransactionLogId(output, policyHeader.getLastTransactionId());
        output.setFieldValue("isDelCovgAvailable", YesNoFlag.Y);
        //set orignal values
        origCoverageFieldLoadProcessor.postProcessRecord(output, false);
        // Initialize Split retroactive date to selected coverage's retroactive date
        output.setFieldValue("splitRetroDate", inputRecord.getStringValue("coverageRetroDate"));        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForPriorActCoverage", output);
        }

        return output;
    }


    /**
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public void validateForDelete(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForDelete", new Object[]{policyHeader, inputRecord});
        }
        inputRecord.setFields(policyHeader.toRecord(), false);

        int activeCarrierCount = getPriorActDAO().getActiveCarrierCount(inputRecord);

        if (activeCarrierCount > 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.activeCarrierExists.error");
            throw new ValidationException("active carrier exist for other coverage");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForDelete");
        }
    }


    /**
     * get prior coverage count for delete risk page entitlement
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public int getPriorActCoverageCount(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPriorActCoverageCount", new Object[]{policyHeader, inputRecord});
        }

        int covgCount = getPriorActDAO().getPriorActCoverageCount(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPriorActCoverageCount", String.valueOf(covgCount));
        }

        return covgCount;
    }

    /**
     * get the minimal retro data of selected risk
     *
     * @param policyHeader
     * @param inputRecord
     * @return the minimal retro data of selected risk
     */
    public String getMinRetroDate(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMinRetroDate", new Object[]{policyHeader, inputRecord});
        }

        String minRetro = getPriorActDAO().getMinRetroDate(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMinRetroDate", minRetro);
        }

        return minRetro;
    }

    /**
     * Call DAO method to copy prior acts stats
     *
     * @param inputRecord
     * @return
     */
    public void copyPriorActsStats(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyPriorActsStats", inputRecord);
        }

        try {
            getPriorActDAO().copyPriorActsStats(inputRecord);

        } catch (Exception e) {
            throw new AppException("pm.maintainPriorActs.copyStats.error", "Failed to copy prior acts stats.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyPriorActsStats");
        }
    }

    /**
     * Delete pending prior acts
     *
     * @param inputRecord
     * @return
     */
    public void deleteAllPendPriorActs(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllPendPriorActs", inputRecord);
        }

        getPriorActDAO().deleteAllPendPriorActs(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllPendPriorActs");
        }
    }


    @Override
    public boolean isEditableForRenewalQuote(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isEditableForRenewalQuote", new Object[]{policyHeader});
        }

        Record record = new Record();
        CoverageFields.setCoverageBaseRecordId(record,
            policyHeader.getCoverageHeader().getCoverageBaseRecordId());
        boolean isPriorActsEditable = getPriorActDAO().isEditableForRenewalQuote(record);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isEditableForRenewalQuote", isPriorActsEditable);
        }
        return isPriorActsEditable;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getPriorActDAO() == null)
            throw new ConfigurationException("The required property 'priorActDAO' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        if (getComponentManager() == null) {
            throw new ConfigurationException("The required property 'componentManager' is missing.");
        }
    }

    public PriorActDAO getPriorActDAO() {
        return m_priorActDAO;
    }

    public void setPriorActDAO(PriorActDAO priorActDAO) {
        m_priorActDAO = priorActDAO;
    }


    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    protected static final String SAVE_PROCESSOR = "PriorActManager";
    private DBUtilityManager m_dbUtilityManager;
    private PriorActDAO m_priorActDAO;
    private ComponentManager m_componentManager;
    private static AddOrigFieldsRecordLoadProcessor origRiskFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{RiskFields.PRACTICE_STATE_CODE}
    );

    private static AddOrigFieldsRecordLoadProcessor origCoverageFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, CoverageFields.COVERAGE_EFFECTIVE_TO_DATE}
    );
}
