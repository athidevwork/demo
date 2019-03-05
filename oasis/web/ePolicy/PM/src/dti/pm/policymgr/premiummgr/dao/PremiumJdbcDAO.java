package dti.pm.policymgr.premiummgr.dao;

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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC dao for premium
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 18, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/01/2008      sxm          Use executeReadonly() in loadAllPayment() to deal with
 *                              Oracle Global Temporary table that delets data on commit.
 * 03/26/2008       yyh         loadAllPayment added for issue 78337
 * 08/01/2011       ryzhao      118806 - Added getLatestTaxTransaction(),
 *                                             getLatestFeeSurchargeTransaction(),
 *                                             getLatestAllTransaction().
 *                                       Modified loadAllFund() to change Sel_Fund_Info to Sel_Non_Prem_Info.
 * 09/27/2011       ryzhao      125523 - Change Get_Latest_Fee_Surchg_Trans to Get_Latest_Fee_Surcharge_Trans.
 * ---------------------------------------------------
 */

public class PremiumJdbcDAO extends BaseDAO implements PremiumDAO {

    /**
     * Retrieves all premium records.
     *
     * @param record              input record
     * @param recordLoadProcessor an instance of the load processor
     * @return recordSet
     */
    public RecordSet loadAllPremium(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPremium", new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_POLICY.Sel_Premium_Info");
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllPremium", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load premium information", e);
            l.throwing(getClass().getName(), "loadAllPremium", ae);
            throw ae;
        }
    }

    /**
     * Get latest premium based transaction of policy
     *
     * @param inputRecord
     * @return transactionId
     */
    public long getLatestPremiumTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getLatestTransaction", new Object[]{inputRecord});

        long returnValue;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Get_Latest_Premium_Transaction");
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getLongValue(StoredProcedureDAO.RETURN_VALUE_FIELD).longValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Pm_Web_Policy.Get_Latest_Premium_Transaction", e);
            l.throwing(getClass().getName(), "getLatestTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getLatestTransaction", new Long(returnValue));

        return returnValue;
    }

    /**
     * get latest tax bearing transaction of policy
     *
     * @param inputRecord Record containing current policyId and term history record information
     * @return transactionId
     */
    public long getLatestTaxTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getLatestTaxTransaction", new Object[]{inputRecord});

        long returnValue;
        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Get_Latest_Tax_Transaction");
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getLongValue(StoredProcedureDAO.RETURN_VALUE_FIELD).longValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get latest tax transaction", e);
            l.throwing(getClass().getName(), "getLatestTaxTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getLatestTaxTransaction", new Long(returnValue));
        return returnValue;
    }

    /**
     * get latest fee/surcharge bearing transaction of policy
     *
     * @param inputRecord Record containing current policyId and term history record information
     * @return transactionId
     */
    public long getLatestFeeSurchargeTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getLatestFeeSurchargeTransaction", new Object[]{inputRecord});

        long returnValue;
        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Get_Latest_Fee_Surcharge_Trans");
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getLongValue(StoredProcedureDAO.RETURN_VALUE_FIELD).longValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get latest fee/surcharge transaction", e);
            l.throwing(getClass().getName(), "getLatestFeeSurchargeTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getLatestFeeSurchargeTransaction", new Long(returnValue));
        return returnValue;
    }

    /**
     * get latest fund/tax/fee/surcharge bearing transaction of policy
     *
     * @param inputRecord Record containing current policyId and term history record information
     * @return transactionId
     */
    public long getLatestAllTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getLatestAllTransaction", new Object[]{inputRecord});

        long returnValue;
        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Get_Latest_All_Transaction");
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            // Pull the stored procedure output information
            returnValue = outputRecordSet.getSummaryRecord().getLongValue(StoredProcedureDAO.RETURN_VALUE_FIELD).longValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get latest fund/tax/fee/surcharge transaction", e);
            l.throwing(getClass().getName(), "getLatestAllTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getLatestAllTransaction", new Long(returnValue));
        return returnValue;
    }

    /**
     * Retrieves all rating log info by transactionId, showDetailFlag
     *
     * @param record              input record
     * @param recordLoadProcessor recordLoadProcessor
     * @return recordSet
     */
    public RecordSet loadAllRatingLog(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRatingLog", record);
        }

        RecordSet rs;
        try {

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Sel_Rating_Log_Info");
            rs = spDao.execute(record, recordLoadProcessor);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllRatingLog", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load rating log information", e);
            l.throwing(getClass().getName(), "loadAllRatingLog", ae);
            throw ae;
        }
    }

    /**
     * judge if rating log exist
     *
     * @param inputRecord input record, contains current policyId
     * @return recordSet
     */
    public boolean isRatingLogExist(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRatingLogExist", inputRecord);
        }

        RecordSet rs;
        try {

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_TRANSACTION.Is_Rating_Log_Exist");
            rs = spDao.execute(inputRecord);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRatingLogExist", rs);
            }
            return rs.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load isRatingLogExist", e);
            l.throwing(getClass().getName(), "isRatingLogExist", ae);
            throw ae;
        }
    }

    /**
     * Retrieves all member contribution info
     *
     * @param record              input record
     * @param recordLoadProcessor recordLoadProcessor
     * @return recordSet
     */
    public RecordSet loadAllMemberContribution(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMemberContribution", record);
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Sel_Member_Contribution_Info", mapping);
            RecordSet rs1 = spDao.execute(record);
            rs = spDao.execute(record, recordLoadProcessor);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllMemberContribution", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load member contribution", e);
            l.throwing(getClass().getName(), "loadAllMemberContribution", ae);
            throw ae;
        }
    }

    /**
     * Retrieves all layer detail info
     *
     * @param record              input record
     * @param recordLoadProcessor recordLoadProcessor
     * @return recordSet
     */
    public RecordSet loadAllLayerDetail(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllLayerDetail", record);
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Sel_Layer_Detail_Info", mapping);
            rs = spDao.execute(record, recordLoadProcessor);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllLayerDetail", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load layer detail", e);
            l.throwing(getClass().getName(), "loadAllLayerDetail", ae);
            throw ae;
        }
    }

    /**
     * Retrieves all fund records.
     *
     * @param record              input record
     * @param recordLoadProcessor an instance of the load processor
     * @return recordSet
     */
    public RecordSet loadAllFund(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllFund", new Object[]{record, recordLoadProcessor});
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_POLICY.Sel_Non_Prem_Info");
            rs = spDao.execute(record, recordLoadProcessor);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllFund", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load fund information", e);
            l.throwing(getClass().getName(), "loadAllFund", ae);
            throw ae;
        }
    }

    /**
    * Retrieves all payment information.
    *
    * @param record              input record
    * @return recordSet
    */
    public RecordSet loadAllPayment(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPayment", new Object[]{record});
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEffective", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("accountingDate", "transAccountingDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("quoteId", "endorsementQuoteId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_POLICY.Sel_Payment_Info",mapping);

            rs = spDao.executeReadonly(record);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllPayment", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load payment information", e);
            l.throwing(getClass().getName(), "loadAllPayment", ae);
            throw ae;
        }
    }

    /**
     * Retrieves all premium accounting data
     *
     * @param inputRecord input Record
     * @return RecordSet
     */
    public RecordSet loadAllPremiumAccounting(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPremiumAccounting", inputRecord);
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Premium_Accounting");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load premium accounting data.", e);
            l.throwing(getClass().getName(), "loadAllPremiumAccounting", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPremiumAccounting", rs);
        }
        return rs;
    }

    /**
     * Generate the premium accounting data for selected transaction
     *
     * @param inputRecord  input Record
     * @return Record
     */
    public Record generatePremiumAccounting(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePremiumAccounting", inputRecord);
        }
        Record output;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Rp_Premacct_Data");
            output = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load premium accounting data.", e);
            l.throwing(getClass().getName(), "generatePremiumAccounting", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generatePremiumAccounting", output);
        }
        return output;
    }
}
