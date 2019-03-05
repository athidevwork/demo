package dti.pm.policymgr.additionalinsuredmgr.impl;

import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 19, 2008
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface AdditionalInsuredSaveProcessor {
    /**
     * Processes Save for Additional Insured
     *
     * @param policyHeader
     * @param inputRecords
     */
    void performSaveAllAdditionalInsured(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord);
}

