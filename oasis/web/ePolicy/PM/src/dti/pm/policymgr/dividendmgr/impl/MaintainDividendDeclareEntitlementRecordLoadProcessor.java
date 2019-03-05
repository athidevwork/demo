package dti.pm.policymgr.dividendmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.dividendmgr.DividendFields;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for maintain dividend declaration.
 * <p/>
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   March 12, 2012
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class MaintainDividendDeclareEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        DividendFields.setIsDividendEditable(record, YesNoFlag.Y);
        // Status has values Pending and Processed.
        if (record.hasStringValue(DividendFields.STATUS)) {
            String status = DividendFields.getStatus(record);
            if (status.equalsIgnoreCase(DividendFields.STATUS_PROCESSED)) {
                DividendFields.setIsDividendEditable(record, YesNoFlag.N);
            }
        }

        l.exiting(getClass().getName(), "postProcessRecord");

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

        Record record = recordSet.getSummaryRecord();
        DividendFields.setIsProcessAvailable(record, YesNoFlag.N);
        // If no record is fetched, add the page entitlement fields to the record set field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add(DividendFields.IS_PROCESS_AVAILABLE);
            pageEntitlementFields.add(DividendFields.IS_DIVIDEND_EDITABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        } else {
            // For Preview and Process in maintain dividend declaration page
            // Preview/Process: not empty record list.
            DividendFields.setIsProcessAvailable(record, YesNoFlag.Y);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecordSet");
        }

    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public MaintainDividendDeclareEntitlementRecordLoadProcessor() {
    }

}