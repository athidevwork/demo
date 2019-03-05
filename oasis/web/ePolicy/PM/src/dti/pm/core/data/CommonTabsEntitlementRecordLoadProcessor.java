package dti.pm.core.data;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.DateUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.ProcessStatus;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.cachemgr.PolicyCacheManager;
import dti.pm.coverageclassmgr.CoverageClassFields;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.policyattributesmgr.PolicyAttributesFactory;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.Term;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.riskmgr.RiskFields;
import dti.pm.transactionmgr.TransactionManager;

import java.util.Iterator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 13, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/29/2007       fcb         policyHeader.getProcessStatusCode().isProcessStatusCancelOnly() used
 *                              instead of policyHeader.isProcessStatusCancelOnly() due to external changes.
 * 11/26/2007       fcb         isChangePolicyAdminAvailable and isChangeTermEffDateAvailable modified: added
 *                              logic to correclty handle policy locking.
 * 11/27/2007       fcb         isCopy2QuoteAvailable and related functionality added.
 * 11/28/2007       mzhu        isRenewalAvailable updated to fix issue 77478.
 * 12/06/2007       fcb         isBillingSetupAvailable added.
 * 12/12/2007       fcb         Re-do of multiple functions.
 *                              NOTE: many if/else were intentionally NOT put in a very concise way in order
 *                              to be easier to debug, analyze, and compare with Policy UI and Client Server logic.
 *                              The list of refactored functions is:
 *                              isEndorsementAvailable(), isOosEndorsementAvailable(),isCancelAvailableForPolicy,
 *                              isRenewalAvailable(), isChangePolicyAdminAvailable,isReissueAvailable,
 *                              isPurgeAvailable, isMultiCancelAvailable, isSaveAvailable
 * 01/02/2008       fcb         Logic that was looking whether the policy was locked by other user was removed,
 *                              just the wipB flag will be used.
 * 03/30/2009       msn         91735 In IsCopyDenyAcceptQuoteAvailable check sysparm PM_ACCEPT_AGAIN
 * 05/12/2009       msn         93447 For Cancel Actions checks at row level eg. in risk,coverage tabs
 *                              Check if cancel is availabel for policy first. Remove check for last term
 *                              to allow cancel in prior term
 * 02/01/2010       yhyang      103569, in Policy_Folder_UI.doc 2.3.6.2.
 *                              1) If the cycle is POLICY and it is not locked.
 *                              Herein, it means there is no any WIP transaction in this policy.
 * 04/28/2010       syang       106852 - If the transaction effective date before the risk/coverage/coverageclass
 *                              effective date, the Change option should be hidden.
 * 07/20/2010       syang       109789 - Modified isChangeAvailableForRiskCoverageAndClass() to hide Change option for
 *                              canceled risk/coverage/class.
 * 07/29/2010       syang       109978 - Modified isRenewalAvailable() to hide Renewal option if the policy in WIP
 *                              and locked by others.
 * 09/16/2010       dzhang      103813 - Added isUndoTermAvailable() to check if Undo Term button is available.
 * 10/25/2010       syang       Issue 113528 - Removed the redundant condition for coverage class from isDeleteAvailable().
 * 11/01/2010       gzeng       113774 - Disable 'Chg admin' option in case of OOS WIP but isn't the initial term.
 * 02/01/2010       syang       115087 - Modified isAdjPremiumAvailable() to handle Adj Premium option during OOSE.
 * 03/31/2011       syang       106634 - Added isAcfAvailable() to check ACF status.
 * 04/12/2011       wqfu        116576 - Modified isAutoRenewalAvailable() to check if renewal indicator code is AUTO.
 * 04/29/2011       gxc         105791 - Added CONVERTED related logic.
 * 06/01/2011       ryzhao      103808 - Added isAcceptDeclinePolicyAvailable() to check if accept/decline option is available.
 *                                       Added LockManager private member variable.
 * 03/22/3012       sxm         131482 - Modified isDeleteAvailable()
 *                                       1) to disable deletion in subsequent term(s) during OOSEWIP
 *                                       2) to remove incorrect logic for cancelled coverage
 * 06/25/2012       sxm         134889 - Disable buttons in prior term during renewal WIP
 *                                     - Disable Delete button in Policy Folder Tabs if record has cancelled status
 * 01/09/2013       tcheng      140729 - Modified isCancelAvailableForRiskCoverageAndClass() to make sure primary slot risk can be cancelled.
 * 03/06/2013       sxm         141850 - Enable Delete button in Policy Folder Tabs for TEMP cancelled record
 *                                       during renewal WIP
 * 10/09/2013       adeng       148753 - Modified isReinstateAvailableForRiskCoverageAndClass to check if renewal quote
 *                                       exist in latest term or not, if yes, reinstate option in action should be hidden.
 * 10/16/2013       fcb         148904 - Logic from isBillingSetupAvailable moved to TransactionManagerImpl
 * 10/16/2013       fcb         145725 - Added logic for caching snapshot indicator.
 *                                       Added logic to check the profile in UserCacheManager via OasisUser.
 *                                       Removed the call from isAcceptDeclinePolicyAvailable to check whether we can
 *                                       lock the policy. This is an expensive operation that is executed every time
 *                                       although there is a very rare case when the button will become available, and
 *                                       it will be used. In this case, the system will just state that policy has been
 *                                       locked by somebody else. The logic that is removed does not guarantee anyway
 *                                       the user concurrency, even with this checking it is still possible for an user
 *                                       to change the policy to WIP from another browser after the common entitlement
 *                                       logic has been processed.
 *                                     - some optimization in the overall flow.
 * 07/15/2014       wdang       154953 - 1) Created isCommonMultiCancelAvailable() method cloned from isMultiCancelAvailable()
 *                                          to display/hide Multi COI Holder Cancel option in Policy Action.
 *                                       2) Reconstruct isMultiCancelIAvailable() to add the case of renewal WIP 
 *                                          and call isCommonMultiCancelAvailable() subsequently.
 * 11/19/2015       eyin        167171 - Modified getInitialEntitlementValuesForCommonTabs(), Add synchronized lock on this function.
 * 01/06/2016       wdang       168069 - 1) Added logic of isRiskSumAvailable/isRiskAvailable that is cut-pasted from policy/risk/coverage page.
 *                                       2) Fix a defect when a record has field baseRecordId but hasn't field status.
 * 06/10/2016       ssheng      164927 - Change the quick quote support NB quote, quote endorsement, NB policy, policy endorsement, renewal policy.
 * 08/26/2016       wdang       167534 - Enable Endorsement/OOSE/Cancel by configuration on prior term in Renewal WIP.
 * 10/17/2016       xnie        180447 - 1) Modified isMultiCancelAvailable() to make multi cancel components in renewal
 *                                          WIP transaction only be available for term which is initial term.
 *                                       2) Modified isDeleteWipAvailable() to hide delete WIP button when we are in
 *                                          non-initial cancel term. This is for case when user cancels risk/coverage/
 *                                          sub-coverage in prior term.
 *                                       3) Modified isPreviewAvailable() to hide preview button when we are in
 *                                          non-initial cancel term (This is for case when user cancels risk/coverage/
 *                                          sub-coverage in prior term) and renewal term.
 * 10/25/2016       tzeng       180688 - Modified isCopy2QuoteAvailable to display this option by system parameter when
 *                                       renewal WIP.
 * 02/10/2017       tzeng       183335 - Modified isEndorsementAvailable and isOosEndorsementAvailable to add
 *                                       isOoseAndEndorsementAvailableInRenewalWip to check whether these two
 *                                       options need to be available in last official term when cancel in pre-renewal.
 * 05/09/2017       ssheng      185360 - Add system parameter 'PM_NB_QUICK_QUOTE' to indicate
 *                                       if import quote only supports to NB Quote.
 * 06/09/2017       xnie        185775 - Reverted 180447 fix except isMultiCancelAvailable() changes.
 * 06/13/2017       mlm                - isPreviewAvailable - Code review changes to remove unnecessary logic.
 * 07/24/2017       ssheng      187017 - Reverted issue 183335 fix.
 * 05/08/2018       ryzhao      192725 - Modified isPreviewAvailable() for the OOSE condition.
 *                                       The Preview button should be available for one day gap.
 * ---------------------------------------------------
 */
public class CommonTabsEntitlementRecordLoadProcessor implements RecordLoadProcessor {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {

        boolean isDeleteAvailable = isDeleteAvailable(record);
        record.setFieldValue("isDeleteAvailable", YesNoFlag.getInstance(isDeleteAvailable));

        //for risk/coverage/class set cancel option's available status
        boolean isCancelAvailable = isCancelAvailableForRiskCoverageAndClass(record);
        record.setFieldValue("isCancelAvailable", YesNoFlag.getInstance(isCancelAvailable));

        // for risk/coverage/class set Change option's available status
        boolean isChangeAvailable = isChangeAvailableForRiskCoverageAndClass(record);
        record.setFieldValue("isOosChangeAvailable", YesNoFlag.getInstance(isChangeAvailable));

        boolean isReinstateAvailable = isReinstateAvailableForRiskCoverageAndClass(record);
        record.setFieldValue("isReinstateAvailable", YesNoFlag.getInstance(isReinstateAvailable));

        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     * a field isDeleteWipAvailable is added to summary Record to determing if the actionItem DeleteWIP
     * should be visible or not.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        Record sumRecord = recordSet.getSummaryRecord();

        boolean isDeleteWipVisible = isDeleteWipAvailable(recordSet);
        sumRecord.setFieldValue("isDeleteWipAvailable", YesNoFlag.getInstance(isDeleteWipVisible));

        boolean isPreviewVisible = isPreviewAvailable(recordSet);
        sumRecord.setFieldValue("isPreviewAvailable", YesNoFlag.getInstance(isPreviewVisible));

        boolean isEndorsementVisible = isEndorsementAvailable(recordSet);
        sumRecord.setFieldValue("isEndorsementAvailable", YesNoFlag.getInstance(isEndorsementVisible));

        boolean isReRateAvailable = isReRateAvailable(recordSet);
        sumRecord.setFieldValue("isReRateAvailable", YesNoFlag.getInstance(isReRateAvailable));

        boolean isOosEndorsementAvailable = isOosEndorsementAvailable(recordSet);
        sumRecord.setFieldValue("isOosEndorsementAvailable", YesNoFlag.getInstance(isOosEndorsementAvailable));

        boolean isRenewalVisible = isRenewalAvailable(recordSet);
        sumRecord.setFieldValue("isRenewalAvailable", YesNoFlag.getInstance(isRenewalVisible));

        boolean isAutoRenewalVisible = isAutoRenewalAvailable(recordSet);
        sumRecord.setFieldValue("isAutoRenewalAvailable", YesNoFlag.getInstance(isAutoRenewalVisible));

        boolean isReactiveQuoteAvailable = isReactiveAvailable(recordSet);
        sumRecord.setFieldValue("isReactiveQuoteAvailable", YesNoFlag.getInstance(isReactiveQuoteAvailable));

        boolean isCopyDenyAcceptQuoteMenuAvailable = isCopyDenyAcceptQuoteMenuAvailable(recordSet);
        sumRecord.setFieldValue("isCopyDenyAcceptQuoteMenuAvailable", YesNoFlag.getInstance(isCopyDenyAcceptQuoteMenuAvailable));

        boolean isCopyDenyAcceptQuoteButtonAvailable = isCopyDenyAcceptQuoteButtonAvailable(recordSet);
        sumRecord.setFieldValue("isCopyDenyAcceptQuoteButtonAvailable", YesNoFlag.getInstance(isCopyDenyAcceptQuoteButtonAvailable));

        boolean isChangeTermEffDateAvailable = isChangeTermEffDateAvailable(recordSet);
        sumRecord.setFieldValue("isChgTermEffDateAvailable", YesNoFlag.getInstance(isChangeTermEffDateAvailable));

        boolean isChangePolicyAdminAvailable = isChangePolicyAdminAvailable();
        sumRecord.setFieldValue("isChgPolicyAdminAvailable", YesNoFlag.getInstance(isChangePolicyAdminAvailable));

        boolean isReissueAvailable = isReissueAvailable(recordSet);
        sumRecord.setFieldValue("isReissueAvailable", YesNoFlag.getInstance(isReissueAvailable));

        boolean isCancelAvailableForPolicy = isCancelAvailableForPolicy(recordSet);
        sumRecord.setFieldValue("isCancelAvailable", YesNoFlag.getInstance(isCancelAvailableForPolicy));

        boolean isPurgeVisible = isPurgeAvailable(recordSet);
        sumRecord.setFieldValue("isPurgeAvailable", YesNoFlag.getInstance(isPurgeVisible));

        boolean isMultiCancel = isMultiCancelAvailable(recordSet);
        sumRecord.setFieldValue("isMultiCancelAvailable", YesNoFlag.getInstance(isMultiCancel));

        boolean isMultiCOICancel = isCommonMultiCancelAvailable(recordSet);
        sumRecord.setFieldValue("isMultiCOICancelAvailable", YesNoFlag.getInstance(isMultiCOICancel));
        
        boolean isExtendCancel = isExtendCancelAvailable(recordSet);
        sumRecord.setFieldValue("isExtendCancelAvailable", YesNoFlag.getInstance(isExtendCancel));

        boolean isCancelFlat = isCancelFlatAvailable(recordSet);
        sumRecord.setFieldValue("isCancelFlatAvailable", YesNoFlag.getInstance(isCancelFlat));

        boolean isSaveAvailable = isSaveAvailable(recordSet);
        sumRecord.setFieldValue("isSaveAvailable", YesNoFlag.getInstance(isSaveAvailable));
        //for risk/coverage/class set Reinstate option's available status

        boolean isReinstatePolicyAvailable = isReinstateAvailableForPolicy(recordSet);
        sumRecord.setFieldValue("isReinstatePolicyAvailable", YesNoFlag.getInstance(isReinstatePolicyAvailable));

        boolean isCopy2QuoteAvailable = isCopy2QuoteAvailable();
        sumRecord.setFieldValue("isCopy2QuoteAvailable", YesNoFlag.getInstance(isCopy2QuoteAvailable));

        boolean isBillingSetupAvailable = isBillingSetupAvailable();
        sumRecord.setFieldValue("isBillingSetupAvailable", YesNoFlag.getInstance(isBillingSetupAvailable));

        boolean isAdjPremiumAvailable = isAdjPremiumAvailable();
        sumRecord.setFieldValue("isAdjPremiumAvailable", YesNoFlag.getInstance(isAdjPremiumAvailable));
        //add by Simon for ENDQUOTE
        boolean isCopyEndQuoteAvailable = isCopyEndQuoteAvailable();
        sumRecord.setFieldValue("isCopyEndQuoteAvailable", YesNoFlag.getInstance(isCopyEndQuoteAvailable));
        boolean isDelEndQuoteAvailable = isDelEndQuoteAvailable();
        sumRecord.setFieldValue("isDeleteEndQuoteAvailable", YesNoFlag.getInstance(isDelEndQuoteAvailable));
        boolean isDelRenQuoteAvailable = isDelRenQuoteAvailable();
        sumRecord.setFieldValue("isDeleteRenQuoteAvailable", YesNoFlag.getInstance(isDelRenQuoteAvailable));

        boolean isAppAvailable = isAppAvailable();
        sumRecord.setFieldValue("isAppAvailable", YesNoFlag.getInstance(isAppAvailable));

        boolean isQuickQuoteAvailable = isQuickQuoteAvailable();
        sumRecord.setFieldValue("isQuickQuoteAvailable", YesNoFlag.getInstance(isQuickQuoteAvailable));

        boolean isViewCancelDetailAvailable = isSnapshotExist();
        sumRecord.setFieldValue("isViewCancelDetailAvailable", YesNoFlag.getInstance(isViewCancelDetailAvailable));

        boolean isUndoTermAvailable = isUndoTermAvailable(recordSet);
        sumRecord.setFieldValue("isUndoTermAvailable", YesNoFlag.getInstance(isUndoTermAvailable));

        boolean isAcfAvailable = isAcfAvailable();
        sumRecord.setFieldValue("isAcfAvailable", YesNoFlag.getInstance(isAcfAvailable));

        boolean isAcceptDeclinePolicyAvailable = isAcceptDeclinePolicyAvailable();
        sumRecord.setFieldValue("isAcceptDeclinePolicyAvailable", YesNoFlag.getInstance(isAcceptDeclinePolicyAvailable));

        boolean isRiskSummaryAvailable = isRiskSummaryAvailable(recordSet);
        sumRecord.setFieldValue("isRiskSumAvailable", YesNoFlag.getInstance(isRiskSummaryAvailable));
        sumRecord.setFieldValue("isRiskAvailable", YesNoFlag.getInstance(!isRiskSummaryAvailable));

        if (recordSet.getSize() == 0) {
            recordSet.addFieldNameCollection(getInitialEntitlementValuesForCommonTabs().getFieldNameList());
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * To get if risk summary tab is available.
     *
     * @param recordSet intput from RecordSet.
     * @return boolean isRiskSumAvailable.
     */
    private boolean isRiskSummaryAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isRiskSummaryAvailable", new Object[]{recordSet});

        // Below logic is cut-pasted from other entitlement processor to just make it common.
        // However it doesn't make sense to activate isRiskSumAvailable only when initial loading
        // without any performance improvement.
        if (UserSessionManager.getInstance().getUserSession().has(RiskFields.INITIAL_LOADING_POLICY_ID)) {
            String initialLoadingPolicyId = (String) UserSessionManager.getInstance().getUserSession().get(RiskFields.INITIAL_LOADING_POLICY_ID);
            if (!getPolicyHeader().getPolicyId().equals(initialLoadingPolicyId)) {
                UserSessionManager.getInstance().getUserSession().set(RiskFields.INITIAL_LOADING_B, "Y");
                UserSessionManager.getInstance().getUserSession().set(RiskFields.INITIAL_LOADING_POLICY_ID, getPolicyHeader().getPolicyId());
            }
            else {
                UserSessionManager.getInstance().getUserSession().set(RiskFields.INITIAL_LOADING_B, "N");
            }
        }
        else {
            UserSessionManager.getInstance().getUserSession().set(RiskFields.INITIAL_LOADING_B, "Y");
            UserSessionManager.getInstance().getUserSession().set(RiskFields.INITIAL_LOADING_POLICY_ID, getPolicyHeader().getPolicyId());
        }

        YesNoFlag isRiskSumAvailable = YesNoFlag.getInstance(getPolicyHeader().getRiskSumAvailableB());
        if (UserSessionManager.getInstance().getUserSession().get(RiskFields.INITIAL_LOADING_B) == "Y") {
            UserSessionManager.getInstance().getUserSession().set("isRiskSumAvailable", isRiskSumAvailable);
        }
        else {
            isRiskSumAvailable = (YesNoFlag) UserSessionManager.getInstance().getUserSession().get("isRiskSumAvailable");
        }

        l.exiting(getClass().getName(), "isRiskSummaryAvailable", isRiskSumAvailable);
        return isRiskSumAvailable.booleanValue();
    }

    /**
     * To get policy stauts is cancel.
     *
     * @param recordSet intput from RecordSet.
     * @return boolean isReinstateAvailable.
     */
    private boolean isReinstateAvailableForPolicy(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isReinstateAvailableForPolicy", new Object[]{recordSet});

        boolean isReinstateAvailable = false;

        //for policy page
        PolicyHeader policyHeader = getPolicyHeader();
        ProcessStatus processStatus = policyHeader.getProcessStatusCode();

        if (policyHeader.getScreenModeCode().isViewPolicy()) {
            PolicyStatus policyStatus = policyHeader.getPolicyStatus();
            PolicyCycleCode policyCycleCode = policyHeader.getPolicyCycleCode();
            if (policyStatus.isActive()) {
                if (policyHeader.isWipB()) {
                    return isReinstateAvailable;
                }
                else if (!policyHeader.isQuoteRenewalExists()) {
                    if (!processStatus.isProcessStatusCancelOnly() && !policyHeader.isLastTerm()) {
                        if (!policyHeader.isShortTermB() && policyCycleCode.isPolicy()) {
                            isReinstateAvailable = true;
                        }
                    }
                }
            }
            else if (policyStatus.isCancelled()) {
                if (policyCycleCode.isPolicy() && !policyHeader.isWipB()) {
                    if (!processStatus.isProcessStatusCancelOnly() && policyHeader.isLastTerm()) {
                        if (!policyHeader.isShortTermB()) {
                            isReinstateAvailable = true;
                        }
                    }
                    else if (processStatus.isProcessStatusCancelOnly()) {
                        if (SysParmProvider.getInstance().getSysParm("PM_REINST_CANCELONLY", "N").equalsIgnoreCase("Y") &&
                            policyHeader.isLastTerm() && !policyHeader.isShortTermB()) {
                            isReinstateAvailable = true;
                        }
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "isReinstateAvailableForPolicy", YesNoFlag.getInstance(isReinstateAvailable));
        return isReinstateAvailable;
    }

    /**
     * To get risk/coverage/coverage class stauts is cancel.
     *
     * @param record intput record.
     * @return boolean isReinstateAvailable.
     */
    private boolean isReinstateAvailableForRiskCoverageAndClass(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "isReinstateAvailableForRiskCoverageAndClass", record);
        boolean isReinstateAvailable = false;
        boolean isAvailable = false;

        //for policy page
        PolicyHeader policyHeader = getPolicyHeader();
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode(); //PolicyViewMode.OFFICIAL,PolicyViewMode.WIP
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        // pre-condition: the quote or policy must be saved as official or is in Renewal WIP mode
        if (viewMode.isOfficial() || screenMode.isRenewWIP()) {
            isAvailable = true;
        }
        if (isAvailable) {
            RecordMode recordModeCode;
            //Get Latest Term
            Iterator iter = policyHeader.getPolicyTerms();
            Term lastTerm = null;
            if (iter.hasNext()) {
                lastTerm = (Term) iter.next();
            }
            //check coverage class
            if (record.hasStringValue(CoverageClassFields.COVERAGE_CLASS_BASE_RECORD_ID)) {
                if (record.getStringValue("coverageClassStatus").equals("CANCEL") && !policyHeader.isWipB() &&
                    !lastTerm.isRenewalQuoteExists()) {
                    isReinstateAvailable = true;
                }
            }
            // check coverage
            else if (record.hasStringValue(CoverageFields.COVERAGE_BASE_RECORD_ID)) {
                recordModeCode = PMCommonFields.getRecordModeCode(record);
                if (record.getStringValue("coverageStatus").equals("CANCEL") && !policyHeader.isWipB() &&
                    !(screenMode.isRenewWIP() && recordModeCode.isTemp())&& !lastTerm.isRenewalQuoteExists()) {
                    isReinstateAvailable = true;
                }
            }
            //check risk
            else if (record.hasStringValue(RiskFields.RISK_BASE_RECORD_ID)) {
                recordModeCode = PMCommonFields.getRecordModeCode(record);
                if (RiskFields.getRiskStatus(record).isCancelled() && !policyHeader.isWipB() &&
                    !(screenMode.isRenewWIP() && recordModeCode.isTemp()) && !lastTerm.isRenewalQuoteExists()) {
                    isReinstateAvailable = true;
                }
            }
        }

        l.exiting(getClass().getName(), "isReinstateAvailableForRiskCoverageAndClass", YesNoFlag.getInstance(isReinstateAvailable));
        return isReinstateAvailable;
    }

    /**
     * Check Cancle option status for Risk, Coverage and Coverage class
     *
     * @param record
     * @return
     */
    private boolean isCancelAvailableForRiskCoverageAndClass(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "isCancelAvailableForRiskCoverageAndClass", record);
        boolean isCancelAvailable = false;
        boolean isAvailable = false;
        RecordSet recordSet;
        recordSet = new RecordSet();

        //for policy page
        PolicyHeader policyHeader = getPolicyHeader();
        PolicyViewMode viewMode     = policyHeader.getPolicyIdentifier().getPolicyViewMode(); //PolicyViewMode.OFFICIAL,PolicyViewMode.WIP
        PolicyStatus policyStatus = getPolicyHeader().getPolicyStatus();

        if (policyStatus.isCancelled() && viewMode.equals(PolicyViewMode.OFFICIAL)) {
            isCancelAvailable = false;
        }
        else {
            // First Check if cancel is available for policy
            boolean isCancelAvailableForPolicy = isCancelAvailableForPolicy(recordSet);
            ScreenModeCode screenMode   = policyHeader.getScreenModeCode();

            // the quote or policy must be saved as official or is in Renewal WIP mode
            if (viewMode.isOfficial() || screenMode.isRenewWIP()) {
                if (isCancelAvailableForPolicy || getTransactionManager().isAutoPendingRenewalEnable(policyHeader)) {
                    isAvailable = true;
                }
            }
            //the primary Risk/Coverage can not be cancelled
            //temp record in renewal WIP mode can not be cancelled
            if (isAvailable) {
                RecordMode recordModeCode;
                //check coverage class
                if (record.hasStringValue(CoverageClassFields.COVERAGE_CLASS_BASE_RECORD_ID)) {
                    if (record.getStringValue("coverageClassStatus").equals("ACTIVE")) {
                        isCancelAvailable = true;
                    }
                }
                // check coverage
                else if (record.hasStringValue(CoverageFields.COVERAGE_BASE_RECORD_ID)) {
                    recordModeCode = PMCommonFields.getRecordModeCode(record);
                    PMStatusCode coverageStatus = CoverageFields.getCoverageStatus(record);
                    if ((coverageStatus.isActive() || coverageStatus.isConverted()) &&
                        !YesNoFlag.getInstance(record.getStringValue("primaryCoverageB")).booleanValue() &&
                        !(screenMode.isRenewWIP() && recordModeCode.isTemp())) {
                        isCancelAvailable = true;
                    }
                }
                //check risk
                else if (record.hasStringValue(RiskFields.RISK_BASE_RECORD_ID)) {
                    recordModeCode = PMCommonFields.getRecordModeCode(record);
                    String riskAddCode = RiskFields.getAddCode(record);
                    if (RiskFields.getRiskStatus(record).isActive() &&
                        (!YesNoFlag.getInstance(record.getStringValue("primaryRiskB")).booleanValue() ||
                            "SLOT".equals(riskAddCode)) &&
                        !(screenMode.isRenewWIP() && recordModeCode.isTemp())) {
                        isCancelAvailable = true;
                    }
                }
            }
        }
        l.exiting(getClass().getName(), "isCancelAvailableForRiskCoverageAndClass", YesNoFlag.getInstance(isCancelAvailable));
        return isCancelAvailable;
    }

    /**
     * Check Change option status
     *
     * @param record
     * @return
     */
    private boolean isChangeAvailableForRiskCoverageAndClass(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "isChangeAvailableForRiskCoverageAndClass", record);

        boolean isChangeAvailable = false;
        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        // Fix issue 102697, during OOSWIP, it is visible only in the OOSE initiated term.
        if (screenMode.isOosWIP() && getPolicyHeader().isInitTermB()) {
            isChangeAvailable = true;
        }

        String transEffDateString = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        Date transEffDate = DateUtils.parseDate(transEffDateString);
        String effFromDateString = null;
        PMStatusCode recordStatus = null;
        if (record.hasStringValue(CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_FROM_DATE)) {
            effFromDateString = CoverageClassFields.getCoverageClassEffectiveFromDate(record);
            recordStatus = CoverageClassFields.getCoverageClassStatus(record);
        }
        else if (record.hasStringValue(CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE)) {
            effFromDateString = CoverageFields.getCoverageEffectiveFromDate(record);
            recordStatus = CoverageFields.getCoverageStatus(record);
        }
        else if (record.hasStringValue(RiskFields.RISK_EFFECTIVE_FROM_DATE)) {
            effFromDateString = RiskFields.getRiskEffectiveFromDate(record);
            recordStatus = RiskFields.getRiskStatus(record);
        }
        // If the transaction effective from date before the effective from date, Change option should be hidden.
        if (isChangeAvailable && !StringUtils.isBlank(effFromDateString)) {
            Date effFromDate = DateUtils.parseDate(effFromDateString);
            if (transEffDate.before(effFromDate)) {
                isChangeAvailable = false;
            }
        }
        // Issue 109789, the Change option should be hidden for canceled record.
        if(isChangeAvailable && recordStatus != null && recordStatus.isCancelled()){
            isChangeAvailable = false;
        }

        l.exiting(getClass().getName(), "isChangeAvailableForRiskCoverageAndClass");
        return isChangeAvailable;
    }

    /**
     * Check Cancel option status for Policy
     *
     * @param recordSet
     * @return
     */
    private boolean isCancelAvailableForPolicy(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isCancelAvailableForPolicy", new Object[]{recordSet});

        boolean isCancelAvailable = false;

        //for policy page
        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isViewPolicy()) {
            PolicyStatus policyStatus = policyHeader.getPolicyStatus();
            PolicyStatus termStatus = policyHeader.getTermStatus();
            if (policyStatus.isActive()) {
                if (policyHeader.isWipB()) {
                    return isCancelAvailable;
                }
                else if (!policyHeader.isQuoteRenewalExists()) {
                    ProcessStatus processStatus = policyHeader.getProcessStatusCode();
                    if (processStatus.isProcessStatusCancelOnly()) {
                        if (termStatus.isActive() || termStatus.isExpired()) {
                            isCancelAvailable = true;
                        }
                    }
                    else if (!policyHeader.isLastTerm()) {
                        if (termStatus.isActive() || termStatus.isExpired()) {
                            if (policyHeader.isValidOosTerm()) {
                                isCancelAvailable = true;
                            }
                        }
                    }
                    else {
                        if (!policyHeader.isShortTermB()) {
                            isCancelAvailable = true;
                        }
                    }
                }
            }
            else if (policyStatus.isPending() && !termStatus.isCancelled() && !policyHeader.isWipB()) {
                if (!policyHeader.isQuoteRenewalExists()) {
                    isCancelAvailable = true;
                }
            }
        }
        else if (screenMode.isRenewWIP() && policyHeader.isInitTermB()) {
            if (policyHeader.getPolicyIdentifier().ownLock()) {
                isCancelAvailable = true;
            }
        }

        l.exiting(getClass().getName(), "isCancelAvailableForPolicy", YesNoFlag.getInstance(isCancelAvailable));
        return isCancelAvailable;
    }

    /**
     * Check Cancel option status for Policy
     *
     * @param recordSet
     * @return
     */
    private boolean isCancelFlatAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isCancelFlatAvailable", new Object[]{recordSet});

        boolean isCancelAvailable = false;

        PolicyHeader policyHeader = getPolicyHeader();

        if (policyHeader.getScreenModeCode().isViewPolicy()) {
            if (policyHeader.getPolicyStatus().isActive()) {
                if (!policyHeader.isQuoteRenewalExists()) {
                    ProcessStatus processStatus = policyHeader.getProcessStatusCode();
                    if (!processStatus.isProcessStatusCancelOnly() && policyHeader.isLastTerm()) {
                        if (policyHeader.isShortTermB()) {
                            isCancelAvailable = true;
                        }
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "isCancelFlatAvailable", YesNoFlag.getInstance(isCancelAvailable));
        return isCancelAvailable;
    }

    /**
     * Check Delete option status
     *
     * @param record
     * @return
     */
    private boolean isDeleteAvailable(Record record) {
        boolean isVisible = false;

        // General Rule - enable deletion only if the record mode is not OFFICIAL
        if (PMCommonFields.hasRecordModeCode(record)) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            if (!recordModeCode.isOfficial()) {
                isVisible = true;
            }
        }

        // Policy Tab specific rule - disable deletion if screen mode is view-only or the record is in cancel status
        if (isVisible) {
            PMStatusCode recordStatus = null;
            if (record.hasStringValue(CoverageClassFields.COVERAGE_CLASS_BASE_RECORD_ID)
                && record.hasField(CoverageClassFields.COVERAGE_CLASS_STATUS))
                recordStatus = CoverageClassFields.getCoverageClassStatus(record);
            else if (record.hasStringValue(CoverageFields.COVERAGE_BASE_RECORD_ID)
                && record.hasField(CoverageFields.COVERAGE_STATUS))
                recordStatus = CoverageFields.getCoverageStatus(record);
            else if (record.hasStringValue(RiskFields.RISK_BASE_RECORD_ID)
                && record.hasField(RiskFields.RISK_STATUS))
                recordStatus = RiskFields.getRiskStatus(record);

            PolicyHeader policyHeader = getPolicyHeader();
            ScreenModeCode screenMode = policyHeader.getScreenModeCode();
            if (recordStatus != null && recordStatus.isCancelled() && !screenMode.isRenewWIP()||
                screenMode.isViewEndquote() || screenMode.isViewPolicy() ||
                screenMode.isCancelWIP() || screenMode.isResinstateWIP() ||
                (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !policyHeader.isInitTermB()) {
                isVisible = false;
            }
        }

        return isVisible;
    }

    /**
     * Check Delete WIP option status
     *
     * @param recordSet
     * @return
     */
    private boolean isDeleteWipAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isDeleteWipAvailable", new Object[]{recordSet});
        boolean isVisible = false;
        PolicyViewMode viewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode(); //PolicyViewMode.OFFICIAL,PolicyViewMode.WIP

        if (viewMode.equals(PolicyViewMode.OFFICIAL)) {
            isVisible = false;   // in official mode, the deleteWIP option is hidden
        }
        else {
            if (getPolicyHeader().getLastTransactionInfo().getTransactionStatusCode().isInProgress()) {
                isVisible = true;
                TransactionCode transactionCode = getPolicyHeader().getLastTransactionInfo().getTransactionCode();
                if (transactionCode.isNewBus() &&
                    SysParmProvider.getInstance().getSysParm("PM_NBDEL_BY_SECPROF", "N").equalsIgnoreCase("Y")) {
                    boolean hasProfile = false;
                    try {
                        hasProfile = UserSessionManager.getInstance().getUserSession().getOasisUser().hasProfile("PM_NEWBUS_WIP_DELETES");
                    }
                    catch (Exception e) {
                        l.throwing(getClass().getName(), "postProcessRecordSet", e);
                        throw new AppException("errors occurred when determing if user has the profile PM_NEWBUS_WIP_DELETES");
                    }
                    if (!hasProfile) {
                        isVisible = false;
                    }
                }
            }
        }

        // It is visible only in the initiated term during OOSE WIP and renewal WIP.
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if((screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()){
            isVisible = false;
        }

        l.exiting(getClass().getName(), "isDeleteWipAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    /**
     * Check Delete WIP option status
     *
     * @param recordSet
     * @return
     */
    private boolean isPreviewAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isPreviewAvailable", new Object[]{recordSet});
        boolean isDeleteWipAvailable = isDeleteWipAvailable(recordSet);
        boolean isVisible = isDeleteWipAvailable;

        if (!isDeleteWipAvailable) {
            // This could be an OOS transaction.
            // Show Preview button only on terms after the trans effective date and the view mode is WIP.
            l.info("Policy View Mode:" + getPolicyHeader().getPolicyIdentifier().getPolicyViewMode());
            if (PolicyViewMode.WIP.equals(getPolicyHeader().getPolicyIdentifier().getPolicyViewMode())) {
                ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
                if (screenMode.isOosWIP()) {
                    if (DateUtils.dateDiff(DateUtils.DD_DAYS, DateUtils.parseDate(getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate()),
                            DateUtils.parseDate(getPolicyHeader().getTermEffectiveFromDate())) > 0) {
                        isVisible = true;
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "isPreviewAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    /**
     * Check Endorsement option status
     *
     * @param recordSet
     * @return
     */
    private boolean isEndorsementAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isEndorsementAvailable", new Object[]{recordSet});
        boolean isEndorsementVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();

        if (!viewMode.isOfficial()) {
            isEndorsementVisible = false;
        }
        else if (policyHeader.getScreenModeCode().isViewPolicy()) {
            PolicyStatus policyStatus = policyHeader.getPolicyStatus();
            if (policyStatus.isActive()) {
                if (policyHeader.isWipB()) {
                    isEndorsementVisible = getTransactionManager().isAutoPendingRenewalEnable(policyHeader);
                }
                else if (!policyHeader.isQuoteRenewalExists()) {
                    if (policyHeader.getProcessStatusCode().isProcessStatusCancelOnly()) {
                        if (policyHeader.getTermStatus().isActive()) {
                            isEndorsementVisible = true;
                        }
                    }
                    else if (policyHeader.isLastTerm() && !policyHeader.isShortTermB()) {
                        isEndorsementVisible = true;
                    }
                }
            }
            else if (policyStatus.isPending()) {
                if (!policyHeader.isQuoteRenewalExists() && !policyHeader.isWipB()) {
                    isEndorsementVisible = true;
                }
            }
        }

        l.exiting(getClass().getName(), "isEndorsementAvailable", Boolean.valueOf(isEndorsementVisible));
        return isEndorsementVisible;
    }

    /**
     * check re-rate status
     *
     * @return if available
     */
    private boolean isReRateAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isReRateAvailable", new Object[]{recordSet});
        boolean isReRateAvailable = false;

        PolicyHeader policyHeader = getPolicyHeader();
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        PolicyStatus policyStatus = policyHeader.getPolicyStatus();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        if (screenMode.isViewPolicy() && viewMode.isOfficial() && policyStatus.isActive()
            && !policyHeader.isQuoteRenewalExists() && !policyHeader.isWipB()
            && !policyHeader.getProcessStatusCode().isProcessStatusCancelOnly() && isLastTerm()
            && !policyHeader.isShortTermB() && policyHeader.getPolicyCycleCode().isPolicy()) {
            isReRateAvailable = true;
        }
        l.exiting(getClass().getName(), "isReRateAvailable", Boolean.valueOf(isReRateAvailable));
        return isReRateAvailable;
    }

    /**
     * Check OOS Endorsement option status
     *
     * @param recordSet
     * @return
     */
    private boolean isOosEndorsementAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isOosEndorsementAvailable", new Object[]{recordSet});
        boolean isOosEndorsementVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();

        if (!viewMode.isOfficial()) {
            isOosEndorsementVisible = false;
        }
        else if (policyHeader.getScreenModeCode().isViewPolicy()) {
            if (policyHeader.getPolicyStatus().isActive()) {
                ProcessStatus processStatus = policyHeader.getProcessStatusCode();
                if (policyHeader.isWipB()) {
                    isOosEndorsementVisible = getTransactionManager().isAutoPendingRenewalEnable(policyHeader);
                }
                else if (!policyHeader.isQuoteRenewalExists() && !processStatus.isProcessStatusCancelOnly()) {
                    if (!policyHeader.isLastTerm()) {
                        PolicyStatus termStatus = policyHeader.getTermStatus();
                        if (termStatus.isActive() || termStatus.isExpired()) {
                            if (policyHeader.isValidOosTerm()) {
                                isOosEndorsementVisible = true;
                            }
                        }
                    }
                    else {
                        PolicyCycleCode policyCycleCode = policyHeader.getPolicyCycleCode();
                        if (!policyHeader.isShortTermB() && policyCycleCode.isPolicy()) {
                            isOosEndorsementVisible = true;
                        }
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "isOosEndorsementAvailable", Boolean.valueOf(isOosEndorsementVisible));
        return isOosEndorsementVisible;
    }

    private boolean isPurgeAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isPurgeAvailable", new Object[]{recordSet});
        boolean isVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();

        if (!viewMode.isOfficial()) {
            return isVisible;
        }
        else if (policyHeader.getScreenModeCode().isViewPolicy()) {
            if (policyHeader.getPolicyStatus().isActive()) {
                if (policyHeader.isWipB()) {
                    return isVisible;
                }
                else if (!policyHeader.isQuoteRenewalExists()) {
                    ProcessStatus processStatus = policyHeader.getProcessStatusCode();
                    if (!processStatus.isProcessStatusCancelOnly() && policyHeader.isLastTerm()) {
                        if (policyHeader.isValidPurgeCandidate()) {
                            isVisible = true;
                        }
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "isPurgeAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    /**
     * Check Common Multi Cancel option status<p>
     * 
     * @param recordSet
     * @return
     */
    private boolean isCommonMultiCancelAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isCommonMultiCancelAvailable", new Object[]{recordSet});
        boolean isVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isViewPolicy()) {
            PolicyStatus policyStatus = policyHeader.getPolicyStatus();
            PolicyStatus termStatus = policyHeader.getTermStatus();
            ProcessStatus processStatus = policyHeader.getProcessStatusCode();
            if (policyStatus.isActive()) {
                if (policyHeader.isWipB()) {
                    return isVisible;
                }
                else if (!policyHeader.isQuoteRenewalExists() && !processStatus.isProcessStatusCancelOnly()) {
                    if (!policyHeader.isLastTerm()) {
                        if (termStatus.isActive() || termStatus.isExpired()) {
                            if (policyHeader.isValidOosTerm()) {
                                isVisible = true;
                            }
                        }
                    }
                    else {
                        if (!policyHeader.isShortTermB()) {
                            isVisible = true;
                        }
                    }
                }
            }
            else if (policyStatus.isPending() && !termStatus.isCancelled()) {
                if (!policyHeader.isQuoteRenewalExists() && !policyHeader.isWipB()) {
                    isVisible = true;
                }
            }
        }

        l.exiting(getClass().getName(), "isCommonMultiCancelAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }
    
    /**
     * Check Multi Cancel option status<p>
     * It is available when policy is Renewal WIP or isCommonMultiCancelAvailable() is available. 
     * 
     * @param recordSet
     * @return
     */
    private boolean isMultiCancelAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isMultiCancelAvailable", new Object[]{recordSet});
        boolean isVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        if (screenMode.isRenewWIP() && policyHeader.isInitTermB()) {
            isVisible = true;
        } 
        else {
            isVisible = isCommonMultiCancelAvailable(recordSet);
        }
        
        l.exiting(getClass().getName(), "isMultiCancelAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }
    
    /**
     * check extend term status
     * <p/>
     * IF policy is not locked
     * And IF screen mode = VIEWPOLICY
     * And IF policy status = ACTIVE
     * And IF process status is NOT CANCELONLY
     * And IF there is no existing RENEWAL QUOTE on the policy
     * <p/>
     * And IF the current term displayed is the LAST term
     * And IF it is NOT a short term policy
     * And IF it is a policy (policy cycle is POLICY)
     *
     * @param recordSet input recordset
     * @return if available
     */
    private boolean isExtendCancelAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isExtendCancelAvailable", new Object[]{recordSet});
        boolean isVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isViewPolicy()) {
            PolicyStatus policyStatus = policyHeader.getPolicyStatus();
            if (policyStatus.isActive()) {
                if (policyHeader.isWipB()) {
                    return isVisible;
                }
                else if (!policyHeader.isQuoteRenewalExists()) {
                    ProcessStatus processStatus = policyHeader.getProcessStatusCode();
                    if (!processStatus.isProcessStatusCancelOnly() && policyHeader.isLastTerm()) {
                        PolicyCycleCode policyCycleCode = policyHeader.getPolicyCycleCode();
                        if (!policyHeader.isShortTermB() && policyCycleCode.isPolicy()) {
                            isVisible = true;
                        }
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "isExtendCancelAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    /**
     * Check Save option status
     *
     * @param recordSet
     * @return
     */
    private boolean isSaveAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isSaveAvailable", new Object[]{recordSet});
        boolean isVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isManualEntry()) {
            if (policyHeader.isWipB()) {
                if (policyHeader.getPolicyIdentifier().ownLock()) {
                    isVisible = true;
                }
            }
            else {
                isVisible = true;
            }
        }
        else if (screenMode.isOosWIP() || screenMode.isWIP() || screenMode.isResinstateWIP() ||
            screenMode.isRenewWIP()) {
            if (policyHeader.getPolicyIdentifier().ownLock()) {
                isVisible = true;
            }
        }
        else if (screenMode.isCancelWIP()) {
            if (policyHeader.getPolicyIdentifier().ownLock() && policyHeader.isInitTermB()) {
                isVisible = true;
            }
        }

        // visible only in the initiated term during OOSE WIP and renewal WIP
        if((screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()){
            isVisible = false;
        }


        l.exiting(getClass().getName(), "isSaveAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    /**
     * Check Renewal option status
     *
     * @param recordSet
     * @return
     */
    private boolean isRenewalAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isRenewalAvailable", new Object[]{recordSet});
        boolean isRenewalVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        String renewalIndicatorCode = policyHeader.getRenewalIndicatorCode();
        PolicyStatus policyStatus = policyHeader.getPolicyStatus();

        if (renewalIndicatorCode != null && screenMode.isViewPolicy() && policyStatus.isActive()) {
            if (!policyHeader.isQuoteRenewalExists()) {
                ProcessStatus processStatus = policyHeader.getProcessStatusCode();
                PolicyCycleCode policyCycleCode = policyHeader.getPolicyCycleCode();
                if (!processStatus.isProcessStatusCancelOnly() && policyHeader.isLastTerm() &&
                    !policyHeader.isShortTermB() && policyCycleCode.isPolicy() &&
                    renewalIndicatorCode.equalsIgnoreCase("MANUAL") && policyHeader.isValidRenewalCandidate()) {
                    isRenewalVisible = true;
                }
            }
        }

        // Issue 109978, the Renewal option should be hidden if the policy in WIP and locked by others.
        if (policyHeader.isWipB() && !policyHeader.getPolicyIdentifier().ownLock()) {
            isRenewalVisible = false;
        }

        l.exiting(getClass().getName(), "isRenewalAvailable", Boolean.valueOf(isRenewalVisible));
        return isRenewalVisible;
    }

    /**
     * Check Auto Renewal option status
     *
     * @param recordSet
     * @return boolean
     */
    private boolean isAutoRenewalAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isAutoRenewalAvailable", new Object[]{recordSet});
        boolean isAutoRenewalVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        String renewalIndicatorCode = policyHeader.getRenewalIndicatorCode();
        PolicyStatus policyStatus = policyHeader.getPolicyStatus();

        boolean isAutoRenewEnabled = YesNoFlag.getInstance(
            SysParmProvider.getInstance().getSysParm(SysParmIds.AUTORENEW, "N")).booleanValue();

        if (renewalIndicatorCode != null && screenMode.isViewPolicy() && policyStatus.isActive()) {
            if (!policyHeader.isQuoteRenewalExists()) {
                ProcessStatus processStatus = policyHeader.getProcessStatusCode();
                PolicyCycleCode policyCycleCode = policyHeader.getPolicyCycleCode();
                if (!processStatus.isProcessStatusCancelOnly() && policyHeader.isLastTerm() &&
                    !policyHeader.isShortTermB() && policyCycleCode.isPolicy() &&
                    renewalIndicatorCode.equalsIgnoreCase("AUTO") && policyHeader.isValidRenewalCandidate()
                    && isAutoRenewEnabled) {
                    isAutoRenewalVisible = true;
                }
            }
        }

        l.exiting(getClass().getName(), "isAutoRenewalAvailable", Boolean.valueOf(isAutoRenewalVisible));
        return isAutoRenewalVisible;
    }

    private boolean isReactiveAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isReactiveAvailable", new Object[]{recordSet});
        boolean isVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isViewPolicy() && policyHeader.getPolicyCycleCode().isQuote() && !policyHeader.isShortTermB()) {
            if (policyHeader.getTermStatus().isDenied())
                isVisible = true;
        }
        l.exiting(getClass().getName(), "isReactiveAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    private boolean isCopyDenyAcceptQuoteAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isCopyDenyAcceptQuoteAvailable", new Object[]{recordSet});
        boolean isVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isViewPolicy() && policyHeader.getPolicyCycleCode().isQuote()) {
            if (policyHeader.getPolicyStatus().isHold()) {
                if (policyHeader.getPolicyCycleCode().isPolicy()) {
                    PolicyStatus policyTermStatus = policyHeader.getTermStatus();
                    if (!policyTermStatus.isCancelled()) {
                        isVisible = true;
                    }
                }
            }
            else if (policyHeader.getTermStatus().isActive() && !policyHeader.isShortTermB()) {
                ProcessStatus processStatus = policyHeader.getProcessStatusCode();
                if (!processStatus.isProcessStatusCancelOnly() && policyHeader.isLastTerm()) {
                    if (!policyHeader.isQuoteTempVersionExists()) {
                        SysParmProvider sysParm = SysParmProvider.getInstance();
                        String sysparmPmAcceptAgain = sysParm.getSysParm(SysParmIds.PM_ACCEPT_AGAIN,"N");
                        if (sysparmPmAcceptAgain.equals("Y")) {
                            isVisible = true;
                        }
                        else {
                            if (!policyHeader.isQuoteEndorsementExists()) {
                                isVisible = true;
                            }
                        }
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "isCopyDenyAcceptQuoteAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    private boolean isCopyDenyAcceptQuoteMenuAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isCopyDenyAcceptQuoteMenuAvailable", new Object[]{recordSet});

        PolicyHeader policyHeader = getPolicyHeader();
        boolean isVisible = isCopyDenyAcceptQuoteAvailable(recordSet);

        // disable CopyDenyAccept Quote Menu if configured.
        if (PolicyAttributesFactory.getInstance().isCopyDenyAcceptQuoteMenuDisable(
            policyHeader.getTermEffectiveFromDate(),
            policyHeader.getPolicyTypeCode(),
            policyHeader.getQuoteCycleCode())) {
            isVisible = false;
        }

        l.exiting(getClass().getName(), "isCopyDenyAcceptQuoteMenuAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    private boolean isCopyDenyAcceptQuoteButtonAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isCopyDenyAcceptQuoteButtonAvailable", new Object[]{recordSet});

        PolicyHeader policyHeader = getPolicyHeader();
        boolean isVisible = isCopyDenyAcceptQuoteAvailable(recordSet);

        // disable CopyDenyAccept Quote Button if configured.
        if (PolicyAttributesFactory.getInstance().isCopyDenyAcceptQuoteButtonDisable(
            policyHeader.getTermEffectiveFromDate(),
            policyHeader.getPolicyTypeCode(),
            policyHeader.getQuoteCycleCode())) {
            isVisible = false;
        }

        l.exiting(getClass().getName(), "isCopyDenyAcceptQuoteButtonAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    /**
     * Check Chg Term Dates option status
     *
     * @param recordSet
     * @return
     */
    private boolean isChangeTermEffDateAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isChangeTermEffDateAvailable", new Object[]{recordSet});
        boolean isVisible = false;

        TransactionCode transactionCode = getPolicyHeader().getLastTransactionInfo().getTransactionCode();
        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isManualEntry()) {
            if (policyHeader.isWipB()) {
                if (policyHeader.getPolicyIdentifier().ownLock() && !transactionCode.isReissue()) {
                    isVisible = true;
                }
            }
            else {
                isVisible = true;
            }
        }

        l.exiting(getClass().getName(), "isChangeTermEffDateAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    private boolean isChangePolicyAdminAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isChangePolicyAdminAvailable");

        boolean isVisible = false;
        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isManualEntry()) {
            if (policyHeader.isWipB()) {
                if (policyHeader.getPolicyIdentifier().ownLock()) {
                    isVisible = true;
                }
            }
            else {
                isVisible = true;
            }
        }
        else if ((screenMode.isOosWIP() || screenMode.isRenewWIP()) && policyHeader.isInitTermB() ||
            screenMode.isWIP()) {
            PolicyCycleCode policyCycleCode = policyHeader.getPolicyCycleCode();
            if (policyHeader.getPolicyIdentifier().ownLock() && policyCycleCode.isPolicy()) {
                isVisible = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isChangePolicyAdminAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    /**
     * Check Reissue option status
     *
     * @param recordSet
     * @return
     */
    private boolean isReissueAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isReissueAvailable", new Object[]{recordSet});
        boolean isAvailable = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isViewPolicy()) {
            PolicyStatus policyStatus = policyHeader.getPolicyStatus();
            if (policyStatus.isActive()) {
                if (!policyHeader.isQuoteRenewalExists() && policyHeader.isLastTerm()) {
                    if (policyHeader.isShortTermB()) {
                        isAvailable = true;
                    }
                }
            }
            else if (policyStatus.isCancelled()) {
                PolicyCycleCode policyCycleCode = policyHeader.getPolicyCycleCode();
                if (policyCycleCode.isPolicy() && !policyHeader.isWipB()) {
                    ProcessStatus processStatus = policyHeader.getProcessStatusCode();
                    if (!processStatus.isProcessStatusCancelOnly() && policyHeader.isLastTerm()) {
                        isAvailable = true;
                    }
                    else if (processStatus.isProcessStatusCancelOnly()) {
                        if (SysParmProvider.getInstance().getSysParm("PM_REISSUE_CANCLONLY", "N").equalsIgnoreCase("Y")) {
                            isAvailable = true;
                        }
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "isReissueAvailable", Boolean.valueOf(isAvailable));
        return isAvailable;
    }

    private boolean isCopy2QuoteAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isCopy2QuoteAvailable");
        boolean isVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        PolicyCycleCode policyCycleCode = policyHeader.getPolicyCycleCode();

        Iterator iter = policyHeader.getPolicyTerms();
        if (iter.hasNext()) {
            // The Terms are order in descending order, so the first in the list is the last term.
            Term lastTerm = (Term) iter.next();
            //lastPolicyTermHistoryId = lastTerm.getPolicyTermHistoryId();
        }

        if (screenMode.isViewPolicy()) {
            PolicyStatus policyStatus = policyHeader.getPolicyStatus();
            if (policyCycleCode.isPolicy() && policyStatus.isActive() && policyHeader.isLastTerm()) {
                isVisible = true;
            }
        }
        else if (screenMode.isOosWIP() || screenMode.isWIP() || screenMode.isCancelWIP()) {
            if (policyCycleCode.isPolicy() && policyHeader.isLastTerm()) {
                isVisible = true;
            }
        }
        else if (screenMode.isRenewWIP()) {
            if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_COPY2QUOTE_RN, "N")).booleanValue() &&
                policyCycleCode.isPolicy() && policyHeader.isLastTerm()) {
                isVisible = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCopy2QuoteAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    private boolean isBillingSetupAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isBillingSetupAvailable");

        boolean isVisible = getTransactionManager().isBillingSetupAvailable(getPolicyHeader());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isBillingSetupAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    private boolean isAdjPremiumAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isAdjPremiumAvailable");
        boolean isVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isCancelWIP()) {
            PolicyCycleCode policyCycle = policyHeader.getPolicyCycleCode();
            if (policyHeader.getPolicyIdentifier().ownLock() && policyHeader.isInitTermB() &&
                policyCycle.isPolicy()) {
                isVisible = true;
            }
        }
        else if (screenMode.isResinstateWIP()) {
            if (policyHeader.getPolicyIdentifier().ownLock()) {
                isVisible = true;
            }
        }
        // For OOS WIP transaction: If the term displayed is the term in which the OOS was initiated, enable the Adj Premium option.
        // If it is not the term in which the OOS was initiated, disable it.
        if (screenMode.isOosWIP()) {
            isVisible = policyHeader.getPolicyIdentifier().ownLock() && getPolicyHeader().isInitTermB();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAdjPremiumAvailable", Boolean.valueOf(isVisible));
        }

        return isVisible;
    }

    private boolean isCopyEndQuoteAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isCopyEndQuoteAvailable");
        boolean isVisible = false;
        PolicyHeader policyHeader = getPolicyHeader();
        Term currentTerm = policyHeader.getPolicyTerm(policyHeader.getPolicyTermHistoryId());
        boolean isWipAvailable = currentTerm.isWipExists();
        if ((!isWipAvailable) && (policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote())) {
            isVisible = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCopyEndQuoteAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    private boolean isDelEndQuoteAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isDelEndQuoteAvailable");
        boolean isVisible = false;
        PolicyHeader policyHeader = getPolicyHeader();
        Term currentTerm = policyHeader.getPolicyTerm(policyHeader.getPolicyTermHistoryId());
        boolean isWipAvailable = currentTerm.isWipExists();
        boolean isEndorsementQuoteAailable = currentTerm.isEndorsementQuoteExists();
        if ((!isWipAvailable) && (policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote()) && isEndorsementQuoteAailable) {
            isVisible = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isDelEndQuoteAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    private boolean isDelRenQuoteAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isDelRenQuoteAvailable");
        boolean isVisible = false;
        PolicyHeader policyHeader = getPolicyHeader();
        Term currentTerm = policyHeader.getPolicyTerm(policyHeader.getPolicyTermHistoryId());
        boolean isWipAvailable = currentTerm.isWipExists();
        boolean isRenewalQuoteAailable = currentTerm.isRenewalQuoteExists();
        if ((!isWipAvailable) && (policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote()) && isRenewalQuoteAailable) {
            isVisible = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isDelRenQuoteAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    /**
     * indicate if eApp is available for this policy
     *
     * @return boolean value indicate if app is available
     */
    private boolean isAppAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isAppAvailable");
        boolean isVisible = false;

        PolicyHeader policyHeader = getPolicyHeader();
        isVisible = policyHeader.getAppAvailableB();

        l.exiting(getClass().getName(), "isAppAvailable", Boolean.valueOf(isVisible));
        return isVisible;
    }

    /**
     * Indicate if quick quote option is available
     *
     * @return boolean
     */
    private boolean isQuickQuoteAvailable() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isQuickQuoteAvailable");
        }

        YesNoFlag nbQuickQuote = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_NB_QUICK_QUOTE, "Y"));
        boolean isVisible = false;
        PolicyHeader policyHeader = getPolicyHeader();

        if (((policyHeader.getPolicyCycleCode().isQuote() &&
              (policyHeader.getQuoteCycleCode().isNBQuote() ||
                  (policyHeader.getQuoteCycleCode().isRNQuote() && !nbQuickQuote.booleanValue())) &&
              ((policyHeader.getLastTransactionInfo().getTransactionCode().isQuote()) ||
                  (policyHeader.getLastTransactionInfo().getTransactionCode().isEndorsement() &&
                      !nbQuickQuote.booleanValue()))) ||
              (policyHeader.getPolicyCycleCode().isPolicy() && !nbQuickQuote.booleanValue() &&
              (policyHeader.getLastTransactionInfo().getTransactionCode().isEndorsement() ||
               policyHeader.getLastTransactionInfo().getTransactionCode().isNewBus() ||
               policyHeader.getLastTransactionInfo().getTransactionCode().isManualRenewal() ||
               policyHeader.getLastTransactionInfo().getTransactionCode().isAutoRenewal()))) &&
               policyHeader.isWipB()) {
            // Only display for new Quote WIP status
            isVisible = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isQuickQuoteAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    /**
     * Indicate if snapshot information exists for the current policy
     *
     * @return boolean
     */
    private boolean isSnapshotExist() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSnapshotExist");
        }

        boolean isExists = false;
        PolicyCacheManager policyCacheManager = PolicyCacheManager.getInstance();

        if (!policyCacheManager.hasSnapshotConfigured()) {
            policyCacheManager.setSnapshotConfigured(getTransactionManager().isSnapshotConfigured());
        }

        if (policyCacheManager.getSnapshotConfigured()) {
            PolicyHeader policyHeader = getPolicyHeader();
            if (!policyHeader.hasTransactionSnapshotCache()) {
                boolean isSnapshot = getTransactionManager().isTransactionSnapshotExist(policyHeader.toRecord());
                policyHeader.setTransactionSnapshotCache(String.valueOf(isSnapshot));
            }
            isExists = Boolean.valueOf(policyHeader.getTransactionSnapshotCache()).booleanValue();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSnapshotExist", Boolean.valueOf(isExists));
        }

        return isExists;
    }

    /**
     * Return a Record of initial entitlement values for a new record on the common tab pages.
     */
    public synchronized static Record getInitialEntitlementValuesForCommonTabs() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            c_initialEntitlementValues.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue("isCancelAvailable", YesNoFlag.N);
            c_initialEntitlementValues.setFieldValue("isReinstateAvailable", YesNoFlag.N);
            c_initialEntitlementValues.setFieldValue("isOosChangeAvailable", YesNoFlag.N);
        }
        return c_initialEntitlementValues;
    }

    /**
     * indicate if the current term is the last term
     *
     * @return boolean value indicate the current term is the last term
     */
    protected boolean isLastTerm() {
        if (m_isLastTerm == null) {
            boolean isLastTerm = true;

            PolicyHeader policyHeader = getPolicyHeader();
            PolicyStatus policyTermStatus = policyHeader.getTermStatus();

            //check is last term
            Iterator policyTerms = policyHeader.getPolicyTerms();
            long currentTermId = Long.parseLong(policyHeader.getPolicyIdentifier().getTermBaseRecordId());
            while (policyTerms.hasNext()) {
                Term term = (Term) policyTerms.next();
                if (Long.parseLong(term.getTermBaseRecordId()) > currentTermId &&
                    (policyTermStatus.isActive() || policyTermStatus.isExpired())) {
                    isLastTerm = false;
                    break;
                }
            }

            m_isLastTerm = new Boolean(isLastTerm);
        }
        return m_isLastTerm.booleanValue();
    }

    /**
     * Check Undo Term option status
     *
     * @param recordSet
     * @return
     */
    private boolean isUndoTermAvailable(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "isUndoTermAvailable", new Object[]{recordSet});
        boolean isUndoTermAvailable = false;

        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        if (screenModeCode.isViewPolicy() && policyHeader.getPolicyStatus().isActive() &&
            (policyHeader.getPolicyCycleCode().isPolicy() || policyHeader.getPolicyCycleCode().isQuote())) {
            if (!policyHeader.hasUndoTermAvailableCache()) {
                boolean isUndo = getTransactionManager().isUndoTermAvailable(policyHeader);
                policyHeader.setUndoTermAvailableCache(String.valueOf(isUndo));
            }
            boolean isUndoTermCanBeDone = Boolean.valueOf(policyHeader.getUndoTermAvailableCache()).booleanValue();
            if (isUndoTermCanBeDone) {
                isUndoTermAvailable = true;
            }
        }

        l.exiting(getClass().getName(), "isUndoTermAvailable", Boolean.valueOf(isUndoTermAvailable));
        return isUndoTermAvailable;
    }

    /**
     * Check ACF option status
     *
     * @return
     */
    private boolean isAcfAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isAcfAvailable");
        boolean isAcfAvailable = false;
        if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_PROCESS_ACF", "N")).booleanValue()) {
            isAcfAvailable = true;
        }
        l.exiting(getClass().getName(), "isAcfAvailable", Boolean.valueOf(isAcfAvailable));
        return isAcfAvailable;
    }

    /**
     * Indicate if accept/decline short term policy option is available
     *
     * @return boolean value indicate if accept/decline option is available
     */
    private boolean isAcceptDeclinePolicyAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isAcceptDeclinePolicyAvailable");
        boolean isAcceptDeclinePolicyAvailable = false;

        PolicyHeader policyHeader = getPolicyHeader();
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();

        if (!viewMode.isOfficial()) {
            return isAcceptDeclinePolicyAvailable;
        }

        PolicyStatus policyStatus = policyHeader.getPolicyStatus();
        PolicyStatus termStatus = policyHeader.getTermStatus();
        PolicyCycleCode policyCycleCode = policyHeader.getPolicyCycleCode();
        if (policyCycleCode.isPolicy() && policyStatus.isHold() && !termStatus.isCancelled()) {
            isAcceptDeclinePolicyAvailable = true;
        }

        l.exiting(getClass().getName(), "isAcceptDeclinePolicyAvailable", Boolean.valueOf(isAcceptDeclinePolicyAvailable));
        return isAcceptDeclinePolicyAvailable;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public CommonTabsEntitlementRecordLoadProcessor() {
    }

    public CommonTabsEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        setPolicyHeader(policyHeader);
    }

    public void verifyConfig() {
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public TransactionManager getTransactionManager() {
        if (m_transactionManager == null) {
            m_transactionManager = (TransactionManager) ApplicationContext.getInstance().getBean(TransactionManager.BEAN_NAME);
        }
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public LockManager getLockManager() {
        if (m_lockManager == null) {
            m_lockManager = (LockManager) ApplicationContext.getInstance().getBean(LockManager.BEAN_NAME);
        }
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    private Boolean m_isLastTerm;
    private TransactionManager m_transactionManager;
    private PolicyHeader m_policyHeader;
    private LockManager m_lockManager;

    private static Record c_initialEntitlementValues;
}

