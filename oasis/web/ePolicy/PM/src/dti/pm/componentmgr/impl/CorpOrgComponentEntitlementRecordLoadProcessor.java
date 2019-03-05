package dti.pm.componentmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Record load processor for process Org/Corp component.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 20, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CorpOrgComponentEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Record summaryRecord = recordSet.getSummaryRecord();
        if (recordSet.getSize() <= 0) {
            List nameList = new ArrayList();
            nameList.add("policyId");
            recordSet.addFieldNameCollection(nameList);
            summaryRecord.setFieldValue("isProcessAvailable", "N");
        }
        else {
            summaryRecord.setFieldValue("isProcessAvailable", "Y");
        }
        // The History option is available if the Parent Organization is selected.
        summaryRecord.setFieldValue("isHistoryAvailable", "Y");
    }
}
