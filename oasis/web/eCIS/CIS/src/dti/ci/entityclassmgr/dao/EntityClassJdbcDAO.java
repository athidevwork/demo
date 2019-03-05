package dti.ci.entityclassmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The jdbc DAO for entity class.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/11/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/17/2018       JLD         Issue 189300. Correction for batch update.
 * ---------------------------------------------------
 */
public class EntityClassJdbcDAO extends BaseDAO implements EntityClassDAO {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Load entity class record by given entity class id.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record loadEntityClass(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityClass", new Object[]{inputRecord});
        }

        Record result = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Class.Sel_Entity_Class");

        try {
            RecordSet rs = spDao.executeReadonly(inputRecord);

            if (rs != null && rs.getSize() > 0) {
                result = rs.getFirstRecord();
            }
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleSQLException("Failed to load entity class.", e);
            l.throwing(getClass().getName(), "loadEntityClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityClass", result);
        }
        return result;
    }

    /**
     * Load all entity class of an entity.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllEntityClass(Record inputRecord, RecordLoadProcessor loadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEntityClass", new Object[]{inputRecord, loadProcessor});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Class.Sel_All_Entity_Class");
        RecordSet rs = null;

        try {
            rs = spDao.executeReadonly(inputRecord, loadProcessor);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleSQLException("Failed to load all entity class for entity.", e);
            l.throwing(getClass().getName(), "loadAllEntityClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEntityClass", rs);
        }
        return rs;
    }

    /**
     * Save entity class.
     *
     * @param rs
     * @return The PK (entity_class_pk) of the new entity class.
     */
    @Override
    public void saveEntityClass(RecordSet rs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityClass", new Object[]{rs});
        }

        RecordSet changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(rs);

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Class.Save_Entity_Class");

        try {
            spDao.executeBatch(changedRecords);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleSQLException("Failed to save entity class.", e);
            l.throwing(getClass().getName(), "saveEntityClass", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveEntityClass");
    }


    /**
     * Save entity class codes for web service PartyChangeService.
     *
     * @param record
     * @return
     */
    @Override
    public Record saveEntityClassWs(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityClassWs", new Object[]{record});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Class.Save_Entity_Class_Ws");

        try {
            Record result = spDao.executeUpdate(record);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveEntityClassWs", result);
            }
            return result;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleSQLException("Unable to save entity class", e);
            l.throwing(getClass().getName(), "saveEntityClassWs", ae);
            throw ae;
        }
    }

    /**
     * Check if the current entity class is overlap with another entity class.
     *
     * @param record
     * @return
     */
    @Override
    public boolean hasOverlapEntityClass(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasOverlapEntityClass", new Object[]{record});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Class.Has_Overlap_Class");

        try {
            Record summaryRecord = spDao.execute(record).getSummaryRecord();

            boolean result = YesNoFlag.getInstance(
                    summaryRecord.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD, "N")).booleanValue();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "hasOverlapEntityClass", result);
            }
            return result;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleSQLException("Unable to check if there is overlap entity class.", e);
            l.throwing(getClass().getName(), "hasOverlapEntityClass", ae);
            throw ae;
        }
    }
}
