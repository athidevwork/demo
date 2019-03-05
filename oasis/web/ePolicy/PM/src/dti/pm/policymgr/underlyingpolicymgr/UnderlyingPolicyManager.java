package dti.pm.policymgr.underlyingpolicymgr;

import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * An interface to handle CRUD operation on Underlying Policy information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 3, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface UnderlyingPolicyManager {

    /**
     * load all underlying policies
     * @param policyHeader policy header
     * @return result recordset
     */
    RecordSet loadAllUnderlyingPolicy(PolicyHeader policyHeader);

    /**
     * get retro date for reset
     * @param policyHeader policy header
     * @param inputRecord contains all required infos
     * @return retor date
     */
    String getRetroDateForReset(PolicyHeader policyHeader, Record inputRecord);

    /**
     * get initial values for underlying policy add
     * @param policyHeader policy header
     * @param inputRecord contains parameters for adding new record
     * @return record contains all returned default values
     */
    Record getInitialValuesForUnderlyingPolicy(PolicyHeader policyHeader, Record inputRecord);

    /**
     * save all underlying policy data
     * @param policyHeader policy header
     * @param inputRecords input recordset
     */
    void saveAllUnderlyingPolicy(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * load all active policies
     * @param policyHeader policy header
     * @param inputRecord input record
     * @return recordset of active policy list
     */
    RecordSet loadAllActivePolicy(PolicyHeader policyHeader, Record inputRecord);
}
