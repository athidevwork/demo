package dti.ci.entitysearch.listrole.data;

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
 * JDBC DAO for Entity List Role.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 18, 2008
 *
 * @author ldong
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/03/2010       fcb         Issue 105866: logic added for policy holder role. 
 * 05/26/2010       kshen       Moved the role type code checking into stored procedure.
 * 02/18/2011       kshen       Added method getGotoSourceUrl.
 * ---------------------------------------------------
*/

public class EntityListRoleJdbcDAO extends BaseDAO implements EntityListRoleDAO {
    /**
     * Retrieves all Edi Extract History information
     *
     * @param record              Record
     * @return RecordSet
     */
    public RecordSet loadEntityListRoleByEntity(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityListRoleByEntity", new Object[]{record});
        }

        String dbPkg = "WB_CI_TABS.get_entity_role_list_new";

        try {

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(dbPkg);
            RecordSet rs = spDao.execute(record);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadEntityListRoleByEntity", rs);
            }
            return rs;

        } catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load Entity List Role By Entity", e);
            l.throwing(getClass().getName(), "loadEntityListRoleByEntity", ae);
            throw ae;
        }
    }

    /**
     * Get goto source url for role type code.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public String getGotoSourceUrl(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getGotoSourceUrl", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("Ci_Web_Entity_Role.Get_Goto_Source_Url");

        try {
            RecordSet rs = spDAO.execute(inputRecord);
            String gotoSourceUrl = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD, "");

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getGotoSourceUrl", gotoSourceUrl);
            }
            return gotoSourceUrl;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get Goto Type.", e);
            l.throwing(getClass().getName(), "getGotoSourceUrl", ae);
            throw ae;
        }
    }
}
