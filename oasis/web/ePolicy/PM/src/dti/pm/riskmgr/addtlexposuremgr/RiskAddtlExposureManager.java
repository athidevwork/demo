package dti.pm.riskmgr.addtlexposuremgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * Date:   May 23, 2017
 *
 * @author eyin
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 09/21/2017    eyin       169483, Initial version.
 * ---------------------------------------------------
 */
public interface RiskAddtlExposureManager {

    /**
     * Returns a RecordSet loaded with list of available Additional Exposure for the provided risk.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with original selected risk information
     * @param loadProcessor an instance of data load processor
     * @param processEntitlements true/false indicator for processing entitlements
     * @return RecordSet   a RecordSet loaded with list of available Additional Exposure.
     */
    RecordSet loadAllRiskAddtlExposure(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor, boolean processEntitlements);

    /**
     * Returns a RecordSet loaded with list of Primary Practice for the provided risk.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with original selected risk information
     * @return RecordSet   a RecordSet loaded with list of Primary Practice.
     */
    RecordSet loadPrimaryPractice(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Save all data in Additional Exposure page
     *
     * @param policyHeader   policy header that contains all key policy information.
     * @param inputRecords   a record set with data to be saved.
     */
    int saveAllRiskAddtlExposure(PolicyHeader policyHeader, RecordSet inputRecords);
    
    /**
     * Get initial values for Additional Exposure
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return Record a Record loaded with initial values.
     */
    Record getInitialValuesForAddRiskAddtlExposure(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Check if Change option is available
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with the selected risk additional exposure record.
     */
    void validateForOoseRiskAddtlExposure(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get initial values for OOSE risk additional exposure
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values
     */
    Record getInitialValuesForOoseRiskAddtlExposure(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Load all the policy term's Additional Exposure records, it will be called by Policy Inquiry Service.
     * @param policyHeader
     * @param filterInsured
     *
     * @return
     */
    RecordSet loadAllRiskAddtlExposureForWS(PolicyHeader policyHeader, String filterInsured) ;
}
