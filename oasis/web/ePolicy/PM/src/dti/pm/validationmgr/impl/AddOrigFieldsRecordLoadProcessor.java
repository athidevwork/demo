package dti.pm.validationmgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;


/**
 * This class adds field(s)'s orignal value to the record set.
 * The purpose of this class is to stored the field's original value for validation.
 * <p/>
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Jue 05, 2007
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/09/2007       Joe Shen    Supports different prefix instead of "orig"
 * ---------------------------------------------------
 */
public class AddOrigFieldsRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Add field's original value
     *
     * @param record
     * @param rowIsOnCurrentPage
     * @return boolean
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord",
                new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }

        String origFieldPrefix = getCustOrigFieldPrefix() != null ? getCustOrigFieldPrefix() : ORIGINAL_FIELD_PREFIX;
        String[] fieldNameList = getFieldNameList();
        int len = fieldNameList.length;
        for (int i = 0; i < len; i++) {
            String orgFieldName = origFieldPrefix + fieldNameList[i];
            record.setFieldValue(orgFieldName, record.getFieldValue(fieldNameList[i], null)); 
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord");
        }
        return true;
    }

    public void postProcessRecordSet(RecordSet recordSet) {
        if(recordSet.getSize() == 0) {
            String origFieldPrefix = getCustOrigFieldPrefix() != null ? getCustOrigFieldPrefix() : ORIGINAL_FIELD_PREFIX;
            List orgFields = new ArrayList();
            String[] fieldNameList = getFieldNameList();
            int len = fieldNameList.length;
            for (int i = 0; i < len; i++) {
                String orgFieldName = origFieldPrefix + fieldNameList[i];
                orgFields.add(orgFieldName);
            }
            recordSet.addFieldNameCollection(orgFields);
        }
    }

    public AddOrigFieldsRecordLoadProcessor(String[] fieldNameList) {
        setFieldNameList(fieldNameList);
        setCustOrigFieldPrefix(null);
    }

    public AddOrigFieldsRecordLoadProcessor(String[] fieldNameList, String custOrgFieldPrefix) {
        setFieldNameList(fieldNameList);
        setCustOrigFieldPrefix(custOrgFieldPrefix);
    }

    public String[] getFieldNameList() {
        return m_fieldNameList;
    }

    public void setFieldNameList(String[] fieldNameList) {
        m_fieldNameList = fieldNameList;
    }

    public String getCustOrigFieldPrefix() {
        return m_custOrigFieldPrefix;
    }

    public void setCustOrigFieldPrefix(String custOrigFieldPrefix) {
        m_custOrigFieldPrefix = custOrigFieldPrefix;
    }

    private String[] m_fieldNameList;
    private static final String ORIGINAL_FIELD_PREFIX = "orig";
    private String m_custOrigFieldPrefix;
}
