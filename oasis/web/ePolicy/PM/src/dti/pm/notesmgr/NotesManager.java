package dti.pm.notesmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Implementation of Notes Manager.
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
public interface NotesManager {
    /**
     * Validate search criteria
     *
     * @param inputRecord
     */
    public void validateSearchCriteria(Record inputRecord);

    /**
     * Load all part time notes
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPartTimeNotes(Record inputRecord);

    /**
     * Validate policy no
     *
     * @param inputRecord
     * @return Record
     */
    public Record validatePolicyNo(Record inputRecord);

    /**
     * Get initial values
     *
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValues(Record inputRecord);

    /**
     * Save all part time notes
     *
     * @param inputRecords
     */
    public void saveAllPartTimeNotes(RecordSet inputRecords);
}
