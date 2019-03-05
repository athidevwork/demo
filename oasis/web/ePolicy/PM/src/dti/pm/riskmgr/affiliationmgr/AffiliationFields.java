package dti.pm.riskmgr.affiliationmgr;

import dti.oasis.recordset.Record;

import java.util.Date;

/**
 * Helper constants and set/get methods to access Affiliation Fields.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 7, 2008
 *
 * @author Simon.Li
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/25/2014       kxiang      Issue 158853 - Added fields ORGANIZATION_NAME_GH and ORGANIZATION_NAME_HREF to get href
 *                                             value in WebWB.
 * 10/31/2017       lzhang      Issue 188425 - Add MAX_AFF_EXP_DATE,TERM_EFFECTIVE_TO_DATE,
 *                                             POLICY_TERM_BASE_RECORD_IDS,FAILED_TERMS_FOR_PRACT_PER
 * ---------------------------------------------------
 */
public class AffiliationFields {
    public static final String ENTITY_RELATION_ID = "entityRelationId";
    public static final String ENTITY_PARENT_ID = "entityParentId";
    public static final String ENTITY_CHILD_ID = "entityChildId";
    public static final String ORGANIZATION_NAME = "organizationName";
    public static final String EFF_DATE = "effDate";
    public static final String EXP_DATE = "expDate";
    public static final String ADDL_RELATION_TYPE_CODE = "addlRelationTypeCode";
    public static final String PAY_EXCESS_B = "payExcessB";
    public static final String PERCENT_PRACTICE = "percentPractice";
    public static final String PRIMARY_AFFILIATION_B = "primaryAffiliationB";
    public static final String VAP_B = "vapB";
    public static final String RELATION_TYPE_CODE = "relationTypeCode";
    public static final String ORIG_EFF_DATE = "origEffDate";
    public static final String ORIG_EXP_DATE = "origExpDate";
    public static final String ORGANIZATION_NAME_GH = "organizationName_GH";
    public static final String ORGANIZATION_NAME_HREF = "organizationNameHref";
    public static final String MAX_AFF_EXP_DATE = "maxAffExpDate";
    public static final String TERM_EFFECTIVE_TO_DATE = "termEffectiveToDate";
    public static final String POLICY_TERM_BASE_RECORD_IDS = "policyTermBaseRecordIds";
    public static final String FAILED_TERMS_FOR_PRACT_PERC = "failedTermsForPractPerc";

    public static void setEntityRelationId(Record record, String entityRelationId) {
        record.setFieldValue(ENTITY_RELATION_ID, entityRelationId);
    }

    public static String getEntityRelationId(Record record) {
        return record.getStringValue(ENTITY_RELATION_ID);
    }

    public static void setEntityParentId(Record record, String entityParentId) {
        record.setFieldValue(ENTITY_PARENT_ID, entityParentId);
    }

    public static String getEntityParentId(Record record) {
        return record.getStringValue(ENTITY_PARENT_ID);
    }

    public static void setEntityChildId(Record record, String entityChildId) {
        record.setFieldValue(ENTITY_CHILD_ID, entityChildId);
    }

    public static String getEntityChildId(Record record) {
        return record.getStringValue(ENTITY_CHILD_ID);
    }

    public static void setOrganizationName(Record record, String organizationName) {
        record.setFieldValue(ORGANIZATION_NAME, organizationName);
    }

    public static String getOrganizationName(Record record) {
        return record.getStringValue(ORGANIZATION_NAME);
    }

    public static void setEffDate(Record record, String effDate) {
        record.setFieldValue(EFF_DATE, effDate);
    }

    public static String getEffDate(Record record) {
        return record.getStringValue(EFF_DATE);
    }

    public static void setExpDate(Record record, String expDate) {
        record.setFieldValue(EXP_DATE, expDate);
    }

    public static String getExpDate(Record record) {
        return record.getStringValue(EXP_DATE);
    }

    public static void setAddlRelationTypeCode(Record record, String addlRelationTypeCode) {
        record.setFieldValue(ADDL_RELATION_TYPE_CODE, addlRelationTypeCode);
    }

    public static String getAddlRelationTypeCode(Record record) {
        return record.getStringValue(ADDL_RELATION_TYPE_CODE);
    }

    public static void setPayExcessB(Record record, String payExcessB) {
        record.setFieldValue(PAY_EXCESS_B, payExcessB);
    }

    public static String getPayExcessB(Record record) {
        return record.getStringValue(PAY_EXCESS_B);
    }

    public static void setPercentPractice(Record record, String percentPractice) {
        record.setFieldValue(PERCENT_PRACTICE, percentPractice);
    }

    public static String getPercentPractice(Record record) {
        return record.getStringValue(PERCENT_PRACTICE);
    }

    public static void setPrimaryAffiliationB(Record record, String primaryAffiliationB) {
        record.setFieldValue(PRIMARY_AFFILIATION_B, primaryAffiliationB);
    }

    public static String getPrimaryAffiliationB(Record record) {
        return record.getStringValue(PRIMARY_AFFILIATION_B);
    }

    public static void setVapB(Record record, String vapB) {
        record.setFieldValue(VAP_B, vapB);
    }

    public static String getVapB(Record record) {
        return record.getStringValue(VAP_B);
    }

    public static void setRelationTypeCode(Record record, String relationTypeCode) {
        record.setFieldValue(RELATION_TYPE_CODE, relationTypeCode);
    }

    public static String getRelationTypeCode(Record record) {
        return record.getStringValue(RELATION_TYPE_CODE);
    }

    public static void setOrigEffDate(Record record, String origEffDate) {
        record.setFieldValue(ORIG_EFF_DATE, origEffDate);
    }

    public static String getOrigEffDate(Record record) {
        return record.getStringValue(ORIG_EFF_DATE);
    }

    public static void setOrigExpDate(Record record, String origExpDate) {
        record.setFieldValue(ORIG_EXP_DATE, origExpDate);
    }

    public static String getOrigExpDate(Record record) {
        return record.getStringValue(ORIG_EXP_DATE);
    }

    public static void setOrganizationNameHref(Record record, String organizationNameHref) {
        record.setFieldValue(ORGANIZATION_NAME_HREF, organizationNameHref);
    }

    public static String getOrganizationNameHref(Record record) {
        return record.getStringValue(ORGANIZATION_NAME_HREF);
    }

    public static void setMaxAffExpDate(Record record, String maxAffExpDate) {
        record.setFieldValue(MAX_AFF_EXP_DATE, maxAffExpDate);
    }

    public static String getMaxAffExpDate(Record record) {
        return record.getStringValue(MAX_AFF_EXP_DATE);
    }

    public static void setTermEffectiveToDate(Record record, String termEffectiveToDate) {
        record.setFieldValue(TERM_EFFECTIVE_TO_DATE, termEffectiveToDate);
    }

    public static void setPolicyTermBaseRecordIds(Record record, String policyTermBaseRecordIds) {
        record.setFieldValue(POLICY_TERM_BASE_RECORD_IDS, policyTermBaseRecordIds);
    }

    public static String getPolicyTermBaseRecordIds(Record record) {
        return record.getStringValue(POLICY_TERM_BASE_RECORD_IDS);
    }

    public static void setFailedTermsForPractPerc(Record record, String failedTermsForPractPerc) {
        record.setFieldValue(FAILED_TERMS_FOR_PRACT_PERC, failedTermsForPractPerc);
    }

    public static String getFailedTermsForPractPerc(Record record) {
        return record.getStringValue(FAILED_TERMS_FOR_PRACT_PERC);
    }
}
