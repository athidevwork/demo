package dti.pm.transactionmgr.coirenewalmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class is an implements class for CoiRenewalDAO
 * <p/>
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 23, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/05/2010       dzhang     Renamed class & methods name.
 * 07/06/2010       dzhang     FieldId changed & remove DataRecordMapping.
 * ---------------------------------------------------
 */

public class CoiRenewalJdbcDAO extends BaseDAO implements CoiRenewalDAO {

    /**
     * save the Coi renewal data
     * <p/>
     *
     * @param inputRecord input record with Coi renewal data.
     */
    public void createCoiRenewal(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "createCoiRenewal", new Object[]{inputRecord});
        }
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Batch_Renewal.Coi_Form");

        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save data.", e);
            l.throwing(getClass().getName(), "createCoiRenewal", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "createCoiRenewal");
        }
    }

    /**
     * To load all Coi renewal event data.
     * <p/>
     *
     * @param inputRecord input record
     * @return Coi renewal event recordset
     */
    public RecordSet loadAllCoiRenewalEvent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoiRenewalEvent", new Object[]{inputRecord});
        }
        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "startSearchDateFilter"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "endSearchDateFilter"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termType", "termTypeFilter"));
        mapping.addFieldMapping(new DataRecordFieldMapping("submittedBy", "submittedByFilter"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coi_Renewal.Sel_Coi_Renewal_Event", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Coi renewal event data.", e);
            l.throwing(getClass().getName(), "loadAllCoiRenewalEvent", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoiRenewalEvent", new Object[]{rs});
        }
        return rs;
    }

    /**
     * To load all Coi renewal event detail data.
     * <p/>
     *
     * @param inputRecord input record
     * @return Coi renewal event detail recordset
     */
    public RecordSet loadAllCoiRenewalEventDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoiRenewalEventDetail", new Object[]{inputRecord});
        }
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coi_Renewal.Sel_Coi_Renewal_Event_Detail");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Coi renewal detail data.", e);
            l.throwing(getClass().getName(), "loadAllCoiRenewalEventDetail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoiRenewalEventDetail", new Object[]{rs});
        }
        return rs;
    }

}
