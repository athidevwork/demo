package dti.pm.riskmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskHeader;

/**
 * An interface that provides DAO operation for risk information.
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
 * 07/10/2007       sxm         Moved getInitialDddwForRisk() into PMDefaultDAO
 * 11/18/2008       yhyang      Added loadAllInsuredHistory().
 * 06/07/2010       Dzhang      Added loadAllProcedureCode().
 * 08/03/2010       syang       103793 - Added getPrimaryCoverage(), loadAllRiskSurchargePoint() and saveAllRiskSurchargePoint().
 * 08/31/2010       dzhang      108261 - Added getAllFieldForCopyAll() and getParmsForCopyAll().
 * 01/19/2011       wfu         113566 - Added copyNewPolicyFromRisk() to handle copying policy from risk.
 * 01/21/2011       syang       105832 - Added getDisciplineDeclineEntityStatus() to retrive ddl status.
 * 06/29/2012       tcheng      133964 - Added loadAllInsuredInfo().
 * 07/17/2012       sxm         Issue 135029 - Added new logic to get Go To Risk List for Coverage class from back end
 *                                             to improve performance
 * 07/31/2013       hxu         146027 - Added new logic to get Go To Risk List for Coverage form back end to
 *                                       improve performance.
 * 08/06/2013       awu         146878 - Added getChainStatus.
 * 12/27/2013       xnie        148083 - 1) Added loadAllPracticeState() to load available practice state list.
 *                                       2) Added getRiskDetailId() to get risk detail id of updated record based on
 *                                          gaven risk id/transaction/term eff/exp date.
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_RiskStat
 * 09/16/2014       awu         157552 - Add validateRiskDuplicate;
 * 11/10/2014       kxiang      158495 - 1) Modified getDefaultPracticeState() to add a formal parameter.
 *                                       2) Modified getLocationAddress() to change the returned type.
 *                                       3) Removed loadAllPracticeState, as it's not used any more.
 * 12/24/2014       xnie        156995 - Added loadAllRiskByIds() to load all risks based on gaven ID list.
 * 09/04/2015       tzeng       164679 - 1) Added isAutoRiskRelConfigured() to check if auto risk relation is
 *                                          configured.
 *                                       2) Added processAutoRiskRelation() to process auto risk relation and return
 *                                          result.
 * 01/15/2016       tzeng       166924 - Added isAlternativeRatingMethodEditable.
 * 07/11/2016       lzhang      177681 - Modified 'RecordSet' return type for processCopyAll method instead of 'String'.
 * 08/12/2016       eyin        177410 - Added validateTempCovgExist() and performAutoDeleteTempCovgs().
 * 07/172017        wrong       168374 - Added method loadIsFundStateValue, getDefaultValueForPcfCounty and
 *                                       getDefaultValueForPcfRiskClass.
 * 06/08/2018       xnie        193805 - Added getRiskTypeDefinition() to get risk type description with slot/FTE ID.
 * 07/05/2018       ryzhao      187070 - Added three new methods.
 *                                       1) isGr1CompVisible 2) isGr1CompEditable 3) isGr2CompEditable
 * ---------------------------------------------------
 */
public interface RiskDAO {

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader an instance of the PolicyHeader object with the current policy/term details loaded
     * @param inputRecord  record containting input parameters including an option risk id.
     * @return RiskHeader an instance of the RiskHeader object loaded
     */
    RiskHeader loadRiskHeader(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param inputRecord         record with policy key fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRisk(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Returns a RecordSet loaded with list of risks that have coverage class defined
     * for the provided policy information.
     * <p/>
     *
     * @param inputRecord         record with policy key fields.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRiskWithCoverageClass(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of risks that have coverage defined
     * for the provided policy information.
     * <p/>
     *
     * @param inputRecord         record with policy key fields.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllRiskWithCoverage(Record inputRecord);

    /**
     * load all risk summary
     * @param inputRecord input records that contains key information
     * @param recordLoadProcessor an instance of data load processor
     * @return risk summary
     */
    public RecordSet loadAllRiskSummary(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information and entity name
     * <p/>
     *
     * @param inputRecord         record with policy key fields.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
     RecordSet findRiskByEntityName(Record inputRecord);

    /**
     * Returns a Record with the additional info 1,2,3 fields for the particular risk and term dates.
     * <p/>
     *
     * @param inputRecord record with riskId and term dates.
     * @return Record a Record with the Addl Info fields if configured via system parameters.
     */
    Record loadRiskAddlInfo(Record inputRecord);

    /**
     * Save all given input records with the Pm_Nb_End.Save_Risk stored procedure,
     * assuming they all have recordModeCode = TEMP, were added during this WIP transaction
     * (ie. have not been saved as Official yet).
     * This assumes all input records have recordModeCode = TEMP.
     * Set the rowStatus field to NEW for records that are newly added in this request.
     * Set the rowStatus field to MODIFIED for records that have already been saved in this WIP transaction,
     * and are just being updated not.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Risk Detail info matching the fields returned from the loadAllRisk method..
     * @return the number of rows updated.
     */
    int addAllRisk(RecordSet inputRecords);

    /**
     * Update all given input records with the Pm_Endorse.Change_Risk stored procedure,
     * assuming they all have field recordModeCode = Official, and were marked as updated.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Risk Detail info matching the fields returned from the loadAllRisk method..
     * @return the number of rows updated.
     */
    int updateAllRisk(RecordSet inputRecords);

    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Risk stored procedure,
     * assuming they all have recordModeCode = TEMP, and were marked for delete.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Risk Detail info matching the fields returned from the loadAllRisk method..
     * @return the number of rows updated.
     */
    int deleteAllRisk(RecordSet inputRecords);

    /**
     * Returns a RecordSet loaded with list of available risk types for the provided
     * policy information.
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of available risk types.
     */
    RecordSet loadAllRiskType(Record inputRecord);
    /**
     * Returns a RecordSet loaded with list of existing risks for the provided
     * policy information.
     * <p/>
     *
     * @param inputRecord input record that contains all key policy information.
     * @param processor to add select check box
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllExistingRisk(Record inputRecord, RecordLoadProcessor processor);
    /**
     * Returns a list of add codes for all risk types.
     *
     * @return RecordSet a RecordSet loaded with list of add codes for all risk types.
     */
    RecordSet loadAllRiskTypeAddCode();

    /**
     * Get "add code" for a given risk type.
     *
     * @param inputRecord Record contains input values
     * @return String a String contains an add code.
     */
    String getAddCodeForRisk(Record inputRecord);

    /**
     * check if the entity type of risk matches the required entity type of risk type.
     *
     * @param inputRecord Record contains Policy type code, Risk type code, and Risk entity ID.
     * @return String a String contains an add code.
     */
    String isRiskEntityTypeValid(Record inputRecord);

    /**
     * Get risk Id and base ID.
     *
     * @param policyId     Policy ID.
     * @param riskTypeCode Risk type code.
     * @param entityId     Risk entity ID.
     * @param location     Location ID.
     * @param slotId       Slot ID.
     * @return Record that contains risk Id and base ID.
     */
    Record getRiskIdAndBaseId(String policyId, String riskTypeCode,
                              String entityId, String location, String slotId);

    /**
     * Get ID for a given risk type and policy ID
     *
     * @param policyId     Policy ID.
     * @param riskTypeCode Risk type code.
     * @return String a String contains a address.
     */
    String getRiskId(String policyId, String riskTypeCode);

    /**
     * Get address for a given location ID
     *
     * @param location Location ID.
     * @return RecordSet a recordSet contains a address.
     */
    RecordSet getLocationAddress(String location);

    /**
     * Get default practice state code.
     *
     * @param issueStateCode         Issue state code
     * @param regionalOffice         Regional office code.
     * @param entityId               Risk entity ID.
     * @param transEffectiveFromDate Transaction effective from date.
     * @param getPmDefaultB          indicator for if call Pm Default.
     * @return String a String contains a default practice state code.
     */
    String getDefaultPracticeState(String issueStateCode, String regionalOffice, String entityId,
                                   String transEffectiveFromDate, String getPmDefaultB);

    /**
     * Returns a RecordSet loaded with list of locations for the given entity ID
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of locations.
     */
    RecordSet loadAllLocation(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Get the default risk type code for a newly created policy.
     *
     * @param inputRecord Record contains input values
     * @return String a String containing the riskTypeCode
     */
    String getAddDefaultRiskTypeCode(Record inputRecord);

    /**
     * Validate IBNR Indicator
     *
     * @param inputRecord
     * @return String
     */
    String validateIBNR(Record inputRecord);

    /**
     * check if the effective to date of a risk type can be changed.
     *
     * @param inputRecord
     * @return String a String contains an add code.
     */
    String isDateChangeAllowed(Record inputRecord);

    /**
     * Check if Change option is available or not by function Pm_Valid_Oos_Risk
     *
     * @param inputRecord
     * @return
     */
    String isOosRiskValid(Record inputRecord);

    /**
     * Change the effective to date on all related tables.
     *
     * @param inputRecord
     */
    void changeTermForEndorseDates(Record inputRecord);

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
     * To get risk type description
     *
     * @param riskBaseId
     * @return
     */
    String getRiskTypeDescription(String riskBaseId);

    /**
     * To get risk type description with slot/FTE ID if applicable
     * <p/>
     *
     * @param riskBaseId
     * @return the risk type description with id
     */
    public String getRiskTypeDefinition(String riskBaseId);

    /**
     * To get risk base id
     *
     * @param record
     * @return
     */
    String getRiskBaseId(Record record);

    /**
     * validate risk copy
     * @param inputRecord
     * @return validate status code statusCode
     */
    String validateCopyAllRisk(Record inputRecord);    
    
    /**
     * load all target risk for copy all risk         
     * @param inputRecord
     * @param processor
     * @return recordset of all target risks
     */
    RecordSet loadAllTargetRisk(Record inputRecord, RecordLoadProcessor processor);

    /**
     * validate risk
     * @param inputRecord
     * @return validate result
     */
    String validateRiskCopySource(Record inputRecord);

    /**
     * get to risk coverage
     * @param inputRecord
     * @return coverage info and validate status
     */
    Record getToRiskCoverage(Record inputRecord);

    /**
     * get copy to coverage count for validation
     * @return copy to coverage count
     */
    int getToCoverageCount(Record inputRecord);

    /**
     * get from risk type risk class count
     * @return from risk type risk class count
     */
    int getFromRiskClassCount(Record inputRecord);

    /**
     * To load all source risk's addresses and phone numbers
     * 
     * @param inputRecord
     * @param processor
     * @return
     */
    RecordSet loadAllAddressPhone(Record inputRecord, RecordLoadProcessor processor);

    /**
     * To copy all source risk's addresses and phone numbers to copy-to list risks
     * 
     * @param inputRecord
     * @return
     */
    Record copyAllAddressPhone(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of insured history.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllInsuredHistory(Record inputRecord);

    /**
     * Get risk type code by riskBaseRecordId
     *
     * @param inputRecord
     * @return String
     */
    public String getRiskTypeCode(Record inputRecord);

    /**
     * Get risk generic type
     *
     * @param inputRecord
     * @return String
     */
    public String getGenericRiskType(Record inputRecord);

    /**
     * Get entity owner id for location.
     *
     * @param inputRecord
     * @return int
     */
    public long getEntityOwnerId(Record inputRecord);

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
     * Returns a RecordSet loaded with list of procedure code.
     * <p/>
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllProcedureCode(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

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
     * @param inputRecord
     * @param entitlementRLP
     * @return RecordSet
     */
    public RecordSet loadAllRiskSurchargePoint(Record inputRecord, RecordLoadProcessor entitlementRLP);

    /**
     * Save all risk surcharge point.
     *
     * @param inputRecords
     * @return the number of rows updated.
     */
    int saveAllRiskSurchargePoint(RecordSet inputRecords);

    /**
     * Get all copy all configured fields.
     * <p/>
     *
     * @return the RecordSet contains config fields.
     */
    public RecordSet getAllFieldForCopyAll();

    /**
     * Process Copy All.
     * <p/>
     *
     * @param inputRecord
     */
    public RecordSet processCopyAll(Record inputRecord);

    /**
     * Method that copy policy to risk based on input Record.
     * <p/>
     * @param  inputRecord Record that contains new policy information
     * @return RecordSet
     */
    public RecordSet copyNewPolicyFromRisk(Record inputRecord);

    /**
     * Get the status of discipline decline entity.
     * <p/>
     * @param  inputRecord Record that contains entity id.
     * @return String
     */
    public String getDisciplineDeclineEntityStatus(Record inputRecord);

    /**
     * To validate reinstate ibnr risk
     *
     * @param inputRecord
     * @return
     */
    String valReinstateIbnrRisk(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of insured information.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllInsuredInfo(Record inputRecord);

    /**
     * Return the Chain status of the risk.
     *
     * @param inputRecord
     * @return
     */
    public Record getChainStatus(Record inputRecord);

    /**
     * Returns risk detail id.
     * <p/>
     *
     * @param inputRecord
     * @return the risk detail id
     */
    public String getRiskDetailId(Record inputRecord);

    /**
     * Validate Risk Duplication
     * @param inputRecord
     * @return
     */
    public Record validateRiskDuplicate(Record inputRecord);

    /**
     * Get all risks based on given ID list.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllRiskByIds(Record inputRecord);

    /**
     * Check the policy level combination is configured or not for auto risk relation.
     * If return value is greater than 0, it means this auto risk relation configuration is enable.
     * If return value is equals 0, it means this auto risk relation configuration is not enable.
     * @param inputRecord: Input record information.
     * @return PROD_RISK_RELATION primary key. If not exist, then return 0.
     */
    public int isAutoRiskRelConfigured(Record inputRecord);

    /**
     *
     * @param inputRecord: policy header information and new inserted risk ids.
     * @return Y: At least one added successfully.
     *         N: No configured or unavailable.
     *         M: If no any auto risk relation is successful and at least one auto risk relation is failed by multiple
     *            owner risk types exist.
     *         P: When some relations were created, and some were not due to the multiple parents.
     */
    public String processAutoRiskRelation(Record inputRecord);

    /**
     * Determines if alternative rating method is editable.
     * @param inputRecord
     * @return
     */
    public boolean isAlternativeRatingMethodEditable (Record inputRecord);

    /**
     * Get default
     *
     * @param inputRecord
     * @return String
     */
    public Record loadIsFundStateValue(Record inputRecord);

    /**
     * Get Default pcf county value.
     *
     * @param inputRecord
     * @return Record
     */
    public String getDefaultValueForPcfCounty(Record inputRecord);

    /**
     * Get Default pcf risk class value.
     *
     * @param inputRecord
     * @return Record
     */
    public String getDefaultValueForPcfRiskClass(Record inputRecord);

    /**
     * Determines if exclude_comp_gr1 field is visible.
     * @param inputRecord
     * @return
     */
    public boolean isGr1CompVisible (Record inputRecord);

    /**
     * Determines if exclude_comp_gr1 field is editable.
     * @param inputRecord
     * @return
     */
    public boolean isGr1CompEditable (Record inputRecord);

    /**
     * Determines if exclude_comp_gr2 field is editable.
     * @param inputRecord
     * @return
     */
    public boolean isGr2CompEditable (Record inputRecord);
}
