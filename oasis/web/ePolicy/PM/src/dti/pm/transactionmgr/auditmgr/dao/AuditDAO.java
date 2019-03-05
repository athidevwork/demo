package dti.pm.transactionmgr.auditmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for view premium.
 * <p/>
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

public interface AuditDAO {
    /**
     * Retrieves all premium information.
     *
     * @param record input record
     * @return recordSet
     */
    RecordSet loadAllAudit(Record record);
}
