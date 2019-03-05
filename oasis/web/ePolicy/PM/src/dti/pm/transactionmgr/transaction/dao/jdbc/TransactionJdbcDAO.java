package dti.pm.transactionmgr.transaction.dao.jdbc;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.core.dao.BaseDAO;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyIdentifier;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.transactionmgr.transaction.dao.TransactionDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents methods that performs database operation on transaction entity object.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 9, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/05/2007       sxm         Modified createTransaction() to check if endorsementCode exists before get it.
 * 01/08/2008       fcb         isPolicyLocked and loadPolicyLockInfo added.
 * 01/15/2008       fcb         isCancelWipEditable added.
 * 03/13/2008       fcb         getJobCategory added.
 * 03/26/2008       yyh         loadAllPolicyAdminHistory added for issue 78338
 * 06/02/2008       yhchen      #80757, change the method signature of processBilling, return RecordType result which
 *                              conatins both error code and error message
 * 05/20/2010       syang       107932 - Modified loadDiscrepancyInterfaceStatus to get "msg" instead of "outMsg"
 *                              since the "out" is one of stripPrefixes defined.
 * 06/28/2010       fcb         109187: isJobSessionActive added.
 * 08/06/2010       syang       103797 - Modified loadAllProfessionalEntityTransaction() to pass RecordLoadProcessor.
 * 09/14/2010       dzhang      103813 - Added method processUndoTerm(), getPreviousTermInformation(),isUndoTermAvailable();
 *                                       Modified createTransaction() to overwrite termBaseRecordId for UNDO TERM.
 * 09/28/2010       sxm         109809 - Pass policy term non-base record PK to PM_RATE_POLICY() to correct the value of
 *                                       transaction_applied_term.policy_term_history_fk.
 * 11/02/2010       syang       111070 - Removed applyTransactions().
 * 04/07/2011       ryzhao      103801 - Added method loadAllDistinctRelatedPolicy(), getRelatedPolicyDisplayMode().
 * 04/11/2011       ryzhao      103801 - Deleted loadAllRelatedPolicy(), loadAllDistinctRelatedPolicy() which as no RecordLoadProcessor parameter.
 *                                       For those methods which invokes them, we can invoke the method with load processor
 *                                       and  use DefaultRecordLoadProcessor for second parameter.
 * 04/14/2011       dzhang      94232 - Add method isInactiveAssociated().
 * 05/01/2011       fcb         105791 - convertCoverage() added.
 * 08/01/2011       ryzhao      118806 - loadTransactionById() added.
 * 12/15/2011       fcb         119974 - added checkTaxUpdates and applyTaxUpdates
 * 06/26/2012       fcb         129528 - Added owsHandleNBPolicyError.
 * 09/06/2011       fcb         137198 - Removed owsHandleNBPolicyError
 * 10/04/2012       xnie        133766 - Added getMassJobCategory() for mass rerate.
 * 10/18/2012       xnie        133766 - Reverted prior fix.
 * 12/26/2012       awu         140186 - Added method getPolicyRelReasonCode(), saveFormTransaction().
 * 12/27/2012       tcg         139862 - Added getWarningMessage() and initWarning() to pop up warning message.
 * 06/21/2013       adeng       117011 - Modified createTransaction() to add field "transactionComment2" to object
 *                                       newTransaction if it exist.
 * 09/25/2013       fcb         145725 - Added isSnapshotConfigured, isProfEntityConfigured, isReplicationConfigured,
 *                                       isPolicyReplicationConfigured
 * 11/21/2013       fcb         148037 - Added loadNotifyTransactionCode, isProdNotifyConfigured, isFeesConfigured, isTaxConfigured
 * 03/11/2014       fcb         152685 - Oasis_Database_Interface.get_warning_msg replaced with PM_Warning.Get_Message_Str
 * 11/18/2014       fcb         157975 - isSkipNddValidationConfigured added
 * 12/19/2014       awu         159339 - Added validateRelatedPolicy.
 * 01/12/2015       awu         160142 - Renamed isFeesConfigured to isChargesNFeesConfigured
 *                                       and modified it to call PM_Environment.Is_ChargesNFees_Configured
 * 02/03/2015       wdang       160142 - Modified waiveFee() to replace "transLogId" with "tranLogId".
 * 08/28/2016       wdang       167534 - Adde createTransactionXref, loadTransactionXref.
 * 03/14/2017       mlm         184076 - Added DataRecordMapping for i_preview_b.
 * 04/26/2017       mlm         184827 - Refactored to initialize previewRequest in policy header correctly.
 * 06/28/2018       xnie        187070 - Added adjustFutureTermsGr().
 * ---------------------------------------------------
 */
public class TransactionJdbcDAO extends BaseDAO implements TransactionDAO {

    /**
     * Method to backup Renewal WIP Transaction
     *
     * @param inputRecord
     * @return
     */
    public Record backupRenewalWipTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "backupRenewalWipTransaction", new Object[]{inputRecord});
        Record outputRecord = null;
        String returnValue = "";

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Pending_Renewal.backup", mapping); // call it with p_transaction_log_pk
            outputRecord = spDao.executeUpdate(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to Backup Renewal WIP Transaction.", e);
            l.throwing(getClass().getName(), "backupRenewalWipTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "deleteWIPTransaction", outputRecord);
        return outputRecord;
    }

    /**
     * Method to delete the billing Relation Before delete WIP Transaction
     *
     * @param inputRecord
     * @return
     */

    public Record deleteBillingRelationForWiPTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "deleteWIPTransaction", new Object[]{inputRecord});
        Record outputRecord = null;

        try {

            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("policyEffectiveDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("policyEntityId", "policyHolderNameEntityId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fm_Billing_Relation_Pol_Del", mapping); // call it with p_transaction_log_pk
            outputRecord = spDao.executeUpdate(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to Delete WIP Transaction.", e);
            l.throwing(getClass().getName(), "deleteWIPTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "deleteWIPTransaction", outputRecord);
        return outputRecord;
    }

    /**
     * Method to perform the actual delete, if failed to delete, a app exception is thrown
     *
     * @param inputRecord
     * @return void
     */
    public int deleteWipTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "deleteWIPTransaction", new Object[]{inputRecord});
        Record outputRecord = null;
        int returnValue = -1;

        try {
            // now we are actually to delete the wip transaction
            // Execute the stored procedure Pm_Web_Transaction.Delete_WIP
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("tranId", "transactionLogId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_lock.Delete_WIP", mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getIntegerValue(spDao.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to Delete WIP Transaction.", e);
            l.throwing(getClass().getName(), "deleteWIPTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "deleteWIPTransaction", outputRecord);
        return returnValue;
    }

    /**
     * Method to dertermine if the given transaction is a batch RenewalWIP Transaction
     *
     * @param inputRecord
     * @return
     */
    public boolean isBatchRenewWip(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isBatchRenewWip", new Object[]{inputRecord});
        boolean isBatchRenewWipTransaction = false;

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Is_Batch_Renew_Wip", mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            isBatchRenewWipTransaction = outputRecordSet.getSummaryRecord().getBooleanValue(spDao.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if it is a Batch REnewal WIP transaction.", e);
            l.throwing(getClass().getName(), "isBatchRenewWip", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "isBatchRenewWip", inputRecord);
        return isBatchRenewWipTransaction;
    }

    /**
     * This method returns a transaction bean that represents information about the last transaction for the provided
     * term and last transaction of the current policy header.
     *
     * @param policyHeader
     * @return Transaction
     */
    public Transaction loadLastTransactionInfoForTerm(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "loadLastTransactionInfoForTerm", new Object[]{policyHeader});

        Transaction tranInfo = new Transaction();

        try {
            //Map the input values
            Record input = new Record();
            input.setFieldValue("transactionLogId", policyHeader.getLastTransactionId());

            //Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Get_Trans_Detail");
            Record output = spDao.executeUpdate(input);

            // Map the output record to the Transaction bean
            RecordBeanMapper recBeanMapper = new RecordBeanMapper();
            recBeanMapper.map(output, tranInfo);

            // Manually set those items that cannot be mapped
            tranInfo.setTransactionLogId(policyHeader.getLastTransactionId());

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load the Transaction Information.", e);
            l.throwing(getClass().getName(), "loadLastTransactionInfoForTerm", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "loadLastTransactionInfoForTerm", tranInfo);

        return tranInfo;
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

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Sel_Transaction_Summary", mapping);
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllTransactionSummary", se);
            l.throwing(getClass().getName(), "loadAllTransactionSummary", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransactionSummary", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Load transaction information by transaction id
     *
     * @param inputRecord input record that contains transaction pk
     * @return transaction information
     */
    public Record loadTransactionById(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTransactionById", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Sel_Transaction_Info_By_Id");
        RecordSet outRecordSet = null;
        Record outRecord = null;
        try {
            outRecordSet = sp.execute(inputRecord);
            if (outRecordSet.getSize() > 0) {
                outRecord = outRecordSet.getFirstRecord();
            }
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load the Transaction Information.", se);
            l.throwing(getClass().getName(), "loadTransactionById", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTransactionById", outRecordSet);
        }
        return outRecord;
    }

    /**
     * Create's a database transaction based upon user entered parameters
     *
     * @param policyHeader                 Current policy header with data populated
     * @param transactionEffectiveFromDate User entered transaction effective date
     * @param transactionCode              Code value to drive the transaction type and code created
     * @return Transaction
     * @parma inputRecord                   a record containing at least accountingDate, endorsementCode, transactionComment
     */
    public Transaction createTransaction(PolicyHeader policyHeader, Record inputRecord,
                                         String transactionEffectiveFromDate,
                                         TransactionCode transactionCode) {
        Logger l = LogUtils.enterLog(getClass(), "createTransaction", new Object[]{policyHeader, inputRecord, transactionEffectiveFromDate, transactionCode});

        Transaction newTransaction = new Transaction();

        try {
            // Initially populate the new transaction object
            newTransaction.setPolicyId(policyHeader.getPolicyId());
            newTransaction.setTransEffectiveFromDate(transactionEffectiveFromDate);
            newTransaction.setTransAccountingDate(inputRecord.getStringValue("accountingDate"));
            newTransaction.setTransactionCode(transactionCode);
            String endorsementCode = inputRecord.getStringValue("endorsementCode", "");
            newTransaction.setEndorsementCode(endorsementCode);
            newTransaction.setTransactionComments(TransactionFields.getTransactionComment(inputRecord));
            if (inputRecord.hasStringValue(TransactionFields.TRANSACTION_COMMENT2)) {
                newTransaction.setTransactionComment2(TransactionFields.getTransactionComment2(inputRecord));
            }
            // Set transaction effective to date, this is for HIROC PM MODS issue#86047
            if (inputRecord.hasStringValue("effectiveToDate")) {
                String sEffToDate = inputRecord.getStringValue("effectiveToDate");
                if (!StringUtils.isBlank(sEffToDate)) {
                    newTransaction.setTransEffectiveToDate(sEffToDate);
                }
            }

            if (inputRecord.hasStringValue("convertionType")) {
                newTransaction.setConvertionType(inputRecord.getStringValue("convertionType"));
            }

            // Map the input values
            Record input = new Record();
            RecordBeanMapper recBeanMapper = new RecordBeanMapper();
            recBeanMapper.map(newTransaction, input);
            input.setFieldValue("termBaseRecordId", inputRecord.getStringValue("termBaseRecordId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Create_Transaction");
            Record output = spDao.executeUpdate(input);

            // Map the output for the transaction log pk, status, and transaction type
            recBeanMapper.map(output, newTransaction);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to create new transaction.", e);
            l.throwing(getClass().getName(), "createTransaction", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "createTransaction", newTransaction);

        return newTransaction;
    }

    /**
     * Update an exisitng transaction with a new status
     *
     * @param trans                 Current transaction in progress
     * @param transactionStatusCode New transaction status code
     * @return Transaction
     */
    public Transaction updateTransactionStatus(Transaction trans, TransactionStatus transactionStatusCode) {
        Logger l = LogUtils.enterLog(getClass(), "updateTransactionStatus", new Object[]{trans, transactionStatusCode});

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transStatus", "transactionStatusCode"));

            // Map the input values
            Record input = new Record();
            RecordBeanMapper recBeanMapper = new RecordBeanMapper();
            recBeanMapper.map(trans, input);
            TransactionFields.setTransactionStatusCode(input, transactionStatusCode);

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Update_Transaction", mapping);
            Record output = spDao.executeUpdate(input);

            // Check the return code for success
            if (!output.getStringValue("returnCode").equalsIgnoreCase("SUCCESS")) {
                throw new AppException("Unable to update transaction status for pk: " + trans.getTransactionLogId());
            }
            else {
                trans.setTransactionStatusCode(transactionStatusCode);
            }

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update transaction status.", e);
            l.throwing(getClass().getName(), "updateTransactionStatus", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "updateTransactionStatus", trans);

        return trans;
    }

    /**
     * Determine if an agent exists on the current policy.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isAgentExist(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isAgentExist", new Object[]{inputRecord});

        boolean agentExist = false;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fm_Agent_Relation_Exist");
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            agentExist = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Fm_Agent_Relation_Exist.", e);
            l.throwing(getClass().getName(), "isAgentExist", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isAgentExist", Boolean.valueOf(agentExist));

        return agentExist;
    }

    /**
     * Determine if an underwriter exists on the current policy/quote.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isUnderwriterExist(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isUnderwriterExist", new Object[]{inputRecord});

        boolean underwriterExist = false;

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Underwriter_Exist", mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            underwriterExist = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Underwriter_Exist.", e);
            l.throwing(getClass().getName(), "isUnderwriterExist", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isUnderwriterExist", Boolean.valueOf(underwriterExist));

        return underwriterExist;
    }

    /**
     * Determine if collateral is required on the current policy.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isCollateralRequired(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isCollateralRequired", new Object[]{inputRecord});

        boolean collateralRequired = false;

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("polType", "policyTypeCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("issCompId", "issueCompanyEntityId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Check_Collateral_Saveas", mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            collateralRequired = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Check_Collateral_Saveas.", e);
            l.throwing(getClass().getName(), "isCollateralRequired", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isCollateralRequired", Boolean.valueOf(collateralRequired));

        return collateralRequired;
    }

    /**
     * Process logic to insert a default agent
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     */
    public void insertDefaultAgent(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "insertDefaultAgent", new Object[]{inputRecord});

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Ins_Default_Agent", mapping);
            Record output = spDao.executeUpdate(inputRecord);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Ins_Default_Agent.", e);
            l.throwing(getClass().getName(), "insertDefaultAgent", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "insertDefaultAgent");
    }

    /**
     * Determine if the selected or defaulted agent is valid for the policy/term.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return String
     */
    public String isAgentValid(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isAgentValid", new Object[]{inputRecord});

        String returnValue = null;

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("cycle", "policyCycleCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Required.Validate_Policy_Agent", mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);

            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getStringValue("return");

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Validate_Required.Validate_Policy_Agent.", e);
            l.throwing(getClass().getName(), "isAgentValid", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isAgentValid", returnValue);

        return returnValue;
    }


    /**
     * Determine if the a billing relationship has been setup for the policy.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isBillingRelationValid(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isBillingRelationValid", new Object[]{inputRecord});

        boolean validBillingRelation = false;

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("accountingDate", "transAccountingDate"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fm_Check_Tranlog_Relation", mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);

            // Pull the stored procedure output information
            validBillingRelation = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Fm_Check_Tranlog_Relation.", e);
            l.throwing(getClass().getName(), "isBillingRelationValid", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isBillingRelationValid", Boolean.valueOf(validBillingRelation));

        return validBillingRelation;
    }

    /**
     * Determine if the a particular save option is available based upon configuration.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isSaveOptionAvailable(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isSaveOptionAvailable", new Object[]{inputRecord});

        boolean returnValue = false;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Is_Save_Code_Allowed");
            RecordSet outputRecordSet = spDao.execute(inputRecord);

            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Web_Transaction.Is_Save_Code_Allowed.", e);
            l.throwing(getClass().getName(), "isSaveOptionAvailable", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isSaveOptionAvailable", Boolean.valueOf(returnValue));

        return returnValue;
    }

    /**
     * Perform replication or data for the business component's data being saved.
     *
     * @param inputRecord Record containing term, transaction level details
     */
    public void doReplication(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "doReplication", new Object[]{inputRecord});

        try {
            // Set input record values
            inputRecord.setFieldValue("replicateLevel", "RISK,COVERAGE,COVERAGE_CLASS,COVERAGE_COMPONENT");

            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "policyTermHistoryId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEffDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Replicate.Process_Replicate", mapping);
            Record output = spDao.executeUpdate(inputRecord);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Replicate.Process_Replicate.", e);
            l.throwing(getClass().getName(), "doReplication", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "doReplication");
    }


    /**
     * Perform OAW custom layer invokation for the save as wip transaction
     *
     * @param inputRecord Record containing term, transaction level details
     */
    public void doWipCustomLayer(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "doWipCustomLayer", new Object[]{inputRecord});

        try {
            // Set specific input record values
            Record customInputRecord = inputRecord;
            customInputRecord.setFieldValue("time", "POST");
            customInputRecord.setFieldValue("parms", "MISC_TYPE^WIP_SAVE^");

            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("trans", "transactionLogId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Custom.Process_Misc", mapping);
            Record output = spDao.executeUpdate(customInputRecord);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Custom.Process_Misc.", e);
            l.throwing(getClass().getName(), "doWipCustomLayer", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "doWipCustomLayer");
    }

    /**
     * Call stored procedure to renumber, clean-up wip slot issues
     *
     * @param inputRecord Record containing term, transaction level details
     */
    public void renumberWipSlots(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "renumberWipSlots", new Object[]{inputRecord});

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renumber_Wip_Slots", mapping);
            Record output = spDao.executeUpdate(inputRecord);

            // Check the return code for success
            if (output.getLongValue("rc").longValue() != 0) {
                throw new AppException("Unable to renumber slots for transaction: " + TransactionFields.getTransactionLogId(inputRecord));
            }

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Renumber_Wip_Slots.", e);
            l.throwing(getClass().getName(), "renumberWipSlots", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "renumberWipSlots");
    }

    /**
     * Perform outputs document processing.
     *
     * @param inputRecord Record containing term, transaction level details
     * @return int integer value indicating success/failure.  Value less than 0 indicates failure.
     */
    public int processOutput(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processOutput", new Object[]{inputRecord});

        int returnCode;

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("isPreviewB", "previewRequest"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Call_Doc", mapping);
            returnCode = spDao.execute(inputRecord).getSummaryRecord().getIntegerValue("retCode").intValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Call_Doc.", e);
            l.throwing(getClass().getName(), "processOutput", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "processOutput", String.valueOf(returnCode));
        return returnCode;
    }

    /**
     * Method to get the max accounting date for a policy term history id
     * that is contained witin the record
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
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_web_transaction.get_Max_Accounting_Date");

        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute Pm_Web_Transaction.get_Max_Accounting_Date.", e);
            l.throwing(getClass().getName(), "getMaxAccountingDate", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getMaxAccountingDate", returnValue);
        return returnValue;
    }

    /**
     * Method to get the latest accouting date in oasis, It does not compare the sysdate
     *
     * @param inputRecord record that contains accountingDate field
     * @return String        date string in mm/dd/yyyy format
     */
    public String getLatestAccountingDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLatestAccountingDate", new Object[]{inputRecord});
        }
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_web_transaction.get_latest_accounting_date");

        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute Pm_Web_Transaction.get_latest_accounting_date.", e);
            l.throwing(getClass().getName(), "getLatestAccountingDate", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getLatestAccountingDate", returnValue);
        return returnValue;
    }

    /**
     * check if the renewal reason is configured as an endorsement reason
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isEndorsementCodeConfiged(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isEndorsementCodeValid", new Object[]{inputRecord});

        boolean returnValue = false;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_web_utility.is_endorsement_code");
            RecordSet outputRecordSet = spDao.execute(inputRecord);

            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to pm_web_utility.is_endorsement_code.", e);
            l.throwing(getClass().getName(), "isRelChildCountConfigured", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isRelChildCountConfigured", Boolean.valueOf(returnValue));

        return returnValue;
    }

    /**
     * check if the bypass of risk relation configured in pm_attribute
     *
     * @param inputRecord Record containing policy_type
     * @return boolean
     */
    public boolean isBypassRiskRelConfigured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isBypassRiskRelConfigured", new Object[]{inputRecord});

        boolean returnValue = false;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_web_transaction.Is_Bypass_Risk_Rel_Configured");
            RecordSet outputRecordSet = spDao.execute(inputRecord);

            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to pm_web_transaction.Is_Bypass_Risk_Rel_Configured.", e);
            l.throwing(getClass().getName(), "isBypassRiskRelConfigured", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isBypassRiskRelConfigured", Boolean.valueOf(returnValue));

        return returnValue;
    }

    /**
     * check if the risk child relation configured in pm_attribute
     *
     * @param inputRecord Record containing policy_type
     * @return boolean
     */
    public boolean isRelChildCountConfigured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isRelChildCountConfigured", new Object[]{inputRecord});

        boolean returnValue = false;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_web_transaction.Is_Rel_Child_Count_Configured");
            RecordSet outputRecordSet = spDao.execute(inputRecord);

            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to pm_web_transaction.Is_Rel_Child_Count_Configured.", e);
            l.throwing(getClass().getName(), "isRelChildCountConfigured", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isRelChildCountConfigured", Boolean.valueOf(returnValue));

        return returnValue;
    }


    /**
     * Validate transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return String
     */
    public String validateTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateTransaction", new Object[]{inputRecord});

        String returnValue;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Pm_Web_Validate_Policy", mapping);
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue("return");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate transaction.", e);
            l.throwing(getClass().getName(), "validateTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateTransaction", returnValue);
        return returnValue;
    }

    /**
     * update transaction comments
     *
     * @param inputRecord input record
     */
    public void updateTransactionComments(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateTransactionComments", new Object[]{inputRecord});
        }
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("comments", "transactionComment"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_UPDATE_TRANS_COMMENTS", mapping);
            spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to update transaction comments", e);
            l.throwing(getClass().getName(), "updateTransactionComments", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "updateTransactionComments");
    }

    /**
     * change term expiration date
     *
     * @param inputRecord input record contains transactionLogId,policyId,termBaseId,newEffTo,mode
     */
    public void changeTermExpirationDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeTermExpirationDate", new Object[]{inputRecord});
        }
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("newEffTo", "newTermEffectiveToDate"));
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_ENDORSE_DATES.CHANGE_TERM", mapping);
            spDao.execute(inputRecord);
        }
        catch (Exception e) {
            e.printStackTrace();
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to change term expiration date.", e);
            l.throwing(getClass().getName(), "changeTermExpirationDate", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "changeTermExpirationDate");
    }

    /**
     * check Policy Holder Status
     *
     * @param inputRecord
     * @return returns string "OFFICIAL" or "TEMP"
     */
    public String checkPolicyHolderStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkPolicyHolderStatus", new Object[]{inputRecord});
        }
        String status;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_End.Check_Hpholder_Status", mapping);
            RecordSet rs = spDao.execute(inputRecord);
            status = rs.getSummaryRecord().getStringValue("returnValue");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to check policy holder status.", e);
            l.throwing(getClass().getName(), "checkPolicyHolderStatus", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "checkPolicyHolderStatus", status);
        }
        return status;
    }

    /**
     * add policy administrator
     *
     * @param inputRecord input record
     */
    public void addPolicyAdministrator(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addPolicyAdministrator", new Object[]{inputRecord});
        }
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "policyHolderNameEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fromDt", "effectiveFromDate"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_End.Save_Polholder", mapping);
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to change policy administrator.", e);
            l.throwing(getClass().getName(), "addPolicyAdministrator", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "addPolicyAdministrator");
    }

    /**
     * update policy administrator
     *
     * @param inputRecord input record
     */
    public void updatePolicyAdministrator(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updatePolicyAdministrator", new Object[]{inputRecord});
        }
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "policyHolderNameEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "effectiveToDate"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Endorse.Change_Polholder", mapping);
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to change policy administrator.", e);
            l.throwing(getClass().getName(), "updatePolicyAdministrator", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "updatePolicyAdministrator");
    }

    /**
     * Returns a RecordSet loaded with validation errors
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return RecordSet
     */
    public RecordSet loadAllValidationError(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllValidationError", new Object[]{inputRecord});

        RecordSet rs;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Error", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load validation errors.", e);
            l.throwing(getClass().getName(), "loadAllValidationError", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllValidationError", rs);
        return rs;
    }

    /**
     * Clean up all validation error
     *
     * @param inputRecord
     */
    public void deleteAllValidationError(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllValidationError", new Object[]{inputRecord});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Del_Error", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete validation error", e);
            l.throwing(getClass().getName(), "deleteAllValidationError", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllValidationError");
        }
    }

    /**
     * Check if taxes are configured for the current customer
     * <p/>
     *
     * @return String
     */
    public String isTaxConfigured() {
        Logger l = LogUtils.enterLog(getClass(), "isTaxConfigured");

        String rtn = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Environment.Is_Tax_Configured");
        try {
            rtn = spDao.execute(new Record()).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing isTaxConfigured.", e);
            l.throwing(getClass().getName(), "isTaxConfigured", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isTaxConfigured", rtn);

        return rtn;
    }

    /**
     * Check if charges fees are configured for the current customer
     * <p/>
     *
     * @return String
     */
    public String isChargesNFeesConfigured() {
        Logger l = LogUtils.enterLog(getClass(), "isChargesNFeesConfigured");

        String rtn = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Environment.Is_ChargesNFees_Configured");
        try {
            rtn = spDao.execute(new Record()).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing isFeesConfigured.", e);
            l.throwing(getClass().getName(), "isChargesNFeesConfigured", ae);
            throw ae;
        }
        
        l.exiting(getClass().getName(), "isChargesNFeesConfigured", rtn);

        return rtn;
    }

    /**
     * Check if fee exists related to the current transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return YesNoFlag
     */
    public YesNoFlag isFeeDefined(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isFeeDefined", new Object[]{inputRecord});

        YesNoFlag returnValue;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "transactionLogId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Fee.Pm_Waive_Fees", mapping);
            returnValue = YesNoFlag.getInstance(spDao.execute(inputRecord).getSummaryRecord().getStringValue("return"));
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to check if fee exists.", e);
            l.throwing(getClass().getName(), "isFeeDefined", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isFeeDefined", returnValue);
        return returnValue;
    }


    /**
     * Waive fee related to the current transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     */
    public void waiveFee(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "waiveFee", new Object[]{inputRecord});

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "transactionLogId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Fee.Pm_update_tran_waive", mapping);
            spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to waive fee.", e);
            l.throwing(getClass().getName(), "waiveFee", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "waiveFee");
    }

    /**
     * Check if tax is configured for a state
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return YesNoFlag
     */
    public YesNoFlag isPolicyTaxConfigured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isPolicyTaxConfigured", new Object[]{inputRecord});

        YesNoFlag returnValue;
        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Is_Tax_Configured");
            returnValue = YesNoFlag.getInstance(spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD));
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to check if tax is configured.", e);
            l.throwing(getClass().getName(), "isPolicyTaxConfigured", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isPolicyTaxConfigured", returnValue);
        return returnValue;
    }

    /**
     * Validate premium
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return int
     */
    public int validatePremium(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validatePremium", new Object[]{inputRecord});

        int returnValue;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Risk_Premium", mapping);
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getIntegerValue("return").intValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate premium.", e);
            l.throwing(getClass().getName(), "validatePremium", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validatePremium", Integer.toString(returnValue));
        return returnValue;
    }

    /**
     * Validates if open claims exist for the changed risk/coverage's of the current transaction.
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return YesNoFlag
     */
    public YesNoFlag validateOpenClaims(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateOpenClaims", new Object[]{inputRecord});

        YesNoFlag returnValue = YesNoFlag.N;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Check_Open_Claims", mapping);
            returnValue = YesNoFlag.getInstance(spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD));
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate open claims.", e);
            l.throwing(getClass().getName(), "validateOpenClaims", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateOpenClaims", returnValue);
        return returnValue;
    }

    /**
     * Rate transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return String
     */
    public String rateTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "rateTransaction", new Object[]{inputRecord});

        String returnValue;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transLog", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("polId", "policyId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "policyTermHistoryId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Rate_Policy", mapping);
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue("statusCode");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to rate transaction.", e);
            l.throwing(getClass().getName(), "rateTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "rateTransaction", returnValue);
        return returnValue;
    }

    /**
     * Returns a RecordSet loaded with product notifications
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return RecordSet
     */
    public RecordSet loadAllProductNotifications(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllProductNotifications", new Object[]{inputRecord});

        RecordSet rs;
        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Transaction.sel_product_notify");
            rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load product notification.", e);
            l.throwing(getClass().getName(), "loadAllProductNotifications", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllProductNotifications", rs);
        return rs;
    }

    /**
     * Returns indicator for next step based on the user response
     * <p/>
     *
     * @param inputRecord Record containing current transaction information and product notification response
     * @return int
     */
    public int productNotificationResponse(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "productNotificationResponse", new Object[]{inputRecord});

        int returnValue;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("prodNotifyId", "productNotifyId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Notify.response", mapping);
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getIntegerValue("return").intValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to send response.", e);
            l.throwing(getClass().getName(), "productNotificationResponse", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "productNotificationResponse", String.valueOf(returnValue));
        return returnValue;
    }

    /**
     * Save the changes of term dates
     *
     * @param inputRecord
     * @return
     */
    public int saveTermDates(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveTermDates", new Object[]{inputRecord});

        int retCode;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("newEffDate", "newTermEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("newExpDate", "newTermEffectiveToDate"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Change_Term_Date", mapping);
            retCode = spDao.execute(inputRecord).getSummaryRecord().getIntegerValue("retCode").intValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to change term dates.", e);
            l.throwing(getClass().getName(), "saveTermDates", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveTermDates", String.valueOf(retCode));
        return 0;
    }

    /**
     * Calls cancel final logic during save as official to handle any cancellations
     * performed during a renewal wip.
     *
     * @param inputRecord containing current transaction/policy information
     */
    public void performRenewalRiskCancelFinal(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "performCancelFinal", new Object[]{inputRecord});

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Cancel_Final.Cancel_Final_Risk_In_Renew");
            RecordSet rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to call Pm_Cancel_Final.Cancel_Final_Risk_In_Renew: " + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "performCancelFinal", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "performCancelFinal");
    }

    /**
     * Calls cancel final logic during save as official to handle any cancellations
     * performed during any type of cancellation transaction.
     *
     * @param inputRecord containing current transaction/policy information
     */
    public void performPolicyCancelFinal(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "performPolicyCancelFinal", new Object[]{inputRecord});

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Cancel_Final.Cancel_Policy");
            RecordSet rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to call Pm_Cancel_Final.Cancel_Policy: " + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "performPolicyCancelFinal", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "performPolicyCancelFinal");
    }

    /**
     * Calls reinstate final logic during save as official to handle specialized
     * reinstatement logic
     *
     * @param inputRecord containing current transaction/policy information
     */
    public void performReinstateFinal(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "performReinstateFinal", new Object[]{inputRecord});

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("tranTypeCode", "transactionTypeCode"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Reinst_Final.Reinst_Policy", mapping);
            RecordSet rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to call Pm_Reinst_Final.Reinstate_Policy: " + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "performReinstateFinal", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "performReinstateFinal");
    }

    /**
     * Calls the main stored procedure to save policy data into Official Mode.
     *
     * @param inputRecord containing current transaction/policy information
     * @return RecordSet containing any related policy save errors
     */
    public RecordSet issuePolicy(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "issuePolicy", new Object[]{inputRecord});

        RecordSet rs = null;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Issue_Policy.Web_Save_As");
            rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to call Issue_Policy.Save_As: " + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "issuePolicy", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "issuePolicy", rs);
        return rs;
    }

    /**
     * Determines if a premium discrepancy exists between PM and FM for the current transaction
     *
     * @param inputRecord containing current transaction/policy information
     * @return RecordSet containing any discrepancy records
     */
    public RecordSet checkPremiumDelta(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "checkPremiumDelta", new Object[]{inputRecord});

        RecordSet rs = null;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fm_Sel_Pmfm_Verify_Delta");
            rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Fm_Sel_Pmfm_Verify_Delta: " + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "checkPremiumDelta", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "checkPremiumDelta", rs);
        return rs;
    }

    /**
     * Perform billing interface between PM and FM, Pm_Fm_Billing
     *
     * @param inputRecord containing current transaction/policy information
     * @return record contains the return code and error message from the stored procedure
     */
    public Record processBilling(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processBilling", new Object[]{inputRecord});

        Record resultRecord;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Fm_Billing");
            RecordSet rs = spDao.execute(inputRecord);
            resultRecord =  rs.getSummaryRecord();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Fm_Billing: " + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "processBilling", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "processBilling", String.valueOf(resultRecord));
        return resultRecord;
    }

    /**
     * Load discrepancy interface status information for the PM/FM Discrepancy page.
     *
     * @param inputRecord record of transaction information being saved
     * @return String containing interface status message for the user
     */
    public String loadDiscrepancyInterfaceStatus(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDiscrepancyInterfaceStatus", new Object[]{inputRecord});

        String returnCode = null;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fmn_Control_Pol_Int_Queue");
            RecordSet rs = spDao.execute(inputRecord);

            returnCode = rs.getSummaryRecord().getStringValue("msg");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Fmn_Control_Pol_Int_Queue: " + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "loadDiscrepancyInterfaceStatus", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadDiscrepancyInterfaceStatus", returnCode);
        return returnCode;
    }

    /**
     * Load discrepancy interface compare information for the PM/FM Discrepancy page.
     *
     * @param inputRecord record of transaction information being saved
     * @return RecordSet containing PM/FM interface comparisson data for the user
     */
    public RecordSet loadDiscrepancyCompareInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDiscrepancyCompareInfo", new Object[]{inputRecord});

        RecordSet rs;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fm_Sel_Pmfm_Verify_Delta");
            rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Fm_Sel_Pmfm_Verify_Delta: " + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "loadDiscrepancyCompareInfo", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadDiscrepancyCompareInfo", rs);
        return rs;
    }

    /**
     * Load discrepancy interface transactional compare information for the PM/FM Discrepancy page.
     *
     * @param inputRecord record of transaction information being saved
     * @return RecordSet containing PM/FM interface transactional comparisson data for the user
     */
    public RecordSet loadDiscrepancyTransCompareInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDiscrepancyTransCompareInfo", new Object[]{inputRecord});

        RecordSet rs;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fm_Sel_Pmfm_Compare_Premium");
            rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Fm_Sel_Pmfm_Compare_Premium: " + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "loadDiscrepancyTransCompareInfo", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadDiscrepancyTransCompareInfo", rs);
        return rs;
    }

    /**
     * Load discrepancy interface information for transactions yet to be processed
     *
     * @param inputRecord record of transaction information being saved
     * @return RecordSet containing PM transactions yet to be processed in FM
     */
    public RecordSet loadDiscrepancyIntfcInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDiscrepancyIntfcInfo", new Object[]{inputRecord});

        RecordSet rs;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fm_Sel_Pm_Intfc_Incomplete");
            rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Fm_Sel_Pm_Intfc_Incomplete: " + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "loadDiscrepancyIntfcInfo", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadDiscrepancyIntfcInfo", rs);
        return rs;
    }

    /**
     * Validate whether the term is eligible for OOS Endorsement or not by function Pm_Valid_Oos_Term.
     *
     * @param inputRecord
     * @return
     */
    public Record vaidateOosEndorseTerm(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "vaidateOosEndorseTerm", new Object[]{inputRecord});
        Record record;

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("oosTerms", "endorsePriorTerm"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Valid_Oos_Term", mapping);
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Valid_Oos_Term: "
                + TransactionFields.getTransactionLogId(inputRecord), e);
            l.throwing(getClass().getName(), "vaidateOosEndorseTerm", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "vaidateOosEndorseTerm", record);
        return record;
    }

    /**
     * loadAllTransaction data
     * <p/>
     *
     * @param inputRecord         input record
     * @param recordLoadProcessor the load processor
     * @return the result which met the condition
     */
    public RecordSet loadAllTransaction(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransaction", new Object[]{inputRecord, recordLoadProcessor});
        }
        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_TRANSACTION.SEL_TRANSACTION_DATA");
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load data.", e);
            l.throwing(getClass().getName(), "loadAllTransaction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransaction", new Object[]{rs});
        }
        return rs;
    }

    /**
     * load the transaction
     * <p/>
     *
     * @param inputRecord input record
     * @return the result which met the condition
     */
    public RecordSet loadAllChangeDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllChangeDetail", new Object[]{inputRecord});
        }
        RecordSet rs = null;
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", RequestIds.TRANSACTION_ID));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_TRANSACTION.SEL_CHANGE_DETAIL", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load data", e);
            l.throwing(getClass().getName(), "loadAllChangeDetail", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllChangeDetail", new Object[]{rs});
        }
        return rs;
    }

    /**
     * load the transaction
     * <p/>
     *
     * @param inputRecord input record
     * @return the result which met the condition
     */
    public RecordSet loadAllTransactionForm(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransactionForm", new Object[]{inputRecord});
        }
        RecordSet rs = null;
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", RequestIds.TRANSACTION_ID));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", RequestIds.TERM_BASE_RECORD_ID));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_TRANSACTION.SEL_TRANSACTION_FORM", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load data", e);
            l.throwing(getClass().getName(), "loadAllTransactionForm", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransactionForm", new Object[]{rs});
        }
        return rs;
    }

    /**
     * save/update the transaction data
     * <p/>
     *
     * @param inputRecords transaction record set
     * @return number of the updated rows
     */
    public int saveTransactionDetail(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveTransactionDetail", new Object[]{inputRecords});
        }
        int updateCount;
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("forceRerate", "forceRerateB"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Update_Trans_Comments", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save data.", e);
            l.throwing(getClass().getName(), "saveTransactionDetail", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveTransactionDetail");
        }
        return updateCount;
    }

    /**
     * load related policies' information
     *
     * @param inputRecord, recordLoadProcessor
     * @return recordSet
     */
    public RecordSet loadAllRelatedPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRelatedPolicy", new Object[]{inputRecord});
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("polId", "policyId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Related_Pols", mapping);
            RecordSet rs = spDao.execute(inputRecord, recordLoadProcessor);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllRelatedPolicy", rs);
            }
            return rs;
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load related policies", e);
            l.throwing(getClass().getName(), "loadAllRelatedPolicy", ae);
            throw ae;
        }
    }

    /**
     * load related policies' information for distinct
     *
     * @param inputRecord contains policy_id, term_eff, term_exp, trans_fk, and time(pre or post), recordLoadProcessor
     * @return related policy resultset
     */
    public RecordSet loadAllDistinctRelatedPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllDistinctRelatedPolicy", new Object[]{inputRecord});

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("polId", "policyId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Related_Pols_Distinct", mapping);
            RecordSet rs = spDao.execute(inputRecord, recordLoadProcessor);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllDistinctRelatedPolicy", rs);
            }
            return rs;
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load related policies for distinct", e);
            l.throwing(getClass().getName(), "loadAllDistinctRelatedPolicy", ae);
            throw ae;
        }
    }

    /**
     * Get related policy display mode per pm attribute
     *
     * @param inputRecord contains policy_type
     * @return pm attribute value indicate which display mode it is
     */
    public String getRelatedPolicyDisplayMode(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRelatedPolicyDisplayMode", new Object[]{inputRecord});
        }

        String result = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Rel_Policies_View_Mode");

        try {
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed get related policy display mode.", e);
            l.throwing(getClass().getName(), "getRelatedPolicyDisplayMode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRelatedPolicyDisplayMode", result);
        }
        return result;
    }

    /**
     * get the parent-relation count and the child-relation count
     *
     * @param inputRecord (policyId, termEffectiveFromDate, termEffectiveToDate, transactionLogId, time)
     * @return record (parent_cnt, child_cnt)
     */
    public Record checkRelatedPolicy(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "checkRelatedPolicy", new Object[]{inputRecord});

        try {
            // Execute the function
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Check_XPolicy_Rel", mapping);

            Record output = spDao.execute(inputRecord).getSummaryRecord();
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "checkRelatedPolicy", output);
            }
            return output;
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check related policies", e);
            l.throwing(getClass().getName(), "checkRelatedPolicy", ae);
            throw ae;
        }
    }

    /**
     * To get count of bypass skip rating.
     *
     * @param inputRecord
     * @return number of the bypass skip rating
     */
    public int getSkipRatingCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSkipRatingCount", new Object[]{inputRecord});
        }

        int returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Is_Skip_Rating");
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get bypass skip rating count.", e);
            l.throwing(getClass().getName(), "getSkipRatingCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSkipRatingCount");
        }
        return returnValue;
    }

    /**
     * Method that returns a boolean value that indicates whether the policy is locked.
     * <p/>
     *
     * @param policyIdentifier an instance of Policy Identifier with all information loaded.
     * @return boolean true, if the policy is lockedd; otherwise, false.
     */
    public boolean isPolicyLocked(PolicyIdentifier policyIdentifier) {
        Logger l = LogUtils.enterLog(getClass(), "isPolicyLocked", new Object[]{policyIdentifier});
        boolean isLocked = false;

        if (policyIdentifier == null) {
            throw new AppException("Invalid policy information (null) passed to PolicyJdbcDAO.isPolicyLocked method.");
        }
        try {
            RecordSet rs = loadPolicyLockInfo(policyIdentifier.getPolicyNo());
            if (rs.getSize() != 0) {
                Record r = rs.getFirstRecord();
                isLocked = (r.getStringValue("lockedBy") == null) ? false : true;
            }
            else {
                throw new AppException("Unable to load policy lock information for policy: " + policyIdentifier.getPolicyNo());
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load policy lock information for policy: " + policyIdentifier.getPolicyNo(), e);
            l.throwing(getClass().getName(), "isPolicyLocked", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "isPolicyLocked", String.valueOf(isLocked));
        return isLocked;
    }

    /**
     * Method that returns an instance of a recordset object with policy lock information for the provided
     * policy number.
     * <p/>
     *
     * @param policyNo policy number
     * @return RecordSet an instance of the recordset result object that contains policy lock information.
     */
    public RecordSet loadPolicyLockInfo(String policyNo) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyLockInfo", new Object[]{policyNo});

        RecordSet rs = null;

        try {
            //Map the input values
            Record input = new Record();
            input.setFieldValue("policyNo", policyNo);

            //Execute the stored proc returning the ref cursor
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Get_Lock_Info");
            rs = spDao.execute(input);
            if (rs.getSize() != 1) {
                throw new AppException("Unable to get policy lock information for policy: " + policyNo);
            }

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to retrieve Policy Lock Information.", e);
            l.throwing(getClass().getName(), "loadPolicyLockInfo", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "loadPolicyLockInfo", rs);
        return rs;
    }

    /**
     * Returns true/false depending whether fields can be edited in cancel wip or not.
     * <p/>
     *
     * @param policyHeader record with coverageId and term dates.
     * @return boolean
     */
    public boolean isCancelWipEditable(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "isCancelWipEditable", new Object[]{policyHeader});

        try {
            Integer isEditable;
            Record inputRecord = new Record();
            inputRecord.setFieldValue("polType", policyHeader.getPolicyTypeCode());
            inputRecord.setFieldValue("issCompId", policyHeader.getIssueCompanyEntityId());
            inputRecord.setFieldValue("termEff", policyHeader.getTermEffectiveFromDate());

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Get_CanWip_Rule");
            isEditable = spDao.execute(inputRecord).getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCancelWipEditable", isEditable);
            }
            return (isEditable.intValue() == 1) ? true : false;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to select cancelwip rule for policy type: " +
                policyHeader.getPolicyTypeCode(), e);
            l.throwing(getClass().getName(), "isCancelWipEditable", ae);
            throw ae;
        }
    }

    /**
     * It calls 'Pm_Extend_Term' to extend a term.
     *
     * @param inputRecord input record
     * @return indicate if tail was created
     */
    public boolean extendCancelTerm(Record inputRecord) {
        Boolean isTailCreated = new Boolean(false);
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "extendCancelTerm", new Object[]{inputRecord});
        }
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("newTermExpDate", "extendToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("reasonCode", "reason"));

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Extend_Term", mapping);
        RecordSet rs = null;
        try {
            rs = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing extendCancelTerm", se);
            l.throwing(getClass().getName(), "extendCancelTerm", ae);
            throw ae;
        }
        if (rs != null) {
            isTailCreated = rs.getSummaryRecord().getBooleanValue("tailCreatedB", false);
        }
        l.exiting(getClass().getName(), "extendCancelTerm", isTailCreated);
        return isTailCreated.booleanValue();
    }


    /**
     * get endorsement/renewal quote id
     *
     * @param inputRecord Record (policy type, cycle, issue state, issue company primary key,process location)
     * @return String
     */
    public String getEndorsementQuoteId(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getEndorsementQuoteId", new Object[]{inputRecord});

        String returnValue = null;

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("stateCode", "issueStateCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("issueCompEntId", "issueCompanyEntityId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("processLocCode", "regionalOffice"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId")); 

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Pol_No", mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getStringValue("polNo");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call Pm_Sel_Policy_No.", e);
            l.throwing(getClass().getName(), "getEndorsementQuoteId", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getEndorsementQuoteId", returnValue);

        return returnValue;
    }

    /**
     * Perform save the edorsement/renewal quote
     *
     * @param inputRecord Record containing transactionId, endquoteId
     */
    public void saveAsEndorsementQuote(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveAsEndorsementQuote", new Object[]{inputRecord});

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Issue_Policy.Save_As_Endquote", mapping);
            spDao.executeUpdate(inputRecord);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Issue_Policy.Save_As_Endquote", e);
            l.throwing(getClass().getName(), "saveAsEndorsementQuote", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAsEndorsementQuote");
    }

    /**
     * Method to perform the actual delete for endorsement quote
     *
     * @param inputRecord
     * @return void
     */
    public String deleteEndQuoteTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "deleteEndQuoteTransaction", new Object[]{inputRecord});
        String returnValue = "";

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("newTranId", "transactionLogId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_endquote.InValidate_Endquote", mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue("status");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to Delete EndQuote Transaction.", e);
            l.throwing(getClass().getName(), "deleteEndQuoteTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "deleteEndQuoteTransaction", returnValue);
        return returnValue;
    }


    /**
     * Method to perform the save endorsement quote as official(apply)
     *
     * @param inputRecord
     * @return void
     */
    public void applyEndorsementQuote(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "applyEndorsementQuote", new Object[]{inputRecord});
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_endquote.Apply_Endquote");
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to apply endorsement quote.", e);
            l.throwing(getClass().getName(), "applyEndorsementQuote", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "applyEndorsementQuote");
    }

    /**
     * Method to perform the copy endorsement quote as endorsement
     *
     * @param inputRecord
     */
    public void copyEndorsementQuote(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "copyEndorsementQuote", new Object[]{inputRecord});
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_endquote.Copy_Endquote");
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to copy endorsement quote.", e);
            l.throwing(getClass().getName(), "copyEndorsementQuote", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "copyEndorsementQuote");
    }

    /**
     * Method to evaluate a job category
     *
     * @param inputRecord
     */
    public String getJobCategory(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getJobCategory", new Object[]{inputRecord});
        String returnValue = "";
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_web_transaction.get_job_category");
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to evaluate the job category.", e);
            l.throwing(getClass().getName(), "getJobCategory", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getJobCategory", returnValue);

        return returnValue;

    }

    /**
     * Load all historical administrator by selected policy
     *
     * @param record input record
     * @return recordSet
     */
    public RecordSet loadAllPolicyAdminHistory(Record record) {
       Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicyAdminHistory", new Object[]{record});
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("polId", "policyId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("eqId", "endorsementQuoteId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_POLICY.Sel_Policy_Admin_History",mapping);

            rs = spDao.execute(record);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllPolicyAdminHistory", rs);
            }
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load policy administrator history information", e);
            l.throwing(getClass().getName(), "loadAllPolicyAdminHistory", ae);
            throw ae;
        }

        return rs;
    }

    /**
     * Get linked policy information for amalgamation
     *
     * @param inputRecord
     * @return Record
     */
    public Record getAmalgamationLinkedPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAmalgamationLinkedPolicy", new Object[]{inputRecord});
        }
        Record result;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "transactionLogId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Amalgamation.Get_Linked_Policy", mapping);
        try {
            result = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to get linked policy for amalgamation.", e);
            l.throwing(getClass().getName(), "getAmalgamationLinkedPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAmalgamationLinkedPolicy", result);
        }
        return result;
    }

    /**
     * Add policy diary for amalgamation when delete amalgamation links
     *
     * @param inputRecord
     */
    public void addPmDiary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addPmDiary", new Object[]{inputRecord});
        }
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("acctDt", "transactionAccountingDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDt", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("note", "amalgamationNote"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Diary.Web_Process_Diary", mapping);
        try {
            spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to add PM diary for amalgamation.", e);
            l.throwing(getClass().getName(), "addPmDiary", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addPmDiary");
        }
    }

    /**
     * Check if source policy in WIP status
     *
     * @param inputRecord
     * @return Record
     */
    public Record isSourcePolicyInWip(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSourcePolicyInWip", new Object[]{inputRecord});
        }

        Record result;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Amalgamation.Get_Src_Policy_Wip_B");
        try {
            result = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to get source policy wipB for amalgamation.", e);
            l.throwing(getClass().getName(), "isSourcePolicyInWip", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSourcePolicyInWip", result);
        }
        return result;
    }

    /**
     * Delete WIP transaction by policy no, the DB tier retrieves the in progress transaction id
     * and perform delete wip action. This method is used to perform delete WIP action in extern policy
     * instead of currently opened policy.
     *
     * @param policyNo
     * @return Record
     */
    public Record deleteWipTransaction(String policyNo) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteWipTransaction", new Object[]{policyNo});
        }

        Record result;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Web_Delete_Wip");
        try {
            Record inputRecord = new Record();
            inputRecord.setFieldValue("policyNo", policyNo);
            result = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to delete WIP transaction.", e);
            l.throwing(getClass().getName(), "deleteWipTransaction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteWipTransaction", result);
        }
        return result;
    }

    /**
     * Check if there's entities that is not populated to CIS for quick quote.
     *
     * @param inputRecord
     * @return
     */
    public boolean isCisPopulated(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCisPopulated", new Object[]{inputRecord,});
        }

        boolean isCisPopulated = false;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_quick_quote.is_cis_populated", mapping);

        try {
            RecordSet rec = spDao.execute(inputRecord);
            isCisPopulated = rec.getSummaryRecord().getBooleanValue(spDao.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if all entities are populated in CIS.", e);
            l.throwing(getClass().getName(), "isCisPopulated", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCisPopulated", Boolean.valueOf(isCisPopulated));
        }
        return isCisPopulated;
    }
    
     /**
     * Method to check if transaction snapshot exists
     *
     * @param inputRecord
     * @return String
     */
    public String isTransactionSnapshotExist(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isTransactionSnapshotExist", new Object[]{inputRecord});
        }

        String result;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("effectiveToDate", "termEffectiveToDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Trans_Snapshot.Is_Snapshot_Available", mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            result = outputRecordSet.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to PM_Trans_Snapshot.Is_Snapshot_Available.", e);
            l.throwing(getClass().getName(), "isTransactionSnapshotExist", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isTransactionSnapshotExist", result);
        }
        return result;
    }

    /**
     * Method to check if transaction snapshot is configured
     *
     * @return String
     */
    public String isSnapshotConfigured() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSnapshotConfigured");
        }

        String result;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Environment.Is_Snapshot_Configured");
            RecordSet outputRecordSet = spDao.execute(new Record());
            result = outputRecordSet.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to PM_Environment.Is_Snapshot_Configured.", e);
            l.throwing(getClass().getName(), "isSnapshotConfigured", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSnapshotConfigured", result);
        }
        return result;
    }

    /**
     * Determines whether a Session is alive for a given job.
     *
     * @param inputRecord
     * @return true/false
     */
    public boolean isJobSessionActive(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isJobSessionActive", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();

        mapping.addFieldMapping(new DataRecordFieldMapping("webSessionId", "policyLockId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Lock.Is_Job_Session_Active", mapping);
        boolean isActive;
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            isActive = outputRecordSet.getSummaryRecord().getBooleanValue(spDao.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in call to Pm_Lock.Is_Job_Session_Active.", e);
            l.throwing(getClass().getName(), "isJobSessionActive", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isJobSessionActive", Boolean.toString(isActive));
        }
        
        return isActive;
    }

    /**
     * Load all professional entity transaction.
     *
     * @param inputRecord
     * @param entitlementRLP
     * @return RecordSet
     */
    public RecordSet loadAllProfessionalEntityTransaction(Record inputRecord, RecordLoadProcessor entitlementRLP) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProfessionalEntityTransaction", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Prof_Entity.Sel_Prof_Entity_Trans");
        RecordSet rs = null;
        try {
            rs = sp.execute(inputRecord, entitlementRLP);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllProfessionalEntityTransaction", se);
            l.throwing(getClass().getName(), "loadAllProfessionalEntityTransaction", ae);
            throw ae;
        }
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

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Prof_Entity.Sel_Transaction_Detail");
        RecordSet rs = null;
        try {
            rs = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllProfessionalEntityTransactionDetail", se);
            l.throwing(getClass().getName(), "loadAllProfessionalEntityTransactionDetail", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProfessionalEntityTransactionDetail", rs);
        }
        return rs;
    }

    /**
     * Method to check if entity detail button is available.
     *
     * @param inputRecord
     * @return String
     */
    public String isEntityDetailAvailable(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isEntityDetailAvailable");

        String rtn = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityPolicyId", "policyId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Prof_Entity.Is_Prof_Entity_Risk_Policy", mapping);
        try {
            rtn = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing isEntityDetailAvailable.", e);
            l.throwing(getClass().getName(), "isEntityDetailAvailable", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isEntityDetailAvailable", rtn);
        }
        return rtn;
    }

    /**
     * Method to check if entity detail is configured.
     *
     * @return String
     */
    public String isProfEntityConfigured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isProfEntityConfigured", inputRecord);

        String rtn = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Environment.Is_Prof_Entity_Configured");
        try {
            rtn = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing isProfEntityConfigured.", e);
            l.throwing(getClass().getName(), "isProfEntityConfigured", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isProfEntityConfigured", rtn);
        }
        return rtn;
    }

    /**
     * Method to check if replication is configured.
     *
     * @return String
     */
    public String isReplicationConfigured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isReplicationConfigured", inputRecord);

        String rtn = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Environment.Is_Replication_Configured");
        try {
            rtn = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing isReplicationConfigured.", e);
            l.throwing(getClass().getName(), "isReplicationConfigured", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isReplicationConfigured", rtn);
        }
        return rtn;
    }

    /**
     * Get the policy term information.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return Record loaded with term information.
     */
    public Record getPreviousTermInformation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPreviousTermInformation");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Get_Prev_Term_Info");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get previous term information.", e);
            l.throwing(getClass().getName(), "getPreviousTermInformation", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPreviousTermInformation", returnRecord);
        }
        return returnRecord;
    }

    /**
     * process undo term
     *
     * @param inputRecord input record with the passed request values.
     */
    public void processUndoTerm(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processUndoTerm", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Undo_Term.Undo_Policy");
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to process undo term.", e);
            l.throwing(getClass().getName(), "processUndoTerm", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processUndoTerm");
        }
    }

    /**
     * Check if undo term can be done for the current policy
     *
     * @param inputRecord Record contains input values
     * @return boolean true or false
     */
    public boolean isUndoTermAvailable(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isUndoTermAvailable", new Object[]{inputRecord});

        try {
            boolean result = false;

            // Execute the function
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Undo_B");
            Record output = spDao.executeUpdate(inputRecord);

            // Get special handling value and set for the record to return that matches field_id
            String outString = output.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
            result = YesNoFlag.getInstance(outString).booleanValue();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isUndoTermAvailable", Boolean.valueOf(result));
            }
            return result;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to call Pm_Get_Undo_B", e);
            l.throwing(getClass().getName(), "isUndoTermAvailable", ae);
            throw ae;
        }
    }

     /**
     * Check if there's inactives that is not Associated to a risk for quick quote.
     *
     * @param inputRecord
     * @return
     */
    public boolean isInactiveAssociated(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isInactiveAssociated", new Object[]{inputRecord,});
        }

        boolean isInactiveAssociated = false;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_quick_quote.Is_Associated");

        try {
            RecordSet rec = spDao.execute(inputRecord);
            isInactiveAssociated = rec.getSummaryRecord().getBooleanValue(spDao.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if all inactives are Associated.", e);
            l.throwing(getClass().getName(), "isInactiveAssociated", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isInactiveAssociated", Boolean.valueOf(isInactiveAssociated));
        }
        return isInactiveAssociated;
    }

    /**
     * Converts coverages
     *
     * @param policyHeader                 Current policy header with data populated
     * @parma inputRecord                   a record containing at least accountingDate, endorsementCode, transactionComment
     */
    public void convertCoverage(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convertCoverage", new Object[]{policyHeader, inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffFromDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffToDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("asOfDate", "newTransactionEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("convertType", "newConvertionType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endorsementCode", "newEndorsementCode"));

        String procedureName;
        if (inputRecord.hasField("riskBaseRecordId")) {
            procedureName = "Pm_Cm_Occ_Convert.Process_Risk";
        }
        else {
            procedureName = "Pm_Cm_Occ_Convert.Process_Policy";
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(procedureName, mapping);

        try {
            spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to convert coverages.", e);
            l.throwing(getClass().getName(), "convertCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "convertCoverage");
        }
    }

    /**
     * Check whether tax updates are necessary for a given policy.
     *
     * @parma inputRecord
     * @return recordSet
     */
    public RecordSet checkTaxUpdates(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkTaxUpdates", new Object[]{inputRecord});
        }

        RecordSet outRecordSet = null;
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Tax_Calc.Check_Tax_Updates");
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error executing checkTaxUpdates.", se);
            l.throwing(getClass().getName(), "checkTaxUpdates", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "checkTaxUpdates", outRecordSet);
        }

        return outRecordSet;
    }

    /**
     * Applies tax updates.
     *
     * @parma inputRecord
     */
    public void applyTaxUpdates(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "applyTaxUpdates", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Tax_Calc.Apply_Tax_Updates");
        try {
            sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error executing applyTaxUpdates.", se);
            l.throwing(getClass().getName(), "applyTaxUpdates", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "applyTaxUpdates");
        }

    }

    /**
     * Get warning message.
     *
     * @param inputRecord
     * @return String.
     */
    public String getWarningMessage(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getWarningMessage", new Object[]{inputRecord});

        String returnValue;

        try {
            // Execute the stored procedure
           StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Warning.Get_Message_Str");
           returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue("returnValue");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get warning message.", e);
            l.throwing(getClass().getName(), "getWarningMessage", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getWarningMessage", String.valueOf(returnValue));
        return returnValue;
    }

    /**
     * Initialize warning.
     *
     * @param inputRecord
     * @return String.
     */
    public void initWarning(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "initWarning", new Object[]{inputRecord});

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Warning.Clean_Messages");
            spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to init warning information.", e);
            l.throwing(getClass().getName(), "initWarning", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "initWarning");
    }

    /**
     * Get Reason Code.
     *
     * @parma inputRecord
     */
    public Record getPolicyRelReasonCode(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyRelReasonCode", new Object[]{inputRecord});
        }
        RecordSet rs;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Get_Curr_Reason", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get cancellation reason.", e);
            l.throwing(getClass().getName(), "getPolicyRelReasonCode", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyRelReasonCode", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Save form transaction value
     *
     * @parma inputRecord
     */
    public Record saveFormTransaction(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveFormTransaction", new Object[]{inputRecord});
        }
        RecordSet rs;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_SAVE_FORM_TRAN", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to set clearing reminder.", e);
            l.throwing(getClass().getName(), "saveFormTransaction", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveFormTransaction", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Loads the transaction codes for product notification.
     *
     * @parma inputRecord
     * @return RecordSet
     */
    public RecordSet loadNotifyTransactionCode() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadNotifyTransactionCode");
        }

        RecordSet rs;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Environment.Get_Trans_Code_Notify");
            rs = spDao.execute(new Record());
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load product notify transaction codes.", e);
            l.throwing(getClass().getName(), "loadNotifyTransactionCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadNotifyTransactionCode", rs);
        }

        return rs;
    }
    
    /**
     * Loads the product notification general indicator.
     *
     * @return boolean
     */
    public boolean isProdNotifyConfigured() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isProdNotifyConfigured");
        }

        boolean isConfigured = false;
        
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Environment.Is_Prod_Notify_Configured");
            RecordSet rec = spDao.execute(new Record());
            isConfigured = rec.getSummaryRecord().getBooleanValue(spDao.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load product notify configuration.", e);
            l.throwing(getClass().getName(), "isProdNotifyConfigured", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isProdNotifyConfigured", isConfigured);
        }

        return isConfigured;
    }

    /**
     * Check if NDD validation for expiration date is configured for the current customer
     * <p/>
     *
     * @return String
     */
    public String isSkipNddValidationConfigured() {
        Logger l = LogUtils.enterLog(getClass(), "isSkipNddValidationConfigured");

        String rtn = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Environment.Is_Skip_NDD_Val_Configured");
        try {
            rtn = spDao.execute(new Record()).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing isSkipNddValidationConfigured.", e);
            l.throwing(getClass().getName(), "isSkipNddValidationConfigured", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isSkipNddValidationConfigured", rtn);

        return rtn;
    }

    /**
     * Validation to check the future cancellation in child policies.
     * @param inputRecord
     * @return
     */
    public Record validateRelatedPolicy(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateRelatedPolicy", inputRecord);

        Record record = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Cancel.Val_Related_Policy");
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing validateRelatedPolicy.", e);
            l.throwing(getClass().getName(), "validateRelatedPolicy", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateRelatedPolicy", record);

        return record;
    }

    /**
     * Create relationship between two transactions.
     * @param inputRecord
     * @return
     */
    public void createTransactionXref(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "createTransactionXref", inputRecord);

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Create_Transaction_Xref");
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing createTransactionXref.", e);
            l.throwing(getClass().getName(), "createTransactionXref", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "createTransactionXref");
    }

    /**
     * Load relationship between two transactions.
     * @param inputRecord
     * @return
     */
    public RecordSet loadTransactionXref(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTransactionXref");
        }

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Sel_Transaction_Xref");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load transaction xref.", e);
            l.throwing(getClass().getName(), "loadTransactionXref", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTransactionXref", rs);
        }
        return rs;
    }

    /**
     * Adjust future terms GR indicator.
     * @param inputRecord
     * @return
     */
    public void adjustFutureTermsGr(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "adjustFutureTermsGr", inputRecord);

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(TransactionFields.TRANS_ID, TransactionFields.TRANSACTION_LOG_ID));
        mapping.addFieldMapping(new DataRecordFieldMapping(TransactionFields.TERM_EFF, PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Adjust_Future_Terms_GR", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when adjust future terms GR indicator.", e);
            l.throwing(getClass().getName(), "adjustFutureTermsGr", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "adjustFutureTermsGr");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public TransactionJdbcDAO() {

    }

}

