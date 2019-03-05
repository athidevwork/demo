package dti.pm.transactionmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.busobjs.TransactionCode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.transaction.Transaction;

import java.util.Map;

/**
 * An interface that extends MaintainTransaction interface to handle implementation of transaction manager.
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
 * 91/08/2007       fcb         validatePolicyPicture added.
 * 03/26/2008       yyh         loadAllPolicyAdminHistory added for issue 78338
 * 08/06/2010       syang       103797 - Modified loadAllProfessionalEntityTransaction() to pass RecordLoadProcessor.
 * 09/10/2010       bhong       110269 - Added lockPolicy and processApplyEndorsementQuote
 * 09/14/2010       dzhang      103813 - Added method processUndoTerm(), getPreviousTermInformation(), isUndoTermAvailable().
 * 04/07/2011       ryzhao      103801 - Added method getRelatedPolicyDisplayMode().
 * 05/01/2011       fcb         105791 - Added convertCoverageTransaction().
 * 08/01/2011       ryzhao      118806 - Added loadTransactionById().
 * 06/26/2012       fcb         129528 - Added owsHandleNBPolicyError.
 * 09/06/2011       fcb         137198 - removed owsHandleNBPolicyError.
 * 12/26/2012       awu         140186 - Added method checkClearingReminder(), processSaveCheckClearingReminder();
 * 12/27/2012       tcg         139862 - Added loadWarningMessage(),initWarning() and addWarningMessage() to pop up warning message.
 * 03/13/2013       awu         141924 - 1. Added addDefaultAgent.
 *                                       2. Added processSaveTransactionOfficialForWS
 * 02/19/2013       jshen       141982 - Renamed method loadTransactionByTerm to loadAllTransaction.
 * 10/16/2013       fcb         148904 - Added isAgentExist, isAgentValid, isCheckAgentConfigured, isBillingSetupAvailable
 * 10/16/2013       fcb         145725 - Added isSnapshotConfigured.
 * 11/20/2013       fcb         148037 - Added isNotifyConfigured, isFeesConfigured, isTaxConfigured
 * 03/11/2014       fcb         152685 - transaction log id passed to loadWarningMessage
 * 01/12/2015       awu         160142 - Renamed isFeesConfigured to isChargesNFeesConfigured.
 * 06/15/2016       lzhang      170647 - Added productNotificationResponse.
 * 08/26/2016       wdang       167534 - Added isAutoPendingRenewalEnable, hasTransactionXref, createTransactionXref, performDeleteRenewalWIP.
 * ---------------------------------------------------
 */
public interface TransactionManager {

    public static final String BEAN_NAME = "TransactionManager";

    /**
     * Create's a database transaction based upon user entered parameters
     *
     * @param policyHeader                 Current policy header with data populated
     * @param transactionEffectiveFromDate User entered transaction effective date
     * @param transactionCode              Code value to drive the transaction type and code created
     * @return Transaction
     */
    Transaction createTransaction(PolicyHeader policyHeader, Record inputRecord, String transactionEffectiveFromDate, TransactionCode transactionCode);

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
     * @param lockPolicyBeforeSave                   to lock policy before the save?
     * @return Transaction
     */
    Transaction createTransaction(PolicyHeader policyHeader, Record inputRecord, String transactionEffectiveFromDate, TransactionCode transactionCode, boolean lockPolicyBeforeSave);

    /**
     * Update an exisitng transaction with a new status
     *
     * @param trans                 Current transaction in progress
     * @param transactionStatusCode New transaction status code
     * @return Transaction
     */
    Transaction updateTransactionStatusWithLock(Transaction trans, TransactionStatus transactionStatusCode);
    /**
     * Update an exisitng transaction with a new status
     *
     * @param trans                 Current transaction in progress
     * @param transactionStatusCode New transaction status code
     * @return Transaction
     */
    Transaction UpdateTransactionStatusNoLock(Transaction trans, TransactionStatus transactionStatusCode);

    /**
     * This method load information about the last transaction performed on a term.
     *
     * @param policyHeader
     * @return Transaction
     */
    Transaction loadLastTransactionInfoForTerm(PolicyHeader policyHeader);

    /**
     * Load all transaction summary by selected policy
     * @param inputRecord input record that contains policy id
     * @return transaction summary
     */
    RecordSet loadAllTransactionSummary(Record inputRecord);

    /**
     * This method to get initial values for capture Transaction Details page
     *
     * @param policyHeader
     * @param inputRecord  record containing minumum information for getting initial values
     * @return record containing the initial values for capture Transaction Details
     */
    Record getInitialValuesForCaptureTransactionDetails(PolicyHeader policyHeader, Record inputRecord);

    /**
     * This method to get initial values for create a endorsement Transaction page
     *
     * @param policyHeader
     * @param inputRecord  record containing minumum information for getting initial values
     * @return record containing the initial values for create endorsement Transaction
     */
    Record getInitialValuesForCreateEndorsementTransaction(PolicyHeader policyHeader, Record inputRecord);

    /**
     * load default date for change term expiration
     * @param policyHeader policy header
     * @return default date
     */
    Record getInitialValuesForChangeTermExpirationDate(PolicyHeader policyHeader);

    /**
     * load default policy administrator
     * @param policyHeader policy header
     * @return default policy administrator
     */
    Record getInitialValuesForChangePolicyAdministrator(PolicyHeader policyHeader);

    /**
     * validate input for change policy administrator
     * @param policyHeader policy header
     * @param inputRecord input record
     */
    void validateForChangePolicyAdministrator(PolicyHeader policyHeader,Record inputRecord);

    /**
     * validate input for change term expiration date
     * @param policyHeader policy header
     * @param inputRecord input record
     */
    void validateForChangeTermExpirationDate(PolicyHeader policyHeader,Record inputRecord);
    /**
     * This method to get initial values for capture Transaction Details page
     *
     * @param inputRecord record entered by user for validation
     * @return String that contains SUCCESSFUL or FAILED|{enteredAccountingDateString} for AJAX to parse
     */
    void validateTransactionDetails(Record inputRecord);

    /**
     * This method returns true or false for pageEntitlement to hide/show endorsement code
     *
     * @param inputRecord record containging transactionCode field used to determine if Endorsement code should be visible.
     * @return boolean: true if  EndorsementCode should be visible to user
     */
    boolean isEndorsementCodeVisible(Record inputRecord);

    /**
     * Determine the save options available for the current transaction
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return Map
     */
    Map loadSaveOptions(Record inputRecord);

    /**
     * change policy administrator
     * @param policyHeader policy header
     * @param inputRecord input record
     */
    void savePolicyAdministrator(PolicyHeader policyHeader,Record inputRecord);
    /**
     * change term expiration
     * @param policyHeader policy header
     * @param inputRecord record from action
     */
    void changeTermExpirationDate(PolicyHeader policyHeader,Record inputRecord);
    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the common save functionality for all types of save actions.
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @param inputRecord Record containing policy header summary information about the
     */
    void processSaveTransaction(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the save occupant cancellation transaciton as official processing
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @param inputRecord  Record containing policy header summary information about the
     */
    public void processSaveOccupantCancellationAsOfficial(PolicyHeader policyHeader, Record inputRecord);
    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the save VL Employee cancellation transaciton as official processing
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @param inputRecord  Record containing policy header summary information about the
     */
    public void processSaveVLEmployeeCancellationAsOfficial(PolicyHeader policyHeader, Record inputRecord);
    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the save as official processing
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @param inputRecord Record containing policy header summary information about the
     */
    void processSaveTransactionAsOfficial(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the save as endorsement quote processing
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @return endorsementQuoteId
     */
    String processSaveTransactionAsEndorsementQuote(PolicyHeader policyHeader);

    /**
     * Non DB transaction wrapped method to invoke the sequential steps for
     * the save tail transaciton as official processing
     *
     * @param policyHeader Instance of the PolicyHeader object
     * @param inputRecord Record containing policy header summary information about the
     */
    void processSaveTailTransactionAsOfficial(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Method to delete Wip Transation
     *
     * @param policyHeader      the policyHeader of the given policy, whose wip transaction is to be processed
     * @param inputRecord Record containing policy header summary information about the
     * @return
     */
    public Record deleteWipTransaction(PolicyHeader policyHeader, Record inputRecord);

    /**
     * check if the renewal reason is configured as an endorsement reason
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    boolean isEndorsementCodeConfigured(Record inputRecord);

    /**
     * Validate transaction
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return String     validation result
     */
    String performTransactionValidation(Record inputRecord);

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
     * Check if taxes are configured for the current customer.
     * <p/>
     *
     * @return boolean
     */
    public boolean isTaxConfigured();

    /**
     * Check if charges fees are configured for the current customer.
     * <p/>
     *
     * @return boolean
     */
    public boolean isChargesNFeesConfigured();

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
    void performFeeWaive(Record inputRecord);

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
    int performPremiumValidation(Record inputRecord);

    /**
     * Rate transaction
     * The code that calls the this method should check isRatingLongRunning() first,
     * and initialize the appropriate workflow if it is.
     * Otherwise, if isRatingLongRunning() returns false, it can simply call this method to rate transaction.
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     * @return String
     */
    String performTransactionRating(Record inputRecord);

    /**
     * return a booelan value to indicates if the rating process is a long running process
     * @return boolean
     */
    boolean isRatingLongRunning();

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
     * @return String
     */
    String performProductNotificationResponse(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get initial values for change term effective date and term expiration date
     *
     * @param policyHeader
     * @return
     */
    Record getInitialValueForChangeTermDates(PolicyHeader policyHeader);

    /**
     * Save the changes of term dates
     *
     * @param policyHeader
     * @param inputRecord
     */
    void saveTermDates(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Check if the open claims validation is configured for save official
     * <p/>
     *
     * @return YesNoFlag
     */
    public YesNoFlag isOpenClaimsValidationConfigured();

    /**
     * Validates if open claims exist for the changed risk/coverage's of the current transaction.
     * <p/>
     *
     * @param inputRecord Record containing current transaction information
     */
    void validateOpenClaims(Record inputRecord);

    /**
     * Get OOSE Expiration Date
     *
     * @param policyHeader
     * @return
     */
    String getOoseExpirationDate(PolicyHeader policyHeader);

    /**
     * Load discrepancy summary information for the PM/FM Discrepancy page.
     *
     * @param inputRecord  record of transaction information being saved
     * @return Record containing discrepancy summary information
     */
    Record loadDiscrepancySummaryInfo(Record inputRecord);

    /**
     * Load discrepancy comparisson information for the PM/FM Discrepancy page.
     *
     * @param inputRecord  record of transaction information being saved
     * @return RecordSet containing discrepancy compare information
     */
    RecordSet loadDiscrepancyCompareInfo(Record inputRecord);

    /**
     * Load discrepancy transaction information for the PM/FM Discrepancy page.
     *
     * @param inputRecord  record of transaction information being saved
     * @return RecordSet containing discrepancy transaction information
     */
    RecordSet loadDiscrepancyTransCompareInfo(Record inputRecord);

    /**
     * Load discrepancy interface information for the PM/FM Discrepancy page.
     *
     * @param inputRecord  record of transaction information being saved
     * @return RecordSet containing discrepancy interface information
     */
    RecordSet loadDiscrepancyIntfcInfo(Record inputRecord);

    /**
     * Wrapper to load related policy errors from the save official process.
     * Current implementation obtains this information from the RequestStorageManager;
     *
     * @param inputRecord  record of transaction information being saved
     * @return RecordSet containing related policy error information
     */
    RecordSet loadRelatedPolicySaveError(Record inputRecord);
    /**
     * save all the transaction data
     * <p/>
     * @param records transaction data that needed to save
     */
    public void saveTransactionDetail(RecordSet records);

    /**
     * load all transaction data for policy
     * <p/>
     * @param policyHeader policy header
     * @return the result met the condition
     */
    public RecordSet loadAllTransaction(PolicyHeader policyHeader);
    /**
     * load all transaction data for policy or load transaction data for a particular term
     * <p/>
     * @param policyHeader input record
     * @param inputRecord
     * @return the result met the condition
     */
    public RecordSet loadAllTransaction(PolicyHeader policyHeader, Record inputRecord);
    /**
     * load transaction data by transaction id
     * <p/>
     * @param inputRecord
     * @return transaction information
     */
    public Record loadTransactionById(Record inputRecord);
    /**
     * load all the transaction change detail data
     * <p/>
     * @param inputRecord input record
     * @return the result met the condition
     */
    public RecordSet loadAllChangeDetail(Record inputRecord);
    /**
     * load all the transaction form data
     * <p/>
     * @param inputRecord input record
     * @return the result met the condition
     */
    public RecordSet loadAllTransactionForm(Record inputRecord);
    /**
     * Check if OOS Endorsement is avaliable or not.
     *
     * @param inputRecord
     * @return
     */
    boolean isOosEndorsementAvailable(Record inputRecord);

    /**
     * check if the billing relation is valid
     * @param inputRecord
     * @return a boolean value to indicate if the billing relation is valid
     */
    boolean isBillingRelationValid(Record inputRecord);

    /**
     * load all related policy info
     *
     * @param policyHeader
     * @param time(preorpost)
     * @return recordSet
     */
    public RecordSet loadAllRelatedPolicy(PolicyHeader policyHeader, String time);

    /**
     * check related policies exist or not
     *
     * @param policyHeader
     * @param time         (pre or post)
     * @return boolean
     */
    public boolean checkRelatedPolicy(PolicyHeader policyHeader, String time);


    /**
     * get lock count of related policies
     *
     * @param policyHeader
     * @param time
     * @return
     */
    public long getLockedRelatedPolicyCount(PolicyHeader policyHeader, String time);

    /**
     * validate locked related policies
     * @param policyHeader
     * @return
     */
    public String validateLockedRelatedPolicy(PolicyHeader policyHeader);

    /**
     * perform OS integration with PM
     *
     * @param inputRecord             a record containing transactional information
     * @param updateTransactionStatus boolean indicating if the status of the transaction should be udpated to OUTPUT
     */
    void processOutput(Record inputRecord, boolean updateTransactionStatus);

    /**
     * validate the current transaction
     * @param policyHeader
     * @param requestId it can be action class name or request URI
     */
    public void validateTransaction(PolicyHeader policyHeader, String requestId);

    /**
     * Set reload code for given transaction code in PolicyHeader.
     *
     * @param policyHeader    policy header
     * @param transactionCode transaction code
     */
    void setPolicyHeaderReloadCode(PolicyHeader policyHeader, TransactionCode transactionCode);

    /**
     * To check if need to skip rating.
     *
     * @param inputRecord
     * @return
     */
    YesNoFlag isSkipRating(Record inputRecord);

    /**
     * validate policy picture
     *
     * @param policyHeader policy header
     */
    public void validatePolicyPicture(PolicyHeader policyHeader, Record inputRecord);

    /**
     * load expiration date for the policy.
     * @param policyHeader
     * @param inputRecord
     * @return initialValue
     */
    public Record getInitialValuesForExtendCancelTerm(PolicyHeader policyHeader,Record inputRecord);

    /**
     *   It does following validations:
     1.	Extension date is greater than the current term expiration date.
     2.	Accounting date is valid.
     * @param policyHeader policy header
     * @param inputRecord input record
     */
    public void validateForExtendCancelTerm(PolicyHeader policyHeader,Record inputRecord);

    /**
     * it utilizes DAO's 'performExtendCancelTerm' and TransactionManager's createTransaction,PolicyManager's
     * lockPolicy/unlockPolicy to process extension.
     * @param policyHeader policy header
     * @param inputRecord input record
     */
    public void performExtendCancelTerm(PolicyHeader policyHeader,Record inputRecord);

    /**
     * re-rate policy
     * @param policyHeader policy header
     * @param inputRecord input record
     */
    public void performReRatePolicy(PolicyHeader policyHeader,Record inputRecord);

    /**
     * Method to delete endquote Transation
     *
     * @param policyHeader      the policyHeader of the given policy
     * @return
     */
    public void performDeleteEndQuoteTransaction(PolicyHeader policyHeader);

    /**
     * Method to apply endquote
     *
     * @param policyHeader      the policyHeader of the given policy
     * @return
     */
    public void applyEndorsementQuote(PolicyHeader policyHeader);

    /**
     * Method to copy endquote
     *
     * @param policyHeader      the policyHeader of the given policy
     * @param inputRecord       params from request
     * @return
     */
    public void copyEndorsementQuote(PolicyHeader policyHeader,Record inputRecord);

    /**
     * Lock policy
     *
     * @param policyHeader
     */
    public void lockPolicy(PolicyHeader policyHeader);

    /**
     * Apply endorsement quote
     *
     * @param policyHeader
     */
    public void processApplyEndorsementQuote(PolicyHeader policyHeader);

    /**
     * Load all historical administrator by selected policy
     *
     * @param policyHeader      the policyHeader of policy
     * @return
     */
    RecordSet loadAllPolicyAdminHistory(PolicyHeader policyHeader);

    /**
     * Check if source policy in WIP status
     *
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
    public Record delWipTransaction(String policyNo);

    /**
     * Get linked amalgamation policy
     *
     * @param inputRecord
     * @return
     */
    public Record getAmalgamationLinkedPolicy(Record inputRecord);

    /*
    * Method to get the max accounting date for a policy term history id
    * that is contained witin the record:
    * get the max accouting date in oasis, if sysdate is greater than max date, return sysdate
    *
    * @param inputRecord record that contains policyTermHistoryId field
    * @return String     date string in mm/dd/yyyy format
    */
    public String getMaxAccountingDate(Record inputRecord);

    /**
     * Method to check if transaction snapshot exists
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean isTransactionSnapshotExist(Record inputRecord);

    /**
     * Method to check if transaction snapshot is configured
     *
     * @return boolean
     */
    public boolean isSnapshotConfigured();

    /**
     * Get defalut values for professional entity search criteria.
     *
     * @return Record
     */
    public Record getDefaultValuesForProfessionalEntitySearchCriteria();

    /**
     * Load all professional entity transaction.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProfessionalEntityTransaction(Record inputRecord);

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
     * @param policyHeader
     * @return String
     */
    public String isEntityDetailAvailable(PolicyHeader policyHeader);

    /**
     * Get the policy term information.
     * <p/>
     *
     * @param policyHeader policy header with policy information.
     * @return Record loaded with previous term information.
     */
    public Record getPreviousTermInformation(PolicyHeader policyHeader);

    /**
     * Process undo term
     *
     * @param policyHeader policy header with policy information
     * @param inputRecord  input record with the passed request values.
     */
    public void processUndoTerm(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Check if undo term can be done for the current policy
     *
     * @param policyHeader policy header contains policy information
     * @return boolean true or false
     */
    public boolean isUndoTermAvailable(PolicyHeader policyHeader);

    /**
     * Get related policy display mode from pm_attribute table.
     *
     * @param inputRecord with policy type code.
     * @return String value indicate which mode to display with.
     */
    public String getRelatedPolicyDisplayMode(Record inputRecord);

    /**
     * Get related policy display mode from pm_attribute table.
     *
     * @param policyHeader with policy header information.
     * @param inputRecord with policy type code.
     * @return Transaction.
     */
    public Transaction convertCoverageTransaction(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Wrapper to load warning message from the save process.
     * Current implementation obtains warning message from the db;
     *
     * @param transactionLogId with transaction id.
     * @return String containing warning information
     */
    public String loadWarningMessage(String transactionLogId);

    /**
     * Wrapper to add warning message.
     * @param transactionLogId with transaction id.
     */
    public void addWarningMessage(String transactionLogId);

    /**
     * Initialize warning.
     * @param transactionLogId with transaction id
     */
    public void initWarningMessage(String transactionLogId);

    /**
     * To check to invoke the check clearing reminder or not.
     *
     * @param policyHeader with policy header information.
     * @return boolean true or false.
     */
    public boolean checkClearingReminder(PolicyHeader policyHeader);

    /**
     * Set the check clearing reminder
     *
     * @param policyHeader with policy header information.
     * @param inputRecord with clearing reminder value
     * @return boolean.
     */
    public void processSaveCheckClearingReminder(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Add the default agent.
     * @param inputRecord
     */
    public void addDefaultAgent(Record inputRecord);

    /**
     * This method is called by policy change service to save policy as official
     * @param policyHeader
     * @param inputRecord
     */
    public void processSaveTransactionOfficialForWS(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Determine if an agent exists on the current policy.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isAgentExist(Record inputRecord);

    /**
     * Determine if an agent is valid on the current policy.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public String isAgentValid(Record inputRecord);

    /**
     * Determine if an agent is configured to be validated.
     *
     * @param inputRecord Record containing current policy, term, and transaction information
     * @return boolean
     */
    public boolean isCheckAgentConfigured(Record inputRecord);

    /**
     * Check if Billing Setup is available or not.
     *
     * @param policyHeader
     * @return
     */
    public boolean isBillingSetupAvailable(PolicyHeader policyHeader);

    /**
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean isNotifyConfigured (PolicyHeader policyHeader);

    /**
     *
     * @param inputRecord
     * @return int
     */
    public int productNotificationResponse(Record inputRecord);

    /**
     * Indicate if Endorsement/OOSE/Cancel Risk/Coverage is available on prior term.
     * @param policyHeader
     * @return
     */
    public boolean isAutoPendingRenewalEnable (PolicyHeader policyHeader);

    /**
     * check if there's relationship between two transactions.
     * @param inputRecord
     * @return
     */
    public boolean hasTransactionXref(Record inputRecord);

    /**
     * Create Transaction Xref
     * @param inputRecord
     */
    public void createTransactionXref(Record inputRecord);
    /**
     * Auto delete renewal WIP.
     * @param policyHeader
     */
    public Record performDeleteRenewalWIP (PolicyHeader policyHeader);
}


