package dti.ci.policymgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * DAO contains all policy associted call in eCIS.
 * <p/>
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
public interface CIPolicyDAO {
    /**
     * Load all locked policy
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllLockedPolicy(Record inputRecord);
}
