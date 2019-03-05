package dti.pm.policymgr.applicationmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
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
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 17, 2009
 *
 * @author gchitta
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/04/2012       bhong       129528 - Added processQuestionnaireRequest
 * 01/09/2013       adeng       139761 - Modified loadAllApplication to pass additional parameters: First name &
 *                              Policy(Quote) Number. It can make search function more helpful.
 * 03/28/2017       tzeng       166929 - Added hasApplicationB, recordDiaryForApplication.
 * ---------------------------------------------------
 */
public class ApplicationJdbcDAO extends BaseDAO implements ApplicationDAO {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Search the questionnaire(s) for the given effective period and questionnaire type.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTerms(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTerms");
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("subsystemCode", "PMS"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Policy_Terms", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get policy terms for the policy.", e);
            l.throwing(getClass().getName(), "loadAllTerms", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTerms", rs);
        }
        return rs;
    }

    /**
     * Get the list of applications for a given policy term.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadApplicationList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTerms");
        }
        RecordSet rs;

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("policyTermHistoryId", "termDesc"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Application_List", mapping);
            rs = spDao.execute(inputRecord);
        }

        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get application list for the policy term selected.", e);
            l.throwing(getClass().getName(), "loadApplicationList", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadApplicationList", rs);
        }
        return rs;
    }

    /**
     * Porcess and generate questionnaire
     *
     * @param inputRecords
     */
    public void processQuestionnaireRequest(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processQuestionnaireRequest", new Object[]{inputRecords});
        }

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "policyTermHistoryId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Application.Process_Questionnaire_Request", mapping);
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to process questionnaire request.", e);
            l.throwing(getClass().getName(), "processQuestionnaireRequest", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processQuestionnaireRequest");
        }
    }

    /**
     * Load all applications
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllApplication(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllApplication", new Object[]{inputRecord});
        }

        RecordSet rs;

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("assigneeId", "assigneeCriteria"));
            mapping.addFieldMapping(new DataRecordFieldMapping("issueCompanyId", "issueCompanyCriteria"));
            mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCriteria"));
            mapping.addFieldMapping(new DataRecordFieldMapping("applicantId", "preparerIdCriteria"));
            mapping.addFieldMapping(new DataRecordFieldMapping("applicantEmail", "applicantEmailCriteria"));
            mapping.addFieldMapping(new DataRecordFieldMapping("appStatus", "statusCriteria"));
            mapping.addFieldMapping(new DataRecordFieldMapping("appStatusDate", "statusDateCriteria"));
            mapping.addFieldMapping(new DataRecordFieldMapping("firstName", "firstNameCriteria"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Application.Sel_App_List", mapping);
            rs = spDao.execute(inputRecord, loadProcessor);
        }

        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get application list.", e);
            l.throwing(getClass().getName(), "loadAllApplication", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllApplication", rs);
        }

        return rs;
    }

    /**
     * Save all applications
     *
     * @param inputRecords
     */
    public void saveAllApplication(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllApplication", new Object[]{inputRecords,});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Application.Update_App");
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save application data.", e);
            l.throwing(getClass().getName(), "saveAllApplication", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllApplication");
        }
    }

    /**
     * Save change history
     *
     * @param inputRecord
     */
    public void saveHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveHistory", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Application.Save_History");
            spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save change history.", e);
            l.throwing(getClass().getName(), "saveHistory", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveHistory");
        }
    }

    /**
     * Load all change history
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllHistory", new Object[]{inputRecord});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Application.Select_History");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to retrieve change history.", e);
            l.throwing(getClass().getName(), "loadAllHistory", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllHistory", rs);
        }
        return rs;
    }

    /**
     * To load all eApp reviewers
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAppReviewer(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAppReviewer", new Object[]{inputRecord});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Application.Sel_All_App_Reviewer");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load all app reviewers.", e);
            l.throwing(getClass().getName(), "loadAllAppReviewer", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAppReviewer", rs);
        }
        return rs;
    }

    @Override
    public boolean hasApplicationB(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasApplicationB", inputRecord);
        }

        boolean returnValue = true;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termBaseRecordId", "policyTermNumberId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Has_Application_B", mapping);
            returnValue = YesNoFlag.getInstance(spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD)).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check if exists application upon term base and form type", e);
            l.throwing(getClass().getName(), "hasApplicationB", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasApplicationB", returnValue);
        }
        return returnValue;
    }

    @Override
    public void recordDiaryForApplication(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "recordDiaryForApplication", inputRecord);
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Record_Diary_For_Application");
            spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to record diary for initializing application", e);
            l.throwing(getClass().getName(), "recordDiaryForApplication", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "recordDiaryForApplication");
        }
    }

}
