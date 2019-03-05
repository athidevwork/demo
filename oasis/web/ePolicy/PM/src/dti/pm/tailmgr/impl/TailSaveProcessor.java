package dti.pm.tailmgr.impl;

import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.RecordSet;

/**
 * This interface if for tail save process
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 3, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface TailSaveProcessor {
    /**
         * Wrapper to invoke the save of all inserted/updated tail and component
         *
         * @param policyHeader     the summary policy information corresponding to the provided coverages.
         * @param inputRecords     a set of Records, each with the updated Coverage Detail info
         *                         matching the fields returned from the loadAllCoverage method.
         * @param componentRecords a set of Records, each with the updated Component Detail info
         *                         matching the fields returned from the ComponentManager.loadAllComponents method.
         * @return updated row count.
         */
        public int saveAllTailAndComponent(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet componentRecords);

}
