package dti.pm.riskmgr.coimgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of COI Manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 6, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/25/2008       fcb         getNoteByNoteCode added.
 * 06/06/2013       adeng       Added one more parameter Record inputRecord for method copyAllCoi().
 * ---------------------------------------------------
 */
public interface CoiManager {
    /**
     * Returns a RecordSet loaded with list of COI Holder for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of COI Holder.
     */

    RecordSet loadAllCoiHolder(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor);

    /**
     * Save all inserted/updated COI Holder records.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the updated COI Holder Detail info
     *                     matching the fields returned from the loadAllCoiHolder method.
     */
    void saveAllCoiHolder(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Get initial values for COI Holder
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return Record a Record loaded with initial values.
     */
    Record getInitialValuesForCoiHolder(PolicyHeader policyHeader, Record inputRecord);

    /**
     * To validate the As of Date
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with the as of date value
     */
    void validateAsOfDateForGenerateCoi(PolicyHeader policyHeader, Record inputRecord);

    /**
     * To load cutoff date for COI Claims History page
     *
     * @return Record a Record loaded with cutoff date value.
     */
    Record loadCutoffDateForCoiClaim();

    /**
     * Generate all COI.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord a Record with the details of COI claims data.
     */
    void generateAllCoi(PolicyHeader policyHeader, Record inputRecord);

    /**
     * To derive the minimum and maximum dates for each entity role record.
     *
     * @param inputRecords
     */
    void deriveMinAndMaxDates(RecordSet inputRecords);

    /**
     * To validate the As of Date for generate client coi
     *
     * @param inputRecords the selected COI Holder list
     * @return comma delimited policyList string if invalid records exist, "" if invalid record not exist.
     */
    String validateAsOfDateForProcessCoi(RecordSet inputRecords);

    /**
     * To process all client COI.
     *
     * @param inputRecords a set of Records, each with the selected COI Holder info from CIS
     *                     and other info like entityId, values from Process COI Claim etc.
     */
    void processAllCoi(RecordSet inputRecords);

    /**
     * copy all coi data to target risk
     *
     * @param inputRecords
     */
    public void copyAllCoi(RecordSet inputRecords, Record inputRecord);

    /**
     * Method to return the notes information from Ajax.
     * @param policyHeader
     * @param inputRecord
     */
    public void getNoteByNoteCode(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Method that generate Coi information base on input record.
     * <p/>
     * @param  inputRecord Record that contains generate coi information parameters.
     * @return new created transaction log fk.
     */
    public String generateCoiForWS(Record inputRecord);
}
