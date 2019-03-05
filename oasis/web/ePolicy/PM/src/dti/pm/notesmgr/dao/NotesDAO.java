package dti.pm.notesmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for part time notes.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 24, 2008
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
public interface NotesDAO {
    /**
     * Load all part time notes
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPartTimeNotes(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Save all changes in part time notes
     *
     * @param inputRecords
     * @return int
     */
    public int saveAllPartTimeNotes(RecordSet inputRecords);
}
