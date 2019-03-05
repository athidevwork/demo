package dti.pm.riskmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LoadProcessor;
import dti.pm.policymgr.PolicyHeader;

import java.util.Map;

/**
 * Interface to handle Implementation of Risk Manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/18/2008       yhyang      Added loadAllInsuredHistory().
 * 06/07/2010       Dzhang      Added loadAllProcedureCode().
 * 08/03/2010       syang       103793 - Added getPrimaryCoverage(), loadAllRiskSurchargePoint() and saveAllRiskSurchargePoint().
 * 08/31/2010       dzhang      108261 - Added getAllFieldForCopyAll(), modified loadAllTargetRisk().
 * 01/19/2011       wfu         113566 - Added getInitialValuesForCopyNewPolicy(), copyNewPolicyFromRisk().
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 06/29/2012       tcheng      133964 - Added loadAllInsuredInfo().
 * 07/17/2012       sxm         Issue 135029 - Added new logic to get Go To Risk List for Coverage class from back end
 *                                             to improve performance
 * 07/24/2012       awu         129250 - Added processAutoSaveAllRisk().
 * 07/31/2013       hxu         146027 - Added new logic to get Go To Risk List for Coverage form back end to
 *                                       improve performance.
 * 12/27/2013       xnie        148083 - 1) Added loadAllRiskSummaryOrDetail() to load risk summary or detail
 *                                          information.
 *                                       2) Added loadAllPracticeState() to load available practice state list.
 *                                       3) Added getRiskDetailId() to get risk detail id of updated record based on
 *                                          gaven risk id/transaction/term eff/exp date.
 * 11/13/2014       kxiang      157730 - Modified loadAllProcedureCode to remove formal parameter policyHeader.
 * 11/21/2014       kxiang      158495 - Removed loadAllPracticeState, as it's not used any more.
 * 11/20/2014       awu         154316 - Added getRiskSequenceId for Policy Change Service running.
 * 05/07/2016       wdang       157211 - Overload isEffectiveToDateEditable with additional input parameter.
 * 09/04/2015       tzeng       164679 - Added isAutoRiskRelConfigured() to check if auto risk relation is configured
 *                                       from cache.
 * 01/21/2016       tzeng       166924 - Added isAlternativeRatingMethodEditable().
 * 01/28/2016       wdang       169024 - Removed isAutoRiskRelConfigured.
 * 08/12/2016       eyin        177410 - Added validateTempCovgExist() and performAutoDeleteTempCovgs().
 * 07/17/2017       wrong       168374 - Added loadIsFundStateValue(), getDefaultValueForPcfCounty() and
 *                                       getDefaultValueForPcfRiskClass().
 * 04/02/2018       tzeng       192229 - Added isAddtlExposureAvailable.
 * 11/09/2018       wrong       194062 - Enhance saveDefaultRisk.
 * ---------------------------------------------------
 */
public interface RiskManager {
    /**
     * Load the RiskHeader bean of the PolicyHeader object with either the requested
     * risk information, or the primary risk if no specific request was made.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param riskId       primary key value for the desired risk information
     * @return PolicyHeader input PolicyHeader object now loaded with risk information
     */
    PolicyHeader loadRiskHeader(PolicyHeader policyHeader, String riskId);

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRisk(PolicyHeader policyHeader);

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRisk(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor);

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param processEntitlements true/false indicator for processing entitlements
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRisk(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, boolean processEntitlements);

    /**
     * Returns a RecordSet loaded with list of available risk summary or detail for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRiskSummaryOrDetail(PolicyHeader policyHeader);

    /**
     * Returns a RecordSet loaded with list of available risk summary or detail for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param riskDetailId risk detail id
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRiskSummaryOrDetail(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, String riskDetailId);

    /**
     * Returns a RecordSet loaded with list of available risk summary or detail for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param riskDetailId risk detail id
     * @param processEntitlements true/false indicator for processing entitlements
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRiskSummaryOrDetail(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, String riskDetailId, boolean processEntitlements);

    /**
     * Returns a RecordSet loaded with list of risks that have coverage class defined
     * for the provided policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRiskWithCoverageClass(PolicyHeader policyHeader);

    /**
     * Returns a RecordSet loaded with list of risks that have coverage defined
     * for the provided policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRiskWithCoverage(PolicyHeader policyHeader);

    /**
     * Returns a RecordSet loaded with list of existing risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param record       record
     * @param processor    to add select check box
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllExistingRisk(PolicyHeader policyHeader, Record record, RecordLoadProcessor processor);


    /**
     * AJAX retrieval of risk additional information fields.
     *
     * @param inputRecord a record with the passed request values.
     * @return Record output record containing the additional info fields
     */
    Record loadRiskAddlInfo(Record inputRecord);

    /**
     * load all risk summary
     *
     * @param inputRecord input records that contains key infomation
     * @return risk summary
     */
    public RecordSet loadAllRiskSummary(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Wrapper to invoke the save of all inserted/updated Risk records and subsequently
     * to invoke the save transaction logic for WIP, OFFICIAL, ENDQUOTE, RENQUOTE, DECLINE
     *
     * @param policyHeader the summary policy information corresponding to the provided risks.
     * @param inputRecords a set of Records, each with the updated Risk Detail info
     *                     matching the fields returned from the loadAllRisk method.
     * @return the number of rows updated.
     */
    int processSaveAllRisk(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Wrapper to invoke the auto save of all inserted/updated Risk records and subsequently
     *
     * @param policyHeader the summary policy information corresponding to the provided risks.
     * @param inputRecords a set of Records, each with the updated Risk Detail info
     *                     matching the fields returned from the loadAllRisk method.
     * @return the number of rows updated.
     */
    int processAutoSaveAllRisk(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Returns a RecordSet loaded with list of available risk types for the provided
     * policy information.
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of available risk types.
     */
    RecordSet loadAllRiskType(Record inputRecord);

    /**
     * Returns a String that contains "add code" for a given risk type.
     *
     * @param inputRecord Record contains input values
     * @return String a String contains an add code.
     */
    String getAddCodeForRisk(Record inputRecord);

    /**
     * Validate for adding risk.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     */
    void validateForAddRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     *                     <p/>
     * @return Record a Record loaded with initial values.
     */
    Record getInitialValuesForAddRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of locations for the given entity ID
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of locations.
     */
    RecordSet loadAllLocation(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Create default risk for a newly created policy
     *
     * @param policyHeader the summary policy information
     * @param inputRecord  a record loaded with data passed from select policy page
     * @return the number of rows added.
     */
    int saveDefaultRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * check if the effective to date of a risk type can be changed.
     *
     * @param inputRecord
     * @return String a String contains an add code.
     */
    String isDateChangeAllowed(Record inputRecord);

    /**
     * Check if risk effective to date is editable
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    YesNoFlag isEffectiveToDateEditable(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Check if risk effective to date is editable
     *
     * @param policyHeader
     * @param inputRecord
     * @param isDateChangeAllowed
     * @return
     */
    YesNoFlag isEffectiveToDateEditable(PolicyHeader policyHeader, Record inputRecord, boolean isDateChangeAllowed);

    /**
     * Check if Change option is available
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with the selected risk record.
     */
    void validateForOoseRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get initial values for OOSE risk
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values
     */
    Record getInitialValuesForOoseRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get initial values for slot occupant
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    Record getInitialValuesForSlotOccupant(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get initial values for copy new policy
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    Record getInitialValuesForCopyNewPolicy(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Method that copy policy to risk based on input Record.
     *
     * @param  polNoRec Record that contains new policy information
     * @param  inputRecord Record that contains new policy information
     * @return String contains policy number
     */
    public String copyNewPolicyFromRisk(Record polNoRec, Record inputRecord);

    /**
     * To get risk expiration date if risk is a date change allowed risk.
     *
     * @param inputRecord
     * @return
     */
    String getRiskExpDate(Record inputRecord);

    /**
     * get Fte facililty count for opion availability
     *
     * @param inputRecord intput record
     * @return the number of facility count
     */
    int getFteFacilityCount(Record inputRecord);

    /**
     * load all target risks for copy all risk
     *
     * @param policyHeader
     * @param inputRecord
     * @param loadProcessor
     * @param riskFormFields all the risk form fields
     * @return taget risks recordset
     */
    public RecordSet loadAllTargetRisk(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor, String riskFormFields);


    /**
     * validate source risk for copy risk all
     *
     * @param policyHeader
     * @param inputRecord
     * @return validate result
     */
    String validateRiskCopySource(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate risk copy
     * @param inputRecord
     * @return validate status code statusCode
     */
    String validateCopyAllRisk(Record inputRecord);
    
    /**
     * to get target coverage base ID if source coverage was not selected for processing
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    String getRiskCopyTargetCoverageBaseId(PolicyHeader policyHeader, Record inputRecord);

    /**
     * copy all risks
     *
     * @param policyHeader
     * @param covgRs
     * @param compRs
     * @param covgClassRs
     * @param coiRs
     * @param affiRs
     * @param inputRecord
     * @return outputRecord
     */
    Record copyAllRisk(PolicyHeader policyHeader, RecordSet covgRs, RecordSet compRs,RecordSet covgClassRs, RecordSet coiRs, RecordSet affiRs,RecordSet scheduleRs,Record inputRecord);

     /**
     * delete risk all
     *
     * @param policyHeader
     * @param covgRs
     * @param compRs
     * @param covgClassRs
     * @param inputRecord
     * @return outputRecord
     */
    Record deleteAllCopiedRisk(PolicyHeader policyHeader, RecordSet covgRs, RecordSet compRs,
                       RecordSet covgClassRs, Record inputRecord);

    /**
     * To load all addresses and phone numbers for the selected risk
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadAllAddressPhone(Record inputRecord);

    /**
     * To load all copy to risks and the addresses and phone numbers will be copied into it.
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadAllRiskForCopyAddressPhone(Record inputRecord);

    /**
     * To copy all source risk's addresses and phone numbers to copy-to risks
     *
     * @param inputRecord
     * @return
     */
    Record copyAllAddressPhone(Record inputRecord);


    /**
     * delete all risk
     * @param policyHeader policy header
     * @param inputRecords input records
     * @return process count
     */
    int deleteAllRisk(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * To get risk base id
     *
     * @param record inputParameters?
     * @return risk base ID
     */
    public String getRiskBaseId(Record record);

    /**
     * Returns a RecordSet loaded with list of insured history.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllInsuredHistory(Record inputRecord);

    /**
     * Get risk generic type
     * @param inputRecord
     * @return String
     */
    public String getGenericRiskType(Record inputRecord);

    /**
     * Get all merged risks
     *
     * @param policyHeader
     * @param inputRecord
     * @param existingRecords
     * @return RecordSet
     */
    public RecordSet getAllMergedRisk(PolicyHeader policyHeader, Record inputRecord, RecordSet existingRecords);

    /**
     * Get entity owner id for location.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getEntityOwnerId(Record inputRecord);

    /**
     * Validate If Any Temp Coverage exists under the Risk.
     *
     * @param inputRecord
     * @return Record
     */
    public Record validateTempCovgExist(Record inputRecord);

    /**
     * Delete temp coverages automatically after issue state was changed.
     *
     * @param inputRecord
     */
    public void performAutoDeleteTempCovgs(Record inputRecord);

    /**
     * Load all procedure code for risk
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllProcedureCode(Record inputRecord, LoadProcessor loadProcessor);

   /**
     * Returns the primary coverage id of current risk.
     *
     * @param inputRecord
     * @return String
     */
    public String getPrimaryCoverageId(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of risk surcharge point.
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRiskSurchargePoint(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Save all risk surcharge point.
     *
     * @param inputRecords
     * @return the number of rows updated.
     */
    int saveAllRiskSurchargePoint(RecordSet inputRecords);

    /**
     * Get all copy all configured fields.
     *
     * @return the Map contains config fields.
     */
    Map getAllFieldForCopyAll();

    /**
     * To validate reinstate ibnr risk
     *
     * @param inputRecord
     * @return
     */
    Record valReinstateIbnrRisk(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param sourceId  the id of the risk that needs to be retrieved, if available.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRiskForWs(PolicyHeader policyHeader, String sourceId);

    /**
     * Returns a RecordSet loaded with list of insured information.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllInsuredInfo(Record inputRecord);

    /**
     * Returns risk detail id.
     * <p/>
     *
     * @param inputRecord
     * @return the risk detail id
     */
    public String getRiskDetailId(Record inputRecord);

    /**
     * Return a new risk id
     * @return
     */
    public String getRiskSequenceId();

    /**
     * Check if alternative rating method is editable.
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public YesNoFlag isAlternativeRatingMethodEditable(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Load isFundState field value.
     *
     * @param inputRecord
     * @return Record
     */
    public Record loadIsFundStateValue(Record inputRecord);

    /**
     * Get default pcf risk county field value.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getDefaultValueForPcfCounty(Record inputRecord);

    /**
     * Get default pcf risk class field value.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getDefaultValueForPcfRiskClass(Record inputRecord);

    /**
     * Check if the risk exposure navigation item is available.
     * @param inputRecord
     * @return
     */
    public boolean isAddtlExposureAvailable (Record inputRecord,
                                             PolicyHeader policyHeader);
}
