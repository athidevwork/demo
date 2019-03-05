package dti.pm.transactionmgr.shorttermpolicymgr;

import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of Short Term Policy Manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 1, 2011
 *
 * @author ryzhao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 * ---------------------------------------------------
 */

public interface ShortTermPolicyManager {

    /**
     * Create accept short term policy transaction
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void createAcceptPolicyTransaction(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Accept short term policy
     *
     * @param policyHeader
     * @param inputRecord
     * @return accept status - SUCCESS/FAILED
     */
    public String performAcceptPolicy(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Decline short term policy
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void performDeclinePolicy(PolicyHeader policyHeader, Record inputRecord);

}
