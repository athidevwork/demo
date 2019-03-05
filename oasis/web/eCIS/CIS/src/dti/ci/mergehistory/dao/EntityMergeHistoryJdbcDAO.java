package dti.ci.mergehistory.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for Merge History
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   10/09/2015
 *
 * @author
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  109/09/2015     ylu         Issue 164517
 * ---------------------------------------------------
*/

public class EntityMergeHistoryJdbcDAO extends BaseDAO implements EntityMergeHistoryDAO {

    /**
     *  load merge history
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadMergeHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadMergeHistory", new Object[]{});
        }
        RecordLoadProcessor eRLP = AddSelectIndLoadProcessor.getInstance();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_cs_consolidate_entity.load_merge_history");

        try {
            RecordSet rs = spDao.execute(inputRecord,eRLP);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadMergeHistory", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Merge History.", e);
            l.throwing(getClass().getName(), "loadMergeHistory", ae);
            throw ae;
        }
    }

    /**
     * Un-merge the history
     * @param inputRecord
     * @return
     */
    @Override
    public String unMergeProcess(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "unMergeProcess", new Object[]{inputRecord});
        }
        String errorMsgInfo = "";

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("headId", "entityMergeHistoryId"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_cs_consolidate_entity.unmerge_process", mapping);
            RecordSet record = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "unMergeProcess", errorMsgInfo);
            }

        } catch (SQLException e) {
            errorMsgInfo = checkException(e);
        }
        return errorMsgInfo;
    }
}


