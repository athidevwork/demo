package dti.ci.entityclassmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The business component for entity class.
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
public interface EntityClassManager {
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
     * @return
     */
    RecordSet loadAllEntityClass(Record inputRecord);

    /**
     * Add entity class.
     *
     * @param inputRecord
     * @return The PK (entity_class_pk) of the new entity class.
     */
    void addEntityClass(Record inputRecord);

    /**
     * Add entity class.
     *
     * @param inputRecord
     * @return The PK (entity_class_pk) of the new entity class.
     */
    void modifyEntityClass(Record inputRecord);

    /**
     * Save all entity class of an entity.
     * @param rs
     * @return
     */
    void deleteEntityClasses(RecordSet rs);

    /**
     * Process entity class info for modify.
     *
     * @param record
     */
    void processEntityClassInfoForModify(Record record);

    /**
     * Save entity class codes for web service PartyChangeService.
     *
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
