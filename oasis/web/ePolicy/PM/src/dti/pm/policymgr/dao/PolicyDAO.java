package dti.pm.policymgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyIdentifier;

/**
 * An interface that extends the MaintainPolicy interface to provide DAO operation.
 * </p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 13, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/10/2007       sxm         Moved getDefaultState() to PMDefaultDAO
 * 12/10/2007       zlzhu       Add new methods
 * 04/17/2008       fcb         loadAllQuoteRiskCovg and getCopyQuoteErrorTrans added.
 * 04/30/2008       yyh         ProcessQuoteStatus:loadAllQuoteStatus and saveQuoteStatus added.
 * 07/25/2008       yyh         Add getPolicyId.
 * 06/11/2009       Joe         Remove the method deriveImageRightMapping() which has been refactored into Common Service.
 * 09/06/2011       dzhang      Rename getEntityRoleIdForPolicyholder to getEntityRoleIdForEntity.
 * 11/21/2011       sxm         issue 126493 - added changePolicy().
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 09/06/2011       fcb         137198 - Added loadPolicyTermList.
 * 10/08/2012       xnie        133766 - Added reRateOnDemand(), reRateBatch(), loadAllReRateResult() , and
 *                                       loadAllReRateResultDetail().
 * 12/12/2012       xnie        139838 - Modified reRateBatch() to change return value from void to record.
 * 08/25/2015       awu         164026 - Added loadPolicyDetailForWS.
 * 01/15/2016       tzeng       166924 - Added isPolicyRetroDateEditable.
 * 01/25/2016       eyin        168882 - Added loadPolicyBillingAccountInfoForWS.
 * 03/08/2016       wdang       168418 - Move updateEntityRoleAddress and saveEntityRoleAddress to EntityDAO.
 * 06/17/2016       eyin        177211 - Added generatePolicyNumberForWS();
 * 12/20/2016       tzeng       166929 - Added loadSoftValidationB(), getLatestTerm(), isNewBusinessTerm().
 * 02/12/2018       lzhang      190834 - Added validatePolicyNosExist and validateTermBaseRecordIdsExist.
 * 04/12/2018       lzhang      191379 - Added loadPolicyHeaderForWS
 * 11/02/2018       wrong       196790 - Added getEntityIdByClientId.
 * ---------------------------------------------------
 */
public interface PolicyDAO {

    /**
     * Method that returns an instance of a recordset object with policy lock information for the provided
     * policy number.
     * <p/>
     *
     * @param inputRecord         an input record represents the search criteria
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet an instance of the recordset result object that contains policy lock information.
     */
    RecordSet findAllPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Load policy summary for one client
     *
     * @param inputRecord input record that contains entity id.
     * @return policy summary
     */
    public RecordSet loadAllPolicySummary(Record inputRecord);

    /**
     * Load related Endorsment/Renewal Quote of policy
     *
     * @param inputRecord input record that contains termBaseRecordId.
     * @return quote list
     */
    public RecordSet loadAllEndorsementQuote(Record inputRecord);

    /**
     * Method that returns an instance of a recordset object with policy lock information for the provided
     * policy number.
     * <p/>
     *
     * @param policyNo policy number
     * @return RecordSet an instance of the recordset result object that contains policy lock information.
     */
    RecordSet loadPolicyLockInfo(String policyNo);

    /**
     * Method that returns an instance of Policy Header with all its class member information loaded up for the provided
     * policy number and policy term id.
     * <p/>
     * Most recent term is considered as the default term, if the provided policy term history id is null.
     * <p/>
     *
     * @param policyNo              policy number
     * @param termBaseRecordId      policy term history base record ID
     * @param policyTermHistoryId   policy term history id.
     * @param desiredPolicyViewMode desired view mode of WIP or OFFICIAL
     * @param endQuoteId            endorsement quote Id
     * @param webSessionId          optional instance of the unique web session id for locking when the desired view mode is WIP
     * @param policyLockDuration    duration of the lock
     * @return PolicyHeader An instance of Policy Header object.
     */
    PolicyHeader loadPolicyHeader(String policyNo, String termBaseRecordId, String policyTermHistoryId,
                                  PolicyViewMode desiredPolicyViewMode, String endQuoteId, String webSessionId, String policyLockDuration);

    /**
     * Method that returns a boolean value that indicates whether the policy picture has been changed, ever since it has
     * been loaded initially.
     * <p/>
     *
     * @param policyIdentifier an instance of Policy Identifier with all information loaded.
     * @return boolean true, if the policy picture has been changed; otherwise, false.
     */
    boolean isPolicyPictureChanged(PolicyIdentifier policyIdentifier);

    /**
     * Method that returns an instance of a recordset object with policy information for the provided
     * policyHeader.
     * <p/>
     *
     * @param policyHeader        an instance of the Policy Header object
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet an instance of the recordset result object that contains policy information.
     */
    RecordSet loadAllPolicy(PolicyHeader policyHeader, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method that returns an instance of a recordset object with policy detail information for the provided
     * policyHeader.
     * <p/>
     *
     * @param inputRecord an input record that contains all member variables for the PolicyHeader
     * @return RecordSet an instance of the recordset result object that contains policy detail information.
     */
    Record loadAddlInfo(Record inputRecord);

    /**
     * Save the given input records with the Pm_Nb_End.Save_Policy stored procedure,
     * This assumes all input records have recordModeCode = TEMP.
     * Set the rowStatus field to MODIFIED for records that have already been saved in this WIP transaction,
     * and are just being updated not.
     *
     * @param inputRecord a Record, each with the PolicyHeader, PolicyIdentifier,
     *                    and Policy Detail info matching the fields returned from the loadAllPolicy method..
     * @return the number of rows updated.
     */
    int addPolicy(Record inputRecord);

    /**
     * Update the given input record with the Pm_Update.Change_Policy stored procedure,
     * assuming they all have field recordModeCode = Official.
     *
     * @param inputRecord a Records with the PolicyHeader, PolicyIdentifier,
     *                    and Policy Detail info matching the fields returned from the loadAllPolicy method..
     * @return the number of rows updated.
     */
    int changePolicy(Record inputRecord);

    /**
     * Update the given input record with the Pm_Endorse.Change_Policy stored procedure,
     * assuming they all have field recordModeCode = Official, and were marked as updated.
     *
     * @param inputRecord a Records with the PolicyHeader, PolicyIdentifier,
     *                    and Policy Detail info matching the fields returned from the loadAllPolicy method..
     * @return the number of rows updated.
     */
    int updatePolicy(Record inputRecord);

    /**
     * Validates the modified policy no with a stored procedure determining if the policy no
     * is already in use, if a billing acount has been setup for the policy, or if an account
     * uses the same number.  If any is met, the modified policy no is invalid (FALSE).
     *
     * @param modifiedPolicyNo user modified policy no value.
     * @param policyId         policy primary key value of the policy no being changed.
     * @return String containing any validation messages.
     */
    String validateModifiedPolicyNo(String modifiedPolicyNo, String policyId);

    /**
     * Get default reginal office code based on given state code
     *
     * @param inputRecord Record contains input values
     * @return String containing the default regional office code
     */
    public String getDefaultRegionalOffice(Record inputRecord);

    /**
     * Get default term expiration date
     *
     * @param inputRecord Record contains input values
     * @return String contains the override indicator concatenated with the default term expiration date
     */
    public String getDefaultTermExpirationDate(Record inputRecord);

    /**
     * Find all policy types based on search criteria
     *
     * @param inputRecord Record contains input values
     * @return RecordSet contains all policy types
     */
    public RecordSet findAllPolicyType(Record inputRecord);

    /**
     * Check policy existence
     *
     * @param inputRecord Record contains input values
     * @return String indicates if policy with the same policy typeexists
     */
    public String checkPolicyExistence(Record inputRecord);

    /**
     * Create a new policy
     *
     * @param inputRecord Record contains all values for creating policy
     * @return Record contains return code and policy no etc
     */
    public Record createPolicy(Record inputRecord);

    /**
     * Generate Policy Number
     *
     * @param inputRecord Record contains all values for policy number generation
     * @return Record contains return code and policy no etc
     */
    public Record generatePolicyNumberForWS(Record inputRecord);

    /**
     * deny quote
     *
     * @param inputRecord
     * @return record include reuslt
     */
    public Record denyQuote(Record inputRecord);

    /**
     * Reactive quote
     *
     * @param inputRecord
     * @return record include reuslt
     */
    public Record reactiveQuote(Record inputRecord);

    /**
     * create next cycle for policy and quote
     *
     * @param inputRecord
     * @return record include reuslt which contains the new policy/quote #
     */
    public Record createNextCycle(Record inputRecord);

    /**
     * create quote no.
     *
     * @param inputRecord
     * @return new quote no
     */
    public String getNewQuoteNo(Record inputRecord);

    /**
     * create parallel policy no.
     *
     * @param inputRecord
     * @return new policy no
     */
    public Record getParallelPolicyNo(Record inputRecord);

    /**
     * create parallel quote no.
     *
     * @param inputRecord
     * @return record include new quote no
     */
    public Record getParallelQuoteNo(Record inputRecord);

    /**
     * Determines if the coverage class item should be enabled
     *
     * @param inputRecord Record contains policy id, risk id and evaluation date
     * @return boolean indicating yes/no to enable the coverage class option
     */
    boolean isCoverageClassAvailable(Record inputRecord);   

    /**
     * Determines if rolling IBNR date can be changed
     *
     * @param inputRecord Record contains policy id, risk id and evaluation date
     * @return boolean indicating yes/no to allow the rolling IBNR change
     */

    public boolean canRollingIbnrDateChange(Record inputRecord);

    /**
     * Remove policy term history records related to an OOS change.
     *
     * @param inputRecord a Records with the PolicyHeader, PolicyIdentifier,
     *                    and Policy Detail info matching the fields returned from the loadAllPolicy method.
     */
    void deleteOosPolicyDetail(Record inputRecord);

    /**
     * Validate that the user has entered a valid OOSE term expiration date
     *
     * @param inputRecord a Records with the PolicyHeader, PolicyIdentifier,
     *                    and Policy Detail info matching the fields returned from the loadAllPolicy method.
     * @return valid boolean indicator if the term expiration date is valid
     */
    boolean validateOoseTermExpDate(Record inputRecord);

    /**
     * To check if policy notes exist
     *
     * @param inputRecord a Record with query conditions
     * @return Y/N
     */
    String isRecordExist(Record inputRecord);

    /**
     * Method to get policy key info
     *
     * @param inputRecord record contains policy no and eff date
     * @return Record with quote data
     */
    Record getPolicyKeyInfo(Record inputRecord);

    /**
     * Method to load selected addess and all available address for the policyholder or COI Holder
     *
     * @param inputRecord a record with query information
     * @param recordLoadProcessor an instance of data load processor
     * @return a RecordSet with selected address and all available address records
     */
    RecordSet loadAllAddress(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method to get entity role Id for entity
     *
     * @param inputRecord a Record with query information
     * @return the entityRoleId
     */
    String getEntityRoleIdForEntity(Record inputRecord);

    /**
     * Loads risk coverage list for quote
     * @param inputRecord input record that contains entity id.
     * @return risk coverage list
     */
    public RecordSet loadAllQuoteRiskCovg(Record inputRecord, RecordLoadProcessor lp);

    /**
     * Checks for errors during Copy Quote
     * @param inputRecord input record.
     * @return YesNoFlag
     */
    public YesNoFlag isCopyQuoteError(Record inputRecord);

    /**
     * Gets the Copy Quote error transaction.
     * @param inputRecord input record.
     * @return transactionId
     */
    public String getCopyQuoteErrorTrans(Record inputRecord);

    /**
     *  Load all status.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllQuoteStatus(Record inputRecord);

    /**
     * Save a quote stauts.
     *
     * @param inputRecord
     */
    public void saveQuoteStatus(Record inputRecord);

    /**
     * Get the policy Id by policy No.
     *
     * @param inputRecord
     * @return String
     */
    public String getPolicyId(Record inputRecord);

    /**
     * Get policy no by policy id
     *
     * @param inputRecord
     * @return Sting
     */
    public String getPolicyNo(Record inputRecord);

    /**
     * Get Policy holder
     *
     * @param inputRecord
     * @return String
     */
    public String getPolicyHolder(Record inputRecord);

    /**
     * Method to load selected policy list for given input.
     *
     * @param inputRecord a record with query information
     * @param recordLoadProcessor a record load processor
     * @return a RecordSet with selected address and all available address records
     */
    RecordSet findAllPolicyForWS(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method that returns an instance of a recordset object with policy lock information for the provided
     * policy number.
     * <p/>
     *
     * @param inputRecord         an input record represents the search criteria
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet an instance of the recordset result object that contains policy lock information.
     */
    public RecordSet findAllPolicyOrMinimalInformationForWs(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Get the policy Id by policy No.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getPrimaryRisk(Record inputRecord);

    /**
     * Method that returns an instance of a record set object with the list of terms.
     * <p/>
     *
     * @param policyId policy pk
     * @return RecordSet an instance of the record set result object object with the list of terms.
     */
    public RecordSet loadPolicyTermList(String policyId);

    /**
     * To rerate On-demand.
     *
     * @param inputRecord
     * @return record
     */
    public Record reRateOnDemand(Record inputRecord);

    /**
     * To rerate Batch.
     *
     * @param inputRecord
     * @return record
     */
    public Record reRateBatch(Record inputRecord);

    /**
     * Load mass rerate result
     * @param inputRecord
     * @return mass rerate result
     */
    public RecordSet loadAllReRateResult(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Load mass rerate result detail
     * @param inputRecord
     * @return mass rerate result detail
     */
    public RecordSet loadAllReRateResultDetail(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Load all the policy detail information for the Policy Inquiry Service.
     * @param inputRecord
     * @return
     */
    public RecordSet loadPolicyDetailForWS (Record inputRecord);

    /**
     * Determines if policy retro date is editable.
     * @param inputRecord
     * @return
     */
    public boolean isPolicyRetroDateEditable (Record inputRecord);

    /** 
     * To load all policy billing accounts for policy level
     *
     * @param inputRecord
     * @return a list of billing account relations
     */
    public RecordSet loadPolicyBillingAccountInfoForWS(Record inputRecord);

    /**
     * Determines if entity belongs PM.
     *
     * @param inputRecord
     * @return result record which contains entity source #
     */
    public String isPolicyEntity(Record inputRecord);

    /**
     * Get indication for soft validation exists or not.
     * @param inputRecord
     * @return record
     */
    public Record loadSoftValidationB(Record inputRecord);

    /**
     * Retrieve latest term by policy no or policy fk.
     * @param inputRecord
     * @return Record include policy fk, policy no and term base fk
     */
    public Record getLatestTerm(Record inputRecord);

    /**
     * Check whether the current term is a new business term.
     * If the return is Y, the current term is a new business term.
     * If the return is N, the current term is a renewed term.
     * @param inputRecord
     * @return Y/N
     */
    public boolean isNewBusinessTerm(Record inputRecord);

    /**
     * Identify whether policyNos exist in system
     * <p/>
     *
     * @param inputRecord
     * @return invalid policyNo
     */
    public String validatePolicyNosExist(Record inputRecord);

    /**
     * Identify whether termBaseRecordIds exist in system
     * <p/>
     *
     * @param inputRecord
     * @return invalid termBaseRecordIds
     */
    public String validateTermBaseRecordIdsExist(Record inputRecord);

    /**
     * Get entity id
     * <p/>
     *
     * @param inputRecord
     */
    public String getEntityIdByClientId(Record inputRecord);

    /**
     * Return policyHeader for webService
     *
     * @param policyNo              policy number
     * @param termBaseRecordId      policy term history base record ID
     * @param transactionStatusCode transactionStatusCode
     */
    public PolicyHeader loadPolicyHeaderForWS(String policyNo, String termBaseRecordId, String transactionStatusCode);

    public static final String BEAN_NAME = "PolicyDAO";
}

