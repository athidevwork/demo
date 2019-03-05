package dti.pm.policymgr.dividendmgr.impl;

import dti.ci.core.struts.AddRowNoLoadProcessor;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.util.StringUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.dividendmgr.DividendFields;
import dti.pm.policymgr.dividendmgr.DividendManager;
import dti.pm.policymgr.dividendmgr.dao.DividendDAO;
import dti.pm.validationmgr.dao.ValidationDAO;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of DividendManager Interface.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/13/2012       wfu         128705 - Added related methods to handle new dividend process.
 * 12/26/2013       awu         148187 - Added loadAllTransferRisk, transferDividend, loadAllDividendAudit.
 * ---------------------------------------------------
 */


public class DividendManagerImpl implements DividendManager {

    /**
     * Returns a RecordSet loaded with list of dividend rules
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of available dividend.
     */
    public RecordSet loadAllDividendRule(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendRule", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        if (inputRecord.hasStringValue(DividendFields.POLICY_TYPE)) {
            DividendFields.setPolicyTypeCode(inputRecord, DividendFields.getPolicyType(inputRecord));
        }

        RecordLoadProcessor load = new MaintainDividendRuleRecordLoadProcessor();
        rs = getDividendDAO().loadAllDividendRule(inputRecord, load);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendRule", rs);
        }

        return rs;
    }

    /**
     * Save selected dividend rule info
     *
     * @param inputRecords dividend rule info
     * @return
     */
    public void saveAllDividendRule(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllDividendRule", new Object[]{inputRecords});
        }

        // Get the changed records to be validated
        RecordSet changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);

        if (changedRecords.getSize() > 0) {
            try {
                getDividendDAO().saveAllDividendRule(changedRecords);
            }
            catch (Exception e) {
                throw new AppException("pm.dividend.maintain.save.error", "Failed to save the dividend rules.");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllDividendRule");
        }
    }

    /**
     * validate the input data for calculate.
     *
     * @param inputRecord
     * @returm
     */
    public void validateCalculateDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCalculateDividend", new Object[]{inputRecord});
        }

        YesNoFlag isAccountingDateValid = getValidationDAO().checkAccountingMonth(inputRecord);
        if (!isAccountingDateValid.booleanValue()) {
            throw new AppException("pm.dividend.calculate.validation.accountingDate.error",
                "Validate dividend data to calculate fail.");
        }

        String percentage = DividendFields.getPercentage(inputRecord);
        if (StringUtils.isBlank(percentage) || Double.parseDouble(percentage) == 0) {
            throw new AppException("pm.dividend.calculate.validation.noPercentage.error",
                "Validate dividend data to calculate fail.");
        }

        if (!StringUtils.isBlank(percentage) && Double.parseDouble(percentage) > 100) {
            throw new AppException("pm.dividend.calculate.validation.percentage.error",
                "Validate dividend data to calculate fail.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCalculateDividend");
        }
    }

    /**
     * Calculate input dividend info
     *
     * @param inputRecord dividend info
     * @return
     */
    public void calculateDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "calculateDividend", new Object[]{inputRecord});
        }

        //Set the value of policyId as 0 to calculate this dividend.
        DividendFields.setPolicyId(inputRecord, "0");
        String cancelB = DividendFields.getCancelB(inputRecord);
        //Set hidden Cancel field as value N
        cancelB = StringUtils.isBlank(cancelB) ? "N" : cancelB;
        DividendFields.setCancelB(inputRecord, cancelB);

        Record record = getDividendDAO().calculateDividend(inputRecord);

        String retMsg = DividendFields.getRetMsg(record);
        Long retId = DividendFields.getRetId(record);
        Long retCode = DividendFields.getRetCode(record);
        if (retCode.longValue() < 0 || retId.longValue() < 1) {
            throw new AppException("pm.dividend.calculate.failed.error",
                "Failed to calculate dividend.", new String[]{retMsg});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "calculateDividend");
        }
    }

    /**
     * Returns a RecordSet loaded with list of prior dividends
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of prior dividends.
     */
    public RecordSet loadAllPriorDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorDividend", new Object[]{inputRecord});
        }

        RecordSet rs = getDividendDAO().loadAllPriorDividend(inputRecord);
        DividendFields.setIsPostAvailable(inputRecord, YesNoFlag.N);
        DividendFields.setIsPrintAvailable(inputRecord, YesNoFlag.N);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPriorDividend", rs);
        }

        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of calculated dividend
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of available calculated dividend.
     */
    public RecordSet loadAllCalculatedDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCalculatedDividend", new Object[]{inputRecord});
        }

        RecordLoadProcessor load = new CalculatedDividendRecordLoadProcessor(inputRecord);
        RecordLoadProcessor loadProcessor = AddSelectIndLoadProcessor.getInstance();
        load = RecordLoadProcessorChainManager.getRecordLoadProcessor(load, loadProcessor);
        RecordSet rs = getDividendDAO().loadAllCalculatedDividend(inputRecord, load);

        // No data found message handled here
        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.dividend.process.calculated.noDataFound.error");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCalculatedDividend", rs);
        }

        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of dividend report summary
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of available dividend report summary.
     */
    public RecordSet loadAllDividendReportSummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendReportSummary", new Object[]{inputRecord});
        }

        RecordSet rs = getDividendDAO().loadAllDividendReportSummary(inputRecord);

        // No data found message handled here
        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.dividend.report.noDataFound.error");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendReportSummary", rs);
        }

        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of dividend report detail
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of available dividend report detail.
     */
    public RecordSet loadAllDividendReportDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendReportDetail", new Object[]{inputRecord});
        }

        RecordSet rs = getDividendDAO().loadAllDividendReportDetail(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendReportDetail", rs);
        }

        return rs;
    }

    /**
     * Method that gets the default values for adding dividend rule
     *
     * @param inputRecord
     * @return Record that contains default values
     */
    public Record getInitialValuesForAddDividendRule(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddDividendRule", new Object[]{inputRecord});
        }

        Record outputRecord = new Record();

        // set request context as initial values
        DividendFields.setDividendRuleId(outputRecord, getDbUtilityManager().getNextSequenceNo().toString());
        DividendFields.setEffectiveToDate(outputRecord, "01/01/3000");
        DividendFields.setPolicyTypeCode(outputRecord, DividendFields.getPolicyTypeCode(inputRecord));
        DividendFields.setAccountingDate(outputRecord, DateUtils.formatDate(new Date()));
        DividendFields.setPercentage(outputRecord, "0");
        DividendFields.setIsDividendEditable(outputRecord, YesNoFlag.Y);
        DividendFields.setIsDeleteAvailable(outputRecord, YesNoFlag.Y);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddDividendRule", outputRecord);
        }

        return outputRecord;
    }

    /**
     * Post Dividend
     *
     * @param inputRecord
     * @return
     */
    public void postDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postDividend", new Object[]{inputRecord});
        }

        // Process distribution
        Record record = getDividendDAO().postDividend(inputRecord);

        String retMsg = DividendFields.getRetMsg(record);
        Long retCode = DividendFields.getRetCode(record);
        if (retCode.longValue() != 0) {
            throw new AppException("pm.dividend.calculate.failed.error",
                "Failed to post the dividend.", new String[]{retMsg});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postDividend");
        }

    }

    /**
     * Returns a RecordSet loaded with list of dividend declaration
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of available dividend.
     */
    public RecordSet loadAllDividendDeclare(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendDeclare", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        Record record = new Record();
        if (inputRecord.hasStringValue(DividendFields.START_DATE)) {
            DividendFields.setStartDate(record, DividendFields.getStartDate(inputRecord));
        }
        if (inputRecord.hasStringValue(DividendFields.END_DATE)) {
            DividendFields.setEndDate(record, DividendFields.getEndDate(inputRecord));
        }

        RecordLoadProcessor load = new MaintainDividendDeclareEntitlementRecordLoadProcessor();
        RecordLoadProcessor loadProcessor = AddSelectIndLoadProcessor.getInstance();
        load = RecordLoadProcessorChainManager.getRecordLoadProcessor(load, loadProcessor);
        rs = getDividendDAO().loadAllDividendDeclare(record, load);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendDeclare", rs);
        }

        return rs;
    }

    /**
     * Save selected dividend declaration
     *
     * @param inputRecords dividend declaration info
     * @return
     */
    public void saveAllDividendDeclare(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllDividendDeclare", new Object[]{inputRecords});
        }

        validateDividendDeclare(inputRecords);

        // Get the changed records to be validated
        RecordSet changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);
        getDividendDAO().saveAllDividendDeclare(changedRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllDividendDeclare");
        }
    }

    /**
     * validate the input data for dividend declarations.
     *
     * @param inputRecords
     * @returm
     */
    protected void validateDividendDeclare(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateDividendDeclare", new Object[]{inputRecords});
        }

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.UPDATED, UpdateIndicator.INSERTED}));

        Iterator it = changedRecords.getRecords();
        Record record = null;
        String percentage = null;
        String divAmount = null;
        while (it.hasNext()) {
            record = (Record) it.next();
            percentage = DividendFields.getPercentage(record);
            divAmount = DividendFields.getDividendAmount(record);
            if (StringUtils.isBlank(percentage) && StringUtils.isBlank(divAmount)) {
                MessageManager.getInstance().addErrorMessage("pm.dividend.maintain.validation.noData.error");
            }
            else if (!StringUtils.isBlank(percentage) && !StringUtils.isBlank(divAmount)) {
                MessageManager.getInstance().addErrorMessage("pm.dividend.maintain.validation.bothData.error");
            }
            else if (!StringUtils.isBlank(percentage)) {
                if (Double.parseDouble(percentage) <= 0 || Double.parseDouble(percentage) >= 100) {
                    MessageManager.getInstance().addErrorMessage("pm.dividend.maintain.validation.invalidPct.error");
                }
            }
            else {
                if (Double.parseDouble(divAmount) <= 0) {
                    MessageManager.getInstance().addErrorMessage("pm.dividend.maintain.validation.invalidAmount.error");
                }
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid dividend declaration data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateDividendDeclare");
        }
    }

    /**
     * Method that gets the default values for adding dividend declaration
     *
     * @param inputRecord
     * @return Record that contains default values
     */
    public Record getInitialValuesForAddDividendDeclare(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddDividendDeclare", new Object[]{inputRecord});
        }

        Record outputRecord = new Record();

        // set request context as initial values
        DividendFields.setDividendRuleId(outputRecord, getDbUtilityManager().getNextSequenceNo().toString());
        DividendFields.setIsDividendEditable(outputRecord, YesNoFlag.Y);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddDividendDeclare", outputRecord);
        }

        return outputRecord;
    }

    /**
     * Returns a RecordSet loaded with list of dividends
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of available dividend.
     */
    public RecordSet loadAllDividendForPreview(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendForPreview", new Object[]{inputRecord});
        }

        RecordSet rs = new RecordSet();
        RecordSet temp = null;
        Record record = new Record();
        if (inputRecord.hasStringValue(DividendFields.DIVIDEND_RULE_ID)) {
            String[] divIds = DividendFields.getDividendRuleId(inputRecord).split(",");
            for (int i = 0; i < divIds.length; i++) {
                DividendFields.setDividendRuleId(record, divIds[i]);
                temp = getDividendDAO().loadAllDividendForPreview(record);
                rs.addRecords(temp);
            }
            rs.addFieldNameCollection(temp.getFieldNames());
            rs.addFieldTypeMap(temp.getFieldTypesMap());
            temp = null;
        }

        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.dividend.process.calculated.noDataFound.error");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendForPreview", rs);
        }

        return rs;
    }

    /**
     * Process Dividend
     *
     * @param inputRecord
     * @return
     */
    public void performProcessDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processDividend", new Object[]{inputRecord});
        }

        try {
            // Process dividend
            getDividendDAO().performProcessDividend(inputRecord);
        }
        catch (Exception e) {
            throw new AppException("pm.dividend.calculate.failed.error",
                "Failed to process the dividend.", new String[]{"Failed to process the dividend."});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processDividend");
        }

    }

    /**
     * Load all of the processed dividend
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of processed dividend.
     */
    public RecordSet loadAllProcessedDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcessedDividend", new Object[]{inputRecord});
        }

        RecordLoadProcessor load = new MaintainDividendEntitlementRecordLoadProcessor();
        RecordLoadProcessor loadProcessor = AddSelectIndLoadProcessor.getInstance();
        load = RecordLoadProcessorChainManager.getRecordLoadProcessor(load, loadProcessor);
        RecordSet rs = getDividendDAO().loadAllProcessedDividend(inputRecord, load);
        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.dividend.process.calculated.noDataFound.error");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcessedDividend", rs);
        }

        return rs;
    }

    /**
     * Post Dividend
     *
     * @param inputRecord
     * @return
     */
    public void performPostDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performPostDividend", new Object[]{inputRecord});
        }

        // Post dividend
        Record record = getDividendDAO().performPostDividend(inputRecord);
        String retMsg = DividendFields.getRetMsg(record);
        Long retCode = DividendFields.getRetCode(record);
        if (retCode.longValue() != 0) {
            throw new AppException("pm.dividend.calculate.failed.error",
                "Failed to post the dividend.", new String[]{retMsg});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performPostDividend");
        }

    }

    /**
     * Load out the available risks which can do dividend transfer.
     *
     * @param policyHeader
     * @return
     */
    public RecordSet loadAllTransferRisk(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransferRisk", new Object[]{policyHeader});
        }

        Record input = new Record();
        DividendFields.setTransactionId(input, policyHeader.getCurTransactionId());

        RecordLoadProcessor loadProcessor = AddSelectIndLoadProcessor.getInstance();
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, new AddRowNoLoadProcessor());
        RecordSet rs = getDividendDAO().loadAllTransferRisk(input, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransferRisk", rs);
        }
        return rs;
    }

    /**
     * process dividend transfer.
     *
     * @param policyHeader
     * @param record
     * @return
     */
    public void transferDividend(PolicyHeader policyHeader, Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "transferDividend", new Object[]{policyHeader, record});
        }

        Record inputRecord = new Record();

        DividendFields.setTransactionId(inputRecord, policyHeader.getCurTransactionId());
        DividendFields.setTransferDividends(inputRecord, DividendFields.getTransferDividends(record));
        Record output = getDividendDAO().transferDividend(inputRecord);

        if (output == null || output.getIntegerValue("retCode") == -1) {
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            wa.setWorkflowTransitionParameter(PolicyHeaderFields.getPolicyNo(record), "INVALID");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "transferDividend");
        }
    }

    /**
     * Used to load all the dividend for page dividendAudit.jsp
     *
     * @param policyHeader
     * @param record
     * @return
     */
    public RecordSet loadAllDividendAudit(PolicyHeader policyHeader, Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadDividendAudit", new Object[]{policyHeader, record});
        }

        Record inputRecord = new Record();
        DividendFields.setPolicyId(inputRecord, policyHeader.getPolicyId());
        String showTermOrAll = "";
        if (record.hasStringValue(DividendFields.SHOW_ALL_OR_SHOW_TERM)) {
            showTermOrAll = DividendFields.getShowAllOrShowTerm(record);
        }
        if (DividendFields.DividendCodeValues.TERM.equals(showTermOrAll)) {
            DividendFields.setTermId(inputRecord, policyHeader.getTermBaseRecordId());
        }
        if (record.hasStringValue(DividendFields.TRANSACTION_ID)) {
            DividendFields.setTransactionId(inputRecord, DividendFields.getTransactionId(record));
        }

        RecordLoadProcessor loadProcessor = new AddRowNoLoadProcessor();
        RecordSet recordSet = getDividendDAO().loadAllDividendAudit(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadDividendAudit", recordSet);
        }
        return recordSet;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * Verify dividendDAO, validationDAO and dbUtilityManager in spring config
     */
    public void verifyConfig() {
        if (getDividendDAO() == null)
            throw new ConfigurationException("The required property 'dividendDAO' is missing.");
        if (getValidationDAO() == null)
            throw new ConfigurationException("The required property 'validationDAO' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    public DividendDAO getDividendDAO() {
        return m_dividendDAO;
    }

    public void setDividendDAO(DividendDAO dividendDAO) {
        m_dividendDAO = dividendDAO;
    }

    public ValidationDAO getValidationDAO() {
        return m_validationDAO;
    }

    public void setValidationDAO(ValidationDAO validationDAO) {
        m_validationDAO = validationDAO;
    }

    private DividendDAO m_dividendDAO;
    private ValidationDAO m_validationDAO;
    private DBUtilityManager m_dbUtilityManager;
}
