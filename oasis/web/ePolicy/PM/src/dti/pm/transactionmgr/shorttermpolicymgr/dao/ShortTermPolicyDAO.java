package dti.pm.transactionmgr.shorttermpolicymgr.dao;

import dti.oasis.recordset.Record;

/**
 * An interface that provides DAO operation for short term policy.
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

public interface ShortTermPolicyDAO {

    /**
     * Accept short term policy
     *
     * @param inputRecord that has transaction id/code, policy id, term eff/exp date
     * @return Record include the field indicating if accept policy successfully
     */
    public Record acceptPolicy(Record inputRecord);

    /**
     * Decline short term policy
     *
     * @param inputRecord that has cancellation related fields
     */
    public void declinePolicy(Record inputRecord);

}