package dti.ci.demographic.clientmgr.specialhandlingmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.ci.core.dao.StoredProcedureTemplate;
import dti.oasis.app.AppException;
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
 * JDBC dao for SpecialHandling
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 30, 2008
 *
 * @author
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SpecialHandlingJdbcDAO extends BaseDAO implements SpecialHandlingDAO {


    public RecordSet loadSpecialHandlingsByEntity(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadSpecialHandlingsByEntity", new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_specialhandling.select_specialhandling");
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadSpecialHandlingsByEntity", rs);
            }
            return rs;

        } catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load Special Handlings information", e);
            l.throwing(getClass().getName(), "loadSpecialHandlingsByEntity", ae);
            throw ae;
        }
    }

    public int addAllSpecialHandlings(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllSpecialHandlings", new Object[]{inputRecords});

        int updateCount = 0;
        // Insert the records in batch mode with 'wb_ci_specialhandling.save_specialhandling'
        updateCount = StoredProcedureTemplate.doBatchUpdate("wb_ci_specialhandling.save_specialhandling", inputRecords);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllSpecialHandlings", new Integer(updateCount));
        }
        return updateCount;
    }

    public int updateAllSpecialHandlings(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllSpecialHandlings", new Object[]{inputRecords});

        int updateCount = 0;
        // Insert the records in batch mode with 'wb_ci_specialhandling.save_specialhandling'
        updateCount = StoredProcedureTemplate.doBatchUpdate("wb_ci_specialhandling.save_specialhandling", inputRecords);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllSpecialHandlings", new Integer(updateCount));
        }
        return updateCount;
    }
}