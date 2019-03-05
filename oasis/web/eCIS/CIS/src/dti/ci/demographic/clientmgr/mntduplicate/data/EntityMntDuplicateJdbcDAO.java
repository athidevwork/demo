package dti.ci.demographic.clientmgr.mntduplicate.data;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC DAO for Maintain Entity Duplicate.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 19, 2008
 *
 * @author ldong
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/09/2015       ylu         164517: incorporate current procedure with new package to unconsolidated/unmerge
 * 11/26/2015       dpang       Issue 164029. Add saveEntityMntDuplicateWs for web service PartyChangeService
 * ---------------------------------------------------
*/

public class EntityMntDuplicateJdbcDAO extends BaseDAO implements EntityMntDuplicateDAO {
    /**
     * Merge Duplicate Entity
     *
     * @param inputRecords input records
     * @return a string representing the error Message if any, or null
     */
    public String saveEntityMntDuplicate(Record inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityMntDuplicate", new Object[]{inputRecords});
        }
        String errorMsg = "";
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityTo", "pk"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entityFrom", "duplicateEntityPk"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_cs_consolidate_entity.call_cs_consolidate_entity", mapping);
            RecordSet record = spDao.execute(inputRecords);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveEntityMntDuplicate", errorMsg);
            }

        } catch (SQLException e) {
            errorMsg = checkException(e);  //commented code below since message itself is used by action class as status,
          //  AppException ae = ExceptionHelper.getInstance().handleException("Unable to merge duplicate entities ", e);
          //  l.throwing(getClass().getName(), "saveEntityMntDuplicate", ae);
         //   throw ae;
        }
        return errorMsg;
    }

    /**
     * Merge Duplicate Entity for web service PartyChangeService.
     *
     * @param inputRecords input records
     * @return a string representing the error Message if any, or null
     */
    public String saveEntityMntDuplicateWs(Record inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityMntDuplicateWs", new Object[]{inputRecords});
        }
        String errorMsg = "";
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_cs_consolidate_entity.call_cs_consolidate_entity_ws");
            spDao.execute(inputRecords);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveEntityMntDuplicateWs", errorMsg);
            }

        } catch (SQLException e) {
            errorMsg = checkException(e);
        }
        return errorMsg;
    }


}
