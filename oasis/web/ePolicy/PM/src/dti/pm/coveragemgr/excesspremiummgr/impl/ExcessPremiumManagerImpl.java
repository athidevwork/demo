package dti.pm.coveragemgr.excesspremiummgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.coveragemgr.excesspremiummgr.ExcessPremiumManager;
import dti.pm.coveragemgr.excesspremiummgr.dao.ExcessPremiumDAO;
import dti.pm.policymgr.PolicyHeader;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle business logics for manual excess premium.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 01, 2009
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/08/2011       wfu         issue 117176 - Modified method saveAllExcessPremium to let zero as valid entry.
 * ---------------------------------------------------
 */
public class ExcessPremiumManagerImpl implements ExcessPremiumManager {
    /**
     * Load all manual excess premium.
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllExcessPremium(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllExcessPremium", new Object[]{policyHeader, inputRecord});
        }

        Record rec = new Record();
        if (inputRecord.hasStringValue("transactionLogId")) {
            rec.setFieldValue("transLogId", inputRecord.getStringValue("transactionLogId"));
        }
        else {
            rec.setFieldValue("transLogId", policyHeader.getLastTransactionId());
        }
        rec.setFieldValue("termId", policyHeader.getTermBaseRecordId());
        rec.setFieldValue("summaryYn", "N");
        boolean fromCoverage = false;
        if (inputRecord.hasStringValue("fromCoverage")) {
            fromCoverage = YesNoFlag.getInstance(inputRecord.getStringValue("fromCoverage")).booleanValue();
        }
        if (fromCoverage) {
            rec.setFieldValue("prodCov", inputRecord.getStringValue("productCoverageCode"));
            rec.setFieldValue("effDt", inputRecord.getStringValue("coverageEffectiveFromDate"));
            rec.setFieldValue("stateCode", inputRecord.getStringValue("practiceStateCode"));
            rec.setFieldValue("limitCode", inputRecord.getStringValue("coverageLimitCode"));
        }
        else {
            rec.setFieldValue("prodCov", null);
            rec.setFieldValue("effDt", null);
            rec.setFieldValue("stateCode", null);
            rec.setFieldValue("limitCode", null);
        }

        RecordLoadProcessor loadProcessor = new ExcessPremiumRecordLoadProcessor();
        RecordSet rs = getExcessPremiumDAO().loadAllExcessPremium(rec, loadProcessor);

        rs.getSummaryRecord().setFieldValue("isRefreshAvailable", YesNoFlag.Y);
        rs.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.Y);
        // Hide "save" and "refresh" button if it is opended from "transaction" page
        PolicyViewMode policyViewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        if (!fromCoverage) {
            rs.getSummaryRecord().setFieldValue("isRefreshAvailable", YesNoFlag.N);
            rs.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else if (policyViewMode.isOfficial() || policyViewMode.isEndquote()) {
            rs.getSummaryRecord().setFieldValue("isRefreshAvailable", YesNoFlag.N);
            rs.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.N);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllExcessPremium", rs);
        }
        return rs;
    }

    /**
     * Load all manual excess premium summary.
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllExcessPremiumSummary(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllExcessPremiumSummary", new Object[]{policyHeader,});
        }

        Record rec = new Record();
        rec.setFieldValue("transLogId", policyHeader.getLastTransactionId());
        rec.setFieldValue("termId", policyHeader.getTermBaseRecordId());
        rec.setFieldValue("summaryYn", "Y");
        rec.setFieldValue("prodCov", null);
        rec.setFieldValue("effDt", null);
        rec.setFieldValue("stateCode", null);
        rec.setFieldValue("limitCode", null);

        RecordLoadProcessor loadProcessor = new ExcessPremiumRecordLoadProcessor();
        RecordSet rs = getExcessPremiumDAO().loadAllExcessPremium(rec, loadProcessor);

        // Hide "save" & "refrsh" option if it displays summary information
        rs.getSummaryRecord().setFieldValue("isRefreshAvailable", YesNoFlag.N);
        rs.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.N);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllExcessPremiumSummary", rs);
        }
        return rs;
    }

    /**
     * Validate all manual excess premium.
     *
     * @param inputRecord
     * @param inputRecords
     */
    public void validateAllExcessPremium(Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllExcessPremium", new Object[]{inputRecord, inputRecords});
        }

        RecordSet diffRecord = inputRecords.getSubSet(new RecordFilter("rowType", "DIFF"));
        String columnNumber = "";
        if (diffRecord.getSize() > 0) {
            Record rec = diffRecord.getFirstRecord();
            for (int i = 1; i <= 5; i++) {
                String columnName = "layerAmount" + i;
                if (rec.hasStringValue(columnName) && rec.getDoubleValue(columnName).doubleValue() != 0) {
                    if (StringUtils.isBlank(columnNumber)) {
                        columnNumber += i;
                    }
                    else {
                        columnNumber += "," + i;
                    }
                }
            }
        }
        if (!StringUtils.isBlank(columnNumber) && !ConfirmationFields.isConfirmed(
            "pm.excessPremium.Confirm.ifContinue", inputRecord)) {
            MessageManager.getInstance().addConfirmationPrompt("pm.excessPremium.Confirm.ifContinue", new String[]{columnNumber});
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts()) {
            throw new ValidationException("Invalid manual excess premium data");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllExcessPremium");
        }
    }

    /**
     * Get all columns for manual excess premium. It contains five columns.
     *
     * @return RecordSet
     */
    public RecordSet getAllExcessPremiumColumn() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllExcessPremiumColumn");
        }

        Record rec = new Record();
        RecordSet rs = new RecordSet();
        // Popluate all five columns into recordset
        for (int i = 1; i <= 5; i++) {
            rec.setFieldValue("colNo", String.valueOf(i));
            Record returnRecord = getExcessPremiumDAO().getExcessPremiumColumn(rec);
            rs.addRecord(returnRecord);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllExcessPremiumColumn", rs);
        }
        return rs;
    }

    /**
     * Re-calculate all manual excess premium.
     *
     * @param inputRecords
     */
    public void calculateAllExcessPremium(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "calculateAllExcessPremium", new Object[]{inputRecords});
        }

        // For each amount columns (1 to 5), repeat these steps:
        // 1.Calculate column total by adding the amount of rows with row_type 'LAYER', and set it to the row with row_type 'TOTAL.
        // 2.Calculate column differences by subtracting the amount of row with row_type 'DELTA' from the column total, and set it to the row with row_type 'DIFF'.
        // For each row, calculate row total by adding amount columns (1 to 5) of the row and set it to the row total column.
        Iterator it = inputRecords.getRecords();
        double[] columnTotals = new double[5];
        double[] deltaValues = new double[5];
        while (it.hasNext()) {
            Record rec = (Record) it.next();
            String rowType = rec.getStringValue("rowType");
            double rowTotal = 0;
            for (int i = 0; i < 5; i++) {
                String columnName = "layerAmount" + (i + 1);
                double curAmt = 0;
                if (rec.hasStringValue(columnName)) {
                    curAmt = rec.getDoubleValue(columnName).doubleValue();
                }
                if ("LAYER".equals(rowType)) {
                    columnTotals[i] += curAmt;
                }
                else if ("TOTAL".equals(rowType)) {
                    curAmt = columnTotals[i];
                    rec.setFieldValue(columnName, new Double(curAmt));
                }
                else if ("DELTA".equals(rowType)) {
                    deltaValues[i] = curAmt;
                }
                else if ("DIFF".equals(rowType)) {
                    curAmt = columnTotals[i] - deltaValues[i];
                    rec.setFieldValue(columnName, new Double(curAmt));
                }
                rowTotal += curAmt;
            }
            rec.setFieldValue("rowTotal", new Double(rowTotal));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "calculateAllExcessPremium");
        }
    }

    /**
     * Save all manual excess premium.
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     */
    public void saveAllExcessPremium(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllExcessPremium", new Object[]{policyHeader, inputRecord, inputRecords});
        }
        // Re-calculate
        calculateAllExcessPremium(inputRecords);
        // Get changed records
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        if (updatedRecords.getSize() > 0) {
            // Validations
            validateAllExcessPremium(inputRecord, inputRecords);
            updatedRecords.setFieldValueOnAll("transLogId", policyHeader.getLastTransactionId());
            updatedRecords.setFieldValueOnAll("insOrDel", "I");

            // Get value of pm_excess_prem_layer_fk from the row with row_type 'DELTA'
            RecordSet deltaRecord = inputRecords.getSubSet(new RecordFilter("rowType", "DELTA"));
            if (deltaRecord.getSize() > 0) {
                String sPmExcessPremLayerId = deltaRecord.getFirstRecord().getStringValue("pmExcessPremLayerId");
                updatedRecords.setFieldValueOnAll("pmExcessPremLayerId", sPmExcessPremLayerId);
            }

            // Issue 117176: Remove below logic and let the zero as valid entry.
            // Update all 0 value as NULL
//            Iterator it = updatedRecords.getRecords();
//            while (it.hasNext()) {
//                Record rec = (Record) it.next();
//                for (int j = 1; j <= 5; j++) {
//                    String columnName = "layerAmount" + j;
//                    if (rec.hasStringValue(columnName) && rec.getDoubleValue(columnName).doubleValue() == 0) {
//                        rec.setFieldValue(columnName, null);
//                    }
//                }
//            }
            getExcessPremiumDAO().saveAllExcessPremium(updatedRecords);
        }

        if (l.isLoggable(Level.FINER)) {

            l.exiting(getClass().getName(), "saveAllExcessPremium");
        }
    }

    /**
     * Verify configuration
     */
    public void verifyConfig() {
        if (getExcessPremiumDAO() == null) {
            throw new ConfigurationException("The required property 'excessPremiumDAO' is missing.");
        }

    }

    /**
     * Get ExcessPremiumDAO
     *
     * @return ExcessPremiumDAO
     */
    public ExcessPremiumDAO getExcessPremiumDAO() {
        return m_excessPremiumDAO;
    }

    /**
     * Set ExcessPremiumDAO
     *
     * @param excessPremiumDAO
     */
    public void setExcessPremiumDAO(ExcessPremiumDAO excessPremiumDAO) {
        m_excessPremiumDAO = excessPremiumDAO;
    }

    private ExcessPremiumDAO m_excessPremiumDAO;
}
