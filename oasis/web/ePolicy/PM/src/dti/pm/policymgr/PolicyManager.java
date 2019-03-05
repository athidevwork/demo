package dti.pm.policymgr;

import com.delphi_tech.ows.policy.ReferredPartyType;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSession;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.session.UserSessionManager;
import dti.pm.busobjs.PolicyViewMode;

import java.sql.SQLException;

/**
 * An interface that extends MaintainPolicy interfce to handle implementation of policy manager.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 13, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/09/2007       sxm         Added setCurrentIdsInSession().
 * 01/18/2008       fcb         isPaymentPlanLstEditable and isDeclinationDateEditable added.
 * 04/17/2008       fcb         loadAllQuoteRiskCovg added.
 * 04/30/2008       yyh         ProcessQuoteStatus:loadAllQuoteStatus and saveQuoteStatus added.
 * 07/25/2008       yyh         Add getPolicyId.
 * 06/11/2009       Joe         Remove the method deriveImageRightMapping() which has been refactored into Common Service.
 * 01/19/2011       wfu         113566 - Added interface validatePolicyForCreate and buildAddlParmsField 
 *                                       for copying policy from risk using.
 * 02/21/2011       wfu         113063 - Add interface triggerFormsFromQuoteStatus to trigger forms.
 * 04/29/2011       dzhang      120329 - Modified isPaymentPlanLstEditable.
 * 05/06/2011       fcb         119324 - getWorkbenchDefaultValues added.
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 07/24/2012       awu         129250 - Added processAutoSavePolicyWIP().
 * 09/06/2011       fcb         137198 - Added loadPolicyTermList.
 * 10/08/2012       xnie        133766 - Added reRateOnDemand(), reRateBatch(), performReRateOnDemand(),
 *                                       loadAllReRateResult(), and loadAllReRateResultDetail().
 * 12/12/2012       xnie        139838 - Modified reRateBatch() to change return value from void to record.
 * 05/29/2013       jshen       141758 - Added new method setCurrentIdsInSession()(one more parameter componentId added)
 * 01/26/2015       fcb         159897 - Added overloaded loadPolicyHeader.
 * 08/25/2015       awu         164026 - Added loadPolicyDetailForWS.
 * 01/21/2016       tzeng       166924 - Added isPolicyRetroDateEditable().
 * 01/25/2016       eyin        168882 - Added loadPolicyBillingAccountInfoForWS.
 * 03/08/2016       wdang       168418 - Move saveEntityRoleAddress to EntityManager.
 * 06/17/2016       eyin        177211 - Added generatePolicyNumberForWS().
 * 01/09/2016       tzeng       166929 - Added getLatestTerm(), isNewBusinessTerm().
 * 02/12/2018       lzhang      190834 - Added validatePolicyNosExist and validateTermBaseRecordIdsExist.
 * 04/12/2018       lzhang      191379 - Added loadPolicyHeaderForWS.
 * 11/02/2018       wrong       196790 - Added getEntityIdByClientId.
 * 11/28/2018       eyin        197179 - Added loadPolicyDetailListForWS().
 * ---------------------------------------------------
 */
public interface PolicyManager {

    public static final String CREATE_POLICY_ACTION_CLASS_NAME = "dti.pm.policymgr.struts.CreatePolicyAction";

    /**
     * method that returns an instance of Policy Header with all its member information loaded for the provided parameters.
     * @param policyNo           policy number
     * @param requestId          can be request URI or action class name
     * @return PolicyHeader an instance of Policy Header with all its member information loaded.
     */
    PolicyHeader loadPolicyHeader(String policyNo,String requestId, String process);

    /**
     * Method that returns an instance of Policy Header with all its member information loaded for the provided parameters.
     * <p/>
     *
     * @param policyNo              policy number
     * @param policyTermHistoryId   policy term history id
     * @param desiredPolicyViewMode desired view mode of WIP or OFFICIAL
     * @param endQuoteId            endorsement quote id
     * @param requestId             can be request URI or action class name
     * @return PolicyHeader an instance of Policy Header with all its member information loaded.
     */
    PolicyHeader loadPolicyHeader(String policyNo, String policyTermHistoryId, PolicyViewMode desiredPolicyViewMode,String endQuoteId,String requestId, String process);

    /**
     * Method that returns an instance of Policy Header with all its member information loaded for the provided parameters.
     * <p/>
     *
     * @param policyNo              policy number
     * @param policyTermHistoryId   policy term history id
     * @param desiredPolicyViewMode desired view mode of WIP or OFFICIAL
     * @param endQuoteId            endorsement quote id
     * @param requestId             can be request URI or action class name
     * @param process               the process that invoked this call
     * @param isMonitorPolicy       indicator whether the policy lock should be refreshed or not.
     * @return PolicyHeader an instance of Policy Header with all its member information loaded.
     */
    PolicyHeader loadPolicyHeader(String policyNo, String policyTermHistoryId, PolicyViewMode desiredPolicyViewMode,String endQuoteId,String requestId,String process,boolean isMonitorPolicy);

    /**
     * Method that returns an instance of disconnected resultset, loaded with policy data for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  record containing passed request parameters
     * @return Record containing the policy term information based on the input criteria
     */
    public Record loadPolicyDetail(PolicyHeader policyHeader, Record inputRecord);

/**
     * Method that returns an instance of disconnected resultset, loaded with policy data for the provided
     * policy information and load processor.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param inputRecord  record containing passed request parameters
     * @param loadProcessor an instance of data load processor
     * @return Record containing the policy term information based on the input criteria
     */
    public Record loadPolicyDetail(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Load policy summary for one client
     * @param inputRecord input record that contains entity id.
     * @return policy summary
     */
    public RecordSet loadAllPolicySummary(Record inputRecord);

     /**
     * Load related Endorsment/Renewal Quote of policy
     * @param policyHeader policyHeader.
     * @return quote list
     */
    public RecordSet loadAllEndorsementQuote(PolicyHeader policyHeader);

    /**
     * Wrapper to invoke the save of the updated Policy record and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader the summary policy information corresponding to the provided policy.
     * @param inputRecord  a set of Records, each with the updated Policy Detail info
     *                     matching the fields returned from the loadAllPolicy method.
     * @return the number of rows processed.
     */
    public int processSavePolicy(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Wrapper to invoke the auto save of the updated Policy record and subsequently
     *
     * @param policyHeader the summary policy information corresponding to the provided policy.
     * @param inputRecord  a set of Records, each with the updated Policy Detail info
     *                     matching the fields returned from the loadAllPolicy method.
     * @return the number of rows processed.
     */
    public int processAutoSavePolicyWIP(PolicyHeader policyHeader, Record inputRecord);

    /**
      * Returns a RecordSet loaded with list of available policies/quotes for the provided
      * inputRecord which contains the search criteria.
      * <p/>
      * @param  inputRecord a record contains all search criteria information.
      * @param  recordLoadProcessor an instance of data load processor
      * @return RecordSet a RecordSet loaded with list of available policies/quotes.
      */
     public RecordSet findAllPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
      * Returns a RecordSet loaded with list of available policies/quotes for the provided
      * inputRecord which contains the search criteria.
      * <p/>
      * @param  inputRecord a record contains all search criteria information.
      * @return RecordSet a RecordSet loaded with list of available policies/quotes.
      */
     public RecordSet findAllPolicy(Record inputRecord);

    /**
      * Method that gets the default values
      * based on parameters stored in the input Record.
      * <p/>
      * @param  inputRecord Record that contains input parameters

      * @return Record that contains default values
      */
     public Record getInitialValuesForCreatePolicy(Record inputRecord) throws SQLException;

    /**
      * Method that validates field that is being changed and
      * gets new default values
      * <p/>
      * @param  inputRecord Record that contains input values
      * @return Record that contains new default values
      */
     public Record handleFieldChangeForCreatePolicy(Record inputRecord);

     /**
      * Method that returns a RecordSet loaded with policy types
      * based on search criteria stored in the input Record.
      * <p/>
      * @param  inputRecord Record that contains search criteria
      * @param  doValidation TRUE if validation is required
      * @return RecordSet that contains policy types
      */
     public RecordSet findAllPolicyType(Record inputRecord, boolean doValidation);

    /**
     * Check policy existence
     *
     * @param inputRecord Record contains input values
     * @return String indicates if policy with the same policy typeexists
     */
    public String checkPolicyExistence(Record inputRecord);

    /**
      * Method that creates policy based on input Record.
      * <p/>
      * @param  inputRecord Record that contains new policy information
      * @param  doValidation TRUE if validation is required
      * @return String contains policy number
     */
    public String createPolicy(Record inputRecord, boolean doValidation);

    /**
     * Method that generate policy number based on input Record.
     * <p/>
     * @param  inputRecord Record that contains new policy number information
     * @return String contains policy number
     */
    public String generatePolicyNumberForWS(Record inputRecord);

    /**
      * Method that gets the default term expiration date
      * based on values stored in the input Record.
      * <p/>
      * @param  inputRecord Record that contains input values
      * @return Record that contains default term expiration date etc.
      */
     public String getDefaultTermExpirationDate(Record inputRecord);

    /**
     * Method that evaluates policy business rule for ability to edit the policy number,
     * updating the OasisField to editable if permitted.
     *
     * @param termRec   Record of current policy data that was retrieved for display
     * @return boolean indicating if the policy no field is editable
     */
    public boolean isPolicyNoEditable(Record termRec);

    /**
     * Method that evaluates policy business rule for ability to edit the renewal indicator,
     * updating the OasisField to editable if permitted.
     *
     * @param termRec   Record of current policy data that was retrieved for display
     * @return boolean indicating if the renewal indicator field is editable
     */
    boolean isRenewalIndicatorEditable(Record termRec);

    /**
     * Method that evaluates policy business rule for ability to edit the program code,
     * updating the OasisField to editable if permitted.
     *
     * @param termRec   Record of current policy data that was retrieved for display
     * @return boolean indicating if the program code field is editable
     */
    boolean isProgramCodeEditable(Record termRec);

    /**
     * Method that evaluates policy business rule for ability to edit the process location,
     * updating the OasisField to editable if permitted.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return boolean indicating if the process location field is editable
     */
    boolean isProcessLocationEditable(PolicyHeader policyHeader);

    /**
     * Method that evaluates policy business rule for ability to edit the IBNR date,
     * updating the OasisField to editable if permitted.
     *
     * @return boolean indicating if the policy no field is editable
     */
    public boolean isIbnrDateEditable();

    /**
     * Validates that the modified policy no is available.
     *
     * @param modifiedPolicyNo the user modified data value.
     * @param policyId         primary key value of the policy record being changed.
     */
    void validateModifiedPolicyNo(String modifiedPolicyNo, String policyId);

    /**
     * Method that validates data before creates policy.
     *
     * @param inputRecord Record that contains new policy information
     */
    void validatePolicyForCreate(Record inputRecord);

    /**
     * Deny quote
     *
     * @param policyHeader
     * @param inputRecord  for deny quote info
     */
    public void denyQuote(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Reactive quote
     *
     * @param policyHeader
     */
    public void reactiveQuote(PolicyHeader policyHeader);

    /**
     * Copy quote
     *
     * @param policyHeader
     * @param inputRecord  with copyQuote infomations
     * @return result record which contains copied quote #
     */
    public Record copyQuote(PolicyHeader policyHeader, Record inputRecord);

    /**
     * get initial values for  Deny Data
     * sets effective date to current term effective from date
     *
     * @param policyHeader
     * @return the return record of PRT
     */
    public Record getInitialValuesForDenyQuote(PolicyHeader policyHeader);

    /**
     * Accept quote
     *
     * @param policyHeader
     * @param inputRecord  with accept infomations
     * @return result record which contains parallel policy #
     */
    public Record acceptQuote(PolicyHeader policyHeader, Record inputRecord);    

    /**
     * Method that sets the current policyTermHistoryId, riskId, coverageId and coverageClassId in the given user session.
     *
     * @param policyTermHistoryId
     * @param riskId
     * @param coverageId
     * @param coverageClassId
     * @param userSession
     */
    public void setCurrentIdsInSession(String policyTermHistoryId, String riskId, String coverageId,
                                       String coverageClassId, UserSession userSession);

    /**
     * Method that sets the current policyTermHistoryId, riskId, coverageId, policyCovComponentId and coverageClassId in the given user session.
     *
     * @param policyTermHistoryId
     * @param riskId
     * @param coverageId
     * @param componentId
     * @param coverageClassId
     * @param userSession
     */
    public void setCurrentIdsInSession(String policyTermHistoryId, String riskId, String coverageId, String componentId,
                                       String coverageClassId, UserSession userSession);

    /**
     * Determines if the coverage class item should be enabled
     *
     * @param policyHeader Instance of the policy header
     * @param inputRecord Record containing policy/risk/term information
     * @param policyLevelCheck boolean indicator if this check is for the policy page
     * @return boolean indicating yes/no to enable the coverage class option
     */
    boolean isCoverageClassAvailable(PolicyHeader policyHeader, Record inputRecord, boolean policyLevelCheck);

     /**
     * Method to determine if an OOSE policy change has occurred and if so
     * call the database object to remove it, otherwise simply refreshing the page.
     *
     * @param policyHeader instance of the PolicyHeader object with current term/transaction data
     * @param inputRecord  record with current policy data from the page
     */
    void deleteOosPolicyDetail(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Check if record exist.
     *
     * @param inputRecord a Record with query conditions
     * @return YesNoFlag
     */
    YesNoFlag isRecordExist(Record inputRecord);

    /**
     * Method that evaluates policy business rule for ability to edit payment plan list.
     *
     * @param termRec Record of current policy data that was retrieved for display
     * @param policyHeader instance of the PolicyHeader object with current term/transaction data
     * @return boolean indicating if the payment plan list field is editable
     */
    public boolean isPaymentPlanLstEditable(Record termRec, PolicyHeader policyHeader);

    /**
     * Method that evaluates policy business rule for ability to edit declination date.
     *
     * @param termRec Record of current policy data that was retrieved for display
     * @return boolean indicating if the declination date field is editable
     */
    public boolean isDeclinationDateEditable(Record termRec);

    /**
     * Check if Program Retro Date is editable or not.
     * @param policyHeader
     * @return
     */
    public YesNoFlag isPolicyRetroDateEditable(PolicyHeader policyHeader);

    /**
     * Method to load selected address and all available address for the policyholder or COI Holder
     *
     * @param policyHeader instance of the PolicyHeader object with current term/transaction data
     * @param inputRecord a record with query information
     * @param loadProcessor an instance of data load processor
     * @return a RecordSet with selected address and all available address records
     */
    RecordSet loadAllAddress(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Builds an additional parameter string buffer for shared policy level fields
     *
     * @param inputRecord  a Record with all input information
     */
    String buildAddlParmsField(Record inputRecord);

    /**
     * Method to generate new policy no
     *
     * @param inputRecord  a Record with all input information
     */
    Record getParallelPolicyNo(Record inputRecord);

    /**
     * Copy policy to quote
     *
     * @param policyHeader
     * @param inputRecord
     * @return result record which contains parallel quote #
     */
    public Record copyPolicyToQuote(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Loads risk coverage list for quote
     * @param inputRecord input record that contains entity id.
     * @return risk coverage list
     */
    public RecordSet loadAllQuoteRiskCovg(Record inputRecord, RecordLoadProcessor lp);

    /**
     * Load all status
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllQuoteStatus(PolicyHeader policyHeader);

    /**
     * save a quote status
     *
     * @param policyHeader
     * @param inputRecords
     */
    public void saveQuoteStatus(PolicyHeader policyHeader,RecordSet inputRecords);

    /**
     * Get the policy Id by policy No.
     *
     * @param inputRecord
     * @return String
     */
    public String getPolicyId(Record inputRecord);

    /**
     * Get the policy No by policy id.
     *
     * @param policyId
     * @return String
     */
    public String getPolicyNo(String policyId);

    /**
     * Get Policy holder
     * @param inputRecord
     * @return String
     */
    public String getPolicyHolder(Record inputRecord);

    /**
     * Method to generate forms trigger process.
     *
     * @param policyHeader the summary policy information.
     * @param inputRecord Record with the related entry info
     */
    public void triggerFormsFromQuoteStatus(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Method to get workbench default values.
     * @return Record
     */
    public Record getWorkbenchDefaultValues();

    /**
     * Returns a RecordSet loaded with list of available policies/quotes for the provided
     * inputRecord which contains the search criteria.
     * <p/>
     *
     * @param inputRecord         a record contains all search criteria information.
     * @return RecordSet a RecordSet loaded with list of available policies/quotes.
     */
    public RecordSet findAllPolicyForWS(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of available policies/quotes for the provided
     * inputRecord which contains the search criteria.
     * <p/>
     *
     * @param inputRecord a record contains all search criteria information.
     * @return RecordSet a RecordSet loaded with list of available policies/quotes.
     */
    public RecordSet findAllPolicyMinimalInformationForWs(Record inputRecord);

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
     * Rerate On-demand.
     *
     * @param inputRecord
     * @return a Record with workflow instance Id and workflow state
     */
    public Record reRateOnDemand(Record inputRecord);

    /**
     * Rerate Batch.
     *
     * @param inputRecord
     * @return Record
     */
    public Record reRateBatch(Record inputRecord);

    /**
     * Returns a RecordSet of mass rerate result for the provided
     * inputRecord which contains the search criteria.
     * <p/>
     *
     * @param inputRecord a record contains all search criteria information.
     * @return RecordSet a RecordSet of mass rerate result.
     */
    public RecordSet loadAllReRateResult(Record inputRecord);

    /**
     * Returns a RecordSet of mass rerate result detail for the provided
     * inputRecord which contains the search criteria.
     * <p/>
     *
     * @param inputRecord a record contains all search criteria information.
     * @return RecordSet a RecordSet of mass rerate result detail.
     */
    public RecordSet loadAllReRateResultDetail(Record inputRecord);

    /**
     * Load the billing accounts for policy.
     * @param inputRecord
     * @return
     */
    public RecordSet loadPolicyBillingAccountInfoForWS(Record inputRecord);

    /**
     * Perform rerate On-demand.
     *
     * @param inputRecord
     * @return String
     */
    public String performReRateOnDemand(Record inputRecord);

    /**
     * Returns the UserSessionManager object.
     *
     * @return
     */
    public UserSessionManager getUserSessionManager();

    /**
     * Load the policy detail information for the Policy Inquiry Service.
     * @param inputRecord
     * @return
     */
    public Record loadPolicyDetailForWS(Record inputRecord);

    /**
     * Load the policy detail list for the Policy Inquiry Service.
     * @param inputRecord
     * @return
     */
    public RecordSet loadPolicyDetailListForWS(Record inputRecord);

    /**
     * Determines if entity belongs to PM.
     *
     * @param inputRecord
     * @return
     */
    public String isPolicyEntity(Record inputRecord);

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
     * Filter records by transStatusCode.
     *
     * @param inputRecordSet
     * @return filterRs
     */
    public RecordSet transStatusCodeFilterRecForWS(RecordSet inputRecordSet, String transStatusCodeFilter, String transStatusCodeResult);

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
     * Get entity id by client id
     * <p/>
     *
     * @param partNumberId
     * @param clientId
     */
    public String getEntityIdByClientId(String partNumberId, String clientId);

    /**
     * load policy header for webService
     * <p/>
     *
     * @param policyNo
     * @param termBaseRecordId
     * @param transactionStatusCode
     * @return policyHeader
     */
    public PolicyHeader loadPolicyHeaderForWS(String policyNo, String termBaseRecordId, String transactionStatusCode);

    public static final String BEAN_NAME = "PolicyManager";
    public static final String OASIS_WORKFLOW_BEAN_NAME = "OasisWorkflowAgentImpl";
}
