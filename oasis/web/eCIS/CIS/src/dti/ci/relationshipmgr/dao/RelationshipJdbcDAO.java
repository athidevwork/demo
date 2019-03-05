package dti.ci.relationshipmgr.dao;

import dti.ci.core.CIFields;
import dti.ci.core.dao.BaseDAO;
import dti.ci.helpers.data.CIBaseDAO;
import dti.ci.relationshipmgr.RelationshipFields;
import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.*;
import dti.oasis.messagemgr.MessageManager;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for getting data about relationships.
 * <p/>
 * <p>(C) 2005 Delphi Technology, inc. (dti)</p>
 * Date: Oct 10, 2005
 *
 * @author Hong Yuan
 */
/*
* Revision Date    Revised By  Description
* --------------------------------------------------------------------
* 03/24/2009       Jacky       Modified for issue #89413
* 11/11/2009       kenney      Modified for issue 96605
* 03/23/2010       Kenney      Modified getRelationshipDataMapSQL to take advantage of phone number format enh
* 03/25/2010       kshen       101585.
* 09/23/2010       kshen       Added method expire.
* 02/15/2011       Michael     Changed for 112658.
* 05/07/2012       kshen       Isuse 131290. Added method getRelationshipDesc.
* 05/30/2016       dpang       Issue 149588: retrieve hub data if needed.
* 11/09/2018       Elvin       Issue 195835: grid replacement
* 12/11/2018       Elvin       Issue 195835: remove field mapping for saveRelationshipWs
* --------------------------------------------------------------------
*/

public class RelationshipJdbcDAO extends BaseDAO implements RelationshipDAO {
    /**
     * Load all relationship of an entity.
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
        boolean isHubEnabled = isHubEnabled();
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(isHubEnabled ? "entityId" : "clientId", CIFields.PK));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(isHubEnabled ? "Ci_Web_Entity_Relation_H.get_relationship_list" : "wb_ci_relationship.get_relationship_list", mapping);

        try {
            RecordSet rs = spDao.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllRelationship", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load relationship", e);
            l.throwing(getClass().getName(), "loadAllRelationship", ae);
            throw ae;
        }
    }

    /**
     * Get relationship list filter pref.
     *
     * @param record
     * @return
     */
    @Override
    public String getRelationshipListFilterPref(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRelationshipListFilterPref", new Object[]{record});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_relationship.get_user_preference");
        try {
            String filterPref = spDao.execute(record).getSummaryRecord()
                    .getStringValue("prefValue", "");

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getRelationshipListFilterPref", filterPref);
            }
            return filterPref;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "Unable to get relationship filter pref", e);
            l.throwing(getClass().getName(), "getRelationshipListFilterPref", ae);
            throw ae;
        }
    }

    /**
     * Expire the relationships with a expire date.
     *
     * @param inputRecordSet
     */
    @Override
    public void expireRelationShips(RecordSet inputRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "expireRelationShips", new Object[]{inputRecordSet});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_relationship.expire_relationship");
        try {
            spDao.executeBatch(inputRecordSet);

            l.exiting(getClass().getName(), "expireRelationShips");
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to execute relationships.", e);
            l.throwing(getClass().getName(), "expireRelationShips", ae);
            throw ae;
        }
    }

    /**
     * Load relationship data.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record loadRelationship(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadRelationship", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_relationship.sel_relationship");

        try {
            Record record = spDao.execute(inputRecord).getFirstRecord();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadRelationship", record);
            }

            return record;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load relationship.", e);
            l.throwing(getClass().getName(), "loadRelationship", ae);
            throw ae;
        }
    }

    /**
     * Get initial values for adding relationship.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record getInitInfoForRelationship(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitInfoForRelationship", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_relationship.get_init_info_for_relationship");

        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getInitInfoForRelationship", record);
            }

            return record;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get init info for relationship", e);
            l.throwing(getClass().getName(), "getInitInfoForRelationship", ae);
            throw ae;
        }
    }

    /**
     * Save a relationship record.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record saveRelationship(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveRelationship", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "pk"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_relationship.save_relationship", mapping);

        try {
            Record result = spDao.executeUpdate(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveRelationship", result);
            }
            return result;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save relationship.", e);
            l.throwing(getClass().getName(), "saveRelationship", ae);
            throw ae;
        }
    }

    /**
     * Save a relationship record for web service.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record saveRelationshipWs(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveRelationship", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_relationship.save_relationship_ws");

        try {
            Record result = spDao.executeUpdate(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveRelationship", result);
            }

            return result;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save relationship.", e);
            l.throwing(getClass().getName(), "saveRelationship", ae);
            throw ae;
        }
    }

    /**
     * Validate all validateOscRelationshipCode from DB
     *
     * @param record
     * @return
     */
    public String validateOscRelationshipCode(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateOscRelationshipCode", new Object[]{record});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("WB_CI_RELATIONSHIP.val_osc_relationship_code");
            String messageKey = spDao.execute(record).getSummaryRecord().getStringValue("msgKey", "");

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "validateOscRelationshipCode", messageKey);
            }

            return messageKey;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to Validations for 'Other Subscriber Contacts' Relationship Code", e);
            l.throwing(getClass().getName(), "validateOscRelationshipCode", ae);
            throw ae;
        }
    }

    /**
     * Get all EntityRelationship from DB
     *
     * @param record
     * @return
     */
    @Override
    public RecordSet loadAllAvailableEntityRelationships(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableEntityRelationships", new Object[]{record});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("clientId", "pk"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_relationship.get_relationship_list",mapping);
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity Relationship information", e);
            l.throwing(getClass().getName(), "loadAllAvailableEntityRelationships", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableEntityRelationships", rs);
        }
        return rs;
    }

    /**
     * Get relationship desc.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public String getRelationshipDesc(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRelationshipDesc", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Addl_Info.Get_Relation_Info");

        try {
            String relationshipDesc = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getRelationshipDesc", relationshipDesc);
            }

            return  relationshipDesc;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get relationship desc.", e);
            l.throwing(getClass().getName(), "getRelationshipDesc", ae);
            throw ae;
        }
    }
}
