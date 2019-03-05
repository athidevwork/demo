package dti.pm.notesmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.security.Authenticator;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class extends the default record load processor to enforce entitlements for part time notes page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 01, 2008
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/26/2013       fcb         145725 - Added logic to check the profile in UserCacheManager via OasisUser.
 * ---------------------------------------------------
 */
public class NotesEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord",
                new Object[]{record, Boolean.valueOf(rowIsOnCurrentPage)});
        }
        boolean hasProfile = UserSessionManager.getInstance().getUserSession().getOasisUser().hasProfile("PM_OVERRIDE_PT_NOTE");

        if (hasProfile) {
            record.setFieldValue("isEffectiveFromDateEditable", "Y");
            record.setFieldValue("isEffectiveToDateEditable", "Y");
            record.setFieldValue("isNotesEditable", "Y");
        }
        else {
            record.setFieldValue("isEffectiveFromDateEditable", "N");
            record.setFieldValue("isEffectiveToDateEditable", "N");
            record.setFieldValue("isNotesEditable", "N");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", Boolean.TRUE);
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

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isEffectiveDateEditable");
            pageEntitlementFields.add("isExpirationDateEditable");
            pageEntitlementFields.add("isNotesEditable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        else {
            // Enable "effectiveToDate" field for the most recent record.
            // It should be first record in the recordSet based on the sorted reference cursor
            recordSet.getRecord(0).setFieldValue("isEffectiveToDateEditable", "Y");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecordSet");
        }
    }
}
