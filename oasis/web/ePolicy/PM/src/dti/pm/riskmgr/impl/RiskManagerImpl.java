package dti.pm.riskmgr.impl;

import dti.ci.addressmgr.AddressFields;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.LoadProcessor;
import dti.oasis.codelookupmgr.CodeLookupManager;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.UserProfiles;
import dti.pm.core.cachemgr.PolicyCacheManager;
import dti.pm.core.data.CommonTabsEntitlementRecordLoadProcessor;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.core.data.RowAccessorRecordLoadProcessor;
import dti.pm.core.http.RequestIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.core.struts.AddAuditHistoryIndLoadProcessor;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.entitymgr.EntityManager;
import dti.pm.pmdefaultmgr.PMDefaultManager;
import dti.pm.policyattributesmgr.PolicyAttributesManager;
import dti.pm.policymgr.CreatePolicyFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.service.RiskInquiryFields;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.riskmgr.RiskManager;
import dti.pm.riskmgr.RiskCopyFields;
import dti.pm.riskmgr.RiskRelationFields;
import dti.pm.riskmgr.coimgr.CoiManager;
import dti.pm.riskmgr.affiliationmgr.AffiliationManager;
import dti.pm.riskmgr.dao.RiskDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.validationmgr.impl.ContinuityRecordValidator;
import dti.pm.validationmgr.impl.PreOoseChangeValidator;
import dti.pm.validationmgr.impl.ShortTermEffectiveToDateRecordValidator;
import dti.pm.validationmgr.impl.StandardEffectiveToDateRecordValidator;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.pm.coverageclassmgr.CoverageClassFields;
import dti.pm.schedulemgr.ScheduleManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.struts.util.LabelValueBean;

/**
 * This Class provides the implementation details of RiskManager Interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/10/2007       sxm         Refactor getInitialDddwForRisk() into PMDefaultManager.
 * 09/12/2007       sxm         Replace String.equals() with StringUtils.isSame() to handle possible NULL values
 * 05/20/2008       joe         Added methods loadAllAddressPhone(), loadAllRiskForCopyAddressPhone() and copyAllAddressPhone()
 * 11/18/2008       yhyang      Added loadAllInsuredHistory().
 * 09/17/2009       fcb         98370: added logic to default info for dddw.
 * 03/01/2010       fcb         104191: transactionLogId passed to getDefaultLevel().
 * 05/07/2010       syang       106894 - Modified getAllNewRisk() to set PrimaryRiskB to "Y" if the current new risk is the first risk.
 * 06/07/2010       Dzhang      101253: Added loadAllProcedureCode().
 * 06/28/2010       dzhang      101253: Added procedureCodes to ProcedureCodeEntitlementRecordLoadProcessor structure method.
 * 08/03/2010       syang       103793 - Added getPrimaryCoverage(), loadAllRiskSurchargePoint() and saveAllRiskSurchargePoint().
 * 08/20/2010       syang       107937 - Modified validateRiskCopyTarget() to validate the fields specialty and network when they are available.
 * 08/30/2010       dzhang      108261 - Added getAllFieldForCopyAll(), processCopyAllConfigField() modified copyAllRisk().
 * 09/20/2010       syang       Issue 110819 - Added FilterOfficialRowForEndquoteRecordLoadProcessor to loadAllRisk().
 * 01/19/2011       wfu         113566 - Added getInitialValuesForCopyNewPolicy(), copyNewPolicyFromRisk() to handle copying policy from risk.
 * 01/21/2011       syang       105832 - Set ddlStatus to getInitialValuesForAddRisk(), getInitialValuesForOoseRisk() 
 *                              and getInitialValuesForSlotOccupant().
 * 07/04/2011       ryzhao      121160 - Modified validateAllRisk(), validateForAddSlotOccupant() to change the checking logic
 *                              if a slot risk is occupied from checking the risk name against "VACANT" to checking if the entity id is zero.
 * 08/17/2011       ryzhao      121160 - Rollback the changes.
 * 08/24/2011       dzhang      123877 - Modified getAllNewRisk().
 * 08/30/2011       ryzhao      124458 - Modified validateAllRisk to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 08/31/2011       ryzhao      124458 - Remove DateUtils.formatDate() and call FormatUtils.formatDateForDisplay() directly.
 * 10/18/2011       lmjiang     126975 - Add a hidden field 'transEffDate' for OBR rule.
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 06/05/2012       tcheng      133869 - Modified validateAllRisk to filter validate for slot occupant risk.
 * 07/06/2012       tcheng      133964 - Added loadAllInsuredInfo().
 * 07/17/2012       sxm         Issue 135029 - Added new logic to get Go To Risk List for Coverage class from back end
 *                                             to improve performance
 * 07/19/2012       awu         134738 - Modified validateAllRisk to check the expiration date is null or not.
 * 07/24/2012       awu         129250 - Added processAutoSaveAllRisk(), processSaveAllRiskData().
 *                                       Modified processSaveAllRisk() to call processSaveAllRiskData. 
 * 08/16/2012       awu         136253 - Modified getInitialValuesForSlotOccupant, if slot risk's expiration date is after
 *                                       policy expiration date, set the occupant risk's expiration date to the policy expiration date,
 *                                       or else, set it to the slot risk's expiration date.
 * 11/02/2012       xnie        121875 - Modified isEffectiveToDateEditable() to add a condition for short term risk.
 * 12/13/2012       awu         138624 - Modified validateRiskCopyTarget() to set the component value of new doctor component to empty.
 * 04/22/2013       xnie        142770 - 1) Added setRecordModeCode() to set correct record mode code.
 *                                       2) Modified loadRiskHeader() to call setRecordModeCode() to set record mode code and set endQuoteId.
 *                                       3) Modified loadAllRisk() to call setRecordModeCode() to set record mode code.
 *                                       4) Modifed loadAllRiskWithCoverageClass() to call setRecordModeCode() to set record mode code.
 *                                       5) Modifed loadAllRiskForWs() to call setRecordModeCode() to set record mode code.
 * 04/25/2013       xnie        142770 - Roll backed prior fix.
 * 05/03/2013       tcheng      143761 - Modified validateAllRisk() to filter official risk.
 * 05/24/2013       xnie        142949 - 1) Modified copyAllRisk() to replace hard code which sets isValidToCopy with
 *                                          call coverage class manager's validateCopyAllCoverageClass().
 *                                       2) Modified validateRiskCopyTarget() to replace hard code which sets isValidToCopy
 *                                          with call coverage class manager's validateCopyAllCoverageClass().
 * 06/06/2013       adeng       Pass one more parameter Record inputRecord when calling method copyAllCoi().
 * 07/31/2013       hxu         146027 - Added new logic to get Go To Risk List for Coverage form back end to
 *                                       improve performance.
 * 08/06/2013       awu         146878 - Modified loadRiskHeader to call getChainStatus to set the risk current status.
 * 09/17/2013       xnie        146452 - Modified saveAllRisk() to remove the logic which set record mode code to
 *                                       'TEMP'. The record mode code have no further act, but cause problem.
 * 09/24/2013       adeng       147468 - Modified loadRiskHeader() to set recordMode to "OFF_ENDQ" when screen mode
 *                                       code is view Endquote and set endorsement quote id into the record.
 * 10/23/2013       xnie        148246 - Modified addAllRisk() to add a indicator to control if risk effective to date
 *                                       should be updated when user tries to save a new record again.
 * 10/18/2013       fcb         145725 - PreOoseChangeValidator called only for OOSE transactions.
 *                                     - loadAllRisk modified to accept additional indicator regarding whether the
 *                                       entitlements should be processed or not.
 * 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
 * 12/27/2013       xnie        148083 - 1) Added loadAllRiskSummaryOrDetail() to load risk summary or detail
 *                                          information.
 *                                       2) Modified getInitialValuesForAddRisk() to
 *                                          a. call loadAllPracticeState() to check if default practice state code is
 *                                             valid.
 *                                          b. Set riskDetailId and use ViewRiskSummaryEntitlementRecordLoadProcessor
 *                                             to get the default risk entitlement values for pop up risk detail page.
 *                                       3) Modified getInitialValuesForOoseRisk() to set riskDetailId and use
 *                                          ViewRiskSummaryEntitlementRecordLoadProcessor to get the default risk
 *                                          entitlement values for pop up risk detail page.
 *                                       4) Modified getInitialValuesForSlotOccupant() to set riskDetailId/entityId and
 *                                          use ViewRiskSummaryEntitlementRecordLoadProcessor to get the default risk
 *                                          entitlement values for pop up risk detail page.
 *                                       5) Added getRiskDetailId() to get risk detail id of updated record based on
 *                                          gaven risk id/transaction/term eff/exp date.
 *                                       6) Added loadAllPracticeState() to load all of available states for gaven
 *                                          policy type/risk type/eff date/exp date.
 *                                       7) Modified processSaveAllRiskData() to skip save transaction process when
 *                                          risk detail is saved.
 * 01/21/2014       Parker      152127 - Cache a primary coverage into policy header for each risk.
 * 03/20/2014       adeng       149313 - Modified validateAllRisk() to set the correct row number into validation
 *                                       error message.
 * 04/17/2014       adeng       153900 - Modified saveAllRisk() to clear cache when saving newly add risks.
 * 04/22/2014       adeng       154037 - Modified getAllMergedRisk() to set the first record's riskId of the new risks
 *                                       into summary record as the selected risk id.
 * 05/06/2014       xnie        154373 - Modified loadAllRiskSummaryOrDetail() to set RiskSumB to 'Y' only when risk
 *                                       detail id is null.
 * 05/02/2014       awu         154278 - Modified saveAllRisk to call deleteAllRisk before addAllRisk.
 * 05/23/2014       wdang       154168 - 1) Modified validateAllRisk() to remove unnecessary code.
 *                                       2) Modified validateForAddSlotOccupant() to exclude deleted risk from being
 *                                          validated.
 * 07/16/2014       jyang       149493 - Updated param NUMBER_FIELDS to use the same field ids defined in base configuration.
 * 09/16/2014       awu         157552 - 1. Modified addAllRisk to add duplication validation.
 *                                       2. Added setInputForDuplicateValidation, handleDuplicateValidation.
 * 11/13/2014       kxiang      157730 - Modified loadAllProcedureCode to remove formal parameter policyHeader.
 * 11/14/2014       kxiang      158495 - Modified getInitialValuesForAddRisk to change set stateCode/countyCode logic.
 * 11/20/2014       awu         154316 - Added getRiskSequenceId for Policy Change Service running.
 * 11/26/2014       wdang       158689 - Modified getInitialValuesForSlotOccupant to set initial row style.
 * 11/27/2014       kxiang      158657 - Removed codes about Location2, as it's obsolete.
 * 12/23/2014       xnie        160018 - 1) Modified loadAllRisk() to load Row accessor process regardless
 *                                          processEntitlements indicator.
 *                                       2) Modified validateAllRisk() to reset m_recordNumber based on
 *                                          displayRecordNumber.
 * 12/24/2014       xnie        156995 - 1) Modified isEffectiveToDateEditable() to roll back 121875 fix.
 *                                       2) Modified validateAllRisk() to add a validation: Short term official risk
 *                                          expiration date and other fields can NOT be changed in non-out of sequence
 *                                          transaction at the same time.
 * 01/07/2015       xnie        160022 - Modified saveAllRisk() and addAllRisk() to move duplicate validation and
 *                                       default coverages logic from addAllRisk() to saveAllRisk().
 * 02/03/2015       xnie        156995 - Modified validateAllRisk() to set default value for expireB.
 * 07/13/2015       xnie        164407 - Modified loadAllRisk(), loadAllRiskSummaryOrDetail(), loadRiskHeader() to call
 *                                       policyHeader.getEvalDate().
 * 05/07/2016       wdang       157211 - 1) Added another isEffectiveToDateEditable to prevent calling DAO extra.
 *                                       2) Modified getInitialValuesForAddRisk to put isInsuredTrackingAvailable.
 * 09/04/2015       tzeng       164679 - 1) Modified addAllRisk() and add RISK_RELATION_MESSAGE_TYPE to judge if auto
 *                                          risk relation is configured then to process auto risk relation and return
 *                                          process result to display message.
 *                                       2) Added isAutoRiskRelConfigured() to check if auto risk relation is configured
 *                                          from cache.
 * 01/15/2016       tzeng       166924 - 1) Modified getInitialValuesForAddRisk to set isAlternativeRatingMethodEditable
 *                                          initial value.
 *                                       2) Added isAlternativeRatingMethodEditable().
 * 01/28/2016       wdang       169024 - 1) Renamed isAutoRiskRelConfigured to getProdAutoRiskRelationId.
 *                                       2) Modified addAllRisk to encapsulate a message object into user session.
 * 02/29/2016       ssheng      169741 - Modified validateCopyAllAddressPhone()
 *                                       to add the separator ',' to addressPhoneIds.
 * 06/08/2016       fcb         177372 - Changed int to long
 * 07/11/2016       lzhang      177681 - Modified copyAllRisk to get toCovgBaseRecordIds.
 * 08/12/2016       eyin        177410 - Added validateTempCovgExist() and performAutoDeleteTempCovgs().
 * 07/17/2017       wrong       168374 - 1) Modified getInitialValuesForAddRisk() to get default value for pcf county
 *                                          and pcf risk.
 *                                       2) Add new methods loadIsFundStateValue, getDefaultValueForPcfCounty and
 *                                          getDefaultValueForPcfRiskClass.
 * 04/02/2018       tzeng       192229 - Added isAddtlExposureAvailable, m_policyAttributesManager.
 * 05/15/2018       ryzhao      192675 - Modified isAddtlExposureAvailable to get risk type code from policy header
 *                                       if not existing in the input record.
 * 07/05/2018       ryzhao      187070 - 1) Modified getInitialValuesForAddRisk() function to set suppress indicator
 *                                       default value to the risk when it is new added.
 *                                       2) Modified validateAllRisk() method to add validation logic.
 *                                       When the suppress indicator fields are changed, the risk expiration date
 *                                       can only be the term expiration date.
 * 11/09/2018       wrong       194062 - Modified saveDefaultRisk() to retrieve policy type code value in Select Policy
 *                                       Type page.
 * ---------------------------------------------------
 */
public class RiskManagerImpl implements RiskManager, RiskSaveProcessor {

    public static final String MAINTAIN_RISK_ACTION_CLASS_NAME = "dti.pm.riskmgr.struts.MaintainRiskAction";
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRisk(PolicyHeader policyHeader) {
        return loadAllRisk(policyHeader, null);
    }

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRisk(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRisk", new Object[]{policyHeader});
        }

        RecordSet rs = loadAllRisk(policyHeader, loadProcessor, true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRisk", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param processEntitlements true/false indicator for processing entitlements
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRisk(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, boolean processEntitlements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRisk", new Object[]{policyHeader});
        }
        // Set the input record
        Record inputRecord = policyHeader.toRecord();
        RecordMode recordModeCode = RecordMode.TEMP;
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (viewMode.isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        PMCommonFields.setRecordModeCode(inputRecord, recordModeCode);

        // Row accessor process will set display indicator to hide records, so this process is not depended on
        // processEntitlements indicator.
        RecordLoadProcessor rowAccessorLP = new RowAccessorRecordLoadProcessor(
            RiskFields.RISK_ID, RiskFields.RISK_EFFECTIVE_FROM_DATE,
            RiskFields.RISK_EFFECTIVE_TO_DATE, policyHeader, policyHeader.getScreenModeCode());
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, rowAccessorLP);

        if (processEntitlements) {
            // Setup the record load processor
        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        RecordLoadProcessor entitlementRLP = new RiskEntitlementRecordLoadProcessor(
                    this, policyHeader, screenModeCode, loadAllRiskTypeAddCode());
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, entitlementRLP);

        CommonTabsEntitlementRecordLoadProcessor commonTabsLP = new CommonTabsEntitlementRecordLoadProcessor(policyHeader);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, commonTabsLP);

        // Add original value for validation
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(origFieldLoadProcessor, loadProcessor);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, new AddAuditHistoryIndLoadProcessor());
        // Issue 110819, filter official record for end quote.
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor = new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "riskId");
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, endquoteLoadProcessor);
        }

        // Determine upon which date to find primary information
        inputRecord.setFieldValue("evalDate", policyHeader.getEvalDate());

        // Get the risk DAO
        RiskDAO r = getRiskDAO();

        // Call the DAO to load risk data
        RecordSet rs = r.loadAllRisk(inputRecord, loadProcessor);

        // Get system parameter "PM_REINST_IBNR_RISK"
        String sysPara = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_REINST_IBNR_RISK, "N");
        rs.getSummaryRecord().setFieldValue(RiskFields.IS_REINSTATE_IBNR_RISK_VALID, sysPara);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRisk", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of available risk summary or detail for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRiskSummaryOrDetail(PolicyHeader policyHeader) {
        return loadAllRiskSummaryOrDetail(policyHeader, null, null);
    }

    /**
     * Returns a RecordSet loaded with list of available risk summary or detail for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param riskDetailId risk detail id
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRiskSummaryOrDetail(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, String riskDetailId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskSummaryOrDetail", new Object[]{policyHeader});
        }

        RecordSet rs = loadAllRiskSummaryOrDetail(policyHeader, loadProcessor, riskDetailId, true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskSummaryOrDetail", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of available risk summary or detail for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param riskDetailId risk detail id
     * @param processEntitlements true/false indicator for processing entitlements
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRiskSummaryOrDetail(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, String riskDetailId, boolean processEntitlements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskSummaryOrDetail", new Object[]{policyHeader});
        }
        // Set the input record
        Record inputRecord = policyHeader.toRecord();

        RiskFields.setRiskDetailId(inputRecord, riskDetailId);
        // If risk detail id is null, system should be loading new risk tab page.
        if (StringUtils.isBlank(riskDetailId)) {
            RiskFields.setRiskSumB(inputRecord, "Y");
        }

        RecordMode recordModeCode = RecordMode.TEMP;
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (viewMode.isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        PMCommonFields.setRecordModeCode(inputRecord, recordModeCode);

        if (processEntitlements) {
            // Setup the record load processor
            RecordLoadProcessor rowAccessorLP = new RowAccessorRecordLoadProcessor(
                RiskFields.RISK_ID, RiskFields.RISK_EFFECTIVE_FROM_DATE,
                RiskFields.RISK_EFFECTIVE_TO_DATE, policyHeader, policyHeader.getScreenModeCode());
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, rowAccessorLP);

            ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
            RecordLoadProcessor entitlementRLP = new ViewRiskSummaryEntitlementRecordLoadProcessor(
                this, policyHeader, screenModeCode, loadAllRiskTypeAddCode());
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, entitlementRLP);

            CommonTabsEntitlementRecordLoadProcessor commonTabsLP = new CommonTabsEntitlementRecordLoadProcessor(policyHeader);
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, commonTabsLP);

            // Add original value for validation
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(origFieldLoadProcessor, loadProcessor);
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, new AddAuditHistoryIndLoadProcessor());
            FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor = new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "riskId");
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, endquoteLoadProcessor);
        }

        // Determine upon which date to find primary information
        inputRecord.setFieldValue("evalDate", policyHeader.getEvalDate());

        // Get the risk DAO
        RiskDAO r = getRiskDAO();

        // Call the DAO to load risk summary or detail data
        RecordSet rs = r.loadAllRisk(inputRecord, loadProcessor);

        // Get system parameter "PM_REINST_IBNR_RISK"
        String sysPara = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_REINST_IBNR_RISK, "N");
        rs.getSummaryRecord().setFieldValue(RiskFields.IS_REINSTATE_IBNR_RISK_VALID, sysPara);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskSummaryOrDetail", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of risks that have coverage class defined
     * for the provided policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRiskWithCoverageClass(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskWithCoverageClass", new Object[]{policyHeader});
        }

        // Set the input record
        Record inputRecord = policyHeader.toRecord();
        RecordMode recordModeCode = RecordMode.TEMP;
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (viewMode.isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        PMCommonFields.setRecordModeCode(inputRecord, recordModeCode);

        // Call the DAO to load risk data
        RecordSet rs = getRiskDAO().loadAllRiskWithCoverageClass(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskWithCoverageClass", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of risks that have coverage defined
     * for the provided policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRiskWithCoverage(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskWithCoverage", new Object[]{policyHeader});
        }

        // Set the input record
        Record inputRecord = policyHeader.toRecord();
        RecordMode recordModeCode = RecordMode.TEMP;
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (viewMode.isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        PMCommonFields.setRecordModeCode(inputRecord, recordModeCode);

        // Call the DAO to load risk data
        RecordSet rs = getRiskDAO().loadAllRiskWithCoverage(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskWithCoverage", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param insuredNumberId  the id of the risk that needs to be retrieved, if available.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRiskForWs(PolicyHeader policyHeader, String insuredNumberId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskForWs", new Object[]{policyHeader, insuredNumberId});
        }

        Record inputRecord = policyHeader.toRecord();
        RecordMode recordModeCode = RecordMode.TEMP;
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (viewMode.isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        PMCommonFields.setRecordModeCode(inputRecord, recordModeCode);

        inputRecord.setFieldValue(RiskInquiryFields.SOURCE_ID, insuredNumberId);
        inputRecord.setFieldValue(RiskInquiryFields.SOURCE_TABLE, RiskInquiryFields.RISK_SOURCE_TABLE);

        RecordLoadProcessor loadProcessor = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;
        RecordSet rs = getRiskDAO().loadAllRisk(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskForWs", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of existing risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  input Record
     * @param processor    to add select check box
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllExistingRisk(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor processor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllExistingRisk", new Object[]{policyHeader});
        }
        Record record = policyHeader.toRecord();
        String transEff = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();

        RiskFields.setRiskBaseRecordId(record, RiskFields.getRiskBaseRecordId(inputRecord));
        TransactionFields.setTransactionEffectiveFromDate(record, transEff);
        RecordSet rs = getRiskDAO().loadAllExistingRisk(record, processor);
        //if no data is found, the fields are defaulted with the policyholder's information
        if (rs.getSize() == 0) {
            rs = new RecordSet();
            String entityId = policyHeader.getPolicyHolderNameEntityId();
            Record rec = new Record();
            RiskFields.setRiskBaseRecordId(rec, "0");
            RiskFields.setRiskName(rec, policyHeader.getPolicyHolderName());
            RiskFields.setEntityId(rec, entityId);
            RiskFields.setEntityType(rec, getEntityManager().getEntityType(entityId));
            RiskFields.setRiskTypeDesc(rec, "Policyholder");
            rec.setFieldValue(RequestIds.SELECT_IND, "0");
            rs.addRecord(rec);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllExistingRisk", rs);
        }
        return rs;
    }

    /**
     * AJAX retrieval of risk additional information fields.
     *
     * @param inputRecord a record with the passed request values.
     * @return Record output record containing the additional info fields
     */
    public Record loadRiskAddlInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadRiskAddlInfo", new Object[]{inputRecord});
        }

        Record outputRecord;

        // Get the risk DAO to perform the operation
        RiskDAO r = getRiskDAO();

        // Call the stored proc to determine validity
        outputRecord = r.loadRiskAddlInfo(inputRecord);

        //set FTE available info
        if (getFteFacilityCount(inputRecord) > 0) {
            outputRecord.setFieldValue("isEmpPhysAvailable", YesNoFlag.Y);
        }
        else {
            outputRecord.setFieldValue("isEmpPhysAvailable", YesNoFlag.N);
        }

        l.exiting(getClass().getName(), "loadRiskAddlInfo");
        return outputRecord;
    }

    /**
     * load all risk summary
     *
     * @param inputRecord input records that contains key infomation
     * @return risk summary
     */
    public RecordSet loadAllRiskSummary(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskSummary", new Object[]{inputRecord});
        }

        RiskSummaryEntitlementRecordLoadProcessor entitlementRLP = new RiskSummaryEntitlementRecordLoadProcessor(policyHeader);
        RecordSet outRecordSet = getRiskDAO().loadAllRiskSummary(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskSummary", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Load the RiskHeader bean of the PolicyHeader object with either the requested
     * risk information, or the primary risk if no specific request was made.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param riskId       primary key value for the desired risk information
     * @return PolicyHeader input PolicyHeader object now loaded with risk information
     */
    public PolicyHeader loadRiskHeader(PolicyHeader policyHeader, String riskId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadRiskHeader", new Object[]{policyHeader, riskId});
        }

        RiskHeader riskHeader = policyHeader.getRiskHeader();

        boolean cached = false;
        Map<String, RiskHeader> cacheRiskHeader = policyHeader.getCacheRiskHeader();
        if (riskHeader != null && riskHeader.getRiskId().equals(riskId)) {
            cached = true;
        } else {
            policyHeader.setRiskHeader(null);
            if (StringUtils.isBlank(riskId)) {
                riskHeader = cacheRiskHeader.get("primaryRisk");
            } else {
                riskHeader = cacheRiskHeader.get(riskId);
            }
            if (riskHeader != null) {
                cached = true;
            }
        }
        // If the riskId is not specified, the first riskId is loaded.
        if (!cached) {
        // Initialize the input record
        Record inputRecord = policyHeader.toRecord();
        inputRecord.setFieldValue("riskId", riskId);

        // Determine upon which date to find primary information
            inputRecord.setFieldValue("evalDate", policyHeader.getEvalDate());

            String recordMode;
            String endorsementQuoteId = null;
            if (policyHeader.getScreenModeCode().isViewPolicy()) {
                recordMode = CoverageFields.CoverageRecordModeValues.OFFICIAL_MOD;
            } else if (policyHeader.getScreenModeCode().isViewEndquote()) {
                recordMode = CoverageFields.CoverageRecordModeValues.OFFICIAL_ENDQ_MOD;
                endorsementQuoteId = policyHeader.getLastTransactionInfo().getEndorsementQuoteId();
            } else {
                recordMode = CoverageFields.CoverageRecordModeValues.OFFICIAL_TEMP_MOD;
        }

        RiskDAO r = getRiskDAO();
            CoverageFields.setRecordMode(inputRecord, recordMode);
            if (endorsementQuoteId != null) {
                TransactionFields.setEndorsementQuoteId(inputRecord, endorsementQuoteId);
            }
        riskHeader = r.loadRiskHeader(policyHeader, inputRecord);

        String riskBaseRecId = riskHeader.getRiskBaseRecordId();

        inputRecord = new Record();
        CoverageFields.setObjId(inputRecord, riskBaseRecId);
        CoverageFields.setRecordMode(inputRecord, recordMode);
        CoverageFields.setDelFlatB(inputRecord, YesNoFlag.N);
        CoverageFields.setStateB(inputRecord, YesNoFlag.N);
            if (endorsementQuoteId != null) {
                TransactionFields.setEndorsementQuoteId(inputRecord, endorsementQuoteId);
            }
        Record outRec = r.getChainStatus(inputRecord);
        PMStatusCode statusCode = PMStatusCode.getInstance(outRec.getStringValue("returnValue"));
        riskHeader.setCurrentRiskStatusCode(statusCode);
        }
        // Add the RiskHeader to the PolicyHeader
        policyHeader.setRiskHeader(riskHeader);
        policyHeader.addCacheRiskHeader(riskHeader);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadRiskHeader", policyHeader);
        }
        return policyHeader;
    }

    /**
     * Wrapper to invoke the save of all inserted/updated Risk records and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader the summary policy information corresponding to the provided risks.
     * @param inputRecords a set of Records, each with the updated Risk Detail info
     *                     matching the fields returned from the loadAllRisk method.
     * @return the number of rows updated.
     */
    public int processSaveAllRisk(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveAllRisk", new Object[]{inputRecords});

        int processCount = processSaveAllRiskData(policyHeader, inputRecords, false);
        
        l.exiting(getClass().getName(), "processSaveAllRisk", new Integer(processCount));
        return processCount;
    }

    /**
     * Wrapper to auto invoke the save of all inserted/updated Risk records and subsequently
     * to invoke the save transaction logic for WIP only.
     *
     * @param policyHeader the summary policy information corresponding to the provided risks.
     * @param inputRecords a set of Records, each with the updated Risk Detail info
     *                     matching the fields returned from the loadAllRisk method.
     * @return the number of rows updated.
     */
    public int processAutoSaveAllRisk(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "processAutoSaveAllRisk", new Object[]{inputRecords});

        int processCount = processSaveAllRiskData(policyHeader, inputRecords, true);

        l.exiting(getClass().getName(), "processAutoSaveAllRisk", new Integer(processCount));
        return processCount;
    }

     /**
     * Wrapper to invoke the save of all inserted/updated Risk records and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader the summary policy information corresponding to the provided risks.
     * @param inputRecords a set of Records, each with the updated Risk Detail info
     *                     matching the fields returned from the loadAllRisk method.
     * @param isAutoSave  a indicator to check it is auto save process or not.
     * @return the number of rows updated.
     */
     protected int processSaveAllRiskData(PolicyHeader policyHeader, RecordSet inputRecords, boolean isAutoSave) {
         Logger l = LogUtils.enterLog(getClass(), "processSaveAllRiskData", new Object[]{inputRecords});

         int processCount = 0;

         // First save all inserted/updated Risk records
         RiskSaveProcessor saveProcessor = (RiskSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
         processCount = saveProcessor.saveAllRisk(policyHeader, inputRecords);

         Record saveRecord = inputRecords.getSummaryRecord();
         boolean saveRiskDetail = false;
         if (saveRecord.hasField(RiskFields.RISK_DETAIL_B)) {
             if (RiskFields.getRiskDetailB(saveRecord).booleanValue()) {
                 saveRiskDetail = true;
             }
         }

         // Complete the save action via the TransactionManager
         // if it is auto save process, no need to save transaction
         if (!isAutoSave && !saveRiskDetail) {
             saveRecord.setFields(policyHeader.toRecord(), false);

             // Force transactionLogId from policyHeader since risk record contains the same fieldId
             saveRecord.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
             PolicyHeaderFields.setPolicyId(saveRecord, policyHeader.getPolicyId());

             saveRecord.setFieldValue("level", "RISK");
             getTransactionManager().processSaveTransaction(policyHeader, saveRecord);
         }

         l.exiting(getClass().getName(), "processSaveAllRiskData", new Integer(processCount));
         return processCount;
     }

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader the summary policy information corresponding to the provided risks.
     * @param inputRecords a set of Records, each with the updated Risk Detail info
     *                     matching the fields returned from the loadAllRisk method.
     * @return the number of rows updated.
     */
    public int saveAllRisk(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllRisk", new Object[]{inputRecords});

        int updateCount = 0;

        // Validate the input risks prior to saving them.
        validateAllRisk(policyHeader, inputRecords);

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        RecordSet cachedChangedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.UPDATED}));

        if (changedRecords.getRecordList().size() > 0) {
            policyHeader.getCacheRiskOption().clear();
            policyHeader.getCacheCoverageRiskOption().clear();
        }
        if (cachedChangedRecords.getRecordList().size() > 0) {
            policyHeader.deleteCacheRiskHeader(cachedChangedRecords);
        }
        // Add the PolicyHeader info to each Risk detail Record
        changedRecords.setFieldsOnAll(policyHeader.toRecord(), false);

        // Set the current transaction id on all records
        changedRecords.setFieldValueOnAll("transactionLogId", policyHeader.getLastTransactionId());

        // Split the input records for add, update and delete

        // Get the WIP records
        RecordSet wipRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet offRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
        RecordSet ooseRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.REQUEST));

        // Delete the WIP records marked for delete in batch mode
        RecordSet deleteRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        updateCount += getRiskDAO().deleteAllRisk(deleteRecords);

        // Add the WIP records in batch mode
        updateCount += addAllRisk(policyHeader, wipRecords);

        // Update the OFFICIAL records marked for update in batch mode
        RecordSet updateRecords = offRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));

        // For the official records set the effective from date equal to the transaction effective date
        // but only for non REQUEST recordModeCode items
        updateRecords.setFieldValueOnAll("transEffectiveFromDate", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        updateRecords.addRecords(ooseRecords);
        updateCount += getRiskDAO().updateAllRisk(updateRecords);

        // Validation for risk duplication.
        if (updateCount > 0) {
            Record inputRecord = setInputForDuplicateValidation(policyHeader);
            Record outputRec = getRiskDAO().validateRiskDuplicate(inputRecord);
            if (outputRec != null && YesNoFlag.N.equals(outputRec.getStringValue("result"))) {
                handleDuplicateValidation(policyHeader, outputRec);
                if (MessageManager.getInstance().hasErrorMessages()) {
                    throw new ValidationException("Risk is duplicated.");
                }
            }
        }

        // Add default coverages for each new risk
        int i;
        RecordSet insertedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        int insertedWipRecordCount = insertedWipRecords.getSize();
        if (!policyHeader.isSkipDefaultCoverage()) {
            for (i = 0; i < insertedWipRecordCount; i++) {
                getCoverageManager().saveAllDefaultCoverage(loadRiskHeader(policyHeader,
                    RiskFields.getRiskId(insertedWipRecords.getRecord(i))));
            }
        }

        // Change the effective to date on all the related tables
        RecordSet allChangedRecords = new RecordSet();
        allChangedRecords.addRecords(wipRecords);
        allChangedRecords.addRecords(updateRecords);
        Iterator recIter = allChangedRecords.getRecords();
        while (recIter.hasNext()) {
            Record record = (Record) recIter.next();
            if (!StringUtils.isSame(RiskFields.getRiskEffectiveToDate(record), RiskFields.getOrigRiskEffectiveToDate(record))) {
                changeTermForEndorseDates(record);
            }
        }

        l.exiting(getClass().getName(), "saveAllRisk", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Create default risk for a newly created policy
     *
     * @param policyHeader the summary policy information
     * @param inputRecord  a record loaded with data passed from select policy page
     * @return the number of rows added.
     */
    public int saveDefaultRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveDefaultRisk", new Object[]{policyHeader});

        // Setup the default risk record
        Record defaultRisk = new Record();
        String riskTypeCode;

        // Add the PolicyHeader info to each Risk detail Record
        defaultRisk.setFields(policyHeader.toRecord(), false);

        // Derive the default risk type
        if (inputRecord.hasField(RiskInquiryFields.RISK_TYPE_CODE)
            && !StringUtils.isBlank(inputRecord.getStringValue(RiskInquiryFields.RISK_TYPE_CODE))) {
            riskTypeCode = inputRecord.getStringValue(RiskInquiryFields.RISK_TYPE_CODE);
        }
        else {
            riskTypeCode = getRiskDAO().getAddDefaultRiskTypeCode(defaultRisk);
        }
        defaultRisk.setFieldValue(RiskFields.RISK_TYPE_CODE, riskTypeCode);

        // Determine the add code
        String addCode = getAddCodeForRisk(defaultRisk);
        defaultRisk.setFieldValue(RiskFields.ADD_CODE, addCode);

        // Setup initial baseline default values that may be modified later
        // Fix issue 104719, default the entityId to policy entity id. 
        defaultRisk.setFieldValue("entityId", policyHeader.getPolicyHolderNameEntityId());
        defaultRisk.setFieldValue("firstRiskB", "Y");

        // Setup entity related attributes based upon Add Code
        if (RiskFields.getAddCode(defaultRisk).equalsIgnoreCase("SLOT") ||
            RiskFields.getAddCode(defaultRisk).equalsIgnoreCase("FTE")) {
            defaultRisk.setFieldValue("slotId", "1");
            defaultRisk.setFieldValue("entityId", "0");
        }

        // Get the initial values
        defaultRisk = getInitialValuesForAddRisk(policyHeader, defaultRisk);

        // Set the current transaction id on all records
        defaultRisk.setFieldValue("transactionLogId", policyHeader.getLastTransactionId());

        // Add the inserted WIP records in batch mode
        defaultRisk.setFieldValue("rowStatus", "NEW");
        RecordSet rs = new RecordSet();
        rs.addRecord(defaultRisk);
        rs.setFieldsOnAll(policyHeader.toRecord(), false);

        int updateCount = getRiskDAO().addAllRisk(rs);

        if ( !policyHeader.isSkipDefaultCoverage() ) {
            // Add default coverage
            getCoverageManager().saveAllDefaultCoverage(loadRiskHeader(policyHeader, RiskFields.getRiskId(defaultRisk)));
        }

        l.exiting(getClass().getName(), "saveDefaultRisk", new Integer(updateCount));
        return updateCount;
    }


    /**
     * Returns a RecordSet loaded with list of available risk types for the provided
     * policy information.
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRiskType(Record inputRecord) {
        RecordSet rs = getRiskDAO().loadAllRiskType(inputRecord);
        if (rs.getSize() == 0) {
            MessageManager.getInstance().addErrorMessage("pm.selectRiskType.NoDataFound");
        }
        return rs;
    }

    /**
     * Returns a String that contains "add code" for a given risk type.
     *
     * @param inputRecord Record contains input values
     * @return String a String contains an add code.
     */
    public String getAddCodeForRisk(Record inputRecord) {
        return getRiskDAO().getAddCodeForRisk(inputRecord);
    }

    /**
     * Validate for adding risk.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     */
    public void validateForAddRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForAddRisk", new Object[]{policyHeader, inputRecord});
        }

        String isValid;
        String addCode = inputRecord.getStringValue("addCode");
        if (addCode.equals("ENTITY")) {
            // check risk entity type
            Record tempRecord = new Record();
            PolicyHeaderFields.setPolicyTypeCode(tempRecord, policyHeader.getPolicyTypeCode());
            RiskFields.setRiskTypeCode(tempRecord, RiskFields.getRiskTypeCode(inputRecord));
            RiskFields.setEntityId(tempRecord, RiskFields.getEntityId(inputRecord));
            isValid = getRiskDAO().isRiskEntityTypeValid(tempRecord);
            if (!YesNoFlag.getInstance(isValid).booleanValue()) {
                MessageManager.getInstance().addErrorMessage("pm.addRisk.invalidEntityType.error");
                throw new ValidationException();
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForAddRisk");
        }
    }

    /**
     * Get initial values for adding risk.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return Record a Record loaded with initial values.
     */
    public Record getInitialValuesForAddRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddRisk", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();

        String riskTypeCode = RiskFields.getRiskTypeCode(inputRecord);

        // get the default values from the workbench configuration for the page corresponding to adding a new risk
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_RISK_ACTION_CLASS_NAME);
        outputRecord.setFields(defaultValuesRecord);

        RiskFields.setAfterImageRecordB(outputRecord, YesNoFlag.Y);
        RiskFields.setBaseRecordB(outputRecord, YesNoFlag.N);
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.TEMP);
        RiskFields.setRiskStatus(outputRecord, PMStatusCode.PENDING);
        RiskFields.setCoveragePartBaseRecordId(outputRecord, "0");
        RiskFields.setOfficialRecordId(outputRecord, "");

        // get the default values based on business rules
        String addCode = inputRecord.getStringValue("addCode");
        String entityId = "0";
        String location = "";
        String slotId = "";
        String riskName = "";
        String locStateCode = "";
        String countyCode = "";
        if (addCode.equals("ENTITY") || addCode.equals("HOSPITAL")) {
            entityId = RiskFields.getEntityId(inputRecord);
            riskName = getEntityManager().getEntityName(entityId);
        }
        else if (addCode.equals("LOCATION")) {
            // Fix issue 104719, system should retrieve entityId from inputRecord and verify the existence of "location".
            entityId = RiskFields.getEntityId(inputRecord);
            if(inputRecord.hasStringValue(RiskFields.LOCATION)){
                location = RiskFields.getLocation(inputRecord);
                RecordSet rs = getRiskDAO().getLocationAddress(location);
                if(rs.getSize() > 0) {
                    riskName = rs.getRecord(0).getStringValue("propertyName");
                    locStateCode = AddressFields.getStateCode(rs.getRecord(0));
                    countyCode = AddressFields.getCountyCode(rs.getRecord(0));
                }
            }
        }
        else if (addCode.equals("SLOT") || addCode.equals("FTE")) {
            slotId = RiskFields.getSlotId(inputRecord);
            // get the slot ID from DB if we don't have one in request
            if (slotId.equals("0"))
                slotId = getRiskDAO().getRiskId(policyHeader.getPolicyId(), riskTypeCode);
            // set the slot ID to 1 if we don't have it in DB neither
            if (slotId.equals("0"))
                slotId = "1";
            if (addCode.equals("SLOT"))
                riskName = "VACANT";
            else
                riskName = "FTE " + slotId;
        }

        YesNoFlag primaryRiskB = YesNoFlag.N;
        if (!StringUtils.isBlank(entityId) && Long.parseLong(entityId) > 0 && inputRecord.hasStringValue("firstRiskB") &&
            YesNoFlag.getInstance(inputRecord.getStringValue("firstRiskB")).booleanValue()) {
            primaryRiskB = YesNoFlag.Y;
        }

        String transEffectiveFromeDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        String callPmDefault = "Y";
        if (addCode.equals("HOSPITAL") || addCode.equals("SLOT") || addCode.equals("FTE") || addCode.equals("LOCATION")) {
            callPmDefault = "N";
        }
        String practiceStateCode = getRiskDAO().getDefaultPracticeState(policyHeader.getIssueStateCode(),
            policyHeader.getRegionalOffice(), entityId, transEffectiveFromeDate, callPmDefault);

        if (addCode.equals("LOCATION") && StringUtils.isBlank(practiceStateCode)) {
            practiceStateCode = locStateCode;
        }
        else if (StringUtils.isBlank(practiceStateCode)) {
            practiceStateCode = policyHeader.getIssueStateCode();
        }

        RiskFields.setEntityId(outputRecord, entityId);
        Record entity = getEntityManager().loadEntityDetail(entityId);
        RiskFields.setEntityType(outputRecord, entity.getStringValue("entityType"));
        RiskFields.setRiskFirstName(outputRecord, entity.getStringValue("firstName"));
        RiskFields.setRiskLastName(outputRecord, entity.getStringValue("lastName"));
        RiskFields.setLocation(outputRecord, location);
        PolicyHeaderFields.setPolicyId(outputRecord, policyHeader.getPolicyId());
        RiskFields.setPrimaryRiskB(outputRecord, primaryRiskB);
        RiskFields.setRiskCounty(outputRecord, countyCode);
        RiskFields.setRiskEffectiveFromDate(outputRecord, transEffectiveFromeDate);
        // issue#106689 default eff to date to policy's expiration date
        RiskFields.setRiskEffectiveToDate(outputRecord, policyHeader.getPolicyExpirationDate());
        RiskFields.setRiskName(outputRecord, riskName);
        RiskFields.setPracticeStateCode(outputRecord, practiceStateCode);
        RiskFields.setRiskTypeCode(outputRecord, riskTypeCode);
        RiskFields.setSlotId(outputRecord, slotId);

        // get the IDs
        Record idRecord = getRiskDAO().getRiskIdAndBaseId(policyHeader.getPolicyId(),
            riskTypeCode, entityId, location, slotId);
        outputRecord.setFields(idRecord);

        // get the default values from PM configuration
        Record defaultValues = getPmDefaultManager().getDefaultLevel("RISK_TAB",
            policyHeader.getLastTransactionInfo().getTransactionLogId(),
            policyHeader.getTermEffectiveFromDate(), transEffectiveFromeDate,
            "POLICY_TYPE_CODE", policyHeader.getPolicyTypeCode(),
            "RISK_TYPE_CODE", riskTypeCode,
            "PRACTICE_STATE_CODE", practiceStateCode);
        outputRecord.setFields(defaultValues);

        String lovFields = getPmDefaultManager().getInitialDddwForRisk(policyHeader.getTermEffectiveFromDate(),
            transEffectiveFromeDate, policyHeader.getPolicyTypeCode(), riskTypeCode, practiceStateCode);

        if (lovFields != null) {
            StringTokenizer lovFieldsToken = new StringTokenizer(lovFields, ",");
            while (lovFieldsToken.hasMoreTokens()) {
                getWorkbenchConfiguration().loadListOfValues(MAINTAIN_RISK_ACTION_CLASS_NAME, outputRecord);
                String fieldId = lovFieldsToken.nextToken();
                ArrayList lov = getWorkbenchConfiguration().getListOfValuesForField(fieldId);
                if (lov != null) {
                    if (lov.size() == 2 && ((LabelValueBean) lov.get(0)).getValue().equals
                        (CodeLookupManager.getInstance().getSelectOptionCode())) {
                        outputRecord.setFieldValue(fieldId, ((LabelValueBean) lov.get(1)).getValue());
                    }
                }
            }
        }

        // Get the default risk entitlement values
        if (inputRecord.hasField(RiskFields.RISK_DETAIL_B)) {
            RiskFields.setRiskDetailId(outputRecord, RiskFields.getRiskId(outputRecord));
            ViewRiskSummaryEntitlementRecordLoadProcessor.setInitialEntitlementValuesForRisk(
                this, policyHeader, policyHeader.getScreenModeCode(),
                loadAllRiskTypeAddCode(), outputRecord);
        }
        else {
        RiskEntitlementRecordLoadProcessor.setInitialEntitlementValuesForRisk(
            this, policyHeader, policyHeader.getScreenModeCode(),
                loadAllRiskTypeAddCode(), outputRecord);
        }

        PolicyHeaderFields.setPolicyTypeCode(outputRecord, policyHeader.getPolicyTypeCode());
        YesNoFlag isDateChangeAllowed = YesNoFlag.getInstance(isDateChangeAllowed(outputRecord));
        outputRecord.setFieldValue("isInsuredTrackingAvailable", isDateChangeAllowed.booleanValue() ? YesNoFlag.N : YesNoFlag.Y);
        outputRecord.setFieldValue("isAlternativeRatingMethodEditable", YesNoFlag.Y);

        // Set initial value for indicator field of "risk expiration date"
        // We don't need to call Ajax to retrieve the value twice
        outputRecord.setFieldValue("isRiskEffectiveToDateEditable",
            isEffectiveToDateEditable(policyHeader, outputRecord, isDateChangeAllowed.booleanValue()));

        if (isEffectiveToDateEditable(policyHeader, outputRecord, isDateChangeAllowed.booleanValue()).booleanValue()) {
            // set risk expiration date if current risk is short term risk
            String transEffectiveToDate = policyHeader.getLastTransactionInfo().getTransEffectiveToDate();
            if (!StringUtils.isBlank(transEffectiveToDate)) {
                RiskFields.setRiskEffectiveToDate(outputRecord, transEffectiveToDate);
            }
        }

        // Get the default Common Tab Entitlement values
        outputRecord.setFields(CommonTabsEntitlementRecordLoadProcessor.getInitialEntitlementValuesForCommonTabs());

        // Add original values
        origFieldLoadProcessor.postProcessRecord(outputRecord, true);

        // Setup intial row style
        RiskRowStyleRecordLoadprocessor.setInitialEntitlementValuesForRowStyle(outputRecord);

        // Set ddl status
        String ddlStatus;
        if (addCode.equals("SLOT") || addCode.equals("FTE")) {
            ddlStatus = "NORECORD";
        }
        else {
            ddlStatus = getRiskDAO().getDisciplineDeclineEntityStatus(inputRecord);
        }
        CancelProcessFields.setDdlStatus(outputRecord, ddlStatus);

        //Set pcf county and specialty value
        String pcfCountyCode = getRiskDAO().getDefaultValueForPcfCounty(outputRecord);
        String pcfRiskClassCode = getRiskDAO().getDefaultValueForPcfRiskClass(outputRecord);
        String isFundState = getRiskDAO().loadIsFundStateValue(outputRecord).getStringValue("returnvalue");
        outputRecord.setFieldValue(RiskFields.PCF_RISK_COUNTY, pcfCountyCode);
        outputRecord.setFieldValue(RiskFields.PCF_RISK_CLASS, pcfRiskClassCode);
        outputRecord.setFieldValue(RiskFields.IS_FUND_STATE, isFundState);

        // Set default value to excludeCompGr1B and excludeCompGr2B fields from policy.
        Record tempRec = new Record();
        PolicyHeaderFields.setPolicyTypeCode(tempRec, policyHeader.getPolicyTypeCode());
        RiskFields.setRiskTypeCode(tempRec, riskTypeCode);
        PMCommonFields.setRecordModeCode(tempRec, RecordMode.TEMP);
        RiskFields.setOfficialRecordId(tempRec, "");
        RiskFields.setRiskEffFromDate(tempRec, transEffectiveFromeDate);
        PolicyHeaderFields.setTermBaseRecordId(tempRec, policyHeader.getTermBaseRecordId());
        boolean isGr1CompVisible = getRiskDAO().isGr1CompVisible(tempRec);
        boolean isGr1CompEditable = getRiskDAO().isGr1CompEditable(tempRec);
        boolean isGr2CompEditable = getRiskDAO().isGr2CompEditable(tempRec);
        RiskFields.setIsGr1CompVisible(outputRecord, YesNoFlag.getInstance(isGr1CompVisible));
        RiskFields.setIsGr1CompEditable(outputRecord, YesNoFlag.getInstance(isGr1CompEditable));
        RiskFields.setIsGr2CompEditable(outputRecord, YesNoFlag.getInstance(isGr2CompEditable));
        if (isGr1CompEditable && !StringUtils.isBlank(policyHeader.getExcludeCompGr1B())) {
            RiskFields.setExcludeCompGr1B(outputRecord, policyHeader.getExcludeCompGr1B());
        }
        if (isGr2CompEditable && !StringUtils.isBlank(policyHeader.getExcludeCompGr2B())) {
            RiskFields.setExcludeCompGr2B(outputRecord, policyHeader.getExcludeCompGr2B());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddRisk", outputRecord);
        }
        return outputRecord;
    }


    /**
     * delete all risk
     *
     * @param policyHeader policy header
     * @param inputRecords input records
     * @return process count
     */
    public int deleteAllRisk(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllRisk", new Object[]{policyHeader,inputRecords});
        }

        int processCount = getRiskDAO().deleteAllRisk(inputRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllRisk", String.valueOf(processCount));
        }

        return processCount;
    }


    /**
     * To get risk base id
     *
     * @param record inputParameters
     * @return risk base ID
     */
    public String getRiskBaseId(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskBaseId", new Object[]{record});
        }

        String riskBaseId = getRiskDAO().getRiskBaseId(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskBaseId", riskBaseId);
        }

        return riskBaseId;
    }

    /**
     * Returns a RecordSet loaded with list of insured history.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllInsuredHistory(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllInsuredHistory", new Object[]{inputRecord});
        }

        RecordSet rs = getRiskDAO().loadAllInsuredHistory(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllInsuredHistory", rs);
        }

        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of locations for the given entity ID
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of locations.
     */
    public RecordSet loadAllLocation(Record inputRecord, RecordLoadProcessor loadProcessor) {
        RecordSet rs = getRiskDAO().loadAllLocation(inputRecord, loadProcessor);
        //set the hidden current transaction date to each record.
        if (inputRecord.hasStringValue(TransactionFields.TRANS_EFF_DATE)) {
            rs.setFieldValueOnAll(TransactionFields.TRANS_EFF_DATE, TransactionFields.getTransEffDate(inputRecord));
        }
        return rs;
    }

    /**
     * check if the effective to date of a risk type can be changed.
     *
     * @param inputRecord
     * @return String a String contains an add code.
     */
    public String isDateChangeAllowed(Record inputRecord) {
        return getRiskDAO().isDateChangeAllowed(inputRecord);
    }

    /**
     * Check if risk effective to date is editable
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public YesNoFlag isEffectiveToDateEditable(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isEffectiveToDateEditable", new Object[]{policyHeader, inputRecord});
        }

        PolicyHeaderFields.setPolicyTypeCode(inputRecord, policyHeader.getPolicyTypeCode());
        boolean isDateChangeAllowed = YesNoFlag.getInstance(isDateChangeAllowed(inputRecord)).booleanValue();
        YesNoFlag isEditable = isEffectiveToDateEditable(policyHeader, inputRecord, isDateChangeAllowed);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isEffectiveToDateEditable", isEditable.getName());
        }
        return isEditable;
    }

    /**
     * Get auto risk relation definition if any.
     * <p/>
     * @param  policyHeader
     * @return product risk relation id
     */
    private String getProdRiskRelationId(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProdRiskRelationId", new Object[]{policyHeader});
        }
        String prodAutoRiskRelationId = policyHeader.getProdRiskRelationId();

        if (StringUtils.isBlank(prodAutoRiskRelationId)) {
            Record record = new Record();
            PolicyFields.setPolicyTypeCode(record, policyHeader.getPolicyTypeCode());
            PolicyFields.setTermEffectiveFromDate(record, policyHeader.getTermEffectiveFromDate());
            PolicyFields.setIssueCompanyEntityId(record, policyHeader.getIssueCompanyEntityId());
            prodAutoRiskRelationId = String.valueOf(getRiskDAO().isAutoRiskRelConfigured(record));
            policyHeader.setProdRiskRelationId(prodAutoRiskRelationId);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProdRiskRelationId", prodAutoRiskRelationId);
        }

        return prodAutoRiskRelationId;
    }

    /**
     * Check if risk effective to date is editable
     *
     * @param policyHeader
     * @param inputRecord
     * @param isDateChangeAllowed
     * @return
     */
    public YesNoFlag isEffectiveToDateEditable(PolicyHeader policyHeader, Record inputRecord, boolean isDateChangeAllowed) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isEffectiveToDateEditable", new Object[]{policyHeader, inputRecord});
        }

        boolean isEditable = false;
        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(inputRecord);
        // Issue#102167 added condition for new added risk in OOSE.
        if ((screenModeCode.isOosWIP() && (recordModeCode.isRequest() || (recordModeCode.isTemp() && isDateChangeAllowed)))
            || (!screenModeCode.isOosWIP() && !screenModeCode.isCancelWIP()
            && !screenModeCode.isResinstateWIP() && isDateChangeAllowed)) {
            isEditable = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isEffectiveToDateEditable", Boolean.valueOf(isEditable));
        }
        return YesNoFlag.getInstance(isEditable);
    }

    /**
     * Check if alternative rating method is editable.
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public YesNoFlag isAlternativeRatingMethodEditable(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAlternativeRatingMethodEditable");
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
                l.throwing(getClass().getName(), "isAlternativeRatingMethodEditable", ae);
                throw ae;
            }
            if (!hasProfile) {
                Record record = new Record();
                RiskFields.setRiskId(record, RiskFields.getRiskId(inputRecord));
                TransactionFields.setTransactionLogId(record, policyHeader.getLastTransactionId());
                isEditable = YesNoFlag.getInstance(getRiskDAO().isAlternativeRatingMethodEditable(record));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAlternativeRatingMethodEditable", isEditable);
        }
        return isEditable;
    }

    /**
     * Get initial values for OOSE risk
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values
     */
    public Record getInitialValuesForOoseRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForOoseRisk", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();

        // Set Default values
        // riskId
        Long lRiskId = getDbUtilityManager().getNextSequenceNo();
        RiskFields.setRiskId(outputRecord, String.valueOf(lRiskId.longValue()));
        // officialRecordId
        RiskFields.setOfficialRecordId(outputRecord, RiskFields.getRiskId(inputRecord));
        // recordModeCode
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.REQUEST);
        // afterImageRecordB
        RiskFields.setAfterImageRecordB(outputRecord, YesNoFlag.Y);
        // transactionCode
        TransactionFields.setTransactionCode(outputRecord, policyHeader.getLastTransactionInfo().getTransactionCode());
        // riskEffectiveFromeDate
        RiskFields.setRiskEffectiveFromDate(outputRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        // riskTypeCode
        RiskFields.setRiskTypeCode(outputRecord, RiskFields.getRiskTypeCode(inputRecord));
        // policyTypeCode
        PolicyHeaderFields.setPolicyTypeCode(outputRecord, policyHeader.getPolicyTypeCode());
        // riskEffectiveToDate
        String newRiskEffToDate = getTransactionManager().getOoseExpirationDate(policyHeader);
        YesNoFlag isEffToDateEditable = isEffectiveToDateEditable(policyHeader, outputRecord);
        if (isEffToDateEditable.booleanValue()) {
            String sRiskEffToDate = RiskFields.getRiskEffectiveToDate(inputRecord);
            String sCurTermExpDate = policyHeader.getTermEffectiveToDate();
            Date riskEffToDate = DateUtils.parseDate(sRiskEffToDate);
            Date curTermExpDate = DateUtils.parseDate(sCurTermExpDate);
            if (riskEffToDate.before(curTermExpDate)) {
                newRiskEffToDate = sRiskEffToDate;
            }
        }
        RiskFields.setRiskEffectiveToDate(outputRecord, newRiskEffToDate);

        // Set other columns same as the original outputRecord for which Change was invoked
        // riskStatus
        RiskFields.setRiskStatus(outputRecord, PMStatusCode.PENDING);
        // original riskEffectiveToDate, it will be used when saveAllRisk() calls changeTermForEndorseDates().
        outputRecord.setFieldValue(RiskFields.ORIG_RISK_EFFECTIVE_TO_DATE, RiskFields.getRiskEffectiveToDate(inputRecord));

        // set inputRecords into outputRecord
        outputRecord.setFields(inputRecord, false);

        // Get the default risk entitlement values
        if (inputRecord.hasField(RiskFields.RISK_DETAIL_B)) {
            RiskFields.setRiskDetailId(outputRecord, RiskFields.getRiskId(outputRecord));
            ViewRiskSummaryEntitlementRecordLoadProcessor.setInitialEntitlementValuesForRisk(
                this, policyHeader, policyHeader.getScreenModeCode(),
                loadAllRiskTypeAddCode(), outputRecord);
        }
        else {
        RiskEntitlementRecordLoadProcessor.setInitialEntitlementValuesForRisk(
            this, policyHeader, policyHeader.getScreenModeCode(),
                loadAllRiskTypeAddCode(), outputRecord);
        }

        // remove some fields
        outputRecord.remove(RiskFields.RISK_NAME);

        // Set Change option to invisible for the new outputRecord
        outputRecord.setFieldValue("isOosChangeAvailable", YesNoFlag.N);
        // Set Delete option to visible for the new outputRecord
        outputRecord.setFieldValue("isDeleteAvailable", YesNoFlag.Y);

        // Setup intial row style
        RiskRowStyleRecordLoadprocessor.setInitialEntitlementValuesForRowStyle(outputRecord);

        // Set ddl status
        String ddlStatus = getRiskDAO().getDisciplineDeclineEntityStatus(inputRecord);
        CancelProcessFields.setDdlStatus(outputRecord, ddlStatus);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForOoseRisk", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Change the effective to date on all related tables.
     *
     * @param inputRecord
     */
    protected void changeTermForEndorseDates(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeTermForEndorseDates", new Object[]{inputRecord});
        }

        inputRecord.setFieldValue("mode", "OFFICIAL");
        getRiskDAO().changeTermForEndorseDates(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "changeTermForEndorseDates");
        }
    }

    /**
     * Check if Change option is available
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with the selected risk record.
     */
    public void validateForOoseRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForOoseRisk", new Object[]{policyHeader, inputRecord});
        }

        String recordModeCode = "";
        if (inputRecord.hasStringValue("recordModeCode")) {
            recordModeCode = inputRecord.getStringValue("recordModeCode");
        }
        if (policyHeader.getScreenModeCode().isOosWIP() && !"TEMP".equalsIgnoreCase(recordModeCode)) {
            // check if Change option is available or not
            YesNoFlag isChangeAvailable = YesNoFlag.N;

            String sTransEffDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
            String sRiskEffDate = RiskFields.getRiskEffectiveFromDate(inputRecord);
            Date transEffDate = DateUtils.parseDate(sTransEffDate);
            Date riskEffDate = DateUtils.parseDate(sRiskEffDate);

            Record record = new Record();
            RiskFields.setRiskEffectiveFromDate(record, RiskFields.getRiskEffectiveFromDate(inputRecord));
            RiskFields.setRiskBaseRecordId(record, RiskFields.getRiskBaseRecordId(inputRecord));
            boolean isOosRiskValid = YesNoFlag.getInstance(getRiskDAO().isOosRiskValid(record)).booleanValue();

            // 1. If the term displayed is the term in which the OOS transaction was initiated
            // 2. If Effective from date of the row is <= the OOS transaction effective date
            // 3. If the query from Pm_Valid_Oos_Risk returns a 'Y'
            if (policyHeader.isInitTermB() && !transEffDate.before(riskEffDate) && isOosRiskValid) {
                isChangeAvailable = YesNoFlag.Y;
            }

            if (!isChangeAvailable.booleanValue()) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRisk.ooseRisk.changeOption.error");
                throw new ValidationException();
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForOoseRisk");
        }
    }

    /**
     * Save all WIP input records with UPDATE_IND set to 'Y' - updated, or 'I' - inserted
     *
     * @param policyHeader the summary policy information corresponding to the provided risks.
     * @param wipRecords   a set of Records in WIP mode, each with the updated Risk Detail info
     *                     matching the fields returned from the loadAllRisk method.
     * @return the number of rows updated.
     */
    private int addAllRisk(PolicyHeader policyHeader, RecordSet wipRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllRisk", new Object[]{wipRecords});

        int updateCount = 0;

        // Add the inserted WIP records in batch mode
        RecordSet insertedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        insertedWipRecords.setFieldValueOnAll("rowStatus", "NEW");
        insertedWipRecords.setFieldsOnAll(policyHeader.toRecord(), false);
        updateCount += getRiskDAO().addAllRisk(insertedWipRecords);

        // Judge to call auto risk relation or not.
        if (updateCount > 0 && getProdRiskRelationId(policyHeader) != null) {
            String connection = ",";
            StringBuilder riskIds = new StringBuilder(connection);
            for (Record record : insertedWipRecords.getRecordList()) {
                riskIds.append(RiskFields.getRiskId(record)).append(connection);
            }
            Record inputRecord = new Record();
            RiskFields.setRiskIds(inputRecord, riskIds.substring(0, riskIds.lastIndexOf(connection) + 1).toString());
            PolicyHeaderFields.setPolicyId(inputRecord, policyHeader.getPolicyId());
            PolicyHeaderFields.setPolicyTypeCode(inputRecord, policyHeader.getPolicyTypeCode());
            PolicyHeaderFields.setTermEffectiveFromDate(inputRecord, policyHeader.getTermEffectiveFromDate());
            PolicyHeaderFields.setTermEffectiveToDate(inputRecord, policyHeader.getTermEffectiveToDate());
            PolicyHeaderFields.setProdRiskRelationId(inputRecord, getProdRiskRelationId(policyHeader));
            TransactionFields.setTransactionEffectiveFromDate(inputRecord,
                policyHeader.getLastTransactionInfo()
                    .getTransEffectiveFromDate());
            TransactionFields.setTransactionCode(inputRecord,
                policyHeader.getLastTransactionInfo().getTransactionCode());
            TransactionFields.setTransactionLogId(inputRecord,
                policyHeader.getLastTransactionInfo().getTransactionLogId());
            String autoRiskRelResult = getRiskDAO().processAutoRiskRelation(inputRecord);

            // encapsulate message object if any.
            Message message = null;
            if ("Y".equals(autoRiskRelResult)) {
                message = new Message();
                message.setMessageCategory(MessageCategory.INFORMATION);
                message.setMessageKey("pm.maintainRiskRelation.autoRiskRelation.success.info");
            }
            else if ("M".equals(autoRiskRelResult)) {
                message = new Message();
                message.setMessageCategory(MessageCategory.WARNING);
                message.setMessageKey("pm.maintainRiskRelation.autoRiskRelation.multipleOwnerRiskType.warning");
            }
            else if ("P".equals(autoRiskRelResult)) {
                message = new Message();
                message.setMessageCategory(MessageCategory.WARNING);
                message.setMessageKey("pm.maintainRiskRelation.autoRiskRelation.incompleteDueToMultipleParents.warning");
            }

            // store message into user session
            if (message != null) {
                UserSession userSession = UserSessionManager.getInstance().getUserSession();
                if (!userSession.has(UserSessionIds.POLICY_SAVE_MESSAGE)) {
                    userSession.set(UserSessionIds.POLICY_SAVE_MESSAGE, new ArrayList());
                }
                List<Message> messageList = (List<Message>) userSession.get(UserSessionIds.POLICY_SAVE_MESSAGE);
                messageList.add(message);
            }
        }

        // Add the updated WIP records in batch mode
        RecordSet updatedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        updatedWipRecords.setFieldValueOnAll("rowStatus", "MODIFIED");

        // Decide if risk effective to date should be updated.
        // When current transaction is out of sequence transaction and effective to date is NOT changed, system should
        // NOT update effective to date.
        TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();
        if (transactionCode.isOosEndorsement()) {
            Iterator recIter = updatedWipRecords.getRecords();
            while (recIter.hasNext()) {
                Record record = (Record) recIter.next();
                String riskEffectiveToDateOrg = RiskFields.getOrigRiskEffectiveToDate(record);
                String riskEffectiveToDate = RiskFields.getRiskEffectiveToDate(record);
                if (!riskEffectiveToDateOrg.equals(riskEffectiveToDate)) {
                    RiskFields.setUpdateExpB(record, YesNoFlag.Y);
                }
                else {
                    RiskFields.setUpdateExpB(record, YesNoFlag.N);
                }
            }
        }

        updatedWipRecords.setFieldsOnAll(policyHeader.toRecord(), false);
        updateCount += getRiskDAO().addAllRisk(updatedWipRecords);

        l.exiting(getClass().getName(), "addAllRisk", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Prepare the input parameters to do duplication validation.
     * @param policyHeader
     * @return
     */
    private Record setInputForDuplicateValidation(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "setInputForDuplicateValidation",
            new Object[]{policyHeader});

        Record parameterRecord = new Record();
        TransactionFields.setTransactionLogId(parameterRecord, policyHeader.getCurTransactionId());
        PolicyFields.setTermEffectiveFromDate(parameterRecord, policyHeader.getTermEffectiveFromDate());
        PolicyFields.setTermEffectiveToDate(parameterRecord, policyHeader.getTermEffectiveToDate());

        l.exiting(getClass().getName(), "setInputForDuplicateValidation", parameterRecord);
        return parameterRecord;
    }

    /**
     * Handle the validation error message after duplication validation is failed.
     * @param policyHeader
     * @param resultRecord
     */
    private void handleDuplicateValidation(PolicyHeader policyHeader, Record resultRecord) {
        Logger l = LogUtils.enterLog(getClass(), "handleDuplicateValidation",
            new Object[]{policyHeader, resultRecord});

        String validateMessage = resultRecord.getStringValue("validateMessage");
        String riskId = RiskFields.getRiskId(resultRecord);
        MessageManager.getInstance().addErrorMessage("pm.common.invalid.data",
            new String[]{validateMessage}, "", riskId);

        l.exiting(getClass().getName(), "handleDuplicateValidation");
    }

    protected void validateAllRisk(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllRisk", new Object[]{policyHeader, inputRecords});
        }

        if (policyHeader.getLastTransactionInfo().getTransactionCode().isOosEndorsement()) {
        // Pre-Oose Change validations
        PreOoseChangeValidator preOoseChangeValidator = new PreOoseChangeValidator(
            null, "risk", RiskFields.RISK_ID, RiskFields.RISK_BASE_RECORD_ID);
        preOoseChangeValidator.validate(inputRecords);
        }

        // Get an instance of the Standard Effective To Date Rule Validator
        StandardEffectiveToDateRecordValidator effToDateValidator =
            new StandardEffectiveToDateRecordValidator(policyHeader,
                RiskFields.RISK_EFFECTIVE_FROM_DATE, RiskFields.RISK_EFFECTIVE_TO_DATE,
                RiskFields.RISK_ID, RiskFields.ORIG_RISK_EFFECTIVE_TO_DATE);

        RecordSet overlapCheckRecords = inputRecords.getSubSet(
            new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP)).
            getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}));
        Iterator overlapCheckIt = overlapCheckRecords.getRecords();
        // Filter out flat cancelled rows
        while (overlapCheckIt.hasNext()) {
            Record rec = (Record) overlapCheckIt.next();
            String sRiskEffFromDate = RiskFields.getRiskEffectiveFromDate(rec);
            String sRiskEffToDate = RiskFields.getRiskEffectiveToDate(rec);

            if (RiskFields.getRiskStatus(rec).isCancelled()
                && !StringUtils.isBlank(sRiskEffFromDate)
                && sRiskEffFromDate.equals(sRiskEffToDate)) {
                // If it is flat cancel, removed from record set
                overlapCheckIt.remove();
            }
        }

        // Get an instance of the Continuity Validator
        ContinuityRecordValidator continuityRecordValidator =
            new ContinuityRecordValidator(RiskFields.RISK_EFFECTIVE_FROM_DATE, RiskFields.RISK_EFFECTIVE_TO_DATE,
                RiskFields.RISK_ID, "pm.addRisk.riskExists.error2", overlapCheckRecords);

        // Get Add Codes for all risk types
        RecordSet riskTypeAddCodeRecords = loadAllRiskTypeAddCode();
        Iterator it = riskTypeAddCodeRecords.getRecords();
        HashMap RiskTypeAddCodeMap = new HashMap();
        while (it.hasNext()) {
            Record addCodeAndRiskTypeRecord = (Record) it.next();
            RiskTypeAddCodeMap.put(addCodeAndRiskTypeRecord.getStringValue(RiskFields.RISK_TYPE_CODE),
                addCodeAndRiskTypeRecord.getStringValue(RiskFields.ADD_CODE));
        }

        // Set the displayRecordNumber to all visible records.
        inputRecords = PMRecordSetHelper.setDisplayRecordNumberOnRecords(inputRecords);

        //get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.UPDATED, UpdateIndicator.INSERTED}));
        changedRecords.setFieldsOnAll(policyHeader.toRecord(), false);

        RecordSet offRiskRecordSet = new RecordSet();
        if (!(policyHeader.getScreenModeCode().isOosWIP())) {
            // Load all risks from database for given risk official ID list.
            Record loadOffRiskInputRecord = new Record();
            RiskFields.setTableName(loadOffRiskInputRecord, RiskFields.RISK_TABLE_NAME);
            List list = RiskFields.getRiskManualUpdatableFieldsList();
            RiskFields.setRiskFieldsList(loadOffRiskInputRecord, (String)list.get(0));
            RiskFields.setRiskDbFieldsList(loadOffRiskInputRecord, (String)list.get(1));
            String riskIds = "";
            String connection = "";

            it = changedRecords.getRecords();
            while (it.hasNext()) {
                Record r = (Record) it.next();
                RecordMode recordModeCode = PMCommonFields.getRecordModeCode(r);
                boolean isDateChangeAllowed = YesNoFlag.getInstance(isDateChangeAllowed(r)).booleanValue();
                connection = StringUtils.isBlank(riskIds) ? "" : ",";
                
                if (isDateChangeAllowed) {
                    // If current temp risk is from official record.
                    if (!StringUtils.isBlank(RiskFields.getOfficialRecordId(r)) && recordModeCode.isTemp()) {
                        riskIds = riskIds + connection + RiskFields.getOfficialRecordId(r);
                    }
                    else {
                        // If current risk is an official record.
                        if (recordModeCode.isOfficial()) {
                            riskIds = riskIds + connection + RiskFields.getRiskId(r);
                        }
                    }
                }
            }
            RiskFields.setRiskIds(loadOffRiskInputRecord, riskIds);

            if (!StringUtils.isBlank(riskIds)) {
                offRiskRecordSet = getRiskDAO().loadAllRiskByIds(loadOffRiskInputRecord);
            }
        }

        // Get an instance of the Short Term Effective To Date Rule Validator
        ShortTermEffectiveToDateRecordValidator shortTermEffToDateValidator =
            new ShortTermEffectiveToDateRecordValidator(policyHeader,
                RiskFields.RISK_ID, RiskFields.RISK_TABLE_NAME,
                RiskFields.RISK_EFFECTIVE_TO_DATE, offRiskRecordSet);

        it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();

            String rowNum =  String.valueOf(r.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));
            // Original m_recordNumber of risk record is incorrect for new risk record due to saveAllRisk of
            // MaintainRiskAction uses merge() method and this method always puts new record at the last position in
            // recordSet. We have to reset m_recordNumber here based on displayRecordNumber of risk record and then
            // StandardEffectiveToDateRecordValidator can get correct row number.
            r.setRecordNumber(Integer.parseInt(rowNum) - 1);
            String rowId = RiskFields.getRiskId(r);
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(r);
            String offRecId = RiskFields.getOfficialRecordId(r);
            boolean isDateChangeAllowed = YesNoFlag.getInstance(isDateChangeAllowed(r)).booleanValue();

            // Set default value for expireB.
            RiskFields.setExpireB(r, "N");

            // Validate for OOSE Risk
            validateForOoseRisk(policyHeader, r);

            // If current row is slot risk occupant
            // Validate for Add Slot Occupant
            String riskAddCode = (String) RiskTypeAddCodeMap.get(RiskFields.getRiskTypeCode(r));
            if ("SLOT".equals(riskAddCode) && !"VACANT".equals(RiskFields.getRiskName(r)) &&
                r.getUpdateIndicator().equals(UpdateIndicator.INSERTED)) {
                validateForAddSlotOccupant(policyHeader, r, inputRecords);
            }

            // Validate all effective to date changes
            YesNoFlag isEffToDateEditable = isEffectiveToDateEditable(policyHeader, r);
            if (!StringUtils.isSame(RiskFields.getRiskEffectiveToDate(r), RiskFields.getOrigRiskEffectiveToDate(r))
                && !isEffToDateEditable.booleanValue()) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRisk.effectiveToDate.error",
                    new String[]{rowNum}, RiskFields.RISK_EFFECTIVE_TO_DATE, rowId);
                break; // throw exception directly
            }

            // Short term official risk or temp risk which is from official expiration date and other fields can NOT be
            // changed in non-out of sequence transaction at the same time.
            if (!(policyHeader.getScreenModeCode().isOosWIP()) && isDateChangeAllowed) {
                if ((!StringUtils.isBlank(offRecId) && recordModeCode.isTemp()) || recordModeCode.isOfficial()) {
                    if (!shortTermEffToDateValidator.validate(r)) {
                        RiskFields.setRiskEffectiveToDate(r, RiskFields.getOrigRiskEffectiveToDate(r));
                    }
                }
            }

            // Validate Standard Effective To Date Rule
            // Rollback to orginal value if validation is not passed
            // issue#93435
            // For the row that is created for OOSE
            // The standard effective to date validator is not applicable.
            // issue#106689
            // The standard effective to date validator is also not applicable for new row added during OOSE
            // Therefore it should not be applicable for all changed row during OOSE.
            // issue#187070
            // Validate once user changed exclude_comp_gr1_b or exclude_comp_gr2_b, the risk effective to date must be
            // equal to policy term's expiration date.
            if ((policyHeader.getScreenModeCode().isOosWIP())) {
                String sRiskEffFromDate = RiskFields.getRiskEffectiveFromDate(r);
                String sRiskEffToDate = RiskFields.getRiskEffectiveToDate(r);
                String sTermExp = policyHeader.getTermEffectiveToDate();
                if (!StringUtils.isBlank(sRiskEffToDate)) {
                    if (DateUtils.parseDate(sRiskEffToDate).before(DateUtils.parseDate(sRiskEffFromDate))) {
                        MessageManager.getInstance().addErrorMessage("pm.standardEffectiveToDateRecordValidator.rule1.error", new String[]{rowNum},
                            RiskFields.RISK_EFFECTIVE_FROM_DATE, rowId);
                    }

                    Date transDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                    Date polExp = DateUtils.parseDate(policyHeader.getPolicyExpirationDate());
                    if (DateUtils.parseDate(sRiskEffToDate).before(transDate) || DateUtils.parseDate(sRiskEffToDate).after(polExp)) {
                        MessageManager.getInstance().addErrorMessage("pm.standardEffectiveToDateRecordValidator.rule2.error",
                            new String[]{rowNum,
                                FormatUtils.formatDateForDisplay(transDate),
                                FormatUtils.formatDateForDisplay(polExp)},
                            RiskFields.RISK_EFFECTIVE_FROM_DATE, rowId);
                    }

                    String excludeCompGr1B = RiskFields.getExcludeCompGr1B(r);
                    String excludeCompGr2B = RiskFields.getExcludeCompGr2B(r);
                    String origExcludeCompGr1B = RiskFields.getOrigExcludeCompGr1B(r);
                    String origExcludeCompGr2B = RiskFields.getOrigExcludeCompGr2B(r);
                    if (!StringUtils.isBlank(RiskFields.getOfficialRecordId(r)) &&
                        (!excludeCompGr1B.equals(origExcludeCompGr1B) || !excludeCompGr2B.equals(origExcludeCompGr2B)) &&
                        !DateUtils.parseDate(sRiskEffToDate).equals(DateUtils.parseDate(sTermExp))) {
                        MessageManager.getInstance().addErrorMessage("pm.standardEffectiveToDateRecordValidator.rule3.error", new String[]{rowNum},
                            RiskFields.RISK_EFFECTIVE_FROM_DATE, rowId);
                    }
                }
                else {
                    MessageManager.getInstance().addErrorMessage("pm.standardEffectiveToDateRecordValidator.required.error",
                        new String[]{rowNum}, RiskFields.RISK_EFFECTIVE_TO_DATE, rowId);
                    RiskFields.setRiskEffectiveToDate(r, RiskFields.getOrigRiskEffectiveToDate(r));
                }

            }
            else {
                if (!effToDateValidator.validate(r)) {
                    RiskFields.setRiskEffectiveToDate(r, RiskFields.getOrigRiskEffectiveToDate(r));
                }
            }

            // Validate IBNR Indicator if it changed
            // Change is equal to null previously and now not null
            // OR not null previously and now null
            // OR not null previously or now and with different values
            if (r.hasField(RiskFields.ROLLING_IBNR_B) && r.hasField(RiskFields.ORIG_ROLLING_IBNR_B) &&
                !StringUtils.isSame(RiskFields.getOrigRollingIbnrB(r), RiskFields.getRollingIbnrB(r), true)) {
                String sTransEffectiveFromDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
                Record inputRecord = new Record();
                TransactionFields.setTransactionEffectiveFromDate(inputRecord, sTransEffectiveFromDate);
                RiskFields.setRiskBaseRecordId(inputRecord, RiskFields.getRiskBaseRecordId(r));
                String sValid = getRiskDAO().validateIBNR(inputRecord);

                // Set the validation message if it is not valid
                if (!YesNoFlag.getInstance(sValid).booleanValue()) {
                    RiskFields.setRollingIbnrB(r, RiskFields.getOrigRollingIbnrB(r));
                    MessageManager.getInstance().addErrorMessage("pm.maintainRisk.IBNR.error",
                        new String[]{rowNum}, RiskFields.ROLLING_IBNR_B, rowId);
                }
            }

            // Check Number Field
            // Loop through field list to get number fieldId
            int len = NUMBER_FIELDS.length;
            for (int i = 0; i < len; i++) {
                String fieldName = NUMBER_FIELDS[i];
                if (r.hasStringValue(fieldName)) {
                    // validate number fields
                    if (r.getFloatValue(fieldName).floatValue() < 0) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainRisk.numberField.error",
                            new String[]{rowNum, getWorkbenchConfiguration().getFieldLabel(MAINTAIN_RISK_ACTION_CLASS_NAME, fieldName)},
                            fieldName, rowId);
                    }
                }
            }

            // Modify Square Footage
            if (r.hasStringValue(RiskFields.SQUARE_FOOTAGE)) {
                Long squareFootage = r.getLongValue(RiskFields.SQUARE_FOOTAGE);
                if (squareFootage != null && (squareFootage.longValue() < 0 || squareFootage.longValue() > 999999999)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainRisk.squareFootage.error",
                        new String[]{rowNum}, RiskFields.SQUARE_FOOTAGE, rowId);
                }
            }

            // Validate new risks and RecordModeCode!="REQUEST" (not oose risk)
            if (r.getUpdateIndicator().equals(UpdateIndicator.INSERTED)
                && !PMCommonFields.getRecordModeCode(r).isRequest()) {
                String addCode = (String) RiskTypeAddCodeMap.get(RiskFields.getRiskTypeCode(r));

                // check risk entity type
                if (addCode.equals("ENTITY")) {
                    Record tempRecord = new Record();
                    PolicyHeaderFields.setPolicyTypeCode(tempRecord, policyHeader.getPolicyTypeCode());
                    RiskFields.setRiskTypeCode(tempRecord, RiskFields.getRiskTypeCode(r));
                    RiskFields.setEntityId(tempRecord, RiskFields.getEntityId(r));
                    if (r.hasField(RiskFields.ENTITY_TYPE)) {
                        tempRecord.setFieldValue(RiskFields.ENTITY_TYPE, r.getStringValue(RiskFields.ENTITY_TYPE));
                    }
                    String isValid = getRiskDAO().isRiskEntityTypeValid(tempRecord);
                    if (!YesNoFlag.getInstance(isValid).booleanValue()) {
                        MessageManager.getInstance().addErrorMessage("pm.addRisk.invalidEntityType.error2",
                            new String[]{rowNum, r.getStringValue(RiskFields.RISK_NAME),
                                r.getStringValue(RiskFields.RISK_TYPE_CODE + "LOVLABEL")}, "", rowId);
                    }
                }

                // valudate overlaps
                String[] keyFieldNames = new String[2];
                String[] parmFieldNames = new String[2];
                boolean validateOverlaps = false;
                if (addCode.equals("ENTITY") || addCode.equals("HOSPITAL")) {
                    keyFieldNames[1] = RiskFields.ENTITY_ID;
                    validateOverlaps = true;
                }
                else if (addCode.equals("LOCATION")) {
                    keyFieldNames[1] = RiskFields.LOCATION;
                    validateOverlaps = true;
                }
                if (validateOverlaps) {
                    keyFieldNames[0] = RiskFields.RISK_TYPE_CODE;
                    parmFieldNames[0] = RiskFields.RISK_TYPE_CODE;
                    parmFieldNames[1] = RiskFields.RISK_NAME;
                    continuityRecordValidator.setKeyFieldNames(keyFieldNames);
                    continuityRecordValidator.setParmFieldNames(parmFieldNames);
                    continuityRecordValidator.validate(r);
                }
            }

            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid risk data.");
        }

        l.exiting(getClass().getName(), "validateAllRisk");
    }

    /**
     * Get Initial Values for slot occupant
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForSlotOccupant(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForSlotOccupant", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();

        // Set Default values
        // riskId
        Long lRiskId = getDbUtilityManager().getNextSequenceNo();
        RiskFields.setRiskId(outputRecord, String.valueOf(lRiskId.longValue()));
        // officialRecordId
        RiskFields.setOfficialRecordId(outputRecord, RiskFields.getRiskId(inputRecord));
        // recordModeCode
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.TEMP);
        // afterImageRecordB
        RiskFields.setAfterImageRecordB(outputRecord, YesNoFlag.Y);
        // riskEffectiveFromeDate
        RiskFields.setRiskEffectiveFromDate(
            outputRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        // riskStatus
        RiskFields.setRiskStatus(outputRecord, PMStatusCode.PENDING);
        // Set risk to date from parent record
        // It is needed to default page entitlement initial values
        
        //set the occupant risk's expiration date to the slot risk's expiration date
        String sExpirationDate = policyHeader.getRiskHeader().getRiskEffectiveToDate();
        String policyExpirationDate = policyHeader.getPolicyExpirationDate();
        Date riskExpirationDate = DateUtils.parseDate(sExpirationDate);
        Date policyExpDate = DateUtils.parseDate(policyExpirationDate);
        if (riskExpirationDate.after(policyExpDate)) {
            sExpirationDate = policyExpirationDate;
        }
        RiskFields.setRiskEffectiveToDate(outputRecord, sExpirationDate);

        // Get the default risk entitlement values
        if (inputRecord.hasField(RiskFields.RISK_DETAIL_B)) {
            RiskFields.setRiskDetailId(outputRecord, RiskFields.getRiskId(outputRecord));
            RiskFields.setEntityId(outputRecord, RiskFields.getEntityId(inputRecord));
            ViewRiskSummaryEntitlementRecordLoadProcessor.setInitialEntitlementValuesForRisk(
                this, policyHeader, policyHeader.getScreenModeCode(),
                loadAllRiskTypeAddCode(), outputRecord);
        }
        else {
        RiskEntitlementRecordLoadProcessor.setInitialEntitlementValuesForRisk(
            this, policyHeader, policyHeader.getScreenModeCode(),
                loadAllRiskTypeAddCode(), outputRecord);
        }

        // Setup intial row style
        RiskRowStyleRecordLoadprocessor.setInitialEntitlementValuesForRowStyle(outputRecord);

        // Set ddl status, for SlotOccupant the ddlStatus should be NORECORD.
        CancelProcessFields.setDdlStatus(outputRecord, "NORECORD");

        origFieldLoadProcessor.postProcessRecord(outputRecord, true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForSlotOccupant", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Method that gets the default values for copy new policy
     * based on parameters stored in the input Record.
     *
     * @param policyHeader
     * @param inputRecord Record that contains input parameters
     * @return Record that contains default values
     */
    public Record getInitialValuesForCopyNewPolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForCopyNewPolicy", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();

        // set input values to the output record
        outputRecord.setFields(inputRecord);

        // set request context if it's not passed in
        if (!outputRecord.hasStringValue(CreatePolicyFields.REQUEST_CONTEXT))
            CreatePolicyFields.setRequestContext(outputRecord, CreatePolicyFields.RequestContextValues.REQUEST_CONTEXT_PM);

        // set policy cycle if it's not passed in
        if (!outputRecord.hasStringValue(CreatePolicyFields.POLICY_CYCLE_CODE))
            CreatePolicyFields.setPolicyCycleCode(outputRecord, PolicyCycleCode.POLICY);

        String entityId = policyHeader.getRiskHeader().getRiskEntityId();

        CreatePolicyFields.setPolicyHolderNameEntityId(outputRecord, entityId);

        CreatePolicyFields.setPolicyHolderEntityType(outputRecord, getEntityManager().getEntityType(entityId));

        // set issue company
        CreatePolicyFields.setIssueCompanyEntityId(outputRecord, policyHeader.getIssueCompanyEntityId());

        // set issue state
        CreatePolicyFields.setIssueStateCode(outputRecord, policyHeader.getIssueStateCode());

        // set term effective dates
        CreatePolicyFields.setTermEffectiveFromDate(outputRecord, policyHeader.getTermEffectiveFromDate());
        CreatePolicyFields.setTermEffectiveToDate(outputRecord, policyHeader.getTermEffectiveToDate());

        // set user transaction code
        CreatePolicyFields.setIsUserTransactionCodeAvailable(outputRecord, YesNoFlag.N);

        // set term expiration date indicators
        CreatePolicyFields.setIsTermEffectiveToDateChanged(outputRecord, YesNoFlag.N);

        // set accounting date
        CreatePolicyFields.setAccountingDate(outputRecord, DateUtils.formatDate(new Date()));

        // set regional office
        CreatePolicyFields.setRegionalOffice(outputRecord, policyHeader.getRegionalOffice());

        // set short rate indicator
        CreatePolicyFields.setShortRateB(outputRecord, YesNoFlag.N);


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForCopyNewPolicy", outputRecord);
        }

        return outputRecord;
    }

    /**
     * Method that copy policy to risk based on input Record.
     *
     * @param polNoRec  Record that contains new policy information
     * @param inputRecord  Record that contains new policy information
     * @return String contains policy number
     */
    public String copyNewPolicyFromRisk(Record polNoRec, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyNewPolicyFromRisk", new Object[]{inputRecord});
        }

        boolean isPolicyNoExist = YesNoFlag.getInstance(polNoRec.getStringValue(
            StoredProcedureDAO.RETURN_VALUE_FIELD.substring(0,6))).booleanValue();
        String policyNo = polNoRec.getStringValue(CreatePolicyFields.POL_NO);
        if (isPolicyNoExist) {
            CreatePolicyFields.setNewPolNo(inputRecord, policyNo);
            CreatePolicyFields.setExistingPolB(inputRecord, YesNoFlag.N);
            CreatePolicyFields.setFromCycle(inputRecord, PolicyCycleCode.POLICY);
            CreatePolicyFields.setToCycle(inputRecord, PolicyCycleCode.POLICY);
            CreatePolicyFields.setQuoteTransactionCode(inputRecord, null);
            CreatePolicyFields.setCoverageList(inputRecord, null);
            CreatePolicyFields.setRetroactiveDate(inputRecord, null);
            CreatePolicyFields.setDummyState(inputRecord, null);

            getRiskDAO().copyNewPolicyFromRisk(inputRecord);
        } else {
            AppException ae = new AppException("pm.createPolicy.failed.error");
            l.throwing(getClass().getName(), "copyNewPolicyFromRisk", ae);
            throw ae;
        }

        // Fix 96889: Remove all the information message when create policy.
        if (MessageManager.getInstance().hasInfoMessages()) {
            Iterator it = MessageManager.getInstance().getInfoMessages();
            while (it.hasNext()) {
                // Must call the method next().
                it.next();
                it.remove();
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyNewPolicyFromRisk", policyNo);
        }

        return policyNo;
    }

    /**
     * To get risk expiration date if risk is a date change allowed risk.
     *
     * @param inputRecord
     * @return
     */
    public String getRiskExpDate(Record inputRecord) {
        return getRiskDAO().getRiskExpDate(inputRecord);
    }


    /**
     * get Fte facililty count for opion availability
     *
     * @param inputRecord intput record
     * @return the number of facility count
     */
    public int getFteFacilityCount(Record inputRecord) {
        return getRiskDAO().getFteFacilityCount(inputRecord);
    }

    /**
     * Validate for add slot occupant
     *
     * @param policyHeader
     * @param record
     * @param recordset
     */
    protected void validateForAddSlotOccupant(PolicyHeader policyHeader, Record record, RecordSet recordset) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForAddSlotOccupant", new Object[]{policyHeader, record, recordset});
        }

        Record parentRow;
        // Check if parent row with same official record Id exists
        RecordSet rs = recordset.getSubSet(new RecordFilter("riskId", RiskFields.getOfficialRecordId(record)));
        if (rs.getSize() != 1 || RiskFields.getRiskStatus(rs.getRecord(0)).isCancelled()) {
            MessageManager.getInstance().addErrorMessage("pm.addSlotOccupant.notSlotRiskType.error");
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "validateForAddSlotOccupant", l);
            }
            return;
        }

        String rowNum = String.valueOf(record.getRecordNumber() + 1);
        String rowId = RiskFields.getRiskId(record);

        // Check if the slot is occupied
        parentRow = rs.getRecord(0);
        String sTransEffDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();

        Iterator it = recordset.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            // Either current row is selected parent row or current inserted row, skip it
            if (RiskFields.getRiskId(parentRow).equals(RiskFields.getRiskId(r)) ||
                RiskFields.getRiskId(record).equals(RiskFields.getRiskId(r))) {
                continue;
            }

            // If current row is deleted, skip it.
            if (r.getUpdateIndicator().equals(UpdateIndicator.DELETED)){
                continue;
            }

            // Special requirement: Slot Occupant Check
            if (RiskFields.getSlotId(parentRow).equals(RiskFields.getSlotId(r)) &&
                !"VACANT".equals(RiskFields.getRiskName(r)) &&
                RiskFields.getRiskTypeCode(parentRow).equals(RiskFields.getRiskTypeCode(r)) &&
                !r.getDateValue(RiskFields.RISK_EFFECTIVE_FROM_DATE).after(DateUtils.parseDate(sTransEffDate)) &&
                r.getDateValue(RiskFields.RISK_EFFECTIVE_TO_DATE).after(DateUtils.parseDate(sTransEffDate))) {
                MessageManager.getInstance().addErrorMessage(
                    "pm.addSlotOccupant.slotOccupied.error1", new String[]{rowNum}, "", rowId);
                // No need to continue
                break;
            }

            //Special requirement: Temp Occupant Check
            if (RiskFields.getSlotId(parentRow).equals(RiskFields.getSlotId(r)) &&
                RiskFields.getRiskTypeCode(parentRow).equals(RiskFields.getRiskTypeCode(r)) &&
                r.getLongValue(RiskFields.ENTITY_ID).longValue() > 0 &&
                PMCommonFields.getRecordModeCode(r).isTemp() &&
                !r.getDateValue(RiskFields.RISK_EFFECTIVE_FROM_DATE).after(DateUtils.parseDate(sTransEffDate)) &&
                r.getDateValue(RiskFields.RISK_EFFECTIVE_TO_DATE).after(DateUtils.parseDate(sTransEffDate))) {
                MessageManager.getInstance().addErrorMessage(
                    "pm.addSlotOccupant.slotOccupied.error1", new String[]{rowNum}, "", rowId);
                // No need to continue
                break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForAddSlotOccupant");
        }
    }

    /**
     * load all target risks for copy all risk
     *
     * @param policyHeader
     * @param inputRecord
     * @param loadProcessor
     * @param riskFormFields all the risk form fields
     * @return taget risks recordset
     */
    public RecordSet loadAllTargetRisk(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor, String riskFormFields) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTargetRisk", new Object[]{policyHeader, inputRecord, riskFormFields});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);
        int fromRiskClassCount = getRiskDAO().getFromRiskClassCount(inputRecord);
        RiskCopyFields.setFromRiskClassCount(inputRecord, String.valueOf(fromRiskClassCount));
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(new RiskCopyAllRecordLoadProcessor(policyHeader, inputRecord, riskFormFields), loadProcessor);
        RecordSet rs = getRiskDAO().loadAllTargetRisk(inputRecord, loadProcessor);

        //set default values on all records
        rs.setFieldValueOnAll(RiskCopyFields.STATUS, YesNoFlag.N);
        rs.setFieldValueOnAll(RiskCopyFields.IS_CONFIRMED, YesNoFlag.N);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTargetRisk", rs);
        }

        return rs;
    }

    /**
     * validate risk copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllRisk", new Object[]{inputRecord});
        }

        String valStatus = getRiskDAO().validateCopyAllRisk(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllRisk", valStatus);
        }

        return valStatus;
    }

    /**
     * copy all risks
     *
     * @param policyHeader
     * @param covgRs
     * @param compRs
     * @param covgClassRs
     * @param coiRs
     * @param affiRs
     * @param scheduleRs
     * @param inputRecord
     * @return outputRecord
     */
    public Record copyAllRisk(PolicyHeader policyHeader, RecordSet covgRs , RecordSet compRs, RecordSet covgClassRs , RecordSet coiRs, RecordSet affiRs, RecordSet scheduleRs, Record inputRecord) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyAllRisk", new Object[]{policyHeader, covgRs, covgClassRs, compRs, coiRs, affiRs,scheduleRs, inputRecord});
        }

        //initialize flag fields for output record
        Record outputRecord = new Record();
        RiskCopyFields.setValidateErrorFlag(outputRecord, YesNoFlag.N);
        RiskCopyFields.setCopyFailureFlag(outputRecord, YesNoFlag.N);
        boolean isFirstSelectedRisk = RiskCopyFields.getIsFirstSelectedRisk(inputRecord).booleanValue();
        String toCovgBaseRecordIds = "";
        //If it is the first selected risk, clear error message
        if (isFirstSelectedRisk) {
            getTransactionManager().deleteAllValidationError(policyHeader.toRecord());
        }

        HashMap configFieldsMap = (HashMap) getAllFieldForCopyAll();
        //validate copy all risk
        validateRiskCopyTarget(
            policyHeader, covgRs, compRs, covgClassRs, coiRs, affiRs, scheduleRs, inputRecord, outputRecord, configFieldsMap);
        try {
            //if there is no validation warning need user to confirm
            if (!MessageManager.getInstance().hasConfirmationPrompts()) {
                Record preInputRecord = new Record();
                preInputRecord.setFieldValue("transId", policyHeader.getLastTransactionId());
                preInputRecord.setFieldValue("effFrom", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                preInputRecord.setFieldValue("effTo", RiskFields.getRiskEffectiveToDate(inputRecord));
                preInputRecord.setFieldValue("termEff", policyHeader.getTermEffectiveFromDate());
                preInputRecord.setFieldValue("termExp", policyHeader.isShortTermB() ? "01/01/3000" : policyHeader.getTermEffectiveToDate());
                preInputRecord.setFieldValue(RiskCopyFields.CALL_FROM,RiskCopyFields.CallFromValues.BS );

                //if risk is valid to copy, then copy risk stats
                if (RiskCopyFields.getIsValidToCopy(inputRecord).booleanValue()) {
                    inputRecord.setFields(policyHeader.toRecord(), false);
                    Record riskInputRecord = new Record();
                    riskInputRecord.setFields(preInputRecord);
                    riskInputRecord.setFieldValue("fromId",RiskFields.getRiskId(inputRecord));
                    riskInputRecord.setFieldValue("toId",RiskCopyFields.getToRiskId(inputRecord));
                    String parms = getParmsForCopyAll(inputRecord,(String)configFieldsMap.get(RiskCopyFields.CopyLevelValues.RISK_LEVEL));
                    riskInputRecord.setFieldValue("parms", parms);
                    riskInputRecord.setFieldValue("level", RiskCopyFields.CopyLevelValues.RISK_LEVEL);

                    String returnCode = getRiskDAO().processCopyAll(riskInputRecord).getSummaryRecord().getStringValue("rc");
                    //check return code, 1 means validation error and -1 means system error
                    if (returnCode.equals("1")) {
                        RiskCopyFields.setValidateErrorFlag(outputRecord, YesNoFlag.Y);
                    }
                    else if (returnCode.equals("-1") ) {
                        RiskCopyFields.setCopyFailureFlag(outputRecord, YesNoFlag.Y);
                    }
                }
                //for those valid coverages, set the toCoverageBaseRecordId to child component and sub coverage records,
                //and validate these components
                Iterator covgRsIter = covgRs.getRecords();
                while (covgRsIter.hasNext()) {
                    Record covgRec = (Record) covgRsIter.next();

                    //validate coverage record
                    checkValidateResult(covgRec, inputRecord, getCoverageManager().validateCopyAllCoverage(covgRec), outputRecord);
                    //copy coverage and retrieve the target coverage base record ID
                    if (RiskCopyFields.getIsValidToCopy(covgRec).booleanValue()) {

                        Record covgInputRecord = new Record();
                        covgInputRecord.setFields(preInputRecord);
                        covgInputRecord.setFieldValue("fromId", CoverageFields.getCoverageId(covgRec));
                        covgInputRecord.setFieldValue("toId",  RiskCopyFields.getToRiskBaseRecordId(covgRec));
                        String parms = getParmsForCopyAll(covgRec, (String) configFieldsMap.get(RiskCopyFields.CopyLevelValues.COVERAGE_LEVEL));
                        covgInputRecord.setFieldValue("parms", parms);
                        covgInputRecord.setFieldValue("level", RiskCopyFields.CopyLevelValues.COVERAGE_LEVEL);
                        RecordSet rs = getRiskDAO().processCopyAll(covgInputRecord);
                        String toCovgBaseId = rs.getSummaryRecord().getStringValue("rc");
                        String newCmCoverageB = RiskCopyFields.getNewCmCoverageB(rs.getSummaryRecord());
                        if (YesNoFlag.getInstance(newCmCoverageB).booleanValue()){
                            if (StringUtils.isBlank(toCovgBaseRecordIds)){
                                toCovgBaseRecordIds = toCovgBaseId;
                            }else {
                                toCovgBaseRecordIds = toCovgBaseRecordIds + "," + toCovgBaseId;
                            }
                        }
                        //getCoverageManager().copyAllCoverage(covgRec);
                        //String toCovgBaseId = RiskCopyFields.getToCoverageBaseRecordId(covgRec);
                        //if the target coverage base ID is zero or less, set copy failure flag
                        if (Long.parseLong(toCovgBaseId) <= 0) {
                            RiskCopyFields.setCopyFailureFlag(outputRecord, YesNoFlag.Y);
                        }
                        else {
                            String covgBaseId = CoverageFields.getCoverageBaseRecordId(covgRec);

                            //set target coverage base ID on child component records
                            RecordSet childCompRs = compRs.getSubSet(new RecordFilter(ComponentFields.COVERAGE_BASE_RECORD_ID, covgBaseId));
                            childCompRs.setFieldValueOnAll(RiskCopyFields.TO_COVERAGE_BASE_RECORD_ID, toCovgBaseId, true);
                            //validate the child component data
                            Iterator childCompRsIter = childCompRs.getRecords();
                            while (childCompRsIter.hasNext()) {
                                Record childCompRec = (Record) childCompRsIter.next();
                                checkValidateResult(childCompRec, inputRecord, getComponentManager().validateCopyAllComponent(childCompRec), outputRecord);
                            }

                            //set target coverage base ID on child component records? and set isValidToCopy flag on child coverage class records
                            RecordSet childCovgClassRs = covgClassRs.getSubSet(new RecordFilter(CoverageClassFields.PARENT_COVERAGE_BASE_RECORD_ID, covgBaseId));
                            childCovgClassRs.setFieldValueOnAll(RiskCopyFields.TO_COVERAGE_BASE_RECORD_ID, toCovgBaseId, true);
                            //validate the child coverage class data
                            Iterator childCovgClassRsIter = childCovgClassRs.getRecords();
                            while (childCovgClassRsIter.hasNext()) {
                                Record childCovgClassRec = (Record) childCovgClassRsIter.next();
                                checkValidateResult(childCovgClassRec, inputRecord, getCoverageClassManager().validateCopyAllCoverageClass(childCovgClassRec), outputRecord);
                            }
                        }
                    }
                }
                RiskCopyFields.setToCovgBaseRecordIds(outputRecord, toCovgBaseRecordIds);
                //copy the valid components and coverage classes
                RecordSet valCompRs = compRs.getSubSet(new RecordFilter(RiskCopyFields.IS_VALID_TO_COPY, YesNoFlag.Y));
                if (valCompRs.getSize() > 0) {
                    Iterator compRsIter = valCompRs.getRecords();
                    while(compRsIter.hasNext()) {
                        Record compRecord = (Record) compRsIter.next();
                        Record compInputRecord = new Record();
                        compInputRecord.setFields(preInputRecord);
                        compInputRecord.setFieldValue("fromId", ComponentFields.getPolicyCovComponentId(compRecord));
                        compInputRecord.setFieldValue("toId", RiskCopyFields.getToCoverageBaseRecordId(compRecord));
                        String parms = getParmsForCopyAll(compRecord, (String) configFieldsMap.get(RiskCopyFields.CopyLevelValues.COMPONENT_LEVEL));
                        compInputRecord.setFieldValue("parms", parms);
                        compInputRecord.setFieldValue("level", RiskCopyFields.CopyLevelValues.COMPONENT_LEVEL);
                        getRiskDAO().processCopyAll(compInputRecord);
                    }
                }
                RecordSet valCovgClassRs = covgClassRs.getSubSet(new RecordFilter(RiskCopyFields.IS_VALID_TO_COPY, YesNoFlag.Y));
                if (valCovgClassRs.getSize() > 0) {
                    Iterator covgClassRsIter = valCovgClassRs.getRecords();
                    while (covgClassRsIter.hasNext()) {
                        Record covgClassRecord = (Record) covgClassRsIter.next();
                        Record covgClassInputRecord = new Record();
                        covgClassInputRecord.setFields(preInputRecord);
                        covgClassInputRecord.setFieldValue("fromId", CoverageClassFields.getCoverageClassId(covgClassRecord));
                        covgClassInputRecord.setFieldValue("toId", RiskCopyFields.getToCoverageBaseRecordId(covgClassRecord));
                        String parms = getParmsForCopyAll(covgClassRecord, (String) configFieldsMap.get(RiskCopyFields.CopyLevelValues.COVERAGE_CLASS_LEVEL));
                        covgClassInputRecord.setFieldValue("parms", parms);
                        covgClassInputRecord.setFieldValue("level", RiskCopyFields.CopyLevelValues.COVERAGE_CLASS_LEVEL);
                        getRiskDAO().processCopyAll(covgClassInputRecord);
                    }
                }

                //copy the coi records
                if (coiRs != null && coiRs.getSize() > 0) {
                    getCoiManager().copyAllCoi(coiRs, inputRecord);
                }

                //if affiliations are valid to copy, copy the data
                if (affiRs != null && affiRs.getSize() > 0
                    && RiskCopyFields.getIsAffiliationValidToCopy(outputRecord).booleanValue()) {
                    getAffiliationManager().copyAllAffiliation(affiRs);
                }

                //if schedule are valid to copy, copy the data
                if (scheduleRs != null && scheduleRs.getSize() > 0) {
                    getScheduleManager().copyAllSchedule(scheduleRs);
                }
            }
        }
        catch (Exception e) {
            l.warning(getClass().getName() + " exception in copyAllRisk: " + e.getMessage());
            RiskCopyFields.setCopyFailureFlag(outputRecord, YesNoFlag.Y);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyAllRisk", outputRecord);
        }

        return outputRecord;
    }

    /**
     * validate copy all risk
     * Validate Target risk, components, coverage classes, it will stop validating if any exception is thrown
     *
     * @param policyHeader
     * @param covgRs
     * @param compRs
     * @param covgClassRs
     * @param coiRs
     * @param affiRs
     * @param scheduleRs
     * @param inputRecord
     * @param outputRecord contains flag fields
     */
    protected Record validateRiskCopyTarget(PolicyHeader policyHeader, RecordSet covgRs, RecordSet compRs, RecordSet covgClassRs, RecordSet coiRs, RecordSet affiRs, RecordSet scheduleRs, Record inputRecord, Record outputRecord, HashMap configFieldsMap) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRiskCopyTarget", new Object[]{policyHeader, covgRs, covgClassRs, compRs, coiRs, affiRs,scheduleRs, inputRecord});
        }

        //maintain selected coverage map
        Map seledCovgMap = new HashMap();

        //for the target risk, validate the process
        String toRiskId = RiskCopyFields.getToRiskId(inputRecord);
        String toRiskBaseRecordId = RiskCopyFields.getToRiskBaseRecordId(inputRecord);
        String termEff = policyHeader.getTermEffectiveFromDate();
        String termExp = policyHeader.isShortTermB() ? "01/01/3000" : policyHeader.getTermEffectiveToDate();

        try {

            //validate source risk, and update flag fields for output
            inputRecord.setFieldValue(RiskCopyFields.TERM_EXP, termExp);
            inputRecord.setFields(policyHeader.toRecord(), false);

            // Fix issue 91935:
            // If at least one of the source risk attributes is selected, system should copy risk.
            // Issue 107937, the fields specialty and network should be validated when they are available.
            if(hasSelectedFields(inputRecord,(String)configFieldsMap.get(RiskCopyFields.CopyLevelValues.RISK_LEVEL)) ){
                checkValidateResult(inputRecord, inputRecord, validateCopyAllRisk(inputRecord), outputRecord);
            }
            else{
                RiskCopyFields.setIsValidToCopy(inputRecord, YesNoFlag.N);
            }

            //construct a selected coverage map, which will be used later for component, coverage class validation
            //It will not call DAO to validate coverage here, since it needs to copy coverage
            // once one coverage is validated
            Iterator covgRsIter = covgRs.getRecords();
            while (covgRsIter.hasNext()) {
                Record covgRec = (Record) covgRsIter.next();
                //for every coverage set initial isValidToCopy flag to N
                RiskCopyFields.setIsValidToCopy(covgRec, YesNoFlag.N);
                RiskCopyFields.setTermExp(covgRec, termExp);
                RiskCopyFields.setToRiskBaseRecordId(covgRec, toRiskBaseRecordId);
                RiskCopyFields.setToRiskId(covgRec, toRiskId);
                TransactionFields.setTransactionLogId(covgRec, policyHeader.getLastTransactionId());
                covgRec.setFields(policyHeader.toRecord(), false);
                //add selected record to seledCovgMap
                String covgBaseId = CoverageFields.getCoverageBaseRecordId(covgRec);
                seledCovgMap.put(covgBaseId, covgRec);
            }

            //validate affiliations, and update flag fields for output
            if (affiRs != null && affiRs.getSize() > 0) {
                affiRs.setFieldValueOnAll(RiskCopyFields.TO_RISK_ID, toRiskId);
                affiRs.setFieldValueOnAll(RiskCopyFields.TO_RISK_BASE_RECORD_ID, toRiskBaseRecordId);
                affiRs.setFieldValueOnAll(RiskCopyFields.TERM_EXP, termExp);
                affiRs.setFieldsOnAll(policyHeader.toRecord(), false);
                affiRs.setFieldsOnAll(inputRecord, false);
                String valResult = getAffiliationManager().validateCopyAllAffiliation(affiRs);
                checkValidateResult(null, null, valResult, outputRecord);
                RiskCopyFields.setIsAffiliationValidToCopy(outputRecord, YesNoFlag.N);
                if (valResult.equals("VALID")) {
                    RiskCopyFields.setIsAffiliationValidToCopy(outputRecord, YesNoFlag.Y);
                }
            }

            //validate coi. coi records are always valid to copy, only set some fields here
            if (coiRs != null && coiRs.getSize() > 0) {
                coiRs.setFieldValueOnAll(RiskCopyFields.TO_RISK_BASE_RECORD_ID, toRiskBaseRecordId);
                coiRs.setFieldValueOnAll(RiskCopyFields.TERM_EXP, termExp);
                coiRs.setFieldsOnAll(policyHeader.toRecord(), false);
            }

            //validate schedule. schedule records are always valid to copy, only set some fields here
            if (scheduleRs != null && scheduleRs.getSize() > 0) {
                scheduleRs.setFieldValueOnAll(RiskCopyFields.TO_RISK_BASE_RECORD_ID, toRiskBaseRecordId);
                scheduleRs.setFieldValueOnAll(RiskCopyFields.TERM_EFF, termEff);
                scheduleRs.setFieldValueOnAll(RiskCopyFields.TERM_EXP, termExp);
                scheduleRs.setFieldsOnAll(policyHeader.toRecord(), false);
            }
            //validate components whose coverage is not selected
            //other components need to be validated after parent coverage is copied
            Iterator compRsIter = compRs.getRecords();
            while (compRsIter.hasNext()) {
                Record compRec = (Record) compRsIter.next();

                //set initial values for component record
                RiskCopyFields.setIsValidToCopy(compRec, YesNoFlag.N);
                RiskCopyFields.setTermExp(compRec, termExp);
                RiskCopyFields.setToRiskBaseRecordId(compRec, toRiskBaseRecordId);
                RiskCopyFields.setToRiskId(compRec, toRiskId);
                TransactionFields.setTransactionLogId(compRec, policyHeader.getLastTransactionId());
                compRec.setFields(policyHeader.toRecord(), false);
                compRec.setFields(inputRecord, false);

                //validate selected components whose source coverage is not selected
                String parentCovgId = ComponentFields.getCoverageBaseRecordId(compRec);
                if (!seledCovgMap.containsKey(parentCovgId)) {
                    String toCovgBaseId = getRiskCopyTargetCoverageBaseId(policyHeader, compRec);
                    RiskCopyFields.setToCoverageBaseRecordId(compRec, toCovgBaseId);
                    checkValidateResult(compRec, inputRecord, getComponentManager().validateCopyAllComponent(compRec), outputRecord);
                }
                //set component value to empty if it is new doctor component.
                if (ComponentFields.ComponentCodeValues.NEWDOCTOR.equals(ComponentFields.getCoverageComponentCode(compRec))) {
                    ComponentFields.setComponentValue(compRec, "");
                }
            }

            //validate coverage class whose coverage is not selected
            //other coverage class will be set to valid to copy after coverage is copied
            Iterator covgClassRsIter = covgClassRs.getRecords();
            while (covgClassRsIter.hasNext()) {
                Record covgClassRec = (Record) covgClassRsIter.next();
                RiskCopyFields.setIsValidToCopy(covgClassRec, YesNoFlag.N);
                RiskCopyFields.setTermExp(covgClassRec, termExp);
                RiskCopyFields.setToRiskBaseRecordId(covgClassRec, toRiskBaseRecordId);
                RiskCopyFields.setToRiskId(covgClassRec, toRiskId);
                TransactionFields.setTransactionLogId(covgClassRec, policyHeader.getLastTransactionId());
                covgClassRec.setFields(policyHeader.toRecord(), false);

                //validate selected coverage class whose source coverage is not selected
                String parentCovgId = CoverageClassFields.getParentCoverageBaseRecordId(covgClassRec);
                if (!seledCovgMap.containsKey(parentCovgId)) {
                    String toCovgBaseId = getRiskCopyTargetCoverageBaseId(policyHeader, covgClassRec);
                    RiskCopyFields.setToCoverageBaseRecordId(covgClassRec, toCovgBaseId);
                    checkValidateResult(covgClassRec, inputRecord, getCoverageClassManager().validateCopyAllCoverageClass(covgClassRec), outputRecord);
                }
            }
        }
        catch (Exception e) {
            l.warning(getClass().getName() + " exception in validateRiskCopyTarget: " + e.getMessage());
            RiskCopyFields.setCopyFailureFlag(outputRecord, YesNoFlag.Y);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateRiskCopyTarget", outputRecord);
        }

        return outputRecord;
    }


    /**
     * delete risk all
     *
     * @param policyHeader
     * @param covgRs
     * @param compRs
     * @param covgClassRs
     * @param inputRecord
     * @return outputRecord
     */
    public Record deleteAllCopiedRisk(PolicyHeader policyHeader, RecordSet covgRs, RecordSet compRs, RecordSet covgClassRs, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllCopiedRisk", new Object[]{policyHeader, covgRs, covgClassRs, compRs, inputRecord});
        }
        //initialize output record
        Record outputRecord = new Record();
        RiskCopyFields.setDeleteSourceFailureFlag(outputRecord, YesNoFlag.N);
        RiskCopyFields.setDeleteTargetFailureFlag(outputRecord, YesNoFlag.N);

        //validate risk delete all
        validateDeleteAllCopiedRisk(policyHeader, covgRs, compRs, covgClassRs, inputRecord, outputRecord);
        boolean isFailed = false;
        try {
            //delete all copied coverage and retrive toCoverageBaseRecordId
            if (covgRs.getSize() > 0) {
                getCoverageManager().deleteAllCopiedCoverage(policyHeader, inputRecord, covgRs);
            }
        }
        catch (Exception e) {
            l.warning(getClass().getName() + " exception in deleteAllCopiedRisk when deleting coverages: " + e.getMessage());
            isFailed = true;
        }
        //delete components
        try {
            RecordSet validCompRs = compRs.getSubSet(new RecordFilter(RiskCopyFields.IS_VALID_TO_DELETE, YesNoFlag.Y));
            if (validCompRs.getSize() > 0) {
                getComponentManager().deleteAllCopiedComponent(policyHeader, inputRecord, validCompRs);
            }
        }
        catch (Exception e) {
            l.warning(getClass().getName() + " exception in deleteAllCopiedRisk when deleting components: " + e.getMessage());
            isFailed = true;
        }
        //delete coverage classes
        try {
            RecordSet validCovgClassRs = covgClassRs.getSubSet(new RecordFilter(RiskCopyFields.IS_VALID_TO_DELETE, YesNoFlag.Y));
            if (validCovgClassRs.getSize() > 0) {
                getCoverageClassManager().deleteAllCopiedCoverageClass(policyHeader, inputRecord, validCovgClassRs);
            }
        }
        catch (Exception e) {
            l.warning(getClass().getName() + " exception in deleteAllCopiedRisk when deleting coverage classes: " + e.getMessage());
            isFailed = true;
        }

        if (isFailed) {
            //set delete failure flag
            setDeleteFailureFlag(policyHeader, inputRecord, outputRecord);
        }


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllCopiedRisk");
        }

        return outputRecord;
    }

    /**
     * To load all addresses and phone numbers for the selected risk
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAddressPhone(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAddressPhone", new Object[]{inputRecord});
        }

        RecordLoadProcessor loadProcessor = new AddressPhoneEntitlementRecordLoadProcessor();
        RecordLoadProcessor selIndLoadProcessor = AddSelectIndLoadProcessor.getInstance();
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(selIndLoadProcessor, loadProcessor);
        RecordSet rs = getRiskDAO().loadAllAddressPhone(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAddressPhone", rs);
        }

        return rs;
    }

    /**
     * To load all copy to risks and the addresses and phone numbers will be copied into it.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllRiskForCopyAddressPhone(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskForCopyAddressPhone", new Object[]{inputRecord});
        }

        RecordLoadProcessor loadProcessor = new RiskNameRecordLoadProcessor(RiskFields.getEntityId(inputRecord));
        RecordLoadProcessor selIndLoadProcessor = AddSelectIndLoadProcessor.getInstance();
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(selIndLoadProcessor, loadProcessor);
        inputRecord.setFieldValue("policyTermHistoryId", "0");
        RecordSet rs = getRiskDAO().loadAllRisk(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskForCopyAddressPhone", rs);
        }

        return rs;
    }

    /**
     * To copy all source risk's addresses and phone numbers to copy-to risks
     *
     * @param inputRecord
     * @return
     */
    public Record copyAllAddressPhone(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyAllAddressPhone", new Object[]{inputRecord});
        }

        // validate first
        validateCopyAllAddressPhone(inputRecord);

        Record outputRecord;
        try {
            outputRecord = getRiskDAO().copyAllAddressPhone(inputRecord);
        }
        catch (Exception e) {
            throw new AppException("pm.copyAddrPhone.copy.system.failure",
                "System failure when copy addresses and phone numbers.");
        }

        // check prcocess failure
        String rtnCode = outputRecord.getStringValue("rtnCode");
        if (rtnCode.equals("-1")) {
            String rtnMsg = outputRecord.getStringValue("rtnMsg");
            throw new AppException("pm.copyAddrPhone.copy.process.failure",
                "Process failure when executing copy procedure.", new Object[]{rtnMsg});
        }

        // check warning message
        String warningMsg = outputRecord.getStringValue("warningMsg");
        if (!StringUtils.isBlank(warningMsg)) {
            MessageManager.getInstance().addErrorMessage("pm.copyAddrPhone.copy.warning.message", new Object[]{warningMsg});
        }

        // if no error add success message
        MessageManager.getInstance().addInfoMessage("pm.copyAddrPhone.copy.success.message");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyAllAddressPhone", outputRecord);
        }
        return outputRecord;
    }

    /**
     * validate risk delete all
     * It will not stop validating, when any exception occurs
     *
     * @param policyHeader
     * @param covgRs
     * @param compRs
     * @param covgClassRs
     * @param outputRecord
     * @param inputRecord
     */
    protected void validateDeleteAllCopiedRisk(
        PolicyHeader policyHeader, RecordSet covgRs, RecordSet compRs, RecordSet covgClassRs, Record inputRecord, Record outputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateDeleteAllCopiedRisk", new Object[]{
                policyHeader, covgRs, covgClassRs, compRs, inputRecord, outputRecord});
        }
        String toRiskId = RiskCopyFields.getToRiskId(inputRecord);
        String toRiskBaseRecordId = RiskCopyFields.getToRiskBaseRecordId(inputRecord);

        //set term exp field
        String termExp = policyHeader.isShortTermB() ? "01/01/3000" : policyHeader.getTermEffectiveToDate();

        //maintain selected coverage map
        Map seledCovgMap = new HashMap();

        //construct source coverage map
        Iterator covgRsIter = covgRs.getRecords();
        while (covgRsIter.hasNext()) {
            Record covgRec = (Record) covgRsIter.next();

            //for every coverage set initial values
            RiskCopyFields.setIsValidToDelete(covgRec, YesNoFlag.Y);
            RiskCopyFields.setTermExp(covgRec, termExp);
            RiskCopyFields.setToRiskBaseRecordId(covgRec, toRiskBaseRecordId);
            RiskCopyFields.setToRiskId(covgRec, toRiskId);
            TransactionFields.setTransactionLogId(covgRec, policyHeader.getLastTransactionId());

            //add selected record to seledCovgMap
            String covgBaseId = CoverageFields.getCoverageBaseRecordId(covgRec);
            seledCovgMap.put(covgBaseId, covgRec);
        }

        //validate components whose coverage is not selected
        //other components need to be validated after parent coverage is copied
        Iterator compRsIter = compRs.getRecords();
        while (compRsIter.hasNext()) {
            Record compRec = (Record) compRsIter.next();

            //for every component, set initial values
            RiskCopyFields.setIsValidToDelete(compRec, YesNoFlag.N);
            RiskCopyFields.setTermExp(compRec, termExp);
            RiskCopyFields.setToRiskBaseRecordId(compRec, toRiskBaseRecordId);
            RiskCopyFields.setToRiskId(compRec, toRiskId);
            TransactionFields.setTransactionLogId(compRec, policyHeader.getLastTransactionId());

            //for every component try to get target coverage base ID
            //ValidateException will be thrown, if no target coverage is found
            //It will not stop validating if any exception occurs
            String parentCovgId = ComponentFields.getCoverageBaseRecordId(compRec);
            if (!seledCovgMap.containsKey(parentCovgId)) {
                try {
                    String toCovgBaseId = getRiskCopyTargetCoverageBaseId(policyHeader, compRec);
                    RiskCopyFields.setToCoverageBaseRecordId(compRec, toCovgBaseId);
                    RiskCopyFields.setIsValidToDelete(compRec, YesNoFlag.Y);
                }
                catch (ValidationException ve) {
                    //the error message was already added to the MessageManager
                    //  and we need to skip processing this item and continue with the next item.
                    l.warning(getClass().getName() + " exception in validateDeleteAllCopiedRisk: " + ve.getMessage());
                }
                catch (Exception e) {
                    l.warning(getClass().getName() + " exception in validateDeleteAllCopiedRisk: " + e.getMessage());
                    //set delete failure flag
                    setDeleteFailureFlag(policyHeader, inputRecord, outputRecord);
                }
            }
        }

        //validate coverage class
        Iterator covgClassRsIter = covgClassRs.getRecords();
        while (covgClassRsIter.hasNext()) {
            Record covgClassRec = (Record) covgClassRsIter.next();

            //for every coverage class record, set initialize parameters
            RiskCopyFields.setIsValidToDelete(covgClassRec, YesNoFlag.N);
            RiskCopyFields.setTermExp(covgClassRec, termExp);
            RiskCopyFields.setToRiskBaseRecordId(covgClassRec, toRiskBaseRecordId);
            RiskCopyFields.setToRiskId(covgClassRec, toRiskId);
            TransactionFields.setTransactionLogId(covgClassRec, policyHeader.getLastTransactionId());

            //check selected coverage
            String parentCovgId = CoverageClassFields.getParentCoverageBaseRecordId(covgClassRec);
            if (!seledCovgMap.containsKey(parentCovgId)) {
                try {
                    String toCovgBaseId = getRiskCopyTargetCoverageBaseId(policyHeader, covgClassRec);
                    RiskCopyFields.setToCoverageBaseRecordId(covgClassRec, toCovgBaseId);
                    RiskCopyFields.setIsValidToDelete(covgClassRec, YesNoFlag.Y);
                }
                catch (ValidationException ve) {
                    //the error message was already added to the MessageManager
                    //  and we need to skip processing this item and continue with the next item.
                    l.warning(getClass().getName() + " exception in validateDeleteAllCopiedRisk: " + ve.getMessage());
                }
                catch (Exception e) {
                    l.warning(getClass().getName() + " exception in validateDeleteAllCopiedRisk: " + e.getMessage());
                    //set delete failure flag
                    setDeleteFailureFlag(policyHeader, inputRecord, outputRecord);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateDeleteAllCopiedRisk");
        }
    }

    /**
     * To validate for copy addresses and phone numbers
     *
     * @param inputRecord
     */
    protected void validateCopyAllAddressPhone(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllAddressPhone", new Object[]{inputRecord});
        }

        // Address/Phone Not Selected
        String addressPhoneIds = RiskFields.getAddressPhoneIds(inputRecord);
        if (StringUtils.isBlank(addressPhoneIds)) {
            MessageManager.getInstance().addErrorMessage("pm.copyAddrPhone.addrPhone.noselection.error");
        }

        // Copy-to Risk Not Selected
        if (StringUtils.isBlank(RiskFields.getRiskEntityIds(inputRecord))) {
            MessageManager.getInstance().addErrorMessage("pm.copyAddrPhone.copyToRisk.noselection.error");
        }

        // Only when address/phone and copy-to risk are selected, continue to do other validations
        if (!MessageManager.getInstance().hasErrorMessages()) {
            // Missing Change Date
            String changeEffDateStr = RiskFields.getChangeEffectiveDate(inputRecord);
            if (StringUtils.isBlank(changeEffDateStr)) {
                MessageManager.getInstance().addErrorMessage("pm.copyAddrPhone.changeDate.missing.error");
            }
            else {
                // Change Date after Today
                Date changeEffDate = DateUtils.parseDate(changeEffDateStr);
                if (changeEffDate.after(new Date())) {
                    MessageManager.getInstance().addErrorMessage("pm.copyAddrPhone.changeDate.afterToday.error");
                }

                // Change Date before Address Effective
                String[] addrPhoneArray = addressPhoneIds.split(",");
                // cut the effDateStr from the addressPhoneIds and set back to inputRecord
                String nAddrPhoneIds = "";
                for (int i = 0; i < addrPhoneArray.length - 1; i++) {
                    if (i != 0) {
                        nAddrPhoneIds += "," + addrPhoneArray[i];
                    }
                    else {
                    nAddrPhoneIds += addrPhoneArray[i];
                }
                }
                addressPhoneIds = nAddrPhoneIds;
                RiskFields.setAddressPhoneIds(inputRecord, addressPhoneIds);

                String effDateStr = addrPhoneArray[addrPhoneArray.length - 1];
                String effDate;
                if (effDateStr.indexOf("|") > 0) {
                    StringTokenizer st = new StringTokenizer(effDateStr, "|");
                    while (st.hasMoreTokens()) {
                        effDate = st.nextToken();
                        if (DateUtils.parseDate(effDate).after(changeEffDate)) {
                            MessageManager.getInstance().addErrorMessage("pm.copyAddrPhone.changeDate.beforeEff.error");
                            break;
                        }
                    }
                }
                else {
                    effDate = effDateStr;
                    if (DateUtils.parseDate(effDate).after(changeEffDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.copyAddrPhone.changeDate.beforeEff.error");
                    }
                }
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid copy all addresses and phone numbers data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllAddressPhone");
        }
    }

    /**
     * set delete target/source failure flag base on the target risk id
     *
     * @param inputRecord
     * @param outputRecord
     */
    private void setDeleteFailureFlag(PolicyHeader policyHeader, Record inputRecord, Record outputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setDeleteFailureFlag", new Object[]{policyHeader, inputRecord, outputRecord});
        }

        if (RiskCopyFields.getToRiskId(inputRecord).equals(policyHeader.getRiskHeader().getRiskId())) {
            RiskCopyFields.setDeleteSourceFailureFlag(outputRecord, YesNoFlag.Y);
        }
        else {
            RiskCopyFields.setDeleteTargetFailureFlag(outputRecord, YesNoFlag.Y);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setDeleteFailureFlag", outputRecord);
        }
    }

    /**
     * This method will check the validate result and update the isValidToCopy and copyFailureFlag
     * It will not thow any exception
     *
     * @param valRecord
     * @param toRiskRec
     * @param validateResult
     * @param outputRecord
     */
    private void checkValidateResult(Record valRecord, Record toRiskRec, String validateResult, Record outputRecord) {
        if (valRecord != null) {
            RiskCopyFields.setIsValidToCopy(valRecord, YesNoFlag.N);
        }

        if (validateResult.equals("INVALID")) {
            RiskCopyFields.setValidateErrorFlag(outputRecord, YesNoFlag.Y);
        }
        else if (toRiskRec != null && validateResult.equals("VALID_FLAG") &&
            !ConfirmationFields.isConfirmed("pm.maintainRiskCopy.validation.confirm", toRiskRec)) {
            String rowId = RiskFields.getRiskId(toRiskRec);
            MessageManager.getInstance().addConfirmationPrompt(
                "pm.maintainRiskCopy.validation.confirm", new String[]{}, false, RiskCopyFields.STATUS, rowId);
        }
        else if (validateResult.equals("FAILED") || validateResult.equals("MTFAILED")) {
            RiskCopyFields.setCopyFailureFlag(outputRecord, YesNoFlag.Y);
        }
        else if (valRecord != null) {
            RiskCopyFields.setIsValidToCopy(valRecord, YesNoFlag.Y);
        }
    }

    /**
     * validate source risk for risk copy all
     *
     * @param policyHeader
     * @param inputRecord
     * @return validate result
     */
    public String validateRiskCopySource(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRiskCopySource", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);

        //clean up validation error messages
        getTransactionManager().deleteAllValidationError(inputRecord);
        //validate source risk
        String validateResult = getRiskDAO().validateRiskCopySource(inputRecord);

        if (validateResult.equals("VALID_FLAG")) {
            MessageManager.getInstance().addConfirmationPrompt("pm.maintainRiskCopy.validation.confirm");
            throw new ValidationException("validate source risk error");
        }

        if (validateResult.equals("FAILED") || validateResult.equals("MTFAILED")) {
            MessageManager.getInstance().addErrorMessage(AppException.UNEXPECTED_ERROR);
            throw new ValidationException("validate source risk fail");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateRiskCopySource", validateResult);
        }
        return validateResult;
    }


    /**
     * to get target coverage base ID if source coverage was not selected for processing
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public String getRiskCopyTargetCoverageBaseId(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskCopyTargetCoverageBaseId", new Object[]{policyHeader, inputRecord});
        }

        String covgBaseId = null;
        inputRecord.setFields(policyHeader.toRecord(), false);

        //get target risk coverage info
        Record toRiskCovgRec = getRiskDAO().getToRiskCoverage(inputRecord);
        String status = toRiskCovgRec.getStringValue("status");

        if (!status.equals("VALID")) {
            //if the Pm_get_to_risk_covg returns status of INVALID/FAILED, treat this as a system error, and throw exception
            throw new AppException("get to risk coverage failed");
        }
        else {
            String prodCovgCode = toRiskCovgRec.getStringValue("toProdCovgCode");
            inputRecord.setFieldValue("productCoverageCode", prodCovgCode);
            covgBaseId = getCoverageManager().getCoverageBaseId(policyHeader, inputRecord);
            //get target coverage count
            RiskCopyFields.setToCoverageBaseRecordId(inputRecord, covgBaseId);
            int targetCovgCount = getRiskDAO().getToCoverageCount(inputRecord);

            //if target coverage not found, throw error
            if (targetCovgCount == 0) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRiskCopy.sourceCoverageNotSelect.error");
                throw new ValidationException("target coverage not found");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskCopyTargetCoverageBaseId", covgBaseId);
        }

        return covgBaseId;
    }

    /**
     * Get risk generic type
     *
     * @param inputRecord
     * @return String
     */
    public String getGenericRiskType(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getGenericRiskType", new Object[]{inputRecord});
        }

        String type = getRiskDAO().getGenericRiskType(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getGenericRiskType", type);
        }
        return type;
    }

    /**
     * Get all new risks
     *
     * @param policyHeader
     * @param inputRecord
     * @param existingRecords
     * @return RecordSet
     */
    protected RecordSet getAllNewRisk(PolicyHeader policyHeader, Record inputRecord, RecordSet existingRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllNewRisk", new Object[]{policyHeader, inputRecord, existingRecords});
        }

        RecordSet newRisks = new RecordSet();
        String entityIds = inputRecord.getStringValue("newEntityIdList");
        String riskTypeCode = inputRecord.getStringValue("newRiskTypeCode");
        String[] entityIdList = entityIds.split(",");
        StringBuffer errMessage = new StringBuffer();
        int curCount = existingRecords.getSize();
        // Fix issue 96028, override riskTypeCode in inputRecord
        RiskFields.setRiskTypeCode(inputRecord, riskTypeCode);
        for (int i = 0; i < entityIdList.length; i++) {
            inputRecord.setFieldValue("entityId", entityIdList[i]);
            String entityName = getEntityManager().getEntityName(entityIdList[i]);

            boolean duplicated = false;
            // validate duplications
            Iterator it = existingRecords.getRecords();
            while (it.hasNext()) {
                Record rec = (Record) it.next();
                String sRiskEffFromDate = RiskFields.getRiskEffectiveFromDate(rec);
                String sRiskEffToDate = RiskFields.getRiskEffectiveToDate(rec);
                String sTransEffFromDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();

                if (entityIdList[i].equals(rec.getStringValue("entityId")) &&
                    riskTypeCode.equals(rec.getStringValue("riskTypeCode")) &&
                    !StringUtils.isBlank(sRiskEffFromDate) && !StringUtils.isBlank(sRiskEffToDate)) {
                    Date riskEffFromDate = DateUtils.parseDate(sRiskEffFromDate);
                    Date riskEffToDate = DateUtils.parseDate(sRiskEffToDate);
                    Date transEffFromDate = DateUtils.parseDate(sTransEffFromDate);
                    if ((transEffFromDate.equals(riskEffFromDate) || transEffFromDate.after(riskEffFromDate)) &&
                        transEffFromDate.before(riskEffToDate) &&
                        !RiskFields.getRiskStatus(rec).isCancelled()) {
                        // Add error messages
                        errMessage.append(MessageManager.getInstance().formatMessage(
                            "pm.addRisk.riskExists.error3", new Object[]{entityName})).append("<br>");
                        duplicated = true;
                        break;
                    }
                }
            }

            if (duplicated) {
                continue;
            }

            // Do validation
            try {
                validateForAddRisk(policyHeader, inputRecord);
            }
            catch (ValidationException e) {
                // continue add next row
                continue;
            }

            // If validation passes, go ahead and get intial values
            Record initValues = getInitialValuesForAddRisk(policyHeader, inputRecord);
            // If the current new risk is the first risk, set PrimaryRiskB to "Y". 
            if (++curCount == 1) {
                RiskFields.setPrimaryRiskB(initValues, YesNoFlag.Y);
            }
            newRisks.addRecord(initValues);
        }

        if (!StringUtils.isBlank(errMessage.toString())) {
            MessageManager.getInstance().addErrorMessage("pm.addRisk.riskExists.error4", new Object[]{errMessage.toString()});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllNewRisk", newRisks);
        }
        return newRisks;
    }

    /**
     * Get all merged risks
     * @param policyHeader
     * @param inputRecord
     * @param existingRecords
     * @return RecordSet
     */
    public RecordSet getAllMergedRisk(PolicyHeader policyHeader, Record inputRecord, RecordSet existingRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllMergedRisk", new Object[]{policyHeader, inputRecord, existingRecords});
        }

        RecordSet newRisks = getAllNewRisk(policyHeader, inputRecord, existingRecords);
        RecordSet newRecords = new RecordSet();
        for (int i = 0; i < newRisks.getSize(); i++) {
            Record rec = new Record();
            Iterator it = existingRecords.getFieldNames();
            while (it.hasNext()) {
                String fieldName = (String) it.next();
                if (newRisks.getRecord(i).hasStringValue(fieldName)) {
                    rec.setFieldValue(fieldName, newRisks.getRecord(i).getStringValue(fieldName));
                }
                else {
                    rec.setFieldValue(fieldName, "");
                }
            }
            // Set indicator fields
            rec.setDisplayIndicator("Y");
            rec.setEditIndicator("Y");
            rec.setUpdateIndicator("I");
            newRecords.addRecord(rec);
        }

        // Merge into existing Records
        existingRecords.syncMissingFieldTypes();
        newRecords.convertDataTypes(existingRecords.getFieldTypesMap());
        existingRecords.addRecords(newRecords);
        // If no new record, selectedRiskId is null, the primary risk will be selected.
        existingRecords.getSummaryRecord().setFieldValue("selectedRiskId",
            newRecords.getSize() > 0 ? newRecords.getRecord(0).getStringValue(RequestIds.RISK_ID) : null);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllMergedRisk", existingRecords);
        }
        return existingRecords;
    }

    /**
     * Get entity owner id for location.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getEntityOwnerId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityOwnerId", inputRecord);
        }

        long entityOwnerId = getRiskDAO().getEntityOwnerId(inputRecord);
        Record outputRecord = new Record();
        outputRecord.setFieldValue("entityOwnerId", new Long(entityOwnerId));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityOwnerId", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Validate If Any Temp Coverage exists under the Risk.
     *
     * @param inputRecord
     * @return Record
     */
    public Record validateTempCovgExist(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateTempCovgExist", inputRecord);
        }
        Record outputRecord = getRiskDAO().validateTempCovgExist(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateTempCovgExist", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Delete temp coverages automatically after issue state was changed.
     *
     * @param inputRecord
     */
    public void performAutoDeleteTempCovgs(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performAutoDeleteTempCovgs", inputRecord);
        }
        getRiskDAO().performAutoDeleteTempCovgs(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performAutoDeleteTempCovgs");
        }
    }

    /**
     * Load all procedure code for risk
     *
     * @param inputRecord
     * @param selIndLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllProcedureCode(Record inputRecord, LoadProcessor selIndLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcedureCode", new Object[]{inputRecord, selIndLoadProcessor});
        }

        RecordLoadProcessor loadProcessor = new ProcedureCodeEntitlementRecordLoadProcessor(inputRecord, inputRecord.getStringValue("procedureCodes"));
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor((RecordLoadProcessor) selIndLoadProcessor, loadProcessor);
        RecordSet rs = getRiskDAO().loadAllProcedureCode(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcedureCode", rs);
        }
        return rs;
    }

    /**
     * Returns the primary coverage id of current risk.
     *
     * @param inputRecord
     * @return String
     */
    public String getPrimaryCoverageId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPrimaryCoverageId", inputRecord);
        }

        String coverageId = getRiskDAO().getPrimaryCoverageId(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPrimaryCoverageId", coverageId);
        }
        return coverageId;
    }

    /**
     * Returns a RecordSet loaded with list of risk surcharge point.
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRiskSurchargePoint(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskSurchargePoint", new Object[]{inputRecord});
        }
        Record record = new Record();
        record.setFields(inputRecord);
        record.setFieldValue("termBaseRecordId", policyHeader.getTermBaseRecordId());
        record.setFieldValue("termEffectiveFromDate", policyHeader.getTermEffectiveFromDate());
        record.setFieldValue("transactionLogId", policyHeader.getLastTransactionInfo().getTransactionLogId());
        // If in Official mode, reset transactionLogId to 0.
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
            record.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, "0");
        }
        // Get the primary coverage id of current risk.
        String primaryCoverageId = getPrimaryCoverageId(record);
        // Get the coverage base record id of the primary coverage.
        policyHeader = getCoverageManager().loadCoverageHeader(policyHeader, primaryCoverageId);
        String primaryCoverageBaseRecordId = policyHeader.getCoverageHeader().getCoverageBaseRecordId();
        record.setFieldValue("coverageBaseRecordId", primaryCoverageBaseRecordId);
        RecordLoadProcessor entitlementRLP = new RiskSurchargePointsEntitlementRecordLoadProcessor(policyHeader);
        // Load all surcharge points for risk level.
        RecordSet rs = getRiskDAO().loadAllRiskSurchargePoint(record, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskSurchargePoint", rs);
        }
        return rs;
    }

    /**
     * Save all risk surcharge point.
     *
     * @param inputRecords
     * @return the number of rows updated.
     */
    public int saveAllRiskSurchargePoint(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRiskSurchargePoint", new Object[]{inputRecords});
        }
        // Get the updated records.
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(new String[]{UpdateIndicator.UPDATED}));
        int updateCount = getRiskDAO().saveAllRiskSurchargePoint(changedRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllRiskSurchargePoint", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Get all copy all configured fields.
     *
     * @return the Map contains config fields.
     */
    public Map getAllFieldForCopyAll() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllFieldForCopyAll");
        }
        // Get the updated records.
        Map resultMap = new HashMap();
        RecordSet rs = getRiskDAO().getAllFieldForCopyAll();
        Iterator it = rs.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            if (r.hasFieldValue(RiskCopyFields.COPY_LEVEL)) {
                if (RiskCopyFields.getCopyLevel(r).equals(RiskCopyFields.CopyLevelValues.RISK_LEVEL)) {
                    resultMap.put(RiskCopyFields.CopyLevelValues.RISK_LEVEL, RiskCopyFields.getBsFieldNameList(r));
                }
                else if (RiskCopyFields.getCopyLevel(r).equals(RiskCopyFields.CopyLevelValues.COVERAGE_LEVEL)) {
                    resultMap.put(RiskCopyFields.CopyLevelValues.COVERAGE_LEVEL, RiskCopyFields.getBsFieldNameList(r));
                }
                else if (RiskCopyFields.getCopyLevel(r).equals(RiskCopyFields.CopyLevelValues.COVERAGE_CLASS_LEVEL)) {
                    resultMap.put(RiskCopyFields.CopyLevelValues.COVERAGE_CLASS_LEVEL, RiskCopyFields.getBsFieldNameList(r));
                }
                else if (RiskCopyFields.getCopyLevel(r).equals(RiskCopyFields.CopyLevelValues.COMPONENT_LEVEL)) {
                    resultMap.put(RiskCopyFields.CopyLevelValues.COMPONENT_LEVEL, RiskCopyFields.getBsFieldNameList(r));
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllFieldForCopyAll", resultMap);
        }
        return resultMap;
    }

    /**
     * To validate reinstate ibnr risk
     *
     * @param inputRecord
     * @return
     */
    public Record valReinstateIbnrRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "valReinstateIbnrRisk", inputRecord);
        }

        String isReinstateIbnrRiskValid = getRiskDAO().valReinstateIbnrRisk(inputRecord);
        Record outputRecord = new Record();
        outputRecord.setFieldValue("isReinstateIbnrRiskValid", isReinstateIbnrRiskValid);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "valReinstateIbnrRisk", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Get parms for copy all.
     *
     * @param inputRecord input record
     * @param fieldsString
     * @return String.
     */
    private String getParmsForCopyAll (Record inputRecord, String fieldsString) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getParmsForCopyAll", new Object[]{inputRecord,fieldsString});
        }
        StringBuffer paramBuf = new StringBuffer();
        String[] FieldsList = null;
        if (!StringUtils.isBlank(fieldsString)) {
            FieldsList = fieldsString.split(",");
        }

        if (FieldsList != null) {
            for (int i = 0; i < FieldsList.length; i++) {
                if ((inputRecord.hasField(FieldsList[i]) && FieldsList[i].equals("exposureUnit"))) {
                    paramBuf.append(FieldsList[i] + "^" + inputRecord.getStringValue(FieldsList[i]) + "^");
                }
                else if ((inputRecord.hasField(FieldsList[i]) && FieldsList[i].equals("componentValue"))) {
                    paramBuf.append(FieldsList[i] + "^" + inputRecord.getStringValue(FieldsList[i]) + "^");
                }
                else if ((inputRecord.hasField(FieldsList[i]) && YesNoFlag.getInstance(inputRecord.getStringValue(FieldsList[i])).booleanValue())) {
                    paramBuf.append(FieldsList[i] + "^Y^");
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getParmsForCopyAll", paramBuf.toString());
        }
        return paramBuf.toString();
    }

    /**
     * Check if at least one of the source risk attributes is selected.
     *
     * @param inputRecord input record
     * @param fieldsString
     * @return boolean true or false.
     */
    private boolean hasSelectedFields(Record inputRecord, String fieldsString) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasSelectedFields", new Object[]{inputRecord, fieldsString});
        }
        boolean hasSelectedRiskFields = false;
        String[] FieldsList = null;
        if (!StringUtils.isBlank(fieldsString)) {
            FieldsList = fieldsString.split(",");
        }

        if (FieldsList != null) {
            for (int i = 0; i < FieldsList.length; i++) {
                if (inputRecord.hasField(FieldsList[i]) && YesNoFlag.getInstance(inputRecord.getStringValue(FieldsList[i])).booleanValue()) {
                    hasSelectedRiskFields = true;
                    break;
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasSelectedFields", hasSelectedRiskFields);
        }
        return hasSelectedRiskFields;
    }

    public RecordSet loadAllRiskTypeAddCode() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskTypeAddCode");
        }

        PolicyCacheManager policyCacheManager = PolicyCacheManager.getInstance();

        if (!policyCacheManager.hasRiskTypeAddCode()) {
            policyCacheManager.setRiskTypeAddCode(getRiskDAO().loadAllRiskTypeAddCode());
        }

        RecordSet recordSet = policyCacheManager.getRiskTypeAddCode();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskTypeAddCode", recordSet);
        }

        return recordSet;
    }

    /**
     * Returns a string which is risk detail id.
     *
     * @param inputRecord Record contains input values
     * @return string which is risk detail id.
     */
    public String getRiskDetailId(Record inputRecord) {
        return getRiskDAO().getRiskDetailId(inputRecord);
    }

    /**
     * Return a new risk id
     *
     * @return
     */
    public String getRiskSequenceId() {
        Long lRiskId = getDbUtilityManager().getNextSequenceNo();
        return String.valueOf(lRiskId);
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getRiskDAO() == null)
            throw new ConfigurationException("The required property 'riskDAO' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        if (getCoverageManager() == null)
            throw new ConfigurationException("The required property 'coverageManager' is missing.");
        if (getPmDefaultManager() == null)
            throw new ConfigurationException("The required property 'pmDefaultManager' is missing.");
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        if (getCoiManager() == null)
            throw new ConfigurationException("The required property 'coiManager' is missing.");
        if (getAffiliationManager() == null)
            throw new ConfigurationException("The required property 'affiliationManager' is missing.");
        if (getCoverageClassManager() == null)
            throw new ConfigurationException("The required property 'coverageClassManager' is missing.");

    }

    /**
     * Returns a RecordSet loaded with list of insured information.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllInsuredInfo(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllInsuredInfo", new Object[]{inputRecord});
        }
        RecordSet rs = getRiskDAO().loadAllInsuredInfo(inputRecord);
        if(rs.getSize() <= 0){
            MessageManager.getInstance().addErrorMessage("pm.insuredInfo.noDataFound");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllInsuredInfo", rs);
        }
        return rs;
     }

    /**
     * Load isFundState field value.
     *
     * @param inputRecord
     * @return Record
     */
    public Record loadIsFundStateValue(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadIsFundStateValue", inputRecord);
        }
        Record outputRecord = getRiskDAO().loadIsFundStateValue(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadIsFundStateValue", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Get default pcf risk county field value.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getDefaultValueForPcfCounty(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultValueForPcfCounty", inputRecord);
        }
        Record outputRecord = new Record();
        String result = getRiskDAO().getDefaultValueForPcfCounty(inputRecord);
        if (inputRecord.hasStringValue("isFromRiskRelationPage")) {
            RiskRelationFields.setPcfRiskCountyCode(outputRecord, result);
        } else {
            RiskFields.setPcfRiskCounty(outputRecord, result);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultValueForPcfCounty", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Get default pcf risk class field value.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getDefaultValueForPcfRiskClass(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultValueForPcfRiskClass", inputRecord);
        }
        Record outputRecord = new Record();
        String result = getRiskDAO().getDefaultValueForPcfRiskClass(inputRecord);
        if (inputRecord.hasStringValue("isFromRiskRelationPage")) {
            RiskRelationFields.setPcfRiskClassCode(outputRecord, result);
        } else {
            RiskFields.setPcfRiskClass(outputRecord, result);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultValueForPcfRiskClass", outputRecord);
        }
        return outputRecord;
    }

    @Override
    public boolean isAddtlExposureAvailable(Record inputRecord,
                                            PolicyHeader policyHeader) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddtlExposureAvailable", new Object[]{inputRecord, policyHeader});
        }

        String riskTypeCode = null;
        if (RiskFields.hasRiskTypeCode(inputRecord)) {
            riskTypeCode = RiskFields.getRiskTypeCode(inputRecord);
        }
        else {
            // Add else branch in case the risk type code is not existing in the input record.
            // For example, when we add occupant for a slot risk, the risk type code is not existing in the input record.
            riskTypeCode = policyHeader.getRiskHeader().getRiskTypeCode();
        }
        boolean result = getPolicyAttributesManager().isAddtlExposureAvailable(
            policyHeader.getPolicyTypeCode(),
            riskTypeCode,
            RiskFields.getRiskEffectiveFromDate(inputRecord));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddtlExposureAvailable", result);
        }
        return result;
    }

    public RiskManagerImpl() {
    }

    public RiskDAO getRiskDAO() {
        return m_riskDAO;
    }

    public void setRiskDAO(RiskDAO riskDAO) {
        m_riskDAO = riskDAO;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
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

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }


    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }


    public AffiliationManager getAffiliationManager() {
        return m_affiliationManager;
    }

    public void setAffiliationManager(AffiliationManager affiliationManager) {
        m_affiliationManager = affiliationManager;
    }


    public CoiManager getCoiManager() {
        return m_coiManager;
    }

    public void setCoiManager(CoiManager coiManager) {
        m_coiManager = coiManager;
    }

    public ScheduleManager getScheduleManager() {
        return m_scheduleManager;
    }

    public void setScheduleManager(ScheduleManager scheduleManager) {
        m_scheduleManager = scheduleManager;
    }

    public CoverageClassManager getCoverageClassManager() {
        return m_coverageClassManager;
    }

    public void setCoverageClassManager(CoverageClassManager coverageClassManager) {
        m_coverageClassManager = coverageClassManager;
    }

    public PolicyAttributesManager getPolicyAttributesManager() {
        return m_policyAttributesManager;
    }

    public void setPolicyAttributesManager(PolicyAttributesManager policyAttributesManager) {
        m_policyAttributesManager = policyAttributesManager;
    }

    private RiskDAO m_riskDAO;
    private TransactionManager m_transactionManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private CoverageManager m_coverageManager;
    private PMDefaultManager m_pmDefaultManager;
    private EntityManager m_entityManager;
    private DBUtilityManager m_dbUtilityManager;
    private ComponentManager m_componentManager;
    private AffiliationManager m_affiliationManager;
    private CoiManager m_coiManager;
    private CoverageClassManager m_coverageClassManager;
    private ScheduleManager m_scheduleManager;
    private PolicyAttributesManager m_policyAttributesManager;

    protected static final String SAVE_PROCESSOR = "RiskManager";

    private static String NUMBER_FIELDS[] = {
        "numberErVisit",
        "averageDailyCensus",
        "numberBed",
        "numberExtBed",
        "numberSkillBed",
        "numberInpatientSurg",
        "numberOutpatientSurg",
        "numberQbDelivery",
        "annualOutpatientVisit",
        "numberEmployedDoctor"
    };

    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{RiskFields.RISK_EFFECTIVE_TO_DATE, RiskFields.ROLLING_IBNR_B, RiskFields.EXCLUDE_COMP_GR1_B, RiskFields.EXCLUDE_COMP_GR2_B});
}
