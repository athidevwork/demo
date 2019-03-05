package dti.ci.auditmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/9/2018
 *
 * @author yllu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/13/2018       ylu         Issue 109179: refactor audit trail tab page and popup page
 * ---------------------------------------------------
 */
public interface AuditTrailDAO {

    /**
     * load all audit trail data in tab page for this entity
     * @param record
     * @return
     */
    RecordSet searchAuditTrailData(Record record);

    /**
     * load audit history data in popup page for this entity
     * @param record
     * @return
     */
    RecordSet loadAuditTrailBySource(Record record);

}
