package dti.pm.riskmgr.coimgr.impl;

import dti.ci.addressmgr.AddressFields;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.XMLRecordSetMapper;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.security.Authenticator;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.entitymgr.EntityFields;
import dti.pm.entitymgr.EntityManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.coimgr.CoiFields;
import dti.pm.riskmgr.coimgr.CoiManager;
import dti.pm.riskmgr.coimgr.dao.CoiDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of CoiManager Interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 6, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/19/2008       fcb         CoiSaveProcessor added.
 * 03/25/2008       fcb         getNoteByNoteCode added.
 * 04/03/2008       fcb         saveAllCoiHolder: refactored logic to determine if
 *                              it is a long running transactions.
 * 03/19/2010       102700      saveAllCoiHolder: passed the Risk Header as a workflow attribute.
 * 03/22/2010       gxc         104426 -  getInitialValuesForCoiHolder: modify effectiveFromDate defaulting
 * 08/25/2010       syang       Issue 108651 - Modified loadAllCoiHolder() to pass riskEffectiveToDate to handle renew indicator.
 * 02/15/2011       ryzhao      Issue 116650 - Modified performSaveAllCoiHolder() to set newEndorsementCode field null when creating ENDCOIHOLD transaction.
 * 08/30/2011       ryzhao      124458 - Modified validateAsOfDateForProcessCoi to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 12/02/2011       wfu         127703 - Modified getInitialValuesForCoiHolder to disable Renew if risk is cancelled.
 * 12/21/2011       syang       126373 - Modified validateAllCoiHolder() to ignore the flat records while validating overlapping records.
 * 01/04/2011       wfu         127802 - Modified getInitialValuesForCoiHolder to set ePolicy common format entity name.
 * 03/01/2012       xnie        129417 - Roll backed fix of 116650.
 * 04/03/2012       lmjiang     128983 - Change the additional parameters passed to the background procedure.
 * 04/13/2012       xnie        120683 - Modified loadAllCoiHolder() to remove user profile check of changing address.
 * 04/24/2012       jshen       128983 - Set riskBaseRecordId and transactionLogId when get initial values for adding COI
 * 08/17/2012       adeng       135238 - Modified saveAllCoiHolder() and performSaveAllCoiHolder() to make sure to do the
 *                                       validation before checking of long running transaction. System will display
 *                                       validation error message if there is any validation exception happened.
 * 08/17/2012       xnie        120683 - 1) Modified performSaveAllCoiHolder() to add save address logic.
 *                                       2) Modified verifyConfig() to add check for getPolicyManager and getDBUtilityManager.
 *                                       3) Added getPolicyManager(), setPolicyManager(), and getDBUtilityManager().
 *                                       4) Added private variable m_policyManager.
 * 11/28/2012       awu         139274 - Modified validateAllCoiHolder() to validate the start date and end date, both should not be empty.
 * 05/22/2013       adeng       144925 - Modified loadAllCoiHolder() to pass in record mode code.
 * 05/31/2013       xnie        145167 - Modified performSaveAllCoiHolder()
 *                                       1) Set entityRoleId for inputRecordSet.getSummaryRecord() based on sourceIdRecord.
 *                                       2) Set COI Holder Id when COI Holder is new.
 * 06/06/2013       adeng       144984 - Added one more parameter Record inputRecord for method copyAllCoi() and pass it on.
 * 10/14/2013       adeng       145247 - Modified performSaveAllCoiHolder() to support case when sourceIdField is NULL
 *                                       case which means when sourceIdField is null, 'coiHolderId' will be the default
 *                                       value of sourceIdField.
 * 12/23/2013       adeng       146639 - Modified validateAllCoiHolder() to use a DisplayIndicatorRecordFilter to filter
 *                                       out invisible records from the record set which is going to be validated.
 * 08/27/2014       jyang       156599 - Revert 76098's change on the condition of setting needToCaptureTransaction's value.
 * 03/08/2016       wdang       168418 - 1) Remove PolicyManager reference, use EntityManager instead.
 *                                       2) Remove unnecessary usage of ApplicationContext.getBean for DBUtilityManager.
 * 10/28/2016       lzhang      180689 - Modified performSaveAllCoiHolder: pass policyHeader to getCoiDAO().saveAllCoiHolder().
 * 03/20/2017       wli         183962 - Revert changes by 126373
 * 09/12/2017       wrong       187839 - Added generateCoiForWS() for coi change web service.
 * ---------------------------------------------------
 */
public class CoiManagerImpl implements CoiManager, CoiSaveProcessor {
    /**
     * Returns a RecordSet loaded with list of COI Holder for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of COI Holder.
     */

    public RecordSet loadAllCoiHolder(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoiHolder", new Object[]{policyHeader, loadProcessor});
        }

        // Set the input record
        Record record = policyHeader.toRecord();
        // Calculate dates
        Record dateRecord = getCoiDAO().calculateDateForCoi(record);
        String riskEffectiveToDate = dateRecord.getStringValue("riskEffectiveToDate");
        // Setup the record load processor
        CoiEntitlementRecordLoadProcessor coiLoadProcessor = new CoiEntitlementRecordLoadProcessor(policyHeader, riskEffectiveToDate);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, coiLoadProcessor);

        CoiFields.setCoiEffectiveFromDate(record, policyHeader.getTermEffectiveFromDate());
        CoiFields.setCoiEffectiveToDate(record, policyHeader.getTermEffectiveToDate());
        RecordMode recordModeCode = RecordMode.TEMP;
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        else if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        record.setFieldValue(PMCommonFields.RECORD_MODE_CODE, recordModeCode);

        // Call the DAO to load coi data
        RecordSet rs = getCoiDAO().loadAllCoiHolder(record, loadProcessor);

        Record summaryRecord = rs.getSummaryRecord();
        summaryRecord.setFieldValue(RiskFields.RISK_NAME, policyHeader.getRiskHeader().getRiskName());
        summaryRecord.setFields(dateRecord);
        EntityFields.setEntityId(summaryRecord, policyHeader.getRiskHeader().getRiskEntityId());

        String transModeSysPara = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_COI_WIP_TRANS);
        String needToCaptureTransaction = "Y";
        if (YesNoFlag.getInstance(transModeSysPara).booleanValue()
            && policyHeader.getPolicyIdentifier().getPolicyViewMode().isWIP()) {
            needToCaptureTransaction = "N";
        }
        summaryRecord.setFieldValue("needToCaptureTransaction", needToCaptureTransaction);
        summaryRecord.setFieldValue("pmCoiClaimsParam", SysParmProvider.getInstance().getSysParm(SysParmIds.PM_COI_CLAIMS));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoiHolder", rs);
        }
        return rs;
    }

    /**
     * Save all inserted/updated COI Holder records.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the updated COI Holder Detail info
     *                     matching the fields returned from the loadAllCoiHolder method.
     * @return the number of rows updated.
     */
    public void saveAllCoiHolder(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllCoiHolder", new Object[]{inputRecords});

        inputRecords.setFieldValueOnAll(RiskFields.RISK_EFFECTIVE_FROM_DATE,
            RiskFields.getRiskEffectiveFromDate(inputRecords.getSummaryRecord()), false);
        inputRecords.setFieldValueOnAll(RiskFields.RISK_EFFECTIVE_TO_DATE,
            RiskFields.getRiskEffectiveToDate(inputRecords.getSummaryRecord()), false);
        // Validate COI data
        validateAllCoiHolder(policyHeader, inputRecords);

        if(getTransactionManager().isRatingLongRunning()) {
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            String policyNumber = PolicyHeaderFields.getPolicyNo(inputRecords.getSummaryRecord());
            wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecords.getSummaryRecord()),
                        SAVE_COI_HOLDER_PROCESS,
                        SAVE_COI_HOLDER_INITIAL_STATE);
            wa.setWorkflowAttribute(policyNumber, "inputRecords", inputRecords);
            wa.setWorkflowAttribute(policyNumber, "riskHeader", policyHeader.getRiskHeader());
        }
        else {
            CoiSaveProcessor saveProcessor = (CoiSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
            saveProcessor.performSaveAllCoiHolder(policyHeader, inputRecords);
        }

        l.exiting(getClass().getName(), "saveAllCoiHolder");
    }

    /**
     * Save all inserted/updated COI Holder records.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the updated COI Holder Detail info
     *                     matching the fields returned from the loadAllCoiHolder method.
     * @return the number of rows updated.
     */
    public void performSaveAllCoiHolder(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "performSaveAllCoiHolder", new Object[]{inputRecords});

        int updateCount = 0;

        // Create an new RecordSet to include all added and modified records
        RecordSet modifiedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter
            (new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        // If a change has occurred to COI data - validate, create/get a trans and save
        if (modifiedRecords.getSize() > 0) {
            modifiedRecords.setFieldsOnAll(policyHeader.toRecord(), false);

            // Get transaction
            String transModeSysPara = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_COI_WIP_TRANS);
            if (StringUtils.isBlank(transModeSysPara)) {
                transModeSysPara = "N";
            }

            Transaction trans;
            String transactionLogId;
            boolean isTransCreated = false;
            Record inputRecord = inputRecords.getSummaryRecord();
            // If Y, the transaction in progress (if exists) of the policy is used.
            String policyTransLogId = policyHeader.getLastTransactionInfo().getTransactionLogId();
            if (YesNoFlag.getInstance(transModeSysPara).booleanValue()
                && policyHeader.getPolicyIdentifier().getPolicyViewMode().isWIP()) {
                transactionLogId = policyTransLogId;
                trans = new Transaction();
                trans.setTransactionLogId(transactionLogId);
            }
            // If N or the policy doesn't have a transaction in progress, an ENDCOIHOLD transaction code is created.
            else {
                trans = getTransactionManager().createTransaction(policyHeader,
                    inputRecord,
                    policyHeader.getTermEffectiveFromDate(),
                    TransactionCode.ENDCOIHOLD,
                    false);
                transactionLogId = trans.getTransactionLogId();
                isTransCreated = true;
            }

            modifiedRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, transactionLogId);

            //set RowStatus On ModifiedRecords
            PMRecordSetHelper.setRowStatusOnModifiedRecords(modifiedRecords);

            try {
                // If entity role id is null, we need to get new pk and set it.
                Iterator itNewPk = modifiedRecords.getRecords();
                while (itNewPk.hasNext()) {
                    Record newPkRecord = (Record) itNewPk.next();
                    if (!newPkRecord.hasStringValue(CoiFields.ENTITY_ROLE_ID)) {
                        long newPk = getDbUtilityManager().getNextSequenceNo();
                        CoiFields.setEntityRoleId(newPkRecord, String.valueOf(newPk));
                    }

                    if (!newPkRecord.hasStringValue(CoiFields.COI_HOLDER_ID) || Long.parseLong(CoiFields.getCoiHolderId(newPkRecord)) < 0) {
                        long newPk = getDbUtilityManager().getNextSequenceNo();
                        CoiFields.setCoiHolderId(newPkRecord, String.valueOf(newPk));
                    }
                }

                // If there are any address changes for current COI Holder, we will save them.
                String addressChanges;
                Iterator it = modifiedRecords.getRecords();
                while (it.hasNext()) {
                    Record coiRecord = (Record) it.next();
                    RecordSet inputRecordSet = null;
                    if (coiRecord.hasStringValue(CoiFields.ADDRESS_CHANGES)) {
                        addressChanges = CoiFields.getAddressChanges(coiRecord);
                        inputRecordSet = new RecordSet();
                        XMLRecordSetMapper.getInstance(AddressFields.ADDRESS_ID).map(addressChanges, inputRecordSet);
                        if (inputRecordSet.getSize() > 0) {
                            inputRecordSet.getSummaryRecord().setFieldValue(TransactionFields.TRANSACTION_LOG_ID, transactionLogId);
                            if (CoiFields.getSourceIdRecord(modifiedRecords.getSummaryRecord()).equals(CoiFields.COI_HOLDER_ID)
                                || StringUtils.isBlank(CoiFields.getSourceIdRecord(modifiedRecords.getSummaryRecord()))) {
                                CoiFields.setEntityRoleId(inputRecordSet.getSummaryRecord(), CoiFields.getCoiHolderId(coiRecord));
                            }
                            else {
                                CoiFields.setEntityRoleId(inputRecordSet.getSummaryRecord(), CoiFields.getEntityRoleId(coiRecord));
                            }

                            getEntityManager().saveEntityRoleAddress(policyHeader, inputRecordSet);
                        }
                    }
                }

                // Call DAO method to update records in batch mode
                updateCount = getCoiDAO().saveAllCoiHolder(modifiedRecords, policyHeader);

                // If no Existing Transaction
                if (isTransCreated) {
                    // Update the transaction status to OUTPUT
                    //  TransactionStatus inprogressTransStatus = policyHeader.getLastTransactionInfo().getTransactionStatusCode();

                    getTransactionManager().UpdateTransactionStatusNoLock(trans, TransactionStatus.OUTPUT);

                    // Process output (PM Call to Output)
                    Record record = new Record();
                    record.setFieldValue("transactionLogId", trans.getTransactionLogId());
                    record.setFieldValue("transactionCode", trans.getTransactionCode());
                    record.setFieldValue("termBaseRecordId", policyHeader.getTermBaseRecordId());
                    getTransactionManager().processOutput(record, false);

                    // Update the transaction status to complete
                    getTransactionManager().UpdateTransactionStatusNoLock(trans, TransactionStatus.COMPLETE);

                }
            }
            catch (Exception ex) {
                // If save failed, roll back all changes and delete wip if transaction is created by the page.
                if (isTransCreated) {
                    getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
                }
                // Throw the exception
                throw ExceptionHelper.getInstance().handleException("Failed to save COI Holder.", ex);
            }
        }

        l.exiting(getClass().getName(), "performSaveAllCoiHolder", new Integer(updateCount));

    }

    /**
     * Validate COI holder data for saving
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the updated COI Holder Detail info
     *                     matching the fields returned from the loadAllCoiHolder method.
     */
    protected void validateAllCoiHolder(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllCoiHolder", new Object[]{policyHeader, inputRecords});
        }

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}))
            .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));

        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String rowNum = String.valueOf(r.getRecordNumber() + 1);
            String rowId = r.getStringValue(CoiFields.COI_HOLDER_ID);
            String sCoiStartDate = CoiFields.getCoiEffectiveFromDate(r);
            String sCoiEndDate = CoiFields.getCoiEffectiveToDate(r);
            if (sCoiStartDate != null && sCoiEndDate != null) {
                Date coiStartDate = DateUtils.parseDate(sCoiStartDate);
                Date coiEndDate = DateUtils.parseDate(sCoiEndDate);

                String sRiskEffDate = RiskFields.getRiskEffectiveFromDate(r);
                String sRiskExpDate = RiskFields.getRiskEffectiveToDate(r);
                Date riskEffDate = DateUtils.parseDate(sRiskEffDate);
                Date riskExpDate = DateUtils.parseDate(sRiskExpDate);

                // Validation #1: Start and End date of COI holder must be within the risk effective date period
                if (coiStartDate.before(riskEffDate) || coiEndDate.after(riskExpDate)) {
                    String fieldId = CoiFields.COI_EFFECTIVE_TO_DATE;
                    if (coiStartDate.before(riskEffDate))
                        fieldId = CoiFields.COI_EFFECTIVE_FROM_DATE;
                    MessageManager.getInstance().addErrorMessage("pm.maintainCoi.dateOutsideTermDates.error",
                        new String[]{rowNum}, fieldId, rowId);
                }

                // Validation #2: End Date cannot be before Start Date
                if (coiEndDate.before(coiStartDate)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainCoi.invalidEndDate.error",
                        new String[]{rowNum}, CoiFields.COI_EFFECTIVE_TO_DATE, rowId);
                }
            }
            else if (sCoiStartDate == null) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCoi.startDateNull.error",
                    new String[]{rowNum}, CoiFields.COI_EFFECTIVE_FROM_DATE, rowId);
                throw new ValidationException("Invalid COI Holder data.");
            }
            else if (sCoiEndDate == null) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCoi.endDateNull.error",
                    new String[]{rowNum}, CoiFields.COI_EFFECTIVE_TO_DATE, rowId);
                throw new ValidationException("Invalid COI Holder data.");
            }
        }

        // Validation #3: Rows with the same COI holder entity can not have a date overlap
        String[] keyFieldNames = new String[]{CoiFields.RISK_ENTITY_ID};
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
            CoiFields.COI_EFFECTIVE_FROM_DATE, CoiFields.COI_EFFECTIVE_TO_DATE, CoiFields.COI_HOLDER_ID,
            "pm.maintainCoi.invalidContinuity.error", keyFieldNames, keyFieldNames);
        continuityValidator.validate(inputRecords);

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Invalid COI Holder data.");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllCoiHolder");
        }
    }

    /**
     * Get initial values for COI Holder
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return Record a Record loaded with initial values.
     */
    public Record getInitialValuesForCoiHolder(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForCoiHolder", new Object[]{policyHeader, inputRecord});
        }

        Record output = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_COI_ACTION_CLASS_NAME);
        if (policyHeader.isWipB()) {
            String transEff = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
            String riskEff = RiskFields.getRiskEffectiveFromDate(inputRecord);

            if (DateUtils.parseDate(riskEff).after(DateUtils.parseDate(transEff))) {
                CoiFields.setCoiEffectiveFromDate(output, riskEff);
            }
            else {
                CoiFields.setCoiEffectiveFromDate(output, transEff);
            }
        }
        else {
            CoiFields.setCoiEffectiveFromDate(output, RiskFields.getRiskEffectiveFromDate(inputRecord));
        }
        CoiFields.setCoiEffectiveToDate(output, RiskFields.getRiskEffectiveToDate(inputRecord));
        CoiFields.setExcessCoverageB(output, YesNoFlag.N);
        CoiFields.setCoiStatus(output, "PENDING");
        if (policyHeader.getRiskHeader().getRiskStatusCode().isCancelled()) {
            CoiFields.setRenewB(output, YesNoFlag.N);
        }

        String coiCsSearch = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_COI_CS_SEARCH);
        String coiEntityId = CoiFields.getRiskEntityId(inputRecord);
        CoiFields.setRiskEntityId(output, coiEntityId);
        if (StringUtils.isBlank(coiCsSearch) || YesNoFlag.getInstance(coiCsSearch).booleanValue()) {
            CoiFields.setCoiName(output, getEntityManager().getEntityName(coiEntityId));
        } else {
            CoiFields.setCoiName(output, CoiFields.getCoiName(inputRecord));
        }

        // Set riskBaseRecordId
        RiskFields.setRiskBaseRecordId(output, policyHeader.getRiskHeader().getRiskBaseRecordId());
        // Set transactionLogId
        TransactionFields.setTransactionLogId(output, policyHeader.getLastTransactionInfo().getTransactionLogId());

        // Set the initial COI Entitlement values
        CoiEntitlementRecordLoadProcessor.setInitialEntitlementValuesForCoi(policyHeader, output);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForCoiHolder", output);
        }
        return output;
    }

    /**
     * To validate the As of Date
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with the as of date value
     */
    public void validateAsOfDateForGenerateCoi(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAsOfDateForGenerateCoi", new Object[]{policyHeader, inputRecord});
        }

        if (!inputRecord.hasStringValue(CoiFields.AS_OF_DATE) || StringUtils.isBlank(CoiFields.getAsOfDate(inputRecord))) {
            MessageManager.getInstance().addErrorMessage("pm.generateCoi.asOfDate.empty.error", CoiFields.AS_OF_DATE);
            throw new ValidationException("As of Date is empty.");
        }

        String sAsOfDate = CoiFields.getAsOfDate(inputRecord);
        Date asOfDate = DateUtils.parseDate(sAsOfDate);

        String sTermEffDate = policyHeader.getTermEffectiveFromDate();
        String sTermExpDate = policyHeader.getTermEffectiveToDate();
        Date termEffDate = DateUtils.parseDate(sTermEffDate);
        Date termExpDate = DateUtils.parseDate(sTermExpDate);

        // Validation #1: the As of Date cannot be prior to the term effective date
        if (asOfDate.before(termEffDate)) {
            MessageManager.getInstance().addErrorMessage("pm.generateCoi.asOfDate.error1", CoiFields.AS_OF_DATE);
        }
        else if (asOfDate.after(termExpDate) || asOfDate.equals(termExpDate)) {
            MessageManager.getInstance().addErrorMessage("pm.generateCoi.asOfDate.error2", CoiFields.AS_OF_DATE);
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Invalid COI As of Date.");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAsOfDateForGenerateCoi");
        }
    }

    /**
     * To load cutoff date for COI Claims History page
     *
     * @return Record a Record loaded with cutoff date value.
     */
    public Record loadCutoffDateForCoiClaim() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCutoffDateForCoiClaim");
        }

        Record record = new Record();
        CoiFields.setCoiCutoffDate(record, getCoiDAO().loadCutoffDateForCoiClaim().getStringValue("returnValue"));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCutoffDateForCoiClaim", record);
        }
        return record;
    }

    /**
     * Generate all COI.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with the details of COI claims data.
     */
    public void generateAllCoi(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateAllCoi", new Object[]{policyHeader, inputRecord});
        }

        Record record = new Record();
        record.setFields(inputRecord);

        // Get As of Date from UserSession
        String asOfDate = CoiFields.getAsOfDate(inputRecord);

        String endorsementCode = "";
        //Endorsement Code is set to the data value from the Please Select a Letter dropdown if it's visible
        String coiSelectLetter = null;
        if (inputRecord.hasStringValue(CoiFields.COI_SELECT_LETTER)) {
            coiSelectLetter = CoiFields.getCoiSelectLetter(inputRecord);
        }

        if (!StringUtils.isBlank(coiSelectLetter)) {
            endorsementCode = coiSelectLetter;
        }
        else {
            // Otherwise get Include Claim or Exclude Claim for endorsement code
            String coiIncludeExcludeClaimValue = null;
            if (inputRecord.hasStringValue(CoiFields.COI_INCLUDE_EXCLUDE_CLAIM)) {
                coiIncludeExcludeClaimValue = CoiFields.getCoiIncludeExcludeClaim(inputRecord);
            }
            if (!StringUtils.isBlank(coiIncludeExcludeClaimValue)) {
                endorsementCode = coiIncludeExcludeClaimValue;
            }
        }

        // Create an new transaction
        Record transRecord = new Record();
        transRecord.setFieldValue("policyTermHistoryId", policyHeader.getPolicyTermHistoryId());
        transRecord.setFieldValue("accountingDate", new Date());
        transRecord.setFieldValue("endorsementCode", endorsementCode);
        transRecord.setFieldValue("transactionComment", null);
        Transaction trans = getTransactionManager().createTransaction(policyHeader,
            transRecord,
            asOfDate,
            TransactionCode.GENCOIHOLD,
            false);

        // transactionLogId
        String transactionLogId = trans.getTransactionLogId();
        TransactionFields.setTransactionLogId(record, transactionLogId);
        // termBaseRecordId
        PolicyHeaderFields.setTermBaseRecordId(record, policyHeader.getTermBaseRecordId());
        // coiCutoffDate
        if (inputRecord.hasStringValue(CoiFields.COI_CUTOFF_DATE)) {
            record.setFieldValue(CoiFields.COI_CUTOFF_DATE, DateUtils.parseDate(CoiFields.getCoiCutoffDate(inputRecord)));
        }

        // Generate COI
        getCoiDAO().generateAllCoi(record);

        // Update the transaction status to complete
        getTransactionManager().UpdateTransactionStatusNoLock(trans, TransactionStatus.COMPLETE);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateAllCoi");
        }
    }

    /**
     * To derive the minimum and maximum dates for each entity role record.
     *
     * @param inputRecords
     */
    public void deriveMinAndMaxDates(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deriveMinAndMaxDates", new Object[]{inputRecords});
        }

        String minDateStr = "01/01/3000", maxDateStr = "01/01/1900";
        Date minDate = DateUtils.parseDate(minDateStr);
        Date maxDate = DateUtils.parseDate(maxDateStr);
        Iterator iter = inputRecords.getRecords();
        while (iter.hasNext()) {
            Record r = (Record) iter.next();
            String effFromDateStr = CoiFields.getEffectiveFromDate(r);
            String effToDateStr = CoiFields.getEffectiveToDate(r);
            if (effToDateStr.equals("01/01/3000")) {
                effToDateStr = getCoiDAO().getActualExpDate(r);
            }
            CoiFields.setCoiEffectiveToDate(r, effToDateStr);
            Date effFromDate = DateUtils.parseDate(effFromDateStr);
            Date effToDate = DateUtils.parseDate(effToDateStr);
            if (!effFromDate.after(minDate)) {
                minDateStr = effFromDateStr;
                minDate = effFromDate;
            }
            if (!effToDate.before(maxDate)) {
                maxDateStr = effToDateStr;
                maxDate = effToDate;
            }
        }
        Record sumRec = inputRecords.getSummaryRecord();
        CoiFields.setMaximumDate(sumRec, maxDateStr);
        CoiFields.setMinimumDate(sumRec, minDateStr);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deriveMinAndMaxDates");
        }
    }

    /**
     * To validate the As of Date for generate client coi
     *
     * @param inputRecords the selected COI Holder list
     * @return comma delimited policyList string if invalid records exist, "" if invalid record not exist.
     */
    public String validateAsOfDateForProcessCoi(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAsOfDateForProcessCoi", new Object[]{inputRecords});
        }

        Record sumRec = inputRecords.getSummaryRecord();
        String asOfDateStr = CoiFields.getAsOfDate(sumRec);
        String minDateStr = CoiFields.getMinimumDate(sumRec);
        String maxDateStr = CoiFields.getMaximumDate(sumRec);
        Date asOfDate = DateUtils.parseDate(asOfDateStr);
        Date minDate = DateUtils.parseDate(minDateStr);
        Date maxDate = DateUtils.parseDate(maxDateStr);
        // validate if asOfDate is greater than the risk effective date
        if (asOfDate.before(minDate)) {
            MessageManager.getInstance().addErrorMessage(
                "pm.generateClientCoi.asOfDate.beforeRiskEffFromDate.error");
            return "";
        }
        // validate if asOfDate is 1 day less than risk expiration date
        if (asOfDate.after(maxDate) || asOfDate.equals(maxDate)) {
            MessageManager.getInstance().addErrorMessage(
                "pm.generateClientCoi.asOfDate.afterRiskEffToDate.error");
            return "";
        }

        String policyStr = "";
        Iterator iter = inputRecords.getRecords();
        while (iter.hasNext()) {
            Record r = (Record) iter.next();
            String effFromDateStr = CoiFields.getEffectiveFromDate(r);
            String effToDateStr = CoiFields.getCoiEffectiveToDate(r);
            Date effFromDate = DateUtils.parseDate(effFromDateStr);
            Date effToDate = DateUtils.parseDate(effToDateStr);

            // validate if asOfDate falls outside of the effective from/to dates of the record
            if (asOfDate.before(effFromDate) || asOfDate.after(effToDate) || asOfDate.equals(effToDate)) {
                String externalId = CoiFields.getExternalId(r);
                if (policyStr.indexOf(externalId) < 0) {
                    policyStr += externalId + ",";
                }
            }
        }
        if (policyStr.length() > 0) {
            policyStr = policyStr.substring(0, policyStr.length() - 1);
            MessageManager.getInstance().addConfirmationPrompt("pm.generateClientCoi.asOfDate.error",
                new String[]{FormatUtils.formatDateForDisplay(asOfDateStr), policyStr});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAsOfDateForProcessCoi");
        }
        return policyStr;
    }

    /**
     * To process all client COI.
     *
     * @param inputRecords a set of Records, each with the selected COI Holder info from CIS
     *                     and other info like entityId, values from Process COI Claim etc.
     */
    public void processAllCoi(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processAllCoi", new Object[]{inputRecords});
        }

        try {
            if (inputRecords.getSize() > 0) {
                Record record = new Record();
                Record firstRec = inputRecords.getRecord(0);
                record.setFields(firstRec);

                String extIdStr = "", roleIdStr = "";
                Iterator iter = inputRecords.getRecords();
                while (iter.hasNext()) {
                    Record r = (Record) iter.next();
                    extIdStr += CoiFields.getExternalId(r) + ",";
                    roleIdStr += CoiFields.getEntityRoleId(r) + ",";
                }
                extIdStr = extIdStr.substring(0, extIdStr.length() - 1);
                roleIdStr = roleIdStr.substring(0, roleIdStr.length() - 1);
                record.setFieldValue("extIds", extIdStr);
                record.setFieldValue("rolePks", roleIdStr);
                record.setFieldValue("noReq", String.valueOf(inputRecords.getSize()));
                record.setFieldValue("endorseCode", null);
                record.setFieldValue("comments", null);
                if (!record.hasStringValue(CoiFields.COI_CUTOFF_DATE)) {
                    CoiFields.setCoiCutoffDate(record, null);
                }

                // Process all Client COI
                Record retRec = getCoiDAO().processAllCoi(record);
                String retCode = retRec.getStringValue("retCode");
                if (retCode.equals("SUCCESS")) {
                    MessageManager.getInstance().addInfoMessage("pm.generateCoi.save.success.info");
                }
                else if (retCode.equals("FAILED")) {
                    throw new AppException("Process Client COI failed.");
                }
            }
        }
        catch (AppException ae) {
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processAllCoi");
        }
    }


    /**
     * copy all coi data to target risk
     *
     * @param inputRecords
     */
    public void copyAllCoi(RecordSet inputRecords, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyAllCoi", new Object[]{inputRecords});
        }

        getCoiDAO().copyAllCoi(inputRecords, inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyAllCoi");
        }
    }

    /**
     * Method to return the notes information from Ajax.
     * @param policyHeader
     * @param inputRecord
     */
    public void getNoteByNoteCode(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());

        inputRecord.setFieldValue("termEff", policyHeader.getTermEffectiveFromDate());
        inputRecord.setFieldValue("termExp", policyHeader.getTermEffectiveToDate());
        String addlParms =  "entity_role_pk^" + inputRecord.getFieldValue("entityRoleId") + "^" +
                            "risk_base_record_fk^" + inputRecord.getFieldValue("riskBaseRecordIdId") + "^";
        inputRecord.setFieldValue("addlParms", addlParms);

        String originalNotes = (String)inputRecord.getFieldValue("note");
        Record outputRecord = getCoiDAO().getNoteByNoteCode(inputRecord);

        String notes = (String)outputRecord.getFieldValue("note");
        if (notes==null) {
            notes = originalNotes;
        }
        inputRecord.setFieldValue("note", notes);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getNoteByNoteCode", inputRecord);
        }
    }

    /**
     * Method that generate Coi information base on input record.
     * <p/>
     * @param  inputRecord Record that contains generate coi information parameters.
     * @return new created transaction log fk.
     */
    public String generateCoiForWS(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateCoiForWS", new Object[]{inputRecord});
        }
        Record outputRecord = getCoiDAO().generateCoiForWS(inputRecord);

        String transactionLogId = null;
        String returnCode = outputRecord.getStringValue("returnCode");
        if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == 1) {
            transactionLogId = outputRecord.getStringValue("transactionLogId");
        }
        else if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == -1) {
            String returnMsg = outputRecord.getStringValue("returnMsg");
            AppException ae = new AppException("ws.policyCertificateOfInsuranceChange.invalid.parameters", returnMsg);
            l.throwing(getClass().getName(), "generateCoiForWS", ae);
            throw ae;
        }
        else if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == 2) {
            String returnMsg = outputRecord.getStringValue("returnMsg");
            AppException ae = new AppException("ws.policyCertificateOfInsuranceChange.system.parameter.warning", returnMsg);
            l.throwing(getClass().getName(), "generateCoiForWS", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateCoiForWS", transactionLogId);
        }
        return transactionLogId;
    }

    public void verifyConfig() {
        if (getCoiDAO() == null)
            throw new ConfigurationException("The required property 'coiDAO' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
    }

    public CoiDAO getCoiDAO() {
        return m_coiDAO;
    }

    public void setCoiDAO(CoiDAO coiDAO) {
        m_coiDAO = coiDAO;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
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

    public CoiManagerImpl() {
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    private CoiDAO m_coiDAO;
    private TransactionManager m_transactionManager;
    private EntityManager m_entityManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    protected static final String MAINTAIN_COI_ACTION_CLASS_NAME = "dti.pm.riskmgr.coimgr.struts.MaintainCoiAction";
    private static final String SAVE_COI_HOLDER_PROCESS = "CoiHolderWorkflow";
    private static final String SAVE_COI_HOLDER_INITIAL_STATE = "invokeSaveCoiHolder";
    protected static final String SAVE_PROCESSOR = "CoiManager";
    private DBUtilityManager m_dbUtilityManager;;
}
