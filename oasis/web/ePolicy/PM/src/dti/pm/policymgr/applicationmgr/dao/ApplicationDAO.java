package dti.pm.policymgr.applicationmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

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
* 03/28/2017       tzeng       166929 - Added HasApplicationB, recordDiaryForApplication.
* ---------------------------------------------------
*/
public interface ApplicationDAO {

    /**
     * Select terms for a policy.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTerms(Record inputRecord);

    /**
     * Get the list of applications for a given policy term.
     *
     * @param inputRecord
     * @return RecordSet
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
     * @param inputRecords
     */
    public void saveAllApplication(RecordSet inputRecords);

    /**
     * Save change history
     *
     * @param inputRecord
     */
    public void saveHistory(Record inputRecord);

    /**
     * Load all change history
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllHistory(Record inputRecord);

    /**
     * To load all eApp reviewers
     * @param inputRecord
     * @return
     */
    RecordSet loadAllAppReviewer(Record inputRecord);

    /**
     * Check if it exists application upon term base and form type.
     * @param inputRecord
     * @return
     */
    public boolean hasApplicationB(Record inputRecord);

    /**
     * Record diary for initializing application.
     * @param inputRecord
     */
    public void recordDiaryForApplication(Record inputRecord);

}
