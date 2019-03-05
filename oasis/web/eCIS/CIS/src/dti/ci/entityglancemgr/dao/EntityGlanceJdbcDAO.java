package dti.ci.entityglancemgr.dao;


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
 * This class implements the entityGlanceDAO interface.
 * This is consumed by any business logic objects that requires information about entityGlance.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: September 08, 2011
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class EntityGlanceJdbcDAO extends BaseDAO implements EntityGlanceDAO {



    /**
     * Get Entity Demographic from DB
     *
     * @param record
     * @return
     */
    public Record loadEntityDemographic(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityDemographic", new Object[]{record});
        }

        RecordSet rs;
        Record rtnRecord;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Glance.Sel_Entity_Demographic");
            rs = spDao.execute(record);
              if (rs != null && rs.getSize() >= 1) {
                rtnRecord = rs.getFirstRecord();
            } else {

                rtnRecord = new Record();
            }
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_Demographic information", e);
            l.throwing(getClass().getName(), "loadEntityDemographic", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityDemographic", rs);
        }
        return rtnRecord;
    }

    /**
     * Get Entity Relationships from DB
     *
     * @param record
     * @return
     */
    public RecordSet loadRelationships(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadRelationships", new Object[]{record});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("clientId", "entityId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("entityRelationPK", "entityRelationId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_ci_relationship.get_relationship_list",mapping);
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_Relationships information", e);
            l.throwing(getClass().getName(), "loadRelationships", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadRelationships", rs);
        }
        return rs;
    }

    /**
     * Get Entity Policy/Quote from DB
     *
     * @param record
     * @return
     */
    public RecordSet loadPolicyQuote(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyQuote", new Object[]{record});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Policy.Sel_Policy_Quote");
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_PolicyQuote information", e);
            l.throwing(getClass().getName(), "loadPolicyQuote", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyQuote", rs);
        }
        return rs;
    }

    /**
     * Get Policy Transactions from DB
     *
     * @param record
     * @return
     */
    public RecordSet loadTransactions(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTransactions", new Object[]{record});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Sel_Trans_Delta_By_Entity");
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_Transactions information", e);
            l.throwing(getClass().getName(), "loadTransactions", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTransactions", rs);
        }
        return rs;
    }
     /**
     * Get Policy Transaction Forms from DB
     *
     * @param record
     * @return
     */
    public RecordSet loadTransactionForms(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTransactions", new Object[]{record});
        }

        RecordSet rs;
        try {
            record.setFieldValue("sourceTableName","transaction_log");
            DataRecordMapping mapping = new DataRecordMapping();
            //mapping.addFieldMapping(new DataRecordFieldMapping("ufeSourceRecordId", "transactionFormId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CS_WEB_UFE.Sel_Ufe_History_By_Entity",mapping);
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_Transactions information", e);
            l.throwing(getClass().getName(), "loadTransactions", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTransactions", rs);
        }
        return rs;
    }
    /**
     * Get Entity Finances from DB
     *
     * @param record
     * @return
     */
    public RecordSet loadFinances(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadFinances", new Object[]{record});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fm_Web_Account.Sel_All_Account");
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_Finances information", e);
            l.throwing(getClass().getName(), "loadFinances", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadFinances", rs);
        }
        return rs;
    }

    /**
     * Get Entity Finance Invoices from DB
     *
     * @param record
     * @return
     */
    public RecordSet loadInvoices(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadInvoices", new Object[]{record});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(".");
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_Finances_Invoices information", e);
            l.throwing(getClass().getName(), "loadInvoices", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadInvoices", rs);
        }
        return rs;
    }

    /**
     * Get Entity Finance Forms from DB
     *
     * @param record
     * @return
     */
    public RecordSet loadFinanceForms(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadFinanceForms", new Object[]{record});
        }

        RecordSet rs;
        try {
            record.setFieldValue("sourceTableName","billing_account");
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("formId", "fmFormId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("formRequestId", "fmFormRequestId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("formInstanceId", "fmFormInstanceId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("formDescription", "fmFormDescription"));
            mapping.addFieldMapping(new DataRecordFieldMapping("formFormat", "fmFormFormat"));
            mapping.addFieldMapping(new DataRecordFieldMapping("requestStatus", "fmRequestStatus"));
            mapping.addFieldMapping(new DataRecordFieldMapping("requestTime", "fmRequestTime"));
            mapping.addFieldMapping(new DataRecordFieldMapping("instanceVersion", "fmInstanceVersion"));
            mapping.addFieldMapping(new DataRecordFieldMapping("instanceUpdateTime", "fmInstanceUpdateTime"));
            mapping.addFieldMapping(new DataRecordFieldMapping("instanceStatus", "fmInstanceStatus"));
            mapping.addFieldMapping(new DataRecordFieldMapping("filePath", "fmFilePath"));
            mapping.addFieldMapping(new DataRecordFieldMapping("fileName", "fmFileName"));
            mapping.addFieldMapping(new DataRecordFieldMapping("ufeSourceRecordId", "fmUfeSourceRecordId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("ufeSourceTableName", "fmUfeSourceTableName"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CS_WEB_UFE.Sel_Ufe_History_By_Entity",mapping);
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_Finances_Forms information", e);
            l.throwing(getClass().getName(), "loadFinanceForms", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadFinanceForms", rs);
        }
        return rs;
    }

    /**
     * Get Entity Claims from DB
     *
     * @param record
     * @return
     */
    public RecordSet loadClaims(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadClaims", new Object[]{record});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("cm_web_claim.sel_claim_info_by_entity");
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_Claims information", e);
            l.throwing(getClass().getName(), "loadClaims", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadClaims", rs);
        }
        return rs;
    }

    /**
     * Get Entity Participants from DB
     *
     * @param record
     * @return
     */
    public RecordSet loadParticipants(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadParticipants", new Object[]{record});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_cm_participant.load_cm_participant_by_entity");
            rs = spDao.execute(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity_Claims_Participants information", e);
            l.throwing(getClass().getName(), "loadParticipants", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadParticipants", rs);
        }
        return rs;
    }
}
