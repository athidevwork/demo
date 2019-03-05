package dti.pm.policymgr.specialhandlingmgr.impl;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.busobjs.ScreenModeCode;

import java.util.Date;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the special handling web page. This
 * class works in conjunction with pageEntitlements.xml configuration.
 *
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 27, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 26/04/2008       yhyang      Modify postProcessRecordSet for issue 81842
 * 08/23/2010       syang       Issue 108651 - Added isRenewalBAvailable() to handle Renew indicator.
 * 11/19/2015       eyin        167171 - Modified getInitialEntitlementValuesForSpecialHandling(), Add synchronized lock on this function.
 * ---------------------------------------------------
 */
public class SpecialHandlingEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.N);
        // Issue 108651 - handle Renew indicator.
        record.setFieldValue("isRenewalBAvailable", isRenewalBAvailable(record));
        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            recordSet.addFieldNameCollection(getInitialEntitlementValuesForSpecialHandling().getFieldNameList());
        }
        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), isRecordSetReadOnly());
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Return a Record of initial entitlement values for a new Schedule record.
     */
    public synchronized static Record getInitialEntitlementValuesForSpecialHandling() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            EntitlementFields.setIsRowEligibleForDelete(c_initialEntitlementValues, YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue("isRenewalBAvailable", YesNoFlag.Y);
        }
        EntitlementFields.setReadOnly(c_initialEntitlementValues, false);
        return c_initialEntitlementValues;
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();

        // All available fields are readonly if the screen mode is NOT configured in the PM_ATTRIBUTE table and if:
        // 1.Policy has CANCEL status, or
        // 2.Cancel Transaction in Progress, or
        // 3.Reinstatement Transaction in Progress
        if (!YesNoFlag.getInstance(getEditableInd()).booleanValue() &&
            (getPolicyHeader().getPolicyStatus().isCancelled() || screenModeCode.isCancelWIP() || screenModeCode.isResinstateWIP())) {
            isReadOnly = true;
        }

        return isReadOnly;
    }

    /**
     * Check if the renew indicator is available.
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isRenewalBAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.Y;
        Date effectiveToDate = record.getDateValue("effectiveToDate");
        Date termExpirationDate = DateUtils.parseDate(getPolicyHeader().getTermEffectiveToDate());
        if (effectiveToDate.before(termExpirationDate)) {
            isAvailable = YesNoFlag.N;
        }
        return isAvailable;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public SpecialHandlingEntitlementRecordLoadProcessor(){}

    public SpecialHandlingEntitlementRecordLoadProcessor(PolicyHeader policyHeader, String editableInd) {
        setPolicyHeader(policyHeader);
        setEditableInd(editableInd);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public String getEditableInd() {
        return m_editable;
    }

    public void setEditableInd(String editableInd) {
        m_editable = editableInd;
    }

    private PolicyHeader m_policyHeader;

    private static Record c_initialEntitlementValues;

    private String m_editable;
}
