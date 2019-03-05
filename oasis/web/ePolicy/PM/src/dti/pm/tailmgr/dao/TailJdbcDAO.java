package dti.pm.tailmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.RecordMode;
import dti.pm.core.dao.BaseDAO;
import dti.pm.tailmgr.TailFields;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the TailDAO interface. This is consumed by any business logic objects
 * that handles tail.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 21, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/30/10         gxc     Issue 111905/111953 Modified saveTailCharge to save the tail coverage base in pm_fm_charge
 *                          table instead of the main coverage's base record fk
 * 11/01/2010       syang       Issue 113780 - Added getCurrentRate() to retrieve current rate.
 * 04/27/2012       kmv     Issue 131649 - Pass Coverage Base Record Id instead of Coverage Id into FMN_GET_BA_CREDIT
 * 08/06/2012       xnie    Issue 135653 - Changed annual rate updatablility logic based on UC document
 *                          (1.2 UPDATE in UC View Tail Information).
 * 04/25/2014       xnie    Issue 153450 - Modified getParentEffecitveDate() to remove mapping for riskBaseRecordId
 *                                         which is unnecessary.
 * 07/22/2016       eyin    Issue 176557 - Modified updateAllTail() to add new field tailExtRemLimitB when save/change
 *                                         tail coverage.
 * ---------------------------------------------------
 */

public class TailJdbcDAO extends BaseDAO implements TailDAO {
    /**
     * load all tales
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return recordset include all tail parents
     */
    public RecordSet loadAllTail(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTail", new Object[]{inputRecord, recordLoadProcessor});
        }

        RecordSet rs;
        //Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("recordMode", "tailRecordMode"));
        // call PM_SEL_TAIL_PARENT procedure to load all tail coverage data
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_SEL_TAIL_PARENT", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load tails.", e);
            l.throwing(getClass().getName(), "loadAllTail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTail", rs);
        }
        return rs;
    }

    /**
     * load tail detail
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return record include infos about tail
     */
    public RecordSet loadAllTailDetail(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTailDetail", new Object[]{inputRecord, recordLoadProcessor});
        }

        //Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("recordMode", "tailRecordMode"));
        RecordSet rs;

        // call PM_SEL_TAIL_CHILD procedure to load all tail coverage data
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_SEL_TAIL_CHILD", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load tail detail.", e);
            l.throwing(getClass().getName(), "loadTailDetail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTailDetail", rs);
        }
        return rs;
    }

    /**
     * add manual tail coverage
     *
     * @param inputRecord
     * @return the updated record count
     */
    public int addManualTail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addManualTail", new Object[]{inputRecord});
        }


        int processCount = 0;
        //Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("mainCovgId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tailCode", "tailCoverageCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cvgRelType", "productCovRelTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("subCovgB", "subCoverageB"));

        // call Pm_Manual_Tail function to add all tails
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Manual_Tail", mapping);
        try {
            spDao.execute(inputRecord);
            processCount = 1;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to add manual tail.", e);
            l.throwing(getClass().getName(), "addManualTail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addManualTail", new Integer(processCount));
        }
        return processCount;
    }


    /**
     * update all tail
     *
     * @param inputRecords
     * @return the updated record count
     */
    public int updateAllTail(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAllTail", new Object[]{inputRecords});
        }

        int processCount;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        // We are saving covg or covg class , pk is coverage or coverage class pk
        mapping.addFieldMapping(new DataRecordFieldMapping("id", "coverageId"));

        //construct p_parms
        Iterator recIter = inputRecords.getRecords();
        while (recIter.hasNext()) {
            StringBuffer pParms = new StringBuffer();
            Record record = (Record) recIter.next();

            String type;
            if (record.hasField("policyCovComponentId")) {
                type = "COMPONENT";
            }
            else {
                type = StringUtils.isBlank(record.getStringValue("subCoverageDesc")) ? "COVERAGE" : "COVERAGE_CLASS";
            }

            String ratingModeCode = TailFields.getRatingModuleCode(record);
            // issue # 102481, set the value of "tailGrossPremium" to "annualBaseRate" for non-flat tail cancel
            String transCode = "";
            if (record.hasStringValue("transactionCode")) {
                transCode = record.getStringValue("transactionCode");
            }
            // Check if current tail coverage is a non-flat cancelled tail coverage
            boolean isNonFlatCancel = false;
            if ("CANCEL".equals(TailFields.getTailCurrPolRelStatTypeCd(record))) {
                Date effDate = DateUtils.parseDate(TailFields.getEffectiveFromDate(record));
                Date expDate = DateUtils.parseDate(TailFields.getEffectiveToDate(record));
                if (expDate.after(effDate)) {
                    isNonFlatCancel = true;
                }
            }

            if (("M".equals(ratingModeCode)|| isNonFlatCancel)
                && record.hasStringValue("tailGrossPremium")) {
                // If the rating module code is "M", set tailGrossPremium to annualBaseRate.
                TailFields.setAnnualBaseRate(record, record.getStringValue("tailGrossPremium"));
            }

            RecordMode recordModeCode = RecordMode.getInstance(TailFields.getTailRecordModeCode(record));
            if (recordModeCode.isOfficial()) {
                if (type.equals("COVERAGE")) {
                    pParms.append(addParm(record, "COVERAGE_PK", "coverageId"))
                        .append(addParm(record, "COVERAGE_BASE_RECORD_FK", "tailCovBaseRecordId"))
                        .append(addParm(record, "RISK_BASE_RECORD_FK", "riskBaseRecordId"))
                        .append(addParm(record, "PRODUCT_COVERAGE_CODE", "productCoverageCode"))
                        .append(addParm(record, "EFFECTIVE_FROM_DATE", "effectiveFromDate"))
                        .append(addParm(record, "EFFECTIVE_TO_DATE", "effectiveToDate"))
                        .append(addParm(record, "COVERAGE_LIMIT_CODE", "coverageLimitCode"))
                        .append(addParm(record, "EXT_TAIL_LIM_B", "tailExtRemLimitB"))
                        .append(addParm(record, "RETROACTIVE_DATE", "retroactiveDate"))
                        .append(addParm(record, "TRANSACTION_LOG_FK", "transactionLogId"))
                        .append(addParm(record, "ANNUAL_BASE_RATE", "annualBaseRate"));

                }
                else if (type.equals("COVERAGE_CLASS")) {
                    pParms.append(addParm(record, "COVERAGE_PK", "coverageId"))
                        .append(addParm(record, "COVERAGE_BASE_RECORD_FK", "tailCovBaseRecordId"))
                        .append(addParm(record, "PRODUCT_COVERAGE_CODE", "productCoverageCode"))
                        .append(addParm(record, "EFFECTIVE_FROM_DATE", "effectiveFromDate"))
                        .append(addParm(record, "EFFECTIVE_TO_DATE", "effectiveToDate"))
                        .append(addParm(record, "COVERAGE_LIMIT_CODE", "coverageLimitCode"))
                        .append(addParm(record, "EXT_TAIL_LIM_B", "tailExtRemLimitB"))
                        .append(addParm(record, "TRANSACTION_LOG_FK", "transactionLogId"))
                        .append(addParm(record, "ANNUAL_BASE_RATE", "annualBaseRate"));
                }
            }
            else if (recordModeCode.isTemp()) {
                if (type.equals("COVERAGE")) {
                    pParms.append(addParm("ROW_STATUS", "MODIFIED"))
                        .append(addParm(record, "COVERAGE_PK", "coverageId"))
                        .append(addParm(record, "COVERAGE_BASE_RECORD_FK", "tailCovBaseRecordId"))
                        .append(addParm(record, "RISK_BASE_RECORD_FK", "riskBaseRecordId"))
                        .append(addParm(record, "PRODUCT_COVERAGE_CODE", "productCoverageCode"))
                        .append(addParm(record, "EFFECTIVE_FROM_DATE", "effectiveFromDate"))
                        .append(addParm(record, "EFFECTIVE_TO_DATE", "effectiveToDate"))
                        .append(addParm(record, "CURR_POL_REL_STAT_TYPE_CD", "tailCurrPolRelStatTypeCd"))
                        .append(addParm(record, "COVERAGE_LIMIT_CODE", "coverageLimitCode"))
                        .append(addParm(record, "EXT_TAIL_LIM_B", "tailExtRemLimitB"))
                        .append(addParm(record, "RETROACTIVE_DATE", "retroactiveDate"))

                        .append(addParm(record, "TRANSACTION_LOG_FK", "transactionLogId"))
                        .append(addParm("RECORD_MODE_CODE", "TEMP"))
                        .append(addParm(record, "OFFICIAL_RECORD_FK", "officialRecordId"))
                        .append(addParm("AFTER_IMAGE_RECORD_B", "Y"))
                        .append(addParm(record, "ANNUAL_BASE_RATE", "annualBaseRate"));

                }
                else if (type.equals("COVERAGE_CLASS")) {
                    pParms.append(addParm("ROW_STATUS", "MODIFIED"))
                        .append(addParm(record, "COVERAGE_PK", "coverageId"))
                        .append(addParm(record, "COVERAGE_BASE_RECORD_FK", "tailCovBaseRecordId"))
                        .append(addParm(record, "PRODUCT_COVERAGE_CODE", "productCoverageCode"))
                        .append(addParm(record, "EFFECTIVE_FROM_DATE", "effectiveFromDate"))
                        .append(addParm(record, "EFFECTIVE_TO_DATE", "effectiveToDate"))
                        .append(addParm(record, "CURR_POL_REL_STAT_TYPE_CD", "tailCurrPolRelStatTypeCd"))
                        .append(addParm(record, "COVERAGE_LIMIT_CODE", "coverageLimitCode"))
                        .append(addParm(record, "EXT_TAIL_LIM_B", "tailExtRemLimitB"))
                        .append(addParm(record, "RETROACTIVE_DATE", "retroactiveDate"))
                        .append(addParm(record, "TRANSACTION_LOG_FK", "transactionLogId"))
                        .append(addParm("RECORD_MODE_CODE", "TEMP"))
                        .append(addParm(record, "OFFICIAL_RECORD_FK", "officialRecordId"))
                        .append(addParm("AFTER_IMAGE_RECORD_B", "Y"))
                        .append(addParm(record, "TERM_BASE_RECORD_FK", "termBaseRecordId"))
                        .append(addParm(record, "ANNUAL_BASE_RATE", "annualBaseRate"));
                }
            }

            record.setFieldValue("type", type);
            record.setFieldValue("parms", pParms.toString());
        }

        // call Pm_Save_Data.Main procedure to update all tail data
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Data.Main", mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update tail.", e);
            l.throwing(getClass().getName(), "updateAllTail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllTail", new Integer(processCount));
        }
        return processCount;
    }

    /**
     * call Pm_Validate_Tail.data to check the tail data
     *
     * @param inputRecord
     * @return boolean value to indicate if the tail data is valid
     */
    public String getTailDataValidateResult(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTailDataValidateResult", new Object[]{inputRecord});
        }

        String validateResult;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "lastTransactionId"));
        // call Pm_Validate_Tail.data procedure to check if the tail data and tail component data are valid
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Tail.data", mapping);
        try {
            validateResult = spDao.execute(inputRecord).getSummaryRecord().getStringValue("return");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate tail data.", e);
            l.throwing(getClass().getName(), "getTailDataValidateResult", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTailDataValidateResult", validateResult);
        }
        return validateResult;
    }

    /**
     * call PM_WEB_TAIL.GET_POLICY_TERM_HISTORY_FK to get policyTermHistoryId of the tail record
     *
     * @param inputRecord
     * @return string value of policyTermHistoryId
     */
    public String getTailHistoryId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTailHistoryId", new Object[]{inputRecord});
        }

        String historyId;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("tailBaseId", "tailCovBaseRecordId"));

        // call PM_WEB_TAIL.GET_POLICY_TERM_HISTORY_FK to get policyTermHistoryId of the tail record
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Tail.GET_POLICY_TERM_HISTORY_FK", mapping);
        try {
            historyId = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get TailHistoryId.", e);
            l.throwing(getClass().getName(), "getTailHistoryId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTailHistoryId", historyId);
        }
        return historyId;
    }


    /**
     * get the current ammount and amountNo info for the billing account related to the tail coverage
     *
     * @param inputRecord
     * @return record include ammount and accountNo infos
     */
    public Record getTailCredit(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTailCredit", new Object[]{inputRecord});
        }

        Record outputRecord;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageId", "tailCovBaseRecordId"));
        
        // call Fmn_get_ba_credit to get credit info of the tail record
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fmn_get_ba_credit", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get tail credit.", e);
            l.throwing(getClass().getName(), "getTailCredit", ae);
            //it doesn't throw excpetiong,but return a default data 0;
            outputRecord = new Record();
            outputRecord.setFieldValue("amount", "0");
            outputRecord.setFieldValue("accountNo", "");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTailCredit", outputRecord);
        }
        return outputRecord;
    }

    /**
     * validate tail coverage
     *
     * @param inputRecord
     * @return rcord include status, message, validateResult VALID/INVALID
     */
    public Record getTailProcessValidateResult(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTailProcessValidateResult", new Object[]{inputRecord});
        }

        Record outputRecord;
        // call Pm_Validate_Tail.web_process to validate tail
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Tail.web_process");
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get tail process validate result.", e);
            l.throwing(getClass().getName(), "getTailProcessValidateResult", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTailProcessValidateResult", outputRecord);
        }
        return outputRecord;
    }

    /**
     * get Tail Transaction Effective Date
     *
     * @param inputRecord
     * @return tail transaction effective date
     */
    public String getTailTransactionEffectiveDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTailTransactionEffectiveDate", new Object[]{inputRecord});
        }

        Record outputRecord;

        // call Pm_Validate_Tail.web_process to validate tail
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Tail.Web_Get_Tail_Trans_Eff");
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get tail transaction effective date.", e);
            l.throwing(getClass().getName(), "getTailTransactionEffectiveDate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTailTransactionEffectiveDate", outputRecord);
        }
        return outputRecord.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
    }

    /**
     * process tail changes
     *
     * @param inputRecord
     * @return rcord
     */
    public Record processTail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processTail", new Object[]{inputRecord});
        }

        Record outputRecord;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();

        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelType", "cancellationType"));

        // call Pm_Validate_Tail.web_process to validate tail
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Tail.Web_Process_Tail", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to process tail changes.", e);
            l.throwing(getClass().getName(), "processTail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processTail", outputRecord);
        }
        return outputRecord;
    }

    /**
     * validate tail delta
     *
     * @param inputRecord
     * @return record include error message and validate result
     */
    public Record getTailDeltaValidateResult(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTailDeltaValidateResult", new Object[]{inputRecord});
        }

        Record outputRecord;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();

        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));

        // call Pm_Validate_Tail_Delta to validate tail
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Tail_Delta", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get Tail Delta Validate Result.", e);
            l.throwing(getClass().getName(), "getTailDeltaValidateResult", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTailDeltaValidateResult", outputRecord);
        }
        return outputRecord;
    }


    /**
     * save tail charge
     *
     * @param inputRecord
     * @return record excecute result
     */
    public Record saveTailCharge(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveTailCharge", new Object[]{inputRecord});
        }

        Record outputRecord;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageId", "tailCovBaseRecordId"));

        // call Pm_Validate_Tail_Delta to validate tail
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_tail.Save_Finance_Charge", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save tail charge.", e);
            l.throwing(getClass().getName(), "saveTailCharge", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveTailCharge", outputRecord);
        }
        return outputRecord;
    }

    protected String addParm(Record inputRecord, String parmName, String mapFieldName) {
        return addParm(parmName, inputRecord.getStringValue(mapFieldName));
    }

    protected String addParm(String parmName, String parmValue) {
        StringBuffer parm = new StringBuffer(parmName);
        if (StringUtils.isBlank(parmValue)) {
            parmValue = "";
        }
        parm.append("^").append(parmValue).append("^");
        return parm.toString();
    }


    /**
     * load all available manual tails for adding new tail coverage
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return recordset of available tails for adding
     */
    public RecordSet loadAllManualTail(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllManualTail", new Object[]{inputRecord});
        }
        RecordSet rs;

        //set constant values
        inputRecord.setFieldValue("expDate", "01/01/3000");

        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("prodCovgParentId", "productCoverageCode"));

        // call Pm_Web_Tail.Sel_Manual_Tail_Covg procedure to load all available manual tail coverage data for adding
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Tail.Sel_Manual_Tail_Covg", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load available manual tails.", e);
            l.throwing(getClass().getName(), "loadAllManualTail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllManualTail", rs);
        }

        return rs;
    }

    /**
     * get tail parent coverage's effective date
     *
     * @param inputRecord
     * @return parent effective date
     */
    public String getParentEffecitveDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getParentEffecitveDate", new Object[]{inputRecord});
        }

        String effDate;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("parentCovgId", "coverageBaseRecordId"));

        inputRecord.setFieldValue("expDate", "01/01/3000");

        // call Pm_Web_Tail.Get_Parent_Effective_Date to get tail parent coverage's effective date
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Tail.Get_Parent_Effective_Date", mapping);
        try {
            effDate = DateUtils.formatDate(
                spDao.execute(inputRecord).getSummaryRecord().getDateValue(StoredProcedureDAO.RETURN_VALUE_FIELD));
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get parent effective date.", e);
            l.throwing(getClass().getName(), "getParentEffecitveDate", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getParentEffecitveDate", effDate);
        }
        return effDate;
    }


    /**
     * get exist tail coverage count
     *
     * @param inputRecord
     * @return count of exist tail coverages
     */
    public int getTailCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTailCount", new Object[]{inputRecord});
        }

        int tailCount;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tailCovgCode", "tailCoverageCode"));
        // call Pm_Web_Tail.Get_Tail_Coverage_Count to get tail coverage count
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Tail.Get_Tail_Coverage_Count", mapping);
        try {
            tailCount = spDao.execute(inputRecord).getSummaryRecord()
                .getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get tail coverage count.", e);
            l.throwing(getClass().getName(), "getTailCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTailCount", String.valueOf(tailCount));
        }
        return tailCount;

    }

    /**
     * Get current rate
     *
     * @param inputRecord
     * @return current rate
     */
    public float getCurrentRate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCurrentRate", new Object[]{inputRecord});
        }

        float currentRate = 0f;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "lastTransactionId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Tail.Get_Current_Rate", mapping);
        try {
            currentRate = spDao.execute(inputRecord).getSummaryRecord().getFloatValue(StoredProcedureDAO.RETURN_VALUE_FIELD).floatValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get current rate.", e);
            l.throwing(getClass().getName(), "getCurrentRate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCurrentRate", String.valueOf(currentRate));
        }
        return currentRate;
    }
}
