package dti.pm.policymgr.creditstackmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.creditstackmgr.CreditStackingFields;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the underwriter web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 3, 2011
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
public class CreditStackingEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

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

        if (record.hasStringValue(CreditStackingFields.CAT_PUB)) {
            String catPub = CreditStackingFields.getCatPub(record);
            // It is used to display Component Category in applied grid.
            if (!CreditStackingFields.CategoryValues.MC.equalsIgnoreCase(catPub) && !CreditStackingFields.CategoryValues.SB.equalsIgnoreCase(catPub)) {
                CreditStackingFields.setCatPub(record, CreditStackingFields.CategoryValues.OTHER);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", String.valueOf(true));
        }
        return true;
    }

}
