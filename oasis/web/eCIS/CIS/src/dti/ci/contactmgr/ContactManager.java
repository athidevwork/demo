package dti.ci.contactmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The business component of Contract.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/20/12
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface ContactManager {
    /**
     * Load the contacts of an entity.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllContact(Record inputRecord);

    /**
     * Save the contacts of an entity.
     *
     * @param rs
     * @return
     */
    public int saveAllContact(RecordSet rs);

    /**
     * Save contact number
     *
     * @param inputRecord
     */
    public Record saveContact(Record inputRecord);
}
