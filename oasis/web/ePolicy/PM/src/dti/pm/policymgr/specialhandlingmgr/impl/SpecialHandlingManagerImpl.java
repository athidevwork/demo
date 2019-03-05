package dti.pm.policymgr.specialhandlingmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.busobjs.TransactionCode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.specialhandlingmgr.SpecialHandlingFields;
import dti.pm.policymgr.specialhandlingmgr.SpecialHandlingManager;
import dti.pm.policymgr.specialhandlingmgr.dao.SpecialHandlingDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for SpecialHandlingManager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 16, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/12/07         JMP         Add setting of default record from WebWB
 * 03/23/11         SXM         Issue 104000 - Modify start/end date validation conditions per use case.
 * 02/23/12         wfu         130015 - Modify saveAllSpecialHandlings to process output per use case.
 * ---------------------------------------------------
 */

public class SpecialHandlingManagerImpl implements SpecialHandlingManager {

    /**
     * Retrieves all Special Handlings' information for one policy
     *
     * @param policyHeader policy header
     * @return RecordSet
     */
    public RecordSet loadAllSpecialHandlings(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSpecialHandlings", new Object[]{policyHeader});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("screenMode",policyHeader.getScreenModeCode().toString());
        inputRecord.setFieldValue("termEff",policyHeader.getTermEffectiveFromDate());

        String isEditable = getSpecialHandlingDAO().getEditableConfiguration(inputRecord);
        
        Record input = new Record();
        input.setFields(policyHeader.toRecord(), false);
        // Setup the entitlements load processor
        RecordLoadProcessor entitlementRLP = new SpecialHandlingEntitlementRecordLoadProcessor(policyHeader, isEditable);
        RecordLoadProcessor loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            entitlementRLP, origFieldLoadProcessor);

        // Gets special handling record set
        RecordSet rs = getSpecialHandlingDAO().loadAllSpecialHandlings(input, loadProcessor);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSpecialHandlings", rs);
        }
        return rs;
    }

    /**
     * Save all Special Handlings' information
     *
     * @param policyHeader policy header
     * @param inputRecords a set of Records, each with the updated special handling info
     * @return the number of rows updated
     */
    public int saveAllSpecialHandlings(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSpecialHandlings", new Object[]{inputRecords});

        int updateCount = 0;
        Transaction trans;

        /* Create an new RecordSet to include all added and modified records */
        RecordSet allRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter
            (new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        /* If a change has occurred to special handling data - validate, create a trans and save */
        if (allRecords.getSize() > 0) {
            allRecords.setFieldsOnAll(policyHeader.toRecord(), false);

            /* validate the input records prior save them */
            validateAllSpecialHandlings(policyHeader, inputRecords);

            // Get the summary record out and use it for createTransaction
            Record inputRecord = inputRecords.getSummaryRecord();

            /* Create the transaction first */
            trans = getTransactionManager().createTransaction(policyHeader,
                inputRecord,
                policyHeader.getTermEffectiveFromDate(),
                TransactionCode.SPHANDLING,
                false);

            /* Set default values for insert, update */
            inputRecords.setFieldValueOnAll("transactionLogId", trans.getTransactionLogId());

            /*set RowStatus On ModifiedRecords*/
            PMRecordSetHelper.setRowStatusOnModifiedRecords(allRecords);

            /* Call DAO method to update records in batch mode */
            updateCount = getSpecialHandlingDAO().saveAllSpecialHandlings(allRecords);

            // Invoke doc output process
            TransactionFields.setTransactionLogId(inputRecord, trans.getTransactionLogId());
            getTransactionManager().processOutput(inputRecord, true);

            // Complete the transaction
            getTransactionManager().UpdateTransactionStatusNoLock(trans, TransactionStatus.COMPLETE);
        }

        l.exiting(getClass().getName(), "saveAllSpecialHandlings", new Integer(updateCount));
        return updateCount;
    }

    protected void validateAllSpecialHandlings(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllSpecialHandlings", new Object[]{policyHeader, inputRecords});
        }

        //get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String rowNum = String.valueOf(r.getRecordNumber() + 1);
            String rowId = SpecialHandlingFields.getPolSpecialHandlingId(r);
            Date effDate = DateUtils.parseDate(SpecialHandlingFields.getEffectiveFromDate(r));
            Date expDate = DateUtils.parseDate(SpecialHandlingFields.getEffectiveToDate(r));

            /* Validation #1 End Date must be greater than or equal to Start Date */
            if (expDate.before(effDate)) {
                // Set back to orignial value
                SpecialHandlingFields.setEffectiveToDate(r, SpecialHandlingFields.getOrigEffectiveToDate(r));
                MessageManager.getInstance().addErrorMessage("pm.maintainSpecialHandling.invalidEffectiveToDate.error",
                    new String[]{rowNum}, SpecialHandlingFields.EFFCTIVE_TO_DATE, rowId);
            }

            // Validation #2:  special handling dates must be within term dates
            if (!effDate.equals(DateUtils.parseDate(SpecialHandlingFields.getOrigEffectiveFromDate(r))) &&
                effDate.before(DateUtils.parseDate(policyHeader.getTermEffectiveFromDate()))) {
                // Set back to orignial value
                SpecialHandlingFields.setEffectiveFromDate(r, SpecialHandlingFields.getOrigEffectiveFromDate(r));
                MessageManager.getInstance().addErrorMessage("pm.maintainSpecialHandling.startDateOutsideTermDates.error",
                    new String[]{rowNum}, SpecialHandlingFields.EFFCTIVE_FROM_DATE, rowId);
            }
            if (!expDate.equals(DateUtils.parseDate(SpecialHandlingFields.getOrigEffectiveToDate(r))) &&
                expDate.after(DateUtils.parseDate(policyHeader.getTermEffectiveToDate()))) {
                // Set back to orignial value
                SpecialHandlingFields.setEffectiveToDate(r, SpecialHandlingFields.getOrigEffectiveToDate(r));
                MessageManager.getInstance().addErrorMessage("pm.maintainSpecialHandling.endDateOutsideTermDates.error",
                    new String[]{rowNum}, SpecialHandlingFields.EFFCTIVE_TO_DATE, rowId);
            }

            // If from date equals with to date, set renewalB field to "N"
            if (effDate.equals(expDate)) {
                SpecialHandlingFields.setRenewalB(r, YesNoFlag.N);
            }

            // If renewalB equals with "Y" and end date equals with the term expiration date,
            //  reset end date to "01/01/3000"
            if (SpecialHandlingFields.getRenewalB(r).booleanValue()
                && expDate.equals(DateUtils.parseDate(policyHeader.getTermEffectiveToDate()))) {
                SpecialHandlingFields.setEffectiveToDate(r, "01/01/3000");
            }

            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;
        }

        /* Validation #3:  Validate continuity */
        String[] keyFieldNames = new String[]{SpecialHandlingFields.SPECIALHANDLING_CODE};
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
            SpecialHandlingFields.EFFCTIVE_FROM_DATE, SpecialHandlingFields.EFFCTIVE_TO_DATE,
            SpecialHandlingFields.POL_SPECIALHANDLING_ID,
            "pm.maintainSpecialHandling.invalidContinuity.error", keyFieldNames, keyFieldNames);
        continuityValidator.validate(inputRecords);

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Invalid Special Handling data.");

        l.exiting(getClass().getName(), "validateAllSpecialHandlings");
    }

    /**
     * To get initial values for a new special handling record
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForSpecialHandling(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForSpecialHandling", new Object[]{policyHeader, inputRecord});
        }

        //get default record from workbench
        Record output = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_SPECIAL_HANDLING_ACTION_CLASS_NAME);

        // Get the initial entitlement values
        output.setFields(SpecialHandlingEntitlementRecordLoadProcessor.getInitialEntitlementValuesForSpecialHandling());

        // Default term effective and expiration dates based on current policy term
        SpecialHandlingFields.setEffectiveFromDate(output, policyHeader.getTermEffectiveFromDate());
        SpecialHandlingFields.setEffectiveToDate(output, policyHeader.getTermEffectiveToDate());

        // Default policy Id
        PolicyHeaderFields.setPolicyId(output,policyHeader.getPolicyId());

        // Add original values
        origFieldLoadProcessor.postProcessRecord(output,true);

        l.exiting(getClass().getName(), "getInitialValuesForSpecialHandling");
        return output;
    }

    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getSpecialHandlingDAO() == null) {
            throw new ConfigurationException("The required property 'SpecialHandlingDAO' is missing.");
        }
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public SpecialHandlingDAO getSpecialHandlingDAO() {
        return m_specialHandlingDAO;
    }

    public void setSpecialHandlingDAO(SpecialHandlingDAO specialHandlingDAO) {
        m_specialHandlingDAO = specialHandlingDAO;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private SpecialHandlingDAO m_specialHandlingDAO;
    private TransactionManager m_transactionManager;
    private WorkbenchConfiguration m_workbenchConfiguration;

    protected static final String MAINTAIN_SPECIAL_HANDLING_ACTION_CLASS_NAME = "dti.pm.policymgr.specialhandlingmgr.struts.MaintainSpecialHandlingAction";
    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{SpecialHandlingFields.EFFCTIVE_FROM_DATE, SpecialHandlingFields.EFFCTIVE_TO_DATE});
}
