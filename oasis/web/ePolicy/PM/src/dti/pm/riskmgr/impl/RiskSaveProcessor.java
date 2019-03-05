package dti.pm.riskmgr.impl;

import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 29, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface RiskSaveProcessor {

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader
     * @param inputRecords
     * @return
     */
    int saveAllRisk(PolicyHeader policyHeader, RecordSet inputRecords);

}
