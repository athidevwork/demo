package dti.pm.entitymgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the EntityDAO interface. This is consumed by any business logic objects
 * that requires information about one or more entities.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/28/2010       Kenney      Added loadEntityListByName
 * 01/04/2012       wfu         127802 - Modified getEntityName to use common used function in ePM.
 * 03/08/2016       wdang       168418 - Added updateEntityRoleAddress and saveEntityRoleAddress.
 * ---------------------------------------------------
 */

public class EntityJdbcDAO extends BaseDAO implements EntityDAO {

    /**
     * Get type of a given entity
     *
     * @param entityId entity ID
     * @return String containing the default state code
     */
    public String getEntityType(String entityId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityType", new Object[]{entityId});
        }

        // map the values to the input record
        Record inputRecord = new Record();
        inputRecord.setFieldValue("entityId", entityId);

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Sel_Entity_Type");
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Sel_Entity_Type.", e);
            l.throwing(getClass().getName(), "getEntityType", ae);
            throw ae;
        }

        // done
        l.exiting(getClass().getName(), "getEntityType", returnValue);
        return returnValue;
    }

    /**
     * Get name for a given entity ID
     *
     * @param entityId Risk entity ID.
     *                 <p/>
     * @return String a String contains an entity name.
     */
    public String getEntityName(String entityId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityName", new Object[]{entityId});
        }

        // Create the input data mapping
        Record inputRecord = new Record();
        inputRecord.setFieldValue("entId", entityId);

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_SEL_ENTITY_RISK_NAME");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get name for entity "
                + entityId, e);
            l.throwing(getClass().getName(), "getEntityName", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityName", returnValue);
        }
        return returnValue;
    }


    /**
     * Get property name for a given entity ID
     *
     * @param entityId Risk entity ID.
     *                 <p/>
     * @return String a String contains an entity property name.
     */
    public String getEntityPropertyName(String entityId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityPropertyName", new Object[]{entityId});
        }

        // Create the input data mapping
        Record inputRecord = new Record();
        inputRecord.setFieldValue("propId", entityId);

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Property");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get property name for entity "
                + entityId, e);
            l.throwing(getClass().getName(), "getEntityPropertyName", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityName", returnValue);
        }
        return returnValue;
    }

    /**
     * Get entity attributes by given entityId
     *
     * @param entityId
     * @return
     */
    public Record getEntityAttributes(String entityId) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityAttributes", new Object[]{entityId});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("entityId", entityId);
        Record returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_report.get_entity_attributes");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load entity.", e);
            l.throwing(getClass().getName(), "getEntityAttributes", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityAttributes", returnValue);
        }
        return returnValue;
    }

    /**
     * Returns a RecordSet loaded with list of entities for given classifications and an effective date
     *
     * @param inputRecord Record contains input values
     *                    <p/>
     * @return RecordSet a RecordSet loaded with list of entities.
     */
    public RecordSet loadAllEntity(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEntity", new Object[]{inputRecord});
        }

        RecordSet rs;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("roleTypeCode", "entityClassCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "effectiveFromDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Sel_Name_List", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load entities.", e);
            l.throwing(getClass().getName(), "loadAllEntity", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEntity", rs);
        }
        return rs;
    }

    /**
     * get entity role type
     *
     * @param inputRecord
     * @return
     */
   public String getEntityRoleType(Record inputRecord){
         Logger l = LogUtils.enterLog(getClass(), "getEntityRoleType", new Object[]{inputRecord});
        RecordSet rs = null;
        String returnValue;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("value1", "policyType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("value2", "riskType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "transactionDate"));

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Pm_Attr",mapping);
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(
                StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get entity role type", e);
            l.throwing(getClass().getName(), "getEntityRoleType", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityRoleType", rs);
        }
        return returnValue;
    }

    /**
     * Returns a RecordSet loaded with list of entities for given name
     * @param inputRecord
     * @return
     */
    public RecordSet loadEntityListByName(Record inputRecord){
        Logger l = LogUtils.enterLog(getClass(), "loadEntityListByName", new Object[]{inputRecord});

        RecordSet rs = null;

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("ci_web_entity.find_all_entity");
        try {
            rs = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load entity list information", se);
            l.throwing(getClass().getName(), "loadEntityListByName", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityListByName", rs);
        }
        return rs;
    }

    /**
     * Update the entity role's address.
     *
     * @param inputRecord a Record with the updated information.
     */
    public void updateEntityRoleAddress(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "updateEntityRoleAddress", new Object[]{inputRecord});

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "effectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Change_Entity_Role_Address", mapping);
        try {
            spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update entity role's address.", e);
            l.throwing(getClass().getName(), "updateEntityRoleAddress", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "updateEntityRoleAddress");
    }

    /**
     * To save entity role's address.
     *
     * @param inputRecord a Record with address information for saving.
     */
    public void saveEntityRoleAddress(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveEntityRoleAddress", new Object[]{inputRecord});

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "effectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Save_Entity_Role_Address", mapping);
        try {
            spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save entity role's address.", e);
            l.throwing(getClass().getName(), "saveEntityRoleAddress", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveEntityRoleAddress");
    }
}
