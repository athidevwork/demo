package dti.ci.propertymgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * The db object of Property.
 *
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
public interface PropertyDAO {
    /**
     * Load all property of an entity.
     * @param record
     * @return
     */
    public RecordSet loadAllProperty(Record record, RecordLoadProcessor loadProcessor);

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
