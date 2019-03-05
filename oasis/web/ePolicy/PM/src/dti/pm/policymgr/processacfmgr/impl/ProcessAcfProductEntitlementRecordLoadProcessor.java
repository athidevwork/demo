package dti.pm.policymgr.processacfmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.StringUtils;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.processacfmgr.ProcessAcfFields;

/**
 * This class extends the default record load processor to enforce entitlements for policy web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published to request as request attributes, which then gets intercepted by
 * pageEntitlements oasis tag to auto-generate java script that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 31, 2011
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
public class ProcessAcfProductEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
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
        Record summaryRecord = recordSet.getSummaryRecord();
        if (recordSet.getSize() == 0) {
            summaryRecord.setFieldValue(IS_SAVE_AVAILABLE, YesNoFlag.N);
            // If no data found, system should disable this page.
            EntitlementFields.setReadOnly(summaryRecord, true);
        }
        else {
            summaryRecord.setFieldValue(IS_SAVE_AVAILABLE, YesNoFlag.Y);
        }
    }

    public static final String IS_SAVE_AVAILABLE = "isSaveAvailable";
}