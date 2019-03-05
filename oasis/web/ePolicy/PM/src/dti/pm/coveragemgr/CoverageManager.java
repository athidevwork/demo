package dti.pm.coveragemgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LoadProcessor;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of Coverage Manager.
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
 * 04/07/2008       fcb         setCoverageLimitShared added.
 * 09/07/2010       dzhang      108261 - Modified loadAllSourceCoverage() to add a new parameter
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 07/24/2012       awu         129250 - Added processAutoSaveAllCoverageAndComponent().
 * 04/24/2013       awu         141758 - Changed addAllDefaultComponent to addAllCoverages to add all coverages,
 *                                       Added addAllComponent().
 * 10/02/2013       fcb         145725 - isProblemPolicy: changed the parameter.
                                       - added overloaded loadAllCoverage
 * 01/24/2014       jyang       150639 - Move getCoverageExpirationDate to ComponentManager.
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Coverage
 * 11/20/2014       awu         154316 - Added getCoverageSequenceId for Policy Change Service using.
 * 07/11/2016       lzhang      177681 - Add loadAllNewCopiedCMCoverage and saveAllRetroDate method.
 * ---------------------------------------------------
 */

public interface CoverageManager {

    public static final String ADD_COVERAGE_ACTION_CLASS_NAME = "dti.pm.coveragemgr.struts.MaintainCoverageAction";

    /**
     * Load the CoverageHeader bean of the PolicyHeader object with either the requested
     * coverage information, or the primary coverage if no specific request was made.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param coverageId   primary key value for the desired coverage information
     * @return PolicyHeader input PolicyHeader object now loaded with coverage information
     */
    PolicyHeader loadCoverageHeader(PolicyHeader policyHeader, String coverageId);

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    RecordSet loadAllCoverage(PolicyHeader policyHeader);

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param processEntitlements true/false indicator for processing entitlements
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllCoverage(PolicyHeader policyHeader, boolean processEntitlements);

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    RecordSet loadAllCoverage(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor);

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param processEntitlements true/false indicator for processing entitlements
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllCoverage(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, boolean processEntitlements );

    /**
     * Load all coverage summary by policy id
     * @param inputRecord input record that contains policy id
     * @return coverage summary
     */
    RecordSet loadAllCoverageSummary(Record inputRecord);

    /**
     * AJAX retrieval of coverage additional information fields.
     * @param policyHeader
     * @param inputRecord a record with the passed request values.
     * @return Record output record containing the additional info fields
     */
    public Record loadCoverageAddlInfo(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Wrapper to invoke the save of all inserted/updated Coverage records and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader     the summary policy information corresponding to the provided coverages.
     * @param inputRecords     a set of Records, each with the updated Coverage Detail info
     *                         matching the fields returned from the loadAllCoverage method.
     * @param componentRecords a set of Records, each with the updated Component Detail info
     *                         matching the fields returned from the ComponentManager.loadAllComponents method.
     * @return the number of rows updated.
     */
    public int processSaveAllCoverageAndComponent(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet componentRecords);

     /**
     * Wrapper to invoke the auto save of all inserted/updated Coverage records and subsequently
     *
     * @param policyHeader     the summary policy information corresponding to the provided coverages.
     * @param inputRecords     a set of Records, each with the updated Coverage Detail info
     *                         matching the fields returned from the loadAllCoverage method.
     * @param componentRecords a set of Records, each with the updated Component Detail info
     *                         matching the fields returned from the ComponentManager.loadAllComponents method.
     * @return the number of rows updated.
     */
    public int processAutoSaveAllCoverageAndComponent(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet componentRecords);

    /**
     * Load all available coverage without load processor
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllAvailableCoverage(PolicyHeader policyHeader);

    /**
     * Load All available coverages
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllAvailableCoverage(PolicyHeader policyHeader, LoadProcessor loadProcessor);

    /**
     * Load dependent Coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadDependentCoverage(PolicyHeader policyHeader, Record inputRecord);

    /**
     * get Initial value of coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForCoverage(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Create default coverages for a new risk
     *
     * @param policyHeader the summary policy information
     * @return the number of rows added.
     */
    public int saveAllDefaultCoverage(PolicyHeader policyHeader);

    /**
     * Check if prior acts exist
     *
     * @param policyHeader
     * @param inputRecord
     * @return boolean
     */
    public boolean validatePriorActsExist(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Method that evaluates policy business rule for ability to edit the Claims Made date,
     * updating the OasisField to editable if permitted.
     * @param policyHeader policy header that contains all key policy information.
     * @return boolean indicating if the claims made date field is editable
     */
    public boolean isClaimsMadeDateEditable(PolicyHeader policyHeader);

    /**
     * Method that evaluates policy business rule for ability to enter the annual base rate
     * depending if the rating module is manually rated.
     *
     * @param ratingModuleCode Rating module identifier of the current coverage
     * @return boolean indicating if the coverage is manually rated
     */
    public boolean isManuallyRated(String ratingModuleCode);

    /**
     * Check if coverage effective to date is editable
     *
     * @param policyHeader
     * @param record
     * @return
     */
    boolean isEffectiveToDateEditable(PolicyHeader policyHeader, Record record);

    /**
     * Check if Change option is available
     *
     * @param policyHeader
     * @param inputRecord
     */
    void validateForOoseCoverage(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get initial values for OOSE Coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    Record getInitialValuesForOoseCoverage(PolicyHeader policyHeader, Record inputRecord);


    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Coverage stored procedure,
     * assuming they all have recordModeCode = TEMP, and were marked for delete.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    int deleteAllCoverage(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * get a boolean value to indicate if practice state is valid
     *
     * @param policyHeader
     * @param inputRecord
     * @return validate result
     */
     boolean isPracticeStateValid(PolicyHeader policyHeader, Record inputRecord);

    /**
     * check is Similar Coverage Exist
     * @param policyHeader
     * @param inputRecord
     * @return
     */
     boolean isSimilarCoverageExist(PolicyHeader policyHeader, Record inputRecord);

    /**
     * get coverage base record id
      * @param policyHeader
     * @param inputRecord
     * @return coverage base record id
     */
    String getCoverageBaseId(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate coverage copy
     * @param inputRecord
     * @return validate status code statusCode
     */
    String validateCopyAllCoverage(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of source coverages for risk copy all
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param covgGridFields all the coverage gird fields
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    RecordSet loadAllSourceCoverage(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, String covgGridFields);

    /**
     * delete all copied coverage
     * @param pHeader
     * @param inputRecord
     * @param covgerageRs
     */
    void deleteAllCopiedCoverage(PolicyHeader pHeader, Record inputRecord, RecordSet covgerageRs);

    /**
     * sets the rule for coverage shared limit.
     * @param policyHeader
     * @param inputRecord
     */
    void setCoverageLimitShared(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Load all the prior carrier
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPriorCarrier(Record inputRecord);

    /**
     *  Load the current carrier
     * @param inputRecord
     * @return Record
     */
    public Record loadAllCurrentCarrier(Record inputRecord);

    /**
     *  Save all parior carrier
     * @param inputRecords
     * @return the number of rows updated
     */
    public int saveAllPriorCarrier(RecordSet inputRecords);

    /**
     *  Get initial values for prior carrier
     * @param inputRecord
     * @return Initial values
     */
    public Record getInitialValuesForPriorCarrier(Record inputRecord);

    /**
     * Get product coverage type
     *
     * @param productCoverageCode
     * @return String
     */
    public String getProductCoverageType(String productCoverageCode);

    /**
     * Check if manual excess button enable
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean isManualExcessButtonEnable(Record inputRecord);

    /**
     * Is Valid for Manual Excess Premium
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean isValidForManualExcessPremium(PolicyHeader policyHeader);

    /**
     * Check if it is a problem policy
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean isProblemPolicy(PolicyHeader policyHeader);

    /**
     * Add all covg, dependent covg and default components
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     * @param componentInputRecords
     */
    public void addAllCoverage(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords, RecordSet componentInputRecords);
    /**
     * Check if add coverage allowed
     *
     * @param record
     * @return
     */
    YesNoFlag isAddCoverageAllowed(Record record);

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param insuredId  the insured id of the records that need to be retrieved.
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllCoverageForWs(PolicyHeader policyHeader, String insuredId);

    /**
     * Add all component, dependent component
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public String addAllComponent(PolicyHeader policyHeader, Record inputRecord, RecordSet componentInputRecords);

    /**
     * Generate the new coverage id.
     * @return
     */
    public String getCoverageSequenceId();

    /**
     * Returns a RecordSet loaded with list of new copied CM coverages
     * coverage information.
     * <p/>
     *
     * @param inputRecord  record with new copied CM coverage IDs.
     * @return RecordSet a RecordSet loaded with list of new copied CM coverages.
     */
    public RecordSet loadAllNewCopiedCMCoverage(Record inputRecord);

    /**
     * Save all retro date of new copied CM coverages
     *
     * @param inputRecords
     */
    public int saveAllRetroDate(RecordSet inputRecords);
}
