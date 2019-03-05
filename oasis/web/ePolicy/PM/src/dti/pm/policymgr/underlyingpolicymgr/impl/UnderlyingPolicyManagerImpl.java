package dti.pm.policymgr.underlyingpolicymgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
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
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.underlyingpolicymgr.UnderlyingPolicyFields;
import dti.pm.policymgr.underlyingpolicymgr.UnderlyingPolicyManager;
import dti.pm.policymgr.underlyingpolicymgr.dao.UnderlyingPolicyDAO;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.policymgr.underlyingpolicymgr.struts.MaintainUnderlyingPolicyAction;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for the underlying policy manager.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 3, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/17/2011       dzhang      121244 - Mofified getInitialValuesForUnderlyingPolicy.
 *  01/02/2014      jyang       148771 - Modified setInitialEntitlementValues process in
 *                                       getInitialValuesForUnderlyingPolicy function.
 * 03/20/2014       awu         153139 - Modified getRetroDateForReset to avoid NULL POINT Exception.
 * 11/27/2015       tzeng       165794 - 1)Modified loadAllUnderlyingPolicy to filter official when endquote and add
 *                                         original date and data fields.
 *                                       2)Modified getInitialValuesForUnderlyingPolicy to add default value.
 *                                       3)Modified saveAllUnderlyingPolicy to transfer transaction log with all records.
 *                                       4)Modified validateSaveAllUnderlyingPolicy to add overlap logic and add
 *                                       validateForEditFields to make sure that data and date field cannot be changed
 *                                       at the same time.
 * 03/09/2016       wdang       170029 - Remove overlap validation as per user request.
 * 04/14/2016       wdang       170793 - Added two more validations in validateSaveAllUnderlyingPolicy.
 * 08/05/2016       wdang       178134 - Modify validateForEditFields to check changedFromOfficialToTemp more accurately.
 * 08/25/2016       ssheng      178365 - Modify the validateSaveAllUnderlyingPolicy and
 *                                       add the validateUnderlyingOverlap.
 * 09/09/2016       xnie        178813 - 1) Added validateSameOffVersionExists() to check if any underlying policy
 *                                          version which is from same official record and in same time period exists.
 *                                       2) Modified validateSaveAllUnderlyingPolicy() to call validateSameOffVersionExists().
 * 01/25/2017       ssheng      182697 - Modified validateSaveAllUnderlyingPolicy to exclude expired underlying records.
 * 03/20/2017       wli         183962 - Modified validateSaveAllUnderlyingPolicy to remove the logic to filter flat
 *                                       records.
 * 05/29/2017       xnie        185461 - Modified getInitialValuesForUnderlyingPolicy() to remove the default value of
 *                                       output_b. The default value will be handlled by cust webWB.
 * ---------------------------------------------------
 */
public class UnderlyingPolicyManagerImpl implements UnderlyingPolicyManager {

    private static final String ORIGINAL_FIELD_PREFIX = "orig";

    private static final String[] underlyingDateFields = new String[] {
        UnderlyingPolicyFields.EFFECTIVE_FROM_DATE,
        UnderlyingPolicyFields.EFFECTIVE_TO_DATE,
        UnderlyingPolicyFields.RENEW_B
    };

    private static final String[] underlyingDataFields  = new String[] {
        UnderlyingPolicyFields.POLICY_UNDER_POL_NO,
        UnderlyingPolicyFields.POLICY_UNDER_POL_ID,
        UnderlyingPolicyFields.UNDER_ISS_COMP_ENT_ID,
        UnderlyingPolicyFields.UNDER_POLICY_TYPE_CODE,
        UnderlyingPolicyFields.COV_PART_COVERAGE_CODE,
        UnderlyingPolicyFields.POLICY_FORM_CODE,
        UnderlyingPolicyFields.COVERAGE_LIMIT_CODE,
        UnderlyingPolicyFields.RETROACTIVE_DATE,
        UnderlyingPolicyFields.OUTPUT_B,
        UnderlyingPolicyFields.COMPANY_INSURED_B,
        UnderlyingPolicyFields.GROUP_NO,
        UnderlyingPolicyFields.LIMIT_VALUE1,
        UnderlyingPolicyFields.LIMIT_VALUE1_CODE,
        UnderlyingPolicyFields.LIMIT_VALUE2,
        UnderlyingPolicyFields.LIMIT_VALUE2_CODE,
        UnderlyingPolicyFields.LIMIT_VALUE3,
        UnderlyingPolicyFields.LIMIT_VALUE3_CODE,
        UnderlyingPolicyFields.SUB_GROUP_CODE
    };


    /**
     * load all underlying policies
     *
     * @param policyHeader policy header
     * @return result recordset
     */
    public RecordSet loadAllUnderlyingPolicy(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllUnderlyingPolicy", new Object[]{policyHeader});
        }
        // set input record
        Record inputRecord = policyHeader.toRecord();
        inputRecord.setFieldValue(UnderlyingPolicyFields.RECORD_MODE_CODE, policyHeader.getRecordMode().getName());
        // record load processor
        RecordLoadProcessor lp = new UnderlyingPolicyEntitlementRecordLoadProcessor(policyHeader);
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(lp,
            new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, UnderlyingPolicyFields.POLICY_UNDERLYING_INFO_ID));
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(lp,
            new AddOrigFieldsRecordLoadProcessor((String[])ArrayUtils.addAll((String[])underlyingDateFields, (String[])underlyingDataFields)));
        RecordSet rs = getUnderlyingPolicyDAO().loadAllUnderlyingPolicy(inputRecord, lp);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllUnderlyingPolicy", rs);
        }

        return rs;
    }


    /**
     * get retro date for reset
     *
     * @param policyHeader policy header
     * @param inputRecord  contains all required infos
     * @return retor date
     */
    public String getRetroDateForReset(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRetroDateForReset", new Object[]{policyHeader, inputRecord});
        }
        //if eff/exp date is null, use term eff/exp date instead
        if (!inputRecord.hasStringValue(UnderlyingPolicyFields.EFFECTIVE_FROM_DATE)) {
            UnderlyingPolicyFields.setEffectiveFromDate(inputRecord, policyHeader.getTermEffectiveFromDate());
        }
        if (!inputRecord.hasStringValue(UnderlyingPolicyFields.EFFECTIVE_TO_DATE)) {
            UnderlyingPolicyFields.setEffectiveToDate(inputRecord, policyHeader.getTermEffectiveToDate());
        }
        inputRecord.setFields(policyHeader.toRecord(), false);
        //get retro dates
        RecordSet retroRs = getUnderlyingPolicyDAO().loadAllRetroDate(inputRecord);
        String retroDate = "00/00/0000";
        Iterator retroRsIter = retroRs.getRecords();
        //If the query returns a value that is not '0/00/0000',
        // set the Retro Date to it; otherwise set the Retro Date to Effective Date of current record
        while (retroRsIter.hasNext()) {
            Record retroRec = (Record) retroRsIter.next();
            String retro = retroRec.getStringValue("retroDate");
            if (StringUtils.isBlank(retro)) {
                retro = "";
            }
            if (!("00/00/0000").equals(retro)) {
                retroDate = retro;
            }
        }
        if (("00/00/0000").equals(retroDate)) {
            retroDate = UnderlyingPolicyFields.getEffectiveFromDate(inputRecord);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRetroDateForReset", retroDate);
        }
        return retroDate;
    }


    /**
     * get initial values for underlying policy add
     *
     * @param policyHeader policy header
     * @param inputRecord  contains parameters for adding new record
     * @return record contains all returned default values
     */
    public Record getInitialValuesForUnderlyingPolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForUnderlyingPolicy", new Object[]{policyHeader});
        }
        Record resultRec = new Record();
        PolicyFields.setPolicyId(resultRec, policyHeader.getPolicyId());
        TransactionFields.setTransactionLogId(resultRec, policyHeader.getLastTransactionId());
        String addOperation = inputRecord.getStringValue("addOperation");
        Record configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(MaintainUnderlyingPolicyAction.class.getName());
        if (configuredDefaultValues != null) {
            resultRec.setFields(configuredDefaultValues);
        }
        if (addOperation.equals("ADD")) {

            //set default values
            UnderlyingPolicyFields.setPolicyUnderPolId(resultRec, "0");
            UnderlyingPolicyFields.setCompanyInsuredB(resultRec, YesNoFlag.N);
            UnderlyingPolicyFields.setPolicyNoReadOnlyB(resultRec, YesNoFlag.N);
            UnderlyingPolicyFields.setRetroactiveDate(resultRec, policyHeader.getTermEffectiveFromDate());
            UnderlyingPolicyFields.setEffectiveFromDate(resultRec, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
            UnderlyingPolicyFields.setEffectiveToDate(resultRec, policyHeader.getTermEffectiveToDate());

            if (inputRecord.hasStringValue(UnderlyingPolicyFields.POLICY_UNDER_POL_ID)) {
                inputRecord.setFields(policyHeader.toRecord(), false);
                Record initialVlauesRec = getUnderlyingPolicyDAO().getInitialValuesForUnderlyingPolicy(inputRecord);
                //If o_und_pol_form_cd is not NULL, override these columns of newly inserted record by the return values
                if (initialVlauesRec.hasStringValue("undPolFormCd")) {
                    UnderlyingPolicyFields.setPolicyUnderPolId(resultRec, UnderlyingPolicyFields.getPolicyUnderPolId(inputRecord));
                    UnderlyingPolicyFields.setPolicyUnderPolNo(resultRec, initialVlauesRec.getStringValue("undPolicyNo"));
                    UnderlyingPolicyFields.setUnderPolicyTypeCode(resultRec, initialVlauesRec.getStringValue("undPolTypeCd"));
                    UnderlyingPolicyFields.setUnderIssCompEntId(resultRec, initialVlauesRec.getStringValue("undIssCompEntId"));
                    UnderlyingPolicyFields.setEffectiveFromDate(resultRec, initialVlauesRec.getStringValue("undTermEff"));
                    UnderlyingPolicyFields.setEffectiveToDate(resultRec, initialVlauesRec.getStringValue("undTermExp"));
                    UnderlyingPolicyFields.setCovPartCoverageCode(resultRec, initialVlauesRec.getStringValue("undProdCovgCd"));
                    UnderlyingPolicyFields.setPolicyFormCode(resultRec, initialVlauesRec.getStringValue("undPolFormCd"));
                    UnderlyingPolicyFields.setCoverageLimitCode(resultRec, initialVlauesRec.getStringValue("undCovgLimitCd"));
                    UnderlyingPolicyFields.setRetroactiveDate(resultRec, initialVlauesRec.getStringValue("undRetroDate"));
                    UnderlyingPolicyFields.setCompanyInsuredB(resultRec, YesNoFlag.Y);
                    UnderlyingPolicyFields.setPolicyNoReadOnlyB(resultRec, YesNoFlag.Y);
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
        //Set initial row st
        UnderlyingPolicyEntitlementRecordLoadProcessor.setInitialEntitlementValuesForUnderlyingPolicy(policyHeader, resultRec);
        return resultRec;
    }

    /**
     * save all underlying policy data
     *
     * @param policyHeader policy header
     * @param inputRecords input recordset
     */
    public void saveAllUnderlyingPolicy(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllUnderlyingPolicy",
                new Object[]{policyHeader, inputRecords});
        }
        // set transaction log Id from policy header
        inputRecords.setFieldValueOnAll(UnderlyingPolicyFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        PMRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);
        RecordSet deletedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        //validate underlying policy data
        validateSaveAllUnderlyingPolicy(policyHeader, inputRecords);

        int processCount;
        //delete records
        processCount = getUnderlyingPolicyDAO().deleteAllUnderlyingPolicy(deletedRecords);
        //insert and update records
        processCount += getUnderlyingPolicyDAO().saveAllUnderlyingPolicy(changedRecords);

        //validate all term underlying overlap data
        validateUnderlyingOverlap(policyHeader);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllUnderlyingPolicy", String.valueOf(processCount));
        }
    }

    /**
     * validate save all underlying policy
     *
     * @param policyHeader policy header
     * @param inputRecords contains all inserted/modified records
     */
    protected void validateSaveAllUnderlyingPolicy(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSaveAllUnderlyingPolicy", new Object[]{inputRecords});
        }

        // Set the displayRecordNumber to all visible records.
        PMRecordSetHelper.setDisplayRecordNumberOnRecords(inputRecords);
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        Iterator recIter = changedRecords.getRecords();
        while (recIter.hasNext()) {
            Record record = (Record) recIter.next();
            String rowNum = String.valueOf(record.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));
            String rowId = UnderlyingPolicyFields.getPolicyUnderlyingInfoId(record);

            if (!record.hasStringValue(UnderlyingPolicyFields.POLICY_UNDER_POL_NO)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingPolicy.policyNo.required.error",
                    UnderlyingPolicyFields.POLICY_UNDER_POL_NO, rowId);
            }
            if (!record.hasStringValue(UnderlyingPolicyFields.EFFECTIVE_FROM_DATE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingPolicy.effDate.required.error",
                    UnderlyingPolicyFields.EFFECTIVE_FROM_DATE, rowId);
            }
            if (!record.hasStringValue(UnderlyingPolicyFields.EFFECTIVE_TO_DATE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingPolicy.expDate.required.error",
                    UnderlyingPolicyFields.EFFECTIVE_TO_DATE, rowId);
            }
            if (record.hasStringValue(UnderlyingPolicyFields.EFFECTIVE_FROM_DATE) &&
                record.hasStringValue(UnderlyingPolicyFields.EFFECTIVE_TO_DATE)) {
                String effDate = record.getStringValue(UnderlyingPolicyFields.EFFECTIVE_FROM_DATE);
                String expDate = record.getStringValue(UnderlyingPolicyFields.EFFECTIVE_TO_DATE);

                try {
                    if (DateUtils.daysDiff(effDate, expDate) < 0) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingPolicy.expLessTanEff.error",
                            UnderlyingPolicyFields.EFFECTIVE_TO_DATE, rowId);
                    }
                    if (YesNoFlag.Y.getName().equals(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_UNDERLYING_TERM, YesNoFlag.N.getName()))) {
                        if ((DateUtils.daysDiff(expDate, policyHeader.getTermEffectiveFromDate()) >= 0 &&
                                DateUtils.daysDiff(effDate, policyHeader.getTermEffectiveFromDate()) > 0)||
                            (DateUtils.daysDiff(effDate, policyHeader.getTermEffectiveToDate()) <= 0) &&
                                DateUtils.daysDiff(expDate, policyHeader.getTermEffectiveToDate()) < 0) {
                            MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingPolicy.invalidDate.error",
                                UnderlyingPolicyFields.EFFECTIVE_FROM_DATE, rowId);
                        }
                    }
                }
                catch (ParseException e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the date.", e);
                    l.throwing(DateUtils.class.getName(), "validateSaveAllUnderlyingPolicy", ae);
                    throw ae;
                }
            }
            if (record.hasStringValue(UnderlyingPolicyFields.POLICY_FORM_CODE) &&
                "CM".equals(UnderlyingPolicyFields.getPolicyFormCode(record))) {
                if (!record.hasStringValue(UnderlyingPolicyFields.RETROACTIVE_DATE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingPolicy.retroDate.required.error",
                        UnderlyingPolicyFields.RETROACTIVE_DATE, rowId);
                }
                else if (record.hasStringValue(UnderlyingPolicyFields.EFFECTIVE_FROM_DATE)) {
                    Date retroDate = record.getDateValue(UnderlyingPolicyFields.RETROACTIVE_DATE);
                    Date effDate = record.getDateValue(UnderlyingPolicyFields.EFFECTIVE_FROM_DATE);
                    if (retroDate.after(effDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingPolicy.retroGreaterEff.error",
                            UnderlyingPolicyFields.RETROACTIVE_DATE, rowId);
                    }
                }
            }

            validateForEditFields(record, inputRecords);

            // Check if any underlying policy version which is from same official record and in same time period exists
            // when current record's official record fk is null and effective to date is changed.
            if (!StringUtils.isBlank(UnderlyingPolicyFields.getOfficialRecordId(record)) &&
                !StringUtils.isSame(UnderlyingPolicyFields.getEffectiveToDate(record), UnderlyingPolicyFields.getOrigEffectiveToDate(record))) {
                if (validateSameOffVersionExists(record).equals("Y")) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainunderlyingPolicy.sameOffVersionExists.error");
                }
            }
        }

        // Overlap validation in front end.
        if (YesNoFlag.Y.getName().equals(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_UNDERLYING_TERM, YesNoFlag.N.getName()))) {
            String[] validationFieldNames = new String[]{UnderlyingPolicyFields.POLICY_UNDER_POL_NO, UnderlyingPolicyFields.COV_PART_COVERAGE_CODE};
            ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
                UnderlyingPolicyFields.EFFECTIVE_FROM_DATE,
                UnderlyingPolicyFields.EFFECTIVE_TO_DATE,
                UnderlyingPolicyFields.POLICY_UNDERLYING_INFO_ID,
                "pm.maintainunderlyingPolicy.currentTermOverlapDates.error",
                validationFieldNames, validationFieldNames);

            continuityValidator.validate(inputRecords);
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("underlying policy data invalid");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSaveAllUnderlyingPolicy");
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

        if (YesNoFlag.Y.getName().equals(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_UNDERLYING_TERM, YesNoFlag.N.getName()))) {
            Record record = new Record();
            record.setFieldValue(PolicyHeaderFields.POLICY_ID, policyHeader.getPolicyId());
            String statusCode = getUnderlyingPolicyDAO().validateUnderlyingOverlap(record);
            if (!StringUtils.isBlank(statusCode)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainunderlyingPolicy.allTermOverlapDates.error");
            }
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateUnderlyingOverlap");
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
        String rowId = UnderlyingPolicyFields.getPolicyUnderlyingInfoId(record);
        //System prevent to change other attributes when revise date fields or vice versa.
        boolean official = PMCommonFields.hasRecordModeCode(record) ? PMCommonFields.getRecordModeCode(record).isOfficial() : false;
        boolean changedFromOfficialToTemp = false;
        String officialRecordId = null;
        if (record.hasStringValue(UnderlyingPolicyFields.OFFICIAL_RECORD_ID)) {
            officialRecordId = UnderlyingPolicyFields.getOfficialRecordId(record);
            if (Long.parseLong(officialRecordId) > 0 && !PMCommonFields.getRecordModeCode(record).isOfficial()) {
                for (int i = 0; i < recordset.getSize(); i++) {
                    Record otherRecord = recordset.getRecord(i);
                    // find the official record
                    if (TransactionFields.getTransactionLogId(record).equals(
                        UnderlyingPolicyFields.getClosingTransLogId(otherRecord)) &&
                        officialRecordId.equals(UnderlyingPolicyFields.getPolicyUnderlyingInfoId(otherRecord))) {
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
                    if (StringUtils.isSame(UnderlyingPolicyFields.getPolicyUnderlyingInfoId(otherRecord), officialRecordId)) {
                        origRecord = otherRecord;
                        break;
                    }
                }
                for (int i = 0; i < recordset.getSize(); i++) {
                    Record otherRecord = recordset.getRecord(i);
                    // if the other TEMP record with the same official record id exists, prevent user changing dates.
                    if (StringUtils.isSame(UnderlyingPolicyFields.getOfficialRecordId(otherRecord), officialRecordId)
                        && PMCommonFields.getRecordModeCode(otherRecord).isTemp()
                        && !StringUtils.isSame(UnderlyingPolicyFields.getPolicyUnderlyingInfoId(otherRecord), UnderlyingPolicyFields.getPolicyUnderlyingInfoId(record))) {
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
                MessageManager.getInstance().addErrorMessage("pm.maintainunderlyingPolicy.conflictEdit.error",  new String[]{rowNum}, "", rowId);
            }
        }
    }

    /**
     * Check if any underlying policy version which is from same official record and in same time period exists.
     *
     * @param inputRecord
     */
    protected String validateSameOffVersionExists(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSameOffVersionExists", new Object[]{inputRecord});
        }
        String sameOffVersionExists = getUnderlyingPolicyDAO().validateSameOffVersionExists(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSameOffVersionExists");
        }
        return sameOffVersionExists;
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
        RecordSet rs = getUnderlyingPolicyDAO().loadAllActivePolicy(inputRecord);

        if(rs.getSize() == 0){
            EntitlementFields.setReadOnly(rs.getSummaryRecord(), true);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllActivePolicy", rs);
        }

        return rs;
    }

    public UnderlyingPolicyDAO getUnderlyingPolicyDAO() {
        return m_underlyingPolicyDAO;
    }

    public void setUnderlyingPolicyDAO(UnderlyingPolicyDAO underlyingPolicyDAO) {
        m_underlyingPolicyDAO = underlyingPolicyDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private UnderlyingPolicyDAO m_underlyingPolicyDAO;
    private WorkbenchConfiguration m_workbenchConfiguration;


}
