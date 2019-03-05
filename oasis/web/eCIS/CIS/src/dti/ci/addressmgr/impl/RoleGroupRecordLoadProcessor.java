package dti.ci.addressmgr.impl;

import dti.ci.addressmgr.AddressFields;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import org.apache.struts.util.LabelValueBean;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Feb 23, 2011
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class RoleGroupRecordLoadProcessor extends DefaultRecordLoadProcessor {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        return super.postProcessRecord(record, rowIsOnCurrentPage);
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecordSet", new Object[]{recordSet});
        }

        recordSet.setFieldValueOnAll(AddressFields.ROLE_GROUP, null);
        recordSet.setFieldValueOnAll(AddressFields.ROLE_PARENT, null);
        recordSet.setFieldValueOnAll("groupExpandCollapse", "");
        HashMap parentToRecords = new HashMap();
        RecordSet hasParentRs;
        RecordSet hasNoParentRs = new RecordSet();
        // Group the records
        int groupId = -1;
        Iterator rsIter = recordSet.getRecords();
        List compParent = new ArrayList();
        while (rsIter.hasNext()) {
            Record rec = (Record) rsIter.next();
            String strRoleTypeCode = rec.getStringValue(AddressFields.ROLE_TYPE_CODE);
            Iterator groupIter = groupList.iterator();
            boolean added = false;
            while (groupIter.hasNext()) {
                LabelValueBean lvb = (LabelValueBean) groupIter.next();
                String value = lvb.getValue();
                if (value.equals(strRoleTypeCode)) {
                    rec.setFieldValue(AddressFields.ROLE_PARENT, strRoleTypeCode);
                    if (parentToRecords.containsKey(strRoleTypeCode)) {
                        hasParentRs = (RecordSet) parentToRecords.get(strRoleTypeCode);
                    } else {
                        compParent.add(strRoleTypeCode);
                        hasParentRs = new RecordSet();
                        parentToRecords.put(strRoleTypeCode, hasParentRs);
                        // Add an empty record to each group
                        Record record = new Record();
                        Iterator fnIter = rec.getFieldNames();
                        while (fnIter.hasNext()) {
                            record.setFieldValue((String) fnIter.next(), "***");
                        }
                        record.setFieldValue(AddressFields.ROLE_GROUP, value);
                        record.setFieldValue(AddressFields.ENTITY_ROLE_ID, groupId);
                        record.setFieldValue(RequestIds.SELECT_IND, new Long(0));
                        record.setFieldValue("groupExpandCollapse", "-");
                        record.setFieldValue(AddressFields.ROLE_PARENT, strRoleTypeCode);
                        hasParentRs.addRecord(record);
                        groupId--;
                    }
                    hasParentRs.addRecord(rec);
                    added = true;
                    break;
                }
            }
            if (!added) {
                hasNoParentRs.addRecord(rec);
            }
        }
        RecordSet returnRec = new RecordSet();
        Iterator iter = compParent.iterator();
        while (iter.hasNext()) {
            returnRec.addRecords((RecordSet) parentToRecords.get(iter.next()));
        }
        returnRec.addRecords(hasNoParentRs);
        Record sumRec = recordSet.getSummaryRecord();
        List fieldNames = (List) ((ArrayList) recordSet.getFieldNameList()).clone();
        recordSet.clear();
        recordSet.addRecords(returnRec);
        recordSet.addFieldNameCollection(fieldNames);
        recordSet.setSummaryRecord(sumRec);

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    public List getGroupList() {
        return groupList;
    }

    public void setGroupList(List groupList) {
        this.groupList = groupList;
    }

    public RoleGroupRecordLoadProcessor(List groupList) {
        groupList = groupList != null ? groupList : Collections.EMPTY_LIST;
        setGroupList(groupList);
    }

    public RoleGroupRecordLoadProcessor() {
        setGroupList(Collections.EMPTY_LIST);
    }

    private List groupList;
}
