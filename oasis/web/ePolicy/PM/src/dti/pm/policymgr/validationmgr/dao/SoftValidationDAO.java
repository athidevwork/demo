package dti.pm.policymgr.validationmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/7/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/07/2016       tzeng       166929 - Initial version.
 * ---------------------------------------------------
 */
public interface SoftValidationDAO {
    /**
     * Returns soft validation by condition.
     * @param inputRecord
     * @return a record set loaded with list of soft validation
     */
    RecordSet loadSoftValidation(Record inputRecord);

    /**
     * Save soft validation.
     * @param inputRecords
     * @return processed record count
     */
    int saveAllSoftValidation(RecordSet inputRecords);

    /**
     * Delete soft validation.
     * @param inputRecord
     * @return processed record count
     */
    int deleteAllSoftValidation(Record inputRecord);

    /**
     * Get latest soft validation based transaction of policy.
     * @param inputRecord
     * @return transactionId
     */
    long getLatestSoftValidationTransaction(Record inputRecord);
}
