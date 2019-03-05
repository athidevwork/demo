package dti.ci.claimsmgr.dao;

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
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   4/19/2018
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ClaimsJdbcDAO extends BaseDAO implements ClaimsDAO {

    /**
     * Get the claim info.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet loadClaimLov(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadClaimLov", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Claims.load_claims_lov");

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadClaimLov", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get claim lov.", e);
            l.throwing(getClass().getName(), "loadClaimLov", ae);
            throw ae;
        }
    }

    /**
     * Get the claim info.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet loadClaimInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadClaimInfo", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Claims.load_claim_info");

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadClaimInfo", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get claim info.", e);
            l.throwing(getClass().getName(), "loadClaimInfo", ae);
            throw ae;
        }
    }

    /**
     * Get the claim participants list.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet loadClaimParticipants(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadClaimParticipantsList", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Claims.load_claim_participants_list");

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadClaimParticipantsList", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get claim participants.", e);
            l.throwing(getClass().getName(), "loadClaimParticipantsList", ae);
            throw ae;
        }
    }

    /**
     * Get the companion claims.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet loadCompanion(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCompanion", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Claims.load_claim_comp_claims_list");

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadCompanion", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get companion claims.", e);
            l.throwing(getClass().getName(), "loadCompanion", ae);
            throw ae;
        }
    }
}
