package dti.pm.tailmgr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.core.http.RequestIds;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.Term;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.tailmgr.TailFields;
import dti.pm.tailmgr.TailManager;
import dti.pm.tailmgr.TailRecordMode;
import dti.pm.tailmgr.TailScreenMode;
import dti.pm.tailmgr.dao.TailDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessManager;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of TailManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 21, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/21/2008       fcb         processSaveAllTailAndComponent: added support for long running transactions.
 * 11/06/2008       yhyang      #87658 Modify loadAllTail():If the tail created is attached to the prior term,
 *                              system should pass the prior termBaseRecordID,eff/exp to load tail page rather than the current term.
 * 04/29/2010       bhong       107094 - Fixed error in performTailProcess method to reset endorsement code.
 * 07/26/2010       gxc         106051 - Modified to handle Adjust Limit process code
 * 08/18/2010       gxc         Issue 106055 - modified isCaptureFinancePercentageRequired to return String instead of
 *                              boolean as one of the possible values is "B".
 *                              Added method saveTailCharge to save finance charge processed from Fin Charge option
 * 10/01/2010       gxc         Issue 111905/111953 Modified to not save the finance rate when it is 0%.
 * 11/01/2010       syang       Issue 113780 - Modified getInitialValuesForTailCharge() to set current rate to ratePercent
 *                              if current rate is greater than zero.
 * 02/25/2011       dzhang      Issue 113062 - Modified performTailProcess() For tail cancel transaction,we need to insert
 *                              records in transaction_applied_term for all other terms from the minimum term that the
 *                              continuous tail existed from. so we pass the minimum term that the continuous tail belonged to.
 * 03/15/2011       jshen       Issue 118616 - Allow modify Gross Premium field in prior term.
 * 03/22/2011       gxc         Issue 113062 - Added getMinimumTermData to obtain/set the minimum term's data and call it before rating
 * 03/25/2011       dzhang      Issue 113062 - Update per Joe's comments.
 * 04/21/2011       dzhang      Issue 120020 - Modified getMinimumTermData.
 * 11/03/2011       dzhang      Issue 124874 - Modified getTailRecordMode: in reinstate WIP set the tailRecordMode to TEMP.
 * 11/08/2011       wfu         Issue 126582 - Modified getTailScreenMode to update tail screen mode to VIEW ONLY
 *                              if the last transaction is ENDQUOTE and status is COMPLETE.
 * 11/21/2011       wfu         126157 - Refactored getTailScreenMode for issue 92123, 118616, 126582 and current 126157.
 * 04/27/2012       xnie        132999 - Modified getInitialValuesForTailCharge to add a parameter inputRecord.
 * 08/22/2012       tcheng      135664 - Modified getTailScreenMode to update tail screen mode to VIEW ONLY
 *                              if the screen mode code is VIEW_POLICY and status is INPROGRESS.
 * 04/25/2014       xnie        153450 - Modified validateAddManualTail() to set trans id and term base id to
 *                                       inputRecord, instead of setting policy header to inputRecord which will change
 *                                       correct risk base id.
 * 06/08/2016       sma         177372 - Replaced Integer with Long for term FK
 * 11/23/2017       xnie        188616 - Modified getTailScreenMode to replace calling loadAllTransaction with calling
 *                                       loadPolicyTermList to improve performance.
 * 09/25/2018       wrong       195793 - Modified processSaveAllTailAndComponent to add needToHandleExitWorkFlow into
 *                                       input record for long running transaction.
 * ---------------------------------------------------
 */

public class TailManagerImpl implements TailManager, TailSaveProcessor {
    /**
     * load all tales
     *
     * @param policyHeader
     * @param inputRecord
     * @return recordset include all tail parents
     */
    public RecordSet loadAllTail(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor selectIndProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllTail",
            new Object[]{policyHeader, inputRecord});
        inputRecord.setFields(policyHeader.toRecord(), false);
        //If the tail created is attached to the prior term,
        // system should pass the prior termBaseRecordID,eff/exp to load tail page rather than the current term.
        //We need to display the tail records in the current term even when they are attached to the prior term
        //Scenario 1 - cancellation wip created and tail is displayed (in this case, workflowInstanceIdName exists
        //Scenario 2 - cancellation wip created and tail is displayed and user clicks Rate/save (in this case, workflowInstanceIdNameSince does not exist)
        // the user is in the same tail screen, the tail should still be displayed.  This is when prcessCode=SAVE.
        if (((inputRecord.hasStringValue("workflowInstanceIdName") && "policyNo".equals(inputRecord.getStringValue("workflowInstanceIdName"))) ||
            (inputRecord.hasStringValue("processCode") && "SAVE".equals(inputRecord.getStringValue("processCode"))
            && inputRecord.hasStringValue("policyViewMode") && "WIP".equals(inputRecord.getStringValue("policyViewMode"))) )
            && getCancelProcessManager().isTailCreatedForPriorTerm(policyHeader)) {

            MessageManager.getInstance().addInfoMessage("pm.matainMultiCancel.tailCreated.msg");
            
            String priorTermBaseRecordId = policyHeader.getPriorTermBaseRecordId();
            Iterator termList = policyHeader.getPolicyTerms();
            // Get the prior term.
            Term term = null;
            while (termList.hasNext()) {
                term = (Term) termList.next();
                if (priorTermBaseRecordId.equals(term.getTermBaseRecordId())) {
                    break;
                }
            }
            // Set the prior term property to the inputRecord.
            if (term != null) {
                TailFields.setTermBaseRecordId(inputRecord,term.getTermBaseRecordId());
                TailFields.setTermEffectiveFromDate(inputRecord,term.getEffectiveFromDate());
                TailFields.setTermEffectiveToDate(inputRecord,term.getEffectiveToDate());
            }
        }
        //setCurrentMode
        ScreenModeCode currentPolicyMode = policyHeader.getScreenModeCode();
        TailFields.setCurrentPolicyMode(inputRecord, currentPolicyMode);
        //set record mode
        TailRecordMode recordMode = getTailRecordMode(policyHeader);
        TailFields.setTailRecordMode(inputRecord, recordMode);
        //set current screen mode
        TailScreenMode tailScreenMode = getTailScreenMode(policyHeader);
        TailFields.setTailScreenMode(inputRecord, tailScreenMode);

        RecordLoadProcessor entitlementRLP = TailEntitlementRecordLoadProcessor.
            getInstance(inputRecord, policyHeader, getTailDAO());
        TailRecordLoadProcessor tailRLP = TailRecordLoadProcessor.getInstance();
        RecordLoadProcessor processor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            RecordLoadProcessorChainManager.getRecordLoadProcessor(selectIndProcessor, entitlementRLP), tailRLP);

        RecordSet tails = getTailDAO().loadAllTail(inputRecord, processor);

        RecordLoadProcessor tailDetailRLP = TailDetailRecordLoadProcessor.getInstance(tailRLP.getTailRecordMap());
        //get all tail detail infos, and bind to every tail
        getTailDAO().loadAllTailDetail(inputRecord, tailDetailRLP);

        l.exiting(getClass().getName(), "loadAllTail", tails);
        return tails;
    }

    /**
     * Wrapper to invoke the save of all inserted/updated tail and component
     *
     * @param policyHeader     the summary policy information corresponding to the provided coverages.
     * @param tailRecords      a set of Records, each with the updated Tail Detail info
     *                         matching the fields returned from the loadAllCoverage method.
     * @param componentRecords a set of Records, each with the updated Component Detail info
     *                         matching the fields returned from the ComponentManager.saveAllComponents method.
     * @return updated row count
     */
    public int saveAllTailAndComponent(PolicyHeader policyHeader, RecordSet tailRecords, RecordSet componentRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllTailAndComponent", new Object[]{tailRecords, componentRecords});

        int processCount = 0;
        //save tail
        processCount += saveAllTail(policyHeader, tailRecords);
        //save tail component
        processCount += getComponentManager().saveAllComponent(policyHeader, componentRecords, ComponentOwner.TAIL, tailRecords);

        l.exiting(getClass().getName(), "saveAllTailAndComponent", new Integer(processCount));
        return processCount;
    }

    /**
     * This method will call the save and validate methods within separate transactions,
     * and return the validate result returned from the validate method.
     * This method will also delete the WIP and unlock the policy if required
     * (according to Alternate Flow: Processing Error).
     *
     * @param policyHeader
     * @param inputRecord
     * @param tailRecords
     * @param componentRecords
     * @return validate result
     */
    public String processSaveAllTailAndComponent(PolicyHeader policyHeader, Record inputRecord, RecordSet tailRecords, RecordSet componentRecords) {

        Logger l = LogUtils.enterLog(getClass(), "processSaveAllTailAndComponent",
            new Object[]{tailRecords, inputRecord, componentRecords});
        TailSaveProcessor tailSaveProcessor = (TailSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
        String validateResult = null;
        try {

            //save tail and component data
            tailSaveProcessor.saveAllTailAndComponent(policyHeader, tailRecords, componentRecords);
            //validate tail data and tail component data
            validateResult = validateTailData(policyHeader);
            if (validateResult.equals("FAILED")) {
                MessageManager.getInstance().addErrorMessage("pm.maintainTail.validateError");
                throw new ValidationException("Internal Error.  Policy Validation failure");
            }

            //rate tail
            if (validateResult.equals("VALID")) {
                if (inputRecord.getBooleanValue(RQ_PARM_IS_RATE_TAIL, false).booleanValue()) {
                    //save selected ids in cached inputRecords for next request
                    saveSelectedIds(tailRecords, inputRecord);

                    if (getTransactionManager().isRatingLongRunning()) {
                        inputRecord.setFieldValue("needToHandleExitWorkFlow", "Y");
                        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                        wa.initializeWorkflow(policyHeader.getPolicyNo(),
                            RATE_TRANSACTION_PROCESS,
                            RATE_TRANSACTION_INITIAL_STATE);
                    }
                    else {
                        //rate policy
                        Record rec = new Record();
                        RecordFilter selectedRecordFilter = new RecordFilter(RequestIds.SELECT_IND, "-1");
                        rec = getMinimumTermData(policyHeader, tailRecords.getSubSet(selectedRecordFilter));
                        rec.setFields(policyHeader.toRecord(), false);
                        getTransactionManager().performTransactionRating(rec);
                    }
                }
                else {
                    //save tail transaction as official for update/cancel tail process
                    TransactionCode lastTranCode = policyHeader.getLastTransactionInfo().getTransactionCode();
                    if (lastTranCode.isTailEndorse() || lastTranCode.isTailCancel()) {
                        getLockManager().lockPolicy(policyHeader, "TailManagerImpl: save tail transaction as official.");
                        // save tail transaction as official
                        PolicyHeader polHeader = policyHeader;
                        RecordFilter selectedRecordFilter = new RecordFilter(RequestIds.SELECT_IND, "-1");
                        Record rec = getMinimumTermData(policyHeader, tailRecords.getSubSet(selectedRecordFilter));
                        polHeader.setPolicyTermHistoryId(TailFields.getPolicyTermHistoryId(rec));
                        polHeader.setTermEffectiveFromDate(TailFields.getTermEffectiveFromDate(rec));
                        polHeader.setTermEffectiveToDate(TailFields.getTermEffectiveToDate(rec));
                        getTransactionManager().processSaveTailTransactionAsOfficial(polHeader, inputRecord);
                    }
                }
            }
        }
        catch (ValidationException ve) {
            throw ve;
        }
        catch (Exception e) {
            // if there are any unexpected exceptions, delete WIP and unlock policy if needed
            TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();
            if (policyHeader.isWipB() && !(transactionCode.isCancellation() || transactionCode.isRiskCancel()
                || transactionCode.isCovgCancel() || transactionCode.isScvgCancel())) {
                getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
            }
            throw ExceptionHelper.getInstance().handleException("failed to save tails.", e);
        }


        l.exiting(getClass().getName(), "processSaveAllTailAndComponent", validateResult);
        return validateResult;
    }

    /**
     * Save all Tale' information
     *
     * @param policyHeader policy header
     * @param inputRecords a set of Records, each with the updated tail info
     * @return the number of rows updated
     */
    public int saveAllTail(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllTail", new Object[]{policyHeader, inputRecords});
        int processCount = 0;
        Record polRec = policyHeader.toRecord();

        // Get the WIP records
        RecordSet wipRecords = inputRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet offRecords = inputRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));

        // Delete the WIP records marked for delete in batch mode
        RecordSet deletedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        if (deletedWipRecords.getSize() > 0)
            processCount += getCoverageManager().deleteAllCoverage(policyHeader, deletedWipRecords);


        RecordSet updatedRecordSet = new RecordSet();
        // Add the updated WIP records in batch mode
        RecordSet updatedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        // Add the updated OFFICIAL records in batch mode
        RecordSet updatedOffRecords = offRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        updatedRecordSet.addRecords(updatedWipRecords);
        updatedRecordSet.addRecords(updatedOffRecords);

        updatedRecordSet.setFieldsOnAll(polRec, false);
        if (updatedRecordSet.getSize() > 0)
            processCount += getTailDAO().updateAllTail(updatedRecordSet);

        l.exiting(getClass().getName(), "saveAllTail", new Integer(processCount));
        return processCount;
    }

    /**
     * check if the tail data is valid
     *
     * @param policyHeader
     * @return validate result
     */
    public String validateTailData(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "validateTailData", new Object[]{policyHeader});

        String validateResult = getTailDAO().getTailDataValidateResult(policyHeader.toRecord());

        l.exiting(getClass().getName(), "validateTailData", validateResult);

        return validateResult;
    }


    /**
     * perform validate tail transaction process
     *
     * @param inputRecord
     * @param policyHeader
     * @param inputRecords
     * @return RecordSet contains all selected records with validation infos for every record
     */
    public RecordSet validateTailProcess(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "validateTailProcess", new Object[]{policyHeader, inputRecords});

        RecordFilter selectedReocordFilter = new RecordFilter(RequestIds.SELECT_IND, "-1");
        RecordSet selectedRecords = inputRecords.getSubSet(selectedReocordFilter);
        TailProcessCode tailProcessCode = TailProcessCode.getInstance(TailFields.getProcessCode(inputRecord));
        //check the selected records' count
        if (selectedRecords.getSize() == 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainTail.noTailSelectedError");
            throw new ValidationException("no tail selected");
        }
        //get tail constant record, contains transction comment, transaction code infos
        Record constantDataRecord = getTailConstants(policyHeader, inputRecord);

        //construct a copy of selected tail coverage's
        //this recordset is the data source for tailValidationError page
        RecordSet processedRecords = new RecordSet();
        Iterator selTailRecIter = selectedRecords.getRecords();
        while (selTailRecIter.hasNext()) {
            Record selTailRec = new Record();
            selTailRec.setFields((Record) selTailRecIter.next());
            processedRecords.addRecord(selTailRec);
        }

        //prepare inputRecord for validate tail DAO
        Record paramRec = getParams(processedRecords);
        inputRecord.setFields(paramRec);
        inputRecord.setFields(policyHeader.toRecord(), false);
        inputRecord.setFields(constantDataRecord, false);

        //validate tail
        Record validateResultRec = getTailDAO().getTailProcessValidateResult(inputRecord);

        //fetch the validate result
        //bind validate result to each tail record
        String validateStatusList = validateResultRec.getStringValue("status");
        String validateMsgList = validateResultRec.getStringValue("msg");
        String validateResult = validateResultRec.getStringValue("return");

        String[] statusArray = null;
        String[] msgArray = null;
        boolean isInvalid = validateResult.equalsIgnoreCase("INVALID");

        if (isInvalid) {
            if (validateStatusList != null)
                statusArray = validateStatusList.split(",");
            if (validateMsgList != null)
                msgArray = validateMsgList.split(",");
        }
        selTailRecIter = processedRecords.getRecords();
        int i = 0;
        boolean isErrorDoneAvailable = false;
        while (selTailRecIter.hasNext()) {
            Record selTailRec = (Record) selTailRecIter.next();
            //set validation infos to every tail record
            String validateStatus = (isInvalid && statusArray != null && i < statusArray.length) ? statusArray[i] : "";
            String message = (isInvalid && msgArray != null && i < msgArray.length) ? msgArray[i] : "";
            String validateMessage = validateStatus.equals("") ?
                "" : new StringBuffer().append(validateStatus).append(": ").append(message).toString();

            selTailRec.setFieldValue("validateStatus", validateStatus);
            selTailRec.setFieldValue("message", validateMessage);

            //disable select checkbox, if the record is invlid to process tail transaction
            if (statusArray != null && i < statusArray.length) {
                if (statusArray[i].equalsIgnoreCase("ERROR") || statusArray[i].equalsIgnoreCase("FAILED")) {
                    selTailRec.setFieldValue("isSelectAvailable", YesNoFlag.N);
                    selTailRec.setFieldValue(RequestIds.SELECT_IND, "0");
                }
                else if (statusArray[i].equalsIgnoreCase("WARNING")) {
                    selTailRec.setFieldValue(RequestIds.SELECT_IND, "0");
                    isErrorDoneAvailable = true;
                }
                else {
                    isErrorDoneAvailable = true;
                }
            }
            else {
                isErrorDoneAvailable = true;
            }
            processedRecords.getSummaryRecord().setFieldValue("isDoneAvailable", YesNoFlag.getInstance(isErrorDoneAvailable));

            //set tal credit info
            Record inRecord = new Record();
            inRecord.setFields(selTailRec);
            inRecord.setFields(policyHeader.toRecord(), false);
            Record creditRec = getTailDAO().getTailCredit(inRecord);
            selTailRec.setFields(creditRec);

            i++;
        }

        //check always display tail accept status
        if (!validateResult.equalsIgnoreCase("INVALID") && tailProcessCode.isAccept()) {
            YesNoFlag showTailErrScn = YesNoFlag.getInstance(
                SysParmProvider.getInstance().getSysParm(SysParmIds.PM_SHOW_TAIL_ERR_SCN));
            if (showTailErrScn.booleanValue()) {
                validateResult = "INVALID";
            }
        }

        //set validateResult to summery record of processed tails
        processedRecords.getSummaryRecord().setFieldValue("validateResult", validateResult);


        l.exiting(getClass().getName(), "validateTailProcess", validateResult);
        return processedRecords;
    }

    /**
     * perform validate tail delta
     *
     * @param inputRecord
     * @return validate result
     */
    public boolean validateTailDelta(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateTailDelta", new Object[]{inputRecord});
        boolean isValid = false;
        Record outRec = getTailDAO().getTailDeltaValidateResult(inputRecord);
        String result = outRec.getStringValue("rc");
        String outMsg = outRec.getStringValue("msg");
        if (result.equals("-1")) {
            MessageManager.getInstance().addErrorMessage("pm.maintainTail.deltaValidationError", new String[]{outMsg});
        }
        else if (result.equals("-2")) {
            MessageManager.getInstance().addErrorMessage("pm.maintainTail.failDeltaValidationError");
        }
        else {
            isValid = true;
        }
        l.exiting(getClass().getName(), "validateTailDelta", new Boolean(isValid));
        return isValid;
    }

    /**
     * get default transaction parameters
     *
     * @param policyHeader
     * @param inputRecord
     * @param selectedRecords
     * @return record include default accounting date, transaction effective date, transaction code, transaction comments
     */
    public Record getDefaultTransactionParms(PolicyHeader policyHeader, Record inputRecord, RecordSet selectedRecords) {
        Logger l = LogUtils.enterLog(getClass(), "getDefualtTransactionParms", new Object[]{inputRecord});

        Record outRec = new Record();

        Record constantsRecord = getTailConstants(policyHeader, inputRecord);
        outRec.setFields(constantsRecord);

        String accountingDate = getDefaultAccountingDate(selectedRecords);
        outRec.setFieldValue("accountingDate", accountingDate);

        l.exiting(getClass().getName(), "getDefualtTransactionParms", outRec);

        return outRec;
    }


    /**
     * perform tail process
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords *
     */
    public void performTailProcess(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "performTailProcess", new Object[]{policyHeader, inputRecord, inputRecords});

        // transaction in the process
        Transaction transaction = null;
        inputRecord.setFields(policyHeader.toRecord(), false);
        // issue#107094 Reset endorsement code to null to avoid using the endorsement code from last transaction
        inputRecord.setFieldValue("newEndorsementCode", null);

        RecordFilter selectedReocordFilter = new RecordFilter(RequestIds.SELECT_IND, "-1");
        RecordSet selectedRecords = inputRecords.getSubSet(selectedReocordFilter);

        //get process code from request, the value is ACCEPT/DECLINE/UPDATE/CANCEL/REACTIVE/REINSTATE/ADJ_LIMIT
        TailProcessCode tailProcessCode = TailProcessCode.getInstance(TailFields.getProcessCode(inputRecord));
        //set selected ids in cached inputRecord for next request
        saveSelectedIds(selectedRecords, inputRecord);
        //get transaction effective date
        String transactionEffectiveDate = getTransactionEffectiveDate(policyHeader, inputRecord, selectedRecords);
        //get constant values for process transaction
        Record constantRec = getTailConstants(policyHeader, inputRecord);
        //set default transaction comment
        if (!inputRecord.hasStringValue("newTransactionComment")) {
            String transactionComment = TransactionFields.getTransactionComment(constantRec);
            inputRecord.setFieldValue("newTransactionComment", transactionComment);
        }

        //get transaction code
        TransactionCode transactionCode = tailProcessCode.isDecline() && TransactionFields.hasNewDeclineReasonCode(inputRecord) ?
            TransactionCode.getInstance(TransactionFields.getNewDeclineReasonCode(inputRecord)) : TransactionFields.getTransactionCode(constantRec);

        // if is cancellation tail, reset the transaction parameters derived from cancel window
        if (tailProcessCode.isCancel()) {
            TransactionFields.setNewAccountingDate(inputRecord, CancelProcessFields.getAccountingDate(inputRecord));
            TransactionFields.setNewTransactionComment(inputRecord, CancelProcessFields.getCancellationComments(inputRecord));
        }

        //Issue 113062 Norcal
        if (tailProcessCode.isCancel() && YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_TAIL_CXL_ANY_TERM, "N")).booleanValue()) {
            RecordSet sortedSelectedRecords = selectedRecords.getSortedCopy(new Comparator() {
                public int compare(Object o1, Object o2) {
                    Record s1 = (Record) o1;
                    Record s2 = (Record) o2;
                    Long termBaseRecordId1 = s1.getLongValue(TailFields.TERM_BASE_RECORD_ID);
                    Long termBaseRecordId2 = s2.getLongValue(TailFields.TERM_BASE_RECORD_ID);
                    return termBaseRecordId1.compareTo(termBaseRecordId2);
                }
            }
            );

            Record minimumTermRecord = sortedSelectedRecords.getRecord(0);
            TailFields.setMinimumTermBaseRecordId(inputRecord, TailFields.getTermBaseRecordId(minimumTermRecord));
        }

        //create transaction
        //set if need to lock the policy
        boolean lockPolicy = true;
        transaction = getTransactionManager().createTransaction(
            policyHeader, inputRecord, transactionEffectiveDate, transactionCode, lockPolicy);

        //process tail changes
        if (!tailProcessCode.isSave() && !tailProcessCode.isUpdate()) {
            TransactionFields.setTransactionLogId(inputRecord, transaction.getTransactionLogId());

            Record paramRec = getParams(selectedRecords);
            inputRecord.setFields(paramRec);
            getTailDAO().processTail(inputRecord);
        }

        //save tail finance charge for Accept Tail process
        if (tailProcessCode.isAccept() &&
            YesNoFlag.getInstance(isCaptureFinancePercentageRequired(policyHeader)).booleanValue() &&
            TailFields.hasRatePercent(inputRecord) && !TailFields.getRatePercent(inputRecord).equals("0")) {
            Iterator selectedRecordIter = selectedRecords.getRecords();
            while (selectedRecordIter.hasNext()) {
                Record selRec = (Record) selectedRecordIter.next();
                selRec.setFields(policyHeader.toRecord(), false);
                selRec.setFields(inputRecord, false);
                getTailDAO().saveTailCharge(selRec);
            }
        }

        if (tailProcessCode.isDecline() || tailProcessCode.isActivate()
            || tailProcessCode.isReinstate() || tailProcessCode.isAccept() || tailProcessCode.isAdjLimit()) {
            // Call the transactionmgr business component to perform the save logic
            getTransactionManager().processSaveTailTransactionAsOfficial(policyHeader, inputRecord);
        }

        //handle validation exceptions
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("failed to perform tail process ");
        }

        l.exiting(getClass().getName(), "performTailProcess");
    }


    /**
     * get common parms for tail related procedure
     *
     * @param selectedRecords
     * @return record include tail procedure required parma
     */
    protected Record getParams(RecordSet selectedRecords) {
        Logger l = LogUtils.enterLog(getClass(), "getParams", new Object[]{selectedRecords});

        Record paramsRecord = new Record();

        StringBuffer covgIdList = new StringBuffer();
        StringBuffer subCovgBList = new StringBuffer();
        StringBuffer covgRelIdList = new StringBuffer();
        Iterator selTailRecIter = selectedRecords.getRecords();
        while (selTailRecIter.hasNext()) {
            //construct a copy of selected tail coverage's
            Record selTailRec = (Record) selTailRecIter.next();

            //construct two parameters for validating tail
            addParm(covgIdList, CoverageFields.getCoverageId(selTailRec));
            addParm(covgRelIdList, TailFields.getCoverageRelationId(selTailRec));
            String isSubCoverage = "Y";
            if (StringUtils.isBlank(TailFields.getSubCoverageDesc(selTailRec))) {
                isSubCoverage = "N";
            }
            addParm(subCovgBList, isSubCoverage);
        }
        paramsRecord.setFieldValue("numCoverage", new Integer(selectedRecords.getSize()));
        paramsRecord.setFieldValue("covgPkList", covgIdList);
        paramsRecord.setFieldValue("subCovgBList", subCovgBList);
        paramsRecord.setFieldValue("covgRelPkList", covgRelIdList);

        l.exiting(getClass().getName(), "getParams", paramsRecord);
        return paramsRecord;
    }

    /**
     * add parm to parmList buffer
     *
     * @param parmList
     * @param parm
     */
    protected void addParm(StringBuffer parmList, String parm) {
        Logger l = LogUtils.enterLog(getClass(), "addParm", new Object[]{parmList, parm});
        if (parmList.length() != 0) {
            parmList.append(',');
        }
        parmList.append(parm);
        l.exiting(getClass().getName(), "addParm", parmList);
    }

    /**
     * @param policyHeader
     * @return a record include stored proceudre parameter, tansaction code and transaction comments info
     */
    protected Record getTailConstants(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getTailConstants", new Object[]{policyHeader, inputRecord});

        Record returnRec = new Record();
        TransactionCode transactionCode = null;
        String transComments = null;
        //get process code from reuquest, the value is ACCEPT/DECLINE/UPDATE/CANCEL/REACTIVE/REINSTATE/ADJ_LIMIT
        TailProcessCode tailProcessCode = TailProcessCode.getInstance(TailFields.getProcessCode(inputRecord));

        if (tailProcessCode.isAccept()) {
            TailFields.setProcessCode(inputRecord, TailProcessCode.ACCEPT.getName());
            transactionCode = TransactionCode.TLACCEPT;
            transComments = "Accept Tail";
        }
        else if (tailProcessCode.isActivate()) {
            TailFields.setProcessCode(inputRecord, TailProcessCode.ACTIVATE.getName());
            transactionCode = TransactionCode.TLACTIVATE;
            transComments = "Activate Tail";
        }
        else if (tailProcessCode.isDecline()) {
            TailFields.setProcessCode(inputRecord, TailProcessCode.DECLINE.getName());
            transactionCode = TransactionCode.TLDECLINE;
            transComments = "Decline Tail";
        }
        else if (tailProcessCode.isCancel()) {
            TailFields.setProcessCode(inputRecord, TailProcessCode.CANCEL.getName());
            transactionCode = TransactionCode.TLCANCEL;
            transComments = "Cancel Tail";
        }
        else if (tailProcessCode.isReinstate()) {
            TailFields.setProcessCode(inputRecord, TailProcessCode.REINSTATE.getName());
            transactionCode = TransactionCode.TLREINST;
            transComments = "Reinstate Tail";
        }
        else if (tailProcessCode.isUpdate() || tailProcessCode.isSave()) {
            TailFields.setProcessCode(inputRecord, TailProcessCode.SAVE.getName());
            transactionCode = TransactionCode.TLENDORSE;
            transComments = "Update Tail";
        }
        else if (tailProcessCode.isAdjLimit()) {
            TailFields.setProcessCode(inputRecord, TailProcessCode.ADJ_LIMIT.getName());
            transactionCode = TransactionCode.TLENDORSE;
            transComments = "Update Tail";
        }

        TransactionFields.setTransactionCode(returnRec, transactionCode);
        TransactionFields.setTransactionComment(returnRec, transComments);

        l.exiting(getClass().getName(), "getTailConstants", returnRec);
        return returnRec;
    }

    /**
     * derive transaction effetive
     *
     * @param policyHeader
     * @param inputRecord
     * @param selectedRecords
     * @return transactionEffectiveDate
     */
    protected String getTransactionEffectiveDate(PolicyHeader policyHeader, Record inputRecord, RecordSet selectedRecords) {
        Logger l = LogUtils.enterLog(getClass(), "getTransactionEffectiveDate", new Object[]{policyHeader, inputRecord});

        String transactionEffectiveDate = null;
        //get process code from reuquest, the value is ACCEPT/DECLINE/UPDATE/CANCEL/REACTIVE/REINSTATE/ADJ_LIMIT
        TailProcessCode tailProcessCode = TailProcessCode.getInstance(TailFields.getProcessCode(inputRecord));

        if (tailProcessCode.isReinstate()) {
            Iterator selTailRecIter = selectedRecords.getRecords();
            Date earliestExpDate = null;
            while (selTailRecIter.hasNext()) {
                Record tailRec = (Record) selTailRecIter.next();
                Date tailExpDate = DateUtils.parseDate(TailFields.getEffectiveToDate(tailRec));
                if (earliestExpDate == null || earliestExpDate.after(tailExpDate)) {
                    earliestExpDate = tailExpDate;
                }
            }
            transactionEffectiveDate = DateUtils.formatDate(earliestExpDate);
        }
        else if (tailProcessCode.isCancel()) {
            transactionEffectiveDate = CancelProcessFields.getCancellationDate(inputRecord);
        }
        else {
            Record paramRec = getParams(selectedRecords);
            inputRecord.setFields(paramRec);
            transactionEffectiveDate = getTailDAO().getTailTransactionEffectiveDate(inputRecord);

            if (transactionEffectiveDate.equals("01/01/1990")) {
                MessageManager.getInstance().addErrorMessage("pm.maintainTail.invalidTransactionEffectiveError");
                throw new ValidationException("Unable to process tail. Selected coverage's have different effective dates.");
            }
        }

        l.exiting(getClass().getName(), "getTransactionEffectiveDate", transactionEffectiveDate);
        return transactionEffectiveDate;
    }

    /**
     * get the default accouting date
     *
     * @param selectedRecords
     * @return default accounting date
     */
    protected String getDefaultAccountingDate(RecordSet selectedRecords) {
        Logger l = LogUtils.enterLog(getClass(), "getDefaultAccountingDate", new Object[]{selectedRecords});

        // For issue 101464,the Use Case has beed updated.
        // System should default the maximum of accounting date and SYSDATE to new accounting date.
        String accountingDate;
        Date maximumDate = null;
        Iterator tailIter = selectedRecords.getRecords();
        while (tailIter.hasNext()) {
            Record tailRec = (Record) tailIter.next();
            Date tailAccountDate = DateUtils.parseDate(TailFields.getTailAccountingFromDate(tailRec));
            if (maximumDate == null || tailAccountDate.after(maximumDate)) {
                maximumDate = tailAccountDate;
            }
        }
        accountingDate = DateUtils.formatDate(maximumDate);

        l.exiting(getClass().getName(), "getDefaultAccountingDate", accountingDate);
        return accountingDate;
    }

    /**
     * check if capture finance percentage is required
     *
     * @param policyHeader
     * @return policy type configured parameter
     */
    public String isCaptureFinancePercentageRequired(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "isCaptureFinancePercentageRequired", policyHeader);

        String isCaptureFinancePercentageRequired = SysParmProvider.getInstance().getSysParm(SysParmIds.FM_TAIL_FIN_CHARGE);

        l.exiting(getClass().getName(), "isCaptureFinancePercentageRequired",
            isCaptureFinancePercentageRequired);
        return isCaptureFinancePercentageRequired;
    }

    /**
     * get recordMode according to screenMode
     *
     * @param policyHeader
     * @return recordMode
     */
    protected TailRecordMode getTailRecordMode(PolicyHeader policyHeader) {
        TailRecordMode recordMode;

        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        if (screenMode.isCancelWIP() || screenMode.isRenewWIP() || screenMode.isResinstateWIP()) {
            recordMode = TailRecordMode.TEMP;
        }
        else if (screenMode.isWIP() || screenMode.isManualEntry()) {
            recordMode = TailRecordMode.TEMP;
        }
        else if (screenMode.isViewEndquote()) {
            recordMode = TailRecordMode.ENDQUOTE;
        }
        else {
            recordMode = TailRecordMode.OFFICIAL;
        }

        return recordMode;
    }

    /**
     * get currentScreenMode according to screenMode
     *
     * @param policyHeader
     * @return currentScreenMode
     */
    protected TailScreenMode getTailScreenMode(PolicyHeader policyHeader) {
        TailScreenMode currentScreenMode = null;
        TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();

        //When policy has any active endorsement quote or renewal quote on current policy or policy has a WIP transaction,
        //we need to disable such as Accept/Decline/Cancel and so on buttons.
        Record record = null;
        RecordSet termList = getPolicyManager().loadPolicyTermList(policyHeader.getPolicyId());
        for (int i = 0; i < termList.getSize(); i++) {
            record = termList.getRecord(i);
            String policyTermHistoryId = TailFields.getPolicyTermHistoryId(record);
            Term termRecord = policyHeader.getPolicyTerm(policyTermHistoryId);
            boolean isWipAvailable = termRecord.isWipExists();
            boolean isRenewalQuoteAailable = termRecord.isRenewalQuoteExists();
            boolean isEndQuoteAailable = termRecord.isEndorsementQuoteExists();
            if (isRenewalQuoteAailable||isEndQuoteAailable||(policyHeader.getScreenModeCode().isViewPolicy() && isWipAvailable)) {
                currentScreenMode = TailScreenMode.VIEW_ONLY;
                return currentScreenMode;
            }
        }

        // If the current term is not the latest term and the policy is in progress,
        // and is not tail cancellation/tail update, set tail screen mode to WIP.
        if (!policyHeader.isLastTerm() && policyHeader.isWipB() &&
                !transactionCode.isTailCancel() && !transactionCode.isTailEndorse()) {
            currentScreenMode = TailScreenMode.WIP;
        }
        else {
            ScreenModeCode screenMode = policyHeader.getScreenModeCode();
            if (screenMode.isCancelWIP() || screenMode.isRenewWIP()) {
                currentScreenMode = TailScreenMode.WIP;
            }
            else if (screenMode.isWIP() || screenMode.isManualEntry()) {
                if (transactionCode.isTailCancel() || transactionCode.isTailEndorse()) {
                    currentScreenMode = TailScreenMode.UPDATE;
                }
                else {
                    currentScreenMode = TailScreenMode.WIP;
                }
            }
            else if (screenMode.isViewPolicy()) {
                if (!StringUtils.isBlank(policyHeader.getPolicyIdentifier().getPolicyLockMessage())) {
                    currentScreenMode = TailScreenMode.VIEW_ONLY;
                }
                else {
                    currentScreenMode = TailScreenMode.UPDATABLE;
                }
            }
            else if (screenMode.isViewEndquote()) {
                currentScreenMode = TailScreenMode.VIEW_ONLY;
            }
            else {
                currentScreenMode = TailScreenMode.VIEW_ONLY;
            }
        }

        return currentScreenMode;

    }

    /**
     * method to get the initial value when capture tail charge page
     *
     * @return record
     */
    public Record getInitialValuesForTailCharge(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForTailCharge", new Object[]{policyHeader, inputRecord});

        // get the default values from the workbench configuration for this page
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(CAPTURE_FINANCE_PERCENT_ACTION_CLASS_NAME);
        // Get the current rate.
        TransactionFields.setLastTransactionId(inputRecord, policyHeader.getLastTransactionId());
        float currentRate = getTailDAO().getCurrentRate(inputRecord);
        // Format the returned rate percent(Don't format the default value).
        if (currentRate > 0f) {
            BigDecimal ratePercent = FormatUtils.parseBigDecimal(currentRate, 3);
            defaultValuesRecord.setFieldValue("ratePercent", ratePercent);
        }

        l.exiting(getClass().toString(), "getInitialValuesForTailCharge", defaultValuesRecord);
        return defaultValuesRecord;
    }

    /**
     * save selected ids in cahced inputRecord for next request
     *
     * @param selectedRecords
     * @param inputRecord
     */
    private void saveSelectedIds(RecordSet selectedRecords, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveSelectedIds", new Object[]{selectedRecords, inputRecord});
        }

        // if is cancel tail or update tail, save selected ids in inputrecord, only those records will be displayed for wip mode
        //get process code from request, the value is ACCEPT/DECLINE/UPDATE/CANCEL/REACTIVE/REINSTATE/ADJ_LIMIT
        TailProcessCode tailProcessCode = TailProcessCode.getInstance(TailFields.getProcessCode(inputRecord));

        StringBuffer selectedIdBuff = new StringBuffer();
        Iterator selectedRecordsIter = selectedRecords.getRecords();
        while (selectedRecordsIter.hasNext()) {
            Record selectedRec = (Record) selectedRecordsIter.next();
            selectedIdBuff.append(TailFields.getTailCovBaseRecordId(selectedRec)).append("^");
        }
        //set in the cached inputRecord for next request
        inputRecord.setFieldValue("selectedIds", selectedIdBuff.toString());


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveSelectedIds");
        }
    }


    /**
     * load all manual tail coverages for adding new tail coverage
     *
     * @param policyHeader policy header
     * @param inputRecord  input record contains parent coverage info
     * @return recordset of available tails for adding
     */
    public RecordSet loadAllManualTail(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllManualTail", new Object[]{policyHeader, inputRecord});
        }
        //set effDate based on screen mode code
        inputRecord.setFields(policyHeader.toRecord(), false);
        if (policyHeader.getScreenModeCode().isCancelWIP()) {
            inputRecord.setFieldValue("effDate", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }
        else {
            inputRecord.setFieldValue("effDate", policyHeader.getTermEffectiveToDate());
        }

        //prepare record load processor
        RecordLoadProcessor lp =
            RecordLoadProcessorChainManager.getRecordLoadProcessor(new TailSelectEntitlementRecordLoadProcessor(),
                new AddSelectIndLoadProcessor());

        RecordSet rs = getTailDAO().loadAllManualTail(inputRecord, lp);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllManualTail", rs);
        }

        return rs;
    }

    /**
     * add manual tail coverage
     *
     * @param policyHeader policy header
     * @param inputRecord input record contains required infos for add manual tail coverage
     */
    public void addManualTail(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addManualTail", new Object[]{policyHeader, inputRecord});
        }
        //validate add manual tail coverage
        validateAddManualTail(policyHeader, inputRecord);
       
        String effDate = getTailDAO().getParentEffecitveDate(inputRecord);
        inputRecord.setFieldValue("effDate", effDate);
        getTailDAO().addManualTail(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addManualTail");
        }
    }

    /**
     * validate add manual tail coverage
     *
     * @param policyHeader policy header
     * @param inputRecord input record contains required infos for add manual tail coverage
     */
    protected void validateAddManualTail(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAddManualTail", new Object[]{policyHeader, inputRecord});
        }

        Record polRec = policyHeader.toRecord();
        TailFields.setTermBaseRecordId(inputRecord, TailFields.getTermBaseRecordId(polRec));
        TransactionFields.setTransactionLogId(inputRecord, TransactionFields.getTransactionLogId(polRec));

        //validate if same type of tail already exists
        int existTailCount = getTailDAO().getTailCount(inputRecord);
        if (existTailCount > 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainTail.tailExistError");
            throw new ValidationException("invalid to add manual tail");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAddManualTail");
        }
    }

    /**
     * save tail finance charge
     *
     * @param policyHeader policy header
     * @param inputRecord input record contains the tail coverages that were created by current transaction
     *
     */
    public void saveTailCharge(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveTailCharge", new Object[]{policyHeader, inputRecord});
        }

        if (isCaptureFinancePercentageRequired(policyHeader).equals("B") &&
            TailFields.hasRatePercent(inputRecord) && !TailFields.getRatePercent(inputRecord).equals("0")) {
            RecordFilter selectedRecordFilter = new RecordFilter(TailFields.TAIL_RECORD_MODE_CODE, "TEMP");
            RecordSet selectedRecords = inputRecords.getSubSet(selectedRecordFilter);
            Iterator selectedRecordIter = selectedRecords.getRecords();

            while (selectedRecordIter.hasNext()) {
                Record selRec = (Record) selectedRecordIter.next();
                selRec.setFields(policyHeader.toRecord(), false);
                selRec.setFields(inputRecord, false);
                getTailDAO().saveTailCharge(selRec);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveTailCharge");
        }
    }

    /**
     * method to get the term data for the minimum term that the coverage belongs out of all the selected coverages
     *
     * @param policyHeader
     * @param selectedRecords
     * @return Record
     */
    private Record getMinimumTermData(PolicyHeader policyHeader, RecordSet selectedRecords) {
        Logger l = LogUtils.enterLog(getClass(), "getMinimumTermData", new Object[]{policyHeader});

        Record returnRecord = new Record(); 
        TransactionCode lastTranCode = policyHeader.getLastTransactionInfo().getTransactionCode();

        if (lastTranCode.isTailCancel() && YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_TAIL_CXL_ANY_TERM, "N")).booleanValue()) {

            RecordSet sortedSelectedRecords = selectedRecords.getSortedCopy(new Comparator() {
                public int compare(Object o1, Object o2) {
                    Record s1 = (Record) o1;
                    Record s2 = (Record) o2;
                    Long termBaseRecordId1 = s1.getLongValue(TailFields.TERM_BASE_RECORD_ID);
                    Long termBaseRecordId2 = s2.getLongValue(TailFields.TERM_BASE_RECORD_ID);
                    return termBaseRecordId1.compareTo(termBaseRecordId2);
                }
            }
            );

            Record minimumTermRecord = sortedSelectedRecords.getRecord(0);
            String minimumTermBaseRecordId = TailFields.getTermBaseRecordId(minimumTermRecord);

            Iterator termList = policyHeader.getPolicyTerms();
            Term term = null;
            while (termList.hasNext()) {
                term = (Term) termList.next();
                if (minimumTermBaseRecordId.equals(term.getTermBaseRecordId())) {
                    TailFields.setPolicyTermHistoryId(returnRecord, term.getPolicyTermHistoryId());
                    TailFields.setTermEffectiveFromDate(returnRecord, term.getEffectiveFromDate());
                    TailFields.setTermEffectiveToDate(returnRecord, term.getEffectiveToDate());
                    break;
                }

            }
        }
        else {
            TailFields.setPolicyTermHistoryId(returnRecord, policyHeader.getPolicyTermHistoryId());
            TailFields.setTermEffectiveFromDate(returnRecord, policyHeader.getTermEffectiveFromDate());
            TailFields.setTermEffectiveToDate(returnRecord, policyHeader.getTermEffectiveToDate());
        }

        l.exiting(getClass().toString(), "getMinimumTermData", returnRecord);
        return returnRecord;
    }

    public void verifyConfig() {
        if (getTailDAO() == null)
            throw new ConfigurationException("The required property 'tailDao' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
        if (getCoverageManager() == null)
            throw new ConfigurationException("The required property 'coverageManager' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public TailDAO getTailDAO() {
        return m_TailDAO;
    }

    public void setTailDAO(TailDAO tailDAO) {
        m_TailDAO = tailDAO;
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public CancelProcessManager getCancelProcessManager() {
        return m_cancelProcessManager;
    }

    public void setCancelProcessManager(CancelProcessManager cancelProcessManager) {
        m_cancelProcessManager = cancelProcessManager;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    protected static final String RQ_PARM_IS_RATE_TAIL = "rateTail";


    private TailDAO m_TailDAO;
    private PolicyManager m_policyManager;
    private CancelProcessManager m_cancelProcessManager;
    private TransactionManager m_transactionManager;
    private CoverageManager m_coverageManager;
    private ComponentManager m_componentManager;
    private LockManager m_lockManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private DBUtilityManager m_dbUtilityManager;

    private static final String SAVE_PROCESSOR = "TailManager";
    private static final String CAPTURE_FINANCE_PERCENT_ACTION_CLASS_NAME = "dti.pm.tailmgr.struts.CaptureFinancePercentAction";
    private static final String RATE_TRANSACTION_PROCESS = "RateTransactionWorkflow";
    private static final String RATE_TRANSACTION_INITIAL_STATE = "invokeRateTransacction";
}
