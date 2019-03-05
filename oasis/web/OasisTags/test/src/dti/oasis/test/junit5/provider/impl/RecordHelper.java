package dti.oasis.test.junit5.provider.impl;

import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/8/2018
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
public class RecordHelper {
    private static final Logger l = LogUtils.getLogger(RecordHelper.class);

    public static Record mapToRecord(Map dataMap) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(RecordHelper.class.getName(), "mapToRecord", new Object[]{dataMap});
        }

        Record record = new Record();

        setRecordValues(record, dataMap);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(RecordHelper.class.getName(), "mapToRecord", record);
        }
        return record;
    }

    public static void setRecordValues(Record record, Map dataMap) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(RecordHelper.class.getName(), "mapToRecord", new Object[]{dataMap});
        }

        Set<String> fieldNames = dataMap.keySet();
        for (String fieldName : fieldNames) {
            switch (fieldName) {
                case "UPDATE_IND":
                    record.setUpdateIndicator((String) dataMap.get(fieldName));
                    break;

                case "recordNumber":
                    record.setRecordNumber((Integer) dataMap.get(fieldName));
                    break;

                case "rowId":
                    record.setRowId((String) dataMap.get(fieldName));
                    break;
            }

            record.setFieldValue(fieldName, dataMap.get(fieldName));
        }

        l.exiting(RecordHelper.class.getName(), "setRecordValues");
    }
}
