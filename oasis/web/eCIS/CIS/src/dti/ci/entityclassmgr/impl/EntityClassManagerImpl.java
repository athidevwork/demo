package dti.ci.entityclassmgr.impl;

import dti.ci.core.CIFields;
import dti.ci.entityclassmgr.EntityClassFields;
import dti.ci.entityclassmgr.EntityClassManager;
import dti.ci.entityclassmgr.dao.EntityClassDAO;
import dti.ci.entitymgr.EntityFields;
import dti.ci.helpers.ICIConstants;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * 05/17/2018       jdingle     Issue 189300. Correction for batch update.
 * 09/27/2018       jdingle     Issue 191748: Move network validation to OBR.
 * 10/31/2018       ylu         Issue 195835: per code review, as added entityClassId web field in webWB,
 *                                            made according change for this web field
 * ---------------------------------------------------
 */
public class EntityClassManagerImpl implements EntityClassManager{
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Load entity class record by given entity class id.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record loadEntityClass(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityClass", new Object[]{inputRecord});
        }

        // Load entity class.
        Record entityClassInfo = getEntityClassDAO().loadEntityClass(inputRecord);

        // Add entityClass_ prefix.
        Record result = addEntityClassPrefix(entityClassInfo);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityClass", result);
        }
        return result;
    }

    /**
     * Load all entity class of an entity.
     *
     * Refactor from the method:
     * dti.ci.helpers.CIEntityClassListHelper#retrieveClassList(java.sql.Connection, java.lang.String)
     *
     * @param inputRecord The input record contains entity PK and entity class code(optional).
     * @return
     */
    @Override
    public RecordSet loadAllEntityClass(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEntityClass", new Object[]{inputRecord});
        }

        Record record = getSearchEntityClassCriteria(inputRecord);

        RecordSet rs = getEntityClassDAO().loadAllEntityClass(record, new EntityClassListRecordLoadProcessor());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEntityClass", rs);
        }
        return rs;
    }

    /**
     * Add entity class.
     *
     * @param inputRecord
     * @return The PK (entity_class_pk) of the new entity class.
     */
    @Override
    public void addEntityClass(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityClass", new Object[]{inputRecord});
        }

        // Get entity class info from input record.
        RecordSet entityClassInfoRs = getEntityClassRecordSetForAdd(inputRecord);

        // Validate add entity class.
        validateAllEntityClass(entityClassInfoRs);

        // Save entity class
        getEntityClassDAO().saveEntityClass(entityClassInfoRs);

        l.exiting(getClass().getName(), "addEntityClass");;
    }

    /**
     * Add entity class.
     *
     * @param inputRecord
     * @return The PK (entity_class_pk) of the new entity class.
     */
    @Override
    public void modifyEntityClass(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "modifyEntityClass", new Object[]{inputRecord});
        }

        // Get entity class info from input record.
        RecordSet entityClassInfoRs = getEntityClassRecordSetForModify(inputRecord);

        // Validate entity class modify
        validateAllEntityClass(entityClassInfoRs);

        // Save entity class.
        getEntityClassDAO().saveEntityClass(entityClassInfoRs);

        l.exiting(getClass().getName(), "modifyEntityClass");
    }

    /**
     * Save all entity class of an entity.
     *
     * dti.ci.helpers.CIEntityClassListHelper#save(java.sql.Connection, java.lang.String)
     *
     * @param rs
     * @return
     */
    @Override
    public void deleteEntityClasses(RecordSet rs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteEntityClasses", new Object[]{rs});
        }

        RecordSet deletedRecords = rs.getSubSet(
                new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));

        getEntityClassDAO().saveEntityClass(deletedRecords);

        l.exiting(getClass().getName(), "deleteEntityClasses");
    }

    /**
     * Save entity class codes for web service PartyChangeService.
     *
     * @param record
     * @return
     */
    @Override
    public Record saveEntityClassWs(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityClassWs", new Object[]{record});
        }
        
        Record result = getEntityClassDAO().saveEntityClassWs(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEntityClassWs", result);
        }
        return result;
    }

    /**
     * Check if the current entity class is overlap with another entity class.
     *
     * @param record
     * @return
     */
    @Override
    public boolean hasOverlapEntityClass(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasOverlapEntityClass", new Object[]{record});
        }

        boolean result = getEntityClassDAO().hasOverlapEntityClass(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasOverlapEntityClass", result);
        }
        return result;
    }

    /**
     * Get search entity class criteria from input record.
     * @param inputRecord
     * @return
     */
    protected Record getSearchEntityClassCriteria(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSearchEntityClassCriteria", new Object[]{inputRecord});
        }

        Record record = new Record();

        EntityFields.setEntityId(record, inputRecord.getStringValue(ICIConstants.PK_PROPERTY));
        EntityClassFields.setEntityClassCode(record, inputRecord.getStringValueDefaultEmpty(EntityClassFields.FILTER_CRITERIA_PREFIX + EntityClassFields.ENTITY_CLASS_CODE));


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSearchEntityClassCriteria", record);
        }
        return record;
    }

    /**
     * Get entity class info from input record for adding entity class..
     * @param inputRecord
     * @return
     */
    protected RecordSet getEntityClassRecordSetForAdd(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityClassInfo", new Object[]{inputRecord});
        }

        RecordSet rs = new RecordSet();

        // Get entity class codes.
        String[] entityClassCodes = inputRecord.getStringValueDefaultEmpty(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.ENTITY_CLASS_CODE).split(",");
        for (String entityClassCode : entityClassCodes) {
            if (!StringUtils.isBlank(entityClassCode, true)) {
                Record record = new Record();
                record.setFieldValue(EntityClassFields.ENTITY_CLASS_CODE, entityClassCode);
                record.setUpdateIndicator(UpdateIndicator.INSERTED);
                rs.addRecord(record);
            }
        }

        // Get other entity class code info.
        List<String> fieldNames = inputRecord.getFieldNameList();
        for (String fieldName : fieldNames) {
            if (fieldName.startsWith(EntityClassFields.ENTITY_CLASS_PREFIX) &&
                    !fieldName.equals(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.ENTITY_CLASS_CODE)) {
                if (fieldName.equals(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.NETWORK_DISCOUNT)) {
                    // The network discount column in DB is number(5,2). So we need to remove the percentage sign.
                    rs.setFieldValueOnAll(
                            StringUtils.strRight(fieldName, EntityClassFields.ENTITY_CLASS_PREFIX),
                            inputRecord.getStringValueDefaultEmpty(fieldName).replace("%", "")
                    );
                } else {
                    rs.setFieldValueOnAll(
                            StringUtils.strRight(fieldName, EntityClassFields.ENTITY_CLASS_PREFIX),
                            inputRecord.getFieldValue(fieldName)
                    );
                }
            }
        }

        // Set row status for adding a new record.
        rs.setFieldValueOnAll(OasisRecordSetHelper.ROW_STATUS, OasisRecordSetHelper.NEW);
        rs.setFieldValueOnAll(CIFields.ENTITY_ID, inputRecord.getStringValue(CIFields.ENTITY_ID));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityClassInfo", rs);
        }
        return rs;
    }

    /**
     * Get entity class record set for modify
     *
     * @param inputRecord
     * @return
     */
    protected RecordSet getEntityClassRecordSetForModify(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityClassRecordSetForModify", new Object[]{inputRecord});
        }

        
        RecordSet rs = new RecordSet();

        Record modifiedRecord = new Record();
        // Get entity id.
        modifiedRecord.setFieldValue(EntityClassFields.ENTITY_CLASS_ID, inputRecord.getStringValue(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.ENTITY_CLASS_ID));
        modifiedRecord.setFieldValue(CIFields.ENTITY_ID, inputRecord.getStringValue(CIFields.ENTITY_ID));
        modifiedRecord.setFieldValue(OasisRecordSetHelper.ROW_STATUS, OasisRecordSetHelper.MODIFIED);
        modifiedRecord.setUpdateIndicator(UpdateIndicator.UPDATED);

        // Get other entity class code info.
        List<String> fieldNames = inputRecord.getFieldNameList();
        for (String fieldName : fieldNames) {
            if (fieldName.startsWith(EntityClassFields.ENTITY_CLASS_PREFIX)) {
                if (fieldName.equals(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.NETWORK_DISCOUNT)) {
                    // The network discount column in DB is number(5,2). So we need to remove the percentage sign.
                    modifiedRecord.setFieldValue(
                            StringUtils.strRight(fieldName, EntityClassFields.ENTITY_CLASS_PREFIX),
                            inputRecord.getStringValueDefaultEmpty(fieldName).replace("%", "")
                    );
                } else {
                    modifiedRecord.setFieldValue(
                            StringUtils.strRight(fieldName, EntityClassFields.ENTITY_CLASS_PREFIX),
                            inputRecord.getFieldValue(fieldName)
                    );
                }
            }
        }

        String entityType = inputRecord.getStringValueDefaultEmpty(CIFields.ENTITY_TYPE);
        String entityClassCode = modifiedRecord.getStringValue(EntityClassFields.ENTITY_CLASS_CODE);

        // Process Network discount effective from date.
        boolean networkDiscountEnabled = YesNoFlag.getInstance(getSysParmProvider().getSysParm(EntityClassFields.CI_ENABLE_NETWORK_DISCOUNT, "N")).booleanValue();

        if (networkDiscountEnabled &&
                !StringUtils.isBlank(entityType) && entityType.charAt(0) == CIFields.ENTITY_TYPE_ORG_CHAR &&
                EntityClassFields.NETWORK_ENTITY_CLASS_CODE.equals(entityClassCode)) {
            // Expire the original record.
            Record expiredEntityClassRecord = new Record();
            expiredEntityClassRecord.setFieldValue(EntityClassFields.ENTITY_CLASS_ID, modifiedRecord.getStringValue(EntityClassFields.ENTITY_CLASS_ID));
            expiredEntityClassRecord.setFieldValue(EntityClassFields.EFFECTIVE_TO_DATE, modifiedRecord.getStringValueDefaultEmpty(EntityClassFields.EFFECTIVE_FROM_DATE));
            expiredEntityClassRecord.setFieldValue(OasisRecordSetHelper.ROW_STATUS, ROW_STATUS_EXPIRE);

            // Add the expired record to changed record set.
            rs.addRecord(expiredEntityClassRecord);

            // Set the row status of the modified record to add a new one.
            modifiedRecord.setFieldValue(OasisRecordSetHelper.ROW_STATUS, OasisRecordSetHelper.NEW);
        }

        rs.addRecord(modifiedRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityClassRecordSetForModify", rs);
        }
        return rs;
    }

    /**
     * Add entity class prefix to the entity class info record..
     * @param record
     */
    protected Record addEntityClassPrefix(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addEntityClassPrefix", new Object[]{record});
        }

        Record result = new Record();

        List<String> fieldNames = record.getFieldNameList();
        for (String fieldName : fieldNames) {
            result.setFieldValue(EntityClassFields.ENTITY_CLASS_PREFIX + fieldName, record.getFieldValue(fieldName));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addEntityClassPrefix", result);
        }
        return result;
    }

    /**
     * Validate add entity class code.
     * @param rs
     */
    protected void validateAllEntityClass(RecordSet rs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllEntityClass", new Object[]{rs});
        }

        int errorMessageCount = getMessageManager().getErrorMessageCount();

        List<Record> records = rs.getRecordList();

        for (Record record : records) {
            if (!ROW_STATUS_EXPIRE.equals(record.getStringValueDefaultEmpty(OasisRecordSetHelper.ROW_STATUS))) {
                // 1. Verify duplicate codes
                if (hasOverlapEntityClass(record)) {
                    getMessageManager().addErrorMessage("ci.entity.class.overlapClass",
                            new Object[]{record.getStringValue(EntityClassFields.ENTITY_CLASS_CODE)});
                }
            }
        }

        if (getMessageManager().getErrorMessageCount() > errorMessageCount) {
            ValidationException ve = new ValidationException("Failed to verify Entity Class.");
            l.throwing(getClass().getName(), "validateAllEntityClass", ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validateAllEntityClass");
    }

    /**
     * Process modify entity class record.
     * @param record
     */
    public void processEntityClassInfoForModify(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processEntityClassRecordForModify", new Object[]{record});
        }

        // Get entity type.
        String entityType = record.getStringValueDefaultEmpty(CIFields.ENTITY_TYPE);

        // Process Network discount effective from date.
        boolean networkDiscountEnabled = YesNoFlag.getInstance(getSysParmProvider().getSysParm(EntityClassFields.CI_ENABLE_NETWORK_DISCOUNT, "N")).booleanValue();

        // Check if network discount is enabled, and the current entity type is Org.
        if (networkDiscountEnabled && !StringUtils.isBlank(entityType) && entityType.charAt(0) == CIFields.ENTITY_TYPE_ORG_CHAR) {
            // Get entity class code.
            String entityClassCode = record.getStringValueDefaultEmpty(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.ENTITY_CLASS_CODE);

            // Check if the current entity code is network.
            if (EntityClassFields.NETWORK_ENTITY_CLASS_CODE.equals(entityClassCode)) {
                // Get the effective dates.
                String effectiveFromDate = record.getStringValueDefaultEmpty(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.EFFECTIVE_FROM_DATE);
                String effectiveToDate = record.getStringValueDefaultEmpty(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.EFFECTIVE_TO_DATE);
                String today = FormatUtils.formatDate(new Date());

                if (StringUtils.isBlank(effectiveToDate) || "01/01/3000".equals(effectiveToDate)) {
                    if (StringUtils.isBlank(effectiveFromDate) ||
                            "01/01/3000".equals(effectiveFromDate) ||
                            DateUtils.isDate2AfterDate1(effectiveFromDate, today) == 'Y') {
                        // If effective to date is empty, or effective from date is before today, use today as the new effective from date.
                        record.setFieldValue(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.EFFECTIVE_FROM_DATE, today);
                    }
                } else {
                    // If the original effective to date is not null, use the original effective to date as the new effective from date.
                    record.setFieldValue(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.EFFECTIVE_FROM_DATE, effectiveToDate);
                }

                // Set default effective to date to empty.
                record.setFieldValue(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.EFFECTIVE_TO_DATE, "");
            }
        }

        l.exiting(getClass().getName(), "processEntityClassRecordForModify");
    }

    public void verifyConfig() {
        if (getEntityClassDAO() == null)
            throw new ConfigurationException("The required property 'entityClassDAO' is missing.");
        if (getSysParmProvider() == null)
            throw new ConfigurationException("The required property 'sysParmProvider' is missing.");
        if (getMessageManager() == null)
            throw new ConfigurationException("The required property 'messageManager' is missing.");
    }

    public EntityClassDAO getEntityClassDAO() {
        return m_entityClassDAO;
    }

    public void setEntityClassDAO(EntityClassDAO entityClassDAO) {
        m_entityClassDAO = entityClassDAO;
    }

    public SysParmProvider getSysParmProvider() {
        return m_sysParmProvider;
    }

    public void setSysParmProvider(SysParmProvider sysParmProvider) {
        m_sysParmProvider = sysParmProvider;
    }

    public MessageManager getMessageManager() {
        return m_messageManager;
    }

    public void setMessageManager(MessageManager messageManager) {
        m_messageManager = messageManager;
    }

    private EntityClassDAO m_entityClassDAO;
    private SysParmProvider m_sysParmProvider;
    private MessageManager m_messageManager;

    private static final String ROW_STATUS_EXPIRE = "EXPIRED";
}
