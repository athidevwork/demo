package dti.ci.entityclassmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * The DAO for entity class.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/11/14
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
public interface EntityClassDAO {
    /**
     * Load entity class record by given entity class id.
     *
     * @param inputRecord
     * @return
     */
    Record loadEntityClass(Record inputRecord);

    /**
     * Load all entity class of an entity.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    RecordSet loadAllEntityClass(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Save entity class.
     *
     * @param rs
     * @return The PK (entity_class_pk) of the new entity class.
     */
    void saveEntityClass(RecordSet rs);

    /**
     * Save entity class codes for web service PartyChangeService.
     * @param record
     * @return
     */
    Record saveEntityClassWs(Record record);

    /**
     * Check if the current entity class is overlap with another entity class.
     *
     * @param record
     * @return
     */
    boolean hasOverlapEntityClass(Record record);
}
