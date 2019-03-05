package dti.pm.tailmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;

/**
 * This class extends the default record load processor to enforce entitlements for select tail web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 5, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class TailSelectEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Record summeryRecord = recordSet.getSummaryRecord();
        summeryRecord.setFieldValue("isSelectAvailable", YesNoFlag.Y);
        if (recordSet.getSize() == 0) {
            summeryRecord.setFieldValue("isSelectAvailable", YesNoFlag.N);
        }
    }
}
