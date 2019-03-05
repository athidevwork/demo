package dti.pm.coveragemgr.underlyingmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.manuscriptmgr.ManuscriptFields;
import dti.pm.coveragemgr.underlyingmgr.UnderlyingCoverageFields;
import dti.pm.coveragemgr.underlyingmgr.dao.UnderlyingCoverageDAO;
import dti.pm.coveragemgr.underlyingmgr.struts.MaintainUnderlyingCoverageAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.underlyingpolicymgr.UnderlyingPolicyFields;
import dti.pm.coveragemgr.underlyingmgr.UnderlyingCoverageManager;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.riskmgr.RiskFields;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import org.apache.commons.lang.ArrayUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for the underlying coverage manager.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 12, 2018
 *
 * @author wrong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  08/24/2018       wrong      188391 - Initial version.
 *  11/15/2018       wrong      197079 - Modified getInitialValuesForUnderlyingCoverage() to set eff date compatible
 *                                       with transaction effective date.
 * ---------------------------------------------------
 */
public class UnderlyingCoverageManagerImpl implements UnderlyingCoverageManager {

    private static final String ORIGINAL_FIELD_PREFIX = "orig";

    private static final String[] underlyingDateFields = new String[] {
        UnderlyingCoverageFields.EFFECTIVE_FROM_DATE,
        UnderlyingCoverageFields.EFFECTIVE_TO_DATE,
        UnderlyingCoverageFields.RENEW_B
    };

    private static final String[] underlyingDataFields  = new String[] {
        UnderlyingCoverageFields.POLICY_UNDER_POL_NO,
        UnderlyingCoverageFields.POLICY_UNDER_POL_ID,
        UnderlyingCoverageFields.UNDER_ISS_COMP_ENT_ID,
        UnderlyingCoverageFields.UNDER_POLICY_TYPE_CODE,
        UnderlyingCoverageFields.UNDER_COVERAGE_CODE,
        UnderlyingCoverageFields.POLICY_FORM_CODE,
        UnderlyingCoverageFields.COVERAGE_LIMIT_CODE,
        UnderlyingCoverageFields.UND_RETROACTIVE_DATE,
        UnderlyingCoverageFields.OUTPUT_B,
        UnderlyingCoverageFields.COMPANY_INSURED_B,
    };


    /**
     * load all underlying coverages
     *
     * @param policyHeader policy header
     * @return result recordset
     */
    public RecordSet loadAllUnderlyingCoverage(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllUnderlyingCoverage", new Object[]{policyHeader, inputRecord});
        }
        // set input record
        Record record = policyHeader.toRecord();
        record.setFieldValue(UnderlyingCoverageFields.RECORD_MODE_CODE, policyHeader.getRecordMode().getName());

        RecordLoadProcessor lp = new UnderlyingCoverageEntitlementRecordLoadProcessor(policyHeader);
        if (inputRecord.hasStringValue(UnderlyingCoverageFields.IS_POLICY_LEVEL)
            && inputRecord.getStringValue(UnderlyingCoverageFields.IS_POLICY_LEVEL).equals("Y")) {
            record.setFieldValue(CoverageFields.COVERAGE_BASE_RECORD_ID, "");
            lp = new UnderlyingPolCoverageEntitlementRecordLoadProcessor(policyHeader);
        }
        // record load processor
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(lp,
            new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, UnderlyingCoverageFields.POLICY_UNDERLYING_COVG_ID));
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(lp,
            new AddOrigFieldsRecordLoadProcessor((String[])ArrayUtils.addAll((String[])underlyingDateFields, (String[])underlyingDataFields)));
        RecordSet rs = getUnderlyingCoverageDAO().loadAllUnderlyingCoverage(record, lp);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllUnderlyingCoverage", rs);
        }

        return rs;
    }

    /**
     * load all active related coverages
     *
     * @param policyHeader policy header
     * @return result recordset
     */
    public RecordSet loadAvailableRelatedCoverage(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAvailableRelatedCoverage", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);
        // Loads available coverages for selection
        //RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
        RecordSet rs = getUnderlyingCoverageDAO().loadAvailableRelatedCoverage(inputRecord);
        rs.setFieldValueOnAll("REL_SELECT_IND", new Field(new Long(0)));

        if(rs.getSize() == 0){
            EntitlementFields.setReadOnly(rs.getSummaryRecord(), true);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAvailableRelatedCoverage", rs);
        }

        return rs;
    }

    /**
     * get initial values for underlying coverage add
     *
     * @param policyHeader policy header
     * @param inputRecord  contains parameters for adding new record
     * @return record contains all returned default values
     */
    public Record getInitialValuesForUnderlyingCoverage(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForUnderlyingCoverage", new Object[]{policyHeader});
        }

        Record resultRec = new Record();
        PolicyFields.setPolicyId(resultRec, policyHeader.getPolicyId());
        TransactionFields.setTransactionLogId(resultRec, policyHeader.getLastTransactionId());
        String addOperation = inputRecord.getStringValue("addOperation");
        Record configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(MaintainUnderlyingCoverageAction.class.getName());

        if (configuredDefaultValues != null) {
            resultRec.setFields(configuredDefaultValues);
        }
        if (addOperation.equals("ADD")) {
            String entityNameForNI = inputRecord.getStringValue("entityNameForNI");
            String underRiskEntityId = inputRecord.getStringValue("underRiskEntityId");
            String riskName = policyHeader.getRiskHeader().getRiskName();
            riskName = riskName.substring(0, riskName.indexOf("(") - 1);
            //set default values
            UnderlyingCoverageFields.setRiskName(resultRec, riskName);
            UnderlyingCoverageFields.setRiskType(resultRec, policyHeader.getRiskHeader().getRiskTypeCode());
            UnderlyingCoverageFields.setProductCoverageCode(resultRec, policyHeader.getCoverageHeader().getProductCoverageCode());
            UnderlyingCoverageFields.setPolicyUnderPolId(resultRec, "0");
            UnderlyingCoverageFields.setCompanyInsuredB(resultRec, YesNoFlag.N);
            UnderlyingCoverageFields.setPolicyNoReadOnlyB(resultRec, YesNoFlag.N);
            UnderlyingCoverageFields.setEffectiveToDate(resultRec, policyHeader.getTermEffectiveToDate());
            UnderlyingCoverageFields.setUnderRiskName(resultRec, entityNameForNI);
            UnderlyingCoverageFields.setUnderCovgType(resultRec, "NI");
            UnderlyingCoverageFields.setUnderRiskEntityId(resultRec, underRiskEntityId);
            PMCommonFields.setRecordModeCode(resultRec, RecordMode.TEMP);
            UnderlyingCoverageFields.setPolicyUnderCovgBaseId(resultRec, "0");

            if (inputRecord.hasStringValue(UnderlyingPolicyFields.POLICY_UNDER_POL_ID)) {
                inputRecord.setFields(policyHeader.toRecord(), false);
                Record initialVlauesRec = getUnderlyingCoverageDAO().getInitialValuesForUnderlyingCoverage(inputRecord);
                //If o_und_pol_form_cd is not NULL, override these columns of newly inserted record by the return values
                if (initialVlauesRec.hasStringValue("undPolFormCd")) {

                    UnderlyingCoverageFields.setPolicyUnderPolId(resultRec, UnderlyingPolicyFields.getPolicyUnderPolId(inputRecord));
                    UnderlyingCoverageFields.setPolicyUnderPolNo(resultRec, initialVlauesRec.getStringValue("undPolicyNo"));
                    UnderlyingCoverageFields.setUnderPolicyTypeCode(resultRec, initialVlauesRec.getStringValue("undPolTypeCd"));
                    UnderlyingCoverageFields.setUnderIssCompEntId(resultRec, initialVlauesRec.getStringValue("undIssCompEntId"));
                    UnderlyingCoverageFields.setEffectiveToDate(resultRec, initialVlauesRec.getStringValue("undExp"));
                    UnderlyingCoverageFields.setPolicyFormCode(resultRec, initialVlauesRec.getStringValue("undPolFormCd"));
                    UnderlyingCoverageFields.setCoverageLimitCode(resultRec, initialVlauesRec.getStringValue("undCovgLimitCd"));
                    UnderlyingCoverageFields.setUndRetroactiveDate(resultRec, initialVlauesRec.getStringValue("undRetroDate"));
                    UnderlyingCoverageFields.setCompanyInsuredB(resultRec, YesNoFlag.Y);
                    UnderlyingCoverageFields.setPolicyNoReadOnlyB(resultRec, YesNoFlag.Y);
                    UnderlyingCoverageFields.setPolicyUnderCovgBaseId(resultRec, initialVlauesRec.getStringValue("undCovgBaseId"));
                    UnderlyingCoverageFields.setUnderCoverageCode(resultRec, initialVlauesRec.getStringValue("undProdCovgCd"));
                    UnderlyingCoverageFields.setUnderRiskName(resultRec, initialVlauesRec.getStringValue("undRiskName"));
                    UnderlyingCoverageFields.setUnderRiskType(resultRec, initialVlauesRec.getStringValue("undRiskType"));
                    UnderlyingCoverageFields.setUnderCovgType(resultRec, "CI");
                    UnderlyingCoverageFields.setUnderRiskEntityId(resultRec, initialVlauesRec.getStringValue("undRiskEntityId"));
                    RiskFields.setPracticeStateCode(resultRec, initialVlauesRec.getStringValue("undStateCode"));
                }
                else {
                    MessageManager.getInstance().addErrorMessage("pm.maintainunderlyingPolicy.deteMustOverlap.error");
                    throw new ValidationException("policy dates must overlap");
                }
            }
        }
        else if (addOperation.equals("COPY")) {
            resultRec.setFields(inputRecord, true);
        }
        UnderlyingCoverageFields.setEffectiveFromDate(resultRec, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        //Set initial row st
        UnderlyingCoverageEntitlementRecordLoadProcessor.setInitialEntitlementValuesForUnderlyingCoverage(policyHeader, resultRec);
        return resultRec;
    }

    /**
     * save all underlying coverage data
     *
     * @param policyHeader policy header
     * @param inputRecords input recordset
     */
    public void saveAllUnderlyingCoverage(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllUnderlyingCoverage",
                new Object[]{policyHeader, inputRecords});
        }

        int updateCount = 0;

        //validate underlying coverage data
        validateSaveAllUnderlyingCoverage(policyHeader, inputRecords);
        // set transaction log Id from policy header
        inputRecords.setFieldValueOnAll(UnderlyingPolicyFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.DELETED}));
        changedRecords.setFieldsOnAll(policyHeader.toRecord(),false);

        RecordSet wipRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet offRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));

        //Delete the wip records marked for delete in batch mode
        RecordSet deletedRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        updateCount += getUnderlyingCoverageDAO().deleteAllUnderlyingCoverage(deletedRecords);

        //update official records
        offRecords.setFieldValueOnAll(UnderlyingCoverageFields.EFFECTIVE_FROM_DATE, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        RecordSet updateRecords = offRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        setFieldFlagExpireUnderlyingCovg(updateRecords);
        updateCount += getUnderlyingCoverageDAO().updateAllUnderlyingCoverage(updateRecords);

        // insert and updated records from WIP records.
        RecordSet modifiedRecords = PMRecordSetHelper.setRowStatusOnModifiedRecords(wipRecords);
        updateCount += getUnderlyingCoverageDAO().saveAllUnderlyingCoverage(modifiedRecords);

        validateUnderlyingOverlap(policyHeader);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllUnderlyingCoverage", String.valueOf(updateCount));
        }
    }

    /**
     * validate save all underlying coverage
     *
     * @param policyHeader policy header
     * @param inputRecords contains all inserted/modified records
     */
    protected void validateSaveAllUnderlyingCoverage(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSaveAllUnderlyingCoverage", new Object[]{inputRecords});
        }

        // Set the displayRecordNumber to all visible records.
        PMRecordSetHelper.setDisplayRecordNumberOnRecords(inputRecords);
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        Iterator recIter = changedRecords.getRecords();

        while (recIter.hasNext()) {
            Record record = (Record) recIter.next();
            String rowNum = String.valueOf(record.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));
            String rowId = UnderlyingCoverageFields.getPolicyUnderlyingCovgId(record);

            if (record.hasStringValue(UnderlyingCoverageFields.EFFECTIVE_FROM_DATE) &&
                record.hasStringValue(UnderlyingCoverageFields.EFFECTIVE_TO_DATE)) {
                String effDate = record.getStringValue(UnderlyingCoverageFields.EFFECTIVE_FROM_DATE);
                String expDate = record.getStringValue(UnderlyingCoverageFields.EFFECTIVE_TO_DATE);
                String tranEffDateStr = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
                String termExpDateStr = policyHeader.getTermEffectiveToDate();

                try {
                    if (DateUtils.daysDiff(effDate, expDate) < 0) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingCoverage.expLessTanEff.error",
                            UnderlyingCoverageFields.EFFECTIVE_TO_DATE, rowId);
                    }
                    if (DateUtils.daysDiff(tranEffDateStr, expDate) < 0 || DateUtils.daysDiff(termExpDateStr, expDate) > 0) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingCoverage.invalidDate.error",
                            new String[]{rowNum,
                                FormatUtils.formatDateForDisplay(tranEffDateStr),
                                FormatUtils.formatDateForDisplay(termExpDateStr)},"",rowId);
                    }
                }
                catch (ParseException e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the date.", e);
                    l.throwing(DateUtils.class.getName(), "validateSaveAllUnderlyingPolicy", ae);
                    throw ae;
                }
            }

            // #2. Retro active date validation
            if (record.hasStringValue(UnderlyingCoverageFields.POLICY_FORM_CODE) &&
                "CM".equals(UnderlyingCoverageFields.getPolicyFormCode(record))) {
                if (!record.hasStringValue(UnderlyingCoverageFields.UND_RETROACTIVE_DATE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingCoverage.retroDate.required.error",
                        UnderlyingCoverageFields.UND_RETROACTIVE_DATE, rowId);
                }
                else if (record.hasStringValue(UnderlyingCoverageFields.EFFECTIVE_FROM_DATE) &&
                         record.getStringValue(UnderlyingCoverageFields.COMPANY_INSURED_B).equals("N")) {
                    Date retroDate = record.getDateValue(UnderlyingCoverageFields.UND_RETROACTIVE_DATE);
                    Date effDate = record.getDateValue(UnderlyingCoverageFields.EFFECTIVE_FROM_DATE);
                    if (retroDate.after(effDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingCoverage.retroGreaterEff.error",
                            UnderlyingCoverageFields.UND_RETROACTIVE_DATE, rowId);
                    }
                }
            }

            validateForEditFields(record, inputRecords);

            // Check if any underlying coverage version which is from same official record and in same time period exists
            // when current record's official record fk is null and effective to date is changed.
            if (!StringUtils.isBlank(UnderlyingCoverageFields.getOfficialRecordId(record)) &&
                !StringUtils.isSame(UnderlyingCoverageFields.getEffectiveToDate(record), UnderlyingCoverageFields.getOrigEffectiveToDate(record))) {
                if (validateSameOffVersionExists(record).equals("Y")) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingCoverage.sameOffVersionExists.error");
                }
            }
        }

        // Overlap validation in front end.
        String[] validationFieldNames = new String[]{UnderlyingCoverageFields.POLICY_UNDER_POL_NO, UnderlyingCoverageFields.UNDER_COVERAGE_CODE};
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
            UnderlyingCoverageFields.EFFECTIVE_FROM_DATE,
            UnderlyingCoverageFields.EFFECTIVE_TO_DATE,
            UnderlyingCoverageFields.POLICY_UNDERLYING_COVG_ID,
            "pm.maintainUnderlyingCoverage.currentTermOverlapDates.error",
            validationFieldNames, validationFieldNames);

        RecordSet wipRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}))
            .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));
        continuityValidator.validate(wipRecords);

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("underlying coverage data invalid");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSaveAllUnderlyingCoverage");
        }
    }

    /**
     * validate editable fields
     *
     * @param record    the specified record
     * @param recordset contains all inserted/modified records
     */
    private void validateForEditFields(Record record, RecordSet recordset) {
        String rowNum = String.valueOf(record.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));
        String rowId = UnderlyingCoverageFields.getPolicyUnderlyingCovgId(record);
        //System prevent to change other attributes when revise date fields or vice versa.
        boolean official = PMCommonFields.hasRecordModeCode(record) ? PMCommonFields.getRecordModeCode(record).isOfficial() : false;
        boolean changedFromOfficialToTemp = false;
        String officialRecordId = null;
        if (record.hasStringValue(UnderlyingCoverageFields.OFFICIAL_RECORD_ID)) {
            officialRecordId = UnderlyingCoverageFields.getOfficialRecordId(record);
            if (Long.parseLong(officialRecordId) > 0 && !PMCommonFields.getRecordModeCode(record).isOfficial()) {
                for (int i = 0; i < recordset.getSize(); i++) {
                    Record otherRecord = recordset.getRecord(i);
                    // find the official record
                    if (TransactionFields.getTransactionLogId(record).equals(
                        UnderlyingCoverageFields.getClosingTransLogId(otherRecord)) &&
                        officialRecordId.equals(UnderlyingCoverageFields.getPolicyUnderlyingCovgId(otherRecord))) {
                        changedFromOfficialToTemp = true;
                        break;
                    }
                }
            }
        }
        // only those two status should validate :  1, official. 2, change status from official to temp.
        if (official || changedFromOfficialToTemp) {
            Record origRecord = record;
            boolean preventChangeDates = false;
            if (changedFromOfficialToTemp) {
                for (int i = 0; i < recordset.getSize(); i++) {
                    Record otherRecord = recordset.getRecord(i);
                    // find the official record
                    if (StringUtils.isSame(UnderlyingCoverageFields.getPolicyUnderlyingCovgId(otherRecord), officialRecordId)) {
                        origRecord = otherRecord;
                        break;
                    }
                }
                for (int i = 0; i < recordset.getSize(); i++) {
                    Record otherRecord = recordset.getRecord(i);
                    // if the other TEMP record with the same official record id exists, prevent user changing dates.
                    if (StringUtils.isSame(UnderlyingCoverageFields.getOfficialRecordId(otherRecord), officialRecordId)
                        && PMCommonFields.getRecordModeCode(otherRecord).isTemp()
                        && !StringUtils.isSame(UnderlyingCoverageFields.getPolicyUnderlyingCovgId(otherRecord), UnderlyingCoverageFields.getPolicyUnderlyingCovgId(record))) {
                        preventChangeDates = true;
                        origRecord = record;
                        break;
                    }
                }
            }
            // get current/original effective values
            int length = underlyingDateFields.length;
            String[] dateFieldsValue = new String[length];
            String[] origDateFieldsValue = new String[length];
            for (int i = 0; i < length; i ++) {
                if (record.hasField(underlyingDateFields[i])){
                    dateFieldsValue[i] = record.getStringValue(underlyingDateFields[i]);
                }
                if (record.hasField(ORIGINAL_FIELD_PREFIX + underlyingDateFields[i])){
                    origDateFieldsValue[i] = origRecord.getStringValue(ORIGINAL_FIELD_PREFIX + underlyingDateFields[i]);
                }
            }
            // get current/original data values
            length = underlyingDataFields.length;
            String[] dataFieldsValue = new String[length];
            String[] origDataFieldsValue  = new String[length];
            for (int i = 0; i < length; i ++) {
                if (record.hasField(underlyingDataFields[i])){
                    dataFieldsValue[i] = record.getStringValue(underlyingDataFields[i]);
                }
                if (record.hasField(ORIGINAL_FIELD_PREFIX + underlyingDataFields[i])){
                    origDataFieldsValue[i] = origRecord.getStringValue(ORIGINAL_FIELD_PREFIX + underlyingDataFields[i]);
                }
            }

            if (!Arrays.equals(dateFieldsValue, origDateFieldsValue)
                && (preventChangeDates || !Arrays.equals(dataFieldsValue, origDataFieldsValue))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainunderlyingCoverage.conflictEdit.error",  new String[]{rowNum}, "", rowId);
            }
        }
    }

    /**
     * All term Overlap validation in back end.
     *
     * @param policyHeader policy header
     */
    private void validateUnderlyingOverlap(PolicyHeader policyHeader) throws ValidationException {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateUnderlyingOverlap", new Object[] { policyHeader });
        }

        Record record = new Record();
        record.setFieldValue(PolicyHeaderFields.POLICY_ID, policyHeader.getPolicyId());
        record.setFieldValue(UnderlyingCoverageFields.COVERAGE_BASE_ID, policyHeader.getCoverageHeader().getCoverageBaseRecordId());

        String statusCode = getUnderlyingCoverageDAO().validateUnderlyingOverlap(record);
        if (!StringUtils.isBlank(statusCode)) {
            MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingCoverage.allTermOverlapDates.error");
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateUnderlyingOverlap");
        }
    }

    /**
     * Check if any underlying coverage version which is from same official record and in same time period exists.
     *
     * @param inputRecord
     */
    protected String validateSameOffVersionExists(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSameOffVersionExists", new Object[]{inputRecord});
        }
        String sameOffVersionExists = getUnderlyingCoverageDAO().validateSameOffVersionExists(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSameOffVersionExists");
        }
        return sameOffVersionExists;
    }

    /**
     * Set flag if only exp date change.
     *
     * @param records
     */
    protected void setFieldFlagExpireUnderlyingCovg(RecordSet records) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setFieldFlagExpireUnderlyingCovg", new Object[]{records});
        }
        Iterator it = records.getRecords();

        while (it.hasNext()) {
            Record record = (Record) it.next();
            if (OFFICIAL.equals(record.getStringValue("RECORDMODECODE")) &&
                !record.getStringValue(UnderlyingCoverageFields.EFFECTIVE_TO_DATE).equals(record.getStringValue(UnderlyingCoverageFields.ORIG_EFFECTIVE_TO_DATE))) {
                record.setFieldValue(UnderlyingCoverageFields.EXPIRED_B, "Y");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setFieldFlagExpireUnderlyingCovg");
        }
    }

    /**
     * load all active policies
     *
     * @param policyHeader policy header
     * @param inputRecord  input record
     * @return recordset of active policy list
     */
    public RecordSet loadAllActivePolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllActivePolicy", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);
        inputRecord.setFieldValue("context", "UNDERLYING_COVG");
        inputRecord.setFieldValue("parentCovgId", policyHeader.getCoverageHeader().getCoverageBaseRecordId());
        RecordSet rs = getUnderlyingCoverageDAO().loadAllActivePolicy(inputRecord);

        if(rs.getSize() == 0){
            EntitlementFields.setReadOnly(rs.getSummaryRecord(), true);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllActivePolicy", rs);
        }

        return rs;
    }

    /**
     * load current coverage
     *
     * @param policyHeader policy header
     * @return result record
     */
    public RecordSet getCurrentCoverage(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCurrentCoverage", new Object[]{policyHeader});
        }

        RecordSet rs = new RecordSet();
        Record resultRec = new Record();
        String riskNameWithRiskType = policyHeader.getRiskHeader().getRiskName();
        // get current coverages for selection
        CoverageFields.setCoverageId(resultRec, policyHeader.getCoverageHeader().getCoverageId());
        CoverageFields.setProductCoverageDesc(resultRec, policyHeader.getCoverageHeader().getCoverageName());
        RiskFields.setRiskName(resultRec, riskNameWithRiskType.substring(0, riskNameWithRiskType.indexOf("(") - 1));
        RiskFields.setRiskTypeCode(resultRec, policyHeader.getRiskHeader().getRiskTypeCode());
        PolicyFields.setPolicyTypeCode(resultRec, policyHeader.getPolicyTypeCode());
        resultRec.setFieldValue("CUR_SELECT_IND", new Field(new Long(0)));

        rs.addRecord(resultRec);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCurrentCoverage", rs);
        }

        return rs;
    }

    public UnderlyingCoverageDAO getUnderlyingCoverageDAO() {
        return m_underlyingCoverageDAO;
    }

    public void setUnderlyingCoverageDAO(UnderlyingCoverageDAO underlyingCoverageDAO) {
        m_underlyingCoverageDAO = underlyingCoverageDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    protected static final String OFFICIAL = "OFFICIAL";

    private UnderlyingCoverageDAO m_underlyingCoverageDAO;
    private WorkbenchConfiguration m_workbenchConfiguration;


}
