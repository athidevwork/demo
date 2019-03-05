package dti.pm.coveragemgr.excesspremiummgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for manual access premium.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 01, 2009
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
public interface ExcessPremiumDAO {
    /**
     * Load all manual excess premium.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllExcessPremium(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Get column name and label for specific column.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getExcessPremiumColumn(Record inputRecord);

    /**
     * Save all manual excess premium.
     *
     * @param inputRecords
     */
    public void saveAllExcessPremium(RecordSet inputRecords);
}
