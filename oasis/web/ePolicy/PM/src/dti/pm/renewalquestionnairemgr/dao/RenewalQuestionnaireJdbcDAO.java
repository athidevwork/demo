package dti.pm.renewalquestionnairemgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of RenewalQuestionnaireDAO Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 14, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/15/2010       wfu         issue 115513 - Removed set Risk ID as NULL for function getWebHeader
 * 01/11/2011       ryzhao      issue 116543 - Replace ' with '' in policyType
 * 01/12/2011       ryzhao      issue 116543 - Move the logics to RenewalQuestionnaireManagerImpl.
 * 06/08/2016       fcb         issue 177372 - Changed int to long
 * ---------------------------------------------------
 */
public class RenewalQuestionnaireJdbcDAO extends BaseDAO implements RenewalQuestionnaireDAO {

    /**
     * Search the questionnaire(s) for the given effective period and questionnaire type.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllRenewalQuestionnaire(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRenewalQuestionnaire");
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("fromDate", "startSearchDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("toDate", "endSearchDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("questType", "questionnaireType"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Sel_Renew_Questionnaire", mapping);
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get questionnaire for the given effective date and type.", e);
            l.throwing(getClass().getName(), "loadAllRenewalQuestionnaire", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRenewalQuestionnaire", rs);
        }
        return rs;
    }

    /**
     * Select the renewal questionnaire nearest cycle date
     *
     * @return String
     */
    public String getRenewalQuestionNearestCycleDate() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRenewalQuestionNearestCycleDate");
        }
        RecordSet rs;
        try {
            Record input = new Record();
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Sel_Renew_Question_CycleDate");
            rs = spDao.execute(input);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get nearest cycle date.", e);
            l.throwing(getClass().getName(), "getRenewalQuestionNearestCycleDate", ae);
            throw ae;
        }
        String rtnString = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD,
            ConverterFactory.getInstance().getConverter(String.class));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRenewalQuestionNearestCycleDate", rtnString);
        }
        return rtnString;
    }

    /**
     * Get the return value of the questionniare, 'Y' means this quesitonnaire has been sent.
     *
     * @param questionnairePk
     * @return String
     */
    public String questionnaireExists(long questionnairePk) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "questionnaireExists");
        }
        RecordSet rs;
        try {
            Record input = new Record();
            input.setFieldValue("questionnairePk", new Long(questionnairePk));
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("renewFormId", "questionnairePk"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Quest_Exists", mapping);
            rs = spDao.execute(input);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check whether questionnaire exists.", e);
            l.throwing(getClass().getName(), "questionnaireExists", ae);
            throw ae;
        }
        String rtnString = rs.getSummaryRecord().getStringValue("found");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "questionnaireExists", rtnString);
        }
        return rtnString;
    }

    /**
     * Select renewal questionnaire default deadline date
     *
     * @param endSearchDate
     * @return String
     */
    public String getRenewalQuestionDefaultDeadlineDate(String endSearchDate) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRenewalQuestionDefaultDeadlineDate");
        }
        RecordSet rs;
        try {
            Date endDate = (Date) ConverterFactory.getInstance().getConverter(Date.class).convert(Date.class, endSearchDate);
            Record input = new Record();
            input.setFieldValue("endSearchDate", endDate);
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Sel_Renew_Question_DeadLineDt");
            rs = spDao.execute(input);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get deadline date.", e);
            l.throwing(getClass().getName(), "getRenewalQuestionDefaultDeadlineDate", ae);
            throw ae;
        }
        String rtnString = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD,
            ConverterFactory.getInstance().getConverter(String.class));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRenewalQuestionDefaultDeadlineDate", rtnString);
        }
        return rtnString;
    }

    /**
     * Gennerate selected questionnaire.
     *
     * @param inputRecord
     * @return Record
     */
    public Record generateRenewalQuestionnaire(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateRenewalQuestionnaire");
        }
        RecordSet rs;
        try {
            inputRecord.setFieldValue("commitB", "Y");
            inputRecord.setFieldValue("previewYn", "N");
            inputRecord.setFieldValue("polrenFormMasterId", null);
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("renFormId", "renewFormId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("type", "QuestionnaireType"));
            mapping.addFieldMapping(new DataRecordFieldMapping("dateFrom", "startSearchDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("dateTo", "endSearchDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("riskClass", "riskClassCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("pracState", "practiceStateCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("mailingNo", "totalMailings"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Pm_Generate_Renew_Form", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to generate renewal questionnaire.", e);
            l.throwing(getClass().getName(), "generateRenewalQuestionnaire", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateRenewalQuestionnaire", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Search the questionnaire(s) mailing event for the given effective period and questionnaire type.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllMailingEvent(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMailingEvent");
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("fromDate", "startSearchDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("toDate", "endSearchDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("questType", "questionnaireType"));
            mapping.addFieldMapping(new DataRecordFieldMapping("riskClass", "specialty"));
            mapping.addFieldMapping(new DataRecordFieldMapping("policyNo", "filterPolicyNo"));
            mapping.addFieldMapping(new DataRecordFieldMapping("name", "filterName"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Sel_Question_Mailing_Event", mapping);
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get mailing event for the given effective date and type.", e);
            l.throwing(getClass().getName(), "loadAllMailingEvent", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMailingEvent", rs);
        }
        return rs;
    }

    /**
     * Search the questionnaire(s) for the selected mailing event.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllQuestionnaireForMailingEvent(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllQuestionnaireForMailingEvent");
        }
        RecordSet rs;
        try {
            inputRecord.setFieldValue("capturedB", null);
            inputRecord.setFieldValue("receivedB", null);
            inputRecord.setFieldValue("maxQuestionnaire", "Y");
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("plcRnwFrmMstrId", "polRenfrmMasterId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Renew_Form_Event_Detail", mapping);
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get questionnaires for the selected mailing event.", e);
            l.throwing(getClass().getName(), "loadAllQuestionnaireMailingEvent", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllQuestionnaireForMailingEvent", rs);
        }
        return rs;
    }

    /**
     * Update the mailing event comment.
     *
     * @param inputRecord
     * @return int the return code
     */
    public int updateMailingEventComment(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateMailingEventComment");
        }
        RecordSet rs;
        try {
            // polRenfrmMasterId and comments are passed by Js.
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Pm_Update_Comment");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to update the mailing event comment.", e);
            l.throwing(getClass().getName(), "updateMailingEventComment", ae);
            throw ae;
        }
        int returnInt = rs.getSummaryRecord().getIntegerValue("retCode").intValue();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateMailingEventComment", String.valueOf(returnInt));
        }
        return returnInt;
    }

    /**
     * Update the questionnaire status
     *
     * @param inputRecord
     * @return Record
     */
    public Record updateMailingQuestionnaireStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateMailingQuestionnaireStatus");
        }
        RecordSet rs;
        try {
            // polRenfrmMasterId is passed by Js.
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("recvB", "receivedB"));
            mapping.addFieldMapping(new DataRecordFieldMapping("captB", "capturedB"));
            mapping.addFieldMapping(new DataRecordFieldMapping("polRenfrmId", "policyRenewFormId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Pm_Update_Renew_Status", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to update the questionnaire status.", e);
            l.throwing(getClass().getName(), "updateMailingQuestionnaireStatus", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateMailingQuestionnaireStatus", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Add a renewal questionnaire
     *
     * @param inputRecord
     * @return Record
     */
    public Record addRenewalQuestionnaire(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addRenewalQuestionnaire");
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("policyNo", "policyNoCriteria"));
            mapping.addFieldMapping(new DataRecordFieldMapping("rBaseId", "riskId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("prfmId", "polRenfrmMasterId"));
            
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Pm_Add_Questionnaire", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to add a renewal questionnaire.", e);
            l.throwing(getClass().getName(), "addRenewalQuestionnaire", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addRenewalQuestionnaire",returnRecord);
        }
        return returnRecord;
    }

    /**
     * Check whether the enter policy no is a valid policy no
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean isValidPolicyNumber(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isValidPolicyNumber");
        }
        RecordSet rs;
        boolean returnValue = true;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("policyNo", "policyNoCriteria"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Policy_PK",mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to validate the policy number.", e);
            l.throwing(getClass().getName(), "isValidPolicyNumber", ae);
            throw ae;
        }
        long rtnValue = rs.getSummaryRecord().getLongValue(StoredProcedureDAO.RETURN_VALUE_FIELD).longValue();
        if (rtnValue == -1) {
            returnValue = false;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isValidPolicyNumber", String.valueOf(returnValue));
        }
        return returnValue;
    }

    /**
     * Update the send date of the current mailing event
     *
     * @param inputRecord
     * @return Record
     */
    public Record updateSendDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateSendDate");
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("plcRnFrmMstrId", "polRenfrmMasterId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.pm_update_send_date", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to update the send date of the current mailing event.", e);
            l.throwing(getClass().getName(), "updateSendDate", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateSendDate", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Submit the print job of questionnaire
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean performPrintRenewalQuestionnaire(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performPrintRenewalQuestionnaire");
        }
        RecordSet rs;
        boolean returnValue = false;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("renMasterId", "polRenfrmMasterId"));
            inputRecord.setFieldValue("polRenId", "0");
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Batch_Renewal.Submit_Output", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to submit print job.", e);
            l.throwing(getClass().getName(), "performPrintRenewalQuestionnaire", ae);
            throw ae;
        }
        int returnCode = rs.getSummaryRecord().getIntegerValue("errCode").intValue();
        if (returnCode == 1) {
            returnValue = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performPrintRenewalQuestionnaire", YesNoFlag.getInstance(returnValue));
        }
        return returnValue;
    }

    /**
     * Get the effective date by policy renew form Id.
     *
     * @param inputRecord
     * @return String
     */
    public String getEffectiveDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEffectiveDate");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Get_Effective_Date");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get effective date.", e);
            l.throwing(getClass().getName(), "getEffectiveDate", ae);
            throw ae;
        }
        String returnString = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEffectiveDate", returnString);
        }
        return returnString;
    }

    /**
     * Get the policy term information.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getTermInformation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTermInformation");
        }
        RecordSet rs;
        try {
            Record inRecord = new Record();
            inRecord.setFields(inputRecord);
            inRecord.setFieldValue("show", "N");
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "effectiveDate"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Term_Info", mapping);
            rs = spDao.execute(inRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get term information.", e);
            l.throwing(getClass().getName(), "getTermInformation", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTermInformation", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Get the web header Id.
     *
     * @param inputRecord
     * @return String
     */
    public String getWebHeader(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWebHeader");
        }
        RecordSet rs;
        try {
            Record inRecord = new Record();
            inRecord.setFields(inputRecord);
            inRecord.setFieldValue("webType", "QUEST");
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "effectiveDate"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Get_Web_Header", mapping);
            rs = spDao.execute(inRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get web app header id.", e);
            l.throwing(getClass().getName(), "getWebHeader", ae);
            throw ae;
        }
        String returnString = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getWebHeader", returnString);
        }
        return returnString;
    }

    /**
     * Get the web app Id and web app header Id.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getAppRules(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAppRules");
        }
        RecordSet rs;
        try {
            Record inRecord = new Record();
            inRecord.setFields(inputRecord);
            inRecord.setFieldValue("type", "QUEST");
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffDt"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Get_Web_Id", mapping);
            rs = spDao.execute(inRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get web app rules.", e);
            l.throwing(getClass().getName(), "getAppRules", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAppRules", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Insert the date.
     *
     * @param inputRecord
     */
    public void saveResponseDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveResponseDate");
        }
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Insert_Response_Date");
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save date.", e);
            l.throwing(getClass().getName(), "saveResponseDate", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveResponseDate");
        }
    }

    /**
     * Update the date.
     *
     * @param inputRecord
     */
    public void updateResponseDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateResponseDate");
        }
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Update_Response_Date");
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to update date.", e);
            l.throwing(getClass().getName(), "updateResponseDate", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateResponseDate");
        }
    }

    /**
     * Get the save point.
     *
     * @param inputRecord
     * @return YesNoFlag
     */
    public YesNoFlag getSavePoint(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSavePoint");
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveDate"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web.Get_Save_Point", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get save point.", e);
            l.throwing(getClass().getName(), "getSavePoint", ae);
            throw ae;
        }
        String returnString = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        YesNoFlag returnFlag = YesNoFlag.N;
        if (!StringUtils.isBlank(returnString) && returnString.equals("Y")) {
            returnFlag = YesNoFlag.Y;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSavePoint", returnFlag);
        }
        return returnFlag;
    }

    /**
     * Save the responses.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveResponses(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveResponses");
        }
        RecordSet rs;
        try {
            Record inRecord = new Record();
            inRecord.setFields(inputRecord);
            inRecord.setFieldValue("transLogId", null);
            inRecord.setFieldValue("reason", null);
            inRecord.setFieldValue("comments", null);
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("policyNo", "policyNoCriteria"));
            mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "termEffectiveDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termExpirationDate"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web.Set_Oasis_Data", mapping);
            rs = spDao.execute(inRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save response.", e);
            l.throwing(getClass().getName(), "saveResponses", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveResponses", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Set app status.
     *
     * @param inputRecord
     */
    public void setAppStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setAppStatus");
        }
        try {
            Record inRecord = new Record();
            inRecord.setFields(inputRecord);
            inRecord.setFieldValue("transLogId", null);
            inRecord.setFieldValue("reason", null);
            inRecord.setFieldValue("comments", null);
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "termEffectiveDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termExpirationDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("appHeaderId", "webAppHeaderId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web.Set_App_Status", mapping);
            spDao.execute(inRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to set app status.", e);
            l.throwing(getClass().getName(), "setAppStatus", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setAppStatus");
        }
    }

    /**
     * Get the dates.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getResponseDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getResponseDate");
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("baseRecordId", "policyRenewFormId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Sel_Response_Date", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get date.", e);
            l.throwing(getClass().getName(), "getResponseDate", ae);
            throw ae;
        }
        Record returnRecord = null;
        if (rs.getSize() > 0) {
            returnRecord = rs.getRecord(0);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getResponseDate", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Get the app status.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getAppStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAppStatus");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web.Get_Status");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get app status.", e);
            l.throwing(getClass().getName(), "getAppStatus", ae);
            throw ae;
        }
        Record returnRecord = rs.getSummaryRecord();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAppStatus", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Get the return value of data changed.
     *
     * @param inputRecord
     * @return YesNoFlag
     */
    public YesNoFlag isDataChanged(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isDataChanged");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web.Data_Changed_B");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get data changed value.", e);
            l.throwing(getClass().getName(), "isDataChanged", ae);
            throw ae;
        }
        String returnString = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        YesNoFlag returnFlag = YesNoFlag.N;
        if (!StringUtils.isBlank(returnString) && returnString.equals("Y")) {
            returnFlag = YesNoFlag.Y;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isDataChanged", returnFlag);
        }
        return returnFlag;
    }

    /**
     * Get the copic participant.
     *
     * @param inputRecord
     * @return String
     */
    public String getCopicParticipant(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCopicParticipant");
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("plcRnwFrmId", "policyRenewFormId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renew_Question.Get_Copic_Participant", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get data changed value.", e);
            l.throwing(getClass().getName(), "getCopicParticipant", ae);
            throw ae;
        }
        String returnString = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCopicParticipant", returnString);
        }
        return returnString;
    }
}
