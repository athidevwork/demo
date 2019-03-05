package dti.ci.contactmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * The DAO of contact.
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
public interface ContactDAO {
    /**
     * Load the contacts of an entity.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public RecordSet loadAllContact(Record inputRecord, RecordLoadProcessor loadProcessor);

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
