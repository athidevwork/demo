package dti.pm.transactionmgr.transaction.dao;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyIdentifier;
import dti.pm.transactionmgr.transaction.Transaction;

/**
 * An interface that extends MaintainTransaction interface to handle DAO operation for transaction.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 13, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/08/2008       fcb         isPolicyLocked added.
 * 01/15/2008       fcb         isCancelWipEditable added.
 * 03/13/2008       fcb         getJobCategory added.
 * 03/26/2008       yyh         loadAllPolicyAdminHistory added for issue 78338
 * 06/28/2010       fcb         109187: isJobSessionActive added. 
 * 08/06/2010       syang       103797 - Modified loadAllProfessionalEntityTransaction() to pass RecordLoadProcessor.
 * 09/14/2010       dzhang      103813 - Added method processUndoTerm(), getPreviousTermInformation(), isUndoTermAvailable().
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
 * 09/25/2013       fcb         145725 - Added isSnapshotConfigured, isProfEntityConfigured.
 * 11/21/2013       fcb         148037 - Added loadNotifyTransactionCode, isProdNotifyConfigured, isFeesConfigured, isTaxConfigured
 * 11/18/2014       fcb         157975 - isSkipNddValidationConfigured added
 * 12/19/2014       awu         159339 - Added validateRelatedPolicy.
 * 01/12/2015       awu         160142 - Renamed isFeesConfigured to isChargesNFeesConfigured.
 * 08/28/2016       wdang       167534 - Adde createTransactionXref, loadTransactionXref.
 * 06/28/2018       xnie        187070 - Added adjustFutureTermsGr().
 * ---------------------------------------------------
 */
public interface TransactionDAO {

    /**
     * Method to backup Renewal WIP Transaction
     *
     * @param inputRecord
     * @return
     */
    public Record backupRenewalWipTransaction(Record inputRecord);

    /**
     * Method to delete the billing Relation Before delete WIP Transaction
     *
     * @param inputRecord
     * @return
     */

    public Record deleteBillingRelationForWiPTransaction(Record inputRecord);

    /**
     * Method to perform the actual delete, if failed to delete, a app exception is thrown
     *
     * @param inputRecord
     * @return void
     */
    int deleteWipTransaction(Record inputRecord);

    /**
     * Method to dertermine if the given transaction is a batch RenewalWIP Transaction
     *
     * @param inputRecord
     * @return
     */
    boolean isBatchRenewWip(Record inputRecord);


    Transaction loadLastTransactionInfoForTerm(PolicyHeader policyHeader);

    /**
     * Load all transaction summary by selected policy
     *
     * @param inputRecord input record that contains policy id
     * @return transaction summary
     */
    RecordSet loadAllTransactionSummary(Record inputRecord);

    /**
     * Load transaction information by transaction id
     *
     * @param inputRecord input record that contains transaction pk
     * @return transaction information
     */
    Record loadTransactionById(Record inputRecord);

    /**
     * Create's a database transaction based upon user entered parameters
     *
     * @param policyHeader                 Current policy header with data populated
     * @param transactionEffectiveFromDate User entered transaction effective date
     * @param transactionCode              Code value to drive the transaction type and code created
     * @return Transaction
     * @parma inputRecord                   a record containing at least accountingDate, endorsementCode, transactionComment
     */
    Transaction createTransaction(PolicyHeader policyHeader, Record inputRecord,
                                  String transactionEffectiveFromDate,
                                  TransactionCode transactionCode);

    /**
     * Update an exisitng transaction with a new status
     *
     * @param trans                 Current transaction in progress
     * @param transactionStatusCode New transaction status code
     * @return Transaction
     */
    Transaction updateTransactionStatus(Transaction trans, TransactionStatus transactionStatusCode);

    /*
    * Method to get the max accounting date for a policy term history id
    * that is contained witin the record:
    * get the max accouting date in oasis, if sysdate is greater than max date, return sysdate
    *
    * @param inputRecord record that contains policyTermHistoryId field
    * @return String     date string in mm/dd/yyyy format
    */
    String getMaxAccountingDate(Record inputRecord);

    /*
    * Method to get the latest accouting date in oasis, It does not compare the sysdate
    * @param inputRecord    record that contains accountingDate field
    * @return String        date string in mm/dd/yyyy format
    */
    String getLatestAccountingDate(Record inputRecord);

    /**
     * Determine if an agent exists on the current policy.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    boolean isAgentExist(Record inputRecord);

    /**
     * Determine if an underwriter exists on the current policy/quote.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    boolean isUnderwriterExist(Record inputRecord);

    /**
     * Determine if collateral is required on the current policy.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    boolean isCollateralRequired(Record inputRecord);

    /**
     * Process logic to insert a default agent
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     */
    void insertDefaultAgent(Record inputRecord);

    /**
     * Determine if the selected or defaulted agent is valid for the policy/term.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return String
     */
    String isAgentValid(Record inputRecord);

    /**
     * Determine if the a billing relationship has been setup for the policy.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    boolean isBillingRelationValid(Record inputRecord);

    /**
     * Determine if the a particular save option is available based upon configuration.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    boolean isSaveOptionAvailable(Record inputRecord);

    /**
     * Perform replication or data for the business component's data being saved.
     *
     * @param inputRecord Record containing term, transaction level details
     */
    void doReplication(Record inputRecord);

    /**
     * Perform OAW custom layer invokation for the save as wip transaction
     *
     * @param inputRecord Record containing term, transaction level details
     */
    void doWipCustomLayer(Record inputRecord);

    /**
     * Call stored procedure to renumber, clean-up wip slot issues
     *
     * @param inputRecord Record containing term, transaction level details
     */
    void renumberWipSlots(Record inputRecord);

    /**
     * Perform outputs document processing.
     *
     * @param inputRecord Record containing term, transaction level details
     * @return int integer value indicating success/failure.  Value less than 0 indicates failure.
     */
    int processOutput(Record inputRecord);

    /**
     * check if the renewal reason is configured as an endorsement reason
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    boolean isEndorsementCodeConfiged(Record inputRecord);

    /**
     * Validate transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return String
     */
    String validateTransaction(Record inputRecord);

    /**
     * change term expiration date
     *
     * @param inputRecord input record
     */
    void changeTermExpirationDate(Record inputRecord);

    /**
     * check Policy Holder Status
     *
     * @param inputRecord
     * @return "OFFICIAL"/"TEMP"
     */
    public String checkPolicyHolderStatus(Record inputRecord);

    /**
     * add policy administrator
     *
     * @param inputRecord
     */
    public void addPolicyAdministrator(Record inputRecord);

    /**
     * update policy administrator
     *
     * @param inputRecord
     */
    public void updatePolicyAdministrator(Record inputRecord);

    /**
     * Returns a RecordSet loaded with validation errors
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return RecordSet
     */
    RecordSet loadAllValidationError(Record inputRecord);

    /**
     * Clean up all validation error
     * @param inputRecord
     */
    void deleteAllValidationError(Record inputRecord);

    /**
     * Check if charges fees are configured for the current customer
     * <p/>
     *
     * @return YesNoFlag
     */
    public String isChargesNFeesConfigured();

    /**
     * Check if fee exists related to the current transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return YesNoFlag
     */
    YesNoFlag isFeeDefined(Record inputRecord);

    /**
     * Waive fee related to the current transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     */
    void waiveFee(Record inputRecord);

    /**
     * Check if tax is configured for a state
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return YesNoFlag
     */
    YesNoFlag isPolicyTaxConfigured(Record inputRecord);

    /**
     * Validate premium
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return int
     */
    int validatePremium(Record inputRecord);

    /**
     * Validates if open claims exist for the changed risk/coverage's of the current transaction.
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return YesNoFlag
     */
    YesNoFlag validateOpenClaims(Record inputRecord);

    /**
     * Rate transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return String
     */
    String rateTransaction(Record inputRecord);

    /**
     * Returns a RecordSet loaded with product notifications
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return RecordSet
     */
    RecordSet loadAllProductNotifications(Record inputRecord);

    /**
     * Returns indicator for next step based on the user response
     * <p/>
     *
     * @param inputRecord Record containing current transaction information and product notification response
     * @return int
     */
    int productNotificationResponse(Record inputRecord);

    /**
     * Save the changes of term dates
     *
     * @param inputRecord
     * @return
     */
    int saveTermDates(Record inputRecord);

    /**
     * Calls cancel final logic during save as official to handle any cancellations
     * performed during a renewal wip.
     *
     * @param inputRecord containing current transaction/policy information
     */
    void performRenewalRiskCancelFinal(Record inputRecord);

    /**
     * Calls cancel final logic during save as official to handle any cancellations
     * performed during any type of cancellation transaction.
     *
     * @param inputRecord containing current transaction/policy information
     */
    void performPolicyCancelFinal(Record inputRecord);

    /**
     * Calls reinstate final logic during save as official to handle specialized
     * reinstatement logic
     *
     * @param inputRecord containing current transaction/policy information
     */
    void performReinstateFinal(Record inputRecord);

    /**
     * Calls the main stored procedure to save policy data into Official Mode.
     *
     * @param inputRecord containing current transaction/policy information
     * @return RecordSet containing any related policy save errors
     */
    RecordSet issuePolicy(Record inputRecord);

    /**
     * Determines if a premium discrepancy exists between PM and FM for the current transaction
     *
     * @param inputRecord containing current transaction/policy information
     * @return RecordSet containing any discrepancy records
     */
    RecordSet checkPremiumDelta(Record inputRecord);

    /**
     * Perform billing interface between PM and FM, Pm_Fm_Billing
     *
     * @param inputRecord containing current transaction/policy information
     * @return record contains the return code and error message from the stored procedure
     */
    Record processBilling(Record inputRecord);

    /**
     * Load discrepancy interface status information for the PM/FM Discrepancy page.
     *
     * @param inputRecord record of transaction information being saved
     * @return Record containing discrepancy summary
     */
    String loadDiscrepancyInterfaceStatus(Record inputRecord);

    /**
     * Load discrepancy interface compare information for the PM/FM Discrepancy page.
     *
     * @param inputRecord record of transaction information being saved
     * @return RecordSet containing PM/FM interface comparisson data for the user
     */
    RecordSet loadDiscrepancyCompareInfo(Record inputRecord);

    /**
     * Load discrepancy interface transactional compare information for the PM/FM Discrepancy page.
     *
     * @param inputRecord record of transaction information being saved
     * @return RecordSet containing PM/FM interface transactional comparisson data for the user
     */
    RecordSet loadDiscrepancyTransCompareInfo(Record inputRecord);

    /**
     * Load discrepancy interface information for transactions yet to be processed
     *
     * @param inputRecord record of transaction information being saved
     * @return RecordSet containing PM transactions yet to be processed in FM
     */
    RecordSet loadDiscrepancyIntfcInfo(Record inputRecord);

    /**
     * Validate whether the term is eligible for OOS Endorsement or not by function Pm_Valid_Oos_Term.
     *
     * @param inputRecord
     * @return
     */
    Record vaidateOosEndorseTerm(Record inputRecord);

    /**
     * load all transaction data for view manage transaction
     *
     * @param inputRecord         input record
     * @param recordLoadProcessor the load processor
     * @return the result which met the condition
     */
    public RecordSet loadAllTransaction(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * load all change detail data
     *
     * @param inputRecord input record
     * @return the result which met the condition
     */
    public RecordSet loadAllChangeDetail(Record inputRecord);

    /**
     * load all transaction form data
     *
     * @param inputRecord input record
     * @return the result which met the condition
     */
    public RecordSet loadAllTransactionForm(Record inputRecord);

    /**
     * save/update the data for view and manage transaction data
     *
     * @param recordSets transaction record set
     * @return number of the updated rows
     */
    public int saveTransactionDetail(RecordSet recordSets);

    /**
     * check if the bypass of risk relation configured in pm_attribute
     *
     * @param inputRecord Record containing policy_type
     * @return boolean
     */
    public boolean isBypassRiskRelConfigured(Record inputRecord);

    /**
     * check if the risk child relation configured in pm_attribute
     *
     * @param inputRecord Record containing policy_type
     * @return boolean
     */
    public boolean isRelChildCountConfigured(Record inputRecord);

    /**
     * load related policies' information
     *
     * @param inputRecord contains policy_id, term_eff, term_exp, trans_fk, and time(pre or post), recordLoadProcessor
     * @return related policy resultset
     */
    public RecordSet loadAllRelatedPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * load related policies' information for distinct
     *
     * @param inputRecord contains policy_id, term_eff, term_exp, trans_fk, and time(pre or post), recordLoadProcessor
     * @return related policy resultset
     */
    public RecordSet loadAllDistinctRelatedPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Get related policy display mode per pm attribute
     *
     * @param inputRecord contains policy_type
     * @return pm attribute value indicate which display mode it is
     */
    public String getRelatedPolicyDisplayMode(Record inputRecord);

    /**
     * update transaction comments
     *
     * @param inputRecord input record
     */
    void updateTransactionComments(Record inputRecord);

    /**
     * get the parent-relation count and the child-relation count
     *
     * @param inputRecord
     * @return record(parent_cnt,child_cnt)
     */
    public Record checkRelatedPolicy(Record inputRecord);

    /**
     * To get count of bypass skip rating.
     *
     * @param inputRecord
     * @return number of the bypass skip rating
     */
    int getSkipRatingCount(Record inputRecord);

    /**
     * Method that returns a boolean value that indicates whether the policy is locked
     * <p/>
     *
     * @param policyIdentifier an instance of Policy Identifier with all information loaded.
     * @return boolean true, if the policy is locked; otherwise, false.
     */
    public boolean isPolicyLocked(PolicyIdentifier policyIdentifier);

    /**
     * Returns true/false depending whether fields can be edited in cancel wip or not.
     * <p/>
     *
     * @param policyHeader record with coverageId and term dates.
     * @return boolean
     */
    public boolean isCancelWipEditable(PolicyHeader policyHeader);

    /**
     * It calls 'Pm_Extend_Term' to extend a term.
     *
     * @param inputRecord input record
     * @return indicate if tail was created
     */
    public boolean extendCancelTerm(Record inputRecord);

    /**
     * get endorsement/renewal quote id
     *
     * @param inputRecord Record (policy type, cycle, issue state, issue company primary key,process location)
     * @return String
     */
    public String getEndorsementQuoteId(Record inputRecord);

    /**
     * Perform save the edorsement/renewal quote
     *
     * @param inputRecord Record containing transactionId, endquoteId
     */
    public void saveAsEndorsementQuote(Record inputRecord);


    /**
     * Method to perform the actual delete for endorsement quote
     *
     * @param inputRecord
     * @return void
     */
    String deleteEndQuoteTransaction(Record inputRecord);

    /**
     * Method to perform the save endorsement quote as official(apply)
     *
     * @param inputRecord
     * @return void
     */
    void applyEndorsementQuote(Record inputRecord);

    /**
     * Method to perform the copy endorsement quote as endorsement
     *
     * @param inputRecord
     * @return void
     */
    void copyEndorsementQuote(Record inputRecord);

    /**
     * Method to evaluate a job category
     *
     * @param inputRecord
     * @return String
     */
    String getJobCategory(Record inputRecord);

     /**
     * Load all historical administrator by selected policy
     *
     * @param record              input record
     * @return recordSet
     */
     RecordSet loadAllPolicyAdminHistory(Record record);

    /**
     * Get linked policy information for amalgamation
     *
     * @param inputRecord
     * @return Record
     */
    public Record getAmalgamationLinkedPolicy(Record inputRecord);

    /**
     * Add policy diary for amalgamation when delete amalgamation links
     *
     * @param inputRecord
     */
    public void addPmDiary(Record inputRecord);

    /**
     * Check if source policy in WIP status
     *
     * @param inputRecord
     * @return Record
     */
    public Record isSourcePolicyInWip(Record inputRecord);

   /**
     * Delete WIP transaction by policy no, the DB tier retrieves the in progress transaction id
     * and perform delete wip action. This method is used to perform delete WIP action in extern policy
     * instead of currently opened policy.
     *
     * @param policyNo
     * @return Record
     */
    public Record deleteWipTransaction(String policyNo);

    /**
     * Check if there's entities that is not populated to CIS for quick quote.
     *
     * @param inputRecord
     * @return
     */
    public boolean isCisPopulated(Record inputRecord);

    /**
     * Method to check if transaction snapshot exists
     *
     * @param inputRecord
     * @return String
     */
    public String isTransactionSnapshotExist(Record inputRecord);

    /**
     * Method to check if snapshot is configured
     *
     * @return String
     */
    public String isSnapshotConfigured();

    /**
     * Determines whether a Session is alive for a given job.
     *
     * @param inputRecord
     * @return true/false
     */
    public boolean isJobSessionActive(Record inputRecord);

    /**
     * Load all professional entity transaction.
     *
     * @param inputRecord
     * @param entitlementRLP
     * @return RecordSet
     */
    public RecordSet loadAllProfessionalEntityTransaction(Record inputRecord, RecordLoadProcessor entitlementRLP);

    /**
     * Load all professional entity transaction detail.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProfessionalEntityTransactionDetail(Record inputRecord);

    /**
     * Method to check if entity detail button is available.
     *
     * @param inputRecord
     * @return String
     */
    public String isEntityDetailAvailable(Record inputRecord);

    /**
     * Method to check if professional entity is configured.
     *
     * @param inputRecord
     * @return String
     */
    public String isProfEntityConfigured(Record inputRecord);

    /**
     * Get the policy term information.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return Record loaded with previous term information.
     */
    public Record getPreviousTermInformation(Record inputRecord);

    /**
     * process undo term
     *
     * @param inputRecord input record with the passed request values.
     */
    void processUndoTerm(Record inputRecord);

    /**
     * Check if undo term can be done for the current policy
     *
     * @param inputRecord Record contains input values
     * @return boolean true or false
     */
    public boolean isUndoTermAvailable(Record inputRecord);

     /**
     * Check if there's inactives that is not Associated to a risk for quick quote.
     *
     * @param inputRecord
     * @return
     */
    public boolean isInactiveAssociated(Record inputRecord);

    /**
     * Converts coverages
     *
     * @param policyHeader                 Current policy header with data populated
     * @parma inputRecord                  A record containing at least accountingDate, endorsementCode, transactionComment
     */
    void convertCoverage(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Check whether tax updates are necessary for a given policy.
     *
     * @parma inputRecord
     * @return recordSet
     */
    RecordSet checkTaxUpdates(Record inputRecord);

    /**
     * Applies tax updates.
     *
     * @parma inputRecord
     */
    void applyTaxUpdates(Record inputRecord);

    /**
     * Get warning message.
     *
     * @param inputRecord
     * @return String
     */
    String getWarningMessage(Record inputRecord);

    /**
     * Initialize warning.
     *
     * @param inputRecord
     * @return String
     */
    void initWarning(Record inputRecord);

    /**
     * Get Reason Code.
     *
     * @parma inputRecord
     */
    public Record getPolicyRelReasonCode(Record inputRecord);

    /**
     * Save form transaction value.
     *
     * @parma inputRecord
     */
    public Record saveFormTransaction(Record inputRecord);

    /**
     * Checks if the replication is configured at the system level.
     *
     * @parma inputRecord
     * @return String
     */
    public String isReplicationConfigured(Record inputRecord);

    /**
     * Loads the transaction codes for product notification.
     *
     * @parma inputRecord
     * @return RecordSet
     */
    public RecordSet loadNotifyTransactionCode();

    /**
     * Loads the product notification general indicator.
     *
     * @return boolean
     */
    public boolean isProdNotifyConfigured();

    /**
     * Check if taxes are configured for the current customer
     * <p/>
     *
     * @return String
     */
    public String isTaxConfigured();

    /**
     * Check if NDD validation for expiration date is configured for the current customer
     * <p/>
     *
     * @return String
     */
    public String isSkipNddValidationConfigured();

    /**
     * Validation to check the future cancellation in child policies.
     * @param inputRecord
     * @return
     */
    public Record validateRelatedPolicy(Record inputRecord);

    /**
     * Create relationship between two transactions.
     * @param inputRecord
     * @return
     */
    public void createTransactionXref(Record inputRecord);

    /**
     * Load relationship between two transactions.
     * @param inputRecord
     * @return
     */
    public RecordSet loadTransactionXref(Record inputRecord);

    /**
     * Adjust future terms GR indicator.
     * @param inputRecord
     * @return
     */
    public void adjustFutureTermsGr(Record inputRecord);
}
