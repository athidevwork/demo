package dti.pm.policymgr.renewalflagmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

import java.security.Policy;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/16/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/24/2017       tzeng       167532 - Initial version.
 * ---------------------------------------------------
 */

public interface RenewalFlagManager {
    /**
     * Returns a RecordSet loaded with list of available renewal flags.
     * <p/>
     *
     * @param policyHeader Policy header that contains all key policy information.
     * @return recordSet a RecordSet loaded with list of available renewal flags.
     */
    RecordSet loadAllRenewalFlag(PolicyHeader policyHeader);

    /**
     * Get initial values for renewal flag added.
     * @param policyHeader policy header
     * @return record contains all returned default values
     */
    Record getInitialValuesForAddRenewalFlag(PolicyHeader policyHeader);

    /**
     * Save changes for renewal flag page.
     * @param policyHeader Policy header.
     * @param inputRecords Input recordset.
     */
    void saveAllRenewalFlag(PolicyHeader policyHeader, RecordSet inputRecords);
}
