package dti.pm.coveragemgr.impl;

import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.RecordSet;

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
 * 03/06/2013       fcb         141924 - Changes for Web Services.
 * ---------------------------------------------------
 */
public interface CoverageSaveProcessor {

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader the summary policy information corresponding to the provided coverages.
     * @param inputRecords a set of Records, each with the updated Coverage Detail info
     *                     matching the fields returned from the loadAllCoverage method.
     * @return the number of rows updated.
     */
    int saveAllCoverage(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Save all Coverage and Component input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     */
    int saveAllCoverageAndComponent(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet componentRecords);

    /**
     * @param policyHeader     the summary policy information corresponding to the provided coverages.
     * @param inputRecords     a set of Records, each with the updated Coverage info
     */
    int saveAllCoverageForWs(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     *
     * @param policyHeader     the summary policy information corresponding to the provided coverages.
     * @param inputRecords     a set of Records, each with the updated Coverage info
     * @param componentRecords a set of Records, each with the updated Component info
     */
    public void saveAllComponentForWs(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet componentRecords);

}
