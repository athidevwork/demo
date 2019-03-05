package dti.pm.policymgr.mailingmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.entitlementmgr.EntitlementFields;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to handle Implementation of Product Mailing.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 14, 2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/19/2015       eyin        167171 - Modified getInitialEntitlementValuesForAddtionalInsured(), Add synchronized lock on this function.
 * ---------------------------------------------------
 */

public class ProductMailingEntitlementRecordLoadProcessor implements RecordLoadProcessor {

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
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }
        EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.Y);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", String.valueOf(true));
        }
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecordSet", new Object[]{recordSet});
        }

        if (recordSet.getSize() == 0) {
            List fieldNames = new ArrayList();
            fieldNames.add(EntitlementFields.IS_ROW_ELIGIBLE_FOR_DELETE);
            recordSet.addFieldNameCollection(fieldNames);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecordSet");
        }

    }

    /**
     * Return a Record of initial entitlement values for a new Product Mailing.
     */
    public synchronized static Record getInitialEntitlementValuesForAddtionalInsured() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            EntitlementFields.setIsRowEligibleForDelete(c_initialEntitlementValues, YesNoFlag.Y);
            EntitlementFields.setReadOnly(c_initialEntitlementValues, false);
            c_initialEntitlementValues.setFieldValue("isRenewalBAvailable", YesNoFlag.Y);
        }
        return c_initialEntitlementValues;
    }

    private static Record c_initialEntitlementValues;
}
