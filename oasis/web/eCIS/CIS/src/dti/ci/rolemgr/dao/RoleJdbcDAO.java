package dti.ci.rolemgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.*;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for getting data about role.
 * <p/>
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date: Mar 28, 2018
 *
 * @author Herb Koenig
 */
/*
* Revision Date    Revised By  Description
* --------------------------------------------------------------------
* 04/02/2018       hxk         Issue 109175: Entity Role refactor
* 05/28/2018       ylu         Issue 109175: fix bug for refactor: search button don't work
 * --------------------------------------------------------------------
*/

public class RoleJdbcDAO extends BaseDAO implements RoleDAO {


    /**
     * Get the role list for an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet getRoleList(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRoleList", new Object[]{inputRecord, recordLoadProcessor});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Role.Get_Role_List");

        try {
            RecordSet rs = spDao.execute(inputRecord, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getRoleList", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get role list.", e);
            l.throwing(getClass().getName(), "getRoleList", ae);
            throw ae;
        }
    }


}
