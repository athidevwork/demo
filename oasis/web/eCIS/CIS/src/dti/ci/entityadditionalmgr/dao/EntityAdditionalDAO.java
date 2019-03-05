package dti.ci.entityadditionalmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for entityAdditional.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: February 08, 2011
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EntityAdditionalDAO {
    /**
     * Get all entityAdditional from DB
     *
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    RecordSet loadAllAvailableEntityAdditionals(Record record);

    /**
     * Update changes of entityAdditional to DB
     *
     * @param inputRecords
     * @return
     */
    int saveAllEntityAdditionals(RecordSet inputRecords);

}
