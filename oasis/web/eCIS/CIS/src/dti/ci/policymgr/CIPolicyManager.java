package dti.ci.policymgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * This bussiness component contains all logics related with policy in eCIS.
 *
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 04, 2009
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface CIPolicyManager {
    /**
     * Load all locked policies
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllLockedPolicy(Record inputRecord);
}
