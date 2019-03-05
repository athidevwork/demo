package dti.pm.policymgr.applicationmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

import java.text.ParseException;
import java.util.Date;

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
 * 03/28/2017       tzeng       166929 - Added processInitiateApp, initiateAppForUI, getApplicationTypeCode
 *                                       getUtcExpirationDate.
 * ---------------------------------------------------
 */
public interface ApplicationManager {

    /**
     * Get the initial data for the page.
     * Get the application list for the currently selected term.
     *
     * @param inputRecord
     */
    public RecordSet loadApplicationList(Record inputRecord);

    /**
     * Porcess and generate questionnaire
     *
     * @param inputRecords
     */
    public void processQuestionnaireRequest(RecordSet inputRecords);

    /**
     * Load all applications
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllApplication(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Save all applications
     *
     * @param inputRecord
     * @param inputRecords
     */
    public void saveAllApplication(Record inputRecord, RecordSet inputRecords);

    /**
     * Load all change history
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllHistory(Record inputRecord);

    /**
     * Load all available app reviewer
     *
     * @return RecordSet
     */
    RecordSet loadAllAvailableAppReviewer();

    /**
     * Call eApp to initiate an application.
     * @param inputRecord it need to include policy PK/policy no and term base id at the least.
     * @param userName
     * @return the record include the field applicationId and the field initResult of initializing application.
     *   applicationId:
     *     A valid application id, otherwise, it would be null.
     *   initResult:
     *     Three following types in ApplicationFields are returned as the result of initializing application.
     *     EAPP_PM_INIT_SUCCESS, create new application successfully.
     *     APP_INIT_EXISTED, the type of application already exists for the current term.
     *     EAPP_PM_INIT_ERROR, an error occurred when creating new application.
     */
    public Record processInitiateApp(Record inputRecord, String userName);

    /**
     * Initiate an application for UI.
     * @param inputRecord
     * @param userName
     */
    public void initiateAppForUI(Record inputRecord, String userName);

    /**
     * Get application type.
     * @param inputRecord
     * @return NBAPP or RENAPP
     */
    public String getApplicationTypeCode(Record inputRecord);

    /**
     * Helper method to generate a UTC expiration date.
     * @param timeoutInMinutes
     * @return
     * @throws ParseException
     */
    public Date getUtcExpirationDate(int timeoutInMinutes) throws ParseException;
}
