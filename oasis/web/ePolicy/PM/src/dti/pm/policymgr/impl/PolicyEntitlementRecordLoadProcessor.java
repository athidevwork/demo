package dti.pm.policymgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.taxmgr.TaxFields;
import dti.pm.policymgr.taxmgr.TaxManager;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskManager;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.componentmgr.ComponentManager;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class extends the default record load processor to enforce entitlements for policy web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published to request as request attributes, which then gets intercepted by
 * pageEntitlements oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 31, 2007
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/04/2008       fcb         postProcessRecordSet: neccessary fields added for 0 recordSet size.
 * 01/18/2008       fcb         postProcessRecord: missing logic added for isOosPolicyChangeAvailable.
 * 09/24/2008       yhyang      Add isAddCompAvailable.
 * 11/10/2009       yhyang      Add isRecordSetHandleLayer().
 * 07/06/2010       syang       Issue - 103797, added isEntityDetailAvailable() to check if Entity Detail button is available.
 * 08/05/2010       dzhang      103800 - Added isMnlQuickPayAvailable() to check if Mnl Quick Pay button is available.
 * 08/23/2010       dzhang      Change the method isMnlQuickPayAvailable() to isManageQuickPayAvailable()
 * 03/18/2011       ryzhao      113559 - Set ERP option available by default.
 * 04/29/2011       dzhang      120329 - Modified isPaymentPlanLstEditable.
 * 05/01/2011       fcb         105791 - Added isConvertCoverageAvailable()
 * 08/15/2011       jshen       123415 - Modified isChangeTermExpDateAvailable() to disable the Change Exp Date button for quote
 * 06/25/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * 08/03/2012       awu         135610 - Modified isAddCompAvailable() to disable the Add button for reinstate WIP.
 * 09/03/2012       adeng       135972 - Modified postProcessRecordSet() to check has risk or not to determine if limit sharing
 *                                       button should be enabled.
 * 10/02/2013       fcb         145725 - isEntityDetailAvailable: changed the parameter.
 *                                     - some optimization in the overall flow.
 * 12/19/2013       jyang       148585 - Correct the parameters which are used to get isCoverageClassAvailable value.
 *                                       And ensure the coverageId and riskId are the right pair.
 * 02/24/2014       xnie        148083 - Modified postProcessRecordSet() to
 *                                       1) Add field isRiskSumAvailable and isRiskAvailable when passed in recordSet's
 *                                          size is 0.
 *                                       2) Check if policy is initial loading, if does, call isRiskSumAvailable()
 *                                          to decide if the new risk page should be shown. Else, use existed value
 *                                          stored in user session to decide if the new risk page should be shown. The
 *                                          original risk page available is exclusive with new risk page.
 * 07/28/2014       awu         152034 - Roll back the changes of issue148585.
 * 10/22/2014       wdang       158112 - Added isMaintainTaxAvailable()
 * 01/06/2016       wdang       168069 - Removed the logic of isRiskSumAvailable/isRiskAvailable.
 * 01/11/2016       tzeng       166924 - Added isPolicyRetroDateEditable indicator.
 * 10/25/2018       xnie        196704 - Added isIssCompanyEntityIdEditable.
 * ---------------------------------------------------
 */
public class PolicyEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {

        // If we are in OOS WIP the process should be that the data on the Policy page
        // is read-only, until the Change button is invoked.  Invoking the Change button
        // will set the ooseChangeB flag to true and the page data should be editable.
        // At this point, the Change button should not be visible, rather the delete button
        // will be visible.  Once data is saved, as standard with an OOSE, further changes
        // should not be permitted.

        // First set the defaults
        record.setFieldValue("isOosPolicyChangeAvailable", YesNoFlag.N);
        record.setFieldValue("isOosPolicyDeleteAvailable", YesNoFlag.N);
        record.setFieldValue("isOoseRequest", YesNoFlag.N);
        Date sTransEffDate = DateUtils.parseDate(getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate());
        Date termEffDate = DateUtils.parseDate(getPolicyHeader().getTermEffectiveFromDate());

        // Set the default values to component buttons.
        record.setFieldValue("isChgCompDateAvailable", YesNoFlag.N);
        record.setFieldValue("isChgCompValueAvailable", YesNoFlag.N);
        record.setFieldValue("isDelCompAvailable", YesNoFlag.N);
        record.setFieldValue("isCycleDetailAvailable", YesNoFlag.N);
        record.setFieldValue("isSurchargePointsAvailable", YesNoFlag.N);

        if (getPolicyHeader().getScreenModeCode().isOosWIP()) {
            boolean isOoseRequest = (getDataRecord().hasStringValue("ooseChangeB") &&
                YesNoFlag.getInstance(getDataRecord().getStringValue("ooseChangeB")).booleanValue());

            if (isOoseRequest) {
                record.setFieldValue("isOoseRequest", YesNoFlag.Y);
                record.setFieldValue("isOosPolicyDeleteAvailable", YesNoFlag.Y);
            } else {
                if (PMCommonFields.getRecordModeCode(record).isOfficial() && sTransEffDate.equals(termEffDate)) {
                    record.setFieldValue("isOosPolicyChangeAvailable", YesNoFlag.Y);
                }
            }

            if (isOoseRequest || PMCommonFields.getRecordModeCode(record).isTemp()) {
                record.setFieldValue("isOosPolicyDeleteAvailable", YesNoFlag.Y);    
            }

            // Fix issue 102697, during OOSWIP, the buttons should be invisible if the current term is not the OOSE initiated term.
            if (!getPolicyHeader().isInitTermB()) {
                record.setFieldValue("isOosPolicyChangeAvailable", YesNoFlag.N);
                record.setFieldValue("isOosPolicyDeleteAvailable", YesNoFlag.N);
            }
        }
        record.setFieldValue("isTailQuoteAvailable", YesNoFlag.N);
        if(!getPolicyHeader().getTermStatus().isCancelled()){
            record.setFieldValue("isTailQuoteAvailable", YesNoFlag.Y);
        }

        // Set value of fields' indicator field
        if (isRecordSetReadOnly()) {
            record.setFieldValue("isRenewalIndicatorEditable", YesNoFlag.N);
            record.setFieldValue("isPolicyNoEditable", YesNoFlag.N);
            record.setFieldValue("isProgramCodeEditable", YesNoFlag.N);
            record.setFieldValue("isProcessLocationEditable", YesNoFlag.N);
            record.setFieldValue("isIbnrDateEditable", YesNoFlag.N);
            record.setFieldValue("isPaymentPlanLstEditable",
                YesNoFlag.getInstance(getPolicyManager().isPaymentPlanLstEditable(record, getPolicyHeader())));
            record.setFieldValue("isDeclinationDateEditable", YesNoFlag.N);
            record.setFieldValue("isIssCompanyEntityIdEditable", YesNoFlag.N);
        }
        else {
            record.setFieldValue("isRenewalIndicatorEditable",
                YesNoFlag.getInstance(getPolicyManager().isRenewalIndicatorEditable(record)));
            record.setFieldValue("isPolicyNoEditable",
                YesNoFlag.getInstance(getPolicyManager().isPolicyNoEditable(record)));
            record.setFieldValue("isProgramCodeEditable",
                YesNoFlag.getInstance(getPolicyManager().isProgramCodeEditable(record)));
            record.setFieldValue("isProcessLocationEditable",
                YesNoFlag.getInstance(getPolicyManager().isProcessLocationEditable(getPolicyHeader())));
            record.setFieldValue("isIbnrDateEditable",
                YesNoFlag.getInstance(getPolicyManager().isIbnrDateEditable()));
            record.setFieldValue("isPaymentPlanLstEditable",
                YesNoFlag.getInstance(getPolicyManager().isPaymentPlanLstEditable(record, getPolicyHeader())));
            record.setFieldValue("isDeclinationDateEditable",
                YesNoFlag.getInstance(getPolicyManager().isDeclinationDateEditable(record)));
            if (getPolicyHeader().getScreenModeCode().isRenewWIP()) {
                record.setFieldValue("isIssCompanyEntityIdEditable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isIssCompanyEntityIdEditable", YesNoFlag.N);
            }

        }

        // Store the original value of the rolling ibnr date for comparisson
        PolicyFields.setOriginalRollingIbnrDate(record, PolicyFields.getRollingIbnrDate(record));     

        // Determine if coverage class option should be enabled
        record.setFieldValue("isCoverageClassAvailable", YesNoFlag.N);
        if (getPolicyHeader().isCoverageClassConfigured()) {
            record.setFieldValue("isCoverageClassAvailable", YesNoFlag.getInstance(getPolicyManager().isCoverageClassAvailable(getPolicyHeader(), record, true)));
        }
        // Enable ERP
        record.setFieldValue("isErpOptionAvailable", YesNoFlag.Y);
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        super.postProcessRecordSet(recordSet);
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isOosPolicyChangeAvailable");
            pageEntitlementFields.add("isOosPolicyDeleteAvailable");
            pageEntitlementFields.add("isOoseRequest");
            pageEntitlementFields.add("isRenewalIndicatorEditable");
            pageEntitlementFields.add("isPolicyNoEditable");
            pageEntitlementFields.add("isProgramCodeEditable");
            pageEntitlementFields.add("isProcessLocationEditable");
            pageEntitlementFields.add("isIbnrDateEditable");
            pageEntitlementFields.add("isCoverageClassAvailable");
            pageEntitlementFields.add("isErpOptionAvailable");
            pageEntitlementFields.add("isIssCompanyEntityIdEditable");
            // Component
            pageEntitlementFields.add("isDelCompAvailable");
            pageEntitlementFields.add("isChgCompValueAvailable");
            pageEntitlementFields.add("isChgCompDateAvailable");
            pageEntitlementFields.add("isCycleDetailAvailable");
            pageEntitlementFields.add("isSurchargePointsAvailable");

            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        Record sumRecord = recordSet.getSummaryRecord();

        boolean isChangeTermExpDateAvailable = isChangeTermExpDateAvailable();
        sumRecord.setFieldValue("isChgTermExpDateAvailable", YesNoFlag.getInstance(isChangeTermExpDateAvailable));

        // check Select Address
        sumRecord.setFieldValue("isSelectAddressAvailable", YesNoFlag.getInstance(isSelectAddressAvailable()));
        // check Policy Administrator History
        sumRecord.setFieldValue("isPolicyAdminHistoryAvailable", YesNoFlag.getInstance(isPolicyAdminHistoryAvailable()));
        // check Quote Status
        sumRecord.setFieldValue("isQuoteStatusAvailable", YesNoFlag.getInstance(isQuoteStatusAvailable()));
        // check Limit sharing
        sumRecord.setFieldValue("isLimitSharingAvailable", YesNoFlag.getInstance(isLimitSharingAvailable()));
        // check Program Retro Date
        if (!isRecordSetReadOnly()) {
            sumRecord.setFieldValue("isPolicyRetroDateEditable", getPolicyManager().isPolicyRetroDateEditable(getPolicyHeader()));
        }
        // Set readOnly attribute to summary record
        if (isRecordSetReadOnly()) {
            EntitlementFields.setReadOnly(sumRecord, true);
        }

        // Fix issue 100751, set handleLayer attribute to summary record
        EntitlementFields.setHandleLayer(sumRecord, isRecordSetHandleLayer());

         // Determine if Add Component action item is show or hide
         sumRecord.setFieldValue("isAddCompAvailable", isAddCompAvailable());
        // Determine if Entity Detail button is show or hide.
        sumRecord.setFieldValue("isEntityDetailAvailable", isEntityDetailAvailable());
        // Determine if Mnl Quick Pay button is show or hide.
        sumRecord.setFieldValue("isManageQuickPayAvailable", isManageQuickPayAvailable());
        // Determine if convert coverage option is show or hide.
        sumRecord.setFieldValue("isConvertCoverageAvailable", isConvertCoverageAvailable());
        // Determine if Maintain Tax button is show or hide.
        sumRecord.setFieldValue("isMaintainTaxAvailable", isMaintainTaxAvailable());
    }

   /**
    * Check Convert Coverage option status
    *
    * @return
    */
   private YesNoFlag isConvertCoverageAvailable() {
       Logger l = LogUtils.enterLog(getClass(), "isConvertCoverageAvailable");
       YesNoFlag isVisible = YesNoFlag.N;

       PolicyHeader policyHeader = getPolicyHeader();
       PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
       ScreenModeCode screenMode = policyHeader.getScreenModeCode();

       if (!viewMode.isOfficial()) {
           return isVisible;
       }
       else if (screenMode.isViewPolicy()) {
           PolicyStatus policyStatus = policyHeader.getPolicyStatus();
           if (policyStatus.isActive()) {
               if (policyHeader.isWipB()) {
                   return isVisible;
               }
               else if (!policyHeader.isQuoteRenewalExists()) {
                   if (policyHeader.getProcessStatusCode().isProcessStatusCancelOnly()) {
                       if (policyHeader.getTermStatus().isActive()) {
                           isVisible = YesNoFlag.Y;
                       }
                   }
                   else if (policyHeader.isLastTerm() && !policyHeader.isShortTermB()) {
                       isVisible = YesNoFlag.Y;
                   }
               }
           }
           else if (policyStatus.isPending()) {
               if (!policyHeader.isQuoteRenewalExists() && !policyHeader.isWipB()) {
                   isVisible = YesNoFlag.Y;
               }
           }
       }

       l.exiting(getClass().getName(), "isConvertCoverageAvailable", isVisible);
       return isVisible;
   }

    private boolean isChangeTermExpDateAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isChangeTermExpDateAvailable");
        boolean isVisible = false;
        PolicyHeader policyHeader = getPolicyHeader();
        PolicyCycleCode pcc = policyHeader.getPolicyCycleCode();
        if (pcc.isPolicy()) {
            TransactionCode transCode = policyHeader.getLastTransactionInfo().getTransactionCode();
            //only a policy is in Offical,renew and reissue,change term exp date is available
            PolicyStatus status = policyHeader.getPolicyStatus();
            //if it is last term
            if (policyHeader.isLastTerm()) {
                if (!policyHeader.isWipB()) {
                    if (status.isCancelled()) {
                        isVisible = false;
                    }
                    else {
                        isVisible = true;
                    }
                }
                else {
                    if (transCode.isRenewal() || transCode.isReissue()) {
                        isVisible = true;
                    }
                    else {
                        isVisible = false;
                    }
                }
            }
            //if it is mid term,we should use policy view mode to tell if a policy
            //is official
            else {
                PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
                if (viewMode.isOfficial()) {
                    isVisible = false;
                }
                else {
                    if (transCode.isRenewal() || transCode.isReissue()) {
                        isVisible = true;
                    }
                    else {
                        isVisible = false;
                    }
                }
            }
        }
        else if (pcc.isQuote()) {
            isVisible = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isChangeTermExpDateAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    private boolean isLimitSharingAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isLimitSharingAvailable");
        boolean isVisible = false;

        if (getDataRecord().hasStringValue(PolicyFields.HAS_RISK)) {
            isVisible = (PolicyFields.getHasRisk(getDataRecord())).booleanValue();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isLimitSharingAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    private boolean isSelectAddressAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isSelectAddressAvailable");
        boolean isVisible = false;

        if (getDataRecord().hasStringValue("userHasProfileForAddress")) {
            isVisible = YesNoFlag.getInstance(getDataRecord().getStringValue("userHasProfileForAddress")).booleanValue();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSelectAddressAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    private boolean isPolicyAdminHistoryAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isPolicyAdminHistoryAvailable");
        boolean isVisible = false;

        if (getPolicyHeader().getPolicyCycleCode().isPolicy()) {
            isVisible = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPolicyAdminHistoryAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    private boolean isQuoteStatusAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isQuoteStatusAvailable");
        boolean isVisible = false;
        if (getPolicyHeader().getPolicyCycleCode().isQuote()) {
            isVisible = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isQuoteStatusAvailable", Boolean.valueOf(isVisible));
        }
        return isVisible;
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        boolean isInitTermB = getPolicyHeader().isInitTermB();
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();

        // All available fields are readonly in following view mode:
        // VIEW_POLICY Mode, VIEW_ENDQUOTE Mode(s) (3.4.3 &3.4.4)
        PolicyViewMode viewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.equals(PolicyViewMode.OFFICIAL) ||
            viewMode.equals(PolicyViewMode.ENDQUOTE)) {
            isReadOnly = true;
        }
        // All available fields are readonly in following screen mode:
        // CANCELWIP,  REINSTATEWIP (3.4.4)
        if (screenMode.isCancelWIP() || screenMode.isResinstateWIP()) {
            isReadOnly = true;
        }
        // In OOSWIP screen Mode (3.4.5)
        // All available fields are readonly when isOoseRequest is false.
        if (screenMode.isOosWIP()) {
            boolean isOoseRequest = (getDataRecord().hasStringValue("ooseChangeB") &&
                YesNoFlag.getInstance(getDataRecord().getStringValue("ooseChangeB")).booleanValue());
            if (!isOoseRequest) {
                isReadOnly = true;
            }
            if(!isInitTermB){
               isReadOnly = true;
            }
        }
        // In RENEWWIP screen Mode
        // All available fields are readonly in prior term, even a tail may be attached to it
        // due to cancel during renewal WIP.
        if (screenMode.isRenewWIP() && !isInitTermB) {
            isReadOnly = true;
        }
        return isReadOnly;
    }

   /**
     * Check if current recordSet should handleLayer.
     * System should set component form to editable for OOS initialized term.
     *
     * @return boolean
     */
    private boolean isRecordSetHandleLayer() {
        boolean isHandleLayer = true;
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        PolicyViewMode viewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode();
        if(screenModeCode.isOosWIP() && getPolicyHeader().isInitTermB() && viewMode.isWIP()){
            isHandleLayer = false;
        }

        return isHandleLayer;
    }


    /**
     * Check if Add component option is available
     * @return YesNoFlag
     */
    private YesNoFlag isAddCompAvailable() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddCompAvailable");
        }

        YesNoFlag isAvailable = YesNoFlag.Y;
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        // If official Add component is never visible
        if (screenModeCode.isViewPolicy() || screenModeCode.isViewEndquote() || screenModeCode.isResinstateWIP()) {
            isAvailable = YesNoFlag.N;
        }
        else {
            if (screenModeCode.isCancelWIP()) {
                Record cancelWipRec = getComponentManager().getCancelWipRule(getPolicyHeader());
                int cancelWipRule = cancelWipRec.getIntegerValue("returnValue").intValue();
                if (cancelWipRule == 0) {
                    isAvailable = YesNoFlag.N;
                }
            }
        }

        // Add component is visible for the initial term during OOSEWIP or RENEWWIP
        if (screenModeCode.isOosWIP() || screenModeCode.isRenewWIP()) {
            PolicyViewMode policyViewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode();
            if (getPolicyHeader().isInitTermB() && policyViewMode.isWIP()) {
                isAvailable = YesNoFlag.Y;
            }
            else {
                isAvailable = YesNoFlag.N;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddCompAvailable", isAvailable);
        }
        return isAvailable;
    }

    /**
     * Check if Entity Detail button is available
     *
     * @return YesNoFlag
     */
    private YesNoFlag isEntityDetailAvailable() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isEntityDetailAvailable");
        }
        YesNoFlag isAvailable = YesNoFlag.N;
        String entityAvailable = getTransactionManager().isEntityDetailAvailable(getPolicyHeader());
        if (!StringUtils.isBlank(entityAvailable) && YesNoFlag.getInstance(entityAvailable).booleanValue()) {
            isAvailable = YesNoFlag.Y;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isEntityDetailAvailable", isAvailable);
        }
        return isAvailable;
    }

    /**
     * Check if Mnl Quick Pay button is available
     * Only visible when policy cycle is policy
     * @return YesNoFlag
     */
    private YesNoFlag isManageQuickPayAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isManageQuickPayAvailable");
        YesNoFlag isVisible = YesNoFlag.N;

        if (getPolicyHeader().getPolicyCycleCode().isPolicy()) {
            isVisible = YesNoFlag.Y;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isManageQuickPayAvailable", isVisible);
        }
        return isVisible;
    }

    /**
     * Check if Maintain Tax button is available
     * Only visible when the tax algorithm is manAlg
     * @return YesNoFlag
     */
    private YesNoFlag isMaintainTaxAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isMaintainTaxAvailable");
        YesNoFlag isVisible = YesNoFlag.N;

        if (TaxFields.TAX_ALGORITHM_M.equals(getTaxManager().getTermAlgorithm(
                getPolicyHeader(), getDataRecord()))) {
            isVisible = YesNoFlag.Y;
        } 
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isMaintainTaxAvailable", isVisible);
        }
        return isVisible;
    }
    
    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
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

    public TaxManager getTaxManager() {
        return m_taxManager;
    }

    public void setTaxManager(TaxManager taxManager) {
        m_taxManager = taxManager;
    }

    public Record getDataRecord() {
        return m_dataRecord;
    }

    public void setDataRecord(Record dataRecord) {
        m_dataRecord = dataRecord;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    public PolicyEntitlementRecordLoadProcessor() {
    }

    public PolicyEntitlementRecordLoadProcessor(PolicyManager policyManager, PolicyHeader policyHeader,
                                                TransactionManager transactionManager,RiskManager riskManager,
                                                ComponentManager componentManager, TaxManager taxManager,
                                                Record dataRecord) {
        setPolicyManager(policyManager);
        setPolicyHeader(policyHeader);
        setTransactionManager(transactionManager);
        setRiskManager(riskManager);
        setComponentManager(componentManager);
        setTaxManager(taxManager);
        setDataRecord(dataRecord);
    }

    private PolicyManager m_policyManager;
    private PolicyHeader m_policyHeader;
    private TransactionManager m_transactionManager;
    private RiskManager m_riskManager;
    private ComponentManager m_componentManager;
    private TaxManager m_taxManager;
    private Record m_dataRecord;
}
