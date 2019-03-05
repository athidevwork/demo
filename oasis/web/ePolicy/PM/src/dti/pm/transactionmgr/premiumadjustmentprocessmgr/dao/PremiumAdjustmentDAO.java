package dti.pm.transactionmgr.premiumadjustmentprocessmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for premium adjustment.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 9, 2008
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

public interface PremiumAdjustmentDAO {

    /**
     * load all coverage by policy_id
     *
     * @param inputRecord
     * @return RecordSet share groups
     */
    RecordSet loadAllCoverage(Record inputRecord);

    /**
     * load all permium adjustment by transactionLogId.
     *
     * @param inputRecord
     * @return RecordSet share group details
     */
    RecordSet loadAllPremiumAdjustment(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * save all premium adjustment
     *
     * @return returnCode
     */
    String saveAllPremiumAdjustment(Record inputRecord);

}
