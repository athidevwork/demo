package dti.pm.policymgr.premiummgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.SysParmProvider;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.premiummgr.PremiumFields;
import dti.pm.policymgr.premiummgr.PremiumManager;
import dti.pm.policymgr.premiummgr.PremiumAccountingFields;
import dti.pm.policymgr.premiummgr.dao.PremiumDAO;
import dti.pm.policyreportmgr.PolicyReportManager;
import dti.pm.riskmgr.RiskFields;
import dti.pm.transactionmgr.TransactionFields;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;

/**
 * This class provides the implementation details for PremiumManager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   June 15, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/12/07         JMP         Add setting of default record from WebWB
 * 03/26/2008       yhyang      loadAllPayment added for issue 78337
 * 12/25/2008       yhyang      Add validateTransactionForPremiumWorksheet for issue #88884
 * 06/15/2011       syang       111676 - Modified loadAllRatingLog() to handle the maximum number of rating log.
 * 08/01/2011       ryzhao      118806 - Added getLatestTaxTransaction(),
 *                                             getLatestFeeSurchargeTransaction(),
 *                                             getLatestAllTransaction().
 *                                       Modified loadAllFund() to get different transaction id per detail type.
 * 10/11/2011       fcb         125838 - Changes due to move of filtering of data from JS to DB 
 * 03/06/2012       syang       131134 - Modified loadAllPremium() to overwrite riskBaseRecordId. The riskBaseRecordId
 *                              is -1 for "All" option, -1 is handled in back-end procedure.
 * ---------------------------------------------------
 */

public class PremiumManagerImpl implements PremiumManager {

    /**
     * Retrieves all premium' information
     *
     * @param policyHeader policy header
     * @param inputRecord  inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPremium(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPremium", new Object[]{policyHeader});
        }
        String transactionId = "";
        if (inputRecord.hasStringValue("transactionId")) {
            transactionId = inputRecord.getStringValue("transactionId");
        }
        else if (inputRecord.hasStringValue("transactionLogId")) {
            transactionId = inputRecord.getStringValue("transactionLogId");
        }

        if (StringUtils.isBlank(transactionId)) {
            transactionId = (new Long(getLatestPremiumTransaction(policyHeader))).toString();
        }
        Record input = new Record();
        input.setFields(policyHeader.toRecord(), false);
        input.setFieldValue("transactionLogId", transactionId);

        // Since PolicyHeader contains info for the term that is currently displayed in Policy Folder,
        // when retrieving data for transaction snapshot we need to replace the term base record FK in
        // it with the term selected from the transaction snapshot. In this case the termBaseRecordId
        // has been specifically passed in the input record.
        if (inputRecord.hasStringValue("termBaseRecordId")){
            String termBaseRecordId = inputRecord.getStringValue("termBaseRecordId");
            if (!StringUtils.isBlank(termBaseRecordId)) {
                input.setFieldValue("termBaseRecordId", termBaseRecordId);
            }
        }

        if (inputRecord.hasStringValue("premiumType")){
            String premiumType = inputRecord.getStringValue("premiumType");
            if (!StringUtils.isBlank(premiumType)) {
                input.setFieldValue("premiumType", premiumType);
            }
        }

        if (inputRecord.hasStringValue("changeRecord")){
            String changeRecord = inputRecord.getStringValue("changeRecord");
            if ("Y".equals(changeRecord) || "N".equals(changeRecord)) {
                input.setFieldValue("changeRecord", changeRecord);
            }
        }
        // The riskBaseRecordId exists in input already(setting via policyHeader.toRecord() above), it should be overwrote here.
        String riskBaseRecordId = "-1";
        if (inputRecord.hasStringValue(RiskFields.RISK_BASE_RECORD_ID)){
            riskBaseRecordId = RiskFields.getRiskBaseRecordId(inputRecord);
        }
        RiskFields.setRiskBaseRecordId(input, riskBaseRecordId);
        // Setup the entitlements load processor
        RecordLoadProcessor loadProcessor = new PremiumRecordLoadProcessor();
        
        // Get premium record set
        RecordSet rs = getPremiumDAO().loadAllPremium(input, loadProcessor);

        //set all page field into summaryRecord
        Record record = rs.getSummaryRecord();
        record.setFieldValue("transactionLogId", transactionId);
        if (rs.getSize() > 0) {
            record.setFields(rs.getFirstRecord());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPremium", rs);
        }
        return rs;
    }

    /**
     * get latest prem based transaction of policy
     *
     * @param policyHeader policy header
     * @return transactionId
     */

    protected long getLatestPremiumTransaction(PolicyHeader policyHeader) {
        Record input = new Record();
        input.setFields(policyHeader.toRecord(), false);
        return getPremiumDAO().getLatestPremiumTransaction(input);
    }

    /**
     * get latest tax based transaction of policy
     *
     * @param policyHeader policy header
     * @return transactionId
     */
    protected long getLatestTaxTransaction(PolicyHeader policyHeader) {
        return getPremiumDAO().getLatestTaxTransaction(policyHeader.toRecord());
    }

    /**
     * get latest fee/surcharge based transaction of policy
     *
     * @param policyHeader policy header
     * @return transactionId
     */
    protected long getLatestFeeSurchargeTransaction(PolicyHeader policyHeader) {
        return getPremiumDAO().getLatestFeeSurchargeTransaction(policyHeader.toRecord());
    }

    /**
     * get latest fund/tax/fee/surcharge based transaction of policy
     *
     * @param policyHeader policy header
     * @return transactionId
     */
    protected long getLatestAllTransaction(PolicyHeader policyHeader) {
        return getPremiumDAO().getLatestAllTransaction(policyHeader.toRecord());
    }

    /**
     * Retrieves all premium's rating log information
     *
     * @param policyHeader policy header
     * @param inputRecord  input Record(contains transactionId and showMoreFlag)
     * @return RecordSet
     */
    public RecordSet loadAllRatingLog(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRatingLog", new Object[]{policyHeader});
        }

        Record input = new Record();
        String transactionId = inputRecord.getStringValue("transactionLogId");
        String showDetailFlag = inputRecord.getStringValue("showMoreFlag");
        RecordSet rs;
        boolean flag = isRatingLogExist(policyHeader);
        if (flag) {
            input.setFieldValue("transactionLogId", transactionId);
            input.setFieldValue("showDetailFlag", showDetailFlag);
            // Get premium record set
            input.setFields(inputRecord, false);
            RecordLoadProcessor loadProcessor = new RatingLogEntitlementRecordLoadProcessor(inputRecord);
            rs = getPremiumDAO().loadAllRatingLog(input, loadProcessor);
            // Display warning message.
            int maxRatingLog = Integer.parseInt(SysParmProvider.getInstance().getSysParm("PM_MAX_RATING_LOG", "1000"));
            if (rs.getSize() >= maxRatingLog) {
                MessageManager.getInstance().addWarningMessage("pm.viewPremiumAccounting.ratingLog.exceedMax", new String[]{String.valueOf(maxRatingLog)});
            }
        }
        else {
            rs = new RecordSet();
        }
        Record record = rs.getSummaryRecord();
        record.setFieldValue("transactionLogId", transactionId);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRatingLog", rs);
        }

        return rs;
    }

    /**
     * Retrieves all member contribution info
     *
     * @param inputRecord (transactionId and riskId and termId)
     * @return RecordSet
     */
    public RecordSet loadAllMemberContribution(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMemberContribution", new Object[]{inputRecord});
        }

        RecordLoadProcessor loadProcessor = new MemberContributionRecordLoadProcessor();
        RecordSet rs = getPremiumDAO().loadAllMemberContribution(inputRecord, loadProcessor);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMemberContribution", rs);
        }
        return rs;
    }

    /**
     * Retrieves all layer detial info
     *
     * @param inputRecord (transactionId and coverageId and termId)
     * @return RecordSet
     */
    public RecordSet loadAllLayerDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllLayerDetail", new Object[]{inputRecord});
        }
        RecordLoadProcessor loadProcessor = new LayerDetailRecordLoadProcessor();
        RecordSet rs = getPremiumDAO().loadAllLayerDetail(inputRecord, loadProcessor);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllLayerDetail", rs);
        }
        return rs;
    }

    /**
     * Retrieves all fund information
     *
     * @param policyHeader policy header
     * @param inputRecord  inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllFund(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllFund", new Object[]{policyHeader});
        }
        
        RecordSet rs;

        String transactionId = PremiumFields.getTransactionId(inputRecord);
        String detailType = PremiumFields.getDetailType(inputRecord);

        if (StringUtils.isBlank(transactionId)) {
            if (PremiumFields.DetailTypeCodeValues.ALL.equalsIgnoreCase(detailType)) {
                transactionId = (new Long(getLatestAllTransaction(policyHeader))).toString();
            }
            else if (PremiumFields.DetailTypeCodeValues.TAX.equalsIgnoreCase(detailType)) {
                transactionId = (new Long(getLatestTaxTransaction(policyHeader))).toString();
            }
            else if (PremiumFields.DetailTypeCodeValues.FEE_SRCHG.equalsIgnoreCase(detailType)) {
                transactionId = (new Long(getLatestFeeSurchargeTransaction(policyHeader))).toString();
            }
            else {
                transactionId = (new Long(getLatestPremiumTransaction(policyHeader))).toString();
            }
        }

        if ("0".equals(transactionId)) {
            rs = new RecordSet();
        }
        else {
            Record input = policyHeader.toRecord();
            TransactionFields.setTransactionLogId(input, transactionId);
            PremiumFields.setDetailType(input, detailType);
            // Setup the entitlements load processor
            RecordLoadProcessor loadProcessor = new FundRecordLoadProcessor();
            // Get premium record set
            rs = getPremiumDAO().loadAllFund(input, loadProcessor);
            //set all page field into summaryRecord
            Record record = rs.getSummaryRecord();
            TransactionFields.setTransactionLogId(input, transactionId);
            //set transaction info
            if (rs.getSize() > 0) {
                record.setFields(rs.getFirstRecord());
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllFund", rs);
        }
        return rs;
    }

    /**
     * Retrieves all payment information
     *
     * @param policyHeader policy header
     * @return RecordSet
     */
    public RecordSet loadAllPayment(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPayment", new Object[]{policyHeader});
        }
        RecordSet rs;
        Record input = policyHeader.toRecord();
        // Get premium record set
        rs = getPremiumDAO().loadAllPayment(input);
        if((rs.getSize()) <= 0){
            MessageManager.getInstance().addErrorMessage("pm.viewPaymentInfo.paymentList.noDataFound");
        }
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPayment", rs);
        }
        return rs;
    }  

    /**
     * Validate if the term base id is the current term base id and whether the data is empty.
     *
     * @param inputRecord  inputRecord
     * @param conn         live JDBC Connection
     * @return boolean
     */
    public void validateTransactionForPremiumWorksheet(Record inputRecord, Connection conn) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateTransactionForPremiumWorksheet", new Object[]{inputRecord, conn});
        }
        // Validate the transaction.
        if (inputRecord.hasStringValue("termBaseId")) {
            String selectTermBaseId = inputRecord.getStringValue("termBaseId");
            String currentTermBaseId = inputRecord.getStringValue("currentTermBaseId");
            if (!selectTermBaseId.equals(currentTermBaseId)) {
                MessageManager.getInstance().addErrorMessage("pm.premiumworksheet.transactionLog.error");
                throw new ValidationException("Not the current term.");
            }
        }
        // Validate the data.
        if (getPolicyReportManager().isReportEmpty(inputRecord, conn)) {
            MessageManager.getInstance().addErrorMessage("pm.premiumworksheet.noData");
            throw new ValidationException("No Data found.");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateTransactionForPremiumWorksheet");
        }
    }

    /**
     * Get the default values for premium accounting date fields
     *
     * @param inputRecord input Record
     * @return RecordSet
     */
    public RecordSet getInitialValuesForPremiumAccounting(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForPremiumAccounting", new Object[]{inputRecord});
        }
        RecordSet rs = new RecordSet();
        Record outputRecord = rs.getSummaryRecord();
        // Set the parameters
        if (inputRecord.hasField(PremiumAccountingFields.POLICY_ID)) {
            PremiumAccountingFields.setPolicyId(outputRecord, PremiumAccountingFields.getPolicyId(inputRecord));
        }
        if (inputRecord.hasField(PremiumAccountingFields.TERM_EFFECTIVE_DATE)) {
            PremiumAccountingFields.setReportFromDate(outputRecord, PremiumAccountingFields.getTermEffectiveDate(inputRecord));
        }
        if (inputRecord.hasField(PremiumAccountingFields.TERM_EXPIRATION_DATE)) {
            PremiumAccountingFields.setReportToDate(outputRecord, PremiumAccountingFields.getTermExpirationDate(inputRecord));
        }
        if (inputRecord.hasField(PremiumAccountingFields.TRANS_EFF_DATE)) {
            PremiumAccountingFields.setEffectiveDate(outputRecord, PremiumAccountingFields.getTransEffDate(inputRecord));
        }
        if (inputRecord.hasField(PremiumAccountingFields.TRANS_ACCOUNT_DATE)) {
            PremiumAccountingFields.setAccountingDate(outputRecord, PremiumAccountingFields.getAccountingDate(inputRecord));
        }
        List nameList = new ArrayList();
        nameList.add(PremiumAccountingFields.PREMIUM_ACCOUNT_ID);
        rs.addFieldNameCollection(nameList);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForPremiumAccounting", rs);
        }

        return rs;
    }

    /**
     * Generate the premium accounting data for selected transaction
     *
     * @param inputRecord input Record
     * @return RecordSet
     */
    public RecordSet generatePremiumAccounting(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePremiumAccounting", new Object[]{inputRecord});
        }
        RecordSet rs;
        long retCode = 0;
        // Validate the dates
        validateDatesForGenerating(inputRecord);
        // Generate permium accounting data
        Record outputRecord = getPremiumDAO().generatePremiumAccounting(inputRecord);
        if (outputRecord.hasField("retCode")) {
            retCode = outputRecord.getLongValue("retCode").longValue();
            if (retCode <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.viewPremiumAccounting.generate.error");
            }
            else
            if (outputRecord.hasField("ratedB") && !YesNoFlag.getInstance(outputRecord.getStringValue("ratedB")).booleanValue()) {
                MessageManager.getInstance().addErrorMessage("pm.viewPremiumAccounting.rate.error");
            }
            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("Failed to generate data.");
            }
        }

        PremiumAccountingFields.setReportId(inputRecord, String.valueOf(retCode));
        // Retrieve all premium accounting data
        rs = getPremiumDAO().loadAllPremiumAccounting(inputRecord);
        // The "total" record always exists, if the size is 1, it means there is no other data, we should remove this record.
        if (rs.getSize() == 1) {
            Record totalRecord = rs.getRecord(0);
            if (totalRecord.hasField("productCoverageCode") && "Total:".equalsIgnoreCase(totalRecord.getStringValue("productCoverageCode"))) {
                rs = new RecordSet();
                List nameList = new ArrayList();
                nameList.add(PremiumAccountingFields.PREMIUM_ACCOUNT_ID);
                rs.addFieldNameCollection(nameList);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generatePremiumAccounting", rs);
        }
        return rs;
    }

    /**
     * Validate dates for generate premium accounting data
     *
     * @param inputRecord input Record
     */
    protected void validateDatesForGenerating(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateDatesForGenerating", new Object[]{inputRecord});
        }
        // All the dates are required
        if (!inputRecord.hasField(PremiumAccountingFields.REPORT_FROM_DATE) ||
            StringUtils.isBlank(PremiumAccountingFields.getReportFromDate(inputRecord))) {
            MessageManager.getInstance().addErrorMessage("pm.viewPremiumAccounting.periodFrom.required");
        }
        if (!inputRecord.hasField(PremiumAccountingFields.REPORT_TO_DATE) ||
            StringUtils.isBlank(PremiumAccountingFields.getReportToDate(inputRecord))) {
            MessageManager.getInstance().addErrorMessage("pm.viewPremiumAccounting.periodTo.required");
        }
        if (!inputRecord.hasField(PremiumAccountingFields.EFFECTIVE_DATE) ||
            StringUtils.isBlank(PremiumAccountingFields.getEffectiveDate(inputRecord))) {
            MessageManager.getInstance().addErrorMessage("pm.viewPremiumAccounting.transactionDate.required");
        }
        if (!inputRecord.hasField(PremiumAccountingFields.ACCOUNTING_DATE) ||
            StringUtils.isBlank(PremiumAccountingFields.getAccountingDate(inputRecord))) {
            MessageManager.getInstance().addErrorMessage("pm.viewPremiumAccounting.accountingDate.required");
        }
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Permium accounting dates error.");
        }
        // Compare the two dates
        Date periodFromDate = DateUtils.parseDate(PremiumAccountingFields.getReportFromDate(inputRecord));
        Date periodToDate = DateUtils.parseDate(PremiumAccountingFields.getReportToDate(inputRecord));
        if (!periodFromDate.before(periodToDate)) {
            MessageManager.getInstance().addErrorMessage("pm.viewPremiumAccounting.periodDates.error");
            throw new ValidationException("Period to must be after period from.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateDatesForGenerating");
        }
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getPremiumDAO() == null)
            throw new ConfigurationException("The required property 'premiumDAO' is missing.");
        if (getPolicyReportManager() == null)
            throw new ConfigurationException("The required property 'policyReportManager' is missing.");
    }

    /**
     * judge if  rating log exist by policyId
     *
     * @param policyHeader policy header
     * @return boolean
     */
    protected boolean isRatingLogExist(PolicyHeader policyHeader) {
        Record input = new Record();
        input.setFieldValue("policyId", policyHeader.getPolicyId());
        return getPremiumDAO().isRatingLogExist(input);
    }

    public PolicyReportManager getPolicyReportManager() {
        return m_PolicyReportManager;
    }

    public void setPolicyReportManager(PolicyReportManager policyReportManager) {
        m_PolicyReportManager = policyReportManager;
    }

    public PremiumDAO getPremiumDAO() {
        return m_premiumDAO;
    }

    public void setPremiumDAO(PremiumDAO premiumDAO) {
        m_premiumDAO = premiumDAO;
    }

    private PolicyReportManager m_PolicyReportManager;
    private PremiumDAO m_premiumDAO;

}
