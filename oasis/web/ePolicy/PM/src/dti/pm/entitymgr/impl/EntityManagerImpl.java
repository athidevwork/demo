package dti.pm.entitymgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.pm.entitymgr.EntityManager;
import dti.pm.entitymgr.dao.EntityDAO;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.dao.PolicyDAO;
import dti.pm.riskmgr.coimgr.CoiFields;
import dti.pm.transactionmgr.TransactionFields;

import java.util.Date;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of EntityManager Interface.
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

public class EntityManagerImpl implements EntityManager {

    /**
     * Get type of a given entity
     * <p/>
     * /**
     * Get type of a given entity
     *
     * @param entityId entity ID
     * @return String containing the default state code
     */
    public String getEntityType(String entityId) {
        return getEntityDAO().getEntityType(entityId);
    }

    /**
     * Get name for a given entity ID
     *
     * @param entityId Risk entity ID.
     *                 <p/>
     * @return String a String contains an entity name.
     */
    public String getEntityName(String entityId) {
        return getEntityDAO().getEntityName(entityId);
    }


    /**
     * Get property name for a given entity ID
     *
     * @param entityId Risk entity ID.
     *                 <p/>
     * @return String a String contains an entity property name.
     */
    public String getEntityPropertyName(String entityId) {
        return getEntityDAO().getEntityPropertyName(entityId);
    }

    /**
     * get entity role type
     *
     * @param inputRecord
     * @return
     */
    public Record getEntityRoleType(Record inputRecord) {
        Record output = new Record();
        String entityRoleType = getEntityDAO().getEntityRoleType(inputRecord);
        output.setFieldValue("entityRoleType", entityRoleType);
        return output;
    }

    /**
     * Load entity detail info by given entityId
     *
     * @param entityId
     * @return
     */
    public Record loadEntityDetail(String entityId) {
        return getEntityDAO().getEntityAttributes(entityId);
    }

    /**
     * Returns a RecordSet loaded with list of entities for given classifications and an effective date
     *
     * @param inputRecord Record contains input values
     *                    <p/>
     * @return RecordSet a RecordSet loaded with list of entities.
     */
    public RecordSet loadAllEntity(Record inputRecord) {
        RecordSet rs = getEntityDAO().loadAllEntity(inputRecord);
        if (rs.getSize() == 0) {
            MessageManager.getInstance().addErrorMessage("pm.lookupEntity.NoDataFound");
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of entities for given name
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadEntityListByName(Record inputRecord){
        Logger l = LogUtils.enterLog(getClass(), "loadEntityListByName", new Object[]{inputRecord});
        RecordSet rs = null;
        rs = getEntityDAO().loadEntityListByName(inputRecord);
        l.exiting(getClass().toString(), "loadEntityListByName");
        return rs;
    }

    /**
     * Method to save entity role's address
     *
     * @param policyHeader instantce of the PolicyHeader object with current term/transaction data
     * @param inputRecords  a RecordSet with all address information
     */
    public void saveEntityRoleAddress(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveEntityRoleAddress", new Object[]{policyHeader, inputRecords});

        boolean needToInsert = false;
        String transactionLogId = null;
        if (inputRecords.getSummaryRecord().hasFieldValue(TransactionFields.TRANSACTION_LOG_ID)) {
            transactionLogId = TransactionFields.getTransactionLogId(inputRecords.getSummaryRecord());
        }
        else {
            transactionLogId = policyHeader.getLastTransactionId();
        }
        RecordSet newSelectedAddrRecords = inputRecords.getSubSet(new RecordFilter("SELECT_IND", "-1"));
        if (newSelectedAddrRecords == null || newSelectedAddrRecords.getSize() <= 0) {
            return;
        }
        Record newSelectedAddrRec = newSelectedAddrRecords.getRecord(0);
        String entityRoleId = null;
        if (inputRecords.getSummaryRecord().hasFieldValue(CoiFields.ENTITY_ROLE_ID)) {
            entityRoleId = CoiFields.getEntityRoleId(inputRecords.getSummaryRecord());
        }
        else {
            entityRoleId = PolicyFields.getEntityRoleId(newSelectedAddrRec);
        }
        RecordSet nullTransAddrRecords = inputRecords.getSubSet(new RecordFilter("transactionLogId", false));
        // the first time to link the address for entity role in current transaction, need to insert data
        if (nullTransAddrRecords.getSize() == inputRecords.getSize()) {
            needToInsert = true;
        }
        else {
            // get all address records which transactionLogId equals current transactionLogId
            RecordSet currentTransAddrRecords = inputRecords.getSubSet(new RecordFilter("transactionLogId", transactionLogId));
            if (currentTransAddrRecords.getSize() > 0) {
                // update the newly selected address association
                Record updateRec = new Record();
                PolicyFields.setAddressRoleXrefId(updateRec, PolicyFields.getAddressRoleXrefId(currentTransAddrRecords.getRecord(0)));
                PolicyFields.setAddressId(updateRec, PolicyFields.getAddressId(newSelectedAddrRec));
                getEntityDAO().updateEntityRoleAddress(updateRec);
            }
            else {
                // there must have address associated with entity role in prior transaction
                RecordSet selectedAddrRecords = inputRecords.getSubSet(new RecordFilter("selectedAddressB", "Y"));
                Record selectedAddrRec = selectedAddrRecords.getRecord(0);
                if (PolicyFields.getAddressId(newSelectedAddrRec).equals(PolicyFields.getAddressId(selectedAddrRec))) {
                    // do nothing
                }
                else {
                    // update the old record
                    Record updateRec = new Record();
                    PolicyFields.setAddressRoleXrefId(updateRec, PolicyFields.getAddressRoleXrefId(selectedAddrRec));
                    PolicyFields.setEffectiveToDate(updateRec, DateUtils.formatDate(new Date()));
                    getEntityDAO().updateEntityRoleAddress(updateRec);

                    // insert an new record
                    needToInsert = true;
                }
            }
        }

        if (needToInsert) {
            Record insertRec = new Record();
            PolicyFields.setEntityRoleId(insertRec, entityRoleId);
            PolicyFields.setAddressId(insertRec, PolicyFields.getAddressId(newSelectedAddrRec));
            PolicyFields.setEffectiveFromDate(insertRec, DateUtils.formatDate(new Date()));
            PolicyFields.setEffectiveToDate(insertRec, "1/1/3000");
            TransactionFields.setTransactionLogId(insertRec, transactionLogId);
            getEntityDAO().saveEntityRoleAddress(insertRec);
        }

        l.exiting(getClass().getName(), "saveEntityRoleAddress");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getEntityDAO() == null)
            throw new ConfigurationException("The required property 'EntityDAO' is missing.");
    }

    public EntityManagerImpl() {
    }

    public EntityDAO getEntityDAO() {
        return m_EntityDAO;
    }

    public void setEntityDAO(EntityDAO EntityDAO) {
        m_EntityDAO = EntityDAO;
    }

    private EntityDAO m_EntityDAO;
}
