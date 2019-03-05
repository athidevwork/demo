package dti.pm.coverageclassmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for Coverage Class information.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 8, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/08/2011       wqfu        121349 - Add isAddCoverageClassAllowed and getShortTermCoverageClassEffAndExpDates
 * 05/24/2013       xnie        142949 - Added validateCopyAllCoverageClass().
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Sub_Covg
 * 09/16/2014       awu         157552 - Add validateCoverageClassDuplicate;
 * ---------------------------------------------------
 */

public interface CoverageClassDAO {

    /**
     * Returns a RecordSet loaded with list of available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param inputRecord         record with policy, risk, coverage key fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available coverage classes.
     */
    RecordSet loadAllCoverageClass(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Save all given input records with the Pm_Nb_End.Save_sub_Coverage stored procedure,
     * assuming they all have recordModeCode = TEMP, were added during this WIP transaction
     * (ie. have not been saved as Official yet).
     * This assumes all input records have recordModeCode = TEMP.
     * Set the rowStatus field to NEW for records that are newly added in this request.
     * Set the rowStatus field to MODIFIED for records that have already been saved in this WIP transaction,
     * and are just being updated not.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Class Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    int addAllCoverageClass(RecordSet inputRecords);

    /**
     * Update all given input records with the Pm_Endorse.Change_Sub_Coverage stored procedure,
     * assuming they all have field recordModeCode = Official, and were marked as updated.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Class Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    int updateAllCoverageClass(RecordSet inputRecords);

    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Coverage stored procedure,
     * assuming they all have recordModeCode = TEMP, and were marked for delete.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Class Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    int deleteAllCoverageClass(RecordSet inputRecords);

    /**
     * Load all available coverage class
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    RecordSet loadAllAvailableCoverageClass(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Get coverage class Id and base Id.
     *
     * @param inputRecord Input record containing coverageBaseRecordId and productCoverageCode infor.
     * @return Record that contains coverage class Id and base Id.
     */
    public Record getCoverageClassIdAndBaseId(Record inputRecord);

    /**
     * Get coverage class exposure information
     *
     * @param inputRecord
     * @return
     */
    public String getExposureInfo(Record inputRecord);

    /**
     * Check if Change option is available or not by function Pm_Valid_Oos_Coverage
     *
     * @param inputRecord
     * @return
     */
    String isOosCoverageClassValid(Record inputRecord);

    /**
     * delete all coverage class from risk for delete risk all
     * @param covgClassRs
     */
    void deleteAllCopiedCoverageClass(RecordSet covgClassRs);

    /**
     * Check if add coverage class is allowed
     *
     * @param record
     * @return
     */
    String isAddCoverageClassAllowed(Record record);

    /**
     * Get short term coverage class's effective from and effective to date
     *
     * @param inputRecord Input record containing coverage level details
     * @return Record that contains coverage class effective from date and effective to date.
     */
    Record getShortTermCoverageClassEffAndExpDates(Record inputRecord);

    /**
     * validate coverage class copy
     * @param inputRecord
     * @return validate status code statusCode
     */
    String validateCopyAllCoverageClass(Record inputRecord);

    /**
     * Validate coverage class duplicate.
     * @param inputRecord
     * @return
     */
    Record validateCoverageClassDuplicate(Record inputRecord);
}

