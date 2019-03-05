package dti.pm.transactionmgr.auditmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 24, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface AuditManager {
    /**
     * Retrieves all audit information
     *
     * @param policyHeader policy header
     * @param inputRecord input Record
     * @return RecordSet
     */
    RecordSet loadAllAudit(PolicyHeader policyHeader,Record inputRecord);

}
