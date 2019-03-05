package dti.ci.entityclassmgr;

import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/26/2018
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityClassFields {
    private final Logger l = LogUtils.getLogger(getClass());

    public static final String ENTITY_CLASS_ID = "entityClassId";
    public static final String NEW_ENTITY_CLASS_ID = "newEntityClassId";
    public static final String ENTITY_CLASS_CODE = "entityClassCode";
    public static final String ENTITY_SUB_CLASS_CODE = "entitySubClassCode";
    public static final String ENTITY_SUB_TYPE_CODE = "entitySubTypeCode";
    public static final String NETWORK_DISCOUNT = "networkDiscount";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String EDIT = "edit";
    public static final String EDIT_LABEL = "Edit";
    public static final String FORM_ACTION_PROPERTY = "formAction";
    public static final String ENT_CLS_SUB_CLASS_CODE_DESC = "entitySubClassCodeDesc";
    public static final String ENT_CLS_SUB_TYPE_CODE_DESC = "entitySubTypeCodeDesc";
    public static final String ALLOW_SUBCLASS_SELECT_PROPERTY = "allowSubClassSelection";
    public static final String ALLOW_SUBTYPE_SELECT_PROPERTY = "allowSubTypeSelection";
    public static final String ADD_WITH_ERROR = "addWithError";
    public static final String ENTITY_CLASS_PREFIX = "entityClass_";
    public static final String FILTER_CRITERIA_PREFIX = "filterCriteria_";
    public static final String GRID_HEADER_SUFFIX = "_GH";
    public static final String NETWORK_DISCOUNT_COLUMN_NAME = NETWORK_DISCOUNT + GRID_HEADER_SUFFIX;
    public static final String CLOSE_PROCESS_DESC = "close";
    public static final String CI_ENABLE_NETWORK_DISCOUNT = "CI_ENABLE_NTWK_DSCT";
    public static final String NETWORK_ENTITY_CLASS_CODE = "NETWORK";
    public static final String ENT_CLS_CODE_ID                = ENTITY_CLASS_PREFIX + ENTITY_CLASS_CODE;
    public static final String ENT_CLS_SUB_CLASS_CODE_ID      = ENTITY_CLASS_PREFIX + ENTITY_SUB_CLASS_CODE;
    public static final String ENT_CLS_SUB_TYPE_CODE_ID       = ENTITY_CLASS_PREFIX + ENTITY_SUB_TYPE_CODE;

    public static String getEntityClassCode(Record record) {
        return record.getStringValueDefaultEmpty(ENTITY_CLASS_CODE);
    }

    public static void setEntityClassCode(Record record, String entityClassCode) {
        record.setFieldValue(ENTITY_CLASS_CODE, entityClassCode);
    }
}
