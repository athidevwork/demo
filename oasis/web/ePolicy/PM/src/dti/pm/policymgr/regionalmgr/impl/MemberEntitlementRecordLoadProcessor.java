package dti.pm.policymgr.regionalmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.regionalmgr.RegionalTeamFields;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for team member on Underwriting Team web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 10, 2014
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/10/2014       awu         148783 - Copied from RegionalTeamEntitlementRecordLoadProcessor to enforce entitlements for team member.
 * 03/03/2015       wdang       160953 - Added entitlement field "effectiveToDateLong" in case of no record retrieved. 
 * ---------------------------------------------------
 */
public class MemberEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        // The effectiveToDateLong is used to filter the current team members.
        String effectiveToDate = RegionalTeamFields.getEffectiveToDate(record);
        long effectiveToDateLong = 0l;
        if (!StringUtils.isBlank(effectiveToDate)) {
            effectiveToDateLong = DateUtils.parseDate(effectiveToDate).getTime();
        }
        record.setFieldValue("effectiveToDateLong", new Long(effectiveToDateLong));
        record.setFieldValue("isShowMEBDelAvailable", YesNoFlag.Y);

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{recordSet});

        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("effectiveToDateLong");
            pageEntitlementFields.add("isShowMEBDelAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        l.exiting(getClass().getName(), "postProcessRecord");
    }

    /**
     * Set initial entitlement values for a new Schedule record.
     */
    public static void setInitialEntitlementValuesForTeam(Record record) {
        new MemberEntitlementRecordLoadProcessor().postProcessRecord(record, true);
    }

}
