package dti.pm.renewalquestionnairemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;

/**
 * Interface to handle renewal questionnaire.
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
public interface RenewalQuestionnaireManager {

    /**
     * Get the initial data for the page.
     * Search the questionnaire(s) for the given effective period and questionnaire type.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRenewalQuestionnaire(Record inputRecord);

    /**
     * Initialize the search and mailing date.
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForRenewalQuestionnaire(Record inputRecord);

    /**
     * Gennerate selected questionnaire(s).
     *
     * @param inputRecords
     * @return RecordSet
     */
    public RecordSet generateRenewalQuestionnaire(RecordSet inputRecords);

    /**
     * Get deadline date.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public String getRenewalQuestionDefaultDeadlineDate(Record inputRecord);

     /**
     * Load all questionnaire mailing events.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllMailingEvent(Record inputRecord);

   /**
     * Get the initial values for mailing event page.
     *
     * @return Record
     */
    public Record getInitialValuesForQuestionnaireMailingEvent();

    /**
     * Load all questionnaires for current mailing event.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllQuestionnaireForMailingEvent(Record inputRecord);

    /**
     * Store the error message.
     *
     * @param inputRecords
     * @return RecordSet
     */
    public RecordSet processSaveAllMailingQuestionnare(RecordSet inputRecords);

     /**
     * Add renewal questionnaire
     *
     * @param inputRecord
     * @return RecordSet
     */
    public void addRenewalQuestionnaire(Record inputRecord);

    /**
     * Print renewal questionnaire.
     *
     * @param inputRecord
     */
    public void performPrintRenewalQuestionnare(Record inputRecord);

    /**
     * Get the initial values for renewal questionnaire response page.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForQuestionnaireResponse(Record inputRecord);

    /**
     * Get the questionnaire response dates.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getResponseDate(Record inputRecord);

    /**
     * Save the questionnaire response dates.If system fails to save date, system rools back.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveResponseDate(Record inputRecord);

    /**
     * Save the questionnaire responses.If system fails to save date, system rools back.
     *
     * @param inputRecord
     */
    public void saveResponses(Record inputRecord);

    /**
     * Save the app status.
     *
     * @param inputRecord
     */
    public void saveAppStatus(Record inputRecord);

    /**
     * Get the app status.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getAppStatus(Record inputRecord);

    /**
     * Get the web app values.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getWebAppValues(Record inputRecord);

    /**
     * Get the web app rules.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getWebAppRules(Record inputRecord);

    /**
     * Get the web app url.
     *
     * @param inputRecord
     * @return String
     */
    public String getWebAppUrl(Record inputRecord);

    /**
     * Get the return value of data changed.
     *
     * @param inputRecord
     * @return YesNoFlag
     */
    public YesNoFlag isDataChanged(Record inputRecord);
}
