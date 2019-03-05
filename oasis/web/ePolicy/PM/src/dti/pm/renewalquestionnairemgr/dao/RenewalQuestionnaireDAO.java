package dti.pm.renewalquestionnairemgr.dao;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to provide DAO operation for renewal questionnaire.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 14, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface RenewalQuestionnaireDAO {
    /**
     * Search the questionnaire(s) for the given effective period and questionnaire type.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllRenewalQuestionnaire(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Select renewal questionnaire nearest cycle date
     *
     * @return String
     */
    public String getRenewalQuestionNearestCycleDate();

    /**
     * Get the return value of the questionniare, 'Y' means this quesitonnaire has been sent.
     *
     * @param questionnairePk
     * @return String
     */
    public String questionnaireExists(long questionnairePk);

    /**
     * Select renewal questionnaire default deadline date
     *
     * @param endSearchDate
     * @return String
     */
    public String getRenewalQuestionDefaultDeadlineDate(String endSearchDate);

    /**
     * Gennerate selected questionnaire(s).
     *
     * @param inputRecord
     * @return Record
     */
    public Record generateRenewalQuestionnaire(Record inputRecord);

    /**
     * Search the questionnaire(s) mailing event for the given effective period and questionnaire type.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllMailingEvent(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Search the questionnaire(s) for the selected mailing event.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllQuestionnaireForMailingEvent(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Update the mailing event comment.
     *
     * @param inputRecord
     * @return int the return code
     */
    public int updateMailingEventComment(Record inputRecord);

    /**
     * Update the questionnaire status
     *
     * @param inputRecord
     * @return Record
     */
    public Record updateMailingQuestionnaireStatus(Record inputRecord);

    /**
     * Add a renewal questionnaire
     *
     * @param inputRecord
     * @return Record
     */
    public Record addRenewalQuestionnaire(Record inputRecord);

    /**
     * Check whether the enter policy no is a valid policy no
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean isValidPolicyNumber(Record inputRecord);

    /**
     * Update the send date of mailing event
     *
     * @param inputRecord
     * @return Record
     */
    public Record updateSendDate(Record inputRecord);

    /**
     * Submit the print job of questionnaire
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean performPrintRenewalQuestionnaire(Record inputRecord);

    /**
     * Get the effective date by policy renew form Id.
     *
     * @param inputRecord
     * @return String
     */
    public String getEffectiveDate(Record inputRecord);

    /**
     * Get the policy term information.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getTermInformation(Record inputRecord);

    /**
     * Get the web header Id.
     *
     * @param inputRecord
     * @return String
     */
    public String getWebHeader(Record inputRecord);


    /**
     * Get the web app Id and web app header Id.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getAppRules(Record inputRecord);

    /**
     * Insert the date.
     *
     * @param inputRecord
     */
    public void saveResponseDate(Record inputRecord);

    /**
     * Update the date.
     *
     * @param inputRecord
     */
    public void updateResponseDate(Record inputRecord);

    /**
     * Get the save point.
     *
     * @param inputRecord
     * @return YesNoFlag
     */
    public YesNoFlag getSavePoint(Record inputRecord);

    /**
     * Save the responses.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveResponses(Record inputRecord);

    /**
     * Set app status.
     *
     * @param inputRecord
     */
    public void setAppStatus(Record inputRecord);

    /**
     * Get the dates.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getResponseDate(Record inputRecord);

    /**
     * Get the app status.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getAppStatus(Record inputRecord);

    /**
     * Get the return value of data changed.
     *
     * @param inputRecord
     * @return YesNoFlag
     */
    public YesNoFlag isDataChanged(Record inputRecord);

    /**
     * Get the copic participant.
     *
     * @param inputRecord
     * @return String
     */
    public String getCopicParticipant(Record inputRecord);
}
