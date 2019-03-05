package dti.pm.policymgr.impl;

import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

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
public interface PolicySaveProcessor {

    /**
     * Saves the input record based upon record mode code.
     *
     * @param policyHeader the summary policy information corresponding to the provided policy.
     * @param inputRecord  a set of Records, each with the updated Policy Detail info
     *                     matching the fields returned from the loadAllPolicy method.
     * @return the number of rows processed.
     */
    public int savePolicy(PolicyHeader policyHeader, Record inputRecord);

}
