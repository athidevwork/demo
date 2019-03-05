package dti.oasis.obr;

import dti.oasis.recordset.Record;

/**
 * Interface for page rule.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 25, 2011
 *
 * @author James
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public class RuleFields {

    public static final String RULE_ID = "ruleId";
    public static final String RULE_DESC = "ruleDesc";
    public static final String RULE_TYPE = "ruleType";
    public static final String RULE_TEXT = "ruleText";
    public static final String RULE_FROM = "ruleFrom";
    public static final String VALIDATION_MESSAGE = "validationMessage";
    public static final String RULE_TYPE_BUSINESS = "BUSINESS";
    public static final String RULE_TYPE_TECHNICAL = "TECHNICAL";
    public static final String RULE_FROM_BASE = "BASE";
    public static final String RULE_FROM_CUST = "CUST";

    public static final String RULE_CODE = "RULE_CODE";

    public static String getRuleId(Record record) {
        return record.getStringValue(RULE_ID);
    }

    public static void setRuleId(Record record, String ruleId) {
        record.setFieldValue(RULE_ID, ruleId);
    }

    public static String getRuleDesc(Record record) {
        return record.getStringValue(RULE_DESC);
    }

    public static void setRuleDesc(Record record, String ruleDesc) {
        record.setFieldValue(RULE_DESC, ruleDesc);
    }

    public static String getRuleType(Record record) {
        return record.getStringValue(RULE_TYPE);
    }

    public static void setRuleType(Record record, String ruleType) {
        record.setFieldValue(RULE_TYPE, ruleType);
    }

    public static String getRuletText(Record record) {
        return record.getStringValue(RULE_TEXT);
    }

    public static void setRuleText(Record record, String ruleText) {
        record.setFieldValue(RULE_TEXT, ruleText);
    }

    public static boolean isBusinessRule(Record record) {
        return RULE_TYPE_BUSINESS.equals(getRuleType(record));
    }

    public static boolean isTechnicalRule(Record record) {
        return RULE_TYPE_TECHNICAL.equals(getRuleType(record));
    }

    public static String getRuleCode(Record record) {
        return record.getStringValue(RULE_CODE);
    }

    public static void setRuleCode(Record record, String ruleCode) {
        record.setFieldValue(RULE_CODE, ruleCode);
    }

    public static boolean isRuleFromBase(Record record) {
        return RULE_FROM_BASE.equals(record.getStringValue(RULE_FROM));
    }

    public static boolean isRuleFromCust(Record record) {
        return RULE_FROM_CUST.equals(record.getStringValue(RULE_FROM));
    }

    public static void setRuleFrom(Record record, String ruleFrom) {
        record.setFieldValue(RULE_FROM, ruleFrom);
    }

    public static String getValidationMessage(Record record) {
        return record.getStringValue(VALIDATION_MESSAGE);
    }

}
