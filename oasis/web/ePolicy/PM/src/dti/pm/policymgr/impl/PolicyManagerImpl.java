package dti.pm.policymgr.impl;

import com.delphi_tech.ows.policy.ReferredPartyType;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.filter.Filter;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.workflowmgr.WorkflowFields;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.PolicyHeaderReloadCode;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.busobjs.UserProfiles;
import dti.pm.core.data.CommonTabsEntitlementRecordLoadProcessor;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.core.http.RequestIds;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.entitymgr.EntityManager;
import dti.pm.entitymgr.EntityFields;
import dti.pm.pmdefaultmgr.PMDefaultManager;
import dti.pm.policymgr.CreatePolicyFields;
import dti.pm.policymgr.DenyQuoteFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.dao.PolicyDAO;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.policymgr.service.ComponentInquiryFields;
import dti.pm.policymgr.service.CoverageInquiryFields;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.policymgr.service.RiskInquiryFields;
import dti.pm.policymgr.taxmgr.TaxManager;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskManager;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.validationmgr.impl.AccountingMonthRecordValidator;
import dti.pm.validationmgr.impl.SimilarPolicyRecordValidator;
import dti.pm.validationmgr.impl.ValidTermDurationRecordValidator;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.componentmgr.ComponentManager;
import dti.oasis.workflowmgr.WorkflowAgent;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for policy manager.
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
 * 07/09/2007       sxm         Added setCurrentIdsInSession().
 * 07/10/2007       sxm         Refactor getDefaultState() into PMDefaultManager
 * 09/07/2007       sxm         Removed call to CreatePolicyFields.setFields() from getInitialValuesForCreatePolicy()
 * 10/02/2007       sxm         Added logic to set policy lock message in MessageManager
 * 10/05/2007       fcb         findAllPolicy: removed sorting, as it was moved to Workbench.
 * 10/19/2007       sxm         Reuse policy lock ID if we got it
 * 01/18/2008       fcb         isPaymentPlanLstEditable and isPaymentPlanLstEditable added.
 * 03/28/2008       fcb         acceptQuote: quoteTransactionCode added.
 * 04/17/2008       fcb         loadAllQuoteRiskCovg added.
 * 04/30/2008       yyh         ProcessQuoteStatus:loadAllQuoteStatus,saveQuoteStatus and validateQuoteStatus added.
 * 07/25/2008       yyh         Add getPolicyId.
 * 06/11/2009       Joe         Remove the method deriveImageRightMapping() which has been refactored into Common Service.
 * 04/29/2010       bhong       106372: Added missing parameters newPolType,riskBaseId,dummyState in "copyQuote" method.
 * 11/02/2010       syang       111070 - Removed the workflow attribute "applyTransB".
 * 01/04/2011       wfu         104540 - Changed field "addlParm" to "addlParms" to input additional parameters.
 * 01/19/2011       wfu         113566 - Changed method validatePolicyForCreate to public for copying policy from risk.
 * 02/21/2011       wfu         113063 - Add method triggerFormsFromQuoteStatus to trigger forms and modified method
 *                                       validateQuoteStatus to support new field quote version validation.
 * 04/22/2011       wqfu        119705 - Modified triggerFormsFromQuoteStatus to process output with OUTPUT transaction
 *                                       status finished.
 * 04/26/2011       ryzhao      116863 - Update isCoverageClassAvailable() to set inputRecord with policy header fields.
 *                                       System will use term eff date and term exp date to load primary risk.
 * 04/29/2011       dzhang      120329 - Modified isPaymentPlanLstEditable: Payment Plan field should be editable in Quote/Policy
 *                              during all modes except cancellation/reinstate and official mode.
 * 05/06/2011       fcb         119324 - getWorkbenchDefaultValues added.
 * 08/30/2011       ryzhao      124458 - Modified validateQuoteForDeny to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 08/31/2011       ryzhao      124458 - Remove DateUtils.formatDate() and call FormatUtils.formatDateForDisplay() directly.
 * 09/06/2011       dzhang      121130 - Modified loadAllAddress.
 * 09/23/2011       wfu         125369 - Renamed several fields duplicate in policy header and policy page.
 * 10/25/2011       lmjiang     126016 - Check if the input record contains field 'regionalOffice' in the validation before finding a policy type.
 * 11/21/2011       sxm         issue 126493 - Invoke changePolicy() to update policy table when endorse OFFICIAL term.
 * 12/12/2011       dzhang      128024 - Modified validatePolicy().
 * 12/15/2011       fcb         128024 - Reverted the previous change.
 * 03/08/2011       fcb         129528 - Policy Web Services.
 * 07/19/2012       awu         134738 - Modified  validatePolicy to add logic that if he OOSE expiration date is null,
 *                                       it will be reset to original date.
 * 07/24/2012       awu         129250 - Added processAutoSavePolicyWIP(), processSavePolicyData().
 *                                       Modified processSavePolicy() to call processSavePolicyData().
 * 08/28/2012       xnie        120683 - Modified saveEntityRoleAddress() to use passed transactionId/entityRoleId if
 *                                       they are existed.
 * 09/06/2011       fcb         137198 - Added loadPolicyTermList.
 * 10/08/2012       xnie        133766 - Added reRateOnDemand(), reRateBatch(), loadAllReRateResult(),
 *                                       loadAllReRateResultDetail(), performReRateOnDemand(), and
 *                                       isMassReRateLongRunning().
 * 10/10/2012       tcheng      136501 - Modified createPolicy() to add more error message.
 * 10/16/2012       tcheng      136500 - Modified getInitialValuesForCreatePolicy() to make Regional Office,Issue Company
 *                                       and Issue State fields default value be able to work when they are set up in the eAdmin.
 * 10/18/2012       xnie        133766 - Removed isMassReRateLongRunning().
 * 11/19/2012       xnie        138948 - Modified reRateOnDemand() to change 'ReRate' to 'Rerate'.
 * 12/12/2012       xnie        139838 - Modified reRateBatch() to change return value from void to record.
 * 05/29/2013       jshen       141758 - Added new method setCurrentIdsInSession()(one more parameter componentId added)
 * 10/01/2013       fcb         145725 - Added logic to check the profile in UserCacheManager via OasisUser.
 * 10/31/2013       jyang       148585 - Added attribute coverageid when get value of isCoverageClassAvailable
 * 11/06/2013       jyang	    148585 - Roll back the change for issue148585.
 * 12/20/2013       jyang       148585 - Remove the logic which set riskId to null in inputRecord from
 *                                       isCoverageClassAvailable function.
 * 04/16/2014       awu         150201 - Modified savePolicy to set orgHierRootId to policyHeader.
 * 07/25/2014       awu         152034 - 1). Roll back the changes of issue148585.
 *                                       2). Modified internalLoadPolicyHeader to set the previous riskHeader and coverageHeader.
 * 08/14/2014       fcb         154159 - Bug fixed.
 * 10/22/2014       wdang       158112 - Added taxManager reference for PolicyEntitlementRecordLoadProcessor in loadPolicyDetail().
 * 01/26/2015       fcb         159897 - Added overloaded loadPolicyHeader.
 *                                       Added logic to only refresh the policy lock when necessary.
 * 08/25/2015       awu         164026 - Added loadPolicyDetailForWS.
 * 11/05/2015       tzeng       165790 - Modified internalLoadPolicyHeader to update policy phase code of policy header
 *                                       from request.
 * 01/21/2016       tzeng       166924 - Added isPolicyRetroDateEditable().
 * 01/25/2016       eyin        168882 - Added loadPolicyBillingAccountInfoForWS.
 * 03/08/2016       wdang       168418 - Move saveEntityRoleAddress to EntityManagerImpl.
 * 06/17/2016       eyin        177211 - Added generatePolicyNumberForWS().
 * 08/26/2016       wdang       167534 - Call getParallelPolicyNo when PM_QTE_NO_FROM_POL is N.
 * 02/08/2017       wrong       182593 - Modified createPolicy() to handle invalid transction code case.
 * 01/09/2016       tzeng       166929 - Added getLatestTerm(), isNewBusinessTerm().
 * 04/26/2017       mlm         184827 - Refactored to initialize previewRequest in policy header correctly.
 * 09/21/2017       eyin        169483 - Modified loadAllAddress() to process isFromExposure indicator.
 * 01/29/2017       lzhang      191116 - Modified findAllPolicyMinimalInformationForWs/loadPolicyDetailForWS:
 *                                       get latest policy info
 * 02/06/2017       lzhang      190834 - 1)Modified findAllPolicyMinimalInformationForWs/loadPolicyDetailForWS:
 *                                         move record filter logic to new procedure transStatusCodeFilterRecForWS()
 *                                       2)Add new procedure transStatusCodeFilterRecForWS():
 *                                         enhance it with transStatusCodeFilter
 *                                       3)Added validatePolicyNosExist and validateTermBaseRecordIdsExist.
 * 04/12/2018       lzhang      191379 - Added loadPolicyHeaderForWS.
 * 11/02/2018       wrong       196790 - Added getEntityIdByClientId.
 * 11/09/2018       wrong       194062 - Modified setDefaultFlags() to add condition to skip creating default risk.
 * 11/28/2018       eyin        197179 - Added loadPolicyDetailListForWS().
 * ---------------------------------------------------
 */
public class PolicyManagerImpl implements PolicyManager, PolicySaveProcessor {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Returns a RecordSet loaded with list of available policies/quotes for the provided
     * inputRecord which contains the search criteria.
     * <p/>
     *
     * @param inputRecord a record contains all search criteria information.
     * @return RecordSet a RecordSet loaded with list of available policies/quotes.
     */
    public RecordSet findAllPolicy(Record inputRecord) {
        RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
        return findAllPolicy(inputRecord, selectIndProcessor);

    }

    /**
     * Returns a RecordSet loaded with list of available policies/quotes for the provided
     * inputRecord which contains the search criteria.
     * <p/>
     *
     * @param inputRecord         a record contains all search criteria information.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available policies/quotes.
     */
    public RecordSet findAllPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        return getPolicyDAO().findAllPolicy(inputRecord, recordLoadProcessor);
    }

    /**
     * Returns a RecordSet loaded with list of available policies/quotes for the provided
     * inputRecord which contains the search criteria.
     * <p/>
     *
     * @param inputRecord a record contains all search criteria information.
     * @return RecordSet a RecordSet loaded with list of available policies/quotes.
     */
    public RecordSet findAllPolicyForWS(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "findAllPolicyForWS", new Object[]{inputRecord});

        if (inputRecord.hasFieldValue(PolicyInquiryFields.FULL_NAME)) {
            parseFullName(inputRecord);
        }

        RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();

        l.exiting(getClass().getName(), "findAllPolicyForWS", inputRecord);

        return getPolicyDAO().findAllPolicyForWS(inputRecord, selectIndProcessor);
    }

    /**
     * Returns a RecordSet loaded with list of available policies/quotes for the provided
     * inputRecord which contains the search criteria.
     * <p/>
     *
     * @param inputRecord a record contains all search criteria information.
     * @return RecordSet a RecordSet loaded with list of available policies/quotes.
     */
    public RecordSet findAllPolicyMinimalInformationForWs(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "findAllPolicyMinimalInformationForWs", new Object[]{inputRecord});

        if (inputRecord.hasFieldValue(PolicyInquiryFields.FULL_NAME)) {
            parseFullName(inputRecord);
        }

        RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();

        RecordSet rs = getPolicyDAO().findAllPolicyOrMinimalInformationForWs(inputRecord, selectIndProcessor);

        Iterator it = rs.getRecords();
        Record prevRs = new Record();
        Record curRs = new Record();
        RecordSet output = new RecordSet();
        // get all terms
        List<String> termBaseRecIds = new ArrayList<String>();
        while (it.hasNext()) {
            curRs = (Record) it.next();
            if(prevRs.getSize() == 0){
                termBaseRecIds.add(curRs.getStringValue(PolicyInquiryFields.TERM_BASE_RECORD_ID));
            }else if(!prevRs.getStringValue(PolicyInquiryFields.TERM_BASE_RECORD_ID).equals(curRs.getStringValue(PolicyInquiryFields.TERM_BASE_RECORD_ID))){
                termBaseRecIds.add(curRs.getStringValue(PolicyInquiryFields.TERM_BASE_RECORD_ID));
            }
            prevRs = curRs;
        }

        Iterator<String> termIt = termBaseRecIds.iterator();
        RecordSet rsPerTerm = new RecordSet();
        String termBaseRecId;
        // get all records per term
        while (termIt.hasNext()) {
            termBaseRecId = termIt.next();
            rsPerTerm = rs.getSubSet(new RecordFilter(PolicyInquiryFields.TERM_BASE_RECORD_ID, termBaseRecId));

            RecordSet filterRs = new RecordSet();
            String transStatusCodeFilter = inputRecord.getStringValue(PolicyInquiryFields.TRANSACTION_STATUS_CODE);
            String transStatusCodeResult = rsPerTerm.getFirstRecord().getStringValue(PolicyInquiryFields.TRANSACTION_STATUS_CODE);
            filterRs = transStatusCodeFilterRecForWS(rsPerTerm, transStatusCodeFilter, transStatusCodeResult);
            if(filterRs.getSize() != 0) {
                output.addRecord(filterRs.getFirstRecord());
            }
        }


        l.exiting(getClass().getName(), "findAllPolicyMinimalInformationForWs", inputRecord);

        return output;
    }

    /**
     * Method to parse a full name is Last, First format.
     * @param inputRecord
     */
    public void parseFullName(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "parseFullName", new Object[]{inputRecord});

        String fullName = null;

        if (inputRecord.hasField(PolicyInquiryFields.FULL_NAME)) {
            fullName = inputRecord.getStringValue(PolicyInquiryFields.FULL_NAME);
        }

        if (fullName!=null) {
            String [] names = fullName.split(",");
            String firstName, lastName;

            if (names.length>1) {
                lastName = names[0].trim();
                firstName = names[1].trim();
            }
            else {
                lastName = "";
                firstName = "";
            }

            inputRecord.setFieldValue(PolicyInquiryFields.LAST_NAME, lastName);
            inputRecord.setFieldValue(PolicyInquiryFields.FIRST_NAME, firstName);
        }

        l.exiting(getClass().getName(), "parseFullName", inputRecord);
    }

    /**
     * method that returns an instance of Policy Header with all its member information loaded for the provided parameters.
     *
     * @param policyNo  policy number
     * @param requestId can be request URI or action class name
     * @return PolicyHeader an instance of Policy Header with all its member information loaded.
     */
    public PolicyHeader loadPolicyHeader(String policyNo, String requestId, String process) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyHeader", new Object[]{policyNo});

        PolicyHeader policyHeader = null;
        policyHeader = loadPolicyHeader(policyNo, null, PolicyViewMode.WIP, null, requestId, process);

        l.exiting(getClass().getName(), "loadPolicyHeader", policyHeader);
        return policyHeader;
    }

    /*
       Policy Header Information is loaded as per the following logic:

        1. Look-up in request storage for the existence of policy header.
           If found, return the policy header; otherwise, continue.
        2. Look-up the session information for the existence of policy header.
        3. If found, make sure the policy information is same as that of the requested policy.
           If the policy number doesnt match, unlock the prior policy, if any lock has been held and
           go to step 5. If the policy number matches, then continue.
        4. Check the requested term is same as the currently selected term in the cache.
           If it is not same, unlock the policy for the prior term (if any lock has been obtained earlier) - go to step 6.

        5. Load the policy from database.
        6. Load requested term's last transaction information (if no term is requested, load the most recent term's last
           transaction information)
        7. If the last transaction is in wip, try to obtain a lock. If a lock cant be obtained, reset the view mode
           as official view; otherwise reset the view mode same as requested view mode.
        8. Return the policy header.

    */

    /**
     * Method that returns an instance of Policy Header with all its member information loaded for the provided parameters.
     * <p/>
     *
     * @param policyNo              policy number
     * @param policyTermHistoryId   policy term history id
     * @param desiredPolicyViewMode desired view mode of WIP or OFFICIAL
     * @param endQuoteId       selected endorsement quote Id
     * @param requestId             requestId can be action class name or request URI
     * @return PolicyHeader an instance of Policy Header with all its member information loaded.
     */
    public PolicyHeader loadPolicyHeader(String policyNo, String policyTermHistoryId, PolicyViewMode desiredPolicyViewMode, String endQuoteId, String requestId, String process) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyHeader", new Object[]{policyNo, policyTermHistoryId, desiredPolicyViewMode});
        }

        PolicyHeader policyHeader = loadPolicyHeader(policyNo, policyTermHistoryId, desiredPolicyViewMode, endQuoteId, requestId, process, false);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyHeader", policyHeader);
        }
        return policyHeader;
    }

    /**
     * Method that returns an instance of Policy Header with all its member information loaded for the provided parameters.
     * <p/>
     *
     * @param policyNo              policy number
     * @param policyTermHistoryId   policy term history id
     * @param desiredPolicyViewMode desired view mode of WIP or OFFICIAL
     * @param endQuoteId       selected endorsement quote Id
     * @param requestId             requestId can be action class name or request URI
     * @param isMonitorPolicy       indicator whether the request only monitors the policy or not.
     * @return PolicyHeader an instance of Policy Header with all its member information loaded.
     */
    public PolicyHeader loadPolicyHeader(String policyNo, String policyTermHistoryId, PolicyViewMode desiredPolicyViewMode, String endQuoteId, String requestId, String process, boolean isMonitorPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyHeader", new Object[]{policyNo, policyTermHistoryId, desiredPolicyViewMode, endQuoteId, requestId, process, isMonitorPolicy});
        }

        PolicyHeader policyHeader = internalLoadPolicyHeader(policyNo, policyTermHistoryId, desiredPolicyViewMode, endQuoteId, process, isMonitorPolicy);
        //validate transaction
        try {
            if (policyHeader != null)
                getTransactionManager().validateTransaction(policyHeader, requestId);
        }
        catch (ValidationException ve) {
            //reload policyHeader
            RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
            getUserSessionManager().getUserSession().remove(UserSessionIds.POLICY_HEADER);
            internalLoadPolicyHeader(policyNo, policyTermHistoryId, desiredPolicyViewMode, endQuoteId, process, isMonitorPolicy);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyHeader", policyHeader);
        }
        return policyHeader;
    }

    /**
     * Method that returns an instance of Policy Header with all its member information loaded for the provided parameters.
     * <p/>
     *
     * @param policyNo              policy number
     * @param policyTermHistoryId   policy term history id
     * @param desiredPolicyViewMode desired view mode of WIP or OFFICIAL
     * @param endQuoteId       selected endorsement quote id
     * @param isMonitorPolicy       boolean indicating whether the current process only monitors or not
     * @return PolicyHeader an instance of Policy Header with all its member information loaded.
     */
    private PolicyHeader internalLoadPolicyHeader(String policyNo, String policyTermHistoryId, PolicyViewMode desiredPolicyViewMode, String endQuoteId, String process, boolean isMonitorPolicy) {
        Logger l = LogUtils.enterLog(getClass(), "internalLoadPolicyHeader", new Object[]{policyNo, policyTermHistoryId, desiredPolicyViewMode, endQuoteId, process, isMonitorPolicy});

        PolicyHeader policyHeader = null;

        try {
            if (RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_HEADER)) {
                policyHeader = (PolicyHeader) RequestStorageManager.getInstance().get(RequestStorageIds.POLICY_HEADER);
            }
            else {
                String webSessionId = null;
                boolean isPageHeaderReloadRequired = isReloadPageHeader(policyNo, policyTermHistoryId, policyHeader, desiredPolicyViewMode, endQuoteId);

                if (isPageHeaderReloadRequired && !StringUtils.isBlank(policyNo)) {
                    l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", "Loading Policy Header information from the database.");

                    // set term IDs to load
                    String termBaseRecordIdToLoad = null;
                    String policyTermHistoryIdToLoad = policyTermHistoryId;
                    if (getUserSessionManager().getUserSession().has(UserSessionIds.POLICY_HEADER)) {
                        policyHeader = (PolicyHeader) getUserSessionManager().getUserSession().get(UserSessionIds.POLICY_HEADER);

                        if (policyHeader.isReloadRequired()) {

                            PolicyHeaderReloadCode policyHeaderReloadCode = policyHeader.getReloadCode();
                            if (policyHeaderReloadCode.isCurrentTerm()) {
                                termBaseRecordIdToLoad = policyHeader.getTermBaseRecordId();
                            }
                            else if (policyHeaderReloadCode.isLastTerm()) {
                                policyTermHistoryIdToLoad = null;
                            }
                        }
                    }

                    //utilizing the lock id in policy header if it exists, otherwise generate new
                    if (policyHeader != null) {
                        webSessionId = policyHeader.getPolicyLockId();
                    }
                    if (StringUtils.isBlank(webSessionId)) {
                        webSessionId = getLockManager().generateLockId(policyNo);
                    }

                    //load the policy header
                    policyHeader = getPolicyDAO().loadPolicyHeader(policyNo, termBaseRecordIdToLoad, policyTermHistoryIdToLoad,
                        desiredPolicyViewMode, endQuoteId, webSessionId, getLockManager().getLockDuration());

                    //load last transaction information for the selected policy and term.
                    policyHeader.setLastTransactionInfo(getTransactionManager().loadLastTransactionInfoForTerm(policyHeader));

                    // set lock message only if we load the policy header from DB
                    if (!StringUtils.isBlank(policyHeader.getPolicyIdentifier().getPolicyLockMessage())) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainPolicy.lockpolicy.error",
                            new Object[]{policyHeader.getPolicyIdentifier().getPolicyLockMessage()});
                    }

                    PolicyHeader previousPolicyHeader = (PolicyHeader) UserSessionManager.getInstance().getUserSession().get("PreviousPolicyHeader");
                    if (previousPolicyHeader != null) {
                        policyHeader.setCoverageHeader(previousPolicyHeader.getCoverageHeader());
                        policyHeader.setRiskHeader(previousPolicyHeader.getRiskHeader());
                        UserSessionManager.getInstance().getUserSession().remove("PreviousPolicyHeader");
                    }

                    l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", "is Last Transaction for the term is in wip?" + String.valueOf(policyHeader.getLastTransactionInfo().getTransactionStatusCode().isInProgress()));
                    l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", policyHeader.getLastTransactionInfo().toString());
                    l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", "Successfully loaded policy header information from database.");
                }
                else {
                    policyHeader = (PolicyHeader) getUserSessionManager().getUserSession().get(UserSessionIds.POLICY_HEADER);
                }

                if (!StringUtils.isBlank(policyNo)) {
                    //If the the current view mode is WIP handle locking
                    if (PolicyViewMode.WIP.equals(policyHeader.getPolicyIdentifier().getPolicyViewMode())) {
                        //User is trying to access the wip version of the policy
                        //First deal with the lock id
                        //generateLockId(policyNo, policyHeader.getPolicyIdentifier());
                        if (isMonitorPolicy) {
                            //Just refresh the lock duration when the current process just monitors the policy
                            boolean isSuccess = getLockManager().refreshPolicyLock(policyHeader, "Internal Load Policy Header - Refresh Lock Policy, Process = " + process + ", Policy View = WIP");
                            if (isSuccess) {
                                l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", "policy lock extended successfully!!!!!!!!!!!!!!!!!!!!");
                            }
                        }
                        else if (getLockManager().lockPolicy(policyHeader, "Internal Load Policy Header - Lock Policy, Process = " + process + ", Policy View = WIP")) {
                            //The lock was already obtained when loading the policy header
                            l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", "policy locked successfully!!!!!!!!!!!!!!!!!!!!");
                        }
                        else {
                            //Always allow access only to official version of the policy, if a policy lock is not obtained.
                            policyHeader.getPolicyIdentifier().setPolicyViewMode(PolicyViewMode.OFFICIAL);
                            policyHeader.setShowViewMode(false);
                            l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", "unable to lock the policy!!!!!!!!!!!!!!!!!!!!");
                        }
                    }

                    // So, check if workflow exists (for long running transactions) in order to reset the preview indicator,
                    // in case if this is a workflow started as a result of preview.
                    WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                    if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                        // If this request is part of a workflow, reset the preview request indicator because it could have been loaded from cached session.
                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", "The request is part of a workflow instance [id=" + policyHeader.getPolicyNo() + ". Checking whether this is a preview request...");
                        }
                        if (wa.hasWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.IS_PREVIEW_REQUEST)) {
                            if (l.isLoggable(Level.FINE)) {
                                l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", "[" + policyHeader.getPolicyNo() + "] The preview indicator is :" + ((YesNoFlag) wa.getWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.IS_PREVIEW_REQUEST)).booleanValue());
                            }
                            policyHeader.setPreviewRequest(((YesNoFlag) wa.getWorkflowAttribute(policyHeader.getPolicyNo(), RequestIds.IS_PREVIEW_REQUEST)).booleanValue());
                        } else {
                            if (l.isLoggable(Level.FINE)) {
                                l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", "[" + policyHeader.getPolicyNo() + "] This workflow is not part of preview request.");
                            }
                        }
                    } else {
                        if (RequestStorageManager.getInstance().has(RequestIds.IS_PREVIEW_REQUEST)) {
                            policyHeader.setPreviewRequest((Boolean) RequestStorageManager.getInstance().get(RequestIds.IS_PREVIEW_REQUEST));
                        } else {
                            if (UserSessionManager.getInstance().getUserSession().has(RequestIds.IS_PREVIEW_REQUEST)) {
                                policyHeader.setPreviewRequest(((YesNoFlag) UserSessionManager.getInstance().getUserSession().get(RequestIds.IS_PREVIEW_REQUEST)).booleanValue());
                            }
                        }
                    }

                    //Set selected policy phase code to policy header.
                    if(RequestStorageManager.getInstance().has(POLICY_PHASE_CODE)) {
                        policyHeader.setPolPhaseCode((String)RequestStorageManager.getInstance().get(POLICY_PHASE_CODE));
                    }

                    //Cache the policy header information in session and request
                    RequestStorageManager.getInstance().set(RequestStorageIds.POLICY_HEADER, policyHeader);
                    UserSessionManager.getInstance().getUserSession().set(UserSessionIds.POLICY_HEADER, policyHeader);
                }
                else {
                    // If the requested page is not related to a policy, remove the policy header information from session.
                    if (getUserSessionManager().getUserSession().has(UserSessionIds.POLICY_HEADER)) {
                        UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_HEADER);
                    }
                }
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load policy header information for 'policyNo'" + policyNo + ", termHistoryId'" + policyTermHistoryId + "'", e);
            l.throwing(getClass().getName(), "internalLoadPolicyHeader", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "internalLoadPolicyHeader", policyHeader);
        return policyHeader;
    }

    /**
     * Method that returns an instance of disconnected resultset, loaded with policy data for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  record containing passed request parameters
     * @return Record containing the policy term information based on the input criteria
     */
    public Record loadPolicyDetail(PolicyHeader policyHeader, Record inputRecord) {
        return loadPolicyDetail(policyHeader, inputRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
    }

    /**
     * Method that returns an instance of disconnected resultset, loaded with policy data for the provided
     * policy information and load processor.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param inputRecord   record containing passed request parameters
     * @param loadProcessor an instance of data load processor
     * @return Record containing the policy term information based on the input criteria
     */
    public Record loadPolicyDetail(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyDetail", new Object[]{policyHeader});

        RecordSet rs, filterRs;
        RecordFilter filter;
        Record outputRecord;

        try {
            // Get the policy DAO to perform the operation
            PolicyDAO p = getPolicyDAO();

            // check if user has Select Address profile or not
            boolean userHasProfileForAddress = UserSessionManager.getInstance().getUserSession().getOasisUser().hasProfile("PM_EDIT_POL_ADDRESS");
            inputRecord.setFieldValue("userHasProfileForAddress", Boolean.toString(userHasProfileForAddress));

            PolicyEntitlementRecordLoadProcessor rowAccessorLP =
                new PolicyEntitlementRecordLoadProcessor(this, policyHeader, getTransactionManager(), getRiskManager(),getComponentManager(), getTaxManager(), inputRecord);
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(rowAccessorLP, loadProcessor);

            CommonTabsEntitlementRecordLoadProcessor commonTabsLP = new CommonTabsEntitlementRecordLoadProcessor(policyHeader);
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(commonTabsLP, loadProcessor);

            // Retrieve one or more records
            rs = p.loadAllPolicy(policyHeader, loadProcessor);

            // Filter the records based on the desired view mode
            if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
                filter = new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
                filterRs = rs.getSubSet(filter);

                // If we have no records check if a lock message was returned
                // which indicates we tried to get the WIP but the policy was locked.
                // Go ahead and show the WIP records with the lock message
                if (filterRs.getSize() == 0) {
                    if (!StringUtils.isBlank(policyHeader.getPolicyIdentifier().getPolicyLockMessage())) {
                        filter = new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP);
                        filterRs = rs.getSubSet(filter);
                    }
                }
            }
            else if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote()) {
                filter = new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.ENDQUOTE);
                filterRs = rs.getSubSet(filter);

                // If no records after the filter, no TEMP changes so take the OFFICIAL picture
                if (filterRs.getSize() == 0) {
                    filter = new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
                    filterRs = rs.getSubSet(filter);
                }else{
                    filter=new RecordFilter("endorsementQuoteId",policyHeader.getLastTransactionInfo().getEndorsementQuoteId());
                    filterRs= filterRs.getSubSet(filter);
                }
            }
            else {
                filter = new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP);
                filterRs = rs.getSubSet(filter);

                // If no records after the filter, no TEMP changes so take the OFFICIAL picture
                if (filterRs.getSize() == 0) {
                    filter = new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
                    filterRs = rs.getSubSet(filter);
                }
            }

            if (filterRs.getSize() != 1) {
                throw new AppException("Unable to filter the correct term record: " + policyHeader.getPolicyNo());
            }

            // Build the input record and load the detail policy information
            Record addlInfoRecord = policyHeader.toRecord();
            filterRs.setFieldsOnAll(p.loadAddlInfo(addlInfoRecord));

            // set policy notes field
            if (filterRs.getFirstRecord().hasStringValue("noteB")) {
                YesNoFlag noteB = YesNoFlag.getInstance(filterRs.getFirstRecord().getStringValue("noteB"));
                if(noteB.booleanValue()){
                    filterRs.getFirstRecord().setFieldValue("policyNotes", noteB);
                }else{
                    filterRs.getFirstRecord().setFieldValue("policyNotes", "");
                }
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load policy data.", e);
            l.throwing(getClass().getName(), "loadPolicyDetail", ae);
            throw ae;
        }

        try {
            //Handle miscellaneous field setting
            outputRecord = filterRs.getFirstRecord();
            String termEffectiveFrom = PolicyFields.getTermEffFromDate(outputRecord);
            String termEffectiveTo = PolicyFields.getTermEffToDate(outputRecord);
            outputRecord.setFieldValue("numTermDays", String.valueOf(DateUtils.daysDiff(termEffectiveFrom, termEffectiveTo)));

            // add the sumamry record to it : the summary record contains pageEntitlement information
            outputRecord.setFields(rs.getSummaryRecord(), true);

            //Handle OOS WIP field setting when Change option is invoked
            if(policyHeader.getScreenModeCode().isOosWIP()){
                // Set ooseTermExpDate to default value based upon PD and store original value
                String ooseTermExpDate = getTransactionManager().getOoseExpirationDate(policyHeader);
                PolicyFields.setOoseTermExpDate(outputRecord, ooseTermExpDate);
            }
            if (policyHeader.getScreenModeCode().isOosWIP() &&
                inputRecord.hasStringValue("ooseChangeB") &&
                YesNoFlag.getInstance(inputRecord.getStringValue("ooseChangeB")).booleanValue()) {

                // Set the record mode to REQUEST
                PMCommonFields.setRecordModeCode(outputRecord, RecordMode.REQUEST);

                // Set Change option to invisible for the new outputRecord
                outputRecord.setFieldValue("isOosChangeAvailable", YesNoFlag.N);

                // Set Delete option to visible for the new outputRecord
                outputRecord.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load policy detail data.", e);
            l.throwing(getClass().getName(), "loadPolicyDetail", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadPolicyDetail", outputRecord);

        return outputRecord;
    }


    /**
     * Method to determine if an OOSE policy change has occurred and if so
     * call the database object to remove it, otherwise simply refreshing the page.
     *
     * @param policyHeader instance of the PolicyHeader object with current term/transaction data
     * @param inputRecord  record with current policy data from the page
     */
    public void deleteOosPolicyDetail(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "deleteOosPolicyDetail", new Object[]{inputRecord});

        // Determine the record mode code
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(inputRecord);

        // If record mode code is REQUEST, the change has not been saved
        // so do nothing and return to refresh the page.  Otherwise call
        // the delete routine and then refresh the page.
        if (!recordModeCode.isRequest()) {
            getPolicyDAO().deleteOosPolicyDetail(inputRecord);

            // force the policy header to reload since the term id will be removed
            // and remove it from the request storage manager since the term data
            // will not be refreshed otherwise
            policyHeader.setReloadCode(PolicyHeaderReloadCode.CURRENT_TERM);
            RequestStorageManager.getInstance().remove((RequestStorageIds.POLICY_HEADER));
        }

        l.exiting(getClass().getName(), "deleteOosPolicyDetail");
    }

    /**
     * Check if record exist.
     *
     * @param inputRecord a Record with query conditions
     * @return YesNoFlag
     */
    public YesNoFlag isRecordExist(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRecordExist", new Object[]{inputRecord});
        }

        YesNoFlag isEditable = YesNoFlag.getInstance(getPolicyDAO().isRecordExist(inputRecord));

        l.exiting(getClass().getName(), "isRecordExist", isEditable);
        return isEditable;
    }

    /*
    * Load policy summary for one client
    *
    * @param inputRecord  input record that contains entity id.
    * @return policy summary
    */
    public RecordSet loadAllPolicySummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicySummary", new Object[]{inputRecord});
        }
        RecordSet outRecordSet = getPolicyDAO().loadAllPolicySummary(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPolicySummary", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Load related Endorsment/Renewal Quote of policy
     *
     * @param policyHeader contains termBaseRecordId.
     * @return quote list
     */
    public RecordSet loadAllEndorsementQuote(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEndorsementQuote", new Object[]{policyHeader});
        }
        Record inputRecord = new Record();
        inputRecord.setFieldValue("termBaseRecordId", policyHeader.getTermBaseRecordId());
        RecordSet outRecordSet = getPolicyDAO().loadAllEndorsementQuote(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEndorsementQuote", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Method that evaluates policy business rule for ability to edit the policy number,
     * updating the OasisField to editable if permitted.
     *
     * @param termRec Record of current policy data that was retrieved for display
     * @return boolean indicating if the policy no field is editable
     */
    public boolean isPolicyNoEditable(Record termRec) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPolicyNoEditable", new Object[]{termRec});
        }

        boolean isEditAllowed = false;

        try {
            // First determine if the policy term is initially being created
            if (PolicyHeaderFields.getPolicyStatus(termRec).isPending()) {
                // Term is eligible, so check the user profile
                isEditAllowed = UserSessionManager.getInstance().getUserSession().getOasisUser().hasProfile(EDIT_POLICYNO_USER_PROFILE);
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if policy no is editable.", e);
            l.throwing(getClass().getName(), "isPolicyNoEditable", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isPolicyNoEditable", Boolean.valueOf(isEditAllowed));
        return isEditAllowed;
    }

    /**
     * Method that evaluates policy business rule for ability to edit the renewal indicator,
     * updating the OasisField to editable if permitted.
     *
     * @param termRec Record of current policy data that was retrieved for display
     * @return boolean indicating if the renewal indicator field is editable
     */
    public boolean isRenewalIndicatorEditable(Record termRec) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRenewalIndicatorEditable", new Object[]{termRec});
        }

        boolean isEditAllowed = true;

        try {
            // First determine if the term is short rated
            if (termRec.getBooleanValue("shortTermB").booleanValue()) {
                isEditAllowed = false;
            }

            l.logp(Level.FINE, getClass().getName(), "isRenewalIndicatorEditable", "Renewal indicator is editable: " + isEditAllowed);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if renewal indicator is editable.", e);
            l.throwing(getClass().getName(), "isRenewalIndicatorEditable", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isRenewalIndicatorEditable", Boolean.valueOf(isEditAllowed));
        return isEditAllowed;
    }


    /**
     * Method that evaluates policy business rule for ability to edit the program code,
     * updating the OasisField to editable if permitted.
     *
     * @param termRec Record of current policy data that was retrieved for display
     * @return boolean indicating if the program code field is editable
     */
    public boolean isProgramCodeEditable(Record termRec) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isProgramCodeEditable", new Object[]{termRec});
        }

        boolean isEditAllowed = true;

        try {
            // First determine if the policy term is initially being created
            if (!PolicyHeaderFields.getPolicyStatus(termRec).isPending()) {
                isEditAllowed = false;
            }

            l.logp(Level.FINE, getClass().getName(), "isProgramCodeEditable", "Program Code is editable: " + isEditAllowed);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if program code is editable.", e);
            l.throwing(getClass().getName(), "isProgramCodeEditable", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isProgramCodeEditable", Boolean.valueOf(isEditAllowed));
        return isEditAllowed;
    }

    /**
     * Method that evaluates policy business rule for ability to edit the process location,
     * updating the OasisField to editable if permitted.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return boolean indicating if the process location field is editable
     */
    public boolean isProcessLocationEditable(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isProcessLocationEditable", new Object[]{policyHeader});
        }

        boolean isEditAllowed = false;

        try {
            // Editable if currently in WIP mode and the transaction effective date
            // equals the current term effective date
            if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isWIP()) {
                if (policyHeader.getTermEffectiveFromDate().equalsIgnoreCase(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate())) {
                    isEditAllowed = true;
                }
            }

            l.logp(Level.FINE, getClass().getName(), "isProcessLocationEditable", "Process Location is editable: " + isEditAllowed);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if process location is editable.", e);
            l.throwing(getClass().getName(), "isProcessLocationEditable", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isProcessLocationEditable", Boolean.valueOf(isEditAllowed));
        return isEditAllowed;
    }

    /**
     * Method that evaluates policy business rule for ability to edit the IBNR date,
     * updating the OasisField to editable if permitted.
     *
     * @return boolean indicating if the rolling inbr date field is editable
     */
    public boolean isIbnrDateEditable() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isIbnrDateEditable");
        }

        boolean isEditAllowed = false;

        try {
            // Check the user profile
            isEditAllowed = UserSessionManager.getInstance().getUserSession().getOasisUser().hasProfile(EDIT_IBNR_ROLLING_DATE);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if the IBNR date is editable.", e);
            l.throwing(getClass().getName(), "isIbnrDateEditable", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isIbnrDateEditable", Boolean.valueOf(isEditAllowed));
        return isEditAllowed;
    }

    /**
     * Check if Program Retro Date is editable or not.
     * @param policyHeader
     * @return
     */
    public YesNoFlag isPolicyRetroDateEditable(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPolicyRetroDateEditable");
        }

        YesNoFlag isEditable = YesNoFlag.Y;
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        if (!screenMode.isManualEntry() &&
            YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_EDIT_POLICY_RETRO)).booleanValue()) {
            boolean hasProfile = false;
            try {
                hasProfile = UserSessionManager.getInstance().getUserSession().getOasisUser().hasProfile(UserProfiles.PM_EDIT_POLICY_RETRO);
            }
            catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to get PM_EDIT_POLICY_RETRO profile", e);
                l.throwing(getClass().getName(), "isPolicyRetroDateEditable", ae);
                throw ae;
            }
            if (!hasProfile) {
                Record record = new Record();
                PolicyFields.setPolicyId(record, policyHeader.getPolicyIdentifier().getPolicyId());
                TransactionFields.setTransactionLogId(record, policyHeader.getLastTransactionId());
                isEditable = YesNoFlag.getInstance(getPolicyDAO().isPolicyRetroDateEditable(record));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPolicyRetroDateEditable", isEditable);
        }
        return isEditable;
    }

    /**
     * Wrapper to invoke the save of the updated Policy record and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader the summary policy information corresponding to the provided policy.
     * @param inputRecord  a set of Records, each with the updated Policy Detail info
     *                     matching the fields returned from the loadAllPolicy method.
     * @return the number of rows processed.
     */
    public int processSavePolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processSavePolicy", new Object[]{inputRecord});

        int processCount = processSavePolicyData(policyHeader, inputRecord, false);

        l.exiting(getClass().getName(), "processSavePolicy", new Integer(processCount));
        return processCount;
    }

    /**
     * Wrapper to auto invoke save of the updated Policy record and subsequently
     * to invoke the save transaction logic for WIP only.
     *
     * @param policyHeader the summary policy information corresponding to the provided policy.
     * @param inputRecord  a set of Records, each with the updated Policy Detail info
     *                     matching the fields returned from the loadAllPolicy method.
     * @return the number of rows processed.
     */
    public int processAutoSavePolicyWIP(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processAutoSavePolicyWIP", new Object[]{inputRecord});

        int processCount = processSavePolicyData(policyHeader, inputRecord, true);

        l.exiting(getClass().getName(), "processAutoSavePolicyWIP", new Integer(processCount));
        return processCount;
    }

    /**
     * Wrapper to invoke the save of the updated Policy record and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader the summary policy information corresponding to the provided policy.
     * @param inputRecord  a set of Records, each with the updated Policy Detail info
     *                     matching the fields returned from the loadAllPolicy method.
     * @param isAutoSave  a indicator to check it is auto save or not
     * @return the number of rows processed.
     */
    protected int processSavePolicyData(PolicyHeader policyHeader, Record inputRecord, boolean isAutoSave) {
        Logger l = LogUtils.enterLog(getClass(), "processSavePolicyData", new Object[]{inputRecord});

        int processCount = 0;

        // UpperCase the policyNoEdit after the field is changed.
        String policyNoEdit = inputRecord.getStringValue("policyNoEdit").toUpperCase();
        inputRecord.setFieldValue("policyNoEdit", policyNoEdit);

        // Fix issue 101945. If the changeB doesn't exist in inputRecord, system should default it true(remain original logic).
        boolean changeB = true;
        if (inputRecord.hasStringValue("changeB") && !inputRecord.getBooleanValue("changeB").booleanValue()) {
            changeB = false;
        }
        // System invokes processSavePolicy to save policy if the policy has been changed.
        if (changeB) {
            PolicySaveProcessor saveProcessor = (PolicySaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
            processCount = saveProcessor.savePolicy(policyHeader, inputRecord);
        }

        // Complete the save action via the TransactionManager
        // if it is auto save, no need the do transaction process
        if (!isAutoSave) {
            inputRecord.setFields(policyHeader.toRecord(), false);
            inputRecord.setFieldValue("level", "POLICY");
            getTransactionManager().processSaveTransaction(policyHeader, inputRecord);
        }

        l.exiting(getClass().getName(), "processSavePolicyData", new Integer(processCount));
        return processCount;
    }

    /**
     * Saves the input record based upon record mode code.
     *
     * @param policyHeader the summary policy information corresponding to the provided policy data.
     * @param inputRecord  a set of Records, each with the updated Policy Detail info
     *                     matching the fields returned from the loadAllPolicy method.
     * @return the number of rows updated.
     */
    public int savePolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "savePolicy", new Object[]{inputRecord});

        int updateCount = 0;

        // Add the PolicyHeader info to the Policy detail Record
        inputRecord.setFields(policyHeader.toRecord(), false);

        // Add the Transaction information to the Policy detail Record
        inputRecord.setFieldValue("transCode", policyHeader.getLastTransactionInfo().getTransactionCode());

        // Validate the input policy record prior to saving
        validatePolicy(policyHeader, inputRecord);

        // Determine the record mode code to call the correct stored procedure
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(inputRecord);

        if (inputRecord.hasField(PolicyFields.ORG_HIER_ID)) {
            policyHeader.setOrgHierRootId(PolicyFields.getOrgHierId(inputRecord));
        }

        if (recordModeCode.isTemp()) {
            inputRecord.setFieldValue("rowStatus", "MODIFIED");
            updateCount = getPolicyDAO().addPolicy(inputRecord);
        }
        else {
            // For OOSWIP Request record use ooseTermExpDate field and
            // set the policyTermHistoryId to a new oasis sequence value
            if (recordModeCode.isRequest()) {
                inputRecord.setFieldValue(PolicyFields.TERM_EFFECTIVE_TO_DATE, PolicyFields.getOoseTermExpDate(inputRecord));
                inputRecord.setFieldValue(PolicyHeaderFields.POLICY_TERM_HISTORY_ID, getDbUtilityManager().getNextSequenceNo());
            }

            // Perform the endorsement of Policy Term
            updateCount = getPolicyDAO().updatePolicy(inputRecord);

            // Update Policy table
            updateCount += getPolicyDAO().changePolicy(inputRecord);
        }

        // Reset the policy no in case the user changed it
        policyHeader.setPolicyNo(PolicyHeaderFields.getPolicyNo(inputRecord));

        l.exiting(getClass().getName(), "savePolicy", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Validates that the modified policy no is available.
     *
     * @param modifiedPolicyNo the user modified data value.
     * @param policyId         primary key value of the policy record being changed.
     */
    public void validateModifiedPolicyNo(String modifiedPolicyNo, String policyId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateModifiedPolicyNo", new Object[]{modifiedPolicyNo, policyId});
        }
        // UpperCase the modifiedPolicyNo.
        modifiedPolicyNo = modifiedPolicyNo.toUpperCase();
        String validationMsg = getPolicyDAO().validateModifiedPolicyNo(modifiedPolicyNo, policyId);
        if (!StringUtils.isBlank(validationMsg)) {
            MessageManager.getInstance().addErrorMessage(validationMsg, new Object[]{modifiedPolicyNo});
            throw new ValidationException();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateModifiedPolicyNo");
        }
    }

    protected void validatePolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePolicy", new Object[]{policyHeader, inputRecord});
        }

        // Business Rule 1:  Validate Rolling INBR date change
        if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_DEF_RETRO_TOIBNR", "N")).booleanValue() &&
            !PolicyFields.getRollingIbnrDate(inputRecord).equalsIgnoreCase(PolicyFields.getOriginalRollingIbnrDate(inputRecord))) {
            // if the system parameter allows the IBNR date to be modified, ensure that it is only
            // modified by a transaction effective on the policy term effective date

            if (!getPolicyDAO().canRollingIbnrDateChange(inputRecord)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPolicy.invalidIbnrChange.error",
                    PolicyFields.ROLLING_IBNR_DATE);
                throw new ValidationException("Invalid IBNR transaction effective date.");
            }
        }

        // Business Rule 2A/B:  Validate OOS Term Exp date for OOSWIP is enter and a valid
        // term expiration date
        if (policyHeader.getScreenModeCode().isOosWIP()) {

            // Is the value of oose term expiration date not null
            if (!inputRecord.hasStringValue(PolicyFields.OOSE_TERM_EXP_DATE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPolicy.missingOoseTermExpDate.error",
                    PolicyFields.OOSE_TERM_EXP_DATE);
                //if the oose term expiration date is null, reset the original value back
                String ooseTermExpDate = getTransactionManager().getOoseExpirationDate(policyHeader);
                PolicyFields.setOoseTermExpDate(inputRecord, ooseTermExpDate);

                throw new ValidationException("Missing oose term expiration date.");
            }

            if (!getPolicyDAO().validateOoseTermExpDate(inputRecord)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPolicy.invalidOoseTermExpDate.error",
                    PolicyFields.OOSE_TERM_EXP_DATE);
                throw new ValidationException("Invalid oose term expiration date.");
            }
        }

        l.exiting(getClass().getName(), "validatePolicy");
    }

    /**
     * Method that returns a boolean value, indicating whether a reload of policy header is required.
     *
     * @param policyNo              policy number
     * @param policyTermHistoryId   policy term history id
     * @param policyHeader          sent in as null, but loads Policy Header from cache, if found
     * @param desiredPolicyViewMode policy view mode
     * @param endQuoteId            endorsement quote id
     * @return true, if a reload is required; otherwise, false.
     */
    private boolean isReloadPageHeader(String policyNo, String policyTermHistoryId, PolicyHeader policyHeader, PolicyViewMode desiredPolicyViewMode, String endQuoteId) {
        Logger l = LogUtils.enterLog(getClass(), "isReloadPageHeader", new Object[]{policyNo, policyTermHistoryId, policyHeader, desiredPolicyViewMode});
        boolean isPageHeaderReloadRequired = true;
        try {
            if (getUserSessionManager().getUserSession().has(UserSessionIds.POLICY_HEADER)) {
                l.logp(Level.FINE, getClass().getName(), "loadPolicyHeader", "Loading Policy Header From Session...");

                // There is already a Policy Header cached in the UserSession.
                policyHeader = (PolicyHeader) getUserSessionManager().getUserSession().get(UserSessionIds.POLICY_HEADER);

                // Check if there is specific need to reload
                if (!policyHeader.isReloadRequired() &&
                    // Check if it is the latest info for the requested policy number
                    policyNo.equalsIgnoreCase(policyHeader.getPolicyNo()) &&
                    // Check if the official number and wip number are the latest
                    !getPolicyDAO().isPolicyPictureChanged(policyHeader.getPolicyIdentifier()) &&
                    // If the TermBaseRecordId is not specified reload the header to be safe
                    // This is for cases where the term dates can change.
                    // Previously we defaulted to the current term id
                    // policyTermHistoryId = policyHeader.getPolicyTermHistoryId();
                    !StringUtils.isBlank(policyTermHistoryId) &&
                    // Check if the term matches or else we need to reload
                    policyTermHistoryId.equalsIgnoreCase(policyHeader.getPolicyTermHistoryId()) &&
                    // We have the same term, now verify if the view mode is the same
                    desiredPolicyViewMode.equals(policyHeader.getPolicyIdentifier().getPolicyViewMode())&&
                    // If the endQuoteId is provided, Verify it has not changed
                    (StringUtils.isBlank(endQuoteId)||"null".equalsIgnoreCase(endQuoteId)||endQuoteId.equals(policyHeader.getLastTransactionInfo().getEndorsementQuoteId()))
                    ) {
                    // Everything is identical no reload is necessary
                    isPageHeaderReloadRequired = false;
                    getUserSessionManager().getUserSession().set(UserSessionIds.POLICY_HEADER, policyHeader);
                }
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load policy header information from cache", e);
            l.throwing(getClass().getName(), "loadPolicyHeader", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "isReloadPageHeader", String.valueOf(isPageHeaderReloadRequired));
        return isPageHeaderReloadRequired;
    }

    /**
     * Method that gets the default values
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord Record that contains input parameters
     * @return Record that contains default values
     */
    public Record getInitialValuesForCreatePolicy(Record inputRecord) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForCreatePolicy",
            new Object[]{inputRecord});

        // Get the default values from the workbench configuration for the page corresponding to creating a new policy
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(CREATE_POLICY_ACTION_CLASS_NAME);

        // add configuration defaults to the output record
        Record outputRecord = new Record();
        outputRecord.setFields(defaultValuesRecord);

        // set input values to the output record
        outputRecord.setFields(inputRecord);

        // set other initial values to the output record
        try {
            SysParmProvider sysParmProvider = SysParmProvider.getInstance();
            PolicyDAO policyDAO = getPolicyDAO();

            // check the required policyholder entity FK
            if (!outputRecord.hasStringValue(CreatePolicyFields.POLICY_HOLDER_NAME_ENTITY_ID)) {
                AppException ae = new AppException("Policyholder entity ID is missing.");
                l.throwing(getClass().getName(), "getInitialValuesForCreatePolicy", ae);
                throw ae;
            }
            String entityId = CreatePolicyFields.getPolicyHolderNameEntityId(outputRecord);

            // set request context if it's not passed in
            if (!outputRecord.hasStringValue(CreatePolicyFields.REQUEST_CONTEXT))
                CreatePolicyFields.setRequestContext(outputRecord, "PM");

            // set policy cycle if it's not passed in
            if (!outputRecord.hasStringValue(CreatePolicyFields.POLICY_CYCLE_CODE))
                CreatePolicyFields.setPolicyCycleCode(outputRecord, PolicyCycleCode.POLICY);

            // set policyholder entity type
            CreatePolicyFields.setPolicyHolderEntityType(outputRecord, getEntityManager().getEntityType(entityId));

            if (CreatePolicyFields.getRequestContext(outputRecord).equals("CM")) {
                // set term effective date if coming from CM and the system parameter is on
                String termExpDate = CreatePolicyFields.getTermEffectiveToDate(outputRecord);

                if (!StringUtils.isBlank(termExpDate) &&
                    YesNoFlag.getInstance(sysParmProvider.getSysParm("PM_CM_DEF_COPY_DTS", "N")).booleanValue()) {
                    String termEffDate = "/" + String.valueOf(Integer.parseInt(termExpDate.substring(6)) - 1);
                    if (termExpDate.substring(0, 2).equals("01"))
                        termEffDate = "12/" + termExpDate.substring(3, 5) + termEffDate;
                    else
                        termEffDate = "12/31" + termEffDate;
                    CreatePolicyFields.setTermEffectiveFromDate(outputRecord, termEffDate);
                }

                // set user transaction code
                CreatePolicyFields.setUserTransactionCode(outputRecord, "RENEW");
                CreatePolicyFields.setIsUserTransactionCodeAvailable(outputRecord, YesNoFlag.Y);
            }
            else {
                // set issue company if not coming from CM
                if (!outputRecord.hasStringValue(CreatePolicyFields.ISSUE_COMPANY_ENTITY_ID)) {
                    CreatePolicyFields.setIssueCompanyEntityId(outputRecord, sysParmProvider.getSysParm("ISSUE_COMPANY"));
                }

                // set issue state if not coming from CM
                if (!outputRecord.hasStringValue(CreatePolicyFields.ISSUE_STATE_CODE)) {
                    CreatePolicyFields.setIssueStateCode(outputRecord, sysParmProvider.getSysParm("DEFAULT_CLIENT_STATE"));
                }

                // leave the term effective dates blank if not coming from CM
                CreatePolicyFields.setTermEffectiveFromDate(outputRecord, "");
                CreatePolicyFields.setTermEffectiveToDate(outputRecord, "");

                // set user transaction code
                CreatePolicyFields.setUserTransactionCode(outputRecord, "NEW");
                if (PolicyFields.getPolicyCycleCode(outputRecord).isQuote())
                    CreatePolicyFields.setIsUserTransactionCodeAvailable(outputRecord, YesNoFlag.N);
                else
                    CreatePolicyFields.setIsUserTransactionCodeAvailable(outputRecord, YesNoFlag.Y);
            }

            // set term expiration date indicators
            CreatePolicyFields.setIsTermEffectiveToDateChanged(outputRecord, YesNoFlag.N);

            // set accounting date
            CreatePolicyFields.setAccountingDate(outputRecord, DateUtils.c_dateFormat.get().format(new Date()));

            // set practice state
            if (!outputRecord.hasStringValue(CreatePolicyFields.PRACTICE_STATE_CODE)) {
                CreatePolicyFields.setPracticeStateCode(outputRecord, getPmDefaultManager().getDefaultState("PRACTICE_STATE_CODE",
                    entityId, DateUtils.c_dateFormat.get().format(new Date())));
            }

            // set regional office
            if (!outputRecord.hasStringValue(CreatePolicyFields.REGIONAL_OFFICE)) {
                CreatePolicyFields.setRegionalOffice(outputRecord, policyDAO.getDefaultRegionalOffice(outputRecord));
            }

            // set short rate indicator
            CreatePolicyFields.setShortRateB(outputRecord, YesNoFlag.N);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load the initial values for create policy. ", e);
            l.throwing(getClass().getName(), "getInitialValuesForCreatePolicy", ae);
            throw ae;
        }

        // done
        l.exiting(getClass().getName(), "getInitialValuesForCreatePolicy", outputRecord);
        return outputRecord;
    }

    /**
     * Method that validates field that is being changed and
     * gets new default values
     * <p/>
     *
     * @param inputRecord Record that contains input values
     * @return Record that contains new default values
     */
    public Record handleFieldChangeForCreatePolicy(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "handleFieldChangeForCreatePolicy", new Object[]{inputRecord});

        // validate input
        validateFieldForCreatePolicy(inputRecord, inputRecord.getStringValue("fieldName"));

        // reset default term effective date if all valid
        Record outputRecord = new Record();
        outputRecord.setFieldValue(CreatePolicyFields.TERM_EFFECTIVE_TO_DATE,
            getDefaultTermExpirationDate(inputRecord));

        // done
        l.exiting(getClass().getName(), "handleFieldChangeForCreatePolicy", outputRecord);
        return outputRecord;
    }

    /**
     * Method that returns a RecordSet loaded with policy types
     * based on search criteria stored in the input Record.
     * <p/>
     *
     * @param inputRecord  Record that contains search criteria
     * @param doValidation TRUE if validation is required
     * @return RecordSet that contains policy types
     */
    public RecordSet findAllPolicyType(Record inputRecord, boolean doValidation) {
        Logger l = LogUtils.enterLog(getClass(), "findAllPolicyType",
            new Object[]{inputRecord, String.valueOf(doValidation)});
        RecordSet recordSet;

        // validate input if asked
        if (doValidation)
            validateForFindAllPolicyType(inputRecord);

        // create additional parms retrieval
        inputRecord.setFieldValue("addlParms", buildAddlParmsField(inputRecord));

        // get policy types
        recordSet = getPolicyDAO().findAllPolicyType(inputRecord);

        // done
        l.exiting(getClass().getName(), "findAllPolicyType", recordSet);
        return recordSet;
    }

    /**
     * Check policy existence
     *
     * @param inputRecord Record contains input values
     * @return String indicates if policy with the same policy typeexists
     */
    public String checkPolicyExistence(Record inputRecord) {
        return getPolicyDAO().checkPolicyExistence(inputRecord);
    }

    /**
     * Method that creates policy based on input Record.
     * <p/>
     *
     * @param inputRecord  Record that contains new policy information
     * @param doValidation TRUE if validation is required
     * @return String contains policy number
     */
    public String createPolicy(Record inputRecord, boolean doValidation) {
        Logger l = LogUtils.enterLog(getClass(), "createPolicy",
            new Object[]{inputRecord, String.valueOf(doValidation)});

        // validate input if asked
        if (doValidation)
            validatePolicyForCreate(inputRecord);

        // create policy
        Record outputRecord = getPolicyDAO().createPolicy(inputRecord);

        // process return code
        String policyNo;
        String returnCode = outputRecord.getStringValue("rtnCode");
        if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == 0) {
            // policy is created and get the policy no
            policyNo = outputRecord.getStringValue("policyNo");

            PolicyHeader policyHeader = loadPolicyHeader(policyNo, "createPolicy", "createPolicy");
            setDefaultFlags(inputRecord, policyHeader);

            if ( !policyHeader.isSkipDefaultRisk() ) {
                // default the primary risk for the newly created policy
                getRiskManager().saveDefaultRisk(policyHeader, inputRecord);
            }
        }
        else if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == 1) {
            String returnMsg = outputRecord.getStringValue("rtnMsg");
            AppException ae = new AppException("pm.createPolicy.policyNo.existing", returnMsg);
            l.throwing(getClass().getName(), "createPolicy", ae);
            throw ae;
        }
        else if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == 2) {
            String returnMsg = outputRecord.getStringValue("rtnMsg");
            AppException ae = new AppException("pm.createPolicy.policyNo.unavailable", returnMsg);
            l.throwing(getClass().getName(), "createPolicy", ae);
            throw ae;
        }
        else if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == 3) {
            String returnMsg = outputRecord.getStringValue("rtnMsg");
            AppException ae = new AppException("pm.createPolicy.pmTransactionCode.notExist", returnMsg);
            l.throwing(getClass().getName(), "createPolicy", ae);
            throw ae;
        }
        else {
            String returnMsg = outputRecord.getStringValue("rtnMsg");
            AppException ae = new AppException("pm.createPolicy.failed.error", returnMsg);
            l.throwing(getClass().getName(), "createPolicy", ae);
            throw ae;
        }

        // Fix 96889: Remove all the inforamtion message when create policy.
        if (MessageManager.getInstance().hasInfoMessages()) {
            Iterator it = MessageManager.getInstance().getInfoMessages();
            while (it.hasNext()) {
                // Must call the method next().
                it.next();
                it.remove();
            }
        }
        // done
        l.exiting(getClass().getName(), "createPolicy", policyNo);
        return policyNo;
    }

    /**
     * Method that generate policy number based on input Record.
     * <p/>
     *
     * @param inputRecord  Record that contains new policy number information
     * @return String contains policy number
     */
    public String generatePolicyNumberForWS(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "generatePolicyNumberForWS",
            new Object[]{inputRecord});

        Record outputRecord = getPolicyDAO().generatePolicyNumberForWS(inputRecord);

        String policyNo;
        String returnCode = outputRecord.getStringValue("returnCode");
        if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == 0) {
            // policy number is generated and get the policy no
            policyNo = outputRecord.getStringValue("policyNumber");
        }
        else if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == 1) {
            //Failed to generate policy number due to a policy with the same number already exists.
            String returnMsg = outputRecord.getStringValue("returnMsg");
            AppException ae = new AppException("ws.policyNumberGeneration.policyNumber.existing", returnMsg);
            l.throwing(getClass().getName(), "generatePolicyNumberForWS", ae);
            throw ae;
        }
        else if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == 2) {
            //Failed to generate policy number due to policy no is not available.
            String returnMsg = outputRecord.getStringValue("returnMsg");
            AppException ae = new AppException("ws.policyNumberGeneration.policyNumber.unavailable", returnMsg);
            l.throwing(getClass().getName(), "generatePolicyNumberForWS", ae);
            throw ae;
        }
        else if (StringUtils.isDecimal(returnCode) && Integer.parseInt(returnCode) == 3) {
            //Failed to generate policy number due to issue Company Identifier is invalid.
            String returnMsg = outputRecord.getStringValue("returnMsg");
            AppException ae = new AppException("ws.policyNumberGeneration.issueCompIdentifier.invalid", returnMsg);
            l.throwing(getClass().getName(), "generatePolicyNumberForWS", ae);
            throw ae;
        }
        else {
            //Failed to generate policy number due to other cause.
            String returnMsg = outputRecord.getStringValue("returnMsg");
            AppException ae = new AppException("ws.policyNumberGeneration.failed.error", returnMsg);
            l.throwing(getClass().getName(), "generatePolicyNumberForWS", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "generatePolicyNumberForWS", policyNo);
        return policyNo;
    }

    /**
     * Method that sets the default flags into the PolicyHeader
     * <p/>
     *
     * @param inputRecord Record that contains input values
     * @param policyHeader The policy header
     */
    public void setDefaultFlags(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "setDefaultFlags", new Object[]{inputRecord, policyHeader});

        if (inputRecord.hasField(RiskInquiryFields.SKIP_DEFAULT_INSURED_CREATION)) {
            policyHeader.setSkipDefaultRisk(true);
        }

        if (inputRecord.hasField(RiskInquiryFields.IS_SETUP_DEFAULT_RISK)
            && inputRecord.getStringValue(RiskInquiryFields.IS_SETUP_DEFAULT_RISK).equals("N")) {
            policyHeader.setSkipDefaultRisk(true);
        }

        if (inputRecord.hasField(CoverageInquiryFields.SKIP_DEFAULT_COVERAGE_CREATION)) {
            policyHeader.setSkipDefaultCoverage(true);
        }

        if (inputRecord.hasField(CoverageInquiryFields.SKIP_DEFAULT_SUB_COVERAGE_CREATION)) {
            policyHeader.setSkipDefaultSubCoverage(true);
        }

        if (inputRecord.hasField(ComponentInquiryFields.SKIP_DEFAULT_COMPONENT_CREATION)) {
            policyHeader.setSkipDefaultComponent(true);
        }

        l.exiting(getClass().getName(), "setDefaultFlags");
    }

    /**
     * Method that gets the default term expiration date
     * based on values stored in the input Record.
     * <p/>
     *
     * @param inputRecord Record that contains input values
     * @return Record that contains default term expiration date etc.
     */
    public String getDefaultTermExpirationDate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getDefaultTermExpirationDate", new Object[]{inputRecord});

        // get input
        String issueCompanyEntityId = CreatePolicyFields.getIssueCompanyEntityId(inputRecord);
        String issueStateCode = CreatePolicyFields.getIssueStateCode(inputRecord);
        String policyTypeCode = CreatePolicyFields.getPolicyTypeCode(inputRecord);
        String termEffectiveDate = CreatePolicyFields.getTermEffectiveFromDate(inputRecord);
        String termExpirationDate = CreatePolicyFields.getTermEffectiveToDate(inputRecord);
        YesNoFlag isTermExpirationDateChanged = CreatePolicyFields.getIsTermEffectiveToDateChanged(inputRecord);

        // get the default term expiration date if we have the minimum input
        if (!StringUtils.isBlank(issueCompanyEntityId) && !StringUtils.isBlank(issueStateCode) &&
            !StringUtils.isBlank(policyTypeCode) && !StringUtils.isBlank(termEffectiveDate)) {
            String returnValue = getPolicyDAO().getDefaultTermExpirationDate(inputRecord);
            String update = returnValue.substring(0, 1);
            String defaultTermExpirationDate = returnValue.substring(1);
            if (YesNoFlag.getInstance(update).booleanValue() ||
                !isTermExpirationDateChanged.booleanValue() ||
                StringUtils.isBlank(termExpirationDate))
                termExpirationDate = defaultTermExpirationDate;
        }

        // done
        l.exiting(getClass().getName(), "getDefaultTermExpirationDate", termExpirationDate);
        return termExpirationDate;
    }

    /**
     * Method that validates a given field
     * <p/>
     *
     * @param inputRecord Record that contains input values
     * @param fieldName   name of the field to validate
     */
    private void validateFieldForCreatePolicy(Record inputRecord, String fieldName) {
        Logger l = LogUtils.enterLog(getClass(), "validateFieldForCreatePolicy", new Object[]{inputRecord, fieldName});
        boolean isValid = true;
        String fieldValue = "";
        try {
            MessageManager messageMgr = MessageManager.getInstance();

            // check term effective date
            if (fieldName.equals(CreatePolicyFields.TERM_EFFECTIVE_FROM_DATE)) {
                String termEffectiveDate = CreatePolicyFields.getTermEffectiveFromDate(inputRecord);
                // check if the term effective date is null
                if (StringUtils.isBlank(termEffectiveDate)) {
                    isValid = false;
                    messageMgr.addErrorMessage("pm.createPolicy.termEffectiveFromDate.null.error",
                        CreatePolicyFields.TERM_EFFECTIVE_FROM_DATE);
                }
                else {
                    // get the number of days for which the term effective date is allowed to exceed the system date
                    String sysParm = SysParmProvider.getInstance().getSysParm("PM_NEW_BUS_EFF_DATE", "0");

                    // set to default value zero if it's not a number
                    if (!StringUtils.isNumeric(sysParm)) {
                        sysParm = "0";
                        l.warning("Syspam PM_NEW_BUS_EFF_DATE must be a number.");
                    }

                    // check the term effective date against the system date
                    String sysEffectiveDate = DateUtils.c_dateFormat.get().format(new Date());
                    if (DateUtils.daysDiff(sysEffectiveDate, termEffectiveDate) > Integer.parseInt(sysParm)) {
                        isValid = false;
                        messageMgr.addErrorMessage("pm.createPolicy.termEffectiveFromDate.invalid.error",
                            new Object[]{sysParm}, CreatePolicyFields.TERM_EFFECTIVE_FROM_DATE);
                    }
                    else {
                        // compare term effective and expiration dates
                        String termExpirationDate = CreatePolicyFields.getTermEffectiveToDate(inputRecord);
                        if (!StringUtils.isBlank(termEffectiveDate) && !StringUtils.isBlank(termExpirationDate)) {
                            if (DateUtils.daysDiff(termEffectiveDate, termExpirationDate) <= 0) {
                                isValid = false;
                                messageMgr.addErrorMessage("pm.createPolicy.termEffectiveFromToDate.invalid.error",
                                    CreatePolicyFields.TERM_EFFECTIVE_FROM_DATE);
                            }
                        }
                    }
                }
            }

            // check term expiration date
            else if (fieldName.equals(CreatePolicyFields.TERM_EFFECTIVE_TO_DATE)) {
                String termExpirationDate = CreatePolicyFields.getTermEffectiveToDate(inputRecord);
                // compare term effective and expiration dates
                if (!StringUtils.isBlank(termExpirationDate)) {
                    String termEffectiveDate = CreatePolicyFields.getTermEffectiveFromDate(inputRecord);
                    if (!StringUtils.isBlank(termEffectiveDate) && !StringUtils.isBlank(termExpirationDate)) {
                        if (DateUtils.daysDiff(termEffectiveDate, termExpirationDate) <= 0) {
                            isValid = false;
                            messageMgr.addErrorMessage("pm.createPolicy.termEffectiveFromToDate.invalid.error",
                                CreatePolicyFields.TERM_EFFECTIVE_TO_DATE);
                        }
                    }
                }
            }

            // check accounting date
            else if (fieldName.equals(CreatePolicyFields.ACCOUNTTING_DATE)) {
                String accountingDate = CreatePolicyFields.getAccountingDate(inputRecord);
                // check if the accounting date is null
                if (StringUtils.isBlank(accountingDate)) {
                    isValid = false;
                    messageMgr.addErrorMessage("pm.createPolicy.accountingDate.null.error",
                        CreatePolicyFields.ACCOUNTTING_DATE);
                }
                else {
                    // check if accounting month is valid
                    isValid = new AccountingMonthRecordValidator().validate(inputRecord);
                }
            }

            // check issue company
            else if (fieldName.equals(CreatePolicyFields.ISSUE_COMPANY_ENTITY_ID)) {
                if (StringUtils.isBlank(CreatePolicyFields.getIssueCompanyEntityId(inputRecord))) {
                    isValid = false;
                    messageMgr.addErrorMessage("pm.createPolicy.issueComp.null.error",
                        CreatePolicyFields.ISSUE_COMPANY_ENTITY_ID);
                }
            }

            // check issue state
            else if (fieldName.equals(CreatePolicyFields.ISSUE_STATE_CODE)) {
                if (StringUtils.isBlank(CreatePolicyFields.getIssueStateCode(inputRecord))) {
                    isValid = false;
                    messageMgr.addErrorMessage("pm.createPolicy.issueState.null.error",
                        CreatePolicyFields.ISSUE_STATE_CODE);
                }
            }

            // check reginal office
            else if (fieldName.equals(CreatePolicyFields.REGIONAL_OFFICE)) {
                if (YesNoFlag.getInstance(inputRecord.getStringValue(CreatePolicyFields.REGIONAL_OFFICE + "IsVisible")).booleanValue() &&
                    !inputRecord.hasStringValue(CreatePolicyFields.REGIONAL_OFFICE)) {
                    isValid = false;
                    messageMgr.addErrorMessage("pm.createPolicy.regionlOffice.null.error",
                        CreatePolicyFields.REGIONAL_OFFICE);
                }
            }

            // check policy type
            else if (fieldName.equals(CreatePolicyFields.POLICY_TYPE_CODE)) {
                String policyTypeCode = CreatePolicyFields.getPolicyTypeCode(inputRecord);
                // check if policy type is null
                if (StringUtils.isBlank(policyTypeCode)) {
                    isValid = false;
                    messageMgr.addErrorMessage("pm.createPolicy.policyType.null.error");
                }
                else {
                    // check is policy type is valid
                    isValid = new ValidTermDurationRecordValidator().validate(inputRecord);
                }
            }
        }
        catch (ParseException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate field value for create policy. ", e);
            l.throwing(getClass().getName(), "validateFieldForCreatePolicy", ae);
            throw ae;
        }

        // done
        if (!isValid) {
            ValidationException ve = new ValidationException();
            Record validFields = new Record();
            validFields.setFieldValue(fieldName, fieldValue);
            ve.setValidFields(validFields);
            throw ve;
        }
        l.exiting(getClass().getName(), "validateFieldForCreatePolicy");
    }

    /**
     * Method that validates the search criteria stored in the input Record
     * before search for policy types.
     * <p/>
     *
     * @param inputRecord Record that contains search criteria
     */
    private void validateForFindAllPolicyType(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateForFindAllPolicyType", new Object[]{inputRecord});
        boolean isValid = true;

        // check term effective date
        try {
            validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.TERM_EFFECTIVE_FROM_DATE);
        }
        catch (ValidationException e) {
            isValid = false;
        }

        // check term expiration date
        try {
            validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.TERM_EFFECTIVE_TO_DATE);
        }
        catch (ValidationException e) {
            isValid = false;
        }

        // check issue company
        try {
            validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.ISSUE_COMPANY_ENTITY_ID);
        }
        catch (ValidationException e) {
            isValid = false;
        }

        // check issue state
        try {
            validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.ISSUE_STATE_CODE);
        }
        catch (ValidationException e) {
            isValid = false;
        }

        // check regional office
        try {
            validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.REGIONAL_OFFICE);
        }
        catch (ValidationException e) {
            isValid = false;
        }

        // throw validation exception if data is invalid
        if (!isValid)
            throw new ValidationException();
        l.exiting(getClass().getName(), "validateForFindAllPolicyType");
    }

    /**
     * Method that validates data before creates policy.
     * <p/>
     *
     * @param inputRecord Record that contains new policy information
     */
    public void validatePolicyForCreate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validatePolicyForCreate", new Object[]{inputRecord});
        boolean isValid = true;

        // check term effective date
        try {
            validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.TERM_EFFECTIVE_FROM_DATE);
        }
        catch (ValidationException e) {
            isValid = false;
        }

        // check term expiration date
        try {
            validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.TERM_EFFECTIVE_TO_DATE);
        }
        catch (ValidationException e) {
            isValid = false;
        }

        // check issue company
        try {
            validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.ISSUE_COMPANY_ENTITY_ID);
        }
        catch (ValidationException e) {
            isValid = false;
        }

        // check issue state
        try {
            validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.ISSUE_STATE_CODE);
        }
        catch (ValidationException e) {
            isValid = false;
        }

        // check regional office
        try {
            validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.REGIONAL_OFFICE);
        }
        catch (ValidationException e) {
            isValid = false;
        }

        // check policy type if other fields are valid
        if (isValid) {
            try {
                validateFieldForCreatePolicy(inputRecord, CreatePolicyFields.POLICY_TYPE_CODE);
            }
            catch (ValidationException e) {
                isValid = false;
            }
        }

        // throw validation exception if data is invalid
        if (!isValid)
            throw new ValidationException();

        // check existing policy
        SimilarPolicyRecordValidator similarPolicyValidator = new SimilarPolicyRecordValidator();
        boolean policyExistPropmt = similarPolicyValidator.validate(inputRecord);

        // check Short Rate indicator
        String messageKey = "pm.createPolicy.confirm.shortRate";
        boolean shortRatePrompt = false;
        if (!ConfirmationFields.isConfirmed(messageKey, inputRecord)) {
            String sysShortRate = SysParmProvider.getInstance().getSysParm("PM_CHK_COM_SR_DATES", "N");
            YesNoFlag shortRateB = CreatePolicyFields.getShortRateB(inputRecord);
            String termTypeCode = CreatePolicyFields.getTermTypeCode(inputRecord);
            String termExpirationMon = CreatePolicyFields.getTermEffectiveToDate(inputRecord).substring(0, 2);
            shortRatePrompt = YesNoFlag.getInstance(sysShortRate).booleanValue() &&
                !shortRateB.booleanValue() &&
                termTypeCode.equals("COMMON") && !termExpirationMon.equals("07");
            if (shortRatePrompt) {
                MessageManager.getInstance().addConfirmationPrompt(messageKey);
            }
        }

        // prompy for user input first
        if (policyExistPropmt) {
            throw new ValidationException("Policy exists already.");
        }

        if (shortRatePrompt) {
            throw new ValidationException();
        }


        l.exiting(getClass().getName(), "validatePolicyForCreate");
    }


    /**
     * Deny quote
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void denyQuote(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "denyQuote", new Object[]{policyHeader, inputRecord});

        Record outputRecord;
        //valid renewal infos,before renew a policy
        validateQuoteForDeny(policyHeader, inputRecord);

        //set field values
        inputRecord.setFields(policyHeader.toRecord(), false);

        // call dao method to deny quote,if deny quote fails throw Exception
        outputRecord = getPolicyDAO().denyQuote(inputRecord);

        l.exiting(getClass().getName(), "denyQuote", outputRecord);
    }

    /**
     * validate the input reocrd for deny quote
     *
     * @param policyHeader
     * @param inputRecord  for deny quote
     */
    protected void validateQuoteForDeny(PolicyHeader policyHeader, Record inputRecord) {

        Logger l = LogUtils.enterLog(getClass(), "validateDenyQuote", new Object[]{policyHeader, inputRecord});

        //#1 deny eff date is required
        if (!inputRecord.hasStringValue(DenyQuoteFields.DENY_EFF_DATE)) {
            MessageManager.getInstance().addErrorMessage("pm.maintainQuote.deny.effDateRequired.error",
                DenyQuoteFields.DENY_EFF_DATE);
        }
        else {
            //#2 deny eff date must between term_eff and term_exp
            Date denyEffDate = DateUtils.parseDate(DenyQuoteFields.getDenyEffDate(inputRecord));
            Date termEffDate = DateUtils.parseDate(policyHeader.getTermEffectiveFromDate());
            Date termExpDate = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());
            if (denyEffDate.after(termExpDate) || denyEffDate.before(termEffDate)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainQuote.deny.effDateRange.error",
                    new String[]{
                        FormatUtils.formatDateForDisplay(termEffDate),
                        FormatUtils.formatDateForDisplay(termExpDate)},
                    DenyQuoteFields.DENY_EFF_DATE);
            }

        }
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("invalid Deny Quote data");

        l.exiting(getClass().getName(), "validateDenyQuote");
    }

    /**
     * Reactive quote
     *
     * @param policyHeader
     */
    public void reactiveQuote(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "reactiveQuote", policyHeader);

        Record inputRecord = policyHeader.toRecord();
        // call dao method to reactive policy
        Record outputRecord = getPolicyDAO().reactiveQuote(inputRecord);

        //if there is error while reactive , save error message to MessageManager
        String rc = outputRecord.getStringValue("rc");
        if (!StringUtils.isBlank(rc) && Integer.parseInt(rc) < 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainQuote.reactive.error");
            throw new ValidationException("reactive quote failed.");
        }

        l.exiting(getClass().getName(), "reactiveQuote", outputRecord);
    }


    /**
     * get initial values for  Deny Data
     * sets effective date to current term effective from date
     *
     * @param policyHeader
     * @return the return record of PRT
     */
    public Record getInitialValuesForDenyQuote(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForDenyData", policyHeader);

        Record outputRecord = policyHeader.toRecord();
        DenyQuoteFields.setDenyEffDate(outputRecord, policyHeader.getTermEffectiveFromDate());

        l.exiting(getClass().getName(), "getInitialValuesForDenyData", outputRecord);
        return outputRecord;
    }

    /**
     * Deny quote
     *
     * @param policyHeader
     * @param inputRecord  with copyQuote infomations
     * @return result record which contains copied quote #
     */
    public Record copyQuote(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "copyQuote", new Object[]{policyHeader, inputRecord});

        Record outputRecord;
        //set field values
        inputRecord.setFields(policyHeader.toRecord(), false);

        //call dao method to copy quote
        String newQuoteNo = getPolicyDAO().getNewQuoteNo(inputRecord);
        inputRecord.setFieldValue("newPolNo", newQuoteNo);
        //set fields' value
        inputRecord.setFieldValue("existingPolB", "N");
        inputRecord.setFieldValue("fromCycle", "QUOTE");
        inputRecord.setFieldValue("toCycle", "QUOTE");
        inputRecord.setFieldValue("termEffectiveFromDate",null);
        inputRecord.setFieldValue("termEffectiveToDate",null);
        inputRecord.setFieldValue("issueCompanyEntityId",null);
        inputRecord.setFieldValue("issueStateCode",null);
        inputRecord.setFieldValue("processLocationCode",null);
        inputRecord.setFieldValue("newPolType",null);
        inputRecord.setFieldValue("riskBaseId",null);
        inputRecord.setFieldValue("dummyState",null);

        outputRecord = getPolicyDAO().createNextCycle(inputRecord);
        //set copiedQuoteNo to the output record
        outputRecord.setFieldValue("copiedQuoteNo", newQuoteNo);

        YesNoFlag isCopyQuoteError = YesNoFlag.N;
        if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_QT_CM_OCC_CONV)).booleanValue()) {
            isCopyQuoteError = getPolicyDAO().isCopyQuoteError(inputRecord);
            if (isCopyQuoteError.booleanValue()) {
                String copyQuoteErrorTrans = getPolicyDAO().getCopyQuoteErrorTrans(inputRecord);
                outputRecord.setFieldValue("copyQuoteErrorTrans", copyQuoteErrorTrans);
            }
        }
        outputRecord.setFieldValue("isCopyQuoteError", isCopyQuoteError);

        l.exiting(getClass().getName(), "copyQuote", outputRecord);
        return outputRecord;
    }


    /**
     * Accept quote
     *
     * @param policyHeader
     * @param inputRecord  with accept information
     * @return result record which contains parallel policy #
     */
    public Record acceptQuote(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "acceptQuote", new Object[]{policyHeader, inputRecord});

        Record outputRecord;

        //validate befoe accept quote
        validateQuoteForAccept(policyHeader, inputRecord);

        //set field values,need overwrite
        inputRecord.setFields(policyHeader.toRecord(), true);

        // create additional parms retrieval
        inputRecord.setFieldValue("addlParms", buildAddlParmsField(inputRecord));

        //set data fields
        if (policyHeader.getPolicyCycleCode().isQuote()) {
            inputRecord.setFieldValue("policyCycle", "POLICY");
        }
        else {
            inputRecord.setFieldValue("policyCycle", "QUOTE");
        }

        if (!inputRecord.hasField("quoteTransactionCode")) {
            inputRecord.setFieldValue("quoteTransactionCode", null);
        }
        // call dao method to get a record which contains parallel policy #
        Record polNoRec = getParallelPolicyNo(inputRecord);
        //get parallel policy No
        boolean isPolicyNotFound = YesNoFlag.getInstance(polNoRec.getStringValue("return")).booleanValue();
        if (isPolicyNotFound) {
            inputRecord.setFieldValue("newPolNo", polNoRec.getStringValue("polNo"));
            inputRecord.setFieldValue("existingPolB", "N");
            inputRecord.setFieldValue("fromCycle", "QUOTE");
            inputRecord.setFieldValue("toCycle", "POLICY");
            inputRecord.setFieldValue("termEffectiveFromDate", null);
            inputRecord.setFieldValue("termEffectiveToDate", null);
            inputRecord.setFieldValue("issueCompanyEntityId", null);
            inputRecord.setFieldValue("issueStateCode", null);
            inputRecord.setFieldValue("processLocationCode", null);

            outputRecord = getPolicyDAO().createNextCycle(inputRecord);
            outputRecord.setFieldValue("parallelPolNo", polNoRec.getStringValue("polNo"));
        }
        else {
            MessageManager.getInstance().addErrorMessage("pm.maintainQuote.accept.genPolicyNo.error");
            throw new ValidationException("failed to get parallel policy #");
        }

        l.exiting(getClass().getName(), "acceptQuote", outputRecord);
        return outputRecord;
    }

    /**
     * Method that validates data before accepte quote.
     * <p/>
     *
     * @param policyHeader
     */
    protected void validateQuoteForAccept(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateQuoteForAccept", new Object[]{policyHeader});

        // check existing policy
        inputRecord.setFields(policyHeader.toRecord(), false);
        SimilarPolicyRecordValidator similarPolicyValidator = new SimilarPolicyRecordValidator();
        CreatePolicyFields.setPolicyHolderNameEntityId(inputRecord, policyHeader.getPolicyHolderNameEntityId());
        similarPolicyValidator.validate(inputRecord);

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasConfirmationPrompts() || MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("invalid accept quote data");

        l.exiting(getClass().getName(), "validateQuoteForAccept");
    }

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
                                       String coverageClassId, UserSession userSession) {
        setCurrentIdsInSession(policyTermHistoryId, riskId, coverageId, null, coverageClassId, userSession);
    }

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
    public void setCurrentIdsInSession(String policyTermHistoryId, String riskId, String coverageId, String componentId, String coverageClassId, UserSession userSession) {
        Logger l = LogUtils.enterLog(getClass(), "setCurrentIdsInSession",
            new Object[]{policyTermHistoryId, riskId, coverageId, componentId, userSession});

        if (StringUtils.isNumeric(policyTermHistoryId) && !policyTermHistoryId.equals(userSession.get(RequestIds.POLICY_TERM_HISTORY_ID))) {
            userSession.set(RequestIds.POLICY_TERM_HISTORY_ID, policyTermHistoryId);
            userSession.set(RequestIds.RISK_ID, null);
            userSession.set(RequestIds.COVERAGE_ID, null);
            userSession.set(RequestIds.POLICY_COV_COMPONENT_ID, null);
            userSession.set(RequestIds.COVERAGE_CLASS_ID, null);
        }

        if (StringUtils.isNumeric(riskId) && !riskId.equals(userSession.get(RequestIds.RISK_ID))) {
            userSession.set(RequestIds.RISK_ID, riskId);
            userSession.set(RequestIds.COVERAGE_ID, null);
            userSession.set(RequestIds.POLICY_COV_COMPONENT_ID, null);
            userSession.set(RequestIds.COVERAGE_CLASS_ID, null);
        }

        if (StringUtils.isNumeric(coverageId) && !coverageId.equals(userSession.get(RequestIds.COVERAGE_ID))) {
            userSession.set(RequestIds.COVERAGE_ID, coverageId);
            userSession.set(RequestIds.POLICY_COV_COMPONENT_ID, null);
            userSession.set(RequestIds.COVERAGE_CLASS_ID, null);
        }

        if (StringUtils.isNumeric(componentId) && !componentId.equals(userSession.get(RequestIds.POLICY_COV_COMPONENT_ID))) {
            userSession.set(RequestIds.POLICY_COV_COMPONENT_ID, componentId);
        }

        if (StringUtils.isNumeric(coverageClassId) && !coverageClassId.equals(userSession.get(RequestIds.COVERAGE_CLASS_ID))) {
            userSession.set(RequestIds.COVERAGE_CLASS_ID, coverageClassId);
        }

        l.exiting(getClass().getName(), "setCurrentIdsInSession");
    }

    /**
     * Determines if the coverage class item should be enabled
     *
     * @param policyHeader     Instance of the policy header
     * @param inputRecord      Record containing policy/risk/term information
     * @param policyLevelCheck boolean indicator if this check is for the policy page
     * @return boolean indicating yes/no to enable the coverage class option
     */
    public boolean isCoverageClassAvailable(PolicyHeader policyHeader, Record inputRecord, boolean policyLevelCheck) {
        Logger l = LogUtils.enterLog(getClass(), "isCoverageClassAvailable", new Object[]{policyHeader, inputRecord, Boolean.valueOf(policyLevelCheck)});

        boolean result = false;

        if (policyLevelCheck) {
            if (inputRecord.hasStringValue("riskId")) {
                inputRecord.setFieldValue("riskId", "");
            }
        }

        // Determine upon which date to find primary information
        if (policyHeader.getLastTransactionInfo().getTransactionStatusCode().isComplete()) {
            inputRecord.setFieldValue("evalDt", policyHeader.getTermEffectiveToDate());
        }
        else {
            inputRecord.setFieldValue("evalDt", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }

        inputRecord.setFields(policyHeader.toRecord(), false);

        // Call the DAO
        result = getPolicyDAO().isCoverageClassAvailable(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCoverageClassAvailable", Boolean.valueOf(result));
        }

        return result;
    }

    /**
     * Method that evaluates policy business rule for ability to edit payment plan list.
     *
     * @param record Record of current policy data that was retrieved for display
     * @param policyHeader instance of the PolicyHeader object with current term/transaction data
     * @return boolean indicating if the payment plan list field is editable
     */
    public boolean isPaymentPlanLstEditable(Record record, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPaymentPlanLstEditable", new Object[]{record});
        }

        boolean isEditAllowed = false;

        try {
            if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isWIP()) {
                if (!(policyHeader.getScreenModeCode().isCancelWIP() ||
                    policyHeader.getScreenModeCode().isResinstateWIP())) {
                    isEditAllowed = true;
                }
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if payment plan is editable.", e);
            l.throwing(getClass().getName(), "isPaymentPlanLstEditable", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isPaymentPlanLstEditable", Boolean.valueOf(isEditAllowed));
        return isEditAllowed;
    }

    /**
     * Method that evaluates policy business rule for ability to edit declination date.
     *
     * @param record Record of current policy data that was retrieved for display
     * @return boolean indicating if the declination date field is editable
     */
    public boolean isDeclinationDateEditable(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isDeclinationDateEditable", new Object[]{record});
        }

        boolean isEditAllowed = false;

        try {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            if (recordModeCode.isRequest()) {
                isEditAllowed = true;
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if declination date is editable.", e);
            l.throwing(getClass().getName(), "isDeclinationDateEditable", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isDeclinationDateEditable", Boolean.valueOf(isEditAllowed));
        return isEditAllowed;
    }

    /**
     * Method to load selected address and all available address for the policyholder or COI Holder
     *
     * @param policyHeader instance of the PolicyHeader object with current term/transaction data
     * @param inputRecord a record with query information
     * @param loadProcessor an instance of data load processor
     * @return a RecordSet with selected address and all available address records
     */
    public RecordSet loadAllAddress(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAddress", new Object[]{inputRecord});
        RecordSet recordSet;

        String type = inputRecord.getStringValue("type");
        // Setup the record load processor
        SelectAddressEntitlementRecordLoadProcessor selAddrLoadProcessor =
            new SelectAddressEntitlementRecordLoadProcessor(policyHeader.getScreenModeCode(), type, inputRecord);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, selAddrLoadProcessor);

        String sourceId = null;
        String entityRoleId = null;

        if (type.equals(PolicyFields.RoleTypeValues.POLICYHOLDER)) {
            Record r = new Record();
            PolicyFields.setPolicyId(r, policyHeader.getPolicyId());
            String roleTypeCode = "";
            if (policyHeader.getPolicyCycleCode().isPolicy()) {
                roleTypeCode = "POLHOLDER";
            }
            else if (policyHeader.getPolicyCycleCode().isQuote()) {
                roleTypeCode = "PROSPECT";
            }
            PolicyFields.setRoleTypeCode(r, roleTypeCode);
            String entityId = policyHeader.getPolicyHolderNameEntityId();
            sourceId = entityId;
            EntityFields.setEntityId(r, entityId);
            TransactionFields.setTransactionEffectiveFromDate(r, policyHeader.getTermEffectiveFromDate());
            PolicyFields.setSourceRecordId(r, policyHeader.getPolicyId());
            entityRoleId = getPolicyDAO().getEntityRoleIdForEntity(r);
        }
        else if (type.equals(PolicyFields.RoleTypeValues.COIHOLDER)) {
            sourceId = EntityFields.getEntityId(inputRecord);
            entityRoleId = PolicyFields.getEntityRoleId(inputRecord);
        }
        else if (type.equals(PolicyFields.RoleTypeValues.RISK)) {
            Record r = new Record();
            PolicyFields.setRoleTypeCode(r, PolicyFields.RoleTypeValues.RISK);
            EntityFields.setEntityId(r, EntityFields.getEntityId(inputRecord));
            PolicyFields.setSourceRecordId(r, RiskFields.getRiskBaseRecordId(inputRecord));
            TransactionFields.setTransactionEffectiveFromDate(r, policyHeader.getTermEffectiveFromDate());
            entityRoleId = getPolicyDAO().getEntityRoleIdForEntity(r);
            sourceId = EntityFields.getEntityId(inputRecord);
            if(inputRecord.hasFieldValue("isFromExposure")){
                inputRecord.setFieldValue("fromExposureB", inputRecord.getFieldValue("isFromExposure"));
                inputRecord.setFieldValue("addtlExpAddrId", inputRecord.getFieldValue("addtlExpAddrId"));
            }
        }

        PolicyFields.setEntityRoleId(inputRecord, entityRoleId);
        PolicyFields.setSourceId(inputRecord, sourceId);

        // load addresses
        recordSet = getPolicyDAO().loadAllAddress(inputRecord, loadProcessor);

        recordSet.setFieldValueOnAll("entityRoleId", entityRoleId);
        recordSet.getSummaryRecord().setFieldValue("type", type);
        recordSet.getSummaryRecord().setFieldValue("entityRoleId", entityRoleId != null ? entityRoleId : "");
        recordSet.getSummaryRecord().setFieldValue("entityId",
            inputRecord.hasFieldValue("entityId") ? EntityFields.getEntityId(inputRecord) : "");

        l.exiting(getClass().getName(), "loadAllAddress", recordSet);
        return recordSet;
    }

    /**
     * Builds an additional parameter string buffer for shared policy level fields
     */
    public String buildAddlParmsField(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "buildAddlParmsField", new Object[]{inputRecord});

        // Concatenate everything together
        StringBuffer addlParm = new StringBuffer();
        addlParm.append("CHAR1^").append(inputRecord.getStringValue("char1", "")).append("^");
        addlParm.append("CHAR2^").append(inputRecord.getStringValue("char2", "")).append("^");
        addlParm.append("CHAR3^").append(inputRecord.getStringValue("char3", "")).append("^");
        addlParm.append("DATE1^").append(inputRecord.getStringValue("date1", "")).append("^");
        addlParm.append("DATE2^").append(inputRecord.getStringValue("date2", "")).append("^");
        addlParm.append("DATE3^").append(inputRecord.getStringValue("date3", "")).append("^");
        addlParm.append("NUM1^").append(inputRecord.getStringValue("num1", "")).append("^");
        addlParm.append("NUM2^").append(inputRecord.getStringValue("num2", "")).append("^");
        addlParm.append("NUM3^").append(inputRecord.getStringValue("num3", "")).append("^");
        addlParm.append("POLICY_LAYER_CODE^").append(inputRecord.getStringValue("policyLayerCode", "")).append("^");
        addlParm.append("POLICY_FORM_CODE^").append(inputRecord.getStringValue("policyPolicyFormCode", "")).append("^");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildAddlParmsField", addlParm);
        }

        return String.valueOf(addlParm);
    }

    /**
     * Copy policy to quote
     *
     * @param policyHeader
     * @param inputRecord
     * @return result record which contains parallel quote #
     */
    public Record copyPolicyToQuote(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyPolicyToQuote", new Object[]{policyHeader, inputRecord});
        }
        Record outputRecord = null;
        inputRecord.setFields(policyHeader.toRecord(), false);
        //set data fields
        inputRecord.setFieldValue("policyCycle", "QUOTE");
        // call dao method to get a record which cotains parallel quote #
        Record polNoRec = null;
        if ("Y".equals(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_QTE_NO_FROM_POL, "Y"))) {
            polNoRec = getPolicyDAO().getParallelQuoteNo(inputRecord);
        }
        else {
            polNoRec = getPolicyDAO().getParallelPolicyNo(inputRecord);
        }
        //get parallel quote No
        boolean isQuoteNoFound = polNoRec.hasStringValue("polNo");
        if (isQuoteNoFound) {
            Record recordForCopy = new Record();
            recordForCopy.setFields(inputRecord);
            recordForCopy.setFieldValue("newPolNo", polNoRec.getStringValue("polNo"));
            //set fields' value
            recordForCopy.setFieldValue("existingPolB", "N");
            recordForCopy.setFieldValue("fromCycle", "POLICY");
            recordForCopy.setFieldValue("toCycle", "QUOTE");
            recordForCopy.setFieldValue("termEffectiveFromDate", null);
            recordForCopy.setFieldValue("termEffectiveToDate", null);
            recordForCopy.setFieldValue("issueCompanyEntityId", null);
            recordForCopy.setFieldValue("issueStateCode", null);
            recordForCopy.setFieldValue("processLocationCode", null);
            outputRecord = getPolicyDAO().createNextCycle(recordForCopy);
            outputRecord.setFieldValue("parallelPolNo", polNoRec.getStringValue("polNo"));
        }
        else {
            MessageManager.getInstance().addErrorMessage("pm.maintainPolicy.copyPolicyToQuote.deriveQuoteNo.error");
            throw new ValidationException("failed to get parallel quote #");
        }
        String quoteNo = polNoRec.getStringValue("polNo");
        inputRecord.setFieldValue("policyNo", quoteNo);
        //get quote data
        Record quoteRec = getPolicyDAO().getPolicyKeyInfo(inputRecord);
        if (quoteRec.getIntegerValue("returnCode").intValue() != 0) {
            throw new AppException("pm.maintainPolicy.copyPolicyToQuote.error");
        }

        //unlock policy
        getLockManager().unLockPreviouslyHeldLock();

        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        // Initialize the new save official workflow
        wa.initializeWorkflow(polNoRec.getStringValue("polNo"),
            "SaveAsOfficialDetail",
            "invokeRateNotifyAndSaveAsOfficialDetail");
        wa.setWorkflowAttribute(quoteNo, "skipNotify", YesNoFlag.Y);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyPolicyToQuote", outputRecord);
        }

        return outputRecord;

    }

    /**
     * Loads risk coverage list for quote
     * @param inputRecord input record that contains entity id.
     * @return risk coverage list
     */
    public RecordSet loadAllQuoteRiskCovg(Record inputRecord, RecordLoadProcessor lp) {
        RecordSet rs = getPolicyDAO().loadAllQuoteRiskCovg(inputRecord, lp);
        return rs;
    }

    /**
     * Load all status
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllQuoteStatus(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllQuoteStatus", new Object[]{policyHeader});
        }
        RecordSet rs;
        Record input = policyHeader.toRecord();
        // Get quote status record set
        rs = getPolicyDAO().loadAllQuoteStatus(input);
        // Set the most current status value.
        String recentStatus = "";
        Record output = rs.getSummaryRecord();
        if (rs.getSize() > 0) {
            recentStatus = rs.getFirstRecord().getStringValue("statusCode");
        }
        output.setFieldValue("recentStatus", recentStatus);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllQuoteStatus", rs);
        }

        return rs;
    }

    /**
     * save a quote status
     *
     * @param policyHeader
     * @param inputRecords
     */
    public void saveQuoteStatus(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveQuoteStatus", new Object[]{policyHeader, inputRecords});
        }

        Record sumRecord = inputRecords.getSummaryRecord();
        // validate the input data.
        validateQuoteStatus(sumRecord);
        Record record = new Record();
        // Format the policy no,for example: from QMP13567-1 to QMP13567.
        String policyInitNo = policyHeader.getPolicyNo();
        String policyNo = policyInitNo.substring(0, policyInitNo.indexOf("-"));
        record.setFieldValue("statusCode", sumRecord.getStringValue("statusHistoryCode"));
        record.setFieldValue("policyNO", policyNo);
        PolicyFields.setPolicyId(record, policyHeader.getPolicyId());
        getPolicyDAO().saveQuoteStatus(record);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveQuoteStatus");
        }

    }

    /**
     * validate the input data.
     *
     * @param sumRecord
     */
    protected void validateQuoteStatus(Record sumRecord) {
        String statusCode = sumRecord.getStringValue("statusHistoryCode");
        String recentStatus = sumRecord.getStringValue("recentStatus");

        if (StringUtils.isBlank(statusCode)) {
            MessageManager.getInstance().addErrorMessage("pm.processQuoteStatus.status.empty");
        }

        // issue 113063: Quote version has a default value as current quote no when first loaded.
        //               If it is visible for trigger forms, it is required.
        if (!sumRecord.hasStringValue(PolicyFields.QUOTE_VERSION)) {
            MessageManager.getInstance().addErrorMessage("pm.processQuoteStatus.version.empty");
        }

        else if (!StringUtils.isBlank(recentStatus) && statusCode.equals(recentStatus) &&
            !ConfirmationFields.isConfirmed("pm.processQuoteStatus.same.status", sumRecord)) {
            MessageManager.getInstance().addConfirmationPrompt("pm.processQuoteStatus.same.status");
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts())
            throw new ValidationException("Validate quote status fail.");
    }

    /**
     * Get the policy Id by policy No.
     *
     * @param inputRecord
     * @return String
     */
    public String getPolicyId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyId", new Object[]{inputRecord});
        }

        String returnString = getPolicyDAO().getPolicyId(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyId", returnString);
        }
        return returnString;
    }

    /**
     * Get the policy No by policy id.
     *
     * @param policyId
     * @return String
     */
    public String getPolicyNo(String policyId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyNo", new Object[]{policyId,});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyId", policyId);
        String policyNo = getPolicyDAO().getPolicyNo(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyNo", policyNo);
        }
        return policyNo;
    }

    /**
     * Get Policy holder
     *
     * @param inputRecord
     * @return String
     */
    public String getPolicyHolder(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyHolder", new Object[]{inputRecord});
        }

        String policyHolder = getPolicyDAO().getPolicyHolder(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyHolder", policyHolder);
        }
        return policyHolder;
    }

    /**
     * Get the policy Id by policy No.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getPrimaryRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPrimaryRisk", new Object[]{inputRecord});
        }

        Record output = getPolicyDAO().getPrimaryRisk(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPrimaryRisk", output);
        }
        return output;
    }

    /**
     * Method to generate new policy no
     *
     * @param inputRecord
     * @retrun Record
     */
    public Record getParallelPolicyNo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getParallelPolicyNo", new Object[]{inputRecord});
        }

        Record polNoRec = getPolicyDAO().getParallelPolicyNo(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getParallelPolicyNo", polNoRec);
        }
        return polNoRec;
    }

    /**
     * Method to generate forms trigger process.
     *
     * @param policyHeader the summary policy information.
     * @param inputRecord Record with the related entry info
     * @return
     */
    public void triggerFormsFromQuoteStatus(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "triggerFormsFromQuoteStatus", new Object[]{policyHeader, inputRecord});
        }

        // Create the transaction first using new transactionCode ENDQTSTAT
        TransactionFields.setNewEndorsementCode(inputRecord, null);
        Transaction trans = getTransactionManager().createTransaction(policyHeader,
            inputRecord, policyHeader.getTermEffectiveFromDate(), TransactionCode.ENDQTSTAT, false);

        TransactionFields.setTransactionLogId(inputRecord, trans.getTransactionLogId());

        // Invoke output process to trigger forms
        getTransactionManager().processOutput(inputRecord, true);

        // Complete the transaction
        getTransactionManager().UpdateTransactionStatusNoLock(trans, TransactionStatus.COMPLETE);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "triggerFormsFromQuoteStatus");
        }
    }

    /**
     * Method to get workbench default values.
     * @return Record
     */
    public Record getWorkbenchDefaultValues() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWorkbenchDefaultValues");
        }

        Record defaultValues = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_POLICY_ACTION_CLASS_NAME);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getWorkbenchDefaultValues", defaultValues);
        }

        return defaultValues;
    }

    /**
     * Method that returns an instance of a record set object with the list of terms.
     * <p/>
     *
     * @param policyId policy pk
     * @return RecordSet an instance of the record set result object object with the list of terms.
     */
    public RecordSet loadPolicyTermList(String policyId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyTermList", policyId);
        }

        RecordSet rs = getPolicyDAO().loadPolicyTermList(policyId);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyTermList", rs);
        }

        return rs;
    }

    /**
     * Rerate On-demand.
     *
     * @param inputRecord
     * @return a Record with workflow instance Id and workflow state
     */
    public Record reRateOnDemand(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "reRateOnDemand", new Object[]{inputRecord});

        Record record = new Record();
        Date todayDate = new Date();
        String today = String.valueOf(todayDate.getTime());
        String workflowInstanceId = "Rerate-" + today;
        WorkflowFields.setWorkflowInstanceId(record, workflowInstanceId);
        String workflowState;

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        String termList = PolicyFields.getTermList(inputRecord);
        rsm.set(PolicyFields.TERM_LIST, termList);

        WorkflowAgent wa = dti.oasis.workflowmgr.impl.WorkflowAgentImpl.getInstance(PolicyManager.OASIS_WORKFLOW_BEAN_NAME);
        workflowState = RERATE_INITIAL_STATE;
        wa.initializeWorkflow(workflowInstanceId,
            RERATE_WORKFLOW_PROCESS,
            workflowState);

        WorkflowFields.setWorkflowState(record, workflowState);
        wa.setWorkflowAttribute(workflowInstanceId, PolicyFields.TERM_LIST, termList);

        l.exiting(getClass().getName(), "reRateOnDemand", record);
        return record;
    }

    /**
     * Rerate Batch.
     *
     * @param inputRecord
     * @return Record
     */
    public Record reRateBatch(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "reRateBatch", new Object[]{inputRecord});

        Record record = getPolicyDAO().reRateBatch(inputRecord);

        l.exiting(getClass().getName(), "reRateBatch");
        return record;
    }

    /**
     * Returns a RecordSet of mass rerate result for the provided
     * inputRecord which contains the search criteria.
     * <p/>
     *
     * @param inputRecord a record contains all search criteria information.
     * @return RecordSet a RecordSet of mass rerate result.
     */
    public RecordSet loadAllReRateResult(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllReRateResult", new Object[]{inputRecord});

        RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();

        RecordSet rs = getPolicyDAO().loadAllReRateResult(inputRecord, selectIndProcessor);

        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.reRatePolicy.reRateResult.nodata.found.error");
        }

        l.exiting(getClass().getName(), "loadAllReRateResult");

        return rs;
    }

    /**
     * Returns a RecordSet of mass rerate result detail for the provided
     * inputRecord which contains the search criteria.
     * <p/>
     *
     * @param inputRecord a record contains all search criteria information.
     * @return RecordSet a RecordSet of mass rerate result detail.
     */
    public RecordSet loadAllReRateResultDetail(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllReRateResultDetail", new Object[]{inputRecord});

        RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();

        RecordSet rs = getPolicyDAO().loadAllReRateResultDetail(inputRecord, selectIndProcessor);

        l.exiting(getClass().getName(), "loadAllReRateResultDetail");

        return rs;
    }

    /**
     * Perform rerate On-demand.
     *
     * @param inputRecord
     * @return String
     */
    public String performReRateOnDemand(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "reRateOnDemand", new Object[]{inputRecord});

        Record record = getPolicyDAO().reRateOnDemand(inputRecord);
        String requestId = PolicyFields.getRequestId(record);

        l.exiting(getClass().getName(), "reRateOnDemand", record);
        return requestId;
    }

    /**
     * Load the policy detail information for the Policy Inquiry Service.
     *
     * @param inputRecord
     * @return
     */
    public Record loadPolicyDetailForWS(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyDetailForWS", new Object[]{inputRecord});

        Record output = new Record();
        RecordSet rs = getPolicyDAO().loadPolicyDetailForWS(inputRecord);
        if (rs == null || rs.getSize() == 0) {
            throw new AppException("Unable to load the policy information for Web Service.");
        }

        RecordSet filterRs = new RecordSet();
        String transStatusCodeFilter = inputRecord.getStringValue(PolicyInquiryFields.TRANSACTION_STATUS_CODE);
        String transStatusCodeResult = rs.getSummaryRecord().getStringValue(PolicyInquiryFields.TRANSACTION_STATUS_CODE);

        filterRs = transStatusCodeFilterRecForWS(rs, transStatusCodeFilter, transStatusCodeResult);

        if(filterRs.getSize() != 0){
            output = filterRs.getFirstRecord();
            Record summaryRecord = filterRs.getSummaryRecord();
            output.setFields(summaryRecord);
        }

        l.exiting(getClass().getName(), "loadPolicyDetailForWS", output);
        return output;
    }

    /**
     * Load the policy detail list for the Policy Inquiry Service.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadPolicyDetailListForWS(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyDetailForWS", new Object[]{inputRecord});

        RecordSet rs = getPolicyDAO().loadPolicyDetailForWS(inputRecord);
        if (rs == null || rs.getSize() == 0) {
            throw new AppException("Unable to load the policy information for Web Service.");
        }

        RecordSet filterRs = new RecordSet();
        String transStatusCodeFilter = inputRecord.getStringValue(PolicyInquiryFields.TRANSACTION_STATUS_CODE);

        if (String.valueOf(RecordMode.OFFICIAL).equalsIgnoreCase(transStatusCodeFilter)) {
            filterRs = rs.getSubSet(new RecordFilter(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
        }else{
            filterRs = rs;
        }

        l.exiting(getClass().getName(), "loadPolicyDetailForWS", filterRs);
        return filterRs;
    }

    /**
     * Load the billing accounts for policy.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadPolicyBillingAccountInfoForWS(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyBillingAccountInfoForWS", new Object[]{inputRecord});

        RecordSet rs = getPolicyDAO().loadPolicyBillingAccountInfoForWS(inputRecord);

        l.exiting(getClass().getName(), "loadPolicyBillingAccountInfoForWS", rs);
        return rs;
    }

    /**
     * Determines if entity belongs to PM.
     *
     * @param inputRecord
     * @return
     */
    public String isPolicyEntity(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isPolicyEntity", new Object[]{inputRecord});

        String rs = getPolicyDAO().isPolicyEntity(inputRecord);

        l.exiting(getClass().getName(), "isPolicyEntity", rs);
        return rs;
    }

    @Override
    public Record getLatestTerm(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLatestTerm", inputRecord);
        }

        Record record = getPolicyDAO().getLatestTerm(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLatestTerm", record);
        }
        return record;
    }

    @Override
    public boolean isNewBusinessTerm(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isNewBusinessTerm", inputRecord);
        }

        boolean isNewBusinessTermB = getPolicyDAO().isNewBusinessTerm(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isNewBusinessTerm", isNewBusinessTermB);
        }
        return isNewBusinessTermB;
    }

    /**
     * Filter records by transStatusCode.
     *
     * @param inputRecordSet
     * @return filterRs
     */
    public RecordSet transStatusCodeFilterRecForWS(RecordSet inputRecordSet, String transStatusCodeFilter, String transStatusCodeResult) {
        Logger l = LogUtils.enterLog(getClass(), "transStatusCodeFilterRecForWS", new Object[]{transStatusCodeFilter});

        RecordSet filterRs = new RecordSet();

        if (String.valueOf(RecordMode.OFFICIAL).equalsIgnoreCase(transStatusCodeFilter)) {
            filterRs = inputRecordSet.getSubSet(new RecordFilter(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
        }else if(String.valueOf(RecordMode.WIP).equalsIgnoreCase(transStatusCodeFilter)
            && String.valueOf(TransactionStatus.INPROGRESS).equals(transStatusCodeResult)
            || StringUtils.isBlank(transStatusCodeFilter)){
            filterRs = inputRecordSet.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));

            // If no records after the filter, no TEMP changes so take the OFFICIAL picture
            if (filterRs.getSize() == 0) {
                filterRs = inputRecordSet.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
            }
        }

        l.exiting(getClass().getName(), "transStatusCodeFilterRecForWS", filterRs);
        return filterRs;
    }

    /**
     * Identify whether policyNos exist in system
     * <p/>
     *
     * @param inputRecord
     * @return invalid policyNo
     */
    public String validatePolicyNosExist(Record inputRecord){
        return getPolicyDAO().validatePolicyNosExist(inputRecord);
    }

    /**
     * Identify whether termBaseRecordIds exist in system
     * <p/>
     *
     * @param inputRecord
     * @return invalid termBaseRecordIds
     */
    public String validateTermBaseRecordIdsExist(Record inputRecord) {
        return getPolicyDAO().validateTermBaseRecordIdsExist(inputRecord);
    }

    /**
     * Get entity id by client id
     * <p/>
     *
     * @param partNumberId
     * @param clientId
     */
    public String getEntityIdByClientId(String partNumberId, String clientId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getValidEntityId", new Object[]{partNumberId, clientId});
        }

        String entityId;
        if (!StringUtils.isBlank(clientId)) {
            Record inputRecord = new Record();
            inputRecord.setFieldValue("sClientId", clientId);
            entityId = getPolicyDAO().getEntityIdByClientId(inputRecord);
        }
        else {
            entityId = partNumberId;
        }

        if (StringUtils.isDecimal(entityId) && Integer.parseInt(entityId) == -1) {
            AppException ae = new AppException("ws.policy.change.issueCompany.invalid.clientId", "",
                new String[]{clientId});
            l.throwing(getClass().getName(), "getEntityIdByClientId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityIdByClientId");
        }

        return entityId;
    }

    /**
     * load policy header for webService
     * <p/>
     *
     * @param policyNo
     * @param termBaseRecordId
     * @param transactionStatusCode
     * @return policyHeader
     */
    public PolicyHeader loadPolicyHeaderForWS(String policyNo, String termBaseRecordId, String transactionStatusCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyHeaderForWS", new Object[]{policyNo});
        }
        PolicyHeader policyHeader = new PolicyHeader();
        policyHeader = getPolicyDAO().loadPolicyHeaderForWS(policyNo, termBaseRecordId, transactionStatusCode);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyHeaderForWS", policyNo);
        }
        return policyHeader;
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig () {
        if (getPolicyDAO() == null)
            throw new ConfigurationException("The required property 'policyDAO' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getUserSessionManager() == null)
            throw new ConfigurationException("The required property 'userSessionManager' is missing.");
        if (getRequestStorageManager() == null)
            throw new ConfigurationException("The required property 'requestStorageManager' is missing.");
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
    }

    public PolicyDAO getPolicyDAO() {
        return m_policyDAO;
    }

    public void setPolicyDAO(PolicyDAO policyDAO) {
        m_policyDAO = policyDAO;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public UserSessionManager getUserSessionManager() {
        return m_userSessionManager;
    }

    public void setUserSessionManager(UserSessionManager userSessionManager) {
        m_userSessionManager = userSessionManager;
    }

    public RequestStorageManager getRequestStorageManager() {
        return m_requestStorageManager;
    }

    public void setRequestStorageManager(RequestStorageManager requestStorageManager) {
        m_requestStorageManager = requestStorageManager;
    }

    public PMDefaultManager getPmDefaultManager() {
        return m_pmDefaultManager;
    }

    public void setPmDefaultManager(PMDefaultManager pmDefaultManager) {
        m_pmDefaultManager = pmDefaultManager;
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

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    public TaxManager getTaxManager() {
        return m_taxManager;
    }

    public void setTaxManager(TaxManager taxManager) {
        m_taxManager = taxManager;
    }

    private PolicyDAO m_policyDAO;
    private TransactionManager m_transactionManager;
    private UserSessionManager m_userSessionManager;
    private RequestStorageManager m_requestStorageManager;
    private PMDefaultManager m_pmDefaultManager;
    private EntityManager m_entityManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private RiskManager m_riskManager;
    private ComponentManager m_componentManager;
    private LockManager m_lockManager;
    private DBUtilityManager m_dbUtilityManager;
    private TaxManager m_taxManager;

    protected static final String SAVE_PROCESSOR = "PolicyManager";

    private final static String EDIT_POLICYNO_USER_PROFILE = "PM_EDIT_POLNO";
    private final static String EDIT_IBNR_ROLLING_DATE = "PM_EDIT_IBNR_ROLLING_DATE";
    protected static final String MAINTAIN_POLICY_ACTION_CLASS_NAME = "dti.pm.policymgr.struts.MaintainPolicyAction";

    private static final String RERATE_WORKFLOW_PROCESS = "ReRatePolicyWorkflow";
    private static final String RERATE_INITIAL_STATE = "reRatePolicies";
    private static final String POLICY_PHASE_CODE = "policyPhaseCode";
}
