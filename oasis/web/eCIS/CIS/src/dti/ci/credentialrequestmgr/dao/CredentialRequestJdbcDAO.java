package dti.ci.credentialrequestmgr.dao;

import dti.ci.core.error.ExpMsgConvertor;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DAO component of Credential Request.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  03/04/2016
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CredentialRequestJdbcDAO implements CredentialRequestDAO {
    /**
     * load entity detail
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadDetail(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDetail", new Object[]{inputRecord});

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Cred_Req.load_cred_dtl");
        RecordSet rs = null;
        try {
            rs = sp.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());
        }
        catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "loadDetail", e);
        }

        l.exiting(getClass().toString(), "loadDetail", rs);
        return rs;
    }

    /**
     * load entity detail
     *
     * @param inputRecord
     * @return Record
     */
    public Record loadEntity(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDetail", new Object[]{inputRecord});

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Cred_Req.get_detail_entity_info");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "loadDetail", e);
        }

        Record outputRecord = new Record();
        if (outRecordSet != null) {
            outputRecord = outRecordSet.getSummaryRecord();
        }

        l.exiting(getClass().toString(), "loadDetail", outputRecord);
        return outputRecord;
    }

    /**
     * Load Service Charge accounts for the entity
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllAccount(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAccount", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_Web_Cred_Req.Sel_cred_Accounts");
        try {
            rs = spDAO.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "loadAllAccount", e);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAccount", rs);
        }

        return rs;
    }

    /**
     * Save credential request.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveRequest(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveRequest", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_Web_Cred_Req.Save_Cred_Req");
        Record record = null;
        try {
            record = spDAO.executeUpdate(inputRecord);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveRequest", record);
            }
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "saveRequest", e);
        }
        return record;
    }

    /**
     * Save credential request detail.
     *
     * @param inputRecords
     * @return int
     */
    public int saveAllRequestDetail(RecordSet inputRecords){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRequestDetail", new Object[]{inputRecords});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_Web_Cred_Req.Save_Cred_Dtl");
        int count = 0;
        try {
            count = spDAO.executeBatch(inputRecords);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveAllRequestDetail", count);
            }
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "saveAllRequestDetail", e);
        }
        return count;
    }

    /**
     * Request New Account from FM.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveAccount(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAccount", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_Web_Cred_Req.Create_Account");
        Record record = null;
        try {
            record = spDAO.executeUpdate(inputRecord);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveAccount", record);
            }
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "saveAccount", e);
        }
        return record;
    }

    /**
     * Request New Account from FM.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveProcessRequest(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveProcessRequest", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_Web_Cred_Req.process_cred_req");
        Record record = null;
        try {
            record = spDAO.executeUpdate(inputRecord);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveProcessRequest", record);
            }
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "saveProcessRequest", e);
        }
        return record;
    }

    /**
     * Generate the report XML.
     * @param inputRecord
     * @return String
     */
    public String exportData(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "exportData", new Object[]{inputRecord});

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Cred_Req.export_data");
        RecordSet outRecordSet = null;
        String xmlData = "";
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "exportData", e);
        }

        Record rec = new Record();
        if (outRecordSet != null) {
            rec = outRecordSet.getSummaryRecord();
            xmlData = rec.getStringValue("xmlData");
        }

        l.exiting(getClass().toString(), "exportData", xmlData);
        return xmlData;
    }

    Logger l = LogUtils.getLogger(getClass());
}
