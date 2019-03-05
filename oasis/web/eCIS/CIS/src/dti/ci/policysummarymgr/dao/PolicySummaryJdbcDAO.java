package dti.ci.policysummarymgr.dao;

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
 * DAO for Policy Summary Info
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   12/20/2013
 *
 * @author hxk
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public class PolicySummaryJdbcDAO extends BaseDAO implements PolicySummaryDAO {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * load additional info data.
     * @param inputRecord
     * @return
     */
    @Override
    public Record loadAddlInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddlInfo", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("wb_ci_tabs.get_policy_addl_info");
        Record policySummaryRecord = null;
        try {
            RecordSet rs = sp.execute(inputRecord);
            l.entering(getClass().getName(), "loadAddlInfo", new Object[]{rs});
            if(rs.getSize() > 0 )   {
                policySummaryRecord = rs.getFirstRecord();
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAddlInfo", policySummaryRecord);
            }
            return policySummaryRecord;
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Addl info", se);
            l.throwing(getClass().getName(), "loadAddlInfo", ae);
            throw ae;
        }
    }
}
