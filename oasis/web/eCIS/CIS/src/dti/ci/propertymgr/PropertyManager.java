package dti.ci.propertymgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/9/12
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
public interface PropertyManager {
    /**
     * Load all property of an entity.
     * @param record
     * @return
     */
    public RecordSet loadAllProperty(Record record);

    /**
     * Save all property.
     * @param rs
     * @return
     */
    public int saveAllProperty(RecordSet rs);

    /**
     * Save property
     *
     * @param inputRecord
     */
    public Record saveProperty(Record inputRecord);
}
