package dti.pm.riskmgr.coimgr.impl;

import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2008
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
public interface CoiSaveProcessor {

    /**
     * Processes Save for COI Holder.
     *
     * @param policyHeader
     * @param inputRecords
     */
    void performSaveAllCoiHolder(PolicyHeader policyHeader, RecordSet inputRecords);

}
