package dti.pm.coverageclassmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.AppException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.error.ValidationException;
import dti.oasis.error.ExceptionHelper;
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
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.core.data.CommonTabsEntitlementRecordLoadProcessor;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.core.data.RowAccessorRecordLoadProcessor;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.core.struts.AddAuditHistoryIndLoadProcessor;
import dti.pm.coverageclassmgr.CoverageClassFields;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.pm.coverageclassmgr.dao.CoverageClassDAO;
import dti.pm.pmdefaultmgr.PMDefaultManager;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.Term;
import dti.pm.policymgr.service.RiskInquiryFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.transactionmgr.transaction.dao.TransactionDAO;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import dti.pm.validationmgr.impl.StandardEffectiveToDateRecordValidator;
import dti.pm.validationmgr.impl.StandardRetroactiveDateRecordValidator;
import dti.pm.validationmgr.impl.PreOoseChangeValidator;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.riskmgr.RiskFields;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of CoverageClassManager Interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 8, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/13/2007       Joe         Add parameter RecordLoadProcessor for method loadAllAvailableCoverageClass()
 * 02/06/2008       fcb         getInitialValuesForCoverageClass: retro date defaulted to coverage retro date
 *                              independently from coverage part. When CP is implemented, additional logic
 *                              might apply. Also, added RETRO_DATE_EDITABLE.
 * 03/01/2010       fcb         104191: passed transaction id to getDefaultLevel call.
 * 06/28/2010       syang       109288 - Modified saveAllCoverageClass to add "transEffectiveFromDate" to OOSE records.
 * 09/07/2010       dzhang      108261 - Add parameter covgClassGridFields for methoed loadAllSourceCoverageClass().
 * 09/20/2010       syang       Issue 110819 - Added FilterOfficialRowForEndquoteRecordLoadProcessor to loadAllCoverageClass().
 * 06/08/2011       wqfu        121349 - Modified getInitialValuesForCoverageClass for short term coverage class.
 *                              add isAddCoverageClassAllowed to check if add coverage class is allowed.
 * 07/20/2011       syang       121208 - Modified getInitialValuesForCoverageClass() to overwrite the field exposureUnit.
 * 07/27/2011       wqfu        122483 - Modified getInitialValuesForCoverageClass to set correct default value for field
 *                              isRetroDateEditable according to current coverage type.
 * 05/10/2011       wqfu        102874 - Modified validateAllCoverageClass to set exposure unit as O replace of null
 *                              to avoid rating exception.
 * 08/30/2011       ryzhao      124458 - Modified validateAllCoverageClass to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 09/06/2011       wfu         124768 - Modified validateAllCoverageClass to set annual base rate as O replace of null.
 * 09/21/2011       ryzhao      124862 - 1) Modified validateAllCoverageClass to check if exposure unit has more than two
 *                                       decimal places.
 *                                       2) Added decimalPlaceCheck().
 * 09/22/2011       ryzhao      124862 - Refactor all the codes by moving method decimalPlaceCheck() into StringUtils.java.
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 05/29/2012       jshen       133797 - Set isManuallyRated to N if the coverage class is not manually rated.
 * 07/19/2012       awu         134738 - 1. Modified validateAllCoverageClass to check the expiration date is null or not.
 *                                       2. Added the AddOrigFieldsRecordLoadProcessor to get out the original expiration date.
 * 07/24/2012       awu         129250 - Added processAutoSaveAllCoverageClass(), processSaveAllCoverageClassData();
 *                                       Modified processSaveAllCoverageClass() to call processSaveAllCoverageClassData().
 * 08/30/2012       adeng       136541 - Modified loadAllCoverageClass() to pass in the return code of cancelwip rule to new constructor
 *                                       of CoverageClassEntitlementRecordLoadProcessor when create the instance during Cancel WIP.
 * 01/04/2013       xnie        140557 - Corrected incorrect value of variable PRODUCT_COVERAGE to 'PRODUCT_COVERAGE_CODE'.
 * 01/22/2013       tcheng      140034 - Modified loadAllSourceCoverageClass() to filter duplicate coverage class on Copy All Page.
 * 02/20/2013       tcheng      141855 - 1. Modified validateAllCoverageClass() to validate coverage class effective to date correctly in oose endorsment.
 *                                       2. Modified getInitialValuesForCoverageClass() to set the open date to policy expiration date.
 * 04/26/2013       awu         141758 - Added addAllCoverageClass(), setReturnRecord().
 * 05/24/2013       xnie        142949 - Added validateCopyAllCoverageClass().
 * 09/17/2013       xnie        146452 - Modified saveAllCoverageClass() to remove the logic which set record mode code
 *                                       to 'TEMP'. The record mode code have no further act, but cause problem.
 * 10/02/2013       fcb         145725 - used PolicyHeader to get/set cancWipRule
 *                                     - PreOoseChangeValidator called only for OOSE transactions.
 * 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Sub_Covg
 * 09/16/2014       awu         157552 - 1. Modified saveAllCoverageClass to add duplication validation.
 *                                       2. Added setInputForDuplicateValidation, handleDuplicateValidation.
 * 11/06/2014       awu         157552 - Modified setInputForDuplicateValidation to remove the riskBaseId and parentCoverageBaseId.
 * 11/20/2014       awu         154316 - Modified loadAllCoverageClassForWs to add the origFieldLoadProcessor.
 * 12/02/2014       jyang       158858 - Modified loadAllSourceCoverageClass, set the edit_indicator to 'Y' for all copy from coverage classes.
 * 04/16/2015       jyang       162312 - Modified loadAllSourceCoverageClass, revised the change of 158858.
 * 06/11/2015       tzeng       163657 - Modified getInitialValuesForCoverageClass() to transfer the merge default value
 *                                       from Pm_Default.Get_Level_Default step before merge from business rule step.
 * 08/04/2015       awu         164918 - Modified saveAllCoverageClass to call deleteAllCoverageClass before addAllCoverageClass.
 * ---------------------------------------------------
 */

public class CoverageClassManagerImpl implements CoverageClassManager, CoverageClassSaveProcessor {

    /**
     * Returns a RecordSet loaded with list of available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllCoverageClass(PolicyHeader policyHeader) {
        return loadAllCoverageClass(policyHeader, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
    }

    /**
     * Returns a RecordSet loaded with list of available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available coverage classes.
     */
    public RecordSet loadAllCoverageClass(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageClass", new Object[]{policyHeader});
        }
        Record inputRecord = policyHeader.toRecord();
        // Fix issue 94317. The riskBaseRecordId(it was added to fix the issue 78329) is only for loading coverages for risk copy all,
        // it should be null when loading coverage class for a selected coverage.
        if(loadProcessor != null && loadProcessor instanceof DefaultRecordLoadProcessor){
           RiskFields.setRiskBaseRecordId(inputRecord, null);
        }
        RecordMode recordModeCode = RecordMode.TEMP;
        PolicyViewMode viewMode=policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (viewMode.isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        PMCommonFields.setRecordModeCode(inputRecord, recordModeCode);

        Transaction trans = policyHeader.getLastTransactionInfo();
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "loadAllCoverageClass", "trans = " + trans);
        }

        RecordLoadProcessor rowAccessorLP = new RowAccessorRecordLoadProcessor(
            CoverageClassFields.COVERAGE_CLASS_ID, CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_FROM_DATE,
            CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_TO_DATE, policyHeader,
            policyHeader.getScreenModeCode());
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, rowAccessorLP);

        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        YesNoFlag cancelWipEdit = null;
        if (screenModeCode.isCancelWIP()) {
            cancelWipEdit = isCancWipEdit(policyHeader);
        }
        CoverageClassEntitlementRecordLoadProcessor covgEntitleRLP =
            new CoverageClassEntitlementRecordLoadProcessor(this, policyHeader, screenModeCode, cancelWipEdit);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, covgEntitleRLP);

        CommonTabsEntitlementRecordLoadProcessor commonTabsLP = new CommonTabsEntitlementRecordLoadProcessor(policyHeader);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, commonTabsLP);
        loadProcessor=RecordLoadProcessorChainManager.getRecordLoadProcessor (loadProcessor,new AddAuditHistoryIndLoadProcessor());
        // Issue 110819, filter official record for end quote.
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor = new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "coverageClassId");
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, endquoteLoadProcessor);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            origFieldLoadProcessor, loadProcessor);
        RecordSet rs;
        CoverageClassDAO r = getCoverageClassDAO();
        rs = r.loadAllCoverageClass(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoveageClass", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param insuredId the source id
     * @return RecordSet a RecordSet loaded with list of available coverage classes.
     */
    public RecordSet loadAllCoverageClassForWs(PolicyHeader policyHeader, String insuredId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageClassForWs", new Object[]{policyHeader, insuredId});
        }
        Record inputRecord = policyHeader.toRecord();
        RecordMode recordModeCode = RecordMode.TEMP;
        PolicyViewMode viewMode=policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        if (viewMode.isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }

        RecordLoadProcessor loadProcessor = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            origFieldLoadProcessor, loadProcessor);

        inputRecord.setFieldValue(PMCommonFields.RECORD_MODE_CODE, recordModeCode);
        inputRecord.setFieldValue(RiskInquiryFields.RISK_NUMBER_ID, insuredId);

        RecordSet rs;
        CoverageClassDAO r = getCoverageClassDAO();
        rs = r.loadAllCoverageClass(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageClassForWs", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param ownerRecords  coverage records
     * @param covgClassGridFields all the coverage class gird fields
     * @return RecordSet a RecordSet loaded with list of available coverage classes.
     */
    public RecordSet loadAllSourceCoverageClass(PolicyHeader policyHeader, RecordSet ownerRecords, RecordLoadProcessor loadProcessor, String covgClassGridFields) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSourceCoverageClass", new Object[]{policyHeader,ownerRecords,loadProcessor,covgClassGridFields});
        }

        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, new CoverageClassCopyRecordLoadProcessor(ownerRecords, covgClassGridFields));
        RecordSet rs = loadAllCoverageClass(policyHeader, loadProcessor);

        if (rs.getSize() > 1) {
            // sort the records by keys, product_coverage_class_code ASC, and then record_mode_code DESC, and then coverage_class_effective_to_date DESC
            RecordComparator rc;
            RecordSet recordSet;
            rc = new RecordComparator(CoverageClassFields.PRODUCT_COVERAGE_CLASS_CODE, true, SortOrder.ASC, null);
            rc.addFieldComparator(RiskInquiryFields.RECORD_MODE_CODE, true, SortOrder.DESC, null);
            rc.addFieldComparator(CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_TO_DATE, true, SortOrder.DESC, ConverterFactory.getInstance().getConverter(Date.class));
            RecordSet records = rs.getSortedCopy(rc);
            // filter duplicate records
            String productCoverageClassCode = "";
            String tempProductCoverageClassCode = "";
            Record sumRec = records.getSummaryRecord();
            List fieldNames = (List) ((ArrayList) records.getFieldNameList()).clone();
            recordSet = new RecordSet();
            recordSet.addFieldNameCollection(fieldNames);
            recordSet.setSummaryRecord(sumRec);
            for (int sortIdx = 0; sortIdx < records.getSize(); sortIdx++) {
                Record currentRecord = records.getRecord(sortIdx);
                if (currentRecord.hasStringValue(CoverageClassFields.PRODUCT_COVERAGE_CLASS_CODE)) {
                    tempProductCoverageClassCode = currentRecord.getStringValue(CoverageClassFields.PRODUCT_COVERAGE_CLASS_CODE);
                }
                if (!StringUtils.isBlank(tempProductCoverageClassCode) &&
                    productCoverageClassCode.equals(tempProductCoverageClassCode)) {
                    continue;
                }
                else {
                    productCoverageClassCode = tempProductCoverageClassCode;
                    recordSet.addRecord(currentRecord);
                }
            }
            rs = recordSet;
            Iterator iter = rs.getRecords();
            while (iter.hasNext()) {
                Record rec = (Record) iter.next();
                rec.setEditIndicator("Y");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSourceCoverageClass", rs);
        }

        return rs;
    }

    /**
     * Load all available coverage class
     *
     * @param policyHeader
     * @return
     */
    public RecordSet loadAllAvailableCoverageClass(PolicyHeader policyHeader) {
        return loadAllAvailableCoverageClass(policyHeader, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
    }

    /**
     * Returns a RecordSet loaded with list of all available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information
     * @param loadProcessor
     * @return RecordSet a RecordSet loaded with all list of available coverage class.
     */
    public RecordSet loadAllAvailableCoverageClass(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableCoverageClass", new Object[]{policyHeader});
        }

        Record input = policyHeader.toRecord();
        CoverageClassFields.setProductCoverageClassCode(input, "X");

        // Handle special mapping functionality via the PMDefaultManager
        getPmDefaultManager().processMappedDefaults("MAP_COVERAGE_CLASS", policyHeader, input);

        /* Get effective date and expire date based on system parameter "PM_ADD_COVG_CLASS_DT" */
        String sysPara = SysParmProvider.getInstance().getSysParm(
            SysParmIds.PM_ADD_COVG_CLASS_DT, SysParmIds.AddCovgClassDateValues.TRANS);

        if (SysParmIds.AddCovgClassDateValues.TRANS.equals(sysPara)) {

            CoverageClassFields.setCoverageClassEffectiveFromDate(input,
                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }
        else if (SysParmIds.AddCovgClassDateValues.TERM.equals(sysPara)) {
            CoverageClassFields.setCoverageClassEffectiveFromDate(input,
                policyHeader.getTermEffectiveFromDate());
        }
        CoverageClassFields.setCoverageClassEffectiveToDate(input,
            policyHeader.getTermEffectiveToDate());

        // Get available coverage class record set
        RecordSet rs = getCoverageClassDAO().loadAllAvailableCoverageClass(input, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableCoverageClass", rs);
        }
        return rs;
    }

    /**
     * Get initial values for coverage class
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForCoverageClass(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForCoverageClass", new Object[]{policyHeader, inputRecord});
        }

        Record returnRecord = new Record();  //This record hold all values for initial new added row

        // Firstly, merge default value from web work bench configuration
        Record configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(
            ADD_COVERAGE_CLASS_ACTION_CLASS_NAME);
        if (configuredDefaultValues != null) {
            returnRecord.setFields(configuredDefaultValues);
        }

        // Secondly, merge all default values for selected coverage class
        String productCoverageClassCode = CoverageClassFields.getProductCoverageClassCode(inputRecord);
        RecordSet availableCoverageClass = loadAllAvailableCoverageClass(policyHeader);
        RecordSet selectedCoverageClass = availableCoverageClass.getSubSet(
            new RecordFilter(CoverageClassFields.PRODUCT_COVERAGE_CLASS_CODE, productCoverageClassCode));
        if (selectedCoverageClass.getSize() == 1) {
            returnRecord.setFields(selectedCoverageClass.getRecord(0));
        }else{
            throw new AppException(AppException.UNEXPECTED_ERROR,"the selected available coverage class is not equals to 1");
        }

        // Thirdly, load other default values
        //Merge default values from Pm_Default.Get_Level_Default
        String sTermEffectiveDate = policyHeader.getTermEffectiveFromDate();
        String sTransEffectiveDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        Record defaultLevelValues = getPmDefaultManager().getDefaultLevel(CLASS_TAB,
            policyHeader.getLastTransactionInfo().getTransactionLogId(),
            sTermEffectiveDate, sTransEffectiveDate, PRODUCT_COVERAGE, productCoverageClassCode, null, null, null, null);
        //Set isRetroDateEditable the default value is N (disabled), if it is under CM type coverage, set to Y.
        if (policyHeader.hasCoverageHeader() &&
            policyHeader.getCoverageHeader().getPolicyFormCode().equals(CM)) {
            defaultLevelValues.setFieldValue(RETRO_DATE_EDITABLE,YesNoFlag.Y);
        } else {
            defaultLevelValues.setFieldValue(RETRO_DATE_EDITABLE,YesNoFlag.N);
        }
        returnRecord.setFields(defaultLevelValues);

        // Fourthly, set more default values based on the business rules

        // Set Coverage base record id
        String parentCoverageBaseRecordId = policyHeader.getCoverageHeader().getCoverageBaseRecordId();
        CoverageClassFields.setParentCoverageBaseRecordId(returnRecord, parentCoverageBaseRecordId);

        // Set coverage class effective from date/to date
        YesNoFlag dataChangeAllowedB = policyHeader.getRiskHeader().getDateChangeAllowedB();

        String sCoverageExpirationDate = policyHeader.getCoverageHeader().getCoverageEffectiveToDate();
        String policyExpirationDate = policyHeader.getPolicyExpirationDate();
        Date coverageExpirationDate = DateUtils.parseDate(sCoverageExpirationDate);
        Date policyExpDate = DateUtils.parseDate(policyExpirationDate);
        String sEffectiveDate, sExpirationDate;

        // If risk exp data change is allowed, short term coverage class is able to create.
        if (dataChangeAllowedB.booleanValue()) {
            Record input = new Record();
            CoverageFields.setCoverageBaseRecordId(input, policyHeader.getCoverageHeader().getCoverageBaseRecordId());
            CoverageFields.setProductCoverageCode(input, productCoverageClassCode);
            TransactionFields.setTransactionEffectiveFromDate(input, sTransEffectiveDate);
            Record datesRecord = getCoverageClassDAO().getShortTermCoverageClassEffAndExpDates(input);
            sEffectiveDate = CoverageFields.getCoverageEffectiveFromDate(datesRecord);
            sExpirationDate = CoverageFields.getCoverageEffectiveToDate(datesRecord);
            // Set the open date to policy expiration date.
            if (coverageExpirationDate.after(policyExpDate)) {
                sExpirationDate =  policyExpirationDate;
            }
        }
        else {
            // Coverage Effective date default to transaction effective date
            sEffectiveDate = sTransEffectiveDate;
            sExpirationDate = policyExpirationDate;
        }
        CoverageClassFields.setCoverageClassEffectiveFromDate(returnRecord, sEffectiveDate);
        CoverageClassFields.setCoverageClassEffectiveToDate(returnRecord, sExpirationDate);

        // Set retroactive date
        boolean covgPartConfigured = policyHeader.isCoveragePartConfigured();
        String sRetroDate = policyHeader.getCoverageHeader().getRetroactiveDate();
        CoverageClassFields.setRetroDate(returnRecord, sRetroDate);

        if (covgPartConfigured) {
            //TODO:This logic to be defined once coverage part is done
        }

        // Set termBaseRecordId
        String termBaseRecordId = policyHeader.getTermBaseRecordId();
        PolicyHeaderFields.setTermBaseRecordId(returnRecord, termBaseRecordId);

        // Set addl_infor1,2,3 fields
        String subCovgAddl1 = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_SUBCOVG_ADDL1);
        String subCovgAddl2 = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_SUBCOVG_ADDL2);
        String subCovgAddl3 = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_SUBCOVG_ADDL3);
        String isoCustCode = CoverageClassFields.getIsoCustCode(returnRecord);

        // Get optional exposure basis information, if any of the PM_SUBCOVG_ADDL1,2,3
        // system parameter is set to 'EXPOSURE_BASIS_NO'
        String ratingModuleCode = CoverageClassFields.getRatingModuleCode(returnRecord);
        // Fix issue 97428
        if (isManuallyRated(ratingModuleCode)) {
            CoverageFields.setAnnualBaseRate(returnRecord, "0");
            CoverageClassFields.setIsManuallyRated(returnRecord, YesNoFlag.Y);
        }
        else {
            CoverageClassFields.setIsManuallyRated(returnRecord, YesNoFlag.N);
        }

        String exposureBasicNo = "";
        if (SysParmIds.PM_SUBCOVG_ADDL_EXPOSURE_BASIC_NO.equals(subCovgAddl1) ||
            SysParmIds.PM_SUBCOVG_ADDL_EXPOSURE_BASIC_NO.equals(subCovgAddl2) ||
            SysParmIds.PM_SUBCOVG_ADDL_EXPOSURE_BASIC_NO.equals(subCovgAddl3)) {

            // Get optional exposure basic information
            Record input = new Record();
            input.setFields(policyHeader.toRecord(),false);
            CoverageClassFields.setProductCoverage(input, productCoverageClassCode);
            CoverageClassFields.setRatingModuleCode(input, ratingModuleCode);

            exposureBasicNo = getCoverageClassDAO().getExposureInfo(input);
        }

        // PM_SUBCOVG_ADDL1,2,3 equal "ISO_CUST_CODE"
        if (SysParmIds.PM_SUBCOVG_ADDL_ISO_CUST_CODE.equals(subCovgAddl1)) {
            CoverageClassFields.setAddlInfo1(returnRecord, isoCustCode);
        }
        if (SysParmIds.PM_SUBCOVG_ADDL_ISO_CUST_CODE.equals(subCovgAddl2)) {
            CoverageClassFields.setAddlInfo2(returnRecord, isoCustCode);
        }
        if (SysParmIds.PM_SUBCOVG_ADDL_ISO_CUST_CODE.equals(subCovgAddl3)) {
            CoverageClassFields.setAddlInfo3(returnRecord, isoCustCode);
        }

        // PM_SUBCOVG_ADDL1,2,3 equal "EXPOSURE_BASIC_NO"
        if (SysParmIds.PM_SUBCOVG_ADDL_EXPOSURE_BASIC_NO.equals(subCovgAddl1)) {
            CoverageClassFields.setAddlInfo1(returnRecord, exposureBasicNo);
        }
        if (SysParmIds.PM_SUBCOVG_ADDL_EXPOSURE_BASIC_NO.equals(subCovgAddl2)) {
            CoverageClassFields.setAddlInfo2(returnRecord, exposureBasicNo);
        }
        if (SysParmIds.PM_SUBCOVG_ADDL_EXPOSURE_BASIC_NO.equals(subCovgAddl3)) {
            CoverageClassFields.setAddlInfo3(returnRecord, exposureBasicNo);
        }
        // Set exposureUnit if it exists in inputRecord
        if(inputRecord.hasStringValue(CoverageClassFields.EXPOSURE_UNIT)){
            CoverageClassFields.setExposureUnit(returnRecord, CoverageClassFields.getExposureUnit(inputRecord));
        }

        // Get the default Common Tab Entitlement values
        returnRecord.setFields(CommonTabsEntitlementRecordLoadProcessor.getInitialEntitlementValuesForCommonTabs());

        // Merge ID and Base Record ID
        Record input = policyHeader.toRecord();
        CoverageClassFields.setProductCoverageClassCode(input, productCoverageClassCode);
        CoverageFields.setCoverageBaseRecordId(input, parentCoverageBaseRecordId);
        Record idRecord = getCoverageClassDAO().getCoverageClassIdAndBaseId(input);
        returnRecord.setFields(idRecord);
        // Setup intial row style
        origFieldLoadProcessor.postProcessRecord(returnRecord, true);
        CoverageClassRowStyleRecordLoadprocessor.setInitialEntitlementValuesForRowStyle(returnRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForCoverageClass", returnRecord);
        }

        return returnRecord;
    }

    /**
     * Wrapper to invoke the save of all inserted/updated Coverage Classrecords and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader the summary policy information corresponding to the provided coverage classes.
     * @param inputRecords a set of Records, each with the updated Coverage Class Detail info
     *                     matching the fields returned from the loadAllCoverageClass method.
     * @return the number of rows updated.
     */
    public int processSaveAllCoverageClass(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveAllCoverageClass", new Object[]{inputRecords});

        int processCount = processSaveAllCoverageClassData(policyHeader, inputRecords, false);

        l.exiting(getClass().getName(), "processSaveAllCoverageClass", new Integer(processCount));
        return processCount;
    }

    /**
     * Wrapper to auto invoke the save of all inserted/updated Coverage Classrecords and subsequently
     * to invoke the save transaction logic for WIP only.
     *
     * @param policyHeader the summary policy information corresponding to the provided coverage classes.
     * @param inputRecords a set of Records, each with the updated Coverage Class Detail info
     *                     matching the fields returned from the loadAllCoverageClass method.
     * @return the number of rows updated.
     */
    public int processAutoSaveAllCoverageClass(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "processAutoSaveAllCoverageClass", new Object[]{inputRecords});

        int processCount = processSaveAllCoverageClassData(policyHeader, inputRecords, true);;

        l.exiting(getClass().getName(), "processAutoSaveAllCoverageClass", new Integer(processCount));
        return processCount;
    }

    /**
     * Wrapper to invoke the save of all inserted/updated Coverage Classrecords and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader the summary policy information corresponding to the provided coverage classes.
     * @param inputRecords a set of Records, each with the updated Coverage Class Detail info
     *                     matching the fields returned from the loadAllCoverageClass method.
     * @param isAutoSave   a indicator to check it is auto save process or not.
     * @return the number of rows updated.
     */
    protected int processSaveAllCoverageClassData(PolicyHeader policyHeader, RecordSet inputRecords, boolean isAutoSave) {
        Logger l = LogUtils.enterLog(getClass(), "processSaveAllCoverageClassData", new Object[]{inputRecords});

        int processCount = 0;

        // First save all inserted/updated Coverage Class records
        CoverageClassSaveProcessor saveProcessor = (CoverageClassSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
        processCount = saveProcessor.saveAllCoverageClass(policyHeader, inputRecords);

        // Complete the save action via the TransactionManager
        if (!isAutoSave) {
            Record saveRecord = inputRecords.getSummaryRecord();
            saveRecord.setFields(policyHeader.toRecord(), false);

            // Force transactionLogId from policyHeader since coverage record contains the same fieldId
            saveRecord.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());

            CoverageClassFields.setLevel(saveRecord, COVERAGE_CLASS);
            getTransactionManager().processSaveTransaction(policyHeader, saveRecord);
        }

        l.exiting(getClass().getName(), "processSaveAllCoverageClassData", new Integer(processCount));
        return processCount;
    }

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader the summary policy information corresponding to the provided coverage classes.
     * @param inputRecords a set of Records, each with the updated Coverage Class Detail info
     *                     matching the fields returned from the loadAllCoverageClass method.
     * @return the number of rows updated.
     */
    public int saveAllCoverageClass(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllCoverageClass", new Object[]{inputRecords});

        int updateCount;

        // Do validation
        validateAllCoverageClass(policyHeader, inputRecords);

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        // Fix issue 106439. System should only add the necessary fields to changedRecords rather than
        // policyHeader.toRecord() since some potential fields may be overwritten.
        // The termEffectiveToDate is necessary for saving coverage class.
        changedRecords.setFieldValueOnAll("termEffectiveToDate", policyHeader.getTermEffectiveToDate());
        // Set the current transaction id on all records
        changedRecords.setFieldValueOnAll("transactionLogId", policyHeader.getLastTransactionId());

        // Split the input records for add, update and delete

        // Get the WIP records
        RecordSet wipRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet offRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
        RecordSet ooseRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.REQUEST));

        // Delete the WIP records marked for delete in batch mode
        RecordSet deleteRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        updateCount = getCoverageClassDAO().deleteAllCoverageClass(deleteRecords);

        // Get all inserted and updated records from WIP records
        RecordSet modifiedRecords = PMRecordSetHelper.setRowStatusOnModifiedRecords(wipRecords);
        // insert and update records
        updateCount += getCoverageClassDAO().addAllCoverageClass(modifiedRecords);

        if (updateCount > 0) {
            policyHeader.getCacheCoverageOption().clear();
            policyHeader.getCacheCoverageRiskOption().clear();
        }

        // Update the OFFICIAL records marked for update in batch mode
        RecordSet updateRecords = offRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));

        updateRecords.addRecords(ooseRecords);
        // For the official records set the effective from date equal to the transaction effective date
        updateRecords.setFieldValueOnAll("transEffectiveFromDate",
            policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        updateCount += getCoverageClassDAO().updateAllCoverageClass(updateRecords);

        //Coverages class should not be duplicated for the same period.
        if (updateCount > 0) {
            Record inputRecord = setInputForDuplicateValidation(policyHeader);
            Record outputRec = getCoverageClassDAO().validateCoverageClassDuplicate(inputRecord);
            if (outputRec != null && YesNoFlag.N.equals(outputRec.getStringValue("result"))) {
                handleDuplicateValidation(policyHeader, outputRec);
                if (MessageManager.getInstance().hasErrorMessages()) {
                    throw new ValidationException("Coverage Class is duplicated.");
                }
            }
        }

        l.exiting(getClass().getName(), "saveAllCoverageClass", new Integer(updateCount));
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
            new Object[]{policyHeader});

        String coverageClassId = CoverageClassFields.getCoverageClassId(resultRecord);
        String validationMessage = resultRecord.getStringValue("validationMessage");
        MessageManager.getInstance().addErrorMessage("pm.common.invalid.data",
            new String[]{validationMessage}, "", coverageClassId);

        l.exiting(getClass().getName(), "handleDuplicateValidation");
    }

    /**
     * validate coverage class data before save
     *
     * @param policyHeader
     * @param inputRecords that to be validated.
     */
    protected void validateAllCoverageClass(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllCoverageClass", new Object[]{policyHeader, inputRecords});
        }

        if (policyHeader.getLastTransactionInfo().getTransactionCode().isOosEndorsement()) {
            // Pre-Oose Change validations
            PreOoseChangeValidator preOoseChangeValidator = new PreOoseChangeValidator(
                null, "coverageClass", CoverageClassFields.COVERAGE_CLASS_ID, CoverageClassFields.COVERAGE_CLASS_BASE_RECORD_ID);
            preOoseChangeValidator.validate(inputRecords);
        }

        // Get an instance of the Standard Effective To Date Rule Validator
        StandardEffectiveToDateRecordValidator effToDateValidator =
            new StandardEffectiveToDateRecordValidator(policyHeader,
                CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_FROM_DATE,
                CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_TO_DATE,
                CoverageClassFields.COVERAGE_CLASS_ID, CoverageClassFields.ORIG_COVERAGE_CLASS_EFFECTIVE_TO_DATE);

        // Get an instance of the Standard Retroactive Date Rule Validator
        StandardRetroactiveDateRecordValidator retroDateValidator =
            new StandardRetroactiveDateRecordValidator(policyHeader,
                CoverageClassFields.PRODUCT_COVERAGE_CLASS_CODE, CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_FROM_DATE,
                CoverageClassFields.RETRO_DATE, CoverageClassFields.COVERAGE_CLASS_ID);

        //Get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        // Get coverage effective date
        String covgEffFromDateStr = policyHeader.getCoverageHeader().getCoverageEffectiveFromDate();
        Date covgEffFromDate = (Date)
            ConverterFactory.getInstance().getConverter(Date.class).convert(Date.class, covgEffFromDateStr);

        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String rowNum = String.valueOf(r.getRecordNumber()+1);
            String rowId = CoverageClassFields.getCoverageClassId(r);

            // Validate for OOSE Coverage Class
            validateForOoseCoverageClass(policyHeader, r);

            // If exposure unit is null, set it as zero to avoid rating error.
            if (!r.hasStringValue(CoverageClassFields.EXPOSURE_UNIT)) {
                CoverageClassFields.setExposureUnit(r, "0");
            }
            else {
                String expUnit = CoverageClassFields.getExposureUnit(r);
                if(!StringUtils.decimalPlaceCheck(expUnit,2)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainCoverageClass.expUnit.decimalPlace.error", new String[]{rowNum},
                    CoverageClassFields.EXPOSURE_UNIT, rowId);
                }
            }

            // If coverage is manually rated, set annual base rate as 0 replacing of null.
            if (r.hasStringValue(CoverageFields.IS_MANUALLY_RATED) &&
                    YesNoFlag.getInstance(CoverageFields.getIsManuallyRated(r)).booleanValue() &&
                    !r.hasStringValue(CoverageFields.ANNUAL_BASE_RATE)) {
                CoverageFields.setAnnualBaseRate(r, "0");
            }

            // Set coverageClassEffectiveToData to termExpirationData, If it is after termExpirationData.
            String sCurTermExpirationDate = policyHeader.getTermEffectiveToDate();
            String sCovgClassEffToDate = CoverageClassFields.getCoverageClassEffectiveToDate(r);
            boolean isBig = false;
            if(!StringUtils.isBlank(sCovgClassEffToDate) && DateUtils.parseDate(sCovgClassEffToDate).after(DateUtils.parseDate(sCurTermExpirationDate))){
                isBig = true;
                CoverageClassFields.setCoverageClassEffectiveToDate(r, sCurTermExpirationDate);
            }
            // Validation #1:  Standard Effective To Date Rule
            effToDateValidator.validate(r);
           // Roll back the coverageClassEffectiveToData.
            if(isBig){
                 CoverageClassFields.setCoverageClassEffectiveToDate(r, sCovgClassEffToDate);
            }

            //Validation #2: validate Retroactive Date
            if (CoverageClassFields.getPolicyFormCode(r).equals(CM))
                retroDateValidator.validate(r);

            // Validation #4 Coverage Class Date - INSERTED records only
            if (r.getUpdateIndicator().equals(UpdateIndicator.INSERTED)) {
                Date covgClassEffFromDate = r.getDateValue(CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_FROM_DATE);
                if (covgClassEffFromDate.before(covgEffFromDate)) {
                    MessageManager.getInstance().addErrorMessage("pm.addCoverageClass.covgClassFromDate.error2",
                        new Object[]{rowNum,CoverageClassFields.getCoverageClassShortDescription(r), FormatUtils.formatDateForDisplay(covgEffFromDate)},
                        CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_FROM_DATE, rowId);
                }
            }

            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;
        }

        // Validation #3 Duplicate Coverage Class - TEMP records only
        String[] keyFieldNames = new String[]{CoverageClassFields.PRODUCT_COVERAGE_CLASS_CODE};
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
            CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_FROM_DATE,
            CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_TO_DATE,
            CoverageClassFields.COVERAGE_CLASS_ID,
            "pm.addCoverageClass.duplicateCoverageClass.error2", keyFieldNames, keyFieldNames);

        RecordSet wipRecords = inputRecords.getSubSet(
            new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP)).
            getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}));
        continuityValidator.validate(wipRecords);

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Coverage Class data.");
        }

        l.exiting(getClass().getName(), "validateAllCoverageClass");
    }

    /**
     * Save all default coverage class
     *
     * @param policyHeader
     * @return update count
     */
    public int saveAllDefaultCoverageClass(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllDefaultCoverageClass", new Object[]{policyHeader});

        // Get all product covreages
        RecordSet rsCoverageClass = loadAllAvailableCoverageClass(policyHeader);

        // Get default Coverage Classes
        RecordSet defaultCoverageClasses = new RecordSet();

        int count = rsCoverageClass.getSize();
        for (int i = 0; i < count; i++) {
            Record productCoverageClassRecord = rsCoverageClass.getRecord(i);
            if (productCoverageClassRecord.getBooleanValue("defaultCoverageClassB").booleanValue() ||
                productCoverageClassRecord.getBooleanValue("mapDefaultCoverageClassB").booleanValue()) {
                Record defaultCoverage = getInitialValuesForCoverageClass(policyHeader, productCoverageClassRecord);
                defaultCoverageClasses.addRecord(defaultCoverage);
            }
        }

        // Add the PolicyHeader info to each Coverage class detail Record
        defaultCoverageClasses.setFieldsOnAll(policyHeader.toRecord(), false);

        // Set the current transaction id on all records
        defaultCoverageClasses.setFieldValueOnAll("transactionLogId", policyHeader.getLastTransactionId());

        // Add the inserted WIP records in batch mode
        defaultCoverageClasses.setFieldValueOnAll("rowStatus", "NEW");
        int updateCount = getCoverageClassDAO().addAllCoverageClass(defaultCoverageClasses);

        l.exiting(getClass().getName(), "saveAllDefaultCoverageClass", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Get initial values for OOSE coverage class
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForOoseCoverageClass(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForOoseCoverageClass", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();

        // Set Default values
        // coverageClassId
        Long lCovgClassId = getDbUtilityManager().getNextSequenceNo();
        CoverageClassFields.setCoverageClassId(outputRecord, String.valueOf(lCovgClassId.longValue()));
        // officialRecordId
        CoverageClassFields.setOfficialRecordId(outputRecord, CoverageClassFields.getCoverageClassId(inputRecord));
        // recordModeCode
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.REQUEST);
        // afterImageRecordB
        CoverageClassFields.setAfterImageRecordB(outputRecord, YesNoFlag.Y);
        // coverageClassEffectiveFromDate
        CoverageClassFields.setCoverageClassEffectiveFromDate(outputRecord,
            policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        // coverageClassEffecitveToDate
        String covgClassEffToDate = null;
        SysParmProvider sysParm = SysParmProvider.getInstance();
        String oosDefExpDate = sysParm.getSysParm(SysParmIds.PM_OOS_DEF_CCLEXP_DT);
        if (oosDefExpDate != null) {
            if (oosDefExpDate.equalsIgnoreCase(SysParmIds.OosDefaultExpDateValues.TERM)) {
                covgClassEffToDate = policyHeader.getTermEffectiveToDate();
            }
            else if (oosDefExpDate.equalsIgnoreCase(SysParmIds.OosDefaultExpDateValues.POLICY)) {
                Iterator iter = policyHeader.getPolicyTerms();
                if (iter.hasNext()) {
                    Term lastTerm = (Term) iter.next();
                    covgClassEffToDate = lastTerm.getEffectiveToDate();
                }
            }
        }
        else {
            covgClassEffToDate = getTransactionManager().getOoseExpirationDate(policyHeader);
        }
        CoverageClassFields.setCoverageClassEffectiveToDate(outputRecord, covgClassEffToDate);

        // Set other columns same as the original outputRecord for which Change was invoked
        // coverageClassStatus
        CoverageClassFields.setCoverageClassStatus(outputRecord, PMStatusCode.PENDING);

        // set inputRecords into outputRecord
        outputRecord.setFields(inputRecord, false);

        // Get the default coverage class entitlement values
        CoverageClassEntitlementRecordLoadProcessor.setInitialEntitlementValuesForCoverageClass(this, policyHeader,
            policyHeader.getScreenModeCode(), outputRecord);

        // set exposureUnit to null
        CoverageClassFields.setExposureUnit(outputRecord, null);

        // Set Change option to invisible for the new outputRecord
        outputRecord.setFieldValue("isOosChangeAvailable", YesNoFlag.N);
        // Set Delete option to visible for the new outputRecord
        outputRecord.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
        // Setup intial row style
        CoverageClassRowStyleRecordLoadprocessor.setInitialEntitlementValuesForRowStyle(outputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForOoseCoverageClass", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Check if Change option is available
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord a record loaded with the selected risk record.
     */
    public void validateForOoseCoverageClass(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForOoseCoverageClass", new Object[]{policyHeader, inputRecord});
        }

        if (policyHeader.getScreenModeCode().isOosWIP() && PMCommonFields.getRecordModeCode(inputRecord).isOfficial()) {
            // check if Change option is available or not
            YesNoFlag isChangeAvailable = YesNoFlag.N;

            String sTransEffDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
            String sEffDate = CoverageClassFields.getCoverageClassEffectiveFromDate(inputRecord);
            Date transEffDate = DateUtils.parseDate(sTransEffDate);
            Date effDate = DateUtils.parseDate(sEffDate);

            // 1. If the term displayed is the initial term in which the OOS transaction was initiated
            // 2. the attached risk has a status of 'PENDING?or 'ACTIVE?
            // 3. If the row selected has an effective from date < the transaction effective date
            // 4. The query from Pm_Valid_Oos_Coverage returns 'Y'
            PMStatusCode riskStatus = policyHeader.getRiskHeader().getRiskStatusCode();
            if (policyHeader.isInitTermB()
                && (riskStatus.isPending() || riskStatus.isActive())
                && !transEffDate.before(effDate)) {

                // do query
                Record record = new Record();
                CoverageClassFields.setCoverageClassEffectiveFromDate(
                    record, CoverageClassFields.getCoverageClassEffectiveFromDate(inputRecord));
                CoverageClassFields.setCoverageClassBaseRecordId(
                    record, CoverageClassFields.getCoverageClassBaseRecordId(inputRecord));
                boolean isOosCovgValid = YesNoFlag.getInstance(
                    getCoverageClassDAO().isOosCoverageClassValid(record)).booleanValue();

                if (isOosCovgValid) {
                    isChangeAvailable = YesNoFlag.Y;
                }
            }

            if (!isChangeAvailable.booleanValue()) {
                MessageManager.getInstance().addErrorMessage("pm.maintainCoverageClass.ooseCovgClass.changeOption.error");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForOoseCoverageClass");
        }
    }

    /**
     * delete all copied coverage class
     *
     * @param policyHeader
     * @param inputRecord
     * @param covgClassRs
     */
    public void deleteAllCopiedCoverageClass(PolicyHeader policyHeader, Record inputRecord, RecordSet covgClassRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllCopiedCoverageClass", new Object[]{policyHeader,inputRecord,covgClassRs});
        }
        covgClassRs.setFieldsOnAll(policyHeader.toRecord(), false);
        getCoverageClassDAO().deleteAllCopiedCoverageClass(covgClassRs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllCopiedCoverageClass");
        }
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
     * Check if add coverage class is allowed
     *
     * @param record
     * @return
     */
    public YesNoFlag isAddCoverageClassAllowed(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddCoverageClassAllowed", record);
        }
        YesNoFlag result = YesNoFlag.getInstance(getCoverageClassDAO().isAddCoverageClassAllowed(record));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddCoverageClassAllowed", result);
        }
        return result;
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
     * Add the selected coverage classes.
     * @param policyHeader
     * @param inputRecord
     * @param coverageClassRecSet
     */
    public void addAllCoverageClass(PolicyHeader policyHeader, Record inputRecord, RecordSet coverageClassRecSet) {
        Logger l = LogUtils.enterLog(getClass(), "addAllCoverageClass", new Object[]{inputRecord, coverageClassRecSet});
        String selectedCoverageClassStr = CoverageClassFields.getSelectedCoverageClass(inputRecord);
        String[] selectedCoverageClassArr = selectedCoverageClassStr.split(",");
        for (int i = 0; i < selectedCoverageClassArr.length; i++) {
            Record record = new Record();
            String[] coverageClassFieldArr = selectedCoverageClassArr[i].split("@");
            CoverageClassFields.setProductCoverageClassCode(record, coverageClassFieldArr[0]);
            CoverageClassFields.setExposureUnit(record, coverageClassFieldArr[1].trim());
            Record initialedRec = getInitialValuesForCoverageClass(policyHeader, record);;
            record = new Record();
            setReturnRecord(initialedRec, record, coverageClassRecSet);
            coverageClassRecSet.addRecord(record);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllCoverageClass", coverageClassRecSet);
        }
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
     * validate coverage class copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllCoverageClass(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllCoverageClass", new Object[]{inputRecord});
        }

        //set subCovgCopyB flag
        if (inputRecord.hasStringValue(CoverageClassFields.EXPOSURE_UNIT)) {
            CoverageClassFields.setSubCovgCopyB(inputRecord, YesNoFlag.Y);
        }
        else {
            CoverageClassFields.setSubCovgCopyB(inputRecord, YesNoFlag.N);
        }

        String valStatus = getCoverageClassDAO().validateCopyAllCoverageClass(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllCoverageClass", valStatus);
        }

        return valStatus;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getCoverageClassDAO() == null)
            throw new ConfigurationException("The required property 'coverageClassDAO' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        if (getPmDefaultManager() == null)
            throw new ConfigurationException("The required property 'pmDefaultManager' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
    }

    public CoverageClassManagerImpl() {
    }

    public CoverageClassDAO getCoverageClassDAO() {
        return m_coverageClassDAO;
    }

    public void setCoverageClassDAO(CoverageClassDAO coverageClassDAO) {
        m_coverageClassDAO = coverageClassDAO;
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

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    public TransactionDAO getTransactionDAO() {
        return m_transactionDAO;
    }

    public void setTransactionDAO(TransactionDAO transactionDAO) {
        m_transactionDAO = transactionDAO;
    }

    protected static final String CLASS_TAB = "CLASS_TAB";
    protected static final String PRODUCT_COVERAGE = "PRODUCT_COVERAGE_CODE";
    protected static final String COVERAGE_CLASS = "COVERAGE_CLASS";
    protected static final String OFFICIAL = "OFFICIAL";
    protected static final String CM = "CM";
    protected static final String SAVE_PROCESSOR = "CoverageClassManager";
    protected static final String RETRO_DATE_EDITABLE = "isRetroDateEditable";

    private CoverageClassDAO m_coverageClassDAO;
    private TransactionManager m_transactionManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private PMDefaultManager m_pmDefaultManager;
    private DBUtilityManager m_dbUtilityManager;
    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{CoverageClassFields.COVERAGE_CLASS_EFFECTIVE_TO_DATE});
    private TransactionDAO m_transactionDAO;
}
