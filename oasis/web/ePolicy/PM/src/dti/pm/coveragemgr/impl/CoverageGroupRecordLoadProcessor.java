package dti.pm.coveragemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.util.LabelValueBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class is used to handle the coverage group data.
 * By grouping available coverage to separate RecordSet and combining them together at the end,
 * we can display the available coverage in proper order.
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
 *  12/13/2013      Jyang       149443 - Remove the code which try to get coverageEffectiveFromDate from coverageHeader,
 *                                       and apply the policy termEffDate to the available coverages' effectiveDate.
 *  01/24/2014      adeng       149172 - Modified postProcessRecordSet() to add logic to avoid set '***' to a
 *                                       FORMATTEDNUMBER display type field.
 * ---------------------------------------------------
 */
public class CoverageGroupRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, rowIsOnCurrentPage});

        // Default isCoverageLimitCodeAvailable to Y.
        record.setFieldValue("isCoverageLimitCodeAvailable", YesNoFlag.Y);

        // Determine if annual base rate is visible
        record.setFieldValue("isManuallyRated", isManuallyRated(record));

        // Determine if retroactive date is visible
        record.setFieldValue("isRetroDateAvailable", isRetroDateAvailable(record));

        // Determine if shared limit b is visible
        record.setFieldValue("isSharedLimitBAvailable", isSharedLimitBAvailable(record));

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
        // Set the annual_base_rate and retroactive date to all records.
        recordSet.setFieldValueOnAll(CoverageFields.ANNUAL_BASE_RATE, "");
        recordSet.setFieldValueOnAll(CoverageFields.RETRO_DATE, "");
        // Set coverage_group_code and group_expand_collapse to all records.
        recordSet.setFieldValueOnAll(CoverageFields.GROUP_EXPAND_COLLAPSE, "");
        recordSet.setFieldValueOnAll(CoverageFields.COVERAGE_GROUP_CODE, "");
        // Add coverageEffectiveFromDate to retrieve LOV of coverage limit code.
        String coverageEffectiveFromDate = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        recordSet.setFieldValueOnAll(CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE, coverageEffectiveFromDate);
        if (recordSet.getSize() > 0) {
            // The group map.
            HashMap groupMap = new HashMap();
            // The set of group and its coverage.
            RecordSet groupItemRs;
            // The set of coverage which doesn't belong to any group.
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
                    // System handles the coverage whose coverage_group in group list.
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
                            String fieldName;
                            while (fnIter.hasNext()) {
                                fieldName = (String) fnIter.next();
                                try {
                                    OasisFormField field = (OasisFormField) m_oasisFields.getField(fieldName + "_GH");
                                    if (!(field.getDisplayType() != null && field.getDisplayType().equals(OasisFields.DISPLAY_TYPE_FORMATTEDNUMBER))) {
                                        record.setFieldValue(fieldName, "***");
                                    }
                                    else {
                                        record.setFieldValue(fieldName, "");
                                    }
                                }
                                catch (Exception e) {
                                    record.setFieldValue(fieldName, "***");
                                }
                            }
                            // Overwrite some necessary fields. The sequence no is used to sort, it can't be ***.
                            CoverageFields.setSequenceNo(record, "-1");
                            CoverageFields.setCoverageGroup(record, lvb.getLabel());
                            CoverageFields.setProductCoverageId(record, String.valueOf(groupId));
                            record.setFieldValue(RequestIds.SELECT_IND, new Long(0));
                            CoverageFields.setGroupExpandCollapse(record, "+");
                            CoverageFields.setCoverageGroupCode(record, CoverageFields.getCoverageGroupCode(rec));
                            record.setFieldValue("isCoverageLimitCodeAvailable", YesNoFlag.N);
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
            pageEntitlementFields.add("isManuallyRated");
            pageEntitlementFields.add("isRetroDateAvailable");
            pageEntitlementFields.add("isSharedLimitBAvailable");
            pageEntitlementFields.add("isCoverageLimitCodeAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Determine if annual base rate is visible.
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isManuallyRated(Record record) {
        return YesNoFlag.getInstance(getCoverageManager().isManuallyRated(CoverageFields.getRatingModuleCode(record)));
    }

    /**
     * Determine if retroactive date is visible.
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isRetroDateAvailable(Record record) {
        return YesNoFlag.getInstance("CM".equals(CoverageFields.getPolicyFormCode(record)));
    }

    /**
     * Determine if shared limit b is visible.
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isSharedLimitBAvailable(Record record) {
        return YesNoFlag.getInstance("Y".equals(CoverageFields.getProductSharedLimitB(record)));
    }

    /**
     * Constructor method.
     *
     * @param coverageManager
     * @param groupList
     */
    public CoverageGroupRecordLoadProcessor(CoverageManager coverageManager, List groupList, PolicyHeader policyHeader, OasisFields oasisFields) {
        setCoverageManager(coverageManager);
        groupList = groupList != null ? groupList : Collections.EMPTY_LIST;
        setGroupList(groupList);
        setPolicyHeader(policyHeader);
        setOasisFields(oasisFields);
    }

    public List getGroupList() {
        return groupList;
    }

    public void setGroupList(List groupList) {
        this.groupList = groupList;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }

    public void setOasisFields(OasisFields oasisFields) {
        m_oasisFields = oasisFields;
    }

    private CoverageGroupRecordLoadProcessor() {
        setGroupList(Collections.EMPTY_LIST);
    }

    private CoverageManager m_coverageManager;
    private List groupList;
    private PolicyHeader m_policyHeader;
    private OasisFields m_oasisFields;
}