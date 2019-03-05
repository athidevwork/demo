package dti.ci.rolemgr.impl;

import dti.ci.core.CIFields;
import dti.ci.entitymgr.EntityFields;
import dti.ci.entitysearch.listrole.bo.EntityListRoleManager;
import dti.ci.rolemgr.RoleFields;
import dti.ci.rolemgr.RoleManager;
import dti.ci.rolemgr.dao.RoleDAO;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object to handle Role.
 * <p/>
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 28, 2018
 *
 * @author Herb Koenig
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/02/2018       hxk         Issue 109175: Entity Role refactor
 * 05/28/2018       ylu         Issue 109175: fix bug for refactor
 *                              1).search button don't work
 *                              2).add Velocity integration process
 * ---------------------------------------------------
*/

public class RoleManagerImpl implements RoleManager {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Get Role List info for an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadRoleList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadRoleList", new Object[]{inputRecord});
        }

        //get search criteria record
        Record searchCriteria = getSearchCriteria(inputRecord);
        //search role
        RecordSet rs = getRoleDAO().getRoleList(searchCriteria, AddSelectIndLoadProcessor.getInstance());

        //handle with velocity integration
        rs = processVelocityPolicies(inputRecord, rs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadRoleList", rs);
        }
        return rs;
    }

    /**
     * populate search criteria record for search
     * @param inputRecord
     * @return
     */
    public Record getSearchCriteria(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSearchCriteria", new Object[]{inputRecord});
        }
        Record searchCriteria = new Record();
        EntityFields.setEntityId(searchCriteria, inputRecord.getStringValue(CIFields.PK_PROPERTY));
        Iterator fieldNameIterator = inputRecord.getFieldNames();
        while (fieldNameIterator.hasNext()) {
            String fieldName = (String) fieldNameIterator.next();
            if (fieldName.startsWith(RoleFields.SEARCH_CRITERIA_PREFIX)) {
                String fieldValue = inputRecord.getStringValue(fieldName,"");
                searchCriteria.setFieldValue(fieldName.substring(RoleFields.SEARCH_CRITERIA_PREFIX.length()) ,fieldValue);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSearchCriteria", searchCriteria);
        }
        return searchCriteria;
    }

    /**
     * handle with integration velocity policy
     * @param inputRecord
     * @param roleRs
     * @return
     */
    public RecordSet processVelocityPolicies(Record inputRecord , RecordSet roleRs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processVelocityPolicies", new Object[]{inputRecord, roleRs});
        }

        EntityListRoleManager entityListRoleManager = (EntityListRoleManager) ApplicationContext.getInstance().getBean("EntityListRoleManager");
        RecordSet velocityRecords = entityListRoleManager.getVelocityPolicyData(EntityFields.getEntityId(inputRecord), roleRs.getFieldNameList());
        for (Record record : velocityRecords.getRecordList()) {
            record.setFieldValue(RequestIds.SELECT_IND, "0");
            record.setFieldValue("roleTypeCodeComputed", record.getStringValue("roleTypeDesc",""));
            record.setFieldValue("roleName", record.getStringValue("entityName",""));
            record.setFieldValue("externalIdComputed", "Velocity Policy# " + record.getStringValue("externalId", ""));

            //filter by search criteria
            String criteriaRoleCode = RoleFields.getSearchCriteriaRoleCode(inputRecord);
            if (!StringUtils.isBlank(criteriaRoleCode,true)) {
                if (!criteriaRoleCode.equalsIgnoreCase(record.getStringValue(RoleFields.ENT_ROLE_TYPE_CODE_ID))) {
                    continue;
                }
            }

            String criteriaExternalId = RoleFields.getSearchCriteriaExternalId(inputRecord);
            if (!StringUtils.isBlank(criteriaExternalId,true)) {
                if (!criteriaExternalId.equalsIgnoreCase(record.getStringValue(RoleFields.ENT_ROLE_EXTERNAL_ID))) {
                    continue;
                }
            }

            String criteriaEffectiveFromDate = RoleFields.getSearchCriteriaEffectiveFromDate(inputRecord);
            if (FormatUtils.isDate(criteriaEffectiveFromDate) &&
                    'Y' == DateUtils.isDate2AfterDate1(record.getStringValue(RoleFields.ENT_ROLE_EFF_FROM_DT_ID), criteriaEffectiveFromDate)) {
                continue;
            }

            String criteriaEffectiveToDate = RoleFields.getSearchCriteriaEffectiveToDate(inputRecord);
            if (FormatUtils.isDate(criteriaEffectiveToDate) &&
                    'Y' == DateUtils.isDate2AfterDate1(criteriaEffectiveToDate,record.getStringValue(RoleFields.ENT_ROLE_EFF_TO_DT_ID))) {
                continue;
            }
            roleRs.addRecord(record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processVelocityPolicies", roleRs);
        }
        return roleRs;
    }

    public void verifyConfig() {
        if (getRoleDAO() == null) {
            throw new ConfigurationException("The required property 'getRoleDAO' is missing.");
        }

        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
    }

    public RoleDAO getRoleDAO() {
        return m_roleDAO;
    }

    public void setRoleDAO(RoleDAO m_roleDAO) {
        this.m_roleDAO = m_roleDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private WorkbenchConfiguration m_workbenchConfiguration;

    private RoleDAO m_roleDAO;
}
