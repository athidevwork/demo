package dti.pm.policymgr;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.MaxSizeHashMap;
import dti.pm.busobjs.QuoteCycleCode;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.coveragemgr.CoverageHeader;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.PolicyHeaderReloadCode;
import dti.pm.busobjs.ProcessStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides header information about a policy. It typically contains key policy information including lock
 * information along with a list of available terms with written premium information.
 * <p/>
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
 * 08/07/2007       sxm         added screenModeCode
 * 10/12/2007       sxm         added reloadCode
 * 10/29/2007       fcb         isProcessStatusCancelOnly removed.
 *                              type of m_processStatusCode changed from String to ProcessStatus
 * 12/11/2007       fcb         setQuoteRenewalExists and isQuoteRenewalExists added.
 * 12/29/2007       zlzhu       add logic to the PolicyHeader.getScreenModeCode to return  VIEW_POLICY if
 *                              the transactionCode is NEWBUS and the policyViewMode is OFFICIAL
 * 02/05/2008       fcb         isLastTerm: logic modified, the indicator is set when getting the
 *                              policyHeader from the database.
 * 04/11/2008       sxm         Removed generic fields char/date/num1-3
 * 11/07/2008       yhyang      Add getPriorTermBaseRecordId() to get the prior termBaseRecordID.
 * 12/23/2010       wfu         issue 103999 - Added policyholder address information items.
 * 07/05/2013       xnie        Issue 145721 - 1) Added m_writtenPremium, m_curTransactionId, m_offTransactionId, and
 *                                                get/set methods.
 *                                             2) Modified toString() to add m_writtenPremium, m_curTransactionId, and
 *                                                m_offTransactionId  to PolicyHeader.
 * 09/26/2013       fcb         145725 - Added logic for caching several items.
 * 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
 * 01/21/2014       Parker      152127 - Cache a primary coverage into policy header for each risk.
 * 02/25/2014       xnie        148083 - Added riskSumAvailableB.
 * 04/09/2014       awu         150201 - Added orgHierRootId.
 * 07/25/2014       awu         152034 - Added currentSelectedMap.
 * 07/13/2015       xnie        164407 - Added getEvalDate().
 * 04/08/2015       wdang       157211 - Added method getRecordMode().
 * 09/07/2015       awu         164026 - Added setTermBaseRecordId.
 * 09/07/2015       tzeng       164679 - Added m_autoRiskRelAvailableCache, m_autoRiskRelPKCache.
 * 11/04/2015       tzeng       165790 - Added m_polPhaseCode.
 * 01/28/2016       wdang       169024 - 1) Removed m_autoRiskRelAvailableCache and m_autoRiskRelPKCache that are added by 164679.
 *                                       2) Added m_prodRiskRelationId instead.
 * 08/26/2016       wdang       167534 - Added quoteCycleCode.
 * 02/10/2017       tzeng       183335 - Added m_ooseAndEndorsementAvailableInRenewalWipB.
 * 02/28/2017       mlm         183387 - Refactored to handle preview for long running transactions.
 * 07/24/2017       ssheng      187017 - Reverted issue 183335 fix.
 * 04/26/2018       wrong       192351 - Added m_cxlWipTermOffExpDate and getter/setter function.
 * 07/05/2018       ryzhao      187070 - Added m_excludeCompGr1B, m_excludeCompGr2B and getter/setter function.
 * ---------------------------------------------------
 */
public class PolicyHeader {

    public String getPolicyId() {
        return getPolicyIdentifier().getPolicyId();
    }

    public void setPolicyId(String policyId) {
        getPolicyIdentifier().setPolicyId(policyId);
    }

    public String getPolicyNo() {
        return getPolicyIdentifier().getPolicyNo();
    }

    public void setPolicyNo(String policyNo) {
        getPolicyIdentifier().setPolicyNo(policyNo);
    }

    public String getPolicyHolderName() {
        return m_policyHolderName;
    }

    public void setPolicyHolderName(String policyHolderName) {
        m_policyHolderName = policyHolderName;
    }

    public String getPolicyHolderNameEntityId() {
        return m_policyHolderNameEntityId;
    }

    public void setPolicyHolderNameEntityId(String policyHolderNameEntityId) {
        m_policyHolderNameEntityId = policyHolderNameEntityId;
    }

    public PolicyCycleCode getPolicyCycleCode() {
        return m_policyCycleCode;
    }

    public void setPolicyCycleCode(PolicyCycleCode policyCycleCode) {
        m_policyCycleCode = policyCycleCode;
    }

    public QuoteCycleCode getQuoteCycleCode() {
        return m_quoteCycleCode;
    }

    public void setQuoteCycleCode(QuoteCycleCode quoteCycleCode) {
        m_quoteCycleCode = quoteCycleCode;
    }

    public String getPolicyTypeCode() {
        return m_policyTypeCode;
    }

    public void setPolicyTypeCode(String policyTypeCode) {
        m_policyTypeCode = policyTypeCode;
    }

    public boolean isWipB() {
        return m_wipB;
    }

    public void setWipB(boolean wipB) {
        m_wipB = wipB;
    }

    public void setProcessStatusCode(ProcessStatus processStatusCode){
        if (processStatusCode==null)
            m_processStatusCode = ProcessStatus.getInstance(null);
        else
            m_processStatusCode = processStatusCode;
    }

    public ProcessStatus getProcessStatusCode() {
        return m_processStatusCode;
    }

    public void setIssueStateCode(String issueStateCode) {
        m_issueStateCode = issueStateCode;
    }

    public String getIssueStateCode() {
        return m_issueStateCode;
    }

    public void setRegionalOffice(String regionalOffice) {
        m_regionalOffice = regionalOffice;
    }

    public String getRegionalOffice() {
        return m_regionalOffice;
    }

    public String getIssueCompanyEntityId() {
        return m_issueCompanyEntityId;
    }

    public void setIssueCompanyEntityId(String issueCompanyEntityId) {
        m_issueCompanyEntityId = issueCompanyEntityId;
    }

    public Iterator getPolicyTerms() {
        return m_policyTerms.values().iterator();
    }

    /**
     * Add the Term, keyed by the policyTermHistoryId.
     *
     * @param policyTerm
     */
    public void addPolicyTerm(Term policyTerm) {
        m_policyTerms.put(String.valueOf(policyTerm.getPolicyTermHistoryId()), policyTerm);
    }

    /**
     * Returns true if the request Term is available for this Policy.
     *
     * @param policyTermHistoryId
     */
    public boolean hasPolicyTerm(String policyTermHistoryId) {
        return m_policyTerms.containsKey(policyTermHistoryId);
    }

    /**
     * Get the Term for the given policyTermHistoryId
     *
     * @param policyTermHistoryId
     */
    public Term getPolicyTerm(String policyTermHistoryId) {
        if (hasPolicyTerm(policyTermHistoryId)) {
            return (Term) m_policyTerms.get(policyTermHistoryId);
        } else {
            String officialTermId = getSelectedPolicyTermId(policyTermHistoryId);
            if (hasPolicyTerm(officialTermId)) {
                return (Term) m_policyTerms.get(officialTermId);
            } else {
                throw new AppException("The term for policyTermHistoryId'" + policyTermHistoryId + "' does not exist in this Policy'" + getPolicyNo() + "'");
            }
        }
    }

    /**
     * Get the Term for the given policyTermHistoryId using the base record
     * to ensure auto selection in the policy header.
     *
     * @param policyTermHistoryId
     */
    public String getSelectedPolicyTermId(String policyTermHistoryId) {
        // First check if it exists
        if (hasPolicyTerm(policyTermHistoryId)) {
            return policyTermHistoryId;
        }
        else {
            // If particular term id is not found, there could be a change to the
            // Policy_Term_History record directly, so find the Term with the same base record id.
            Iterator itr = getPolicyTerms();

            while (itr.hasNext()) {
                Term findTerm = (Term) itr.next();
                if (getPolicyIdentifier().getTermBaseRecordId().equals(findTerm.getTermBaseRecordId())) {
                    return findTerm.getPolicyTermHistoryId();
                }
            }

            throw new AppException("Unable to resolve matching term for: " + policyTermHistoryId);
        }
    }

    public boolean isLastTerm() {
        return m_lastTermB;
    }

    public String getPolicyTermHistoryId() {
        return getPolicyIdentifier().getPolicyTermHistoryId();
    }

    public void setPolicyTermHistoryId(String policyTermHistoryId) {
        getPolicyIdentifier().setPolicyTermHistoryId(policyTermHistoryId);
    }

    public String getTermBaseRecordId() {
        return getPolicyIdentifier().getTermBaseRecordId();
    }

    public void setTermBaseRecordId(String termBaseRecordId) {
        getPolicyIdentifier().setTermBaseRecordId(termBaseRecordId);
    }

    /**
     * Return a list of Available Policy Terms in the LOV format.
     */
    public String getAvailablePolicyTerms() {
        String terms = "[NO_ADD_SELECT_OPTION]LIST:";

        Iterator itr = getPolicyTerms();
        while (itr.hasNext()) {
            Term term= (Term) itr.next();
            String termString;
            if (FormatUtils.isDateFormatUS()) {
                termString = (term.getEffectiveFromDate() + " - " + term.getEffectiveToDate());
            }
            else {
                termString = FormatUtils.formatDateForDisplay(term.getEffectiveFromDate()) + " - " + FormatUtils.formatDateForDisplay(term.getEffectiveToDate());
            }
            terms += "," + term.getPolicyTermHistoryId() + "," + termString;
        }
        return terms;
    }

    /**
     * Get the prior term baseRecordID.
     *
     * @return
     */
    public String getPriorTermBaseRecordId() {
        String priorTermBaseRecordId = "";
        Iterator termList = getPolicyTerms();
        // Get the prior term.
        Term term = null;
        while (termList.hasNext()) {
            term = (Term) termList.next();
            if (m_termEffectiveFromDate.equals(term.getEffectiveToDate())) {
                break;
            }
        }
        if (term != null) {
            priorTermBaseRecordId = term.getTermBaseRecordId();
        }
        return priorTermBaseRecordId;
    }

    public String getTermEffectiveFromDate() {
        return m_termEffectiveFromDate;
    }

    public void setTermEffectiveFromDate(String termEffectiveFromDate) {
        m_termEffectiveFromDate = termEffectiveFromDate;
    }

    public String getTermEffectiveToDate() {
        return m_termEffectiveToDate;
    }

    public void setTermEffectiveToDate(String termEffectiveToDate) {
        m_termEffectiveToDate = termEffectiveToDate;
    }

    public Double getTermWrittenPremium() {
        return m_termWrittenPremium;
    }

    public void setTermWrittenPremium(Double termWrittenPremium) {
        m_termWrittenPremium = termWrittenPremium;
    }

    public Double getWrittenPremium() {
        return m_writtenPremium;
    }

    public void setWrittenPremium(Double writtenPremium) {
        m_writtenPremium = writtenPremium;
    }

    public String getCurTransactionId() {
        return m_curTransactionId;
    }

    public void setCurTransactionId(String curTransactionId) {
        m_curTransactionId = curTransactionId;
    }

    public String getOffTransactionId() {
        return m_offTransactionId;
    }

    public void setOffTransactionId(String offTransactionId) {
        m_offTransactionId = offTransactionId;
    }

    public String getLastTransactionId() {
        return m_lastTransactionId;
    }

    public void setLastTransactionId(String lastTransactionId) {
        m_lastTransactionId = lastTransactionId;
    }

    public PolicyIdentifier getPolicyIdentifier() {
        return m_policyIdentifier;
    }

    public void setPolicyIdentifier(PolicyIdentifier policyIdentifier) {
        m_policyIdentifier = policyIdentifier;
    }

    public Transaction getLastTransactionInfo() {
        return m_lastTransactionInfo;
    }

    public void setLastTransactionInfo(Transaction lastTransactionInfo) {
        m_lastTransactionInfo = lastTransactionInfo;
    }

    public String getPolicyLockId() {
        return m_policyIdentifier.getPolicyLockId();
    }

    public boolean isShowViewMode() {
        return m_showViewMode;
    }

    public void setShowViewMode(boolean showViewMode) {
        m_showViewMode = showViewMode;
    }

    public boolean isCoveragePartConfigured() {
        return m_coveragePartConfigured;
    }

    public void setCoveragePartConfigured(boolean coveragePartConfigured) {
        m_coveragePartConfigured = coveragePartConfigured;
    }

    public boolean isCoverageClassConfigured() {
        return m_coverageClassConfigured;
    }

    public void setCoverageClassConfigured(boolean coverageClassConfigured) {
        m_coverageClassConfigured = coverageClassConfigured;
    }

    public boolean isShortTermB() {
        return m_shortTermB;
    }

    public void setShortTermB(boolean shortTermB) {
        m_shortTermB = shortTermB;
    }

    public PolicyStatus getPolicyStatus() {
        return m_policyStatus;
    }

    public void setPolicyStatus(PolicyStatus policyStatus) {
        m_policyStatus = policyStatus;
    }

    public PolicyStatus getTermStatus() {
        return m_termStatus;
    }

    public void setTermStatus(PolicyStatus termStatus) {
        m_termStatus = termStatus;
    }

    public String getPolicyExpirationDate() {
        return m_policyExpirationDate;
    }

    public void setPolicyExpirationDate(String policyExpirationDate) {
        m_policyExpirationDate = policyExpirationDate;
    }

    public String getRenewalReasonCode() {
        return m_renewalReasonCode;
    }

    public void setRenewalReasonCode(String renewalReasonCode) {
        m_renewalReasonCode = renewalReasonCode;
    }

    public String getRenewalIndicatorCode() {
        return m_renewalIndicatorCode;
    }

    public void setRenewalIndicatorCode(String renewalIndicatorCode) {
        m_renewalIndicatorCode = renewalIndicatorCode;
    }

    public boolean isValidRenewalCandidate() {
        return m_validRenewalCandidate;
    }

    public boolean isValidPurgeCandidate() {
        return m_validPurgeCandidate;
    }

    public boolean isValidOosTerm() {
        return m_validOosTerm;
    }

    public void setValidRenewalCandidate(boolean validRenewalCandidate) {
        m_validRenewalCandidate = validRenewalCandidate;
    }

    public void setValidPurgeCandidate(boolean validPurgeCandidate) {
        m_validPurgeCandidate = validPurgeCandidate;
    }

    public void setValidOosTerm(boolean validOosTerm) {
        m_validOosTerm = validOosTerm;
    }

    public String getCollateralB() {
        return m_collateralB;
    }

    public void setCollateralB(String collateralB) {
        m_collateralB = collateralB;
    }

    public RiskHeader getRiskHeader() {
        return m_riskHeader;
    }

    public void setRiskHeader(RiskHeader riskHeader) {
        m_riskHeader = riskHeader;
    }

    public boolean hasRiskHeader() {
        return (m_riskHeader != null);
    }

    public CoverageHeader getCoverageHeader(){
        return m_coverageHeader;
    }

    public void setCoverageHeader(CoverageHeader coverageHeader) {
        m_coverageHeader = coverageHeader;
    }

    public boolean hasCoverageHeader() {
        return (m_coverageHeader != null);
    }

    public void addCacheRiskHeader(RiskHeader riskHeader) {
        if(riskHeader.getPrimaryRiskB().booleanValue()){
            cacheRiskHeader.put("primaryRisk",riskHeader);
        }
        cacheRiskHeader.put(riskHeader.getRiskId(), riskHeader);
    }

    public void deleteCacheRiskHeader(RecordSet records) {
        for (int i = 0; i < records.getRecordList().size(); i++) {
            Record r = records.getRecord(i);
            if (YesNoFlag.getInstance(r.getStringValue("primaryRiskB")).booleanValue()) {
                cacheRiskHeader.remove("primaryRisk");
            }
            cacheRiskHeader.remove(r.getStringValue("riskId"));
        }
        this.setRiskHeader(null);
    }

    public Map<String,RiskHeader> getCacheRiskHeader() {
        return cacheRiskHeader;
    }

    public void setCacheRiskHeader(Map<String,RiskHeader> cacheRiskHeader) {
        this.cacheRiskHeader = cacheRiskHeader;
    }

    public void addCacheCoverageHeader(CoverageHeader coverageHeader) {
        cacheCoverageHeader.put(coverageHeader.getCoverageId(), coverageHeader);
    }

    public void deleteCacheCoverageHeader(RecordSet records) {
        RiskHeader rHeader = this.getRiskHeader();
        for (int i = 0; i < records.getRecordList().size(); i++) {
            Record r = records.getRecord(i);
            if (rHeader != null) {
                String primaryCoverageId = rHeader.getRiskId() + "primaryCoverage";
                if (cacheCoverageHeader.get(primaryCoverageId) != null && cacheCoverageHeader.get(primaryCoverageId).getCoverageId().equals(r.getStringValue("coverageId"))) {
                    cacheCoverageHeader.remove(primaryCoverageId);
                }
            }
            cacheCoverageHeader.remove(r.getStringValue("coverageId"));
        }
        this.setCoverageHeader(null);
    }

    public void addCachePrimaryCoverageHeader(CoverageHeader coverageHeader) {
        if (this.hasRiskHeader())
            cacheCoverageHeader.put(this.getRiskHeader().getRiskId() + "primaryCoverage", coverageHeader);
    }

    public Map<String,CoverageHeader> getCacheCoverageHeader() {
        return cacheCoverageHeader;
    }

    public void setCacheCoverageHeader(Map<String,CoverageHeader> cacheCoverageHeader) {
        this.cacheCoverageHeader = cacheCoverageHeader;
    }

    public RecordSet getCacheRiskOption() {
        return cacheRiskOption;
    }

    public void setCacheRiskOption(RecordSet cacheRiskOption) {
        this.cacheRiskOption = cacheRiskOption;
    }

    public RecordSet getCacheCoverageOption() {
        return cacheCoverageOption;
    }

    public void setCacheCoverageOption(RecordSet cacheCoverageOption) {
        this.cacheCoverageOption = cacheCoverageOption;
    }

    public RecordSet getCacheCoverageRiskOption() {
        return cacheCoverageRiskOption;
    }

    public void setCacheCoverageRiskOption(RecordSet cacheCoverageRiskOption) {
        this.cacheCoverageRiskOption = cacheCoverageRiskOption;
    }

    public boolean isQuoteEndorsementExists() {
        return m_quoteEndorsementExists;
    }

    public boolean isQuoteRenewalExists() {
        return m_quoteRenewalExists;
    }

    public void setQuoteEndorsementExists(boolean quoteEndorsementExists) {
        m_quoteEndorsementExists = quoteEndorsementExists;
    }

    public void setQuoteRenewalExists(boolean quoteRenewalExists) {
        m_quoteRenewalExists = quoteRenewalExists;
    }

    public boolean isQuoteTempVersionExists() {
        return m_quoteTempVersionExists;
    }

    public void setQuoteTempVersionExists(boolean quoteTempVersionExists) {
        m_quoteTempVersionExists = quoteTempVersionExists;
    }

    public boolean isInitTermB() {
        return m_initTermB;
    }

    public void setInitTermB(boolean initTermB) {
        m_initTermB = initTermB;
    }

    public void setLastTermB(boolean lastTermB) {
        m_lastTermB = lastTermB;
    }

    public String getPolicyLayerCode() {
        return m_policyLayerCode;
    }

    public void setPolicyLayerCode(String policyLayerCode) {
        m_policyLayerCode = policyLayerCode;
    }

    public String getPolicyPolicyFormCode() {
        return m_policyPolicyFormCode;
    }

    public void setPolicyPolicyFormCode(String policyPolicyFormCode) {
        m_policyPolicyFormCode = policyPolicyFormCode;
    }

    public String getWipTransCode() {
        return m_wipTransCode;
    }

    public void setWipTransCode(String WipTransCode) {
        m_wipTransCode = WipTransCode;
    }

    public boolean getAppAvailableB() {
        return m_appAvailableB;
    }

    public void setAppAvailableB(boolean AppAvailableB) {
        m_appAvailableB = AppAvailableB;
    }

    public boolean getRiskSumAvailableB() {
        return m_riskSumAvailableB;
    }

    public void setRiskSumAvailableB(boolean riskSumAvailableB) {
        m_riskSumAvailableB = riskSumAvailableB;
    }

    public ScreenModeCode getScreenModeCode() {
        if (m_screenModeCode == null) {
            PolicyCycleCode policyCycle = getPolicyCycleCode();
            TransactionStatus transStatus = getLastTransactionInfo().getTransactionStatusCode();
            TransactionCode transCode = getLastTransactionInfo().getTransactionCode();
            PolicyViewMode policyViewMode = getPolicyIdentifier().getPolicyViewMode();
            PolicyStatus policyTermStatus = getTermStatus();
            ScreenModeCode screenModeCode = ScreenModeCode.VIEW_POLICY;
            // Issue #102267, Added CANCELWIP screen mode for quote cancellation.
            if (policyCycle.isQuote()) {
                if (!policyViewMode.isOfficial() && policyTermStatus.isPending()) {
                    screenModeCode = ScreenModeCode.MANUAL_ENTRY;
                }
                else if (policyViewMode.isWIP() && policyTermStatus.isActive() && transCode.isEndorsement()) {
                    screenModeCode = ScreenModeCode.WIP;
                }
                else if (policyViewMode.isWIP() && ((policyTermStatus.isCancelled() && transCode.isCancellation())
                    || transCode.isRiskCancel() || transCode.isCovgCancel() || transCode.isScvgCancel())) {
                    screenModeCode = ScreenModeCode.CANCEL_WIP;
                }
                else {
                    screenModeCode = ScreenModeCode.VIEW_POLICY;
                }
            }
            else if (policyCycle.isPolicy()) {
                if (transStatus.isInProgress()) {
                    // Transaction is In Progress
                    //add logic to the PolicyHeader.getScreenModeCode to return  VIEW_POLICY if
                    //the transactionCode is NEWBUS and the policyViewMode is OFFICIAL,it fixes 78259
                    if(transCode.isNewBus()&&policyViewMode.isOfficial()){
                        screenModeCode = ScreenModeCode.VIEW_POLICY;
                    }
                    else if (transCode.isNewBus() || transCode.isReissue() || transCode.isConvRenew() || transCode.isConvReissue()) {
                        screenModeCode =  ScreenModeCode.MANUAL_ENTRY;
                    }
                    else if (transCode.isAutoRenewal() || transCode.isManualRenewal()) {
                        screenModeCode =  ScreenModeCode.RENEW_WIP;
                    }
                    else if (transCode.isOosEndorsement()) {
                        screenModeCode =  ScreenModeCode.OOS_WIP;
                    }
                    else if (transCode.isCancellation() || transCode.isRiskCancel() || transCode.isCovgCancel() ||
                        transCode.isPurge()        || transCode.isScvgCancel() || transCode.isCprtCancel()) {
                        screenModeCode =  ScreenModeCode.CANCEL_WIP;
                    }
                    else if (transCode.isReinstate()    || transCode.isRiskReinst() || transCode.isCovgReinst() ||
                        transCode.isScvgReinst()   || transCode.isCprtReinst()) {
                        screenModeCode =  ScreenModeCode.REINSTATE_WIP;
                    }
                    else {
                        screenModeCode =  ScreenModeCode.WIP;
                    }
                }
                else {
                    // Transaction is NOT In Progress
                    if (policyViewMode.isEndquote()) {
                        screenModeCode =  ScreenModeCode.VIEW_ENDQUOTE;
                    }
                    else {
                        screenModeCode = ScreenModeCode.VIEW_POLICY;
                    }
                }
            }

            m_screenModeCode = screenModeCode;
        }

        return m_screenModeCode;
    }

    public RecordMode getRecordMode() {
        RecordMode recordModeCode = null;
        if (this.getScreenModeCode().isViewEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        else if (this.getScreenModeCode().isViewPolicy()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        else {
            recordModeCode = RecordMode.TEMP;
        }
        return recordModeCode;
    }

    public void setScreenModeCode(ScreenModeCode screenModeCode) {
        m_screenModeCode = screenModeCode;
    }

    public PolicyHeaderReloadCode getReloadCode() {
        return m_reloadCode;
    }

    public void setReloadCode(PolicyHeaderReloadCode reloadCode) {
        m_reloadCode = reloadCode;
    }

    public boolean isReloadRequired() {
        return (m_reloadCode != null);
    }

    //Added for issue 103999: display policyholder address in policy header
    public String getPolAddressLine1() {
        return m_polAddressLine1;
    }

    public void setPolAddressLine1(String polAddressLine1) {
        this.m_polAddressLine1 = polAddressLine1;
    }

    public String getPolAddressLine2() {
        return m_polAddressLine2;
    }

    public void setPolAddressLine2(String polAddressLine2) {
        this.m_polAddressLine2 = polAddressLine2;
    }

    public String getPolCity() {
        return m_polCity;
    }

    public void setPolCity(String polCity) {
        this.m_polCity = polCity;
    }

    public String getPolAddressLine3() {
        return m_polAddressLine3;
    }

    public void setPolAddressLine3(String polAddressLine3) {
        this.m_polAddressLine3 = polAddressLine3;
    }

    public String getPolStateCode() {
        return m_polStateCode;
    }

    public void setPolStateCode(String polStateCode) {
        this.m_polStateCode = polStateCode;
    }

    public String getPolZipCode() {
        return m_polZipCode;
    }

    public void setPolZipCode(String polZipCode) {
        this.m_polZipCode = polZipCode;
    }

    public String getPolInsuredSince() {
        return m_polInsuredSince;
    }

    public void setPolInsuredSince(String polInsuredSince) {
        this.m_polInsuredSince = polInsuredSince;
    }

    public boolean isSkipDefaultRisk() {
        return m_skipDefaultRisk;
    }

    public void setSkipDefaultRisk(boolean skipDefaultRisk) {
        this.m_skipDefaultRisk = skipDefaultRisk;
    }

    public boolean isSkipDefaultCoverage() {
        return m_skipDefaultCoverage;
    }

    public void setSkipDefaultCoverage(boolean skipDefaultCoverage) {
        this.m_skipDefaultCoverage = skipDefaultCoverage;
    }

    public boolean isSkipDefaultSubCoverage() {
        return m_skipDefaultSubCoverage;
    }

    public void setSkipDefaultSubCoverage(boolean skipDefaultSubCoverage) {
        this.m_skipDefaultSubCoverage = skipDefaultSubCoverage;
    }

    public boolean isSkipDefaultComponent() {
        return m_skipDefaultComponent;
    }

    public void setSkipDefaultComponent(boolean skipDefaultComponent) {
        this.m_skipDefaultComponent = skipDefaultComponent;
    }

    public String getTransactionSnapshotCache() {
        return m_transactionSnapshotCache;
    }

    public boolean hasTransactionSnapshotCache() {
        return (m_transactionSnapshotCache != null && !m_transactionSnapshotCache.isEmpty());
    }

    public void setTransactionSnapshotCache(String transactionSnapshotCache) {
        m_transactionSnapshotCache = transactionSnapshotCache;
    }

    public String getJobCategoryCache() {
        return m_jobCategoryCache;
    }

    public boolean hasJobCategoryCache() {
        return (m_jobCategoryCache != null && !m_jobCategoryCache.isEmpty());
    }

    public void setJobCategoryCache(String jobCategoryCache) {
        m_jobCategoryCache = jobCategoryCache;
    }

    public String getUndoTermAvailableCache() {
        return m_undoTermAvailableCache;
    }

    public boolean hasUndoTermAvailableCache() {
        return (m_undoTermAvailableCache != null && !m_undoTermAvailableCache.isEmpty());
    }

    public void setUndoTermAvailableCache(String undoB) {
        m_undoTermAvailableCache = undoB;
    }

    public String getCancWipEditCache() {
        return m_cancWipEditCache;
    }

    public boolean hasCancWipEditCache() {
        return (m_cancWipEditCache != null && !m_cancWipEditCache.isEmpty());
    }

    public void setCancWipEditCache(String cancWipEditCache) {
        m_cancWipEditCache = cancWipEditCache;
    }

    public String getProblemPolicyCache() {
        return m_problemPolicyCache;
    }

    public boolean hasProblemPolicyCache() {
        return (m_problemPolicyCache != null && !m_problemPolicyCache.isEmpty());
    }

    public void setProblemPolicyCache(String problemPolicyCache) {
        m_problemPolicyCache = problemPolicyCache;
    }

    public String getProfEntityCache() {
        return m_profEntityCache;
    }

    public boolean hasProfEntityCache() {
        return (m_profEntityCache != null && !m_profEntityCache.isEmpty());
    }

    public void setProfEntityCache(String profEntityCache) {
        m_profEntityCache = profEntityCache;
    }

    public void setOrgHierRootId(String orgHierRootId) {
        this.m_orgHierRootId = orgHierRootId;
    }

    public String getOrgHierRootId() {
        return m_orgHierRootId;
    }

    public void setProdRiskRelationId(String prodRiskRelationId) {
        this.m_prodRiskRelationId = prodRiskRelationId;
    }

    public String getProdRiskRelationId() {
        return m_prodRiskRelationId;
    }

    public void setCurrentSelectedId(String key, String value) {
        this.m_currentSelectedMap.put(key, value);
    }

    public String getCurrentSelectedId(String key) {
        return m_currentSelectedMap.get(key);
    }

    public String getPolPhaseCode() {
        return m_polPhaseCode;
    }

    public void setPolPhaseCode(String policyPhaseCode) {
        m_polPhaseCode = policyPhaseCode;
    }

    public String getCxlWipTermOffExpDate() {
        return m_cxlWipTermOffExpDate;
    }

    public void setCxlWipTermOffExpDate(String cxlWipTermOffExpDate) {
        m_cxlWipTermOffExpDate = cxlWipTermOffExpDate;
    }

    public void clearCurrentSelectedMap() {
        m_currentSelectedMap.clear();
    }

    public String getEvalDate() {
        String evalDate = null;
        // Determine upon which date to find primary information
        if (getLastTransactionInfo().getTransactionStatusCode().isComplete()) {
            evalDate = getTermEffectiveToDate();
        } else {
            evalDate = getLastTransactionInfo().getTransEffectiveFromDate();
        }

        return evalDate;
    }

    public boolean isPreviewRequest() {
        return m_isPreviewRequest;
    }

    public void setPreviewRequest(boolean isPreviewRequest) {
        m_isPreviewRequest = isPreviewRequest;
    }

    public String getExcludeCompGr1B() {
        return m_excludeCompGr1B;
    }

    public void setExcludeCompGr1B(String excludeCompGr1B) {
        m_excludeCompGr1B = excludeCompGr1B;
    }

    public String getExcludeCompGr2B() {
        return m_excludeCompGr2B;
    }

    public void setExcludeCompGr2B(String excludeCompGr2B) {
        m_excludeCompGr2B = excludeCompGr2B;
    }

    /**
     * Returns a Record with the fields from the PolicyHeader, PolicyIdentifier, and the current policy Term.
     */
    public Record toRecord() {
        Logger l = LogUtils.enterLog(getClass(), "toRecord");

        Record policyHeaderFields = new Record();
        RecordBeanMapper recBeanMapper = new RecordBeanMapper(new String[]{"policyIdentifier", "policyTerms", "lastTransactionInfo", "riskHeader", "reloadRequired"});
        recBeanMapper.map(this, policyHeaderFields);
        recBeanMapper.map(getPolicyIdentifier(), policyHeaderFields);
        recBeanMapper.map(getLastTransactionInfo(), policyHeaderFields);

        if (hasRiskHeader()) {
            recBeanMapper.map(getRiskHeader(), policyHeaderFields);
        }

        if (hasCoverageHeader()) {
            recBeanMapper.map(getCoverageHeader(), policyHeaderFields);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "toRecord", policyHeaderFields);
        }
        return policyHeaderFields;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(
            "PolicyHeader{" +
                ", m_policyHolderName='" + m_policyHolderName + '\'' +
                ", m_policyHolderNameEntityId='" + m_policyHolderNameEntityId + '\'' +
                ", m_policyCycleCode='" + m_policyCycleCode + '\'' +
                ", m_policyTypeCode='" + m_policyTypeCode + '\'' +
                ", m_wip=" + m_wipB +
                ", m_processStatusCode='" + m_processStatusCode + '\'' +
                ", m_issueState='" + m_issueStateCode + '\'' +
                ", m_regionalOffice='" + m_regionalOffice + '\'' +
                ", m_shortTermB='" + m_shortTermB + '\'' +
                ", m_issueCompany='" + m_issueCompanyEntityId + '\'' +
                ", m_termEffectiveFromDate='" + m_termEffectiveFromDate + '\'' +
                ", m_termEffectiveToDate='" + m_termEffectiveToDate + '\'' +
                ", m_termWrittenPremium='" + m_termWrittenPremium + '\'' +
                ", m_writtenPremium='" + m_writtenPremium + '\'' +
                ", m_renewalReasonCode='" + m_renewalReasonCode + '\'' +
                ", m_renewalIndicatorCode='" + m_renewalIndicatorCode + '\'' +
                ", m_validRenewalCandidate='" + m_validRenewalCandidate + '\'' +
                ", m_validPurgeCandidate='" + m_validPurgeCandidate + '\'' +
                ", m_validOosTerm='" + m_validOosTerm + '\'' +
                ", m_curTransactionInfo=" + m_curTransactionId +
                ", m_offTransactionInfo=" + m_offTransactionId +
                ", m_lastTransactionId='" + m_lastTransactionId + '\'' +
                ", m_showViewMode='" + m_showViewMode + '\'' +
                ", m_coveragePartConfigured='" + m_coveragePartConfigured + '\'' +
                ", m_coverageClassConfigured='" + m_coverageClassConfigured + '\'' +
                ", m_policyStatus='" + m_policyStatus + '\'' +
                ", m_termStatus='" + m_termStatus + '\'' +
                ", m_policyExpirationDate='" + m_policyExpirationDate + '\'' +
                ", m_quoteEndorsementExists='" + m_quoteEndorsementExists + '\'' +
                ", m_quoteRenewalExists='" + m_quoteRenewalExists + '\'' +
                ", m_quoteTempVersionExists='" + m_quoteTempVersionExists + '\'' +
                ", m_initTermB='" + m_initTermB + '\'' +
                ", m_policyTerms=" + m_policyTerms +
                ", m_policyIdentifier=" + m_policyIdentifier +
                ", m_lastTransactionInfo=" + m_lastTransactionInfo +
                ", m_screenModeCode=" + m_screenModeCode +
                ", m_reloadCode=" + m_reloadCode +
                ", m_lastTermB=" + m_lastTermB +
                ", m_wipTransCode=" + m_wipTransCode +
                ", m_appAvailableB=" + m_appAvailableB +
                ", m_riskSumAvailableB=" + m_riskSumAvailableB +
                ", m_skipDefaultRisk=" + m_skipDefaultRisk +
                ", m_skipDefaultCoverage=" + m_skipDefaultCoverage +
                ", m_skipDefaultSubCoverage=" + m_skipDefaultSubCoverage +
                ", m_skipDefaultComponent=" + m_skipDefaultComponent+
                ", m_orgHierRootId=" + m_orgHierRootId +
                ", m_polPhaseCode=" + m_polPhaseCode +
                ", m_cxlWipTermOffExpDate=" + m_cxlWipTermOffExpDate +
                ", m_isPreviewRequest=" + m_isPreviewRequest +
                ", m_excludeCompGr1B=" + m_excludeCompGr1B +
                ", m_excludeCompGr2B=" + m_excludeCompGr2B
        );

        // add riskHeader infomation
        sb.append(',');

        if (hasRiskHeader()){
            sb.append("m_riskHeader(...)");
        } else {
            sb.append("m_riskHeader=null)");
        }

        // add coverageHeader information
        sb.append(',');

        if (hasCoverageHeader()){
            sb.append("m_coverageHeader(...)");
        } else {
            sb.append("m_coverageHeader=null)");
        }

        sb.append("}");

        return sb.toString();
    }

    private String m_policyHolderName;
    private String m_policyHolderNameEntityId;
    private PolicyCycleCode m_policyCycleCode;
    private QuoteCycleCode m_quoteCycleCode;
    private String m_policyTypeCode;
    private boolean m_wipB;
    private ProcessStatus m_processStatusCode;
    private String m_issueStateCode;
    private String m_regionalOffice;
    private boolean m_shortTermB;
    private String m_issueCompanyEntityId;
    private String m_termEffectiveFromDate;
    private String m_termEffectiveToDate;
    private Double m_termWrittenPremium;
    private Double m_writtenPremium;
    private String m_renewalReasonCode;
    private String m_renewalIndicatorCode;
    private boolean m_validRenewalCandidate;
    private boolean m_validPurgeCandidate;
    private boolean m_validOosTerm;
    private String m_curTransactionId;
    private String m_offTransactionId;
    private String m_lastTransactionId;
    private boolean m_showViewMode;
    private boolean m_coveragePartConfigured;
    private boolean m_coverageClassConfigured;
    private PolicyStatus m_policyStatus;
    private PolicyStatus m_termStatus;
    private String m_policyExpirationDate;
    private boolean m_quoteEndorsementExists;
    private boolean m_quoteRenewalExists;
    private boolean m_quoteTempVersionExists;
    private boolean m_initTermB;
    private boolean m_lastTermB;
    private PolicyIdentifier m_policyIdentifier = new PolicyIdentifier();
    private Transaction m_lastTransactionInfo;
    private ScreenModeCode m_screenModeCode;
    private Map m_policyTerms = new LinkedHashMap();
    private RiskHeader m_riskHeader;
    private CoverageHeader m_coverageHeader;
    private Map<String,RiskHeader> cacheRiskHeader = new MaxSizeHashMap<String, RiskHeader> (Integer.valueOf(ApplicationContext.getInstance().getProperty(PM_MAX_CACHE_HEADER_SIZE, "50")));
    private Map<String,CoverageHeader> cacheCoverageHeader = new MaxSizeHashMap<String, CoverageHeader> (Integer.valueOf(ApplicationContext.getInstance().getProperty(PM_MAX_CACHE_HEADER_SIZE, "50")));
    private RecordSet cacheRiskOption = new RecordSet();
    private RecordSet cacheCoverageOption = new RecordSet();
    private RecordSet cacheCoverageRiskOption = new RecordSet();
    private String m_policyLayerCode;
    private String m_policyPolicyFormCode;
    private String m_collateralB;
    private PolicyHeaderReloadCode m_reloadCode;
    private String m_wipTransCode;
    private String m_polPhaseCode;
    //Issue 192351: display official record effective to date for cancel wip case in policy inquiry service
    private String m_cxlWipTermOffExpDate;

    private boolean m_appAvailableB;
    private boolean m_riskSumAvailableB;
    private boolean m_skipDefaultRisk;
    private boolean m_skipDefaultCoverage;
    private boolean m_skipDefaultSubCoverage;
    private boolean m_skipDefaultComponent;

    //Added for issue 103999: display policyholder address in policy header
    private String m_polAddressLine1;
    private String m_polAddressLine2;
    private String m_polAddressLine3;
    private String m_polCity;
    private String m_polStateCode;
    private String m_polZipCode;
    private String m_polInsuredSince;

    // Internal Cached Values for better performance
    private String m_jobCategoryCache;
    private String m_transactionSnapshotCache;
    private String m_undoTermAvailableCache;
    private String m_cancWipEditCache;
    private String m_problemPolicyCache;
    private String m_profEntityCache;
    private String m_orgHierRootId;
    private String m_prodRiskRelationId;

    private boolean m_isPreviewRequest;
    private String m_excludeCompGr1B;
    private String m_excludeCompGr2B;

    private Map<String, String> m_currentSelectedMap = new HashMap<String, String>();

    private static final String PM_MAX_CACHE_HEADER_SIZE = "pm.max.cache.header.size";
}
