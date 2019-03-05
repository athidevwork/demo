package dti.pm.renewalquestionnairemgr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyManager;
import dti.pm.renewalquestionnairemgr.QuestionnaireMailingEventFields;
import dti.pm.renewalquestionnairemgr.QuestionnaireResponseFields;
import dti.pm.renewalquestionnairemgr.RenewalQuestionnaireFields;
import dti.pm.renewalquestionnairemgr.RenewalQuestionnaireManager;
import dti.pm.renewalquestionnairemgr.dao.RenewalQuestionnaireDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of RenewalQuestionnaireManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 14, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/06/2008       yyh        addRenewalQuestionnaire: Change the logic for add renewal questionnaire.
 * 09/07/2010       syang      Issue 106500 - Modified updateMailingQuestionnaireStatus() to
 *                                            reset the three fields value from "-1/0" to "Y/N". 
 * 01/12/2011       ryzhao     Issue 116543 - Replace ' with '' in policyType
 * 04/20/2011       dzhang     Issue 119777 - Modified validateGenerateRenewalQuestionnaire() to avoid no data found in record error.
 * 05/03/2011       ryzhao     117394 - Modified getInitialValuesForQuestionnaireResponse() to skip the logic/process for response
 *                                      if sysparm PM_WEB_URL is NULL or undefined.
 * 05/06/2011       ryzhao     117394 - Updated per Joe's comments.
 * 09/13/2012       tcheng     137095 - Modified loadAllMailingEvent to replace ' with '' in filterPolicyNo and filterName
 * ---------------------------------------------------
 */
public class RenewalQuestionnaireManagerImpl implements RenewalQuestionnaireManager, RenewalQuestionnaireSaveProcessor {

    /**
     * Get the initial data for the page.
     * Search the questionnaire(s) for the given effective period and questionnaire type.
     *
     * @param inputRecord
     */
    public RecordSet loadAllRenewalQuestionnaire(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRenewalQuestionnaire", new Object[]{inputRecord});
        }
        RecordSet rs;
        // The if block is for searching questionnaire and else block is for loading the page.
        if (YesNoFlag.getInstance(RenewalQuestionnaireFields.getSearchQuestionnaire(inputRecord)).booleanValue()) {
            // Validate the search data.
            validateSearchCriteriaForQuestionnaire(inputRecord);
            // Get the search questionnaire(s)
            rs = getRenewalQuestionnaireDAO().loadAllRenewalQuestionnaire(inputRecord, AddSelectIndLoadProcessor.getInstance());
            rs.setSummaryRecord(inputRecord);
        }
        else {
            rs = new RecordSet();
            List fieldNameList = new ArrayList();
            fieldNameList.add(RenewalQuestionnaireFields.RENEW_FORM_ID);
            rs.addFieldNameCollection(fieldNameList);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRenewalQuestionnaire");
        }
        return rs;
    }

    /**
     * Initialize the search and mailing date.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForRenewalQuestionnaire(Record inputRecord) {
        // isMailingDateVisible use to determine whether the MailingDate Panel will display.
        Record outputRecord = new Record();
        String startSearchDate = "";
        String endSearchDate = "";
        String today = DateUtils.formatDate(new Date());
        // Get StartSearchDate.
        if (StringUtils.isSame(SysParmProvider.getInstance().getSysParm(PM_DEF_QUEST_DT), "Y")) {
            int year = DateUtils.getYear(new Date());
            startSearchDate = DEFAULT_START_SEARCH_MMDD + String.valueOf(year);
        }
        else if (StringUtils.isSame(SysParmProvider.getInstance().getSysParm(PM_QUEST_DEF_CYCDT), "Y")) {
            String nearestCycleDate = getRenewalQuestionnaireDAO().getRenewalQuestionNearestCycleDate();
            if (!StringUtils.isBlank(nearestCycleDate)) {
                startSearchDate = nearestCycleDate;
            }
        }
        // Set the endSearchDate to the same as startSearchDate.
        endSearchDate = startSearchDate;
        RenewalQuestionnaireFields.setStartSearchDate(outputRecord, startSearchDate);
        RenewalQuestionnaireFields.setEndSearchDate(outputRecord, endSearchDate);

        // Initialize the RecordSet when the page is loading at the first time.
        if (YesNoFlag.getInstance(RenewalQuestionnaireFields.getSecondMailingDateAvailable(inputRecord)).booleanValue()) {
            RenewalQuestionnaireFields.setSecondMailingDate(outputRecord, today);
        }
        if (YesNoFlag.getInstance(RenewalQuestionnaireFields.getThirdMailingDateAvailable(inputRecord)).booleanValue()) {
            RenewalQuestionnaireFields.setThirdMailingDate(outputRecord, today);
        }
        if (YesNoFlag.getInstance(RenewalQuestionnaireFields.getDeadlineDateAvailable(inputRecord)).booleanValue()) {
            // Get the default deadline date.
            String deadlineDate = getRenewalQuestionnaireDAO().getRenewalQuestionDefaultDeadlineDate(endSearchDate);
            RenewalQuestionnaireFields.setDeadlineDate(outputRecord, deadlineDate);
        }
        return outputRecord;
    }

    /**
     * Gennerate the selected questionnaire(s).
     *
     * @param inputRecords the selected questionnaire(s)
     * @return RecordSet
     */
    public RecordSet generateRenewalQuestionnaire(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateRenewalQuestionnaire", new Object[]{inputRecords});
        }
        // Validate the selected questionnaire.
        validateGenerateRenewalQuestionnaire(inputRecords);
        // After validated, the inputRecords is not null and the size is more than 0.
        RecordSet rs = new RecordSet();
        Record inputRecord = inputRecords.getSummaryRecord();
        Record returnRecord;
        // Repeat the following to generate each selected questionnaire.
        Iterator records = inputRecords.getRecords();
        while (records.hasNext()) {
            Record selectedRecord = (Record) records.next();
            // Add the inputRecord to selectedRecord since the store procedure need some input parameters.
            selectedRecord.setFields(inputRecord);
            // Generate selected questionnaire(s).
            returnRecord = getRenewalQuestionnaireDAO().generateRenewalQuestionnaire(selectedRecord);
            // Store the return message along with "<policy Type Description>-<Risk Class Desccription>"
            RenewalQuestionnaireFields.setQuestionnaireResult(returnRecord,
                RenewalQuestionnaireFields.getPolicyTypeDesc(selectedRecord) + "-" +
                    RenewalQuestionnaireFields.getRiskClassDesc(selectedRecord));
            RenewalQuestionnaireFields.setMessage(returnRecord, RenewalQuestionnaireFields.getRetmsg(returnRecord));
            RenewalQuestionnaireFields.setRenewFormId(returnRecord, RenewalQuestionnaireFields.getRenewFormId(selectedRecord));
            // Add the return record to RecordSet.
            rs.addRecord(returnRecord);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateRenewalQuestionnaire");
        }
        return rs;
    }

    /**
     * Get deadline date.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public String getRenewalQuestionDefaultDeadlineDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRenewalQuestionDefaultDeadlineDate", new Object[]{inputRecord});
        }
        String endSearchDate = RenewalQuestionnaireFields.getEndSearchDate(inputRecord);
        String deadlineDate = "";
        if (!StringUtils.isBlank(endSearchDate)) {
            deadlineDate = getRenewalQuestionnaireDAO().getRenewalQuestionDefaultDeadlineDate(endSearchDate);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRenewalQuestionDefaultDeadlineDate");
        }
        return deadlineDate;
    }

    /**
     * Load all questionnaire mailing events.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllMailingEvent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMailingEvent", new Object[]{inputRecord});
        }
        RecordSet rs;
        // The if block is for searching and filtering questionnaire and else block is for loading the page.
        if (QuestionnaireMailingEventFields.getSearchMailingEvent(inputRecord).booleanValue()) {
            // Validate the search criteria.
            validateSearchCriteriaForMailingEvent(inputRecord);
            // Issue 116543
            // If there is a ' in the policyType, we need to put an escape character ' before this ' so that the procedure will be executed correctly.
            // Replace ' with '' in policyType to fix the bug when load all mailing event procedure is executed.
            if (inputRecord.hasStringValue(QuestionnaireMailingEventFields.POLICY_TYPE)) {
                QuestionnaireMailingEventFields.setPolicyType(inputRecord,QuestionnaireMailingEventFields.getPolicyType(inputRecord).replaceAll("'", "''"));
            }
            // Issue 137095
            if (inputRecord.hasStringValue(QuestionnaireMailingEventFields.FILTER_POLICY_NO)) {
                QuestionnaireMailingEventFields.setFilterPolicyNo(inputRecord,QuestionnaireMailingEventFields.getFilterPolicyNo(inputRecord).replaceAll("'", "''"));
            }
            if (inputRecord.hasStringValue(QuestionnaireMailingEventFields.FILTER_NAME)) {
                QuestionnaireMailingEventFields.setFilterName(inputRecord,QuestionnaireMailingEventFields.getFilterName(inputRecord).replaceAll("'", "''"));
            }

            // Get the questionnaire(s) mailing event.
            MailingEventEntitlementRecordLoadProcessor mailingEventLPEntitlement = new MailingEventEntitlementRecordLoadProcessor();
            rs = getRenewalQuestionnaireDAO().loadAllMailingEvent(inputRecord, mailingEventLPEntitlement);
        }
        else {
            rs = new RecordSet();
            List fieldNameList = new ArrayList();
            // Get the initial values for mailing event page.
            Record record = getInitialValuesForQuestionnaireMailingEvent();
            rs.setSummaryRecord(record);
            fieldNameList.add(ROW_NUM);
            fieldNameList.add("isMailAvailable");
            rs.addFieldNameCollection(fieldNameList);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMailingEvent");
        }
        return rs;
    }

    /**
     * Get the initial values for mailing event page.
     *
     * @return Record
     */
    public Record getInitialValuesForQuestionnaireMailingEvent() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForQuestionnaireMailingEvent");
        }
        Record returnRecord = new Record();
        String startSearchDate = "";
        // Get StartSearchDate.
        if (StringUtils.isSame(SysParmProvider.getInstance().getSysParm(PM_QUEST_DEF_CYCDT), "Y")) {
            String nearestCycleDate = getRenewalQuestionnaireDAO().getRenewalQuestionNearestCycleDate();
            if (!StringUtils.isBlank(nearestCycleDate)) {
                startSearchDate = nearestCycleDate;
            }
        }
        // Set the endSearchDate to the same as startSearchDate.
        QuestionnaireMailingEventFields.setStartSearchDate(returnRecord, startSearchDate);
        QuestionnaireMailingEventFields.setEndSearchDate(returnRecord, startSearchDate);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForQuestionnaireMailingEvent");
        }
        return returnRecord;
    }

    /**
     * Load all questionnaires for current mailing event.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllQuestionnaireForMailingEvent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllQuestionnaireForMailingEvent", new Object[]{inputRecord});
        }
        RecordSet rs;
        if (inputRecord.hasStringValue(QuestionnaireMailingEventFields.POL_REN_FRM_MASTER_ID)) {
            QuestionnaireRecordLoadProcessor questionnaireLP = new QuestionnaireRecordLoadProcessor(inputRecord);
            rs = getRenewalQuestionnaireDAO().loadAllQuestionnaireForMailingEvent(inputRecord, questionnaireLP);
        }
        else {
            rs = new RecordSet();
            List fieldNameList = new ArrayList();
            fieldNameList.add(ROW_NUM);
            fieldNameList.add("isReceivedBAvailable");
            fieldNameList.add("isCapturedBAvailable");
            fieldNameList.add("isResendBAvailable");
            rs.addFieldNameCollection(fieldNameList);
        }

        // Set page entitlment for "part time notes"
        String questionnaireType = "";
        if (inputRecord.hasStringValue("questionnaireType")) {
            questionnaireType = inputRecord.getStringValue("questionnaireType");
        }
        if ("PART_TIME".equals(questionnaireType)) {
            rs.getSummaryRecord().setFieldValue("isPartTimeNotesAvailable", "Y");
        }
        else {
            rs.getSummaryRecord().setFieldValue("isPartTimeNotesAvailable", "N");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllQuestionnaireForMailingEvent");
        }
        return rs;
    }

    /**
     * Store the error message.
     *
     * @param inputRecords
     * @return RecordSet
     */
    public RecordSet processSaveAllMailingQuestionnare(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processSaveAllMailingQuestionnare", new Object[]{inputRecords});
        }
        RecordSet rs = new RecordSet();
        Record returnRecord;
        int i = 0;
        Record sumRecord = inputRecords.getSummaryRecord();
        RenewalQuestionnaireSaveProcessor saveProcessor = (RenewalQuestionnaireSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
        // If comment has changed,then get comment from summayRecord and save the new comment.
        if (QuestionnaireMailingEventFields.getCommentChanged(sumRecord).booleanValue()) {
            try {
                saveProcessor.updateMailingEventComment(sumRecord);
            }
            catch (ValidationException ve) {
                 l.info("Update mailing event comment error,system rollbacks this operation.");
            }
            // If update fail, system stores error message.
            if (sumRecord.hasStringValue(RET_CODE) && sumRecord.getIntegerValue(RET_CODE).intValue() == -1) {
                returnRecord = new Record();
                returnRecord.setFieldValue(ROW_NUM, String.valueOf("1"));
                QuestionnaireMailingEventFields.setPolicyNo(returnRecord, null);
                QuestionnaireMailingEventFields.setMessage(returnRecord,
                    MessageManager.getInstance().formatMessage("pm.renewalQuestionnaireMailingEvent.saveComment.fail"));
                rs.addRecord(returnRecord);
            }
        }
        // Update the changed record.
        String polRenfrmMasterId = QuestionnaireMailingEventFields.getPolRenfrmMasterId(sumRecord);
        Iterator records = inputRecords.getRecords();
        while (records.hasNext()) {
            i = i + 1;
            Record changedRecord = (Record) records.next();
            QuestionnaireMailingEventFields.setPolRenfrmMasterId(changedRecord, polRenfrmMasterId);
            try {
                saveProcessor.updateMailingQuestionnaireStatus(changedRecord);
            }
            catch (ValidationException ve) {
                l.info("Update questionnaire status error,system rollbacks this operation.");
            }
            if (changedRecord.hasStringValue(RET_CODE) && changedRecord.getIntegerValue(RET_CODE).intValue() == -1) {
                returnRecord = new Record();
                returnRecord.setFieldValue(ROW_NUM, String.valueOf(i));
                QuestionnaireMailingEventFields.setPolicyNo(returnRecord, QuestionnaireMailingEventFields.getPolicyNo(changedRecord));
                QuestionnaireMailingEventFields.setMessage(returnRecord, changedRecord.getStringValue(RET_MSG));
                rs.addRecord(returnRecord);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processSaveAllMailingQuestionnare");
        }
        return rs;
    }

    /**
     * Update the mailing changed comment.
     *
     * @param inputRecord
     */
    public void updateMailingEventComment(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateMailingEventComment", new Object[]{inputRecord});
        }
        int rtn = getRenewalQuestionnaireDAO().updateMailingEventComment(inputRecord);
        if (rtn == -1) {
            inputRecord.setFieldValue(RET_CODE, "-1");
            // Rollback
            throw new ValidationException("Update comment error,system rollbacks this operation.");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateMailingEventComment");
        }
    }

    /**
     * Update the mailing event questionnaire status.
     *
     * @param inputRecord
     */
    public void updateMailingQuestionnaireStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateMailingQuestionnaireStatus", new Object[]{inputRecord});
        }
        // Issue 106500, system should reset the three fields value from "-1/0" to "Y/N". 
        String receivedB = inputRecord.getStringValue("receivedB");
        String resendB = inputRecord.getStringValue("resendB");
        String capturedB = inputRecord.getStringValue("capturedB");
        // Reset receivedB to Y/N.
        if ("0".equals(receivedB)) {
            inputRecord.setFieldValue("receivedB", YesNoFlag.N);
        }
        else if ("-1".equals(receivedB)) {
            inputRecord.setFieldValue("receivedB", YesNoFlag.Y);
        }
        // Reset resendB to Y/N.
        if ("0".equals(resendB)) {
            inputRecord.setFieldValue("resendB", YesNoFlag.N);
        }
        else if ("-1".equals(resendB)) {
            inputRecord.setFieldValue("resendB", YesNoFlag.Y);
        }
        // Reset capturedB to Y/N.
        if ("0".equals(capturedB)) {
            inputRecord.setFieldValue("capturedB", YesNoFlag.N);
        }
        else if ("-1".equals(capturedB)) {
            inputRecord.setFieldValue("capturedB", YesNoFlag.Y);
        }

        // Update status if questionnaires status are changed.
        Record rtnRecord = getRenewalQuestionnaireDAO().updateMailingQuestionnaireStatus(inputRecord);
        // If the return code is -1, rollback and store error message.
        if (rtnRecord.hasStringValue(RET_CODE) && rtnRecord.getIntegerValue(RET_CODE).intValue() == -1) {
            inputRecord.setFieldValue(RET_CODE, "-1");
            inputRecord.setFieldValue(RET_MSG, rtnRecord.getStringValue(RET_MSG));
            // Rollback
            throw new ValidationException("Update questionnaire status error,system rollbacks this operation.");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateMailingQuestionnaireStatus");
        }
    }

    /**
     * Add renewal questionnaire
     *
     * @param inputRecord
     * @return RecordSet
     */
    public void addRenewalQuestionnaire(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addRenewalQuestionnaire", new Object[]{inputRecord});
        }
        // Validate the inputRecord.
        validateAddRenewalQuestionnaire(inputRecord);
        // Add renewal questionnaire.
        Record returnRecord = getRenewalQuestionnaireDAO().addRenewalQuestionnaire(inputRecord);
        if (returnRecord.hasField(RET_CODE) && returnRecord.getIntegerValue(RET_CODE).intValue() != 0) {
            if (returnRecord.getIntegerValue(RET_CODE).intValue() == -1) {
                MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireMailingEvent.updateSendDate.fail",
                    new String[]{returnRecord.getStringValue(RET_MSG)});
            }
            else {
                l.info(returnRecord.getStringValue(RET_MSG));
                MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireMailingEvent.addRenewal.fail");
            }
        }
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Add renewal questionnaire error.");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addRenewalQuestionnaire");
        }
    }

    /**
     * Print renewal questionnaire.
     *
     * @param inputRecord
     */
    public void performPrintRenewalQuestionnare(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performPrintRenewalQuestionnare", new Object[]{inputRecord});
        }
        // Validate print information.
        validatePerformPrintRenewalQuestionnare(inputRecord);
        // If submit print successfully and option is ALL then update send date.
        if (getRenewalQuestionnaireDAO().performPrintRenewalQuestionnaire(inputRecord)) {
            String option = QuestionnaireMailingEventFields.getPrintOptions(inputRecord);
            if (option.equals("ALL")) {
                Record returnRecord = getRenewalQuestionnaireDAO().updateSendDate(inputRecord);
                if (returnRecord.hasStringValue(RET_CODE) &&
                    returnRecord.getIntegerValue(RET_CODE).intValue() == -1) {
                    MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireMailingEvent.updateSendDate.fail",
                        new String[]{returnRecord.getStringValue(RET_MSG)});
                }
            }
        }
        else {
            MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireMailingEvent.print.fail");
        }
        // throw validation exception if there is some error message.
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Submit the print job error.");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performPrintRenewalQuestionnare");
        }
    }

    /**
     * Get the initial values for renewal questionnaire response page.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForQuestionnaireResponse(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForQuestionnaireResponse", new Object[]{inputRecord});
        }
        Record outputRecord = new Record();
        Record dateRecord = null;
        String url = "";
        String copic = "N";
        // Check where the page is opened.
        if (QuestionnaireResponseFields.getSearchDate(inputRecord).booleanValue() ||
            QuestionnaireResponseFields.getComeFromMailingEvent(inputRecord).booleanValue()) {
            // Set the search criteria.
            QuestionnaireResponseFields.setIsNewDate(outputRecord, YesNoFlag.N);
            QuestionnaireResponseFields.setPolicyNoCriteria(outputRecord, QuestionnaireResponseFields.getPolicyNoCriteria(inputRecord));
            QuestionnaireResponseFields.setRiskId(outputRecord, QuestionnaireResponseFields.getRiskId(inputRecord));
            QuestionnaireResponseFields.setPolicyRenewalFormId(outputRecord, QuestionnaireResponseFields.getPolicyRenewalFormId(inputRecord));
            // If sysparm PM_WEB_URL is NULL or undefined, skip the logic/process for response.
            if (QuestionnaireResponseFields.getShowResponseSection(inputRecord).booleanValue()) {
                // Get web app values.
                Record valueRecord = getWebAppValues(inputRecord);
                QuestionnaireResponseFields.setPolicyID(outputRecord, QuestionnaireResponseFields.getPolicyId(valueRecord));
                QuestionnaireResponseFields.setTermEffectiveDate(outputRecord, QuestionnaireResponseFields.getTermEffDt(valueRecord));
                QuestionnaireResponseFields.setTermExpirationDate(outputRecord, QuestionnaireResponseFields.getTermExpDt(valueRecord));
                QuestionnaireResponseFields.setTermBaseId(outputRecord, QuestionnaireResponseFields.getTermBaseId(valueRecord));
                // Get web app rules.
                Record ruleRecord = getWebAppRules(valueRecord);
                QuestionnaireResponseFields.setWebAppHeaderId(outputRecord, QuestionnaireResponseFields.getWebAppHeaderId(ruleRecord));
                QuestionnaireResponseFields.setWebId(outputRecord, QuestionnaireResponseFields.getWebId(ruleRecord));
                // Get web app URL.
                url = getWebAppUrl(ruleRecord);
                // Get status and the response options availability.
                Record statusRecord = getAppStatus(ruleRecord);
                outputRecord.setFields(statusRecord);
            }
            // Get the dates.
            dateRecord = getResponseDate(inputRecord);
            // Get the default value of copic participant.
            copic = getRenewalQuestionnaireDAO().getCopicParticipant(inputRecord);
        }
        if (dateRecord == null) {
            dateRecord = new Record();
            QuestionnaireResponseFields.setPmApplicationTrackingId(dateRecord, getDbUtilityManager().getNextSequenceNo().toString());
            QuestionnaireResponseFields.setIsNewDate(outputRecord, YesNoFlag.Y);
            // For Insert Date.
            if (inputRecord.hasStringValue(QuestionnaireResponseFields.POLICY_RENEW_FORM_ID)) {
                QuestionnaireResponseFields.setSourceTableName(outputRecord, POLICY_RENEW_FORM);
                QuestionnaireResponseFields.setSourceRecordId(outputRecord, QuestionnaireResponseFields.getPolicyRenewalFormId(inputRecord));
            }
        }
        // Add the fields to outputRecord.
        QuestionnaireResponseFields.setCallCopicParticipant(outputRecord, copic);
        QuestionnaireResponseFields.setResponseURL(outputRecord, url);
        outputRecord.setFields(dateRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForQuestionnaireResponse", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Get the questionnaire response dates.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getResponseDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getResponseDate", new Object[]{inputRecord});
        }
        Record returnRecord = getRenewalQuestionnaireDAO().getResponseDate(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getResponseDate", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Save the questionnaire response dates.If system fails to save date, system rools back.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveResponseDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveResponseDate", new Object[]{inputRecord});
        }
        try {
            if (QuestionnaireResponseFields.getIsNewDate(inputRecord).booleanValue()) {
                getRenewalQuestionnaireDAO().saveResponseDate(inputRecord);
            }
            else {
                getRenewalQuestionnaireDAO().updateResponseDate(inputRecord);
            }
        }
        catch (Exception e) {
            MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireResponse.saveData.error");
            throw new ValidationException("save response date error.");
        }
        Record returnRecord = new Record();
        QuestionnaireResponseFields.setSaveDateSuccess(returnRecord, YesNoFlag.Y);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveResponseDate", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Save the questionnaire responses.If system fails to save responses, system rools back.
     *
     * @param inputRecord
     */
    public void saveResponses(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveResponses", new Object[]{inputRecord});
        }
        YesNoFlag returnFlag = getRenewalQuestionnaireDAO().getSavePoint(inputRecord);
        if (!returnFlag.booleanValue()) {
            Record returnRecord = getRenewalQuestionnaireDAO().saveResponses(inputRecord);
            if (returnRecord.getIntegerValue("rc").intValue() != 0) {
                MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireResponse.saveResposne.error",
                    new String[]{returnRecord.getStringValue("rmsg")});
                throw new ValidationException("Save response error.");
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveResponses");
        }
    }

    /**
     * Save the app status.If system fails to save status, system rools back.
     *
     * @param inputRecord
     */
    public void saveAppStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "SetAppStatus", new Object[]{inputRecord});
        }
        if (QuestionnaireResponseFields.getReopenResponse(inputRecord).booleanValue()) {
            QuestionnaireResponseFields.setStatus(inputRecord, COMPLETED);
        }
        else {
            if (QuestionnaireResponseFields.getAppStatus(inputRecord).equals("Official")) {
                QuestionnaireResponseFields.setStatus(inputRecord, OFFICIAL);
            }
        }
        try {
            getRenewalQuestionnaireDAO().setAppStatus(inputRecord);
        }
        catch (Exception e) {
            throw new ValidationException("save app status error.");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "SetAppStatus");
        }
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
            l.entering(getClass().getName(), "getAppStatus", new Object[]{inputRecord});
        }
        Record inRecord = new Record();
        QuestionnaireResponseFields.setWebAppHeaderId(inRecord, QuestionnaireResponseFields.getWebAppHeaderId(inputRecord));
        ResponseEntitlementRecordLoadProcessor responseLoadPRocessor = new ResponseEntitlementRecordLoadProcessor();
        Record returnRecord = getRenewalQuestionnaireDAO().getAppStatus(inputRecord);
        responseLoadPRocessor.postProcessRecord(returnRecord, true);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAppStatus", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Get the web app values.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getWebAppValues(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWebAppValues", new Object[]{inputRecord});
        }
        // Get the policy Id. There is PolicyNoCriteria in inputRecord.
        Record criteriaRecord = new Record();
        criteriaRecord.setFieldValue("policyNo", QuestionnaireResponseFields.getPolicyNoCriteria(inputRecord));
        String policyId = getPolicyManager().getPolicyId(criteriaRecord);
        // Get the effective date.
        String effectiveDate = getRenewalQuestionnaireDAO().getEffectiveDate(inputRecord);
        // Get term inforamtion.
        QuestionnaireResponseFields.setPolicyID(inputRecord, policyId);
        QuestionnaireResponseFields.setEffectiveDate(inputRecord, effectiveDate);
        Record outputRecord = getRenewalQuestionnaireDAO().getTermInformation(inputRecord);
        // Get web app header Id, if system doesn't find the web header Id the throws a error message.
        String webHeaderId = getRenewalQuestionnaireDAO().getWebHeader(inputRecord);
        if (StringUtils.isBlank(webHeaderId)) {
            MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireResponse.questionnaire.notExists",
                new String[]{QuestionnaireResponseFields.getPolicyNoCriteria(inputRecord)});
            throw new ValidationException("Get web header Id error.");
        }
        // The return values are all should be store.
        QuestionnaireResponseFields.setPolicyNoCriteria(outputRecord, QuestionnaireResponseFields.getPolicyNoCriteria(inputRecord));
        QuestionnaireResponseFields.setWebAppHeaderId(outputRecord, webHeaderId);
        QuestionnaireResponseFields.setPolicyID(outputRecord, policyId);
        QuestionnaireResponseFields.setRiskId(outputRecord, QuestionnaireResponseFields.getRiskId(inputRecord));
        QuestionnaireResponseFields.setEffectiveDate(outputRecord, effectiveDate);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getWebAppValues", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Get the web app rules.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getWebAppRules(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWebAppRules", new Object[]{inputRecord});
        }

        Record returnRecord = getRenewalQuestionnaireDAO().getAppRules(inputRecord);
        if (StringUtils.isBlank(QuestionnaireResponseFields.getWebAppHeaderId(returnRecord)) ||
            StringUtils.isBlank(QuestionnaireResponseFields.getWebId(returnRecord))) {
            MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireResponse.webIdentifier.empty");
            throw new ValidationException("Get web app rules error.");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getWebAppRules", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Get the web app url.
     *
     * @param inputRecord
     * @return String
     */
    public String getWebAppUrl(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWebAppUrl", new Object[]{inputRecord});
        }
        String returnString = "";
        String webUrl = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_WEB_URL);
        if (StringUtils.isBlank(webUrl)) {
            MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireResponse.url.missing");
            throw new ValidationException("Get web app url error.");
        }
        else {
            String webAppHeaderId = QuestionnaireResponseFields.getWebAppHeaderId(inputRecord);
            String webId = QuestionnaireResponseFields.getWebId(inputRecord);
            returnString = webUrl + "?APP_appFk=" + webAppHeaderId + "&appid=" + webId;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getWebAppUrl", returnString);
        }
        return returnString;
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
            l.entering(getClass().getName(), "isDataChanged", new Object[]{inputRecord});
        }
        YesNoFlag returnFlag = getRenewalQuestionnaireDAO().isDataChanged(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isDataChanged", returnFlag);
        }
        return returnFlag;
    }

    /**
     * The validation of searching questionnaire(s) mailing event.
     *
     * @param inputRecord
     */
    protected void validateSearchCriteriaForMailingEvent(Record inputRecord) {
        if (!inputRecord.hasStringValue(QuestionnaireMailingEventFields.START_SEARCH_DATE)) {
            MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.startSearchDate.empty");
        }
        if (!inputRecord.hasStringValue(QuestionnaireMailingEventFields.END_SEARCH_DATE)) {
            MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.endSearchDate.empty");
        }
        if (!inputRecord.hasStringValue(QuestionnaireMailingEventFields.QUESTIONNAIRE_TYPE)) {
            MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.questionnaireType.empty");
        }
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Search questionnaire mailing event error.");
    }

    /**
     * The validation of searching questionnaire(s).
     *
     * @param inputRecord
     */
    protected void validateSearchCriteriaForQuestionnaire(Record inputRecord) {
        if (!inputRecord.hasStringValue(RenewalQuestionnaireFields.START_SEARCH_DATE)) {
            MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.startSearchDate.empty");
        }
        if (!inputRecord.hasStringValue(RenewalQuestionnaireFields.END_SEARCH_DATE)) {
            MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.endSearchDate.empty");
        }
        if (!inputRecord.hasStringValue(RenewalQuestionnaireFields.QUESTIONNAIRE_TYPE)) {
            MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.questionnaireType.empty");
        }
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Search questionnaire error.");
    }

    /**
     * The validation of generating selected questionnaire(s).
     *
     * @param inputRecords
     */
    protected void validateGenerateRenewalQuestionnaire(RecordSet inputRecords) {
        if (inputRecords == null || inputRecords.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.noQuestionnaireSelected");
        }
        // throw validation exception if inputRecords is null or empty.
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("No questionnaire was selected.");
        // If Mailing Dates fields are visible, perform the following validateions.
        Record inputRecord = inputRecords.getSummaryRecord();
        String secondMailingDateAvailable = inputRecord.hasField(RenewalQuestionnaireFields.SECOND_MAILING_DATE_AVAILABLE) ? RenewalQuestionnaireFields.getSecondMailingDateAvailable(inputRecord) : null;
        String thirdMailingDateAvailable = inputRecord.hasField(RenewalQuestionnaireFields.THIRD_MAILING_DATE_AVAILABLE) ? RenewalQuestionnaireFields.getThirdMailingDateAvailable(inputRecord) : null;
        String deadlineDateAvailable = inputRecord.hasField(RenewalQuestionnaireFields.DEADLINE_DATE_AVAILABLE) ? RenewalQuestionnaireFields.getDeadlineDateAvailable(inputRecord) : null;
        if (YesNoFlag.getInstance(secondMailingDateAvailable).booleanValue() &&
            YesNoFlag.getInstance(thirdMailingDateAvailable).booleanValue() &&
            YesNoFlag.getInstance(deadlineDateAvailable).booleanValue()) {
            Date today = DateUtils.parseDate(DateUtils.formatDate(new Date()));
            Date secondMailingDate = inputRecord.getDateValue(RenewalQuestionnaireFields.SECOND_MAILING_DATE);
            Date thirdMailingDate = inputRecord.getDateValue(RenewalQuestionnaireFields.THIRD_MAILING_DATE);
            Date deadlineDate = inputRecord.getDateValue(RenewalQuestionnaireFields.DEADLINE_DATE);
            if ((!inputRecord.hasStringValue(RenewalQuestionnaireFields.SECOND_MAILING_DATE)) || secondMailingDate.before(today)) {
                MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.secondMailingDate.lessSystemDate");
            }
            if ((!inputRecord.hasStringValue(RenewalQuestionnaireFields.THIRD_MAILING_DATE)) || thirdMailingDate.before(today)) {
                MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.thirdMailingDate.lessSystemDate");
            }
            if ((!inputRecord.hasStringValue(RenewalQuestionnaireFields.DEADLINE_DATE)) || deadlineDate.before(today)) {
                MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.deadlineDate.lessSystemDate");
            }
            if ((!inputRecord.hasStringValue(RenewalQuestionnaireFields.THIRD_MAILING_DATE)) ||
                (!inputRecord.hasStringValue(RenewalQuestionnaireFields.SECOND_MAILING_DATE)) ||
                thirdMailingDate.before(secondMailingDate)) {
                MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.thirdMailingDate.lessSecondMailingDate");
            }
            if ((!inputRecord.hasStringValue(RenewalQuestionnaireFields.DEADLINE_DATE)) ||
                (!inputRecord.hasStringValue(RenewalQuestionnaireFields.THIRD_MAILING_DATE)) ||
                deadlineDate.before(thirdMailingDate)) {
                MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.deadlineDate.lessThirdMailingDate");
            }
        }
        else {
            if (!YesNoFlag.getInstance(secondMailingDateAvailable).booleanValue()) {
                RenewalQuestionnaireFields.setSecondMailingDate(inputRecord, null);
            }
            if (!YesNoFlag.getInstance(thirdMailingDateAvailable).booleanValue()) {
                RenewalQuestionnaireFields.setThirdMailingDate(inputRecord, null);
            }
            if (!YesNoFlag.getInstance(deadlineDateAvailable).booleanValue()) {
                RenewalQuestionnaireFields.setDeadlineDate(inputRecord, null);
            }
        }
        // Throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Validate the mailing date error.");
        // Validate whether the questionnaire exists.
        if (StringUtils.isSame(SysParmProvider.getInstance().getSysParm(PM_QUEST_NOREGEN), "Y") &&
            StringUtils.isSame(SysParmProvider.getInstance().getSysParm(PM_QUEST_EXISTS), "Y")) {
            // Since the inputRecord has been checked in the top,we can use it at once.
            // Repeat the following validation for each selected questionnaire.
            Iterator records = inputRecords.getRecords();
            while (records.hasNext()) {
                Record record = (Record) records.next();
                String rowId = RenewalQuestionnaireFields.getRenewFormId(record);
                String rtn = getRenewalQuestionnaireDAO().questionnaireExists(Long.valueOf(rowId).longValue());
                if (YesNoFlag.getInstance(rtn).booleanValue()) {
                    String policyType = RenewalQuestionnaireFields.getPolicyTypeDesc(record);
                    MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.questionnaire.alreadySent",
                        new String[]{policyType.substring(0, policyType.indexOf("-")), RenewalQuestionnaireFields.getRiskClassDesc(record)},
                        RenewalQuestionnaireFields.POLICY_TYPE_DESC, rowId);
                    break;
                }
            }
        }
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("The selected questionnaire has been sent.");
    }

    /**
     * Validate the inputRecord of add renewal questionnaire
     *
     * @param inputRecord
     */
    protected void validateAddRenewalQuestionnaire(Record inputRecord) {
        if (!inputRecord.hasStringValue(QuestionnaireMailingEventFields.POLICY_NO_CRITERIA)) {
            MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireMailingEvent.addRenewal.policyNo.empty");
        }
        else {
            if (!getRenewalQuestionnaireDAO().isValidPolicyNumber(inputRecord)) {
                MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireMailingEvent.addRenewal.policyNo.invalid");
            }
        }
        if (!inputRecord.hasStringValue(QuestionnaireMailingEventFields.RISK_ID)) {
            MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireMailingEvent.addRenewal.riskName.empty");
        }
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Add questionnaire error.");
    }

    /**
     * Validate the inputRecord of print renewal questionnaire
     *
     * @param inputRecord
     */
    protected void validatePerformPrintRenewalQuestionnare(Record inputRecord) {
        if (!inputRecord.hasStringValue(QuestionnaireMailingEventFields.PRINT_OPTIONS)) {
            MessageManager.getInstance().addErrorMessage("pm.renewalQuestionnaireMailingEvent.print.options.empty");
        }
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Submit the print job error.");
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getRenewalQuestionnaireDAO() == null)
            throw new ConfigurationException("The required property 'getRenewalQuestionnaireDAO' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'getDbUtilityManager' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'getPolicyManager' is missing.");
    }

    public RenewalQuestionnaireDAO getRenewalQuestionnaireDAO() {
        return m_renewalQuestionnaireDAO;
    }

    public void setRenewalQuestionnaireDAO(RenewalQuestionnaireDAO renewalQuestionnaireDAO) {
        m_renewalQuestionnaireDAO = renewalQuestionnaireDAO;
    }

    public DBUtilityManager getDbUtilityManager() {
        return this.m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        this.m_dbUtilityManager = dbUtilityManager;
    }

    public PolicyManager getPolicyManager() {
        return this.m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.m_policyManager = policyManager;
    }

    protected final static String SAVE_PROCESSOR = "RenewalQuestionnaireManager";
    private RenewalQuestionnaireDAO m_renewalQuestionnaireDAO;
    private DBUtilityManager m_dbUtilityManager;
    private PolicyManager m_policyManager;
    private final static String PM_DEF_QUEST_DT = "PM_DEF_QUEST_DT";
    private final static String PM_QUEST_DEF_CYCDT = "PM_QUEST_DEF_CYCDT";
    private final static String PM_QUEST_NOREGEN = "PM_QUEST_NOREGEN";
    private final static String PM_QUEST_EXISTS = "PM_QUEST_EXISTS";
    private final static String DEFAULT_START_SEARCH_MMDD = "07/01/";
    private final static String OFFICIAL = "OFFICIAL";
    private final static String COMPLETED = "COMPLETED";
    private final static String POLICY_RENEW_FORM = "POLICY_RENEW_FORM";
    private final static String RET_CODE = "retCode";
    private final static String RET_MSG = "retMsg";
    private static final String ROW_NUM = "rownum";
}
