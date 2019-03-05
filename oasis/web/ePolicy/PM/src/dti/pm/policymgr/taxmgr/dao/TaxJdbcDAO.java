package dti.pm.policymgr.taxmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.taxmgr.TaxFields;
import dti.pm.transactionmgr.TransactionFields;

import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * JDBC dao for tax
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 21, 2007
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/13/2014       wdang       158112 - Add loadAllTaxHeader(), insertAllTaxHeader(), updateAllTaxHeader(), 
 *                              deleteAllTaxHeader(), getTermAlgorithm(), getManualExpiration() for Maintain Tax page.
 * 12/14/2014       wdang       159491 - Modified loadAllRisk() to add input parameter transactionLogId.
 * 01/30/2015       fcb         160508 - added additional validation function for taxes.
 * ---------------------------------------------------
 */
public class TaxJdbcDAO extends BaseDAO implements TaxDAO {

    /**
     * Retrieves all tax records.
     *
     * @param record              input records
     * @return recordSet
     */
    public RecordSet loadAllTax(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTax", new Object[]{record});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Tax_Info");
            rs = spDao.execute(record);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllTax", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load tax information", e);
            l.throwing(getClass().getName(), "loadAllTax", ae);
            throw ae;
        }
    }

    /**
     * Get latest tax based transaction of the policy
     *
     * @param  inputRecord      input record
     * @return transactionId
     */
    public String getLatestTaxTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getLatestTaxTransaction", new Object[]{inputRecord});

        String returnValue;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Get_Latest_Tax_Transaction");
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Pm_Web_Transaction.Get_Latest_Tax_Transaction", e);
            l.throwing(getClass().getName(), "getLatestTaxTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getLatestTaxTransaction", new Long(returnValue));

        return returnValue;
    }
    
    /**
     * Retrieve all risk information for Maintain Tax page.
     *
     * @param inputRecord input record
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllRisk(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRisk", new Object[]{inputRecord});

        RecordSet rs;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.TERM_ID, PolicyHeaderFields.TERM_BASE_RECORD_ID));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.TERM_EFF, PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.TERM_EXP, PolicyHeaderFields.TERM_EFFECTIVE_TO_DATE));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.REC_MODE, TaxFields.RECORD_MODE_CODE));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.EQT_ID, TransactionFields.ENDORSEMENT_QUOTE_ID));
            mapping.addFieldMapping(new DataRecordFieldMapping(TransactionFields.TRANSACTION_LOG_ID, TransactionFields.LAST_TRANSACTION_ID));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Sel_Risk_Coverage", mapping);
            rs = spDao.execute(inputRecord, loadProcessor);

            l.exiting(getClass().getName(), "loadAllRisk", rs);
            return rs;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risk information", e);
            l.throwing(getClass().getName(), "loadAllRisk", ae);
            throw ae;
        }
    }

    /**
     * Retrieve all tax definition for Maintain Tax page.
     * @param inputRecord input record
     * @param loadProcessor
     * @return
     */
    public RecordSet loadAllTaxHeader(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllTaxHeader", new Object[]{inputRecord});

        RecordSet rs;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.EFF_DATE, PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.EXP_DATE, PolicyHeaderFields.TERM_EFFECTIVE_TO_DATE));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.END_QUOTE_ID, TransactionFields.ENDORSEMENT_QUOTE_ID));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Select_Tax", mapping);
            rs = spDao.execute(inputRecord, loadProcessor);

            l.exiting(getClass().getName(), "loadAllTaxHeader", rs);
            return rs;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load tax header", e);
            l.throwing(getClass().getName(), "loadAllTaxHeader", ae);
            throw ae;
        }
    }

    /**
     * Insert all tax definitions for Maintain Tax page.
     * @param inputRecords input records
     */
    public void insertAllTaxHeader(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "insertAllTaxHeader", new Object[]{inputRecords});

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping(TransactionFields.TRANSACTION_LOG_ID, TransactionFields.LAST_TRANSACTION_ID));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.EFF_DATE, TaxFields.EFFECTIVE_FROM_DATE));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.EXP_DATE, TaxFields.EFFECTIVE_TO_DATE));
            
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Tax_Maint.Insert_Tax", mapping);
            spDao.executeBatch(inputRecords);
            
            l.exiting(getClass().getName(), "insertAllTaxHeader");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save tax header", e);
            l.throwing(getClass().getName(), "insertAllTaxHeader", ae);
            throw ae;
        }
    }
    
    /**
     * Update all tax definitions for Maintain Tax page.
     *
     * @param inputRecords input records
     */
    public void updateAllTaxHeader(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllTaxHeader", new Object[]{inputRecords});

        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping(TransactionFields.TRANSACTION_LOG_ID, TransactionFields.LAST_TRANSACTION_ID));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.EFF_DATE, TaxFields.EFFECTIVE_FROM_DATE));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.EXP_DATE, TaxFields.EFFECTIVE_TO_DATE));
            
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Tax_Maint.Update_Tax", mapping);
            spDao.executeBatch(inputRecords);
            
            l.exiting(getClass().getName(), "updateAllTaxHeader");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to change tax header", e);
            l.throwing(getClass().getName(), "updateAllTaxHeader", ae);
            throw ae;
        }
    }
    
    /**
     * Delete all tax definitions for Maintain Tax page.
     *
     * @param inputRecords input records
     */
    public void deleteAllTaxHeader(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllTaxHeader", new Object[]{inputRecords});

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Tax_Maint.Delete_Tax");
            spDao.executeBatch(inputRecords);
            
            l.exiting(getClass().getName(), "deleteAllTaxHeader");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to change tax header", e);
            l.throwing(getClass().getName(), "deleteAllTaxHeader", ae);
            throw ae;
        }
    }
    
    /**
     * Get term algorithm by the given term effective date.
     *
     * @param inputRecord input Record
     * @return algorithm
     */
    public String getTermAlgorithm(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getTermAlgorithm", new Object[]{inputRecord});

        String algorithm;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.EFFECTIVE_DATE, PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE));
            
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Tax_Calc.Get_Term_Alg", mapping);
            algorithm = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
            
            l.exiting(getClass().getName(), "getTermAlgorithm", algorithm);
            return algorithm;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risk information", e);
            l.throwing(getClass().getName(), "getTermAlgorithm", ae);
            throw ae;
        }
    }
    
    /**
     * Get the maximum expiration date of manual tax by specific term.
     *
     * @param inputRecord input Record
     * @return expiration date
     */
    public String getManualExpiration(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getManualExpiration", new Object[]{inputRecord});

        String expirationDate;
        try {
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.TERM_ID, PolicyHeaderFields.POLICY_TERM_HISTORY_ID));
            
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Tax_Maint.Get_Manual_Expiration", mapping);
            expirationDate = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
            
            l.exiting(getClass().getName(), "getManualExpiration", expirationDate);
            return expirationDate;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risk information", e);
            l.throwing(getClass().getName(), "getManualExpiration", ae);
            throw ae;
        }
    }

    /**
     * Get the maximum expiration date of manual tax by specific term.
     *
     * @param inputRecord input Record
     * @return validation message
     */
    public String validateTaxRates(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateTaxRates", new Object[]{inputRecord});

        String returnMessage;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping(TransactionFields.TRANSACTION_LOG_ID, TransactionFields.LAST_TRANSACTION_ID));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.EFF_DATE, TaxFields.EFFECTIVE_FROM_DATE));
            mapping.addFieldMapping(new DataRecordFieldMapping(TaxFields.EXP_DATE, TaxFields.EFFECTIVE_TO_DATE));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Tax_Maint.Validate_Tax_Rates", mapping);
            Record output = spDao.executeUpdate(inputRecord);
            returnMessage = output.getStringValue("retMsg");

            l.exiting(getClass().getName(), "validateTaxRates", returnMessage);
            return returnMessage;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check tax rates.", e);
            l.throwing(getClass().getName(), "validateTaxRates", ae);
            throw ae;
        }
    }
}