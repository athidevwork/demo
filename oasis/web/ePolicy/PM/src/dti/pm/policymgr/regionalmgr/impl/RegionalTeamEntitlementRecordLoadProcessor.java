package dti.pm.policymgr.regionalmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.logging.Logger;


/**
 * This class extends the default record load processor to enforce entitlements for regional team on Underwriting Team web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 19, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/13/2014        awu        148783 - Modified this file to enforce entitlements for regional team grid.
 * ---------------------------------------------------
 */
public class RegionalTeamEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {


    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record the current record
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        record.setFieldValue("isShowCurrentAvailable", YesNoFlag.Y);
        record.setFieldValue("isShowAllAvailable", YesNoFlag.N);
        record.setFieldValue("isShowTeamDelAvailable", YesNoFlag.Y);
        record.setFieldValue("isShowTeamAddAvailable", YesNoFlag.Y);
        record.setFieldValue("isShowMEBAddAvailable", YesNoFlag.Y);

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        // Defaults the show current button enabled and show all button disabled.
        Record record = recordSet.getSummaryRecord();
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isShowCurrentAvailable");
            pageEntitlementFields.add("isShowAllAvailable");
            pageEntitlementFields.add("isShowTeamDelAvailable");
            pageEntitlementFields.add("isShowMEBAddAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        record.setFieldValue("isShowTeamAddAvailable", YesNoFlag.Y);

        l.exiting(getClass().getName(), "postProcessRecord");
    }

    /**
     * Set initial entitlement values for a new Schedule record.
     */
    public static void setInitialEntitlementValuesForTeam(Record record) {
        new RegionalTeamEntitlementRecordLoadProcessor().postProcessRecord(record, true);
    }
}
