package dti.pm.policymgr.premiummgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * This class extends the default record load processor a sum value for the member contribution web page. This
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 11, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/11/2012       awu         Issue 137270, added adjSum to sum all the entity adjusted amount.
 * ---------------------------------------------------
 */
public class MemberContributionRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        sum = sum + record.getDoubleValue("memberContributionAmount").doubleValue();
        adjSum = adjSum + record.getDoubleValue("adjAmount").doubleValue();
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postMemberContributionProcessRecordSet", new Object[]{recordSet});
        Record summaryRecord = recordSet.getSummaryRecord();
        summaryRecord.setFieldValue("totalAmount", sum + "");
        summaryRecord.setFieldValue("totalAdjAmount", adjSum + "");
        l.exiting(getClass().getName(), "postMemberContributionProcessRecordSet");
    }

    private double sum = 0;

    private double adjSum = 0;

}
