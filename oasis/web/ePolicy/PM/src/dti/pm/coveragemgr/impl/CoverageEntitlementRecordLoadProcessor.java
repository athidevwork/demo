package dti.pm.coveragemgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.security.Authenticator;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.ProcessStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for coverage web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 2, 2007
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/11/2007       fcb         Logic added for Mini Tail action item entitlements.
 * 01/03/2008       fcb         isAddCoverageAvailable: removed date condition as it was not consistent with UI/UC
 * 01/04/2008       fcb         postProcessRecordSet: isCoverageEffectiveToDateEditable, isDelCompAvailable,
 *                              isChgCompValueAvailable, isChgCompDateAvailable, isCycleDetailAvailable,
 *                              isSurchargePointsAvailable added to the pageEntitlementFields for 0 size recordset.
 * 01/23/2008       fcb         postProcessRecord: some settings added for OOSE.
 * 03/11/2008       yhchen      initialize pageentitlement fields, so that record has consistent columns
 * 05/13/2010       syang       107664 - Modified isAddCompAvailable() and postProcessRecord(), if the coverage
 *                              has been canceled already, system can't add component.
 * 06/29/2010       syang       108782 - Modified postProcessRecordSet() to set isCompUpdateAvailable to N
 *                              if recordset size is 0.
 * 01/27/2011       dzhang      116359 - Modified isAddCoverageAvailable().
 * 05/18/2011       dzhang      117246 - Modified isAddCoverageAvailable() and isAddCompAvailable().
 * 04/29/2011       gxc         105791 - Added CONVERTED related logic.
 * 05/03/2011       fcb         105791 - isConvertCoverageAvailable set.
 * 04/12/2012       syang       128498 - Added isSharedLimitBAvailable() to handle the Shared Limits field.
 * 06/25/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * 07/20/2012       sxm         135777 - Disable field if the record is not editable.
 * 08/10/2012       adeng       Issue 135971 - Modified isAddCoverageAvailable to check if the risk header is null.
 * 08/06/2013       awu         146878 - Changed isAddCoverageAvailable to display the Add button depends on riskCurrentStatus.
 * 10/08/2013       adeng       148512 - Modified isRecordSetReadOnly() to set recordSet to readonly during cancel WIP.
 * 09/26/2013       fcb         145725 - Added logic to check the profile in UserCacheManager via OasisUser.
 * 02/24/2014       xnie        148083 - Modified postProcessRecordSet() to
 *                                       1) Add field isRiskSumAvailable and isRiskAvailable when passed in recordSet's
 *                                          size is 0.
 *                                       2) Check if policy is initial loading, if does, call isRiskSumAvailable()
 *                                          to decide if the new risk page should be shown. Else, use existed value
 *                                          stored in user session to decide if the new risk page should be shown. The
 *                                          original risk page available is exclusive with new risk page.
 * 10/12/2014       jyang       157749 - Modified isAddCompAvailable to get isAddCompAvailable value from backend for
 *                                       both short term and long term coverages.
 * 02/26/2015       kxiang      161002 - Modified isAddCompAvailable to move checking cancel status to the end.
 * 01/06/2016       wdang       168069 - Removed the logic of isRiskSumAvailable/isRiskAvailable.
 * 07/26/2016       lzhang      169751 - Add logic to hidden or show IBNR From and IBNR To dates
 * 10/15/2018       wrong       188391 - Added isUnderlyingAvailable to support underlying coverage.
 * ---------------------------------------------------
 */
public class CoverageEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        // Determine if annual base rate is visible
        record.setFieldValue("isManuallyRated", isManuallyRated(record));
        // Determine if excess payor is visible
        record.setFieldValue("isRatePayorDependCode", isRatePayorDependCodeAvailable(record));
        // Determine if Add Component action item is show or hide
        record.setFieldValue("isAddCompAvailable", isAddCompAvailable(record));
        // Determine if Mini Tail action item is Enabled or Disabled
        record.setFieldValue("isMiniTailAvailable", isMiniTailAvailable(record));
        // Determine if Prior Act action item is Enabled or Disabled
        record.setFieldValue("isPriorActAvailable", YesNoFlag.N);
        // Determine if More action item is Enabled or Disabled
        record.setFieldValue("isMoreAvailable", isMoreAvailable(record));
        // For Manuscript Endorsement option
        record.setFieldValue("isManuscriptAvailable", isManuscriptAvailable(record));
        // For Schedule option
        record.setFieldValue("isScheduleAvailable", YesNoFlag.Y);
        // For Manual Excess Premium Layers option
        record.setFieldValue("isExcessPremiumAvailable", isExcessPremiumAvailable(record));
        // For Underlying option
        record.setFieldValue("isUnderlyingAvailable", YesNoFlag.Y);

        // Complex Rules (from [Policy_Folder_UI section 3.6.1.1]
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
        ScreenModeCode screenMode = getScreenModeCode();

        record.setFieldValue("isChgCompDateAvailable", YesNoFlag.N);
        record.setFieldValue("isChgCompValueAvailable", YesNoFlag.N);
        record.setFieldValue("isDelCompAvailable", YesNoFlag.N);
        record.setFieldValue("isCycleDetailAvailable", YesNoFlag.N);
        record.setFieldValue("isSurchargePointsAvailable", YesNoFlag.N);

        if (record.hasStringValue(CoverageFields.OFFICIAL_RECORD_ID)
            && !StringUtils.isBlank(CoverageFields.getOfficialRecordId(record))
            && recordModeCode.isTemp()) {
            YesNoFlag afterImageB = CoverageFields.getAfterImageRecordB(record);
            if (CoverageFields.getCoverageEffectiveFromDate(record).equals(CoverageFields.getCoverageEffectiveToDate(record))
                && getScreenModeCode().isRenewWIP()) {
                record.setEditIndicator(YesNoFlag.N);
                record.setFieldValue("isAddCompAvailable", YesNoFlag.N);
            }
            else if (afterImageB.booleanValue()) {
                if (getScreenModeCode().isOosWIP() && !recordModeCode.isRequest()) {
                    record.setEditIndicator(YesNoFlag.N);
                }
            }
            else if (!afterImageB.booleanValue()) {
                record.setEditIndicator(YesNoFlag.N);
            }
        }
        else {
            if (screenMode.isOosWIP()) {
                if (CoverageFields.hasCoverageStatus(record)) {
                    PMStatusCode status = CoverageFields.getCoverageStatus(record);
                    if ((status.isPending() || status.isActive() || status.isConverted())
                        && getPolicyHeader().isInitTermB()) {
                        record.setFieldValue("isChgCompValueAvailable", YesNoFlag.Y);
                        record.setFieldValue("isChgCompDateAvailable", YesNoFlag.Y);

                        // record is editable only when status is PENDING.
                        if (status.isPending()) {
                            record.setEditIndicator(YesNoFlag.Y);
                        }
                        record.setFieldValue("isAddCompAvailable", YesNoFlag.Y);
                    }
                    else if (status.isCancelled()) {
                        record.setEditIndicator(YesNoFlag.N);
                        record.setFieldValue("isAddCompAvailable", YesNoFlag.N);
                    }
                    else {
                        record.setEditIndicator(YesNoFlag.N);
                        record.setFieldValue("isAddCompAvailable", YesNoFlag.N);
                    }
                }
            }
        }
        // Fix issue 100751. Add component is visible for the OOS initialized term in WIP mode.
        // We should check the isAddCompAvailable again since it is cheked above.
        PolicyViewMode policyViewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode();
        PMStatusCode status = CoverageFields.getCoverageStatus(record);
        if (!status.isCancelled() && screenMode.isOosWIP()) {
            if (getPolicyHeader().isInitTermB() && policyViewMode.isWIP()) {
                record.setFieldValue("isAddCompAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isAddCompAvailable", YesNoFlag.N);
            }
        }

        // Determin if the retroactive date is enable based on record edit indicator
        String policyFormCode = CoverageFields.getPolicyFormCode(record);
        String isRetroDateEditable = "N";
        if (record.getEditIndicatorBooleanValue()) {
            String officialRecordId = null;
            if (record.hasStringValue(CoverageFields.OFFICIAL_RECORD_ID)) {
                officialRecordId = CoverageFields.getOfficialRecordId(record);
            }
            boolean hasProfile;

            // Check if user has the PM_EDIT_COVG_RETRO security profile
            try {
                hasProfile = UserSessionManager.getInstance().getUserSession().getOasisUser().hasProfile("PM_EDIT_COVG_RETRO");
            }
            catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if Retroactive Date is editable.", e);
                l.throwing(getClass().getName(), "postProcessRecord", ae);
                throw ae;
            }

            // Check editable of the retroactive date
            if (screenMode.isOosWIP()) {
                if (hasProfile) {
                    if ("CM".equals(policyFormCode) &&
                        (recordModeCode.isTemp() || recordModeCode.isRequest())) {
                        isRetroDateEditable = "Y";
                    }
                }
                else {
                    if ("CM".equals(policyFormCode) &&
                        recordModeCode.isTemp() && StringUtils.isBlank(officialRecordId)) {
                        isRetroDateEditable = "Y";
                    }

                }
            }
            else {
                if ("CM".equals(policyFormCode) && recordModeCode.isTemp()
                    && StringUtils.isBlank(officialRecordId)) {
                    isRetroDateEditable = "Y";
                }

            }
        }
        record.setFieldValue("isRetroDateEditable", isRetroDateEditable);

        // Determine editable of the effective to date based on record edit indicator
        YesNoFlag isEffToDateEditable = YesNoFlag.N;
        if (record.getEditIndicatorBooleanValue()) {
            isEffToDateEditable = YesNoFlag.getInstance(
            getCoverageManager().isEffectiveToDateEditable(getPolicyHeader(), record));
        }
        record.setFieldValue("isCoverageEffectiveToDateEditable", isEffToDateEditable);

        // Determine if other fields is editable based on record edit indicator
        record.setFieldValue("isClaimsMadeDateEditable", isClaimsMadeDateEditable(record));
        record.setFieldValue("isSharedLimitsBAvailable", isSharedLimitsBAvailable(record));

        boolean IBNRCovgB = CoverageFields.getIBNRCovgB(record).booleanValue();
        String isIBNRCovg = "N";
        if("CM".equals(policyFormCode) && IBNRCovgB){
            isIBNRCovg = "Y";
        }
        record.setFieldValue("isIBNRCovg", isIBNRCovg);

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    //determine if More action item is available
    private YesNoFlag isMoreAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;
        String ratingModuleCode = CoverageFields.getRatingModuleCode(record);
        if ((ratingModuleCode.equals("VL") || ratingModuleCode.equals("MVL"))
            && !CoverageFields.getExcessB(record).booleanValue())
        {
            isAvailable = YesNoFlag.Y;
        }
        return isAvailable;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isClaimsMadeDateEditable");
            pageEntitlementFields.add("isManuallyRated");
            pageEntitlementFields.add("isRetroDateEditable");
            pageEntitlementFields.add("isAddCompAvailable");
            pageEntitlementFields.add("isManuscriptAvailable");
            pageEntitlementFields.add("isScheduleAvailable");
            pageEntitlementFields.add("isUnderlyingAvailable");
            pageEntitlementFields.add("isRatePayorDependCode");
            pageEntitlementFields.add("isMiniTailAvailable");
            pageEntitlementFields.add("isPriorActAvailable");
            pageEntitlementFields.add("isCoverageEffectiveToDateEditable");
            pageEntitlementFields.add("isDelCompAvailable");
            pageEntitlementFields.add("isChgCompValueAvailable");
            pageEntitlementFields.add("isChgCompDateAvailable");
            pageEntitlementFields.add("isCycleDetailAvailable");
            pageEntitlementFields.add("isSurchargePointsAvailable");
            pageEntitlementFields.add("isMoreAvailable");
            pageEntitlementFields.add("isExcessPremiumAvailable");
            pageEntitlementFields.add("isSharedLimitsBAvailable");
            pageEntitlementFields.add("isIBNRCovg");
            recordSet.addFieldNameCollection(pageEntitlementFields);
            recordSet.getSummaryRecord().setFieldValue("isCompUpdateAvailable", "N");
        }
        else {
            recordSet.getSummaryRecord().setFieldValue("isCompUpdateAvailable", isCompUpdateAvailable());
        }

        // check add option available
        recordSet.getSummaryRecord().setFieldValue("isAddAvailable", isAddCoverageAvailable());
        // Set readOnly attribute to summary record
        if (isRecordSetReadOnly()) {
            EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), true);
        }
        recordSet.getSummaryRecord().setFieldValue("isConvertCoverageAvailable", YesNoFlag.N);
        // Exclude to set readonly for layers in "coverage page"
        // This is to logic to enable component section for problem coverage
        EntitlementFields.setHandleLayer(recordSet.getSummaryRecord(), isRecordSetHandleLayer());
    }

    /**
     * Set initial engitlement Values for Coverage
     *
     * @param coverageManager
     * @param policyHeader
     * @param screenModeCode
     * @param record
     */
    public static void setInitialEntitlementValuesForCoverage(
        CoverageManager coverageManager, PolicyHeader policyHeader, ScreenModeCode screenModeCode, Record record) {
        CoverageEntitlementRecordLoadProcessor entitlementRLP = new CoverageEntitlementRecordLoadProcessor(
            coverageManager, policyHeader, screenModeCode);
        // Set page entitlement values
        entitlementRLP.postProcessRecord(record, true);
    }

    private YesNoFlag isClaimsMadeDateEditable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;
        if (record.getEditIndicatorBooleanValue()) {
            isAvailable = YesNoFlag.getInstance(getCoverageManager().isClaimsMadeDateEditable(getPolicyHeader()));
        }
        return isAvailable;
    }

    private YesNoFlag isManuallyRated(Record record) {
        return YesNoFlag.getInstance(getCoverageManager().isManuallyRated(CoverageFields.getRatingModuleCode(record)));
    }

    private YesNoFlag isRatePayorDependCodeAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;
        if (record.hasStringValue(CoverageFields.RATE_PAYOR_DEPEND_B) && CoverageFields.getRatePayorDependB(record).booleanValue()) {
            isAvailable = YesNoFlag.Y;
        }
        return isAvailable;
    }

    /**
     * check if Add Coverage option is avaiable
     *
     * @return YesNoFlag
     */
    private YesNoFlag isAddCoverageAvailable() {
        YesNoFlag isAvailable = YesNoFlag.Y;

        // Add option is only available in several screen mode
        //Date riskEffDate = DateUtils.parseDate(getPolicyHeader().getRiskHeader().getEarliestContigEffectiveDate());
        //Date termEffDate = DateUtils.parseDate(getPolicyHeader().getTermEffectiveFromDate());
        RiskHeader riskHeader = getPolicyHeader().getRiskHeader();
        if (getScreenModeCode().isViewPolicy() || getScreenModeCode().isViewEndquote() ||
            getScreenModeCode().isCancelWIP() || getScreenModeCode().isResinstateWIP() ||
            (riskHeader != null && riskHeader.getCurrentRiskStatusCode().isCancelled()) ||
            (getScreenModeCode().isOosWIP() || getScreenModeCode().isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            isAvailable = YesNoFlag.N;
        }

        if (isAvailable.equals(YesNoFlag.Y)) {
            // Add option is unavailable, if either "riskBaseRecordId" or "practiceStateCode" is empty
            if (riskHeader == null) {
                // If riskHeader failed to load, "Add" option should be unavailable
                isAvailable = YesNoFlag.N;
            }
            else {
                String riskBaseRecordId = riskHeader.getRiskBaseRecordId();
                String practiceStateCode = riskHeader.getPracticeStateCode();
                if (StringUtils.isBlank(riskBaseRecordId) || StringUtils.isBlank(practiceStateCode)) {
                    isAvailable = YesNoFlag.N;
                }
            }
        }

        if (isAvailable.equals(YesNoFlag.Y)) {
            if (riskHeader != null && riskHeader.getDateChangeAllowedB().booleanValue()) {
                Record inputRecord = new Record();
                RiskFields.setRiskBaseRecordId(inputRecord, riskHeader.getRiskBaseRecordId());
                TransactionFields.setTransactionEffectiveFromDate(inputRecord, getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate());
                if (!(getCoverageManager().isAddCoverageAllowed(inputRecord).booleanValue())) {
                    isAvailable = YesNoFlag.N;
                }
            }
        }
        return isAvailable;
    }

    /**
     * Check if Add component option is available
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isAddCompAvailable(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddCompAvailable", new Object[]{record});
        }

        YesNoFlag isAvailable = YesNoFlag.Y;

        // If official Add component is never visible
        // Issue #102310 add component is not available in REINSTATEWIP
        if (getScreenModeCode().isViewPolicy() || getScreenModeCode().isViewEndquote() ||
            getScreenModeCode().isRenewWIP() && !getPolicyHeader().isInitTermB() ||
            getScreenModeCode().isResinstateWIP() && !getCoverageManager().isProblemPolicy(getPolicyHeader())) {
                isAvailable = YesNoFlag.N;
        }
        else {
            Record inputRecord = getPolicyHeader().toRecord();
            CoverageFields.setCoverageBaseRecordId(inputRecord, CoverageFields.getCoverageBaseRecordId(record));
            isAvailable = YesNoFlag.getInstance(((CoverageManagerImpl) getCoverageManager()).getComponentManager().isAddComponentAllowed(inputRecord));

            PMStatusCode status = CoverageFields.getCoverageStatus(record);
            if (getScreenModeCode().isCancelWIP()) {
                Record cancelWipRec = ((CoverageManagerImpl) getCoverageManager()).getComponentManager().
                    getCancelWipRule(getPolicyHeader());
                int cancelWipRule = cancelWipRec.getIntegerValue("returnValue").intValue();
                if (cancelWipRule == 0) {
                    isAvailable = YesNoFlag.N;
                } else {
                    isAvailable = YesNoFlag.Y;
                }
            }
            else if (status.isCancelled()) {
                isAvailable = YesNoFlag.N;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddCompAvailable", isAvailable);
        }
        return isAvailable;
    }

    /**
     * Check if Manuscript option is available
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isManuscriptAvailable(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isManuscriptAvailable", new Object[]{record});
        }

        YesNoFlag isAvailable = YesNoFlag.Y;

        if (getScreenModeCode().isOosWIP()) {
            PMStatusCode status = CoverageFields.getCoverageStatus(record);
            if (status.isCancelled()) {
                isAvailable = YesNoFlag.N;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isManuscriptAvailable", isAvailable);
        }
        return isAvailable;
    }


    /**
     * Check if Mini Tail action item should be available.
     *
     * @return YesNoFlag
     */
    private YesNoFlag isMiniTailAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.Y;
        if (OCCURRENCE.equalsIgnoreCase(CoverageFields.getPolicyFormCode(record))) {
            isAvailable = YesNoFlag.N;
        }
        else if (getScreenModeCode().isOosWIP()) {
            PMStatusCode status = CoverageFields.getCoverageStatus(record);
            if (status.isCancelled()) {
                isAvailable = YesNoFlag.N;
            }
        }

        return isAvailable;
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        // All coverage and component fields are read-only in below view modes:
        // VIEW_POLICY, VIEW_ENDQUOTE Mode(s) (Policy_Folder_UI.doc 3.6.3)
        // issue#102310 Added missing condition for REINSTATEWIP (Policy_Folder_UI.doc 3.6.5)
        PolicyViewMode viewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.equals(PolicyViewMode.OFFICIAL) || viewMode.equals(PolicyViewMode.ENDQUOTE)
            ||((getScreenModeCode().isOosWIP() || getScreenModeCode().isRenewWIP()) && !getPolicyHeader().isInitTermB())
            ||getScreenModeCode().isResinstateWIP() || getScreenModeCode().isCancelWIP()) {
                isReadOnly = true;
        }
        return isReadOnly;
    }

    /**
     * For issue 100798, check if Comp Upd button should be available.
     * Comp Upd option should be available to both quote and policy during new businuss/endorsement/renewal WIP.
     *
     * @return YesNoFlag
     */
    private YesNoFlag isCompUpdateAvailable() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCompUpdateAvailable");
        }
        YesNoFlag isAvailable = YesNoFlag.N;

        Transaction lastTransaction = getPolicyHeader().getLastTransactionInfo();
        TransactionCode transactionCode = lastTransaction.getTransactionCode();
        if (lastTransaction.getTransactionStatusCode().isInProgress() &&
            (transactionCode.isNewBus() || transactionCode.isQuote() ||
             transactionCode.isEndorsement() ||
             transactionCode.isRenewal() && getPolicyHeader().isInitTermB())) {
            isAvailable = YesNoFlag.Y;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCompUpdateAvailable", isAvailable);
        }
        return isAvailable;
    }

    /**
     * Check if Manual Excess Premium Layers option is available
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isExcessPremiumAvailable(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isExcessPremiumAvailable");
        }

        YesNoFlag isAvailable = YesNoFlag.N;

        // It is enabled for primary risk only
        String coverageCode = "";
        if (record.hasStringValue("productCoverageCode")) {
            coverageCode = CoverageFields.getProductCoverageCode(record);
        }
        if (!StringUtils.isBlank(coverageCode) && getPolicyHeader().getRiskHeader().getPrimaryRiskB().booleanValue()) {
            if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_MAN_XS_DYN_SQL)).booleanValue()) {
                Record inputRecord = new Record();
                inputRecord.setFieldValue("productCoverage", coverageCode);
                inputRecord.setFieldValue("transLogId", getPolicyHeader().getLastTransactionId());
                if (getCoverageManager().isManualExcessButtonEnable(inputRecord)) {
                    isAvailable = YesNoFlag.Y;
                }
            }
            else {
                String ratingModeCode = CoverageFields.getRatingModuleCode(record);
                if ("M".equals(ratingModeCode)) {
                    YesNoFlag excessB = CoverageFields.getExcessB(record);
                    if (excessB.booleanValue()) {
                        isAvailable = YesNoFlag.Y;
                    }
                    else {
                        String covgType = getCoverageManager().getProductCoverageType(coverageCode);
                        if ("EXCESS".equals(covgType)) {
                            isAvailable = YesNoFlag.Y;
                        }
                    }
                }

            }
        }

        if (isAvailable.booleanValue()) {
            // Product notify passes transaction validation for check type MXSBTTNCLK
            if (!getCoverageManager().isValidForManualExcessPremium(getPolicyHeader())) {
                isAvailable = YesNoFlag.N;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isExcessPremiumAvailable", isAvailable);
        }
        return isAvailable;
    }

    /**
     * This method is to determine if need to allow layers to be editable when readOnly=true.
     * It covers scenario that we want to disable coverage section and enable component section in the page
     *
     * @return boolean
     */
    private boolean isRecordSetHandleLayer() {
        boolean isHandleLayer = true;
        // Enable component layer if this is problem policy. This allow user to do fix manually.
        if (getCoverageManager().isProblemPolicy(getPolicyHeader())) {
            isHandleLayer = false;
        }
        return isHandleLayer;
    }

    /**
     * Shared Limit is not editable if Covg_Part_Shared_Limit_B = 'Y'.
     * Otherwise, not editable if Prod_Shared_Limit_B = 'N'.
     *
     * @param  record
     * @return YesNoFlag
     */
    private YesNoFlag isSharedLimitsBAvailable(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSharedLimitsBAvailable");
        }

        YesNoFlag isAvailable = YesNoFlag.getInstance(record.getEditIndicator());
        if (isAvailable.booleanValue()) {
            if (record.hasStringValue(CoverageFields.COVG_PART_SHARED_LIMIT_B) &&
                YesNoFlag.getInstance(CoverageFields.getCovgPartSharedLimitB(record)).booleanValue()) {
                isAvailable = YesNoFlag.N;
            }
            else if (record.hasStringValue(CoverageFields.PRODUCT_SHARED_LIMIT_B) &&
                !YesNoFlag.getInstance(CoverageFields.getProductSharedLimitB(record)).booleanValue()) {
                isAvailable = YesNoFlag.N;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSharedLimitsBAvailable", isAvailable);
        }
        return isAvailable;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
    }

    public CoverageEntitlementRecordLoadProcessor() {
    }

    public CoverageEntitlementRecordLoadProcessor(CoverageManager coverageManager,
                                                  PolicyHeader policyHeader, ScreenModeCode screenModeCode) {
        setPolicyHeader(policyHeader);
        setCoverageManager(coverageManager);
        setScreenModeCode(screenModeCode);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }

    public ScreenModeCode getScreenModeCode() {
        return m_screenModeCode;
    }

    public void setScreenModeCode(ScreenModeCode screenModeCode) {
        m_screenModeCode = screenModeCode;
    }

    protected static final String OCCURRENCE = "OCCURRENCE";

    private PolicyHeader m_policyHeader;
    private CoverageManager m_coverageManager;
    private ScreenModeCode m_screenModeCode;

}
