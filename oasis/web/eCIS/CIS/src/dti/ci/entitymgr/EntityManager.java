package dti.ci.entitymgr;

import dti.ci.core.EntityInfo;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 28, 2010
 *
 * @author ldong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/24/2016       Elvin       Issue 176524: add searchEntityForWS
 * ---------------------------------------------------
 */
public interface EntityManager {
    /**
     * Retrieves all Entity information
     *
     * @param record
     * @return RecordSet
     */
    RecordSet loadEntityList(Record record);

    /**
     * Add new Entity
     * @param record
     * @return Record
     */
    public Record AddEntity(Record record);

    /**
     * Save entity for service
     *
     * @param record
     */
    public void saveEntityForService(Record record);

    /**
     * Save PartyNote
     *
     * @param inputRecord
     */
    public Record savePartyNote(Record inputRecord);

    /**
     * Search entity by externalReferenceId and/or externalDataId
     *
     * @param inputRecord
     * @return
     */
    public RecordSet searchEntityForWS(Record inputRecord);

    /**
     * Check if an entity has tax ID info.
     * @param inputRecord
     * @return Returns {@code true} if either "Tax ID" or "SSN" exists. Otherwise, returns {@code false};
     */
    public boolean hasTaxIdInfo(Record inputRecord);

    /**
     * Get entity type.
     * @param inputRecord
     * @return
     */
    public String getEntityType(Record inputRecord);

    /**
     * Load entity data into Person Modify & Org modify page, as well mini popup page
     * @param record
     * @return
     */
    public Record loadEntityData(Record record);

    /**
     * Get entity name
     * @param inputRecord
     * @return
     */
    String getEntityName(Record inputRecord);

    /**
     * Get entity info by entity ID.
     * @param inputRecord
     * @return
     */
    EntityInfo getEntityInfo(Record inputRecord);
}
