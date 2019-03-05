package dti.pm.coveragemgr.underlyingmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * An interface to handle CRUD operation on Underlying Coverage information.
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 12, 2008
 *
 * @author wrong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  08/24/2018       wrong      188391 - Initial version.
 * ---------------------------------------------------
 */
public interface UnderlyingCoverageManager {

    /**
     * load all underlying Coverage
     * @param policyHeader policy header
     * @return result recordset
     */
    RecordSet loadAllUnderlyingCoverage(PolicyHeader policyHeader, Record inputRecord);

    /**
     * get initial values for underlying coverage add
     * @param policyHeader policy header
     * @param inputRecord contains parameters for adding new record
     * @return record contains all returned default values
     */
    Record getInitialValuesForUnderlyingCoverage(PolicyHeader policyHeader, Record inputRecord);

    /**
     * save all underlying coverage data
     * @param policyHeader policy header
     * @param inputRecords input recordset
     */
    void saveAllUnderlyingCoverage(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * load all active policies
     * @param policyHeader policy header
     * @param inputRecord input record
     * @return recordset of active policy list
     */
    RecordSet loadAllActivePolicy(PolicyHeader policyHeader, Record inputRecord);

    /**
     * load all available related coverages
     * @param policyHeader policy header
     * @param inputRecord input record
     * @return recordset of active coverage list
     */
    RecordSet loadAvailableRelatedCoverage(PolicyHeader policyHeader, Record inputRecord);

    /**
     * load current coverage
     * @param policyHeader policy header
     * @return recordset of coverage
     */
    RecordSet getCurrentCoverage(PolicyHeader policyHeader);
}
