package dti.ci.propertymgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.ci.core.dao.StoredProcedureTemplate;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * DAO for Property
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 28, 2006
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/13/2009       kshen       Added codes to handle db error.
 * 04/07/2011       Blake       Modified getPropertyListSQL() for issue 118351.
 * ---------------------------------------------------
*/
public class PropertyJdbcDAO extends BaseDAO implements PropertyDAO {
    /**
     * Load all property of an entity.
     *
     * @param record
     * @return
     */
    @Override
    public RecordSet loadAllProperty(Record record, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProperty", new Object[]{record, loadProcessor});
        }

        RecordSet rs = null;
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Ci_Web_Property.Sel_All_Property");

        try {
            rs = sp.execute(record, loadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load property.", se);
            l.throwing(getClass().getName(), "loadAllProperty", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProperty", rs);
        }

        return rs;
    }

    /**
     * Save all property.
     *
     * @param rs
     * @return
     */
    @Override
    public int saveAllProperty(RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllProperty", new Object[]{rs});
        }

        int updateCount = StoredProcedureTemplate.doBatchUpdate("Ci_Web_Property.Save_Property", rs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllProperty", updateCount);
        }

        return updateCount;
    }

    /**
     * Save property
     *
     * @param inputRecord
     */
    public Record saveProperty(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "saveProperty";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        Record recResult = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Property.Save_Property_For_Service");
        try {
            recResult = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save property : " + inputRecord, se);
            l.throwing(getClass().getName(), "saveProperty", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName);
        }
        return recResult;
    }
}
