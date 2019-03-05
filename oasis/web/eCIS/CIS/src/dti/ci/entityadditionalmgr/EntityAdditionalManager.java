package dti.ci.entityadditionalmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Implementation of entityAdditional Manager.
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
public interface EntityAdditionalManager {

    /**
     * Get the entityAdditional recordset
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadAllAvailableEntityAdditionals(Record inputRecord);

    /**
     * Save the changed entityAdditional to DB
     *
     * @param inputRecords

     * @return
     */
    int saveAllEntityAdditionals(RecordSet inputRecords);

   
}
