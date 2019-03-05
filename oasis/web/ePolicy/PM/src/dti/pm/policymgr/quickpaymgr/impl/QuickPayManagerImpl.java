package dti.pm.policymgr.quickpaymgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.quickpaymgr.QuickPayManager;
import dti.pm.policymgr.quickpaymgr.dao.QuickPayDAO;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class is an implementation for interface QuickPayManager
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   July 22, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/19/2010       dzhang      Update per Bill's comments.
 * 09/01/2010       dzhang      138000 - Modified saveAllQuickPayTransactionDetail.
 * 03/14/2016       eyin        169611 - Modified validateForQuickPayTransactionDetail(), remove Math.abs() for
 *                              qpAmount and qpEligAmout, due to Give QP $ is allowed on negative transactions.
 * ---------------------------------------------------
 */

public class QuickPayManagerImpl implements QuickPayManager {
    /**
     * To load all quick pay transaction data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay transaction data.
     */
    public RecordSet loadAllQuickPayTransaction(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllQuickPayTransaction", new Object[]{inputRecord});
        }

        RecordSet rs = getQuickPayDAO().loadAllQuickPayTransaction(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllQuickPayTransaction", rs);
        }
        return rs;

    }

    /**
     * To load all quick pay transaction history data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay transaction history data.
     */
    public RecordSet loadAllTransactionHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransactionHistory", new Object[]{inputRecord});
        }

        RecordLoadProcessor loadProcessor = new QuickPayEntitlementRecordLoadProcessor(inputRecord, getLastWipQuickPayTransactionLogId(inputRecord));
        RecordSet rs = getQuickPayDAO().loadAllTransactionHistory(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransactionHistory", rs);
        }
        return rs;

    }

    /**
     * To load all quick pay risks/coverages for transaction detail page.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay risks/coverages data.
     */
    public RecordSet loadAllRiskCoverageForTransactionDetail(Record inputRecord) {
        RecordLoadProcessor loadProcessor = new QuickPayTransactionDetailEntitlementRecordLoadProcessor(inputRecord, this);
        inputRecord.setFieldValue("qpTransLogId", inputRecord.getFieldValue("lastQpTransLogId"));
        RecordSet rs = loadAllRiskCoverage(inputRecord, loadProcessor);
        return rs;
    }

    /**
     * To load all quick pay risks/coverages for quick pay detail.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay risks/coverages data.
     */
    public RecordSet loadAllRiskCoverageForQuickPayDetail(Record inputRecord) {
        RecordSet rs = loadAllRiskCoverage(inputRecord, new DefaultRecordLoadProcessor());
        return rs;
    }

    /**
     * To load all quick pay risks/coverages data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay risks/coverages data.
     */
    public RecordSet loadAllRiskCoverage(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskCoverage", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs = getQuickPayDAO().loadAllRiskCoverage(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskCoverage", rs);
        }
        return rs;

    }


    /**
     * Get the initial values for manage quick pay search criteria
     * <p/>
     *
     * @param policyHeader policyHeader.
     * @return the result met the condition
     */
    public Record getInitialValuesForSearchCriteria(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForSearchCriteria");
        }

        Record result = new Record();
        result.setFieldValue("termBaseId", policyHeader.getTermBaseRecordId());
        result.setFieldValue("policyHolderName", policyHeader.getPolicyHolderName());
        result.setFieldValue("acctDate", DateUtils.formatDate(new Date()));
        result.setFieldValue("policyNo", policyHeader.getPolicyNo());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForSearchCriteria");
        }
        return result;
    }

    /**
     * To load quick pay summary data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a Record loaded with quick pay summary data.
     */
    public Record loadQuickPaySummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadQuickPaySummary", new Object[]{inputRecord});
        }

        Record output = getQuickPayDAO().loadQuickPaySummary(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadQuickPaySummary", output);
        }
        return output;
    }

    /**
     * To save all quick pay transaction detail data.
     * <p/>
     *
     * @param inputRecords input record with the passed request values.
     * @return the number of rows updated.
     */
    public String saveAllQuickPayTransactionDetail(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllQuickPayTransactionDetail", new Object[]{inputRecords});
        }

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.UPDATED}));

        validateForQuickPayTransactionDetail(changedRecords);

        Record record = changedRecords.getSummaryRecord();
        changedRecords.setFieldValueOnAll("termBaseId", record.getStringValue("termBaseId"));
        changedRecords.setFieldValueOnAll("origTransLogId", record.getStringValue("origTransId"));
        changedRecords.setFieldValueOnAll("currTransLogId", "");
        changedRecords.setFieldValueOnAll("acctDate", DateUtils.formatDate(new Date()));
        changedRecords.setFieldValueOnAll("qpPercent", "0");
        Iterator it = changedRecords.getRecords();
        //If qp_elig_amount > 0 , i_qp_disc_amount should be qp_amount * -1.
        //Otherwise, just qp_amount from risk/coverages record
        int index = 0;
        String transLogId = "";
        while (it.hasNext()) {
            Record r = (Record) it.next();
            float qpEligAmout = r.getFloatValue("riskCoverageQpEligAmount");
            if (qpEligAmout > 0) {
                r.setFieldValue("riskCoverageQpAmount", r.getFloatValue("riskCoverageQpAmount") * (-1));
            }

            if (index == 0) {
                r.setFieldValue("currTransLogId", "");
            }
            else {
                r.setFieldValue("currTransLogId", transLogId);
            }

            r.setFieldValue("transAppliedTermId", r.getStringValue("termBaseId"));
            r.setFieldValue("covgId", r.getStringValue("coverageId"));
            r.setFieldValue("deltaAmount", r.getStringValue("riskCoveragePremiumAmount"));
            r.setFieldValue("qpDiscAmount", r.getStringValue("riskCoverageQpAmount"));

            RecordSet resSet = getQuickPayDAO().saveQuickPay(r);
            Record returnRecord = resSet.getSummaryRecord();
            transLogId = returnRecord.getStringValue("currTransLogId");
            index++;
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("transLogId", transLogId);
        getQuickPayDAO().completeQuickPayTransaction(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllQuickPayTransactionDetail", transLogId);
        }

        return transLogId;
    }

    /**
     * To remove quick pay discount data.
     * <p/>
     *
     * @param inputRecords input record with the passed request values.
     */
    public void removeQuickPayDiscount(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeQuickPayDiscount", new Object[]{inputRecords});
        }
        Record sumRecord = inputRecords.getSummaryRecord();

        //currently only can submit one record every time.
        RecordSet submitRec = inputRecords.getSubSet(new RecordFilter("ROWNUM", sumRecord.getFieldValue("submitRownum")));
        if (submitRec.getSize() > 0) {
            Record record = submitRec.getFirstRecord();
            record.setFieldValue("termBaseId", sumRecord.getStringValue("termBaseId"));
            record.setFieldValue("acctDate", DateUtils.formatDate(new Date()));
            getQuickPayDAO().removeQuickPayDiscount(record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "removeQuickPayDiscount");
        }
    }

    /**
     * To give quick pay discount.
     * <p/>
     *
     * @param inputRecords input record with the passed request values.
     */
    public void addQuickPayDiscount(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addQuickPayDiscount", new Object[]{inputRecords});
        }
        Record sumRecord = inputRecords.getSummaryRecord();

        //currently only can submit one record every time.
        RecordSet submitRec = inputRecords.getSubSet(new RecordFilter("ROWNUM", sumRecord.getFieldValue("submitRownum")));
        if (submitRec.getSize() > 0) {
            Record record = submitRec.getFirstRecord();
            record.setFieldValue("termBaseId", sumRecord.getStringValue("termBaseId"));
            record.setFieldValue("acctDate", DateUtils.formatDate(new Date()));
            getQuickPayDAO().addQuickPayDiscount(record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addQuickPayDiscount");
        }
    }

    /**
     * To save all quick pay discount data.
     * <p/>
     *
     * @param inputRecords input record with the passed request values.
     */
    public void completeQuickPayTransaction (RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "completeQuickPayTransaction", new Object[]{inputRecords});
        }
        // Save the changes
        Record record = inputRecords.getSummaryRecord();
        if (record.hasStringValue("wipQpTransLogId") && !StringUtils.isBlank(record.getStringValue("wipQpTransLogId"))) {
            record.setFieldValue("transLogId", record.getStringValue("wipQpTransLogId"));
            getQuickPayDAO().completeQuickPayTransaction(record);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "completeQuickPayTransaction");
        }
    }

    /**
     * Validate for quick pay transaction detail.
     *
     * @param inputRecords a records loaded with user entered data
     */
    protected void validateForQuickPayTransactionDetail(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForQuickPayTransactionDetail", new Object[]{inputRecords});
        }

        //get validate recordset(inserted and updated, not changed) from input records
        RecordSet allRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.UPDATED, UpdateIndicator.INSERTED}));

        Iterator it = allRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String rowNum = String.valueOf(r.getRecordNumber() + 1);
            String rowId = rowNum;

            // Check QP Amount value
            if (r.hasStringValue("riskCoverageQpAmount")) {
                if (r.getFloatValue("riskCoverageQpAmount") == 0) {
                    MessageManager.getInstance().addErrorMessage("pm.quickPayTransactionDetail.qpAmountBlankOrZero.error",
                        new String[]{rowNum}, "riskCoverageQpAmount", rowId);
                }
                else {
                    float qpAmount = r.getFloatValue("riskCoverageQpAmount");
                    float qpEligAmout = r.getStringValue("riskCoverageQpEligAmount") == null ? 0 : r.getFloatValue("riskCoverageQpEligAmount");
                    if (qpAmount > qpEligAmout) {
                        MessageManager.getInstance().addErrorMessage("pm.quickPayTransactionDetail.qpAmountRange.error",
                            new String[]{rowNum}, "riskCoverageQpAmount", rowId);
                    }
                }
            }
            else {
                MessageManager.getInstance().addErrorMessage("pm.quickPayTransactionDetail.qpAmountBlankOrZero.error",
                    new String[]{rowNum}, "riskCoverageQpAmount", rowId);
            }

            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;

        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid quick pay transaction detail data.");
        }

        l.exiting(getClass().getName(), "validateForQuickPayTransactionDetail");
    }

    /**
     * Get last quick pay transaction log id.
     * <p/>
     *
     * @param inputRecord input record
     * @return String last quick pay transaction log id.
     */
    public String getLastQuickPayTransactionLogId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLastQuickPayTransactionLogId", new Object[]{inputRecord});
        }

        String lastQpTransLogId = getQuickPayDAO().getLastQuickPayTransactionLogId(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLastQuickPayTransactionLogId", lastQpTransLogId);
        }
        return lastQpTransLogId;
    }

    /**
     * To check if the quick pay discount can be given or not.
     *
     * @param inputRecord a record loaded with query conditions
     * @return YesNoFlag to indicate quick pay discount can be given or not.
     */
    public boolean isAddQuickPayAllowed (Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddQuickPayAllowed", new Object[]{inputRecord});
        }

        boolean isAddQuickPayAllowed = YesNoFlag.getInstance(getQuickPayDAO().isAddQuickPayAllowed(inputRecord)).booleanValue();

        l.exiting(getClass().getName(), "isAddQuickPayAllowed", isAddQuickPayAllowed);
        return isAddQuickPayAllowed;
    }

    /**
     * To delete the WIP data
     * <p/>
     *
     * @param inputRecord input record
     */
    public void deleteQuickPayWip(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteQuickPayWip", new Object[]{inputRecord});
        }

        getQuickPayDAO().deleteQuickPayWip(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteQuickPayWip");
        }
    }

    /**
     * To load all Original Transaction data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a RecordSet loaded with list of original transaction data.
     */
    public Record loadOriginalTransaction(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadOriginalTransaction", new Object[]{inputRecord});
        }

        Record output = getQuickPayDAO().loadOriginalTransaction(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadOriginalTransaction", output);
        }
        return output;
    }

    /**
     * To load all quick pay risks/coverages data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay risks/coverages data.
     */
    public RecordSet loadAllRiskCoverageForOriginalTransaction(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskCoverageForOriginalTransaction", new Object[]{inputRecord});
        }
        RecordSet rs = getQuickPayDAO().loadAllRiskCoverageForOriginalTransaction(inputRecord);
        RecordSet returnRs = isQuickPayEligibility(rs, inputRecord.getStringValue("openMode"));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskCoverageForOriginalTransaction", rs);
        }
        return returnRs;
    }

        /**
     * To check the eligibility for Quick Pay Discount.
     *
     * @param inputRecords a records loaded with user entered data
     * @param openMode     openMode
     * @return RecordSet recordset
     */
    protected RecordSet isQuickPayEligibility(RecordSet inputRecords, String openMode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isQuickPayEligibility", new Object[]{inputRecords});
        }

        RecordSet rs = inputRecords;
        if (openMode.equals("REMOVE")) {
            RecordSet validRs = rs.getSubSet(new RecordFilter("qpIndb", "Q"));
            if (validRs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.processQuickPay.noDiscountsToRemove.error");
            }
            rs = validRs;
        }

        if (openMode.equals("GIVEQPPERCENT") || openMode.equals("GIVEQPDISCOUNT")) {
            Iterator it = inputRecords.getRecords();
            int eligibleCount = 0;
            while (it.hasNext() && !(MessageManager.getInstance().hasErrorMessages())) {
                Record r = (Record) it.next();
                int qpCovgValid = r.getIntegerValue("qpCovgValid").intValue();
                float transAmount = Math.abs(r.getFloatValue("transAmount").floatValue());
                // Check if QP Discount already exists.
                if (qpCovgValid == 1) {
                    MessageManager.getInstance().addErrorMessage("pm.processQuickPay.discountExists.error");
                    break;
                }
                // Check if the current policy is short term policy
                if (qpCovgValid == 4) {
                    MessageManager.getInstance().addErrorMessage("pm.processQuickPay.shortTermPolicy.error");
                    break;
                }
                if (qpCovgValid == 5) {
                    MessageManager.getInstance().addErrorMessage("pm.processQuickPay.validateProcedureFailed.error");
                    break;
                }

                if (qpCovgValid != 2 && qpCovgValid != 3 && transAmount > 0) {
                    eligibleCount++;
                }
            }

            if (eligibleCount == 0) {
                MessageManager.getInstance().addErrorMessage("pm.processQuickPay.invalidTransAmount.error");
            }

            Record record = rs.getSummaryRecord();
            record.setFieldValue("eligibleCount", eligibleCount);
            rs.setSummaryRecord(record);
        }

        l.exiting(getClass().getName(), "isQuickPayEligibility");

        return rs;
    }

    /**
     * To save all quick pay transaction detail data.
     * <p/>
     *
     * @param inputRecords input record with the passed request values.
     */
    public void saveAllRiskCoverageForOriginalTransaction(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRiskCoverageForOriginalTransaction", new Object[]{inputRecords});
        }

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.UPDATED}));

        Record record = changedRecords.getSummaryRecord();
        changedRecords.setFieldValueOnAll("termBaseId", record.getStringValue("termBaseId"));
        changedRecords.setFieldValueOnAll("origTransLogId", record.getStringValue("transactionLogId"));
        changedRecords.setFieldValueOnAll("acctDate", record.getStringValue("qpaccountingdate"));
        Iterator it = changedRecords.getRecords();
        //If qp_elig_amount > 0 , i_qp_disc_amount should be qp_amount * -1.
        //Otherwise, just qp_amount from risk/coverages record
        int index = 0;
        String transLogId = "";
        while (it.hasNext()) {
            Record r = (Record) it.next();
            if (index == 0) {
                r.setFieldValue("currTransLogId", "");
            }
            else {
                r.setFieldValue("currTransLogId", transLogId);
            }

            r.setFieldValue("transAppliedTermId", r.getStringValue("termBaseId"));
            r.setFieldValue("covgId", r.getStringValue("coverageId"));
            r.setFieldValue("deltaAmount", r.getStringValue("deltaAmount"));
            r.setFieldValue("qpDiscAmount", r.getStringValue("quickPayAmount"));
            r.setFieldValue("qpPercent", r.getStringValue("quickPayPercent"));

            RecordSet resSet = getQuickPayDAO().saveQuickPay(r);
            Record returnRecord = resSet.getSummaryRecord();
            transLogId = returnRecord.getStringValue("currTransLogId");
            index++;
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("transLogId", transLogId);
        getQuickPayDAO().completeQuickPayTransaction(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllRiskCoverageForOriginalTransaction");
        }
    }

    /**
     * To check if the coverage payor is a hospital.
     *
     * @param inputRecord input record.
     * @return true or false.
     */
    public boolean isHospitalCoveragePayor(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isHospitalCoveragePayor", new Object[]{inputRecord});
        }

        String flag = getQuickPayDAO().isHospitalCoveragePayor(inputRecord);
        boolean isHospitalCoveragePayor = YesNoFlag.getInstance(flag).booleanValue();
        l.exiting(getClass().getName(), "isHospitalCoveragePayor", isHospitalCoveragePayor);
        return isHospitalCoveragePayor;
    }

    /**
     * To load quick pay transaction summary data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a Record loaded with quick pay transaction summary data.
     */
    public Record loadTransactionSummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTransactionSummary", new Object[]{inputRecord});
        }

        Record output = getQuickPayDAO().loadTransactionSummary(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTransactionSummary", output);
        }
        return output;
    }

    /**
     * To get last wip quick pay transaction log id
     * <p/>
     *
     * @param inputRecord input record.
     * @return last wip quick pay transaction log id
     */
    public String getLastWipQuickPayTransactionLogId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLastQuickPayTransactionLogId", new Object[]{inputRecord});
        }

        String lastQpTransLogId = getQuickPayDAO().getLastWipQuickPayTransactionLogId(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLastQuickPayTransactionLogId", lastQpTransLogId);
        }
        return lastQpTransLogId;
    }

    /**
     * get current DAO
     * <p/>
     *
     * @return current DAO
     */
    public QuickPayDAO getQuickPayDAO() {
        return m_quickPayDAO;
    }

    /**
     * set current DAO
     * <p/>
     *
     * @param quickPayDAO QuickPayDAO
     */
    public void setQuickPayDAO(QuickPayDAO quickPayDAO) {
        m_quickPayDAO = quickPayDAO;
    }

    /**
     * verify config
     */
    public void verifyConfig() {
        if (getQuickPayDAO() == null)
            throw new ConfigurationException("The required property 'getQuickPayDAO' is missing.");
    }

    public QuickPayManagerImpl() {
    }

    private QuickPayDAO m_quickPayDAO;
}
