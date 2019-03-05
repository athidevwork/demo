package dti.pm.policymgr.processacfmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.filter.Filter;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.processacfmgr.ProcessAcfFields;
import dti.pm.policymgr.processacfmgr.ProcessAcfManager;
import dti.pm.policymgr.processacfmgr.dao.ProcessAcfDAO;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for ProcessAcfManager.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/27/2012       syang       129088 - Modified validateAllAcf() to validate the allocAmt/commAmt and accept the negative Fee Amount.
 * 04/19/2012       syang       132566 - Modified validateAllAcf() to validate the allocAmt/commAmt to different policy types.
 * 04/25/2012       syang       129144 - Modified validateAllAcf(), the override alloc/comm type may be empty.
 * 05/11/2012       xnie        132566 - Modified validateAllAcf(): If the override is a percentage then the system must
 *                                       ensure that the total % (allocation + commission) equals 100% for all lines of
 *                                       business except Property has a Prem Diff override type.
 * ---------------------------------------------------
 */

public class ProcessAcfManagerImpl implements ProcessAcfManager {

    /**
     * Retrieves all product.
     *
     * @param inputRecord input record
     * @return recordSet
     */
    public RecordSet loadAllProduct(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProduct", new Object[]{inputRecord});
        }

        RecordLoadProcessor entitlementRLP = new ProcessAcfProductEntitlementRecordLoadProcessor();
        entitlementRLP = RecordLoadProcessorChainManager.getRecordLoadProcessor(entitlementRLP, AddSelectIndLoadProcessor.getInstance());
        RecordSet rs = getProcessAcfDAO().loadAllProduct(inputRecord, entitlementRLP);
        // System displays error message if no data found.
        if (rs.getSize() == 0) {
            MessageManager.getInstance().addErrorMessage("pm.processAcf.product.noDataFound");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProduct", rs);
        }
        return rs;
    }

    /**
     * Retrieves all override.
     *
     * @param inputRecord  input record
     * @param policyHeader
     * @return recordSet
     */
    public RecordSet loadAllOverride(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllOverride", new Object[]{inputRecord, policyHeader});
        }
        DefaultRecordLoadProcessor entitlementRLP = new ProcessAcfOverrideEntitlementRecordLoadProcessor(policyHeader);
        RecordSet rs = getProcessAcfDAO().loadAllOverride(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllOverride", rs);
        }
        return rs;
    }

    /**
     * Retrieves all result.
     *
     * @param inputRecord input record
     * @return recordSet
     */
    public RecordSet loadAllResult(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllResult", new Object[]{inputRecord});
        }
        DefaultRecordLoadProcessor entitlementRLP = new ProcessAcfResultEntitlementRecordLoadProcessor();
        RecordSet rs = getProcessAcfDAO().loadAllResult(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllResult", rs);
        }
        return rs;
    }

    /**
     * Retrieves all fee.
     *
     * @param inputRecord  input record
     * @param policyHeader
     * @return recordSet
     */
    public RecordSet loadAllFee(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllFee", new Object[]{inputRecord, policyHeader});
        }
        DefaultRecordLoadProcessor entitlementRLP = new ProcessAcfFeeEntitlementRecordLoadProcessor(policyHeader);
        RecordSet rs = getProcessAcfDAO().loadAllFee(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllFee", rs);
        }
        return rs;
    }

    /**
     * Save all override and fee records.
     *
     * @param inputRecordSets override and fee RecordSet
     * @param policyHeader 
     * @return int the number of rows saved.
     */
    public int saveAllAcf(RecordSet[] inputRecordSets, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAcf", new Object[]{inputRecordSets});
        }
        Filter filter = new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED});
        // Get changed override and fee record sets.
        RecordSet overrideChangedRs = inputRecordSets[1].getSubSet(filter);
        RecordSet resultRs = inputRecordSets[2];
        RecordSet feeChangedRs = inputRecordSets[3].getSubSet(filter);
        // Validate override, result and fee records.
        validateAllAcf(overrideChangedRs, resultRs, feeChangedRs, policyHeader);
        // Set changed flags.
        RecordSet overrideRs = OasisRecordSetHelper.setRowStatusOnModifiedRecords(overrideChangedRs);
        RecordSet feeRs = OasisRecordSetHelper.setRowStatusOnModifiedRecords(feeChangedRs);
        // Save override and fee.
        int updatedCount = 0;
        if (overrideRs.getSize() > 0) {
            updatedCount = getProcessAcfDAO().saveAllOverride(overrideRs);
        }
        if (feeRs.getSize() > 0) {
            updatedCount = getProcessAcfDAO().saveAllFee(feeRs);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAcf", new Integer(updatedCount));
        }
        return updatedCount;
    }

    /**
     * Validate override and fee data before save them.
     *
     * @param overrideRecordSet
     * @param resultRecordSet
     * @param feeRecordSet
     * @param policyHeader
     */
    protected void validateAllAcf(RecordSet overrideRecordSet, RecordSet resultRecordSet, RecordSet feeRecordSet, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllAcf", new Object[]{overrideRecordSet, resultRecordSet, feeRecordSet, policyHeader});
        }

        String policyType = policyHeader.getPolicyTypeCode();
        String propertyPolicyType = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_PROPERTY_POL_TYPE);
        Double percentAllocAmt = 0d, percentCommAmt = 0d, flatAllocAmt = 0d, flatCommAmt = 0d;
        boolean overridePercent = false, overrideFlat = false, overridePremDiff = false;
        // Validate the newly added overrides
        RecordSet newOverrideRs = overrideRecordSet.getSubSet(new UpdateIndicatorRecordFilter(new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        Iterator overrideIt = newOverrideRs.getRecords();
        while (overrideIt.hasNext()) {
            Record overrideRecord = (Record) overrideIt.next();
            String rowId = ProcessAcfFields.getPolicyBrokerageOverrideId(overrideRecord);
            String currentRowNum = String.valueOf(overrideRecord.getRecordNumber() + 1);
            // The productBrokerageId of newly added override is null.
            if (!overrideRecord.hasStringValue(ProcessAcfFields.PRODUCT_BROKERAGE_ID) &&
                (!overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_LAYER_NO) ||
                    !overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_ENTITY_ID) ||
                    !overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_EXTERNAL_ID) ||
                    !overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_ALLOC_AMT) ||
                    !overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_ALLOC_TYPE) ||
                    !overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_COMM_AMT) ||
                    !overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_COMM_TYPE))) {
                MessageManager.getInstance().addErrorMessage("pm.processAcf.add.override.missing", new String[]{currentRowNum},
                    ProcessAcfFields.OVERRIDE_LAYER_NO, rowId);
                throw new ValidationException("Invalid override data.");
            }

            // Calculate the total of allocation and commission.
            if (overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_ALLOC_TYPE) &&
                overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_COMM_TYPE) &&
                ProcessAcfFields.getOverrideAllocType(overrideRecord).equalsIgnoreCase(ProcessAcfFields.OverrideAllocTypeCodeValues.PERCENT) &&
                ProcessAcfFields.getOverrideCommType(overrideRecord).equalsIgnoreCase(ProcessAcfFields.OverrideAllocTypeCodeValues.PERCENT)) {
                if(!overridePercent){
                   overridePercent = true;
                }
                if (overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_ALLOC_AMT)) {
                    percentAllocAmt += Double.valueOf(ProcessAcfFields.getOverrideAllocAmt(overrideRecord));
                }
                if (overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_COMM_AMT)) {
                    percentCommAmt += Double.valueOf(ProcessAcfFields.getOverrideCommAmt(overrideRecord));
                }
            }
            else if (overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_ALLOC_TYPE) &&
                overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_COMM_TYPE) &&
                ProcessAcfFields.getOverrideAllocType(overrideRecord).equalsIgnoreCase(ProcessAcfFields.OverrideAllocTypeCodeValues.FLAT) &&
                ProcessAcfFields.getOverrideCommType(overrideRecord).equalsIgnoreCase(ProcessAcfFields.OverrideAllocTypeCodeValues.FLAT)) {
                if(!overrideFlat){
                   overrideFlat = true;
                }
                if (overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_ALLOC_AMT)) {
                    flatAllocAmt += Double.valueOf(ProcessAcfFields.getOverrideAllocAmt(overrideRecord));
                }
                if (overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_COMM_AMT)) {
                    flatCommAmt += Double.valueOf(ProcessAcfFields.getOverrideCommAmt(overrideRecord));
                }
            }
            else if (overrideRecord.hasStringValue(ProcessAcfFields.OVERRIDE_ALLOC_TYPE) &&
                ProcessAcfFields.getOverrideAllocType(overrideRecord).equalsIgnoreCase(ProcessAcfFields.OverrideAllocTypeCodeValues.PREM_DIFF)) {
                if(!overridePremDiff){
                   overridePremDiff = true;
                }
            }
        }
        // Calculate the transaction written premium for a specific transaction.
        String transLogId = policyHeader.getLastTransactionInfo().getTransactionLogId();
        Double transWrittenPremium = 0d;
        if (resultRecordSet != null && resultRecordSet.getSize() > 0) {
            Iterator resultIt = resultRecordSet.getRecords();
            while (resultIt.hasNext()) {
                Record resultRecord = (Record) resultIt.next();
                if (resultRecord.hasStringValue(ProcessAcfFields.TRANSACTION_LOG_ID) &&
                    transLogId.equals(ProcessAcfFields.getTransactionLogId(resultRecord)) &&
                    resultRecord.hasStringValue(ProcessAcfFields.DELTA_AMT)) {
                    transWrittenPremium += Double.valueOf(ProcessAcfFields.getDeltaAmt(resultRecord));
                }
            }
        }
        // The total allocation + commission for all lines of business whatever percentage or flat must be validated with the exception of Standalone and Property.
        // For Standalone, validate the flat dollar override type; for Property, validate the percentage override type.
        // If the override type is a percentage then the system must ensure that the total % (allocation + commission) equals 100% except Property has a Prem Diff override type.
        // If the override type is a flat dollar amount then the system must ensure that the total entered premium equals the transactions written premium.
        if (ProcessAcfFields.PolicyTypeCodeValues.STANDALONE_POL_TYPE.equalsIgnoreCase(policyType)) {
            if (overrideFlat && transWrittenPremium > 0d && (flatAllocAmt + flatCommAmt != transWrittenPremium)) {
                MessageManager.getInstance().addErrorMessage("pm.processAcf.add.override.flat.invalid", ProcessAcfFields.OVERRIDE_ALLOC_AMT);
                throw new ValidationException("Invalid override data.");
            }
        }
        else if (!StringUtils.isBlank(propertyPolicyType) && propertyPolicyType.indexOf(policyType) > -1) {
            if (!overridePremDiff && overridePercent && (percentAllocAmt + percentCommAmt != 100d)) {
                MessageManager.getInstance().addErrorMessage("pm.processAcf.add.override.percent.invalid", ProcessAcfFields.OVERRIDE_ALLOC_AMT);
                throw new ValidationException("Invalid override data.");
            }
        }
        else {
            if (overrideFlat && transWrittenPremium > 0d && (flatAllocAmt + flatCommAmt != transWrittenPremium)) {
                MessageManager.getInstance().addErrorMessage("pm.processAcf.add.override.flat.invalid", ProcessAcfFields.OVERRIDE_ALLOC_AMT);
                throw new ValidationException("Invalid override data.");
            }
            if (overridePercent && (percentAllocAmt + percentCommAmt != 100d)) {
                MessageManager.getInstance().addErrorMessage("pm.processAcf.add.override.percent.invalid", ProcessAcfFields.OVERRIDE_ALLOC_AMT);
                throw new ValidationException("Invalid override data.");
            }
        }

        // Validate fee
        Iterator feeIt = feeRecordSet.getRecords();
        while (feeIt.hasNext()) {
            Record feeRecord = (Record) feeIt.next();
            String rowId = ProcessAcfFields.getPolicyBrokerageFeeDetailId(feeRecord);
            String currentRowNum = String.valueOf(feeRecord.getRecordNumber() + 1);
            // Entity is required.
            if (!feeRecord.hasStringValue(ProcessAcfFields.FEE_ENTITY_ID) || Long.parseLong(ProcessAcfFields.getFeeEntityId(feeRecord)) <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.processAcf.add.fee.entity.missing", new String[]{currentRowNum},
                    ProcessAcfFields.FEE_ENTITY_ID, rowId);
            }
            // Fee amount is required.
            if (!feeRecord.hasStringValue(ProcessAcfFields.FEE_AMT) || !StringUtils.isNumeric(ProcessAcfFields.getFeeAmt(feeRecord))) {
                MessageManager.getInstance().addErrorMessage("pm.processAcf.add.fee.amt.missing", new String[]{currentRowNum},
                    ProcessAcfFields.FEE_AMT, rowId);
            }
            // Throw validation exception if there is any error message.
            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("Invalid acf data.");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllAcf");
        }
    }

    /**
     * Get initial values for override.
     *
     * @param inputRecord
     * @param policyHeader
     * @return Record
     */
    public Record getInitialValuesForOverride(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForOverride", new Object[]{inputRecord});
        }
        Record outputRecord = new Record();
        outputRecord.setFields(inputRecord);
        outputRecord.setFieldValue(ProcessAcfOverrideEntitlementRecordLoadProcessor.IS_OVERRIDE_DEL_AVAILABLE, YesNoFlag.Y);
        ProcessAcfFields.setOverrideTransId(outputRecord, policyHeader.getLastTransactionInfo().getTransactionLogId());
        ProcessAcfFields.setOverrideTermId(outputRecord, policyHeader.getTermBaseRecordId());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForOverride", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Get initial values for fee.
     *
     * @param inputRecord
     * @param policyHeader
     * @return Record
     */
    public Record getInitialValuesForFee(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForFee", new Object[]{inputRecord});
        }
        Record outputRecord = new Record();
        outputRecord.setFields(inputRecord);
        outputRecord.setFieldValue(ProcessAcfFeeEntitlementRecordLoadProcessor.IS_FEE_DEL_AVAILABLE, YesNoFlag.Y);
        ProcessAcfFields.setInterfaceStatusCode(outputRecord, INTERFACE_STATUS_CODE);
        ProcessAcfFields.setTransEff(outputRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        ProcessAcfFields.setFeeTransId(outputRecord, policyHeader.getLastTransactionInfo().getTransactionLogId());
        ProcessAcfFields.setFeeTermId(outputRecord, policyHeader.getTermBaseRecordId());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForFee", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Configuration constructor and access methods
     */
    public void verifyConfig() {
        if (getProcessAcfDAO() == null)
            throw new ConfigurationException("The required property 'processAcfDAO' is missing.");
    }

    public ProcessAcfDAO getProcessAcfDAO() {
        return m_processAcfDAO;
    }

    public void setProcessAcfDAO(ProcessAcfDAO processAcfDAO) {
        m_processAcfDAO = processAcfDAO;
    }

    private ProcessAcfDAO m_processAcfDAO;
    private final static String INTERFACE_STATUS_CODE = "PENDING";
}