package dti.pm.coveragemgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.core.data.CommonTabsEntitlementRecordLoadProcessor;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.core.data.RowAccessorRecordLoadProcessor;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageHeader;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.coveragemgr.ExcessCoverageFields;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.coveragemgr.dao.CoverageDAO;
import dti.pm.dao.DataFieldNames;
import dti.pm.pmdefaultmgr.PMDefaultManager;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.service.RiskInquiryFields;
import dti.pm.riskmgr.RiskFields;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import dti.pm.validationmgr.impl.PreOoseChangeValidator;
import dti.pm.validationmgr.impl.ShortTermEffectiveToDateRecordValidator;
import dti.pm.validationmgr.impl.StandardEffectiveToDateRecordValidator;
import dti.pm.validationmgr.impl.StandardRetroactiveDateRecordValidator;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of CoverageManager Interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 10, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/27/2007       sxm         Modified loadCoverageHeader() to allow loading coverage header w/o riskId
 * 09/12/2007       sxm         1) Fixed saving coverage w/ default component problem by passing ComponentOwner to
 *                                 saveAllDefaultComponent().
 *                              2) Replace String.equals() with StringUtils.isSame() to handle possible NULL values
 * 04/07/2008       fcb         1) setCoverageLimitShared added.
 * 10/01/2008       sxm         Issue 86893 - default retro date to transaction effective date instead of term effective
 * 03/01/2010       fcb         104191: passed transaction id to getDefaultLevel call.
 * 06/11/2010       bhong       108368 - add condition in "isEffectiveToDateEditable" method to disable
 *                              coverage effective to date field for new rows during OOSE if dateChangeB=N
 * 09/07/2010       dzhang      108261 - Modified loadAllSourceCoverage() to add a new parameter
 * 09/20/2010       syang       Issue 110819 - Added FilterOfficialRowForEndquoteRecordLoadProcessor to loadAllCoverage().
 * 01/21/2011       dzhang      116359 - Modified getInitialValuesForCoverage, added isAddCoverageAllowed().
 * 03/28/2011       sxm         Issue 119216 - Modify retro date validation condition by checking NULL return value
 *                              from PM_Get_Retro_Date() function.
 * 05/10/2011       wqfu        120223 - Modified validateAllCoverage to set annual base rate as O replace of null
 *                              to prevent null manual rate rating exception in RT_Get.Get_Covg procedure.
 * 05/13/2011       dzhang      120483 - Mofified validateAllCoverage to remove the validation on expiration date.
 * 05/18/2011       dzhang      117246 - Modified getInitialValuesForCoverage.
 * 05/30/2011       ryzhao      121334 - Modified loadCoverageAddlInfo. If system parameter PM_RETRIEVE_CVG_ADDL is set to Y,
 *                                       we needn't to retrieve additional info again.
 * 05/25/2011       gxc         Issue 105791 - Modify to set the coverage expiration date for converted coverages
 * 06/23/2011       wqfu        121715 - Modified getInitialValuesForOoseCoverage to load coverageBaseRecordId.
 * 07/20/2011       syang       121208 - Modified getInitialValuesForCoverage() to overwrite the fields Coverage Limit Code,
 *                                       Shared Limits, Annual Base Rate and Retroactive Date.
 * 08/02/2011       wqfu        122985 - Modified validateAllCoverage to add condition policyFormCode to check retro date.
 * 08/30/2011       ryzhao      124458 - Modified validateAllCoverage to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 08/31/2011       ryzhao      124458 - Remove DateUtils.formatDate() and call FormatUtils.formatDateForDisplay() directly.
 * 09/22/2011       ryzhao      124862 - Refactor all the codes by moving method decimalPlaceCheck() into StringUtils.java.
 * 10/11/2011       syang       125626 - Modified getInitialValuesForCoverage() to carry retro date from select coverage
 *                                       whatever the "SET_RETRO_DATE" is.
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 03/05/2012       lmjiang     127780 - Modified getInitialValuesForCoverage: set the value of riskTypeCode to the record
 *                                       to be added on the coverage information page.
 * 05/09/2012       sxm         133432 - Modified getInitialValuesForCoverage() to pass term effective date to
 *                                       getShortTermCoverageEffAndExpDates().
 * 06/11/2012       awu         131172 - Add a logic in method addAllDefaultComponent() to check if the default component
 *                                       is already added or not.
 * 07/19/2012       awu         134738 - Modified validateAllCoverage to add logics to check the expiration date is null or not.
 * 07/24/2012       awu         129250 - Added processAutoSaveAllCoverageAndComponent(), processSaveAllCoverageAndComponentData().
 *                                       Modified processSaveAllCoverageAndComponent() to call processSaveAllCoverageAndComponentData().
 * 11/05/2012       xnie        121875 - 1) Modified isEffectiveToDateEditable() to add a condition for short term coverage.
 *                                       2) Modified getInitialValuesForCoverage() to set officialRecordId.
 * 12/05/2012       xnie        139365 - Modified validateAllCarrier() to remove limit code required validation logic.
 * 01/16/2013       tcheng      140034 - Modified loadAllSourceCoverage to keep the records with coverage version effective to date equals to 1/1/3000.
 * 03/06/2013       fcb         141924 - Changes for Web Services.
 * 04/22/2013       xnie        142770 - 1) Added setRecordModeCode() to set correct record mode code.
 *                                       2) Modified loadCoverageHeader() to call setRecordModeCode() to set record mode code and set endQuoteId.
 *                                       3) Modified loadAllCoverage() to call setRecordModeCode() to set record mode code.
 *                                       4) Modifed loadAllCoverageForWs() to call setRecordModeCode() to set record mode code.
 * 04/24/2013       awu         141758 - Changed addAllDefaultComponent to addAllCoverage.
 *                                       Added logic to add all selected coverages, dependent ones and default comp.
 *                                       Added function setReturnRecord to setup record for new coverage displaying.
 *                                       Added addAllComponent().
 * 04/25/2013       xnie        142770 - Roll back prior fix.
 * 05/30/2013       jshen       141758 - Resolve highlight row problem when adding components
 * 06/27/2013       xnie        146180 - Removed coverageBaseRecordId condition for dependent components. Because these
 *                                       dependent components will be added to same coverage, we don't need to check it.
 * 08/07/2013       awu         146878 - Modified loadCoverageHeader to set coverage chain status to coverage header.
 * 08/22/2013       adeng       145619 - Modified loadAllSourceCoverage to
 *                                       1) revert the fixing of issue 140034, it cause problem to load coverage when
 *                                       perform copy all for short term risk, because the effective to date of coverages
 *                                       are not same as 01/01/3000.
 *                                       2) use another solution to fix issue 140034.
 * 08/27/2013       adeng       146452 - Modified saveAllCoverage() to remove the logic which set record mode code
 *                                       to 'TEMP'. The record mode code have no further act, but cause problem.
 * 08/30/2013       adeng       146452 - Roll back previous changes.
 * 09/17/2013       xnie        146452 - Modified saveAllCoverage() to remove the logic which set record mode code
 *                                       to 'TEMP'. The record mode code have no further act, but cause problem.
 * 09/24/2013       adeng       147468 - Modified loadCoverageHeader() to set recordMode to "OFF_ENDQ" when screen mode
 *                                       code is view Endquote and set endorsement quote id into the record.
 * 10/10/2013       adeng       148929 - MOdified getInitialValuesForCoverage() to set subLimitB.
 * 10/22/2013       xnie        148287 - Modified validateAllCoverage() to remove retroactive date check below for out
 *                                       of sequence transaction case which has been handled in
 *                                       StandardRetroactiveDateRecordValidator:
 *                                       a. Validate that retroactive date is not after coverage effective date.
 *                                       b. Validate that retroactive date is not prior to the system minimum
 *                                          retroactive date.
 * 10/23/2013       xnie        148246 - Modified saveAllCoverage() to add a indicator to control if coverage effective
 *                                       to date should be updated when user tries to save a new record again.
 * 10/02/2013       fcb         145725 - isProblemPolicy: changed the parameter.
 *                                     - PreOoseChangeValidator called only for OOSE transactions.
 *                                     - Added overloaded version of loadAllCoverage to skip entitlements for navigation.
 * 12/19/2013       jyang       148585 - Put the coverageId and riskId pair into userSession.
 * 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
 * 01/22/2014       jyang       150639 - Remove CoverageContinuousExpDate and CoverageContinuousEffDate from component records.
 *                                       Move getCoverageExpirationDate method to ComponentManager.
 * 02/20/2014       jyang       151796 - Updated addAllComponent() to only filter existing displayed components for
 *                                       adding component.
 * 01/21/2014       Parker      152127 - Cache a primary coverage into policy header for each risk.
 * 03/12/2014       awu         152873 - Modified addAllComponent to remove the checking same component logic.
 * 04/17/2014       adeng       153900 - Modified saveAllCoverage() to clear cache when saving newly add coverage and
 *                                       remove the unnecessary logic to clear risk option cache.
 * 04/21/2014       jyang2      149575 - Modified validateAllCoverage, corrected the old code which may set the
 *                                       annualBaseRate back to the negative value.
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Coverage
 * 05/15/2014       wdang       154193 - Revert issue 145619.
 * 6/11/2014        kxiang      155121 - Comment out if condtion  isCoveragePartConfigured in method  getInitialValuesForCoverage.
 * 6/20/2014        kxiang      155121 - Roll back changes 155121 ,add trim() for covgArray[5] in method addAllCoverage().
 * 06/30/2014       fcb         155670 - addAllCoverage: when checking if default coverage already exists, added logic
 *                                       to skip flat records when deciding if the default coverage exists or not already
 * 07/25/2014       awu         152034 - Roll back the changes of issue148585.
 * 10/12/2014       jyang       157749 - Modified validateAllCoverage to use transaction effDate to get correct risk
 *                                       effective_from_date for coverage effective date validation.
 * 09/16/2014       awu         157552 - 1. Modified saveAllCoverage to add duplication validation;
 *                                       2. Move the default coverage logic from addAllCoverage to saveAllCoverage.
 *                                       3. Added setInputForDuplicateValidation, handleDuplicateValidation.
 * 10/13/2014       kxiang      157884 - Modified getInitialValuesForCoverage, for short term risk, set coverage
 *                                       effectiveDate to retroDate, others set transEffectiveDate to retroDate.
 * 11/06/2014       awu         157552 - Modified setInputForDuplicateValidation to remove the baseRiskId.
 * 11/20/2014       awu         154316 - 1. Added getCoverageSequenceId for Policy Change Service using.
 *                                       2. Modified loadAllCoverageForWs to add the origFieldLoadProcessor.
 * 12/02/2014       jyang       158858 - Modified loadAllSourceCoverage, set the edit_indicator to 'Y' for all copy from coverages.
 * 12/26/2014       xnie        156995 - 1) Modified isEffectiveToDateEditable() to roll back 121875 fix.
 *                                       2) Modified validateAllCoverage() to add a validation: Short term official
 *                                          coverage expiration date and other fields can NOT be changed in non-out of
 *                                          sequence transaction at the same time.
 * 02/04/2015       xnie        156995 - Modified validateAllCoverage() to set default value for expireB.
 * 04/16/2015       jyang       162312 - Modified loadAllSourceCoverage, revised the change of 158858.
 * 06/11/2015       tzeng       163657 - Modified getInitialValuesForCoverage() to transfer the merge default value
 *                                       from Pm_Default.Get_Level_Default step before merge from business rule step.
 * 07/13/2015       xnie        164407 - Modified loadCoverageHeader() to call policyHeader.getEvalDate().
 * 11/19/2015       eyin        167171 - Modified addAllCoverage(), Add logic to process when inputRecords is null.
 * 06/30/2016       xnie        177836 - Modified isPriorActAvailable() to use coverage effective from date for date
 *                                       checking instead of term effective from date.
 * 07/11/2016       lzhang      177681 - Add loadAllNewCopiedCMCoverage, saveAllRetroDate method
 *                                       and validateAllRetrodate method.
 * 07/26/2016       lzhang      169751 - Add validateIbnrCoverageDates to validate IBNR From and IBNR To dates
 *                                       when IBNRCovgB is 'Y'
 * 08/25/2016       xnie        179096 - Modified getInitialValuesForOoseCoverage() to set IBNRCovgB to outputRecord.
 * 10/10/2016       jyang2      180121 - Modified addAllCoverage to only load dependent coverages for the new selected
 *                                       product coverage on Select Coverage screen.
 * 10/21/2016       ssheng      179215 - Set the first coverage header back after save
 *                                       default component/default coverage class.
 * 10/26/2016       jyang2      180121 - Rework. Modified addAllCoverage to create a recordSet for new selected product
 *                                       coverage insert the unsaved existing insert coverages also have the insert flag.
 * 09/17/2018       tayng       194900 - Modified getInitialValuesForCoverage() to change the priority in inputted,
 *                                       default value,product_coverage with limit code
 * ---------------------------------------------------
 */

public class CoverageManagerImpl implements CoverageManager, CoverageSaveProcessor {

    /**
     * Load the CoverageHeader bean of the PolicyHeader object with either the requested
     * coverage information, or the primary coverage if no specific request was made.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param coverageId   primary key value for the desired coverage information
     * @return PolicyHeader input PolicyHeader object now loaded with coverage information
     */

    public PolicyHeader loadCoverageHeader(PolicyHeader policyHeader, String coverageId) {

        Logger l = LogUtils.getLogger(getClass());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCoverageHeader", new Object[]{policyHeader, coverageId});
        }

        CoverageHeader coverageHeader = policyHeader.getCoverageHeader();
        boolean cached = false;
        Map<String, CoverageHeader> cacheCoverageHeader = policyHeader.getCacheCoverageHeader();
        if (coverageHeader != null && coverageHeader.getCoverageId().equals(coverageId)) {
            cached = true;
        } else {
            policyHeader.setCoverageHeader(null);
            if (StringUtils.isBlank(coverageId)) {
                if (policyHeader.hasRiskHeader())
                    coverageHeader = cacheCoverageHeader.get(policyHeader.getRiskHeader().getRiskId() + "primaryCoverage");
            } else {
                coverageHeader = cacheCoverageHeader.get(coverageId);
            }
            if (coverageHeader != null) {
                cached = true;
            }
        }
        // If the coverageId is not specified, the first coverageId is loaded.
        if (!cached) {
            // Initialize the input record
            Record inputRecord = policyHeader.toRecord();
            CoverageFields.setCoverageId(inputRecord, coverageId);

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

            CoverageDAO r = getCoverageDAO();
            CoverageFields.setRecordMode(inputRecord, recordMode);
            if (endorsementQuoteId != null) {
                TransactionFields.setEndorsementQuoteId(inputRecord, endorsementQuoteId);
            }
            coverageHeader = r.loadCoverageHeader(policyHeader, inputRecord);

            String covgBaseRecId = coverageHeader.getCoverageBaseRecordId();
            inputRecord = new Record();

            CoverageFields.setObjId(inputRecord, covgBaseRecId);
            CoverageFields.setRecordMode(inputRecord, recordMode);
            CoverageFields.setNoseB(inputRecord, YesNoFlag.Y);
            CoverageFields.setRetroB(inputRecord, YesNoFlag.Y);
            CoverageFields.setRetroChangeB(inputRecord, YesNoFlag.N);
            CoverageFields.setDelFlatB(inputRecord, YesNoFlag.N);
            CoverageFields.setSameCoverageB(inputRecord, YesNoFlag.N);
            if (endorsementQuoteId != null) {
                TransactionFields.setEndorsementQuoteId(inputRecord, endorsementQuoteId);
            }
            Record outRec = r.getCurrentCoverageStatus(inputRecord);
            coverageHeader.setCurrentCoverageStatus(outRec.getStringValue("returnValue"));
        }
        // Add the coverageHeader to the PolicyHeader
        policyHeader.setCoverageHeader(coverageHeader);
        policyHeader.addCacheCoverageHeader(coverageHeader);
        if(StringUtils.isBlank(coverageId)){
            policyHeader.addCachePrimaryCoverageHeader(coverageHeader);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCoverageHeader", policyHeader);
        }

        return policyHeader;
    }

    /**
     * Load all coverage summary by policy id
     *
     * @param inputRecord input record that contains policy id
     * @return coverage summary
     */
    public RecordSet loadAllCoverageSummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageSummary", new Object[]{inputRecord});
        }
        RecordSet outRecordSet = getCoverageDAO().loadAllCoverageSummary(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageSummary", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllCoverage(PolicyHeader policyHeader) {
        return loadAllCoverage(policyHeader, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
    }

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param processEntitlements true/false indicator for processing entitlements
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllCoverage(PolicyHeader policyHeader, boolean processEntitlements) {
        return loadAllCoverage(policyHeader, DefaultRecordLoadProcessor.DEFAULT_INSTANCE, processEntitlements);
    }

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllCoverage(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverage", new Object[]{policyHeader});
        }

        RecordSet rs = loadAllCoverage(policyHeader, loadProcessor, true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverage", rs);
        }

        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param processEntitlements true/false indicator for processing entitlements
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllCoverage(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, boolean processEntitlements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverage", new Object[]{policyHeader});
        }

        // Build the input record
        Record inputRecord = policyHeader.toRecord();
        RecordMode recordModeCode = RecordMode.TEMP;
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        inputRecord.setFieldValue(PMCommonFields.RECORD_MODE_CODE, recordModeCode);

        if (processEntitlements) {
        // Setup the record load processor in LIFO order (Last In, First Out)
        // The order here is: rowAccessorLP, entitlementRLP and finally passed-in loadProcessor

        RecordLoadProcessor rowAccessorLP = new RowAccessorRecordLoadProcessor(
            CoverageFields.COVERAGE_ID, CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE,
            CoverageFields.COVERAGE_EFFECTIVE_TO_DATE, policyHeader, policyHeader.getScreenModeCode());
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, rowAccessorLP);

        RecordLoadProcessor entitlementRLP = new CoverageEntitlementRecordLoadProcessor(this, policyHeader,
            policyHeader.getScreenModeCode());
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, entitlementRLP);

        CommonTabsEntitlementRecordLoadProcessor commonTabsLP = new CommonTabsEntitlementRecordLoadProcessor(policyHeader);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, commonTabsLP);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            origFieldLoadProcessor, loadProcessor);
        // Issue 110819, filter official record for end quote.
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor = new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "coverageId");
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, endquoteLoadProcessor);
        }

        RecordSet rs = null;
        CoverageDAO c = getCoverageDAO();
        try {
            rs = c.loadAllCoverage(inputRecord, loadProcessor);
        }
        catch (Exception e) {
            l.severe("Error when loading coverages.");
            throw new AppException(e.getMessage());
        }
        //set auditHistory href
        rs.setFieldValueOnAll("covgAuditHistory", "View");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverage", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param insuredId  the insured id of the records that need to be retrieved.
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllCoverageForWs(PolicyHeader policyHeader, String insuredId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageForWs", new Object[]{policyHeader});
        }

        // Build the input record
        Record inputRecord = policyHeader.toRecord();
        RecordMode recordModeCode = RecordMode.TEMP;
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }

        inputRecord.setFieldValue(PMCommonFields.RECORD_MODE_CODE, recordModeCode);
        inputRecord.setFieldValue(RiskInquiryFields.RISK_NUMBER_ID, insuredId);

        RecordLoadProcessor loadProcessor = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            origFieldLoadProcessor, loadProcessor);
        RecordSet rs = null;
        CoverageDAO c = getCoverageDAO();
        try {
            rs = c.loadAllCoverage(inputRecord, loadProcessor);
        }
        catch (Exception e) {
            l.severe("Error when loading coverages.");
            throw new AppException(e.getMessage());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageForWs", rs);
        }
        return rs;
    }

    /**
     * AJAX retrieval of coverage additional information fields.
     *
     * @param policyHeader
     * @param inputRecord  a record with the passed request values.
     * @return Record output record containing the additional info fields
     */
    public Record loadCoverageAddlInfo(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCoverageAddlInfo", new Object[]{inputRecord});
        }

        // If system parameter PM_RETRIEVE_CVG_ADDL is set to Y, we needn't to retrieve additional info again here.
        String retrieveCoverageAdditionalInfo = SysParmProvider.getInstance().getSysParm("PM_RETRIEVE_CVG_ADDL", "N");
        Record outputRecord = null;
        if (retrieveCoverageAdditionalInfo.equalsIgnoreCase("N")) {
            // Call the stored proc to determine validity
            outputRecord = getCoverageDAO().loadCoverageAddlInfo(inputRecord);
        }
        else {
            outputRecord = new Record();
        }

        //set Prior Act page entitlement info
        YesNoFlag isPriorActAvailable = isPriorActAvailable(inputRecord, policyHeader);
        outputRecord.setFieldValue("isPriorActAvailable", isPriorActAvailable);

        l.exiting(getClass().getName(), "loadCoverageAddlInfo", outputRecord);
        return outputRecord;
    }

    /**
     * Wrapper to invoke the save of all inserted/updated Coverage records and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader     the summary policy information corresponding to the provided coverages.
     * @param inputRecords     a set of Records, each with the updated Coverage Detail info
     *                         matching the fields returned from the loadAllCoverage method.
     * @param componentRecords a set of Records, each with the updated Component Detail info
     *                         matching the fields returned from the ComponentManager.loadAllComponents method.
     * @return the number of rows updated.
     */
    public int processSaveAllCoverageAndComponent(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet componentRecords) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveAllCoverage", new Object[]{inputRecords, componentRecords});

        int processCount = processSaveAllCoverageAndComponentData(policyHeader, inputRecords, componentRecords, false);

        l.exiting(getClass().getName(), "processSaveAllCoverage", new Integer(processCount));
        return processCount;
    }

    /**
     * Wrapper to auto invoke the save of all inserted/updated Coverage records and subsequently
     * to invoke the save transaction logic for WIP only.
     *
     * @param policyHeader     the summary policy information corresponding to the provided coverages.
     * @param inputRecords     a set of Records, each with the updated Coverage Detail info
     *                         matching the fields returned from the loadAllCoverage method.
     * @param componentRecords a set of Records, each with the updated Component Detail info
     *                         matching the fields returned from the ComponentManager.loadAllComponents method.
     * @return the number of rows updated.
     */
    public int processAutoSaveAllCoverageAndComponent(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet componentRecords) {
        Logger l = LogUtils.enterLog(getClass(), "processAutoSaveAllCoverageAndComponent", new Object[]{inputRecords, componentRecords});

        int processCount = processSaveAllCoverageAndComponentData(policyHeader, inputRecords, componentRecords, true);

        l.exiting(getClass().getName(), "processAutoSaveAllCoverageAndComponent", new Integer(processCount));
        return processCount;
    }

     /**
     * Wrapper to invoke the save of all inserted/updated Coverage records and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader     the summary policy information corresponding to the provided coverages.
     * @param inputRecords     a set of Records, each with the updated Coverage Detail info
     *                         matching the fields returned from the loadAllCoverage method.
     * @param componentRecords a set of Records, each with the updated Component Detail info
     *                         matching the fields returned from the ComponentManager.loadAllComponents method.
     * @param isAutoSave       a indicator to check it is auto save or not
     * @return the number of rows updated.
     */
     protected int processSaveAllCoverageAndComponentData(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet componentRecords, boolean isAutoSave) {
         Logger l = LogUtils.enterLog(getClass(), "processSaveAllCoverageAndComponentData", new Object[]{inputRecords, componentRecords});

         CoverageSaveProcessor saveProcessor = (CoverageSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
         int processCount = saveProcessor.saveAllCoverageAndComponent(policyHeader, inputRecords, componentRecords);

         // Complete the save action via the TransactionManager
         if (!isAutoSave) {
             Record saveRecord = inputRecords.getSummaryRecord();
             saveRecord.setFields(policyHeader.toRecord(), false);

             // Force transactionLogId from policyHeader since coverage record contains the same fieldId
             saveRecord.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
             saveRecord.setFieldValue("level", "COVERAGE");
             getTransactionManager().processSaveTransaction(policyHeader, saveRecord);
         }

         l.exiting(getClass().getName(), "processSaveAllCoverageAndComponentData", new Integer(processCount));
         return processCount;
    }

    /**
     * Save all Coverage and Component input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     */
    public int saveAllCoverageAndComponent(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet componentRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllCoverageAndComponent", new Object[]{inputRecords, componentRecords});

        // First save all inserted/updated Coverage records
        CoverageSaveProcessor saveProcessor = (CoverageSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
        int processCount = saveProcessor.saveAllCoverage(policyHeader, inputRecords);

        // Second save component records
        // Prepare component owner
        ComponentOwner owner = ComponentOwner.COVERAGE;
        // Save the component changes
        getComponentManager().saveAllComponent(policyHeader, componentRecords, owner, inputRecords);

        l.exiting(getClass().getName(), "saveAllCoverageAndComponent", new Integer(processCount));
        return processCount;
    }

    /**
     * Save all Coverage input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     */
    public int saveAllCoverageForWs(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllCoverageForWs", new Object[]{inputRecords});

        CoverageSaveProcessor saveProcessor = (CoverageSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
        int processCount = saveProcessor.saveAllCoverage(policyHeader, inputRecords);

        l.exiting(getClass().getName(), "saveAllCoverageForWs", new Integer(processCount));
        return processCount;
    }

    /**
     * Save all Component input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     */
    public void saveAllComponentForWs(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet componentRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllComponentForWs", new Object[]{inputRecords, componentRecords});

        ComponentOwner owner = ComponentOwner.COVERAGE;
        // Save the component changes
        getComponentManager().saveAllComponent(policyHeader, componentRecords, owner, inputRecords);

        l.exiting(getClass().getName(), "saveAllComponentForWs");
    }

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader the summary policy information corresponding to the provided coverages.
     * @param inputRecords a set of Records, each with the updated Coverage Detail info
     *                     matching the fields returned from the loadAllCoverage method.
     * @return the number of rows updated.
     */
    public int saveAllCoverage(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllCoverage", new Object[]{inputRecords});

        int updateCount = 0;

        // Validate the input coverages prior to saving them.
        validateAllCoverage(policyHeader, inputRecords);

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        RecordSet cachedChangedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.UPDATED}));

        if (changedRecords.getRecordList().size() > 0) {
            policyHeader.getCacheCoverageOption().clear();
            policyHeader.getCacheCoverageRiskOption().clear();
        }
        if (cachedChangedRecords.getRecordList().size() > 0) {
            policyHeader.deleteCacheCoverageHeader(cachedChangedRecords);
        }
        // Add the PolicyHeader info to each Coverage detail Record
        changedRecords.setFieldsOnAll(policyHeader.toRecord(), false);

        // Set the current transaction id on all records
        changedRecords.setFieldValueOnAll("transactionLogId", policyHeader.getLastTransactionId());

        // Split the input records for add, update and delete

        // Get the WIP records
        RecordSet wipRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet offRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
        RecordSet ooseRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.REQUEST));

        // Add the inserted WIP records in batch mode
        updateCount += addAllCoverage(policyHeader, wipRecords);

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
                String coverageEffectiveToDateOrg = CoverageFields.getOrigCoverageEffectiveToDate(record);
                String coverageEffectiveToDate = CoverageFields.getCoverageEffectiveToDate(record);
                if (!coverageEffectiveToDateOrg.equals(coverageEffectiveToDate)) {
                    CoverageFields.setUpdateExpB(record, YesNoFlag.Y);
                }
                else {
                    CoverageFields.setUpdateExpB(record, YesNoFlag.N);
                }
            }
        }

        updateCount += getCoverageDAO().addAllCoverage(updatedWipRecords);

        // Delete the WIP records marked for delete in batch mode
        RecordSet deleteRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        updateCount += getCoverageDAO().deleteAllCoverage(deleteRecords);

        // Update the OFFICIAL records marked for update in batch mode
        RecordSet updateRecords = offRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));

        // For the official records set the effective from date equal to the transaction effective date
        updateRecords.setFieldValueOnAll("transEffectiveFromDate", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        updateRecords.addRecords(ooseRecords);
        updateCount += getCoverageDAO().updateAllCoverage(updateRecords);

        //Coverages should not be duplicated for the same period.
        if (updateCount > 0) {
            Record inputRecord = setInputForDuplicateValidation(policyHeader);
            Record outputRec = getCoverageDAO().validateCoverageDuplicate(inputRecord);
            if (outputRec != null && YesNoFlag.N.equals(outputRec.getStringValue("result"))) {
                handleDuplicateValidation(policyHeader, outputRec);
                if (MessageManager.getInstance().hasErrorMessages()) {
                    throw new ValidationException("Coverage is duplicated.");
                }
            }
        }

        // Add default objects
        RecordSet insertedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        int insertedWipRecordCount = insertedWipRecords.getSize();
        if (!policyHeader.isSkipDefaultSubCoverage()) {
            // Add default coverage class for each new coverage
            for (int i = 0; i < insertedWipRecordCount; i++) {
                getCoverageClassManager().saveAllDefaultCoverageClass(loadCoverageHeader(policyHeader,
                    CoverageFields.getCoverageId(insertedWipRecords.getRecord(i))));
            }
        }

        l.exiting(getClass().getName(), "saveAllCoverage", new Integer(updateCount));
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

        String coverageId = CoverageFields.getCoverageId(resultRecord);
        String validationMessage = resultRecord.getStringValue("validationMessage");
        MessageManager.getInstance().addErrorMessage("pm.common.invalid.data",
            new String[]{validationMessage}, "", coverageId);

        l.exiting(getClass().getName(), "handleDuplicateValidation");
    }

    /**
     * Load all available coverage
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllAvailableCoverage(PolicyHeader policyHeader) {
        return loadAllAvailableCoverage(policyHeader, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
    }

    /**
     * Load all available coverage
     *
     * @param policyHeader
     * @return RecorSet
     */
    public RecordSet loadAllAvailableCoverage(PolicyHeader policyHeader, LoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableCoverage", new Object[]{policyHeader});
        }

        Record input = policyHeader.toRecord();
        CoverageFields.setCovPartCode(input, "X");
        CoverageFields.setParentCovCode(input, "X");

        // Handle special mapping functionality via the PMDefaultManager
        getPmDefaultManager().processMappedDefaults("MAP_COVERAGE", policyHeader, input);

        /* Get effective date and expire date based on system parameter "PM_ADD_COMPONENT_DT" */
        String sysPara = SysParmProvider.getInstance().getSysParm(
            SysParmIds.PM_ADD_COMPONENT_DT, SysParmIds.AddComponentDateValues.TRANS);
        if (SysParmIds.AddComponentDateValues.TRANS.equals(sysPara)) {
            input.setFieldValue(DataFieldNames.EFF_DATE,
                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }
        if (SysParmIds.AddComponentDateValues.TERM.equals(sysPara)) {
            input.setFieldValue(DataFieldNames.EFF_DATE, policyHeader.getTermEffectiveFromDate());
        }
        input.setFieldValue(DataFieldNames.EXP_DATE, policyHeader.getTermEffectiveToDate());

        /* Get available Coverage record set */
        RecordSet rs = getCoverageDAO().loadAllAvailableCoverage(input, (RecordLoadProcessor) loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableCoverage", rs);
        }
        return rs;
    }

    /**
     * Load dependent coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadDependentCoverage(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadDependentCoverage", new Object[]{policyHeader, inputRecord});
        }

        String parentCovCode = CoverageFields.getProductCoverageCode(inputRecord);
        Record input = policyHeader.toRecord();
        CoverageFields.setCovPartCode(input, "X");
        CoverageFields.setParentCovCode(input, parentCovCode);

        /* Get effective date and expire date based on system parameter "PM_ADD_COMPONENT_DT" */
        String sysPara = SysParmProvider.getInstance().getSysParm(
            SysParmIds.PM_ADD_COMPONENT_DT, SysParmIds.AddComponentDateValues.TRANS);
        if (SysParmIds.AddComponentDateValues.TRANS.equals(sysPara)) {
            input.setFieldValue(DataFieldNames.EFF_DATE,
                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }
        if (SysParmIds.AddComponentDateValues.TERM.equals(sysPara)) {
            input.setFieldValue(DataFieldNames.EFF_DATE, policyHeader.getTermEffectiveFromDate());
        }
        input.setFieldValue(DataFieldNames.EXP_DATE, policyHeader.getTermEffectiveToDate());

        // Get dependent coverage
        RecordSet rs = getCoverageDAO().loadDependentCoverage(input);
        rs = rs.getSubSet(new RecordFilter(CoverageFields.DEFAULT_DEPENDENT_COV_B, YesNoFlag.Y));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadDependentCoverage", rs);
        }
        return rs;
    }

    /**
     * Get inital values for coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForCoverage(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForCoverage",
                new Object[]{policyHeader, policyHeader.getRiskHeader(), inputRecord});
        }

        Record returnRecord = new Record();  //This record hold all values for initial new added row

        // Firstly, merge default value from web work bench configuration
        Record configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(ADD_COVERAGE_ACTION_CLASS_NAME);
        if (configuredDefaultValues != null) {
            returnRecord.setFields(configuredDefaultValues);
        }

        // Secondly, Merge all default values for selected coverage
        String productCovgCode = CoverageFields.getProductCoverageCode(inputRecord);
        RecordSet availableCoverage = loadAllAvailableCoverage(policyHeader);
        RecordSet selectedCoverage = availableCoverage.getSubSet(
            new RecordFilter(CoverageFields.PRODUCT_COVERAGE_CODE, productCovgCode));
        if (selectedCoverage.getSize() == 1) {
            returnRecord.setFields(selectedCoverage.getRecord(0));
        }
        else {
            throw new AppException(AppException.UNEXPECTED_ERROR, "the selected available class for <" + productCovgCode + "> is not equals to 1; it returned " + selectedCoverage.getSize() + " records.");
        }

        // Thirdly, load other default values
        //Merge default values from Pm_Default.Get_Level_Default
        Record defaultLevelValues = getPmDefaultManager().getDefaultLevel(COVG_TAB,
            policyHeader.getLastTransactionInfo().getTransactionLogId(),
            policyHeader.getTermEffectiveFromDate(), policyHeader.getLastTransactionInfo().getTransEffectiveFromDate(),
            PRODUCT_COVERAGE_CODE, productCovgCode,
            null, null, null, null);
        returnRecord.setFields(defaultLevelValues);

        // Fourthly, set more default values based on the business rules
        //[UC15.11]Special Requirement: Coverage Selection Input.
        //[UC15.13]Special Requirement: Default Values, Set default value for effective and expiration date.

        // Get the default Common Tab Entitlement values
        returnRecord.setFields(CommonTabsEntitlementRecordLoadProcessor.getInitialEntitlementValuesForCommonTabs());

        YesNoFlag dataChangeAllowedB = policyHeader.getRiskHeader().getDateChangeAllowedB();
        String sRiskEffectiveDate = policyHeader.getRiskHeader().getEarliestContigEffectiveDate();
        String sRiskExpirationDate = policyHeader.getRiskHeader().getRiskEffectiveToDate();
        String sTransEffectiveDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        String sTransExpDate = policyHeader.getLastTransactionInfo().getTransEffectiveToDate();
        String policyExpirationDate = policyHeader.getPolicyExpirationDate();
        Date riskExpirationDate = DateUtils.parseDate(sRiskExpirationDate);
        Date policyExpDate = DateUtils.parseDate(policyExpirationDate);
        String sEffectiveDate, sExpirationDate;
        boolean isShortTerm = YesNoFlag.getInstance(CoverageFields.getShortTermB(inputRecord)).booleanValue();

        //  Coverage expiration date's initial value depends on dataChangeAllowedB
        if (dataChangeAllowedB.booleanValue()) {
            Record input = new Record();
            RiskFields.setRiskBaseRecordId(input, policyHeader.getRiskHeader().getRiskBaseRecordId());
            CoverageFields.setProductCoverageCode(input, productCovgCode);
            TransactionFields.setTransactionEffectiveFromDate(input, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
            PolicyHeaderFields.setTermEffectiveFromDate(input,policyHeader.getTermEffectiveFromDate());
            Record datesRecord = getCoverageDAO().getShortTermCoverageEffAndExpDates(input);
            sEffectiveDate = CoverageFields.getCoverageEffectiveFromDate(datesRecord);
            sExpirationDate = CoverageFields.getCoverageEffectiveToDate(datesRecord);
            // Set the open date to policy expiration date.
            if (riskExpirationDate.after(policyExpDate)) {
                sExpirationDate =  policyExpirationDate;
            }
        }
        else {
            // Coverage Effective date default to transaction effective date
            sEffectiveDate = sTransEffectiveDate;
            sExpirationDate = policyExpirationDate;

            // If current coverage is short term coverage and transaction expiration date is not null
            // Initialize coverage expiration date to transaction expiration date
            // This is to cover the requirement that user may add shor-term coverage on a non-short term risk
            if (isShortTerm && !StringUtils.isBlank(sTransExpDate)) {
                sExpirationDate = sTransExpDate;
            }
        }

        CoverageFields.setCoverageEffectiveFromDate(returnRecord, sEffectiveDate);
        CoverageFields.setCoverageEffectiveToDate(returnRecord, sExpirationDate);
        CoverageFields.setCoverageBaseEffectiveFromDate(returnRecord, sEffectiveDate);
        CoverageFields.setCoverageBaseEffectiveToDate(returnRecord, sExpirationDate);

        // Set Rate_Payor_Depend_Code according with Rate_Payor_Depend_B
        if (CoverageFields.getRatePayorDependB(returnRecord).booleanValue()) {
            CoverageFields.setRatePayorDependCode(returnRecord, CoverageFields.RatePayorDependCodeValues.HOSPITALPY);
        }

        //Set Coverage Limit Code
        boolean defaultSharedLimitB = returnRecord.getBooleanValue(
            CoverageFields.PRODUCT_DEFAULT_SHARED_LIMITB).booleanValue();
        String productDefaultLimitCode = CoverageFields.getProductDefaultLimitCode(returnRecord);
        String pmDefaultLimitCode = "";
        if(returnRecord.hasField(CoverageFields.COVERAGE_LIMIT_CODE)){
            pmDefaultLimitCode = CoverageFields.getCoverageLimitCode(returnRecord);
        }
        if (policyHeader.isCoveragePartConfigured() && defaultSharedLimitB) {
            //todo: this logic to be defined once coverage part is done.
        }
        else {
            // If the coverageLimitCode has been set in select coverage page, system should use it here.
            if (inputRecord.hasStringValue(CoverageFields.COVERAGE_LIMIT_CODE)) {
                CoverageFields.setCoverageLimitCode(returnRecord, CoverageFields.getCoverageLimitCode(inputRecord));
            }
            else{
                if (!StringUtils.isBlank(pmDefaultLimitCode)) {
                    CoverageFields.setCoverageLimitCode(returnRecord, pmDefaultLimitCode);
                }
                else {
                    CoverageFields.setCoverageLimitCode(returnRecord, productDefaultLimitCode);
                }
            }
        }
        // If the shared limit has been set in select coverage page, system should use it here.
        if (inputRecord.hasStringValue(CoverageFields.PRODUCT_DEFAULT_SHARED_LIMITB)) {
            CoverageFields.setSharedLimitsB(returnRecord, CoverageFields.getProductDefaultSharedLimitB(inputRecord));
        }
        else {
            CoverageFields.setSharedLimitsB(returnRecord, CoverageFields.getProductDefaultSharedLimitB(returnRecord));
        }

        //Set subLimitB
        CoverageFields.setSubLimitB(returnRecord, CoverageFields.getProductDefaultSubLimitB(returnRecord));

        //Set retroactive Date
        String setRetroDate = SysParmProvider.getInstance().getSysParm(SysParmIds.SET_RETRO_DATE, "N");
        String policyFormCode = CoverageFields.getPolicyFormCode(returnRecord);
        String sTermEffectiveDate = policyHeader.getTermEffectiveFromDate();

        if (policyHeader.isCoveragePartConfigured()) {
            //todo: This logic to be defined once coverage part is done.
        }
        else {
            if (CM.equals(policyFormCode)) {
                // If the retroDate has been set in select coverage page, system should use it here,
                // else set the transaction effective from date to retroDate if SET_RETRO_DATE is Y.
                if (inputRecord.hasStringValue(CoverageFields.RETRO_DATE)) {
                    CoverageFields.setRetroDate(returnRecord, CoverageFields.getRetroDate(inputRecord));
                }
                else if (YesNoFlag.getInstance(setRetroDate).booleanValue()) {
                    //If it's short term risk, set CoverageEffectiveDate to retroDate, else set TransEffectiveDate.
                    CoverageFields.setRetroDate(returnRecord,sEffectiveDate);
                }
            }
        }

        //Clear the retroactive date if its policy form code is OCCURRENCE
        if (OCCURRENCE.equalsIgnoreCase(policyFormCode)) {
            CoverageFields.setRetroDate(returnRecord, "");
        }

        //Set annual base rate
        String ratingModuleCode = CoverageFields.getRatingModuleCode(returnRecord);

        if (isManuallyRated(ratingModuleCode)) {
            // If the annualBaseRate has been set in select coverage page, system should use it here,
            // else set 0 to annualBaseRate.
            if(inputRecord.hasStringValue(CoverageFields.ANNUAL_BASE_RATE)){
                CoverageFields.setAnnualBaseRate(returnRecord, CoverageFields.getAnnualBaseRate(inputRecord));
            }
            else{
                CoverageFields.setAnnualBaseRate(returnRecord, "0");
            }
            returnRecord.setFieldValue("isManuallyRated", "Y");
        }

        //Set cancellation method code
        if (policyHeader.isShortTermB()) {
            CoverageFields.setCancellationMethodCode(returnRecord, SHORTRATE);
        }

        //Merge ID and Base Record ID
        String riskBaseRecordId = policyHeader.getRiskHeader().getRiskBaseRecordId();
        Record input = policyHeader.toRecord();
        CoverageFields.setProductCoverageCode(input, productCovgCode);
        RiskFields.setRiskBaseRecordId(input, riskBaseRecordId);
        Record idRecord = getCoverageDAO().getCoverageIdAndBaseId(input);
        returnRecord.setFields(idRecord);

        // Set RiskBaseRecordId
        RiskFields.setRiskBaseRecordId(returnRecord, riskBaseRecordId);

        // Set OfficialRecordId
        CoverageFields.setOfficialRecordId(returnRecord, "");

        // Set RiskTypeCode
        RiskFields.setRiskTypeCode(returnRecord, policyHeader.getRiskHeader().getRiskTypeCode());

        // Set TransactionLogId
        TransactionFields.setTransactionLogId(returnRecord, policyHeader.getLastTransactionInfo().getTransactionLogId());

        // Set the initial Coverage Entitlement values
        CoverageEntitlementRecordLoadProcessor.setInitialEntitlementValuesForCoverage(
            this, policyHeader, policyHeader.getScreenModeCode(), returnRecord);

        // Set original value
        origFieldLoadProcessor.postProcessRecord(returnRecord, true);

        // Setup intial row style
        CoverageRowStyleRecordLoadprocessor.setInitialEntitlementValuesForRowStyle(returnRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForCoverage", returnRecord);
        }

        return returnRecord;
    }

    /**
     * Create default coverages for a new risk
     *
     * @param policyHeader the summary policy information
     * @return the number of rows added.
     */
    public int saveAllDefaultCoverage(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllDefaultCoverage", new Object[]{policyHeader});

        // Get all product covreages
        RecordSet productCoverages = loadAllAvailableCoverage(policyHeader);

        // Get default coverages
        RecordSet defaultCoverages = new RecordSet();
        int productCoverageCount = productCoverages.getSize();
        for (int i = 0; i < productCoverageCount; i++) {
            Record productCoverageRecord = productCoverages.getRecord(i);
            if (productCoverageRecord.getBooleanValue("defaultCoverageB").booleanValue() ||
                productCoverageRecord.getBooleanValue("mapDefaultCoverageB").booleanValue()) {
                Record defaultCoverage = getInitialValuesForCoverage(policyHeader, productCoverageRecord);
                defaultCoverages.addRecord(defaultCoverage);
            }
        }

        // Add the PolicyHeader info to each Coverage detail Record
        defaultCoverages.setFieldsOnAll(policyHeader.toRecord(), false);

        // Set the current transaction id on all records
        defaultCoverages.setFieldValueOnAll("transactionLogId", policyHeader.getLastTransactionId());

        // Add the inserted WIP records in batch mode
        defaultCoverages.setFieldValueOnAll("rowStatus", "NEW");
        int updateCount = getCoverageDAO().addAllCoverage(defaultCoverages);

        // Add default objects
        int insertedWipRecordCount = defaultCoverages.getSize();

        if ( !policyHeader.isSkipDefaultComponent() ) {
            // Add default component for each new coverage
            for (int i = 0; i < insertedWipRecordCount; i++) {
                getComponentManager().saveAllDefaultComponent(
                    loadCoverageHeader(policyHeader, CoverageFields.getCoverageId(defaultCoverages.getRecord(i))),
                    defaultCoverages.getRecord(i), ComponentOwner.COVERAGE);
            }
        }

        if ( !policyHeader.isSkipDefaultSubCoverage() ) {
            // Add default coverage class for each new coverage
            for (int i = 0; i < insertedWipRecordCount; i++) {
                getCoverageClassManager().saveAllDefaultCoverageClass(loadCoverageHeader(policyHeader,
                    CoverageFields.getCoverageId(defaultCoverages.getRecord(i))));
            }
        }

        if ( insertedWipRecordCount > 1 && (!policyHeader.isSkipDefaultComponent() || !policyHeader.isSkipDefaultSubCoverage())) {
            loadCoverageHeader(policyHeader, CoverageFields.getCoverageId(defaultCoverages.getFirstRecord()));
        }

        l.exiting(getClass().getName(), "saveAllDefaultCoverage", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Check if prior acts exist
     *
     * @param policyHeader
     * @param inputRecord
     * @return boolean
     */
    public boolean validatePriorActsExist(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePriorActsExist", new Object[]{policyHeader, inputRecord});
        }

        String coverageBaseRecordId = CoverageFields.getCoverageBaseRecordId(inputRecord);

        Record input = policyHeader.toRecord();
        CoverageFields.setCoverageBaseRecordId(input, coverageBaseRecordId);

        boolean isExist = YesNoFlag.getInstance(getCoverageDAO().isPriorActsExist(input)).booleanValue();

        if (isExist) {
            MessageManager.getInstance().addConfirmationPrompt("pm.maintainCoverage.priorActsExists.warning");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePriorActsExist", Boolean.valueOf(isExist));
        }

        return isExist;
    }

    /**
     * Method that evaluates policy business rule for ability to edit the Claims Made date,
     * updating the OasisField to editable if permitted.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return boolean indicating if the claims made date field is editable
     */
    public boolean isClaimsMadeDateEditable(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isClaimsMadeDateEditable", new Object[]{policyHeader});
        }

        boolean isEditAllowed = false;

        if (policyHeader.getScreenModeCode().isOosWIP()) {
            return isEditAllowed;
        }

        try {
            // System parameter to determine if claims made date is ever editable
            String sysParm = SysParmProvider.getInstance().getSysParm("PM_CMDATE_EDT_IBNR_Y", "N");

            if (YesNoFlag.getInstance(sysParm).booleanValue() &&
                policyHeader.getRiskHeader().getRollingIbnrIndicator().booleanValue() ||
                !YesNoFlag.getInstance(sysParm).booleanValue()) {
                isEditAllowed = true;
            }

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if the claims made date is editable.", e);
            l.throwing(getClass().getName(), "isClaimsMadeDateEditable", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isClaimsMadeDateEditable", Boolean.valueOf(isEditAllowed));
        return isEditAllowed;
    }

    /**
     * Method that evaluates policy business rule for ability to enter the annual base rate
     * depending if the rating module is manually rated.
     *
     * @param ratingModuleCode Rating module identifier of the current coverage
     * @return boolean indicating if the coverage is manually rated
     */
    public boolean isManuallyRated(String ratingModuleCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isManuallyRated", new Object[]{ratingModuleCode});
        }

        boolean returnValue = false;

        try {
            if (ratingModuleCode.substring(0, 1).equals("M")) {
                returnValue = true;
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if manually rated", e);
            l.throwing(getClass().getName(), "isManuallyRated", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isManuallyRated", Boolean.valueOf(returnValue));
        return returnValue;
    }

    /**
     * Check if coverage effective to date is editable
     *
     * @param policyHeader
     * @param record
     * @return
     */
    public boolean isEffectiveToDateEditable(PolicyHeader policyHeader, Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isEffectiveToDateEditable", new Object[]{policyHeader, record});
        }

        boolean isEditable = false;
        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
        boolean isDateChangeAllowed = policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue();
        if ((screenModeCode.isOosWIP() && (recordModeCode.isRequest() || (isDateChangeAllowed&&recordModeCode.isTemp())))
            || (!screenModeCode.isOosWIP() && !screenModeCode.isCancelWIP()
            && !screenModeCode.isResinstateWIP() && isDateChangeAllowed)) {
            isEditable = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isEffectiveToDateEditable", Boolean.valueOf(isEditable));
        }
        return isEditable;
    }

    /**
     * Get initial values for OOSE Coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForOoseCoverage(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForOoseCoverage", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();

        // Set Default values
        // coverageId
        Long lCoverageId = getDbUtilityManager().getNextSequenceNo();
        CoverageFields.setCoverageId(outputRecord, String.valueOf(lCoverageId.longValue()));
        // officialRecordId
        CoverageFields.setOfficialRecordId(outputRecord, CoverageFields.getCoverageId(inputRecord));
        // coverageBaseRecordId
        CoverageFields.setCoverageBaseRecordId(outputRecord, CoverageFields.getCoverageBaseRecordId(inputRecord));
        // recordModeCode
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.REQUEST);
        // afterImageRecordB
        CoverageFields.setAfterImageRecordB(outputRecord, YesNoFlag.Y);
        // transactionCode
        TransactionFields.setTransactionCode(outputRecord, policyHeader.getLastTransactionInfo().getTransactionCode());
        // coverageEffectiveFromeDate
        CoverageFields.setCoverageEffectiveFromDate(outputRecord,
            policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        // coverageEffectiveToDate
        String newCovgEffToDate = getTransactionManager().getOoseExpirationDate(policyHeader);
        if (policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue() || CoverageFields.getCoverageStatus(inputRecord).isConverted()) {
            String sCovgEffToDate = CoverageFields.getCoverageEffectiveToDate(inputRecord);
            String sCurTermExpDate = policyHeader.getTermEffectiveToDate();
            Date covgEffToDate = DateUtils.parseDate(sCovgEffToDate);
            Date curTermExpDate = DateUtils.parseDate(sCurTermExpDate);
            if (covgEffToDate.before(curTermExpDate)) {
                newCovgEffToDate = sCovgEffToDate;
            }
        }
        CoverageFields.setCoverageEffectiveToDate(outputRecord, newCovgEffToDate);

        // Set other columns same as the original outputRecord for which Change was invoked
        // policyFormCode
        CoverageFields.setPolicyFormCode(outputRecord, CoverageFields.getPolicyFormCode(inputRecord));
        // ratingModuleCode
        CoverageFields.setRatingModuleCode(outputRecord, getRatingModuleCode(policyHeader, inputRecord));
        // coverageStatus
        CoverageFields.setCoverageStatus(outputRecord, PMStatusCode.PENDING);
        // IBNRCovgB
        CoverageFields.setIBNRCovgB(outputRecord, CoverageFields.getIBNRCovgB(inputRecord));

        // Get the default coverage entitlement values
        CoverageEntitlementRecordLoadProcessor.setInitialEntitlementValuesForCoverage(this, policyHeader,
            policyHeader.getScreenModeCode(), outputRecord);

        // Set Change option to invisible for the new outputRecord
        outputRecord.setFieldValue("isOosChangeAvailable", YesNoFlag.N);
        // Set Delete option to visible for the new outputRecord
        outputRecord.setFieldValue("isDeleteAvailable", YesNoFlag.Y);

        // Setup intial row style
        CoverageRowStyleRecordLoadprocessor.setInitialEntitlementValuesForRowStyle(outputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForOoseCoverage", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Validate for OOSE coverage
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void validateForOoseCoverage(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForOoseCoverage", new Object[]{policyHeader, inputRecord});
        }

        if (policyHeader.getScreenModeCode().isOosWIP() && PMCommonFields.getRecordModeCode(inputRecord).isOfficial()) {
            // check if Change option is available or not
            YesNoFlag isChangeAvailable = YesNoFlag.N;

            String sTransEffDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
            String sEffDate = CoverageFields.getCoverageEffectiveFromDate(inputRecord);
            Date transEffDate = DateUtils.parseDate(sTransEffDate);
            Date effDate = DateUtils.parseDate(sEffDate);

            // 1. If the term displayed is the initial term in which the OOS transaction was initiated
            // 2. the attached risk has a status of 'PENDING' or 'ACTIVE'

            // 3. If the row selected has an effective from date < the transaction effective date
            // 4. The query from Pm_Valid_Oos_Coverage returns 'Y'
            PMStatusCode riskStatus = policyHeader.getRiskHeader().getRiskStatusCode();
            if (policyHeader.isInitTermB()
                && (riskStatus.isPending() || riskStatus.isActive())
                && !transEffDate.before(effDate)) {

                // do query
                Record record = new Record();
                CoverageFields.setCoverageEffectiveFromDate(record, CoverageFields.getCoverageEffectiveFromDate(inputRecord));
                CoverageFields.setCoverageBaseRecordId(record, CoverageFields.getCoverageBaseRecordId(inputRecord));
                boolean isOosCovgValid = YesNoFlag.getInstance(getCoverageDAO().isOosCoverageValid(record)).booleanValue();

                if (isOosCovgValid) {
                    isChangeAvailable = YesNoFlag.Y;
                }
            }

            if (!isChangeAvailable.booleanValue()) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.ooseCovg.changeOption.error");
                throw new ValidationException();
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForOoseCoverage");
        }
    }

    /**
     * Validate all coverages
     *
     * @param policyHeader
     * @param inputRecords
     */
    protected void validateAllCoverage(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllCoverage", new Object[]{inputRecords});
        }

        if (policyHeader.getLastTransactionInfo().getTransactionCode().isOosEndorsement()) {
        // Pre-Oose Change validations
        PreOoseChangeValidator preOoseChangeValidator = new PreOoseChangeValidator(
            null, "coverage", CoverageFields.COVERAGE_ID, CoverageFields.COVERAGE_BASE_RECORD_ID);
        preOoseChangeValidator.validate(inputRecords);
        }

        // Get an instance of the Standard Effective To Date Rule Validator
        StandardEffectiveToDateRecordValidator effToDateValidator =
            new StandardEffectiveToDateRecordValidator(policyHeader,
                CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, CoverageFields.COVERAGE_EFFECTIVE_TO_DATE,
                CoverageFields.COVERAGE_ID, CoverageFields.ORIG_COVERAGE_EFFECTIVE_TO_DATE);

        // Get an instance of the Standard Retroactive Date Rule Validator
        StandardRetroactiveDateRecordValidator retroDateValidator =
            new StandardRetroactiveDateRecordValidator(policyHeader,
                CoverageFields.PRODUCT_COVERAGE_CODE, CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE,
                CoverageFields.RETRO_DATE, CoverageFields.COVERAGE_ID);

        //get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        RecordSet offCoverageRecordSet = new RecordSet();
        boolean isDateChangeAllowed = policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue();
        if (!(policyHeader.getScreenModeCode().isOosWIP())) {
            // Load all coverages from database for given coverage official ID list.
            Record loadOffCoverageInputRecord = new Record();
            CoverageFields.setTableName(loadOffCoverageInputRecord, CoverageFields.COVERAGE_TABLE_NAME);
            List list = CoverageFields.getCoverageManualUpdatableFieldsList();
            CoverageFields.setCoverageFieldsList(loadOffCoverageInputRecord, (String)list.get(0));
            CoverageFields.setCoverageDbFieldsList(loadOffCoverageInputRecord, (String)list.get(1));
            String coverageIds = "";
            String connection = "";

            Iterator it = changedRecords.getRecords();
            while (it.hasNext()) {
                Record r = (Record) it.next();
                RecordMode recordModeCode = PMCommonFields.getRecordModeCode(r);
                connection = StringUtils.isBlank(coverageIds) ? "" : ",";

                if (isDateChangeAllowed) {
                    // If current temp coverage is from official record.
                    if (!StringUtils.isBlank(CoverageFields.getOfficialRecordId(r)) && recordModeCode.isTemp()) {
                        coverageIds = coverageIds + connection + CoverageFields.getOfficialRecordId(r);
                    }
                    else {
                        // If current risk is an official record.
                        if (recordModeCode.isOfficial()) {
                            coverageIds = coverageIds + connection + CoverageFields.getCoverageId(r);
                        }
                    }
                }
            }
            CoverageFields.setCoverageIds(loadOffCoverageInputRecord, coverageIds);

            if (!StringUtils.isBlank(coverageIds)) {
                offCoverageRecordSet = getCoverageDAO().loadAllCoverageByIds(loadOffCoverageInputRecord);
            }
        }

        // Get an instance of the Short Term Effective To Date Rule Validator
        ShortTermEffectiveToDateRecordValidator shortTermEffToDateValidator =
            new ShortTermEffectiveToDateRecordValidator(policyHeader,
                CoverageFields.COVERAGE_ID, CoverageFields.COVERAGE_TABLE_NAME,
                CoverageFields.COVERAGE_EFFECTIVE_TO_DATE, offCoverageRecordSet);

        // Get risk effective date
        Record tempRecord = new Record();
        RiskFields.setRiskBaseRecordId(tempRecord, policyHeader.getRiskHeader().getRiskBaseRecordId());
        RiskFields.setRiskEffectiveFromDate(tempRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        String sRiskEffectiveDate = getCoverageDAO().getRiskContiguousEffectiveDate(tempRecord);
        Date riskEffectiveDate = DateUtils.parseDate(sRiskEffectiveDate);

        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String rowNum = String.valueOf(r.getRecordNumber() + 1);
            String rowId = CoverageFields.getCoverageId(r);
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(r);
            String offRecId = CoverageFields.getOfficialRecordId(r);

            Date coverageExpirationDate = r.getDateValue(CoverageFields.COVERAGE_EFFECTIVE_TO_DATE);
            Date coverageEffectiveDate = r.getDateValue(CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE);

            // Set default value for expireB.
            RiskFields.setExpireB(r, "N");

            //if in oose endorsment
            if (policyHeader.getScreenModeCode().isOosWIP()) {
                if (coverageExpirationDate != null) {
                    if (coverageExpirationDate.before(coverageEffectiveDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.standardEffectiveToDateRecordValidator.rule1.error", new String[]{rowNum},
                            CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, rowId);
                    }

                    Date transDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                    Date polExp = DateUtils.parseDate(policyHeader.getPolicyExpirationDate());
                    if (coverageExpirationDate.before(transDate) || coverageExpirationDate.after(polExp)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.effToDate.oosRange.error",
                            new String[]{FormatUtils.formatDateForDisplay(transDate), FormatUtils.formatDateForDisplay(polExp)},
                            CoverageFields.COVERAGE_EFFECTIVE_TO_DATE, rowId);
                    }
                }
                else {
                    CoverageFields.setCoverageEffectiveToDate(r, CoverageFields.getOrigCoverageEffectiveToDate(r));
                    MessageManager.getInstance().addErrorMessage("pm.standardEffectiveToDateRecordValidator.required.error",
                        new String[]{rowNum}, CoverageFields.COVERAGE_EFFECTIVE_TO_DATE, rowId);
                }
            }
            else {
                effToDateValidator.validate(r);
            }
            r.setFields(policyHeader.toRecord(), false);

            if (MessageManager.getInstance().hasErrorMessages())
                break;
            // Validate for OOSE Coverage
            validateForOoseCoverage(policyHeader, r);

            // Validate all effective to date changes
            if (!StringUtils.isSame(CoverageFields.getCoverageEffectiveToDate(r), CoverageFields.getOrigCoverageEffectiveToDate(r))
                && !isEffectiveToDateEditable(policyHeader, r)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.effectiveToDate.error",
                    new String[]{rowNum}, CoverageFields.COVERAGE_EFFECTIVE_TO_DATE, rowId);
                break; // throw exception directly
            }


            String productCoverageCode = CoverageFields.getProductCoverageCode(r);

            // Newly added coverage effective date is not prior to the risk effective date
            if (r.getUpdateIndicator().equals(UpdateIndicator.INSERTED)) {
                if (coverageEffectiveDate.before(riskEffectiveDate)) {
                    String productCovDesc = "";
                    if (r.hasStringValue("productCoverageDesc")) {
                        productCovDesc = r.getStringValue("productCoverageDesc");
                    }
                    else {
                        productCovDesc = productCoverageCode;
                    }

                    MessageManager.getInstance().addErrorMessage("pm.addCoverage.coverageEffectiveDate.error2",
                        new String[]{rowNum, productCovDesc, FormatUtils.formatDateForDisplay(sRiskEffectiveDate)},
                        CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, rowId);
                }
            }

            // Short term official coverage or temp coverage which is from official expiration date and other fields can
            // NOT be changed in non-out of sequence transaction at the same time.
            if (!(policyHeader.getScreenModeCode().isOosWIP()) && isDateChangeAllowed) {
                if ((!StringUtils.isBlank(offRecId) && recordModeCode.isTemp()) || recordModeCode.isOfficial()) {
                    if (!shortTermEffToDateValidator.validate(r)) {
                        CoverageFields.setCoverageEffectiveToDate(r, CoverageFields.getOrigCoverageEffectiveToDate(r));
                    }
                }
            }

            String policyFormCode = CoverageFields.getPolicyFormCode(r);
            // Standard Retroactive Date Rule
            if (policyFormCode.equals(CM)){
                retroDateValidator.validate(r);
                if (CoverageFields.getIBNRCovgB(r).booleanValue()){
                    validateIbnrCoverageDates(r, rowNum, rowId);
                }
            }

            // Negative annual base rate check
            String sAnnualBaseRate = r.getStringValue(CoverageFields.ANNUAL_BASE_RATE);
            if (!StringUtils.isBlank(sAnnualBaseRate)) {
                Float annualBaseRate = null;
                if (FormatUtils.isFloat(sAnnualBaseRate)) {
                    annualBaseRate = r.getFloatValue(CoverageFields.ANNUAL_BASE_RATE);
                }
                if (annualBaseRate == null || annualBaseRate.floatValue() < 0) {
                    CoverageFields.setAnnualBaseRate(r, CoverageFields.getOrigAnnualBaseRate(r));
                    sAnnualBaseRate = r.getStringValue(CoverageFields.ANNUAL_BASE_RATE);
                    MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.annualBaseRate.error",
                        new String[]{rowNum}, CoverageFields.ANNUAL_BASE_RATE, rowId);
                }
            }

            // If coverage is manually rated, set annual base rate as 0 replacing of null.
            if (YesNoFlag.getInstance(CoverageFields.getIsManuallyRated(r)).booleanValue()) {
                CoverageFields.setAnnualBaseRate(r, StringUtils.isBlank(sAnnualBaseRate) ? "0" : sAnnualBaseRate);
            }

            // Modify Excess Payor
            if (!StringUtils.isSame(CoverageFields.getRatePayorDependCode(r), CoverageFields.getOrigRatePayorDependCode(r))) {
                if (policyHeader.getLastTransactionInfo().getTransactionCode().isNewBus()) {
                    String checkExcessPy = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_CHECK_EXCESS_PY);
                    if (YesNoFlag.getInstance(checkExcessPy).booleanValue()) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.excessPayor.error",
                            new String[]{rowNum}, CoverageFields.RATE_PAYOR_DEPEND_CODE, rowId);
                    }
                }
            }

            // OOSWIP mode
            TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();
            if (transactionCode.isOosEndorsement() && policyFormCode.equals(CM)) {
                String officialRecordId = CoverageFields.getOfficialRecordId(r);
                String sPolicyExpirationDate = policyHeader.getPolicyExpirationDate();
                String sOffRetroDate = getOfficialRetroactiveDate(inputRecords, officialRecordId);
                String sCurRetroDate = CoverageFields.getRetroDate(r);

                // OOSWIP Retroactive Date Validation
                // Step 1: Get prior retroactive date of the coverage
                sOffRetroDate = getCoverageDAO().getRetroactiveDate(r);

                // Step 2 & 2.1: Pre-check before call stored procedure
                boolean dataChangedAllowedB = policyHeader.getRiskHeader().getDateChangeAllowedB().booleanValue();
                RecordMode recordMode = PMCommonFields.getRecordModeCode(r);
                if ((sOffRetroDate == null || !sOffRetroDate.equals(sCurRetroDate)) &&
                    recordMode.isRequest() && !dataChangedAllowedB) {
                    // Step 2.1.1: Call Pm_Validate_Retro_Change to get result
                    //reset result and message
                    int result = -1;
                    String msg = "";
                    Record rc = getCoverageDAO().validationRetroactiveDateForOoswip(policyHeader, r);
                    if (rc != null && rc.getSize() != 0) {
                        result = rc.getIntegerValue(RC).intValue();
                        msg = rc.getStringValue(RMSG);
                    }

                    if (result < 0) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.retroactiveDate.rule1.error",
                            new String[]{rowNum, msg}, CoverageFields.RETRO_DATE, rowId);
                        // stop validating
                        break;
                    }
                }

                // Step 3: Ensure the retroactive date is not changed across terms
                String origRetroDate = CoverageFields.getOrigRetroDate(r);
                if (!StringUtils.isBlank(officialRecordId) && Long.parseLong(officialRecordId) > 0) {
                    if ((!policyFormCode.equalsIgnoreCase(OCCURRENCE) && !StringUtils.isSame(sCurRetroDate, origRetroDate))
                        && !DateUtils.formatDate(coverageExpirationDate).equals(sPolicyExpirationDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.retroactiveDate.rule4.error",
                            new String[]{rowNum, FormatUtils.formatDateForDisplay(sPolicyExpirationDate)}, CoverageFields.RETRO_DATE, rowId);
                    }
                }
            }

            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;
        }

        // Validation Duplicate/overlap Coverage - TEMP records only
        String[] keyFieldNames = new String[]{CoverageFields.PRODUCT_COVERAGE_CODE};
        String[] parmFieldNames = new String[]{CoverageFields.PRODUCT_COVERAGE_DESC};
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
            CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, CoverageFields.COVERAGE_EFFECTIVE_TO_DATE,
            CoverageFields.COVERAGE_ID, "pm.addCoverage.duplicateCoverage.error", keyFieldNames, parmFieldNames);

        RecordSet wipRecords = inputRecords.getSubSet(
            new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP)).
            getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}));
        continuityValidator.validate(wipRecords);

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Coverage data.");
        }
        l.exiting(getClass().getName(), "validateAllCoverage");
    }

    /**
     * @param recordSet
     * @param officialRecordId
     * @return String
     */
    private String getOfficialRetroactiveDate(RecordSet recordSet, String officialRecordId) {
        Iterator it = recordSet.getRecords();
        String sDate = "";
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String id = CoverageFields.getCoverageId(r);

            if (id.equals(officialRecordId)) {
                sDate = CoverageFields.getRetroDate(r);
                break;
            }
        }
        return sDate;
    }

    /**
     * Add All coverage
     *
     * @param policyHeader
     * @param wipRecords
     * @return The count of sucessful saved row.
     */
    protected int addAllCoverage(PolicyHeader policyHeader, RecordSet wipRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllCoverage", new Object[]{wipRecords});

        int updateCount = 0;

        // Add the inserted WIP records in batch mode
        RecordSet insertedWipRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        insertedWipRecords.setFieldValueOnAll("rowStatus", "NEW");
        insertedWipRecords.setFieldsOnAll(policyHeader.toRecord(), false);
        updateCount += getCoverageDAO().addAllCoverage(insertedWipRecords);

        l.exiting(getClass().getName(), "addAllCoverage", new Integer(updateCount));
        return updateCount;
    }

    private String getRatingModuleCode(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRatingModuleCode", new Object[]{inputRecord});
        }

        Record record = new Record();
        TransactionFields.setTransactionLogId(record, policyHeader.getLastTransactionInfo().getTransactionLogId());
        record.setFieldValue("newCovgB", "N");
        record.setFields(inputRecord, false);
        String ratingModuleCode = getCoverageDAO().getRatingModuleCode(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRatingModuleCode", ratingModuleCode);
        }
        return ratingModuleCode;
    }

    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Coverage stored procedure,
     * assuming they all have recordModeCode = TEMP, and were marked for delete.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    public int deleteAllCoverage(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllCoverage", new Object[]{policyHeader, inputRecords});
        int updateCount;

        inputRecords.setFieldsOnAll(policyHeader.toRecord(), false);
        updateCount = getCoverageDAO().deleteAllCoverage(inputRecords);

        l.exiting(getClass().getName(), "deleteAllCoverage", new Integer(updateCount));

        return updateCount;
    }

    /**
     * get a boolean value to indicate if practice state is valid
     *
     * @param policyHeader
     * @param inputRecord
     * @return validate result
     */
    public boolean isPracticeStateValid(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPracticeStateValid", new Object[]{policyHeader, inputRecord});
        }
        boolean isValid = true;

        Record record = new Record();
        record.setFields(inputRecord);
        record.setFields(policyHeader.toRecord(), false);
        String validateResult = getCoverageDAO().getValidatePracticeStateResult(record);
        if (validateResult.equals("N")) {
            isValid = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPracticeStateValid", String.valueOf(isValid));
        }

        return isValid;
    }

    /**
     * check is Similar Coverage Exist
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public boolean isSimilarCoverageExist(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSimilarCoverageExist", new Object[]{policyHeader, inputRecord});
        }
        boolean isExist = false;

        Record record = new Record();
        record.setFields(inputRecord);
        record.setFields(policyHeader.toRecord(), false);
        String validateResult = getCoverageDAO().getCheckSimilarCoverageResult(record);
        if (validateResult.equals("Y")) {
            isExist = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSimilarCoverageExist", String.valueOf(isExist));
        }

        return isExist;
    }


    /**
     * get coverage base record id
     *
     * @param policyHeader
     * @param inputRecord
     * @return coverage base record id
     */
    public String getCoverageBaseId(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageBaseId", new Object[]{policyHeader,inputRecord});
        }
        inputRecord.setFields(policyHeader.toRecord(),false);
        String covgBaseId = getCoverageDAO().getCoverageBaseId(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCoverageBaseId", covgBaseId);
        }

        return covgBaseId;
    }

    /**
     * Check if Prior Acts option is available
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isPriorActAvailable(Record record, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPriorActAvailable", new Object[]{record});
        }
        YesNoFlag isAvailable = YesNoFlag.Y;

        if (record.hasStringValue(CoverageFields.RETRO_DATE) && FormatUtils.isDate(CoverageFields.getRetroDate(record))) {
            Date covgRetro = DateUtils.parseDate(CoverageFields.getRetroDate(record));

            if (!covgRetro.before(DateUtils.parseDate(CoverageFields.getCoverageEffectiveFromDate(record)))) {
                isAvailable = YesNoFlag.N;
            }
        }
        else {
            isAvailable = YesNoFlag.N;
        }


        if (!isPracticeStateValid(policyHeader, record)) {
            isAvailable = YesNoFlag.N;
        }

        YesNoFlag noseCoverageB = CoverageFields.getNoseCoverageB(record);
        if (!noseCoverageB.booleanValue()) {
            isAvailable = YesNoFlag.N;
        }
        else if (SysParmProvider.getInstance().getSysParm(SysParmIds.PM_CHECK_SIMILAR_COVG, "N").equals("N")) {
            boolean isSimilarCovgExist = isSimilarCoverageExist(policyHeader, record);
            if (isSimilarCovgExist) {
                isAvailable = YesNoFlag.N;
            }
        }


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPriorActAvailable", isAvailable);
        }
        return isAvailable;
    }

    /**
     * validate coverage copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllCoverage(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllCoverage", new Object[]{inputRecord});
        }

        String valStatus = getCoverageDAO().validateCopyAllCoverage(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllCoverage", valStatus);
        }

        return valStatus;
    }

    /**
     * Returns a RecordSet loaded with list of source coverages for risk copy all
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param covgGridFields covgGridFields all the coverage gird fields
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllSourceCoverage(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, String covgGridFields) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSourceCoverage", new Object[]{policyHeader,loadProcessor,covgGridFields});
        }


        RecordLoadProcessor lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, new CoverageCopyRecordLoadProcessor(this,covgGridFields));
        RecordSet rs = loadAllCoverage(policyHeader, lp);
        rs = rs.getSubSet(new RecordFilter(CoverageFields.COVERAGE_VERSION_EFFECTIVE_TO_DATE, INFINITY_DATE));
        Iterator iter = rs.getRecords();
        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            rec.setEditIndicator("Y");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSourceCoverage", rs);
        }

        return rs;
    }

    /**
     * delete all copied coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @param covgRs
     */
    public void deleteAllCopiedCoverage(PolicyHeader policyHeader, Record inputRecord, RecordSet covgRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllCopiedCoverage", new Object[]{policyHeader,inputRecord,covgRs});
        }
        //delete all copied coverage, and retrive the toCoverageBaseRecordId
        covgRs.setFieldsOnAll(policyHeader.toRecord(), false);
        covgRs.setFieldsOnAll(inputRecord, false);

        getCoverageDAO().deleteAllCopiedCoverage(covgRs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllCopiedCoverage");
        }

    }

    /**
     * sets the rule for coverage shared limit.
     * @param policyHeader
     * @param inputRecord
     */
    public void setCoverageLimitShared(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setCoverageLimitShared", new Object[]{policyHeader,inputRecord});
        }

        String isCovgLimitShared = getCoverageDAO().getCoverageLimitShared(inputRecord);
        inputRecord.setFieldValue("sharedLimitsB", isCovgLimitShared);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setCoverageLimitShared");
        }
    }

    /**
     * Load all the prior carrier
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPriorCarrier(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorCarrier", new Object[]{inputRecord});
        }

        RecordLoadProcessor recordLoadProcessor = new ExcessCoverageEntitlementRecordLoadProcessor(inputRecord);
        RecordSet rs = getCoverageDAO().loadAllPriorCarrier(inputRecord, recordLoadProcessor);
        Record summaryRecord = rs.getSummaryRecord();

        // Set term effective from/to data since the effectiveFromDate will be used to load Carrier and Broker.
        ExcessCoverageFields.setPolicyId(summaryRecord, ExcessCoverageFields.getPolicyId(inputRecord));
        ExcessCoverageFields.setTransactionId(summaryRecord, inputRecord.getStringValue("lastTransactionId"));
        ExcessCoverageFields.setTermEffectiveFromDate(summaryRecord, ExcessCoverageFields.getTermEffectiveFromDate(inputRecord));
        ExcessCoverageFields.setTermEffectiveToDate(summaryRecord, ExcessCoverageFields.getTermEffectiveToDate(inputRecord));
        ExcessCoverageFields.setExcessCoverageB(summaryRecord, "Y");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPriorCarrier", rs);
        }

        return rs;
    }

    /**
     * Load the current carrier
     *
     * @param inputRecord
     * @return Record
     */
    public Record loadAllCurrentCarrier(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCurrentCarrier", new Object[]{inputRecord});
        }
        Record outputRecord;
        RecordSet rs = getCoverageDAO().loadAllCurrentCarrier(inputRecord);
        if (rs.getSize() > 0) {
            outputRecord = rs.getRecord(0);
            ExcessCoverageFields.setNewCurrentCarrierB(outputRecord, "N");
        }
        else {
            outputRecord = new Record();
            // Default Effective/Expiration/Retro Date to the effective/expiration/effective date of displayed term.
            ExcessCoverageFields.setNewCurrentCarrierB(outputRecord, "Y");
            ExcessCoverageFields.setEffectiveFromDate(outputRecord, ExcessCoverageFields.getTermEffectiveFromDate(inputRecord));
            ExcessCoverageFields.setEffectiveToDate(outputRecord, ExcessCoverageFields.getTermEffectiveToDate(inputRecord));
            ExcessCoverageFields.setExcessRetroDate(outputRecord, ExcessCoverageFields.getTermEffectiveFromDate(inputRecord));
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCurrentCarrier", outputRecord);
        }

        return outputRecord;
    }

    /**
     * Save all parior carrier
     *
     * @param inputRecords
     * @return the number of rows updated
     */
    public int saveAllPriorCarrier(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPriorCarrier", new Object[]{inputRecords});
        }

        boolean currentCarrierChangedB = false;
        Record summaryRecord = inputRecords.getSummaryRecord();
        //  For current carrier, any one of the four fields is not empty, system should validate all the fields.
        //  Can't update the current carrier with the four fields empty, the newCurrentCarrierB is the flag of insert/update.
        if (!YesNoFlag.getInstance(ExcessCoverageFields.getNewCurrentCarrierB(summaryRecord)).booleanValue() ||
            (summaryRecord.hasStringValue(ExcessCoverageFields.CARRIER_ENTITY_ID) && !StringUtils.isBlank(ExcessCoverageFields.getCarrierEntityId(summaryRecord))) ||
            (summaryRecord.hasStringValue(ExcessCoverageFields.BROKEN_ENTITY_ID) && !StringUtils.isBlank(ExcessCoverageFields.getBrokerEntityId(summaryRecord))) ||
            (summaryRecord.hasStringValue(ExcessCoverageFields.EXCESS_LIMIT) && !StringUtils.isBlank(ExcessCoverageFields.getExcessLimit(summaryRecord))) ||
            (summaryRecord.hasStringValue(ExcessCoverageFields.ATTACHEMENT_POINT) && !StringUtils.isBlank(ExcessCoverageFields.getAttachmentPoint(summaryRecord)))) {
            currentCarrierChangedB = true;
        }
        // Validate all the carrier.
        validateAllCarrier(inputRecords, currentCarrierChangedB);

        Record inputRecord = new Record();
        inputRecord.setFields(inputRecords.getSummaryRecord());
        // Set the rowStatus to all records.
        RecordSet changedPriorCarrier = OasisRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);
        // Set policyId and excessCoverageB to all records.
        changedPriorCarrier.setFieldValueOnAll(ExcessCoverageFields.POLICY_ID, ExcessCoverageFields.getPolicyId(inputRecord));
        changedPriorCarrier.setFieldValueOnAll(ExcessCoverageFields.EXCESS_COVERAGE_B, ExcessCoverageFields.getExcessCoverageB(inputRecord));
        int updatedCount = getCoverageDAO().saveAllPriorCarrier(changedPriorCarrier);

        // Transaction Id exists in inputRecord.
        ExcessCoverageFields.setVapPremium(inputRecord, null);
        ExcessCoverageFields.setOccPremium(inputRecord, null);
        if (YesNoFlag.getInstance(ExcessCoverageFields.getNewCurrentCarrierB(inputRecord)).booleanValue()) {
            if (currentCarrierChangedB) {
                ExcessCoverageFields.setHospitalMiscInfoId(inputRecord, getDbUtilityManager().getNextSequenceNo().toString());
                getCoverageDAO().insertCurrentCarrier(inputRecord);
            }
        }
        else {
            inputRecord.setFieldValue("newId", null);
            getCoverageDAO().updateCurrentCarrier(inputRecord);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPriorCarrier", new Integer(updatedCount));
        }

        return updatedCount;
    }

    /**
     * Get initial values for prior carrier
     *
     * @param inputRecord
     * @return Initial values
     */
    public Record getInitialValuesForPriorCarrier(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForPriorCarrier");
        }

        Record outputRecord = new Record();
        ExcessCoverageFields.setPriorCarrierHistoryId(outputRecord, getDbUtilityManager().getNextSequenceNo().toString());
        outputRecord.setFieldValue("isDeleteAvailable", "Y");
        // Default the values of Start/End date.
        if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_PRCR_INT_DTYRMNS1", "N")).booleanValue()) {
            Date termEffFromDate = DateUtils.parseDate(ExcessCoverageFields.getTermEffectiveFromDate(inputRecord));
            int year = DateUtils.getYear(termEffFromDate);
            int month = DateUtils.getMonth(termEffFromDate);
            int day = DateUtils.getDayOfMonth(termEffFromDate);
            String newTermEffDate = DateUtils.formatDate(DateUtils.makeDate((year - 1), month, day)).toString();
            ExcessCoverageFields.setEffectiveStartDate(outputRecord, newTermEffDate);
            ExcessCoverageFields.setEffectiveEndDate(outputRecord, ExcessCoverageFields.getTermEffectiveFromDate(inputRecord));
        }
        else {
            ExcessCoverageFields.setEffectiveStartDate(outputRecord, ExcessCoverageFields.getTermEffectiveFromDate(inputRecord));
            ExcessCoverageFields.setEffectiveEndDate(outputRecord, ExcessCoverageFields.getTermEffectiveToDate(inputRecord));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForPriorCarrier", outputRecord);
        }

        return outputRecord;
    }

    /**
     * Get product coverage type
     *
     * @param productCoverageCode
     * @return String
     */
    public String getProductCoverageType(String productCoverageCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProductCoverageType", new Object[]{productCoverageCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("prodCovgCode", productCoverageCode);
        String coverageType = getCoverageDAO().getProductCoverageType(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProductCoverageType", coverageType);
        }
        return coverageType;
    }

    /**
     * Check if manual excess button enable
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean isManualExcessButtonEnable(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isManualExcessButtonEnable", new Object[]{inputRecord,});
        }

        boolean isEnable = YesNoFlag.getInstance(getCoverageDAO().isManualExcessButtonEnable(inputRecord)).booleanValue();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isManualExcessButtonEnable", Boolean.valueOf(isEnable));
        }
        return isEnable;
    }

    /**
     * Is Valid for Manual Excess Premium
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean isValidForManualExcessPremium(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isValidForManualExcessPremium", new Object[]{policyHeader,});
        }

        boolean isValid = true;
        // Get product notifications
        Record inputRecord = policyHeader.toRecord();
        inputRecord.setFieldValue("notifyLevel", "MXSBTTNCLK");
        try {
            RecordSet rs = getTransactionManager().loadAllProductNotifications(inputRecord);
            // Is invalid if there's message in product notify.
            if (rs.getSize() > 0) {
                isValid = false;
            }
        }
        catch (Exception e) {
            l.throwing(getClass().getName(), "isValidForManualExcessPremium", e);
            // product notification is not configured for MXSBTTNCLK
            isValid = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isValidForManualExcessPremium", Boolean.valueOf(isValid));
        }
        return isValid;
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
            l.entering(getClass().getName(), "isProblemPolicy", new Object[]{policyHeader});
        }

        boolean isProblemPolicy = YesNoFlag.getInstance(getComponentManager().isProblemPolicy(policyHeader)).booleanValue();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isProblemPolicy", Boolean.valueOf(isProblemPolicy));
        }
        return isProblemPolicy;
    }

    /**
     * Add all covg, dependent covg and default components
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     * @param componentInputRecords
     */
    public void addAllCoverage(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords, RecordSet componentInputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addAllCoverage",
                new Object[]{policyHeader, inputRecords, componentInputRecords});
        }
        // Populate all existed coverage to avoid duplicatly adding dependent coverages
        Record depParm = new Record();
        Record tmpRecord = null;
        String codeStr = "";
        // Get all selected coverage list string to set initial values for all new coverages
        String[] selCovgArray = CoverageFields.getParentCovCode(inputRecord).split(",");
        String[] covgArray = null;
        String selCovgStr = null;
        Record record = null;
        RecordSet newInsertedCoverages = new RecordSet();
        for (int i = 0; i < selCovgArray.length; i++) {
            selCovgStr = selCovgArray[i];
            covgArray = selCovgStr.split("@");
            tmpRecord = new Record();
            CoverageFields.setProductCoverageCode(tmpRecord, covgArray[0]);
            CoverageFields.setShortTermB(tmpRecord, covgArray[1]);
            CoverageFields.setCoverageLimitCode(tmpRecord, covgArray[2]);
            CoverageFields.setRetroDate(tmpRecord, covgArray[3]);
            CoverageFields.setProductDefaultSharedLimitB(tmpRecord, covgArray[4]);
            CoverageFields.setAnnualBaseRate(tmpRecord, covgArray[5].trim());

            // Get initial values for each selected coverage
            tmpRecord = getInitialValuesForCoverage(policyHeader, tmpRecord);
            record = new Record();
            setReturnRecord(tmpRecord, record, inputRecords);
            inputRecords.addRecord(record);
            newInsertedCoverages.addRecord(record);
        }

        // Set select coverage code as parent covg.
        // Load all dependent coverages for the select coverages and set initial values.
        RecordSet newInsertedCoveragesImage = new RecordSet();
        newInsertedCoveragesImage.addRecords(newInsertedCoverages);

        int newInsertedRecordsSize = newInsertedCoveragesImage.getSize();
        for (int i = 0; i < newInsertedRecordsSize; i++) {
            Record tempInputRec = newInsertedCoveragesImage.getRecord(i);
            codeStr = CoverageFields.getProductCoverageCode(tempInputRec);
            CoverageFields.setProductCoverageCode(depParm, codeStr);
            RecordSet depCovg = loadDependentCoverage(policyHeader, depParm);
            int depCovgSize = 0;
            if (depCovg != null) {
                depCovgSize = depCovg.getSize();
            }
            for (int j = 0; j < depCovgSize; j++) {
                Record tempDepCovgRec = depCovg.getRecord(j);
                //To check the coverage is already added or not.
                RecordSet tempInputSet = inputRecords.getSubSet(new RecordFilter(CoverageFields.PRODUCT_COVERAGE_CODE,
                    CoverageFields.getProductCoverageCode(tempDepCovgRec)), false);
                if (tempInputSet == null || tempInputSet.getSize() == 0) {
                    record = getInitialValuesForCoverage(policyHeader, depCovg.getRecord(j));
                    Record depRecord = new Record();
                    setReturnRecord(record, depRecord, inputRecords);
                    inputRecords.addRecord(depRecord);
                    newInsertedCoverages.addRecord(depRecord);
                }
            }
        }

        //Loop the recordSet of new selected product coverage and default dependent coverages.
        Iterator covgIt = newInsertedCoverages.getRecords();
        RecordSet defaultComponents = new RecordSet();
        while (covgIt.hasNext()) {
            Record coverageRec = (Record) covgIt.next();
            // Get all available components
            RecordSet compRs = getComponentManager().loadAllAvailableComponent(policyHeader, coverageRec);
            // Get default Components
            Iterator compIt = compRs.getRecords();
            while (compIt.hasNext()) {
                Record compRec = (Record) compIt.next();
                if (compRec.getBooleanValue("defaultComponentB").booleanValue() ||
                    compRec.getBooleanValue("mapDefaultCoverageCompB").booleanValue()) {

                    // Add componentId
                    ComponentFields.setProductCovComponentId(
                        coverageRec, ComponentFields.getProductCovComponentId(compRec));
                    ComponentFields.setComponentOwner(coverageRec, ComponentOwner.COVERAGE.getOwnerName());
                    Record defaultComponent = getComponentManager().getInitialValuesForAddComponent(policyHeader, coverageRec);
                    // Set coverage base record Id
                    ComponentFields.setCoverageBaseRecordId(defaultComponent, CoverageFields.getCoverageBaseRecordId(coverageRec));
                    defaultComponents.addRecord(defaultComponent);
                }
            }
        }

        // add default components to component input record
        Iterator defaultCompIt = defaultComponents.getRecords();
        Record newComponent = null;
        while (defaultCompIt.hasNext()) {
            Record defaultCompRec = (Record) defaultCompIt.next();
            // Check whether the default component is already added or not, if it is added, then ignore it to avoid
            // the duplicated component issue
            Iterator componentInputRecIt = componentInputRecords.getRecords();
            boolean newComponentFlag = true;
            while (componentInputRecIt.hasNext()) {
                Record existCompRecord = (Record) componentInputRecIt.next();
                //if the default component already added for the coverage before, then no need to add it again.
                if (ComponentFields.getCoverageBaseRecordId(existCompRecord).equals(ComponentFields.getCoverageBaseRecordId(defaultCompRec)) &&
                    ComponentFields.getCoverageComponentCode(existCompRecord).equals(ComponentFields.getCoverageComponentCode(defaultCompRec)) &&
                    !ComponentFields.getEffectiveFromDate(existCompRecord).equals(ComponentFields.getEffectiveToDate(existCompRecord))) {
                    newComponentFlag = false;
                    break;
                }
            }
            // if the newComponentFlag is true means the default component hasn't been added before.
            if (newComponentFlag) {
                newComponent = new Record();
                Iterator columnNameIt = componentInputRecords.getFieldNames();
                setReturnRecord(defaultCompRec, newComponent, componentInputRecords);
                componentInputRecords.addRecord(newComponent);
            }
        }


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllCoverage");
        }
    }

    /**
     * addAllComponent
     *
     * @param policyHeader
     * @param inputRecord
     */
    public String addAllComponent(PolicyHeader policyHeader, Record inputRecord, RecordSet componentInputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addAllComponent",
                new Object[]{policyHeader, inputRecord});
        }
        String selectedCompStr = inputRecord.getStringValue("selectedComponentCode");
        Record record = null;
        String parentCoverageId = "";
        String[] selectedCompArray = selectedCompStr.split(",");
        for (int i = 0; i < selectedCompArray.length; i++) {
            Record compRecord = new Record();
            if (selectedCompArray[i] != null) {
                String[] compArray = selectedCompArray[i].split("@");
                ComponentFields.setProductCovComponentId(compRecord, compArray[0].trim());
                CoverageFields.setProductCoverageCode(compRecord, compArray[1].trim());
                CoverageFields.setCoverageBaseRecordId(compRecord, compArray[2].trim());
                CoverageFields.setCoverageBaseEffectiveFromDate(compRecord, compArray[3].trim());
                CoverageFields.setCoverageBaseEffectiveToDate(compRecord, compArray[4].trim());
                ComponentFields.setComponentOwner(compRecord, compArray[5].trim());
                RiskFields.setRiskId(compRecord, compArray[6].trim());
                CoverageFields.setCoverageEffectiveToDate(compRecord, compArray[7].trim());
                CoverageFields.setCoverageStatus(compRecord, compArray[8].trim());
                CoverageFields.setLatestCoverageEffectiveToDate(compRecord, compArray[9].trim());
                ComponentFields.setParentCoverageComponentCode(compRecord, compArray[10].trim());
                CoverageFields.setCoverageId(compRecord, compArray[11].trim());
                CoverageFields.setCoverageEffectiveFromDate(compRecord, compArray[12].trim());
                parentCoverageId = compArray[11].trim();
            }
            Record initialedRecord = getComponentManager().getInitialValuesForAddComponent(policyHeader, compRecord);
            CoverageFields.setParentCoverageId(initialedRecord, CoverageFields.getCoverageId(compRecord));

            record = new Record();
            setReturnRecord(initialedRecord, record, componentInputRecords);
            componentInputRecords.addRecord(record);

            RecordSet dependentCompSet = getComponentManager().loadDependentComponent(policyHeader, compRecord,
                DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
            for (int j = 0; j < dependentCompSet.getSize(); j++) {
                Record dependentRec = dependentCompSet.getRecord(j);
                dependentRec.setFields(compRecord, false);
                Record depInitialedRec = depInitialedRec = getComponentManager().getInitialValuesForAddComponent(policyHeader, dependentRec);
                record = new Record();
                setReturnRecord(depInitialedRec, record, componentInputRecords);
                componentInputRecords.addRecord(record);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllComponent");
        }
        return parentCoverageId;
    }

    /**
     * Validate all the carrier.
     *
     * @param inputRecords
     * @param currentCarrierChangedB
     */
    protected void validateAllCarrier(RecordSet inputRecords, boolean currentCarrierChangedB) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePriorActsExist", new Object[]{inputRecords});
        }

        Record summaryRecord = inputRecords.getSummaryRecord();
        // Validate the prior carrier.
        for (int i = 0; i < inputRecords.getSize(); i++) {
            Record record = inputRecords.getRecord(i);
            String rowNum = String.valueOf(record.getRecordNumber() + 1);
            String rowId = ExcessCoverageFields.getPriorCarrierHistoryId(record);
            // Validate the required fields.
            if(!record.hasField(ExcessCoverageFields.ENTITY_ID) || StringUtils.isBlank(ExcessCoverageFields.getEntityId(record))){
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.prior.carrier.required", new String[]{rowNum}, ExcessCoverageFields.ENTITY_ID, rowId);
            }

            if(!record.hasField(ExcessCoverageFields.EFFECTIVE_START_DATE) || StringUtils.isBlank(ExcessCoverageFields.getEffectiveStartDate(record))){
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.prior.startDate.required", new String[]{rowNum}, ExcessCoverageFields.EFFECTIVE_START_DATE, rowId);
            }

            if(!record.hasField(ExcessCoverageFields.EFFECTIVE_END_DATE) || StringUtils.isBlank(ExcessCoverageFields.getEffectiveEndDate(record))){
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.prior.endDate.required", new String[]{rowNum}, ExcessCoverageFields.EFFECTIVE_END_DATE, rowId);
            }
            // Throw validation exception if there is any error message.
            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("All the fields are required.");
            }

            if (record.hasField(ExcessCoverageFields.LIMIT_INCIDENT)) {
                String limitString = ExcessCoverageFields.getLimitIncident(record);
                if (!StringUtils.isBlank(limitString)) {
                    if(Float.parseFloat(limitString) < 0){
                        MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.prior.limitLessZero", new String[]{rowNum},
                            ExcessCoverageFields.LIMIT_INCIDENT, rowId);
                        break;
                    }
                    else if(!StringUtils.decimalPlaceCheck(limitString,2)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.prior.limit.decimalPlace.error", new String[]{rowNum},
                            ExcessCoverageFields.LIMIT_INCIDENT, rowId);
                        break;
                    }
                }
            }

            Date effDate = DateUtils.parseDate(ExcessCoverageFields.getEffectiveStartDate(record));
            Date expDate = DateUtils.parseDate(ExcessCoverageFields.getEffectiveEndDate(record));
            if (effDate.after(expDate)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.prior.date.rang.error", new String[]{rowNum},
                    ExcessCoverageFields.EFFECTIVE_START_DATE, rowId);
                break;
            }

            for (int j = i + 1; j < inputRecords.getSize(); j++) {
                Record innerRecord = inputRecords.getRecord(j);
                String rowNum2 = String.valueOf(innerRecord.getRecordNumber() + 1);
                if (ExcessCoverageFields.getEntityId(record).equals(ExcessCoverageFields.getEntityId(innerRecord))) {
                    Date innerEffDate = DateUtils.parseDate(ExcessCoverageFields.getEffectiveStartDate(innerRecord));
                    Date innerExpDate = DateUtils.parseDate(ExcessCoverageFields.getEffectiveEndDate(innerRecord));
                    if (innerEffDate.after(innerExpDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.prior.date.rang.error", new String[]{rowNum},
                            ExcessCoverageFields.EFFECTIVE_START_DATE, rowId);
                        break;
                    }
                    if ((innerEffDate.after(effDate) && innerEffDate.before(expDate))||
                        (innerExpDate.after(effDate) && innerExpDate.before(expDate))||
                        (effDate.after(innerEffDate) && effDate.before(innerExpDate))||
                        (expDate.after(innerEffDate) && expDate.before(innerExpDate))||
                        (innerEffDate.equals(effDate) && innerExpDate.equals(expDate))) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.prior.periodOverlap",
                            new String[]{rowNum, rowNum2}, ExcessCoverageFields.EFFECTIVE_START_DATE, rowId);
                        break;
                    }
                }
            }
            // Throw validation exception if there is any error message.
            if (MessageManager.getInstance().hasErrorMessages())
                throw new ValidationException("The prior carrier is invalid.");
        }

        if(currentCarrierChangedB){

            // Validate the required fields.
            if(!summaryRecord.hasField(ExcessCoverageFields.CARRIER_ENTITY_ID) || StringUtils.isBlank(ExcessCoverageFields.getCarrierEntityId(summaryRecord))){
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.carrier.required", ExcessCoverageFields.CARRIER_ENTITY_ID);
            }

            if(!summaryRecord.hasField(ExcessCoverageFields.BROKEN_ENTITY_ID) || StringUtils.isBlank(ExcessCoverageFields.getBrokerEntityId(summaryRecord))){
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.broker.required", ExcessCoverageFields.BROKEN_ENTITY_ID);
            }

            if(!summaryRecord.hasField(ExcessCoverageFields.EXCESS_LIMIT) || StringUtils.isBlank(ExcessCoverageFields.getExcessLimit(summaryRecord))){
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.limit.required", ExcessCoverageFields.EXCESS_LIMIT);
            }

            if(!summaryRecord.hasField(ExcessCoverageFields.ATTACHEMENT_POINT) || StringUtils.isBlank(ExcessCoverageFields.getAttachmentPoint(summaryRecord))){
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.point.required", ExcessCoverageFields.ATTACHEMENT_POINT);
            }

            if(!summaryRecord.hasField(ExcessCoverageFields.EFFECTIVE_FROM_DATE) || StringUtils.isBlank(ExcessCoverageFields.getEffectiveFromDate(summaryRecord))){
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.startDate.required", ExcessCoverageFields.EFFECTIVE_FROM_DATE);
            }

            if(!summaryRecord.hasField(ExcessCoverageFields.EFFECTIVE_TO_DATE) || StringUtils.isBlank(ExcessCoverageFields.getEffectiveToDate(summaryRecord))){
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.endDate.required", ExcessCoverageFields.EFFECTIVE_TO_DATE);
            }

            if(!summaryRecord.hasField(ExcessCoverageFields.EXCESS_RETRO_DATE) || StringUtils.isBlank(ExcessCoverageFields.getExcessRetroDate(summaryRecord))){
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.retroactive.required", ExcessCoverageFields.EXCESS_RETRO_DATE);
            }

            // Throw validation exception if there is any error message.
            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("All the fields are required.");
            }

            // Validate the current carrier.
            Date effFromDate = DateUtils.parseDate(ExcessCoverageFields.getEffectiveFromDate(summaryRecord));
            Date effToDate = DateUtils.parseDate(ExcessCoverageFields.getEffectiveToDate(summaryRecord));
            if (effFromDate.after(effToDate)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.date.rang.error",
                    ExcessCoverageFields.EFFECTIVE_FROM_DATE);
            }
            // 4.1 current carrier data (1)
            if (summaryRecord.hasField(ExcessCoverageFields.EXCESS_LIMIT)) {
                String limitString = ExcessCoverageFields.getExcessLimit(summaryRecord);
                if (Float.parseFloat(limitString) < 0) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.limitLessZero");
                }
                else if (!StringUtils.decimalPlaceCheck(limitString, 2)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.limit.decimalPlace.error");
                }
            }
            // 4.1 current carrier data (2)
            if (summaryRecord.hasField(ExcessCoverageFields.ATTACHEMENT_POINT)) {
                String pointString = ExcessCoverageFields.getAttachmentPoint(summaryRecord);
                if (Float.parseFloat(pointString) < 0) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.pointLessZero");
                }
                else if (!StringUtils.decimalPlaceCheck(pointString, 2)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.point.decimalPlace.error");
                }
            }
            // 4.1 current carrier data (3)
            if (summaryRecord.hasField(ExcessCoverageFields.EXCESS_RETRO_DATE)
                && summaryRecord.hasField(ExcessCoverageFields.TERM_EFFECTIVE_FROM_DATE)) {
                Date termEffFromDate = DateUtils.parseDate(ExcessCoverageFields.getTermEffectiveFromDate(summaryRecord));
                Date retroDate = DateUtils.parseDate(ExcessCoverageFields.getExcessRetroDate(summaryRecord));
                if (retroDate.after(termEffFromDate)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainExcessCoverage.currentCarrier.retroDateError");
                }
            }
            // Throw validation exception if there is any error message.
            if (MessageManager.getInstance().hasErrorMessages())
                throw new ValidationException("The current carrier is invalid.");

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePriorActsExist");
        }
    }


    /**
     * Generate the new coverage id.
     * @return
     */
    public String getCoverageSequenceId() {
        Long lCoverageId = getDbUtilityManager().getNextSequenceNo();
        return String.valueOf(lCoverageId.longValue());
    }
    /**
     * Check if add coverage allowed
     *
     * @param record
     * @return
     */
    public YesNoFlag isAddCoverageAllowed(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "IsAddCoverageAllowed", record);
        }
        YesNoFlag result = YesNoFlag.getInstance(getCoverageDAO().isAddCoverageAllowed(record));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "IsAddCoverageAllowed", result);
        }
        return result;
    }

    /**
     * Set up returned record for display using
     *
     * @param inputRecord
     * @param returnRecord
     * @param inputRecords
     * @return record
     */
    private void setReturnRecord(Record inputRecord, Record returnRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setReturnRecord", inputRecord);
        }

        String columnName = null;
        Iterator iter = inputRecords.getFieldNames();
        while (iter.hasNext()) {
            columnName = (String) iter.next();
            // Set empty value for all other columns that are not initialized
            if (inputRecord.hasField(columnName)) {
                returnRecord.setFieldValue(columnName, inputRecord.getStringValue(columnName));
            }
            else {
                returnRecord.setFieldValue(columnName, null);
            }
        }
        // Set indicators
        returnRecord.setDisplayIndicator(YesNoFlag.Y);
        returnRecord.setEditIndicator(YesNoFlag.Y);
        returnRecord.setUpdateIndicator(UpdateIndicator.INSERTED);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setReturnRecord", null);
        }

    }

    /**
     * Returns a RecordSet loaded with list of new copied CM coverages
     * coverage information.
     * <p/>
     *
     * @param inputRecord  record with new copied CM coverage IDs.
     * @return RecordSet a RecordSet loaded with list of new copied CM coverages.
     */
    public RecordSet loadAllNewCopiedCMCoverage(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllNewCopiedCMCoverage", new Object[]{inputRecord});
        }

        RecordSet rs = getCoverageDAO().loadAllNewCopiedCMCoverage(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllNewCopiedCMCoverage", rs);
        }

        return rs;
    }

    /**
     * Save all retro date of new copied CM coverages
     *
     * @param inputRecords
     */
    public int  saveAllRetroDate(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllRetroDate", new Object[]{inputRecords});

        validateAllRetrodate(inputRecords);
        int updateCount = getCoverageDAO().saveAllRetroDate(inputRecords);

        l.exiting(getClass().getName(), "saveAllRetroDate");

        return updateCount;
    }

    /**
     * validate that all retro date of new copied CM coverages
     *
     * @param inputRecords
     */
    protected void validateAllRetrodate(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllRetrodate", new Object[]{inputRecords});
        }

        Iterator iter = inputRecords.getRecords();
        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            String rowId = rec.getRowId();
            String rowNum = String.valueOf(rec.getRecordNumber() + 1);

            if (!rec.hasStringValue(CoverageFields.RETRO_DATE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRetroDate.retroDate.required.error",
                    new String[]{rowNum}, CoverageFields.RETRO_DATE, rowId);

            }
            else {
                if (DateUtils.parseDate(CoverageFields.getCoverageEffectiveFromDate(rec)).before(DateUtils.parseDate(CoverageFields.getRetroDate(rec)))){
                    MessageManager.getInstance().addErrorMessage("pm.maintainRetroDate.retroGreaterEff.error",
                        new String[]{rowNum}, CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, rowId);
                }
            }
        }
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid retroactive data.");
        }

        l.exiting(getClass().getName(), "validateAllRetrodate");
    }

    /**
     * validate all IBNR coverage dates of CM IBNR coverages
     *
     * @param inputRecord
     */
    protected void validateIbnrCoverageDates(Record inputRecord, String rowNum, String rowId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateIbnrCoverageDates", new Object[]{inputRecord});
        }

        if (StringUtils.isBlank(CoverageFields.getIBNRCovgEffectiveFromDate(inputRecord))){
            MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.ibnrEffFromDate.required.error",
                new String[]{rowNum}, CoverageFields.IBNR_COVG_EFFECTIVE_FROM_DATE, rowId);
        }

        if (StringUtils.isBlank(CoverageFields.getIBNRCovgEffectiveToDate(inputRecord))){
            MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.ibnrEffToDate.required.error",
                new String[]{rowNum}, CoverageFields.IBNR_COVG_EFFECTIVE_TO_DATE, rowId);
        }

        if (!StringUtils.isBlank(CoverageFields.getRetroDate(inputRecord))
            && !StringUtils.isBlank(CoverageFields.getIBNRCovgEffectiveFromDate(inputRecord))
            && DateUtils.parseDate(CoverageFields.getRetroDate(inputRecord)).after(DateUtils.parseDate(CoverageFields.getIBNRCovgEffectiveFromDate(inputRecord)))){
            // The retroactive date of the IBNR coverage should be prior to or equal to the original IBNR effective from date.
            MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.ibnrEffFromDateAfterRetroDate.error",
                new String[]{rowNum}, CoverageFields.IBNR_COVG_EFFECTIVE_FROM_DATE + CoverageFields.RETRO_DATE, rowId);
        }

        if (!StringUtils.isBlank(CoverageFields.getIBNRCovgEffectiveFromDate(inputRecord))
            && !StringUtils.isBlank(CoverageFields.getIBNRCovgEffectiveToDate(inputRecord))
            && !DateUtils.parseDate(CoverageFields.getIBNRCovgEffectiveToDate(inputRecord)).after(DateUtils.parseDate(CoverageFields.getIBNRCovgEffectiveFromDate(inputRecord)))) {
            // The original IBNR effective from date should be prior to the original IBNR effective to date.
            MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.ibnrEffFromDateBeforeIbnrEffToDate.error",
                new String[]{rowNum}, CoverageFields.IBNR_COVG_EFFECTIVE_FROM_DATE + CoverageFields.IBNR_COVG_EFFECTIVE_TO_DATE, rowId);
        }

        if (!StringUtils.isBlank(CoverageFields.getIBNRCovgEffectiveToDate(inputRecord))
            && !StringUtils.isBlank(CoverageFields.getCoverageEffectiveFromDate(inputRecord))
            && DateUtils.parseDate(CoverageFields.getIBNRCovgEffectiveToDate(inputRecord)).after(DateUtils.parseDate(CoverageFields.getCoverageEffectiveFromDate(inputRecord)))) {
            // The original IBNR efffective to date should be prior to or equal to the IBNR coverage effective date.
            MessageManager.getInstance().addErrorMessage("pm.maintainCoverage.ibnrEffToDateBeforeCovgEffDate.error",
                new String[]{rowNum}, CoverageFields.IBNR_COVG_EFFECTIVE_TO_DATE + CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, rowId);
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid IBNR coverage dates.");
        }

        l.exiting(getClass().getName(), "validateIbnrCoverageDates");
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getCoverageDAO() == null)
            throw new ConfigurationException("The required property 'coverageDAO' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        if (getPmDefaultManager() == null)
            throw new ConfigurationException("The required property 'pmDefaultManager' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
    }

    public CoverageClassManager getCoverageClassManager() {
        return m_coverageClassManager;
    }

    public void setCoverageClassManager(CoverageClassManager coverageClassManager) {
        m_coverageClassManager = coverageClassManager;
    }

    public CoverageManagerImpl() {
    }

    public CoverageDAO getCoverageDAO() {
        return m_coverageDAO;
    }

    public void setCoverageDAO(CoverageDAO coverageDAO) {
        m_coverageDAO = coverageDAO;
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

    public PMDefaultManager getPmDefaultManager() {
        return m_pmDefaultManager;
    }

    public void setPmDefaultManager(PMDefaultManager pmDefaultManager) {
        m_pmDefaultManager = pmDefaultManager;
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    protected static final String PRODUCT_COVERAGE_CODE = "PRODUCT_COVERAGE_CODE";
    protected static final String COVG_TAB = "COVG_TAB";
    protected static final String RC = "rc";
    protected static final String RMSG = "rmsg";
    protected static final String OOSENDORSE_TRANSACTION_CODE = "OOSENDORSE";
    protected static final String OCCURRENCE = "OCCURRENCE";
    protected static final String CM = "CM";
    protected static final String SHORTRATE = "SHORTRATE";
    protected static final String SAVE_PROCESSOR = "CoverageManager";
    protected static final String INFINITY_DATE = "01/01/3000";
    
    private CoverageDAO m_coverageDAO;
    private TransactionManager m_transactionManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private PMDefaultManager m_pmDefaultManager;
    private ComponentManager m_componentManager;
    private CoverageClassManager m_coverageClassManager;
    private DBUtilityManager m_dbUtilityManager;
    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{CoverageFields.ANNUAL_BASE_RATE, CoverageFields.RETRO_DATE,
            CoverageFields.COVERAGE_EFFECTIVE_TO_DATE, CoverageFields.RATE_PAYOR_DEPEND_CODE});
}
