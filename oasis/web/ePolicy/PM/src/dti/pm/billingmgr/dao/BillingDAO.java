package dti.pm.billingmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
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
public interface BillingDAO {

    /**
     * Method that gets the default values with recordLoadProcessor
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord Record that contains input parameters
     * @return Record that contains default (init) values
     */
    public RecordSet getInitialValuesForBilling(Record inputRecord);

    /**
     * Method that gets the default values with recordLoadProcessor
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord         Record that contains input parameters
     * @param recordLoadProcessor RecordLoad Processor
     * @return Record that contains default (init) values
     */
    public RecordSet getInitialValuesForBilling(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method that validates the billing data
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord Record that contains input parameters
     * @return Record that contains validated data.
     */
    public Record validateValuesForBilling(Record inputRecord);

    /**
     * Method that validates the billing data with recordLoadProcessor
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord Record that contains input parameters
     * @return Record that contains validated data.
     */

    public Record validateValuesForBilling(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method that saves the billing data with recordLoadProcessor
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord Record that have been validated
     * @return Record that returned by Database process in additon to validated input data (for redisplay).
     */
    public Record saveBilling(Record inputRecord);

    /**
     * Method that updates the Billing Account information.
     *
     * @param inputRecord Record that contains input parameters
     */
    void updateBillingAccount(Record inputRecord);

    /**
     * Get the policy relation value.
     *
     * @param inputRecord
     * @return String
     */
    public String getPolicyRelationValue(Record inputRecord);

    /**
     * Check if coverage id exists
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean isCoverageIdExists(Record inputRecord);

    /**
     * To check if an account exists for an entity
     * @param inputRecord
     * @return Record
     */
    Record validateAccountExistsForEntity(Record inputRecord);
}
