package dti.pm.policymgr.reviewduplicatemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Action class for Renewal Candidate.
 * <p/>
 *
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   June 28, 2016
 *
 * @author ssheng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/28/2016        ssheng      164927 - Created this action class for Quote Import enhancement.
 * ---------------------------------------------------
 */
public interface ReviewDuplicateManager {
    /**
     * Load all roster risks
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllRosterRisk(PolicyHeader policyHeader);

    /**
     * Load all CIS Match entity
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCISDuplicate(Record inputRecord);

    /**
     * Save review Duplicate records
     *
     * @param policyHeader
     * @param inputRecords
     * @param cisDupInputRecords
     * @return
     */
    public void saveReviewDuplicate(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet cisDupInputRecords);

    /**
     * Save review all Duplicate records to CIS
     *
     * @param policyHeader
     * @param inputRecords
     * @return
     */
    public void saveAllToCIS(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Validate no process review duplicate
     *
     * @param policyHeader
     * @return
     */
    public String validateReviewDuplicate(PolicyHeader policyHeader);
}
