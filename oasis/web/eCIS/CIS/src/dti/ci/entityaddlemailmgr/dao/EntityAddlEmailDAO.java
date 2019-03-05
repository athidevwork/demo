package dti.ci.entityaddlemailmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
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
public interface EntityAddlEmailDAO {

    /**
     * Load the entity additional email list.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadEntityAddlEmailList(Record inputRecord);

    /**
     * Delete the entity additional email.
     *
     * @param inputRecords
     * @return
     */
    public int deleteEntityAddlEmail(RecordSet inputRecords);

    /**
     * Update the entity additional email list.
     *
     * @param inputRecords
     * @return
     */
    public int updateEntityAddlEmail(RecordSet inputRecords);

    /**
     * Insert the entity additional email list.
     *
     * @param inputRecords
     * @return
     */
    public int insertEntityAddlEmail(RecordSet inputRecords);
}
