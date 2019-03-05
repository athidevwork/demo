package dti.pm.busobjs;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 30, 2007
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
public class PMCommonFields {

    public static final String RECORD_MODE_CODE = "recordModeCode";

    public static boolean hasRecordModeCode(Record record) {
        return record.hasStringValue(RECORD_MODE_CODE);
    }

    public static RecordMode getRecordModeCode(Record record) {
        Object value = record.getFieldValue(RECORD_MODE_CODE);
        RecordMode result = null;
        if (value == null || value instanceof RecordMode) {
            result = (RecordMode) value;
        }
        else {
            result = RecordMode.getInstance(value.toString());
        }
        return result;
    }

    public static void setRecordModeCode(Record record, RecordMode recordModeCode) {
        record.setFieldValue(RECORD_MODE_CODE, recordModeCode);
    }

}
