package dti.pm.riskmgr.nationalprogrammgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.nationalprogrammgr.NationalProgramFields;
import dti.pm.riskmgr.nationalprogrammgr.NationalProgramManager;
import dti.pm.riskmgr.nationalprogrammgr.dao.NationalProgramDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of NationalProgramManager Interface.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 25, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/09/2011       dzhang      123803 - Modified validateOverLapForNationalProgram.
 * 08/20/2015       ssheng      165340 - Modified getNationalProgramRecordMode.
 * 11/19/2015       eyin        167171 - Modified validateAllNationalProgram(), Add logic to make sure both eff date and exp date are not null.
 * ---------------------------------------------------
 */
public class NationalProgramManagerImpl implements NationalProgramManager {

    /**
     * Returns a RecordSet loaded with list of available national programs for the provided risk.
     * <p/>
     * param policyHeader policy header that contains all key policy information.
     *
     * @param inputRecord input records that contains key information
     * @return RecordSet a RecordSet loaded with list of available national programs.
     */
    public RecordSet loadAllNationalProgram(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllNationalProgram", new Object[]{inputRecord});
        }

        //set record mode
        PMCommonFields.setRecordModeCode(inputRecord, getNationalProgramRecordMode(policyHeader));
        RecordLoadProcessor entitlementRLP = new NationalProgramEntitlementRecordLoadProcessor(policyHeader);
        RecordLoadProcessor rowStyleLp = new NationalProgramRowStyleRecordLoadProcessor();
        RecordLoadProcessor loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(rowStyleLp, entitlementRLP);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            origFieldLoadProcessor, loadProcessor);
        inputRecord.setFields(policyHeader.toRecord());
        RecordSet rs = getNationalProgramDAO().loadAllNationalProgram(inputRecord, loadProcessor);

        // Calculate dates
        Record dateRecord = getNationalProgramDAO().calculateRiskDatesForNationalProgram(inputRecord);
        rs.getSummaryRecord().setFields(dateRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllNationalProgram", rs);
        }
        return rs;
    }


    /**
     * Get initial values for add national program
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record that contains key information
     * @return a Record loaded with initial values
     */
    public Record getInitialValuesForAddNationalProgram(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddNationalProgram", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();
        if (policyHeader.isWipB()) {
            String transEff = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
            String riskEff = RiskFields.getRiskEffectiveFromDate(inputRecord);

            if (DateUtils.parseDate(riskEff).after(DateUtils.parseDate(transEff))) {
                NationalProgramFields.setEffectiveFromDate(outputRecord, riskEff);
            }
            else {
                NationalProgramFields.setEffectiveFromDate(outputRecord, transEff);
            }
        }
        else {
            NationalProgramFields.setEffectiveFromDate(outputRecord, RiskFields.getRiskEffectiveFromDate(inputRecord));
        }

        NationalProgramFields.setRiskBaseRecordId(outputRecord, NationalProgramFields.getRiskBaseRecordId(inputRecord));
        NationalProgramFields.setOfficialRecordId(outputRecord, "");
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.TEMP);
        NationalProgramFields.setClosingTransLogId(outputRecord, "");

        // Set original values
        origFieldLoadProcessor.postProcessRecord(outputRecord, true);
        NationalProgramEntitlementRecordLoadProcessor.setInitialEntitlementValuesForNationalProgram(policyHeader, outputRecord);

        // Setup initial row style
        NationalProgramRowStyleRecordLoadProcessor.setInitialEntitlementValuesForRowStyle(outputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddNationalProgram", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Save all data in National Program page
     *
     * @param policyHeader   policy header that contains all key policy information.
     * @param inputRecordSet a record set with data to be saved
     */
    public void saveAllNationalProgram(PolicyHeader policyHeader, RecordSet inputRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllNationalProgram", new Object[]{inputRecordSet});
        }

        // Validate data
        validateAllNationalProgram(inputRecordSet);

        // Get changedRecords
        RecordSet changedInactiveRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(inputRecordSet);

        // Insert/Update/Delete National Programs
        RecordSet changedRecords = changedInactiveRecords.getSubSet(
            new UpdateIndicatorRecordFilter((new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.DELETED})));

        changedRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
        changedRecords.setFieldValueOnAll(PolicyFields.POLICY_ID, policyHeader.getPolicyId());
        getNationalProgramDAO().saveAllNationalProgram(changedRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllNationalProgram");
        }
    }


    /**
     * Validate all National Program data
     *
     * @param inputRecords a record set with data to be validated
     */
    protected void validateAllNationalProgram(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllNationalProgram", new Object[]{inputRecords});
        }

        RecordSet programRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        programRecords.setFieldValueOnAll(RiskFields.RISK_EFFECTIVE_FROM_DATE,
            RiskFields.getRiskEffectiveFromDate(inputRecords.getSummaryRecord()), false);
        programRecords.setFieldValueOnAll(RiskFields.RISK_EFFECTIVE_TO_DATE,
            RiskFields.getRiskEffectiveToDate(inputRecords.getSummaryRecord()), false);

        Iterator programItor = programRecords.getRecords();
        while (programItor.hasNext()) { // loop for national programs
            Record programRec = (Record) programItor.next();
            String rowNum = String.valueOf(programRec.getRecordNumber() + 1);

            // Validate the required fields.
            if (!programRec.hasStringValue(NationalProgramFields.NATIONAL_PROGRAM_CODE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainNationalProgram.programRequired.error", new String[]{rowNum}, NationalProgramFields.NATIONAL_PROGRAM_CODE, rowNum);
            }
            if (!programRec.hasStringValue(NationalProgramFields.PRIMARY_B)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainNationalProgram.primaryRequired.error", new String[]{rowNum}, NationalProgramFields.PRIMARY_B, rowNum);
            }
            if (!programRec.hasStringValue(NationalProgramFields.EFFECTIVE_TO_DATE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainNationalProgram.expirationDateRequired.error", new String[]{rowNum}, NationalProgramFields.EFFECTIVE_TO_DATE, rowNum);
            }

            // Break if there is any error message.
            if (MessageManager.getInstance().hasErrorMessages()) {
                break;
            }

            // Validate program's effective form/to date.
            Date effDate = programRec.getDateValue(NationalProgramFields.EFFECTIVE_FROM_DATE);
            Date expDate = programRec.getDateValue(NationalProgramFields.EFFECTIVE_TO_DATE);
            if (effDate != null && expDate != null && effDate.after(expDate)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainNationalProgram.fromDateBeforeToDate.error",
                    new String[]{rowNum}, NationalProgramFields.EFFECTIVE_TO_DATE, rowNum);
            }

            // Start and End date of program must be must be within the risk effective date period
            String sRiskEffDate = RiskFields.getRiskEffectiveFromDate(programRec);
            String sRiskExpDate = RiskFields.getRiskEffectiveToDate(programRec);
            Date riskEffDate = DateUtils.parseDate(sRiskEffDate);
            Date riskExpDate = DateUtils.parseDate(sRiskExpDate);
            if ((effDate != null && effDate.before(riskEffDate))
                || (expDate != null && expDate.after(riskExpDate))) {
                String fieldId = NationalProgramFields.EFFECTIVE_TO_DATE;
                if (effDate.before(riskEffDate))
                    fieldId = NationalProgramFields.EFFECTIVE_FROM_DATE;
                MessageManager.getInstance().addErrorMessage("pm.maintainNationalProgram.dateOutsideTermDates.error",
                    new String[]{rowNum}, fieldId, rowNum);
            }

            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(programRec);
            if (recordModeCode.isOfficial()
                && !NationalProgramFields.getPrimaryB(programRec).equals(NationalProgramFields.getOriginalPrimaryB(programRec))
                && !NationalProgramFields.getEffectiveToDate(programRec).equals(NationalProgramFields.getOriginalEffectiveToDate(programRec))) {

                MessageManager.getInstance().addErrorMessage("pm.maintainNationalProgram.PrimaryBExpiratinoDate.rule.error",
                    new String[]{rowNum}, NationalProgramFields.EFFECTIVE_TO_DATE, rowNum);
            }

            // Break if there is any error message.
            if (MessageManager.getInstance().hasErrorMessages()) {
                break;
            }
        } // end of loop

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid National Program data.");
        }

        //validation #1,#2,#3,#4.
        validateOverLapForNationalProgram(inputRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllNationalProgram");
        }
    }

    /**
     * Overlap validation for national program.
     *
     * @param inputRecords a record set with data to be validated
     */
    private void validateOverLapForNationalProgram(RecordSet inputRecords) {

        //construct a sorted date set
        SortedSet dateSet = new TreeSet();
        RecordSet programRs = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}))
            .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));

        // Validation #2 overlap between national programs with same program code.
        String[] keyFieldNames = new String[]{NationalProgramFields.NATIONAL_PROGRAM_CODE};
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
            NationalProgramFields.EFFECTIVE_FROM_DATE,
            NationalProgramFields.EFFECTIVE_TO_DATE,
            NationalProgramFields.NATIONAL_PROGRAM_ID,
            "pm.maintainNationalProgram.overLapForSameNationalProgram.error", keyFieldNames, keyFieldNames);
        continuityValidator.validate(programRs);

        Iterator itor = programRs.getRecords();
        while (itor.hasNext()) {
            Record record = (Record) itor.next();
            Date effDate = DateUtils.parseDate(NationalProgramFields.getEffectiveFromDate(record));
            Date expDate = DateUtils.parseDate(NationalProgramFields.getEffectiveToDate(record));
            if (!(effDate.compareTo(expDate) == 0)) {
                dateSet.add(effDate);
                dateSet.add(expDate);
            }
        }
        //dateArray is a sorted date array .
        Date[] dateArray = (Date[]) (dateSet.toArray(new Date[0]));
        List rsList = new ArrayList();
        for (int i = 0; i < dateArray.length - 1; i++) {
            RecordSet rs = new RecordSet();
            Record summaryRecord = rs.getSummaryRecord();
            summaryRecord.setFieldValue("beginDate", dateArray[i]);
            summaryRecord.setFieldValue("endDate", dateArray[i + 1]);
            itor = programRs.getRecords();
            while (itor.hasNext()) {
                Record record = (Record) itor.next();
                Date effDate = DateUtils.parseDate(NationalProgramFields.getEffectiveFromDate(record));
                Date expDate = DateUtils.parseDate(NationalProgramFields.getEffectiveToDate(record));
                //if this record contains the time period
                if ((!effDate.after(dateArray[i])) && (!expDate.before(dateArray[i + 1]))) {
                    rs.addRecord(record);
                }
            }
            rsList.add(rs);
        }
        //loop per time period
        itor = rsList.iterator();

        while (itor.hasNext()) {
            RecordSet rs = (RecordSet) itor.next();
            if (rs.getSize() >= 1) {
                Iterator rsItor = rs.getRecords();
                Set set = new HashSet();
                int primaryNationalProgramCount = 0;
                String effDate = "";
                String expDate = "";
                String rowNum = "";
                while (rsItor.hasNext()) {
                    Record record = (Record) rsItor.next();
                    rowNum = String.valueOf(record.getRecordNumber() + 1);
                    String nationalProgramCode = NationalProgramFields.getNationalProgramCode(record);
                    String primaryProgramB = NationalProgramFields.getPrimaryB(record);
                    String key = nationalProgramCode;
                    effDate = NationalProgramFields.getEffectiveFromDate(record);
                    expDate = NationalProgramFields.getEffectiveToDate(record);
                    
                    if (!set.contains(key)) {
                        set.add(key);
                    }

                    if (YesNoFlag.getInstance(primaryProgramB).booleanValue()) {
                        primaryNationalProgramCount++;
                    }
                }

                //validation #2: There must be one primary national program during any time range.
                if (primaryNationalProgramCount == 0) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainNationalProgram.noPrimaryNationalProgram.error",
                        new String[]{
                            FormatUtils.formatDateForDisplay(effDate),
                            FormatUtils.formatDateForDisplay(expDate)});
                }

                //validation #2: Only one primary national program allowed during overlap time range.
                if (primaryNationalProgramCount > 1) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainNationalProgram.overLapForPrimaryNationalProgramCount.error", new String[]{rowNum});
                }
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid National Program data.");
        }
    }

    /**
     * get recordMode according to screenMode
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return recordMode recordMode
     */
    private RecordMode getNationalProgramRecordMode(PolicyHeader policyHeader) {
        RecordMode recordMode = policyHeader.getRecordMode();
        return recordMode;
    }

    public NationalProgramDAO getNationalProgramDAO() {
        return m_nationalProgramDAO;
    }

    public void setNationalProgramDAO(NationalProgramDAO nationalProgramDAO) {
        m_nationalProgramDAO = nationalProgramDAO;
    }

    public void verifyConfig() {
        if (getNationalProgramDAO() == null)
            throw new ConfigurationException("The required property 'nationalProgramDAO' is missing.");
    }

    private NationalProgramDAO m_nationalProgramDAO;
    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{NationalProgramFields.PRIMARY_B, NationalProgramFields.EFFECTIVE_TO_DATE});
}