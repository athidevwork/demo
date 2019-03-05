package dti.ci.expertwitnessmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * DAO for Expert Witness
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 01, 2007
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  Issue  70335    eCIS/ Expert Witness  jerry
 *  07/09/2009      Fred        Modified getPersonInfoSQL()
 * ---------------------------------------------------
*/
public class ExpertWitnessJdbcDAO extends BaseDAO implements ExpertWitnessDAO {
    /**
     * Get Expert witness count of an entity.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public int getExpertWitnessCountOfEntity(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getExpertWitnessCountOfEntity", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "pk"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Expertwitness.Get_Exp_Wit_Count", mapping);

        try {
            int count = spDao.execute(inputRecord).getSummaryRecord()
                    .getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getExpertWitnessCountOfEntity", count);
            }

            return count;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get Expert Witness count.", e);
            l.throwing(getClass().getName(), "getExpertWitnessCountOfEntity", ae);
            throw ae;
        }
    }

    /**
     * Get person info of Expert Witness.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record getPersonInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPersonInfo", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "entityFK"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Expertwitness.Sel_Person_Info", mapping);
        Record record = null;
        try {
            RecordSet rs = spDao.execute(inputRecord);
            if (rs.getSize() > 0) {
                record = rs.getFirstRecord();
            }
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get person info.", e);
            l.throwing(getClass().getName(), "getPersonInfo", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPersonInfo", record);
        }
        return record;
    }

    /**
     * Load Expert Witness addresses.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllAddress(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAddress", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "entityFK"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Expertwitness.Sel_Address_List", mapping);

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllAddress", rs);
            }
            return rs;            
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get address list.", e);
            l.throwing(getClass().getName(), "loadAllAddress", ae);
            throw ae;
        }
    }

    /**
     * Load Expert Witness addresses.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllPhone(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPhone", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "entityFK"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Expertwitness.Sel_Phone_List", mapping);

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllPhone", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Expert Witness phone.", e);
            l.throwing(getClass().getName(), "loadAllPhone", ae);
            throw ae;
        }
    }

    /**
     * Load Education info of Expert Witness.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllEducation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEducation", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "entityFK"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Expertwitness.Sel_Education_List", mapping);

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllEducation", rs);
            }

            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load expert witness education", e);
            l.throwing(getClass().getName(), "loadAllEducation", ae);
            throw ae;
        }
    }

    /**
     * Load all classification of an expert witness.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllClassification(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllClassification", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "entityFK"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Expertwitness.Sel_Class_List", mapping);

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllClassification", rs);
            }

            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load classification.", e);
            l.throwing(getClass().getName(), "loadAllClassification", ae);
            throw ae;
        }
    }

    /**
     * Load all relationship of an expert witness.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllRelationship(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRelationship", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "entityFK"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Expertwitness.Sel_Relation_List", mapping);

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllRelationship", rs);
            }

            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load relationship.", e);
            l.throwing(getClass().getName(), "loadAllRelationship", ae);
            throw ae;
        }
    }

    /**
     * Load all claim of an expert witness.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllClaim(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllClaim", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "entityFK"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Expertwitness.Sel_Claim_List", mapping);

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllClaim", rs);
            }

            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load claim info.", e);
            l.throwing(getClass().getName(), "loadAllClaim", ae);
            throw ae;
        }
    }

    /**
     * Change expert witness status.
     *
     * @param inputRecord
     */
    @Override
    public void changeStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeStatus", new Object[]{inputRecord,});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "pk"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Expertwitness.Change_Status", mapping);

        try {
            spDao.executeUpdate(inputRecord);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to change status.", e);
            l.throwing(getClass().getName(), "changeStatus", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "changeStatus");
    }
}
