package dti.pm.coveragemgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.coveragemgr.CoverageHeader;
import dti.pm.policymgr.PolicyHeader;

/**
 * An interface that provides DAO operation for Coverage information.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 10, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/07/2008       fcb         getCoverageLimitShared added.
 * 01/27/2011       dzhang      116359 - Add isAddCoverageAllowed().
 * 05/18/2011       dzhang      117246 - Added getShortTermCoverageEffAndExpDates().
 * 08/07/2013       awu         146878 - Added getCurrentCoverageStatus.
 * 01/24/2014       jyang       150639 - Move getCoverageExpirationDate to ComponentDAO.
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Coverage.
 * 10/12/2014       jyang       157749 - Added getRiskContiguousEffectiveDate().
 * 09/16/2014       awu         157552 - Add validateCoverageDuplicate.
 * 12/26/2014       xnie        156995 - Added loadAllCoverageByIds() to load all coverages based on gaven ID list.
 * 07/11/2016       lzhang      177681 - Add loadAllNewCopiedCMCoverage and saveAllRetroDate method.
 * ---------------------------------------------------
 */

public interface CoverageDAO {

        /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader an instance of the PolicyHeader object with the current policy/term details loaded
     * @param inputRecord  record containting input parameters including an option risk id.
     * @return CoverageHeader an instance of the CoverageHeader object loaded
     */
    CoverageHeader loadCoverageHeader(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     * @param inputRecord record with policy/risk key fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    RecordSet loadAllCoverage(Record inputRecord, RecordLoadProcessor recordLoadProcessor);
    /**
     * Load all coverage summary by policy id
     * @param inputRecord input record that contains policy id
     * @return coverage summary
     */
    RecordSet loadAllCoverageSummary(Record inputRecord);
    /**
     * Returns a Record with the additional info 1,2,3 fields for the particular coverage and term dates.
     * <p/>
     * @param inputRecord record with coverageId and term dates.
     * @return Record a Record with the Addl Info fields if configured via system parameters.
     */
    public Record loadCoverageAddlInfo(Record inputRecord);

    /**
     * Save all given input records with the Pm_Nb_End.Save_Coverage stored procedure,
     * assuming they all have recordModeCode = TEMP, were added during this WIP transaction
     * (ie. have not been saved as Official yet).
     * This assumes all input records have recordModeCode = TEMP.
     * Set the rowStatus field to NEW for records that are newly added in this request.
     * Set the rowStatus field to MODIFIED for records that have already been saved in this WIP transaction,
     * and are just being updated not.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    int addAllCoverage(RecordSet inputRecords);

    /**
     * Update all given input records with the Pm_Endorse.Change_Coverage stored procedure,
     * assuming they all have field recordModeCode = Official, and were marked as updated.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    int updateAllCoverage(RecordSet inputRecords);

    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Coverage stored procedure,
     * assuming they all have recordModeCode = TEMP, and were marked for delete.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    int deleteAllCoverage(RecordSet inputRecords);

    /**
     * Load all avaiable coverage
     *
     * @param inputRecord
     *@param recordLoadProcessor
     * @return RecordSet
     */
    RecordSet loadAllAvailableCoverage(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Load dependent coverage
     *
     * @param inputRecord
     * @return RecordSet
     */
    RecordSet loadDependentCoverage(Record inputRecord);

    /**
     * Get coverage Id and base ID.
     * @param inputRecord        Input record containing risk and coverage level details
     * @return Record that contains coverage Id and base ID.
     */
    public Record getCoverageIdAndBaseId(Record inputRecord);

    /**
     * Validate OOSWIP Retroactive Date
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public Record validationRetroactiveDateForOoswip(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Check if prior acts exist
     *
     * @param inputRecord
     * @return String
     */
    public String isPriorActsExist(Record inputRecord);

    /**
     * Get Retroactive date
     *
     * @param inputRecord
     * @return String
     */
    String getRetroactiveDate(Record inputRecord);

    /**
     * Get the ratingModuleCode for the copied record when oose coverage
     *
     * @param inputRecord
     * @return
     */
    String getRatingModuleCode(Record inputRecord);

    /**
     * Check if Change option is available or not by function Pm_Valid_Oos_Coverage
     *
     * @param inputRecord
     * @return
     */
    String isOosCoverageValid(Record inputRecord);

    /**
     * get validate practice state result
     * @param inputRecord
     * @return validate result
     */
    String getValidatePracticeStateResult(Record inputRecord);

    /**
     * check similar coverage
     * @param inputRecord
     * @return
     */
    String getCheckSimilarCoverageResult(Record inputRecord);

    /**
     * get coverage base record id
     * @param inputRecord
     * @return coverage base record id
     */
    String getCoverageBaseId(Record inputRecord);

    /**
     * validate coverage copy
     * @param inputRecord
     * @return validate status code statusCode
     */
    String validateCopyAllCoverage(Record inputRecord);

    /**
     * delete all coverage from risk for delete risk all
     * @param coverageRs
     */
    void deleteAllCopiedCoverage(RecordSet coverageRs);

    /**
     * get coverage shared limit indicator
     * @param inputRecord
     * @return Y/N indicator
     */
    String getCoverageLimitShared(Record inputRecord);

    /**
     * Load all the prior carrier
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPriorCarrier(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     *  Load the current carrier
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCurrentCarrier(Record inputRecord);

    /**
     *  Save all parior carrier
     *
     * @param inputRecords
     * @return the number of rows updated
     */
    public int saveAllPriorCarrier(RecordSet inputRecords);

    /**
     *  Insert a current carrier
     *
     * @param inputRecord
     */
    public void insertCurrentCarrier(Record inputRecord);

    /**
     *  Update the current carrier
     * 
     * @param inputRecord
     */
    public void updateCurrentCarrier(Record inputRecord);

    /**
     * Get product coverage type
     *
     * @param inputRecord
     * @return String
     */
    public String getProductCoverageType(Record inputRecord);

    /**
     * Return if manual excess button enable
     *
     * @param inputRecord
     * @return String
     */
    public String isManualExcessButtonEnable(Record inputRecord);

    /**
     * Check if add coverage allowed
     *
     * @param record
     * @return
     */
    String isAddCoverageAllowed(Record record);

    /**
     * Get short term coverage's effective from and effective to date
     *
     * @param inputRecord Input record containing risk and coverage level details
     * @return Record that contains coverage effective from date and effective to date.
     */
    Record getShortTermCoverageEffAndExpDates(Record inputRecord);

    /**
     * Get the coverage chain status
     *
     * @param inputRecord
     * @return
     */
    Record getCurrentCoverageStatus(Record inputRecord);

    /**
     * get risk start date
     * @param inputRecord
     * @return
     */
    String getRiskContiguousEffectiveDate(Record inputRecord);

    /**
     * Validate for coverage duplicate.
     *
     * @param inputRecord
     * @return
     */
    public Record validateCoverageDuplicate(Record inputRecord);

    /**
     * Get all coverages based on given ID list.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllCoverageByIds(Record inputRecord);

    /**
     * Get all coverages based on given coverage ID list.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllNewCopiedCMCoverage(Record inputRecord);

    /**
     * Save all retro date of new copied CM coverages
     * @param inputRecords
     * @return
     */
    public int saveAllRetroDate(RecordSet inputRecords);
}
