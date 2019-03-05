package dti.ci.relationshipmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The business component of Relationship information.
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
 * 06/08/2017       jdingle     Issue 190314. Save performance.
 * 11/09/2018       Elvin       Issue 195835: grid replacement
 * ---------------------------------------------------
 */
public interface RelationshipManager {
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
     * @param inputRecord
     */
    public void expireRelationShips(Record inputRecord);

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
    public Record saveRelationship(Record inputRecord, RecordSet inputRS);

    /**
     * Save a relationship record for web service.
     * @param inputRecord
     * @return
     */
    public Record saveRelationshipWs(Record inputRecord);

    /**
     * Validate all validateOscRelationshipCode from DB.
     * @param record
     */
    public void validateOscRelationshipCode(Record record);

    /**
     * Check if relationship list filter valid.
     * @param relationshipListFilter
     */
    public void isRelationshipListFilterValid(String relationshipListFilter);

    /**
     * Get relationship desc.
     * @param inputRecord
     * @return
     */
    public String getRelationshipDesc(Record inputRecord);

    public Record getFieldDefaultValues(Record inputRecord);
}
