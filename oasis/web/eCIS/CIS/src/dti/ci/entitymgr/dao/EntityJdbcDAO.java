package dti.ci.entitymgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   April 09, 2012
 *
 * @author ldong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/24/2016       Elvin       Issue 176524: add searchEntityForWS
 * ---------------------------------------------------
 */
public class EntityJdbcDAO extends BaseDAO implements EntityDAO{
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Retrieves all Entity information
     *
     * @param record Record
     * @return RecordSet
     */
    public RecordSet loadEntityList(Record record) {
        String methodName = "loadEntityList";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record});
        }

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity.find_all_entity");
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute " + methodName, e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    /**
     * save new entity
     *
     * @param inputRecord
     * @return Record
     */
    public Record AddEntity(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "AddEntity", inputRecord);
        String methodName = "AddEntity";
        Record outputRecord = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity.insert_entity_for_service");
        try {
            outputRecord = spDao.executeUpdate(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute " + methodName, e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
        l.exiting(getClass().toString(), methodName, outputRecord);
        return outputRecord;
    }

    /**
     * Save entity for service
     *
     * @param record
     */
    public void saveEntityForService(Record record){
        Logger l = LogUtils.enterLog(this.getClass(), "saveEntityForService", record);
        String methodName = "saveEntityForService";
        Record outputRecord = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity.save_entity_service");
        try {
            outputRecord = spDao.executeUpdate(record);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute " + methodName, e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
        l.exiting(getClass().toString(), methodName, outputRecord);

    }

    /**
     * save PartyNote
     *
     * @param inputRecord
     * @return Record
     */
    public Record savePartyNote(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "savePartyNote";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        Record recResult = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("cs_rte_note.insert_update_note_from_svc");
        try {
            recResult = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save PartyNote : " + inputRecord, se);
            l.throwing(getClass().getName(), "savePartyNote", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName);
        }
        return recResult;
    }

    @Override
    public RecordSet searchEntityForWS(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "searchEntityForWS";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        RecordSet rs = new RecordSet();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity.search_entity_for_ws");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to search entity : " + inputRecord, se);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName);
        }
        return rs;
    }

    /**
     * load entity data
     * @param inputRecord
     * @return
     */
    @Override
    public Record loadEntityData(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityData", new Object[]{inputRecord,});
        }
        DataRecordMapping mapping = new DataRecordMapping();
        //since below 2 web fields have many usage in other pages,
        //we use field mapping, instead of rename their web fieldId in workbench
        mapping.addFieldMapping(new DataRecordFieldMapping("clientId", "clientID"));
        mapping.addFieldMapping(new DataRecordFieldMapping("federalTaxId", "federalTaxID"));
        mapping.addFieldMapping(new DataRecordFieldMapping("defaultTaxId", "defaultTaxID"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity_modify.load_entity_data",mapping);
        Record record = null;
        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (rs.getSize() > 0 ) {
                record = rs.getFirstRecord();
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadEntityData", record);
            }
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load entity data.", e);
            l.throwing(getClass().getName(), "loadEntityData", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityData",record);
        }
        return record;
    }

    @Override
    public Record saveEntity(Record inputRecord) {
        String methodName = "saveEntity";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entClsEffectiveFromDate", "entityClassEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entClsEffectiveToDate", "entityClassEffectiveToDate"));

        Record record = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity.create_new_entity", mapping);
            record = spDao.executeUpdate(inputRecord);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleSQLException("Failed to save entity.", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, record);
        }
        return record;
    }

    /**
     * load name history
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadNameHistory(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadNameHistory", new Object[]{inputRecord,});
        }

        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity_modify.load_name_history");
        try {
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "Failed to get entity name history.", e);
            l.throwing(getClass().getName(), "loadNameHistory", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadNameHistory", rs);
        }
        return rs;
    }

    /**
     * load tax history
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadTaxHistory(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTaxHistory", new Object[]{inputRecord,});
        }

        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity_modify.load_tax_history");
        try {
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "Failed to get entity tax history.", e);
            l.throwing(getClass().getName(), "loadTaxHistory", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTaxHistory", rs);
        }
        return rs;
    }

    /**
     * load Loss data history
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadLossHistory(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadLossHistory", new Object[]{inputRecord});
        }
        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity_modify.load_loss_history");
        try {
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "Failed to get entity loss history.", e);
            l.throwing(getClass().getName(), "loadLossHistory", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadLossHistory", rs);
        }
        return rs;
    }

    /**
     * load DBA data
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadDbaHistory(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadDbaHistory", new Object[]{inputRecord});
        }

        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity_modify.load_dba_history");
        try {
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "Failed to get entity dba history.", e);
            l.throwing(getClass().getName(), "loadDbaHistory", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadDbaHistory", rs);
        }
        return rs;
    }

    /**
     * load electronic data
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadEtdHistory(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEtdHistory", new Object[]{inputRecord});
        }
        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity_modify.load_etd_history");
        try {
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "Failed to get entity dba history.", e);
            l.throwing(getClass().getName(), "loadEtdHistory", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEtdHistory", rs);
        }
        return rs;
    }

    /**
     * save Entity data
     * @param inputRecord
     */
    @Override
    public Record saveEntityData(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityData", new Object[]{inputRecord});
        }
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity_modify.save_entity_data");
        Record retRecord = null;
        try {
            retRecord = spDao.execute(inputRecord).getSummaryRecord();
        } catch (SQLException e) {
            throw new AppException("ci.generic.error",
                    "fail to save data", new String[]{StringUtils.formatDBErrorForHtml(e.getMessage())});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEntityData", retRecord);
        }
        return retRecord;
    }

    /**
     * change Entity Type
     * @param inputRecord
     */
    @Override
    public Record changeEntityType(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeEntityType", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_entity_modify.change_entity_type");

        Record record = null;
        try {
            record = spDao.executeUpdate(inputRecord);
        } catch (SQLException e) {
            throw new AppException("ci.generic.error",
                    "fail to save data", new String[]{StringUtils.formatDBErrorForHtml(e.getMessage())});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "changeEntityType", record);
        }
        return record;
    }

    /**
     * Check if policy number entered in Reference Number field is valid or not
     * note: this is dummy function for base version and always return True
     * @param record
     * @return
     */
    @Override
    public boolean checkPolNo(Record record, DataRecordMapping mapping) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkPolNo", new Object[]{record});
        }

        RecordSet rs = null;
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Check_Pol_No", mapping);
        try {
            rs = sp.execute(record);
        }
        catch (SQLException se) {
            l.warning(getClass().getName() + ".checkPolNo:" + se.getMessage());
            throw new AppException("can not validate reference number for entity");
        }
        String returnValueStr = rs.getSummaryRecord().getStringValue(sp.RETURN_VALUE_FIELD);
        boolean returnValue = YesNoFlag.getInstance(returnValueStr).booleanValue();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "checkPolNo", returnValue);
        }
        return returnValue;
    }

    /**
     * check if the entered policy number in Reference number field is duplicated or not
     * @param record
     * @return
     */
    @Override
    public boolean checkPolNoIsDuplicated(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "checkPolNoIsDuplicated", record);
        }

        RecordSet rs = null;
        DataRecordMapping mapping = new DataRecordMapping();
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Check_Dup_Pol_No", mapping);
        try {
            rs = sp.execute(record);
        }
        catch (SQLException se) {
            l.warning(getClass().getName() + ".checkDupPolNo:" + se.getMessage());
            throw new AppException("can not validate reference number for entity");
        }
        String returnStr = rs.getSummaryRecord().getStringValue(sp.RETURN_VALUE_FIELD);
        boolean returnValue = YesNoFlag.getInstance(returnStr).booleanValue();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "checkPolNoIsDuplicated", returnValue);
        }
        return returnValue;
    }

    /**
     * check if the client has active policy associated
     * @param record
     * @return
     */
    @Override
    public boolean getClientDiscardPolCheck(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getClientDiscardPolCheck", new Object[]{record});
        }

        RecordSet rs = null;
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("ci_web_entity_modify.client_discard_pol_check");
        try {
            rs = sp.execute(record);
        }
        catch (SQLException se) {
            l.throwing(getClass().getName(), "getClientDiscardPolCheck", se);
            throw new AppException("can not get Client Discard Policy check flag");
        }
        String returnValue = rs.getSummaryRecord().getStringValue(sp.RETURN_VALUE_FIELD);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getClientDiscardPolCheck", returnValue);
        }
        return (returnValue.equalsIgnoreCase("Y")? true:false);
    }

    /**
     * return if has Exp Wit classification
     * @param record
     * @return
     */
    @Override
    public boolean getClientHasExpertWitnessClass(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getClientHasExpertWitnessClass", new Object[]{record});
        }

        RecordSet rs = null;
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("ci_web_entity_modify.has_expert_witness_class");
        try {
            rs = sp.execute(record);
        } catch (SQLException se) {
            l.throwing(getClass().getName(), "getClientHasExpertWitnessClass", se);
            throw new AppException("can not get Expert Witness classification");
        }

        String returnStr = rs.getSummaryRecord().getStringValue(sp.RETURN_VALUE_FIELD);
        boolean returnValue = YesNoFlag.getInstance(returnStr).booleanValue();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getClientHasExpertWitnessClass", returnValue);
        }
        return returnValue;
    }

    /**
     * Check if an entity has tax ID info.
     *
     * @param inputRecord
     * @return Returns {@code true} if either "Tax ID" or "SSN" exists. Otherwise, returns {@code false};
     */
    @Override
    public boolean hasTaxIdInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasTaxIdInfo", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("ci_web_entity.has_tax_info");

        try {
            Record summaryRecord = sp.executeReadonly(inputRecord).getSummaryRecord();

            boolean hasTaxInfo = YesNoFlag.getInstance(summaryRecord.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD)).booleanValue();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "hasTaxIdInfo", hasTaxInfo);
            }
            return hasTaxInfo;
        } catch (SQLException se) {
            AppException ae = handleSQLException(se, "ci.generic.error");
            l.throwing(getClass().getName(), "hasTaxIdInfo", ae);
            throw ae;
        }
    }

    /**
     * Get entity type.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public String getEntityType(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityType", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("wb_client_utility.get_entity_type");

        try {
            Record summaryRecord = sp.executeReadonly(inputRecord).getSummaryRecord();

            String entityType = summaryRecord.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "hasTaxIdInfo", entityType);
            }
            return entityType;
        } catch (SQLException se) {
            AppException ae = handleSQLException(se, "ci.generic.error");
            l.throwing(getClass().getName(), "hasTaxIdInfo", ae);
            throw ae;
        }
    }

    @Override
    public String getEntityName(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityName", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO sp = StoredProcedureDAO.getInstance("cs_ci_get_name");
            RecordSet rs = sp.execute(inputRecord);
            String entityName = rs.getSummaryRecord().getStringValue("name");

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getEntityName", entityName);
            }
            return entityName;
        } catch (SQLException se) {
            AppException ae = handleSQLException(se, "ci.generic.error");
            l.throwing(getClass().getName(), "getEntityName", se);
            throw ae;
        }
    }
}


