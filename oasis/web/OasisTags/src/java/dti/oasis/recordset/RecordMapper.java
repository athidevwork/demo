package dti.oasis.recordset;

import dti.oasis.util.LogUtils;
import dti.oasis.util.Mapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Map between Record data and Map.
 * If the target is null, system creates the target object.
 * 
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 26, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/03/2009       yhyang      Change the value to Object in the target.
 * 11/12/2018       wreeder     196160 - Optimize iteration through Fields in a Record with getFields() / field.getStringValue() instead of getFieldNames() / record.hasFieldValue(fieldId) / record.getStringValue(fieldId)
 * ---------------------------------------------------
 */
public class RecordMapper implements Mapper {

    public static RecordMapper getInstance() {
        return new RecordMapper();
    }

    /**
     * Convert Record to Map.
     * 
     * @param sourceRecord
     * @param targetMap
     */
    public void map(Record sourceRecord, Map targetMap) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "map", new Object[]{sourceRecord, targetMap});
        }
        if (sourceRecord != null) {
            if (targetMap == null) {
                targetMap = new HashMap();
            }
            Iterator iterator = sourceRecord.getFields();
            while (iterator.hasNext()) {
                Field field = (Field) iterator.next();
                targetMap.put(field.getName(), field.getValue());
            }
        }

        l.logp(Level.FINE, getClass().getName(), "map", "targetMap = " + targetMap);

        l.exiting(getClass().getName(), "map");
    }

    /**
     * Convert Map to Record
     *
     * @param sourceMap
     * @param targetRecord
     */
    public void map(Map sourceMap, Record targetRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "map", new Object[]{sourceMap,targetRecord});
        }
        if (sourceMap != null) {
            if (targetRecord == null) {
                targetRecord = new Record();
            }
            Iterator entries = sourceMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                targetRecord.setFieldValue((String) entry.getKey(), entry.getValue());
            }
        }

        l.logp(Level.FINE, getClass().getName(), "map", "targetRecord = " + targetRecord);

        l.exiting(getClass().getName(), "map");
    }
    private final Logger l = LogUtils.getLogger(getClass());
}
