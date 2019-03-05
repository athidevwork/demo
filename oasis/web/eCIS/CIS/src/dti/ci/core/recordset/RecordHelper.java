package dti.ci.core.recordset;

import dti.oasis.app.AppException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/9/2018
 *
 * @author yllu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/22/2018       dzhang      issue 196922: Skip add prefix to select_ind column.
 * ---------------------------------------------------
 */
public class RecordHelper {
    private final Logger l = LogUtils.getLogger(getClass());
    /**
     * Add filed name prefix for record.
     *
     * @param record
     * @param prefix
     */
    public static Record addRecordPrefix(Record record, String prefix) {
        Record result = cloneRecord(record);

        Iterator it = record.getFieldNames();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (RequestIds.SELECT_IND.equalsIgnoreCase(key)) {
                continue;
            }
            Field field = record.getField(key);
            result.remove(key);
            result.setField(prefix + key, field);
        }

        return result;
    }

    /**
     * Remove name prefix for record.
     *
     * @param record
     * @param prefix
     */
    public static Record removeRecordPrefix(Record record, String prefix) {
        Record result = cloneRecord(record);

        Iterator it = record.getFieldNames();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (StringUtils.indexOf(key, prefix) == 0) {
                Field field = record.getField(key);
                result.remove(key);
                String newKey = StringUtils.substringAfter(key, prefix);
                result.setField(newKey, field);
            }
        }

        return result;
    }

    /**
     * Filter fileds by prefix.
     *
     * @param record
     * @param prefix
     * @return
     */
    public static Record filterRecordWithPrefix(Record record, String prefix) {
        Record result = cloneRecord(record);

        Iterator it = record.getFieldNames();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (StringUtils.indexOf(key, prefix) != 0) {
                result.remove(key);
            }
        }
        return result;
    }

    public static Record cloneRecord(Record record) {
        Record result = null;

        try {
            result = (Record) BeanUtils.cloneBean(record);
        } catch (Exception e) {
            throw new AppException("Failed to clone record");
        }

        result.setFields(record);
        return result;
    }

    public static RecordSet addPrefixToAllRecords(RecordSet origRecordSet, String prefix) {
        RecordSet newRecordSet = new RecordSet();
        newRecordSet.setSummaryRecord(origRecordSet.getSummaryRecord());
        newRecordSet.setPagingInfo(origRecordSet.getPagingInfo());
        newRecordSet.setDataFromClient(origRecordSet.isDataFromClient());

        if (origRecordSet.getSize() <= 0) {
            List<String> origFieldNameList = origRecordSet.getFieldNameList();
            int keySize = origFieldNameList.size();
            List<String> newFieldNameList = new ArrayList<>(keySize);
            for (int i = 0; i < keySize; i++) {
                newFieldNameList.add(prefix + origFieldNameList.get(i));
            }
            newRecordSet.addFieldNameCollection(newFieldNameList);
        } else {
            int size = origRecordSet.getSize();
            for (int i = 0; i < size; i++) {
                Record newRecord = addRecordPrefix(origRecordSet.getRecord(i), prefix);
                newRecordSet.addRecord(newRecord);
            }
        }

        return newRecordSet;
    }

    private RecordHelper() {
    }
}
