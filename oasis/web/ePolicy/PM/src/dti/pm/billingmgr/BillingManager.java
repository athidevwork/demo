package dti.pm.billingmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 1, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/29/2009       yhyang      91531: Add the method getPolicyRelationValue().
 * 08/25/2010       bhong       110269 - Added isCoverageIdExists
 * ---------------------------------------------------
 */
public interface BillingManager {

    /**
     * Method that gets the default values
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param policyHeader the summary information for the given policy
     * @param inputRecord  Record that contains input parameters
     * @return Record that contains default (init) values
     */
    public Record getInitialValuesForBilling(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Method that gets the default values with recordLoadProcessor
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param policyHeader        the summary information for the given policy
     * @param inputRecord         Record that contains input parameters
     * @param recordLoadProcessor a load processor to be applied when getting values from datasource
     * @return Record that contains default (init) values
     */
    public Record getInitialValuesForBilling(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method that saves billing data based on parameters stored in the input Record.
     * It calls validationBillingForSave first.
     * and if the validationReturns false, it will not call BillingDAO to save
     * <p/>
     *
     * @param policyHeader summaryInforamtion for the policy whose
     *                     billing information is about to be saved
     * @param inputRecord  Record that contains input parameters
     * @return Record returned by BillingDAO merged with the inputRecord for redisplay
     */
    public Record saveBilling(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Method that updates billing account data.
     *
     * @param inputRecord Record that contains input parameters
     */
    public void updateBillingAccount(Record inputRecord);

    /**
     * Get the policy relation value.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getPolicyRelationValue(Record inputRecord);

    /**
     * Check if coverage id exists
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean isCoverageIdExists(PolicyHeader policyHeader);

    /**
     * To validate if an account exists for an entity
     *
     * @param inputRecord
     */
    Record validateAccountExistsForEntity(Record inputRecord);
}
