package dti.pm.policymgr.reinsurancemgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * An interface to handle operation on the reinsurance information.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 30, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface ReinsuranceManager {

    /**
     * Retrieves all reinsurance information for one policy
     *
     * @param policyHeader policy header
     * @return RecordSet
     */
    RecordSet loadAllReinsurance(PolicyHeader policyHeader);

    /**
     * Save all reinsurance's information
     *
     * @param policyHeader policy header
     * @param inputRecords a set of Records, each with the updated special handling info
     * @return the number of rows updated
     */
    int saveAllReinsurance(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * To get initial value for a new reinsurance record
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForReinsurance(PolicyHeader policyHeader, Record inputRecord);
}
