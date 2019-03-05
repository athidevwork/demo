package dti.pm.transactionmgr.premiumadjustmentprocessmgr;

import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle permium adjustment Manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 10, 2007
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

public interface PremiumAdjustmentManager {

    /**
     * Returns a RecordSet loaded with list of available coverage for the provided
     * policy information.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllCoverage(PolicyHeader policyHeader);

    /**
     * load all permium adjustment
     *
     * @param policyHeader
     * @param coverages
     * @return RecordSet   a record set loaded with list of premium adjustment
     */
    public RecordSet loadAllPremiumAdjustment(PolicyHeader policyHeader, RecordSet coverages);


    /**
     * save all permium adjustment info
     *
     * @param policyHeader the summary policy information corresponding to the provided coverages.
     * @param inputRecords premium adjustment records
     * @return the number of rows updated.
     */
    public void saveAllPremiumAdjustment(PolicyHeader policyHeader, RecordSet inputRecords);


}
