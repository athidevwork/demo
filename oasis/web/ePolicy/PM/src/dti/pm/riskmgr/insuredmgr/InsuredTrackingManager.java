package dti.pm.riskmgr.insuredmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * <p>(C) 2015 Delphi Technology, inc. (dti)</p>
 * Date:   April 08, 2015
 *
 * @author wdang
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 04/08/2015    wdang      157211 - Initial version, Maintain Insured Tracking Information.
 * ---------------------------------------------------
 */
public interface InsuredTrackingManager {

    /**
     * Returns a RecordSet loaded with list of available insured tracking for the provided risk.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  input records that contains key information.
     * @return RecordSet   a RecordSet loaded with list of available insured tracking.
     */
    RecordSet loadAllInsuredTracking(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Save all data in insured tracking page
     *
     * @param policyHeader   policy header that contains all key policy information.
     * @param inputRecord    a record that contains risk information.
     * @param inputRecords   a record set with data to be saved.
     */
    void saveAllInsuredTracking(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords);
    
    /**
     * Get initial values for Insured Tracking
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return Record a Record loaded with initial values.
     */
    Record getInitialValuesForInsuredTracking(PolicyHeader policyHeader, Record inputRecord);
}
