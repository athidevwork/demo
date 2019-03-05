package dti.pm.policymgr.limitsharingmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.policymgr.PolicyHeader;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the share group web page.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 23, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/24/2010       syang       Issue 108651 - Added isRenewalBAvailable() to handle Renew indicator.
 * 06/25/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * ---------------------------------------------------
 */

public class SharedDetailEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        if (screenMode.isViewPolicy() || screenMode.isViewEndquote() ||
            screenMode.isCancelWIP() || screenMode.isResinstateWIP() ||
           (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !policyHeader.isInitTermB()) {
            record.setFieldValue("isDelShareDetailAvailable", YesNoFlag.N);
            record.setFieldValue("isShareDetailDataEditable", YesNoFlag.N);
            record.setFieldValue("isRenewalBAvailable", YesNoFlag.N);
        }
        else {
            String recordModeCode = record.getStringValue("shareDtlRecordModeCode");
            record.setFieldValue("isShareDetailDataEditable", isDataEditable(record));
            record.setFieldValue("isRenewalBAvailable", isDataEditable(record));
            if (recordModeCode.equals("TEMP")) {
                record.setFieldValue("isDelShareDetailAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isDelShareDetailAvailable", YesNoFlag.N);
            }
        }
        // Issue 108651 - handle Renew indicator.
        record.setFieldValue("isRenewalBAvailable", isRenewalBAvailable(record));

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
            pageEntitlementFields.add("isDelShareDetailAvailable");
            pageEntitlementFields.add("isShareDetailDataEditable");
            pageEntitlementFields.add("isRenewalBAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
    }

    /**
     * Set initial entitlement Values for shared detail
     *
     * @param record
     */
    public void setInitialEntitlementValuesForSharedDetail(Record record) {
        postProcessRecord(record, true);
    }

    private YesNoFlag isDataEditable(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isDataEditable", new Object[]{record,});
        }
        YesNoFlag isEditable = YesNoFlag.N;
        Date transactionEffFromDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        Date currentEffFromDate = DateUtils.parseDate(record.getStringValue("shareDtlEffFromDate"));
        Date currentEffToDate = DateUtils.parseDate(record.getStringValue("shareDtlEffToDate"));
        if ((!(transactionEffFromDate.before(currentEffFromDate))) && (transactionEffFromDate.before(currentEffToDate))) {
            isEditable = YesNoFlag.Y;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isDataEditable", isEditable);
        }
        return isEditable;
    }

    /**
     * Check if the renew indicator is available.
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isRenewalBAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.getInstance(record.getStringValue("isRenewalBAvailable"));
        Date effectiveToDate = record.getDateValue("shareDtlEffToDate");
        Date termExpirationDate = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());
        if (isAvailable.booleanValue() && effectiveToDate.before(termExpirationDate)) {
            isAvailable = YesNoFlag.N;
        }
        return isAvailable;
    }

    public SharedDetailEntitlementRecordLoadProcessor() {
    }

    public SharedDetailEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        this.policyHeader = policyHeader;
    }

    private PolicyHeader policyHeader;
}
