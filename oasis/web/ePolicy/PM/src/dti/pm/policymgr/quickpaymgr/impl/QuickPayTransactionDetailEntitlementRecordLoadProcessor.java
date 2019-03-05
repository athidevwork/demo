package dti.pm.policymgr.quickpaymgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.quickpaymgr.QuickPayManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p/>
 * This class extends the default record load processor to enforce
 * entitlements for Quick Pay Transaction Details web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 29, 2010
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/19/2010       dzhang      Update per Bill's comments.
 * 03/14/2016       eyin        169611 - Modified postProcessRecord(), remove the check that qpEligAmount must be
 *                              greater than 0, allow give QP $ on negative transactions also.
 * ---------------------------------------------------
 */

public class QuickPayTransactionDetailEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }
        
        String openMode = getInputRecord().getStringValue("openMode");
        //If the openMode is viewOnly, the Save option will be hidden. And share with the other openMode, move the
        //logic to row level.
        if (!StringUtils.isBlank(openMode) && openMode.equals("VIEW_ONLY")) {
            displayInViewOnlyMode(record);
        }
        else if (!StringUtils.isBlank(openMode) && openMode.equals("ADD_QPDISCOUNT") &&
            record.hasStringValue("riskCoverageQpEligAmount") &&
            !StringUtils.isBlank(record.getStringValue("riskCoverageQpEligAmount"))) {
            float qpEligAmount = record.getFloatValue("riskCoverageQpEligAmount");
            if (!getIsHospitalCoveragePayor(record)) {
                record.setFieldValue("isSaveAvailable", YesNoFlag.Y);
                record.setFieldValue("isRiskCoverageQpAmountEditable", YesNoFlag.Y);
            }
            else {
                displayInViewOnlyMode(record);
            }
        }
        else {
            displayInViewOnlyMode(record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord");
        }
        return true;
    }

    /**
     * Display in viewOnly mode.
     *
     * @param record record
     */
    private void displayInViewOnlyMode(Record record) {
        record.setFieldValue("isSaveAvailable", YesNoFlag.N);
        record.setFieldValue("isRiskCoverageQpAmountEditable", YesNoFlag.N);
    }

    public QuickPayTransactionDetailEntitlementRecordLoadProcessor(Record inputRecord, QuickPayManager quickPayManager) {
        setInputRecord(inputRecord);
        setQuickPayManager(quickPayManager);
    }

    public void setInputRecord(Record inputRecord) {
        this.m_inputRecord = inputRecord;
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    private boolean getIsHospitalCoveragePayor(Record record) {
        Record inputRecord = getInputRecord();
        inputRecord.setFieldValue("coverageId", record.getFieldValue("coverageId"));
        return getQuickPayManager().isHospitalCoveragePayor(inputRecord);
    }

    public QuickPayManager getQuickPayManager() {
        return m_quickPayManager;
    }

    public void setQuickPayManager(QuickPayManager quickPayManager) {
        m_quickPayManager = quickPayManager;
    }

    private Record m_inputRecord;
    private QuickPayManager m_quickPayManager;
}
