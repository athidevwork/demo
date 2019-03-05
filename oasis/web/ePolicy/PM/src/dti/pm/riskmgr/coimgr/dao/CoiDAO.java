package dti.pm.riskmgr.coimgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;

/**
 * An interface that provides DAO operation for COI information.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 5, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/25/2008       fcb         getNoteByNoteCode added.
 * 06/06/2013       adeng       Added one more parameter Record inputRecord for method copyAllCoi().
 * 10/28/2016       lzhang      180689 Modified saveAllCoiHolder: add policyHeader as input parameter
 * 09/12/2017       wrong       187839 Added function generateCoiForWS().
 * ---------------------------------------------------
 */
public interface CoiDAO {

    /**
     * To calculate dates for load COI holder.
     * <p/>
     *
     * @param inputRecord
     * @return
     */
    Record calculateDateForCoi(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of available COI Holders for the provided
     * risk information.
     * <p/>
     *
     * @param inputRecord         record with risk fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available COI Holders.
     */
    RecordSet loadAllCoiHolder(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Save all COI Holder informations.
     * <p/>
     *
     * @param inputRecords intput records
     * @return the number of row updateds
     */
    int saveAllCoiHolder(RecordSet inputRecords, PolicyHeader policyHeader);

    /**
     * If not slot risk type, caculate effective from date and effective to date by stored procedure "Pm_Sel_Date_Range".
     * <p/>
     *
     * @param inputRecord record with riskBaseId and type
     * @return a Record loaded with the effective from date and effective to date.
     */
    Record loadDateRangeForCoiHolder(Record inputRecord);

    /**
     * Load the original contiguous period risk start date by stored procedure "Pm_Dates.Nb_Risk_Startdt".
     * <p/>
     *
     * @param inputRecord record with riskBaseId and the selected risk effecitve date
     * @return a Record loaded with risk effective from date.
     */
    Record loadOrigContiguousRiskEffFromDate(Record inputRecord);

    /**
     * Load the cutoff date for COI Claims
     *
     * @return a Record loaded with cutoff date.
     */
    Record loadCutoffDateForCoiClaim();

    /**
     * Generate all COI.
     *
     * @param inputRecord input coi data.
     */
    void generateAllCoi(Record inputRecord);

    /**
     * To get actual term expiration date.
     *
     * @param inputRecord record with external id
     * @return actual term expiration date
     */
    String getActualExpDate(Record inputRecord);

    /**
     * To process all Client COI.
     *
     * @param inputRecord intput record
     * @return record with return code and return message
     */
    Record processAllCoi(Record inputRecord);


    /**
     * copy all coi data to target risk
     * @param inputRecords
     */
    void copyAllCoi(RecordSet inputRecords, Record inputRecord);

    /**
     * Method to get the notes based on the note code.
     * @param inputRecord
     * @return
     */
    Record getNoteByNoteCode(Record inputRecord);

    /**
     * Method that generate Coi information base on input record.
     * <p/>
     * @param  inputRecord
     * @return record with return message information.
     */
    Record generateCoiForWS(Record inputRecord);
}
