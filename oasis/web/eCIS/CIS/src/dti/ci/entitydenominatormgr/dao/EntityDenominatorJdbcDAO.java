package dti.ci.entitydenominatormgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.*;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * DAO for Denominator
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   May 30, 2006
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 *
 * ---------------------------------------------------
*/
public class EntityDenominatorJdbcDAO extends BaseDAO implements EntityDenominatorDAO {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Load all denominator of an entity.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    @Override
    public RecordSet loadAllEntityDenominator(Record inputRecord, RecordLoadProcessor loadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEntityDenominator", new Object[]{inputRecord, loadProcessor});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Denominator.Load_All_Entity_Denominator");

        try {
            RecordSet rs = spDao.executeReadonly(inputRecord, loadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllEntityDenominator", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException appException = handleSQLException(e, "ci.generic.error");
            l.throwing(getClass().getName(), "loadAllEntityDenominator", appException);
            throw appException;
        }
    }

    /**
     * Save all entity denominator.
     *
     * @param rs
     */
    @Override
    public void saveAllEntityDenominator(RecordSet rs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllEntityDenominator", new Object[]{rs});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Denominator.Save_Entity_Denominator");
        try {
            spDao.executeBatch(rs);
        } catch (SQLException e) {
            AppException appException = handleSQLException(e, "ci.generic.error");
            l.throwing(getClass().getName(), "saveEntityClass", appException);
            throw appException;
        }

        l.exiting(getClass().getName(), "saveEntityClass");
    }
}
