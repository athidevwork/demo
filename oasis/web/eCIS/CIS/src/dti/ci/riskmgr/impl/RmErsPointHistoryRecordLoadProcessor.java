package dti.ci.riskmgr.impl;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.ci.riskmgr.RiskFields;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The record load processor class for ERS Points History.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 24, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class RmErsPointHistoryRecordLoadProcessor implements RecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, Boolean.valueOf(rowIsOnCurrentPage)});
        }

        Integer netpointCap = RiskFields.getNetpointCap(record);
        if (netpointCap == null) {
            netpointCap = new Integer(0);
        }

        Integer presumptiveNetpts = RiskFields.getPresumptiveNetpts(record);
        if (presumptiveNetpts == null) {
            presumptiveNetpts = new Integer(0);
        }

        Integer netPoints;
        if (presumptiveNetpts.intValue() > netpointCap.intValue()) {
            netPoints = netpointCap;
        } else {
            netPoints = presumptiveNetpts;
        }

        RiskFields.setNetPoints(record, netPoints);

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // do nothing.
    }
}
