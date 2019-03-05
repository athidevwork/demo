package dti.pm.transactionmgr;

import dti.oasis.recordset.Record;

/**
 * Created by AWU
 * Date: 12/25/12
 */
public class CheckClearingReminderFields {
    public static final String CONTEXT = "context";
    public static final String VALUE = "value";

    public static String getContext(Record record) {
        return record.getStringValue(CONTEXT);
    }

    public static void setContext(Record record, String context) {
        record.setFieldValue(CONTEXT, context);
    }

    public static String getValue(Record record) {
        return record.getStringValue(VALUE);
    }

    public static void setValue(Record record, String value) {
        record.setFieldValue(VALUE, value);
    }

    public class PolicyRelReasonCodeValues {
        public static final String CNPP = "CNPP";
    }

    public class ContextCodeValues {
        public static final String CHECK_CLEARING_REMINDER = "CHECK_CLEAR_REMINDER";
    }
}
