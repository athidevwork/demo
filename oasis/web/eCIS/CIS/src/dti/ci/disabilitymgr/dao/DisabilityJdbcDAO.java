package dti.ci.disabilitymgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.ci.core.dao.StoredProcedureTemplate;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.*;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * DAO for Disability
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   May 12, 2006
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
public class DisabilityJdbcDAO extends BaseDAO implements DisabilityDAO {
    /**
     * Get the Disability list of an entity.
     *
     * @param inputRecord
     * @return  RecordSet
     */
    @Override
    public RecordSet getDisabilityList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDisabilityList", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Disability.Get_Disability_List");
        try {
            RecordSet rs = spDao.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getDisabilityList", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get education list.", e);
            l.throwing(getClass().getName(), "getDisabilityList", ae);
            throw ae;
        }
    }

    /**
     * Save the disability Info change for an entity
     *
     * @param inputRecords
     * @return int
     */
    @Override
    public int saveDisabilityData(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveDisabilityData", new Object[]{inputRecords});
        }
        int updateCount = StoredProcedureTemplate.doBatchUpdate("Ci_Web_Disability.Save_Disability_Data", inputRecords);
        l.exiting(getClass().getName(), "saveDisabilityData", new Integer(updateCount));

        return updateCount;
    }


}
