package dti.pm.coveragemgr.manuscriptmgr.dao;

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
 * This class implements the ManuscriptDAO interface. This is consumed by any business logic objects
 * that requires information about one or more Manuscript/Detail.
 * <p/>
 * 
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 17, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/09/2010       gxc         Issue 117088 Modified saveAllManuscript to pass effectiveFromDate and effectiveToDate
 *                              instead of manuscriptEffectiveFromDate and manuscriptEffectiveToDate
 * 02/10/2012       wfu         125055 - Added saveAttachment and loadAttachment.
 * 06/25/2012       tcheng      134650 - Added changeAttachment.
 * 08/31/2016       tzeng       179057 - Added validateManuscriptOverlap to validate duplicate manuscripts in back end.
 * 09/09/2016       xnie        178813 - Added validateSameOffVersionExists().
 * ---------------------------------------------------
 */
public class ManuscriptJdbcDAO extends BaseDAO implements ManuscriptDAO {
    /**
     * Retrieves all Manuscript data
     * <p/>
     *
     * @param record        input record
     * @param loadProcessor an instance of the load processor to set page entitlements
     * @return a RecordSet with loaded list of Manuscript data.
     */
    public RecordSet loadAllManuscript(Record record, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllManuscript", new Object[]{record});

        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Manuscript.Sel_Manuscript_Endorsement", mapping);
        try {
            rs = spDao.execute(record, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to load Manuscript information", e);
            l.throwing(getClass().getName(), "loadAllManuscript", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllManuscript", rs);
        return rs;
    }

    /**
     * Retrieves all Manuscript Detail data
     * <p/>
     *
     * @param record        input record
     * @param loadProcessor an instance of the load processor to set page entitlements
     * @return a RecordSet with loaded list of Manuscript Detail data.
     */
    public RecordSet loadAllManuscriptDetail(Record record, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllManuscriptDetail", new Object[]{record});

        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("manuscriptId", "manuscriptEndorsementId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Manuscript.Sel_Manuscript_Detail", mapping);
        try {
            rs = spDao.execute(record, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to load Manuscript Detail information", e);
            l.throwing(getClass().getName(), "loadAllManuscriptDetail", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllManuscriptDetail", rs);
        return rs;
    }

    /**
     * Load label, width and visibility for Manuscript Detail data.
     * <p/>
     *
     * @param record input record
     * @return a RecordSet with loaded list of information to control Manuscript Detail data.
     */
    public RecordSet loadManuscriptEndorsementDtl(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "loadManuscriptEndorsementDtl", record);

        RecordSet rs;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Manuscript.Sel_Product_Manuscript_Detail");
        try {
            rs = spDao.execute(record);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to load Manuscript Detail formatting information", e);
            l.throwing(getClass().getName(), "loadManuscriptEndorsementDtl", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadManuscriptEndorsementDtl", rs);
        return rs;
    }

    /**
     * Save all newly added or updated Manuscript data with Pm_Manu_Endor.Save_Manuscript stored procedure.
     * <p/>
     *
     * @param inputRecords a set of Mansucript Records for saving.
     * @return the number of rows updated.
     */
    public int saveAllManuscript(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllManuscript", new Object[]{inputRecords});

        int updateCount;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("manusEndId", "manuscriptEndorsementId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("text", "additionalText"));
        mapping.addFieldMapping(new DataRecordFieldMapping("premium", "manuscriptPremium"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "transactionLogId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Manu_Endor.Save_Manuscript", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save Manuscript.", e);
            l.throwing(getClass().getName(), "saveAllManuscript", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAllManuscript", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Save the Manuscript data when the officially saved data changed with Pm_Manu_Endor.Change_Manuscript stored procedure.
     * <p/>
     *
     * @param inputRecords a set of Manuscript Records for updating.
     * @return the number of rows updated.
     */
    public int updateAllManuscript(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllManuscript", inputRecords);

        int updateCount;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("manusEndId", "manuscriptEndorsementId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("text", "additionalText"));
        mapping.addFieldMapping(new DataRecordFieldMapping("premium", "manuscriptPremium"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "EffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "EffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "transactionLogId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Manu_Endor.Change_Manuscript", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to change Manuscript.", e);
            l.throwing(getClass().getName(), "updateAllManuscript", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "updateAllManuscript", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Save all newly added or updated Manuscript detail data with Pm_Manu_Endor.Save_Manuscript_Dtl stored procedure.
     * <p/>
     *
     * @param inputRecords a set of Manuscript Detail Records for saving.
     * @return the number of rows updated.
     */
    public int saveAllManuscriptDetail(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllManuscriptDetail", inputRecords);

        int updateCount;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("manuEndorsementDtlId", "manuscriptEndorsementDtlId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Manu_Endor.Save_Manuscript_Dtl", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save Manuscript Detail.", e);
            l.throwing(getClass().getName(), "saveAllManuscriptDetail", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAllManuscriptDetail", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Delte all given input Manuscript records with Pm_Manu_Endor.Delete_Manuscript stored procedure.
     * <p/>
     *
     * @param inputRecords a set of Mansucript Records for deleting.
     * @return the number of rows deleted.
     */
    public int deleteAllManuscript(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllManuscript", inputRecords);

        int deleteCount;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("manusEndId", "manuscriptEndorsementId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Manu_Endor.Delete_Manuscript", mapping);
        try {
            deleteCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete Manuscripts.", e);
            l.throwing(getClass().getName(), "deleteAllManuscript", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "deleteAllManuscript", new Integer(deleteCount));
        return deleteCount;
    }

    /**
     * Delete all given input Manuscript Detail records with Pm_Manu_Endor.Delete_Manuscript_Dtl stored procedure.
     * <p/>
     *
     * @param inputRecords a set of Manuscript Detail records for deleting.
     * @return the number of rows deleted.
     */
    public int deleteAllManuscriptDetail(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllManuscriptDetail", inputRecords);

        int deleteCount;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("manuDtlId", "manuscriptEndorsementDtlId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Manu_Endor.Delete_Manuscript_Dtl", mapping);
        try {
            deleteCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete Manuscript Details.", e);
            l.throwing(getClass().getName(), "deleteAllManuscriptDetail", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "deleteAllManuscriptDetail", new Integer(deleteCount));
        return deleteCount;
    }

    /**
     * Load all available Mansucript data for selection.
     * <p/>
     *
     * @param inputRecord a Record with information to load the results.
     * @param loadProcessor an instance of the load processor to set page entitlements
     * @return a set of Records with loaded available Manuscript data.
     */
    public RecordSet loadAllAvailableManuscript(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableManuscript", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("stateCode", "issueStateCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("effectiveFromDate", "transEffectiveFromDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Manuscript.Sel_Product_Manuscript", mapping);
            rs = spDao.execute(inputRecord, loadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllAvailableManuscript", rs);
            }

            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load available manuscript information", e);
            l.throwing(getClass().getName(), "loadAllAvailableManuscript", ae);
            throw ae;
        }
    }

    /**
     * Save the attached RTF file to db table.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    public void saveAttachment(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveAttachment", inputRecord);

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_MANU_ENDOR.Save_Attachment");
        try {
            spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save manuscript file.", e);
            l.throwing(getClass().getName(), "saveAttachment", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAttachment");
    }

    /**
     * Load the attached RTF file to web page for downloading.
     * <p/>
     *
     * @param inputRecord input record
     * @return a Record with file input stream.
     */
    public Record loadAttachment(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAttachment", inputRecord);

        RecordSet rs = null;
        Record record = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_MANU_ENDOR.Load_Attachment");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load manuscript file.", e);
            l.throwing(getClass().getName(), "loadAttachment", ae);
            throw ae;
        }

        if (rs !=null && rs.getSize() > 0) {
            record = rs.getRecord(0);
        }


        l.exiting(getClass().getName(), "loadAttachment");
        return record;
    }

    /**
     * Change the attached RTF file to db table.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    public void changeAttachment(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "changeAttachment", inputRecord);
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_MANU_ENDOR.Change_Attachment");
        try {
            spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to change manuscript file.", e);
            l.throwing(getClass().getName(), "changeAttachment", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "changeAttachment");
    }

    /**
     * Check if manuscripts overlap in all terms.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    @Override
    public boolean hasManuscriptOverlap(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasManuscriptOverlap", new Object[]{inputRecord});
        }

        boolean isOverlap = false;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Manuscript.Has_Manuscript_Overlap");
        try {
            isOverlap = spDao.execute(inputRecord).getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check if manuscript overlap", e);
            l.throwing(getClass().getName(), "hasManuscriptOverlap", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasManuscriptOverlap", isOverlap);
        }

        return isOverlap;
    }

    /**
     * Check if any manuscript endorsement version which is from same official record and in same time period exists.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    public String validateSameOffVersionExists(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateSameOffVersionExists", inputRecord);
        String sameOffVersionExists;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Manuscript.Same_Official_Version_Exist");
        try {
            sameOffVersionExists = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate if same official record exists.", e);
            l.throwing(getClass().getName(), "validateSameOffVersionExists", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateSameOffVersionExists");

        return sameOffVersionExists;
    }
}
