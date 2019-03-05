package dti.pm.policymgr.reinsurancemgr;

import dti.oasis.recordset.Record;

/**
 * Constant field class for reinsurance
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 30, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/25/2014       kxiang      Issue 158853 - Added fields REINSURER_ENTITY_ID_GH and REINSURER_ENTITY_ID_HREF to get
 *                                             href value in WebWB.
 * ---------------------------------------------------
 */
public class ReinsuranceFields {
    public static final String POLICY_REINSURANCE_ID = "policyReinsuranceId";
    public static final String POLICY_ID = "policyId";
    public static final String CONTRACT_NO = "contractNo";
    public static final String REINSURANCE_TYPE_CODE = "reinsuranceTypeCode";
    public static final String POLICY_TYPE = "policyType";
    public static final String EFFCTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFCTIVE_TO_DATE = "effectiveToDate";
    public static final String REINSURER_ENTITY_ID = "reinsurerEntityId";
    public static final String REINSURER_ENTITY_ID_GH = "reinsurerEntityId_GH";
    public static final String REINSURER_ENTITY_ID_HREF = "reinsurerEntityIdHref";

    public static void setContractNo(Record record, String contractNo) {
        record.setFieldValue(CONTRACT_NO, contractNo);
    }

    public static String getContractNo(Record record) {
        return record.getStringValue(CONTRACT_NO);
    }

    public static void setPoilcyReinsuranceId(Record record, String poilcyReinsuranceId) {
        record.setFieldValue(POLICY_REINSURANCE_ID, poilcyReinsuranceId);
    }

    public static String getPoilcyReinsuranceId(Record record) {
        return record.getStringValue(POLICY_REINSURANCE_ID);
    }

     public static void setPoilcyId(Record record, String poilcyId) {
        record.setFieldValue(POLICY_ID, poilcyId);
    }

    public static String getPoilcyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFCTIVE_TO_DATE);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFCTIVE_TO_DATE, effectiveToDate);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFCTIVE_FROM_DATE);
    }

    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFCTIVE_FROM_DATE, effectiveFromDate);
    }

    public static String getPolicyType(Record record) {
        return record.getStringValue(POLICY_TYPE);
    }

    public static void setPolicyType(Record record, String policyType) {
        record.setFieldValue(POLICY_TYPE, policyType);
    }

    public static String getReinsurerEntityId(Record record) {
        return record.getStringValue(REINSURER_ENTITY_ID);
    }

    public static void setReinsurerEntityId(Record record, String reinsurerEntityId) {
        record.setFieldValue(REINSURER_ENTITY_ID, reinsurerEntityId);
    }
    
    public static void setReinsurerEntityIdHref(Record record, String reinsurerEntityIdHref) {
        record.setFieldValue(REINSURER_ENTITY_ID_HREF, reinsurerEntityIdHref);
    }

    public static String getReinsurerEntityIdHref(Record record) {
        return record.getStringValue(REINSURER_ENTITY_ID_HREF);
    }
}
