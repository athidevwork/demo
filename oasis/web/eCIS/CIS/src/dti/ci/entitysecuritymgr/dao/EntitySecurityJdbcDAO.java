package dti.ci.entitysecuritymgr.dao;

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
 * DAO for Security
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   07/01/2013
 *
 * @author Herb Koenig
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
*/
public class EntitySecurityJdbcDAO extends BaseDAO implements EntitySecurityDAO {

    /**
     * Get security
     *
     * @param inputRecord
     */
    public RecordSet getSecurity(Record inputRecord){

        Logger l = LogUtils.getLogger(getClass());

        String methodName = "getSecurity" ;

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }

        RecordSet recResult = new RecordSet();

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("cs_check_entity_access_cstm");


        try {
            recResult = spDao.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get security : " + inputRecord, se);
            l.throwing(getClass().getName(), "getSecurity", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName);
        }
        return recResult;
    }
}
