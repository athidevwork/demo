package dti.pm.policymgr.distributionmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.distributionmgr.DistributionFields;

import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the distribution web page.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/21/2013       xnie        142674 - Modify postProcessRecord() for catch up dividend case.
 * ---------------------------------------------------
 */

public class DistributionEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        record.setFieldValue("isProcessAvailable", YesNoFlag.Y);
        record.setFieldValue("isCatchUpAvailable", YesNoFlag.Y);
        if (record.hasStringValue(DistributionFields.ACTION)) {
            String action = DistributionFields.getAction(record);
            if ((action.equalsIgnoreCase(DistributionFields.ActionValues.ACTION_CALCWIP)
                 || action.equalsIgnoreCase(DistributionFields.ActionValues.ACTION_CALCDONE))) {
                record.setFieldValue("isProcessAvailable", YesNoFlag.N);
            }

            if (!action.equalsIgnoreCase(DistributionFields.ActionValues.ACTION_CALC_TRANS)) {
                record.setFieldValue("isCatchUpAvailable", YesNoFlag.N);
            }
        }
        else {
            record.setFieldValue("isCatchUpAvailable", YesNoFlag.N);
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
    }

}
