package dti.pm.componentmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.core.http.RequestIds;

import java.util.logging.Logger;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;

import org.apache.struts.util.LabelValueBean;

/**
 * This class is used to handle the Component Group data.
 * By grouping available components to separate RecordSet and combining them together at the end,
 * we can display the available components in proper order.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 7, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/14/2007       sxm         Changed group ID to negative number to avoide possible conflict with component PKs.
 * 11/20/2007       fcb         logic to get info from parentToRecords in the order it was added in order to fix
 *                              incorrect order of the components.
 * 12/28/2009       bhong       issue#102238, removed this empty record to avoid confusion since it displays
 *                              but nothing behind it so it doesn't expand.
 * ---------------------------------------------------
 */
public class ComponentGroupRecordLoadProcessor extends DefaultRecordLoadProcessor {
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
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        recordSet.setFieldValueOnAll(ComponentFields.COMPONENT_GROUP, null);
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
            String componentParent = ComponentFields.getComponentParent(rec);
            Iterator groupIter = groupList.iterator();
            boolean added = false;
            while (groupIter.hasNext()) {
                LabelValueBean lvb = (LabelValueBean) groupIter.next();
                String value = lvb.getValue();
                if (value.equals(componentParent)) {
                    //ComponentFields.setComponentGroup(rec, lvb.getLabel());
                    if (parentToRecords.containsKey(componentParent)) {
                        hasParentRs = (RecordSet) parentToRecords.get(componentParent);
                    }
                    else {
                        compParent.add(componentParent);
                        hasParentRs = new RecordSet();
                        parentToRecords.put(componentParent, hasParentRs);
                        // Add an empty record to each group
                        Record record = new Record();
                        Iterator fnIter = rec.getFieldNames();
                        while (fnIter.hasNext()) {
                            record.setFieldValue((String) fnIter.next(), "***");
                        }
                        ComponentFields.setComponentGroup(record, lvb.getLabel());
                        ComponentFields.setProductCovComponentId(record, String.valueOf(groupId));
                        record.setFieldValue(RequestIds.SELECT_IND, new Long(0));
                        record.setFieldValue("groupExpandCollapse", "+");
                        ComponentFields.setParentCoverageComponentCode(record, ComponentFields.getComponentParent(rec));
                        hasParentRs.addRecord(record);
                        groupId --;
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
            returnRec.addRecords((RecordSet)parentToRecords.get(iter.next()));
        }
        returnRec.addRecords(hasNoParentRs);
        Record sumRec = recordSet.getSummaryRecord();
        List fieldNames = (List) ((ArrayList)recordSet.getFieldNameList()).clone();
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

    public ComponentGroupRecordLoadProcessor(List groupList) {
        groupList = groupList != null ? groupList : Collections.EMPTY_LIST;
        setGroupList(groupList);
    }

    public ComponentGroupRecordLoadProcessor() {
        setGroupList(Collections.EMPTY_LIST);
    }

    private List groupList;
}
