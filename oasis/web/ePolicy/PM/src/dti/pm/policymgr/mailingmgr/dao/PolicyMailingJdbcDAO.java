package dti.pm.policymgr.mailingmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the PolicyMailingDAO interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 10, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/02/2008       sxm         Issue 86930 - add loadAllMailingGenerationError
 * 10/06/2008       sxm         Issue 86930 - merge generateMailingEvent and loadAllMailingGenerationError
 *                              since the generation errors are stored in global temp table
 * ---------------------------------------------------
 */

public class PolicyMailingJdbcDAO extends BaseDAO implements PolicyMailingDAO {


    /**
     * load all mailing event
     *
     * @param inputRecord
     * @param loadProcessor
     * @return shared group record set
     */
    public RecordSet loadAllMailingEvent(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMailingEvent", new Object[]{inputRecord});
        RecordSet rs = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Sel_Mailing_Event_Info");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load mailing event information ", e);
            l.throwing(getClass().getName(), "loadAllMailingEvent", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMailingEvent", rs);
        }
        return rs;
    }

    /**
     * load all mailing attribute
     *
     * @param inputRecord
     * @param loadProcessor
     * @return shared group detail record set
     */
    public RecordSet loadAllMailingAttribute(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMailingAttribute", new Object[]{inputRecord});
        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Sel_Mailing_Attribute_Info");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load mailing attribute information", e);
            l.throwing(getClass().getName(), "loadAllMailingAttribute", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMailingAttribute", rs);
        }
        return rs;
    }

    /**
     * load all mailing recipient
     *
     * @param inputRecord
     * @return shared group detail record set
     */
    public RecordSet loadAllMailingRecipient(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMailingRecipient", new Object[]{inputRecord});
        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Sel_Mailing_Recipient_Info");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load mailing recipient information", e);
            l.throwing(getClass().getName(), "loadAllMailingRecipient", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMailingRecipient", rs);
        }
        return rs;
    }

    /**
     * load all past mailing
     *
     * @param inputRecord
     * @return RecordSet past mailing policies
     */
    public RecordSet loadAllPastMailing(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPastMailing", new Object[]{inputRecord});
        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Sel_Past_Mailing_Info");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load past mailing information", e);
            l.throwing(getClass().getName(), "loadAllPastMailing", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPastMailing", rs);
        }
        return rs;
    }

    /**
     * save all mailing attribute
     *
     * @param inputRecords
     * @return updateCount
     */
    public int saveAllMailingAttribute(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllMailingAttribute", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Save_Mailing_Attribute_Info");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save mailing attribute.", e);
            l.throwing(getClass().getName(), "saveAllSharedDetail", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "saveAllMailingAttribute", new Integer(updateCount));
        return updateCount;
    }

    /**
     * delete all mailing event
     *
     * @param inputRecords
     * @return updateCount
     */
    public int deleteAllMailingEvent(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllMailingEvent", new Object[]{inputRecords});

        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Delete_Mailing_Event_Info");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete mailing event", e);
            l.throwing(getClass().getName(), "deleteAllSharedGroup", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "deleteAllSharedGroup", new Integer(updateCount));
        return updateCount;
    }

    /**
     * delete all mailing attribute
     *
     * @param inputRecords
     * @return updateCount
     */
    public int deleteAllMailingAttribute(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllMailingAttribute", new Object[]{inputRecords});

        int updateCount = 0;
        // Create a DataRecordMapping for this stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Delete_Mailing_Attribute_Info");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete mailing attribute.", e);
            l.throwing(getClass().getName(), "deleteAllMailingAttribute", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "deleteAllMailingAttribute", new Integer(updateCount));
        return updateCount;
    }

    /**
     * delete all mailing attribute
     *
     * @param inputRecords
     * @return updateCount
     */
    public int deleteAllMailingRecipient(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllMailingRecipient", new Object[]{inputRecords});

        int updateCount = 0;
        // Create a DataRecordMapping for this stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Delete_Mailing_Recipient_Info");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete mailing recipient.", e);
            l.throwing(getClass().getName(), "deleteAllMailingRecipient", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "deleteAllMailingRecipient", new Integer(updateCount));
        return updateCount;
    }

    /**
     * save all mailing event
     *
     * @param inputRecords
     * @return updateCount
     */
    public int saveAllMailingEvent(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSharedGroup", new Object[]{inputRecords});

        int updateCount = 0;
        // Create a DataRecordMapping for this stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Save_Mailing_Event_Info");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save mailing event.", e);
            l.throwing(getClass().getName(), "saveAllMailingEvent", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "saveAllMailingEvent", new Integer(updateCount));
        return updateCount;

    }

    /**
     * save all mailing recipient
     *
     * @param inputRecords
     * @return updateCount
     */
    public int saveAllMailingRecipient(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllMailingRecipient", new Object[]{inputRecords});

        int updateCount = 0;
        // Create a DataRecordMapping for this stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Save_Mailing_Recipient_Info");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save mailing recipient.", e);
            l.throwing(getClass().getName(), "saveAllMailingRecipient", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "saveAllMailingRecipient", new Integer(updateCount));
        return updateCount;

    }


    /**
     * validate recipient
     *
     * @param inputRecord(policyNo)
     * @return recordSet(only1or0record)
     */
    public RecordSet validateMailingRecipient(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateMailingRecipient", new Object[]{inputRecord});
        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Validate_Mailing_Recipient");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate mailing recipient", e);
            l.throwing(getClass().getName(), "validateMailingRecipient", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateMailingRecipient", rs);
        }
        return rs;
    }

    /**
     * get resend days by selected resend
     *
     * @param inputRecord(policyNo)
     * @return String
     */
    public String getResendDaysBySelectedResend(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getResendDaysBySelectedResend", new Object[]{inputRecord});
        RecordSet rs = null;
        String returnValue;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Get_Resend_Days");
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(
                StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get resend days", e);
            l.throwing(getClass().getName(), "getResendDaysBySelectedResend", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getResendDaysBySelectedResend", rs);
        }
        return returnValue;
    }

    /**
     * check past mailing exist or not
     *
     * @param inputRecord
     * @return count(ifcountvalue>0,exist)
     */
    public int checkPastMailing(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "checkPastMailing", new Object[]{inputRecord});
        RecordSet rs = null;
        int returnValue;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Check_Past_Mailing");
            returnValue = Integer.parseInt(spDao.execute(inputRecord).getSummaryRecord().getStringValue(
                StoredProcedureDAO.RETURN_VALUE_FIELD));
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check past mailing", e);
            l.throwing(getClass().getName(), "checkPastMailing", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "checkPastMailing", rs);
        }
        return returnValue;
    }

    /**
     * generate mailing event
     *
     * @param inputRecord (policyMailingId)
     * @return RecordSet   a record set loaded with list of mailing generation errors
     */
    public RecordSet generateMailingEvent(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "generateMailingEvent", new Object[]{inputRecord});

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mailing.Generate");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to generate policy mailing", e);
            l.throwing(getClass().getName(), "generateMailingEvent", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateMailingEvent", rs);
        }
        return rs;
    }

    /**
     * reprint mailing event
     *
     * @param inputRecord
     * @return
     */
    public Record reprintMailingEvent(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "reprintMailingEvent", new Object[]{inputRecord});
        Record output = new Record();
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Mailing.Reprint");
            output = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to reprint mailing event", e);
            l.throwing(getClass().getName(), "reprintMailingEvent", ae);
            throw ae;
        }
        return output;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public PolicyMailingJdbcDAO() {
    }
}
