package dti.pm.entitymgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;


/**
 * Interface to handle Implementation of Entity Manager.
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
 * 03/08/2016       wdang       168418 - Added saveEntityRoleAddress.
 * ---------------------------------------------------
 */

public interface EntityManager {

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
     * Load entity detail info by given entityId
     *
     * @param entityId
     * @return
     */
    Record loadEntityDetail(String entityId);

    /**
     * get entity role type
     *
     * @param inputRecord
     * @return
     */
    Record getEntityRoleType(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of entities for given classifications and an effective date
     *
     * @param inputRecord Record contains input values
     *                    <p/>
     * @return RecordSet a RecordSet loaded with list of entities.
     */
    RecordSet loadAllEntity(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of entities for given name
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadEntityListByName(Record inputRecord);

    /**
     * Method to save entity role's address
     *
     * @param policyHeader instantce of the PolicyHeader object with current term/transaction data
     * @param inputRecords  a RecordSet with all address information
     */
    void saveEntityRoleAddress(PolicyHeader policyHeader, RecordSet inputRecords);

    public static final String BEAN_NAME = "EntityManager";
}
