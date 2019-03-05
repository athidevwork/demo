package dti.pm.policymgr.dividendmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.dividendmgr.DividendFields;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for calculated dividend.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/07/2013       Sharon Ma   Issue 145501 - Remove policy term type & cancel indicator checks for Post button
 * ---------------------------------------------------
 */

public class CalculatedDividendRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *                                                                                                              pr
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecordSet", new Object[]{recordSet});
        }

        // For Print and Post option in calculated dividend page
        // Print: not empty record list
        // Post: not empty record list
        Record sumRecord = recordSet.getSummaryRecord();
        DividendFields.setIsPrintAvailable(sumRecord, YesNoFlag.N);
        DividendFields.setIsPostAvailable(sumRecord, YesNoFlag.N);
        if (recordSet.getSize()>0) {
            DividendFields.setIsPrintAvailable(sumRecord, YesNoFlag.Y);
            DividendFields.setIsPostAvailable(sumRecord, YesNoFlag.Y);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecordSet");
        }

    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public CalculatedDividendRecordLoadProcessor(Record dataRecord) {
        m_dataRecord = dataRecord;
    }

    private Record m_dataRecord;

}
