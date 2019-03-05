package dti.ci.demographic.clientmgr.clientidmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/24/2008       kshen       Removed class prefix "CI".
 * ---------------------------------------------------
 */
public interface ClientIdDAO {
    /**
     * Load all client ids for a client.
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadAllClientIds(Record client, RecordLoadProcessor recordLoadProcessor);

    /**
     * Add all client ids.
     * @param inputRecords
     * @return
     */
    public int addAllClientIds(RecordSet inputRecords);

    /**
     * Update all client ids.
     * @param inputRecords
     * @return
     */
    public int updateAllClientIds(RecordSet inputRecords);

    /**
     * Delete all client ids.
     * @param inputRecords
     * @return
     */
    public int deleteAllClientIds(RecordSet inputRecords);
}
