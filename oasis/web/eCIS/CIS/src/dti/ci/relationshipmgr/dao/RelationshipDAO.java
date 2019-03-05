package dti.ci.relationshipmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The DAO for Relationship information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 2, 2012
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/07/2012       kshen       Isuse 131290. Added method getRelationshipDesc.
 * ---------------------------------------------------
 */
public interface RelationshipDAO {
    /**
     * Load all relationship of an entity.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllRelationship(Record inputRecord);

    /**
     * Get relationship list filter pref.
     * @param record
     * @return
     */
    public String getRelationshipListFilterPref(Record record);

    /**
     * Expire the relationships with a expire date.
     * @param inputRecordSet
     */
    public void expireRelationShips(RecordSet inputRecordSet);

    /**
     * Load relationship data.
     * @param inputRecord
     * @return
     */
    public Record loadRelationship(Record inputRecord);

    /**
     * Get initial values for adding relationship.
     * @param inputRecord
     * @return
     */
    public Record getInitInfoForRelationship(Record inputRecord);

    /**
     * Save a relationship record.
     * @param inputRecord
     * @return
     */
    public Record saveRelationship(Record inputRecord);

    /**
     * Save a relationship record for web service.
     * @param inputRecord
     * @return
     */
    public Record saveRelationshipWs(Record inputRecord);

    /**
     * Validate all validateOscRelationshipCode from DB.
     * @param record
     * @return
     */
    public String validateOscRelationshipCode(Record record);

    /**
     * Get all EntityRelationship from DB
     *
     * @param record
     * @return
     */
    public RecordSet loadAllAvailableEntityRelationships(Record record);

    /**
     * Get relationship desc.
     * @param inputRecord
     * @return
     */
    public String getRelationshipDesc(Record inputRecord);
}
