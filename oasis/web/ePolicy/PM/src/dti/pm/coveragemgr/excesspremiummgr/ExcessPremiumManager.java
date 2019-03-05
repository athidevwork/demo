package dti.pm.coveragemgr.excesspremiummgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * An interface to handle business logics for manual excess premium.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 01, 2009
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface ExcessPremiumManager {
    /**
     * Load all manual excess premium.
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllExcessPremium(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Load all manual excess premium summary.
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllExcessPremiumSummary(PolicyHeader policyHeader);

    /**
     * Get all columns for manual excess premium. It contains five columns.
     *
     * @return RecordSet
     */
    public RecordSet getAllExcessPremiumColumn();

    /**
     * Re-calculate all manual excess premium.
     *
     * @param inputRecords
     */
    public void calculateAllExcessPremium(RecordSet inputRecords);

    /**
     * Save all manual excess premium.
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     */
    public void saveAllExcessPremium(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords);
}
