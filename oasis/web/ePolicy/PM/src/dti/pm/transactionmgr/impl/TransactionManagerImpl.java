package dti.pm.transactionmgr.impl;

import dti.ci.core.struts.AddRowNoLoadProcessor;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.*;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.billingmgr.BillingManager;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.PolicyHeaderReloadCode;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.core.cachemgr.PolicyCacheManager;
import dti.pm.core.http.RequestIds;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policyattributesmgr.PolicyAttributesFactory;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.Term;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.policymgr.validationmgr.ResponseTypeEnum;
import dti.pm.policymgr.validationmgr.SoftValidationManager;
import dti.pm.riskmgr.RiskFields;
import dti.pm.tailmgr.TailFields;
import dti.pm.transactionmgr.CheckClearingReminderFields;
import dti.pm.transactionmgr.NotifyFields;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.TransactionXrefFields;
import dti.pm.transactionmgr.ValidateOpenClaimsMessageIds;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.transactionmgr.transaction.dao.TransactionDAO;
import dti.pm.validationmgr.impl.AccountingMonthRecordValidator;
import dti.pm.validationmgr.impl.AvailablePolicyTypeRecordValidator;
import dti.pm.validationmgr.impl.SimilarPolicyRecordValidator;
import dti.pm.validationmgr.impl.ValidTermDurationRecordValidator;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.workflowmgr.jobqueuemgr.JobCategory;
import dti.pm.workflowmgr.jobqueuemgr.JobQueueManager;
import dti.pm.soapmgr.tritechsoft.allocatoragent.allocateaddress.AddressWebService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the business logic that revolves around any given transaction.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 3, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/07/2007       sxm         Moved getScreenMode() to PolicyHeader
 * 09/19/2007       sxm         Fixed for issue 75326
 * 09/25/2007       fcb         Logic added for ENDQUOTE
 * 01/08/2008       fcb         getCommonInitialValuesForCreateTransaction: call to validatePolicyPicture added.
 * 02/21/2008       fcb         PM Issue 77308
 * 03/20/2008       fcb         Changed logic for processSaveTransaction for Long Running Transacitons handling.
 * 04/01/2008       sxm         Issue 81409 (for PM Issue 80965 - Agent logic change for Save Options)
 * 03/26/2008       yyh         loadAllPolicyAdminHistory added for issue 78338
 * 06/02/2008       yhchen      #80757, handle error message returned from processBilling
 * 10/01/2008       sxm         Issue 86925 - modify pre-save option validation of agent for quote 
 * 05/14/2010       fcb         Issue 107017 - excluded deleteWipTransaction for tails when the process
 *                              is SaveWip.  
 * 05/20/2010       syang       107872 - Modified loadTransactionByTerm to pass inputRecord to
 *                              LoadAllTransactionEntitlementRecordLoadProcessor. 
 * 05/20/2010       syang       107932 - Modified loadDiscrepancySummaryInfo to pass "mode" instead of "inMode"
 *                              since the "in" is one of stripPrefixes defined.
 * 07/30/2010       bhong       110357 - Made change in "saveTermDates" method to avoid using old term effective date from policy header. 
 * 08/06/2010       syang       103797 - Modified loadAllProfessionalEntityTransaction() to pass RecordLoadProcessor.
 * 09/10/2010       bhong       110269 - Added lockPolicy and processApplyEndorsementQuote
 * 09/14/2010       dzhang      103813 - Added method processUndoTerm(), getPreviousTermInformation(), isUndoTermAvailable().
 * 10/29/2010       gzeng       113774 - Make change in getInitialValuesForChangePolicyAdministrator() to make 'new administrator' field editable
 *                              in case of WIP,OOS WIP(only apply for initial term),newBus,Reissue WIP and Renew WIP
 * 11/02/2010       syang       111070 - Removed the applyTransactions from save official workflow and deleted associated method.
 * 11/03/2010       gzeng       113774 - Make page editable in case of Manual entry and Renew WIP, but not editable if it's OOS WIP
 *                              but not the initial term.
 * 11/18/2010       bhong       113819 - Added logics to cleanup lock data in official policy if other thread happened to lock the policy before complete the transaction.
 * 12/09/2010       syang       115296 - Modified processSaveTransactionAsEndorsementQuote() to set the new endorsement quote id to workflow.
 * 03/10/2011       dzhang      113062 - Modified createTransaction().
 * 03/18/2011       ryzhao      113559 - Modified loadRelatedPolicySaveError().
 * 03/31/2011       sxm         Issue 102311 - Modified loadSaveOptions() to remove Renewal Quote option when configured.
 * 04/07/2011       ryzhao      103801 - Added method getRelatedPolicyDisplayMode().
 *                                       Modified loadAllRelatedPolicy(), getLockedRelatedPolicyCount() to call different DAO method per display mode.
 * 04/11/2011       ryzhao      109243 - Modified deleteWipTransaction() to get transactionCode from record but not inputRecord.  
 * 04/11/2011       ryzhao      103801 - Modified loadAllRelatedPolicy() to call loadAllRelatedPolicy(), loadAllDistinctRelatedPolicy() with load processor parameter
 *                                       and use DefaultRecordLoadProcessor for second parameter.
 * 04/14/2011       dzhang      94232 - Modified loadSaveOptions() to check if there's inactives that is not Associated to a risk for quick quote.  
 * 04/15/2011       ryzhao      119243 - Modified deleteWipTransaction() to move the parameters for message key "pm.transactionmgr.deleteWIPTransaction.error.processError".
 * 05/01/2011       fcb         105791 - convertCoverageTransaction() and convertCoverage() added.
 * 08/01/2011       ryzhao      118806 - loadTransactionById() added.
 * 08/30/2011       ryzhao      124458 - Modified validateTransactionDetails to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 02/17/2012       xnie        130253 - Modified changeTermExpirationDate() to assign term effective to date to trans
 *                                       date instead of current system date.
 * 02/27/2012       xnie        129417 - Modified getCommonInitialValuesForCreateTransaction() to replace getFieldValue()
 *                                       with getStringValue() when get endorsementCode. 
 * 02/27/2012       xnie        129417 - a. Roll backed prior version fix for getCommonInitialValuesForCreateTransaction().
 *                                       b. Modified getInitialValuesForCaptureTransactionDetails(). If current transaction
 *                                          is not in wip policy transaction, we should set endorsement code as null.
 * 04/02/2012       sxm         Issue 131912 - Modified saveTransactionDetail to save changed transaction only.
 * 05/03/2012       fcb         Issue 131358 1. Fixed bug with setting the workflow next step when discrepancies are found.
 *                                           2. Loaded the Transaction Compare discrepancies in RSM only when records are found.
 * 05/30/2012       jshen       Issue 130229 - Modified saveTransactionAsOfficial() method to stop executing other steps
 *                                             when related policy errors occurred and save the policy as WIP.
 * 06/04/2012       sxm         Issue 133826 1. Remove save as decline option since it's not migrated to web.
 *                                           2. Remove redundant logic for save as renewal quote option
 * 06/15/2012       tcheng      Issue 133870 - Modified processSaveTransaction to change rate process before output
 * 06/26/2012       fcb         Issue 129528 1. Added owsHandleNBPolicyError.
 * 09/01/2013       awu         Issue 137122 - Modified validateTransactionDetails to add a condition for multiple cancel a component.
 * 09/06/2011       fcb         137198 - Changes related to creating transactions and handling error for ows.
 * 10/23/2012       awu         Issue 137764 - 1. Divided method saveTransactionAsOfficial into two methods(saveTransactionAsOfficial and saveContinueTransactionAsOfficial);
 *                                             2. Modified method processSaveTransactionAsOfficial to handle related policy info process.
 *                                             3. Modified loadRelatedPolicySaveError() to set the continued official flag
 *                                                into workflow.
 * 12/12/2012       tcg         Issue 138744 - Modified loadSaveOptions to make sure the options of "Save As" is Renewal Quote when copying
 *                                             the Renewal Quote.
 * 12/26/2012       awu         140186 - Added method checkClearingReminder(), processSaveCheckClearingReminder();
 * 12/27/2012       tcg         Issue 139862 - 1. Added loadWarningMessage(),initWarningMessage() and addWarningMessage();
 *                                             2. Modified applyEndorsementQuote(),processSaveTransactionAsOfficial(),saveTransactionAsWip()
 *                                             to pop up warning message.
 * 03/13/2013       awu         141924 - 1. Added addDefaultAgent.
 *                                       2. Added processSaveTransactionOfficialForWS
 * 01/29/2013       tcheng      141447 - Modified processSaveTransaction to make sure only rate process view premium
 * 02/04/2013       adeng       140101 - For 2-ways relationship, whereby the parent is also the child and the child is
 *                                       also the parent,rel_type is the one with different values.In order to avoid
 *                                       display duplicate records in screen,modified loadAllRelatedPolicy() to filter
 *                                       out duplicate records which have same policyNo,riskName,wipB.
 * 02/19/2013       jshen       141982 - 1. Renamed method loadTransactionByTerm to loadAllTransaction.
 *                                       2. Since retrieve all transaction and retrieve transaction for one term will all call this new method,
 *                                          modified it to add related logic to do the control.
 *                                       3. Moved logic of setting Entity Detail button page entitlement logic into
 *                                          LoadAllTransactionEntitlementRecordLoadProcessor for both loadAllTransaction() methods.
 * 03/06/2013       fcb         142697 - Removed logic for isRateProcess.
 * 04/02/2013       adeng       143062 - 1. Modified method changeTermExpirationDate() to reload policy header after change
 *                                       term expiration date successfully.
 *                                       2. Modified changeTermExpirationDate() to set field riskId to null, in order to
 *                                       extend term base on policy level.
 * 06/21/2013       adeng       117011 - Modified createTransaction() to create the fields that with the name "transactionComment2".
 * 10/16/2013       fcb         148904 - Added isAgentExist, isAgentValid, isCheckAgentConfigured, isBillingSetupAvailable
 * 10/16/2013       fcb         145725 - Added isSnapshotConfigured.
 *                                     - Added logic to check the profile in UserCacheManager via OasisUser.
 *                                     - Added logic to check if the replication is configured at the system level.
 *                                     - renumberWipSlots is only called when the level is RISK.
 * 12/03/2013       adeng       150131 - Modified loadSaveOptions() to switch the Business rule 4 & 2, check if
 *                                       billing_relation exists before validate if a valid agent record exists.
 * 11/20/2013       fcb         148037 - Added isNotifyConfigured, isFeesConfigured, isTaxConfigured
 * 02/12/2013       fcb         151011 - Bug fixed: Due to the changes in all the workflows to remove intermediate 
 *                                       messages that let the user know what step it is processing, and lumping 
 *                                       together processes in the background, the SaveAsOfficialDetail workflow 
 *                                       was changed. The cancel slot occupant workflow invocation was still 
 *                                       attempting to start the workflow with an initial state that does not 
 *                                       exist anymore and therefore was failing.
 * 03/11/2014       fcb         152685 - transaction log id passed to loadWarningMessage
 * 03/18/2014       awu         153022 - 1. Modified processSaveOccupantCancellationAsOfficial to put the renewal transaction
 *                                          id to workflow.
 *                                       2. Modified saveTransactionAsOfficial to get the transaction id from workflow.
 * 03/27/2014       awu         153398 - Modified saveTransactionAsOfficial to check whether the current policy has workflow.
 * 06/29/2014       awu         155065 - Modified saveTransaction to remove the conditions of skipping replication.
 * 12/19/2014       awu         159339 - Modified validateForExtendCancelTerm to add future cancellation validation.
 * 12/28/2014       wdang       158738 - Modified processSaveTransactionAsOfficial() to add a transition parameter for PURGE.
 * 01/12/2015       awu         160142 - Renamed isFeesConfigured to isChargesNFeesConfigured.
 * 08/21/2015       wdang       165535 - Modified processSaveTransactionAsEndorsementQuote() to reset policyViewMode & endQuoteId,
 *                                       so that subsequent workflow entries can retrieve policyHeader correctly.
 * 04/26/2016       eyin        171030 - Modified copyEndorsementQuote() and processApplyEndorsementQuote(),
 *                                       unlockWIPReinitialize() is called to unlock policy once any exception is caught.
 * 06/08/2016       sma         177372 - Replaced Integer with Long for transaction FK
 * 06/15/2016       lzhang      170647 - Add productNotificationResponse method: call
 *                                       transactionDAO().productNotificationResponse
 * 08/26/2016       wdang       167534 - 1. Added isAutoPendingRenewalEnable, hasTransactionXref, createTransactionXref, performDeleteRenewalWIP.
 *                                     - 2. Delete WIP Automatically when processing endorsement if transactionXref exists.
 * 09/14/2016       lzhang      179506 - Modified validateForDeleteWipTransaction: backup message does not prompt up
 *                                       when the renewal term is the only term of a policy
 * 10/07/2016       tzeng       179949 - Modified loadSaveOptions to display "Renewal Quote" and "Endorsement Quote"
 *                                       options only when cycle code is POLICY.
 * 02/10/2017       tzeng       183335 - Modified isAutoPendingRenewalEnable to make OOSE/Endorsement options available
 *                                       in last official term when cancel in pre-renewal.
 * 02/28/2017       mlm         183387 - Refactored to handle preview for long running transactions.
 * 03/09/2017       tzeng       166929 - Modified performProductNotificationResponse to record soft validation.
 * 04/26/2017       mlm         184827 - Refactored to initialize previewRequest in policy header correctly.
 * 07/17/2017       mlm         186951 - Refactored to reinitialize previewRequest in policy header after saveTransactionAsWip.
 * 06/25/2018       xnie        187070 - Modified saveTransactionAsOfficial() to do future terms's endorsement to adjust
 *                                       Exclude_Comp_Gr1/Exclude_Comp_Gr2 difference between term and risks.
 * 09/17/2018       ryzhao      195271 - Modified saveTransactionAsWip to skip extract logic when it is auto save
 *                                       when jumping among sub-tabs to improve performance.
 * 10/17/2018       ryzhao      196360 - Modified processSaveTransaction to go with the short rate instead of
 *                                       long running transaction when user navigate among sub-tabs.
 * ---------------------------------------------------
 */
public class TransactionManagerImpl implements TransactionManager, TransactionSaveProcessor {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Method to delete Wip Transaction
     *
     * @param policyHeader the policyHeader of the given policy, whose wip transaction is to be processed
     * @param inputRecord  Record containing policy header summary information about the
     * @return
     */
    public Record deleteWipTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "deleteWipTransaction", new Object[]{policyHeader, inputRecord});

        String actionClassName = "dti.pm.transactionmgr.struts.DeleteWIPTransactionAction";

        Record record = new Record();

        // do we have any default data specified by the action class?
        // because there is not the "getInitialValues.." method for this UC, we put the logic here
        // get the default values. just in case there are field values configured in workbench.
        Record defaultRecord = getWorkbenchConfiguration().getDefaultValues(actionClassName);
        record.setFields(defaultRecord);

        // overwrite with the parameter values
        record.setFields(inputRecord);

        // overwrite with the policyHeader record.
        record.setFields(policyHeader.toRecord());

        // Added below transaction information to the record:
        // 1.transactionTypeCode
        // 2.transactionCode
        // 3.transactionLogId
        // 4.transactionEffectiveDate
        // 5.transactionAccountingDate
        Transaction lastTrans = policyHeader.getLastTransactionInfo();
        TransactionFields.setTransactionTypeCode(record, lastTrans.getTransactionTypeCode());
        TransactionFields.setTransactionCode(record, lastTrans.getTransactionCode());
        TransactionFields.setTransactionLogId(record, lastTrans.getTransactionLogId());
        TransactionFields.setTransactionEffectiveFromDate(record, lastTrans.getTransEffectiveFromDate());
        TransactionFields.setTransactionAccountingDate(record, lastTrans.getTransAccountingDate());

        if (record.getBooleanValue("autoBackupRenewalWip", false).booleanValue()) {
            getTransactionDAO().backupRenewalWipTransaction(record);
        }
        else {
            validateForDeleteWipTransaction(policyHeader, record);
            // should we backup the transaction prior to delete?
            if (record.getBooleanValue("backupRenewalWip", false).booleanValue()) {
                getTransactionDAO().backupRenewalWipTransaction(record);
            }
        }

        // is it the first transaction? if the transactinoCode in ('NEWBUS','CONVRENEW','CONVREISSU') then it is!
        // we ought to delete the corresponding billing releation Fm_Billing_Relation
        TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();
        if (transactionCode.isNewBus() ||
            transactionCode.isConvRenew() ||
            transactionCode.isConvReissue() ||
            transactionCode.isRenewal() ||
            transactionCode.isReissue()) {
            getTransactionDAO().deleteBillingRelationForWiPTransaction(record);
        }

        int deleteResult = getTransactionDAO().deleteWipTransaction(record);

        // if delete was successful unlock the policy and set the wip indicator to N
        // but only if not the first transaction
        if (!(transactionCode.isNewBus() ||
            transactionCode.isConvRenew() ||
            transactionCode.isConvReissue())) {
            getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from delete WIP transaction.");
        }

        // if not returning 0, let us throw a ValidationException,
        if (deleteResult != 0) {
            l.logp(Level.WARNING, getClass().getName(), "deleteWIPTransaction", "Transaction could not be deleted. return code:" + deleteResult);
            String processErrorMessagekey = "pm.transactionmgr.deleteWIPTransaction.error.processError";
            MessageManager.getInstance().addErrorMessage(processErrorMessagekey);
            ValidationException ve = new ValidationException(MessageManager.getInstance().formatMessage(processErrorMessagekey, null));
            throw ve;
        }
        else {
            // set reload code in policy header
            setPolicyHeaderReloadCode(policyHeader, transactionCode);
        }

        l.exiting(getClass().getName(), "deleteWipTransaction", record);
        return record;
    }

    /**
     * Method to delete EndQuote Transation
     *
     * @param policyHeader the policyHeader of the given policy
     * @return
     */
    public void performDeleteEndQuoteTransaction(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "deleteEndQuoteTransaction", new Object[]{policyHeader});

        Record record = new Record();

        PolicyFields.setPolicyId(record, policyHeader.getPolicyId());
        TransactionFields.setTransactionLogId(record, policyHeader.getLastTransactionInfo().getTransactionLogId());
        record.setFieldValue("delRenQuote", "Y");
        String deleteResult = getTransactionDAO().deleteEndQuoteTransaction(record);
        if (!"SUCCESS".equals(deleteResult)) {
            AppException ve = new AppException("pm.transactionmgr.deleteEndQuoteTransaction.error.processError", "fail to delete end quote", new Object[]{getQuoteTypeByPolicyHeader(policyHeader)});
            l.throwing(getClass().getName(), "performDeleteEndQuoteTransaction", ve);
            throw ve;
        }
        l.exiting(getClass().getName(), "deleteEndQuoteTransaction");
    }

    /**
     * Lock policy
     *
     * @param policyHeader
     */
    public void lockPolicy(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "lockPolicy", new Object[]{policyHeader,});
        }

        //lock policy
        boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
        if (!ownLock) {
            ownLock = getLockManager().lockPolicy(policyHeader, "TransactionManager: lockPolicy.");
        }
        if (!ownLock) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error");
            ValidationException ve = new ValidationException(MessageManager.getInstance().formatMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error"));
            l.throwing(getClass().getName(), "applyEndorsementQuote", ve);
            throw ve;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "lockPolicy");
        }
    }

    /**
     * Method to apply endquote
     *
     * @param policyHeader the policyHeader of the given policy
     * @return
     */
    public void processApplyEndorsementQuote(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "processApplyEndorsementQuote", new Object[]{policyHeader});

        //validate billing relation
        Record billInputRec = new Record();
        TransactionFields.setTransactionLogId(billInputRec, policyHeader.getLastTransactionInfo().getTransactionLogId());
        billInputRec.setFieldValue("transAccountingDate", policyHeader.getLastTransactionInfo().getTransAccountingDate());
        boolean validBillingRelation = getTransactionDAO().isBillingRelationValid(billInputRec);
        if (!validBillingRelation) {
            getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from process Apply Endorsement.");
            AppException ae = new AppException(
                "pm.maintainTail.invalidBillingRelationError", "validation error found");
            l.throwing(getClass().getName(), "processApplyEndorsementQuote", ae);
            throw ae;
        }
        //apply Endorsement quote
        Record record = new Record();
        PolicyFields.setPolicyId(record, policyHeader.getPolicyId());
        record.setFieldValue("tranId", policyHeader.getLastTransactionInfo().getTransactionLogId());
        record.setFieldValue("termEff", policyHeader.getTermEffectiveFromDate());
        record.setFieldValue("termExp", policyHeader.getTermEffectiveToDate());
        record.setFieldValue("userId", UserSessionManager.getInstance().getUserSession().getUserId());
        try {
            getTransactionDAO().applyEndorsementQuote(record);
        }
        catch (Exception e) {
            getLockManager().unlockWIPReinitialize(policyHeader, YesNoFlag.N, "Unlock from Exception during process Apply Endorsement Quote.");
            AppException ae = new AppException(
                "pm.transactionmgr.applyEndQuoteTransaction.error.processError", "fail to apply endorsement quote", new Object[]{getQuoteTypeByPolicyHeader(policyHeader)});
            l.throwing(getClass().getName(), "processApplyEndorsementQuote", ae);
            throw ae;
        }

        Record inputRecord = new Record();
        inputRecord.setFields(policyHeader.toRecord(), false);
        try {
            // Process Billing
            processBilling(inputRecord);
        }
        catch (Exception e) {
            getLockManager().unlockWIPReinitialize(policyHeader, YesNoFlag.N, "Unlock from Exception during process Apply Endorsement Quote/process Billing.");
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.BILLING_ERROR", "Billing error found", e);
            l.throwing(getClass().getName(), "processApplyEndorsementQuote", ae);
            throw ae;
        }

        try {
            // Process Output
            processOutput(inputRecord, true);
        }
        catch (Exception e) {
            getLockManager().unlockWIPReinitialize(policyHeader, YesNoFlag.N, "Unlock from Process Apply Endorsement Quote after Output.");
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.OUTPUT_ERROR", "Output error found", e);
            l.throwing(getClass().getName(), "processApplyEndorsementQuote", ae);
            throw ae;
        }


        try {
            // Process unlock
            getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from Process Apply Endorsement Quote after Output");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.UNLOCK_ERROR", "Unlock error found", e);
            l.throwing(getClass().getName(), "processApplyEndorsementQuote", ae);
            throw ae;
        }
        try {
            // Update transaction status to COMPLETE
            updateTransactionStatusWithLock(inputRecord, TransactionStatus.COMPLETE);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.COMPLETE_ERROR", "Complete error found", e);
            l.throwing(getClass().getName(), "processApplyEndorsementQuote", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "processApplyEndorsementQuote");
    }

    /**
     * Apply endorsement quote
     *
     * @param policyHeader
     */
    public void applyEndorsementQuote(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "applyEndorsementQuote", new Object[]{policyHeader,});
        }
        initWarningMessage(policyHeader.getLastTransactionId());
        lockPolicy(policyHeader);
        processApplyEndorsementQuote(policyHeader);
        addWarningMessage(policyHeader.getLastTransactionId());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "applyEndorsementQuote");
        }
    }

    /**
     * Method to copy endquote
     *
     * @param policyHeader the policyHeader of the given policy
     * @return
     */
    public void copyEndorsementQuote(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "copyEndorsementQuote", new Object[]{policyHeader, inputRecord});
        //create new endorsement transaction
        Transaction newTrans;
        try {
            inputRecord.setFieldValue("effectiveFromDate", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
            newTrans = createTransaction(policyHeader, inputRecord, inputRecord.getStringValue("effectiveFromDate"), TransactionCode.ENDORSE);
        }
        catch (Exception e) {
            AppException ae = new AppException(
                "pm.transactionmgr.copyEndQuoteTransaction.error.createEndorseError", "create endorsement quote error");
            l.throwing(getClass().getName(), "copyEndorsementQuote", ae);
            throw ae;
        }
        //copy endorsement quote to endorsement transaction
        Record record = new Record();
        record.setFieldValue("oldeqTranId", policyHeader.getLastTransactionInfo().getTransactionLogId());
        record.setFieldValue("newwipTranId", newTrans.getTransactionLogId());
        try {
            getTransactionDAO().copyEndorsementQuote(record);
        }
        catch (Exception e) {
            //delete wip
            Record inputRecordForDelWip = new Record();
            inputRecordForDelWip.setFieldValue("policyId", policyHeader.getPolicyId());
            inputRecordForDelWip.setFieldValue("TranId", newTrans.getTransactionLogId());
            getTransactionDAO().deleteWipTransaction(inputRecordForDelWip);
            //unlock policy
            getLockManager().unlockWIPReinitialize(policyHeader, YesNoFlag.N, "Unlock from Exception during copy endorsement quote.");
            AppException ae = new AppException(
                "pm.transactionmgr.copyEndQuoteTransaction.error.copyEndQuoteError", "fail to copy endorsement quote", new Object[]{getQuoteTypeByPolicyHeader(policyHeader)});
            l.throwing(getClass().getName(), "copyEndorsementQuote", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "copyEndorsementQuote", record);
    }

    /**
     * Method to perform the validation prior to delete
     * It conditionally use ConfirmationFields.isConfirmed based on the actually business  requirements;
     * and store the confirmation prompts into MessageManager if possible per business requirements.
     *
     * @param inputRecord
     */

    protected void validateForDeleteWipTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateForDeleteWIPTransaciton", new Object[]{inputRecord});
        boolean proceedToDelete = true; // started with true,

        MessageManager mm = MessageManager.getInstance();
        SysParmProvider sysParm = SysParmProvider.getInstance();

        String batchAutoRenewalMessageKey = "pm.transactionmgr.deleteWIPTransaction.confirm.batchAutoRenewalWIP";
        String backupRenewalWIPMessageKey = "pm.transactionmgr.deleteWIPTransaction.confirm.backupRenewalWIP";

        //confirm for Renewal WIP Delete.
        if (!ConfirmationFields.isConfirmed(batchAutoRenewalMessageKey, inputRecord)) {
            // either user did not confirm Y, or it is the first time, we need to store the confirm
            String renewalWarningEnabled = sysParm.getSysParm("PM_REN_EVT_WARN", "N");
            if (!YesNoFlag.getInstance(renewalWarningEnabled).booleanValue()) { // Not enabled, do not need to prompt
                proceedToDelete = true;
            }
            else {// make a call to DAO to see if we should prompt for it
                boolean isBatchRenewWip = getTransactionDAO().isBatchRenewWip(inputRecord);
                if (isBatchRenewWip) {
                    mm.addConfirmationPrompt(batchAutoRenewalMessageKey);
                    proceedToDelete = false;
                }
                else {
                    proceedToDelete = true; // do not need to confirm with user
                }
            }
        }

        // the authority is done by PageEntitlement

        // confirm for backUpWIP?
        // we do not use ConfirmationFields.isConfirmed method, because
        // we have to differentiate the case that the key does not exists and the case that user confirmed N
        if (proceedToDelete) {
            if (!inputRecord.hasStringValue(backupRenewalWIPMessageKey + ".confirmed")) {
                // does not have the field, so, we might need to generate one for it
                TransactionTypeCode transactionTypeCode = TransactionFields.getTransactionTypeCode(inputRecord);
                String backUpRenewalEnabled = sysParm.getSysParm("PM_PENDING_RENEWAL", "N");
                Iterator termIter = policyHeader.getPolicyTerms();
                int count = 0;
                while (termIter.hasNext()) {
                    termIter.next();
                    count = count + 1;
                }
                if (transactionTypeCode.isRenewal() &&
                    YesNoFlag.getInstance(backUpRenewalEnabled).booleanValue()&&
                    count > 1) {
                    // need to inform user to get the response back
                    mm.addConfirmationPrompt(backupRenewalWIPMessageKey, false);
                    proceedToDelete = false;  // need user's confirmation before delete it
                }
                else {
                    // not relevant, do not need to generate confirm to prompt user for input
                    inputRecord.setFieldValue("backupRenewalWip", "N");
                }
            }
            else { // it has the confirmation field inside the inputRecord
                if (ConfirmationFields.isConfirmed(backupRenewalWIPMessageKey, inputRecord)) {
                    inputRecord.setFieldValue("backupRenewalWip", "Y");
                }
                else {
                    inputRecord.setFieldValue("backupRenewalWip", "N");
                }
            }
        }

        // Comfirm for deleting amalgamation link between linked policy
        String delSourceKey = "pm.transactionmgr.deleteWIPTransaction.confirm.deleteSourcePolicyForAmalgamation";
        String delTargetKey = "pm.transactionmgr.deleteWIPTransaction.confirm.deleteTargetPolicyForAmalgamation";

        // check if there's linked policy
        Record linkedPolicy = getTransactionDAO().getAmalgamationLinkedPolicy(inputRecord);
        String linkedPolicyNo = linkedPolicy.getStringValue("policyNo");
        String linkedPolicyId = linkedPolicy.getStringValue("policyId");
        boolean isOriginal = YesNoFlag.getInstance(linkedPolicy.getStringValue("originalB")).booleanValue();

        if (!ConfirmationFields.isConfirmed(delSourceKey, inputRecord) &&
            !ConfirmationFields.isConfirmed(delTargetKey, inputRecord)) {

            // Linked policy exists if the return policy No is not -1
            if (!"-1".equals(linkedPolicyNo)) {
                if (isOriginal) {
                    mm.addConfirmationPrompt(delSourceKey, true);
                }
                else {
                    mm.addConfirmationPrompt(delTargetKey, true);
                }
                proceedToDelete = false;
            }

        }
        else if (ConfirmationFields.isConfirmed(delSourceKey, inputRecord) ||
            ConfirmationFields.isConfirmed(delTargetKey, inputRecord)) {
            String sourcePolicyNo = PolicyHeaderFields.getPolicyNo(inputRecord);
            String note = MessageManager.getInstance().formatMessage("pm.amalgamation.pmDiary.note", new Object[]{sourcePolicyNo, linkedPolicyNo});

            // Set input parameters
            Record diaryInput = new Record();
            if (isOriginal) {
                // If current policy is "amalgamation from" policy, pass current policy's policyId
                diaryInput.setFieldValue("policyId", PolicyHeaderFields.getPolicyId(inputRecord));
            }
            else {
                // If current policy is "amalgamation to" policy.
                // Still pass "amalgamation from" policyId to diary to avoid issue when current policy is new created.
                diaryInput.setFieldValue("policyId", linkedPolicyId);
            }
            diaryInput.setFieldValue("transType", "AMALINKBRK");
            diaryInput.setFieldValue("classCode", "UNDWRTVP");
            diaryInput.setFieldValue("amalgamationNote", note);

            // Add PM Diary that amalgamation link is broken
            getTransactionDAO().addPmDiary(diaryInput);
        }

        if (!proceedToDelete) {
            ValidationException ve = new ValidationException("can not proceed to delete due to insufficient data");
            l.throwing(getClass().getName(), "validateForDeleteWIPTransaciton", ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validateForDeleteWIPTransaciton", inputRecord);
    }

    /**
     * This method load information about the last transaction performed on a term.
     * The transaction may be OFFICIAL or INPROGRES dependent upon the term and current view mode.
     *
     * @param policyHeader
     * @return Transaction
     */
    public Transaction loadLastTransactionInfoForTerm(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "loadLastTransactionInfoForTerm", new Object[]{policyHeader});

        Transaction transactionData = getTransactionDAO().loadLastTransactionInfoForTerm(policyHeader);

        l.exiting(getClass().getName(), "loadLastTransactionInfoForTerm", transactionData);
        return transactionData;
    }

    /**
     * Load all transaction summary by selected policy
     *
     * @param inputRecord input record that contains policy id
     * @return transaction summary
     */
    public RecordSet loadAllTransactionSummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransactionSummary", new Object[]{inputRecord});
        }
        RecordSet outRecordSet = getTransactionDAO().loadAllTransactionSummary(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransactionSummary", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Create's a database transaction based upon user entered parameters
     * the caller has to be sure to lock the policy (policyManager.lock policy) if it is required.
     * Because of the circular references. We can not declare a memember variable
     * PolicyManager and configure it in Spring
     *
     * @param policyHeader                 Current policy header with data populated
     * @param inputRecord                  a record contains transactionAccountingDate, endorsementCode, transactionComments values
     * @param transactionEffectiveFromDate User entered transaction effective date
     * @param transactionCode              Code value to drive the transaction type and code created
     * @return Transaction
     */

    public Transaction createTransaction(PolicyHeader policyHeader, Record inputRecord, String transactionEffectiveFromDate, TransactionCode transactionCode) {
        return createTransaction(policyHeader, inputRecord, transactionEffectiveFromDate, transactionCode, true);
    }

    /**
     * Create's a database transaction based upon user entered parameters
     * the caller has to be sure to lock the policy (policyManager.lock policy) if it is required.
     * Because of the circular references. We can not declare a memember variable
     * PolicyManager and configure it in Spring
     *
     * @param policyHeader                 Current policy header with data populated
     * @param inputRecord                  a record contains transactionAccountingDate, endorsementCode, transactionComments values
     * @param transactionEffectiveFromDate User entered transaction effective date
     * @param transactionCode              Code value to drive the transaction type and code created
     * @param lockPolicyBeforeSave         to lock policy before the save?
     * @return Transaction
     */

    public Transaction createTransaction(PolicyHeader policyHeader, Record inputRecord, String transactionEffectiveFromDate, TransactionCode transactionCode, boolean lockPolicyBeforeSave) {
        Logger l = LogUtils.enterLog(getClass(), "createTransaction", new Object[]{policyHeader, inputRecord, transactionEffectiveFromDate, transactionCode});

        // check the policy lock, if not owning the lock, stop it!
        if (lockPolicyBeforeSave) {
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (!ownLock) {
                ownLock = getLockManager().lockPolicy(policyHeader, "TransactionManager: createTransaction request");
            }
            if (!ownLock) {
                MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error");
                ValidationException ve = new ValidationException(MessageManager.getInstance().formatMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error"));
                l.throwing(getClass().getName(), "createTransaction", ve);
                throw ve;
            }
        }

        // the fields were created by function handleOnCaptureTransactionDetails (within common.js)
        // with names starting with the "new" word,
        // let us create the fields that with the names not containing "new" ,
        // but also keep the old version of the fields, just in case we need it for access.

        // so the caller of this method does not have to "convert" the fields anymore.

        if (inputRecord.hasField("newAccountingDate")) {
            inputRecord.setFieldValue("accountingDate", inputRecord.getFieldValue("newAccountingDate"));
        }
        if (inputRecord.hasField("newEndorsementCode")) {
            inputRecord.setFieldValue("endorsementCode", inputRecord.getFieldValue("newEndorsementCode"));
        }
        if (inputRecord.hasField("newTransactionComment")) {
            inputRecord.setFieldValue("transactionComment", inputRecord.getFieldValue("newTransactionComment"));
        }
        if (inputRecord.hasField("newTransactionComment2")) {
            inputRecord.setFieldValue("transactionComment2", inputRecord.getFieldValue("newTransactionComment2"));
        }

        // add transactionCode into inputRecord, so it can be used for validation
        TransactionFields.setTransactionCode(inputRecord, transactionCode);

        // add termFromDate and termToDate for the validation.
        // for now. just add for endorsement Transaction,
        // maybe needed for other trasnactionCode as well?
        if (transactionCode.isEndorsement() ||
            transactionCode.isOosEndorsement()) {
            inputRecord.setFieldValue("termEffectiveFromDate", policyHeader.getTermEffectiveFromDate());
            inputRecord.setFieldValue("termEffectiveToDate", policyHeader.getTermEffectiveToDate());
            inputRecord.setFieldValue("policyTermHistoryId", policyHeader.getPolicyTermHistoryId());
        }

        // validate it before create it, (to do this for web services),
        // if valiation fails, a ValidationException is thrown by validateTransactionDetails
        validateTransactionDetails(inputRecord);

        //Set termBaseRecordId, this is for MLMIC issue#103813
        if (TransactionCode.UNDOTERM.equals(transactionCode)) {
            inputRecord.setFieldValue("termBaseRecordId", inputRecord.getStringValue("undoPrevTermBaseId"));
        }
        //Set termBaseRecordId, this is for Norcal issue#113062
        else if (TransactionCode.TLCANCEL.equals(transactionCode) && inputRecord.hasStringValue(TailFields.MINIMUM_TERM_BASE_RECORD_ID)) {
            inputRecord.setFieldValue("termBaseRecordId", TailFields.getMinimumTermBaseRecordId(inputRecord));
        }
        else {
            inputRecord.setFieldValue("termBaseRecordId", policyHeader.getTermBaseRecordId());
        }


        Record renewalRecord = null;
        // Auto delete renewal WIP if any
        if (policyHeader.isWipB()
            && policyHeader.getWipTransCode() != null
            && PolicyAttributesFactory.getInstance().isAutoPendingRenewalEnable(
            policyHeader.getTermEffectiveFromDate(),
            transactionCode,
            TransactionCode.getInstance(policyHeader.getWipTransCode()))) {
            renewalRecord = performDeleteRenewalWIP(policyHeader);
        }
        Transaction transactionData = getTransactionDAO().createTransaction(policyHeader, inputRecord, transactionEffectiveFromDate, transactionCode);

        // Create Transaction Xref if necessary
        if (renewalRecord != null
            && renewalRecord.hasFieldValue(TransactionFields.TRANSACTION_LOG_ID)) {
            Record xrefRecord = new Record();
            xrefRecord.setFieldValue(TransactionXrefFields.ORIGINAL_TRANS_ID,
                renewalRecord.getFieldValue(TransactionFields.TRANSACTION_LOG_ID));
            xrefRecord.setFieldValue(TransactionXrefFields.RELATED_TRANS_ID,
                transactionData.getTransactionLogId());
            xrefRecord.setFieldValue(TransactionXrefFields.XREF_TYPE, TransactionXrefFields.AUTO_PENDING_RENEWAL);
            createTransactionXref(xrefRecord);
        }

        // set reload code in policy header
        setPolicyHeaderReloadCode(policyHeader, transactionCode);

        l.exiting(getClass().getName(), "createTransaction", transactionData);
        return transactionData;
    }

    /**
     * Update an exisitng transaction with a new status.  Wrapper to create the Transaction object first.
     *
     * @param inputRecord           Record with data to create the Transaction object
     * @param transactionStatusCode New transaction status code
     * @return Transaction
     */
    protected Transaction updateTransactionStatusWithLock(Record inputRecord, TransactionStatus transactionStatusCode) {
        Logger l = LogUtils.enterLog(getClass(), "updateTransactionStatusWithLock", new Object[]{inputRecord, transactionStatusCode});

        // Create the Transaction object wrapper
        Transaction t = new Transaction();
        t.setTransactionLogId(TransactionFields.getTransactionLogId(inputRecord));

        // Call the update
        Transaction transactionData = updateTransactionStatusWithLock(t, transactionStatusCode);

        l.exiting(getClass().getName(), "updateTransactionStatusWithLock", transactionData);
        return transactionData;
    }

    /**
     * Update an exisitng transaction with a new status
     *
     * @param trans                 Current transaction in progress
     * @param transactionStatusCode New transaction status code
     * @return Transaction
     */
    public Transaction updateTransactionStatusWithLock(Transaction trans, TransactionStatus transactionStatusCode) {
        Logger l = LogUtils.enterLog(getClass(), "updateTransactionStatusWithLock", new Object[]{trans, transactionStatusCode});

        Transaction transactionData = UpdateTransactionStatusNoLock(trans, transactionStatusCode);

        l.exiting(getClass().getName(), "updateTransactionStatusWithLock", transactionData);
        return transactionData;
    }

    /**
     * Update an exisitng transaction with a new status.
     * This method does the same as updateTransactionStatusWithLock except that it is not configured to verify we have a lock before executing.
     *
     * @param trans                 Current transaction in progress
     * @param transactionStatusCode New transaction status code
     * @return Transaction
     */
    public Transaction UpdateTransactionStatusNoLock(Transaction trans, TransactionStatus transactionStatusCode) {
        Logger l = LogUtils.enterLog(getClass(), "UpdateTransactionStatusNoLock", new Object[]{trans, transactionStatusCode});

        Transaction transactionData = getTransactionDAO().updateTransactionStatus(trans, transactionStatusCode);

        l.exiting(getClass().getName(), "UpdateTransactionStatusNoLock", transactionData);
        return transactionData;
    }

    /**
     * This method returns true or false for pageEntitlement to hide/show endorsement code
     *
     * @param inputRecord record containging transactionCode field used to determine if Endorsement code should be visible.
     * @return boolean: true if  EndorsementCode should be visible to user
     */
    public boolean isEndorsementCodeVisible(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isEndorsementCodeVisible", new Object[]{inputRecord});
        }

        boolean isVisible = true;

        TransactionCode transactionCode = TransactionFields.getTransactionCode(inputRecord);

        // add isTailDecline to the out record
        if (transactionCode.isTailDecline() || transactionCode.isTailDeclins()) {
            isVisible = true;
        }
        else {
            isVisible = false;
        }

        l.logp(Level.FINE, getClass().getName(), "isEndorsementCodeVisible", " EndorsementCodeVisible " + isVisible);

        l.exiting(getClass().getName(), "isRenewalIndicatorEditable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    /**
     * This method to get initial values for capture Transaction Details
     *
     * @param policyHeader
     * @param inputRecord  record containing minumum information for getting initial values
     * @return record containing the initial values for capture Transaction Details
     */

    public Record getInitialValuesForCaptureTransactionDetails(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForCaptureTransactionDetails", new Object[]{inputRecord});

        TransactionCode transactionCode = null;
        if (TransactionFields.hasTransactionCode(inputRecord)) {
            transactionCode = TransactionFields.getTransactionCode(inputRecord);
        }
        // get the initial values from wb bench
        String actionClassName = "dti.pm.transactionmgr.struts.CaptureTransactionDetailsAction";
        Record benchDefault = getWorkbenchConfiguration().getDefaultValues(actionClassName);
        inputRecord.setFields(benchDefault);
        Record outputRecord = getCommonInitialValuesForCreateTransaction(policyHeader, inputRecord);
        //If current transaction is not in wip policy transaction, we should set endorsement code as null.
        outputRecord.setFieldValue("endorsementCode", null);
        getInitialValueForAccountdingDate(inputRecord, outputRecord, true);

        // for taildecline show declination reason: transactionCode.
        YesNoFlag showDeclineTailResaon =
            YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_DECLINE_TAIL_RESN));
        if (showDeclineTailResaon.booleanValue()) {
            if (transactionCode != null && (transactionCode.isTailDecline() || transactionCode.isTailDeclins())) {
                outputRecord.setFieldValue("isDeclineReasonAvailable", YesNoFlag.Y);
            }
            else {
                outputRecord.setFieldValue("isDeclineReasonAvailable", YesNoFlag.N);
            }
        }

        l.exiting(getClass().getName(), "getInitialValuesForCaptureTransactionDetails", outputRecord);
        return outputRecord;
    }

    /**
     * This method to get initial values for create a endorsement Transaction
     *
     * @param policyHeader
     * @param inputRecord  record containing minumum information for getting initial values
     * @return record containing the initial values for create endorsement Transaction
     */
    public Record getInitialValuesForCreateEndorsementTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForCreateEndorsementTransaction", new Object[]{inputRecord});

        // get the initial values from wb bench
        String actionClassName = "dti.pm.transactionmgr.struts.CreateEndorsementTransactionAction";
        Record benchDefault = getWorkbenchConfiguration().getDefaultValues(actionClassName);
        inputRecord.setFields(benchDefault);
        Record outputRecord = getCommonInitialValuesForCreateTransaction(policyHeader, inputRecord);
        getInitialValueForAccountdingDate(inputRecord, outputRecord, false);
        //for create endorsement
        if (!(inputRecord.hasStringValue("isForCaptureTransactionDetail"))) {
            // if the policy is possible to lock down, but do not lock it until we are about to createTransaction
            boolean canLockPolicy = getLockManager().canLockPolicy(policyHeader);
            String cantLockPolicyMessageKey = "pm.transactionmgr.captureTransationDetails.cantLockPolicy.error";

            if (!canLockPolicy) {
                MessageManager.getInstance().addErrorMessage(cantLockPolicyMessageKey);
                EntitlementFields.setReadOnly(outputRecord, YesNoFlag.Y);
            }
            else {
                EntitlementFields.setReadOnly(outputRecord, YesNoFlag.N);
            }
        }
        // For 97299. The default value of effectiveFromDate should be decided by system parameter PM_DFLT_ENDORSE_DATE. 
        String defaultEffDate = SysParmProvider.getInstance().getSysParm("PM_DFLT_ENDORSE_DATE", "TERMEFF");
        if ("BLANK".equals(defaultEffDate)) {
            outputRecord.setFieldValue("effectiveFromDate", null);
        }
        else if ("TERMEFF".equals(defaultEffDate)) {
            outputRecord.setFieldValue("effectiveFromDate", policyHeader.getTermEffectiveFromDate());
        }
        outputRecord.setFieldValue("isEffectiveFromDateEditable", YesNoFlag.Y);
        //for capture transaction details with effDate
        if (inputRecord.hasStringValue("isForCaptureTransactionDetail") && inputRecord.hasStringValue("transactionEffDate")) {
            outputRecord.setFieldValue("effectiveFromDate", inputRecord.getFieldValue("transactionEffDate"));
            outputRecord.setFieldValue("isEffectiveFromDateEditable", YesNoFlag.N);
        }
        // Fix 96250: avoid set the Reason to existing endorsementCode value, system should set the endorsementCode empty.
        outputRecord.setFieldValue("endorsementCode", "");

        l.exiting(getClass().getName(), "getInitialValuesForCreateEndorsementTransaction", outputRecord);
        return outputRecord;
    }


    /**
     * This method to get common initial values for Transaction related pages
     *
     * @param policyHeader
     * @param inputRecord  record containing minumum information for getting initial values
     * @return record containing the common initial values for Create Transaction Details page
     */

    protected Record getCommonInitialValuesForCreateTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getCommonInitialValuesForCreateTransaction", new Object[]{policyHeader, inputRecord});

        validatePolicyPicture(policyHeader, inputRecord);

        inputRecord.setFields(policyHeader.toRecord(), false);

        // the logic really depends on the transactionCode, if the transactionCode is not available,
        // we throw a valiation exception
        String noTransactionCodeMessageKey = "pm.transactionmgr.captureTransationDetails.noTransactionCode.error";
        if (!TransactionFields.hasTransactionCode(inputRecord)) {
            MessageManager.getInstance().addErrorMessage(noTransactionCodeMessageKey);
            ValidationException ve = new ValidationException();
            ve.setMessageKey(noTransactionCodeMessageKey);
            l.throwing(getClass().getName(), "getInitialValuesForCaptureTransactionDetails", ve);
            throw ve;
        }

        Record outputRecord = new Record();
        outputRecord.setFields(inputRecord);

        // add transation code to the out record
        TransactionCode transactionCode = TransactionFields.getTransactionCode(inputRecord);
        TransactionFields.setTransactionCode(outputRecord, transactionCode);

        // add policyNo to the out record
        outputRecord.setFieldValue("policyNo", policyHeader.getPolicyNo());

        // add termBaseRecordId to the out record
        outputRecord.setFieldValue("termBaseRecordId", policyHeader.getTermBaseRecordId());
        outputRecord.setFieldValue("policyTermHistoryId", policyHeader.getPolicyTermHistoryId());

        // add endorsementCode to the out record
        if (inputRecord.hasStringValue("endorsementCode")) {
            outputRecord.setFieldValue("endorsementCode", inputRecord.getFieldValue("endorsementCode"));
        }

        // add effectiveFromDate to the out record
        if (!inputRecord.hasStringValue("effectiveFromDate")) {
            if (transactionCode.isEndorsement() ||
                transactionCode.isOosEndorsement()) {
                outputRecord.setFieldValue("effectiveFromDate", policyHeader.getTermEffectiveFromDate());
            }
            else {
                outputRecord.setFieldValue("effectiveFromDate", "");
            }
        }
        else {
            outputRecord.setFieldValue("effectiveFromDate", inputRecord.getStringValue("effectiveFromDate"));
        }

        l.exiting(getClass().getName(), "getCommonInitialValuesForCreateTransaction", outputRecord);
        return outputRecord;
    }

    /**
     * To get initial value for accountingDate, which will be used when capturing transaction and endorsement/oos transaction.
     *
     * @param inputRecord
     * @param outputRecord
     * @param isCaptureTransaction
     */
    private void getInitialValueForAccountdingDate(Record inputRecord, Record outputRecord, boolean isCaptureTransaction) {
        String accountingDate = inputRecord.getStringValue("accountingDate", "");
        String checkAccountingDate = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_CHK_ACCT_DATE, "N");
        Date currentDate = new Date();
        if (!YesNoFlag.getInstance(checkAccountingDate).booleanValue()) {
            // get the value per UC requirements, it is set to be today
            accountingDate = DateUtils.c_dateFormat.get().format(currentDate);
        }
        else {
            if (isCaptureTransaction) {
                TransactionCode transactionCode = TransactionFields.getTransactionCode(inputRecord);
                if (transactionCode.isTailAccept() || transactionCode.isTailReinstate()
                    || transactionCode.isTailActivate() || transactionCode.isTailDecline()) {
                    if (StringUtils.isBlank(accountingDate) || DateUtils.parseDate(accountingDate).before(currentDate)) {
                        accountingDate = DateUtils.c_dateFormat.get().format(currentDate);
                    }
                }
                else {
                    accountingDate = getMaxAccountingDate(inputRecord);
                }
            }
            else {
                accountingDate = getMaxAccountingDate(inputRecord);
            }
        }
        outputRecord.setFieldValue("accountingDate", accountingDate);
    }

    /**
     * This method to validate values for capture Transaction Details page and
     * createEndorsementTransaction page.
     * It attempts to performs "all" required validations per UC, and add the messages
     * into MessageManager if needed. At the end it throws validationException if data
     * is found invalid
     * <p/>
     * accontingMonth is checked only for captureTransactionDetails page
     *
     * @param inputRecord record entered by user for validation
     */
    public void validateTransactionDetails(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateTransactionDetails", new Object[]{inputRecord});
        boolean isValid = true;
        String enteredAccountingDateString = inputRecord.getStringValue("accountingDate");
        String invalidAccountingDateMessageKey = "pm.transactionmgr.captureTransationDetails.invalidAccountingDate.error";
        String validateAccountingDate = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_VALD_ACCT_DATE, "N");
        String noEffectiveFromDateMessageKey = "pm.transactionmgr.createEndorsementTransaction.noEffectiveFromDate.error";
        String invalidEffectiveFromDateMessageKey = "pm.transactionmgr.createEndorsementTransaction.invalidEffectiveFromDate.error";
        String latestAccountingDateString = getTransactionDAO().getLatestAccountingDate(inputRecord);  //policyTermHistoryId

        // make sure the transactionCode is presented
        if (!TransactionFields.hasTransactionCode(inputRecord)) {
            isValid = false;
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.noTransactionCode.error");
        }

        boolean isEndorsementCode = isEndorseTransactionCode(inputRecord);
        boolean isEndAffiliaTransactionCode = isEndAffiliaTransactionCode(inputRecord);

        if (isValid && !isEndorsementCode) { // skip this validation for endorsement transactionCode,
            // validate accounting month by calling Oasis_Valid_Accounting_Date (indirectly)
            isValid = new AccountingMonthRecordValidator().validate(inputRecord); //enteredAccountingDateString
        }

        // check the entered accounting Date against the latest accouting date for this policy term
        if (isValid && YesNoFlag.getInstance(validateAccountingDate).booleanValue()) {

            Date enteredAccountingDate = DateUtils.parseDate(enteredAccountingDateString);
            Date latestAccountingDate = DateUtils.parseDate(latestAccountingDateString);

            if (enteredAccountingDate.before(latestAccountingDate)) {
                isValid = false;
                MessageManager.getInstance().addErrorMessage(invalidAccountingDateMessageKey,
                    new Object[]{FormatUtils.formatDateForDisplay(latestAccountingDateString)}, "accountingDate");
                inputRecord.setFieldValue("accountingDate", latestAccountingDateString);
            }
        }

        // check the entered effectiveFromDate  for endorsement/oosendorsements transationCode
        if (isValid) {
             //for cancellation transaction, no need to validate its effectiveFromDate.
            // But the transaction of multiple cancel a component is an endorsement, so need to check the cancellationLevel
            if (!inputRecord.hasStringValue("cancellationLevel")) {
                if ((isEndorsementCode && inputRecord.hasField("effectiveFromDate")) || isEndAffiliaTransactionCode) {
                    // endorsement/oosendorsements transaction always has "effectiveFromDate" field.
                    // If there's no such field, it could be invoked by other page like copy endorsement quote
                    // This is purpose of adding " inputRecord.hasField("effectiveFromDate")" in above condition
                    if (!inputRecord.hasStringValue("effectiveFromDate")) {
                        isValid = false;
                        MessageManager.getInstance().addErrorMessage(noEffectiveFromDateMessageKey, "effectiveFromDate");
                    }
                    else { // has value for EffectiveFromDate field. let us validation it.
                        String effectiveFromDate = inputRecord.getStringValue("effectiveFromDate");
                        String termFromDate = inputRecord.getStringValue("termEffectiveFromDate");
                        String termToDate = inputRecord.getStringValue("termEffectiveToDate");
                        try {
                            if (DateUtils.daysDiff(effectiveFromDate, termFromDate) > 0 ||  // error if termFromDate > effectiveFromDate
                                DateUtils.daysDiff(termToDate, effectiveFromDate) >= 0) {     // error if effectiveFromDate >= termToDate
                                isValid = false;
                                inputRecord.setFieldValue("effectiveFromDate", ""); // null out the value per UC requirements
                                MessageManager.getInstance().addErrorMessage(invalidEffectiveFromDateMessageKey,
                                    new String[]{FormatUtils.formatDateForDisplay(termFromDate), FormatUtils.formatDateForDisplay(termToDate)}, "effectiveFromDate");
                            }
                        }
                        catch (ParseException pe) {
                            isValid = false;
                            MessageManager.getInstance().addErrorMessage(invalidEffectiveFromDateMessageKey,
                                new String[]{FormatUtils.formatDateForDisplay(termFromDate), FormatUtils.formatDateForDisplay(termToDate)}, "effectiveFromDate");
                        }
                    }
                }
            }
        }

        if (!isValid) {
            ValidationException ve = new ValidationException("validation error(s) found by TransactionManagerImpl");
            ve.setValidFieldValue("latestAccountingDate", latestAccountingDateString);
            ve.setValidFieldValue("accountingDate", inputRecord.getStringValue("accountingDate"));
            l.throwing(getClass().getName(), "validateTransactionDetails", ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validateTransactionDetails", "validated successfully");
    }

    /**
     * Determine if an agent exists on the current policy.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isAgentExist(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isAgentExist", new Object[]{inputRecord});

        boolean agentExist = getTransactionDAO().isAgentExist(inputRecord);

        l.exiting(getClass().getName(), "isAgentExist", Boolean.valueOf(agentExist));

        return agentExist;
    }

    /**
     * Determine if an agent is valid on the current policy.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return String
     */
    public String isAgentValid(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isAgentValid", new Object[]{inputRecord});

        String agentValid = getTransactionDAO().isAgentValid(inputRecord);

        l.exiting(getClass().getName(), "isAgentValid", agentValid);

        return agentValid;
    }

    /**
     * Determine if an agent is configured to be validated.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isCheckAgentConfigured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isCheckAgentConfigured", new Object[]{inputRecord});

        boolean isConfigured = PolicyFields.getPolicyCycleCode(inputRecord).isPolicy() &&
            SysParmProvider.getInstance().getSysParm("AGENCY", "X").equalsIgnoreCase("Y") ||
            PolicyFields.getPolicyCycleCode(inputRecord).isQuote() &&
                SysParmProvider.getInstance().getSysParm("QAGENCY", "X").equalsIgnoreCase("Y");

        l.exiting(getClass().getName(), "isCheckAgentConfigured", isConfigured);

        return isConfigured;
    }

    /**
     * Determine the save options available for the current transaction
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return String
     */
    public Map loadSaveOptions(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadSaveOptions", new Object[]{inputRecord});

        boolean warningsFound = false;
        MessageManager messageManager = MessageManager.getInstance();

        // Setup the initial save options based upon transaction code
        Map saveOptions = new HashMap();
        saveOptions.put("OFFICIAL", "Official");
        //if it's re-rate policy,it always has only two options:wip and official
        if (inputRecord.hasField("onlyWipOfficial")) {
            return saveOptions;
        }

        if (PolicyFields.getPolicyCycleCode(inputRecord).getName() == POLICY) {
            if (TransactionFields.getTransactionCode(inputRecord).isEndorsement()) {
                if (TransactionFields.getTransactionTypeCode(inputRecord).isRenewal()) {
                    saveOptions.put("RENQUOTE", "Renewal Quote");
                }
                else {
                    saveOptions.put("ENDQUOTE", "Endorsement Quote");
                }
            }

            if (TransactionFields.getTransactionCode(inputRecord).isRenewal()) {
                saveOptions.put("RENQUOTE", "Renewal Quote");
            }
        }

        // Retrieve the system parameter to determine if warning messages are displayed
        String displayWarnings = SysParmProvider.getInstance().getSysParm("PM_SKIP_SAVE_AS_REQ", "N");

        // Business Rule 1: If policyCycleCode = 'POLICY'/'QUOTE', system parameter AGENCY/QAGENCY = 'Y', and an Agent does not exist,
        // then check the validity of defaulted agent.  If default agent is nod valid, show invalid agent message.
        if (isCheckAgentConfigured(inputRecord)) {

            // Determine if the agent exists
            boolean agentFound = isAgentExist(inputRecord);

            // Agent does not exists, validate default agent
            if (!agentFound) {
                String agentValid = isAgentValid(inputRecord);

                // Invalid default agent, show warning
                if (!agentValid.equalsIgnoreCase("VALID")) {
                    warningsFound = true;

                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "loadSaveOptions", "Business Rule 1:  Agent Valid = " + agentValid);
                    }
                    if (agentValid.equalsIgnoreCase("INVALID") || StringUtils.isBlank(agentValid)) {
                        if (displayWarnings.equalsIgnoreCase("N")) {
                            messageManager.addInfoMessage("pm.transactionmgr.loadSaveOptions.agentRequired.info");
                        }
                    }
                    else {
                        if (displayWarnings.equalsIgnoreCase("N")) {
                            messageManager.addInfoMessage("pm.transactionmgr.loadSaveOptions.agentDatabaseMessage.info", new String[]{agentValid});
                        }
                    }
                }
            }
        }

        // Business Rule 4:  If policyCycleCode = 'POLICY' and PM_SET_BILLING_COVG = 'N'
        // and the billing relation is incomplete, raise a missing billing relation message
        if (PolicyFields.getPolicyCycleCode(inputRecord).isPolicy() &&
            SysParmProvider.getInstance().getSysParm("PM_SET_BILLING_COVG", "N").equalsIgnoreCase("N")) {

            boolean validBillingRelation = getTransactionDAO().isBillingRelationValid(inputRecord);

            if (!validBillingRelation) {
                warningsFound = true;
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "loadSaveOptions", "Business Rule 4:  validBillingRelation = " + validBillingRelation);
                }
                if (displayWarnings.equalsIgnoreCase("N")) {
                    messageManager.addInfoMessage("pm.transactionmgr.loadSaveOptions.incompleteBillingRelation.info");
                }
            }
        }

        // Business Rule 2:  If the prior agent check was skipped (Business Rule #1) due to configuration and
        // Policy/Quote AGENCY/QAGENCY system parameters are "Y" and PM_AUTO_AGENT system
        // parameter = "Y" then auto default the agent
        if (!warningsFound) {
            if ((PolicyFields.getPolicyCycleCode(inputRecord).isPolicy() &&
                SysParmProvider.getInstance().getSysParm("AGENCY", "N").equalsIgnoreCase("Y")) ||
                (PolicyFields.getPolicyCycleCode(inputRecord).isQuote() &&
                    SysParmProvider.getInstance().getSysParm("QAGENCY", "N").equalsIgnoreCase("Y"))) {

                if (SysParmProvider.getInstance().getSysParm("PM_AUTO_AGENT", "N").equalsIgnoreCase("Y")) {
                    getTransactionDAO().insertDefaultAgent(inputRecord);
                }
            }
                            String agentValid = getTransactionDAO().isAgentValid(inputRecord);

                // Invalid default agent, show warning
                if (!agentValid.equalsIgnoreCase("VALID")) {
                    warningsFound = true;

                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "loadSaveOptions", "Business Rule 1:  Agent Valid = " + agentValid);
                    }
                    if (agentValid.equalsIgnoreCase("INVALID") || StringUtils.isBlank(agentValid)) {
                        if (displayWarnings.equalsIgnoreCase("N")) {
                            messageManager.addInfoMessage("pm.transactionmgr.loadSaveOptions.agentRequired.info");
                        }
                    }
                    else {
                        if (displayWarnings.equalsIgnoreCase("N")) {
                            messageManager.addInfoMessage("pm.transactionmgr.loadSaveOptions.agentDatabaseMessage.info", new String[]{agentValid});
                        }
                    }
                }
        }

        // Business Rule 3:  If an Underwriter is required but does not exist on the policy
        // the raise a missing underwriter warning
        if ((PolicyFields.getPolicyCycleCode(inputRecord).isPolicy() &&
            SysParmProvider.getInstance().getSysParm("UNDERWRITER", "N").equalsIgnoreCase("Y")) ||
            (PolicyFields.getPolicyCycleCode(inputRecord).isQuote() &&
                SysParmProvider.getInstance().getSysParm("QUNDERWRITER", "N").equalsIgnoreCase("Y"))) {
            String effDate = PolicyFields.getTermEffectiveFromDate(inputRecord);
            String expDate = PolicyFields.getTermEffectiveToDate(inputRecord);
            long termLength = 0;
            try {
                termLength = DateUtils.daysDiff(effDate, expDate);
            }
            catch (ParseException e) {
                AppException ae = new AppException("Date format error: " + effDate + " " + expDate);
                l.throwing(getClass().getName(), "loadSaveOptions", ae);
                throw ae;
            }
            //The underwriter check is only performed when the term expiration date is > term effective date
            if (termLength > 0) {
                // Determine if the underwriter exists
                boolean underwriterFound = getTransactionDAO().isUnderwriterExist(inputRecord);
                if (!underwriterFound) {
                    warningsFound = true;
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "loadSaveOptions", "Business Rule 3:  underwriterFound = " + underwriterFound);
                    }
                    if (displayWarnings.equalsIgnoreCase("N")) {
                        messageManager.addInfoMessage("pm.transactionmgr.loadSaveOptions.underwriterRequired.info");
                    }
                }
            }
        }

        // Business Rule 5:  Determine if ENDQUOTE/RENQUOTE option is available based on configuration
        inputRecord.setFieldValue("saveOption", "ENDQUOTE");
        if (getTransactionDAO().isSaveOptionAvailable(inputRecord)) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "loadSaveOptions", "Business Rule 5:  Endorsement quote option not available");
            }
            saveOptions.remove("ENDQUOTE");
            saveOptions.remove("RENQUOTE");
        }

        // Business Rule 6:  Determine if DECLINE option is available based on configuration
        inputRecord.setFieldValue("saveOption", "DECLINE");
        if (!getTransactionDAO().isSaveOptionAvailable(inputRecord)) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "loadSaveOptions", "Business Rule 6:  Decline option not available");
            }
            saveOptions.remove("DECLINE");
        }

        // Business Rule 7:  If Collateral is missing but required for the policy type
        if ((PolicyFields.getPolicyCycleCode(inputRecord).isPolicy() &&
            SysParmProvider.getInstance().getSysParm("PM_ACCEPT_COLLATERAL", "N").equalsIgnoreCase("Y"))) {

            if (StringUtils.isBlank(inputRecord.getStringValue("collateralB"))) {
                // Determine if the collateral is required
                boolean collateralRequired = getTransactionDAO().isCollateralRequired(inputRecord);


                if (collateralRequired) {
                    warningsFound = true;
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "loadSaveOptions", "Business Rule 7:  Collateral missing but required = " + collateralRequired);
                    }
                    if (displayWarnings.equalsIgnoreCase("N")) {
                        messageManager.addInfoMessage("pm.transactionmgr.loadSaveOptions.collateralRequired.info");
                    }
                }
            }
        }

        // Business Rule 8: if policy cyle is quote and there's un-populated entities which is loaded by quick quote,
        // display warning message.
        if (PolicyFields.getPolicyCycleCode(inputRecord).isQuote()) {
            boolean isCisPopulated = getTransactionDAO().isCisPopulated(inputRecord);
            if (!isCisPopulated) {
                warningsFound = true;
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "loadSaveOptions", "Business Rule 8:  populate cis is required.");
                }
                if (displayWarnings.equalsIgnoreCase("N")) {
                    messageManager.addInfoMessage("pm.transactionmgr.loadSaveOptions.populateCisRequired.info");
                }
            }
        }

        // Business Rule 9: if policy cyle is quote and there's un-associated inactives which is loaded by quick quote,
        // display warning message.
        if (PolicyFields.getPolicyCycleCode(inputRecord).isQuote()) {
            boolean isInactiveAssociated = getTransactionDAO().isInactiveAssociated(inputRecord);
            if (!isInactiveAssociated) {
                warningsFound = true;
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "loadSaveOptions", "Business Rule 9:  associated inactive risk is required.");
                }
                if (displayWarnings.equalsIgnoreCase("N")) {
                    messageManager.addInfoMessage("pm.transactionmgr.loadSaveOptions.associatedInactiveRequired.info");
                }
            }
        }

        // If any warnings exist, only WIP is available
        if (warningsFound) {
            saveOptions.clear();
        }

        l.exiting(getClass().getName(), "loadSaveOptions", saveOptions);
        return saveOptions;
    }

    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the common save functionality for all types of save actions.
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @param inputRecord  Record containing policy header summary information about the
     */
    public void processSaveTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveTransaction", new Object[]{policyHeader, inputRecord});

        // First perform the common save processing in a db wrapped transactional unit of work
        TransactionSaveProcessor saveProcessor = (TransactionSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
        saveProcessor.saveTransaction(inputRecord);

        String saveOption = inputRecord.getStringValue("newSaveOption");
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();

        // Process save based upon save option chosen by the user
        if (saveOption.equalsIgnoreCase("WIP")) {
            JobCategory jobCategory = m_jobQueueManager.getJobCategoryEvaluator().evaluate();

            // Do not go with long running transaction if it is called from sub tab auto save
            if (jobCategory.isShort() || TransactionFields.getCallFromSubTabB(inputRecord)) {
                // Check if this is a rate request
                if (inputRecord.getBooleanValue("processRatingB", false).booleanValue()) {
                    wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                        RATE_WORKFLOW_PROCESS,
                        RATE_INITIAL_STATE);
                    wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "processOutput", YesNoFlag.Y);
                    wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "showViewPremium", YesNoFlag.Y);
                }else{
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "processSaveTransaction", "Before calling saveTransactionWip->policyHeader.isPreviewRequest():" + policyHeader.isPreviewRequest());
                    }
                    saveProcessor.saveTransactionAsWip(inputRecord);
                    // For smaller job, add the IS_SAVE_REQUEST_COMPLETED_SUCCESSFULLY into user session, so that PREVIEW
                    // request can start.
                    // For larger job (large policies), the same is added as part of the workflow exit process - exitFromInvokeSaveWipProcess.
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "processSaveTransaction", "After calling saveTransactionWip->policyHeader.isPreviewRequest():" + policyHeader.isPreviewRequest());
                    }
                    // The preview indicator in policy header may get stale when refreshPolicyLock gets fired.
                    // If this is truly a preview request, then update the preview indicator again in policy header and cache it.
                    if (!policyHeader.isPreviewRequest() && inputRecord.getStringValue(RequestIds.IS_PREVIEW_REQUEST, "N").equalsIgnoreCase("Y")) {

                        policyHeader.setPreviewRequest(true);
                        //Update the Cache in session and request
                        RequestStorageManager.getInstance().set(RequestStorageIds.POLICY_HEADER, policyHeader);
                        UserSessionManager.getInstance().getUserSession().set(UserSessionIds.POLICY_HEADER, policyHeader);

                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "processSaveTransaction", "After resetting the preview indicator in policy header->policyHeader.isPreviewRequest():" + policyHeader.isPreviewRequest());
                        }
                    }
                    if (policyHeader.isPreviewRequest()) {
                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "processSaveTransaction", "Adding " + IS_SAVE_REQUEST_COMPLETED_SUCCESSFULLY + " into User Session");
                        }
                        UserSessionManager.getInstance().getUserSession().set(RequestIds.IS_PREVIEW_REQUEST, YesNoFlag.getInstance(policyHeader.isPreviewRequest()));
                        UserSessionManager.getInstance().getUserSession().set(IS_SAVE_REQUEST_COMPLETED_SUCCESSFULLY, YesNoFlag.Y);
                    }
                }
            }
            else {
                wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                    SAVE_WIP_WORKFLOW_PROCESS,
                    SAVE_WIP_INITIAL_STATE);
                if (inputRecord.getBooleanValue("processRatingB", false).booleanValue()) {
                    wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "processRating", YesNoFlag.Y);
                    wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "showViewPremium", YesNoFlag.Y);

                }
                else {
                    wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "processRating", YesNoFlag.N);
                    wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "showViewPremium", YesNoFlag.N);
                }
            }
        }
        else if (saveOption.equalsIgnoreCase("OFFICIAL")) {
            // Initialize the new save official workflow
            wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                SAVE_OFFICIAL_WORKFLOW_PROCESS,
                SAVE_OFFICIAL_INITIAL_STATE);
            wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "relatedPoliciesTiming", "POST");
        }
        else if (saveOption.equalsIgnoreCase("ENDQUOTE") || saveOption.equalsIgnoreCase("RENQUOTE")) {
            wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                SAVE_ENDORSEMENT_QUOTE_WORKFLOW_PROCESS,
                SAVE_ENDORSEMENT_QUOTE_INITIAL_STATE);
        }
    }

    /**
     * This method is called by policy change service to save policy as official
     * @param policyHeader
     * @param inputRecord
     */
    public void processSaveTransactionOfficialForWS(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveTransactionOfficialForWS", new Object[]{inputRecord});

        TransactionSaveProcessor saveProcessor = (TransactionSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
        saveProcessor.saveTransactionAsOfficial(policyHeader, inputRecord);

        l.exiting(getClass().getName(), "loadSaveOptions");
    }

    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the save as official processing
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @param inputRecord  Record containing policy header summary information about the
     */
    public void processSaveTransactionAsOfficial(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveTransactionAsOfficial", new Object[]{inputRecord});

        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        // Perform the save official detail processing
        try {
            TransactionSaveProcessor saveProcessor = (TransactionSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
            saveProcessor.saveTransactionAsOfficial(policyHeader, inputRecord);

            // Check if amalgamation link exists
            // Get amalgamation policy No
            Record record = new Record();
            TransactionFields.setTransactionLogId(
                record, policyHeader.getLastTransactionInfo().getTransactionLogId());
            Record result = getAmalgamationLinkedPolicy(record);
            String linkedPolicyNo = result.getStringValue("policyNo");
            boolean isOriginal = YesNoFlag.getInstance(result.getStringValue("originalB")).booleanValue();
            if (!"-1".equals(linkedPolicyNo) && isOriginal) {
                wa.setWorkflowTransitionParameter(PolicyHeaderFields.getPolicyNo(inputRecord), "DEFAULT_WITH_AMALGAMATE_CONFIRM");
            }

            //For #103813 For Undo Term transaction, system enter a new flow UNDOTERM in saveAsOfficalDetailWorkflowConfig.xml
            if(inputRecord.hasStringValue("transactionCode") && "UNDOTERM".equals(inputRecord.getStringValue("transactionCode"))){
               wa.setWorkflowTransitionParameter(PolicyHeaderFields.getPolicyNo(inputRecord), "UNDOTERM");
            }
            // For Purge transaction, system enter a new flow PURGE and clear term id.
            if (inputRecord.hasStringValue(TransactionFields.TRANSACTION_CODE) &&
                TransactionCode.PURGE.getName().equals(inputRecord.getStringValue(TransactionFields.TRANSACTION_CODE))){
                wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), RequestIds.POLICY_TERM_HISTORY_ID, null);
                wa.setWorkflowTransitionParameter(PolicyHeaderFields.getPolicyNo(inputRecord), "PURGE");
            }
        }
        catch (AppException ae) {
            // An AppException indicates a failure in the Save Official.  Do not raise this error,
            // rather get the AppException message key, strip out the info needed,  and set it into workflow for the correct transition
            String messageKey = ae.getMessageKey();
            int lastPeriod = messageKey.lastIndexOf(".") + 1;
            String transitionParameter = messageKey.substring(lastPeriod);

            wa.setWorkflowTransitionParameter(PolicyHeaderFields.getPolicyNo(inputRecord), transitionParameter);

            // Finally log this exception for support purposes
            l.logp(Level.SEVERE, getClass().getName(), "processSaveTransactionAsOfficial", "Error in Save Official Processing.", ae);

        }
        catch (Exception e) {
            // Something complete unexpected (true system error)

            // First clear the workflow
            wa.clearWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord));

            // Second re-throw the exception
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error in Save Official Processing.", e);
            l.throwing(getClass().getName(), "processSaveTransactionAsOfficial", ae);
            throw ae;
        }
        
    }

    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the save as official processing
     *
     * @param policyHeader Instance of the PolicyHeader object
     */
    public String processSaveTransactionAsEndorsementQuote(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveTransactionAsEndorsementQuote", new Object[]{policyHeader});

        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        String endorsementQuoteId = null;

        // Perform the save official detail processing
        try {
            TransactionSaveProcessor saveProcessor = (TransactionSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
            endorsementQuoteId = saveProcessor.saveTransactionAsEndorsementQuote(policyHeader);
            // Set "endQuoteId" to workflow, it will be used to set correct view mode after saved endorsement quote.
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                wa.setWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.END_QUOTE_ID, endorsementQuoteId);
                wa.setWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.POLICY_VIEW_MODE, PolicyViewMode.ENDQUOTE.getName());
            }
            RequestStorageManager rsm = RequestStorageManager.getInstance();
            if (rsm.has(PROCESS_JOB)) {
                endorsementQuoteId = null;
            }
        }
        catch (AppException ae) {
            // An AppException indicates a failure in the Save Official.  Do not raise this error,
            // rather get the AppException message key, strip out the info needed,  and set it into workflow for the correct transition
            String messageKey = ae.getMessageKey();
            int lastPeriod = messageKey.lastIndexOf(".") + 1;
            String transitionParameter = messageKey.substring(lastPeriod);
            wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), transitionParameter);

            // Finally log this exception for support purposes
            l.logp(Level.SEVERE, getClass().getName(), "processSaveTransactionAsEndorsementQuote", "Error in Save Endorsement Quote Processing.", ae);

        }
        catch (Exception e) {
            // Something complete unexpected (true system error)

            // First clear the workflow
            wa.clearWorkflow(policyHeader.getPolicyNo());

            // Second re-throw the exception
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error in Save Endorsement Quote Processing.", e);
            l.throwing(getClass().getName(), "processSaveTransactionAsEndorsementQuote", ae);
            throw ae;
        }
        return endorsementQuoteId;
    }

    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the save tail transaciton as official processing
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @param inputRecord  Record containing policy header summary information about the
     */
    public void processSaveTailTransactionAsOfficial(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveTransaction", new Object[]{inputRecord});

        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        // Initialize the new save official workflow
        wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
            SAVE_TAIL_OFFICIAL_WORKFLOW_PROCESS,
            SAVE_TAIL_OFFICIAL_INITIAL_STATE);
    }

    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the save occupant cancellation transaciton as official processing
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @param inputRecord  Record containing policy header summary information about the
     */
    public void processSaveOccupantCancellationAsOfficial(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveTransaction", new Object[]{inputRecord});

        WorkflowAgent wa = WorkflowAgentImpl.getInstance();

        // Initialize the new save official workflow
        wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
            "SaveAsOfficialDetail",
            "invokeViewRelPolicyProcess");
        wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "relatedPoliciesTiming", "POST");

        //Issue153022, use renewal transaction id to save official.
        if (policyHeader.getScreenModeCode().isRenewWIP()) {
            wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "renewalTransactionId",
                policyHeader.getLastTransactionId());
        }
    }


    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the save VL Employee cancellation transaciton as official processing
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @param inputRecord  Record containing policy header summary information about the
     */
    public void processSaveVLEmployeeCancellationAsOfficial(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveTransaction", new Object[]{inputRecord});

        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        // Initialize the new save official workflow
        // Initialize the new save official workflow
        wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
            "SaveAsOfficialDetail",
            "invokeRatingMsg");
    }

    /**
     * Process the common save functionality for all types of save actions.
     *
     * @param inputRecord Record containing policy header summary information about the
     *                    transaction information being saved
     */
    public void saveTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveTransactionCommon", new Object[]{inputRecord});

        PolicyCacheManager policyCacheManager = PolicyCacheManager.getInstance();
        if (!policyCacheManager.hasReplicationConfigured()) {
            String isConfigured = getTransactionDAO().isReplicationConfigured(inputRecord);
            policyCacheManager.setReplicationConfigured(YesNoFlag.getInstance(isConfigured).booleanValue());
        }

        if (policyCacheManager.getReplicationConfigured()) {
            // Process Replication
            getTransactionDAO().doReplication(inputRecord);
        }

        if (inputRecord.getStringValue("level").equalsIgnoreCase("RISK")) {
        // Next call the slot renumbering logic
        getTransactionDAO().renumberWipSlots(inputRecord);
        }

        // Next call the WIP custom layer process
        getTransactionDAO().doWipCustomLayer(inputRecord);

        l.exiting(getClass().getName(), "saveTransactionCommon");
    }

    /**
     * Process the save as WIP specific functionality.
     *
     * @param inputRecord Record containing policy header summary information about the
     *                    transaction information being saved
     */
    public void saveTransactionAsWip(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveTransactionAsWip", new Object[]{inputRecord});

        try {
            // Next call outputs
            Record record = new Record();
            record.setFields(inputRecord);
            record.setFieldValue("parms", null);
            String transactionLogId = inputRecord.getStringValue(TransactionFields.TRANSACTION_LOG_ID);
            initWarningMessage(transactionLogId);
            // Skip extract logic if it is called from sub tab auto save
            if (!TransactionFields.getCallFromSubTabB(inputRecord)) {
                processOutput(record, false);
            }
            addWarningMessage(transactionLogId);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to process output.", e);
            l.throwing(getClass().getName(), "saveTransactionAsWip", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveTransactionAsWip");
    }

    /**
     * Process the save as Official specific functionality.
     *
     * @param policyHeader instance of the PolicyHeader for the current policy/transaction
     * @param inputRecord  Record containing policy header summary information about the
     *                     transaction information being saved
     */
    public void saveTransactionAsOfficial(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveTransactionAsOfficial", new Object[]{inputRecord});

        // Set policy header information into the record
        inputRecord.setFields(policyHeader.toRecord(), false);

        // First based upon the transaction code being saved perform
        // specialized pre-official activities
        TransactionCode transactionCode = TransactionFields.getTransactionCode(inputRecord);

        //Start Issue 153022, save official workflow invoked after cancel occupant risk in renewal wip.
        //Then the renewal transaction id should be used to save official instead of occupant cancellation transaction id.
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        String renwalTransactionId = null;
        String policyNo = PolicyHeaderFields.getPolicyNo(inputRecord);
        if (wa.hasWorkflow(policyNo) && wa.hasWorkflowAttribute(policyNo, "renewalTransactionId")) {
            renwalTransactionId = (String) wa.getWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "renewalTransactionId");
        }
        if (!StringUtils.isBlank(renwalTransactionId) && policyHeader.getScreenModeCode().isRenewWIP()) {
            TransactionFields.setTransactionLogId(inputRecord, renwalTransactionId);
        }
        //End issue 153022

        try {
            if (transactionCode.isRenewal()) {

                // For Renewals (auto or manual) handle cancellations during renewal
                getTransactionDAO().performRenewalRiskCancelFinal(inputRecord);

            }
            else if (transactionCode.isCancellation() ||
                transactionCode.isRiskCancel() ||
                transactionCode.isPurge() ||
                transactionCode.isExtendToCancel() ||
                transactionCode.isDeclinePol()) {

                // For cancellations, handle specific activites
                getTransactionDAO().performPolicyCancelFinal(inputRecord);

            }
            else if (transactionCode.isReinstate() ||
                transactionCode.isRiskReinst() ||
                transactionCode.isCovgReinst()) {

                // For reinstatements, handle specific activites
                getTransactionDAO().performReinstateFinal(inputRecord);
            }

            // Next update the transaction status to OFFICIAL
            updateTransactionStatusWithLock(inputRecord, TransactionStatus.OFFICIAL);

            // Finally invoke the call to Issue_Policy
            PMCommonFields.setRecordModeCode(inputRecord, RecordMode.OFFICIAL);
            inputRecord.setFieldValue("transStatus", "OFFICIAL");
            RecordSet relatedPolicyErrors = getTransactionDAO().issuePolicy(inputRecord);

            // If any related policy errors exists store this in the RSM for later display
            if (relatedPolicyErrors.getSize() > 0) {
                RequestStorageManager.getInstance().set(RELATED_POLICY_ERRORS_RECORDSET, relatedPolicyErrors);
                wa.setWorkflowTransitionParameter(PolicyHeaderFields.getPolicyNo(inputRecord), "RELATED_POLICY_DISPLAY");
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "pm.workflowmgr.save.official.processSaveOfficial.info.SAVE_ERROR", "Save error found", e);
            l.throwing(getClass().getName(), "saveTransactionAsOfficial", ae);
            throw ae;
        }

        try {
            // Validate if any PM/FM Discrepancies exist
            validatePmFmDiscrepancy(inputRecord, true);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.PM_FM_DISCREPANCY", "PM FM Discrepancy found", e);
            l.throwing(getClass().getName(), "saveTransactionAsOfficial", ae);
            throw ae;
        }

        try {
            // Process Billing
            processBilling(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.BILLING_ERROR", "Billing error found", e);
            l.throwing(getClass().getName(), "saveTransactionAsOfficial", ae);
            throw ae;
        }

        try {
            // Process Output
            processOutput(inputRecord, true);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.OUTPUT_ERROR", "Output error found", e);
            l.throwing(getClass().getName(), "saveTransactionAsOfficial", ae);
            throw ae;
        }

        try {
            // Process unlock
            getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from saveTransactionAsOfficial after Output");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.UNLOCK_ERROR", "Unlock error found", e);
            l.throwing(getClass().getName(), "saveTransactionAsOfficial", ae);
            throw ae;
        }

        try {
            // Update transaction status to COMPLETE
            Transaction t = updateTransactionStatusWithLock(inputRecord, TransactionStatus.COMPLETE);

            // Unlock official policy if other thread happened to lock policy before transaction is completed.
            getLockManager().unlockOfficialPolicy(policyHeader.getPolicyId());
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.COMPLETE_ERROR", "Complete error found", e);
            l.throwing(getClass().getName(), "saveTransactionAsOfficial", ae);
            throw ae;
        }

        //Adjust future terms' GR indicator difference between term and risks if current transaction is a OOSE.
        YesNoFlag adjustFutureGrB = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_ADJUST_FUTURE_GR", "N"));
        if (adjustFutureGrB.booleanValue() && transactionCode.isOosEndorsement()) {
            getTransactionDAO().adjustFutureTermsGr(inputRecord);
        }

        l.exiting(getClass().getName(), "saveTransactionAsOfficial");
    }

    /**
     * Process the save as endquote specific functionality.
     *
     * @param policyHeader instance of the PolicyHeader for the current policy/transaction
     * @return endorsmentQuoteId
     */
    public String saveTransactionAsEndorsementQuote(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "saveTransactionAsEndorsmentQuote", new Object[]{policyHeader});
        // Set policy header information into the record
        Record inputRecord = policyHeader.toRecord();
        String endorsmentQuoteId;
        String quoteType = "endorsement";
        TransactionTypeCode transactionTypeCode = TransactionFields.getTransactionTypeCode(inputRecord);
        if (transactionTypeCode.isRenewal()) {
            quoteType = "renewal";
        }
        try {
            initWarningMessage(policyHeader.getLastTransactionId());
            inputRecord.setFieldValue("policyCycle", "ENDQUOTE");
            //get endquoteid
            endorsmentQuoteId = getTransactionDAO().getEndorsementQuoteId(inputRecord);
            // Next update the transaction status to endquote
            updateTransactionStatusWithLock(inputRecord, TransactionStatus.ENDQUOTE);
            //save endorsement quote
            inputRecord.setFieldValue("endquoteId", endorsmentQuoteId);
            getTransactionDAO().saveAsEndorsementQuote(inputRecord);
            // Process output (PM Call to Output)
            Record record = new Record();
            record.setFields(policyHeader.toRecord());
            processOutput(record, false);            
            // Process unlock
            getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from saveTransactionAsEndorsementQuote after Output");
            // Update transaction status to COMPLETE
            Transaction t = updateTransactionStatusWithLock(inputRecord, TransactionStatus.COMPLETE);
            // Unlock official policy if other thread happened to lock policy before transaction is completed.
            getLockManager().unlockOfficialPolicy(policyHeader.getPolicyId());
        }
        catch (Exception e) {
            MessageManager.getInstance().addInfoMessage("pm.workflowmgr.save.endQuote.processSaveEndQuote.info.SAVE_ERROR", new Object[]{quoteType});
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.endQuote.processSaveEndQuote.info.SAVE_ERROR", "Save EndQuote Error ", e);
            l.throwing(getClass().getName(), "saveTransactionAsEndorsementQuote", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveTransactionAsEndorsementQuote");
        return endorsmentQuoteId;
    }

    /**
     * Private method to determine if any discrepancies exist between PM and FM
     *
     * @param inputRecord a record containing transactional information
     * @param storeInRSM  store the resulting recordset in the RequestStorageManager
     */
    private void validatePmFmDiscrepancy(Record inputRecord, boolean storeInRSM) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPmFmDiscrepancyExist", new Object[]{inputRecord, Boolean.valueOf(storeInRSM)});
        }

        if (PolicyFields.getPolicyCycleCode(inputRecord).isPolicy() &&
            !YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_FM_NO_BILLING", "N")).booleanValue() &&
            YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_FM_VERIFY_DELTA", "Y")).booleanValue()) {

            RecordSet rs = getTransactionDAO().checkPremiumDelta(inputRecord);
            if (rs.getSize() > 0) {

                // If I need to store this in the Request Storage Manager, do it now
                if (storeInRSM) {
                    RequestStorageManager.getInstance().set(DISCREPANCY_COMPARE_RECORDSET, rs);

                    RecordSet rst = loadDiscrepancyTransCompareInfo(inputRecord);
                    if (rst.getSize()>0) {
                        // Load remaining transactional compare data as well
                        RequestStorageManager.getInstance().set(DISCREPANCY_TRANS_RECORDSET, loadDiscrepancyTransCompareInfo(inputRecord));
                    }
                }

                throw new ValidationException("PM/FM discrepancy exists");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPmFmDiscrepancyExist");
        }
    }

    /**
     * Private method to perform billing interface between PM and FM
     *
     * @param inputRecord a record containing transactional information
     */
    private void processBilling(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processBilling", new Object[]{inputRecord});
        }

        // Call the DAO
        Record resultRecord = getTransactionDAO().processBilling(inputRecord);
        int returnCode = 0;
        if (resultRecord.hasStringValue("returnCode")) {
            returnCode = resultRecord.getIntegerValue("returnCode").intValue();
        }
        // Return code less than 0 indicates problem
        if (returnCode < 0) {
            String errorMessage = resultRecord.getStringValue("errMsg");
            MessageManager.getInstance().addErrorMessage("pm.workflowmgr.save.official.processBilling.error",
                new String[]{errorMessage});
            throw new ValidationException("BILLING_ERROR");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processBilling");
        }
    }

    /**
     * perform OS integration with PM
     *
     * @param inputRecord             a record containing transactional information
     * @param updateTransactionStatus boolean indicating if the status of the transaction should be udpated to OUTPUT
     */
    public void processOutput(Record inputRecord, boolean updateTransactionStatus) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOutput", new Object[]{inputRecord, Boolean.valueOf(updateTransactionStatus)});
        }

        // Handle the optional update of transaction status
        if (updateTransactionStatus) {
            // Setup the transaction object
            Transaction outputTrans = new Transaction();
            outputTrans.setTransactionLogId(TransactionFields.getTransactionLogId(inputRecord));
            outputTrans.setTransactionCode(TransactionFields.getTransactionCode(inputRecord));

            // Update the transaction status in the database
            updateTransactionStatusWithLock(outputTrans, TransactionStatus.OUTPUT);
        }

        // Call the DAO
        int returnCode = getTransactionDAO().processOutput(inputRecord);

        // Return code less than 0 indicates problem
        if (returnCode < 0) {
            throw new ValidationException("OUTPUT_ERROR");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processOutput");
        }
    }

    /**
     * private method  to determine if the transactionCode contained within the given record is a endorsement-like transationCode:
     * (oosEndorsement, Endorsement). for these transactions, the validation logic is slightly different.
     *
     * @param inputRecord a record containing transactionCode field
     * @return
     */
    private boolean isEndorseTransactionCode(Record inputRecord) {
        boolean endorseTransactionCode = false;
        if (TransactionFields.hasTransactionCode(inputRecord)) {
            TransactionCode transactionCode = TransactionFields.getTransactionCode(inputRecord);
            if (transactionCode.isEndorsement() ||
                transactionCode.isOosEndorsement()) {
                endorseTransactionCode = true;
            }
        }
        return endorseTransactionCode;
    }

    /**
     * Private method to determine if the transactionCode contained within the given record is endAffilia.
     *
     * @param inputRecord a record containing transactionCode field
     * @return
     */              
    private boolean isEndAffiliaTransactionCode(Record inputRecord) {
        boolean endAffiliaTransactionCode = false;
        if (TransactionFields.hasTransactionCode(inputRecord)) {
            TransactionCode transactionCode = TransactionFields.getTransactionCode(inputRecord);
            if (transactionCode.isEndAffilia()) {
                endAffiliaTransactionCode = true;
            }
        }
        return endAffiliaTransactionCode;
    }

    /**
     * check if the renewal reason is configured as an endorsement reason
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isEndorsementCodeConfigured(Record inputRecord) {
        return getTransactionDAO().isEndorsementCodeConfiged(inputRecord);
    }

    /**
     * Validate transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return String
     */
    public String performTransactionValidation(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "performTransactionValidation");

        String returnValue = getTransactionDAO().validateTransaction(inputRecord);

        if (returnValue.equals("FAILED")) {
            MessageManager.getInstance().addErrorMessage("pm.validateAndRateTransaction.error.validation");
        }
        else if (returnValue.equals("MTFAILED")) {
            MessageManager.getInstance().addErrorMessage("pm.validateAndRateTransaction.error.miniTail");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performTransactionValidation", returnValue);
        }

        return returnValue;
    }

    /**
     * Returns a RecordSet loaded with validation errors
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return RecordSet
     */
    public RecordSet loadAllValidationError(Record inputRecord) {
        return getTransactionDAO().loadAllValidationError(inputRecord);
    }


    /**
     * Clean up all validation error
     *
     * @param inputRecord
     */
    public void deleteAllValidationError(Record inputRecord) {
        getTransactionDAO().deleteAllValidationError(inputRecord);
    }

    /**
     * Check if taxes are configured for the current customer.
     * <p/>
     *
     * @return boolean
     */
    public boolean isTaxConfigured() {
        return YesNoFlag.getInstance(getTransactionDAO().isTaxConfigured()).booleanValue();
    }


    /**
     * Check if charges fees are configured for the current customer.
     * <p/>
     *
     * @return boolean
     */
    public boolean isChargesNFeesConfigured() {
        return YesNoFlag.getInstance(getTransactionDAO().isChargesNFeesConfigured()).booleanValue();
    }

    /**
     * Check if fee exists related to the current transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return YesNoFlag
     */
    public YesNoFlag isFeeDefined(Record inputRecord) {
        return getTransactionDAO().isFeeDefined(inputRecord);
    }

    /**
     * Waive fee related to the current transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     */
    public void performFeeWaive(Record inputRecord) {
        getTransactionDAO().waiveFee(inputRecord);
    }

    /**
     * Check if tax is configured for a state
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return YesNoFlag
     */
    public YesNoFlag isPolicyTaxConfigured(Record inputRecord) {
        return getTransactionDAO().isPolicyTaxConfigured(inputRecord);
    }

    /**
     * Validate premium
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return int
     */
    public int performPremiumValidation(Record inputRecord) {
        return getTransactionDAO().validatePremium(inputRecord);
    }

    /**
     * Check if the open claims validation is configured for save official
     * <p/>
     *
     * @return YesNoFlag
     */
    public YesNoFlag isOpenClaimsValidationConfigured() {
        Logger l = LogUtils.enterLog(getClass(), "isOpenClaimsValidationConfigured");

        SysParmProvider sysParm = SysParmProvider.getInstance();
        YesNoFlag returnValue = YesNoFlag.getInstance(sysParm.getSysParm("PM_OPEN_CLAIMS_MSG", "N"));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOpenClaimsValidationConfigured", returnValue);
        }

        return returnValue;
    }

    /**
     * Validates if open claims exist for the changed risk/coverage's of the current transaction.
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     */
    public void validateOpenClaims(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateOpenClaims", new Object[]{inputRecord});
        }

        // First check if validation is configured
        YesNoFlag isValidationConfigured = isOpenClaimsValidationConfigured();

        if (isValidationConfigured.equals(YesNoFlag.Y)) {
            // Confirm if exist
            if (!inputRecord.hasStringValue(ValidateOpenClaimsMessageIds.CONFIRM_OPEN_CLAIMS + ".confirmed")) {

                // Validate open claims
                YesNoFlag result = getTransactionDAO().validateOpenClaims(inputRecord);

                // Add confirmation prompt if validation fails
                if (result.equals(YesNoFlag.Y)) {
                    MessageManager.getInstance().addConfirmationPrompt(ValidateOpenClaimsMessageIds.CONFIRM_OPEN_CLAIMS);
                    throw new ValidationException("Open claims exist.");
                }
                else {
                    // Create dummy confirmed field to move on in the process
                    inputRecord.setFieldValue(ValidateOpenClaimsMessageIds.CONFIRM_OPEN_CLAIMS + ".confirmed", "Y");
                }
            }
        }
        else {
            // Create dummy confirmed field to move on in the process
            inputRecord.setFieldValue(ValidateOpenClaimsMessageIds.CONFIRM_OPEN_CLAIMS + ".confirmed", "Y");
        }

        l.exiting(getClass().getName(), "validateOpenClaims");
    }

    /**
     * Get OOSE Expiration Date
     *
     * @param policyHeader
     * @return
     */
    public String getOoseExpirationDate(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "getOoseExpirationDate");

        SysParmProvider sysParm = SysParmProvider.getInstance();
        String oosDefExpDate = sysParm.getSysParm(SysParmIds.PM_OOS_DEF_EXP_DATE, SysParmIds.OosDefaultExpDateValues.TERM);
        String expirationDate = null;
        if (oosDefExpDate.equalsIgnoreCase(SysParmIds.OosDefaultExpDateValues.TERM)) {
            expirationDate = policyHeader.getTermEffectiveToDate();
        }
        else if (oosDefExpDate.equalsIgnoreCase(SysParmIds.OosDefaultExpDateValues.POLICY)) {
            Iterator iter = policyHeader.getPolicyTerms();
            if (iter.hasNext()) {
                Term lastTerm = (Term) iter.next();
                expirationDate = lastTerm.getEffectiveToDate();
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOoseExpirationDate", expirationDate);
        }
        return expirationDate;
    }

    /**
     * Rate transaction
     * 
     * The code that calls the this method should check isRatingLongRunning() first,
     * and initialize the appropriate workflow if it is.
     * Otherwise, if isRatingLongRunning() returns false, it can simply call this method to rate transaction.
     * </p>
     *
     * @param inputRecord Record containing current transaction information
     * @return String
     */
    public String performTransactionRating(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "performTransactionRating");
        String returnValue = "SUCCESS";
        try {
            AddressWebService addressWebService = AddressWebService.getAddressWebService();
            if (addressWebService.isEnable()) {
                returnValue = performTaxUpdate(addressWebService, inputRecord);
            }
        }
        catch (Exception e) {
            l.throwing(getClass().getName(), "performTransactionRating", e);
            e.printStackTrace();
            returnValue = "FAILED";
            MessageManager.getInstance().addErrorMessage("pm.validateAndRateTransaction.error.tax.update");
        }

        if ("SUCCESS".equals(returnValue)) {
            try {
                returnValue = getTransactionDAO().rateTransaction(inputRecord);

                if (returnValue.equals("0")) {
                    returnValue = "SUCCESS";
                }
                else {
                    returnValue = "FAILED";
                    MessageManager.getInstance().addErrorMessage("pm.validateAndRateTransaction.error.rating");
                }
            }
            catch (Exception e) {
                l.throwing(getClass().getName(), "performTransactionRating", e);
                returnValue = "FAILED";
                MessageManager.getInstance().addErrorMessage("pm.validateAndRateTransaction.error.rating");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performTransactionRating", returnValue);
        }
        return returnValue;
    }

    /**
     * Function to perform tax update.
     * </p>
     *
     * @param record Record containing current transaction information
     */
    public String performTaxUpdate(AddressWebService addressWebService, Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performTaxUpdate", new Object[]{record});
        }

        String returnCode = "SUCCESS";
/*
        try {
            RecordSet rs = getTransactionDAO().checkTaxUpdates(record);

            Record taxUpdatesRecord;
            Record webServiceResponseRecord = null;

            Iterator it = rs.getRecords();
            while (it.hasNext()) {
                taxUpdatesRecord = (Record)it.next();

                webServiceResponseRecord = addressWebService.runWebService(taxUpdatesRecord);
                webServiceResponseRecord.setFields(taxUpdatesRecord);

                if ("SUCCESS".equalsIgnoreCase((String)webServiceResponseRecord.getFieldValue("returnCode"))) {
                    getTransactionDAO().applyTaxUpdates(webServiceResponseRecord);
                }
                else {
                    returnCode = "FAILED";
                    break;
                }
            }
        }
        catch (Exception e) {
            l.throwing(getClass().getName(), "performTaxUpdate", e);
            e.printStackTrace();
            MessageManager.getInstance().addErrorMessage("pm.validateAndRateTransaction.error.tax.update");
        }
*/

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performTaxUpdate", returnCode);
        }

        return returnCode;
    }

    /**
     * return a booelan value to indicates if the rating process is a long running process
     *
     * @return boolean
     */
    public boolean isRatingLongRunning() {
        Logger l = LogUtils.getLogger(getClass());
        boolean isRatingLongRunning = false;
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRatingLongRunning", new Object[]{});
        }

        JobCategory jobCategory = getJobQueueManager().getJobCategoryEvaluator().evaluate();
        if (!jobCategory.isShort()) {
            isRatingLongRunning = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isRatingLongRunning", String.valueOf(isRatingLongRunning));
        }

        return isRatingLongRunning;
    }

    /**
     * Returns a RecordSet loaded with product notifications
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return RecordSet
     */
    public RecordSet loadAllProductNotifications(Record inputRecord) {
        return getTransactionDAO().loadAllProductNotifications(inputRecord);
    }

    /**
     * Returns indicator for next step based on the user response
     * <p/>
     *
     * @param inputRecord Record containing current transaction information and product notification response
     * @return int
     */
    public String performProductNotificationResponse(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "performProductNotificationResponse");

        String returnValue = "VALID";

        // Get the notification one more time since we don't keep the non-confirmation notifications
        Record policyRecord = policyHeader.toRecord();
        policyRecord.setFieldValue("notifyLevel", inputRecord.getStringValue("notifyLevel"));
        RecordSet rs = getTransactionDAO().loadAllProductNotifications(policyRecord);

        // Process responses
        int notifyCount = rs.getSize();
        if (notifyCount > 0) {
            for (int i = 0; i < notifyCount; i++) {
                Record r = rs.getRecord(i);

                if (NotifyFields.getStatus(r).equals("VALID")) {
                    // Get the notification ID
                    String productNotifyId = NotifyFields.getProductNotifyId(r);

                    // Get the default value
                    String fieldValue = NotifyFields.getDefaultValue(r);

                    // Replace default value with user response if we got one
                    String fieldName = productNotifyId + ".confirmed";
                    if (inputRecord.hasStringValue(fieldName)) {
                        fieldValue = inputRecord.getStringValue(fieldName).equals("Y") ? "1" : "2";
                        NotifyFields.setResponseType(r, ResponseTypeEnum.USER.getResponseTypeValue());
                        NotifyFields.setResponse(r, fieldValue);
                    }

                    if (returnValue.equals("VALID")) {
                        // Get the next step indicator from the system
                        Record record = new Record();
                        record.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
                        record.setFieldValue(PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE, policyHeader.getTermEffectiveFromDate());
                        record.setFieldValue(PolicyHeaderFields.TERM_EFFECTIVE_TO_DATE, policyHeader.getTermEffectiveToDate());
                        record.setFieldValue(NotifyFields.PRODUCT_NOTIFY_ID, productNotifyId);
                        record.setFieldValue("userResponse", fieldValue);
                        returnValue = getTransactionDAO().productNotificationResponse(record) == 0 ? "VALID" : "INVALID";
                    }
                }
            }
            // Save soft validation
            rs.setFieldValueOnAll("notifyLevel", inputRecord.getStringValue("notifyLevel"));
            getSoftValidationManager().processSoftValidation(policyHeader, rs);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performProductNotificationResponse", returnValue);
        }
        return returnValue;
    }

    /**
     * Get initial values for change term effective date and term expiration date
     *
     * @param policyHeader policy header
     * @return default values for change term date
     */
    public Record getInitialValueForChangeTermDates(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValueForChangeTermDates", new Object[]{policyHeader});

        Record outputRecord = new Record();
        PolicyFields.setNewTermEffDate(outputRecord, policyHeader.getTermEffectiveFromDate());
        PolicyFields.setNewTermExpDate(outputRecord, policyHeader.getTermEffectiveToDate());

        l.exiting(getClass().getName(), "getInitialValueForChangeTermDates", new Object[]{outputRecord});
        return outputRecord;
    }

    /**
     * Save the changes of term dates
     *
     * @param policyHeader policy header
     * @param inputRecord
     */
    public void saveTermDates(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveTermDates", new Object[]{policyHeader, inputRecord});

        validateTermDates(policyHeader, inputRecord);

        inputRecord.setFields(policyHeader.toRecord(), false);

        // Save term dates
        getTransactionDAO().saveTermDates(inputRecord);
        setPolicyHeaderReloadCode(policyHeader, TransactionCode.NEWBUS);

        // Update billing account
        getBillingManager().updateBillingAccount(inputRecord);

        l.exiting(getClass().getName(), "saveTermDates");
    }

    /**
     * Load discrepancy summary information for the PM/FM Discrepancy page.
     *
     * @param inputRecord record of transaction information being saved
     * @return Record containing discrepancy summary information
     */
    public Record loadDiscrepancySummaryInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDiscrepancySummaryInfo", new Object[]{inputRecord});

        StringBuffer interfaceMsg = new StringBuffer();
        interfaceMsg.append("PM Transaction waits for FM processing to complete before Saving as Official.");
        Record outputRec = new Record();

        // First determine system parameter PM_WAIT_FOR_FM setting
        boolean waitSysparm = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_WAIT_FOR_FM")).booleanValue();

        if (!waitSysparm) {
            interfaceMsg.append(" PM Transaction is saved as Official.  FM transactions are submitted to a Queue.");

            // Get the interface status for the queue
            inputRecord.setFieldValue("mode", "STATUS");
            String interfaceMsgAppend = getTransactionDAO().loadDiscrepancyInterfaceStatus(inputRecord);

            if (!StringUtils.isBlank(interfaceMsgAppend)) {
                interfaceMsg.append(" ");
                interfaceMsg.append(interfaceMsgAppend);
            }
        }

        // Setup the output record
        outputRec.setFieldValue("pmFmInterfaceMsg", interfaceMsg);

        l.exiting(getClass().getName(), "loadDiscrepancySummaryInfo", outputRec);
        return outputRec;
    }

    /**
     * Load discrepancy comparisson information for the PM/FM Discrepancy page.
     *
     * @param inputRecord record of transaction information being saved
     * @return RecordSet containing discrepancy compare information
     */
    public RecordSet loadDiscrepancyCompareInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDiscrepancyCompareInfo", new Object[]{inputRecord});

        RecordSet rs;

        // First check if the information exists in the RSM
        if (RequestStorageManager.getInstance().has(DISCREPANCY_COMPARE_RECORDSET)) {
            rs = (RecordSet) RequestStorageManager.getInstance().get(DISCREPANCY_COMPARE_RECORDSET);
        }
        else {
            rs = getTransactionDAO().loadDiscrepancyCompareInfo(inputRecord);
        }

        l.exiting(getClass().getName(), "loadDiscrepancyCompareInfo", rs);
        return rs;
    }

    /**
     * Load discrepancy transaction information for the PM/FM Discrepancy page.
     *
     * @param inputRecord record of transaction information being saved
     * @return RecordSet containing discrepancy transaction information
     */
    public RecordSet loadDiscrepancyTransCompareInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDiscrepancyTransCompareInfo", new Object[]{inputRecord});

        RecordSet rs;

        // First check if the information exists in the RSM
        if (RequestStorageManager.getInstance().has(DISCREPANCY_TRANS_RECORDSET)) {
            rs = (RecordSet) RequestStorageManager.getInstance().get(DISCREPANCY_TRANS_RECORDSET);
        }
        else {
            rs = getTransactionDAO().loadDiscrepancyTransCompareInfo(inputRecord);
        }

        l.exiting(getClass().getName(), "loadDiscrepancyTransCompareInfo", rs);
        return rs;
    }

    /**
     * Load discrepancy interface information for the PM/FM Discrepancy page.
     *
     * @param inputRecord record of transaction information being saved
     * @return RecordSet containing discrepancy interface information
     */
    public RecordSet loadDiscrepancyIntfcInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDiscrepancyIntfcInfo", new Object[]{inputRecord});

        RecordSet rs = getTransactionDAO().loadDiscrepancyIntfcInfo(inputRecord);

        l.exiting(getClass().getName(), "loadDiscrepancyIntfcInfo", rs);
        return rs;
    }

    /**
     * Wrapper to load related policy errors from the save official process.
     * Current implementation obtains this information from the RequestStorageManager;
     *
     * @param inputRecord record of transaction information being saved
     * @return RecordSet containing related policy error information
     */
    public RecordSet loadRelatedPolicySaveError(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadRelatedPolicySaveError", new Object[]{inputRecord});

        RecordSet rs;

        // First check if the information exists in the RSM
        if (RequestStorageManager.getInstance().has(RELATED_POLICY_ERRORS_RECORDSET)) {
            rs = (RecordSet) RequestStorageManager.getInstance().get(RELATED_POLICY_ERRORS_RECORDSET);
        }
        else if (UserSessionManager.getInstance().getUserSession().has(RELATED_POLICY_ERRORS_RECORDSET)) {
            rs = (RecordSet) UserSessionManager.getInstance().getUserSession().get(RELATED_POLICY_ERRORS_RECORDSET);
            UserSessionManager.getInstance().getUserSession().remove(RELATED_POLICY_ERRORS_RECORDSET);
        }
        else {
            throw new AppException("Unable to obtain the related policies recordset from the RSM.");
        }
        
        l.exiting(getClass().getName(), "loadRelatedPolicySaveError", rs);
        return rs;
    }

    /**
     * save all transaction data
     * <p/>
     *
     * @param inputRecords the transaction needed to save
     */
    public void saveTransactionDetail(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveTransactionDetail", new Object[]{inputRecords});
        }

        // Save changed records only
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(new String[]{UpdateIndicator.UPDATED}));
        getTransactionDAO().saveTransactionDetail(changedRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveTransactionDetail");
        }
    }

    /**
     * load all transaction data for policy
     * <p/>
     *
     * @param policyHeader policy header
     * @return the result
     */
    public RecordSet loadAllTransaction(PolicyHeader policyHeader) {
        Record inputRecord = new Record();
        TransactionFields.setShowAllOrShowTerm(inputRecord, TransactionFields.ShowAllOrShowTermValues.ALL);
        return loadAllTransaction(policyHeader, inputRecord);
        }

    /**
     * load all transaction data for policy or load transaction data for a particular term
     * <p/>
     *
     * @param policyHeader policy header
     * @param inputRecord
     * @return the result
     */
    public RecordSet loadAllTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransaction", new Object[]{policyHeader, inputRecord});
        }

        Record rec = new Record();
        PolicyHeaderFields.setPolicyId(rec, policyHeader.getPolicyId());
        String showAllOrShowTerm = TransactionFields.ShowAllOrShowTermValues.TERM;
        if (inputRecord != null && inputRecord.hasStringValue(TransactionFields.SHOW_ALL_OR_SHOW_TERM)) {
            showAllOrShowTerm = TransactionFields.getShowAllOrShowTerm(inputRecord);
        }
        TransactionFields.setShowAllOrShowTerm(rec, showAllOrShowTerm);

        if (TransactionFields.ShowAllOrShowTermValues.ALL.equals(showAllOrShowTerm)) {
            PolicyHeaderFields.setTermBaseRecordId(rec, "0");
        }
        else if (TransactionFields.ShowAllOrShowTermValues.TERM.equals(showAllOrShowTerm)) {
            PolicyHeaderFields.setTermBaseRecordId(rec, policyHeader.getTermBaseRecordId());
        // Since PolicyHeader contains info for the term that is currently displayed in Policy Folder,
        // when retrieving data for transaction snapshot we need to replace the term base record FK in
        // it with the term selected from the transaction snapshot. In this case the termBaseRecordId
        // has been specifically passed in the input record.
            if (inputRecord != null && inputRecord.hasStringValue(PolicyHeaderFields.TERM_BASE_RECORD_ID)) {
                PolicyHeaderFields.setTermBaseRecordId(rec, PolicyHeaderFields.getTermBaseRecordId(inputRecord));
            }
        }

        // Determine if Entity Detail button is show or hide.
        String entityAvailable = isEntityDetailAvailable(policyHeader);
        RecordLoadProcessor loadProcessor = new LoadAllTransactionEntitlementRecordLoadProcessor(rec, entityAvailable);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, new AddRowNoLoadProcessor());
        RecordSet resultSet = getTransactionDAO().loadAllTransaction(rec, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransaction", resultSet);
        }
        return resultSet;
    }

    /**
     * load transaction data by transaction id
     *
     * @param inputRecord
     * @return transaction information
     */
    public Record loadTransactionById(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTransactionById", new Object[]{inputRecord});
        }

        Record result = getTransactionDAO().loadTransactionById(inputRecord);
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTransactionById", new Object[]{result});
        }
        return result;
    }

    /**
     * load the change detail data
     *
     * @param inputRecord input record
     * @return the change detail data
     */
    public RecordSet loadAllChangeDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllChangeDetail", new Object[]{inputRecord});
        }
        RecordSet resultSet = getTransactionDAO().loadAllChangeDetail(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllChangeDetail", new Object[]{resultSet});
        }
        return resultSet;
    }

    /**
     * load the transaction form data
     *
     * @param inputRecord input record
     * @return the transaction form data
     */
    public RecordSet loadAllTransactionForm(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransactionForm", new Object[]{inputRecord});
        }
        RecordSet resultSet = getTransactionDAO().loadAllTransactionForm(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransactionForm", new Object[]{resultSet});
        }
        return resultSet;
    }

    /**
     * Check if OOS Endorsement is avaliable or not.
     *
     * @param inputRecord
     * @return
     */
    public boolean isOosEndorsementAvailable(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isOosEndorsementAvailable", new Object[]{inputRecord});

        boolean isAvailable;
        SysParmProvider sysParm = SysParmProvider.getInstance();
        String endorsePriorTerm = sysParm.getSysParm("PM_ENDORSE_PRIORTERM");
        inputRecord.setFieldValue("endorsePriorTerm", endorsePriorTerm);
        Record record = getTransactionDAO().vaidateOosEndorseTerm(inputRecord);
        isAvailable = YesNoFlag.getInstance(record.getStringValue("returnValue")).booleanValue();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOosEndorsementAvailable", Boolean.valueOf(isAvailable));
        }

        return isAvailable;
    }

    /**
     * Check if Billing Setup is available or not.
     *
     * @param policyHeader
     * @return
     */
    public boolean isBillingSetupAvailable(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "isBillingSetupAvailable", new Object[]{policyHeader});

        boolean isAvailable = false;
        PolicyCycleCode policyCycle = policyHeader.getPolicyCycleCode();

        if (policyCycle.isPolicy()) {
            isAvailable = true;
        }
        else if (policyCycle.isQuote()) {
            if (SysParmProvider.getInstance().getSysParm("PM_QT_BILL_SETUP", "N").equalsIgnoreCase("Y")) {
                isAvailable = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isBillingSetupAvailable", Boolean.valueOf(isAvailable));
        }

        return isAvailable;
    }

    /**
     * Validate the changed term dates
     *
     * @param policyHeader
     * @param inputRecord
     */
    protected void validateTermDates(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateTermDates", new Object[]{inputRecord});
        }

        // Validation #1: Term Effective Date and Term Expiration Date Validations
        String sTermEffDate = PolicyFields.getNewTermEffDate(inputRecord);
        String sTermExpDate = PolicyFields.getNewTermExpDate(inputRecord);
        if (StringUtils.isBlank(sTermEffDate)) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.changeTermDates.effDate.missing",
                PolicyFields.NEW_TERM_EFF_DATE);
        }
        if (StringUtils.isBlank(sTermExpDate)) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.changeTermDates.expDate.missing",
                PolicyFields.NEW_TERM_EXP_DATE);
        }
        if (!StringUtils.isBlank(sTermEffDate) && !StringUtils.isBlank(sTermExpDate)) {
            Date termEffDate = DateUtils.parseDate(sTermEffDate);
            Date termExpDate = DateUtils.parseDate(sTermExpDate);
            if (termEffDate.after(termExpDate) || termEffDate.equals(termExpDate)) {
                MessageManager.getInstance().addErrorMessage("pm.transactionmgr.changeTermDates.dates.error",
                    PolicyFields.NEW_TERM_EXP_DATE);
            }
        }

        // Validation #2: Combination validation
        Record record = policyHeader.toRecord();
        PolicyHeaderFields.setTermEffectiveFromDate(record, sTermEffDate);
        PolicyHeaderFields.setTermEffectiveToDate(record, sTermExpDate);
        PolicyFields.setPolicyCycleCode(record, PolicyCycleCode.POLICY);
        AvailablePolicyTypeRecordValidator policyTypeValidator = new AvailablePolicyTypeRecordValidator();
        policyTypeValidator.validate(record);

        // Validation #3: Term Duration validation
        if (!StringUtils.isBlank(sTermEffDate) && !StringUtils.isBlank(sTermExpDate)) {
            String invalidTermDurationKey = "pm.transactionmgr.changeTermDates.termDuration.error";
            ValidTermDurationRecordValidator termDurationValidator = new ValidTermDurationRecordValidator(
                invalidTermDurationKey, PolicyFields.NEW_TERM_EFF_DATE);
            termDurationValidator.validate(record);
        }

        // Validation #4: Policy Existence validation
        SimilarPolicyRecordValidator similarPolicyValidator = new SimilarPolicyRecordValidator();
        record.setFields(inputRecord);
        similarPolicyValidator.validate(record);

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts()) {
            throw new ValidationException("Invalid Term Dates.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateTermDates");
        }
    }

    /**
     * check if the billing relation is valid
     *
     * @param inputRecord
     * @return a boolean value to indicate if the billing relation is valid
     */
    public boolean isBillingRelationValid(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isBillingRelationValid", new Object[]{inputRecord});

        boolean isValid;
        isValid = getTransactionDAO().isBillingRelationValid(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isBillingRelationValid", Boolean.valueOf(isValid));
        }

        return isValid;
    }


    /**
     * load all related policy info
     *
     * @param policyHeader
     * @param time(preorpost)
     * @return recordSet
     */
    public RecordSet loadAllRelatedPolicy(PolicyHeader policyHeader, String time) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRelatedPolicy",
            new Object[]{policyHeader, time});
        RecordSet recordSet;
        Record input = new Record();
        input.setFields(policyHeader.toRecord(), false);
        input.setFieldValue("time", time);
        if (RELATED_POLICY_DISPLAY_MODE_FOR_DISTINCT.equalsIgnoreCase(getRelatedPolicyDisplayMode(input))) {
            recordSet = getTransactionDAO().loadAllDistinctRelatedPolicy(input, new DefaultRecordLoadProcessor());
        }
        else {
            recordSet = getTransactionDAO().loadAllRelatedPolicy(input, new DefaultRecordLoadProcessor());
        }
        RecordComparator rc;
        RecordSet returnRecordSet = new RecordSet();
        rc = new RecordComparator(PolicyHeaderFields.POLICY_NO, true, SortOrder.ASC, null);
        rc.addFieldComparator(RiskFields.RISK_NAME, true, SortOrder.ASC, null);
        rc.addFieldComparator(WIP_B, true, SortOrder.ASC, null);
        RecordSet records = recordSet.getSortedCopy(rc);
        String policyNo = null;
        String riskName = null;
        String wipB = null;
        String tempPolicyNo = null;
        String tempRiskName = null;
        String tempWipB = null;
        for (int sortIdx = 0; sortIdx < records.getSize(); sortIdx++) {
            Record currentRecord = records.getRecord(sortIdx);
            if (currentRecord.hasStringValue(PolicyHeaderFields.POLICY_NO)) {
                tempPolicyNo = currentRecord.getStringValue(PolicyHeaderFields.POLICY_NO);
            }
            if (currentRecord.hasStringValue(RiskFields.RISK_NAME)) {
                tempRiskName = currentRecord.getStringValue(RiskFields.RISK_NAME);
            }
            if (currentRecord.hasStringValue(WIP_B)) {
                tempWipB = currentRecord.getStringValue(WIP_B);
            }
            if ((tempPolicyNo != null && tempPolicyNo.equals(policyNo)) &&
                (tempRiskName != null && tempRiskName.equals(riskName)) &&
                (tempWipB != null && tempWipB.equals(wipB))) {
                continue;
            }
            else {
                policyNo = tempPolicyNo;
                riskName = tempRiskName;
                wipB = tempWipB;
                returnRecordSet.addRecord(currentRecord);
            }
        }
        l.exiting(getClass().getName(), "loadAllRelatedPolicy", returnRecordSet);
        return returnRecordSet;
    }

    /**
     * check related policies exist or not
     *
     * @param policyHeader
     * @param time         (pre or post)
     * @return boolean
     */
    public boolean checkRelatedPolicy(PolicyHeader policyHeader, String time) {
        Logger l = LogUtils.enterLog(getClass(), "checkRelatedPolicy",
            new Object[]{policyHeader, time});
        boolean result = false;
        if (isRelPolicyShowed(time)) {
            Record input = new Record();
            input.setFields(policyHeader.toRecord(), false);
            input.setFieldValue("time", time);
            if (time.equals("PRE")) {
                input.setFieldValue("transactionLogId", new Integer(0));
            }
            Record record = getTransactionDAO().checkRelatedPolicy(input);
            if ((record.getLongValue("parentCnt").longValue() > 0) || (record.getLongValue("childCnt").longValue() > 0)) {
                result = true;
            }
        }
        l.exiting(getClass().getName(), "checkRelatedPolicy", new Boolean(result));
        return result;

    }

    /**
     * get flag of related policy should be shown or not
     *
     * @param time (pre or post)
     * @return boolean
     */
    private boolean isRelPolicyShowed(String time) {
        Logger l = LogUtils.enterLog(getClass(), "isRelPolicyShowed",
            time);
        boolean result = false;
        SysParmProvider sysParm = SysParmProvider.getInstance();
        String relatedPolsConfig = sysParm.getSysParm(SysParmIds.PM_VIEW_RELATED_POLS);
        if ("BOTH".equals(relatedPolsConfig) && "PRE".equals(time) || "POST".equals(time)) {
            result = true;
        }
        l.exiting(getClass().getName(), "isRelPolicyShowed", Boolean.valueOf(result));
        return result;
    }

    /**
     * check if the risk child relation configured in pm_attribute
     *
     * @param inputRecord Record containing policy_type
     * @return boolean
     */
    private boolean isRelChildCountConfigured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isRelChildCountConfigured",
            inputRecord);
        Record record = new Record();
        record.setFieldValue("policyType", inputRecord.getFieldValue("policyTypeCode"));
        boolean result = getTransactionDAO().isRelChildCountConfigured(record);
        l.exiting(getClass().getName(), "isRelChildCountConfigured", Boolean.valueOf(result));
        return result;

    }

    /**
     * check if the bypass of risk relation configured in pm_attribute
     *
     * @param inputRecord Record containing policy_type
     * @return boolean
     */
    private boolean isBypassRiskRelConfigured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isBypassRiskRelConfigured",
            inputRecord);
        Record record = new Record();
        record.setFieldValue("policyType", inputRecord.getFieldValue("policyTypeCode"));
        boolean result = getTransactionDAO().isBypassRiskRelConfigured(record);
        l.exiting(getClass().getName(), "isBypassRiskRelConfigured", Boolean.valueOf(result));
        return result;
    }

    /**
     * check if the user authority for override locked policy exist
     *
     * @return boolean
     */
    private boolean isUserAuthorityForLockedPolExist() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isUserAuthorityForLockedPolExist");
        }
        boolean result = false;
        try {
            result = UserSessionManager.getInstance().getUserSession().getOasisUser().hasProfile("PM_OVERRIDE_LOCKED");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if user authority exist for override locked.", e);
            l.throwing(getClass().getName(), "isUserAuthorityForLockedPolExist", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "isUserAuthorityForLockedPolExist", Boolean.valueOf(result));
        return result;
    }

    /**
     * get lock count of related policies
     *
     * @param policyHeader
     * @param time
     * @return
     */
    public long getLockedRelatedPolicyCount(PolicyHeader policyHeader, String time) {
        Logger l = LogUtils.enterLog(getClass(), "getLockedRelatedPolicyCount",
            new Object[]{policyHeader, time});
        RecordSet recordSet;
        Record input = new Record();
        input.setFields(policyHeader.toRecord(), false);
        input.setFieldValue("time", time);
        RecordLoadProcessor loadProcessor = new RelatedPolicyRecordLoadProcessor();
        if (RELATED_POLICY_DISPLAY_MODE_FOR_DISTINCT.equalsIgnoreCase(getRelatedPolicyDisplayMode(input))) {
            recordSet = getTransactionDAO().loadAllDistinctRelatedPolicy(input, loadProcessor);
        }
        else {
            recordSet = getTransactionDAO().loadAllRelatedPolicy(input, loadProcessor);
        }
        Record summaryRecord = recordSet.getSummaryRecord();
        boolean relChildCountConfigured = isRelChildCountConfigured(input);
        if (relChildCountConfigured) {
            summaryRecord.setFieldValue("lockCount", summaryRecord.getFieldValue("lockChildCount"));
        }
        else {
            summaryRecord.setFieldValue("lockCount", summaryRecord.getFieldValue("lockParentCount"));
        }
        Long lockCount = summaryRecord.getLongValue("lockCount");
        l.exiting(getClass().getName(), "loadAllRelatedPolicy", lockCount);
        return lockCount.longValue();
    }

    /**
     * validate locked related policies
     *
     * @param policyHeader
     * @return "pass" or "confirm" or "error"
     */
    public String validateLockedRelatedPolicy(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "validateLockedRelatedPolicy",
            new Object[]{policyHeader});
        String result = "pass";
        long lockCount = getLockedRelatedPolicyCount(policyHeader, "POST");
        Record input = new Record();
        input.setFields(policyHeader.toRecord(), false);
        boolean isByPassConfigExist = isBypassRiskRelConfigured(input);
        boolean isUserAuthorityExist = isUserAuthorityForLockedPolExist();
        if (lockCount > 0) {
            if (!isByPassConfigExist) {
                if (isUserAuthorityExist) {
                    result = "confirm";
                }
                else {
                    result = "error";
                }
            }
        }
        l.exiting(getClass().getName(), "validateLockedRelatedPolicy", result);
        return result;
    }

    /**
     * validate the current transaction.
     *
     * @param policyHeader
     * @param requestId    it can be action class name or request URI
     */
    public void validateTransaction(PolicyHeader policyHeader, String requestId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateTransaction", new Object[]{policyHeader, requestId});
        }
        boolean isTransactionValid = true;
        TransactionCode lastTransactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();
        //validate transaction
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        requestId = requestId.toUpperCase();
        //validate tail transactions
        if (!wa.hasWorkflow(policyHeader.getPolicyNo(), "SaveTailOfficial") &&
            !wa.hasWorkflow(policyHeader.getPolicyNo(), "SaveWip") &&
            (policyHeader.getPolicyIdentifier().getPolicyViewMode().isWIP() &&
                requestId.indexOf("MAINTAINTAIL") < 0 && requestId.indexOf("VIEWVALIDATIONERROR") < 0
                && requestId.indexOf("MAINTAINLOCK") < 0 && requestId.indexOf("COMPONENT") < 0
                && requestId.indexOf("SAVEOFFICIAL") < 0
                && (lastTransactionCode.isTailCancel() || lastTransactionCode.isTailEndorse()))) {
            //delete wip
            getLockManager().lockPolicy(policyHeader, "TransactionManager: validateTransaction.");
            deleteWipTransaction(policyHeader, policyHeader.toRecord());
            isTransactionValid = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateTransaction", new Boolean(isTransactionValid));
        }

        if (!isTransactionValid) {
            throw new ValidationException("transaction is invalid");
        }

    }

    /**
     * Set reload code for given transaction code in PolicyHeader.
     *
     * @param policyHeader    policy header
     * @param transactionCode transaction code
     */
    public void setPolicyHeaderReloadCode(PolicyHeader policyHeader, TransactionCode transactionCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyHeaderReloadCode", new Object[]{transactionCode});
        }

        PolicyHeaderReloadCode policyHeaderReloadCode = null;
        if (transactionCode.isAutoRenewal() || transactionCode.isManualRenewal() || transactionCode.isReissue() || transactionCode.isPurge()) {
            policyHeaderReloadCode = PolicyHeaderReloadCode.LAST_TERM;
        }
        else {
            policyHeaderReloadCode = PolicyHeaderReloadCode.CURRENT_TERM;
        }

        policyHeader.setReloadCode(policyHeaderReloadCode);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyHeaderReloadCode", policyHeaderReloadCode);
        }
    }

    /**
     * To check if need to skip rating.
     *
     * @param inputRecord
     * @return
     */
    public YesNoFlag isSkipRating(Record inputRecord) {
        int count = getTransactionDAO().getSkipRatingCount(inputRecord);
        if (count > 0) {
            return YesNoFlag.Y;
        }
        else {
            return YesNoFlag.N;
        }
    }

    /**
     * change term expiration
     *
     * @param policyHeader policy header
     * @param inputRecord  input record contains transEffectiveFromDate,AccountingDate,
     *                     newTermEffectiveFromDate,newTermEffectiveToDate,endorsementCode,Note
     */
    public void changeTermExpirationDate(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeTermExpirationDate", new Object[]{policyHeader});
        }
        String transEff = PolicyFields.getTermEffectiveToDate(inputRecord);
        PolicyFields.setTermEffectiveFromDate(inputRecord, PolicyFields.getNewTermEffDate(inputRecord));
        PolicyFields.setTermEffectiveToDate(inputRecord, PolicyFields.getNewTermExpDate(inputRecord));
        PolicyFields.setPolicyCycleCode(inputRecord, PolicyCycleCode.POLICY);
        PolicyFields.setIssueCompanyEntityId(inputRecord, policyHeader.getIssueCompanyEntityId());
        PolicyFields.setIssueStateCode(inputRecord, policyHeader.getIssueStateCode());
        PolicyFields.setRegionalOffice(inputRecord, policyHeader.getRegionalOffice());
        PolicyFields.setPolicyTypeCode(inputRecord, policyHeader.getPolicyTypeCode());
        PolicyFields.setPolicyId(inputRecord, policyHeader.getPolicyId());
        PolicyFields.setTermBaseRecordId(inputRecord, policyHeader.getTermBaseRecordId());
        //this param is for updateTransactionComments
        inputRecord.setFieldValue("forceRerateB", "N");

        RiskFields.setEntityId(inputRecord, policyHeader.getPolicyHolderNameEntityId());
        inputRecord.setFieldValue("cycleCode", "POLICY");

        validateForChangeTermExpirationDate(policyHeader, inputRecord);
        TransactionTypeCode typeCode = policyHeader.getLastTransactionInfo().getTransactionTypeCode();
        //If a Renewal or Reissue WIP transaction does not exist,create a new transaction
        boolean isLocked = false;
        if (policyHeader.getScreenModeCode().isViewPolicy()) {
            isLocked = getLockManager().lockPolicy(policyHeader, "changeTermExpirationDate: locking policy before creating transaction.");
            if (isLocked) {
                //if a transaction created then set mode to OFFICIAL,otherwise set it to TEMP
                //a record containing at least accountingDate, endorsementCode, transactionComment
                Transaction newTrans = createTransaction(policyHeader, inputRecord, transEff, TransactionCode.ENDCHGTERM);
                //set new created transaction log id to input record
                TransactionFields.setTransactionLogId(inputRecord, newTrans.getTransactionLogId());
                inputRecord.setFieldValue("mode", "OFFICIAL");
            }
            else {
                MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error");
                throw new ValidationException(MessageManager.getInstance().formatMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error"));
            }
        }
        else if (typeCode.isRenewal() || typeCode.isReissue()) {
            //set old transaction log id to input record
            TransactionFields.setTransactionLogId(inputRecord, policyHeader.getLastTransactionInfo().getTransactionLogId());
            inputRecord.setFieldValue("mode", "TEMP");
        }
        // Set riskId to null,in order to extend term base on policy level.
        inputRecord.setFieldValue("riskId",null);
        getTransactionDAO().changeTermExpirationDate(inputRecord);
        // set reload code in policy header
        setPolicyHeaderReloadCode(policyHeader, policyHeader.getLastTransactionInfo().getTransactionCode());
        if (isLocked) {
            getLockManager().unLockPolicy(policyHeader, "Unlock from change Term Expiration Date.");
        }
        l.exiting(getClass().getName(), "changeTermExpirationDate");
    }

    /**
     * load default date for change term expiration
     *
     * @param policyHeader policy header
     * @return default date
     */
    public Record getInitialValuesForChangeTermExpirationDate(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForChangeTermExpirationDate", new Object[]{policyHeader});
        }

        Record outputRecord = new Record();
        TransactionFields.setTransactionEffectiveFromDate(outputRecord, policyHeader.getTermEffectiveToDate());
        //set page entitlement flag
        Transaction lastTrans = policyHeader.getLastTransactionInfo();
        TransactionCode transCode = lastTrans.getTransactionCode();
        PolicyFields.setNewTermEffDate(outputRecord, policyHeader.getTermEffectiveFromDate());
        PolicyFields.setNewTermExpDate(outputRecord, policyHeader.getTermEffectiveToDate());

        if (policyHeader.getScreenModeCode().isViewPolicy()) {
            outputRecord.setFieldValue("isOfficial", YesNoFlag.Y);
            //if accounting date is editable,default to today
            String today = DateUtils.formatDate(new Date());
            outputRecord.setFieldValue("accountingDate", today);
        }
        else if (transCode.isRenewal() || transCode.isReissue()) {
            //If there is an existing Renewal or Reissue transaction,hide accounting date,endorsementCode and
            //transactionComments
            //set the accountDate and transactionComments to the values from the current transaction
            PolicyFields.setAccountingDate(outputRecord, lastTrans.getTransAccountingDate());
            TransactionFields.setTransactionComment(outputRecord, lastTrans.getTransactionComments());
            TransactionFields.setTransactionComment2(outputRecord, lastTrans.getTransactionComments2());
            outputRecord.setFieldValue("isOfficial", YesNoFlag.N);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForChangeTermExpirationDate", outputRecord);
        }
        return outputRecord;
    }

    /**
     * validate input for change term expiration date
     * It does following validations:
     * 1. If expiration date is not later than original date
     * 2. If it's invalid term duration
     * 3. If it's invalid term dates
     * 4. If it's invalid accounting month
     * 5. If policy already locked
     *
     * @param policyHeader policy header
     * @param inputRecord  input record
     */
    public void validateForChangeTermExpirationDate(PolicyHeader policyHeader, Record inputRecord) {

        //If expiration date is not later than original date
        String termExpirationDate = PolicyFields.getNewTermExpDate(inputRecord);
        String originalDate = policyHeader.getTermEffectiveToDate();
        //it means termExpirationDate - originalDate
        long days = DateUtils.dateDiff(DateUtils.DD_DAYS, DateUtils.parseDate(originalDate),
            DateUtils.parseDate(termExpirationDate));
        if (days <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.changeTermExpirationDate.dateNotLater", NEW_TERM_EFFECTIVE_TO_DATE);
        }
        //If it's invalid term duration,it should contains following parameters:
        //policyHolderNameEntityId,termEffectiveFromDate,termEffectiveToDate
        new AvailablePolicyTypeRecordValidator("pm.transactionmgr.changeTermExpirationDate.invalidDuration", NEW_TERM_EFFECTIVE_TO_DATE).validate(inputRecord);
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid data");
        }
        //If it's invalid term dates,it should pass a inputrecord which contains following parameters:
        // issueCompanyEntityId,issueStateCode,policyTypeCode,
        // regionalOffice,termEffectiveFromDate,termEffectiveToDate
        SysParmProvider sysParm = SysParmProvider.getInstance();
        String param = sysParm.getSysParm("PM_CHGTRMEXP_COMMON", "N");
        if (!YesNoFlag.getInstance(param).booleanValue()) {
            new ValidTermDurationRecordValidator("pm.transactionmgr.changeTermExpirationDate.invalidDates", NEW_TERM_EFFECTIVE_TO_DATE).validate(inputRecord);
        }
        //If it's invalid accounting month,it should pass "accountingDate" as the only parameter
        new AccountingMonthRecordValidator("pm.transactionmgr.changeTermExpirationDate.invalidAccountingMonth").validate(inputRecord);

        if (!getLockManager().canLockPolicy(policyHeader)) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error");
        }
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid data");
        }
    }

    /**
     * load default policy administrator
     *
     * @param policyHeader policy header
     * @return default policy administrator
     */
    public Record getInitialValuesForChangePolicyAdministrator(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForChangePolicyAdministrator", new Object[]{policyHeader});
        }

        Record output = new Record();
        String policyHolder = policyHeader.getPolicyHolderName();
        PolicyFields.setCurrentAdmin(output, policyHolder);
        PolicyFields.setNewAdmin(output, policyHolder);
        PolicyFields.setEffectiveFromDate(output, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        PolicyFields.setEffectiveToDate(output, DEFAULT_EFFECTIVE_TO);

        // make the page editable if the screen mode code is WIP, OOS WIP(initial term only),
        // Manual entry or Renewal WIP.
        ScreenModeCode smc = policyHeader.getScreenModeCode();
        if (smc.isWIP() || (smc.isOosWIP() && policyHeader.isInitTermB()) || smc.isRenewWIP() || smc.isManualEntry()) {
            output.setFieldValue("isChgAdminEffectiveToEditable", YesNoFlag.Y);
        }
        else {
            output.setFieldValue("isChgAdminEffectiveToEditable", YesNoFlag.N);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForChangePolicyAdministrator", output);
        }
        return output;
    }

    /**
     * validate input for change policy administrator
     *
     * @param policyHeader policy header
     * @param inputRecord  input record
     */
    public void validateForChangePolicyAdministrator(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangePolicyAdministrator", new Object[]{policyHeader, inputRecord});
        }

        if (policyHeader.getScreenModeCode().isManualEntry()) {
            String effectiveTo = PolicyFields.getEffectiveToDate(inputRecord);
            if (!DEFAULT_EFFECTIVE_TO.equals(effectiveTo)) {
                MessageManager.getInstance().addErrorMessage("pm.transactionmgr.changePolicyAdministrator.effectiveTo.error");
            }
        }
        l.exiting(getClass().getName(), "validateForChangePolicyAdministrator");

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Data.Effective To date must be 01/01/3000.");
        }

    }

    /**
     * change policy administrator
     *
     * @param policyHeader policy header
     * @param inputRecord  input record
     */
    public void savePolicyAdministrator(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePolicyAdministrator", new Object[]{policyHeader, inputRecord});
        }
        PolicyFields.setPolicyId(inputRecord, policyHeader.getPolicyId());
        TransactionFields.setTransactionLogId(inputRecord, policyHeader.getLastTransactionId());
        validateForChangePolicyAdministrator(policyHeader, inputRecord);
        String status = getTransactionDAO().checkPolicyHolderStatus(inputRecord);
        if ("OFFICIAL".equals(status)) {
            getTransactionDAO().updatePolicyAdministrator(inputRecord);
        }
        else {
            getTransactionDAO().addPolicyAdministrator(inputRecord);
        }
        l.exiting(getClass().getName(), "savePolicyAdministrator");
    }

    /**
     * validate policy picture
     *
     * @param policyHeader policy header
     * @param inputRecord  input record
     */
    public void validatePolicyPicture(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePolicyPicture", new Object[]{policyHeader});
        }

        // If the policy is lockes we let the regular locking mechanism do its validations.
        if (getTransactionDAO().isPolicyLocked(policyHeader.getPolicyIdentifier())) {
            return;
        }

        Object wipNo = inputRecord.getFieldValue("wipNo");
        Object offNo = inputRecord.getFieldValue("offNo");
        Object headerWipNo = policyHeader.getPolicyIdentifier().getPolicyWipNumber();
        Object headerOffNo = policyHeader.getPolicyIdentifier().getPolicyOffNumber();

        if (!headerWipNo.equals(wipNo) || !headerOffNo.equals(offNo)) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.changedPolicyPicture.error");
            ValidationException ve = new ValidationException();
            // Set the message key in case this is being called from a normal load page
            ve.setMessageKey("pm.transactionmgr.captureTransationDetails.changedPolicyPicture.error");
            l.throwing(getClass().getName(), "validatePolicyPicture", ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validatePolicyPicture");
    }

    /**
     * get initial value for extend term
     *
     * @param policyHeader
     * @param inputRecord
     * @return initialValue
     */
    public Record getInitialValuesForExtendCancelTerm(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForExtendCancelTerm", new Object[]{policyHeader});
        }
        validatePolicyPicture(policyHeader, inputRecord);
        Record outRecord = new Record();
        outRecord.setFieldValue("accountingDate", DateUtils.formatDate(new Date()));
        outRecord.setFieldValue("termEffective", policyHeader.getTermEffectiveFromDate());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForExtendCancelTerm", outRecord);
        }
        return outRecord;
    }

    /**
     * It does following validations:
     * 1.	Extension date is greater than the current term expiration date.
     * 2.	Accounting date is valid.
     *
     * @param policyHeader policy header
     * @param inputRecord  input record
     */
    public void validateForExtendCancelTerm(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForExtendCancelTerm", new Object[]{inputRecord});
        }
        String termExpDate = policyHeader.getTermEffectiveToDate();
        String extendToDate = inputRecord.getStringValue("extendToDate");
        try {
            if (DateUtils.daysDiff(termExpDate, extendToDate) <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.transactionmgr.extendCancelTerm.validation.error");
            }
        }
        catch (ParseException e) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.extendCancelTerm.validation.error");
            l.throwing(getClass().getName(), "validateForExtendCancelTerm", e);
        }
        //need accountingDate in inputRecord
        new AccountingMonthRecordValidator().validate(inputRecord);

        Record valInRecord = new Record();
        PolicyFields.setPolicyId(valInRecord, policyHeader.getPolicyId());
        CancelProcessFields.setTermBaseId(valInRecord, policyHeader.getTermBaseRecordId());
        CancelProcessFields.setCancelDate(valInRecord, extendToDate);
        CancelProcessFields.setIsExtendB(valInRecord, YesNoFlag.Y);
        Record valOutRecord = getTransactionDAO().validateRelatedPolicy(valInRecord);
        if (valOutRecord != null && "INVALID".equals(valOutRecord.getStringValue("status"))) {
            MessageManager.getInstance().addErrorMessage("pm.common.warning.message",
                new String[]{valOutRecord.getStringValue("msg")});
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Validation error for Extend Term.");
        }
    }

    /**
     * it utilizes DAO's performExtendCancelTerm and TransactionManager's createTransaction,PolicyManager's
     * lockPolicy/unlockPolicy to process extension.
     *
     * @param policyHeader policy header
     * @param inputRecord  input record
     */
    public void performExtendCancelTerm(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performExtendCancelTerm", new Object[]{inputRecord});
        }
        PolicyFields.setPolicyId(inputRecord, policyHeader.getPolicyId());
        PolicyFields.setTermBaseRecordId(inputRecord, policyHeader.getTermBaseRecordId());
        validateForExtendCancelTerm(policyHeader, inputRecord);
        if (!getLockManager().canLockPolicy(policyHeader)) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error");
            throw new ValidationException("The policy is locked by another user at this time.");
        }
        getLockManager().lockPolicy(policyHeader, "TransactionManager: performExtendCancelTerm");
        Transaction trans = null;
        boolean isTailCreated = false;
        try {
            //need accountingDate(from page),endorsementCode,transactionComment to be set
            inputRecord.setFieldValue("endorsementCode", "");
            inputRecord.setFieldValue("transactionComment", inputRecord.getStringValue("comments"));
            trans = createTransaction(policyHeader, inputRecord, inputRecord.getStringValue("extendToDate"), TransactionCode.EXTEND);
            if (trans != null) {
                TransactionFields.setTransactionLogId(inputRecord, trans.getTransactionLogId());
                isTailCreated = getTransactionDAO().extendCancelTerm(inputRecord);
            }
            getLockManager().unLockPolicy(policyHeader, "Unlock from Perform Extend Cancel Term.");
        }
        catch (Exception e) {
            l.throwing(getClass().getName(), "performExtendCancelTerm", e);
            //Deletes TEMP or ENDQUOTE policy data. Policy is always unlocked after
            //this and effectively returns to its OFFICIAL state.
            if (trans != null) {
                deleteWipTransaction(policyHeader, inputRecord);
            }
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.extendCancelTerm.save.error");
        }

        // determine the first workflow step
        String initialState;
        //if it has tail
        if (isTailCreated) {
            initialState = "invokeTailRateNotifyAndSaveAsOfficialDetail";
        }
        else {
            initialState = "invokeRateNotifyAndSaveAsOfficialDetail";
        }
        // Initialize the workflow
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
            "SaveAsOfficialDetail",
            initialState);
        //to skip notify
        wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "skipNotify", YesNoFlag.Y);
        wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), dti.pm.core.http.RequestIds.WORKFLOW_FOR, "extendCancelTerm");
    }

    /**
     * re-rate policy
     *
     * @param policyHeader policy header
     * @param inputRecord  input record
     */
    public void performReRatePolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performReRatePolicy", new Object[]{policyHeader, inputRecord});
        }

        getLockManager().lockPolicy(policyHeader, "TransactionManager: performReRatePolicy");
        //need accountingDate(from page),endorsementCode,transactionComment to be set
        TransactionFields.setNewAccountingDate(inputRecord, DateUtils.formatDate(new Date()));
        TransactionFields.setNewEndorsementCode(inputRecord, "");
        TransactionFields.setNewTransactionComment(inputRecord, "");
        //according to uc,it should be equals to termEffectiveFromDate
        PolicyFields.setEffectiveFromDate(inputRecord, policyHeader.getTermEffectiveFromDate());
        if (policyHeader.isLastTerm()) {
            createTransaction(policyHeader, inputRecord, policyHeader.getTermEffectiveFromDate(), TransactionCode.ENDORSE);
        }
        else {
            createTransaction(policyHeader, inputRecord, policyHeader.getTermEffectiveFromDate(), TransactionCode.OOSENDORSE);
        }
        String saveOption = inputRecord.getStringValue("newSaveOption");
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        // Process save based upon save option chosen by the user
        if (saveOption.equalsIgnoreCase("WIP")) {
            wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                RATE_WORKFLOW_PROCESS,
                RATE_INITIAL_STATE);
        }
        else if (saveOption.equalsIgnoreCase("OFFICIAL")) {
            // Initialize the new save official workflow
            wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                SAVE_OFFICIAL_WORKFLOW_PROCESS,
                SAVE_OFFICIAL_INITIAL_STATE);
        }
        wa.setWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "showViewPremium", YesNoFlag.Y);
    }

    /**
     * Load all historical administrator by selected policy
     *
     * @param policyHeader the policyHeader of policy
     * @return RecordSet
     */
    public RecordSet loadAllPolicyAdminHistory(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicyAdminHistory", new Object[]{policyHeader});
        }
        RecordSet rs;
        Record input = policyHeader.toRecord();
        // Get historical administrator record set.
        rs = getTransactionDAO().loadAllPolicyAdminHistory(input);
        if ((rs.getSize()) <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.viewPolicyAdminHistoryInfo.adminHistoryList.noDataFound");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPolicyAdminHistory", rs);
        }
        return rs;
    }

    /**
     * Check if source policy in WIP status
     *
     * @return Record
     */
    public Record isSourcePolicyInWip(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSourcePolicyInWip", new Object[]{inputRecord});
        }
        Record rs = getTransactionDAO().isSourcePolicyInWip(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSourcePolicyInWip", rs);
        }
        return rs;
    }

    /**
     * Delete WIP transaction by policy no, the DB tier retrieves the in progress transaction id
     * and perform delete wip action. This method is used to perform delete WIP action in extern policy
     * instead of currently opened policy.
     *
     * @param policyNo
     * @return Record
     */
    public Record delWipTransaction(String policyNo) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "delWipTransaction", new Object[]{policyNo});
        }

        Record result = getTransactionDAO().deleteWipTransaction(policyNo);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "delWipTransaction", result);
        }
        return result;
    }

    /**
     * Get linked amalgamation policy
     *
     * @param inputRecord
     * @return
     */
    public Record getAmalgamationLinkedPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAmalgamationLinkedPolicy", new Object[]{inputRecord});
        }
        Record  result;
        try{
            result = getTransactionDAO().getAmalgamationLinkedPolicy(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.SAVE_ERROR", "Save error found", e);
            l.throwing(getClass().getName(), "saveTransactionAsOfficial", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAmalgamationLinkedPolicy");
        }
        return result;
    }

    /*
    * Method to get the max accounting date for a policy term history id
     * that is contained witin the record:
     * get the max accouting date in oasis, if sysdate is greater than max date, return sysdate
     *
     * @param inputRecord record that contains policyTermHistoryId field
     * @return String     date string in mm/dd/yyyy format
     */
     public String getMaxAccountingDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMaxAccountingDate", new Object[]{inputRecord});
        }

        String accountingDate = getTransactionDAO().getMaxAccountingDate(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMaxAccountingDate", accountingDate);
        }
        return accountingDate;
    }

    /**
     * Method to check if transaction snapshot exists
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean isTransactionSnapshotExist(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isTransactionSnapshotExist", new Object[]{inputRecord});
        }

        boolean isExist = YesNoFlag.getInstance(getTransactionDAO().isTransactionSnapshotExist(inputRecord)).booleanValue();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isTransactionSnapshotExist", Boolean.valueOf(isExist));
        }
        return isExist;
    }

    /**
     * Method to check if snapshot is configured for the current installation.
     *
     * @return boolean
     */
    public boolean isSnapshotConfigured() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSnapshotConfigured");
        }

        boolean isConfigured = YesNoFlag.getInstance(getTransactionDAO().isSnapshotConfigured()).booleanValue();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSnapshotConfigured", Boolean.valueOf(isConfigured));
        }
        return isConfigured;
    }

    /**
     * Get defalut values for professional entity search criteria.
     *
     * @return Record
     */
    public Record getDefaultValuesForProfessionalEntitySearchCriteria() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultValuesForProfessionalEntitySearchCriteria");
        }

        Record record = new Record();
        record.setFieldValue("ddRisk", "-1");
        record.setFieldValue("ddChgRecord", "N");
        record.setFieldValue("ddChangeCode", "-1");
        record.setFieldValue("ddChgDelta", "N");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultValuesForProfessionalEntitySearchCriteria", record);
        }
        return record;
    }

    /**
     * Load all professional entity transaction.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProfessionalEntityTransaction(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProfessionalEntityTransaction", new Object[]{inputRecord});
        }
        RecordLoadProcessor entitlementRLP = new ProfEntityDetailEntitlementRecordLoadProcessor();
        RecordSet rs = getTransactionDAO().loadAllProfessionalEntityTransaction(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProfessionalEntityTransaction", rs);
        }
        return rs;
    }

    /**
     * Load all professional entity transaction detail.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProfessionalEntityTransactionDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProfessionalEntityTransactionDetail", new Object[]{inputRecord});
        }
        RecordSet rs;
        // System only retrieves details for the originalTransactionLogId is greater than 0.
        if (StringUtils.isBlank(inputRecord.getStringValue("originalTransactionLogId")) ||
            inputRecord.getLongValue("originalTransactionLogId").longValue() == 0) {
            rs = new RecordSet();
            List nameList = new ArrayList();
            nameList.add("rownum");
            rs.addFieldNameCollection(nameList);
        }
        else {
            rs = getTransactionDAO().loadAllProfessionalEntityTransactionDetail(inputRecord);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProfessionalEntityTransactionDetail", rs);
        }
        return rs;
    }

    /**
     * Method to check if entity detail button is available.
     *
     * @param policyHeader
     * @return String
     */
    public String isEntityDetailAvailable(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isEntityDetailAvailable");
        }

        String rtnString = "N";
        Record inputRecord = policyHeader.toRecord();
        PolicyCacheManager policyCacheManager = PolicyCacheManager.getInstance();

        if (!policyCacheManager.hasProfEntityConfigured())  {
            rtnString = getTransactionDAO().isProfEntityConfigured(inputRecord);
            policyCacheManager.setProfEntityConfigured(YesNoFlag.getInstance(rtnString).booleanValue());
        }

        if (policyCacheManager.getProfEntityConfigured()) {
            if (!policyHeader.hasProfEntityCache()) {
                rtnString = getTransactionDAO().isEntityDetailAvailable(inputRecord);
                policyHeader.setProfEntityCache(rtnString);
            }
            else {
                rtnString = policyHeader.getProfEntityCache();
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isEntityDetailAvailable", rtnString);
        }

        return rtnString;
    }

    //get quote type from policyHeader.transaction
    private String getQuoteTypeByPolicyHeader(PolicyHeader policyHeader) {
        String quoteType = "endorsement";
        TransactionTypeCode transactionTypeCode = policyHeader.getLastTransactionInfo().getTransactionTypeCode();
        if (transactionTypeCode.isRenewal()) {
            quoteType = "renewal";
        }
        return quoteType;
    }

    /**
     * Get the policy term information.
     * <p/>
     *
     * @param policyHeader policy header with policy information.
     * @return Record loaded with previous term information.
     */
    public Record getPreviousTermInformation(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPreviousTermInformation", new Object[]{policyHeader});
        }
        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyId", policyHeader.getPolicyId());
        inputRecord.setFieldValue("termBaseId", policyHeader.getPolicyTerm(policyHeader.getPolicyTermHistoryId()).getTermBaseRecordId());
        inputRecord.setFieldValue("transId", policyHeader.getLastTransactionId());
        inputRecord.setFieldValue("currTermEff", policyHeader.getTermEffectiveFromDate());
        inputRecord.setFieldValue("currTermExp", policyHeader.getTermEffectiveToDate());
        inputRecord.setFieldValue("initTermB", "N");

        Record output = getTransactionDAO().getPreviousTermInformation(inputRecord);

        if (!output.hasStringValue("getprevdateretcode") ||
            (output.hasStringValue("getprevdateretcode") && !"0".equals(output.getStringValue("getprevdateretcode")))) {
            MessageManager.getInstance().addErrorMessage("pm.undoTerm.getPrevTermDate.error");
            throw new ValidationException("Error in Getting Previous Term");
        }

        if (!output.hasStringValue("getterminforetcode") ||
            (output.hasStringValue("getterminforetcode") && !"0".equals(output.getStringValue("getterminforetcode")))) {
            MessageManager.getInstance().addErrorMessage("pm.undoTerm.getPrevTermInfo.error");
            throw new ValidationException("Error Getting Term Information for Undo transaction");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPreviousTermInformation", output);
        }
        return output;
    }

    /**
     * Process undo term
     *
     * @param policyHeader policy header with policy information
     * @param inputRecord  input record with the passed request values.
     */
    public void processUndoTerm(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processUndoTerm", new Object[]{policyHeader, inputRecord});
        }

        Record prevTermRecord = getPreviousTermInformation(policyHeader);
        String transactionEffectiveDate = prevTermRecord.getStringValue("termExpDt");
        Transaction trans = null;
        try {
            inputRecord.setFieldValue("undoPrevTermBaseId",prevTermRecord.getStringValue("prevTermBaseId"));
            trans = createTransaction(policyHeader, inputRecord, transactionEffectiveDate, TransactionCode.UNDOTERM, true);
            if (trans != null) {
                TransactionFields.setTransactionLogId(inputRecord, trans.getTransactionLogId());
                Record record = new Record();
                record.setFieldValue("transLogId", trans.getTransactionLogId());
                record.setFieldValue("policyId", policyHeader.getPolicyId());
                record.setFieldValue("undoDate", prevTermRecord.getStringValue("termExpDt"));
                record.setFieldValue("termBaseId", prevTermRecord.getStringValue("prevTermBaseId"));
                record.setFieldValue("termEff", prevTermRecord.getStringValue("termEffDt"));
                record.setFieldValue("termExp", prevTermRecord.getStringValue("termExpDt"));
                getTransactionDAO().processUndoTerm(record);
            }
        }
        catch (Exception e) {
            l.warning(getClass().getName() + "exception in processUndoTerm+ " + e.getMessage());
            if (trans != null) {
                deleteWipTransaction(policyHeader, inputRecord);
            }
        }

        // Initialize the workflow
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        wa.initializeWorkflow(policyHeader.getPolicyNo(),
            "SaveAsOfficialDetail",
            "invokeRateNotifyAndSaveAsOfficialDetail");

        // For Undo Term transaction, set the policyTermHistoryId to previous policy term history id, policyView Mode
        // to WIP, so the policy header in workflow can get the lock id and transaction information correctly.
        String previousTermBaseId = prevTermRecord.getStringValue("prevTermBaseId");
        String previousPolicyTermHistoryId = "";
        Iterator itr = policyHeader.getPolicyTerms();
        while (itr.hasNext()) {
            Term term = (Term) itr.next();
            if (previousTermBaseId.equals(term.getTermBaseRecordId())) {
                previousPolicyTermHistoryId = term.getPolicyTermHistoryId();
                break;
            }
        }
        if (!StringUtils.isBlank(previousPolicyTermHistoryId)) {
            wa.setWorkflowAttribute(policyHeader.getPolicyNo(), "policyTermHistoryId", previousPolicyTermHistoryId);
        }

        wa.setWorkflowAttribute(policyHeader.getPolicyNo(), "policyViewMode", "WIP");
        // Add transactionCode to workflow, For Undo Term transaction check this flag to decide use a new function to
        // refresh parent window. 
        wa.setWorkflowAttribute(policyHeader.getPolicyNo(), "transactionCode", TransactionCode.UNDOTERM);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processUndoTerm");
        }
    }

    /**
     * Check if undo term can be done for the current policy
     *
     * @param policyHeader policy header contains policy information
     * @return boolean true or false
     */
    public boolean isUndoTermAvailable(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isUndoTermAvailable", new Object[]{policyHeader, policyHeader});
        }

        boolean isUndoTermAvailable =false;
        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyId", policyHeader.getPolicyId());
        inputRecord.setFieldValue("termBaseId", policyHeader.getPolicyTerm(policyHeader.getPolicyTermHistoryId()).getTermBaseRecordId());
        inputRecord.setFieldValue("termEff", policyHeader.getTermEffectiveFromDate());
        inputRecord.setFieldValue("termExp", policyHeader.getTermEffectiveToDate());
        
        isUndoTermAvailable = getTransactionDAO().isUndoTermAvailable(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isUndoTermAvailable", isUndoTermAvailable);
        }

        return isUndoTermAvailable;
    }

    /**
     * Get related policy display mode from pm_attribute table.
     *
     * @param inputRecord with policy type code.
     * @return String value indicate which mode to display with.
     */
    public String getRelatedPolicyDisplayMode(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRelatedPolicyDisplayMode");
        }

        String rtnString = getTransactionDAO().getRelatedPolicyDisplayMode(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRelatedPolicyDisplayMode", rtnString);
        }
        return rtnString;
    }

    /**
     * Get related policy display mode from pm_attribute table.
     *
     * @param policyHeader with policy header information.
     * @param inputRecord with policy type code.
     * @return Transaction.
     */
    public Transaction convertCoverageTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "convertCoverageTransaction", new Object[]{policyHeader, inputRecord});

        Transaction transactionData = createTransaction(policyHeader, inputRecord, inputRecord.getStringValue("newTransactionEffectiveFromDate"),
            TransactionFields.getTransactionCode(inputRecord));

        TransactionFields.setTransactionLogId(inputRecord, transactionData.getTransactionLogId());

        getTransactionDAO().convertCoverage(policyHeader, inputRecord);

        l.exiting(getClass().getName(), "convertCoverageTransaction", transactionData);
        return transactionData;
    }

    /**
     * Wrapper to load warning message from the save process.
     * Current implementation obtains warning message from db;
     *
     * @return String containing warning information
     */
    public String loadWarningMessage(String transactionLogId) {
        Logger l = LogUtils.enterLog(getClass(), "loadWarningMessage", new Object[]{transactionLogId});

        Record inputRecord = new Record();
        inputRecord.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, transactionLogId);

        String warningMsg = getTransactionDAO().getWarningMessage(inputRecord);

        l.exiting(getClass().getName(), "loadWarningMessage", warningMsg);
        return warningMsg;
    }

    /**
     * Wrapper to add warning message.
     * Current implementation obtains warning message from db;
     *
     */
    public void addWarningMessage(String transactionLogId) {
        Logger l = LogUtils.enterLog(getClass(), "addWarningMessage");

        String warningMsg = loadWarningMessage(transactionLogId);

        if (!StringUtils.isBlank(warningMsg)) {
            MessageManager.getInstance().addWarningMessage("pm.common.warning.message",
                new String[]{warningMsg});
        }

        l.exiting(getClass().getName(), "addWarningMessage", warningMsg);
    }

    /**
     * Wrapper to init warning message.
     *
     */
    public void initWarningMessage(String transactionLogId) {
        Logger l = LogUtils.enterLog(getClass(), "initWarningMessage", new Object[]{transactionLogId});
        Record inputRecord = new Record();
        inputRecord.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, transactionLogId);
        try{
            getTransactionDAO().initWarning(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "pm.workflowmgr.save.official.processSaveOfficial.info.SAVE_ERROR", "Save error found", e);
            l.throwing(getClass().getName(), "saveTransactionAsOfficial", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "initWarningMessage");
    }

    /**
     * To check to invoke the check clearing reminder or not.
     *
     * @param policyHeader with policy header information.
     * @return boolean true or false.
     */
    public boolean checkClearingReminder(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkClearingReminder", policyHeader);
        }

        String checkReminder = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_OS_CHECK_REMINDER);
        TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();

        if (transactionCode.isReinstate() && YesNoFlag.getInstance(checkReminder).booleanValue()) {
            Record inputRecord = new Record();
            PolicyFields.setTermBaseRecordId(inputRecord, policyHeader.getTermBaseRecordId());
            Record outputRecord = getTransactionDAO().getPolicyRelReasonCode(inputRecord);
            String returnValue = outputRecord.getStringValue("returnvalue");
            if (CheckClearingReminderFields.PolicyRelReasonCodeValues.CNPP.equals(returnValue)) {
                l.exiting(getClass().getName(), "checkClearingReminder", true);
                return true;
            }
        }
        l.exiting(getClass().getName(), "checkClearingReminder", false);
        return false;
    }

    /**
     * Set the check clearing reminder
     *
     * @param policyHeader with policy header information.
     * @param inputRecord with clearing reminder value
     * @return boolean.
     */
    public void processSaveCheckClearingReminder(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processSaveCheckClearingReminder");
        }
        Record record = new Record();
        CheckClearingReminderFields.setValue(record, inputRecord.getStringValue(policyHeader.getPolicyNo() + ".confirmed"));
        CheckClearingReminderFields.setContext(record, CheckClearingReminderFields.ContextCodeValues.CHECK_CLEARING_REMINDER);
        TransactionFields.setTransactionLogId(record, policyHeader.getLastTransactionId());

        WorkflowAgent wa = WorkflowAgentImpl.getInstance();

        Record outputRecord = getTransactionDAO().saveFormTransaction(record);

        //If error exists in DB.
        if (outputRecord == null || "-1".equals(outputRecord.getStringValue("retCode"))) {
            wa.setWorkflowTransitionParameter(PolicyHeaderFields.getPolicyNo(inputRecord), "CHECK_CLEARING_REMINDER_ERROR");
            l.logp(Level.SEVERE, getClass().getName(), "processSaveCheckClearingReminder",
                "Error in Check Clearing Reminder Processing.", "");
            return;
        }

        wa.setWorkflowTransitionParameter(PolicyHeaderFields.getPolicyNo(inputRecord), "SUCCESS");
        l.exiting(getClass().getName(), "processSaveCheckClearingReminder", "");
    }

    /**
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean isNotifyConfigured (PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "isNotifyConfigured", new Object[]{policyHeader});

        boolean isConfigured = true;

        PolicyCacheManager policyCacheManager = PolicyCacheManager.getInstance();
        if ( !policyCacheManager.hasNotifyConfigured() ) {
            boolean isNotifyConfigured = getTransactionDAO().isProdNotifyConfigured();
            policyCacheManager.setNotifyConfigured(YesNoFlag.getInstance(isNotifyConfigured).booleanValue());
        }

        isConfigured = policyCacheManager.getNotifyConfigured();

        if (isConfigured) {
            if (!policyCacheManager.hasNotifyTransactionCode()) {
                policyCacheManager.setNotifyTransactionCode(getTransactionDAO().loadNotifyTransactionCode());
            }

            RecordSet transCodes = policyCacheManager.getNotifyTransactionCode();
            String code = null;
            boolean found = false;

            Iterator it = transCodes.getRecords();
            while (it.hasNext()) {
                code = (((Record)it.next()).getStringValue("transactionCode")).trim();
                if ("*".equalsIgnoreCase(code) || policyHeader.getLastTransactionInfo().getTransactionCode().equals(code)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                isConfigured = false;
            }
        }

        l.exiting(getClass().getName(), "isNotifyConfigured", isConfigured);

        return isConfigured;
    }

    /**
     * Add the default agent.
     * @param inputRecord
     */
    public void addDefaultAgent(Record inputRecord) {
        getTransactionDAO().insertDefaultAgent(inputRecord);
    }

    /**
     * Returns int value loaded with product notification response
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return RecordSet
     */
    public int productNotificationResponse(Record inputRecord) {
        int returnValue = getTransactionDAO().productNotificationResponse(inputRecord);
        return returnValue;
    }

    @Override
    public boolean isAutoPendingRenewalEnable (PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "isRenewalWIPAutoDeleteAvailable", new Object[]{policyHeader});

        boolean isAutoPendingRenewalEnable = PolicyAttributesFactory.getInstance().
            isAutoPendingRenewalEnable(policyHeader.getTermEffectiveFromDate(),
                null,
                policyHeader.getWipTransCode() == null ? null : TransactionCode.getInstance(policyHeader.getWipTransCode()));

        if (isAutoPendingRenewalEnable) {
            Iterator itr = policyHeader.getPolicyTerms();
            Term lastTerm = null;
            Term currentTerm = null;
            if (itr.hasNext()) {
                lastTerm = (Term) itr.next();
            }
            if (itr.hasNext()) {
                currentTerm = (Term) itr.next();
            }
            if (lastTerm != null && lastTerm.isWipExists() && !lastTerm.isOfficialExists() &&
                currentTerm != null && currentTerm.isOfficialExists() &&
                StringUtils.isSame(currentTerm.getPolicyTermHistoryId(), policyHeader.getPolicyTermHistoryId())) {
                isAutoPendingRenewalEnable = true;
            }
            else {
                isAutoPendingRenewalEnable = false;
            }
        }
        l.exiting(getClass().getName(), "isAutoPendingRenewalEnable", isAutoPendingRenewalEnable);
        return isAutoPendingRenewalEnable;
    }

    /**
     * Auto delete renewal WIP.
     * @param policyHeader
     */
    public Record performDeleteRenewalWIP (PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "autoDeleteRenewalWIP", new Object[]{policyHeader});

        Record outputRecord = null;
        if (isAutoPendingRenewalEnable(policyHeader)) {
            Term lastTerm = null;
            Iterator itr = policyHeader.getPolicyTerms();
            if (itr.hasNext()) {
                lastTerm = (Term) itr.next();
            }
            if (lastTerm != null) {
                PolicyManager policyManager = ((PolicyManager) ApplicationContext.getInstance().getBean(PolicyManager.BEAN_NAME));
                // Unload policy if locked
                if (policyHeader.getPolicyIdentifier().ownLock()) {
                    getLockManager().unLockPolicy(policyHeader, YesNoFlag.Y, "autoDeleteRenewalWIP: unlock policy before auto delete WIP");
                }
                // Reload WIP policy header before delete WIP
                RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
                UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_HEADER);
                PolicyHeader wipPolicyHeader = policyManager.loadPolicyHeader(policyHeader.getPolicyNo(), getClass().getName(), "autoDeleteRenewalWIP: delete WIP");
                Record inputRecord = wipPolicyHeader.toRecord();
                inputRecord.setFieldValue("autoBackupRenewalWip", Boolean.TRUE);
                outputRecord = deleteWipTransaction(wipPolicyHeader, inputRecord);
                // Reload current policy header after delete WIP
                RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
                UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_HEADER);
                policyHeader = policyManager.loadPolicyHeader(policyHeader.getPolicyNo(), getClass().getName(), "autoDeleteRenewalWIP: reload policy header");
            }
        }

        l.exiting(getClass().getName(), "autoDeleteRenewalWIP", outputRecord);
        return outputRecord;
    }

    @Override
    public boolean hasTransactionXref(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadTransactionXref", new Object[]{inputRecord});
        RecordSet rs = getTransactionDAO().loadTransactionXref(inputRecord);
        int size = rs.getSize();
        l.exiting(getClass().getName(), "loadTransactionXref", size);
        return size > 0;
    }

    @Override
    public void createTransactionXref(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "createTransactionXref", new Object[]{inputRecord});
        getTransactionDAO().createTransactionXref(inputRecord);
        l.exiting(getClass().getName(), "createTransactionXref");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getTransactionDAO() == null)
            throw new ConfigurationException("The required property 'transactionDAO' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
        if (getBillingManager() == null)
            throw new ConfigurationException("The required property 'billingManager' is missing.");
        if (getJobQueueManager() == null)
            throw new ConfigurationException("The required property 'jobQueueManager' is missing.");
    }

    public TransactionDAO getTransactionDAO() {
        return m_transactionDAO;
    }

    public void setTransactionDAO(TransactionDAO transactionDAO) {
        m_transactionDAO = transactionDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    public BillingManager getBillingManager() {
        return m_billingManager;
    }

    public void setBillingManager(BillingManager billingManager) {
        m_billingManager = billingManager;
    }

    public JobQueueManager getJobQueueManager() {
        return m_jobQueueManager;
    }

    public void setJobQueueManager(JobQueueManager jobQueueManager) {
        m_jobQueueManager = jobQueueManager;
    }

    public SoftValidationManager getSoftValidationManager() {
        return m_softValidationManager;
    }

    public void setSoftValidationManager(SoftValidationManager softValidationManager) {
        m_softValidationManager = softValidationManager;
    }

    private TransactionDAO m_transactionDAO;
    private BillingManager m_billingManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private LockManager m_lockManager;
    private JobQueueManager m_jobQueueManager;
    private SoftValidationManager m_softValidationManager;

    protected static final String SAVE_PROCESSOR = BEAN_NAME;
    private static final String SAVE_OFFICIAL_WORKFLOW_PROCESS = "SaveOfficialWorkflow";
    private static final String SAVE_ENDORSEMENT_QUOTE_WORKFLOW_PROCESS = "SaveEndorsementQuoteWorkflow";
    private static final String SAVE_OFFICIAL_INITIAL_STATE = "invokeSaveOfficial";
    private static final String SAVE_ENDORSEMENT_QUOTE_INITIAL_STATE = "invokeSaveEndorsementQuote";
    private static final String SAVE_TAIL_OFFICIAL_WORKFLOW_PROCESS = "SaveTailOfficialWorkflow";
    private static final String SAVE_TAIL_OFFICIAL_INITIAL_STATE = "invokeSaveTailOfficial";
    private static final String RATE_WORKFLOW_PROCESS = "ValidateAndRateTransactionWorkflow";
    private static final String RATE_INITIAL_STATE = "invokeRatingProcess";
    private static final String DISCREPANCY_COMPARE_RECORDSET = "discrepancyCompareRecordSet";
    private static final String DISCREPANCY_TRANS_RECORDSET = "discrepancyTransRecordSet";
    private static final String RELATED_POLICY_ERRORS_RECORDSET = "relatedPolicyErrorsRecordSet";
    private static final String DEFAULT_EFFECTIVE_TO = "01/01/3000";
    private static final String NEW_TERM_EFFECTIVE_TO_DATE = "newTermEffectiveToDate";
    private static final String SAVE_WIP_WORKFLOW_PROCESS = "SaveWipWorkflow";
    private static final String SAVE_WIP_INITIAL_STATE = "invokeSaveWip";
    private static final String PROCESS_JOB = "processJob";
    public static final String RELATED_POLICY_DISPLAY_MODE_FOR_DISTINCT = "d_pm_related_policies_3";
    public static final String RELATED_POLICY_DISPLAY_MODE_FOR_GROUP = "d_pm_related_policies_group";
    public final static String WIP_B = "wipB";
    public static final String POLICY = "POLICY";
    public final static String IS_SAVE_REQUEST_COMPLETED_SUCCESSFULLY = "isSaveRequestCompletedSuccessfully";
}
