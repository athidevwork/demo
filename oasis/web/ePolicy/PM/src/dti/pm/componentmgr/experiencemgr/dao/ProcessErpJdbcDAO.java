package dti.pm.componentmgr.experiencemgr.dao;

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
 * This class implements the ProcessErpDAO interface. This is consumed by any business logic objects
 * that require information about Experience Rating Programs.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 1, 2011
 *
 * @author ryzhao
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ProcessErpJdbcDAO extends BaseDAO implements ProcessErpDAO {

    /**
     * Load all ERP data
     *
     * @param inputRecord         with user entered search criteria
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet
     */
    public RecordSet loadAllErp(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllErp", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEff"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termExp"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueStateCode", "erpIssueStateCode"));

        // call Pm_Exp_Discount.Get_Erp_List procedure to load all ERP information
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Exp_Discount.Get_Erp_List", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load ERP list.", e);
            l.throwing(getClass().getName(), "loadAllErp", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllErp", rs);
        }
        return rs;
    }

    /**
     * If the process ERP page is accessed from policy/risk page, call this method to process ERP.
     *
     * @param inputRecord
     * @return Record
     */
    public Record processErp(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processErp", new Object[]{inputRecord});
        }

        Record outputRecord = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Erp.Process_Exp_Disc");

        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to process ERP.", e);
            l.throwing(getClass().getName(), "processErp", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processErp", outputRecord);
        }
        return outputRecord;
    }

    /**
     * If the process ERP page is accessed from main menu, call this method to process ERP.
     *
     * @param inputRecord
     * @return Record
     */
    public Record processErpBatch(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processErpBatch", new Object[]{inputRecord});
        }

        Record outputRecord = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("issueStateCode", "erpIssueStateCode"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Exp_Discount.Process_Exp_Disc_Batch", mapping);

        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to process ERP.", e);
            l.throwing(getClass().getName(), "processErpBatch", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processErpBatch", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Save all updated ERP
     *
     * @param inputRecords
     * @return the number of rows updated
     */
    public int saveAllErp(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllErp", new Object[]{inputRecords});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("pointOverrideId", "pmComponentPointOverrideId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Erp.Save_All_Erp", mapping);
        int updateCount = 0;
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing saveAllErp", se);
            l.throwing(getClass().getName(), "saveAllErp", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllErp", updateCount);
        }
        return updateCount;
    }

    /**
     * Delete a ERP batch
     *
     * @param inputRecord
     * @return Record
     */
    public Record deleteErpBatch(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteErpBatch", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Exp_Discount.Delete_ERP_Batch");
        Record outputRecord = null;
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing deleteErpBatch", se);
            l.throwing(getClass().getName(), "deleteErpBatch", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteErpBatch", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Display the policies that have errors when deleting an ERP batch.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllErrorPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllErrorPolicy", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Erp.Load_All_Error_Policy");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load all error policies.", e);
            l.throwing(getClass().getName(), "loadAllErrorPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllErrorPolicy", rs);
        }
        return rs;
    }

}
