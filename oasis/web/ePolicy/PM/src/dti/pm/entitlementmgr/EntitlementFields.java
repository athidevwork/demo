package dti.pm.entitlementmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * This class provides constants and set/get helper methods for access entitlement fields in a Record.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 25, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntitlementFields {

    public static final String READ_ONLY = "readOnly";
    public static final String IS_ROW_ELIGIBLE_FOR_DELETE = "isRowEligibleForDelete";
    public static final String HANDLE_LAYER = "handleLayer";

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

    public static boolean isHandleLayer(Record record) {
        return record.getBooleanValue(HANDLE_LAYER, false).booleanValue();
    }

    public static void setHandleLayer(Record record, boolean isHandleLayer) {
        record.setFieldValue(HANDLE_LAYER, YesNoFlag.getInstance(isHandleLayer));
    }

    public static void setHandleLayer(Record record, YesNoFlag isHandleLayer) {
        record.setFieldValue(HANDLE_LAYER, isHandleLayer);
    }
}
