package dti.ci.vendormgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
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
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   8/13/14
 *
 * @author wkong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class PhoneLogJdbcDAO extends BaseDAO implements PhoneLogDAO{

    /**
     * Get the phone log list for an entity.
     * @param inputRecord the information of an entity.
     * @return The phone log list of the entity.
     */
    public RecordSet getPhoneLog(Record inputRecord, RecordLoadProcessor loadProcessor){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPhoneLog", new Object[]{inputRecord});
        }

        RecordSet recordSet = null;

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "entityFK"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Vendor_Phone_Log.Sel_Phone_Log", mapping);
            recordSet = spDao.execute(inputRecord, loadProcessor);
        } catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to get phone log list", e);
            l.throwing(getClass().getName(), "getPhoneLog", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPhoneLog", recordSet);
        }
        return recordSet;
    }

    /**
     * Save phone Log.
     * @param inputRecord the information of an entity.
     * @param recordSet the detail info.
     */
    public void savePhoneLog(RecordSet recordSet){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePhoneLog", new Object[]{recordSet});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "entityFK"));
        mapping.addFieldMapping(new DataRecordFieldMapping("vendorId", "vendorPK"));

        StoredProcedureDAO spDao =  StoredProcedureDAO.getInstance("Ci_Web_Vendor_Phone_Log.Save_Phone_Log", mapping);
        try {
            int count = spDao.executeBatch(recordSet);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "savePhoneLog", count);
            }
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save phone log.", e);
            l.throwing(getClass().getName(), "savePhoneLog", ae);
            throw ae;
        }
    }
}
