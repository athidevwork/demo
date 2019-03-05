package dti.pm.policysummarymgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.obr.RequestHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordMapper;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.WebQuery;
import dti.oasis.util.WebQueryInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the PolicySummaryDAO interface. This is consumed by any business logic objects
 * that requires information about one or more Policy Summary.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 06, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/06/2016       wdang       168069 - Initial version.
 * ---------------------------------------------------
 */
public class PolicySummaryJdbcDAO implements PolicySummaryDAO {

    private final static String PM_POLICY_SUMMARY = "PM_POLICY_SUMMARY";

    @Override
    public RecordSet loadPolicySummary(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicySummary", new Object[]{inputRecord});
        }

        RecordSet rs = new RecordSet();
        Connection connection = null;
        try {
            // build the Map parameters.
            Map parmsMap = new HashMap();
            RecordMapper.getInstance().map(inputRecord, parmsMap);

            // get connection
            connection = getAppDataSource().getConnection();

            WebQuery query = WebQuery.getInstance();
            WebQueryInfo queryInfo = query.getQueryById(connection, PM_POLICY_SUMMARY);

            DisconnectedResultSet drs = WebQuery.getInstance().getResultSet(connection, queryInfo.getQueryPk(), parmsMap);
            RecordSet rs0 = RequestHelper.convertDisconnectedResultSetToRecordSet(drs);

            //  RecordLoadProcessor
            for (int i = 0; i < rs0.getSize(); i ++) {
                Record rec = rs0.getRecord(i);
                if (recordLoadProcessor.postProcessRecord(rec, true)) {
                    rs.addRecord(rec);
                }
            }
            recordLoadProcessor.postProcessRecordSet(rs);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load policy summary", e);
            l.throwing(getClass().getName(), "loadPolicySummary", ae);
            throw ae;
        }
         finally {
            if (connection != null) {
                DatabaseUtils.close(connection);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicySummary", rs);
        }
        return rs;
    }

    public DataSource getAppDataSource() {
        return m_appDataSource;
    }

    public void setAppDataSource(DataSource appDataSource) {
        m_appDataSource = appDataSource;
    }

    private DataSource m_appDataSource;
}
