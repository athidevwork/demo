package dti.oasis.recordset;

import dti.oasis.busobjs.YesNoFlag;

/**
 * Created by IntelliJ IDEA.
 * User: gjlong
 * Date: Jul 7, 2008
 * Time: 5:51:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntitlementFields {

    public static final String READ_ONLY = "readOnly";
    public static final String IS_ROW_ELIGIBLE_FOR_DELETE = "isRowEligibleForDelete";

    public static boolean isReadOnly(Record record) {
        return record.getBooleanValue(READ_ONLY, false).booleanValue();
    }

    public static void setReadOnly(Record record, boolean isReadOnly) {
        record.setFieldValue(READ_ONLY, YesNoFlag.getInstance(isReadOnly));
    }

    public static void setReadOnly(Record record, YesNoFlag isReadOnly) {
        record.setFieldValue(READ_ONLY, isReadOnly);
    }

    public static boolean isRowEligibleForDelete(Record record) {
        return record.getBooleanValue(IS_ROW_ELIGIBLE_FOR_DELETE, false).booleanValue();
    }

    public static void setIsRowEligibleForDelete(Record record, boolean isRowEligibleForDelete) {
        record.setFieldValue(IS_ROW_ELIGIBLE_FOR_DELETE, YesNoFlag.getInstance(isRowEligibleForDelete));
    }

    public static void setIsRowEligibleForDelete(Record record, YesNoFlag isRowEligibleForDelete) {
        record.setFieldValue(IS_ROW_ELIGIBLE_FOR_DELETE, isRowEligibleForDelete);
    }
}