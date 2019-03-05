package dti.oasis.obr;

import dti.oasis.recordset.Record;
import org.drools.lang.dsl.DSLMappingEntry;

/**
 * Interface for rule mapping.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 20, 2011
 *
 * @author James
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public class RuleMappingFields {

    public static final String MAPPING_SCOPE = "mappingScope";
    public static final String LANGUAGE_EXPRESSION = "languageExpression";
    public static final String RULE_MAPPING = "ruleMapping";

    public static final String MAPPING_SCOPE_WHEN = "when";
    public static final String MAPPING_SCOPE_THEN = "then";

    public static final String DSL_MAPPING_ENTRY = "DSLMappingEntry";

    public static String getMappingScope(Record record) {
        return record.getStringValue(MAPPING_SCOPE);
    }

    public static boolean isMappingScopeForWhen(Record record) {
        return MAPPING_SCOPE_WHEN.equals(record.getStringValue(MAPPING_SCOPE));
    }

    public static boolean isMappingScopeForThen(Record record) {
        return MAPPING_SCOPE_THEN.equals(record.getStringValue(MAPPING_SCOPE));
    }

    public static void setMappingScope(Record record, String mappingScope) {
        record.setFieldValue(MAPPING_SCOPE, mappingScope);
    }

    public static String getLanguageExpression(Record record) {
        return record.getStringValue(LANGUAGE_EXPRESSION);
    }

    public static void setLanguageExpression(Record record, String languageExpression) {
        record.setFieldValue(LANGUAGE_EXPRESSION, languageExpression);
    }

    public static String getRuleMapping(Record record) {
        return record.getStringValue(RULE_MAPPING);
    }

    public static void setRuleMapping(Record record, String ruleMapping) {
        record.setFieldValue(RULE_MAPPING, ruleMapping);
    }

    public static void setDSLMappingEntry(Record record, DSLMappingEntry dslMappingEntry){
          record.setFieldValue(DSL_MAPPING_ENTRY, dslMappingEntry);
    }

    public static DSLMappingEntry getDSLMappingEntry(Record record) {
        return (DSLMappingEntry) record.getFieldValue(DSL_MAPPING_ENTRY);
    }

}