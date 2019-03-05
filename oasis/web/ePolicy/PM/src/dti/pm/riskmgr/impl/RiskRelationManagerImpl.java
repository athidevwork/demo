package dti.pm.riskmgr.impl;

import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.core.http.RequestIds;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.entitymgr.EntityFields;
import dti.pm.entitymgr.EntityManager;
import dti.pm.pmdefaultmgr.PMDefaultManager;
import dti.pm.pmdefaultmgr.impl.DefaultLevelFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.Term;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskManager;
import dti.pm.riskmgr.RiskRelationFields;
import dti.pm.riskmgr.RiskRelationManager;
import dti.pm.riskmgr.dao.RiskDAO;
import dti.pm.riskmgr.dao.RiskRelationDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessManager;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details of RiskRelationManager Interface.
 * <p/>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 1, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/07/2008       Joe         Added method loadAllReverseRiskRelation()
 * 05/22/2008       fcb         80759: logic added for MultiRiskRelation
 * 07/06/2010       dzhang      103806: Added getInitialValuesForAddNonBaseCompanyInsured.
 *                              Change method getCompanyInsuredValue to public.
 *                              add additional conditions for delete/save risk to include risk process code 'INSOWNER'.
 * 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
 * 09/23/2010       syang       Issue 110819 - Added FilterOfficialRowForEndquoteRecordLoadProcessor to loadAllRiskRelationData().
 * 10/12/2010       dzhang      112946 - Modified validateAllRiskRelation to correct the compare dates overlap logic.
 * 11/09/2010       dzhang      114209 - Modified saveAllRiskRelation: for OOS Endorsement set the eff_to_date based on system config.
 * 01/17/2011       dzhang      116263 - Added riskEffFromDateStr and riskEffToDateStr to RiskRelationEntitlementRecordLoadProcessor.
 * 06/10/2011       wqfu        103799 - Added logic for active risk list selection of copy prior act stats.
 * 10/24/2011       ryzhao      123576 - Change the type of toRateB from YesNoFlag to String. Customer can config the
 *                              values of toRateB dropdown list in lookup_code table with type "RISK_REL_RATE_CODE".
 *                              It might contain other options besides Yes/No options.
 * 11/09/2011       dzhang      126933 - Modified validateAllRiskRelation: change the validate logic for annual premium.
 * 11/29/2011       wfu         127124 - Modified isRatingBasisEditable to disable rating base field for base CI. 
 * 05/29/2012       xnie        133772 - Modified saveAllRiskRelation() to correct cycle code for NI risk relation.
 * 01/08/2013       fcb         137981 - changes related to Pm_Dates modifications.
 * 01/29/2013       xnie        141290 - Modified validateAllRiskRelation() to convert rating basis to double value but
 *                                       not integer value.
 * 02/14/2013       sxm         141969 - correct the field ID for char4 column of NI risk
 * 03/01/2013       adeng       142548 - 1)Refactored code to add a method isOverlap() to check is the new record overlap
 *                                       with the one of exist records, it will reuse for overlap validation of NI,PI and CI .
 *                                       2)Modified method isOverlap() to add logic to skip flat record from the validation.
 * 02/20/2014       adeng       149313 - Modified validateAllRiskRelation() to set the correct row number into validation
 *                                       error message, and filter out the invisible records when do overlap validation.
 * 05/06/2014       fcb         151632 - Added isRefreshRequired.
 * 05/12/2014       xnie        154169 - Modified saveAllRiskRelation() to
 *                                       1) Delete temp risk relation when delete dummy temp risk for NI risk relation.
 *                                       2) Ignore duplicate risk relation id for delete from risk relation.
 * 09/01/2014       awu         157180 - Modified loadAllAvailableRiskForCompanyInsuredRiskRelation to set the input parameters
 *                                       currentRiskTypeCode and transEffectiveFromDate to null when generate from prior acts.
 * 10/10/2014       kxiang      158079 - Modified saveAllRiskRelation() to add logic when add NI risk relation into
 *                                       delRecords.
 * 10/27/2014       kxiang      158657 - Removed codes about Location2, as it's obsolete.
 * 05/02/2015       wdang       160336 - Modified mergeCommonInitialValuesForAddRiskRelation() to 
 *                                       1) Complete input parameters for PmDefaultManager.getDefaultValue().
 *                                       2) Set toRateB to 'N' by default if no value is chosen.
 * 03/16/2016       wdang       161448 - Modified mergeCommonInitialValuesForAddRiskRelation() to handle location field.
 * 08/20/2015       ssheng      165340 - Use Common Function PolicyHeader.getRecordMode to replace calcuate recordModeCode
 *                                       via screenMode.
 * 09/21/2015       ssheng      166004 - Validation the addCode whether is not equals "LOCATION".
 * 09/18/2015       lzhang      165941 - Modify loadAllRiskRelationData: use screenModeCode to identify
 *                                       whether user can add risk relation.
 *                                       Modify isRatingBasisEditable: use screenModeCode to identify
 *                                       whether isAnnualPremiumEditable button is displayed.
 * 06/08/2016       fcb         177372 - Changed int to long
 * 05/08/2017       xnie        180317 - Modified validateAllRiskRelation() to check related risk county code/specialty
 *                                       required based on isRiskRelValAvailable.
 * 07/17/2017       wrong       168374 - 1) Modified getInitialValuesForAddRisk() to add logic to get default value for
 *                                          pcf risk and pcf county.
 *                                       2) Modified method saveAllRiskRelation to save new fields
 *                                          PCF county/class/annual charge in risk relation table.
 *                                       3) Modified getInitialValuesForAddNonBaseCompanyInsured() to add pcf risk
 *                                          county in return record.
 * 08/17/2017       wrong       187776 - Modified getInitialValuesForAddRiskRelation() to add new logic to initialize
 *                                       PCF county/specialty for PI/CI by system parameter PM_USE_RR_PCF_FLDS.
 * 12/07/2017       lzhang      182769 - Modified isRatingBasisEditable: not editable field when transaction date
 *                                       is not located in risk relation period
 * 06/08/2018       xnie        193805 - Modified mergeCommonInitialValuesForAddRiskRelation() to append slot/FTE ID to
 *                                       risk name when adding PI risk relation.
 * ---------------------------------------------------
 */
public class RiskRelationManagerImpl implements RiskRelationManager {
    /**
     * To load all risk relation data.
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param inputRecord  a Record with original selected risk information
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risk relations.
     */
    public RecordSet loadAllRiskRelation(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor) {
        return loadAllRiskRelationData(policyHeader, inputRecord, loadProcessor, "N");
    }

    /**
     * To load all reverse risk relation data.
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param inputRecord   a Record with original selected risk information
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of reverse risk relations.
     */
    public RecordSet loadAllReverseRiskRelation(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor) {
        return loadAllRiskRelationData(policyHeader, inputRecord, loadProcessor, "Y");
    }

    /**
     * To load all risk relation data either for current relation or for reverse relation.
     */
    private RecordSet loadAllRiskRelationData(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor, String reverse) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskRelationData", new Object[]{policyHeader, inputRecord, loadProcessor, reverse});
        }

        Record record = policyHeader.toRecord();
        inputRecord.setFields(record, false);

        // record mode code
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        RecordMode recordModeCode = policyHeader.getRecordMode();
        PMCommonFields.setRecordModeCode(inputRecord, recordModeCode);

        // transation type code
        TransactionFields.setTransactionTypeCode(inputRecord,
            policyHeader.getLastTransactionInfo().getTransactionTypeCode());

        // reverse
        YesNoFlag isReverse = YesNoFlag.getInstance(reverse);
        RiskRelationFields.setReverse(inputRecord, isReverse);

        // get the Non-Insured FTE count and pass into load processor
        int fteCount = getRiskRelationDAO().getNIFteCount(inputRecord);

        // get the is company insured value and pass into load processor
        String isCompanyInsuredStr = getCompanyInsuredValue(policyHeader, inputRecord);

        // check solo owner [UC32.11]
        Record r = new Record();
        r.setFieldValue("baseId", RiskFields.getRiskBaseRecordId(inputRecord));
        r.setFieldValue("inputStr", "CANCEL_REL");
        // set cancellationDate here since CancelProcessJdbcDAO.isSoloOwner() using mapping "cancellationDate" for parameter "p_tran_eff_date"
        CancelProcessFields.setCancellationDate(r, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        boolean isSoloOwner = getCancelProcessManager().isSoloOwner(r);

        // 105611, system should determine whether the current request is from view cancellation detail page.
        boolean snapshotB = false;
        if(inputRecord.hasStringValue("snapshotB") && "Y".equalsIgnoreCase(inputRecord.getStringValue("snapshotB"))){
            snapshotB = true;
        }

        String riskEffFromDateStr = RiskFields.getRiskEffectiveFromDate(inputRecord);
        String riskEffToDateStr = RiskFields.getRiskEffectiveToDate(inputRecord);
        if (policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue()) {
            riskEffToDateStr = getRiskManager().getRiskExpDate(inputRecord);
            if(StringUtils.isBlank(riskEffToDateStr)) {
                riskEffToDateStr = "01/01/3000";
        }
        }

        // Load risk relation data
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            loadProcessor, origFieldLoadProcessor);
        RecordLoadProcessor entitlementLoadProcessor = new RiskRelationEntitlementRecordLoadProcessor(
            policyHeader, fteCount, isCompanyInsuredStr, isSoloOwner, isReverse.booleanValue(), snapshotB, riskEffFromDateStr, riskEffToDateStr);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            loadProcessor, entitlementLoadProcessor);
        // Issue 110819, filter official record for end quote.
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor = new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "riskRelId");
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, endquoteLoadProcessor);
        RecordSet rs = getRiskRelationDAO().loadAllRiskRelation(inputRecord, loadProcessor);
        Record sumRec = rs.getSummaryRecord();

        // get risk name
        RiskFields.setRiskName(sumRec, policyHeader.getRiskHeader().getRiskName());
        // get risk effective from and to date
        RiskFields.setRiskEffectiveToDate(sumRec, riskEffToDateStr);
        RiskFields.setRiskEffectiveFromDate(sumRec, RiskFields.getRiskEffectiveFromDate(inputRecord));
        if (isReverse.booleanValue()) {
            sumRec.setFieldValue("reverse", MessageManager.getInstance().formatMessage("pm.maintainRiskRelation.header.reverse"));
        }
        else {
            sumRec.setFieldValue("reverse", "");
        }
        if (inputRecord.hasStringValue("origRiskEffectiveFromDate")) {
            RiskFields.setOrigRiskEffectiveFromDate(sumRec, RiskFields.getOrigRiskEffectiveFromDate(inputRecord));
        }
        RiskFields.setRiskBaseRecordId(sumRec, RiskFields.getRiskBaseRecordId(inputRecord));

        // transaction effective from date
        TransactionFields.setTransactionEffectiveFromDate(sumRec,
            policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        // risk county code
        RiskRelationFields.setRiskCountyCode(sumRec, RiskRelationFields.getRiskCountyCode(inputRecord));
        // originally selected risk type code
        RiskRelationFields.setCurrentRiskTypeCode(sumRec, policyHeader.getRiskHeader().getRiskTypeCode());

        if (sumRec.hasFieldValue("screenModeCode")) {
            screenMode = ScreenModeCode.getInstance(sumRec.getStringValue("screenModeCode"));
        }
        // Show error message if there's no record in VIEW_POLICY or VIEW_ENDQUOTE mode
        if (!isReverse.booleanValue() && rs.getSize() <= 0 && (screenMode.isViewPolicy() ||
            screenMode.isViewEndquote() || screenMode.isResinstateWIP() || screenMode.isCancelWIP())) {
            MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.noRelation.data.info");
            rs.getSummaryRecord().setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
            rs.getSummaryRecord().setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
            rs.getSummaryRecord().setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
            rs.getSummaryRecord().setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
            rs.getSummaryRecord().setFieldValue("isDoneAvailable", YesNoFlag.N);
        }
        else if (isReverse.booleanValue() && rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.noRelation.data.info");
        }

        YesNoFlag multiRiskRelation = getRiskRelationDAO().getMultiRiskRelation(inputRecord);
        rs.getSummaryRecord().setFieldValue("multiRiskRelation", multiRiskRelation);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskRelationData", rs);
        }
        return rs;
    }

    /**
     * To save/delete/update all risk relation data.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a RecordSet with all need to be processed Risk Relation records
     * @return The number of updated records.
     */
    public int saveAllRiskRelation(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllRiskRelation", new Object[]{policyHeader, inputRecords});

        int updateCount = 0;

        // Validate the input risks prior to saving them.
        validateAllRiskRelation(policyHeader, inputRecords);

        Record policyRecord = policyHeader.toRecord();

        RecordSet wipRecords = inputRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet offRecords = inputRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));

        RecordSet deletedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));

        try {

            // UC112.21 Save Procedure: #1
            RecordSet niDelRecords = new RecordSet();
            RecordSet delRecords = new RecordSet();
            Iterator delIter = deletedWipRecords.getRecords();
            while (delIter.hasNext()) {
                Record r = (Record) delIter.next();
                if (RiskRelationFields.isNonInsured(r) ||
                    (r.hasStringValue(RiskRelationFields.RISK_PROCESS_CODE) && RiskRelationFields.getRiskProcessCode(r).equals("INSOWNER"))) {
                    // For NI risk relation which is from official record, before delete TEMP dummy risk, TEMP risk relation needs to be deleted as well.
                    if(r.hasStringValue(RiskRelationFields.OFFICIAL_RECORD_ID) && !StringUtils.isBlank(RiskRelationFields.getOfficialRecordId(r))){
                        delRecords.addRecord(r);
                    }
                    niDelRecords.addRecord(r);
                }
                else if (RiskRelationFields.isPolicyInsured(r) || RiskRelationFields.isCompanyInsured(r) ||
                    !r.hasStringValue(RiskRelationFields.OFFICIAL_RECORD_ID) ||
                    RiskRelationFields.getOfficialRecordId(r).equals("0")) {
                    // For PI/CI risk relation which parent risk has multiple versions, system only needs to delete risk
                    // relation data one time.
                    if (delRecords.getSubSet(new RecordFilter(RiskRelationFields.RISK_RELATION_ID,
                        RiskRelationFields.getRiskRelationId(r))).getSize() == 0) {
                    delRecords.addRecord(r);
                }
            }
            }
            // UC112.21 #1.1
            if (niDelRecords.getSize() > 0) {
                updateCount += deleteAllNIRisk(niDelRecords);
            }
            // UC112.21 #1.2
            if (delRecords.getSize() > 0) {
                updateCount += getRiskRelationDAO().deleteAllRiskRelation(delRecords);
            }

            // UC112.21 #2: saves risk/coverage information
            RecordSet updatedRiskRecords = new RecordSet();
            // get last term's effective to date
            String lastTermEffToDate = "";
            Iterator iter = policyHeader.getPolicyTerms();
            if (iter.hasNext()) {
                Term lastTerm = (Term) iter.next();
                lastTermEffToDate = lastTerm.getEffectiveToDate();
            }
            String configuredExpDate = getConfiguredExpDate(policyHeader);
            RecordSet changedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
            RecordSet updatedOffRecords = offRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
            changedWipRecords = PMRecordSetHelper.setRowStatusOnModifiedRecords(changedWipRecords);
            RecordSet updatedRecords = new RecordSet();
            updatedRecords.addRecords(changedWipRecords);
            updatedRecords.addRecords(updatedOffRecords);

            // For issue 101090. System should use the current transaction log primary key.
            updatedRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());

            // Add the PolicyHeader info to each inserted/updated Risk Relation Record
            updatedRecords.setFieldsOnAll(policyRecord, false);
            Iterator updatedIter = updatedRecords.getRecords();
            while (updatedIter.hasNext()) {
                Record r = (Record) updatedIter.next();
                if (RiskRelationFields.isNonInsured(r) ||
                    (r.hasStringValue(RiskRelationFields.RISK_PROCESS_CODE) && RiskRelationFields.getRiskProcessCode(r).equals("INSOWNER"))) {
                    Record record = new Record();
                    RiskRelationFields.setType(record, "RISK");
                    record.setFieldValue(RiskFields.RISK_ID, r.getLongValue(RiskFields.RISK_ID));
                    RecordMode recordMode = PMCommonFields.getRecordModeCode(r);
                    StringBuffer parms = new StringBuffer();
                    // same parms values for both Temp mode and Official mode
                    parms.append(addParm(r, "RISK_PK", RiskFields.RISK_ID))
                        .append(addParm(r, "RISK_BASE_RECORD_FK", RiskRelationFields.RISK_PARENT_ID))
                        .append(addParm("EFFECTIVE_FROM_DATE", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate()))
                        .append(addParm("EFFECTIVE_TO_DATE", configuredExpDate))
                        .append(addParm("PRIMARY_RISK_B", "N"))
                        .append(addParm(r, "COUNTY_CODE_USED_TO_RATE", RiskRelationFields.COUNTY_CODE_USED_TO_RATE))
                        .append(addParm(r, "RISK_CLS_USED_TO_RATE", RiskRelationFields.RISK_CLASS_CODE))
                        .append(addParm(r, "ENTITY_FK", EntityFields.ENTITY_ID))
                        .append(addParm("POLICY_FK", policyHeader.getPolicyId()))
                        .append(addParm("POLICY_NO", policyHeader.getPolicyNo()))
                        .append(addParm(r, "TRANSACTION_LOG_FK", TransactionFields.TRANSACTION_LOG_ID))
                        .append(addParm(r, "RISK_TYPE_CODE",
                            RiskRelationFields.isNonInsured(r) &&
                                RiskRelationFields.getAddNiCoverageB(r).booleanValue() ?
                                RiskRelationFields.NI_RISK_TYPE_CODE : RiskFields.RISK_TYPE_CODE))
                        .append(addParm(r, "RATING_BASIS", RiskRelationFields.RATING_BASIS))
                        .append(addParm(r, "AFTER_IMAGE_RECORD_B", RiskFields.AFTER_IMAGE_RECORD_B));
                    //103806 dzhang add for non-base
                    if (r.hasStringValue(RiskRelationFields.RISK_PROCESS_CODE) && RiskRelationFields.getRiskProcessCode(r).equals("INSOWNER")) {
                        parms.append(addParm("RISK_PROCESS_CODE", "INSOWNER"));
                    }
                    else {
                        parms.append(addParm("RISK_PROCESS_CODE", "NINSOWNER"));
                    }
                    parms.append(addParm(r, "PRACTICE_STATE_CODE", RiskRelationFields.PRACTICE_STATE_CODE))
                        .append(addParm("TEACHING_B", "N"))
                        .append(addParm("FTE_EQUIVALENT", ""))
                        .append(addParm("FTE_FULL_TIME_HRS", ""))
                        .append(addParm("FTE_PART_TIME_HRS", ""))
                        .append(addParm("FTE_PER_DIEM_HRS", ""))
                        .append(addParm(r, "NUMBER_OF_EMPLOYED_DOCTOR", RiskRelationFields.NUMBER_OF_EMPLOYED_DOCTOR));
                    if (RiskRelationFields.isNonInsured(r) &&
                        RiskRelationFields.getAddNiCoverageB(r).booleanValue()) {
                        parms.append(addParm(r, "CHAR4", "childPolicyNo"));
                    }
                    if (recordMode.isTemp()) {
                        parms.append(addParm(r, "ROW_STATUS", "rowStatus"))
                            .append(addParm(r, "CURR_POL_REL_STATUS_CODE", RiskRelationFields.RISK_RELATION_STATUS))
                            .append(addParm("TERM_EXPIRATION_DATE", policyHeader.getTermEffectiveToDate()))
                            .append(addParm(r, "RECORD_MODE_CODE", PMCommonFields.RECORD_MODE_CODE))
                            .append(addParm("POLICY_CYCLE_CODE", String.valueOf(policyHeader.getPolicyCycleCode())))
                            .append(addParm("COVERAGE_PART_BASE_RECORD_FK", ""));
                    }
                    record.setFieldValue("parms", parms.toString());
                    updatedRiskRecords.addRecord(record);
                }
            }
            if (updatedRiskRecords.getSize() > 0) {
                updateCount += getRiskRelationDAO().saveAllNIRisk(updatedRiskRecords);
            }

            // save all ni coverage information
            RecordSet updatedCovgRecords = new RecordSet();
            updatedIter = updatedRecords.getRecords();
            int insertedNiRiskSize = changedWipRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.INSERTED})).getSize();
            while (updatedIter.hasNext()) {
                Record r = (Record) updatedIter.next();
                if (RiskRelationFields.isNonInsured(r) && RiskRelationFields.getAddNiCoverageB(r).booleanValue()) {
                    // if a new NI risk was inserted or
                    // any of the niRiskTypeCode, niCoverageLimitCode, niRetroDate, coverageId or niCurrentCarrierId is changed
                    if (insertedNiRiskSize > 0 ||
                        isFieldChanged(r, RiskRelationFields.NI_RISK_TYPE_CODE) ||
                        isFieldChanged(r, RiskRelationFields.NI_COVERAGE_LIMIT_CODE) ||
                        isFieldChanged(r, RiskRelationFields.NI_RETRO_DATE) ||
                        isFieldChanged(r, CoverageFields.COVERAGE_ID) ||
                        isFieldChanged(r, RiskRelationFields.NI_CURRENT_CARRIER_ID)) {
                        RecordMode covgRecordMode = RiskRelationFields.getCoverageRecordModeCode(r);
                        if (covgRecordMode != null && covgRecordMode.isOfficial()) {
                            RiskRelationFields.setRowStatus(r, null);
                        }
                        else {
                            if (r.getUpdateIndicator().equals(UpdateIndicator.INSERTED)) {
                                RiskRelationFields.setRowStatus(r, "NEW");
                            }
                            else if (r.getUpdateIndicator().equals(UpdateIndicator.UPDATED)) {
                                RiskRelationFields.setRowStatus(r, "MODIFIED");
                            }
                        }
                        if (covgRecordMode == null || !covgRecordMode.isOfficial() && !covgRecordMode.isTemp()) {
                            // coverage id
                            CoverageFields.setCoverageId(r,
                                String.valueOf(getDbUtilityManager().getNextSequenceNo().longValue()));
                            // coverage base record id
                            CoverageFields.setCoverageBaseRecordId(r,
                                String.valueOf(getDbUtilityManager().getNextSequenceNo().longValue()));
                        }
                        TransactionFields.setTransactionEffectiveFromDate(r,
                            policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                        PolicyFields.setNewTermExpDate(r, configuredExpDate);
                        PolicyHeaderFields.setTermEffectiveToDate(r, policyHeader.getTermEffectiveToDate());
                        PolicyHeaderFields.setTermBaseRecordId(r, policyHeader.getTermBaseRecordId());
                        CoverageFields.setCovPartCode(r, "X");
                        updatedCovgRecords.addRecord(r);
                    }
                }
            }
            if (updatedCovgRecords.getSize() > 0) {
                updateCount += getRiskRelationDAO().saveAllNICoverage(updatedCovgRecords);
            }

            // UC112.21 #3: saves updated records
            RecordSet updatedRelRecords = new RecordSet();
            RecordSet changedRelRecords = new RecordSet();
            updatedIter = updatedRecords.getRecords();
            while (updatedIter.hasNext()) {
                Record r = (Record) updatedIter.next();
                Record relRec = new Record();
                TransactionFields.setTransactionLogId(relRec, TransactionFields.getTransactionLogId(r));
                RiskRelationFields.setRiskRelationId(relRec, RiskRelationFields.getRiskRelationId(r));
                RiskRelationFields.setRiskRelEffectiveFromDate(relRec,
                    RiskRelationFields.getRiskRelEffectiveFromDate(r));
                RiskRelationFields.setRiskRelationTypeCode(relRec, RiskRelationFields.getRiskRelationTypeCode(r));
                RiskRelationFields.setOverrideRiskBaseId(relRec, null);
                RiskRelationFields.setToRateB(relRec, RiskRelationFields.getToRateB(r));
                RiskRelationFields.setPcfRiskCountyCode(relRec, RiskRelationFields.getPcfRiskCountyCode(r));
                RiskRelationFields.setPcfRiskClassCode(relRec, RiskRelationFields.getPcfRiskClassCode(r));
                RiskRelationFields.setAnnualPcfCharge(relRec, RiskRelationFields.getAnnualPcfCharge(r));
                if (RiskRelationFields.getRiskRelationStatus(r).equals("PENDING") ||
                    PMCommonFields.getRecordModeCode(r).isTemp()) {
                    if (r.getUpdateIndicator().equals(UpdateIndicator.INSERTED)) {
                        RiskRelationFields.setRowStatus(relRec, "NEW");
                    }
                    else if (r.getUpdateIndicator().equals(UpdateIndicator.UPDATED)) {
                        RiskRelationFields.setRowStatus(relRec, "MODIFIED");
                    }
                    RiskFields.setRiskBaseRecordId(relRec, policyHeader.getRiskHeader().getRiskBaseRecordId());
                    RiskRelationFields.setRiskParentId(relRec, RiskRelationFields.getRiskParentId(r));
                    PMCommonFields.setRecordModeCode(relRec, PMCommonFields.getRecordModeCode(r));
                    if (policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue()) {
                        Record tmpRec = new Record();
                        // For issue 100657.
                        RiskFields.setRiskBaseRecordId(tmpRec, policyHeader.getRiskHeader().getRiskBaseRecordId());
                        RiskFields.setRiskEffectiveFromDate(tmpRec, r.getStringValue("origRiskEffectiveFromDate"));
                        String riskExpDateStr = getRiskManager().getRiskExpDate(tmpRec);
                        if (StringUtils.isBlank(riskExpDateStr)) {
                            riskExpDateStr = "01/01/3000";
                        }
                        RiskRelationFields.setRiskRelEffectiveToDate(relRec, riskExpDateStr);
                    }else{
                        RiskRelationFields.setRiskRelEffectiveToDate(relRec, configuredExpDate);
                    }
                    RiskRelationFields.setRiskRelationStatus(relRec, RiskRelationFields.getRiskRelationStatus(r));                    
                    updatedRelRecords.addRecord(relRec);
                }
                else {
                    changedRelRecords.addRecord(relRec);
                }
            }
            if (updatedRelRecords.getSize() > 0) {
                updateCount += getRiskRelationDAO().saveAllRiskRelation(updatedRelRecords);
            }
            if (changedRelRecords.getSize() > 0) {
                changedRelRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_EFFECTIVE_FROM_DATE,
                    policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                updateCount += getRiskRelationDAO().updateAllRiskRelation(changedRelRecords);
            }

        }
        catch (AppException ae) {
            MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.save.failed.error");
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAllRiskRelation", new Integer(updateCount));
        return updateCount;
    }

    protected void validateAllRiskRelation(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllRiskRelation", new Object[]{inputRecords});
        }

        //get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.UPDATED, UpdateIndicator.INSERTED}));
        inputRecords.setFieldsOnAll(policyHeader.toRecord(), false);

        // Set the displayRecordNumber to all visible records.
        inputRecords = PMRecordSetHelper.setDisplayRecordNumberOnRecords(inputRecords);

        // get Non-Insured FTE count
        YesNoFlag isFteAvailable = isFteAvailable(policyHeader.toRecord());

        Iterator iter = changedRecords.getRecords();
        while (iter.hasNext()) {
            Record r = (Record) iter.next();
            String rowNum = String.valueOf(r.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));

            // Validaton #1: Modify Relationship Type
            if (RiskRelationFields.getAddNiCoverageB(r).booleanValue()) {
                // do nothing
            }

            boolean isNiPremiumEditable = isNiPremiumEditable(r).booleanValue();
            // Validation #2: NI Rating Basis Changed to Null or Negative Value
            if (RiskRelationFields.isNonInsured(r)) {
                String ratingBasis = RiskRelationFields.getRatingBasis(r);
                String oldRatingBasis = RiskRelationFields.getOldRatingBasis(r);
                if (oldRatingBasis == null && ratingBasis != null ||
                    oldRatingBasis != null && !oldRatingBasis.equals(ratingBasis)) {
                    if ((ratingBasis == null || Double.parseDouble(ratingBasis) < 0) && isNiPremiumEditable) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.ratingBasis.nullOrNegative.error",
                            new String[]{rowNum});
                    }
                }
            }

            // Validation #3: Modify NI Risk Type/Entity Type (check niRiskTypeCode instead of riskTypeCode field)
            if (RiskRelationFields.isNonInsured(r)) {
                String niRiskTypeCode = RiskRelationFields.getNiRiskTypeCode(r);
                String oldNiRiskTypeCode = RiskRelationFields.getOldNiRiskTypeCode(r);
                if (niRiskTypeCode == null && oldNiRiskTypeCode != null ||
                    niRiskTypeCode != null && !niRiskTypeCode.equals(oldNiRiskTypeCode)) {
                    Record rec = new Record();
                    PolicyHeaderFields.setPolicyTypeCode(rec, policyHeader.getPolicyTypeCode());
                    RiskFields.setRiskTypeCode(rec, niRiskTypeCode);
                    EntityFields.setEntityId(rec, EntityFields.getEntityId(r));
                    if (!YesNoFlag.getInstance(getRiskDAO().isRiskEntityTypeValid(rec)).booleanValue()) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.niRisk.invalid.error",
                            new String[]{rowNum});
                    }
                }
            }

            // UC112.20 Validations
            // #1 Overlap validation
            String entityIdStr = RiskFields.getEntityId(r);
            long entityId = Long.parseLong(entityIdStr);
            String riskRelTypeCode = RiskRelationFields.getRiskRelationTypeCode(r);
            String countyCode = RiskRelationFields.getCountyCodeUsedToRate(r);
            String riskClassCode = RiskRelationFields.getRiskClassCode(r);
            String riskEffFromDateStr = RiskFields.getRiskEffectiveFromDate(r);
            String riskEffToDateStr = RiskFields.getRiskEffectiveToDate(r);
            Date riskEffFromDate = DateUtils.parseDate(riskEffFromDateStr);
            Date riskEffToDate = DateUtils.parseDate(riskEffToDateStr);
            String riskParentId = RiskRelationFields.getRiskParentId(r);
            Iterator allIter = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.UPDATED, UpdateIndicator.INSERTED, UpdateIndicator.NOT_CHANGED})).getRecords();
            while (allIter.hasNext()) {
                Record record = (Record) allIter.next();
                String tmpRowNum = String.valueOf(record.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));
                if (tmpRowNum.equals(rowNum)) {
                    continue;
                }

                String tmpEntityIdStr = RiskFields.getEntityId(record);
                long tmpEntityId = Long.parseLong(tmpEntityIdStr);
                String tmpRiskEffFromDateStr = RiskFields.getRiskEffectiveFromDate(record);
                String tmpRiskEffToDateStr = RiskFields.getRiskEffectiveToDate(record);
                Date tmpRiskEffFromDate = DateUtils.parseDate(tmpRiskEffFromDateStr);
                Date tmpRiskEffToDate = DateUtils.parseDate(tmpRiskEffToDateStr);
                // #1.1 NI
                if (RiskRelationFields.isNonInsured(r) && RiskRelationFields.isNonInsured(record)) {
                    if (entityId == 0 && tmpEntityId == 0 && isFteAvailable.booleanValue()) {
                        if (RiskRelationFields.getRiskRelationTypeCode(record).equals(riskRelTypeCode) &&
                            RiskRelationFields.getCountyCodeUsedToRate(record).equals(countyCode) &&
                            RiskRelationFields.getRiskClassCode(record).equals(riskClassCode)) {
                            if (isOverlap(tmpRiskEffFromDate, tmpRiskEffToDate, riskEffFromDate, riskEffToDate)) {
                                MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.niDate.overlap1.error",
                                    new String[]{rowNum, tmpRowNum});
                            }
                        }
                    }
                    else if (entityId != 0 && tmpEntityId != 0 && entityId == tmpEntityId) {
                        if (isOverlap(tmpRiskEffFromDate, tmpRiskEffToDate, riskEffFromDate, riskEffToDate)) {
                            MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.niDate.overlap2.error",
                                new String[]{rowNum, tmpRowNum});
                        }
                    }
                }
                // #1.2 PI or CI
                else if (RiskRelationFields.isPolicyInsured(r) && RiskRelationFields.isPolicyInsured(record) ||
                    RiskRelationFields.isCompanyInsured(r) && RiskRelationFields.isCompanyInsured(record)) {
                    if (RiskRelationFields.getRiskParentId(record).equals(riskParentId)) {
                        if (isOverlap(tmpRiskEffFromDate, tmpRiskEffToDate, riskEffFromDate, riskEffToDate)) {
                            MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.piOrCiDate.overlap.error",
                                new String[]{rowNum, tmpRowNum});
                        }
                    }
                }
            }

            // #2 Relationship type is required
            if (!r.hasStringValue(RiskRelationFields.RISK_RELATION_TYPE_CODE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.riskRelationType.required.error",
                    new String[]{rowNum});
            }

            // #3 niRiskTypeCode or riskTypeCode is required
            if (RiskRelationFields.isNonInsured(r) &&
                RiskRelationFields.getAddNiCoverageB(r).booleanValue() &&
                !r.hasStringValue(RiskRelationFields.NI_RISK_TYPE_CODE) ||
                !RiskRelationFields.isNonInsured(r) &&
                !RiskRelationFields.getAddNiCoverageB(r).booleanValue() &&
                !r.hasStringValue(RiskFields.RISK_TYPE_CODE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.riskType.required.error",
                    new String[]{rowNum});
            }

            // #4 Practice state code is required
            if (!r.hasStringValue(RiskRelationFields.PRACTICE_STATE_CODE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.practiceStateCode.required.error",
                    new String[]{rowNum});
            }

            Record riskRelValRecord = new Record();
            RiskRelationFields.setCompanyInsured(riskRelValRecord, RiskRelationFields.getCompanyInsured(r));
            RiskRelationFields.setOwnerRiskTypeCode(riskRelValRecord, policyHeader.getRiskHeader().getRiskTypeCode());
            RiskRelationFields.setRelatedRiskTypeCode(riskRelValRecord, RiskFields.getRiskTypeCode(r));
            TransactionFields.setTransactionEffectiveFromDate(riskRelValRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
            YesNoFlag isRiskRelValAvailable = getRiskRelationDAO().isRiskRelValAvailable(riskRelValRecord);
            if (isRiskRelValAvailable.booleanValue()) {
                // #5 County code is required
                if (!r.hasStringValue(RiskRelationFields.COUNTY_CODE_USED_TO_RATE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.countyCode.required.error",
                        new String[]{rowNum});
                }

                // #6 Speicalty is required for "P"erson risk type
                if (r.hasStringValue(EntityFields.ENTITY_ID) &&
                    !"O".equals(getEntityManager().getEntityType(EntityFields.getEntityId(r))) &&
                    !"LOCATION".equals(r.getStringValue(RiskFields.ADD_CODE)) &&
                    !r.hasStringValue(RiskRelationFields.RISK_CLASS_CODE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.specialty.required.error",
                        new String[]{rowNum});
                }
            }

            // #7 Non-Insured coverage limit is required
            if (RiskRelationFields.isNonInsured(r) && RiskRelationFields.getAddNiCoverageB(r).booleanValue() &&
                !r.hasStringValue(RiskRelationFields.NI_COVERAGE_LIMIT_CODE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.niCoverageLimit.required.error",
                    new String[]{rowNum});
            }

            // #8 Rating Basis is required
            if (RiskRelationFields.isNonInsured(r) && isNiPremiumEditable &&
                !r.hasStringValue(RiskRelationFields.RATING_BASIS)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.ratingBasis.required.error",
                    new String[]{rowNum});
            }

            if (MessageManager.getInstance().hasErrorMessages())
                break;
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid risk relation data.");
        }

        l.exiting(getClass().getName(), "validateAllRiskRelation");
    }

    /**
     * To delete all NI risks. This is because the NI risk relations are saved as risks,
     * so if wants to delete NI risk relation, we need to delete all NI risks.
     *
     * @param inputRecords a RecordSet with all need to be deleted Risk records
     * @return The number of deleted records.
     */
    public int deleteAllNIRisk(RecordSet inputRecords) {
        return getRiskDAO().deleteAllRisk(inputRecords);
    }

    /**
     * To get initial values for adding Risk Relation record.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values.
     */
    public Record getInitialValuesForAddRiskRelation(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddRiskRelation",
                new Object[]{policyHeader, inputRecord});
        }
        boolean isMultiRisk = false;
        String userRrPcfFlds = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_USE_RR_PCF_FLDS, "N");
        if (inputRecord.hasStringValue("multiRiskRelation")) {
            isMultiRisk = YesNoFlag.getInstance(
                inputRecord.getStringValue("multiRiskRelation")).booleanValue();
        }
        if (isMultiRisk) {
            inputRecord.setFieldValue("entityId", inputRecord.getFieldValue("multiRiskEntityId"));
        }

        Record returnRecord = new Record();

        // Firstly, merge default value from web work bench configuration
        Record configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_RISKRELATION_ACTION_CLASS_NAME);
        if (configuredDefaultValues != null) {
            returnRecord.setFields(configuredDefaultValues);
        }

        // Secondly, merge the Common Initial Values (UC22.3)
        mergeCommonInitialValuesForAddRiskRelation(policyHeader, inputRecord, returnRecord);

        // Thirdly, merge each relationship type's initial values
        if (RiskRelationFields.isPolicyInsured(inputRecord)) {
            returnRecord.setFields(inputRecord, false);
            if (userRrPcfFlds.equals("N")) {
                RiskRelationFields.setPcfRiskClassCode(returnRecord, "");
                RiskRelationFields.setPcfRiskCountyCode(returnRecord, "");
            }
            RiskRelationFields.setRiskParentId(returnRecord, RiskFields.getRiskBaseRecordId(inputRecord));
            RiskRelationFields.setRatingBasis(returnRecord, null);
            // For issue 100502, replaced the polcyNo by childPolicyNo.
            RiskRelationFields.setChildPolicyNo(returnRecord, policyHeader.getPolicyNo());
        }
        else if (RiskRelationFields.isCompanyInsured(inputRecord)) {
            Record r = new Record();
            PolicyHeaderFields.setPolicyId(r, policyHeader.getPolicyId());
            EntityFields.setEntityId(r, EntityFields.getEntityId(inputRecord));
            PolicyHeaderFields.setTermEffectiveFromDate(r, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
            PolicyHeaderFields.setTermEffectiveToDate(r, policyHeader.getTermEffectiveToDate());
            String riskBaseId = null;
            String sourceRecordId = RiskRelationFields.getSourceRecordId(inputRecord);
            if (!sourceRecordId.equals("0")) {
                riskBaseId = sourceRecordId;
            }
            RiskFields.setRiskBaseRecordId(r, riskBaseId);
            Record retRec = getRiskRelationDAO().getInitialValuesForAddCompINRiskRelation(r);

            // set values into returnRecord
            RiskRelationFields.setRiskParentId(returnRecord, RiskFields.getRiskBaseRecordId(retRec));
            RiskRelationFields.setCountyCodeUsedToRate(returnRecord, RiskRelationFields.getCounty(retRec));
            RiskRelationFields.setRiskClassCode(returnRecord, RiskRelationFields.getRiskClass(retRec));
            if (userRrPcfFlds.equals("Y")) {
                RiskRelationFields.setPcfRiskCountyCode(returnRecord, RiskRelationFields.getPcfRiskCountyCode(retRec));
                RiskRelationFields.setPcfRiskClassCode(returnRecord, RiskRelationFields.getPcfRiskClassCode(retRec));
            }
            RiskFields.setRiskTypeCode(returnRecord, RiskFields.getRiskTypeCode(retRec));
            RiskRelationFields.setRatingBasis(returnRecord, null);
            RiskFields.setRiskEffectiveFromDate(returnRecord, RiskRelationFields.getRiskEff(retRec));
            RiskFields.setRiskEffectiveToDate(returnRecord, RiskRelationFields.getRiskExp(retRec));
            RiskRelationFields.setPracticeStateCode(returnRecord, RiskRelationFields.getPracticeState(retRec));
            if (!isMultiRisk){
                EntityFields.setEntityId(returnRecord, EntityFields.getEntityId(inputRecord));
            }
            else {
                EntityFields.setEntityId(returnRecord, (String)inputRecord.getFieldValue("multiRiskEntityId"));
            }
            String sPolicyId = PolicyHeaderFields.getPolicyId(inputRecord);
            // For issue 100502, replaced the polcyNo by childPolicyNo.
            RiskRelationFields.setChildPolicyNo(returnRecord, getPolicyManager().getPolicyNo(sPolicyId));
            RiskRelationFields.setOverrideStateB(returnRecord, YesNoFlag.N);
            RiskRelationFields.setOldPracticeStateCode(returnRecord, RiskRelationFields.getPracticeState(retRec));
            RiskRelationFields.setOldCountyCodeUsedToRate(returnRecord, RiskRelationFields.getCounty(retRec));
            RiskRelationFields.setOldRiskClassCode(returnRecord, RiskRelationFields.getRiskClass(retRec));
            RiskRelationFields.setCompanyInsured(returnRecord, "CI");

            // set CI riskName here
            String riskTypeDesc = getRiskDAO().getRiskTypeDescription(RiskFields.getRiskBaseRecordId(retRec));
            if (riskTypeDesc != null) {
                String riskName = RiskFields.getRiskName(returnRecord) + " (" + riskTypeDesc + ")";
                RiskFields.setRiskName(returnRecord, riskName);
            }
        }
        else if (RiskRelationFields.isNonInsured(inputRecord)) {
            // riskParentId
            Record r = new Record();
            PolicyHeaderFields.setPolicyId(r, policyHeader.getPolicyId());
            EntityFields.setEntityId(r, EntityFields.getEntityId(inputRecord));
            RiskFields.setRiskTypeCode(r, "NINSOWNER");
            RiskFields.setSlotId(r, null);
            RiskRelationFields.setPropertyRiskId(r, null);
            RiskFields.setCoveragePartBaseRecordId(r, null);
            String riskBaseId = getRiskDAO().getRiskBaseId(r);
            RiskRelationFields.setRiskParentId(returnRecord, riskBaseId);

            // riskId
            RiskFields.setRiskId(returnRecord, String.valueOf(getDbUtilityManager().getNextSequenceNo().longValue()));
            RiskRelationFields.setRiskProcessCode(returnRecord, "NINSOWNER");
            // riskTypeCode & niRiskTypeCode
            String riskTypeCode = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_NONINS_RISKTYPE);
            RiskFields.setRiskTypeCode(returnRecord, riskTypeCode);
            RiskRelationFields.setNiRiskTypeCode(returnRecord, riskTypeCode);
            RiskFields.setPracticeStateCode(returnRecord, policyHeader.getIssueStateCode());

            // countyCodeUsedToRate
            String countyParm = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_NONINS_DEF_COUNTY, "N");
            String riskCounty = YesNoFlag.getInstance(countyParm).booleanValue() ?
                RiskRelationFields.getRiskCountyCode(inputRecord) : null;
            RiskRelationFields.setCountyCodeUsedToRate(returnRecord, riskCounty);
            // pcfCountyCode
            if (!StringUtils.isBlank(riskCounty)) {
                RiskFields.setPracticeStateCode(inputRecord, policyHeader.getIssueStateCode());
                RiskFields.setRiskCounty(inputRecord, riskCounty);
                String pcfRiskCountyCode = getRiskDAO().getDefaultValueForPcfCounty(inputRecord);
                RiskRelationFields.setPcfRiskCountyCode(returnRecord, pcfRiskCountyCode);
            }
            // entityId
            EntityFields.setEntityId(returnRecord, EntityFields.getEntityId(inputRecord));

            // ratingBasis
            r = new Record();
            r.setFields(inputRecord);
            RiskFields.setRiskTypeCode(r, riskTypeCode);
            RiskRelationFields.setNiRiskTypeCode(r, riskTypeCode);
            String ratingBasis = isNiPremiumEditable(r).booleanValue() ? "0" : null;
            RiskRelationFields.setRatingBasis(returnRecord, ratingBasis);

            // productCoverageCode
            r = new Record();
            CoverageFields.setCovPartCode(r, "X");
            PolicyHeaderFields.setPolicyId(r, policyHeader.getPolicyId());
            RiskRelationFields.setNiRiskTypeCode(r, riskTypeCode);
            RiskRelationFields.setPracticeStateCode(r, RiskRelationFields.getPracticeStateCode(returnRecord));
            RiskRelationFields.setNiRetroDate(r, null);
            TransactionFields.setTransactionEffectiveFromDate(r, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
            String prodCovgCode = getRiskRelationDAO().getNICoverage(r);
            CoverageFields.setProductCoverageCode(returnRecord, prodCovgCode);

            RiskRelationFields.setCompanyInsured(returnRecord, "NI");
        }

        PMCommonFields.setRecordModeCode(returnRecord, RecordMode.TEMP);
        RiskRelationFields.setRiskRelationStatus(returnRecord, PMStatusCode.PENDING);

        // Get the default risk entitlement values
        RiskRelationEntitlementRecordLoadProcessor.setInitialEntitlementValuesForRiskRelation(
            policyHeader, getCompanyInsuredValue(policyHeader, returnRecord), returnRecord);

        // Set original value
        origFieldLoadProcessor.postProcessRecord(returnRecord, true);

        return returnRecord;
    }

    /**
     * To load all available risks from other policies for adding company insured risk relation.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord a Record with entityId
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of all available policies.
     */
    public RecordSet loadAllAvailableRiskForCompanyInsuredRiskRelation(PolicyHeader policyHeader,
                                                                       Record inputRecord,
                                                                       RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableRiskForCompanyInsuredRiskRelation",
                new Object[]{policyHeader});
        }

        Record record = policyHeader.toRecord();
        record.setFields(inputRecord, false);

        // If generate from copy prior acts stats, no entity will be selected and risk related entity will be used.
        // Then nose retroactive date is used for term effective date.
        if (inputRecord.hasStringValue(RequestIds.IS_COPY_ACTS_STATS) && 
            YesNoFlag.getInstance(inputRecord.getStringValue(RequestIds.IS_COPY_ACTS_STATS)).booleanValue()) {
            String entityId = policyHeader.getRiskHeader().getRiskEntityId();
            record.setFieldValue(RequestIds.ENTITY_ID, entityId);
            PolicyFields.setTermEffectiveFromDate(record, PolicyFields.getTermEffectiveFromDate(inputRecord));
            PolicyFields.setTermEffectiveToDate(record, PolicyFields.getTermEffectiveToDate(inputRecord));
            TransactionFields.setTransactionEffectiveFromDate(record, null);
            RiskRelationFields.setCurrentRiskTypeCode(record, null);
        }

        // Get available Manuscript record set
        RecordLoadProcessor entitlementLoadProcessor = new SelCompInsRiskRelEntitlementRecordLoadProcessor(policyHeader);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            loadProcessor, entitlementLoadProcessor);
        RecordSet rs = getRiskRelationDAO().loadAllAvailableRiskForCompanyInsuredRiskRelation(record, loadProcessor);

        // Show error message if there's no record
        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.selectCompInsRisk.noData.error");
            rs.getSummaryRecord().setFieldValue("isDoneAvailable", YesNoFlag.N);
        }
        else {
            rs.getSummaryRecord().setFieldValue("isDoneAvailable", YesNoFlag.Y);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableRiskForCompanyInsuredRiskRelation", rs);
        }
        return rs;
    }

    /**
     * To load all available risks for adding policy insured risk relation.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllAvailableRiskForPolicyInsuredRiskRelation(PolicyHeader policyHeader,
                                                                      RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableRiskForPolicyInsuredRiskRelation",
                new Object[]{policyHeader});
        }

        Record record = policyHeader.toRecord();
        RiskRelationFields.setRetCanRisks(record,
            SysParmProvider.getInstance().getSysParm(SysParmIds.PM_RSKREL_PI_ALLWCAN, "N"));

        // Get available Manuscript record set
        RecordSet rs = getRiskRelationDAO().loadAllAvailableRiskForPolicyInsuredRiskRelation(record, loadProcessor);

        // Show error message if there's no record
        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.selectPolInsRisk.noData.error");
            rs.getSummaryRecord().setFieldValue("isDoneAvailable", YesNoFlag.N);
        }
        else {
            rs.getSummaryRecord().setFieldValue("isDoneAvailable", YesNoFlag.Y);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableRiskForPolicyInsuredRiskRelation", rs);
        }
        return rs;
    }

    /**
     * To load all available risks for adding multi risk company insured risk relation.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord record with neccessary input information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllAvailableMultiRiskForCompanyInsuredRiskRelation(PolicyHeader policyHeader,
                                           Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableMultiRiskForCompanyInsuredRiskRelation",
                new Object[]{policyHeader});
        }

        Record record = policyHeader.toRecord();
        RiskRelationFields.setRetCanRisks(record,
            SysParmProvider.getInstance().getSysParm(SysParmIds.PM_RSKREL_PI_ALLWCAN, "N"));

        String policyId;
        if (inputRecord.hasStringValue("policyList")) {
            policyId = inputRecord.getStringValue("policyList");
        }
        else {
            policyId = getRiskRelationDAO().loadAvailablePolicyForCompanyInsuredRisk(policyHeader, inputRecord);
        }

        record.setFieldValue("riskBaseRecordId", "0");
        record.setFieldValue("policyId", policyId);

        RecordSet rs = getRiskRelationDAO().loadAllAvailableRiskForPolicyInsuredRiskRelation(record, loadProcessor);

        // Show error message if there's no record
        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.selectPolInsRisk.noData.error");
            rs.getSummaryRecord().setFieldValue("isDoneAvailable", YesNoFlag.N);
        }
        else {
            rs.getSummaryRecord().setFieldValue("isDoneAvailable", YesNoFlag.Y);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableMultiRiskForCompanyInsuredRiskRelation", rs);
        }
        return rs;
    }

    /**
     * To check if the Non-Insured Premium can be editable or not.
     *
     * @param inputRecord a record loaded with query conditions
     * @return YesNoFlag to indicate field ratingBasis can be editable or not.
     */
    public YesNoFlag isNiPremiumEditable(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isNiPremiumEditable", new Object[]{inputRecord});
        }

        YesNoFlag isEditable;
        String enterNiPremiumSysPara = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_ENTER_NINS_PREM, "Y");
        if (YesNoFlag.getInstance(enterNiPremiumSysPara).booleanValue()) {
            isEditable = YesNoFlag.Y;
        }
        else {
            int count = getRiskRelationDAO().getNIPremiumCount(inputRecord);
            if (count > 0) {
                isEditable = YesNoFlag.Y;
            }
            else {
                isEditable = YesNoFlag.N;
            }
        }

        l.exiting(getClass().getName(), "isNiPremiumEditable", isEditable);
        return isEditable;
    }

    /**
     * To handle item 1 of Rate Enablement for GDR71.5 Row Level Attribute.
     * That is to check ratingBasis field's availability.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord a record loaded with query conditions
     * @return a record with availability of ratingBasis field
     */
    public Record isRatingBasisEditable(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRatingBasisEditable", new Object[]{inputRecord});
        }

        Record outputRecord = new Record();
        inputRecord.setFields(policyHeader.toRecord(), false);

        // GDR71.5 Row Level Attributes
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        String transEffFromDateStr = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        String riskRelEffFromDateStr = RiskRelationFields.getRiskRelEffectiveFromDate(inputRecord);
        String riskRelEffToDateStr = RiskRelationFields.getRiskRelEffectiveToDate(inputRecord);
        if (inputRecord.hasFieldValue("screenModeCode")) {
            screenMode = ScreenModeCode.getInstance(inputRecord.getStringValue("screenModeCode"));
        }
        RecordMode recordMode = PMCommonFields.getRecordModeCode(inputRecord);
        RiskRelationFields.setCurrentRiskTypeCode(inputRecord, policyHeader.getRiskHeader().getRiskTypeCode());
        if (!screenMode.isViewPolicy() && !screenMode.isViewEndquote() &&
            !screenMode.isCancelWIP() && !screenMode.isResinstateWIP()) {
            // #2

            // get company insured value first
            String isCompanyInsuredStr = getCompanyInsuredValue(policyHeader, inputRecord);

            Date transEffFromDate = DateUtils.parseDate(transEffFromDateStr);
            String riskEffFromDateStr = RiskFields.getRiskEffectiveFromDate(inputRecord);
            String riskEffToDateStr = RiskFields.getRiskEffectiveToDate(inputRecord);
            Date riskEffFromDate = DateUtils.parseDate(riskEffFromDateStr);
            Date riskEffToDate = DateUtils.parseDate(riskEffToDateStr);
            Date riskRelEffFromDate = DateUtils.parseDate(riskRelEffFromDateStr);
            Date riskRelEffToDate = DateUtils.parseDate(riskRelEffToDateStr);
            if (inputRecord.hasStringValue(RiskRelationFields.OFFICIAL_RECORD_ID) &&
                Long.parseLong(RiskRelationFields.getOfficialRecordId(inputRecord)) > 0 && !recordMode.isOfficial()) {
                if (inputRecord.hasStringValue(RiskFields.AFTER_IMAGE_RECORD_B) &&
                    RiskFields.getAfterImageRecordB(inputRecord).booleanValue()) {
                    enableRatingBasis(policyHeader, inputRecord, outputRecord, transEffFromDate, riskEffFromDate,
                        riskEffToDate, isCompanyInsuredStr);
                }
                else { // afterImageB is N
                    // disables the ratingBasis field
                    outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
                }
            }
            else { // officialRecordId <= 0 or is in OFFICIAL mode
                // status is PENDING
                if (RiskRelationFields.getRiskRelationStatus(inputRecord).isPending()) {
                    enableRatingBasis(policyHeader, inputRecord, outputRecord, transEffFromDate, riskEffFromDate,
                        riskEffToDate, isCompanyInsuredStr);
                }
                // status is ACTIVE
                else if (RiskRelationFields.getRiskRelationStatus(inputRecord).isActive()) {
                    if (screenMode.isOosWIP() ||
                        RiskRelationFields.isPolicyInsured(inputRecord) &&
                            !inputRecord.hasStringValue(RiskRelationFields.RISK_PROCESS_CODE) ||
                        transEffFromDate.before(riskRelEffFromDate) ||
                        transEffFromDate.after(riskRelEffToDate) ||
                        RiskRelationFields.isCompanyInsured(inputRecord) &&
                            !RiskRelationFields.getOverrideStatsB(inputRecord).booleanValue()) {
                        if (!YesNoFlag.getInstance(isCompanyInsuredStr).booleanValue() &&
                            RiskRelationFields.isCompanyInsured(inputRecord)) {
                            if (!isDateChangeAllowedAndTransDateNotInRiskDates(
                                policyHeader, transEffFromDate, riskEffFromDate, riskEffToDate)) {
                                outputRecord.setFieldValue("isAnnualPremiumEditable", isNiPremiumEditable(inputRecord));
                            }
                            else {
                                outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
                            }
                        }
                        else {
                            // disables the ratingBasis field
                            outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
                        }
                    }
                    else if (YesNoFlag.getInstance(isCompanyInsuredStr).booleanValue() &&
                            RiskRelationFields.isCompanyInsured(inputRecord)) {
                        // disable the ratingBasis field if it is base CI
                        outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
                    }
                    else {
                        if (!isDateChangeAllowedAndTransDateNotInRiskDates(
                            policyHeader, transEffFromDate, riskEffFromDate, riskEffToDate)) {
                            outputRecord.setFieldValue("isAnnualPremiumEditable", isNiPremiumEditable(inputRecord));
                        }
                        else {
                            outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
                        }
                    }
                }
                // all the other status
                else {
                    // disables the ratingBasis field
                    outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
                }
            }
        }

        if (!policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue()){
            if ((screenMode.isManualEntry() || screenMode.isWIP() || screenMode.isRenewWIP() || screenMode.isOosWIP())
                && DateUtils.isTargetDateNotInDatesPeriod(transEffFromDateStr, riskRelEffFromDateStr, riskRelEffToDateStr)){
                outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
            }
        }

        l.exiting(getClass().getName(), "isRatingBasisEditable", outputRecord);
        return outputRecord;
    }

    /**
     * To get product coverage code for NI coverage.
     *
     * @param inputRecord a record loaded with query conditions
     * @return product coverage code
     */
    public String getNICoverage(Record inputRecord) {
        CoverageFields.setCovPartCode(inputRecord, "X");
        String prodCovgCode = getRiskRelationDAO().getNICoverage(inputRecord);
        if (StringUtils.isBlank(prodCovgCode)) {
            String entityName = getEntityManager().getEntityName(EntityFields.getEntityId(inputRecord));
            MessageManager.getInstance().addErrorMessage("pm.maintainRiskRelation.prodCovgCode.error",
                new String[]{entityName});
            throw new ValidationException();
        }
        return prodCovgCode;
    }

    private void enableRatingBasis(PolicyHeader policyHeader,
                                   Record record,
                                   Record outputRecord,
                                   Date transEffFromDate,
                                   Date riskEffFromDate,
                                   Date riskEffToDate,
                                   String isCompanyInsuredStr) {
        if (RiskRelationFields.isPolicyInsured(record) && !record.hasStringValue(RiskRelationFields.RISK_PROCESS_CODE) ||
            RiskRelationFields.isCompanyInsured(record) && !RiskRelationFields.getOverrideStatsB(record).booleanValue()) {
            if (!YesNoFlag.getInstance(isCompanyInsuredStr).booleanValue() && RiskRelationFields.isCompanyInsured(record)) {
                if (!isDateChangeAllowedAndTransDateNotInRiskDates(
                    policyHeader, transEffFromDate, riskEffFromDate, riskEffToDate)) {
                    // GDR71.7 Rate Enablement: #1
                    outputRecord.setFieldValue("isAnnualPremiumEditable", isNiPremiumEditable(record));
                }
                else {
                    outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
                }
            }
            else {
                // disable the ratingBasis field
                outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
            }
        }
        else if (YesNoFlag.getInstance(isCompanyInsuredStr).booleanValue() &&
                RiskRelationFields.isCompanyInsured(record)) {
            // disable the ratingBasis field if it is base CI
            outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
        }
        else {
            if (!isDateChangeAllowedAndTransDateNotInRiskDates(
                policyHeader, transEffFromDate, riskEffFromDate, riskEffToDate)) {
                outputRecord.setFieldValue("isAnnualPremiumEditable", isNiPremiumEditable(record));
            }
            else {
                outputRecord.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
            }
        }
    }

    private boolean isDateChangeAllowedAndTransDateNotInRiskDates(PolicyHeader policyHeader,
                                                                  Date transEffFromDate,
                                                                  Date riskEffFromDate,
                                                                  Date riskEffToDate) {
        if (policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue()) {
            if (transEffFromDate.before(riskEffFromDate) ||
                transEffFromDate.after(riskEffToDate) || transEffFromDate.equals(riskEffToDate)) {
                return true;
            }
        }
        return false;
    }

    private YesNoFlag isFteAvailable(Record record) {
        int fteCount = getRiskRelationDAO().getNIFteCount(record);
        return fteCount > 0 ? YesNoFlag.Y : YesNoFlag.N;
    }

    private String addParm(Record inputRecord, String parmName, String mapFieldName) {
        return addParm(parmName, inputRecord.getStringValue(mapFieldName));
    }

    private String addParm(String parmName, String parmValue) {
        StringBuffer parm = new StringBuffer(parmName);
        if (StringUtils.isBlank(parmValue)) {
            parmValue = "";
        }
        parm.append("^").append(parmValue).append("^");
        return parm.toString();
    }

    private boolean isFieldChanged(Record r, String fieldName) {
        String oldFieldName = "old" + StringUtils.capitalizeFirstLetter(fieldName);
        return !r.hasStringValue(oldFieldName) && r.hasStringValue(fieldName) ||
            r.hasStringValue(oldFieldName) && !r.getStringValue(oldFieldName).equals(r.getStringValue(fieldName));
    }

    /**
     * To check is the new record overlap with the one of exist records.
     *
     * @param tmpRiskEffFromDate effective date of the one of exist records
     * @param tmpRiskEffToDate expired date of the one of exist records
     * @param riskEffFromDate effective date of the new record
     * @param riskEffToDate expired date of the new record
     * @return boolean to indicate the new record is overlap or not.
     */
    private boolean isOverlap(Date tmpRiskEffFromDate,Date tmpRiskEffToDate,Date riskEffFromDate,Date riskEffToDate) {
        if (!tmpRiskEffFromDate.equals(tmpRiskEffToDate) && !riskEffFromDate.equals(riskEffToDate)) {
            if (tmpRiskEffToDate.after(riskEffFromDate) && tmpRiskEffFromDate.before(riskEffToDate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle UC22.3 Common Initial Values
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord a record loaded with user entered data
     * @param outputRecord a Record loaded with initial values.
     */
    private void mergeCommonInitialValuesForAddRiskRelation(PolicyHeader policyHeader,
                                                            Record inputRecord,
                                                            Record outputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mergeCommonInitialValuesForAddRiskRelation",
                new Object[]{policyHeader, inputRecord});
        }

        // riskName
        String riskName;
        String addCode = inputRecord.hasStringValue(RiskFields.ADD_CODE) ? RiskFields.getAddCode(inputRecord) : "";
        String entityId = EntityFields.getEntityId(inputRecord);
        String riskTypeDesc;
        if (RiskRelationFields.isPolicyInsured(inputRecord) &&
            addCode.equals("LOCATION")) {
            String location = RiskFields.getLocation(inputRecord);
            riskName = getEntityManager().getEntityPropertyName(location);
        }
        else if (RiskRelationFields.isPolicyInsured(inputRecord) &&
            (addCode.equals("SLOT") || addCode.equals("FTE"))) {
            riskName = getRiskDAO().getRiskTypeDefinition(RiskFields.getRiskBaseRecordId(inputRecord));

            if (!StringUtils.isBlank(entityId) && Long.parseLong(entityId) > 0) {
                String occupName = getEntityManager().getEntityName(entityId);
                riskName = riskName + "-" + occupName;
            }
        }
        else {
            riskName = getEntityManager().getEntityName(entityId);
        }
        // get risk type description for PI type
        if (RiskRelationFields.isPolicyInsured(inputRecord)) {
            riskTypeDesc = getRiskDAO().getRiskTypeDescription(RiskFields.getRiskBaseRecordId(inputRecord));
            riskName = riskName + " (" + riskTypeDesc + ")";
        }
        RiskFields.setRiskName(outputRecord, riskName);

        String riskEffFromDateStr = RiskFields.getOrigRiskEffectiveFromDate(inputRecord);
        String transEffDateStr = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();

        Date riskEffFromDate = DateUtils.parseDate(riskEffFromDateStr);
        Date transEffDate = DateUtils.parseDate(transEffDateStr);

        String rrelEffFromDate;
        if (transEffDate.after(riskEffFromDate)) {
            rrelEffFromDate =  transEffDateStr;
        }
        else {
            rrelEffFromDate =  riskEffFromDateStr;
        }

        // risk relation effectiveFromDate
        RiskRelationFields.setRiskRelEffectiveFromDate(outputRecord, rrelEffFromDate);

        // risk relation effectiveToDate
        RiskRelationFields.setRiskRelEffectiveToDate(outputRecord, policyHeader.getTermEffectiveToDate());

        // transactionLogId
        TransactionFields.setTransactionLogId(outputRecord, policyHeader.getLastTransactionId());

        // childRiskType
        RiskRelationFields.setChildRiskType(outputRecord, policyHeader.getRiskHeader().getRiskTypeCode());

        // childPolicyType
        RiskRelationFields.setChildPolicyType(outputRecord, policyHeader.getPolicyTypeCode());

        // riskRelationTypeCode
        String riskRelTypeCode;
        Record r = new Record();
        if (RiskRelationFields.isPolicyInsured(inputRecord) &&
            inputRecord.hasStringValue(RiskRelationFields.RISK_RELATION_TYPE_CODE)) {
            riskRelTypeCode = RiskRelationFields.getRiskRelationTypeCode(inputRecord);
        }
        else {
            DefaultLevelFields.setLevel(r, DefaultLevelFields.RISK_RELATION_RELTYP_DFLT);
            PolicyHeaderFields.setTermEffectiveFromDate(r, policyHeader.getTermEffectiveFromDate());
            TransactionFields.setTransactionEffectiveFromDate(r, policyHeader.getTermEffectiveFromDate());
            TransactionFields.setTransactionLogId(r, policyHeader.getLastTransactionId());
            DefaultLevelFields.setCode1(r, DefaultLevelFields.POLICY_TYPE_CODE);
            DefaultLevelFields.setValue1(r, policyHeader.getPolicyTypeCode());
            DefaultLevelFields.setCode2(r, DefaultLevelFields.INSURED_TYPE);
            DefaultLevelFields.setValue2(r, RiskRelationFields.getCompanyInsured(inputRecord));
            riskRelTypeCode = getPmDefaultManager().getDefaultValue(r);
            if (inputRecord.hasStringValue(RiskRelationFields.RISK_RELATION_TYPE_CODE)) {
                riskRelTypeCode = RiskRelationFields.getRiskRelationTypeCode(inputRecord);
            }
        }
        RiskRelationFields.setRiskRelationTypeCode(outputRecord, riskRelTypeCode);

        // toRateB
        String toRateB;
        if (RiskRelationFields.isPolicyInsured(inputRecord) &&
            inputRecord.hasStringValue(RiskRelationFields.TO_RATE_B)) {
            toRateB = RiskRelationFields.getToRateB(inputRecord);
        }
        else {
            DefaultLevelFields.setLevel(r, DefaultLevelFields.RISK_RELATION_RATE_DFLT);
            PolicyHeaderFields.setTermEffectiveFromDate(r, policyHeader.getTermEffectiveFromDate());
            TransactionFields.setTransactionEffectiveFromDate(r, policyHeader.getTermEffectiveFromDate());
            TransactionFields.setTransactionLogId(r, "0");
            DefaultLevelFields.setCode1(r, DefaultLevelFields.POLICY_TYPE_CODE);
            DefaultLevelFields.setValue1(r, policyHeader.getPolicyTypeCode());
            DefaultLevelFields.setCode2(r, DefaultLevelFields.INSURED_TYPE);
            DefaultLevelFields.setValue2(r, RiskRelationFields.getCompanyInsured(inputRecord));
            toRateB = getPmDefaultManager().getDefaultValue(r);
            if (inputRecord.hasStringValue(RiskRelationFields.TO_RATE_B)) {
                toRateB = RiskRelationFields.getToRateB(inputRecord);    
            }
        }
        // Set "N" by default, if no value is chosen. 
        if (StringUtils.isBlank(toRateB)){
            toRateB = YesNoFlag.N.getName();
        }
        RiskRelationFields.setToRateB(outputRecord, toRateB);

        // for PI/NI relationship, riskEffectiveFromDate and riskEffectiveToDate
        if (RiskRelationFields.isPolicyInsured(inputRecord) ||
            RiskRelationFields.isNonInsured(inputRecord)) {
            RiskFields.setRiskEffectiveFromDate(outputRecord,
                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());

            if (policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue()) {
                r = new Record();
                RiskFields.setRiskBaseRecordId(r, policyHeader.getRiskHeader().getRiskBaseRecordId());
                RiskFields.setRiskEffectiveFromDate(r, RiskFields.getRiskEffectiveFromDate(inputRecord));
                String maxContiguousDateStr = getRiskManager().getRiskExpDate(r);
                String termExpDateStr = policyHeader.getTermEffectiveToDate();
                if (!StringUtils.isBlank(maxContiguousDateStr)) {
                    Date maxContiguousDate = DateUtils.parseDate(maxContiguousDateStr);
                    Date termExpDate = DateUtils.parseDate(termExpDateStr);
                    if (maxContiguousDate.before(termExpDate)) {
                        RiskFields.setRiskEffectiveToDate(outputRecord, maxContiguousDateStr);
                        // also set risk relation effective to date
                        RiskRelationFields.setRiskRelEffectiveToDate(outputRecord, maxContiguousDateStr);
                    }
                    else {
                        RiskFields.setRiskEffectiveToDate(outputRecord, termExpDateStr);
                    }
                }
                else {
                    RiskFields.setRiskEffectiveToDate(outputRecord, termExpDateStr);
                }
            }
            else {                                     
                RiskFields.setRiskEffectiveToDate(outputRecord, policyHeader.getTermEffectiveToDate());
            }

            String rskRelParm = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_RSKREL_PI_ALLWCAN, "N");
            if (RiskRelationFields.isPolicyInsured(inputRecord) &&
                YesNoFlag.getInstance(rskRelParm).booleanValue()) {
                r = new Record();
                PolicyHeaderFields.setPolicyId(r, policyHeader.getPolicyId());
                EntityFields.setEntityId(r, entityId);
                PolicyHeaderFields.setTermEffectiveFromDate(r, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                PolicyHeaderFields.setTermEffectiveToDate(r, policyHeader.getTermEffectiveToDate());
                RiskFields.setRiskBaseRecordId(r, RiskFields.getRiskBaseRecordId(inputRecord));
                Record retRec = getRiskRelationDAO().getInitialValuesForAddCompINRiskRelation(r);
                String riskExpDate = RiskRelationFields.getRiskExp(retRec);
                if (!StringUtils.isBlank(riskExpDate)) {
                    RiskFields.setRiskEffectiveToDate(outputRecord, riskExpDate);
                }
            }
        }

        // addNiCoverageB
        r = new Record();
        PolicyHeaderFields.setPolicyTypeCode(r, policyHeader.getPolicyTypeCode());
        RiskRelationFields.setCurrentRiskTypeCode(r, policyHeader.getRiskHeader().getRiskTypeCode());
        RiskRelationFields.setRiskRelationTypeCode(r, null);
        PolicyHeaderFields.setTermEffectiveFromDate(r, policyHeader.getTermEffectiveFromDate());
        PolicyHeaderFields.setTermEffectiveToDate(r, policyHeader.getTermEffectiveToDate());
        String addNiCoverageB = getRiskRelationDAO().getAddNICoverageB(r);
        RiskRelationFields.setAddNiCoverageB(outputRecord, YesNoFlag.getInstance(addNiCoverageB));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "mergeCommonInitialValuesForAddRiskRelation", outputRecord);
        }
    }

    /**
     * Get company insured value
     *
     * @param policyHeader
     * @param record
     * @return
     */
    public String getCompanyInsuredValue(PolicyHeader policyHeader, Record record) {
        Record r = new Record();
        RiskRelationFields.setTypeCode(r, RiskRelationFields.RiskRelationTypeCodeValues.COMP_INSURED_RISK_TYPES);
        RiskRelationFields.setPracticeStateCode(r, RiskRelationFields.getPracticeStateCode(record));
        RiskRelationFields.setCurrentRiskTypeCode(r, policyHeader.getRiskHeader().getRiskTypeCode());
        PolicyHeaderFields.setTermEffectiveFromDate(r, policyHeader.getTermEffectiveFromDate());
        PolicyHeaderFields.setTermEffectiveToDate(r, policyHeader.getTermEffectiveToDate());
        return getRiskRelationDAO().getCompanyInsured(r);
    }

    /**
     * To get initial values for adding Non-base Company Insured Risk Relation record.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values.
     */
    public Record getInitialValuesForAddNonBaseCompanyInsured(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddNonBaseCompIndRiskRelation",
                new Object[]{policyHeader, inputRecord});
        }

        Record returnRecord = new Record();

        // Firstly, merge default value from web work bench configuration
        Record configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_RISKRELATION_ACTION_CLASS_NAME);
        if (configuredDefaultValues != null) {
            returnRecord.setFields(configuredDefaultValues);
        }

        // Secondly, merge the Common Initial Values (UC22.3)
        mergeCommonInitialValuesForAddRiskRelation(policyHeader, inputRecord, returnRecord);

        PolicyHeaderFields.setTermEffectiveFromDate(returnRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        PolicyHeaderFields.setTermEffectiveToDate(returnRecord, policyHeader.getTermEffectiveToDate());
        String riskTypeCode = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_NONINS_RISKTYPE);
        RiskFields.setRiskTypeCode(returnRecord, riskTypeCode);
        RiskRelationFields.setRiskProcessCode(returnRecord, "INSOWNER");
        RiskFields.setRiskEffectiveFromDate(returnRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        RiskFields.setRiskEffectiveToDate(returnRecord, policyHeader.getTermEffectiveToDate());
        RiskRelationFields.setRiskRelEffectiveFromDate(returnRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        RiskRelationFields.setRiskRelEffectiveToDate(returnRecord, policyHeader.getTermEffectiveToDate());
        RiskRelationFields.setPracticeStateCode(returnRecord,inputRecord.getStringValue("origPracticeStateCode"));
        EntityFields.setEntityId(returnRecord, EntityFields.getEntityId(inputRecord));
        String riskBaseId = getRiskDAO().getRiskBaseId(inputRecord);
        RiskRelationFields.setRiskParentId(returnRecord, riskBaseId);

        // riskId
        RiskFields.setRiskId(returnRecord, String.valueOf(getDbUtilityManager().getNextSequenceNo().longValue()));
        // riskName
        String riskName = getEntityManager().getEntityName(EntityFields.getEntityId(inputRecord));
        RiskFields.setRiskName(returnRecord, riskName);
        RiskRelationFields.setChildPolicyNo(returnRecord, policyHeader.getPolicyNo());
        RiskRelationFields.setCountyCodeUsedToRate(returnRecord, RiskRelationFields.getRiskCountyCode(inputRecord));
        RiskRelationFields.setPcfRiskCountyCode(returnRecord, RiskRelationFields.getPcfRiskCountyCode(inputRecord));
        RiskRelationFields.setAddNiCoverageB(returnRecord, YesNoFlag.N);
        RiskRelationFields.setOverrideStateB(returnRecord, YesNoFlag.N);
        RiskRelationFields.setRatingBasis(returnRecord, null);
        RiskRelationFields.setCompanyInsured(returnRecord, "CI");

        PMCommonFields.setRecordModeCode(returnRecord, RecordMode.TEMP);
        RiskRelationFields.setRiskRelationStatus(returnRecord, PMStatusCode.PENDING);

        // Get the default risk entitlement values
        RiskRelationEntitlementRecordLoadProcessor.setInitialEntitlementValuesForRiskRelation(
            policyHeader, "N", returnRecord);

        // Set original value
        origFieldLoadProcessor.postProcessRecord(returnRecord, true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddNonBaseCompIndRiskRelation", returnRecord);
        }

        return returnRecord;
    }

    /**
     * Get expiration date based on configuration
     *
     * @param policyHeader
     * @return
     */
    public String getConfiguredExpDate(PolicyHeader policyHeader) {
        String expDate = "01/01/3000";
        boolean isRenewRiskRel = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_OOS_RENEW_RREL, "Y")).booleanValue();
        TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();

        if (!isRenewRiskRel && transactionCode.isOosEndorsement()) {
            expDate = policyHeader.getTermEffectiveToDate();
        }
        return expDate;
    }

    /**
     * Determines whether the parent window might need to be refreshed.
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public YesNoFlag isRefreshRequired(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRefreshRequired",
                new Object[]{policyHeader, inputRecord});
        }

        YesNoFlag isRequired = null;
        boolean hasRefreshB = inputRecord.hasStringValue("refreshParentB");
        
        // We set the flag to refresh or not the parent as follows:
        // 1. If the flag is specifically passed in, then use that value.
        // 2. If the flag is not passed in, then check whether the latest transaction was a Risk Relation transaction
        if (hasRefreshB) {
            isRequired = YesNoFlag.getInstance(inputRecord.getStringValue("refreshParentB"));    
        }
        
        if (isRequired == null) {
            TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();
            if (!transactionCode.isRrelCancel() && !transactionCode.isRRelRein()) {
                isRequired = YesNoFlag.N;
            }
            else {
                isRequired = YesNoFlag.Y;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isRefreshRequired", isRequired);
        }

        return isRequired;

    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getRiskRelationDAO() == null)
            throw new ConfigurationException("The required property 'riskRelationDAO' is missing.");
        if (getRiskDAO() == null)
            throw new ConfigurationException("The required property 'riskDAO' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        if (getPmDefaultManager() == null)
            throw new ConfigurationException("The required property 'pmDefaultManager' is missing.");
        if (getCancelProcessManager() == null)
            throw new ConfigurationException("The required property 'cancelProcessManager' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    public RiskRelationManagerImpl() {}

    public RiskRelationDAO getRiskRelationDAO() {
        return m_riskRelationDAO;
    }

    public void setRiskRelationDAO(RiskRelationDAO riskRelationDAO) {
        m_riskRelationDAO = riskRelationDAO;
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public PMDefaultManager getPmDefaultManager() {
        return m_pmDefaultManager;
    }

    public void setPmDefaultManager(PMDefaultManager pmDefaultManager) {
        m_pmDefaultManager = pmDefaultManager;
    }

    public RiskDAO getRiskDAO() {
        return m_riskDAO;
    }

    public void setRiskDAO(RiskDAO riskDAO) {
        m_riskDAO = riskDAO;
    }

    public CancelProcessManager getCancelProcessManager() {
        return m_cancelProcessManager;
    }

    public void setCancelProcessManager(CancelProcessManager cancelProcessManager) {
        m_cancelProcessManager = cancelProcessManager;
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    private RiskRelationDAO m_riskRelationDAO;
    private RiskDAO m_riskDAO;
    private RiskManager m_riskManager;
    private EntityManager m_entityManager;
    private DBUtilityManager m_dbUtilityManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private PMDefaultManager m_pmDefaultManager;
    private CancelProcessManager m_cancelProcessManager;
    private PolicyManager m_policyManager;

    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{StringUtils.capitalizeFirstLetter(RiskRelationFields.PRACTICE_STATE_CODE),
            StringUtils.capitalizeFirstLetter(RiskRelationFields.COUNTY_CODE_USED_TO_RATE),
            StringUtils.capitalizeFirstLetter(RiskRelationFields.RISK_CLASS_CODE),
            StringUtils.capitalizeFirstLetter(RiskRelationFields.RATING_BASIS),
            StringUtils.capitalizeFirstLetter(RiskFields.RISK_TYPE_CODE),
            StringUtils.capitalizeFirstLetter(RiskRelationFields.NI_RISK_TYPE_CODE),
            StringUtils.capitalizeFirstLetter(RiskRelationFields.NI_COVERAGE_LIMIT_CODE),
            StringUtils.capitalizeFirstLetter(RiskRelationFields.NI_RETRO_DATE),
            StringUtils.capitalizeFirstLetter(RiskRelationFields.NI_CURRENT_CARRIER_ID),
            StringUtils.capitalizeFirstLetter(CoverageFields.COVERAGE_ID)}, "old");
}
