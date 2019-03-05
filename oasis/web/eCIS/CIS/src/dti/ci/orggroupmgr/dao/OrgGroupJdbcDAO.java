package dti.ci.orggroupmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 5, 2009
 *
 * @author msnadar
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/12/2018       dpang       Issue 193846: Refactor Org/Group page.
 * ---------------------------------------------------
 */
public class OrgGroupJdbcDAO extends BaseDAO implements OrgGroupDAO {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Get Claim Code History
     *
     * @param inputRecord Source Table and FK
     * @return  The CLaim Code History
     */
    public RecordSet loadAllMember(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMember", new Object[]{inputRecord});
        }
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_tabs.get_org_group_members_list");
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get org group member list", e);
            l.throwing(getClass().getName(), "loadAllMember", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMember", rs);
        }

        return rs;
    }

    public RecordSet loadSummary(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadSummary", new Object[]{inputRecord});
        }
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_tabs.get_org_group_summary");
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get summary result.", e);
            l.throwing(getClass().getName(), "loadSummary", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadSummary", rs);
        }

        return rs;
    }
 public RecordSet loadAddress(Record inputRecord) {
     if (l.isLoggable(Level.FINER)) {
         l.entering(getClass().getName(), "loadAddress", new Object[]{inputRecord});
     }
     RecordSet rs = null;
     try {
         StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_tabs.get_org_group_address");
         rs = spDao.execute(inputRecord);
     } catch (SQLException e) {
         AppException ae =
                 ExceptionHelper.getInstance().handleException("Unable to get address record.", e);
         l.throwing(getClass().getName(), "retrieveAddressResultSet", ae);
         throw ae;
     }

     if (l.isLoggable(Level.FINER)) {
         l.exiting(getClass().getName(), "loadAddress", rs);
     }

     return rs;
 }
}
