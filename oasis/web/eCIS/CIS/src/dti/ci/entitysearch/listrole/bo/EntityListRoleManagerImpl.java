package dti.ci.entitysearch.listrole.bo;


import com.delphi_tech.velocity.entityroleinquiryservice.*;
import dti.ci.entitysearch.listrole.data.EntityListRoleDAO;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.cs.velocityentitymgr.VelocityEntityManager;
import dti.cs.velocitypolicymgr.VelocityPolicyManager;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle Entity List Role page information.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 18, 2008
 *
 * @author ldong
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/16/2009       Jacky       Add 'Jurisdiction' logic for issue #97673 
 * 05/03/2010       fcb         Issue 105866: logic added for policy holder role.
 * 02/18/2011       kshen       Added method getGotoSourceUrl.
 * 08/18/2016       ylu         Issue 178205: handle with velocity data
 * 01/12/2017       Elvin       Issue 182136: Velocity Integration
 * ---------------------------------------------------
*/

public class EntityListRoleManagerImpl implements EntityListRoleManager {
    /**
     * Retrieves all Edi Extract History information
     *
     * @param entityPk String
     * @return RecordSet
     */
    public RecordSet loadEntityListRoleByEntity(String entityPk) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityListRoleByEntity", new Object[]{entityPk});
        }

        Record input = new Record();
        input.setFieldValue("entityId", new Long(entityPk));

        /* Gets Edi Extract History record set */
        RecordSet rs = getEntityListRoleDAO().loadEntityListRoleByEntity(input);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityListRoleByEntity", rs);
        }
        return rs;
    }

    public RecordSet loadEntityListRoleByEntity(String entityPk, String filter) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityListRoleByEntity", new Object[]{entityPk, filter});
        }

        Record input = new Record();
        input.setFieldValue("entityId", new Long(entityPk));
        if (null != filter && "accountHolderEntityId".equals(filter)) { // flag of searching account Holder only
            input.setFieldValue("roleTypeCode", "ACCTHOLDER");
        }

        if (null != filter && "policyHolderEntityId".equals(filter)) { // flag of searching policy Holder only
            input.setFieldValue("roleTypeCode", "POLHOLDER");
        }

        /* Gets Edi Extract History record set */
        RecordSet rs = getEntityListRoleDAO().loadEntityListRoleByEntity(input);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityListRoleByEntity", rs);
        }
        return rs;
    }

    /**
     * Get goto source url for role type code.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public String getGotoSourceUrl(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getGotoSourceUrl", new Object[]{inputRecord});
        }

        String gotoSourceUrl = getEntityListRoleDAO().getGotoSourceUrl(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getGotoSourceUrl", gotoSourceUrl);
        }

        return gotoSourceUrl;
    }

    /**
     * Get velocity data
     * @param entityPk
     * @return
     */
    @Override
    public RecordSet getVelocityPolicyData(String entityPk, List<String> fieldNameList) {
        Logger l = LogUtils.getLogger(this.getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(this.getClass().getName(), "getVelocityPolicyData", new Object[]{entityPk});
        }

        RecordSet recordset = new RecordSet();
        String clientId = getVelocityEntityManager().getClientIdByEntityPK(entityPk);
        EntityRoleInquiryResultType wsResponse = getVelocityPolicyManager().getVelocityPolicies(entityPk);
        if (wsResponse != null) {
            DBUtilityManager dbUtilityManager = (DBUtilityManager) ApplicationContext.getInstance().getBean("DBUtilityManager");
            for (PartyType party : wsResponse.getParty()) {
                if (clientId.equals(party.getClientId())) {
                    for (PartyRoleType partyRole : party.getPartyRole()) {
                        Record record = new Record();
                        if (fieldNameList != null && fieldNameList.size() > 0) {
                            for (String fieldName : fieldNameList) {
                                record.setFieldValue(fieldName, "");
                            }
                        }

                        record.setFieldValue("entityRoleId", dbUtilityManager.getNextSequenceNo());
                        record.setFieldValue("sourceTableName", "VELOCITY_POLICY");
                        mapObjectToRecord(getListRoleFieldElementMaps(), party, record);
                        mapObjectToRecord(getListRoleTypeFieldElementMaps(), partyRole, record);
                        for (PolicyType policy : wsResponse.getPolicy()) {
                            if (partyRole.getTargetNumberId().equals(policy.getPolicyNumberId())) {
                                mapObjectToRecord(getListRolePolicyFieldElementMaps(), policy, record);
                                break;
                            }
                        }

                        recordset.addRecord(record);
                    }
                }
            }
        }

        l.exiting(this.getClass().getName(), "getVelocityPolicyData", new Object[]{recordset});
        return recordset;
    }

    protected void mapObjectToRecord(List<FieldElementMap> fieldMapList, Object obj, Record record) {
        mapObjectToRecord(fieldMapList, obj, record, true);
    }

    protected void mapObjectToRecord(List<FieldElementMap> fieldMapList, Object obj, Record record,
                                     boolean overwriteFieldIfExists) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mapObjectToRecord",
                    new Object[]{fieldMapList, obj, record, overwriteFieldIfExists});
        }
        try {
            if (fieldMapList != null && obj != null && record != null) {
                for (FieldElementMap map : fieldMapList) {
                    if (!overwriteFieldIfExists && record.hasField(map.getFieldName())) {
                        continue;
                    }

                    String value = map.getElementValue(obj);

                    if (value != null) {
                        record.setFieldValue(map.getFieldName(), value);
                    }
                }
            }
        } catch (Exception e) {
            if (l.isLoggable(Level.FINER)) {
                l.log(Level.FINER, this.getClass().getName(), new  Object[]{fieldMapList,obj,record});
            }
        }
        l.exiting(getClass().getName(), "setMappedFieldValues");
    }

    public void verifyConfig() {
        if (getListRoleFieldElementMaps() == null)
            throw new ConfigurationException("The required property 'listRoleFieldElementMaps' is missing.");
        if (getListRoleTypeFieldElementMaps() == null)
            throw new ConfigurationException("The required property 'listRoleTypeFieldElementMaps' is missing.");
        if (getListRolePolicyFieldElementMaps() == null)
            throw new ConfigurationException("The required property 'listRolePolicyFieldElementMaps' is missing.");
    }

    public EntityListRoleDAO getEntityListRoleDAO() {
        return entityListRoleDAO;
    }

    public void setEntityListRoleDAO(EntityListRoleDAO entityLstRolDAO) {
        this.entityListRoleDAO = entityLstRolDAO;
    }

    public List<FieldElementMap> getListRoleFieldElementMaps() {
        return m_listRoleFieldElementMaps;
    }

    public void setListRoleFieldElementMaps(List<FieldElementMap> listRoleFieldElementMaps) {
        m_listRoleFieldElementMaps = listRoleFieldElementMaps;
    }

    public List<FieldElementMap> getListRoleTypeFieldElementMaps() {
        return m_listRoleTypeFieldElementMaps;
    }

    public void setListRoleTypeFieldElementMaps(List<FieldElementMap> listRoleTypeFieldElementMaps) {
        m_listRoleTypeFieldElementMaps = listRoleTypeFieldElementMaps;
    }

    public List<FieldElementMap> getListRolePolicyFieldElementMaps() {
        return m_listRolePolicyFieldElementMaps;
    }

    public void setListRolePolicyFieldElementMaps(List<FieldElementMap> listRolePolicyFieldElementMaps) {
        this.m_listRolePolicyFieldElementMaps = listRolePolicyFieldElementMaps;
    }

    public VelocityPolicyManager getVelocityPolicyManager() {
        return m_velocityPolicyManager;
    }

    public void setVelocityPolicyManager(VelocityPolicyManager velocityPolicyManager) {
        this.m_velocityPolicyManager = velocityPolicyManager;
    }

    public VelocityEntityManager getVelocityEntityManager() {
        return m_velocityEntityManager;
    }

    public void setVelocityEntityManager(VelocityEntityManager velocityEntityManager) {
        this.m_velocityEntityManager = velocityEntityManager;
    }

    private VelocityPolicyManager m_velocityPolicyManager;
    private VelocityEntityManager m_velocityEntityManager;
    private EntityListRoleDAO entityListRoleDAO;

    private List<FieldElementMap> m_listRoleFieldElementMaps;
    private List<FieldElementMap> m_listRoleTypeFieldElementMaps;
    private List<FieldElementMap> m_listRolePolicyFieldElementMaps;
}
