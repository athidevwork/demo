package dti.pm.policymgr.validationmgr;

import dti.pm.policymgr.PolicyHeader;
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

public interface SoftValidationManager {
    /**
     * Return soft validation by condition.
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    RecordSet loadSoftValidation(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Process soft validation.
     * If inputRecords is null or size = 0, then delete soft validation. Otherwise, then save soft validation.
     * @param policyHeader
     * @param inputRecords
     */
    void processSoftValidation(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Get indication for soft validation existed or not.
     * @param inputRecord
     * @return Record
     */
    Record getSoftValidationB(Record inputRecord);
}
