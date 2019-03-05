package dti.pm.policymgr.tailquotemgr.dao;

import dti.pm.core.dao.BaseDAO;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * This Class provides the implementation details of TailQuoteDAO Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 23, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class TailQuoteJdbcDAO extends BaseDAO implements TailQuoteDAO{

    /**
     * method to load all tail quote transactions
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return the recordset of tail quote transactions
     */
    public RecordSet loadAllTailQuoteTransaction(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTailQuoteTransaction", new Object[]{inputRecord});
        }
        RecordSet rs = null;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quote_Tail.Get_Quote_Tail_Transactions", mapping);
        try {
            rs = spDao.execute(inputRecord,recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load tail quote transaction information ", e);
            l.throwing(getClass().getName(), "loadAllTailQuoteTransaction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTailQuoteTransaction", rs);
        }
        return rs;
    }

    /**
     * method to load all tail quote
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return the recordset of tail quote transactions
     */
    public RecordSet loadAllTailQuote(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTailQuote", new Object[]{inputRecord});
        }
        RecordSet rs = null;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionLogId", "tailQuoteTransactionLogId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quote_Tail.Get_Quote_Tails", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load tail quote information ", e);
            l.throwing(getClass().getName(), "loadAllTailQuote", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTailQuote", rs);
        }
        return rs;
    }

    /**
     * save all tail quote transaction data
     *
     * @param inputRecords
     * @return processed records count
     */
    public int saveAllTailQuoteTransaction(RecordSet inputRecords) {
       Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllTailQuoteTransaction", new Object[]{inputRecords});
        }

        int processCount = 0;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("tlQuoteDate", "tailQuoteDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quote_Tail.Insert_Transaction",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save tail quote transaction information ", e);
            l.throwing(getClass().getName(), "saveAllTailQuoteTransaction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllTailQuoteTransaction", new Integer(processCount));
        }
        return processCount;
    }


    /**
     * save all tail quote data
     *
     * @param inputRecords
     * @return processed records count
     */
    public int saveAllTailQuote(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllTailQuote", new Object[]{inputRecords});
        }

        int processCount = 0;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("tailQuoteId", "pmTailQuoteId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quote_Tail.Update_Tail_Quote",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save quote tail information ", e);
            l.throwing(getClass().getName(), "saveAllTailQuote", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllTailQuote", new Integer(processCount));
        }
        return processCount;
    }
}
