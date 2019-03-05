package dti.pm.transactionmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * This class extends the default record load processor to view related policies.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 5, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class RelatedPolicyRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {


        if (record.getStringValue("wipB").equals("Y")) {
            if (record.getStringValue("relType").equals("CHILD")) {
                lockChildCount++;
            }
            else if (record.getStringValue("relType").equals("PARENT")) {
                lockParentCount++;
            }
        }
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postRelatedPolicyRecordLoadProcessRecordSet", new Object[]{recordSet});
        Record summaryRecord = recordSet.getSummaryRecord();
        summaryRecord.setFieldValue("lockChildCount", new Long(lockChildCount));
        summaryRecord.setFieldValue("lockParentCount", new Long(lockParentCount));
        l.exiting(getClass().getName(), "postRelatedPolicyRecordLoadProcessRecordSet");
    }

    private int lockChildCount = 0;
    private int lockParentCount = 0;
}
