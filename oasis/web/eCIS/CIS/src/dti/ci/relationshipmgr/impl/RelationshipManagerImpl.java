package dti.ci.relationshipmgr.impl;

import dti.ci.core.CIFields;
import dti.ci.relationshipmgr.RelationshipManager;
import dti.ci.relationshipmgr.dao.RelationshipDAO;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.ci.relationshipmgr.RelationshipFields;
import dti.oasis.util.SysParmProvider;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object to handle relationship.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 10, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/23/2010       kshen       Added method expire.
 * 02/15/2011       Michael     Changed for 112658.
 * 04/18/2012       kshen       Changed for 131383.
 * 06/08/2017       jdingle     Issue 190314. Save performance.
 * 11/09/2018       Elvin       Issue 195835: grid replacement
 * ---------------------------------------------------
*/

public class RelationshipManagerImpl implements RelationshipManager {

    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public Record getFieldDefaultValues(Record inputRecord) {
        l.entering(getClass().getName(), "getFieldDefaultValues");

        String actionClassName = inputRecord.getStringValueDefaultEmpty("actionClassName");
        if (StringUtils.isBlank(actionClassName)) {
            throw new AppException("No action class name.");
        }

        Record outRecord = getWorkbenchConfiguration().getDefaultValues(actionClassName);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFieldDefaultValues", outRecord);
        }
        return outRecord;
    }

    /**
     * Load all relationship of an entity.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllRelationship(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRelationship", new Object[]{inputRecord});
        }

        RecordSet rs = getRelationshipDAO().loadAllRelationship(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRelationship", rs);
        }
        return rs;
    }

    /**
     * Get relationship list filter pref.
     *
     * @param record
     * @return
     */
    @Override
    public String getRelationshipListFilterPref(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRelationshipListFilterPref", new Object[]{record});
        }

        String filterPref = getRelationshipDAO().getRelationshipListFilterPref(record);
        if (StringUtils.isBlank(filterPref))
            filterPref = RelationshipFields.RELATIONSHIP_LIST_FILTER_ACTIVE;

        isRelationshipListFilterValid(filterPref);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRelationshipListFilterPref", filterPref);
        }
        return filterPref;
    }

    /**
     * Expire the relationships with a expire date.
     *
     * @param inputRecord
     */
    @Override
    public void expireRelationShips(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "expireRelationShips", new Object[]{inputRecord});
        }

        String[] selectedKeys = inputRecord.getStringValue(RelationshipFields.SELECTE_RECORD_IDS).split("\\^");
        String expireDate = inputRecord.getStringValue(RelationshipFields.EXP_DATE);

        RecordSet rs = new RecordSet();
        for (int i = 0; i < selectedKeys.length; i++) {
            if (!StringUtils.isBlank(selectedKeys[i])) {
                Record record = new Record();
                record.setFieldValue("entityRelationId", selectedKeys[i]);
                record.setFieldValue(RelationshipFields.EXP_DATE, expireDate);

                rs.addRecord(record);
            }
        }

        getRelationshipDAO().expireRelationShips(rs);

        l.exiting(getClass().getName(), "expireRelationShips");
    }

    /**
     * Load relationship data.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record loadRelationship(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadRelationship", new Object[]{inputRecord,});
        }

        Record record = getRelationshipDAO().loadRelationship(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadRelationship", record);
        }

        return record;
    }

    /**
     * Get initial values for adding relationship.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record getInitInfoForRelationship(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitInfoForRelationship", new Object[]{inputRecord});
        }

        Record paramRecord = new Record();
        paramRecord.setFieldValue(CIFields.ENTITY_ID, inputRecord.getStringValue(CIFields.PK_PROPERTY));
        
        Record record = getRelationshipDAO().getInitInfoForRelationship(paramRecord);

        String dateOfBirth = record.getStringValue(RelationshipFields.DATE_OF_BIRTH, "");
        if (StringUtils.isBlank(dateOfBirth)) {
            record.setFieldValue(RelationshipFields.DATE_OF_BIRTH, "-1");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitInfoForRelationship", record);
        }

        return record;
    }

    /**
     * Save a relationship record.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record saveRelationship(Record inputRecord, RecordSet inputRS) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveRelationship", new Object[]{inputRecord});
        }

        // We now get the RecordSet from the user session to avoid another retrieval from the database.
        RecordSet rs = inputRS;
        if (rs == null || rs.getSize() == 0) {
            rs = getRelationshipDAO().loadAllAvailableEntityRelationships(inputRecord);
        }

        validateRelationship(inputRecord, rs);

        Record record = getRelationshipDAO().saveRelationship(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveRelationship", record);
        }

        return record;
    }

    /**
     * Save a relationship record for web service.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record saveRelationshipWs(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveRelationshipWs", new Object[]{inputRecord});
        }

        RecordSet rs = getRelationshipDAO().loadAllAvailableEntityRelationships(inputRecord);
        validateRelationship(inputRecord, rs);

        Record record = getRelationshipDAO().saveRelationshipWs(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveRelationshipWs", record);
        }

        return record;
    }

    /**
     * Validate all validateOscRelationshipCode from DB.
     *
     * @param record
     * @return
     */
    @Override
    public void validateOscRelationshipCode(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateOscRelationshipCode", new Object[]{record});
        }

        String messageKey = getRelationshipDAO().validateOscRelationshipCode(record);
        if (!StringUtils.isBlank(messageKey)) {
            MessageManager.getInstance().addErrorMessage(messageKey);
            throw new ValidationException("The entities were not valid for osc relationship.");
        }

        l.exiting(getClass().getName(), "validateOscRelationshipCode");
    }

    /**
     * Check whether relationship List Filter preference is valid.
     *
     * @param relationshipListFilter relationship list filter should be(ALL, ACTIVE, EXPIRED)
     * @throws Exception
     */
    public void isRelationshipListFilterValid(String relationshipListFilter) {
        if (!RelationshipFields.RELATIONSHIP_LIST_FILTER_ACTIVE.equals(relationshipListFilter) &&
                !RelationshipFields.RELATIONSHIP_LIST_FILTER_ALL.equals(relationshipListFilter) &&
                !RelationshipFields.RELATIONSHIP_LIST_FILTER_EXPIRED.equals(relationshipListFilter))
            throw new IllegalArgumentException(new StringBuffer().append("Relationship List Filter [").append(relationshipListFilter)
                    .append("] is not valid.").toString());
    }

    /**
     * Get relationship desc.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public String getRelationshipDesc(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRelationshipDesc", new Object[]{inputRecord});
        }
        
        String relationshipDesc = getRelationshipDAO().getRelationshipDesc(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRelationshipDesc", relationshipDesc);
        }
        return relationshipDesc;
    }

    /**
     * Validate all  Relationship
     *
     * @param record
     * @param rs
     */
    protected void validateRelationship(Record record, RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRelationship", new Object[]{record});
        }
        validateModifiedRecordPrimaryEmployer(record, rs);
        validateRelationTypeCodeName(record, rs);
        validateReverseRelationship(record, rs);
        validatePrimaryEmployer(record, rs);

        l.exiting(getClass().getName(), "validateRelationship");
    }

    /**
     * Validate Modified Record Primary Employer
     *
     * @param record
     */
    protected void validateModifiedRecordPrimaryEmployer(Record record, RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateModifiedRecordPrimaryEmployer", new Object[]{record});
        }
        if (rs != null && rs.getSize() != 0) {
            return;
        }
        String CS_VAL_PRIM_EMP = SysParmProvider.getInstance().getSysParm(RelationshipFields.SYSPARM_CS_VAL_PRIM_EMP, "N");
        if (CS_VAL_PRIM_EMP.equals("N")) {
            return;
        }
        String entityId = record.getStringValue("pk");
        String relationTypeCode = record.getStringValue("relationTypeCode");
        String entityParentId = record.getStringValue("entityParentId");
        String effectiveToDate = record.getStringValue("effectiveToDate", "");
        String reverseRelationIndicator = record.getStringValue("reverseRelationIndicator");
        String primaryAffiliationB;
        if (record.hasField("primaryAffiliationB")) {
            primaryAffiliationB = "-1";
        } else {
            primaryAffiliationB = "0";
        }
        Boolean hasEmployeeRelation = false;
        Boolean hasPrimaryEmployee = false;
        if (!"REVERSE RELATION".equals(reverseRelationIndicator)
                && (StringUtils.isBlank(effectiveToDate) || effectiveToDate.equals("01/01/3000"))
                && entityParentId.equals(entityId)
                && relationTypeCode.equals("EMPLOYEE")) {
            hasEmployeeRelation = true;
            if (primaryAffiliationB.equals("-1")) {
                hasPrimaryEmployee = true;
            }
        }

        if (hasEmployeeRelation && !hasPrimaryEmployee) {
            MessageManager.getInstance().addErrorMessage("ci.entity.message.relation.active");
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            ValidationException ve = new  ValidationException("Validate the Modified Record Primary Employer error.");
            l.throwing(getClass().getName(), "validateModifiedRecordPrimaryEmployer", ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validateModifiedRecordPrimaryEmployer");
    }

    /**
     * Validate all  RelationTypeCode and Name
     *
     * @param record
     * @param rs
     */
    protected void validateRelationTypeCodeName(Record record, RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRelationTypeCodeName", new Object[]{record});
        }

        try {
            String entityPK = record.getStringValue("pk");
            String entityRelationId = record.getStringValue("entityRelationId");
            String relationTypeCode = record.getStringValue("relationTypeCode");
            String entityParentId = record.getStringValue("entityParentId");
            String entityChildId = record.getStringValue("entityChildId");
            String entityDiffFK = getDiffFK(entityPK, entityParentId, entityChildId);
            int effectiveFromDate;
            int effectiveToDate;
            int accountingToDate;
            effectiveFromDate = DateUtils.daysDiff(BASE_FROM_DATE, record.getStringValue("effectiveFromDate", BASE_FROM_DATE));
            effectiveToDate = DateUtils.daysDiff(BASE_FROM_DATE, record.getStringValue("effectiveToDate", BASE_TO_DATE));
            if (record.getStringValue("accountingToDate") == null || StringUtils.replace(record.getStringValue("accountingToDate"), "null", "").equals(""))
                accountingToDate = DateUtils.daysDiff(BASE_FROM_DATE, BASE_TO_DATE);
            else {
                accountingToDate = DateUtils.daysDiff(BASE_FROM_DATE, record.getStringValue("accountingToDate"));
                if (relationTypeCode.equals("AW"))
                    return;
            }
            //      Skip validation when (sys parm CS_INCL_SAMEFFDT_REL = N and
            //      effective_from_date = effective_to_date) or
            //      accounting_to_date <> 1/1/3000
            String CS_INCL_SAMEFFDT_REL = SysParmProvider.getInstance().getSysParm(RelationshipFields.SYSPARM_CS_INCL_SAMEFFDT_REL, "N");
            if ((CS_INCL_SAMEFFDT_REL.equals("N") && effectiveFromDate == effectiveToDate) || accountingToDate != DateUtils.daysDiff(BASE_FROM_DATE, BASE_TO_DATE)) {
                return;
            }
            String CS_EXCL_ENDDT_REL = SysParmProvider.getInstance().getSysParm(RelationshipFields.SYSPARM_CS_EXCL_ENDDT_REL, "N");
            if (CS_EXCL_ENDDT_REL.equals("Y")) {
                effectiveToDate = DateUtils.daysDiff(BASE_FROM_DATE, DateUtils.addDay(record.getStringValue("effectiveToDate"), -1));
            }
            for (int i = 0; i < rs.getSize(); i++) {
                Record recordOld = rs.getRecord(i);
                Relationship oldPart = retrieveFields(recordOld, record);

                if (oldPart.entityRelationPK_compare.equals(entityRelationId)) {
                    continue;
                }
                if (entityDiffFK.equals(oldPart.entityDiffFK_compare)) {
                    if (relationTypeCode.equals(oldPart.relationTypeCode_compare)) {

                        /*
                          Skip validation when (sys parm CS_INCL_SAMEFFDT_REL = N and
                          effective_from_date = effective_to_date) or
                          accounting_to_date <> 1/1/3000
                        */
                        if ((CS_INCL_SAMEFFDT_REL.equals("N") && oldPart.effectiveFromDate_compare == oldPart.effectiveToDate_compare) || oldPart.accountingToDate_compare != DateUtils.daysDiff(BASE_FROM_DATE, BASE_TO_DATE)) {
                            continue;
                        }
                        if (CS_EXCL_ENDDT_REL.equals("Y")) {
                            oldPart.effectiveToDate_compare = DateUtils.daysDiff(BASE_FROM_DATE, DateUtils.addDay(recordOld.getStringValue("effectiveToDate"), -1));
                        }

                        if (((effectiveFromDate >= oldPart.effectiveFromDate_compare && effectiveFromDate <= oldPart.effectiveToDate_compare) ||
                                (effectiveToDate >= oldPart.effectiveFromDate_compare && effectiveToDate <= oldPart.effectiveToDate_compare) ||
                                (oldPart.effectiveFromDate_compare >= effectiveFromDate && oldPart.effectiveFromDate_compare <= effectiveToDate) ||
                                (oldPart.effectiveToDate_compare >= effectiveFromDate && oldPart.effectiveToDate_compare <= effectiveToDate))
                                && oldPart.effectiveFromDate_compare != oldPart.effectiveToDate_compare) {
                            MessageManager.getInstance().addErrorMessage("ci.entity.message.nameAndRelationType.overlap");
                            break;
                        }
                    }//end if(rel_type == rel_type_compare)
                }//end if (entityDiffFK == entityDiffFK_compare )
            }
        } catch (Exception e) {

        }
        if (MessageManager.getInstance().hasErrorMessages()) {
            ValidationException ve = new ValidationException("Validate the  RelationTypeCode and Name error.");
            l.throwing(getClass().getName(), "validateRelationTypeCodeName", ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validateRelationTypeCodeName");
    }

    /**
     * Validate all  Primary Employer
     *
     * @param record
     * @param rs
     */
    protected void validatePrimaryEmployer(Record record, RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePrimaryEmployer", new Object[]{record});
        }

        List list = new ArrayList();
        try {
            Boolean hasEmployeeRelation = false;
            Boolean hasPrimaryEmployee = false;
            int primaryEmployeeCount = 0;
            String CS_VAL_PRIM_EMP = SysParmProvider.getInstance().getSysParm(RelationshipFields.SYSPARM_CS_VAL_PRIM_EMP, "N");
            if (CS_VAL_PRIM_EMP.equals("N")) {
                return;
            }

            String entityRelationId = record.getStringValue("entityRelationId");
            String relationTypeCode = record.getStringValue("relationTypeCode");
            String effectiveFromDate = record.getStringValue("effectiveFromDate");
            String effectiveToDate = record.getStringValue("effectiveToDate");
            String reverseRelationIndicator = record.getStringValue("reverseRelationIndicator");
            String primaryAffiliationB;
            if (record.hasField("primaryAffiliationB")) {
                primaryAffiliationB = "Y";
            } else {
                primaryAffiliationB = "N";
            }
            if ((reverseRelationIndicator == null || reverseRelationIndicator.equals("") || reverseRelationIndicator.equals("null") || !reverseRelationIndicator.equals("REVERSE RELATION"))
                    && !effectiveToDate.equals(effectiveFromDate)
                    && relationTypeCode.equals("EMPLOYEE")) {
                hasEmployeeRelation = true;
                if (primaryAffiliationB == "Y") {
                    Map subMap = new HashMap();
                    subMap.put("from", DateUtils.daysDiff(BASE_FROM_DATE, effectiveFromDate));
                    subMap.put("to", DateUtils.daysDiff(BASE_FROM_DATE, effectiveToDate));
                    list.add(subMap);
                    primaryEmployeeCount++;
                    hasPrimaryEmployee = true;
                }
            }

            for (int i = 0; i < rs.getSize(); i++) {
                Record recordOld = rs.getRecord(i);
                Relationship oldPart = retrieveFields(recordOld, record);

                if (oldPart.entityRelationPK_compare.equals(entityRelationId)) {
                    continue;
                }
                if ((oldPart.reverseRelationIndicator_compare == null || oldPart.reverseRelationIndicator_compare.equals("") || oldPart.reverseRelationIndicator_compare.equals("null") || !oldPart.reverseRelationIndicator_compare.equals("REVERSE RELATION"))
                        && oldPart.effectiveToDate_compare != oldPart.effectiveFromDate_compare
                        && oldPart.relationTypeCode_compare.equals("EMPLOYEE")) {
                    hasEmployeeRelation = true;
                    if (oldPart.primaryAffiliationB_compare.equals("-1")) {
                        Map subMap = new HashMap();
                        subMap.put("from", new Integer(oldPart.effectiveFromDate_compare));
                        subMap.put("to", new Integer(oldPart.effectiveToDate_compare));
                        list.add(subMap);
                        primaryEmployeeCount++;
                        hasPrimaryEmployee = true;
                    }
                }

            }
            if (hasEmployeeRelation) {
                if (primaryEmployeeCount == 0) {
                    MessageManager.getInstance().addErrorMessage("ci.entity.message.relation.active");
                }
                if (primaryEmployeeCount > 1) {
                    if (isOverlapTime(list)) {
                        MessageManager.getInstance().addErrorMessage("ci.entity.message.relation.oneActive");
                    }
                }
            }

        } catch (Exception e) {

        }
        if (MessageManager.getInstance().hasErrorMessages()) {
            ValidationException ve = new ValidationException("Validate the Primary Employer error.");
            l.throwing(getClass().getName(), "validatePrimaryEmployer", ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validatePrimaryEmployer");
    }

    /**
     * Validate all   Modified Record Primary Employer
     *
     * @param record
     * @param rs
     */
    protected void validateReverseRelationship(Record record, RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateReverseRelationship", new Object[]{record});
        }

        try {
            String entityPK = record.getStringValue("pk");
            String entityRelationId = record.getStringValue("entityRelationId");
            String relationTypeCode = record.getStringValue("relationTypeCode");
            String entityParentId = record.getStringValue("entityParentId");
            String entityChildId = record.getStringValue("entityChildFK");
            String entityDiffFK = getDiffFK(entityPK, entityParentId, entityChildId);
            String relationTypeCode_check = null;
            int effectiveFromDate;
            int effectiveToDate;
            effectiveFromDate = DateUtils.daysDiff(BASE_FROM_DATE, record.getStringValue("effectiveFromDate", BASE_FROM_DATE));
            effectiveToDate = DateUtils.daysDiff(BASE_FROM_DATE, record.getStringValue("effectiveToDate", BASE_TO_DATE));

            if (effectiveFromDate == effectiveToDate) {
                return;
            }

            if (relationTypeCode.equals("EMPLOYEE")) {
                relationTypeCode_check = "EMPLOYER";
            } else if (relationTypeCode.equals("EMPLOYER")) {
                relationTypeCode_check = "EMPLOYEE";
            }

            if (relationTypeCode_check == null) {
                return;
            }

            for (int i = 0; i < rs.getSize(); i++) {
                Record recordOld = rs.getRecord(i);
                Relationship oldPart = retrieveFields(recordOld, record);

                if (oldPart.entityRelationPK_compare.equals(entityRelationId)) {
                    continue;
                }
                if (oldPart.relationTypeCode_compare.equals(relationTypeCode_check) &&
                        oldPart.entityParentFK_compare.equals(entityChildId) &&
                        oldPart.entityChildFK_compare.equals(entityParentId) &&
                        oldPart.effectiveToDate_compare > oldPart.effectiveFromDate_compare) {
                    if ((effectiveFromDate >= oldPart.effectiveFromDate_compare && effectiveFromDate <= oldPart.effectiveToDate_compare) ||
                            (effectiveToDate >= oldPart.effectiveFromDate_compare && effectiveToDate <= oldPart.effectiveToDate_compare) ||
                            (oldPart.effectiveFromDate_compare >= effectiveFromDate && oldPart.effectiveFromDate_compare <= effectiveToDate) ||
                            (oldPart.effectiveToDate_compare >= effectiveFromDate && oldPart.effectiveToDate_compare <= effectiveToDate)) {
                        MessageManager.getInstance().addErrorMessage("ci.entity.message.relation.defined");
                        break;
                    }
                }
            }
        } catch (Exception e) {

        }
        if (MessageManager.getInstance().hasErrorMessages()) {
            ValidationException ve = new ValidationException("Validate  Modified Record Primary Employer error.");
            l.throwing(getClass().getName(), "validateReverseRelationship", ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validateReverseRelationship");
    }

    protected String getDiffFK(String clientPK, String entityParentFK, String entityChildFK) {
        if (clientPK.equals(entityParentFK))
            return entityChildFK;
        else
            return entityParentFK;
    }

    private Relationship retrieveFields(Record dataRecord, Record requestRecord) {
        Relationship part = new Relationship();
        try {

            part.entityPK_compare = requestRecord.getStringValue("pk");
            part.entityRelationPK_compare = dataRecord.getStringValue("entityRelationPK");
            part.entityParentFK_compare = dataRecord.getStringValue("entityParentFK");
            part.entityChildFK_compare = dataRecord.getStringValue("entityChildFK");
            part.entityDiffFK_compare = getDiffFK(part.entityPK_compare, part.entityParentFK_compare, part.entityChildFK_compare);

            part.relationTypeCode_compare = dataRecord.getStringValue("relationTypeCode");
            part.reverseRelationIndicator_compare = dataRecord.getStringValue("reverseRelationIndicator");
            part.primaryAffiliationB_compare = dataRecord.getStringValue("primaryAffiliationB");

            part.effectiveFromDate_compare = DateUtils.daysDiff(BASE_FROM_DATE, dataRecord.getStringValue("effectiveFromDate", BASE_FROM_DATE));
            part.effectiveToDate_compare = DateUtils.daysDiff(BASE_FROM_DATE, dataRecord.getStringValue("effectiveToDate", BASE_TO_DATE));

            if (dataRecord.getStringValue("accountingToDate") == null || StringUtils.replace(dataRecord.getStringValue("accountingToDate"), "null", "").equals(""))
                part.accountingToDate_compare = DateUtils.daysDiff(BASE_FROM_DATE, BASE_TO_DATE);
            else {
                part.accountingToDate_compare = DateUtils.daysDiff(BASE_FROM_DATE, dataRecord.getStringValue("accountingToDate"));
            }
        } catch (ParseException e) {

        }
        return part;
    }

    private Boolean isOverlapTime(List list) {
        if (list == null || list.size() == 1) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            Map map1 = (HashMap) list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                Map map2 = (HashMap) list.get(j);
                if ((((Integer) map1.get("from")).intValue() >= ((Integer) map2.get("from")).intValue() && ((Integer) map1.get("from")).intValue() < ((Integer) map2.get("to")).intValue()) ||
                        (((Integer) map1.get("to")).intValue() > ((Integer) map2.get("from")).intValue() && ((Integer) map1.get("to")).intValue() <= ((Integer) map2.get("to")).intValue()) ||
                        (((Integer) map2.get("from")).intValue() >= ((Integer) map1.get("from")).intValue() && ((Integer) map2.get("from")).intValue() < ((Integer) map1.get("to")).intValue()) ||
                        (((Integer) map2.get("to")).intValue() > ((Integer) map1.get("from")).intValue() && ((Integer) map2.get("to")).intValue() <= ((Integer) map1.get("to")).intValue())
                        ) {
                    return true;
                }
            }
        }
        return false;
    }

    public void verifyConfig() {
        if (getRelationshipDAO() == null) {
            throw new ConfigurationException("The required property 'relationshipDAO' is missing.");
        }
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        this.m_workbenchConfiguration = workbenchConfiguration;
    }

    public RelationshipDAO getRelationshipDAO() {
        return m_relationshipDAO;
    }

    public void setRelationshipDAO(RelationshipDAO relationshipDAO) {
        m_relationshipDAO = relationshipDAO;
    }

    private static class Relationship {
        public String entityPK_compare;
        public String entityRelationPK_compare;
        public String entityParentFK_compare;
        public String entityChildFK_compare;
        public String entityDiffFK_compare;
        public String relationTypeCode_compare;
        public String primaryAffiliationB_compare;
        public int effectiveFromDate_compare;
        public int effectiveToDate_compare;
        public int accountingToDate_compare;
        public String reverseRelationIndicator_compare;
    }

    private WorkbenchConfiguration m_workbenchConfiguration;
    private RelationshipDAO m_relationshipDAO;
    private static final String BASE_FROM_DATE = "01/01/1900";
    private static final String BASE_TO_DATE = "01/01/3000";
}