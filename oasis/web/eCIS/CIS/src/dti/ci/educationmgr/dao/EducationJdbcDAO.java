package dti.ci.educationmgr.dao;

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
 * DAO for Education
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   May 17, 2006
 *
 * @author gjli
 */

//
//* Revision Date    Revised By  Description
//* ---------------------------------------------------
//* 04/3/2007       Jerry        entities based on the value entered in the system parameter 'CI_SCHOOL_CLASS'.
//* 12/12/2007      Kenney       Enh 78054: Modified the SQL to get entity_institution_fk
//* 02/05/2009      hxk          Added effective from/to dates to SQL.    
//* 02/09/2010      kenney       Modfied for issue 104106. Set page entitlement flag
//* 05/30/2016       dpang       Issue 149588: retrieve hub data if needed.
//* ---------------------------------------------------
public class EducationJdbcDAO extends BaseDAO implements EducationDAO {



    /**
     * Get the education list of an entity.
     *
     * @param inputRecord
     * @return  RecordSet
     */
    @Override
    public RecordSet getEducationList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEducationList", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(isHubEnabled() ? "Ci_Web_Education_H.Get_Education_List" : "ci_web_education.Get_Education_List");

        try {
            RecordSet rs = spDao.execute(inputRecord,AddSelectIndLoadProcessor.getInstance());

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getEducationList", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get education list.", e);
            l.throwing(getClass().getName(), "getEducationList", ae);
            throw ae;
        }


    }

    /**
     * Get entity Info for an entity (dateOfBirth, dateOfDead).
     *
     * @param inputRecord
     * @return Record
     */
    @Override
    public Record getEntityInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityInfo", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_education.Get_Entity_Info");

        try {
            RecordSet entityRecordSet = spDao.execute(inputRecord);
            Record entityRecord = entityRecordSet.getFirstRecord();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getEntityInfo", entityRecordSet);
            }
            return entityRecord;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get entity info.", e);
            l.throwing(getClass().getName(), "getEntityInfo", ae);
            throw ae;
        }

    }

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

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_education.Get_Institution_List");

        RecordSet rs=null;
        try {
            rs = spDao.execute(inputRecord);

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get Institution Name List.", e);
            l.throwing(getClass().getName(), "getListOfInstitutionName", ae);
            throw ae;
        }
        List instiArray = new ArrayList();
        Iterator it= rs.getRecords();
        while (it.hasNext()) {
            Record rd = (Record) it.next();
            HashMap map = new HashMap(6);
            map.put("entityFk",rd.getStringValue("entityId",""));
            map.put("EducationPopupIND",rd.getStringValue("educationPopupInd",""));
            map.put("entityName",rd.getStringValue("entityName",""));
            map.put("city",rd.getStringValue("city",""));
            map.put("stateCode",rd.getStringValue("stateCode","") );
            map.put("countryCode",rd.getStringValue("countryCode",""));
            instiArray.add(map);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getListOfInstitutionName", instiArray);
        }

        return instiArray;

    }
    
    /**
     * Save the education Info change for an entity
     *
     * @param inputRecord
     * @return int
     */
    @Override
    public int saveEducationData(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEducationData", new Object[]{inputRecords});
        }
        int updateCount = StoredProcedureTemplate.doBatchUpdate("ci_web_education.Save_Education_Data", inputRecords);
        l.exiting(getClass().getName(), "saveEducationData", new Integer(updateCount));

        return updateCount;
    }

    /**
     * Save phone number
     *
     * @param inputRecord
     */
    public Record saveEducation(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "saveEducation";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        Record recResult = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_education.Save_Education_For_Service");
        try {
            recResult = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save education : " + inputRecord, se);
            l.throwing(getClass().getName(), "saveEducation", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName);
        }
        return recResult;
    }

}
