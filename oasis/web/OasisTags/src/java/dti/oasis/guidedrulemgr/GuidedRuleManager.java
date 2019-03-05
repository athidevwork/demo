package dti.oasis.guidedrulemgr;

import dti.oasis.recordset.Record;

/**
 * Manager class for edit guided rule page
 *
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2011
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface GuidedRuleManager {

    public static final String TAG_NAME_RULE_MAPPING = "RuleMapping";
    public static final String TAG_NAME_MAPPING_ITEM = "MappingItem";
    public static final String TAG_NAME_SCOPE = "Scope";
    public static final String TAG_NAME_LANGUAGE_EXPRESSION = "LanguageExpression";
    public static final String TAG_NAME_MAPPING = "Mapping";
    public static final String TAG_NAME_IS_FOR_SAVE_EVENT = "IsForSaveEvent";
    public static final String TAG_NAME_RULE = "Rule";
    public static final String TAG_NAME_OPTION = "Option";
    public static final String TAG_NAME_WHEN = "When";
    public static final String TAG_NAME_THEN = "Then";

    public static final String TAG_NAME_LINE = "Line";
    public static final String TAG_NAME_TEXT = "Text";
    public static final String TAG_NAME_INPUT = "Input";

    public static final String LINE_ID_PREFIX = "LINE_";
    public static final String INPUT_NAME_STRING = "_INPUT_";

    public static final String PLACE_HOLDER_FOR_BLANK_VALUE = "BLANK_VALUE";

    public static final String TAG_NAME_FIELD = "Field";
    public static final String TAG_NAME_NAVIGATION = "Navigation";

    public static final String  INPUT_TYPE_NORMAL = "NORMAL";

    public static final String PAGE_TITLE = "PAGE_TITLE";
    public static final String RULE_INFORMATION = "RULE_INFORMATION";
    public static final String GUIDED_RULE_XML = "GuidedRuleXML";
    public static final String PAGE_FIELDS_XML = "PageFieldsXML";


    /**
     * convert rule text to xml for edit
     *
     * @param record
     * @return
     */
    public abstract String generateRuleXML(Record record);

    /**
     * get validation message
     *
     * @param inputRecord
     * @return
     */
    public String getValidateMessage(Record inputRecord);

    /**
     * get rule source
     *
     * @param inputRecord
     * @return
     */
    public abstract String getRuleSourceCode(Record inputRecord);

    /**
     * get xml for page fields
     * @param inputRecord
     * @return
     */
    public abstract String getPageFieldsXML(Record inputRecord);

}