package dti.pm.transactionmgr.cancelprocessmgr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.filter.Filter;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.componentmgr.dao.ComponentDAO;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policyattributesmgr.PolicyAttributesFactory;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskManager;
import dti.pm.riskmgr.RiskRelationFields;
import dti.pm.riskmgr.coimgr.CoiFields;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.TransactionXrefFields;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessManager;
import dti.pm.transactionmgr.cancelprocessmgr.dao.CancelProcessDAO;
import dti.pm.transactionmgr.reinstateprocessmgr.ReinstateProcessFields;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of CancelProcessManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 4, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/31/2008       yhyang      #87658 Modify performMultiCancellation():
 *                                     Only when a policy risk,coverage or class is cancelled flat,
 *                                     should tail be attached to the prior term.
 * 11/06/2008       yhyang      #87658 Add isTailCreatedForPriorTerm() to check if the tail created for the prior term.
 * 12/09/2008       sxm         Issue 89002 - Added handling of sysparm PM_AUTO_CREATE_TAIL
 * 02/18/2009       yhyang      #84592 Add all selected risk ids to inputRecord for processing amalgamation.
 * 05/05/2010       bhong       107165 - Defined "isToCreateTail" in "isCreateTailRequied" method to correct logic error.
 *                              Also prevent it populating tail data if "isCreateTailRequired" returns false.
 * 06/24/2010	    gxc         108863 - Modified validatePerformCancellation to consolidate the logic in isCreateTailRequired
 *                              and fix bug with not generating tail when PM_AUTO_CREATE_TAIL sysparm is not set.
 * 06/25/2010       wtian       issue#109058 Added a validate message for "Cancellation Method" in "validateCancellationDetail()".
 * 06/29/2010	    gxc		108863 - Remove isCreateTailRequied function as it is not being used
 * 08/18/2010       syang       Issue 109058 - Modified validateCancellationDetail() to exclude
 *                              the "Cancellation Method" validation for Tail Cancellation. 
 * 08/18/2010       syang       Issue 110012 - Modified performCancellation() to rate policy after canceled successfully.
 * 09/01/2010       syang       Issue 111417 - Modified performCancellation() and performMultiCancellation() to change
 *                              the workflow of cancellation tail workflow. We should include the rating and viewing
 *                              related policy in the workflow definition rather than in java.
 * 01/11/2011       ryzhao      Issue 113558 - Add new carrier validation and isNewCarrierAvailable flag.
 * 01/14/2011       ryzhao      Issue 113558 - Construct parms field before performing cancellation.
 *                                             Do other adjustment per code review comments.
 * 01/17/2011       ryzhao      Issue 113558 - Add new carrier existence validation before set parms field.
 *                                             Move CANCEL_TRANS_CODE_CANCEL and CANCEL_TRANS_CODE_RISKCANCEL constants to CancelProcessFields class.
 * 01/12/2011       syang       105832 - Added loadAllActiveRiskForCancellation(), modified loadAllCancelableItem()
 *                              and performCancellation() to handle discipline decline list.
 * 02/16/2011       gxc         117067 - Modified purgePolicy to pass the current purge transaction log ID in call
 *                              to rating
 * 03/17/2011       wfu         118437 - Modified saveAllDisciplineDeclineEntity to save ddl method in policy level cancel.
 * 03/31/2011       ryzhao      117767 - Modified getTransactionCodeForMultiCancel to set effectiveFromDate with cancellation date.
 * 03/30/2011       dzhang      94232 - Modified validatePerformCancellation, Tail will never be created when cancelling a
 *                              risk when the cancellation type of 'IBNR' is selected.
 * 07/22/2011       wfu         122754 - Modified performCancellation to add CANIBNR type condition for IBNR risk cancellation.
 * 08/30/2011       ryzhao      124458 - Modified validateCancellationDetail to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 08/18/2011       syang       121201 - Changed the current process flow of multi cancellation for risk/coverage/coverage class/component.
 *                                       1) Added loadAllMultiCancelConfirmation(), processMultiCancelConfirmation() and validateMultiCancelConfirmation().
 *                                       2) Modified the existing logic of performMultiCancellation() and validatePerformMultiCancellation().
 * 10/19/2011       wfu         125007 - Added logic to catch policy locking or policy picture changing 
 *                                       validation exception for correct error message displaying.
 * 11/09/2011       syang       125724 - Modified performMultiCancellation() to not override the record. 
 * 11/11/2011       syang       127136 - Modified performMultiCancellation() to handle COI cancellation.
 * 12/30/2011       syang       128248 - Modified performMultiCancellation() to complete COI cancellation transaction. 
 * 02/28/2012       xnie        130244 - Modified performMultiCancellation() to throw new AppException when perform
 *                                       multiple cancellation failed.  
 * 03/21/2012       xnie        130643 - a) Added flatCancelPolicy() to call performFlactCancel() which handles flat
 *                                          cancel policy logic.
 *                                       b) Modified purgePolicy() to call performFlactCancel().
 *                                       c) Added performFlactCancel() which is called by flatCancelPolicy() and purgePolicy().
 * 05/02/2012       jshen       132521 - Modified performCancellation() to set newEndorsementCode to null when cancelling the risk relation.
 * 05/18/2012       xnie        132231 - Modified performMultiCancellation() to add a cancel level check: when cancel
 *                                       level is COI, system does output.
 * 05/23/2012       xnie        132862 - Modified validateMultiCancelConfirmation() to use getRiskName to replace
 *                                       getRiskNameDisplay to get non-null risk name.
 * 07/13/2012       jshen       135467 - Modified validatePerformMultiCancellation() method to not validate amalgamation
 *                                       when cancelling the COI.
 * 08/16/2012       wfu         136591 - Modified loadAllMultiCancelConfirmation to distinguish different cancel comments.
 * 08/29/2011       ryzhao      133360 - Modified validatePerformMultiCancellation() to change the validate owner logic.
 * 10/29/2012       tcheng      138675 - Modified loadAllMultiCancelConfirmation() to fix multiple cancel problem that 
 *                                       system tried to use the policyHeader's risk information to cancel the first
 *                                       selected risk/coverage/component which was incorrect.
 * 03/11/2013       tcheng      141856 - Modified validatePerformMultiCancellation to set termBaseRecordId for validating coverage class.
 * 06/21/2013       adeng       117011 - Modified loadAllMultiCancelConfirmation() and processMultiCancelConfirmation()
 *                                       for the new filed "transactionComment2".
 * 03/18/2014       awu         153022 - Modified performCancellation, Don't create transaction when cancel slot risk in renewal WIP.
 * 05/09/2014       awu         152675 - Modified validatePerformMultiCancellation to add backend coverage level validation.
 * 06/13/2014       jyang2      149970 - 1.Modified getInitialValuesForCancellation to get cancelled details from input
 *                                       ,generate cancel description and set into output to show on cancel screen.
 *                                       2.Added param riskManager and set/get method to get addCode for risks.
 * 07/15/2014       wdang       154953 - 1) Created cancelAllComponent() to handle Multi Cancel components: 
 *                                          delete TEMP components first, expire the remaining OFFICIAL components after that. 
 *                                       2) Modified performMultiCancellation() to perform Multi Cancel Component 
 *                                          with the current transaction during renewal WIP.
 *                                       3) Deleted seledCompRs.setFieldsOnAll(inputRecord, false) before cancelAllComponent(), 
 *                                          because some field with NULL value is wrongly overwrote due to this method. Also, 
 *                                          few fields of seledCompRs are used subsequently and they are already filled correctly.
 * 08/21/2014       jyang       156829 - Updated performMultiCancellation method, removed useless code, because no amalgamation
 *                                       could be done when multi-cancel COI holder.
 * 10/24/2014       kxiang      158125 - Modified validatePerformMultiCancellation to add logic to set value to status.
 * 03/18/2016       eyin        169939 - Modified loadAllCancelableItem(), change code to distinguish cancel and
 *                                       multiple cancel when show error message.
 * 05/17/2016       wdang       176804 - Removed isNewCarrierAvailable and added carrierB.
 * 06/08/2016       fcb         177372 - Changed int to long
 * 07/04/2016       eyin        176476 - Modified validatePerformCancellation() and validatePerformMultiCancellation(), put
 *                                       the future cancellation recordSet into user session if future cancellation exists.
 * 08/26/2016       wdang       167534 - Delete WIP Automatically when processing cancellation if transactionXref exists.
 * 08/28/2017       lzhang      187753 - Modified performCancellation: do not save official under renewal WIP
 *                                       for slot cancellation
 * ---------------------------------------------------
 */

public class CancelProcessManagerImpl implements CancelProcessManager {

    /**
     * set initial values for cancellation popup
     *
     * @param policyHeader the summary information for the policy being reissued
     * @param inputRecord  an input record from request prior to get the initial values
     * @return record containing initial values for cancellation
     */

    public Record getInitialValuesForCancellation(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForCancellation", new Object[]{policyHeader, inputRecord});

        Record outputRecord = policyHeader.toRecord();


        String cancelLevel = CancelProcessFields.getCancellationLevel(inputRecord);
        if (cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_SLOT)) {
            // allow only field cancel occupant slot available
            outputRecord.setFieldValue(IS_CANCEL_ADD_OCCUPANT_AVAILABLE, YesNoFlag.Y);
            outputRecord.setFieldValue(IS_CANCEL_TYPE_AVAILABLE, YesNoFlag.N);
            outputRecord.setFieldValue(IS_CANCEL_REASON_AVAILABLE, YesNoFlag.N);
            outputRecord.setFieldValue(IS_CANCEL_METHOD_AVAILABLE, YesNoFlag.N);
        }
        else if (cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_TAIL)) {
            outputRecord.setFieldValue(IS_CANCEL_ADD_OCCUPANT_AVAILABLE, YesNoFlag.N);
            outputRecord.setFieldValue(IS_CANCEL_TYPE_AVAILABLE, YesNoFlag.Y);
            outputRecord.setFieldValue(IS_CANCEL_REASON_AVAILABLE, YesNoFlag.Y);
            outputRecord.setFieldValue(IS_CANCEL_METHOD_AVAILABLE, YesNoFlag.N);
        }
        else if (cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_RISKREL)) {
            outputRecord.setFieldValue(IS_CANCEL_ADD_OCCUPANT_AVAILABLE, YesNoFlag.N);
            outputRecord.setFieldValue(IS_CANCEL_TYPE_AVAILABLE, YesNoFlag.N);
            outputRecord.setFieldValue(IS_CANCEL_REASON_AVAILABLE, YesNoFlag.N);
            outputRecord.setFieldValue(IS_CANCEL_METHOD_AVAILABLE, YesNoFlag.N);
        }
        else if (cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_EMPLOYEE)) {
            outputRecord.setFieldValue(IS_CANCEL_ADD_OCCUPANT_AVAILABLE, YesNoFlag.N);
            outputRecord.setFieldValue(IS_CANCEL_TYPE_AVAILABLE, YesNoFlag.N);
            outputRecord.setFieldValue(IS_CANCEL_REASON_AVAILABLE, YesNoFlag.N);
            outputRecord.setFieldValue(IS_CANCEL_METHOD_AVAILABLE, YesNoFlag.N);
        }
        else {
            outputRecord.setFieldValue(IS_CANCEL_ADD_OCCUPANT_AVAILABLE, YesNoFlag.N);
            outputRecord.setFieldValue(IS_CANCEL_TYPE_AVAILABLE, YesNoFlag.Y);
            outputRecord.setFieldValue(IS_CANCEL_REASON_AVAILABLE, YesNoFlag.Y);
            outputRecord.setFieldValue(IS_CANCEL_METHOD_AVAILABLE, YesNoFlag.Y);
        }

        //For issue 113558, add new carrier flag
        if (cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_POLICY) || cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_RISK)) {
            //set transCode according to cancel level
            if (cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_POLICY)) {
                CancelProcessFields.setTransCode(inputRecord, CancelProcessFields.CancelTransCodeValues.CANCEL);
            }
            else {
                CancelProcessFields.setTransCode(inputRecord, CancelProcessFields.CancelTransCodeValues.RISKCANCEL);
            }
            CancelProcessFields.setCarrierB(outputRecord, getCancelProcessDAO().isNewCarrierEnabled(inputRecord));
        }
        else {
            CancelProcessFields.setCarrierB(outputRecord, YesNoFlag.N);
        }

        //set risk ,coverage, coverage class name to cancel description to cancelDesc field.
        if (cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_RISK)) {
            String addCode = getRiskManager().getAddCodeForRisk(inputRecord);
            if (!StringUtils.isBlank(addCode) && addCode.equals(CANCEL_LEVEL_SLOT)) {
                CancelProcessFields.setCancelDesc(outputRecord, CANCEL_LEVEL_SLOT.substring(0, 1) + CANCEL_LEVEL_SLOT.substring(1).toLowerCase()
                    + " - " + CancelProcessFields.getCancelDesc(inputRecord));
            }
            else {
                CancelProcessFields.setCancelDesc(outputRecord, cancelLevel.substring(0, 1) + cancelLevel.substring(1).toLowerCase()
                    + " - " + CancelProcessFields.getCancelDesc(inputRecord));
            }
        }
        else if (cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_SLOT) || cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_COVERAGE) || cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_CLASS)) {
            CancelProcessFields.setCancelDesc(outputRecord, cancelLevel.substring(0, 1) + cancelLevel.substring(1).toLowerCase()
                + " - " + CancelProcessFields.getCancelDesc(inputRecord));
        }
        // For issue 101464,
        // The Process Cancellation UC was not very clear about the accounting date default logic.
        // The correct requirements for all level of cancellation (including tail) are:
        // 1. If the system parameter PM_CHK_ACCT_DATE is NULL or 'N', use TODAY as default for all cancellations;
        // 2. otherwise -
        //    a) for tail cancellation, use the max of tail coverage accounting date and TODAY as default
        //    b) for other cancellations use max of all transaction accounting date of the term
        String checkAccountingDate = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_CHK_ACCT_DATE, "N");
        Date currentDate = new Date();
        String accountingDate = inputRecord.getStringValue(CancelProcessFields.ACCOUNTING_DATE, "");
        if (!YesNoFlag.getInstance(checkAccountingDate).booleanValue()) {
            // get the value per UC requirements, it is set to be today
            accountingDate = DateUtils.formatDate(currentDate);
        }
        else {
            if (cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_TAIL)) {
                // For Tail cancellation, the accountingDate is returned from TailManagerImpl.getDefaultAccountingDate().
                if (StringUtils.isBlank(accountingDate) || DateUtils.parseDate(accountingDate).before(currentDate)) {
                    accountingDate = DateUtils.formatDate(currentDate);
                }
            }
            else {
                accountingDate = getTransactionManager().getMaxAccountingDate(inputRecord);
            }
        }
        CancelProcessFields.setAccountingDate(outputRecord, accountingDate);
        //Initialize cancel effective date to term effective date if cancel during renewal WIP.
        if (policyHeader.isWipB() && policyHeader.getLastTransactionInfo().getTransactionCode().isRenewal()) {
            CancelProcessFields.setCancellationDate(outputRecord, policyHeader.getTermEffectiveFromDate());
            outputRecord.setFieldValue("isCancelDateEditable", YesNoFlag.N);
        }
        else {
            outputRecord.setFieldValue("isCancelDateEditable", YesNoFlag.Y);
        }

        l.exiting(getClass().getName(), "getInitialValuesForCancellation", outputRecord);
        return outputRecord;
    }

    /**
     * process Cancellation
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     * @return record containing cancel parameter tailB
     */
    public Record performCancellation(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "performCancellation",
            new Object[]{policyHeader, inputRecord});

        //set field values
        inputRecord.setFields(policyHeader.toRecord(), true);

        //get cancel level from request parameter
        String cancelLevel = CancelProcessFields.getCancellationLevel(inputRecord);

        //validate the cancel process precondition
        validatePerformCancellation(policyHeader, inputRecord);
        Record outputRecord = null;

        if (cancelLevel.equals(CANCEL_LEVEL_RISKREL)) {
            TransactionCode transactionCode = TransactionCode.RRELCANCEL;
            TransactionFields.setNewAccountingDate(inputRecord, CancelProcessFields.getAccountingDate(inputRecord));
            TransactionFields.setNewTransactionComment(
                inputRecord, CancelProcessFields.getCancellationComments(inputRecord));
            String transactionEffectiveDate = CancelProcessFields.getCancellationDate(inputRecord);
            // Set newEndorsementCode to null
            TransactionFields.setNewEndorsementCode(inputRecord, null);
            //lock policy and create transaction
            Transaction transaction = getTransactionManager().createTransaction(
                policyHeader, inputRecord, transactionEffectiveDate, transactionCode, true);
            TransactionFields.setTransactionLogId(inputRecord, transaction.getTransactionLogId());

            // cancel risk relation
            outputRecord = getCancelProcessDAO().performRiskRelationCancellation(inputRecord);
            RiskFields.setOrigRiskEffectiveFromDate(outputRecord, RiskFields.getOrigRiskEffectiveFromDate(inputRecord));
            RiskFields.setRiskEffectiveFromDate(outputRecord, RiskFields.getRiskEffectiveFromDate(inputRecord));
            RiskFields.setRiskEffectiveToDate(outputRecord, RiskFields.getRiskEffectiveToDate(inputRecord));
            RiskRelationFields.setRiskCountyCode(outputRecord, RiskRelationFields.getRiskCountyCode(inputRecord));
            RiskRelationFields.setCurrentRiskTypeCode(outputRecord, RiskRelationFields.getCurrentRiskTypeCode(inputRecord));
            CancelProcessFields.setCancellationLevel(outputRecord, CANCEL_LEVEL_RISKREL);

            // get bypass rating count
            Record r = new Record();
            PolicyHeaderFields.setPolicyTypeCode(r, policyHeader.getPolicyTypeCode());
            YesNoFlag bypassRating = getTransactionManager().isSkipRating(r);
            // determine the first workflow step
            String initialState;
            if (bypassRating.booleanValue()) {
                initialState = "invokeProductNotifyMsg";
            }
            else {
                initialState = "invokeRateNotifyAndSaveAsOfficialDetail";
            }

            // Initialize the workflow
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                "SaveAsOfficialDetail",
                initialState);
        }
        else if (cancelLevel.equals(CANCEL_LEVEL_SLOT)) {
            boolean isAddOccupantSameDay = CancelProcessFields.getCancellationAddOccupant(inputRecord).booleanValue();
            TransactionCode transactionCode = TransactionCode.OCCPCANCEL;
            String endorsementCode = isAddOccupantSameDay ? "ADDOCCUP" : "";
            TransactionFields.setNewAccountingDate(inputRecord, CancelProcessFields.getAccountingDate(inputRecord));
            TransactionFields.setNewTransactionComment(
                inputRecord, CancelProcessFields.getCancellationComments(inputRecord));
            TransactionFields.setNewEndorsementCode(inputRecord, endorsementCode);
            String transactionEffectiveDate = CancelProcessFields.getCancellationDate(inputRecord);
            ScreenModeCode screenMode = policyHeader.getScreenModeCode();
            boolean isRenewWIP = screenMode.isRenewWIP();
            //issue153022, Cancel Slot risk in Renewal WIP. Use renewal transaction id to cancel risk.
            if (!isRenewWIP) {
            //lock policy and create transaction
            Transaction transaction = getTransactionManager().createTransaction(
                policyHeader, inputRecord, transactionEffectiveDate, transactionCode, true);
            TransactionFields.setTransactionLogId(inputRecord, transaction.getTransactionLogId());
            }
            //cancel slot occupant
            outputRecord = getCancelProcessDAO().performSlotCancellation(inputRecord);

            if (!isRenewWIP){
                //save slot cancellation transaction as official
                getTransactionManager().processSaveOccupantCancellationAsOfficial(policyHeader, inputRecord);
            }

        }
        else if (cancelLevel.equals(CANCEL_LEVEL_EMPLOYEE)) {
            TransactionCode transactionCode = TransactionCode.EMPCANCEL;
            TransactionFields.setNewAccountingDate(inputRecord, CancelProcessFields.getAccountingDate(inputRecord));
            TransactionFields.setNewTransactionComment(
                inputRecord, CancelProcessFields.getCancellationComments(inputRecord));
            TransactionFields.setNewEndorsementCode(inputRecord, null);
            String transactionEffectiveDate = CancelProcessFields.getCancellationDate(inputRecord);
            //lock policy and create transaction
            Transaction transaction = getTransactionManager().createTransaction(
                policyHeader, inputRecord, transactionEffectiveDate, transactionCode, true);
            TransactionFields.setTransactionLogId(inputRecord, transaction.getTransactionLogId());
            //cancel VL Employee
            getCancelProcessDAO().cancelVLEmployee(inputRecord);
            //save as official
            getTransactionManager().processSaveVLEmployeeCancellationAsOfficial(policyHeader, inputRecord);
        }
        else {
            TransactionCode transactionCode = getTransactionCode(policyHeader, cancelLevel);
            Record renewalRecord = null;
            if (policyHeader.isWipB()
                && policyHeader.getWipTransCode() != null
                && transactionCode != null
                && PolicyAttributesFactory.getInstance().isAutoPendingRenewalEnable(
                policyHeader.getTermEffectiveFromDate(),
                transactionCode,
                TransactionCode.getInstance(policyHeader.getWipTransCode()))) {
                renewalRecord = getTransactionManager().performDeleteRenewalWIP(policyHeader);
                PolicyManager policyManager = ((PolicyManager) ApplicationContext.getInstance().getBean(PolicyManager.BEAN_NAME));
                policyHeader = policyManager.loadPolicyHeader(policyHeader.getPolicyNo(), getClass().getName(), "performCancellation: reload policy header");
            }

            //system create transaction
            //If the policy is already in renewal wip, then use the existing wip transaction for cancellaion process
            //else derive a cancel transaction code based on cancel level and create a new transaction
            ScreenModeCode screenMode = policyHeader.getScreenModeCode();
            String transactionId;
            if (screenMode.isRenewWIP()) {
                transactionId = policyHeader.getLastTransactionId();
            }
            else {
                transactionId = "0";
            }
            inputRecord.setFieldValue("transId", transactionId);

            //set tailB
            if (ConfirmationFields.isConfirmed("pm.maintainCancellation.confirm.createTail", inputRecord) ||
                   !inputRecord.hasStringValue("pm.maintainCancellation.confirm.createTail.confirmed")) {
                CancelProcessFields.setTailB(inputRecord, YesNoFlag.Y);
            }
            else {
                CancelProcessFields.setTailB(inputRecord, YesNoFlag.N);
            }

            //process cancellation
            inputRecord.setFields(policyHeader.toRecord(), false);
            // issue 113558: Construct parms field before performing cancellation
            // Set "^NEW_CARRIER_FK^<value here>^" for parms field, in which <value here> will be replaced by the selected New Carrier field value.
            if(inputRecord.hasStringValue(CancelProcessFields.CARRIER))    {
                CancelProcessFields.setParms(inputRecord,"^NEW_CARRIER_FK^"+CancelProcessFields.getCarrier(inputRecord)+"^");
            }
            else {
                CancelProcessFields.setParms(inputRecord,"");
            }
            outputRecord = getCancelProcessDAO().performCancellation(inputRecord);

            // If cancellation failed, display err message to user
            if (outputRecord.getLongValue("rc").longValue() < 0) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.processCancellation.error",
                    new String[]{outputRecord.getStringValue("rmsg")});
            }
            else {
                String policyNo = PolicyHeaderFields.getPolicyNo(inputRecord);
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (cancelLevel.equals(CANCEL_LEVEL_RISK)
                    && CancelProcessFields.getCancellationType(inputRecord).equals(CANCEL_TYPE_IBNR)
                    && CancelProcessFields.isIbnrRisk(inputRecord).booleanValue()) {
                    // Initialize the invoke notify workflow
                    wa.initializeWorkflow(policyNo, "ProcessCancellationTailWorkflow", "invokeDefaultIbnrDetailNotify");
                    wa.setWorkflowAttribute(policyNo, "riskId", inputRecord.getStringValue("baseId"));
                }
                else if (CancelProcessFields.getTailB(outputRecord).booleanValue()) {
                    // Initialize the invoke view tail workflow
                    wa.initializeWorkflow(policyNo, "ProcessCancellationTailWorkflow", "invokeViewTail");
                }
                else {
                    // Initialize the invoke rate transaction workflow
                    wa.initializeWorkflow(policyNo, "ProcessCancellationTailWorkflow", "invokeRateTransaction");
                }
                // This attribute is used to check whether it is in other workflow when open tail coverage page.
                wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), dti.pm.core.http.RequestIds.WORKFLOW_FOR, "invokeRating");

                // Save discipline decline entity
                TransactionFields.setTransactionLogId(inputRecord, outputRecord.getStringValue("rc"));
                inputRecord.setFieldValue("isMultiCancelB", YesNoFlag.N);
                saveAllDisciplineDeclineEntity(inputRecord, inputRecords);

                if (renewalRecord != null
                    && renewalRecord.hasFieldValue(TransactionFields.TRANSACTION_LOG_ID)) {
                    Record xrefRecord = new Record();
                    xrefRecord.setFieldValue(TransactionXrefFields.ORIGINAL_TRANS_ID,
                        TransactionFields.getTransactionLogId(renewalRecord));
                    xrefRecord.setFieldValue(TransactionXrefFields.RELATED_TRANS_ID,
                        TransactionFields.getTransactionLogId(inputRecord));
                    xrefRecord.setFieldValue(TransactionXrefFields.XREF_TYPE, TransactionXrefFields.AUTO_PENDING_RENEWAL);
                    getTransactionManager().createTransactionXref(xrefRecord);
                }
            }
        }

        l.exiting(getClass().getName(), "performCancellation", outputRecord);
        return outputRecord;
    }


    /**
     * validate process cancellation
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void validatePerformCancellation(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validatePerformCancellation",
            new Object[]{policyHeader, inputRecord});

        inputRecord.setFields(policyHeader.toRecord(), false);

        String cancelLevel = CancelProcessFields.getCancellationLevel(inputRecord);

        RecordSet outRecs = new RecordSet();
        int cunt = outRecs.getSize();

        if (!cancelLevel.equals(CANCEL_LEVEL_TAIL) && !cancelLevel.equals(CANCEL_LEVEL_SLOT)
            && !cancelLevel.equals(CANCEL_LEVEL_RISKREL) && !cancelLevel.equals(CANCEL_LEVEL_EMPLOYEE)) {
            //#4 if is single active owner of an entity-type need to be confirmed by underwriter
            if (cancelLevel.equals(CANCEL_LEVEL_RISK)) {
                //set constant fields
                inputRecord.setFieldValue("inputStr", "CANCEL_OWNER");
                boolean isSoloOwner = getCancelProcessDAO().isSoloOwner(inputRecord);
                if (isSoloOwner && !ConfirmationFields.isConfirmed("pm.maintainCancellation.confirm.cancelSingleActiveOwner"
                    , inputRecord)) {
                    MessageManager.getInstance().addConfirmationPrompt("pm.maintainCancellation.confirm.cancelSingleActiveOwner");
                }
            }

            // 94232: Tail will never be created when cancelling an active IBNR risk when the cancellation type of "IBNR" is selected
            if (CancelProcessFields.getCancellationType(inputRecord).equals(CANCEL_TYPE_IBNR) && CancelProcessFields.isIbnrRisk(inputRecord).booleanValue()) {
                inputRecord.setFieldValue("pm.maintainCancellation.confirm.createTail.confirmed", "N");
            }
            else {
                //Check system parameter to see if we need to ask the user if they want to create tail
                //"Y" means don't ask, go ahead and create tail
                //"N" means to ask if they want tail depending on tail reasons config
                boolean isPopUpToAskAboutTailCreation = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_AUTO_CEEATE_TAIL, "Y")).booleanValue();

                if (!isPopUpToAskAboutTailCreation) {
                    boolean isTailToBeCreated = getCancelProcessDAO().isToCreateTail(inputRecord);

                    if (isTailToBeCreated) {
                        //Tail can be created. Determine if we need to ask the user whether to create tail
                        //if the cancel reason is one of the cancel reasons that should not generate tail, don't prompt
                        //if not, ask if tail should be generated.
                        if (!inputRecord.hasStringValue("pm.maintainCancellation.confirm.createTail.confirmed")) {
                            String sysNoTailReasons = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_NO_TAIL_REASONS);
                            String cancelReason = CancelProcessFields.getCancellationReason(inputRecord);

                            if (!StringUtils.isBlank(sysNoTailReasons) && sysNoTailReasons.indexOf(cancelReason) >= 0) {
                                inputRecord.setFieldValue("pm.maintainCancellation.confirm.createTail.confirmed", "N");
                            } else {
                                MessageManager.getInstance().addConfirmationPrompt("pm.maintainCancellation.confirm.createTail", false);
                            }
                        }
                    }
                }
            }

            //#6 database validate cancellation
            outRecs = getCancelProcessDAO().validateCancellation(inputRecord);
            if(outRecs.getSize() > 0){
                UserSessionManager.getInstance().getUserSession().set("futureCancellationDetailsGridDataBean", outRecs);
                RequestStorageManager.getInstance().set(CancelProcessFields.FUTURECANCELLATIONEXISTB, YesNoFlag.Y);
            }
            Record outRec = outRecs.getSummaryRecord();
            if (outRec.getStringValue("rc").equals("-1")) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.processCancellation.error",
                    new String[]{outRec.getStringValue("rmsg")});
            }
        }

        l.exiting(getClass().getName(), "validatePerformCancellation", Boolean.valueOf(
            MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts()));

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts())
            throw new ValidationException();
    }

    /**
     * validate process cancellation
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void validatePrePerformCancellation(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validatePrePerformCancellation",
            new Object[]{policyHeader, inputRecord});

        inputRecord.setFields(policyHeader.toRecord(), false);

        String cancelLevel = CancelProcessFields.getCancellationLevel(inputRecord);

        if (!cancelLevel.equals(CANCEL_LEVEL_TAIL) && !cancelLevel.equals(CANCEL_LEVEL_SLOT)
            && !cancelLevel.equals(CANCEL_LEVEL_RISKREL) && !cancelLevel.equals(CANCEL_LEVEL_EMPLOYEE)) {

            ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
            if (screenModeCode.isRenewWIP()) {
                Record outRec = getCancelProcessDAO().validateRenewalWipCancellation(inputRecord);
                if (!outRec.getStringValue("rc").equals("0")) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.renewalWIPChange.error",
                        new String[]{outRec.getStringValue("rmsg")});
                }
            }
        }

        l.exiting(getClass().getName(), "validatePrePerformCancellation", Boolean.valueOf(
            MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts()));

        if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts())
            throw new ValidationException();
    }

    /**
     * validate cancellation detail
     *
     * @param policyHeader
     * @param inputRecord  *
     */
    public void validateCancellationDetail(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCancellationDetail", new Object[]{policyHeader, inputRecord});
        }

        String cancelLevel = CancelProcessFields.getCancellationLevel(inputRecord);
        String cancelItemEffDate = CancelProcessFields.getCancelItemEffDate(inputRecord);
        String cancelItemExpDate = CancelProcessFields.getCancelItemExpDate(inputRecord);
        String cancellationDate = CancelProcessFields.getCancellationDate(inputRecord);
        String cancellationType = CancelProcessFields.getCancellationType(inputRecord);
        String cancellationReason = CancelProcessFields.getCancellationReason(inputRecord);
        String cancellationMethod = CancelProcessFields.getCancellationMethod(inputRecord);
        String cancelAccountingDate = CancelProcessFields.getAccountingDate(inputRecord);

        //#0 check required fields
        if (StringUtils.isBlank(cancellationDate)) {
            MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.noCancellationDate.error",
                CancelProcessFields.CANCELLATION_DATE);
        }

        // Validate amalgamate to policy No
        validateAmalgamation(inputRecord);

        if (cancelLevel.equals(CANCEL_LEVEL_SLOT)) {
            //slot validation
            if (!StringUtils.isBlank(cancellationDate)
                && cancelItemEffDate.equals(cancellationDate) && cancelItemExpDate.equals(cancellationDate)
                && DateUtils.parseDate(cancelItemExpDate).before(DateUtils.parseDate(policyHeader.getTermEffectiveToDate()))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.slotCancelled.error",
                    new String[]{FormatUtils.formatDateForDisplay(cancelItemEffDate)}, CancelProcessFields.CANCELLATION_DATE);
            }
        }
        else {
            if (!cancelLevel.equals(CANCEL_LEVEL_RISKREL) && !cancelLevel.equals(CANCEL_LEVEL_EMPLOYEE)) {
                if (StringUtils.isBlank(cancellationType)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.noCancellationType.error",
                        CancelProcessFields.CANCELLATION_TYPE);
                }
                if (StringUtils.isBlank(cancellationReason)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.noCancellationReason.error",
                        CancelProcessFields.CANCELLATION_REASON);
                }
                // This validation shouldn't be applied to Tail Cancellation since CancellationMethod is unavailable for Tail Cancellation.
                if (!cancelLevel.equals(CANCEL_LEVEL_TAIL) && StringUtils.isBlank(cancellationMethod)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.noCancellationMethod.error",
                        CancelProcessFields.CANCELLATION_METHOD);
                }
            }
        }

        if (StringUtils.isBlank(cancelAccountingDate)) {
            MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.noAccountingDate.error",
                CancelProcessFields.ACCOUNTING_DATE);
        }

        if (!cancelLevel.equals(CANCEL_LEVEL_TAIL)) {

            //#1 cancellation date cannot be before cancel item's effective date
            if (!StringUtils.isBlank(cancellationDate) && DateUtils.parseDate(cancellationDate).before(DateUtils.parseDate(cancelItemEffDate))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.cancelDateBeforeEffDate.error",
                    new String[]{cancelLevel.toLowerCase()}, CancelProcessFields.CANCELLATION_DATE);
            }
            //#2 cancellation date cannot be after cancel item's expiration date
            if (!StringUtils.isBlank(cancellationDate) && DateUtils.parseDate(cancellationDate).after(DateUtils.parseDate(cancelItemExpDate))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.cancelDateAfterExpDate.error",
                    new String[]{cancelLevel.toLowerCase()}, CancelProcessFields.CANCELLATION_DATE);
            }

            //#3 if cancel type is EXPIRED, cancellation date must be the same as the expiration date
            if (!StringUtils.isBlank(cancellationDate) && cancellationType.equals(CANCEL_TYPE_EXPIRED) &&
                DateUtils.parseDate(cancellationDate).compareTo(DateUtils.parseDate(cancelItemExpDate)) != 0) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.cancelDateForExpiredCancel.error",
                    new String[]{cancelLevel.toLowerCase()}, CancelProcessFields.CANCELLATION_DATE);
            }
        }
        else if (cancelLevel.equals(CANCEL_LEVEL_TAIL)) {
            String[] cancelValidationEffDates = cancelItemEffDate.split(",");
            String[] cancelValidationExpDates = cancelItemExpDate.split(",");

            for (int i = 0; i < cancelValidationEffDates.length; i++) {
                String tailEffDate = cancelValidationEffDates[i];
                String tailExpDate = cancelValidationExpDates[i];

                //#1 cancellation date cannot be before cancel item's effective date
                if (!StringUtils.isBlank(cancellationDate) && DateUtils.parseDate(cancellationDate).before(DateUtils.parseDate(tailEffDate))) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.tail.cancelDateBeforeEffDate.error",
                        new String[]{cancelLevel.toLowerCase(), FormatUtils.formatDateForDisplay(tailEffDate)}, CancelProcessFields.CANCELLATION_DATE);
                }
                //#2 cancellation date cannot be after cancel item's expiration date
                if (!StringUtils.isBlank(cancellationDate) && DateUtils.parseDate(cancellationDate).after(DateUtils.parseDate(tailExpDate))) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.tail.cancelDateAfterExpDate.error",
                        new String[]{cancelLevel.toLowerCase(), FormatUtils.formatDateForDisplay(tailExpDate)}, CancelProcessFields.CANCELLATION_DATE);
                }
            }
        }

        //issue#113558: Check if new carrier is selected if the cancel level is POLICY/RISK.
        if (cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_POLICY) || cancelLevel.equalsIgnoreCase(CANCEL_LEVEL_RISK)) {
            String carrier = CancelProcessFields.getCarrier(inputRecord);
            YesNoFlag carrierB = CancelProcessFields.getCarrierB(inputRecord);
            if (carrierB.booleanValue()) {
                if (StringUtils.isBlank(carrier)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.noNewCarrier.error",
                        CancelProcessFields.CARRIER);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCancellationDetail", Boolean.valueOf(
                MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts()));
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts())
            throw new ValidationException();
    }


    /**
     * load all cancelable items for multi cancel
     *
     * @param policyHeader policy header
     * @param inputRecord  input record
     * @return recordset of cancelable items
     */
    public RecordSet loadAllCancelableItem(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCancelableItem", new Object[]{policyHeader, inputRecord});
        }

        // Is it possible to lock down the policy? but do not lock it until we are about to createTransaction
        boolean canLockPolicy = getLockManager().canLockPolicy(policyHeader);
        if (!canLockPolicy) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error");
            throw new ValidationException();
        }

        //prepare load processor
        RecordLoadProcessor lp =
            new MultiCancelEntitlementRecordLoadProcessor(policyHeader, inputRecord);
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(lp, AddSelectIndLoadProcessor.getInstance());

        RecordSet rs;
        Record polRec = policyHeader.toRecord();
        if (inputRecord.hasStringValue(CancelProcessFields.CANCELLATION_LEVEL) &&
            CancelProcessFields.getCancellationLevel(inputRecord).equalsIgnoreCase(CANCEL_LEVEL_COI)) {
            //if the cancellation level is COI, then load cancelable COI holders
            rs = getCancelProcessDAO().loadAllCancelableCoi(polRec, lp);
        }
        else {
            //load cancellable risk/coverage/coverage class/component
            rs = getCancelProcessDAO().loadAllCancelableItem(polRec, lp);
            // Set three fields to risk recordSet
            rs.setFieldValueOnAll(CancelProcessFields.SELECT_TO_DDL, "");
            rs.setFieldValueOnAll(CancelProcessFields.DDL_REASON, "");
            rs.setFieldValueOnAll(CancelProcessFields.DDL_COMMENTS, "");
        }
        //if count of records is zero, add error message
        if (rs.getSize() == 0) {
            if(inputRecord.hasStringValue(CancelProcessFields.PROCESS) &&
                CancelProcessFields.getProcess(inputRecord).equalsIgnoreCase(CAPTURE_CANCELLATION_DETAIL)){
                //Show error Msg when Customer does Cancellation
                MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.noItem.msg");
            }else{
                //Show error Msg when Customer does Multi Cancellation
            MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noItem.msg");
        }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCancelableItem", rs);
        }

        return rs;
    }

    /**
     * Process Multi Cancel Component<p>
     * Delete TEMP components first, expire the remaining OFFICIAL components after that.
     *  
     * @param seledCompRs input selected components recordset.
     */
    private void cancelAllComponent(RecordSet seledCompRs){
        // Filter those components with COMP_REC_MODE_CODE is TEMP, delete them.
        RecordSet tempCompRs = seledCompRs.getSubSet(new RecordFilter(CancelProcessFields.COMP_REC_MODE_CODE, PolicyInquiryFields.TEMP));
        getComponentDAO().deleteAllComponents(tempCompRs);

        // Filter those components with COMP_REC_MODE_CODE is OFFICIAL or COMP_REC_MODE_CODE is TEMP and COMP_OFF_REC_ID is non-empty, expire them.     
        RecordSet tempCompRs1 = seledCompRs.getSubSet(new RecordFilter(CancelProcessFields.COMP_REC_MODE_CODE, PolicyInquiryFields.TEMP, 
                new RecordFilter(CancelProcessFields.COMP_OFF_REC_ID, true)));
        RecordSet tempCompRs2 = seledCompRs.getSubSet(new RecordFilter(CancelProcessFields.COMP_REC_MODE_CODE, PolicyInquiryFields.OFFICIAL));
        tempCompRs1.addRecords(tempCompRs2);
        getCancelProcessDAO().cancelAllComponent(tempCompRs1);
    }

    /**
     * process Multi Cancellation
     *
     * @param policyHeader policy header
     * @param inputRs      the cancelable items returned from client
     * @param inputRecord  with policy multiple cancel infos
     * @return record containing multi cancel result
     */
    public Record performMultiCancellation(PolicyHeader policyHeader, RecordSet inputRs, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performMultiCancellation",
                new Object[]{policyHeader, inputRs, inputRecord});
        }

        Record outputRecord = new Record();
        //set field values
        inputRecord.setFields(policyHeader.toRecord(), true);
        //get cancel level from request parameter
        String cancelLevl = CancelProcessFields.getCancellationLevel(inputRecord).toUpperCase();
        //indicate if cancel transaction has been created
        boolean isTransactionCreated = false;
        //validate the multi cancel process
        Record valResultRec;
        // The validatePerformMultiCancellation is only for COI,
        // because all the validations for risk/coverage/class/component has been processed already, it should be ignored here.
        if (CANCEL_LEVEL_COI.equals(cancelLevl)) {
            try {
                valResultRec = validatePerformMultiCancellation(policyHeader, inputRs, inputRecord);
            }
            catch (ValidationException ve) {
                throw ve;
            }
        }
        else {
            valResultRec = new Record();
            valResultRec.setFieldValue("status", "VALID");
        }
        String valStatus = CancelProcessFields.getStatus(valResultRec);
        if ("VALID".equals(valStatus)) {
            try {
                Transaction cancelTransaction = null;
                if (CANCEL_LEVEL_COMPONENT.equals(cancelLevl)
                        && policyHeader.getScreenModeCode().isRenewWIP()) {
                    // Retrieve transaction information
                    cancelTransaction = policyHeader.getLastTransactionInfo();
                    // Set transaction created flag to false
                    isTransactionCreated = false;
                }
                else {
                    //Lock the policy
                    getLockManager().lockPolicy(policyHeader, "performMultiCancellation: locking policy before multi cancel transaction.");

                    //Derive transaction code for Multi Cancel
                TransactionCode transCode = getTransactionCodeForMultiCancel(policyHeader, inputRecord);
                    cancelTransaction = getTransactionManager().createTransaction(
                    policyHeader, inputRecord,
                    CancelProcessFields.getCancellationDate(inputRecord), transCode);
                TransactionFields.setTransactionLogId(inputRecord, cancelTransaction.getTransactionLogId());
                    //Set cancel transaction info into policyHeader
                policyHeader.setLastTransactionInfo(cancelTransaction);

                    //Set transaction created flag to true
                isTransactionCreated = true;
                }

                //indicate if tail is created by multi cancel
                boolean isTailCreated = false;
                // 94232 - if it is false, it means system will create IBNR risk
                boolean createTail = true;

                RecordSet seledRiskRs;
                RecordSet seledCovgRs;
                RecordSet seledCompRs;
                RecordSet seledSubcovgRs;
                RecordSet seledCoiRs;
                if (CANCEL_LEVEL_RISK.equals(cancelLevl)) {
                    seledRiskRs =
                        inputRs.getSubSet(new RecordFilter(CancelProcessFields.RISK_NAME_DISPLAY, true)).
                            getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));

                    // 94232 - if any risk being cancelled is IBNR active risk and cancel type is IBNR, not create tail
                    Iterator seledRiskIter = seledRiskRs.getRecords();
                    while (seledRiskIter.hasNext()) {
                        Record riskRec = (Record) seledRiskIter.next();
                        if (YesNoFlag.getInstance(RiskFields.getRollingIbnrB(riskRec)).booleanValue()
                            && CancelProcessFields.getCancellationType(inputRecord).equals(CANCEL_TYPE_IBNR)) {
                            createTail = false;
                        }
                    }
                    if (!createTail) {
                        seledRiskRs.setFieldValueOnAll(CancelProcessFields.TAIL_CREATE_B, YesNoFlag.N);
                    }

                    // The selected risk base record ids.
                    StringBuffer seledRiskBaseIdBuff = new StringBuffer();
                    seledRiskIter = seledRiskRs.getRecords();
                    while (seledRiskIter.hasNext()) {
                        Record riskRec = (Record) seledRiskIter.next();
                        riskRec.setFields(inputRecord, false);
                        String riskBaseId = RiskFields.getRiskBaseRecordId(riskRec);
                        seledRiskBaseIdBuff.append(riskBaseId).append(",");
                        //cancel risk
                        Record outRec = getCancelProcessDAO().cancelRisk(riskRec);
                        if (CancelProcessFields.getTailCreateB(outRec).booleanValue()) {
                            isTailCreated = true;
                        }
                    }
                    String seledRiskBaseIds = seledRiskBaseIdBuff.toString();
                    inputRecord.setFieldValue("riskBaseRecordIds",seledRiskBaseIds);
                    // Save discipline decline entity(pass the selected risks and transactionLogId).
                    inputRecord.setFieldValue("isMultiCancelB", YesNoFlag.Y);
                    saveAllDisciplineDeclineEntity(inputRecord, seledRiskRs);

                    // Process amalgamate
                    boolean isAmalgamation = inputRecord.getBooleanValue("amalgamationB").booleanValue();
                    if (isAmalgamation) {
                        Record processRs = getCancelProcessDAO().processAmalgamation(inputRecord);
                        // if amalgamate fails
                        if (processRs.getLongValue("rc").longValue() < 0) {
                            MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.processCancellation.error",
                                new String[]{processRs.getStringValue("rmsg")});
                            throw new ValidationException("Error in amalgamation");
                        }
                    }
                }
                else if (CANCEL_LEVEL_COVERAGE.equals(cancelLevl)) {
                    seledCovgRs =
                        inputRs.getSubSet(new RecordFilter(CancelProcessFields.COVERAGE_DISPLAY, true)).
                            getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
                    Iterator seledCovgIter = seledCovgRs.getRecords();
                    while (seledCovgIter.hasNext()) {
                        Record covgRec = (Record) seledCovgIter.next();
                        covgRec.setFields(inputRecord, false);
                        //cancel coverage
                        Record outRec = getCancelProcessDAO().cancelCoverage(covgRec);
                        if (CancelProcessFields.getTailCreateB(outRec).booleanValue()) {
                            isTailCreated = true;
                        }
                    }
                }
                else if (CANCEL_LEVEL_COMPONENT.equals(cancelLevl)) {
                    seledCompRs =
                        inputRs.getSubSet(new RecordFilter(CancelProcessFields.COMPONENT_DISPLAY, true)).
                            getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
                    seledCompRs.setFieldsOnAll(policyHeader.toRecord(), false);
                    //cancel component
                    cancelAllComponent(seledCompRs);
                }
                else if (CANCEL_LEVEL_CLASS.equals(cancelLevl)) {
                    seledSubcovgRs =
                        inputRs.getSubSet(new RecordFilter(CancelProcessFields.SUBCOVERAGE_DISPLAY, true)).
                            getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
                    Iterator seledSubcovgIter = seledSubcovgRs.getRecords();
                    while (seledSubcovgIter.hasNext()) {
                        Record subcovgRec = (Record) seledSubcovgIter.next();
                        subcovgRec.setFields(inputRecord, false);
                        //cancel coverage class
                        Record outRec = getCancelProcessDAO().cancelCoverageClass(subcovgRec);
                        if (CancelProcessFields.getTailCreateB(outRec).booleanValue()) {
                            isTailCreated = true;
                        }
                    }
                }
                else if (CANCEL_LEVEL_COI.equals(cancelLevl)) {
                    seledCoiRs = inputRs.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
                    seledCoiRs.setFieldsOnAll(inputRecord, false);
                    //cancel COI holder
                    getCancelProcessDAO().cancelAllCoiHolder(seledCoiRs);
                }

                //Set tail created for prior term flag.
                boolean tailCreatedForPriorTerm = false;

                //if tail is created, then resolve tail
                if (isTailCreated) {
                    getCancelProcessDAO().resolveTail(inputRecord);
                    tailCreatedForPriorTerm = isTailCreatedForPriorTerm(policyHeader);
                    //After resolve tail,check if the tail term still exist.
                    String tailTermBaseId = getCancelProcessDAO().getTailTerm(inputRecord);
                    if (!StringUtils.isBlank(tailTermBaseId) && "0".equals(tailTermBaseId)) {
                       isTailCreated = false;
                    }
                }

                // If there is no any workflow, system initiates a new workflow.
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                String policyNo = PolicyHeaderFields.getPolicyNo(inputRecord);
                boolean saveOfficialB = false;
                if(inputRecord.hasStringValue(CancelProcessFields.SAVE_OFFICIAL_B)){
                    saveOfficialB = YesNoFlag.getInstance(CancelProcessFields.getSaveOfficialB(inputRecord)).booleanValue();
                }
                if (!CANCEL_LEVEL_COI.equals(cancelLevl) && !wa.hasWorkflow(policyNo)) {
                    String initialState = "";
                    String workflowProcessId = "";
                    if (isTailCreated) {
                        if (saveOfficialB) {
                            workflowProcessId = "SaveMultiCancellationOfficialWorkflow";
                            initialState = "invokeSaveMultiCancellationOfficial";
                        }
                        else {
                            workflowProcessId = "ProcessCancellationTailWorkflow";
                            initialState = "invokeViewTail";
                        }

                        // If the tail is created for prior term,system displays the message.
                        if (tailCreatedForPriorTerm) {
                            MessageManager.getInstance().addInfoMessage("pm.matainMultiCancel.tailCreated.msg");
                        }

                    }
                    else if (!createTail) {
                        if (saveOfficialB) {
                            workflowProcessId = "SaveMultiCancellationOfficialWorkflow";
                            initialState = "invokeDefaultIbnrDetailNotify";
                        }
                        else {
                            workflowProcessId = "ProcessCancellationTailWorkflow";
                            initialState = "invokeDefaultIbnrDetailNotify";
                        }

                        if (inputRecord.hasStringValue("riskBaseRecordIds")) {
                            String riskIds = inputRecord.getStringValue("riskBaseRecordIds");
                            riskIds = riskIds.substring(0, riskIds.length() - 1);
                            wa.setWorkflowAttribute(policyNo, "riskId", riskIds);
                        }
                    }
                    else {
                        if (saveOfficialB) {
                            workflowProcessId = "SaveMultiCancellationOfficialWorkflow";
                            initialState = "invokeSaveMultiCancellationOfficial";
                        }
                        else {
                            workflowProcessId = "ProcessCancellationTailWorkflow";
                            initialState = "invokeRateTransaction";
                        }
                    }

                    // Initialize the workflow.
                    wa.initializeWorkflow(policyNo, workflowProcessId, initialState);

                    outputRecord.setFieldValue("refreshPage", YesNoFlag.N);
                }
                else {
                    // For COI, if cancellation transaction is created, system should process output and complete this transaction.
                    if (isTransactionCreated && CANCEL_LEVEL_COI.equals(cancelLevl)) {
                        // Update the transaction status to OUTPUT
                        getTransactionManager().UpdateTransactionStatusNoLock(cancelTransaction, TransactionStatus.OUTPUT);

                        // Process output (PM Call to Output)
                        Record record = new Record();
                        record.setFieldValue("transactionLogId", cancelTransaction.getTransactionLogId());
                        record.setFieldValue("transactionCode", cancelTransaction.getTransactionCode());
                        record.setFieldValue("termBaseRecordId", policyHeader.getTermBaseRecordId());
                        getTransactionManager().processOutput(record, false);

                        // Update the transaction status to complete
                        getTransactionManager().UpdateTransactionStatusNoLock(cancelTransaction, TransactionStatus.COMPLETE);
                    }
                    //if is cancelling COI, refresh parent page directly
                    outputRecord.setFieldValue("refreshPage", YesNoFlag.Y);
                }
                if (wa.hasWorkflow(policyNo)) {
                    // This attribute is used to check whether it is in other workflow when open tail coverage page.
                    wa.setWorkflowAttribute(policyNo, dti.pm.core.http.RequestIds.WORKFLOW_FOR, "multiInvokeRating");
                    wa.setWorkflowAttribute(policyNo, "isTailCreated", YesNoFlag.getInstance(isTailCreated));
                    if (saveOfficialB && isTailCreated) {
                        wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), TAIL_CREATED);
                    }
                }
                //set status with VALID in outputRecord
                CancelProcessFields.setStatus(outputRecord, "VALID");
            }
            catch (ValidationException ve) {
                //if there is validation exception throw it
                throw ve;
            }
            catch (Exception e) {
                l.warning(getClass().getName() + "exception in performMultiCancellation+ " + e.getMessage());

                //set status with Failed in outputRecord
                CancelProcessFields.setStatus(outputRecord, "FAILED");

                // clean up data for amalgamation
                boolean isAmalgamation = inputRecord.getBooleanValue("amalgamationB").booleanValue();
                if (isAmalgamation) {
                    String amalgamationMethod = inputRecord.getStringValue("amalgamationMethod");
                    if ("NEW".equals(amalgamationMethod)) {
                        String policyNo = inputRecord.getStringValue("amalgamationTo");
                        // delete new created policy
                        if (!StringUtils.isBlank(policyNo)) {
                            Record rs = getTransactionManager().delWipTransaction(policyNo);
                            if (rs.getLongValue("rc").longValue() < 0) {
                                throw new AppException("pm.maintainCancellation.multiCancel.error", "Error in deleteWIP:" + rs.getStringValue("rmsg"));
                            }
                        }
                    }
                }

                //if transaction is created, delete WIP
                if (isTransactionCreated) {
                    //delete WIP transaction
                    getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
                }

                throw new AppException("pm.maintainCancellation.multiCancel.error", "exception in performMultiCancellation+ " + e.getMessage());
            }
        }
        else {
            //validate error exists
            outputRecord = valResultRec;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performMultiCancellation", outputRecord);
        }

        return outputRecord;
    }

    /**
     * rate policy for multi cancel
     *
     * @param policyHeader policy header
     */
    public Record ratePolicyForMultiCancel(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "ratePolicyForMultiCancel", new Object[]{policyHeader});
        }

        Record outputRecord = new Record();
        outputRecord.setFieldValue("rateWorkFlow", YesNoFlag.N);

        //if is long running transaction, initialize workflow to rate policy
        if (getTransactionManager().isRatingLongRunning()) {
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            wa.initializeWorkflow(policyHeader.getPolicyNo(),
                RATE_TRANSACTION_PROCESS,
                RATE_TRANSACTION_INITIAL_STATE);
            outputRecord.setFieldValue("rateWorkFlow", YesNoFlag.Y);
        }
        else {
            //rate policy
            getTransactionManager().performTransactionRating(policyHeader.toRecord());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "ratePolicyForMultiCancel");
        }
        return outputRecord;
    }

    /**
     * validate perform multi cancellation
     *
     * @param policyHeader policy header
     * @param inputRs      recordset from client
     * @param inputRecord  inputRecord contain request parameters
     * @return validate result
     */
    protected Record validatePerformMultiCancellation(PolicyHeader policyHeader, RecordSet inputRs, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePerformMultiCancellation", new Object[]{policyHeader, inputRs, inputRecord});
        }

        Record outputRecord = new Record();
        CancelProcessFields.setStatus(outputRecord, "VALID");

        Date termEff = DateUtils.parseDate(policyHeader.getTermEffectiveFromDate());
        Date termExp = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());
        String cancelLevl = CancelProcessFields.getCancellationLevel(inputRecord).toUpperCase();


        // The common validation is only for COI,
        // because it is handled in method validateMultiCancelConfirmation() for risk/coverage/class/component.
        // The cancellation date is required for COI, but not required for risk/coverage/class/component.
        if (CANCEL_LEVEL_COI.equals(cancelLevl)) {
            if (!inputRecord.hasStringValue(CancelProcessFields.CANCELLATION_LEVEL)) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noCancelLevel.error");
            }
            if (!inputRecord.hasStringValue(CancelProcessFields.CANCELLATION_DATE)) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noCancelDate.error");
            }

            if (!inputRecord.hasStringValue(CancelProcessFields.ACCOUNTING_DATE)) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noAccountingDate.error");
            }
        }
        // Validate amalgamate to policy No
        if (!CANCEL_LEVEL_COI.equals(cancelLevl)) {
            validateAmalgamation(inputRecord);
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Validation for multi cancel failed.");
        }
        // The validation of cancellation date is only for COI,
        // because it is handled in method validateMultiCancelConfirmation() for risk/coverage/class/component.
        if (CANCEL_LEVEL_COI.equals(cancelLevl)) {
            Date cancelDate = DateUtils.parseDate(CancelProcessFields.getCancellationDate(inputRecord));
            if (cancelDate.before(termEff)) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.cancelDateBeforeTermEff.error");
            }
            if (cancelDate.after(termExp)) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.cancelDateAfterTermExp.error");
            }
        }
        // Check the claims access indicator.
        if (inputRecord.hasStringValue(CancelProcessFields.AMALGAMATE_B)) {
            boolean isAmalgamation = inputRecord.getBooleanValue("amalgamationB").booleanValue();
            if (isAmalgamation && !inputRecord.hasStringValue(CancelProcessFields.CLAIMS_ACCESS_INDICATOR)) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noClaimsAccessIndicator.error");
            }
        }

        String cancelType = CancelProcessFields.getCancellationType(inputRecord);
        //validate if cancel items are selected
        RecordSet seledRiskRs = null;
        RecordSet seledCovgRs = null;
        RecordSet seledCompRs = null;
        RecordSet seledSubcovgRs = null;
        RecordSet seledCoiRs = null;
        if (CANCEL_LEVEL_RISK.equals(cancelLevl)) {
            seledRiskRs =
                inputRs.getSubSet(new RecordFilter(CancelProcessFields.RISK_NAME_DISPLAY, true)).
                    getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            if (seledRiskRs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noSeledRisk.error");
            }
        }
        else if (CANCEL_LEVEL_COVERAGE.equals(cancelLevl)) {
            seledCovgRs =
                inputRs.getSubSet(new RecordFilter(CancelProcessFields.COVERAGE_DISPLAY, true)).
                    getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            if (seledCovgRs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noSeledCovg.error");
            }
        }
        else if (CANCEL_LEVEL_COMPONENT.equals(cancelLevl)) {
            seledCompRs =
                inputRs.getSubSet(new RecordFilter(CancelProcessFields.COMPONENT_DISPLAY, true)).
                    getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            if (seledCompRs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noSeledComponent.error");
            }
        }
        else if (CANCEL_LEVEL_CLASS.equals(cancelLevl)) {
            seledSubcovgRs =
                inputRs.getSubSet(new RecordFilter(CancelProcessFields.SUBCOVERAGE_DISPLAY, true)).
                    getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            if (seledSubcovgRs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noSeledSubCovg.error");
            }
        }
        else if (CANCEL_LEVEL_COI.equals(cancelLevl)) {
            seledCoiRs = inputRs.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            if (seledCoiRs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noSeledCoi.error");
            }
        }
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("common validation for multi cancel failed.");
        }

        RecordSet processRs = new RecordSet();
        RecordSet futureCancellationRecords = new RecordSet();

        //Risk Validation
        if (CANCEL_LEVEL_RISK.equals(cancelLevl)) {
            int seledRiskCount = seledRiskRs.getSize();
            StringBuffer seledRiskBaseIdBuff = new StringBuffer();
            Iterator seledRiskIter = seledRiskRs.getRecords();
            while (seledRiskIter.hasNext()) {
                Record riskRec = (Record) seledRiskIter.next();
                String riskBaseId = RiskFields.getRiskBaseRecordId(riskRec);
                if (seledRiskBaseIdBuff.length() == 0) {
                    seledRiskBaseIdBuff.append(riskBaseId);
                }
                else {
                    seledRiskBaseIdBuff.append(",").append(riskBaseId);
                }
            }
            String seledRiskBaseIds = seledRiskBaseIdBuff.toString();

            seledRiskIter = seledRiskRs.getRecords();
            while (seledRiskIter.hasNext()) {
                Record riskRec = (Record) seledRiskIter.next();
                //clone a risk record for process, and add to process recordset
                Record processRiskRec = new Record();
                processRiskRec.setFields(riskRec);
                processRs.addRecord(processRiskRec);
                CancelProcessFields.setMsg(processRiskRec, "");
                CancelProcessFields.setStatus(processRiskRec, "VALID");

                //validate primary risk cancellation
                boolean isPrimary = RiskFields.getPrimaryRiskB(riskRec).booleanValue();
                if (isPrimary) {
                    updateCancellationStatus("pm.matainMultiCancel.cancelPrimaryRisk.error",
                        "INVALID", processRiskRec, outputRecord);
                    continue;
                }
                // Validate cancellation date.
                // For Risk level, the cancellation date is the cancel date for each risk.
                Date cancelDate = DateUtils.parseDate(CancelProcessFields.getCancelDate(riskRec));
                Date riskEff = DateUtils.parseDate(RiskFields.getRiskEffectiveFromDate(riskRec));
                Date riskExp = DateUtils.parseDate(RiskFields.getRiskEffectiveToDate(riskRec));
                if (cancelDate.before(riskEff) || cancelDate.after(riskExp)) {
                    updateCancellationStatus("pm.matainMultiCancel.cancellationDate.error",
                        "INVALID", processRiskRec, outputRecord);
                    continue;
                }
                //validate future primary risk count
                Record tempRecord = new Record();
                tempRecord.setFields(riskRec);
                tempRecord.setFields(policyHeader.toRecord(), false);
                // The three fields selectToDdl, ddlReason and ddlComments exist in both inputRs and inputRecord with different values,
                // they should be removed from inputRecord so as to avoid be overwrited.
                if(inputRecord.hasField(CancelProcessFields.SELECT_TO_DDL)){
                    inputRecord.remove(CancelProcessFields.SELECT_TO_DDL);
                }
                if(inputRecord.hasField(CancelProcessFields.DDL_REASON)){
                    inputRecord.remove(CancelProcessFields.DDL_REASON);
                }
                if(inputRecord.hasField(CancelProcessFields.DDL_COMMENTS)){
                    inputRecord.remove(CancelProcessFields.DDL_COMMENTS);
                }
                tempRecord.setFields(inputRecord, false);
                int futurePrimaryRiskCount = getCancelProcessDAO().getFuturePrimaryRiskCount(tempRecord);
                if (futurePrimaryRiskCount > 0) {
                    updateCancellationStatus("pm.matainMultiCancel.cancelFuturePrimaryRisk.error",
                        "INVALID", processRiskRec, outputRecord);
                    continue;
                }

                //validate owner
                tempRecord.setFieldValue("allSelRisks", seledRiskBaseIds);
                tempRecord.setFieldValue("totalSelRisks", String.valueOf(seledRiskCount));
                tempRecord.setFieldValue("inputStr", "CANCEL_PRIMARY");
                // It gets value from baseId in isSoloOwner function, we should set baseId here.
                ReinstateProcessFields.setBaseID(tempRecord, RiskFields.getRiskBaseRecordId(tempRecord));
                Record returnRecord = getCancelProcessDAO().isAllRiskOwnersSelected(tempRecord);
                boolean isAllOwnerSelected = YesNoFlag.getInstance(returnRecord.getStringValue("allOwnersSel")).booleanValue();
                String entityBase = returnRecord.getStringValue("entityBase");

                if (isAllOwnerSelected) {
                    boolean isSoloOwner = isSoloOwner(tempRecord);
                    if (isSoloOwner) {
                        updateCancellationStatus("pm.matainMultiCancel.cancelSingleOwner.error",
                            "INVALID", processRiskRec, outputRecord);
                        continue;
                    }
                    else {
                        /**
                         * If entityBase is one of the risks selected to be cancelled, nothing needs to be done, it is valid
                         * If entityBase is NOT one of the risks selected for cancellation, give a warning message
                         */
                        if (!seledRiskBaseIds.contains(entityBase)) {
                            updateCancellationStatus("pm.matainMultiCancel.cancelSingleOwner.warning",
                                "WARNING", processRiskRec, outputRecord);
                        }
                    }
                }
                else {
                    //other validation error
                    RecordSet valResults = getCancelProcessDAO().validateCancelRisk(tempRecord);
                    Record valResultRec = valResults.getSummaryRecord();
                    String status = CancelProcessFields.getStatus(valResultRec);
                    if (status == null) {
                        status = "VALID";
                    }
                    processRiskRec.setFields(valResultRec, true);
                    if (!status.equals("VALID")) {
                        processRiskRec.setFieldValue(RequestIds.SELECT_IND, "0");
                        CancelProcessFields.setStatus(outputRecord, "INVALID");
                        if(valResults.getSize() > 0){
                            futureCancellationRecords.addRecords(valResults);
                        }
                    }
                }
            }
        }
        //coverage validation
        else if (CANCEL_LEVEL_COVERAGE.equals(cancelLevl)) {
            Iterator seledCovgIter = seledCovgRs.getRecords();
            while (seledCovgIter.hasNext()) {
                Record covgRec = (Record) seledCovgIter.next();
                //clone a coverage record for process, and add to process recordset
                Record processCovgRec = new Record();
                processCovgRec.setFields(covgRec);
                processRs.addRecord(processCovgRec);
                CancelProcessFields.setMsg(processCovgRec, "");
                CancelProcessFields.setStatus(processCovgRec, "VALID");

                //validate primary coverage cancellation
                boolean isPrimaryCoverage = CoverageFields.getPrimaryCoverageB(covgRec).booleanValue();
                if (isPrimaryCoverage) {
                    updateCancellationStatus("pm.matainMultiCancel.cancelPrimaryCovg.error",
                        "INVALID", processCovgRec, outputRecord);
                    continue;
                }
                // Validate cancellation date.
                // For Coverage level, the cancellation date is the cancel date for each coverage.
                Date cancelDate = DateUtils.parseDate(CancelProcessFields.getCancelDate(covgRec));
                Date covgEff = DateUtils.parseDate(CoverageFields.getCoverageEffectiveFromDate(covgRec));
                Date covgExp = DateUtils.parseDate(CoverageFields.getCoverageEffectiveToDate(covgRec));
                if (cancelDate.before(covgEff) || cancelDate.after(covgExp)) {
                    updateCancellationStatus("pm.matainMultiCancel.covgCancelDate.error",
                        "INVALID", processCovgRec, outputRecord);
                    continue;
                }
                //validate prior acts cancellation
                String segmentCode = CoverageFields.getCoverageSegmentCode(covgRec);
                if (segmentCode.equals("PRIORACTS")) {
                    if (cancelDate.after(termEff) && !cancelType.equals("CANCNPP")) {
                        updateCancellationStatus("pm.matainMultiCancel.cancelPriorActs.error",
                            "INVALID", processCovgRec, outputRecord);
                        continue;
                    }
                }
                //Other validation for coverage.
                Record valInputRec = new Record();
                CancelProcessFields.setTermBaseId(valInputRec, policyHeader.getTermBaseRecordId());
                CancelProcessFields.setCancelDate(valInputRec, CancelProcessFields.getCancelDate(covgRec));
                CancelProcessFields.setType(valInputRec, CancelProcessFields.getCancelType(covgRec));
                CancelProcessFields.setReason(valInputRec, CancelProcessFields.getCancelReason(covgRec));
                CancelProcessFields.setMethodCode(valInputRec, CancelProcessFields.getCancelMethod(covgRec));
                CancelProcessFields.setCovBaseId(valInputRec, CoverageFields.getCoverageBaseRecordId(covgRec));
                RecordSet valResults = getCancelProcessDAO().validateCancelCoverage(valInputRec);
                Record valResultRec = valResults.getSummaryRecord();
                String status = CancelProcessFields.getStatus(valResultRec);
                if (status == null) {
                    status = "VALID";
                }
                if (!"VALID".equals(status)) {
                    processCovgRec.setFieldValue(RequestIds.SELECT_IND, "0");
                    processCovgRec.setFields(valResultRec, true);
                    CancelProcessFields.setStatus(outputRecord, "INVALID");
                    if(valResults.getSize() > 0){
                        futureCancellationRecords.addRecords(valResults);
                    }
                }
            }
        }
        //validate component
        else if (CANCEL_LEVEL_COMPONENT.equals(cancelLevl)) {
            Iterator seledCompIter = seledCompRs.getRecords();
            while (seledCompIter.hasNext()) {
                Record compRec = (Record) seledCompIter.next();
                //clone a coverage record for process, and add to process recordset
                Record processCompRec = new Record();
                processCompRec.setFields(compRec);
                processRs.addRecord(processCompRec);
                CancelProcessFields.setMsg(processCompRec, "");
                CancelProcessFields.setStatus(processCompRec, "VALID");

                // Validate cancellation date.
                // For Component level, the cancellation date is the cancel date for each component.
                Date cancelDate = DateUtils.parseDate(CancelProcessFields.getCancelDate(compRec));
                Date compEff = DateUtils.parseDate(ComponentFields.getEffectiveFromDate(compRec));
                Date compExp = DateUtils.parseDate(ComponentFields.getEffectiveToDate(compRec));
                if (cancelDate.before(compEff) || cancelDate.after(compExp)) {
                    updateCancellationStatus("pm.matainMultiCancel.compCancelDate.error", "INVALID",
                        processCompRec, outputRecord);
                    continue;
                }
            }
        }
        //validate coverage class
        else if (CANCEL_LEVEL_CLASS.equals(cancelLevl)) {
            Iterator seledSubcovgIter = seledSubcovgRs.getRecords();
            Map subCovgCountMap = new HashMap();
            Map seledSubcovgCountMap = new HashMap();
            while (seledSubcovgIter.hasNext()) {
                Record subcovgRec = (Record) seledSubcovgIter.next();
                //clone a coverage record for process, and add to process recordset
                Record processSubcovgRec = new Record();
                processSubcovgRec.setFields(subcovgRec);
                processRs.addRecord(processSubcovgRec);
                CancelProcessFields.setMsg(processSubcovgRec, "");
                CancelProcessFields.setStatus(processSubcovgRec, "VALID");

                Record tempRecord = new Record();
                tempRecord.setFields(subcovgRec);
                tempRecord.setFields(inputRecord, false);

                //validate active coverage class
                String covgBaseId = CoverageFields.getCoverageBaseRecordId(tempRecord);
                int subCovgCount = 0;
                int seledSubcovgCount = 0;
                if (subCovgCountMap.containsKey(covgBaseId)) {
                    subCovgCount = Integer.parseInt((String) subCovgCountMap.get(covgBaseId));
                    seledSubcovgCount = Integer.parseInt((String) seledSubcovgCountMap.get(covgBaseId));

                }
                else {
                    subCovgCount = getCancelProcessDAO().getCancelableCoverageClassCount(tempRecord);
                    subCovgCountMap.put(covgBaseId, String.valueOf(subCovgCount));
                    seledSubcovgCount = seledSubcovgRs.getSubSet(
                        new RecordFilter(CoverageFields.COVERAGE_BASE_RECORD_ID, covgBaseId)).getSize();
                    seledSubcovgCountMap.put(covgBaseId, String.valueOf(seledSubcovgCount));
                }
                if (seledSubcovgCount >= subCovgCount) {
                    updateCancellationStatus("pm.matainMultiCancel.noActiveSubcoverage.error",
                        "INVALID", processSubcovgRec, outputRecord);
                    continue;
                }

                // Validate cancellation date
                // For Coverage Class level, the cancellation date is the cancel date for each coverage class.
                Date cancelDate = DateUtils.parseDate(CancelProcessFields.getCancelDate(tempRecord));
                Date subcovgEff = DateUtils.parseDate(subcovgRec.getStringValue("subcoverageEffective"));
                Date subcovgExp = DateUtils.parseDate(subcovgRec.getStringValue("subcoverageExpiration"));
                if (cancelDate.before(subcovgEff) || cancelDate.after(subcovgExp)) {
                    updateCancellationStatus("pm.matainMultiCancel.subcovgCancelDate.error",
                        "INVALID", processSubcovgRec, outputRecord);
                    continue;
                }

                //other validation error
                tempRecord.setFields(inputRecord, false);
                PolicyHeaderFields.setTermBaseRecordId(tempRecord,policyHeader.getTermBaseRecordId());
                RecordSet valResults = getCancelProcessDAO().validateCancelCoverageClass(tempRecord);
                Record valResultRec = valResults.getSummaryRecord();
                String status = CancelProcessFields.getStatus(valResultRec);
                if (!status.equals("VALID")) {
                    processSubcovgRec.setFields(valResultRec, true);

                    if(valResults.getSize() > 0){
                        futureCancellationRecords.addRecords(valResults);
                    }
                }
            }
        }
        else if (CANCEL_LEVEL_COI.equals(cancelLevl)) {
            Iterator seledCoiIter = seledCoiRs.getRecords();
            Date cancelDate = DateUtils.parseDate(CancelProcessFields.getCancellationDate(inputRecord));
            while (seledCoiIter.hasNext()) {
                Record coiRec = (Record) seledCoiIter.next();
                //clone a coverage record for process, and add to process recordset
                Record processCoiRec = new Record();
                processCoiRec.setFields(coiRec);
                processRs.addRecord(processCoiRec);
                CancelProcessFields.setMsg(processCoiRec, "");
                CancelProcessFields.setStatus(processCoiRec, "VALID");

                //validate cancellation date
                Date coiEff = DateUtils.parseDate(CoiFields.getCoiEffectiveFromDate(coiRec));
                Date coiExp = DateUtils.parseDate(CoiFields.getCoiEffectiveToDate(coiRec));
                if (cancelDate.before(coiEff) || cancelDate.after(coiExp)) {
                    updateCancellationStatus("pm.matainMultiCancel.coiCancelDate.error",
                        "INVALID", processCoiRec, outputRecord);
                    continue;
                }
            }
        }
        if(futureCancellationRecords.getSize() > 0){
            CancelProcessFields.setFutureCancellationExistB(outputRecord, YesNoFlag.Y);
            UserSessionManager.getInstance().getUserSession().set("futureCancellationDetailsGridDataBean", futureCancellationRecords);
        }

        String valStatus = CancelProcessFields.getStatus(outputRecord);
        if (CancelProcessFields.StatusCodeValues.INVALID.equals(valStatus) || CancelProcessFields.StatusCodeValues.WARNING.equals(valStatus)) {
            RequestStorageManager.getInstance().set(CancelProcessFields.PROCESS_RECORDS, processRs);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePerformMultiCancellation", outputRecord);
        }

        return outputRecord;
    }


    /**
     * get initial values for mulit cancellation, add some page entitlements fields.
     *
     * @param policyHeader policy header
     * @param inputRecord  input record
     * @return inintial values include field indicators
     */
    public Record getInitialValueForMultiCancel(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValueForMultiCancel", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = MultiCancelEntitlementRecordLoadProcessor.setInitialValueForMultiCancel(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValueForMultiCancel", outputRecord);
        }
        return outputRecord;
    }

    /**
     * derive transaction code for different level multi cancel
     *
     * @param policyHeader policy header
     * @param inputRecord  inputRecord contains all request parameters
     * @return transactionCode
     */
    protected TransactionCode getTransactionCodeForMultiCancel(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTransactionCodeForMultiCancel",
                new Object[]{policyHeader, inputRecord});
        }
        TransactionCode tranCode = null;
        inputRecord.setFieldValue("endorsementCode", null);
        String cancelLevel = CancelProcessFields.getCancellationLevel(inputRecord).toUpperCase();
        if (CANCEL_LEVEL_RISK.equals(cancelLevel)) {
            tranCode = TransactionCode.RISKCANCEL;
            TransactionFields.setTransactionComment(inputRecord, "Risk is being cancelled");
        }
        else if (CANCEL_LEVEL_COVERAGE.equals(cancelLevel)) {
            tranCode = TransactionCode.COVGCANCEL;
            TransactionFields.setTransactionComment(inputRecord, "Coverage is being cancelled");
        }
        else if (CANCEL_LEVEL_COMPONENT.equals(cancelLevel)) {
            TransactionFields.setTransactionComment(inputRecord, "Component is being cancelled");
            //for transaction validation
            inputRecord.setFieldValue("effectiveFromDate",
                CancelProcessFields.getCancellationDate(inputRecord));
            if (policyHeader.isLastTerm()) {
                tranCode = TransactionCode.ENDORSE;
            }
            else {
                tranCode = TransactionCode.OOSENDORSE;
            }
        }
        else if (CANCEL_LEVEL_CLASS.equals(cancelLevel)) {
            tranCode = TransactionCode.SCVGCANCEL;
            TransactionFields.setTransactionComment(inputRecord, "Coverage class is being cancelled");
        }
        else if (CANCEL_LEVEL_COI.equals(cancelLevel)) {
            tranCode = TransactionCode.ENDCOIHOLD;
            TransactionFields.setTransactionComment(inputRecord, "COI Holder is being cancelled");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTransactionCodeForMultiCancel", tranCode);
        }
        return tranCode;
    }


    /**
     * update the status and message fields for every canceling item
     *
     * @param messageKey    validate message
     * @param statusCode    validate result
     * @param processRecord the canceling record
     * @param outputRecord  output record
     */
    private void updateCancellationStatus(String messageKey, String statusCode, Record processRecord, Record outputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateCancellationStatus", new Object[]{messageKey, statusCode, processRecord, outputRecord});
        }

        CancelProcessFields.setMsg(processRecord,
            MessageManager.getInstance().formatMessage(messageKey));
        CancelProcessFields.setStatus(processRecord, statusCode);

        if (statusCode.equals(CancelProcessFields.StatusCodeValues.INVALID) || statusCode.equals(CancelProcessFields.StatusCodeValues.WARNING)) {
            CancelProcessFields.setStatus(outputRecord, statusCode);
        }
        if (statusCode.equals(CancelProcessFields.StatusCodeValues.INVALID) || statusCode.equals(CancelProcessFields.StatusCodeValues.WARNING))
            processRecord.setFieldValue(RequestIds.SELECT_IND, "0");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateCancellationStatus");
        }
    }

    /**
     * validate if solo owner
     *
     * @param inputRecord inputRecord contain request parameters
     * @return boolean value indicates if it is solo owner
     */
    public boolean isSoloOwner(Record inputRecord) {
        return getCancelProcessDAO().isSoloOwner(inputRecord);
    }

    /**
     * Validate amalgamation data
     *
     * @param inputRecord
     */
    private void validateAmalgamation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAmalgamation", new Object[]{inputRecord});
        }
        // Get amalgamation method and policy number
        String amalgamationMethod = CancelProcessFields.getAmalgamationMethod(inputRecord);
        boolean isAmalgamation = inputRecord.getBooleanValue("amalgamationB").booleanValue();
        if (isAmalgamation && "EXIST".equalsIgnoreCase(amalgamationMethod)) {
            // Destination policy must be different than the source policy
            String sourcePolicyNo = inputRecord.getStringValue("policyNo");
            String destPolicyNo = inputRecord.getStringValue("amalgamationTo");
            if (!StringUtils.isBlank(destPolicyNo) && destPolicyNo.equals(sourcePolicyNo)) {
                MessageManager.getInstance().addErrorMessage("pm.amalgamation.invalidDestinationPolicyNo.error");
            }
            else {
                Record rec = getCancelProcessDAO().validateAmalgamation(inputRecord);
                long result = rec.getLongValue("RC").longValue();
                String message = rec.getStringValue("RMSG");
                if (result < 0) {
                    MessageManager.getInstance().addErrorMessage("pm.amalgamation.invalidPolicyNo.error", new Object[]{message});
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAmalgamation");
        }
    }

    /**
     * Check if the tail created for the prior term.
     *
     * @param policyHeader
     * @return
     */
    public boolean isTailCreatedForPriorTerm(PolicyHeader policyHeader){
        boolean isTailCreatedForPriorTerm = false;
        String tailTermBaseId = getCancelProcessDAO().getTailTerm(policyHeader.toRecord());
        String termBaseId = policyHeader.getTermBaseRecordId();
        if (!StringUtils.isBlank(tailTermBaseId) && !"0".equals(tailTermBaseId) && !termBaseId.equals(tailTermBaseId)) {
            isTailCreatedForPriorTerm = true;
        }
        return isTailCreatedForPriorTerm;
    }

    /**
     * Purge policy
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void purgePolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "purgePolicy", new Object[]{inputRecord});
        }

        // Set parameters for purge
        CancelProcessFields.setCancelReason(inputRecord, CancelProcessFields.CancelReasonCodeValues.CANCPURGE);
        CancelProcessFields.setMethodCode(inputRecord, CancelProcessFields.CancelMethodCodeValues.PRORATE);

        // Call performFlatCancel
        performFlatCancel(policyHeader, inputRecord, CancelProcessFields.CancelTypeCodeValues.PURGE);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "purgePolicy");
        }
    }

    /**
     * Flat cancel policy
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void flatCancelPolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "flatCancelPolicy", new Object[]{inputRecord});
        }

        // Set parameters for flat cancel
        CancelProcessFields.setCancelReason(inputRecord, CancelProcessFields.CancelReasonCodeValues.SHORTTERM);
        CancelProcessFields.setMethodCode(inputRecord, CancelProcessFields.CancelMethodCodeValues.SHORTRATE);

        // Call performFlatCancel
        performFlatCancel(policyHeader, inputRecord, CancelProcessFields.CancelTypeCodeValues.FLAT_CANCEL);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "flatCancelPolicy");
        }
    }

    /**
     * Perform flat cancel policy
     *
     * @param policyHeader
     * @param inputRecord
     * @param cancelType
     */
    private void performFlatCancel(PolicyHeader policyHeader, Record inputRecord, String cancelType) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performFlactCancel", new Object[]{inputRecord});
        }

        TransactionCode flatCancelCode;
        if (cancelType.equalsIgnoreCase(CancelProcessFields.CancelTypeCodeValues.FLAT_CANCEL)){
            flatCancelCode = TransactionCode.CANCEL;
        }
        else {
            flatCancelCode = TransactionCode.PURGE;
        }

        // Lock Policy
        boolean canLockPolicy = getLockManager().canLockPolicy(policyHeader);
        if (!canLockPolicy) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error");
            throw new ValidationException("The policy is already locked by another user at this time.");
        }
        else {
            getLockManager().lockPolicy(policyHeader, "performFlatCancel: locking policy before creating transaction.");
        }

        // Create transaction
        String transactionEffectiveDate = policyHeader.getTermEffectiveFromDate();
        try {
            Transaction trans = getTransactionManager().createTransaction(policyHeader, inputRecord, transactionEffectiveDate, flatCancelCode, false);
            String transactionLogId = trans.getTransactionLogId();
            TransactionFields.setTransactionLogId(inputRecord, transactionLogId);
        }
        catch (Exception e) {
            MessageManager.getInstance().addErrorMessage("pm.specialTrans.createTransaction.error");
            throw new ValidationException("Unable to create transaction.");
        }

        // Set parameters
        CancelProcessFields.setCancelType(inputRecord, CancelProcessFields.CancelReasonCodeValues.CANCUNDWR);
        CancelProcessFields.setTailCreateB(inputRecord, YesNoFlag.N);
        CancelProcessFields.setNumAgeOvrdRisks(inputRecord, "0");
        CancelProcessFields.setAgeOvrdRisks(inputRecord, "0");

        // Cancel policy
        try {
            getCancelProcessDAO().cancelPolicy(inputRecord);
        }
        catch (Exception e) {
            MessageManager.getInstance().addErrorMessage("pm.specialTrans.cancelPolicy.error");
            throw new ValidationException("Unable to cancel the policy.");
        }

        // Rate policy
        String transactionLogId = TransactionFields.getTransactionLogId(inputRecord);
        inputRecord.setFields(policyHeader.toRecord());
        TransactionFields.setTransactionLogId(inputRecord, transactionLogId);
        String returnValue = getTransactionManager().performTransactionRating(inputRecord);
        if ("FAILED".equals(returnValue)) {
            MessageManager.getInstance().addErrorMessage("pm.specialTrans.ratingPolicy.error");
            throw new ValidationException("Unable to rate the policy.");
        }

        if (cancelType.equalsIgnoreCase(CancelProcessFields.CancelTypeCodeValues.FLAT_CANCEL)){
            // Call the transactionmgr business component to perform the save logic
            TransactionFields.setTransactionCode(inputRecord, TransactionCode.CANCEL);
            getTransactionManager().processSaveTransactionAsOfficial(policyHeader, inputRecord);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performFlactCancel");
        }
    }

    /**
     * Load all transaction snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTransactionSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransactionSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs = getCancelProcessDAO().loadAllTransactionSnapshot(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransactionSnapshot", rs);
        }
        return rs;
    }

    /**
     * Load all term snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTermSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTermSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs = getCancelProcessDAO().loadAllTermSnapshot(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTermSnapshot", rs);
        }
        return rs;
    }

    /**
     * Load all policy component snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPolicyComponentSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicyComponentSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs = getCancelProcessDAO().loadAllPolicyComponentSnapshot(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPolicyComponentSnapshot", rs);
        }
        return rs;
    }

    /**
     * Load all risk snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRiskSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs = getCancelProcessDAO().loadAllRiskSnapshot(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskSnapshot", rs);
        }
        return rs;
    }

    /**
     * Load all coverage snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCoverageSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs = getCancelProcessDAO().loadAllCoverageSnapshot(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageSnapshot", rs);
        }
        return rs;
    }

    /**
     * Load all coverage component
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCoverageComponentSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageComponentSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs = getCancelProcessDAO().loadAllCoverageComponentSnapshot(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageComponentSnapshot", rs);
        }
        return rs;
    }

    /**
     * For policy level cancellation, system should load all active risks.
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllActiveRiskForCancellation(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllActiveRiskForCancellation", new Object[]{inputRecord});
        }

        RecordSet rs;
        String cancelLevel = CancelProcessFields.getCancellationLevel(inputRecord);
        if (CANCEL_LEVEL_POLICY.equals(cancelLevel)) {
            // Get all cancel items and then filter out risks
            RecordSet cancelItems = loadAllCancelableItem(policyHeader, inputRecord);
            RecordFilter riskReocordFilter = new RecordFilter(CancelProcessFields.RISK_NAME_DISPLAY, true);
            rs = cancelItems.getSubSet(riskReocordFilter);
            // Set two fields to risk recordSet
            rs.setFieldValueOnAll(CancelProcessFields.DDL_REASON, "", false);
            rs.setFieldValueOnAll(CancelProcessFields.DDL_COMMENTS, "", false);
        }
        else {
            rs = new RecordSet();
            ArrayList fieldsList = new ArrayList();
            fieldsList.add("rownum");
            rs.addFieldNameCollection(fieldsList);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllActiveRiskForCancellation", rs);
        }
        return rs;
    }

    /**
     * Save all discipline decline entity. System saves discipline decline entity only for
     * Policy and Risk level cancellation when the "markAsDdl" is "Y".
     *
     * @param inputRecord
     * @param inputRecords
     * @return
     */
    public int saveAllDisciplineDeclineEntity(Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllDisciplineDeclineEntity", new Object[]{inputRecord, inputRecords});
        }
        int count = 0;
        String cancelLevel = CancelProcessFields.getCancellationLevel(inputRecord);
        String markAsDdl = inputRecord.hasStringValue(CancelProcessFields.MARK_AS_DDL) ? CancelProcessFields.getMarkAsDdl(inputRecord) : "N";
        // "markAsDdl" must be "Y"
        if (YesNoFlag.getInstance(markAsDdl).booleanValue() &&
            (CANCEL_LEVEL_POLICY.equalsIgnoreCase(cancelLevel) || CANCEL_LEVEL_RISK.equalsIgnoreCase(cancelLevel))) {
            RecordSet selectedRisks;
            // Set the transactionLogId and cancellationDate to all records.
            inputRecords.setFieldValueOnAll(CancelProcessFields.CANCELLATION_DATE, CancelProcessFields.getCancellationDate(inputRecord));
            inputRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, TransactionFields.getTransactionLogId(inputRecord));
            boolean isMultiCancelB = CancelProcessFields.getIsMultiCancelB(inputRecord).booleanValue();
            // Filter the risks marked as "select to ddl" for multi cancellation
            if (isMultiCancelB) {
                selectedRisks = inputRecords.getSubSet(new RecordFilter(CancelProcessFields.SELECT_TO_DDL, "Y"));
            }
            else {
                // Filter the selected risks for policy cancellation
                if (CANCEL_LEVEL_POLICY.equalsIgnoreCase(cancelLevel)) {
                    selectedRisks = inputRecords.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));

                    // Issue 118437: Set cancellation method code to save
                    selectedRisks.setFieldValueOnAll(CancelProcessFields.CANCELLATION_METHOD,
                            CancelProcessFields.getCancellationMethod(inputRecord));
                }
                else { // For risk level, the ddl information exists in inputRecord.
                    selectedRisks = new RecordSet();
                    CancelProcessFields.setDdlReason(inputRecord, CancelProcessFields.getDdlReasonForRisk(inputRecord));
                    CancelProcessFields.setDdlComments(inputRecord, CancelProcessFields.getDdlCommentsForRisk(inputRecord));
                    selectedRisks.addRecord(inputRecord);
                }
            }
            count = getCancelProcessDAO().saveAllDisciplineDeclineEntity(selectedRisks);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllDisciplineDeclineEntity", count);
        }
        return count;
    }

    /**
     * Load all multi cancellation transactions for confirmation page.
     *
     * @param policyHeader
     * @param inputRecords
     * @param inputRecord
     * @return List
     */
    public List loadAllMultiCancelConfirmation(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMultiCancelConfirmation", new Object[]{policyHeader, inputRecords, inputRecord});
        }
        
        List rsList = new ArrayList(2);
        RecordSet transRs = new RecordSet();
        RecordSet detailRs = new RecordSet();

        // Handle all the cancellation transactions.
        if (inputRecords.getSize() > 0) {
            // Loop the record set to find distinct record.
            Iterator rsIt = inputRecords.getRecords();
            while (rsIt.hasNext()) {
                Record firstRecord = (Record) rsIt.next();
                Record record = new Record();
                record.setFields(firstRecord);
                record.setFieldValue(CancelProcessFields.CANCEL_DISTINCT_ID, CancelProcessFields.getRowNum(firstRecord));
                transRs.addRecord(record);

                // Find all the records which has the same cancellation information.
                String cancelDate = CancelProcessFields.getCancelDate(firstRecord);
                String cancelType = CancelProcessFields.getCancelType(firstRecord);
                String cancelReason = CancelProcessFields.getCancelReason(firstRecord);
                String cancelMethod = CancelProcessFields.getCancelMethod(firstRecord);
                String cancelComment = CancelProcessFields.getCancelComment(firstRecord);

                RecordSet tempDetailRs = inputRecords.getSubSet(new RecordFilter(CancelProcessFields.CANCEL_DATE, cancelDate)).
                    getSubSet(new RecordFilter(CancelProcessFields.CANCEL_TYPE, cancelType)).
                    getSubSet(new RecordFilter(CancelProcessFields.CANCEL_REASON, cancelReason)).
                    getSubSet(new RecordFilter(CancelProcessFields.CANCEL_METHOD, cancelMethod)).
                    getSubSet(new RecordFilter(CancelProcessFields.CANCEL_COMMENT, cancelComment));
                if (firstRecord.hasStringValue(TransactionFields.TRANSACTION_COMMENT2)) {
                    String transactionComment2 = TransactionFields.getTransactionComment2(firstRecord);
                    tempDetailRs = inputRecords.getSubSet(new RecordFilter(TransactionFields.TRANSACTION_COMMENT2, transactionComment2));
                }
                Iterator tempDetailIt = tempDetailRs.getRecords();
                while (tempDetailIt.hasNext()) {
                    Record tempRecord = (Record) tempDetailIt.next();
                    tempRecord.setFieldValue(CancelProcessFields.CANCEL_DISTINCT_ID, CancelProcessFields.getRowNum(firstRecord));
                    detailRs.addRecord(tempRecord);
                    inputRecords.removeRecord(tempRecord, true);
                }

                // The rsIt must be reset again since some records may be removed from inputRecords, reset rsIt to get the latest iterator.
                rsIt = inputRecords.getRecords();
            }
            // The continue button should be available.
            CancelProcessFields.setIsContinueAvailable(transRs.getSummaryRecord(), YesNoFlag.Y.toString());
        }
        else {
            // Add column rownum.
            List fieldNameList = new ArrayList();
            fieldNameList.add(CancelProcessFields.ROW_NUM);
            transRs.addFieldNameCollection(fieldNameList);
            detailRs.addFieldNameCollection(fieldNameList);
            // Disable the Continue button.
            CancelProcessFields.setIsContinueAvailable(transRs.getSummaryRecord(), YesNoFlag.N.toString());
            MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.confirmation.noDataFound");
        }

        // Add the record set of distinct transaction and detail.
        rsList.add(transRs);
        rsList.add(detailRs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMultiCancelConfirmation", rsList);
        }
        return rsList;
    }

    /**
     * Validate all multi cancellation transactions.
     * All the risk/coverage/covreage class/component validations are handled in this method, system ignores all the validation
     * in subsequent process.
     *
     * @param policyHeader policy header
     * @param inputRecords recordSet from client
     * @param inputRecord  inputRecord contain request parameters
     * @return Record      validate result
     */
   public Record validateMultiCancelConfirmation(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord) {
       Logger l = LogUtils.getLogger(getClass());
       if (l.isLoggable(Level.FINER)) {
           l.entering(getClass().getName(), "validateMultiCancelConfirmation", new Object[]{policyHeader, inputRecord});
       }
        
       Record validateRecord;
       try {
           String cancelLevl = CancelProcessFields.getCancellationLevel(inputRecord).toUpperCase();
           Date termEff = DateUtils.parseDate(policyHeader.getTermEffectiveFromDate());
           Date termExp = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());

           // Validate required fields cancel level and accounting date(cancel date is not required for risk/coverage/class/component).
           if (!inputRecord.hasStringValue(CancelProcessFields.CANCELLATION_LEVEL)) {
               MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noCancelLevel.error");
           }
           // If the amalgamationB is Y, the cancellation date is required.
           boolean isAmalgamation = YesNoFlag.getInstance(CancelProcessFields.getAmalgamationB(inputRecord)).booleanValue();
           if (isAmalgamation && !inputRecord.hasStringValue(CancelProcessFields.CANCELLATION_DATE)) {
               MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noCancelDate.error");
           }

           if (!inputRecord.hasStringValue(CancelProcessFields.ACCOUNTING_DATE)) {
               MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noAccountingDate.error");
           }

           if (MessageManager.getInstance().hasErrorMessages()) {
               throw new ValidationException("Validation for multi cancel failed.");
           }

           // Validate each cancellation transaction.
           Iterator rsIt = inputRecords.getRecords();
           while (rsIt.hasNext()) {
               Record record = (Record) rsIt.next();
               String name = RiskFields.getRiskName(record);
               // The cancel data for row is required.
               if (StringUtils.isBlank(CancelProcessFields.getCancelDate(record))) {
                   MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noCancelDate.error2", new String[]{name});
                   throw new ValidationException("The effective date for row can not be empty.");
               }
               // The cancel reason and method are determined by cancel type.
               if (StringUtils.isBlank(CancelProcessFields.getCancelType(record))) {
                   CancelProcessFields.setCancelType(record, CancelProcessFields.getCancellationType(inputRecord));
                   CancelProcessFields.setCancelReason(record, CancelProcessFields.getCancellationReason(inputRecord));
                   CancelProcessFields.setCancelMethod(record, CancelProcessFields.getCancellationMethod(inputRecord));
               }
               else if (CancelProcessFields.getCancellationType(inputRecord).equals(CancelProcessFields.getCancelType(record))) {
                   if (StringUtils.isBlank(CancelProcessFields.getCancelReason(record))) {
                       CancelProcessFields.setCancelReason(record, CancelProcessFields.getCancellationReason(inputRecord));
                       CancelProcessFields.setCancelMethod(record, CancelProcessFields.getCancellationMethod(inputRecord));
                   }
                   else if (CancelProcessFields.getCancellationReason(inputRecord).equals(CancelProcessFields.getCancelReason(record))) {
                       if (StringUtils.isBlank(CancelProcessFields.getCancelReason(record))) {
                           CancelProcessFields.setCancelMethod(record, CancelProcessFields.getCancellationMethod(inputRecord));
                       }
                   }
               }
               // Add the cancellation associated fields to record, they are necessary for the existing logic.
               record.setFieldValue(CancelProcessFields.CANCELLATION_LEVEL, CancelProcessFields.getCancellationLevel(inputRecord));
               record.setFieldValue(CancelProcessFields.ACCOUNTING_DATE, CancelProcessFields.getAccountingDate(inputRecord));
               record.setFieldValue(CancelProcessFields.CANCELLATION_DATE, CancelProcessFields.getCancelDate(record));
               record.setFieldValue(CancelProcessFields.CANCELLATION_TYPE, CancelProcessFields.getCancelType(record));
               record.setFieldValue(CancelProcessFields.CANCELLATION_REASON, CancelProcessFields.getCancelReason(record));
               record.setFieldValue(CancelProcessFields.CANCELLATION_METHOD, CancelProcessFields.getCancelMethod(record));

               // Validate for each row.
               Date cancelDate = DateUtils.parseDate(CancelProcessFields.getCancelDate(record));
               if (cancelDate.before(termEff)) {
                   MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.cancelDateBeforeTermEff.error2", new String[]{name});
               }
               if (cancelDate.after(termExp)) {
                   MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.cancelDateAfterTermExp.error2", new String[]{name});
               }

               if ((CANCEL_LEVEL_RISK.equals(cancelLevl) || CANCEL_LEVEL_COVERAGE.equals(cancelLevl)) &&
                   !record.hasStringValue(CancelProcessFields.CANCEL_TYPE)) {
                   MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noCancelType.error2", new String[]{name});
               }
               if ((CANCEL_LEVEL_RISK.equals(cancelLevl) || CANCEL_LEVEL_COVERAGE.equals(cancelLevl) ||
                   CANCEL_LEVEL_CLASS.equals(cancelLevl)) &&
                   !record.hasStringValue(CancelProcessFields.CANCEL_REASON)) {
                   MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noCancelReason.error2", new String[]{name});
               }
               if ((CANCEL_LEVEL_RISK.equals(cancelLevl) || CANCEL_LEVEL_COVERAGE.equals(cancelLevl) ||
                   CANCEL_LEVEL_CLASS.equals(cancelLevl)) &&
                   !record.hasStringValue(CancelProcessFields.CANCEL_METHOD)) {
                   MessageManager.getInstance().addErrorMessage("pm.matainMultiCancel.noCancelMethod.error2", new String[]{name});
               }

               if (MessageManager.getInstance().hasErrorMessages()) {
                   throw new ValidationException("Common validation for multi cancel failed.");
               }
           }

           // Above common validation will be ignored in the following method.
           validateRecord = validatePerformMultiCancellation(policyHeader, inputRecords, inputRecord);
       }
       catch (ValidationException ve) {
           // Clean up new created policy for amalgamation.
           boolean isAmalgamation = false;
           if(inputRecord.hasStringValue(CancelProcessFields.AMALGAMATE_B)){
               isAmalgamation = YesNoFlag.getInstance(CancelProcessFields.getAmalgamationB(inputRecord)).booleanValue();
           }

           if (isAmalgamation) {
               String amalgamationMethod = CancelProcessFields.getAmalgamationMethod(inputRecord);
               if (CancelProcessFields.StatusCodeValues.NEW.equals(amalgamationMethod)) {
                   String policyNo = CancelProcessFields.getAmalgamateTo(inputRecord);
                   // delete new created policy
                   if (!StringUtils.isBlank(policyNo)) {
                       Record rs = getTransactionManager().delWipTransaction(policyNo);
                       if (rs.getLongValue("rc").longValue() < 0) {
                           throw new AppException("Error in deleteWIP:" + rs.getStringValue("rmsg"));
                       }
                   }
               }
           }
           throw ve;
       }
        
       if (l.isLoggable(Level.FINER)) {
           l.exiting(getClass().getName(), "validateMultiCancelConfirmation", validateRecord);
       }
       return validateRecord;
   }

    /**
     * Process multi cancellation transactions.
     * If there are more than one cancellation transaction, system will being looping through each cancellation transaction to be processed.
     * System will process all steps on each transaction including displaying the tail window.  This will enable the user to
     * enter any manually rated tail premium, as needed.  Upon closing the tail screen or if no tail is created and additional
     * cancellation transactions need to be processed the system will save as official and initiate the next transaction.
     * If only one transaction is initiated on the multi-cancel screen the system will save as WIP and require the user to
     * save as official, which is no change from current.
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record processMultiCancelConfirmation(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processMultiCancelConfirmation", new Object[]{policyHeader, inputRecord});
        }
        
        Record outputRecord = null;
        List transList = (List) UserSessionManager.getInstance().getUserSession().get(CancelProcessFields.CONFIRM_TRANSACTION_LIST);
        if (transList == null || transList.size() != 2) {
            throw new AppException(AppException.UNEXPECTED_ERROR, "Invalid data for processing multi cancellation transaction.");
        }
        // No any validation need here, since all the cancellation transactions have been validated already.
        RecordSet transRs = (RecordSet) transList.get(0);
        RecordSet detailRs = (RecordSet) transList.get(1);
        // If it is invoked at the first time, system checks whether the cancellation transaction should be saved as OFFICIAL or WIP.
        if (inputRecord.hasStringValue(CancelProcessFields.IS_INIT_TRANS_B) &&
            YesNoFlag.getInstance(CancelProcessFields.getIsInitTransB(inputRecord)).booleanValue()) {
            if (transRs.getSize() == 1) {
                CancelProcessFields.setSaveOfficialB(inputRecord, YesNoFlag.N.toString());
            }
            else {
                CancelProcessFields.setSaveOfficialB(inputRecord, YesNoFlag.Y.toString());
            }
        }
        else {
            CancelProcessFields.setSaveOfficialB(inputRecord, YesNoFlag.Y.toString());
            // If no any subsequent cancellation transaction, it means all the transactions have been saved successfully, then system exits.
            if (transRs == null || transRs.getSize() == 0) {
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), CANCEL_TRANSACTION_COMPLETE);
                }
                else {
                    throw new AppException(AppException.UNEXPECTED_ERROR, "Failed to determine workflow for save multi cancellation transaction Official.");
                }
            }
        }
        // The amalgamation data should be added to transRs, so as to retrieve them in the next workflow steps.
        transRs.setFieldsOnAll(inputRecord, false);
        // Processes cancellation transaction. 
        if (transRs != null && transRs.getSize() > 0) {
            // System processes all the transaction details which pertain to the current cancellation transaction.
            Record currentTrans = transRs.getRecord(0);
            RecordSet currentDetails = detailRs.getSubSet(new RecordFilter(CancelProcessFields.CANCEL_DISTINCT_ID,
                CancelProcessFields.getCancelDistinctId(currentTrans)));

            currentTrans.setFields(inputRecord);
            // Comment will be used to create transaction.
            CancelProcessFields.setNewTransactionComment(currentTrans, CancelProcessFields.getCancelComment(currentTrans));
            if (currentTrans.hasStringValue(TransactionFields.TRANSACTION_COMMENT2)) {
                TransactionFields.setNewTransactionComment2(currentTrans, TransactionFields.getTransactionComment2(currentTrans));
            }
            performMultiCancellation(policyHeader, currentDetails, currentTrans);
            // After processed the current cancellation transaction, system should remove it from session since it will not be used.
            // System continues to process the next transaction in future workflow.

            transRs.removeRecord(currentTrans, true);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processMultiCancelConfirmation", outputRecord);
        }
        return outputRecord;
    }

    /**
     * getTransactionCode from cancel level
     * @param policyHeader
     * @param cancelLevel
     * @return
     */
    private TransactionCode getTransactionCode(PolicyHeader policyHeader, String cancelLevel) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTransactionCode", new Object[]{policyHeader, cancelLevel});
        }

        TransactionCode tranCode = null;
        if (CANCEL_LEVEL_RISK.equals(cancelLevel)) {
            tranCode = TransactionCode.RISKCANCEL;
        }
        else if (CANCEL_LEVEL_COVERAGE.equals(cancelLevel)) {
            tranCode = TransactionCode.COVGCANCEL;
        }
        else if (CANCEL_LEVEL_COMPONENT.equals(cancelLevel)) {
            //for transaction validation
            if (policyHeader.isLastTerm()) {
                tranCode = TransactionCode.ENDORSE;
            }
            else {
                tranCode = TransactionCode.OOSENDORSE;
            }
        }
        else if (CANCEL_LEVEL_CLASS.equals(cancelLevel)) {
            tranCode = TransactionCode.SCVGCANCEL;
        }
        else if (CANCEL_LEVEL_COI.equals(cancelLevel)) {
            tranCode = TransactionCode.ENDCOIHOLD;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTransactionCode", tranCode);
        }
        return tranCode;
    }

    /**
     * Verify config
     */
    public void verifyConfig() {
        if (getCancelProcessDAO() == null)
            throw new ConfigurationException("The required property 'cancelProcessDAO' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
        if (getTransactionManager() == null) {
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        }
        if (getRiskManager() == null) {
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        }
        if (getComponentDAO() == null) {
            throw new ConfigurationException("The required property 'componentDAO' is missing.");
        }
    }

    public CancelProcessDAO getCancelProcessDAO() {
        return m_CancelProcessDAO;
    }

    public void setCancelProcessDAO(CancelProcessDAO cancelProcessDAO) {
        m_CancelProcessDAO = cancelProcessDAO;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public RiskManager getRiskManager(){
        return m_riskManager;
    }
    
    public void setRiskManager(RiskManager riskManager){
        m_riskManager = riskManager;
    }
    
    public ComponentDAO getComponentDAO() {
        return m_componentDAO;
    }

    public void setComponentDAO(ComponentDAO componentDAO) {
        this.m_componentDAO = componentDAO;
    }

    private static final String CANCEL_TYPE_EXPIRED = "EXPIRED";
    private static final String CANCEL_TYPE_IBNR = "CANIBNR";

    private LockManager m_lockManager;
    private CancelProcessDAO m_CancelProcessDAO;
    private TransactionManager m_transactionManager;
    private RiskManager m_riskManager;
    private ComponentDAO m_componentDAO;
    //constant for cancel level
    public static final String CANCEL_LEVEL_POLICY = "POLICY";
    public static final String CANCEL_LEVEL_RISK = "RISK";
    public static final String CANCEL_LEVEL_COVERAGE = "COVERAGE";
    public static final String CANCEL_LEVEL_COMPONENT = "COMPONENT";
    public static final String CANCEL_LEVEL_CLASS = "COVERAGE CLASS";
    public static final String CANCEL_LEVEL_SLOT = "SLOT";
    public static final String CANCEL_LEVEL_TAIL = "TAIL";
    public static final String CANCEL_LEVEL_RISKREL = "RISK RELATION";
    public static final String CANCEL_LEVEL_EMPLOYEE = "EMPLOYEE";
    public static final String CANCEL_LEVEL_COI = "COI";
    private static final String IS_CANCEL_ADD_OCCUPANT_AVAILABLE = "isCancellationAddOccupantAvailable";
    private static final String IS_CANCEL_TYPE_AVAILABLE = "isCancellationTypeAvailable";
    private static final String IS_CANCEL_REASON_AVAILABLE = "isCancellationReasonAvailable";
    private static final String IS_CANCEL_METHOD_AVAILABLE = "isCancelMethodAvailable";
    private static final String RELATED_POLICY_WORKFLOW_PROCESS = "RelatedPolicyWorkflow";
    private static final String RELATED_POLICY_INITIAL_STATE = "invokeViewRelPolicy";
    private static final String RATE_TRANSACTION_PROCESS = "RateTransactionWorkflow";
    private static final String RATE_TRANSACTION_INITIAL_STATE = "invokeRateTransacction";
    private static final String CANCEL_TRANSACTION_COMPLETE = "COMPLETE";
    private static final String TAIL_CREATED = "TAIL_CREATED";
    private static final String CAPTURE_CANCELLATION_DETAIL = "captureCancellationDetail";
}
