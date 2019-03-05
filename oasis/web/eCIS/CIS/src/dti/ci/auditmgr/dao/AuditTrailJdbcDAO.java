package dti.ci.auditmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.Querier;
import dti.oasis.util.QueryParm;
import oracle.jdbc.OracleTypes;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * DAO for getting data about audit trails.
 * <p/>
 * <p>(C) 2005 Delphi Technology, inc. (dti)</p>
 * Date: Oct 31, 2005
 *
 * @author Hong Yuan
 */
/*
* Revision Date    Revised By  Description
* --------------------------------------------------------------------
* 15/08/2006       gjli        Issue No. 62585
* 07/02/2007       gjli        delete  import files
* 04/09/2018       ylu         109179: refactor from CIAuditTrialDAO.java and CIAuditTrialPopupDAO.java
* --------------------------------------------------------------------
*/

public class AuditTrailJdbcDAO extends BaseDAO implements AuditTrailDAO {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * load all audit trail data in tab page for this entity
     *
     * refactor from CIAuditTrialDAO.java
     *
     * @param record
     * @return
     */
    @Override
    public RecordSet searchAuditTrailData(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "searhAuditTrailData", new Object[]{record});
        }

        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_AuditTrail.get_audit_trail_list");

        try {

            rs = spDao.execute(record);

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "Failed to search audit trail data.", e);
            l.throwing(getClass().getName(), "searhAuditTrailData", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "searhAuditTrailData", rs);
        }

        return rs;
    }

    /**
     * load audit history data into Popup page for this entity
     *
     * refactor from CIAuditTrialPopupDAO.java
     *
     * @param record
     * @return
     */
    @Override
    public RecordSet loadAuditTrailBySource(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAuditTrailBySource", new Object[]{record});
        }

        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_AuditTrail.get_audit_trail_popup_list");

        try {

            rs = spDao.execute(record);

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "Failed to load audit trail data.", e);
            l.throwing(getClass().getName(), "loadAuditTrailBySource", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAuditTrailBySource", rs);
        }

        return rs;
    }
}