package dti.ci.correspondencemgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/10/2018
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/11/2018       dzhang      Issue 109204: correspondence refactor
 * ---------------------------------------------------
 */
public class CorrespondenceJdbcDAO extends BaseDAO implements CorrespondenceDAO {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public RecordSet loadCorrespondenceList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCorrespondenceList", new Object[]{inputRecord});
        }

        RecordSet outputRecordSet = null;
        StoredProcedureDAO storedProcedureDAO = StoredProcedureDAO.getInstance("ci_web_correspondence.get_correspondence_list");
        try {
            outputRecordSet = storedProcedureDAO.execute(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadCorrespondenceList");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCorrespondenceList", outputRecordSet);
        }
        return outputRecordSet;
    }
}
