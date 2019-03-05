package dti.pm.coverageclassmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.coverageclassmgr.CoverageClassFields;
import dti.pm.coveragemgr.CoverageFields;
import org.apache.struts.util.LabelValueBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class is used to handle the coverage class Group data.
 * By grouping available coverage class to separate RecordSet and combining them together at the end,
 * we can display the available coverage class in proper order.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   July 14, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CoverageClassGroupRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, rowIsOnCurrentPage});

        record.setFieldValue("isExposureAvailable", YesNoFlag.Y);

        l.exiting(getClass().getName(), "postProcessRecord", true);
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});
        // Set the exposure unit to all records.
        recordSet.setFieldValueOnAll(CoverageClassFields.EXPOSURE_UNIT, "");
        // Set coverage_group_code and group_expand_collapse to all records.
        recordSet.setFieldValueOnAll(CoverageFields.GROUP_EXPAND_COLLAPSE, "");
        recordSet.setFieldValueOnAll(CoverageFields.COVERAGE_GROUP_CODE, "");

        if (recordSet.getSize() > 0) {
            // The group map.
            HashMap groupMap = new HashMap();
            // The set of group and its coverage class.
            RecordSet groupItemRs;
            // The set of coverage class which doesn't belong to any group.
            RecordSet nonGroupItemRs = new RecordSet();
            // Group the records
            int groupId = -1;
            Iterator rsIter = recordSet.getRecords();
            List covgGroup = new ArrayList();
            while (rsIter.hasNext()) {
                Record rec = (Record) rsIter.next();
                // The sequenceNo is used to sort grid and it is returned as BigDecimal from DB, the sequence no of group is string as below.
                // Avoid error while sorting, they should be converted as the same.
                CoverageFields.setSequenceNo(rec, CoverageFields.getSequenceNo(rec));
                String coverageGroup = CoverageFields.getCoverageGroup(rec);
                boolean added = false;
                Iterator groupIter = getGroupList().iterator();
                while (groupIter.hasNext()) {
                    LabelValueBean lvb = (LabelValueBean) groupIter.next();
                    String value = lvb.getValue();
                    // System handles the coverage class whose coverage_group in group list.
                    // System set the group's short description to the Group but set empty to the subordinate coverage.
                    if (!StringUtils.isBlank(value) && value.equals(coverageGroup)) {
                        CoverageFields.setCoverageGroup(rec, null);
                        CoverageFields.setCoverageGroupCode(rec, coverageGroup);
                        if (groupMap.containsKey(coverageGroup)) {
                            groupItemRs = (RecordSet) groupMap.get(coverageGroup);
                        }
                        else {
                            covgGroup.add(coverageGroup);
                            groupItemRs = new RecordSet();
                            groupMap.put(coverageGroup, groupItemRs);
                            // Add an empty record to each group
                            Record record = new Record();
                            Iterator fnIter = rec.getFieldNames();
                            while (fnIter.hasNext()) {
                                record.setFieldValue((String) fnIter.next(), "***");
                            }
                            // Overwrite some necessary fields. The sequence no is used to sort, it can't be ***.
                            CoverageFields.setSequenceNo(record, "-1");
                            CoverageFields.setCoverageGroup(record, lvb.getLabel());
                            CoverageClassFields.setProductCoverageClassId(record, String.valueOf(groupId));
                            record.setFieldValue(RequestIds.SELECT_IND, new Long(0));
                            CoverageFields.setGroupExpandCollapse(record, "+");
                            CoverageFields.setCoverageGroupCode(record, CoverageFields.getCoverageGroupCode(rec));
                            record.setFieldValue("isExposureAvailable", YesNoFlag.N);
                            groupItemRs.addRecord(record);
                            groupId--;
                        }
                        groupItemRs.addRecord(rec);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    nonGroupItemRs.addRecord(rec);
                }
            }

            RecordSet returnRec = new RecordSet();
            Iterator iter = covgGroup.iterator();
            while (iter.hasNext()) {
                returnRec.addRecords((RecordSet) groupMap.get(iter.next()));
            }
            returnRec.addRecords(nonGroupItemRs);
            Record sumRec = recordSet.getSummaryRecord();
            List fieldNames = (List) ((ArrayList) recordSet.getFieldNameList()).clone();
            // Replace the old record set by newly changed record set.
            recordSet.clear();
            recordSet.addRecords(returnRec);
            recordSet.addFieldNameCollection(fieldNames);
            recordSet.setSummaryRecord(sumRec);
        }
        else {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isExposureAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    public List getGroupList() {
        return groupList;
    }

    public void setGroupList(List groupList) {
        this.groupList = groupList;
    }

    public CoverageClassGroupRecordLoadProcessor(List groupList) {
        groupList = groupList != null ? groupList : Collections.EMPTY_LIST;
        setGroupList(groupList);
    }

    private CoverageClassGroupRecordLoadProcessor() {
        setGroupList(Collections.EMPTY_LIST);
    }

    private List groupList;
}