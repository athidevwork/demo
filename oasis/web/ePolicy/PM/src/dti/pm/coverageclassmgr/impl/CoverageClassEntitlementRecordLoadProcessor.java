package dti.pm.coverageclassmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.coverageclassmgr.CoverageClassFields;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageHeader;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.transactionmgr.TransactionFields;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 21, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/08/2011       wqfu        121349 - Modify isAddCoveragClassAvailable for short term coverage class.
 * 05/03/2011       fcb         105791 - isConvertCoverageAvailable set.
 * 05/29/2012       jshen       133797 - Add isManuallyRated to recordSet fieldName list if there is no record loaded.
 * 06/25/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * 07/20/2012       sxm         135777 - 1) Disable field if the record is not editable.
 *                                       2) Coverage Class of active coverage should be read only during Cancel WIP
 * 08/30/2012       adeng       136541 - fields in Coverage Class should be enable if the CANCELWIP Rule returns 1
 *                                       during Cancel WIP except CoverageClassEffective and RetroDate, they should following
 *                                       some existed particular conditional.
 * 08/07/2013       awu         146878 - Modified isAddCoverageClassAvailable to set the Add button to display depends on
 *                                       the CoverageHeader.currentCoverageStatus.
 * 10/18/2013       fcb         145725 - various optimizations.
 * 02/24/2014       xnie        148083 - Modified postProcessRecordSet() to
 *                                       1) Add field isRiskSumAvailable and isRiskAvailable when passed in recordSet's
 *                                          size is 0.
 *                                       2) Check if policy is initial loading, if does, call isRiskSumAvailable()
 *                                          to decide if the new risk page should be shown. Else, use existed value
 *                                          stored in user session to decide if the new risk page should be shown. The
 *                                          original risk page available is exclusive with new risk page.
 * 01/06/2016       wdang       168069 - Removed the logic of isRiskSumAvailable/isRiskAvailable.
 * ---------------------------------------------------
 */
public class CoverageClassEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);

        // Complex Rules (from [Policy_Folder_UI section 3.7.1.1]
        ScreenModeCode screenModeCode = getScreenModeCode();
        if (!StringUtils.isBlank(CoverageClassFields.getOfficialRecordId(record))
            && recordModeCode.isTemp()) {
            YesNoFlag afterImageB = CoverageFields.getAfterImageRecordB(record);
            String covgClassEffFromDate = CoverageClassFields.getCoverageClassEffectiveFromDate(record);
            String covgClassEffToDate = CoverageClassFields.getCoverageClassEffectiveToDate(record);
            if (covgClassEffFromDate.equals(covgClassEffToDate) && screenModeCode.isRenewWIP()) {
                record.setEditIndicator(YesNoFlag.N);
            }
            else if (screenModeCode.isCancelWIP() && isCancelWIPRule().booleanValue()) {
                record.setEditIndicator(YesNoFlag.Y);
            }
            else if (screenModeCode.isCancelWIP() && !isCancelWIPRule().booleanValue()) {
                record.setEditIndicator(YesNoFlag.N);
            }
            else if (afterImageB.booleanValue()) {
                if (screenModeCode.isOosWIP() && !recordModeCode.isRequest()) {
                    record.setEditIndicator(YesNoFlag.N);
                }
            }
            else if (!afterImageB.booleanValue()) {
                record.setEditIndicator(YesNoFlag.N);
            }
        }
        else {
            if (screenModeCode.isOosWIP()) {
                PMStatusCode status = CoverageClassFields.getCoverageClassStatus(record);
                if (status.isPending() && getPolicyHeader().isInitTermB()) {
                    record.setEditIndicator(YesNoFlag.Y);
                }
                else {
                    record.setEditIndicator(YesNoFlag.N);
                }
                if (status.isCancelled()) {
                    record.setEditIndicator(YesNoFlag.N);
                }
            }
        }

        // Determine if field is editable based on record edit indicator
        record.setFieldValue("isCoverageClassEffectiveToDateEditable", isCoverageClassEffectiveToDateEditable(record));
        record.setFieldValue("isRetroDateEditable", isRetroactiveDateEditable(record));

        // Determine if annual base rate is visible
        record.setFieldValue("isManuallyRated", isManuallyRated(record));
        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        recordSet.getSummaryRecord().setFieldValue("isDetailAvailable", YesNoFlag.Y);
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isCoverageClassEffectiveToDateEditable");
            pageEntitlementFields.add("isRetroDateEditable");
            pageEntitlementFields.add("isManuallyRated");
            recordSet.getSummaryRecord().setFieldValue("isDetailAvailable", YesNoFlag.N);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        // check add option avaiable
        recordSet.getSummaryRecord().setFieldValue("isAddAvailable",isAddCoverageClassAvailable());
        recordSet.getSummaryRecord().setFieldValue("isConvertCoverageAvailable", YesNoFlag.N);
        // Set readOnly attribute to summary record
        if (isRecordSetReadOnly()) {
            EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), true);
        }
    }

    /**
     * Return a Record of initial entitlement values for a new Coverage Class record.
     */
    public static void setInitialEntitlementValuesForCoverageClass(CoverageClassManager coverageClassManager,
                                                                   PolicyHeader policyHeader,
                                                                   ScreenModeCode screenModeCode,
                                                                   Record record) {
        CoverageClassEntitlementRecordLoadProcessor covgClassERLP =
            new CoverageClassEntitlementRecordLoadProcessor(coverageClassManager, policyHeader, screenModeCode,null);
        covgClassERLP.postProcessRecord(record, true);
    }

    /**
     * Check if Add coverage class option is available
     *
     * @return YesNoFlag
     */
    private YesNoFlag isAddCoverageClassAvailable() {
        YesNoFlag isAvailable = YesNoFlag.Y;
        if (getScreenModeCode().isViewPolicy() || getScreenModeCode().isViewEndquote() ||
            getScreenModeCode().isCancelWIP() || getScreenModeCode().isResinstateWIP() ||
           (getScreenModeCode().isOosWIP() ||getScreenModeCode().isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            isAvailable = YesNoFlag.N;
        }

        if (isAvailable.equals(YesNoFlag.Y)) {
            // Add option is unavaiable, if either "coverageBaseId" is empty
            CoverageHeader coverageHeader = getPolicyHeader().getCoverageHeader();
            if (coverageHeader == null) {
                // If coverage header failed to load, "Add" option should be unavailable
                isAvailable = YesNoFlag.N;
            }
            else {
                String covBaseRecordId = coverageHeader.getCoverageBaseRecordId();
                PMStatusCode recordStatus = PMStatusCode.getInstance(coverageHeader.getCurrentCoverageStatus());
                if (StringUtils.isBlank(covBaseRecordId) || recordStatus.isCancelled()) {
                    isAvailable = YesNoFlag.N;
                }
            }

            if (isAvailable.equals(YesNoFlag.Y)) {
                RiskHeader riskHeader = getPolicyHeader().getRiskHeader();
                if (riskHeader != null && riskHeader.getDateChangeAllowedB().booleanValue() && coverageHeader != null) {
                    Record inputRecord = new Record();
                    CoverageFields.setCoverageBaseRecordId(inputRecord, coverageHeader.getCoverageBaseRecordId());
                    TransactionFields.setTransactionEffectiveFromDate(inputRecord,
                            getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate());
                    if (!(getCoverageClassManager().isAddCoverageClassAllowed(inputRecord).booleanValue())) {
                        isAvailable = YesNoFlag.N;
                    }
                }
            }
        }

        return isAvailable;
    }

    /**
     * Check if effective to date is editable
     *
     * @param inputRecord
     * @return YesNoFlag
     */
    private YesNoFlag isCoverageClassEffectiveToDateEditable(Record inputRecord) {
        YesNoFlag isEditable = YesNoFlag.N;

        if (inputRecord.getEditIndicatorBooleanValue() && getScreenModeCode().isOosWIP()) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(inputRecord);

            if (recordModeCode.isRequest())
                isEditable = YesNoFlag.Y;
        }

        return isEditable;
    }

    /**
     * Check if retroactive date is editable
     *
     * @param inputRecord
     * @return YesNoFlag
     */
    private YesNoFlag isRetroactiveDateEditable(Record inputRecord) {
        YesNoFlag isEditable = YesNoFlag.N;

        if (inputRecord.getEditIndicatorBooleanValue()) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(inputRecord);
            String officialRecordId = CoverageClassFields.getOfficialRecordId(inputRecord);
            if (CoverageClassFields.getPolicyFormCode(inputRecord).equals("CM")
                && recordModeCode.isTemp()
                && (officialRecordId == null || officialRecordId.equals("0"))) {

                isEditable = YesNoFlag.Y;
            }
        }

        return isEditable;
    }

    private YesNoFlag isManuallyRated(Record record) {
        return YesNoFlag.getInstance(getCoverageClassManager().isManuallyRated(CoverageFields.getRatingModuleCode(record)));
    }
    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        // All coverage class fields are read-only in following
        // VIEW_POLICY, VIEW_ENDQUOTE view mode (Policy_Folder_UI.doc 3.7.3), or
        // REINSTATEWIP or CANCELWIP screen mode (Policy_Folder_UI.doc 3.7.3), or
        // OOSEWIP or RENEWWIP screen mode and the current term is not he transaction initiated term
        PolicyViewMode viewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.equals(PolicyViewMode.OFFICIAL) ||
            viewMode.equals(PolicyViewMode.ENDQUOTE) ||
            getScreenModeCode().isResinstateWIP() || (getScreenModeCode().isCancelWIP() && !isCancelWIPRule().booleanValue()) ||
            (getScreenModeCode().isOosWIP() || getScreenModeCode().isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            isReadOnly = true;
        }
        return isReadOnly;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public ScreenModeCode getScreenModeCode() {
        return m_screenModeCode;
    }

    public void setScreenModeCode(ScreenModeCode screenModeCode) {
        m_screenModeCode = screenModeCode;
    }

    public CoverageClassEntitlementRecordLoadProcessor() {
    }

    public CoverageClassManager getCoverageClassManager() {
        return m_coverageClassManager;
    }

    public void setCoverageClassManager(CoverageClassManager coverageClassManager) {
        m_coverageClassManager = coverageClassManager;
    }

    public YesNoFlag isCancelWIPRule() {
        if (m_cancelWIPRule == null) {
            return YesNoFlag.N;
        }
        return m_cancelWIPRule;
    }

    public void setCancelWIPRule(YesNoFlag cancelWIPRule) {
        m_cancelWIPRule = cancelWIPRule;
    }

    public CoverageClassEntitlementRecordLoadProcessor(CoverageClassManager coverageClassManager,
                                                       PolicyHeader policyHeader,
                                                       ScreenModeCode screenModeCode, YesNoFlag cancelWIPRule) {
        setCoverageClassManager(coverageClassManager);
        setPolicyHeader(policyHeader);
        setScreenModeCode(screenModeCode);
        setCancelWIPRule(cancelWIPRule);
    }

    private PolicyHeader m_policyHeader;
    private ScreenModeCode m_screenModeCode;
    private CoverageClassManager m_coverageClassManager;
    private YesNoFlag m_cancelWIPRule = null;
}
