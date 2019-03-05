package dti.pm.tailmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle tails.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 21, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/10/2010       gchitta     Modified isCaptureFinancePercentageRequired to return String instead of YesNo as one of
 *                              possible values is 'B'
 * 03/22/2011       gchitta     Added getMinimumTermData to obtain/set the minimum term's data
 * 03/25/2011       dzhang      Issue 113602 - Update per Joe's comments.
 * 04/27/2012       xnie        Issue 132999 - Added a parameter inputRecord to getInitialValuesForTailCharge.
 * ---------------------------------------------------
 */

public interface TailManager {
    /**
     * load all tales
     *
     * @param policyHeader
     * @param inputRecord
     * @param selectIndProcessor
     * @return recordset include all tail parents
     */
    RecordSet loadAllTail(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor selectIndProcessor);


    
    /**
     * This method will call the save and validate methods within separate transactions,
     * and return the validate result returned from the validate method.
     * This method will also delete the WIP and unlock the policy if required
     * (according to Alternate Flow: Processing Error).
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     * @param componentRecords
     * @return validate result
     */
    public String processSaveAllTailAndComponent(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords, RecordSet componentRecords);

    /**
     * check if the tail data is valid
     *
     * @param policyHeader *
     * @return validate result
     */
    String validateTailData(PolicyHeader policyHeader);

    /**
     * perform validate tail delta
     *
     * @param inputRecord
     * @return validate result
     */
    public boolean validateTailDelta(Record inputRecord);

    /**
     * perform validate tail transaction process
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     * @return RecordSet contains all selected records with validation infos for every record
     */
    public RecordSet validateTailProcess(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords);


    /**
     * perform tail transaction
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     */
    public void performTailProcess(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords);

    /**
     * get default transaction parameters
     *
     * @param policyHeader
     * @param inputRecord
     * @param selectedRecords
     * @return record include default accounting date, transaction effective date, transaction code, transaction comments
     */
    public Record getDefaultTransactionParms(PolicyHeader policyHeader, Record inputRecord, RecordSet selectedRecords);

    /**
     * check if capture finance percentage is required
     *
     * @param policyHeader
     * @return policy type configured parameter
     */
    public String isCaptureFinancePercentageRequired(PolicyHeader policyHeader);


    /**
     * method to get the initial value when capture tail charge page
     *  
     * @return record
     */
    public Record getInitialValuesForTailCharge(PolicyHeader policyHeader, Record inputRecord);

   /**
     * load all manual tails for adding new tail coverage
     * @param policyHeader policy header
     * @param inputRecord input record contains parent coverage info
     * @return recordset of available tails for adding
     */
    RecordSet loadAllManualTail(PolicyHeader policyHeader, Record inputRecord);

    /**
     * add manual tail coverage
     * @param policyHeader policy header
     * @param inputRecord input record contains required infos for add manual tail coverage
     */
    public void addManualTail(PolicyHeader policyHeader, Record inputRecord);

    /**
     * save tail finance charge
     * @param policyHeader policy header
     * @param inputRecord input record contains tail coverage info
     * @param inputRecords
     */
    public void saveTailCharge(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords);

}
