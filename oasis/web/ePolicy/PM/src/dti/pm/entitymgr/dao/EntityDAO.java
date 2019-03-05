package dti.pm.entitymgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for entity information.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 25, 2007
 *
 * @author sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/28/2010       Kenney      Added loadEntityListByName
 * 03/08/2016       wdang       168418 - Added updateEntityRoleAddress and saveEntityRoleAddress.
 * ---------------------------------------------------
 */

public interface EntityDAO {

    /**
     * Get type of a given entity
     * <p/>
     * /**
     * Get type of a given entity
     *
     * @param entityId entity ID
     * @return String containing the default state code
     */
    String getEntityType(String entityId);

    /**
     * Get name for a given entity ID
     *
     * @param entityId Risk entity ID.
     *                 <p/>
     * @return String a String contains an entity name.
     */
    String getEntityName(String entityId);

    /**
     * Get property name for a given entity ID
     *
     * @param entityId Risk entity ID.
     *                 <p/>
     * @return String a String contains an entity property name.
     */
    String getEntityPropertyName(String entityId);


    /**
     * Get entity attributes by given entityId
     *
     * @param entityId
     * @return
     */
    Record getEntityAttributes(String entityId);


    /**
     * Returns a RecordSet loaded with list of entities for given classifications and an effective date
     *
     * @param inputRecord Record contains input values
     *                    <p/>
     * @return RecordSet a RecordSet loaded with list of entities.
     */
    RecordSet loadAllEntity(Record inputRecord);

    /**
     * get entity role type
     *
     * @param inputRecord
     * @return
     */
    String getEntityRoleType(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of entities for given name
     * @param inputRecord
     * @return
     */
    RecordSet loadEntityListByName(Record inputRecord);

    /**
     * Update the entity role's address.
     *
     * @param inputRecord a Record with the updated information.
     */
    void updateEntityRoleAddress(Record inputRecord);

    /**
     * To save entity role's address.
     *
     * @param inputRecord a Record with address information for saving.
     */
    void saveEntityRoleAddress(Record inputRecord);

    public static final String BEAN_NAME = "EntityDAO";
}
