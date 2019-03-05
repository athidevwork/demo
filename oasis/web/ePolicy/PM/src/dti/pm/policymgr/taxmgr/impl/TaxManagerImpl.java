package dti.pm.policymgr.taxmgr.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.coveragemgr.manuscriptmgr.ManuscriptFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.taxmgr.TaxFields;
import dti.pm.policymgr.taxmgr.TaxManager;
import dti.pm.policymgr.taxmgr.dao.TaxDAO;
import dti.pm.riskmgr.RiskFields;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import dti.pm.busobjs.SysParmIds;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for TaxManager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 21, 2007
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/19/2007       fcb         loadAllTax: redundant logic to set transactionLogId removed. 
 * 10/13/2014       wdang       158112 - Add loadAllTaxHeader(), saveAllTaxHeader(), getTermAlgorithm() for Maintain Tax page.
 * 01/30/2015       fcb         160508 - added additional validation for taxes.
 * 08/21/2015       ssheng      165340 - Use Common Function PolicyHeader.getRecordMode to replace calcuate recordModeCode
 *                                       via screenMode.
 * 11/19/2018       xnie        196983 - Modified getInitialValuesForAddTax to set tax exp date based on system parameter
 *                                       PM_TAX_MT_OPEN_EXP.
 * ---------------------------------------------------
 */
public class TaxManagerImpl implements TaxManager {

    /**
     * Retrieves all the tax information
     *
     * @param policyHeader policy header
     * @param inputRecord  inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTax(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTax", new Object[]{policyHeader});
        }

        // If transactionLogId is not defined, default it to the latest available
        if (!inputRecord.hasStringValue(TransactionFields.TRANSACTION_LOG_ID)) {
            inputRecord.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, getLatestTaxTransaction(policyHeader));
        }

        // Add the policy header to the input record
        inputRecord.setFields(policyHeader.toRecord(), false);

        /* Get tax record set */
        RecordSet rs = getTaxDAO().loadAllTax(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTax", rs);
        }
        return rs;
    }

    /**
     * Retrieve all risk information for Maintain Tax page.
     *
     * @param policyHeader policy header
     * @param inputRecord input record
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllRisk(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRisk", new Object[]{policyHeader, inputRecord});
        }

        Record input = new Record();
        input.setFields(policyHeader.toRecord());
        input.setFields(inputRecord, false);
        
        // Set parameter recordModeCode
        RecordMode recordModeCode = policyHeader.getRecordMode();
        input.setFieldValue(TaxFields.RECORD_MODE_CODE, recordModeCode.getName());
        
        RecordSet rs = getTaxDAO().loadAllRisk(input, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRisk", rs);
        }
        return rs;
    }
    
    /**
     * Gets the latest tax based transaction of the policy
     *
     * @param policyHeader policy header
     * @return transactionId
     */
    protected String getLatestTaxTransaction(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLatestTaxTransaction", new Object[]{policyHeader,});
        }

        Record input = new Record();
        input.setFields(policyHeader.toRecord(), false);

        // Execute the DAO
        String latestTaxTransaction = getTaxDAO().getLatestTaxTransaction(input);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLatestTaxTransaction", latestTaxTransaction);
        }
        return latestTaxTransaction;
    }
    
    /**
     * Retrieve all tax definition for Maintain Tax page.
     *
     * @param policyHeader policy header
     * @param inputRecord  inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllTaxHeader(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTaxHeader", new Object[]{policyHeader, inputRecord});
        }

        Record input = new Record();
        input.setFields(policyHeader.toRecord());
        input.setFields(inputRecord, false);
        
        // Set parameter recordModeCode
        RecordMode recordModeCode = null;
        recordModeCode = policyHeader.getRecordMode();
        input.setFieldValue(TaxFields.RECORD_MODE_CODE, recordModeCode.getName());
        
        RecordSet rs = getTaxDAO().loadAllTaxHeader(input, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTaxHeader", rs);
        }
        return rs;
    }
    
    /**
     * Save/Change/Delete all tax definitions for Maintain Tax page.
     *
     * @param policyHeader policy header
     * @param inputRecords input records
     */
    public void saveAllTaxHeader(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllTaxHeader", new Object[]{inputRecords});
        }

        // input policyHeader parameter
        inputRecords.setFieldsOnAll(policyHeader.toRecord(), false);
        
        // validation
        validateAllTax(policyHeader, inputRecords);
        
        // delete all tax
        RecordSet deleteRs = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        if(deleteRs.getSize() > 0){
            getTaxDAO().deleteAllTaxHeader(deleteRs);
        }

        // insert all tax
        RecordSet insertRs = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        if(insertRs.getSize() > 0){
            getTaxDAO().insertAllTaxHeader(insertRs);
        }
        
        // update all tax
        RecordSet updateRs = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        if(updateRs.getSize() > 0){
            getTaxDAO().updateAllTaxHeader(updateRs);
        }
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllTaxHeader");
        }
    }
    
    /**
     * Get term algorithm by the given term effective date
     *
     * @param policyHeader policy header
     * @param inputRecord  inputRecord
     * @return TaxCalcAlgorithm
     */
    public String getTermAlgorithm(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTermAlgorithm", new Object[]{policyHeader, inputRecord});
        }
        
        String algorithm = getTaxDAO().getTermAlgorithm(policyHeader.toRecord());
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTermAlgorithm", algorithm);
        }
        return algorithm;
    }
    
    /**
     * To get initial values for a newly inserted Tax record
     * 
     * @param policyHeader
     * @param inputRecord
     * @return Record with initial values
     */
    public Record getInitialValuesForAddTax(PolicyHeader policyHeader,
            Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddTax", new Object[]{policyHeader, inputRecord});
        }
        
        //get default record from workbench
        Record output = new Record();
        output.setFields(inputRecord, true);
        //set effective from/to date
        TaxFields.setEffectiveFromDate(output, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());

        SysParmProvider sysParm = SysParmProvider.getInstance();
        String syspram = sysParm.getSysParm(SysParmIds.PM_TAX_MT_OPEN_EXP, "N");
        if (YesNoFlag.getInstance(syspram).booleanValue()) {
            TaxFields.setEffectiveToDate(output, DEFAULT_EFFECTIVE_TO);
        }
        else {
            TaxFields.setEffectiveToDate(output, policyHeader.getTermEffectiveToDate());
        }

        // set default entitlement fields
        MaintainTaxRiskEntitlementRecordLoadProcessor.getInitialValuesForAddTax(output);
        MaintainTaxEntitlementRecordLoadProcessor.getInitialValuesForAddTax(output);
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddTax", output);
        }

        return output;
    }
    
    private void validateForEditFields(Record record, RecordSet recordset){
        //System prevent to change other attributes when expiring/extending the premium tax
        boolean official = PMCommonFields.getRecordModeCode(record).isOfficial();
        boolean changedFromOfficialToTemp = false;
        String officialRecordId = null;
        if (record.hasStringValue(TaxFields.OFFICIAL_RECORD_ID)) {
            officialRecordId = TaxFields.getOfficialRecordId(record);
            if (Long.parseLong(officialRecordId) > 0 && !PMCommonFields.getRecordModeCode(record).isOfficial()) {
                changedFromOfficialToTemp = true;
            }
        }
        // only those two status should validate :  1, official. 2, change status from official to temp.
        if (official || changedFromOfficialToTemp) {
            Date origEffectiveToDate = DateUtils.parseDate(TaxFields.getOrigEffectiveToDate(record));
            String origState = TaxFields.getOrigStateCode(record);
            String origCounty = TaxFields.getOrigCountyTaxCode(record);
            String origCity = TaxFields.getOrigCityTaxCode(record);
            String origTaxLevel = TaxFields.getOrigTaxLevel(record);
            
            Date effectiveToDate = DateUtils.parseDate(TaxFields.getEffectiveToDate(record));
            String state = TaxFields.getStateCode(record);
            String county = TaxFields.getCountyTaxCode(record);
            String city = TaxFields.getCityTaxCode(record);
            String taxLevel = TaxFields.getTaxLevel(record);
            
            if (changedFromOfficialToTemp) {
                for (int i = 0; i < recordset.getSize(); i++) {
                    Record offRecord = recordset.getRecord(i);
                    if (StringUtils.isSame(TaxFields.getPremiumTaxHeaderId(offRecord), officialRecordId)) {
                        origEffectiveToDate = DateUtils.parseDate(TaxFields.getEffectiveToDate(offRecord));
                        origState = TaxFields.getStateCode(offRecord);
                        origCounty = TaxFields.getCountyTaxCode(offRecord);
                        origCity = TaxFields.getCityTaxCode(offRecord);
                        origTaxLevel = TaxFields.getOrigTaxLevel(offRecord);
                        break;
                    }
                }
            }
            if (DateUtils.dateDiff(DateUtils.DD_DAYS, effectiveToDate, origEffectiveToDate) != 0
                    && (!StringUtils.isSame(state, origState)
                    || !StringUtils.isSame(county, origCounty)
                    || !StringUtils.isSame(city, origCity))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainTax.conflictEdit.error");
            }
            if (!StringUtils.isSame(origTaxLevel, taxLevel)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainTax.taxLevel.error");
            }
        }
    }
    
    private void validateAllTax(PolicyHeader policyHeader, RecordSet inputRecords) throws ValidationException {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllTax", new Object[] { inputRecords });
        }
        
        // create a recordset for overlap validation
        RecordSet overlapRecords = inputRecords
                .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}))
                .getSubSet(new UpdateIndicatorRecordFilter(new String[] {UpdateIndicator.NOT_CHANGED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED }));
        
        // create a recordset for insert/update validation   
        RecordSet modifiedRecords = inputRecords
                .getSubSet(new UpdateIndicatorRecordFilter(new String[] {UpdateIndicator.INSERTED, UpdateIndicator.UPDATED }));
        
        // Get the expiration date of last manual-tax term 
        String expirationDate = getTaxDAO().getManualExpiration(policyHeader.toRecord());
        
        // loop validate records
        try {
            for (int i = 0; i < modifiedRecords.getSize(); i ++) {
                Record record = modifiedRecords.getRecord(i);
                if(StringUtils.isBlank(TaxFields.getStateCode(record), true)){
                    MessageManager.getInstance().addErrorMessage("pm.maintainTax.emptyState.error");
                }
                else if(!StringUtils.isBlank(TaxFields.getCountyTaxCode(record), true)
                        && !StringUtils.isBlank(TaxFields.getCityTaxCode(record), true)){
                    MessageManager.getInstance().addErrorMessage("pm.maintainTax.invalidCombination.error");
                }
                if (DateUtils.daysDiff(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate(), TaxFields.getEffectiveToDate(record)) < 0){
                    MessageManager.getInstance().addErrorMessage("pm.maintainTax.minEndDate.error");
                }
                if (DateUtils.daysDiff(TaxFields.getEffectiveToDate(record), expirationDate) < 0){
                    MessageManager.getInstance().addErrorMessage("pm.maintainTax.maxEndDate.error");
                }
                if (UpdateIndicator.UPDATED.equals(record.getUpdateIndicator())) {
                    validateForEditFields(record, inputRecords);
                }
                String returnCode = getTaxDAO().validateTaxRates(record);
                if (!StringUtils.isBlank(returnCode)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainTax.saveTaxInfo.error", new String[]{returnCode});
                }
            }
            
            // overlap validation
            ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
                    TaxFields.EFFECTIVE_FROM_DATE, 
                    TaxFields.EFFECTIVE_TO_DATE, 
                    TaxFields.PREMIUM_TAX_RECORD_ID, 
                    "pm.maintainTax.overlapDates.error",
                    new String[] { TaxFields.RISK_ID, TaxFields.TAX_LEVEL},
                    new String[0]);
            continuityValidator.validate(overlapRecords);

        } catch (ParseException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in parsing dates.", e);
            l.throwing(getClass().getName(), "validateAllTax", ae);
            throw ae;
        }
        
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Data.");
        }
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllTax");
        }
    }

    public TaxDAO getTaxDAO() {
        return m_taxDAO;
    }

    public void setTaxDAO(TaxDAO taxDAO) {
        m_taxDAO = taxDAO;
    }

    private TaxDAO m_taxDAO;

    private static final String DEFAULT_EFFECTIVE_TO = "01/01/3000";
}
