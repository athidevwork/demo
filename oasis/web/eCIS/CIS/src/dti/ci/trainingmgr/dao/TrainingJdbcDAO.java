package dti.ci.trainingmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.ci.core.dao.StoredProcedureTemplate;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for getting data about training.
 * <p/>
 * <p>(C) 2005 Delphi Technology, inc. (dti)</p>
 * Date: Jan 12, 2006
 *
 * @author Hong Yuan
 */
/*
* Revision Date    Revised By  Description
* --------------------------------------------------------------------
*
* --------------------------------------------------------------------
*/

public class TrainingJdbcDAO extends BaseDAO implements TrainingDAO {

    /**
     * Get the list of the Institution Name
     *
     * @param inputRecord
     * @return List
     */
    @Override
    public List getListOfInstitutionName(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getListOfInstitutionName", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Training.Get_Institution_List");

        RecordSet rs = null;
        try {
            rs = spDao.execute(inputRecord);

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get Institution Name List.", e);
            l.throwing(getClass().getName(), "getListOfInstitutionName", ae);
            throw ae;
        }
        List instiArray = new ArrayList();
        Iterator it = rs.getRecords();
        while (it.hasNext()) {
            Record rd = (Record) it.next();
            HashMap map = new HashMap(6);
            map.put("entityFk", rd.getStringValueDefaultEmpty("entityId"));
            map.put("TrainingPopupIND", rd.getStringValueDefaultEmpty("trainingPopupInd"));
            map.put("entityName", rd.getStringValueDefaultEmpty("entityName"));
            map.put("city", rd.getStringValueDefaultEmpty("city"));
            map.put("stateCode", rd.getStringValueDefaultEmpty("stateCode"));
            map.put("countryCode", rd.getStringValueDefaultEmpty("countryCode"));
            instiArray.add(map);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getListOfInstitutionName", instiArray);
        }

        return instiArray;

    }

    /**
     * Get the training list of an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet getTrainingList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTrainingList", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Training.Get_Training_List");

        try {
            RecordSet rs = spDao.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getTrainingList", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get training list.", e);
            l.throwing(getClass().getName(), "getTrainingList", ae);
            throw ae;
        }
    }

    /**
     * Save the training Info change for an entity
     *
     * @param inputRecords
     * @return int
     */
    @Override
    public int saveTrainingData(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveTrainingData", new Object[]{inputRecords});
        }
        int updateCount = StoredProcedureTemplate.doBatchUpdate("Ci_Web_Training.Save_Training_Data", inputRecords);
        l.exiting(getClass().getName(), "saveTrainingData", new Integer(updateCount));

        return updateCount;
    }

}
