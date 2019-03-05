package dti.ci.priorcarriermgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.ci.core.dao.StoredProcedureTemplate;
import dti.ci.priorcarriermgr.PriorCarrierFields;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   9/27/12
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/08/2012       kshen       Added methods for refactor Prior Carrier page.
 * ---------------------------------------------------
 */
public class PriorCarrierHistoryJdbcDAO extends BaseDAO implements PriorCarrierHistoryDAO {

    /**
     * Load all prior carrier of a entity by filter criteria.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    @Override
    public RecordSet loadAllPriorCarrier(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorCarrier", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("ci_web_prior_carrier.Sel_Prior_Carrier");

        try {
            RecordSet rs = sp.execute(inputRecord, loadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllPriorCarrier", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load prior carrier", e);
            l.throwing(getClass().getName(), "loadAllPriorCarrier", ae);
            throw ae;
        }
    }

    /**
     * Get the default term year.
     *
     * @return
     */
    @Override
    public String getDefaultTermYear(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultTermYear", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("ci_web_prior_carrier.Get_Default_Term_Year");

        try {
            RecordSet rs = sp.execute(inputRecord);

            String defaultTermYear = rs.getSummaryRecord().getStringValue(PriorCarrierFields.DEFAULT_TERM_YEAR);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getDefaultTermYear", defaultTermYear);
            }

            return defaultTermYear;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load prior carrier", e);
            l.throwing(getClass().getName(), "loadAllPriorCarrier", ae);
            throw ae;
        }
    }

    /**
     * Save all the prior carrier records.
     *
     * @param inputRecords
     * @return
     */
    @Override
    public int saveAllPriorCarrier(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPriorCarrier", new Object[]{inputRecords});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("externalClmsRptSummaryId", "externalClaimsRptSummaryId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("finalDispositionDesc", "finalDispositionDescription"));
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("ci_web_prior_carrier.save_prior_carrier", mapping);

        try {
            int count = sp.executeBatch(inputRecords);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveAllPriorCarrier", count);
            }
            return count;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save prior carrier.", e);
            l.throwing(getClass().getName(), "saveAllPriorCarrier", ae);
            throw ae;
        }
    }

    /**
     * load all prior carrier history
     *
     * @param inputRecord
     * @return rs
     */
    public RecordSet loadAllPriorCarrierHistory(Record inputRecord) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPriorCarrierHistory", new Object[]{inputRecord});

        RecordSet rs = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("extClaimsRptSummaryId", "pk"));
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("ci_web_prior_carrier.load_prior_carrier_history",mapping);
        try {
            rs = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load prior carrier history", se);
            l.throwing(getClass().getName(), "loadAllPriorCarrierHistory", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPriorCarrierHistory", rs);
        }
        return rs;
    }

    /**
     * save all prior carrier history
     *
     * @param rs prior carrier info
     */
    public int saveAllPriorCarrierHistory(RecordSet rs) throws SQLException {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPriorCarrierHistory", new Object[]{rs});
        }

        int updateCount = StoredProcedureTemplate.doBatchUpdate("ci_web_prior_carrier.update_prior_carrier_history", rs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPriorCarrierHistory", new Integer(updateCount));
        }

        return updateCount;
    }

    /**
     * Check if audit record exists for an entity.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public YesNoFlag hasAuditRecord(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasAuditRecord", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("ci_web_prior_carrier.Has_Audit_Record");
        try {
            Record resultRecord = sp.execute(inputRecord).getSummaryRecord();
            YesNoFlag result = YesNoFlag.getInstance(resultRecord.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD, "N"));

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "hasAuditRecord", result);
            }
            return result;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check if audit record exist for an entity", e);
            l.throwing(getClass().getName(), "hasAuditRecord", ae);
            throw ae;
        }
    }
}
