package dti.ci.entityaddlemailmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   4/11/13
 *
 * @author bzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EntityAddlEmailManager {

    /**
     * Load the entity additional email list.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadEntityAddlEmailList(Record inputRecord);

    /**
     * Save the entity additional email list.
     *
     * @param inputRecords
     * @return
     */
    public int updateEntityAddlEmailList(RecordSet inputRecords);
}
