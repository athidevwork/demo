package dti.pm.componentmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordComparator;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.SortOrder;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.PolicyStatus;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.core.cachemgr.PolicyCacheManager;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.core.data.RowAccessorRecordLoadProcessor;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.AddAuditHistoryIndLoadProcessor;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.componentmgr.dao.ComponentDAO;
import dti.pm.dao.DataFieldNames;
import dti.pm.pmdefaultmgr.PMDefaultManager;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.Term;
import dti.pm.policymgr.regionalmgr.RmComponentFields;
import dti.pm.policymgr.service.RiskInquiryFields;
import dti.pm.riskmgr.RiskFields;
import dti.pm.tailmgr.TailFields;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.transactionmgr.transaction.dao.TransactionDAO;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.validationmgr.impl.PreOoseChangeValidator;
import dti.pm.validationmgr.impl.StandardEffectiveToDateRecordValidator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details of ComponentManager Interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 18, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/12/2007       sxm         1) Added ComponentOwner to saveAllDefaultComponent().
 *                              2) Pass rowId to validateCycleYears() and validateNumDays()
 *                              3) Replace productCoverageCodeLOVLABEL with productCoverageDesc
 * 09/17/2007       fcb         1) loadAvailableOrDependentComponent: added logic to set the
 *                                 CoverageBaseRecordId for ComponentFields.
 * 01/15/2008       fcb         1) isCancWipEdit and TransactionDAO added.
 * 03/27/2008       fcb         1) setFieldsForExpireComponent added.
 * 03/27/2008       yhchen      #81217 Add logics to check if low value and high value is null
 * 05/05/2008       yhchen      #81217 check if the owner coverage id is null for validate component for prior act
 * 09/22/2008       yhyang      #86561 Modify the logic of setting default value to Effective_To_Date when the cycleYears is not 0
 *                                     in getInitialValuesForAddComponent().
 * 01/15/2009       yhyang      #89108 Add five methods for Process RM component: loadAllProcessingEvent,
 *                              loadAllProcessingDetail,saveAllProcessingEvent,processEvent,processRmDiscount.
 * 01/20/2009       yhyang      #89109 Add four methods for Porcess Org/Corp Component:loadAllCorpOrgDiscountMember,
 *                              processCorpOrgDiscount,loadAllProcessEventHistory,loadAllProcessDetailHistory.
 * 03/13/2009       yhyang      #91508 Handle the component value or low/high value is non-int.
 * 09/28/2009       fcb         98981: added logic for Cycle Begin date/Expiration date for NDD.
 * 03/01/2010       fcb         104191: passed transaction id to getDefaultLevel call.
 * 03/10/2010       fcb         105137: changed COVG_TAB_COMP to COVG_COMP_TAB.
 * 04/27/2010       bhong       106372: 1)removed the incorrect overlap check logics for prior acts component
 *                                      2)Fixed a few mistake in getIntialValues for prior act component
 * 06/29/2010       syang       108782 - Modified getInitialValuesForAddProcessComponent() to add % to low/high value
 *                                       if the percentValueB is Y.
 * 08/04/2010       syang       103793 - Modified loadAllSurchargePoint() to hide Save button while PM_CUST_SURCG_POINTS is 'Y'.
 * 09/07/2010       dzhang      #108261 Modified loadAllSourceComponent() to add a new parameter compGridFields
 * 09/20/2010       syang       Issue 110819 - Added FilterOfficialRowForEndquoteRecordLoadProcessor to loadAllComponent().
 * 01/13/2011       dzhang      Issue 115986 - Modified getInitialValuesForAddComponent() and validateAllComponents() to handle the reissue condition.
 * 02/09/2011       jshen       Issue 117529 - 1) Modified validateAllComponents() to validate num days only when cycle begin date is changed.
 *                                             2) Modified getInitialValuesForOoseComponent() to set component effective from date to official record if change date.
 * 02/17/2011       syang       116264 - Modified getInitialValuesForAddComponent(), system should default the greater date
 *                              between latest coverage expiration date and coverage base effective to date to component expiration date.
 * 05/18/2011       dzhang      117246 - Modified getInitialValuesForAddComponent and added isAddComponentAllowed.
 * 05/25/2011       gxc         Issue 105791 - getInitialValuesForAddComponent() modified.
 * 06/10/2011       wqfu        103799 - Modified loadAllComponent to handle copy prior act stats.
 * 06/22/2011       wqfu        103810 - Modified getInitialValuesForAddComponent to handle field compLongDescription. 
 * 06/29/2011       ryzhao      122363 - Modified getInitialValuesForAddComponent to use CoverageField.getCoverageStatus() to get coverage status. 
 * 08/26/2011       ryzhao      122840 - Modified getInitialValuesForOoseComponent. If it is change component value operation, change codes to below.
 *                                       1) Copy the component effective to date from original record.
 *                                       2) Make component effective to date readonly.
 * 08/30/2011       ryzhao      124458 - Modified validateCycleYears and validateNumDays to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 09/06/2011       wfu         124768 - Modified validateAllComponents to set component value as 0 instead of NULL.
 * 09/14/2011       wfu         123860 - Modified validateAllComponents to exclude tail components for rule 6.
 * 09/05/2011       wfu         124768 - Modified validateAllComponents to set component value as 0 instead of NULL
 *                                       exclude during changing component exp date(OOSE).
 * 09/16/2011       ryzhao      122840 - 1) Rollback previous changes.
 *                                       2) Modified validateAllComponents to check if the official component record
 *                                          has a temp record exists for the specific transaction.
 * 09/21/2011       wfu         125245 - Added validateCompValueRange for both adding components and copy all components.
 * 10/28/2011       ryzhao      122840 - 1) Added isComponentTempRecordExist().
 *                                       2) Move the logic to check if the official component record
 *                                          has a temp record exists for the specific transaction
 *                                          from validateAllComponents to ComponentEntitlementRecordLoadProcessor.
 * 11/01/2011       ryzhao      122840 - Modified per Stephen's comments.
 * 11/18/2011       ryzhao      127233 - Modified loadAllComponent() with tail component logic.
 *                                       As is described in View Tail UC, we should pass the term eff date and term exp
 *                                       date to the corresponding input parameters of the procedure.
 *                                       The original code is not consistent with the UC.
 * 11/25/2011       ryzhao      127658 - Modified loadAllComponent() with tail component logic. We should set term exp
 *                                       date to 01/01/3000 to pull the future TAIL components.
 * 12/07/2011       wfu         127859 - Modified loadAvailableOrDependentComponent to add condition when calling
 *                                       processMappedDefaults for coverage component if without primary risk.
 * 02/21/2012       xnie        130602 - Modified validateAllComponents to set component value based on component type.
 *                                       If component is cycled or new doctor discount, the component should be set to
 *                                       "". If not and it is null, the component should be set to "0".
 * 02/29/2012       xnie        130506 - Modified saveAllComponent(): Filter deleted components from saving WIP
 *                                       inserted/updated components logic.
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 04/26/2012       hxu         132111 - Modified getInitialValuesForAddComponent to set effective to date as the
 *                                       coverage continuous effective to date for short term component.
 * 04/27/2012       jshen       132111 - roll back the change
 *
 * 08/16/2012       awu         130383 - Modified getInitialValuesForOoseComponent to set the backup fields to empty, 
 *                                       and if change date, set these fields to readonly.
 * 10/26/2012       ryzhao      138511 - Modified loadAllComponent() to set risk base record id to null for tail.
 * 01/08/2013       fcb         137981 - changes related to Pm_Dates modifications.
 * 01/22/2013       tcheng      140034 - Modified loadAllSourceComponent() to filter duplicate component on Copy All Page.
 * 04/25/2013       xnie        142770 - 1) Modified getInitialValuesForAddComponent() to set coverage status to inputRecord.
 *                                       2) Modified getInitialValuesForOoseComponent() to set coverage status to inputRecord.
 *                                       3) Modified getInitialValuesForOoseComponent() to set correct owner but not hard
 *                                          code as COVERAGE.
 * 08/27/2013       adeng       146452 - Modified validateAllComponents() to remove useless code. The coverage effective
 *                                       to date have been set to sLatestCoverageExpDate without any condition. The 3 rows
 *                                       afterward codes made no sense. These codes were added in fixing of issue 115986.
 * 08/30/2013       adeng       146452 - Roll back previous changes.
 * 09/02/2013       adeng       146449 - It was incorrect to set the effective from date equal to the transaction
 *                                       effective date anyway. Modified saveAllComponent() to add condition to check if
 *                                       the owner is prior act or not first. If yes, we should not do anything.
 * 08/27/2013       xnie        146452 - Modified validateAllComponents() to
 *                                       1) Removed codes which set last coverage
 *                                          expiring date when TEMP coverage transaction is the last transaction in
 *                                          policy header. visibleCoverageRecords has a reasonable 'order by', so we only
  *                                         need to get the last record's effective to date. These removed codes tried
  *                                         to imitate PB's 115986 fix logic, but in fact we don't need to do that.
 *                                       2) Corrected error message when component expiring date is NULL.
 * 09/26/2013       xnie        146797 - Modified validateAllComponents() to prevent changing both component value and
 *                                       component expiring date in same transaction except OOSE transaction for a
 *                                       record which was saved as official before.
 * 10/14/2013       xnie        146082 - 1) Added validateCompChangeDateAllowed().
 *                                       2) Modified validateAllComponents() to add logic to check if component expiring
 *                                          date can be changed in out of sequence transaction.
 * 10/02/2013       fcb         145725 - used PolicyHeader to get/set cancWipRule and problemPolicyB
 *                                     - changed the list of parameters for isProblemPolicy
 *                                     - PreOoseChangeValidator called only for OOSE transactions.
 * 11/28/2013       adeng       150161 - Modified getInitialValuesForAddComponent to prevent alerting reminder for part
 *                                       time note in prior act page.
 * 01/22/2014       jyang       150639 - 1. Update getInitialValuesForAddComponent() to get coverage continuous date from
 *                                          backend for short term risks.
 *                                       2. Update validateAllComponents() to get coverage expiration date from backend and
 *                                          proceed the validation for components.
 *                                       3. Move getCoverageExpirationDate from CoverageManager to ComponentManager.
 * 01/30/2014       adeng       149551 - Modified saveAllComponent() to reuse the same component base Id if the same
 *                                       component exists, or set to system assigned base fk by calling
 *                                       pm_get_component_base.
 * 03/13/2014       awu         152963 - Modified getInitialValuesForAddComponent to remove the part time message. It will
 *                                       be added in maintainComponent.js.
 * 04/15/2014       xnie        153864 - Modified validateAllComponents() to roll back 150639 changes for validation #6.
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Component
 * 10/07/2014       kxiang      157857 - 1. Modified loadAllSourceComponent(): when do copyAll, filter component which
 *                                          is earlier than current transaction.
 *                                       2. Added method getEffectiveComponentForCopyAll().
 * 10/08/2014       awu         157694 - Modified validateAllComponents to correct the coverage description for prior acts.
 * 10/12/2014       jyang       157749 - 1.Modified getInitialValuesForAddComponent():
 *                                         a.Get correct coverage version eff/exp date when the coverage is short term coverage.
 *                                         b.For cancelWIP screen mode, use transaction effDate for component expDate.
 *                                       2.Modified getEffectiveDateForAddComponent,the effectiveFromDate should not equals
 *                                       to policy term effDate.
 * 09/16/2014       awu         157552 - 1. Modified saveAllComponent to add duplication validation;
 *                                       2. Added setInputForDuplicateValidation, handleDuplicateValidation.
 * 11/06/2014       awu         157552 - Modified setInputForDuplicateValidation to remove the baseRiskId.
 * 11/11/2014       kxiang      157857 - Modified getEffectiveComponentForCopyAll() to use removeRecord instead of
 *                                       addRecord method.
 * 11/18/2014       fcb         157975 - Added logic to skip NDD expiration date validation.
 * 11/20/2014       awu         154316 - 1. Added getComponentSequenceId for Policy Change Service using.
 *                                       2. Modified setFieldsForExpireComponent to use toString() to avoid
 *                                          the error of BigDecimal cast to String
 * 01/09/2015       fcb         160027 - Bug fixed in validateNumDays for the case where for NDD the validation for
 *                                       dates is turned off: the effective date should not be reset when the NDD
 *                                       duration is less than the years in the NDD cycle.
 * 04/01/2015       fcb         162078 - Initialized componentValueCompare variable.
 *                                     - fixed indentation.
 * 07/14/2015       kxiang      163584 - 1. Modified validateAllComponents to modify the condition before calling
 *                                          validateNumDays
 *                                       2. Modified getInitialValuesForAddProcessComponent When component is not a New
 *                                          doctor component or a cycled component, set component value to low value
 *                                       3. Modified applyMassComponent to use "" to append to values if component value
 *                                          is null.
 *                                       4. Modified validateApplyMassComponet to add condition to check it's not a New
 *                                          doctor component or a cycled component.
 *                                       5. Modified isValidComponentValue to add logical to skip validation for
 *                                          new doctor component or cycled component.
 * 11/19/2015       eyin        167171 - Modified loadAvailableOrDependentComponent(), remove duplicate validation condition.
 * 07/22/2016       bwang       178033 - Modified loadAllSourceComponent(),changed integer type variables to long
 *                                       type which are from PK/FK fields in DB.
 * 05/24/2017       xnie        185494 - Modified saveAllComponent() to reset term eff/exp date for prior acts components.
 * 06/28/2017       tzeng       186273 - Modified loadAvailableOrDependentComponent to set MainCoverageBaseRecordId for
 *                                       tail coverage.
 * 08/28/2018       ryzhao      188891 - Added loadExpHistoryInfo() and loadClaimInfo() for new experience discount
 *                                       history page.
 * ---------------------------------------------------
 */
public class ComponentManagerImpl implements ComponentManager {
                                                                     

    /**
     * Retrieves all coverage components
     *
     * @param policyHeader  policy header
     * @param owner         Represents the type of component owner. Can be "Policy", "Coverage" or "Tail" type.
     * @param inputRecord
     * @param ownerRecords  owner record set
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllComponent(PolicyHeader policyHeader, Record inputRecord, ComponentOwner owner, RecordSet ownerRecords, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllComponents", new Object[]{policyHeader, owner});
        }

        Record input = new Record();
        input.setFields(policyHeader.toRecord(), false);
        input.setFields(inputRecord, false);
        // For policy level component the risk base record id should be null,elsewise db doesn't return the correct result.
        /**
         * issue 138511
         *
         * For tail level component, the risk base record id should also be null, otherwise it will only return
         * components for specific risk. The component records for other risks will disappear after the page is refreshed.
         */
        if((owner.isPolicyOwner() || owner.isTailOwner()) && input.hasStringValue("riskBaseRecordId")){
            input.setFieldValue("riskBaseRecordId",null);
        }

        RecordMode recordModeCode = RecordMode.TEMP;
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        PMCommonFields.setRecordModeCode(input, recordModeCode);
        ComponentFields.setComponentTypeCode(input, owner.getOwnerType());
        ComponentFields.setComponentOwner(input, owner.getOwnerName());

        // Setup the entitlements load processor
        RecordLoadProcessor rowAccessorLP = null;
        if (!owner.isPriorActOwner()) {
            rowAccessorLP = new RowAccessorRecordLoadProcessor(ComponentFields.POLICY_COV_COMPONENT_ID, ComponentFields.EFFECTIVE_FROM_DATE,
                ComponentFields.EFFECTIVE_TO_DATE, policyHeader, policyHeader.getScreenModeCode());
        }
        else {
            ScreenModeCode screenMode = policyHeader.getScreenModeCode();
            PolicyStatus policyStatus = policyHeader.getPolicyStatus();
            Transaction trans = policyHeader.getLastTransactionInfo();
            boolean isViewModeWIP = (screenMode.isManualEntry() || screenMode.isOosWIP() || screenMode.isCancelWIP()) ||
                ((!(screenMode.isViewPolicy() || screenMode.isViewEndquote() || screenMode.isResinstateWIP())) &&
                    ((policyStatus.isActive() || policyStatus.isPending())));
            rowAccessorLP = new RowAccessorRecordLoadProcessor(
                ComponentFields.POLICY_COV_COMPONENT_ID, ComponentFields.EFFECTIVE_FROM_DATE,
                ComponentFields.EFFECTIVE_TO_DATE, trans.getTransactionStatusCode().isInProgress(),
                isViewModeWIP, trans.getTransactionLogId(), trans.getTransEffectiveFromDate());
        }
        RecordLoadProcessor entitlementLoadProcessor = new ComponentEntitlementRecordLoadProcessor(this, policyHeader, inputRecord,
            policyHeader.getScreenModeCode(), owner, ownerRecords);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            origFieldLoadProcessor, loadProcessor);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            entitlementLoadProcessor, loadProcessor);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            rowAccessorLP, loadProcessor);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, new AddAuditHistoryIndLoadProcessor());
        // Issue 110819, filter official record for end quote.
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor = new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "policyCovComponentId");
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, endquoteLoadProcessor);
        // Gets components
        if (owner.isTailOwner()) {
            PolicyHeaderFields.setTermEffectiveToDate(input, "01/01/3000");
            PolicyHeaderFields.setTermEffectiveFromDate(input, policyHeader.getTermEffectiveFromDate());
        }
        RecordSet rs;
        if (inputRecord.hasStringValue(RequestIds.IS_COPY_ACTS_STATS) &&
            YesNoFlag.getInstance(inputRecord.getStringValue(RequestIds.IS_COPY_ACTS_STATS)).booleanValue()) {
            rs = getComponentDAO().loadAllPendPriorActComp(input, loadProcessor);
            Iterator iter = rs.getRecords();
            while (iter.hasNext()) {
                Record record = (Record)iter.next();
                record.setUpdateIndicator(UpdateIndicator.INSERTED);
            }
        } else {
            rs = getComponentDAO().loadAllComponents(input, loadProcessor);
        }

        ComponentFields.setComponentOwner(rs.getSummaryRecord(), owner.getOwnerName());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllComponents", rs);
        }
        return rs;
    }

    /**
     * Retrieves all coverage components
     *
     * @param policyHeader policy header
     * @param owner        Represents the type of component owner. Can be "Policy", "Coverage" or "Tail" type
     * @param inputRecord
     * @param ownerRecords owner record set
     * @return RecordSet
     */
    public RecordSet loadAllComponent(PolicyHeader policyHeader, Record inputRecord, ComponentOwner owner, RecordSet ownerRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllComponents", new Object[]{policyHeader, owner});
        }

        RecordSet rs = loadAllComponent(policyHeader, inputRecord, owner, ownerRecords, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllComponents", rs);
        }
        return rs;
    }

    /**
     * Retrieves all coverage components
     *
     * @param policyHeader  policy header
     * @param insuredNumberId
     * @param owner         Represents the type of component owner. Can be "Policy", "Coverage" or "Tail" type.
     * @return RecordSet
     */
    public RecordSet loadAllComponentForWs(PolicyHeader policyHeader, String insuredNumberId, ComponentOwner owner) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllComponentForWs", new Object[]{policyHeader, insuredNumberId, owner});
        }

        Record input = new Record();
        input.setFields(policyHeader.toRecord(), false);
        input.setFieldValue(RiskInquiryFields.RISK_NUMBER_ID, insuredNumberId);

        // For policy level component the risk base record id should be null,elsewise db doesn't return the correct result.
        if(owner.isPolicyOwner() && input.hasStringValue("riskBaseRecordId") && (input.getStringValue("riskBaseRecordId") != null)){
             input.setFieldValue("riskBaseRecordId", null);
        }

        RecordMode recordModeCode = RecordMode.TEMP;
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        PMCommonFields.setRecordModeCode(input, recordModeCode);
        ComponentFields.setComponentTypeCode(input, owner.getOwnerType());
        ComponentFields.setComponentOwner(input, owner.getOwnerName());

        // Gets components
        if (owner.isTailOwner()) {
            PolicyHeaderFields.setTermEffectiveToDate(input, "01/01/3000");
            PolicyHeaderFields.setTermEffectiveFromDate(input, policyHeader.getTermEffectiveFromDate());
        }

        RecordLoadProcessor loadProcessor = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            origFieldLoadProcessor, loadProcessor);
        RecordSet rs = getComponentDAO().loadAllComponents(input, loadProcessor);

        ComponentFields.setComponentOwner(rs.getSummaryRecord(), owner.getOwnerName());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllComponents", rs);
        }
        return rs;
    }

    /**
     * Retrieves all source coverage components for risk copy all
     *
     * @param policyHeader  policy header
     * @param owner         Represents the type of component owner. Can be "Policy", "Coverage" or "Tail" type.
     * @param inputRecord
     * @param ownerRecords  owner record set
     * @param loadProcessor
     * @param compGridFields all the component gird fields
     * @return RecordSet
     */
    public RecordSet loadAllSourceComponent(PolicyHeader policyHeader, Record inputRecord, ComponentOwner owner, RecordSet ownerRecords, RecordLoadProcessor loadProcessor, String compGridFields) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSourceComponent", new Object[]{policyHeader, inputRecord, owner, ownerRecords, loadProcessor, compGridFields});
        }

        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(new ComponentCopyRecordLoadProcessor(compGridFields), loadProcessor);
        RecordSet rs = loadAllComponent(policyHeader, inputRecord, owner, ownerRecords, loadProcessor);
        // when do copyAll, filter component which is earlier than current transaction.
        if(inputRecord.hasField(ComponentFields.OPERATION) && ComponentFields.COPY_ALL.equalsIgnoreCase(ComponentFields.getOperation(inputRecord))){
            rs = getEffectiveComponentForCopyAll(rs, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }

        if (rs.getSize() > 1) {
        // sort the records by product_cov_component_id ASC, and then record_mode_code DESC, and then effective_to_date DESC
            RecordComparator rc;
            RecordSet recordSet;
            rc = new RecordComparator(ComponentFields.PRODUCT_COV_COMPONENT_ID, true, SortOrder.ASC, null);
            rc.addFieldComparator(RiskInquiryFields.RECORD_MODE_CODE, true, SortOrder.DESC, null);
            rc.addFieldComparator(ComponentFields.EFFECTIVE_TO_DATE, true, SortOrder.DESC, ConverterFactory.getInstance().getConverter(Date.class));
            RecordSet records = rs.getSortedCopy(rc);
            // filter duplicate records
            long productCovComponentId = 0;
            long tempProductCovComponentId = 0;
            Record sumRec = records.getSummaryRecord();
            List fieldNames = (List) ((ArrayList) records.getFieldNameList()).clone();
            recordSet = new RecordSet();
            recordSet.addFieldNameCollection(fieldNames);
            recordSet.setSummaryRecord(sumRec);
            // Replace the old record set by newly changed record set.
            for (int sortIdx = 0; sortIdx < records.getSize(); sortIdx++) {
                Record currentRecord = records.getRecord(sortIdx);
                if (currentRecord.hasStringValue(ComponentFields.PRODUCT_COV_COMPONENT_ID)) {
                    tempProductCovComponentId = currentRecord.getLongValue(ComponentFields.PRODUCT_COV_COMPONENT_ID).longValue();
                }
                if (tempProductCovComponentId != 0 && productCovComponentId == tempProductCovComponentId) {
                    continue;
                }
                else {
                    productCovComponentId = tempProductCovComponentId;
                    recordSet.addRecord(currentRecord);
                }
            }
            rs = recordSet;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSourceComponent", rs);
        }

        return rs;
    }

    /**
     * Wrapper to invoke the save of all inserted/updated Component records
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader the summary policy information corresponding to the provided coverages.
     * @param inputRecords a set of Records, each with the updated Component Detail info
     *                     matching the fields returned from the loadAllComponents method.
     * @param owner        Represents the type of component owner. Can be "Policy", "Coverage" or "Tail" type.
     * @param ownerRecords a set of owner's Records
     * @return the number of rows updated.
     */
    public int saveAllComponent(PolicyHeader policyHeader,
                                RecordSet inputRecords,
                                ComponentOwner owner,
                                RecordSet ownerRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllComponents",
            new Object[]{policyHeader, inputRecords, owner, ownerRecords});

        if (owner.isPriorActOwner()) {
            //Reuse the same component base Id if the same component exists, or set to system assigned base fk by calling pm_get_component_base.
            if (inputRecords.getSize() > 1) {
                // sort the records by product_cov_component_id ASC, and then record_mode_code DESC, and then effective_to_date DESC
                RecordComparator rc;
                rc = new RecordComparator(ComponentFields.PRODUCT_COV_COMPONENT_ID, true, SortOrder.ASC, null);
                rc.addFieldComparator(ComponentFields.EFFECTIVE_FROM_DATE, true, SortOrder.ASC, ConverterFactory.getInstance().getConverter(Date.class));
                rc.addFieldComparator(ComponentFields.EFFECTIVE_TO_DATE, true, SortOrder.ASC, ConverterFactory.getInstance().getConverter(Date.class));
                inputRecords = inputRecords.getSortedCopy(rc);
                String productCovComponentId = null;
                String tempProductCovComponentId = null;
                String polCovCompBaseRecId = null;
                for (int sortIdx = 0; sortIdx < inputRecords.getSize(); sortIdx++) {
                    Record currentRecord = inputRecords.getRecord(sortIdx);
                    productCovComponentId = ComponentFields.getProductCovComponentId(currentRecord);
                    if (!productCovComponentId.equals(tempProductCovComponentId)) {
                        tempProductCovComponentId = productCovComponentId;
                        polCovCompBaseRecId = ComponentFields.getPolCovCompBaseRecId(currentRecord);
                    }
                    ComponentFields.setPolCovCompBaseRecId(currentRecord, polCovCompBaseRecId);
                }
            }
        }

        // validate the input components before saving them
        validateAllComponents(policyHeader, inputRecords, owner, ownerRecords);

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        changedRecords.setFieldsOnAll(policyHeader.toRecord(), false);

        // Set the current transaction id on all records
        changedRecords.setFieldValueOnAll("transactionLogId", policyHeader.getLastTransactionId());

        // Split the input records for add, update and delete
        // Get the WIP records and Official records
        RecordSet wipRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet offRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
        RecordSet ooseRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.REQUEST));

        // Get the inserted and updated WIP records
        RecordSet insertedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        insertedWipRecords.setFieldValueOnAll(ROW_STATUS, NEW);
        RecordSet updatedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        updatedWipRecords.setFieldValueOnAll(ROW_STATUS, MODIFIED);

        // Inserted or updated WIP records in batch mode
        updatedWipRecords.addRecords(insertedWipRecords);
        // Reset term eff date for prior acts components.
        if (owner.isPriorActOwner()) {
            Iterator updatedWipRecordIter = updatedWipRecords.getRecords();
            while (updatedWipRecordIter.hasNext()) {
                Record updatedWipRecord = (Record) updatedWipRecordIter.next();
                PolicyHeaderFields.setTermEffectiveFromDate(updatedWipRecord, ComponentFields.getEffectiveFromDate(updatedWipRecord));
                ComponentFields.setTermEffectiveToDate(updatedWipRecord, ComponentFields.getEffectiveToDate(updatedWipRecord));
            }
        }
        int updateCount = getComponentDAO().addAllComponents(updatedWipRecords);

        // Delete the WIP records marked for delete in batch mode
        RecordSet deleteRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        if (deleteRecords.getSize() > 0) {
            updateCount += getComponentDAO().deleteAllComponents(deleteRecords);
        }
        // Update the OFFICIAL records marked for update in batch mode
        RecordSet updateRecords = offRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));

        // For the oose records (recordModeCode = 'REQUEST') only reset recordModeCode = 'TEMP'
        ooseRecords.setFieldValueOnAll(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP);

        if (!owner.isPriorActOwner()) {
            // Mapping the component effective from date to the transaction effective date when owner is prior act.
            updateRecords.setFieldValueOnAll("componentEffectiveFromDate", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }

        setFieldsForExpireComponent(updateRecords);

        updateRecords.addRecords(ooseRecords);
        updateCount += getComponentDAO().updateAllComponents(updateRecords);

        //Validate for duplicate component records.
        if (updateCount > 0) {
            Record inputRecord = setInputForDuplicateValidation(policyHeader, ownerRecords, owner);
            Record outputRec = getComponentDAO().validateComponentDuplicate(inputRecord);
            if (outputRec != null && "N".equals(outputRec.getStringValue("result"))) {
                handleDuplicateValidation(policyHeader, inputRecords, ownerRecords, outputRec, owner);
                if (MessageManager.getInstance().hasErrorMessages()) {
                    throw new ValidationException("Component is duplicated.");
                }
            }
        }

        l.exiting(getClass().getName(), "saveAllComponents", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Prepare the input parameters to do duplication validation.
     * @param policyHeader
     * @param ownerRecords
     * @param owner
     * @return
     */
    private Record setInputForDuplicateValidation(PolicyHeader policyHeader,
                                                  RecordSet ownerRecords,
                                                  ComponentOwner owner) {
        Logger l = LogUtils.enterLog(getClass(), "setInputForDuplicateValidation",
            new Object[]{policyHeader, ownerRecords, owner});

        // Build a comma delimited list of coverage ids.
        int size = ownerRecords.getSize();
        String parentBaseRecordIds = "";
        if (owner.isCoverageOwner()) {
            for (int i = 0; i < size; i++) {
                Record tempRec = ownerRecords.getRecord(i);
                String tempCoverageRecordId = CoverageFields.getCoverageBaseRecordId(tempRec);
                if (parentBaseRecordIds.indexOf(tempCoverageRecordId) < 0) {
                    parentBaseRecordIds = parentBaseRecordIds + "," + tempCoverageRecordId;
                }
            }
        }
        else if (owner.isTailOwner()) {
            for (int i = 0; i < size; i++) {
                Record tempRec = ownerRecords.getRecord(i);
                String tempCoverageRecordId = TailFields.getTailCovBaseRecordId(tempRec);
                if (parentBaseRecordIds.indexOf(tempCoverageRecordId) < 0) {
                    parentBaseRecordIds = parentBaseRecordIds + "," + tempCoverageRecordId;
                }
            }
        }
        else if (owner.isPriorActOwner()) {
            parentBaseRecordIds = "," + policyHeader.getCoverageHeader().getCoverageBaseRecordId();
        }
        else if (owner.isPolicyOwner()) {
            parentBaseRecordIds = "," + policyHeader.getPolicyId();
        }
        parentBaseRecordIds = parentBaseRecordIds + ",";

        Record parameterRecord = new Record();
        TransactionFields.setTransactionLogId(parameterRecord, policyHeader.getCurTransactionId());
        parameterRecord.setFieldValue("coverageBaseRecordList", parentBaseRecordIds);
        ComponentFields.setComponentOwner(parameterRecord, owner.getName());

        l.exiting(getClass().getName(), "setInputForDuplicateValidation", parameterRecord);
        return parameterRecord;
    }

    /**
     * Handle the validation error message after duplication validation is failed.
     * @param policyHeader
     * @param inputRecords
     * @param resultRecord
     * @param owner
     */
    private void handleDuplicateValidation(PolicyHeader policyHeader,
                                           RecordSet inputRecords,
                                           RecordSet ownerRecords,
                                           Record resultRecord,
                                           ComponentOwner owner) {
        Logger l = LogUtils.enterLog(getClass(), "handleDuplicateValidation",
            new Object[]{policyHeader, inputRecords, ownerRecords, resultRecord, owner});

        //The validation messages returned from backend are like: message#componentId@message#componetId
        String validationResultList = resultRecord.getStringValue("validationMsgList");
        String[] validateionResults = validationResultList.split("@");
        for (int i = 0; i < validateionResults.length; i++) {
            String[] validateDetails = validateionResults[i].split("#");
            String errorMsg = validateDetails[0];
            String componentId = validateDetails[1];
            String coverageId = "";
            int size = inputRecords.getSize();
            for (int j = 0; j < size; j++) {
                Record tempRec = inputRecords.getRecord(j);
                if (ComponentFields.getPolicyCovComponentId(tempRec).equals(componentId)) {
                    if (owner.isPolicyOwner()) {
                        coverageId = policyHeader.getPolicyId();
                    }
                    else {
                        coverageId = CoverageFields.getCoverageId(tempRec);
                    }
                    break;
                }
            }
            MessageManager.getInstance().addErrorMessage("pm.common.invalid.data",
                new String[]{errorMsg}, "", coverageId + "," + componentId);
        }
        l.exiting(getClass().getName(), "handleDuplicateValidation");
    }

    /**
     * To skip standard effective to date validation in OOSE component: copy componentEffectiveFromDate to OOSE records.
     *
     * @param inputRecords
     * @param ooseRecord
     */
    private void copyOoseComponentEffFromDateBeforeValidation(RecordSet inputRecords, Record ooseRecord) {
        RecordSet offRecords = inputRecords.getSubSet(new RecordFilter(
            PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));

        String compBaseRecId = ComponentFields.getPolCovCompBaseRecId(ooseRecord);
        String effFromDate = ComponentFields.getEffectiveFromDate(ooseRecord);
        Iterator offRecIter = offRecords.getRecords();
        while (offRecIter.hasNext()) {
            Record offRec = (Record) offRecIter.next();
            String offCompBaseRecId = ComponentFields.getPolCovCompBaseRecId(offRec);
            if (compBaseRecId.equals(offCompBaseRecId) && (effFromDate == null || effFromDate.equals(""))) {
                ComponentFields.setEffectiveFromDate(ooseRecord, ComponentFields.getEffectiveFromDate(offRec));
                ooseRecord.setFieldValue("copyOoseFieldValue", "Y");
            }
        }
    }

    /**
     * To skip standard effective to date validation in OOSE component: clear componentEffectiveFromDate of OOSE record.
     *
     * @param ooseRecord
     */
    private void clearOoseComponentEffFromDateAfterValidation(Record ooseRecord) {
        if (ooseRecord.getBooleanValue("copyOoseFieldValue", false).booleanValue()) {
            ComponentFields.setEffectiveFromDate(ooseRecord, null);
            ooseRecord.remove("copyOoseFieldValue");
        }
    }

    /**
     * Get Cancel WIP mode and Rule
     *
     * @param policyHeader
     * @return a record inluding the cancel wip mode and rule infos.
     */
    public Record getCancelWipRule(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCancelWipModeAndRule", new Object[]{policyHeader});
        }

        Record rec = null;
        if (policyHeader.hasCancWipEditCache()) {
            rec = new Record();
            rec.setFieldValue("returnValue", policyHeader.getCancWipEditCache());
        }
        else {
        Record record = policyHeader.toRecord();
        ComponentFields.setIssureCompanyEntityId(record, policyHeader.getIssueCompanyEntityId());

            rec = getComponentDAO().getCancelWipRule(record);
            policyHeader.setCancWipEditCache(rec.getStringValue("returnValue"));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCancelWipModeAndRule", rec);
        }
        return rec;
    }

    protected int getCycleYears(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCycleYears", new Object[]{inputRecord});
        }

        Record record = new Record();
        ComponentFields.setProductCovComponentId(record, ComponentFields.getProductCovComponentId(inputRecord));
        ComponentFields.setEffectiveFromDate(record, ComponentFields.getEffectiveFromDate(inputRecord));
        int cycleYears = getComponentDAO().getCycleYearsForComponent(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCycleYears", new Integer(cycleYears));
        }
        return cycleYears;
    }

    /**
     * Validate component cycle years
     *
     * @param inputRecord
     * @param rowId
     * @return
     */
    protected boolean validateCycleYears(Record inputRecord, String rowId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCycleYears", new Object[]{inputRecord, rowId});
        }

        //String rowNum = String.valueOf(inputRecord.getRecordNumber()+1);
        String rowNum = String.valueOf(Integer.parseInt(inputRecord.getStringValue(ROW_NUM)) + 1);
        String productCovgDesc = CoverageFields.getProductCoverageDesc(inputRecord);
        String coverageComponentDesc = ComponentFields.getShortDescription(inputRecord);
        boolean isValid = false;
        int cycleYears = getCycleYears(inputRecord);

        String sCurrentCycleDate = ComponentFields.getComponentCycleDate(inputRecord);
        String sTempCompExpirationDate = null;
        if (sCurrentCycleDate != null) {
            Date currentCycleDate = DateUtils.parseDate(sCurrentCycleDate);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(currentCycleDate);
            int nYear = calendar.get(Calendar.YEAR);
            int nMonth = calendar.get(Calendar.MONTH) + 1;
            int nDay = calendar.get(Calendar.DATE);
            int nNewYear = nYear + cycleYears;
            sTempCompExpirationDate = String.valueOf(nMonth) + "/" + String.valueOf(nDay) + "/" + String.valueOf(nNewYear);
            Date tempCompExpirationDate = DateUtils.parseDate(sTempCompExpirationDate);
            String effectiveToDate = ComponentFields.getEffectiveToDate(inputRecord);
            Date expDate = DateUtils.parseDate(effectiveToDate);
            if (expDate.after(tempCompExpirationDate)) {
                // Set back the original value
                ComponentFields.setEffectiveToDate(inputRecord, ComponentFields.getOrigEffectiveToDate(inputRecord));
                MessageManager.getInstance().addErrorMessage("pm.maintainComponent.newDoctor.effectiveToDate.error",
                    new Object[]{rowNum, productCovgDesc, coverageComponentDesc, FormatUtils.formatDateForDisplay(sTempCompExpirationDate)},
                    ComponentFields.EFFECTIVE_TO_DATE, rowId);
            }
        }

        // Put cycleYears into inputRecord which will be used by validateNumDays()
        ComponentFields.setCycleYears(inputRecord, String.valueOf(cycleYears));
        // Put temp component expiration date into inputRecord which will be used to set value in validateNumDays()
        inputRecord.setFieldValue(TEMP_COMP_EXPIRATION_DATE, sTempCompExpirationDate);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCycleYears", Boolean.valueOf(isValid));
        }
        return isValid;
    }

    /**
     * Validate component Num days
     *
     * @param inputRecord
     * @param rowId
     * @param isSkipNddValB
     * @return
     */
    protected boolean validateNumDays(Record inputRecord, String rowId, boolean isSkipNddValB) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateNumDays", new Object[]{inputRecord, rowId, isSkipNddValB});
        }

//        String rowNum = String.valueOf(inputRecord.getRecordNumber()+1);
        String rowNum = String.valueOf(Integer.parseInt(inputRecord.getStringValue(ROW_NUM)) + 1);
        String productCovgDesc = CoverageFields.getProductCoverageDesc(inputRecord);
        String coverageComponentDesc = ComponentFields.getShortDescription(inputRecord);

        boolean isValid = false;
        Record record = new Record();
        ComponentFields.setEffectiveFromDate(record, ComponentFields.getEffectiveFromDate(inputRecord));
        ComponentFields.setComponentCycleDate(record, ComponentFields.getComponentCycleDate(inputRecord));
        int numDays = getComponentDAO().getNumDaysForComponent(record);

        int cycleYears;
        if (inputRecord.hasStringValue(ComponentFields.CYCLE_YEARS)) {
            String sCycleYears = ComponentFields.getCycleYears(inputRecord);
            cycleYears = Integer.valueOf(sCycleYears).intValue();
        }
        // To get the cycle years if not find it from the inputRecord,
        // this is happened when validating num days before validating cycle years for component.
        else {
            cycleYears = getCycleYears(inputRecord);
        }
        int cycleYearDays = cycleYears * 365;
        String sEffectiveFromDate = ComponentFields.getEffectiveFromDate(inputRecord);
        Date effFromDate = DateUtils.parseDate(sEffectiveFromDate);
        String sCurrentCycleDate = ComponentFields.getComponentCycleDate(inputRecord);
        if (numDays > cycleYearDays) {
            if (!isSkipNddValB) {
                // Set back the original value
                ComponentFields.setComponentCycleDate(inputRecord, ComponentFields.getComponentCycleDateOrg(inputRecord));
                MessageManager.getInstance().addErrorMessage("pm.maintainComponent.newDoctor.cycleDate.exceed.error",
                    new Object[]{rowNum, productCovgDesc, coverageComponentDesc, new Integer(cycleYears), FormatUtils.formatDateForDisplay(sEffectiveFromDate)},
                    ComponentFields.COMPONENT_CYCLE_DATE, rowId);
            }
        }
        else if (numDays == cycleYearDays) {
            if (!isSkipNddValB) {
                // Set back the original value
                ComponentFields.setComponentCycleDate(inputRecord, ComponentFields.getComponentCycleDateOrg(inputRecord));
                MessageManager.getInstance().addErrorMessage("pm.maintainComponent.newDoctor.cycleDate.equal.error",
                    new Object[]{rowNum, productCovgDesc, coverageComponentDesc, FormatUtils.formatDateForDisplay(sCurrentCycleDate)},
                    ComponentFields.COMPONENT_CYCLE_DATE, rowId);
            }
        }
        else {
            Date currentCycleDate = DateUtils.parseDate(sCurrentCycleDate);
            if (currentCycleDate.after(effFromDate)) {
                // Set back the original value
                ComponentFields.setComponentCycleDate(inputRecord, ComponentFields.getComponentCycleDateOrg(inputRecord));
                MessageManager.getInstance().addErrorMessage("pm.maintainComponent.newDoctor.cycleDate.error",
                    new Object[]{rowNum, productCovgDesc, coverageComponentDesc, sCurrentCycleDate},
                    ComponentFields.COMPONENT_CYCLE_DATE, rowId);
            }
            else {
                if (!isSkipNddValB) {
                    String sTempCompExpirationDate = null;
                    String sComponentCycleDateOrg = ComponentFields.getComponentCycleDateOrg(inputRecord);
                    if (sCurrentCycleDate != null && sComponentCycleDateOrg != null && !sCurrentCycleDate.equalsIgnoreCase(sComponentCycleDateOrg)) {
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTime(currentCycleDate);
                        int nYear = calendar.get(Calendar.YEAR);
                        int nMonth = calendar.get(Calendar.MONTH) + 1;
                        int nDay = calendar.get(Calendar.DATE);
                        int nNewYear = nYear + cycleYears;
                        sTempCompExpirationDate = String.valueOf(nMonth) + "/" + String.valueOf(nDay) + "/" + String.valueOf(nNewYear);
                        ComponentFields.setEffectiveToDate(inputRecord, sTempCompExpirationDate);
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateNumDays", Boolean.valueOf(isValid));
        }
        return isValid;
    }

    /**
     * Get the EFFECTIVE_DATE according with the Date Setup rule.
     *
     * @return
     */
    public String getEffectiveDateForAddComponent(PolicyHeader policyHeader) {
        String effectiveDate;
        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        if (screenModeCode.isCancelWIP() || screenModeCode.isResinstateWIP()) {
            effectiveDate = policyHeader.getTermEffectiveFromDate();
        }
        else {
            effectiveDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        }
        return effectiveDate;
    }

    /**
     * Load all available components
     *
     * @param policyHeader
     * @param record
     * @return
     */
    public RecordSet loadAllAvailableComponent(PolicyHeader policyHeader, Record record) {
        return loadAllAvailableComponent(policyHeader, record, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
    }

    /**
     * Load all available components
     *
     * @param policyHeader
     * @param record
     * @param loadProcessor
     * @return
     */
    public RecordSet loadAllAvailableComponent(PolicyHeader policyHeader, Record record, LoadProcessor loadProcessor) {
        RecordLoadProcessor compLoadProcessor = new ComponentAvailableRecordLoadProcessor();
        RecordLoadProcessor processor = RecordLoadProcessorChainManager.
            getRecordLoadProcessor(compLoadProcessor, (RecordLoadProcessor) loadProcessor);
        return loadAvailableOrDependentComponent(policyHeader, record, processor, false);
    }

    /**
     * Load all dependent components
     *
     * @param policyHeader
     * @param record
     * @param loadProcessor
     * @return
     */
    public RecordSet loadDependentComponent(PolicyHeader policyHeader, Record record, LoadProcessor loadProcessor) {

        RecordSet rs = loadAvailableOrDependentComponent(policyHeader, record, loadProcessor, true);
        // Do Filter
        rs = rs.getSubSet(new RecordFilter(ComponentFields.DEFAULT_DEPENDENT_COMPONENT_B, YesNoFlag.Y));
        return rs;
    }

    /**
     * Load all available or dependent components
     *
     * @param policyHeader
     * @param record
     * @param loadProcessor
     * @param hasComponentParent a flag used to check if has parent component
     * @return
     */
    private RecordSet loadAvailableOrDependentComponent(PolicyHeader policyHeader,
                                                        Record record,
                                                        LoadProcessor loadProcessor,
                                                        boolean hasComponentParent) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAvailableOrDependentComponent", new Object[]{policyHeader, record});
        }

        Record input = new Record();

        // Set product coverage code to input record
        CoverageFields.setProductCoverageCode(input, CoverageFields.getProductCoverageCode(record));

        // Get effective date and expire date based on system parameter "PM_ADD_COMPONENT_DT"
        String sysPara = SysParmProvider.getInstance().getSysParm(
            SysParmIds.PM_ADD_COMPONENT_DT, SysParmIds.AddComponentDateValues.TRANS);
        if (SysParmIds.AddComponentDateValues.TRANS.equals(sysPara)) {
            input.setFieldValue(DataFieldNames.EFF_DATE,
                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }
        else if (SysParmIds.AddComponentDateValues.TERM.equals(sysPara)) {
            input.setFieldValue(DataFieldNames.EFF_DATE, policyHeader.getTermEffectiveFromDate());
        }
        input.setFieldValue(DataFieldNames.EXP_DATE, policyHeader.getTermEffectiveToDate());

        // Get the earliest contiguous coverage effective date
        Record inputRec = new Record();
        inputRec.setFields(policyHeader.toRecord(), false);
        CoverageFields.setCoverageBaseRecordId(inputRec, CoverageFields.getCoverageBaseRecordId(record));
        ComponentFields.setCheckDt(inputRec, getEffectiveDateForAddComponent(policyHeader));

        Record contiguousDateRec = getComponentDAO().getCoverageContiguousEffectiveDate(inputRec);

        // Put contiguous coverage effective date into input record
        String sContiguousDate = contiguousDateRec.getStringValue("returnValue");
        // If sContiguousDate is not null and is not a valid date
        if (!StringUtils.isBlank(sContiguousDate)) {
            Date contiguousCovgEffDate = DateUtils.parseDate(sContiguousDate);
            Date termEffDate = DateUtils.parseDate(policyHeader.getTermEffectiveFromDate());
            if (contiguousCovgEffDate.after(termEffDate)) {
                CoverageFields.setCoverageEffectiveFromDate(input, policyHeader.getTermEffectiveFromDate());
            }
            else {
                CoverageFields.setCoverageEffectiveFromDate(input, sContiguousDate);
            }
        }
        else {
            CoverageFields.setCoverageEffectiveFromDate(input, CoverageFields.getCoverageBaseEffectiveFromDate(record));
        }
        CoverageFields.setCoverageEffectiveFromDate(input, CoverageFields.getCoverageBaseEffectiveFromDate(record));
        // Set current transaction log Id into input record
        TransactionFields.setTransactionLogId(input, policyHeader.getLastTransactionInfo().getTransactionLogId());

        // Set parent coverage component
        if (!hasComponentParent) {
            ComponentFields.setParentCoverageComponentCode(input, null);
        }
        else {
            ComponentFields.setParentCoverageComponentCode(input, ComponentFields.getParentCoverageComponentCode(record));
        }
        ComponentFields.setCoverageBaseRecordId(input, CoverageFields.getCoverageBaseRecordId(record));
        if (CoverageFields.hasMainCoverageBaseRecordId(record)) {
            ComponentFields.setMainCoverageBaseRecordId(input, CoverageFields.getMainCoverageBaseRecordId(record));
        }

        if (!hasComponentParent && policyHeader.hasRiskHeader()) {
            // Handle special mapping functionality via the PMDefaultManager
            // but only for default, not dependent components
            getPmDefaultManager().processMappedDefaults("MAP_COVERAGE_COMPONENT", policyHeader, input);
        }

        RecordSet rs = getComponentDAO().loadAllAvailableComponent(input, (RecordLoadProcessor) loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAvailableOrDependentComponent", rs);
        }
        return rs;
    }

    /**
     * Get the default values for new added component(s)
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForAddComponent(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddComponent", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);

        Record returnRecord = new Record();
        ComponentOwner owner = ComponentOwner.getInstance(ComponentFields.getComponentOwner(inputRecord));
        // Firstly, merge default value from web work bench configuration
         Record configuredDefaultValues;
        if(owner.isPolicyOwner()){
            configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_POLICY_ACTION_CLASS_NAME);
        }else{
            configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(CoverageManager.ADD_COVERAGE_ACTION_CLASS_NAME);
        }
        if (configuredDefaultValues != null) {
            returnRecord.setFields(configuredDefaultValues);
        }

        // Secondly, Merge all values of component which come from loadAllAvailableComponent
        String productCovComponentId = ComponentFields.getProductCovComponentId(inputRecord);
        Long lProductCovComponentId = Long.valueOf(productCovComponentId);
        RecordSet availableComps = loadAllAvailableComponent(policyHeader, inputRecord,
            DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
        RecordSet selectedCompRs = availableComps.getSubSet(
            new RecordFilter(ComponentFields.PRODUCT_COV_COMPONENT_ID, lProductCovComponentId));
        if (selectedCompRs.getSize() == 1) {
            returnRecord.setFields(selectedCompRs.getRecord(0));
        }
        else {
            throw new AppException(AppException.UNEXPECTED_ERROR, "the selected available component is not equals to 1");
        }

        // Thirdly, set more default values based on the business rules
        // Set component PK and base record fk
        Record compIdAndBaseIdRec = getComponentDAO().getComponentIdAndBaseId(inputRecord);
        returnRecord.setFields(compIdAndBaseIdRec);

        // Set RenewalB
        YesNoFlag expiryDateB = ComponentFields.getExpiryDateB(returnRecord);
        if (expiryDateB.booleanValue()) {
            ComponentFields.setRenewalB(returnRecord, "N");
        }
        else {
            ComponentFields.setRenewalB(returnRecord, "Y");
        }

        // Set Component Value
        String componentTypeCode = ComponentFields.getComponentTypeCode(returnRecord);
        if (componentTypeCode.equals(ADJUSTMENT)) {
            ComponentFields.setComponentValue(returnRecord, "0");
        }
        else {
            // Skip low/high value setting for cycled or NDD components
            if (!(ComponentFields.getCycledB(returnRecord).booleanValue() ||
                ComponentFields.getCoverageComponentCode(returnRecord).equals(ComponentFields.ComponentCodeValues.NEWDOCTOR))) {
                String sLowValue = ComponentFields.getLowValue(returnRecord);
                String sHighValue = ComponentFields.getHighValue(returnRecord);
                if (sLowValue != null && sHighValue != null) {
                    float lowValue = Float.parseFloat(sLowValue);
                    float highValue = Float.parseFloat(sHighValue);

                    if (lowValue > highValue) {
                        ComponentFields.setComponentValue(returnRecord, sHighValue);
                    }
                    else {
                        ComponentFields.setComponentValue(returnRecord, sLowValue);
                    }
                }else if(sLowValue == null && sHighValue == null){
                      ComponentFields.setComponentValue(returnRecord, null);
                }else if (sLowValue == null){
                    ComponentFields.setComponentValue(returnRecord, sLowValue);
                }else if (sHighValue == null){
                    ComponentFields.setComponentValue(returnRecord, sHighValue);
                }
            }
        }
        //set component sequence No by selected component
        ComponentFields.setSequenceNo(returnRecord, ComponentFields.getComponentSequenceNo(returnRecord));
        //Set component long description
        ComponentFields.setCompLongDescription(returnRecord, ComponentFields.getLongDescription(returnRecord));

        String effecitveDateForAddComp = getEffectiveDateForAddComponent(policyHeader);

        //if owner is tail or prior act
        if (owner.isTailOwner() || owner.isPriorActOwner()) {
            // Set Effective From Date
            ComponentFields.setEffectiveFromDate(returnRecord, CoverageFields.getCoverageBaseEffectiveFromDate(inputRecord));
            // Set Effective To Date
            ComponentFields.setEffectiveToDate(returnRecord, CoverageFields.getCoverageBaseEffectiveToDate(inputRecord));
            //set coverage id
            if (owner.isPriorActOwner()) {
                CoverageFields.setCoverageId(returnRecord, CoverageFields.getCoverageId(inputRecord));
            }

            // Set Coverage_Term_Effective_To_Date
            ComponentFields.setTermEffectiveToDate(returnRecord, CoverageFields.getCoverageBaseEffectiveToDate(inputRecord));

        }
        else {
            // Set Effective From Date
            ComponentFields.setEffectiveFromDate(returnRecord, effecitveDateForAddComp);

            // Set Effective To Date
            if(owner.isPolicyOwner()){
               ComponentFields.setEffectiveToDate(returnRecord, policyHeader.getPolicyExpirationDate());
            }
            else {
                boolean expirationDateCanChange = policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue();
                PMStatusCode covgStatus = CoverageFields.getCoverageStatus(inputRecord);
                ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
                String sCovgBaseEffToDate = CoverageFields.getCoverageBaseEffectiveToDate(inputRecord);
                Date covgBaseEffToDate = DateUtils.parseDate(sCovgBaseEffToDate);
                Date termEffToDate = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());
                String sInitCovgEffToDate = sCovgBaseEffToDate;

                // System should compare the greater date between coverage expiration date and coverage base effective to date.
                if (inputRecord.hasStringValue("latestCoverageEffectiveToDate")) {
                    String sLatestCovgEffToDate = inputRecord.getStringValue("latestCoverageEffectiveToDate");
                    if (DateUtils.parseDate(sLatestCovgEffToDate).after(covgBaseEffToDate)) {
                        sInitCovgEffToDate = sLatestCovgEffToDate;
                    }
                }

                // When adding components for short term coverages, we should find the correct coverage version.
                if (expirationDateCanChange) {
                    ComponentFields.setCheckDt(inputRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                    String sCovgContiguousEffDate = getComponentDAO().getCoverageContiguousEffectiveDate(inputRecord).getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
                    if (sCovgContiguousEffDate == null)
                        sCovgContiguousEffDate = CoverageFields.getCoverageEffectiveFromDate(inputRecord);
                    ComponentFields.setEffectiveFromDate(returnRecord, sCovgContiguousEffDate);

                    String sCovgContiguousExpDate = getComponentDAO().getCoverageExpirationDate(inputRecord);
                    if (sCovgContiguousExpDate == null)
                        sCovgContiguousExpDate = CoverageFields.getCoverageEffectiveToDate(inputRecord);
                    if (DateUtils.parseDate(sCovgContiguousExpDate).after(DateUtils.parseDate(getLatestTermExpDate(policyHeader)))) {
                        sCovgContiguousExpDate = getLatestTermExpDate(policyHeader);
                    }
                    sInitCovgEffToDate = sCovgContiguousExpDate;
                }

                Date initCovgEffToDate = DateUtils.parseDate(sInitCovgEffToDate);
                if (screenModeCode.isCancelWIP()) {
                    ComponentFields.setEffectiveToDate(returnRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                    // Set RenewB to N
                    ComponentFields.setRenewalB(returnRecord, "N");
                }
                else if (screenModeCode.isOosWIP() &&
                    (componentTypeCode.equals(ADJUSTMENT) || !ComponentFields.getPercentValueB(returnRecord).booleanValue() || ComponentFields.getExpiryDateB(returnRecord).booleanValue())) {
                    if ((expirationDateCanChange || covgStatus.isConverted()) && initCovgEffToDate.before(termEffToDate)) {
                        ComponentFields.setEffectiveToDate(returnRecord, sInitCovgEffToDate);
                    }
                    else {
                        ComponentFields.setEffectiveToDate(returnRecord, policyHeader.getTermEffectiveToDate());
                    }
                }
                else {
                    if (covgStatus.isConverted() && initCovgEffToDate.before(termEffToDate)) {
                        ComponentFields.setEffectiveToDate(returnRecord, sInitCovgEffToDate);
                    }
                    else if (expirationDateCanChange) {
                        ComponentFields.setEffectiveToDate(returnRecord, sInitCovgEffToDate);
                    }
                    else {
                        ComponentFields.setEffectiveToDate(returnRecord, getLatestTermExpDate(policyHeader));
                    }
                }
            }
            // Set Coverage_Term_Effective_To_Date
            ComponentFields.setTermEffectiveToDate(returnRecord, policyHeader.getTermEffectiveToDate());
        }

        // Set Coverage Base Record Id
        CoverageFields.setCoverageBaseRecordId(returnRecord, CoverageFields.getCoverageBaseRecordId(inputRecord));

        // Set Cycle Date
        if ((!owner.isPriorActOwner() && ComponentFields.getCycledB(returnRecord).booleanValue()) ||
            ComponentFields.getCoverageComponentCode(returnRecord).equals(ComponentFields.ComponentCodeValues.NEWDOCTOR)) {
            ComponentFields.setComponentCycleDate(returnRecord, effecitveDateForAddComp);
            // Check if the Effective_To_Date is adjusted or not
            Record record = new Record();
            ComponentFields.setProductCovComponentId(record, ComponentFields.getProductCovComponentId(inputRecord));
            ComponentFields.setEffectiveFromDate(record, effecitveDateForAddComp);
            int cycleYears = getCycleYears(record);
            if (cycleYears == 0) {
                // raise a warning
                MessageManager.getInstance().addWarningMessage("pm.addComponent.cycled.component.info",
                    new Object[]{ComponentFields.getShortDescription(returnRecord)});
            }
            else{
                // The cycleYears is a number of years which is added to the year component of the effective_from_date
                // and used to set the effective_to_date.
                String sEffectiveFormDate = ComponentFields.getEffectiveFromDate(returnRecord);
                Date currentEffectiveFormDate = DateUtils.parseDate(sEffectiveFormDate);
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(currentEffectiveFormDate);
                int nYear = calendar.get(Calendar.YEAR);
                int nMonth = calendar.get(Calendar.MONTH) + 1;
                int nDay = calendar.get(Calendar.DATE);
                int nNewYear = nYear + cycleYears;
                sEffectiveFormDate = String.valueOf(nMonth) + "/" + String.valueOf(nDay) + "/" + String.valueOf(nNewYear);

                // Convert it to a date in order to get the proper format
                Date newEffectiveToDate = DateUtils.parseDate(sEffectiveFormDate);
                ComponentFields.setEffectiveToDate(returnRecord, DateUtils.formatDate(newEffectiveToDate));
            }
        }
        else {
            ComponentFields.setComponentCycleDate(returnRecord, null);
        }

        // If Suspension Component
        if (!owner.isPriorActOwner() && !ComponentFields.getAdvanceCmYearB(returnRecord).booleanValue()) {
            RecordSet ownerRecords = new RecordSet();
            ownerRecords.addRecord(inputRecord);
            RecordSet addedComps = loadAllComponent(policyHeader, inputRecord , owner, ownerRecords);
            addedComps = addedComps.getSubSet(new RecordFilter(ComponentFields.COVERAGE_BASE_RECORD_ID, ComponentFields.getCoverageBaseRecordId(inputRecord)));
            String applyRiskSuspendRecordDesc = getApplyRiskSuspendRecordDesc(addedComps, returnRecord,
                ComponentFields.PRODUCT_COV_COMPONENT_ID);
            if (!StringUtils.isBlank(applyRiskSuspendRecordDesc)) {
                MessageManager.getInstance().addInfoMessage("pm.maintainComponent.add.suspension.effectiveToDate.info",
                    new Object[]{applyRiskSuspendRecordDesc});
            }
        }

        // Set some higher level values that may be used for filtering
        ComponentFields.setCompPolicyTypeCode(returnRecord, policyHeader.getPolicyTypeCode());
        ComponentFields.setCompRiskTypeCode(returnRecord, policyHeader.hasRiskHeader() ? policyHeader.getRiskHeader().getRiskTypeCode() : null);
        ComponentFields.setCompProductCoverageCode(returnRecord, CoverageFields.getProductCoverageCode(inputRecord));

        // Merge default values from Pm_Default.Get_Level_Default
        Record defaultLevelValues = getPmDefaultManager().getDefaultLevel(COVG_COMP_TAB,
            policyHeader.getLastTransactionInfo().getTransactionLogId(),
            policyHeader.getTermEffectiveFromDate(), policyHeader.getLastTransactionInfo().getTransEffectiveFromDate(),
            PRODUCT_COVERAGE_CODE, ComponentFields.getCompProductCoverageCode(returnRecord),
            COVERAGE_COMPONENT_CODE, ComponentFields.getCoverageComponentCode(returnRecord),
            null, null);
        returnRecord.setFields(defaultLevelValues);

        // Set original values
        origFieldLoadProcessor.postProcessRecord(returnRecord, true);

        // Set coverage status if owner is coverage
        if (owner.isCoverageOwner()) {
            CoverageFields.setCoverageStatus(returnRecord, CoverageFields.getCoverageStatus(inputRecord));
        }

        // Set the initial Component Entitlement values
        ComponentEntitlementRecordLoadProcessor.setInitialEntitlementValuesForComponent(this, policyHeader, inputRecord,
            policyHeader.getScreenModeCode(), owner, returnRecord);

        // Setup intial row style
        ComponentRowStyleRecordLoadprocessor.setInitialEntitlementValuesForRowStyle(returnRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddComponent", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Validate all components
     *
     * @param policyHeader
     * @param inputRecords
     * @param owner
     * @param ownerRecords
     */
    public void validateAllComponents(PolicyHeader policyHeader,
                                      RecordSet inputRecords,
                                      ComponentOwner owner,
                                      RecordSet ownerRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllComponents",
                new Object[]{policyHeader, inputRecords, owner, ownerRecords});
        }

        if (policyHeader.getLastTransactionInfo().getTransactionCode().isOosEndorsement()) {
            // Pre-Oose Change validations
            PreOoseChangeValidator preOoseChangeValidator = new PreOoseChangeValidator(
                null, "component", ComponentFields.POLICY_COV_COMPONENT_ID, ComponentFields.POL_COV_COMP_BASE_REC_ID);
            preOoseChangeValidator.validate(inputRecords);
        }

        // Fill the required owner fields into component RecordSet by the owner type
        RecordSet rs = fillOwnerFieldsToComponentByOwnerType(inputRecords, owner, ownerRecords);

        // Get Cancel WIP Rule
        Record cancelWipRec = getCancelWipRule(policyHeader);
        int cancelWipRule = cancelWipRec.getIntegerValue("returnValue").intValue();
        // Get Cancel WIP Mode
        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        boolean isCancelWip = screenModeCode.isCancelWIP();

        // Get an instance of the Standard Effective To Date Rule Validator
        StandardEffectiveToDateRecordValidator effToDateValidator =
            new StandardEffectiveToDateRecordValidator(policyHeader,
                ComponentFields.EFFECTIVE_FROM_DATE, ComponentFields.EFFECTIVE_TO_DATE,
                ComponentFields.POLICY_COV_COMPONENT_ID);

        // To get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = rs.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(r);

            String productCovgDesc = null;
            if (owner.isCoverageOwner() || owner.isPolicyOwner()) {
                productCovgDesc = CoverageFields.getProductCoverageDesc(r);
            }
            else if (owner.isTailOwner()) {
                productCovgDesc = r.getStringValue(TailFields.COVERAGE_LIMIT_CODE + "LOVLABEL");
            }
            else if (owner.isPriorActOwner()) {
                productCovgDesc = policyHeader.getCoverageHeader().getCoverageName();
            }
            String rowNum = String.valueOf(Integer.parseInt(r.getStringValue(ROW_NUM)) + 1);
            String rowId = ComponentFields.getPolicyCovComponentId(r);
            if (owner.isCoverageOwner() || owner.isTailOwner() || owner.isPriorActOwner()) {
                rowId = CoverageFields.getCoverageId(r) + "," + rowId;
            }
            // Component Effective To Date validations
            // Validation #1: Only Effective To Date or Component Value can change.
            // Validation #12: Renew checkbox validation
            String componentValueOrg = ComponentFields.getOrigComponentValue(r);
            String coverageComponentDesc = ComponentFields.getShortDescription(r);
            componentValueOrg = componentValueOrg == null ? "" : componentValueOrg;
            String effectiveToDateOrg = ComponentFields.getOrigEffectiveToDate(r);
            // If current component is cycled or NDD components, component value should be "".
            // If it is not cycled or NDD components and it is null value, we should set it to "0".
            String componentValue = ComponentFields.getComponentValue(r);
            if (ComponentFields.getCycledB(r).booleanValue() ||
                ComponentFields.getCoverageComponentCode(r).equals(ComponentFields.ComponentCodeValues.NEWDOCTOR)) {
                componentValue = "";
            }
            else {
                componentValue = componentValue == null ? (recordModeCode.isRequest() ? "" : "0") : componentValue;
            }
            ComponentFields.setComponentValue(r, componentValue);
            String effectiveToDate = ComponentFields.getEffectiveToDate(r);
            String renewalB = ComponentFields.getRenewalB(r);
            String renewalBOrg = ComponentFields.getOrigRenewalB(r);
            String componentCycleDate = ComponentFields.getComponentCycleDate(r);
            String componentCycleDateOrg = ComponentFields.getComponentCycleDateOrg(r);
            String officialRecordId = ComponentFields.getOfficialRecordId(r);
            if (owner.isPriorActOwner()) {
                if (!r.hasStringValue(ComponentFields.COMPONENT_VALUE) &&
                    (r.hasStringValue(ComponentFields.HIGH_VALUE )|| r.hasStringValue(ComponentFields.LOW_VALUE))) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.componentValueRequired.error",
                        new String[]{rowNum}, ComponentFields.COMPONENT_VALUE, rowId);
                }

                if (ComponentFields.getCoverageComponentCode(r).equals(ComponentFields.ComponentCodeValues.NEWDOCTOR) &&
                    !r.hasStringValue(ComponentFields.COMPONENT_CYCLE_DATE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainPriorActs.cycleBeginDateRequired.error",
                        new String[]{rowNum}, ComponentFields.COMPONENT_CYCLE_DATE, rowId);
                }
            }
            else {
                if (StringUtils.isBlank(effectiveToDate)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainComponent.effectiveToDate.rule9.error",
                        new String[]{rowNum, productCovgDesc, coverageComponentDesc}, ComponentFields.EFFECTIVE_TO_DATE, rowId);
                    break; // no need to continue...
                }
                // if componentValueOrg and effectiveToDateOrg are all null, allow change them together.(OOSE component)
                // if current record's official record id is null and record mode code is TEMP which means current
                // record is a new added one, the validation should be skipped.
                if (!(StringUtils.isBlank(officialRecordId) && recordModeCode.isTemp())) {
                    String componentValueCompare = componentValueOrg;
                    String effectiveToDateCompare = effectiveToDateOrg;
                    String renewalBCompare = renewalBOrg;
                    if (recordModeCode.isTemp()) {
                        Iterator offIt = rs.getRecords();
                        while (offIt.hasNext()) {
                            Record offR = (Record) offIt.next();
                            if (ComponentFields.getPolicyCovComponentId(offR).equals(officialRecordId)) {
                                componentValueCompare = ComponentFields.getComponentValue(offR);
                                effectiveToDateCompare = ComponentFields.getEffectiveToDate(offR);
                                renewalBCompare = ComponentFields.getRenewalB(offR);
                                break;
                            }
                        }
                    }

					componentValueCompare = componentValueCompare == null ? "" : componentValueCompare;
					if (!(componentValueOrg.equals("") && effectiveToDateOrg == null) &&
							!componentValueCompare.equals(componentValue) && !StringUtils.isBlank(effectiveToDate) &&
							(!effectiveToDate.equals(effectiveToDateCompare) || !renewalB.equals(renewalBCompare)) ) {
						// Set back the original value
						if (renewalB != null && !renewalB.equals(renewalBOrg)) {
							ComponentFields.setRenewalB(r, renewalBOrg);
						}
						MessageManager.getInstance().addErrorMessage("pm.maintainComponent.effectiveToDate.rule1.error",
							new String[]{rowNum, productCovgDesc, coverageComponentDesc}, ComponentFields.EFFECTIVE_TO_DATE, rowId);
					}
                }

                // Validation #2: Standard Effective To Date Rule
                // If change component date(OOSE), copy component effective from date to the record
                if (recordModeCode.isRequest()) {
                    copyOoseComponentEffFromDateBeforeValidation(inputRecords, r);
                }
                if (owner.isCoverageOwner()) {
                    // If it is percentage component and expiration date is greater than current term expiration date,
                    // use current term expiration date as component's effective date.
                    // This is to fix issue#92182
                    String origEffToDate = ComponentFields.getEffectiveToDate(r);
                    String sTermEffToDate = policyHeader.getTermEffectiveToDate();
                    if (ComponentFields.getPercentValueB(r).booleanValue() &&
                        DateUtils.parseDate(origEffToDate).after(DateUtils.parseDate(sTermEffToDate))) {
                        ComponentFields.setEffectiveToDate(r, sTermEffToDate);
                        effToDateValidator.validate(r);
                        // change it back
                        ComponentFields.setEffectiveToDate(r, origEffToDate);
                    }
                    else {
                        effToDateValidator.validate(r);
                    }
                }
                // If change component date(OOSE), clear component effective from date after the validation
                if (recordModeCode.isRequest()) {
                    clearOoseComponentEffFromDateAfterValidation(r);
                }

                Date expDate = DateUtils.parseDate(effectiveToDate);
                String sTermEffToDate = policyHeader.getTermEffectiveToDate();
                Date termEffToDate = DateUtils.parseDate(sTermEffToDate);
                String sTranEffFromDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
                Date tranEffFromDate = DateUtils.parseDate(sTranEffFromDate);

                // Removed the below validation per issue 73084
                // Validation #3: Effective To Date must be after the transaction effective date and the Record_Mode_Code is OFFICIAL
/*            if (expDate.before(tranEffFromDate) && recordModeCode.isOfficial()
                && !effectiveToDate.equals(effectiveToDateOrg)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainComponent.effectiveToDate.rule4.error",
                    new String[]{rowNum, productCovgCode, coverageComponentDesc});
            }*/

                // Validation #4: if NOT (in CANCEL WIP mode and the Cancel WIP Rule is set to 1),
                // the Effective To Date cannot be changed prior than the transaction effective date.
                if (!(isCancelWip && cancelWipRule == 1)) {
                    if (expDate.before(tranEffFromDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainComponent.effectiveToDate.rule5.error",
                            new String[]{rowNum, productCovgDesc, coverageComponentDesc}, ComponentFields.EFFECTIVE_TO_DATE, rowId);
                    }
                }

                // Validation #5: if in CANCEL WIP mode and the Cancel WIP Rule is set to 1,
                // the Effective To Date cannot be set to be after the transaction effective date.
                if (!owner.isTailOwner() && isCancelWip && cancelWipRule == 1) {
                    if (expDate.after(tranEffFromDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainComponent.effectiveToDate.rule6.error",
                            new String[]{rowNum, productCovgDesc, coverageComponentDesc}, ComponentFields.EFFECTIVE_TO_DATE, rowId);
                    }
                }


                if (owner.isCoverageOwner()) {
                    // Validation #6: if it is a flat dollar component, the Effective To Date cannot exceed the current term expiration date.
                    if (!ComponentFields.getPercentValueB(r).booleanValue()) {
                        if (expDate.after(termEffToDate)) {
                            MessageManager.getInstance().addErrorMessage("pm.maintainComponent.effectiveToDate.rule7.error",
                                new String[]{rowNum, productCovgDesc, coverageComponentDesc}, ComponentFields.EFFECTIVE_TO_DATE, rowId);
                        }
                    }
                    // Validation #7: if Change Risk/Coverage Expiration Date is allowed,
                    // the component Effecitve To Date cannot exceed the coverage's base record effective to date.

                    if (policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue()) {
                        Record record = new Record();
                        CoverageFields.setCoverageBaseRecordId(record, CoverageFields.getCoverageBaseRecordId(r));
                        CoverageFields.setCoverageEffectiveFromDate(record, CoverageFields.getCoverageEffectiveFromDate(r));
                        String sContiguousDate = getComponentDAO().getCoverageExpirationDate(record);
                        Date coverageContiguousDate = DateUtils.parseDate(sContiguousDate);
                        if (coverageContiguousDate.after(DateUtils.parseDate(getLatestTermExpDate(policyHeader)))) {
                            coverageContiguousDate = DateUtils.parseDate(getLatestTermExpDate(policyHeader));
                        }
                        if (expDate.after(coverageContiguousDate)) {
                            MessageManager.getInstance().addErrorMessage("pm.maintainComponent.effectiveToDate.rule8.error",
                                new String[]{rowNum, productCovgDesc, coverageComponentDesc}, ComponentFields.EFFECTIVE_TO_DATE, rowId);
                        }
                    }
                }

                // Validation #8: Suspension Component Effective To Date validations
                if (!ComponentFields.getAdvanceCmYearB(r).booleanValue()) { // if suspension component
                    // Get all the added components have the same coverage base record id.
                    RecordSet addedComps = rs.getSubSet(new RecordFilter(ComponentFields.COVERAGE_BASE_RECORD_ID, ComponentFields.getCoverageBaseRecordId(r)));
                    String applyRiskSuspendRecordDesc = getApplyRiskSuspendRecordDesc(addedComps, r, ComponentFields.POLICY_COV_COMPONENT_ID);
                    if (!applyRiskSuspendRecordDesc.equals("")) {
                        MessageManager.getInstance().addInfoMessage("pm.maintainComponent.suspension.effectiveToDate.info",
                            new Object[]{rowNum, applyRiskSuspendRecordDesc, coverageComponentDesc});
                    }
                }

                if (ComponentFields.getCoverageComponentCode(r).equals(ComponentFields.ComponentCodeValues.NEWDOCTOR) ||
                    ComponentFields.getCycledB(r).booleanValue()) { // if New Doctor component or Cycled component

                    PolicyCacheManager policyCacheManager = PolicyCacheManager.getInstance();
                    if (!policyCacheManager.hasNddValConfigured()) {
                        policyCacheManager.setNddValConfigured(YesNoFlag.getInstance(getTransactionDAO().isSkipNddValidationConfigured()).booleanValue());
                    }
                    boolean isSkipNddValB = false;
                    if (policyCacheManager.getNddValConfigured()) {
                        ComponentFields.setTermEffectiveFromDate(r, policyHeader.getTermEffectiveFromDate());
                        isSkipNddValB = getComponentDAO().getNddSkipValidateB(r);
                    }

                    // Validation #9: New Doctor Component Effective To Date validations
                    if (!effectiveToDate.equals(effectiveToDateOrg)) { // Validate it only if the date is changed.
                        if (!isSkipNddValB) {
                            validateCycleYears(r, rowId);
                        }
                    }

                    // Validation #10: New Doctor Cycle Date validations
                    if (!StringUtils.isBlank(componentCycleDate) && !componentCycleDate.equals(componentCycleDateOrg)) {
                        // Validate it only if the date is not empty and is changed.
                        validateNumDays(r, rowId, isSkipNddValB);
                    }
                }

                // Validatoin #11: Component value validations
                // If OOSE, doesn't need to do this validaton.
                if (!recordModeCode.isRequest() &&
                    (!(ComponentFields.getCoverageComponentCode(r).equals(ComponentFields.ComponentCodeValues.NEWDOCTOR) ||
                        ComponentFields.getCycledB(r).booleanValue()))) {
                    if (componentValue == null || componentValue.equals("")) {
                        // Set back the original value
                        ComponentFields.setComponentValue(r, componentValueOrg);
                        MessageManager.getInstance().addErrorMessage("pm.maintainComponent.componentValue.null.error",
                            new String[]{rowNum, productCovgDesc, coverageComponentDesc},
                            ComponentFields.COMPONENT_VALUE, rowId);
                    }
                }
            }

            // If OOSE, after click Change Date, the component value is empty and disabled, system should skip to
            // validate component value and system should validate change date logic.
            boolean validateComponentValue = true;
            boolean validateComponentChangeDate = false;
            if(recordModeCode.isRequest() && r.hasStringValue("isCompValueEditable") &&
                !YesNoFlag.getInstance(r.getStringValue("isCompValueEditable")).booleanValue()){
                validateComponentValue = false;
                validateComponentChangeDate = true;
            }

            // validate the range of component value
            if (validateComponentValue) {
                boolean isOutOfRange = validateCompValueRange(r);
                if (isOutOfRange) {
                    if (owner.isCoverageOwner()) {
                        // Set back the original value
                        ComponentFields.setComponentValue(r, componentValueOrg);
                    }

                    String sHighValue = ComponentFields.getHighValue(r);
                    String sLowValue = ComponentFields.getLowValue(r);
                    MessageManager.getInstance().addErrorMessage("pm.maintainComponent.componentValue.outofRange.error",
                        new Object[]{rowNum, productCovgDesc, coverageComponentDesc, sLowValue, sHighValue},
                        ComponentFields.COMPONENT_VALUE, rowId);
                }
            }

            // Check if component expiring date can be changed in OOSE.
            if (validateComponentChangeDate) {
                boolean isOoseChangeDateAllowed = validateCompChangeDateAllowed(r);
                if (!isOoseChangeDateAllowed) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainComponent.oose.changeDate.effectiveToDate.error",
                        new Object[]{rowNum, productCovgDesc, coverageComponentDesc, effectiveToDate},
                        ComponentFields.EFFECTIVE_TO_DATE, rowId);
                }
            }

            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Component data.");
        }

        l.exiting(getClass().getName(), "validateAllComponents");
    }

    /**
     * Fill several needed component's owner fields into component Record which will be used by validations.
     *
     * @param compRecords
     * @param owner
     * @param ownerRecords
     * @return
     */
    private RecordSet fillOwnerFieldsToComponentByOwnerType(RecordSet compRecords,
                                                            ComponentOwner owner,
                                                            RecordSet ownerRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "fillOwnerFieldsToComponentByOwnerType",
                new Object[]{compRecords, owner, ownerRecords});
        }

        RecordSet newRecords = new RecordSet();

        if (owner.isPolicyOwner()) {
            compRecords.setFieldValueOnAll(ROW_NUM, "0");
            int compRecSize = compRecords.getSize();
            int rowNum = 0;
            for (int i = 0; i < compRecSize; i++) {
                Record compRec = compRecords.getRecord(i);
                compRec.setFieldValue(ROW_NUM, String.valueOf(rowNum));
                compRec.setFieldValue(CoverageFields.PRODUCT_COVERAGE_DESC, POLICY);
                rowNum++;
                newRecords.addRecord(compRec);
            }
        }
        else if (owner.isCoverageOwner()) {
            int compRecSize = compRecords.getSize();
            int ownerRecSize = ownerRecords.getSize();
            String compIdStr = "";
            for (int j = 0; j < ownerRecSize; j++) {
                Record ownerRec = ownerRecords.getRecord(j);

                // Skip to be closed OFFICIAL record
                if (PMCommonFields.getRecordModeCode(ownerRec).isOfficial() &&
                    !StringUtils.isBlank(ownerRec.getStringValue("closingTransLogId"))) {
                    continue;
                }

                String ownerCovgBaseRecId = CoverageFields.getCoverageBaseRecordId(ownerRec);
                int rowNum = 0;
                for (int i = 0; i < compRecSize; i++) {
                    Record compRec = compRecords.getRecord(i);
                    String compCovgBaseRecId = CoverageFields.getCoverageBaseRecordId(compRec);
                    if (compCovgBaseRecId.equals(ownerCovgBaseRecId)) {
                        // Set coverage ID
                        CoverageFields.setCoverageId(compRec, CoverageFields.getCoverageId(ownerRec));
                        // Set coverage record's effective from date into component record
                        CoverageFields.setCoverageEffectiveFromDate(compRec, CoverageFields.getCoverageEffectiveFromDate(ownerRec));
                        // Set coverage base record's effective from date into component record
                        CoverageFields.setCoverageBaseEffectiveToDate(compRec,
                            CoverageFields.getCoverageBaseEffectiveToDate(ownerRec));
                        // Set product coverage code lov label
                        compRec.setFieldValue(CoverageFields.PRODUCT_COVERAGE_DESC,
                            COVERAGE + CoverageFields.getProductCoverageDesc(ownerRec));
                        // Set row No
                        compRec.setFieldValue(ROW_NUM, String.valueOf(rowNum));
                        rowNum++;
                        String polCovCompId = ComponentFields.getPolicyCovComponentId(compRec);
                        if (newRecords.getSize() == 0) {
                            compIdStr += polCovCompId + ",";
                            newRecords.addRecord(compRec);
                        }
                        else {
                            if (compIdStr.indexOf(polCovCompId) >= 0) {
                                continue;
                            }
                            else {
                                compIdStr += polCovCompId + ",";
                                newRecords.addRecord(compRec);
                            }
                        }
                    }
                }
            }
        }
        else if (owner.isTailOwner()) {

            int compRecSize = compRecords.getSize();
            int ownerRecSize = ownerRecords.getSize();
            String covgCodeLovLabel = TailFields.COVERAGE_LIMIT_CODE + "LOVLABEL";
            compRecords.setFieldValueOnAll(ROW_NUM, "0");
            compRecords.setFieldValueOnAll(covgCodeLovLabel, "");
            compRecords.setFieldValueOnAll(CoverageFields.COVERAGE_BASE_EFFECTIVE_TO_DATE, "");

            for (int j = 0; j < ownerRecSize; j++) {
                Record ownerRec = ownerRecords.getRecord(j);

                // Skip to be closed OFFICIAL record
                if (PMCommonFields.getRecordModeCode(ownerRec).isOfficial() && ownerRec.hasStringValue("closingTransLogId")) {
                    continue;
                }

                String ownerCovgBaseRecId = TailFields.getTailCovBaseRecordId(ownerRec);
                int rowNum = 0;
                for (int i = 0; i < compRecSize; i++) {
                    Record compRec = compRecords.getRecord(i);

                    String compCovgBaseRecId = CoverageFields.getCoverageBaseRecordId(compRec);
                    if (compCovgBaseRecId.equals(ownerCovgBaseRecId)) {
                        // Set coverage ID
                        CoverageFields.setCoverageId(compRec, TailFields.getTailCovBaseRecordId(ownerRec));
                        // Set coverage base record's effective from date into component record
                        CoverageFields.setCoverageBaseEffectiveToDate(compRec,
                            TailFields.getEffectiveToDate(ownerRec));
                        // Set product coverage code lov label
                        compRec.setFieldValue(covgCodeLovLabel, ownerRec.getStringValue(covgCodeLovLabel));
                        // Set row No
                        compRec.setFieldValue(ROW_NUM, String.valueOf(rowNum));
                        rowNum++;

                        newRecords = compRecords;
                    }
                }
            }
        }
        else if (owner.isPriorActOwner()) {
            compRecords = compRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}))
                .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));
            newRecords = compRecords;
            int compRecSize = compRecords.getSize();
            int ownerRecSize = ownerRecords.getSize();
            String covgCodeLovLabel = TailFields.COVERAGE_LIMIT_CODE + "LOVLABEL";
            compRecords.setFieldValueOnAll(ROW_NUM, "0");

            for (int j = 0; j < ownerRecSize; j++) {
                Record ownerRec = ownerRecords.getRecord(j);
                String ownerCovgId = CoverageFields.getCoverageId(ownerRec);
                int rowNum = 0;
                for (int i = 0; i < compRecSize; i++) {
                    Record compRec = compRecords.getRecord(i);

                    String compCovgId = CoverageFields.getCoverageId(compRec);
                    String compId = ComponentFields.getPolicyCovComponentId(compRec);
                    if (compCovgId!=null && compCovgId.equals(ownerCovgId)) {
                        String rowId = ownerCovgId + "," + compId;
                        // Set row No/Row ID
                        compRec.setFieldValue(ROW_NUM, String.valueOf(rowNum));
                        compRec.setFieldValue(ROW_ID, rowId);
                        rowNum++;
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "fillOwnerFieldsToComponentByOwnerType", newRecords);
        }
        return newRecords;
    }

    /**
     * Get the comma-delimited list of components that apply during the suspension period.
     *
     * @param changedRecords all inserted and updated records
     * @param suspendRecord  a suspension component record
     * @param pkFieldName    the PK field name of the record
     * @return the comma-delimited list of components that apply during the suspension period
     */
    private String getApplyRiskSuspendRecordDesc(RecordSet changedRecords, Record suspendRecord, String pkFieldName) {
        String descriptions = "";
        Iterator recIter = changedRecords.getRecords();
        while (recIter.hasNext()) {
            Record rec = (Record) recIter.next();
            // Exclude the current selected ccomponent
            if (!rec.getStringValue(pkFieldName).equals(suspendRecord.getStringValue(pkFieldName))) {
//            if (!ComponentFields.getPolicyCovComponentId(rec).equals(ComponentFields.getPolicyCovComponentId(suspendRecord))) {
                // If exists other components that apply during the suspension period
                if (ComponentFields.getApplyRiskSuspendB(rec).booleanValue()) {
                    descriptions += ComponentFields.getShortDescription(rec) + ",";
                }
            }
        }
        if (descriptions.length() > 0) {
            descriptions = descriptions.substring(0, descriptions.length() - 1);
        }
        return descriptions;
    }

    /**
     * Load Cyecle Detail
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCycleDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCycleDetail", new Object[]{inputRecord});
        }

        RecordSet rs;

        // Alternate Flow : Cycle Date Missing
        if (StringUtils.isBlank(inputRecord.getStringValue("componentCycleDate"))) {
            MessageManager.getInstance().addErrorMessage("pm.cycleDetail.cycleDate.missing",
                ComponentFields.COMPONENT_CYCLE_DATE);
            rs = new RecordSet();
        }
        else {
            // For policy level component, riskBaseRecordId is unnecessary.
            if (inputRecord.hasStringValue("componentOwner") && inputRecord.getStringValue("componentOwner").equals("POLICY")) {
                inputRecord.setFieldValue("riskBaseRecordId", null);
            }
            rs = getComponentDAO().loadAllCycleDetail(inputRecord);
            if (rs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.cycleDetail.nodata.error");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCycleDetail", rs);
        }

        return rs;
    }

    /**
     * Load Surcharge Points
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllSurchargePoint(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSurchargePoint", new Object[]{inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);

        // If in Official mode, reset transactionLogId to 0
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
            inputRecord.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, "0");
        }

        // Call the DAO to do the retrieval
        RecordSet rs = getComponentDAO().loadAllSurchargePoint(inputRecord);

        // Add coverageBaseRecordId to summary record for publish out
        rs.getSummaryRecord().setFieldValue(
            "coverageBaseRecordId", inputRecord.getStringValue("coverageBaseRecordId"));

        // Set page entitlement
        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainSurchargePoints.nodata.error");
            rs.getSummaryRecord().setFieldValue("isButtonSaveAvailable", "N");
        }
        else if (screenModeCode.isRenewWIP() || screenModeCode.isWIP() || screenModeCode.isManualEntry()) {
            rs.getSummaryRecord().setFieldValue("isButtonSaveAvailable", "Y");
        }
        else {
            rs.getSummaryRecord().setFieldValue("isButtonSaveAvailable", "N");
        }
        // Issue 103793, if the PM_CUST_SURCG_POINTS is 'Y', the Save button should be hidden,
        // else the button is available if there is a WIP transaction and policy view mode is not OFFICIAL.
        String sysParameter = SysParmProvider.getInstance().getSysParm("PM_CUST_SURCG_POINTS", "N");
        if (YesNoFlag.getInstance(sysParameter).booleanValue()) {
            rs.getSummaryRecord().setFieldValue("isButtonSaveAvailable", "N");
        }
        else if (rs.getSize() > 0 && policyHeader.isWipB() && !policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
            rs.getSummaryRecord().setFieldValue("isButtonSaveAvailable", "Y");
        }
        else {
            rs.getSummaryRecord().setFieldValue("isButtonSaveAvailable", "N");
        }
       
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSurchargePoint", rs);
        }

        return rs;
    }

    /**
     * Save all surcharge point information
     *
     * @param inputRecords a set of Records, each with the updated special handling info
     * @return the number of rows updated
     */
    public int saveAllSurchargePoint(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSurchargePoint", new Object[]{inputRecords});

        int updateCount = 0;

        // Create an new RecordSet to include  modified records
        RecordSet allRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter
            (new String[]{UpdateIndicator.UPDATED}));

        if (allRecords.getSize() > 0) {
            /* validate the input records prior save them */
            validateAllSurchargePoint(inputRecords);

            /*set RowStatus On ModifiedRecords*/
            PMRecordSetHelper.setRowStatusOnModifiedRecords(allRecords);

            /* Call DAO method to update records in batch mode */
            updateCount = getComponentDAO().saveAllSurchargePoint(allRecords);
        }

        l.exiting(getClass().getName(), "saveAllSurchargePoint", new Integer(updateCount));
        return updateCount;
    }

    /**
     * validate All Surcharge point
     *
     * @param inputRecords
     */
    protected void validateAllSurchargePoint(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllSurchargePoint", new Object[]{inputRecords});
        }

        //get validate recordset from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.UPDATED}));

        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String sOverridePoint = r.getStringValue("overridePoints");
            if ((!StringUtils.isBlank(sOverridePoint)) && (!FormatUtils.isLong(sOverridePoint))) {
                MessageManager.getInstance().addErrorMessage("pm.surchargePoints.overridePoints.error",
                    new Object[]{String.valueOf(Integer.parseInt(r.getStringValue(ROW_NUM)) + 1)},
                    "overridePoints", r.getStringValue("pmComponentPointOverrideId"));
            }

            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Invalid Surcharge Points data.");

        l.exiting(getClass().getName(), "validateAllSurchargePoint");
    }

    /**
     * Save all default component
     *
     * @param policyHeader
     * @return update count
     */
    public int saveAllDefaultComponent(PolicyHeader policyHeader, Record record, ComponentOwner owner) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllDefaultComponent", new Object[]{policyHeader});

        // Get all avaiable compoents
        RecordSet rsComponent = loadAllAvailableComponent(policyHeader, record);

        // Get default Components
        RecordSet defaultComponents = new RecordSet();

        int count = rsComponent.getSize();
        for (int i = 0; i < count; i++) {
            Record componentRecord = rsComponent.getRecord(i);
            if (componentRecord.getBooleanValue("defaultComponentB").booleanValue() ||
                componentRecord.getBooleanValue("mapDefaultCoverageCompB").booleanValue()) {

                // Add componentId
                ComponentFields.setProductCovComponentId(
                    record, ComponentFields.getProductCovComponentId(componentRecord));
                ComponentFields.setComponentOwner(record, owner.getOwnerName());
                Record defaultComponent = getInitialValuesForAddComponent(policyHeader, record);
                defaultComponents.addRecord(defaultComponent);
            }
        }

        // Add the PolicyHeader info to each component detail Record
        defaultComponents.setFieldsOnAll(policyHeader.toRecord(), false);

        // Set the current transaction id on all records
        defaultComponents.setFieldValueOnAll("transactionLogId", policyHeader.getLastTransactionId());

        // Add the inserted WIP records in batch mode
        defaultComponents.setFieldValueOnAll("rowStatus", "NEW");
        int updateCount = getComponentDAO().addAllComponents(defaultComponents);

        l.exiting(getClass().getName(), "saveAllDefaultComponent", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Get initial values for OOSE component
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values
     */
    public Record getInitialValuesForOoseComponent(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForOoseComponent", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();

        ComponentOwner owner = ComponentOwner.getInstance(ComponentFields.getComponentOwner(inputRecord));

        // Set Default values
        // componentId
        Long lCompId = getDbUtilityManager().getNextSequenceNo();
        ComponentFields.setPolicyCovComponentId(outputRecord, String.valueOf(lCompId.longValue()));
        // officialRecordId
        CoverageFields.setOfficialRecordId(outputRecord, "0");
        // recordModeCode
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.REQUEST);
        // afterImageRecordB
        CoverageFields.setAfterImageRecordB(outputRecord, YesNoFlag.Y);
        // componentEffectiveFromDate
        String compEffFromDate = null;
        String changeType = inputRecord.getStringValue("changeType");
        if (changeType.equals("chgCompValue")) {
            compEffFromDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        }
        else if (changeType.equals("chgCompDate")) {
            compEffFromDate = ComponentFields.getEffectiveFromDate(inputRecord);
        }
        ComponentFields.setEffectiveFromDate(outputRecord, compEffFromDate);
        // componentEffectiveToDate
        String compEffToDate = null;
        String compTypeCode = ComponentFields.getComponentTypeCode(inputRecord);
        if ("ADJUSTMENT".equals(compTypeCode)) {
            compEffToDate = policyHeader.getTermEffectiveToDate();
        }
        ComponentFields.setEffectiveToDate(outputRecord, compEffToDate);

        // Set Change option to invisible for the new outputRecord
        outputRecord.setFieldValue("isChgCompValueAvailable", YesNoFlag.N);
        outputRecord.setFieldValue("isChgCompDateAvailable", YesNoFlag.N);

        // set inputRecords into outputRecord
        outputRecord.setFields(inputRecord, false);

        // Set coverage status if owner is coverage
        if (owner.isCoverageOwner()) {
            CoverageFields.setCoverageStatus(outputRecord, CoverageFields.getCoverageStatus(inputRecord));
        }

        // Get the default component entitlement values
        ComponentEntitlementRecordLoadProcessor.setInitialEntitlementValuesForComponent(this, policyHeader, inputRecord,
            policyHeader.getScreenModeCode(), owner, outputRecord);

        // set origEffectiveFromDate, origComponentValue and componentValue to null
        ComponentFields.setOrigEffectiveToDate(outputRecord, null);
        ComponentFields.setOrigComponentValue(outputRecord, null);
        ComponentFields.setComponentValue(outputRecord, null);
        ComponentFields.setCompNum1(outputRecord, null);
        ComponentFields.setCompNum2(outputRecord, null);
        ComponentFields.setCompNum3(outputRecord, null);
        ComponentFields.setIncValue(outputRecord, null);
        ComponentFields.setAggValue(outputRecord, null);
        ComponentFields.setClassificationCode(outputRecord, null);
        ComponentFields.setCompChar1(outputRecord, null);
        ComponentFields.setCompChar2(outputRecord, null);
        ComponentFields.setCompChar3(outputRecord, null);
        ComponentFields.setCompDate1(outputRecord, null);
        ComponentFields.setCompDate2(outputRecord, null);
        ComponentFields.setCompDate3(outputRecord, null);
        ComponentFields.setCompNote(outputRecord, null);

        // Setup componentValue/componentEffFromDate/componentEffToDate fields diable or enable according with changeType
        if (changeType.equals("chgCompValue")) {
            // enable componentValue and componentEffToDate fields
            outputRecord.setFieldValue("isCompValueEditable", YesNoFlag.Y);
            outputRecord.setFieldValue("isCompEffectiveToDateEditable", YesNoFlag.Y);
        }
        else if (changeType.equals("chgCompDate")) {
            // disable componentValue field
            outputRecord.setFieldValue("isCompValueEditable", YesNoFlag.N);
            outputRecord.setFieldValue("isDataEditable", YesNoFlag.N);
            // enable componentEffToDate field
            outputRecord.setFieldValue("isCompEffectiveToDateEditable", YesNoFlag.Y);
        }

        // Setup intial row style
        ComponentRowStyleRecordLoadprocessor.setInitialEntitlementValuesForRowStyle(outputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForOoseComponent", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Covg_Component stored procedure.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a record loaded with user entered data
     * @return the number of rows updated.
     */
    public int deleteAllComponents(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllComponents",
            new Object[]{policyHeader, inputRecords});

        inputRecords.setFieldsOnAll(policyHeader.toRecord(), false);
        int deleteCount = getComponentDAO().deleteAllComponents(inputRecords);

        l.exiting(getClass().getName(), "deleteAllComponents", new Integer(deleteCount));
        return deleteCount;
    }

    /**
     * Determines if fields could be editable in cancel wip.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return boolean.
     */
    public YesNoFlag isCancWipEdit(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "isCancWipEdit", new Object[]{policyHeader});

        YesNoFlag isEdit = YesNoFlag.N;
        if (policyHeader.hasCancWipEditCache()) {
            isEdit = (Integer.valueOf(policyHeader.getCancWipEditCache()).intValue() == 1) ? YesNoFlag.Y : YesNoFlag.N;
        }
        else {
            boolean isCancWip = getTransactionDAO().isCancelWipEditable(policyHeader);
            policyHeader.setCancWipEditCache( (isCancWip ? "1" : "0") );
            if (isCancWip) {
            isEdit = YesNoFlag.Y;
        }
        }

        l.exiting(getClass().getName(), "isCancWipEdit", isEdit);
        return isEdit;
    }

    /**
     * validate component copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllComponent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllComponent", new Object[]{inputRecord});
        }

        if (inputRecord.hasStringValue(ComponentFields.COMPONENT_VALUE)) {
            inputRecord.setFieldValue("compCopyB", YesNoFlag.Y);
        }
        String valStatus = getComponentDAO().validateCopyAllComponent(inputRecord);

        // Validate the range of component value
        boolean isOutOfRange = validateCompValueRange(inputRecord);
        if (isOutOfRange) {
            String sHighValue = ComponentFields.getHighValue(inputRecord);
            String sLowValue = ComponentFields.getLowValue(inputRecord);
            String rowId = ComponentFields.getPolicyCovComponentId(inputRecord);
            MessageManager.getInstance().addErrorMessage("pm.maintainRiskCopy.compValue.outOfRange.error",
                    new Object[]{sLowValue, sHighValue},
                    ComponentFields.COMPONENT_VALUE, rowId);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllComponent", valStatus);
        }

        return valStatus;
    }

    /**
     * delete all copied component
     *
     * @param policyHeader
     * @param inputRecord
     * @param compRs
     */
    public void deleteAllCopiedComponent(PolicyHeader policyHeader, Record inputRecord, RecordSet compRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllCopiedComponent", new Object[]{policyHeader, inputRecord, compRs});
        }
        compRs.setFieldsOnAll(policyHeader.toRecord(), false);
        getComponentDAO().deleteAllCopiedComponent(compRs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllCopiedComponent");
        }
    }

    /**
     * Load all processing event.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessingEvent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcessingEvent", inputRecord);
        }
        RecordLoadProcessor entitlementRLP = new RmComponentEntitlementRecordLoadProcessor();
        RecordSet rs = getComponentDAO().loadAllProcessingEvent(inputRecord, entitlementRLP);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcessingEvent", rs);
        }
        return rs;
    }

    /**
     * Load all processing detail.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessingDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcessingDetail", inputRecord);
        }
        RecordSet rs = getComponentDAO().loadAllProcessingDetail(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcessingDetail", rs);
        }
        return rs;
    }

    /**
     * Save all processing event.
     *
     * @param inputRecords
     * @return the number of row updated
     */
    public int performProcessingEvent(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performProcessingEvent", inputRecords);
        }
        // Set the rowStatus to all records.
        RecordSet changedEvent = PMRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);
        int updateCount = 0;
        if (changedEvent.getSize() > 0) {
            validateProcessingEvent(changedEvent);
            updateCount = getComponentDAO().saveAllProcessingEvent(changedEvent);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performProcessingEvent", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Set RMT Classification indicator.
     *
     * @param inputRecord
     */
    public void setRMTIndicator(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setRMTIndicator", inputRecord);
        }
        getComponentDAO().setRMTIndicator(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setRMTIndicator");
        }
    }

    /**
     * Process RM Discount
     *
     * @param inputRecord
     */
    public void processRmDiscount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processRmDiscount", inputRecord);
        }
        Record returnRecord = getComponentDAO().processRmDiscount(inputRecord);
        if (returnRecord.hasFieldValue("rc")) {
            if (returnRecord.getIntegerValue("rc").intValue() < 0) {
                MessageManager.getInstance().addErrorMessage("pm.processingRmComponent.discount.error", returnRecord.getStringValue("rmsg"));
            }
            else {
                MessageManager.getInstance().addInfoMessage("pm.processingRmComponent.process.completed");
            }
        }
        // Throw validation exception if there is any error message.
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Process RM Discount error.");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processRmDiscount");
        }
    }

    /**
     * Get initial values for the processing event.
     *
     * @return Record
     */
    public Record getInitialValuesForProcessingEvent() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForProcessingEvent");
        }
        // Set the sequence No to pmRmProcessMstrId.
        String today = DateUtils.formatDate(new Date());
        Record output = new Record();
        RmComponentFields.setPmRmProcessMstrId(output, getDbUtilityManager().getNextSequenceNo().toString());
        RmComponentFields.setAccountingDate(output, today);
        RmComponentFields.setProcessStatus(output, RmComponentFields.ProcessStatusValues.INPROGRESS);
        RmComponentFields.setTransactionEffectiveDate(output, today);
        // The Delete/Process options should be shown.
        output.setFieldValue("isDeleteAvailable", "Y");
        output.setFieldValue("isProcessAvailable", "Y");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForProcessingEvent", output);
        }
        return output;
    }

    /**
     * Load all Corp/Org discount member.
     *
     * @param inputRecord
     * @param selIndLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllCorpOrgDiscountMember(Record inputRecord, RecordLoadProcessor selIndLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCorpOrgDiscountMember", inputRecord);
        }
        RecordSet rs = null;
        if (inputRecord.hasStringValue("searchMember") && YesNoFlag.getInstance(inputRecord.getStringValue("searchMember")).booleanValue()) {
            RecordLoadProcessor loadProcessor = new CorpOrgComponentEntitlementRecordLoadProcessor();
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(selIndLoadProcessor, loadProcessor);
            inputRecord.setFieldValue("transId", null);
            rs = getComponentDAO().loadAllCorpOrgDiscountMember(inputRecord, loadProcessor);
            if (rs.getSize() <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.processingCorpOrgComponent.noMember");
            }
        }
        else {
            rs = new RecordSet();
            List fieldNameList = new ArrayList();
            fieldNameList.add("policyId");
            rs.addFieldNameCollection(fieldNameList);
        }

        // Default the transactionEffectiveDate to today.
        if (!inputRecord.hasFieldValue("transactionEffectiveDate") || StringUtils.isBlank(inputRecord.getStringValue("transactionEffectiveDate"))) {
            String today = DateUtils.formatDate(new Date());
            rs.getSummaryRecord().setFieldValue("transactionEffectiveDate", today);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCorpOrgDiscountMember", rs);
        }
        return rs;
    }

    /**
     * Process Corp/Org discount
     *
     * @param inputRecord
     * @param inputRecords
     * @return Record
     */
    public Record processCorpOrgDiscount(Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processCorpOrgDiscount", inputRecords);
        }
        String riskIdString = "";
        // Loop the Recordset to combine the riskId, each riskId should be splite by comma ','.
        for (int i = 0; i < inputRecords.getSize(); i++) {
            String riskId = inputRecords.getRecord(i).getStringValue("riskBaseRecordId");
            if (!StringUtils.isBlank(riskId)) {
                riskIdString = riskIdString + riskId + ",";
            }
        }
        inputRecord.setFieldValue("riskIdString", riskIdString);
        inputRecord.setFieldValue("numberOfMember", new Integer(inputRecords.getSize()));
        inputRecord.setFieldValue("relTransId",null);
        Record returnRecord = getComponentDAO().processCorpOrgDiscount(inputRecord);
        if (returnRecord.hasFieldValue("rc")) {
            if (returnRecord.getIntegerValue("rc").intValue() < 0) {
                MessageManager.getInstance().addErrorMessage("pm.processingCorpOrgComponent.discount.error", returnRecord.getStringValue("rmsg"));
            }
            else {
                MessageManager.getInstance().addInfoMessage("pm.processingCorpOrgComponent.process.completed");
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processCorpOrgDiscount", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Load all processing event history
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessEventHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcessEventHistory", inputRecord);
        }
        RecordSet rs = getComponentDAO().loadAllProcessEventHistory(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcessEventHistory", rs);
        }
        return rs;
    }

    /**
     * Load all processing detail history
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessDetailHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcessDetailHistory", inputRecord);
        }
        RecordSet rs = getComponentDAO().loadAllProcessDetailHistory(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcessDetailHistory", rs);
        }
        return rs;
    }

    /**
     * Apply the component
     *
     * @param inputRecord
     * @param inputRecords
     * @return Record
     */
    public Record applyMassComponent(Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "applyMassComponent", inputRecords);
        }

        // Validate component.
        validateApplyMassComponet(inputRecords);

        int size = inputRecords.getSize();
        StringBuffer codes = new StringBuffer();
        StringBuffer values = new StringBuffer();
        StringBuffer actions = new StringBuffer();
        for (int i = 0; i < size; i++) {
            Record tempRecord = inputRecords.getRecord(i);
            codes.append(ComponentFields.getCoverageComponentCode(tempRecord));
            values.append(ComponentFields.getComponentValue(tempRecord) == null ?
                          "" : ComponentFields.getComponentValue(tempRecord));
            actions.append(ComponentFields.getComponentAction(tempRecord));
            // Append ',' to codes/values/actions.
            if (i < size - 1) {
                codes.append(",");
                values.append(",");
                actions.append(",");
            }
        }
        // Set the parameters into inputRecord
        inputRecord.setFieldValue("cnt", String.valueOf(size));
        inputRecord.setFieldValue("codes", codes.toString());
        inputRecord.setFieldValue("values", values.toString());
        inputRecord.setFieldValue("actions", actions.toString());
        // Apply component
        Record record = getComponentDAO().applyMassComponet(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "applyMassComponent",record);
        }

        return record;
    }

    /**
     * Get the default values for new added component(s)
     *
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForAddProcessComponent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddProcessComponent", inputRecord);
        }
        Record outputRecord = new Record();
        // Set values.
        if (inputRecord.hasStringValue(ComponentFields.COMPONENT_SIGN)) {
            ComponentFields.setComponentSigh(outputRecord, ComponentFields.getComponentSigh(inputRecord));
        }

        if (inputRecord.hasStringValue(ComponentFields.PRODUCT_COV_COMPONENT_ID)) {
            ComponentFields.setProductCovComponentId(outputRecord, ComponentFields.getProductCovComponentId(inputRecord));
        }

        if (inputRecord.hasStringValue(ComponentFields.COMPONENT_TYPE_CODE)) {
            ComponentFields.setComponentTypeCode(outputRecord, ComponentFields.getComponentTypeCode(inputRecord));
        }

        if (inputRecord.hasStringValue(ComponentFields.SHORT_DESCRIPTION)) {
            ComponentFields.setShortDescription(outputRecord, ComponentFields.getShortDescription(inputRecord));
        }

        if (inputRecord.hasStringValue(ComponentFields.COVERAGE_COMPONENT_CODE)) {
            ComponentFields.setCoverageComponentCode(outputRecord, ComponentFields.getCoverageComponentCode(inputRecord));
        }

        if (inputRecord.hasStringValue(ComponentFields.CYCLED_B)) {
            ComponentFields.setCycledB(outputRecord, ComponentFields.getCycledB(inputRecord));
        }

        // If the low/high value is empty, system defaults it to zero.
        if (StringUtils.isBlank(ComponentFields.getLowValue(inputRecord))) {
            ComponentFields.setLowValue(inputRecord, "0");
        }
        if (StringUtils.isBlank(ComponentFields.getHighValue(inputRecord))) {
            ComponentFields.setHighValue(inputRecord, "0");
        }
        // Set low/high value to compchar1/2
        ComponentFields.setOrigLowValue(outputRecord, ComponentFields.getLowValue(inputRecord));
        ComponentFields.setOrigHighValue(outputRecord, ComponentFields.getHighValue(inputRecord));
        // If the lowvalue equals the highvalue, system set the component value to lowvalue.
        if (ComponentFields.getLowValue(inputRecord).equals(ComponentFields.getHighValue(inputRecord)) &&
            !(ComponentFields.getCycledB(inputRecord).booleanValue() ||
            ComponentFields.getCoverageComponentCode(inputRecord).equals(ComponentFields.ComponentCodeValues.NEWDOCTOR))) {
            ComponentFields.setComponentValue(outputRecord, ComponentFields.getLowValue(inputRecord));
        }
        // Append '%' to the low/high value if percentValueB is Y.
        if (ComponentFields.getPercentValueB(inputRecord).booleanValue()) {
            ComponentFields.setLowValue(outputRecord, ComponentFields.getLowValue(inputRecord) + "%");
            ComponentFields.setHighValue(outputRecord, ComponentFields.getHighValue(inputRecord) + "%");
        }
        else {
            ComponentFields.setLowValue(outputRecord, ComponentFields.getLowValue(inputRecord));
            ComponentFields.setHighValue(outputRecord, ComponentFields.getHighValue(inputRecord));
        }

        // Append '-' prior to low/high value if the sing is '-1'.
        if (inputRecord.hasStringValue(ComponentFields.COMPONENT_SIGN) && "-1".equals(ComponentFields.getComponentSigh(inputRecord))) {
            ComponentFields.setLowValue(outputRecord, "-" + ComponentFields.getLowValue(outputRecord));
            ComponentFields.setHighValue(outputRecord, "-" + ComponentFields.getHighValue(outputRecord));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddProcessComponent", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Validate component
     *
     * @param inputRecords
     */
    protected void validateApplyMassComponet(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateApplyMassComponet", inputRecords);
        }
        int size = inputRecords.getSize();
        for (int i = 0; i < size; i++) {
            Record record = inputRecords.getRecord(i);
            String rowNum = String.valueOf(record.getRecordNumber() + 1);
            String rowId = ComponentFields.getProductCovComponentId(record);

            // Action can't be empty.
            if (!record.hasStringValue(ComponentFields.COMPONENT_ACTION) ||
                StringUtils.isBlank(ComponentFields.getComponentAction(record))) {
                MessageManager.getInstance().addErrorMessage("pm.process.mass.component.action.error", new String[]{rowNum},
                    ComponentFields.COMPONENT_ACTION, rowId);
                break;
            }
            else if (!(ComponentFields.getCycledB(record).booleanValue() ||
                       ComponentFields.getCoverageComponentCode(record).equals(ComponentFields.ComponentCodeValues.NEWDOCTOR))
                       && "M".equals(ComponentFields.getComponentAction(record))) {
                // Set the component value to zero if the Action is "Remove" and it's not the new doctor component or cycled component.
                ComponentFields.setComponentValue(record, "0");
            }
            // Component value can't be empty or out of the range between low value and high value.
            if (!isValidComponentValue(record)) {
                MessageManager.getInstance().addErrorMessage("pm.process.mass.component.value.error", new String[]{rowNum},
                    ComponentFields.COMPONENT_VALUE, rowId);
                break;
            }
        }

        // Throw validation exception if there is any error message.
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Failed to validate component.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateApplyMassComponet");
        }
    }

    /**
     * Check whether the component value is between low value and high value.
     *
     * @param inputRecord
     * @return boolean
     */
    protected boolean isValidComponentValue(Record inputRecord) {
        boolean rtnValue = true;
        if ((ComponentFields.getCycledB(inputRecord).booleanValue() ||
            ComponentFields.getCoverageComponentCode(inputRecord).equals(ComponentFields.ComponentCodeValues.NEWDOCTOR))) {
            rtnValue = true;
        }else if (!inputRecord.hasStringValue(ComponentFields.COMPONENT_VALUE) ||
            StringUtils.isBlank(ComponentFields.getComponentAction(inputRecord))) {
            rtnValue = false;
        }
        else {
            String action = ComponentFields.getComponentAction(inputRecord);
            Float componentValue = Float.valueOf(ComponentFields.getComponentValue(inputRecord));
            Float origLowValue = Float.valueOf(ComponentFields.getOrigLowValue(inputRecord));
            Float origHightValue = Float.valueOf(ComponentFields.getOrigHighValue(inputRecord));
            // System shouldn't check the value if the action is 'Remove'.
            if (!"M".equals(action)) {
                if (origLowValue.compareTo(origHightValue) > 0) {
                    if ((componentValue.compareTo(origLowValue) > 0 || (componentValue.compareTo(origHightValue) < 0))) {
                        rtnValue = false;
                    }
                }
                else
                if ((componentValue.compareTo(origLowValue) < 0 || (componentValue.compareTo(origHightValue) > 0))) {
                    rtnValue = false;
                }
            }
        }
        return rtnValue;
    }

    /**
     * Validate the processing event data.
     *
     * @param inputRecords
     */
    public void validateProcessingEvent(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateProcessingEvent");
        }
        for (int i = 0; i < inputRecords.getSize(); i++) {
            Record record = inputRecords.getRecord(i);
            String rowId = RmComponentFields.getPmRmProcessMstrId(record);
            String transEffDate = "";
            if(record.hasStringValue(RmComponentFields.TRANSACTION_EFFECTIVE_DATE)){
                transEffDate = RmComponentFields.getTransactionEffectiveDate(record);
            }
            String startDateString = RmComponentFields.getEffectiveFromDate(record);
            String endDateString = RmComponentFields.getEffectiveToDate(record);
            if (StringUtils.isBlank(transEffDate)) {
                MessageManager.getInstance().addErrorMessage("pm.processingRmComponent.transEffDate.required", "transactionEffectiveDate", rowId);
            }
            if (StringUtils.isBlank(startDateString)) {
                MessageManager.getInstance().addErrorMessage("pm.processingRmComponent.startDate.required", "effectiveFromDate", rowId);
            }
            if (StringUtils.isBlank(endDateString)) {
                MessageManager.getInstance().addErrorMessage("pm.processingRmComponent.endDate.required", "effectiveToDate", rowId);
            }
            // Throw validation exception if there is any error message.
            if (MessageManager.getInstance().hasErrorMessages())
                throw new ValidationException("The start date is after the end date.");

            Date startDate = DateUtils.parseDate(startDateString);
            Date endDate = DateUtils.parseDate(endDateString);
            if (startDate.after(endDate)) {
                MessageManager.getInstance().addErrorMessage("pm.processingRmComponent.date.error", "effectiveFromDate", rowId);
                break;
            }
        }
        // Throw validation exception if there is any error message.
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("The start date is after the end date.");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateProcessingEvent");
        }
    }
    /**
     * Validate some fields if a component expiration was processed.
     * @param records
     */
    protected void setFieldsForExpireComponent(RecordSet records) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setFieldsForExpireComponent", new Object[]{records});
        }

        Iterator it = records.getRecords();
        while (it.hasNext()) {
            Record record = (Record)it.next();
            if (record.getFieldValue("RECORDMODECODE").equals(OFFICIAL) &&
                !record.getFieldValue(ComponentFields.EFFECTIVE_TO_DATE).equals(ComponentFields.ORIG_EFFECTIVE_TO_DATE)) {

                String compValue = (record.getFieldValue(ComponentFields.COMPONENT_VALUE)==null)?NULL:record.getFieldValue(ComponentFields.COMPONENT_VALUE).toString();
                String origCompValue = (record.getFieldValue(ComponentFields.ORIG_COMPONENT_VALUE)==null)?NULL:record.getFieldValue(ComponentFields.ORIG_COMPONENT_VALUE).toString();
                String compNote = (record.getFieldValue(ComponentFields.COMP_NOTE)==null)?NULL:record.getFieldValue(ComponentFields.COMP_NOTE).toString();
                String origCompNote = (record.getFieldValue(ComponentFields.ORIGINAL_COMP_NOTE)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_COMP_NOTE).toString();
                String incValue = (record.getFieldValue(ComponentFields.INC_VALUE)==null)?NULL:record.getFieldValue(ComponentFields.INC_VALUE).toString();
                String origIncValue = (record.getFieldValue(ComponentFields.ORIGINAL_INC_VALUE)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_INC_VALUE).toString();
                String aggValue = (record.getFieldValue(ComponentFields.AGG_VALUE)==null)?NULL:record.getFieldValue(ComponentFields.AGG_VALUE).toString();
                String origAggValue = (record.getFieldValue(ComponentFields.ORIGINAL_AGG_VALUE)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_AGG_VALUE).toString();
                String classCode = (record.getFieldValue(ComponentFields.CLASSIFICATION_CODE)==null)?NULL:record.getFieldValue(ComponentFields.CLASSIFICATION_CODE).toString();
                String origClassCode = (record.getFieldValue(ComponentFields.ORIGINAL_CLASSIFICATION_CODE)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_CLASSIFICATION_CODE).toString();
                String compChar1 = (record.getFieldValue(ComponentFields.COMP_CHAR1)==null)?NULL:record.getFieldValue(ComponentFields.COMP_CHAR1).toString();
                String origCompChar1 = (record.getFieldValue(ComponentFields.ORIGINAL_COMP_CHAR1)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_COMP_CHAR1).toString();
                String compChar2 = (record.getFieldValue(ComponentFields.COMP_CHAR2)==null)?NULL:record.getFieldValue(ComponentFields.COMP_CHAR2).toString();
                String origCompChar2 = (record.getFieldValue(ComponentFields.ORIGINAL_COMP_CHAR2)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_COMP_CHAR2).toString();
                String compChar3 = (record.getFieldValue(ComponentFields.COMP_CHAR3)==null)?NULL:record.getFieldValue(ComponentFields.COMP_CHAR3).toString();
                String origCompChar3 = (record.getFieldValue(ComponentFields.ORIGINAL_COMP_CHAR3)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_COMP_CHAR3).toString();
                String compNum1 = (record.getFieldValue(ComponentFields.COMP_NUM1)==null)?NULL:record.getFieldValue(ComponentFields.COMP_NUM1).toString();
                String origCompNum1 = (record.getFieldValue(ComponentFields.ORIGINAL_COMP_NUM1)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_COMP_NUM1).toString();
                String compNum2 = (record.getFieldValue(ComponentFields.COMP_NUM2)==null)?NULL:record.getFieldValue(ComponentFields.COMP_NUM2).toString();
                String origCompNum2 = (record.getFieldValue(ComponentFields.ORIGINAL_COMP_NUM2)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_COMP_NUM2).toString();
                String compNum3 = (record.getFieldValue(ComponentFields.COMP_NUM3)==null)?NULL:record.getFieldValue(ComponentFields.COMP_NUM3).toString();
                String origCompNum3 = (record.getFieldValue(ComponentFields.ORIGINAL_COMP_NUM3)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_COMP_NUM3).toString();
                String compDate1 = (record.getFieldValue(ComponentFields.COMP_DATE1)==null)?NULL:record.getFieldValue(ComponentFields.COMP_DATE1).toString();
                String origCompDate1 = (record.getFieldValue(ComponentFields.ORIGINAL_COMP_DATE1)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_COMP_DATE1).toString();
                String compDate2 = (record.getFieldValue(ComponentFields.COMP_DATE2)==null)?NULL:record.getFieldValue(ComponentFields.COMP_DATE2).toString();
                String origCompDate2 = (record.getFieldValue(ComponentFields.ORIGINAL_COMP_DATE2)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_COMP_DATE2).toString();
                String compDate3 = (record.getFieldValue(ComponentFields.COMP_DATE3)==null)?NULL:record.getFieldValue(ComponentFields.COMP_DATE3).toString();
                String origCompDate3 = (record.getFieldValue(ComponentFields.ORIGINAL_COMP_DATE3)==null)?NULL:record.getFieldValue(ComponentFields.ORIGINAL_COMP_DATE3).toString();

                if (compValue.equals(origCompValue) && compNote.equals(origCompNote) && incValue.equals(origIncValue) &&
                    aggValue.equals(origAggValue) && classCode.equals(origClassCode) && compChar1.equals(origCompChar1) &&
                    compChar2.equals(origCompChar2) && compChar3.equals(origCompChar3) && compNum1.equals(origCompNum1) &&
                    compNum2.equals(origCompNum2) && compNum3.equals(origCompNum3) && compDate1.equals(origCompDate1) &&
                    compDate2.equals(origCompDate2) && compDate3.equals(origCompDate3) ) {

                    record.setFieldValue(ComponentFields.COMPONENT_VALUE, null);
                    record.setFieldValue(ComponentFields.COMP_NOTE, null);
                    record.setFieldValue(ComponentFields.INC_VALUE, null);
                    record.setFieldValue(ComponentFields.AGG_VALUE, null);
                    record.setFieldValue(ComponentFields.CLASSIFICATION_CODE, null);
                    record.setFieldValue(ComponentFields.COMP_CHAR1, null);
                    record.setFieldValue(ComponentFields.COMP_CHAR2, null);
                    record.setFieldValue(ComponentFields.COMP_CHAR3, null);
                    record.setFieldValue(ComponentFields.COMP_NUM1, null);
                    record.setFieldValue(ComponentFields.COMP_NUM2, null);
                    record.setFieldValue(ComponentFields.COMP_NUM3, null);
                    record.setFieldValue(ComponentFields.COMP_DATE1, null);
                    record.setFieldValue(ComponentFields.COMP_DATE2, null);
                    record.setFieldValue(ComponentFields.COMP_DATE3, null);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setFieldsForExpireComponent");
        }
    }

    /**
     * Get the latest term expiration date.
     *
     * @param policyHeader
     * @return
     */
    private String getLatestTermExpDate(PolicyHeader policyHeader) {
        String latestTermExpDateStr = "";
        Iterator iter = policyHeader.getPolicyTerms();
        if (iter.hasNext()) {
            Term lastTerm = (Term) iter.next();
            latestTermExpDateStr = lastTerm.getEffectiveToDate();
        }
        return latestTermExpDateStr;
    }

    /**
     * Check if it is a problem policy
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean isProblemPolicy(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isProblemPolicy", new Object[]{policyHeader,});
        }

        boolean isProblemPolicy = false;
        if (policyHeader.hasProblemPolicyCache()) {
            isProblemPolicy = YesNoFlag.getInstance(policyHeader.getProblemPolicyCache()).booleanValue();
        }
        else {
            isProblemPolicy = YesNoFlag.getInstance(getComponentDAO().isProblemPolicy(policyHeader.toRecord())).booleanValue();
            policyHeader.setProblemPolicyCache(isProblemPolicy ? "Y" : "N");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isProblemPolicy", Boolean.valueOf(isProblemPolicy));
        }
        return isProblemPolicy;
    }

    /**
     * Check if add component allowed
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean isAddComponentAllowed(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddComponentAllowed", new Object[]{inputRecord,});
        }

        boolean isAddComponentAllowed = YesNoFlag.getInstance(getComponentDAO().isAddComponentAllowed(inputRecord)).booleanValue();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddComponentAllowed", Boolean.valueOf(isAddComponentAllowed));
        }
        return isAddComponentAllowed;
    }

    /**
     * Check if the official component record has a temp record exists for the specific transaction.
     *
     * @param inputRecord include component base record id and transaction id
     * @return true if component temp record exists
     *         false if component temp record does not exist
     */
    public boolean isComponentTempRecordExist(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isComponentTempRecordExist", new Object[]{inputRecord});
        }

        boolean isComponentTempRecordExist = getComponentDAO().isComponentTempRecordExist(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isComponentTempRecordExist", Boolean.valueOf(isComponentTempRecordExist));
        }
        return isComponentTempRecordExist;
    }

    /**
     * Validate if component value is out of range.
     *
     * @param inputRecord
     * @return boolean
     */
    private boolean validateCompValueRange(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());

        String componentSign = ComponentFields.getComponentSigh(inputRecord);
        String sHighValue = ComponentFields.getHighValue(inputRecord);
        String sLowValue = ComponentFields.getLowValue(inputRecord);
        String componentValue = ComponentFields.getComponentValue(inputRecord);
        boolean isOutOfRange = false;

        if (componentValue != null && sHighValue != null && sLowValue != null) {
            // Judge high/low value according with component sign.
            float highValue;
            float lowValue;
            int compSign;
            if (componentSign.equals("-1")) {
                compSign = -1;
            } else {
                compSign = 1;
            }

            // get component value
            float compValue = 0f;
            if (!StringUtils.isNumeric(componentValue)) {
                isOutOfRange = true;
            } else {
                compValue = compSign * Float.parseFloat(componentValue);
                // get real high/low value
                lowValue = compSign * Float.parseFloat(sLowValue);
                highValue = compSign * Float.parseFloat(sHighValue);

                if (compValue > highValue || compValue < lowValue) {
                    // if component sign=-1, user probable defines the low/high value in opposite way.
                    // need to support it as well
                    if (compSign < 0) {
                        float value = lowValue;
                        lowValue = highValue;
                        highValue = value;
                        if (compValue > highValue || compValue < lowValue) {
                            isOutOfRange = true;
                        }
                    } else {
                        isOutOfRange = true;
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCompValueRange", Boolean.valueOf(isOutOfRange));
        }

        return isOutOfRange;
    }

    /**
     * Check if component expiring date can be changed in OOSE.
     *
     * @param inputRecord
     * @return boolean
     */
    protected boolean validateCompChangeDateAllowed(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCompChangeDateAllowed", new Object[]{inputRecord,});
        }

        boolean isOoseChangeDateAllowed = YesNoFlag.getInstance(getComponentDAO().isOoseChangeDateAllowed(inputRecord)).booleanValue();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCompChangeDateAllowed", Boolean.valueOf(isOoseChangeDateAllowed));
        }
        return isOoseChangeDateAllowed;
    }

    /**
     * Load coverage effective to date.
     *
     * @param inputRecord
     * @return
     */
    public String getCoverageExpirationDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageExpirationDate", new Object[]{inputRecord});
        }

        Record record = new Record();
        CoverageFields.setCoverageBaseRecordId(record, CoverageFields.getCoverageBaseRecordId(inputRecord));
        CoverageFields.setCoverageEffectiveFromDate(record, CoverageFields.getCoverageEffectiveFromDate(inputRecord));
        String expirationDate = getComponentDAO().getCoverageExpirationDate(record);

        l.exiting(getClass().getName(), "getCoverageExpirationDate", expirationDate);
        return expirationDate;
    }

    /**
     * Exclude component which is earlier than current transaction.
     *
     * @param recordSet
     * @param transEffFromDate
     * @return recordSet
     */
    private RecordSet getEffectiveComponentForCopyAll(RecordSet recordSet, String transEffFromDate) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEffectiveComponentForCopyAll", new Object[]{recordSet, transEffFromDate});
        }

        Date transDate = DateUtils.parseDateTime(transEffFromDate);
        RecordSet rs = new RecordSet();
        rs.addRecords(recordSet);
        Iterator iter = rs.getRecords();
        while (iter.hasNext()) {
            Record record = (Record) iter.next();
            Date compEffToDate = DateUtils.parseDateTime(ComponentFields.getEffectiveToDate(record));
            if(!transDate.before(compEffToDate)) {
                recordSet.removeRecord(record, true);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEffectiveComponentForCopyAll", new Object[]{recordSet});
        }
        return recordSet;
    }


    /**
     * Generate the new component id.
     * @return
     */
    public String getComponentSequenceId() {
        Long lCompId = getDbUtilityManager().getNextSequenceNo();
        return String.valueOf(lCompId.longValue());
    }

    /**
     * Load experience discount history information.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadExpHistoryInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadExpHistoryInfo", inputRecord);
        }
        RecordSet rs = getComponentDAO().loadExpHistoryInfo(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadExpHistoryInfo", rs);
        }
        return rs;
    }

    /**
     * Load claim information for a specific period of the risk.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadClaimInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadClaimInfo", inputRecord);
        }
        RecordSet rs = getComponentDAO().loadClaimInfo(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadClaimInfo", rs);
        }
        return rs;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getComponentDAO() == null)
            throw new ConfigurationException("The required property 'componentDAO' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        if (getPmDefaultManager() == null)
            throw new ConfigurationException("The required property 'pmDefaultManager' is missing.");
        if (getTransactionDAO() == null)
            throw new ConfigurationException("The required property 'transactionDAO' is missing.");
    }

    public ComponentDAO getComponentDAO() {
        return m_componentDAO;
    }

    public void setComponentDAO(ComponentDAO componentDAO) {
        m_componentDAO = componentDAO;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public PMDefaultManager getPmDefaultManager() {
        return m_pmDefaultManager;
    }

    public void setPmDefaultManager(PMDefaultManager pmDefaultManager) {
        m_pmDefaultManager = pmDefaultManager;
    }

    public TransactionDAO getTransactionDAO() {
        return m_transactionDAO;
    }

    public void setTransactionDAO(TransactionDAO transactionDAO) {
        m_transactionDAO = transactionDAO;
    }

    protected static final String TEMP_COMP_EXPIRATION_DATE = "tempCompExpirationDate";
    protected static final String ADJUSTMENT = "ADJUSTMENT";
    protected static final String ROW_NUM = "rowNum";
    protected static final String ROW_ID = "rowId";
    protected static final String COVG_COMP_TAB = "COVG_COMP_TAB";
    protected static final String PRODUCT_COVERAGE_CODE = "PRODUCT_COVERAGE_CODE";
    protected static final String COVERAGE_COMPONENT_CODE = "COVERAGE_COMPONENT_CODE";
    protected static final String OFFICIAL = "OFFICIAL";
    protected static final String NULL = "NULL";
    protected static final String POLICY = "Policy";
    protected static final String COVERAGE = "Coverage ";
    protected static final String MAINTAIN_POLICY_ACTION_CLASS_NAME = "dti.pm.policymgr.struts.MaintainPolicyAction";
    protected static final String ROW_STATUS = "rowStatus";
    protected static final String NEW = "NEW";
    protected static final String MODIFIED = "MODIFIED";

    private WorkbenchConfiguration m_workbenchConfiguration;
    private ComponentDAO m_componentDAO;
    private DBUtilityManager m_dbUtilityManager;
    private PMDefaultManager m_pmDefaultManager;
    private TransactionDAO m_transactionDAO;

    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{ComponentFields.EFFECTIVE_FROM_DATE, ComponentFields.EFFECTIVE_TO_DATE, ComponentFields.COMPONENT_VALUE,
            ComponentFields.RENEWAL_B, ComponentFields.COMPONENT_CYCLE_DATE, ComponentFields.COMP_NOTE,
            ComponentFields.INC_VALUE, ComponentFields.AGG_VALUE, ComponentFields.CLASSIFICATION_CODE,
            ComponentFields.COMP_CHAR1, ComponentFields.COMP_CHAR2, ComponentFields.COMP_CHAR3,
            ComponentFields.COMP_NUM1, ComponentFields.COMP_NUM2, ComponentFields.COMP_NUM3,
            ComponentFields.COMP_DATE1, ComponentFields.COMP_DATE2, ComponentFields.COMP_DATE3});
}
