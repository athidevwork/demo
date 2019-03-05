package dti.pm.pmdefaultmgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 19, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/01/2010       fcb         104191: MAP_COLUMN_LIST and MAP_VALUE_LIST added.
 * 02/06/2015       wdang       160336 - Add Constants.
 * ---------------------------------------------------
 */
public class DefaultLevelFields {

    // Input Fields
    public static final String LEVEL = "level";
    public static final String CODE_1 = "code1";
    public static final String VALUE_1 = "value1";
    public static final String CODE_2 = "code2";
    public static final String VALUE_2 = "value2";
    public static final String CODE_3 = "code3";
    public static final String VALUE_3 = "value3";
    public static final String WEB_FIELD_ID_B = "webFieldIdB";

    // Level constants
    public static final String RISK_RELATION_RELTYP_DFLT = "RISK_RELATION_RELTYP_DFLT";
    public static final String RISK_RELATION_RATE_DFLT = "RISK_RELATION_RATE_DFLT";

    // Code constants
    public static final String POLICY_TYPE_CODE = "POLICY_TYPE_CODE";
    public static final String INSURED_TYPE = "INSURED_TYPE";

    // OutputFields
    public static final String COLUMN_LIST = "columnList";
    public static final String VALUE_LIST = "valueList";
    public static final String MAP_COLUMN_LIST = "mapColumnList";
    public static final String MAP_VALUE_LIST = "mapValueList";

    public static void setLevel(Record record, String level) {
        record.setFieldValue(LEVEL, level);
    }

    public static void setCode1(Record record, String code1) {
        record.setFieldValue(CODE_1, code1);
    }

    public static void setValue1(Record record, String value1) {
        record.setFieldValue(VALUE_1, value1);
    }

    public static void setCode2(Record record, String code2) {
        record.setFieldValue(CODE_2, code2);
    }

    public static void setValue2(Record record, String value2) {
        record.setFieldValue(VALUE_2, value2);
    }

    public static void setCode3(Record record, String code3) {
        record.setFieldValue(CODE_3, code3);
    }

    public static void setValue3(Record record, String value3) {
        record.setFieldValue(VALUE_3, value3);
    }

    public static void setWebFieldIdB(Record record, YesNoFlag webFieldIdB) {
        record.setFieldValue(WEB_FIELD_ID_B, webFieldIdB);
    }
}
