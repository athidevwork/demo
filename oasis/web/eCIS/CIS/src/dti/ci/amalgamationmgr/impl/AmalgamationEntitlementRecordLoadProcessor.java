package dti.ci.amalgamationmgr.impl;

import dti.ci.amalgamationmgr.AmalgamationFields;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor .
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2009
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
public class AmalgamationEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        if (record.hasStringValue(AmalgamationFields.MANUAL_B) &&
                YesNoFlag.getInstance(record.getStringValue(AmalgamationFields.MANUAL_B)).booleanValue()) {
            record.setFieldValue(IS_DELETE_AVAILABLE, YesNoFlag.Y);
        }
        else{
            record.setFieldValue(IS_DELETE_AVAILABLE, YesNoFlag.N);
            record.setEditIndicator(YesNoFlag.N);
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
        if (recordSet.getSize() == 0) {
            recordSet.getSummaryRecord().setFieldValue(IS_DELETE_AVAILABLE, YesNoFlag.N);
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add(AmalgamationFields.POLICY_AMALGAMATION_ID);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
    }

    private static final String IS_DELETE_AVAILABLE = "isDeleteAvailable";
}
