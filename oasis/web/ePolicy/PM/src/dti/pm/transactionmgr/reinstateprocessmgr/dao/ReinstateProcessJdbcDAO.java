package dti.pm.transactionmgr.reinstateprocessmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the ReinstateProcessDAO interface. This is get some informations by any business logic objects
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 20, 2007
 *
 * @author Jerry
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/30/2007       Bhong       clean up codes/fix issues
 * 08/09/2012       xnie        136384 Modified performReinstate() to set cancellation transactionLogId
 *                              for non-policy reinstate.
 * 08/13/2012       xnie        136384 Roll backed incorrect fix.
 * ---------------------------------------------------
 */
public class ReinstateProcessJdbcDAO extends BaseDAO implements ReinstateProcessDAO {

    /**
     * check term if policy have multiple term
     *
     * @param inputRecord intput record
     * @return String value to identifying Term
     */
    public String identifyingTerm(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "identifyingTerm", new Object[]{inputRecord});
        }

        String policyPrompt;

        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelTransId", "transactionLogId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(
            "Pm_Web_Transaction.Is_Trans_Initiated_In_Term", mapping);
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            policyPrompt = outputRecordSet.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (
            SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "identifyingTerm", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "identifyingTerm", policyPrompt);
        }
        return policyPrompt;
    }

    /**
     * to check if the Policy multiple reinstatements
     *
     * @param inputRecord intput record
     * @return String value to Policy prompt
     */
    public String isPolicyprompt(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPolicyprompt", new Object[]{inputRecord});
        }

        // Create DataRecordMappig for this stored procedure
        inputRecord.setFieldValue("tranCode", "REINSTATE");
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transEffective", "itemToDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Data.Pm_reinstate_fees", mapping);
        String policyprompt;
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            Record customReinstateStatusRecordSet = outputRecordSet.getSummaryRecord();
            policyprompt = customReinstateStatusRecordSet.getStringValue("RETURN");
        }
        catch (
            SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "isPolicyprompt", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPolicyprompt", policyprompt);
        }
        return policyprompt;
    }

    /**
     * to check policy/risk/coverage/coverage class Active information.
     *
     * @param inputRecord intput record
     * @return String value to get Active Reinstate
     */
    public String validateActiveReinstate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateActiveReinstate", new Object[]{inputRecord});
        }

        inputRecord.setFieldValue("transactionLogId", "0");
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "itemToDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Check_Act_Tail", mapping);
        String activeReinstate;
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            Record getActiveReinstate = outputRecordSet.getSummaryRecord();
            activeReinstate = getActiveReinstate.getStringValue("activetail");
        }
        catch (
            SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "validateActiveReinstate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateActiveReinstate", activeReinstate);
        }
        return activeReinstate;
    }

    /**
     * to check policy/risk/coverage/coverage class Solo Owner information.
     *
     * @param inputRecord intput record
     * @return String value to get Solo Owner Reinstate
     */
    public Long validateSoloOwnerReinstate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSoloOwnerReinstate", new Object[]{inputRecord});
        }

        inputRecord.setFieldValue("inputStr", "RINS_ENTITY");
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("tranEffDate", "itemToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "baseId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Is_Solo_Owner", mapping);
        Long soloReinstateStatus;
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            soloReinstateStatus = outputRecordSet.getSummaryRecord().getLongValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (
            SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "validateSoloOwnerReinstate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSoloOwnerReinstate", soloReinstateStatus);
        }
        return soloReinstateStatus;
    }

    /**
     * to check policy/risk/coverage/coverage class custom information.
     *
     * @param inputRecord intput record
     * @return Record
     */
    public Record validateCustomReinstate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCustomReinstate", new Object[]{inputRecord});
        }

        Record rc;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "itemToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termbaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("level", "reinstateLevel"));
        mapping.addFieldMapping(new DataRecordFieldMapping("id", "selectedId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Reinstate.Val_Reinstate", mapping);
        try {
            rc = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (
            SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "validateCustomReinstate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCustomReinstate", rc);
        }
        return rc;
    }

    /**
     * to perform Reinstate
     *
     * @param inputRecord intput record
     * @return Record get perform information
     */
    public Record performReinstate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performReinstate", new Object[]{inputRecord});
        }

        Record rc;
        //set fields' value
        inputRecord.setFieldValue("cxlTran", null);
        inputRecord.setFieldValue("batchModeyn", "Y");
        inputRecord.setFieldValue("valiDateB", "N");
        inputRecord.setFieldValue("saveOfficialB", "N");
        inputRecord.setFieldValue("processFeeB", "N");
        inputRecord.setFieldValue("parms", null);

        // field mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDt", "itemToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("level", "reinstateLevel"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(
            "Pm_Process_Transaction.Process_Reinstate", mapping);
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            rc = outputRecordSet.getSummaryRecord();
        }
        catch (
            SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "performReinstate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performReinstate", rc);
        }
        return rc;
    }

    /**
     * to perform reinstate for risk relationship
     *
     * @param inputRecord
     * @return
     */
    public Record performRiskRelationReinstate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performRiskRelationReinstate", new Object[]{inputRecord});
        }

        Record rc;
        //set fields' value
        inputRecord.setFieldValue("batchModeYn", "N");
        inputRecord.setFieldValue("parms", "save_as_official^N^");

        // field mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskRelId", "riskRelationId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termFrom", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termTo", "termEffectiveToDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(
            "Pm_Process_Transaction.Reinstate_Risk_Relation", mapping);
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            rc = outputRecordSet.getSummaryRecord();
        }
        catch (SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "performRiskRelationReinstate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performRiskRelationReinstate", rc);
        }
        return rc;
    }
}
