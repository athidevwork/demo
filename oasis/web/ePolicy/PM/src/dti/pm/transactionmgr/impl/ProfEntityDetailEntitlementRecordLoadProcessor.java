package dti.pm.transactionmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * This class extends the default record load processor to enforce entitlements for risk web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 06, 2010
 *
 * @author syang
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ProfEntityDetailEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // System should hide the Print if no data found.
        YesNoFlag isPrintAvailable = YesNoFlag.N;
        if (recordSet.getSize() > 0) {
            isPrintAvailable = YesNoFlag.Y;
        }
        recordSet.getSummaryRecord().setFieldValue("isPrintAvailable", isPrintAvailable);
    }
}