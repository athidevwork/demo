package dti.pm.policymgr;

import dti.oasis.recordset.Record;
import dti.pm.busobjs.PolicyStatus;

/**
 * Helper constants and set/get methods to access Policy Header Fields.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 5, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/07/2014       kxiang      158411 - Added get/set method for policyTermHistoryId.
 * 11/07/2014       tzeng       164679 - Added get/set method for prodRiskRelationId.
 * 06/28/2016       tzeng       167531 - Added get/set method for lastOffTermEffToDate.
 * 09/18/2016       lzhang      179121 - Added get method for policyNoCriteria.
 * ---------------------------------------------------
 */
public class PolicyHeaderFields {

    public static final String POLICY_NO = "policyNo";
    public static final String POLICY_ID = "policyId";
    public static final String TERM_BASE_RECORD_ID = "termBaseRecordId";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String TERM_EFFECTIVE_FROM_DATE = "termEffectiveFromDate";
    public static final String TERM_EFFECTIVE_TO_DATE = "termEffectiveToDate";
    public static final String POLICY_TYPE_CODE = "policyTypeCode";
    public static final String LEGACY_POLICY_NO = "legacyPolicyNo";
    public static final String POLICY_STATUS = "policyStatus";
    public static final String POLICY_TERM_HISTORY_ID = "policyTermHistoryId";
    public static final String PROD_RISK_RELATION_ID = "prodRiskRelationId";
    public static final String LAST_OFF_TERM_EFF_TO_DATE = "lastOffTermEffToDate";
    public static final String POLICY_NO_CRITERIA = "policyNoCriteria";


    public static String getLegacyPolicyNo(Record record) {
      return record.getStringValue(LEGACY_POLICY_NO);
    }

    public static void setLegacyPolicyNo(Record record, String legacyPolicyNo) {
      record.setFieldValue(LEGACY_POLICY_NO, legacyPolicyNo);
    }

    public static String getPolicyNo(Record record) {
        return record.getStringValue(POLICY_NO);
    }

     public static void setPolicyNo(Record record, String policyNo) {
      record.setFieldValue(POLICY_NO, policyNo);   }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID, policyId);
    }

    public static String getTermEffectiveFromDate(Record record) {
        return record.getStringValue(TERM_EFFECTIVE_FROM_DATE);
    }

    public static void setTermEffectiveFromDate(Record record, String termEffectiveFromDate) {
        record.setFieldValue(TERM_EFFECTIVE_FROM_DATE, termEffectiveFromDate);
    }

    public static String getTermEffectiveToDate(Record record) {
        return record.getStringValue(TERM_EFFECTIVE_TO_DATE);
    }

    public static String getPolicyTypeCode(Record record) {
        return record.getStringValue(POLICY_TYPE_CODE);
    }

    public static void setPolicyTypeCode(Record record, String policyTypeCode) {
        record.setFieldValue(POLICY_TYPE_CODE, policyTypeCode);
    }

    public static void setTermEffectiveToDate(Record record, String termEffectiveToDate) {
        record.setFieldValue(TERM_EFFECTIVE_TO_DATE, termEffectiveToDate);
    }

    public static String getTermBaseRecordId(Record record) {
        return record.getStringValue(TERM_BASE_RECORD_ID);
    }

    public static void setTermBaseRecordId(Record record, String termBaseRecordId) {
        record.setFieldValue(TERM_BASE_RECORD_ID, termBaseRecordId);
    }

    public static PolicyStatus getPolicyStatus(Record record) {
        Object value = record.getFieldValue(POLICY_STATUS);
        PolicyStatus result = null;
        if (value == null || value instanceof PolicyStatus) {
            result = (PolicyStatus) value;
        }
        else {
            result = PolicyStatus.getInstance(value.toString());
        }
        return result;
    }

    public static void setPolicyStatus(Record record, PolicyStatus policyStatus) {
        record.setFieldValue(POLICY_STATUS, policyStatus);
    }

    public static String getPolicyTermHistoryId(Record record) {
        return record.getStringValue(POLICY_TERM_HISTORY_ID);
    }

    public static void setPolicyTermHistoryId(Record record, String policyTermHistoryId) {
        record.setFieldValue(POLICY_TERM_HISTORY_ID, policyTermHistoryId);
    }

    public static String getProdRiskRelationId(Record record) {
        return record.getStringValue(PROD_RISK_RELATION_ID);
    }

    public static void setProdRiskRelationId(Record record, String prodRiskRelationId) {
        record.setFieldValue(PROD_RISK_RELATION_ID, prodRiskRelationId);
    }

    public static String getLastOffTermEffToDate(Record record) {
        return record.getStringValue(LAST_OFF_TERM_EFF_TO_DATE);
    }

    public static void setLastOffTermEffToDate(Record record, String lastOffTermEffToDate) {
        record.setFieldValue(LAST_OFF_TERM_EFF_TO_DATE, lastOffTermEffToDate);
    }

    public static String getPolicyNoCriteria(Record record) {
        return record.getStringValue(POLICY_NO_CRITERIA);
    }

}
