package dti.pm.coverageclassmgr.impl;

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
 *
 * ---------------------------------------------------
 */
public interface CoverageClassSaveProcessor {
    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader the summary policy information corresponding to the provided coverage classes.
     * @param inputRecords a set of Records, each with the updated Coverage Class Detail info
     *                     matching the fields returned from the loadAllCoverageClass method.
     * @return the number of rows updated.
     */
    int saveAllCoverageClass(PolicyHeader policyHeader, RecordSet inputRecords);
}
