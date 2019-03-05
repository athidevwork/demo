package dti.pm.coverageclassmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of Coverage Class Manager.
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
 * 04/13/2007       Joe         Add parameter RecordLoadProcessor for method loadAllAvailableCoverageClass()
 * 09/07/2010       dzhang      Add parameter covgClassGridFields for methoed loadAllSourceCoverageClass().
 * 06/08/2011       wqfu        121349 - Add isAddCoverageClassAllowed for short term coverage class.
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 07/24/2012       awu         129250 - Added processAutoSaveAllCoverageClass().
 * 04/26/2013       awu         141758 - Added addAllCoverageClass().
 * 05/24/2013       xnie        142949 - Added validateCopyAllCoverageClass().
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Sub_Covg
 * ---------------------------------------------------
 */

public interface CoverageClassManager {

    public static final String ADD_COVERAGE_CLASS_ACTION_CLASS_NAME = "dti.pm.coverageclassmgr.struts.MaintainCoverageClassAction";

    /**
     * Returns a RecordSet loaded with list of available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available coverage classes.
     */

    RecordSet loadAllCoverageClass(PolicyHeader policyHeader);

    /**
     * Returns a RecordSet loaded with list of available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available coverage classes.
     */
    RecordSet loadAllCoverageClass(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor);

    /**
     * Returns a RecordSet loaded with list of available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param ownerRecords coverage records
     * @param covgClassGridFields all the coverage class gird fields
     * @return RecordSet a RecordSet loaded with list of available coverage classes.
     */
    public RecordSet loadAllSourceCoverageClass(PolicyHeader policyHeader,RecordSet ownerRecords, RecordLoadProcessor loadProcessor, String covgClassGridFields);

    /**
     * Wrapper to invoke the save of all inserted/updated Coverage Classrecords and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader the summary policy information corresponding to the provided coverage classes.
     * @param inputRecords a set of Records, each with the updated Coverage Class Detail info
     *                     matching the fields returned from the loadAllCoverageClass method.
     * @return the number of rows updated.
     */
    public int processSaveAllCoverageClass(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Wrapper to invoke the auto save of all inserted/updated Coverage Class records and subsequently
     *
     * @param policyHeader the summary policy information corresponding to the provided coverage classes.
     * @param inputRecords a set of Records, each with the updated Coverage Class Detail info
     *                     matching the fields returned from the loadAllCoverageClass method.
     * @return the number of rows updated.
     */
    public int processAutoSaveAllCoverageClass(PolicyHeader policyHeader, RecordSet inputRecords);
    /**
     * Load all available coverage class
     *
     * @param policyHeader
     * @return
     */
    RecordSet loadAllAvailableCoverageClass(PolicyHeader policyHeader);

    /**
     * Returns a RecordSet loaded with list of all available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information
     * @param loadProcessor
     * @return RecordSet a RecordSet loaded with all list of available coverage class.
     */
    RecordSet loadAllAvailableCoverageClass(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor);

    /**
     * Get initial values for coverage class
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForCoverageClass(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Save all default coverage class
     *
     * @param policyHeader
     * @return update count
     */
    public int saveAllDefaultCoverageClass(PolicyHeader policyHeader);

    /**
     * Check if Change option is available
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord a record loaded with the selected risk record.
     */
    public void validateForOoseCoverageClass(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get initial values for OOSE coverage class
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForOoseCoverageClass(PolicyHeader policyHeader, Record inputRecord);

    /**
     * delete all copied coverage class
     * @param policyHeader
     * @param inputRecord
     * @param covgClassRs
     */
    public void deleteAllCopiedCoverageClass(PolicyHeader policyHeader, Record inputRecord, RecordSet covgClassRs);

    /**
     * Method that evaluates policy business rule for ability to enter the annual base rate
     * depending if the rating module is manually rated.
     *
     * @param ratingModuleCode Rating module identifier of the current coverage
     * @return boolean indicating if the coverage is manually rated
     */
    public boolean isManuallyRated(String ratingModuleCode);

    /**
     * Check if add coverage class is allowed
     *
     * @param record
     * @return
     */
    public YesNoFlag isAddCoverageClassAllowed(Record record);

    /**
     * Returns a RecordSet loaded with list of available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param insuredId the source id
     * @return RecordSet a RecordSet loaded with list of available coverage classes.
     */
    public RecordSet loadAllCoverageClassForWs(PolicyHeader policyHeader, String insuredId);

    /**
     * Add the selected coverage classes.
     * @param policyHeader
     * @param inputRecord
     * @param coverageClassRecSet
     */
    public void addAllCoverageClass(PolicyHeader policyHeader, Record inputRecord, RecordSet coverageClassRecSet);

    /**
     * validate coverage class copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllCoverageClass(Record inputRecord);
}
