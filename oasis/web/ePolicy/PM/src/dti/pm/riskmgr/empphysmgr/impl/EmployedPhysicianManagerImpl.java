package dti.pm.riskmgr.empphysmgr.impl;

import dti.pm.riskmgr.empphysmgr.EmployedPhysicianManager;
import dti.pm.riskmgr.empphysmgr.EmployedPhysicianFields;
import dti.pm.riskmgr.empphysmgr.EmploymentStatusCode;
import dti.pm.riskmgr.empphysmgr.dao.EmployedPhysicianDAO;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.entitymgr.EntityManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.util.LogUtils;

import dti.oasis.util.SysParmProvider;
import dti.oasis.util.StringUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;
import dti.oasis.app.ConfigurationException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.math.BigDecimal;


/**
 * This class provides the implementation details for EmployedPhysicianManager.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/30/2010       syang       106758 - Modified getInitialValuesForEmployedPhysician to retrieve the default values.
 * 07/23/2010       syang       102365 - Modified validateEmployedPhysician to validate new fields if system parameter
 *                              PM_FTE_BY_ENTITY is 'Y' and modified getInitialValuesForEmployedPhysician to defalut
 *                              FTE Status to "ACTIVE".
 * 08/02/2010       syang       102365 - Modified validateEmployedPhysician to skip the LPED validation if FTE Status is "ACTIVE".
 * 09/24/2010       syang       Issue 111898 - Modified validateEmployedPhysician() to exclude the validation for
 *                              calculating fte value(when page fields are changed).
 * 02/25/2011       sxm         111017 - Change risk_pk to risk_base_reocrd_fk
 * 07/06/2011       wqfu        121424 - Modified getInitialValuesForEmployedPhysician to populate risk effective to
 *                              date as correct exp date instead of term effective to date.
 * 07/20/2011       wqfu        121424 - Modified validateEmployedPhysician to handle short term fte risk
 * 08/30/2011       ryzhao      124458 - Modified validateEmployedPhysician to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 10/28/2011       xnie        126107 - Used riskBaseRecordId in risk header instead of riskBaseRecordId in
 *                                       inputRecord as riskChildId.
 * 01/04/2012       wfu         127802 - Modified getInitialValuesForEmployedPhysician to reset entity id and name.
 * 04/16/2012       gxc         131759 - Modified getInitialValuesForEmployedPhysician to set effective to date to
 *                              term expiration date only if risk expiration is greater than term expiration date
 * 10/23/2012       xnie        137735 - Modified validateEmployedPhysician to add check if fteStatusCode is visible.
 * 10/27/2014       kxiang      158657 - Removed codes about Location2, as it's obsolete.
 * 03/16/2016       wdang       161448 - Modified getInitialValuesForEmployedPhysician() to handle location field
 *                                       for LOCATION risk type.
 * 08/20/2015       ssheng      165340 - Use Common Function PolicyHeader.getRecordMode to replace calcuate recordModeCode
 *                                       via screenMode.
 * ---------------------------------------------------
 */
public class EmployedPhysicianManagerImpl implements EmployedPhysicianManager {

    /**
     * load recordset of all Employed Physician infos
     *
     * @param policyHeader
     * @param inputRecord
     * @return recordset of all Employed Physician infos
     */
    public RecordSet loadAllEmployedPhysician(PolicyHeader policyHeader, Record inputRecord) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEmployedPhysician", new Object[]{policyHeader, inputRecord});
        }

        //set recordModeCode
        PMCommonFields.setRecordModeCode(inputRecord, getEmpPhysRecordMode(policyHeader));
        //set rsik child id
        EmployedPhysicianFields.setRiskChildId(inputRecord, policyHeader.getRiskHeader().getRiskBaseRecordId());
        
        //set coverage base record id
        if (!inputRecord.hasStringValue(RiskFields.COVERAGE_PART_BASE_RECORD_ID)) {
            RiskFields.setCoveragePartBaseRecordId(inputRecord, "0");
        }
        //set add FTE by entity indicator
        YesNoFlag addFteByEntity = YesNoFlag.getInstance(SysParmProvider.getInstance().
            getSysParm(SysParmIds.PM_FTE_BY_ENTITY, "N"));
        EmployedPhysicianFields.setAddFteByEntity(inputRecord, addFteByEntity);
        //set fteValExpDate indicator
        YesNoFlag fteValExpDate = YesNoFlag.getInstance(SysParmProvider.getInstance().
            getSysParm(SysParmIds.PM_FTE_VAL_EXPDATE, "Y"));
        EmployedPhysicianFields.setFteValExpdate(inputRecord, fteValExpDate);

        EmployedPhysicianRecordLoadProcessor empPhysRLP =
            new EmployedPhysicianRecordLoadProcessor(policyHeader, this, inputRecord);
        EmployedPhysicianEntitlementRecordLoadProcessor empPhysEntitlementRLP =
            new EmployedPhysicianEntitlementRecordLoadProcessor(policyHeader, inputRecord);
        RecordLoadProcessor processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            empPhysRLP, empPhysEntitlementRLP);
        processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            processor, origFieldLoadProcessor);

        inputRecord.setFields(policyHeader.toRecord(), false);
        //load all employed physicians
        RecordSet empPhysRs = getEmployedPhysicianDAO().loadAllEmployedPhysician(inputRecord, processor);

        Record summaryRecord = empPhysRs.getSummaryRecord();

        EmployedPhysicianFields.setHospTermEffective(summaryRecord,policyHeader.getTermEffectiveFromDate());
        EmployedPhysicianFields.setHospTermExpiration(summaryRecord,policyHeader.getTermEffectiveToDate());
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEmployedPhysician", empPhysRs);
        }

        return empPhysRs;

    }

    /**
     * get recordmode from the current view mode
     *
     * @param policyHeader
     * @return recordmode code
     */
    protected RecordMode getEmpPhysRecordMode(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEmpPhysRecordMode", new Object[]{policyHeader});
        }
        RecordMode recordMode = policyHeader.getRecordMode();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEmpPhysRecordMode", recordMode);
        }

        return recordMode;
    }

    /**
     * validate all employed physician data
     *
     * @param policyHeader
     * @param inputRecords
     * @param inputRecord
     */
    protected void validateAllEmployedPhysician(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllEmployedPhysician", new Object[]{policyHeader, inputRecords});
        }

        //get validate recordset(visible and inserted or updated) from input records
        RecordSet valRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}))
            .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));

        YesNoFlag fteValExpDate = YesNoFlag.getInstance(SysParmProvider.getInstance().
            getSysParm(SysParmIds.PM_FTE_VAL_EXPDATE, "Y"));
        boolean isFteByEntity =
            YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_FTE_BY_ENTITY, "N")).booleanValue();
        Iterator it = valRecords.getRecords();
        while (it.hasNext()) {
            Record empPhyRec = (Record) it.next();
            //validate each employed physician record
            validateEmployedPhysician(policyHeader, empPhyRec, inputRecord);
        }

        //validate overlapping
        String[] keyFieldNames;
        String messageKey = "pm.maintainEmployedPhysician.timeOverlap.error";
        if (isFteByEntity) {
            keyFieldNames = new String[]{EmployedPhysicianFields.ENTITY_ID};
        }
        else {
            keyFieldNames = new String[]{EmployedPhysicianFields.RISK_PARENT_ID};
        }
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
            EmployedPhysicianFields.EFFECTIVE_FROM_DATE, EmployedPhysicianFields.EFFECTIVE_TO_DATE,
            EmployedPhysicianFields.POLICY_FTE_RELATION_ID,
            messageKey, keyFieldNames, new String[0]);
        continuityValidator.validate(valRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllEmployedPhysician",
                String.valueOf(MessageManager.getInstance().hasErrorMessages()));
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Employed Physician data.");
        }
    }

    /**
     * validate one employed physician record
     *
     * @param policyHeader
     * @param inputRecord
     */
    protected void validateEmployedPhysician(PolicyHeader policyHeader, Record empPhysRec, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateEmployedPhysician", new Object[]{policyHeader, empPhysRec, inputRecord});
        }

        //create a new record contains all infos for validating
        Record valEmpPhysRec = new Record();
        valEmpPhysRec.setFields(inputRecord);
        valEmpPhysRec.setFields(empPhysRec, true);
        valEmpPhysRec.setUpdateIndicator(empPhysRec.getUpdateIndicator());
        valEmpPhysRec.setDisplayIndicator(empPhysRec.getDisplayIndicator());

        //validate employed physician data, validate exception will be throwed out if there is validation error
        validateEmployedPhysician(policyHeader, valEmpPhysRec);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateEmployedPhysician");
        }

    }

    /**
     * validate one employed physician record
     *
     * @param policyHeader
     * @param inputRecord  contains all infos for validating
     */
    protected void validateEmployedPhysician(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateEmployedPhysician", new Object[]{policyHeader, inputRecord});
        }
        YesNoFlag fteValExpDate = YesNoFlag.getInstance(SysParmProvider.getInstance().
            getSysParm(SysParmIds.PM_FTE_VAL_EXPDATE, "Y"));
        boolean isFteByEntity =
            YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_FTE_BY_ENTITY, "N")).booleanValue();

        YesNoFlag fteStatusCodeVisible = YesNoFlag.N;
        if (inputRecord.hasStringValue(EmployedPhysicianFields.FTE_STATUS_CODE_VISIBLE)) {
            fteStatusCodeVisible = YesNoFlag.getInstance(EmployedPhysicianFields.getFteStatusCodeVisible(inputRecord).booleanValue());
        }

        String rowNum = String.valueOf(inputRecord.getRecordNumber() + 1);
        String rowId = EmployedPhysicianFields.getPolicyFteRelationId(inputRecord);
        EmploymentStatusCode empStus = EmployedPhysicianFields.getEmploymentStatus(inputRecord);

        if (inputRecord.hasStringValue(EmployedPhysicianFields.ACTUAL_HOURS) &&
            !inputRecord.hasStringValue(RiskFields.FTE_EQUIVALENT)) {
            MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.fullTimeEquivalent.required.error",
                new String[]{rowNum}, EmployedPhysicianFields.ACTUAL_HOURS, rowId);
        }

        if (empStus.isFulltime() && !inputRecord.hasStringValue(RiskFields.FTE_FULL_TIME_HRS)) {
            MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.fullTimeHours.required.error",
                new String[]{rowNum}, EmployedPhysicianFields.EMPLOYMENT_STATUS, rowId);
        }

        if (empStus.isParttime() && !inputRecord.hasStringValue(RiskFields.FTE_PART_TIME_HRS)) {
            MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.partTimeHours.required.error",
                new String[]{rowNum}, EmployedPhysicianFields.EMPLOYMENT_STATUS, rowId);
        }

        if (empStus.isPrediem() && !inputRecord.hasStringValue(RiskFields.FTE_PER_DIEM_HRS)) {
            MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.perDiemHourst.required.error",
                new String[]{rowNum}, EmployedPhysicianFields.EMPLOYMENT_STATUS, rowId);
        }

        if (isFteByEntity && (!inputRecord.hasStringValue(EmployedPhysicianFields.ENTITY_ID)
            || EmployedPhysicianFields.getEntityId(inputRecord).equals("0"))) {
            MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.entity.error",
                new String[]{rowNum}, EmployedPhysicianFields.EMPLOYMENT_STATUS, rowId);
        }

        //validate end date
        if (fteValExpDate.booleanValue()) {
            // Added to handle FTE short term risk
            boolean iValShortRisk = true;
            RiskHeader riskHeader = policyHeader.getRiskHeader();
            if(riskHeader != null && riskHeader.getDateChangeAllowedB().booleanValue() &&
               EmployedPhysicianFields.getEffectiveToDate(inputRecord).equals(riskHeader.getRiskEffectiveToDate())){
               iValShortRisk = false;
            }
            if ((inputRecord.getUpdateIndicator().equals(UpdateIndicator.INSERTED)
                && !EmployedPhysicianFields.getEffectiveToDate(inputRecord).equals(policyHeader.getTermEffectiveToDate())
                && iValShortRisk)
                || (inputRecord.getUpdateIndicator().equals(UpdateIndicator.UPDATED)
                && !EmployedPhysicianFields.getOrigEffectiveToDate(inputRecord).equals(
                EmployedPhysicianFields.getEffectiveToDate(inputRecord))
                && !EmployedPhysicianFields.getEffectiveToDate(inputRecord).equals(
                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate()))) {

                MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.endDate.error",
                    new String[]{rowNum}, EmployedPhysicianFields.EFFECTIVE_TO_DATE, rowId);
            }
        }

        // Issue 102365, add validation for newly added fields if system parameter PM_FTE_BY_ENTITY is 'Y' .
        // Issue 111898, exclude the validation for calculating fte value(when page fields are changed). 
        if ((isFteByEntity && fteStatusCodeVisible.booleanValue())
            && !inputRecord.getBooleanValue("isToValidate", false).booleanValue()) {
            if (!inputRecord.hasStringValue(EmployedPhysicianFields.FTE_STATUS_CODE) ||
                StringUtils.isBlank(EmployedPhysicianFields.getFteStatusCode(inputRecord))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.fteStatus.required.error",
                    new String[]{rowNum}, EmployedPhysicianFields.FTE_STATUS_CODE, rowId);
            }
            else {
                String fteStatusCode = EmployedPhysicianFields.getFteStatusCode(inputRecord);
                // For INACTIVE, the FTE Effective To Date is required and RenewB should be "No".
                if ("INACTIVE".equalsIgnoreCase(fteStatusCode)) {
                    if (!inputRecord.hasStringValue(EmployedPhysicianFields.FTE_EFFECTIVE_TO_DATE)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.fteEffToDate.required.error",
                            new String[]{rowNum}, EmployedPhysicianFields.FTE_EFFECTIVE_TO_DATE, rowId);
                    }
                    else {
                        String termEffFromStr = policyHeader.getTermEffectiveFromDate();
                        String termEffToStr = policyHeader.getTermEffectiveToDate();
                        Date fteEffToDate = inputRecord.getDateValue(EmployedPhysicianFields.FTE_EFFECTIVE_TO_DATE);
                        Date termEffFromDate = DateUtils.parseDate(termEffFromStr);
                        Date termEffToDate = DateUtils.parseDate(termEffToStr);
                        if (fteEffToDate.before(termEffFromDate) || fteEffToDate.after(termEffToDate)) {
                            MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.LPED.error",
                                new String[]{rowNum,
                                    FormatUtils.formatDateForDisplay(termEffFromStr),
                                    FormatUtils.formatDateForDisplay(termEffToStr)},
                                EmployedPhysicianFields.FTE_EFFECTIVE_TO_DATE, rowId);
                        }
                    }
                    if (!inputRecord.hasStringValue(EmployedPhysicianFields.RENEW_B)
                        || EmployedPhysicianFields.getRenewB(inputRecord).booleanValue()) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainEmployedPhysician.fteStatus.error",
                            new String[]{rowNum}, EmployedPhysicianFields.RENEW_B, rowId);
                    }
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateEmployedPhysician",
                String.valueOf(MessageManager.getInstance().hasErrorMessages()));
        }

        // throw validation exception if data is invalid and not continue validating
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Employed Physician data.");
        }
    }

    /**
     * To get initial values for a newly inserted Employed Physician record
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record with initial vlaues
     */
    public Record getInitialValuesForEmployedPhysician(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForEmployedPhysician", new Object[]{policyHeader, inputRecord});
        }

        boolean isFteByEntity =
            YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_FTE_BY_ENTITY, "N")).booleanValue();

        //get default record from entitlement load processor
        Record output = new Record();
        // Firstly, merge default value from web work bench configuration
        Record configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_EMPOLYED_PHYSICIAN_ACTION_CLASS_NAME);
        if (configuredDefaultValues != null) {
            output.setFields(configuredDefaultValues);
        }
        output.setFields(EmployedPhysicianEntitlementRecordLoadProcessor.getInitialEntitlementValuesForEmployedPhysician());

        // For issue 101311, the effective date should default to the later of risk effective and transaction effective.
        Transaction lastTransaction = policyHeader.getLastTransactionInfo();
        String effectiveFromDate = lastTransaction.getTransEffectiveFromDate();
        String effectiveToDate = policyHeader.getTermEffectiveToDate();
        RiskHeader riskHeader = policyHeader.getRiskHeader();
        if(riskHeader != null){
            String riskEffectiveFromDate = riskHeader.getEarliestContigEffectiveDate();
            effectiveToDate = riskHeader.getRiskEffectiveToDate();
            if (DateUtils.parseDate(effectiveToDate).after(DateUtils.parseDate(policyHeader.getTermEffectiveToDate()))) {
                effectiveToDate = policyHeader.getTermEffectiveToDate();
            }
            Date riskEffFromDate = DateUtils.parseDate(riskEffectiveFromDate);
            Date transEffFromDate = DateUtils.parseDate(effectiveFromDate);
            if(transEffFromDate.before(riskEffFromDate)){
               effectiveFromDate = riskEffectiveFromDate;
            }
        }
        EmployedPhysicianFields.setEffectiveFromDate(output, effectiveFromDate);
        EmployedPhysicianFields.setEffectiveToDate(output, effectiveToDate);
        TransactionFields.setTransactionLogId(output, lastTransaction.getTransactionLogId());
        EmployedPhysicianFields.setRiskBaseRecordId(output, null);
        PMCommonFields.setRecordModeCode(output, RecordMode.TEMP);
        EmployedPhysicianFields.setAfterImageRecordB(output, YesNoFlag.Y);
        EmployedPhysicianFields.setCurrStatusCode(output, PMStatusCode.PENDING);
        EmployedPhysicianFields.setEmploymentStatus(output, null);
        PolicyHeaderFields.setPolicyNo(output, policyHeader.getPolicyNo());

        String entityId = EmployedPhysicianFields.getEntityId(inputRecord);
        if (isFteByEntity) {
            EmployedPhysicianFields.setRiskParentId(output, "-1");
            EmployedPhysicianFields.setFteStatusCode(output, "ACTIVE");
            EmployedPhysicianFields.setEntityId(output, entityId);
            RiskFields.setRiskName(output, getEntityManager().getEntityName(entityId));
        }
        else {
            String addCode = RiskFields.getAddCode(inputRecord);
            String riskTypeCode = RiskFields.getRiskTypeCode(inputRecord);
            String location = RiskFields.getLocation(inputRecord);
            String riskName = null;
            if (addCode.equals("LOCATION") && !StringUtils.isBlank(location)) {
                riskName = getEntityManager().getEntityPropertyName(location);
            }
            else if ((addCode.equals("SLOT") || addCode.equals("FTE"))
                && entityId.equals("0")) {
                riskName = riskTypeCode;
            }
            else if ((addCode.equals("SLOT") || riskTypeCode.equals("FTE"))
                && !entityId.equals("0")) {
                riskName = riskTypeCode + " - " + getEntityManager().getEntityName(entityId);
            }
            else {
                riskName = getEntityManager().getEntityName(entityId);
            }
            EmployedPhysicianFields.setEntityId(output, entityId);
            RiskFields.setLocation(output, location);
            RiskFields.setRiskName(output, riskName);

            String riskBaseRecordId = RiskFields.getRiskBaseRecordId(inputRecord);
            EmployedPhysicianFields.setRiskParentId(output, riskBaseRecordId);
        }

        // Default policy Id
        PolicyHeaderFields.setPolicyId(output, policyHeader.getPolicyId());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForEmployedPhysician", output);
        }

        return output;
    }

    /**
     * save all Employed Physician infos
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     * @return process count to indicate how many records have been changed
     */
    public int saveAllEmployedPhysician(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllEmployedPhysician", new Object[]{policyHeader, inputRecords, inputRecord});
        }

        int processCount = 0;

        RecordSet deletedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));

        RecordSet delRecords = new RecordSet();
        Iterator deletedRsIter = deletedRecords.getRecords();
        //keep track to not pass the same pk more than once.
        Map fteIdMap = new HashMap();
        while (deletedRsIter.hasNext()) {
            Record deletedRec = (Record) deletedRsIter.next();
            String fteId = EmployedPhysicianFields.getPolicyFteRelationId(deletedRec);
            if (!fteIdMap.containsKey(fteId)) {
                delRecords.addRecord(deletedRec);
            }
        }
        //delete records
        processCount += getEmployedPhysicianDAO().deleteAllEmployedPhysician(delRecords);

        //validate employed physician data
        validateAllEmployedPhysician(policyHeader, inputRecords, inputRecord);

        insertedRecords.setFieldValueOnAll(EmployedPhysicianFields.POLICY_FTE_RELATION_ID, null);
        RecordSet changedRs = PMRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);
        //set tansaction log id as the current transaction log id
        changedRs.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID,policyHeader.getLastTransactionInfo().getTransactionLogId());
        RecordSet changedPendingRs = changedRs.getSubSet(
            new RecordFilter(EmployedPhysicianFields.CURR_STATUS_CODE, PMStatusCode.PENDING));
        RecordSet changedActiveRs = changedRs.getSubSet(
            new RecordFilter(EmployedPhysicianFields.CURR_STATUS_CODE, PMStatusCode.ACTIVE));

        // For the official records set the effective from date equal to the transaction effective date
        changedActiveRs.setFieldValueOnAll("transEffectiveFromDate",
            policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        
        changedRs.setFieldsOnAll(policyHeader.toRecord(), false);

        processCount += getEmployedPhysicianDAO().saveAllPendingEmployedPhysician(changedPendingRs);
        processCount += getEmployedPhysicianDAO().saveAllActiveEmployedPhysician(changedActiveRs);


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllEmployedPhysician", String.valueOf(processCount));
        }

        return processCount;
    }

    /**
     * load recordset of all FTE risks for selection
     *
     * @param inputRecord
     * @param policyHeader
     * @param selectIndProcessor
     * @return recordset of all FTE Risks
     */
    public RecordSet loadAllFteRisk(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor selectIndProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllFteRisk", new Object[]{policyHeader, inputRecord});
        }
        RecordSet rs = null;

        inputRecord.setFields(policyHeader.toRecord(), false);

        RecordLoadProcessor processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            selectIndProcessor, new SelectFteRiskEntitlementRecordLoadProcessor());
        rs = getEmployedPhysicianDAO().loadAllFteRisk(inputRecord, processor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllFteRisk", rs);
        }

        return rs;
    }


    /**
     * get changed values for changed record
     *
     * @param policyHeader
     * @param empPhysRec
     * @param inputRecord
     * @return output record with fte value
     */
    public Record getChangedValuesForEmployedPhysician(PolicyHeader policyHeader, Record empPhysRec, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedValuesForEmployedPhysician", new Object[]{policyHeader, empPhysRec, inputRecord});
        }

        //create a new record contains all infos of one employed physician data and query parameters
        Record valEmpPhysRec = new Record();
        valEmpPhysRec.setFields(inputRecord);
        valEmpPhysRec.setFields(empPhysRec, true);
        valEmpPhysRec.setUpdateIndicator(empPhysRec.getUpdateIndicator());
        valEmpPhysRec.setDisplayIndicator(empPhysRec.getDisplayIndicator());

        //get the record contains fte value
        Record outputRecord = getChangedValuesForEmployedPhysician(policyHeader, valEmpPhysRec);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedValuesForEmployedPhysician", outputRecord);
        }

        return outputRecord;

    }

    /**
     * get Values For ChangedRecord
     *
     * @param policyHeader
     * @param empPhysRec
     * @return inputRecord record with fte value
     */
    public Record getChangedValuesForEmployedPhysician(PolicyHeader policyHeader, Record empPhysRec) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedValuesForEmployedPhysician", new Object[]{empPhysRec});
        }
        Record outputRecord = new Record();

        if (empPhysRec.getBooleanValue("isToValidate", false).booleanValue()) {
            //reset updateIndicator
            empPhysRec.setUpdateIndicator(empPhysRec.getStringValue(UpdateIndicator.FIELD_NAME));
            //validate changed record
            validateEmployedPhysician(policyHeader, empPhysRec);
        }

        //set fte value
        String fteEquivalent = RiskFields.getFteEquivalent(empPhysRec);
        String acutalHours = EmployedPhysicianFields.getActualHours(empPhysRec);
        EmploymentStatusCode employmentStatus = EmployedPhysicianFields.getEmploymentStatus(empPhysRec);

        double fteValue = 0.00;
        boolean isActualHoursZero = isZero(acutalHours);
        boolean isFteEquivalentZero = isZero(fteEquivalent);
        if (!isActualHoursZero && !isFteEquivalentZero) {
            fteValue = Double.parseDouble(acutalHours) / Double.parseDouble(fteEquivalent);
        }
        else {
            String fteFullTimeHours = RiskFields.getFteFullTimeHrs(empPhysRec);
            String ftePartTimeHours = RiskFields.getFtePartTimeHrs(empPhysRec);
            String ftePerDiemTimeHours = RiskFields.getFtePerDiemHrs(empPhysRec);
            boolean isFteFullTimeHoursZero = isZero(fteFullTimeHours);
            boolean isFtePartTimeHoursZero = isZero(ftePartTimeHours);
            boolean isFtePerDiemTimeHours = isZero(ftePerDiemTimeHours);
            if (employmentStatus.isFulltime() && !isFteEquivalentZero && !isFteFullTimeHoursZero) {
                fteValue = Double.parseDouble(fteFullTimeHours) / Double.parseDouble(fteEquivalent);
            }
            else if (employmentStatus.isParttime() && !isFteEquivalentZero && !isFtePartTimeHoursZero) {
                fteValue = Double.parseDouble(ftePartTimeHours) / Double.parseDouble(fteEquivalent);
            }
            else if (employmentStatus.isPrediem() && !isFteEquivalentZero && !isFtePerDiemTimeHours) {
                fteValue = Double.parseDouble(ftePerDiemTimeHours) / Double.parseDouble(fteEquivalent);
            }
        }
        //set fte value
        String fte = null;
        if (fteValue != 0.00) {
            fte = formatNumericValue(fteValue, 4);
        }

        //end date changed set renewB field
        if (empPhysRec.getBooleanValue("isEndDateChanged", false).booleanValue()) {
            //set renewB field
            EmployedPhysicianFields.setRenewB(outputRecord, YesNoFlag.N);
            outputRecord.setFieldValue(EmployedPhysicianFields.RENEW_B+"LOVLABEL","No");
        }

        EmployedPhysicianFields.setFteValue(outputRecord, fte);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedValuesForEmployedPhysician", empPhysRec);
        }
        return outputRecord;
    }

    /**
     * calculate total fte for input recordset
     *
     * @param policyHeader
     * @param inputRecords
     * @return output record with total fte value
     */
    public Record calculateTotalFte(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "calculateTotalFte", new Object[]{inputRecords});
        }
        Record inputRecord = inputRecords.getSummaryRecord();
        Date lastTransEffDate;
        if (inputRecord.hasStringValue(TransactionFields.TRANSACTION_EFFECTIVE_FROM_DATE)) {
            lastTransEffDate = DateUtils.parseDate(TransactionFields.getTransactionEffectiveFromDate(inputRecord));
        }
        else {
            lastTransEffDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }
        
        Record outputRecord = new Record();
        double totalFteValue = 0.0;
        Iterator recIter = inputRecords.getRecords();
        while (recIter.hasNext()) {
            Record rec = (Record) recIter.next();


            Date effDate = DateUtils.parseDate(EmployedPhysicianFields.getEffectiveFromDate(rec));
            Date expDate = DateUtils.parseDate(EmployedPhysicianFields.getEffectiveToDate(rec));

            double fteValue = 0.0;
            if (rec.getDisplayIndicator().equals(DisplayIndicator.VISIBLE)
                && rec.hasStringValue(EmployedPhysicianFields.FTE_VALUE)) {
                fteValue = rec.getFloatValue(EmployedPhysicianFields.FTE_VALUE).doubleValue();
            }
            //calculate and set total fte value
            if (!lastTransEffDate.before(effDate) && lastTransEffDate.before(expDate)) {
                totalFteValue += fteValue;
            }
        }

        //set total fte value to output record
        EmployedPhysicianFields.setFteTotal(outputRecord, formatNumericValue(totalFteValue, 2));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "calculateTotalFte", outputRecord);
        }

        return outputRecord;
    }

    /**
     * return a boolean value to indicate if the inputValue is equal zero
     *
     * @param inputValue
     * @return result
     */
    private boolean isZero(String inputValue) {
        return StringUtils.isBlank(inputValue) || !FormatUtils.isFloat(inputValue) || inputValue.equals("0");
    }

    /**
     * @param inputValue
     * @return formated value
     */
    private String formatNumericValue(double inputValue, int decimal) {
        //format decimal
        BigDecimal tmpValue = new BigDecimal(inputValue);
        tmpValue = tmpValue.setScale(decimal, BigDecimal.ROUND_HALF_UP);
        double value = tmpValue.doubleValue();
        return String.valueOf(value);
    }

//-------------------------------------------------
// Configuration constructor and accessor methods

//-------------------------------------------------

    public void verifyConfig() {
        if (getEmployedPhysicianDAO() == null)
            throw new ConfigurationException("The required property 'employedPhysicianDAO' is missing.");
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
    }


    public EmployedPhysicianDAO getEmployedPhysicianDAO() {
        return m_employedPhysicianDAO;
    }

    public void setEmployedPhysicianDAO(EmployedPhysicianDAO employedPhysicianDAO) {
        m_employedPhysicianDAO = employedPhysicianDAO;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }
    
    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private EmployedPhysicianDAO m_employedPhysicianDAO;
    private EntityManager m_entityManager;
    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{EmployedPhysicianFields.EFFECTIVE_TO_DATE});
    private WorkbenchConfiguration m_workbenchConfiguration;
}
