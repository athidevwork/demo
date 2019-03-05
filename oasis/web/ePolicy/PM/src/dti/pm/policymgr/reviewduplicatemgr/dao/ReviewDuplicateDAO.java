package dti.pm.policymgr.reviewduplicatemgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

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
 * 03/13/2018        tzeng       189424 - Added validateRosterRisk to get all invalid types per one roster risk.
 * ---------------------------------------------------
 */
public interface ReviewDuplicateDAO {
    /**
     * Load all roster risks
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRosterRisk(Record inputRecord);

    /**
     * Load all CIS Match entity
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCISDuplicate(Record inputRecord);

    /**
     * Add new entity to CIS
     *
     * @param inputRecords
     * @return
     */
    public void savePopulateToCIS(RecordSet inputRecords);

    /**
     * Use CIS entity
     *
     * @param inputRecords
     * @return
     */
    public void saveUseCISRecord(RecordSet inputRecords);

    /**
     * Validate risk existed
     *
     * @param inputRecord
     * @return returnMessage
     */
    public String validateRisk(Record inputRecord);

    /**
     * Validate no process review duplicate
     *
     * @param inputRecord
     * @return
     */
    public String validateReviewDuplicate(Record inputRecord);

    /**
     * Get all invalid types of CIS information.
     *
     * @param inputRecord input Record
     * @return invalidateTypes
     */
    public String validateCISInfo(Record inputRecord);
}
