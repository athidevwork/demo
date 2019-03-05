package dti.pm.riskmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.riskmgr.RiskFields;

import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * This class extends the default record load processor to enforce entitlements for risk address phone copy page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 14, 2008
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/07/2010       bhong       Deleted the logics that set value of indicator fields for phone/address
 *                              form fields since they are removed from page entitlements and controlled
 *                              by field dependency.
 * ---------------------------------------------------
 */
public class AddressPhoneEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        // set foreign zip for the record, it is same with zipCode
        RiskFields.setForeignZip(record, RiskFields.getZipCode(record));

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isCopyAllAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
            recordSet.getSummaryRecord().setFieldValue("isCopyAllAvailable", YesNoFlag.N);
        }
        else {
            recordSet.getSummaryRecord().setFieldValue("isCopyAllAvailable", YesNoFlag.Y);
        }
    }

    public AddressPhoneEntitlementRecordLoadProcessor() {
    }
}
