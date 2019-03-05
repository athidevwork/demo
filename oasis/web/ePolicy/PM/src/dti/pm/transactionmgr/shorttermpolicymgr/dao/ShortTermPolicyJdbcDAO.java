package dti.pm.transactionmgr.shorttermpolicymgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC dao for short term policy
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 01, 2011
 *
 * @author ryzhao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 * ---------------------------------------------------
 */

public class ShortTermPolicyJdbcDAO extends BaseDAO implements ShortTermPolicyDAO {

    /**
     * Accept short term policy
     *
     * @param inputRecord that has transaction id/code, policy id, term eff/exp date
     * @return Record include the field indicating if accept policy successfully
     */
    public Record acceptPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "acceptPolicy", new Object[]{inputRecord});
        }

        Record result;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("tranId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tranCode", "transactionCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Short_Term.Accept_Policy", mapping);
        try {
            result = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to accept policy.", e);
            l.throwing(getClass().getName(), "acceptPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "acceptPolicy", result);
        }
        return result;
    }

    /**
     * Decline short term policy
     *
     * @param inputRecord that has cancellation related fields
     */
    public void declinePolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "declinePolicy", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffFrom", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffTo", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelType", "cancellationType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelReason", "cancellationReason"));
        mapping.addFieldMapping(new DataRecordFieldMapping("methodCode", "cancellationMethod"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Short_Term.Decline_Policy", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to decline policy.", e);
            l.throwing(getClass().getName(), "declinePolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "declinePolicy");
        }
    }
}